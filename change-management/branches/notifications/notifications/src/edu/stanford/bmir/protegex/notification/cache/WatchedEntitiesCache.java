package edu.stanford.bmir.protegex.notification.cache;

import edu.stanford.bmir.protegex.chao.ontologycomp.api.OntologyComponentFactory;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.User;
import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public class WatchedEntitiesCache extends FrameAdapter  {
    private static Map<Project, WatchedEntitiesCache> projectsToCaches= new HashMap<Project, WatchedEntitiesCache>();
    private Map<User, Set<Ontology_Component>> usersToBranches = new HashMap<User, Set<Ontology_Component>>();
    private Map<User, Set<Ontology_Component>> usersToEntities = new HashMap<User, Set<Ontology_Component>>();
    private Map<Ontology_Component, Set<User>> branchesToUsers = new HashMap<Ontology_Component, Set<User>>();
    private Map<Ontology_Component, Set<User>> entitiesToUsers = new HashMap<Ontology_Component, Set<User>>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    WatchedEntitiesCache(OntologyComponentFactory factory) {

        final Collection<User> allUserObjects = factory.getAllUserObjects();
        for (User user : allUserObjects) {
            if (user.getWatchedEntity() != null && !user.getWatchedEntity().isEmpty()) {
                final Collection<Ontology_Component> entities = user.getWatchedEntity();
                for (Ontology_Component entity : entities) {
                    addToMap(entitiesToUsers, entity, user);
                    addToMap(usersToEntities, user, entity);
                }
            }
            if (user.getWatchedBranch() != null && !user.getWatchedBranch().isEmpty()) {
                final Collection<Ontology_Component> branches = user.getWatchedBranch();
                for (Ontology_Component branch : branches) {
                    addToMap(branchesToUsers, branch, user);
                    addToMap(usersToBranches, user, branch);
                }
            }
        }
    }

    public static void initialize(final Project project, final OntologyComponentFactory factory){
        projectsToCaches.put(project, new WatchedEntitiesCache(factory));
        project.getKnowledgeBase().addFrameListener(WatchedEntitiesCache.getCache(project));
    }

    public static WatchedEntitiesCache getCache(Project project){
        return projectsToCaches.get(project);
    }

    /**
     * Call returns a string, because the calling class (the NotificationSchedulerServlet) has only class names.
     *
     * To have it reconstitute Ontology_Components would further reduce performance for the caller.
     * @return
     */
    public Map<String, List<User>> getWatchedBranches() {
        try {
            lock.readLock().lock();
            final HashMap<String, List<User>> collector = new HashMap<String, List<User>>();
            for (Map.Entry<Ontology_Component, Set<User>> entry : branchesToUsers.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()){
                    collector.put(entry.getKey().getCurrentName(), new ArrayList<User>(entry.getValue()));
                }
            }
            return collector;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Set<User> getEntityWatches(Ontology_Component component) {
        try {
            lock.readLock().lock();
            if (entitiesToUsers.get(component) == null){
                return new HashSet<User>();
            }
            return new HashSet<User>(entitiesToUsers.get(component));
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addEntityWatch(Ontology_Component ontology_component, User user) {
        try {
            lock.writeLock().lock();
            addToMap(entitiesToUsers, ontology_component, user);
            addToMap(usersToEntities, user, ontology_component);
        } finally {
            lock.writeLock().unlock();
        }
    }


    public void addBranchWatch(Ontology_Component branchToWatch, User user) {
        try {
            lock.writeLock().lock();
            addToMap(branchesToUsers, branchToWatch, user);
            addToMap(usersToBranches, user, branchToWatch);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeEntityWatch( Ontology_Component entity, User user) {
        try {
            lock.writeLock().lock();
            removeFromMap(entitiesToUsers, entity, user);
            removeFromMap(usersToEntities, user, entity);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeBranchWatch(Ontology_Component ontology_component, User user) {
        try {
            lock.writeLock().lock();
            removeFromMap(branchesToUsers, ontology_component, user);
            removeFromMap(usersToBranches, user, ontology_component);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public <M extends Map<K, Set<V>>, K, V> void addToMap(M map, K key, V value) {
        Set<V> set = map.get(key);
        if (set == null) {
            set = new HashSet<V>();
            map.put(key, set);
        }
        set.add(value);
    }

    public <M extends Map<K, Set<V>>, K, V> boolean removeFromMap(M map, K key, V value) {
        Set<V> set = map.get(key);
        return set != null && set.remove(value);
    }

    @Override
    public void ownSlotValueChanged(FrameEvent event) {
        OntologyComponentFactory factory = new OntologyComponentFactory(event.getFrame().getKnowledgeBase());
        final Slot watchedBranchSlot = factory.getWatchedBranchSlot();
        final Slot watchedEntitySlot = factory.getWatchedEntitySlot();
        if (watchedBranchSlot.equals(event.getSlot())) {
            final Instance userInstance = (Instance) event.getFrame();
            final User user = factory.getUser(userInstance.getName());
            final Collection<Ontology_Component> watchedBranches = user.getWatchedBranch();
            if (event.getOldValues().size() > watchedBranches.size()) {
                final Collection<Ontology_Component> branches = usersToBranches.get(user);
                if (branches != null) {
                    for (Ontology_Component branch : branches) {
                        removeBranchWatch(branch, user);
                    }
                }
            }
            for (Ontology_Component watchedBranch : watchedBranches) {
                addBranchWatch(watchedBranch, user);
            }
        }
        if (watchedEntitySlot.equals(event.getSlot())){
            final Instance userInstance = (Instance) event.getFrame();
            final User user = factory.getUser(userInstance.getName());
            final Collection<Ontology_Component> watchedEntities = user.getWatchedEntity();
            if (event.getOldValues().size() > watchedEntities.size()) {
                final Collection<Ontology_Component> entities = usersToEntities.get(user);
                if (entities != null) {
                    for (Ontology_Component branch : entities) {
                        removeEntityWatch(branch, user);
                    }
                }
            }
            for (Ontology_Component watchedEntity : watchedEntities) {
                addEntityWatch(watchedEntity, user);
            }
        }
    }

}

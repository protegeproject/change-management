package edu.stanford.bmir.protegex.notification;


import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.server.metaproject.MetaProject;
import edu.stanford.smi.protege.server.metaproject.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public abstract class BranchWatchAwareDelegate implements NotificationDelegate {


    /**
     * Add the change to our collector if it occurs on a branch that is being watched by any user.
     * <p/>
     * TT: Code below might cause performance issues with the main ontology, especially with the coarse grain lock.
     * With the readers-writers branch, should be fine.
     *
     * @param collector
     * @param watchedBranchNodeToUserMap
     * @param frame
     * @param dataWithProject
     * @param metaProject
     */
    protected void addIfIsBranchWatch(Map<User, Set<ChangeData>> collector, Map<String, List<edu.stanford.bmir.protegex.chao.ontologycomp.api.User>> watchedBranchNodeToUserMap, Frame frame,
                                      ChangeData dataWithProject, MetaProject metaProject) {
        if (frame != null && frame instanceof Cls) {

            Collection superclasses = ((Cls) frame).getSuperclasses();
            if (superclasses == null) {
                superclasses = new ArrayList();
            }
            superclasses = new ArrayList(superclasses);
            superclasses.add(frame);

            for (Object object : superclasses) {
                final Cls superclass = (Cls) object;
                final String superclassName = superclass.getName();
                if (watchedBranchNodeToUserMap.containsKey(superclassName)) {
                    final List<edu.stanford.bmir.protegex.chao.ontologycomp.api.User> userNames = watchedBranchNodeToUserMap.get(superclassName);
                    for (edu.stanford.bmir.protegex.chao.ontologycomp.api.User user : userNames) {
                        final User metaProjectUser = metaProject.getUser(user.getName());
                        if (metaProjectUser != null) {
                            Set<ChangeData> changeData = collector.get(metaProjectUser);
                            if (changeData == null) {
                                changeData = new HashSet<ChangeData>();
                                collector.put(metaProjectUser, changeData);
                            }
                            changeData.add(dataWithProject);
                        }
                    }
                }
            }
        }
    }
}

package edu.stanford.smi.protegex.server_changes.prompt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.model.Model;
import edu.stanford.smi.protegex.server_changes.time.ChangingFrame;
import edu.stanford.smi.protegex.server_changes.time.ChangingFrameManager;

public class AuthorManagement {
    private ChangingFrameManager frameManager;
    private KnowledgeBase kb;
    private KnowledgeBase changeskb;
    
    private Map<String, Set<String>> userConflictsMap = new HashMap<String, Set<String>>();
    private Map<String, Set<ChangingFrame>> conflictingFrames = new HashMap<String, Set<ChangingFrame>>();
    private Map<String, Set<ChangingFrame>> unconflictedFrames = new HashMap<String, Set<ChangingFrame>>();
    
    private AuthorManagement(KnowledgeBase kb1, KnowledgeBase kb2) {
        this.kb = kb2;
        ChangesDb changesdb = ChangesProject.getChangesDb(kb);
        changeskb = changesdb.getChangesKb();
        frameManager = changesdb.getFrameManager();
        evaluateConflicts();
    }
    
    public static AuthorManagement getAuthorManagement(KnowledgeBase kb1, KnowledgeBase kb2) {
        if (ChangesProject.getChangesDb(kb2) != null) {
            return new AuthorManagement(kb1, kb2);
        }
        else {
            return null;
        }
    }
    
    public ChangingFrameManager getFrameManager() {
        return frameManager;
    }
    
    private void evaluateConflicts() {
        
        Map<ChangingFrame, Set<String>> whoChangedMeMap = new HashMap<ChangingFrame, Set<String>>();
        for (Object o : ServerChangesUtil.removeRoots(Model.getChangeInsts(changeskb))) {
            Instance change = (Instance) o;
            ChangingFrame frame = frameManager.getChangingFrame(change);
            Set<String> users = whoChangedMeMap.get(frame);
            if (users == null) {
                users = new HashSet<String>();
                whoChangedMeMap.put(frame, users);
            }
            String user = Model.getAuthor(change);
            users.add(user);
        }
        for (Entry<ChangingFrame, Set<String>> entry : whoChangedMeMap.entrySet()) {
            ChangingFrame frame = entry.getKey();
            Set<String> users = entry.getValue();
            if (users.size() > 1) {
                for (String user : users) {
                    Set<ChangingFrame> frames = getConflictedFrames(user);
                    frames.add(frame);
                    
                    Set<String> conflictingUsers = getUsersInConflictWith(user);
                    conflictingUsers.addAll(users);
                }
            }
            else {
                for (String user : users) {
                    Set<ChangingFrame> frames = getUnConlictedFrames(user);
                    frames.add(frame);
                }
            }
        }
        for (Entry<String, Set<String>> entry : userConflictsMap.entrySet()) {
            String user = entry.getKey();
            Set<String> conflictingUsers = entry.getValue();
            conflictingUsers.remove(user);
        }   
    }

    public Set<String> getUsersInConflictWith(String user) {
        Set<String> conflictingUsers = userConflictsMap.get(user);
        if (conflictingUsers == null) {
            conflictingUsers = new HashSet<String>();
            userConflictsMap.put(user, conflictingUsers);
        }
        return conflictingUsers;
    }
    
    public Set<ChangingFrame> getConflictedFrames(String user) {
        Set<ChangingFrame> myConflictingFrames = conflictingFrames.get(user);
        if (myConflictingFrames == null) {
            myConflictingFrames = new HashSet<ChangingFrame>();
            conflictingFrames.put(user, myConflictingFrames);
        }
        return myConflictingFrames;
    }
    
    public Set<ChangingFrame> getUnConlictedFrames(String user) {
        Set<ChangingFrame> myUnconflictedFrames = unconflictedFrames.get(user);
        if (myUnconflictedFrames == null) {
            myUnconflictedFrames = new HashSet<ChangingFrame>();
            unconflictedFrames.put(user, myUnconflictedFrames);
        }
        return myUnconflictedFrames;
    }
    
}

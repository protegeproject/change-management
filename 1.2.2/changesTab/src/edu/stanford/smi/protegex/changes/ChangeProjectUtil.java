package edu.stanford.smi.protegex.changes;

import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;

public class ChangeProjectUtil {
	
	/**
	 * Deletes all annotation instances from the Changes&Annotations kb
	 * @param changesKb - the Changes&Annotations kb
	 */
	public static void deleteAllAnnotations(KnowledgeBase changesKb) {
		Cls annotationCls = changesKb.getCls(ChangeModel.AnnotationCls.Annotation.name());
		
		if (annotationCls == null) {
			return;
		}
		
		Collection annotations = annotationCls.getInstances();
		
		for (Iterator iterator = annotations.iterator(); iterator.hasNext();) {
			Annotation annotation = (Annotation) iterator.next();
			
			Timestamp created = (Timestamp) annotation.getCreated();
			Timestamp modified = (Timestamp) annotation.getModified();
			
			annotation.delete();
			
			if (created != null) {
				created.delete();
			}
			
			if (modified != null) {
				modified.delete();
			}
		}
	}

	/**
	 * Deletes all change instances from the Changes&Annotations kb
	 * @param changesKb - the Changes&Annotations kb
	 */
	public static void deleteAllChanges(KnowledgeBase changesKb) {
		Cls changeCls = changesKb.getCls(ChangeModel.ChangeCls.Change.name());
		
		if (changeCls == null) {
			return;
		}
		
		Collection changes = changeCls.getInstances();
		
		for (Iterator iterator = changes.iterator(); iterator.hasNext();) {
			Change change = (Change) iterator.next();
			
			Timestamp timestamp = (Timestamp) change.getTimestamp();
					
			change.delete();
						
			if (timestamp != null) {
				timestamp.delete();
			}
		}
	}

	
	
}

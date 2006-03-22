/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License");  you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is Protege-2000.
 *
 * The Initial Developer of the Original Code is Stanford University. Portions
 * created by Stanford University are Copyright (C) 2005.  All Rights Reserved.
 *
 * Protege was developed by Stanford Medical Informatics
 * (http://www.smi.stanford.edu) at the Stanford University School of Medicine
 * with support from the National Library of Medicine, the National Science
 * Foundation, and the Defense Advanced Research Projects Agency.  Current
 * information about Protege can be obtained at http://protege.stanford.edu.
 *
 */

package edu.stanford.smi.protegex.changes.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColoredTableCellRenderer extends DefaultTableCellRenderer {

	Color altColor = new Color(220, 220, 220, 100);
	public Component getTableCellRendererComponent 
		(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)  {
		
		if (value instanceof Object[]) {
			Object[] wrapper = (Object[]) value;
			Integer color = (Integer) wrapper[0];
			Object context = (String) wrapper[1];
			
			if (color.intValue() == 1) {
				setBackground(altColor);
			} else {
				setBackground(null);
			}
			
			super.getTableCellRendererComponent(table, context, isSelected, hasFocus, row, column);
		}
		return this;
	}

}

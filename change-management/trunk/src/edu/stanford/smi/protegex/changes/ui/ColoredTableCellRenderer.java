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

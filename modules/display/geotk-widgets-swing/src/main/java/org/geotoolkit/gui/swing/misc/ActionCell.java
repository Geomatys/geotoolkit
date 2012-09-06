/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gui.swing.misc;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

/**
 * Convenient class to handle actions in table cells.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class ActionCell {
    
    private ActionCell(){}
    
    public static class Renderer extends DefaultTableCellRenderer{

        private final Icon icon;
        
        public Renderer(final Icon icon) {
            this.icon = icon;
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setText("");
            lbl.setIcon(getIcon(value));
            lbl.setIconTextGap(0);
            lbl.setHorizontalTextPosition(SwingConstants.CENTER);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            
            final Color bg = getBackgroundColor(value);
            if(!isSelected && bg != null){
                lbl.setBackground(bg);
            }
            
            return lbl;
        }
        
        public Icon getIcon(Object value){
            return icon;
        }
        
        public Color getBackgroundColor(Object value){
            return null;
        }
        
    }
    
    public static abstract class Editor extends AbstractCellEditor implements TableCellEditor, ActionListener{

        private Icon icon;
        private final JButton button;
        private Object value;
        
        public Editor(final Icon icon) {
            this.icon = icon;
            button = new JButton(icon);
            button.addActionListener(this);
            button.setText("");         
            button.setIconTextGap(0);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setHorizontalAlignment(SwingConstants.CENTER);  
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.value = value;
            button.setIcon(getIcon(value));
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            return value;
        }

        @Override
        public final void actionPerformed(final ActionEvent e) {
            fireEditingStopped();
            //we don't know how long can be the action, we better exit the EDT thread.
            new Thread(){
                @Override
                public void run() {
                    actionPerformed(e, value);
                }
            }.start();
            
        }

        public Icon getIcon(Object value){
            return icon;
        }
        
        public abstract void actionPerformed(ActionEvent e, Object value);
        
    }
    
}

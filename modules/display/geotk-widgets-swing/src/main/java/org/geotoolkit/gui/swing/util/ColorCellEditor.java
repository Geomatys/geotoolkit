/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Johann Sorel
 *    (C) 2011 - 2014 Geomatys
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
package org.geotoolkit.gui.swing.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import static org.geotoolkit.gui.swing.util.ColorCellRenderer.paintComp;

/**
 * Cell Renderer for Color objects.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ColorCellEditor extends AbstractCellEditor implements TableCellEditor,ActionListener {

    private Color[] colors = null;
    
    private final JButton button = new JButton(){

        @Override
        protected void paintComponent(Graphics g) {
            final Graphics2D g2d = (Graphics2D) g;
            paintComp(g2d, ColorCellEditor.this.button, colors);
        }
        
    };
    private Object value;

    public ColorCellEditor() {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(this);
        button.setOpaque(false);
    }

    @Override
    public Object getCellEditorValue() {
        return value;
    }

    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
        this.value = value;
        if(value instanceof Color){
            colors = new Color[]{(Color)value};
        }else if(value instanceof Color[]){
            colors = ((Color[])value);
            this.value = colors[1];
        }
        return button;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        //display a colorpicker dialog
        Color c = JAlphaColorChooser.showDialog(null, "", (Color)value);
        if(c != null){
            value = c;
        }
        button.setBackground((Color)value);
        button.setForeground((Color)value);
        if(colors.length==1){
            colors[0] = (Color)value;
        }else{
            colors[1] = (Color)value;
        }
        button.repaint();
        fireEditingStopped();
    }
}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Johann Sorel
 *    (C) 2011 Geomatys
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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Cell Editor for Color objects.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ColorCellRenderer extends JComponent implements TableCellRenderer {

    private static final int SQR_SIZE = 6;
    
    public ColorCellRenderer() {
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;
        paintComp(g2d, this, getForeground());
        
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        if(value instanceof Color){
            setForeground((Color)value);
        }
        return this;
    }
    
    public static void paintComp(Graphics2D g2d, JComponent comp, Color color){
        final Dimension dim = comp.getSize();
        final int SQR_SIZE = dim.height/2;
        
        final Color LIGHT_GRAY = new Color(0.8f, 0.8f, 0.8f);
        
        final Shape oldClip = g2d.getClip();
        g2d.setClip(0,0,dim.width,dim.height);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, dim.width, dim.height);
        //draw grey squares to represent transparency
        boolean swap = false;
        g2d.setColor(LIGHT_GRAY);
        for(int y=0;y<dim.height;y+=SQR_SIZE){
            for(int x=swap?0:SQR_SIZE; x<dim.width; x+=SQR_SIZE*2){
                g2d.fillRect(x, y,SQR_SIZE, SQR_SIZE);
            }            
            swap = !swap;
        }
        
        //paint color above
        g2d.setColor(color);
        g2d.fillRect(0, 0, dim.width, dim.height);
        
        g2d.setClip(oldClip);
    }
    
    
}

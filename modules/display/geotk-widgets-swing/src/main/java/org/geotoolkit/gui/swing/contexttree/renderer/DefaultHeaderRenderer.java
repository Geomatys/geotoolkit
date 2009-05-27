/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.contexttree.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Styled header Renderer for JContextTree
 * 
 * @author Johann Sorel
 */
public class DefaultHeaderRenderer implements TableCellRenderer{

    
    private DefaultColumnHeader header = null;
    
    
    /**
     * create en empty header, only a styled background
     */
    public DefaultHeaderRenderer(){
        this(null,null,null);
    }
    
    /**
     * create a header with the specified image
     * 
     * @param img
     */
    public DefaultHeaderRenderer(ImageIcon img){
        this(img,null,null);
    }
    
    /**
     * create a header with the specified text
     * 
     * @param str
     */
    public DefaultHeaderRenderer(String str){
        this(null,str,null);
    }
    
    /**
     * create a header with the specified text,image and tooltip
     * 
     * @param img null if no image
     * @param str null if no string
     * @param tooltip null if no tooltip
     */
    public DefaultHeaderRenderer(ImageIcon img, String str, String tooltip){
        
        if(img == null && str == null){
            header = new DefaultColumnHeader();
        }
        else{
            JLabel lbl = new JLabel(img);
            lbl.setText(str);            
            header = new DefaultColumnHeader(lbl);
        }
        
        if(tooltip != null){
            header.setToolTipText(tooltip);
        }
                
    }
    
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      return header;      
    }
    
}


class DefaultColumnHeader extends JPanel{
    
    
    DefaultColumnHeader() { 
        this(null);
    }
    
    
    /** 
     * Creates a new instance of JXMapContextRowHeader
     * @param name 
     * @param c 
     */
    public DefaultColumnHeader(Component comp) {        
        super( new GridLayout(1,1));       
        setPreferredSize( new Dimension(20,20));
        setOpaque(true);
        
        if(comp != null){
            add(comp);
        }        
    }
    
        
    @Override
    protected void paintComponent(Graphics g) {
        
        g.setColor(Color.WHITE);
        g.drawRect(0,0,getWidth(),getHeight());
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(new GradientPaint(0,0,Color.WHITE,getWidth()-1,getHeight()-1,Color.LIGHT_GRAY));
        g2.fillRect(0, 0, getWidth()-1,getHeight()-1);

        paintChildren(g);
        
        g.setColor(Color.GRAY);
        g.drawRoundRect(0,0,getWidth()-1,getHeight()-1,5,5);
    }
        
}

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
package org.geotoolkit.gui.swing.go2.decoration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.resource.IconBundle;


/**
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class WaitingStatic extends JPanel{

    private static final ImageIcon ICO_NOVER = IconBundle.getInstance().getIcon("32_play");
    private static final ImageIcon ICO_OVER = IconBundle.getInstance().getIcon("32_stop");
    
    private final JButton stopRendering;
    private Map2D map = null;


    //waiting animation
    private final BufferedImage buffer;
    
    public WaitingStatic() {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);
        Dimension fixed = new Dimension(120, 50);
        setPreferredSize(fixed);
        setMaximumSize(fixed);
        setMinimumSize(fixed);
        setSize(fixed);
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setAlignmentY(Component.CENTER_ALIGNMENT);
        
        buffer = new BufferedImage(fixed.width,fixed.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buffer.createGraphics();

        ////////////////////////////////////////////////////////////////////////
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = fixed.width-1;
        int height = fixed.height-1;

        g2.setColor(new Color(1f, 1f, 1f, 0.9f));
        g2.fillRoundRect(0, 0, width, height, 16,16);

        try{
            BufferedImage EMPTY_ICON = IconBundle.getInstance().getBuffer("EARTH_WHITE");
            Paint imgPaint = new TexturePaint(EMPTY_ICON, new Rectangle(width, height));
            g2.setPaint(imgPaint);
            g2.fillRoundRect(0, 0, width, height, 16,16);
        }catch(IOException ex){

        }

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(0, 0, width, height, 8,8);
        g2.dispose();

        ////////////////////////////////////////////////////////////////////////
               
        stopRendering = new JButton(ICO_NOVER);
        stopRendering.setRolloverIcon(ICO_OVER);
        stopRendering.setBorder(null);
        stopRendering.setBorderPainted(false);
        stopRendering.setContentAreaFilled(false);
        stopRendering.setSize(stopRendering.getPreferredSize());
        stopRendering.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(map != null && map instanceof Map2D){
                    Map2D go = (Map2D) map;
                    go.getCanvas().getMonitor().stopRendering();
                }
            }
        });
        
        add(BorderLayout.CENTER,stopRendering);
        
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
    }
            
    protected void paintWaiting(final Graphics g) {
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.drawImage(buffer, 0,0, this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintWaiting(g);
    }
    
    public void setMap(Map2D map){
        this.map = map;
    }
    
    public Map2D getMap(){
        return map;
    }
    
    
}

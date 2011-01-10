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
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.resource.IconBundle;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

/**
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class WaitingAnim extends JPanel{

//    private static final ImageIcon ICO_NOVER = IconBundle.getIcon("32_play");
    private static final ImageIcon ICO_OVER = IconBundle.getIcon("32_stop");
    
    private final JButton stopRendering;
    private JMap2D map = null;


    //waiting animation
    private final Color pulseColor = Color.GRAY;
    private final Color transparant = new Color(1f,1f,1f,0f);
    private final Animator waitController;
    private float propagation = 0.0f;
    private final List<Float> dists= new ArrayList<Float>();
    private final List<Color> colors= new ArrayList<Color>();
    private RadialGradientPaint radial = null;
    
    public WaitingAnim(final boolean animated) {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);
        Dimension fixed = new Dimension(80, 20);
        setPreferredSize(fixed);
        setSize(fixed);
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setAlignmentY(Component.CENTER_ALIGNMENT);

        waitController = new Animator(2500, Animator.INFINITE, Animator.RepeatBehavior.LOOP, new PropertySetter(this, "propagation", 1.0f));

        if(animated) waitController.start();
        
        stopRendering = new JButton("Painting...");
        stopRendering.setBorder(null);
        stopRendering.setBorderPainted(false);
        stopRendering.setContentAreaFilled(false);
        stopRendering.setSize(stopRendering.getPreferredSize());
        stopRendering.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(map != null && map instanceof JMap2D){
                    JMap2D go = (JMap2D) map;
                    go.getCanvas().getMonitor().stopRendering();
                }
            }
        });
        
        add(BorderLayout.CENTER,stopRendering);
        
    }

    @Override
    public void setVisible(final boolean aFlag) {
        if(aFlag){
            startWaitSequence();
        }else{
            stopWaitSequence();
        }
        super.setVisible(aFlag);
    }
    
    public void startWaitSequence() {
        stopRendering.setVisible(true);
        waitController.resume();
    }

    public void stopWaitSequence() {
        stopRendering.setVisible(false);
        setPropagation(0);
        waitController.pause();
    }

    public float getPropagation() {
        return propagation;
    }

    public void setPropagation(final float prop) {
        this.propagation = prop;

        final int width = getWidth()-1;
        final int height = getHeight()-1;
        final int centerX = width / 2;
        final int centerY = height / 2;

        final Point2D center = new Point2D.Float(centerX, centerY);

        float radius = width / 2 + height / 2;
        dists.clear();
        colors.clear();
        if (propagation > 0.0f) {
            dists.add(0.0f);
            colors.add(transparant);
        }
        if (propagation > 0.3f) {
            dists.add(propagation - 0.3f);
            colors.add(transparant);
        }
        dists.add(propagation);
        colors.add(pulseColor);
        if (propagation < 0.95f) {
            dists.add(propagation + 0.05f);
            colors.add(transparant);
        } else if (propagation < 1.0f) {
            dists.add(1.0f);
            colors.add(transparant);
        }

        float[] dist = new float[dists.size()];
        for (int i = 0, n = dists.size(); i < n; i++) {
            dist[i] = dists.get(i);
        }

        Color[] colorss = colors.toArray(new Color[colors.size()]);
        radial = new RadialGradientPaint(center, radius, dist, colorss);
        
        repaint(0,0,getWidth(),getHeight());
    }
        
    protected void paintWaiting(final Graphics g) {
        final Graphics2D g2 = (Graphics2D) g.create();

        ////////////////////////////////////////////////////////////////////////
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g2.setColor(new Color(1f, 1f, 1f, 0.9f));
        g2.fillRoundRect(0, 0, width, height, 16,16);

//        try{
            BufferedImage EMPTY_ICON = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB); //getInstance().getBuffer("EARTH_WHITE");
            Paint imgPaint = new TexturePaint(EMPTY_ICON, new Rectangle(width, height));
            g2.setPaint(imgPaint);
            g2.fillRoundRect(0, 0, width, height, 16,16);
//        }catch(IOException ex){
//
//        }

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(0, 0, width-1, height-1, 8,8);

        if(radial != null){
            g2.setPaint(radial);
            g2.fillRoundRect(0, 0,width-1, height-1, 16,16);
        }

    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        paintWaiting(g);
    }
    
    public void setMap(final JMap2D map){
        this.map = map;
    }
    
    public JMap2D getMap(){
        return map;
    }
    
    
}

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
package org.geotoolkit.gui.swing.map.map2d.decoration;

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

import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.resource.IconBundle;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

/**
 * @author Johann Sorel (Puzzle-GIS)
 */
public class WaitingAnim extends JPanel{

    private static final ImageIcon ICO_NOVER = IconBundle.getInstance().getIcon("32_play");
    private static final ImageIcon ICO_OVER = IconBundle.getInstance().getIcon("32_stop");
    
    private final JButton stopRendering;
    private Map2D map = null;


    //waiting animation
    private final BufferedImage buffer;
    private final Color pulseColor = Color.GRAY;
    private final Color transparant = new Color(1f,1f,1f,0f);
    private final Animator waitController;
    private float propagation = 0.0f;
    private final List<Float> dists= new ArrayList<Float>();
    private final List<Color> colors= new ArrayList<Color>();
    private RadialGradientPaint radial = null;
    
    public WaitingAnim(boolean animated) {
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

        waitController = new Animator(2500, Animator.INFINITE, Animator.RepeatBehavior.LOOP, new PropertySetter(this, "propagation", 1.0f));

        if(animated) waitController.start();

        
        buffer = new BufferedImage(fixed.width,fixed.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buffer.createGraphics();

        ////////////////////////////////////////////////////////////////////////
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = fixed.width-1;
        int height = fixed.height-1;

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
        if(aFlag){
            startWaitSequence();
        }else{
            stopWaitSequence();
        }
        super.setVisible(aFlag);
    }
    
    public void startWaitSequence() {
        waitController.resume();
    }

    public void stopWaitSequence() {
        waitController.pause();
    }

    public float getPropagation() {
        return propagation;
    }

    public void setPropagation(float prop) {
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
        g2.drawImage(buffer, 0,0, this);

        if(radial != null){
            g2.setPaint(radial);
            g2.fillRoundRect(0, 0,getWidth()-1, getHeight()-1, 16,16);
        }

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

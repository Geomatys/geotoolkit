/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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

package org.geotoolkit.gui.swing.style;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;
import org.geotoolkit.renderer.style.WellKnownMarkFactory;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JOpacitySlider extends JComponent implements MouseInputListener{

    private static final Shape TRIANGLE;
    private static final float VERTICAL_MARGIN;
    private static final float HORIZONTAL_MARGIN;

    static{
        AffineTransform trs = new AffineTransform();
        trs.translate(0d, -0.5d);
        Shape topCentred = trs.createTransformedShape(WellKnownMarkFactory.TRIANGLE);
        trs.setToIdentity();
        trs.scale(10, 10);
        trs.rotate(Math.PI);
        TRIANGLE = trs.createTransformedShape(topCentred);
        VERTICAL_MARGIN = (float) TRIANGLE.getBounds().getHeight();
        HORIZONTAL_MARGIN = (float) TRIANGLE.getBounds().getWidth();
    }

    private final List<ActionListener> listeners = new ArrayList<ActionListener>();
    private double opacity = 1d;

    public JOpacitySlider(){
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        
        final Graphics2D g2 = (Graphics2D) g;
        final Dimension dim = getSize();

        final Object before = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final LinearGradientPaint gradient = new LinearGradientPaint(0, 0, dim.width, 0,
//                new float[]{0f,0.5f,1f},new Color[]{
//                    new Color(1f,0f,0f,0.1f),
//                    new Color(0f,1f,0f,0.5f),
//                    new Color(0f,0f,1f,1f)});
                new float[]{0f,1f},new Color[]{
                    new Color(1f,1f,1f,0.1f),
                    new Color(0.3f,0.3f,0.3f,1f)});
        g2.setPaint(gradient);

        final GeneralPath path = new GeneralPath();
        path.moveTo(HORIZONTAL_MARGIN/2d, dim.getHeight()-2);
        path.lineTo(dim.getWidth()-HORIZONTAL_MARGIN/2d, VERTICAL_MARGIN);
        path.lineTo(dim.getWidth()-HORIZONTAL_MARGIN/2d, dim.getHeight()-2);
        path.lineTo(HORIZONTAL_MARGIN/2d, dim.getHeight()-2);

        g2.fill(path);

        g2.setColor(Color.DARK_GRAY);
        g2.translate(opacity * (dim.width-HORIZONTAL_MARGIN) + HORIZONTAL_MARGIN/2d ,
                     (1-opacity) * (dim.height-2 -VERTICAL_MARGIN) );
        g2.fill(TRIANGLE);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, before);
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(final double opacity) {
        this.opacity = opacity;
        repaint();
    }

    public void addActionListener(final ActionListener listener){
        listeners.add(listener);
    }

    public void removeActionListener(final ActionListener listener){
        listeners.remove(listener);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        double x = e.getX();
        x -= HORIZONTAL_MARGIN/2d;
        double ratio = x / (getWidth()-HORIZONTAL_MARGIN) ;
        if(ratio < 0) ratio = 0;
        if(ratio > 1) ratio = 1;
        setOpacity(ratio);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        mouseReleased(e);
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        mouseDragged(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        mouseDragged(e);
        
        final ActionEvent event = new ActionEvent(this, -1, "opacity");
        for(ActionListener listener : listeners){
            listener.actionPerformed(event);
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

}

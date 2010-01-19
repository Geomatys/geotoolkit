/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.gui.swing.navigator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JNavigator<T extends Comparable> extends JPanel {

    private final NavigatorModel<T> model;
    private NavigatorRenderer<T> renderer;
    private final JComponent graduation = new JComponent(){};

    private int orientation = SwingConstants.SOUTH;

    private final PropertyChangeListener listener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent pce) {

            if(model.getOrientation() != orientation){
                orientation = model.getOrientation();
                //change the order
                removeAll();
                if(orientation == SwingConstants.NORTH){
                    add(BorderLayout.NORTH,graduation);
                }else if(orientation == SwingConstants.SOUTH){
                    add(BorderLayout.SOUTH,graduation);
                }else if(orientation == SwingConstants.EAST){
                    add(BorderLayout.EAST,graduation);
                }else if(orientation == SwingConstants.WEST){
                    add(BorderLayout.WEST,graduation);
                }else{
                    throw new IllegalArgumentException("Orientation doesnt have a valid value :" + orientation);
                }
            }
            revalidate();
            repaint();
        }
    };

    public JNavigator(NavigatorModel<T> model) {
        super(new BorderLayout(0,0));
        if(model == null){
            throw new NullPointerException("Model can not be null.");
        }

        this.model = model;
        model.addPropertyChangeListener(listener);
        this.graduation.setOpaque(false);
        add(BorderLayout.SOUTH,graduation);
    }

    public NavigatorModel<T> getModel() {
        return model;
    }

    public NavigatorRenderer<T> getModelRenderer() {
        return renderer;
    }

    public void setModelRenderer(NavigatorRenderer<T> renderer) {
        this.renderer = renderer;

        if(this.renderer != null){
            graduation.setPreferredSize(new Dimension(this.renderer.getGraduationHeight(), this.renderer.getGraduationHeight()));
            revalidate();
            repaint();
        }
    }

    public void setOrientation(int orientation) {
        this.model.setOrientation(orientation);
    }

    public int getOrientation() {
        return this.model.getOrientation();
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        final Graphics2D g = (Graphics2D) grphcs;
        if(renderer != null){
            renderer.render(model, g, new Rectangle(getWidth(), getHeight()));
        }
    }

}

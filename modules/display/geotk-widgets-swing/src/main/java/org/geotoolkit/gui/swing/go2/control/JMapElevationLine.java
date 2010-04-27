/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C)2010, Geomatys
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

package org.geotoolkit.gui.swing.go2.control;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.measure.unit.SI;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geotoolkit.display.canvas.CanvasController2D;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.navigator.DoubleRenderer;
import org.geotoolkit.gui.swing.navigator.JNavigator;
import org.geotoolkit.gui.swing.navigator.JNavigatorBand;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JMapElevationLine extends JNavigator implements PropertyChangeListener{

    private static final Color MAIN = new Color(0f,0.3f,0.6f,1f);
    private static final Color SECOND = new Color(0f,0.3f,0.6f,0.4f);
    private static final float LIMIT_WIDTH = 1.25f;

    private final SpinnerNumberModel modelHaut;
    private final SpinnerNumberModel modelBas;

    private final JPopupMenu menu;
    private final JAnimationMenu animation = new JAnimationMenu() {
        @Override
        protected void update(Map2D map, double step) {
            final Double[] range = map.getCanvas().getController().getElevationRange().clone();

            if(range[0] != null){
                range[0] = range[0] + step;
            }
            if(range[1] != null){
                range[1] = range[1] + step;
            }

            map.getCanvas().getController().setElevationRange(range[0], range[1], null);
        }
    };

    private volatile Map2D map = null;

    public JMapElevationLine(){
        setModelRenderer(new DoubleRenderer());
        setOrientation(SwingConstants.WEST);
        getModel().scale(-1, 0);

        modelHaut = new SpinnerNumberModel();
        modelHaut.setStepSize(10);
        modelHaut.setMinimum(Double.NEGATIVE_INFINITY);
        modelHaut.setMaximum(Double.POSITIVE_INFINITY);
        modelHaut.setValue(Double.POSITIVE_INFINITY);

        modelBas = new SpinnerNumberModel();
        modelBas.setStepSize(10);
        modelBas.setMinimum(Double.NEGATIVE_INFINITY);
        modelBas.setMaximum(Double.POSITIVE_INFINITY);
        modelBas.setValue(Double.NEGATIVE_INFINITY);

        final JSpinner haut = new JSpinner(modelHaut);
        final JSpinner bas = new JSpinner(modelBas);

        ChangeListener lst = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                if(map == null) return;

                Double vh = (Double) modelHaut.getValue();
                Double vb = (Double) modelBas.getValue();
                if(vh.isInfinite()) vh = null;
                if(vb.isInfinite()) vb = null;
                
                map.getCanvas().getController().setElevationRange(vb, vh, SI.METRE);

            }
        };

        modelBas.addChangeListener(lst);
        modelHaut.addChangeListener(lst);

        final JPanel minPan = new JPanel(new BorderLayout());
        minPan.add(BorderLayout.WEST, new JLabel("min"));
        minPan.add(BorderLayout.CENTER, bas);

        final JPanel maxPan = new JPanel(new BorderLayout());
        maxPan.add(BorderLayout.WEST, new JLabel("max"));
        maxPan.add(BorderLayout.CENTER, haut);

        menu = new JPopupMenu(){

            @Override
            public void setVisible(boolean b) {
                if(b){
                    final Point pt = MouseInfo.getPointerInfo().getLocation();
                    final int y = pt.y - JMapElevationLine.this.getLocationOnScreen().y;
                    popupEdit = getModel().getDimensionValueAt(y);
                }
                super.setVisible(b);
            }

        };

        menu.add(animation);

        menu.addSeparator();

        menu.add(new JMenuItem(
                new AbstractAction(MessageBundle.getString("map_move_elevation_center")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if (getMap() != null && popupEdit != null) {
                            final CanvasController2D controller = getMap().getCanvas().getController();
                            final Double[] range = controller.getElevationRange();
                            if (range == null || range[0] == null || range[1] == null) {
                                controller.setElevationRange(popupEdit, popupEdit,null);
                            } else {
                                double middle = (range[0] + range[1]) / 2l;
                                double step = popupEdit - middle;
                                double start = range[0] + step;
                                double end = range[1] + step;
                                getMap().getCanvas().getController().setElevationRange(start, end, null);
                            }
                            JMapElevationLine.this.repaint();
                        }
                    }
                }){

            @Override
            public boolean isEnabled() {
                return getMap() != null;
            }
        });
        menu.add(new JMenuItem(
                new AbstractAction(MessageBundle.getString("map_move_elevation_maximum")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(getMap() != null && popupEdit != null){
                            final CanvasController2D controller = getMap().getCanvas().getController();
                            final Double[] range = controller.getElevationRange();
                            if(range == null){
                                controller.setElevationRange(popupEdit, popupEdit, null);
                            }else{
                                controller.setElevationRange(range[0],popupEdit, null);
                            }
                            JMapElevationLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final CanvasController2D controller = getMap().getCanvas().getController();
                    final Double[] range = controller.getElevationRange();
                    return range == null || range[0] == null || (range[0] != null && range[0] < popupEdit);
                }
                return false;
            }
        });
        menu.add(new JMenuItem(
                new AbstractAction(MessageBundle.getString("map_move_elevation_minimum")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(getMap() != null && popupEdit != null){
                            final CanvasController2D controller = getMap().getCanvas().getController();
                            final Double[] range = controller.getElevationRange();
                            if(range == null){
                                controller.setElevationRange(popupEdit, popupEdit, null);
                            }else{
                                controller.setElevationRange(popupEdit, range[1], null);
                            }
                            JMapElevationLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final CanvasController2D controller = getMap().getCanvas().getController();
                    final Double[] range = controller.getElevationRange();
                    return range == null || range[1] == null || (range[1] != null && range[1] > popupEdit);
                }
                return false;
            }
        });
        
        
        menu.addSeparator();
        
        menu.add(new JMenuItem(
                new AbstractAction(MessageBundle.getString("map_remove_elevation")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(getMap() != null && popupEdit != null){
                            final CanvasController2D controller = getMap().getCanvas().getController();
                            controller.setElevationRange(null, null, null);
                            JMapElevationLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final CanvasController2D controller = getMap().getCanvas().getController();
                    final Double[] range = controller.getElevationRange();
                    return range != null;
                }
                return false;
            }
        });
        
        menu.add(new JMenuItem(
                new AbstractAction(MessageBundle.getString("map_remove_elevation_maximum")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(getMap() != null && popupEdit != null){
                            final CanvasController2D controller = getMap().getCanvas().getController();
                            final Double[] range = controller.getElevationRange();
                            if(range != null){
                                range[1] = null;
                                controller.setElevationRange(range[0], range[1], null);
                            }
                            JMapElevationLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final CanvasController2D controller = getMap().getCanvas().getController();
                    final Double[] range = controller.getElevationRange();
                    return range != null && range[0] != null;
                }
                return false;
            }
        });
        menu.add(new JMenuItem(
                new AbstractAction(MessageBundle.getString("map_remove_elevation_minimum")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(getMap() != null && popupEdit != null){
                            final CanvasController2D controller = getMap().getCanvas().getController();
                            final Double[] range = controller.getElevationRange();
                            if(range != null){
                                range[0] = null;
                                controller.setElevationRange(range[0], range[1], null);
                            }
                            JMapElevationLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final CanvasController2D controller = getMap().getCanvas().getController();
                    final Double[] range = controller.getElevationRange();
                    return range != null && range[0] != null;
                }
                return false;
            }
        });
        
        menu.addSeparator();
        menu.add(minPan);
        menu.add(maxPan);


        setComponentPopupMenu(menu);
        AreaBand band = new AreaBand();
        band.setComponentPopupMenu(menu);
        band.addMouseListener(this);
        band.addMouseMotionListener(this);
        band.addMouseWheelListener(this);
        band.addKeyListener(this);

        addBand(band);


    }
    
    public Map2D getMap() {
        return map;
    }

    public void setMap(Map2D map) {
        if(this.map != null){
            this.map.getCanvas().removePropertyChangeListener(this);
        }

        this.map = map;
        animation.setMap(map);
        
        if(map != null){
            this.map.getCanvas().addPropertyChangeListener(this);
        }
        repaint();
    }

    //handle mouse event for dragging range ends -------------------------------

    // 0 for left limit
    // 1 for middle
    // 2 for right limit
    private int selected = -1;
    private Double edit = null;
    private volatile Double popupEdit = null;

    @Override
    public void mousePressed(MouseEvent e) {

        if(map != null){
            final Double[] range = map.getCanvas().getController().getElevationRange();

            if(range != null){
                final int y = e.getY();

                if(range[0] != null){
                    int pos = (int)getModel().getGraphicValueAt(range[0]);
                    if( Math.abs(y-pos) < LIMIT_WIDTH*2 ){
                        selected = 0;
                    }
                }
                if(range[1] != null){
                    int pos = (int)getModel().getGraphicValueAt(range[1]);
                    if( Math.abs(y-pos) < LIMIT_WIDTH*2 ){
                        selected = 2;
                    }
                }
                if(range[0] != null && range[1] != null){
                    int pos = (int) ((
                              getModel().getGraphicValueAt(range[0])
                            + getModel().getGraphicValueAt(range[1])
                            ) / 2);
                    if( Math.abs(y-pos) < LIMIT_WIDTH*4 ){
                        selected = 1;
                    }
                }
            }
        }

        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if(selected >= 0 && edit != null){

            final Double[] range = map.getCanvas().getController().getElevationRange();

            if(selected == 0){
                map.getCanvas().getController().setElevationRange(edit, range[1], null);
            }else if(selected == 2){
                map.getCanvas().getController().setElevationRange(range[0], edit, null);
            }else if(selected == 1){
                double middle = (range[0] + range[1]) / 2d;
                double step = edit - middle;
                double start = range[0] + step;
                double end = range[1] + step;
                map.getCanvas().getController().setElevationRange(start, end, null);
            }

            repaint();
        }
        selected = -1;
        edit = null;

        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if(selected >= 0){
            //drag one limit
            edit = getModel().getDimensionValueAt(e.getY());

            //ensure we do not go over the other limit
            final Double[] range = map.getCanvas().getController().getElevationRange();
            if(selected == 0 && range[1] != null){
                if(edit > range[1]) edit = range[1];
            }else if(selected == 2 && range[0] != null){
                if(edit < range[0]) edit = range[0];
            }

            repaint();
        }else{
            super.mouseDragged(e);
        }

    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(CanvasController2D.ELEVATION_PROPERTY)){
            Double[] range = map.getCanvas().getController().getElevationRange();

            if(range == null){
                modelBas.setValue(Double.NEGATIVE_INFINITY);
                modelHaut.setValue(Double.POSITIVE_INFINITY);
            }else{
                if(range[0] != null){
                    modelBas.setValue(range[0]);
                }else{
                    modelBas.setValue(Double.NEGATIVE_INFINITY);
                }

                if(range[1] != null){
                    modelHaut.setValue(range[1]);
                }else{
                    modelHaut.setValue(Double.POSITIVE_INFINITY);
                }
                
            }

        }
    }

    private class AreaBand extends JNavigatorBand{

        public AreaBand(){
            setPreferredSize(new Dimension(50, 50));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if(map == null) return;

            final Double[] range = map.getCanvas().getController().getElevationRange();

            if(range == null) return;

            if(range[0] == null && range[1] == null) return;

            double start = getHeight() +5;
            double end = -5;
            double center = -5;

            if(range[0] != null) start = getModel().getGraphicValueAt(range[0]);
            if(range[1] != null) end = getModel().getGraphicValueAt(range[1]);


            //apply change if there are some
            if(edit != null){
                if(selected == 0){
                    start = getModel().getGraphicValueAt(edit);
                }else if(selected == 2){
                    end = getModel().getGraphicValueAt(edit);
                }else if(selected == 1){
                    double middleDate = (range[0] + range[1]) / 2l;
                    double step = edit - middleDate;
                    start = getModel().getGraphicValueAt(range[0] + step);
                    end = getModel().getGraphicValueAt(range[1] + step);
                }
            }

            if(start>end){
                double n = start;
                start = end;
                end = n;
            }

            if(range[0] != null && range[1] != null){
                center = (start+end)/2;
            }


            final Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(SECOND);
            g2d.fillRect(0,(int)start,getWidth(),(int)(end-start));

            g2d.setColor(MAIN);
            g2d.setStroke(new BasicStroke(LIMIT_WIDTH*2));
            g2d.drawLine(0, (int)start, getWidth(), (int)start);
            g2d.drawLine(0, (int)end,  getWidth(), (int)end);

            g2d.setStroke(new BasicStroke(LIMIT_WIDTH*4));
            g2d.drawLine(0, (int)center, getWidth(), (int)center);
        }

    }

}

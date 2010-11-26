/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C)2009, Johann Sorel
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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.geotoolkit.display.canvas.CanvasController2D;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.navigator.DateRenderer;
import org.geotoolkit.gui.swing.navigator.JNavigator;
import org.geotoolkit.gui.swing.navigator.JNavigatorBand;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.util.logging.Logging;
import org.opengis.referencing.operation.TransformException;

/**
 * Extension of a JTimeline displaying the temporal range
 * of the map envelope.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JMapTimeLine extends JNavigator implements PropertyChangeListener{

    private static final Logger LOGGER = Logging.getLogger(JMapTimeLine.class);

    private static final Color MAIN = new Color(0f,0.3f,0.6f,1f);
    private static final Color SECOND = new Color(0f,0.3f,0.6f,0.4f);
    private static final float LIMIT_WIDTH = 1.25f;

    private final JPopupMenu menu;
    private final JAnimationMenu animation = new JAnimationMenu() {
        @Override
        protected void update(Map2D map, double step) {
            final Date[] range = map.getCanvas().getController().getTemporalRange().clone();

            step =  step * animation.getRefreshInterval();

            if(range[0] != null){
                range[0] = new Date(range[0].getTime() + (long)step);
            }
            if(range[1] != null){
                range[1] = new Date(range[1].getTime() + (long)step);
            }
            try {
                map.getCanvas().getController().setTemporalRange(range[0], range[1]);
            } catch (TransformException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
    };
    private volatile Map2D map = null;


    public JMapTimeLine(){
        setModelRenderer(new DateRenderer());
        long now = System.currentTimeMillis();
        getModel().translate(-now);
        getModel().scale(0.0000001d, 0);

        menu = new JPopupMenu(){

            @Override
            public void setVisible(boolean b) {
                if(b){
                    final Point pt = MouseInfo.getPointerInfo().getLocation();
                    final int x = pt.x - JMapTimeLine.this.getLocationOnScreen().x;
                    popupEdit = new Date((long)getModel().getDimensionValueAt(x));
                }
                super.setVisible(b);
            }

        };

        menu.add(animation);

        menu.addSeparator();

        menu.add(new JMenuItem(
                new AbstractAction(MessageBundle.getString("map_move_temporal_center")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if (getMap() != null && popupEdit != null) {
                            final CanvasController2D controller = getMap().getCanvas().getController();
                            final Date[] range = controller.getTemporalRange();
                            try{
                                if (range == null || range[0] == null || range[1] == null) {
                                    controller.setTemporalRange(popupEdit, popupEdit);
                                } else {
                                    long middleDate = (range[0].getTime() + range[1].getTime()) / 2l;
                                    long step = popupEdit.getTime() - middleDate;
                                    Date start = new Date(range[0].getTime() + step);
                                    Date end = new Date(range[1].getTime() + step);
                                    controller.setTemporalRange(start, end);
                                }
                            }catch(TransformException ex){
                                LOGGER.log(Level.WARNING, null,ex);
                            }
                            JMapTimeLine.this.repaint();
                        }
                    }
                }){

            @Override
            public boolean isEnabled() {
                return getMap() != null;
            }
        });
        menu.add(new JMenuItem(
                new AbstractAction(MessageBundle.getString("map_move_temporal_left")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(getMap() != null && popupEdit != null){
                            final CanvasController2D controller = getMap().getCanvas().getController();
                            final Date[] range = controller.getTemporalRange();
                            try{
                                if(range == null){
                                    controller.setTemporalRange(popupEdit, popupEdit);
                                }else{
                                    controller.setTemporalRange(popupEdit, range[1]);
                                }
                            }catch(TransformException ex){
                                LOGGER.log(Level.WARNING, null,ex);
                            }
                            JMapTimeLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final CanvasController2D controller = getMap().getCanvas().getController();
                    final Date[] range = controller.getTemporalRange();
                    return range == null || range[1] == null || (range[1] != null && range[1].after(popupEdit));
                }
                return false;
            }
        });
        menu.add(new JMenuItem(
                new AbstractAction(MessageBundle.getString("map_move_temporal_right")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(getMap() != null && popupEdit != null){
                            final CanvasController2D controller = getMap().getCanvas().getController();
                            final Date[] range = controller.getTemporalRange();
                            try{
                                if(range == null){
                                    controller.setTemporalRange(popupEdit, popupEdit);
                                }else{
                                    controller.setTemporalRange(range[0],popupEdit);
                                }
                            }catch(TransformException ex){
                                LOGGER.log(Level.WARNING, null,ex);
                            }
                            JMapTimeLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final CanvasController2D controller = getMap().getCanvas().getController();
                    final Date[] range = controller.getTemporalRange();
                    return range == null || range[0] == null || (range[0] != null && range[0].before(popupEdit));
                }
                return false;
            }
        });
        
        menu.addSeparator();
        
        menu.add(new JMenuItem(
                new AbstractAction(MessageBundle.getString("map_remove_temporal")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(getMap() != null && popupEdit != null){
                            final CanvasController2D controller = getMap().getCanvas().getController();
                            try{
                                controller.setTemporalRange(null, null);
                            }catch(TransformException ex){
                                LOGGER.log(Level.WARNING, null,ex);
                            }
                            JMapTimeLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final CanvasController2D controller = getMap().getCanvas().getController();
                    final Date[] range = controller.getTemporalRange();
                    return range != null;
                }
                return false;
            }
        });
        menu.add(new JMenuItem(
                new AbstractAction(MessageBundle.getString("map_remove_temporal_left")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(getMap() != null && popupEdit != null){
                            final CanvasController2D controller = getMap().getCanvas().getController();
                            final Date[] range = controller.getTemporalRange();
                            if(range != null){
                                range[0] = null;
                                try{
                                    controller.setTemporalRange(range[0], range[1]);
                                }catch(TransformException ex){
                                    LOGGER.log(Level.WARNING, null,ex);
                                }
                            }
                            JMapTimeLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final CanvasController2D controller = getMap().getCanvas().getController();
                    final Date[] range = controller.getTemporalRange();
                    return range != null && range[0] != null;
                }
                return false;
            }
        });
        menu.add(new JMenuItem(
                new AbstractAction(MessageBundle.getString("map_remove_temporal_right")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(getMap() != null && popupEdit != null){
                            final CanvasController2D controller = getMap().getCanvas().getController();
                            final Date[] range = controller.getTemporalRange();
                            if(range != null){
                                range[1] = null;
                                try{
                                    controller.setTemporalRange(range[0], range[1]);
                                }catch(TransformException ex){
                                    LOGGER.log(Level.WARNING, null,ex);
                                }
                            }
                            JMapTimeLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final CanvasController2D controller = getMap().getCanvas().getController();
                    final Date[] range = controller.getTemporalRange();
                    return range != null && range[0] != null;
                }
                return false;
            }
        });

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
    private Date edit = null;
    private volatile Date popupEdit = null;

    @Override
    public void mousePressed(MouseEvent e) {

        if(map != null){
            final Date[] range = map.getCanvas().getController().getTemporalRange();

            if(range != null){
                final int x = e.getX();

                if(range[0] != null){
                    int pos = (int)getModel().getGraphicValueAt(range[0].getTime());
                    if( Math.abs(x-pos) < LIMIT_WIDTH*2 ){
                        selected = 0;
                    }
                }
                if(range[1] != null){
                    int pos = (int)getModel().getGraphicValueAt(range[1].getTime());
                    if( Math.abs(x-pos) < LIMIT_WIDTH*2 ){
                        selected = 2;
                    }
                }
                if(range[0] != null && range[1] != null){
                    int pos = (int) ((
                              getModel().getGraphicValueAt(range[0].getTime())
                            + getModel().getGraphicValueAt(range[1].getTime())
                            ) / 2);
                    if( Math.abs(x-pos) < LIMIT_WIDTH*4 ){
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

            final Date[] range = map.getCanvas().getController().getTemporalRange();

            try{
                if(selected == 0){
                    map.getCanvas().getController().setTemporalRange(edit, range[1]);
                }else if(selected == 2){
                    map.getCanvas().getController().setTemporalRange(range[0], edit);
                }else if(selected == 1){
                    long middleDate = (range[0].getTime() + range[1].getTime()) / 2l;
                    long step = edit.getTime() - middleDate;
                    Date start = new Date(range[0].getTime() + step);
                    Date end = new Date(range[1].getTime() + step);
                    map.getCanvas().getController().setTemporalRange(start, end);
                }
            }catch(TransformException ex){
                LOGGER.log(Level.WARNING, null,ex);
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
            edit = new Date((long)getModel().getDimensionValueAt(e.getX()));

            //ensure we do not go over the other limit
            final Date[] range = map.getCanvas().getController().getTemporalRange();
            if(selected == 0 && range[1] != null){
                if(edit.after(range[1])) edit = new Date(range[1].getTime());
            }else if(selected == 2 && range[0] != null){
                if(edit.before(range[0])) edit = new Date(range[0].getTime());
            }
            
            repaint();
        }else{
            super.mouseDragged(e);
        }
        
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(CanvasController2D.TEMPORAL_PROPERTY)){
            repaint();
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

            final Date[] range = map.getCanvas().getController().getTemporalRange();

            if(range == null) return;

            if(range[0] == null && range[1] == null) return;

            double start = -5;
            double end = getWidth() +5;
            double center = -5;

            if(range[0] != null) start = getModel().getGraphicValueAt(range[0].getTime());
            if(range[1] != null) end = getModel().getGraphicValueAt(range[1].getTime());


            //apply change if there are some
            if(edit != null){
                if(selected == 0){
                    start = getModel().getGraphicValueAt(edit.getTime());
                }else if(selected == 2){
                    end = getModel().getGraphicValueAt(edit.getTime());
                }else if(selected == 1){
                    long middleDate = (range[0].getTime() + range[1].getTime()) / 2l;
                    long step = edit.getTime() - middleDate;
                    start = getModel().getGraphicValueAt(range[0].getTime() + step);
                    end = getModel().getGraphicValueAt(range[1].getTime() + step);
                }
            }

            if(range[0] != null && range[1] != null){
                center = (start+end)/2;
            }


            final Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(SECOND);
            g2d.fillRect((int)start,0,(int)(end-start),getHeight());

            g2d.setColor(MAIN);
            g2d.setStroke(new BasicStroke(LIMIT_WIDTH*2));
            g2d.drawLine((int)start, 0, (int)start, getHeight());
            g2d.drawLine((int)end, 0, (int)end, getHeight());

            g2d.setStroke(new BasicStroke(LIMIT_WIDTH*4));
            g2d.drawLine((int)center, 0, (int)center, getHeight());
        }

    }

}

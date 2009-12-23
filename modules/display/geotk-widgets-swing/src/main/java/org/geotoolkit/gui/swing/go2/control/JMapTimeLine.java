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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.geotoolkit.display.canvas.CanvasController2D;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.timeline.JTimeLine;

/**
 * Extension of a JTimeline displaying the temporal range
 * of the map envelope.
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class JMapTimeLine extends JTimeLine implements PropertyChangeListener{

    private static final Color MAIN = new Color(0f,0.3f,0.6f,1f);
    private static final Color SECOND = new Color(0f,0.3f,0.6f,0.4f);
    private static final float LIMIT_WIDTH = 1.25f;

    private final JPopupMenu menu;
    private volatile Map2D map = null;


    public JMapTimeLine(){

        menu = new JPopupMenu(){

            @Override
            public void setVisible(boolean b) {
                Point pt = MouseInfo.getPointerInfo().getLocation();
                int x = pt.x - JMapTimeLine.this.getLocationOnScreen().x;
                popupEdit = getDateAt(x);
                super.setVisible(b);
            }

        };

        menu.add(new JMenuItem(
                new AbstractAction(MessageBundle.getString("map_move_temporal_center")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if (getMap() != null && popupEdit != null) {
                            final CanvasController2D controller = getMap().getCanvas().getController();
                            final Date[] range = controller.getTemporalRange();
                            if (range == null || range[0] == null || range[1] == null) {
                                controller.setTemporalRange(popupEdit, popupEdit);
                            } else {
                                long middleDate = (range[0].getTime() + range[1].getTime()) / 2l;
                                long step = popupEdit.getTime() - middleDate;
                                Date start = new Date(range[0].getTime() + step);
                                Date end = new Date(range[1].getTime() + step);
                                getMap().getCanvas().getController().setTemporalRange(start, end);
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
                            if(range == null){
                                controller.setTemporalRange(popupEdit, popupEdit);
                            }else{
                                controller.setTemporalRange(popupEdit, range[1]);
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
                            if(range == null){
                                controller.setTemporalRange(popupEdit, popupEdit);
                            }else{
                                controller.setTemporalRange(range[0],popupEdit);
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
                            controller.setTemporalRange(null, null);
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
                                controller.setTemporalRange(range[0], range[1]);
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
                                controller.setTemporalRange(range[0], range[1]);
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
    }

    public Map2D getMap() {
        return map;
    }

    public void setMap(Map2D map) {
        if(this.map != null){
            this.map.getCanvas().removePropertyChangeListener(this);
        }
        this.map = map;
        if(map != null){
            this.map.getCanvas().addPropertyChangeListener(this);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(map == null) return;

        final Date[] range = map.getCanvas().getController().getTemporalRange();

        if(range == null) return;

        if(range[0] == null && range[1] == null) return;

        int start = -5;
        int end = getWidth() +5;
        int center = -5;

        if(range[0] != null) start = getPosition(range[0]);
        if(range[1] != null) end = getPosition(range[1]);
        

        //apply change if there are some
        if(edit != null){
            if(selected == 0){
                start = getPosition(edit);
            }else if(selected == 2){
                end = getPosition(edit);
            }else if(selected == 1){
                long middleDate = (range[0].getTime() + range[1].getTime()) / 2l;
                long step = edit.getTime() - middleDate;
                start = getPosition(new Date(range[0].getTime() + step));
                end = getPosition(new Date(range[1].getTime() + step));
            }
        }
        
        if(range[0] != null && range[1] != null){
            center = (start+end)/2;
        }


        final Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(SECOND);
        g2d.fillRect(start,0,end-start,getHeight());
        
        g2d.setColor(MAIN);
        g2d.setStroke(new BasicStroke(LIMIT_WIDTH*2));
        g2d.drawLine(start, 0, start, getHeight());
        g2d.drawLine(end, 0, end, getHeight());

        g2d.setStroke(new BasicStroke(LIMIT_WIDTH*4));
        g2d.drawLine(center, 0, center, getHeight());
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
                    int pos = getPosition(range[0]);
                    if( Math.abs(x-pos) < LIMIT_WIDTH ){
                        selected = 0;
                    }
                }
                if(range[1] != null){
                    int pos = getPosition(range[1]);
                    if( Math.abs(x-pos) < LIMIT_WIDTH ){
                        selected = 2;
                    }
                }
                if(range[0] != null && range[1] != null){
                    int pos = (getPosition(range[0]) + getPosition(range[1])) /2;
                    if( Math.abs(x-pos) < LIMIT_WIDTH*2 ){
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
            edit = getDateAt(e.getX());

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



}

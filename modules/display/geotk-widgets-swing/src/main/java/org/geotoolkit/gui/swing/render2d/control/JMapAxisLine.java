/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C)2010-2011, Geomatys
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

package org.geotoolkit.gui.swing.render2d.control;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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

import org.apache.sis.measure.Range;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.navigator.DoubleRenderer;
import org.geotoolkit.gui.swing.navigator.JNavigator;
import org.geotoolkit.gui.swing.navigator.JNavigatorBand;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display.canvas.AbstractCanvas2D;
import org.geotoolkit.map.*;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.TransformException;

import static java.awt.event.KeyEvent.*;

/**
 * A {@link JNavigator} to display a scroll bar. It allows the user to browse
 * the axis of the {@link CoordinateReferenceSystem} given at built.
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 * @module pending
 */
 public class JMapAxisLine extends JNavigator implements PropertyChangeListener, ContextListener {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.gui.swing.render2d.control");

    private static final Color MAIN = new Color(0f,0.3f,0.6f,1f);
    private static final Color SECOND = new Color(0f,0.3f,0.6f,0.4f);
    private static final float LIMIT_WIDTH = 1.25f;

    private final SpinnerNumberModel modelHaut;
    private final SpinnerNumberModel modelBas;

    /**
     * The popup menu to display at right click. Contains several options to
     * manage movements, animation, etc.
     */
    private final JPopupMenu menu;

    /**
     * A Jcomponent to configure animation mecanism on the current axis.
     */
    private final JAnimationMenu animation = new JAnimationMenu() {
        @Override
        protected void update(JMap2D map, double step) {
            final Double[] range = map.getCanvas().getAxisRange(axisIndexFinder).clone();

            if(range[0] != null){
                range[0] = range[0] + step;
            }
            if(range[1] != null){
                range[1] = range[1] + step;
            }

            try{
                map.getCanvas().setAxisRange(range[0], range[1], axisIndexFinder, crs);
            } catch (TransformException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
    };

    /** Scrolling mecanism. */
    private final ChangeListener spinnerListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                if(map == null) return;

                Double vh = (Double) modelHaut.getValue();
                Double vb = (Double) modelBas.getValue();
                if(vh.isInfinite()) vh = null;
                if(vb.isInfinite()) vb = null;

                try{
                    map.getCanvas().setAxisRange(vb, vh, axisIndexFinder,crs);
                } catch (TransformException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
            }
        };

    private JLayerBandMenu layers = null;

    private volatile JMap2D map = null;

    /** The CRS containing the axis to browse. This CRS should have only one dimension. */
    private final CoordinateReferenceSystem crs;

    private final Comparator<CoordinateSystemAxis> axisIndexFinder;

    /**
     * A boolean to determine what mecanism must be used for layer activation.
     * Two mecanisms are allowed :
     *  - If true, a menu will be added on right click, on which the user will
     *    be able to choose which layer to activate on the current line.
     *  - If false, the choice mecanism will use directly the {@link MapLayer#isSelectable() }
     *    property to determine if should be activated or not. The
     *    {@link MapLayer#isSelectable() } property can be changed via
     *    {@link org.geotoolkit.gui.swing.contexttree.JContextTree} component.
     */
    private final boolean useMenu;

    public JMapAxisLine(final CoordinateReferenceSystem crs){
        this(crs, true);
    }

    public JMapAxisLine(final CoordinateReferenceSystem crs, final boolean useMenu) {
        this.crs = crs;
        this.axisIndexFinder = new AbstractCanvas2D.AxisFinder(crs.getCoordinateSystem().getAxis(0));
        this.useMenu = useMenu;
        animation.setSpeedFactor(10);
        setModelRenderer(new DoubleRenderer());
        getModel().setCRS(crs);

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

        modelBas.addChangeListener(spinnerListener);
        modelHaut.addChangeListener(spinnerListener);

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
                    pt.x -= JMapAxisLine.this.getLocationOnScreen().x;
                    pt.y -= JMapAxisLine.this.getLocationOnScreen().y;
                    final int coord = getCoord(pt);
                    popupEdit = getModel().getDimensionValueAt(coord);
                }
                super.setVisible(b);
            }

        };

        if (useMenu) {
            layers = new JLayerBandMenu(this);
            menu.add(layers);
        }

        menu.addSeparator();

        menu.add(animation);

        menu.addSeparator();

        menu.add(new JMenuItem(
                new AbstractAction(MessageBundle.getString("map_move_elevation_center")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        moveTo(popupEdit);
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
                            final AbstractCanvas2D controller = getMap().getCanvas();
                            final Double[] range = controller.getAxisRange(axisIndexFinder);
                            try{
                                if(range == null){
                                    controller.setAxisRange(popupEdit, popupEdit, axisIndexFinder, crs);
                                }else{
                                    controller.setAxisRange(range[0],popupEdit, axisIndexFinder,crs);
                                }
                            } catch (TransformException ex) {
                                LOGGER.log(Level.WARNING, null, ex);
                            }
                            JMapAxisLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final AbstractCanvas2D controller = getMap().getCanvas();
                    final Double[] range = controller.getAxisRange(axisIndexFinder);
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
                            final AbstractCanvas2D controller = getMap().getCanvas();
                            final Double[] range = controller.getAxisRange(axisIndexFinder);
                            try{
                                if(range == null){
                                    controller.setAxisRange(popupEdit, popupEdit, axisIndexFinder,crs);
                                }else{
                                    controller.setAxisRange(popupEdit, range[1], axisIndexFinder,crs);
                                }
                            } catch (TransformException ex) {
                                LOGGER.log(Level.WARNING, null, ex);
                            }
                            JMapAxisLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final AbstractCanvas2D controller = getMap().getCanvas();
                    final Double[] range = controller.getAxisRange(axisIndexFinder);
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
                            final AbstractCanvas2D controller = getMap().getCanvas();
                            try{
                                controller.setAxisRange(null, null, axisIndexFinder, crs);
                            } catch (TransformException ex) {
                                LOGGER.log(Level.WARNING, null, ex);
                            }
                            JMapAxisLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final AbstractCanvas2D controller = getMap().getCanvas();
                    final Double[] range = controller.getAxisRange(axisIndexFinder);
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
                            final AbstractCanvas2D controller = getMap().getCanvas();
                            final Double[] range = controller.getAxisRange(axisIndexFinder);
                            if(range != null){
                                range[1] = null;
                                try{
                                    controller.setAxisRange(range[0], range[1], axisIndexFinder, crs);
                                } catch (TransformException ex) {
                                    LOGGER.log(Level.WARNING, null, ex);
                                }
                            }
                            JMapAxisLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final Double[] range = getMap().getCanvas().getAxisRange(axisIndexFinder);
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
                            final Double[] range = getMap().getCanvas().getAxisRange(axisIndexFinder);
                            if(range != null){
                                range[0] = null;
                                try{
                                    getMap().getCanvas().setAxisRange(range[0], range[1], axisIndexFinder, crs);
                                } catch (TransformException ex) {
                                    LOGGER.log(Level.WARNING, null, ex);
                                }
                            }
                            JMapAxisLine.this.repaint();
                        }
                    }
            }){

            @Override
            public boolean isEnabled() {
                if(getMap() != null){
                    final Double[] range = getMap().getCanvas().getAxisRange(axisIndexFinder);
                    return range != null && range[0] != null;
                }
                return false;
            }
        });

        menu.addSeparator();
        menu.add(minPan);
        menu.add(maxPan);

        setComponentPopupMenu(menu);
    }

    public CoordinateReferenceSystem getCrs() {
        return crs;
    }

    public Comparator<CoordinateSystemAxis> getAxisIndexFinder() {
        return axisIndexFinder;
    }

    /**
     * Disable spinner the time to update their values, otherwise
     * the listener will cause the canvas to be repainted.
     */
    private synchronized void updateSpiners(final double min, final double max){
        modelBas.removeChangeListener(spinnerListener);
        modelHaut.removeChangeListener(spinnerListener);

        modelBas.setValue(min);
        modelHaut.setValue(max);

        modelBas.addChangeListener(spinnerListener);
        modelHaut.addChangeListener(spinnerListener);
    }

    public JMap2D getMap() {
        return map;
    }

    public void setMap(final JMap2D map) {
        if(this.map != null){
            this.map.getCanvas().removePropertyChangeListener(this);
        }

        this.map = map;
        animation.setMap(map);

        if(map != null){
            this.map.getCanvas().addPropertyChangeListener(this);
            if (useMenu) {
                layers.setMap(map);
            } else {
                final MapContext context = this.map.getContainer().getContext();
                if (context != null) {
                    context.addContextListener(this);
                    checkLayerBands(context, CollectionChangeEvent.ITEM_ADDED);
                }
            }
        }
        repaint();
    }

    /**
     * Get value checking orientation.
     *
     * @param me
     * @return
     */
    private int getCoord(Point me){
        final int coord;
        final int orientation = getOrientation();
        if(orientation == SwingConstants.SOUTH || orientation == SwingConstants.NORTH){
            coord = (int)me.getX();
        }else if(orientation == SwingConstants.EAST || orientation == SwingConstants.WEST){
            coord = (int)me.getY();
        }else{
            throw new IllegalArgumentException("Invalid orientation : "+orientation);
        }
        return coord;
    }

    //handle mouse event for dragging range ends -------------------------------

    // 0 for left limit
    // 1 for middle
    // 2 for right limit
    private int selected = -1;
    private Double edit = null;
    private volatile Double popupEdit = null;

    @Override
    public void mousePressed(final MouseEvent e) {

        if(map != null){
            final Double[] range = getMap().getCanvas().getAxisRange(axisIndexFinder);

            if(range != null){

                final int coord = getCoord(e.getPoint());

                if(range[0] != null){
                    int pos = (int)getModel().getGraphicValueAt(range[0]);
                    if( Math.abs(coord-pos) < LIMIT_WIDTH*2 ){
                        selected = 0;
                    }
                }
                if(range[1] != null){
                    int pos = (int)getModel().getGraphicValueAt(range[1]);
                    if( Math.abs(coord-pos) < LIMIT_WIDTH*2 ){
                        selected = 2;
                    }
                }
                if(range[0] != null && range[1] != null){
                    int pos = (int) ((
                              getModel().getGraphicValueAt(range[0])
                            + getModel().getGraphicValueAt(range[1])
                            ) / 2);
                    if( Math.abs(coord-pos) < LIMIT_WIDTH*4 ){
                        selected = 1;
                    }
                }
            }
        }

        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {

        if(selected >= 0 && edit != null){

            final Double[] range = getMap().getCanvas().getAxisRange(axisIndexFinder);

            try{
                if(selected == 0){
                    getMap().getCanvas().setAxisRange(edit, range[1], axisIndexFinder, crs);
                }else if(selected == 2){
                    getMap().getCanvas().setAxisRange(range[0], edit, axisIndexFinder, crs);
                }else if(selected == 1){
                    double middle = (range[0] + range[1]) / 2d;
                    double step = edit - middle;
                    double start = range[0] + step;
                    double end = range[1] + step;
                    getMap().getCanvas().setAxisRange(start, end, axisIndexFinder, crs);
                }
            } catch (TransformException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }

            repaint();
        }
        selected = -1;
        edit = null;

        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {

        if(selected >= 0){
            //drag one limit
            final int coord = getCoord(e.getPoint());
            edit = getModel().getDimensionValueAt(coord);

            //ensure we do not go over the other limit
            final Double[] range = getMap().getCanvas().getAxisRange(axisIndexFinder);
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

    /**
     * Change behavior of key pressed events to navigate from one element to the next.
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        final int code = e.getKeyCode();

        boolean next = false;
        boolean previous = false;
        switch(code){
            case VK_UP :
            case VK_RIGHT :next=true;break;
            case VK_LEFT :
            case VK_DOWN :previous=true;break;
        }

        Double[] current = getCurrentRange();
        if(current != null && (next || previous)){
            //find all elements
            final SortedSet<Double> steps = new TreeSet<Double>();
            final List<JNavigatorBand> bands = getBands();
            for(JNavigatorBand band : bands){
                final JLayerBand lb = (JLayerBand) band;
                final List<Range<Double>> ranges = lb.getRanges();
                final List<Double> ponctuals = lb.getPonctuals();
                if(ranges != null){
                    for(Range<Double> range :ranges){
                        steps.add(range.getMinValue());
                        steps.add(range.getMaxValue());
                    }
                }
                if(ponctuals != null){
                    steps.addAll(ponctuals);
                }
            }
            final Double[] array = steps.toArray(new Double[0]);

            boolean range;
            double middle;
            if(current[0] == null || Double.isInfinite(current[0]) ){
                range = false;
                middle = current[1];
            }else if(current[1] == null || Double.isInfinite(current[1]) ){
                range = false;
                middle = current[0];
            }else{
                middle = (current[1] + current[0]) / 2.0;
            }

            int index = Arrays.binarySearch(array, ((Double) middle));
            if(index < 0){
                //(-(insertion point) - 1)
                index = (-(index))-1;
                if(previous){
                    index--;
                }
            }else{
                if(next){
                    //move the closest element above current value
                    index++;
                }else if(previous){
                    index--;
                }
            }

            if(index<0) index = 0;
            if(index>=array.length) index = array.length-1;

            moveTo(array[index]);
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(AbstractCanvas2D.ENVELOPE_KEY)){
            Double[] range = map.getCanvas().getAxisRange(axisIndexFinder);

            if(range == null){
                range = new Double[2];
                range[0] = Double.NEGATIVE_INFINITY;
                range[1] = Double.POSITIVE_INFINITY;
            }else{
                if(range[0] == null){
                    range[0] = Double.NEGATIVE_INFINITY;
                }
                if(range[1] == null){
                    range[1] = Double.POSITIVE_INFINITY;
                }
            }

            updateSpiners(range[0], range[1]);
            repaint();

        } else if (evt.getSource() instanceof MapLayer && !useMenu) {
            checkLayerBands((MapItem) evt.getSource(), CollectionChangeEvent.ITEM_CHANGED);
        }
    }

    void moveTo(final Double targetValue) {
        if (getMap() != null && targetValue != null) {
            final Double[] range = getMap().getCanvas().getAxisRange(axisIndexFinder);
            try{
                if (range == null || range[0] == null || range[1] == null) {
                    getMap().getCanvas().setAxisRange(targetValue, targetValue, axisIndexFinder, crs);
                } else {
                    double middle = (range[0] + range[1]) / 2l;
                    double step = targetValue - middle;
                    double start = range[0] + step;
                    double end = range[1] + step;
                    getMap().getCanvas().setAxisRange(start, end, axisIndexFinder, crs);
                }
            } catch (TransformException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
            JMapAxisLine.this.repaint();
        }
    }

    private Double[] getCurrentRange(){
        if (getMap() != null) {
            final Double[] range = getMap().getCanvas().getAxisRange(axisIndexFinder);
            return range;
        }
        return null;
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);

        if(map == null) return;

        final Double[] range = getMap().getCanvas().getAxisRange(axisIndexFinder);

        if(range == null) return;

        if(range[0] == null && range[1] == null) return;

        double start;
        double end;
        double center;
        final int orientation = getOrientation();
        if(orientation == SwingConstants.SOUTH || orientation == SwingConstants.NORTH){
            start = -5;
            end = getWidth() +5;
            center = -5;
        }else if(orientation == SwingConstants.EAST || orientation == SwingConstants.WEST){
            start = getHeight() +5;
            end = -5;
            center = -5;
        }else{
            throw new IllegalArgumentException("Invalid orientation : "+orientation);
        }

        if(range[0] != null) start = getModel().getGraphicValueAt(range[0]);
        if(range[1] != null) end = getModel().getGraphicValueAt(range[1]);

        //apply change if there are some
        if(edit != null){
            if(selected == 0){
                start = getModel().getGraphicValueAt(edit);
            }else if(selected == 2){
                end = getModel().getGraphicValueAt(edit);
            }else if(selected == 1){
                double middle = (range[0] + range[1]) / 2l;
                double step = edit - middle;
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

        if(orientation == SwingConstants.SOUTH || orientation == SwingConstants.NORTH){
            g2d.setColor(SECOND);
            g2d.fillRect((int)start,0,(int)(end-start),getHeight());

            g2d.setColor(MAIN);
            g2d.setStroke(new BasicStroke(LIMIT_WIDTH*2));
            g2d.drawLine((int)start,0, (int)start, getHeight());
            g2d.drawLine((int)end,0,  (int)end, getHeight());
            g2d.setStroke(new BasicStroke(LIMIT_WIDTH*4));
            g2d.drawLine((int)center,0,  (int)center, getHeight());

        }else if(orientation == SwingConstants.EAST || orientation == SwingConstants.WEST){
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

    /**
     * Browse the source MapItem to determine if some {@link JLayerBand} should
     * be created or removed.
     * @param source The mapItem on which an event occured.
     * @param checkType The event type, as described in {@link CollectionChangeEvent}
     */
    private void checkLayerBands(MapItem source, int checkType) {
        if (source == null) {
            return;
        }

        if (source instanceof MapLayer) {
            final MapLayer layer = (MapLayer) source;
            // First, we check if we already get a listener on this object.
            if (checkType == CollectionChangeEvent.ITEM_ADDED) {
                final ItemListener.Weak weak = new ItemListener.Weak(this);
                layer.removeItemListener(weak);
                layer.addItemListener(weak);
            }
            boolean exist = false;
            for (JNavigatorBand band : getBands()) {
                if (band instanceof JLayerBand) {
                    final JLayerBand lb = (JLayerBand) band;
                    if (layer.equals(lb.getLayer())) {
                        exist = true;
                        if (checkType == CollectionChangeEvent.ITEM_REMOVED
                                || !layer.isSelectable()) {
                            getBands().remove(lb);
                        }
                        break;
                    }
                }
            }
            if (!exist && layer.isSelectable()) {
                final JLayerBand band = new JLayerBand(layer, getModel());
                if (!band.isEmpty()) {
                    getBands().add(band);
                }
            }

            // If an element have been added or removed, we'll parse item to find
            // which layer to add or delete.
        } else if (checkType != CollectionChangeEvent.ITEM_CHANGED) {
            for (MapItem child : source.items()) {
                checkLayerBands(child, checkType);
            }
        }
    }

    @Override
    public void layerChange(CollectionChangeEvent<MapLayer> event) {}

    /**
     * If an item of the current {@link MapContext} changed, we'll check for the
     * corresponding {@link JLayerBand} of this axis.
     * @param event
     */
    @Override
    public void itemChange(CollectionChangeEvent<MapItem> event) {
        if (!useMenu) {
            for (MapItem item : event.getItems()) {
                checkLayerBands(item, event.getType());
            }
        }
    }

}

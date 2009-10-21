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
package org.geotoolkit.gui.swing.go2.control.creation;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import org.geotoolkit.data.FeatureSource;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.gui.swing.RoundedBorder;
import org.geotoolkit.gui.swing.go2.CanvasHandler;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.misc.LayerListRenderer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class DefaultEditionHandler implements CanvasHandler {

    private static final Color MAIN_COLOR = Color.RED;

    private final DefaultEditionDecoration deco = new DefaultEditionDecoration();
    private final GestureProxy gestureProxy = new GestureProxy();
    private final EditionHelper helper = new EditionHelper(this);

    private final Action startAction = new AbstractAction("Start") {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            final Object candidate = guiLayers.getSelectedItem();
            if(candidate instanceof FeatureMapLayer){
                FeatureMapLayer layer = (FeatureMapLayer)candidate;
                FeatureSource fs = layer.getFeatureSource();
                if(fs instanceof FeatureStore){
                    final Class c = fs.getSchema().getGeometryDescriptor().getType().getBinding();
                    guiLayers.setEnabled(false);
                    startAction.setEnabled(false);
                    guiEnd.setEnabled(true);
                    if(c == Point.class){
                        setDelegate(new PointDelegate(DefaultEditionHandler.this));
                    }else if(c == LineString.class){
                    }else if(c == Polygon.class){
                    }else if(c == MultiPoint.class){
                    }else if(c == MultiLineString.class){
                    }else if(c == MultiPolygon.class){
                    }else if(c == Geometry.class){
                    }
                }
            }

        }
    };
    private final Action endAction = new AbstractAction("End") {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            reset();
        }
    };

    private final JComboBox guiLayers = new JComboBox();
    private final JButton guiStart = new JButton(startAction);
    private final JButton guiEnd = new JButton(endAction);

    private Map2D map;
    private AbstractEditionDelegate delegate = null;

    public DefaultEditionHandler(Map2D map) {
        this.map = map;

        guiLayers.setRenderer(new LayerListRenderer());
        guiLayers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                final Object candidate = guiLayers.getSelectedItem();
            if(candidate instanceof FeatureMapLayer){
                FeatureMapLayer layer = (FeatureMapLayer)candidate;
                FeatureSource fs = layer.getFeatureSource();
                if(fs instanceof FeatureStore){
                    guiStart.setEnabled(true);
                }
            }
            }
        });

    }

    public void setMap(Map2D map){
        this.map = map;

        guiLayers.setEnabled(false);

        final List<Object> objects = new ArrayList<Object>();
        objects.add("-");

        if(map != null){
            AbstractContainer2D container = map.getCanvas().getContainer();
            if(container instanceof ContextContainer2D){
                guiLayers.setEnabled(true);
                ContextContainer2D cc = (ContextContainer2D) container;
                if(cc != null && cc.getContext() != null){
                    objects.addAll(cc.getContext().layers());
                }
            }
        }

        guiLayers.setModel(new ListComboBoxModel(objects));
        guiLayers.setSelectedIndex(0);

    }

    public Map2D getMap() {
        return map;
    }

    FeatureMapLayer getEditedLayer(){
        return (FeatureMapLayer) guiLayers.getSelectedItem();
    }

    public EditionHelper getHelper(){
        return helper;
    }

    public DefaultEditionDecoration getDecoration(){
        return deco;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void install(Component component) {
        installChooser();
        //install decoration and listener
        gestureProxy.install(component);
        map.addDecoration(0,deco);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void uninstall(Component component) {
        gestureProxy.uninstall(component);
        map.removeDecoration(deco);
    }

    @Override
    public J2DCanvas getCanvas() {
        return map.getCanvas();
    }


    public void reset(){
        guiStart.setEnabled(false);
        guiEnd.setEnabled(false);
        deco.setToolsPane(null);
        setDelegate(null);
        installChooser();
    }

    private void installChooser(){
        final JPanel panNorth = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panNorth.setOpaque(false);
        final JPanel panTools = new JPanel(new FlowLayout());
        panTools.setOpaque(false);
        panTools.setBorder(new RoundedBorder());
        panTools.add(guiLayers);
        panTools.add(guiStart);
        panTools.add(new JLabel("      "));
        panTools.add(guiEnd);
        panNorth.add(panTools);

        deco.removeAll();
        deco.add(BorderLayout.NORTH, panNorth);
        deco.revalidate();
    }

    private void setDelegate(AbstractEditionDelegate delegate){
        this.delegate = delegate;
        gestureProxy.delegate = this.delegate;

        if(delegate != null){
            delegate.setMap(map);
            delegate.initialize();
        }

    }
    
    /**
     * Will send information to sub handler when necessary.
     */
    private final class GestureProxy implements MouseInputListener,KeyListener, MouseWheelListener {

        private Object delegate;

        public void install(Component component){
            component.addMouseListener(this);
            component.addMouseMotionListener(this);
            component.addMouseWheelListener(this);
            component.addKeyListener(this);
        }

        public void uninstall(Component component){
            component.removeMouseListener(this);
            component.removeMouseMotionListener(this);
            component.removeMouseWheelListener(this);
            component.removeKeyListener(this);
        }

        @Override
        public void mouseClicked(MouseEvent arg0) {
            if(delegate != null && delegate instanceof MouseInputListener){
                final MouseInputListener candidate = (MouseInputListener) delegate;
                candidate.mouseClicked(arg0);
            }
        }

        @Override
        public void mousePressed(MouseEvent arg0) {
            if(delegate != null && delegate instanceof MouseInputListener){
                final MouseInputListener candidate = (MouseInputListener) delegate;
                candidate.mousePressed(arg0);
            }
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            if(delegate != null && delegate instanceof MouseInputListener){
                final MouseInputListener candidate = (MouseInputListener) delegate;
                candidate.mouseReleased(arg0);
            }
        }

        @Override
        public void mouseEntered(MouseEvent arg0) {
            map.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            if(delegate != null && delegate instanceof MouseInputListener){
                final MouseInputListener candidate = (MouseInputListener) delegate;
                candidate.mouseEntered(arg0);
            }
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
            if(delegate != null && delegate instanceof MouseInputListener){
                final MouseInputListener candidate = (MouseInputListener) delegate;
                candidate.mouseExited(arg0);
            }
        }

        @Override
        public void mouseDragged(MouseEvent arg0) {
            if(delegate != null && delegate instanceof MouseInputListener){
                final MouseInputListener candidate = (MouseInputListener) delegate;
                candidate.mouseDragged(arg0);
            }
        }

        @Override
        public void mouseMoved(MouseEvent arg0) {
            if(delegate != null && delegate instanceof MouseInputListener){
                final MouseInputListener candidate = (MouseInputListener) delegate;
                candidate.mouseMoved(arg0);
            }
        }

        @Override
        public void keyTyped(KeyEvent arg0) {
            if(delegate != null && delegate instanceof KeyListener){
                final KeyListener candidate = (KeyListener) delegate;
                candidate.keyTyped(arg0);
            }
        }

        @Override
        public void keyPressed(KeyEvent arg0) {
            if(delegate != null && delegate instanceof KeyListener){
                final KeyListener candidate = (KeyListener) delegate;
                candidate.keyPressed(arg0);
            }
        }

        @Override
        public void keyReleased(KeyEvent arg0) {
            if(delegate != null && delegate instanceof KeyListener){
                final KeyListener candidate = (KeyListener) delegate;
                candidate.keyReleased(arg0);
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent arg0) {
            if(delegate != null && delegate instanceof MouseWheelListener){
                final MouseWheelListener candidate = (MouseWheelListener) delegate;
                candidate.mouseWheelMoved(arg0);
            }
        }

    }

}

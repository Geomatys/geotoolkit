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
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.session.Session;

import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.gui.swing.RoundedBorder;
import org.geotoolkit.gui.swing.go2.CanvasHandler;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.misc.LayerListRenderer;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.jdesktop.swingx.JXErrorPane;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class DefaultEditionHandler implements CanvasHandler {

    private final DefaultEditionDecoration deco = new DefaultEditionDecoration();
    private final GestureProxy gestureProxy = new GestureProxy();
    private final EditionHelper helper = new EditionHelper(this);

    private final Action startAction = new AbstractAction(MessageBundle.getString("start")) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            final Object candidate = guiLayers.getSelectedItem();
            if(candidate instanceof FeatureMapLayer){
                FeatureMapLayer layer = (FeatureMapLayer)candidate;
                if(layer.getCollection().isWritable()){
                    final Class c = layer.getCollection().getFeatureType().getGeometryDescriptor().getType().getBinding();
                    guiLayers.setEnabled(false);
                    startAction.setEnabled(false);
                    guiCommit.setEnabled(true);
                    if(c == Point.class){
                        setDelegate(new PointDelegate(DefaultEditionHandler.this));
                    }else if(c == LineString.class){
                        setDelegate(new LineDelegate(DefaultEditionHandler.this));
                    }else if(c == Polygon.class){
                        setDelegate(new PolygonDelegate(DefaultEditionHandler.this));
                    }else if(c == MultiPoint.class){
                        setDelegate(new MultiPointDelegate(DefaultEditionHandler.this));
                    }else if(c == MultiLineString.class){
                        setDelegate(new MultiLineDelegate(DefaultEditionHandler.this));
                    }else if(c == MultiPolygon.class){
                        setDelegate(new MultiPolygonDelegate(DefaultEditionHandler.this));
                    }else if(Geometry.class.isAssignableFrom(c)){
                        setDelegate(new AnyTypeDelegate(DefaultEditionHandler.this));
                    }
                    checkChanges();
                }
            }

        }
    };

    private final Action rollbackAction = new AbstractAction(MessageBundle.getString("rollback")) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            final Object candidate = guiLayers.getSelectedItem();
            if(candidate instanceof FeatureMapLayer){
                FeatureMapLayer layer = (FeatureMapLayer)candidate;
                if(layer != null){
                    layer.getCollection().getSession().rollback();

                    final J2DCanvas canvas = getCanvas();
                    if(canvas != null){
                        canvas.repaint();
                    }

                }
            }

            reset();
        }
    };

    private final Action commitAction = new AbstractAction(MessageBundle.getString("commit")) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            final Object candidate = guiLayers.getSelectedItem();
            if(candidate instanceof FeatureMapLayer){
                FeatureMapLayer layer = (FeatureMapLayer)candidate;
                if(layer != null){
                    try {
                        layer.getCollection().getSession().commit();
                    } catch (DataStoreException ex) {
                        JXErrorPane.showDialog(ex);
                    }
                }
            }

            reset();
        }
    };

    private final JComboBox guiLayers = new JComboBox();
    private final JButton guiStart = new JButton(startAction);
    private final JButton guiRollBack = new JButton(rollbackAction);
    private final JButton guiCommit = new JButton(commitAction);

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
                    if(layer.getCollection().isWritable()){
                        guiStart.setEnabled(true);
                    }
                }
                checkChanges();
            }
        });

        installChooser();
    }

    /**
     * Check if there are changes in the current session and
     * activate commit/rollback buttons.
     */
    private void checkChanges(){
        boolean changes = false;
        final Object candidate = guiLayers.getSelectedItem();

        if(candidate instanceof FeatureMapLayer){
            final FeatureMapLayer layer = (FeatureMapLayer)candidate;

            if(layer != null){
                final FeatureCollection col = layer.getCollection();
                final Session session = col.getSession();
                if(session != null){
                    changes = session.hasPendingChanges();
                }
            }
        }

        guiCommit.setEnabled(changes);
        guiRollBack.setEnabled(changes);
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
        guiStart.setEnabled(true);
        guiLayers.setEnabled(true);
        checkChanges();
        deco.setToolsPane(null);
        deco.setGestureMessages(null, null, null, null);
        setDelegate(null);
    }

    private void installChooser(){
        final JPanel panNorth = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panNorth.setOpaque(false);
        final JPanel panTools = new JPanel(new FlowLayout());
        panTools.setOpaque(false);
        panTools.setBackground(Color.WHITE);
        panTools.setBorder(new RoundedBorder());
        panTools.add(guiLayers);
        panTools.add(guiStart);
        panTools.add(new JLabel("  "));
        panTools.add(guiRollBack);
        panTools.add(guiCommit);
        panNorth.add(panTools);

        deco.setToNorth(panNorth);
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

            //todo the good place to do so but since events are not working yet
            checkChanges();
        }

        @Override
        public void mousePressed(MouseEvent arg0) {
            if(delegate != null && delegate instanceof MouseInputListener){
                final MouseInputListener candidate = (MouseInputListener) delegate;
                candidate.mousePressed(arg0);
            }

            //todo the good place to do so but since events are not working yet
            checkChanges();
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            if(delegate != null && delegate instanceof MouseInputListener){
                final MouseInputListener candidate = (MouseInputListener) delegate;
                candidate.mouseReleased(arg0);
            }

            //todo the good place to do so but since events are not working yet
            checkChanges();
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

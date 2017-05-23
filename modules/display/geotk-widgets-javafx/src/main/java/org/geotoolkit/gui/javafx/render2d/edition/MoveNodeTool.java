/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.render2d.edition;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.feature.FeatureExt;
import org.opengis.feature.Feature;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.FXPanMouseListen;
import org.geotoolkit.gui.javafx.render2d.shape.FXGeometryLayer;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.FeatureMapLayer;
import org.opengis.feature.AttributeType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MoveNodeTool extends AbstractEditionTool{

    public static final class Spi extends AbstractEditionToolSpi{

        public Spi() {
            super("MoveNode",
                GeotkFX.getI18NString(MoveNodeTool.class, "title"),
                GeotkFX.getI18NString(MoveNodeTool.class, "abstract"),
                GeotkFX.ICON_EDIT);
        }

        @Override
        public boolean canHandle(Object candidate) {
            if(candidate instanceof FeatureMapLayer){
                final FeatureMapLayer fml = (FeatureMapLayer) candidate;
                if(!fml.getCollection().isWritable()) return false;

                final AttributeType desc = FeatureExt.getDefaultGeometryAttribute(fml.getCollection().getFeatureType());
                return desc != null;
            }
            return false;
        }

        @Override
        public EditionTool create(FXMap map, Object layer) {
            return new MoveNodeTool(map, (FeatureMapLayer) layer);
        }
    };


    private final BorderPane configPane = null;
    private final BorderPane helpPane = new BorderPane();
    private final FeatureMapLayer layer;
    private final EditionHelper helper;
    private final MouseListen mouseInputListener = new MouseListen();
    private final FXGeometryLayer decoration= new EditionLayer();


    private Feature feature = null;
    private final EditionHelper.EditionGeometry selection = new EditionHelper.EditionGeometry();
    private boolean modified = false;
    private MouseButton pressed = null;

    public MoveNodeTool(FXMap map, FeatureMapLayer layer) {
        super(EditionHelper.getToolSpi("MoveNode"));
        this.layer = layer;
        this.helper = new EditionHelper(map, layer);
    }

    @Override
    public Node getConfigurationPane() {
        return configPane;
    }

    @Override
    public Node getHelpPane() {
        return helpPane;
    }

    private void reset(){
        feature = null;
        selection.reset();
        decoration.getGeometries().clear();
        decoration.setNodeSelection(null);
    }

    private void refreshDecoration(){
        decoration.getGeometries().setAll(this.selection.geometry.get());
        decoration.setNodeSelection(this.selection);
    }

    public void setCurrentFeature(final Feature feature){
        this.feature = feature;
        if(feature != null){
            this.selection.geometry.set( helper.toObjectiveCRS(feature) );
        }else{
            this.selection.geometry.set( null );
        }
        refreshDecoration();
    }

    @Override
    public void install(final FXMap component) {
        super.install(component);
        component.addEventHandler(MouseEvent.ANY, mouseInputListener);
        component.addEventHandler(ScrollEvent.ANY, mouseInputListener);
        component.addEventHandler(KeyEvent.ANY, mouseInputListener);
        component.setCursor(Cursor.CROSSHAIR);
        component.addDecoration(0,decoration);
    }

    @Override
    public boolean uninstall(FXMap component) {
        super.uninstall(component);
        component.removeEventHandler(MouseEvent.ANY, mouseInputListener);
        component.removeEventHandler(ScrollEvent.ANY, mouseInputListener);
        component.removeEventHandler(KeyEvent.ANY, mouseInputListener);
        component.removeDecoration(decoration);
        return true;
    }

    private class MouseListen extends FXPanMouseListen {

        public MouseListen() {
            super(MoveNodeTool.this);
        }

        @Override
        public void mouseClicked(final MouseEvent e) {

            final MouseButton button = e.getButton();

            if(button == MouseButton.PRIMARY){
                if(selection.geometry.get() == null){
                    setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
                }else if(e.getClickCount() >= 2){
                    //double click = add a node
                    final Geometry result;
                    if(selection.geometry.get() instanceof LineString){
                        result = helper.insertNode((LineString)selection.geometry.get(), e.getX(), e.getY());
                    }else if(selection.geometry.get() instanceof Polygon){
                        result = helper.insertNode((Polygon)selection.geometry.get(), e.getX(), e.getY());
                    }else if(selection.geometry.get() instanceof GeometryCollection){
                        result = helper.insertNode((GeometryCollection)selection.geometry.get(), e.getX(), e.getY());
                    }else{
                        result = selection.geometry.get();
                    }
                    modified = modified || result != selection.geometry.get();
                    selection.geometry.set( result );
                    decoration.getGeometries().setAll(selection.geometry.get());
                }else if(e.getClickCount() == 1){
                    //single click with a geometry = select a node
                    helper.grabGeometryNode(e.getX(), e.getY(), selection);
                    decoration.setNodeSelection(selection);
                }
            }else if(button == MouseButton.SECONDARY){
                helper.sourceModifyFeature(feature, selection.geometry.get(), true);
                reset();
            }

        }

        @Override
        public void mousePressed(final MouseEvent e) {
            pressed = e.getButton();

            if(pressed == MouseButton.PRIMARY){
                if(selection.geometry.get() == null){
                    setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
                }else if(e.getClickCount() == 1){
                    //single click with a geometry = select a node
                    helper.grabGeometryNode(e.getX(), e.getY(), selection);
                    decoration.setNodeSelection(selection);
                }
            }

            super.mousePressed(e);
        }

        @Override
        public void mouseDragged(final MouseEvent e) {

            if(pressed == MouseButton.PRIMARY && selection != null){
                //dragging node
                selection.moveSelectedNode(helper.toCoord(e.getX(), e.getY()));
                refreshDecoration();
                modified = true;
                return;
            }

            super.mouseDragged(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if(KeyCode.DELETE == e.getCode() && selection != null){
                //delete node
                selection.deleteSelectedNode();
                refreshDecoration();
                modified = true;
            }
        }


    }



}

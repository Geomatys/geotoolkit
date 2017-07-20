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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javafx.scene.Cursor;
import javafx.scene.Node;
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
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.FeatureMapLayer;
import org.opengis.feature.PropertyNotFoundException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MoveGeometryTool extends AbstractEditionTool{

    public static final class Spi extends AbstractEditionToolSpi{

        public Spi() {
            super("MoveGeometry",
                GeotkFX.getI18NString(MoveGeometryTool.class, "title"),
                GeotkFX.getI18NString(MoveGeometryTool.class, "abstract"),
                GeotkFX.ICON_MOVE);
        }

        @Override
        public boolean canHandle(Object candidate) {
            if(candidate instanceof FeatureMapLayer){
                final FeatureMapLayer fml = (FeatureMapLayer) candidate;
                if(!fml.getCollection().isWritable()) return false;

                try {
                    // Check we can reach a geometry property
                    FeatureExt.getDefaultGeometry(fml.getCollection().getType());
                    return true;
                } catch (PropertyNotFoundException | IllegalStateException e) {
                    return false;
                }
            }
            return false;
        }

        @Override
        public EditionTool create(FXMap map, Object layer) {
            return new MoveGeometryTool(map, (FeatureMapLayer) layer);
        }
    };


    private final BorderPane configPane = null;
    private final BorderPane helpPane = new BorderPane();
    private final FeatureMapLayer layer;
    private final EditionHelper helper;
    private final MouseListen mouseInputListener = new MouseListen();
    private final FXGeometryLayer decoration= new EditionLayer();


    private Feature feature = null;
    private Geometry geometry = null;
    private final List<Geometry> subGeometries =  new ArrayList<>();
    private boolean draggingAll = false;

    private MouseButton pressed = null;
    private double lastX = 0;
    private double lastY = 0;

    public MoveGeometryTool(FXMap map, FeatureMapLayer layer) {
        super(EditionHelper.getToolSpi("MoveGeometry"));
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
        geometry = null;
        subGeometries.clear();
        draggingAll = false;
        decoration.getGeometries().clear();
        pressed = null;
        lastX = 0;
        lastY = 0;
    }

    private void setCurrentFeature(final Feature feature){
        this.feature = feature;
        if(feature != null){
            this.geometry = helper.toObjectiveCRS(feature);
        }else{
            this.geometry = null;
        }
        decoration.getGeometries().setAll(this.geometry);
    }

    @Override
    public void install(final FXMap component) {
        super.install(component);
        component.addEventHandler(MouseEvent.ANY, mouseInputListener);
        component.addEventHandler(ScrollEvent.ANY, mouseInputListener);
        component.setCursor(Cursor.CROSSHAIR);
        component.addDecoration(0,decoration);
    }

    @Override
    public boolean uninstall(FXMap component) {
        super.uninstall(component);
        component.removeEventHandler(MouseEvent.ANY, mouseInputListener);
        component.removeEventHandler(ScrollEvent.ANY, mouseInputListener);
        component.removeDecoration(decoration);
        return true;
    }

    private class MouseListen extends FXPanMouseListen {

        public MouseListen() {
            super(MoveGeometryTool.this);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            pressed = e.getButton();
            lastX = getMouseX(e);
            lastY = getMouseY(e);

            if (pressed == MouseButton.PRIMARY) {
                //find feature where mouse clicked
                if(geometry == null){
                    setCurrentFeature(helper.grabFeature(lastX,lastY, false));
                }

                if(geometry != null){
                    try {
                        //start dragging mode
                        final Geometry mouseGeo = helper.mousePositionToGeometry(lastX,lastY);
                        if(mouseGeo.intersects(geometry)){
                            draggingAll = true;
                        }
                    } catch (Exception ex) {
                        Loggers.JAVAFX.log(Level.WARNING, null, ex);
                    }
                    return;
                }
            }

            super.mousePressed(e);
        }

        @Override
        public void mouseDragged(final MouseEvent e) {

            if(draggingAll && pressed == MouseButton.PRIMARY){
                double currentX = getMouseX(e);
                double currentY = getMouseY(e);

                //update geometry/feature position
                helper.moveGeometry(geometry, currentX-lastX, currentY-lastY);
                decoration.getGeometries().setAll(geometry);

                lastX = currentX;
                lastY = currentY;
                return;
            }
            super.mouseDragged(e);
        }

        @Override
        public void mouseReleased(final MouseEvent e) {

            if(draggingAll && pressed == MouseButton.PRIMARY){
                double currentX = getMouseX(e);
                double currentY = getMouseY(e);

                //last position update
                helper.moveGeometry(geometry, currentX-lastX, currentY-lastY);
                decoration.getGeometries().setAll(geometry);

                //save
                helper.sourceModifyFeature(feature, geometry, true);
                reset();
                return;
            }
            super.mouseReleased(e);
        }


    }



}

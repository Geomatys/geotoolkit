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
import com.vividsolutions.jts.geom.Point;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import org.apache.sis.feature.FeatureExt;
import org.geotoolkit.geometry.jts.JTS;
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
public class CreatePointTool extends AbstractEditionTool{

    public static final class Spi extends AbstractEditionToolSpi{

        public Spi() {
            super("CreatePoint",
                GeotkFX.getI18NString(CreatePointTool.class, "title"),
                GeotkFX.getI18NString(CreatePointTool.class, "abstract"),
                GeotkFX.ICON_ADD);
        }
    
        @Override
        public boolean canHandle(Object candidate) {
            if(candidate instanceof FeatureMapLayer){
                final FeatureMapLayer fml = (FeatureMapLayer) candidate;
                if(!fml.getCollection().isWritable()) return false;

                final AttributeType desc = FeatureExt.getDefaultGeometryAttribute(fml.getCollection().getFeatureType());
                if(desc == null) return false;

                return Point.class.isAssignableFrom(desc.getValueClass())
                    || Geometry.class.equals(desc.getValueClass());
            }
            return false;
        }

        @Override
        public EditionTool create(FXMap map, Object layer) {
            return new CreatePointTool(map, (FeatureMapLayer) layer);
        }
    };


    private final BorderPane configPane = null;
    private final BorderPane helpPane = new BorderPane();
    private final FeatureMapLayer layer;
    private final EditionHelper helper;
    private final MouseListen mouseInputListener = new MouseListen();
    private final FXGeometryLayer decoration= new EditionLayer();

    public CreatePointTool(FXMap map, FeatureMapLayer layer) {
        super(EditionHelper.getToolSpi("CreatePoint"));
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
            super(CreatePointTool.this);
        }

        @Override
        public void mouseClicked(final MouseEvent e) {

            mousebutton = e.getButton();

            if(mousebutton == MouseButton.PRIMARY){
                final double x = getMouseX(e);
                final double y = getMouseY(e);
                final Point geometry = helper.toJTS(x,y);
                JTS.setCRS(geometry, map.getCanvas().getObjectiveCRS2D());
                helper.sourceAddGeometry(geometry);
            }
        }

    }



}

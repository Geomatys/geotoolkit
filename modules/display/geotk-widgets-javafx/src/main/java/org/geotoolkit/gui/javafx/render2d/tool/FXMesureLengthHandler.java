/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.gui.javafx.render2d.tool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.geotoolkit.display.MeasureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gui.javafx.render2d.FXAbstractNavigationHandler;
import org.geotoolkit.gui.javafx.render2d.shape.FXGeometryLayer;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.FXPanMouseListen;
import org.geotoolkit.gui.javafx.render2d.navigation.AbstractMouseHandler;
import org.geotoolkit.internal.Loggers;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXMesureLengthHandler extends FXAbstractNavigationHandler {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    private final MouseListen mouseInputListener = new MouseListen();
    private final double zoomFactor = 2;
    private final List<Coordinate> coords = new ArrayList<>();
    private final FXGeometryLayer layer;
    private final Label uiLength = new Label();
    private final ChoiceBox<Unit> uiUnit = new ChoiceBox<>();
    private final HBox pane = new HBox(10,uiLength,uiUnit);
    
    
    public FXMesureLengthHandler(final FXMap map) {
        super(map);
        layer = new FXGeometryLayer();        
        uiLength.setMaxHeight(Double.MAX_VALUE);
        uiUnit.setItems(FXCollections.observableArrayList(SI.KILOMETRE,SI.METRE));
        uiUnit.getSelectionModel().selectFirst();
        
        pane.setAlignment(Pos.CENTER);
        pane.setFillHeight(true);
                
        uiUnit.valueProperty().addListener((ObservableValue<? extends Unit> observable, Unit oldValue, Unit newValue) -> {
            updateGeometry();
        });
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void install(final FXMap component) {
        super.install(component);
        component.addEventHandler(MouseEvent.ANY, mouseInputListener);
        component.addEventHandler(ScrollEvent.ANY, mouseInputListener);
        map.setCursor(Cursor.CROSSHAIR);
        map.addDecoration(0,layer);
        component.setBottom(pane);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean uninstall(final FXMap component) {
        super.uninstall(component);
        component.removeEventHandler(MouseEvent.ANY, mouseInputListener);
        component.removeEventHandler(ScrollEvent.ANY, mouseInputListener);
        map.removeDecoration(layer);
        component.setBottom(null);
        return true;
    }
    
    private void updateGeometry(){
        final List<Geometry> geoms = new ArrayList<>();
        if(coords.size() == 1){
            //single point
            final Geometry geom = GEOMETRY_FACTORY.createPoint(coords.get(0));
            JTS.setCRS(geom, map.getCanvas().getObjectiveCRS2D());
            geoms.add(geom);
        }else if(coords.size() > 1){
            //line
            final Geometry geom = GEOMETRY_FACTORY.createLineString(coords.toArray(new Coordinate[coords.size()]));
            JTS.setCRS(geom, map.getCanvas().getObjectiveCRS2D());
            geoms.add(geom);
        }
        layer.getGeometries().setAll(geoms);
        
        if(geoms.isEmpty()){
            uiLength.setText("-");
        }else{
            uiLength.setText(NumberFormat.getNumberInstance().format(
                    MeasureUtilities.calculateLenght(geoms.get(0), 
                    map.getCanvas().getObjectiveCRS2D(), uiUnit.getValue())));
        }
    }
    
    private class MouseListen extends FXPanMouseListen {
        
        public MouseListen() {
            super(FXMesureLengthHandler.this);
        }

        @Override
        public void mouseClicked(final MouseEvent e) {
            mousebutton = e.getButton();
            if (mousebutton == MouseButton.PRIMARY) {
                //add a coordinate
                try {
                    final AffineTransform2D dispToObj = map.getCanvas().getDisplayToObjective();
                    final double[] crds = new double[]{e.getX(),e.getY()};
                    dispToObj.transform(crds, 0, crds, 0, 1);
                    coords.add(new Coordinate(crds[0], crds[1]));
                    updateGeometry();
                } catch (NoninvertibleTransformException ex) {
                    Loggers.JAVAFX.log(Level.WARNING, null, ex);
                }

            } else if (mousebutton == MouseButton.SECONDARY) {
                //erase coordiantes
                coords.clear();
                updateGeometry();
            }
        }
    }
    
}

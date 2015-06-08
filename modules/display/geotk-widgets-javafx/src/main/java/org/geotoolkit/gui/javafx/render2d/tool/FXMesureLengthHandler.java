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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.geotoolkit.display.MeasureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gui.javafx.render2d.FXAbstractNavigationHandler;
import org.geotoolkit.gui.javafx.render2d.FXGridDecoration;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.FXPanMouseListen;
import org.geotoolkit.gui.javafx.render2d.shape.FXGeometryLayer;
import org.geotoolkit.internal.Loggers;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXMesureLengthHandler extends FXAbstractNavigationHandler {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    private final MouseListen mouseInputListener = new MouseListen();
    private final List<Coordinate> coords = new ArrayList<>();
    private final FXGeometryLayer layer = new FXGeometryLayer();
    private final FXGridDecoration deco = new FXGridDecoration();
    private final Label uiLength = new Label();
    private final ChoiceBox<Unit> uiUnit = new ChoiceBox<>();
    private final HBox pane = new HBox(10,uiLength,uiUnit);
    
    
    public FXMesureLengthHandler(final FXMap map) {
        super(map);    
        uiLength.setMaxHeight(Double.MAX_VALUE);
        uiUnit.setItems(FXCollections.observableArrayList(SI.KILOMETRE,SI.METRE));
        uiUnit.getSelectionModel().selectFirst();
        
        pane.setAlignment(Pos.CENTER);
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        pane.setPadding(new Insets(10, 10, 10, 10));
        deco.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        deco.getColumnConstraints().add(new ColumnConstraints(0,HBox.USE_COMPUTED_SIZE,Double.MAX_VALUE,Priority.ALWAYS,HPos.CENTER,true));
        deco.getColumnConstraints().add(new ColumnConstraints(0,HBox.USE_COMPUTED_SIZE,Double.MAX_VALUE,Priority.NEVER,HPos.CENTER,true));
        deco.getColumnConstraints().add(new ColumnConstraints(0,HBox.USE_COMPUTED_SIZE,Double.MAX_VALUE,Priority.ALWAYS,HPos.CENTER,true));
                
        uiUnit.valueProperty().addListener((ObservableValue<? extends Unit> observable, Unit oldValue, Unit newValue) -> {
            updateGeometry();
        });

        deco.add(pane, 1, 0);
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
        map.addDecoration(layer);
        map.addDecoration(deco);
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
        map.removeDecoration(deco);
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

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
package org.geotoolkit.gui.javafx.render2d.shape;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.FXMapDecoration;
import org.geotoolkit.gui.javafx.render2d.edition.EditionHelper;
import org.geotoolkit.internal.Loggers;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXGeometryLayer extends Pane implements FXMapDecoration{
    
    private FXMap map;
    private final ObservableList<Geometry> geoms = FXCollections.observableArrayList();
    
    private final PropertyChangeListener canvasListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            final String propName = evt.getPropertyName();
            if(J2DCanvas.ENVELOPE_KEY.equals(propName) || J2DCanvas.OBJECTIVE_CRS_KEY.equals(propName) || J2DCanvas.TRANSFORM_KEY.equals(propName)){
                updateGraphics();
            }
        }
    };

    private Paint fill = new Color(0, 0, 0, 0.4);
    private EditionHelper.EditionGeometry editGeom;

    public FXGeometryLayer() {
        geoms.addListener((ListChangeListener.Change<? extends Geometry> c) -> {
            updateGraphics();
        });             
        
        //clip content
        widthProperty().addListener(new ChangeListener(){
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                setClip(new Rectangle(getWidth(), getHeight()));
            }
        });
        heightProperty().addListener(new ChangeListener(){
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                setClip(new Rectangle(getWidth(), getHeight()));
            }
        });
        
        //disable cache, may have many and large geometries
        setCache(false);
        setCacheShape(false);
    }

    public void setFill(Paint fill) {
        this.fill = fill;
    }

    public Paint getFill() {
        return fill;
    }
    
    public ObservableList<Geometry> getGeometries() {
        return geoms;
    }
    
    private synchronized void updateGraphics(){
        final CoordinateReferenceSystem dispCrs = (map==null) ? null : map.getCanvas().getDisplayCRS();
        
        final List<Node> shapes = new ArrayList<>();
        final List<Coordinate> coords = new ArrayList<>();
        //defensive copy
        for(Geometry geom : geoms.toArray(new Geometry[0])){
            if(geom==null) continue;
            try{
                final CoordinateReferenceSystem geomcrs = CRS.getHorizontalComponent(JTS.findCoordinateReferenceSystem(geom));
                if(dispCrs==null || geomcrs==null || org.geotoolkit.referencing.CRS.equalsIgnoreMetadata(geomcrs, dispCrs)){
                    //do nothing
                }else{
                    geom = JTS.transform(geom, org.geotoolkit.referencing.CRS.findMathTransform(geomcrs, dispCrs, true));
                }

                coords.addAll(Arrays.asList(geom.getCoordinates()));
                final FXGeometry fxgeom = new FXGeometry(geom);
                if(geom instanceof Polygon || geom instanceof MultiPolygon){
                    fxgeom.setFill(fill);
                }
                shapes.add(fxgeom);
            }catch(Exception ex){
                Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        
        //create circles where there are points
        if(!coords.isEmpty()){
            final Group group = new Group();
            group.setCache(false);
            for(Coordinate c : coords){
                final Node vertice = createVerticeNode(c,false);
                if(vertice!=null){
                    group.getChildren().add(vertice);
                }
            }
            shapes.add(group);
        }


        //paint the selected node
        selectedNode :
        if(editGeom != null && editGeom.hasNodeSelected()){

            Geometry candidate = editGeom.geometry.get();
            try{
                final CoordinateReferenceSystem geomcrs = CRS.getHorizontalComponent(JTS.findCoordinateReferenceSystem(candidate));
                if(dispCrs==null || geomcrs==null || org.geotoolkit.referencing.CRS.equalsIgnoreMetadata(geomcrs, dispCrs)){
                    //do nothing
                }else{
                    candidate = JTS.transform(editGeom.geometry.get(), org.geotoolkit.referencing.CRS.findMathTransform(geomcrs, dispCrs, true));
                }
            }catch(Exception ex){
                Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
            }

            final Geometry geo = candidate.getGeometryN(editGeom.numSubGeom);

            if(editGeom.numHole < 0){
                final Coordinate coord = geo.getCoordinates()[editGeom.selectedNode[0]];
                shapes.add(createVerticeNode(coord, true));
            }else{
                final Polygon poly = (Polygon) geo;
                final LineString ls = poly.getInteriorRingN(editGeom.numHole);
                final Coordinate coord = ls.getCoordinates()[editGeom.selectedNode[0]];
                shapes.add(createVerticeNode(coord, true));

            }
        }


        //JAVAFX BUG : 
        //getChildren().setAll(shapes);
        Platform.runLater(() -> {
            getChildren().setAll(shapes);
        });
           
    }
    
    protected Node createVerticeNode(Coordinate c, boolean selected){
        final Circle circle = new Circle(c.x, c.y, 5);
        circle.setFill(selected ? Color.BLUE : Color.WHITE);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(1);
        return circle;
    }

    @Override
    public void refresh() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void setMap2D(FXMap map) {
        if(this.map!=null){
            this.map.getCanvas().removePropertyChangeListener(canvasListener);
        }
        this.map = map;
        if(this.map!=null){
            this.map.getCanvas().addPropertyChangeListener(canvasListener);   
        }
    }

    @Override
    public FXMap getMap2D() {
        return map;
    }

    @Override
    public Node getComponent() {
        return this;
    }

    public void setNodeSelection(EditionHelper.EditionGeometry editGeom) {
        this.editGeom = editGeom;
    }
        
}

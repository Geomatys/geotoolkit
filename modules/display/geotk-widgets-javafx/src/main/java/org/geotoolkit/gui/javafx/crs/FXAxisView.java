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
package org.geotoolkit.gui.javafx.crs;

import java.awt.geom.AffineTransform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javax.measure.unit.SI;
import javax.swing.SwingConstants;
import org.apache.sis.measure.Range;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.temporal.object.TemporalConstants;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Display a graphic timeline or numberline for a crs axis.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class FXAxisView extends Control {
    
    public static enum SelectionType {
        SINGLE,
        RANGE
    }
    
    private final ObjectProperty<CoordinateReferenceSystem> crs = new SimpleObjectProperty<>();
    private final DoubleProperty minScale = new SimpleDoubleProperty(0.000001);
    private final DoubleProperty maxScale = new SimpleDoubleProperty(Double.MAX_VALUE);
    private final DoubleProperty scale = new SimpleDoubleProperty(1.0){
        @Override
        public void set(double newValue) {
            super.set(newValue);
            //super.set(XMath.clamp(newValue, minScale.get(), maxScale.get()));
        }
    };
    private final DoubleProperty offset = new SimpleDoubleProperty(0.0);
    private final IntegerProperty orientation = new SimpleIntegerProperty(SwingConstants.SOUTH);
    private final ObjectProperty<SelectionType> selectionType = new SimpleObjectProperty<>();
    private final ObjectProperty<Range<? extends Number>> selection = new SimpleObjectProperty<>();
    
    public FXAxisView(){
        getStyleClass().add("axis-view");
        
        //adjust min/max valid range based on CRS.
        crsProperty().addListener((ObservableValue<? extends CoordinateReferenceSystem> observable, 
                CoordinateReferenceSystem oldValue, CoordinateReferenceSystem newValue) -> {
            final boolean temporal = crs==null || SI.SECOND.isCompatible(
                    crs.get().getCoordinateSystem().getAxis(0).getUnit());            
            if(temporal){
                minScale.set(30.0/TemporalConstants.YEAR_MS);
                maxScale.set(1.0/TemporalConstants.MINUTE_MS);
            }else{
                minScale.set(0.0000001);
                maxScale.set(Double.MAX_VALUE);
            }
        });
        
        crs.set(CommonCRS.Temporal.JAVA.crs());
        scale.set(1.0);
    }
 
    @Override
    protected String getUserAgentStylesheet() {
        return FXAxisView.class.getResource("fxaxisview.css").toExternalForm();
    }
    
    public DoubleProperty scaleProperty() {
        return scale;
    }
    
    public DoubleProperty offsetProperty() {
        return offset;
    }
        
    public IntegerProperty orientationProperty() {
        return orientation;
    }
            
    public ObjectProperty<CoordinateReferenceSystem> crsProperty() {
        return crs;
    }
    
    public ObjectProperty<SelectionType> selectionTypeProperty() {
        return selectionType;
    }
    
    public ObjectProperty<Range<? extends Number>> selectionProperty() {
        return selection;
    }
    
    public double getGraphicValueAt(final double d) {
        return scale.get() * d + offset.get();
    }

    public double getAxisValueAt(final double candidate) {
        return (candidate-offset.get()) / scale.get();
    }

    public void scale(final double factor, double position) {
        position = getAxisValueAt(position);
        final AffineTransform newtrs = new AffineTransform(scale.get(),0,0,1,offset.get(),0);
        newtrs.translate(+position, 0);
        newtrs.scale(factor, 1);
        newtrs.translate(-position, 0);
        scale.set(newtrs.getScaleX());
        offset.set(newtrs.getTranslateX());
    }

    /**
     * 
     * @param tr 
     */
    public void translate(final double tr) {
        offset.set(offset.get()+tr);
    }
    
    /**
     * Set center of the axisview on given position.
     * 
     * @param position in crs unit
     */
    public void moveTo(double position){
        final double currentCenter =  getWidth()/2.0;
        final double newOffset = currentCenter - scale.get()*position;
        offset.set(newOffset);
    }
   
}

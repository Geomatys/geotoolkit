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

package org.geotoolkit.gui.javafx.render2d;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.util.converter.LongStringConverter;
import org.apache.sis.geometry.DirectPosition2D;
import org.controlsfx.control.StatusBar;
import org.geotoolkit.display.canvas.AbstractCanvas2D;
import org.geotoolkit.display2d.canvas.painter.SolidColorPainter;
import org.geotoolkit.gui.javafx.crs.FXAxisView;
import org.geotoolkit.gui.javafx.crs.FXCRSButton;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.temporal.object.TemporalConstants;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXCoordinateBar extends GridPane {
    
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();
    
    private final FXMap map;
    private final PropertyChangeListener listener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            final String propertyName = evt.getPropertyName();
            
            if(AbstractCanvas2D.OBJECTIVE_CRS_KEY.equals(propertyName)){
                //update crs button
                crsButton.crsProperty().set((CoordinateReferenceSystem)evt.getNewValue());
            }else if(AbstractCanvas2D.ENVELOPE_KEY.equals(propertyName)){
                //range slider
                final Date[] range = map.getCanvas().getTemporalRange();
                if(range==null){
                    sliderview.rangeMinProperty().set(null);
                    sliderview.rangeMaxProperty().set(null);
                }else{
                    final boolean wasNull = sliderview.rangeMinProperty().get() == null;
                    final double min = range[0].getTime();
                    final double max = range[1]!=null ? range[1].getTime() : min;
                    sliderview.rangeMinProperty().set(min);
                    sliderview.rangeMaxProperty().set(max);
                    if(wasNull){
                        //zoom on selection
                        sliderview.moveTo((max+min/2.0));
                    }
                }
            }
            
            //update scale box
            Platform.runLater(() -> {
                cbox.valueProperty().removeListener(action);
                try {
                    final double scale = map.getCanvas().getGeographicScale();
                    cbox.setValue((long)scale);
                    cbox.valueProperty().addListener(action);
                } catch (TransformException ex) {
                    Loggers.JAVAFX.log(Level.WARNING, null, ex);
                }
            });
                     
        }
    };
    private final ChangeListener action = new ChangeListener() {

        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            if (map != null) {
                try {
                    map.getCanvas().setGeographicScale((Long)newValue);
                } catch (TransformException ex) {
                    Loggers.JAVAFX.log(Level.WARNING, null, ex);
                }
            }
        }
    };
    
    
    private final StatusBar statusBar = new StatusBar();
    private final ComboBox cbox = new ComboBox();
    private final ColorPicker colorPicker = new ColorPicker(Color.WHITE);
    private final FXCRSButton crsButton = new FXCRSButton();
    private final ToggleButton sliderButton = new ToggleButton(null, new ImageView(GeotkFX.ICON_SLIDERS));
    private final FXAxisView sliderview = new FXAxisView();
    
    public FXCoordinateBar(FXMap map) {
        this.map = map;
        
        colorPicker.setStyle("-fx-color-label-visible:false;");
        
        statusBar.setMaxWidth(Double.MAX_VALUE);
        add(statusBar, 0, 1);
        
        final ColumnConstraints col0 = new ColumnConstraints();
        col0.setHgrow(Priority.ALWAYS);
        final RowConstraints row0 = new RowConstraints();
        row0.setVgrow(Priority.ALWAYS);
        final RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.NEVER);
        getColumnConstraints().addAll(col0);
        getRowConstraints().addAll(row0,row1);
        
        sliderview.scaleProperty().set( (1.0/TemporalConstants.DAY_MS)*30 );
        sliderview.visibleProperty().bind(sliderButton.selectedProperty());
        sliderButton.setOnAction((ActionEvent event) -> {
            getChildren().remove(sliderview);
            if(sliderButton.isSelected()){
                add(sliderview, 0, 0, 1, 1);
            }
        });
        
        final ChangeListener rangeListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                try {
                    if(newValue==null){
                        map.getCanvas().setTemporalRange(null,null);
                    }else{
                        Number minValue = sliderview.rangeMinProperty().get();
                        Number maxValue = sliderview.rangeMaxProperty().get();
                        if(minValue!=null && maxValue!=null && minValue.doubleValue() > maxValue.doubleValue()){
                            //avoid an invalid range
                            maxValue = minValue;
                        }
                        map.getCanvas().setTemporalRange(
                                minValue!=null ? new Date(minValue.longValue()) : null, 
                                maxValue!=null ? new Date(maxValue.longValue()) : null);
                    }
                } catch (TransformException ex) {
                    Loggers.JAVAFX.log(Level.INFO, ex.getMessage(), ex);
                }
            }
        };
        
        sliderview.rangeMinProperty().addListener(rangeListener);
        sliderview.rangeMaxProperty().addListener(rangeListener);
        
        
        statusBar.getLeftItems().add(sliderButton);
        
        cbox.getItems().addAll(  1000l,
                                 5000l,
                                20000l,
                                50000l,
                               100000l,
                               500000l);
        cbox.setEditable(true);
        cbox.setConverter(new LongStringConverter());
        
        statusBar.getRightItems().add(cbox);
        statusBar.getRightItems().add(colorPicker);
        statusBar.getRightItems().add(crsButton);
        
        map.addEventHandler(MouseEvent.ANY, new myListener());
        
        if (this.map != null) {
            this.map.getCanvas().addPropertyChangeListener(listener);
        }        
        
        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                if (map != null) {
                    map.getCanvas().setBackgroundPainter(new SolidColorPainter(FXUtilities.toSwingColor(colorPicker.getValue())));
                    map.getCanvas().repaint();
                }     
            }
        });
        
        crsButton.crsProperty().setValue(map.getCanvas().getObjectiveCRS());
        crsButton.crsProperty().addListener((ObservableValue<? extends CoordinateReferenceSystem> observable, 
                CoordinateReferenceSystem oldValue, CoordinateReferenceSystem newValue) -> {
            try {
                if(newValue!=null){
                    map.getCanvas().setObjectiveCRS(newValue);
                }
            } catch (TransformException ex) {
                Loggers.JAVAFX.log(Level.INFO, ex.getMessage(), ex);
            }
        });
        
    }
    
    public void setCrsButtonVisible(boolean visible){
        if(statusBar.getRightItems().contains(crsButton)){
            statusBar.getRightItems().remove(crsButton);
        }else{
            statusBar.getRightItems().add(crsButton);
        }
    }
    
    public boolean isCrsButtonVisible(){
        return crsButton.isVisible();
    }

    /**
     * TODO change this, we should be able to control multiple crs axis at the same time.
     * @return temporal axis crs viewer
     */
    public FXAxisView getSliderview() {
        return sliderview;
    }
    
    /**
     * Set scale values displayed in the right corner combo box.
     * 
     * @param scales 
     */
    public void setScaleBoxValues(Long[] scales){
        cbox.getItems().setAll(Arrays.asList(scales));
    }
        
    private class myListener implements EventHandler<MouseEvent>{

        @Override
        public void handle(MouseEvent event) {
            
            final Point2D pt = new Point2D.Double(event.getX(), event.getY());
            Point2D coord = new DirectPosition2D();
            try {
                coord = map.getCanvas().getObjectiveToDisplay().inverseTransform(pt, coord);
            } catch (NoninvertibleTransformException ex) {
                statusBar.setText("");
                return;
            }

            final CoordinateReferenceSystem crs = map.getCanvas().getObjectiveCRS();

            final StringBuilder sb = new StringBuilder("  ");
            sb.append(crs.getCoordinateSystem().getAxis(0).getAbbreviation());
            sb.append(" : ");
            sb.append(NUMBER_FORMAT.format(coord.getX()));
            sb.append("   ");
            sb.append(crs.getCoordinateSystem().getAxis(1).getAbbreviation());
            sb.append(" : ");
            sb.append(NUMBER_FORMAT.format(coord.getY()));
            statusBar.setText(sb.toString());
        }

    }
    
}

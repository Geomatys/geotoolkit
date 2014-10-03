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
import java.util.logging.Level;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.util.converter.LongStringConverter;
import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.display2d.canvas.painter.SolidColorPainter;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.Loggers;
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
            cbox.valueProperty().removeListener(action);
            try {
                final double scale = map.getCanvas().getGeographicScale();
                cbox.setValue((long)scale);
                cbox.valueProperty().addListener(action);
            } catch (TransformException ex) {
                Loggers.JAVAFX.log(Level.WARNING, null, ex);
            }
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
    
    
    private final ToolBar barLeft = new ToolBar();
    private final ToolBar barCenter = new ToolBar();
    private final ToolBar barRight = new ToolBar();
    
    private final Label coordText = new Label("");
    private final ComboBox cbox = new ComboBox();
    private final ColorPicker colorPicker = new ColorPicker(Color.WHITE);

    public FXCoordinateBar(FXMap map) {
        this.map = map;
        barLeft.setMaxHeight(Double.MAX_VALUE);
        barCenter.setMaxHeight(Double.MAX_VALUE);
        barRight.setMaxHeight(Double.MAX_VALUE);
        
        add(barLeft, 0, 0);
        add(barCenter, 1, 0);
        add(barRight, 2, 0);
        
        
        final ColumnConstraints col0 = new ColumnConstraints();
        final ColumnConstraints col1 = new ColumnConstraints();
        final ColumnConstraints col2 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        final RowConstraints row0 = new RowConstraints();
        row0.setVgrow(Priority.ALWAYS);
        getColumnConstraints().addAll(col0,col1,col2);
        getRowConstraints().addAll(row0);
        
        barCenter.getItems().add(coordText);
        
        cbox.getItems().addAll(  1000l,
                                 5000l,
                                20000l,
                                50000l,
                               100000l,
                               500000l);
        cbox.setEditable(true);
        cbox.setConverter(new LongStringConverter());
        
        barRight.getItems().add(cbox);
        barRight.getItems().add(colorPicker);
        
        map.addEventHandler(MouseEvent.ANY, new myListener());
        
        if (this.map != null) {
            this.map.getCanvas().addPropertyChangeListener(listener);
        }        
        
        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                if (map != null) {
                    map.getCanvas().setBackgroundPainter(new SolidColorPainter(FXUtilities.toSwingColor(colorPicker.getValue())));
                }     
            }
        });
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
                coordText.setText("");
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
            coordText.setText(sb.toString());
        }

    }
    
}

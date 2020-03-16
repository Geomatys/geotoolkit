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
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.util.converter.LongStringConverter;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.display.canvas.AbstractCanvas2D;
import org.geotoolkit.display2d.canvas.painter.SolidColorPainter;
import org.geotoolkit.gui.javafx.crs.CRSButton;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

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

            if(AbstractCanvas2D.GRIDGEOMETRY_KEY.equals(propertyName)){
                //update crs button
                final GridGeometry grid = (GridGeometry) evt.getNewValue();
                crsButton.crsProperty().set(grid.getCoordinateReferenceSystem());
                //range slider
                final Date[] range = map.getCanvas().getTemporalRange();
                if(range==null){
                    //TODO
                }else{
                    //TODO
                }
            }

            //update scale box
            Platform.runLater(() -> {
                scaleCombo.valueProperty().removeListener(action);
                try {
                    final double scale = map.getCanvas().getGeographicScale();
                    scaleCombo.setValue((long)scale);
                    scaleCombo.valueProperty().addListener(action);
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


    private final BorderPane statusBar = new BorderPane();
    private final ComboBox scaleCombo = new ComboBox();
    private final ColorPicker colorPicker = new ColorPicker(Color.WHITE);
    private final CRSButton crsButton = new CRSButton();
    private final DatePicker datePicker = new DatePicker();
    private final TextField text = new TextField();

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

        datePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                try {
                    if (newValue != null) {
                        final LocalDate date = newValue;
                        final int year = date.get(ChronoField.YEAR);
                        final int day = date.get(ChronoField.DAY_OF_YEAR);
                        final Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(0);
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.DAY_OF_YEAR, day);
                        final Date dt = cal.getTime();
                        map.getCanvas().setTemporalRange(dt, dt);
                    } else {
                        map.getCanvas().setTemporalRange(null, null);
                    }
                } catch (TransformException ex) {
                    ex.printStackTrace();
                }
            }
        });

        statusBar.setLeft(new ToolBar(datePicker));
        text.setMaxWidth(Double.MAX_VALUE);
        text.setPrefWidth(450);
        text.setMinWidth(200);
        final ToolBar centerbar = new ToolBar(text);
        statusBar.setCenter(centerbar);

        scaleCombo.getItems().addAll(  1000l,
                                 5000l,
                                20000l,
                                50000l,
                               100000l,
                               500000l);
        scaleCombo.setEditable(true);
        scaleCombo.setConverter(new LongStringConverter());

        statusBar.setRight(new ToolBar(scaleCombo,colorPicker,crsButton));

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
            } catch (TransformException | FactoryException ex) {
                Loggers.JAVAFX.log(Level.INFO, ex.getMessage(), ex);
            }
        });

        // Set button tooltips
        datePicker.setTooltip(new Tooltip(GeotkFX.getString(FXCoordinateBar.class, "temporalTooltip")));
        scaleCombo.setTooltip(new Tooltip(GeotkFX.getString(FXCoordinateBar.class, "scaleTooltip")));
        colorPicker.setTooltip(new Tooltip(GeotkFX.getString(FXCoordinateBar.class, "bgColorTooltip")));
        crsButton.setTooltip(new Tooltip(GeotkFX.getString(FXCoordinateBar.class, "crsTooltip")));
    }

    /**
     * Set scale values displayed in the right corner combo box.
     *
     * @param scales predefined scale values
     */
    public void setScaleBoxValues(Long[] scales){
        scaleCombo.getItems().setAll(Arrays.asList(scales));
    }

    private class myListener implements EventHandler<MouseEvent>{

        @Override
        public void handle(MouseEvent event) {

            final Point2D pt = new Point2D.Double(event.getX(), event.getY());
            Point2D coord = new DirectPosition2D();
            try {
                coord = map.getCanvas().getObjectiveToDisplay().inverseTransform(pt, coord);
            } catch (NoninvertibleTransformException ex) {
                text.setText("");
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
            text.setText(sb.toString());
        }

    }

}

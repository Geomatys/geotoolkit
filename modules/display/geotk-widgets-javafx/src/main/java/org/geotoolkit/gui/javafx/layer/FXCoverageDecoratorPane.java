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
package org.geotoolkit.gui.javafx.layer;

import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.logging.Level;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.apache.sis.gui.crs.CRSButton;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.geotoolkit.coverage.amended.AmendedCoverageResource;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXCoverageDecoratorPane extends GridPane {

    private static final StringConverter CVT =new StringConverter<Double>() {
        private final DecimalFormat df = new DecimalFormat("#.#########");

        @Override
        public synchronized String toString(Double value) {
            if (value == null) {
                return "";
            }
            return df.format(value);
        }

        @Override
        public synchronized Double fromString(String value) {
            try {
                if (value == null) {
                    return null;
                }
                value = value.trim();
                if (value.length() < 1) {
                    return null;
                }
                return df.parse(value).doubleValue();
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        }
    };

    @FXML private GridPane uiTrsPane;
    @FXML private CheckBox uiCrs;
    @FXML private CheckBox uiGridToCrs;

    private final CRSButton crsButton = new CRSButton();
    private final Spinner<Double> uiScaleX = new Spinner<>(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0, 1.0));
    private final Spinner<Double> uiScaleY = new Spinner<>(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0, 1.0));
    private final Spinner<Double> uiShearX = new Spinner<>(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0, 1.0));
    private final Spinner<Double> uiShearY = new Spinner<>(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0, 1.0));
    private final Spinner<Double> uiTranslateX = new Spinner<>(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0, 1.0));
    private final Spinner<Double> uiTranslateY = new Spinner<>(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0, 1.0));

    private final AmendedCoverageResource decoratedRef;

    public FXCoverageDecoratorPane(AmendedCoverageResource ref) {
        GeotkFX.loadJRXML(this, FXCoverageDecoratorPane.class);
        this.decoratedRef = ref;

        add(crsButton, 0, 1, 2, 1);
        uiTrsPane.add(uiScaleX, 1, 0);
        uiTrsPane.add(uiScaleY, 1, 1);
        uiTrsPane.add(uiShearX, 3, 0);
        uiTrsPane.add(uiShearY, 3, 1);
        uiTrsPane.add(uiTranslateX, 5, 0);
        uiTrsPane.add(uiTranslateY, 5, 1);
        uiScaleX.setEditable(true);
        uiScaleY.setEditable(true);
        uiScaleX.setEditable(true);
        uiShearX.setEditable(true);
        uiShearY.setEditable(true);
        uiTranslateX.setEditable(true);
        uiTranslateY.setEditable(true);
        uiScaleX.getValueFactory().setConverter(CVT);
        uiScaleY.getValueFactory().setConverter(CVT);
        uiShearX.getValueFactory().setConverter(CVT);
        uiShearY.getValueFactory().setConverter(CVT);
        uiTranslateX.getValueFactory().setConverter(CVT);
        uiTranslateY.getValueFactory().setConverter(CVT);

        uiTrsPane.disableProperty().bind(uiGridToCrs.selectedProperty().not());
        crsButton.disableProperty().bind(uiCrs.selectedProperty().not());

        ChangeListener<Boolean> lst = (s, ov, nv) -> {
            if (nv) return;
            commitEditorText(uiScaleX);
            commitEditorText(uiScaleY);
            commitEditorText(uiShearX);
            commitEditorText(uiShearY);
            commitEditorText(uiTranslateX);
            commitEditorText(uiTranslateY);
        };

        uiScaleX.focusedProperty().addListener(lst);
        uiScaleY.focusedProperty().addListener(lst);
        uiShearX.focusedProperty().addListener(lst);
        uiShearY.focusedProperty().addListener(lst);
        uiTranslateX.focusedProperty().addListener(lst);
        uiTranslateY.focusedProperty().addListener(lst);

        updateFields();
    }

    private void updateFields(){
        CoordinateReferenceSystem crs = decoratedRef.getOverrideCRS();
        uiCrs.setSelected(crs!=null);
        if(crs==null){
            try {
                crs = decoratedRef.getGridGeometry(decoratedRef.getImageIndex()).getCoordinateReferenceSystem();
            } catch (CoverageStoreException ex) {
                Loggers.JAVAFX.log(Level.FINE, ex.getMessage(), ex);
            }
        }
        crsButton.crsProperty().set(crs);

        MathTransform overrideGridToCrs = decoratedRef.getOverrideGridToCrs();
        uiGridToCrs.setSelected(overrideGridToCrs!=null);
        if(overrideGridToCrs==null){
            try {
                overrideGridToCrs = decoratedRef.getGridGeometry(decoratedRef.getImageIndex()).getGridToCRS();
            } catch (CoverageStoreException ex) {
                Loggers.JAVAFX.log(Level.FINE, ex.getMessage(), ex);
            }
        }

        if(overrideGridToCrs instanceof AffineTransform){
            final AffineTransform aff = (AffineTransform) overrideGridToCrs;
            uiScaleX.getValueFactory().setValue(aff.getScaleX());
            uiScaleY.getValueFactory().setValue(aff.getScaleY());
            uiShearX.getValueFactory().setValue(aff.getShearX());
            uiShearY.getValueFactory().setValue(aff.getShearY());
            uiTranslateX.getValueFactory().setValue(aff.getTranslateX());
            uiTranslateY.getValueFactory().setValue(aff.getTranslateY());
        }else{
            uiScaleX.getValueFactory().setValue(1.0);
            uiScaleY.getValueFactory().setValue(1.0);
            uiShearX.getValueFactory().setValue(0.0);
            uiShearY.getValueFactory().setValue(0.0);
            uiTranslateX.getValueFactory().setValue(0.0);
            uiTranslateY.getValueFactory().setValue(0.0);
        }
    }


    @FXML
    void resetCrs(ActionEvent event) {
        decoratedRef.setOverrideCRS(null);
        CoordinateReferenceSystem crs = null;
        uiCrs.setSelected(false);
        try {
            crs = decoratedRef.getGridGeometry(decoratedRef.getImageIndex()).getCoordinateReferenceSystem();
        } catch (CoverageStoreException ex) {
            Loggers.JAVAFX.log(Level.FINE, ex.getMessage(), ex);
        }
        crsButton.crsProperty().set(crs);
    }

    @FXML
    void resetGridToCrs(ActionEvent event) {
        decoratedRef.setOverrideGridToCrs(null);
        MathTransform overrideGridToCrs = null;
        uiGridToCrs.setSelected(false);
        try {
            overrideGridToCrs = decoratedRef.getGridGeometry(decoratedRef.getImageIndex()).getGridToCRS();
        } catch (CoverageStoreException ex) {
            Loggers.JAVAFX.log(Level.FINE, ex.getMessage(), ex);
        }

        if(overrideGridToCrs instanceof AffineTransform){
            final AffineTransform aff = (AffineTransform) overrideGridToCrs;
            uiScaleX.getValueFactory().setValue(aff.getScaleX());
            uiScaleY.getValueFactory().setValue(aff.getScaleY());
            uiShearX.getValueFactory().setValue(aff.getShearX());
            uiShearY.getValueFactory().setValue(aff.getShearY());
            uiTranslateX.getValueFactory().setValue(aff.getTranslateX());
            uiTranslateY.getValueFactory().setValue(aff.getTranslateY());
        }else{
            uiScaleX.getValueFactory().setValue(1.0);
            uiScaleY.getValueFactory().setValue(1.0);
            uiShearX.getValueFactory().setValue(0.0);
            uiShearY.getValueFactory().setValue(0.0);
            uiTranslateX.getValueFactory().setValue(0.0);
            uiTranslateY.getValueFactory().setValue(0.0);
        }
    }

    @FXML
    void apply(ActionEvent event) {
        if(uiCrs.isSelected()){
            decoratedRef.setOverrideCRS(crsButton.crsProperty().get());
        }else{
            decoratedRef.setOverrideCRS(null);
        }

        if(uiGridToCrs.isSelected()){
            final AffineTransform2D gridToCrs = new AffineTransform2D(
                    uiScaleX.getValue(),
                    uiShearY.getValue(),
                    uiShearX.getValue(),
                    uiScaleY.getValue(),
                    uiTranslateX.getValue(),
                    uiTranslateY.getValue());

            decoratedRef.setOverrideGridToCrs(gridToCrs);
        }else{
            decoratedRef.setOverrideGridToCrs(null);
        }
    }


    private <T> void commitEditorText(Spinner<T> spinner) {
        if (!spinner.isEditable()) return;
        String text = spinner.getEditor().getText();
        SpinnerValueFactory<T> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            StringConverter<T> converter = valueFactory.getConverter();
            if (converter != null) {
                T value = converter.fromString(text);
                valueFactory.setValue(value);
            }
        }
    }


}

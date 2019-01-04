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
package org.geotoolkit.gui.javafx.style.dynamicrange;

import java.awt.image.RenderedImage;
import java.util.List;
import java.util.logging.Level;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.ext.dynamicrange.DynamicRangeSymbolizer;
import org.geotoolkit.gui.javafx.style.FXStyleElementController;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXDRChannel extends FXStyleElementController<DynamicRangeSymbolizer.DRChannel> {

    @FXML
    private ChoiceBox<String> uiBands;
    @FXML
    private ChoiceBox<String> uiCsComponent;
    @FXML
    private FXDRBound uiLower;
    @FXML
    private FXDRBound uiUpper;

    @Override
    public Class<DynamicRangeSymbolizer.DRChannel> getEditedClass() {
        return DynamicRangeSymbolizer.DRChannel.class;
    }

    public void lock(String csc){
        uiCsComponent.valueProperty().setValue(csc);
        uiCsComponent.setDisable(true);
    }

    @Override
    public DynamicRangeSymbolizer.DRChannel newValue() {
        return new DynamicRangeSymbolizer.DRChannel();
    }

    @Override
    public void initialize() {
        super.initialize();

        uiCsComponent.setItems(FXCollections.observableArrayList(
                DynamicRangeSymbolizer.DRChannel.BAND_RED,
                DynamicRangeSymbolizer.DRChannel.BAND_GREEN,
                DynamicRangeSymbolizer.DRChannel.BAND_BLUE,
                DynamicRangeSymbolizer.DRChannel.BAND_ALPHA
            ));
        valueProperty().set(new DynamicRangeSymbolizer.DRChannel());

        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            final DynamicRangeSymbolizer.DRChannel element = new DynamicRangeSymbolizer.DRChannel();
            element.setBand(uiBands.valueProperty().get());
            element.setColorSpaceComponent(uiCsComponent.valueProperty().get());
            element.setLower(uiLower.valueProperty().get());
            element.setUpper(uiUpper.valueProperty().get());
            value.set(element);
        };
        uiBands.valueProperty().addListener(changeListener);
        uiCsComponent.valueProperty().addListener(changeListener);
        uiLower.valueProperty().addListener(changeListener);
        uiUpper.valueProperty().addListener(changeListener);

    }

    @Override
    protected void updateEditor(DynamicRangeSymbolizer.DRChannel styleElement) {
        if(styleElement!=null){
            uiBands.valueProperty().setValue(styleElement.getBand());
            uiCsComponent.valueProperty().setValue(styleElement.getColorSpaceComponent());
            uiLower.valueProperty().setValue(styleElement.getLower());
            uiUpper.valueProperty().setValue(styleElement.getUpper());
        }
    }

    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiLower.setLayer(layer);
        uiUpper.setLayer(layer);


        if(layer instanceof CoverageMapLayer){
            final CoverageMapLayer cml = (CoverageMapLayer) layer;
            final GridCoverageResource ref = cml.getCoverageReference();
            GridCoverageReader reader = null;
            try {
                reader = ref.acquireReader();
                final List<GridSampleDimension> dims = reader.getSampleDimensions(ref.getImageIndex());

                final int nbdim;
                if(dims==null){
                    //read a very low resolution image to extract bands from it
                    final GridGeometry gg = reader.getGridGeometry(ref.getImageIndex());
                    final Envelope env = gg.getEnvelope();
                    final double[] res = gg.getResolution(false);
                    for(int i=0;i<res.length;i++){
                        res[i] = env.getSpan(i);
                    }
                    final GridCoverageReadParam params = new GridCoverageReadParam();
                    params.setEnvelope(env);
                    params.setResolution(res);

                    final GridCoverage2D cov = (GridCoverage2D) reader.read(ref.getImageIndex(), params);
                    final RenderedImage ri = cov.getRenderedImage();
                    nbdim = ri.getSampleModel().getNumBands();
                }else{
                    nbdim = dims.size();
                }

                final ObservableList<String> bvals = FXCollections.observableArrayList();
                bvals.add("none");
                for(int i=0;i<nbdim;i++){
                    bvals.add(""+i);
                }
                uiBands.setItems(bvals);

                ref.recycle(reader);
            } catch (CoverageStoreException ex) {
                Logging.getLogger("org.geotoolkit.gui.javafx.style.dynamicrange").log(Level.SEVERE, null, ex);
            } finally{
                if(reader!=null) ref.recycle(reader);
            }

        }

    }

    /**
     * Try to find best values for band and min/max.
     */
    public void fitToData(){
        final MapLayer cml = getLayer();
        if(cml instanceof CoverageMapLayer){
            final GridCoverageResource ref = ((CoverageMapLayer)cml).getCoverageReference();
            GridCoverageReader reader = null;
            try {
                reader = ref.acquireReader();
                List<GridSampleDimension> dims = reader.getSampleDimensions(ref.getImageIndex());

                final int nbdim;
                if(dims==null){
                    //read a very low resolution image to extract bands from it
                    final GridGeometry gg = reader.getGridGeometry(ref.getImageIndex());
                    final Envelope env = gg.getEnvelope();
                    final double[] res = gg.getResolution(false);
                    for(int i=0;i<res.length;i++){
                        res[i] = env.getSpan(i);
                    }
                    final GridCoverageReadParam params = new GridCoverageReadParam();
                    params.setEnvelope(env);
                    params.setResolution(res);

                    final GridCoverage2D cov = (GridCoverage2D) reader.read(ref.getImageIndex(), params);
                    final RenderedImage ri = cov.getRenderedImage();
                    nbdim = ri.getSampleModel().getNumBands();
                }else{
                    nbdim = dims.size();
                }


                //find best match band index
                int index = 0;
                switch (uiCsComponent.getValue()) {
                    case DynamicRangeSymbolizer.DRChannel.BAND_RED:   index = 0; break;
                    case DynamicRangeSymbolizer.DRChannel.BAND_GREEN: index = 1; break;
                    case DynamicRangeSymbolizer.DRChannel.BAND_BLUE:  index = 2; break;
                    case DynamicRangeSymbolizer.DRChannel.BAND_ALPHA: index = 3; break;
                }

                if(index>=nbdim) index = 0;

                uiBands.setValue(""+index);

                //extract band min/max
                double min = 0;
                double max = 255;
                if(dims!=null){
                    final GridSampleDimension sd = dims.get(index);
                    min = sd.getMinimumValue();
                    max = sd.getMaximumValue();
                }

                final DynamicRangeSymbolizer.DRBound boundMin = new DynamicRangeSymbolizer.DRBound();
                boundMin.setMode(DynamicRangeSymbolizer.DRBound.MODE_EXPRESSION);
                boundMin.setValue(GO2Utilities.FILTER_FACTORY.literal(min));
                valueProperty().get().setLower(boundMin);
                uiLower.valueProperty().setValue(boundMin);

                final DynamicRangeSymbolizer.DRBound boundMax = new DynamicRangeSymbolizer.DRBound();
                boundMax.setMode(DynamicRangeSymbolizer.DRBound.MODE_EXPRESSION);
                boundMax.setValue(GO2Utilities.FILTER_FACTORY.literal(max));
                valueProperty().get().setUpper(boundMax);
                uiUpper.valueProperty().setValue(boundMax);

            } catch (CoverageStoreException ex) {
                Logging.getLogger("org.geotoolkit.gui.javafx.style.dynamicrange").log(Level.SEVERE, null, ex);
            } finally{
                if(reader!=null) ref.recycle(reader);
            }
        }
    }

}

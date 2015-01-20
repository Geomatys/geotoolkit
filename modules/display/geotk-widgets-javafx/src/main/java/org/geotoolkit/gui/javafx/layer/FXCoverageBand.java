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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.metadata.DefaultSampleDimensionExt;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.content.SampleDimension;
import org.opengis.util.MemberName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXCoverageBand extends BorderPane {

    private static final NumberFormat NF = new DecimalFormat("0.###");

    @FXML private Label uiMax;
    @FXML private BarChart<String, Long> uiHisto;
    @FXML private AreaChart<String, Long> uiArea;
    @FXML private Label uiMean;
    @FXML private Label uiName;
    @FXML private Label uiStd;
    @FXML private Label uiMin;

    public FXCoverageBand() {
        GeotkFX.loadJRXML(this);
        uiHisto.setCache(false);

    }

    public void init(SampleDimension dim){

        final StringBuilder sb = new StringBuilder();
        final MemberName sequenceIdentifier = dim.getSequenceIdentifier();
        if(sequenceIdentifier!=null){
            sb.append(sequenceIdentifier).append(" : ");
        }
        for(Identifier id : dim.getNames()){
            sb.append(id.getCode()).append(' ');
        }
        uiName.setText(sb.toString());

        uiMin.setText(dim.getMinValue()==null ? "-" : dim.getMinValue().toString());
        uiMax.setText(dim.getMaxValue()==null ? "-" : dim.getMaxValue().toString());
        uiMean.setText(dim.getMeanValue()==null ? "-" : dim.getMeanValue().toString());
        uiStd.setText(dim.getStandardDeviation()==null ? "-" : dim.getStandardDeviation().toString());


        if(dim instanceof DefaultSampleDimensionExt){
            final DefaultSampleDimensionExt ext = (DefaultSampleDimensionExt) dim;
            final long[] vals = ext.getHistogram();

            final double step = (ext.getHistogramMax()-ext.getHistogramMin())/ vals.length;

            double v = ext.getHistogramMin();

            final ObservableList<Data<String,Long>> datas = FXCollections.observableArrayList();
            for(long l : vals){
                datas.add(new Data<>(NF.format(v), l));
                v += step;
            }
            final XYChart.Series<String,Long> serie = new XYChart.Series<>("", datas);

            final ObservableList<XYChart.Series<String, Long>> series = FXCollections.observableArrayList(serie);

            uiHisto.setData(series);
            uiHisto.setVisible(true);
            uiArea.setVisible(false);
        }


    }

    
}

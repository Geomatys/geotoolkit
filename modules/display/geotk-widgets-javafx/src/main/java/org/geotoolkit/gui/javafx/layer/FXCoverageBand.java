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

    private static final NumberFormat NF = new DecimalFormat("#0.000");

    @FXML private Label uiName;
    @FXML private Label uiMin;
    @FXML private Label uiMax;
    @FXML private Label uiMean;
    @FXML private Label uiStd;
    @FXML private Label uiUnit;
    @FXML private BarChart<String, Long> uiHisto;

    public FXCoverageBand() {
        GeotkFX.loadJRXML(this,FXCoverageBand.class);
        uiHisto.setCache(false);
        uiHisto.setBarGap(0);
        uiHisto.setCategoryGap(0);
        uiHisto.setVerticalGridLinesVisible(false);
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

        uiMin.setText(dim.getMinValue()==null ? "-" :  NF.format(dim.getMinValue()));
        uiMax.setText(dim.getMaxValue()==null ? "-" :  NF.format(dim.getMaxValue()));
        uiMean.setText(dim.getMeanValue()==null ? "-" : NF.format(dim.getMeanValue()));
        uiStd.setText(dim.getStandardDeviation()==null ? "-" :  NF.format(dim.getStandardDeviation()));
        uiUnit.setText(dim.getUnits()==null ? "-" : dim.getUnits().toString());

        if(dim instanceof DefaultSampleDimensionExt){
            final DefaultSampleDimensionExt ext = (DefaultSampleDimensionExt) dim;
            long[] vals = ext.getHistogram();

            double step = (ext.getHistogramMax()-ext.getHistogramMin())/ (vals.length-1);

            //NOTE : reduce number of values, javafx is slow and can't handle more then a few hundreds of values
            //NOTE : we lost a few values here if vals size is not % 2
            while(vals.length>200){
                final long[] nvals = new long[vals.length/2];
                for(int i=0;i<nvals.length;i++){
                    nvals[i] = vals[i*2] + vals[i*2+1];
                }
                step *= 2;
                vals = nvals;
            }

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
        }


    }


}

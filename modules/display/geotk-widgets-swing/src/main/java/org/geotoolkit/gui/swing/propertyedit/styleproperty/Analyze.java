/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009 Geomatys
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

package org.geotoolkit.gui.swing.propertyedit.styleproperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotools.data.DefaultQuery;
import org.geotools.feature.FeatureIterator;
import org.jfree.chart.JFreeChart;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.expression.PropertyName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Analyze extends AbstractTableModel{

    public static enum METHOD{
        EL("el"),
        QANTILE("qantile"),
        MANUAL("manual");

        private final String title;

        METHOD(String key){
            this.title = MessageBundle.getString(key);
        }

        public String getTitle(){
            return title;
        }

    };

    private FeatureMapLayer layer;
    private PropertyName classification;
    private PropertyName normalize;
    private int nbClasses = 0;
    private double[] values = null;
    private METHOD method = null;

    private boolean analyze = false;
    private double count = 0;
    private double minimum = 0;
    private double maximum = 0;
    private double sum = 0;
    private double mean = 0;
    private double median = 0;
    private double deviation = 0;

    public Analyze() {
    }

    private void reset(){
        analyze = false;
        count = 0;
        minimum = Double.POSITIVE_INFINITY;
        maximum = Double.NEGATIVE_INFINITY;
        sum = 0;
        mean = 0;
        median = 0;
        deviation = 0;
    }

    public void setLayer(FeatureMapLayer layer) {
        this.layer = layer;
        reset();
    }

    public FeatureMapLayer getLayer() {
        return layer;
    }

    public void setClassification(PropertyName classification) {
        this.classification = classification;
        reset();
    }

    public void setNormalize(PropertyName normalize) {
        this.normalize = normalize;
        reset();
    }

    public void setMethod(METHOD method) {
        this.method = method;
        reset();
    }

    public METHOD getMethod() {
        return method;
    }

    public void setNbClasses(int nbClasses) {
        this.nbClasses = nbClasses;
        reset();
    }

    public int getNbClasses() {
        return nbClasses;
    }

    private void analyze(){
        if(analyze) return;
        reset();


        //search the extreme values
        final DefaultQuery query = new DefaultQuery();

        if(normalize == null){
            query.setPropertyNames(new String[]{classification.getPropertyName()});
        }else{
            query.setPropertyNames(new String[]{classification.getPropertyName(),
                                                normalize.getPropertyName()});
        }

        FeatureIterator<SimpleFeature> features = null;
        try{
            features = layer.getFeatureSource().getFeatures(query).features();
            List<Double> values = new ArrayList<Double>();

            while(features.hasNext()){
                SimpleFeature sf = features.next();
                count++;

                Number classifValue = classification.evaluate(sf, Number.class);
                double value;

                if(normalize == null){
                    value = classifValue.doubleValue();
                }else{
                    Number normalizeValue = normalize.evaluate(sf, Number.class);
                    value = classifValue.doubleValue() / normalizeValue.doubleValue();
                }

                values.add(value);
                sum += value;

                if(value < minimum){
                    minimum = value;
                }
                if(value > maximum){
                    maximum = value;
                }

            }

            mean = (minimum+maximum) / 2;

            //find the median
            Double[] b = values.toArray(new Double[values.size()]);
            Arrays.sort(b);

            if (values.size() % 2 == 0) {
                median = (b[(b.length / 2) - 1] + b[b.length / 2]) / 2.0;
            } else {
                median = b[b.length / 2];
            }



        }catch(IOException ex){
            ex.printStackTrace();
        }finally{
            if(features != null){
                features.close();
            }
        }



        values = new double[nbClasses];

        analyze = true;
    }

    public JFreeChart getChart(){
        analyze();
        return null;
    }

    ////////////// TABLE MODEL /////////////////////////////////////////////////

    @Override
    public int getRowCount() {
        return nbClasses;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        analyze();
        return values[rowIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return method == METHOD.MANUAL;
    }

}

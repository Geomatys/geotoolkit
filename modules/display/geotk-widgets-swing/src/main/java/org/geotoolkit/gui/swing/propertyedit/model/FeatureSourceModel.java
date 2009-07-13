/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.propertyedit.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;

import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.Query;
import org.geotoolkit.feature.collection.FeatureIterator;

import org.geotoolkit.data.DefaultQuery;
import org.geotoolkit.data.DefaultTransaction;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

/**
 * Feature source model
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class FeatureSourceModel extends DefaultTableModel {

    private final ArrayList<PropertyDescriptor> columns = new ArrayList<PropertyDescriptor>();
    private final ArrayList<Feature> features = new ArrayList<Feature>();
    private MapLayer layer;
    private JXTable tab;
    private Query query = Query.ALL; 

    /** Creates a new instance of BasicTableModel
     * @param tab
     * @param layer 
     */
    public FeatureSourceModel(JXTable tab, FeatureMapLayer layer) {
        super();
        this.tab = tab;
        this.layer = layer;

        setQuery(layer.getQuery());
    }

    public void setQuery(Query candidateQuery) {
        query = removeGeometryAttributs(candidateQuery);
        
        columns.clear();
        features.clear();

        FeatureType ft = ((FeatureMapLayer)layer).getFeatureSource().getSchema();

        for(String name : query.getPropertyNames()){
            columns.add(ft.getDescriptor(name));
        }
        
        try {
            FeatureIterator<SimpleFeature> fi = (FeatureIterator<SimpleFeature>)  ((FeatureMapLayer)layer).getFeatureSource().getFeatures(query).features();            
            while (fi.hasNext()) {
                features.add(fi.next());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public Query removeGeometryAttributs(Query query){
        DefaultQuery simpleQuery = new DefaultQuery(query);

        FeatureType ft = ((FeatureMapLayer)layer).getFeatureSource().getSchema();

        String[] propNames = simpleQuery.getPropertyNames();

        List<String> props = new ArrayList<String>();
        if(propNames != null){
            for(String str : propNames){
                props.add(str);
            }
            for(PropertyDescriptor desc : ft.getDescriptors()){
                if((desc instanceof GeometryDescriptor)){
                    props.remove(desc.getName().toString());
                }
            }
        }else{
            for(PropertyDescriptor desc : ft.getDescriptors()){
                if(!(desc instanceof GeometryDescriptor)){
                    props.add(desc.getName().toString());
                }
            }
        }

        simpleQuery.setPropertyNames(props);
        return simpleQuery;
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return columns.get(column).getType().getBinding();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).getName().toString();
    }

    @Override
    public int getRowCount() {
        if(features != null){
            return features.size();
        }else{
            return 0;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public Feature getFeatureAt(int rowIndex){
        return features.get(rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return features.get(rowIndex).getProperty(columns.get(columnIndex).getName()).getValue();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        FeatureStore<SimpleFeatureType, SimpleFeature> store;
        if ( ((FeatureMapLayer)layer).getFeatureSource() instanceof FeatureStore) {

            store = (FeatureStore<SimpleFeatureType, SimpleFeature>)  ((FeatureMapLayer)layer).getFeatureSource();
            DefaultTransaction transaction = new DefaultTransaction("trans_maj");


            store.setTransaction(transaction);
            FilterFactory ff = FactoryFinder.getFilterFactory(null);
            Filter filter = ff.id(Collections.singleton(features.get(rowIndex).getIdentifier()));            
            FeatureType schema = store.getSchema();
            
            AttributeDescriptor NAME = (AttributeDescriptor) schema.getDescriptor(getColumnName(columnIndex));
                        
            try {
                store.modifyFeatures(NAME, aValue, filter);
                transaction.commit();
            } catch (IOException ex) {
                ex.printStackTrace();
                try {
                    transaction.rollback();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            setQuery(query);

        }
    }

}

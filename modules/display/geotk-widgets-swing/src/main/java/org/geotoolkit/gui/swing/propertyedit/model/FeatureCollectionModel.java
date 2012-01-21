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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.Converters;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

/**
 * Feature collection model
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class FeatureCollectionModel extends DefaultTableModel {

    private final ArrayList<PropertyDescriptor> columns = new ArrayList<PropertyDescriptor>();
    private final ArrayList<Feature> features = new ArrayList<Feature>();
    private final boolean selectIds;
    private FeatureCollection featureCollection = null;
    private MapLayer layer;
    private JXTable tab;
    private Query query = null;

    /** Creates a new instance of BasicTableModel
     * @param tab
     * @param layer 
     */
    public FeatureCollectionModel(final JXTable tab, final FeatureMapLayer layer, final boolean selectIds) {
        super();
        this.tab = tab;
        this.layer = layer;
        this.selectIds = selectIds;

        setQuery(layer.getQuery());
    }

    public void setQuery(final Query candidateQuery) {
        query = removeGeometryAttributs(candidateQuery);
        
        columns.clear();
        features.clear();

        try {
            featureCollection = ((FeatureMapLayer) layer).getCollection().subCollection(query);
        } catch (DataStoreException ex) {
            throw new DataStoreRuntimeException(ex);
        }
        final FeatureType ft = featureCollection.getFeatureType();

        for(PropertyDescriptor desc : ft.getDescriptors()){
            columns.add(desc);
        }

        FeatureIterator<SimpleFeature> fi = null;
        try {
            fi = (FeatureIterator<SimpleFeature>)  featureCollection.iterator();
            while (fi.hasNext()) {
                features.add(FeatureUtilities.deepCopy(fi.next()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally{
            if(fi != null){
                fi.close();
            }
        }

    }

    public Query removeGeometryAttributs(Query query){

        FeatureType ft = ((FeatureMapLayer)layer).getCollection().getFeatureType();

        Name[] propNames = query.getPropertyNames();

        List<Name> props = new ArrayList<Name>();
        if(propNames != null){
            for(Name str : propNames){
                props.add(str);
            }
            for(PropertyDescriptor desc : ft.getDescriptors()){
                if((desc instanceof GeometryDescriptor)){
                    props.remove(desc.getName());
                }
            }
        }else{
            for(PropertyDescriptor desc : ft.getDescriptors()){
                if(!(desc instanceof GeometryDescriptor)){
                    props.add(desc.getName());
                }
            }
        }

        final QueryBuilder builder = new QueryBuilder(query);
        builder.setProperties(props.toArray(new Name[props.size()]));
        if(!selectIds){
            builder.setHints(new Hints(HintsPending.FEATURE_HIDE_ID_PROPERTY, Boolean.TRUE));
        }
        builder.setHints(new Hints(HintsPending.FEATURE_DETACHED, Boolean.TRUE));
        query = builder.buildQuery();
        return query;
    }

    @Override
    public int getColumnCount() {
        return columns.size()+1; //for id column
    }

    @Override
    public Class getColumnClass(final int column) {
        if(column == 0) return String.class;
        return columns.get(column-1).getType().getBinding();
    }

    @Override
    public String getColumnName(final int column) {
        if(column == 0) return "id";
        return columns.get(column-1).getName().getLocalPart();
    }
    
    public PropertyDescriptor getColumnDesc(final int column) {
        if(column == 0) return null;
        return columns.get(column-1);
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
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return columnIndex != 0;
    }

    public Feature getFeatureAt(final int rowIndex){
        return features.get(rowIndex);
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if(columnIndex == 0) return features.get(rowIndex).getIdentifier().getID();
        return features.get(rowIndex).getProperty(columns.get(columnIndex-1).getName()).getValue();
    }

    @Override
    public void setValueAt(Object aValue, final int rowIndex, final int columnIndex) {
        if(columnIndex == 0) return;

        if (featureCollection.isWritable()) {
            final FilterFactory ff = FactoryFinder.getFilterFactory(null);
            final Filter filter = ff.id(Collections.singleton(features.get(rowIndex).getIdentifier()));
            final AttributeDescriptor NAME = (AttributeDescriptor) columns.get(columnIndex-1);

            aValue = Converters.convert(aValue, getColumnClass(columnIndex));
            
            try {
                featureCollection.update(filter, NAME, aValue);
            } catch (DataStoreException ex) {
                ex.printStackTrace();
            }

            setQuery(query);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

}

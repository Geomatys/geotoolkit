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
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;

import org.jdesktop.swingx.JXTable;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.version.Versioned;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;

import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

/**
 * Feature collection model
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
public class FeatureCollectionModel extends DefaultTableModel {

    private final ArrayList<AttributeType> columns = new ArrayList<>();
    private final ArrayList<Feature> features = new ArrayList<>();
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
            featureCollection = ((FeatureMapLayer) layer).getCollection().subset(query);
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
        final FeatureType ft = featureCollection.getType();

        for(PropertyType desc : ft.getProperties(true)){
            columns.add((AttributeType) desc);
        }

        FeatureIterator fi = null;
        try {
            fi = featureCollection.iterator();
            while (fi.hasNext()) {
                features.add(FeatureExt.deepCopy(fi.next()));
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

        FeatureType ft = ((FeatureMapLayer)layer).getCollection().getType();

        String[] propNames = query.getPropertyNames();

        List<String> props = new ArrayList<>();
        if(propNames != null){
            for(String str : propNames){
                props.add(str);
            }
            for(PropertyType desc : ft.getProperties(true)){
                if((AttributeConvention.isGeometryAttribute(desc))){
                    props.remove(desc.getName().toString());
                }
            }
        }else{
            for(PropertyType desc : ft.getProperties(true)){
                if(!(AttributeConvention.isGeometryAttribute(desc))){
                    props.add(desc.getName().toString());
                }
            }
        }

        final QueryBuilder builder = new QueryBuilder(query);
        builder.setProperties(props.toArray(new String[props.size()]));
        if(!selectIds){
            builder.setHints(new Hints(HintsPending.FEATURE_HIDE_ID_PROPERTY, Boolean.TRUE));
        }
        builder.setHints(new Hints(HintsPending.FEATURE_DETACHED, Boolean.TRUE));
        query = builder.buildQuery();
        return query;
    }

    @Override
    public int getColumnCount() {
        return columns.size()+2; //for id column + versioning
    }

    @Override
    public Class getColumnClass(final int column) {
        if(column == 0) return Versioned.class;
        if(column == 1) return String.class;
        return columns.get(column-2).getValueClass();
    }

    @Override
    public String getColumnName(final int column) {
        if(column == 0) return "";//versioning
        if(column == 1) return "id";
        return columns.get(column-2).getName().tip().toString();
    }

    public PropertyType getColumnDesc(final int column) {
        if(column == 0) return null;
        if(column == 1) return null;
        return columns.get(column-2);
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
        return columnIndex != 1;
    }

    public Feature getFeatureAt(final int rowIndex){
        return features.get(rowIndex);
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final Feature f = features.get(rowIndex);
        if(columnIndex == 0) return null;
        if(columnIndex == 1) return FeatureExt.getId(f);
        return f.getPropertyValue(columns.get(columnIndex-2).getName().toString());
    }

    @Override
    public void setValueAt(Object aValue, final int rowIndex, final int columnIndex) {
        if(columnIndex == 0) return;
        if(columnIndex == 1) return;

        if (featureCollection.isWritable()) {
            final FilterFactory ff = FactoryFinder.getFilterFactory(null);
            final Filter filter = ff.id(Collections.singleton(FeatureExt.getId(features.get(rowIndex))));
            final GenericName NAME = columns.get(columnIndex-2).getName();

            aValue = ObjectConverters.convert(aValue, getColumnClass(columnIndex));

            try {
                featureCollection.update(filter, Collections.singletonMap(NAME.toString(), aValue));
            } catch (DataStoreException ex) {
                ex.printStackTrace();
            }

            setQuery(query);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

}

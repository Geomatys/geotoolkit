/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.owc.gtkext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.owc.xml.OwcExtension;
import org.geotoolkit.owc.xml.v10.OfferingType;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.util.NamesExt;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class OwcDataStoreExtension extends OwcExtension {

    public static final String CODE = "http://www.geotoolkit.org/owc";
    //this parameter is part of factory parameter descriptors
    private static final String KEY_STOREFACTORY = "identifier";
    private static final String KEY_DATANAME = "dataName";

    public OwcDataStoreExtension() {
        super(CODE, 0);
    }

    @Override
    public boolean canHandle(MapLayer layer) {
        return getParams(layer) != null && getStoreFactoryName(layer) != null;
    }

    @Override
    public MapLayer createLayer(OfferingType offering) throws DataStoreException {
        final List<Object> fields = offering.getOperationOrContentOrStyleSet();

        //rebuild parameters map
        String factoryName = null;
        String typeName = null;
        final Map params = new HashMap();
        for(Object o : fields){
            if(o instanceof JAXBElement){
                o = ((JAXBElement)o).getValue();
            }

            if(o instanceof ParameterType){
                final ParameterType param = (ParameterType) o;
                final String key = param.getKey();
                final Class valClass;
                try {
                    valClass = Class.forName(param.getType());
                } catch (ClassNotFoundException ex) {
                    throw new DataStoreException(ex.getMessage(),ex);
                }
                Object value = param.getValue();
                value = ObjectConverters.convert(value, valClass);

                if(KEY_STOREFACTORY.equalsIgnoreCase(key)){
                    factoryName = (String)value;
                    params.put(key, factoryName);
                }else if(KEY_DATANAME.equalsIgnoreCase(key)){
                    typeName = (String)value;
                }else{
                    params.put(key, value);
                }
            }
        }

        final DataStoreFactory ff = DataStores.getFactoryById(factoryName);
        if(ff!=null){
            final DataStore store = DataStores.open(ff,params);
            if(store instanceof FeatureStore){
                final Session session = ((FeatureStore)store).createSession(true);
                final FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(NamesExt.valueOf(typeName)));
                final MapLayer layer = MapBuilder.createFeatureLayer(col);
                return layer;
            }else if(store instanceof CoverageStore){
                final Resource covref = ((CoverageStore)store).findResource(NamesExt.valueOf(typeName).toString());
                final MapLayer layer = MapBuilder.createCoverageLayer(covref);
                return layer;
            }
        }

        //unknown factory, may no be in the classpath
        return MapBuilder.createEmptyMapLayer();
    }

    @Override
    public OfferingType createOffering(MapLayer mapLayer) {
        final OfferingType offering = new OfferingType();
        offering.setCode(getCode());

        //write the type name
        final List<Object> fieldList = offering.getOperationOrContentOrStyleSet();
        final String typeName = getTypeName(mapLayer);
        if(typeName!=null){
            fieldList.add(new ParameterType(KEY_DATANAME,String.class.getName(),typeName));
        }

        //write store creation parameters
        final Parameters params = Parameters.castOrWrap(getParams(mapLayer));

        final ParameterDescriptorGroup desc = params.getDescriptor();
        for(GeneralParameterDescriptor pdesc : desc.descriptors()){
            if (pdesc instanceof ParameterDescriptor) {
                final Object value = params.getValue((ParameterDescriptor) pdesc);
                if (value!=null) {
                    fieldList.add(new ParameterType(
                            pdesc.getName().getCode(),
                            ((ParameterDescriptor)pdesc).getValueClass().getName(),
                            String.valueOf(value)));
                }
            }
        }


        return offering;
    }

    private static String getStoreFactoryName(MapLayer layer){
        if(layer instanceof FeatureMapLayer){
            final FeatureMapLayer fml = (FeatureMapLayer) layer;
            final Session session = ((FeatureCollection)fml.getResource()).getSession();
            if(session!=null){
                final FeatureStore store = session.getFeatureStore();
                if(store!=null){
                    final DataStoreFactory factory = store.getProvider();
                    return factory.getOpenParameters().getName().getCode();
                }
            }
        }else if(layer instanceof CoverageMapLayer){
            final CoverageMapLayer cml = (CoverageMapLayer) layer;
            final GridCoverageResource covref = cml.getResource();
            if (covref instanceof StoreResource) {
                final DataStore store = ((StoreResource) covref).getOriginator();
                if (store != null) {
                    final DataStoreProvider factory = store.getProvider();
                    return factory.getOpenParameters().getName().getCode();
                }
            }
        }
        return null;
    }

    private static ParameterValueGroup getParams(MapLayer layer){
        if(layer instanceof FeatureMapLayer){
            final FeatureMapLayer fml = (FeatureMapLayer) layer;
            final Session session = ((FeatureCollection)fml.getResource()).getSession();
            if(session!=null){
                final FeatureStore store = session.getFeatureStore();
                if(store!=null){
                    return store.getOpenParameters();
                }
            }
        }else if(layer instanceof CoverageMapLayer){
            final CoverageMapLayer cml = (CoverageMapLayer) layer;
            final GridCoverageResource covref = cml.getResource();
            if (covref instanceof StoreResource) {
                final DataStore store = ((StoreResource) covref).getOriginator();
                if (store != null) {
                    return store.getOpenParameters();
                }
            }
        }
        return null;
    }

    private static String getTypeName(MapLayer layer){
        if(layer instanceof FeatureMapLayer){
            final FeatureMapLayer fml = (FeatureMapLayer) layer;
            try {
                return fml.getResource().getType().getName().toString();
            } catch (DataStoreException ex) {
                return null;
            }
        }else if(layer instanceof CoverageMapLayer){
            final CoverageMapLayer cml = (CoverageMapLayer) layer;
            final GridCoverageResource covref = cml.getResource();
            try {
                return covref.getIdentifier().toString();
            } catch (DataStoreException ex) {
                return null;
            }
        }
        return null;
    }

}

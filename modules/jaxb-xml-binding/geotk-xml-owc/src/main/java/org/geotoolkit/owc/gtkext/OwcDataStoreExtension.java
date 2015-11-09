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
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.storage.coverage.CoverageStoreFactory;
import org.geotoolkit.storage.coverage.CoverageStoreFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.Selector;
import org.geotoolkit.data.query.Source;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.owc.xml.OwcExtension;
import org.geotoolkit.owc.xml.v10.OfferingType;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

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
        
        //search feature store factories
        final FeatureStoreFactory ff = FeatureStoreFinder.getFactoryById(factoryName);
        if(ff!=null){
            final FeatureStore store = ff.open(params);
            final Session session = store.createSession(true);
            final FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(NamesExt.valueOf(typeName)));
            final FeatureMapLayer layer = MapBuilder.createFeatureLayer(col);
            return layer;
        }
        //search coverage store factories
        final CoverageStoreFactory cf = CoverageStoreFinder.getFactoryById(factoryName);
        if(cf!=null){
            final CoverageStore store = cf.open(params);
            final CoverageReference covref = store.getCoverageReference(NamesExt.valueOf(typeName));
            final CoverageMapLayer layer = MapBuilder.createCoverageLayer(covref);
            return layer;
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
        final GenericName typeName = getTypeName(mapLayer);
        if(typeName!=null){
            fieldList.add(new ParameterType(KEY_DATANAME,String.class.getName(),NamesExt.toExpandedString(typeName)));
        }
                       
        //write store creation parameters
        final ParameterValueGroup params = getParams(mapLayer);
        
        final ParameterDescriptorGroup desc = params.getDescriptor();
        for(GeneralParameterDescriptor pdesc : desc.descriptors()){
            final GeneralParameterValue param = ParametersExt.getParameter(params, pdesc.getName().getCode());
            if(param instanceof ParameterValue){
                final ParameterDescriptor pvdesc = (ParameterDescriptor) pdesc;
                final Object value = ((ParameterValue)param).getValue();
                if(value!=null){
                    fieldList.add(new ParameterType(
                            pdesc.getName().getCode(), 
                            pvdesc.getValueClass().getName(),
                            String.valueOf(value)));
                }
            }
        }
        
        
        return offering;
    }
    
    private static String getStoreFactoryName(MapLayer layer){
        if(layer instanceof FeatureMapLayer){
            final FeatureMapLayer fml = (FeatureMapLayer) layer;
            final Source source = fml.getCollection().getSource();
            if(source instanceof Selector){
                final Selector selector = (Selector)source;
                final Session session = selector.getSession();
                if(session!=null){
                    final FeatureStore store = session.getFeatureStore();
                    if(store!=null){
                        final FeatureStoreFactory factory = store.getFactory();
                        final InternationalString id = factory.getIdentification().getCitation().getTitle();
                        return id.toString();
                    }
                }
            }
        }else if(layer instanceof CoverageMapLayer){
            final CoverageMapLayer cml = (CoverageMapLayer) layer;
            final CoverageReference covref = cml.getCoverageReference();
            final CoverageStore store = covref.getStore();
            if(store!=null){
                final CoverageStoreFactory factory = store.getFactory();
                final InternationalString id = factory.getIdentification().getCitation().getTitle();
                return id.toString();
            }
        }
        return null;
    }
    
    private static ParameterValueGroup getParams(MapLayer layer){
        if(layer instanceof FeatureMapLayer){
            final FeatureMapLayer fml = (FeatureMapLayer) layer;
            final Source source = fml.getCollection().getSource();
            if(source instanceof Selector){
                final Selector selector = (Selector)source;
                final Session session = selector.getSession();
                if(session!=null){
                    final FeatureStore store = session.getFeatureStore();
                    if(store!=null){
                        return store.getConfiguration();
                    }
                }
            }
        }else if(layer instanceof CoverageMapLayer){
            final CoverageMapLayer cml = (CoverageMapLayer) layer;
            final CoverageReference covref = cml.getCoverageReference();
            final CoverageStore store = covref.getStore();
            if(store!=null){
                return store.getConfiguration();
            }
        }
        return null;
    }
    
    private static GenericName getTypeName(MapLayer layer){
        if(layer instanceof FeatureMapLayer){
            final FeatureMapLayer fml = (FeatureMapLayer) layer;
            final Source source = fml.getCollection().getSource();
            if(source instanceof Selector){
                final Selector selector = (Selector)source;
                return selector.getFeatureTypeName();
            }
        }else if(layer instanceof CoverageMapLayer){
            final CoverageMapLayer cml = (CoverageMapLayer) layer;
            final CoverageReference covref = cml.getCoverageReference();
            return covref.getName();
        }
        return null;
    }
    
}

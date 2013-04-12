/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.s57;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.s57.model.FeatureRecord;
import org.geotoolkit.data.s57.model.S57ModelObject;
import org.geotoolkit.data.s57.model.S57ModelObjectReader;
import org.geotoolkit.data.s57.model.VectorRecord;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.util.Converters;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class S57FeatureReader implements FeatureReader{

    //searched type
    private final FeatureType type;
    private final Map<Integer,PropertyDescriptor> properties = new HashMap<Integer, PropertyDescriptor>();
    private final int s57TypeCode;
    // S-57 objects
    private final S57ModelObjectReader mreader;
    private final Map<Long,S57ModelObject> spatials = new HashMap<Long, S57ModelObject>();
    
    private Feature feature;

    public S57FeatureReader(FeatureType type, int s57typeCode, S57ModelObjectReader mreader) {
        this.type = type;
        this.s57TypeCode = s57typeCode;
        this.mreader = mreader;
        
        for(PropertyDescriptor desc :type.getDescriptors()){
            Integer code = (Integer) desc.getUserData().get(S57FeatureStore.S57TYPECODE);
            properties.put(code, desc);
        }
        
    }
    
    @Override
    public FeatureType getFeatureType() {
        return type;
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        findNext();
        if(feature == null){
            throw new FeatureStoreRuntimeException("No more features");
        }
        Feature f = feature;
        feature = null;
        return f;
    }

    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        findNext();
        return feature != null;
    }

    private void findNext() throws FeatureStoreRuntimeException {
        if(feature != null) return;
        
        try{
            while(feature==null && mreader.hasNext()){
                final S57ModelObject modelObj = mreader.next();
                if(modelObj instanceof VectorRecord){
                    final VectorRecord rec = (VectorRecord) modelObj;
                    spatials.put(rec.id, rec);
                }else if(modelObj instanceof FeatureRecord){
                    final FeatureRecord rec = (FeatureRecord) modelObj;
                    //only the given type
                    if(rec.code != s57TypeCode) continue;
                    feature = toFeature(rec);
                }
            }
        }catch(IOException ex){
            throw new FeatureStoreRuntimeException(ex.getMessage(), ex);
        }
        
    }
    
    private Feature toFeature(FeatureRecord record){
        final Feature f =  FeatureUtilities.defaultFeature(type, String.valueOf(record.id));
        
        //read attributes
        for(FeatureRecord.Attribute att : record.attributes){
            final PropertyDescriptor desc = properties.get(att.code);
            final Object val = Converters.convert(att.value, desc.getType().getBinding());
            f.getProperty(desc.getName().getLocalPart()).setValue(val);
        }
        for(FeatureRecord.NationalAttribute att : record.nattributes){
            final PropertyDescriptor desc = properties.get(att.code);
            final Object val = Converters.convert(att.value, desc.getType().getBinding());
            f.getProperty(desc.getName().getLocalPart()).setValue(val);
        }
        //rebuild geometry
        //record.spatialPointers.get(0).
        
        return f;
    }
    
    @Override
    public void close() throws FeatureStoreRuntimeException {
        try {
            mreader.dispose();
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public void remove() {
        throw new FeatureStoreRuntimeException("writing not supported");
    }
    
}

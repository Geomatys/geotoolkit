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
package org.geotoolkit.data.s57.annexe;

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.data.s57.S57FeatureStore;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Give access to all S-57 feature and property types.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class S57TypeBank {

    private static final URL PATH_FEATURE_TYPE = S57TypeBank.class.getResource("/org/geotoolkit/s57/S57FeatureType.properties");
    private static final URL PATH_PROPERTY_TYPE = S57TypeBank.class.getResource("/org/geotoolkit/s57/S57PropertyType.properties");
    private static final Properties FEATURE_TYPES = new Properties();
    private static final Properties PROPERTY_TYPES = new Properties();
    
    private static Map<String,String> FT_ACC_KEY = new HashMap<String, String>();
    private static Map<Integer,String> FT_CODE_KEY = new HashMap<Integer,String>();
    private static Map<String,String> PT_ACC_KEY = new HashMap<String, String>();
    private static Map<Integer,String> PT_CODE_KEY = new HashMap<Integer,String>();
    
    static {
        InputStream stream = null;
        try {
            stream = PATH_FEATURE_TYPE.openStream();
            FEATURE_TYPES.load(stream);
            for(Object key : FEATURE_TYPES.keySet()){
                final String str = key.toString();
                final Entry<Integer,String> entry = splitKey(str);
                FT_ACC_KEY.put(entry.getValue(), str);
                FT_CODE_KEY.put(entry.getKey(), str);
            }
        } catch (IOException ex) {
            Logger.getLogger(S57TypeBank.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
        } finally {
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException ex) {
                    Logger.getLogger(S57TypeBank.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
        try {
            stream = PATH_PROPERTY_TYPE.openStream();
            PROPERTY_TYPES.load(stream);
            for(Object key : PROPERTY_TYPES.keySet()){
                final String str = key.toString();
                final Entry<Integer,String> entry = splitKey(str);
                PT_ACC_KEY.put(entry.getValue(), str);
                PT_CODE_KEY.put(entry.getKey(), str);
            }
        } catch (IOException ex) {
            Logger.getLogger(S57TypeBank.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
        } finally {
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException ex) {
                    Logger.getLogger(S57TypeBank.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
    }
    
    private S57TypeBank() {        
    }
    
    public static Set<String> getFeatureTypeNames(){
        return FT_ACC_KEY.keySet();
    }
    
    public static int getFeatureTypeCode(String name) throws DataStoreException{
        name = name.toUpperCase();
        String key = FT_ACC_KEY.get(name);
        if(key == null) throw new DataStoreException("No feature type for name : "+ name);
        return splitKey(key).getKey();
    }
    
    public static String getFeatureTypeName(int code) throws DataStoreException{
        String key = FT_CODE_KEY.get(code);
        if(key == null) throw new DataStoreException("No feature type for code : "+ code);
        return splitKey(key).getValue();
    }
    
    public static int getPropertyTypeCode(String name) throws DataStoreException{
        name = name.toUpperCase();
        String key = PT_ACC_KEY.get(name);
        if(key == null) throw new DataStoreException("No property type for name : "+ name);
        return splitKey(key).getKey();
    }
    
    public static String getPropertyTypeName(int code) throws DataStoreException{
        String key = PT_CODE_KEY.get(code);
        if(key == null) throw new DataStoreException("No property type for code : "+ code);
        return splitKey(key).getValue();
    }
    
    public static FeatureType getFeatureType(String name, CoordinateReferenceSystem crs) throws DataStoreException{
        String key = FT_ACC_KEY.get(name);
        if(key == null) throw new DataStoreException("No feature type for name : "+ name);
        return getFeatureTypeByKey(key,crs);
    }
    
    public static FeatureType getFeatureType(int code, CoordinateReferenceSystem crs) throws DataStoreException{
        String key = FT_CODE_KEY.get(code);
        if(key == null) throw new DataStoreException("No feature type for code : "+ code);
        return getFeatureTypeByKey(key,crs);
    }
    
    private static FeatureType getFeatureTypeByKey(String key, CoordinateReferenceSystem crs) throws DataStoreException{
        ArgumentChecks.ensureNonNull("crs", crs);
        
        final String values = FEATURE_TYPES.get(key).toString();
        Entry<Integer,String> entry = splitKey(key);
        final S57FeatureType sft = new S57FeatureType();
        sft.code = entry.getKey();
        sft.acronym = entry.getValue();
        sft.fromFormattedString(values);
        
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(sft.acronym);
        //add a geometry type
        ftb.add("spatial", Geometry.class, crs);
        
        final List<String> allAtts = new ArrayList<String>();
        allAtts.addAll(sft.attA);
        allAtts.addAll(sft.attB);
        allAtts.addAll(sft.attC);

        for(String att : allAtts){
            final AttributeDescriptor attDesc = getAttributeDescriptor(att);
            ftb.add(attDesc);
        }

        final FeatureType ft = ftb.buildFeatureType();   
        // do we need a cache here ?
        return ft;
    }
    
    public static AttributeDescriptor getAttributeDescriptor(final String code) throws DataStoreException{
        final String key = PT_ACC_KEY.get(code);
        if(key == null) throw new DataStoreException("No property type for code : "+ code);
        return getAttributeDescriptorByKey(key);
    }
    
    public static AttributeDescriptor getAttributeDescriptor(final int code) throws DataStoreException{
        final String key = PT_CODE_KEY.get(code);
        if(key == null) throw new DataStoreException("No property type for code : "+ code);
        return getAttributeDescriptorByKey(key);
    }
    
    public static AttributeDescriptor getAttributeDescriptorByKey(final String propertyKey){
        final Entry<Integer,String> entry = splitKey(propertyKey);
        
        final String pvalues = PROPERTY_TYPES.getProperty(propertyKey);
        final S57PropertyType pt = new S57PropertyType();
        pt.code = entry.getKey();
        pt.acronym = entry.getValue();
        pt.fromFormattedString(pvalues);

        Class binding;
        if("E".equalsIgnoreCase(pt.type)){
            //enumeration type
            binding = String.class;
        }else if("L".equalsIgnoreCase(pt.type)){
            //enumaration list
            binding = String.class;
        }else if("F".equalsIgnoreCase(pt.type)){
            //float
            binding = Double.class;
        }else if("I".equalsIgnoreCase(pt.type)){
            //integer
            binding = Integer.class;
        }else if("A".equalsIgnoreCase(pt.type)){
            //code string
            binding = String.class;
        }else if("S".equalsIgnoreCase(pt.type)){
            // free text
            binding = String.class;
        }else{
            throw new RuntimeException("unknowned property type : "+pt.type);
        }
        
        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        atb.setName(pt.acronym);
        atb.setBinding(binding);        
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        adb.setName(pt.acronym);
        adb.setMinOccurs(1);
        adb.setMaxOccurs(1);
        adb.setNillable(true);
        adb.setType(atb.buildType());
        
        final AttributeDescriptor attDesc = adb.buildDescriptor();
        attDesc.getUserData().put(S57FeatureStore.S57TYPECODE, pt.code);
        return attDesc;
    }
    
    /**
     * Split the key which is composed of 'Integer:String'
     * @param key
     * @return Entry
     */
    private static Entry<Integer,String> splitKey(final String key){
        final int index = key.indexOf('.');
        return new AbstractMap.SimpleImmutableEntry<Integer, String>(
                Integer.valueOf(key.substring(0,index)), 
                key.substring(index+1));
    }
    
}

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
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.data.s57.S57Constants;
import org.geotoolkit.data.s57.TypeBank;
import org.geotoolkit.data.s57.TypeBanks;
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
public class S57TypeBank implements TypeBank{

    private final Properties FEATURE_TYPES = new Properties();
    private final Properties PROPERTY_TYPES = new Properties();
    private final Map<String,String> FT_ACC_KEY = new HashMap<>();
    private final Map<Integer,String> FT_CODE_KEY = new HashMap<>();
    private final Map<String,String> PT_ACC_KEY = new HashMap<>();
    private final Map<Integer,String> PT_CODE_KEY = new HashMap<>();

    public S57TypeBank() {
        this(S57TypeBank.class.getResource("/org/geotoolkit/s57/S57FeatureType.properties"),
             S57TypeBank.class.getResource("/org/geotoolkit/s57/S57PropertyType.properties"));
    }

    @Override
    public String getSpecification() {
        return "S-57 : Base";
    }

    public S57TypeBank(URL featureTypeFile, URL propertyTypeFile) {
        InputStream stream = null;
        try {
            stream = featureTypeFile.openStream();
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
            stream = propertyTypeFile.openStream();
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

    @Override
    public Set<String> getFeatureTypeNames(){
        return FT_ACC_KEY.keySet();
    }

    @Override
    public Set<String> getPropertyTypeNames(){
        return PT_ACC_KEY.keySet();
    }

    @Override
    public int getFeatureTypeCode(String name) throws DataStoreException{
        String key = FT_ACC_KEY.get(name);
        if(key == null) throw new DataStoreException("No feature type for name : "+ name);
        return splitKey(key).getKey();
    }

    @Override
    public String getFeatureTypeName(int code) throws DataStoreException{
        String key = FT_CODE_KEY.get(code);
        if(key == null) throw new DataStoreException("No feature type for code : "+ code);
        return splitKey(key).getValue();
    }

    @Override
    public int getPropertyTypeCode(String name) throws DataStoreException{
        String key = PT_ACC_KEY.get(name);
        if(key == null) throw new DataStoreException("No property type for name : "+ name);
        return splitKey(key).getKey();
    }

    @Override
    public String getPropertyTypeName(int code) throws DataStoreException{
        String key = PT_CODE_KEY.get(code);
        if(key == null) throw new DataStoreException("No property type for code : "+ code);
        return splitKey(key).getValue();
    }

    @Override
    public FeatureType getFeatureType(String name, CoordinateReferenceSystem crs) throws DataStoreException{
        String key = FT_ACC_KEY.get(name);
        if(key == null) throw new DataStoreException("No feature type for name : "+ name);
        return getFeatureTypeByKey(key,crs);
    }

    @Override
    public FeatureType getFeatureType(int code, CoordinateReferenceSystem crs) throws DataStoreException{
        String key = FT_CODE_KEY.get(code);
        if(key == null) throw new DataStoreException("No feature type for code : "+ code);
        return getFeatureTypeByKey(key,crs);
    }

    private FeatureType getFeatureTypeByKey(String key, CoordinateReferenceSystem crs) throws DataStoreException{
        ArgumentChecks.ensureNonNull("crs", crs);

        final String values = FEATURE_TYPES.get(key).toString();
        Entry<Integer,String> entry = splitKey(key);
        final S57FeatureType sft = new S57FeatureType();
        sft.code = entry.getKey();
        sft.acronym = entry.getValue();
        sft.fromFormattedString(values);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setSuperType(S57Constants.ABSTRACT_S57FEATURETYPE);
        ftb.setName(sft.acronym);
        ftb.setDescription(new SimpleInternationalString(sft.fullName+".  "+sft.description));
        //add a geometry type
        ftb.add("spatial", Geometry.class, crs);

        final List<String> allAtts = new ArrayList<>();
        allAtts.addAll(sft.attA);
        allAtts.addAll(sft.attB);
        allAtts.addAll(sft.attC);

        for(String att : allAtts){
            final AttributeDescriptor attDesc = TypeBanks.getAttributeDescriptor(att);
            ftb.add(attDesc);
        }

        final FeatureType ft = ftb.buildFeatureType();
        // do we need a cache here ?
        return ft;
    }

    @Override
    public AttributeDescriptor getAttributeDescriptor(final String code) throws DataStoreException{
        final String key = PT_ACC_KEY.get(code);
        if(key == null) throw new DataStoreException("No property type for code : "+ code);
        return getAttributeDescriptorByKey(key);
    }

    @Override
    public AttributeDescriptor getAttributeDescriptor(final int code) throws DataStoreException{
        final String key = PT_CODE_KEY.get(code);
        if(key == null) throw new DataStoreException("No property type for code : "+ code);
        return getAttributeDescriptorByKey(key);
    }

    public AttributeDescriptor getAttributeDescriptorByKey(final String propertyKey) throws DataStoreException{
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
            binding = String[].class;
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
            throw new DataStoreException("unknowned property type : "+pt);
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
        return new AbstractMap.SimpleImmutableEntry<>(
                Integer.valueOf(key.substring(0,index)),
                key.substring(index+1));
    }

}

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
package org.geotoolkit.data.aml;

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.s57.S57Constants;
import org.geotoolkit.data.s57.S57FeatureStore;
import org.geotoolkit.data.s57.TypeBank;
import org.geotoolkit.data.s57.TypeBanks;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.util.FileUtilities;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class AMLTypeBank implements TypeBank{

    private static final Map<Class,String> PATHS = new HashMap<>();
    static {
        PATHS.put(AMLAttribute.class, "/org/geotoolkit/aml/AMLAttributes.csv");
        PATHS.put(AMLEnumeration.class, "/org/geotoolkit/aml/AMLEnumerations.csv");
        PATHS.put(AMLFeatureCatalog.class, "/org/geotoolkit/aml/AMLFeatureCatalogue.csv");
        PATHS.put(AMLFeatureType.class, "/org/geotoolkit/aml/AMLFeatureTypes.csv");
        PATHS.put(AMLTheme.class, "/org/geotoolkit/aml/AMLThemes.csv");
        PATHS.put(AMLUnitOfMeasure.class, "/org/geotoolkit/aml/AMLUnitsOfMeasure.csv");
    }

    private static Map<String,AMLAttribute> PT_ACC_KEY = new HashMap<>();
    private static Map<Integer,AMLAttribute> PT_CODE_KEY = new HashMap<>();

    public AMLTypeBank(){}

    private synchronized static Map<Integer,AMLAttribute> getAttributeByCodeMap(){
        getAttributeByAccMap();
        return PT_CODE_KEY;
    }

    private synchronized static Map<String,AMLAttribute> getAttributeByAccMap(){
        if(!PT_ACC_KEY.isEmpty()) return PT_ACC_KEY;

        final List<AMLAttribute> attributes = getAMLObjects(AMLAttribute.class);
        for(AMLAttribute att : attributes){
            PT_CODE_KEY.put(att.Attribute_Code, att);
            PT_ACC_KEY.put(att.Attribute_Acronym, att);
        }

        return PT_ACC_KEY;
    }

    /**
     * List available types defined by AML.
     * Cautipn, some types are already defined in S-57, prefer S-57 if possible.
     * @return Set<String> , never null
     */
    @Override
    public Set<String> getFeatureTypeNames(){
        final Set<String> names = new HashSet<>();

        final List<AMLFeatureType> types = getAMLObjects(AMLFeatureType.class);
        for(AMLFeatureType type : types){
            names.add(type.Feature_Type_Acronym);
        }

        return names;
    }

    @Override
    public Set<String> getPropertyTypeNames(){
        return getAttributeByAccMap().keySet();
    }

    @Override
    public FeatureType getFeatureType(final String name, final CoordinateReferenceSystem crs) throws DataStoreException{
        final List<AMLFeatureType> types = getAMLObjects(AMLFeatureType.class);
        for(AMLFeatureType type : types){
            if(type.Feature_Type_Acronym.equalsIgnoreCase(name)){
                return getFeatureType(type, crs);
            }
        }
        return null;
    }

    @Override
    public FeatureType getFeatureType(final int code, final CoordinateReferenceSystem crs) throws DataStoreException{
        final List<AMLFeatureType> types = getAMLObjects(AMLFeatureType.class);
        for(AMLFeatureType type : types){
            if(type.Feature_Type_Code == code){
                return getFeatureType(type, crs);
            }
        }
        return null;
    }

    private static FeatureType getFeatureType(final AMLFeatureType type, final CoordinateReferenceSystem crs) throws DataStoreException{
        final List<AMLFeatureCatalog> catalogs = getAMLObjects(AMLFeatureCatalog.class);
        final List<AMLAttribute> attributes = getAMLObjects(AMLAttribute.class);
        final Map<Integer,AMLAttribute> attIndex = new HashMap<>();
        for(AMLAttribute att : attributes){
            attIndex.put(att.Attribute_Code, att);
        }

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setSuperType(S57Constants.ABSTRACT_S57FEATURETYPE);
        ftb.add("spatial", Geometry.class, crs);
        ftb.setName(type.Feature_Type_Acronym);

        for(AMLFeatureCatalog entry : catalogs){
            if(entry.Feature_Type_Code != type.Feature_Type_Code) continue;

            //find the attribute
            final AttributeDescriptor attDesc = TypeBanks.getAttributeDescriptor(entry.Attribute_Code);

            ftb.add(attDesc);
        }

        return ftb.buildFeatureType();
    }

    @Override
    public AttributeDescriptor getAttributeDescriptor(final String code) throws DataStoreException{
        final AMLAttribute att = getAttributeByAccMap().get(code);
        if(att == null) throw new DataStoreException("No property type for code : "+ code);
        return getAttributeDescriptonFromElement(att);
    }

    @Override
    public AttributeDescriptor getAttributeDescriptor(final int code) throws DataStoreException{
        final AMLAttribute att = getAttributeByCodeMap().get(code);
        if(att == null) throw new DataStoreException("No property type for code : "+ code);
        return getAttributeDescriptonFromElement(att);
    }

    public AttributeDescriptor getAttributeDescriptonFromElement(final AMLAttribute aml) throws DataStoreException{

        final Class binding;
        if("String".equals(aml.Attribute_Type)){
            binding = String.class;
        }else if("Coded String".equals(aml.Attribute_Type)){
            binding = String.class;
        }else if("Enumeration".equals(aml.Attribute_Type)){
            binding = String.class;
        }else if("List".equals(aml.Attribute_Type)){
            binding = String.class;
        }else if("Integer".equals(aml.Attribute_Type)){
            binding = Integer.class;
        }else if("Real".equals(aml.Attribute_Type)){
            binding = Double.class;
        }else if("Date".equals(aml.Attribute_Type)){
            binding = Date.class;
        }else{
            throw new DataStoreException("Unexpected attribute type : "+aml.Attribute_Type);
        }

        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        atb.setName(aml.Attribute_Acronym);
        atb.setBinding(binding);
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        adb.setName(aml.Attribute_Acronym);
        adb.setMinOccurs(1);
        adb.setMaxOccurs(1);
        adb.setNillable(true);
        adb.setType(atb.buildType());

        final AttributeDescriptor attDesc = adb.buildDescriptor();
        attDesc.getUserData().put(S57FeatureStore.S57TYPECODE, aml.Attribute_Code);
        return attDesc;
    }

    /**
     * List available types defined by AML.
     */
    private static <T> List<T> getAMLObjects(final Class<T> c){
        final List<T> types = new ArrayList<T>();

        final String str;
        final Constructor constructor;
        try {
            str = FileUtilities.getStringFromStream(AMLTypeBank.class.getResourceAsStream(PATHS.get(c)));
            constructor = c.getConstructor(String[].class);

            final String[] lines = str.split("\n");
            for(String line : lines){
                types.add((T)constructor.newInstance((Object)split(line)));
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }

        return types;
    }

    private static String[] split(String line){
        final String[] parts = line.split("\t",-1);
        //remove escape quotes
        for(int i=0;i<parts.length;i++){
            if(parts[i].startsWith("\"")){
                parts[i] = parts[i].substring(1,parts[i].length()-1);
            }
        }
        return parts;
    }

    @Override
    public int getFeatureTypeCode(String name) throws DataStoreException {
        final List<AMLFeatureType> types = getAMLObjects(AMLFeatureType.class);
        for(AMLFeatureType type : types){
            if(type.Feature_Type_Acronym.equals(name)){
                return type.Feature_Type_Code;
            }
        }
        throw new DataStoreException("No type for name : "+name);
    }

    @Override
    public String getFeatureTypeName(int code) throws DataStoreException {
        final List<AMLFeatureType> types = getAMLObjects(AMLFeatureType.class);
        for(AMLFeatureType type : types){
            if(type.Feature_Type_Code == code){
                return type.Feature_Type_Acronym;
            }
        }
        throw new DataStoreException("No type for code : "+code);
    }

    @Override
    public int getPropertyTypeCode(String name) throws DataStoreException {
        final AMLAttribute att = getAttributeByAccMap().get(name);
        if(att == null){
            throw new DataStoreException("No property for name : "+name);
        }
        return att.Attribute_Code;
    }

    @Override
    public String getPropertyTypeName(int code) throws DataStoreException {
        final AMLAttribute att = getAttributeByCodeMap().get(code);
        if(att == null){
            throw new DataStoreException("No property for code : "+code);
        }
        return att.Attribute_Acronym;
    }

}

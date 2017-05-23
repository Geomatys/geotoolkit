/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.sis.feature.AbstractOperation;
import org.apache.sis.feature.DefaultAssociationRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.util.Static;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;
import org.apache.sis.internal.feature.AttributeConvention;
import org.opengis.feature.AttributeType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FeatureTypeExt extends Static {


    /**
     *
     * @param featureType
     * @param properties
     * @return
     * @throws MismatchedFeatureException
     */
    public static FeatureType createSubType(final FeatureType featureType,
            final String ... properties) throws MismatchedFeatureException{
        if (properties == null) {
            return featureType;
        }

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder(featureType);
        ftb.properties().clear();
        //rebuild type, preserve original property order
        boolean same = true;
        loop:
        for (PropertyType pt : featureType.getProperties(true)) {
            for (String name : properties) {
                if(featureType.getProperty(name).equals(pt)){
                    ftb.addProperty(pt);
                    continue loop;
                }
            }
            same = false;
        }

        return same ? featureType : ftb.build();
    }

    public static FeatureType createSubType(final FeatureType featureType,
            final GenericName[] properties) throws MismatchedFeatureException{
        if (properties == null) {
            return featureType;
        }
        final String[] names = new String[properties.length];
        for(int i=0;i<names.length;i++){
            names[i] = properties[i].toString();
        }
        return createSubType(featureType, names);
    }

    /**
     * Create a derived FeatureType
     *
     * <p></p>
     *
     * @param featureType
     * @param properties - if null, every property of the feature type in input will be used
     * @param override
     * @throws MismatchedFeatureException
     */
    public static FeatureType createSubType(final FeatureType featureType,
            final String[] properties, final CoordinateReferenceSystem override) throws MismatchedFeatureException{

        FeatureType type = featureType;

        if (properties!=null) {
            type = createSubType(featureType, properties);
        }
        if (override!=null) {
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setAbstract(type.isAbstract());
            ftb.setDefinition(type.getDefinition());
            ftb.setDescription(type.getDescription());
            ftb.setDesignation(type.getDesignation());
            ftb.setName(type.getName());
            ftb.setSuperTypes(type.getSuperTypes().toArray(new FeatureType[0]));

            for (PropertyType property : type.getProperties(true)) {
                //replace operations by ViewOperation
                if (AttributeConvention.isGeometryAttribute(property) && property instanceof AttributeType) {
                    ftb.addAttribute((AttributeType) property).setCRS(override);
                } else {
                    ftb.addProperty(property);
                }
            }

            type = ftb.build();
        }

        return type;
    }

    /**
     *
     * @param featureType
     * @param properties
     * @return true if property list contains all feature type properties.
     */
    public static boolean isAllProperties(final FeatureType featureType, String ... properties) {
        final int size = featureType.getProperties(true).size();
        if(size>properties.length) return false;

        //check list contains all properties, using a Set to avoid duplicated names
        final Set<GenericName> names = new HashSet<>();
        for (String name : properties) {
            try{
                names.add(featureType.getProperty(name).getName());
            }catch(PropertyNotFoundException ex) {
                return false;
            }
        }
        return names.size() == size;
    }

    /**
     * Test field equality ignoring convention properties.
     *
     * @param type1
     * @param type2
     * @return
     */
    public static boolean equalsIgnoreConvention(FeatureType type1, FeatureType type2){

        if (type1 == type2) {
            return true;
        }

        //check base properties
        if (!Objects.equals(type1.getName(),        type2.getName()) ||
            !Objects.equals(type1.getDefinition(),  type2.getDefinition()) ||
            !Objects.equals(type1.getDesignation(), type2.getDesignation()) ||
            !Objects.equals(type1.getDesignation(), type2.getDesignation()) ||
            !Objects.equals(type1.isAbstract(),     type2.isAbstract())){
            return false;
        }

        //check super types
        final Set<? extends FeatureType> super1 = type1.getSuperTypes();
        final Set<? extends FeatureType> super2 = type2.getSuperTypes();
        if(super1.size() != super2.size()) return false;
        final Iterator<? extends FeatureType> site1 = super1.iterator();
        final Iterator<? extends FeatureType> site2 = super2.iterator();
        while(site1.hasNext()){
            if(!equalsIgnoreConvention(site1.next(), site2.next())) return false;
        }

        //check properties
        final Set<GenericName> visited = new HashSet<>();
        for (PropertyType pt1 : type1.getProperties(true)) {
            visited.add(pt1.getName());
            if (AttributeConvention.contains(pt1.getName())) continue;
            try {
                final PropertyType pt2 = type2.getProperty(pt1.getName().toString());
                if (!equalsIgnoreConvention(pt1, pt2)) return false;
            } catch (PropertyNotFoundException ex) {
                return false;
            }
        }

        for (PropertyType pt2 : type2.getProperties(true)) {
            if (AttributeConvention.contains(pt2.getName()) || visited.contains(pt2.getName())) continue;
            try {
                final PropertyType pt1 = type1.getProperty(pt2.getName().toString());
                if (!equalsIgnoreConvention(pt1, pt2)) return false;
            } catch (PropertyNotFoundException ex) {
                return false;
            }
        }

        return true;
    }

    private static boolean equalsIgnoreConvention(PropertyType pt1, PropertyType pt2){
        if(pt1 instanceof FeatureAssociationRole){
            if(pt2 instanceof FeatureAssociationRole){
                final FeatureAssociationRole far1 = (FeatureAssociationRole) pt1;
                final FeatureAssociationRole far2 = (FeatureAssociationRole) pt2;

                 //check base properties
                if (!Objects.equals(far1.getName(),        far2.getName()) ||
                    !Objects.equals(far1.getDefinition(),  far2.getDefinition()) ||
                    !Objects.equals(far1.getDesignation(), far2.getDesignation()) ||
                    !Objects.equals(far1.getDesignation(), far2.getDesignation())){
                    return false;
                }

                if(far1.getMinimumOccurs()!=far2.getMinimumOccurs()||
                   far1.getMaximumOccurs()!=far2.getMaximumOccurs()){
                    return false;
                }

                if(!equalsIgnoreConvention(far1.getValueType(), far2.getValueType())){
                    return false;
                }

            }else{
                return false;
            }
        }else if(!pt1.equals(pt2)){
            return false;
        }
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////
    // PARAMETERS API MAPPING OPERATIONS ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Convert given parameter descriptor to a feature type.
     * the original parameter descriptor will be store in the user map with key "origin"
     *
     * @param desc
     * @return ComplexType
     */
    public static FeatureType toFeatureType(final ParameterDescriptorGroup desc){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(NamesExt.valueOf(desc.getName().getCode()));

        for(GeneralParameterDescriptor sd : desc.descriptors()){
            final PropertyType pt = toPropertyType(sd);
            ftb.addProperty(pt);
        }

        return ftb.build();
    }

    /**
     * Convert given parameter descriptor to a feature type.
     * the original parameter descriptor will be store in the user map with key "origin"
     *
     * @param descriptor
     * @return PropertyType
     */
    public static PropertyType toPropertyType(final GeneralParameterDescriptor descriptor){

        if(descriptor instanceof ParameterDescriptor){
            final ParameterDescriptor desc = (ParameterDescriptor) descriptor;

            final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
            atb.setName(NamesExt.valueOf(desc.getName().getCode()));
            atb.setDescription(desc.getRemarks());
            atb.setValueClass(desc.getValueClass());
            atb.setMinimumOccurs(desc.getMinimumOccurs());
            atb.setMaximumOccurs(desc.getMaximumOccurs());
            atb.setDefaultValue(desc.getDefaultValue());

            final Set validValues = desc.getValidValues();
            if(validValues != null && !validValues.isEmpty()){
                atb.setPossibleValues(validValues);
            }

            return atb.build();

        }else if (descriptor instanceof ParameterDescriptorGroup){

            final ParameterDescriptorGroup desc = (ParameterDescriptorGroup) descriptor;
            final FeatureType type = toFeatureType(desc);

            final Map params = new HashMap();
            params.put(DefaultAssociationRole.NAME_KEY, NamesExt.valueOf(desc.getName().getCode()));
            return new DefaultAssociationRole(params, type, desc.getMinimumOccurs(), desc.getMaximumOccurs());
        }else{
            throw new IllegalArgumentException("Unsupported type : " + descriptor.getClass());
        }

    }

    /**
     * Search in the given feature type for a property whose name matches given pattern.
     * Comparison only occurs on local part of the attribute names.
     *
     * @param regex The regex used to describe wanted name.
     * @param toSearchIn The feature type in which we'll perform the search.
     * @return The name of all the attributes which are compliant with given pattern.
     * Can return an empty list, but never null.
     */
    public static List<GenericName> hasNameLike(final String regex, final FeatureType toSearchIn) {
        final List<GenericName> names = new ArrayList<>();
        for (final PropertyType desc : toSearchIn.getProperties(true)) {
            final GenericName name = desc.getName();
            if (name != null && name.tip().toString().matches(regex)) {
                names.add(name);
            }
        }
        return names;
    }

    /**
     * Returns true if property is a component of the feature type primary key.
     *
     * @param type
     * @param propertyName
     * @return
     */
    public static boolean isPartOfPrimaryKey(FeatureType type, String propertyName) {
        PropertyType property;
        try{
            property = type.getProperty(AttributeConvention.IDENTIFIER_PROPERTY.toString());
        } catch(PropertyNotFoundException ex) {
            //no identifier property
            return false;
        }
        if(property instanceof AbstractOperation){
            final Set<String> dependencies = ((AbstractOperation)property).getDependencies();
            return dependencies.contains(propertyName);
        }
        return false;
    }

}

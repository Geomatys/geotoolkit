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

import com.vividsolutions.jts.geom.Geometry;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.internal.feature.ArrayFeature;
import java.util.function.BiFunction;
import org.geotoolkit.internal.feature.FeatureLoop;
import java.util.function.Predicate;
import org.apache.sis.feature.DefaultAttributeType;
import org.apache.sis.feature.DefaultFeatureType;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.Static;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.iso.DefaultNameSpace;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.parameter.Parameters;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociation;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Operation;
import org.opengis.feature.Property;
import org.opengis.feature.PropertyType;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;
import org.opengis.util.NameFactory;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.system.DefaultFactories;

import static org.apache.sis.feature.AbstractIdentifiedType.NAME_KEY;
import org.apache.sis.feature.AbstractOperation;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.util.CollectionsExt;
import org.geotoolkit.geometry.jts.JTS;
import org.opengis.util.FactoryException;

/**
 * NOTE : merge with Apache SIS 'org.apache.sis.feature.Features' class.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FeatureExt extends Static {

    /**
     * Default attribute type names separator.
     */
    public static final String SEPARATOR = String.valueOf(DefaultNameSpace.DEFAULT_SEPARATOR);

    /**
     * Convention name of the feature symbolizers.
     * Some features may have self defined symbology, this is the case of kml,dwg,...
     */
    public static final GenericName ATTRIBUTE_SYMBOLIZERS = DefaultFactories.forBuildin(NameFactory.class)
            .createLocalName(AttributeConvention.IDENTIFIER_PROPERTY.scope(), "@symbolizers");

    /**
     * Method for creating feature id's when none is specified.
     */
    public static String createDefaultFeatureId() {
        // According to GML and XML schema standards, FID is a XML ID
        // (http://www.w3.org/TR/xmlschema-2/#ID), whose acceptable values are those that match an
        // NCNAME production (http://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName):
        // NCName ::= (Letter | '_') (NCNameChar)* /* An XML Name, minus the ":" */
        // NCNameChar ::= Letter | Digit | '.' | '-' | '_' | CombiningChar | Extender
        // We have to fix the generated UID replacing all non word chars with an _ (it seems
        // they area all ":")
        //return "fid-" + NON_WORD_PATTERN.matcher(new UID().toString()).replaceAll("_");
        // optimization, since the UID toString uses only ":" and converts long and integers
        // to strings for the rest, so the only non word character is really ":"
        return "fid-" + new UID().toString().replace(':', '_');
    }

    /**
     * Validate feature state.
     * This method is a shortcut to loop on feature dataquality results.
     * If one ConformanceResult is false then an IllegalArgumentException is throw.
     * Otherwise the function return doing nothing.
     *
     * @deprecated Moved to {@link org.apache.sis.feature.Features#validate(Feature)}.
     */
    @Deprecated
    public static void isValid(Feature feature) throws IllegalArgumentException {
        org.apache.sis.feature.Features.validate(feature);
    }

    /**
     * Create a new array feature.
     *
     * @param type simple FeatureType
     * @return ArrayFeature
     */
    public static ArrayFeature newArrayInstance(FeatureType type){
        return new DefaultArrayFeature((DefaultFeatureType) type);
    }

    /**
     * Build a FeatureId for given feature.
     *
     * This method expect the feature to have the IDENTIFIER_PROPERTY field.
     *
     * @param feature
     * @return FeatureId
     */
    public static FeatureId getId(Feature feature) {
        return new DefaultFeatureId(String.valueOf(feature.getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString())));
    }

    public static void setId(Feature feature, FeatureId id) {
        feature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), id.getID());
    }

    /**
     * Extract feature envelope.
     * The envelope is based on the default geometry only.
     *
     * @param feature
     * @return Envelope, can be null if there is no default geometry
     */
    public static Envelope getEnvelope(Feature feature) {
        GeneralEnvelope bounds = null;
        for (final PropertyType pt : feature.getType().getProperties(true)) {
            if (AttributeConvention.isGeometryAttribute(pt)) {
                final Object val = feature.getPropertyValue(pt.getName().toString());
                if (val instanceof Geometry) {
                    final Geometry geom = (Geometry) val;
                    final com.vividsolutions.jts.geom.Envelope env = geom.getEnvelopeInternal();
                    if (env != null && !env.isNull()) {
                        // extract geometry enveloppe
                        CoordinateReferenceSystem crs = FeatureExt.getCRS(pt);
                        if (crs == null) {
                            try {
                                crs = JTS.findCoordinateReferenceSystem(geom);
                            } catch (FactoryException ex) {
                                //do nothing, we have try
                            }
                        }

                        final GeneralEnvelope genv;
                        if (crs != null) {
                            genv = new GeneralEnvelope(crs);
                        } else {
                            genv = new GeneralEnvelope(2);
                        }
                        genv.setRange(0, env.getMinX(), env.getMaxX());
                        genv.setRange(1, env.getMinY(), env.getMaxY());
                        if (bounds == null) {
                            bounds = genv;
                        } else {
                            bounds.add(genv);
                        }
                    }
                }
            }
        }
        return bounds;
    }

    /**
     * Loop on properties, returns true if there is at least one geometry property.
     *
     * @param type
     * @return true if type has a geometry.
     */
    public static boolean hasAGeometry(FeatureType type){
        for (PropertyType pt : type.getProperties(true)){
            if (AttributeConvention.isGeometryAttribute(pt)) return true;
        }
        return false;
    }

    /**
     * Extract default geometry property crs.
     *
     * @param type
     * @return CoordinateReferenceSystem or null
     */
    public static CoordinateReferenceSystem getCRS(FeatureType type){
        try {
            final IdentifiedType prop = type.getProperty(AttributeConvention.GEOMETRY_PROPERTY.toString());
            if (prop instanceof PropertyType) {
                return getCRS((PropertyType) prop);
            } else {
                return null;
            }
        } catch (IllegalArgumentException ex) {
            //no default geometry property
            return null;
        }
    }

    /**
     * Extract CRS characteristic if it exist.
     *
     * @param type
     * @return CoordinateReferenceSystem or null
     */
    public static CoordinateReferenceSystem getCRS(PropertyType type){
        return getCharacteristicValue(type, AttributeConvention.CRS_CHARACTERISTIC.toString(), null);
    }

    /**
     * Extract characteristic value if it exist.
     *
     * @param <T> expected value class
     * @param type base type to search in
     * @param charName characteristic name
     * @param defaulValue default value if characteristic is missing or null.
     * @return characteristic value or default value is not found
     */
    public static <T> T getCharacteristicValue(PropertyType type, String charName, T defaulValue){
        while(type instanceof Operation){
            type = (PropertyType) ((Operation)type).getResult();
        }
        if(type instanceof AttributeType){
            final AttributeType at = (AttributeType) ((AttributeType)type).characteristics().get(charName);
            if(at!=null){
                T val = (T) at.getDefaultValue();
                return val==null ? defaulValue : val;
            }
        }
        return defaulValue;
    }

    /**
     * Extract field lengh characteristic if it exist.
     *
     * @param type
     * @return Length or null
     */
    public static Integer getLengthCharacteristic(AttributeType type){
        final AttributeType at = (AttributeType) type.characteristics().get(AttributeConvention.MAXIMAL_LENGTH_CHARACTERISTIC.toString());
        if(at!=null){
            return (Integer) at.getDefaultValue();
        }
        return null;
    }

    /**
     * Get AttributeType or FeatureAssociationRole of the given property.
     *
     * @param property
     * @return
     */
    public static PropertyType getType(Property property){
        if(property instanceof Attribute){
            return ((Attribute)property).getType();
        }else {
            return ((FeatureAssociation)property).getRole();
        }
    }

    /**
     * Copy values from first to second feature.
     * This method will only copy values which exist in both types.
     *
     * @param feature
     * @param copy
     * @param deep make a deep copy of property values
     */
    public static void copy(Feature feature, Feature copy, boolean deep){
        final FeatureType baseType = feature.getType();
        final FeatureType copyType = copy.getType();

        final Collection<? extends PropertyType> props = copyType.getProperties(true);
        for(PropertyType pt : props){
            final GenericName gname = pt.getName();
            String name;

            PropertyType bt;
            try{
                name = gname.toString();
                bt = baseType.getProperty(name);
            }catch(IllegalArgumentException ex){
                //property does not exist in base type
                try{
                    name = gname.tip().toString();
                    bt = baseType.getProperty(name);
                }catch(IllegalArgumentException e){
                    //property does not exist in base type
                    continue;
                }
            }


            if(pt instanceof AttributeType){
                Object val = feature.getPropertyValue(name);
                if(val!=null){
                    if(deep) val = deepCopy(val);
                    copy.setPropertyValue(name, ObjectConverters.convert(val,((AttributeType) pt).getValueClass()));
                }
            }else if(pt instanceof FeatureAssociationRole){
                Object val = feature.getPropertyValue(name);
                if(val instanceof Collection){
                    final Collection col = (Collection) val;
                    final Collection cpCol = new ArrayList(col.size());
                    for(Iterator ite=col.iterator();ite.hasNext();){
                        Feature f = (Feature)ite.next();
                        if(deep) f = deepCopy(f);
                        cpCol.add(f);
                    }
                    copy.setPropertyValue(name, cpCol);
                }else if(val!=null){
                    if(deep) val = deepCopy(val);
                    copy.setPropertyValue(name, val);
                }
            }
        }
    }

    /**
     * Create a copy of given feature.
     * This is not a deep copy, only the feature and associated feature are copied,
     * values are not copied.
     *
     * @param feature
     * @return
     */
    public static Feature copy(Feature feature){
        return copy(feature, false);
    }

    /**
     * Make a deep copy of given Feature.
     *
     * @param feature Feature to copy
     * @return Deep copy of the feature
     */
    public static Feature deepCopy(Feature feature){
        return copy(feature, true);
    }

    /**
     *
     * @param feature
     * @param deep true for a deep copy
     * @return
     */
    private static Feature copy(Feature feature, boolean deep){
        final FeatureType type = feature.getType();

        if (type instanceof DecoratedFeatureType) {
            final DecoratedFeatureType decoratingType = (DecoratedFeatureType)type;
            Feature decoratedFeature = ((DecoratedFeature)feature).getDecoratedFeature();
            decoratedFeature = deepCopy(decoratedFeature);
            return decoratingType.newInstance(decoratedFeature);
        } else {
            final Feature cp = type.newInstance();

            final Collection<? extends PropertyType> props = type.getProperties(true);
            for (PropertyType pt : props) {
                if (pt instanceof AttributeType ){
                    final String name = pt.getName().toString();
                    final Object val = feature.getPropertyValue(name);
                    if(val!=null){
                        cp.setPropertyValue(name, deep ? deepCopy(val) : val);
                    }
                } else if(pt instanceof FeatureAssociationRole) {
                    final String name = pt.getName().toString();
                    final Object val = feature.getPropertyValue(name);
                    if (deep) {
                        if(val!=null){
                            cp.setPropertyValue(name, deepCopy(val));
                        }
                    } else {
                        if(val instanceof Collection){
                            final Collection col = (Collection) val;
                            final Collection cpCol = new ArrayList(col.size());
                            for(Iterator ite=col.iterator();ite.hasNext();){
                                cpCol.add(copy((Feature)ite.next()));
                            }
                            cp.setPropertyValue(name, cpCol);
                        }else if(val!=null){
                            cp.setPropertyValue(name, copy((Feature)val));
                        }
                    }

                }
            }
            return cp;
        }
    }

    /**
     * Make a copy of given object.
     * Multiplace cases are tested to make a deep copy.
     *
     * @param candidate
     * @return copied object
     */
    public static Object deepCopy(final Object candidate) {
        if(candidate==null) return null;

        if(candidate instanceof String ||
           candidate instanceof Number ||
           candidate instanceof URL ||
           candidate instanceof URI ||
           candidate.getClass().isPrimitive() ||
           candidate instanceof Character ||
           candidate instanceof GridCoverage){
            //we consider those immutable
            return candidate;
        }else if(candidate instanceof Feature){
            return deepCopy((Feature)candidate);
        }else if(candidate instanceof org.geotoolkit.util.Cloneable){
            return ((org.geotoolkit.util.Cloneable)candidate).clone();
        }else if(candidate instanceof Geometry){
            return ((Geometry)candidate).clone();
        }else if(candidate instanceof Date){
            return ((Date)candidate).clone();
        }else if(candidate instanceof Date){
            return ((Date)candidate).clone();
        }else if(candidate instanceof Object[]){
            final Object[] array = (Object[])candidate;
            final Object[] copy = new Object[array.length];
            for (int i = 0; i < array.length; i++) {
                copy[i] = deepCopy(array[i]);
            }
            return copy;
        }else if(candidate instanceof List){
            final List list = (List)candidate;
            final int size = list.size();
            final List cp = new ArrayList(size);
            for(int i=0;i<size;i++){
                cp.add(deepCopy(list.get(i)));
            }
            return cp;
        }else if (candidate instanceof Map) {
            final Map map = (Map)candidate;
            final Map cp = new HashMap(map.size());
            for(final Iterator<Map.Entry> ite=map.entrySet().iterator(); ite.hasNext();) {
                final Map.Entry entry = ite.next();
                cp.put(entry.getKey(), deepCopy(entry.getValue()));
            }
            return Collections.unmodifiableMap(cp);
        }

        //array type
        final Class clazz = candidate.getClass();
        if(clazz.isArray()){
            final Class compClazz = clazz.getComponentType();
            final int length = Array.getLength(candidate);
            final Object cp = Array.newInstance(compClazz, length);

            if(compClazz.isPrimitive()){
                System.arraycopy(candidate, 0, cp, 0, length);
            }else{
                for(int i=0;i<length; i++){
                    Array.set(cp, i, deepCopy(Array.get(candidate, i)));
                }
            }
            return cp;
        }

        //could not copy
        return candidate;
    }

    /**
     * Get default geometry property.
     *
     * @param type FeatureType
     * @return geometry AttributeType or null
     */
    public static AttributeType<?> getDefaultGeometryAttribute(FeatureType type){
        try {
            PropertyType prop = type.getProperty(AttributeConvention.GEOMETRY_PROPERTY.toString());
            if (prop instanceof AbstractOperation) {
                final Set<String> dependencies = ((AbstractOperation) prop).getDependencies();
                final String referentName = CollectionsExt.first(dependencies);
                if (referentName != null && dependencies.size() == 1) {
                    PropertyType original = type.getProperty(referentName);
                    if (AttributeConvention.isGeometryAttribute(original)) {
                        prop = original;
                    }
                }
            }
            if (prop instanceof AttributeType) {
                return (AttributeType<?>) prop;
            }
        } catch (IllegalArgumentException ex) {
        }
        return null;
    }

    /**
     * Get default geometry property value.
     * This method searches for the default geometry property and return it's value
     * if it exist.
     *
     * @param type FeatureType
     * @return geometry AttributeType or null
     */
    public static Object getDefaultGeometryAttributeValue(Feature type){
        try{
            return type.getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString());
        }catch(IllegalArgumentException ex){
            return null;
        }
    }

    /**
     * Create a new feature type including only the properties in the given array.
     *
     * @param type original feature type
     * @param propertyNames properies to preserve
     * @return Reduced feature type
     */
    public static FeatureType createSubType(FeatureType type, String ... propertyNames) throws IllegalArgumentException {

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(type.getName());
        ftb.setAbstract(type.isAbstract());
        ftb.setSuperTypes(type.getSuperTypes().toArray(new FeatureType[0]));

        for(String name : propertyNames){
            ftb.addProperty(type.getProperty(name));
        }

        return ftb.build();
    }

    /**
     * Create a new feature type including only the properties in the given array.
     *
     * @param type original feature type
     * @param propertyNames properies to preserve
     * @return Reduced feature type
     */
    public static FeatureType createSubType(FeatureType type, GenericName ... propertyNames){

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(type.getName());
        ftb.setAbstract(type.isAbstract());
        ftb.setSuperTypes(type.getSuperTypes().toArray(new FeatureType[0]));

        for(GenericName name : propertyNames){
            ftb.addProperty(type.getProperty(name.toString()));
        }

        return ftb.build();
    }

    /**
     * Create a new feature type including only the properties in the given array
     * and changing the geometry type properties crs.
     *
     * @param type original feature type
     * @param crs change geometric properties crs
     * @param propertyNames properies to preserve
     * @return Reducedd feature type
     */
    public static FeatureType createSubType(FeatureType type, CoordinateReferenceSystem crs, GenericName ... propertyNames){

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(type.getName());
        ftb.setAbstract(type.isAbstract());
        ftb.setSuperTypes(type.getSuperTypes().toArray(new FeatureType[0]));

        for(GenericName name : propertyNames){
            final PropertyType pt = type.getProperty(name.toString());
            if(pt instanceof AttributeType && Geometry.class.isAssignableFrom(((AttributeType)pt).getValueClass()) ){
                final AttributeType qualifier = new DefaultAttributeType(
                    Collections.singletonMap(NAME_KEY, AttributeConvention.CRS_CHARACTERISTIC),
                    CoordinateReferenceSystem.class,1,1,crs);
                AttributeType at = (AttributeType) pt;
                at = new DefaultAttributeType(extractIdentification(pt), at.getValueClass(),
                        at.getMinimumOccurs(), at.getMaximumOccurs(), at.getDefaultValue(), qualifier);
                ftb.addProperty(at);
            }else{
                ftb.addProperty(type.getProperty(name.toString()));
            }
        }

        return ftb.build();
    }

    /**
     * Create a transformed version of the feature type with the new CRS.
     *
     * @param type FeatureType
     * @param crs , new crs for geometric attributes
     * @return FeatureType
     */
    public static FeatureType transform(FeatureType type, CoordinateReferenceSystem crs){

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(type.getName());
        ftb.setAbstract(type.isAbstract());
        ftb.setSuperTypes(type.getSuperTypes().toArray(new FeatureType[0]));

        for(PropertyType pt : type.getProperties(false)){
            if(pt instanceof AttributeType && Geometry.class.isAssignableFrom(((AttributeType)pt).getValueClass()) ){
                final AttributeType qualifier = new DefaultAttributeType(
                    Collections.singletonMap(NAME_KEY, AttributeConvention.CRS_CHARACTERISTIC),
                    CoordinateReferenceSystem.class,1,1,crs);
                AttributeType at = (AttributeType) pt;
                at = new DefaultAttributeType(extractIdentification(pt), at.getValueClass(),
                        at.getMinimumOccurs(), at.getMaximumOccurs(), at.getDefaultValue(), qualifier);
                ftb.addProperty(at);
            }else{
                ftb.addProperty(pt);
            }
        }

        return ftb.build();
    }

    private static Map extractIdentification(PropertyType type){
        final Map map = new HashMap();
        map.put(DefaultFeatureType.NAME_KEY,type.getName());
        map.put(DefaultFeatureType.DEFINITION_KEY,type.getDefinition());
        map.put(DefaultFeatureType.DESCRIPTION_KEY,type.getDescription());
        map.put(DefaultFeatureType.DESIGNATION_KEY,type.getDesignation());
        return map;
    }


    ////////////////////////////////////////////////////////////////////////////
    // PARAMETERS API MAPPING OPERATIONS ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Convert a ComplexAttribute in a parameter.
     *
     * @param source : attributeto convert
     * @param desc : parameter descriptor
     * @return ParameterValueGroup
     */
    public static ParameterValueGroup toParameter(final Feature source, final ParameterDescriptorGroup desc) {

        ArgumentChecks.ensureNonNull("source", source);
        ArgumentChecks.ensureNonNull("desc", desc);
        final ParameterValueGroup target = desc.createValue();
        fill(source,target);
        return target;
    }

    /**
     * Convert a ParameterValueGroup in a feature.
     *
     * @param source : parameter to convert
     * @return Feature
     */
    public static Feature toFeature(final ParameterValueGroup source) {
        ArgumentChecks.ensureNonNull("source", source);
        return toFeature(source,null);
    }

    /**
     * Convert a ParameterValueGroup in a feature.
     *
     * @param source : parameter to convert
     * @param targetType : wanted type, may contain more or less parameters.
     * @return Feature
     */
    public static Feature toFeature(final ParameterValueGroup source, FeatureType targetType) {

        ArgumentChecks.ensureNonNull("source", source);
        if(targetType == null){
            targetType = FeatureTypeExt.toFeatureType(source.getDescriptor());
        }
        final Feature target = targetType.newInstance();
        fill(source,target);
        return target;
    }

    /**
     * Convert a map in a feature.
     *
     * @param source
     * @param targetType
     * @return Feature
     */
    public static Feature toFeature(final Map<String,?> source, FeatureType targetType){
        final Feature feature = targetType.newInstance();
        for(Entry<String,?> entry : source.entrySet()){
            final String key = entry.getKey();
            try{
                feature.setPropertyValue(key, entry.getValue());
            }catch(IllegalArgumentException ex){
                //normal
            }
        }
        return feature;
    }

    /**
     * Convert a Feature in a Map of values.
     *
     * @param att : property to convert
     * @return Map
     */
    public static Map<String,Object> toMap(final Feature att) {

        ArgumentChecks.ensureNonNull("att", att);
        final Map<String,Object> map = new HashMap<>();
        FeatureLoop.loop(att, (Predicate)null, new BiFunction<PropertyType, Object, Object>() {
            @Override
            public Object apply(PropertyType t, Object u) {
                map.put(t.getName().tip().toString(), u);
                return u;
            }
        });
        return map;
    }

    /**
     * Convert a ParameterValueGroup in a Map of values.
     *
     * @param source : parameter to convert
     * @return Map
     *
     * @deprecated Use method {@link Parameters#toMap(org.opengis.parameter.ParameterValueGroup)} instead.
     */
    public static Map<String,Object> toMap(final ParameterValueGroup source) {
        ArgumentChecks.ensureNonNull("source", source);
        return toMap(toFeature(source));
    }

    /**
     * Transform a Map in a ParameterValueGroup.
     * A default parameter is first created and all key found in the map
     * that match the descriptor will be completed.
     *
     * @param params
     * @param desc
     * @return
     *
     * @deprecated Use method {@link Parameters#toParameter(java.util.Map, org.opengis.parameter.ParameterDescriptorGroup)} instead.
     */
    public static ParameterValueGroup toParameter(final Map<String, ?> params, final ParameterDescriptorGroup desc) {
        ArgumentChecks.ensureNonNull("params", params);
        ArgumentChecks.ensureNonNull("desc", desc);
        return toParameter(params, desc, true);
    }

    /**
     * Transform a Map in a ParameterValueGroup.
     * A default parameter is first created and all key found in the map
     * that match the descriptor will be completed.
     *
     * @param params
     * @param desc
     * @param checkMandatory : will return a parameter only if all mandatory values
     *      have been found in the map.
     * @return
     *
     * @deprecated Use method {@link Parameters#toParameter(java.util.Map, org.opengis.parameter.ParameterDescriptorGroup, boolean)} instead.
     */
    public static ParameterValueGroup toParameter(final Map<String, ?> params,
            final ParameterDescriptorGroup desc, final boolean checkMandatory) {

        ArgumentChecks.ensureNonNull("params", params);
        ArgumentChecks.ensureNonNull("desc", desc);
        if(checkMandatory){
            for(GeneralParameterDescriptor de : desc.descriptors()){
                if(de.getMinimumOccurs()>0 && !(params.containsKey(de.getName().getCode()))){
                    //a mandatory parameter is not present
                    return null;
                }
            }
        }

        final ParameterValueGroup parameter = desc.createValue();

        for(final Entry<String, ?> entry : params.entrySet()){

            final GeneralParameterDescriptor subdesc;
            try{
                subdesc = desc.descriptor(entry.getKey());
            }catch(ParameterNotFoundException ex){
                //do nothing, the map may contain other values for other uses
                continue;
            }

            if(!(subdesc instanceof ParameterDescriptor)){
                //we can not recreate value groups
                continue;
            }

            final ParameterDescriptor pd = (ParameterDescriptor) subdesc;

            final ParameterValue param;
            try{
                param = Parameters.getOrCreate(pd,parameter);
            }catch(ParameterNotFoundException ex){
                //do nothing, the map may contain other values for other uses
                continue;
            }

            Object val = entry.getValue();
            try {
                val = ObjectConverters.convert(val, pd.getValueClass());
                param.setValue(val);
            } catch (UnconvertibleObjectException e) {
                Logging.recoverableException(Logging.getLogger("org.apache.sis"), FeatureExt.class, "toParameter", e);
                // TODO - do we really want to ignore?
            }
        }

        return parameter;
    }

    /**
     * Build a {@link Property} from a {@link ParameterValue}.
     * @param parameter {@link ParameterValue}
     * @return a {@link Property}
     */
    public static Property toProperty (final ParameterValue parameter) {
        final ParameterDescriptor descriptor = parameter.getDescriptor();
        final Object value = parameter.getValue();

        final AttributeType at = (AttributeType) FeatureTypeExt.toPropertyType(descriptor);
        final Attribute property = at.newInstance();
        property.setValue(value);
        return property;
    }

    private static void fill(final ParameterValueGroup source, final Feature target){

        final ParameterDescriptorGroup paramdesc = source.getDescriptor();

        for(final PropertyType desc : target.getType().getProperties(true)){

            if(desc instanceof FeatureAssociationRole){
                final FeatureAssociationRole assRole = (FeatureAssociationRole) desc;
                try{
                    final List<ParameterValueGroup> groups = source.groups(desc.getName().tip().toString());
                    if(groups != null){
                        for(ParameterValueGroup gr : groups){
                            final FeatureAssociation att = assRole.newInstance();
                            final Feature val = assRole.getValueType().newInstance();
                            att.setValue(val);
                            fill(gr,val);
                            target.setProperty(att);
                        }
                    }
                }catch(Exception ex){
                    //parameter might not exist of might be a group
                }

            }else if(desc instanceof AttributeType){
                final AttributeType at = (AttributeType) desc;
                final String code = desc.getName().tip().toString();
                final GeneralParameterValue gpv = searchParameter(source, code);

                if(gpv instanceof ParameterValue){
                    target.setPropertyValue(code, ((ParameterValue)gpv).getValue());
                }
            }

        }

    }

    private static void fill(final Feature source, final ParameterValueGroup target){

        for(final GeneralParameterDescriptor gpd : target.getDescriptor().descriptors()){

            final Property property = source.getProperty(gpd.getName().getCode());

            if(gpd instanceof ParameterDescriptor){
                final ParameterDescriptor desc = (ParameterDescriptor) gpd;

                for(final Object v : ((Attribute)property).getValues()){
                    Parameters.getOrCreate(desc, target).setValue(v);
                }
            }else if(gpd instanceof ParameterDescriptorGroup){
                final ParameterDescriptorGroup desc = (ParameterDescriptorGroup) gpd;

                final FeatureAssociation asso = (FeatureAssociation) property;
                for(Feature prop : asso.getValues()){
                    ParameterValueGroup subGroup = null;
                    if (desc.getMaximumOccurs() != 1) {
                        subGroup = target.addGroup(desc.getName().getCode());
                    } else {
                        if (desc.getMinimumOccurs() == 1) {
                            subGroup = target.groups(desc.getName().getCode()).get(0);
                        }
                        if( subGroup == null) {
                            subGroup = target.addGroup(desc.getName().getCode());
                        }
                    }
                    fill(prop,subGroup);
                }
            }
        }
    }

    /**
     * Equivalent to Parameters.getOrCreate but doesn't create the value if it does not exist.
     *
     * @param group
     * @param code
     * @return GeneralParameterValue
     */
    public static GeneralParameterValue searchParameter(final ParameterValueGroup group, final String code){
        ArgumentChecks.ensureNonNull("group", group);
        for(GeneralParameterValue param : group.values()){
            if(param instanceof ParameterValue){
                final ParameterValue pv = (ParameterValue) param;
                if(pv.getDescriptor().getName().getCode().equals(code)){
                    return pv;
                }
            }else if(param instanceof ParameterValueGroup){
                final ParameterValueGroup pvg = (ParameterValueGroup) param;
                if(pvg.getDescriptor().getName().getCode().equals(code)){
                    return pvg;
                }
            }
        }
        return null;
    }

    /**
     * Check that given feature types have got the exact same properties. It is
     * sort of an equality, where type name and property order are ignored.
     * @param first First type to compare.
     * @param second Second type to compare.
     * @param checkSuperTypes True if super types properties must be included in
     * the comparison, false otherwise.
     * @return True if both feature types contains the same properties (whatever
     * order theyr appear in), false otherwise.
     */
    public static boolean sameProperties(final FeatureType first, final FeatureType second, boolean checkSuperTypes) {
        final Collection<? extends PropertyType> firstProperties = first.getProperties(checkSuperTypes);
        final Collection<? extends PropertyType> secondProperties = second.getProperties(checkSuperTypes);

        return firstProperties.size() == secondProperties.size() && firstProperties.containsAll(secondProperties);
    }
}

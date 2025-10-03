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

import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.sis.coverage.grid.GridCoverage;
import static org.apache.sis.feature.AbstractIdentifiedType.NAME_KEY;
import org.apache.sis.feature.DefaultAttributeType;
import org.apache.sis.feature.DefaultFeatureType;
import org.apache.sis.feature.Features;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.geometry.wrapper.Geometries;
import org.apache.sis.geometry.wrapper.GeometryWrapper;
import org.apache.sis.parameter.Parameters;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.collection.BackingStoreException;
import org.apache.sis.util.iso.DefaultNameFactory;
import org.apache.sis.util.iso.DefaultNameSpace;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.internal.feature.FeatureLoop;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociation;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Operation;
import org.opengis.feature.Property;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.ResourceId;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * NOTE : merge with Apache SIS 'org.apache.sis.feature.Features' class.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FeatureExt {

    public static final Logger LOGGER = Logger.getLogger("org.geotoolkit.feature");

    /**
     * Default attribute type names separator.
     */
    public static final String SEPARATOR = String.valueOf(DefaultNameSpace.DEFAULT_SEPARATOR);

    /**
     * TODO remove when AttributeConvention.CRS will exist
     */
    public static final String CRS = "sis:crs";

    /**
     * TODO remove when AttributeConvention.MAXIMAL_LENGTH will exist
     */
    public static final String MAXIMAL_LENGTH = "sis:maximalLength";

    /**
     * Convention name of the feature symbolizers.
     * Some features may have self defined symbology, this is the case of kml,dwg,...
     */
    public static final GenericName ATTRIBUTE_SYMBOLIZERS = DefaultNameFactory.provider()
            .createLocalName(AttributeConvention.IDENTIFIER_PROPERTY.scope(), "@symbolizers");

    /**
     * A test to know if a given property is an SIS convention or not. Return true if
     * the property is NOT marked as an SIS convention, false otherwise.
     */
    public static final Predicate<IdentifiedType> IS_NOT_CONVENTION = p -> !AttributeConvention.contains(p.getName());

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
     * Build a FeatureId for given feature.
     *
     * This method expect the feature to have the IDENTIFIER_PROPERTY field.
     *
     * @param feature The feature on which we want to extract the feature id.
     *
     * @return FeatureId
     */
    public static ResourceId getId(Feature feature) {
        return FilterUtilities.FF.resourceId(String.valueOf(feature.getPropertyValue(AttributeConvention.IDENTIFIER)));
    }

    public static void setId(Feature feature, ResourceId id) {
        feature.setPropertyValue(AttributeConvention.IDENTIFIER, id.getIdentifier());
    }

    /**
     * Extract feature envelope.
     * The envelope is based on the default geometry only.
     *
     * @param feature
     * @return Envelope, can be null if there is no default geometry
     */
    public static Envelope getEnvelope(Feature feature) {
        // envelope attribute is not reprojected
        // if (feature.getValueOrFallback(AttributeConvention.ENVELOPE, null) instanceof Envelope env) return env;

        Object val = getDefaultGeometryValueSafe(feature).orElse(null);
        if (val == null || !Geometries.isKnownType(val.getClass())) return null;
        Geometries<?> factory = Geometries.factory(val.getClass());
        if (factory == null) return null;
        GeometryWrapper geoW = factory.castOrWrap(val);
        return geoW.getEnvelope();
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
     * Extract the coordinate reference system associated to the primary geometry
     * of input data type.
     *
     * @implNote
     * Primary geometry is determined using {@link #getDefaultGeometry(org.opengis.feature.FeatureType) }.
     *
     * @param type The data type to extract reference system from.
     * @return The CRS associated to the default geometry of this data type, or
     * a null value if we cannot determine what is the primary geometry of the
     * data type. Note that a null value is also returned if a geometry property
     * is found, but no CRS characteristics is associated with it.
     */
    public static CoordinateReferenceSystem getCRS(FeatureType type){
        try {
            return getCRS(getDefaultGeometry(type));
        } catch (IllegalArgumentException|IllegalStateException ex) {
            LOGGER.log(Level.FINE, "Cannot extract CRS from type, cause no default geometry is available", ex);
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
        return getCharacteristicValue(type, CRS, null);
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
        final AttributeType at = (AttributeType) type.characteristics().get(MAXIMAL_LENGTH);
        if(at!=null){
            return (Integer) at.getDefaultValue();
        }
        return null;
    }

    /**
     * Get AttributeType or FeatureAssociationRole of the given property.
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
     * @param deep true for a deep copy
     */
    private static Feature copy(Feature feature, boolean deep){
        final FeatureType type = feature.getType();

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
            final Object[] copy = Arrays.copyOf(array, array.length);
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
     * Search for the main geometric property in the given type. We'll search
     * for an SIS convention first (see
     * {@link AttributeConvention#GEOMETRY_PROPERTY}. If no convention is set on
     * the input type, we'll check if it contains a single geometric property.
     * If it's the case, we return it. if multiple geometries are found we throw
     * an exception.
     *
     * @param type The data type to search into.
     * @return The main geometric property we've found.
     * @throws IllegalStateException If we've found more than one geometry.
     */
    public static Optional<PropertyType> getDefaultGeometrySafe(final FeatureType type) throws IllegalStateException {
        PropertyType geometry = null;
        try {
            geometry = getDefaultGeometry(type);
        } catch (PropertyNotFoundException e) {
        }
        return Optional.ofNullable(geometry);
    }

    /**
     * Search for the main geometric property in the given type. We'll search
     * for an SIS convention first (see
     * {@link AttributeConvention#GEOMETRY_PROPERTY}. If no convention is set on
     * the input type, we'll check if it contains a single geometric property.
     * If it's the case, we return it. Otherwise (no or multiple geometries), we
     * throw an exception.
     *
     * @param type The data type to search into.
     * @return The main geometric property we've found.
     * @throws PropertyNotFoundException If no geometric property is available
     * in the given type.
     * @throws IllegalStateException If no convention is set (see
     * {@link AttributeConvention#GEOMETRY_PROPERTY}), and we've found more than
     * one geometry.
     */
    public static PropertyType getDefaultGeometry(final FeatureType type) throws PropertyNotFoundException, IllegalStateException {
        PropertyType geometry;
        try {
            geometry = type.getProperty(AttributeConvention.GEOMETRY);
        } catch (PropertyNotFoundException e) {
            try {
                geometry = searchForGeometry(type);
            } catch (RuntimeException e2) {
                e2.addSuppressed(e);
                throw e2;
            }
        }

        return geometry;
    }

    /**
     * Search for a geometric attribute outside SIS conventions. More accurately,
     * we expect the given type to have a single geometry attribute. If many are
     * found, an exception is thrown.
     *
     * @param type The data type to search into.
     * @return The only geometric property we've found.
     * @throws PropertyNotFoundException If no geometric property is available in
     * the given type.
     * @throws IllegalStateException If we've found more than one geometry.
     */
    private static PropertyType searchForGeometry(final FeatureType type) throws PropertyNotFoundException, IllegalStateException {
        final List<? extends PropertyType> geometries = type.getProperties(true).stream()
                .filter(IS_NOT_CONVENTION)
                .filter(AttributeConvention::isGeometryAttribute)
                .collect(Collectors.toList());

        if (geometries.size() < 1) {
            throw new PropertyNotFoundException("No geometric property can be found outside of sis convention.");
        } else if (geometries.size() > 1) {
            throw new IllegalStateException("Multiple geometries found. We don't know which one to select.");
        } else {
            return geometries.get(0);
        }
    }

    public static Optional<Object> getDefaultGeometryValueSafe(Feature input) throws IllegalStateException {
        try {
            return getDefaultGeometryValue(input);
        } catch (PropertyNotFoundException ex) {}
        return Optional.empty();
    }

    /**
     * Get main geometry property value. The ways this method determines default
     * geometry property are the same as {@link #getDefaultGeometry(org.opengis.feature.FeatureType) }.
     *
     * @param input the feature to extract geometry from.
     * @return Value of the main geometric property of the given feature. The returned
     * optional will be empty only if the feature defines a geometric property, but has
     * no value for it.
     * @throws PropertyNotFoundException If no geometric property is available in
     * the given feature.
     * @throws IllegalStateException If we've found more than one geometry.
     */
    public static Optional<Object> getDefaultGeometryValue(Feature input) throws PropertyNotFoundException, IllegalStateException {
        PropertyType geomType = null;
        Object geometry;
        try{
            geometry = input.getPropertyValue(AttributeConvention.GEOMETRY);
        } catch(PropertyNotFoundException ex) {
            try {
                geomType = FeatureExt.getDefaultGeometry(input.getType());
                geometry = input.getPropertyValue(geomType.getName().toString());
            } catch (RuntimeException e) {
                e.addSuppressed(ex);
                throw e;
            }
        }

        if (geometry instanceof Geometry) {
            //fix for bad readers who do not have crs set on geometries
            Geometry g = (Geometry) geometry;
            CoordinateReferenceSystem crs = Geometries.wrap(geometry).get().getCoordinateReferenceSystem();
            if (crs == null) {
                if (geomType == null) {
                    crs = getCRS(input.getType().getProperty(AttributeConvention.GEOMETRY));
                } else {
                    crs = getCRS(geomType);
                }
                if (crs != null) {
                    g.setUserData(crs);
                }
            }
        }

        return Optional.ofNullable(geometry);
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

        ensureNonNull("source", source);
        ensureNonNull("desc", desc);
        final Parameters target = Parameters.castOrWrap(desc.createValue());
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
        ensureNonNull("source", source);
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

        ensureNonNull("source", source);
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

        ensureNonNull("att", att);
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
        ensureNonNull("source", source);
        return toMap(toFeature(source));
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

    private static void fill(final Feature source, final Parameters target){

        for(final GeneralParameterDescriptor gpd : target.getDescriptor().descriptors()){

            final Property property = source.getProperty(gpd.getName().getCode());

            if(gpd instanceof ParameterDescriptor){
                final ParameterDescriptor desc = (ParameterDescriptor) gpd;

                for(final Object v : ((Attribute)property).getValues()){
                    target.getOrCreate(desc).setValue(v);
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
                    fill(prop,Parameters.castOrWrap(subGroup));
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
        ensureNonNull("group", group);
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
        return sameProperties(first, second, checkSuperTypes, false);
    }

    public static boolean sameProperties(final FeatureType first, final FeatureType second, boolean checkSuperTypes, final boolean ignoreConventions) {
        Collection<? extends PropertyType> firstProperties = first.getProperties(checkSuperTypes);
        Collection<? extends PropertyType> secondProperties = second.getProperties(checkSuperTypes);

        if (ignoreConventions) {
            final Predicate<IdentifiedType> isNotConvention = IS_NOT_CONVENTION;
            firstProperties = firstProperties.stream()
                    .filter(isNotConvention)
                    .collect(Collectors.toList());
            secondProperties = secondProperties.stream()
                    .filter(isNotConvention)
                    .collect(Collectors.toList());
        }

        return firstProperties.size() == secondProperties.size() && firstProperties.containsAll(secondProperties);
    }

    /**
     * Try to create an operator to extract primary geometry from features of a specific type. This method offers the
     * following advantages:
     * <ul>
     *     <li>When read geometry does not define any CRS, we assign the one extracted from related property type.</li>
     *     <li>Property/characteristic analysis is done on assembly, to ensure minimal overhead on result function execution.</li>
     * </ul>
     *
     * @param targetType Type of the features that will be passed as input to the resulting function. Cannot be null.
     * @return A function for geometry extraction from features whose is or inherits from input type. Never null.
     * If we cannot return a valid value, an error will be thrown as specified by {@link #getDefaultGeometry(FeatureType)}.
     * @throws RuntimeException See {@link #getDefaultGeometry(FeatureType)}.
     */
    public static Function<Feature, Geometry> prepareGeometryExtractor(final FeatureType targetType) {
        ensureNonNull("Target type", targetType);
        PropertyType geom = getDefaultGeometry(targetType);
        // Minor optimisation : directly use geometry attribute in case a link convention has been set.
        geom = Features.getLinkTarget(geom)
                .map(name -> targetType.getProperty(name))
                .orElse(geom);
        final AttributeType<?> attr = Features.toAttribute(geom)
                .orElseThrow(() -> new IllegalStateException("Cannot extract geometries when associate type is not an attribute"));

        final Class<?> vClass = attr.getValueClass();
        if (!Geometry.class.isAssignableFrom(vClass)) {
            throw new UnsupportedOperationException("Only JTS geometries are supported for now.");
        }

        // Name is built from geom, not attr, because attr can be a virtual result property, not present in source type.
        // For example, if you've got two numeric attributes x and y, then add a concatenation operation, you've got no
        // geometric attribute, but a geometric operation.
        final String name = geom.getName().toString();
        final CoordinateReferenceSystem crs = AttributeConvention.getCRSCharacteristic(targetType, attr);
        if (crs == null) {
            return f -> (Geometry) f.getPropertyValue(name);
        } else {
            return f -> {
                final Object value = f.getPropertyValue(name);
                if (value == null) return null;
                final Geometry geometry = (Geometry) value;
                final CoordinateReferenceSystem currentCrs;
                try {
                    currentCrs = JTS.findCoordinateReferenceSystem(geometry);
                } catch (FactoryException e) {
                    throw new BackingStoreException(e);
                }
                if (currentCrs == null) JTS.setCRS(geometry, crs);
                return geometry;
            };
        }
    }
}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geotoolkit.factory.FactoryRegistryException;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotools.geometry.jts.JTS;
import org.geotoolkit.util.Utilities;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.SchemaException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.expression.Literal;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.filter.function.other.LengthFunction;

/**
 * Utility methods for working against the FeatureType interface.
 * <p>
 * Many methods from DataUtilities should be refractored here.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Schema construction from String spec
 * <li>Schema Force CRS
 * </ul>
 *
 * @author Jody Garnett, Refractions Research
 * @since 2.1.M3
 * @source $URL$
 */
public class FeatureTypes {

    /** the default namespace for feature types */
    //public static final URI = GMLSchema.NAMESPACE;
    public static final URI DEFAULT_NAMESPACE;


    static {
        URI uri;
        try {
            uri = new URI("http://www.opengis.net/gml");
        } catch (URISyntaxException e) {
            uri = null;	//will never happen
        }
        DEFAULT_NAMESPACE = uri;
    }
    /** abstract base type for all feature types */
    public final static SimpleFeatureType ABSTRACT_FEATURE_TYPE;


    static {
        SimpleFeatureType featureType = null;
        try {
            featureType = FeatureTypes.newFeatureType(null, "Feature", new URI("http://www.opengis.net/gml"), true);
        } catch (Exception e) {
            //shold not happen
        }
        ABSTRACT_FEATURE_TYPE = featureType;
    }
    /** default feature collection name */
    public static final DefaultName DEFAULT_TYPENAME =
            new DefaultName("AbstractFeatureCollectionType", DEFAULT_NAMESPACE.toString());
    /** represent an unbounded field length */
    public static final int ANY_LENGTH = -1;
    /** An feature type with no attributes */
    public static final SimpleFeatureType EMPTY = new DefaultSimpleFeatureType(
            new DefaultName("Empty"), Collections.EMPTY_LIST, null, false, Collections.EMPTY_LIST, null, null);

    /**
     * This is a 'suitable replacement for extracting the expected field length of an attribute
     * absed on its "facets" (ie Filter describing type restrictions);
     * <p>
     * This code is copied from the ShapefileDataStore where it was written (probably by dzwiers).
     * Cholmes is providing documentation.
     * </p>
     *
     * @param type the AttributeType
     *
     * @return an int indicating the max length of field in characters, or ANY_LENGTH
     */
    public static int getFieldLength(final AttributeDescriptor descriptor) {

        AttributeType type = descriptor.getType();
        while (type != null) {
            // TODO: We should really go through all the restrictions and find
            // the minimum of all the length restrictions; for now we assume an
            // override behaviour.
            for (Filter f : type.getRestrictions()) {
                if (f != null && f != Filter.EXCLUDE && f != Filter.INCLUDE && (f instanceof PropertyIsLessThan || f instanceof PropertyIsLessThanOrEqualTo)) {
                    try {
                        BinaryComparisonOperator cf = (BinaryComparisonOperator) f;
                        if (cf.getExpression1() instanceof LengthFunction) {
                            return Integer.parseInt(((Literal) cf.getExpression2()).getValue().toString());
                        } else if (cf.getExpression2() instanceof LengthFunction) {
                            return Integer.parseInt(((Literal) cf.getExpression1()).getValue().toString());
                        } else {
                            return ANY_LENGTH;
                        }
                    } catch (NumberFormatException e) {
                        return ANY_LENGTH;
                    }
                }
            }
            type = type.getSuper();
        }
        return ANY_LENGTH;
    }

    /**
     * Forces the specified CRS on all geometry attributes
     * @param schema the original schema
     * @param crs the forced crs
     * @return
     * @throws SchemaException
     */
    public static SimpleFeatureType transform(final SimpleFeatureType schema, final CoordinateReferenceSystem crs)
            throws SchemaException
    {
        return transform(schema, crs, false);
    }

    /**
     * Forces the specified CRS on geometry attributes (all or some, depends on the parameters).
     * @param schema the original schema
     * @param crs the forced crs
     * @param forceOnlyMissing if true, will force the specified crs only on the attributes that
     *        do miss one
     * @return
     * @throws SchemaException
     */
    public static SimpleFeatureType transform(final SimpleFeatureType schema, final CoordinateReferenceSystem crs,
            boolean forceOnlyMissing) throws SchemaException
    {
        final SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
        tb.setName(schema.getTypeName());
        tb.setNamespaceURI(schema.getName().getNamespaceURI());
        tb.setAbstract(schema.isAbstract());

        for (int i = 0; i < schema.getAttributeCount(); i++) {
            final AttributeDescriptor attributeType = schema.getDescriptor(i);
            if (attributeType instanceof GeometryDescriptor) {
                final GeometryDescriptor geometryType = (GeometryDescriptor) attributeType;

                tb.descriptor(geometryType);
                if (!forceOnlyMissing || geometryType.getCoordinateReferenceSystem() == null) {
                    tb.crs(crs);
                }

                tb.add(geometryType.getLocalName(), geometryType.getType().getBinding());
            } else {
                tb.add(attributeType);
            }
        }
        if (schema.getGeometryDescriptor() != null) {
            tb.setDefaultGeometry(schema.getGeometryDescriptor().getLocalName());
        }

        tb.setSuperType((SimpleFeatureType) schema.getSuper());

        return tb.buildFeatureType();
    }

    /**
     * Applies transform to all geometry attribute.
     *
     * @param feature Feature to be transformed
     * @param schema Schema for target transformation - transform( schema, crs )
     * @param transform MathTransform used to transform coordinates - reproject( crs, crs )
     * @return transformed Feature of type schema
     * @throws TransformException
     * @throws MismatchedDimensionException
     * @throws IllegalAttributeException
     */
    public static SimpleFeature transform(SimpleFeature feature, final SimpleFeatureType schema, final MathTransform transform)
            throws MismatchedDimensionException, TransformException, IllegalAttributeException
    {
        feature = SimpleFeatureBuilder.copy(feature);

        final GeometryDescriptor geomType = schema.getGeometryDescriptor();
        Geometry geom = (Geometry) feature.getAttribute(geomType.getLocalName());

        geom = JTS.transform(geom, transform);

        feature.setAttribute(geomType.getLocalName(), geom);

        return feature;
    }

    /**
     * The most specific way to create a new FeatureType.
     *
     * @param types The AttributeTypes to create the FeatureType with.
     * @param name The typeName of the FeatureType. Required, may not be null.
     * @param ns The namespace of the FeatureType. Optional, may be null.
     * @param isAbstract True if this created type should be abstract.
     * @param superTypes A Collection of types the FeatureType will inherit from. Currently, all
     *        types inherit from feature in the opengis namespace.
     * @return A new FeatureType created from the given arguments.
     * @throws FactoryRegistryException If there are problems creating a factory.
     * @throws SchemaException If the AttributeTypes provided are invalid in some way.
     */
    public static SimpleFeatureType newFeatureType(final AttributeDescriptor[] types, final String name,
            final URI ns, final boolean isAbstract, final SimpleFeatureType[] superTypes)
            throws FactoryRegistryException, SchemaException
    {
        return newFeatureType(types, name, ns, isAbstract, superTypes, null);
    }

    /**
     * The most specific way to create a new FeatureType.
     *
     * @param types The AttributeTypes to create the FeatureType with.
     * @param name The typeName of the FeatureType. Required, may not be null.
     * @param ns The namespace of the FeatureType. Optional, may be null.
     * @param isAbstract True if this created type should be abstract.
     * @param superTypes A Collection of types the FeatureType will inherit from. Currently, all
     *        types inherit from feature in the opengis namespace.
     * @return A new FeatureType created from the given arguments.
     * @throws FactoryRegistryException If there are problems creating a factory.
     * @throws SchemaException If the AttributeTypes provided are invalid in some way.
     */
    public static SimpleFeatureType newFeatureType(final AttributeDescriptor[] types, final String name,
            final URI ns, final boolean isAbstract, final SimpleFeatureType[] superTypes,
            final AttributeDescriptor defaultGeometry) throws FactoryRegistryException, SchemaException
    {
        final SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();

        tb.setName(name);
        tb.setNamespaceURI(ns);
        tb.setAbstract(isAbstract);
        tb.addAll(types);

        if (defaultGeometry != null) {
            //make sure that the default geometry was one of the types specified
            boolean add = true;
            for (int i = 0; i < types.length; i++) {
                if (types[i] == defaultGeometry) {
                    add = false;
                    break;
                }
            }
            if (add) {
                tb.add(defaultGeometry);
            }
            tb.setDefaultGeometry(defaultGeometry.getLocalName());
        }
        if (superTypes != null && superTypes.length > 0) {
            if (superTypes.length > 1) {
                throw new SchemaException("Can only specify a single super type");
            }
            tb.setSuperType(superTypes[0]);

        } else {
            //use the default super type
            tb.setSuperType(ABSTRACT_FEATURE_TYPE);
        }
        return (SimpleFeatureType) tb.buildFeatureType();
    }

    /**
     * The most specific way to create a new FeatureType.
     *
     * @param types The AttributeTypes to create the FeatureType with.
     * @param name The typeName of the FeatureType. Required, may not be null.
     * @param ns The namespace of the FeatureType. Optional, may be null.
     * @param isAbstract True if this created type should be abstract.
     * @param superTypes A Collection of types the FeatureType will inherit from. Currently, all
     *        types inherit from feature in the opengis namespace.
     * @return A new FeatureType created from the given arguments.
     * @throws FactoryRegistryException If there are problems creating a factory.
     * @throws SchemaException If the AttributeTypes provided are invalid in some way.
     */
    public static SimpleFeatureType newFeatureType(final AttributeDescriptor[] types, final String name,
            final URI ns, final boolean isAbstract, final SimpleFeatureType[] superTypes,
            final GeometryDescriptor defaultGeometry) throws FactoryRegistryException, SchemaException
    {
        return newFeatureType(types, name, ns, isAbstract, superTypes, (AttributeDescriptor) defaultGeometry);
    }

    /**
     * Create a new FeatureType with the given AttributeTypes. A short cut for calling
     * <code>newFeatureType(types,name,ns,isAbstract,null)</code>.
     *
     * @param types The AttributeTypes to create the FeatureType with.
     * @param name The typeName of the FeatureType. Required, may not be null.
     * @param ns The namespace of the FeatureType. Optional, may be null.
     * @param isAbstract True if this created type should be abstract.
     * @return A new FeatureType created from the given arguments.
     * @throws FactoryRegistryException If there are problems creating a factory.
     * @throws SchemaException If the AttributeTypes provided are invalid in some way.
     */
    public static SimpleFeatureType newFeatureType(final AttributeDescriptor[] types, final String name,
            final URI ns, final boolean isAbstract) throws FactoryRegistryException, SchemaException
    {
        return newFeatureType(types, name, ns, isAbstract, null);
    }

    /**
     * Create a new FeatureType with the given AttributeTypes. A short cut for calling
     * <code>newFeatureType(types,name,ns,false,null)</code>.
     *
     * @param types The AttributeTypes to create the FeatureType with.
     * @param name The typeName of the FeatureType. Required, may not be null.
     * @param ns The namespace of the FeatureType. Optional, may be null.
     * @return A new FeatureType created from the given arguments.
     * @throws FactoryRegistryException If there are problems creating a factory.
     * @throws SchemaException If the AttributeTypes provided are invalid in some way.
     */
    public static SimpleFeatureType newFeatureType(final AttributeDescriptor[] types, final String name,
            final URI ns) throws FactoryRegistryException, SchemaException
    {
        return newFeatureType(types, name, ns, false);
    }

    /**
     * Create a new FeatureType with the given AttributeTypes. A short cut for calling
     * <code>newFeatureType(types,name,null,false,null)</code>. Useful for test cases or
     * datasources which may not allow a namespace.
     *
     * @param types The AttributeTypes to create the FeatureType with.
     * @param name The typeName of the FeatureType. Required, may not be null.
     * @return A new FeatureType created from the given arguments.
     * @throws FactoryRegistryException If there are problems creating a factory.
     * @throws SchemaException If the AttributeTypes provided are invalid in some way.
     */
    public static SimpleFeatureType newFeatureType(final AttributeDescriptor[] types, final String name)
            throws FactoryRegistryException, SchemaException
    {
        return newFeatureType(types, name, DEFAULT_NAMESPACE, false);
    }

    /**
     * Walks up the type hierachy of the feature returning all super types of the specified feature
     * type.
     */
    public static List<FeatureType> getAncestors(FeatureType featureType) {
        final List<FeatureType> ancestors = new ArrayList<FeatureType>();
        while (featureType.getSuper() != null) {
            if (featureType.getSuper() instanceof FeatureType) {
                final FeatureType superType = (FeatureType) featureType.getSuper();
                ancestors.add(superType);
                featureType = superType;
            }
        }
        return ancestors;
    }

    /**
     * A query of the the types ancestor information.
     * <p>
     * This utility method may be used as common implementation for
     * <code>FeatureType.isDecendedFrom( namespace, typeName )</code>, however for specific uses,
     * such as GML, an implementor may be able to provide a more efficient implemenation based on
     * prior knolwege.
     * </p>
     * <p>
     * This is a proper check, if the provided FeatureType matches the given namespace and typename
     * it is <b>not </b> considered to be decended from itself.
     * </p>
     *
     * @param featureType
     *            typeName with parentage in question
     * @param namespace
     *            namespace to match against, or null for a "wildcard"
     * @param typeName
     *            typename to match against, or null for a "wildcard"
     * @return true if featureType is a decendent of the indicated namespace & typeName
     */
    public static boolean isDecendedFrom(final FeatureType featureType, final URI namespace,
            final String typeName)
    {
        if (featureType == null) {
            return false;
        }
        final List<FeatureType> ancestors = getAncestors(featureType);
        for (FeatureType superType : ancestors) {
            if (namespace == null) {
                // dont match on namespace
                if (Utilities.equals(superType.getName().getLocalPart(), typeName)) {
                    return true;
                }
            } else {
                if (Utilities.equals(superType.getName().getNamespaceURI(), namespace.toString()) &&
                    Utilities.equals(superType.getName().getLocalPart(), typeName))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDecendedFrom(final FeatureType featureType, final FeatureType isParentType) {
        try {
            return isDecendedFrom(featureType, new URI(isParentType.getName().getNamespaceURI()),
                    isParentType.getName().getLocalPart());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /** Exact equality based on typeNames, namespace, attributes and ancestors */
    public static boolean equals(final SimpleFeatureType typeA, final SimpleFeatureType typeB) {
        if (typeA == typeB) {
            return true;
        }

        if (typeA == null || typeB == null) {
            return false;
        }
        return equalsId(typeA, typeB) && equals(typeA.getAttributeDescriptors(), typeB.getAttributeDescriptors()) &&
                equalsAncestors(typeA, typeB);
    }

    public static boolean equals(final List attributesA, final List attributesB) {
        return equals(
                (AttributeDescriptor[]) attributesA.toArray(new AttributeDescriptor[attributesA.size()]),
                (AttributeDescriptor[]) attributesB.toArray(new AttributeDescriptor[attributesB.size()]));
    }

    public static boolean equals(final AttributeDescriptor attributesA[], final AttributeDescriptor attributesB[]) {
        if (attributesA.length != attributesB.length) {
            return false;
        }

        for (int i = 0, length = attributesA.length; i < length; i++) {
            if (!equals(attributesA[i], attributesB[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method depends on the correct implementation of FeatureType equals
     * <p>
     * We may need to write an implementation that can detect cycles,
     * </p>
     *
     * @param typeA
     * @param typeB
     */
    public static boolean equalsAncestors(final SimpleFeatureType typeA, final SimpleFeatureType typeB) {
        return ancestors(typeA).equals(typeB);
    }

    public static Set ancestors(final SimpleFeatureType featureType) {
        if (featureType == null || getAncestors(featureType).isEmpty()) {
            return Collections.EMPTY_SET;
        }
        return new HashSet(getAncestors(featureType));
    }

    public static boolean equals(final AttributeDescriptor a, final AttributeDescriptor b) {
        return a == b || (a != null && a.equals(b));
    }

    /** Quick check of namespace and typename */
    public static boolean equalsId(final SimpleFeatureType typeA, final SimpleFeatureType typeB) {
        if (typeA == typeB) {
            return true;
        }

        if (typeA == null || typeB == null) {
            return false;
        }

        String typeNameA = typeA.getTypeName();
        String typeNameB = typeB.getTypeName();
        if (typeNameA == null && typeNameB != null) {
            return false;
        } else if (!typeNameA.equals(typeNameB)) {
            return false;
        }

        String namespaceA = typeA.getName().getNamespaceURI();
        String namespaceB = typeB.getName().getNamespaceURI();
        if (namespaceA == null && namespaceB != null) {
            return false;
        } else if (!namespaceA.equals(namespaceB)) {
            return false;
        }

        return true;
    }
}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.geotoolkit.data.collection.CollectionDataStore;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.collection.FeatureCollection;
import org.geotoolkit.feature.collection.FeatureIterator;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.FeatureCollectionUtilities;
import org.geotoolkit.filter.visitor.FilterAttributeExtractor;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.util.Converters;

import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Utility functions for use when implementing working with data classes.
 * <p>
 * TODO: Move FeatureType manipulation to feature package
 * </p>
 * @author Jody Garnett, Refractions Research
 * @source $URL$
 * @module pending
 */
public final class DataUtilities extends FeatureCollectionUtilities {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static String[] attributeNames(final SimpleFeatureType featureType) {
        final String[] names = new String[featureType.getAttributeCount()];
        final int count = featureType.getAttributeCount();
        for (int i = 0; i < count; i++) {
            names[i] = featureType.getDescriptor(i).getLocalName();
        }

        return names;
    }

    /**
     * Takes a URL and converts it to a File. The attempts to deal with
     * Windows UNC format specific problems, specifically files located
     * on network shares and different drives.
     *
     * If the URL.getAuthority() returns null or is empty, then only the
     * url's path property is used to construct the file. Otherwise, the
     * authority is prefixed before the path.
     *
     * It is assumed that url.getProtocol returns "file".
     *
     * Authority is the drive or network share the file is located on.
     * Such as "C:", "E:", "\\fooServer"
     *
     * @param url a URL object that uses protocol "file"
     * @return a File that corresponds to the URL's location
     */
    public static File urlToFile(final URL url) {
        String string = url.toExternalForm();

        try {
            string = URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Shouldn't happen
        }

        final String path3;
        final String simplePrefix = "file:/";
        final String standardPrefix = simplePrefix + "/";

        if (string.startsWith(standardPrefix)) {
            path3 = string.substring(standardPrefix.length());
        } else if (string.startsWith(simplePrefix)) {
            path3 = string.substring(simplePrefix.length() - 1);
        } else {
            final String auth = url.getAuthority();
            final String path2 = url.getPath().replace("%20", " ");
            if (auth != null && !auth.equals("")) {
                path3 = "//" + auth + path2;
            } else {
                path3 = path2;
            }
        }

        return new File(path3);
    }

    /**
     * Traverses the filter and returns any encoutered property names.
     * <p>
     * The feautre type is supplied as contexts used to lookup expressions in cases where the
     * attributeName does not match the actual name of the type.
     * </p>
     */
    public static String[] attributeNames(final Filter filter, final SimpleFeatureType featureType) {
        if (filter == null) {
            return new String[0];
        }
        final FilterAttributeExtractor attExtractor = new FilterAttributeExtractor(featureType);
        filter.accept(attExtractor, null);
        final String[] attributeNames = attExtractor.getAttributeNames();
        return attributeNames;
    }

    /**
     * Traverses the expression and returns any encoutered property names.
     * <p>
     * The feautre type is supplied as contexts used to lookup expressions in cases where the
     * attributeName does not match the actual name of the type.
     * </p>
     */
    public static String[] attributeNames(final Expression expression, final SimpleFeatureType featureType) {
        if (expression == null) {
            return new String[0];
        }
        final FilterAttributeExtractor attExtractor = new FilterAttributeExtractor(featureType);
        expression.accept(attExtractor, null);
        final String[] attributeNames = attExtractor.getAttributeNames();
        return attributeNames;
    }

    /**
     * Compare operation for FeatureType.
     *
     * <p>
     * Results in:
     * </p>
     *
     * <ul>
     * <li>
     * 1: if typeA is a sub type/reorder/renamespace of typeB
     * </li>
     * <li>
     * 0: if typeA and typeB are the same type
     * </li>
     * <li>
     * -1: if typeA is not subtype of typeB
     * </li>
     * </ul>
     *
     * <p>
     * Comparison is based on AttributeTypes, an IOException is thrown if the
     * AttributeTypes are not compatiable.
     * </p>
     *
     * <p>
     * Namespace is not considered in this opperations. You may still need to
     * reType to get the correct namesapce, or reorder.
     * </p>
     *
     * @param typeA FeatureType beind compared
     * @param typeB FeatureType being compared against
     *
     */
    public static int compare(final SimpleFeatureType typeA, final SimpleFeatureType typeB) {
        if (typeA == typeB) {
            return 0;
        }

        if (typeA == null) {
            return -1;
        }

        if (typeB == null) {
            return -1;
        }

        final int countA = typeA.getAttributeCount();
        final int countB = typeB.getAttributeCount();

        if (countA > countB) {
            return -1;
        }

        // may still be the same featureType
        // (Perhaps they differ on namespace?)
        AttributeDescriptor a;

        // may still be the same featureType
        // (Perhaps they differ on namespace?)
        int match = 0;

        for (int i = 0; i < countA; i++) {
            a = typeA.getDescriptor(i);

            if (isMatch(a, typeB.getDescriptor(i))) {
                match++;
            } else if (isMatch(a, typeB.getDescriptor(a.getLocalName()))) {
                // match was found in a different position
            } else {
                // cannot find any match for Attribute in typeA
                return -1;
            }
        }

        if ((countA == countB) && (match == countA)) {
            // all attributes in typeA agreed with typeB
            // (same order and type)
            //            if (typeA.getNamespace() == null) {
            //            	if(typeB.getNamespace() == null) {
            //            		return 0;
            //            	} else {
            //            		return 1;
            //            	}
            //            } else if(typeA.getNamespace().equals(typeB.getNamespace())) {
            //                return 0;
            //            } else {
            //                return 1;
            //            }
            return 0;
        }

        return 1;
    }

    public static boolean isMatch(final AttributeDescriptor a, final AttributeDescriptor b) {
        if (a == b) {
            return true;
        }

        if (b == null) {
            return false;
        }

        if (a == null) {
            return false;
        }

        if (a.equals(b)) {
            return true;
        }

        if (a.getLocalName().equals(b.getLocalName()) && a.getClass().equals(b.getClass())) {
            return true;
        }

        return false;
    }

    /**
     * Creates duplicate of feature adjusted to the provided featureType.
     *
     * @param featureType FeatureType requested
     * @param feature Origional Feature from DataStore
     *
     * @return An instance of featureType based on feature
     *
     * @throws IllegalAttributeException If opperation could not be performed
     */
    public static SimpleFeature reType(final SimpleFeatureType featureType, final SimpleFeature feature)
            throws IllegalAttributeException {
        final SimpleFeatureType origional = feature.getFeatureType();

        if (featureType.equals(origional)) {
            return SimpleFeatureBuilder.copy(feature);
        }

        final String id = feature.getID();
        final int numAtts = featureType.getAttributeCount();
        final Object[] attributes = new Object[numAtts];
        String xpath;

        for (int i = 0; i < numAtts; i++) {
            final AttributeDescriptor curAttType = featureType.getDescriptor(i);
            xpath = curAttType.getLocalName();
            attributes[i] = FeatureUtilities.duplicate(feature.getAttribute(xpath));
        }

        return SimpleFeatureBuilder.build(featureType, attributes, id);
    }

    /**
     * Constructs an empty feature to use as a Template for new content.
     *
     * <p>
     * We may move this functionality to FeatureType.create( null )?
     * </p>
     *
     * @param featureType Type of feature we wish to create
     *
     * @return A new Feature of type featureType
     *
     * @throws IllegalAttributeException if we could not create featureType
     *         instance with acceptable default values
     */
    public static SimpleFeature template(final SimpleFeatureType featureType)
            throws IllegalAttributeException {
        return SimpleFeatureBuilder.build(featureType, defaultValues(featureType), null);
    }

    public static SimpleFeature template(final SimpleFeatureType featureType, final String featureID)
            throws IllegalAttributeException {
        return SimpleFeatureBuilder.build(featureType, defaultValues(featureType), featureID);
    }

    public static Object[] defaultValues(final SimpleFeatureType featureType)
            throws IllegalAttributeException {
        return defaultValues(featureType, null);
    }

    public static SimpleFeature template(final SimpleFeatureType featureType, final Object[] atts)
            throws IllegalAttributeException {
        return SimpleFeatureBuilder.build(featureType, defaultValues(featureType, atts), null);
    }

    public static SimpleFeature template(final SimpleFeatureType featureType, final String featureID,
            Object[] atts) throws IllegalAttributeException {
        return SimpleFeatureBuilder.build(featureType, defaultValues(featureType, atts), featureID);
    }

    public static Object[] defaultValues(final SimpleFeatureType featureType,
            Object[] values) throws IllegalAttributeException {
        if (values == null) {
            values = new Object[featureType.getAttributeCount()];
        } else if (values.length != featureType.getAttributeCount()) {
            throw new ArrayIndexOutOfBoundsException("values");
        }

        for (int i = 0; i < featureType.getAttributeCount(); i++) {
            values[i] = defaultValue(featureType.getDescriptor(i));
        }

        return values;
    }

    /**
     * Provides a defautlValue for attributeType.
     *
     * <p>
     * Will return null if attributeType isNillable(), or attempt to use
     * Reflection, or attributeType.parse( null )
     * </p>
     *
     * @param attributeType
     *
     * @return null for nillable attributeType, attempt at reflection
     *
     * @throws IllegalAttributeException If value cannot be constructed for
     *         attribtueType
     */
    public static Object defaultValue(final AttributeDescriptor attributeType)
            throws IllegalAttributeException {
        final Object value = attributeType.getDefaultValue();

        if (value == null && !attributeType.isNillable()) {
            return null; // sometimes there is no valid default value :-(
            // throw new IllegalAttributeException("Got null default value for non-null type.");
        }
        return value;
    }

    /**
     * Creates a  FeatureReader<SimpleFeatureType, SimpleFeature> for testing.
     *
     * @param features Array of features
     *
     * @return  FeatureReader<SimpleFeatureType, SimpleFeature> spaning provided feature array
     *
     * @throws IOException If provided features Are null or empty
     * @throws NoSuchElementException DOCUMENT ME!
     */
    public static FeatureReader<SimpleFeatureType, SimpleFeature> reader(final SimpleFeature[] features)
            throws IOException {
        if ((features == null) || (features.length == 0)) {
            throw new IOException("Provided features where empty");
        }

        return new FeatureReader<SimpleFeatureType, SimpleFeature>() {

            SimpleFeature[] array = features;
            int offset = -1;

            @Override
            public SimpleFeatureType getFeatureType() {
                return features[0].getFeatureType();
            }

            @Override
            public SimpleFeature next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more features");
                }

                return array[++offset];
            }

            @Override
            public boolean hasNext() {
                return (array != null) && (offset < (array.length - 1));
            }

            @Override
            public void close() {
                array = null;
                offset = -1;
            }
        };
    }

    public static FeatureSource<SimpleFeatureType, SimpleFeature> source(final SimpleFeature[] featureArray) {
        final SimpleFeatureType featureType;

        if ((featureArray == null) || (featureArray.length == 0)) {
            featureType = FeatureTypeUtilities.EMPTY;
        } else {
            featureType = featureArray[0].getFeatureType();
        }

        final DataStore arrayStore = new AbstractDataStore() {

            @Override
            public String[] getTypeNames() {
                return new String[]{featureType.getTypeName()};
            }

            @Override
            public SimpleFeatureType getSchema(final String typeName)
                    throws IOException {
                if ((typeName != null) && typeName.equals(featureType.getTypeName())) {
                    return featureType;
                }

                throw new IOException(typeName + " not available");
            }

            @Override
            protected FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(final String typeName)
                    throws IOException {
                return reader(featureArray);
            }
        };

        try {
            return arrayStore.getFeatureSource(arrayStore.getTypeNames()[0]);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Something is wrong with the geotools code, " + "this exception should not happen", e);
        }
    }

    public static FeatureSource<SimpleFeatureType, SimpleFeature> source(final FeatureCollection<SimpleFeatureType, SimpleFeature> collection) {
        if (collection == null) {
            throw new NullPointerException();
        }

        final DataStore store = new CollectionDataStore(collection);

        try {
            return store.getFeatureSource(store.getTypeNames()[0]);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Something is wrong with the geotools code, " + "this exception should not happen", e);
        }
    }

    public static FeatureCollection<SimpleFeatureType, SimpleFeature> results(final SimpleFeature[] featureArray) {
        return results(collection(featureArray));
    }

    /**
     * Returns collection if non empty.
     *
     * @param collection
     *
     * @return provided collection
     *
     * @throws IOException Raised if collection was empty
     */
    public static FeatureCollection<SimpleFeatureType, SimpleFeature> results(final FeatureCollection<SimpleFeatureType, SimpleFeature> collection) {
        if (collection.size() == 0) {
            //throw new IOException("Provided collection was empty");
        }
        return collection;
    }

    /**
     * Adapt a collection to a reader for use with FeatureStore.setFeatures( reader ).
     *
     * @param collection Collection of SimpleFeature
     *
     * @return FeatureRedaer over the provided contents
     * @throws IOException IOException if there is any problem reading the content.
     */
    public static FeatureReader<SimpleFeatureType, SimpleFeature> reader(final Collection<SimpleFeature> collection)
            throws IOException {
        return reader((SimpleFeature[]) collection.toArray(
                new SimpleFeature[collection.size()]));
    }

    /**
     * Adapt a collection to a reader for use with FeatureStore.setFeatures( reader ).
     *
     * @param collection Collection of SimpleFeature
     *
     * @return FeatureRedaer over the provided contents
     * @throws IOException IOException if there is any problem reading the content.
     */
    public static FeatureReader<SimpleFeatureType, SimpleFeature> reader(
            final FeatureCollection<SimpleFeatureType, SimpleFeature> collection) throws IOException {
        return reader((SimpleFeature[]) collection.toArray(new SimpleFeature[collection.size()]));
    }

    public static boolean attributesEqual(final Object att, final Object otherAtt) {
        if (att == null) {
            if (otherAtt != null) {
                return false;
            }
        } else {
            if (!att.equals(otherAtt)) {
                if (att instanceof Geometry && otherAtt instanceof Geometry) {
                    // we need to special case Geometry
                    // as JTS is broken
                    // Geometry.equals( Object ) and Geometry.equals( Geometry )
                    // are different
                    // (We should fold this knowledge into AttributeType...)
                    //
                    if (!((Geometry) att).equals((Geometry) otherAtt)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    public static SimpleFeature parse(final SimpleFeatureType type, final String fid,
            final String[] text) throws IllegalAttributeException {
        final Object[] attributes = new Object[text.length];

        for (int i = 0; i < text.length; i++) {
            final AttributeType attType = type.getDescriptor(i).getType();
            attributes[i] = Converters.convert(text[i], attType.getBinding());
        }

        return SimpleFeatureBuilder.build(type, attributes, fid);
    }

    /**
     * Takes two {@link Query}objects and produce a new one by mixing the
     * restrictions of both of them.
     *
     * <p>
     * The policy to mix the queries components is the following:
     *
     * <ul>
     * <li>
     * typeName: type names MUST match (not checked if some or both queries
     * equals to <code>Query.ALL</code>)
     * </li>
     * <li>
     * handle: you must provide one since no sensible choice can be done
     * between the handles of both queries
     * </li>
     * <li>
     * maxFeatures: the lower of the two maxFeatures values will be used (most
     * restrictive)
     * </li>
     * <li>
     * attributeNames: the attributes of both queries will be joined in a
     * single set of attributes. IMPORTANT: only <b><i>explicitly</i></b>
     * requested attributes will be joint, so, if the method
     * <code>retrieveAllProperties()</code> of some of the queries returns
     * <code>true</code> it does not means that all the properties will be
     * joined. You must create the query with the names of the properties you
     * want to load.
     * </li>
     * <li>
     * filter: the filtets of both queries are or'ed
     * </li>
     * <li>
     * <b>any other query property is ignored</b> and no guarantees are made of
     * their return values, so client code shall explicitly care of hints, startIndex, etc.,
     * if needed.
     * </li>
     * </ul>
     * </p>
     *
     * @param firstQuery Query against this DataStore
     * @param secondQuery DOCUMENT ME!
     * @param handle DOCUMENT ME!
     *
     * @return Query restricted to the limits of definitionQuery
     *
     * @throws NullPointerException if some of the queries is null
     * @throws IllegalArgumentException if the type names of both queries do
     *         not match
     */
    public static Query mixQueries(final Query firstQuery, final Query secondQuery, final String handle) {
        if ((firstQuery == null) || (secondQuery == null)) {
            throw new NullPointerException("got a null query argument");
        }

        if (firstQuery.equals(Query.ALL)) {
            return secondQuery;
        } else if (secondQuery.equals(Query.ALL)) {
            return firstQuery;
        }

        if ((firstQuery.getTypeName() != null) && (secondQuery.getTypeName() != null)) {
            if (!firstQuery.getTypeName().equals(secondQuery.getTypeName())) {
                String msg = "Type names do not match: " + firstQuery.getTypeName() + " != " + secondQuery.getTypeName();
                throw new IllegalArgumentException(msg);
            }
        }

        // mix versions, if possible
        final String version;
        if (firstQuery.getVersion() != null) {
            if (secondQuery.getVersion() != null && !secondQuery.getVersion().equals(firstQuery.getVersion())) {
                throw new IllegalArgumentException("First and second query refer different versions");
            }
            version = firstQuery.getVersion();
        } else {
            version = secondQuery.getVersion();
        }


        //none of the queries equals Query.ALL, mix them
        //use the more restrictive max features field
        final int maxFeatures = Math.min(firstQuery.getMaxFeatures(),
                secondQuery.getMaxFeatures());

        //join attributes names
        final String[] propNames = joinAttributes(firstQuery.getPropertyNames(),
                secondQuery.getPropertyNames());

        //join filters
        Filter filter = firstQuery.getFilter();
        Filter filter2 = secondQuery.getFilter();

        if ((filter == null) || filter.equals(Filter.INCLUDE)) {
            filter = filter2;
        } else if ((filter2 != null) && !filter2.equals(Filter.INCLUDE)) {
            filter = FF.and(filter, filter2);
        }
        Integer start = 0;
        if (firstQuery.getStartIndex() != null) {
            start = firstQuery.getStartIndex();
        }
        if (secondQuery.getStartIndex() != null) {
            start += secondQuery.getStartIndex();
        }
        //build the mixed query
        final String typeName = firstQuery.getTypeName() != null ? firstQuery.getTypeName() : secondQuery.getTypeName();

        final DefaultQuery mixed = new DefaultQuery(typeName, filter, maxFeatures, propNames, handle);
        mixed.setVersion(version);
        if (start != 0) {
            mixed.setStartIndex(start);
        }
        return mixed;
    }

    /**
     * Creates a set of attribute names from the two input lists of names,
     * maintaining the order of the first list and appending the non repeated
     * names of the second.
     * <p>
     * In the case where both lists are <code>null</code>, <code>null</code>
     * is returned.
     * </p>
     *
     * @param atts1 the first list of attribute names, who's order will be
     *        maintained
     * @param atts2 the second list of attribute names, from wich the non
     *        repeated names will be appended to the resulting list
     *
     * @return Set of attribute names from <code>atts1</code> and
     *         <code>atts2</code>
     */
    private static String[] joinAttributes(final String[] atts1, final String[] atts2) {
        if (atts1 == null && atts2 == null) {
            return null;
        }

        final List atts = new LinkedList();

        if (atts1 != null) {
            atts.addAll(Arrays.asList(atts1));
        }

        if (atts2 != null) {
            for (int i = 0; i < atts2.length; i++) {
                if (!atts.contains(atts2[i])) {
                    atts.add(atts2[i]);
                }
            }
        }

        final String[] propNames = new String[atts.size()];
        atts.toArray(propNames);

        return propNames;
    }

    /**
     * Manually calculates the bounds of a feature collection.
     * @param collection
     * @return
     */
    public static Envelope bounds(final FeatureCollection<? extends FeatureType, ? extends Feature> collection) {
        final FeatureIterator<? extends Feature> i = collection.features();
        try {
            final JTSEnvelope2D bounds = new JTSEnvelope2D(collection.getSchema().getCoordinateReferenceSystem());
            if (!i.hasNext()) {
                bounds.setToNull();
                return bounds;
            }

            bounds.init(((SimpleFeature) i.next()).getBounds());
            return bounds;
        } finally {
            i.close();
        }
    }
}

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collection;

import org.opengis.util.GenericName;
import org.opengis.util.FactoryException;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.iso.DefaultNameSpace;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.factory.IdentifiedObjectFinder;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.apache.sis.referencing.NamedIdentifier;

import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import static org.opengis.referencing.IdentifiedObject.IDENTIFIERS_KEY;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Utility methods working on arbitrary implementations of the {@link IdentifiedObject}
 * interface.
 *
 * {@section Note on Spatial Reference System (SRS) identifiers}
 * OGC Web Services have the concept of a Spatial Reference System identifier used to
 * communicate CRS information between systems. In <cite>Well Known Text</cite> (WKT)
 * format, this identifier is declared in the {@code AUTHORITY} element.
 * <p>
 * Examples of Spatial Reference System (SRS) values:
 * <ul>
 *   <li>{@code EPSG:4326} - this was understood to mean <cite>force XY axis order</cite> in
 *       old Web Map Services (WMS). Note that latest WMS specifications require the respect
 *       of axis order as declared in the EPSG database, which is (<var>latitude</var>,
 *       <var>longitude</var>).</li>
 *   <li>{@code urn:ogc:def:crs:EPSG:4326} - understood to match the EPSG database axis order
 *       in all cases, no matter the WMS version.</li>
 *   <li>{@code AUTO:43200} - without the parameters that are specific to AUTO codes.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.20
 *
 * @see CRS
 * @see org.geotoolkit.geometry.Envelopes
 *
 * @since 3.18 (derived from 1.2)
 * @module
 */
public final class IdentifiedObjects extends Static {
    /**
     * Do not allows instantiation of this class.
     */
    private IdentifiedObjects() {
    }

    /**
     * An empty array of identifiers. This is useful for fetching identifiers as an array,
     * using the following idiom:
     *
     * {@preformat java
     *     getIdentifiers().toArray(EMPTY_IDENTIFIER_ARRAY);
     * }
     *
     * @see IdentifiedObject#getIdentifiers()
     */
    public static final ReferenceIdentifier[] EMPTY_IDENTIFIER_ARRAY = new ReferenceIdentifier[0];

    /**
     * An empty array of alias. This is useful for fetching alias as an array,
     * using the following idiom:
     *
     * {@preformat java
     *     getAlias().toArray(EMPTY_ALIAS_ARRAY);
     * }
     *
     * @see IdentifiedObject#getAlias()
     */
    public static final GenericName[] EMPTY_ALIAS_ARRAY = new GenericName[0];

    /**
     * A comparator for sorting identified objects by {@linkplain IdentifiedObject#getName() name}.
     */
    public static final Comparator<IdentifiedObject> NAME_COMPARATOR = new NameComparator();

    /**
     * A comparator for sorting identified objects by {@linkplain IdentifiedObject#getIdentifiers identifiers}.
     * Identifiers are compared in their iteration order.
     */
    public static final Comparator<IdentifiedObject> IDENTIFIER_COMPARATOR = new IdentifierComparator();

    /**
     * A comparator for sorting identified objects by {@linkplain IdentifiedObject#getRemarks remarks}.
     */
    public static final Comparator<IdentifiedObject> REMARKS_COMPARATOR = new RemarksComparator();

    /**
     * Compares two objects for order. Any object may be null. This method is
     * used for implementation of {@link #NAME_COMPARATOR} and its friends.
     */
    static <E extends Comparable<E>> int doCompare(final E c1, final E c2) {
        if (c1 == null) {
            return (c2 == null) ? 0 : -1;
        }
        if (c2 == null) {
            return +1;
        }
        return c1.compareTo(c2);
    }

    /**
     * Returns the collection iterator, or {@code null} if the given collection is null or
     * empty. We use this method as a paranoiac safety against broken implementations.
     *
     * @param  <E> The type of elements in the collection.
     * @param  collection The collection from which to get the iterator, or {@code null}.
     * @return The iterator over the given collection elements, or {@code null}.
     *
     * @since 3.20
     */
    private static <E> Iterator<E> iterator(final Collection<E> collection) {
        return (collection != null && !collection.isEmpty()) ? collection.iterator() : null;
    }

    /**
     * Returns the properties to be given to an identified object derived from the specified one.
     * This method returns the same properties than the supplied argument (as of
     * <code>{@linkplain #getProperties(IdentifiedObject) getProperties}(info)</code>), except for
     * the following:
     * <p>
     * <ul>
     *   <li>The {@linkplain IdentifiedObject#getName() name}'s authority is replaced by the specified one.</li>
     *   <li>All {@linkplain IdentifiedObject#getIdentifiers identifiers} are removed, because the new object
     *       to be created is probably not endorsed by the original authority.</li>
     * </ul>
     * <p>
     * This method returns a mutable map. Consequently, callers can add their own identifiers
     * directly to this map if they wish.
     *
     * @param  info The identified object to view as a properties map.
     * @param  authority The new authority for the object to be created, or {@code null} if it
     *         is not going to have any declared authority.
     * @return The identified object properties in a mutable map.
     */
    public static Map<String,Object> getProperties(final IdentifiedObject info, final Citation authority) {
        final Map<String,Object> properties = new HashMap<>(org.apache.sis.referencing.IdentifiedObjects.getProperties(info));
        properties.put(NAME_KEY, new NamedIdentifier(authority, info.getName().getCode()));
        properties.remove(IDENTIFIERS_KEY);
        return properties;
    }

    /**
     * Looks up an {@linkplain ReferenceIdentifier identifier}, such as {@code "EPSG:4326"},
     * of the specified object. This method searches in registered factories for an object
     * {@linkplain ComparisonMode#APPROXIMATIVE approximatively equals} to the specified
     * object. If such an object is found, then its first identifier is returned. Otherwise
     * this method returns {@code null}.
     * <p>
     * <strong>Note that this method checks the identifier validity</strong>. If the given object
     * declares explicitly an identifier, then this method will instantiate an object from the
     * authority factory using that identifier and compare it with the given object. If the
     * comparison fails, then this method returns {@code null}. Consequently this method may
     * returns {@code null} even if the given object declares explicitly its identifier. If
     * the declared identifier is wanted unconditionally, use
     * {@link #getIdentifier(IdentifiedObject)} instead.
     *
     * @param  object The object (usually a {@linkplain CoordinateReferenceSystem coordinate
     *         reference system}) whose identifier is to be found, or {@code null}.
     * @param  fullScan If {@code true}, an exhaustive full scan against all registered objects
     *         should be performed (may be slow). Otherwise only a fast lookup based on embedded
     *         identifiers and names will be performed.
     * @return The identifier, or {@code null} if none was found or if the given object was null.
     * @throws FactoryException If an unexpected failure occurred during the search.
     *
     * @see AbstractAuthorityFactory#getIdentifiedObjectFinder(Class)
     * @see IdentifiedObjectFinder#findIdentifier(IdentifiedObject)
     */
    public static String lookupIdentifier(final IdentifiedObject object, final boolean fullScan)
            throws FactoryException
    {
        if (object == null) {
            return null;
        }
        /*
         * We perform the search using the 'xyFactory' because our implementation of
         * IdentifiedObjectFinder should be able to inspect both the (x,y) and (y,x)
         * axis order using this factory.
         */
        final AbstractAuthorityFactory xyFactory = (AbstractAuthorityFactory) CRS.getAuthorityFactory(true);
        final IdentifiedObjectFinder finder = xyFactory.getIdentifiedObjectFinder(object.getClass());
        finder.setComparisonMode(ComparisonMode.APPROXIMATIVE);
        finder.setFullScanAllowed(fullScan);
        return finder.findIdentifier(object);
    }

    /**
     * Looks up an {@linkplain ReferenceIdentifier identifier} in the namespace of the given
     * authority, such as {@link Citations#EPSG EPSG}, of the specified CRS. Invoking this
     * method is equivalent to invoking
     * <code>{@linkplain #lookupIdentifier(IdentifiedObject, boolean) lookupIdentifier}(object,
     * fullScan)</code> except that the search is performed only among the factories of the given
     * authority.
     *
     * {@section Identifiers in URN and HTTP namespaces}
     * Note that if the given authority is {@link Citations#URN_OGC} or {@link Citations#HTTP_OGC},
     * then this method behaves as if the code was searched in all authority factories and the
     * result formatted in a {@code "urn:ogc:def:"} or
     * {@value org.geotoolkit.referencing.factory.web.HTTP_AuthorityFactory#BASE_URL} namespace.
     *
     * @param  authority The authority for the code to search.
     * @param  object The object (usually a {@linkplain CoordinateReferenceSystem coordinate
     *         reference system}) whose identifier is to be found, or {@code null}.
     * @param  fullScan If {@code true}, an exhaustive full scan against all registered objects
     *         should be performed (may be slow). Otherwise only a fast lookup based on embedded
     *         identifiers and names will be performed.
     * @return The identifier, or {@code null} if none was found or if the given object was null.
     * @throws FactoryException If an unexpected failure occurred during the search.
     *
     * @category information
     */
    public static String lookupIdentifier(final Citation authority, final IdentifiedObject object,
            final boolean fullScan) throws FactoryException
    {
        ensureNonNull("authority", authority);
        if (object == null) {
            return null;
        }
        ReferenceIdentifier id = org.apache.sis.referencing.IdentifiedObjects.getIdentifier(object, authority);
        if (id != null) {
            return id.getCode();
        }
        final DefaultAuthorityFactory df = (DefaultAuthorityFactory) CRS.getAuthorityFactory(true);
        for (final AuthorityFactory factory : df.backingStore.getFactories()) {
            if (!Citations.identifierMatches(factory.getAuthority(), authority)) {
                continue;
            }
            if (!(factory instanceof AbstractAuthorityFactory)) {
                continue;
            }
            final AbstractAuthorityFactory f = (AbstractAuthorityFactory) factory;
            final IdentifiedObjectFinder finder = f.getIdentifiedObjectFinder(object.getClass());
            finder.setComparisonMode(ComparisonMode.APPROXIMATIVE);
            finder.setFullScanAllowed(fullScan);
            final String code = finder.findIdentifier(object);
            if (code != null) {
                return code;
            }
        }
        return null;
    }

    /**
     * Looks up an EPSG code of the given {@linkplain CoordinateReferenceSystem
     * coordinate reference system}). This is a convenience method for <code>{@linkplain
     * #lookupIdentifier(Citation, IdentifiedObject, boolean) lookupIdentifier}({@linkplain
     * Citations#EPSG EPSG}, crs, fullScan)</code> with the returned code parsed as an integer.
     *
     * @param  object The object (usually a {@linkplain CoordinateReferenceSystem coordinate
     *         reference system}) whose identifier is to be found, or {@code null}.
     * @param  fullScan If {@code true}, an exhaustive full scan against all registered objects
     *         should be performed (may be slow). Otherwise only a fast lookup based on embedded
     *         identifiers and names will be performed.
     * @return The identifier, or {@code null} if none was found or if the given object was null.
     * @throws FactoryException If an unexpected failure occurred during the search.
     *
     * @category information
     */
    public static Integer lookupEpsgCode(final IdentifiedObject object, final boolean fullScan)
            throws FactoryException
    {
        final String identifier = lookupIdentifier(Citations.EPSG, object, fullScan);
        if (identifier != null) {
            final int split = identifier.lastIndexOf(DefaultNameSpace.DEFAULT_SEPARATOR);
            final String code = identifier.substring(split + 1);
            // The above code works even if the separator was not found, since in such case
            // split == -1, which implies a call to substring(0) which returns 'identifier'.
            try {
                return Integer.parseInt(code);
            } catch (NumberFormatException e) {
                throw new FactoryException(Errors.format(Errors.Keys.ILLEGAL_IDENTIFIER_1, identifier), e);
            }
        }
        return null;
    }
}

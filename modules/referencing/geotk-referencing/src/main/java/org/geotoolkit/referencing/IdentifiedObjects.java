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
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collection;

import org.opengis.util.NameSpace;
import org.opengis.util.LocalName;
import org.opengis.util.ScopedName;
import org.opengis.util.GenericName;
import org.opengis.util.FactoryException;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.naming.DefaultNameSpace;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.factory.IdentifiedObjectFinder;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;

import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import static org.opengis.referencing.IdentifiedObject.IDENTIFIERS_KEY;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.apache.sis.internal.util.Citations.identifierMatches;


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
     * Returns the informations provided in the specified identified object as a map of
     * properties. The returned map contains keys declared in the {@link IdentifiedObject}
     * interface, for example {@link IdentifiedObject#NAME_KEY NAME_KEY}. The values are
     * obtained by calls to the methods associated to each key, for example
     * {@link IdentifiedObject#getName()} for the {@code NAME_KEY}.
     *
     * @param  info The identified object to view as a properties map.
     * @return An view of the identified object as an immutable map.
     */
    public static Map<String,?> getProperties(final IdentifiedObject info) {
        ensureNonNull("info", info);
        return new Properties(info);
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
     * @return A view of the identified object as a mutable map.
     */
    public static Map<String,Object> getProperties(final IdentifiedObject info, final Citation authority) {
        final Map<String,Object> properties = new HashMap<>(getProperties(info));
        properties.put(NAME_KEY, new NamedIdentifier(authority, info.getName().getCode()));
        properties.remove(IDENTIFIERS_KEY);
        return properties;
    }

    /**
     * Returns an object name according the given authority. This method checks first the
     * {@linkplain IdentifiedObject#getName() primary name}, then all
     * {@linkplain IdentifiedObject#getAlias() alias} in their iteration order.
     *
     * <ul>
     *   <li><p>If the name or alias implements the {@link ReferenceIdentifier} interface,
     *       then this method compares the {@linkplain ReferenceIdentifier#getAuthority()
     *       identifier authority} against the specified citation using the
     *       {@link Citations#identifierMatches(Citation,Citation) identifierMatches} method.
     *       If a matching is found, then this method returns the
     *       {@linkplain ReferenceIdentifier#getCode identifier code} of this object.</p></li>
     *
     *   <li><p>Otherwise, if the alias implements the {@link GenericName} interface, then this method
     *       compares the {@linkplain GenericName#scope name scope} against the specified citation using the
     *       {@linkplain Citations#identifierMatches(Citation,String) identifierMatches} method.
     *       If a matching is found, then this method returns the
     *       {@linkplain GenericName#tip name tip} of this object.</p></li>
     * </ul>
     *
     * Note that alias may implement both the {@link ReferenceIdentifier} and {@link GenericName}
     * interfaces (for example {@link NamedIdentifier}). In such cases, the identifier view has
     * precedence.
     *
     * @param  info The object to get the name from, or {@code null}.
     * @param  authority The authority for the name to return, or {@code null} for any authority.
     * @return The object name (either a {@linkplain ReferenceIdentifier#getCode code} or a
     *         {@linkplain GenericName#tip name tip}), or {@code null} if no name matching the
     *         specified authority was found.
     *
     * @see AbstractIdentifiedObject#getName(Citation)
     */
    public static String getName(final IdentifiedObject info, final Citation authority) {
        if (info instanceof AbstractIdentifiedObject) {
            // Gives a chances to subclasses to get their overridden method invoked.
            return ((AbstractIdentifiedObject) info).getName(authority);
        }
        return name(info, authority, null);
    }

    /**
     * Returns every object names and aliases according the given authority. This method performs
     * the same work than {@link #getName(IdentifiedObject, Citation)}, except that it doesn't
     * stop at the first match. This method is useful in the rare cases where the same authority
     * declare more than one name, and all those names are of interest.
     *
     * @param  info The object to get the names and aliases from, or {@code null}.
     * @param  authority The authority for the names to return, or {@code null} for any authority.
     * @return The object names and aliases, or an empty set if no name or alias matching the
     *         specified authority was found.
     *
     * @since 3.20
     */
    public static Set<String> getNames(final IdentifiedObject info, final Citation authority) {
        final Set<String> names = new LinkedHashSet<>(8);
        name(info, authority, names);
        return names;
    }

    /**
     * Returns an object name according the given authority. This method is {@code null}-safe:
     * every properties are checked for null values, even the properties that are supposed to
     * be mandatory (not all implementation defines all mandatory values).
     *
     * @param  info The object to get the name from, or {@code null}.
     * @param  authority The authority for the name to return, or {@code null} for any authority.
     * @param  addTo If non-null, the collection where to add all names found.
     * @return The object's name (either a {@linkplain ReferenceIdentifier#getCode code} or a
     *         {@linkplain GenericName#tip name tip}), or {@code null} if no name matching the
     *         specified authority was found.
     */
    static String name(final IdentifiedObject info, final Citation authority, final Collection<String> addTo) {
        if (info != null) {
            Identifier identifier = info.getName();
            if (authority == null) {
                if (identifier != null) {
                    final String name = identifier.getCode();
                    if (name != null) {
                        if (addTo == null) {
                            return name;
                        }
                        addTo.add(name);
                    }
                }
                final Iterator<GenericName> it = iterator(info.getAlias());
                if (it != null) while (it.hasNext()) {
                    final GenericName alias = it.next();
                    if (alias != null) {
                        final String name = (alias instanceof Identifier) ?
                                ((Identifier) alias).getCode() : alias.toString();
                        if (name != null) {
                            if (addTo == null) {
                                return name;
                            }
                            addTo.add(name);
                        }
                    }
                }
            } else {
                if (identifier != null) {
                    if (identifierMatches(authority, identifier.getAuthority())) {
                        final String name = identifier.getCode();
                        if (name != null) {
                            if (addTo == null) {
                                return name;
                            }
                            addTo.add(name);
                        }
                    }
                }
                final Iterator<GenericName> it = iterator(info.getAlias());
                if (it != null) while (it.hasNext()) {
                    final GenericName alias = it.next();
                    if (alias != null) {
                        if (alias instanceof Identifier) {
                            identifier = (Identifier) alias;
                            if (identifierMatches(authority, identifier.getAuthority())) {
                                final String name = identifier.getCode();
                                if (name != null) {
                                    if (addTo == null) {
                                        return name;
                                    }
                                    addTo.add(name);
                                }
                            }
                        } else {
                            final NameSpace ns = alias.scope();
                            if (ns != null) {
                                final GenericName scope = ns.name();
                                if (scope != null) {
                                    if (identifierMatches(authority, scope.toString())) {
                                        final String name = alias.toString();
                                        if (name != null) {
                                            if (addTo == null) {
                                                return name;
                                            }
                                            addTo.add(name);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns an identifier for the given object according the given authority. This method checks
     * all {@linkplain IdentifiedObject#getIdentifiers() identifiers} in their iteration order. It
     * returns the first identifier with an {@linkplain ReferenceIdentifier#getAuthority authority}
     * citation {@linkplain org.geotoolkit.metadata.iso.citation.Citations#identifierMatches(Citation,
     * Citation) matching} the specified authority.
     *
     * @param  object The object to get the identifier from, or {@code null}.
     * @param  authority The authority for the identifier to return, or {@code null} for
     *         the first identifier regardless its authority.
     * @return The object's identifier, or {@code null} if no identifier matching the specified
     *         authority was found.
     *
     * @see AbstractIdentifiedObject#getIdentifier(Citation)
     */
    public static ReferenceIdentifier getIdentifier(final IdentifiedObject object, final Citation authority) {
        if (object instanceof AbstractIdentifiedObject) {
            // Gives a chances to subclasses to get their overridden method invoked.
            return ((AbstractIdentifiedObject) object).getIdentifier(authority);
        }
        return identifier(object, authority);
    }

    /**
     * Returns an identifier according the given authority.
     *
     * @param  object The object to get the identifier from, or {@code null}.
     * @param  authority The authority for the identifier to return, or {@code null}.
     * @return The object's identifier, or {@code null} if none.
     */
    static ReferenceIdentifier identifier(final IdentifiedObject object, final Citation authority) {
        if (object != null) {
            final Iterator<ReferenceIdentifier> it = iterator(object.getIdentifiers());
            if (it != null) while (it.hasNext()) {
                final ReferenceIdentifier identifier = it.next();
                if (identifier != null) { // Paranoiac check.
                    if (authority == null || identifierMatches(authority, identifier.getAuthority())) {
                        return identifier;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the declared identifier, or {@code null} if none. This method searches for the first
     * identifier (which is usually the main one) explicitly declared in the {@link IdentifiedObject}.
     * At the opposite of {@link #lookupIdentifier(IdentifiedObject, boolean) lookupIdentifier},
     * <em>this method does not verify the identifier validity</em>.
     * <p>
     * More specifically, this method uses the first non-null element found in
     * <code>object.{@linkplain IdentifiedObject#getIdentifiers() getIdentifiers()}</code>. If there
     * is none, then it uses <code>object.{@linkplain IdentifiedObject#getName() getName()}</code> -
     * which is not guaranteed to be a valid identifier.
     *
     * {@section Recommanded alternatives}
     * <ul>
     *   <li>If the code of a specific authority is wanted (typically EPSG), then consider
     *       using {@link #getIdentifier(IdentifiedObject, Citation)} instead.</li>
     *   <li>In many cases, the identifier is not specified. For an exhaustive scan of the EPSG
     *       database looking for a match, use one of the lookup methods defined below.</li>
     * </ul>
     *
     * @param  object The identified object, or {@code null}.
     * @return Identifier represented as a string for communication between systems, or {@code null}.
     *
     * @see #getIdentifier(IdentifiedObject, Citation)
     * @see #lookupIdentifier(IdentifiedObject, boolean)
     */
    public static String getIdentifier(final IdentifiedObject object) {
        if (object != null) {
            final Iterator<ReferenceIdentifier> it = iterator(object.getIdentifiers());
            if (it != null) while (it.hasNext()) {
                final String code = toString(it.next());
                if (code != null) { // Paranoiac check.
                    return code;
                }
            }
            final String name = toString(object.getName());
            if (name != null) { // Paranoiac check.
                return name;
            }
        }
        return null;
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
        ReferenceIdentifier id = IdentifiedObjects.getIdentifier(object, authority);
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

    /**
     * Returns {@code true} if either the {@linkplain IdentifiedObject#getName() primary name} or
     * at least one {@linkplain IdentifiedObject#getAlias() alias} matches the specified string.
     * This method performs the search in the following order, regardless of any authority:
     * <p>
     * <ul>
     *   <li>The {@linkplain IdentifiedObject#getName() primary name} of the object</li>
     *   <li>The {@linkplain ScopedName fully qualified name} of an alias</li>
     *   <li>The {@linkplain LocalName local name} of an alias</li>
     * </ul>
     *
     * @param  object The object to check.
     * @param  name The name.
     * @return {@code true} if the primary name of at least one alias
     *         matches the specified {@code name}.
     *
     * @see AbstractIdentifiedObject#nameMatches(String)
     */
    public static boolean nameMatches(final IdentifiedObject object, final String name) {
        if (object instanceof AbstractIdentifiedObject) {
            return ((AbstractIdentifiedObject) object).nameMatches(name);
        } else {
            ensureNonNull("object", object);
            return nameMatches(object, object.getAlias(), name);
        }
    }

    /**
     * Returns {@code true} if the {@linkplain IdentifiedObject#getName() primary name} of an
     * object matches the primary name or one {@linkplain IdentifiedObject#getAlias() alias}
     * of the other object.
     *
     * @param o1 The first object to compare by name.
     * @param o2 The second object to compare by name.
     * @return {@code true} if both objects have a common name.
     */
    public static boolean nameMatches(final IdentifiedObject o1, final IdentifiedObject o2) {
        ensureNonNull("o1", o1);
        ensureNonNull("o2", o2);
        return nameMatches(o1, o2.getName().getCode()) ||
               nameMatches(o2, o1.getName().getCode());
    }

    /**
     * Returns {@code true} if the {@linkplain #getName() primary name} of the given object
     * or one of the given alias matches the given name.
     *
     * @param  object The object to check.
     * @param  alias  The list of alias in {@code object} (may be {@code null}).
     *                This method will never modify this list. Consequently, it
     *                may be a direct reference to an internal array.
     * @param  name   The name for which to check for equality.
     * @return {@code true} if the primary name or at least one alias matches the given {@code name}.
     */
    static boolean nameMatches(final IdentifiedObject object, final Collection<GenericName> alias, String name) {
        name = name.trim();
        if (name.equalsIgnoreCase(object.getName().getCode().trim())) {
            return true;
        }
        if (alias != null) {
            for (GenericName asName : alias) {
                if (asName != null) { // Paranoiac check.
                    asName = asName.toFullyQualifiedName();
                    while (asName != null) {
                        if (name.equalsIgnoreCase(asName.toString().trim())) {
                            return true;
                        }
                        if (!(asName instanceof ScopedName)) {
                            break;
                        }
                        asName = ((ScopedName) asName).tail();
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns a string representation of the given identifier.
     * This method applies the following rules:
     * <p>
     * <ul>
     *   <li>If the given identifier implements the {@link GenericName} interface, then
     *       this method delegates to the {@link GenericName#toString()} method.</li>
     *   <li>Otherwise if the given identifier has a {@linkplain ReferenceIdentifier#getCodeSpace()
     *       code space}, then formats the identifier as "{@code codespace:code}".</li>
     *   <li>Otherwise if the given identifier has an {@linkplain Identifier#getAuthority()
     *       authority}, then formats the identifier as "{@code authority:code}".</li>
     *   <li>Otherwise returns the {@linkplain Identifier#getCode() code}.</li>
     * </ul>
     * <p>
     * This method is provided because the {@link GenericName#toString()} behavior is specified
     * by its javadoc, while {@link ReferenceIdentifier} has no such contract. For example the
     * {@link DefaultIdentifier} implementation provides a WKT-like string representation. This
     * static method can be used when a "name-like" representation is needed for any implementation.
     *
     * @param  identifier The identifier, or {@code null}.
     * @return A string representation of the given identifier, or {@code null}.
     *
     * @see DefaultIdentifier#toString()
     * @see NamedIdentifier#toString()
     *
     * @since 3.20
     */
    public static String toString(final Identifier identifier) {
        if (identifier == null) {
            return null;
        }
        if (identifier instanceof GenericName) {
            // The toString() behavior is specified by the GenericName javadoc.
            return identifier.toString();
        }
        final String code = identifier.getCode();
        if (identifier instanceof ReferenceIdentifier) {
            final String cs = ((ReferenceIdentifier) identifier).getCodeSpace();
            if (cs != null) {
                return cs + DefaultNameSpace.DEFAULT_SEPARATOR + code;
            }
        }
        final String authority = org.apache.sis.internal.util.Citations.getIdentifier(identifier.getAuthority());
        return (authority != null) ? (authority + DefaultNameSpace.DEFAULT_SEPARATOR + code) : code;
    }
}

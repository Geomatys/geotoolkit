/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.referencing.factory.wkt;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.text.ParseException;
import java.awt.RenderingHints;

import org.opengis.util.ScopedName;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.io.wkt.Symbols;
import org.geotoolkit.io.wkt.WKTFormat;
import org.geotoolkit.io.wkt.ReferencingParser;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.util.collection.DerivedSet;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.util.collection.BackingStoreException;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.referencing.factory.DirectAuthorityFactory;
import org.geotoolkit.referencing.factory.IdentifiedObjectFinder;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.lang.ThreadSafe;


/**
 * A CRS Authority Factory that manages object creation by parsing <cite>Well Known Text</cite>
 * (WKT) strings. The strings may be loaded from property files or be queried in a database (for
 * example the {@code "spatial_ref_sys"} table in a PostGIS database).
 * <p>
 * This base implementation expects a map of (<var>code</var>, <var>WKT</var>) entries, where the
 * authority codes are the keys and WKT strings are the values. If the map is backed by a store
 * which may throw checked exceptions (for example a connection to a PostGIS database), then it
 * shall wrap the checked exceptions in {@link BackingStoreException}s.
 *
 * {@section Declaring more than one authority}
 * There is usually only one authority for a given instance of {@code WKTParsingAuthorityFactory},
 * but more authorities can be given to the constructor if the CRS objects to create should have
 * more than one {@linkplain CoordinateReferenceSystem#getIdentifiers identifier}, each with the
 * same code but different namespace. For example a
 * {@linkplain org.geotoolkit.referencing.factory.epsg.EsriExtension factory for CRS defined
 * by ESRI} uses the {@code "ESRI"} namespace, but also the {@code "EPSG"} namespace because
 * those CRS are used as extension of the EPSG database. Consequently the same CRS can be
 * identified as both {@code "ESRI:53001"} and {@code "EPSG:53001"}, where {@code "53001"}
 * is a unused code in the official EPSG database.
 *
 * {@section Caching of CRS objects}
 * This factory doesn't cache any result. Any call to a {@code createFoo} method
 * will trig a new WKT parsing. For adding caching service, this factory should
 * be wrapped in {@link org.geotoolkit.referencing.factory.CachingAuthorityFactory}.
 *
 * @author Jody Garnett (Refractions)
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from 3.00)
 * @module
 */
@ThreadSafe(concurrent = false)
public class WKTParsingAuthorityFactory extends DirectAuthorityFactory {
    /**
     * The authority for this factory. Will be computed by
     * {@link #getAuthority()} when first needed.
     */
    Citation authority;

    /**
     * The authorities for this factory, usually as an array of length 1.
     */
    Citation[] authorities;

    /**
     * The properties object for our properties file. Keys are the authority
     * code for a coordinate reference system and the associated value is a
     * WKT string for the CRS.
     */
    final Map<String,String> definitions;

    /**
     * An unmodifiable view of the authority keys.
     * Will be created when first needed.
     */
    private transient Set<String> codes;

    /**
     * Views of {@link #codes} for different types. Views will be constructed only when first
     * needed. View are always up to date even if entries are added or removed in the
     * {@linkplain #definitions} map.
     */
    private transient Map<Class<? extends IdentifiedObject>, Set<String>> filteredCodes;

    /**
     * A WKT parser. Will be created when first needed.
     */
    private transient Parser parser;

    /**
     * Creates a factory for the specified authorities using the definitions in the given map.
     * There is usually only one authority, but more can be given when the objects to create
     * should have more than one {@linkplain CoordinateReferenceSystem#getIdentifiers identifier},
     * each with the same code but different namespace. See the <a href="#skip-navbar_top">class
     * javadoc</a> for more details.
     *
     * @param userHints
     *          An optional set of hints, or {@code null} for the default ones.
     * @param definitions
     *          The object definitions as a map with authority codes as keys and WKT strings as values.
     * @param authorities
     *          The organizations or parties responsible for definition and maintenance of the database.
     */
    public WKTParsingAuthorityFactory(final Hints userHints, final Map<String,String> definitions, Citation... authorities) {
        this(userHints, definitions);
        ensureNonNull("authorities", authorities);
        if (authorities.length == 0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.EMPTY_ARRAY));
        }
        this.authorities = authorities = authorities.clone();
        for (final Citation authority : authorities) {
            ensureNonNull("authority", authority);
        }
    }

    /**
     * Creates a factory without authorities. This is subclass responsability to initialize
     * the {@link #authorities} field when first needed.
     */
    WKTParsingAuthorityFactory(final Hints userHints, final Map<String,String> definitions) {
        super(userHints);
        ensureNonNull("definitions", definitions);
        this.definitions = definitions;
        copyRelevantHints(userHints, hints);
    }

    /**
     * Copies only the relevant hints from {@code userHints} into the given {@code hints} map.
     */
    static void copyRelevantHints(final Hints userHints, final Map<RenderingHints.Key, Object> hints) {
        Boolean forceXY = Boolean.FALSE;
        if (userHints != null) {
            forceXY = (Boolean) userHints.get(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER);
            if (forceXY == null) {
                forceXY = (Boolean) userHints.get(Hints.FORCE_STANDARD_AXIS_DIRECTIONS);
                if (forceXY == null) {
                    forceXY = Boolean.FALSE;  // By default AXIS elements in WKT are honored.
                }
            }
        }
        /*
         * The two first hints must be set to the same value because current ReferencingParser
         * implementation can not handle them in different way. The last hint is inconditional
         * because units are always taken in account - the WKT format specifies them outside the
         * AXIS[...] elements and includes them in the enclosing GEOCS or PROJCS element instead.
         */
        hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, forceXY);
        hints.put(Hints.FORCE_STANDARD_AXIS_DIRECTIONS,   forceXY);
        hints.put(Hints.FORCE_STANDARD_AXIS_UNITS,  Boolean.FALSE);
    }

    /**
     * Returns whatever this factory is ready for use. The factory is considered ready if
     * the map given at construction time is not empty and the factory has not yet been
     * {@linkplain #dispose disposed}.
     *
     * @since 3.03
     */
    @Override
    public ConformanceResult availability() {
        return new Availability() {
            @Override public boolean pass() {
                synchronized (WKTParsingAuthorityFactory.this) {
                    return super.pass() && !definitions.isEmpty();
                }
            }
        };
    }

    /**
     * Returns all authority names. The default implementation returns the authority given
     * to the constructor. This method <strong>must</strong> be overriden if the subclass
     * used the constructor without authority list. This method is not allowed to return
     * {@code null}.
     *
     * @return All authorities known to this factory.
     */
    Citation[] getAuthorities() {
        if (authorities == null) {
            throw new IllegalStateException(Errors.format(Errors.Keys.DISPOSED_FACTORY));
        }
        return authorities;
    }

    /**
     * Returns the authority. The default implementation returns the first citation given to
     * the constructor, or a modified version of that citation if many of them were given to
     * the constructor. In the later case, the returned citation will have a set of
     * {@linkplain Citation#getIdentifiers() identifiers} which is the union of identifiers
     * of all citations given to the constructor.
     */
    @Override
    public synchronized Citation getAuthority() {
        if (authority == null) {
            final Citation[] authorities = getAuthorities();
            switch (authorities.length) {
                case 0: authority = Citations.EPSG; break;
                case 1: authority = authorities[0]; break;
                default: {
                    final DefaultCitation c = new DefaultCitation(authorities[0]);
                    final Collection<Identifier> identifiers = c.getIdentifiers();
                    for (int i=1; i<authorities.length; i++) {
                        identifiers.addAll(authorities[i].getIdentifiers());
                    }
                    c.freeze();
                    authority = c;
                    break;
                }
            }
        }
        return authority;
    }

    /**
     * Returns the set of authority codes of the given type. The {@code type} argument specifies
     * the base class. For example if this factory is an instance of {@link CRSAuthorityFactory},
     * then:
     * <p>
     * <ul>
     *  <li>{@code CoordinateReferenceSystem.class} asks for all authority codes accepted by
     *      {@link #createGeographicCRS createGeographicCRS},
     *      {@link #createProjectedCRS  createProjectedCRS},
     *      {@link #createVerticalCRS   createVerticalCRS},
     *      {@link #createTemporalCRS   createTemporalCRS}
     *       and any other method returning a sub-type of {@code CoordinateReferenceSystem}.</li>
     *  <li>{@code ProjectedCRS.class} asks only for authority codes accepted by
     *      {@link #createProjectedCRS createProjectedCRS}.</li>
     * </ul>
     * <p>
     * The default implementation filters the set of codes based on the
     * {@code "PROJCS"} and {@code "GEOGCS"} at the start of the WKT strings.
     *
     * @param  type The spatial reference objects type (can be {@code IdentifiedObject.class}).
     * @return The set of authority codes for spatial reference objects of the given type.
     *         If this factory doesn't contains any object of the given type, then this method
     *         returns an empty set.
     * @throws FactoryException if access to the underlying database failed.
     */
    @Override
    public synchronized Set<String> getAuthorityCodes(final Class<? extends IdentifiedObject> type)
            throws FactoryException
    {
        if (codes == null) {
            codes = Collections.unmodifiableSet(definitions.keySet());
        }
        if (type == null || type.isAssignableFrom(IdentifiedObject.class)) {
            return codes;
        }
        if (filteredCodes == null) {
            filteredCodes = new HashMap<Class<? extends IdentifiedObject>, Set<String>>();
        }
        Set<String> filtered = filteredCodes.get(type);
        if (filtered == null) {
            filtered = new Codes(type);
            filteredCodes.put(type, filtered);
        }
        return filtered;
    }

    /**
     * The set of codes for a specific type of CRS. This set filters the codes set in the
     * enclosing {@link WKTParsingAuthorityFactory} in order to keep only the codes for the
     * specified type. Filtering is performed on the fly. Consequently, this set is cheap
     * if the user just wants to check for the existence of a particular code.
     */
    private final class Codes extends DerivedSet<String, String> {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 2681905294171687900L;

        /**
         * The spatial reference objects type.
         */
        private final Class<? extends IdentifiedObject> type;

        /**
         * Constructs a set of codes for the specified type.
         */
        public Codes(final Class<? extends IdentifiedObject> type) {
            super(definitions.keySet(), String.class);
            this.type = type;
        }

        /**
         * Returns the code if the associated key is of the expected type, or {@code null}
         * otherwise.
         */
        @Override
        protected String baseToDerived(final String key) {
            final String wkt;
            try {
                wkt = definitions.get(getPrimaryKey(type, key));
            } catch (FactoryException e) {
                throw new BackingStoreException(e);
            }
            final int length = wkt.length();
            int i=0; while (i<length && Character.isJavaIdentifierPart(wkt.charAt(i))) i++;
            Class<?> candidate = WKTFormat.getClassOf(wkt.substring(0,i));
            if (candidate == null) {
                candidate = IdentifiedObject.class;
            }
            return type.isAssignableFrom(candidate) ? key : null;
        }

        /**
         * Transforms a value in this set to a value in the base set.
         */
        @Override
        protected String derivedToBase(final String element) {
            return element;
        }
    }

    /**
     * Returns the Well Know Text from a code.
     *
     * @param  type The type of the object to be created.
     * @param  code Value allocated by authority.
     * @return The Well Know Text (WKT) for the specified code.
     * @throws NoSuchAuthorityCodeException if the specified {@code code} was not found.
     */
    private String getWKT(final Class<? extends IdentifiedObject> type, final String code)
            throws FactoryException
    {
        assert Thread.holdsLock(this);
        ensureNonNull("code", code);
        final String wkt;
        try {
            wkt = definitions.get(getPrimaryKey(type, code));
        } catch (BackingStoreException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof FactoryException) {
                throw (FactoryException) cause;
            }
            throw databaseFailure(type, code, cause);
        }
        if (wkt == null) {
            throw noSuchAuthorityCode(type, code);
        }
        return wkt.trim();
    }

    /**
     * Wraps an {@link Exception} into a {@link FactoryException}.
     * The given exception is typically a {@link java.sql.SQLException}.
     *
     * @param  type The type of the object being created, or {@code null} if unknown.
     * @param  code The code of the object being created, or {@code null} if unknown.
     * @param  exception The exception that occured while querying the backing store.
     * @return A factory exception wrapping the given exception.
     */
    static FactoryException databaseFailure(final Class<?> type, final String code, final Throwable exception) {
        String message = exception.getLocalizedMessage();
        if (code != null) {
            String typeName;
            if (type != null) {
                typeName = type.getSimpleName();
            } else {
                typeName = Vocabulary.format(Vocabulary.Keys.UNKNOW);
            }
            message = Errors.format(Errors.Keys.DATABASE_FAILURE_$2, typeName, code) + ": " + message;
        }
        return new FactoryException(message, exception);
    }

    /**
     * Gets a description of the object corresponding to a code.
     *
     * @param  code Value allocated by authority.
     * @return A description of the object, or {@code null} if the object
     *         corresponding to the specified {@code code} has no description.
     * @throws NoSuchAuthorityCodeException if the specified {@code code} was not found.
     * @throws FactoryException if the query failed for some other reason.
     */
    @Override
    public InternationalString getDescriptionText(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        final String wkt;
        synchronized (this) {
            wkt = getWKT(IdentifiedObject.class, code);
        }
        int start = wkt.indexOf('"');
        if (start >= 0) {
            final int end = wkt.indexOf('"', ++start);
            if (end >= 0) {
                return new SimpleInternationalString(wkt.substring(start, end).trim());
            }
        }
        return null;
    }

    /**
     * Returns the parser.
     */
    private Parser getParser() {
        if (parser == null) {
            parser = new Parser();
            parser.setAxisIgnored(Boolean.TRUE.equals(hints.get(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER)));
        }
        return parser;
    }

    /**
     * Returns an arbitrary object from a code. If the object type is know at compile time,
     * it is recommended to invoke the most precise method instead of this one.
     *
     * @param  code Value allocated by authority.
     * @throws NoSuchAuthorityCodeException if the specified {@code code} was not found.
     * @throws FactoryException if the object creation failed for some other reason.
     */
    @Override
    public synchronized IdentifiedObject createObject(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        final String wkt = getWKT(IdentifiedObject.class, code);
        final Parser parser = getParser();
        try {
            parser.code = code;
            return (IdentifiedObject) parser.parseObject(wkt);
        } catch (ParseException exception) {
            throw new FactoryException(exception);
        }
    }

    /**
     * Returns a coordinate reference system from a code. If the object type is know at compile
     * time, it is recommended to invoke the most precise method instead of this one.
     *
     * @param  code Value allocated by authority.
     * @throws NoSuchAuthorityCodeException if the specified {@code code} was not found.
     * @throws FactoryException if the object creation failed for some other reason.
     */
    @Override
    public synchronized CoordinateReferenceSystem createCoordinateReferenceSystem(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        final String wkt = getWKT(CoordinateReferenceSystem.class, code);
        final Parser parser = getParser();
        try {
            parser.code = code;
            // parseCoordinateReferenceSystem provides a slightly faster path than parseObject.
            return parser.parseCoordinateReferenceSystem(wkt);
        } catch (ParseException exception) {
            throw new FactoryException(exception);
        }
    }

    /**
     * Trims the authority scope, if presents. If more than one authority were given at
     * {@linkplain #WKTParsingAuthorityFactory construction time}, then any of them may
     * appears as the scope in the supplied code.
     *
     * @param  code The code to trim.
     * @return The code without the authority scope.
     */
    @Override
    protected String trimAuthority(String code) {
        code = code.trim();
        final GenericName name  = nameFactory.parseGenericName(null, code);
        if (name instanceof ScopedName) {
            final GenericName scope = ((ScopedName) name).path();
            final String candidate = scope.toString();
            final Citation[] authorities = getAuthorities();
            for (int i=0; i<authorities.length; i++) {
                if (Citations.identifierMatches(authorities[i], candidate)) {
                    return name.tip().toString().trim();
                }
            }
        }
        return code;
    }

    /**
     * Returns the primary key for the specified authority code. The default implementation
     * returns the given code with the "authority" part trimmed. This method is overriden by
     * {@link PostgisAuthorityFactory}. Note that {@code PostgisAuthorityFactory} will trim
     * the authority itself, because it needs the authority part of the code.
     *
     * @param  type The type of the object being created.
     * @param  code The authority code to convert to primary key value.
     * @return The primary key for the supplied code.
     * @throws FactoryException if an error occured while querying the database.
     */
    Object getPrimaryKey(Class<? extends IdentifiedObject> type, String code) throws FactoryException {
        return trimAuthority(code);
    }

    /**
     * Returns a finder which can be used for looking up unidentified objects.
     *
     * @throws FactoryException if the finder can not be created.
     */
    @Override
    public synchronized IdentifiedObjectFinder getIdentifiedObjectFinder(
            final Class<? extends IdentifiedObject> type) throws FactoryException
    {
        final Parser parser = getParser();
        if (!parser.isAxisIgnored()) {
            return super.getIdentifiedObjectFinder(type);
        }
        return new Finder(type);
    }

    /**
     * A {@link IdentifiedObjectFinder} which tests CRS in standard axis order in addition
     * of the "longitude first" axis order.
     */
    private final class Finder extends IdentifiedObjectFinder {
        /**
         * Creates a finder for the enclosing backing store.
         */
        Finder(final Class<? extends IdentifiedObject> type) {
            super(WKTParsingAuthorityFactory.this, type);
        }

        /**
         * Creates an object from the given code.
         *
         * @throws FactoryException if an error occured while creating the object.
         */
        @Override
        protected final IdentifiedObject create(final String code, final int attempt) throws FactoryException {
            switch (attempt) {
                case 0: {
                    return super.create(code, attempt);
                }
                case 1: {
                    synchronized (WKTParsingAuthorityFactory.this) {
                        final Parser parser = getParser();
                        assert parser.isAxisIgnored();
                        try {
                            parser.setAxisIgnored(false);
                            return super.create(code, 0);
                        } finally {
                            parser.setAxisIgnored(true);
                        }
                    }

                }
                default: {
                    return null;
                }
            }
        }
    }

    /**
     * The WKT parser for this authority factory. This parser add automatically the authority
     * code if it was not explicitly specified in the WKT.
     */
    private final class Parser extends ReferencingParser {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -5910561042299146066L;

        /**
         * The authority code for the WKT to be parsed.
         */
        String code;

        /**
         * Creates the parser.
         */
        public Parser() {
            super(Symbols.DEFAULT, factories);
        }

        /**
         * Adds the authority code to the specified properties, if not already present.
         */
        @Override
        protected Map<String,Object> alterProperties(Map<String,Object> properties) {
            Object candidate = properties.get(IdentifiedObject.IDENTIFIERS_KEY);
            if (candidate == null && code != null) {
                properties = new HashMap<String,Object>(properties);
                code = trimAuthority(code);
                final Object identifiers;
                final Citation[] authorities = getAuthorities();
                if (authorities.length == 1) {
                    identifiers = new NamedIdentifier(authorities[0], code);
                } else {
                    final NamedIdentifier[] ids = new NamedIdentifier[authorities.length];
                    for (int i=0; i<ids.length; i++) {
                        ids[i] = new NamedIdentifier(authorities[i], code);
                    }
                    identifiers = ids;
                }
                properties.put(IdentifiedObject.IDENTIFIERS_KEY, identifiers);
            }
            return super.alterProperties(properties);
        }
    }

    /**
     * Releases resources immediately instead of waiting for the garbage collector.
     */
    @Override
    protected synchronized void dispose(final boolean shutdown) {
        authority     = null;
        authorities   = null;
        codes         = null;
        filteredCodes = null;
        parser        = null;
        super.dispose(shutdown);
    }
}

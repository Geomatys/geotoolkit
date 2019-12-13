/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.referencing.factory.wkt;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import org.apache.sis.internal.simple.SimpleCitation;
import org.opengis.util.ScopedName;
import org.opengis.util.GenericName;
import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.util.collection.DerivedSet;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.util.collection.BackingStoreException;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.referencing.datum.BursaWolfParameters;
import org.geotoolkit.referencing.factory.DirectAuthorityFactory;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;
import org.apache.sis.referencing.factory.GeodeticObjectFactory;
import org.apache.sis.util.ArraysExt;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.DerivedCRS;
import org.opengis.referencing.crs.EngineeringCRS;
import org.opengis.referencing.crs.GeocentricCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.EngineeringDatum;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.referencing.datum.VerticalDatum;
import org.opengis.referencing.operation.MathTransform;


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
 * @version 3.14
 *
 * @since 3.10 (derived from 3.00)
 * @module
 */
public class WKTParsingAuthorityFactory extends DirectAuthorityFactory {

    /**
     * The mapping between WKT element name and the object class to be created.
     * Keys must be upper case.
     */
    private static final Map<String,Class<?>> TYPES;
    static {
        final Map<String,Class<?>> map = new LinkedHashMap<>(25);
        map.put(        "GEOGCS",        GeographicCRS.class);
        map.put(        "PROJCS",         ProjectedCRS.class);
        map.put(        "GEOCCS",        GeocentricCRS.class);
        map.put(       "VERT_CS",          VerticalCRS.class);
        map.put(      "LOCAL_CS",       EngineeringCRS.class);
        map.put(      "COMPD_CS",          CompoundCRS.class);
        map.put(     "FITTED_CS",           DerivedCRS.class);
        map.put(          "AXIS", CoordinateSystemAxis.class);
        map.put(        "PRIMEM",        PrimeMeridian.class);
        map.put(       "TOWGS84",  BursaWolfParameters.class);
        map.put(      "SPHEROID",            Ellipsoid.class);
        map.put(    "VERT_DATUM",        VerticalDatum.class);
        map.put(   "LOCAL_DATUM",     EngineeringDatum.class);
        map.put(         "DATUM",        GeodeticDatum.class);
        map.put(      "PARAM_MT",        MathTransform.class);
        map.put(     "CONCAT_MT",        MathTransform.class);
        map.put(    "INVERSE_MT",        MathTransform.class);
        map.put("PASSTHROUGH_MT",        MathTransform.class);
        TYPES = map;
    }

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
     * @param definitions
     *          The object definitions as a map with authority codes as keys and WKT strings as values.
     * @param authorities
     *          The organizations or parties responsible for definition and maintenance of the database.
     */
    public WKTParsingAuthorityFactory(final Map<String,String> definitions, Citation... authorities) {
        this(definitions);
        ensureNonNull("authorities", authorities);
        if (authorities.length == 0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.EmptyArray));
        }
        this.authorities = authorities = authorities.clone();
        for (final Citation authority : authorities) {
            ensureNonNull("authority", authority);
        }
    }

    /**
     * Creates a factory without authorities. This is subclass responsibility to initialize
     * the {@link #authorities} field when first needed.
     */
    WKTParsingAuthorityFactory(final Map<String,String> definitions) {
        ensureNonNull("definitions", definitions);
        this.definitions = definitions;
    }

    /**
     * Returns all authority names. The default implementation returns the authority given
     * to the constructor. This method <strong>must</strong> be overridden if the subclass
     * used the constructor without authority list. This method is not allowed to return
     * {@code null}.
     * <p>
     * The returned array shall contain the value returned by
     * {@linkplain #getPrimaryKeyAuthority()}, if non-null.
     *
     * @return All authorities known to this factory. <strong>Do not modify</strong>,
     *         since this method returns a direct reference to the internal array.
     */
    Citation[] getAuthorities() {
        if (authorities == null) {
            throw new IllegalStateException(Errors.format(Errors.Keys.DisposedFactory));
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
            Citation[] authorities = getAuthorities();
            final Citation pkAuthority = getPrimaryKeyAuthority();
            if (pkAuthority != null) {
                for (int i=0; i<authorities.length; i++) {
                    if (pkAuthority.equals(authorities[i])) {
                        authorities = ArraysExt.remove(authorities, i, 1);
                        break;
                    }
                }
            }
            switch (authorities.length) {
                case 0: authority = new SimpleCitation("Unknown"); break;
                case 1: authority = authorities[0]; break;
                default: {
                    final DefaultCitation c = new DefaultCitation(authorities[0]);
                    final Collection<Identifier> identifiers = c.getIdentifiers();
                    for (int i=1; i<authorities.length; i++) {
                        identifiers.addAll(authorities[i].getIdentifiers());
                    }
                    c.transitionTo(DefaultCitation.State.FINAL);
                    authority = c;
                    break;
                }
            }
        }
        return authority;
    }

    /**
     * Returns the authority which is responsible for the maintenance of the primary keys,
     * or {@code null} if none. The returned value (if non-null) shall also be one of the
     * elements returned by {@link #getAuthorities()}.
     * <p>
     * The default implementation returns {@code null} in all cases. This method is overridden
     * and made public by implementations that are backed by a SQL database. For example the
     * {@link DirectPostgisFactory} overrides this method in order to return
     * {@link Citations#POSTGIS}.
     *
     * @return The authority which is reponsible for the maintenance of primary keys,
     *         or {@code null} if none.
     *
     * @see #getPrimaryKey(Class, String)
     */
    Citation getPrimaryKeyAuthority() {
        return null;
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
            filteredCodes = new HashMap<>();
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
            Class<?> candidate = getWKTClassOf(wkt.substring(0,i));
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
     * @param  parser If non-null, code and primary key will be stored in this parser.
     * @return The Well Know Text (WKT) for the specified code.
     * @throws NoSuchAuthorityCodeException if the specified {@code code} was not found.
     */
    private String getWKT(final Class<? extends IdentifiedObject> type, final String code, final Parser parser)
            throws FactoryException
    {
        assert Thread.holdsLock(this);
        ensureNonNull("code", code);
        final Comparable<?> pk;
        final String wkt;
        try {
            pk = getPrimaryKey(type, code);
            wkt = definitions.get(pk);
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
        if (parser != null) {
            parser.code = code;
            parser.primaryKey = pk;
        }
        return wkt.trim();
    }

    /**
     * Wraps an {@link Exception} into a {@link FactoryException}.
     * The given exception is typically a {@link java.sql.SQLException}.
     *
     * @param  type The type of the object being created, or {@code null} if unknown.
     * @param  code The code of the object being created, or {@code null} if unknown.
     * @param  exception The exception that occurred while querying the backing store.
     * @return A factory exception wrapping the given exception.
     */
    static FactoryException databaseFailure(final Class<?> type, final String code, final Throwable exception) {
        String message = exception.getLocalizedMessage();
        if (code != null) {
            String typeName;
            if (type != null) {
                typeName = type.getSimpleName();
            } else {
                typeName = Vocabulary.format(Vocabulary.Keys.Unknown);
            }
            message = Errors.format(Errors.Keys.DatabaseFailure_2, typeName, code) + ": " + message;
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
            wkt = getWKT(IdentifiedObject.class, code, null);
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
        return createCoordinateReferenceSystem(code);
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
        final Parser parser = getParser();
        final String wkt = getWKT(CoordinateReferenceSystem.class, code, parser);
        return parser.createFromWKT(wkt);
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
     * returns the given code with the "authority" part trimmed. This method is overridden by
     * {@link DirectPostgisFactory}. Note that {@code DirectPostgisFactory} will trim
     * the authority itself, because it needs the authority part of the code.
     *
     * @param  type The type of the object being created.
     * @param  code The authority code to convert to primary key value.
     * @return The primary key for the supplied code.
     * @throws FactoryException if an error occurred while querying the database.
     *
     * @see #getPrimaryKeyAuthority()
     */
    Comparable<?> getPrimaryKey(Class<? extends IdentifiedObject> type, String code) throws FactoryException {
        return trimAuthority(code);
    }

    /**
     * Returns the class of the specified WKT element. For example this method returns
     * <code>{@linkplain ProjectedCRS}.class</code> for element "{@code PROJCS}".
     * <p>
     * This method is the converse of {@link #getNameOf}.
     *
     * @param  element The WKT element name.
     * @return The GeoAPI class of the specified element, or {@code null} if unknown.
     */
    static Class<?> getWKTClassOf(String element) {
        if (element == null) {
            return null;
        }
        element = element.trim().toUpperCase(Locale.US);
        final Class<?> type = TYPES.get(element);
        assert type == null || type.equals(MathTransform.class) || element.equals(getWKTNameOf(type)) : type;
        return type;
    }

    /**
     * Returns the WKT name of the specified object type. For example this method returns
     * "{@code PROJCS}" for type <code>{@linkplain ProjectedCRS}.class</code>.
     * <p>
     * This method is the converse of {@link #getClassOf}.
     *
     * @param type The GeoAPI class of the specified element.
     * @return The WKT element name, or {@code null} if unknown.
     */
    static String getWKTNameOf(final Class<?> type) {
        if (type != null) {
            for (final Map.Entry<String,Class<?>> entry : TYPES.entrySet()) {
                final Class<?> candidate = entry.getValue();
                if (candidate.isAssignableFrom(type)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * The WKT parser for this authority factory. This parser add automatically the authority
     * code if it was not explicitly specified in the WKT.
     */
    private final class Parser extends GeodeticObjectFactory {
        /**
         * The authority code for the WKT to be parsed. This is the code provided by the user to
         * the {@link WKTParsingAuthorityFactory#createCoordinateReferenceSystem(String)} method.
         */
        String code;

        /**
         * The primary key corresponding to the code. For {@link PropertyAuthorityFactory},
         * this is equals to {@link #code}. For {@link DirectPostgisFactory}, this is an
         * {@link Integer} obtained from the {@code srid} column.
         */
        Comparable<?> primaryKey;

        /**
         * Creates the parser.
         */
        public Parser() {
        }

        /**
         * Adds the authority code to the specified properties, if not already present.
         * In addition, if a primary key was used for fetching the CRS, adds also that
         * primary key to the list of identifiers.
         */
        @Override
        protected Map<String,?> complete(Map<String,?> properties) {
            final Citation pkAuthority = getPrimaryKeyAuthority();
            final Citation[] authorities;
            final Identifier[] identifiers;
            final Identifier declaredIdentifier =
                    (Identifier) properties.get(IdentifiedObject.IDENTIFIERS_KEY);
            /*
             * If the WKT does not declare explicitly an authority code, we will adds an
             * identifier for all authorities given at construction time. Otherwise we
             * will add an identifier only for the primary key (if there is one).
             */
            if (declaredIdentifier == null) {
                authorities = getAuthorities();
                identifiers = new NamedIdentifier[authorities.length];
            } else if (pkAuthority != null) {
                authorities = new Citation[] {pkAuthority};
                identifiers = new Identifier[2];
                identifiers[0] = declaredIdentifier;
            } else {
                authorities = null;
                identifiers = null;
            }
            /*
             * Now create an identifier for each authority in the 'authorities' array.
             * Note that the 'identifiers' array may be longer, in which case the first
             * elements are assumed already initialized.
             */
            if (authorities != null) {
                String trimmedCode = null, pkCode = null;
                final int offset = identifiers.length - authorities.length;
                for (int i=0; i<authorities.length; i++) {
                    final String ci;
                    final Citation authority = authorities[i];
                    if (pkAuthority == null || pkAuthority.equals(authority)) {
                        if (pkCode == null) {
                            pkCode = primaryKey.toString();
                        }
                        ci = pkCode;
                    } else {
                        if (trimmedCode == null) {
                            trimmedCode = trimAuthority(code);
                        }
                        ci = trimmedCode;
                    }
                    identifiers[i + offset] = new NamedIdentifier(authority, ci);
                }
                final Map<String,Object> modified = new HashMap<>(properties);
                modified.put(IdentifiedObject.IDENTIFIERS_KEY, identifiers);
                properties = modified;
            }
            return super.complete(properties);
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

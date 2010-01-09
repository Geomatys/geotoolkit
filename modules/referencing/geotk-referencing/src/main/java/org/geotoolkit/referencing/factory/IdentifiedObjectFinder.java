/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.factory;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opengis.util.GenericName;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.internal.Citations;
import org.geotoolkit.naming.DefaultNameSpace;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.logging.Logging;


/**
 * Lookups an object from an {@linkplain AuthorityFactory authority factory} which is
 * {@linkplain CRS#equalsIgnoreMetadata equal, ignoring metadata}, to the specified
 * object. The main purpose of this class is to get a fully {@linkplain IdentifiedObject
 * identified object} from an incomplete one, for example from an object without
 * {@linkplain IdentifiedObject#getIdentifiers identifiers} ("{@code AUTHORITY[...]}"
 * element in <cite>Well Known Text</cite> terminology).
 * <p>
 * The steps for using {@code IdentifiedObjectFinder} are:
 * <p>
 * <ol>
 *   <li>Get a new instance by calling
 *       {@link AbstractAuthorityFactory#getIdentifiedObjectFinder(Class)}.</li>
 *   <li>Optionaly configure that instance by calling its setter methods.</li>
 *   <li>Perform a search by invoking the {@link #find(IdentifiedObject)} or
 *       {@link #findIdentifier(IdentifiedObject)} methods.</li>
 *   <li>Reuse the same {@code IdentifiedObjectFinder} instance for consecutive searchs.</li>
 * </ol>
 *
 * {@section Thread safety}
 * {@code IdentifiedObjectFinder} are <strong>not</strong> garanteed to be thread-safe
 * even if the underlying factory is thread-safe. If concurrent searchs are desired,
 * then a new instance should be created for each thread.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.06
 *
 * @see AbstractAuthorityFactory#getIdentifiedObjectFinder(Class)
 * @see CRS#lookupIdentifier(IdentifiedObject, boolean)
 *
 * @since 2.4
 * @module
 */
public class IdentifiedObjectFinder {
    /**
     * The proxy for objects creation. This is usually set at construction time.
     * But in the particular case of {@link CachingAuthorityFactory#Finder}, this
     * is left to {@code null} and assigned only when a backing store factory is
     * in use.
     * <p>
     * If this field is initialized only when needed, then the following methods
     * must be overriden and ensure that the initialization has been performed:
     * <p>
     * <ul>
     *   <li>{@link #getAuthority}</li>
     *   <li>{@link #getCodeCandidates}</li>
     *   <li>{@link #find} (see note below)</li>
     *   <li>{@link #findIdentifier} (see note below)</li>
     * </ul>
     * <p>
     * Note: the {@code find} methods do not need to be overriden if all other methods
     * are overrided and the {@link #create} method in overrided too.
     */
    AuthorityFactoryProxy proxy;

    /**
     * {@code true} for performing full scans, or {@code false} otherwise.
     */
    private boolean fullScan = true;

    /**
     * Creates a finder using no proxy. The {@link #proxy} field must be
     * assigned by the sub-class before any method in this class is used.
     */
    IdentifiedObjectFinder() {
    }

    /**
     * Creates a finder using the specified factory. This constructor is
     * protected because instances of this class should not be created directly.
     * Use {@link AbstractAuthorityFactory#getIdentifiedObjectFinder(Class)} instead.
     *
     * @param factory The factory to scan for the identified objects.
     * @param type    The type of objects to lookup.
     *
     * @see AbstractAuthorityFactory#getIdentifiedObjectFinder(Class)
     */
    protected IdentifiedObjectFinder(final AuthorityFactory factory,
            final Class<? extends IdentifiedObject> type)
    {
        proxy = AuthorityFactoryProxy.getInstance(factory, type);
    }

    /**
     * Returns the type of the objects to be created by the proxy instance.
     * This method needs to be overriden by the sub-classes that do not define a proxy.
     */
    Class<? extends IdentifiedObject> getObjectType() {
        return proxy.getObjectType();
    }

    /*
     * Do NOT provide the following method:
     *
     *     public AuthorityFactory getAuthorityFactory() {
     *         return proxy.getAuthorityFactory();
     *     }
     *
     * because the returned factory may not be the one the user would expect. Some of our
     * AbstractAuthorityFactory implementations create proxy to the underlying backing
     * store rather than to the factory on which 'getIdentifiedObjectFinder()' was invoked.
     */

    /**
     * Returns the authority of the factory examined by this finder.
     *
     * @return The authority of the factory used for the searches.
     * @throws FactoryException If an error occured while fetching the authority.
     */
    public Citation getAuthority() throws FactoryException {
        return proxy.getAuthorityFactory().getAuthority();
    }

    /**
     * Copies the configuration of the given finder. This method provides a central place
     * where to add call to setters methods if such methods are added in a future version.
     */
    final void copyConfiguration(final IdentifiedObjectFinder other) {
        setFullScanAllowed(other.isFullScanAllowed());
    }

    /**
     * If {@code true}, an exhaustive full scan against all registered objects will be performed
     * (may be slow). Otherwise only a fast lookup based on embedded identifiers and names will
     * be performed. The default value is {@code true}.
     *
     * @return {@code true} if exhaustive scans are allowed, or {@code false} for faster
     *         but less complete searches.
     */
    public boolean isFullScanAllowed() {
        return fullScan;
    }

    /**
     * Sets whatever an exhaustive scan against all registered objects is allowed.
     * The default value is {@code true}.
     *
     * @param fullScan {@code true} for allowing exhaustive scans, or {@code false}
     *        for faster but less complete searches.
     */
    public void setFullScanAllowed(final boolean fullScan) {
        this.fullScan = fullScan;
    }

    /**
     * Lookups an object which is
     * {@linkplain CRS#equalsIgnoreMetadata equal, ignoring metadata}, to the
     * specified object. The default implementation tries to instantiate some
     * {@linkplain IdentifiedObject identified objects} from the authority factory
     * specified at construction time, in the following order:
     * <p>
     * <ul>
     *   <li>If the specified object contains {@linkplain IdentifiedObject#getIdentifiers
     *       identifiers} associated to the same authority than the factory, then those
     *       identifiers are used for {@linkplain AuthorityFactory#createObject creating
     *       objects} to be tested.</li>
     *   <li>If the authority factory can create objects from their {@linkplain
     *       IdentifiedObject#getName name} in addition of identifiers, then the name and
     *       {@linkplain IdentifiedObject#getAlias aliases} are used for creating objects
     *       to be tested.</li>
     *   <li>If {@linkplain #isFullScanAllowed full scan is allowed}, then full
     *       {@linkplain #getCodeCandidates set of authority codes} are used for
     *       creating objects to be tested.</li>
     * </ul>
     * <p>
     * The first of the above created objects which is equal to the specified object in the
     * the sense of {@link CRS#equalsIgnoreMetadata equalsIgnoreMetadata} is returned.
     *
     * @param  object The object looked up.
     * @return The identified object, or {@code null} if not found.
     * @throws FactoryException if an error occured while creating an object.
     */
    public IdentifiedObject find(final IdentifiedObject object) throws FactoryException {
        /*
         * First check if one of the identifiers can be used to spot directly an
         * identified object (and check it's actually equal to one in the factory).
         */
        IdentifiedObject candidate = createFromIdentifiers(object);
        if (candidate != null) {
            return candidate;
        }
        /*
         * We are unable to find the object from its identifiers. Try a quick name lookup.
         * Some implementations like the one backed by the EPSG database are capable to find
         * an object from its name.
         */
        candidate = createFromNames(object);
        if (candidate != null) {
            return candidate;
        }
        /*
         * Here we exhausted the quick paths. Bail out if the user does not want a full scan.
         */
        return fullScan ? createFromCodes(object) : null;
    }

    /**
     * Returns the identifier of the specified object, or {@code null} if none. The default
     * implementation invokes <code>{@linkplain #find find}(object)</code> and extracts the
     * code from the returned {@linkplain IdentifiedObject identified object}.
     *
     * @param  object The object looked up.
     * @return The identifier of the given object, or {@code null} if none were found.
     * @throws FactoryException if an error occured while creating an object.
     */
    public String findIdentifier(final IdentifiedObject object) throws FactoryException {
        final IdentifiedObject candidate = find(object);
        return (candidate != null) ? getIdentifier(candidate) : null;
    }

    /**
     * Returns the identifier for the specified object. This method is invoked only from
     * {@link #findIdentifier(IdentifiedObject)}, either the method defined above or its
     * overriden implementation defined in {@link CachingAuthorityFactory}.
     *
     * @throws FactoryException If an error occured while fetching the identifier.
     */
    final String getIdentifier(final IdentifiedObject object) throws FactoryException {
        Citation authority = getAuthority();
        if (ReferencingFactory.ALL.equals(authority)) {
            /*
             * "All" is a pseudo-authority declared by AllAuthoritiesFactory. This is not a real
             * authority, so we will not find any identifier if we search for this authority. We
             * will rather pickup the first identifier, regardless its authority.
             */
            authority = null;
        }
        ReferenceIdentifier identifier = AbstractIdentifiedObject.getIdentifier(object, authority);
        if (identifier == null) {
            identifier = object.getName();
            // Should never be null past this point, since 'name' is a mandatory attribute.
        }
        final String code      = identifier.getCode();
        final String codespace = identifier.getCodeSpace();
        if (codespace != null) {
            return codespace + DefaultNameSpace.DEFAULT_SEPARATOR + code;
        } else {
            return code;
        }
    }

    /**
     * Creates an object {@linkplain CRS#equalsIgnoreMetadata equal, ignoring metadata}, to the
     * specified object using only the {@linkplain IdentifiedObject#getIdentifiers identifiers}.
     * If no such object is found, returns {@code null}.
     * <p>
     * This method may be used in order to get a fully identified object from a partially
     * identified one.
     *
     * @param  object The object looked up.
     * @return The identified object, or {@code null} if not found.
     * @throws FactoryException if an error occured while creating an object.
     *
     * @see #createFromCodes
     * @see #createFromNames
     */
    final IdentifiedObject createFromIdentifiers(final IdentifiedObject object) throws FactoryException {
        final Citation authority = getAuthority();
        final boolean isAll = ReferencingFactory.ALL.equals(authority);
        for (final Identifier id : object.getIdentifiers()) {
            if (!isAll && !Citations.identifierMatches(authority, id.getAuthority())) {
                // The identifier is not for this authority. Looks the other ones.
                continue;
            }
            final String code = id.toString();
            for (int n=0; ; n++) {
                final IdentifiedObject candidate;
                try {
                    candidate = create(code, n);
                } catch (NoSuchAuthorityCodeException e) {
                    // The identifier was not recognized. No problem, let's go on.
                    break;
                }
                if (candidate == null) break;
                if (CRS.equalsIgnoreMetadata(candidate, object)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Creates an object {@linkplain CRS#equalsIgnoreMetadata equal, ignoring metadata}, to
     * the specified object using only the {@linkplain IdentifiedObject#getName name} and
     * {@linkplain IdentifiedObject#getAlias aliases}. If no such object is found, returns
     * {@code null}.
     * <p>
     * This method may be used with some {@linkplain AuthorityFactory authority factory}
     * implementations like the one backed by the EPSG database, which are capable to find
     * an object from its name when the identifier is unknown.
     *
     * @param  object The object looked up.
     * @return The identified object, or {@code null} if not found.
     * @throws FactoryException if an error occured while creating an object.
     *
     * @see #createFromCodes
     * @see #createFromIdentifiers
     */
    final IdentifiedObject createFromNames(final IdentifiedObject object) throws FactoryException {
        String code = object.getName().getCode();
        for (int n=0; ; n++) {
            final IdentifiedObject candidate;
            try {
                candidate = create(code, n);
            } catch (FactoryException e) {
                /*
                 * The identifier was not recognized. No problem, let's go on.
                 * Note: we catch a more generic exception than NoSuchAuthorityCodeException
                 *       because this attempt may fail for various reasons (character string
                 *       not supported by the underlying database for primary key, duplicated
                 *       name found, etc.).
                 */
                break;
            }
            if (candidate == null) break;
            if (CRS.equalsIgnoreMetadata(candidate, object)) {
                return candidate;
            }
        }
        for (final GenericName id : object.getAlias()) {
            code = id.toString();
            for (int n=0; ; n++) {
                final IdentifiedObject candidate;
                try {
                    candidate = create(code, n);
                } catch (FactoryException e) {
                    // The name was not recognized. No problem, let's go on.
                    break;
                }
                if (candidate == null) break;
                if (CRS.equalsIgnoreMetadata(candidate, object)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Creates an object {@linkplain CRS#equalsIgnoreMetadata equal, ignoring metadata}, to the
     * specified object. This method scans the {@linkplain #getAuthorityCodes authority codes},
     * create the objects and returns the first one which is equal to the specified object in
     * the sense of {@link CRS#equalsIgnoreMetadata equalsIgnoreMetadata}.
     * <p>
     * This method may be used in order to get a fully {@linkplain IdentifiedObject identified
     * object} from an object without {@linkplain IdentifiedObject#getIdentifiers identifiers}.
     * <p>
     * Scaning the whole set of authority codes may be slow. Users should try
     * <code>{@linkplain #createFromIdentifiers createFromIdentifiers}(object)</code> and/or
     * <code>{@linkplain #createFromNames createFromNames}(object)</code> before to fallback
     * on this method.
     *
     * @param  object The object looked up.
     * @return The identified object, or {@code null} if not found.
     * @throws FactoryException if an error occured while scanning through authority codes.
     *
     * @see #createFromIdentifiers
     * @see #createFromNames
     */
    final IdentifiedObject createFromCodes(final IdentifiedObject object) throws FactoryException {
        final Set<String> codes = getCodeCandidates(object);
        if (!codes.isEmpty()) {
            for (final String code : codes) {
                for (int n=0; ; n++) {
                    final IdentifiedObject candidate;
                    try {
                        candidate = create(code, n);
                    } catch (FactoryException e) {
                        break;
                    }
                    if (candidate == null) break;
                    if (CRS.equalsIgnoreMetadata(candidate, object)) {
                        return candidate;
                    }
                }
            }
            final Logger logger = Logging.getLogger(IdentifiedObjectFinder.class);
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("No match found for \"" + object.getName() + "\" among " + codes);
            }
        }
        return null;
    }

    /**
     * Creates an object from the given code. This method may be invoked many time if we want
     * to attempt creating many flavors of the same object (for example with "longitude first"
     * axis order forced or not). The {@code attempt} argument gives the number of attempts prior
     * this one. This method shall return {@code null} if no more attempt should be done.
     * <p>
     * The default implementation delegates to the factory only for the first attempt, and returns
     * {@code null} in all other cases. This method is aimed to be overriden, but do NOT override
     * it with caching service. See {@link CachingAuthorityFactory#Finder} for details.
     *
     * @param  code The authority code for which to create an object.
     * @param  count The number of previous attempt before this one.
     * @return The identified object for the given code, or {@code null} to stop attempts.
     * @throws FactoryException if an error occured while creating the object.
     */
    IdentifiedObject create(final String code, final int attempt) throws FactoryException {
        return (attempt == 0) ? proxy.create(code) : null;
    }

    /**
     * Returns a set of authority codes that <strong>may</strong> identify the same object than
     * the specified one. The returned set must contains <em>at least</em> the code of every objects
     * that are {@linkplain CRS#equalsIgnoreMetadata equal, ignoring metadata}, to the specified one.
     * However the set is not required to contains only the codes of those objects; it may
     * conservatively contains the code for more objects if an exact search is too expensive.
     * <p>
     * This method is invoked by the default {@link #find find} method implementation. The caller
     * iterates through the returned codes, instantiate the objects and compare them with the
     * specified one in order to determine which codes are really applicable. The iteration
     * stops as soon as a match is found (in other words, if more than one object is equals to
     * the specified one, then the {@code find} method selects the first one in iteration order).
     *
     * {@section Default implementation}
     * The default implementation returns the same set than
     * <code>{@linkplain AuthorityFactory#getAuthorityCodes getAuthorityCodes}(type)</code>
     * where {@code type} is the interface specified at construction type. Subclasses should
     * override this method in order to return a smaller set, if they can.
     *
     * @param  object The object looked up.
     * @return A set of code candidates.
     * @throws FactoryException if an error occured while fetching the set of code candidates.
     */
    protected Set<String> getCodeCandidates(final IdentifiedObject object) throws FactoryException {
        return proxy.getAuthorityCodes();
    }

    /**
     * Returns a string representation of this finder, for debugging purpose only.
     */
    @Override
    public String toString() {
        if (proxy != null) {
            return proxy.toString(getClass());
        } else {
            return Classes.getShortClassName(this);
        }
    }
}

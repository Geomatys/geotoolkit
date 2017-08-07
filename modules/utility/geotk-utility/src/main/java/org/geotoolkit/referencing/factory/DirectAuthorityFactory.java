/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory;

import java.util.logging.Logger;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;
import org.opengis.util.NameFactory;
import org.opengis.util.ScopedName;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.resources.Errors;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.referencing.factory.GeodeticAuthorityFactory;
import org.apache.sis.util.logging.Logging;


/**
 * The base class for authority factories that create referencing object directly. This is
 * in contrast with other factories like the {@linkplain AuthorityFactoryAdapter adapter}
 * or {@linkplain CachingAuthorityFactory caching} ones, which delegates their work to
 * an other factory.
 *
 * @author Martin Desruisseaux (IRD)
 * @module
 *
 * @deprecated Moved to {@link org.apache.sis.referencing.factory.GeodeticAuthorityFactory} in Apache SIS.
 */
@Deprecated
public abstract class DirectAuthorityFactory extends GeodeticAuthorityFactory {
    /**
     * The logger for event related to Geotk factories.
     */
    public static final Logger LOGGER = Logging.getLogger("org.geotoolkit.referencing.factory");

    /**
     * The name factory to use for creating {@link GenericName}.
     *
     * @since 3.00
     */
    protected final NameFactory nameFactory;

    /**
     * The underlying factories used for objects creation.
     */
    protected final ReferencingFactoryContainer factories;

    protected DirectAuthorityFactory() {
        this((Hints) null);
    }

    /**
     * Constructs an instance using the specified set of factories.
     *
     * @param factories The low-level factories to use.
     */
    protected DirectAuthorityFactory(final ReferencingFactoryContainer factories) {
        ensureNonNull("factories", factories);
        this.factories = factories;
        nameFactory = DefaultFactories.forBuildin(NameFactory.class);
    }

    /**
     * Constructs an instance using the specified hints. This constructor recognizes the
     * {@link Hints#CRS_FACTORY CRS}, {@link Hints#CS_FACTORY CS}, {@link Hints#DATUM_FACTORY DATUM}
     * and {@link Hints#MATH_TRANSFORM_FACTORY MATH_TRANSFORM} {@code FACTORY} hints.
     *
     * @param userHints An optional set of hints, or {@code null} for the default ones.
     */
    protected DirectAuthorityFactory(final Hints userHints) {
        this(ReferencingFactoryContainer.instance(userHints));
    }

    /**
     * Returns the vendor responsible for creating this factory implementation.
     * Many implementations from different vendors may be available for the same
     * factory interface.
     * <p>
     * The default for Geotk implementations is to return
     * {@linkplain Citations#GEOTOOLKIT Geotoolkit.org}.
     *
     * @return The vendor for this factory implementation.
     *
     * @see Citations#GEOTOOLKIT
     */
    @Override
    public Citation getVendor() {
        return getClass().getName().startsWith("org.geotoolkit.") ? org.geotoolkit.metadata.Citations.GEOTOOLKIT : org.geotoolkit.metadata.Citations.UNKNOWN;
    }

    /**
     * Returns the organization or party responsible for definition and maintenance of the database.
     */
    @Override
    public abstract Citation getAuthority();

    /**
     * Returns a description of the underlying backing store, or {@code null} if unknown.
     * This is for example the database software used for storing the data.
     * The default implementation returns always {@code null}.
     *
     * @return The description of the underlying backing store, or {@code null}.
     * @throws FactoryException if a failure occurs while fetching the engine description.
     */
    public String getBackingStoreDescription() throws FactoryException {
        return null;
    }

    /**
     * Releases resources immediately instead of waiting for the garbage collector.
     * Once a factory has been disposed, further {@code create(...)} invocations
     * may throw a {@link FactoryException}. Disposing a previously-disposed factory,
     * however, has no effect.
     */
    protected void dispose(final boolean shutdown) {
        // To be overridden by subclasses.
    }

    /**
     * Trims the authority scope, if presents. For example if this factory is an EPSG authority
     * factory and the specified code start with the {@code "EPSG:"} prefix, then the prefix is
     * removed. Otherwise, the string is returned unchanged (except for leading and trailing spaces).
     *
     * @param  code The code to trim.
     * @return The code without the authority scope.
     */
    protected String trimAuthority(String code) {
        /*
         * IMPLEMENTATION NOTE: This method is overridden in PropertyAuthorityFactory. If
         * implementation below is modified, it is probably worth to revisit the overridden
         * method as well.
         */
        code = code.trim();
        final GenericName name  = nameFactory.parseGenericName(null, code);
        if (name instanceof ScopedName) {
            final GenericName scope = ((ScopedName) name).path();
            if (Citations.identifierMatches(getAuthority(), scope.toString())) {
                return name.tip().toString().trim();
            }
        }
        return code;
    }

    /**
     * Creates an exception for an unknown authority code. This convenience method is provided
     * for implementation of {@code createXXX} methods.
     *
     * @param  type  The GeoAPI interface that was to be created
     *               (e.g. {@code CoordinateReferenceSystem.class}).
     * @param  code  The unknown authority code.
     * @return An exception initialized with an error message built
     *         from the specified informations.
     */
    protected final NoSuchAuthorityCodeException noSuchAuthorityCode(final Class<?> type, final String code) {
        final InternationalString authority = getAuthority().getTitle();
        return new NoSuchAuthorityCodeException(Errors.format(Errors.Keys.NoSuchAuthorityCode_3,
                   code, authority, type), authority.toString(), trimAuthority(code), code);
    }

    /**
     * Makes sure that an argument is non-null. This is a convenience method for subclass methods.
     *
     * @param  name   Argument name.
     * @param  object User argument.
     * @throws InvalidParameterValueException if {@code object} is null.
     */
    protected static void ensureNonNull(final String name, final Object object)
            throws InvalidParameterValueException
    {
        if (object == null) {
            throw new InvalidParameterValueException(Errors.format(
                    Errors.Keys.NullArgument_1, name), name, object);
        }
    }
}

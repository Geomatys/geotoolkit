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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.factory;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import org.opengis.util.Factory;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.InvalidParameterValueException;

import org.geotoolkit.lang.Debug;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.metadata.iso.citation.DefaultCitation;


/**
 * Base class for all factories in the referencing module.
 * Factories can be grouped in two categories:
 *
 * <ul>
 *   <li><p>{@linkplain AuthorityFactory Authority factories} creates objects from a compact
 *       string defined by an authority. These classes are working as "builders": they hold
 *       the definition or recipes used to construct an object.</p></li>
 *
 *   <li><p>{@linkplain Factory Object factories} allows applications to make objects that
 *       cannot be created by an authority factory. This factory is very flexible, whereas
 *       the authority factory is easier to use. These classes are working as "Factories":
 *       they provide a series of {@code create} methods that can be used like a constructor.</p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
public class ReferencingFactory extends org.geotoolkit.factory.Factory implements Factory {
    /**
     * The logger for event related to Geotk factories.
     */
    public static final Logger LOGGER = Logging.getLogger("org.geotoolkit.referencing.factory");

    /**
     * A citation which contains only the title "All" in localized language. Used
     * as a pseudo-authority name for {@link AllAuthoritiesFactory}. Declared here
     * because processed specially by {@link IdentifiedObjectFinder}, since it is
     * not a valid authority name (not declared in {@link AllAuthoritiesFactory}
     * because we want to avoid this dependency in {@link IdentifiedObjectFinder}).
     */
    static final Citation ALL;
    static {
        final DefaultCitation citation = new DefaultCitation(Vocabulary.format(Vocabulary.Keys.All));
        citation.freeze();
        ALL = citation;
    }

    /**
     * Constructs a factory.
     */
    protected ReferencingFactory() {
        super();
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
        return getClass().getName().startsWith("org.geotoolkit.") ? Citations.GEOTOOLKIT : Citations.UNKNOWN;
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

    /**
     * Returns the direct {@linkplain Factory factory} dependencies, or an empty set if none. This
     * method shall returns direct dependencies only - it shall not iterate down the dependencies
     * tree for indirect ones. The elements are usually instance of {@link Factory}, but may also
     * be {@link FactoryException} if a particular dependency can't be obtained.
     * <p>
     * This method is used by {@link FactoryDependencies} only, for the sole purpose of printing
     * a dependencies graph for debugging purpose. {@code FactoryDependencies} will not use this
     * factory in any way other than checking its type and invoking {@code getAuthorities()}.
     * However it is possible that the user get access to those factories by invoking public
     * methods of {@link FactoryDependencies}.
     * <p>
     * The default implementation always returns an empty set.
     */
    @Debug
    Collection<? super AuthorityFactory> dependencies() {
        return Collections.emptySet();
    }
}

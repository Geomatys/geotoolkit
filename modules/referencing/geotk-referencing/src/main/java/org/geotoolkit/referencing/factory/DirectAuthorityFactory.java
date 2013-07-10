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

import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Collection;
import java.awt.RenderingHints;
import net.jcip.annotations.ThreadSafe;

import org.opengis.referencing.*;

import org.geotoolkit.factory.Hints;


/**
 * The base class for authority factories that create referencing object directly. This is
 * in contrast with other factories like the {@linkplain AuthorityFactoryAdapter adapter}
 * or {@linkplain CachingAuthorityFactory caching} ones, which delegates their work to
 * an other factory.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.3
 * @module
 */
@ThreadSafe
public abstract class DirectAuthorityFactory extends AbstractAuthorityFactory {

    // IMPLEMENTATION NOTE:  The reason why this class exist is that we don't want "indirect"
    // factories like CachingAuthorityFactory to inherit the factories field.  If this field
    // existed in their super-class, then the super-class constructor could try to initialize
    // it while in fact CachingAuthorityFactory doesn't need it. Experience with GeoTools 2.2
    // suggests that it can lead to tricky recursivity problems in FactoryFinder, because most
    // factories registered in META-INF/services are some kind of CachingAuthorityFactory.

    /**
     * The underlying factories used for objects creation.
     */
    protected final ReferencingFactoryContainer factories;

    /**
     * Tells if {@link ReferencingFactoryContainer#hints} has been invoked. It must be
     * invoked exactly once, but can't be invoked in the constructor because it causes
     * a {@link StackOverflowError} in some situations.
     */
    private volatile boolean hintsInitialized;

    /**
     * Constructs an instance using the specified set of factories.
     *
     * @param factories The low-level factories to use.
     */
    protected DirectAuthorityFactory(final ReferencingFactoryContainer factories) {
        super(EMPTY_HINTS);
        this.factories = factories;
        ensureNonNull("factories", factories);
    }

    /**
     * Constructs an instance using the specified hints. This constructor recognizes the
     * {@link Hints#CRS_FACTORY CRS}, {@link Hints#CS_FACTORY CS}, {@link Hints#DATUM_FACTORY DATUM}
     * and {@link Hints#MATH_TRANSFORM_FACTORY MATH_TRANSFORM} {@code FACTORY} hints.
     *
     * @param userHints An optional set of hints, or {@code null} for the default ones.
     */
    protected DirectAuthorityFactory(final Hints userHints) {
        super(userHints);
        factories = ReferencingFactoryContainer.instance(userHints);
        // Do not copies the user-provided hints to this.hints, because
        // this is up to sub-classes to decide which hints are relevant.
    }

    /**
     * Returns the implementation hints for this factory. The returned map contains values for
     * {@link Hints#CRS_FACTORY CRS}, {@link Hints#CS_FACTORY CS}, {@link Hints#DATUM_FACTORY DATUM}
     * and {@link Hints#MATH_TRANSFORM_FACTORY MATH_TRANSFORM} {@code FACTORY} hints. Other values
     * may be provided as well, at implementation choice.
     */
    @Override
    public Map<RenderingHints.Key,?> getImplementationHints() {
        if (!hintsInitialized) {
            final Map<RenderingHints.Key, ?> toAdd = factories.getImplementationHints();
            /*
             * Double-check locking: was a deprecated practice before Java 5, but is okay since
             * Java 5 provided that 'hintsInitialized' is volatile. In this particular case, it
             * is important to invoke factories.getImplementationHints() outside the synchronized
             * block in order to reduce the risk of deadlock. It is not a big deal if its value
             * is computed twice.
             */
            synchronized (this) {
                if (!hintsInitialized) {
                    hintsInitialized = true;
                    hints.putAll(toAdd);
                }
            }
        }
        return super.getImplementationHints();
    }

    /**
     * Returns the direct {@linkplain Factory factory} dependencies.
     */
    @Override
    final Collection<? super AuthorityFactory> dependencies() {
        final ReferencingFactoryContainer factories = this.factories;
        if (factories != null) {
            final Set<Object> dependencies = new LinkedHashSet<>(8);
            dependencies.add(factories.getCRSFactory());
            dependencies.add(factories.getCSFactory());
            dependencies.add(factories.getDatumFactory());
            return dependencies;
        }
        return super.dependencies();
    }
}

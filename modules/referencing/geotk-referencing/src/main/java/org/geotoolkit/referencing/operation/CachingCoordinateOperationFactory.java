/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation;

import java.util.Map;
import java.util.Objects;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.OperationNotFoundException;

import org.geotoolkit.lang.Buffered;
import org.geotoolkit.lang.Decorator;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.util.collection.Cache;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.factory.Factory.EMPTY_HINTS;


/**
 * Caches the {@linkplain CoordinateOperation coordinate operations} created by an other factory.
 * Those coordinate operations may be expensive to create. During rendering and during data I/O,
 * some implementations make use a lot of coordinate transformations, hence caching them might
 * help.
 * <p>
 * In most cases, users should not need to create an instance of this class explicitly. An instance
 * of {@code CachingCoordinateOperationFactory} should be automatically registered and returned
 * by {@link FactoryFinder} in default Geotk configuration.
 *
 * @author Simone Giannecchini (Geosolutions)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 2.3
 * @level advanced
 * @module
 */
@Buffered
@Decorator(CoordinateOperationFactory.class)
public class CachingCoordinateOperationFactory extends AbstractCoordinateOperationFactory {
    /**
     * Helper class used in order to build an hashing for a pair of source-destination
     * {@link CoordinateReferenceSystem} objects. This is used to cache the transformations
     * that are pretty time-consuming to build each time.
     */
    private static final class CRSPair {
        /**
         * The hash code value, computed once for ever at construction time.
         */
        private final int hash;

        /**
         * The source and target CRS.
         */
        final CoordinateReferenceSystem sourceCRS, targetCRS;

        /**
         * If a particular method was requested, that method. Otherwise {@code null}.
         */
        private final OperationMethod method;

        /**
         * Creates a {@code CRSPair} for the specified source and target CRS.
         */
        public CRSPair(final CoordinateReferenceSystem sourceCRS,
                       final CoordinateReferenceSystem targetCRS,
                       final OperationMethod method)
        {
            this.sourceCRS = sourceCRS;
            this.targetCRS = targetCRS;
            this.method    = method;
            int hash = sourceCRS.hashCode() * 31 + targetCRS.hashCode();
            if (method != null) {
                hash = hash*31 + method.hashCode();
            }
            this.hash = hash;
        }

        /**
         * Returns the hash code value.
         */
        @Override
        public int hashCode() {
            return hash;
        }

        /**
         * Compares this pair to the specified object for equality.
         *
         * {@note We perform the CRS comparison using strict equality, not using
         *        <code>equalsIgnoreMetadata</code>, because metadata matter since
         *        they are attributes of the <code>CoordinateOperation</code>
         *        object to be created.}
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof CRSPair) {
                final CRSPair that = (CRSPair) object;
                return Objects.equals(this.sourceCRS, that.sourceCRS) &&
                       Objects.equals(this.targetCRS, that.targetCRS) &&
                       Objects.equals(this.method,    that.method);
            }
            return false;
        }
    }

    /**
     * The wrapped factory. If {@code null}, will be fetched when first needed.
     * We should not initialize this field with {@link FactoryFinder} from the
     * no-argument constructor, since this constructor is typically invoked while
     * {@code FactoryFinder} is still iterating over the registered implementations.
     */
    private volatile CoordinateOperationFactory factory;

    /**
     * The pool of cached transformations. This map can not be static, because the values may
     * be different for the same ({@code sourceCRS}, {@code targetCRS}) pair depending of
     * hint values like {@link Hints#LENIENT_DATUM_SHIFT}.
     */
    private final Cache<CRSPair, CoordinateOperation> cache = new Cache<>();

    /**
     * Creates a buffered factory wrapping the {@linkplain AuthorityBackedFactory default one}.
     */
    public CachingCoordinateOperationFactory() {
        super(EMPTY_HINTS);
        /*
         * Do not use FactoryFinder here (directly or indirectly through the call
         * to an other constructor), because this constructor is typically invoked
         * while FactoryFinder is iterating over registered implementations. We
         * left the 'factory' field uninitialized and will initialize it when first
         * needed.
         */
    }

    /**
     * Creates a buffered factory wrapping an other factory selected according the specified hints.
     *
     * @param hints The hints to use for choosing a backing factory.
     */
    public CachingCoordinateOperationFactory(final Hints hints) {
        this(getBackingFactory(hints), hints);
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private CachingCoordinateOperationFactory(final CoordinateOperationFactory factory, final Hints hints) {
        super(factory, hints);
        this.factory = factory;
        ensureNonNull("factory", factory);
    }

    /**
     * Returns a backing factory from the specified hints.
     */
    private static CoordinateOperationFactory getBackingFactory(final Hints hints) {
        // TODO
        return new AuthorityBackedFactory();
    }

    /**
     * Returns the backing factory. Coordinate operation creation will be delegated to this
     * factory when not available in the cache.
     */
    @Override
    final CoordinateOperationFactory getBackingFactory() {
        CoordinateOperationFactory f = factory;
        if (f == null) {
            // Double-check: was a deprecated practice before Java 5, but
            // is okay sine Java 5 providing that the variable is volatile.
            synchronized (this) {
                f = factory;
                if (f == null) {
                    factory = f = getBackingFactory(EMPTY_HINTS);
                }
            }
        }
        return f;
    }

    /**
     * Implementations of the public {@code createOperation(sourceCRS, targetCRS)} methods.
     */
    private CoordinateOperation createOperation(final CRSPair key)
            throws OperationNotFoundException, FactoryException
    {
        CoordinateOperation op = cache.peek(key);
        if (op == null) {
            final Cache.Handler<CoordinateOperation> handler = cache.lock(key);
            try {
                op = handler.peek();
                if (op == null) {
                    op = getBackingFactory().createOperation(key.sourceCRS, key.targetCRS);
                }
            } finally {
                handler.putAndUnlock(op);
            }
        }
        return op;
    }

    /**
     * Returns an operation for conversion or transformation between two coordinate reference
     * systems. If an operation was already created and still in the cache, the cached operation
     * is returned. Otherwise the operation creation is delegated to the factory specified at
     * construction time and the result is cached.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws OperationNotFoundException if no operation path was found from {@code sourceCRS}
     *         to {@code targetCRS}.
     * @throws FactoryException if the operation creation failed for some other reason.
     */
    @Override
    public CoordinateOperation createOperation(final CoordinateReferenceSystem sourceCRS,
                                               final CoordinateReferenceSystem targetCRS)
            throws OperationNotFoundException, FactoryException
    {
        ensureNonNull("sourceCRS", sourceCRS);
        ensureNonNull("targetCRS", targetCRS);
        return createOperation(new CRSPair(sourceCRS, targetCRS, null));
    }

    /**
     * Returns an operation for conversion or transformation between two coordinate reference
     * systems using the specified method. If an operation was already created and still in the
     * cache, the cached operation is returned. Otherwise the operation creation is delegated
     * to the factory specified at construction time and the result is cached.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @param  method The algorithmic method for conversion or transformation.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws OperationNotFoundException if no operation path was found from {@code sourceCRS}
     *         to {@code targetCRS}.
     * @throws FactoryException if the operation creation failed for some other reason.
     */
    @Override
    public CoordinateOperation createOperation(final CoordinateReferenceSystem sourceCRS,
                                               final CoordinateReferenceSystem targetCRS,
                                               final OperationMethod method)
            throws OperationNotFoundException, FactoryException
    {
        ensureNonNull("sourceCRS", sourceCRS);
        ensureNonNull("targetCRS", targetCRS);
        ensureNonNull("method",    method);
        return createOperation(new CRSPair(sourceCRS, targetCRS, method));
    }

    /**
     * Creates an operation method from a set of properties and a descriptor group. The default
     * implementation delegates to the backing factory.
     *
     * @since 3.19
     */
    @Override
    public OperationMethod createOperationMethod(final Map<String,?> properties,
            final Integer sourceDimension, final Integer targetDimension,
            final ParameterDescriptorGroup parameters) throws FactoryException
    {
        return getBackingFactory().createOperationMethod(properties, sourceDimension, targetDimension, parameters);
    }

    /**
     * Returns the operation method of the given name. The default implementation delegates
     * to the backing factory without caching, since invoking this method usually don't
     * imply any object creation.
     *
     * @since 3.19
     */
    @Override
    public OperationMethod getOperationMethod(final String name) throws FactoryException {
        return getBackingFactory().getOperationMethod(name);
    }
}

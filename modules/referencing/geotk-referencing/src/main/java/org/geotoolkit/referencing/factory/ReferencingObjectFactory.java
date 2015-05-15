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

import java.util.Map;
import java.text.ParseException;

import org.opengis.referencing.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.io.wkt.ReferencingParser;
import org.apache.sis.io.wkt.Symbols;
import org.apache.sis.referencing.GeodeticObjectFactory;

import static org.apache.sis.util.collection.Containers.isNullOrEmpty;


/**
 * Builds Geotk implementations of {@linkplain CoordinateReferenceSystem CRS},
 * {@linkplain CoordinateSystem CS} and {@linkplain Datum datum} objects. Most factory methods
 * expect properties given through a {@link Map} argument. The content of this map is described
 * in the {@link ObjectFactory} interface.
 *
 * @author Martin Desruisseaux (IRD)
 * @since 1.2
 * @module
 */
public class ReferencingObjectFactory extends GeodeticObjectFactory {
    /**
     * The math transform factory. Will be created only when first needed.
     */
    private volatile MathTransformFactory mtFactory;

    /**
     * The datum factory, which should be {@code this} or a {@link DatumAliases} backed
     * by {@code this}. This information is stored in order to ensure that every WKT
     * parser created by {@link #createFromWKT} use the same factories.
     */
    private DatumFactory datumFactory;

    /**
     * The object to use for parsing <cite>Well-Known Text</cite> (WKT) strings.
     * Values will be created only when first needed.
     */
    private final ThreadLocal<ReferencingParser> parser;

    /**
     * Constructs a default factory. This method is public in order to allows instantiations
     * from a {@linkplain java.util.ServiceLoader service loaders}. Users should not instantiate
     * this factory directly, but use one of the following lines instead:
     *
     * {@preformat java
     *     DatumFactory factory = FactoryFinder.getDatumFactory (null);
     *     CSFactory    factory = FactoryFinder.getCSFactory    (null);
     *     CRSFactory   factory = FactoryFinder.getCRSFactory   (null);
     * }
     *
     * @see FactoryFinder
     */
    public ReferencingObjectFactory() {
        this(ReferencingFactory.EMPTY_HINTS);
    }

    /**
     * Constructs a factory with the specified hints. Users should not instantiate this
     * factory directly, but use one of the following lines instead:
     *
     * {@preformat java
     *     DatumFactory factory = FactoryFinder.getDatumFactory (hints);
     *     CSFactory    factory = FactoryFinder.getCSFactory    (hints);
     *     CRSFactory   factory = FactoryFinder.getCRSFactory   (hints);
     * }
     *
     * @param hints An optional set of hints, or {@code null} if none.
     *
     * @see FactoryFinder
     *
     * @since 2.5
     */
    public ReferencingObjectFactory(final Hints hints) {
        parser = new ThreadLocal<>();
        if (!isNullOrEmpty(hints)) {
            /*
             * Creates the dependencies (MathTransform factory, WKT parser...) now because
             * we need to process user's hints. Then, we will keep only the relevant hints.
             */
            mtFactory = FactoryFinder.getMathTransformFactory(hints);
        }
    }

    /**
     * Returns the datum factory, which should be {@code this} or a {@link DatumAliases}
     * backed by {@code this}.
     */
    private synchronized DatumFactory getDatumFactory() {
        if (datumFactory == null) {
            datumFactory = this; // The fallback value.
            for (final DatumFactory factory : FactoryFinder.getDatumFactories(ReferencingFactory.EMPTY_HINTS)) {
                if (factory instanceof DatumAliases) {
                    if (((DatumAliases) factory).getDatumFactory() == this) {
                        datumFactory = factory;
                        break;
                    }
                }
            }
        }
        return datumFactory;
    }

    /**
     * Returns the math transform factory for internal usage only. The hints given to
     * {@link ReferencingFactoryFinder} must be null, since the non-null case should
     * have been handled by the constructor.
     *
     * @see #createParser
     */
    private MathTransformFactory getMathTransformFactory() {
        MathTransformFactory factory = mtFactory;
        if (factory == null) {
            // Following line must be outside the synchronized block, as a safety against
            // deadlocks. This is not a big deal if this information is fetched twice.
            final MathTransformFactory candidate = FactoryFinder.getMathTransformFactory(ReferencingFactory.EMPTY_HINTS);
            synchronized (this) {
                // Double-checked locking - was a deprecated practice before Java 5.
                // Is okay since Java 5 provided that the variable is volatile.
                factory = mtFactory;
                if (factory == null) {
                    mtFactory = factory = candidate;
//                  hints.put(Hints.MATH_TRANSFORM_FACTORY, factory);
                }
            }
        }
        return factory;
    }

    /**
     * Creates a coordinate reference system object from a string.
     *
     * @param  wkt Coordinate system encoded in Well-Known Text format.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CoordinateReferenceSystem createFromWKT(final String wkt) throws FactoryException {
        /*
         * Note: while this factory is thread safe, the WKT parser is not.
         * We need either to synchronize, or use one instance per thread.
         */
        ReferencingParser parser = this.parser.get();
        if (parser == null) {
            parser = new ReferencingParser(Symbols.getDefault(),
                    getDatumFactory(), this, this, getMathTransformFactory());
            parser.setISOConform(true);
            this.parser.set(parser);
        }
        try {
            return parser.parseCoordinateReferenceSystem(wkt);
        } catch (ParseException exception) {
            final Throwable cause = exception.getCause();
            if (cause instanceof FactoryException) {
                throw (FactoryException) cause;
            }
            throw new FactoryException(exception);
        }
    }
}

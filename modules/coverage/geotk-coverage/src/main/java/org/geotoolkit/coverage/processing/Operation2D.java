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
 */
package org.geotoolkit.coverage.processing;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.IdentifiedObject;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.InvalidParameterValueException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.coverage.CoverageFactoryFinder;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * An operation working on a two-dimensional slice of a coverage. The current implementation
 * requires the source to be instances of {@link GridCoverage2D}, but this restriction may be
 * relaxed in a future version.
 * <p>
 * This base class does not really impose any restriction about what the
 * {@link #doOperation doOperation} method can do, but it provides convenience methods for
 * {@linkplain #extractSources extracting the sources} as {@code GridCoverage2D} objects.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @since 1.2
 * @module
 *
 * @deprecated The API of this class will change in a future Geotk release. Do not rely on it.
 */
@Immutable
@Deprecated
public abstract class Operation2D extends AbstractOperation {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 574096338873406394L;

    /**
     * Index of the source {@link GridCoverage2D} to use as a model. The destination grid coverage
     * will reuse the same coordinate reference system, envelope and qualitative categories than
     * this primary source.
     * <p>
     * For operations expecting only one source, there is no ambiguity. But for operations
     * expecting more than one source, the choice of a primary source is somewhat arbitrary.
     * This constant is used merely as a flag for spotting those places in the code.
     *
     * @since 2.4
     */
    protected static final int PRIMARY_SOURCE_INDEX = 0;

    /**
     * The preferred type of views to be returned by {@link #extractSources},
     * in preference order.
     */
    private static final ViewType[] PREFERRED_OUTPUTS = {
        ViewType.GEOPHYSICS,
        ViewType.PACKED,
        ViewType.PHOTOGRAPHIC,
        ViewType.RENDERED
    };

    /**
     * Convenience constant for the first source {@link GridCoverage2D}. The parameter name
     * is {@code "Source"} (as specified in OGC implementation specification) and the alias
     * is {@code "source0"} (for compatibility with <cite>Java Advanced Imaging</cite>).
     */
    public static final ParameterDescriptor<GridCoverage2D> SOURCE_0;

    /**
     * Convenience constant for the second source {@link GridCoverage2D}. The parameter name
     * is {@code "source1"} (for compatibility with <cite>Java Advanced Imaging</cite>).
     *
     * @since 3.00
     */
    public static final ParameterDescriptor<GridCoverage2D> SOURCE_1;

    /**
     * Convenience constant for the third source {@link GridCoverage2D}. The parameter name
     * is {@code "source2"} (for compatibility with <cite>Java Advanced Imaging</cite>).
     *
     * @since 3.00
     */
    public static final ParameterDescriptor<GridCoverage2D> SOURCE_2;
    static {
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put(IdentifiedObject.NAME_KEY,  new NamedIdentifier(Citations.OGC, "Source"));
        properties.put(IdentifiedObject.ALIAS_KEY, new NamedIdentifier(Citations.JAI, "source0"));
        SOURCE_0 = new DefaultParameterDescriptor<>(properties, GridCoverage2D.class,
                        null, null, null, null, null, true);

        properties.clear();
        properties.put(IdentifiedObject.NAME_KEY, new NamedIdentifier(Citations.JAI, "source1"));
        SOURCE_1 = new DefaultParameterDescriptor<>(properties, GridCoverage2D.class,
                        null, null, null, null, null, true);

        properties.put(IdentifiedObject.NAME_KEY, new NamedIdentifier(Citations.JAI, "source2"));
        SOURCE_2 = new DefaultParameterDescriptor<>(properties, GridCoverage2D.class,
                        null, null, null, null, null, true);
    }

    /**
     * Constructs an operation. The operation name will be the same than the
     * parameter descriptor name.
     *
     * @param descriptor The parameters descriptor.
     */
    protected Operation2D(final ParameterDescriptorGroup descriptor) {
        super(descriptor);
    }

    /**
     * Returns the view of {@link GridCoverage2D} to use for computation purpose. The default
     * implementation conservatively returns {@link ViewType#GEOPHYSICS} in all case. This is
     * a "conservative" implementation because this is often the only view which is guaranteed
     * to be mainfull (assuming that a grid coverage has defined such a view). If computation
     * should be performed on the data "as is" without any "<cite>samples to geophysics values</cite>"
     * conversion, then subclasses should override this method and returns {@link ViewType#SAME}.
     *
     * @param  parameters The parameters supplied by the user to the {@code doOperation} method.
     * @return The view on which computation should be performed.
     *
     * @see GridCoverage2D#view(ViewType)
     *
     * @since 3.00
     */
    protected ViewType getComputationView(final ParameterValueGroup parameters) {
        return ViewType.GEOPHYSICS;
    }

    /**
     * Extracts and prepares the sources for this {@code Operation2D}, assuming that the parameters
     * use the standard names for the sources. The expected number of sources is the length of the
     * {@code sources} array. If this length is 1, then the parameter group is expected to contain
     * a parameter named {@code "Source"} with a value of type {@link GridCoverage2D}. If the length
     * of the {@code sources} array is greater then 1, then the parameter group is expected to contain
     * parameters named {@code "source0"}, {@code "source1"}, ..., <code>"source<var>n</var>"</code>
     * where <var>n</var> is the array length.
     * <p>
     * The source coverages extracted from the parameters are stored in the given {@code sources}
     * array, taking into account the need for going to the geophysics view of the data in case
     * this operation requires so.
     *
     * @param parameters
     *          Parameters that will control this operation.
     * @param sources
     *          The array where to store the {@link GridCoverage2D} to be used as sources
     *          for this operation.
     * @return The view of the result. The {@code doOperation} method should converts its
     *          result to that view.
     * @throws ParameterNotFoundException
     *          If a required source has not been found.
     * @throws InvalidParameterValueException
     *          If a source doesn't contain a value of type {@link GridCoverage2D}.
     *
     * @since 3.00
     */
    protected ViewType extractSources(final ParameterValueGroup parameters,
                                      final GridCoverage2D[]    sources)
            throws ParameterNotFoundException, InvalidParameterValueException
    {
        ensureNonNull("parameters", parameters);
        ensureNonNull("sources",    sources);
        final String[] names = new String[sources.length];
        if (sources.length == 1) {
            names[0] = "Source";
        } else {
            final StringBuilder buffer = new StringBuilder("source");
            final int length = buffer.length();
            for (int i=0; i<sources.length; i++) {
                buffer.setLength(length);
                names[i] = buffer.append(i).toString();
            }
        }
        return extractSources(parameters, names, sources);
    }

    /**
     * Extracts and prepares the sources for this {@code Operation2D}, knowing that the parameters
     * use the given names for the sources. The expected number of sources is the length of the
     * {@code names} array. For each name at index <var>i</var>, a parameter having the name
     * {@code name[i]} is expected to exist and have a value of type {@link GridCoverage2D}.
     * The reference to this coverage is stored in {@code sources[i]} (overwriting any previous
     * value), taking into account the need for going to the geophysics view of the data in case
     * this operation requires so.
     *
     * @param parameters
     *          Parameters that will control this operation.
     * @param sourceNames
     *          Names of the sources to extract from {@link ParameterValueGroup}.
     * @param sources
     *          An array with the same length than {@code sourceNames} where to store
     *          the {@link GridCoverage2D} to be used as sources for this operation.
     * @return The view of the result. The {@code doOperation} method should converts its
     *          result to that view.
     * @throws ParameterNotFoundException
     *          if a required source has not been found.
     * @throws InvalidParameterValueException
     *          if a source doesn't contain a value of type {@link GridCoverage2D}.
     *
     * @since 2.4
     */
    protected ViewType extractSources(final ParameterValueGroup parameters,
                                      final String[]            sourceNames,
                                      final GridCoverage2D[]    sources)
            throws ParameterNotFoundException, InvalidParameterValueException
    {
        ensureNonNull("parameters",  parameters);
        ensureNonNull("sourceNames", sourceNames);
        ensureNonNull("sources",     sources);
        if (sources.length != sourceNames.length) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.MISMATCHED_ARRAY_LENGTH_$2, "sources", "sourceNames"));
        }
        final ViewType computationView = getComputationView(parameters);
        ensureNonNull("computationView", computationView);
        ViewType targetView = ViewType.SAME;
        for (int i=0; i<sourceNames.length; i++) {
            final Object candidate = parameters.parameter(sourceNames[i]).getValue();
            if (!(candidate instanceof GridCoverage2D)) {
                throw new InvalidParameterValueException(Errors.format(Errors.Keys.ILLEGAL_CLASS_$2,
                        Classes.getClass(candidate), GridCoverage2D.class), sourceNames[i], candidate);
            }
            final GridCoverage2D old = (GridCoverage2D) candidate;
            final GridCoverage2D source = old.view(computationView);
            /*
             * If we are setting the primary source, and the computation view of that source
             * is not the same than the original source coverage, takes the preferred view of
             * the output.
             */
            if (i == PRIMARY_SOURCE_INDEX && old != source) {
                final Set<ViewType> views = old.getViewTypes();
                if (views != null) {
                    for (final ViewType type : PREFERRED_OUTPUTS) {
                        if (views.contains(type)) {
                            targetView = type;
                            break;
                        }
                    }
                }
            }
            sources[i] = source;
        }
        return targetView;
    }

    /**
     * Returns the factory to use for creating new {@link GridCoverage2D} objects.
     *
     * @param  hints An optional set of hints, or {@code null} if none.
     * @return The factory to use for creating new {@link GridCoverage2D} objects.
     *
     * @since 2.2
     */
    static GridCoverageFactory getFactory(final Hints hints) {
        return CoverageFactoryFinder.getGridCoverageFactory(hints);
    }
}

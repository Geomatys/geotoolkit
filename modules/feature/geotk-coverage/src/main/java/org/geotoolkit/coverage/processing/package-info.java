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

/**
 * {@linkplain org.geotoolkit.coverage.processing.AbstractCoverageProcessor Coverage processor}
 * implementations. An explanation for this package is provided in the
 * {@linkplain org.opengis.coverage.processing OpenGIS&reg; javadoc}.
 * The remaining discussion on this page is specific to the Geotk implementation.
 * <p>
 * If the operation to apply is know at compile time, then the easiest way to use this package
 * is to use the {@link org.geotoolkit.coverage.processing.Operations} convenience class. For
 * example a {@linkplain org.opengis.coverage.grid.GridCoverage grid coverage} can be resampled
 * to a different {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem coordinate
 * reference system} using the following code:
 *
 * {@preformat java
 *     Coverage reprojected = Operations.DEFAULT.resample(myCoverage, newCRS);
 * }
 *
 * If the operation to apply is unknown at compile time, or if the operation is not listed in the
 * {@code Operations} convenience class, then the generic way to invoke an operation is described
 * in the {@linkplain org.geotoolkit.coverage.processing.operation operation package}.
 *
 *
 * {@section Creating new operations}
 *
 * Geotk coverage operations must extend the {@link org.geotoolkit.coverage.processing.AbstractOperation}
 * class or one of its subclasses. All operations must declare the expected parameters, including source
 * coverages, as an {@link org.opengis.parameter.ParameterDescriptorGroup} object. The source coverages
 * are ordinary parameters for which the {@linkplain org.opengis.parameter.ParameterDescriptor#getValueClass
 * value class} is {@link org.opengis.coverage.Coverage} or a subclass. The parameter name of source
 * coverages can be anything, but the <cite>Java Advanced Imaging</cite> usage is {@code "source0"}
 * for the first source, {@code "source1"} for the second source (if any), <i>etc</i>.
 * <p>
 * {@link org.geotoolkit.coverage.processing.AbstractOperation} is the base class offering few convenience
 * for the developers, but few restrictions on the kind of source coverage and on the way to implement
 * the operation. {@code AbstractOperation} can be pure mathematical functions.
 * <p>
 * {@link org.geotoolkit.coverage.processing.Operation2D} is a subclass expecting source coverages
 * that are specifically of type {@link org.geotoolkit.coverage.grid.GridCoverage2D} or a subclass.
 * {@code GridCoverage2D} has a mechanism for storing its data in a <cite>packed</cite> form,
 * typically as 8 or 16 bits integers convertible to <cite>geophysics</cite> values by a linear
 * equation. Because most operations need to be applied on geophysics values in order to produce
 * correct results, {@code Operation2D} provides facilities for converting the source coverages
 * to their geophysics view, and converting the result back to the original (packed or not) view.
 * <p>
 * {@link org.geotoolkit.coverage.processing.OperationJAI} is a subclass expecting the coverage
 * operation to be implemented as a <cite>Java Advanced Imaging</cite> (JAI) operation working
 * on {@link java.awt.image.RenderedImage}. Because {@code GridCoverage2D} are basically wrappers
 * around {@code RenderedImage}, it is often good design to implement the coverage operation as a
 * wrapper around the corresponding JAI operation. However such wrapper needs to perform some
 * pre-processing and post-processing tasks that are relative to the geographic nature of the
 * operation. For example calculating the sum of sample values of two images usually requires
 * that those images are first projected in the same Coordinate Reference System (CRS). The
 * {@code OperationJAI} class is designed for handling those common pre- and post-processing.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
package org.geotoolkit.coverage.processing;

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
 * {@linkplain org.geotoolkit.coverage.grid.GridCoverage2D} implementation. An explanation for this
 * package is provided in the {@linkplain org.opengis.coverage.grid OpenGIS&reg; javadoc}.
 * The remaining discussion on this page is specific to the Geotk implementation.
 * <p>
 * The main class in this package is {@link org.geotoolkit.coverage.grid.GridCoverage2D},
 * which is a two-dimensional slice in a <var>n</var>-dimensional cube of data. Despite
 * its name, {@code GridCoverage2D} instances can be associated to <var>n</var>-dimensional
 * {@linkplain org.opengis.geometry.Envelope envelopes} providing that only two dimensions
 * have a {@link org.opengis.coverage.grid.GridEnvelope#getSpan(int) grid span} greater than 1.
 * <p>
 * The {@code GridCoverage2D} constructor is rather tedious. The
 * {@link org.geotoolkit.coverage.grid.GridCoverageBuilder} convenience class provides
 * more convenient ways to create a grid coverage.
 *
 * {@section Accurate definition of georeferencing information}
 * While it is possible to create a grid coverage from a geodetic
 * {@linkplain org.opengis.geometry.Envelope envelope}, this approach should be used
 * <strong>in last resort</strong> only. Instead, always specify the <cite>grid to CRS</cite>
 * affine transform. This is preferable because envelopes have ambiguities (do we need to swap
 * the longitude and latitude axes? Do we need to flip the <var>y</var> axis?) which Geotk tries
 * to resolve using heuristic rules implemented in
 * {@link org.geotoolkit.referencing.operation.builder.GridToEnvelopeMapper}. Those rules are
 * somewhat arbitrary and are not guaranteed to produce the expected result. On the other hand,
 * the <cite>grid to CRS</cite> affine transform is fully determinist.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.1
 * @module
 */
package org.geotoolkit.coverage.grid;

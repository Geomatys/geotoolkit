/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
 * Provides {@link org.geotoolkit.coverage.io.GridCoverageReader} implementations for creating
 * {@link org.geotoolkit.coverage.grid.GridCoverage2D} instances from a datasource. The main
 * implementation is {@link org.geotoolkit.coverage.io.ImageCoverageReader}, which read the
 * sample values using an {@link javax.imageio.ImageReader} from the standard Java library.
 * <p>
 * Many different data formats exist. However most of this variety is not handled by this package.
 * The various data formats are rather handled by different subclasses of {@code ImageReader},
 * for example
 * {@link org.geotoolkit.image.io.plugin.AsciiGridReader},
 * {@link org.geotoolkit.image.io.plugin.NetcdfImageReader} and
 * {@link org.geotoolkit.image.io.plugin.WorldFileImageReader}.
 * All those different {@code ImageReader} implementations can be wrapped by the
 * same {@link org.geotoolkit.coverage.io.ImageCoverageReader} implementation.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 3.20
 *
 * @since 3.09 (derived from 2.4)
 * @module
 */
package org.geotoolkit.coverage.io;

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.image.io;

import java.util.List;
import java.net.URI;
import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;


/**
 * Interface for {@link ImageReader} and {@link ImageWriter} implementations that may store
 * a large dataset as an aggregation of smaller datasets. There is many different way in which
 * an aggregation may be used:
 * <p>
 * <ul>
 *   <li>A <var>n</var> dimensional coverage (the aggregation) may be defined as a list of
 *       <var>n</var>-1 dimensional slices (the aggregated files).</li>
 *   <li>A long time series (the aggregation) may be separated in a sequence of smaller
 *       time series (the aggregated files). In this use case, the aggregation and the
 *       aggregated files have the same number of dimension.</li>
 *   <li>The bands (or <cite>variables</cite> in NetCDF files) may be defined in separated
 *       files.</li>
 * </ul>
 * <p>
 * The main practical case for this interface is NcML files, which are XML files enumerating
 * the NetCDF files that are the elements of the aggregation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module
 */
public interface AggregatedImageStore {
    /**
     * Returns the URIs to the aggregated files, or {@code null} if the current
     * {@linkplain ImageReader#getInput() input} is not an aggregation.
     *
     * @param  imageIndex The index of the image (or variable) for which to get the aggregated files.
     * @return The individual files which are aggregated, or {@code null} if none.
     * @throws IOException If an error occurred while building the list of files.
     */
    List<URI> getAggregatedFiles(int imageIndex) throws IOException;
}

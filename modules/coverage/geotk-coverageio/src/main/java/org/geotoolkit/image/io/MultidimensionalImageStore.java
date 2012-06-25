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

import java.util.Set;
import javax.imageio.IIOParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;


/**
 * Interface for {@link ImageReader} and {@link ImageWriter} implementations handling data
 * which can have more than two dimensions. The standard Java Image I/O API is designed for
 * two-dimensional data. The Geotk library can gives access to supplemental dimensions in
 * two different ways:
 *
 * <ul>
 *   <li><p>Using many {@link DimensionSlice} objects (one for each dimension) associated
 *       to a single {@link SpatialImageReadParam} object controlling the reading process.
 *       This approach is similar in spirit to the WCS 2.0 specification, but requires
 *       knowledge of Geotk API.</p></li>
 *
 *   <li><p>Using the standard Java Image I/O API for <cite>bands</cite> and <cite>image
 *       index</cite>. An advantage of this approach is to work with libraries having no
 *       knowledge of the Geotk-specific {@code DimensionSlice} class. This is the approach
 *       enabled by this {@code MultidimensionalImageStore} interface.</p></li>
 * </ul>
 *
 * {@section Assigning a third dimension to bands}
 * Whatever a third dimension is assigned to bands or not is plugin-specific. Plugins that have
 * no concept of bands (like NetCDF which has the concept of <var>n</var>-dimensional data cube
 * instead) can do that. For example in a dataset having (<var>x</var>, <var>y</var>, <var>z</var>,
 * <var>t</var>) dimensions, it may be useful to handle the <var>z</var> dimension as bands. After
 * the method calls below, users can select one or many elevation indices through the standard
 * {@link IIOParam#setSourceBands(int[])} API. Compared to the {@link DimensionSlice} API, it
 * allows loading more than one slice in a single read operation.
 *
 * {@preformat java
 *     MultidimensionalImageStore reader = ...;
 *     DimensionIdentification bandsDimension = reader.getDimensionForAPI(DimensionSlice.API.BANDS);
 *     bandsDimension.addDimensionId(2); // 0-based index of the third dimension.
 * }
 *
 * When a dimension is assigned to bands as in the above example and if no
 * {@link IIOParam#sourceBands} array is specified, then all bands will be read.
 * This number of bands may be large, for example in NetCDF files. Users are advised
 * to always {@linkplain IIOParam#setSourceBands(int[]) set the source bands} when using
 * the band API for multi-dimensional data.
 * <p>
 * After the <var>z</var> dimension in the above example has been assigned to the bands API,
 * the bands can be used as below:
 * <p>
 * <ul>
 *   <li>The (<var>x</var>,<var>y</var>) plane at <var>z</var><sub>{@code sourceBands[0]}</sub> is stored in band 0.</li>
 *   <li>The (<var>x</var>,<var>y</var>) plane at <var>z</var><sub>{@code sourceBands[1]}</sub> is stored in band 1.</li>
 *   <li><i>etc.</i></li>
 * </ul>
 *
 * {@section Note for implementors}
 * {@code ImageReader} and {@code ImageWriter} implementors can determine which (if any) dimension
 * index has been assigned to the bands API by using the code below:
 *
 * {@preformat java
 *     DimensionSet dimensionsForAPI = ...; // Typically an ImageReader/Writer field.
 *     DimensionIdentification bandsDimension = dimensionsForAPI.get(DimensionSlice.API.BANDS);
 *     if (bandsDimension != null) {
 *         Collection<?> propertiesOfAxes = ...; // This is plugin-specific.
 *         int index = bandsDimension.findDimensionIndex(propertiesOfAxes);
 *         if (index >= 0) {
 *             // We have found the dimension index of bands.
 *         }
 *     }
 * }
 *
 * See also the {@link DimensionSet} javadoc for code snippet implementing the methods
 * declared in this interface.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see DimensionSlice
 * @see SpatialImageReader#getDimension(int)
 * @see SpatialImageReader#getGridEnvelope(int)
 *
 * @since 3.15
 * @module
 */
public interface MultidimensionalImageStore {
    /**
     * The standard dimension index of pixel columns (<var>x</var> ordinates) in images,
     * which is {@value}. This is for example the standard dimension index of
     * {@linkplain java.awt.image.RenderedImage#getWidth() image width} in
     * {@linkplain org.opengis.coverage.grid.GridEnvelope grid envelopes}.
     * <p>
     * In theory, the {@code MultidimensionalImageStore} API allows some flexibility about
     * which dimension is the "image width". However in practice, the {@value} dimension is
     * often hard-coded, sometime as an index (in which case this {@code X_DIMENSION} field
     * shall be used), or sometime in the way loops are structured (in which case a
     * {@code // X_DIMENSION} comment shall be put). The purpose of this constant is to
     * allow traceability of code making such hard-coded assumption, in case more flexibility
     * is needed in the future.
     *
     * @since 3.19
     */
    int X_DIMENSION = 0;

    /**
     * The standard dimension index of pixel rows (<var>y</var> ordinates) in images,
     * which is {@value}. This is for example the standard dimension index of
     * {@linkplain java.awt.image.RenderedImage#getHeight() image height} in
     * {@linkplain org.opengis.coverage.grid.GridEnvelope grid envelopes}.
     * <p>
     * In theory, the {@code MultidimensionalImageStore} API allows some flexibility about
     * which dimension is the "image height". However in practice, the {@value} dimension is
     * often hard-coded, sometime as an index (in which case this {@code Y_DIMENSION} field
     * shall be used), or sometime in the way loops are structured (in which case a
     * {@code // Y_DIMENSION} comment shall be put). The purpose of this constant is to
     * allow traceability of code making such hard-coded assumption, in case more flexibility
     * is needed in the future.
     *
     * @since 3.19
     */
    int Y_DIMENSION = 1;

    /**
     * Returns the dimension assigned to the given API. This method never return {@code null}.
     * However the returned dimension can be used only if the {@code addDimensionId(...)} method
     * has been invoked at least once on the returned instance.
     *
     * @param  api The API for which to return a dimension.
     * @return The dimension assigned to the given API.
     *
     * @see DimensionSet#getOrCreate(DimensionSlice.API)
     */
    DimensionIdentification getDimensionForAPI(DimensionSlice.API api);

    /**
     * Returns the API assigned to the given dimension identifiers. The identifiers can be any
     * kind of objects accepted by the {@link DimensionIdentification#addDimensionId(int)
     * DimensionIdentification.addDimensionId(...)} methods. Unknown identifiers are silently
     * ignored.
     * <p>
     * If more than one dimension is found for the given identifiers, then a
     * {@linkplain SpatialImageReader#warningOccurred warning is emitted} and
     * this method returns the first dimension matching the given identifiers.
     * If no dimension is found, {@code API.NONE} is returned.
     *
     * @param  identifiers The identifiers of the dimension to query.
     * @return The API assigned to the given dimension, or {@link DimensionSlice.API#NONE} if none.
     *
     * @see DimensionSet#getAPI(Object[])
     */
    DimensionSlice.API getAPIForDimension(Object... identifiers);

    /**
     * Returns the set of APIs for which at least one dimension has identifiers. This
     * is typically an empty set until the following code is invoked at least once:
     *
     * {@preformat java
     *     getDimensionForAPI(...).addDimensionId(...);
     * }
     *
     * @return The API for which at least one dimension has identifiers.
     *
     * @see DimensionSet#getAPIs()
     */
    Set<DimensionSlice.API> getAPIForDimensions();
}

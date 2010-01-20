/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.awt.Rectangle;
import java.awt.image.IndexColorModel;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;

import org.opengis.referencing.cs.AxisDirection;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.IndexedResourceBundle;
import org.geotoolkit.util.Localized;
import org.geotoolkit.util.converter.Classes;


/**
 * Default parameters for {@link SpatialImageReader}. This class extends the standard
 * {@link ImageReadParam} class with the following additional capabilities:
 * <p>
 * <ul>
 *   <li><p>Specify the plane to read in datasets having more than 2 dimensions, as for example
 *       in {@linkplain org.geotoolkit.image.io.plugin.NetcdfImageReader NetCDF} files.</p></li>
 *
 *   <li><p>Specify the name of a {@linkplain Palette color palette}. This is useful when
 *       reading an image from a file that doesn't contain such information, for example
 *       {@linkplain org.geotoolkit.image.io.plugin.AsciiGridReader ASCII grid}.</p></li>
 *
 *   <li><p>For images having more than one band where the bands are <strong>not</strong> color
 *       components, specify which band to use with the {@linkplain IndexColorModel Index
 *       Color Model}. Example: an image may contain <cite>Sea Surface Temperature</cite> (SST)
 *       measurements in the first band, and an estimation of the measurement errors in the second
 *       band. Users may want to read both bands for computation purpose, while applying a color
 *       palette using only the values in the first band.</p></li>
 * </ul>
 *
 * {@section Handling more than two dimensions}
 * Some file formats like NetCDF can store dataset having more than two dimensions.
 * Geotk handles extra dimensions with the following policies by default:
 *
 * <ul>
 *   <li><p>The two first dimensions - typically named (<var>x</var>, <var>y</var>) - are
 *     assigned to the (<var>columns</var>, <var>rows</var>) pixel indices.</p></li>
 *
 *   <li><p>The third dimension is assigned to band indices. This is typically the altitude
 *     (<var>z</var>) in a dataset having the (<var>x</var>, <var>y</var>, <var>z</var>,
 *     <var>t</var>) dimensions, in which case the bands are used as below:</p>
 *     <ul>
 *       <li>The (<var>x</var>,<var>y</var>) plane at <var>z</var><sub>0</sub> is stored in band 0</li>
 *       <li>The (<var>x</var>,<var>y</var>) plane at <var>z</var><sub>1</sub> is stored in band 1</li>
 *       <li><i>etc.</i></li>
 *     </ul>
 *     <p>The actual dimension assigned to band indices is returned by {@link #getDimensionForAPI
 *     getDimensionForAPI(...)}. See {@link DimensionSlice} for changing the assignment.</p>
 *
 *   <li><p>Only one slice of every extra dimensions can be read. By default the data at indice 0
 *     are loaded, but different indices can be selected (see {@link DimensionSlice}). The actual
 *     indice used is the value returned by {@link #getSourceIndiceForDimension(Object[])}.</p></li>
 * </ul>
 *
 * Note that the reader for some formats (e.g. NetCDF) will initialize the {@linkplain #sourceBands
 * source bands} array to {@code {0}} in order to load only one band by default. This is different
 * than the usual {@code ImageReadParam} behavior which is to initialize source bands to {@code null}
 * (meaning to load all bands). This is done that way because the number of bands in NetCDF files is
 * typically large and those bands are not color components. The decision to initialize
 * {@code sourceBands} to {@code {0}} or {@code null} is left to reader implementations.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.05 (derived from 2.4)
 * @module
 */
public class SpatialImageReadParam extends ImageReadParam implements Localized {
    /**
     * The name of the default color palette to apply when none was explicitly specified.
     * The default palette is {@value}.
     *
     * @see #getPaletteName()
     * @see #setPaletteName(String)
     */
    public static final String DEFAULT_PALETTE_NAME = "grayscale";

    /**
     * For <var>n</var>-dimensional images where <var>n</var>&gt;2, the data to
     * select in arbitrary dimensions. Will be created only when first needed.
     *
     * @since 3.08
     */
    private Map<Object,DimensionSlice> slicesForDimensions;

    /**
     * For <var>n</var>-dimensional images, the standard Java API to use for setting the index.
     * Will be created only when first needed. The length of this array shall be equals to the
     * length of the {@link DimensionSlice.API#VALIDS} array.
     *
     * @since 3.08
     */
    private DimensionSlice[] apiMapping;

    /**
     * The name of the color palette.
     */
    private String palette;

    /**
     * The band to display.
     */
    private int visibleBand;

    /**
     * The image reader which created the parameters, or {@code null} if unknown.
     * This is used for emitting warnings.
     */
    private final ImageReader reader;

    /**
     * Creates a new, initially empty, set of parameters.
     *
     * @param reader The reader for which this parameter block is created, or {@code null}.
     */
    public SpatialImageReadParam(final ImageReader reader) {
        this.reader = reader;
    }

    /**
     * Returns the resources for formatting error messages.
     */
    private IndexedResourceBundle getErrorResources() {
        return Errors.getResources(getLocale());
    }

    /**
     * Convenience method for logging a warning from the given method.
     */
    private void warningOccurred(final String method, final String message) {
        final LogRecord record = new LogRecord(Level.WARNING, message);
        record.setSourceClassName(SpatialImageReadParam.class.getName());
        record.setSourceMethodName(method);
        if (reader instanceof SpatialImageReader) {
            ((SpatialImageReader) reader).warningOccurred(record);
        } else {
            record.setLoggerName(SpatialImageReader.LOGGER.getName());
            SpatialImageReader.LOGGER.log(record);
        }
    }

    /**
     * Creates a new handler for selecting a slice of the (<var>x</var>, <var>y</var>) plane to
     * read. This is relevant only for <var>n</var>-dimensional dataset where <var>n</var>&gt;2.
     * The caller should invoke one or many {@link DimensionSlice#addDimensionId(int)
     * addDimensionId(...)} methods for specifying the dimension, and invoke
     * {@link DimensionSlice#setIndice(int)} for specifying the indice of the
     * <cite>slice point</cite> (WMS 2.0 terminology).
     * <p>
     * A new handler can be created for each supplemental dimension above the two (<var>x</var>,
     * <var>y</var>) dimensions and the bands handled by Java2D. If no slice is specified for a
     * supplemental dimension, then the default is the slice at indice 0.
     *
     * @return A new handler for specifying the indice of the slice to read in a supplemental
     *         dimension.
     *
     * @since 3.08
     */
    public DimensionSlice newDimensionSlice() {
        if (slicesForDimensions == null) {
            slicesForDimensions = new HashMap<Object,DimensionSlice>();
            apiMapping = new DimensionSlice[DimensionSlice.API.VALIDS.length];
        }
        return new DimensionSlice(this, slicesForDimensions, apiMapping, false);
    }

    /**
     * Returns the first key in the given map. If the map has more than one entry,
     * a warning is emitted. If the map is empty, {@code null} is returned. This
     * method is used for determining the {@link DimensionSlice} instance to use
     * after we have iterated over the properties of all axes in a coordinate system.
     *
     * @param  <T>         Either {@link Integer} or {@link API}.
     * @param  methodName  The method which is invoking this method, used for logging purpose.
     * @param  found       The map from which to extract the first key.
     * @return The first key in the given map, or {@code null} if none.
     */
    private <T> T first(final String methodName, final Map<T,?> found) {
        if (found != null) {
            final int size = found.size();
            if (size != 0) {
                /*
                 * At least one (source, property) pair has been found.  We will return the
                 * indice. However if we found more than one pair, we have an ambiguity. In
                 * the later case, we will log a warning before to return the first indice.
                 */
                if (size > 1) {
                    final StringBuilder buffer = new StringBuilder();
                    for (final Object value : found.values()) {
                        if (buffer.length() != 0) {
                            buffer.append(" | ");
                        }
                        buffer.append(value);
                    }
                    String message = getErrorResources().getString(Errors.Keys.AMBIGIOUS_VALUE_$1, buffer);
                    buffer.setLength(0);
                    buffer.append(message);
                    for (final T source : found.keySet()) {
                        if (buffer.length() != 0) {
                            buffer.append(',');
                        }
                        buffer.append(' ').append(source);
                    }
                    message = buffer.toString();
                    warningOccurred(methodName, message);
                }
                return found.keySet().iterator().next();
            }
        }
        return null;
    }

    /**
     * Returns the slice at the given dimension. This is relevant only for <var>n</var>-dimensional
     * dataset where <var>n</var>&gt;2. The dimension can be identified by a zero-based index as an
     * {@link Integer}, a dimension name as a {@link String}, or an axis direction as an
     * {@link AxisDirection}. More than one identifier can be specified in order to increase
     * the chance to get the indice, for example:
     *
     * {@preformat java
     *     DimensionSlice slice = getDimensionSlice("time", AxisDirection.FUTURE);
     * }
     *
     * If different slices are found for the given identifiers, then a warning is emitted and
     * this method returns the first slice matching the given identifiers. If no slice is found,
     * {@code null} is returned.
     *
     * @param  dimension {@link Integer}, {@link String} or {@link AxisDirection}
     *         that identify the dimension for which the slice is desired.
     * @return The slice for the given dimension, or {@code null} if none.
     *
     * @since 3.08
     */
    public DimensionSlice getDimensionSlice(final Object... dimension) {
        if (slicesForDimensions != null) {
            Map<DimensionSlice,Object> found = null;
            for (final Object id : dimension) {
                final DimensionSlice slice = slicesForDimensions.get(id);
                if (slice != null) {
                    if (found == null) {
                        found = new LinkedHashMap<DimensionSlice,Object>(4);
                    }
                    final Object old = found.put(slice, id);
                    if (old != null) {
                        found.put(slice, old); // Keep the old value.
                    }
                }
            }
            final DimensionSlice slice = first("getDimensionSlice", found);
            if (slice != null) {
                return slice;
            }
        }
        return null;
    }

    /**
     * Returns the indice to set at the given dimension. This is relevant only for
     * <var>n</var>-dimensional dataset where <var>n</var>&gt;2. The dimension
     * can be identified by a zero-based index as an {@link Integer}, a dimension name
     * as a {@link String}, or an axis direction as an {@link AxisDirection}. More than
     * one identifier can be specified in order to increase the chance to get the indice,
     * for example:
     *
     * {@preformat java
     *     int indice = getSourceIndiceForDimension("time", AxisDirection.FUTURE);
     * }
     *
     * If different indices are found for the given identifiers, then a warning is emitted and this
     * method returns the indice for the first slice matching the given identifiers. If no indice
     * is found, 0 is returned.
     *
     * @param  dimension {@link Integer}, {@link String} or {@link AxisDirection}
     *         that identify the dimension for which the indice is desired.
     * @return The indice to set at the given dimension, or 0 if no indice were specified.
     *
     * @since 3.08
     */
    public int getSourceIndiceForDimension(final Object... dimension) {
        if (slicesForDimensions != null) {
            Map<Integer,Object> found = null;
            for (final Object id : dimension) {
                final DimensionSlice source = slicesForDimensions.get(id);
                if (source != null) {
                    final Integer indice = source.getIndice();
                    if (found == null) {
                        found = new LinkedHashMap<Integer,Object>(4);
                    }
                    final Object old = found.put(indice, id);
                    if (old != null) {
                        found.put(indice, old); // Keep the old value.
                    }
                }
            }
            final Integer indice = first("getSourceIndiceForDimension", found);
            if (indice != null) {
                return indice;
            }
        }
        return 0;
    }

    /**
     * Returns the dimension to assign to a standard Java API. For example in a dataset having
     * (<var>x</var>, <var>y</var>, <var>z</var>, <var>t</var>) dimensions, it may be useful to
     * handle the <var>z</var> dimension as bands. After the method calls below, users can select
     * one or many elevation indices through the standard {@link #setSourceBands(int[])} API.
     *
     * {@preformat java
     *     DimensionSlice slice = parameters.newDimensionSlice();
     *     slice.setDimensionId(3);
     *     slice.setAPI(DimensionSlice.API.BANDS);
     * }
     *
     * Lets {@code slice} be the {@link DimensionSlice} instance on which the
     * {@linkplain DimensionSlice#setAPI API has been set} to the given {@code api} argument value.
     * The default implementation makes the following choice:
     *
     * <ol>
     *   <li><p>If <code>slice.{@linkplain DimensionSlice#setDimensionId(int) setDimensionId(int)}</code>
     *       has been invoked, then the value specified to that method is returned regardeless the
     *       {@code properties} argument value.</p></li>
     *   <li><p>Otherwise if an other <code>slice.{@linkplain DimensionSlice#setDimensionId(String[])
     *       setDimensionId(...)}</code> method has been invoked and the {@code properties} argument
     *       is non-null, then this method iterates over the given properties. The iteration must
     *       return exactly one element for each dimension, in order.
     *       If an element is equals to a value specified to a {@code setDimensionId(...)} method,
     *       then the position of that element in the {@code properties} iteration is returned.</p></li>
     *   <li><p>Otherwise this method returns 0 for {@link DimensionSlice.API#COLUMNS}, 1 for
     *       {@link DimensionSlice.API#ROWS ROWS}, 2 for {@link DimensionSlice.API#BANDS BANDS}
     *       or -1 otherwise.</p></li>
     * </ol>
     *
     * If more than one dimension match, then a warning is emitted and this method returns the
     * dimension index of the first slice matching the given properties.
     *
     * @param  api The API for which to get the dimension index. This is typically
     *         {@link DimensionSlice.API#BANDS BANDS}.
     * @param  properties Contains one property (the dimension name as a {@link String} or the axis
     *         direction as an {@link AxisDirection}) for each dimension. The iteration order must
     *         be the order of dimensions in the dataset. This argument can be {@code null} if there
     *         is no such properties.
     * @return The dimension assigned to the given API, or -1 if none.
     *
     * @since 3.08
     */
    public int getDimensionForAPI(final DimensionSlice.API api, final Iterable<?> properties) {
        // Note: we want a NullPointerException if 'api' is null.
        if (!api.equals(DimensionSlice.API.NONE) && apiMapping != null) {
            final DimensionSlice slice = apiMapping[api.ordinal()];
            if (slice != null) {
                /*
                 * Get all identifiers for the slice. If an explicit dimension
                 * index is found in the process, it will be returned immediately.
                 */
                Set<Object> identifiers = null;
                for (final Map.Entry<Object,DimensionSlice> entry : slicesForDimensions.entrySet()) {
                    if (slice.equals(entry.getValue())) {
                        final Object key = entry.getKey();
                        if (key instanceof Integer) {
                            return (Integer) key;
                        }
                        if (identifiers == null) {
                            identifiers = new HashSet<Object>(8);
                        }
                        identifiers.add(key);
                    }
                }
                /*
                 * No explicit dimension found. Now searchs for an element from the
                 * given iterator which would be one of the declared identifiers.
                 */
                if (properties != null && identifiers != null) {
                    Map<Integer,Object> found = null;
                    int position = 0;
                    for (Object property : properties) {
                        /*
                         * Undocumented (for now) feature: if we have Map.Entry<?,Integer>,
                         * the value will be the dimension. This allow us to pass more than
                         * one property per dimension.
                         */
                        if (property instanceof Map.Entry<?,?>) {
                            final Map.Entry<?,?> entry = (Map.Entry<?,?>) property;
                            property = entry.getKey();
                            position = (Integer) entry.getValue();
                        }
                        if (identifiers.contains(property)) {
                            if (found == null) {
                                found = new LinkedHashMap<Integer,Object>(4);
                            }
                            final Object old = found.put(position, property);
                            if (old != null) {
                                found.put(position, old); // Keep the first value.
                            }
                        }
                        position++;
                    }
                    final Integer dimension = first("getDimensionForAPI", found);
                    if (dimension != null) {
                        return dimension;
                    }
                }
            }
        }
        switch (api) {
            case COLUMNS: return 0;
            case ROWS:    return 1;
            case BANDS:   return 2;
            default:      return -1;
        }
    }

    /**
     * Ensures that the specified band number is valid.
     */
    private void ensureValidBand(final int band) throws IllegalArgumentException {
        if (band < 0) {
            throw new IllegalArgumentException(getErrorResources().getString(
                    Errors.Keys.BAD_BAND_NUMBER_$1, band));
        }
    }

    /**
     * Returns the band to display in the target image. In theory, images backed by
     * {@linkplain java.awt.image.IndexColorModel index color model} should have only
     * one band. But sometime we want to load additional bands as numerical data, in
     * order to perform computations. In such case, we need to specify which band in
     * the destination image will be used as an index for displaying the colors. The
     * default value is 0.
     *
     * @return The band to display in the target image.
     */
    public int getVisibleBand() {
        return visibleBand;
    }

    /**
     * Sets the band to make visible in the destination image.
     *
     * @param  visibleBand The band to make visible.
     * @throws IllegalArgumentException if the specified band index is invalid.
     */
    public void setVisibleBand(final int visibleBand) throws IllegalArgumentException {
        ensureValidBand(visibleBand);
        this.visibleBand = visibleBand;
    }

    /**
     * Returns a name of the color palette, or a {@linkplain #DEFAULT_PALETTE_NAME default name}
     * if none were explicitly specified.
     */
    final String getNonNullPaletteName() {
        final String palette = getPaletteName();
        return (palette != null) ? palette : DEFAULT_PALETTE_NAME;
    }

    /**
     * Returns the name of the color palette to apply when creating an
     * {@linkplain java.awt.image.IndexColorModel index color model}.
     * This is the name specified by the last call to {@link #setPaletteName(String)}.
     * <p>
     * For a table of available palette names in the default Geotk installation,
     * see the {@link PaletteFactory} class javadoc.
     *
     * @return The name of the color palette to apply.
     */
    public String getPaletteName() {
        return palette;
    }

    /**
     * Sets the color palette as one of the {@linkplain PaletteFactory#getAvailableNames available
     * names} provided by the {@linkplain PaletteFactory#getDefault default palette factory}. This
     * name will be given by the {@link SpatialImageReader} default implementation to the
     * {@linkplain PaletteFactory#getDefault default palette factory} for creating a
     * {@linkplain javax.imageio.ImageTypeSpecifier image type specifier}.
     * <p>
     * For a table of available palette names in the default Geotk installation,
     * see the {@link PaletteFactory} class javadoc.
     *
     * @param palette The name of the color palette to apply.
     *
     * @see PaletteFactory#getAvailableNames
     */
    public void setPaletteName(final String palette) {
        this.palette = palette;
    }

    /**
     * Returns the locale used for formatting error messages, or {@code null} if none.
     * The default implementation returns the locale used by the {@link ImageReader}
     * given at construction time, or {@code null} if none.
     */
    @Override
    public Locale getLocale() {
        return (reader != null) ? reader.getLocale() : null;
    }

    /**
     * Returns a string representation of this block of parameters.
     */
    @Override
    public String toString() {
        final int[]     sourceBands  = this.sourceBands;
        final Rectangle sourceRegion = this.sourceRegion;
        final int sourceXSubsampling = this.sourceXSubsampling;
        final int sourceYSubsampling = this.sourceYSubsampling;
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this));
        buffer.append('[');
        if (sourceRegion != null) {
            buffer.append("sourceRegion=(").
                   append(sourceRegion.x).append(':').append(sourceRegion.x + sourceRegion.width).append(',').
                   append(sourceRegion.y).append(':').append(sourceRegion.y + sourceRegion.height).append("), ");
        }
        if (sourceXSubsampling != 1 || sourceYSubsampling != 1) {
            buffer.append("subsampling=(").append(sourceXSubsampling).append(',').
                    append(sourceYSubsampling).append("), ");
        }
        if (sourceBands != null) {
            buffer.append("sourceBands={");
            for (int i=0; i<sourceBands.length; i++) {
                if (i != 0) {
                    buffer.append(',');
                }
                buffer.append(sourceBands[i]);
            }
            buffer.append("}, ");
        }
        return buffer.append("palette=\"").append(palette).append("\", ").append(']').toString();
    }
}

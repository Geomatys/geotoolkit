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
import java.util.List;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.awt.Rectangle;
import java.awt.image.IndexColorModel;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;

import org.opengis.referencing.cs.AxisDirection;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.IndexedResourceBundle;
import org.geotoolkit.util.converter.Classes;


/**
 * Default parameters for {@link SpatialImageReader}. This class extends the standard
 * {@link ImageReadParam} class with the following additional capabilities:
 * <p>
 * <ul>
 *   <li><p>Specify the slice to read in datasets having more than 2 dimensions, as for example
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
 * Geotk handles extra dimensions with the following policies:
 *
 * <ul>
 *   <li><p>The two first dimensions - typically named (<var>x</var>, <var>y</var>) - are
 *     assigned to the (<var>columns</var>, <var>rows</var>) pixel indices.</p></li>
 *
 *   <li><p>The third dimension is assigned to band indices (see {@link #DEFAULT_DIMENSION_FOR_BANDS}
 *     for more details). This is typically the altitude (<var>z</var>) in a dataset having the
 *     (<var>x</var>, <var>y</var>, <var>z</var>, <var>t</var>) dimensions. This behavior can be
 *     changed by invoking the {@link #setDimensionForBands(int)} method.</p></li>
 *
 *   <li><p>Only one slice of every extra dimensions can be read. By default the data at indice 0
 *     are loaded, but different indices can be selected by invoking
 *     {@link #setSourceIndiceForDimension(int, int)}.</p></li>
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
public class SpatialImageReadParam extends ImageReadParam {
    /**
     * The default dimension to assign to band indices, 0-based. Its value is {@value} which
     * means that if a dataset has (<var>x</var>, <var>y</var>, <var>z</var>, <var>t</var>)
     * dimensions, then by default the <var>z</var> dimension will be assigned to bands as
     * below:
     * <p>
     * <ul>
     *   <li>The (<var>x</var>,<var>y</var>) plane at <var>z</var><sub>0</sub> is stored in band 0</li>
     *   <li>The (<var>x</var>,<var>y</var>) plane at <var>z</var><sub>1</sub> is stored in band 1</li>
     *   <li><i>etc.</i></li>
     * </ul>
     *
     * @see #getDimensionForBands(Iterable)
     * @see #setDimensionForBands(int)
     * @see #setDimensionForBands(String[])
     * @see #setDimensionForBands(AxisDirection[])
     *
     * @since 3.08
     */
    public static final int DEFAULT_DIMENSION_FOR_BANDS = 2;

    /**
     * The name of the default color palette to apply when none was explicitly specified.
     * The default palette is {@value}.
     *
     * @see #getPaletteName()
     * @see #setPaletteName(String)
     */
    public static final String DEFAULT_PALETTE_NAME = "grayscale";

    /**
     * The dimension to use as image bands, or {@code null} for the default behavior.
     * The dimension can be identified by a single {@link Integer}, by a name as a set
     * of {@link String}s, or as an axis direction as a set of {@link AxisDirection}s.
     * See class javadoc for details.
     *
     * @since 3.08
     */
    private Object dimensionForBands;

    /**
     * For <var>n</var>-dimensional images, the indices to use for dimensions above 2.
     * Will be created only when first needed.
     */
    private Map<Object,Integer> sourceIndicesForDimensions;

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
        return Errors.getResources((reader != null) ? reader.getLocale() : null);
    }

    /**
     * Convenience method for logging a warning from the given method.
     */
    private void warningOccurred(final String method, final String message) {
        if (reader instanceof SpatialImageReader) {
            ((SpatialImageReader) reader).warningOccurred(SpatialImageReadParam.class, method, message);
        } else {
            final LogRecord record = new LogRecord(Level.WARNING, message);
            record.setSourceClassName(SpatialImageReadParam.class.getName());
            record.setSourceMethodName(method);
            record.setLoggerName(SpatialImageReader.LOGGER.getName());
            SpatialImageReader.LOGGER.log(record);
        }
    }

    /**
     * Returns {@code true} if the parameters contain at least one
     * {@linkplain #getSourceIndiceForDimension(Object[]) source indice} defined to a value
     * different than 0. This is the case if at least one {@code setSourceIndiceForDimension(...)}
     * method has been invoked with an {@code indice} argument different than zero.
     * <p>
     * This method is relevant only for <var>n</var>-dimensional dataset where <var>n</var>&gt;2.
     *
     * @return {@code true} if the parameters contain at least one indice defined
     *         to a value different than 0.
     *
     * @since 3.08
     */
    public boolean hasNonDefaultSourceIndices() {
        return sourceIndicesForDimensions != null && !sourceIndicesForDimensions.isEmpty();
    }

    /**
     * Returns the first key in the given map. If the map has more than one entry,
     * a warning is emitted. If the map is empty, {@code null} is returned.
     *
     * @param  methodName  The method which is invoking this method, used for logging purpose.
     * @param  found       The map from which to extract the first key.
     * @return The first key in the given map, or {@code null} if none.
     */
    private Integer firstIndice(final String methodName, final Map<Integer,?> found) {
        final int size = found.size();
        if (size == 0) {
            return null;
        }
        /*
         * At least one (indice, property) pair has been found.  We will return the
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
            for (final int indice : found.keySet()) {
                if (buffer.length() != 0) {
                    buffer.append(',');
                }
                buffer.append(' ').append(indice);
            }
            message = buffer.toString();
            warningOccurred(methodName, message);
        }
        return found.keySet().iterator().next();
    }

    /**
     * Returns the indice to set at the given dimension. This is relevant only for
     * <var>n</var>-dimensional dataset where <var>n</var>&gt;2 (note that dimension
     * {@value #DEFAULT_DIMENSION_FOR_BANDS} may be assigned to bands). The dimension
     * can be identified by a zero-based index as an {@link Integer}, a dimension name
     * as a {@link String}, or an axis direction as an {@link AxisDirection}. More than
     * one identifier can be specified in order to increase the chance to get the indice,
     * for example:
     *
     * {@preformat java
     *     int indice = getSourceIndiceForDimension("time", AxisDirection.FUTURE);
     * }
     *
     * If different indices are found for the given identifiers, a warning is emitted
     * and the first indice is returned. If no indice is found, 0 is returned.
     *
     * @param  dimension {@link Integer}, {@link String} or {@link AxisDirection}
     *         that identify the dimension for which the indice is desired.
     * @return The indice to set at the given dimension, or 0 if no indice were specified.
     *
     * @since 3.08
     */
    public int getSourceIndiceForDimension(final Object... dimension) {
        if (sourceIndicesForDimensions != null) {
            final Map<Integer,Object> found = new LinkedHashMap<Integer,Object>(4);
            for (final Object id : dimension) {
                final Integer indice = sourceIndicesForDimensions.get(id);
                if (indice != null) {
                    final Object old = found.put(indice, id);
                    if (old != null) {
                        found.put(indice, old); // Keep the old value.
                    }
                }
            }
            final Integer indice = firstIndice("getSourceIndiceForDimension", found);
            if (indice != null) {
                return indice;
            }
        }
        return 0;
    }

    /**
     * Sets the indice of the slice to read in the given dimension. This is relevant only
     * for <var>n</var>-dimensional dataset where <var>n</var>&gt;2 (note that dimension
     * {@value #DEFAULT_DIMENSION_FOR_BANDS} may be assigned to bands). For example in
     * a 4-D dataset having (<var>x</var>, <var>y</var>, <var>z</var>, <var>t</var>) dimensions,
     * users may want to read the (<var>x</var>, <var>y</var>) plane at time <var>t</var><sub>25</sub>.
     * This can be done by invoking:
     *
     * {@preformat java
     *     setSourceIndiceForDimension(25, 3);
     * }
     *
     * If no {@code setSourceIndiceForDimension} method is invoked, then the default value is 0.
     * This means that for the above-cited 4-D dataset, only the image at the first
     * time (<var>t</var><sub>0</sub>) and first altitude (<var>z</var><sub>0</sub>)
     * is selected by default. See <cite>Handling more than two dimensions</cite> in
     * <a href="#skip-navbar_top">class javadoc</a> for more details.
     *
     * @param indice The indice of the plane to read in the given dimension.
     * @param dimension The 0-based index of the dimension for which the indice is set.
     *
     * @since 3.08
     */
    public void setSourceIndiceForDimension(final int indice, final int dimension) {
        setSourceIndiceForDimension(new Integer[] {dimension}, indice);
    }

    /**
     * Sets the indice of the slice to read in the given dimension. This is relevant only
     * for <var>n</var>-dimensional dataset where <var>n</var>&gt;2 (note that dimension
     * {@value #DEFAULT_DIMENSION_FOR_BANDS} may be assigned to bands). The dimension is
     * identified by name, which works only with file formats that provide support for named
     * dimensions (e.g. NetCDF).
     * <p>
     * For example in a 4-D dataset having (<var>x</var>, <var>y</var>, <var>z</var>, <var>t</var>)
     * dimensions, users may want to read the (<var>x</var>, <var>y</var>) plane at time
     * <var>t</var><sub>25</sub>. This can be done by invoking the following (assuming
     * that the time dimension is named "<cite>time</cite>" in the file to be read):
     *
     * {@preformat java
     *     setSourceIndiceForDimension(25, "time");
     * }
     *
     * More than one name can be specified if they should be considered as the same dimension.
     * For example in order to set the indice in the <var>z</var> dimension, it may be necessary
     * to specify both the {@link "height"} and {@code "depth"} names.
     *
     * @param indice The indice of the plane to read in the given dimension.
     * @param dimension The name of the dimension for which the indice is set.
     *
     * @since 3.08
     */
    public void setSourceIndiceForDimension(final int indice, final String... dimension) {
        setSourceIndiceForDimension(dimension, indice);
    }

    /**
     * Sets the indice of the slice to read in the given dimension. This is relevant only
     * for <var>n</var>-dimensional dataset where <var>n</var>&gt;2 (note that dimension
     * {@value #DEFAULT_DIMENSION_FOR_BANDS} may be assigned to bands). The dimension is
     * identified by axis direction.
     * <p>
     * For example in a 4-D dataset having (<var>x</var>, <var>y</var>, <var>z</var>, <var>t</var>)
     * dimensions, users may want to read the (<var>x</var>, <var>y</var>) plane at time
     * <var>t</var><sub>25</sub>. This can be done by invoking the following:
     *
     * {@preformat java
     *     setSourceIndiceForDimension(25, AxisDirection.FUTURE);
     * }
     *
     * More than one direction can be specified if they should be considered as the same dimension.
     * For example in order to set the indice in the <var>z</var> dimension, it may be necessary to
     * specify both the {@link AxisDirection#UP UP} and {@link AxisDirection#DOWN DOWN} directions.
     *
     * @param indice The indice of the plane to read in the given dimension.
     * @param dimension The name of the dimension for which the indice is set.
     *
     * @since 3.08
     */
    public void setSourceIndiceForDimension(final int indice, final AxisDirection... dimension) {
        setSourceIndiceForDimension(dimension, indice);
    }

    /**
     * Implementation of {@code setSourceIndiceForDimension(...)} methods.
     */
    private void setSourceIndiceForDimension(final Object[] dimensions, final int indice) {
        if (indice < 0) {
            throw new IllegalArgumentException(getErrorResources()
                    .getString(Errors.Keys.ILLEGAL_ARGUMENT_$2, "indice", indice));
        }
        if (indice != 0) {
            if (sourceIndicesForDimensions == null) {
                sourceIndicesForDimensions = new HashMap<Object,Integer>();
            }
            for (final Object dimension : dimensions) {
                sourceIndicesForDimensions.put(dimension, indice);
            }
        } else if (sourceIndicesForDimensions != null) {
            for (final Object dimension : dimensions) {
                sourceIndicesForDimensions.remove(dimension);
            }
        }
    }

    /**
     * Returns the dimension to assign to bands. The default implementation makes the following
     * choice:
     * <p>
     * <ol>
     *   <li>If {@link #setDimensionForBands(int)} has been invoked, then the value specified
     *       to that method is returned regardeless the {@code properties} argument value.</li>
     *   <li>Otherwise if a {@code setDimensionForBands(...)} method has been invoked and the
     *       {@code properties} argument is non-null, then this method iterates over the given
     *       properties (<em>note</em>: the iteration order must be the dimension order). If an
     *       element is equals to a value specified to a {@code setDimensionForBands(...)} method,
     *       then the position of that element in the {@code properties} iteration is returned.</li>
     *   <li>Otherwise this method returns {@value #DEFAULT_DIMENSION_FOR_BANDS}.</li>
     * </ol>
     * <p>
     * If more than one dimension match, then a warning is emitted and the first dimension
     * is returned.
     *
     * @param  properties Contains one property (the dimension name as a {@link String} or the axis
     *         direction as an {@link AxisDirection}) for each dimension. The iteration order must
     *         be the order of dimensions in the dataset. This argument can be {@code null} if there
     *         is no such properties.
     * @return The dimension assigned to bands.
     *
     * @see #DEFAULT_DIMENSION_FOR_BANDS
     *
     * @since 3.08
     */
    public int getDimensionForBands(final Iterable<?> properties) {
        final Object dimensionForBands = this.dimensionForBands;
        if (dimensionForBands instanceof Integer) {
            return (Integer) dimensionForBands;
        }
        if (properties != null && dimensionForBands != null) {
            final Set<?> set = (Set<?>) dimensionForBands;
            final Map<Integer,Object> found = new LinkedHashMap<Integer,Object>(4);
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
                if (set.contains(property)) {
                    final Object old = found.put(position, property);
                    if (old != null) {
                        found.put(position, old); // Keep the first value.
                    }
                }
                position++;
            }
            final Integer dimension = firstIndice("getDimensionForBands", found);
            if (dimension != null) {
                return dimension;
            }
        }
        return DEFAULT_DIMENSION_FOR_BANDS;
    }

    /**
     * Assigns a dimension to band indices. For example in a dataset having (<var>x</var>,
     * <var>y</var>, <var>z</var>, <var>t</var>) dimensions, it may be useful to handle the
     * <var>t</var> dimension as bands. After invoking this method with the {@code 3} argument
     * value, users can select one or many time indices through the standard
     * {@link #setSourceBands(int[])} API.
     * <p>
     * Invoking this method discard the value set by previous call to any {@code setDimensionForBands(...)}
     * method. If no {@code setDimensionForBands} method is invoked, then the default value is
     * {@value #DEFAULT_DIMENSION_FOR_BANDS}. See <cite>Handling more than two dimensions</cite>
     * in <a href="#skip-navbar_top">class javadoc</a> for more details.
     *
     * @param dimension The 0-based index of the dimension to assign to bands.
     *        Shall be equals or greater than {@value #DEFAULT_DIMENSION_FOR_BANDS}.
     *
     * @since 3.08
     */
    public void setDimensionForBands(final int dimension) {
        if (dimension < DEFAULT_DIMENSION_FOR_BANDS) {
            throw new IllegalArgumentException(getErrorResources().getString(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "dimension", dimension));
        }
        dimensionForBands = dimension;
    }

    /**
     * Assigns a dimension to band indices. The dimension is identified by name, which works only
     * with file formats that provide support for named dimensions (e.g. NetCDF). For example in
     * a dataset having (<var>x</var>, <var>y</var>, <var>z</var>, <var>t</var>) dimensions, it may
     * be useful to handle the <var>t</var> dimension as bands. After invoking this method with the
     * {@code "time"} argument value (assuming the dimension is named that way), users can select
     * one or many time indices through the standard {@link #setSourceBands(int[])} API.
     * <p>
     * More than one name can be specified if they should be considered as the same dimension.
     * For example in order to assign the <var>z</var> dimension to bands, it may be necessary
     * to specify both the {@link "height"} and {@code "depth"} names.
     * <p>
     * Invoking this method discard the value set by previous call to the
     * {@link #setDimensionForBands(int)} method. Note however that this method
     * can be combined with {@link #setDimensionForBands(AxisDirection[])}.
     *
     * @param names The name(s) of the dimension to assign to bands.
     *
     * @since 3.08
     */
    public void setDimensionForBands(final String... names) {
        dimensionForBands(names);
    }

    /**
     * Assigns a dimension to band indices. The dimension is identified by axis direction. For
     * example in a dataset having (<var>x</var>, <var>y</var>, <var>z</var>, <var>t</var>)
     * dimensions, it may be useful to handle the <var>t</var> dimension as bands. After invoking
     * this method with the {@link AxisDirection#FUTURE} argument value, users can select one or
     * many time indices through the standard {@link #setSourceBands(int[])} API.
     * <p>
     * More than one direction can be specified if they should be considered as the same dimension.
     * For example in order to assign the <var>z</var> dimension to bands, it may be necessary to
     * specify both the {@link AxisDirection#UP UP} and {@link AxisDirection#DOWN DOWN} directions.
     * <p>
     * Invoking this method discard the value set by previous call to the
     * {@link #setDimensionForBands(int)} method. Note however that this method
     * can be combined with {@link #setDimensionForBands(String[])}.
     *
     * @param directions The direction(s) of the dimension to assign to bands.
     *
     * @since 3.08
     */
    public void setDimensionForBands(final AxisDirection... directions) {
        dimensionForBands(directions);
    }

    /**
     * Implementation of {@code setDimensionForBands(...)} methods.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private void dimensionForBands(final Object[] identifiers) {
        final List<Object> list = Arrays.asList(identifiers);
        final Set<Object> ids;
        if (dimensionForBands instanceof Set<?>) {
            ids = (Set) dimensionForBands;
            ids.addAll(list);
        } else {
            ids = new HashSet<Object>(list);
            dimensionForBands = ids;
        }
        ids.remove(null);
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

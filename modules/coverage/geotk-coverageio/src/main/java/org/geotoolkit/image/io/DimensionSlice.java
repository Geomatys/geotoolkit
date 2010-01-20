/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import java.util.Map;
import java.util.Locale;
import java.awt.Point;
import java.awt.Rectangle;
import javax.imageio.IIOParam;

import org.opengis.referencing.cs.AxisDirection;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.IndexedResourceBundle;
import org.geotoolkit.util.Localized;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.converter.Classes;


/**
 * Selects the indice of the data to read/write along an arbitrary dimension. This class is relevant
 * only for <var>n</var>-dimensional datasets where <var>n</var>&gt;2. Each {@code DimensionSlice}
 * instance applies to only one dimension. Many instances are required for defining the selection
 * along all dimensions.
 *
 * {@note The <code>DimensionSlice</code> name is used in the WCS 2.0 specification for the same
 * purpose. The semantic of attributes are similar but not identical: the <code>getDimensionIds()</code>
 * method in this class is equivalent to the <code>dimension</code> attribute in WCS 2.0, and the
 * <code>getIndice()</code> method is close to the <code>slicePoint</code> attribute. The main
 * differences compared to WCS 2.0 are:
 * <p>
 * <ul>
 *   <li>The dimension can be identified by index or by axis direction, in addition to the name.</li>
 *   <li>The dimension can have more than one identifier; the additional identifiers are aliases.</li>
 *   <li>The slice point is an indice in the discrete coverage, instead than a value in
 *       the continuous coverge.</li>
 * </ul>}
 *
 * This class refers always to the indices in the file: when used with {@link SpatialImageReadParam},
 * this class contains the indice of the section to read from the file (the <cite>source region</cite>).
 * When used with {@link SpatialImageWriteParam}, this class contains the indice of the section to
 * write in the file (the <cite>destination region</cite>).
 * <p>
 * {@code DimensionSlice} contains the indice from which to read the data or where to write the
 * data, and the dimension on which the above indice applies. The dimension can be identified in
 * any of the following ways:
 *
 * <ul>
 *   <li><p>A zero-based index as an {@link Integer}. This is the most straightforward approach
 *     when the set of dimensions if known. For example if the dimensions are known to be
 *     (<var>x</var>, <var>y</var>, <var>z</var>, <var>t</var>), then the <var>t</var> dimension
 *     is at index 3.</p></li>
 *
 *   <li><p>A dimension name as a {@link String}. This is a better approach than indexes when each
 *     dimension has a known name, but the list of available dimensions and their order may vary.
 *     For example if the list of dimensions can be either ({@code "longitude"}, {@code "latitude"},
 *     {@code "depth"}, {@code "time"}) or ({@code "longitude"}, {@code "latitude"}, {@code "time"}),
 *     then the index of the time dimension can be either 2 or 3. It is better to identify the time
 *     dimension by its {@code "time"} name, which is insensitive to whatever the depth dimension
 *     exists or not.</p></li>
 *
 *   <li><p>An axis direction as an {@link AxisDirection}. This provides similar benefit to named
 *     dimension, but can work without knowledge of the actual name and can be used with file
 *     formats that don't support named dimensions.</p></li>
 * </ul>
 *
 * More than one identifier can be used in order to increase the chance to find the dimension.
 * For example in order to fetch the <var>z</var> dimension, it may be necessary to specify both
 * the {@code "height"} and {@code "depth"} names. In case of ambiguity, a warning will be emitted
 * at image reading time (see the example #2 below).
 * <p>
 * Instances of {@code DimensionSlice} can be created and used as below:
 *
 * {@preformat java
 *     SpatialImageReadParam parameters = imageReader.getDefaultReadParam();
 *
 *     DimensionSlice timeSlice = parameters.newDimensionSlice();
 *     timeSlice.addDimensionId("time");
 *     timeSlice.setIndice(25);
 *
 *     DimensionSlice depthSlice = parameters.newDimensionSlice();
 *     depthSlice.addDimensionId("depth");
 *     depthSlice.setIndice(40);
 *
 *     // Read the (x,y) plane at time[25] and depth[40]
 *     BufferedImage image = imageReader.read(0, parameters);
 * }
 *
 * {@section Example 1}
 * In a 4-D dataset having (<var>x</var>, <var>y</var>, <var>z</var>, <var>t</var>) dimensions
 * when the time is known to be the dimension at index 3 (0-based numbering), read the
 * (<var>x</var>, <var>y</var>) plane at time <var>t</var><sub>25</sub>:
 *
 * {@preformat java
 *     DimensionSlice timeSlice = parameters.newDimensionSlice();
 *     timeSlice.addDimensionId(3);
 *     timeSlice.setIndice(25);
 * }
 *
 * If no {@code setIndice(int)} method is invoked, then the default value is 0.
 * This means that for the above-cited 4-D dataset, only the image at the first
 * time (<var>t</var><sub>0</sub>) and first altitude (<var>z</var><sub>0</sub>)
 * is selected by default. See <cite>Handling more than two dimensions</cite> in
 * {@link SpatialImageReadParam} javadoc for more details.
 *
 * {@section Example 2}
 * Read the (<var>x</var>, <var>y</var>) plane at elevation <var>z</var><sub>25</sub>,
 * where the index of the <var>z</var> dimension is unknown. We don't even known if the
 * <var>z</var> dimension is actually a height or a depth. We can identify the dimension
 * by its name, but it works only with file formats that provide support for named dimensions
 * (like NetCDF). So we also identify the dimension by axis directions, which should works
 * for any format:
 *
 * {@preformat java
 *     DimensionSlice elevationSlice = parameters.newDimensionSlice();
 *     elevationSlice.addDimensionId("height", "depth");
 *     elevationSlice.addDimensionId(AxisDirection.UP, AxisDirection.DOWN);
 *     elevationSlice.setIndice(25);
 * }
 *
 * If there is ambiguity (for example a dimension named {@code "height"} and an other dimension
 * named {@code "depth"}, then a warning will be emitted at reading time and the indice 25 will
 * be set to the dimension named {@code "height"} because that name has been specified first.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 * @module
 */
public class DimensionSlice {
    /**
     * The standard Java API used for selecting the data to read or write. The selection can be
     * performed by specifying the {@linkplain IIOParam#getSourceRegion() source region} for the
     * two first dimensions, the {@linkplain IIOParam#getSourceBands() source bands} for a third
     * dimension, or the image index for the fourth dimension. Extra-dimensions if any can not be
     * specified by an API from the standard Java library.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.08
     *
     * @since 3.08
     * @module
     */
    public static enum API {
        /**
         * The region to read/write along a dimension is specified by the
         * {@linkplain Rectangle#x x} and {@linkplain Rectangle#width width}
         * attributes of the {@linkplain IIOParam#getSourceRegion() source region}.
         */
        COLUMNS,

        /**
         * The region to read/write along a dimension is specified by the
         * {@linkplain Rectangle#y y} and {@linkplain Rectangle#height height}
         * attributes of the {@linkplain IIOParam#getSourceRegion() source region}.
         */
        ROWS,

        /**
         * The region to read/write along a dimension is specified by the
         * {@linkplain IIOParam#getSourceBands() source bands}.
         */
        BANDS,

        /**
         * The region to read/write along a dimension is specified by the image index. Note that
         * this parameter needs to be given directly to the {@link javax.imageio.ImageReader}
         * instead than the {@link IIOParam} object.
         */
        IMAGES,

        /**
         * Indicates that no standard Java API match the dimension.
         */
        NONE;

        /**
         * All valid API except {@link #NONE}. The indice of each element in the
         * array shall be equals to the ordinal value.
         */
        static final API[] VALIDS = new API[] {
            COLUMNS, ROWS, BANDS, IMAGES
        };
    }

    /**
     * The parameters that created this object.
     */
    private final IIOParam parameters;

    /**
     * A reference to the {@code slicesForDimensions} map in the {@link #parameters}.
     */
    private final Map<Object,DimensionSlice> paramMap;

    /**
     * A reference to the {@code apiMapping} array in the {@link #parameters}.
     */
    private final DimensionSlice[] apiMapping;

    /**
     * {@code true} if this section is for specifying the target indices,
     * or {@code false} for specifying the source indices.
     */
    private final boolean write;

    /**
     * The indice of the region to read along the dimension that this
     * {@code DimensionSlice} object represents.
     */
    private int indice;

    /**
     * Creates a new {@code DimensionSlice} instance. The arguments given to this method
     * are direct references to the internal objects of {@link SpatialImageReadParam} and
     * are shared by every instances of {@code DimensionSlice} for a given parameters.
     * This is intentional, since changing the state of this {@code DimensionSlice} may
     * impact the state of other {@code DimensionSlice}s for a given parameters (for example
     * only one {@code DimensionSlice} can be assigned to the bands API). Consequently this
     * constructor can be invoked by {@link SpatialImageReadParam#newSourceSelection()} only,
     * because we want to make sure that the right {@code DimensionSlice} are getting the
     * references to the right internal objects.
     * <p>
     * The same rational applies to {@link SpatialImageWriteParam}.
     *
     * @param parameters The parameters that created this object.
     * @param paramMap   A reference to the map in the parameters object.
     * @param apiMapping A reference to the array in the parameters object.
     * @param write      {@code true} for specifying the target indices, or
     *                   {@code false} for source indices.
     */
    DimensionSlice(final IIOParam parameters, final Map<Object,DimensionSlice> paramMap,
            final DimensionSlice[] apiMapping, final boolean write)
    {
        this.parameters = parameters;
        this.paramMap   = paramMap;
        this.apiMapping = apiMapping;
        this.write      = write;
    }

    /**
     * Creates a new instance initialized to the same values than the given instance.
     * This copy constructor provides a way to substitute the instances created by
     * {@link SpatialImageReadParam#newDimensionSlice()} by custom instances which
     * override some methods, as in the example below:
     *
     * {@preformat java
     *     class MyParameters extends SpatialImageReadParam {
     *         MyParameters(ImageReader reader) {
     *             super(reader);
     *         }
     *
     *         public DimensionSlice newDimensionSlice() {
     *             return new MySelection(super.newDimensionSlice());
     *         }
     *     }
     *
     *     class MySelection extends DimensionSlice {
     *         MySelection(DimensionSlice original) {
     *             super(original);
     *         }
     *
     *         // Override some methods here.
     *     }
     * }
     *
     * @param original The instance to copy.
     */
    protected DimensionSlice(final DimensionSlice original) {
        this.parameters = original.parameters;
        this.paramMap   = original.paramMap;
        this.apiMapping = original.apiMapping;
        this.write      = original.write;
        this.indice     = original.indice;
    }

    /**
     * Returns the resources for formatting error messages.
     */
    private IndexedResourceBundle getErrorResources() {
        Locale locale = null;
        if (parameters instanceof Localized) {
            locale = ((Localized) parameters).getLocale();
        }
        return Errors.getResources(locale);
    }

    /**
     * Adds an identifier for the dimension represented by this object.
     *
     * @param  identifier The identifier to add.
     * @throws IllegalArgumentException If the given identifier is already assigned
     *         to an other {@code DimensionSlice} instance.
     */
    private void add(final Object identifier) throws IllegalArgumentException {
        final DimensionSlice old = paramMap.put(identifier, this);
        if (old != null && !equals(old)) {
            paramMap.put(identifier, old); // Restore the previous value.
            throw new IllegalArgumentException(getErrorResources().getString(
                    Errors.Keys.VALUE_ALREADY_DEFINED_$1, identifier));
        }
    }

    /**
     * Adds one or many identifiers for the dimension represented by this object.
     *
     * @param  argName The argument name, used for producing an error message if needed.
     * @param  identifiers The identifiers to add.
     * @throws IllegalArgumentException If an identifier is already
     *         assigned to an other {@code DimensionSlice} instance.
     */
    private void addDimensionId(final String argName, final Object[] identifiers)
            throws IllegalArgumentException
    {
        for (int i=0; i<identifiers.length; i++) {
            final Object identifier = identifiers[i];
            if (identifier == null) {
                throw new NullArgumentException(getErrorResources().getString(
                        Errors.Keys.NULL_ARGUMENT_$1, argName + '[' + i + ']'));
            }
            add(identifier);
        }
    }

    /**
     * Declares the index for the dimension represented by this object. For example in a 4-D
     * dataset having (<var>x</var>, <var>y</var>, <var>z</var>, <var>t</var>) dimensions where
     * the time dimension is known to be the dimension #3 (0-based numbering), users may want to
     * read the (<var>x</var>, <var>y</var>) plane at time <var>t</var><sub>25</sub>. This can be
     * done by invoking:
     *
     * {@preformat java
     *     addDimensionId(3);
     *     setIndice(25);
     * }
     *
     * @param  index The index of the dimension. Must be non-negative.
     * @throws IllegalArgumentException If the given dimension index is negative
     *         or already assigned to an other {@code DimensionSlice} instance.
     */
    public void addDimensionId(final int index) throws IllegalArgumentException {
        if (index < 2) {
            throw new IllegalArgumentException(getErrorResources().getString(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "index", index));
        }
        add(index);
    }

    /**
     * Adds an identifier for the dimension represented by this object. The dimension is identified
     * by name, which works only with file formats that provide support for named dimensions (e.g.
     * NetCDF). For example in a dataset having either (<var>x</var>, <var>y</var>, <var>z</var>,
     * <var>t</var>) or (<var>x</var>, <var>y</var>, <var>t</var>) dimensions, users may want to
     * read the (<var>x</var>, <var>y</var>) plane at time <var>t</var><sub>25</sub> without knowing
     * if the time dimension is the third or the fourth one. This can be done by invoking:
     *
     * {@preformat java
     *     addDimensionId("time");
     *     setIndice(25);
     * }
     *
     * More than one name can be specified if they should be considered as possible identifiers
     * for the same dimension. For example in order to assign the <var>z</var> dimension to bands,
     * it may be necessary to specify both the {@code "height"} and {@code "depth"} names.
     *
     * @param  names The names of the dimension.
     * @throws IllegalArgumentException If a name is already assigned to an
     *         other {@code DimensionSlice} instance.
     */
    public void addDimensionId(final String... names) throws IllegalArgumentException {
        addDimensionId("names", names);
    }

    /**
     * Adds an identifier for the dimension represented by this object. The dimension is identified
     * by axis direction. For example in a dataset having either (<var>x</var>, <var>y</var>,
     * <var>z</var>, <var>t</var>) or (<var>x</var>, <var>y</var>, <var>t</var>) dimensions, users
     * may want to read the (<var>x</var>, <var>y</var>) plane at time <var>t</var><sub>25</sub>
     * without knowing if the time dimension is the third or the fourth one. This can be done by
     * invoking:
     *
     * {@preformat java
     *     addDimensionId(AxisDirection.FUTURE);
     *     setIndice(25);
     * }
     *
     * More than one direction can be specified if they should be considered as possible identifiers
     * for the same dimension. For example in order to assign the <var>z</var> dimension to bands,
     * it may be necessary to specify both the {@link AxisDirection#UP UP} and
     * {@link AxisDirection#DOWN DOWN} directions.
     *
     * @param  axes The axis directions of the dimension.
     * @throws IllegalArgumentException If a name is already assigned to an
     *         other {@code DimensionSlice} instance.
     */
    public void addDimensionId(final AxisDirection... axes) throws IllegalArgumentException {
        addDimensionId("axes", axes);
    }

    /**
     * Removes identifiers for the dimension represented by this object. The {@code identifiers}
     * argument can contains the identifiers given to any {@code addDimensionId(...)} method.
     * Unknown identifiers are silently ignored.
     *
     * @param identifiers The identifiers to remove.
     */
    public void removeDimensionId(final Object... identifiers) {
        for (final Object identifier : identifiers) {
            final DimensionSlice old = paramMap.remove(identifier);
            if (old != null && !equals(old)) {
                paramMap.put(identifier, old); // Restore the previous state.
            }
        }
    }

    /**
     * Returns {@code true} if the given API can not be assigned to a new dimension.
     */
    private static boolean isReserved(final API api) {
        return API.COLUMNS.equals(api) || API.ROWS.equals(api);
    }

    /**
     * Returns the standard Java API that can be used for selecting a region along the
     * dimension represented by this object. The default value is {@link API#NONE NONE}.
     *
     * @return The standard Java API for selecting a region along the dimension.
     */
    public final API getAPI() {
        for (int i=apiMapping.length; --i>=0;) {
            if (apiMapping[i] == this) {
                return API.VALIDS[i];
            }
        }
        return API.NONE;
    }

    /**
     * Sets the standard Java API to use for selecting a region to read/write along the
     * dimension represented by this object. If the given API was already assigned to an
     * other {@code DimensionSlice} instance, then the API of the other dimension is set
     * to {@link API#NONE NONE}.
     * <p>
     * In the current implementation, the {@link API#COLUMNS COLUMNS} and {@link API#ROWS ROWS}
     * API can not be set because those dimensions are typically hard-coded in image readers and
     * writers.
     *
     * @param  newAPI The standard Java API to use for the dimension.
     * @throws IllegalArgumentException If the given API can not be used with this dimension.
     */
    public void setAPI(final API newAPI) throws IllegalArgumentException {
        final API api = getAPI();
        if (!newAPI.equals(api)) { // We want a NullPointerException if newAPI is null.
            if (isReserved(newAPI) || isReserved(api)) {
                throw new IllegalArgumentException(getErrorResources().getString(
                        Errors.Keys.BAD_PARAMETER_$2, "newAPI", newAPI));
            }
            for (int i=apiMapping.length; --i>=0;) {
                if (apiMapping[i] == this) {
                    apiMapping[i] = null;
                }
            }
            if (!newAPI.equals(API.NONE)) {
                apiMapping[newAPI.ordinal()] = this;
            }
            assert newAPI.equals(getAPI()) : newAPI;
        }
    }

    /**
     * Returns the indice of the section to read or write along the dimension
     * represented by this object. This method applies the following rules:
     * <p>
     * <ul>
     *   <li>For {@link SpatialImageReadParam}:<ul>
     *     <li>If the API is {@link API#COLUMNS COLUMNS} or {@link API#ROWS ROWS}, then this method
     *         invokes {@link IIOParam#getSourceRegion()} and returns the {@link Rectangle#x x} or
     *         {@link Rectangle#y y} attribute respectively, or 0 if the source region is not set.</li>
     *     <li>Otherwise if the API is {@link API#BANDS BANDS}, then this method invokes
     *         {@link IIOParam#getSourceBands()} and returns the indice of the first band,
     *         or 0 if the source bands are not set.</li>
     *     <li>Otherwise this method returns the last value set by {@link #setIndice(int)}.</li>
     *   </ul></li>
     *   <li>For {@link SpatialImageWriteParam}:<ul>
     *     <li>If the API is {@link API#COLUMNS COLUMNS} or {@link API#ROWS ROWS}, then this method
     *         invokes {@link IIOParam#getDestinationOffset()} and returns the {@link Point#x x} or
     *         {@link Point#y y} attribute respectively, or 0 if the offset is not set.</li>
     *     <li>Otherwise this method returns the last value set by {@link #setIndice(int)}.</li>
     *   </ul></li>
     * </ul>
     *
     * {@note This method could have been named <code>getFirstIndice()</code> because it returns
     * the indice of the <em>first</em> element to read (often the lower indice, but not always).
     * However there would be no <code>getLastIndice()</code> method, because the default values
     * to return when the source region or source bands are unspecified depend on information known
     * only to the <code>ImageReader</code> when the input is set. This is probably not a major
     * issue since the main purpose of this method is to get the indice in extra dimensions where
     * no standard Java API is available.}
     *
     * @return The indice of the first element to read.
     */
    @SuppressWarnings("fallthrough")
    public int getIndice() {
        final boolean isY;
        switch (getAPI()) {
            case COLUMNS: {
                isY = false;
                break;
            }
            case ROWS: {
                isY = true;
                break;
            }
            case BANDS: {
                if (!write) {
                    final int[] sourceBands = parameters.getSourceBands();
                    return (sourceBands != null && sourceBands.length != 0) ? sourceBands[0] : 0;
                }
                // Fall through
            }
            default: {
                return indice;
            }
        }
        /*
         * COLUMNS and ROWS cases.
         */
        if (write) {
            final Point offset = parameters.getDestinationOffset();
            if (offset != null) {
                return isY ? offset.y : offset.x;
            }
        } else {
            final Rectangle region = parameters.getSourceRegion();
            if (region != null) {
                return isY ? region.y : region.x;
            }
        }
        return 0;
    }

    /**
     * Sets the indice of the region to read along the dimension represented by this object.
     * This method applies the following rules:
     * <p>
     * <ul>
     *   <li>For {@link SpatialImageReadParam}:<ul>
     *     <li>If the API is {@link API#COLUMNS COLUMNS} or {@link API#ROWS ROWS}, then this method
     *         invokes {@link IIOParam#setSourceRegion(Rectangle)} with a {@link Rectangle#x x} or
     *         {@link Rectangle#y y} attribute set to the given indice, and the corresponding width
     *         or height attribute set to 1.</li>
     *     <li>Otherwise if the API is {@link API#BANDS BANDS}, then this method invokes
     *         {@link IIOParam#setSourceBands(int[])} with the given indice.</li>
     *     <li>Otherwise this method stores the given indice.</li>
     *   </ul></li>
     *   <li>For {@link SpatialImageWriteParam}:<ul>
     *     <li>If the API is {@link API#COLUMNS COLUMNS} or {@link API#ROWS ROWS}, then this method
     *         invokes {@link IIOParam#setDestinationOffset(Point)} with a {@link Point#x x} or
     *         {@link Point#y y} attribute set to the given indice.</li>
     *     <li>Otherwise this method stores the given indice.</li>
     *   </ul></li>
     * </ul>
     *
     * @param indice
     */
    @SuppressWarnings("fallthrough")
    public void setIndice(final int indice) {
        final boolean isY;
        switch (getAPI()) {
            case COLUMNS: {
                isY = false;
                break;
            }
            case ROWS: {
                isY = true;
                break;
            }
            case BANDS: {
                if (!write) {
                    parameters.setSourceBands(new int[] {indice});
                    return;
                }
                // Fall through
            }
            default: {
                this.indice = indice;
                return;
            }
        }
        /*
         * COLUMNS and ROWS cases.
         */
        if (write) {
            final Point offset = parameters.getDestinationOffset();
            if (isY) {
                offset.y = indice;
            } else {
                offset.x = indice;
            }
            parameters.setDestinationOffset(offset);
        } else {
            Rectangle region = parameters.getSourceRegion();
            if (region == null) {
                region = new Rectangle(1,1);
            }
            if (isY) {
                region.y = indice;
                region.height = 1;
            } else {
                region.x = indice;
                region.width = 1;
            }
            parameters.setSourceRegion(region);
        }
    }

    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this)).append("[id={");
        boolean addSeparator = false;
        for (final Map.Entry<Object,DimensionSlice> entry : paramMap.entrySet()) {
            if (entry.getValue() == this) {
                buffer.append(entry.getKey());
                if (addSeparator) {
                    buffer.append(", ");
                }
                addSeparator = true;
            }
        }
        buffer.append('}');
        final API api = getAPI();
        if (!API.NONE.equals(api)) {
            buffer.append(", API=").append(api.name());
        }
        return buffer.append(", indice=").append(getIndice()).append(']').toString();
    }
}

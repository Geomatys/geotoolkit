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
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.logging.LogRecord;
import java.awt.Point;
import java.awt.Rectangle;
import javax.imageio.IIOParam;
import javax.imageio.ImageWriteParam;

import org.opengis.util.CodeList;
import org.opengis.referencing.cs.AxisDirection;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.IndexedResourceBundle;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.internal.image.io.Warnings;


/**
 * Tupple of a <cite>dimension identifier</cite> and <cite>index</cite> in that dimension for a slice
 * to read or write in a data file. This class is relevant mostly for <var>n</var>-dimensional datasets
 * where <var>n</var>&gt;2. Each {@code DimensionSlice} instance applies to only one dimension;
 * if the indices of a slice need to be specified for more than one dimension, then many instances
 * of {@code DimensionSlice} will be required.
 *
 * {@note The <code>DimensionSlice</code> name is used in the WCS 2.0 specification for the same
 * purpose. The semantic of attributes are similar but not identical: the <code>getDimensionIds()</code>
 * method in this class is equivalent to the <code>dimension</code> attribute in WCS 2.0, and the
 * <code>getSliceIndex()</code> method is close to the <code>slicePoint</code> attribute. The main
 * differences compared to WCS 2.0 are:
 * <p>
 * <ul>
 *   <li>The dimension can be identified by an index type, by an axis direction or by the name
 *       type as used in WCS 2.0.</li>
 *   <li>The dimension can be identified by more than one identifier, with conflicts trigging
 *       a warning.</li>
 *   <li>The slice point is an index offset in the discrete coverage, rather than a metric
 *       value in the continuous dimension.</li>
 * </ul>}
 *
 * This class refers always to the indices in the file: when used with {@link SpatialImageReadParam},
 * this class contains the index of the section to read from the file (the <cite>source region</cite>).
 * When used with {@link SpatialImageWriteParam}, this class contains the index of the section to
 * write in the file (the <cite>destination offset</cite>).
 * <p>
 * In addition to the index, {@code DimensionSlice} also specifies the dimension on which the
 * index applies. The dimension can be identified in any of the following ways:
 *
 * <ul>
 *   <li><p>As a zero-based index using an {@link Integer}. This is the most straightforward approach
 *     when the set of dimensions is known. For example if the dimensions are known to be
 *     (<var>x</var>, <var>y</var>, <var>z</var>, <var>t</var>), then the <var>t</var> dimension
 *     is at index 3.</p></li>
 *
 *   <li><p>As a dimension name using a {@link String}. This is a better approach than indexes when
 *     each dimension has a known name, but the list of available dimensions and their order may vary.
 *     For example if the list of dimensions can be either ({@code "longitude"}, {@code "latitude"},
 *     {@code "depth"}, {@code "time"}) or ({@code "longitude"}, {@code "latitude"}, {@code "time"}),
 *     then the index of the time dimension can be either 2 or 3. It is better to identify the time
 *     dimension by its {@code "time"} name, which is insensitive to position, wheter the depth
 *     dimension exists or not.</p></li>
 *
 *   <li><p>As a direction using an {@link AxisDirection}. This provides similar benefit to using
 *     a named dimension, but can work without knowledge of the actual name or can be used with
 *     file formats that don't support named dimensions.</p></li>
 * </ul>
 *
 * More than one identifier can be used in order to increase the chance of finding the dimension.
 * For example in order to fetch the <var>z</var> dimension, it may be necessary to specify both
 * the {@code "height"} and {@code "depth"} names. In case of ambiguity, a
 * {@linkplain SpatialImageReader#warningOccurred warning will be emitted} to any registered
 * listeners at image reading time (see the example #3 below).
 *
 * {@section Example 1: Setting the indices in the time and depth dimensions}
 * Instances of {@code DimensionSlice} can be created and used as below:
 *
 * {@preformat java
 *     SpatialImageReadParam parameters = imageReader.getDefaultReadParam();
 *
 *     DimensionSlice timeSlice = parameters.newDimensionSlice();
 *     timeSlice.addDimensionId("time");
 *     timeSlice.setSliceIndex(25);
 *
 *     DimensionSlice depthSlice = parameters.newDimensionSlice();
 *     depthSlice.addDimensionId("depth");
 *     depthSlice.setSliceIndex(40);
 *
 *     // Read the (x,y) plane at time[25] and depth[40]
 *     BufferedImage image = imageReader.read(0, parameters);
 * }
 *
 * {@section Example 2: Setting the indices in the third dimension}
 * In a 4-D dataset having (<var>x</var>, <var>y</var>, <var>z</var>, <var>t</var>) dimensions
 * when the time is known to be the dimension at index 3 (0-based numbering), read the
 * (<var>x</var>, <var>y</var>) plane at time <var>t</var><sub>25</sub>:
 *
 * {@preformat java
 *     DimensionSlice timeSlice = parameters.newDimensionSlice();
 *     timeSlice.addDimensionId(3);
 *     timeSlice.setSliceIndex(25);
 * }
 *
 * If no {@code setSliceIndex(int)} method is invoked, then the default value is 0. This means that
 * for the above-cited 4-D dataset, only the image at the first time (<var>t</var><sub>0</sub>)
 * is selected by default. See <cite>Handling more than two dimensions</cite> in
 * {@link SpatialImageReadParam} javadoc for more details.
 *
 * {@section Example 3: Setting the index of the vertical axis}
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
 *     elevationSlice.setSliceIndex(25);
 * }
 *
 * If there is ambiguity (for example if both a dimension named {@code "height"} and an other
 * dimension named {@code "depth"} exist), then a {@linkplain SpatialImageReader#warningOccurred
 * warning will be emitted} at reading time and the index 25 will be set to the dimension named
 * {@code "height"} because that name has been specified first.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 * @module
 */
public class DimensionSlice implements WarningProducer {
    /**
     * The standard Java API used for selecting the slice to read or write.
     * The selection can be performed in 3 or 4 dimensions by:
     *
     * <ul>
     *   <li><p>When reading:</p></li>
     *   <ol>
     *     <li>Specifying the {@linkplain Rectangle#x x} ordinate of the
     *         {@linkplain IIOParam#getSourceRegion() source region}.</li>
     *     <li>Specifying the {@linkplain Rectangle#y y} ordinate of the
     *         {@linkplain IIOParam#getSourceRegion() source region}.</li>
     *     <li>Specifying the the {@linkplain IIOParam#getSourceBands() source bands}.</li>
     *     <li>Specifying the image index.</li>
     *   </ol>
     *   <li><p>When writing:</p></li>
     *   <ol>
     *     <li>Specifying the {@linkplain Point#x x} ordinate of the
     *         {@linkplain IIOParam#getDestinationOffset() destination offset}.</li>
     *     <li>Specifying the {@linkplain Point#y y} ordinate of the
     *         {@linkplain IIOParam#getDestinationOffset() destination offset}.</li>
     *     <li>Specifying the image index.</li>
     *   </ol>
     * </ul>
     * <p>
     * Supplemental dimensions if any can not be specified by an API from the standard Java library.
     * {@link DimensionSlice} instances shall be created for those supplemental dimensions.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.08
     *
     * @since 3.08
     * @module
     */
    public static enum API {
        /**
         * The region to read/write along a dimension is specified by the <var>x</var> ordinate.
         * <p>
         * <ul>
         *   <li>On reading, this is the ({@linkplain Rectangle#x x}, {@linkplain Rectangle#width width})
         *       attributes of the {@linkplain IIOParam#getSourceRegion() source region}.</li>
         *   <li>On writing, this is the {@linkplain Point#x x} attribute of the
         *       {@linkplain IIOParam#getDestinationOffset() destination offset}.</li>
         * </ul>
         */
        COLUMNS,

        /**
         * The region to read/write along a dimension is specified by the <var>y</var> ordinate.
         * <p>
         * <ul>
         *   <li>On reading, this is the ({@linkplain Rectangle#y y}, {@linkplain Rectangle#height height})
         *       attributes of the {@linkplain IIOParam#getSourceRegion() source region}.</li>
         *   <li>On writing, this is the {@linkplain Point#y y} attribute of the
         *       {@linkplain IIOParam#getDestinationOffset() destination offset}.</li>
         * </ul>
         */
        ROWS,

        /**
         * The region to read along a dimension is specified by the
         * {@linkplain IIOParam#getSourceBands() source bands}.
         */
        BANDS,

        /**
         * The region to read/write along a dimension is specified by the image index. Note that
         * this parameter needs to be given directly to the {@link javax.imageio.ImageReader} or
         * {@link javax.imageio.ImageWriter} instead than the {@link IIOParam} object.
         */
        IMAGES,

        /**
         * Indicates that no standard Java API match the dimension.
         */
        NONE;

        /**
         * All valid API except {@link #NONE}. The index of each element in the
         * array shall be equals to the ordinal value.
         */
        static final API[] VALIDS = new API[] {
            COLUMNS, ROWS, BANDS, IMAGES
        };
    }

    /**
     * The collection that created this object.
     */
    private final DimensionSlices owner;

    /**
     * The index of the region to read along the dimension that this
     * {@code DimensionSlice} object represents.
     */
    private int index;

    /**
     * Creates a new {@code DimensionSlice} instance.
     *
     * @param owner The collection that created this object.
     */
    DimensionSlice(final DimensionSlices owner) {
        this.owner = owner;
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
        this.owner = original.owner;
        this.index = original.index;
    }

    /**
     * Returns the resources for formatting error messages.
     */
    private IndexedResourceBundle getErrorResources() {
        return Errors.getResources(getLocale());
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
            owner.addDimensionId(this, identifier);
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
     *     setSliceIndex(25);
     * }
     *
     * @param  index The index of the dimension. Must be non-negative.
     * @throws IllegalArgumentException If the given dimension index is negative
     *         or already assigned to an other {@code DimensionSlice} instance.
     */
    public void addDimensionId(final int index) throws IllegalArgumentException {
        if (index < 0) {
            throw new IllegalArgumentException(getErrorResources().getString(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "index", index));
        }
        owner.addDimensionId(this, index);
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
     *     setSliceIndex(25);
     * }
     *
     * More than one name can be specified if they should be considered as possible identifiers
     * for the same dimension. For example in order to set the index for the <var>z</var> dimension,
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
     *     setSliceIndex(25);
     * }
     *
     * More than one direction can be specified if they should be considered as possible identifiers
     * for the same dimension. For example in order to set the index for the <var>z</var> dimension,
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
     * argument can contain the identifiers given to any {@code addDimensionId(...)} method.
     * Unknown identifiers are silently ignored.
     *
     * @param identifiers The identifiers to remove.
     */
    public void removeDimensionId(final Object... identifiers) {
        final Map<Object,DimensionSlice> identifiersMap = owner.identifiersMap;
        if (identifiersMap != null) {
            for (final Object identifier : identifiers) {
                final DimensionSlice old = identifiersMap.remove(identifier);
                if (old != null && !equals(old)) {
                    identifiersMap.put(identifier, old); // Restore the previous state.
                }
            }
        }
        owner.refresh();
    }

    /**
     * Returns the standard Java API that can be used for selecting a region along the
     * dimension represented by this object. The default value is {@link API#NONE NONE}.
     *
     * @return The standard Java API for selecting a region along the dimension.
     *
     * @see SpatialImageReadParam#getDimensionSliceForAPI(DimensionSlice.API)
     */
    public final API getAPI() {
        final DimensionSlice[] apiMapping = owner.apiMapping;
        if (apiMapping != null) {
            for (int i=apiMapping.length; --i>=0;) {
                if (apiMapping[i] == this) {
                    return API.VALIDS[i];
                }
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
     * Note that many plugins don't allow usage of {@link API#COLUMNS COLUMNS} and
     * {@link API#ROWS ROWS} API for anything else than dimensions 0 and 1 respectively.
     * If the given APi can not be set to this dimension slice, then an exception may be
     * thrown at image reading or writing time.
     *
     * @param  newAPI The standard Java API to use for the dimension.
     * @throws IllegalArgumentException If the given API can not be used with this dimension.
     *
     * @see SpatialImageReadParam#getDimensionSliceForAPI(DimensionSlice.API)
     */
    public void setAPI(final API newAPI) throws IllegalArgumentException {
        final API api = getAPI();
        if (!newAPI.equals(api)) { // We want a NullPointerException if newAPI is null.
            final int index = getSliceIndex(api);
            DimensionSlice[] apiMapping = owner.apiMapping;
            if (apiMapping == null) {
                owner.apiMapping = apiMapping = new DimensionSlice[API.VALIDS.length];
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
            setSliceIndex(newAPI, index, index == 0);
        }
    }

    /**
     * Returns the index of the section to read or write along the dimension
     * represented by this object. This method applies the following rules:
     * <p>
     * <ul>
     *   <li>For {@link SpatialImageReadParam}:<ul>
     *     <li>If the API is {@link API#COLUMNS COLUMNS} or {@link API#ROWS ROWS}, then this method
     *         invokes {@link IIOParam#getSourceRegion()} and returns the {@link Rectangle#x x} or
     *         {@link Rectangle#y y} attribute respectively, or 0 if the source region is not set.</li>
     *     <li>Otherwise if the API is {@link API#BANDS BANDS}, then this method invokes
     *         {@link IIOParam#getSourceBands()} and returns the index of the first band,
     *         or 0 if the source bands are not set.</li>
     *     <li>Otherwise this method returns the value set by the last call to
     *         {@link #setSliceIndex(int)}.</li>
     *   </ul></li>
     *   <li>For {@link SpatialImageWriteParam}:<ul>
     *     <li>If the API is {@link API#COLUMNS COLUMNS} or {@link API#ROWS ROWS}, then this method
     *         invokes {@link IIOParam#getDestinationOffset()} and returns the {@link Point#x x} or
     *         {@link Point#y y} attribute respectively, or 0 if the offset is not set.</li>
     *     <li>Otherwise this method returns the value set by the last call to
     *         {@link #setSliceIndex(int)}.</li>
     *   </ul></li>
     * </ul>
     *
     * {@note This method could have been named <code>getFirstIndex()</code> because it returns
     * the index of the <em>first</em> element to read (often the lower index, but not always).
     * However there would be no <code>getLastIndex()</code> method, because the default values
     * to return when the source region or source bands are unspecified depend on information known
     * only to the <code>ImageReader</code> when the input is set. This is probably not a major
     * issue since the main purpose of this method is to get the index in extra dimensions where
     * no standard Java API is available.}
     *
     * @return The index of the first element to read/write in the dimension represented by this
     *         object.
     *
     * @see SpatialImageReadParam#getSliceIndex(Object[])
     */
    public int getSliceIndex() {
        return getSliceIndex(getAPI());
    }

    /**
     * Implementation of {@link #getSliceIndex()}.
     *
     * @param api The value of {@link #getAPI()}.
     */
    @SuppressWarnings("fallthrough")
    private int getSliceIndex(final API api) {
        final boolean isY;
        switch (api) {
            case COLUMNS: {
                isY = false;
                break;
            }
            case ROWS: {
                isY = true;
                break;
            }
            case BANDS: {
                final IIOParam parameters = owner.parameters;
                if (!(parameters instanceof ImageWriteParam)) {
                    final int[] sourceBands = parameters.getSourceBands();
                    return (sourceBands != null && sourceBands.length != 0) ? sourceBands[0] : 0;
                }
                // Fall through
            }
            default: {
                return index;
            }
        }
        /*
         * COLUMNS and ROWS cases.
         */
        final IIOParam parameters = owner.parameters;
        if (parameters instanceof ImageWriteParam) {
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
     * Sets the index of the region to read along the dimension represented by this object.
     * This method applies the following rules:
     * <p>
     * <ul>
     *   <li>For {@link SpatialImageReadParam}:<ul>
     *     <li>If the API is {@link API#COLUMNS COLUMNS} or {@link API#ROWS ROWS}, then this method
     *         invokes {@link IIOParam#setSourceRegion(Rectangle)} with a {@link Rectangle#x x} or
     *         {@link Rectangle#y y} attribute set to the given index, and the corresponding width
     *         or height attribute set to 1.</li>
     *     <li>Otherwise if the API is {@link API#BANDS BANDS}, then this method invokes
     *         {@link IIOParam#setSourceBands(int[])} with the given index.</li>
     *     <li>Otherwise this method stores the given index.</li>
     *   </ul></li>
     *   <li>For {@link SpatialImageWriteParam}:<ul>
     *     <li>If the API is {@link API#COLUMNS COLUMNS} or {@link API#ROWS ROWS}, then this method
     *         invokes {@link IIOParam#setDestinationOffset(Point)} with a {@link Point#x x} or
     *         {@link Point#y y} attribute set to the given index.</li>
     *     <li>Otherwise this method stores the given index.</li>
     *   </ul></li>
     * </ul>
     *
     * @param index The slice point to read/write in the dimension represented by this object.
     *
     * @see SpatialImageReadParam#getSliceIndex(Object[])
     */
    public void setSliceIndex(final int index) {
        setSliceIndex(getAPI(), index, false);
    }

    /**
     * Implementation of {@link #getSliceIndex()}.
     *
     * @param api The value of {@link #getAPI()}.
     */
    @SuppressWarnings("fallthrough")
    private void setSliceIndex(final API api, final int index, final boolean noCreate) {
        final boolean isY;
        switch (api) {
            case COLUMNS: {
                isY = false;
                break;
            }
            case ROWS: {
                isY = true;
                break;
            }
            case BANDS: {
                final IIOParam parameters = owner.parameters;
                if (!(parameters instanceof ImageWriteParam)) {
                    parameters.setSourceBands(new int[] {index});
                    return;
                }
                // Fall through
            }
            default: {
                this.index = index;
                return;
            }
        }
        /*
         * COLUMNS and ROWS cases.
         */
        final IIOParam parameters = owner.parameters;
        if (parameters instanceof ImageWriteParam) {
            final Point offset = parameters.getDestinationOffset();
            if (isY) {
                offset.y = index;
            } else {
                offset.x = index;
            }
            parameters.setDestinationOffset(offset);
        } else {
            Rectangle region = parameters.getSourceRegion();
            if (region == null) {
                if (noCreate) {
                    return;
                }
                region = new Rectangle(1,1);
            }
            if (isY) {
                region.y = index;
                region.height = 1;
            } else {
                region.x = index;
                region.width = 1;
            }
            parameters.setSourceRegion(region);
        }
    }

    /**
     * Returns the index of this dimension in a data file. This method is invoked by some
     * {@link SpatialImageReader} or {@link SpatialImageWriter} subclasses when a dataset
     * is about to be read from a file, or written to a file. The caller needs to know the
     * set of dimensions that exist in the file dataset, which may not be identical to the set
     * of dimensions declared in {@link SpatialImageReadParam} or {@link SpatialImageWriteParam}.
     * <p>
     * The default implementation makes the following choice:
     *
     * <ol>
     *   <li><p>If {@link #addDimensionId(int)} has been invoked, then the value specified to
     *       that method is returned regardeless the {@code properties} argument value.</p></li>
     *   <li><p>Otherwise if an other {@link #addDimensionId(String[]) addDimensionId(...)} method
     *       has been invoked and the {@code properties} argument is non-null, then this method
     *       iterates over the given properties. The iteration must return exactly one element
     *       for each dimension, in order. If an element is equals to a value specified to a
     *       {@code addDimensionId(...)} method, then the position of that element in the
     *       {@code properties} iteration is returned.</p></li>
     *   <li><p>Otherwise this method returns -1.</p></li>
     * </ol>
     *
     * If more than one dimension match, then a {@linkplain SpatialImageReader#warningOccurred
     * warning is emitted} and this method returns the index of the first property matching an
     * identifier.
     *
     * @param  properties Contains one property (the dimension name as a {@link String} or the axis
     *         direction as an {@link AxisDirection}) for each dimension in the dataset being read
     *         or written. The iteration order shall be the order of dimensions in the dataset. This
     *         argument can be {@code null} if no such properties are available.
     * @return The index of the dimension, or -1 if none.
     */
    public int findDimensionIndex(final Iterable<?> properties) {
        /*
         * Get all identifiers for the slice. If an explicit dimension
         * index is found in the process, it will be returned immediately.
         */
        Set<Object> identifiers = null;
        for (final Map.Entry<Object,DimensionSlice> entry : owner.identifiersMap.entrySet()) {
            if (equals(entry.getValue())) {
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
            final Integer dimension = DimensionSlices.first(found, this, DimensionSlice.class, "findDimensionIndex");
            if (dimension != null) {
                return dimension;
            }
        }
        return -1;
    }

    /**
     * Returns the locale used for formatting error messages, or {@code null} if none.
     */
    @Override
    public Locale getLocale() {
        return owner.getLocale();
    }

    /**
     * Invoked when a warning occurred. The default implementation
     * {@linkplain SpatialImageReader#warningOccurred forwards the warning to the image reader} or
     * {@linkplain SpatialImageWriter#warningOccurred writer} if possible, or logs the warning
     * otherwise.
     */
    @Override
    public boolean warningOccurred(final LogRecord record) {
        return Warnings.log(owner.parameters, record);
    }

    /**
     * Returns a string representation of this object. The default implementation
     * formats on a single line the class name, the list of dimension identifiers,
     * the {@linkplain #getSliceIndex() index} and the {@linkplain #getAPI() API} (if any).
     *
     * @see SpatialImageReadParam#toString()
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this)).append("[id={");
        boolean addSeparator = false;
        for (final Map.Entry<Object,DimensionSlice> entry : owner.identifiersMap.entrySet()) {
            if (entry.getValue() == this) {
                Object key = entry.getKey();
                final boolean addQuotes = (key instanceof CharSequence);
                if (key instanceof CodeList<?>) {
                    key = ((CodeList<?>) key).name();
                }
                if (addSeparator) {
                    buffer.append(", ");
                }
                if (addQuotes) {
                    buffer.append('"');
                }
                buffer.append(key);
                if (addQuotes) {
                    buffer.append('"');
                }
                addSeparator = true;
            }
        }
        buffer.append("}, sliceIndex=").append(getSliceIndex());
        final API api = getAPI();
        if (!API.NONE.equals(api)) {
            buffer.append(", API=").append(api.name());
        }
        return buffer.append(']').toString();
    }
}

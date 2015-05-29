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

import java.awt.Point;
import java.awt.Rectangle;
import javax.imageio.IIOParam;
import javax.imageio.ImageWriteParam;


/**
 * Tuple of a <cite>dimension identifier</cite> and <cite>index</cite> in that dimension for a slice
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
 * This class refers always to the indices in the file, which can be either the source
 * or the destination:
 * <p>
 * <ul>
 *   <li>When used with {@link SpatialImageReadParam}, this class contains the index of the
 *       section to read from the file (the <cite>source region</cite>).</li>
 *   <li>When used with {@link SpatialImageWriteParam}, this class contains the index of the
 *       section to write in the file (the <cite>destination offset</cite>).</li>
 * </ul>
 * <p>
 * In addition to the index, {@code DimensionSlice} also specifies the dimension on which the
 * index applies. See the {@link DimensionIdentification} javadoc for more information about
 * how dimensions are identified.
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
 * @version 3.15
 *
 * @see SpatialImageReadParam
 * @see MultidimensionalImageStore
 *
 * @since 3.08
 * @module
 */
public class DimensionSlice extends DimensionIdentification {
    /**
     * The standard Java API used for selecting the slice to read or write in a particular
     * dimension. The region to read or write in a hyper-cube can be specified in up to 4
     * dimensions in the following ways:
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
     * @see DimensionSlice
     * @see MultidimensionalImageStore
     * @see IllegalImageDimensionException
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
     * The index of the region to read along the dimension that this
     * {@code DimensionSlice} object represents.
     */
    private int index;

    /**
     * Creates a new {@code DimensionSlice} instance. This constructor is not public in order
     * to ensure that the given collection contains only {@code DimensionSlice} instances, not
     * mixed with {@link DimensionIdentification}. In addition, the {@link DimensionSet#owner}
     * shall be a {@link SpatialImageReadParam} or {@link SpatialImageWriteParam} instance, as
     * required by {@link #getParameters()}.
     *
     * @param owner The collection that created this object.
     */
    DimensionSlice(final DimensionSet owner) {
        super(owner);
    }

    /**
     * Creates a new instance initialized to the same values than the given instance.
     * This copy constructor provides a way to substitute the instances created by
     * {@link SpatialImageReadParam#newDimensionSlice()} by custom instances overriding
     * some methods, as in the example below:
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
        super(original);
        this.index = original.index;
    }

    /**
     * Returns the parameter which created this {@code DimensionSlice} instance.
     */
    private IIOParam getParameters() {
        return (IIOParam) owner.owner;
    }

    /**
     * Returns the standard Java API that can be used for selecting a region along the
     * dimension represented by this object. The default value is {@link API#NONE NONE}.
     *
     * @return The standard Java API for selecting a region along the dimension.
     *
     * @see SpatialImageReadParam#getDimensionSliceForAPI(DimensionSlice.API)
     */
    private API getAPI() {
        final IIOParam param = getParameters();
        Object candidate = null;
        if (param instanceof SpatialImageReadParam) {
            candidate = ((SpatialImageReadParam) param).reader;
        } else if (param instanceof SpatialImageWriteParam) {
            candidate = ((SpatialImageWriteParam) param).writer;
        }
        if (candidate instanceof MultidimensionalImageStore) {
            return ((MultidimensionalImageStore) candidate).getAPIForDimension(getDimensionIds());
        }
        return API.NONE;
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
    @SuppressWarnings("fallthrough")
    public int getSliceIndex() {
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
                final IIOParam parameters = getParameters();
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
        final IIOParam parameters = getParameters();
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
    @SuppressWarnings("fallthrough")
    public void setSliceIndex(final int index) {
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
                final IIOParam parameters = getParameters();
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
        final IIOParam parameters = getParameters();
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
     * Returns a string representation of this object. The default implementation
     * formats on a single line the class name, the list of dimension identifiers,
     * the {@linkplain #getSliceIndex() index} and the {@linkplain #getAPI() API} (if any).
     *
     * @see SpatialImageReadParam#toString()
     */
    @Override
    public String toString() {
        final StringBuilder buffer = toStringBuilder().append("}, sliceIndex=").append(getSliceIndex());
        final API api = getAPI();
        if (api != API.NONE) {
            buffer.append(", API=").append(api.name());
    }
        return buffer.append(']').toString();
    }
}

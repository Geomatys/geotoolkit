/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.io.plugin;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Collections;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.measure.unit.NonSI;

import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Variable;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.iosp.netcdf3.N3iosp;
import ucar.nc2.constants.CF;
import ucar.nc2.constants.CDM;
import ucar.nc2.constants.AxisType;
import ucar.nc2.constants._Coordinate;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.apache.sis.measure.Units;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.geotoolkit.internal.referencing.AxisDirections;
import org.geotoolkit.internal.image.io.IIOImageHelper;
import org.geotoolkit.image.io.DimensionSlice.API;
import org.geotoolkit.image.io.ImageMetadataException;
import org.geotoolkit.metadata.iso.citation.Citations;

import static org.geotoolkit.image.io.MultidimensionalImageStore.*;


/**
 * Describes a CRS dimension to be written into a NetCDF file. The constructor computes the array
 * of coordinate values for the given axis. However the actual NetCDF variable and dimension are
 * written only by the {@link #create(NetcdfFileWriteable)} method. Before to process to the write
 * operation, the {@link #equals(Object)} method can be invoked in order to check if the dimension
 * already exists, since many NetCDF variables may share the same dimensions.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see <a href="http://cf-pcmdi.llnl.gov/documents/cf-conventions/1.6/cf-conventions.html#coordinate-system">NetCDF Coordinate Systems</a>
 *
 * @since 3.20
 * @module
 */
final class NetcdfDimension {
    /**
     * The default dimension index assigned to bands or images.
     * Used only if not explicitely specified by the user.
     */
    private static final int BAND_DIMENSION=2, IMAGE_DIMENSION=3;

    /**
     * The Image I/O API associated to this dimension.
     */
    final API api;

    /**
     * The coordinate system axis to write in the NetCDF file.
     * This field may be {@code null} for the band dimension is this dimension is
     * not explicitely described by the coordinate system.
     */
    private final CoordinateSystemAxis axis;

    /**
     * The ordinate values for this dimension, as a one-dimensional UCAR array.
     */
    private final Array ordinates;

    /**
     * The type of values in the {@linkplain #ordinates} array. This field exists only because
     * as of NetCDF 4.10, the {@link Array} API doesn't seem to provide a {@code getDataType()}
     * method.
     */
    private final DataType dataType;

    /**
     * The NetCDF dimension for the axis.
     * This array is created by {@link #create(NetcdfFileWriteable)}.
     */
    private Dimension dimension;

    /**
     * The NetCDF variable for the {@linkplain #dimension}. This variable is created
     * by {@link #create(NetcdfFileWriteable)} but its values are left uninitialized.
     */
    private Variable variable;

    /**
     * Creates a new {@code NetcdfDimension} instance for a single coordinate system axis.
     * The actual writing process will happen when the {@link #create(NetcdfFileWriteable)}
     * method will be invoked.
     *
     * @param image       A description about the bounds of the NetCDF image to write.
     * @param dimension   The dimension for which to create a sequence of ordinate values.
     *                    This method assumes that the same dimension is used for both source
     *                    and target ordinate values (i.e. there is no axis swapping).
     * @param flip        {@code true} if the axis direction needs to be flipped. This is the
     *                    case of the <var>y</var> axis. This flag must set to a value consistent
     *                    with the behavior of the code writing the actual pixel values.
     * @throws ImageMetadataException If an error occurred while computing the grid geometry.
     *
     * @todo Define a 'sourceToTargetDimension' method somewhere based on the value of the
     *       derivative at the center position.
     */
    NetcdfDimension(final IIOImageHelper image, final int dimension, final boolean flip)
            throws ImageMetadataException
    {
        final CoordinateSystem cs = image.getCoordinateSystem();
        axis = (cs.getDimension() > dimension) ? cs.getAxis(dimension) : null;
        final boolean canUseDiscreteAxis = image.isDefaultParameters
                && (axis instanceof DiscreteCoordinateSystemAxis<?>);
        /*
         * TODO: We use DimensionSlice.API as a matter of principle (for keeping room for future
         * improvement), but the mapping is hard-coded for now. Futures versions may use some of
         * the following methods:
         *
         *   api = DimensionSet.getAPI(dimension, axis);
         *   api = MultidimensionalImageStore.getAPIForDimension(dimension, axis);
         */
        switch (dimension) {
            case X_DIMENSION:     api=API.COLUMNS; break;
            case Y_DIMENSION:     api=API.ROWS;    break;
            case BAND_DIMENSION:  api=API.BANDS;   break;
            case IMAGE_DIMENSION: api=API.IMAGES;  break;
            default:              api=API.NONE;    break;
        }
        int[] sourceIndices = null; // Source ordinate values, or null for [index:subsampling:...]
        int   subsampling   = 1;    // The grid ordinates increment. Must be equals or greater than 1.
        int   length        = 1;    // Number of ordinate values to write in the NetCDF file.
        int   index         = 0;    // Grid ordinate value, from lower inclusive to lower + length × subsampling exclusive.
        switch (api) {
            case COLUMNS: index=image.sourceRegion.x; subsampling=image.sourceXSubsampling; length=(image.sourceRegion.width +subsampling-1)/subsampling; break;
            case ROWS:    index=image.sourceRegion.y; subsampling=image.sourceYSubsampling; length=(image.sourceRegion.height+subsampling-1)/subsampling; break;
            case BANDS: {
                length = image.getNumSourceBands();
                sourceIndices = image.sourceBands;
                break;
            }
            case IMAGES: {
                // We don't know in advance how many images are going to be written.
                // So we rely on the metadata as the best we can do for now.
                final GridEnvelope domain = image.getGridDomain();
                if (domain != null) {
                    index  = domain.getLow (dimension);
                    length = domain.getSpan(dimension);
                } else {
                    length = canUseDiscreteAxis ? ((DiscreteCoordinateSystemAxis<?>) axis).length() : 1;
                }
                break;
            }
        }
        /*
         * If the axis declares directly its set of valid ordinate values, use those values.
         * This is the case if the coordinate system has been created by NetcdfImageReader.
         */
        if (canUseDiscreteAxis) {
            final DiscreteCoordinateSystemAxis<?> ds = (DiscreteCoordinateSystemAxis<?>) axis;
            final Class<?> type = ds.getElementType();
            final boolean isNumeric = Number.class.isAssignableFrom(type);
            if (isNumeric || Date.class.isAssignableFrom(type)) {
                dataType  = isNumeric ? DataType.getType(type) : DataType.DOUBLE;
                ordinates = Array.factory(dataType, new int[] {length});
                DefaultTemporalCRS converter = null;
                if (!isNumeric) {
                    final CoordinateReferenceSystem subCRS = CRS.getSubCRS(
                            image.getCoordinateReferenceSystem(), dimension, dimension+1);
                    if (subCRS instanceof TemporalCRS) {
                        converter = DefaultTemporalCRS.castOrCopy((TemporalCRS) subCRS);
                    } else {
                        throw new ImageMetadataException(Errors.format(Errors.Keys.INCOMPATIBLE_COORDINATE_SYSTEM_TYPE));
                    }
                }
                for (int i=0; i<length; i++) {
                    if (sourceIndices != null) {
                        index = sourceIndices[i];
                    }
                    final Comparable<?> ordinate = ds.getOrdinateAt(index);
                    ordinates.setDouble(flip ? (length-1)-i : i, isNumeric
                            ? ((Number) ordinate).doubleValue()
                            : converter.toValue((Date) ordinate));
                    index += subsampling;
                }
                return;
            }
        }
        /*
         * If we reach this point, we have not been able to compute the grid cell coordinates
         * from the axis. Try to compute them from the grid to CRS transform instead.
         */
        final MathTransform gridToCRS = image.getGridToCRS();
        if (gridToCRS == null || gridToCRS.getSourceDimensions() <= dimension) {
            if (sourceIndices == null) {
                sourceIndices = new int[length];
                for (int i=0; i<length; i++) {
                    sourceIndices[i] = index;
                    index += subsampling;
                }
            }
            if (flip) {
                if (sourceIndices == image.sourceBands) {
                    sourceIndices = sourceIndices.clone();
                }
                ArraysExt.reverse(sourceIndices);
            }
            ordinates = Array.factory(dataType = DataType.INT, new int[] {length}, sourceIndices);
        } else {
            ordinates = Array.factory(dataType = DataType.FLOAT, new int[] {length});
            final double[] center = image.getSourceRegionCenter();
            final double[] source = new double[(gridToCRS != null) ? gridToCRS.getSourceDimensions() : 2];
            final double[] target = new double[(gridToCRS != null) ? gridToCRS.getTargetDimensions() : 2];
            System.arraycopy(center, 0, source, 0, Math.min(source.length, center.length));
            try {
                for (int i=0; i<length; i++) {
                    if (sourceIndices != null) {
                        index = sourceIndices[i];
                    }
                    source[dimension] = index;
                    gridToCRS.transform(source, 0, target, 0, 1);
                    ordinates.setDouble(flip ? (length-1)-i : i, target[dimension]);
                    index += subsampling;
                }
            } catch (TransformException e) {
                throw new ImageMetadataException(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * Adds this dimension in the given NetCDF file for the axis given at construction time.
     * This constructor creates a new {@linkplain #variable} and {@linkplain #dimension},
     * which are referenced in this class fields.
     * <p>
     * The NetCDF file must be in "define mode" when this method is invoked. This method will
     * create the dimension and the variable, but will not physically write them to the disk.
     * The actual writing will happen in the {@link #write(NetcdfFileWriteable)} method.
     *
     * @param  file The UCAR NetCDF object where to write the new dimension and variable.
     * @param  locale The locale for dimension and variable names.
     */
    void create(final NetcdfFileWriteable file, final Locale locale) {
        if (axis == null) {
            // Currently, this case occurs only when writing bands.
            // We will need to revisit the name if more cases occur.
            create(file, "band");
            return;
        }
        final String        longName  = IdentifiedObjects.getName(axis, null);
        final AxisDirection direction = axis.getDirection();
        final AxisDirection absdir    = AxisDirections.absolute(direction);
        final Unit<?>       unit      = axis.getUnit();
        final AxisType      type;
        String name, positive = null;
        if (AxisDirection.EAST.equals(absdir)) {
            if (Units.isLinear(unit)) {
                type = AxisType.GeoX;
                name = "x";
            } else {
                type = AxisType.Lon;
                name = "lon";
            }
        } else if (AxisDirection.NORTH.equals(absdir)) {
            if (Units.isLinear(unit)) {
                type = AxisType.GeoY;
                name = "y";
            } else {
                type = AxisType.Lat;
                name = "lat";
            }
        } else if (AxisDirection.UP.equals(absdir)) {
            type = Units.isPressure(unit) ? AxisType.Pressure : AxisType.Height;
            positive = (absdir == direction) ? CF.POSITIVE_UP : CF.POSITIVE_DOWN;
            name = "z";
        } else if (AxisDirection.FUTURE.equals(absdir)) {
            type = AxisType.Time;
            name = "time";
        } else if (AxisDirection.GEOCENTRIC_X.equals(absdir)) {
            type = AxisType.GeoX;
            name = "x";
        } else if (AxisDirection.GEOCENTRIC_Y.equals(absdir)) {
            type = AxisType.GeoY;
            name = "y";
        } else if (AxisDirection.GEOCENTRIC_Z.equals(absdir)) {
            type = AxisType.GeoZ;
            name = "z";
        } else {
            type = null;
            name = N3iosp.createValidNetcdf3ObjectName(longName);
            name = (locale != null) ? name.toLowerCase(locale) : name.toLowerCase();
        }
        /*
         * 'name' has been initialized to a reasonable name for the dimension and variable to
         * create for the given axis. However if the axis name ('longName') is a valid NetCDF
         * name, then it will be used on the assumption that this name come from a previous
         * reading of a NetCDF file.
         */
        final String ncName = IdentifiedObjects.getName(axis, Citations.NETCDF);
        if (ncName != null && N3iosp.isValidNetcdf3ObjectName(ncName)) {
            name = ncName;
        }
        /*
         * Create the variable and attach the relevant attribute value.
         * Note that the values in the variable are left uninitialized.
         */
        create(file, name);
        if (!name.equals(longName)) {
            addAttribute(CDM.LONG_NAME, longName);
        }
        if (unit != null && !unit.equals(Unit.ONE)) {
            addAttribute(CDM.UNITS, getUnitSymbol(unit, direction));
        }
        addAttribute(CF.POSITIVE, positive);
        if (type != null) {
            addAttribute(CF.AXIS, type.getCFAxisName());
            addAttribute(_Coordinate.AxisType, type.name());
        }
    }

    /**
     * Creates initially empty {@link #dimension} and {@link #variable} instances.
     */
    private void create(final NetcdfFileWriteable file, final String name) {
        dimension = file.addDimension(name, (int) ordinates.getSize());
        variable  = file.addVariable(name, dataType, Collections.singletonList(dimension));
    }

    /**
     * Writes this dimension in the given NetCDF file. This method can be invoked after the
     * {@link #create(NetcdfFileWriteable)} method, when the NetCDF file is no longer in
     * "define mode".
     *
     * @param  file The UCAR NetCDF object where to write the new dimension and variable.
     * @throws IOException if an error occurred while writing the NetCDF variable.
     */
    void write(final NetcdfFileWriteable file) throws IOException {
        try {
            file.write(variable.getFullNameEscaped(), ordinates);
        } catch (InvalidRangeException e) {
            throw new IIOException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Adds the given attribute value to the {@linkplain #variable},
     * provided that the value is neither null or empty.
     */
    private void addAttribute(final String name, String value) {
        if (value != null && !((value = value.trim()).isEmpty())) {
            variable.addAttribute(new Attribute(name, value));
        }
    }

    /**
     * Returns the symbol for the given units for the axis direction.
     */
    private String getUnitSymbol(final Unit<?> unit, final AxisDirection direction) {
        if (NonSI.DEGREE_ANGLE.equals(unit)) {
            if (AxisDirection.EAST .equals(direction)) return "degrees_east";
            if (AxisDirection.NORTH.equals(direction)) return "degrees_north";
            if (AxisDirection.WEST .equals(direction)) return "degrees_west";
            if (AxisDirection.SOUTH.equals(direction)) return "degrees_south";
            return "degrees";
        }
        if (Units.isTemporal(unit)) {
            final StringBuilder buffer = new StringBuilder();
            if (SI.SECOND.equals(unit)) {
                buffer.append("seconds");
            } else if (NonSI.MINUTE.equals(unit)) {
                buffer.append("minutes");
            } else if (NonSI.HOUR.equals(unit)) {
                buffer.append("hours");
            } else if (NonSI.DAY.equals(unit)) {
                buffer.append("days");
            } else {
                buffer.append(unit);
            }
            // TODO: needs to append " since ", then the epoch.
            return buffer.toString();
        }
        return String.valueOf(unit);
    }

    /**
     * Returns the NetCDF dimension. This method returns a non-null value if and only if the
     * {@link #create(NetcdfFileWriteable)} method has been invoked before.
     */
    Dimension getDimension() {
        return dimension;
    }

    /**
     * Returns a hash code value for this dimension. This method is defined
     * for consistency with {@link #equals(Object)}.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(axis) ^ Utilities.deepHashCode(ordinates.getStorage());
    }

    /**
     * Compares the dimension to be written in the NetCDF file with the given object for equality.
     * This method is designed for comparing only the attributes having an influence on the NetCDF
     * file content, especially {@link #axis} and {@link #ordinates}. It does not compare the
     * {@link #api} attribute, because it has an influence only on the source of the data.
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof NetcdfDimension) {
            final NetcdfDimension that = (NetcdfDimension) other;
            return Utilities.deepEquals(axis, that.axis, ComparisonMode.IGNORE_METADATA) &&
                   Objects.deepEquals(ordinates.getStorage(), that.ordinates.getStorage());
        }
        return false;
    }

    /**
     * Returns a string representation for debugging purpose.
     */
    @Override
    public String toString() {
        return api + " → " + IdentifiedObjects.getName(axis, null) + Utilities.deepToString(ordinates.getStorage());
    }
}

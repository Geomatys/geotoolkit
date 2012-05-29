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

import java.util.List;
import java.util.ArrayList;
import javax.imageio.IIOImage;
import javax.imageio.IIOParam;
import javax.imageio.ImageWriter;
import java.awt.image.DataBuffer;
import javax.measure.unit.Unit;

import ucar.ma2.DataType;
import ucar.nc2.Variable;
import ucar.nc2.Dimension;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.constants.CDM;
import ucar.nc2.iosp.netcdf3.N3iosp;

import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.content.RangeDimension;

import org.geotoolkit.util.collection.XCollections;
import org.geotoolkit.image.io.ImageMetadataException;
import org.geotoolkit.image.io.metadata.SampleDimension;
import org.geotoolkit.internal.image.io.IIOImageHelper;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.internal.image.io.NetcdfVariable.*;


/**
 * Holds the information about an image to be written in a NetCDF file.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
final class NetcdfImage extends IIOImageHelper {
    /**
     * The dimensions associated with the NetCDF image.
     * The array length is the number of coordinate system axis.
     */
    private final NetcdfDimension[] dimensions;

    /**
     * The variables where to write the pixel values. The length of this list must be equals
     * to either 1, or to the number of source bands in the image to write. More specifically,
     * the {@linkplain #dimensions} and {@linkplain #variables} must be related by exactly one,
     * and only one, of the following rules:
     *
     * <ul>
     *   <li><p>{@code variables} contains only one element. The {@linkplain Variable#getRank() rank}
     *   of that element is the {@code dimensions} array length. For each <var>i</var> in the range
     *   [0…rank-1], we have {@code variable.getShape()[i] == dimensions[i].getLength()}. The bands
     *   are the third dimension in the variable.</p></li>
     *
     *   <li><p>{@code variables} contains one or more elements. Every elements have the same shape,
     *   and the {@linkplain Variable#getRank() rank} is {@code dimensions.length - 1}. The length
     *   of this array is the number of bands.</p></li>
     * </ul>
     */
    private final List<Variable> variables;

    /**
     * Creates a new object for the given image to write.
     *
     * @param writer The writer which is preparing the image to write, or {@code null} if unknown.
     * @param image  The image or raster to be read or written.
     * @param param  The parameters that control the writing process, or {@code null} if none.
     * @param allDimensions All dimensions created by previous invocations of this constructor.
     *        The constructor will add new dimensions in this array if needed.
     */
    NetcdfImage(final ImageWriter writer, final IIOImage image, final IIOParam parameters,
            final List<NetcdfDimension> allDimensions) throws ImageMetadataException
    {
        super(writer, image, parameters);
        dimensions = new NetcdfDimension[getCoordinateSystem().getDimension()];
        for (int i=0; i<dimensions.length; i++) {
            NetcdfDimension dimension = new NetcdfDimension(this, i);
            final int existing = allDimensions.indexOf(dimension);
            if (existing >= 0) {
                dimension = allDimensions.get(existing);
            } else {
                allDimensions.add(dimension);
            }
            dimensions[i] = dimension;
        }
        variables = new ArrayList<>(4);
    }

    /**
     * Adds the variables to the given NetCDF file.
     *
     * @param  file The NetCDF file where to add the variables.
     * @throws ImageMetadataException If an error occurred while creating the variables.
     */
    final void addVariables(final NetcdfFileWriteable file) throws ImageMetadataException {
        final ImageDescription description = metadata.getInstanceForType(ImageDescription.class);
        if (description != null) {
            final List<? extends RangeDimension> ranges = XCollections.asList(description.getDimensions());
            if (!ranges.isEmpty()) {
                // Either we store all bands in a single variable, or either we use one variable for each bands.
                final int[] srcDimensions = (ranges.size() == 1) ? new int[1] : getSourceBands();
                for (final int band : srcDimensions) {
                    final RangeDimension range = (band < ranges.size()) ? ranges.get(band) : null;
                    final String name = (range != null) ? toString(range.getDescriptor()) : null;
                    final Variable var = createVariable(file, (name != null) ? name : "band_" + (band+1));
                    if (range instanceof SampleDimension) {
                        final SampleDimension sd = (SampleDimension) range;
                        final Double max    = sd.getMaxValue();
                        final Double min    = sd.getMinValue();
                        final Double offset = sd.getOffset();
                        final Double scale  = sd.getScaleFactor();
                        final Unit<?> unit  = sd.getUnits();
                        if (min    != null) var.addAttribute(new Attribute(VALID_MIN, min));
                        if (max    != null) var.addAttribute(new Attribute(VALID_MAX, max));
                        if (offset != null) var.addAttribute(new Attribute(CDM.ADD_OFFSET, offset));
                        if (scale  != null) var.addAttribute(new Attribute(CDM.SCALE_FACTOR, scale));
                        if (unit   != null) var.addAttribute(new Attribute(CDM.UNITS, String.valueOf(unit)));
                    }
                    variables.add(var);
                }
                return;
            }
        }
        /*
         * If we reach this point, we found no metadata for the bands to write.
         * Add the variables for each bands.
         */
        for (final int band : getSourceBands()) {
            variables.add(createVariable(file, "band_" + (band+1)));
        }
    }

    /**
     * Adds a NetCDF variable of the given name and type to the given NetCDF file.
     * The NetCDF file must be in "define mode". The returned variable is initially
     * empty.
     *
     * @param  file The NetCDF file where to add the variable.
     * @param  name The name of the new NetCDF variable to create.
     * @throws ImageMetadataException If no variable can be created for the given type.
     *
     * @see org.geotoolkit.internal.image.io.NetcdfVariable#getRawDataType
     */
    @SuppressWarnings("fallthrough")
    private Variable createVariable(final NetcdfFileWriteable file, String name)
            throws ImageMetadataException
    {
        final DataType type;
        boolean unsigned = false;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:   type = DataType.BYTE; unsigned=true; break;
            case DataBuffer.TYPE_USHORT: unsigned = true; // Fallthrough
            case DataBuffer.TYPE_SHORT:  type = DataType.SHORT;  break;
            case DataBuffer.TYPE_INT:    type = DataType.INT;    break;
            case DataBuffer.TYPE_FLOAT:  type = DataType.FLOAT;  break;
            case DataBuffer.TYPE_DOUBLE: type = DataType.DOUBLE; break;
            default: throw new ImageMetadataException(Errors.format(Errors.Keys.UNSUPPORTED_DATA_TYPE_$1, dataType));
        }
        final Dimension[] ncDimensions = new Dimension[dimensions.length];
        for (int i=0; i<ncDimensions.length; i++) {
            ncDimensions[ncDimensions.length - (i+1)] = dimensions[i].getDimension();
        }
        String longName = null;
        if (!N3iosp.isValidNetcdf3ObjectName(name)) {
            longName = name;
            name = N3iosp.createValidNetcdf3ObjectName(name);
        }
        final Variable variable = file.addVariable(name, type, ncDimensions);
        if (unsigned) {
            variable.addAttribute(new Attribute(CDM.UNSIGNED, "true"));
        }
        if (longName != null) {
            variable.addAttribute(new Attribute(CDM.LONG_NAME, longName));
        }
        return variable;
    }
}

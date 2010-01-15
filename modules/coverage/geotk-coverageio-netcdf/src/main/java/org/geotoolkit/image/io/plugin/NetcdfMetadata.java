/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.image.io.plugin;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.imageio.ImageReader;

import ucar.nc2.VariableIF;
import ucar.nc2.dataset.CoordinateSystem;
import ucar.nc2.dataset.CoordSysBuilderIF;
import ucar.nc2.dataset.EnhanceScaleMissing;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.Enhancements;

import org.opengis.metadata.content.TransferFunctionType;

import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.referencing.adapters.NetcdfAxis;
import org.geotoolkit.referencing.adapters.NetcdfCRS;
import org.geotoolkit.resources.Errors;


/**
 * Metadata from NetCDF file. This implementation assumes that the NetCDF file follows the
 * <A HREF="http://www.cfconventions.org/">CF Metadata conventions</A>.
 *
 * {@section Limitation}
 * Current implementation retains only the first {@linkplain CoordinateSystem coordinate system}
 * found in the NetCDF file or for a given variable. The {@link org.geotoolkit.coverage.io} package
 * would not know what to do with the extra coordinate systems anyway.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08 (derived from 2.4)
 * @module
 */
final class NetcdfMetadata extends SpatialMetadata {
    /**
     * Forces usage of UCAR libraries in some places where we use our own code instead.
     * This may result in rounding errors and absence of information regarding fill values,
     * but is useful for checking if we are doing the right thing compared to the UCAR way.
     */
    private static final boolean USE_UCAR_LIB = false;

    /**
     * Creates image metadata from the specified file. Note that
     * {@link CoordSysBuilderIF#buildCoordinateSystems(NetcdfDataset)}
     * should have been invoked (if needed) before this constructor.
     *
     * @param reader The reader for which to assign the metadata.
     * @param file The file for which to read metadata.
     */
    public NetcdfMetadata(final ImageReader reader, final NetcdfDataset file) {
        super(SpatialMetadataFormat.STREAM, reader, null);
    }

    /**
     * Creates image metadata from the specified variable. Note that
     * {@link CoordSysBuilderIF#buildCoordinateSystems(NetcdfDataset)}
     * should have been invoked (if needed) before this constructor.
     *
     * @param reader The reader for which to assign the metadata.
     * @param variable The variable for which to read metadata.
     */
    public NetcdfMetadata(final ImageReader reader, final VariableIF variable) {
        super(SpatialMetadataFormat.IMAGE, reader, null);
        if (variable instanceof Enhancements) {
            final List<CoordinateSystem> systems = ((Enhancements) variable).getCoordinateSystems();
            if (systems != null && !systems.isEmpty()) {
                setCoordinateSystem(systems.get(0));
            }
        }
        addSampleDimension(variable);
    }

    /**
     * Sets the Coordinate Reference System to a value inferred from the specified
     * NetCDF object. This method wraps the given NetCDF coordinate system in to a
     * GeoAPI {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem
     * Coordinate Reference System} implementation.
     *
     * @param cs The coordinate system to define in metadata.
     */
    public void setCoordinateSystem(final CoordinateSystem cs) {
        final NetcdfCRS crs = NetcdfCRS.create(cs);
        final int dimension = crs.getDimension();
        for (int i=0; i<dimension; i++) {
            final NetcdfAxis axis = crs.getAxis(i);
            final String units = axis.delegate().getUnitsString();
            final int offset = units.lastIndexOf('_');
            if (offset >= 0) {
                final String direction = units.substring(offset + 1).trim();
                final String opposite = axis.getDirection().opposite().name();
                if (direction.equalsIgnoreCase(opposite)) {
                    warning("setCoordinateSystem", Errors.Keys.INCONSISTENT_AXIS_ORIENTATION_$2,
                            new String[] {axis.getCode(), direction});
                }
            }
        }
    }

    /**
     * Adds sample dimension information for the specified variables.
     *
     * @param variables The variables to add as sample dimensions.
     */
    public void addSampleDimension(final VariableIF... variables) {
        final DimensionAccessor accessor = new DimensionAccessor(this);
        for (final VariableIF variable : variables) {
            final NetcdfVariable m;
            if (USE_UCAR_LIB && variable instanceof EnhanceScaleMissing) {
                m = new NetcdfVariable((EnhanceScaleMissing) variable);
            } else {
                m = new NetcdfVariable(variable);
            }
            accessor.selectChild(accessor.appendChild());
            accessor.setValueRange(m.minimum, m.maximum);
            accessor.setFillSampleValues(m.fillValues);
            accessor.setTransfertFunction(m.scale, m.offset, TransferFunctionType.LINEAR);
        }
    }

    /**
     * Convenience method for logging a warning.
     */
    private void warning(final String method, final int key, final Object value) {
        LogRecord record = Errors.getResources(getLocale()).getLogRecord(Level.WARNING, key, value);
        record.setSourceClassName(NetcdfMetadata.class.getName());
        record.setSourceMethodName(method);
        warningOccurred(record);
    }
}

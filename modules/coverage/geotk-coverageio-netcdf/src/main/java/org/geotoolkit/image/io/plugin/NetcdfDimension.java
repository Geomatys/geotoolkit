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

import java.util.Collections;

import ucar.ma2.DataType;
import ucar.nc2.Variable;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.iosp.netcdf3.N3iosp;
import ucar.nc2.constants.CF;
import ucar.nc2.constants.CDM;
import ucar.nc2.constants.AxisType;
import ucar.nc2.constants._Coordinate;
import javax.measure.unit.Unit;
import javax.measure.unit.NonSI;

import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;

import org.geotoolkit.measure.Units;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.internal.referencing.AxisDirections;


/**
 * Writes a CRS dimension into a NetCDF file.
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
     * The NetCDF dimension for the axis.
     */
    final Dimension dimension;

    /**
     * The NetCDF variable for the {@linkplain #dimension}. This variable is
     * created at construction time but its values are left uninitialized.
     */
    final Variable variable;

    /**
     * Adds a new dimension in the given NetCDF file for the given coordinate system axis.
     * This constructor creates a new {@linkplain #variable} and {@linkplain #dimension},
     * which are referenced in this class fields.
     *
     * @param  file The UCAR NetCDF object where to write the new dimension and variable.
     * @param  axis The axis to write in the NetCDF file.
     * @param  size Number of values along the given axis. This will determine the dimension shape.
     */
    NetcdfDimension(final NetcdfFileWriteable file, final CoordinateSystemAxis axis, final int size) {
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
        } else if (longName != null && N3iosp.isValidNetcdf3ObjectName(longName)) {
            name = longName;
        }
        /*
         * Create the variable and attach the relevant attribute value.
         * Note that the values in the variable are left uninitialized.
         */
        dimension = file.addDimension(name, size);
        variable  = file.addVariable(name, DataType.DOUBLE, Collections.singletonList(dimension));
        if (!name.equals(longName)) {
            addAttribute(CDM.LONG_NAME, longName);
        }
        if (unit != null && !unit.equals(Unit.ONE)) {
            addAttribute(CDM.UNITS, NonSI.DEGREE_ANGLE.equals(unit) ? getAngularUnit(direction) : String.valueOf(unit));
        }
        addAttribute(CF.POSITIVE, positive);
        if (type != null) {
            addAttribute(CF.AXIS, type.getCFAxisName());
            addAttribute(_Coordinate.AxisType, type.name());
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
     * Returns the angular units for the given axis direction.
     */
    private static String getAngularUnit(final AxisDirection direction) {
        if (AxisDirection.EAST .equals(direction)) return "degrees_east";
        if (AxisDirection.NORTH.equals(direction)) return "degrees_north";
        if (AxisDirection.WEST .equals(direction)) return "degrees_west";
        if (AxisDirection.SOUTH.equals(direction)) return "degrees_south";
        return "degrees";
    }
}

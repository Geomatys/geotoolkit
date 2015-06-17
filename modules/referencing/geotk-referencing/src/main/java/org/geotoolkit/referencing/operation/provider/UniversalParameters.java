/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.referencing.operation.provider;

import java.util.Map;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;
import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotoolkit.internal.referencing.DeprecatedName;
import org.opengis.util.GenericName;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.metadata.Identifier;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.resources.Errors;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;

import static org.opengis.referencing.operation.SingleOperation.*;
import static org.geotoolkit.metadata.Citations.*;
import static org.apache.sis.util.collection.Containers.hashMapCapacity;


/**
 * Collection of {@linkplain MapProjection map projection} parameters containing every names known
 * to Geotk. This class can be used for lenient parsing of projection parameters, when they are not
 * used in a way strictly compliant to their standard.
 * <p>
 * The same parameter may have different names according different authorities
 * ({@linkplain Citations#EPSG EPSG}, {@linkplain Citations#OGC OGC}, {@linkplain Citations#ESRI ESRI},
 * <cite>etc.</cite>). But in addition, the same authority may use different names for a parameter
 * which, from a computational point of view, serves the same purpose in every projections.
 * For example the EPSG database uses all the following names for the {@link #CENTRAL_MERIDIAN}
 * parameter, even if the value is always used in the same way:
 * <p>
 * <ul>
 *   <li>Longitude of origin</li>
 *   <li>Longitude of false origin</li>
 *   <li>Longitude of natural origin</li>
 *   <li>Spherical longitude of origin</li>
 *   <li>Longitude of projection centre</li>
 * </ul>
 * <p>
 * In every {@link MapProjection} subclass, only the official parameter names are declared.
 * For example the {@link Mercator1SP} class uses "<cite>Longitude of natural origin</cite>"
 * for the above-cited {@code CENTRAL_MERIDIAN} parameter, while {@link ObliqueMercator} uses
 * "<cite>Longitude of projection centre</cite>". However not every softwares use the right
 * parameter name with the right projection. This {@code UniversalParameters} class can be
 * used for processing parameters which may have the wrong name.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 3.00)
 * @module
 */
public final class UniversalParameters extends DefaultParameterDescriptor<Double> {
    /**
     * For cross-version compatibility. Provided as a safety, however
     * we do not expect instance of this class to be serialized since
     * they should not appear in public API.
     */
    private static final long serialVersionUID = -4608976443553166518L;

    /**
     * All known names for the
     * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#semiMajor
     * semi major} parameter. This parameter is mandatory and has no default value.
     * The range of valid values is (0 &hellip; &infin;).
     * <p>
     * Some names for this parameter are {@code "Semi-major axis"}, {@code "semi_major"},
     * {@code "SemiMajor"} and {@code "a"}.
     *
     * @see org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#semiMajor
     */
    public static final UniversalParameters SEMI_MAJOR = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "semi_major"),
            new NamedIdentifier(EPSG,    "Semi-major axis"), // EPSG does not specifically define this parameter
            new NamedIdentifier(ESRI,    "Semi_Major"),
            new NamedIdentifier(NETCDF,  "semi_major_axis"),
            new NamedIdentifier(GEOTIFF, "SemiMajor"),
            new NamedIdentifier(PROJ4,   "a")
        }, Double.NaN, 0, Double.POSITIVE_INFINITY, SI.METRE, true);

    /**
     * All known names for the
     * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#semiMinor
     * semi minor} parameter. This parameter is mandatory and has no default value.
     * The range of valid values is (0 &hellip; &infin;).
     * <p>
     * Some names for this parameter are {@code "Semi-minor axis"}, {@code "semi_minor"},
     * {@code "SemiMinor"} and {@code "b"}.
     *
     * @see org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#semiMinor
     */
    public static final UniversalParameters SEMI_MINOR = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "semi_minor"),
            new NamedIdentifier(EPSG,    "Semi-minor axis"), // EPSG does not specifically define this parameter
            new NamedIdentifier(ESRI,    "Semi_Minor"),
            new NamedIdentifier(NETCDF,  "semi_minor_axis"),
            new NamedIdentifier(GEOTIFF, "SemiMinor"),
            new NamedIdentifier(PROJ4,   "b")
        }, Double.NaN, 0, Double.POSITIVE_INFINITY, SI.METRE, true);

    /**
     * All known names for the Earth radius parameter.
     * This is used in some NetCDF files instead of {@link #SEMI_MAJOR} and {@link #SEMI_MINOR}.
     * This is not a standard parameter.
     */
    static final ParameterDescriptor<Double> EARTH_RADIUS = createDescriptor(new NamedIdentifier[] {
            new NamedIdentifier(NETCDF, MapProjectionDescriptor.EARTH_RADIUS)
        }, Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE, false);

    /**
     * All known names for the inverse flattening parameter.
     * This is used in some NetCDF files instead of {@link #SEMI_MINOR}.
     * This is not a standard parameter.
     */
    static final ParameterDescriptor<Double> INVERSE_FLATTENING = createDescriptor(new NamedIdentifier[] {
            new NamedIdentifier(NETCDF, MapProjectionDescriptor.INVERSE_FLATTENING)
        }, Double.NaN, 0.0, Double.POSITIVE_INFINITY, Unit.ONE, false);

    /**
     * All known names for the
     * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian
     * central meridian} parameter.
     * This parameter is mandatory - meaning that it appears in {@linkplain ParameterValueGroup
     * parameter value group} even if the user didn't set it explicitly - and its default value
     * is 0&deg;. The range of valid values is [-180 &hellip; 180]&deg;.
     * <p>
     * Some names for this parameter are {@code "Longitude of origin"},
     * {@code "Longitude of false origin"}, {@code "Longitude of natural origin"},
     * {@code "Spherical longitude of origin"}, {@code "Longitude of projection centre"},
     * {@code "Longitude_Of_Center"}, {@code "longitude_of_projection_origin"},
     * {@code "central_meridian"}, {@code "longitude_of_central_meridian"},
     * {@code "NatOriginLong"}, {@code "FalseOriginLong"}, {@code "ProjCenterLong"},
     * {@code "CenterLong"} and {@code "lon_0"}.
     *
     * @see org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian
     */
    public static final UniversalParameters CENTRAL_MERIDIAN = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "central_meridian"),
            new NamedIdentifier(OGC,     "longitude_of_center"),
            new NamedIdentifier(EPSG,    "Longitude of origin"),
            new NamedIdentifier(EPSG,    "Longitude of false origin"),
            new NamedIdentifier(EPSG,    "Longitude of natural origin"),
            new NamedIdentifier(EPSG,    "Spherical longitude of origin"),
            new NamedIdentifier(EPSG,    "Longitude of projection centre"),
            new NamedIdentifier(ESRI,    "Central_Meridian"),
            new NamedIdentifier(ESRI,    "Longitude_Of_Center"),
            new NamedIdentifier(ESRI,    "Longitude_Of_Origin"),                // LGPL
            new NamedIdentifier(NETCDF,  "longitude_of_projection_origin"),
            new NamedIdentifier(NETCDF,  "longitude_of_central_meridian"),
            new NamedIdentifier(GEOTIFF, "NatOriginLong"),
            new NamedIdentifier(GEOTIFF, "FalseOriginLong"),
            new NamedIdentifier(GEOTIFF, "ProjCenterLong"),
            new NamedIdentifier(GEOTIFF, "CenterLong"),
            new NamedIdentifier(GEOTIFF, "StraightVertPoleLong"),
            new NamedIdentifier(PROJ4,   "lon_0")
        }, 0, -180, 180, NonSI.DEGREE_ANGLE, true);

    /**
     * All known names for the
     * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#latitudeOfOrigin
     * latitude of origin} parameter.
     * This parameter is mandatory - meaning that it appears in {@linkplain ParameterValueGroup
     * parameter value group} even if the user didn't set it explicitly - and its default value
     * is 0&deg;. The range of valid values is [-90 &hellip; 90]&deg;.
     * <p>
     * Some names for this parameter are {@code "Latitude of false origin"},
     * {@code "Latitude of natural origin"}, {@code "Spherical latitude of origin"},
     * {@code "Latitude of projection centre"}, {@code "latitude_of_center"},
     * {@code "latitude_of_projection_origin"}, {@code "latitude_of_origin"},
     * {@code "NatOriginLat"}, {@code "FalseOriginLat"}, {@code "ProjCenterLat"}, {@code "CenterLat"}
     * and @code "lat_0"}.
     *
     * @see org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#latitudeOfOrigin
     */
    public static final UniversalParameters LATITUDE_OF_ORIGIN;

    /**
     * All known names for the standard parallels parameter, as an array of 1 or 2 elements.
     * This is used in some NetCDF files instead of {@link #STANDARD_PARALLEL_1} and
     * {@link #STANDARD_PARALLEL_2}. This is not a standard parameter.
     */
    static final ParameterDescriptor<double[]> STANDARD_PARALLEL;

    /**
     * All known names for the standard parallel 1 parameter.
     * This parameter is optional. The range of valid values is [-90 &hellip; 90]&deg;.
     *
     * <blockquote><b>EPSG description:</b> For a conic projection with two standard parallels,
     * this is the latitude of intersection of the cone with the ellipsoid that is nearest the pole.
     * Scale is true along this parallel.</blockquote>
     * <p>
     * Some names for this parameter are {@code "Latitude of standard parallel"},
     * {@code "Latitude of pseudo standard parallel"}, {@code "standard_parallel_1"},
     * {@code "pseudo_standard_parallel_1"}, {@code "StdParallel1"} and {@code "lat_1"}.
     */
    public static final UniversalParameters STANDARD_PARALLEL_1;

    /**
     * Creates the above constants together in order to share instances of identifiers
     * that appear in both cases. Those common identifiers are misplaced for historical
     * reasons (in the EPSG case, one of them is usually deprecated). We still need to
     * declare them in both places for compatibility with historical data.
     */
    static {
        final NamedIdentifier esri = new NamedIdentifier(ESRI, "Standard_Parallel_1");
        final NamedIdentifier epsg = new NamedIdentifier(EPSG, "Latitude of 1st standard parallel");
        final NamedIdentifier nc   = new NamedIdentifier(NETCDF, MapProjectionDescriptor.STANDARD_PARALLEL);

        LATITUDE_OF_ORIGIN = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "latitude_of_origin"),
            new NamedIdentifier(OGC,     "latitude_of_center"),
            new NamedIdentifier(EPSG,    "Latitude of false origin"),
            new NamedIdentifier(EPSG,    "Latitude of natural origin"),
            new NamedIdentifier(EPSG,    "Spherical latitude of origin"),
            new NamedIdentifier(EPSG,    "Latitude of projection centre"), epsg,
            new NamedIdentifier(ESRI,    "Latitude_Of_Origin"),
            new NamedIdentifier(ESRI,    "Latitude_Of_Center"), esri,
            new NamedIdentifier(NETCDF,  "latitude_of_projection_origin"),
            new NamedIdentifier(GEOTIFF, "NatOriginLat"),
            new NamedIdentifier(GEOTIFF, "FalseOriginLat"),
            new NamedIdentifier(GEOTIFF, "ProjCenterLat"),
            new NamedIdentifier(GEOTIFF, "CenterLat"),
            new NamedIdentifier(PROJ4,   "lat_0")
        }, 0, -90, 90, NonSI.DEGREE_ANGLE, true);

        STANDARD_PARALLEL = new DefaultParameterDescriptor<>(Collections.singletonMap(NAME_KEY, nc),
            double[].class, null, null, null, null, NonSI.DEGREE_ANGLE, false);

        STANDARD_PARALLEL_1 = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "standard_parallel_1"),
            new NamedIdentifier(OGC,     "pseudo_standard_parallel_1"),
            new NamedIdentifier(EPSG,    "Latitude of standard parallel"), epsg,
            new NamedIdentifier(EPSG,    "Latitude of pseudo standard parallel"),
            new NamedIdentifier(ESRI,    "Pseudo_Standard_Parallel_1"), esri,
            new NamedIdentifier(NETCDF,  "standard_parallel[1]"), nc, // Because this parameter is an array.
            new NamedIdentifier(GEOTIFF, "StdParallel1"),
            new NamedIdentifier(PROJ4,   "lat_1")
        }, Double.NaN, -90, 90, NonSI.DEGREE_ANGLE, false);
    }

    /**
     * All known names for the standard parallel 2 parameter.
     * This parameter is optional. The range of valid values is [-90 &hellip; 90]&deg;.
     *
     * <blockquote><b>EPSG description:</b> For a conic projection with two standard parallels,
     * this is the latitude of intersection of the cone with the ellipsoid that is furthest from
     * the pole. Scale is true along this parallel.</blockquote>
     * <p>
     * Some names for this parameter are {@code "Latitude of 2nd standard parallel"},
     * {@code "standard_parallel_2"}, {@code "StdParallel2"} and {@code "lat_2"}.
     */
    public static final UniversalParameters STANDARD_PARALLEL_2 = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "standard_parallel_2"),
            new NamedIdentifier(EPSG,    "Latitude of 2nd standard parallel"),
            new NamedIdentifier(ESRI,    "Standard_Parallel_2"),
            new NamedIdentifier(NETCDF,  "standard_parallel[2]"),
            new NamedIdentifier(GEOTIFF, "StdParallel2"),
            new NamedIdentifier(PROJ4,   "lat_2")
        }, Double.NaN, -90, 90, NonSI.DEGREE_ANGLE, false);

    /**
     * All known names for the {@code latitudeOf1stPoint} parameter.
     * This parameter is mandatory and has no default value.
     * The range of valid values is [-90 &hellip; 90]&deg;.
     */
    public static final UniversalParameters LAT_OF_1ST_POINT = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(Citations.ESRI, "Latitude_Of_1st_Point")
        }, Double.NaN, -90, 90, NonSI.DEGREE_ANGLE, true);

    /**
     * All known names for the {@code longitudeOf1stPoint} parameter.
     * This parameter is mandatory and has no default value.
     * The range of valid values is [-180 &hellip; 180]&deg;.
     */
    public static final UniversalParameters LONG_OF_1ST_POINT = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(Citations.ESRI, "Longitude_Of_1st_Point")
        }, Double.NaN, -180, 180, NonSI.DEGREE_ANGLE, true);

    /**
     * All known names for the {@code latitudeOf2ndPoint} parameter.
     * This parameter is mandatory and has no default value.
     * The range of valid values is [-90 &hellip; 90]&deg;.
     */
    public static final UniversalParameters LAT_OF_2ND_POINT = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(Citations.ESRI, "Latitude_Of_2nd_Point")
        }, Double.NaN, -90, 90, NonSI.DEGREE_ANGLE, true);

    /**
     * All known names for the {@code longitudeOf2ndPoint} parameter.
     * This parameter is mandatory and has no default value.
     * The range of valid values is [-180 &hellip; 180]&deg;.
     */
    public static final UniversalParameters LONG_OF_2ND_POINT = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(Citations.ESRI, "Longitude_Of_2nd_Point")
        }, Double.NaN, -180, 180, NonSI.DEGREE_ANGLE, true);

    /**
     * All known names for the {@code azimuth} parameter.
     * This parameter is mandatory and has no default value.
     *
     * <blockquote><b>EPSG description:</b> The azimuthal direction (north zero, east of north
     * being positive) of the great circle which is the centre line of an oblique projection.
     * The azimuth is given at the projection center.</blockquote>
     * <p>
     * Some names for this parameter are {@code "Azimuth of initial line"},
     * {@code "Co-latitude of cone axis"}, {@code "azimuth"} and {@code "AzimuthAngle"}.
     */
    public static final UniversalParameters AZIMUTH = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(OGC,      "azimuth"),
            new NamedIdentifier(EPSG,     "Azimuth of initial line"),
            new NamedIdentifier(EPSG,     "Co-latitude of cone axis"), // Used in Krovak projection.
            new NamedIdentifier(ESRI,     "Azimuth"),
            new NamedIdentifier(GEOTIFF,  "AzimuthAngle")
        }, Double.NaN, -360, 360, NonSI.DEGREE_ANGLE, true);

    /**
     * All known names for the {@code rectifiedGridAngle} parameter.
     * This is an optional parameter with valid values ranging [-360 &hellip; 360]&deg;.
     * The default value is the value of the {@linkplain #AZIMUTH azimuth} parameter.
     *
     * <blockquote><b>EPSG description:</b> The angle at the natural origin of an oblique projection
     * through which the natural coordinate reference system is rotated to make the projection
     * north axis parallel with true north.</blockquote>
     * <p>
     * Some names for this parameter are {@code "Angle from Rectified to Skew Grid"},
     * {@code "rectified_grid_angle"}, {@code "RectifiedGridAngle"} and
     * {@code "XY_Plane_Rotation"}.
     */
    public static final UniversalParameters RECTIFIED_GRID_ANGLE = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(Citations.OGC,      "rectified_grid_angle"),
            new NamedIdentifier(Citations.EPSG,     "Angle from Rectified to Skew Grid"),
            new NamedIdentifier(Citations.ESRI,     "XY_Plane_Rotation"),
            new NamedIdentifier(Citations.GEOTIFF,  "RectifiedGridAngle")
        }, Double.NaN, -360, 360, NonSI.DEGREE_ANGLE, false);

    /**
     * All known names for the
     * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor
     * scale factor} parameter.
     * This parameter is mandatory - meaning that it appears in {@linkplain ParameterValueGroup
     * parameter value group} even if the user didn't set it explicitly - and its default value
     * is 1. The range of valid values is (0 &hellip; &infin;).
     * <p>
     * Some names for this parameter are {@code "Scale factor at natural origin"},
     * {@code "Scale factor on initial line"}, {@code "Scale factor on pseudo standard parallel"},
     * {@code "scale_factor"}, {@code "scale_factor_at_projection_origin"},
     * {@code "scale_factor_at_central_meridian"}, {@code "ScaleAtNatOrigin"},
     * {@code "ScaleAtCenter"} and {@code "k"}.
     *
     * @see org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor
     */
    public static final UniversalParameters SCALE_FACTOR = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "scale_factor"),
            new NamedIdentifier(EPSG,    "Scale factor at natural origin"),
            new NamedIdentifier(EPSG,    "Scale factor on initial line"),
            new NamedIdentifier(EPSG,    "Scale factor on pseudo standard parallel"),
            new NamedIdentifier(ESRI,    "Scale_Factor"),
            new NamedIdentifier(NETCDF,  "scale_factor_at_projection_origin"),
            new NamedIdentifier(NETCDF,  "scale_factor_at_central_meridian"),
            new NamedIdentifier(GEOTIFF, "ScaleAtNatOrigin"),
            new NamedIdentifier(GEOTIFF, "ScaleAtCenter"),
            new NamedIdentifier(PROJ4,   "k")
        }, 1, 0, Double.POSITIVE_INFINITY, Unit.ONE, true);

    /**
     * All known names for the {@code "X_Scale"} parameter.
     * This parameter is optional and its default value is 1.
     * The range of valid values is unrestricted (but value 0 is not recommended).
     * In particular, negative values can be used for reverting the axis orientation.
     * <p>
     * This is an ESRI-specific parameter, sometime used instead of {@code "AXIS"} elements
     * in <cite>Well Known Text</cite> for resolving axis orientation (especially for the
     * {@linkplain Krovak} projection). However its usage could be extended to any projection.
     * The choice to allow this parameter or not is taken on a projection-by-projection basis.
     */
    public static final UniversalParameters X_SCALE = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(ESRI, "X_Scale")
        }, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Unit.ONE, false);

    /**
     * All known names for the {@code "Y_Scale"} parameter.
     * This parameter is optional and its default value is 1.
     * The range of valid values is unrestricted (but value 0 is not recommended).
     * In particular, negative values can be used for reverting the axis orientation.
     * <p>
     * This is an ESRI-specific parameter, sometime used instead of {@code "AXIS"} elements
     * in <cite>Well Known Text</cite> for resolving axis orientation (especially for the
     * {@linkplain Krovak} projection). However its usage could be extended to any projection.
     * The choice to allow this parameter or not is taken on a projection-by-projection basis.
     */
    public static final UniversalParameters Y_SCALE = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(ESRI, "Y_Scale")
        }, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Unit.ONE, false);

    /**
     * All known names for the
     * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseEasting
     * false easting} parameter.
     * This parameter is mandatory - meaning that it appears in {@linkplain ParameterValueGroup
     * parameter value group} even if the user didn't set it explicitly - and its default value
     * is 0 metres. The range of valid values is unrestricted.
     * <p>
     * Some names for this parameter are {@code "Easting at false origin"},
     * {@code "Easting at projection centre"}, {@code "false_easting"}, {@code "FalseEasting"},
     * {@code "FalseOriginEasting"} and {@code "x_0"}.
     *
     * @see org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseEasting
     */
    public static final UniversalParameters FALSE_EASTING = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "false_easting"),
            new NamedIdentifier(EPSG,    "False easting"),
            new NamedIdentifier(EPSG,    "Easting at false origin"),
            new NamedIdentifier(EPSG,    "Easting at projection centre"),
            new NamedIdentifier(ESRI,    "False_Easting"),
            new NamedIdentifier(NETCDF,  "false_easting"),
            new NamedIdentifier(GEOTIFF, "FalseEasting"),
            new NamedIdentifier(GEOTIFF, "FalseOriginEasting"),
            new NamedIdentifier(PROJ4,   "x_0")
        }, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SI.METRE, true);

    /**
     * All known names for the
     * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseNorthing
     * false northing} parameter.
     * This parameter is mandatory - meaning that it appears in {@linkplain ParameterValueGroup
     * parameter value group} even if the user didn't set it explicitly - and its default value
     * is 0 metres. The range of valid values is unrestricted.
     * <p>
     * Some names for this parameter are {@code "Northing at false origin"},
     * {@code "Northing at projection centre"}, {@code "false_northing"}, {@code "FalseNorthing"},
     * {@code "FalseOriginNorthing"} and {@code "y_0"}.
     *
     * @see org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseNorthing
     */
    public static final UniversalParameters FALSE_NORTHING = new UniversalParameters(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "false_northing"),
            new NamedIdentifier(EPSG,    "False northing"),
            new NamedIdentifier(EPSG,    "Northing at false origin"),
            new NamedIdentifier(EPSG,    "Northing at projection centre"),
            new NamedIdentifier(ESRI,    "False_Northing"),
            new NamedIdentifier(NETCDF,  "false_northing"),
            new NamedIdentifier(GEOTIFF, "FalseNorthing"),
            new NamedIdentifier(GEOTIFF, "FalseOriginNorthing"),
            new NamedIdentifier(PROJ4,   "y_0")
        }, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SI.METRE, true);

    /**
     * The identifiers which can be declared to the descriptor. Only a subset of those values
     * will actually be used. The subset is specified by a call to a {@code select} method.
     */
    private final NamedIdentifier[] identifiers;

    /**
     * Locates the identifiers by their {@linkplain Identifier#getCode() code}.
     * If there is more than one parameter instance for the same name, this map contains
     * only the first occurrence. The other occurrences can be obtained by {@link #nextSameName}.
     */
    private final Map<String,NamedIdentifier> identifiersMap;

    /**
     * If there is many parameter instances for the same name, allow to iterate over the
     * other instances. Otherwise, {@code null}.
     */
    private final Map<NamedIdentifier,NamedIdentifier> nextSameName;

    /**
     * Creates a new instance of {@code Identifiers} for the given identifiers.
     * The array given in argument should never be modified, since it will not be cloned.
     *
     * @param identifiers  The parameter identifiers. Must contains at least one entry.
     * @param defaultValue The default value for the parameter, or {@link Double#NaN} if none.
     * @param minimum      The minimum parameter value, or {@link Double#NEGATIVE_INFINITY} if none.
     * @param maximum      The maximum parameter value, or {@link Double#POSITIVE_INFINITY} if none.
     * @param unit         The unit for default, minimum and maximum values.
     * @param required     {@code true} if the parameter is mandatory.
     */
    private UniversalParameters(final NamedIdentifier[] identifiers, final double defaultValue,
            final double minimum, final double maximum, final Unit<?> unit, final boolean required)
    {
        super(toMap(identifiers), Double.class, null,
                Double.isNaN(defaultValue)          ? null : Double.valueOf(defaultValue),
                minimum == Double.NEGATIVE_INFINITY ? null : Double.valueOf(minimum),
                maximum == Double.POSITIVE_INFINITY ? null : Double.valueOf(maximum), unit, required);
        this.identifiers = identifiers;
        identifiersMap = new HashMap<>(hashMapCapacity(identifiers.length));
        Map<NamedIdentifier,NamedIdentifier> nextSameName = null;
        // Put elements in reverse order in order to give precedence to the first occurrence.
        for (int i=identifiers.length; --i>=0;) {
            final NamedIdentifier id = identifiers[i];
            final NamedIdentifier old = identifiersMap.put(id.getCode(), id);
            if (old != null) {
                if (nextSameName == null) {
                    nextSameName = new IdentityHashMap<>(4);
                }
                nextSameName.put(id, old);
            }
        }
        this.nextSameName = nextSameName;
    }

    /**
     * Returns a new descriptor having the same identifiers than this descriptor.
     * The given array is used for disambiguation when the same authority defines
     * many names.
     *
     * @param  excludes   The authorities to exclude, or {@code null} if none.
     * @param  names      The names to be used for disambiguation.
     * @return The requested identifiers.
     */
    final ParameterDescriptor<Double> select(final Citation[] excludes, final String... names) {
        return select(getMinimumOccurs() != 0, getDefaultValue(), excludes, null, names);
    }

    /**
     * Returns a new descriptor having the same identifiers than this descriptor but a different
     * {@code mandatory} status and default value. The given array is used for disambiguation when
     * the same authority defines many names.
     *
     * @param  required     Whatever the parameter shall be mandatory or not, or {@code null} if unchanged.
     * @param  defaultValue The default value, or {@code null} for keeping it unchanged.
     * @param  excludes     The authorities to exclude, or {@code null} if none.
     * @param  deprecated   The names of deprecated identifiers, or {@code null} if none.
     * @param  names        The names to be used for disambiguation.
     *                      The same name may be used for more than one authority.
     * @return The requested identifiers.
     */
    final ParameterDescriptor<Double> select(final Boolean required, final Double defaultValue,
            final Citation[] excludes, final String[] deprecated, final String... names)
    {
        final Map<Citation,Boolean> authorities = new HashMap<>();
        NamedIdentifier[] selected = new NamedIdentifier[identifiers.length];
        long usedIdent = 0; // A bitmask of elements from the 'identifiers' array which have been used.
        long usedNames = 0; // A bitmask of elements from the given 'names' array which have been used.
        /*
         * Finds every identifiers which have not been excluded. In this process, also take note
         * of every identifiers explicitly requested by the names array given in argument.
         */
        int included = 0;
        for (final NamedIdentifier candidate : identifiers) {
            final Citation authority = candidate.getAuthority();
            if (ArraysExt.contains(excludes, authority)) {
                continue;
            }
            selected[included] = candidate;
            final String code = candidate.getCode();
            for (int j=names.length; --j>=0;) {
                if (code.equals(names[j])) {
                    if (authorities.put(authority, Boolean.TRUE) != null) {
                        throw new IllegalArgumentException(Errors.format(
                                Errors.Keys.ValueAlreadyDefined_1, authority));
                    }
                    usedNames |= (1 << j);
                    usedIdent |= (1 << included);
                    break;
                }
            }
            included++;
        }
        /*
         * If a name has not been used, this is considered as an error. We perform
         * this check for reducing the risk of erroneous declaration in providers.
         * Note that the same name may be used for more than one authority.
         */
        if (usedNames != (1 << names.length) - 1) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.UnknownParameter_1, names[Long.numberOfTrailingZeros(~usedNames)]));
        }
        /*
         * If some identifiers were selected as a result of explicit requirement through the
         * names array, discards all other identifiers of that authority. Otherwise if there
         * is some remaining authorities declaring exactly one identifier, inherits that
         * identifier silently. If more than one identifier is found for the same authority,
         * this is considered an error.
         */
        int n = 0;
        for (int i=0; i<included; i++) {
            final NamedIdentifier candidate = selected[i];
            if ((usedIdent & (1 << i)) == 0) {
                final Citation authority = candidate.getAuthority();
                final Boolean explicit = authorities.put(authority, Boolean.FALSE);
                if (explicit != null) {
                    // An identifier was already specified for this authority.
                    // If the identifier was specified explicitly by the user,
                    // do nothing. Otherwise we have ambiguity.
                    if (explicit) {
                        authorities.put(authority, Boolean.TRUE); // Restore the previous value.
                        continue;
                    }
                    throw new IllegalStateException(String.valueOf(candidate));
                }
            }
            selected[n++] = candidate;
        }
        /*
         * Adds deprecated names, if any. Those names will appears last in the names array.
         * Note that at the difference of ordinary names, we don't share deprecated names
         * between different provider. Deprecated names are rare enough that this is not needed.
         */
        if (deprecated != null) {
            selected = ArraysExt.resize(selected, n + deprecated.length);
            for (final String code : deprecated) {
                selected[n++] = new DeprecatedName(identifiersMap.get(code));
            }
        }
        selected = ArraysExt.resize(selected, n);
        return new DefaultParameterDescriptor<>(toMap(selected), Double.class, null,
                (defaultValue != null) ? defaultValue : getDefaultValue(),
                getMinimumValue(), getMaximumValue(), getUnit(),
                (required != null) ? required : getMinimumOccurs() != 0);
    }

    /**
     * Returns the element from the given collection having at least one of the names known to
     * this {@code UniversalParameters} instance. If no such element is found, returns {@code null}.
     *
     * @param  candidates The collection of descriptors to compare with the names known to this
     *         {@code UniversalParameters} instance.
     * @return A descriptor from the given collection, or {@code null} if this method did not
     *         found any descriptor having at least one known name.
     * @throws IllegalArgumentException If more than one descriptor having a known name is found.
     */
    public ParameterDescriptor<?> find(final Collection<GeneralParameterDescriptor> candidates)
            throws IllegalArgumentException
    {
        ParameterDescriptor<?> found = null;
        for (final GeneralParameterDescriptor candidate : candidates) {
            final Identifier candidateId = candidate.getName();
            NamedIdentifier identifier = identifiersMap.get(candidateId.getCode());
            while (identifier != null) {
                final Citation authority = candidateId.getAuthority();
                if (authority == null || org.apache.sis.metadata.iso.citation.Citations.identifierMatches(authority, identifier.getAuthority())) {
                    if (candidate instanceof ParameterDescriptor<?>) {
                        if (found != null) {
                            throw new IllegalArgumentException(Errors.format(
                                    Errors.Keys.AmbiguousValue_1, getName().getCode()) +
                                    IdentifiedObjects.toString(found.getName()) + ", " +
                                    IdentifiedObjects.toString(candidate.getName()));
                        }
                        found = (ParameterDescriptor<?>) candidate;
                        break; // Continue the 'for' loop.
                    } else {
                        // Name matches, but this is not an instance of parameter descriptor.
                        // It is probably an error. For now continue the search, but future
                        // implementations may do some other action here.
                    }
                }
                if (nextSameName == null) break;
                identifier = nextSameName.get(identifier);
            }
        }
        return found;
    }

    /**
     * Constructs a parameter descriptor for a mandatory floating point value. The parameter is
     * identified by codes in the namespace of one or more authorities ({@link Citations#OGC OGC},
     * {@link Citations#EPSG EPSG}, <i>etc.</i>). Those codes are declared as elements in the
     * {@code identifiers} array argument. The first element ({@code identifiers[0]}) is both the
     * {@linkplain ParameterDescriptor#getName main name} and the
     * {@linkplain ParameterDescriptor#getIdentifiers identifiers}.
     * All others elements are {@linkplain ParameterDescriptor#getAlias aliases}.
     * <p>
     * The {@code required} argument is handled as below:
     * <ul>
     *   <li><p>If {@code true}, then the descriptor created by this method is flagged as
     *   <cite>mandatory</cite>, meaning that it will always appear in the list of parameter values
     *   that a user shall provide. However the value will be initialized with the given default
     *   value (if different than {@linkplain Double#NaN NaN}), so the user may not needs to supply
     *   explicitly a value.</p></li>
     *
     *   <li><p>If {@code false}, then the descriptor created by this method is flagged as
     *   <cite>optional</cite>, meaning that it will appear in the list of parameter values
     *   only if set to a value different than the default value.</p></li>
     * </ul>
     *
     * @param  identifiers  The parameter identifiers. Must contains at least one entry.
     * @param  defaultValue The default value for the parameter, or {@link Double#NaN} if none.
     * @param  minimum      The minimum parameter value, or {@link Double#NEGATIVE_INFINITY} if none.
     * @param  maximum      The maximum parameter value, or {@link Double#POSITIVE_INFINITY} if none.
     * @param  unit         The unit for default, minimum and maximum values.
     * @param  required     {@code true} if the parameter is mandatory, or {@code false} if optional.
     * @return The descriptor for the given identifiers.
     */
    static ParameterDescriptor<Double> createDescriptor(
            final Identifier[] identifiers, final double defaultValue,
            final double minimum, final double maximum, final Unit<?> unit, final boolean required)
    {
        return new DefaultParameterDescriptor<Double>(toMap(identifiers), Double.class, null,
                Double.isNaN(defaultValue)          ? null : Double.valueOf(defaultValue),
                minimum == Double.NEGATIVE_INFINITY ? null : Double.valueOf(minimum),
                maximum == Double.POSITIVE_INFINITY ? null : Double.valueOf(maximum), unit, required);
    }

    /**
     * Constructs a parameter group from a set of alias. The parameter group is
     * identified by codes provided by one or more authorities. Common authorities are
     * {@link Citations#OGC OGC} and {@link Citations#EPSG EPSG} for example.
     * <p>
     * Special rules:
     * <ul>
     *   <li>The first entry in the {@code identifiers} array is the
     *       {@linkplain ParameterDescriptorGroup#getName() primary name}.</li>
     *   <li>If an identifier does not implements the {@link GenericName} interface, it is
     *       used as an {@linkplain ParameterDescriptorGroup#getIdentifiers identifiers}.</li>
     *   <li>All others are {@linkplain ParameterDescriptorGroup#getAlias aliases}.</li>
     * </ul>
     * <p>
     * <b>Note:</b> This method may modify in-place the given parameters array.
     * Do not pass a cached array.
     *
     * @param  identifiers  The operation identifiers. Must contains at least one entry.
     * @param  excludes     The authorities to exclude from all parameters, or {@code null} if none.
     * @param  parameters   The set of parameters, or {@code null} or an empty array if none.
     * @param  supplement   Non-standard dynamic parameters to add as bitwise combination of
     *                      {@link MapProjectionDescriptor}, or 0 if none.
     * @return The descriptor for the given identifiers.
     */
    static ParameterDescriptorGroup createDescriptorGroup(final Identifier[] identifiers,
            final Citation[] excludes, final ParameterDescriptor<?>[] parameters, final int supplement)
    {
        if (excludes != null) {
            final Map<String,Object> properties = new HashMap<>();
            for (int i=0; i<parameters.length; i++) {
                @SuppressWarnings("unchecked")
                final ParameterDescriptor<Double> param = (ParameterDescriptor) parameters[i];
                if (param.getValueClass() != Double.class) {
                    // To be strict, we should have done this check before the above cast.
                    // However since generic types are implemented in Java by type erasure,
                    // it actually doesn't hurt to perform the check after, so we can use
                    // the 'param' field.
                    continue;
                }
                properties.putAll(IdentifiedObjects.getProperties(param));
                boolean forAlias = false;
                boolean modified = false;
                Object[] aliases;
                do { // Executed exactly twice: once for identifier, then once for aliases.
                    final String key = forAlias ? ALIAS_KEY : IDENTIFIERS_KEY;
                    aliases = (Object[]) properties.get(key);
                    if (aliases != null) {
                        int n = 0;
                        for (final Object alias : aliases) {
                            if (alias instanceof Identifier) {
                                if (ArraysExt.contains(excludes, ((Identifier) alias).getAuthority())) {
                                    continue;
                                }
                            }
                            aliases[n++] = alias;
                        }
                        // If at least one alias or identifier has been removed, remember that we
                        // will need to create a new parameter in replacement to the provided one.
                        if (n != aliases.length) {
                            properties.put(key, Arrays.copyOf(aliases, n));
                            modified = true;
                        }
                    }
                } while ((forAlias = !forAlias) == true);
                if (modified) {
                    properties.put(NAME_KEY, aliases[0]); // In case the primary name was one of the excluded names.
                    parameters[i] = new DefaultParameterDescriptor<>(properties, Double.class, null,
                            param.getDefaultValue(), param.getMinimumValue(), param.getMaximumValue(),
                            param.getUnit(), param.getMinimumOccurs() != 0);
                }
                properties.clear();
            }
        }
        final Map<String,Object> properties = toMap(identifiers);
        return (supplement == 0) ? new DefaultParameterDescriptorGroup(properties, 1, 1, parameters) :
                new MapProjectionDescriptor(properties, parameters, supplement);
    }

    /**
     * Puts the identifiers into a properties map suitable for {@link ParameterDescriptorGroup}
     * constructor. The first identifier is used as the primary name. All other elements are aliases.
     */
    private static Map<String,Object> toMap(final Identifier[] identifiers) {
        int idCount    = 0;
        int aliasCount = 0;
        GenericName[] alias = null;
        Identifier[] id = null;
        for (int i=0; i<identifiers.length; i++) {
            final Identifier candidate = identifiers[i];
            if (candidate instanceof GenericName) {
                if (alias == null) {
                    alias = new GenericName[identifiers.length - i];
                }
                alias[aliasCount++] = (GenericName) candidate;
            } else {
                if (id == null) {
                    id = new Identifier[identifiers.length - i];
                }
                id[idCount++] = candidate;
            }
        }
        id    = ArraysExt.resize(id,    idCount);
        alias = ArraysExt.resize(alias, aliasCount);
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put(NAME_KEY,        identifiers[0]);
        properties.put(IDENTIFIERS_KEY, id);
        properties.put(ALIAS_KEY,       alias);
        return properties;
    }
}

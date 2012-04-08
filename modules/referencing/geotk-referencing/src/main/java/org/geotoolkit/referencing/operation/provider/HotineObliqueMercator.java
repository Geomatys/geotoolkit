/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.List;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Hotine Oblique Mercator</cite>" projection (EPSG:9812).
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/hotine_oblique_mercator.html">Hotine
 * Oblique Mercator on RemoteSensing.org</A>. The math transform implementations instantiated by
 * this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.ObliqueMercator}</li>
 * </ul>
 * <p>
 * This projection is similar to the {@linkplain ObliqueMercator oblique mercator} projection,
 * except that coordinates start at the intersection of the central line and the equator
 * of the aposphere.
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 2.4
 * @module
 */
@Immutable
public class HotineObliqueMercator extends ObliqueMercator {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 5822488360988630419L;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseEasting
     * false easting} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 0 metre.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> FALSE_EASTING = EquidistantCylindrical.FALSE_EASTING;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseNorthing
     * false northing} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 0 metre.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> FALSE_NORTHING = EquidistantCylindrical.FALSE_NORTHING;

    /**
     * The parameters group.
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table bgcolor="#F4F8FF" border="1" cellspacing="0" cellpadding="6">
     *   <tr bgcolor="#B9DCFF" valign="top"><td colspan="2">
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>Hotine_Oblique_Mercator</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Hotine Oblique Mercator (variant A)</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Hotine Oblique Mercator</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Hotine_Oblique_Mercator_Azimuth_Natural_Origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Rectified_Skew_Orthomorphic_Natural_Origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>CT_ObliqueMercator_Hotine</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>omerc</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>Geotk:</code></td><td><code>Rectified Skew Orthomorphic (RSO)</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>Geotk:</code></td><td><code>Oblique Mercator projection</code></td></tr>
     *       <tr><th align="left">Identifier:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>9812</code></td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>semi_major</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Semi-major axis</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Semi_Major</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>SemiMajor</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>a</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>semi_minor</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Semi-minor axis</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Semi_Minor</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>SemiMinor</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>b</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>Geotk:</code></td><td><code>roll_longitude</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Boolean</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>false</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>longitude_of_center</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Longitude of projection centre</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Longitude_Of_Center</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>CenterLong</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>lon_0</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-180 … 180]°</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0°</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>latitude_of_center</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Latitude of projection centre</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Latitude_Of_Center</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>CenterLat</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>lat_0</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-90 … 90]°</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0°</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>azimuth</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Azimuth of initial line</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Azimuth</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>AzimuthAngle</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-360 … 360]°</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>rectified_grid_angle</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Angle from Rectified to Skew Grid</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>XY_Plane_Rotation</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>RectifiedGridAngle</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-360 … 360]°</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0°</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>scale_factor</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Scale factor on initial line</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Scale_Factor</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>ScaleAtCenter</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>k</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[0…∞)</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>1</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>false_easting</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>False easting</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>False_Easting</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>FalseEasting</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>x_0</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>(-∞ … ∞) metres</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>false_northing</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>False northing</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>False_Northing</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>FalseNorthing</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>y_0</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>(-∞ … ∞) metres</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     * </table>
     */
    @SuppressWarnings("hiding")
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,     "Hotine_Oblique_Mercator"),
            new NamedIdentifier(Citations.EPSG,    "Hotine Oblique Mercator (variant A)"), // Starting from 7.6
            new NamedIdentifier(Citations.EPSG,    "Hotine Oblique Mercator"), // Prior to EPSG version 7.6
            new IdentifierCode (Citations.EPSG,     9812),
            new NamedIdentifier(Citations.ESRI,    "Hotine_Oblique_Mercator_Azimuth_Natural_Origin"),
            new NamedIdentifier(Citations.ESRI,    "Rectified_Skew_Orthomorphic_Natural_Origin"),
            new NamedIdentifier(Citations.GEOTIFF, "CT_ObliqueMercator_Hotine"),
            // Note: The GeoTIFF numerical code (3) is already used by CT_ObliqueMercator.
                     sameNameAs(Citations.PROJ4, ObliqueMercator.PARAMETERS),
            new NamedIdentifier(Citations.GEOTOOLKIT, "Rectified Skew Orthomorphic (RSO)"), // Legacy EPSG
                     sameNameAs(Citations.GEOTOOLKIT, ObliqueMercator.PARAMETERS)
        }, null, new ParameterDescriptor<?>[] {
            SEMI_MAJOR,          SEMI_MINOR, ROLL_LONGITUDE,
            LONGITUDE_OF_CENTRE, LATITUDE_OF_CENTRE,
            AZIMUTH,             RECTIFIED_GRID_ANGLE,
            SCALE_FACTOR,
            FALSE_EASTING,       FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public HotineObliqueMercator() {
        super(PARAMETERS);
    }

    /**
     * Constructs a new provider for the given parameters.
     */
    HotineObliqueMercator(ParameterDescriptorGroup parameters) {
        super(parameters);
    }




    /**
     * The provider for "<cite>Hotine Oblique Mercator</cite>" projection specified by two points
     * on the central line. This is different than the classical {@linkplain HotineObliqueMercator
     * Hotine Oblique Mercator}, which uses a central point and azimuth.
     *
     * @author Rueben Schulz (UBC)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.20
     *
     * @see org.geotoolkit.referencing.operation.projection.ObliqueMercator
     *
     * @since 2.4
     * @module
     */
    @Immutable
    public static class TwoPoint extends HotineObliqueMercator {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -3104452416276842816L;

        /**
         * The operation parameter descriptor for the {@code latitudeOf1stPoint} parameter value.
         * Valid values range is [-90 &hellip; 90]&deg;. This parameter is mandatory and has no
         * default value.
         */
        public static final ParameterDescriptor<Double> LAT_OF_1ST_POINT =
                ObliqueMercator.TwoPoint.LAT_OF_1ST_POINT;

        /**
         * The operation parameter descriptor for the {@code longitudeOf1stPoint} parameter value.
         * Valid values range is [-180 &hellip; 180]&deg;. This parameter is mandatory and has no
         * default value.
         */
        public static final ParameterDescriptor<Double> LONG_OF_1ST_POINT =
                ObliqueMercator.TwoPoint.LONG_OF_1ST_POINT;

        /**
         * The operation parameter descriptor for the {@code latitudeOf2ndPoint} parameter value.
         * Valid values range is [-90 &hellip; 90]&deg;. This parameter is mandatory and has no
         * default value.
         */
        public static final ParameterDescriptor<Double> LAT_OF_2ND_POINT =
                ObliqueMercator.TwoPoint.LAT_OF_2ND_POINT;

        /**
         * The operation parameter descriptor for the {@code longitudeOf2ndPoint} parameter value.
         * Valid values range is [-180 &hellip; 180]&deg;. This parameter is mandatory and has no
         * default value.
         */
        public static final ParameterDescriptor<Double> LONG_OF_2ND_POINT =
                ObliqueMercator.TwoPoint.LONG_OF_2ND_POINT;

        /**
         * The parameters group.
         * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
         * <table bgcolor="#F4F8FF" border="1" cellspacing="0" cellpadding="6">
         *   <tr bgcolor="#B9DCFF" valign="top"><td colspan="2">
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Hotine_Oblique_Mercator_Two_Point_Natural_Origin</code></td></tr>
         *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>Geotk:</code></td><td><code>Rectified Skew Orthomorphic (RSO)</code></td></tr>
         *     </table>
         *   </td></tr>
         *   <tr valign="top"><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Semi_Major</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
         *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
         *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[0…∞) metres</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr valign="top"><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Semi_Minor</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
         *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
         *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[0…∞) metres</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr valign="top"><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>Geotk:</code></td><td><code>roll_longitude</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Boolean</code></td></tr>
         *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
         *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>false</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr valign="top"><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Latitude_Of_1st_Point</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
         *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
         *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-90 … 90]°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr valign="top"><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Longitude_Of_1st_Point</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
         *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
         *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-180 … 180]°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr valign="top"><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Latitude_Of_2nd_Point</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
         *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
         *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-90 … 90]°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr valign="top"><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Longitude_Of_2nd_Point</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
         *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
         *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-180 … 180]°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr valign="top"><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Latitude_Of_Center</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
         *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
         *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-90 … 90]°</td></tr>
         *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr valign="top"><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Scale_Factor</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
         *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
         *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[0…∞)</td></tr>
         *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>1</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr valign="top"><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>False_Easting</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
         *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
         *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>(-∞ … ∞) metres</td></tr>
         *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0 metres</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr valign="top"><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>False_Northing</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
         *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
         *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>(-∞ … ∞) metres</td></tr>
         *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0 metres</td></tr>
         *     </table>
         *   </td></tr>
         * </table>
         */
        @SuppressWarnings("hiding")
        public static final ParameterDescriptorGroup PARAMETERS;
        static {
            final List<GeneralParameterDescriptor> param = ObliqueMercator.TwoPoint.PARAMETERS.descriptors();
            PARAMETERS = Identifiers.createDescriptorGroup(
                new ReferenceIdentifier[] {
                    new NamedIdentifier(Citations.ESRI, "Hotine_Oblique_Mercator_Two_Point_Natural_Origin"),
                    sameNameAs(Citations.GEOTOOLKIT, HotineObliqueMercator.PARAMETERS)
            }, null, param.toArray(new ParameterDescriptor<?>[param.size()]));
        }

        /**
         * Constructs a new provider.
         */
        public TwoPoint() {
            super(PARAMETERS);
        }
    }
}

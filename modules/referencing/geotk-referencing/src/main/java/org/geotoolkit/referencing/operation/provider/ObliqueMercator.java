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

import net.jcip.annotations.Immutable;

import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.CylindricalProjection;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.internal.referencing.DeprecatedName;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Oblique Mercator</cite>" projection (EPSG:9815).
 * The math transform implementations instantiated by this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.ObliqueMercator}</li>
 * </ul>
 *
 * <!-- PARAMETERS ObliqueMercator -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code Oblique_Mercator}</p>
 * <table class="geotk">
 *   <tr><th>Parameter Name</th><th>Default value</th></tr>
 *   <tr><td>{@code semi_major}</td><td></td></tr>
 *   <tr><td>{@code semi_minor}</td><td></td></tr>
 *   <tr><td>{@code roll_longitude}</td><td>false</td></tr>
 *   <tr><td>{@code longitude_of_center}</td><td>0°</td></tr>
 *   <tr><td>{@code latitude_of_center}</td><td>0°</td></tr>
 *   <tr><td>{@code azimuth}</td><td></td></tr>
 *   <tr><td>{@code rectified_grid_angle}</td><td><var>Azimuth of initial line</var></td></tr>
 *   <tr><td>{@code scale_factor}</td><td>1</td></tr>
 *   <tr><td>{@code false_easting}</td><td>0 metres</td></tr>
 *   <tr><td>{@code false_northing}</td><td>0 metres</td></tr>
 * </table></blockquote>
 * <!-- END OF PARAMETERS -->
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see <A HREF="http://www.remotesensing.org/geotiff/proj_list/oblique_mercator.html">Oblique Mercator on RemoteSensing.org</A>
 *
 * @since 2.1
 * @module
 */
@Immutable
public class ObliqueMercator extends MapProjection {
    /**
     * For compatibility with different versions during deserialization.
     */
    private static final long serialVersionUID = 201776686002266891L;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian
     * central meridian} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-180 &hellip; 180]&deg; and default value is 0&deg;.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> LONGITUDE_OF_CENTRE;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#latitudeOfOrigin
     * latitude of origin} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 0&deg;.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> LATITUDE_OF_CENTRE;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#azimuth azimuth}
     * parameter value. Valid values range is from -360 to -270, -90 to 90, and 270 to 360 degrees.
     * This parameter is mandatory and has no default value.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> AZIMUTH;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.ObliqueMercator.Parameters#rectifiedGridAngle
     * rectifiedGridAngle} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">optional</a>.
     * Valid values rage is [-360 &hellip; 360]&deg; and default value is the azimuth.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> RECTIFIED_GRID_ANGLE;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor
     * scale factor} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is (0 &hellip; &infin;) and default value is 1.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> SCALE_FACTOR;

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
    public static final ParameterDescriptor<Double> FALSE_EASTING;

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
    public static final ParameterDescriptor<Double> FALSE_NORTHING;

    /**
     * Parameters creation, which must be done before to initialize the {@link #PARAMETERS} field.
     */
    static {
        final Citation[] excludes = new Citation[] {Citations.NETCDF};
        LONGITUDE_OF_CENTRE = Identifiers.CENTRAL_MERIDIAN.select(excludes,
                "Longitude of projection centre",   // EPSG
                "longitude_of_center",              // OGC
                "Longitude_Of_Center",              // ESRI
                "CenterLong");                      // GeoTIFF
        LATITUDE_OF_CENTRE = Identifiers.LATITUDE_OF_ORIGIN.select(excludes,
                "Latitude of projection centre",    // EPSG
                "latitude_of_center",               // OGC
                "Latitude_Of_Center",               // ESRI
                "CenterLat");                       // GeoTIFF
        AZIMUTH = Identifiers.AZIMUTH.select(excludes,
                "Azimuth of initial line");         // EPSG
        RECTIFIED_GRID_ANGLE = Identifiers.RECTIFIED_GRID_ANGLE;
        SCALE_FACTOR = Identifiers.SCALE_FACTOR.select(excludes,
                "Scale factor on initial line",     // EPSG
                "ScaleAtCenter");                   // GeoTIFF
        FALSE_EASTING = Identifiers.FALSE_EASTING.select(excludes,
                "Easting at projection centre",     // EPSG
                "FalseEasting");                    // GeoTIFF
        FALSE_NORTHING = Identifiers.FALSE_NORTHING.select(excludes,
                "Northing at projection centre",    // EPSG
                "FalseNorthing");                   // GeoTIFF
    }

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk:
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code Oblique_Mercator}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Hotine Oblique Mercator (variant B)}</td></tr>
     *       <tr><td></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Rectified Skew Orthomorphic (RSO)}</td></tr>
     *       <tr><td></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code <del>Oblique Mercator</del>}</td></tr>
     *       <tr><td></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Hotine_Oblique_Mercator_Azimuth_Center}</td></tr>
     *       <tr><td></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Rectified_Skew_Orthomorphic_Center}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code CT_ObliqueMercator}</td></tr>
     *       <tr><td></td><td class="onright">{@code PROJ4}:</td><td class="onleft">{@code omerc}</td></tr>
     *       <tr><td></td><td class="onright">{@code Geotk}:</td><td class="onleft">{@code Oblique Mercator projection}</td></tr>
     *       <tr><td><b>Identifier:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code 9815}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code 3}</td></tr>
     *     </table>
     *   </th></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code semi_major}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Semi-major axis}</td></tr>
     *       <tr><td></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Semi_Major}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code SemiMajor}</td></tr>
     *       <tr><td></td><td class="onright">{@code PROJ4}:</td><td class="onleft">{@code a}</td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code semi_minor}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Semi-minor axis}</td></tr>
     *       <tr><td></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Semi_Minor}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code SemiMinor}</td></tr>
     *       <tr><td></td><td class="onright">{@code PROJ4}:</td><td class="onleft">{@code b}</td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright">{@code Geotk}:</td><td class="onleft">{@code roll_longitude}</td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Boolean}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Default value:</b></td><td>false</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code longitude_of_center}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Longitude of projection centre}</td></tr>
     *       <tr><td></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Longitude_Of_Center}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code CenterLong}</td></tr>
     *       <tr><td></td><td class="onright">{@code PROJ4}:</td><td class="onleft">{@code lon_0}</td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-180 … 180]°</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0°</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code latitude_of_center}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Latitude of projection centre}</td></tr>
     *       <tr><td></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Latitude_Of_Center}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code CenterLat}</td></tr>
     *       <tr><td></td><td class="onright">{@code PROJ4}:</td><td class="onleft">{@code lat_0}</td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-90 … 90]°</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0°</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code azimuth}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Azimuth of initial line}</td></tr>
     *       <tr><td></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Azimuth}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code AzimuthAngle}</td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-360 … 360]°</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code rectified_grid_angle}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Angle from Rectified to Skew Grid}</td></tr>
     *       <tr><td></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code XY_Plane_Rotation}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code RectifiedGridAngle}</td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-360 … 360]°</td></tr>
     *       <tr><td><b>Default value:</b></td><td><var>Azimuth of initial line</var></td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code scale_factor}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Scale factor on initial line}</td></tr>
     *       <tr><td></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Scale_Factor}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code ScaleAtCenter}</td></tr>
     *       <tr><td></td><td class="onright">{@code PROJ4}:</td><td class="onleft">{@code k}</td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞)</td></tr>
     *       <tr><td><b>Default value:</b></td><td>1</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code false_easting}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Easting at projection centre}</td></tr>
     *       <tr><td></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code False_Easting}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code FalseEasting}</td></tr>
     *       <tr><td></td><td class="onright">{@code PROJ4}:</td><td class="onleft">{@code x_0}</td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code false_northing}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Northing at projection centre}</td></tr>
     *       <tr><td></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code False_Northing}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code FalseNorthing}</td></tr>
     *       <tr><td></td><td class="onright">{@code PROJ4}:</td><td class="onleft">{@code y_0}</td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     * </table>
     */
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,     "Oblique_Mercator"),
            new NamedIdentifier(Citations.EPSG,    "Hotine Oblique Mercator (variant B)"), // Starting from 7.6
            new NamedIdentifier(Citations.EPSG,    "Rectified Skew Orthomorphic (RSO)"),
            new DeprecatedName (Citations.EPSG,    "Oblique Mercator"), // Prior to EPSG database version 7.6
            new IdentifierCode (Citations.EPSG,     9815),
            new NamedIdentifier(Citations.ESRI,    "Hotine_Oblique_Mercator_Azimuth_Center"),
            new NamedIdentifier(Citations.ESRI,    "Rectified_Skew_Orthomorphic_Center"),
            new NamedIdentifier(Citations.GEOTIFF, "CT_ObliqueMercator"),
            new IdentifierCode (Citations.GEOTIFF,  3), // Also used by CT_ObliqueMercator_Hotine
            new NamedIdentifier(Citations.PROJ4,   "omerc"),
            new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                Vocabulary.Keys.OBLIQUE_MERCATOR_PROJECTION))
        }, null, new ParameterDescriptor<?>[] {
            SEMI_MAJOR, SEMI_MINOR, ROLL_LONGITUDE,
            LONGITUDE_OF_CENTRE, LATITUDE_OF_CENTRE,
            AZIMUTH, RECTIFIED_GRID_ANGLE, SCALE_FACTOR,
            FALSE_EASTING, FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public ObliqueMercator() {
        super(PARAMETERS);
    }

    /**
     * Constructs a new provider for the given parameters.
     */
    ObliqueMercator(ParameterDescriptorGroup parameters) {
        super(parameters);
    }

    /**
     * Returns the operation type for this map projection.
     */
    @Override
    public Class<CylindricalProjection> getOperationType() {
        return CylindricalProjection.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MathTransform2D createMathTransform(ParameterValueGroup values) {
        return org.geotoolkit.referencing.operation.projection.ObliqueMercator.create(getParameters(), values);
    }




    /**
     * The provider for "<cite>Oblique Mercator</cite>" projection specified by two points
     * on the central line. This is different than the classical {@linkplain ObliqueMercator
     * Oblique Mercator}, which uses a central point and azimuth.
     *
     * <!-- PARAMETERS TwoPoint -->
     * <p>The following table summarizes the parameters recognized by this provider.
     * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
     * <blockquote><p><b>Operation name:</b> {@code Hotine_Oblique_Mercator_Two_Point_Center}</p>
     * <table class="geotk">
     *   <tr><th>Parameter Name</th><th>Default value</th></tr>
     *   <tr><td>{@code Semi_Major}</td><td></td></tr>
     *   <tr><td>{@code Semi_Minor}</td><td></td></tr>
     *   <tr><td>{@code roll_longitude}</td><td>false</td></tr>
     *   <tr><td>{@code Latitude_Of_1st_Point}</td><td></td></tr>
     *   <tr><td>{@code Longitude_Of_1st_Point}</td><td></td></tr>
     *   <tr><td>{@code Latitude_Of_2nd_Point}</td><td></td></tr>
     *   <tr><td>{@code Longitude_Of_2nd_Point}</td><td></td></tr>
     *   <tr><td>{@code Latitude_Of_Center}</td><td>0°</td></tr>
     *   <tr><td>{@code Scale_Factor}</td><td>1</td></tr>
     *   <tr><td>{@code False_Easting}</td><td>0 metres</td></tr>
     *   <tr><td>{@code False_Northing}</td><td>0 metres</td></tr>
     * </table></blockquote>
     * <!-- END OF PARAMETERS -->
     *
     * @author Rueben Schulz (UBC)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.20
     *
     * @see org.geotoolkit.referencing.operation.projection.ObliqueMercator
     *
     * @since 2.1
     * @module
     */
    @Immutable
    public static class TwoPoint extends ObliqueMercator {
        /**
         * For compatibility with different versions during deserialization.
         */
        private static final long serialVersionUID = 7124258885016543889L;

        /**
         * The operation parameter descriptor for the {@code latitudeOf1stPoint} parameter value.
         * Valid values range is [-90 &hellip; 90]&deg;. This parameter is mandatory and has no
         * default value.
         *
         * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
         * descriptor(String)}</code> instead.
         */
        @Deprecated
        public static final ParameterDescriptor<Double> LAT_OF_1ST_POINT = Identifiers.LAT_OF_1ST_POINT;

        /**
         * The operation parameter descriptor for the {@code longitudeOf1stPoint} parameter value.
         * Valid values range is [-180 &hellip; 180]&deg;. This parameter is mandatory and has no
         * default value.
         *
         * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
         * descriptor(String)}</code> instead.
         */
        @Deprecated
        public static final ParameterDescriptor<Double> LONG_OF_1ST_POINT = Identifiers.LONG_OF_1ST_POINT;

        /**
         * The operation parameter descriptor for the {@code latitudeOf2ndPoint} parameter value.
         * Valid values range is [-90 &hellip; 90]&deg;. This parameter is mandatory and has no
         * default value.
         *
         * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
         * descriptor(String)}</code> instead.
         */
        @Deprecated
        public static final ParameterDescriptor<Double> LAT_OF_2ND_POINT = Identifiers.LAT_OF_2ND_POINT;

        /**
         * The operation parameter descriptor for the {@code longitudeOf2ndPoint} parameter value.
         * Valid values range is [-180 &hellip; 180]&deg;. This parameter is mandatory and has no
         * default value.
         *
         * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
         * descriptor(String)}</code> instead.
         */
        @Deprecated
        public static final ParameterDescriptor<Double> LONG_OF_2ND_POINT = Identifiers.LONG_OF_2ND_POINT;

        /**
         * The group of all parameters expected by this coordinate operation.
         * The following table lists the operation names and the parameters recognized by Geotk:
         * <p>
         * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
         * <table class="geotk" border="1">
         *   <tr><th colspan="2">
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Hotine_Oblique_Mercator_Two_Point_Center}</td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright">{@code Geotk}:</td><td class="onleft">{@code Oblique Mercator projection}</td></tr>
         *     </table>
         *   </th></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Semi_Major}</td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Semi_Minor}</td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code Geotk}:</td><td class="onleft">{@code roll_longitude}</td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Boolean}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
         *       <tr><td><b>Default value:</b></td><td>false</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Latitude_Of_1st_Point}</td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[-90 … 90]°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Longitude_Of_1st_Point}</td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[-180 … 180]°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Latitude_Of_2nd_Point}</td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[-90 … 90]°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Longitude_Of_2nd_Point}</td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[-180 … 180]°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Latitude_Of_Center}</td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[-90 … 90]°</td></tr>
         *       <tr><td><b>Default value:</b></td><td>0°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Scale_Factor}</td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[0…∞)</td></tr>
         *       <tr><td><b>Default value:</b></td><td>1</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code False_Easting}</td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
         *       <tr><td><b>Default value:</b></td><td>0 metres</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code False_Northing}</td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
         *       <tr><td><b>Default value:</b></td><td>0 metres</td></tr>
         *     </table>
         *   </td></tr>
         * </table>
         */
        @SuppressWarnings("hiding")
        public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.ESRI, "Hotine_Oblique_Mercator_Two_Point_Center"),
                sameNameAs(Citations.GEOTOOLKIT, ObliqueMercator.PARAMETERS)
            }, new Citation[] { // Authorities to exclude from the parameter descriptors.
                Citations.EPSG, Citations.OGC, Citations.NETCDF, Citations.GEOTIFF, Citations.PROJ4
            }, new ParameterDescriptor<?>[] {
                SEMI_MAJOR, SEMI_MINOR, ROLL_LONGITUDE,
                LAT_OF_1ST_POINT,    LONG_OF_1ST_POINT,
                LAT_OF_2ND_POINT,    LONG_OF_2ND_POINT,
                LATITUDE_OF_CENTRE,  SCALE_FACTOR,
                FALSE_EASTING,       FALSE_NORTHING
            });

        /**
         * Constructs a new provider.
         */
        public TwoPoint() {
            super(PARAMETERS);
        }
    }
}

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
import org.opengis.referencing.operation.ConicProjection;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.projection.LambertConformal;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Lambert Conic Conformal (2SP)</cite>" projection (EPSG:9802).
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/lambert_conic_conformal_2sp.html">Lambert
 * Conic Conformal 2SP on RemoteSensing.org</A>. The math transform implementations instantiated by
 * this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.LambertConformal}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @version 3.20
 *
 * @since 2.2
 * @module
 */
@Immutable
public class LambertConformal2SP extends MapProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 3240860802816724947L;

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
    public static final ParameterDescriptor<Double> CENTRAL_MERIDIAN =
            Identifiers.CENTRAL_MERIDIAN.select(null,
                "Longitude of false origin",     // EPSG
                "central_meridian",              // OGC
                "Central_Meridian",              // ESRI
                "longitude_of_central_meridian", // NetCDF
                "FalseOriginLong");              // GeoTIFF

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
    public static final ParameterDescriptor<Double> LATITUDE_OF_ORIGIN =
            Identifiers.LATITUDE_OF_ORIGIN.select(null,
                "Latitude of false origin",  // EPSG
                "latitude_of_origin",        // OGC
                "Latitude_Of_Origin",        // ESRI
                "FalseOriginLat");           // GeoTIFF

    /**
     * The operation parameter descriptor for the first {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#standardParallels
     * standard parallel} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">optional</a> - if omitted,
     * it takes the same value than the one given for {@link #LATITUDE_OF_ORIGIN}.
     * Valid values range is [-90 &hellip; 90]&deg;.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> STANDARD_PARALLEL_1 =
            Identifiers.STANDARD_PARALLEL_1.select(null,
                "Latitude of 1st standard parallel",  // EPSG
                "standard_parallel_1",                // OGC
                "Standard_Parallel_1");               // ESRI

    /**
     * The operation parameter descriptor for the second {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#standardParallels
     * standard parallel} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">optional</a> - if omitted,
     * it takes the same value than the one given for {@link #STANDARD_PARALLEL_1}.
     * Valid values range is [-90 &hellip; 90]&deg;.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> STANDARD_PARALLEL_2 =
            Identifiers.STANDARD_PARALLEL_2.select(null,
                    "Latitude of 2nd standard parallel");

    /**
     * The ESRI operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor
     * scale factor} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">optional</a>, because not
     * defined by EPSG. Valid values range is (0 &hellip; &infin;) and default value is 1.
     *
     * @since 3.20
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> SCALE_FACTOR =
            Identifiers.SCALE_FACTOR.select(false, null, new Citation[] {
                Citations.EPSG, Citations.OGC, Citations.NETCDF, Citations.GEOTIFF, Citations.PROJ4
            }, null);

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
    public static final ParameterDescriptor<Double> FALSE_EASTING =
            Identifiers.FALSE_EASTING.select(null,
                "Easting at false origin",  // EPSG
                "FalseOriginEasting");      // GeoTIFF

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
    public static final ParameterDescriptor<Double> FALSE_NORTHING =
            Identifiers.FALSE_NORTHING.select(null,
                "Northing at false origin", // EPSG
                "FalseOriginNorthing");     // GeoTIFF

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk:
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table bgcolor="#F4F8FF" border="1" cellspacing="0" cellpadding="6">
     *   <tr bgcolor="#B9DCFF" valign="top"><td colspan="2">
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>Lambert_Conformal_Conic_2SP</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Lambert Conic Conformal (2SP)</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Lambert_Conformal_Conic</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>NetCDF:</code></td><td><code>LambertConformal</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>CT_LambertConfConic_2SP</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>CT_LambertConfConic</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>lcc</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>Geotk:</code></td><td><code>Lambert conformal conic projection</code></td></tr>
     *       <tr><th align="left">Identifier:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>9802</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>9</code></td></tr>
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
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>central_meridian</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Longitude of false origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Central_Meridian</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>NetCDF:</code></td><td><code>longitude_of_central_meridian</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>FalseOriginLong</code></td></tr>
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
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>latitude_of_origin</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Latitude of false origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Latitude_Of_Origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>NetCDF:</code></td><td><code>latitude_of_projection_origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>FalseOriginLat</code></td></tr>
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
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>standard_parallel_1</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Latitude of 1st standard parallel</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Standard_Parallel_1</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>NetCDF:</code></td><td><code>standard_parallel[1]</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>StdParallel1</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>lat_1</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-90 … 90]°</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td><var>latitude of origin</var></td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>standard_parallel_2</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Latitude of 2nd standard parallel</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Standard_Parallel_2</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>NetCDF:</code></td><td><code>standard_parallel[2]</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>StdParallel2</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>lat_2</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-90 … 90]°</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td><var>standard parallel 1</var></td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Scale_Factor</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[0…∞)</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>1</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>false_easting</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Easting at false origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>False_Easting</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>NetCDF:</code></td><td><code>false_easting</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>FalseOriginEasting</code></td></tr>
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
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Northing at false origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>False_Northing</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>NetCDF:</code></td><td><code>false_northing</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>FalseOriginNorthing</code></td></tr>
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
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,     "Lambert_Conformal_Conic_2SP"),
            new NamedIdentifier(Citations.EPSG,    "Lambert Conic Conformal (2SP)"),
            new IdentifierCode (Citations.EPSG,     9802),
            new NamedIdentifier(Citations.ESRI,    "Lambert_Conformal_Conic"),
            new NamedIdentifier(Citations.NETCDF,  "LambertConformal"),
            new NamedIdentifier(Citations.GEOTIFF, "CT_LambertConfConic_2SP"),
            new NamedIdentifier(Citations.GEOTIFF, "CT_LambertConfConic"),
            new IdentifierCode (Citations.GEOTIFF,  9), // The same code is used for 1SP.
                     sameNameAs(Citations.PROJ4,      LambertConformal1SP.PARAMETERS),
                     sameNameAs(Citations.GEOTOOLKIT, LambertConformal1SP.PARAMETERS)
        }, null, new ParameterDescriptor<?>[] {
            SEMI_MAJOR,          SEMI_MINOR,
            ROLL_LONGITUDE,
            CENTRAL_MERIDIAN,    LATITUDE_OF_ORIGIN,
            STANDARD_PARALLEL_1, STANDARD_PARALLEL_2, SCALE_FACTOR,
            FALSE_EASTING,       FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public LambertConformal2SP() {
        super(PARAMETERS);
    }

    /**
     * Constructs a new provider with the given descriptor.
     */
    LambertConformal2SP(final ParameterDescriptorGroup descriptor) {
        super(descriptor);
    }

    /**
     * Returns the operation type for this map projection.
     */
    @Override
    public Class<ConicProjection> getOperationType() {
        return ConicProjection.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MathTransform2D createMathTransform(ParameterValueGroup values) {
        return LambertConformal.create(getParameters(), values);
    }

    /**
     * The provider for "<cite>Lambert Conic Conformal (2SP Belgium)</cite>" projection (EPSG:9803).
     * The programmatic names and parameters are enumerated at
     * <A HREF="http://www.remotesensing.org/geotiff/proj_list/lambert_conic_conformal_2sp_belgium.html">Lambert
     * Conic Conformal 2SP (Belgium) on RemoteSensing.org</A>. The math transform implementations
     * instantiated by this provider may be any of the following classes:
     * <p>
     * <ul>
     *   <li>{@link org.geotoolkit.referencing.operation.projection.LambertConformal}</li>
     * </ul>
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @author Rueben Schulz (UBC)
     * @version 3.20
     *
     * @since 2.2
     * @module
     */
    @Immutable
    public static class Belgium extends LambertConformal2SP {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -6388030784088639876L;

        /**
         * The group of all parameters expected by this coordinate operation.
         * The following table lists the operation names and the parameters recognized by Geotk:
         * <p>
         * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
         * <table bgcolor="#F4F8FF" border="1" cellspacing="0" cellpadding="6">
         *   <tr bgcolor="#B9DCFF" valign="top"><td colspan="2">
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>Lambert_Conformal_Conic_2SP_Belgium</code></td></tr>
         *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Lambert_Conformal_Conic_2SP_Belgium</code></td></tr>
         *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Lambert Conic Conformal (2SP Belgium)</code></td></tr>
         *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>Geotk:</code></td><td><code>Lambert conformal conic projection</code></td></tr>
         *       <tr><th align="left">Identifier:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>9803</code></td></tr>
         *     </table>
         *   </td></tr>
         *   <tr valign="top"><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>semi_major</code></td></tr>
         *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Semi-major axis</code></td></tr>
         *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Semi_Major</code></td></tr>
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
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>central_meridian</code></td></tr>
         *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Longitude of false origin</code></td></tr>
         *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Central_Meridian</code></td></tr>
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
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>latitude_of_origin</code></td></tr>
         *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Latitude of false origin</code></td></tr>
         *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Latitude_Of_Origin</code></td></tr>
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
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>standard_parallel_1</code></td></tr>
         *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Latitude of 1st standard parallel</code></td></tr>
         *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Standard_Parallel_1</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
         *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
         *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-90 … 90]°</td></tr>
         *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td><var>latitude of origin</var></td></tr>
         *     </table>
         *   </td></tr>
         *   <tr valign="top"><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>standard_parallel_2</code></td></tr>
         *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Latitude of 2nd standard parallel</code></td></tr>
         *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Standard_Parallel_2</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
         *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
         *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-90 … 90]°</td></tr>
         *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td><var>standard parallel 1</var></td></tr>
         *     </table>
         *   </td></tr>
         *   <tr valign="top"><td>
         *     <table border="0" cellspacing="0" cellpadding="0">
         *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>false_easting</code></td></tr>
         *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Easting at false origin</code></td></tr>
         *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>False_Easting</code></td></tr>
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
         *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Northing at false origin</code></td></tr>
         *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>False_Northing</code></td></tr>
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
                /*
                 * IMPORTANT: Do not put any name that could be confused with the 1SP or
                 * 2SP cases below, except for the Citations.GEOTOOLKIT authority which is
                 * ignored. The LambertConformal constructor relies on those names for
                 * distinguish the kind of projection being created.
                 */
                new NamedIdentifier(Citations.OGC,  "Lambert_Conformal_Conic_2SP_Belgium"),
                new NamedIdentifier(Citations.ESRI, "Lambert_Conformal_Conic_2SP_Belgium"),
                new NamedIdentifier(Citations.EPSG, "Lambert Conic Conformal (2SP Belgium)"),
                new IdentifierCode (Citations.EPSG,  9803),
                        sameNameAs(Citations.GEOTOOLKIT, LambertConformal2SP.PARAMETERS)
            }, new Citation[] { // Authorities to exclude from the parameter descriptors.
                Citations.NETCDF, Citations.GEOTIFF, Citations.PROJ4
            }, new ParameterDescriptor<?>[] {
                SEMI_MAJOR,          SEMI_MINOR,
                ROLL_LONGITUDE,
                CENTRAL_MERIDIAN,    LATITUDE_OF_ORIGIN,
                STANDARD_PARALLEL_1, STANDARD_PARALLEL_2,
                FALSE_EASTING,       FALSE_NORTHING
            });

        /**
         * Constructs a new provider.
         */
        public Belgium() {
            super(PARAMETERS);
        }
    }
}

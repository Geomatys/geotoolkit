/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Lambert Azimuthal Equal Area</cite>" projection (EPSG:9820, EPSG:1027,
 * <del>EPSG:9821</del>).
 *
 * {@note EPSG defines two codes for this projection, 1027 being the spherical case and 9820 the
 *        ellipsoidal case. However the formulas are the same in both cases. Consequently they are
 *        implemented in Geotk by the same class.}
 *
 * {@note EPSG:9820 and 1027 are the current codes, while EPSG:9821 is a deprecated code. The new
 *        and deprecated definitions differ only by their names. In the Geotk implementation, both
 *        current and legacy definitions are known, but the legacy names are marked as deprecated.}
 *
 * The math transform implementations instantiated by this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.LambertAzimuthalEqualArea}</li>
 * </ul>
 *
 * <!-- PARAMETERS LambertAzimuthalEqualArea -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <p><b>Operation name:</b> Lambert_Azimuthal_Equal_Area</p>
 * <table bgcolor="#F4F8FF" cellspacing="0" cellpadding="0">
 *   <tr bgcolor="#B9DCFF"><th>Parameter Name</th><th>Default value</th></tr>
 *   <tr><td>semi_major</td><td>&nbsp;&nbsp;</td></tr>
 *   <tr><td>semi_minor</td><td>&nbsp;&nbsp;</td></tr>
 *   <tr><td>roll_longitude</td><td>&nbsp;&nbsp;false</td></tr>
 *   <tr><td>latitude_of_center</td><td>&nbsp;&nbsp;0°</td></tr>
 *   <tr><td>longitude_of_center</td><td>&nbsp;&nbsp;0°</td></tr>
 *   <tr><td>false_easting</td><td>&nbsp;&nbsp;0 metres</td></tr>
 *   <tr><td>false_northing</td><td>&nbsp;&nbsp;0 metres</td></tr>
 * </table>
 * <!-- END OF PARAMETERS -->
 *
 * @author Beate Stollberg
 * @author Martin Desruisseaux (Geomatys, Geomatys)
 * @version 3.20
 *
 * @see <A HREF="http://www.remotesensing.org/geotiff/proj_list/lambert_azimuthal_equal_area.html">Lambert Azimuthal Equal Area on RemoteSensing.org</A>
 *
 * @since 2.4
 * @module
 */
@Immutable
public class LambertAzimuthalEqualArea extends MapProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 3877793025552244132L;

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
    public static final ParameterDescriptor<Double> LONGITUDE_OF_CENTRE =
            Identifiers.CENTRAL_MERIDIAN.select(null, null, null, new String[] {
                "Spherical longitude of origin"}, // EPSG (deprecated - was used by EPSG:9821 only)
                "Longitude of natural origin",    // EPSG
                "longitude_of_center",            // OGC
                "Central_Meridian",               // ESRI
                "longitude_of_projection_origin", // NetCDF
                "ProjCenterLong");                // GeoTIFF

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
    public static final ParameterDescriptor<Double> LATITUDE_OF_CENTRE =
            Identifiers.LATITUDE_OF_ORIGIN.select(null, null, null, new String[] {
                "Spherical latitude of origin"},  // EPSG (deprecated - was used by EPSG:9821 only)
                "Latitude of natural origin",     // EPSG
                "latitude_of_center",             // OGC
                "Latitude_Of_Origin",             // ESRI
                "ProjCenterLat");                 // GeoTIFF

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
    public static final ParameterDescriptor<Double> FALSE_EASTING = Mercator2SP.FALSE_EASTING;

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
    public static final ParameterDescriptor<Double> FALSE_NORTHING = Mercator2SP.FALSE_NORTHING;

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk:
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table bgcolor="#F4F8FF" border="1" cellspacing="0" cellpadding="6">
     *   <tr bgcolor="#B9DCFF" valign="top"><td colspan="2">
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>Lambert_Azimuthal_Equal_Area</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Lambert Azimuthal Equal Area</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Lambert Azimuthal Equal Area (Spherical)</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Lambert_Azimuthal_Equal_Area</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>NetCDF:</code></td><td><code>LambertAzimuthalEqualArea</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>CT_LambertAzimEqualArea</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>laea</code></td></tr>
     *       <tr><th align="left">Identifier:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>9820</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>1027</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code><del>9821</del></code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>10</code></td></tr>
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
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>latitude_of_center</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Latitude of natural origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Latitude_Of_Origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>NetCDF:</code></td><td><code>latitude_of_projection_origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>ProjCenterLat</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>lat_0</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code><del>Spherical latitude of origin</del></code></td></tr>
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
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>longitude_of_center</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Longitude of natural origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Central_Meridian</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>NetCDF:</code></td><td><code>longitude_of_projection_origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>ProjCenterLong</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>lon_0</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code><del>Spherical longitude of origin</del></code></td></tr>
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
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>false_easting</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>False easting</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>False_Easting</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>NetCDF:</code></td><td><code>false_easting</code></td></tr>
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
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>NetCDF:</code></td><td><code>false_northing</code></td></tr>
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
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,     "Lambert_Azimuthal_Equal_Area"),
            new NamedIdentifier(Citations.EPSG,    "Lambert Azimuthal Equal Area"),
            new NamedIdentifier(Citations.EPSG,    "Lambert Azimuthal Equal Area (Spherical)"),
            new IdentifierCode (Citations.EPSG,     9820),
            new IdentifierCode (Citations.EPSG,     1027),
            new IdentifierCode (Citations.EPSG,     9821, 1027), // Legacy (deprecated) code.
            new NamedIdentifier(Citations.ESRI,    "Lambert_Azimuthal_Equal_Area"),
            new NamedIdentifier(Citations.NETCDF,  "LambertAzimuthalEqualArea"),
            new NamedIdentifier(Citations.GEOTIFF, "CT_LambertAzimEqualArea"),
            new IdentifierCode (Citations.GEOTIFF,  10),
            new NamedIdentifier(Citations.PROJ4,    "laea"),
        }, null, new ParameterDescriptor<?>[] {
                SEMI_MAJOR,         SEMI_MINOR, ROLL_LONGITUDE,
                LATITUDE_OF_CENTRE, LONGITUDE_OF_CENTRE,
                FALSE_EASTING,      FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public LambertAzimuthalEqualArea() {
        super(PARAMETERS);
    }

    /**
     * Constructs a new provider for the given parameters.
     */
    LambertAzimuthalEqualArea(ParameterDescriptorGroup parameters) {
        super(parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MathTransform2D createMathTransform(ParameterValueGroup values) {
        return org.geotoolkit.referencing.operation.projection.LambertAzimuthalEqualArea.create(getParameters(), values);
    }
}

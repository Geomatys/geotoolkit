/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Oblique Stereographic</cite>" projection using EPSG equations (EPSG:9809).
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/oblique_stereographic.html">Oblique
 * Stereographic on RemoteSensing.org</A>. The math transform implementations instantiated by
 * this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.ObliqueStereographic}</li>
 * </ul>
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 2.4
 * @module
 */
@Immutable
public class ObliqueStereographic extends Stereographic {
    /**
     * For compatibility with different versions during deserialization.
     */
    private static final long serialVersionUID = 6505988910141381354L;

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
    public static final ParameterDescriptor<Double> CENTRAL_MERIDIAN;

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
    public static final ParameterDescriptor<Double> LATITUDE_OF_ORIGIN;

    /**
     * The parameters group.
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table bgcolor="#F4F8FF" border="1" cellspacing="0" cellpadding="6">
     *   <tr bgcolor="#B9DCFF" valign="top"><td colspan="2">
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>Oblique_Stereographic</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Oblique Stereographic</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Roussilhe</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Double_Stereographic</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>CT_ObliqueStereographic</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>sterea</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>Geotk:</code></td><td><code>Stereographic projection</code></td></tr>
     *       <tr><th align="left">Identifier:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>9809</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>16</code></td></tr>
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
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Longitude of natural origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Central_Meridian</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>NatOriginLong</code></td></tr>
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
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Latitude of natural origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Latitude_Of_Origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>NatOriginLat</code></td></tr>
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
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>scale_factor</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Scale factor at natural origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Scale_Factor</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>ScaleAtNatOrigin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>k</code></td></tr>
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
    public static final ParameterDescriptorGroup PARAMETERS;
    static {
        final Citation[] excludes = new Citation[] {Citations.NETCDF};
        CENTRAL_MERIDIAN   = CassiniSoldner.CENTRAL_MERIDIAN;
        LATITUDE_OF_ORIGIN = CassiniSoldner.LATITUDE_OF_ORIGIN;

        PARAMETERS = Identifiers.createDescriptorGroup(new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,     "Oblique_Stereographic"),
            new NamedIdentifier(Citations.EPSG,    "Oblique Stereographic"),
            new NamedIdentifier(Citations.EPSG,    "Roussilhe"),
            new IdentifierCode (Citations.EPSG,     9809),
            new NamedIdentifier(Citations.ESRI,    "Double_Stereographic"),
            new NamedIdentifier(Citations.GEOTIFF, "CT_ObliqueStereographic"),
            new IdentifierCode (Citations.GEOTIFF,  16),
            new NamedIdentifier(Citations.PROJ4,   "sterea"),
            sameNameAs(Citations.GEOTOOLKIT, Stereographic.PARAMETERS)
        }, null, new ParameterDescriptor<?>[] {
            SEMI_MAJOR, SEMI_MINOR, ROLL_LONGITUDE,
            CENTRAL_MERIDIAN, LATITUDE_OF_ORIGIN,
            Orthographic.SCALE_FACTOR,
            EquidistantCylindrical.FALSE_EASTING,
            EquidistantCylindrical.FALSE_NORTHING
        });
    }

    /**
     * Constructs a new provider.
     */
    public ObliqueStereographic() {
        super(PARAMETERS);
    }
}

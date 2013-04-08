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

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Oblique Stereographic</cite>" projection using EPSG equations (EPSG:9809).
 * The math transform implementations instantiated by this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.ObliqueStereographic}</li>
 * </ul>
 *
 * <!-- PARAMETERS ObliqueStereographic -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code Oblique_Stereographic}
 * <br><b>Area of use:</b> <font size="-1">(union of CRS domains of validity in EPSG database)</font></p>
 * <blockquote><table class="compact">
 *   <tr><td><b>in latitudes:</b></td><td class="onright">32°18.6′N</td><td>to</td><td class="onright">54°52.2′N</td></tr>
 *   <tr><td><b>in longitudes:</b></td><td class="onright">69°03.0′W</td><td>to</td><td class="onright">42°22.8′E</td></tr>
 * </table></blockquote>
 * <table class="geotk">
 *   <tr><th>Parameter name</th><th>Default value</th></tr>
 *   <tr><td>{@code semi_major}</td><td></td></tr>
 *   <tr><td>{@code semi_minor}</td><td></td></tr>
 *   <tr><td>{@code roll_longitude}</td><td>false</td></tr>
 *   <tr><td>{@code central_meridian}</td><td>0°</td></tr>
 *   <tr><td>{@code latitude_of_origin}</td><td>0°</td></tr>
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
 * @see <A HREF="http://www.remotesensing.org/geotiff/proj_list/oblique_stereographic.html">Oblique Stereographic on RemoteSensing.org</A>
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
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
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk:
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>Oblique_Stereographic</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Oblique Stereographic</code></td></tr>
     *       <tr><td></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Roussilhe</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Double_Stereographic</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>CT_ObliqueStereographic</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>sterea</code></td></tr>
     *       <tr><td></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>Stereographic projection</code></td></tr>
     *       <tr><td><b>Identifier:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>9809</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>16</code></td></tr>
     *     </table>
     *   </th></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>semi_major</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Semi-major axis</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Semi_Major</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>SemiMajor</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>a</code></td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>semi_minor</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Semi-minor axis</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Semi_Minor</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>SemiMinor</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>b</code></td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>roll_longitude</code></td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>central_meridian</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Longitude of natural origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Central_Meridian</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>NatOriginLong</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>lon_0</code></td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>latitude_of_origin</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Latitude of natural origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Latitude_Of_Origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>NatOriginLat</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>lat_0</code></td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>scale_factor</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Scale factor at natural origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Scale_Factor</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>ScaleAtNatOrigin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>k</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞)</td></tr>
     *       <tr><td><b>Default value:</b></td><td>1</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>false_easting</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>False easting</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>False_Easting</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>FalseEasting</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>x_0</code></td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>false_northing</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>False northing</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>False_Northing</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>FalseNorthing</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>y_0</code></td></tr>
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
    public static final ParameterDescriptorGroup PARAMETERS;
    static {
        CENTRAL_MERIDIAN   = CassiniSoldner.CENTRAL_MERIDIAN;
        LATITUDE_OF_ORIGIN = CassiniSoldner.LATITUDE_OF_ORIGIN;

        PARAMETERS = UniversalParameters.createDescriptorGroup(new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,     "Oblique_Stereographic"),
            new NamedIdentifier(Citations.EPSG,    "Oblique Stereographic"),
            new NamedIdentifier(Citations.EPSG,    "Roussilhe"),
            new IdentifierCode (Citations.EPSG,     9809),
            new NamedIdentifier(Citations.ESRI,    "Double_Stereographic"),
            new NamedIdentifier(Citations.GEOTIFF, "CT_ObliqueStereographic"),
            new IdentifierCode (Citations.GEOTIFF,  16),
            new NamedIdentifier(Citations.PROJ4,   "sterea"),
            new NamedIdentifier(Citations.S57,     "Oblique stereographic"),
            new NamedIdentifier(Citations.S57,     "OST"),
            new IdentifierCode (Citations.S57,      14),
            sameNameAs(Citations.GEOTOOLKIT, Stereographic.PARAMETERS)
        }, null, new ParameterDescriptor<?>[] {
            sameParameterAs(EquidistantCylindrical.PARAMETERS, "semi_major"),
            sameParameterAs(EquidistantCylindrical.PARAMETERS, "semi_minor"),
            ROLL_LONGITUDE,
            CENTRAL_MERIDIAN, LATITUDE_OF_ORIGIN,
            Orthographic.SCALE_FACTOR,
            EquidistantCylindrical.FALSE_EASTING,
            EquidistantCylindrical.FALSE_NORTHING
        }, MapProjectionDescriptor.ADD_EARTH_RADIUS);
    }

    /**
     * Constructs a new provider.
     */
    public ObliqueStereographic() {
        super(PARAMETERS);
    }
}

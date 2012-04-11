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
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Polar Stereographic (Variant A)</cite>" projection (EPSG:9810).
 * The math transform implementations instantiated by this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.PolarStereographic}</li>
 * </ul>
 *
 * <!-- PARAMETERS PolarStereographic -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code Polar_Stereographic}</p>
 * <table class="geotk">
 *   <tr><th>Parameter Name</th><th>Default value</th></tr>
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
 * @see <A HREF="http://www.remotesensing.org/geotiff/proj_list/polar_stereographic.html">Polar Stereographic on RemoteSensing.org</A>
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.4
 * @module
 */
@Immutable
public class PolarStereographic extends Stereographic {
    /**
     * For compatibility with different versions during deserialization.
     */
    private static final long serialVersionUID = 9124091259039220308L;

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
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code Polar_Stereographic}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Polar Stereographic (variant A)}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code CT_PolarStereographic}</td></tr>
     *       <tr><td></td><td class="onright">{@code PROJ4}:</td><td class="onleft">{@code stere}</td></tr>
     *       <tr><td></td><td class="onright">{@code Geotk}:</td><td class="onleft">{@code Stereographic projection}</td></tr>
     *       <tr><td><b>Identifier:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code 9810}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code 15}</td></tr>
     *     </table>
     *   </th></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code semi_major}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Semi-major axis}</td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code central_meridian}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Longitude of natural origin}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code StraightVertPoleLong}</td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code latitude_of_origin}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Latitude of natural origin}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code NatOriginLat}</td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code scale_factor}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Scale factor at natural origin}</td></tr>
     *       <tr><td></td><td class="onright">{@code GeoTIFF}:</td><td class="onleft">{@code ScaleAtNatOrigin}</td></tr>
     *       <tr><td></td><td class="onright">{@code PROJ4}:</td><td class="onleft">{@code k}</td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code false_easting}</td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code False easting}</td></tr>
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
     *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code False northing}</td></tr>
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
    @SuppressWarnings("hiding")
    public static final ParameterDescriptorGroup PARAMETERS;
    static {
        final Citation[] excludes = {Citations.ESRI, Citations.NETCDF};
        CENTRAL_MERIDIAN = Identifiers.CENTRAL_MERIDIAN.select(excludes,
                "Longitude of natural origin",  // EPSG
                "central_meridian",             // OGC
                "StraightVertPoleLong");        // GeoTIFF
        LATITUDE_OF_ORIGIN = Mercator1SP.LATITUDE_OF_ORIGIN;

        PARAMETERS = Identifiers.createDescriptorGroup(
            new ReferenceIdentifier[] {
                new NamedIdentifier(Citations.OGC,      "Polar_Stereographic"),
                new NamedIdentifier(Citations.EPSG,     "Polar Stereographic (variant A)"),
                new IdentifierCode (Citations.EPSG,      9810),
                new NamedIdentifier(Citations.GEOTIFF,  "CT_PolarStereographic"),
                new IdentifierCode (Citations.GEOTIFF,   15),
                sameNameAs(Citations.PROJ4,      Stereographic.PARAMETERS),
                sameNameAs(Citations.GEOTOOLKIT, Stereographic.PARAMETERS)
            }, excludes, new ParameterDescriptor<?>[] {
                sameParameterAs(Mercator1SP.PARAMETERS, "semi_major"),
                sameParameterAs(Mercator1SP.PARAMETERS, "semi_minor"),
                ROLL_LONGITUDE,
                CENTRAL_MERIDIAN, LATITUDE_OF_ORIGIN, SCALE_FACTOR,
                Mercator1SP.FALSE_EASTING,
                Mercator1SP.FALSE_NORTHING
            });
    }

    /**
     * Constructs a new provider.
     */
    public PolarStereographic() {
        super(PARAMETERS);
    }

    /**
     * Constructs a provider from a set of parameters.
     *
     * @param parameters The set of parameters (never {@code null}).
     */
    PolarStereographic(final ParameterDescriptorGroup parameters) {
        super(parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MathTransform2D createMathTransform(ParameterValueGroup values) {
        return org.geotoolkit.referencing.operation.projection.PolarStereographic.create(getParameters(), values);
    }




    /**
     * The provider for "<cite>Polar Stereographic (Variant B)</cite>" projection (EPSG:9829).
     * This provider includes a {@code "Standard_Parallel_1"} parameter and determines
     * the hemisphere of the projection from the {@code Standard_Parallel_1} value.
     *
     * <!-- PARAMETERS VariantB -->
     * <p>The following table summarizes the parameters recognized by this provider.
     * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
     * <blockquote><p><b>Operation name:</b> {@code Polar Stereographic (variant B)}</p>
     * <table class="geotk">
     *   <tr><th>Parameter Name</th><th>Default value</th></tr>
     *   <tr><td>{@code semi_major}</td><td></td></tr>
     *   <tr><td>{@code semi_minor}</td><td></td></tr>
     *   <tr><td>{@code roll_longitude}</td><td>false</td></tr>
     *   <tr><td>{@code central_meridian}</td><td>0°</td></tr>
     *   <tr><td>{@code standard_parallel_1}</td><td>90°</td></tr>
     *   <tr><td>{@code false_easting}</td><td>0 metres</td></tr>
     *   <tr><td>{@code false_northing}</td><td>0 metres</td></tr>
     * </table></blockquote>
     * <!-- END OF PARAMETERS -->
     *
     * @author Rueben Schulz (UBC)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.20
     *
     * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
     *
     * @since 2.4
     * @module
     */
    @Immutable
    public static class VariantB extends PolarStereographic {
        /**
         * For compatibility with different versions during deserialization.
         */
        private static final long serialVersionUID = 5188231050523249971L;

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
         * The operation parameter descriptor for the {@code standardParallel} parameter value.
         * Valid values range is from [-90 &hellip; 90]&deg; and default value is 90&deg;N.
         *
         * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
         * descriptor(String)}</code> instead.
         */
        @Deprecated
        public static final ParameterDescriptor<Double> STANDARD_PARALLEL;

        /**
         * The group of all parameters expected by this coordinate operation.
         * The following table lists the operation names and the parameters recognized by Geotk:
         * <p>
         * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
         * <table class="geotk" border="1">
         *   <tr><th colspan="2">
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Polar Stereographic (variant B)}</td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright">{@code Geotk}:</td><td class="onleft">{@code Stereographic projection}</td></tr>
         *       <tr><td><b>Identifier:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code 9829}</td></tr>
         *     </table>
         *   </th></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code semi_major}</td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Semi-major axis}</td></tr>
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
         *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code central_meridian}</td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Longitude of origin}</td></tr>
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
         *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code standard_parallel_1}</td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code Latitude of standard parallel}</td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[-90 … 90]°</td></tr>
         *       <tr><td><b>Default value:</b></td><td>90°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code OGC}:</td><td class="onleft">{@code false_easting}</td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code False easting}</td></tr>
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
         *       <tr><td><b>Alias:</b></td><td class="onright">{@code EPSG}:</td><td class="onleft">{@code False northing}</td></tr>
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
            final Citation[] excludes = {
                // While I'm not sure that OGC parameter names are defined for this
                // projection, some WKT expect them. We could exclude the OGC param
                // if we had ESRI param (because there are often the same except for
                // the case), but EPSG parameter names are too different.
                Citations.ESRI, Citations.NETCDF, Citations.GEOTIFF, Citations.PROJ4
            };
            CENTRAL_MERIDIAN = Identifiers.CENTRAL_MERIDIAN.select(excludes,
                    "Longitude of origin",            // EPSG
                    "central_meridian");              // OGC
            STANDARD_PARALLEL = Identifiers.STANDARD_PARALLEL_1.select(false, 90.0, excludes, null,
                    "Latitude of standard parallel",  // EPSG
                    "standard_parallel_1");           // OGC

            PARAMETERS = Identifiers.createDescriptorGroup(
                new ReferenceIdentifier[] {
                    new NamedIdentifier(Citations.EPSG, "Polar Stereographic (variant B)"),
                    new IdentifierCode (Citations.EPSG,  9829),
                    sameNameAs(Citations.GEOTOOLKIT, PolarStereographic.PARAMETERS)
                }, excludes, new ParameterDescriptor<?>[] {
                    sameParameterAs(PseudoMercator.PARAMETERS, "semi_major"),
                    sameParameterAs(PseudoMercator.PARAMETERS, "semi_minor"),
                    ROLL_LONGITUDE,
                    CENTRAL_MERIDIAN, STANDARD_PARALLEL,
                    sameParameterAs(PseudoMercator.PARAMETERS, "false_easting"),
                    sameParameterAs(PseudoMercator.PARAMETERS, "false_northing"),
                });
        }

        /**
         * Constructs a new provider.
         */
        public VariantB() {
            super(PARAMETERS);
        }
    }




    /**
     * The provider for "<cite>North Polar Stereographic</cite>" projection. This provider sets the
     * <cite>latitude of origin</cite> parameter to 90&deg;N.
     *
     * <!-- PARAMETERS North -->
     * <p>The following table summarizes the parameters recognized by this provider.
     * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
     * <blockquote><p><b>Operation name:</b> {@code Stereographic_North_Pole}</p>
     * <table class="geotk">
     *   <tr><th>Parameter Name</th><th>Default value</th></tr>
     *   <tr><td>{@code Semi_Major}</td><td></td></tr>
     *   <tr><td>{@code Semi_Minor}</td><td></td></tr>
     *   <tr><td>{@code roll_longitude}</td><td>false</td></tr>
     *   <tr><td>{@code Central_Meridian}</td><td>0°</td></tr>
     *   <tr><td>{@code Standard_Parallel_1}</td><td>90°</td></tr>
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
     * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
     *
     * @since 2.4
     * @module
     */
    @Immutable
    public static class North extends PolarStereographic {
        /**
         * For compatibility with different versions during deserialization.
         */
        private static final long serialVersionUID = 657493908431273866L;

        /**
         * The operation parameter descriptor for the {@code standardParallel} parameter value.
         * Valid values range is from -90 to 90&deg;. The default value is 90&deg;N.
         *
         * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
         * descriptor(String)}</code> instead.
         */
        @Deprecated
        public static final ParameterDescriptor<Double> STANDARD_PARALLEL;

        /**
         * The group of all parameters expected by this coordinate operation.
         * The following table lists the operation names and the parameters recognized by Geotk:
         * <p>
         * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
         * <table class="geotk" border="1">
         *   <tr><th colspan="2">
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Stereographic_North_Pole}</td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright">{@code Geotk}:</td><td class="onleft">{@code Stereographic projection}</td></tr>
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
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Central_Meridian}</td></tr>
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
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Standard_Parallel_1}</td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[-90 … 90]°</td></tr>
         *       <tr><td><b>Default value:</b></td><td>90°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Scale_Factor}</td></tr>
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
        public static final ParameterDescriptorGroup PARAMETERS;
        static {
            final Citation[] excludes = {
                Citations.EPSG, Citations.OGC, Citations.NETCDF, Citations.GEOTIFF, Citations.PROJ4
            };
            STANDARD_PARALLEL = Identifiers.STANDARD_PARALLEL_1.select(false, 90.0, excludes, null,
                    "Standard_Parallel_1"); // ESRI

            PARAMETERS = Identifiers.createDescriptorGroup(
                new NamedIdentifier[] {
                    new NamedIdentifier(Citations.ESRI, "Stereographic_North_Pole"),
                    sameNameAs(Citations.GEOTOOLKIT, PolarStereographic.PARAMETERS)
                }, excludes, new ParameterDescriptor<?>[] {
                    sameParameterAs(ObliqueMercator.TwoPoint.PARAMETERS, "semi_major"),
                    sameParameterAs(ObliqueMercator.TwoPoint.PARAMETERS, "semi_minor"),
                    ROLL_LONGITUDE, Stereographic.CENTRAL_MERIDIAN, STANDARD_PARALLEL,
                    sameParameterAs(LambertConformal2SP     .PARAMETERS, "scale_factor"),
                    sameParameterAs(ObliqueMercator.TwoPoint.PARAMETERS, "false_easting"),
                    sameParameterAs(ObliqueMercator.TwoPoint.PARAMETERS, "false_northing"),
                });
        }

        /**
         * Constructs a new provider.
         */
        public North() {
            super(PARAMETERS);
        }
    }




    /**
     * The Provider for "<cite>South Polar Stereographic</cite>" projection. This provider sets the
     * <cite>latitude of origin</cite> parameter to 90&deg;S.
     *
     * <!-- PARAMETERS South -->
     * <p>The following table summarizes the parameters recognized by this provider.
     * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
     * <blockquote><p><b>Operation name:</b> {@code Stereographic_South_Pole}</p>
     * <table class="geotk">
     *   <tr><th>Parameter Name</th><th>Default value</th></tr>
     *   <tr><td>{@code Semi_Major}</td><td></td></tr>
     *   <tr><td>{@code Semi_Minor}</td><td></td></tr>
     *   <tr><td>{@code roll_longitude}</td><td>false</td></tr>
     *   <tr><td>{@code Central_Meridian}</td><td>0°</td></tr>
     *   <tr><td>{@code Standard_Parallel_1}</td><td>-90°</td></tr>
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
     * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
     *
     * @since 2.4
     * @module
     */
    @Immutable
    public static class South extends PolarStereographic {
        /**
         * For compatibility with different versions during deserialization.
         */
        private static final long serialVersionUID = 6537800238416448564L;

        /**
         * The operation parameter descriptor for the {@code standardParallel} parameter value.
         * Valid values range is from -90 to 90&deg;. The default value is 90&deg;S.
         *
         * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
         * descriptor(String)}</code> instead.
         */
        @Deprecated
        public static final ParameterDescriptor<Double> STANDARD_PARALLEL;

        /**
         * The group of all parameters expected by this coordinate operation.
         * The following table lists the operation names and the parameters recognized by Geotk:
         * <p>
         * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
         * <table class="geotk" border="1">
         *   <tr><th colspan="2">
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Stereographic_South_Pole}</td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright">{@code Geotk}:</td><td class="onleft">{@code Stereographic projection}</td></tr>
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
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Central_Meridian}</td></tr>
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
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Standard_Parallel_1}</td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[-90 … 90]°</td></tr>
         *       <tr><td><b>Default value:</b></td><td>-90°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright">{@code ESRI}:</td><td class="onleft">{@code Scale_Factor}</td></tr>
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
        public static final ParameterDescriptorGroup PARAMETERS;
        static {
            final Citation[] excludes = {
                Citations.EPSG, Citations.OGC, Citations.NETCDF, Citations.GEOTIFF, Citations.PROJ4
            };
            STANDARD_PARALLEL = Identifiers.STANDARD_PARALLEL_1.select(false, -90.0, excludes, null,
                "Standard_Parallel_1"); // ESRI
            PARAMETERS = Identifiers.createDescriptorGroup(
                new NamedIdentifier[] {
                    new NamedIdentifier(Citations.ESRI, "Stereographic_South_Pole"),
                    sameNameAs(Citations.GEOTOOLKIT, PolarStereographic.PARAMETERS)
                }, null, new ParameterDescriptor<?>[] {
                    sameParameterAs(North.PARAMETERS, "semi_major"),
                    sameParameterAs(North.PARAMETERS, "semi_minor"),
                    ROLL_LONGITUDE,
                    sameParameterAs(North.PARAMETERS, "central_meridian"),
                    STANDARD_PARALLEL,
                    sameParameterAs(North.PARAMETERS, "scale_factor"),
                    sameParameterAs(North.PARAMETERS, "false_easting"),
                    sameParameterAs(North.PARAMETERS, "false_northing"),
                });
        }

        /**
         * Constructs a new provider.
         */
        public South() {
            super(PARAMETERS);
        }
    }
}

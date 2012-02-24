/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.referencing.operation.ConicProjection;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Albers Equal Area</cite>" projection (EPSG:9822).
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/albers_equal_area_conic.html">Albers
 * Equal-Area Conic on RemoteSensing.org</A>. The math transform implementations instantiated by
 * this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.AlbersEqualArea}</li>
 * </ul>
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@Immutable
public class AlbersEqualArea extends MapProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -7489679528438418778L;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian
     * central meridian} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-180 &hellip; 180]&deg; and default value is 0&deg;.
     *
     * @todo According the <cite>remote-sensing</cite> web site, the OGC name for this parameter
     *       is <code>"longitude_of_center"</code>. However the <cite>spatial-reference</cite>
     *       web site said <code>"central_meridian"</code>, which was also the usage in GeoTools
     *       2.x and is preserved for now.
     */
    public static final ParameterDescriptor<Double> CENTRAL_MERIDIAN =
            Identifiers.CENTRAL_MERIDIAN.select(
                "central_meridian",             // OGC
                "Central_Meridian",             // ESRI
                "Longitude of false origin",    // EPSG
                "NatOriginLong");               // GeoTIFF

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#latitudeOfOrigin
     * latitude of origin} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">optional</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 0&deg;.
     *
     * @todo According the <cite>remote-sensing</cite> web site, the OGC name for this parameter
     *       is <code>"latitude_of_center"</code>. However the <cite>spatial-reference</cite>
     *       web site said <code>"latitude_of_origin"</code>, which was also the usage in GeoTools
     *       2.x and is preserved for now.
     */
    public static final ParameterDescriptor<Double> LATITUDE_OF_ORIGIN =
            Identifiers.LATITUDE_OF_ORIGIN.select(
                "latitude_of_origin",           // OGC
                "Latitude_Of_Origin",           // ESRI
                "Latitude of false origin",     // EPSG
                "NatOriginLat");                // GeoTIFF

    /**
     * The operation parameter descriptor for the first {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#standardParallels
     * standard parallel} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">optional</a> - if omitted,
     * it takes the same value than the one given for {@link #LATITUDE_OF_ORIGIN}.
     * Valid values range is [-90 &hellip; 90]&deg;.
     */
    public static final ParameterDescriptor<Double> STANDARD_PARALLEL_1 = LambertConformal2SP.STANDARD_PARALLEL_1;

    /**
     * The operation parameter descriptor for the second {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#standardParallels
     * standard parallel} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">optional</a> - if omitted,
     * it takes the same value than the one given for {@link #STANDARD_PARALLEL_1}.
     * Valid values range is [-90 &hellip; 90]&deg;.
     */
    public static final ParameterDescriptor<Double> STANDARD_PARALLEL_2 = LambertConformal2SP.STANDARD_PARALLEL_2;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseEasting
     * false easting} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 0 metre.
     */
    public static final ParameterDescriptor<Double> FALSE_EASTING =
            Identifiers.FALSE_EASTING.select(
                "Easting at false origin",  // EPSG
                "FalseEasting");            // GeoTIFF

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseNorthing
     * false northing} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 0 metre.
     */
    public static final ParameterDescriptor<Double> FALSE_NORTHING =
            Identifiers.FALSE_NORTHING.select(
                "Northing at false origin", // EPSG
                "FalseNorthing");           // GeoTIFF

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,     "Albers_Conic_Equal_Area"),
            new NamedIdentifier(Citations.EPSG,    "Albers Equal Area"),
            new IdentifierCode (Citations.EPSG,     9822),
            new NamedIdentifier(Citations.GEOTIFF, "CT_AlbersEqualArea"),
            new IdentifierCode (Citations.GEOTIFF,  11),
            new NamedIdentifier(Citations.ESRI,    "Albers"),
            new NamedIdentifier(Citations.ESRI,    "Albers_Equal_Area_Conic"),
            new NamedIdentifier(Citations.PROJ4,   "aea"),
            new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                Vocabulary.Keys.ALBERS_EQUAL_AREA_PROJECTION))
        }, new ParameterDescriptor<?>[] {
            SEMI_MAJOR,          SEMI_MINOR, ROLL_LONGITUDE,
            CENTRAL_MERIDIAN,    LATITUDE_OF_ORIGIN,
            STANDARD_PARALLEL_1, STANDARD_PARALLEL_2,
            FALSE_EASTING,       FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public AlbersEqualArea() {
        super(PARAMETERS);
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
        return org.geotoolkit.referencing.operation.projection.AlbersEqualArea.create(getParameters(), values);
    }
}

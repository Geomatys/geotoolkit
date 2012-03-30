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
 * {@code EPSG:9820 and 1027 are the current codes, while EPSG:9821 is a deprecated code. The new
 *        and deprecated definitions differ only by their names. In the Geotk implementation, both
 *        current and legacy definitions are known, but the legacy names are marked as deprecated.}
 *
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/lambert_azimuthal_equal_area.html">Lambert
 * Azimuthal Equal Area on RemoteSensing.org</A>. The math transform implementations instantiated by
 * this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.LambertAzimuthalEqualArea}</li>
 * </ul>
 *
 * @author Beate Stollberg
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
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
     */
    public static final ParameterDescriptor<Double> LONGITUDE_OF_CENTRE =
            Identifiers.CENTRAL_MERIDIAN.select(null, new String[] {
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
     */
    public static final ParameterDescriptor<Double> LATITUDE_OF_CENTRE =
            Identifiers.LATITUDE_OF_ORIGIN.select(null, new String[] {
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
     */
    public static final ParameterDescriptor<Double> FALSE_EASTING = Mercator1SP.FALSE_EASTING;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseNorthing
     * false northing} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 0 metre.
     */
    public static final ParameterDescriptor<Double> FALSE_NORTHING = Mercator1SP.FALSE_NORTHING;

    /**
     * The parameters group.
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
        },  new ParameterDescriptor<?>[] {
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

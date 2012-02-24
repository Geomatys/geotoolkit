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

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.CylindricalProjection;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.projection.Mercator;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Mercator (1SP)</cite>" projection (EPSG:9804, EPSG:1026).
 * EPSG defines two codes for this projection, 1026 being the spherical case and 9804 the
 * ellipsoidal case.
 * <p>
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/mercator_1sp.html">Mercator 1SP on
 * RemoteSensing.org</A>. The math transform implementations instantiated by this provider may
 * be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.Mercator}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @author Rueben Schulz (UBC)
 * @version 3.03
 *
 * @since 2.2
 * @module
 */
@Immutable
public class Mercator1SP extends MapProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -5886510621481710072L;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian
     * central meridian} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-180 &hellip; 180]&deg; and default value is 0&deg;.
     */
    public static final ParameterDescriptor<Double> CENTRAL_MERIDIAN =
            Identifiers.CENTRAL_MERIDIAN.select(
                "central_meridian",             // OGC
                "Central_Meridian",             // ESRI
                "Longitude of natural origin",  // EPSG
                "NatOriginLong");               // GeoTIFF

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#latitudeOfOrigin
     * latitude of origin} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 0&deg;.
     */
    public static final ParameterDescriptor<Double> LATITUDE_OF_ORIGIN =
            Identifiers.LATITUDE_OF_ORIGIN.select(
                "latitude_of_origin",           // OGC
                "Latitude of natural origin",   // EPSG
                "Standard_Parallel_1",          // ESRI
                "NatOriginLat");                // GeoTIFF

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor
     * scale factor} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is (0 &hellip; &infin;) and default value is 1.
     */
    public static final ParameterDescriptor<Double> SCALE_FACTOR =
            Identifiers.SCALE_FACTOR.select(
                "Scale factor at natural origin",   // EPSG
                "ScaleAtNatOrigin");                // GeoTIFF

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
                "False easting",    // EPSG
                "FalseEasting");    // GeoTIFF

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
                "False northing",   // EPSG
                "FalseNorthing");   // GeoTIFF

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,     "Mercator_1SP"),
            new NamedIdentifier(Citations.EPSG,    "Mercator (variant A)"), // Starting from 7.6
            new NamedIdentifier(Citations.EPSG,    "Mercator (1SP)"), // Prior to EPSG version 7.6.
            new NamedIdentifier(Citations.EPSG,    "Mercator (Spherical)"),
            new NamedIdentifier(Citations.EPSG,    "Mercator (1SP) (Spherical)"),
            new IdentifierCode (Citations.EPSG,     9804), // The ellipsoidal case
            new IdentifierCode (Citations.EPSG,     1026), // The spherical case
            new IdentifierCode (Citations.EPSG,     9841), // The spherical (1SP) case
            new NamedIdentifier(Citations.GEOTIFF, "CT_Mercator"),
            new IdentifierCode (Citations.GEOTIFF,  7),
            new NamedIdentifier(Citations.PROJ4,   "merc"),
            new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                Vocabulary.Keys.CYLINDRICAL_MERCATOR_PROJECTION))
        }, new ParameterDescriptor<?>[] {
            SEMI_MAJOR, SEMI_MINOR, ROLL_LONGITUDE,
            LATITUDE_OF_ORIGIN, CENTRAL_MERIDIAN, SCALE_FACTOR,
            FALSE_EASTING, FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public Mercator1SP() {
        super(PARAMETERS);
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
        return Mercator.create(getParameters(), values);
    }
}

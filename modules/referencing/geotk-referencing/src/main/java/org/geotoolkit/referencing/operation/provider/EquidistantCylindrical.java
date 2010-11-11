/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.CylindricalProjection;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.lang.Immutable;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.projection.Equirectangular;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Equidistant Cylindrical</cite>" projection (EPSG:1028).
 *
 * {@note EPSG defines two codes for this projection, 9823 being the spherical case and 1028 the
 *        ellipsoidal case. However the formulas are the same in both cases, with an additional
 *        adjustment of Earth radius in the ellipsoidal case. Consequently they are implemented
 *        in Geotk by the same class. Note however that the 9823 code is deprecated and replaced
 *        by EPSG:1029.}
 *
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/equirectangular.html">Equirectangular
 * on RemoteSensing.org</A>. The math transform implementations instantiated by this provider may
 * be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.Equirectangular}</li>
 * </ul>
 *
 * @author John Grange
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 2.2
 * @module
 */
@Immutable
public class EquidistantCylindrical extends MapProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -278288251842178001L;

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
                "central_meridian",               // OGC
                "Central_Meridian",               // ESRI
                "Longitude of natural origin",    // EPSG
                "ProjCenterLong");                // GeoTIFF

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#latitudeOfOrigin
     * latitude of origin} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 0&deg;.
     *
     * {@note ESRI includes a "<code>standard_parallel_1</code>" parameter instead of
     *        "<cite>Latitude of natural origin</cite>". The ESRI name is also the one
     *        used by Synder, and is declared as an alias.}
     */
    public static final ParameterDescriptor<Double> LATITUDE_OF_ORIGIN =
            Identifiers.LATITUDE_OF_ORIGIN.select(
                "latitude_of_origin",           // OGC
                "Latitude of natural origin",   // EPSG
                "Standard_Parallel_1",          // ESRI
                "ProjCenterLat");               // GeoTIFF

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
            new NamedIdentifier(Citations.OGC,     "Equidistant_Cylindrical"),
            new NamedIdentifier(Citations.EPSG,    "Equidistant Cylindrical"),
            new DeprecatedName (Citations.EPSG,    "Equidistant Cylindrical (Spherical)"),
            new IdentifierCode (Citations.EPSG,     1028),       // The ellipsoidal case
            new IdentifierCode (Citations.EPSG,     9842, 1028), // The ellipsoidal case (deprecated)
            new IdentifierCode (Citations.EPSG,     9823, 1029), // The cylindrical case (deprecated)
            new NamedIdentifier(Citations.GEOTIFF, "CT_Equirectangular"),
            new IdentifierCode (Citations.GEOTIFF,  17),
            new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                Vocabulary.Keys.EQUIDISTANT_CYLINDRICAL_PROJECTION))
        }, new ParameterDescriptor<?>[] {
            SEMI_MAJOR,       SEMI_MINOR, ROLL_LONGITUDE,
            CENTRAL_MERIDIAN, LATITUDE_OF_ORIGIN,
            FALSE_EASTING,    FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public EquidistantCylindrical() {
        super(PARAMETERS);
    }

    /**
     * Constructs a new provider for the given parameters.
     */
    EquidistantCylindrical(ParameterDescriptorGroup parameters) {
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
        return Equirectangular.create(getParameters(), values);
    }

    /**
     * The provider for "<cite>Equidistant Cylindrical (Spherical)</cite>" projection (EPSG:1029).
     * This provider is declared explicitly only because EPSG uses a distinct code with different
     * parameter names for this case.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.16
     *
     * @since 3.16
     * @module
     */
    @Immutable
    public static class Spherical extends EquidistantCylindrical {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -8719362109051183475L;

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
                    "latitude_of_origin",                // OGC
                    "Latitude of 1st standard parallel", // EPSG
                    "Standard_Parallel_1",               // ESRI
                    "ProjCenterLat");                    // GeoTIFF

        /**
         * The parameters group.
         */
        public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
            new ReferenceIdentifier[] {
                new NamedIdentifier(Citations.EPSG, "Equidistant Cylindrical (Spherical)"),
                new IdentifierCode (Citations.EPSG, 1029)
            }, new ParameterDescriptor<?>[] {
                SEMI_MAJOR,       SEMI_MINOR, ROLL_LONGITUDE,
                CENTRAL_MERIDIAN, LATITUDE_OF_ORIGIN,
                FALSE_EASTING,    FALSE_NORTHING
            });

        /**
         * Constructs a new provider.
         */
        public Spherical() {
            super(PARAMETERS);
        }
    }
}

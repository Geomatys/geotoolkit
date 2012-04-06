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
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.CylindricalProjection;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.projection.Mercator;
import org.geotoolkit.internal.referencing.DeprecatedName;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Mercator (variant A)</cite>" projection (EPSG:9804, EPSG:1026,
 * <del>EPSG:9841</del>).
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
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @version 3.20
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
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS;
    static {
        final Citation[] excludes = new Citation[] {Citations.ESRI, Citations.NETCDF};
        CENTRAL_MERIDIAN = Identifiers.CENTRAL_MERIDIAN.select(excludes,
                "Longitude of natural origin",      // EPSG
                "central_meridian",                 // OGC
                "NatOriginLong");                   // GeoTIFF
        LATITUDE_OF_ORIGIN = Identifiers.LATITUDE_OF_ORIGIN.select(excludes,
                "Latitude of natural origin",       // EPSG
                "latitude_of_origin",               // OGC
                "NatOriginLat");                    // GeoTIFF
        SCALE_FACTOR = Identifiers.SCALE_FACTOR.select(excludes,
                "Scale factor at natural origin",   // EPSG
                "ScaleAtNatOrigin");                // GeoTIFF
        FALSE_EASTING = Identifiers.FALSE_EASTING.select(excludes,
                "False easting",                    // EPSG
                "FalseEasting");                    // GeoTIFF
        FALSE_NORTHING = Identifiers.FALSE_NORTHING.select(excludes,
                "False northing",                   // EPSG
                "FalseNorthing");                   // GeoTIFF

        PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,     "Mercator_1SP"),
            new NamedIdentifier(Citations.EPSG,    "Mercator (variant A)"), // Starting from 7.6
            new NamedIdentifier(Citations.EPSG,    "Mercator (Spherical)"),
            new DeprecatedName (Citations.EPSG,    "Mercator (1SP)"), // Prior to EPSG version 7.6.
            new DeprecatedName (Citations.EPSG,    "Mercator (1SP) (Spherical)"),
            new IdentifierCode (Citations.EPSG,     9804),       // The ellipsoidal case
            new IdentifierCode (Citations.EPSG,     1026),       // The spherical case
            new IdentifierCode (Citations.EPSG,     9841, 1026), // The spherical (1SP) case
            new NamedIdentifier(Citations.GEOTIFF, "CT_Mercator"),
            new IdentifierCode (Citations.GEOTIFF,  7),
            new NamedIdentifier(Citations.PROJ4,   "merc"),
            new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                Vocabulary.Keys.CYLINDRICAL_MERCATOR_PROJECTION))
        }, excludes, new ParameterDescriptor<?>[] {
            MapProjection.SEMI_MAJOR,
            MapProjection.SEMI_MINOR, ROLL_LONGITUDE,
            LATITUDE_OF_ORIGIN, CENTRAL_MERIDIAN, SCALE_FACTOR,
            FALSE_EASTING, FALSE_NORTHING
        });
    }

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

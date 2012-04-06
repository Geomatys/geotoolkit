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

import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Cassini-Soldner</cite>" projection (EPSG:9806).
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/cassini_soldner.html">Cassini-Soldner
 * on RemoteSensing.org</A>. The math transform implementations instantiated by this provider may
 * be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.CassiniSoldner}</li>
 * </ul>
 *
 * @author Mauro Bartolomeoli
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 * @module
 */
@Immutable
public class CassiniSoldner extends MapProjection {
    /**
     * For compatibility with different versions during deserialization.
     */
    private static final long serialVersionUID = -2473637102471705721L;

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
     * Parameters creation, which must be done before to initialize the {@link #PARAMETERS} field.
     * Note that the central Meridian and Latitude of Origin are shared with ObliqueStereographic.
     */
    static {
        final Citation[] excludes = new Citation[] {Citations.NETCDF};
        CENTRAL_MERIDIAN = Identifiers.CENTRAL_MERIDIAN.select(excludes,
                "Longitude of natural origin",      // EPSG
                "central_meridian",                 // OGC
                "Central_Meridian",                 // ESRI
                "NatOriginLong");                   // GeoTIFF
        LATITUDE_OF_ORIGIN = Identifiers.LATITUDE_OF_ORIGIN.select(excludes,
                "Latitude of natural origin",       // EPSG
                "latitude_of_origin",               // OGC
                "Latitude_Of_Origin",               // ESRI
                "NatOriginLat");                    // GeoTIFF
        SCALE_FACTOR = Identifiers.SCALE_FACTOR.select(excludes,
                "Scale factor at natural origin",   // EPSG
                "ScaleAtNatOrigin");                // GeoTIFf
        FALSE_EASTING  = EquidistantCylindrical.FALSE_EASTING;
        FALSE_NORTHING = EquidistantCylindrical.FALSE_NORTHING;
    }

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,     "Cassini_Soldner"),
            new NamedIdentifier(Citations.EPSG,    "Cassini-Soldner"),
            new IdentifierCode (Citations.EPSG,     9806),
            new NamedIdentifier(Citations.ESRI,    "Cassini"),
            new NamedIdentifier(Citations.GEOTIFF, "CT_CassiniSoldner"),
            new IdentifierCode (Citations.GEOTIFF,  18),
            new NamedIdentifier(Citations.PROJ4,   "cass"),
            new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                Vocabulary.Keys.CASSINI_SOLDNER_PROJECTION))
        }, null, new ParameterDescriptor<?>[] {
            SEMI_MAJOR, SEMI_MINOR, ROLL_LONGITUDE,
            CENTRAL_MERIDIAN, LATITUDE_OF_ORIGIN, SCALE_FACTOR,
            FALSE_EASTING, FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public CassiniSoldner() {
        super(PARAMETERS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MathTransform2D createMathTransform(ParameterValueGroup values) {
        return org.geotoolkit.referencing.operation.projection.CassiniSoldner.create(getParameters(), values);
    }
}

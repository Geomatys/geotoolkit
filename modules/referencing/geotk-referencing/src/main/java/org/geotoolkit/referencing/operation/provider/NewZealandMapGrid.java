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

import javax.measure.unit.SI;
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
import static org.geotoolkit.internal.referencing.Identifiers.createDescriptor;


/**
 * The provider for "<cite>New Zealand Map Grid</cite>" (EPSG:9811).
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/new_zealand_map_grid.html">New Zealand
 * Map Grid on RemoteSensing.org</A>. The math transform implementations instantiated by this
 * provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.NewZealandMapGrid}</li>
 * </ul>
 * <p>
 * This projection is used with the International 1924 ellipsoid.
 *
 * @author Justin Deoliveira (Refractions)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.2
 * @module
 */
@Immutable
public class NewZealandMapGrid extends MapProjection {
    /**
     * For compatibility with different versions during deserialization.
     */
    private static final long serialVersionUID = -7716733400419275656L;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#semiMajor
     * semi major} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is (0 &hellip; &infin;) and default value is 6378388 metres.
     */
    @SuppressWarnings("hiding")
    public static final ParameterDescriptor<Double> SEMI_MAJOR;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#semiMinor
     * semi minor} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is (0 &hellip; &infin;) and default value is approximatively
     * 6356911.95 metres.
     */
    @SuppressWarnings("hiding")
    public static final ParameterDescriptor<Double> SEMI_MINOR;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian
     * central meridian} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-180 &hellip; 180]&deg; and default value is 173&deg;.
     */
    public static final ParameterDescriptor<Double> CENTRAL_MERIDIAN;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#latitudeOfOrigin
     * latitude of origin} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is -41&deg;.
     */
    public static final ParameterDescriptor<Double> LATITUDE_OF_ORIGIN;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseEasting
     * false easting} parameter value. Valid values range is unrestricted.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 2510000 metre.
     */
    public static final ParameterDescriptor<Double> FALSE_EASTING;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseNorthing
     * false northing} parameter value. Valid values range is unrestricted.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 6023150 metre.
     */
    public static final ParameterDescriptor<Double> FALSE_NORTHING;

    /**
     * Parameters creation, which must be done before to initialize the {@link #PARAMETERS} field.
     */
    static {
        final Citation[] excludes = new Citation[] {Citations.ESRI, Citations.NETCDF};
        SEMI_MAJOR = createDescriptor(new NamedIdentifier[] {
                sameNameAs(Citations.OGC,     MapProjection.SEMI_MAJOR),
                sameNameAs(Citations.EPSG,    MapProjection.SEMI_MAJOR),
                sameNameAs(Citations.GEOTIFF, MapProjection.SEMI_MAJOR),
                sameNameAs(Citations.PROJ4,   MapProjection.SEMI_MAJOR)
            }, 6378388.0, 0.0, Double.POSITIVE_INFINITY, SI.METRE, true);
        SEMI_MINOR = createDescriptor(new NamedIdentifier[] {
                sameNameAs(Citations.OGC,     MapProjection.SEMI_MINOR),
                sameNameAs(Citations.EPSG,    MapProjection.SEMI_MINOR),
                sameNameAs(Citations.GEOTIFF, MapProjection.SEMI_MINOR),
                sameNameAs(Citations.PROJ4,   MapProjection.SEMI_MINOR)
            }, 6378388.0*(1-1/297.0), 0.0, Double.POSITIVE_INFINITY, SI.METRE, true);
        CENTRAL_MERIDIAN = Identifiers.CENTRAL_MERIDIAN.select(true, 173.0, excludes, null,
                "Longitude of natural origin",  // EPSG
                "central_meridian",             // OGC
                "NatOriginLong");               // GeoTIFF
        LATITUDE_OF_ORIGIN = Identifiers.LATITUDE_OF_ORIGIN.select(true, -41.0, excludes, null,
                "Latitude of natural origin",   // EPSG
                "latitude_of_origin",           // OGC
                "NatOriginLat");                // GeoTIFF
        FALSE_EASTING = Identifiers.FALSE_EASTING.select(true, 2510000.0, excludes, null,
                "False easting",                // EPSG
                "FalseEasting");                // GeoTIFF
        FALSE_NORTHING = Identifiers.FALSE_NORTHING.select(true, 6023150.0, excludes, null,
                "False northing",               // EPSG
                "FalseNorthing");               // GeoTIFF
    }

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,     "New_Zealand_Map_Grid"),
            new NamedIdentifier(Citations.EPSG,    "New Zealand Map Grid"),
            new IdentifierCode (Citations.EPSG,     9811),
            new NamedIdentifier(Citations.GEOTIFF, "CT_NewZealandMapGrid"),
            new IdentifierCode (Citations.GEOTIFF,  26),
            new NamedIdentifier(Citations.PROJ4,   "nzmg")
        }, null, new ParameterDescriptor<?>[] {
            SEMI_MAJOR, SEMI_MINOR, ROLL_LONGITUDE,
            LATITUDE_OF_ORIGIN, CENTRAL_MERIDIAN,
            FALSE_EASTING, FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public NewZealandMapGrid() {
        super(PARAMETERS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MathTransform2D createMathTransform(ParameterValueGroup values) {
        return org.geotoolkit.referencing.operation.projection.NewZealandMapGrid.create(getParameters(), values);
    }
}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.geotiff.IIOMetadataAdpaters.utils.codes;

/**
 * 
 * ProjCoordTransGeoKey<br>
 * Key ID = 3075<br>
 * Type = SHORT (code)<br>
 * Values: Section 6.3.3.3 codes<br>
 * Allows specification of the coordinate transformation method used. Note: this
 * does not include the definition of the correspondingì Geographic Coordinate
 * System to which the projected CS is related; only the transformation method
 * is defined here.<br>
 * <br>
 * <br>
 * <strong>GeoKeys Required for "user-defined" Coordinate Transformations</strong><br>
 * <br>
 * <br>

 * 
 * @author Simone Giannecchini
 * @module pending
 * @since 2.3
 * 
 */
public final class GeoTiffCoordinateTransformationsCodes {

    public static final short CT_TRANSVERSE_MERCATOR = 1;
    public static final short CT_TRANSVERSE_MERCATOR_MODIFIED_ALASKA = 2;
    public static final short CT_OBLIQUE_MERCATOR = 3;
    public static final short CT_OBLIQUE_MERCATOR_LABORDE = 4;
    public static final short CT_OBLIQUE_MERCATOR_ROSENMUND = 5;
    public static final short CT_OBLIQUE_MERCATOR_SPHERICAL = 6;
    public static final short CT_MERCATOR = 7;
    public static final short CT_LAMBERT_CONF_CONIC_2SP = 8;
    public static final short CT_LAMBERT_CONF_CONIC = CT_LAMBERT_CONF_CONIC_2SP;
    public static final short CT_LAMBERT_CONF_CONIC_1SP = 9;
    public static final short CT_LAMBERT_CONF_CONIC_HELMERT = CT_LAMBERT_CONF_CONIC_1SP;
    public static final short CT_LAMBERT_AZIM_EQUAL_AREA = 10;
    public static final short CT_ALBERS_EQUAL_AREA = 11;
    public static final short CT_AZIMUTHAL_EQUIDISTANT = 12;
    public static final short CT_EQUIDISTANT_CONIC = 13;
    public static final short CT_STEREOGRAPHIC = 14;
    public static final short CT_POLAR_STEREOGRAPHIC = 15;
    public static final short CT_OBLIQUE_STEREOGRAPHIC = 16;
    public static final short CT_EQUIRECTANGULAR = 17;
    public static final short CT_CASSINI_SOLDNER = 18;
    public static final short CT_GNOMONIC = 19;
    public static final short CT_MILLER_CYLINDRICAL = 20;
    public static final short CT_ORTHOGRAPHIC = 21;
    public static final short CT_POLYCONIC = 22;
    public static final short CT_ROBINSON = 23;
    public static final short CT_SINUSOIDAL = 24;
    public static final short CT_VAN_DER_GRINTEN = 25;
    public static final short CT_NEW_ZEALAND_MAP_GRID = 26;
    public static final short CT_TRANSV_MERCATOR_SOUTH_ORIENTED = 27;
    public static final short CT_SOUTH_ORIENTED_GAUSS_CONFORMAL = CT_TRANSV_MERCATOR_SOUTH_ORIENTED;
    public static final short CT_ALASKA_CONFORMAL = CT_TRANSVERSE_MERCATOR_MODIFIED_ALASKA;
    public static final short CT_TRANSV_EQUIDIST_CYLINDRICAL = CT_CASSINI_SOLDNER;
    public static final short CT_OBLIQUE_MERCATOR_HOTINE = CT_OBLIQUE_MERCATOR;
    public static final short CT_SWISS_OBLIQUE_CYLINDRICAL = CT_OBLIQUE_MERCATOR_ROSENMUND;
    public static final short CT_GAUSS_BOAGA = CT_TRANSVERSE_MERCATOR;
    public static final short CT_GAUSS_KRUGER = CT_TRANSVERSE_MERCATOR;

    private GeoTiffCoordinateTransformationsCodes() {
    }
}

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
package org.geotoolkit.coverage.geotiff.IIOMetadataAdpaters.utils;

/**
 * @author simone
 *
 */
public final class GeoTiffConstants {

    public static final short GT_USER_DEFINED_GEO_KEY = 32767;
    public static final String GT_USER_DEFINED_GEO_KEY_STRING = "32767".intern();
    public static final short ARRAY_ELEM_INCREMENT = 5;
    public static final String GEOTIFF_IIO_METADATA_FORMAT_NAME = "com_sun_media_imageio_plugins_tiff_image_1.0".intern();
    public static final String GEOTIFF_IIO_ROOT_ELEMENT_NAME = GEOTIFF_IIO_METADATA_FORMAT_NAME;
    public static final int USHORT_MAX = (1 << 16) - 1;
    public static final int USHORT_MIN = 0;
    /**
     * GTModelTypeGeoKey Key ID = 1024 Type: SHORT (code) Values: Section
     * 6.3.1.1 Codes This GeoKey defines the general type of model Coordinate
     * system used, and to which the raster space will be transformed: unknown,
     * Geocentric (rarely used), Geographic, Projected Coordinate System, or
     * user-defined. If the coordinate system is a PCS, then only the PCS code
     * need be specified. If the coordinate system does not fit into one of the
     * standard registered PCS'S, but it uses one of the standard projections
     * and datums, then its should be documented as a PCS model with
     * "user-defined" type, requiring the specification of projection
     * parameters, etc. GeoKey requirements for User-Defined Model Type (not
     * advisable): GTCitationGeoKey
     */
    public static final int GT_MODEL_TYPE_GEO_KEY = 1024;
    /**
     * GTRasterTypeGeoKey Key ID = 1025 Type = Section 6.3.1.2 codes This
     * establishes the Raster Space coordinate system used; there are currently
     * only two, namely RasterPixelIsPoint and RasterPixelIsArea. 
     */
    public static final int GT_RASTER_TYPE_GEO_KEY = 1025;
    /**
     * 6.3.1.2 Raster Type Codes Ranges: 0 = undefined [ 1, 1023] = Raster Type
     * Codes (GeoTIFFWritingUtilities Defined) [1024, 32766] = Reserved 32767 =
     * user-defined [32768, 65535]= Private User 
     */
    public static final int RASTER_PIXEL_IS_AREA = 1;
    public static final int RASTER_PIXEL_IS_POINT = 2;
    /**
     * The DOM element ID (tag) for a single TIFF Ascii value
     */
    public static final String GEOTIFF_ASCII_TAG = "TIFFAscii".intern();
    /**
     * The DOM element ID (tag) for a set of TIFF Ascii values
     */
    public static final String GEOTIFF_ASCIIS_TAG = "TIFFAsciis".intern();
    /**
     * The DOM element ID (tag) for a single TIFF double. The value is stored in
     * an attribute named "value"
     */
    public static final String GEOTIFF_DOUBLE_TAG = "TIFFDouble".intern();
    /**
     * The DOM element ID (tag) for a set of TIFF Double values
     */
    public static final String GEOTIFF_DOUBLES_TAG = "TIFFDoubles".intern();
    /**
     * The DOM element ID (tag) for a TIFF Field
     */
    public static final String GEOTIFF_FIELD_TAG = "TIFFField".intern();
    /**
     * The DOM element ID (tag) for a TIFF Image File Directory
     */
    public static final String GEOTIFF_IFD_TAG = "TIFFIFD".intern();
    /**
     * The DOM element ID (tag) for a single TIFF Short value. The value is
     * stored in an attribute named "value"
     */
    public static final String GEOTIFF_SHORT_TAG = "TIFFShort".intern();
    /**
     * The DOM element ID (tag) for a set of TIFF Short values
     */
    public static final String GEOTIFF_SHORTS_TAG = "TIFFShorts".intern();
    /**
     * The DOM attribute name for a TIFF Entry value (whether Short, Double, or
     * Ascii)
     */
    public static final String VALUE_ATTRIBUTE = "value".intern();
    /**
     * The DOM attribute name for a TIFF Field Tag (number)
     */
    public static final String NUMBER_ATTRIBUTE = "number".intern();
    /**
     * The DOM attribute name for a TIFF Field Tag (name)
     */
    public static final String NAME_ATTRIBUTE = "name";
    public static final String GEOTIFF_TAGSETS_ATT_NAME = "tagSets".intern();
    public static final int DEFAULT_GEOTIFF_VERSION = 1;
    public static final int DEFAULT_KEY_REVISION_MAJOR = 1;
    public static final int DEFAULT_KEY_REVISION_MINOR = 2;
    public static final int UNDEFINED = 0;

    private GeoTiffConstants() {}
}

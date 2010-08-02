/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.metadata.geotiff;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class GeoTiffConstants {

    ////////////////////////////////////////////////////////////////////////////
    // TAGS
    ////////////////////////////////////////////////////////////////////////////

    /**
     * GeoTIFF Image File Directory tag name.
     */
    public static final String TAG_GEOTIFF_IFD = "TIFFIFD";
    /**
     * GeoTIFF Field tag name.
     */
    public static final String TAG_GEOTIFF_FIELD = "TIFFField";
    /**
     * GeoTIFF Ascii value tag name.
     */
    public static final String TAG_GEOTIFF_ASCII = "TIFFAscii";
    /**
     * GeoTIFF Ascii values tag name.
     */
    public static final String TAG_GEOTIFF_ASCIIS = "TIFFAsciis";
    /**
     * GeoTIFF Double value tag name.
     */
    public static final String TAG_GEOTIFF_DOUBLE = "TIFFDouble";
    /**
     * GeoTIFF Double values tag names.
     */
    public static final String TAG_GEOTIFF_DOUBLES = "TIFFDoubles";
    /**
     * GeoTIFF Short value tag name.
     */
    public static final String TAG_GEOTIFF_SHORT = "TIFFShort";
    /**
     * GeoTIFF Short values tag name.
     */
    public static final String TAG_GEOTIFF_SHORTS = "TIFFShorts";
    /**
     * GeoTIFF Long value tag name.
     */
    public static final String TAG_GEOTIFF_LONG = "TIFFLong";
    /**
     * GeoTIFF Long values tag name.
     */
    public static final String TAG_GEOTIFF_LONGS = "TIFFLongs";


    ////////////////////////////////////////////////////////////////////////////
    // ATTRIBUTES
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Number attribut of a node.
     */
    public static final String ATT_NUMBER = "number";
    /**
     * Name attribut of a node.
     */
    public static final String ATT_NAME = "name";
    /**
     * Value attribut of a node.
     */
    public static final String ATT_VALUE = "value";
    /**
     * Serie on string values concatenate by ','.
     */
    public static final String ATT_TAGSETS = "tagSets";

    
    ////////////////////////////////////////////////////////////////////////////
    // KEYS, values are taken from :
    // http://www.remotesensing.org/geotiff/spec/geotiff6.html#6
    ////////////////////////////////////////////////////////////////////////////

    //6.2.1 GeoTIFF Configuration Keys
    public static final int GTModelTypeGeoKey           = 1024; /* Section 6.3.1.1 Codes */
    public static final int GTRasterTypeGeoKey          = 1025; /* Section 6.3.1.2 Codes */
    public static final int GTCitationGeoKey            = 1026; /* documentation */

    //6.2.2 Geographic CS Parameter Keys
    public static final int GeographicTypeGeoKey        = 2048; /* Section 6.3.2.1 Codes */
    public static final int GeogCitationGeoKey          = 2049; /* documentation */
    public static final int GeogGeodeticDatumGeoKey     = 2050; /* Section 6.3.2.2 Codes */
    public static final int GeogPrimeMeridianGeoKey     = 2051; /* Section 6.3.2.4 codes */
    public static final int GeogLinearUnitsGeoKey       = 2052; /* Section 6.3.1.3 Codes */
    public static final int GeogLinearUnitSizeGeoKey    = 2053; /* meters */
    public static final int GeogAngularUnitsGeoKey      = 2054; /* Section 6.3.1.4 Codes */
    public static final int GeogAngularUnitSizeGeoKey   = 2055; /* radians */
    public static final int GeogEllipsoidGeoKey         = 2056; /* Section 6.3.2.3 Codes */
    public static final int GeogSemiMajorAxisGeoKey     = 2057; /* GeogLinearUnits */
    public static final int GeogSemiMinorAxisGeoKey     = 2058; /* GeogLinearUnits */
    public static final int GeogInvFlatteningGeoKey     = 2059; /* ratio */
    public static final int GeogAzimuthUnitsGeoKey      = 2060; /* Section 6.3.1.4 Codes */
    public static final int GeogPrimeMeridianLongGeoKey = 2061; /* GeogAngularUnit */

    //6.2.3 Projected CS Parameter Keys
    public static final int ProjectedCSTypeGeoKey          = 3072;  /* Section 6.3.3.1 codes */
    public static final int PCSCitationGeoKey              = 3073;  /* documentation */
    public static final int ProjectionGeoKey               = 3074;  /* Section 6.3.3.2 codes */
    public static final int ProjCoordTransGeoKey           = 3075;  /* Section 6.3.3.3 codes */
    public static final int ProjLinearUnitsGeoKey          = 3076;  /* Section 6.3.1.3 codes */
    public static final int ProjLinearUnitSizeGeoKey       = 3077;  /* meters */
    public static final int ProjStdParallel1GeoKey         = 3078;  /* GeogAngularUnit */
    public static final int ProjStdParallel2GeoKey         = 3079;  /* GeogAngularUnit */
    public static final int ProjNatOriginLongGeoKey        = 3080;  /* GeogAngularUnit */
    public static final int ProjNatOriginLatGeoKey         = 3081;  /* GeogAngularUnit */
    public static final int ProjFalseEastingGeoKey         = 3082;  /* ProjLinearUnits */
    public static final int ProjFalseNorthingGeoKey        = 3083;  /* ProjLinearUnits */
    public static final int ProjFalseOriginLongGeoKey      = 3084;  /* GeogAngularUnit */
    public static final int ProjFalseOriginLatGeoKey       = 3085;  /* GeogAngularUnit */
    public static final int ProjFalseOriginEastingGeoKey   = 3086;  /* ProjLinearUnits */
    public static final int ProjFalseOriginNorthingGeoKey  = 3087;  /* ProjLinearUnits */
    public static final int ProjCenterLongGeoKey           = 3088;  /* GeogAngularUnit */
    public static final int ProjCenterLatGeoKey            = 3089;  /* GeogAngularUnit */
    public static final int ProjCenterEastingGeoKey        = 3090;  /* ProjLinearUnits */
    public static final int ProjCenterNorthingGeoKey       = 3091;  /* ProjLinearUnits */
    public static final int ProjScaleAtNatOriginGeoKey     = 3092;  /* ratio */
    public static final int ProjScaleAtCenterGeoKey        = 3093;  /* ratio */
    public static final int ProjAzimuthAngleGeoKey         = 3094;  /* GeogAzimuthUnit */
    public static final int ProjStraightVertPoleLongGeoKey = 3095;  /* GeogAngularUnit */
    //Aliases:
    public static final int ProjStdParallelGeoKey       = ProjStdParallel1GeoKey;
    public static final int ProjOriginLongGeoKey        = ProjNatOriginLongGeoKey;
    public static final int ProjOriginLatGeoKey         = ProjNatOriginLatGeoKey;
    public static final int ProjScaleAtOriginGeoKey     = ProjScaleAtNatOriginGeoKey;

    //6.2.4 Vertical CS Keys
    public static final int VerticalCSTypeGeoKey    = 4096;   /* Section 6.3.4.1 codes */
    public static final int VerticalCitationGeoKey  = 4097;   /* documentation */
    public static final int VerticalDatumGeoKey     = 4098;   /* Section 6.3.4.2 codes */
    public static final int VerticalUnitsGeoKey     = 4099;   /* Section 6.3.1.3 codes */

    ////////////////////////////////////////////////////////////////////////////
    // Codes
    ////////////////////////////////////////////////////////////////////////////

    /*
     * 6.3.1.1 Model Type Codes
     *
     * Ranges:
     *   0              = undefined
     *   [   1,  32766] = GeoTIFF Reserved Codes
     *   32767          = user-defined
     *   [32768, 65535] = Private User Implementations
     *
     * Notes:
     *   1. ModelTypeGeographic and ModelTypeProjected
     *   correspond to the FGDC metadata Geographic and
     *   Planar-Projected coordinate system types.
     */
    //GeoTIFF defined CS Model Type Codes:
    public static final int ModelTypeProjected   = 1;   /* Projection Coordinate System         */
    public static final int ModelTypeGeographic  = 2;   /* Geographic latitude-longitude System */
    public static final int ModelTypeGeocentric  = 3;   /* Geocentric (X,Y,Z) Coordinate System */

    /*
     * 6.3.1.2 Raster Type Codes
     *
     * Ranges:
     *  0             = undefined
     *  [   1,  1023] = Raster Type Codes (GeoTIFF Defined)
     *  [1024, 32766] = Reserved
     *  32767         = user-defined
     *  [32768, 65535]= Private User Implementations
     *
     * Notes:
     *   Use of "user-defined" or "undefined" raster codes is not recommended.
     */
    public static final int RasterPixelIsArea  = 1;
    public static final int RasterPixelIsPoint = 2;


}

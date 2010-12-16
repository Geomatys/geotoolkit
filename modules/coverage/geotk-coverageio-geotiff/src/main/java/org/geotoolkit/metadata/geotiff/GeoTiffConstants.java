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
 * @Static
 */
public final class GeoTiffConstants {

    private GeoTiffConstants(){}

    public static final int GEOTIFF_VERSION = 1;
    public static final int REVISION_MAJOR = 1;
    public static final int REVISION_MINOR = 2;

    ////////////////////////////////////////////////////////////////////////////
    // TIFF TAGS
    ////////////////////////////////////////////////////////////////////////////

    public static final int ImageWidth = 256;
    public static final int ImageLenght = 257;
    public static final int ResolutionUnit = 296;
    public static final int XResolution = 282;
    public static final int YResolution = 283;
    public static final int Orientation = 274;
    public static final int XPosition = 286;
    public static final int YPosition = 287;

    
    ////////////////////////////////////////////////////////////////////////////
    // GEOTIFF TAGS
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


    public static final String NAME_GEO_KEY_DIRECTORY = "GeoKeyDirectory";


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


    public static final short GTUserDefinedGeoKey = 32767;
    public static final String GTUserDefinedGeoKey_String = "32767";

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

    /*
     *6.3.1.3 Linear Units Codes
     *
     *  There are several different kinds of units that may be used in geographically related raster data: linear units, angular units, units of time (e.g. for radar-return), CCD-voltages, etc. For this reason there will be a single, unique range for each kind of unit, broken down into the following currently defined ranges:
     *  Ranges:
     *     0             = undefined
     *     [   1,  2000] = Obsolete GeoTIFF codes
     *     [2001,  8999] = Reserved by GeoTIFF
     *     [9000,  9099] = EPSG Linear Units.
     *     [9100,  9199] = EPSG Angular Units.
     *     32767         = user-defined unit
     *     [32768, 65535]= Private User Implementations
     *  Linear Unit Values (See the ESPG/POSC tables for definition):
     */
    public static final int Linear_Meter                       = 9001;
    public static final int Linear_Foot                        = 9002;
    public static final int Linear_Foot_US_Survey              = 9003;
    public static final int Linear_Foot_Modified_American      = 9004;
    public static final int Linear_Foot_Clarke                 = 9005;
    public static final int Linear_Foot_Indian                 = 9006;
    public static final int Linear_Link                        = 9007;
    public static final int Linear_Link_Benoit                 = 9008;
    public static final int Linear_Link_Sears                  = 9009;
    public static final int Linear_Chain_Benoit                = 9010;
    public static final int Linear_Chain_Sears                 = 9011;
    public static final int Linear_Yard_Sears                  = 9012;
    public static final int Linear_Yard_Indian                 = 9013;
    public static final int Linear_Fathom                      = 9014;
    public static final int Linear_Mile_International_Nautical = 9015;

    /*
     * 6.3.1.4 Angular Units Codes
     * These codes shall be used for any key that requires specification of an angular unit of measurement. 
     */
    public static final int Angular_Radian         = 9101;
    public static final int Angular_Degree         = 9102;
    public static final int Angular_Arc_Minute     = 9103;
    public static final int Angular_Arc_Second     = 9104;
    public static final int Angular_Grad           = 9105;
    public static final int Angular_Gon            = 9106;
    public static final int Angular_DMS            = 9107;
    public static final int Angular_DMS_Hemisphere = 9108;

    /*
     * 6.3.3.3 Coordinate Transformation Codes
     * Ranges:
     * 0 = undefined
     * [    1, 16383] = GeoTIFF Coordinate Transformation codes
     * [16384, 32766] = Reserved by GeoTIFF
     * 32767          = user-defined
     * [32768, 65535] = Private User Implementations
     *
     */
    public static final int CT_TransverseMercator =             1;
    public static final int CT_TransvMercator_Modified_Alaska = 2;
    public static final int CT_ObliqueMercator =                3;
    public static final int CT_ObliqueMercator_Laborde =        4;
    public static final int CT_ObliqueMercator_Rosenmund =      5;
    public static final int CT_ObliqueMercator_Spherical =      6;
    public static final int CT_Mercator =                       7;
    public static final int CT_LambertConfConic_2SP =           8;
    public static final int CT_LambertConfConic_1SP =           9;
    public static final int CT_LambertAzimEqualArea =           10;
    public static final int CT_AlbersEqualArea =                11;
    public static final int CT_AzimuthalEquidistant =           12;
    public static final int CT_EquidistantConic =               13;
    public static final int CT_Stereographic =                  14;
    public static final int CT_PolarStereographic =             15;
    public static final int CT_ObliqueStereographic =           16;
    public static final int CT_Equirectangular =                17;
    public static final int CT_CassiniSoldner =                 18;
    public static final int CT_Gnomonic =                       19;
    public static final int CT_MillerCylindrical =              20;
    public static final int CT_Orthographic =                   21;
    public static final int CT_Polyconic =                      22;
    public static final int CT_Robinson =                       23;
    public static final int CT_Sinusoidal =                     24;
    public static final int CT_VanDerGrinten =                  25;
    public static final int CT_NewZealandMapGrid =              26;
    public static final int CT_TransvMercator_SouthOriented=    27;
    //Aliases:
    public static final int CT_AlaskaConformal =                CT_TransvMercator_Modified_Alaska;
    public static final int CT_TransvEquidistCylindrical =      CT_CassiniSoldner;
    public static final int CT_ObliqueMercator_Hotine =         CT_ObliqueMercator;
    public static final int CT_SwissObliqueCylindrical =        CT_ObliqueMercator_Rosenmund;
    public static final int CT_GaussBoaga =                     CT_TransverseMercator;
    public static final int CT_GaussKruger =                    CT_TransverseMercator;
    public static final int CT_LambertConfConic =               CT_LambertConfConic_2SP ;
    public static final int CT_LambertConfConic_Helmert =       CT_LambertConfConic_1SP;
    public static final int CT_SouthOrientedGaussConformal =    CT_TransvMercator_SouthOriented;


}

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

import java.lang.reflect.Field;

/**
 * @author Johann Sorel (Geomatys)
 * @module
 * @Static
 */
public final class GeoTiffConstants {

    private GeoTiffConstants(){}

    public static final int GEOTIFF_VERSION = 1;
    public static final int REVISION_MAJOR = 1;
    public static final int REVISION_MINOR = 2;


    //////////////////////////////////////////////////////////
    //                    BASELINE TIFF TAG                 //
    //////////////////////////////////////////////////////////

    public static final int NewSubfileType              = 0x00FE;
    public static final int SubfileType                 = 0x00FF;
    public static final int ImageWidth                  = 0x0100;
    public static final int ImageLength                 = 0x0101;
    public static final int BitsPerSample               = 0x0102;
    public static final int Compression                 = 0x0103;
    public static final int PhotometricInterpretation   = 0x0106;
    public static final int Threshholding               = 0x0107;
    public static final int CellWidth                   = 0x0108;
    public static final int CellLength                  = 0x0109;
    public static final int FillOrder                   = 0x010A;
    public static final int DocumentName                = 0x010D;
    public static final int ImageDescription            = 0x010E;
    public static final int Make                        = 0x010F;
    public static final int Model                       = 0x0110;
    public static final int StripOffsets                = 0x0111;
    public static final int Orientation                 = 0x0112;
    public static final int SamplesPerPixel             = 0x0115;
    public static final int RowsPerStrip                = 0x0116;
    public static final int StripByteCounts             = 0x0117;
    public static final int MinSampleValue              = 0x0118;
    public static final int MaxSampleValue              = 0x0119;
    public static final int XResolution                 = 0x011A;
    public static final int YResolution                 = 0x011B;
    public static final int PlanarConfiguration         = 0x011C;
    public static final int PageName                    = 0x011D;
    public static final int XPosition                   = 0x011E;
    public static final int YPosition                   = 0x011F;
    public static final int FreeOffsets                 = 0x0120;
    public static final int FreeByteCounts              = 0x0121;
    public static final int GrayResponseUnit            = 0x0122;
    public static final int GrayResponseCurve           = 0x0123;
    public static final int T4Options                   = 0x0124;
    public static final int T6Options                   = 0x0125;
    public static final int ResolutionUnit              = 0x0128;
    public static final int PageNumber                  = 0x0129;
    public static final int TransferFunction            = 0x012D;
    public static final int Software                    = 0x0131;
    public static final int DateTime                    = 0x0132;
    public static final int DateTimeOriginal            = 0x9003;
    public static final int DateTimeDigitized           = 0x9004;
    public static final int Artist                      = 0x013B;
    public static final int HostComputer                = 0x013C;
    public static final int Predictor                   = 0x013D;
    public static final int WhitePoint                  = 0x013E;
    public static final int PrimaryChromaticities       = 0x013F;
    public static final int ColorMap                    = 0x0140;
    public static final int HalftoneHints               = 0x0141;
    public static final int TileWidth                   = 0x0142;
    public static final int TileLength                  = 0x0143;
    public static final int TileOffsets                 = 0x0144;
    public static final int TileByteCounts              = 0x0145;
    public static final int InkSet                      = 0x014C;
    public static final int InkNames                    = 0x014D;
    public static final int NumberOfInks                = 0x014E;
    public static final int DotRange                    = 0x0150;
    public static final int TargetPrinter               = 0x0151;
    public static final int ExtraSamples                = 0x0152;
    public static final int SampleFormat                = 0x0153;
    public static final int SMinSampleValue             = 0x0154;
    public static final int SMaxSampleValue             = 0x0155;
    public static final int TransferRange               = 0x0156;
    public static final int JPEGProc                    = 0x0200;
    public static final int JPEGInterchangeFormat       = 0x0201;
    public static final int JPEGInterchangeFormatLength = 0x0202;
    public static final int JPEGRestartInterval         = 0x0203;
    public static final int JPEGLosslessPredictors      = 0x0205;
    public static final int JPEGPointTransforms         = 0x0206;
    public static final int JPEGQTables                 = 0x0207;
    public static final int JPEGDCTables                = 0x0208;
    public static final int JPEGACTables                = 0x0209;
    public static final int YCbCrCoefficients           = 0x0211;
    public static final int YCbCrSubSampling            = 0x0212;
    public static final int YCbCrPositioning            = 0x0213;
    public static final int ReferenceBlackWhite         = 0x0214;
    public static final int Copyright                   = 0x8298;

    /**
     * Return tag Name from {@link GeoTiffConstants} class.
     *
     * @param tag
     * @return tag Name from {@link GeoTiffConstants} class.
     */
    public static String getName(final int tag) {
        try {
            for (final Field field : GeoTiffConstants.class.getDeclaredFields()) {
                if (field.getType() == Integer.TYPE) {
                    if (field.getInt(null) == tag) {
                        return field.getName();
                    }
                }
            }
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex); // Should never happen.
        }
        return Integer.toHexString(tag);
    }

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

    /**
     * GeoTIFF Image CRS Directory tag name.
     */
    public static final String NAME_GEO_KEY_DIRECTORY = "GeoKeyDirectory";


    //-- CRS tags
    /**
     * References the needed "GeoKeys" to build CRS.
     */
    public static final int GeoKeyDirectoryTag = 0x87AF; //-- 34735

    /**
     * This tag is used to store all of the DOUBLE valued GeoKeys, referenced by the GeoKeyDirectoryTag.
     */
    public static final int GeoDoubleParamsTag = 0x87B0; //-- 34736

    /**
     * This tag is used to store all of the ASCII valued GeoKeys, referenced by the GeoKeyDirectoryTag.
     */
    public static final int GeoAsciiParamsTag = 0x87B1; //-- 34737


    //-- grid to Crs tags
    /**
     *  This tag is optionally provided for defining exact affine transformations between raster and model space.
     */
    public static final int ModelTransformationTag = 0x85D8; //-- 34264

    /**
     * This tag is optionally provided for defining exact affine transformations between raster and model space.<br>
     * Baseline GeoTIFF files may use this tag or ModelTransformationTag, but shall never use both within the same TIFF image directory.<br>
     *
     * This tag may be used to specify the size of raster pixel spacing in the model space units,
     * when the raster space can be embedded in the model space coordinate system without rotation, and consists of the following 3 values:
     *
     * ModelPixelScaleTag = (ScaleX, ScaleY, ScaleZ)
     *
     * where ScaleX and ScaleY give the horizontal and vertical spacing of raster pixels.<br>
     *
     * The ScaleZ is primarily used to map the pixel value of a digital elevation model into the correct Z-scale,
     * and so for most other purposes this value should be zero (since most model spaces are 2-D, with Z=0).
     */
    public static final int ModelPixelScaleTag = 0x830E; //-- 33550

    /**
     * This tag stores raster -> model tiepoint pairs in the order
     * ModelTiepointTag = (...,I, J, K, X, Y, Z...)
     *
     * where (I, J, K) is the point at location (I, J) in raster space with pixel-value K, and (X, Y, Z) is a vector in model space.<br>
     *
     * In most cases the model space is only two-dimensional, in which case both K and Z should be set to zero;
     * this third dimension is provided in anticipation of future support for 3D digital elevation models and vertical
     * coordinate systems.
     */
    public static final int ModelTiepointTag = 0x8482; //-- 33922


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
     * Type attribut of a node.
     */
    public static final String ATT_TYPE = "type";
    /**
     * Value number from value attribut of a node.
     */
    public static final String ATT_COUNT = "count";
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

    // GDAL extension tags
    public static final int GDAL_METADATA_KEY = 42112;  /* http://www.awaresystems.be/imaging/tiff/tifftags/gdal_metadata.html */
    public static final int GDAL_NODATA_KEY   = 42113;  /* http://www.awaresystems.be/imaging/tiff/tifftags/gdal_nodata.html */


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
     * 6.3.1.3 Linear Units Codes
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

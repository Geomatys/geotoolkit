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

package org.geotoolkit.metadata.dimap;

import org.geotoolkit.math.NumberSet;

/**
 * Dimap constants.
 *
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class DimapConstants {
    
    /**
     * Raster encoding possible byteOrder
     */
    public static enum ByteOrder{
        I(java.nio.ByteOrder.LITTLE_ENDIAN),
        INTEL(java.nio.ByteOrder.LITTLE_ENDIAN),
        M(java.nio.ByteOrder.BIG_ENDIAN),
        MOTOROLA(java.nio.ByteOrder.BIG_ENDIAN);

        private final java.nio.ByteOrder bo;

        private ByteOrder(final java.nio.ByteOrder bo){
            this.bo = bo;
        }

        public java.nio.ByteOrder getOrder(){
            return bo;
        }

    }

    /**
     * Raster encoding possible types.
     */
    public static enum DataType{
        BYTE(NumberSet.NATURAL),
        SHORT(NumberSet.NATURAL),
        LONG(NumberSet.NATURAL),
        SBYTE(NumberSet.INTEGER),
        SSHORT(NumberSet.INTEGER),
        SLONG(NumberSet.INTEGER),
        FLOAT(NumberSet.REAL),
        DOUBLE(NumberSet.REAL),
        UNSIGNED(NumberSet.REAL);

        private final NumberSet n;

        private DataType(final NumberSet n){
            this.n = n;
        }

        public NumberSet getNumberSet(){
            return n;
        }
        
    }

    ////////////////////////////////////////////////////////////////////////////
    // XML CONSTANTS ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static final String TAG_DIMAP = "Dimap_Document";

    // Dataset id group --------------------------------------------------------
    public static final String TAG_DATASET_ID = "Dataset_Id";
    public static final String TAG_DATASET_NAME = "DATASET_NAME";
    public static final String TAG_DATASET_COPYRIGHT = "COPYRIGHT";
    public static final String TAG_DATASET_QL_PATH = "DATASET_QL_PATH";
    public static final String TAG_DATASET_QL_FORMAT = "DATASET_QL_FORMAT";
    public static final String TAG_DATASET_TN_PATH = "DATASET_TN_PATH";
    public static final String TAG_DATASET_TN_FORMAT = "DATASET_TN_FORMAT";
    public static final String ATTRIBUTE_HREF = "href";

    // Scene frame group -------------------------------------------------------
    public static final String TAG_DATASET_FRAME = "Dataset_Frame";
    public static final String TAG_VERTEX = "Vertex";
    public static final String TAG_FRAME_LON = "FRAME_LON";
    public static final String TAG_FRAME_LAT = "FRAME_LAT";
    public static final String TAG_FRAME_ROW = "FRAME_ROW";
    public static final String TAG_FRAME_COL = "FRAME_COL";

    // Production group --------------------------------------------------------
    public static final String TAG_PRODUCTION = "Production";
    public static final String TAG_DATASET_PRODUCER_NAME = "DATASET_PRODUCER_NAME";
    public static final String TAG_DATASET_PRODUCER_URL = "DATASET_PRODUCER_URL";
    public static final String ATT_HREF = "href";
    public static final String TAG_DATASET_PRODUCTION_DATE = "DATASET_PRODUCTION_DATE";
    public static final String TAG_PRODUCT_TYPE = "PRODUCT_TYPE";
    public static final String TAG_PRODUCT_INFO = "PRODUCT_INFO";
    public static final String TAG_JOB_ID = "JOB_ID";
    public static final String TAG_PRODUCTION_FACILITY = "Production_Facility";
    public static final String TAG_PRODUCTION_FACILITY_SOFTWARE_NAME = "SOFTWARE_NAME";
    public static final String TAG_PRODUCTION_FACILITY_SOFTWARE_VERSION = "SOFTWARE_VERSION";
    public static final String TAG_PRODUCTION_FACILITY_PROCESSING_CENTER = "PROCESSING_CENTER";
    
    // Quality assessment group ------------------------------------------------
    public static final String TAG_QUALITY_ASSESSMENT = "Quality_Assessment";
    public static final String TAG_QUALITY_TABLES = "QUALITY_TABLES";
    public static final String TAG_QUALITY_PARAMETER = "Quality_Parameter";
    public static final String TAG_QUALITY_PARAMETER_DESC = "QUALITY_PARAMETER_DESC";
    public static final String TAG_QUALITY_PARAMETER_CODE = "QUALITY_PARAMETER_CODE";
    public static final String TAG_QUALITY_PARAMETER_VALUE = "QUALITY_PARAMETER_VALUE";

    // Dataset sources group ---------------------------------------------------
    public static final String TAG_DATASET_SOURCES = "Dataset_Sources";
    public static final String TAG_SOURCE_INFORMATION = "Source_Information";
    public static final String TAG_SOURCE_ID = "SOURCE_ID";
    public static final String TAG_SOURCE_TYPE = "SOURCE_TYPE";
    public static final String TAG_SOURCE_DESCRIPTION = "SOURCE_DESCRIPTION";
    public static final String TAG_SOURCE_REF = "SOURCE_REF";
    // Scene tags
    public static final String TAG_SCENE_SOURCE = "Scene_Source";
    public static final String TAG_SCENE_IMAGING_DATE = "IMAGING_DATE";
    public static final String TAG_SCENE_MISSION = "MISSION";
    public static final String TAG_SCENE_MISSION_INDEX = "MISSION_INDEX";
    public static final String TAG_SCENE_INSTRUMENT = "INSTRUMENT";
    public static final String TAG_SCENE_INSTRUMENT_INDEX = "INSTRUMENT_INDEX";
    public static final String TAG_SCENE_PROCESSING_LEVEL = "SCENE_PROCESSING_LEVEL";
    public static final String TAG_SCENE_INCIDENCE_ANGLE = "INCIDENCE_ANGLE";
    public static final String TAG_SCENE_THEORETICAL_RESOLUTION = "THEORETICAL_RESOLUTION";
    public static final String TAG_SCENE_VIEWING_ANGLE = "VIEWING_ANGLE";
    public static final String TAG_SCENE_SUN_AZIMUTH = "SUN_AZIMUTH";
    public static final String TAG_SCENE_SUN_ELEVATION = "SUN_ELEVATION";
    // ...
    //TAG quality assessment


    // geoposition group -------------------------------------------------------
    public static final String TAG_GEOPOSITION = "Geoposition";
    public static final String TAG_GEOPOSITION_INSERT = "Geoposition_Insert";
    public static final String TAG_GEOPOSITION_POINTS = "Geoposition_Points";
    public static final String TAG_GEOPOSITION_AFFINE = "Geoposition_Affine";
    public static final String TAG_ULXMAP = "ULXMAP";
    public static final String TAG_ULYMAP = "ULYMAP";
    public static final String TAG_XDIM = "XDIM";
    public static final String TAG_YDIM = "YDIM";
    public static final String TAG_AFFINE_X0 = "AFFINE_X0";
    public static final String TAG_AFFINE_X1 = "AFFINE_X1";
    public static final String TAG_AFFINE_X2 = "AFFINE_X2";
    public static final String TAG_AFFINE_Y0 = "AFFINE_Y0";
    public static final String TAG_AFFINE_Y1 = "AFFINE_Y1";
    public static final String TAG_AFFINE_Y2 = "AFFINE_Y2";
    public static final String TAG_TIE_POINT = "Tie_Point";
    public static final String TAG_TIE_POINT_CRS_X = "TIE_POINT_CRS_X";
    public static final String TAG_TIE_POINT_CRS_Y = "TIE_POINT_CRS_Y";
    public static final String TAG_TIE_POINT_DATA_X = "TIE_POINT_DATA_X";
    public static final String TAG_TIE_POINT_DATA_Y = "TIE_POINT_DATA_Y";

    // raster dimensions group -------------------------------------------------
    public static final String TAG_RASTER_DIMENSIONS = "Raster_Dimensions";
    public static final String TAG_NCOLS = "NCOLS";
    public static final String TAG_NROWS = "NROWS";
    public static final String TAG_NBANDS = "NBANDS";

    // raster encoding group ---------------------------------------------------
    public static final String TAG_RASTER_ENCODING = "Raster_Encoding";
    public static final String TAG_NBITS = "NBITS";
    public static final String TAG_BYTEORDER = "BYTEORDER";
    public static final String TAG_DATA_TYPE = "DATA_TYPE";
    public static final String TAG_SKIP_BYTES = "SKIP_BYTES";
    public static final String TAG_BANDS_LAYOUT = "BANDS_LAYOUT";

    // CRS tag group -----------------------------------------------------------
    public static final String TAG_CRS = "Coordinate_Reference_System";
    public static final String TAG_HORIZONTAL_CS = "Horizontal_CS";
    public static final String TAG_HORIZONTAL_CS_CODE = "HORIZONTAL_CS_CODE";
    public static final String TAG_HORIZONTAL_CS_TYPE = "HORIZONTAL_CS_TYPE";
    public static final String TAG_HORIZONTAL_CS_NAME = "HORIZONTAL_CS_NAME";

    // RASTER CS tag group -----------------------------------------------------
    public static final String TAG_RASTER_CS = "Raster_CS";

    // Image display information -----------------------------------------------
    public static final String TAG_IMAGE_DISPLAY = "Image_Display";
    public static final String TAG_BAND_DISPLAY_ORDER = "Band_Display_Order";
    public static final String TAG_RED_CHANNEL = "RED_CHANNEL";
    public static final String TAG_GREEN_CHANNEL = "GREEN_CHANNEL";
    public static final String TAG_BLUE_CHANNEL = "BLUE_CHANNEL";
    public static final String TAG_SPECIAL_VALUE = "Special_Value";
    public static final String TAG_SPECIAL_VALUE_INDEX = "SPECIAL_VALUE_INDEX";
    public static final String TAG_SPECIAL_VALUE_TEXT = "SPECIAL_VALUE_TEXT";
    public static final String TAG_SPECIAL_VALUE_COLOR = "Special_Value_Color";
    public static final String TAG_RED_LEVEL = "RED_LEVEL";
    public static final String TAG_GREEN_LEVEL = "GREEN_LEVEL";
    public static final String TAG_BLUE_LEVEL = "BLUE_LEVEL";
    public static final String TAG_BAND_STATISTICS = "Band_Statistics";
    public static final String TAG_STX_MIN = "STX_MIN";
    public static final String TAG_STX_MAX = "STX_MAX";
    public static final String TAG_STX_MEAN = "STX_MEAN";
    public static final String TAG_STX_STDV = "STX_STDV";
    public static final String TAG_STX_LIN_MIN = "STX_LIN_MIN";
    public static final String TAG_STX_LIN_MAX = "STX_LIN_MAX";
    public static final String TAG_BAND_INDEX = "BAND_INDEX";


    // Image interpretation information ----------------------------------------
    public static final String TAG_IMAGE_INTERPRETATION = "Image_Interpretation";
    public static final String TAG_SPECTRAL_BAND_INFO = "Spectral_Band_Info";
    //public static final String TAG_BAND_INDEX = "BAND_INDEX";     //already declared
    public static final String TAG_BAND_DESCRIPTION = "BAND_DESCRIPTION";
    public static final String TAG_PHYSICAL_GAIN = "PHYSICAL_GAIN";
    public static final String TAG_PHYSICAL_BIAS = "PHYSICAL_BIAS";
    public static final String TAG_PHYSICAL_UNIT = "PHYSICAL_UNIT";

    //Data Access tag group ----------------------------------------------------
    public static final String TAG_DATA_ACCESS = "Data_Access";
    public static final String TAG_DATA_FILE_FORMAT = "DATA_FILE_FORMAT";
    public static final String ATT_VERSION = "version";

    //Data Processing tag group ------------------------------------------------
    public static final String TAG_DATA_PROCESSING = "Data_Processing";
    public static final String TAG_DATA_PROCESSING_PROCESSING_LEVEL = "PROCESSING_LEVEL";
    public static final String TAG_DATA_PROCESSING_GEOMETRIC_PROCESSING = "GEOMETRIC_PROCESSING";
    public static final String TAG_DATA_PROCESSING_RADIOMETRIC_PROCESSING = "RADIOMETRIC_PROCESSING";
    public static final String TAG_DATA_PROCESSING_ALGORITHM_TYPE = "ALGORITHM_TYPE";
    public static final String TAG_DATA_PROCESSING_ALGORITHM_NAME = "ALGORITHM_NAME";
    public static final String TAG_DATA_PROCESSING_ALGORITHM_ACTIVATION = "ALGORITHM_ACTIVATION";

    //Satellite_Time------------------------------------------------------------
    public static final String TAG_SATELLITE_TIME = "Satellite_Time";

    private DimapConstants(){}

}

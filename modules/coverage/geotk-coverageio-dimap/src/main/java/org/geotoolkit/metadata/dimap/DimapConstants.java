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

        private ByteOrder(java.nio.ByteOrder bo){
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

        private DataType(NumberSet n){
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

    // Scene frame group -------------------------------------------------------
    public static final String TAG_DATASET_FRAME = "Dataset_Frame";
    public static final String TAG_VERTEX = "Vertex";
    public static final String TAG_FRAME_LON = "FRAME_LON";
    public static final String TAG_FRAME_LAT = "FRAME_LAT";
    public static final String TAG_FRAME_ROW = "FRAME_ROW";
    public static final String TAG_FRAME_COL = "FRAME_COL";

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

    private DimapConstants(){}

}

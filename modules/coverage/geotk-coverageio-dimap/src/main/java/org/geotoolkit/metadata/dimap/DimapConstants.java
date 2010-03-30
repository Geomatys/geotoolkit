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

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class DimapConstants {

    public static final String RED = "red";
    public static final String GREEN = "green";
    public static final String BLUE = "blue";
    public static final String ALPHA = "alpha";
    

    ////////////////////////////////////////////////////////////////////////////
    // XML CONSTANTS ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static final String TAG_DIMAP = "Dimap_Document";

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

    private DimapConstants(){}

}

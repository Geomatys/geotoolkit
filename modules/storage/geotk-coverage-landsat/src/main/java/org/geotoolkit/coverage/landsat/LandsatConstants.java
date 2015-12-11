/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.coverage.landsat;

import org.apache.sis.util.Static;

/**
 * Reference all metadata fields name.<br><br>
 *
 * Values and commentary from document: <br>
 * LANDSAT 8 (L8)
 * LEVEL 1 (L1)
 * DATA FORMAT CONTROL BOOK (DFCB)
 *
 * @author Remi Marechal (Geomatys)
 * @version 1.0
 * @since 1.0
 */
final class LandsatConstants extends Static {

    /**
     * To define Global or General metadata.
     * Note : this label does not exist into Landsat specification.
     */
    public static final String GENERAL_LABEL      = "GENERAL";

    /**
     * To define REFLECTIVE coverage (band 1-7 and 9)
     */
    public static final String REFLECTIVE_LABEL   = "REFLECTIVE";

    /**
     * To define PANCHROMATIC coverage (band 8)
     */
    public static final String PANCHROMATIC_LABEL = "PANCHROMATIC";

    /**
     * To define THERMIC coverage (band 10-11)
     */
    public static final String THERMAL_LABEL      = "THERMAL";

    /**
     * The unique Landsat scene identifier.
     */
    public static final String SCENE_ID           = "LANDSAT_SCENE_ID";

    /**
     * The file name for Band n.
     * This parameter is only present if the band is included in the product.
     */
    public static final String BAND_NAME_LABEL    = "FILE_NAME_BAND_";

    /**
     * Minimum achievable reflectance or radiance value for Band n.
     * This parameter is only present if this band is included in the product.
     */
    public static final String MIN_LABEL          = "_MINIMUM_BAND_";

    /**
     * Maximum achievable reflectance or radiance value for Band n.
     * This parameter is only present if this band is included in the product.
     */
    public static final String MAX_LABEL          = "_MAXIMUM_BAND_";

    /**
     * Minimum possible pixel value for Band n.
     * This parameter is only present if this band is included in the product.
     */
    public static final String SAMPLE_MIN_LABEL   = "QUANTIZE_CAL_MIN_BAND_";

    /**
     * Maximum possible pixel value for Band n.
     * This parameter is only present if this band is included in the product.
     */
    public static final String SAMPLE_MAX_LABEL   = "QUANTIZE_CAL_MAX_BAND_";

    /**
     * The multiplicative rescaling factor used to convert calibrated DN to
     * Radiance units for Band n (W/(m^2 sr um)/DN).
     */
    public static final String SCALE_LABEL        = "_MULT_BAND_";

    /**
     * The additive rescaling factor used to convert calibrated DN to Radiance
     * units for Band n (W/(m^2 sr um).
     */
    public static final String OFFSET_LABEL       = "_ADD_BAND_";

    /**
     * The grid cell size in meters used in creating the image for the
     * (reflective, panchromatic or thermic) band(s), if part of the product.
     * This parameter is only included if the (reflective, panchromatic or thermic)
     * band(s) are (is) included in the product.
     */
    public static final String RESOLUTION_LABEL   = "GRID_CELL_SIZE_";

    /**
     * The number of product lines for the (reflective, panchromatic or thermic)  bands.
     * This parameter is only present if thermal bands are in the product.
     */
    public static final String LINES_LABEL        = "_LINES";

    /**
     * The number of product samples for the (reflective, panchromatic or thermic) bands.
     * This parameter is only present if reflective bands are in the product.
     */
    public static final String SAMPLES_LABEL      = "_SAMPLES";


}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.hips;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.geotoolkit.nio.IOUtilities;

/**
 * File aquired from .../properties URI
 *
 * @author Johann Sorel (Geomatys)
 * @see https://www.ivoa.net/documents/HiPS/20170519/REC-HIPS-1.0-20170519.pdf part 4.4.1
 */
public final class HIPSProperties extends HashMap<String,Object> {

    /**
     *  R Unique ID of the HiPS - Format: IVOID - Ex : ivo://CDS/P/2MASS/J
     */
    public static final String CREATOR_DID              = "creator_did";
    /**
     *  Unique ID of the HiPS publisher – Format: IVOID - Ex : ivo://CDS
     */
    public static final String PUBLISHER_ID             = "publisher_id";
    /**
     *  Short name of original data set – Format: one word – Ex : 2MASS
     */
    public static final String OBS_COLLECTION           = "obs_collection";
    /**
     *  R Data set title – Format: free text, one line – Ex : HST F110W observations
     */
    public static final String OBS_TITLE                = "obs_title";
    /**
     *  S Data set description – Format: free text, longer free text description of the dataset
     */
    public static final String OBS_DESCRIPTION          = "obs_description";
    /**
     *  Acknowledgment mention.
     */
    public static final String OBS_ACK                  = "obs_ack";
    /**
     *  (*) S Provenance of the original data – Format: free text
     */
    public static final String PROV_PROGENITOR          = "prov_progenitor";
    /**
     *  (*) Bibliographic reference
     */
    public static final String BIB_REFERENCE            = "bib_reference";
    /**
     *  (*) URL to bibliographic reference
     */
    public static final String BIB_REFERENCE_URL        = "bib_reference_url";
    /**
     *  Copyright mention associated to the original data – Format: free text
     */
    public static final String OBS_COPYRIGHT            = "obs_copyright";
    /**
     *  URL to a copyright mention
     */
    public static final String OBS_COPYRIGHT_URL        = "obs_copyright_url";
    /**
     *  Copyright mention associated to the HiPS – Format: free text
     */
    public static final String HIPS_COPYRIGHT           = "hips_copyright";
    /**
     *  (*) S General wavelength – Format: word: "Radio" | "Millimeter" | "Infrared" | "Optical" | "UV" | "EUV" | "X-ray" | "Gamma-ray"
     */
    public static final String OBS_REGIME               = "obs_regime";
    /**
     *  (*) UCD describing data contents
     */
    public static final String DATA_UCD                 = "data_ucd";
    /**
     *  R Number of HiPS version – Format: 1.4 (corresponds to this document)
     */
    public static final String HIPS_VERSION             = "hips_version";
    /**
     *  Name and version of the tool used for building the HiPS – Format: free text
     */
    public static final String HIPS_BUILDER             = "hips_builder";
    /**
     *  Institute or person who built the HiPS – Format: free text – Ex : CDS (T.Boch)
     */
    public static final String HIPS_CREATOR             = "hips_creator";
    /**
     *  S HiPS first creation date - Format: ISO 8601 => YYYY-mm-ddTHH:MMZ
     */
    public static final String HIPS_CREATION_DATE       = "hips_creation_date";
    /**
     *  R Last HiPS update date - Format: ISO 8601 => YYYY-mm-ddTHH:MMZ
     */
    public static final String HIPS_RELEASE_DATE        = "hips_release_date";
    /**
     *  HiPS access url – Format: URL
     */
    public static final String HIPS_SERVICE_URL         = "hips_service_url";
    /**
     *  R  HiPS status – Format: list of blank separated words (private” or “public”),  (“master”, “mirror”, or “partial”), (“clonable”, “unclonable” or “clonableOnce”) –     Default : public master clonableOnce
     */
    public static final String HIPS_STATUS              = "hips_status";
    /**
     *  HiPS size estimation – Format: positive integer – Unit : KB
     */
    public static final String HIPS_ESTSIZE             = "hips_estsize";
    /**
     *  R Coordinate frame reference – Format: word “equatorial” (ICRS), “galactic”, “ecliptic”
     */
    public static final String HIPS_FRAME               = "hips_frame";
    /**
     *  R Deepest HiPS order – Format: positive
     */
    public static final String HIPS_ORDER               = "hips_order";
    /**
     *  Tiles width in pixels – Format: positive integer – Default : 512
     */
    public static final String HIPS_TILE_WIDTH          = "hips_tile_width";
    /**
     *  R List of available tile formats. The first one is the default suggested to the client – Format: list of word blank separated: “jpeg”, “png”, “fits”, “tsv”
     */
    public static final String HIPS_TILE_FORMAT         = "hips_tile_format";
    /**
     *  Suggested pixel display cut range (physical values) – Format: min max – Ex : 10 300
     */
    public static final String HIPS_PIXEL_CUT           = "hips_pixel_cut";
    /**
     *  Pixel data range taken into account during the HiPS generation (physical values) – Format: min max – Ex : -18.5 510.5
     */
    public static final String HIPS_DATA_RANGE          = "hips_data_range";
    /**
     *  Sampling applied for the HiPS generation – Format: words “none”, “nearest”, “bilinear”
     */
    public static final String HIPS_SAMPLING            = "hips_sampling";
    /**
     *  Pixel composition method applied on the image overlay region during HiPS
     */
    public static final String HIPS_OVERLAY             = "hips_overlay";
    /**
     *  – Format: word “add”, “mean”, “first”, “border_fading”, “custom”
     */
    public static final String GENERATION               = "generation";
    /**
     *  Sky background subtraction method applied during HiPS generation – Format: word: “none”, “hips_estimation”, “fits_keyword”
     */
    public static final String HIPS_SKYVAL              = "hips_skyval";
    /**
     *  Fits tile BITPIX code – Format: -64, -32, 8, 16, 32, 64 (FITS convention)
     */
    public static final String HIPS_PIXEL_BITPIX        = "hips_pixel_bitpix";
    /**
     *  Original data BITPIX code - Format: -64, -32, 8, 16, 32, 64 (FITS convention)
     */
    public static final String DATA_PIXEL_BITPIX        = "data_pixel_bitpix";
    /**
     *  R Type of data – Format: word “image”, “cube”, “catalog”
     */
    public static final String DATAPRODUCT_TYPE         = "dataproduct_type";
    /**
     *  R Subtype of data – Format: word “color”, “live”
     */
    public static final String DATAPRODUCT_SUBTYPE      = "dataproduct_subtype";
    /**
     *  URL to an associated progenitor HiPS
     */
    public static final String HIPS_PROGENITOR_URL      = "hips_progenitor_url";
    /**
     *  S Number of rows of the HiPS catalog – Format: positive integer
     */
    public static final String HIPS_CAT_NROWS           = "hips_cat_nrows";
    /**
     *  R Number of frames of the HiPS cube – Format: positive integer
     */
    public static final String HIPS_CUBE_DEPTH          = "hips_cube_depth";
    /**
     *  Initial first index frame to display for a HiPS cube – Format: positive integer – Default :
     */
    public static final String HIPS_CUBE_FIRSTFRAME     = "hips_cube_firstframe";
    /**
     *  Coef for computing physical channel value (see FITS doc) – Format: real
     */
    public static final String DATA_CUBE_CRPIX3         = "data_cube_crpix3";
    /**
     *  Coef for computing physical channel value (see FITS doc) – Format: real
     */
    public static final String DATA_CUBE_CRVAL3         = "data_cube_crval3";
    /**
     *  Coef for computing physical channel value (see FITS doc) – Format: real
     */
    public static final String DATA_CUBE_CDELT3         = "data_cube_cdelt3";
    /**
     *  Third axis unit (see FITS doc) – Format: string
     */
    public static final String DATA_CUBE_BUNIT3         = "data_cube_bunit3";
    /**
     *  S Default RA display position – Format: real (ICRS frame) – Unit : degrees
     */
    public static final String HIPS_INITIAL_RA          = "hips_initial_ra";
    /**
     *  S Default DEC display position – Format: real (ICRS frame) – Unit : degrees
     */
    public static final String HIPS_INITIAL_DEC         = "hips_initial_dec";
    /**
     *  S Default display size – Format: real – Unit : degrees
     */
    public static final String HIPS_INITIAL_FOV         = "hips_initial_fov";
    /**
     *  HiPS pixel angular resolution at the highest order – Format: real – Unit : degrees
     */
    public static final String HIPS_PIXEL_SCALE         = "hips_pixel_scale";
    /**
     *  Best pixel angular resolution of the original images – Format: real – Unit : degrees
     */
    public static final String S_PIXEL_SCALE            = "s_pixel_scale";
    /**
     *  S Start time of the observations – Format: real – Representation: MJD2
     */
    public static final String T_MIN                    = "t_min";
    /**
     *  S Stop time of the observations – Format: real – Representation: MJD
     */
    public static final String T_MAX                    = "t_max";
    /**
     *  S Start in spectral coordinates – Format: real – Unit: meters
     */
    public static final String EM_MIN                   = "em_min";
    /**
     *  S Stop in spectral coordinates – Format: real – Unit: meters
     */
    public static final String EM_MAX                   = "em_max";
    /**
     *  '/' separated keywords suggesting a display hierarchy to the client – Ex :Image/InfraRed
     */
    public static final String CLIENT_CATEGORY          = "client_category";
    /**
     *  Sort key suggesting a display order to the client inside a “client_category” – Format: free text – Sort : alphanumeric
     */
    public static final String CLIENT_SORT_KEY          = "client_sort_key";
    /**
     * (*) In case of “live” HiPS, creator_did of the added HiPS
     */
    public static final String ADDENDUM_DID             = "addendum_did";
    /**
     *  Fraction of the sky covers by the MOC associated to the HiPS – Format: real between 0 and 1
     */
    public static final String MOC_SKY_FRACTION         = "moc_sky_fraction";

    // OTHER properties found

    /**
     *  Highest HIPS order – Format: positive
     */
    public static final String HIPS_ORDER_MIN           = "hips_order_min";


    public void append(String line) {
        final int split = line.indexOf('=');
        final String key = line.substring(0, split).trim();
        final String value = line.substring(split+1).trim();

        Object previous = get(key);
        if (previous == null) {
            put(key, value);
        } else if (previous instanceof String p) {
            final List<String> multipleOcc = new ArrayList<>();
            multipleOcc.add(p);
            multipleOcc.add(value);
            put(key, multipleOcc);
        } else if (previous instanceof List lst) {
            lst.add(value);
        } else {
            throw new IllegalArgumentException("Unexpected case, previous value is a " + previous.getClass().getName());
        }
    }

    public void read(InputStream in) throws IOException {
        final String string = IOUtilities.toString(in);
        final String[] lines = string.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isBlank() || line.startsWith("#")) {
                //empty or comment, ignore it
            } else {
                append(line);
            }
        }
    }
}

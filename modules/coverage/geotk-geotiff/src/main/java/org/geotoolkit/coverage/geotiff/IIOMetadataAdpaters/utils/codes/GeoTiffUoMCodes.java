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
 * @author Simone Giannecchini
 * @module pending
 * @since 2.3
 *
 */
public final class GeoTiffUoMCodes {

    public static final int ANGULAR_ARC_MINUTE = 9103;
    public static final int ANGULAR_ARC_SECOND = 9104;
    public static final int ANGULAR_DEGREE = 9102;
    public static final int ANGULAR_DMS = 9107;
    public static final int ANGULAR_DMS_HEIMSPHERE = 9108;
    public static final int ANGULAR_GON = 9106;
    public static final int ANGULAR_GRAD = 9105;
    /**
     * 6.3.1.4 Angular Units Codes These codes shall be used for any key that
     * requires specification of an angular unit of measurement.
     */
    public static final int ANGULAR_RADIAN = 9101;
    public static final int LINEAR_CHAIN_BENOIT = 9010;
    public static final int LINEAR_CHAIN_SEARS = 9011;
    public static final int LINEAR_FATHOM = 9014;
    public static final int LINEAR_FOOT = 9002;
    public static final int LINEAR_FOOT_CLARKE = 9005;
    public static final int LINEAR_FOOT_INDIAN = 9006;
    public static final int LINEAR_FOOT_MODIFIED_AMERICAN = 9004;
    public static final int LINEAR_FOOT_US_SURVEY = 9003;
    public static final int LINEAR_LINK = 9007;
    public static final int LINEAR_LINK_BENOIT = 9008;
    public static final int LINEAR_LINK_SEARS = 9009;
    /**
     * 6.3.1.3 Linear Units Codes There are several different kinds of units
     * that may be used in geographically related raster data: linear units,
     * angular units, units of time (e.g. for radar-return), CCD-voltages, etc.
     * For this reason there will be a single, unique range for each kind of
     * unit, broken down into the following currently defined ranges: Ranges: 0 =
     * undefined [ 1, 2000] = Obsolete GeoTIFFWritingUtilities codes [2001,
     * 8999] = Reserved by GeoTIFFWritingUtilities [9000, 9099] = EPSG Linear
     * Units. [9100, 9199] = EPSG Angular Units. 32767 = user-defined unit
     * [32768, 65535]= Private User Implementations
     */
    public static final int LINEAR_METER = 9001;
    public static final int LINEAR_MILE_INTERNATIONAL_NAUTICAL = 9015;
    public static final int LINEAR_YARD_INDIAN = 9013;
    public static final int LINEAR_YARD_SEARS = 9012;

    private GeoTiffUoMCodes() {
    }
}

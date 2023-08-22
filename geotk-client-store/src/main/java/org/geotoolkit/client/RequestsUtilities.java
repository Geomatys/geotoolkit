/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.client;

import java.awt.Color;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.opengis.geometry.Envelope;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.util.FactoryException;

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.util.GeodeticObjectBuilder;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.CommonCRS;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.apache.sis.util.Utilities;

/**
 * Utilities methods that provide conversion functionnalities between real objects and queries
 * (in both ways).
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public final class RequestsUtilities {
    /**
     * List of supported mime-types.
     */
    private static final Set<String> SUPPORTED_FORMATS = new HashSet<String>(Arrays.asList(ImageIO.getWriterMIMETypes()));
    static {
        SUPPORTED_FORMATS.addAll(Arrays.asList(ImageIO.getWriterFormatNames()));
    }

    private RequestsUtilities() {}

    /**
     * Returns a string representation of the {@code Bounding Box}. It is a comma-separated
     * list matching with this pattern: minx,miny,maxx,maxy.
     *
     * @param envelope The envelope to return the string representation.
     */
    public static String toBboxValue(final Envelope envelope) {
        final StringBuilder builder = new StringBuilder();
        final int dimEnv = envelope.getDimension();
        for (int i=0; i<dimEnv; i++) {
            builder.append(envelope.getMinimum(i)).append(',');
        }
        for (int j=0; j<dimEnv; j++) {
            if (j>0) {
                builder.append(',');
            }
            builder.append(envelope.getMaximum(j));
        }
        return builder.toString();
    }

    /**
     * Parse a boolean from a string value.
     */
    public static boolean toBoolean(final String strTransparent) {
        if (strTransparent == null) {
            return false;
        }
        return Boolean.parseBoolean(strTransparent.trim());
    }

    /**
     * Get the {@link Color} object matching with the given string.
     *
     * @param background A string representing the color. It will be given to
     *                   {@link Color#decode(String)}.
     * @return The color object, or {@code null} if the given string is {@code null}.
     * @throws NumberFormatException if the specified string cannot be interpreted
     *                               as a decimal, octal, or hexidecimal integer.
     */
    public static Color toColor(String background) throws NumberFormatException{
        Color color = null;
        if (background != null) {
            background = background.trim();
            color = Color.decode(background);
        }
        return color;
    }

    /**
     * Returns the CRS code for the specified envelope, or {@code null} if not found.
     *
     * @param envelope The envelope to return the CRS code.
     */
    public static String toCrsCode(final Envelope envelope) {
        if (Utilities.equalsIgnoreMetadata(envelope.getCoordinateReferenceSystem(), CommonCRS.WGS84.normalizedGeographic())) {
            return "EPSG:4326";
        }
        final Set<Identifier> identifiers = envelope.getCoordinateReferenceSystem().getIdentifiers();
        if (identifiers != null && !identifiers.isEmpty()) {
            return identifiers.iterator().next().toString();
        }
        return null;
    }

    /**
     * Extends the {@link Double#valueOf(String)} method, in removing the leading
     * and trailing whitespace.
     *
     * @param value A string representing a double value.
     * @return A double representation of the given string, or {@code NaN} if the
     *         given string is null.
     * @throws NumberFormatException if the string can't be parsed as an double value.
     */
    public static double toDouble(String value) throws NumberFormatException {
        if (value == null) {
            return Double.NaN;
        }
        value = value.trim();
        return Double.parseDouble(value);
    }

    /**
     * Converts a string representing the bbox coordinates into a {@link GeneralEnvelope}.
     *
     * @param bbox Coordinates of the bounding box, seperated by comas.
     * @param crs  The {@linkplain CoordinateReferenceSystem coordinate reference system} in
     *             which the envelope is expressed. Must not be {@code null}.
     * @return The enveloppe for the bounding box specified, or an
     *         {@linkplain GeneralEnvelope#setToInfinite infinite envelope}
     *         if the bbox is {@code null}.
     * @throws IllegalArgumentException if the given CRS is {@code null}, or if the bbox string
     *                                  contains too much parameters to fill the CRS ranges.
     */
    public static Envelope toEnvelope(final String bbox, final CoordinateReferenceSystem crs)
                                                              throws IllegalArgumentException
    {
        if (crs == null) {
            throw new IllegalArgumentException("The CRS must not be null");
        }
        if (bbox == null) {
            final GeneralEnvelope infinite = new GeneralEnvelope(crs);
            infinite.setToInfinite();
            return infinite;
        }

        final GeneralEnvelope envelope = new GeneralEnvelope(crs);
        final int dimension = envelope.getDimension();
        final StringTokenizer tokens = new StringTokenizer(bbox, ",;");
        final double[] coordinates = new double[dimension * 2];

        int index = 0;
        while (tokens.hasMoreTokens()) {
            final double value = toDouble(tokens.nextToken());
            if (index >= coordinates.length) {
                throw new IllegalArgumentException(
                        Errors.format(Errors.Keys.IllegalCsDimension_1, coordinates.length));
            }
            coordinates[index++] = value;
        }
        if ((index & 1) != 0) {
            throw new IllegalArgumentException(
                    Errors.format(Errors.Keys.OddArrayLength_1, index));
        }
        // Fallthrough in every cases.
        switch (index) {
            default: {
                while (index >= 6) {
                    final double maximum = coordinates[--index];
                    final double minimum = coordinates[--index];
                    envelope.setRange(index >> 1, minimum, maximum);
                }
            }
            case 4: envelope.setRange(1, coordinates[1], coordinates[3]);
            case 3:
            case 2: envelope.setRange(0, coordinates[0], coordinates[2]);
            case 1:
            case 0: break;
        }
        /*
         * Checks the envelope validity. Given that the parameter order in the bounding box
         * is a little-bit counter-intuitive, it is worth to perform this check in order to
         * avoid a NonInvertibleTransformException at some later stage.
         */
        for (index = 0; index < dimension; index++) {
            final double minimum = envelope.getMinimum(index);
            final double maximum = envelope.getMaximum(index);
            if (!(minimum < maximum)) {
                throw new IllegalArgumentException(
                        Errors.format(Errors.Keys.IllegalRange_2, minimum, maximum));
            }
        }
        return envelope;
    }

    /**
     * Converts a string representing the bbox coordinates into a {@link GeneralEnvelope}.
     *
     * @param bbox Coordinates of the bounding box, seperated by comas.
     * @param crs  The {@linkplain CoordinateReferenceSystem coordinate reference system} in
     *             which the envelope is expressed. Must not be {@code null}.
     * @return The enveloppe for the bounding box specified, or an
     *         {@linkplain GeneralEnvelope#setToInfinite infinite envelope}
     *         if the bbox is {@code null}.
     */
    public static Envelope toEnvelope(final String bbox, final CoordinateReferenceSystem crs,
                    final String strElevation, final String strTime)
                    throws IllegalArgumentException, ParseException, FactoryException {

        final CoordinateReferenceSystem horizontalCRS = CRS.getHorizontalComponent(crs);
        final VerticalCRS               verticalCRS;
        final TemporalCRS               temporalCRS;
        final double[] dimX = new double[]{Double.NaN,Double.NaN};
        final double[] dimY = new double[]{Double.NaN,Double.NaN};
        final double[] dimZ = new double[]{Double.NaN,Double.NaN};
        final double[] dimT = new double[]{Double.NaN,Double.NaN};


        //parse bbox -----------------------------------------------------------
        if (bbox == null) {
            //set to infinity
            dimX[0] = dimY[0] = Double.NEGATIVE_INFINITY;
            dimX[1] = dimY[1] = Double.POSITIVE_INFINITY;
        } else {
            final StringTokenizer tokens = new StringTokenizer(bbox, ",;");
            final double[] values = new double[4];
            int index = 0;
            while (tokens.hasMoreTokens()) {
                    values[index] = toDouble(tokens.nextToken());
                if (index >= 4) {
                    throw new IllegalArgumentException(Errors.format(Errors.Keys.IndexOutOfBounds_1, index));
                }
                index++;
            }

            if (index != 5) {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.IllegalArgument_1, index));
            }

            dimX[0] = values[0];
            dimX[1] = values[2];
            dimY[0] = values[1];
            dimY[1] = values[3];
        }

        //parse elevation ------------------------------------------------------
        if (strElevation != null) {
            final double elevation = toDouble(strElevation);
            dimZ[0] = dimZ[1] = elevation;

            final VerticalCRS zCRS = CRS.getVerticalComponent(crs, true);
            verticalCRS = (zCRS != null) ? zCRS : CommonCRS.Vertical.MEAN_SEA_LEVEL.crs();

        } else {
            verticalCRS = null;
        }

        //parse temporal -------------------------------------------------------
        if (strTime != null) {
            final Date date = TemporalUtilities.parseDateSafe(strTime,true);
            final TemporalCRS tCRS = CRS.getTemporalComponent(crs);
            temporalCRS = (tCRS != null) ? tCRS : CommonCRS.Temporal.MODIFIED_JULIAN.crs();

            dimT[0] = dimT[1] = ((DefaultTemporalCRS)temporalCRS).toValue(date);

        } else {
            temporalCRS = null;
        }

        //create the 2/3/4 D BBox ----------------------------------------------
        if (verticalCRS != null && temporalCRS != null) {
            final CoordinateReferenceSystem finalCRS = new GeodeticObjectBuilder().addName("rendering bbox")
                                                                                  .createCompoundCRS(horizontalCRS,
                                                                                                     verticalCRS,
                                                                                                     temporalCRS);
            final GeneralEnvelope envelope = new GeneralEnvelope(finalCRS);
            envelope.setRange(0, dimX[0], dimX[1]);
            envelope.setRange(1, dimY[0], dimY[1]);
            envelope.setRange(2, dimZ[0], dimZ[1]);
            envelope.setRange(3, dimT[0], dimT[1]);
            return envelope;
        } else if(verticalCRS != null) {
            final CoordinateReferenceSystem finalCRS = new GeodeticObjectBuilder().addName("rendering bbox")
                                                                                  .createCompoundCRS(horizontalCRS, verticalCRS);

            final GeneralEnvelope envelope = new GeneralEnvelope(finalCRS);
            envelope.setRange(0, dimX[0], dimX[1]);
            envelope.setRange(1, dimY[0], dimY[1]);
            envelope.setRange(2, dimZ[0], dimZ[1]);
            return envelope;
        } else if(temporalCRS != null) {
            final CoordinateReferenceSystem finalCRS = new GeodeticObjectBuilder().addName("rendering bbox")
                                                                                  .createCompoundCRS(horizontalCRS, temporalCRS);
            final GeneralEnvelope envelope = new GeneralEnvelope(finalCRS);
            envelope.setRange(0, dimX[0], dimX[1]);
            envelope.setRange(1, dimY[0], dimY[1]);
            envelope.setRange(2, dimT[0], dimT[1]);
            return envelope;
        } else {
            final GeneralEnvelope envelope = new GeneralEnvelope(horizontalCRS);
            envelope.setRange(0, dimX[0], dimX[1]);
            envelope.setRange(1, dimY[0], dimY[1]);
            return envelope;
        }

    }

    /**
     * Returns the format if it is a known format, that can be used for writting.
     *
     * @param format The format chosen, as a string.
     * @return The supported format, or {@code null} if the given format is {@code null}.
     * @throws IllegalArgumentException if the given string is not a known format.
     */
    public static String toFormat(String format) throws IllegalArgumentException {
        if (format == null) {
            return null;
        }
        format = format.trim();
        if (!SUPPORTED_FORMATS.contains(format)) {
            throw new IllegalArgumentException("Invalid format specified.");
        }
        return format;
    }

    /**
     * Extends the {@link Integer#valueOf(String)} method, in removing the leading
     * and trailing whitespace.
     *
     * @param value A string representing an integer value.
     * @return An integer representation of the given string.
     * @throws NumberFormatException if the given value is {@code null}, or if the
     *                               string can't be parsed as an integer value.
     */
    public static int toInt(String value) throws NumberFormatException {
        if (value == null) {
            throw new NumberFormatException("Int value not defined.");
        }
        value = value.trim();
        return Integer.parseInt(value);
    }

    /**
     * Takes a string with the pattern: beginVal,lastVal/otherVal.
     * That string will be splitted in order to return a list of ranges.
     *
     * @param ranges A string representing a list of ranges.
     */
    public static List<Double[]> toCategoriesRange(final String ranges) {
        final List<Double[]> exts = new ArrayList<Double[]>();
        final String[] blocks = ranges.split("/");

        for(final String block : blocks){
            final String[] parts = block.split(",");

            if(parts.length == 1){
                //single value range
                final Double d = Double.valueOf(parts[0]);
                exts.add( new Double[]{d,d} ) ;
            }else if(parts.length == 2){
                //interval range
                final Double d1 = Double.valueOf(parts[0]);
                final Double d2 = Double.valueOf(parts[1]);
                exts.add( new Double[]{d1,d2} ) ;
            }else{
                //not possible, invalid string
                throw new IllegalArgumentException("Range definition is not valid : " + ranges);
            }
        }

        Collections.sort(exts, new Comparator<Double[]>(){
            @Override
            public int compare(Double[] t, Double[] t1) {
                double res = t[0] - t1[0];
                if (res < 0) {
                    return -1;
                }
                if (res > 0) {
                    return 1;
                }
                res = t[1] - t1[1];
                if (res < 0) {
                    return -1;
                }
                if (res > 0) {
                    return 1;
                }
                return 0;
            }
        });

        return exts;
    }
}

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.referencing.factory.web;

import javax.measure.unit.Unit;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.apache.sis.measure.Units;
import org.apache.sis.measure.Latitude;
import org.apache.sis.measure.Longitude;
import org.geotoolkit.resources.Errors;


/**
 * A code parsed by the {@link AutoCRSFactory} methods.
 * See the {@link AutoCRSFactory} javadoc for a description of the expected syntax.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 2.2
 * @module
 */
final class Code {
    /**
     * The maximal amount of numeric fields that we expect after the {@code "AUTO:"} part.
     * The expected fields are (code, unit, longitude, latitude).
     */
    private static final int MAXIMUM_FIELDS = 4;

    /**
     * The authority name. Should usually be {@code AUTO}.
     */
    public final String authority;

    /**
     * The code number.
     */
    public final int code;

    /**
     * The unit of measurement.
     */
    public final Unit<?> unit;

    /**
     * The central longitude.
     */
    public final double longitude;

    /**
     * The central latitude.
     */
    public final double latitude;

    /**
     * The type of the CRS to be constructed (e.g. {@code GeographicCRS.class}).
     * Used only in case of failure for constructing an error message.
     */
    final Class<? extends CoordinateReferenceSystem> type;

    /**
     * Parses the code string to retrieve the code number and central longitude / latitude.
     * Assumed format is {@code AUTO:code,lon0,lat0} where {@code AUTO} is optional.
     *
     * @param  text The code in the {@code AUTO:code,lon0,lat0} format.
     * @param  The type of the CRS to be constructed (e.g. {@code GeographicCRS.class}).
     *         Used only in case of failure for constructing an error message.
     * @param  mandatory {@code true} if all mandatory fields must be present.
     * @throws NoSuchAuthorityCodeException if the specified code can't be parsed.
     */
    public Code(final String text, final Class<? extends CoordinateReferenceSystem> type,
            final boolean mandatory) throws NoSuchAuthorityCodeException
    {
        /*
         * Extract the authority, which is optional.
         */
        String authority = "AUTO";
        int splitAt = text.indexOf(':');
        if (splitAt > 0) {
            authority = text.substring(0, splitAt).trim();
        }
        /*
         * Get the indices where to split the string. Throw the
         * exception immediately if there is too many fields.
         */
        final int[] splitIndices = new int[MAXIMUM_FIELDS + 1];
        int fieldCount = 0;
        do {
            if (fieldCount >= MAXIMUM_FIELDS) {
                throw noSuchAuthorityCode(type, authority, text, splitIndices);
            }
            splitIndices[fieldCount++] = ++splitAt;
            splitAt = text.indexOf(',', splitAt);
        } while (splitAt >= 0);
        splitIndices[fieldCount] = text.length() + 1;
        /*
         * If there is some missing fields, guess which ones are the missing ones
         * and offset the array of indices in order to put the missing field at
         * the proper place.
         */
        switch (MAXIMUM_FIELDS - fieldCount) {
            case 0: {
                // No field are missing.
                break;
            }
            case 1: {
                // Exactly one field is missing. Assume that this is the unit field.
                // This field was not present in WMS 1.0, and added in WMS 1.1.
                System.arraycopy(splitIndices, 1, splitIndices, 2, MAXIMUM_FIELDS - 1);
                break;
            }
            default: {
                if (!mandatory) break;
                // Too many fields are missing.
                throw noSuchAuthorityCode(type, authority, text, splitIndices);
            }
        }
        /*
         * Parse the fields.
         */
        int    code      = 0;
        int    unit      = 9001;
        double longitude = Double.NaN;
        double latitude  = Double.NaN;
parse:  for (int i=0; i<MAXIMUM_FIELDS; i++) {
            splitAt = splitIndices[i];
            final int end = splitIndices[i+1] - 1;
            if (end > splitAt) {
                final String field = text.substring(splitAt, end).trim();
                if (!field.isEmpty()) try {
                    switch (i) {
                        default: throw new AssertionError(i);
                        case 0:  code      = Integer.parseInt  (field); break;
                        case 1:  unit      = Integer.parseInt  (field); break;
                        case 2:  longitude = Double.parseDouble(field); break;
                        case 3:  latitude  = Double.parseDouble(field); break parse;
                        /*
                         * Add case statements here if there is more fields to parse.
                         * Only the last case should end with 'break parse' instead of 'break'.
                         */
                    }
                } catch (NumberFormatException exception) {
                    // If a number can't be parsed, then this is an invalid authority code.
                    NoSuchAuthorityCodeException e = noSuchAuthorityCode(type, authority, text, splitIndices);
                    e.initCause(exception);
                    throw e;
                }
            }
        }
        if (mandatory &&
            !(longitude >= Longitude.MIN_VALUE && longitude <= Longitude.MAX_VALUE &&
              latitude  >=  Latitude.MIN_VALUE && latitude  <=  Latitude.MAX_VALUE))
        {
            // A longitude or latitude is out of range, or was not present
            // (i.e. the field still has a NaN value).
            throw noSuchAuthorityCode(type, authority, text, splitIndices);
        }
        this.authority = authority;
        this.code      = code;
        this.unit      = Units.valueOfEPSG(unit);
        this.longitude = longitude;
        this.latitude  = latitude;
        this.type      = type;
        if (this.unit == null) {
            throw new NoSuchAuthorityCodeException(Errors.format(Errors.Keys.UnknownUnit_1, unit),
                    authority, text.substring(splitIndices[0]).trim(), text);
        }
    }

    /**
     * Creates an exception for an unknown authority code.
     *
     * @param  type         The GeoAPI interface that was to be created.
     * @param  authority    Either {@code "AUTO"} or {@code "AUTO2"}.
     * @param  identifier   The text that we were trying to parse.
     * @param  splitIndices The indices where to split. Only the first one is used.
     * @return An exception initialized with an error message built from the specified informations.
     */
    private static NoSuchAuthorityCodeException noSuchAuthorityCode(final Class<?> type,
            final String authority, final String identifier, final int[] splitIndices)
    {
        final String code = identifier.substring(splitIndices[0]).trim();
        return new NoSuchAuthorityCodeException(Errors.format(Errors.Keys.NoSuchAuthorityCode_3,
                code, authority, type), authority, code, identifier);
    }

    /**
     * Returns a string representation of this code.
     */
    @Override
    public String toString(){
        return authority + ':' + code + ',' + longitude + ',' + latitude;
    }
}

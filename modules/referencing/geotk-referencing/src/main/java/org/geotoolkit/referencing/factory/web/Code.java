/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.measure.Latitude;
import org.geotoolkit.measure.Longitude;
import org.geotoolkit.resources.Errors;


/**
 * A code parsed by the {@link AutoCRSFactory} methods.
 * The expected format is {@code AUTO:code,lon0,lat0} where {@code AUTO} is optional.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
final class Code {
    /**
     * The authority name. Should usually be {@code AUTO}.
     */
    public final String authority;

    /**
     * The code number.
     */
    public final int code;

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
     * Parses the code string to retrive the code number and central longitude / latitude.
     * Assumed format is {@code AUTO:code,lon0,lat0} where {@code AUTO} is optional.
     *
     * @param  text The code in the {@code AUTO:code,lon0,lat0} format.
     * @param  The type of the CRS to be constructed (e.g. {@code GeographicCRS.class}).
     *         Used only in case of failure for constructing an error message.
     * @throws NoSuchAuthorityCodeException if the specified code can't be parsed.
     */
    public Code(final String text, final Class<? extends CoordinateReferenceSystem> type)
            throws NoSuchAuthorityCodeException
    {
        String authority = "AUTO";
        int    code      = 0;
        double longitude = Double.NaN;
        double latitude  = Double.NaN;
        int startField   = -1;
parse:  for (int i=0; /*stop condition in the 'switch' statement below*/; i++) {
            final char delimiter = (i == 0) ? ':' : ',';
            int endField = text.indexOf(delimiter, ++startField);
            if (endField < 0) {
                if (i == 0) {
                    // The "AUTO" prefix is optional. Continue the search for next fields.
                    startField = -1;
                    continue;
                }
                endField = text.length();
            }
            if (endField <= startField) {
                // A required field was not found.
                throw noSuchAuthorityCode(type, text);
            }
            final String field = text.substring(startField, endField).trim();
            try {
                switch (i) {
                    default: throw new AssertionError(i);
                    case 0:  authority =                    field;  break;
                    case 1:  code      = Integer.parseInt  (field); break;
                    case 2:  longitude = Double.parseDouble(field); break;
                    case 3:  latitude  = Double.parseDouble(field); break parse;
                    /*
                     * Add case statements here if the is more fields to parse.
                     * Only the last case should end with 'break parse' instead of 'break'.
                     */
                }
            } catch (NumberFormatException exception) {
                // If a number can't be parsed, then this is an invalid authority code.
                NoSuchAuthorityCodeException e = noSuchAuthorityCode(type, text);
                e.initCause(exception);
                throw e;
            }
            startField = endField;
        }
        if (!(longitude >= Longitude.MIN_VALUE && longitude <= Longitude.MAX_VALUE &&
              latitude  >=  Latitude.MIN_VALUE && latitude  <=  Latitude.MAX_VALUE))
        {
            // A longitude or latitude is out of range, or was not present
            // (i.e. the field still has a NaN value).
            throw noSuchAuthorityCode(type, text);
        }
        this.authority = authority;
        this.code      = code;
        this.longitude = longitude;
        this.latitude  = latitude;
        this.type      = type;
    }

    /**
     * Creates an exception for an unknow authority code.
     *
     * @param  type  The GeoAPI interface that was to be created.
     * @param  code  The unknow authority code.
     * @return An exception initialized with an error message built from the specified informations.
     */
    private static NoSuchAuthorityCodeException noSuchAuthorityCode(final Class<?> type, final String code) {
        final String authority = "AUTO";
        return new NoSuchAuthorityCodeException(Errors.format(Errors.Keys.NO_SUCH_AUTHORITY_CODE_$3,
                code, authority, type), authority, code);
    }

    /**
     * Returns a string representation of this code.
     */
    @Override
    public String toString(){
        return authority + ':' + code + ',' + longitude + ',' + latitude;
    }
}

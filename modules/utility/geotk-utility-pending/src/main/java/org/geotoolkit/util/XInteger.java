/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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

package org.geotoolkit.util;

/**
 * Extend Integer class with paring capabilities within a CharSequence.
 * This avoid calling substring on each part we need.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class XInteger {

    private static final int RADIX = 10;
    private static final int UNSIGNED_LIMIT = -Integer.MAX_VALUE;
    private static final int UNSIGNED_MULMIN = UNSIGNED_LIMIT / RADIX;

    private XInteger(){}

    public static final int parseIntSigned(CharSequence str, int i, int max) throws NumberFormatException {
        if (str == null) {
            throw new NumberFormatException("null");
        }

        int result = 0;
        boolean negative = false;
        int limit;
        int multmin;
        int digit;

        if (max > 0) {
            if (str.charAt(i) == '-') {
                negative = true;
                limit = Integer.MIN_VALUE;
                i++;
            } else {
                limit = -Integer.MAX_VALUE;
            }
            multmin = limit / RADIX;
            if (i < max) {
                digit = Character.digit(str.charAt(i++), RADIX);
                if (digit < 0) {
                    throw new NumberFormatException("Unvalid integer string :" + str.subSequence(i, max));
                } else {
                    result = -digit;
                }
            }
            while (i < max) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit(str.charAt(i++), RADIX);
                if (digit < 0) {
                    throw new NumberFormatException("Unvalid integer string :" + str.subSequence(i, max));
                }
                if (result < multmin) {
                    throw new NumberFormatException("Unvalid integer string :" + str.subSequence(i, max));
                }
                result *= RADIX;
                if (result < limit + digit) {
                    throw new NumberFormatException("Unvalid integer string :" + str.subSequence(i, max));
                }
                result -= digit;
            }
        } else {
            throw new NumberFormatException("Unvalid integer string :" + str.subSequence(i, max));
        }
        if (negative) {
            if (i > 1) {
                return result;
            } else {	/* Only got "-" */
                throw new NumberFormatException("Unvalid integer string :" + str.subSequence(i, max));
            }
        } else {
            return -result;
        }
    }

    public static final int parseIntUnsigned(CharSequence str, int i, int max) throws NumberFormatException {
        if (str == null) {
            throw new NumberFormatException("null");
        }

        int result = 0;
        int digit;

        if (i < max) {

            digit = Character.digit(str.charAt(i++), RADIX);
            if (digit < 0) {
                throw new NumberFormatException("Unvalid integer string :" + str.subSequence(i, max));
            } else {
                result = -digit;
            }

            while (i < max) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit(str.charAt(i++), RADIX);
                if (digit < 0) {
                    throw new NumberFormatException("Unvalid integer string :" + str.subSequence(i, max));
                }
                if (result < UNSIGNED_MULMIN) {
                    throw new NumberFormatException("Unvalid integer string :" + str.subSequence(i, max));
                }
                result *= RADIX;
                if (result < UNSIGNED_LIMIT + digit) {
                    throw new NumberFormatException("Unvalid integer string :" + str.subSequence(i, max));
                }
                result -= digit;
            }
        } else {
            throw new NumberFormatException("Unvalid integer string :" + str.subSequence(i, max));
        }

        return -result;
    }


}

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
package org.geotoolkit.data.dbf;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility for formatting Dbase fields.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DbaseFieldFormatter {

    private StringBuffer buffer = new StringBuffer(255);
    private NumberFormat numFormat = NumberFormat.getNumberInstance(Locale.US);
    private Calendar calendar = Calendar.getInstance(Locale.US);
    private String emptyString;
    private static final int MAXCHARS = 255;
    private Charset charset;

    public DbaseFieldFormatter(final Charset charset) {
        // Avoid grouping on number format
        numFormat.setGroupingUsed(false);

        // build a 255 white spaces string
        StringBuffer sb = new StringBuffer(MAXCHARS);
        sb.setLength(MAXCHARS);
        for (int i = 0; i < MAXCHARS; i++) {
            sb.setCharAt(i, ' ');
        }

        this.charset = charset;

        emptyString = sb.toString();
    }

    public String getFieldString(final int size, final String s) {
        try {
            buffer.replace(0, size, emptyString);
            buffer.setLength(size);
            // international characters must be accounted for so size != length.
            int maxSize = size;
            if (s != null) {
                buffer.replace(0, size, s);
                int currentBytes = s.substring(0, Math.min(size, s.length())).getBytes(charset.name()).length;
                if (currentBytes > size) {
                    char[] c = new char[1];
                    for (int index = size - 1; currentBytes > size; index--) {
                        c[0] = buffer.charAt(index);
                        String string = new String(c);
                        buffer.deleteCharAt(index);
                        currentBytes -= string.getBytes().length;
                        maxSize--;
                    }
                } else {
                    if (s.length() < size) {
                        maxSize = size - (currentBytes - s.length());
                        for (int i = s.length(); i < size; i++) {
                            buffer.append(' ');
                        }
                    }
                }
            }

            buffer.setLength(maxSize);

            return buffer.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("This error should never occurr", e);
        }
    }

    public String getFieldString(final Date d) {

        if (d != null) {
            buffer.delete(0, buffer.length());

            calendar.setTime(d);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // returns 0
            // based month?
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            if (year < 1000) {
                if (year >= 100) {
                    buffer.append("0");
                } else if (year >= 10) {
                    buffer.append("00");
                } else {
                    buffer.append("000");
                }
            }
            buffer.append(year);

            if (month < 10) {
                buffer.append("0");
            }
            buffer.append(month);

            if (day < 10) {
                buffer.append("0");
            }
            buffer.append(day);
        } else {
            buffer.setLength(8);
            buffer.replace(0, 8, emptyString);
        }

        buffer.setLength(8);
        return buffer.toString();
    }

    public String getFieldString(final int size, final int decimalPlaces, final Number n) {
        buffer.delete(0, buffer.length());

        if (n != null) {
            numFormat.setMaximumFractionDigits(decimalPlaces);
            numFormat.setMinimumFractionDigits(decimalPlaces);
            numFormat.format(n, buffer, new FieldPosition(
                    NumberFormat.INTEGER_FIELD));
        }

        int diff = size - buffer.length();
        if (diff >= 0) {
            while (diff-- > 0) {
                buffer.insert(0, ' ');
            }
        } else {
            buffer.setLength(size);
        }
        return buffer.toString();
    }
}

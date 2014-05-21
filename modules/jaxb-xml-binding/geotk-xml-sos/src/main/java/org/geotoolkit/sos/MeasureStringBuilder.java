/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.sos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.geotoolkit.swe.xml.TextBlock;
import static org.geotoolkit.swe.xml.v200.TextEncodingType.DEFAULT_ENCODING;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class MeasureStringBuilder {
    
    private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private final TextBlock encoding = DEFAULT_ENCODING;
    
    private final StringBuilder sb = new StringBuilder();
    
    public void appendDate(final Date d) {
        synchronized(FORMATTER) {
            sb.append(FORMATTER.format(d)).append(encoding.getTokenSeparator());
        }
    }
    
    public void appendDate(final long millis) {
        final Date d = new Date(millis);
        synchronized(FORMATTER) {
            sb.append(FORMATTER.format(d)).append(encoding.getTokenSeparator());
        }
    }
    
    public void appendValue(final Double value) {
        //empty string for missing value
        if (!Double.isNaN(value)) {
            sb.append(value);
        }
        sb.append(encoding.getTokenSeparator());
    }
    
    public void closeBlock() {
         // remove the last token separator
        sb.deleteCharAt(sb.length() - 1);
        sb.append(encoding.getBlockSeparator());
    }
    
    public String getString() {
        return sb.toString();
    }
}

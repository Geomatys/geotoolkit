/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.s57;

import static org.geotoolkit.data.s57.S57Constants.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class S57Structures {
    
    private S57Structures(){}
    
    /** The domain for ASCII data is specified by a domain code */
    public static enum Domain{
        /** Basic text (see clause 2.4) */
        BASIC_TEXT("bt"),
        /** General text (see clause 2.4) */
        GENERAL_TEXT("gt"),
        /** digits; 0-9, right-adjusted and zero filled left (e.g. A(2) "03") */
        DIGITS("dg"),
        /** a date subfield in the form: YYYYMMDD (e.g. "19960101") */
        DATE("date"),
        /** integer; ISO 6093 NR1, SPACE, "+", "-", 0-9, right-adjusted and zero filled left (e.g. I(5) “00015”) */
        INT("int"),
        /** real number : ISO 6093 NR2, SPACE, "+", "-", ".", 0-9 */
        REAL("real"),
        /** alphanumerics; A-Z, a-z, 0-9, "*", "?" */
        ALPHANUM("an"),
        /** hexadecimals; A-F, 0-9 */
        HEXADEC("hex");
        
        private final String code;

        private Domain(String code) {
            this.code = code;
        }
    }
    
}

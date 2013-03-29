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
package org.geotoolkit.data.s57.iso8211;

import java.io.IOException;

/**
 * ISO8211 constants.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class ISO8211Constants {
    
    /** End of field : UT */
    public static final char FEND = '\u001F';
    /** End of Subfield : FT */
    public static final char SFEND = '\u001E';

    /** DDR/DR fields length */                                       // DDR          DR
    public static final int R_RECORD_LENGTH = 5;                     //
    public static final int R_INTERCHANGE_LEVEL = 1;                  // "3"          SPACE
    public static final int R_LEADER_IDENTIFIER = 1;                   // "L"          "D"
    public static final int R_EXTENSION_INDICATOR = 1;                // "E"          SPACE
    public static final int R_VERSION_NUMBER = 1;                     // "1"          SPACE
    public static final int R_APPLICATION_INDICATOR = 1;               // SPACE        SPACE
    public static final int R_FIELD_CONTROL_LENGHT = 2;               // "09"         2 SPACEs
    public static final int R_AREA_ADDRESS = 5;                       // number of bytes in leader and directory
    public static final int R_CHARSET_INDICATOR = 3;                  // " ! "       3 SPACES
    
    /** DDR/DR map entry fields length*/
    public static final int E_LENGTH = 1;        //
    public static final int E_POSITION = 1;       //
    public static final int E_RESERVED = 1;      // "0"
    public static final int E_TAG = 1;           // "4"
    public static final int FC_STRUCTURE_LINEAR = 1;
    public static final int FC_STRUCTURE_MULTI = 2;
    public static final int FC_TYPE_STRING = 0;
    public static final int FC_TYPE_INTEGER = 1;
    public static final int FC_TYPE_BINARY = 5;
    public static final int FC_TYPE_MIXE = 6;
    public static final byte[] FC_CONTROL = new byte[]{'0','0',';','&'};
    public static final byte[] FC_LEXICAL_0 = new byte[]{' ',' ',' '};
    public static final byte[] FC_LEXICAL_1 = new byte[]{'-','A',' '};
    public static final byte[] FC_LEXICAL_2 = new byte[]{'%','/','A'};
                       

    
    private ISO8211Constants(){}
    
}

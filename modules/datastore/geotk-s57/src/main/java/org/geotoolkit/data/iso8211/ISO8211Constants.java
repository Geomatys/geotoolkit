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
package org.geotoolkit.data.iso8211;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * ISO8211 constants.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class ISO8211Constants {
    
    public static final Charset US_ASCII;
    static {
        US_ASCII = Charset.forName("US-ASCII");
    }
    
    public enum LeaderIdentifier{
        /** Means that the record is the Data Descriptive Record */
        L('L'),
        /** Means that the record is a Data Record and that the next data
         * record has a leader and directory */
        D('D'),
        /** Means that the record is a Data Record and that a leader and direc-
         * tory will not be found in any of the subsequent LRs. The leader and
         * directory of the current LR shall be applied to each of the subse-
         * quent LRs. */
        R('R');
        
        private final byte c;

        private LeaderIdentifier(char c) {
            this.c = (byte)c;
        }
        
        public static LeaderIdentifier fromCode(byte val) throws IOException{
            for(LeaderIdentifier l : values()){
                if(l.c == val) return l;
            }
            throw new IOException("Unknown leader identifier : "+(char)val);
        }
        
        public byte toCode(){
            return c;
        }        
    }
    
    public enum InterchangeLevel{
        UNSET(' '),
        LEVEL_1('1'),
        LEVEL_2('2'),
        LEVEL_3('3');
        
        private final byte c;

        private InterchangeLevel(char c) {
            this.c = (byte) c;
        }
        
        public static InterchangeLevel fromCode(byte val) throws IOException{
            for(InterchangeLevel l : values()){
                if(l.c == val) return l;
            }
            throw new IOException("Unknown interchange level : "+(char)val);
        }
        
        public byte toCode(){
            return c;
        }        
    }
    
    public enum FieldDataStructure {
        /** the dimension of the data structure is zero, i.e., a single data item */
        ELEMENTARY('0'), 
        /** the dimension of the data structure is one, i.e., a linear structure */
        LINEAR('1'), 
        /** the dimension of the data structure is equal to or greater than two, 
         * i.e., a multi-dimensional structure */
        CARTESIAN('2'),
        /** the data structure is concatenated */
        CONCATENATED('3');

        private final byte c;

        private FieldDataStructure(int value) {
            this.c = (byte) value;
        }

        public static FieldDataStructure fromCode(byte val) throws IOException {
            for(FieldDataStructure l : values()){
                if(l.c == val) return l;
            }
            throw new IOException("Unknown data structure : "+(char)val);
        }

        public byte toCode() {
            return c;
        }

    }
    
    public enum FieldDataType {
        /** Character string */
        CHARSTRING('0'), 
        /** Implicit point */
        IMP_POINT('1'), 
        /** explicit point */
        EXP_POINT('2'), 
        /** explicit point scaled */
        EXP_POINT_SCALED('3'), 
        /** Character mode bit string */
        CHARBITSTRING('4'), 
        /** bit string including binary forms */
        BITSTRING('5'), 
        /** miwed data types */
        MIXED('6');

        private final byte c;

        private FieldDataType(int value) {
            this.c = (byte) value;
        }

        public static FieldDataType fromCode(byte val) throws IOException {
            for(FieldDataType l : values()){
                if(l.c == val) return l;
            }
            throw new IOException("Unknown data type : "+(char)val);
        }

        public byte toCode() {
            return c;
        }

    }
    
    
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
                       
    public static final String DELIMITER_SUBFIELD = "!";
    public static final String DELIMITER_VECTOR = "*";
    public static final String DELIMITER_ARRAY = "\\";
    public static final String DELIMITER_TYPE = ",";
    

    
    private ISO8211Constants(){}
    
}

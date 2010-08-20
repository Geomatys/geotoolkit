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

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Calendar;
import java.util.Date;

import org.geotoolkit.util.XInteger;

/**
 * Class for holding the information assicated with a record.
 *
 * @author Johann Sorel (Geomatys)
 */
abstract class DbaseField {

    private static final Number NULL_NUMBER = new Integer(0);
    private static final String NULL_STRING = "";
    private static final Date NULL_DATE = new Date();

    public static DbaseField create(String fieldName, char fieldType, int fieldDataAddress,
                        int fieldLength, int decimalCount, Class clazz) throws IOException{

        switch (fieldType) {

            // (L)logical (T,t,F,f,Y,y,N,n)
            case 'l':
            case 'L':
                return new BooleanField(fieldName, fieldType, fieldDataAddress, fieldLength, decimalCount,clazz);

            // (C)character (String)
            case 'c':
            case 'C':
                return new CharField(fieldName, fieldType, fieldDataAddress, fieldLength, decimalCount,clazz);

            // (D)date (Date)
            case 'd':
            case 'D':
                return new DateField(fieldName, fieldType, fieldDataAddress, fieldLength, decimalCount,clazz);

            // (N)Numeric (Integer/Long)
            case 'n':
            case 'N':
                if(clazz == Integer.class){
                    return new IntegerField(fieldName, fieldType, fieldDataAddress, fieldLength, decimalCount,clazz);
                }else if(clazz == Long.class){
                    return new LongField(fieldName, fieldType, fieldDataAddress, fieldLength, decimalCount,clazz);
                }
            case 'f':
            case 'F': // floating point number
                return new FloatingField(fieldName, fieldType, fieldDataAddress, fieldLength, decimalCount,clazz);
            default:
                throw new IOException("Invalid field type : " + fieldType);
        }

    }

    /**
     * Create a copy of hte DbaseField with a different adress
     */
    public static DbaseField create(DbaseField field, int fieldDataAddress){
        if(field instanceof BooleanField){
            return new BooleanField(field.fieldName, field.fieldType, fieldDataAddress,
                field.fieldLength, field.decimalCount, field.clazz);
        }else if(field instanceof CharField){
            return new CharField(field.fieldName, field.fieldType, fieldDataAddress,
                field.fieldLength, field.decimalCount, field.clazz);
        }else if(field instanceof DateField){
            return new DateField(field.fieldName, field.fieldType, fieldDataAddress,
                field.fieldLength, field.decimalCount, field.clazz);
        }else if(field instanceof IntegerField){
            return new IntegerField(field.fieldName, field.fieldType, fieldDataAddress,
                field.fieldLength, field.decimalCount, field.clazz);
        }else if(field instanceof LongField){
            return new LongField(field.fieldName, field.fieldType, fieldDataAddress,
                field.fieldLength, field.decimalCount, field.clazz);
        }else if(field instanceof FloatingField){
            return new FloatingField(field.fieldName, field.fieldType, fieldDataAddress,
                field.fieldLength, field.decimalCount, field.clazz);
        }else{
            throw new IllegalArgumentException("DBaseField unknowned : " + field);
        }
    }


    //todo should be set final

    // Field Name
    public final String fieldName;

    // Field Type (C N L D or M)
    public final char fieldType;

    // Field Data Address offset from the start of the record.
    public final int fieldDataAddress;

    // Length of the data in bytes
    public final int fieldLength;

    // Field decimal count in Binary, indicating where the decimal is
    public final int decimalCount;

    // Class
    public final Class clazz;

    private DbaseField(String fieldName, char fieldType, int fieldDataAddress,
                        int fieldLength, int decimalCount, Class clazz) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldDataAddress = fieldDataAddress;
        this.fieldLength = fieldLength;
        this.decimalCount = decimalCount;
        this.clazz = clazz;
    }


    protected CharSequence extractNumberString(final CharBuffer charBuffer2,
            int fieldOffset) {
        final int fieldLen = fieldOffset+fieldLength;
        while(fieldOffset<fieldLen && charBuffer2.charAt(fieldOffset) <= ' ') fieldOffset++;
        return charBuffer2.subSequence(fieldOffset, fieldLen);
    }

    public abstract Object read(CharBuffer buffer, int offset) throws IOException;

    public abstract String string(Object obj, DbaseFieldFormatter formatter) throws IOException;


    private static final class BooleanField extends DbaseField{

        public BooleanField(String fieldName, char fieldType, int fieldDataAddress,
                        int fieldLength, int decimalCount, Class clazz) {
            super(fieldName, fieldType, fieldDataAddress, fieldLength, decimalCount,clazz);
        }

        @Override
        public Object read(CharBuffer charBuffer, int fieldOffset) throws IOException {
            switch (charBuffer.charAt(fieldOffset)) {
                case 't':
                case 'T':
                case 'Y':
                case 'y':
                    return Boolean.TRUE;
                case 'f':
                case 'F':
                case 'N':
                case 'n':
                    return Boolean.FALSE;
                default:
                    throw new IOException("Unknown logical value : '"
                            + charBuffer.charAt(fieldOffset) + "'");
                }
        }

        @Override
        public String string(Object obj, DbaseFieldFormatter formatter) throws IOException {
            return (obj == null ? "F" : obj == Boolean.TRUE ? "T" : "F");
        }

    }

    private static final class CharField extends DbaseField{

        public CharField(String fieldName, char fieldType, int fieldDataAddress,
                        int fieldLength, int decimalCount, Class clazz) {
            super(fieldName, fieldType, fieldDataAddress, fieldLength, decimalCount,clazz);
        }

        @Override
        public Object read(CharBuffer charBuffer, int fieldOffset) throws IOException {
            // oh, this seems like a lot of work to parse strings...but,
            // For some reason if zero characters ( (int) char == 0 ) are allowed
            // in these strings, they do not compare correctly later on down
            // the line....

            int start = fieldOffset;
            int end = fieldOffset + fieldLength - 1;
            charBuffer.limit(charBuffer.capacity());
            // trim off whitespace and 'zero' chars
            while (start < end) {
                char c = charBuffer.get(start);
                if (c == 0 || Character.isWhitespace(c)) {
                    start++;
                } else
                    break;
            }
            while (end > start) {
                char c = charBuffer.get(end);
                if (c == 0 || Character.isWhitespace(c)) {
                    end--;
                } else
                    break;
            }
            // set up the new indexes for start and end
            charBuffer.position(start).limit(end + 1);
            String s = charBuffer.toString();
            charBuffer.clear();
            return s;
        }

        @Override
        public String string(Object obj, DbaseFieldFormatter formatter) throws IOException {
            return formatter.getFieldString(fieldLength, obj == null ? NULL_STRING
                    : obj.toString());
        }

    }

    private static final class DateField extends DbaseField{

        public DateField(String fieldName, char fieldType, int fieldDataAddress,
                        int fieldLength, int decimalCount, Class clazz) {
            super(fieldName, fieldType, fieldDataAddress, fieldLength, decimalCount,clazz);
        }

        @Override
        public Object read(CharBuffer charBuffer, int fieldOffset) throws IOException {
            try {
                String tempString = charBuffer.subSequence(fieldOffset,
                        fieldOffset + 4).toString();
                final int tempYear = Integer.parseInt(tempString);
                tempString = charBuffer.subSequence(fieldOffset + 4,
                        fieldOffset + 6).toString();
                final int tempMonth = Integer.parseInt(tempString) - 1;
                tempString = charBuffer.subSequence(fieldOffset + 6,
                        fieldOffset + 8).toString();
                final int tempDay = Integer.parseInt(tempString);
                final Calendar cal = Calendar.getInstance();
                cal.clear();
                cal.set(Calendar.YEAR, tempYear);
                cal.set(Calendar.MONTH, tempMonth);
                cal.set(Calendar.DAY_OF_MONTH, tempDay);
                return cal.getTime();
            } catch (NumberFormatException nfe) {
                // todo: use progresslistener, this isn't a grave error.
                return null;
            }
        }

        @Override
        public String string(Object obj, DbaseFieldFormatter formatter) throws IOException {
            return formatter.getFieldString((Date) (obj == null ? NULL_DATE : obj));
        }

    }

    private static final class IntegerField extends DbaseField{

        private static final Long ZERO = 0l;

        public IntegerField(String fieldName, char fieldType, int fieldDataAddress,
                        int fieldLength, int decimalCount, Class clazz) {
            super(fieldName, fieldType, fieldDataAddress, fieldLength, decimalCount,clazz);
        }

        @Override
        public Object read(CharBuffer charBuffer, int fieldOffset) throws IOException {
            try {
                final CharSequence number = extractNumberString(charBuffer,
                        fieldOffset);
                return XInteger.parseIntSigned(number, 0, number.length());
                // else will fall through to the floating point number
            } catch (NumberFormatException e) {
                // Lets try parsing a long instead...
                try {
                    return Long.valueOf(extractNumberString(charBuffer, fieldOffset).toString());
                } catch (NumberFormatException e2) {
                    return ZERO;
                }
            }
        }

        @Override
        public String string(Object obj, DbaseFieldFormatter formatter) throws IOException {
            return formatter.getFieldString(fieldLength, 0,(Number) (obj == null ? NULL_NUMBER : obj));
        }

    }

    private static final class LongField extends DbaseField{

        private static final Long ZERO = 0l;

        public LongField(String fieldName, char fieldType, int fieldDataAddress,
                        int fieldLength, int decimalCount, Class clazz) {
            super(fieldName, fieldType, fieldDataAddress, fieldLength, decimalCount,clazz);
        }

        @Override
        public Object read(CharBuffer charBuffer, int fieldOffset) throws IOException {
            try {
                final CharSequence number = extractNumberString(charBuffer,
                        fieldOffset);
                return Long.valueOf(number.toString());
                // else will fall through to the floating point number
            } catch (NumberFormatException e) {
                return ZERO;
            }
        }

        @Override
        public String string(Object obj, DbaseFieldFormatter formatter) throws IOException {
            return formatter.getFieldString(fieldLength, 0,(Number) (obj == null ? NULL_NUMBER : obj));
        }
    }

    private static final class FloatingField extends DbaseField{

        private static final Double ZERO = 0d;

        public FloatingField(String fieldName, char fieldType, int fieldDataAddress,
                        int fieldLength, int decimalCount, Class clazz) {
            super(fieldName, fieldType, fieldDataAddress, fieldLength, decimalCount,clazz);
        }

        @Override
        public Object read(CharBuffer charBuffer, int fieldOffset) throws IOException {
            try {
                return Double.valueOf(extractNumberString(charBuffer, fieldOffset).toString());
            } catch (NumberFormatException e) {
                // todo: use progresslistener, this isn't a grave error,
                // though it does indicate something is wrong
                // okay, now whatever we got was truly undigestable. Lets go
                // with a zero Double.
                return ZERO;
            }
        }

        @Override
        public String string(Object obj, DbaseFieldFormatter formatter) throws IOException {
            return formatter.getFieldString(fieldLength, decimalCount,
                    (Number) (obj == null ? NULL_NUMBER : obj));
        }

    }

}

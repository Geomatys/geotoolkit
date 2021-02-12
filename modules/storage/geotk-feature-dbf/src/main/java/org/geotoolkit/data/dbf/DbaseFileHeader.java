/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This file is based on an origional contained in the GISToolkit project:
 *    http://gistoolkit.sourceforge.net/
 */
package org.geotoolkit.data.dbf;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.feature.Features;
import org.geotoolkit.feature.SingleAttributeTypeBuilder;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.util.logging.Logging;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;


/**
 * Class to represent the header of a Dbase III file.
 *
 * Creation date: (5/15/2001 5:15:30 PM)
 *
 * TODO: remove all Buffer cast after migration to JDK9.
 *
 * @module
 */
public class DbaseFileHeader {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.shapefile");

    // Constant for the size of a record
    private static final int FILE_DESCRIPTOR_SIZE = 32;

    // type of the file, must be 03h
    private static final byte MAGIC = 0x03;

    private static final int MINIMUM_HEADER = 33;

    // Date the file was last updated.
    private Date date = new Date();

    private int recordCnt = 0;

    private int fieldCnt = 0;

    // set this to a default length of 1, which is enough for one "space"
    // character which signifies an empty record
    private int recordLength = 1;

    // set this to a flagged value so if no fields are added before the write,
    // we know to adjust the headerLength to MINIMUM_HEADER
    private int headerLength = -1;

    private int largestFieldSize = 0;


    // collection of header records.
    // lets start out with a zero-length array, just in case
    private DbaseField[] fields = new DbaseField[0];

    private void read(final ByteBuffer buffer, final ReadableByteChannel channel) throws IOException {
        while (buffer.remaining() > 0) {
            if (channel.read(buffer) == -1) {
                throw new EOFException("Premature end of file");
            }
        }
    }

    /**
     * Determine the most appropriate Java Class for representing the data in
     * the field.
     *
     * <PRE>
     * All packages are java.lang unless otherwise specified.
     * C (Character) -&gt; String
     * N (Numeric)   -&gt; Integer or Double (depends on field's decimal count)
     * F (Floating)  -&gt; Double
     * L (Logical)   -&gt; Boolean
     * D (Date)      -&gt; java.util.Date
     * Unknown       -&gt; String
     * </PRE>
     *
     * @param i
     *                The index of the field, from 0 to
     *                <CODE>getNumFields() - 1</CODE> .
     * @return A Class which closely represents the dbase field type.
     */
    public Class getFieldClass(final int i) {
        return getFieldClass(fields[i].fieldType, fields[i].decimalCount, fields[i].fieldLength);
    }

    private static Class getFieldClass(final char fieldType, final int decimalCount, final int fieldLength) {
        final Class typeClass;

        switch (fieldType) {
            case 'C':
                typeClass = String.class;
                break;

            case 'N':
                if (decimalCount == 0) {
                    if (fieldLength < 10) {
                        typeClass = Integer.class;
                    } else {
                        typeClass = Long.class;
                    }
                } else {
                    typeClass = Double.class;
                }
                break;

            case 'F':
                typeClass = Double.class;
                break;

            case 'L':
                typeClass = Boolean.class;
                break;

            case 'D':
                typeClass = Date.class;
                break;

            default:
                typeClass = String.class;
                break;
        }

        return typeClass;
    }

    DbaseField getField(final int index){
        return fields[index];
    }


    /**
     * Add a column to this DbaseFileHeader. The type is one of (C N L or D)
     * character, number, logical(true/false), or date. The Field length is the
     * total length in bytes reserved for this column. The decimal count only
     * applies to numbers(N), and floating point values (F), and refers to the
     * number of characters to reserve after the decimal point. <B>Don't expect
     * miracles from this...</B>
     *
     * <PRE>
     * Field Type MaxLength
     * ---------- ---------
     * C          254
     * D          8
     * F          20
     * N          18
     * </PRE>
     *
     * @param inFieldName
     *                The name of the new field, must be less than 10 characters
     *                or it gets truncated.
     * @param inFieldType
     *                A character representing the dBase field, ( see above ).
     *                Case insensitive.
     * @param inFieldLength
     *                The length of the field, in bytes ( see above )
     * @param inDecimalCount
     *                For numeric fields, the number of decimal places to track.
     * @throws DbaseFileException
     *                 If the type is not recognized.
     */
    public void addColumn(String inFieldName, char inFieldType,
            int inFieldLength, int inDecimalCount) throws DbaseFileException {
        if (inFieldLength <= 0) {
            throw new DbaseFileException("field length <= 0");
        }
        if (fields == null) {
            fields = new DbaseField[0];
        }
        int tempLength = 1; // the length is used for the offset, and there is a
                            // * for deleted as the first byte
        final DbaseField[] tempFieldDescriptors = new DbaseField[fields.length + 1];
        for (int i = 0; i < fields.length; i++) {
            tempFieldDescriptors[i] = DbaseField.create(fields[i],tempLength);
            tempLength += fields[i].fieldLength;
        }

        // set the field name
        if (inFieldName == null) {
            inFieldName = "NoName";
        }
        // Fix for GEOT-42, ArcExplorer will not handle field names > 10 chars
        // Sorry folks.
        if (inFieldName.length() > 10) {
            inFieldName = inFieldName.substring(0, 10);
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "FieldName {0} is longer than 10 characters, truncating to {1}", new Object[]{inFieldName, inFieldName});
            }
        }

        // the field type
        if ((inFieldType == 'C') || (inFieldType == 'c')) {
            inFieldType = 'C';
            if (inFieldLength > 254) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Field Length for {0} set to {1} Which is longer than 254, not consistent with dbase III", new Object[]{inFieldName, inFieldLength});
                }
            }
        } else if ((inFieldType == 'S') || (inFieldType == 's')) {
            inFieldType = 'C';
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "Field type for {0} set to S which is flat out wrong people!, I am setting this to C, in the hopes you meant character.", inFieldName);
            }
            if (inFieldLength > 254) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Field Length for {0} set to {1} Which is longer than 254, not consistent with dbase III", new Object[]{inFieldName, inFieldLength});
                }
            }
            inFieldLength = 8;
        } else if ((inFieldType == 'D') || (inFieldType == 'd')) {
            inFieldType = 'D';
            if (inFieldLength != 8) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Field Length for {0} set to {1} Setting to 8 digets YYYYMMDD", new Object[]{inFieldName, inFieldLength});
                }
            }
            inFieldLength = 8;
        } else if ((inFieldType == 'F') || (inFieldType == 'f')) {
            inFieldType = 'F';
            if (inFieldLength > 20) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Field Length for {0} set to {1} Preserving length, but should be set to Max of 20 not valid for dbase IV, and UP specification, not present in dbaseIII.", new Object[]{inFieldName, inFieldLength});
                }
            }
        } else if ((inFieldType == 'N') || (inFieldType == 'n')) {
            inFieldType = 'N';
            if (inFieldLength > 18) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Field Length for {0} set to {1} Preserving length, but should be set to Max of 18 for dbase III specification.", new Object[]{inFieldName, inFieldLength});
                }
            }
            if (inDecimalCount < 0) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Field Decimal Position for {0} set to {1} Setting to 0 no decimal data will be saved.", new Object[]{inFieldName, inDecimalCount});
                }
                inDecimalCount = 0;
            }
            if (inDecimalCount > inFieldLength - 1) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "Field Decimal Position for {0} set to {1} Setting to {2} no non decimal data will be saved.", new Object[]{inFieldName, inDecimalCount, inFieldLength - 1});
                }
                inDecimalCount = inFieldLength - 1;
            }
        } else if ((inFieldType == 'L') || (inFieldType == 'l')) {
            inFieldType = 'L';
            if (inFieldLength != 1) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Field Length for {0} set to {1} Setting to length of 1 for logical fields.", new Object[]{inFieldName, inFieldLength});
                }
            }
            inFieldLength = 1;
        } else {
            throw new DbaseFileException("Undefined field type " + inFieldType
                    + " For column " + inFieldName);
        }

        try {
            tempFieldDescriptors[fields.length] = DbaseField.create(
                    inFieldName, inFieldType, tempLength, inFieldLength, inDecimalCount,
                    getFieldClass(inFieldType, inDecimalCount, inFieldLength));
        } catch (IOException ex) {
            throw new DbaseFileException("Invalid field declaration", ex);
        }

        // the length of a record
        tempLength = tempLength + tempFieldDescriptors[fields.length].fieldLength;

        // set the new fields.
        fields = tempFieldDescriptors;
        fieldCnt = fields.length;
        headerLength = MINIMUM_HEADER + 32 * fields.length;
        recordLength = tempLength;
    }

    /**
     * Remove a column from this DbaseFileHeader.
     *
     * @todo This is really ugly, don't know who wrote it, but it needs fixin...
     * @param inFieldName
     *                The name of the field, will ignore case and trim.
     * @return index of the removed column, -1 if no found
     */
    public int removeColumn(final String inFieldName) throws IOException {

        int retCol = -1;
        int tempLength = 1;
        DbaseField[] tempFieldDescriptors = new DbaseField[fields.length - 1];
        for (int i = 0, j = 0; i < fields.length; i++) {
            if (!inFieldName.equalsIgnoreCase(fields[i].fieldName.trim())) {
                // if this is the last field and we still haven't found the
                // named field
                if (i == j && i == fields.length - 1) {
                    System.err.println("Could not find a field named '"
                            + inFieldName + "' for removal");
                    return retCol;
                }
                tempFieldDescriptors[j] = DbaseField.create(
                        fields[i].fieldName,
                        fields[i].fieldType,
                        tempLength,
                        fields[i].fieldLength,
                        fields[i].decimalCount,
                        fields[i].clazz);
                tempLength += tempFieldDescriptors[j].fieldLength;
                // only increment j on non-matching fields
                j++;
            } else {
                retCol = i;
            }
        }

        // set the new fields.
        fields = tempFieldDescriptors;
        headerLength = 33 + 32 * fields.length;
        recordLength = tempLength;

        return retCol;
    }

    /**
     * Returns the field length in bytes.
     *
     * @param inIndex
     *                The field index.
     * @return The length in bytes.
     */
    public int getFieldLength(final int inIndex) {
        return fields[inIndex].fieldLength;
    }

    /**
     * Get the decimal count of this field.
     * location of the decimal point within the field.
     *
     * @param inIndex
     *                The field index.
     * @return The decimal count.
     */
    public int getFieldDecimalCount(final int inIndex) {
        return fields[inIndex].decimalCount;
    }

    /**
     * Get the field name.
     *
     * @param inIndex
     *                The field index.
     * @return The name of the field.
     */
    public String getFieldName(final int inIndex) {
        return fields[inIndex].fieldName;
    }

    /**
     * Get the character class of the field.
     *
     * @param inIndex
     *                The field index.
     * @return The dbase character representing this field.
     */
    public char getFieldType(final int inIndex) {
        return fields[inIndex].fieldType;
    }

    /**
     * Get the field offset from the record start position.
     * First field will start at 1, the first byte is for the deleted flag.
     *
     * @param inIndex
     *                The field index.
     * @return fild offset
     */
    public int getFieldOffset(final int inIndex) {
        int offset = 1;
        for (int x = 0, n = inIndex; x < n; x++) {
            offset += fields[x].fieldLength;
        }
        return offset;
    }

    /**
     * Get the date this file was last updated.
     *
     * @return The Date last modified.
     */
    public Date getLastUpdateDate() {
        return date;
    }

    /**
     * Return the number of fields in the records.
     *
     * @return The number of fields in this table.
     */
    public int getNumFields() {
        return fields.length;
    }

    /**
     * Return the number of records in the file
     *
     * @return The number of records in this table.
     */
    public int getNumRecords() {
        return recordCnt;
    }

    /**
     * Get the length of the records in bytes.
     *
     * @return The number of bytes per record.
     */
    public int getRecordLength() {
        return recordLength;
    }

    /**
     * Get the length of the header
     *
     * @return The length of the header in bytes.
     */
    public int getHeaderLength() {
        return headerLength;
    }

    /**
     * Read the header data from the DBF file.
     *
     * @param channel
     *                A readable byte channel. If you have an InputStream you
     *                need to use, you can call
     *                java.nio.Channels.getChannel(InputStream in).
     * @throws IOException
     *                 If errors occur while reading.
     */
    public void readHeader(final ReadableByteChannel channel) throws IOException {
        // we'll read in chunks of 1K
        ByteBuffer in = ByteBuffer.allocate(1024);
        // do this or GO CRAZY
        // ByteBuffers come preset to BIG_ENDIAN !
        in.order(ByteOrder.LITTLE_ENDIAN);

        // only want to read first 10 bytes...
        ((Buffer) in).limit(10);

        read(in, channel);
        ((Buffer) in).position(0);

        // type of file.
        byte magic = in.get();
        if (magic != MAGIC) {
            throw new IOException("Unsupported DBF file Type "
                    + Integer.toHexString(magic));
        }

        // parse the update date information.
        int tempUpdateYear = in.get();
        int tempUpdateMonth = in.get();
        int tempUpdateDay = in.get();
        // ouch Y2K uncompliant
        if (tempUpdateYear > 90) {
            tempUpdateYear = tempUpdateYear + 1900;
        } else {
            tempUpdateYear = tempUpdateYear + 2000;
        }
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, tempUpdateYear);
        c.set(Calendar.MONTH, tempUpdateMonth - 1);
        c.set(Calendar.DATE, tempUpdateDay);
        date = c.getTime();

        // read the number of records.
        recordCnt = in.getInt();

        // read the length of the header structure.
        // ahhh.. unsigned little-endian shorts
        // mask out the byte and or it with shifted 2nd byte
        headerLength = (in.get() & 0xff) | ((in.get() & 0xff) << 8);

        // if the header is bigger than our 1K, reallocate
        if (headerLength > in.capacity()) {
            in = ByteBuffer.allocate(headerLength - 10);
        }
        ((Buffer) in).limit(headerLength - 10);
        ((Buffer) in).position(0);
        read(in, channel);
        ((Buffer) in).position(0);

        // read the length of a record
        // ahhh.. unsigned little-endian shorts
        recordLength = (in.get() & 0xff) | ((in.get() & 0xff) << 8);

        // skip / skip thesreserved bytes in the header.
        ((Buffer) in).position(in.position() + 20);

        // calculate the number of Fields in the header
        fieldCnt = (headerLength - FILE_DESCRIPTOR_SIZE - 1)
                / FILE_DESCRIPTOR_SIZE;

        // read all of the header records
        final Set<String> names = new HashSet<String>();
        final List<DbaseField> lfields = new ArrayList<DbaseField>();
        for (int i = 0; i < fieldCnt; i++) {

            // read the field name
            byte[] buffer = new byte[11];
            in.get(buffer);
            String name = new String(buffer);
            int nullPoint = name.indexOf(0);
            if (nullPoint != -1) {
                name = name.substring(0, nullPoint);
            }
            String fieldName = name.trim();
            if(names.contains(fieldName)){
                int inc = 2;
                while(names.contains(fieldName+inc)){
                    inc++;
                }
                LOGGER.log(Level.INFO, "DBF : multiple fields for name {0} , one will be exposed as {1}{2}", new Object[]{fieldName, fieldName, inc});
                fieldName = fieldName+inc;
            }

            // read the field type
            char fieldType = (char) in.get();

            // read the field data address, offset from the start of the record.
            int fieldDataAddress = in.getInt();

            // read the field length in bytes
            int length = (int) in.get();
            if (length < 0) {
                length = length + 256;
            }
            int fieldLength = length;

            if (length > largestFieldSize) {
                largestFieldSize = length;
            }

            // read the field decimal count in bytes
            int decimalCount = (int) in.get();

            // reserved bytes.
            // in.skipBytes(14);
            ((Buffer) in).position(((Buffer) in).position() + 14);

            // some broken shapefiles have 0-length attributes. The reference
            // implementation
            // (ArcExplorer 2.0, built with MapObjects) just ignores them.
            final DbaseField field = DbaseField.create(fieldName, fieldType,
                        fieldDataAddress, fieldLength, decimalCount,
                        getFieldClass(fieldType, decimalCount, fieldLength));
            if (field.fieldLength > 0) {
                lfields.add(field);
            }
            names.add(fieldName);
        }

        // Last byte is a marker for the end of the field definitions.
        // in.skipBytes(1);
        ((Buffer) in).position(((Buffer) in).position() + 1);

        fields = new DbaseField[lfields.size()];
        fields = (DbaseField[]) lfields.toArray(fields);
    }

    /**
     * Get the largest field size of this table.
     *
     * @return The largt field size in bytes.
     */
    public int getLargestFieldSize() {
        return largestFieldSize;
    }

    /**
     * Set the number of records in the file
     *
     * @param inNumRecords
     *                The number of records.
     */
    public void setNumRecords(final int inNumRecords) {
        recordCnt = inNumRecords;
    }

    /**
     * Create the list of matching attribute descriptor from header informations.
     *
     * @return List of AttributDescriptor
     */
    public List<AttributeType> createDescriptors() {
        final int nbFields = getNumFields();

        final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();

        final List<AttributeType> attributes = new ArrayList<>(nbFields);
        for(int i=0; i<nbFields; i++){
            final String name = getFieldName(i);
            final Class attributeClass = getFieldClass(i);
            final int length = getFieldLength(i);

            atb.reset();
            atb.setName(name);
            atb.setValueClass(attributeClass);
            atb.setLength(length);

            attributes.add(atb.build());
        }

        return attributes;
    }

    /**
     * Write the header data to the DBF file.
     *
     * @param out
     *                A channel to write to. If you have an OutputStream you can
     *                obtain the correct channel by using
     *                java.nio.Channels.newChannel(OutputStream out).
     * @throws IOException
     *                 If errors occur.
     */
    public void writeHeader(final WritableByteChannel out) throws IOException {
        // take care of the annoying case where no records have been added...
        if (headerLength == -1) {
            headerLength = MINIMUM_HEADER;
        }
        ByteBuffer buffer = ByteBuffer.allocate(headerLength);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // write the output file type.
        buffer.put((byte) MAGIC);

        // write the date stuff
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        buffer.put((byte) (c.get(Calendar.YEAR) % 100));
        buffer.put((byte) (c.get(Calendar.MONTH) + 1));
        buffer.put((byte) (c.get(Calendar.DAY_OF_MONTH)));

        // write the number of records in the datafile.
        buffer.putInt(recordCnt);

        // write the length of the header structure.
        buffer.putShort((short) headerLength);

        // write the length of a record
        buffer.putShort((short) recordLength);

        // // write the reserved bytes in the header
        // for (int i=0; i<20; i++) out.writeByteLE(0);
        ((Buffer) buffer).position(((Buffer) buffer).position() + 20);

        // write all of the header records
        int tempOffset = 0;
        for (int i = 0; i < fields.length; i++) {

            // write the field name
            for (int j = 0; j < 11; j++) {
                if (fields[i].fieldName.length() > j) {
                    buffer.put((byte) fields[i].fieldName.charAt(j));
                } else {
                    buffer.put((byte) 0);
                }
            }

            // write the field type
            buffer.put((byte) fields[i].fieldType);
            // // write the field data address, offset from the start of the
            // record.
            buffer.putInt(tempOffset);
            tempOffset += fields[i].fieldLength;

            // write the length of the field.
            buffer.put((byte) fields[i].fieldLength);

            // write the decimal count.
            buffer.put((byte) fields[i].decimalCount);

            // write the reserved bytes.
            // for (in j=0; jj<14; j++) out.writeByteLE(0);
            ((Buffer) buffer).position(((Buffer) buffer).position() + 14);
        }

        // write the end of the field definitions marker
        buffer.put((byte) 0x0D);

        ((Buffer) buffer).position(0);

        int r = buffer.remaining();
        while ((r -= out.write(buffer)) > 0) {
            // do nothing
        }

    }

    /**
     * Get a simple representation of this header.
     *
     * @return A String representing the state of the header.
     */
    @Override
    public String toString() {
        final StringBuilder fs = new StringBuilder();

        fs.append("DB3 Header\n Date : ").append(date)
          .append("\n Records : ").append(recordCnt)
          .append("\n Fields : ").append(fieldCnt)
          .append('\n');

        for (DbaseField f : fields) {
            fs.append(f.fieldName).append(' ').append(f.fieldType).append(' ')
                    .append(f.fieldLength).append(' ').append(f.decimalCount)
                    .append(' ').append(f.fieldDataAddress).append('\n');
        }

        return fs.toString();
    }

    /**
     * Analyze input data type, and try to create a new column matching its
     * definition in the current header.
     *
     * @param columnDefinition The attribute to create a column from. If {@link AttributeConvention#MAXIMAL_LENGTH_CHARACTERISTIC}
     * characteristics is set, it will be used as a constraint for the new column.
     * @throws DbaseFileException If an error occurs while adding the column using
     * input information.
     */
    private void addColumn(final AttributeType columnDefinition) throws DbaseFileException {
        final Class<?> colType = columnDefinition.getValueClass();
        final String colName = columnDefinition.getName().tip().toString();

        Integer fieldLen = FeatureExt.getLengthCharacteristic(columnDefinition);
        if (fieldLen == null)
            fieldLen = 255;
        if ((colType == Integer.class) || (colType == Short.class)
                || (colType == Byte.class)) {
            addColumn(colName, 'N', Math.min(fieldLen, 9), 0);
        } else if (colType == Long.class) {
            addColumn(colName, 'N', Math.min(fieldLen, 19), 0);
        } else if (colType == BigInteger.class) {
            addColumn(colName, 'N', Math.min(fieldLen, 33), 0);
        } else if (Number.class.isAssignableFrom(colType)) {
            int l = Math.min(fieldLen, 33);
            int d = Math.max(l - 2, 0);
            addColumn(colName, 'N', l, d);
        } else if (java.util.Date.class.isAssignableFrom(colType)) {
            addColumn(colName, 'D', fieldLen, 0);
        } else if (colType == Boolean.class) {
            addColumn(colName, 'L', 1, 0);
        } else if (CharSequence.class.isAssignableFrom(colType)) {
            // Possible fix for GEOT-42 : ArcExplorer doesn't like 0 length
            // ensure that maxLength is at least 1
            addColumn(colName, 'C', Math.min(254, fieldLen), 0);
        } else {
            //fallback : write as string
            addColumn(colName, 'C', Math.min(254, fieldLen), 0);
        }
    }

    /**
     * Attempt to create a DbaseFileHeader for the FeatureType. Note, we cannot
     * set the number of records until the write has completed.
     *
     * @param featureType The data type describing columns to create. Geometric
     * and convention fields will be ignored.
     * @return A new header in DBF format, containing all columns from input feature type.
     * @throws DbaseFileException If an error occurs while adding columns in the
     * header to create.
     */
    public static DbaseFileHeader createDbaseHeader(final FeatureType featureType)
            throws DbaseFileException {

        final DbaseFileHeader header = new DbaseFileHeader();
        for (final PropertyType type : featureType.getProperties(true)) {
            if (AttributeConvention.contains(type.getName()) || AttributeConvention.isGeometryAttribute(type)) {
                continue;
            }

            Optional<AttributeType<?>> attribute = Features.toAttribute(type);
            if (attribute.isPresent()) {
                header.addColumn(attribute.get());
            }
        }

        return header;
    }
}

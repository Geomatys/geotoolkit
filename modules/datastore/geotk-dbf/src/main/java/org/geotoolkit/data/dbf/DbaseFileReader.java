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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * A DbaseFileReader is used to read a dbase III format file. <br>
 * The general use of this class is: <CODE><PRE>
 * 
 * FileChannel in = new FileInputStream(&quot;thefile.dbf&quot;).getChannel();
 * DbaseFileReader r = new DbaseFileReader( in ) Object[] fields = new
 * Object[r.getHeader().getNumFields()]; while (r.hasNext()) {
 * r.readEntry(fields); // do stuff } r.close();
 * 
 * </PRE></CODE> For consumers who wish to be a bit more selective with their reading
 * of rows, the Row object has been added. The semantics are the same as using
 * the readEntry method, but remember that the Row object is always the same.
 * The values are parsed as they are read, so it pays to copy them out (as each
 * call to Row.read() will result in an expensive String parse). <br>
 * <b>EACH CALL TO readEntry OR readRow ADVANCES THE FILE!</b><br>
 * An example of using the Row method of reading: <CODE><PRE>
 * 
 * FileChannel in = new FileInputStream(&quot;thefile.dbf&quot;).getChannel();
 * DbaseFileReader r = new DbaseFileReader( in ) int fields =
 * r.getHeader().getNumFields(); while (r.hasNext()) { DbaseFileReader.Row row =
 * r.readRow(); for (int i = 0; i &lt; fields; i++) { // do stuff Foo.bar(
 * row.read(i) ); } } r.close();
 * 
 * </PRE></CODE>
 * 
 * @author Ian Schneider
 * @module pending
 */
public class DbaseFileReader {

    public static final Charset DEFAULT_STRING_CHARSET = Charset.forName("ISO-8859-1");

    public final class Row {
        public Object read(int column) throws IOException {
            final int offset = getOffset(column);
            return fieldReaders[column].read(charBuffer, offset);
        }

        @Override
        public String toString() {
            final StringBuilder ret = new StringBuilder("DBF Row - ");
            for (int i=0; i < header.getNumFields(); i++) {
                ret.append(header.getFieldName(i)).append(": \"");
                try {
                    ret.append(this.read(i));
                } catch (IOException ioe) {
                    ret.append(ioe.getMessage());
                }
                ret.append("\" ");
            }
            return ret.toString();
        }
    }

    protected final DbaseFileHeader header;
    protected final ByteBuffer buffer;
    protected final ReadableByteChannel channel;
    protected final CharBuffer charBuffer;
    private final Charset charset;
    private final CharsetDecoder decoder;
    private final DbaseField[] fieldReaders;
    private int cnt = 1;
    private final Row row;

    protected boolean useMemoryMappedBuffer;
    protected boolean randomAccessEnabled;
    protected long currentOffset = 0;

    /**
     * Creates a new instance of DBaseFileReader
     * 
     * @param dbfChannel The readable channel to use.
     * @throws IOException If an error occurs while initializing.
     */

    public DbaseFileReader(ReadableByteChannel dbfChannel, boolean useMemoryMappedBuffer, Charset charset) throws IOException {
        this.channel = dbfChannel;

        if(charset == null) charset = DEFAULT_STRING_CHARSET;

        this.charset = Charset.forName("ISO-8859-1"); // charset;

        this.useMemoryMappedBuffer = useMemoryMappedBuffer;
        this.randomAccessEnabled = (channel instanceof FileChannel);
        header = new DbaseFileHeader();
        header.readHeader(channel);

        // create the ByteBuffer
        // if we have a FileChannel, lets map it
        if (channel instanceof FileChannel && this.useMemoryMappedBuffer) {
            FileChannel fc = (FileChannel) channel;
            buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            buffer.position((int) fc.position());
            this.currentOffset = 0;
        } else {
            // Force useMemoryMappedBuffer to false
            this.useMemoryMappedBuffer = false;
            // Some other type of channel
            // start with a 8K buffer, should be more than adequate
            int size = 8 * 1024;
            // if for some reason its not, resize it
            size = header.getRecordLength() > size ? header.getRecordLength()
                    : size;
            buffer = ByteBuffer.allocateDirect(size);
            // fill it and reset
            fill(buffer, channel);
            buffer.flip();
            this.currentOffset = header.getHeaderLength();
        }
        
        // The entire file is in little endian
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        // Set up some buffers and lookups for efficiency
        fieldReaders = new DbaseField[header.getNumFields()];
        for (int i = 0, ii = header.getNumFields(); i < ii; i++) {
            fieldReaders[i] = header.getField(i);
        }
        
        charBuffer = CharBuffer.allocate(header.getRecordLength() - 1);
        decoder = charset.newDecoder();
        
        row = new Row();
    }

    protected void fill(ByteBuffer buffer, ReadableByteChannel channel)
            throws IOException {
        int r = buffer.remaining();
        // channel reads return -1 when EOF or other error
        // because they a non-blocking reads, 0 is a valid return value!!
        while (buffer.remaining() > 0 && r != -1) {
            r = channel.read(buffer);
        }
        if (r == -1) {
            buffer.limit(buffer.position());
        }
    }

    private void bufferCheck() throws IOException {
        // remaining is less than record length
        // compact the remaining data and read again
        if (!buffer.isReadOnly()
                && buffer.remaining() < header.getRecordLength()) {
            // if (!this.useMemoryMappedBuffer) {
            this.currentOffset += buffer.position();
            // }
            buffer.compact();
            fill(buffer, channel);
            buffer.position(0);
        }
    }

    private int getOffset(int column) {
        int offset = 0;
        for (int i = 0, ii = column; i < ii; i++) {
            offset += fieldReaders[i].fieldLength;
        }
        return offset;
    }

    /**
     * Get the header from this file. The header is read upon instantiation.
     * 
     * @return The header associated with this file or null if an error
     *         occurred.
     */
    public DbaseFileHeader getHeader() {
        return header;
    }

    /**
     * Clean up all resources associated with this reader.<B>Highly recomended.</B>
     * 
     * @throws IOException If an error occurs.
     */
    public void close() throws IOException {
        if (channel.isOpen()) {
            channel.close();
        }
    }

    /**
     * Query the reader as to whether there is another record.
     * 
     * @return True if more records exist, false otherwise.
     */
    public boolean hasNext() {
        return cnt < header.getNumRecords() + 1;
    }

    /**
     * Get the next record (entry). Will return a new array of values.
     * 
     * @throws IOException
     *                 If an error occurs.
     * @return A new array of values.
     */
    public Object[] readEntry() throws IOException {
        return readEntry(new Object[fieldReaders.length]);
    }

    public Row readRow() throws IOException {
        read();
        return row;
    }

    /**
     * Skip the next record.
     * 
     * @throws IOException
     *                 If an error occurs.
     */
    public void skip() throws IOException {
        boolean foundRecord = false;
        while (!foundRecord) {

            bufferCheck();

            // read the deleted flag
            final char tempDeleted = (char) buffer.get();

            // skip the next bytes
            // the 1 is for the deleted flag just read.
            buffer.position(buffer.position() + header.getRecordLength() - 1); 

            // add the row if it is not deleted.
            if (tempDeleted != '*') {
                foundRecord = true;
            }
        }
    }

    /**
     * Copy the next record into the array starting at offset.
     * 
     * @param entry The array to copy into.
     * @param offset The offset to start at
     * @throws IOException If an error occurs.
     * @return The same array passed in.
     */
    public Object[] readEntry(Object[] entry, final int offset)
            throws IOException {
        if (entry.length - offset < fieldReaders.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        read();

        int fieldOffset = 0;
        for (int j = 0; j < fieldReaders.length; j++) {
            final DbaseField field = fieldReaders[j];
            entry[j + offset] = field.read(charBuffer, fieldOffset);
            fieldOffset += field.fieldLength;
        }

        return entry;
    }
    
    /**
     * Reads a single field from the current record and returns it. Remember to call {@link #read()} before
     * starting to read fields from the dbf, and call it every time you need to move to the next record.
     * @param fieldNum The field number to be read (zero based)
     * @throws IOException
     *                 If an error occurs.
     * @return The value of the field
     */
    public Object readField(int fieldNum)
            throws IOException {
        // retrieve the record length
        int fieldOffset = 0;
        for (int j = 0; j < fieldNum; j++) {
            fieldOffset += fieldReaders[j].fieldLength;
        }
        return fieldReaders[fieldNum].read(charBuffer, fieldOffset);
    }

    /**
     * Transfer, by bytes, the next record to the writer.
     */
    public void transferTo(DbaseFileWriter writer) throws IOException {
        bufferCheck();
        buffer.limit(buffer.position() + header.getRecordLength());
        writer.channel.write(buffer);
        buffer.limit(buffer.capacity());

        cnt++;
    }

    /**
     * Reads the next record into memory. You need to use this directly when reading only
     * a subset of the fields using {@link #readField(int)}. 
     * @throws IOException
     */
    public void read() throws IOException {
        boolean foundRecord = false;
        while (!foundRecord) {

            bufferCheck();

            // read the deleted flag
            char deleted = (char) buffer.get();
            if (deleted == '*') {
                continue;
            }

            charBuffer.position(0);
            buffer.limit(buffer.position() + header.getRecordLength() - 1);
            decoder.decode(buffer, charBuffer, true);
            buffer.limit(buffer.capacity());
            charBuffer.flip();

            foundRecord = true;
        }

        cnt++;
    }

    /**
     * Copy the next entry into the array.
     * 
     * @param entry
     *                The array to copy into.
     * @throws IOException
     *                 If an error occurs.
     * @return The same array passed in.
     */
    public Object[] readEntry(Object[] entry) throws IOException {
        return readEntry(entry, 0);
    }

    public static void main(String[] args) throws Exception {
        DbaseFileReader reader = new DbaseFileReader(new RandomAccessFile(new File(args[0]),"r").getChannel(),
                false, Charset.forName("ISO-8859-1"));
        System.out.println(reader.getHeader());
        int r = 0;
        while (reader.hasNext()) {
            System.out.println(++r + "," + java.util.Arrays.asList(reader.readEntry()));
        }
        reader.close();
    }

}

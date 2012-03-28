/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import org.geotoolkit.io.Closeable;

/**
 * A DbaseFileReader is used to read a dbase III format file. <br>
 * The general use of this class is: <CODE><PRE>
 * 
 * FileChannel in = new FileInputStream(&quot;thefile.dbf&quot;).getChannel();
 * DbaseFileReader r = new DbaseFileReader( in );
 * Object[] fields = new Object[r.getHeader().getNumFields()]; 
 * while (r.hasNext()) {
 *    Row row = r.next();
 *    row.readAll(fields);
 *    //do stuff
 * }
 * r.close();
 * 
 * </PRE></CODE> 
 * For consumers who wish to be a bit more selective with their reading
 * of rows, the read(column) method has been added. 
 * Remember that the Row object is always the same.
 * The values are parsed as they are read, so it pays to copy them out (as each
 * call to Row.read() will result in an expensive String parse).
 * 
 * @author Ian Schneider
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class DbaseFileReader implements Closeable{

    public static final Charset DEFAULT_STRING_CHARSET = Charset.forName("ISO-8859-1");

    public final class Row {
        
        public Object read(final int column) throws IOException {
            final int offset = header.getFieldOffset(column);
            final DbaseField field = fieldReaders[column];
            prepareFieldRead(field, offset);
            return field.read(charBuffer);
        }
        
        public Object[] readAll(Object[] entry) throws IOException {
            if(entry == null){
                entry = new Object[fieldReaders.length];
            }else if (entry.length < fieldReaders.length) {
                throw new ArrayIndexOutOfBoundsException();
            }

            int fieldOffset = 1; //1 to skip the delete flag
            for (int x = 0; x < fieldReaders.length; x++) {
                final DbaseField field = fieldReaders[x];
                prepareFieldRead(field, fieldOffset);
                entry[x] = field.read(charBuffer);
                fieldOffset += field.fieldLength;
            }

            return entry;
        }
        
    }

    protected final DbaseFileHeader header;
    protected final ByteBuffer buffer;
    protected final ReadableByteChannel channel;
    protected final CharBuffer charBuffer; //char buffer cache
    private final CharsetDecoder decoder;
    private final DbaseField[] fieldReaders;
    private int cnt = 0;
    private final Row row = new Row();
    private Row next = null;

    protected boolean useMemoryMappedBuffer;
    protected boolean randomAccessEnabled;

    /**
     * Creates a new instance of DBaseFileReader
     * 
     * @param dbfChannel The readable channel to use.
     * @param useMemoryMappedBuffer 
     * @param charset 
     * @throws IOException If an error occurs while initializing.
     */

    public DbaseFileReader(final ReadableByteChannel dbfChannel, 
            final boolean useMemoryMappedBuffer, Charset charset) throws IOException {
        
        if(charset == null) charset = DEFAULT_STRING_CHARSET;

        this.channel = dbfChannel;
        this.useMemoryMappedBuffer = useMemoryMappedBuffer;
        this.randomAccessEnabled = (channel instanceof FileChannel);
        this.header = new DbaseFileHeader();
        this.header.readHeader(channel);

        // create the ByteBuffer
        // if we have a FileChannel, lets map it
        if (channel instanceof FileChannel && this.useMemoryMappedBuffer) {
            final FileChannel fc = (FileChannel) channel;
            this.buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            this.buffer.position((int) fc.position());
        } else {
            // Force useMemoryMappedBuffer to false
            this.useMemoryMappedBuffer = false;
            // Some other type of channel
            // start with a 8K buffer, should be more than adequate
            int size = 8 * 1024;
            // if for some reason its not, resize it
            size = header.getRecordLength() > size ? header.getRecordLength() : size;
            buffer = ByteBuffer.allocateDirect(size);
            // fill it and reset
            fill(buffer, channel);
            buffer.flip();
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
    }

    protected void fill(final ByteBuffer buffer, final ReadableByteChannel channel)
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

    /**
     * Fill buffer if remaining is smaller then one record size.
     * @throws IOException 
     */
    private void bufferCheck() throws IOException {
        buffer.limit(buffer.capacity());
        if (!buffer.isReadOnly() && buffer.remaining() < header.getRecordLength()) {
            buffer.compact();
            fill(buffer, channel);
            buffer.position(0);
        }
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
     * Query the reader as to whether there is another record.
     * 
     * @return True if more records exist, false otherwise.
     */
    public boolean hasNext() {
        return cnt < header.getNumRecords();
    }

    /**
     * @return Row, always same instance
     * @throws IOException
     */
    public Row next() throws IOException {
        checkNext();
        final Row r = next;
        next = null;
        return r;
    }

    private void checkNext() throws IOException{
        if(next!=null)return;
        
        if(cnt != 0){
            //move cursor to next record if it's not the first
            buffer.position(buffer.position()+header.getRecordLength());
        }
        prepareNext();
    }
        
    /**
     * fill buffer with current record, skip it if it's deleted
     * @throws IOException
     */
    private void prepareNext() throws IOException {
                
        boolean foundRecord = false;
        while (!foundRecord) {
            bufferCheck();

            // read the deleted flag
            char deleted = (char) buffer.get();
            if (deleted == '*') {
                //record was deleted, move to next one, -1 for the delete flag we just read
                buffer.position(buffer.position()+header.getRecordLength()-1);
                continue;
            }
            buffer.position(buffer.position()-1);
            foundRecord = true;
            next = row;
        }
        
        cnt++;
    }
    
    private void prepareFieldRead(final DbaseField field, final int fieldOffset) throws CharacterCodingException{
        //prepare byte buffer
        final int previousposition = buffer.position();
        final int previouslimit = buffer.limit();
        decoder.reset();
        charBuffer.clear();
        buffer.position(previousposition+fieldOffset);
        buffer.limit(buffer.position()+field.fieldLength);
        CoderResult result = decoder.decode(buffer, charBuffer, true);
        if(CoderResult.UNDERFLOW != result){
            result.throwException();
        }
        result = decoder.flush(charBuffer);
        if(CoderResult.UNDERFLOW != result){
            result.throwException();
        }
        buffer.limit(previouslimit);
        buffer.position(previousposition);
        charBuffer.flip();
    }
    
    /**
     * Transfer, by bytes, the next record to the writer.
     * @param writer
     * @throws IOException  
     */
    public void transferTo(final DbaseFileWriter writer) throws IOException {
        bufferCheck();
        buffer.limit(buffer.position() + header.getRecordLength());
        writer.channel.write(buffer);
        buffer.limit(buffer.capacity());
        cnt++;
    }

    /**
     * Navigate to the given record index.
     * 
     * @param recno
     * @throws IOException
     * @throws UnsupportedOperationException 
     */
    public void goTo(final int recno) throws IOException, UnsupportedOperationException {

        if (randomAccessEnabled) {
            final long newPosition = header.getHeaderLength()
                    + header.getRecordLength() * (long)(recno - 1);

            if (useMemoryMappedBuffer) {
                buffer.position((int)newPosition);
            } else {
                final FileChannel fc = (FileChannel) channel;
                fc.position(newPosition);
                buffer.limit(buffer.capacity());
                buffer.position(0);
                fill(buffer, channel);
                buffer.position(0);
            }
            prepareNext();
        } else {
            throw new UnsupportedOperationException("Random access not enabled!");
        }

    }
    
    /**
     * If this method return true, then the index navigation (goto method) can be used.
     * @return true if source is a FileChannel
     */
    public boolean IsRandomAccessEnabled() {
        return this.randomAccessEnabled;
    }
    
    /**
     * Clean up all resources associated with this reader.<B>Highly recomended.</B>
     * 
     * @throws IOException If an error occurs.
     */
    @Override
    public void close() throws IOException {
        if (channel.isOpen()) {
            channel.close();
        }
    }

    @Override
    public boolean isClosed() {
        return !channel.isOpen();
    }
    
}

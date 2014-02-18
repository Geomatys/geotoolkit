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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import org.geotoolkit.io.Closeable;


/**
 * A DbaseFileReader is used to read a dbase III format file. The general use of
 * this class is: <CODE><PRE>
 * DbaseFileHeader header = ...
 * WritableFileChannel out = new FileOutputStream(&quot;thefile.dbf&quot;).getChannel();
 * DbaseFileWriter w = new DbaseFileWriter(header,out);
 * while ( moreRecords ) {
 *   w.write( getMyRecord() );
 * }
 * w.close();
 * </PRE></CODE> You must supply the <CODE>moreRecords</CODE> and
 * <CODE>getMyRecord()</CODE> logic...
 * 
 * @author Ian Schneider
 * @module pending
 */
public class DbaseFileWriter implements Closeable{

    private DbaseFileHeader header;
    private DbaseFieldFormatter formatter;
    WritableByteChannel channel;
    private ByteBuffer buffer;
    private Charset charset;
    
    /**
     * Create a DbaseFileWriter using the specified header and writing to the
     * given channel.
     * 
     * @param header
     *                The DbaseFileHeader to write.
     * @param out
     *                The Channel to write to.
     * @throws IOException
     *                 If errors occur while initializing.
     */
    public DbaseFileWriter(final DbaseFileHeader header, final WritableByteChannel out)
            throws IOException {
        this(header, out, null);
    }
    

    /**
     * Create a DbaseFileWriter using the specified header and writing to the
     * given channel.
     * 
     * @param header
     *                The DbaseFileHeader to write.
     * @param out
     *                The Channel to write to.
     * @param charset The charset the dbf is (will be) encoded in
     * @throws IOException
     *                 If errors occur while initializing.
     */
    public DbaseFileWriter(final DbaseFileHeader header, final WritableByteChannel out, final Charset charset)
            throws IOException {
        header.writeHeader(out);
        this.header = header;
        this.channel = out;
        this.charset = charset == null ? Charset.defaultCharset() : charset;
        this.formatter = new DbaseFieldFormatter(this.charset);
        init();
    }

    private void init() throws IOException {
        buffer = ByteBuffer.allocateDirect(header.getRecordLength());
    }

    private void write() throws IOException {
        buffer.position(0);
        int r = buffer.remaining();
        while ((r -= channel.write(buffer)) > 0) {
            // do nothing
        }
    }

    /**
     * Write a single dbase record.
     * 
     * @param record
     *                The entries to write.
     * @throws IOException
     *                 If IO error occurs.
     * @throws DbaseFileException
     *                 If the entry doesn't comply to the header.
     */
    public void write(final Object[] record) throws IOException, DbaseFileException {

        if (record.length != header.getNumFields()) {
            throw new DbaseFileException("Wrong number of fields "
                    + record.length + " expected " + header.getNumFields());
        }

        buffer.position(0);

        // put the 'not-deleted' marker
        buffer.put((byte) ' ');

        for (int i = 0; i < header.getNumFields(); i++) {
            String fieldString = header.getField(i).string(record[i], formatter);
            if (header.getFieldLength(i) != fieldString.getBytes(charset.name()).length) {
                // System.out.println(i + " : " + header.getFieldName(i)+" value
                // = "+fieldString+"");
                buffer.put(new byte[header.getFieldLength(i)]);
            } else {
                buffer.put(fieldString.getBytes(charset.name()));
            }

        }

        write();
    }

    /**
     * Release resources associated with this writer. <B>Highly recommended</B>
     * 
     * @throws IOException
     *                 If errors occur.
     */
    @Override
    public void close() throws IOException {
        // IANS - GEOT 193, bogus 0x00 written. According to dbf spec, optional
        // eof 0x1a marker is, well, optional. Since the original code wrote a
        // 0x00 (which is wrong anyway) lets just do away with this :)
        // - produced dbf works in OpenOffice and ArcExplorer java, so it must
        // be okay.
        // buffer.position(0);
        // buffer.put((byte) 0).position(0).limit(1);
        // write();
        if (channel.isOpen()) {
            channel.close();
        }
        buffer = null;
        channel = null;
        formatter = null;
    }

    @Override
    public boolean isClosed() {
        return !channel.isOpen();
    }

}

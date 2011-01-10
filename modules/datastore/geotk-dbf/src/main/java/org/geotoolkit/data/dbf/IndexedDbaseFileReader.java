/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

/**
 * A DbaseFileReader is used to read a dbase III format file. <br>
 * The general use of this class is: <CODE><PRE>
 * FileChannel in = new FileInputStream(&quot;thefile.dbf&quot;).getChannel();
 * DbaseFileReader r = new DbaseFileReader( in )
 * Object[] fields = new Object[r.getHeader().getNumFields()];
 * while (r.hasNext()) {
 *    r.readEntry(fields);
 *    // do stuff
 * }
 * r.close();
 * </PRE></CODE> For consumers who wish to be a bit more selective with their reading
 * of rows, the Row object has been added. The semantics are the same as using
 * the readEntry method, but remember that the Row object is always the same.
 * The values are parsed as they are read, so it pays to copy them out (as each
 * call to Row.read() will result in an expensive String parse). <br>
 * <b>EACH CALL TO readEntry OR readRow ADVANCES THE FILE!</b><br>
 * An example of using the Row method of reading: <CODE><PRE>
 * FileChannel in = new FileInputStream(&quot;thefile.dbf&quot;).getChannel();
 * DbaseFileReader r = new DbaseFileReader( in )
 * int fields = r.getHeader().getNumFields();
 * while (r.hasNext()) {
 *   DbaseFileReader.Row row = r.readRow();
 *   for (int i = 0; i &lt; fields; i++) {
 *     // do stuff
 *     Foo.bar( row.read(i) );
 *   }
 * }
 * r.close();
 * </PRE></CODE>
 * 
 * @author Ian Schneider
 * @author Tommaso Nolli
 * @module pending
 */
public class IndexedDbaseFileReader extends DbaseFileReader {

    /**
     * Like calling DbaseFileReader(ReadableByteChannel, true);
     * 
     * @param readChannel
     * @throws IOException
     */
    public IndexedDbaseFileReader(final ReadableByteChannel readChannel) throws IOException {
        this(readChannel, false, DEFAULT_STRING_CHARSET);
    }

    /**
     * Creates a new instance of DBaseFileReader
     * 
     * @param shpFreadChannel The channel to use.
     * @param useMemoryMappedBuffer Wether or not map the file in memory
     * @throws IOException If an error occurs while initializing.
     */
    public IndexedDbaseFileReader(final ReadableByteChannel readChannel,
            final boolean useMemoryMappedBuffer) throws IOException {
        super(readChannel, useMemoryMappedBuffer,DEFAULT_STRING_CHARSET);
    }

    public IndexedDbaseFileReader(final ReadableByteChannel readChannel,
            final boolean useMemoryMappedBuffer, final Charset stringCharset)
            throws IOException {
        super(readChannel, useMemoryMappedBuffer, stringCharset);
    }

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

                this.currentOffset = newPosition;
            }
        } else {
            throw new UnsupportedOperationException("Random access not enabled!");
        }

    }

    public boolean IsRandomAccessEnabled() {
        return this.randomAccessEnabled;
    }

    public static void main(final String[] args) throws Exception {
        final IndexedDbaseFileReader reader = new IndexedDbaseFileReader(
                new RandomAccessFile(new File(args[0]),"r").getChannel(), false);
        System.out.println(reader.getHeader());
        for(int r=0; reader.hasNext();) {
            System.out.println(++r + ","+ java.util.Arrays.asList(reader.readEntry()));
        }
        reader.close();
    }

}

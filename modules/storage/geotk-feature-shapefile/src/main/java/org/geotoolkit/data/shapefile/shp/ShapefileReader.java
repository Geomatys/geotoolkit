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
 */
package org.geotoolkit.data.shapefile.shp;

import java.io.EOFException;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.dbf.Closeable;
import org.geotoolkit.data.shapefile.shx.ShxReader;
import org.locationtech.jts.geom.Geometry;

/**
 * The general use of this class is: <CODE><PRE>
 *
 * FileChannel in = new FileInputStream(&quot;thefile.dbf&quot;).getChannel();
 * ShapefileReader r = new ShapefileReader( in ) while (r.hasNext()) { Geometry
 * shape = (Geometry) r.nextRecord().shape() // do stuff } r.close();
 *
 * </PRE></CODE> You don't have to immediately ask for the shape from the record. The
 * record will contain the bounds of the shape and will only read the shape when
 * the shape() method is called. This ShapefileReader.Record is the same object
 * every time, so if you need data from the Record, be sure to copy it.
 *
 *
 * TODO: remove all Buffer cast after migration to JDK9.
 *
 * @author jamesm
 * @author aaime
 * @author Ian Schneider
 * @module
 */
public final class ShapefileReader implements Closeable{

    /**
     *  Used to mark the current shape is not known, either because someone moved the reader
     *  to a specific byte offset manually, or because the .shx could not be opened
     */
    private static final int UNKNOWN = Integer.MIN_VALUE;

    /**
     * The reader returns only one Record instance in its lifetime. The record
     * contains the current record information.
     */
    public final class Record {
        int length;
        int number = 0;
        int offset; // Relative to the whole file
        int start = 0; // Relative to the current loaded buffer

        /** The minimum X value. */
        public double minX;
        /** The minimum Y value. */
        public double minY;
        /** The maximum X value. */
        public double maxX;
        /** The maximum Y value. */
        public double maxY;

        ShapeType type;

        int end = 0; // Relative to the whole file

        Geometry shape = null;

        /** Fetch the shape stored in this record. */
        public Geometry shape() {
            if (shape == null) {
                ((Buffer) buffer).position(start);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                shape = handler.read(buffer, type);
            }
            return shape;
        }

        /**
         * Generate and estimated geometry calculated from the bounding box.
         * It can be used when the bounding box is  already smaller then what we need.
         * Whatever shape it has will have no consequences.
         *
         * @return an average shape generated using the bounding box
         */
        public Geometry estimatedShape() {
            return handler.estimated(minX, maxX, minY, maxY);
        }

        public int offset() {
            return offset;
        }

        /** A summary of the record. */
        @Override
        public String toString() {
            return "Record " + number + " length " + length + " bounds " + minX
                    + "," + minY + " " + maxX + "," + maxY;
        }
    }

    private final ShapeHandler handler;
    private final ShapefileHeader header;
    private final ShapeType fileShapeType;
    private final ByteBuffer headerTransfer;
    private final Record record = new Record();
    private final boolean randomAccessEnabled;
    private final boolean useMemoryMappedBuffer;

    private long currentOffset = 0L;
    private int currentShape = 0;
    private ShxReader shxReader;
    private ReadableByteChannel channel;
    ByteBuffer buffer;

    /**
     * Creates a new instance of ShapeFile.
     *
     * @param shpChannel
     *                The ReadableByteChannel this reader will use.
     * @param shxChannel
     *                The ReadableByteChannel for shx reader.
     * @param strict
     *                True to make the header parsing throw Exceptions if the
     *                version or magic number are incorrect.
     * @throws IOException
     *                 If problems arise.
     * @throws ShapefileException
     *                 If for some reason the file contains invalid records.
     */
    public ShapefileReader(final ReadableByteChannel shpChannel, final ReadableByteChannel shxChannel,
            final boolean strict,final boolean useMemoryMapped, final boolean read3D,
            final double[] resample) throws IOException, DataStoreException {
        this.channel = shpChannel;
        this.randomAccessEnabled = channel instanceof FileChannel;

        try {
            header = readHeader(channel, strict);

            if(shxChannel != null){
                shxReader = new ShxReader(shxChannel, true);
            }else{
                currentShape = UNKNOWN;
            }

            fileShapeType = header.getShapeType();
            handler = fileShapeType.getShapeHandler(read3D,resample);

            if (handler == null) {
                throw new IOException("Unsuported shape type:" + fileShapeType);
            }

            if (channel instanceof FileChannel && useMemoryMapped) {
                this.useMemoryMappedBuffer = useMemoryMapped;
                final FileChannel fc = (FileChannel) channel;
                buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                ((Buffer) buffer).position(100);
                this.currentOffset = 0;
            } else {
                // force useMemoryMappedBuffer to false
                this.useMemoryMappedBuffer = false;
                // start with 8K buffer
                buffer = ByteBuffer.allocate(8 * 1024);
                fill(buffer, channel);
                ((Buffer) buffer).flip();
                this.currentOffset = 100;
            }

            headerTransfer = ByteBuffer.allocate(8);
            headerTransfer.order(ByteOrder.BIG_ENDIAN);

            // make sure the record end is set now...
            record.end = this.toFileOffset(((Buffer) buffer).position());
        }  catch (IOException | DataStoreException e) {
            if (shxReader != null) {
                shxReader.close();
            }
            if (channel != null) {
                channel.close();
            }
            throw e;
        }
    }

    /**
     * Disables .shx file usage. By doing so you drop support for sparse shapefiles, the
     * .shp will have to be without holes, all the valid shapefile records will have to
     * be contiguous.
     * @throws IOException
     */
    public void disableShxUsage() throws IOException {
        if(shxReader != null) {
            shxReader.close();
            shxReader = null;
        }
        currentShape = UNKNOWN;
    }

    // convenience to peak at a header
    /**
     * A short cut for reading the header from the given channel.
     *
     * @param channel
     *                The channel to read from.
     * @param strict
     *                True to make the header parsing throw Exceptions if the
     *                version or magic number are incorrect.
     * @throws IOException
     *                 If problems arise.
     * @return A ShapefileHeader object.
     */
    public static ShapefileHeader readHeader(final ReadableByteChannel channel,
            final boolean strict) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(100);
        if (fill(buffer, channel) == -1) {
            throw new EOFException("Premature end of header");
        }
        ((Buffer) buffer).flip();
        return ShapefileHeader.read(buffer, strict);
    }

    // ensure the capacity of the buffer is of size by doubling the original
    // capacity until it is big enough
    // this may be naiive and result in out of MemoryError as implemented...
    public static ByteBuffer ensureCapacity(ByteBuffer buffer, final int size,
            final boolean useMemoryMappedBuffer) {
        // This sucks if you accidentally pass is a MemoryMappedBuffer of size
        // 80M
        // like I did while messing around, within moments I had 1 gig of
        // swap...
        if (buffer.isReadOnly() || useMemoryMappedBuffer) {
            return buffer;
        }

        int limit = ((Buffer) buffer).limit();
        while (limit < size) {
            limit *= 2;
        }
        if (limit != ((Buffer) buffer).limit()) {
            // if (record.ready) {
            buffer = ByteBuffer.allocate(limit);
            // }
            // else {
            // throw new IllegalArgumentException("next before hasNext");
            // }
        }
        return buffer;
    }

    // for filling a ReadableByteChannel
    public static int fill(final ByteBuffer buffer, final ReadableByteChannel channel)
            throws IOException {
        int r = buffer.remaining();
        // channel reads return -1 when EOF or other error
        // because they a non-blocking reads, 0 is a valid return value!!
        while (buffer.remaining() > 0 && r != -1) {
            r = channel.read(buffer);
        }
        if (r == -1) {
            ((Buffer) buffer).limit(((Buffer) buffer).position());
        }
        return r;
    }

    /**
     * Get the header. Its parsed in the constructor.
     *
     * @return The header that is associated with this file.
     */
    public ShapefileHeader getHeader() {
        return header;
    }

    // do important cleanup stuff.
    // Closes channel !
    /**
     * Clean up any resources. Closes the channel.
     *
     * @throws IOException
     *                 If errors occur while closing the channel.
     */
    @Override
    public void close() throws IOException {
        if (channel!= null && channel.isOpen()) {
            channel.close();
        }
        if(shxReader != null){
            shxReader.close();
        }
        shxReader = null;
        channel = null;
    }

    @Override
    public boolean isClosed() {
        if(channel != null){
            return !channel.isOpen();
        }
        return true;
    }

    public boolean supportsRandomAccess() {
        return randomAccessEnabled;
    }

    /**
     * If there exists another record. Currently checks the stream for the
     * presence of 8 more bytes, the length of a record. If this is true and the
     * record indicates the next logical record number, there exists more
     * records.
     *
     * @throws IOException
     * @return True if has next record, false otherwise.
     */
    public boolean hasNext() throws IOException {
        return hasNext(true);
    }

    /**
     * If there exists another record. Currently checks the stream for the
     * presence of 8 more bytes, the length of a record. If this is true and the
     * record indicates the next logical record number (if checkRecord == true),
     * there exists more records.
     *
     * @param checkRecno
     *                If true then record number is checked
     * @throws IOException
     * @return True if has next record, false otherwise.
     */
    private boolean hasNext(final boolean checkRecno) throws IOException {
        // don't read past the end of the file (provided currentShape accurately
        // represents the current position)
        if(currentShape > UNKNOWN && currentShape > shxReader.getRecordCount() - 1)
            return false;

        // mark current position
        final int position = ((Buffer) buffer).position();

        // ensure the proper position, regardless of read or handler behavior
        ((Buffer) buffer).position(getNextOffset());

        // no more data left
        if (buffer.remaining() < 8)
            return false;

        // looks good
        boolean hasNext = true;
        if (checkRecno) {
            // record headers in big endian
            buffer.order(ByteOrder.BIG_ENDIAN);
            final int declaredRecNo = buffer.getInt();
            hasNext = declaredRecNo == record.number + 1;
        }

        // reset things to as they were
        ((Buffer) buffer).position(position);

        return hasNext;
    }

    private int getNextOffset() throws IOException {
        if(currentShape >= 0) {
            return this.toBufferOffset(shxReader.getOffsetInBytes(currentShape));
        } else {
            return this.toBufferOffset(record.end);
        }
    }

    /**
     * Transfer (by bytes) the data at the current record to the
     * ShapefileWriter.
     *
     * @param bounds double array of length four for transfering the bounds into
     * @return The length of the record transfered in bytes
     */
    public int transferTo(final ShapefileWriter writer, final int recordNum, final double[] bounds) throws IOException {

        ((Buffer) buffer).position(this.toBufferOffset(record.end));
        buffer.order(ByteOrder.BIG_ENDIAN);

        buffer.getInt(); // record number
        final int recordLenght = buffer.getInt();
        final int len = recordLenght * 2;

        if (!buffer.isReadOnly() && !useMemoryMappedBuffer) {
            // capacity is less than required for the record
            // copy the old into the newly allocated
            if (buffer.capacity() < len + 8) {
                this.currentOffset += ((Buffer) buffer).position();
                final ByteBuffer old = buffer;
                // ensure enough capacity for one more record header
                buffer = ensureCapacity(buffer, len + 8, useMemoryMappedBuffer);
                buffer.put(old);
                fill(buffer, channel);
                ((Buffer) buffer).position(0);
            } else
            // remaining is less than record length
            // compact the remaining data and read again,
            // allowing enough room for one more record header
            if (buffer.remaining() < len + 8) {
                this.currentOffset += ((Buffer) buffer).position();
                buffer.compact();
                fill(buffer, channel);
                ((Buffer) buffer).position(0);
            }
        }

        final int mark = ((Buffer) buffer).position();


        buffer.order(ByteOrder.LITTLE_ENDIAN);
        final ShapeType recordType = ShapeType.forID(buffer.getInt());

        if (recordType.isMultiPoint()) {
            for (int i=0; i<4; i++) {
                bounds[i] = buffer.getDouble();
            }
        } else if (recordType != ShapeType.NULL) {
            bounds[0] = bounds[1] = buffer.getDouble();
            bounds[2] = bounds[3] = buffer.getDouble();
        }

        // write header to shp and shx
        ((Buffer) headerTransfer).position(0);
        headerTransfer.putInt(recordNum).putInt(recordLenght).position(0);
        writer.shpChannel.write(headerTransfer);
        headerTransfer.putInt(0, writer.offset).position(0);
        writer.offset += recordLenght + 4;
        writer.shx.getChannel().write(headerTransfer);

        // reset to mark and limit at end of record, then write
        ((Buffer) buffer).position(mark).limit(mark + len);
        writer.shpChannel.write(buffer);
        ((Buffer) buffer).limit(buffer.capacity());

        record.end = this.toFileOffset(((Buffer) buffer).position());
        record.number++;

        return len;
    }

    /**
     * Fetch the next record information.
     *
     * @throws IOException
     * @return The record instance associated with this reader.
     */
    public Record nextRecord() throws IOException {

        // need to update position
        ((Buffer) buffer).position(getNextOffset());
        if(currentShape != UNKNOWN)
            currentShape++;

        // record header is big endian
        buffer.order(ByteOrder.BIG_ENDIAN);

        // read shape record header
        final int recordNumber = buffer.getInt();
        // silly ESRI say contentLength is in 2-byte words
        // and ByteByffer uses bytes.
        // track the record location
        final int recordLength = buffer.getInt() * 2;

        if (!buffer.isReadOnly() && !useMemoryMappedBuffer) {
            // capacity is less than required for the record
            // copy the old into the newly allocated
            if (buffer.capacity() < recordLength + 8) {
                this.currentOffset += ((Buffer) buffer).position();
                final ByteBuffer old = buffer;
                // ensure enough capacity for one more record header
                buffer = ensureCapacity(buffer, recordLength + 8, useMemoryMappedBuffer);
                buffer.put(old);
                fill(buffer, channel);
                ((Buffer) buffer).position(0);
            } else
            // remaining is less than record length
            // compact the remaining data and read again,
            // allowing enough room for one more record header
            if (buffer.remaining() < recordLength + 8) {
                this.currentOffset += ((Buffer) buffer).position();
                buffer.compact();
                fill(buffer, channel);
                ((Buffer) buffer).position(0);
            }
        }

        // shape record is all little endian
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // read the type, handlers don't need it
        final ShapeType recordType = ShapeType.forID(buffer.getInt());

        // this usually happens if the handler logic is bunk,
        // but bad files could exist as well...
        if (recordType != ShapeType.NULL && recordType != fileShapeType) {
            throw new IllegalStateException("ShapeType changed illegally from "
                    + fileShapeType + " to " + recordType);
        }

        // peek at bounds, then reset for handler
        // many handler's may ignore bounds reading, but we don't want to
        // second guess them...
        ((Buffer) buffer).mark();
        if (recordType.isMultiPoint()) {
            record.minX = buffer.getDouble();
            record.minY = buffer.getDouble();
            record.maxX = buffer.getDouble();
            record.maxY = buffer.getDouble();
        } else if (recordType != ShapeType.NULL) {
            record.minX = record.maxX = buffer.getDouble();
            record.minY = record.maxY = buffer.getDouble();
        }
        ((Buffer) buffer).reset();

        record.offset = record.end;
        // update all the record info.
        record.length = recordLength;
        record.type = recordType;
        record.number = recordNumber;
        // remember, we read one int already...
        record.end = this.toFileOffset(((Buffer) buffer).position()) + recordLength - 4;
        // mark this position for the reader
        record.start = ((Buffer) buffer).position();
        // clear any cached shape
        record.shape = null;

        return record;
    }

    /**
     * Moves the reader to the specified byte offset in the file. Mind that:
     * <ul>
     * <li>it's your responsibility to ensure the offset corresponds to the
     * actual beginning of a shape struct</li>
     * <li>once you call this, reading with hasNext/next on sparse shapefiles
     * will be broken (we don't know anymore at which shape we are)</li>
     * </ul>
     *
     * @param offset
     * @throws IOException
     * @throws UnsupportedOperationException
     */
    public void goTo(final int offset) throws IOException, UnsupportedOperationException {
        disableShxUsage();
        if (randomAccessEnabled) {
            if (useMemoryMappedBuffer) {
                ((Buffer) buffer).position(offset);
            } else {
                /*
                 * Check to see if requested offset is already loaded; ensure
                 * that record header is in the buffer
                 */
                if (currentOffset <= offset && currentOffset + ((Buffer) buffer).limit() >= offset + 8) {
                    ((Buffer) buffer).position(toBufferOffset(offset));
                } else {
                    final FileChannel fc = (FileChannel)channel;
                    fc.position(offset);
                    currentOffset = offset;
                    ((Buffer) buffer).position(0);
                    fill(buffer, fc);
                    ((Buffer) buffer).position(0);
                }
            }

            final int oldRecordOffset = record.end;
            record.end = offset;
            try {
                hasNext(false); // don't check for next logical record equality
            } catch (IOException ioe) {
                record.end = oldRecordOffset;
                throw ioe;
            }
        } else {
            throw new UnsupportedOperationException("Random Access not enabled");
        }
    }

    /**
     * Returns the shape at the specified byte distance from the beginning of
     * the file. Mind that:
     * <ul>
     * <li>it's your responsibility to ensure the offset corresponds to the
     * actual beginning of a shape struct</li>
     * <li>once you call this, reading with hasNext/next on sparse shapefiles
     * will be broken (we don't know anymore at which shape we are)</li>
     * </ul>
     *
     *
     * @param offset
     * @throws IOException
     * @throws UnsupportedOperationException
     */
    public Object shapeAt(final int offset) throws IOException, UnsupportedOperationException {
        if (randomAccessEnabled) {
            goTo(offset);
            return nextRecord().shape();
        }
        throw new UnsupportedOperationException("Random Access not enabled");
    }

    /**
     * Sets the current location of the byteStream to offset and returns the
     * next record. Usually used in conjuctions with the shx file or some other
     * index file. Mind that:
     * <ul>
     * <li>it's your responsibility to ensure the offset corresponds to the
     * actual beginning of a shape struct</li>
     * <li>once you call this, reading with hasNext/next on sparse shapefiles
     * will be broken (we don't know anymore at which shape we are)</li>
     * </ul>
     *
     *
     *
     * @param offset
     *            If using an shx file the offset would be: 2 *
     *            (index.getOffset(i))
     * @return The record after the offset location in the bytestream
     * @throws IOException
     *             thrown in a read error occurs
     * @throws UnsupportedOperationException
     *             thrown if not a random access file
     */
    public Record recordAt(final int offset) throws IOException, UnsupportedOperationException {
        if (randomAccessEnabled) {
            goTo(offset);
            return nextRecord();
        }
        throw new UnsupportedOperationException("Random Access not enabled");
    }

    /**
     * Converts file offset to buffer offset
     *
     * @param offset The offset relative to the whole file
     * @return The offset relative to the current loaded portion of the file
     */
    private int toBufferOffset(final int offset) {
        return (int) (offset - currentOffset);
    }

    /**
     * Converts buffer offset to file offset
     *
     * @param offset The offset relative to the buffer
     * @return The offset relative to the whole file
     */
    private int toFileOffset(final int offset) {
        return (int) (currentOffset + offset);
    }

    /**
     * Parses the shpfile counting the records.
     *
     * @return the number of non-null records in the shapefile
     */
    public int getCount() throws DataStoreException {

        if (channel == null) return -1;

        int count = 0;
        final long offset = currentOffset;

        try {
            try {
                goTo(100);
            } catch (UnsupportedOperationException e) {
                return -1;
            }

            while (hasNext()) {
                count++;
                nextRecord();
            }

            //go back to where we was
            goTo((int) offset);

        } catch (IOException ioe) {
            // What now? This seems arbitrarily appropriate !
            throw new DataStoreException("Problem reading shapefile record",ioe);
        }

        return count;
    }

}

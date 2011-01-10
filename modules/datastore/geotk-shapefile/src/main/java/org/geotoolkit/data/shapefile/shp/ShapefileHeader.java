/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * 
 * @author jamesm
 * @author Ian Schneider
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ShapefileHeader {

    public static final int MAGIC = 9994;
    public static final int VERSION = 1000;

    private final int fileLength;
    private final int version;
    private final ShapeType shapeType;
    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    public ShapefileHeader(final int fileLenght, final int version, 
            final ShapeType shapeType, final double minX, final double maxX, final double minY, final double maxY){
        this.fileLength = fileLenght;
        this.version = version;
        this.shapeType = shapeType;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public int getVersion() {
        return version;
    }

    public int getFileLength() {
        return fileLength;
    }

    public double minX() {
        return minX;
    }

    public double minY() {
        return minY;
    }

    public double maxX() {
        return maxX;
    }

    public double maxY() {
        return maxY;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShapeFileHeader[");
        sb.append(" size ").append(fileLength);
        sb.append(" version ").append(version);
        sb.append(" shapeType ").append(shapeType);
        sb.append(" bounds ").append(minX).append(',').append(minY).append(',').append(maxX).append(',').append(maxY);
        sb.append(" ]");
        return sb.toString();
    }

    private static void checkMagic(final int fileCode, final boolean strict) throws IOException {
        if (fileCode != MAGIC) {
            final String message = "Wrong magic number, expected " + MAGIC + ", got " + fileCode;
            if (!strict) {
                System.err.println(message);
            } else {
                throw new IOException(message);
            }
        }
    }

    private static void checkVersion(final int version,final boolean strict) throws IOException {
        if (version != VERSION) {
            final String message = "Wrong version, expected " + MAGIC + ", got " + version;
            if (!strict) {
                System.err.println(message);
            } else {
                throw new IOException(message);
            }
        }
    }

    /**
     * Read the header from the given ByteBuffer.
     * SHP and SHX share the same header structure.
     *
     * @param buffer
     * @param strict : will check version if true
     * @return
     * @throws IOException
     */
    public static ShapefileHeader read(final ByteBuffer buffer, final boolean strict) throws IOException {

        buffer.order(ByteOrder.BIG_ENDIAN);
        final int fileCode = buffer.getInt();
        checkMagic(fileCode,strict);

        // skip 5 ints...
        buffer.position(buffer.position() + 20);

        final int fileLength = buffer.getInt();

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        final int version = buffer.getInt();
        checkVersion(version,strict);
        final ShapeType shapeType = ShapeType.forID(buffer.getInt());

        final double minX = buffer.getDouble();
        final double minY = buffer.getDouble();
        final double maxX = buffer.getDouble();
        final double maxY = buffer.getDouble();

        // skip remaining unused bytes
        buffer.order(ByteOrder.BIG_ENDIAN);
        // well they may not be unused forever...
        buffer.position(buffer.position() + 32);

        return new ShapefileHeader(fileLength, version, shapeType, minX, maxX, minY, maxY);
    }

    /**
     * Write header in the given ByteBuffer.
     */
    public static void write(final ByteBuffer buffer, final ShapeType type,
            final int length, final double minX, final double minY, final double maxX, final double maxY)
            throws IOException {
        buffer.order(ByteOrder.BIG_ENDIAN);

        buffer.putInt(MAGIC);

        // Skip unused part of header
        for (int i=0; i<5; i++) {
            buffer.putInt(0);
        }

        buffer.putInt(length);

        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.putInt(VERSION);
        buffer.putInt(type.id);

        // write the bounding box
        buffer.putDouble(minX);
        buffer.putDouble(minY);
        buffer.putDouble(maxX);
        buffer.putDouble(maxY);

        // skip remaining unused bytes
        buffer.order(ByteOrder.BIG_ENDIAN);
        for (int i=0; i<8; i++) {
            buffer.putInt(0); // Skip unused part of header
        }
    }

    public static void main(final String[] args) throws Exception {
        final FileChannel channel = new FileInputStream(new File(args[0])).getChannel();
        System.out.println(ShapefileReader.readHeader(channel, true));
        channel.close();
    }
}

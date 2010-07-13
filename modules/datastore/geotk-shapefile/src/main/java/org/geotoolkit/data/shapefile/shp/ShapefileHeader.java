/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
 * @module pending
 */
public class ShapefileHeader {

    public static final int MAGIC = 9994;
    public static final int VERSION = 1000;

    private int fileCode = -1;
    private int fileLength = -1;
    private int version = -1;
    private ShapeType shapeType = ShapeType.UNDEFINED;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    private ShapefileHeader(){}

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

    private static void checkMagic(int fileCode, final boolean strict) throws IOException {
        if (fileCode != MAGIC) {
            final String message = "Wrong magic number, expected " + MAGIC + ", got " + fileCode;
            if (!strict) {
                System.err.println(message);
            } else {
                throw new IOException(message);
            }
        }
    }

    private static void checkVersion(int version,final boolean strict) throws IOException {
        if (version != VERSION) {
            final String message = "Wrong version, expected " + MAGIC + ", got " + version;
            if (!strict) {
                System.err.println(message);
            } else {
                throw new IOException(message);
            }
        }
    }

    public static ShapefileHeader read(ByteBuffer file, boolean strict) throws IOException {
        ShapefileHeader header = new ShapefileHeader();
        file.order(ByteOrder.BIG_ENDIAN);
        header.fileCode = file.getInt();

        checkMagic(header.fileCode,strict);

        // skip 5 ints...
        file.position(file.position() + 20);

        header.fileLength = file.getInt();

        file.order(ByteOrder.LITTLE_ENDIAN);
        header.version = file.getInt();
        checkVersion(header.version,strict);
        header.shapeType = ShapeType.forID(file.getInt());

        header.minX = file.getDouble();
        header.minY = file.getDouble();
        header.maxX = file.getDouble();
        header.maxY = file.getDouble();

        // skip remaining unused bytes
        file.order(ByteOrder.BIG_ENDIAN);
        // well they may not be unused forever...
        file.position(file.position() + 32);

        return header;
    }

    public static void write(ByteBuffer file, ShapeType type, int numGeoms,
            int length, double minX, double minY, double maxX, double maxY)
            throws IOException {
        file.order(ByteOrder.BIG_ENDIAN);

        file.putInt(MAGIC);

        // Skip unused part of header
        for (int i=0; i<5; i++) {
            file.putInt(0);
        }

        file.putInt(length);

        file.order(ByteOrder.LITTLE_ENDIAN);

        file.putInt(VERSION);
        file.putInt(type.id);

        // write the bounding box
        file.putDouble(minX);
        file.putDouble(minY);
        file.putDouble(maxX);
        file.putDouble(maxY);

        // skip remaining unused bytes
        file.order(ByteOrder.BIG_ENDIAN);
        for (int i=0; i<8; i++) {
            file.putInt(0); // Skip unused part of header
        }
    }

    public static void main(String[] args) throws Exception {
        final FileChannel channel = new FileInputStream(new File(args[0])).getChannel();
        System.out.println(ShapefileReader.readHeader(channel, true));
        channel.close();
    }
}

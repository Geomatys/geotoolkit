/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.index.tree.hilbert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.geotoolkit.index.tree.Node;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * {@link TreeAccess} implementation adapted for {@link HilbertRTree}.<br/>
 * Store all {@link Node} architecture use by {@link Tree} on disk drive.
 *
 * @author Remi Marechal (Geomatys).
 */
final class HilbertTreeAccessFile extends HilbertChannelTreeAccess {

    /**
     * Build a {@link Tree} from a already filled file at {@link Path} location.
     *
     * @param input {@code File} which already contains {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @throws IOException if problem during read or write Node.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    HilbertTreeAccessFile(final Path input, final int magicNumber, final double versionNumber ) throws IOException, ClassNotFoundException {
        this(input, magicNumber, versionNumber, DEFAULT_BUFFER_LENGTH);
    }

    /**
     * Build a {@link Tree} from a already filled file at {@link Path} location.
     *
     * @param input {@code File} which already contains {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    HilbertTreeAccessFile(final Path input, final int magicNumber, final double versionNumber, final int byteBufferLength) throws IOException, ClassNotFoundException {
        super(Files.newByteChannel(input, StandardOpenOption.CREATE, StandardOpenOption.READ,
                StandardOpenOption.WRITE), magicNumber, versionNumber, byteBufferLength);
    }

    /**
     * Build and insert {@link Node} architecture in a file at {@link Path} location.<br/>
     * If file is not empty, data within it will be overwrite.<br/>
     * If file does not exist a file will be create.<br/>
     * The default length value of ByteBuffer which read and write on hard disk, is 4096 Bytes.
     *
     * @param outPut {@code File} where {@link Node} architecture which will be write.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param hilbertOrder maximum hilbert order value permit for each tree leaf.
     * @param maxElements element number per cell.
     * @param crs
     * @throws IOException if problem during read or write Node.
     */
    HilbertTreeAccessFile(final Path outPut, final int magicNumber, final double versionNumber,
            final int maxElements, final int hilbertOrder, final CoordinateReferenceSystem crs) throws IOException {
        this(outPut, magicNumber, versionNumber, maxElements, hilbertOrder, crs, DEFAULT_BUFFER_LENGTH);
    }

    /**
     * Build and insert {@link Node} architecture in a file at {@link Path} location.<br/>
     * If file is not empty, data within it will be overwrite.<br/>
     * If file does not exist a file will be create.<br/>
     * The default length value of ByteBuffer which read and write on hard disk, is 4096 Bytes.
     *
     * @param outPut {@code File} where {@link Node} architecture which will be write.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param hilbertOrder maximum hilbert order value permit for each tree leaf.
     * @param crs
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     */
    HilbertTreeAccessFile(final Path outPut, final int magicNumber, final double versionNumber,
            final int maxElements, final int hilbertOrder, final CoordinateReferenceSystem crs, final int byteBufferLength) throws IOException {
        super(Files.newByteChannel(outPut, StandardOpenOption.CREATE, StandardOpenOption.READ,
                StandardOpenOption.WRITE), magicNumber, versionNumber, maxElements, hilbertOrder, crs, byteBufferLength);
    }
}

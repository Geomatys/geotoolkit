/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
import org.geotoolkit.internal.tree.SeekableByteArrayChannel;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * {@link TreeAccess} implementation adapted for {@link HilbertRTree}.<br/>
 * Store all {@link Node} architecture use by {@link Tree} into byte array.
 *
 * @author Remi Marechal (Geomatys).
 * @see HilbertRTree
 */
public class HilbertAccessByteArray extends HilbertChannelTreeAccess {
    /**
     * Build a {@link Tree} from a already filled {@link File}.
     *
     * @param input {@code File} which already contains {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @throws IOException if problem during read or write Node.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    HilbertAccessByteArray(final byte[] data, final int magicNumber, final double versionNumber ) throws IOException, ClassNotFoundException {
        this(data, magicNumber, versionNumber, DEFAULT_BUFFER_LENGTH);
    }

    /**
     * Build a {@link Tree} from a already filled {@link File}.
     *
     * @param input {@code File} which already contains {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    HilbertAccessByteArray(final byte[] data, final int magicNumber, final double versionNumber, final int byteBufferLength) throws IOException, ClassNotFoundException {
        super(new SeekableByteArrayChannel(data), magicNumber, versionNumber, byteBufferLength);
    }

    /**
     * Build and insert {@link Node} architecture in a {@link File}.<br/>
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
    HilbertAccessByteArray(final int magicNumber, final double versionNumber,
            final int maxElements, final int hilbertOrder, final CoordinateReferenceSystem crs) throws IOException {
        this(magicNumber, versionNumber, maxElements, hilbertOrder, crs, DEFAULT_BUFFER_LENGTH);
    }

    /**
     * Build and insert {@link Node} architecture in a {@link File}.<br/>
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
    HilbertAccessByteArray(final int magicNumber, final double versionNumber,
            final int maxElements, final int hilbertOrder, final CoordinateReferenceSystem crs, final int byteBufferLength) throws IOException {
        super(new SeekableByteArrayChannel(), magicNumber, versionNumber, maxElements, hilbertOrder, crs, byteBufferLength);
    }

    /**
     * Returns internal channel stored data.<br>
     * <strong>Don't forget to call {@link TreeAccess#close() } or {@link TreeAccess#flush() } before,
     * to update internal channel values.</strong>
     *
     * @return internal channel stored data.
     * @see SeekableByteArrayChannel#getData()
     */
    public byte[] getData() {
        return ((SeekableByteArrayChannel)inOutChannel).getData();
    }
}

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
package org.geotoolkit.internal.tree;

import java.io.IOException;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * {@link TreeAccess} implementation.<br/>
 * Store all {@link Node} architecture use by {@link Tree} into byte array.
 *
 * @author Remi Marechal (Geomatys).
 */
public class TreeAccessByteArray extends ChannelTreeAccess {

    /**
     * Number of Integer per Node.<br/><br/>
     * parent ID<br/>
     * sibling ID<br/>
     * child ID<br/>
     * children number.
     *
     * @see HilbertTreeAccessFile#INT_NUMBER
     */
    private static final int NODE_INT_SIZE = 4;

    //--------------------------- reading mode ---------------------------------
    /**
     * Build a {@link Tree} from a already filled {@link Byte} array.<br/><br/>
     *
     * Note : The default length value of ByteBuffer which read and write on hard disk, is 4096 Bytes.
     *
     * @param data {@code File} which already contains {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    public TreeAccessByteArray(final byte[] data, final int magicNumber, final double versionNumber, final int byteBufferLength)
            throws IOException, ClassNotFoundException {
        this(data, magicNumber, versionNumber, byteBufferLength, NODE_INT_SIZE);
    }

    /**
     * Build a {@link Tree} from a already filled {@link Byte} array.
     *
     * @param data byte array which contain all {@link Tree} {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @throws IOException if problem during read or write Node.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    public TreeAccessByteArray(final byte[] data, final int magicNumber, final double versionNumber)
            throws IOException, ClassNotFoundException{
        this(data, magicNumber, versionNumber, DEFAULT_BUFFER_LENGTH, NODE_INT_SIZE);
    }

    /**
     * Build a {@link TreeAccess} from a already filled {@link Byte} array.
     *
     * @param data byte array which contain all {@link Tree} {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @param integerNumberPerNode integer number per Node which will be red/written during Node reading/writing process.
     * @throws IOException if problem during channel read / write operation
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    public TreeAccessByteArray(final byte[] data, int magicNumber, double versionNumber,
            int byteBufferLength, int integerNumberPerNode) throws IOException, ClassNotFoundException {
        super(new SeekableByteArrayChannel(data), magicNumber, versionNumber, byteBufferLength, integerNumberPerNode);
    }

    //----------------------------- writing mode -------------------------------
    /**
     * Build and insert {@link Node} architecture in a byte array.<br><br>
     *
     * Constructor only use by {@link BasicRTree} implementation.
     *
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param maxElements element number per cell.
     * @param crs
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessByteArray(final int magicNumber, final double versionNumber, final int maxElements,
            final SplitCase splitMade, final CoordinateReferenceSystem crs, final int byteBufferLength) throws IOException {
        this(magicNumber, versionNumber, maxElements, 0, splitMade, crs, byteBufferLength, NODE_INT_SIZE);
    }

    /**
     * Build and insert {@link Node} architecture in a byte array.<br/><br>
     *
     * Constructor only use by {@link BasicRTree} implementation.
     *
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param splitMade
     * @param maxElements element number per cell.
     * @param crs
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessByteArray(final int magicNumber, final double versionNumber, final int maxElements,
             final SplitCase splitMade, final CoordinateReferenceSystem crs) throws IOException {
        this(magicNumber, versionNumber, maxElements, 0, splitMade, crs, DEFAULT_BUFFER_LENGTH, NODE_INT_SIZE);
    }

    /**
     * Build and insert {@link Node} architecture in a byte array.
     *
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param maxElements element number per cell.
     * @param crs
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessByteArray(final int magicNumber, final double versionNumber,
            final int maxElements, final CoordinateReferenceSystem crs, final int byteBufferLength) throws IOException {
        this(magicNumber, versionNumber, maxElements, 0, null, crs, byteBufferLength, NODE_INT_SIZE);
    }

    /**
     * Build and insert {@link Node} architecture in a byte array.
     *
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param maxElements element number per cell.
     * @param crs
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessByteArray(final int magicNumber, final double versionNumber,
            final int maxElements, final CoordinateReferenceSystem crs) throws IOException {
        this(magicNumber, versionNumber, maxElements, 0, null, crs, DEFAULT_BUFFER_LENGTH, NODE_INT_SIZE);
    }

    /**
     * Create a {@link TreeAccessFile} which store all {@link Node} architecture into a {@link Byte} array.
     *
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param maxElements element number per cell.
     * @param hilbertOrder
     * @param splitMade
     * @param crs
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @param integerNumberPerNode integer number per Node which will be red/written during Node reading/writing process.
     * @throws IOException if problem during channel read / write operation
     */
    public TreeAccessByteArray(final int magicNumber, final double versionNumber,
            final int maxElements, final int hilbertOrder, final SplitCase splitMade,
            final CoordinateReferenceSystem crs, final int byteBufferLength, final int integerNumberPerNode)
            throws IOException {
        super(new SeekableByteArrayChannel(), magicNumber, versionNumber, maxElements, hilbertOrder, splitMade, crs, byteBufferLength, integerNumberPerNode);
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

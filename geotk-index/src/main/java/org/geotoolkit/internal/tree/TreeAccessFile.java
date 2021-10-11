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
package org.geotoolkit.internal.tree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.apache.sis.referencing.CRS;

import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * {@link TreeAccess} implementation.<br>
 * Store all {@link Node} architecture use by {@link Tree} on disk drive.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class TreeAccessFile extends ChannelTreeAccess {

    /**
     * Position in the tree file where CRS description should begin.
     */
    private static final int CRS_POSITION = 34;

    /**
     * Number of Integer per Node.<br><br>
     * parent ID<br>
     * sibling ID<br>
     * child ID<br>
     * children number.
     *
     * @see HilbertTreeAccessFile#INT_NUMBER
     */
    private static final int INT_NUMBER = 4;

    /**
     * Build a {@link Tree} from an already filled file at {@link Path} location.<br><br>
     *
     * @param input {@code File} which already contains {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessFile(final Path input, final int magicNumber, final double versionNumber, final int byteBufferLength)  throws IOException {
        this(input, magicNumber, versionNumber, byteBufferLength, INT_NUMBER);
    }

    /**
     * Build a {@link Tree} from an already filled file at {@link Path} location.<br><br>
     *
     * Note : The default length value of ByteBuffer which read and write on hard disk, is 4096 Bytes.
     *
     * @param input {@code File} which already contains {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessFile(final Path input, final int magicNumber, final double versionNumber) throws IOException {
        this(input, magicNumber, versionNumber, DEFAULT_BUFFER_LENGTH, INT_NUMBER);
    }

    /**
     * Build a {@link TreeAccess} from an already filled file at {@link Path} location.
     *
     * @param input {@code Path} which already contains {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @param integerNumberPerNode integer number per Node which will be red/written during Node reading/writing process.
     * @throws IOException if problem during read or write Node.
     */
    protected TreeAccessFile(final Path input, final int magicNumber, final double versionNumber ,
            final int byteBufferLength, final int integerNumberPerNode) throws IOException {
        super(Files.newByteChannel(input, StandardOpenOption.CREATE, StandardOpenOption.READ,
                StandardOpenOption.WRITE),
                magicNumber, versionNumber, byteBufferLength, integerNumberPerNode);
    }

    /**
     * Build and insert {@link Node} architecture in a file at {@link Path} location.<br>
     * If file is not empty, data within it will be overwrite.<br>
     * If file does not exist a file will be create.<br><br>
     *
     * Constructor only use by {@link BasicRTree} implementation.
     *
     * @param outPut {@code File} where {@link Node} architecture which will be write.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param maxElements element number per cell.
     * @param splitMade define tree node split made.
     * @param crs
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessFile(final Path outPut, final int magicNumber, final double versionNumber, final int maxElements,
            final SplitCase splitMade, final CoordinateReferenceSystem crs, final int byteBufferLength) throws IOException {
        this(outPut, magicNumber, versionNumber, maxElements, 0, splitMade, crs, byteBufferLength, INT_NUMBER);
    }

    /**
     * Build and insert {@link Node} architecture in a file at {@link Path} location.<br>
     * If file is not empty, data within it will be overwrite.<br>
     * If file does not exist a file will be create.<br><br>
     *
     * Constructor only use by {@link BasicRTree} implementation.
     *
     * @param outPut {@code File} where {@link Node} architecture which will be write.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param maxElements element number per cell.
     * @param splitMade define how to split a {@link Node}, only use by {@link BasicRTree}, may be {@code null} for other tree.
     * @param crs
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessFile(final Path outPut, final int magicNumber, final double versionNumber, final int maxElements,
             final SplitCase splitMade, final CoordinateReferenceSystem crs) throws IOException {
        this(outPut, magicNumber, versionNumber, maxElements, 0, splitMade, crs, DEFAULT_BUFFER_LENGTH, INT_NUMBER);
    }

    /**
     * Build and insert {@link Node} architecture in a file at {@link Path} location.<br>
     * If file is not empty, data within it will be overwrite.<br>
     * If file does not exist a file will be create.
     *
     * @param outPut {@code File} where {@link Node} architecture which will be write.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param maxElements element number per cell.
     * @param crs
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessFile(final Path outPut, final int magicNumber, final double versionNumber,
            final int maxElements, final CoordinateReferenceSystem crs, final int byteBufferLength) throws IOException {
        this(outPut, magicNumber, versionNumber, maxElements, 0, null, crs, byteBufferLength, INT_NUMBER);
    }

    /**
     * Build and insert {@link Node} architecture in a file at {@link Path} location.<br>
     * If file is not empty, data within it will be overwrite.<br>
     * If file does not exist a file will be create.
     *
     * @param outPut {@code File} where {@link Node} architecture which will be write.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param maxElements element number per cell.
     * @param crs
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessFile(final Path outPut, final int magicNumber, final double versionNumber,
            final int maxElements, final CoordinateReferenceSystem crs) throws IOException {
        this(outPut, magicNumber, versionNumber, maxElements, 0, null, crs, DEFAULT_BUFFER_LENGTH, INT_NUMBER);
    }

    /**
     * Build and insert {@link Node} architecture in a file at {@link Path} location.<br>
     * If file is not empty, data within it will be overwrite.<br>
     * If file does not exist a file will be create.
     *
     * @param outPut {@code File} where {@link Node} architecture which will be write.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param maxElements element number per cell.
     * @param hilbertOrder
     * @param crs
     * @param splitMade define how to split a {@link Node}, only use by {@link BasicRTree}, may be {@code null} for other tree.
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @param integerNumberPerNode integer number per Node which will be red/written during Node reading/writing process.
     * @throws IOException if problem during read or write Node.
     */
    protected TreeAccessFile(final Path outPut, final int magicNumber, final double versionNumber,
            final int maxElements, final int hilbertOrder, final SplitCase splitMade,
            final CoordinateReferenceSystem crs, final int byteBufferLength, final int integerNumberPerNode)
            throws IOException {
        super(Files.newByteChannel(outPut, StandardOpenOption.CREATE, StandardOpenOption.READ,
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING), magicNumber, versionNumber, maxElements, hilbertOrder, splitMade, crs, byteBufferLength, integerNumberPerNode);
    }

    /**
     * Retrieve the CRS of the input tree.
     * @param treeFile The file containing the tree.
     * @return The {@link org.opengis.referencing.crs.CoordinateReferenceSystem} in which teh tree is expressed.
     * @throws java.lang.IllegalArgumentException if input file is null.
     * @throws java.io.IOException if input file does not exists, or is a directory, or a problem happens at reading, or if problem during CRS WKT serialization.
     */
    public static CoordinateReferenceSystem getTreeCRS(final File treeFile) throws IOException {
        ArgumentChecks.ensureNonNull("Input tree file", treeFile);
        if (!treeFile.isFile()) {
            throw new IOException("Input file is not a regular file : "+treeFile);
        }
        final RandomAccessFile raf = new RandomAccessFile(treeFile, "r");
        raf.seek(34);
        final int byteTabLength   = raf.readInt();
        final byte[] crsByteArray = new byte[byteTabLength];
        raf.read(crsByteArray, 0, byteTabLength);
        final String wktCrs = new String(crsByteArray);
        try {
            return CRS.fromWKT(wktCrs);
        } catch (FactoryException ex) {
            throw new IOException(ex);
        }
    }
//
//    /**
//     * Retrieve the CRS of the input tree.
//     * @param treeFile The file containing the tree.
//     * @return The {@link org.opengis.referencing.crs.CoordinateReferenceSystem} in which teh tree is expressed.
//     * @throws java.lang.IllegalArgumentException if input file is null.
//     * @throws java.io.IOException if input file does not exists, or is a directory, or a problem happens at reading.
//     * @throws java.lang.ClassNotFoundException If the read object is corrupted,
//     */
//    public static CoordinateReferenceSystem getTreeCRS(final File treeFile) throws IOException, ClassNotFoundException {
//        ArgumentChecks.ensureNonNull("Input tree file", treeFile);
//        if (!treeFile.isFile()) {
//            throw new IOException("Input file is not a regular file : "+treeFile);
//        }
//        final RandomAccessFile raf = new RandomAccessFile(treeFile, "r");
//        final FileChannel fChannel = raf.getChannel();
//        final ByteBuffer buff = ByteBuffer.allocate(38);
//        fChannel.read(buff);
//        assert fChannel.position() == 38;
//        buff.flip();
//        buff.position(4);
//        final ByteOrder bO = (buff.get() == 1) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
//        buff.order(bO);
//        buff.position(34);
//        final int byteTabLength   = buff.getInt();
//        final byte[] crsByteArray = new byte[byteTabLength];
//        raf.seek(38);
//        raf.read(crsByteArray, 0, byteTabLength);
//
//        try (final ObjectInputStream crsInputS =
//                     new ObjectInputStream(new ByteArrayInputStream(crsByteArray))) {
//
//            return (CoordinateReferenceSystem) crsInputS.readObject();
//        }
//    }
}

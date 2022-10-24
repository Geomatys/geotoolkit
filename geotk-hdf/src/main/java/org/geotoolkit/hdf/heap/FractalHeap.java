/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.hdf.heap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.geotoolkit.hdf.IOStructure;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Each fractal heap consists of a header and zero or more direct and indirect
 * blocks (described below). The header contains general information as well as
 * initialization parameters for the doubling table. The Address of Root Block
 * field in the header points to the first direct or indirect block in the heap.
 * <p>
 * Fractal heaps are based on a data structure called a doubling table. A
 * doubling table provides a mechanism for quickly extending an array-like data
 * structure that minimizes the number of empty blocks in the heap, while
 * retaining very fast lookup of any element within the array. More information
 * on fractal heaps and doubling tables can be found in the RFC
 * “Private Heaps in HDF5.”
 * <p>
 * The fractal heap implements the doubling table structure with indirect and
 * direct blocks. Indirect blocks in the heap do not actually contain data for
 * objects in the heap, their “size” is abstract - they represent the indexing
 * structure for locating the direct blocks in the doubling table. Direct blocks
 * contain the actual data for objects stored in the heap.
 * <p>
 * All indirect blocks have a constant number of block entries in each row,
 * called the width of the doubling table (see Table Width field in the header).
 * The number of rows for each indirect block in the heap is determined by the
 * size of the block that the indirect block represents in the doubling table
 * (calculation of this is shown below) and is constant, except for the “root”
 * indirect block, which expands and shrinks its number of rows as needed.
 * <p>
 * Blocks in the first two rows of an indirect block are Starting Block Size
 * number of bytes in size. For example, if the row width of the doubling table
 * is 4, then the first eight block entries in the indirect block are Starting
 * Block Size number of bytes in size. The blocks in each subsequent row are
 * twice the size of the blocks in the previous row. In other words, blocks in
 * the third row are twice the Starting Block Size, blocks in the fourth row are
 * four times the Starting Block Size, and so on. Entries for blocks up to the
 * Maximum Direct Block Size point to direct blocks, and entries for blocks
 * greater than that size point to further indirect blocks (which have their
 * own entries for direct and indirect blocks). Starting Block Size and Maximum
 * Direct Block Size are fields stored in the header.
 * <p>
 * The number of rows of blocks, nrows, in an indirect block is calculated by
 * the following expression:
 * <p>
 * nrows = (log2(block_size) - log2(<Starting Block Size>)) + 1
 * <p>
 * where block_size is the size of the block that the indirect block represents
 * in the doubling table. For example, to represent a block with block_size
 * equals to 1024, and Starting Block Size equals to 256, three rows are needed.
 * <p>
 * The maximum number of rows of direct blocks, max_dblock_rows, in any indirect
 * block of a fractal heap is given by the following expression:
 * <p>
 * max_dblock_rows = (log2(<Maximum Direct Block Size>) - log2(<Starting Block Size>)) + 2
 * <p>
 * Using the computed values for nrows and max_dblock_rows, along with the width
 * of the doubling table, the number of direct and indirect block entries
 * (K and N in the indirect block description, below)
 * in an indirect block can be computed:
 * <p>
 * K = MIN(nrows, max_dblock_rows) * <Table Width>
 * <p>
 * If nrows is less than or equal to max_dblock_rows, N is 0. Otherwise, N is simply computed:
 * <p>
 * N = K - (max_dblock_rows * <Table Width>)
 * <p>
 * The size of indirect blocks on disk is determined by the number of rows in
 * the indirect block (computed above). The size of direct blocks on disk is
 * exactly the size of the block in the doubling table.
 * <p>
 * <p>
 * An object in the fractal heap is identified by means of a fractal heap ID,
 * which encodes information to locate the object in the heap. Currently, the
 * fractal heap stores an object in one of three ways, depending on the object’s size:
 * <ul>
 * <li>Tiny : When an object is small enough to be encoded in the heap ID, the
 * object’s data is embedded in the fractal heap ID itself. There are two
 * sub-types for this type of object: normal and extended. The sub-type for tiny
 * heap IDs depends on whether the heap ID is large enough to store objects
 * greater than 16 bytes or not. If the heap ID length is 18 bytes or smaller,
 * the ‘normal’ tiny heap ID form is used. If the heap ID length is greater than
 * 18 bytes in length, the “extended” form is used. See the format description
 * below for both sub-types. </li>
 * <li>Huge : When the size of an object is larger than Maximum Size of Managed
 * Objects in the Fractal Heap Header, the object’s data is stored on its own in
 * the file and the object is tracked/indexed via a version 2 B-tree. All huge
 * objects for a particular fractal heap use the same v2 B-tree. All huge objects
 * for a particular fractal heap use the same format for their huge object IDs.
 * <p>
 * Depending on whether the IDs for a heap are large enough to hold the object’s
 * retrieval information and whether I/O pipeline filters are applied to the heap’s
 * objects, 4 sub-types are derived for huge object IDs for this heap:
 *   <ul>
 *   <li>Directly accessed, non-filtered : The object’s address and length are
 *   embedded in the fractal heap ID itself and the object is directly accessed
 *   from them. This allows the object to be accessed without resorting to the
 *   B-tree. </li>
 *   <li>Directly accessed, filtered : The filtered object’s address, length,
 *   filter mask and de-filtered size are embedded in the fractal heap ID itself
 *   and the object is accessed directly with them. This allows the object to be
 *   accessed without resorting to the B-tree. </li>
 *   <li>Indirectly accessed, non-filtered : The object is located by using a
 *   B-tree key embedded in the fractal heap ID to retrieve the address and length
 *   from the version 2 B-tree for huge objects. Then, the address and length
 *   are used to access the object. </li>
 *   <li>Indirectly accessed, filtered : The object is located by using a B-tree
 *   key embedded in the fractal heap ID to retrieve the filtered object’s address,
 *   length, filter mask and de-filtered size from the version 2 B-tree for huge
 *   objects. Then, this information is used to access the object.</li>
 *   </ul>
 * </li>
 * <li>Managed : When the size of an object does not meet the above two conditions,
 * the object is stored and managed via the direct and indirect blocks based on
 * the doubling table. </li>
 * </ul>
 *
 * @see III.G. Disk Format: Level 1G - Fractal Heap
 * @author Johann Sorel (Geomatys)
 */
public final class FractalHeap extends IOStructure {

    /**
     * The ASCII character string “FRHP” is used to indicate the beginning of a
     * fractal heap header. This gives file consistency checking utilities a
     * better chance of reconstructing a damaged file.
     */
    public static final byte[] SIGNATURE = "FRHP".getBytes(StandardCharsets.US_ASCII);

    /**
     * This is the length in bytes of heap object IDs for this heap.
     */
    private int heapIDLength;
    /**
     * This is the size in bytes of the encoded I/O Filter Information.
     */
    private int ioFilterEncodedLength;
    /**
     * This field is the heap status flag and is a bit field indicating
     * additional information about the fractal heap.
     * <p>
     * Bits ;
     * <ul>
     * <li>0 : If set, the ID value to use for huge object has wrapped around.
     * If the value for the Next Huge Object ID has wrapped around, each new
     * huge object inserted into the heap will require a search for an ID value.</li>
     * <li>1 : If set, the direct blocks in the heap are checksummed.</li>
     * <li>2-7 : Reserved</li>
     * </ul>
     */
    private int flags;
    /**
     * This is the maximum size of managed objects allowed in the heap.
     * Objects greater than this this are ‘huge’ objects and will be stored
     * in the file directly, rather than in a direct block for the heap.
     */
    private int maximumSizeOfManagedObjects;
    /**
     * This is the next ID value to use for a huge object in the heap.
     */
    private long nextHugeObjectId;
    /**
     * This is the address of the v2 B-tree used to track huge objects in the
     * heap. The type of records stored in the v2 B-tree will be determined
     * by whether the address and length of a huge object can fit into a heap
     * ID (if yes, it is a “directly” accessed huge object) and whether there
     * is a filter used on objects in the heap.
     */
    private long v2BtreeAddressOfHugeObjects;
    /**
     * This is the total amount of free space in managed direct blocks (in bytes).
     */
    private long amountOfFreeSpaceInManagedBlocks;
    /**
     * This is the address of the Free-space Manager for managed blocks.
     */
    private long addressOfManagedBlockFreeSpaceManager;
    /**
     * This is the total amount of managed space in the heap (in bytes),
     * essentially the upper bound of the heap’s linear address space.
     */
    private long amountOfManagedSpaceInHeap;
    /**
     * This is the total amount of managed space (in bytes) actually allocated
     * in the heap. This can be less than the Amount of Managed Space in Heap
     * field, if some direct blocks in the heap’s linear address space are
     * not allocated.
     */
    private long amountOfAllocatedManagedSpaceInHeap;
    /**
     * This is the linear heap offset where the next direct block should be
     * allocated at (in bytes). This may be less than the Amount of Managed
     * Space in Heap value because the heap’s address space is increased by a
     * “row” of direct blocks at a time, rather than by single direct block
     * increments.
     */
    private long offsetOfDirectBlockAllocationIteratorOnManagedSpace;
    /**
     * This is the number of managed objects in the heap.
     */
    private long numberOfManagedObjectsInHeap;
    /**
     * This is the total size of huge objects in the heap (in bytes).
     */
    private long sizeOfHugeObjectsInHeap;
    /**
     * This is the number of huge objects in the heap.
     */
    private long numberOfHugeObjectsInHeap;
    /**
     * This is the total size of tiny objects that are packed in heap IDs (in bytes).
     */
    private long sizeOfTinyObjectsInHeap;
    /**
     * This is the number of tiny objects that are packed in heap IDs.
     */
    private long numberOfTinyObjectsInHeap;
    /**
     * This is the number of columns in the doubling table for managed blocks.
     * This value must be a power of two.
     */
    private int tableWidth;
    /**
     * This is the starting block size to use in the doubling table for managed
     * blocks (in bytes). This value must be a power of two.
     */
    private long startingBlockSize;
    /**
     * This is the maximum size allowed for a managed direct block.
     * Objects inserted into the heap that are larger than this value (less
     * the number of bytes of direct block prefix/suffix) are stored as ‘huge’
     * objects. This value must be a power of two.
     */
    private long maximumDirectBlockSize;
    /**
     * This is the maximum size of the heap’s linear address space for managed
     * objects (in bytes). The value stored is the log2 of the actual value,
     * that is: the number of bits of the address space. ‘Huge’ and ‘tiny’
     * objects are not counted in this value, since they do not store objects
     * in the linear address space of the heap.
     */
    private int maximumHeapSize;
    /**
     * This is the starting number of rows for the root indirect block.
     * A value of 0 indicates that the root indirect block will have the maximum
     * number of rows needed to address the heap’s Maximum Heap Size.
     */
    private int startingOfRowsInRootIndirectBlock;
    /**
     * This is the address of the root block for the heap. It can be the
     * undefined address if there is no data in the heap. It either points to a
     * direct block (if the Current # of Rows in the Root Indirect Block value
     * is 0), or an indirect block.
     */
    private long addressOfRootBlock;
    /**
     * This is the current number of rows in the root indirect block. A value
     * of 0 indicates that Address of Root Block points to direct block instead
     * of indirect block.
     */
    private int currentOfRowsInRootIndirectBlock;
    /**
     * This is the size of the root direct block, if filters are applied to
     * heap objects (in bytes). This field is only stored in the header if the
     * I/O Filters’ Encoded Length is greater than 0.
     */
    private long sizeOfFilteredRootDirectBlock;
    /**
     * This is the filter mask for the root direct block, if filters are applied
     * to heap objects. This mask has the same format as that used for the filter
     * mask in chunked raw data records in a v1 B-tree. This field is only stored
     * in the header if the I/O Filters’ Encoded Length is greater than 0.
     */
    private int ioFilterMask;
    /**
     * This is the I/O filter information encoding direct blocks and huge objects,
     * if filters are applied to heap objects. This field is encoded as a Filter
     * Pipeline message. The size of this field is determined by I/O Filters’
     * Encoded Length.
     */
    private int ioFilterInformation;

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        channel.ensureSignature(SIGNATURE);
        /*
        This document describes version 0.
        */
        channel.ensureVersion(0);
        heapIDLength = channel.readUnsignedShort();
        ioFilterEncodedLength = channel.readUnsignedShort();
        flags = channel.readUnsignedByte();
        maximumSizeOfManagedObjects = channel.readInt();
        nextHugeObjectId = channel.readLength();
        v2BtreeAddressOfHugeObjects = channel.readOffset();
        amountOfFreeSpaceInManagedBlocks = channel.readLength();
        addressOfManagedBlockFreeSpaceManager = channel.readOffset();
        amountOfManagedSpaceInHeap = channel.readLength();
        amountOfAllocatedManagedSpaceInHeap = channel.readLength();
        offsetOfDirectBlockAllocationIteratorOnManagedSpace = channel.readLength();
        numberOfManagedObjectsInHeap = channel.readLength();
        sizeOfHugeObjectsInHeap = channel.readLength();
        numberOfHugeObjectsInHeap = channel.readLength();
        sizeOfTinyObjectsInHeap = channel.readLength();
        numberOfTinyObjectsInHeap = channel.readLength();
        tableWidth = channel.readUnsignedShort();
        startingBlockSize = channel.readLength();
        maximumDirectBlockSize = channel.readLength();
        maximumHeapSize = channel.readUnsignedShort();
        startingOfRowsInRootIndirectBlock = channel.readUnsignedShort();
        addressOfRootBlock = channel.readOffset();
        currentOfRowsInRootIndirectBlock = channel.readUnsignedShort();
        sizeOfFilteredRootDirectBlock = channel.readLength();
        ioFilterMask = channel.readInt();

        /*
        ioFilterInformation

        Checksum
        */

        throw new IOException("TODO");
    }
}

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
package org.geotoolkit.hdf;

import java.io.IOException;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * The superblock may begin at certain predefined offsets within the HDF5 file,
 * allowing a block of unspecified content for users to place additional information
 * at the beginning (and end) of the HDF5 file without limiting the HDF5 Library’s
 * ability to manage the objects within the file itself.
 * This feature was designed to accommodate wrapping an HDF5 file in another file
 * format or adding descriptive information to an HDF5 file without requiring the
 * modification of the actual file’s information. The superblock is located by
 * searching for the HDF5 format signature at byte offset 0, byte offset 512,
 * and at successive locations in the file, each a multiple of two of the
 * previous location; in other words, at these byte offsets: 0, 512, 1024, 2048, and so on.
 *
 * <p>
 * The superblock is composed of the format signature, followed by a superblock
 * version number and information that is specific to each version of the superblock.
 *
 * <p>
 * Currently, there are four versions of the superblock format:
 * <ul>
 *   <li>Version 0 is the default format.</li>
 *   <li>Version 1 is the same as version 0 but with the “Indexed Storage Internal Node K”
 * field for storing non-default B-tree ‘K’ value.</li>
 *   <li>Version 2 has some fields eliminated and compressed from superblock format
 * versions 0 and 1. It has added checksum support and superblock extension to store additional superblock metadata.</li>
 *   <li>Version 3 is the same as version 2 except that the field “File Consistency Flags”
 * is used for file locking. This format version will enable support for the latest version.</li>
 * </ul>
 *
 * @see II.A. Disk Format: Level 0A - Format Signature and Superblock
 * @author Johann Sorel (Geomatys)
 */
public final class SuperBlock extends IOStructure {

    /**
     * This field contains a constant value and can be used to quickly identify
     * a file as being an HDF5 file. The constant value is designed to allow easy
     * identification of an HDF5 file and to allow certain types of data corruption to be detected.
     * <p>
     * This signature both identifies the file as an HDF5 file and provides for
     * immediate detection of common file-transfer problems.
     * The first two bytes distinguish HDF5 files on systems that expect the first
     * two bytes to identify the file type uniquely.
     * The first byte is chosen as a non-ASCII value to reduce the probability
     * that a text file may be misrecognized as an HDF5 file; also, it catches
     * bad file transfers that clear bit 7. Bytes two through four name the format.
     * The CR-LF sequence catches bad file transfers that alter newline sequences.
     * The control-Z character stops file display under MS-DOS.
     * The final line feed checks for the inverse of the CR-LF translation problem.
     * (This is a direct descendent of the PNG file signature.)
     * This field is present in version 0+ of the superblock.
     */
    public static final byte[] SIGNATURE = new byte[]{(byte) 137, 72, 68, 70, 13, 10, 26, 10};

    /**
     * This value is used to determine the format of the information in the superblock.
     * When the format of the information in the superblock is changed,
     * the version number is incremented to the next integer and can be used to
     * determine how the information in the superblock is formatted.
     * <p>
     * This field is present in version 0+ of the superblock.
     */
    private int superblockVersion;
    /**
     * This value is used to determine the format of the file’s free space information.
     * The only value currently valid in this field is ‘0’,
     * <p>
     * This field is present in versions 0 and 1 of the superblock.
     */
    private int freespaceVersion;
    /**
     * This value is used to determine the format of the information in the Root
     * Group Symbol Table Entry. When the format of the information in that field
     * is changed, the version number is incremented to the next integer and can
     * be used to determine how the information in the field is formatted.
     * <p>
     * The only value currently valid in this field is ‘0’.
     * <p>
     * This field is present in version 0 and 1 of the superblock.
     */
    private int rootGroupSymbolTableVersion;
    /**
     * This value is used to determine the format of the information in a shared
     * object header message. Since the format of the shared header messages
     * differs from the other private header messages, a version number is used
     * to identify changes in the format.
     * <p>
     * The only value currently valid in this field is ‘0’.
     * <p>
     * This field is present in version 0 and 1 of the superblock.
     */
    private int sharedHeaderMessageVersion;
    /**
     * This value contains the number of bytes used to store addresses in the file.
     * The values for the addresses of objects in the file are offsets relative
     * to a base address, usually the address of the superblock signature.
     * This allows a wrapper to be added after the file is created without
     * invalidating the internal offset locations.
     * <p>
     * This field is present in version 0+ of the superblock.
     */
    private int sizeOfOffsets;
    /**
     * This value contains the number of bytes used to store the size of an object.
     * <p>
     * This field is present in version 0+ of the superblock.
     */
    private int sizeOfLengths;
    /**
     * Each leaf node of a group B-tree will have at least this many entries but
     * not more than twice this many.
     * If a group has a single leaf node then it may have fewer entries.
     * <p>
     * This value must be greater than zero.
     * <p>
     * This field is present in version 0 and 1 of the superblock.
     */
    private int groupLeafNodeK;
    /**
     * Each internal node of a group B-tree will have at least this many entries
     * but not more than twice this many. If the group has only one internal node
     * then it might have fewer entries.
     * <p>
     * This value must be greater than zero.
     * <p>
     * This field is present in version 0 and 1 of the superblock.
     */
    public int groupInternalNodeK;
    /**
     * For superblock version 0,1,2:This field is unused and should be ignored.
     * <p>
     * For superblock version 3: This value contains flags to ensure file consistency
     * for file locking. Currently, the following bit flags are defined:
     * Bit 0 if set indicates that the file has been opened for write access.
     * Bit 1 is reserved for future use.
     * Bit 2 if set indicates that the file has been opened for single-writer/multiple-reader (SWMR) write access.
     * Bits 3-7 are reserved for future use.
     *
     * Bit 0 should be set as the first action when a file has been opened for write access. Bit 2 should be set when a file has been opened for SWMR write access. These two bits should be cleared only as the final action when closing a file.
     * The size of this field has been reduced from 4 bytes in superblock format versions 0 and 1 to 1 byte.
     *
     * This field is present in version 0+ of the superblock.
     */
    private int fileConsistencyFlags;
    /**
     * Each internal node of an indexed storage B-tree will have at least this
     * many entries but not more than twice this many. If the index storage B-tree
     * has only one internal node then it might have fewer entries.
     * <p>
     * This value must be greater than zero.
     * <p>
     * This field is present in version 1 of the superblock.
     */
    private int indexedStorageInternalNodeK;
    /**
     * This is the absolute file address of the first byte of the HDF5 data within the file.
     * The library currently constrains this value to be the absolute file address
     * of the superblock itself when creating new files; future versions of the
     * library may provide greater flexibility. When opening an existing file and
     * this address does not match the offset of the superblock, the library assumes
     * that the entire contents of the HDF5 file have been adjusted in the file and
     * adjusts the base address and end of file address to reflect their new positions
     * in the file. Unless otherwise noted, all other file addresses are relative to this base address.
     * <p>
     * This field is present in version 0+ of the superblock.
     */
    private long baseAddress;
    /**
     * The file’s free space is not persistent for version 0 and 1 of the superblock.
     * Currently this field always contains the undefined address.
     * <p>
     * This field is present in version 0 and 1 of the superblock.
     */
    private long globalFreeSpaceIndexAddress;
    /**
     * This is the absolute file address of the first byte past the end of all HDF5 data.
     * It is used to determine whether a file has been accidentally truncated and
     * as an address where file data allocation can occur if space from the free list is not used.
     * <p>
     * This field is present in version 0+ of the superblock.
     */
    private long endOfFileAddress;
    /**
     * This is the relative file address of the file driver information block
     * which contains driver-specific information needed to reopen the file.
     * If there is no driver information block then this entry should be the undefined address.
     * <p>
     * This field is present in version 0 and 1 of the superblock.
     */
    private long driverInformationBlockAddress;
    /**
     * This is the symbol table entry of the root group, which serves as the entry
     * point into the group graph for the file.
     * <p>
     * This field is present in version 0 and 1 of the superblock.
     */
    public SymbolTableEntry rootGroupSymbolTableEntry;
    /**
     * The field is the address of the object header for the superblock extension.
     * If there is no extension then this entry should be the undefined address.
     * <p>
     * This field is present in version 2 and 3 of the superblock.
     * <p>
     * The extension can be :
     * <ul>
     * <li>Shared Message Table message containing information to locate the
     * master table of shared object header message indices.</li>
     * <li>B-tree ‘K’ Values message containing non-default B-tree ‘K’ values.</li>
     * <li>Driver Info message containing information needed by the file driver
     * in order to reopen a file. See also the “Disk Format: Level 0B - File Driver Info” section above.</li>
     * <li>File Space Info message containing information about file space handling in the file.</li>
     * </ul>
     */
    private long superblockExtensionAddress;
    /**
     * This is the address of the root group object header, which serves as the
     * entry point into the group graph for the file.
     * <p>
     * This field is present in version 2 and 3 of the superblock.
     */
    private long rootGroupObjectHeaderAddress;

    public int getSizeOfLengths() {
        return sizeOfLengths;
    }

    public int getSizeOfOffsets() {
        return sizeOfOffsets;
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        channel.ensureSignature(SIGNATURE);
        superblockVersion = channel.readUnsignedByte();
        if (superblockVersion == 0 || superblockVersion == 1) {
            freespaceVersion = channel.readUnsignedByte();
            rootGroupSymbolTableVersion = channel.readUnsignedByte();
            channel.skipFully(1); //reserved
            sharedHeaderMessageVersion = channel.readUnsignedByte();
            sizeOfOffsets = channel.readUnsignedByte();
            sizeOfLengths = channel.readUnsignedByte();
            channel.setOffsetSize(sizeOfOffsets);
            channel.setLengthSize(sizeOfLengths);
            channel.skipFully(1); //reserved
            groupLeafNodeK = channel.readUnsignedShort();
            groupInternalNodeK = channel.readUnsignedShort();
            fileConsistencyFlags = channel.readInt();
            if (superblockVersion == 1) {
                indexedStorageInternalNodeK = channel.readUnsignedShort();
                channel.skipFully(2);
            }
            baseAddress = channel.readOffset();
            globalFreeSpaceIndexAddress = channel.readOffset();
            endOfFileAddress = channel.readOffset();
            driverInformationBlockAddress = channel.readOffset();

            rootGroupSymbolTableEntry = new SymbolTableEntry();
            rootGroupSymbolTableEntry.read(channel);
        } else if (superblockVersion == 2 || superblockVersion == 3) {
            sizeOfOffsets = channel.readUnsignedByte();
            sizeOfLengths = channel.readUnsignedByte();
            channel.setOffsetSize(sizeOfOffsets);
            channel.setLengthSize(sizeOfLengths);
            fileConsistencyFlags = channel.readUnsignedByte();
            superblockExtensionAddress = channel.readOffset();
            endOfFileAddress = channel.readOffset();
            rootGroupObjectHeaderAddress = channel.readOffset();
            int checksum = channel.readInt();//ignored for now
        }
    }

    @Override
    public String toString() {
        return IOStructure.reflectionToString(this);
    }
}

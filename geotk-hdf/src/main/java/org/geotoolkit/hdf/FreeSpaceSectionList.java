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
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * The free-space sections being managed are stored in a free-space section list,
 * described below. The sections in the free-space section list are stored in the
 * following way: a count of the number of sections describing a particular size
 * of free space and the size of the free-space described (in bytes), followed
 * by a list of section description records; then another section count and size,
 * followed by the list of section descriptions for that size; and so on.
 *
 * @see III.H. Disk Format: Level 1H - Free-space Manager
 * @author Johann Sorel (Geomatys)
 */
public final class FreeSpaceSectionList extends IOStructure {

    /**
     * The ASCII character string “FSSE” is used to indicate the beginning of
     * the Free-space Section Information. This gives file consistency checking
     * utilities a better chance of reconstructing a damaged file.
     */
    public static final byte[] SIGNATURE = "FSSE".getBytes(StandardCharsets.US_ASCII);

    /**
     * This is the address of the Free-space Manager Header. This field is
     * principally used for file integrity checking.
     */
    private int freeSpaceManagerHeaderAddress;
    /**
     * This is the number of free-space section records for set #N. The length
     * of this field is the minimum number of bytes needed to store the number
     * of serialized sections (from the free-space manager header).
     * <p>
     * The number of sets of free-space section records is determined by the
     * size of serialized section list in the free-space manager header.
     */
    private int numberOfSectionRecordsInSets;
    /**
     * This is the size (in bytes) of the free-space section described for all
     * the section records in set #N.
     * The length of this field is the minimum number of bytes needed to store
     * the maximum section size (from the free-space manager header).
     */
    public List<Integer> sizeOfFreeSpaceSectionDescribedInRecordSets;
    /**
     * This is the offset (in bytes) of the free-space section within the client
     * for the free-space manager.
     * The length of this field is the minimum number of bytes needed to store
     * the size of address space (from the free-space manager header).
     */
    public List<Integer> recordSetSectionRecordOffset;
    /**
     * This is the type of the section record, used to decode the record set #N
     * section #K data information. The defined record type for file client is:
     * <ul>
     * <li>0 : File’s section (a range of actual bytes in file)</li>
     * <li>1+ : Reserved.</li>
     * </ul>
     * The defined record types for a fractal heap client are:
     * <ul>
     * <li>0 Fractal heap “single” section</li>
     * <li>1 Fractal heap “first row” section</li>
     * <li>2 Fractal heap “normal row” section</li>
     * <li>3 Fractal heap “indirect” section</li>
     * <li>4+ Reserved. </li>
     * </ul>
     */
    public List<Integer> recordSetSectionRecordType;
    /**
     * This is the section-type specific information for each record in the
     * record set, described below.
     */
    public List<byte[]> recordSetSectionRecordData;

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        channel.ensureSignature(SIGNATURE);
        /*
        This is the version number for the Free-space Section List and this
        document describes version 0.
         */
        channel.ensureVersion(0);

        /*
        all lists
        checksum
        */
        throw new IOException("TODO");
    }
}

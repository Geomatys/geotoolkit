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
package org.geotoolkit.hdf.message;

import java.io.IOException;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Header Message Name: Dataspace
 * <p>
 * Header Message Type: 0x0001
 * <p>
 * Length: Varies according to the number of dimensions, as described in the following table.
 * <p>
 * Status: Required for dataset objects; may not be repeated.
 * <p>
 * Description: The dataspace message describes the number of dimensions
 * (in other words, “rank”) and size of each dimension that the data object has.
 * This message is only used for datasets which have a simple, rectilinear,
 * array-like layout; datasets requiring a more complex layout are not yet supported.
 * <p>
 * Version 2 of the dataspace message dropped the optional permutation index
 * value support, as it was never implemented in the HDF5 Library.
 *
 * @see IV.A.2.b. The Dataspace Message
 * @author Johann Sorel (Geomatys)
 */
public final class DataspaceMessage extends Message {

    /**
     * This value is the number of dimensions that the data object has.
     */
    private int dimensionality;
    /**
     * This field is used to store flags to indicate the presence of parts of
     * this message. Bit 0 (the least significant bit) is used to indicate that
     * maximum dimensions are present. Bit 1 is used to indicate that permutation
     * indices are present.
     */
    private int flags;
    /**
     * This field indicates the type of the dataspace:
     * 0 : A scalar dataspace; in other words, a dataspace with a single,
     *   dimensionless element.
     * 1 : A simple dataspace; in other words, a dataspace with a rank greater
     *   than 0 and an appropriate number of dimensions.
     * 2 : A null dataspace; in other words, a dataspace with no elements.
     * <p>
     * Only in version 2.
     */
    private int type;
    /**
     * This value is the current size of the dimension of the data as stored in
     * the file. The first dimension stored in the list of dimensions is the
     * slowest changing dimension and the last dimension stored is the fastest
     * changing dimension.
     */
    private long[] dimensionSizes;
    /**
     * This value is the maximum size of the dimension of the data as stored in
     * the file. This value may be the special “unlimited” size which indicates
     * that the data may expand along this dimension indefinitely. If these
     * values are not stored, the maximum size of each dimension is assumed to
     * be the dimension’s current size.
     */
    private long[] dimensionMaximumSizes;
    /**
     * This value is the index permutation used to map each dimension from the
     * canonical representation to an alternate axis for each dimension. If these
     * values are not stored, the first dimension stored in the list of dimensions
     * is the slowest changing dimension and the last dimension stored is the
     * fastest changing dimension.
     *
     * This property has never been implemented by libhdf.
     */
    private long[] permutationIndexes;

    /**
     * This value is the current size of the dimension of the data as stored in
     * the file. The first dimension stored in the list of dimensions is the
     * slowest changing dimension and the last dimension stored is the fastest
     * changing dimension.
     */
    public int[] getDimensionSizes() {
        final int[] size = new int[dimensionSizes.length];
        for (int i = 0; i < size.length; i++) {
            size[i] = Math.toIntExact(dimensionSizes[i]);
        }
        return size;
    }

    /**
     * This value is the maximum size of the dimension of the data as stored in
     * the file. This value may be the special “unlimited” size which indicates
     * that the data may expand along this dimension indefinitely. If these
     * values are not stored, the maximum size of each dimension is assumed to
     * be the dimension’s current size.
     */
    public int[] getDimensionMaximumSizes() {
        final int[] maxs = new int[dimensionMaximumSizes.length];
        for (int i = 0; i < maxs.length; i++) {
            maxs[i] = Math.toIntExact(dimensionMaximumSizes[i]);
        }
        return maxs;
    }

    /**
     * The dataspace is completly empty and contains no even a single data.
     * This can be true only with version 2.
     */
    public boolean isNull() {
        return type == 2;
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        final int version = channel.readUnsignedByte();

        if (version == 1) {
            dimensionality = channel.readUnsignedByte();
            flags = channel.readUnsignedByte();
            channel.skipFully(5);
            dimensionSizes = new long[dimensionality];
            for (int i = 0; i < dimensionality; i++) {
                dimensionSizes[i] = channel.readLength();
            }
            if ((flags & 1) != 0) {
                dimensionMaximumSizes = new long[dimensionality];
                for (int i = 0; i < dimensionality; i++) {
                    dimensionMaximumSizes[i] = channel.readLength();
                }
            }
            if ((flags & 2) != 0) {
                permutationIndexes = new long[dimensionality];
                for (int i = 0; i < dimensionality; i++) {
                    permutationIndexes[i] = channel.readLength();
                }
            }
        } else if (version == 2) {
            dimensionality = channel.readUnsignedByte();
            flags = channel.readUnsignedByte();
            /*
            This field indicates the type of the dataspace:
            0 : A scalar dataspace; in other words, a dataspace with a single,
                dimensionless element.
            1 : A simple dataspace; in other words, a dataspace with a rank greater
                than 0 and an appropriate number of dimensions.
            2 : A null dataspace; in other words, a dataspace with no elements.
            */
            type = channel.readUnsignedByte();
            dimensionSizes = new long[dimensionality];
            for (int i = 0; i < dimensionality; i++) {
                dimensionSizes[i] = channel.readLength();
            }
            if ((flags & 1) != 0) {
                dimensionMaximumSizes = new long[dimensionality];
                for (int i = 0; i < dimensionality; i++) {
                    dimensionMaximumSizes[i] = channel.readLength();
                }
            }
        } else {
            throw new IOException("Unexpected dataspace version " + version);
        }
    }
}

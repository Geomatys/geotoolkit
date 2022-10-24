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
 * Header Message Name: Attribute Info
 * <p>
 * Header Message Type: 0x0015
 * <p>
 * Length: Varies
 * <p>
 * Status: Optional; may not be repeated.
 * <p>
 * Description:
 * This message stores information about the attributes on an object, such as
 * the maximum creation index for the attributes created and the location of
 * the attribute storage when the attributes are stored “densely”.
 *
 * @see IV.A.2.v. The Attribute Info Message
 * @author Johann Sorel (Geomatys)
 */
public final class AttributeInfoMessage extends Message {

    /**
     * This is the attribute index information flag with the following
     * definition:
     * Bit
     * 0 : If set, creation order for attributes is tracked.
     * 1 : If set, creation order for attributes is indexed.
     * 2-7 : Reserved
     */
    private int flags;
    /**
     * This field is present if bit 0 of Flags is set.
     *
     * The is the maximum creation order index value for the attributes on the
     * object.
     */
    private int maximumCreationIndex;
    /**
     * This is the address of the fractal heap to store dense attributes. Each
     * attribute stored in the fractal heap is described by the Attribute
     * Message.
     */
    private long fractalHeapAddress;
    /**
     * This is the address of the version 2 B-tree to index the names of densely
     * stored attributes.
     */
    private long attributeNameV2btreeAddress;
    /**
     * This field is present if bit 1 of Flags is set.
     *
     * This is the address of the version 2 B-tree to index the creation order
     * of densely stored attributes.
     */
    private long attributeCreationOrderV2btreeAddress;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        The version number for this message. This document describes version 0.
        */
        channel.ensureVersion(0);
        flags = channel.readUnsignedByte();
        if ((flags & 1) != 0) maximumCreationIndex = channel.readUnsignedShort();
        fractalHeapAddress = channel.readOffset();
        attributeNameV2btreeAddress = channel.readOffset();
        if ((flags & 2) != 0) attributeCreationOrderV2btreeAddress = channel.readOffset();
    }
}

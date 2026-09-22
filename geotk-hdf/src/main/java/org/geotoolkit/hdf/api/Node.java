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
package org.geotoolkit.hdf.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.hdf.HDF5Provider;
import org.geotoolkit.hdf.ObjectHeader;
import org.geotoolkit.hdf.io.Connector;
import org.geotoolkit.hdf.io.HDF5DataInput;
import org.geotoolkit.hdf.message.DataspaceMessage;
import org.geotoolkit.hdf.message.DatatypeMessage;
import org.geotoolkit.hdf.message.Message;
import org.opengis.util.GenericName;

/**
 * Parent interface for {@link Group} and {@link Dataset}.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Node extends Resource {

    /**
     * Returns the parent group, or {@code null} for the root group.
     *
     * @return the parent group, or {@code null} for the root group
     */
    Group getParent();

    /**
     * Returns the name of this node within its parent group, or the file name for the root group.
     *
     * @return the name of this node within its parent group, or the file name for the root group
     */
    String getName();

    /**
     * Returns the offset in the file of this node object header.
     * This is used by Reference data types.
     *
     * @return the offset in the file of this node object header
     */
    long getAddress();

    /**
     * Search the resource and its children for the node with given address.
     *
     * @param address object header address of the searched node.
     * @return the node, or {@code null} if not found.
     * @throws DataStoreException if the children of a group could not be listed.
     */
    default Node findNode(long address) throws DataStoreException {
        if (getAddress() == address) {
            return this;
        }
        if (this instanceof Aggregate agg) {
            for (Resource r : agg.components()) {
                if (r instanceof Node n) {
                    Node cdt = n.findNode(address);
                    if (cdt != null) return cdt;
                }
            }
        }
        return null;
    }

    /**
     * Returns the attributes of this node by name, never {@code null}.
     *
     * @return the attributes of this node by name, never {@code null}
     */
    Map<String,Object> getAttributes();

    /**
     * Read the object header located at the given address.
     * <p>
     * The header version is deduced from its first byte : version 1 headers start
     * with the version number itself, version 2 headers start with the {@code OHDR}
     * signature whose first character is {@code 'O'} (79).
     *
     * @param channel channel to read from, its position is restored on return
     * @param objectHeaderAddress address of the object header
     * @return the decoded header, never {@code null}
     * @throws IOException if an I/O error occurred.
     * @throws DataStoreException if the header is invalid or unsupported.
     */
    static ObjectHeader readHeader(HDF5DataInput channel, long objectHeaderAddress) throws IOException, DataStoreException {
        channel.mark();
        try {
            channel.seek(objectHeaderAddress);
            final int version = channel.readUnsignedByte();
            final ObjectHeader header = ObjectHeader.forVersion(version);
            channel.seek(objectHeaderAddress);
            header.read(channel);
            return header;
        } finally {
            channel.reset();
        }
    }

    /**
     * Read the object header located at the given address, using a channel of its own.
     *
     * @param connector file connector.
     * @param objectHeaderAddress address of the object header
     * @return the decoded header, never {@code null}
     * @throws IOException if an I/O error occurred.
     * @throws DataStoreException if the header is invalid or unsupported.
     * @see #readHeader(HDF5DataInput, long)
     */
    static ObjectHeader readHeader(Connector connector, long objectHeaderAddress) throws IOException, DataStoreException {
        try (HDF5DataInput channel = connector.createChannel()) {
            return readHeader(channel, objectHeaderAddress);
        }
    }

    /**
     * Create the node whose object header is at the given address.
     * <p>
     * The kind of node is decided from the header content rather than from any
     * external hint : an object carrying both a datatype and a dataspace is a
     * dataset, anything else is a group. This is the only classification that
     * works for both the symbol table based groups of the 1.6 era and the link
     * based groups introduced in 1.8. In particular it avoids relying on the
     * symbol table entry cache type, whose value 0 only means "nothing cached"
     * and legitimately occurs for groups whose hard link count exceeds one.
     *
     * @param parent parent group, {@code null} for the root group
     * @param connector file connector, not {@code null}
     * @param objectHeaderAddress address of the node object header
     * @param name name of the node within its parent
     * @return a {@link Group} or a {@link Dataset}, never {@code null}
     * @throws IOException if an I/O error occurred.
     * @throws DataStoreException if the node content is invalid or unsupported.
     */
    static Node create(Group parent, Connector connector, long objectHeaderAddress, String name)
            throws IOException, DataStoreException {
        final ObjectHeader header = readHeader(connector, objectHeaderAddress);

        boolean hasDatatype = false;
        boolean hasDataspace = false;
        for (Message message : header.getMessages()) {
            if (message instanceof DatatypeMessage) {
                hasDatatype = true;
            } else if (message instanceof DataspaceMessage) {
                hasDataspace = true;
            }
        }

        if (hasDatatype && hasDataspace) {
            return new Dataset(parent, connector, objectHeaderAddress, name, header);
        }
        if (hasDatatype) {
            /*
            A datatype without a dataspace is a committed (named) datatype. It is
            neither a group nor a dataset, expose it as an empty group so that the
            resource tree stays navigable.
            */
            HDF5Provider.LOGGER.log(Level.FINE, () -> "Object " + name
                    + " is a committed datatype, exposed as an empty group.");
        }
        return new Group(parent, connector, objectHeaderAddress, name, header);
    }

    /**
     * Returns the name of the given node.
     * This is a local name for the root group, otherwise a scoped name made of the names
     * of the node and its ancestors, excluding the root group.
     *
     * @param node node to name.
     * @return the name of the given node
     */
    static GenericName createName(Node node) {
        if (node.getParent() == null) {
            //root node
            return Names.createLocalName(null, null, node.getName());
        }

        final List<String> parts = new ArrayList<>();
        for (Node n = node; n != null && n.getParent() != null; n = n.getParent()) {
            parts.add(0, n.getName());
        }
        return Names.createGenericName(null, null, parts.toArray(String[]::new));
    }
}

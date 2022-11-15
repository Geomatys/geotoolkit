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
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * A group is an object internal to the file that allows arbitrary nesting of
 * objects within the file (including other groups). A group maps a set of link
 * names in the group to a set of relative file addresses of objects in the file.
 * Certain metadata for an object to which the group points can be cached in the
 * group’s symbol table entry in addition to being in the object’s header.
 * <p>
 * An HDF5 object name space can be stored hierarchically by partitioning the
 * name into components and storing each component as a link in a group. The
 * link for a non-ultimate component points to the group containing the next
 * component. The link for the last component points to the object being named.
 * <p>
 * One implementation of a group is a collection of symbol table nodes indexed
 * by a B-tree. Each symbol table node contains entries for one or more links.
 * If an attempt is made to add a link to an already full symbol table node
 * containing 2K entries, then the node is split and one node contains K symbols
 * and the other contains K+1 symbols.
 *
 * @see III.B. Disk Format: Level 1B - Group Symbol Table Nodes
 * @author Johann Sorel (Geomatys)
 */
public final class SymbolTableNode extends IOStructure {

    /**
     * The ASCII character string “SNOD” is used to indicate the beginning of a
     * symbol table node. This gives file consistency checking utilities a
     * better chance of reconstructing a damaged file.
     */
    public static final byte[] SIGNATURE = "SNOD".getBytes(StandardCharsets.US_ASCII);

    /**
     * Although all symbol table nodes have the same length, most contain fewer
     * than the maximum possible number of link entries. This field indicates
     * how many entries contain valid data. The valid entries are packed at the
     * beginning of the symbol table node while the remaining entries contain
     * undefined values.
     */
    private int numberOfEntries;

    /**
     * Each link has an entry in the symbol table node. The format of the entry
     * is described below. There are 2K entries in each group node, where K is
     * the “Group Leaf Node K” value from the superblock.
     */
    public List<SymbolTableEntry> entries;

    public void read(HDF5DataInput channel) throws IOException {

        channel.ensureSignature(SIGNATURE);
        /*
        The version number for the symbol table node. This document describes
        version 1. (There is no version ‘0’ of the symbol table node)
         */
        channel.ensureVersion(1);
        channel.skipFully(1);
        numberOfEntries = channel.readUnsignedShort();

        entries = new ArrayList<>(numberOfEntries);
        for (int i = 0; i < numberOfEntries;i++) {
            SymbolTableEntry entry  = new SymbolTableEntry();
            entry.read(channel);
            entries.add(entry);
        }
    }

    @Override
    public String toString() {
        return IOStructure.reflectionToString(this);
    }
}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.index.rtree.fs;

import java.nio.channels.FileChannel;
import java.util.Stack;

import org.geotoolkit.index.DataDefinition;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @source $URL:
 *         http://svn.geotools.org/geotools/trunk/gt/modules/plugin/shapefile/src/main/java/org/geotools/index/rtree/fs/Parameters.java $
 */
public class Parameters {
    private int maxNodeEntries;
    private int minNodeEntries;
    private short splitAlg;
    private DataDefinition dataDef;
    private FileChannel channel;
    private Stack freePages;
    private boolean forceChannel;

    public Parameters() {
        this.freePages = new Stack();
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public FileChannel getChannel() {
        return channel;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public DataDefinition getDataDef() {
        return dataDef;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public int getMaxNodeEntries() {
        return maxNodeEntries;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public int getMinNodeEntries() {
        return minNodeEntries;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public short getSplitAlg() {
        return splitAlg;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param channel
     */
    public void setChannel(FileChannel channel) {
        this.channel = channel;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param definition
     */
    public void setDataDef(DataDefinition definition) {
        dataDef = definition;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param i
     */
    public void setMaxNodeEntries(int i) {
        maxNodeEntries = i;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param i
     */
    public void setMinNodeEntries(int i) {
        minNodeEntries = i;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param s
     */
    public void setSplitAlg(short s) {
        splitAlg = s;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public boolean getForceChannel() {
        return forceChannel;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param b
     */
    public void setForceChannel(boolean b) {
        forceChannel = b;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public Stack getFreePages() {
        return freePages;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param stack
     */
    public void setFreePages(Stack stack) {
        freePages = stack;
    }
}

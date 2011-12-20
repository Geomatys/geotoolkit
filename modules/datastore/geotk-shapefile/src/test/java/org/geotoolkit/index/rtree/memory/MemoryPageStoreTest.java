/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
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
package org.geotoolkit.index.rtree.memory;

import org.geotoolkit.index.DataDefinition;
import org.geotoolkit.index.TreeException;
import org.geotoolkit.index.rtree.PageStore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @module pending
 */
public class MemoryPageStoreTest {

    /*
     * Test for void MemoryPageStoreTest(DataDefinition)
     */
    @Test
    public void testMemoryPageStoreTestDataDefinition() throws Exception {
        DataDefinition dd = new DataDefinition("US-ASCII");

        try {
            new MemoryPageStore(dd);
            fail("Cannot use an empty DataDefinition");
        } catch (TreeException e) {
            // OK
        }

        dd.addField(Integer.class);

        MemoryPageStore ps = new MemoryPageStore(dd);
        ps.close();
    }

    /*
     * Test for void MemoryPageStore(DataDefinition, int, int, short)
     */
    @Test
    public void testMemoryPageStoreDataDefinitionintintshort() throws Exception {
        DataDefinition dd = new DataDefinition("US-ASCII");
        dd.addField(Integer.class);

        MemoryPageStore ps = null;

        try {
            ps = new MemoryPageStore(dd, 10, 10, PageStore.SPLIT_QUADRATIC);
            fail("MinNodeEntries must be <= MaxNodeEntries / 2");
        } catch (TreeException e) {
            // OK
        }

        try {
            ps = new MemoryPageStore(dd, 10, 5, (short) 1000);
            fail("SplitAlgorithm not supported");
        } catch (TreeException e) {
            // OK
        }

        ps = new MemoryPageStore(dd, 50, 25, PageStore.SPLIT_QUADRATIC);
        ps.close();
    }
}

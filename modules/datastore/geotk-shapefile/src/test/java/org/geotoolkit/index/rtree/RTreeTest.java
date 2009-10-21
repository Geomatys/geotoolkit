/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
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
package org.geotoolkit.index.rtree;

import com.vividsolutions.jts.geom.Envelope;
import junit.framework.TestCase;
import org.geotoolkit.index.Data;
import org.geotoolkit.index.DataDefinition;
import org.geotoolkit.index.rtree.fs.FileSystemPageStore;
import org.geotoolkit.index.rtree.memory.MemoryPageStore;
import java.io.File;
import java.util.Collection;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @source $URL:
 *         http://svn.geotools.org/geotools/trunk/gt/modules/plugin/shapefile/src/test/java/org/geotools/index/rtree/RTreeTest.java $
 * @module pending
 */
public class RTreeTest extends TestCase {
    private static final short FILE = 0;
    private static final short MEMORY = 1;
    private static final short NUM_IMPLEMENTATIONS = 2;
    private DataDefinition dd = null;

    /**
     * Constructor for RTreeTest.
     * 
     * @param arg0
     */
    public RTreeTest(String arg0) {
        super(arg0);
    }

    private RTree getRTree(short type) throws Exception {
        dd = new DataDefinition("US-ASCII");
        dd.addField(Integer.class);

        PageStore ps = null;

        switch (type) {
        case FILE:

            File file = File.createTempFile("geotools2", ".grx");
            file.deleteOnExit();
            ps = new FileSystemPageStore(file, dd);

            break;

        case MEMORY:
            ps = new MemoryPageStore(dd);

            break;
        }

        return new RTree(ps);
    }

    private RTree getFullRTree(short type) throws Exception {
        RTree idx = getRTree(type);

        Data data = null;
        Envelope env = null;

        for (int i = 0; i < 200; i += 2) {
            env = new Envelope(i, i + 1, i, i + 1);
            data = new Data(dd);
            data.addValue(new Integer(i));
            idx.insert(env, data);
        }

        return idx;
    }

    public void testRTree() throws Exception {
        for (short ni = 0; ni < NUM_IMPLEMENTATIONS; ni++) {
            this.getRTree(ni).close();
        }
    }

    /*
     * Test for Collection search(Envelope)
     */
    public void testSearchEnvelope() throws Exception {
        for (short ni = 0; ni < NUM_IMPLEMENTATIONS; ni++) {
            RTree idx = this.getFullRTree(ni);

            Envelope env = new Envelope(2, 6, 2, 6);
            Collection res = idx.search(env);

            assertEquals(res.size(), 3);

            idx.close();
        }
    }

    /*
     * Test for Collection search(Filter)
     */
    public void testSearchFilter() {
        // TODO Write the test
    }

    public void testInsert() throws Exception {
        for (short ni = 0; ni < NUM_IMPLEMENTATIONS; ni++) {
            RTree idx = this.getFullRTree(ni);
            idx.close();
        }
    }

    public void testDelete() throws Exception {
        for (short ni = 0; ni < NUM_IMPLEMENTATIONS; ni++) {
            RTree idx = this.getFullRTree(ni);

            Envelope env = new Envelope(4, 5, 4, 5);

            idx.delete(env);

            env = new Envelope(2, 6, 2, 6);

            Collection res = idx.search(env);

            assertEquals(res.size(), 2);

            idx.close();
        }
    }
}

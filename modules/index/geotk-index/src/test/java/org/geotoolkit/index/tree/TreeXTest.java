/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.index.tree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.filter.SpatialFilterType;
import org.geotoolkit.index.tree.star.FileStarRTree;
import org.geotoolkit.referencing.crs.PredefinedCRS;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Test static TreeX methods.<br/><br/>
 *
 * Intersect test is already effectuate by tree test suite.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class TreeXTest extends TreeTest {

    Tree tree ;
    private TreeXElementMapperTest tXEM ;

    private static final CoordinateReferenceSystem CRS_TEST = PredefinedCRS.CARTESIAN_3D;

    public TreeXTest() throws StoreIndexException, IOException {
        tXEM = new TreeXElementMapperTest();
        tree = new FileStarRTree(File.createTempFile("TreeX", "test"), 4, CRS_TEST, tXEM);
        final GeneralEnvelope geTemp = new GeneralEnvelope(CRS_TEST);
        for(int z = 0; z <= 200; z += 20) {
            for(int y = 0; y <= 200; y += 20) {
                for(int x = 0; x <= 200; x += 20) {
                    geTemp.setEnvelope(x-5, y-5, z-5, x+5, y+5, z+5);
                    tree.insert(new GeneralEnvelope(geTemp));
                }
            }
        }
    }

    /**
     * Find and return objects which match with them tree identifiers.
     *
     * @param tabID integer table which contain tree identifier return by seach action.
     * @return objects which match with them tree identifiers.
     */
    private List<Envelope> getresult(int[] tabID) {
        List<Envelope> lResult = new ArrayList<Envelope>(tabID.length);
        for (int i = 0, s = tabID.length; i < s; i++) {
            lResult.add(tXEM.getObjectFromTreeIdentifier(tabID[i]));
        }
        return lResult;
    }

    /**
     * Test result {@link Envelope} which contain specified search Envelope.
     *
     * @throws StoreIndexException
     */
    @Test
    public void testContains() throws StoreIndexException {
        final List<Envelope> listRef = new ArrayList<Envelope>();
        final GeneralEnvelope geTemp = new GeneralEnvelope(CRS_TEST);
        geTemp.setEnvelope(115, 135, 35, 125, 145, 45);
        listRef.add(new GeneralEnvelope(geTemp));
        geTemp.setEnvelope(116, 136, 36, 124, 144, 44);
        int[] tabResult = TreeX.search(tree, geTemp, SpatialFilterType.CONTAINS);
        assertTrue(compareList(listRef, getresult(tabResult)));
        geTemp.setEnvelope(tree.getRoot().getBoundary());
        tabResult = TreeX.search(tree, geTemp, SpatialFilterType.CONTAINS);
        assertTrue(tabResult.length == 0);
    }

    /**
     * Test result {@link Envelope} not within specified search Envelope.
     *
     * @throws StoreIndexException
     */
    @Test
    public void testDisjoin() throws StoreIndexException {
        final List<Envelope> listRef = new ArrayList<Envelope>();
        final GeneralEnvelope geTemp = new GeneralEnvelope(CRS_TEST);
        for(int z = 0; z <= 100; z += 20) {
            for(int y = 0; y <= 200; y += 20) {
                for(int x = 0; x<=200; x += 20) {
                    geTemp.setEnvelope(x-5, y-5, z-5, x+5, y+5, z+5);
                    listRef.add(new GeneralEnvelope(geTemp));
                }
            }
        }
        geTemp.setEnvelope(-10, -10, 110, 210, 210, 210);
        int[] tabResult = TreeX.search(tree, geTemp, SpatialFilterType.DISJOINT);
        assertTrue(compareList(listRef, getresult(tabResult)));
        geTemp.setEnvelope(tree.getRoot().getBoundary());
        tabResult = TreeX.search(tree, geTemp, SpatialFilterType.DISJOINT);
        assertTrue(tabResult.length == 0);
    }

    /**
     * Test result {@link Envelope} within specified search Envelope.
     *
     * @throws StoreIndexException
     */
    @Test
    public void testWithin() throws StoreIndexException {
        final List<Envelope> listRef = new ArrayList<Envelope>();
        final GeneralEnvelope geTemp = new GeneralEnvelope(CRS_TEST);
        for(int z = 0; z <= 200; z += 20) {
            for(int y = 0; y <= 200; y += 20) {
                    geTemp.setEnvelope(195, y-5, z-5, 205, y+5, z+5);
                    listRef.add(new GeneralEnvelope(geTemp));
            }
        }
        geTemp.setEnvelope(180, -10, -10, 210, 210, 210);
        int[] tabresult = TreeX.search(tree, geTemp, SpatialFilterType.WITHIN);
        assertTrue(compareList(listRef, getresult(tabresult)));
        geTemp.setEnvelope(-10, 97, -10, 210, 104, 210);
        tabresult = TreeX.search(tree, geTemp, SpatialFilterType.WITHIN);
        assertTrue(tabresult.length == 0);
    }

    /**
     * Test result {@link Envelope} which touch specified search Envelope.
     *
     * @throws StoreIndexException
     */
    @Test
    public void testTouches() throws StoreIndexException {
        final List<Envelope> listRef = new ArrayList<Envelope>();
        final GeneralEnvelope geTemp = new GeneralEnvelope(CRS_TEST);
        for(int z = 0; z <= 200; z += 20) {
            for(int y = 0; y <= 200; y += 20) {
                for(int x = 140; x <= 160; x += 20) {
                    geTemp.setEnvelope(x-5, y-5, z-5, x+5, y+5, z+5);
                    listRef.add(new GeneralEnvelope(geTemp));
                }
            }
        }
        geTemp.setEnvelope(145, -10, -10, 155, 210, 210);
        int[] tabresult = TreeX.search(tree, geTemp, SpatialFilterType.TOUCHES);
        assertTrue(compareList(listRef, getresult(tabresult)));
        geTemp.setEnvelope(144, -10, -10, 156, 210, 210);
        tabresult = TreeX.search(tree, geTemp, SpatialFilterType.TOUCHES);
        assertTrue(tabresult.length == 0);
    }

    /**
     * Test result {@link Envelope} which are equals to search Envelope.
     *
     * @throws StoreIndexException
     */
    @Test
    public void testEquals() throws StoreIndexException {
        final List<Envelope> listRef = new ArrayList<Envelope>();
        final GeneralEnvelope geTemp = new GeneralEnvelope(CRS_TEST);
        geTemp.setEnvelope(115, 135, 35, 125, 145, 45);
        listRef.add(new GeneralEnvelope(geTemp));
        geTemp.setEnvelope(115, 135, 35, 125, 145, 45);
        int[] tabresult = TreeX.search(tree, geTemp, SpatialFilterType.EQUALS);
        assertTrue(compareList(listRef, getresult(tabresult)));
        geTemp.setEnvelope(tree.getRoot().getBoundary());
        tabresult = TreeX.search(tree, geTemp, SpatialFilterType.EQUALS);
        assertTrue(tabresult.length == 0);
    }

    /**
     * Test result {@link Envelope} which overlaps specified search Envelope.
     *
     * @throws StoreIndexException
     */
    @Test
    public void testOverlaps() throws StoreIndexException {
        final List<Envelope> listRef = new ArrayList<Envelope>();
        final GeneralEnvelope geTemp = new GeneralEnvelope(CRS_TEST);
        for(int z = 0; z <= 200; z += 20) {
            for(int y = 0; y <= 200; y += 20) {
                for(int x = 140; x <= 160; x += 20) {
                    geTemp.setEnvelope(x-5, y-5, z-5, x+5, y+5, z+5);
                    listRef.add(new GeneralEnvelope(geTemp));
                }
            }
        }
        geTemp.setEnvelope(144, -10, -10, 156, 210, 210);
        int[] tabresult = TreeX.search(tree, geTemp, SpatialFilterType.OVERLAPS);
        assertTrue(compareList(listRef, getresult(tabresult)));
        geTemp.setEnvelope(145, -10, -10, 155, 210, 210);
        tabresult = TreeX.search(tree, geTemp, SpatialFilterType.OVERLAPS);
        assertTrue(tabresult.length == 0);
        geTemp.setEnvelope(145, -10, -10, 165, 210, 210);
        tabresult = TreeX.search(tree, geTemp, SpatialFilterType.OVERLAPS);
        assertTrue(tabresult.length == 0);
    }

}

/**
 * Do link between datas inserted for this test and Tree identifier.
 *
 * @author Remi Marechal (Geomatys).
 * @see TreeElementMapper
 */
class TreeXElementMapperTest implements TreeElementMapper<Envelope> {

    private final List<Envelope> lData;
    private final List<Integer> lID;
    private boolean isClosed;

    public TreeXElementMapperTest() {
        this.lData    = new ArrayList<Envelope>();
        this.lID      = new ArrayList<Integer>();
        this.isClosed = false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getTreeIdentifier(Envelope object) {
        for (int i = 0, s = lData.size(); i < s; i++) {
            if (lData.get(i).equals(object)) {
                return lID.get(i);
            }
        }
        throw new IllegalStateException("impossible to found treeIdentifier.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope(Envelope object) {
        return object;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTreeIdentifier(Envelope object, int treeIdentifier) {
        lData.add(object);
        lID.add(treeIdentifier);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getObjectFromTreeIdentifier(int treeIdentifier) {
        for (int i = 0, l = lID.size(); i < l; i++) {
            if (lID.get(i) == treeIdentifier) {
                return lData.get(i);
            }
        }
        throw new IllegalStateException("impossible to found Data.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clear() {
        lData.clear();
        lID.clear();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws IOException {
        // do nothing
        isClosed = true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void flush() throws IOException {
        // do nothing
    }
}
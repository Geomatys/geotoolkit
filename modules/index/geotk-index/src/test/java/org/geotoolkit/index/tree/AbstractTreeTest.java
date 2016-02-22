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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.junit.Test;
import static org.geotoolkit.internal.tree.TreeUtilities.*;
import static org.geotoolkit.index.tree.TreeTest.createEntry;
import org.geotoolkit.internal.tree.TreeAccess;
import static org.junit.Assert.assertTrue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;


/**
 * Test suite adapted for all {@link Tree} implementation.
 *
 * @author Remi Marechal (Geomatys).
 */
public abstract class AbstractTreeTest extends TreeTest {

    /**
     * data number inserted in Tree.
     */
    private final int lSize = 300;

    /**
     * Data list which contain data use in this test series.
     */
    private final List<double[]> lData = new ArrayList<double[]>();

    /**
     * Tree CRS.
     */
    protected final CoordinateReferenceSystem crs;

    /**
     * Dimension of Tree CRS space.
     */
    private final int dimension;

    /**
     * double table which contain "extends" area of all data.
     */
    private final double[] minMax;

    /**
     * Contain Tree Node architecture.
     */
    protected TreeAccess tAF;

    /**
     * Tested Tree.
     */
    protected Tree<double[]> tree;

    /**
     * Do link between between TreeIdentifier and objects.
     */
    protected TreeElementMapper<double[]> tEM;

    /**
     * Create tests series from specified {@link CoordinateReferenceSystem}.
     *
     * @param crs
     */
    protected AbstractTreeTest(final CoordinateReferenceSystem crs) throws IOException {
        super();
        this.crs = crs;
        this.dimension = crs.getCoordinateSystem().getDimension();
        ArgumentChecks.ensurePositive("dimension", this.dimension);
        final CoordinateSystem cs = crs.getCoordinateSystem();
        minMax = new double[2 * dimension];
        final double cartesianValue = 1E6;
        for (int i = 0; i < dimension; i++) {
            final CoordinateSystemAxis csa = cs.getAxis(i);
            final double minV = csa.getMinimumValue();
            minMax[i] = ( ! Double.isInfinite(minV)) ? minV : (minV < 0) ? -cartesianValue : cartesianValue;
            final double maxV = csa.getMaximumValue();
            minMax[i + dimension] = ( ! Double.isInfinite(maxV)) ? maxV : (maxV < 0) ? -cartesianValue : cartesianValue;
        }
        final double[] centerEntry = new double[dimension];
        for (int i = 0; i < lSize; i++) {
            for (int d = 0; d < dimension; d++) {
                centerEntry[d] = (minMax[d+dimension]-minMax[d]) * Math.random() * Math.random() + minMax[d];
            }
            lData.add(createEntry(centerEntry));
        }
    }

    /**
     * Create test series from specified {@link Tree}.
     *
     * @param tree Tree which will be test.
     */
    protected AbstractTreeTest(final Tree tree) throws IOException {
        this(tree.getCrs());
        this.tree = tree;
        this.tEM  = tree.getTreeElementMapper();
    }

    /**
     * Insert appropriate elements in Tree.
     *
     * @throws StoreIndexException if problem during insertion.
     * @throws IOException if problem during {@link TreeElementMapper#clear() } method.
     */
    protected void insert() throws StoreIndexException, IOException {
        tEM.clear();
        for (int i = 0, s = lData.size(); i < s; i++) {
            final double[] envData = lData.get(i).clone();
            tree.insert(envData);
            tree.flush(); //-- add persistence comportement
        }
        assertTrue("after massive insertion root node should not be null", tree.getRoot() != null);
    }

    /**
     * Test if tree contain all elements inserted.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void insertTest() throws StoreIndexException, IOException {
        tree.setRoot(null);
        insert();
        final double[] gr = tree.getRoot().getBoundary();
        final double[] envSearch = gr.clone();

        final GeneralEnvelope rG = new GeneralEnvelope(crs);
        rG.setEnvelope(gr);

        int[] tabSearch = tree.searchID(rG);
        final TreeIdentifierIterator triter = tree.search(rG);
        final int[] tabIterSearch = new int[tabSearch.length];
        int tabID = 0;
        while (triter.hasNext()) {
            tabIterSearch[tabID++] = triter.nextInt();
        }
        assertTrue("comparison between tabSearch from iterator not equals with tabSearch", compareID(tabSearch, tabIterSearch));
        assertTrue(tabSearch.length == lData.size());
        assertTrue(tree.getElementsNumber() == lData.size());
        try {
            final double[] ge = new double[]{ Double.NaN, 10, 5, Double.NaN};
            tree.insert(ge);
            Assert.fail("test should have fail");
        } catch (Exception ex) {
            assertTrue(ex instanceof IllegalArgumentException);
            //ok
        }
    }

    /**
     * Compare node properties from its children.<br/>
     * Compare Node boundary from its sub-Nodes boundary sum.<br/>
     * Moreover verify conformity of stored datas.
     */
    protected void checkNode(final Node node, List<double[]> listRef) throws StoreIndexException, IOException {
        final double[] nodeBoundary = node.getBoundary();
        double[] subNodeBound = null;
        int sibl = node.getChildId();
        while (sibl != 0) {
            final Node currentChild = tAF.readNode(sibl);
            assertTrue("Node child should never be empty.", !currentChild.isEmpty());
            if (subNodeBound == null) {
                subNodeBound = currentChild.getBoundary().clone();
            } else {
                add(subNodeBound, currentChild.getBoundary());
            }
            if (node.isLeaf()) {
                assertTrue(currentChild.isData());
                final int currentValue = - currentChild.getChildId();
                final int listId = currentValue -1;
                assertTrue("bad ID = "+(currentValue)
                        +" expected : "+Arrays.toString(listRef.get(listId))
                        +" found : "+Arrays.toString(currentChild.getBoundary()), Arrays.equals(currentChild.getBoundary(), listRef.get(listId)));
            } else {
                checkNode(currentChild, listRef);
            }
            sibl = currentChild.getSiblingId();
        }
        assertTrue("Node should have a boundary equals from its sub-Nodes boundary sum : "
            +" \nNode boundary = "+Arrays.toString(nodeBoundary)
            +"\nsub-nodes sum = "+Arrays.toString(subNodeBound), Arrays.equals(nodeBoundary, subNodeBound));
    }

    /**
     * Compare all boundary node from their children boundary.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void checkNodeTest() throws StoreIndexException, IOException {
        tAF = ((AbstractTree)tree).getTreeAccess();
        if (tree.getRoot() == null) insert();
        checkNode(tree.getRoot(), lData);
    }

    /**
     * Test search query on tree border.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void queryOnBorderTest() throws StoreIndexException, IOException {
        tree.setRoot(null);
        tEM.clear();
        final List<double[]> lGE = new ArrayList<double[]>();

        final List<double[]> lGERef = new ArrayList<double[]>();
        final double[] gR ;

        assertTrue(tree.getElementsNumber() == 0);

        if (dimension == 2) {
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    final double[] gE = new double[]{5 * i, 5 * j, 5 * i, 5 * j};
                    lGE.add(gE);
                    if (i == 19 && j > 3 && j < 18) {
                        lGERef.add(gE);
                    }
                }
            }
            gR = new double[]{93, 18, 130, 87};
        } else {
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    final double[] gE = new double[]{5 * i, 5 * j, 20, 5 * i, 5 * j, 20};
                    lGE.add(gE);
                    if (i == 19 && j > 3 && j < 18) {
                        lGERef.add(gE);
                    }
                }
            }
            gR = new double[]{93, 18, 19, 130, 87, 21};
        }
        for (int i = 0,  s = lGE.size(); i < s; i++) {
            tree.insert(lGE.get(i));
            tree.flush();
            tEM.flush();
        }
        final GeneralEnvelope rG = new GeneralEnvelope(crs);
        rG.setEnvelope(gR);

        final int[] tabSearch = tree.searchID(rG);
        final TreeIdentifierIterator triter = tree.search(rG);
        final int[] tabIterSearch = new int[tabSearch.length];
        int tabID = 0;
        while (triter.hasNext()) {
            tabIterSearch[tabID++] = triter.nextInt();
        }
        assertTrue("comparison between tabSearch from iterator not equals with tabSearch", compareID(tabSearch, tabIterSearch));
        assertTrue(compareLists(lGERef, Arrays.asList(getResult(tabSearch))));
    }

    /**
     * Test search query inside tree.
     */
    @Test
    public void queryInsideTest() throws StoreIndexException, IOException {
        if (tree.getRoot() == null) insert();
        final List<double[]> lDataTemp = new ArrayList<double[]>();
        for (int i = 0; i < lSize; i++) {
            lDataTemp.add(lData.get(i));
        }

        final GeneralEnvelope rG = new GeneralEnvelope(crs);
        rG.setEnvelope(getExtent(lData));

        int[] tabSearch = tree.searchID(rG);
        final TreeIdentifierIterator triter = tree.search(rG);
        final int[] tabIterSearch = new int[tabSearch.length];
        int tabID = 0;
        while (triter.hasNext()) {
            tabIterSearch[tabID++] = triter.nextInt();
        }
        assertTrue("comparison between tabSearch from iterator not equals with tabSearch", compareID(tabSearch, tabIterSearch));
        assertTrue(compareLists(lDataTemp, Arrays.asList(getResult(tabSearch))));
    }

     /**
     * Test query outside of tree area.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void queryOutsideTest() throws StoreIndexException, IOException {
        if (tree.getRoot() == null) insert();
        final double[] areaSearch = new double[dimension<<1];
        for (int i = 0; i < dimension; i++) {
            areaSearch[i] = minMax[i+1]+100;
            areaSearch[dimension+i] = minMax[i+1]+2000;
        }

        final GeneralEnvelope rG = new GeneralEnvelope(crs);
        rG.setEnvelope(areaSearch);

        int[] tabResult = tree.searchID(rG);
        final TreeIdentifierIterator triter = tree.search(rG);
        final int[] tabIterSearch = new int[tabResult.length];
        int tabID = 0;
        while (triter.hasNext()) {
            tabIterSearch[tabID++] = triter.nextInt();
        }
        assertTrue("comparison between tabSearch from iterator not equals with tabSearch", compareID(tabResult, tabIterSearch));
        assertTrue(tabResult.length == 0);
    }

    /**
     * Test insertion and deletion in tree.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void insertDelete() throws StoreIndexException, IOException {
        if (tree.getRoot() == null) insert();
        Collections.shuffle(lData);
        for (int i = 0, s = lData.size(); i < s; i++) {
            assertTrue(tree.remove(lData.get(i)));
        }

        final GeneralEnvelope rG = new GeneralEnvelope(crs);
        rG.setEnvelope(minMax.clone());

        int[] tabSearch = tree.searchID(rG);
        TreeIdentifierIterator triter = tree.search(rG);
        int[] tabIterSearch = new int[tabSearch.length];
        int tabID = 0;
        while (triter.hasNext()) {
            Assert.fail("test should not be pass here.");
            tabIterSearch[tabID++] = triter.nextInt();
        }
        assertTrue(tabSearch.length == 0);
        assertTrue(tree.getElementsNumber() == 0);

        insert();
        tabSearch = tree.searchID(rG);
        triter    = tree.search(rG);
        tabIterSearch = new int[tabSearch.length];
        tabID = 0;
        while (triter.hasNext()) {
            tabIterSearch[tabID++] = triter.nextInt();
        }
        assertTrue("comparison between tabSearch from iterator not equals with tabSearch", compareID(tabSearch, tabIterSearch));
        assertTrue(compareLists(lData, Arrays.asList(getResult(tabSearch))));
    }

    /**
     * Return result given by {@link TreeElementMapper} from tree identifier table given in parameter.
     *
     * @param tabID tree identifier table results.
     * @return all object result (in our case Object = double[]).
     * @throws IOException if problem during tree identifier "translate".
     */
    protected double[][] getResult(int[] tabID) throws IOException {
        final int l = tabID.length;
        double[][] tabResult = new double[l][];
        for (int i = 0; i < l; i++) {
            tabResult[i] = tEM.getObjectFromTreeIdentifier(tabID[i]);
        }
        return tabResult;
    }
}
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
import static org.geotoolkit.index.tree.TreeUtilities.*;
import static org.geotoolkit.index.tree.TreeTest.createEntry;
import org.geotoolkit.internal.tree.TreeAccess;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;


/**
 *
 * @author rmarechal
 */
public abstract class AbstractTreeTest extends TreeTest {

    int nbrTemp = 0;
    
    //debug 
    int lSize = 300;
    
    protected TreeAccess tAF;
    protected Tree<double[]> tree;
    protected final List<double[]> lData = new ArrayList<double[]>();
    protected final CoordinateReferenceSystem crs;
    protected final int dimension;
    protected final double[] minMax;
    protected TreeElementMapper<double[]> tEM;
    
    protected AbstractTreeTest(final CoordinateReferenceSystem crs) {
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
    
    protected AbstractTreeTest(final Tree tree) {
        this(tree.getCrs());
        this.tree = tree;
        this.tEM  = tree.getTreeElementMapper();
    }

    protected void insert() throws StoreIndexException, IOException {
        tEM.clear();
        nbrTemp = 0;
        for (int i = 0, s = lSize/*lData.size()*/; i < s; i++) {
            final double[] envData = lData.get(i).clone();
            tree.insert(envData);
//            checkNodeBoundaryTest(tree.getRoot(), lData);
            nbrTemp++;
        }
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
        assertTrue(tabSearch.length == nbrTemp);
        assertTrue(tree.getElementsNumber() == nbrTemp);
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
            +" Node boundary = "+Arrays.toString(nodeBoundary)
            +"sub-nodes sum = "+Arrays.toString(subNodeBound), Arrays.equals(nodeBoundary, subNodeBound));
    }
    
    /**
     * Compare all boundary node from their children boundary.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void checkNodeTest() throws StoreIndexException, IOException {
        if (tree.getRoot() == null) insert();
        tAF = ((AbstractTree)tree).getTreeAccess();
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
        }
        final GeneralEnvelope rG = new GeneralEnvelope(crs);
        rG.setEnvelope(gR);
        
        int[] tabSearch = tree.searchID(rG);
        assertTrue(compareLists(lGERef, Arrays.asList(getResult(tabSearch))));
//        checkNodeBoundaryTest(tree.getRoot(), lGE);
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
        
        // visitor
        int[] tabSearch = tree.searchID(rG);
//        List<double[]> ld = Arrays.asList(getResult(tabSearch));
        assertTrue(compareLists(lDataTemp, Arrays.asList(getResult(tabSearch))));
//        checkNodeBoundaryTest(tree.getRoot(), lData);
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
        assertTrue(tabResult == null || tabResult.length == 0);
//        checkNodeBoundaryTest(tree.getRoot(), lData);
    }
    
    /**
     * Test insertion and deletion in tree.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void insertDelete() throws StoreIndexException, IOException {
        if (tree.getRoot() == null) insert();
//        checkNodeBoundaryTest(tree.getRoot(), lData);
        Collections.shuffle(lData);
        for (int i = 0, s = lSize; i < s; i++) {
            assertTrue(tree.remove(lData.get(i)));
        }
        
        final GeneralEnvelope rG = new GeneralEnvelope(crs);
        rG.setEnvelope(minMax.clone());
        
        int[] tabSearch = tree.searchID(rG);
        assertTrue(tabSearch == null);
        assertTrue(tree.getElementsNumber() == 0);
////        assertTrue(checkTreeElts(tree));
        insert();
        tabSearch = tree.searchID(rG);
        assertTrue(compareLists(lData, Arrays.asList(getResult(tabSearch))));
    }
    
    protected double[][] getResult(int[] tabID) throws IOException {
        final int l = tabID.length;
        double[][] tabResult = new double[l][];
        for (int i = 0; i < l; i++) {
            try {
                tabResult[i] = tEM.getObjectFromTreeIdentifier(tabID[i]);
            } catch (Exception ex) {
                System.out.println("");
            }
        }
        return tabResult;
    }
}
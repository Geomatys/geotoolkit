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
package org.geotoolkit.index.tree.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.SpatialTreeTest;
import org.junit.Test;
import org.opengis.referencing.operation.TransformException;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.geotoolkit.index.tree.FileNode;
import org.geotoolkit.index.tree.access.TreeAccess;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 *
 * @author rmarechal
 */
public abstract class AbstractTreeTest extends SpatialTreeTest {

    int nbrTemp = 0;
    
    //debug 
    int lSize = 3000;
    
    protected TreeAccess tAF;
    
    public AbstractTreeTest(CoordinateReferenceSystem crs) throws StoreIndexException, IOException {
        super(crs);
    }
    
//    @After
//    public void close() throws StoreIndexException, IOException {
//        tree.close();
//    }

    @Override
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
    @Override
//    @Ignore
    public void insertTest() throws StoreIndexException, IOException {
        tree.setRoot(null);
        insert();
        final double[] gr = ((Node) tree.getRoot()).getBoundary();
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
//        checkNodeBoundaryTest(tree.getRoot(), lData);
    }
    
    /**
     * Compare boundary node from its children boundary.
     */    
    protected void checkNodeBoundaryTest(final Node node, List<double[]> listRef) throws StoreIndexException, IOException {
        assertTrue(checkBoundaryNode(node));
        if (!node.isLeaf()) {
            int sibl = ((FileNode)node).getChildId();
            while (sibl != 0) {
                final FileNode currentChild = tAF.readNode(sibl);
                checkNodeBoundaryTest(currentChild, listRef);
                sibl = currentChild.getSiblingId();
            }
        } else {
            int sibl = ((FileNode)node).getChildId();
            while (sibl != 0) {
                final FileNode currentData = tAF.readNode(sibl);
                assertTrue(currentData.isData());
                final int currentValue = - currentData.getChildId();
                final int listId = currentValue -1;
                assertTrue("bad ID = "+(currentValue)
                        +" expected : "+Arrays.toString(listRef.get(listId))
                        +" found : "+Arrays.toString(currentData.getBoundary()), Arrays.equals(currentData.getBoundary(), listRef.get(listId)));
                sibl = currentData.getSiblingId();
            }
        }
        assertTrue(checkBoundaryNode(node));
    }
    
    /**
     * Compare boundary node from his children or elements boundary.
     */
    @Override
    public boolean checkBoundaryNode(final Node node) throws IOException {
        double[] subBound = null;
        final double[] currentBoundary = node.getBoundary().clone();
        int sibl = ((FileNode)node).getChildId();
        while (sibl != 0) {
            final FileNode currentChild = tAF.readNode(sibl);
            if (subBound == null) {
                subBound = currentChild.getBoundary().clone();
            } else {
                add(subBound, currentChild.getBoundary());
            }
            sibl = currentChild.getSiblingId();
        }
        return Arrays.equals(subBound, currentBoundary);
    }
    
    /**
     * Compare all boundary node from their children boundary.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    @Override
    @Ignore
    public void checkBoundaryTest() throws StoreIndexException, IOException {
        if (tree.getRoot() == null) insert();
        checkNodeBoundaryTest(tree.getRoot(), lData);
    }
    
    /**
     * Test search query on tree border.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    @Override
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
        int siz = lGE.size();
        for (int i = 0,  s = siz/*lGE.size()*/; i < s; i++) {
            System.out.println("insert i = "+i);
            tree.insert(lGE.get(i));
//            System.out.println(tree.toString());
        }
        
//        for (int i = 0,  s = siz/*lGE.size()*/; i < s; i++) {
//            System.out.println("remove i = "+i);
//            tree.remove(lGE.get(i));
////            System.out.println(tree.toString());
//        }
        final GeneralEnvelope rG = new GeneralEnvelope(crs);
        rG.setEnvelope(gR);
//        System.out.println(tree.toString());
//      // visitor
        int[] tabSearch = tree.searchID(rG);
        assertTrue(compareLists(lGERef, Arrays.asList(getResult(tabSearch))));
//        checkNodeBoundaryTest(tree.getRoot(), lGE);
    }
    
    /**
     * Test search query inside tree.
     */
    @Test
    @Override
//    @Ignore
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
        assertTrue(compareLists(lDataTemp, Arrays.asList(getResult(tabSearch))));
//        checkNodeBoundaryTest(tree.getRoot(), lData);
    }
    
     /**
     * Test query outside of tree area.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    @Override
//    @Ignore
    public void queryOutsideTest() throws StoreIndexException, IOException {
//        System.out.println("queryOutsideTest");
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
    @Override
//    @Ignore
    public void insertDelete() throws StoreIndexException, IOException {
//        System.out.println("insertDelete");
        if (tree.getRoot() == null) insert();
//        checkNodeBoundaryTest(tree.getRoot(), lData);
//        Collections.shuffle(lData);
        for (int i = 0, s = lSize; i < s; i++) {
            assertTrue(tree.remove(lData.get(i)));
        }
        final double[] areaSearch = new double[dimension << 1];
        for (int i = 0; i < dimension; i++) {
            areaSearch[i] = minMax[2 * i];
            areaSearch[i + dimension] = minMax[2 * i + 1];
        }
        
        final GeneralEnvelope rG = new GeneralEnvelope(crs);
        rG.setEnvelope(areaSearch);
        
        int[] tabSearch = tree.searchID(rG);
        assertTrue(tabSearch == null);
        assertTrue(tree.getElementsNumber() == 0);
////        assertTrue(checkTreeElts(tree));
        insert();
        tabSearch = tree.searchID(rG);
        assertTrue(compareLists(lData, Arrays.asList(getResult(tabSearch))));
    }
}
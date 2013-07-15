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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.geotoolkit.index.tree.FileBasicRTree;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.SpatialTreeTest;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.geotoolkit.index.tree.FileNode;
import org.geotoolkit.index.tree.TreeAccessFile;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;


/**
 *
 * @author rmarechal
 */
public class FileTreeTest extends SpatialTreeTest {

    int nbrTemp = 0;
    
    //debug 
    int lSize = 3000;
    
    TreeAccessFile tAF;
    
    public FileTreeTest() throws StoreIndexException, IOException {
        super(new FileBasicRTree(File.createTempFile("test", "tree"), 3, DefaultEngineeringCRS.CARTESIAN_2D, SplitCase.LINEAR));
        tAF = ((FileBasicRTree)tree).getTreeAccess();
    }
    
    @After
    public void close() throws StoreIndexException, IOException {
        tree.close();
    }

    @Override
    protected void insert() throws StoreIndexException, IOException {
        for (int i = 0, s = lSize/*lData.size()*/; i < s; i++) {
            final double[] envData = lData.get(i).clone();
            tree.insert(i+1, envData);
            checkNodeBoundaryTest(tree.getRoot(), lData);
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
    @Ignore
    public void insertTest() throws StoreIndexException, IOException {
        tree.setRoot(null);
        insert();
        final double[] gr = ((Node) tree.getRoot()).getBoundary();
        final double[] envSearch = gr.clone();
        final List listSearch = new ArrayList<Envelope>();
        tree.search(envSearch, new FileTreeVisitor(lData, listSearch));
        assertTrue(listSearch.size() == nbrTemp);
        assertTrue(tree.getElementsNumber() == nbrTemp);
        try {
            final double[] ge = new double[]{ Double.NaN, 10, 5, Double.NaN};
            tree.insert(ge, ge);
            Assert.fail("test should have fail");
        } catch (Exception ex) {
            assertTrue(ex instanceof IllegalArgumentException);
            //ok
        }
        checkNodeBoundaryTest(tree.getRoot(), lData);
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
    @Ignore
    public void queryOnBorderTest() throws StoreIndexException, IOException {
        tree.setRoot(null);
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
            tree.insert(i+1, lGE.get(i));
        }
        // visitor
        final List<double[]> result   = new ArrayList<double[]>();
        final TreeVisitor testVisitor = new FileTreeVisitor(lGE, result);
        tree.search(gR, testVisitor);
        assertTrue(compareLists(lGERef, result));
        checkNodeBoundaryTest(tree.getRoot(), lGE);
    }
    
    /**
     * Test search query inside tree.
     */
    @Test
    @Override
    @Ignore
    public void queryInsideTest() throws StoreIndexException, IOException {
        if (tree.getRoot() == null) insert();
        final List<double[]> lDataTemp = new ArrayList<double[]>();
        for (int i = 0; i < lSize; i++) {
            lDataTemp.add(lData.get(i));
        }
        // visitor
        final List<double[]> result   = new ArrayList<double[]>();
        final TreeVisitor testVisitor = new FileTreeVisitor(lData, result);
        tree.search(getExtent(lData), testVisitor);
        assertTrue(compareLists(lDataTemp, result));
        checkNodeBoundaryTest(tree.getRoot(), lData);
    }
    
     /**
     * Test query outside of tree area.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    @Override
    @Ignore
    public void queryOutsideTest() throws StoreIndexException, IOException {
        System.out.println("queryOutsideTest");
        if (tree.getRoot() == null) insert();
        final double[] areaSearch = new double[dimension<<1];
        for (int i = 0; i < dimension; i++) {
            areaSearch[i] = minMax[i+1]+100;
            areaSearch[dimension+i] = minMax[i+1]+2000;
        }
        // visitor
        final List<double[]> result   = new ArrayList<double[]>();
        final TreeVisitor testVisitor = new FileTreeVisitor(lData, result);
        tree.search(areaSearch, testVisitor);
        assertTrue(result.isEmpty());
        checkNodeBoundaryTest(tree.getRoot(), lData);
    }
    
    /**
     * Test insertion and deletion in tree.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    @Override
    public void insertDelete() throws StoreIndexException, IOException {
        System.out.println("insertDelete");
        // visitor
        final List<double[]> result   = new ArrayList<double[]>();
        final TreeVisitor testVisitor = new FileTreeVisitor(lData, result);
        
        if (tree.getRoot() == null) insert();
        checkNodeBoundaryTest(tree.getRoot(), lData);
        final List<Integer> lId = new ArrayList<Integer>(100);
        for (int i = 0; i < lSize; i++) {
            lId.add(i+1);
        }
        Collections.shuffle(lId);
        for (int i = 0, s = lSize; i < s; i++) {
            final int id = lId.get(i);
            double[] env = lData.get(id-1);
            assertTrue(tree.remove(id, env));
        }
        final double[] areaSearch = new double[dimension << 1];
        for (int i = 0; i < dimension; i++) {
            areaSearch[i] = minMax[2 * i];
            areaSearch[i + dimension] = minMax[2 * i + 1];
        }
        result.clear();
        tree.search(areaSearch, testVisitor);
        assertTrue(result.isEmpty());
        assertTrue(tree.getElementsNumber() == 0);
        assertTrue(checkTreeElts(tree));
        insert();
        result.clear();
        tree.search(areaSearch, testVisitor);
        final List<double[]> tempList = new ArrayList<double[]>(lSize);
        for (int i = 0; i < lSize; i++) {
            tempList.add(lData.get(i));
        }
        assertTrue(compareLists(result, tempList));
    }
}
class FileTreeVisitor implements TreeVisitor{

    final Collection result;
    final List dataRef;

    public FileTreeVisitor(List dataRef, Collection result) {
        this.dataRef = dataRef;
        this.result  = result;
    }

    @Override
    public TreeVisitorResult filter(Node node) {
        return TreeVisitorResult.CONTINUE;
    }

    @Override
    public TreeVisitorResult visit(Object element) {
        result.add(dataRef.get(((Integer)element)-1));
        return TreeVisitorResult.CONTINUE;
    }
};





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
import java.util.Arrays;
import org.geotoolkit.index.tree.basic.BasicRTree;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.io.TreeElementMapperTest;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Test {@link DefaultNode} class.
 *
 * @author Remi Marechal (Geomatys).
 */
public class DefaultNodeTest {
    
    private final static Tree TREE = new BasicRTree(5, DefaultEngineeringCRS.CARTESIAN_2D, SplitCase.LINEAR, new TreeElementMapperTest(null));
    private DefaultNode dn;
    private String[] objects = new String[]{"(0,0)","(-3,-3)","(3,-3)","(3,3)","(-3,3)"};
    private double[][] coordinates = new double[][]{new double[]{-0.5, -0.5, 0.5, 0.5},
                                                    new double[]{-3.5, -3.5, -2.5, -2.5},
                                                    new double[]{2.5, -3.5, 3.5, -2.5},
                                                    new double[]{2.5, 2.5, 3.5, 3.5},
                                                    new double[]{-3.5, 2.5, -2.5, 3.5}};
    private final Node[] children;
    public DefaultNodeTest() throws IOException {
        children = new Node[objects.length];
        for (int i = 0, s = objects.length; i < s; i++) {
            children[i] = new DefaultNode(TREE, null, null, null, null, new Object[]{objects[i]}, new double[][]{coordinates[i]});
        }
    }
    
    /**
     * Test {@link DefaultNode} creation.
     */
    @Test
    public void initTest() throws IOException {
        dn = new DefaultNode(TREE);
        assertTrue(dn.getParent() == null);
        assertTrue(dn.getBound() == null);
        assertTrue(dn.getCoordinates() == null);
        assertTrue(dn.getChildren() == null);
        assertTrue(dn.isEmpty());
        assertFalse(dn.isFull());
        assertTrue(dn.isLeaf());
        assertTrue(dn.getChildCount()  == 0);
        assertTrue(dn.getCoordsCount() == 0);
        assertTrue(dn.getObjectCount() == 0);
        
        /// with elements
        dn = new DefaultNode(TREE, null, null, null, null, objects, coordinates);
        assertTrue(dn.getParent() == null);
        assertTrue(dn.getBound() == null);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBoundary(), 1E-12);
        assertTrue(dn.getChildren() == null);
        assertFalse(dn.isEmpty());
        assertTrue(dn.isFull());
        assertTrue(dn.isLeaf());
        assertTrue(dn.getChildCount()  == 0);
        assertTrue(dn.getCoordsCount() == 5);
        assertTrue(dn.getObjectCount() == 5);
        try {
            dn.addChild(children[0]);
            Assert.fail("test should had fail");
        } catch (Exception Ex) {
            // ok
        }
        
        // with children
        dn = new DefaultNode(TREE, null, null, null, children, null, null);
        assertTrue(dn.getParent() == null);
        assertTrue(dn.getBound() == null);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBoundary(), 1E-12);
        assertTrue(dn.getCoordinates() == null);
        assertTrue(dn.getObjects() == null);
        assertFalse(dn.isEmpty());
        assertTrue(dn.isFull());
        assertFalse(dn.isLeaf());
        assertTrue(dn.getChildCount()  == 5);
        assertTrue(dn.getCoordsCount() == 0);
        assertTrue(dn.getObjectCount() == 0);
        try {
            dn.addElement(objects[0], coordinates[0]);
            Assert.fail("test should had fail");
        } catch (Exception Ex) {
            // ok
        }
        for (int i = 0, s = dn.getChildCount(); i < s; i++) {
            assertTrue(dn.getChild(i).getParent() == dn);
        }
    }
    
    /**
     * Test adding and deleting element in a {@link DefaultNode} which is leaf.
     */
    @Test
    public void insertRemoveElements() throws IOException {
        dn = new DefaultNode(TREE);
        
        // insert
        assertTrue(dn.getObjects() == null);
        assertTrue(dn.getCoordinates() == null);
        assertTrue(dn.getBound() == null);
        dn.addElement(objects[0], coordinates[0]);
        assertTrue(dn.getBound() == null);
        assertArrayEquals(new double[]{-0.5, -0.5, 0.5, 0.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-0.5, -0.5, 0.5, 0.5}, dn.getBound(), 1E-12);
        assertTrue(dn.getChildCount() == 0);
        assertTrue(dn.getObjectCount() == 1);
        assertTrue(dn.getCoordsCount() == 1);
        assertTrue(dn.getChildren() == null);
        
        dn.addElement(objects[1], coordinates[1]);
        assertTrue(dn.getBound() == null);
        assertArrayEquals(new double[]{-3.5, -3.5, 0.5, 0.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-3.5, -3.5, 0.5, 0.5}, dn.getBound(), 1E-12);
        assertTrue(dn.getChildCount() == 0);
        assertTrue(dn.getObjectCount() == 2);
        assertTrue(dn.getCoordsCount() == 2);
        assertTrue(dn.getChildren() == null);
        
        dn.addElement(objects[2], coordinates[2]);
        assertTrue(dn.getBound() == null);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 0.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 0.5}, dn.getBound(), 1E-12);
        assertTrue(dn.getChildCount() == 0);
        assertTrue(dn.getObjectCount() == 3);
        assertTrue(dn.getCoordsCount() == 3);
        assertTrue(dn.getChildren() == null);
        
        dn.addElement(objects[3], coordinates[3]);
        assertTrue(dn.getBound() == null);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBound(), 1E-12);
        assertTrue(dn.getChildCount() == 0);
        assertTrue(dn.getObjectCount() == 4);
        assertTrue(dn.getCoordsCount() == 4);
        assertTrue(dn.getChildren() == null);
        
        dn.addElement(objects[4], coordinates[4]);
        assertTrue(dn.getBound() == null);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBound(), 1E-12);
        assertTrue(dn.getChildCount() == 0);
        assertTrue(dn.getObjectCount() == 5);
        assertTrue(dn.getCoordsCount() == 5);
        assertTrue(dn.getChildren() == null);
        
        
        //// remove
        Object remO = dn.removeObject(0);
        assertTrue(dn.getBound() == null);
        assertTrue(dn.getChildCount() == 0);
        assertTrue(dn.getObjectCount() == 4);
        assertTrue(dn.getCoordsCount() == 5);
        assertTrue(dn.getChildren() == null);
        assertTrue(objects[0] == remO);
        assertTrue(((String)objects[0]).equals(remO));
        Object[] resultObject = new Object[4];
        System.arraycopy(dn.getObjects(), 0, resultObject, 0, 4);
        for (int i = 4; i < dn.getObjects().length; i++) {
            assertTrue(dn.getObjects()[i] == null);
        }
        assertArrayEquals(resultObject, new Object[]{objects[1], objects[2], objects[3], objects[4]});
        
        double[] remC = dn.removeCoordinate(0);
        assertTrue(dn.getBound() == null);
        assertTrue(dn.getChildCount() == 0);
        assertTrue(dn.getObjectCount() == 4);
        assertTrue(dn.getCoordsCount() == 4);
        assertTrue(dn.getChildren() == null);
        assertArrayEquals(coordinates[0], remC, 1E-12);
        double[][] resultCoords = new double[4][];
        System.arraycopy(dn.getCoordinates(), 0, resultCoords, 0, 4);
        for (int i = 4; i < dn.getCoordinates().length; i++) {
            assertTrue(dn.getCoordinates()[i] == null);
        }
        assertArrayEquals(resultCoords, new double[][]{coordinates[1], coordinates[2], coordinates[3], coordinates[4]});
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBound(), 1E-12);
        
        remO = dn.removeObject(1);
        assertTrue(dn.getBound() == null);
        assertTrue(dn.getChildCount() == 0);
        assertTrue(dn.getObjectCount() == 3);
        assertTrue(dn.getCoordsCount() == 4);
        assertTrue(dn.getChildren() == null);
        assertTrue(objects[2] == remO);
        assertTrue(((String)objects[2]).equals(remO));
        resultObject = new Object[3];
        System.arraycopy(dn.getObjects(), 0, resultObject, 0, 3);
        for (int i = 3; i < dn.getObjects().length; i++) {
            assertTrue(dn.getObjects()[i] == null);
        }
        assertArrayEquals(resultObject, new Object[]{objects[1], objects[3], objects[4]});
        
        remC = dn.removeCoordinate(1);
        assertTrue(dn.getBound() == null);
        assertTrue(dn.getChildCount() == 0);
        assertTrue(dn.getObjectCount() == 3);
        assertTrue(dn.getCoordsCount() == 3);
        assertTrue(dn.getChildren() == null);
        assertArrayEquals(coordinates[2], remC, 1E-12);
        resultCoords = new double[3][];
        System.arraycopy(dn.getCoordinates(), 0, resultCoords, 0, 3);
        for (int i = 3; i < dn.getCoordinates().length; i++) {
            assertTrue(dn.getCoordinates()[i] == null);
        }
        assertArrayEquals(resultCoords, new double[][]{coordinates[1], coordinates[3], coordinates[4]});
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBound(), 1E-12);
        
        //try catch 
        try {
            dn.removeObject(10);
            Assert.fail("test should had fail");
        } catch (Exception ex) {
            // ok
        }
        
        try {
            dn.removeCoordinate(10);
            Assert.fail("test should had fail");
        } catch (Exception ex) {
            // ok
        }
        
        remO = dn.removeObject(1);
        assertTrue(dn.getBound() == null);
        assertTrue(dn.getChildCount() == 0);
        assertTrue(dn.getObjectCount() == 2);
        assertTrue(dn.getCoordsCount() == 3);
        assertTrue(dn.getChildren() == null);
        assertTrue(objects[3] == remO);
        assertTrue(((String)objects[3]).equals(remO));
        resultObject = new Object[2];
        System.arraycopy(dn.getObjects(), 0, resultObject, 0, 2);
        for (int i = 2; i < dn.getObjects().length; i++) {
            assertTrue(dn.getObjects()[i] == null);
        }
        assertArrayEquals(resultObject, new Object[]{objects[1], objects[4]});
        
        remC = dn.removeCoordinate(1);
        assertTrue(dn.getBound() == null);
        assertTrue(dn.getChildCount() == 0);
        assertTrue(dn.getObjectCount() == 2);
        assertTrue(dn.getCoordsCount() == 2);
        assertTrue(dn.getChildren() == null);
        assertArrayEquals(coordinates[3], remC, 1E-12);
        resultCoords = new double[2][];
        System.arraycopy(dn.getCoordinates(), 0, resultCoords, 0, 2);
        for (int i = 2; i < dn.getCoordinates().length; i++) {
            assertTrue(dn.getCoordinates()[i] == null);
        }
        assertArrayEquals(resultCoords, new double[][]{coordinates[1], coordinates[4]});
        assertArrayEquals(new double[]{-3.5, -3.5, -2.5, 3.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-3.5, -3.5, -2.5, 3.5}, dn.getBound(), 1E-12);
        
        final Object[] remOs = dn.removeObjects();
        assertTrue(dn.getBound() == null);
        assertTrue(dn.getObjects() == null);
        assertTrue(dn.getObjectCount() == 0);
        resultObject = new Object[2];
        System.arraycopy(remOs, 0, resultObject, 0, 2);
        assertArrayEquals(resultObject, new Object[]{objects[1], objects[4]});
        
        final double[][] remCs = dn.removeCoordinates();
        assertTrue(dn.getBound() == null);
        assertTrue(dn.getCoordinates() == null);
        assertTrue(dn.getCoordsCount() == 0);
        resultCoords = new double[2][];
        System.arraycopy(remCs, 0, resultCoords, 0, 2);
        assertArrayEquals(resultCoords, new double[][]{coordinates[1], coordinates[4]});
        
    }
    
    /**
     * Test adding and deleting element in a {@link DefaultNode} which is not leaf.
     */
    @Test
    public void insertRemoveNode() throws IOException {
        dn = new DefaultNode(TREE);
        
        // insert
        assertTrue(dn.getObjects() == null);
        assertTrue(dn.getCoordinates() == null);
        assertTrue(dn.getChildren() == null);
        assertTrue(dn.getBound() == null);
        dn.addChild(children[0]);
        assertTrue(dn.getBound() == null);
        assertArrayEquals(new double[]{-0.5, -0.5, 0.5, 0.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-0.5, -0.5, 0.5, 0.5}, dn.getBound(), 1E-12);
        assertTrue(dn.getChildCount() == 1);
        assertTrue(dn.getObjectCount() == 0);
        assertTrue(dn.getCoordsCount() == 0);
        assertTrue(dn.getCoordinates() == null);
        assertTrue(dn.getObjects() == null);
        
        dn.addChild(children[1]);
        assertTrue(dn.getBound() == null);
        assertArrayEquals(new double[]{-3.5, -3.5, 0.5, 0.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-3.5, -3.5, 0.5, 0.5}, dn.getBound(), 1E-12);
        assertTrue(dn.getChildCount() == 2);
        assertTrue(dn.getObjectCount() == 0);
        assertTrue(dn.getCoordsCount() == 0);
        assertTrue(dn.getCoordinates() == null);
        assertTrue(dn.getObjects() == null);
        
        dn.addChild(children[2]);
        assertTrue(dn.getBound() == null);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 0.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 0.5}, dn.getBound(), 1E-12);
        assertTrue(dn.getChildCount() == 3);
        assertTrue(dn.getObjectCount() == 0);
        assertTrue(dn.getCoordsCount() == 0);
        assertTrue(dn.getCoordinates() == null);
        assertTrue(dn.getObjects() == null);
        
        dn.addChild(children[3]);
        assertTrue(dn.getBound() == null);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBound(), 1E-12);
        assertTrue(dn.getChildCount() == 4);
        assertTrue(dn.getObjectCount() == 0);
        assertTrue(dn.getCoordsCount() == 0);
        assertTrue(dn.getCoordinates() == null);
        assertTrue(dn.getObjects() == null);
        
        dn.addChild(children[4]);
        assertTrue(dn.getBound() == null);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBound(), 1E-12);
        assertTrue(dn.getChildCount() == 5);
        assertTrue(dn.getObjectCount() == 0);
        assertTrue(dn.getCoordsCount() == 0);
        assertTrue(dn.getCoordinates() == null);
        assertTrue(dn.getObjects() == null);
        
        
        //// remove
        Node remN = dn.removeChild(0);
        assertTrue(dn.getBound() == null);
        assertTrue(dn.getChildCount() == 4);
        assertTrue(dn.getObjectCount() == 0);
        assertTrue(dn.getCoordsCount() == 0);
        assertTrue(dn.getCoordinates() == null);
        assertTrue(dn.getObjects() == null);
        assertTrue(children[0] == remN);
        assertTrue(children[0].equals(remN));
        Node[] resultNode = new Node[4];
        System.arraycopy(dn.getChildren(), 0, resultNode, 0, 4);
        assertArrayEquals(resultNode, new Node[]{children[1], children[2], children[3], children[4]});
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBound(), 1E-12);
        
        remN = dn.removeChild(1);
        assertTrue(dn.getBound() == null);
        assertTrue(dn.getChildCount() == 3);
        assertTrue(dn.getObjectCount() == 0);
        assertTrue(dn.getCoordsCount() == 0);
        assertTrue(dn.getCoordinates() == null);
        assertTrue(dn.getObjects() == null);
        assertTrue(children[2] == remN);
        assertTrue(children[2].equals(remN));
        resultNode = new Node[3];
        System.arraycopy(dn.getChildren(), 0, resultNode, 0, 3);
        assertArrayEquals(resultNode, new Node[]{children[1], children[3], children[4]});
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-3.5, -3.5, 3.5, 3.5}, dn.getBound(), 1E-12);
        
        //try catch 
        try {
            dn.removeChild(10);
            Assert.fail("test should had fail");
        } catch (Exception ex) {
            // ok
        }
        
        remN = dn.removeChild(1);
        assertTrue(dn.getBound() == null);
        assertTrue(dn.getChildCount() == 2);
        assertTrue(dn.getObjectCount() == 0);
        assertTrue(dn.getCoordsCount() == 0);
        assertTrue(dn.getCoordinates() == null);
        assertTrue(dn.getObjects() == null);
        assertTrue(children[3] == remN);
        assertTrue(children[3].equals(remN));
        resultNode = new Node[2];
        System.arraycopy(dn.getChildren(), 0, resultNode, 0, 2);
        assertArrayEquals(resultNode, new Node[]{children[1], children[4]});
        assertArrayEquals(new double[]{-3.5, -3.5, -2.5, 3.5}, dn.getBoundary(), 1E-12);
        assertArrayEquals(new double[]{-3.5, -3.5, -2.5, 3.5}, dn.getBound(), 1E-12);
        
        final Node[] remNT = dn.removeChildren();
        assertTrue(dn.getBound() == null);
        assertTrue(dn.getCoordsCount() == 0);
        assertTrue(dn.getCoordinates() == null);
        assertTrue(dn.getChildren() == null);
        assertTrue(dn.getChildCount()== 0);
        assertTrue(dn.getObjects() == null);
        assertTrue(dn.getObjectCount() == 0);
        assertArrayEquals(Arrays.copyOf(remNT, 2), new Node[]{children[1], children[4]});
    }
}

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.io.DefaultTreeVisitor;
import org.apache.sis.util.ArgumentChecks;
import static org.junit.Assert.*;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.junit.Test;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.operation.TransformException;

/**
 * Test some R-Tree queries.
 *
 * @author Rémi Marechal (Geomatys).
 */
public abstract class SpatialTreeTest extends TreeTest{

    protected final Tree tree;
    protected final List<Envelope> lData = new ArrayList<Envelope>();
    protected final CoordinateReferenceSystem crs;
    protected final int dimension;
    protected final double[] minMax;

    public SpatialTreeTest(Tree tree) throws TransformException {
        ArgumentChecks.ensureNonNull("tree", tree);
        crs = tree.getCrs();
        ArgumentChecks.ensureNonNull("crs", crs);
        this.dimension = crs.getCoordinateSystem().getDimension();
        ArgumentChecks.ensurePositive("dimension", this.dimension);
        this.tree = tree;
        final CoordinateSystem cs = crs.getCoordinateSystem();
        minMax = new double[2*dimension];
        final DirectPosition centerEntry = new GeneralDirectPosition(crs);
        if (cs instanceof CartesianCS) {
            for(int l = 0; l<2*dimension; l+=2) {
                minMax[l] = -1500;
                minMax[l+1] = 1500;
            }
            for (int i = 0; i < 3000; i++) {
                for (int nbCoords = 0; nbCoords < this.dimension; nbCoords++) {
                    double value = (Math.random() < 0.5) ? -1 : 1;
                    value *= 1500 * Math.random();
                    centerEntry.setOrdinate(nbCoords, value);
                }
                lData.add(createEntry(centerEntry));
            }
        } else if (cs instanceof EllipsoidalCS) {
            minMax[0] = -180;
            minMax[1] = 180;
            minMax[2] = -90;
            minMax[3] = 90;
            for (int i = 0; i<3000; i++) {
                centerEntry.setOrdinate(0, (Math.random()-0.5) * 2*170);
                centerEntry.setOrdinate(1, (Math.random()-0.5) * 2*80);
                if(cs.getDimension()>2) {
                    centerEntry.setOrdinate(2, Math.random()*8800);
                    minMax[4] = -8800;
                    minMax[5] = 8800;
                }
                lData.add(createEntry(centerEntry));
            }
        } else {
            throw new IllegalArgumentException("invalid crs, Coordinate system type :"+ cs.getClass() +" is not supported");
        }
        insert();
    }

    /**
     * Insert all entries within ldata in tree.
     *
     * @throws TransformException if lData entries can't be transform into tree
     * crs.
     */
    protected void insert() throws IllegalArgumentException, TransformException {
        double[] add = null;
        for (int i = 0, s = lData.size(); i < s; i++) {
            final double[] env = DefaultTreeUtils.getCoords(lData.get(i));
            if (add == null) add = env.clone();
            add = DefaultTreeUtils.add(add, env);
            tree.insert(lData.get(i), env);
//            assertTrue(checkElementInsertion(tree.getRoot(), lData));
        }
    }

    /**
     * Test if tree contain all elements inserted.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void insertTest() throws MismatchedReferenceSystemException {
        final double[] gr = ((Node) tree.getRoot()).getBoundary();
        final double[] gem = super.getEnvelopeMin(lData);
        assertTrue(Arrays.equals(gr, gem));
        final double[] envSearch = gr.clone();
        final List listSearch = new ArrayList<Envelope>();
        tree.search(envSearch, new DefaultTreeVisitor(listSearch));
        assertTrue(listSearch.size() == lData.size());
        assertTrue(tree.getElementsNumber() == lData.size());
        try {
            final double[] ge = new double[]{ Double.NaN, 10, 5, Double.NaN};
            tree.insert(ge, ge);
            Assert.fail("test should have fail");
        } catch (Exception ex) {
            assertTrue(ex instanceof IllegalArgumentException);
            //ok
        }
        assertTrue(checkTreeElts(tree));
    }

    /**
     * Compare all boundary node from their children boundary.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void checkBoundaryTest() throws MismatchedReferenceSystemException {
        checkNodeBoundaryTest(tree.getRoot());
    }

    /**
     * Compare boundary node from its children boundary.
     */
    protected void checkNodeBoundaryTest(final Node node) {
        if(!node.isLeaf()){
            for (int i = 0, s = node.getChildCount(); i < s; i++) {
                checkNodeBoundaryTest(node.getChild(i));
            }
        }
        assertTrue(checkBoundaryNode(node));
    }

    /**
     * Test search query on tree border.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void queryOnBorderTest() throws IllegalArgumentException, TransformException {
        final List<GeneralEnvelope> lGE = new ArrayList<GeneralEnvelope>();
        for (Envelope env : lData) {
            assertTrue(tree.delete(env, getCoords(env)));
        }
        
        final List<Envelope> lGERef = new ArrayList<Envelope>();
        final GeneralEnvelope gR = new GeneralEnvelope(crs);
        
        assertTrue(tree.getElementsNumber() == 0);
        assertTrue(tree.getRoot().isEmpty());
        assertTrue(tree.getRoot().getCoordsCount() == 0);
        assertTrue(tree.getRoot().getObjectCount() == 0);
        
        if (dimension == 2) {
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    final GeneralEnvelope gE = new GeneralEnvelope(crs);
                    gE.setEnvelope(5 * i, 5 * j, 5 * i, 5 * j);
                    lGE.add(gE);
                    if (i == 19 && j > 3 && j < 18) {
                        lGERef.add(gE);
                    }
                }
            }
            gR.setEnvelope(93, 18, 130, 87);
        } else {
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    final GeneralEnvelope gE = new GeneralEnvelope(crs);
                    gE.setEnvelope(5 * i, 5 * j, 20, 5 * i, 5 * j, 20);
                    lGE.add(gE);
                    if (i == 19 && j > 3 && j < 18) {
                        lGERef.add(gE);
                    }
                }
            }
            gR.setEnvelope(93, 18, 19, 130, 87, 21);
        }
        for (Envelope env : lGE) {
            tree.insert(env, getCoords(env));
        }
        final List lGES = new ArrayList<Envelope>();
        tree.search(getCoords(gR), new DefaultTreeVisitor(lGES));
        assertTrue(compareList(lGERef, lGES));
        assertTrue(checkTreeElts(tree));
    }

    /**
     * Assert avoid null pointer exception.
     */
    @Test
    public void checkExtends(){
        tree.setRoot(null);
        assertTrue(tree.getExtent() == null);
    }


    /**
     * Compare boundary node from his children or elements boundary.
     */
    public boolean checkBoundaryNode(final Node node) {
        final double[] subBound;
        if (node.isLeaf()) {
            final int s = node.getCoordsCount();
            subBound = node.getCoordinate(0).clone();
            for (int i = 1; i < s; i++) {
                final double[] cuCoords = node.getCoordinate(i);
                DefaultTreeUtils.add(subBound, cuCoords);
            }
        } else {
            final int s = node.getChildCount();
            subBound = node.getChild(0).getBoundary().clone();
            for (int i = 1; i < s; i++) {
                DefaultTreeUtils.add(subBound, node.getChild(i).getBoundary());
            }
        }
        return Arrays.equals(node.getBoundary(), subBound);
    }

    /**
     * Test search query inside tree.
     */
    @Test
    public void queryInsideTest() throws MismatchedReferenceSystemException {
        final List listSearch = new ArrayList<Envelope>();
        tree.search(getEnvelopeMin(lData), new DefaultTreeVisitor(listSearch));
        assertTrue(compareList(lData, listSearch));
        assertTrue(checkTreeElts(tree));
    }

    /**
     * Test query outside of tree area.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void queryOutsideTest() throws MismatchedReferenceSystemException {
        final double[] areaSearch = new double[dimension<<1];
        for (int i = 0; i < dimension; i++) {
            areaSearch[i] = minMax[i+1]+100;
            areaSearch[dimension+i] = minMax[i+1]+2000;
        }
        final List listSearch = new ArrayList<Envelope>();
        tree.search(areaSearch, new DefaultTreeVisitor(listSearch));
        assertTrue(listSearch.isEmpty());
        assertTrue(checkTreeElts(tree));
    }

    /**
     * Test insertion and deletion in tree.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void insertDelete() throws IllegalArgumentException, TransformException {
        Collections.shuffle(lData);
        for (int i = 0, s = lData.size(); i < s; i++) {
            Envelope env = lData.get(i);
            assertTrue(tree.delete(env, getCoords(env)));
        }
        final double[] areaSearch = new double[dimension << 1];
        for (int i = 0; i < dimension; i++) {
            areaSearch[i] = minMax[2 * i];
            areaSearch[i + dimension] = minMax[2 * i + 1];
        }
        final List listSearch = new ArrayList<Envelope>();
        tree.search(areaSearch, new DefaultTreeVisitor(listSearch));
        assertTrue(listSearch.isEmpty());
        assertTrue(tree.getElementsNumber() == 0);
        assertTrue(checkTreeElts(tree));
        insert();
        tree.search(areaSearch, new DefaultTreeVisitor(listSearch));
        assertTrue(compareList(listSearch, lData));
        assertTrue(checkTreeElts(tree));
    }
    
    protected boolean checkElementInsertion(final Node candidate, List<Envelope> listRef) {
        if (candidate.isLeaf()) {
            final int siz = candidate.getCoordsCount();
            assert (siz == candidate.getObjectCount()) : "coord and object should be same length.";
            for (int i = 0; i < siz; i++) {
                Object cuObj = candidate.getObject(i);
                double[] coords = candidate.getCoordinate(i);
                assertTrue(Arrays.equals(coords, getCoords((Envelope)cuObj)));
                boolean found = false;
                for (int il = 0, s = listRef.size(); il < s; il++) {
                    if (cuObj == listRef.get(il)) {
                        if (Arrays.equals(coords, getCoords(listRef.get(il)))) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) return false; 
            }
            return true;// we found all.
        } else {
            for (int i = 0; i < candidate.getChildCount(); i++) {
                boolean check = checkElementInsertion(candidate.getChild(i), listRef);
                if (!check) return false;
            }
            return true;
        }
    }
    
    private boolean checkTree(Node candidate) {
        assert !candidate.isEmpty();
        if (!candidate.isLeaf()) {
            for (int i = 0; i < candidate.getChildCount(); i++) {
                assert candidate.getChild(i).getParent() == candidate:"parent should corespond";
                return checkTree(candidate.getChild(i));
            }
        }
        return checkBoundaryNode(candidate);
    }
    
    /*
    @Test
    @Ignore
    public void insertDelete2() {
        Tree tree2 = new HilbertRTree(3, 1, crs);
        final int sizz = 50;//lData.size();
        for (int i = 0; i < sizz; i++) {
            System.out.println("insert elt n° "+i);
            tree2.insert(lData.get(i), getCoords(lData.get(i)));
            assertTrue(checkElementInsertion(tree2.getRoot(), lData));
            checkNodeBoundaryTest(tree2.getRoot());
            assertTrue(checkTree(tree2.getRoot()));
        }
        assertTrue(checkTree(tree2.getRoot()));
        System.out.println("Tree : "+tree2.toString());
        final List listSearch = new ArrayList<Envelope>();
        for (int i = 0; i < sizz; i++) {
            System.out.println("delete elt n° "+i);
            assertTrue(tree2.delete(lData.get(i), getCoords(lData.get(i))));
            if (i != sizz-1) {
                assertTrue(checkTree(tree2.getRoot()));
            } else {
                assertTrue(tree2.getRoot().isEmpty());
            }
        }
        
        final double[] areaSearch = new double[dimension << 1];
        for (int i = 0; i < dimension; i++) {
            areaSearch[i] = minMax[2 * i];
            areaSearch[i + dimension] = minMax[2 * i + 1];
        }
        tree2.search(areaSearch, new DefaultTreeVisitor(listSearch));
        assertTrue(listSearch.isEmpty());
        assertTrue(tree2.getElementsNumber() == 0);
        assertTrue(checkTreeElts(tree2));
    }*/
}

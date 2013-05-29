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
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.io.DefaultTreeVisitor;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.apache.sis.util.ArgumentChecks;
import static org.junit.Assert.assertTrue;
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
        tree.insertAll(lData.iterator());
    }

    /**
     * Test if tree contain all elements inserted.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void insertTest() throws MismatchedReferenceSystemException {
        final Envelope gr = ((Node) tree.getRoot()).getBoundary();
        final GeneralEnvelope gem = DefaultTreeUtils.getEnveloppeMin(lData);
        assertTrue(gem.equals(gr, 1E-9, false));
        final List<Envelope> listSearch = new ArrayList<Envelope>();
        tree.search(gr, new DefaultTreeVisitor(listSearch));
        assertTrue(listSearch.size() == lData.size());
        try {
            GeneralEnvelope ge = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_2D);
            ge.setEnvelope(Double.NaN, 10, 5, Double.NaN);
            tree.insert(ge);
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
        assertTrue(checkBoundaryNode(node));
        if(!node.isLeaf()){
            for (Node no : node.getChildren()) {
                checkNodeBoundaryTest(no);
            }
        }
    }

    /**
     * Test search query on tree border.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void queryOnBorderTest() throws IllegalArgumentException, TransformException {
        final List<GeneralEnvelope> lGE = new ArrayList<GeneralEnvelope>();
        tree.deleteAll(lData.iterator());
        final List<Envelope> lGERef = new ArrayList<Envelope>();
        final GeneralEnvelope gR = new GeneralEnvelope(crs);
        
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
        tree.insertAll(lGE.iterator());
        final List<Envelope> lGES = new ArrayList<Envelope>();
        tree.search(gR, new DefaultTreeVisitor(lGES));
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
     * Compare boundary node from his children boundary.
     */
    public boolean checkBoundaryNode(final Node node) {
        final List<Envelope> lGE = new ArrayList<Envelope>();
        if (node.isLeaf()) {
            for (Envelope gEnv : node.getEntries()) {
                lGE.add(gEnv);
            }

        } else {
            for (Node no : node.getChildren()) {
                lGE.add(no.getBoundary());
            }
        }
        final GeneralEnvelope subBound = DefaultTreeUtils.getEnveloppeMin(lGE);
        return subBound.equals(node.getBoundary());
    }

    /**
     * Test search query inside tree.
     */
    @Test
    public void queryInsideTest() throws MismatchedReferenceSystemException {
        final List<Envelope> listSearch = new ArrayList<Envelope>();
        tree.search(DefaultTreeUtils.getEnveloppeMin(lData), new DefaultTreeVisitor(listSearch));
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
        final GeneralEnvelope areaSearch = new GeneralEnvelope(crs);
        for (int i = 0; i < dimension; i++) {
            areaSearch.setRange(i, minMax[i+1]+100, minMax[i+1]+2000);
        }
        final List<Envelope> listSearch = new ArrayList<Envelope>();
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
        tree.deleteAll(lData.iterator());
        final GeneralEnvelope areaSearch = new GeneralEnvelope(crs);
        for (int i = 0; i < dimension; i++) {
            areaSearch.setRange(i, minMax[2*i], minMax[2*i+1]);
        }
        final List<Envelope> listSearch = new ArrayList<Envelope>();
        tree.search(areaSearch, new DefaultTreeVisitor(listSearch));
        assertTrue(listSearch.isEmpty());
        assertTrue(tree.getElementsNumber() == 0);
        assertTrue(checkTreeElts(tree));
        insert();
        tree.search(areaSearch, new DefaultTreeVisitor(listSearch));
        assertTrue(compareList(listSearch, lData));
        assertTrue(checkTreeElts(tree));
    }
}

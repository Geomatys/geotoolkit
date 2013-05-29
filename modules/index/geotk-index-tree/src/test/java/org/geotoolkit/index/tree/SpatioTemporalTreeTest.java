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
import java.util.List;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.basic.BasicRTree;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.geotoolkit.index.tree.io.DefaultTreeVisitor;
import org.geotoolkit.index.tree.io.TreeVisitor;
import org.geotoolkit.index.tree.star.StarRTree;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**Test trees with {@code CoordinateReferenceSystem} Temporal.
 *
 * @author Rémi Maréchal (Géomatys).
 */
public class SpatioTemporalTreeTest extends TreeTest{

    private static final NodeFactory NODEFACTORY = DefaultNodeFactory.INSTANCE;
    private static final CoordinateReferenceSystem CARTESIAN_2DCRS = DefaultEngineeringCRS.CARTESIAN_2D;
    private static final CoordinateReferenceSystem CARTESIAN_3DCRS = DefaultEngineeringCRS.CARTESIAN_3D;
    private static final CoordinateReferenceSystem GEOCENTRIC_2DCRS = DefaultGeographicCRS.WGS84;
    private static final CoordinateReferenceSystem GEOCENTRIC_3DCRS = DefaultGeographicCRS.WGS84_3D;
    private static final CoordinateReferenceSystem TEMPORALCRS = DefaultTemporalCRS.JAVA;
    Tree tree;
    CoordinateReferenceSystem crs;
    final List<GeneralEnvelope>lData = new ArrayList<GeneralEnvelope>();
    final List<List<GeneralEnvelope>> lResult = new ArrayList<List<GeneralEnvelope>>();
    int indexTemp;
    public SpatioTemporalTreeTest() {

    }

    public void setTree(Tree tree, int indexTemp){
        this.indexTemp = indexTemp;
        this.tree = tree;
        this.crs = tree.getCrs();
        lData.clear();
        lResult.clear();
        GeneralDirectPosition dpTemp = new GeneralDirectPosition(crs);
        final int dim = crs.getCoordinateSystem().getDimension();
        for(int temp = 0; temp < 100; temp += 10){
            dpTemp.setOrdinate(indexTemp, temp);
            final List<GeneralEnvelope> lgeT = new ArrayList<GeneralEnvelope>();
            for (int i = 0; i < 300; i++) {
                for (int o = 0; o < dim; o++){
                    if (o != indexTemp) dpTemp.setOrdinate(o, Math.random()*10);
                }
                lgeT.add(new GeneralEnvelope(dpTemp, dpTemp));
            }
            lData.addAll(lgeT);
            lResult.add(lgeT);
        }
        for (GeneralEnvelope ge : lData) {
            tree.insert(ge);
        }
    }

    @Test
    public void testHilbert(){
        CoordinateReferenceSystem crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { TEMPORALCRS, CARTESIAN_2DCRS});
        final Tree hilbertA = new HilbertRTree(4, 2, crsCompound, NODEFACTORY);
        setTree(hilbertA, 0);
        test();
        assertTrue(checkTreeElts(tree));
        crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { CARTESIAN_2DCRS, TEMPORALCRS});
        final Tree hilbertABis = new HilbertRTree(4, 2, crsCompound, NODEFACTORY);
        setTree(hilbertABis, 2);
        test();
        assertTrue(checkTreeElts(tree));
        crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { GEOCENTRIC_2DCRS, TEMPORALCRS});
        final Tree hilbertB = new HilbertRTree(4, 2, crsCompound, NODEFACTORY);
        setTree(hilbertB, 2);
        test();
        assertTrue(checkTreeElts(tree));
        crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { TEMPORALCRS, GEOCENTRIC_2DCRS});
        final Tree hilbertBBis = new HilbertRTree(4, 2, crsCompound, NODEFACTORY);
        setTree(hilbertBBis, 0);
        test();
        assertTrue(checkTreeElts(tree));
        crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { TEMPORALCRS, CARTESIAN_3DCRS});
        final Tree hilbertC = new HilbertRTree(4, 2, crsCompound, NODEFACTORY);
        setTree(hilbertC, 0);
        test();
        assertTrue(checkTreeElts(tree));
        crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { CARTESIAN_3DCRS, TEMPORALCRS});
        final Tree hilbertCBis = new HilbertRTree(4, 2, crsCompound, NODEFACTORY);
        setTree(hilbertCBis, 3);
        test();
        assertTrue(checkTreeElts(tree));
        crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { GEOCENTRIC_3DCRS, TEMPORALCRS});
        final Tree hilbertD = new HilbertRTree(4, 2, crsCompound, NODEFACTORY);
        setTree(hilbertD, 3);
        test();
        assertTrue(checkTreeElts(tree));
        crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { TEMPORALCRS, GEOCENTRIC_3DCRS});
        final Tree hilbertDBis = new HilbertRTree(4, 2, crsCompound, NODEFACTORY);
        setTree(hilbertDBis, 0);
        test();
        assertTrue(checkTreeElts(tree));
    }

    @Test
    public void testStar(){
        CoordinateReferenceSystem crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { TEMPORALCRS, CARTESIAN_2DCRS});
        final Tree starRTreeA = new StarRTree(4, crsCompound, NODEFACTORY);
        setTree(starRTreeA, 0);
        test();
        assertTrue(checkTreeElts(tree));
        crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { GEOCENTRIC_2DCRS, TEMPORALCRS});
        final Tree starRTreeB = new StarRTree(4, crsCompound, NODEFACTORY);
        setTree(starRTreeB, 2);
        test();
        assertTrue(checkTreeElts(tree));
        crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { TEMPORALCRS, CARTESIAN_3DCRS});
        final Tree starRTreeC = new StarRTree(4, crsCompound, NODEFACTORY);
        setTree(starRTreeC, 0);
        test();
        assertTrue(checkTreeElts(tree));
        crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { GEOCENTRIC_3DCRS, TEMPORALCRS});
        final Tree starRTreeD = new StarRTree(4, crsCompound, NODEFACTORY);
        setTree(starRTreeD, 3);
        test();
        assertTrue(checkTreeElts(tree));
    }

    @Test
    public void testBasic(){
        CoordinateReferenceSystem crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { TEMPORALCRS, CARTESIAN_2DCRS});
        final Tree basicA = new BasicRTree(4, crsCompound, SplitCase.QUADRATIC, NODEFACTORY);
        setTree(basicA, 0);
        test();
        assertTrue(checkTreeElts(tree));
        crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { GEOCENTRIC_2DCRS, TEMPORALCRS});
        final Tree basicB = new BasicRTree(4, crsCompound, SplitCase.QUADRATIC, NODEFACTORY);
        setTree(basicB, 2);
        test();
        assertTrue(checkTreeElts(tree));
        crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { TEMPORALCRS, CARTESIAN_3DCRS});
        final Tree basicC = new BasicRTree(4, crsCompound, SplitCase.QUADRATIC, NODEFACTORY);
        setTree(basicC, 0);
        test();
        assertTrue(checkTreeElts(tree));
        crsCompound = new DefaultCompoundCRS("compoundCrs", new CoordinateReferenceSystem[] { GEOCENTRIC_3DCRS, TEMPORALCRS});
        final Tree basicD = new BasicRTree(4, crsCompound, SplitCase.QUADRATIC, NODEFACTORY);
        setTree(basicD, 3);
        test();
        assertTrue(checkTreeElts(tree));
    }

    public void test(){
        final GeneralEnvelope areaSearch1 = new GeneralEnvelope(crs);
        initAreaSearch(areaSearch1, 0, 9);
        final List<Envelope> listSearch = new ArrayList<Envelope>();
        final List<Envelope> listRef = new ArrayList<Envelope>();
        final TreeVisitor tv = new DefaultTreeVisitor(listSearch);
        listRef.addAll(lResult.get(0));
        tree.search(areaSearch1, tv);
        assertTrue(compareList(listSearch, listRef));

        listRef.clear();
        listSearch.clear();
        initAreaSearch(areaSearch1, 85, 95);
        listRef.addAll(lResult.get(9));
        tree.search(areaSearch1, tv);
        assertTrue(compareList(listSearch, listRef));

        listRef.clear();
        listSearch.clear();
        initAreaSearch(areaSearch1, 45, 75);
        listRef.addAll(lResult.get(5));
        listRef.addAll(lResult.get(6));
        listRef.addAll(lResult.get(7));
        tree.search(areaSearch1, tv);
        assertTrue(compareList(listSearch, listRef));
    }

    public void initAreaSearch(final GeneralEnvelope area, final double tBeg, final double tEnd) {
        area.setEnvelope(tree.getRoot().getBoundary());
        area.setRange(indexTemp, tBeg, tEnd);
    }
}

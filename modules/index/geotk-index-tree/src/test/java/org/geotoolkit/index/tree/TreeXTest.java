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
import org.geotoolkit.filter.SpatialFilterType;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.io.DefaultTreeVisitor;
import org.geotoolkit.index.tree.io.TreeVisitor;
import org.geotoolkit.index.tree.io.TreeX;
import org.geotoolkit.index.tree.nodefactory.TreeNodeFactory;
import org.geotoolkit.index.tree.star.StarRTree;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opengis.geometry.Envelope;

/**Test static TreeX methods.
 * Intersect test is already effectuate by tree test suite.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class TreeXTest extends TreeTest {

    List<Envelope> listSearch = new ArrayList<Envelope>();
    TreeVisitor defVisit = new DefaultTreeVisitor(listSearch);
    Tree tree = new StarRTree(4, DefaultEngineeringCRS.CARTESIAN_3D, TreeNodeFactory.DEFAULT_FACTORY);

    public TreeXTest() {
        final GeneralEnvelope geTemp = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_3D);
        for(int z = 0; z<=200; z+=20) {
            for(int y = 0; y<=200; y+=20) {
                for(int x = 0; x<=200; x+=20) {
                    geTemp.setEnvelope(x-5, y-5, z-5, x+5, y+5, z+5);
                    tree.insert(new GeneralEnvelope(geTemp));
                }
            }
        }
    }

    @Test
    public void testContains() {
        final List<Envelope> listRef = new ArrayList<Envelope>();
        final GeneralEnvelope geTemp = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_3D);
        geTemp.setEnvelope(115, 135, 35, 125, 145, 45);
        listRef.add(new GeneralEnvelope(geTemp));
        geTemp.setEnvelope(116, 136, 36, 124, 144, 44);
        TreeX.search(tree, geTemp, SpatialFilterType.CONTAINS, defVisit);
        assertTrue(compareList(listRef, listSearch));
        listSearch.clear();
        geTemp.setEnvelope(tree.getRoot().getBoundary());
        TreeX.search(tree, geTemp, SpatialFilterType.CONTAINS, defVisit);
        assertTrue(listSearch.isEmpty());
    }

    @Test
    public void testDisjoint() {
        final List<Envelope> listRef = new ArrayList<Envelope>();
        final GeneralEnvelope geTemp = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_3D);
        for(int z = 0; z<=100; z+=20) {
            for(int y = 0; y<=200; y+=20) {
                for(int x = 0; x<=200; x+=20) {
                    geTemp.setEnvelope(x-5, y-5, z-5, x+5, y+5, z+5);
                    listRef.add(new GeneralEnvelope(geTemp));
                }
            }
        }
        geTemp.setEnvelope(-10, -10, 110, 210, 210, 210);
        TreeX.search(tree, geTemp, SpatialFilterType.DISJOINT, defVisit);
        assertTrue(compareList(listRef, listSearch));
        listSearch.clear();
        geTemp.setEnvelope(tree.getRoot().getBoundary());
        TreeX.search(tree, geTemp, SpatialFilterType.DISJOINT, defVisit);
        assertTrue(listSearch.isEmpty());
    }

    @Test
    public void testWithin() {
        final List<Envelope> listRef = new ArrayList<Envelope>();
        final GeneralEnvelope geTemp = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_3D);
        for(int z = 0; z<=200; z+=20) {
            for(int y = 0; y<=200; y+=20) {
                    geTemp.setEnvelope(195, y-5, z-5, 205, y+5, z+5);
                    listRef.add(new GeneralEnvelope(geTemp));
            }
        }
        geTemp.setEnvelope(180, -10, -10, 210, 210, 210);
        TreeX.search(tree, geTemp, SpatialFilterType.WITHIN, defVisit);
        assertTrue(compareList(listRef, listSearch));
        listSearch.clear();
        geTemp.setEnvelope(-10, 97, -10, 210, 104, 210);
        TreeX.search(tree, geTemp, SpatialFilterType.WITHIN, defVisit);
        assertTrue(listSearch.isEmpty());
    }

    @Test
    public void testTouches() {
        final List<Envelope> listRef = new ArrayList<Envelope>();
        final GeneralEnvelope geTemp = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_3D);
        for(int z = 0; z<=200; z+=20) {
            for(int y = 0; y<=200; y+=20) {
                for(int x = 140; x<=160; x+=20) {
                    geTemp.setEnvelope(x-5, y-5, z-5, x+5, y+5, z+5);
                    listRef.add(new GeneralEnvelope(geTemp));
                }
            }
        }
        geTemp.setEnvelope(145, -10, -10, 155, 210, 210);
        TreeX.search(tree, geTemp, SpatialFilterType.TOUCHES, defVisit);
        assertTrue(compareList(listRef, listSearch));
        listSearch.clear();
        geTemp.setEnvelope(144, -10, -10, 156, 210, 210);
        TreeX.search(tree, geTemp, SpatialFilterType.TOUCHES, defVisit);
        assertTrue(listSearch.isEmpty());
    }

    @Test
    public void testEquals() {
        final List<Envelope> listRef = new ArrayList<Envelope>();
        final GeneralEnvelope geTemp = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_3D);
        geTemp.setEnvelope(115, 135, 35, 125, 145, 45);
        listRef.add(new GeneralEnvelope(geTemp));
        geTemp.setEnvelope(115, 135, 35, 125, 145, 45);
        TreeX.search(tree, geTemp, SpatialFilterType.EQUALS, defVisit);
        assertTrue(compareList(listRef, listSearch));
        listSearch.clear();
        geTemp.setEnvelope(tree.getRoot().getBoundary());
        TreeX.search(tree, geTemp, SpatialFilterType.EQUALS, defVisit);
        assertTrue(listSearch.isEmpty());
    }

    @Test
    public void testOverlaps() {
        final List<Envelope> listRef = new ArrayList<Envelope>();
        final GeneralEnvelope geTemp = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_3D);
        for(int z = 0; z<=200; z+=20){
            for(int y = 0; y<=200; y+=20){
                for(int x = 140; x<=160; x+=20){
                    geTemp.setEnvelope(x-5, y-5, z-5, x+5, y+5, z+5);
                    listRef.add(new GeneralEnvelope(geTemp));
                }
            }
        }
        geTemp.setEnvelope(144, -10, -10, 156, 210, 210);
        TreeX.search(tree, geTemp, SpatialFilterType.OVERLAPS, defVisit);
        assertTrue(compareList(listRef, listSearch));
        listSearch.clear();
        geTemp.setEnvelope(145, -10, -10, 155, 210, 210);
        TreeX.search(tree, geTemp, SpatialFilterType.OVERLAPS, defVisit);
        assertTrue(listSearch.isEmpty());
        listSearch.clear();
        geTemp.setEnvelope(145, -10, -10, 165, 210, 210);
        TreeX.search(tree, geTemp, SpatialFilterType.OVERLAPS, defVisit);
        assertTrue(listSearch.isEmpty());
    }

}

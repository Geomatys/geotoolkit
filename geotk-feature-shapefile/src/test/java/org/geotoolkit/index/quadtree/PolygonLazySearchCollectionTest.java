/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2007-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.index.quadtree;

import java.io.File;
import java.util.Iterator;
import org.geotoolkit.feature.FeatureExt;

import org.geotoolkit.data.shapefile.AbstractTestCaseSupport;
import org.geotoolkit.data.shapefile.indexed.IndexedShapefileFeatureStore;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.junit.Assert.*;

/**
 * @author Jesse
 * @module
 */
public class PolygonLazySearchCollectionTest extends AbstractTestCaseSupport {

    private File file;
    private IndexedShapefileFeatureStore ds;
    private QuadTree tree;
    private DataReader dr;
    private Iterator iterator;
    private CoordinateReferenceSystem crs;

    @Before
    public void setUp() throws Exception {
        file = copyShapefiles("shapes/statepop.shp");
        ds = new IndexedShapefileFeatureStore(file.toURI());
        ds.buildQuadTree(0);
        final Object[] v = LineLazySearchCollectionTest.openQuadTree(file);
        tree = (QuadTree) v[0];
        dr = (DataReader) v[1];
        crs = FeatureExt.getCRS(ds.getFeatureType(ds.getNames().iterator().next().toString()));
    }

    @After
    public void tearDown() throws Exception {
        if (iterator != null)
            tree.close(iterator);
        tree.close();
        super.tearDown();
        file.getParentFile().delete();
    }

    @Test
    public void testGetAllFeatures() throws Exception {
        JTSEnvelope2D env = new JTSEnvelope2D(-125.5, -66, 23.6,
                53.0, crs);
        LazySearchCollection collection = new LazySearchCollection(tree, dr, env);
        assertEquals(49, collection.size());
    }

    @Test
    public void testGetOneFeatures() throws Exception {
        JTSEnvelope2D env = new JTSEnvelope2D(-70, -68.2, 44.5, 45.7,crs);
        LazySearchCollection collection = new LazySearchCollection(tree, dr, env);
        assertEquals(22, collection.size());
    }

    @Test
    public void testGetNoFeatures() throws Exception {
        JTSEnvelope2D env = new JTSEnvelope2D(0, 10, 0, 10, crs);
        LazySearchCollection collection = new LazySearchCollection(tree, dr, env);
        assertEquals(0, collection.size());
    }
}

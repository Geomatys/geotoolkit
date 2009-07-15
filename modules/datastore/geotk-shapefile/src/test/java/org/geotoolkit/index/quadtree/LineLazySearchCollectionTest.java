/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
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
import java.io.IOException;
import java.util.Iterator;

import org.geotoolkit.data.shapefile.ShpFiles;
import org.geotoolkit.data.shapefile.AbstractTestCaseSupport;
import org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStore;
import org.geotoolkit.data.shapefile.shp.IndexFile;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.index.quadtree.fs.FileSystemIndexStore;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Jesse
 */
public class LineLazySearchCollectionTest extends AbstractTestCaseSupport {

    private File file;
    private IndexedShapefileDataStore ds;
    private QuadTree tree;
    private Iterator iterator;
    private CoordinateReferenceSystem crs;

    public LineLazySearchCollectionTest() throws IOException {
        super("LazySearchIteratorTest");
    }

    protected void setUp() throws Exception {
        super.setUp();
        file = copyShapefiles("shapes/streams.shp");
        ds = new IndexedShapefileDataStore(file.toURL());
        ds.buildQuadTree(0);
        tree = openQuadTree(file);
        crs = ds.getSchema().getCoordinateReferenceSystem();
    }

    public static QuadTree openQuadTree(File file) throws StoreException {
        File qixFile = sibling(file, "qix");
        FileSystemIndexStore store = new FileSystemIndexStore(qixFile);
        try {
            ShpFiles shpFiles = new ShpFiles(qixFile);

            IndexFile indexFile = new IndexFile(shpFiles, false);
            return store.load(indexFile);

        } catch (IOException e) {
            throw new StoreException(e);
        }
    }

    protected void tearDown() throws Exception {
        if (iterator != null)
            tree.close(iterator);
        tree.close();
        super.tearDown();
        file.getParentFile().delete();
    }

    public void testGetAllFeatures() throws Exception {
        JTSEnvelope2D env = new JTSEnvelope2D(585000, 610000,
                4910000, 4930000, crs);
        LazySearchCollection collection = new LazySearchCollection(tree, env);
        assertEquals(116, collection.size());
    }

    public void testGetOneFeatures() throws Exception {
        JTSEnvelope2D env = new JTSEnvelope2D(588993, 589604,
                4927443, 4927443, crs);
        LazySearchCollection collection = new LazySearchCollection(tree, env);
        assertEquals(14, collection.size());

    }

    public void testGetNoFeatures() throws Exception {
        JTSEnvelope2D env = new JTSEnvelope2D(592211, 597000,
                4910947, 4913500, crs);
        LazySearchCollection collection = new LazySearchCollection(tree, env);
        assertEquals(0, collection.size());
    }
}

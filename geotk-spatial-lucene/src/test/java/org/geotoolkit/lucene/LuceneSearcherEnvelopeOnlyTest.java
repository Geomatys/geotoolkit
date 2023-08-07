/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.lucene;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.geotoolkit.filter.FilterFactory2;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.index.LogicalFilterType;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.manager.NamedEnvelope;
import org.geotoolkit.index.tree.manager.SQLRtreeManager;
import org.geotoolkit.index.tree.manager.postgres.LucenePostgresSQLTreeEltMapper;
import org.geotoolkit.index.tree.manager.postgres.PGDataSource;
import org.geotoolkit.io.wkb.WKBUtils;
import org.geotoolkit.lucene.DocumentIndexer.DocumentEnvelope;
import static org.geotoolkit.lucene.LuceneSearcherTest.getresultsfromID;
import org.geotoolkit.lucene.analysis.standard.ClassicAnalyzer;
import org.geotoolkit.lucene.filter.LuceneOGCSpatialQuery;
import static org.geotoolkit.lucene.filter.LuceneOGCSpatialQuery.*;
import org.geotoolkit.lucene.filter.SpatialQuery;
import org.geotoolkit.lucene.index.LuceneIndexSearcher;
import org.geotoolkit.nio.IOUtilities;
import org.junit.*;
import static org.junit.Assert.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.filter.FilterFactory;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * A Test classes testing the different spatial filters.
 *
 * @author Guilhem Legal
 * @module
 */
public class LuceneSearcherEnvelopeOnlyTest {

    private static final GeometryFactory GF = JTS.getFactory();
    private static final FilterFactory2 FF = DefaultFactories.forBuildin(FilterFactory.class, FilterFactory2.class);
    private static final Logger LOGGER = Logger.getLogger("org.constellation.lucene");
    private static final double TOLERANCE = 0.001;

    private static final Map<String, NamedEnvelope> envelopes = new HashMap<>();
    private static final Path directory = Paths.get("luceneSearcherEnvTest");
    private static LuceneIndexSearcher searcher;
    private static CoordinateReferenceSystem treeCrs;
    private org.opengis.filter.Filter filter;
    private Geometry geom;
    private static Query simpleQuery;

    @BeforeClass
    public static void setUpMethod() throws Exception {

        IOUtilities.deleteRecursively(directory);
        Files.createDirectory(directory);

        // the tree CRS (must be) cartesian
        treeCrs = CRS.forCode("CRS:84");

        //creating tree (R-Tree)------------------------------------------------

        final Analyzer analyzer  = new StandardAnalyzer();
        final DocumentIndexer indexer = new DocumentIndexer(directory, fillTestData(), analyzer);
        indexer.createIndex();
        indexer.destroy();
        searcher = new LuceneIndexSearcher(directory, null, new StandardAnalyzer(), true);
        simpleQuery = new TermQuery(new Term("metafile", "doc"));

    }

    @AfterClass
    public static void tearDownMethod() throws Exception {
        // postgres
        if (PGDataSource.isSetPGDataSource()) {
            if (Files.isDirectory(directory)) {
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
                    final Iterator<Path> iterator = directoryStream.iterator();
                    if (iterator.hasNext()) {
                        LucenePostgresSQLTreeEltMapper.resetDB(iterator.next());
                    }
                }
            }
        }
        try {
            searcher.destroy();
        } finally {
            IOUtilities.deleteRecursively(directory);
        }
    }

    /**
     * Test the spatial filter BBOX.
     * @throws java.lang.Exception
     */
    @Test
    public void BBOXTest() throws Exception {

        /*
         * first bbox
         */
        org.opengis.filter.Filter spaFilter = FF.bbox(GEOMETRY_PROPERTY, -20,-20,20,20,"CRS:84");
        SpatialQuery bboxQuery = new SpatialQuery(wrap(spaFilter));

        //we perform a lucene query
        Set<String> results = searcher.doSearch(bboxQuery);

        int nbResults = results.size();
        LOGGER.log(Level.FINER, "BBOX:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));

        /*
         * The same box in a diferent crs
         */
        spaFilter = FF.bbox(GEOMETRY_PROPERTY,
                -2226389.8158654715, -2258423.6490963786,
                2226389.8158654715, 2258423.6490963805,
                "EPSG:3395");
        bboxQuery = new SpatialQuery(wrap(spaFilter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "BBOX:BBOX 1 CRS= 3395: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));

        /*
         * second bbox
         */
        spaFilter = FF.bbox(GEOMETRY_PROPERTY, -5, -5, 60, 60, "CRS:84");
        bboxQuery = new SpatialQuery(wrap(spaFilter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "BBOX:BBOX 2 CRS= 4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results
        assertEquals(nbResults, 4);
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 5"));

        /*
         * third bbox
         */
        spaFilter = FF.bbox(GEOMETRY_PROPERTY, 40, -9, 50, -5, "CRS:84");
        bboxQuery = new SpatialQuery(wrap(spaFilter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "BBOX:BBOX 3 CRS= 4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 5"));
    }

    /**
     * Test the rTree.
     * @throws java.lang.Exception
     */
    @Test
    public void rTreeBBOXTest() throws Exception {
        final Tree rTree = SQLRtreeManager.get(searcher.getFileDirectory(), this);

        /*
         * first bbox
         */
        GeneralEnvelope env = new GeneralEnvelope(CommonCRS.defaultGeographic());
        env.setEnvelope(-20,-20,20,20);

        // reproject to tree CRS
        env = (GeneralEnvelope) Envelopes.transform(env, treeCrs);

        //we perform a retree query
        List<Envelope> docs = new ArrayList<>();
        final TreeElementMapper<NamedEnvelope> tem = rTree.getTreeElementMapper();

        int[] resultsID = rTree.searchID(env);
        getresultsfromID(resultsID, tem, docs);

        int nbResults = docs.size();
        LOGGER.log(Level.FINER, "BBOX:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            NamedEnvelope doc = (NamedEnvelope) docs.get(i);
            String id =  doc.getId();
            results.add(id);
            LOGGER.log(Level.FINER, "\tid: {0}", id);
        }

        //we verify that we obtain the correct results
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected")); // depends on test order
        assertTrue(results.contains("box 4"));

        /*
         * The same box in a diferent crs
         */
        env = new GeneralEnvelope(CRS.forCode("EPSG:3395"));
        env.setEnvelope(-2226389.8158654715, -2258423.6490963786,
                         2226389.8158654715, 2258423.6490963805);

        // reproject to tree CRS
        env = (GeneralEnvelope) Envelopes.transform(env, treeCrs);


        //we perform a retree query
        docs.clear();
        resultsID = rTree.searchID(env);
        getresultsfromID(resultsID, tem, docs);

        nbResults = docs.size();
        LOGGER.log(Level.FINER, "BBOX:BBOX 1 CRS= 3395: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            NamedEnvelope doc = (NamedEnvelope) docs.get(i);
            String name =  doc.getId();
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected")); // depends on test order
        assertTrue(results.contains("box 4"));

        /*
         * second bbox
         */
        env = new GeneralEnvelope(CommonCRS.defaultGeographic());
        env.setEnvelope(-5, -5, 60, 60);

        // reproject to tree CRS
        env = (GeneralEnvelope) Envelopes.transform(env, treeCrs);

        //we perform a retree query
        docs.clear();
        resultsID = rTree.searchID(env);
        getresultsfromID(resultsID, tem, docs);

        nbResults = docs.size();
        LOGGER.log(Level.FINER, "BBOX:BBOX 2 CRS= 4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            NamedEnvelope doc = (NamedEnvelope) docs.get(i);
            String name =  doc.getId();
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

         //we verify that we obtain the correct results
        assertEquals(nbResults, 4);
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected")); // depends on test order
        assertTrue(results.contains("box 5"));

        /*
         * third bbox
         */
        env = new GeneralEnvelope(CommonCRS.defaultGeographic());
        env.setEnvelope(40, -9, 50, -5);

        // reproject to tree CRS
        env = (GeneralEnvelope) Envelopes.transform(env, treeCrs);

        //we perform a retree query
        docs.clear();
        resultsID = rTree.searchID(env);
        getresultsfromID(resultsID, tem, docs);

        nbResults = docs.size();
        LOGGER.log(Level.FINER, "BBOX:BBOX 3 CRS= 4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            NamedEnvelope doc = (NamedEnvelope) docs.get(i);
            String name =  doc.getId();
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

         //we verify that we obtain the correct results
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 5"));
    }

    /**
     * Test the spatial filter INTERSECT.
     */
    @Test
    public void intersectTest() throws Exception {

        /*
         * case 1: bbox.
         */
        double min1[] = {-20, -20};
        double max1[] = { 20,  20};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.intersects(GEOMETRY_PROPERTY, FF.literal(bbox));
        SpatialQuery bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        Set<String> results = searcher.doSearch(bboxQuery);

        int nbResults = results.size();
        LOGGER.log(Level.FINER, "INTER:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 2"  ));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"  ));

        /*
         * case 2: The same box in a diferent crs.
         */
        double min2[] = {-2226389.8158654715, -2258423.6490963786};
        double max2[] = { 2226389.8158654715,  2258423.6490963805};
        bbox = new GeneralEnvelope(min2, max2);
        bbox.setCoordinateReferenceSystem(AbstractCRS.castOrCopy(CRS.forCode("EPSG:3395")).forConvention(AxesConvention.RIGHT_HANDED));
        filter = FF.intersects(GEOMETRY_PROPERTY, FF.literal(bbox));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "INTER:BBOX 1 CRS= 3395: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 2"  ));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"  ));

        /*
         * case 3: line
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(7, 30),
            new Coordinate(7, -30),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.intersects(GEOMETRY_PROPERTY,FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "INTER:Line 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));

        /*
         * case 4: same line diferent CRS
         */
        final CoordinateReferenceSystem CRS3395 = CRS.forCode("EPSG:3395");
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(775978.5043848383, 3339584.723798207),
            new Coordinate(775978.5043848383, -3339584.723798207),
        });
        JTS.setCRS(geom, CRS3395);
        filter = FF.intersects(GEOMETRY_PROPERTY, FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "INTER:Line 1 CRS=3395: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 2"  ));
        assertTrue(results.contains("box 2 projected"));

        /*
         * case 5: another line
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(40, 40),
            new Coordinate(40, -30),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.intersects(GEOMETRY_PROPERTY, FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "INTER:Line 2 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 3"  ));

        /*
         * case 6: same line another CRS
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(4452779.631730943, 4838471.398061137),
            new Coordinate(4452779.631730943, -3339584.723798207),
        });
        JTS.setCRS(geom, CRS3395);
        filter = FF.intersects(GEOMETRY_PROPERTY, FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "INTER:Line 2 CRS=3395: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 3"  ));
    }

    /**
     * Test the spatial filter EQUALS.
     */
    @Test
    public void equalsTest() throws Exception {

        /*
         * case 1: bbox.
         */
        double min1[] = { 30,   0};
        double max1[] = { 50,  15};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.equals(GEOMETRY_PROPERTY, FF.literal(bbox));
        SpatialQuery bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        Set<String> results = searcher.doSearch(bboxQuery);

        int nbResults = results.size();
        LOGGER.log(Level.FINER, "EQ:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 3"));


        /*
         * case 2: line
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(0, 0),
            new Coordinate(25, 0),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.equals(GEOMETRY_PROPERTY, FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "EQ:Line 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);


        /*
         * case 3: point
         */
        geom = GF.createPoint(new Coordinate(-10, 10));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.equals(GEOMETRY_PROPERTY, FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "EQ:Point 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);
    }

    /**
     * Test the spatial filter CONTAINS.
     */
    @Test
    public void containsTest() throws Exception {

        /*
         * case 1: BOX/BOX.
         */
        double min1[] = {-30, -47};
        double max1[] = {-26, -42};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.contains(GEOMETRY_PROPERTY, FF.literal(bbox));
        SpatialQuery bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        Set<String> results = searcher.doSearch(bboxQuery);

        int nbResults = results.size();
        LOGGER.log(Level.FINER, "CT:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 1"));

        /*
         * case 2: BOX/Line
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(-25, 5),
            new Coordinate(-15, 5),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.contains(GEOMETRY_PROPERTY, FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "CT:Line 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 4"));

        /*
         * case 3: BOX/point
         */
        geom = GF.createPoint(new Coordinate(-25, 5));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.contains(GEOMETRY_PROPERTY, FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "CT:Point 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 4"));

        /*
         * case 4: Line/point
         */
        geom = GF.createPoint(new Coordinate(20, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),0.00001,"m");
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "CT:Point 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);

        /*
         * case 5: Line/Line
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(20, 0),
            new Coordinate(15, 0),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),TOLERANCE,"m");
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "CT:Line 2 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);
    }

    /**
     * Test the spatial filter DISJOINT.
     */
    @Test
    public void disjointTest() throws Exception {

        /*
         * case 1: point
         *
         */
        geom = GF.createPoint(new Coordinate(-25, 5));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.disjoint(GEOMETRY_PROPERTY, FF.literal(geom));
        SpatialQuery spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        Set<String> results = searcher.doSearch(spatialQuery);

        int nbResults = results.size();
        LOGGER.log(Level.FINER, "DJ:Point 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 5);
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 5"));

        /*
         * case 2: another point intersecting with the two registered lines.
         *  (equals to point 3)
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.disjoint(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DJ:Point 2 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 6);
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));

        /*
         * case 3: a line
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(-40, 0),
            new Coordinate(30, 0),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.disjoint(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DJ:Line 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 5"));

        /*
         * case 4: another line
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(7, 40),
            new Coordinate(7, -20),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.disjoint(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DJ:Line 2 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));

        /*
         * case 5: a BBOX
         *
         */
        double min1[] = {-20, -20};
        double max1[] = { 20,  20};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.disjoint(GEOMETRY_PROPERTY, FF.literal(bbox));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DJ:BBox 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 1"  ));
        assertTrue(results.contains("box 3"  ));
        assertTrue(results.contains("box 5"));

        /*
         * case 6: another BBOX
         *
         */
        double min2[] = {-50, -60};
        double max2[] = { -5,  60};
        bbox = new GeneralEnvelope(min2, max2);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.disjoint(GEOMETRY_PROPERTY,FF.literal(bbox));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DJ:BBox 2 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 5"));
    }

     /**
     * Test the spatial filter TOUCHES.
     */
    @Test
    public void touchesTest() throws Exception {

        /*
         * case 1: point (equals to point 3)
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        SpatialQuery spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        Set<String> results = searcher.doSearch(spatialQuery);

        int nbResults = results.size();
        LOGGER.log(Level.FINER, "TO:Point 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);

        /*
         * case 2: another point
         *
         */
        geom = GF.createPoint(new Coordinate(-30, 5));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "TO:Point 2 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 4"));

        /*
         * case 3: another point
         *
         */
        geom = GF.createPoint(new Coordinate(-25, -50));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "TO:Point 3 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 1"));

        /*
         * case 4: another point
         *
         */
        geom = GF.createPoint(new Coordinate(0, -10));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));


        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "TO:Point 4 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);
//        assertTrue(results.contains("line 2")); //point intersect or in within, it is not consider "touches" in jts

        /*
         * case 5: another point
         *
         */
        geom = GF.createPoint(new Coordinate(40, 20));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "TO:Point 5 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);
//        assertTrue(results.contains("point 4")); //same point intersect,within,overlaps but not consider "touches"

        /*
         * case 6: a line
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(7, 30),
            new Coordinate(7, 0),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "TO:Line 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);

        /*
         * case 7: another line
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(-15, 3),
            new Coordinate(30, 4),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "TO:Line 2 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));

         /*
         * case 8: another line
         *

        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(0, 0),
            new Coordinate(-40, -40),
        });
        geom.setSRID(SRIDGenerator.toSRID(crs, Version.V1));
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "TO:Line 2 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<String>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 1")); */

        /*
         * case 9: a BBOX
         *
         */
        double min1[] = {-15,   0};
        double max1[] = { 30,  50};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(bbox));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "TO:BBox 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 3"  ));
        assertTrue(results.contains("box 4"  ));
    }

    /**
     * Test the combinated spatial filter.
     */
    @Test
    public void withinTest() throws Exception {

        /*
         * case 1: BBOX
         */
        double min1[] = {-20, -20};
        double max1[] = { 20,  20};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.within(GEOMETRY_PROPERTY, FF.literal(bbox));
        SpatialQuery bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        Set<String> results = searcher.doSearch(bboxQuery);

        int nbResults = results.size();
        LOGGER.log(Level.FINER, "WT:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));

        /*
         * case 2: another BBOX.
         */
        double min2[] = {  3,   5};
        double max2[] = { 55,  50};
        bbox = new GeneralEnvelope(min2, max2);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.within(GEOMETRY_PROPERTY, FF.literal(bbox));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "WT:BBOX 2 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));

        /*
         * case 6: a line
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(-40, 30),
            new Coordinate(40, 20),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.within(GEOMETRY_PROPERTY, FF.literal(geom));
        SpatialQuery spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "WT:Line 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);
//        assertTrue(results.contains("point 4")); //intersect or crosses but not within
//        assertTrue(results.contains("point 5")); // within is only when a point in between two nodes
    }

    /**
     * Test the combinated spatial filter.
     */
    @Test
    public void crossesTest() throws Exception {

        /*
         * case 1: a line
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(40, 10),
            new Coordinate(40, 30),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.crosses(GEOMETRY_PROPERTY, FF.literal(geom));
        SpatialQuery spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        Set<String> results = searcher.doSearch(spatialQuery);

        int nbResults = results.size();
        LOGGER.log(Level.FINER, "CR:Line 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 3"));
//        assertTrue(results.contains("point 4")); //a point cant not cross anything

        /*
         * case 2: another line
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(40, 10),
            new Coordinate(-5, -5),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.crosses(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "CR:Line 2 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 3"));

        /*
         * case 3: another line
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(-25, 5),
            new Coordinate(-35, -45),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.crosses(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "CR:Line 2 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 1"));

        /*
         * case 4: point (equals to point 3)
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.crosses(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "CR:Point 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);
//        assertTrue(results.contains("point 3")); // crossing a point is not possible
//        assertTrue(results.contains("line 1"));
//        assertTrue(results.contains("line 1 projected"));
//        assertTrue(results.contains("line 2"));

        /*
         * case 5: another point
         *
         */
        geom = GF.createPoint(new Coordinate(5, 13));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.crosses(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "CR:Point 2 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);
//        assertTrue(results.contains("box 2"));            //crossing a point is not possible
//        assertTrue(results.contains("box 2 projected"));

        /*
         * case 6: a BBOX
         *
         */
        double min1[] = {-10, -20};
        double max1[] = { 20,   5};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.crosses(GEOMETRY_PROPERTY, FF.literal(bbox));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "CR:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);
//        assertTrue(results.contains("point 2"));     //points can not cross anything
    }

    /**
     * Test the combinated spatial filter.
     */
    @Test
    public void mulitpleFilterTest() throws Exception {

         /*
         * case 1: a BBOX TOUCHES filter OR a BBOX filter
         *
         */
        double min1[] = { 25, -10};
        double max1[] = { 60,  50};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        org.opengis.filter.Filter filter1 = FF.touches(GEOMETRY_PROPERTY, FF.literal(bbox));
        org.opengis.filter.Filter filter2 = FF.bbox(GEOMETRY_PROPERTY, 25,-10,60,50,"CRS:84");
        SpatialQuery spatialQuery1 = new SpatialQuery(wrap(filter1));
        SpatialQuery spatialQuery2 = new SpatialQuery(wrap(filter2));

        BooleanQuery serialQuery = new BooleanQuery.Builder()
                                .add(spatialQuery1.getQuery(), BooleanClause.Occur.SHOULD)
                                .add(spatialQuery2.getQuery(), BooleanClause.Occur.SHOULD)
                                .build();

        SpatialQuery sQuery = new SpatialQuery("", serialQuery, LogicalFilterType.AND);

        //we perform a lucene query
        Set<String> results = searcher.doSearch(sQuery);

        int nbResults = results.size();
        LOGGER.log(Level.FINER, "TO || BBOX: BBox 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 3"  ));
        assertTrue(results.contains("box 5"));

        // TODO add precision
        //assertTrue(results.contains("line 1 projected"));

        /*
         * case 2: same test with AND instead of OR
         *
         */
        serialQuery = new BooleanQuery.Builder()
                                .add(spatialQuery1.getQuery(), BooleanClause.Occur.MUST)
                                .add(spatialQuery2.getQuery(), BooleanClause.Occur.MUST)
                                .build();
        sQuery = new SpatialQuery("", serialQuery, LogicalFilterType.AND);

        //we perform a lucene query
        results = searcher.doSearch(sQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "TO && BBOX: BBox 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);

        /*
         * case 3: NOT INTERSECT line1
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(7, 40),
            new Coordinate(6, -40),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.intersects(GEOMETRY_PROPERTY, FF.literal(geom));
        SpatialQuery spatialQuery = new SpatialQuery(wrap(filter));
        serialQuery = new BooleanQuery.Builder()
                          .add(spatialQuery.getQuery(), BooleanClause.Occur.MUST_NOT)
                          .add(simpleQuery,                     BooleanClause.Occur.MUST)
                          .build();
        sQuery = new SpatialQuery("", serialQuery, LogicalFilterType.AND);

        //we perform a lucene query
        results = searcher.doSearch(sQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "NOT INTER:Line 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));


        /*
         * case 4: INTERSECT line AND BBOX
         *
         */
        double min2[]          = {-12, -17};
        double max2[]          = { 15,  50};
        GeneralEnvelope bbox2  = new GeneralEnvelope(min2, max2);
        bbox2.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        org.opengis.filter.Filter bfilter = FF.bbox(GEOMETRY_PROPERTY, -12,-17,15,50,"CRS:84");
        SpatialQuery bboxQuery = new SpatialQuery(wrap(bfilter));
        serialQuery = new BooleanQuery.Builder()
                          .add(spatialQuery.getQuery(), BooleanClause.Occur.MUST)
                          .add(bboxQuery.getQuery(),    BooleanClause.Occur.MUST)
                          .build();
        sQuery = new SpatialQuery("", serialQuery, LogicalFilterType.AND);

        //we perform a lucene query
        results = searcher.doSearch(sQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "NOT INTER:Line 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
    assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));

        /*
         * case 5: INTERSECT line AND NOT BBOX
         *
         */
         serialQuery = new BooleanQuery.Builder()
                          .add(spatialQuery.getQuery(), BooleanClause.Occur.MUST)
                          .add(bboxQuery.getQuery(),    BooleanClause.Occur.MUST_NOT)
                          .build();
        sQuery = new SpatialQuery("", serialQuery, LogicalFilterType.AND);

        //we perform a lucene query
        results = searcher.doSearch(sQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "NOT INTER:Line 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);


    }

    /**
     * Test the distance spatial filter DWithin.
     */
    @Test
    public void dWithinTest() throws Exception {

        /*
         * case 1: point distance 5Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),5.0,"kilometers");
        SpatialQuery spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        Set<String> results = searcher.doSearch(spatialQuery);

        int nbResults = results.size();
        LOGGER.log(Level.FINER, "DW:Point 1 dist: 5km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);

        /*
         * case 2: point distance 1500Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),1500.0,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DW:Point 1 dist: 1500km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));

        /*
         * case 3: point distance 1500000m (same request than 2 in meters)
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),1500000,"meters");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DW:Point 1 dist: 1500000m CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));

        /*
         * case 4: point distance 2000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),2000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DW:Point 1 dist: 2000km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));

        /*
         * case 5: point distance 4000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),4000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DW:Point 1 dist: 4000km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 3"));

        /*
         * case 6: point distance 5000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),5000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DW:Point 1 dist: 5000km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals("was:" + results, nbResults, 5);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 3"));
        assertTrue("was:" + results, results.contains("box 5"));

        /*
         * case 6: point distance 6000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),6000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DW:Point 1 dist: 6000km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 6);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 5"));

        /*
         * case 7: BBOX distance 5km
         */
        double min1[] = {-20, -20};
        double max1[] = { 20,  20};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(bbox), 5.0, "kilometers");
        SpatialQuery bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DW:BBOX 1 dist: 5km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));


        /*
         * case 8: BBOX distance 1500km
         */
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(bbox), 1500.0, "kilometers");
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DW:BBOX 1 dist: 1500km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));

        /*
         * case 9: BBOX distance 3000km
         */
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(bbox),3000.0, "kilometers");
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DW:BBOX 1 dist: 3000km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 6);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 5"));

        /*
         * case 10: a line distance 5km
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(-50, -45),
            new Coordinate(60, -43),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),5,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DW:Line 1 dist: 5km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 1"));

         /*
         * case 11: a line distance 4000km
         *
         */
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),4000.0, "kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DW:Line 1 dist: 4000km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 1"));
//        assertTrue(results.contains("box 3"));
//        assertTrue(results.contains("box 5"));

        /*
         * case 12: a line distance 5000km
         *
         */
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),5000.0, "kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));



        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DW:Line 1 dist: 5000km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
//        assertTrue(results.contains("point 2"));   //touches are not considered within
//        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 1"));
//        assertTrue(results.contains("box 3"));
//        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));
//        assertTrue(results.contains("line 1"));
//        assertTrue(results.contains("line 1 projected"));

        /*
         * case 12: a line distance 6000km
         *
         */
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom), 6000.0, "kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "DW:Line 1 dist: 6000km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));
//        assertTrue(results.contains("box 2"));
//        assertTrue(results.contains("box 2 projected"));

    }

    /**
     * Test the Distance spatial filter BEYOND.
     * @throws java.lang.Exception
     */
    @Test
    public void beyondTest() throws Exception {

        /*
         * case 1: point distance 5Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),5,"kilometers");
        SpatialQuery spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        Set<String> results = searcher.doSearch(spatialQuery);

        int nbResults = results.size();
        LOGGER.log(Level.FINER, "BY:Point 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 6);
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));

        /*
         * case 2: point distance 1500Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),1500,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "BY:Point 1 dist: 1500km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));

        assertEquals("was:" + results, nbResults, 4);

        /*
         * case 3: point distance 1500000m
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),1500000,"meters");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "BY:Point 1 dist: 1500000m CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));


        /*
         * case 4: point distance 2000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),2000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "BY:Point 1 dist: 2000km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 5"));

        /*
         * case 5: point distance 4000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),4000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "BY:Point 1 dist: 4000km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 5"));

        /*
         * case 6: point distance 5000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),5000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "BY:Point 1 dist: 5000km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 1"));

        /*
         * case 7: point distance 6000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),6000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "BY:Point 1 dist: 6000km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);

        /*
         * case 8: BBOX distance 5km
         */
        double min1[] = {-20, -20};
        double max1[] = { 20,  20};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.beyond(GEOMETRY_PROPERTY,FF.literal(bbox), 5.0, "kilometers");
        SpatialQuery bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "BY:BBOX 1 dist: 5km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 5"));

        /*
         * case 8: BBOX distance 1500km
         */
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.beyond(GEOMETRY_PROPERTY,FF.literal(bbox),1500.0, "kilometers");
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "BY:BBOX 1 dist: 1500km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 5"));

        /*
         * case 9: BBOX distance 3000km
         */
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.beyond(GEOMETRY_PROPERTY,FF.literal(bbox),3000.0, "kilometers");
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "BY:BBOX 1 dist: 3000km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);

         /*
         * case 10: a line distance 5km
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(-50, -45),
            new Coordinate(60, -43),
        });
        JTS.setCRS(geom, CommonCRS.defaultGeographic());
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),5,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "BY:Line 1 dist: 5km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 5);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));

        /*
         * case 11: a line distance 4000km
         *
         */
        filter = FF.beyond(GEOMETRY_PROPERTY,FF.literal(geom), 4000.0, "kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(spatialQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "BY:Line 1 dist: 4000km CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 5);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        //issue: this box as tha same y value than box 3
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));

//        /*
//         * case 12: a line distance 5000km
//         *
//         */
//        filter = FF.beyond(GEOMETRY_PROPERTY,FF.literal(geom), 5000.0, "kilometers");
//        spatialQuery = new SpatialQuery(wrap(filter));
//
//        //we perform a lucene query
//        results = searcher.doSearch(spatialQuery);
//
//        nbResults = results.size();
//        logger.finer("BY:Line 1 dist: 5000km CRS=4326: nb Results: " + nbResults);
//
//        results = new ArrayList<String>();
//        for (int i = 0; i < nbResults; i++) {
//            Document doc = searcher.doc(docs.scoreDocs[i].doc);
//            String name =  doc.get("id");
//            results.add(name);
//            LOGGER.log(Level.FINER, "\tid: {0}", name);
//        }
//
//        //we verify that we obtain the correct results.
//        assertEquals(nbResults, 6);
//        assertTrue(results.contains("point 1"));
//        assertTrue(results.contains("point 1 projected"));
//      assertTrue(results.contains("point 4"));
//        assertTrue(results.contains("point 5"));
//        assertTrue(results.contains("box 2"));
//      assertTrue(results.contains("box 2 projected"));
//
//        /*
//         * case 13: a line distance 6000km
//         *
//         */
//        filter = FF.beyond(GEOMETRY_PROPERTY,FF.literal(geom), 6000.0, "kilometers");
//        spatialQuery = new SpatialQuery(wrap(filter));
//
//        //we perform a lucene query
//        results = searcher.doSearch(spatialQuery);
//
//        nbResults = results.size();
//        logger.finer("BY:Line 1 dist: 6000km CRS=4326: nb Results: " + nbResults);
//
//        results = new ArrayList<String>();
//        for (int i = 0; i < nbResults; i++) {
//            Document doc = searcher.doc(docs.scoreDocs[i].doc);
//            String name =  doc.get("id");
//            results.add(name);
//            LOGGER.log(Level.FINER, "\tid: {0}", name);
//        }
//
//        //we verify that we obtain the correct results.
//        assertEquals(nbResults, 1);
//  assertTrue(results.contains("point 5"));


    }

    /**
     * Test the combinated spatial filter.
     */
    @Test
    public void overlapsTest() throws Exception {
        /*
         * case 1: bbox.
         */
        double min1[] = {-20, -20};
        double max1[] = { 20,  20};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.overlaps(GEOMETRY_PROPERTY, FF.literal(bbox));
        SpatialQuery bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        Set<String> results = searcher.doSearch(bboxQuery);

        int nbResults = results.size();
        LOGGER.log(Level.FINER, "OL:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 4"));

        /*
         * case 2: another bbox.
         */
        double min2[] = {-20, -20};
        double max2[] = {  7,  20};
        bbox = new GeneralEnvelope(min2, max2);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        filter = FF.overlaps(GEOMETRY_PROPERTY, FF.literal(bbox));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "OL:BBOX 2 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 2"));
    assertTrue(results.contains("box 2 projected"));

    }

    /**
     * Test the combination of a String query and/or spatial filter.
     * @throws java.lang.Exception
     */
    @Test
    public void QueryAndSpatialFilterTest() throws Exception {

        /*
         * case 1: a normal spatial request BBOX
         */
        double min1[] = {-20, -20};
        double max1[] = { 20,  20};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        org.opengis.filter.Filter bboxFilter = FF.bbox(GEOMETRY_PROPERTY, -20, -20, 20, 20, "CRS:84");
        SpatialQuery bboxQuery = new SpatialQuery(wrap(bboxFilter));

        //we perform a lucene query
        Set<String> results = searcher.doSearch(bboxQuery);

        int nbResults = results.size();
        LOGGER.log(Level.FINER, "QnS:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));

        /*
         *  case 2: same filter with a StringQuery
         */

        //we perform a lucene query
        SpatialQuery sQuery = new SpatialQuery("id:point*", bboxQuery.getQuery(), LogicalFilterType.AND);

        results = searcher.doSearch(sQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "QnS: title like point* AND BBOX 1: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results
        assertEquals(nbResults, 0);

        /*
         *  case 3: same filter same query but with an OR
         */

        //we perform two lucene query
        sQuery = new SpatialQuery("id:point*");
        Set<String> hits1 = searcher.doSearch(sQuery);
        Set<String> hits2 = searcher.doSearch(bboxQuery);


        results = new HashSet<>();
        results.addAll(hits1);
        results.addAll(hits2);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "QnS: name like point* OR BBOX 1: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));

        /*
         *  case 4: two filter two query with an OR in the middle
         *          (BBOX and name like point*) OR (INTERSECT line1 and name like box*)
         */

        //we perform two lucene query
        Geometry geom1 = GF.createLineString(new Coordinate[]{
            new Coordinate(40, 30),
            new Coordinate(40, -30),
        });
        JTS.setCRS(geom1, CommonCRS.defaultGeographic());
        filter = FF.intersects(GEOMETRY_PROPERTY, FF.literal(geom1));
        SpatialQuery interQuery = new SpatialQuery(wrap(filter));

        SpatialQuery query1     = new SpatialQuery("id:point*", bboxQuery.getQuery(), LogicalFilterType.AND);
        SpatialQuery query2     = new SpatialQuery("id:box*", interQuery.getQuery(),  LogicalFilterType.AND);

        hits1 = searcher.doSearch(query1);
        hits2 = searcher.doSearch(query2);

        results      = new HashSet<>();
        results.addAll(hits1);
        results.addAll(hits2);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "QnS: (name like point* AND BBOX 1) OR (name like box* AND INTERSECT line 1): nb Results: {0}", nbResults);

        //we verify that we obtain the correct results
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 3"));
    }


    /**
     * Test the combination of a String query and/or spatial filter.
     * @throws java.lang.Exception
     */
    @Test
    public void QueryAndSpatialFilterAfterRemoveTest() throws Exception {

        org.opengis.filter.Filter bboxFilter = FF.bbox(GEOMETRY_PROPERTY, -20, -20, 20, 20, "CRS:84");
        SpatialQuery bboxQuery = new SpatialQuery(wrap(bboxFilter));

        Set<String> results = searcher.doSearch(bboxQuery);

        int nbResults = results.size();
        LOGGER.log(Level.FINER, "QnS:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));

        /*
         * we remove a document
         */
        final Analyzer analyzer = new StandardAnalyzer();
        DocumentIndexer indexer = new DocumentIndexer(directory, null, analyzer);
        indexer.removeDocument("box 2 projected");
        indexer.destroy();
        searcher.destroy();

        searcher = new LuceneIndexSearcher(directory, null, new ClassicAnalyzer(), true);

        //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "QnS:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 2"));


        // re-add the document

        final CoordinateReferenceSystem CRS3395 = CRS.forCode("EPSG:3395");
        Document doc = new Document();
        doc.add(new StringField("id", "box 2 projected", Field.Store.YES));
        doc.add(new StringField("docid", 66 + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc", Field.Store.YES));
        NamedEnvelope env = addBoundingBox(doc,             556597.4539663679,  1113194.9079327357,  1111475.1028522244, 1678147.5163917788, CRS3395);
        envelopes.put("box 2 projected", env); // attention !! reprojeté

        indexer = new DocumentIndexer(directory, null, analyzer);
        indexer.indexDocument(new DocumentEnvelope(doc, env));
        indexer.destroy();
        searcher.destroy();

        searcher = new LuceneIndexSearcher(directory, null, new StandardAnalyzer(), true);

         //we perform a lucene query
        results = searcher.doSearch(bboxQuery);

        nbResults = results.size();
        LOGGER.log(Level.FINER, "QnS:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        //we verify that we obtain the correct results
        assertEquals(nbResults,  3);
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
    }

    private static List<DocumentEnvelope> fillTestData() throws Exception {

        final List<DocumentEnvelope> docs = new ArrayList<>();
        final CoordinateReferenceSystem CRS3395 = CRS.forCode("EPSG:3395");

        Document doc = new Document();
        doc.add(new StringField("id", "box 1", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc", Field.Store.YES));
        NamedEnvelope env = addBoundingBox(doc,           -40,                -25,           -50,               -40, CommonCRS.defaultGeographic());
        envelopes.put("box 1", env);
        docs.add(new DocumentEnvelope(doc, env));

        doc = new Document();
        doc.add(new StringField("id", "box 2", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc", Field.Store.YES));
        env = addBoundingBox(doc,             5,                 10,            10,                15, CommonCRS.defaultGeographic());
        envelopes.put("box 2", env);
        docs.add(new DocumentEnvelope(doc, env));

        doc = new Document();
        doc.add(new StringField("id", "box 2 projected", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc", Field.Store.YES));
        env = addBoundingBox(doc,             556597.4539663679,  1113194.9079327357,  1111475.1028522244, 1678147.5163917788, CRS3395);
        envelopes.put("box 2 projected", env); // attention !! reprojeté
        docs.add(new DocumentEnvelope(doc, env));

        doc = new Document();
        doc.add(new StringField("id", "box 3", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc", Field.Store.YES));
        env = addBoundingBox(doc,            30,                 50,             0,                15, CommonCRS.defaultGeographic());
        envelopes.put("box 3", env);
        docs.add(new DocumentEnvelope(doc, env));

        doc = new Document();
        doc.add(new StringField("id", "box 4", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc", Field.Store.YES));
        env = addBoundingBox(doc,           -30,                -15,             0,                10, CommonCRS.defaultGeographic());
        envelopes.put("box 4", env);
        docs.add(new DocumentEnvelope(doc, env));

        doc = new Document();
        doc.add(new StringField("id", "box 5", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc", Field.Store.YES));
        env = addBoundingBox(doc,        44.792,             51.126,        -6.171,             -2.28, CommonCRS.defaultGeographic());
        envelopes.put("box 5", env);
        docs.add(new DocumentEnvelope(doc, env));

        return docs;
    }

    /**
     * Add a boundingBox geometry to the specified Document.
     *
     * @param doc  The document to add the geometry
     * @param minx the minimun X coordinate of the bounding box.
     * @param maxx the maximum X coordinate of the bounding box.
     * @param miny the minimun Y coordinate of the bounding box.
     * @param maxy the maximum Y coordinate of the bounding box.
     * @param crsName The coordinate reference system in witch the coordinates are expressed.
     */
    private static NamedEnvelope addBoundingBox(final Document doc, final double minx, final double maxx, final double miny, final double maxy, final CoordinateReferenceSystem crs) throws FactoryException, TransformException {

        final Geometry poly = LuceneUtils.getPolygon(minx, maxx, miny, maxy, crs);
        final String id = doc.get("id");
        final NamedEnvelope namedBound = LuceneUtils.getNamedEnvelope(id, poly, treeCrs);

        doc.add(new StoredField(LuceneOGCSpatialQuery.GEOMETRY_FIELD_NAME,WKBUtils.toWKBwithSRID(poly)));
        return namedBound;
    }
}

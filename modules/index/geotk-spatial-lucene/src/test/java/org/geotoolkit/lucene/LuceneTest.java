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

import java.util.logging.Level;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.io.File;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import org.geotoolkit.filter.DefaultFilterFactory2;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.geometry.jts.SRIDGenerator.Version;
import org.geotoolkit.index.tree.manager.NamedEnvelope;
import org.geotoolkit.io.wkb.WKBUtils;
import org.geotoolkit.lucene.DocumentIndexer.DocumentEnvelope;
import org.geotoolkit.lucene.analysis.standard.ClassicAnalyzer;
import org.geotoolkit.lucene.filter.LuceneOGCFilter;
import org.geotoolkit.lucene.filter.SerialChainFilter;
import org.geotoolkit.lucene.filter.SpatialQuery;
import org.geotoolkit.referencing.CRS;
import static org.geotoolkit.lucene.filter.LuceneOGCFilter.*;
import org.geotoolkit.util.FileUtilities;

import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * A Test classes testing the different spatial filters.
 *
 * @author Guilhem Legal
 * @module pending
 */
public class LuceneTest {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final Logger LOGGER = Logger.getLogger("org.constellation.lucene");
    private static final FilterFactory2 FF = new DefaultFilterFactory2();
    private static final CoordinateReferenceSystem WGS84;
    private static final double TOLERANCE = 0.001;
    
    static{
        try {
            WGS84 = CRS.decode("CRS:84");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static final File directory = new File("luceneTest");
    private static IndexSearcher searcher;
    private static Query simpleQuery;
    private org.opengis.filter.Filter filter;
    private Geometry geom;



    @BeforeClass
    public static void setUpMethod() throws Exception {
        if (directory.exists()) {
            FileUtilities.deleteDirectory(directory.toPath());
        }
        directory.mkdir();
        
        final Analyzer analyzer = new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_4_9);
        final DocumentIndexer indexer = new DocumentIndexer(directory, fillTestData(), analyzer);
        indexer.createIndex();
        indexer.destroy();

        IndexReader reader = DirectoryReader.open(LuceneUtils.getAppropriateDirectory(directory.listFiles()[0]));
        searcher = new IndexSearcher(reader);
        //create a term query to search against all documents
        simpleQuery = new TermQuery(new Term("metafile", "doc"));
    }

    @AfterClass
    public static void tearDownMethod() throws Exception {
        FileUtilities.deleteDirectory(directory.toPath());
    }


    /**
     * Test the spatial filter BBOX.
     */
    @Test
    public void BBOXTest() throws Exception {

        /*
         * first bbox
         */
        org.opengis.filter.Filter spaFilter = FF.bbox(GEOMETRY_PROPERTY, -20,-20,20,20,"CRS:84");
        SpatialQuery bboxQuery = new SpatialQuery(wrap(spaFilter));

        //we perform a lucene query
        TopDocs docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        int nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BBOX:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results
        assertEquals(nbResults, 10);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));

        /*
         * The same box in a diferent crs
         */
        spaFilter = FF.bbox(GEOMETRY_PROPERTY,
                -2226389.8158654715, -2258423.6490963786,
                2226389.8158654715, 2258423.6490963805,
                "EPSG:3395");
        bboxQuery = new SpatialQuery(wrap(spaFilter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BBOX:BBOX 1 CRS= 3395: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results
        assertEquals(nbResults, 10);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));

        /*
         * second bbox
         */
        spaFilter = FF.bbox(GEOMETRY_PROPERTY, -5, -5, 60, 60, "CRS:84");
        bboxQuery = new SpatialQuery(wrap(spaFilter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BBOX:BBOX 2 CRS= 4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

         //we verify that we obtain the correct results
        assertEquals(nbResults, 9);
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 5"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));

        /*
         * third bbox
         */
        spaFilter = FF.bbox(GEOMETRY_PROPERTY, 40, -9, 50, -5, "CRS:84");
        bboxQuery = new SpatialQuery(wrap(spaFilter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BBOX:BBOX 3 CRS= 4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
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
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.intersects(GEOMETRY_PROPERTY, FF.literal(bbox));
        SpatialQuery bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        TopDocs docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        int nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "INTER:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 10);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 2"  ));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"  ));
        assertTrue(results.contains("line 1" ));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2" ));

        /*
         * case 2: The same box in a diferent crs.
         */
        double min2[] = {-2226389.8158654715, -2258423.6490963786};
        double max2[] = { 2226389.8158654715,  2258423.6490963805};
        bbox = new GeneralEnvelope(min2, max2);
        bbox.setCoordinateReferenceSystem(CRS.decode("EPSG:3395", true));
        filter = FF.intersects(GEOMETRY_PROPERTY, FF.literal(bbox));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "INTER:BBOX 1 CRS= 3395: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results
        assertEquals(nbResults, 10);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 2"  ));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"  ));
        assertTrue(results.contains("line 1" ));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2" ));

        /*
         * case 3: line
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(7, 30),
            new Coordinate(7, -30),
        });
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.intersects(GEOMETRY_PROPERTY,FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "INTER:Line 1 CRS=4326: nb Results: {0}", nbResults);



        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }


        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("line 1" ));
        assertTrue(results.contains("line 1 projected"));

        /*
         * case 4: same line diferent CRS
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(775978.5043848383, 3339584.723798207),
            new Coordinate(775978.5043848383, -3339584.723798207),
        });
        geom.setSRID(SRIDGenerator.toSRID(CRS.decode("EPSG:3395"), Version.V1));
        filter = FF.intersects(GEOMETRY_PROPERTY, FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "INTER:Line 1 CRS=3395: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
        assertTrue(results.contains("box 2"  ));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("line 1" ));
        assertTrue(results.contains("line 1 projected"));

        /*
         * case 5: another line
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(40, 40),
            new Coordinate(40, -30),
        });
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.intersects(GEOMETRY_PROPERTY, FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "INTER:Line 2 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 3"  ));
        assertTrue(results.contains("point 4"));

        /*
         * case 6: same line another CRS
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(4452779.631730943, 4838471.398061137),
            new Coordinate(4452779.631730943, -3339584.723798207),
        });
        geom.setSRID(SRIDGenerator.toSRID(CRS.decode("EPSG:3395"), Version.V1));
        filter = FF.intersects(GEOMETRY_PROPERTY, FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "INTER:Line 2 CRS=3395: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 3"  ));
        assertTrue(results.contains("point 4"));
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
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.equal(GEOMETRY_PROPERTY, FF.literal(bbox));
        SpatialQuery bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        TopDocs docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        int nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "EQ:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

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
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.equal(GEOMETRY_PROPERTY, FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "EQ:Line 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("line 1" ));

        //TODO  issue here the projected line does not have the exact same coordinates (this issue happen for all geometry in Equals)
        //assertTrue(results.contains("line 1 projected"));

        /*
         * case 3: point
         */
        geom = GF.createPoint(new Coordinate(-10, 10));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.equal(GEOMETRY_PROPERTY, FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "EQ:Point 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

         //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("point 1" ));
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
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.contains(GEOMETRY_PROPERTY, FF.literal(bbox));
        SpatialQuery bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        TopDocs docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        int nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "CT:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

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
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.contains(GEOMETRY_PROPERTY, FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "CT:Line 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 4"));

        /*
         * case 3: BOX/point
         */
        geom = GF.createPoint(new Coordinate(-25, 5));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.contains(GEOMETRY_PROPERTY, FF.literal(geom));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "CT:Point 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 4"));

        /*
         * case 4: Line/point
         */
        geom = GF.createPoint(new Coordinate(20, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),0.00001,"m");
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "CT:Point 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }


        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));

        /*
         * case 5: Line/Line
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(20, 0),
            new Coordinate(15, 0),
        });
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),TOLERANCE,"m");
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "CT:Line 2 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
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
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.disjoint(GEOMETRY_PROPERTY, FF.literal(geom));
        SpatialQuery spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        TopDocs docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        int nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DJ:Point 1 CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 14);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 5"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));

        /*
         * case 2: another point intersecting with the two registered lines.
         *  (equals to point 3)
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.disjoint(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DJ:Point 2 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 11);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));
        //since there is no more precision errors this geometry should not be present
        assertFalse(results.contains("line 1 projected"));

        /*
         * case 3: a line
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(-40, 0),
            new Coordinate(30, 0),
        });
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.disjoint(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DJ:Line 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 8);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 5"));
        //since there is no more precision errors this geometry should not be present
        assertFalse(results.contains("line 1 projected"));

        /*
         * case 4: another line
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(7, 40),
            new Coordinate(7, -20),
        });
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.disjoint(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DJ:Line 2 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 11);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));
        assertTrue(results.contains("line 2"));

        /*
         * case 5: a BBOX
         *
         */
        double min1[] = {-20, -20};
        double max1[] = { 20,  20};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.disjoint(GEOMETRY_PROPERTY, FF.literal(bbox));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DJ:BBox 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 5);
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
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
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.disjoint(GEOMETRY_PROPERTY,FF.literal(bbox));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DJ:BBox 2 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 9);
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 5"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));


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
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        SpatialQuery spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        TopDocs docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        int nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "TO:Point 1 CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 3);
//        assertTrue(results.contains("point 3")); //it overlaps
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected")); // match because precision errors have been corrected
        assertTrue(results.contains("line 2"));

        /*
         * case 2: another point
         *
         */
        geom = GF.createPoint(new Coordinate(-30, 5));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "TO:Point 2 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 4"));

        /*
         * case 3: another point
         *
         */
        geom = GF.createPoint(new Coordinate(-25, -50));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "TO:Point 3 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 1"));

        /*
         * case 4: another point
         *
         */
        geom = GF.createPoint(new Coordinate(0, -10));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));


        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "TO:Point 4 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }



        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);
//        assertTrue(results.contains("line 2")); //point intersect or in within, it is not consider "touches" in jts

        /*
         * case 5: another point
         *
         */
        geom = GF.createPoint(new Coordinate(40, 20));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "TO:Point 5 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

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
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "TO:Line 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected")); // match because precision errors have been corrected

        /*
         * case 7: another line
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(-15, 3),
            new Coordinate(30, 4),
        });
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "TO:Line 2 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));

         /*
         * case 8: another line
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(0, 0),
            new Coordinate(-40, -40),
        });
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "TO:Line 2 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 5);
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));  // match because precision errors have been corrected
        assertTrue(results.contains("line 2"));

        /*
         * case 9: a BBOX
         *
         */
        double min1[] = {-15,   0};
        double max1[] = { 30,  50};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.touches(GEOMETRY_PROPERTY, FF.literal(bbox));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "TO:BBox 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 7);
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 3"  ));
        assertTrue(results.contains("box 4"  ));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));// match because precision errors have been corrected
        assertTrue(results.contains("line 2"));
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
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.within(GEOMETRY_PROPERTY, FF.literal(bbox));
        SpatialQuery bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        TopDocs docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        int nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "WT:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 7);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("line 2"));

        /*
         * case 2: another BBOX.
         */
        double min2[] = {  3,   5};
        double max2[] = { 55,  50};
        bbox = new GeneralEnvelope(min2, max2);
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.within(GEOMETRY_PROPERTY, FF.literal(bbox));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "WT:BBOX 2 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("point 4"));

        /*
         * case 6: a line
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(-40, 30),
            new Coordinate(40, 20),
        });
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.within(GEOMETRY_PROPERTY, FF.literal(geom));
        SpatialQuery spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "WT:Line 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

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
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.crosses(GEOMETRY_PROPERTY, FF.literal(geom));
        SpatialQuery spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        TopDocs docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        int nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "CR:Line 1 CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

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
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.crosses(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "CR:Line 2 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));

        /*
         * case 3: another line
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(-25, 5),
            new Coordinate(-35, -45),
        });
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.crosses(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "CR:Line 2 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 1"));

        /*
         * case 4: point (equals to point 3)
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.crosses(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "CR:Point 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

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
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.crosses(GEOMETRY_PROPERTY, FF.literal(geom));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "CR:Point 2 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

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
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.crosses(GEOMETRY_PROPERTY, FF.literal(bbox));
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "CR:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
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
        bbox.setCoordinateReferenceSystem(WGS84);
        org.opengis.filter.Filter filter1 = FF.touches(GEOMETRY_PROPERTY, FF.literal(bbox));
        org.opengis.filter.Filter filter2 = FF.bbox(GEOMETRY_PROPERTY, 25,-10,60,50,"CRS:84");
        SpatialQuery spatialQuery1 = new SpatialQuery(wrap(filter1));
        SpatialQuery spatialQuery2 = new SpatialQuery(wrap(filter2));

        List<Filter> filters  = new ArrayList<>();
        filters.add(spatialQuery1.getSpatialFilter());
        filters.add(spatialQuery2.getSpatialFilter());
        int filterType[]  = {SerialChainFilter.OR};
        SerialChainFilter serialFilter = new SerialChainFilter(filters, filterType);



        //we perform a lucene query
        TopDocs docs = searcher.search(simpleQuery, serialFilter, 15);

        int nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "TO || BBOX: BBox 1 CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 5);
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("box 3"  ));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("box 5"));

        // TODO add precision
        //assertTrue(results.contains("line 1 projected"));

        /*
         * case 2: same test with AND instead of OR
         *
         */
        int filterType2[]  = {SerialChainFilter.AND};
        serialFilter = new SerialChainFilter(filters, filterType2);

        //we perform a lucene query
        docs = searcher.search(simpleQuery, serialFilter, 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "TO && BBOX: BBox 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("line 1"));

        /*
         * case 3: NOT INTERSECT line1
         *
         */
        geom = GF.createLineString(new Coordinate[]{
            new Coordinate(7, 40),
            new Coordinate(6, -40),
        });
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.intersects(GEOMETRY_PROPERTY, FF.literal(geom));
        SpatialQuery spatialQuery = new SpatialQuery(wrap(filter));
        List<Filter> filters3     = new ArrayList<>();
        filters3.add(spatialQuery.getSpatialFilter());
        int filterType3[]         = {SerialChainFilter.NOT};
        serialFilter              = new SerialChainFilter(filters3, filterType3);

        //we perform a lucene query
        docs = searcher.search(simpleQuery, serialFilter, 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "NOT INTER:Line 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 11);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));
        assertTrue(results.contains("line 2"));


        /*
         * case 4: INTERSECT line AND BBOX
         *
         */
        double min2[]          = {-12, -17};
        double max2[]          = { 15,  50};
        GeneralEnvelope bbox2  = new GeneralEnvelope(min2, max2);
        bbox2.setCoordinateReferenceSystem(WGS84);
        org.opengis.filter.Filter bfilter = FF.bbox(GEOMETRY_PROPERTY, -12,-17,15,50,"CRS:84");
        SpatialQuery bboxQuery = new SpatialQuery(wrap(bfilter));
        List<Filter> filters4  = new ArrayList<>();
        filters4.add(spatialQuery.getSpatialFilter());
        filters4.add(bboxQuery.getSpatialFilter());
        int filterType4[]         = {SerialChainFilter.AND};
        serialFilter              = new SerialChainFilter(filters4, filterType4);

        //we perform a lucene query
        docs = searcher.search(simpleQuery, serialFilter, 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "NOT INTER:Line 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
	assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));

        /*
         * case 5: INTERSECT line AND NOT BBOX
         *
         */
        int filterType5[] = {SerialChainFilter.AND, SerialChainFilter.NOT};
        serialFilter      = new SerialChainFilter(filters4, filterType5);

        //we perform a lucene query
        docs = searcher.search(simpleQuery, serialFilter, 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "NOT INTER:Line 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

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
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),5.0,"kilometers");
        SpatialQuery spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        TopDocs docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        int nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DW:Point 1 dist: 5km CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));

        /*
         * case 2: point distance 1500Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),1500.0,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DW:Point 1 dist: 1500km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 7);
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));

        /*
         * case 3: point distance 1500000m (same request than 2 in meters)
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),1500000,"meters");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DW:Point 1 dist: 1500000m CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 7);
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));

        /*
         * case 4: point distance 2000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),2000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DW:Point 1 dist: 2000km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 10);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));

        /*
         * case 5: point distance 4000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),4000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DW:Point 1 dist: 4000km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 11);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));
        assertTrue(results.contains("box 3"));

        /*
         * case 6: point distance 5000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),5000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DW:Point 1 dist: 5000km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 13);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 5"));

        /*
         * case 6: point distance 6000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),6000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DW:Point 1 dist: 6000km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 15);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 5"));

        /*
         * case 7: BBOX distance 5km
         */
        double min1[] = {-20, -20};
        double max1[] = { 20,  20};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(bbox), 5.0, "kilometers");
        SpatialQuery bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DW:BBOX 1 dist: 5km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 10);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));


        /*
         * case 8: BBOX distance 1500km
         */
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(bbox), 1500.0, "kilometers");
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DW:BBOX 1 dist: 1500km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 11);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));

        /*
         * case 9: BBOX distance 3000km
         */
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(bbox),3000.0, "kilometers");
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DW:BBOX 1 dist: 3000km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 15);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
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
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),5,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DW:Line 1 dist: 5km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

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
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DW:Line 1 dist: 4000km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("box 1"));
//        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("line 2"));
//        assertTrue(results.contains("box 5"));

        /*
         * case 12: a line distance 5000km
         *
         */
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom),5000.0, "kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));



        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DW:Line 1 dist: 5000km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }


        //we verify that we obtain the correct results.
        assertEquals(nbResults, 3);
//        assertTrue(results.contains("point 2"));   //touches are not considered within
//        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 1"));
//        assertTrue(results.contains("box 3"));
//        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));
        assertTrue(results.contains("line 2"));
//        assertTrue(results.contains("line 1"));
//        assertTrue(results.contains("line 1 projected"));

        /*
         * case 12: a line distance 6000km
         *
         */
        filter = FF.dwithin(GEOMETRY_PROPERTY, FF.literal(geom), 6000.0, "kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "DW:Line 1 dist: 6000km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 9);
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));
        assertTrue(results.contains("line 2"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
//        assertTrue(results.contains("point 1"));
//        assertTrue(results.contains("point 1 projected"));
//        assertTrue(results.contains("point 4"));
//        assertTrue(results.contains("box 2"));
//        assertTrue(results.contains("box 2 projected"));

    }

    /**
     * Test the Distance spatial filter BEYOND.
     */
    @Test
    public void beyondTest() throws Exception {

        /*
         * case 1: point distance 5Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),5,"kilometers");
        SpatialQuery spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        TopDocs docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        int nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BY:Point 1 CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 11);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
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
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),1500,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BY:Point 1 dist: 1500km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));

        assertEquals(nbResults, 8);

        /*
         * case 3: point distance 1500000m
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),1500000,"meters");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BY:Point 1 dist: 1500000m CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 8);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));


        /*
         * case 4: point distance 2000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),2000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BY:Point 1 dist: 2000km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 5);
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 5"));

        /*
         * case 5: point distance 4000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),4000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BY:Point 1 dist: 4000km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 5"));

        /*
         * case 6: point distance 5000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),5000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BY:Point 1 dist: 5000km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 2);
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 1"));

        /*
         * case 7: point distance 6000Km
         *
         */
        geom = GF.createPoint(new Coordinate(0, 0));
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),6000,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BY:Point 1 dist: 6000km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 0);

        /*
         * case 8: BBOX distance 5km
         */
        double min1[] = {-20, -20};
        double max1[] = { 20,  20};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.beyond(GEOMETRY_PROPERTY,FF.literal(bbox), 5.0, "kilometers");
        SpatialQuery bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BY:BBOX 1 dist: 5km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 5);
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 5"));

        /*
         * case 8: BBOX distance 1500km
         */
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.beyond(GEOMETRY_PROPERTY,FF.literal(bbox),1500.0, "kilometers");
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BY:BBOX 1 dist: 1500km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 4);
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 1"));
        assertTrue(results.contains("box 5"));

        /*
         * case 9: BBOX distance 3000km
         */
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.beyond(GEOMETRY_PROPERTY,FF.literal(bbox),3000.0, "kilometers");
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BY:BBOX 1 dist: 3000km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

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
        geom.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.beyond(GEOMETRY_PROPERTY, FF.literal(geom),5,"kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BY:Line 1 dist: 5km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 14);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 2"));

        /*
         * case 11: a line distance 4000km
         *
         */
        filter = FF.beyond(GEOMETRY_PROPERTY,FF.literal(geom), 4000.0, "kilometers");
        spatialQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "BY:Line 1 dist: 4000km CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 13);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        //issue: this box as tha same y value than box 3
        assertTrue(results.contains("box 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 5"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));

//        /*
//         * case 12: a line distance 5000km
//         *
//         */
//        filter = FF.beyond(GEOMETRY_PROPERTY,FF.literal(geom), 5000.0, "kilometers");
//        spatialQuery = new SpatialQuery(wrap(filter));
//
//        //we perform a lucene query
//        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);
//
//        nbResults = docs.totalHits;
//        logger.finer("BY:Line 1 dist: 5000km CRS=4326: nb Results: " + nbResults);
//
//        results = new ArrayList<>();
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
//    	assertTrue(results.contains("point 4"));
//        assertTrue(results.contains("point 5"));
//        assertTrue(results.contains("box 2"));
//    	assertTrue(results.contains("box 2 projected"));
//
//        /*
//         * case 13: a line distance 6000km
//         *
//         */
//        filter = FF.beyond(GEOMETRY_PROPERTY,FF.literal(geom), 6000.0, "kilometers");
//        spatialQuery = new SpatialQuery(wrap(filter));
//
//        //we perform a lucene query
//        docs = searcher.search(simpleQuery, spatialQuery.getSpatialFilter(), 15);
//
//        nbResults = docs.totalHits;
//        logger.finer("BY:Line 1 dist: 6000km CRS=4326: nb Results: " + nbResults);
//
//        results = new ArrayList<>();
//        for (int i = 0; i < nbResults; i++) {
//            Document doc = searcher.doc(docs.scoreDocs[i].doc);
//            String name =  doc.get("id");
//            results.add(name);
//            LOGGER.log(Level.FINER, "\tid: {0}", name);
//        }
//
//        //we verify that we obtain the correct results.
//        assertEquals(nbResults, 1);
//	assertTrue(results.contains("point 5"));


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
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.overlaps(GEOMETRY_PROPERTY, FF.literal(bbox));
        SpatialQuery bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        TopDocs docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        int nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "OL:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 1);
        assertTrue(results.contains("box 4"));

        /*
         * case 2: another bbox.
         */
        double min2[] = {-20, -20};
        double max2[] = {  7,  20};
        bbox = new GeneralEnvelope(min2, max2);
        bbox.setCoordinateReferenceSystem(WGS84);
        filter = FF.overlaps(GEOMETRY_PROPERTY, FF.literal(bbox));
        bboxQuery = new SpatialQuery(wrap(filter));

        //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "OL:BBOX 2 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results.
        assertEquals(nbResults, 3);
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 2"));
	assertTrue(results.contains("box 2 projected"));

    }

    /**
     * Test the combination of a String query and/or spatial filter.
     */
    @Test
    public void QueryAndSpatialFilterTest() throws Exception {

        /*
         * case 1: a normal spatial request BBOX
         */
        double min1[] = {-20, -20};
        double max1[] = { 20,  20};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(WGS84);
        org.opengis.filter.Filter bboxFilter = FF.bbox(GEOMETRY_PROPERTY, -20, -20, 20, 20, "CRS:84");
        SpatialQuery bboxQuery = new SpatialQuery(wrap(bboxFilter));

        //we perform a lucene query
        TopDocs docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        int nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "QnS:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results
        assertEquals(nbResults, 10);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("line 2"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));

        /*
         *  case 2: same filter with a StringQuery
         */

        //we perform a lucene query
        Analyzer analyzer   = new ClassicAnalyzer(org.apache.lucene.util.Version.LUCENE_4_9);
        QueryParser parser  = new QueryParser(org.apache.lucene.util.Version.LUCENE_4_9, "metafile", analyzer);
        Query query         = parser.parse("id:point*");

        docs = searcher.search(query, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "QnS: title like point* AND BBOX 1: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results
        assertEquals(nbResults, 4);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));

        /*
         *  case 3: same filter same query but with an OR
         */

        //we perform two lucene query
        analyzer      = new ClassicAnalyzer(org.apache.lucene.util.Version.LUCENE_4_9);
        parser        = new QueryParser(org.apache.lucene.util.Version.LUCENE_4_9, "metafile", analyzer);
        query         = parser.parse("id:point*");

        TopDocs hits1 = searcher.search(query, 15);
        TopDocs hits2 = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);


        results = new ArrayList<>();
        StringBuilder resultString = new StringBuilder();
        for (int i = 0; i < hits1.totalHits; i++) {

            String name = searcher.doc(hits1.scoreDocs[i].doc).get("id");
            results.add(name);
            resultString.append('\t').append("id: ").append(name).append('\n');
        }
        for (int i = 0; i < hits2.totalHits; i++) {
            String name = searcher.doc(hits2.scoreDocs[i].doc).get("id");
            if (!results.contains(name)) {
                results.add(name);
                resultString.append('\t').append("id: ").append(name).append('\n');
            }
        }
        nbResults = results.size();
        LOGGER.log(Level.FINER, "QnS: name like point* OR BBOX 1: nb Results: {0}", nbResults);
        LOGGER.finer(resultString.toString());

        //we verify that we obtain the correct results
        assertEquals(nbResults, 12);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("point 4"));
        assertTrue(results.contains("point 5"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("line 2"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("line 1 projected"));
        assertTrue(results.contains("line 1"));

        /*
         *  case 4: two filter two query with an OR in the middle
         *          (BBOX and name like point*) OR (INTERSECT line1 and name like box*)
         */

        //we perform two lucene query
        analyzer                = new ClassicAnalyzer(org.apache.lucene.util.Version.LUCENE_4_9);
        parser                  = new QueryParser(org.apache.lucene.util.Version.LUCENE_4_9, "metafile", analyzer);
        Query query1            = parser.parse("id:point*");
        Query query2            = parser.parse("id:box*");

        Geometry geom1 = GF.createLineString(new Coordinate[]{
            new Coordinate(40, 30),
            new Coordinate(40, -30),
        });
        geom1.setSRID(SRIDGenerator.toSRID(WGS84, Version.V1));
        filter = FF.intersects(GEOMETRY_PROPERTY, FF.literal(geom1));
        SpatialQuery interQuery = new SpatialQuery(wrap(filter));

        hits1 = searcher.search(query1, bboxQuery.getSpatialFilter(), 15);
        hits2 = searcher.search(query2, interQuery.getSpatialFilter(), 15);

        results      = new ArrayList<>();
        resultString = new StringBuilder();
        for (int i = 0; i < hits1.totalHits; i++) {
            String name = searcher.doc(hits1.scoreDocs[i].doc).get("id");
            results.add(name);
            resultString.append('\t').append("id: ").append(name).append('\n');
        }
        for (int i = 0; i < hits2.totalHits; i++) {
            String name = searcher.doc(hits2.scoreDocs[i].doc).get("id");
            if (!results.contains(name)) {
                results.add(name);
                resultString.append('\t').append("id: ").append(name).append('\n');
            }
        }
        nbResults = results.size();
        LOGGER.log(Level.FINER, "QnS: (name like point* AND BBOX 1) OR (name like box* AND INTERSECT line 1): nb Results: {0}", nbResults);
        LOGGER.finer(resultString.toString());

        //we verify that we obtain the correct results
        assertEquals(nbResults, 5);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 3"));
    }


    /**
     * Test the combination of a String query and/or spatial filter.
     */
    @Test
    public void QueryAndSpatialFilterAfterRemoveTest() throws Exception {

        // we remove a document
        final Analyzer analyzer = new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_4_9);
        DocumentIndexer indexer = new DocumentIndexer(directory, null, analyzer);
        indexer.removeDocument("box 2 projected");
        indexer.destroy();

        IndexReader reader = DirectoryReader.open(LuceneUtils.getAppropriateDirectory(directory.listFiles()[0]));
        searcher = new IndexSearcher(reader);

        /*
         * case 1: a normal spatial request BBOX
         */
        double min1[] = {-20, -20};
        double max1[] = { 20,  20};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        bbox.setCoordinateReferenceSystem(WGS84);
        org.opengis.filter.Filter bboxFilter = FF.bbox(GEOMETRY_PROPERTY, -20, -20, 20, 20, "CRS:84");
        SpatialQuery bboxQuery = new SpatialQuery(wrap(bboxFilter));

        //we perform a lucene query
        TopDocs docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        int nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "QnS:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results
        assertEquals(nbResults, 9);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("line 2"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));


        // re-add the document

        final int srid3395 = SRIDGenerator.toSRID(CRS.decode("EPSG:3395"), Version.V1);
        Document docu = new Document();
        docu.add(new StringField("id", "box 2 projected", Field.Store.YES));
        docu.add(new StringField("docid", 66 + "", Field.Store.YES));
        docu.add(new StringField("metafile", "doc",   Field.Store.YES));
        addBoundingBox(docu,             556597.4539663679,  1113194.9079327357,  1111475.1028522244, 1678147.5163917788, srid3395); // attention !! reprojeté

        indexer = new DocumentIndexer(directory, null, analyzer);
        indexer.indexDocument(new DocumentEnvelope(docu, null));
        indexer.destroy();

        reader = DirectoryReader.open(LuceneUtils.getAppropriateDirectory(directory.listFiles()[0]));
        searcher = new IndexSearcher(reader);


         //we perform a lucene query
        docs = searcher.search(simpleQuery, bboxQuery.getSpatialFilter(), 15);

        nbResults = docs.totalHits;
        LOGGER.log(Level.FINER, "QnS:BBOX 1 CRS=4326: nb Results: {0}", nbResults);

        results = new ArrayList<>();
        for (int i = 0; i < nbResults; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            String name =  doc.get("id");
            results.add(name);
            LOGGER.log(Level.FINER, "\tid: {0}", name);
        }

        //we verify that we obtain the correct results
        assertEquals(nbResults, 10);
        assertTrue(results.contains("point 1"));
        assertTrue(results.contains("point 1 projected"));
        assertTrue(results.contains("point 2"));
        assertTrue(results.contains("point 3"));
        assertTrue(results.contains("box 4"));
        assertTrue(results.contains("box 2"));
        assertTrue(results.contains("box 2 projected"));
        assertTrue(results.contains("line 2"));
        assertTrue(results.contains("line 1"));
        assertTrue(results.contains("line 1 projected"));
    }

    private static List<DocumentEnvelope> fillTestData() throws Exception {

        final List<DocumentEnvelope> docs = new ArrayList<>();
        final int srid4326 = SRIDGenerator.toSRID(WGS84, Version.V1);
        final int srid3395 = SRIDGenerator.toSRID(CRS.decode("EPSG:3395"), Version.V1);

        Document doc = new Document();
        doc.add(new StringField("id", "point 1", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addPoint      (doc,           -10,                10, srid4326);
        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new StringField("id", "point 1 projected", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addPoint      (doc,           -1111475.102852225,   1113194.9079327357, srid3395); // attention !! reprojeté
        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new StringField("id", "point 2", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addPoint      (doc,           -10,                 0, srid4326);
        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new StringField("id", "point 3", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addPoint      (doc,             0,                 0, srid4326);
        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new StringField("id", "point 4", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addPoint      (doc,            40,                20, srid4326);
        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new StringField("id", "point 5", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addPoint      (doc,           -40,                30, srid4326);
        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new StringField("id", "box 1", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addBoundingBox(doc,           -40,                -25,           -50,               -40, srid4326);
        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new StringField("id", "box 2", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addBoundingBox(doc,             5,                 10,            10,                15, srid4326);
        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new StringField("id", "box 2 projected", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addBoundingBox(doc,             556597.4539663679,  1113194.9079327357,  1111475.1028522244, 1678147.5163917788, srid3395); // attention !! reprojeté
        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new StringField("id", "box 3", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addBoundingBox(doc,            30,                 50,             0,                15, srid4326);
        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new StringField("id", "box 4", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addBoundingBox(doc,           -30,                -15,             0,                10, srid4326);
        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new StringField("id", "box 5", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addBoundingBox(doc,        44.792,             51.126,        -6.171,             -2.28, srid4326);
        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new StringField("id", "line 1", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addLine       (doc,             0,                  0,            25,                 0, srid4326);
        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new StringField("id", "line 1 projected", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addLine       (doc,             0,        0,      2857692.6111605316,                 0, srid3395); // attention !! reprojeté
        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new StringField("id", "line 2", Field.Store.YES));
        doc.add(new StringField("docid", docs.size() + "", Field.Store.YES));
        doc.add(new StringField("metafile", "doc",   Field.Store.YES));
        addLine       (doc,             0,                  0,             0,               -15, srid4326);
        docs.add(new DocumentEnvelope(doc, null));

        return docs;
    }

    /**
     * Add a Line geometry to the specified Document.
     *
     * @param doc The document to add the geometry
     * @param x1  the X coordinate of the first point of the line.
     * @param y1  the Y coordinate of the first point of the line.
     * @param x2  the X coordinate of the second point of the line.
     * @param y2  the Y coordinate of the first point of the line.
     * @param crsName The coordinate reference system in witch the coordinates are expressed.
     */
    private static NamedEnvelope addLine(final Document doc, final double x1, final double y1, final double x2, final double y2, final int srid) throws Exception {

        LineString line = GF.createLineString(new Coordinate[]{
            new Coordinate(x1,y1),
            new Coordinate(x2,y2)
        });
        line.setSRID(srid);

        final String id = doc.get("id");
        NamedEnvelope namedBound      = LuceneUtils.getNamedEnvelope(id, line, WGS84);
        doc.add(new StoredField(LuceneOGCFilter.GEOMETRY_FIELD_NAME,WKBUtils.toWKBwithSRID(line)));

        return namedBound;
    }

    /**
     *  Add a point geometry to the specified Document.
     *
     * @param doc     The document to add the geometry
     * @param x       The x coordinate of the point.
     * @param y       The y coordinate of the point.
     * @param crsName The coordinate reference system in witch the coordinates are expressed.
     */
    private static NamedEnvelope addPoint(final Document doc, final double x, final double y, final int srid) throws Exception {

        Point pt = GF.createPoint(new Coordinate(x, y));
        pt.setSRID(srid);

        final String id = doc.get("id");
        NamedEnvelope namedBound      = LuceneUtils.getNamedEnvelope(id, pt, WGS84);
        doc.add(new StoredField(LuceneOGCFilter.GEOMETRY_FIELD_NAME,WKBUtils.toWKBwithSRID(pt)));

        return namedBound;
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
    private static NamedEnvelope addBoundingBox(final Document doc, final double minx, final double maxx, final double miny, final double maxy, final int srid) throws Exception {

        final Geometry poly = LuceneUtils.getPolygon(minx, maxx, miny, maxy, srid);
        final String id = doc.get("id");
        NamedEnvelope namedBound      = LuceneUtils.getNamedEnvelope(id, poly, WGS84);
        doc.add(new StoredField(LuceneOGCFilter.GEOMETRY_FIELD_NAME,WKBUtils.toWKBwithSRID(poly)));

        return namedBound;
    }
}

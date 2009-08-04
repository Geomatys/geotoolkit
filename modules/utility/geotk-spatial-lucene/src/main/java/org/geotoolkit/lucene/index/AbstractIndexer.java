/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.lucene.index;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.io.WKBWriter;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;

import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.lucene.IndexingException;
import org.geotoolkit.lucene.filter.LuceneOGCFilter;
import org.geotoolkit.resources.NIOUtilities;

/**
 * An abstract lucene Indexer used to create and writer lucene index.
 *
 * @author Mehdi Sidhoum
 * @author Guilhem Legal
 */
public abstract class AbstractIndexer<E> extends IndexLucene {


    private static final GeometryFactory GF = new GeometryFactory();
    protected static final String CORRUPTED_SINGLE_MSG = "CorruptIndexException while indexing document: ";
    protected static final String CORRUPTED_MULTI_MSG  = "CorruptIndexException while indexing document: ";
    protected static final String LOCK_SINGLE_MSG      = "LockObtainException while indexing document: ";
    protected static final String LOCK_MULTI_MSG       = "LockObtainException while indexing documents.";
    protected static final String IO_SINGLE_MSG        = "IOException while indexing document: ";
    
    /**
     * A flag use in child constructor.
     */
    protected boolean create;

    /**
     * Build a new Indexer witch create an index in the specified directory,
     * with the specified analyzer.
     *
     * @param serviceID
     * @param configDirectory
     * @param analyzer
     */
    public AbstractIndexer(String serviceID, File configDirectory, Analyzer analyzer) {
        super(analyzer);
        //we look if an index has been pre-generated. if yes, we delete the precedent index and replace it.
        final File preGeneratedIndexDirectory = new File(configDirectory, serviceID + "nextIndex");

        // we get the current index directory
        final File currentIndexDirectory = new File(configDirectory, serviceID + "index");
        setFileDirectory(currentIndexDirectory);

        if (preGeneratedIndexDirectory.exists()) {
            switchIndexDir(preGeneratedIndexDirectory, currentIndexDirectory);
            LOGGER.info("using pre-created index.");

        } else {
            //if the index File exists we don't need to index the documents again.
            if(!currentIndexDirectory.exists()) {
                create = true;
            } else {
                LOGGER.info("Index already created.");
                create = false;
            }
        }
    }

    /**
     * Build a new Indexer witch create an index in the specified directory,
     * with a Keyword analyzer.
     *
     * @param serviceID
     * @param configDirectory
     */
    public AbstractIndexer(String serviceID, File configDirectory) {
        this(serviceID, configDirectory, null);
    }

    /**
     * Replace the precedent index directory by another pre-generated.
     */
    private void switchIndexDir(File preGeneratedDirectory, File indexDirectory) {
        if (indexDirectory.exists()) {
            try {
                NIOUtilities.deleteDirectory(indexDirectory);
            } catch (IOException ex) {
                Logger.getLogger(AbstractIndexer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        preGeneratedDirectory.renameTo(indexDirectory);
    }
    
    /**
     * Create a new Index.
     *
     * @throws java.sql.SQLException
     */
    public abstract void createIndex() throws IndexingException;

    /**
     * Create a new Index with the specified list of object.
     *
     * @throws java.sql.SQLException
     */
    public abstract void createIndex(List<? extends Object> toIndex) throws IndexingException;

    /**
     * Index a document from the specified object with the specified index writer.
     * Used when indexing in line many document.
     *
     * @param writer An Lucene index writer.
     * @param object The object to index.
     */
    public abstract void indexDocument(IndexWriter writer, E object);

    /**
     * This method add to index of lucene a new document.
     * (implements AbstractIndex.indexDocument() )
     *
     * @param object The object to index.
     */
    public abstract void indexDocument(E object);

    /**
     * This method remove index of lucene a document identified by identifier.
     *
     * @param identifier
     */
    public void removeDocument(String identifier) {
        try {
            final IndexWriter writer = new IndexWriter(getFileDirectory(), analyzer, false, MaxFieldLength.UNLIMITED);

            final Term t          = new Term("id", identifier);
            final TermQuery query = new TermQuery(t);
            LOGGER.info("Term query:" + query);


            writer.deleteDocuments(query);
            LOGGER.info("Metadata: " + identifier + " removed from the index");

            writer.commit();
            writer.optimize();
            writer.close();

        } catch (CorruptIndexException ex) {
            LOGGER.severe("CorruptIndexException while indexing document: " + ex.getMessage());
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException ex) {
            LOGGER.severe("IOException while indexing document: " + ex.getMessage());
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }


    /**
    * Makes a document from the specified object.
    *
    * @param Form An MDweb formular to index.
    * @return A Lucene document.
    */
    protected abstract Document createDocument(E object) throws SQLException;


    
    private final Coordinate[] coords = new Coordinate[]{
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0)
    };
    private final CoordinateSequence points = new CoordinateArraySequence(coords);
    private final LinearRing ring = new LinearRing(points, GF);
    private final Polygon polygon = new Polygon(ring, new LinearRing[0],GF);
    

    protected byte[] toByte(double minx, double maxx, double miny, double maxy, int srid){
        coords[0].x = minx;
        coords[0].y = miny;
        coords[1].x = minx;
        coords[1].y = maxy;
        coords[2].x = maxx;
        coords[2].y = maxy;
        coords[3].x = maxx;
        coords[3].y = miny;
        coords[4].x = minx;
        coords[4].y = miny;
        polygon.setSRID(srid);
       return new WKBWriter(2).write(polygon);
    }


    protected void addBoundingBox(Document doc, double minx, double maxx, double miny, double maxy, int srid) {
        addGeometry(doc, toByte(minx, maxx, miny, maxy,srid));
    }

    public static byte[] toBytes(Geometry geom){
        final byte[] wkb = new WKBWriter(2).write(geom);
        final int SRID = geom.getSRID();
        final byte[] crs = SRIDGenerator.toBytes(SRID, SRIDGenerator.COMPACT_V1);
        final byte[] compact = new byte[wkb.length+crs.length];
        
        int i=0;
        for(;i<crs.length;i++){
            compact[i] = crs[i];
        }
        for(int j=0; j<wkb.length; i++,j++){
            compact[i] = wkb[j];
        }

        return compact;
    }

    public static void addGeometry(Document doc, Geometry geom) {
        addGeometry(doc, toBytes(geom));
    }

    public static void addGeometry(Document doc, byte[] geom) {
        doc.add(new Field(LuceneOGCFilter.GEOMETRY_FIELD_NAME,geom,Field.Store.YES));
    }

    public abstract void destroy();
}


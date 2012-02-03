/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile.indexed;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.shapefile.lock.ShpFiles;
import org.geotoolkit.data.shapefile.lock.StorageFile;
import org.geotoolkit.data.shapefile.lock.AccessManager;
import org.geotoolkit.data.shapefile.shx.ShxReader;
import org.geotoolkit.data.shapefile.shp.ShapefileHeader;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.geotoolkit.data.shapefile.shp.ShapefileReader.Record;
import org.geotoolkit.index.LockTimeoutException;
import org.geotoolkit.index.TreeException;
import org.geotoolkit.index.quadtree.DataReader;
import org.geotoolkit.index.quadtree.QuadTree;
import org.geotoolkit.index.quadtree.StoreException;
import org.geotoolkit.index.quadtree.fs.FileSystemIndexStore;
import org.geotoolkit.index.quadtree.fs.IndexHeader;
import org.geotoolkit.util.NullProgressListener;
import org.geotoolkit.process.ProgressController;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Utility class for Shapefile spatial indexing
 * 
 * @author Tommaso Nolli
 * @module pending
 */
public class ShapeFileIndexer {
    private IndexType idxType;
    private int max = 50;
    private int min = 25;
    private String byteOrder;
    private ShpFiles shpFiles;


    /**
     * Index the shapefile denoted by setShapeFileName(String fileName) If when
     * a thread starts, another thread is indexing the same file, this thread
     * will wait that the first thread ends indexing; in this case <b>zero</b>
     * is reurned as result of the indexing process.
     * 
     * @param verbose
     *                enable/disable printing of dots every 500 indexed records
     * @param listener
     *                DOCUMENT ME!
     * 
     * @return The number of indexed records (or zero)
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws TreeException
     * @throws StoreException
     *                 DOCUMENT ME!
     * @throws LockTimeoutException
     */
    public int index(final boolean verbose, final ProgressController listener)
            throws MalformedURLException, IOException, TreeException,
            StoreException, LockTimeoutException {

        if (this.shpFiles == null) {
            throw new IOException("You have to set a shape file name!");
        }

        final AccessManager locker = shpFiles.createLocker();        
        int cnt = 0;

        // Temporary file for building...
        final StorageFile storage = locker.getStorageFile(this.idxType.shpFileType);
        final File treeFile = storage.getFile();

        ShapefileReader reader = null;
        try {
            reader = locker.getSHPReader(true, false, false, null);

            switch (idxType) {
            case QIX:
                cnt = this.buildQuadTree(locker,reader, treeFile, verbose);
                break;
            default:
                throw new IllegalArgumentException("NONE is not a legal index choice");
            }
        } catch(DataStoreException ex){
            //do nothing
        }finally {
            if (reader != null)
                reader.close();
        }

        // Final index file
        locker.disposeReaderAndWriters();
        locker.replaceStorageFiles();

        return cnt;
    }

    private int buildQuadTree(final AccessManager locker, final ShapefileReader reader, 
            final File file, final boolean verbose)
            throws IOException, StoreException {
        byte order = 0;

        if ((this.byteOrder == null) || this.byteOrder.equalsIgnoreCase("NM")) {
            order = IndexHeader.NEW_MSB_ORDER;
        } else if (this.byteOrder.equalsIgnoreCase("NL")) {
            order = IndexHeader.NEW_LSB_ORDER;
        } else {
            throw new StoreException("Asked byte order '" + this.byteOrder
                    + "' must be 'NL' or 'NM'!");
        }
        
        final ShxReader shpIndex = locker.getSHXReader(false);
        QuadTree tree = null;
        int cnt = 0;
        int numRecs = shpIndex.getRecordCount();
        ShapefileHeader header = reader.getHeader();
        Envelope bounds = new Envelope(header.minX(), header.maxX(), header
                .minY(), header.maxY());

        DataReader dr = new IndexDataReader(shpIndex);

        tree = new QuadTree(numRecs, max, bounds);
        try {
            Record rec = null;

            while (reader.hasNext()) {
                rec = reader.nextRecord();
                tree.insert(cnt++, new Envelope(rec.minX, rec.maxX, rec.minY,
                        rec.maxY));

                if (verbose && ((cnt % 1000) == 0)) {
                    System.out.print('.');
                }
                if (cnt % 100000 == 0)
                    System.out.print('\n');
            }
            if (verbose)
                System.out.println("done");
            FileSystemIndexStore store = new FileSystemIndexStore(file, order);
            store.store(tree);
        } finally {
            tree.close();
        }
        return cnt;
    }

    /**
     * For quad tree this is the max depth. I don't know what it is for RTree
     * 
     * @param i
     */
    public void setMax(final int i) {
        max = i;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param i
     */
    public void setMin(final int i) {
        min = i;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param shpFiles
     */
    public void setShapeFileName(final ShpFiles shpFiles) {
        this.shpFiles = shpFiles;
    }

    /**
     * Sets the type of index to create
     * 
     * @param indexType
     *                The idxType to set.
     */
    public void setIdxType(final IndexType indexType) {
        this.idxType = indexType;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param byteOrder The byteOrder to set.
     */
    public void setByteOrder(final String byteOrder) {
        this.byteOrder = byteOrder;
    }


    public static void main(final String[] args) throws IOException {
        if ((args.length < 1) || (((args.length - 1) % 2) != 0)) {
            usage();
        }

        long start = System.currentTimeMillis();

        ShapeFileIndexer idx = new ShapeFileIndexer();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-t")) {
                idx.setIdxType(IndexType.valueOf(args[++i]));
            } else if (args[i].equals("-M")) {
                idx.setMax(Integer.parseInt(args[++i]));
            } else if (args[i].equals("-m")) {
                idx.setMin(Integer.parseInt(args[++i]));
            } else if (args[i].equals("-b")) {
                idx.setByteOrder(args[++i]);
            } else {
                if (!args[i].toLowerCase().endsWith(".shp")) {
                    System.out.println("File extension must be '.shp'");
                    System.exit(1);
                }

                idx.setShapeFileName(new ShpFiles(args[i]));
            }
        }

        try {
            System.out.print("Indexing ");

            int cnt = idx.index(true, new NullProgressListener());
            System.out.println();
            System.out.print(cnt + " features indexed ");
            System.out.println("in " + (System.currentTimeMillis() - start)
                    + "ms.");
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
            usage();
            System.exit(1);
        }
    }

    private static void usage() {
        System.out.println("Usage: ShapeFileIndexer " + "-t <QIX | GRX> "
                + "[-M <max entries per node>] "
                + "[-m <min entries per node>] " + "[-s <split algorithm>] "
                + "[-b <byte order NL | NM>] " + "<shape file>");

        System.out.println();

        System.out.println("Options:");
        System.out.println("\t-t Index type: RTREE or QUADTREE");
        System.out.println();
        System.out.println("Following options apllies only to RTREE:");
        System.out.println("\t-M maximum number of entries per node");
        System.out.println("\t-m minimum number of entries per node");
        System.out.println("\t-s split algorithm to use");
        System.out.println();
        System.out.println("Following options apllies only to QUADTREE:");
        System.out.println("\t-b byte order to use: NL = LSB; "
                + "NM = MSB (default)");

        System.exit(1);
    }


}

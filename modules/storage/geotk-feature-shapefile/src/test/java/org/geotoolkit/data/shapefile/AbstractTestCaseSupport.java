/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
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
package org.geotoolkit.data.shapefile;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.ShapeTestData;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import static org.junit.Assert.*;

/**
 * Base class for test suite. This class is not abstract for the purpose of
 * {@link TestCaseSupportTest}, but should not be instantiated otherwise. It
 * should be extented (which is why the constructor is protected).
 * <p>
 * Note: a nearly identical copy of this file exists in the {@code ext/shape}
 * module.
 * 
 * @version $Id$
 * @author Ian Schneider
 * @author Martin Desruisseaux
 * @module pending
 */
public abstract class AbstractTestCaseSupport {
    
    /**
     * Stores all temporary files here - delete on tear down.
     */
    private final List<File> tmpFiles = new ArrayList<File>();

    /**
     * Deletes all temporary files created by {@link #getTempFile}. This method
     * is automatically run after each test.
     */
    @After
    public void tearDown() throws Exception {
        
        Runtime.getRuntime().runFinalization();
        // it seems that not all files marked as temp will get erased, perhaps
        // this is because they have been rewritten? Don't know, don't _really_
        // care, so I'll just delete everything
        final Iterator<File> f = tmpFiles.iterator();
        while (f.hasNext()) {
            File targetFile = (File) f.next();

            dieDieDIE(targetFile);
            dieDieDIE(sibling(targetFile, "dbf"));
            dieDieDIE(sibling(targetFile, "shx"));
            // Quad tree index
            dieDieDIE(sibling(targetFile, "qix"));
            // Feature ID index
            dieDieDIE(sibling(targetFile, "fix"));
            // R-Tree index
            dieDieDIE(sibling(targetFile, "grx"));
            dieDieDIE(sibling(targetFile, "prj"));
            dieDieDIE(sibling(targetFile, "shp.xml"));

            f.remove();
        }
    }

    private void dieDieDIE(final File file) {
        if (file.exists()) {
            if (file.delete()) {
                // dead
            } else {
                System.out.println("Couldn't delete "+file);
                file.deleteOnExit(); // dead later
            }
        }
    }

    /**
     * Helper method for {@link #tearDown}.
     */
    protected static File sibling(final File f, final String ext) {
        return new File(f.getParent(), sibling(f.getName(), ext));
    }

    /**
     * Helper method for {@link #copyShapefiles}.
     */
    private static String sibling(String name, final String ext) {
        final int s = name.lastIndexOf('.');
        if (s >= 0) {
            name = name.substring(0, s);
        }
        return name + '.' + ext;
    }

    /**
     * Read a geometry of the given name.
     * 
     * @param wktResource
     *                The resource name to load, without its {@code .wkt}
     *                extension.
     * @return The geometry.
     * @throws IOException
     *                 if reading failed.
     */
    protected Geometry readGeometry(final String wktResource)
            throws IOException {
        final BufferedReader stream = ShapeTestData.openReader("wkt/" + wktResource
                + ".wkt");
        final WKTReader reader = new WKTReader();
        final Geometry geom;
        try {
            geom = reader.read(stream);
        } catch (ParseException pe) {
            IOException e = new IOException("parsing error in resource "
                    + wktResource);
            e.initCause(pe);
            throw e;
        }
        stream.close();
        return geom;
    }

    /**
     * Returns the first feature in the given feature collection.
     */
    protected SimpleFeature firstFeature(final FeatureCollection fc) {
        FeatureIterator<SimpleFeature> features = fc.iterator();
        SimpleFeature next = features.next();
        features.close();
        return next;
    }

    /**
     * Creates a temporary file, to be automatically deleted at the end of the
     * test suite.
     */
    protected File getTempFile() throws IOException {
        File tmpFile = File.createTempFile("test-shp", ".shp");
        tmpFile.deleteOnExit();
        assertTrue(tmpFile.isFile());

        // keep track of all temp files so we can delete them
        markTempFile(tmpFile);

        return tmpFile;
    }

    private void markTempFile(final File tmpFile) {
        tmpFiles.add(tmpFile);
    }

    /**
     * Copies the specified shape file into the {@code test-data} directory,
     * together with its sibling ({@code .dbf}, {@code .shp}, {@code .shx}
     * and {@code .prj} files).
     */
    protected File copyShapefiles(final String name) throws IOException {
        assertTrue(ShapeTestData.copy(AbstractTestCaseSupport.class, sibling(name, "dbf")).canRead());
        assertTrue(ShapeTestData.copy(AbstractTestCaseSupport.class, sibling(name, "shp")).canRead());
        try {
            assertTrue(ShapeTestData.copy(AbstractTestCaseSupport.class, sibling(name, "shx")).canRead());
        } catch (FileNotFoundException e) {
            // Ignore: this file is optional.
        }
        try {
            assertTrue(ShapeTestData.copy(AbstractTestCaseSupport.class, sibling(name, "prj")).canRead());
        } catch (FileNotFoundException e) {
            // Ignore: this file is optional.
        }
        try {
            assertTrue(ShapeTestData.copy(AbstractTestCaseSupport.class, sibling(name, "fix")).canRead());
        } catch (FileNotFoundException e) {
            // Ignore: this file is optional.
        }
        try {
            assertTrue(ShapeTestData.copy(AbstractTestCaseSupport.class, sibling(name, "qix")).canRead());
        } catch (FileNotFoundException e) {
            // Ignore: this file is optional.
        }
        try {
            assertTrue(ShapeTestData.copy(AbstractTestCaseSupport.class, sibling(name, "grx")).canRead());
        } catch (FileNotFoundException e) {
            // Ignore: this file is optional.
        }
        try {
            assertTrue(ShapeTestData.copy(AbstractTestCaseSupport.class, sibling(name, "shp.xml")).canRead());
        } catch (FileNotFoundException e) {
            // Ignore: this file is optional.
        }
        File copy = ShapeTestData.copy(AbstractTestCaseSupport.class, name);
        markTempFile(copy);
        
        return copy;
    }

}

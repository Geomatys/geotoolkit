/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.coverage.postgresql;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataSet;
import org.apache.sis.storage.DataStoreException;
import static org.geotoolkit.coverage.postgresql.PGCoverageStoreFactory.*;
import org.geotoolkit.data.multires.DefiningMosaic;
import org.geotoolkit.data.multires.DefiningPyramid;
import org.geotoolkit.data.multires.Mosaic;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.coverage.DefiningCoverageResource;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PGCVersioningTest extends org.geotoolkit.test.TestBase {

    private static final TimeZone GMT0 = TimeZone.getTimeZone("GMT+0");

    private static ParameterValueGroup params;
    private PGCoverageStore store;

    @BeforeClass
    public static void beforeClass() throws IOException {
        String path = System.getProperty("user.home");
        path += "/.geotoolkit.org/test-pgcoverage.properties";
        final File f = new File(path);
        Assume.assumeTrue(f.exists());
        final Properties properties = new Properties();
        properties.load(new FileInputStream(f));
        params = Parameters.toParameter((Map)properties, PARAMETERS_DESCRIPTOR, false);
    }

    public PGCVersioningTest(){
    }

    private void reload() throws DataStoreException, VersioningException {
        if(store != null){
            store.close();
        }

        final PGCoverageStoreFactory factory = (PGCoverageStoreFactory) DataStores.getProviderById("pgraster");

        try{
            store = factory.create(params);
        }catch(DataStoreException ex){
            //it may already exist
            store = factory.open(params);
        }

        for (GridCoverageResource r : DataStores.flatten(store, true, GridCoverageResource.class)) {
            store.remove(r);
        }
        assertTrue(DataStores.getNames(store, true, DataSet.class).isEmpty());
    }

    @Test
    public void testVersioning() throws DataStoreException, VersioningException {
        reload();

        final GeneralDirectPosition upperLeft = new GeneralDirectPosition(CommonCRS.WGS84.geographic());
        final Dimension dimension = new Dimension(20, 20);
        final Calendar calendar = Calendar.getInstance(GMT0);
        upperLeft.setOrdinate(0, -90);
        upperLeft.setOrdinate(1, +180);
        List<Version> versions;
        Version version;
        PyramidalCoverageResource cref;
        Pyramid pyramid;
        Mosaic mosaic;
        GridCoverage coverage;

        final GenericName name = NamesExt.create(null, "versLayer");
        store.add(new DefiningCoverageResource(name));
        final VersionControl vc = store.getVersioning(name);
        versions = vc.list();
        assertTrue(versions.isEmpty());

        //create version 1 -----------------------------------------------------
        calendar.setTimeInMillis(0);
        final Date date1 = calendar.getTime();
        version = vc.createVersion(date1);
        cref = (PyramidalCoverageResource) store.findResource(name, version);
        assertNotNull(cref);
        //we need to create a pyramid otherwise the version not really be created
        pyramid = (Pyramid) cref.createModel(new DefiningPyramid(CommonCRS.WGS84.geographic()));
        mosaic = pyramid.createMosaic(
                new DefiningMosaic(null, upperLeft, 1, dimension, new Dimension(1, 1)));
        mosaic.writeTiles(Stream.of(new DefaultImageTile(createImage(dimension, Color.RED), new Point(0, 0))), null);

        versions = vc.list();
        assertEquals(1, versions.size());
        assertEquals(versions.get(0).getDate().getTime(),0);
        assertEquals(date1.getTime(),versions.get(0).getDate().getTime());

        coverage = cref.read(null);
        assertImageColor(coverage.render(null), Color.RED);

        //create version 2 -----------------------------------------------------
        calendar.setTimeInMillis(50000);
        final Date date2 = calendar.getTime();
        version = vc.createVersion(date2);
        cref = (PyramidalCoverageResource) store.findResource(name, version);
        assertNotNull(cref);
        //we need to create a pyramid otherwise the version not really be created
        pyramid = (Pyramid) cref.createModel(new DefiningPyramid(CommonCRS.WGS84.geographic()));
        mosaic = pyramid.createMosaic(
                new DefiningMosaic(null, upperLeft, 1, dimension, new Dimension(1, 1)));
        mosaic.writeTiles(Stream.of(new DefaultImageTile(createImage(dimension, Color.BLUE), new Point(0, 0))), null);

        coverage = cref.read(null);
        assertImageColor(coverage.render(null), Color.BLUE);

        versions = vc.list();
        assertEquals(2, versions.size());
        assertEquals(versions.get(0).getDate().getTime(),0);
        assertEquals(versions.get(1).getDate().getTime(),50000);

        //create version 3 -----------------------------------------------------
        calendar.setTimeInMillis(20000);
        final Date date3 = calendar.getTime();
        version = vc.createVersion(date3);
        cref = (PyramidalCoverageResource) store.findResource(name, version);
        assertNotNull(cref);
        //we need to create a pyramid otherwise the version not really be created
        pyramid = (Pyramid) cref.createModel(new DefiningPyramid(CommonCRS.WGS84.geographic()));
        mosaic = pyramid.createMosaic(
                new DefiningMosaic(null, upperLeft, 1, dimension, new Dimension(1, 1)));
        mosaic.writeTiles(Stream.of(new DefaultImageTile(createImage(dimension, Color.BLUE), new Point(0, 0))), null);

        coverage = cref.read(null);
        assertImageColor(coverage.render(null), Color.GREEN);

        versions = vc.list();
        assertEquals(3, versions.size());
        assertEquals(versions.get(0).getDate().getTime(),0);
        assertEquals(versions.get(1).getDate().getTime(),20000);
        assertEquals(versions.get(2).getDate().getTime(),50000);


        //try accesing different version ---------------------------------------
        cref = (PyramidalCoverageResource) store.findResource(name.toString());
        //we should have the blue image
        coverage = cref.read(null);
        assertImageColor(coverage.render(null), Color.BLUE);

        //grab by version
        cref = (PyramidalCoverageResource) store.findResource(name,versions.get(0));
        coverage = cref.read(null);
        assertImageColor(coverage.render(null), Color.RED);
        cref = (PyramidalCoverageResource) store.findResource(name,versions.get(1));
        coverage = cref.read(null);
        assertImageColor(coverage.render(null), Color.GREEN);
        cref = (PyramidalCoverageResource) store.findResource(name,versions.get(2));
        coverage = cref.read(null);
        assertImageColor(coverage.render(null), Color.BLUE);


        //drop some versions ---------------------------------------------------
        vc.dropVersion(versions.get(1));
        versions = vc.list();
        assertEquals(2, versions.size());
        assertEquals(versions.get(0).getDate().getTime(),0);
        assertEquals(versions.get(1).getDate().getTime(),50000);

        cref = (PyramidalCoverageResource) store.findResource(name,versions.get(0));
        coverage = cref.read(null);
        assertImageColor(coverage.render(null), Color.RED);
        cref = (PyramidalCoverageResource) store.findResource(name,versions.get(1));
        coverage = cref.read(null);
        assertImageColor(coverage.render(null), Color.BLUE);

    }


    private static BufferedImage createImage(Dimension tileSize, Color color){
        final BufferedImage image = new BufferedImage(tileSize.width, tileSize.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, tileSize.width, tileSize.height);
        return image;
    }

    private static void assertImageColor(RenderedImage image, Color color){
        final BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        img.createGraphics().drawRenderedImage(image, new AffineTransform());
        image = img;
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int refargb = color.getRGB();;

        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                int argb = ((BufferedImage)image).getRGB(x, y);
                assertEquals(refargb, argb);
            }
        }
    }
}

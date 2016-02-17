/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.coverage.amended;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Collections;
import javax.measure.unit.NonSI;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.cs.DefaultCartesianCS;
import org.apache.sis.referencing.cs.DefaultCoordinateSystemAxis;
import org.apache.sis.referencing.datum.DefaultImageDatum;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.memory.MemoryCoverageStore;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.CoverageStore;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ImageCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AmendedCoverageStoreTest extends org.geotoolkit.test.TestBase {

    private static final ImageCRS IMAGECRS;
    static {
        try {
            IMAGECRS = FactoryFinder.getCRSFactory(null).createImageCRS(
                    Collections.singletonMap(CoordinateReferenceSystem.NAME_KEY,"ImageCRS"),
                    new DefaultImageDatum(
                            Collections.singletonMap(CoordinateReferenceSystem.NAME_KEY,"ImageDatum"),
                            PixelInCell.CELL_CENTER),
                    new DefaultCartesianCS(
                            Collections.singletonMap(CoordinateReferenceSystem.NAME_KEY,"ImageCS"),
                            new DefaultCoordinateSystemAxis(
                                    Collections.singletonMap(CoordinateReferenceSystem.NAME_KEY,"AxisX"),
                                    "x", AxisDirection.DISPLAY_LEFT, NonSI.PIXEL),
                            new DefaultCoordinateSystemAxis(
                                    Collections.singletonMap(CoordinateReferenceSystem.NAME_KEY,"AxisY"),
                                    "y", AxisDirection.DISPLAY_DOWN, NonSI.PIXEL)));
        } catch (FactoryException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static CoverageStore createStore() throws DataStoreException{
        final GenericName name = NamesExt.create("coverage");

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setRenderedImage(new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB));
        gcb.setCoordinateReferenceSystem(IMAGECRS);
        gcb.setGridToCRS(new AffineTransform());
        final GridCoverage coverage = gcb.build();

        final MemoryCoverageStore store = new MemoryCoverageStore();
        final CoverageReference ref = store.create(name);
        ref.acquireWriter().write(coverage, null);

        return store;
    }


    /**
     * Test override crs.
     *
     * @throws DataStoreException
     */
    @Test
    public void testOverrideCRS() throws DataStoreException{

        //create a coverage store with an unreferenced coverage
        final GenericName name = NamesExt.create("coverage");
        final CoverageStore store = createStore();

        //decorate this coverage
        final CoverageStore decorated = new AmendedCoverageStore(store);
        assertEquals(1,decorated.getNames().size());
        final AmendedCoverageReference decoratedRef = (AmendedCoverageReference) decorated.getCoverageReference(name);
        assertNotNull(decoratedRef);
        assertEquals(IMAGECRS, decoratedRef.getGridGeometry(0).getCoordinateReferenceSystem());
        assertEquals(new AffineTransform(), decoratedRef.getGridGeometry(0).getGridToCRS());

        //override crs
        decoratedRef.setOverrideCRS(CommonCRS.WGS84.normalizedGeographic());
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), decoratedRef.getGridGeometry(0).getCoordinateReferenceSystem());
        assertEquals(new AffineTransform(), decoratedRef.getGridGeometry(0).getGridToCRS());
        GridCoverage coverage = decoratedRef.acquireReader().read(0, null);
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), coverage.getCoordinateReferenceSystem());


    }

    /**
     * Test override grid to crs.
     *
     * @throws DataStoreException
     */
    @Test
    public void testOverrideGridToCrs() throws DataStoreException{

        //create a coverage store with an unreferenced coverage
        final GenericName name = NamesExt.create("coverage");
        final CoverageStore store = createStore();

        //decorate this coverage
        final CoverageStore decorated = new AmendedCoverageStore(store);
        assertEquals(1,decorated.getNames().size());
        final AmendedCoverageReference decoratedRef = (AmendedCoverageReference) decorated.getCoverageReference(name);
        assertNotNull(decoratedRef);
        assertEquals(IMAGECRS, decoratedRef.getGridGeometry(0).getCoordinateReferenceSystem());
        assertEquals(new AffineTransform(), decoratedRef.getGridGeometry(0).getGridToCRS());

        //override grid to crs
        decoratedRef.setOverrideGridToCrs(new AffineTransform2D(1, 0, 0, 1, 20, 20));
        assertEquals(IMAGECRS, decoratedRef.getGridGeometry(0).getCoordinateReferenceSystem());
        assertEquals(new AffineTransform2D(1, 0, 0, 1, 20, 20), decoratedRef.getGridGeometry(0).getGridToCRS());
        GridCoverage coverage = decoratedRef.acquireReader().read(0, null);
        assertEquals(IMAGECRS, coverage.getCoordinateReferenceSystem());
        assertEquals(new AffineTransform2D(1, 0, 0, 1, 20, 20), decoratedRef.getGridGeometry(0).getGridToCRS());


    }

    /**
     * Test reading changeing crs and transform.
     *
     * @throws DataStoreException
     */
    @Test
    public void testOverrideRead() throws DataStoreException, FactoryException{

        //create a coverage store with an unreferenced coverage
        final GenericName name = NamesExt.create("coverage");

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setRenderedImage(new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB));
        gcb.setCoordinateReferenceSystem(CRS.decode("EPSG:4326",false));
        gcb.setGridToCRS(new AffineTransform(1,0,0,1,0,0));
        final GridCoverage coverage = gcb.build();

        final MemoryCoverageStore store = new MemoryCoverageStore();
        final CoverageReference ref = store.create(name);
        ref.acquireWriter().write(coverage, null);

        //decorate this coverage
        final CoverageStore decorated = new AmendedCoverageStore(store);
        final AmendedCoverageReference decoratedRef = (AmendedCoverageReference) decorated.getCoverageReference(name);

        //override grid to crs
        final CoordinateReferenceSystem overrideCrs = CRS.decode("EPSG:4326",true);
        decoratedRef.setOverrideCRS(overrideCrs);
        decoratedRef.setOverrideGridToCrs(new AffineTransform2D(1, 0, 0, 1, 20, 30));
        assertEquals(overrideCrs, decoratedRef.getGridGeometry(0).getCoordinateReferenceSystem());
        assertEquals(new AffineTransform2D(1, 0, 0, 1, 20, 30), decoratedRef.getGridGeometry(0).getGridToCRS());
        GridCoverage decoratedCov = decoratedRef.acquireReader().read(0, null);
        assertEquals(overrideCrs, decoratedCov.getCoordinateReferenceSystem());
        assertEquals(new AffineTransform2D(1, 0, 0, 1, 20, 30), decoratedRef.getGridGeometry(0).getGridToCRS());

        //TODO this est is biazed : memory coverage store do not care about the read parameters
        //read an area
        final GridCoverageReadParam param = new GridCoverageReadParam();
        final GeneralEnvelope env = new GeneralEnvelope(overrideCrs);
        env.setRange(0, 0, 10);
        env.setRange(1, 0, 10);
        param.setEnvelope(env);
        decoratedCov = decoratedRef.acquireReader().read(0, param);
        assertEquals(overrideCrs, decoratedCov.getCoordinateReferenceSystem());
        assertEquals(new AffineTransform2D(1, 0, 0, 1, 20, 30), decoratedRef.getGridGeometry(0).getGridToCRS());


    }

}

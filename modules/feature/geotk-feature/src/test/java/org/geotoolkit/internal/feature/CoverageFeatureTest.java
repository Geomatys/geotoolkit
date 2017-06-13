/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.internal.feature;

import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.util.Collection;
import java.util.Iterator;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.SampleDimensionBuilder;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.geometry.jts.coordinatesequence.LiteCoordinateSequence;
import org.geotoolkit.image.BufferedImages;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageFeatureTest {

    private static final GeometryFactory GF = new GeometryFactory();

    /**
     * Test coverage 2D mapped as a feature.
     *
     * @throws CoverageStoreException
     */
    @Test
    public void coverageRecord2DTest() throws CoverageStoreException {

        //create coverage
        final BufferedImage image = BufferedImages.createImage(2, 2, 2, DataBuffer.TYPE_INT);
        final WritableRaster raster = image.getRaster();
        raster.setPixel(0, 0, new int[]{10,2});
        raster.setPixel(1, 0, new int[]{30,4});
        raster.setPixel(0, 1, new int[]{50,6});
        raster.setPixel(1, 1, new int[]{70,8});

        SampleDimensionBuilder sdb = new SampleDimensionBuilder(DataBuffer.TYPE_INT);
        sdb.setDescription("values");
        sdb.add("valuesCat", null, NumberRange.create(0, true, 1000, true), 10, -5);
        final GridSampleDimension sdim1 = sdb.build();
        sdb = new SampleDimensionBuilder(DataBuffer.TYPE_INT);
        sdb.setDescription("quality");
        sdb.add("qualityCat", null, NumberRange.create(0, true, 100, true), 1, 0);
        final GridSampleDimension sdim2 = sdb.build();


        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName("MyCoverage");
        gcb.setRenderedImage(image);
        gcb.setGridToCRS(new AffineTransform(2, 0, 0, 2, 31, 11));
        gcb.setCoordinateReferenceSystem(CommonCRS.WGS84.normalizedGeographic());
        gcb.setSampleDimensions(sdim1,sdim2);
        final GridCoverage coverage = gcb.build();

        //test mapped feature type
        final FeatureType coverageType = CoverageFeature.createCoverageType(coverage);
        final FeatureAssociationRole role = (FeatureAssociationRole) coverageType.getProperty(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        final FeatureType recordType = role.getValueType();

        assertEquals("MyCoverage",coverageType.getName().toString());
        assertTrue(TypeConventions.COVERAGE_TYPE.isAssignableFrom(coverageType));

        assertEquals("MyCoverageRecord",recordType.getName().toString());
        assertTrue(TypeConventions.COVERAGE_RECORD_TYPE.isAssignableFrom(recordType));

        //convert coverage to feature
        final Feature feature = coverageType.newInstance();
        feature.setProperty(CoverageFeature.coverageRecords(coverage,role));

        //check records
        final Collection col = (Collection) feature.getPropertyValue(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        assertEquals(4, col.size());
        final Iterator<Feature> ite = col.iterator();
        final Feature r1 = ite.next();
        final Feature r2 = ite.next();
        final Feature r3 = ite.next();
        final Feature r4 = ite.next();
        assertFalse(ite.hasNext());

        assertEquals(10.0*10-5, r1.getPropertyValue("values"));
        assertEquals(2.0,       r1.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{30,12, 32,12, 32,14, 30,14, 30,12})), r1.getProperty("geometry").getValue());
        assertEquals(30.0*10-5, r2.getPropertyValue("values"));
        assertEquals(4.0,       r2.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{32,12, 34,12, 34,14, 32,14, 32,12})), r2.getProperty("geometry").getValue());
        assertEquals(50.0*10-5, r3.getPropertyValue("values"));
        assertEquals(6.0,       r3.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{30,14, 32,14, 32,16, 30,16, 30,14})), r3.getProperty("geometry").getValue());
        assertEquals(70.0*10-5, r4.getPropertyValue("values"));
        assertEquals(8.0,       r4.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{32,14, 34,14, 34,16, 32,16, 32,14})), r4.getProperty("geometry").getValue());

    }

}

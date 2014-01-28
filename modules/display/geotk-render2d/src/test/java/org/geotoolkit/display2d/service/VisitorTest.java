/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.display2d.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotoolkit.data.FeatureStoreUtilities;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.display2d.canvas.AbstractGraphicVisitor;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.identity.FeatureId;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class VisitorTest {

    public VisitorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Feature visitor test.
     *
     * @throws Exception
     */
    @Test
    public void intersectionFeatureTest() throws Exception {
        final MutableStyleFactory sf = new DefaultStyleFactory();

        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("testingIntersect");
        sftb.add("geom", Polygon.class, DefaultGeographicCRS.WGS84);
        sftb.setDefaultGeometry("geom");
        final SimpleFeatureType sft = sftb.buildSimpleFeatureType();

        final FeatureCollection collection = FeatureStoreUtilities.collection("id", sft);
        final SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);

        final GeometryFactory gf = new GeometryFactory();
        LinearRing ring = gf.createLinearRing(new Coordinate[]{
                    new Coordinate(10, 10),
                    new Coordinate(20, 10),
                    new Coordinate(20, 20),
                    new Coordinate(10, 20),
                    new Coordinate(10, 10),});
        Polygon pol = gf.createPolygon(ring, new LinearRing[0]);
        sfb.set("geom", pol);

        collection.add(sfb.buildFeature(""));

        assertTrue(collection.size() == 1);


        MapLayer layer = MapBuilder.createFeatureLayer(collection, sf.style(sf.polygonSymbolizer()));
        layer.setSelectable(true);
        layer.setVisible(true);
        MapContext context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
        context.layers().add(layer);

        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final Dimension dim = new Dimension(360, 180);
        Shape shparea = new Rectangle(195, 75, 2, 2); //starting at top left corner
        ListVisitor visitor = new ListVisitor();

        //ensure we can paint image
        DefaultPortrayalService.portray(context, env, dim, true);
        DefaultPortrayalService.visit(context, env, dim, true, null, shparea, visitor);

        assertEquals(1, visitor.features.size());
        assertEquals("testingIntersect.0", visitor.features.get(0).getID());

        shparea = new Rectangle(30, 12, 2, 2); //starting at top left corner
        visitor = new ListVisitor();

        //ensure we can paint image
        DefaultPortrayalService.portray(context, env, dim, true);
        DefaultPortrayalService.visit(context, env, dim, true, null, shparea, visitor);

        assertTrue(visitor.features.size() == 0);

    }

    /**
     * Coverage visitor test
     */
    @Test
    public void intersectionCoverageTest() throws Exception {

        final float[][] data = new float[180][360];
        for(int i=0;i<180;i++)Arrays.fill(data[i], 15f);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName("coverage");
        gcb.setRenderedImage(data);
        gcb.setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84);
        final AffineTransform trs = new AffineTransform(1,0,0,-1,-180,90);
        gcb.setGridToCRS(trs);


        final CoverageMapLayer cml = MapBuilder.createCoverageLayer(gcb.build());
        cml.setSelectable(true);
        MapContext context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
        context.layers().add(cml);

        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final Dimension dim = new Dimension(360, 180);
        final Shape shparea = new Rectangle(195, 75, 2, 2); //starting at top left corner
        final ListVisitor visitor = new ListVisitor();

        DefaultPortrayalService.visit(context, env, dim, true, null, shparea, visitor);

        assertTrue(visitor.coverages.size() != 0);

    }


    private static class ListVisitor extends AbstractGraphicVisitor {

        public List<FeatureId> features = new ArrayList<FeatureId>();
        public List<ProjectedCoverage> coverages = new ArrayList<ProjectedCoverage>();

        @Override
        public void visit(final ProjectedFeature feature, final RenderingContext2D context, final SearchAreaJ2D queryArea) {
            features.add(feature.getFeatureId());
        }

        @Override
        public void visit(final ProjectedCoverage coverage, final RenderingContext2D context, final SearchAreaJ2D queryArea) {
            coverages.add(coverage);
        }
    }
}

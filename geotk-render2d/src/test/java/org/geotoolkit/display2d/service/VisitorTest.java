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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.portrayal.MapLayers;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.WritableFeatureSet;
import org.geotoolkit.display2d.canvas.AbstractGraphicVisitor;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.PixelInCell;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class VisitorTest extends org.geotoolkit.test.TestBase {
    /**
     * Feature visitor test.
     */
    @Test
    public void intersectionFeatureTest() throws Exception {
        final MutableStyleFactory sf = new DefaultStyleFactory();
        final GeographicCRS crs = CommonCRS.WGS84.normalizedGeographic();

        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("testingIntersect");
        sftb.addAttribute(String.class).setName("id").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        sftb.addAttribute(Polygon.class).setName("geom").setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType sft = sftb.build();

        final WritableFeatureSet collection = new InMemoryFeatureSet("id", sft);
        final Feature f = sft.newInstance();

        final GeometryFactory gf = org.geotoolkit.geometry.jts.JTS.getFactory();
        LinearRing ring = gf.createLinearRing(new Coordinate[]{
                    new Coordinate(10, 10),
                    new Coordinate(20, 10),
                    new Coordinate(20, 20),
                    new Coordinate(10, 20),
                    new Coordinate(10, 10),});
        Polygon pol = gf.createPolygon(ring, new LinearRing[0]);
        pol.setUserData(crs);
        f.setPropertyValue("id", "id-0");
        f.setPropertyValue("geom", pol);

        collection.add(Arrays.asList(f).iterator());

        MapLayer layer = MapBuilder.createLayer(collection);
        layer.setStyle(sf.style(sf.polygonSymbolizer()));
        layer.setVisible(true);
        MapLayers context = MapBuilder.createContext(CommonCRS.WGS84.normalizedGeographic());
        context.getComponents().add(layer);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final Dimension dim = new Dimension(360, 180);
        Shape shparea = new Rectangle(195, 75, 2, 2); //starting at top left corner
        ListVisitor visitor = new ListVisitor();

        //ensure we can paint image
        DefaultPortrayalService.portray(context, env, dim, true);
        DefaultPortrayalService.visit(context, env, dim, true, null, shparea, visitor);

        assertEquals(1, visitor.features.size());
        assertEquals("id-0", FeatureExt.getId(visitor.features.get(0)).getIdentifier());

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
    @Ignore("Need to revisit DataBuffer construction.")
    public void intersectionCoverageTest() throws Exception {

        final float[][] data = new float[180][360];
        for(int i=0;i<180;i++)Arrays.fill(data[i], 15f);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setValues(BufferedImages.toDataBuffer1D(data), null);
        final AffineTransform trs = new AffineTransform(1,0,0,-1,-180,90);
        gcb.setDomain(new GridGeometry(new GridExtent(360, 180), PixelInCell.CELL_CENTER, new AffineTransform2D(trs), CommonCRS.WGS84.normalizedGeographic()));
        gcb.setRanges(new SampleDimension.Builder().setName(0).build());


        final MapLayer cml = MapBuilder.createLayer(new InMemoryGridCoverageResource(gcb.build()));
        MapLayers context = MapBuilder.createContext(CommonCRS.WGS84.normalizedGeographic());
        context.getComponents().add(cml);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final Dimension dim = new Dimension(360, 180);
        final Shape shparea = new Rectangle(195, 75, 2, 2); //starting at top left corner
        final ListVisitor visitor = new ListVisitor();

        DefaultPortrayalService.visit(context, env, dim, true, null, shparea, visitor);

        assertTrue(visitor.coverages.size() != 0);
    }

    private static class ListVisitor extends AbstractGraphicVisitor {

        public List<Feature> features = new ArrayList<>();
        public List<GridCoverageResource> coverages = new ArrayList<>();

        @Override
        public void visit(Feature feature, final RenderingContext2D context, final SearchAreaJ2D queryArea) {
            features.add(feature);
        }

        @Override
        public void visit(final GridCoverageResource coverage, final RenderingContext2D context, final SearchAreaJ2D queryArea) {
            coverages.add(coverage);
        }
    }
}

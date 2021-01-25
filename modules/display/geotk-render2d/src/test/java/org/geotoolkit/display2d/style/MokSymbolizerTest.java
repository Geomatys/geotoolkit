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
package org.geotoolkit.display2d.style;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Arrays;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.geometry.ImmutableEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.WritableFeatureSet;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * Test that symbolizer renderer are properly called and only once.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MokSymbolizerTest extends org.geotoolkit.test.TestBase {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final MutableStyleFactory SF = new DefaultStyleFactory();


    private final MapContext context;
    private final ImmutableEnvelope env;

    public MokSymbolizerTest() throws Exception {

        GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        this.env = ImmutableEnvelope.castOrCopy(env);

        context = MapBuilder.createContext();

        // create the feature collection for tests -----------------------------
        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("test");
        sftb.addAttribute(Point.class).setName("geom").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        sftb.addAttribute(String.class).setName("att1");
        sftb.addAttribute(Double.class).setName("att2");
        final FeatureType sft = sftb.build();
        WritableFeatureSet col = new InMemoryFeatureSet("id", sft);

        Feature sf1 = sft.newInstance();
        sf1.setPropertyValue("geom", GF.createPoint(new Coordinate(0, 0)));
        sf1.setPropertyValue("att1", "value1");
        Feature sf2 = sft.newInstance();
        sf2.setPropertyValue("geom", GF.createPoint(new Coordinate(-180, -90)));
        sf2.setPropertyValue("att1", "value1");
        Feature sf3 = sft.newInstance();
        sf3.setPropertyValue("geom", GF.createPoint(new Coordinate(-180, 90)));
        sf3.setPropertyValue("att1", "value1");
        Feature sf4 = sft.newInstance();
        sf4.setPropertyValue("geom", GF.createPoint(new Coordinate(180, -90)));
        sf4.setPropertyValue("att1", "value1");
        Feature sf5 = sft.newInstance();
        sf5.setPropertyValue("geom", GF.createPoint(new Coordinate(180, -90)));
        sf5.setPropertyValue("att1", "value1");

        col.add(Arrays.asList(sf1,sf2,sf3,sf4,sf5).iterator());

        final MapLayer layer = MapBuilder.createFeatureLayer(col);
        layer.setStyle(SF.style(new MokSymbolizer()));
        context.getComponents().add(layer);

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSymbolizer() throws PortrayalException {
        final CanvasDef cdef = new CanvasDef(new Dimension(500, 500), env);

        //test normal pass
        Hints hints = new Hints();
        hints.put(GO2Hints.KEY_MULTI_THREAD, Boolean.FALSE);
        MokSymbolizerRenderer.called = 0;
        cdef.setBackground(Color.WHITE);
        DefaultPortrayalService.portray(cdef, new SceneDef(context,hints));
        assertEquals(5, MokSymbolizerRenderer.called);


        //test multithread
        hints = new Hints();
        hints.put(GO2Hints.KEY_MULTI_THREAD, Boolean.TRUE);
        MokSymbolizerRenderer.called = 0;
        DefaultPortrayalService.portray(cdef, new SceneDef(context, hints));
        assertEquals(5, MokSymbolizerRenderer.called);

    }

}

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

package org.geotoolkit.display2d.ext.legend;

import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.style.Symbolizer;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.display2d.ext.dimrange.DimRangeSymbolizer;
import javax.measure.unit.Unit;
import org.geotoolkit.display2d.ext.DefaultBackgroundTemplate;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LegendSizeTest {

    private static final LegendTemplate NO_MARGIN_TEMPLATE = new DefaultLegendTemplate(
            new DefaultBackgroundTemplate(new BasicStroke(0), Color.BLACK, Color.RED, new Insets(0, 0, 0, 0), 0),
            2, null, new Font("arial", Font.PLAIN, 10), false, new Font("arial", Font.PLAIN, 10));

    private static final MutableStyleFactory SF = new DefaultStyleFactory();

    public LegendSizeTest() {
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
    public void testDimRangeLegend() {

        final Symbolizer dr = new DimRangeSymbolizer(new MeasurementRange(NumberRange.create(10, 20), Unit.ONE));
        final MapLayer layer = MapBuilder.createEmptyMapLayer();
        layer.setStyle(SF.style(dr));

        final MapContext ctx = MapBuilder.createContext();
        ctx.layers().add(layer);

        Dimension dim = DefaultLegendService.legendPreferredSize(null, ctx);

        assertEquals(120,dim.width);
        assertEquals(20,dim.height);

        //test with an empty template
        dim = DefaultLegendService.legendPreferredSize(NO_MARGIN_TEMPLATE, ctx);
        assertEquals(120,dim.width);
        assertEquals(20,dim.height);

    }

    @Test
    public void testRasterLegend() {

        final Symbolizer dr = StyleConstants.DEFAULT_RASTER_SYMBOLIZER;
        final MapLayer layer = MapBuilder.createEmptyMapLayer();
        layer.setStyle(SF.style(dr));

        final MapContext ctx = MapBuilder.createContext();
        ctx.layers().add(layer);

        Dimension dim = DefaultLegendService.legendPreferredSize(null, ctx);

        assertEquals(30,dim.width);
        assertEquals(24,dim.height);

        //test with an empty template
        dim = DefaultLegendService.legendPreferredSize(NO_MARGIN_TEMPLATE, ctx);
        assertEquals(30,dim.width);
        assertEquals(24,dim.height);
    }

    @Test
    public void testNoStyle() {

        final MapLayer layer = MapBuilder.createEmptyMapLayer();
        layer.setStyle(SF.style());

        final MapContext ctx = MapBuilder.createContext();
        ctx.layers().add(layer);

        Dimension dim = DefaultLegendService.legendPreferredSize(null, ctx);

        assertEquals(1,dim.width);
        assertEquals(1,dim.height);

        //test with an empty template
        dim = DefaultLegendService.legendPreferredSize(NO_MARGIN_TEMPLATE, ctx);
        assertEquals(1,dim.width);
        assertEquals(1,dim.height);
    }

    @Test
    public void testPolygonStyle() {

        final MapLayer layer = MapBuilder.createEmptyMapLayer();
        layer.setStyle(SF.style(StyleConstants.DEFAULT_POLYGON_SYMBOLIZER));

        final MapContext ctx = MapBuilder.createContext();
        ctx.layers().add(layer);

        Dimension dim = DefaultLegendService.legendPreferredSize(null, ctx);

        assertEquals(30,dim.width);
        assertEquals(24,dim.height);

        //test with an empty template
        dim = DefaultLegendService.legendPreferredSize(NO_MARGIN_TEMPLATE, ctx);
        assertEquals(30,dim.width);
        assertEquals(24,dim.height);
    }

}

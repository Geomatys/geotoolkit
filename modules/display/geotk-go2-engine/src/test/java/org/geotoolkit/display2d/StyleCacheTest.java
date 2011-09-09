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
package org.geotoolkit.display2d;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.display2d.style.CachedExternal;
import org.geotoolkit.display2d.style.CachedFill;
import org.geotoolkit.display2d.style.CachedGraphic;
import org.geotoolkit.display2d.style.CachedMark;
import org.geotoolkit.display2d.style.CachedPointSymbolizer;
import org.geotoolkit.display2d.style.CachedStroke;
import org.geotoolkit.display2d.style.VisibilityState;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.function.DefaultInterpolate;
import org.geotoolkit.style.function.DefaultInterpolationPoint;
import org.geotoolkit.style.function.Interpolate;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.Fill;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Mark;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.Stroke;

import static org.junit.Assert.*;
import static org.geotoolkit.style.StyleConstants.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StyleCacheTest {

    private final MutableStyleFactory SF = new DefaultStyleFactory();
    private final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public StyleCacheTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void strokeCacheTest() throws Exception {
        Stroke stroke = SF.stroke();
        CachedStroke cached = CachedStroke.cache(stroke);

        assertTrue(cached.isStatic());
        assertTrue(cached.isStaticVisible() == VisibilityState.VISIBLE);
        assertTrue(cached.isVisible(null));


        stroke = SF.stroke(FF.property("color_prop"), FF.property("width_prop"));
        cached = CachedStroke.cache(stroke);

        assertTrue(!cached.isStatic());
        assertTrue(cached.isStaticVisible() == VisibilityState.DYNAMIC);


        stroke = SF.stroke(Color.WHITE, 0.20d, new float[]{1,3,1});
        cached = CachedStroke.cache(stroke);
        
    }

    @Test
    public void fillCacheTest() throws Exception {
        Fill fill = SF.fill();
        CachedFill cached = CachedFill.cache(fill);

        assertTrue(cached.isStatic());
        assertTrue(cached.isStaticVisible() == VisibilityState.VISIBLE);
        assertTrue(cached.isVisible(null));

        fill = SF.fill(FF.property("color_prop"));
        cached = CachedFill.cache(fill);

        assertTrue(!cached.isStatic());
        assertTrue(cached.isStaticVisible() == VisibilityState.DYNAMIC);

    }

    @Test
    public void markCacheTest() throws Exception {
        Mark mark = SF.getSquareMark();
        CachedMark cached = CachedMark.cache(mark);

        assertTrue(cached.isStatic());
        assertEquals(VisibilityState.VISIBLE, cached.isStaticVisible() );
        assertTrue(cached.isVisible(null));

        mark = SF.mark(StyleConstants.MARK_CROSS, SF.fill(), SF.stroke(FF.property("color_prop"), FF.property("width_prop")));
        cached = CachedMark.cache(mark);

        assertTrue(!cached.isStatic());
        assertEquals(VisibilityState.VISIBLE, cached.isStaticVisible());

    }

    @Test
    public void externalCacheTest() throws Exception{
        final ExternalGraphic ext = SF.externalGraphic("/org/geotoolkit/display2d/sample.svg", "image/svg");
        CachedExternal cached = CachedExternal.cache(ext);

        assertFalse(cached.isStatic());
        assertEquals(VisibilityState.VISIBLE, cached.isStaticVisible() );
        assertTrue(cached.isVisible(null));

        BufferedImage buffer = cached.getImage(Float.NaN, 1, null);
        assertNotNull(buffer);
        assertEquals(buffer.getWidth(), 12);
        assertEquals(buffer.getHeight(), 12);

        buffer = cached.getImage(null, 1, null);
        assertEquals(buffer.getWidth(), 12);
        assertEquals(buffer.getHeight(), 12);

        buffer = cached.getImage(24f, 1, null);
        assertEquals(buffer.getWidth(), 24);
        assertEquals(buffer.getHeight(), 24);

    }

    @Test
    public void GraphicCacheTest() throws Exception {

        //Test a complex graphic
        final Expression Lookup = FF.property("POP_CNTRY");
        final List<InterpolationPoint> values = new ArrayList<InterpolationPoint>();

        //test color interpolation ---------------------------------------------
        values.clear();
        values.add(new DefaultInterpolationPoint(0d,FF.literal(3d)));
        values.add(new DefaultInterpolationPoint(500000000d,FF.literal(50d)));

        Interpolate interpolate = new DefaultInterpolate(Lookup, values, Method.COLOR, Mode.CUBIC, null);

        List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        symbols.add(SF.mark(StyleConstants.MARK_CIRCLE,SF.fill(Color.RED),SF.stroke()));
        Graphic graphic = SF.graphic(
                symbols,
                StyleConstants.DEFAULT_GRAPHIC_OPACITY,
                interpolate,
                StyleConstants.DEFAULT_GRAPHIC_ROTATION,
                StyleConstants.DEFAULT_ANCHOR_POINT,
                StyleConstants.DEFAULT_DISPLACEMENT);

        CachedGraphic cached = CachedGraphic.cache(graphic);

        assertTrue(!cached.isStatic());
        assertEquals(VisibilityState.DYNAMIC, cached.isStaticVisible() );

    }

    @Test
    public void pointCacheTest() throws Exception {

        //test that we have a static cache
        PointSymbolizer point = SF.pointSymbolizer();
        CachedPointSymbolizer cached = (CachedPointSymbolizer) GO2Utilities.getCached(point,null);

        assertTrue(cached.isStatic());
        assertEquals(VisibilityState.VISIBLE, cached.isStaticVisible());
        assertTrue(cached.isVisible(null));


        BufferedImage buffer1 = cached.getImage(null, 5f, null);
        BufferedImage buffer2 = cached.getImage(null, 5f, null);

        //we must have exactly the same object
        assertTrue(buffer1 == buffer2);


        //test svg external image ----------------------------------------------

        ExternalGraphic ext = SF.externalGraphic("/org/geotoolkit/display2d/sample.svg", "image/svg");
        List<GraphicalSymbol> gs = new ArrayList<GraphicalSymbol>();
        gs.add(ext);
        point = SF.pointSymbolizer(
                    SF.graphic(
                        gs,
                        DEFAULT_GRAPHIC_OPACITY,
                        FF.literal(12),
                        DEFAULT_GRAPHIC_ROTATION,
                        DEFAULT_ANCHOR_POINT,
                        DEFAULT_DISPLACEMENT)
                    ,null);
        cached = (CachedPointSymbolizer) GO2Utilities.getCached(point,null);

        assertFalse(cached.isStatic());
        assertEquals(VisibilityState.DYNAMIC, cached.isStaticVisible() );
        assertTrue(cached.isVisible(null));

        BufferedImage buffer = cached.getImage(null, 1, null);
        assertNotNull(buffer);
        assertEquals(buffer.getWidth(), 12);
        assertEquals(buffer.getHeight(), 12);

        //different size
        point = SF.pointSymbolizer(
                    SF.graphic(
                        gs,
                        DEFAULT_GRAPHIC_OPACITY,
                        FF.literal(24),
                        DEFAULT_GRAPHIC_ROTATION,
                        DEFAULT_ANCHOR_POINT,
                        DEFAULT_DISPLACEMENT)
                    ,null);
        cached = (CachedPointSymbolizer) GO2Utilities.getCached(point,null);

        assertFalse(cached.isStatic());
        assertEquals(VisibilityState.DYNAMIC, cached.isStaticVisible() );
        assertTrue(cached.isVisible(null));

        buffer = cached.getImage(null, 1, null);
        assertNotNull(buffer);
        assertEquals(buffer.getWidth(), 24);
        assertEquals(buffer.getHeight(), 24);


    }

}

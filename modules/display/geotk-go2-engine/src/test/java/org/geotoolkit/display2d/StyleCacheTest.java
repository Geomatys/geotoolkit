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
import org.opengis.style.Fill;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Mark;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;

import static org.junit.Assert.*;

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
        CachedStroke cached = new CachedStroke(stroke);

        assertTrue(cached.isStatic());
        assertTrue(cached.isStaticVisible() == VisibilityState.VISIBLE);
        assertTrue(cached.isVisible(null));


        stroke = SF.stroke(FF.property("color_prop"), FF.property("width_prop"));
        cached = new CachedStroke(stroke);

        assertTrue(!cached.isStatic());
        assertTrue(cached.isStaticVisible() == VisibilityState.DYNAMIC);

    }

    @Test
    public void fillCacheTest() throws Exception {
        Fill fill = SF.fill();
        CachedFill cached = new CachedFill(fill);

        assertTrue(cached.isStatic());
        assertTrue(cached.isStaticVisible() == VisibilityState.VISIBLE);
        assertTrue(cached.isVisible(null));

        fill = SF.fill(FF.property("color_prop"));
        cached = new CachedFill(fill);

        assertTrue(!cached.isStatic());
        assertTrue(cached.isStaticVisible() == VisibilityState.DYNAMIC);

    }

    @Test
    public void markCacheTest() throws Exception {
        Mark mark = SF.getSquareMark();
        CachedMark cached = new CachedMark(mark);

        assertTrue(cached.isStatic());
        assertEquals(VisibilityState.VISIBLE, cached.isStaticVisible() );
        assertTrue(cached.isVisible(null));

        mark = SF.mark(StyleConstants.MARK_CROSS, SF.fill(), SF.stroke(FF.property("color_prop"), FF.property("width_prop")));
        cached = new CachedMark(mark);

        assertTrue(!cached.isStatic());
        assertEquals(VisibilityState.VISIBLE, cached.isStaticVisible());

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

        CachedGraphic cached = new CachedGraphic(graphic);

        assertTrue(!cached.isStatic());
        assertEquals(VisibilityState.DYNAMIC, cached.isStaticVisible() );

    }

    @Test
    public void pointCacheTest() throws Exception {

        //test that we have a static cache
        PointSymbolizer point = SF.pointSymbolizer();
        CachedPointSymbolizer cached = (CachedPointSymbolizer) GO2Utilities.getCached(point);

        assertTrue(cached.isStatic());
        assertEquals(VisibilityState.VISIBLE, cached.isStaticVisible());
        assertTrue(cached.isVisible(null));


        BufferedImage buffer1 = cached.getImage(null, 5f, null);
        BufferedImage buffer2 = cached.getImage(null, 5f, null);

        //we must have exactly the same object
        assertTrue(buffer1 == buffer2);


    }

}

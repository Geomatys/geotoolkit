/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.style.io;

import java.awt.Color;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.sis.internal.system.DefaultFactories;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.function.Categorize;
import org.geotoolkit.style.function.DefaultInterpolationPoint;
import org.geotoolkit.style.function.Interpolate;
import org.geotoolkit.style.function.InterpolationPoint;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.style.ColorMap;
import org.opengis.style.StyleFactory;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PaletteReaderTest {

    protected static final FilterFactory FF = DefaultFactories.forBuildin(FilterFactory.class);
    protected static final MutableStyleFactory SF = (MutableStyleFactory) DefaultFactories.forBuildin(StyleFactory.class);

    @Test
    public void readCLR() throws IOException {
        String palette =
            "ColorMap 1 1\n" +
            "0.000000 143 0 0\n" +
            "500.000000 244 0 0\n" +
            "1000.000000 255 89 0\n" +
            "1500.000000 255 189 0\n" +
            "2000.000000 236 255 34\n" +
            "2500.000000 135 255 135\n" +
            "3000.000000 34 255 236\n" +
            "3500.000000 0 189 255\n" +
            "4000.000000 0 89 255\n" +
            "4500.000000 0 0 244\n" +
            "5000.000000 0 0 143";

        final ColorMap cm = new PaletteReader(PaletteReader.PATTERN_CLR).read(palette);
        assertTrue(cm.getFunction() instanceof Interpolate);
        final Interpolate interpolate = (Interpolate) cm.getFunction();
        final List<InterpolationPoint> steps = interpolate.getInterpolationPoints();
        assertEquals(12, steps.size());
        for (int i = 0;i < steps.size(); i++) {
            final InterpolationPoint step = steps.get(i);
            InterpolationPoint expected = null;
            switch (i) {
                case  0 : expected = new DefaultInterpolationPoint(Double.NaN, SF.literal(new Color(0,  0,  0, 0))); break;
                case  1 : expected = new DefaultInterpolationPoint(   0d, SF.literal(new Color(143,  0,  0))); break;
                case  2 : expected = new DefaultInterpolationPoint( 500d, SF.literal(new Color(244,  0,  0))); break;
                case  3 : expected = new DefaultInterpolationPoint(1000d, SF.literal(new Color(255, 89,  0))); break;
                case  4 : expected = new DefaultInterpolationPoint(1500d, SF.literal(new Color(255,189,  0))); break;
                case  5 : expected = new DefaultInterpolationPoint(2000d, SF.literal(new Color(236,255, 34))); break;
                case  6 : expected = new DefaultInterpolationPoint(2500d, SF.literal(new Color(135,255,135))); break;
                case  7 : expected = new DefaultInterpolationPoint(3000d, SF.literal(new Color( 34,255,236))); break;
                case  8 : expected = new DefaultInterpolationPoint(3500d, SF.literal(new Color(  0,189,255))); break;
                case  9 : expected = new DefaultInterpolationPoint(4000d, SF.literal(new Color(  0, 89,255))); break;
                case 10 : expected = new DefaultInterpolationPoint(4500d, SF.literal(new Color(  0,  0,244))); break;
                case 11 : expected = new DefaultInterpolationPoint(5000d, SF.literal(new Color(  0,  0,143))); break;
                default : fail("Unexpected number of elements.");
            }
            assertEquals(expected, step);
        }
    }

    @Test
    public void readCPT() throws IOException {
        String palette =
            "# Truly simulates the JET colormap in Matlab\n" +
            "# COLOR_MODEL = RGB\n" +
            "  0   0   0 143   1   0   0 159\n" +
            "  1   0   0 159   2  50   0 175\n" +
            "  2  50   0 175   3   0  12 191\n" +
            "  3   0  12 191   4   0   0 207\n" +
            "  4   0   0 207   5   0   0 223";

        final ColorMap cm = new PaletteReader(PaletteReader.PATTERN_CPT).read(palette);
        assertTrue(cm.getFunction() instanceof Categorize);
        final Categorize categorize = (Categorize) cm.getFunction();
        final Map<Expression,Expression> steps = categorize.getThresholds();
        assertEquals(8, steps.size());
        int i=0;
        for (Entry<Expression,Expression> entry : steps.entrySet()) {
            Entry<Expression,Expression> expected = null;
            switch (i) {
                case  0 : expected = new AbstractMap.SimpleEntry(StyleConstants.CATEGORIZE_LESS_INFINITY, SF.literal(new Color(0f,0f,0f,0f))); break;
                case  1 : expected = new AbstractMap.SimpleEntry(FF.literal(0), SF.literal(new Color(  0,  0,143))); break;
                case  2 : expected = new AbstractMap.SimpleEntry(FF.literal(1), SF.literal(new Color(  0,  0,159))); break;
                case  3 : expected = new AbstractMap.SimpleEntry(FF.literal(2), SF.literal(new Color( 50,  0,175))); break;
                case  4 : expected = new AbstractMap.SimpleEntry(FF.literal(3), SF.literal(new Color(  0, 12,191))); break;
                case  5 : expected = new AbstractMap.SimpleEntry(FF.literal(4), SF.literal(new Color(  0,  0,207))); break;
                case  6 : expected = new AbstractMap.SimpleEntry(FF.literal(5), SF.literal(new Color(  0,  0,223))); break;
                case  7 : expected = new AbstractMap.SimpleEntry(FF.literal(Double.NaN), SF.literal(new Color(  0,  0, 0, 0))); break;
                default : fail("Unexpected number of elements.");
            }
            assertEquals(expected, entry);
            i++;
        }
    }

    @Test
    public void readPAL() throws IOException {
        String palette =
            "0,0,143,\"5000 - 4500\"\n" +
            "0,0,244,\"4500 - 3500\"\n" +
            "236,255,34,\"3500 - 1000\"\n" +
            "255,89,0,\"1000 - 500\"\n" +
            "244,0,0,\"500 - 0\"\n" +
            "143,0,0,\"0\"";

        final ColorMap cm = new PaletteReader(PaletteReader.PATTERN_PAL).read(palette);
        assertTrue(cm.getFunction() instanceof Categorize);
        final Categorize categorize = (Categorize) cm.getFunction();
        final Map<Expression,Expression> steps = categorize.getThresholds();
        assertEquals(8, steps.size());
        int i=0;
        for (Entry<Expression,Expression> entry : steps.entrySet()) {
            Entry<Expression,Expression> expected = null;
            switch (i) {
                case  0 : expected = new AbstractMap.SimpleEntry(StyleConstants.CATEGORIZE_LESS_INFINITY, SF.literal(new Color(143,0,0))); break;
                case  1 : expected = new AbstractMap.SimpleEntry(FF.literal(   0), SF.literal(new Color(244,  0,  0))); break;
                case  2 : expected = new AbstractMap.SimpleEntry(FF.literal( 500), SF.literal(new Color(255, 89,  0))); break;
                case  3 : expected = new AbstractMap.SimpleEntry(FF.literal(1000), SF.literal(new Color(236,255, 34))); break;
                case  4 : expected = new AbstractMap.SimpleEntry(FF.literal(3500), SF.literal(new Color(  0,  0,244))); break;
                case  5 : expected = new AbstractMap.SimpleEntry(FF.literal(4500), SF.literal(new Color(  0,  0,143))); break;
                case  6 : expected = new AbstractMap.SimpleEntry(FF.literal(5000), SF.literal(new Color(0f,0f,0f,0f))); break;
                case  7 : expected = new AbstractMap.SimpleEntry(FF.literal(Double.NaN), SF.literal(new Color(  0,  0, 0, 0))); break;
                default : fail("Unexpected number of elements.");
            }
            assertEquals(expected, entry);
            i++;
        }
    }

}

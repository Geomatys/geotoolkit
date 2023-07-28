/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
import java.util.ArrayList;
import java.util.List;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.style.MutableStyleFactory;
import static org.geotoolkit.style.StyleConstants.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import org.opengis.style.Fill;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Mark;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.Stroke;
import org.opengis.style.StyleFactory;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (geomatys)
 */
public class CachedPointSymbolizerTest {

    private static final float DELTA = 0.0000001f;

    private static final FilterFactory FF = FilterUtilities.FF;
    private static final MutableStyleFactory SF = (MutableStyleFactory) DefaultFactories.forBuildin(StyleFactory.class);

    @Test
    public void testMargin() throws FactoryException{
        final GridGeometry grid = new GridGeometry(new GridExtent(1, 1), CRS.getDomainOfValidity(CommonCRS.WGS84.normalizedGeographic()), GridOrientation.HOMOTHETY);
        final RenderingContext2D ctx = new RenderingContext2D(grid, null);

        {// NO ANCHOR, NO DISPLACEMENT
            final List<GraphicalSymbol> symbols = new ArrayList<>();
            final Stroke stroke = SF.stroke(Color.BLACK, 2);
            final Fill fill = SF.fill(Color.RED);
            final Mark mark = SF.mark(MARK_CIRCLE, fill, stroke);
            symbols.add(mark);
            final Graphic graphic = SF.graphic(symbols, LITERAL_ONE_FLOAT, FF.literal(12), LITERAL_ONE_FLOAT, DEFAULT_ANCHOR_POINT, DEFAULT_DISPLACEMENT);
            final PointSymbolizer symbolizer = SF.pointSymbolizer("mySymbol",(String)null,DEFAULT_DESCRIPTION, Units.POINT, graphic);

            final CachedSymbolizer cached = GO2Utilities.getCached(symbolizer, null);
            final float margin = cached.getMargin(null, ctx);
            assertEquals(8f,margin,DELTA); // 12/2 + 2*2(stroke width)
        }

        {//NO ANCHOR
            final List<GraphicalSymbol> symbols = new ArrayList<>();
            final Stroke stroke = SF.stroke(Color.BLACK, 2);
            final Fill fill = SF.fill(Color.RED);
            final Mark mark = SF.mark(MARK_CIRCLE, fill, stroke);
            symbols.add(mark);
            final Graphic graphic = SF.graphic(symbols, LITERAL_ONE_FLOAT, FF.literal(12), LITERAL_ONE_FLOAT, DEFAULT_ANCHOR_POINT, SF.displacement(10, 15));
            final PointSymbolizer symbolizer = SF.pointSymbolizer("mySymbol",(String)null,DEFAULT_DESCRIPTION, Units.POINT, graphic);

            final CachedSymbolizer cached = GO2Utilities.getCached(symbolizer, null);
            final float margin = cached.getMargin(null, ctx);
            assertEquals(23f,margin,DELTA); // 12/2 + 2*2(stroke width) + 15(disp)
        }

        {
            final List<GraphicalSymbol> symbols = new ArrayList<>();
            final Stroke stroke = SF.stroke(Color.BLACK, 2);
            final Fill fill = SF.fill(Color.RED);
            final Mark mark = SF.mark(MARK_CIRCLE, fill, stroke);
            symbols.add(mark);
            final Graphic graphic = SF.graphic(symbols, LITERAL_ONE_FLOAT, FF.literal(12), LITERAL_ONE_FLOAT, SF.anchorPoint(0, 1.7), SF.displacement(10, 15));
            final PointSymbolizer symbolizer = SF.pointSymbolizer("mySymbol",(String)null,DEFAULT_DESCRIPTION, Units.POINT, graphic);

            final CachedSymbolizer cached = GO2Utilities.getCached(symbolizer, null);
            final float margin = cached.getMargin(null, ctx);
            assertEquals(23f+19.2f,margin,DELTA); // 12/2 + 2*2(stroke width) + 15(disp) + 16*(1.7-0.5)
        }

    }

}

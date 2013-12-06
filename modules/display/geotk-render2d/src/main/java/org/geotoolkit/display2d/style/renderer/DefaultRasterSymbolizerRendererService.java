/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.display2d.style.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedRasterSymbolizer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.function.Categorize;
import org.geotoolkit.style.function.Interpolate;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Jenks;
import org.geotoolkit.util.Converters;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.measure.Range;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.style.ColorMap;
import org.opengis.style.RasterSymbolizer;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultRasterSymbolizerRendererService extends AbstractSymbolizerRendererService<RasterSymbolizer, CachedRasterSymbolizer>{

    private static final int LEGEND_PALETTE_WIDTH = 30;
    private static final Font LEGEND_FONT = new Font(Font.SERIF, Font.BOLD, 12);

    @Override
    public boolean isGroupSymbolizer() {
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<RasterSymbolizer> getSymbolizerClass() {
        return RasterSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<CachedRasterSymbolizer> getCachedSymbolizerClass() {
        return CachedRasterSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CachedRasterSymbolizer createCachedSymbolizer(final RasterSymbolizer symbol) {
        return new CachedRasterSymbolizer(symbol,this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SymbolizerRenderer createRenderer(final CachedRasterSymbolizer symbol, final RenderingContext2D context) {
        return new DefaultRasterSymbolizerRenderer(this, symbol, context);
    }

    @Override
    public Rectangle2D glyphPreferredSize(CachedRasterSymbolizer symbol, MapLayer layer) {
        final Map<Object, Color> colorMap = getMapColor(symbol);

        if (colorMap.isEmpty()) {
            return super.glyphPreferredSize(symbol, layer);

        } else {
            final int mapLength = colorMap.size();
            int maxX = LEGEND_PALETTE_WIDTH;

            final BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            final FontMetrics fm = img.createGraphics().getFontMetrics(LEGEND_FONT);

            for (Object key : colorMap.keySet()) {
                int lineWidth = LEGEND_PALETTE_WIDTH + fm.stringWidth("< "+key.toString());
                maxX = Math.max(maxX, lineWidth);
            }

            int maxY = mapLength * fm.getHeight();
            return new Rectangle2D.Double(0, 0, maxX+5, maxY);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void glyph(final Graphics2D g, final Rectangle2D rectangle, final CachedRasterSymbolizer symbol, final MapLayer layer) {

        float[] fractions;
        Color[] colors;

        final ColorMap cm = symbol.getSource().getColorMap();

        //paint default Glyph
        if (cm == null || cm.getFunction() == null || ( !(cm.getFunction() instanceof Interpolate)
                && !(cm.getFunction() instanceof Jenks) && !(cm.getFunction() instanceof Categorize) )) {

            fractions = new float[] {0.0f, 0.5f, 1.0f};
            colors = new Color[] {Color.RED, Color.GREEN, Color.BLUE};

            final MultipleGradientPaint.CycleMethod cycleMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;

            final LinearGradientPaint paint = new LinearGradientPaint(
                new Point2D.Double(rectangle.getMinX(),rectangle.getMinY()),
                new Point2D.Double(rectangle.getMaxX(),rectangle.getMinY()),
                fractions,
                colors,
                cycleMethod
            );

            g.setPaint(paint);
            g.fill(rectangle);
            return;
        }

        //paint Interpolation, Categorize and Jenks Glyphs
        final Map<Object, Color> colorMap = getMapColor(symbol);

        if (!colorMap.isEmpty()) {
            boolean doInterpolation = true;
            if (colorMap.keySet().iterator().next() instanceof Range) {
                doInterpolation = false;
            }

            final int colorMapSize = colorMap.size();

            int fillHeight = Double.valueOf(rectangle.getHeight()).intValue();
            int intervalHeight = fillHeight / colorMapSize;

            Rectangle2D paintRectangle = new Rectangle((int) rectangle.getX(), (int) rectangle.getY(), LEGEND_PALETTE_WIDTH, fillHeight);

            g.setClip(rectangle);


            if (doInterpolation) {
                //fill color array
                colors = colorMap.values().toArray(new Color[colorMapSize]);

                //fill fraction array
                final float interval = 0.9f / colorMapSize;
                float fraction = 0.1f;
                fractions = new float[colorMapSize];
                for (int i = 0; i < colorMapSize; i++) {
                    fractions[i] = fraction;
                    fraction += interval;
                }

                //paint nothing
                if(colors.length == 0){
                    return;
                }

                //ensure we have at least 2 colors
                if(colors.length == 1){
                    colors = new Color[]{colors[0],colors[0]};
                    fractions = new float[]{fractions[0], 1.0f};
                }

                //create gradient
                final LinearGradientPaint paint = new LinearGradientPaint(
                    new Point2D.Double(paintRectangle.getMinX(),rectangle.getMinY()),
                    new Point2D.Double(paintRectangle.getMinX(),rectangle.getMaxY()),
                    fractions,
                    colors,
                    MultipleGradientPaint.CycleMethod.NO_CYCLE);
                g.setPaint(paint);
                g.fill(paintRectangle);
            } else {

                //paint all colors rectangles
                Collection<Color> colorsList = colorMap.values();
                int intX = Double.valueOf(rectangle.getMinX()).intValue();
                int intY = Double.valueOf(rectangle.getMinY()).intValue();

                for (Color color : colorsList) {
                    final Rectangle2D colorRect = new Rectangle(intX, intY, LEGEND_PALETTE_WIDTH, intervalHeight);
                    g.setPaint(color);
                    g.fill(colorRect);
                    intY += intervalHeight;
                }
            }

            //paint text
            float Y = Double.valueOf(rectangle.getMinY()).floatValue();
            float shift = doInterpolation ? 0.6f : 0.7f;

            g.setColor(Color.BLACK);
            for (Map.Entry<Object, Color> elem : colorMap.entrySet()) {
                final String text = "< "+elem.getKey().toString();
                g.drawString(text, LEGEND_PALETTE_WIDTH + 1f , Y + intervalHeight * shift );

                Y += intervalHeight;
            }
        }
    }

    /**
     * Create a map of object and colors from symbolizer colormap functions like
     * Interpolate, Jenks and Categorize.
     *
     * @param symbol CachedRaserSymbolizer
     * @return a Map containing Object like Range or String for key and Color as value.
     */
    private Map<Object, Color> getMapColor(final CachedRasterSymbolizer symbol) {
        Map<Object, Color> colorMap = new LinkedHashMap<Object, Color>();

        final ColorMap cm = symbol.getSource().getColorMap();

        if (cm != null && cm.getFunction() != null ) {
            final Function fct = cm.getFunction();
            if (fct instanceof Interpolate) {
                final Interpolate interpolate = (Interpolate) fct;
                final List<InterpolationPoint> points = interpolate.getInterpolationPoints();
                final int size = points.size();
                for(int i=0;i<size;i++){
                    final InterpolationPoint pt = points.get(i);
                    Color color = pt.getValue().evaluate(null, Color.class);
                    if(color == null){
                        color = Converters.convert(pt.getValue().toString(), Color.class);
                    }
                    colorMap.put(pt.getData().toString(), color);
                }

            } else if(fct instanceof Jenks) {
                final Jenks jenks = (Jenks) fct;
                final Map<Double, Color> jenksColorMap = jenks.getColorMap();
                final Map<Color, List<Double>> rangeJenksMap = new HashMap<Color, List<Double>>();

                for (Map.Entry<Double, Color> elem : jenksColorMap.entrySet()) {

                    if (rangeJenksMap.containsKey(elem.getValue())) {
                        final List<Double> values = (List<Double>)rangeJenksMap.get(elem.getValue());
                        values.add(elem.getKey());
                        Collections.sort(values);
                        rangeJenksMap.put(elem.getValue(), values);
                    } else {
                        final List<Double> values = new ArrayList<Double>();
                        values.add(elem.getKey());
                        rangeJenksMap.put(elem.getValue(), values);
                    }
                }

                //create range sorted map.
                colorMap = new TreeMap(new RangeComparator());
                for (Map.Entry<Color, List<Double>> elem : rangeJenksMap.entrySet()) {
                    final List<Double> values = elem.getValue();
                    Collections.sort(values);
                    colorMap.put(new NumberRange<Double>(Double.class, values.get(0), true, values.get(values.size()-1), true), elem.getKey());
                }

            } else if(fct instanceof Categorize) {
                final Categorize categorize = (Categorize) fct;
                final Map<Expression, Expression> thresholds = categorize.getThresholds();

                final Map<Color, List<Double>> colorValuesMap = new HashMap<Color, List<Double>>();

                for (Map.Entry<Expression, Expression> entry : thresholds.entrySet()) {

                    final Color currentColor = entry.getValue().evaluate(null, Color.class);
                    Double currentValue = Double.NEGATIVE_INFINITY;

                    if(!entry.getKey().equals(StyleConstants.CATEGORIZE_LESS_INFINITY)) {
                        Double value = (Double) entry.getKey().evaluate(null, Double.class);
                        if (value != null) {
                            currentValue = value;
                        }
                    }

                    if (currentColor != null) {
                        if (colorValuesMap.containsKey(currentColor)) {
                            final LinkedList<Double> values = (LinkedList<Double>)colorValuesMap.get(currentColor);
                            values.add(currentValue);
                            colorValuesMap.put(currentColor, values);
                        } else {
                            final LinkedList<Double> values = new LinkedList<Double>();
                            values.add(currentValue);
                            colorValuesMap.put(currentColor, values);
                        }
                    }
                }

                //create range sorted map.
                colorMap = new TreeMap(new RangeComparator());
                for (Map.Entry<Color, List<Double>> elem : colorValuesMap.entrySet()) {
                    final List<Double> values = elem.getValue();
                    Collections.sort(values);
                    colorMap.put(new NumberRange<Double>(Double.class, values.get(0), true, values.get(values.size()-1), true), elem.getKey());
                }
            }
        }

        return colorMap;
    }

    /**
     * Range comparator.
     */
    private class RangeComparator implements Comparator<Range> {

        @Override
        public int compare(Range o1, Range o2) {
            return o1.getMaxValue().compareTo(o2.getMinValue());
        }
    }

}

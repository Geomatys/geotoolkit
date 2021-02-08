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
import java.text.NumberFormat;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.math.DecimalFunctions;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedRasterSymbolizer;
import org.apache.sis.portrayal.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.function.Categorize;
import org.geotoolkit.style.function.Interpolate;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Jenks;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.measure.Range;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.style.ColorMap;
import org.opengis.style.RasterSymbolizer;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.logging.Logging;

/**
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class RasterSymbolizerRendererService extends AbstractSymbolizerRendererService<RasterSymbolizer, CachedRasterSymbolizer>{

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.display2d.style.renderer");

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
        return new RasterSymbolizerRenderer(this, symbol, context);
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

            Object[] keys = colorMap.keySet().toArray(new Object[colorMap.size()]);
            for (int i = 0; i < keys.length; i++) {
                final Object current = keys[i];
                final Object next = (i<keys.length - 1) ? keys[i+1] : null;
                final StringBuilder text = getLineText(current, next, null);
                int lineWidth = LEGEND_PALETTE_WIDTH + fm.stringWidth(text.toString());
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

            //find an appropriate number format without too much digits for this value range
            NumberFormat numFormat = null;
            if(doInterpolation){
                double min = Double.POSITIVE_INFINITY;
                double max = Double.NEGATIVE_INFINITY;
                for(Object o : colorMap.keySet()){
                    if(o instanceof String){
                        try{
                            o = Double.valueOf(o.toString().trim());
                        }catch(NumberFormatException ex){
                            continue;
                        }
                    }

                    if(o instanceof Number){
                        min = Math.min( ((Number)o).doubleValue(), min);
                        max = Math.max( ((Number)o).doubleValue(), max);
                    }
                }
                if(!Double.isInfinite(max)){
                    final double step = (max-min) / (colorMap.size()*10);
                    final int nbDigit = DecimalFunctions.fractionDigitsForDelta(step, false);
                    numFormat = NumberFormat.getNumberInstance();
                    numFormat.setMaximumFractionDigits(nbDigit);
                }
            }

            final int colorMapSize = colorMap.size();

            int fillHeight = Double.valueOf(rectangle.getHeight()).intValue();
            int intervalHeight = fillHeight / colorMapSize;

            Rectangle2D paintRectangle = new Rectangle((int) rectangle.getX(), (int) rectangle.getY(), LEGEND_PALETTE_WIDTH, fillHeight);

            g.setClip(rectangle);


            if (doInterpolation) {
                //fill color array
                colors = colorMap.values().toArray(new Color[colorMapSize]);
                //check we don't have any null colors
                for (int i = 0; i < colors.length; i++) {
                    if (colors[i] == null) {
                        colors[i] = new Color(0, 0, 0, 0);
                    }
                }

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
            Object[] keys = colorMap.keySet().toArray(new Object[colorMap.size()]);
            for (int i = 0; i < keys.length; i++) {
                final Object current = keys[i];
                final Object next = (i<keys.length - 1) ? keys[i+1] : null;
                final StringBuilder text = getLineText(current, next, numFormat);
                g.drawString(text.toString(), LEGEND_PALETTE_WIDTH + 1f , Y + intervalHeight * shift );

                Y += intervalHeight;
            }
        }
    }

    private static StringBuilder getLineText(Object currentElem, Object nextElement, NumberFormat numFormat) {
        final StringBuilder text = new StringBuilder(" < ");
        if (currentElem instanceof NumberRange) {
            double min = ((NumberRange) currentElem).getMaxDouble();
            double max = Double.POSITIVE_INFINITY;
            if (nextElement instanceof NumberRange) {
                max = ((NumberRange) nextElement).getMinDouble();
            }

            text.append('[');
            text.append(String.format("%.3f", min));
            text.append(" ... ");

            text.append(String.format("%.3f", max));
            text.append(']');
        } else if (numFormat != null) {
            if(currentElem instanceof String){
                try{
                    currentElem = Double.valueOf(currentElem.toString().trim());
                    text.append(numFormat.format(currentElem));
                }catch(NumberFormatException ex){
                    text.append(currentElem);
                }
            }else if(currentElem instanceof Number){
                text.append(numFormat.format(currentElem));
            }
        } else {
            text.append(currentElem);
        }
        return text;
    }

    /**
     * Create a map of object and colors from symbolizer colormap functions like
     * Interpolate, Jenks and Categorize.
     *
     * @param symbol CachedRaserSymbolizer
     * @return a Map containing Object like Range or String for key and Color as value.
     */
    private Map<Object, Color> getMapColor(final CachedRasterSymbolizer symbol) {
        Map<Object, Color> colorMap = new LinkedHashMap<>();

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
                    if(color == null) try {
                        color = ObjectConverters.convert(pt.getValue().toString(), Color.class);
                    } catch (UnconvertibleObjectException e) {
                        Logging.recoverableException(LOGGER, RasterSymbolizerRendererService.class, "getMapColor", e);
                        // TODO - do we really want to ignore?
                    }
                    colorMap.put(pt.getData().toString(), color);
                }

            } else if(fct instanceof Jenks) {
                final Jenks jenks = (Jenks) fct;
                final Map<Double, Color> jenksColorMap = jenks.getColorMap();
                final Map<Color, List<Double>> rangeJenksMap = new HashMap<>();

                for (Map.Entry<Double, Color> elem : jenksColorMap.entrySet()) {

                    if (rangeJenksMap.containsKey(elem.getValue())) {
                        final List<Double> values = rangeJenksMap.get(elem.getValue());
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
                    colorMap.put(new NumberRange<>(Double.class, values.get(0), true, values.get(values.size()-1), true), elem.getKey());
                }

            } else if(fct instanceof Categorize) {
                final Categorize categorize = (Categorize) fct;
                final Map<Expression, Expression> thresholds = categorize.getThresholds();

                final Map<Color, List<Double>> colorValuesMap = new HashMap<>();

                for (Map.Entry<Expression, Expression> entry : thresholds.entrySet()) {

                    final Color currentColor = entry.getValue().evaluate(null, Color.class);
                    Double currentValue = Double.NEGATIVE_INFINITY;

                    try {
                        Double value = entry.getKey().evaluate(null, Double.class);
                        if (value != null) {
                            currentValue = value;
                        }
                    } catch (Exception e) {
                        if (StyleConstants.CATEGORIZE_LESS_INFINITY.equals(entry.getKey())) {
                            currentValue = Double.NEGATIVE_INFINITY;
                        } else {
                            // Cannot read value, it's not a number, neither a "categorize less infinity".
                            LOGGER.log(Level.INFO, "A color map value cannot be evaluated. it will be ignored.\nCause : "+ e.getLocalizedMessage());
                            currentValue = null;
                        }
                    }

                    if (currentColor != null && currentValue != null) {
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
                    colorMap.put(new NumberRange<>(Double.class, values.get(0), true, values.get(values.size()-1), true), elem.getKey());
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

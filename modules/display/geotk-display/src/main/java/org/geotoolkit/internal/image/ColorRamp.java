/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.internal.image;

import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.AbstractMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitFormat;

import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.PaletteInterpretation;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.display.axis.Graduation;
import org.geotoolkit.display.axis.TickIterator;
import org.geotoolkit.display.axis.NumberGraduation;
import org.geotoolkit.display.axis.AbstractGraduation;
import org.geotoolkit.display.axis.LogarithmicNumberGraduation;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.referencing.operation.transform.ExponentialTransform1D;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Errors;


/**
 * Paints color ramps with a graduation. This class provides the implementation of
 * {@link org.geotoolkit.gui.swing.image.ColorRamp}. It has been factored out in order
 * to be leveraged in other modules without introducing a dependency to Swing widgets.
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @version 3.16
 *
 * @since 3.10 (derived from 1.1)
 * @module
 */
@SuppressWarnings("serial") // Used only for Swing serialization.
public class ColorRamp implements Serializable {
    /**
     * The default margin (in pixel) on each sides: top, left, right and bottom of the color ramp.
     * This margin is added in order to keep some space for the first and the last graduation label,
     * otherwise that graduation would be partially outside the color ramp area.
     */
    public static final int MARGIN = 10;

    /**
     * Small tolerance factor for rounding error.
     */
    private static final double EPS = 1E-6;

    /**
     * The locale for formatting error messages, or {@code null} for the default locale.
     */
    private Locale locale;

    /**
     * The graduation to write over the color ramp.
     */
    private Graduation graduation;

    /**
     * The object to use for creating the unit symbol.
     * This is created when first needed.
     */
    private transient UnitFormat unitFormat;

    /**
     * Graduation units. This is constructed from {@link Graduation#getUnit()} and cached
     * for faster rendering.
     */
    private String units;

    /**
     * The colors to paint as ARGB values (never {@code null}).
     */
    private int[] colors = ArraysExt.EMPTY_INT;

    /**
     * {@code true} if colors should be interpolated.
     *
     * @since 3.16
     */
    public boolean interpolationEnabled = true;

    /**
     * {@code true} if tick labels shall be painted.
     */
    public boolean labelVisibles = true;

    /**
     * {@code true} if tick labels can be painted with an automatic color. The
     * automatic color will be white or black depending on the background color.
     */
    public boolean autoForeground = true;

    /**
     * {@code true} if the color bar should be drawn horizontally,
     * or {@code false} if it should be drawn vertically.
     */
    private boolean horizontal = true;

    /**
     * Rendering hints for the graduation. This include the color bar
     * length, which is used for the space between ticks.
     */
    private transient RenderingHints hints;

    /**
     * The tick iterator used during the last painting. This iterator will be reused as mush
     * as possible in order to reduce garbage-collections.
     */
    private transient TickIterator reuse;

    /**
     * A temporary buffer for conversions from RGB to HSB
     * values. This is used by {@link #getForeground(int)}.
     */
    private transient float[] HSB;

    /**
     * Constructs an initially empty color ramp. Colors can be
     * set using one of the {@code setColors(...)} methods.
     */
    public ColorRamp() {
    }

    /**
     * Returns the locale to use for formatting error messages and graduation labels,
     * or {@code null} for the default locale.
     *
     * @return The locale to use for formatting error messages and graduation labels.
     *
     * @since 3.16
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the locale to use for formatting error messages and graduation labels.
     * As an alternative, users can override the {@link #getLocale()} method instead
     * than invoking this {@code setLocale(...)} method.
     *
     * @param locale The locale to use for formatting error messages and graduation labels.
     *
     * @since 3.16
     */
    public void setLocale(final Locale locale) {
        this.locale = locale;
        unitFormat = null;
    }

    /**
     * Returns {@code false} if the methods having a {@code Color[][]} return type are allowed
     * to return {@code null} unconditionally. This is more efficient for callers which are not
     * interested to fire property change events.
     * <p>
     * The default implementation returns {@code false} in every cases. Subclasses shall
     * override this method with a cheap test if they want to be informed about changes.
     *
     * @return Whatever the caller wants to be informed about color changes.
     */
    protected boolean reportColorChanges() {
        return false;
    }

    /**
     * Returns the graduation to paint over colors. If the graduation is
     * not yet defined, then this method returns {@code null}.
     *
     * @return The graduation to draw.
     */
    public final Graduation getGraduation() {
        return graduation;
    }

    /**
     * Sets the graduation to paint on top of the color bar.
     * The graduation minimum and maximum values should be both inclusive.
     *
     * @param  graduation The new graduation, or {@code null} if none.
     * @return The old graduation, or {@code null} if none.
     */
    public final Graduation setGraduation(final Graduation graduation) {
        final Graduation oldGraduation = this.graduation;
        this.graduation = graduation;
        units = null;
        if (graduation != null) {
            final Unit<?> unit = graduation.getUnit();
            if (unit != null) {
                if (unitFormat == null) {
                    unitFormat = (locale != null) ? UnitFormat.getInstance(locale) : UnitFormat.getInstance();
                }
                units = unitFormat.format(unit);
            }
        }
        return oldGraduation;
    }

    /**
     * Sets the graduation to the given range of values. The {@code sampleToGeophysics} argument
     * is used in order to determine whatever the scale is linear or logarithmic.
     *
     * @param range The range of values, or {@code null} for removing the graduation.
     * @param sampleToGeophysics The <cite>sample to geophysics</cite> transform.
     *
     * @since 3.16
     */
    public final void setGraduation(final MeasurementRange<?> range, final MathTransform1D sampleToGeophysics) {
        setGraduation(range != null ? createDefaultGraduation(graduation, sampleToGeophysics,
                range.getMinimum(), range.getMaximum(), range.getUnits(), getLocale()) : null);
    }

    /**
     * Returns {@code true} if some colors are defined.
     *
     * @return {@code true} if some colors are defined.
     */
    public final boolean hasColors() {
        return colors != null;
    }

    /**
     * Returns the colors painted by this {@code ColorRamp}.
     *
     * @return The colors (never {@code null}).
     */
    public final Color[] getColors() {
        return getColors(colors, new HashMap<Integer,Color>());
    }

    /**
     * Creates an array of {@link Color} values from the given array of ARGB values.
     *
     * @param  ARGB  The array of ARGB values.
     * @param  share A map of {@link Color} instances previously created, or an empty map if none.
     * @return The array of color instances.
     */
    private static Color[] getColors(final int[] ARGB, final Map<Integer,Color> share) {
        final Color[] colors = new Color[ARGB.length];
        for (int i=0; i<colors.length; i++) {
            final Integer value = ARGB[i];
            Color ci = share.get(value);
            if (ci == null) {
                ci = new Color(value, true);
                share.put(value, ci);
            }
            colors[i] = ci;
        }
        return colors;
    }

    /**
     * Sets the colors to paint.
     *
     * @param  colors The colors to paint, or {@code null} if none.
     * @return The old and new colors, or {@code null} if there is no change.
     */
    public final Color[][] setColors(final Color... colors) {
        final Map<Integer,Color> share = new HashMap<>();
        int[] ARGB = null;
        if (colors != null) {
            ARGB = new int[colors.length];
            for (int i=0; i<colors.length; i++) {
                final Color c = colors[i];
                share.put(ARGB[i] = c.getRGB(), c);
            }
        }
        return setColors(ARGB, share);
    }

    /**
     * Sets the colors to paint as an array of ARGB values. This method is the most
     * efficient one if the colors were already available as an array of ARGB values.
     *
     * @param  colors The colors to paint, or {@code null} if none.
     * @return The old and new colors, or {@code null} if there is no change.
     */
    public final Color[][] setColors(final int... colors) {
        return setColors((colors != null) ? colors.clone() : null, null);
    }

    /**
     * Sets the colors to paint as an array of ARGB values.
     *
     * @param  newColors The colors to paint, or {@code null} if none.
     * @param  share A map of {@link Color} instances previously created, or {@code null} if none.
     * @return The old and new colors, or {@code null} if there is no change.
     */
    private Color[][] setColors(int[] newColors, Map<Integer,Color> share) {
        if (newColors == null) {
            newColors = ArraysExt.EMPTY_INT;
        }
        final int[] oldColors = colors;
        colors = newColors;
        if (!reportColorChanges() || Arrays.equals(oldColors, newColors)) {
            return null;
        }
        if (share == null) {
            share = new HashMap<>();
        }
        return new Color[][] {getColors(oldColors, share), getColors(newColors, share)};
    }

    /**
     * Sets the graduation and the colors from a sample dimension.
     *
     * @param band The sample dimension, or {@code null} if none.
     */
    public final void setColors(final SampleDimension band) {
        final Map.Entry<Graduation,Color[]> entry = getColors(band);
        setGraduation(entry.getKey());
        setColors(entry.getValue());
    }

    /**
     * Returns the graduation and the colors from a sample dimension. This is caller
     * responsibility to invoke {@code setColors} and {@code setGraduation} with the
     * returned values.
     *
     * @param  band The sample dimension, or {@code null} if none.
     * @return The pair of graduation and colors.
     */
    @SuppressWarnings("fallthrough")
    public final Map.Entry<Graduation,Color[]> getColors(SampleDimension band) {
        Color[] colors = null;
        Graduation graduation = null;
        /*
         * Gets the color palette, preferably from the "non-geophysics" view since it is usually
         * the one backed by an IndexColorModel.  We assume that 'palette[i]' gives the color of
         * sample value 'i'. We will search for the largest range of valid sample integer values,
         * ignoring "nodata" values. Those "nodata" values appear usually at the beginning or at
         * the end of the whole palette range.
         *
         * Note that the above algorithm works without Category. We try to avoid dependency
         * on categories because some applications don't use them. TODO: should we use this
         * algorithm only as a fallback (i.e. use categories when available)?
         */
        if (band != null) {
            if (band instanceof GridSampleDimension) {
                band = ((GridSampleDimension) band).geophysics(false);
            }
            final int[][] palette = band.getPalette();
            if (palette != null) {
                int lower = 0; // Will be inclusive
                int upper = 0; // Will be exclusive
                final double[] nodata = band.getNoDataValues();
                final double[] sorted = new double[nodata!=null ? nodata.length + 2 : 2];
                sorted[0] = -1;
                sorted[sorted.length - 1] = palette.length;
                if (nodata != null) {
                    System.arraycopy(nodata, 0, sorted, 1, nodata.length);
                }
                Arrays.sort(sorted);
                for (int i=1; i<sorted.length; i++) {
                    // Note: Don't cast to integer now, because we
                    // want to take NaN and infinity in account.
                    final double lo = Math.floor(sorted[i-1])+1; // "Nodata" always excluded
                    final double hi = Math.ceil (sorted[i  ]);   // "Nodata" included if integer
                    if (lo>=0 && hi<=palette.length && (hi-lo)>(upper-lower)) {
                        lower = (int) lo;
                        upper = (int) hi;
                    }
                }
                /*
                 * We now know the range of values to show on the palette. Creates the colors from
                 * the palette. Only palette using RGB colors are understood at this time, but the
                 * graduation (after this block) is still created for all kind of palette.
                 */
                if (PaletteInterpretation.RGB.equals(band.getPaletteInterpretation())) {
                    colors = new Color[upper - lower];
                    for (int i=0; i<colors.length; i++) {
                        int r=0, g=0, b=0, a=255;
                        final int[] c = palette[i+lower];
                        if (c != null) switch (c.length) {
                            default:        // Fall through
                            case 4: a=c[3]; // Fall through
                            case 3: b=c[2]; // Fall through
                            case 2: g=c[1]; // Fall through
                            case 1: r=c[0]; // Fall through
                            case 0: break;
                        }
                        colors[i] = new Color(r,g,b,a);
                    }
                }
                /*
                 * Transforms the lower and upper sample values into minimum and maximum geophysics
                 * values and creates the graduation. Note that the maximum value will be inclusive,
                 * at the difference of upper value which was exclusive prior this point.
                 */
                if (upper > lower) {
                    upper--; // Make it inclusive.
                }
                double min, max;
                try {
                    final MathTransform1D tr = band.getSampleToGeophysics();
                    min = tr.transform(lower);
                    max = tr.transform(upper);
                } catch (TransformException cause) {
                    throw new IllegalArgumentException(illegalBand(band), cause);
                }
                if (min > max) {
                    // This case occurs typically when displaying a color ramp for
                    // sea bathymetry, for which floor level are negative numbers.
                    min = -min;
                    max = -max;
                }
                if (!(min <= max)) {
                    // This case occurs if one or both values is NaN.
                    throw new IllegalArgumentException(illegalBand(band));
                }
                graduation = createGraduation(this.graduation, band, min, max);
            }
        }
        return new AbstractMap.SimpleEntry<>(graduation, colors);
    }

    /**
     * Formats an error message for an illegal sample dimension.
     */
    private String illegalBand(final SampleDimension band) {
        return Errors.getResources(getLocale()).getString(Errors.Keys.ILLEGAL_ARGUMENT_$2, "band", band);
    }

    /**
     * Returns the component's orientation (horizontal or vertical). It should be one of the
     * following constants: {@link SwingConstants#HORIZONTAL} or {@link SwingConstants#VERTICAL}.
     *
     * @return The component orientation.
     */
    public final int getOrientation() {
        return (horizontal) ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL;
    }

    /**
     * Sets the component's orientation (horizontal or vertical).
     *
     * @param orient {@link SwingConstants#HORIZONTAL} or {@link SwingConstants#VERTICAL}.
     * @return The old orientation.
     */
    public final int setOrientation(final int orient) {
        final int old = getOrientation();
        switch (orient) {
            case SwingConstants.HORIZONTAL: horizontal=true;  break;
            case SwingConstants.VERTICAL:   horizontal=false; break;
            default: throw new IllegalArgumentException(String.valueOf(orient));
        }
        return old;
    }

    /**
     * Returns a color for label at the specified index. The default color will be
     * black or white, depending of the background color at the specified index.
     */
    private Color getForeground(final int colorIndex) {
        final int color = colors[colorIndex];
        final int R = ((color >>> 16) & 0xFF);
        final int G = ((color >>>  8) & 0xFF);
        final int B = ( color         & 0xFF);
        HSB = Color.RGBtoHSB(R, G, B, HSB);
        return (HSB[2] >= 0.5f) ? Color.BLACK : Color.WHITE;
    }

    /**
     * Paints the color ramp. This method doesn't need to restore
     * {@link Graphics2D} to its initial state once finished.
     *
     * @param  graphics   The graphic context in which to paint.
     * @param  bounds     The bounding box where to paint the color ramp.
     * @param  font       The font to use for the label, or {@code null} for a default font.
     * @param  foreground The color to use for label, or {@code null} for a default color.
     * @return box of graduation labels (NOT taking in account the color ramp behind them),
     *         or {@code null} if no label has been painted.
     */
    public final Rectangle2D paint(final Graphics2D graphics, final Rectangle bounds, Font font, Color foreground) {
        final int margin = labelVisibles ? MARGIN : 0;
        final int[] colors = this.colors;
        final int length = colors.length;
        final double dx, dy;
        if (length == 0) {
            dx = 0;
            dy = 0;
        } else {
            final boolean interpolate = interpolationEnabled && length >= 2 &&
                    (horizontal ? bounds.width : bounds.height) >= length;
            final int numSteps = interpolate ? length-1 : length;
            dx = (bounds.width  - 2*margin) / (double) numSteps;
            dy = (bounds.height - 2*margin) / (double) numSteps;
            boolean paintedMargin = false;
            int lastIndex = 0;
            int thisARGB  = colors[0];
            int nextARGB  = thisARGB;
            Color thisColor = new Color(thisARGB, true);
            final int ox = bounds.x + margin;
            final int oy = bounds.y + bounds.height - margin;
            final Rectangle2D.Double rect = new Rectangle2D.Double();
            rect.setRect(bounds);
            for (int i=0; i<=length; i++) {
                if (i != length) {
                    nextARGB = colors[i];
                    if (nextARGB == thisARGB && !interpolate) {
                        continue;
                    }
                }
                if (horizontal) {
                    rect.x      = ox + dx*lastIndex;
                    rect.width  = dx * (i-lastIndex);
                    if (!paintedMargin) {
                        rect.x     -= margin;
                        rect.width += margin;
                        paintedMargin = true;
                    }
                    if (i == length) {
                        if (interpolate) {
                            rect.width = margin;
                        } else {
                            rect.width += margin;
                        }
                    }
                } else {
                    rect.y      = oy - dy*i;
                    rect.height = dy * (i-lastIndex);
                    if (!paintedMargin) {
                        rect.height += margin;
                        paintedMargin = true;
                    }
                    if (i == length) {
                        if (interpolate) {
                            rect.y      = 0;
                            rect.height = margin;
                        } else {
                            rect.y      -= margin;
                            rect.height += margin;
                        }
                    }
                }
                final Color nextColor = new Color(nextARGB, true);
                if (interpolate && thisARGB != nextARGB) {
                    final double x1, y1, x2, y2;
                    if (horizontal) {
                        x1 = rect.getMinX();
                        x2 = rect.getMaxX();
                        y1 = y2 = rect.getCenterY();
                    } else {
                        y1 = rect.getMaxY();
                        y2 = rect.getMinY();
                        x1 = x2 = rect.getCenterX();
                    }
                    graphics.setPaint(new GradientPaint(
                            (float) x1, (float) y1, thisColor,
                            (float) x2, (float) y2, nextColor));
                } else {
                    graphics.setColor(thisColor);
                }
                graphics.fill(rect);
                lastIndex = i;
                thisARGB  = nextARGB;
                thisColor = nextColor;
            }
        }
        Rectangle2D labelBounds = null;
        if (labelVisibles && graduation!=null) {
            /*
             * Prepares graduation writing. First, computes the color ramp width in pixels.
             * Then, computes the coefficients for conversion of graduation values to pixel
             * coordinates.
             */
            double x = bounds.getCenterX();
            double y = bounds.getCenterY();
            final double axisRange   = graduation.getSpan();
            final double axisMinimum = graduation.getMinimum();
            final double visualLength, scale, offset;
            if (horizontal) {
                visualLength = bounds.getWidth() - 2*margin - dx;
                scale        = visualLength / axisRange;
                offset       = (bounds.getMinX() + margin + 0.5*dx) - scale*axisMinimum;
            } else {
                visualLength = bounds.getHeight() - 2*margin - dy;
                scale        = -visualLength / axisRange;
                offset       = (bounds.getMaxY() - margin - 0.5*dy) + scale*axisMinimum;
            }
            if (hints == null) {
                hints = new RenderingHints(null);
            }
            final double valueToLocation = length / axisRange;
            if (font == null) {
                font = Font.decode("SansSerif-10");
            }
            final FontRenderContext context = graphics.getFontRenderContext();
            hints.put(Graduation.VISUAL_AXIS_LENGTH, (float) visualLength);
            if (foreground == null) {
                foreground = Color.BLACK;
            }
            graphics.setColor(foreground);
            /*
             * Now write the graduation.
             */
            final TickIterator ticks = graduation.getTickIterator(hints, reuse);
            for (reuse=ticks; !ticks.isDone(); ticks.nextMajor()) {
                if (ticks.isMajorTick()) {
                    final GlyphVector glyph = font.createGlyphVector(context, ticks.currentLabel());
                    final Rectangle2D rectg = glyph.getVisualBounds();
                    final double      width = rectg.getWidth();
                    final double     height = rectg.getHeight();
                    final double      value = ticks.currentPosition();
                    final double   position = value*scale + offset;
                    final int    colorIndex = Math.min(Math.max((int) Math.round(
                                              (value - axisMinimum)*valueToLocation),0), length-1);
                    if (horizontal) x = position;
                    else            y = position;
                    rectg.setRect(x - 0.5*width, y - 0.5*height, width, height);
                    if (autoForeground) {
                        graphics.setColor(getForeground(colorIndex));
                    }
                    graphics.drawGlyphVector(glyph, (float)rectg.getMinX(), (float)rectg.getMaxY());
                    if (labelBounds != null) {
                        labelBounds.add(rectg);
                    } else {
                        labelBounds = rectg;
                    }
                }
            }
            /*
             * Writes units.
             */
            if (units != null) {
                final GlyphVector glyph = font.createGlyphVector(context, units);
                final Rectangle2D rectg = glyph.getVisualBounds();
                final double      width = rectg.getWidth();
                final double     height = rectg.getHeight();
                if (horizontal) {
                    double left = bounds.getMaxX() - width;
                    if (labelBounds != null) {
                        final double check = labelBounds.getMaxX() + 4;
                        if (check < left) {
                            left = check;
                        }
                    }
                    rectg.setRect(left, y - 0.5*height, width, height);
                } else {
                    rectg.setRect(x - 0.5*width, bounds.getMinY() + height, width, height);
                }
                if (autoForeground) {
                    graphics.setColor(getForeground(length-1));
                }
                if (labelBounds==null || !labelBounds.intersects(rectg)) {
                    graphics.drawGlyphVector(glyph, (float)rectg.getMinX(), (float)rectg.getMaxY());
                }
            }
        }
        return labelBounds;
    }

    /**
     * Returns a graduation for the specified sample dimension, minimum and maximum values. If
     * the supplied {@code reuse} object is non-null and is of the appropriate class, then this
     * method can return {@code reuse} without creating a new graduation object. Otherwise this
     * method must returns a graduation of the appropriate class, usually {@link NumberGraduation}
     * or {@link LogarithmicNumberGraduation}.
     * <p>
     * In every cases, this method must set graduations
     * {@linkplain AbstractGraduation#setMinimum minimum},
     * {@linkplain AbstractGraduation#setMaximum maximum} and
     * {@linkplain AbstractGraduation#setUnit unit} according the values given in arguments.
     *
     * @param  reuse   The graduation to reuse if possible.
     * @param  band    The sample dimension to create graduation for.
     * @param  minimum The minimal geophysics value to appear in the graduation.
     * @param  maximum The maximal geophysics value to appear in the graduation.
     * @return A graduation for the supplied sample dimension.
     */
    protected Graduation createGraduation(final Graduation reuse, final SampleDimension band,
                                          final double minimum, final double maximum)
    {
        return createDefaultGraduation(reuse, band.getSampleToGeophysics(),
                minimum, maximum, band.getUnits(), getLocale());
    }

    /**
     * Default implementation of {@code createGraduation}.
     *
     * @param  reuse   The graduation to reuse if possible.
     * @param  tr      The <cite>sample to geophysics</cite> transform.
     * @param  minimum The minimal geophysics value to appear in the graduation.
     * @param  maximum The maximal geophysics value to appear in the graduation.
     * @param  units   The units of the minimal and maximal values.
     * @param  locale  The locale, or {@code null} for the default locale.
     * @return A graduation for the supplied sample dimension.
     */
    public static Graduation createDefaultGraduation(final Graduation reuse, MathTransform1D tr,
            final double minimum, final double maximum, final Unit<?> units, final Locale locale)
    {
        AbstractGraduation graduation = (reuse instanceof AbstractGraduation) ?
                (AbstractGraduation) reuse : null;
        if (tr instanceof LinearTransform) {
            if (graduation == null || graduation.getClass() != NumberGraduation.class) {
                graduation = new NumberGraduation(units);
            }
        } else if (tr instanceof ExponentialTransform1D) { // The *inverse* of 'tr' is logarithmic.
            if (graduation == null || graduation.getClass() != LogarithmicNumberGraduation.class) {
                graduation = new LogarithmicNumberGraduation(units);
            }
        } else {
            final Logger logger = Logging.getLogger("org.geotoolkit.image");
            final LogRecord record = Loggings.getResources(locale).getLogRecord(Level.WARNING,
                    Loggings.Keys.UNRECOGNIZED_SCALE_TYPE_$1, Classes.getShortClassName(tr));
            record.setLoggerName(logger.getName());
            logger.log(record);
            graduation = new NumberGraduation(units);
        }
        if (locale != null) {
            graduation.setLocale(locale);
        }
        if (graduation == reuse) {
            graduation.setUnit(units);
        }
        graduation.setMinimum(minimum);
        graduation.setMaximum(maximum);
        return graduation;
    }

    /**
     * Paints a color ramp for the given range of values. This convenience method is provided
     * mostly for {@link org.geotoolkit.coverage.sql.LayerEntry#getColorRamp} implementation,
     * which invoke this method through reflection (not directly in order to avoid a direct
     * dependencies of {@code geotk-coverage-sql} toward {@code geotk-display}.
     * <p>
     * See {@link org.geotoolkit.coverage.sql.Layer#getColorRamp(int, MeasurementRange, Map)}
     * for a description of the expected content of the {@code properties} map.
     *
     * @param  range The range for the graduation, or {@code null} if no graduation should
     *         be written.
     * @param  properties An optional map of properties controlling the rendering.
     * @param  colors The colors to use in the color ramp.
     * @param  sampleToGeophysics The <cite>sample to geophysics</cite> transform,
     *         used in order to determine if the graduation is linear or logarithmic.
     * @param  locale The locale to use for formatting labels.
     * @return The color ramp as an image, or {@code null} if none.
     * @throws IllegalArgumentException If the units of the given range are incompatible
     *         with the units of measurement found in this layer.
     * @throws CoverageStoreException If an error occurred while creating the color ramp.
     *
     * @see org.geotoolkit.coverage.sql.LayerEntry#getColorRamp(int, MeasurementRange, Map)
     *
     * @since 3.16
     */
    public static BufferedImage paint(final MeasurementRange<?> range, final Color[] colors,
            final MathTransform1D sampleToGeophysics, final Locale locale, final Map<String,?> properties)
    {
        Dimension  size     = null;
        Font       font     = null;
        Color      color    = null;
        Graphics2D graphics = null;
        if (properties != null) {
            size     = (Dimension)  properties.get("size");
            font     = (Font)       properties.get("font");
            color    = (Color)      properties.get("foreground");
            graphics = (Graphics2D) properties.get("graphics");
        }
        if (size == null) {
            size = new Dimension(400, 20);
        }
        final ColorRamp cr = new ColorRamp();
        cr.labelVisibles = (range != null);
        cr.setLocale(locale);
        cr.setColors(colors);
        cr.setGraduation(range, sampleToGeophysics);
        if (graphics != null) {
            cr.paint(graphics, new Rectangle(size), font, color);
            return null;
        }
        return cr.toImage(size.width, size.height, font, color);
    }

    /**
     * Returns an image representation for this color ramp.
     *
     * @param  width      The image width.
     * @param  height     The image height.
     * @param  font       The font to use for the label, or {@code null} for a default font.
     * @param  foreground The color to use for label, or {@code null} for a default color.
     * @return The color ramp as a buffered image.
     */
    public final BufferedImage toImage(final int width, final int height, final Font font, final Color foreground) {
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = image.createGraphics();
        paint(graphics, new Rectangle(image.getWidth(), image.getHeight()), font, foreground);
        graphics.dispose();
        return image;
    }

    /**
     * Returns a string representation for this color ramp.
     *
     * @param caller The caller class.
     * @return A string representation of the color ramp.
     */
    public final String toString(final Class<?> caller) {
        final int[] colors = this.colors;
        int count = 0;
        int i = 0;
        if (i < colors.length) {
            int last = colors[i];
            while (++i < colors.length) {
                int c = colors[i];
                if (c != last) {
                    last = c;
                    count++;
                }
            }
        }
        return Classes.getShortName(caller) + '[' + count + " colors]";
    }

    /**
     * Returns a string representation for this color ramp.
     */
    @Override
    public final String toString() {
        return toString(getClass());
    }
}

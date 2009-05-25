/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.gui.swing.image;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.measure.unit.Unit;

import org.opengis.coverage.Coverage;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.PaletteInterpretation;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.display.axis.Graduation;
import org.geotoolkit.display.axis.TickIterator;
import org.geotoolkit.display.axis.NumberGraduation;
import org.geotoolkit.display.axis.AbstractGraduation;
import org.geotoolkit.display.axis.LogarithmicNumberGraduation;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Errors;


/**
 * A color ramp with a graduation. The colors can be specified with a {@link SampleDimension},
 * an array of {@link Color}s or an {@link IndexColorModel} object, and the graduation is
 * specified with a {@link Graduation} object. The resulting {@code ColorRamp} object
 * is usually painted together with a remote sensing image.
 *
 * <p>&nbsp;</p>
 * <p align="center"><img src="doc-files/ColorRamp.png"></p>
 * <p>&nbsp;</p>
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 1.1
 * @module
 */
@SuppressWarnings("serial")
public class ColorRamp extends JComponent {
    /**
     * Margin (in pixel) on each sides: top, left, right and bottom of the color ramp.
     */
    private static final int MARGIN = 10;

    /**
     * An empty list of colors.
     */
    private static final Color[] EMPTY = new Color[0];

    /**
     * The graduation to write over the color ramp.
     */
    private Graduation graduation;

    /**
     * Graduation units. This is constructed from {@link Graduation#getUnit} and cached
     * for faster rendering.
     */
    private String units;

    /**
     * The colors to paint (never {@code null}).
     */
    private Color[] colors = EMPTY;

    /**
     * {@code true} if tick label must be display.
     */
    private boolean labelVisibles = true;

    /**
     * {@code true} if tick label can be display with an automatic color. The
     * automatic color will be white or black depending the background color.
     */
    private boolean autoForeground = true;

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
     * The {@link ComponentUI} object for computing preferred
     * size, drawn the component and handle some events.
     */
    private final UI ui = new UI();

    /**
     * Constructs an initially empty color ramp. Colors can be
     * set using one of the {@code setColors(...)} methods.
     */
    public ColorRamp() {
        setOpaque(true);
        setUI(ui);
    }

    /**
     * Constructs a color ramp for the specified coverage.
     *
     * @param coverage The coverage for which to create a color ramp.
     */
    public ColorRamp(final Coverage coverage) {
        this();
        setColors(coverage);
    }

    /**
     * Returns the graduation to paint over colors. If the graduation is
     * not yet defined, then this method returns {@code null}.
     *
     * @return The graduation to draw.
     */
    public Graduation getGraduation() {
        return graduation;
    }

    /**
     * Sets the graduation to paint on top of the color bar. The graduation can be set also
     * by a call to {@link #setColors(SampleDimension)} and {@link #setColors(Coverage)}.
     * This method will fire a property change event with the {@code "graduation"} name.
     * <p>
     * The graduation minimum and maximum values should be both inclusive.
     *
     * @param  graduation The new graduation, or {@code null} if none.
     * @return {@code true} if this object changed as a result of this call.
     */
    public boolean setGraduation(final Graduation graduation) {
        final Graduation oldGraduation = this.graduation;
        if (graduation != oldGraduation) {
            if (oldGraduation != null) {
                oldGraduation.removePropertyChangeListener(ui);
            }
            if (graduation != null) {
                graduation.addPropertyChangeListener(ui);
            }
            this.graduation = graduation;
            units = null;
            if (graduation != null) {
                final Unit<?> unit = graduation.getUnit();
                if (unit != null) {
                    units = unit.toString();
                }
            }
        }
        final boolean changed = !Utilities.equals(graduation, oldGraduation);
        if (changed) {
            repaint();
        }
        firePropertyChange("graduation", oldGraduation, graduation);
        return changed;
    }

    /**
     * Returns the colors painted by this {@code ColorRamp}.
     *
     * @return The colors (never {@code null}).
     */
    public Color[] getColors() {
        return (colors.length!=0) ? colors.clone() : colors;
    }

    /**
     * Sets the colors to paint. This method fires a property change event
     * with the {@code "colors"} name.
     *
     * @param  colors The colors to paint.
     * @return {@code true} if the state of this {@code ColorRamp} changed as a result of this call.
     *
     * @see #setColors(Coverage)
     * @see #setColors(SampleDimension)
     * @see #setColors(IndexColorModel)
     * @see #getColors()
     * @see #getGraduation()
     */
    public boolean setColors(final Color[] colors) {
        final Color[] oldColors = this.colors;
        this.colors = (colors!=null && colors.length!=0) ? colors.clone() : EMPTY;
        final boolean changed = !Arrays.equals(oldColors, this.colors);
        if (changed) {
            repaint();
        }
        firePropertyChange("colors", oldColors, colors);
        return changed;
    }

    /**
     * Sets the colors to paint from an {@link IndexColorModel}. The default implementation
     * fetches the colors from the index color model and invokes {@link #setColors(Color[])}.
     *
     * @param  model The colors to paint.
     * @return {@code true} if the state of this {@code ColorRamp} changed as a result of this call.
     *
     * @see #setColors(Coverage)
     * @see #setColors(SampleDimension)
     * @see #setColors(Color[])
     * @see #getColors()
     * @see #getGraduation()
     */
    public boolean setColors(final IndexColorModel model) {
        final Color[] colors;
        if (model == null) {
            colors = EMPTY;
        } else {
            colors = new Color[model.getMapSize()];
            for (int i=0; i<colors.length; i++) {
                colors[i] = new Color(model.getRed  (i),
                                      model.getGreen(i),
                                      model.getBlue (i),
                                      model.getAlpha(i));
            }
        }
        return setColors(colors);
    }

    /**
     * Sets the graduation and the colors from a sample dimension.
     * The default implementation fetches the palette and the minimum and maximum values
     * from the supplied band, and then invokes {@link #setColors(Color[]) setColors} and
     * {@link #setGraduation setGraduation}.
     *
     * @param  band The sample dimension, or {@code null}.
     * @return {@code true} if the state of this {@code ColorRamp} changed as a result of this call.
     *
     * @see #setColors(Coverage)
     * @see #setColors(SampleDimension)
     * @see #setColors(IndexColorModel)
     * @see #setColors(Color[])
     * @see #getColors()
     * @see #getGraduation()
     */
    @SuppressWarnings("fallthrough")
    public boolean setColors(SampleDimension band) {
        Color[] colors = EMPTY;
        Graduation graduation = null;
        /*
         * Gets the color palette, preferably from the "non-geophysics" view since it is usually
         * the one backed by an IndexColorModel.  We assume that 'palette[i]' gives the color of
         * sample value 'i'. We will search for the largest range of valid sample integer values,
         * ignoring "nodata" values. Those "nodata" values appear usually at the begining or at
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
                        if (c!=null) switch (c.length) {
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
                    throw new IllegalArgumentException(Errors.format(
                            Errors.Keys.ILLEGAL_ARGUMENT_$2, "band", band), cause);
                }
                if (min > max) {
                    // This case occurs typically when displaying a color ramp for
                    // sea bathymetry, for which floor level are negative numbers.
                    min = -min;
                    max = -max;
                }
                if (!(min <= max)) {
                    // This case occurs if one or both values is NaN.
                    throw new IllegalArgumentException(Errors.format(
                            Errors.Keys.ILLEGAL_ARGUMENT_$2, "band", band));
                }
                graduation = createGraduation(this.graduation, band, min, max);
            }
        }
        return setGraduation(graduation) | setColors(colors); // Realy |, not ||
    }

    /**
     * Sets the graduation and the colors from a coverage. The default implementation
     * fetches the visible sample dimension from the specified coverage, and then invokes
     * {@link #setColors(Color[]) setColors} and {@link #setGraduation setGraduation}.
     *
     * @param coverage The coverage, or {@code null}.
     * @return {@code true} if the state of this {@code ColorRamp} changed as a result of this call.
     *
     * @see #setColors(IndexColorModel)
     * @see #setColors(SampleDimension)
     * @see #getColors()
     * @see #getGraduation()
     */
    public boolean setColors(final Coverage coverage) {
        SampleDimension band = null;
        if (coverage != null) {
            band = coverage.getSampleDimension(CoverageUtilities.getVisibleBand(band));
        }
        return setColors(band);
    }

    /**
     * Returns the component's orientation (horizontal or vertical). It should be one of the
     * following constants: {@link SwingConstants#HORIZONTAL} or {@link SwingConstants#VERTICAL}.
     *
     * @return The component orientation.
     */
    public int getOrientation() {
        return (horizontal) ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL;
    }

    /**
     * Sets the component's orientation (horizontal or vertical).
     *
     * @param orient {@link SwingConstants#HORIZONTAL} or {@link SwingConstants#VERTICAL}.
     */
    public void setOrientation(final int orient) {
        switch (orient) {
            case SwingConstants.HORIZONTAL: horizontal=true;  break;
            case SwingConstants.VERTICAL:   horizontal=false; break;
            default: throw new IllegalArgumentException(String.valueOf(orient));
        }
    }

    /**
     * Tests if graduation labels are paint on top of the
     * colors ramp. Default value is {@code true}.
     *
     * @return {@code true} if graduation labels are drawn.
     */
    public boolean isLabelVisibles() {
        return labelVisibles;
    }

    /**
     * Sets whatever the graduation labels should be painted on top of the colors ramp.
     *
     * @param visible {@code true} if graduation labels should be drawn.
     */
    public void setLabelVisibles(final boolean visible) {
        labelVisibles = visible;
    }

    /**
     * Sets the label colors. A {@code null} value reset the automatic color.
     *
     * @param color The new label color, or {@code null} for the default.
     *
     * @see #getForeground
     */
    @Override
    public void setForeground(final Color color) {
        super.setForeground(color);
        autoForeground = (color==null);
    }

    /**
     * Returns a color for label at the specified index. The default color will be
     * black or white, depending of the background color at the specified index.
     */
    private Color getForeground(final int colorIndex) {
        final Color color = colors[colorIndex];
        HSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), HSB);
        return (HSB[2] >= 0.5f) ? Color.BLACK : Color.WHITE;
    }

    /**
     * Paints the color ramp. This method doesn't need to restore
     * {@link Graphics2D} to its initial state once finished.
     *
     * @param  graphics The graphic context in which to paint.
     * @param  bounds   The bounding box where to paint the color ramp.
     * @return Bounding box of graduation labels (NOT taking in account the color ramp
     *                  behind them), or {@code null} if no label has been painted.
     */
    private Rectangle2D paint(final Graphics2D graphics, final Rectangle bounds) {
        final int length = colors.length;
        final double dx, dy;
        if (length == 0) {
            dx = 0;
            dy = 0;
        } else {
            dx = (double)(bounds.width  - 2*MARGIN) / length;
            dy = (double)(bounds.height - 2*MARGIN) / length;
            int i=0, lastIndex=0;
            Color color = colors[i];
            Color nextColor = color;
            int R,G,B;
            int nR = R = color.getRed  ();
            int nG = G = color.getGreen();
            int nB = B = color.getBlue ();
            final int ox = bounds.x + MARGIN;
            final int oy = bounds.y + bounds.height - MARGIN;
            final Rectangle2D.Double rect = new Rectangle2D.Double();
            rect.setRect(bounds);
            while (++i <= length) {
                if (i != length) {
                    nextColor = colors[i];
                    nR = nextColor.getRed  ();
                    nG = nextColor.getGreen();
                    nB = nextColor.getBlue ();
                    if (R==nR && G==nG && B==nB) {
                        continue;
                    }
                }
                if (horizontal) {
                    rect.x      = ox + dx*lastIndex;
                    rect.width  = dx * (i-lastIndex);
                    if (lastIndex == 0) {
                        rect.x     -= MARGIN;
                        rect.width += MARGIN;
                    }
                    if (i == length) {
                        rect.width += MARGIN;
                    }
                } else {
                    rect.y      = oy - dy*i;
                    rect.height = dy * (i-lastIndex);
                    if (lastIndex == 0) {
                        rect.height += MARGIN;
                    }
                    if (i == length) {
                        rect.y      -= MARGIN;
                        rect.height += MARGIN;
                    }
                }
                graphics.setColor(color);
                graphics.fill(rect);
                lastIndex = i;
                color = nextColor;
                R = nR;
                G = nG;
                B = nB;
            }
        }
        Rectangle2D labelBounds = null;
        if (labelVisibles && graduation!=null) {
            /*
             * Prepares graduation writting. First, computes the color ramp width in pixels.
             * Then, computes the coefficients for conversion of graduation values to pixel
             * coordinates.
             */
            double x = bounds.getCenterX();
            double y = bounds.getCenterY();
            final double axisRange   = graduation.getRange();
            final double axisMinimum = graduation.getMinimum();
            final double visualLength, scale, offset;
            if (horizontal) {
                visualLength = bounds.getWidth() - 2*MARGIN - dx;
                scale        = visualLength / axisRange;
                offset       = (bounds.getMinX() + MARGIN + 0.5*dx) - scale*axisMinimum;
            } else {
                visualLength = bounds.getHeight() - 2*MARGIN - dy;
                scale        = -visualLength / axisRange;
                offset       = (bounds.getMaxY() - MARGIN - 0.5*dy) + scale*axisMinimum;
            }
            if (hints == null) {
                hints = new RenderingHints(null);
            }
            final double valueToLocation = length / axisRange;
            Font font = getFont();
            if (font == null) {
                font = Font.decode("SansSerif-10");
            }
            final FontRenderContext context = graphics.getFontRenderContext();
            hints.put(Graduation.VISUAL_AXIS_LENGTH, new Float((float)visualLength));
            graphics.setColor(getForeground());
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
                    if (horizontal) x=position;
                    else            y=position;
                    rectg.setRect(x-0.5*width, y-0.5*height, width, height);
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
     * Returns a graduation for the specified sample dimension, minimum and maximum values. This
     * method must returns a graduation of the appropriate class, usually {@link NumberGraduation}
     * or {@link LogarithmicNumberGraduation}. If the supplied {@code reuse} object is non-null and
     * is of the appropriate class, then this method can returns {@code reuse} without creating a
     * new graduation object. This method must set graduations's
     * {@linkplain AbstractGraduation#setMinimum minimum},
     * {@linkplain AbstractGraduation#setMaximum maximum} and
     * {@linkplain AbstractGraduation#setUnit unit} according the values given in arguments.
     *
     * @param  reuse   The graduation to reuse if possible.
     * @param  band    The sample dimension to create graduation for.
     * @param  minimum The minimal geophysics value to appears in the graduation.
     * @param  maximum The maximal geophysics value to appears in the graduation.
     * @return A graduation for the supplied sample dimension.
     */
    protected Graduation createGraduation(final Graduation reuse, final SampleDimension band,
                                          final double minimum, final double maximum)
    {
        MathTransform1D tr  = band.getSampleToGeophysics();
        boolean linear      = false;
        boolean logarithmic = false;
        try {
            /*
             * An heuristic approach to determine if the transform is linear or logarithmic.
             * We look at the derivative, which should be constant everywhere for a linear
             * scale and be proportional to the inverse of 'x' for a logarithmic one.
             */
            tr = tr.inverse();
            final double EPS   = 1E-6; // For rounding error.
            final double ratio = tr.derivative(minimum) / tr.derivative(maximum);
            if (Math.abs(ratio-1) <= EPS) {
                linear = true;
            }
            if (Math.abs(ratio*(minimum/maximum) - 1) <= EPS) {
                logarithmic = true;
            }
        } catch (TransformException exception) {
            // Transformation failed. We don't know if the scale is linear or logarithmic.
            // Continue anyway. A warning will be logged later in this method.
        }
        final Unit<?> units = band.getUnits();
        AbstractGraduation graduation = (reuse instanceof AbstractGraduation) ?
                (AbstractGraduation) reuse : null;
        if (linear) {
            if (graduation==null || !graduation.getClass().equals(NumberGraduation.class)) {
                graduation = new NumberGraduation(units);
            }
        } else if (logarithmic) {
            if (graduation==null || !graduation.getClass().equals(LogarithmicNumberGraduation.class)) {
                graduation = new LogarithmicNumberGraduation(units);
            }
        } else {
            final Logger logger = Logging.getLogger(ColorRamp.class);
            final LogRecord record = Loggings.format(Level.WARNING,
                    Loggings.Keys.UNRECOGNIZED_SCALE_TYPE_$1, Classes.getShortClassName(tr));
            record.setLoggerName(logger.getName());
            logger.log(record);
            graduation = new NumberGraduation(units);
        }
        if (graduation == reuse) {
            graduation.setUnit(units);
        }
        graduation.setMinimum(minimum);
        graduation.setMaximum(maximum);
        return graduation;
    }

    /**
     * Returns an image representation for this color ramp. The image size will be this
     * {@linkplain #getSize widget size}.
     *
     * @return The color ramp as a buffered image.
     *
     * @since 2.4
     */
    public BufferedImage toImage() {
        final BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = image.createGraphics();
        paint(graphics, new Rectangle(0, 0, image.getWidth(), image.getHeight()));
        graphics.dispose();
        return image;
    }

    /**
     * Returns a string representation for this color ramp.
     */
    @Override
    public String toString() {
        int count=0;
        int i = 0;
        if (i < colors.length) {
            Color last = colors[i];
            while (++i < colors.length) {
                Color c = colors[i];
                if (!c.equals(last)) {
                    last = c;
                    count++;
                }
            }
        }
        return Classes.getShortClassName(this) + '[' + count + " colors]";
    }

    /**
     * Notifies this component that it now has a parent component. This method
     * is invoked by <cite>Swing</cite> and shouldn't be directly used.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        if (graduation != null) {
            graduation.removePropertyChangeListener(ui); // Avoid duplication
            graduation.addPropertyChangeListener(ui);
        }
    }

    /**
     * Notifies this component that it no longer has a parent component.
     * This method is invoked by <em>Swing</em> and shouldn't be directly used.
     */
    @Override
    public void removeNotify() {
        if (graduation != null) {
            graduation.removePropertyChangeListener(ui);
        }
        super.removeNotify();
    }

    /**
     * Classe ayant la charge de dessiner la rampe de couleurs, ainsi que
     * de calculer l'espace qu'elle occupe. Cette classe peut aussi réagir
     * à certains événements.
     *
     * @author Martin Desruisseaux (MPO, IRD)
     * @version 3.00
     *
     * @since 2.3
     * @module
     */
    private final class UI extends ComponentUI implements PropertyChangeListener {
        /**
         * Retourne la dimension minimale de cette rampe de couleurs.
         */
        @Override
        public Dimension getMinimumSize(final JComponent c) {
            return (((ColorRamp) c).horizontal) ?
                    new Dimension(2*MARGIN, 16) : new Dimension(16, 2*MARGIN);
        }

        /**
         * Retourne la dimension préférée de cette rampe de couleurs.
         */
        @Override
        public Dimension getPreferredSize(final JComponent c) {
            return (((ColorRamp) c).horizontal) ? new Dimension(256, 16) : new Dimension(16, 256);
        }

        /**
         * Dessine la rampe de couleurs vers le graphique spécifié.  Cette méthode a
         * l'avantage d'être appelée automatiquement par <i>Swing</i> avec une copie
         * d'un objet {@link Graphics}, ce qui nous évite d'avoir à le remettre dans
         * son état initial lorsqu'on a terminé le traçage de la rampe de couleurs.
         * On n'a pas cet avantage lorsque l'on ne fait que redéfinir
         * {@link JComponent#paintComponent}.
         */
        @Override
        public void paint(final Graphics graphics, final JComponent component) {
            final ColorRamp ramp = (ColorRamp) component;
            if (ramp.colors != null) {
                final Rectangle bounds = ramp.getBounds();
                bounds.x = 0;
                bounds.y = 0;
                ramp.paint((Graphics2D) graphics, bounds);
            }
        }

        /**
         * Méthode appelée automatiquement chaque fois qu'une propriété de l'axe a changée.
         */
        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            repaint();
        }
    }
}

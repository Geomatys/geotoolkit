/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1998-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing;

import java.awt.Font;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.IdentityHashMap;
import java.util.NoSuchElementException;

import java.io.Serializable;
import javax.vecmath.MismatchedSizeException;

import org.geotoolkit.math.Vector;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.display.axis.Axis2D;
import org.geotoolkit.display.axis.AbstractGraduation;
import org.geotoolkit.display.shape.TransformedShape;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.logging.Logging;

import static java.lang.Math.hypot;


/**
 * Displays two axes and an arbitrary amount of series with zoom capability.
 * Axes may have arbitrary orientation (they don't need to be perpendicular).
 * <p>
 * Axes color and font can bet set with call to {@link #setForeground} and {@link #setFont}
 * methods respectively. A scroll pane can be created with {@link #createScrollPane}.
 *
 * <p>&nbsp;</p>
 * <p align="center"><img src="doc-files/Plot2D.png"></p>
 * <p>&nbsp;</p>
 *
 * @author Martin Desruisseaux (MPO, Geomatys)
 * @version 3.0
 *
 * @since 1.1
 * @module
 */
@SuppressWarnings("serial")
public class Plot2D extends ZoomPane {
    /**
     * The axes for a given series. Instances of this class are used as values in the
     * {@link Plot2D#series} map. The <var>x</var> and <var>y</var> axes in this {@code Entry}
     * <strong>must</strong> be listed in {@link Plot2D#xAxes} and {@link Plot2D#yAxes} as well,
     * but the list order don't have to be the same than the {@link Plot2D#series} order.
     */
    private static final class Entry implements Serializable {
        /** For cross-version compatibility. */
        private static final long serialVersionUID = 1965783272889292496L;

        /** The <var>x</var> and <var>y</var> axis for a given series. */
        public final Axis2D xAxis, yAxis;

        /** Constructs a new entry with the specified axis. */
        public Entry(final Axis2D xAxis, final Axis2D yAxis) {
            this.xAxis = xAxis;
            this.yAxis = yAxis;
        }
    }

    /**
     * The set of <var>x</var> axes. There is usually only one axis, but more axes are allowed.
     * All {@code Entry.xAxis} instance <strong>must</strong> appears in this list as well, but
     * not necessarly in the same order.
     *
     * @see #newAxis
     * @see #addSeries
     */
    private final List<Axis2D> xAxes = new ArrayList<Axis2D>(3);

    /**
     * The set of <var>y</var> axes. There is usually only one axis, but more axes are allowed.
     * All {@code Entry.yAxis} instance <strong>must</strong> appears in this list as well, but
     * not necessarly in the same order.
     *
     * @see #newAxis
     * @see #addSeries
     */
    private final List<Axis2D> yAxes = new ArrayList<Axis2D>(3);

    /**
     * The set of series to plot. Keys are {@link Series} objects while values are {@code Entry}
     * objects with the <var>x</var> and <var>y</var> axis to use for the series.
     *
     * @see #addSeries
     */
    private final Map<Series,Entry> series = new LinkedHashMap<Series,Entry>();

    /**
     * Immutable version of {@code series} to be returned by {@link #getSeries}.
     *
     * @see #getSeries
     */
    private final Set<Series> unmodifiableSeries = Collections.unmodifiableSet(series.keySet());

    /**
     * The axes to use for the next series to be added to this plot,
     * or {@code null} if not yet created.
     */
    private Entry currentAxes;

    /**
     * Title for next axis to be created, or {@code null} if the current axis should be used
     * instead.
     *
     * @see #addXAxis
     * @see #addYAxis
     */
    private String nextXAxis="", nextYAxis="";

    /**
     * Bounding box of data in all series, or {@code null} if it must be recomputed.
     */
    private transient Rectangle2D seriesBounds;

    /**
     * Margin between widget border and the drawing area.
     */
    private int top=30, bottom=60, left=60, right=30;

    /**
     * Horizontal (x) and vertival (y) offset to apply to any supplementary axis.
     */
    private int xOffset=20, yOffset=-20;

    /**
     * The widget's width and height when the graphics was rendered for the last time.
     */
    private int lastWidth, lastHeight;

    /**
     * The plot title.
     */
    private String title;

    /**
     * The title font.
     */
    private Font titleFont = new Font("SansSerif", Font.BOLD, 16);

    /**
     * The default stroke for painting axes. By default we use the thickness line that
     * Java2D can display. This is often identical to a line of 1 pixel tick, except
     * if the user zoom in a graphic portion using the magnifier glass.
     */
    private final Stroke stroke = new BasicStroke(0);

    /**
     * The cycle of colors.
     */
    private final Color[] colors = new Color[] {
        Color.BLUE, Color.RED, Color.ORANGE
    };

    /**
     * Listener class for various events.
     */
    private static final class Listeners extends ComponentAdapter {
        /**
         * When resized, force the widget to layout its axis.
         */
        @Override public void componentResized(final ComponentEvent event) {
            final Plot2D c = (Plot2D) event.getSource();
            c.layoutAxis(false);
        }
    }

    /**
     * Constructs an initially empty {@code Plot2D} with
     * zoom capabilities on horizontal and vertical axis.
     */
    public Plot2D() {
        this(SCALE_X | SCALE_Y | TRANSLATE_X | TRANSLATE_Y | RESET);
    }

    /**
     * Constructs an initially empty {@code Plot2D} with
     * zoom capabilities on the specified axis.
     *
     * @param zoomX {@code true} for allowing zooming on the <var>x</var> axis.
     * @param zoomY {@code true} for allowing zooming on the <var>y</var> axis.
     */
    public Plot2D(final boolean zoomX, final boolean zoomY) {
        this((zoomX ? SCALE_X | TRANSLATE_X : 0) |
             (zoomY ? SCALE_Y | TRANSLATE_Y : 0) | RESET);
    }

    /**
     * Construct an initially empty {@code Plot2D} with the specified zoom capacities.
     *
     * @param  zoomCapacities Allowed zoom types. It can be a
     *         bitwise combinaison of the following constants:
     *         {@link #SCALE_X SCALE_X}, {@link #SCALE_Y SCALE_Y},
     *         {@link #TRANSLATE_X TRANSLATE_X}, {@link #TRANSLATE_Y TRANSLATE_Y},
     *         {@link #ROTATE ROTATE}, {@link #RESET RESET} and {@link #DEFAULT_ZOOM DEFAULT_ZOOM}.
     * @throws IllegalArgumentException If {@code zoomCapacities} is invalid.
     */
    private Plot2D(final int zoomCapacities) {
        super(zoomCapacities);
        super.setPaintingWhileAdjusting(true);
        final Listeners listeners = new Listeners();
        super.addComponentListener(listeners);
    }

    /**
     * Adds a new <var>x</var> axis to be used for the next series to be added to this plot.
     * Special cases:
     * <p>
     * <ul>
     *   <li>If this method is never invoked, then a single <var>x</var> axis with no label
     *       will be used.</li>
     *   <li>If this method is invoked only once before the first series is added,
     *       then a single axis with the given label will be used.</li>
     *   <li>If this method is invoked more than once, then many <var>x</var> axes
     *       will be used. Additional axes will be drawn below the first axis.</li>
     * </ul>
     *
     * @param label The axis label, or {@code null} if the axis should not have any label.
     */
    public void addXAxis(String label) {
        if (label == null) {
            label = "";
        }
        nextXAxis = label.trim();
    }

    /**
     * Adds a new <var>y</var> axis to be used for the next series to be added to this plot.
     * Special cases:
     * <p>
     * <ul>
     *   <li>If this method is never invoked, then a single <var>y</var> axis with no label
     *       will be used.</li>
     *   <li>If this method is invoked only once before the first series is added,
     *       then a single axis with the given label will be used.</li>
     *   <li>If this method is invoked more than once, then many <var>y</var> axes
     *       will be used. Additional axes will be drawn at the left of the first axis.</li>
     * </ul>
     *
     * @param label The axis label, or {@code null} if the axis should not have any label.
     */
    public void addYAxis(String label) {
        if (label == null) {
            label = "";
        }
        nextYAxis = label.trim();
    }

    /**
     * Adds a new serie to the plot. This convenience method wraps the given arrays into
     * {@link Vector} objects and delegates to {@linkplain #addSeries(String, Paint, Vector, Vector)}.
     *
     * @param  name The series name, or {@code null} if none.
     * @param  color The color to use for plotting the series, or {@code null} for a default color.
     * @param  x The vector of <var>x</var> values.
     * @param  y The vector of <var>y</var> values.
     * @return The series added.
     * @throws MismatchedSizeException if the arrays don't have the same length.
     */
    public Series addSeries(final String name, final Paint color, final float[] x, final float[] y)
            throws MismatchedSizeException
    {
        return addSeries(name, color, Vector.create(x), Vector.create(y));
    }

    /**
     * Adds a new serie to the plot. This convenience method wraps the given arrays into
     * {@link Vector} objects and delegates to {@link #addSeries(String, Paint, Vector, Vector)}.
     *
     * @param  name The series name, or {@code null} if none.
     * @param  color The color to use for plotting the series, or {@code null} for a default color.
     * @param  x The vector of <var>x</var> values.
     * @param  y The vector of <var>y</var> values.
     * @return The series added.
     * @throws MismatchedSizeException if the arrays don't have the same length.
     */
    public Series addSeries(final String name, final Paint color, final double[] x, final double[] y)
            throws MismatchedSizeException
    {
        return addSeries(name, color, Vector.create(x), Vector.create(y));
    }

    /**
     * Adds a new serie to the plot. This convenience method creates a default {@link Series}
     * implementation for the given vectors and delegates to {@link #addSeries(Series)}.
     *
     * @param  name The series name, or {@code null} if none.
     * @param  color The color to use for plotting the series, or {@code null} for a default color.
     * @param  x The vector of <var>x</var> values.
     * @param  y The vector of <var>y</var> values.
     * @return The series added.
     * @throws MismatchedSizeException if the arrays don't have the same length.
     */
    public Series addSeries(final String name, Paint color, final Vector x, final Vector y)
            throws MismatchedSizeException
    {
        if (color == null) {
            color = colors[series.size() % colors.length];
        }
        return addSeries(new DefaultSeries(name, color, x, y));
    }

    /**
     * Adds a new serie to the plot. The new series will use the axes given by the last calls
     * to {@link #addXAxis addXAxis} and {@link #addYAxis addYAxis}.
     *
     * @param  series The serie to add.
     * @return The added series, returned for convenience.
     */
    public Series addSeries(final Series series) {
        final Rectangle2D bounds = series.bounds();
        final Axis2D xAxis;
        final Axis2D yAxis;
        if (nextXAxis != null) {
            xAxis = new Axis2D();
            final AbstractGraduation grad = (AbstractGraduation) xAxis.getGraduation();
            grad.setMinimum(bounds.getMinX());
            grad.setMaximum(bounds.getMaxX());
            grad.setTitle(nextXAxis);
            xAxes.add(xAxis);
            nextXAxis = null;
        } else {
            xAxis = currentAxes.xAxis;
        }
        if (nextYAxis != null) {
            yAxis = new Axis2D();
            final AbstractGraduation grad = (AbstractGraduation) yAxis.getGraduation();
            grad.setMinimum(bounds.getMinY());
            grad.setMaximum(bounds.getMaxY());
            grad.setTitle(nextYAxis);
            yAxes.add(yAxis);
            nextYAxis = null;
        } else {
            yAxis = currentAxes.yAxis;
        }
        if (xAxis != null || yAxis != null) {
            // At least one axis has been created.
            currentAxes = new Entry(xAxis, yAxis);
        }
        this.series.put(series, currentAxes);
        if (title == null) {
            title = series.getName();
        }
        if (seriesBounds == null) {
            seriesBounds = new Rectangle2D.Double();
            seriesBounds.setRect(bounds);
        } else {
            seriesBounds.add(bounds);
        }
        repaint();
        return series;
    }

    /**
     * Returns the set of series to be plotted.
     * Series are painted in the order they are returned.
     *
     * @return The series to be plotted.
     */
    public Set<Series> getSeries() {
        return unmodifiableSeries;
    }

    /**
     * Returns the {<var>x</var>, <var>y</var>} axis for the specified series.
     *
     * @param  series The series for which axis are wanted.
     * @return An array of length 2 containing <var>x</var> and <var>y</var> axis.
     * @throws NoSuchElementException if this widget doesn't contains the specified series.
     */
    public Axis2D[] getAxis(final Series series) throws NoSuchElementException {
        final Entry entry = this.series.get(series);
        if (entry != null) {
            assert xAxes.indexOf(entry.xAxis) >= 0 : xAxes;
            assert yAxes.indexOf(entry.yAxis) >= 0 : yAxes;
            return new Axis2D[] {
                entry.xAxis,
                entry.yAxis
            };
        }
        throw new NoSuchElementException(series.getName());
    }

    /**
     * Returns the minimal and maximal ordinate values of all (<var>x</var>,<var>y</var>) points
     * to be plotted. This is the union of the bounding boxes of {@linkplain #getSeries all series}
     * in this {@code Plot2D} component.
     *
     * @return The minimal and maximal ordinate values of (<var>x</var>,<var>y</var>) points.
     */
    @Override
    public Rectangle2D getArea() {
        final Rectangle2D bounds = seriesBounds;
        return (bounds != null) ? (Rectangle2D) bounds.clone() : null;
    }

    /**
     * Returns the zoomable area in pixel coordinates. This area will not cover the
     * full widget area, since some room will be left for painting axis and titles.
     */
    @Override
    protected Rectangle getZoomableBounds(Rectangle bounds) {
        bounds = super.getZoomableBounds(bounds);
        bounds.x      += left;
        bounds.y      +=  top;
        bounds.width  -= (left + right);
        bounds.height -= (top + bottom);
        return bounds;
    }

    /**
     * Adds the given bounds to a map of bounds. If no bounds were assigned to the given axis,
     * then the given bounds is copied and assigned to that axis. Otherwise - if a bounds
     * already exists for the given axis - then that bounds is expanded in order to contains
     * fully the given bounds.
     *
     * @param union The bounds computed up to date.
     * @param axis  The axis for which the bounds is to be updated.
     * @param box   The bounds to be added to the bounds associated to the given axis.
     */
    private static void add(final Map<Axis2D,Rectangle2D> unions, final Axis2D axis, final Rectangle2D bounds) {
        Rectangle2D union = unions.get(axis);
        if (union != null) {
            union.add(bounds);
        } else {
            union = new Rectangle2D.Double();
            union.setRect(bounds);
            unions.put(axis, union);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        layoutAxis(true);
        /*
         * It is okay to use the same IdentityHashMap instance for both X and Y axes because the
         * same Axis2D instance should never be used for both axes. Note however that a plain HashMap
         * would not work because X and Y axis could be equal in the sense of Axis2D.equals(Object).
         */
        final Map<Axis2D,Rectangle2D> unions = new IdentityHashMap<Axis2D,Rectangle2D>();
        for (final Map.Entry<Series,Entry> e : series.entrySet()) {
            final Rectangle2D bounds = e.getKey().bounds();
            final Entry entry = e.getValue();
            add(unions, entry.xAxis, bounds);
            add(unions, entry.yAxis, bounds);
        }
        for (final Axis2D axis : xAxes) {
            final Rectangle2D bounds = unions.get(axis);
            if (bounds != null) {
                final AbstractGraduation grad = (AbstractGraduation) axis.getGraduation();
                grad.setMinimum(bounds.getMinX());
                grad.setMaximum(bounds.getMaxX());
            }
        }
        for (final Axis2D axis : yAxes) {
            final Rectangle2D bounds = unions.get(axis);
            if (bounds != null) {
                final AbstractGraduation grad = (AbstractGraduation) axis.getGraduation();
                grad.setMinimum(bounds.getMinY());
                grad.setMaximum(bounds.getMaxY());
            }
        }
        super.reset();
    }

    /**
     * Sets axis location. This method is automatically invoked when the axis needs to be layout.
     * This occurs for example when new axis are added, or when the component has been resized.
     *
     * @param force If {@code true}, then axis orientation and position are reset to their default
     *        value. If {@code false}, then this method tries to preserve axis orientation and
     *        position relative to widget's border.
     */
    private void layoutAxis(final boolean force) {
        final int width  = getWidth();
        final int height = getHeight();
        final double tx  = width  - lastWidth;
        final double ty  = height - lastHeight;
        int axisCount = 0;
        for (final Axis2D axis : xAxes) {
            if (force) {
                axis.setLabelClockwise(true);
                axis.setLine(left, height-bottom, width-right, height-bottom);
                translatePerpendicularly(axis, xOffset*axisCount, yOffset*axisCount);
            } else {
                resize(axis, tx, ty);
            }
            axisCount++;
        }
        axisCount = 0;
        for (final Axis2D axis : yAxes) {
            if (force) {
                axis.setLabelClockwise(false);
                axis.setLine(left, height-bottom, left, top);
                translatePerpendicularly(axis, xOffset*axisCount, yOffset*axisCount);
            } else {
                resize(axis, tx, ty);
            }
            axisCount++;
        }
        lastWidth  = width;
        lastHeight = height;
    }

    /**
     * Translates an axis in a perpendicular direction to its orientation.
     * The following rules applies:
     * <p>
     * <ul>
     *   <li>If the axis is vertical, then the axis is translated horizontally
     *       by {@code tx} only. The {@code ty} argument is ignored.</li>
     *   <li>If the axis is horizontal, then the axis is translated vertically
     *       by {@code ty} only. The {@code tx} argument is ignored.</li>
     *   <li>If the axis is diagonal, then the axis is translated using the following
     *       formula (<var>theta</var> is the axis orientation relative to the horizontal):
     *
     *       {@preformat math
     *          dx = tx*sin(theta)
     *          dy = ty*cos(theta)
     *       }
     *    </li>
     *  </ul>
     */
    private static void translatePerpendicularly(final Axis2D axis, final double tx, final double ty) {
        final double x1 = axis.getX1();
        final double y1 = axis.getY1();
        final double x2 = axis.getX2();
        final double y2 = axis.getY2();
        double dy = x2 - x1; // Note: dx and dy are really swapped - this is not an error.
        double dx = y1 - y2;
        double length = hypot(dx, dy);
        dx *= tx/length;
        dy *= ty/length;
        axis.setLine(x1+dx, y1+dy, x2+dx, y2+dy);
    }

    /**
     * Invoked when this component has been resized. This method adjust axis length will
     * preserving their orientation and position relative to border.
     *
     * @param axis The axis to adjust.
     * @param tx The change in component width.
     * @param ty The change in component height.
     */
    private static void resize(final Axis2D axis, final double tx, final double ty) {
        final Point2D P1 = axis.getP1();
        final Point2D P2 = axis.getP2();
        final Point2D anchor, moveable;
        if (length(P1) <= length(P2)) {
            anchor   = P1;
            moveable = P2;
        } else {
            anchor   = P2;
            moveable = P1;
        }
        final double  x = moveable.getX();
        final double  y = moveable.getY();
        final double dx = x-anchor.getX();
        final double dy = y-anchor.getY();
        final double length = hypot(dx, dy);
        moveable.setLocation(x + tx*dx/length,
                             y + ty*dy/length);
        axis.setLine(P1, P2);
    }

    /**
     * Returns the length of the vector from (0,0) to the given point. We compute the length
     * instead then the square of the length because the later may overflow,  while the Java
     * {@link Math#hypot} implementation is designated for avoiding such overflow.
     */
    private static double length(final Point2D point) {
        return hypot(point.getX(), point.getY());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transform(final AffineTransform change) {
        super.transform(change);
        Point2D.Double P1 = new Point2D.Double();
        Point2D.Double P2 = new Point2D.Double();
        try {
            /*
             * Process horizontal axis first, then process the vertical axis.
             */
            boolean processVerticalAxis = false;
            do {
                for (final Axis2D axis : (processVerticalAxis ? yAxes : xAxes)) {
                    P1.setLocation(axis.getX1(), axis.getY1());
                    P2.setLocation(axis.getX2(), axis.getY2());
                    zoom.inverseTransform(P1, P1);
                    zoom.inverseTransform(P2, P2);
                    final AbstractGraduation grad = (AbstractGraduation) axis.getGraduation();
                    if (!processVerticalAxis) {
                        if (P1.x > P2.x) {
                            final Point2D.Double tmp = P1;
                            P1 = P2;
                            P2 = tmp;
                        }
                        grad.setMinimum(P1.x);
                        grad.setMaximum(P2.x);
                    } else {
                        if (P1.y > P2.y) {
                            final Point2D.Double tmp = P1;
                            P1 = P2;
                            P2 = tmp;
                        }
                        grad.setMinimum(P1.y);
                        grad.setMaximum(P2.y);
                    }
                }
            } while ((processVerticalAxis = !processVerticalAxis) == true);
        } catch (NoninvertibleTransformException exception) {
            Logging.unexpectedException(Plot2D.class, "transform", exception);
        }
        repaint();
    }

    /**
     * Paints the axes and all series.
     */
    @Override
    protected void paintComponent(final Graphics2D graphics) {
        final Rectangle bounds    = getZoomableBounds(null);
        final Stroke    oldStroke = graphics.getStroke();
        final Paint     oldPaint  = graphics.getPaint();
        final Shape     oldClip   = graphics.getClip();
        final Font      oldFont   = graphics.getFont();
        /*
         * Paints series first.
         */
        int axisCount = 0;
        graphics.clip(bounds);
        graphics.setStroke(stroke);
        final TransformedShape transformed = new TransformedShape();
        for (final Map.Entry<Series,Entry> e : series.entrySet()) {
            final Series series = e.getKey();
            final Entry  entry  = e.getValue();
            final AffineTransform transform = Axis2D.createAffineTransform(entry.xAxis, entry.yAxis);
            final Shape path = series.path();
            transformed.setTransform(transform);
            transformed.setOriginalShape(path);
            graphics.setPaint(series.getPaint());
            graphics.draw(transformed);
            axisCount++;
        }
        /*
         * Paints axes on top of series.
         */
        graphics.setStroke(oldStroke);
        graphics.setPaint(getForeground());
        graphics.setFont(getFont());
        graphics.setClip(oldClip);
        for (final Axis2D axis : xAxes) {
            axis.paint(graphics);
        }
        for (final Axis2D axis : yAxes) {
            axis.paint(graphics);
        }
        /*
         * Paints the title.
         */
        if (title != null) {
            final FontRenderContext context = graphics.getFontRenderContext();
            final GlyphVector glyphs = titleFont.createGlyphVector(context, title);
            final Rectangle2D titleBounds = glyphs.getVisualBounds();
            graphics.drawGlyphVector(glyphs, (float) ((getWidth() - titleBounds.getWidth()) / 2), 20);
        }
        graphics.setStroke(oldStroke);
        graphics.setPaint(oldPaint);
        graphics.setFont(oldFont);
    }

    /**
     * Removes all axes and series from this plot.
     */
    public void clear() {
        series.clear();
        xAxes .clear();
        yAxes .clear();
        nextXAxis    = "";
        nextYAxis    = "";
        seriesBounds = null;
        currentAxes  = null;
        repaint();
    }

    /**
     * A series to be displayed in a {@link Plot2D} widget. A {@code Series} contains the data
     * to plot as a {@link Shape} object and the {@link Paint} to use for drawing the lines.
     *
     * @author Martin Desruisseaux (MPO, Geomatys)
     * @version 3.0
     *
     * @since 1.1
     * @module
     */
    public static interface Series {
        /**
         * Returns the name of this series. If only one series is plotted,
         * then the name of that series will be used as the plot title.
         *
         * @return The name of this series, or {@code null} if none.
         */
        String getName();

        /**
         * Returns the color to use for plotting this series.
         *
         * @return The color to use for plotting this series.
         */
        Paint getPaint();

        /**
         * Returns the bounding box of all <var>x</var> and <var>y</var> ordinates.
         *
         * @return The minimal and maximal (<var>x</var>, <var>y</var>) values.
         */
        Rectangle2D bounds();

        /**
         * Returns the series data as a path.
         *
         * @return The (<var>x</var>,<var>y</var>) coordinates as a Java2D {@linkplain Shape shape}.
         */
        Shape path();
    }

    /**
     * Default implementation of {@link Plot2D.Series}.
     *
     * @author Martin Desruisseaux (MPO, Geomatys)
     * @version 3.0
     *
     * @since 1.1
     * @module
     */
    private static final class DefaultSeries implements Series {
        /**
         * The series name.
         */
        private final String name;

        /**
         * The color.
         */
        private Paint color;

        /**
         * The path, which may be float or double precision.
         */
        private final Path2D path;

        /**
         * The minimal and maximum (<var>x</var>,<var>y</var>) values.
         */
        private final Rectangle2D bounds;

        /**
         * Constructs a series with the given name and (<var>x</var>,<var>y</var>) vectors.
         *
         * @throws MismatchedSizeException if the arrays don't have the same length.
         */
        public DefaultSeries(final String name, final Paint color, final Vector x, final Vector y)
                throws MismatchedSizeException
        {
            this.name  = name;
            this.color = color;
            final int length = x.size();
            if (length != y.size()) {
                throw new MismatchedSizeException(Errors.format(Errors.Keys.MISMATCHED_ARRAY_LENGTH));
            }
            final Class<?> type = Classes.widestClass(
                    Classes.primitiveToWrapper(x.getElementType()).asSubclass(Number.class),
                    Classes.primitiveToWrapper(y.getElementType()).asSubclass(Number.class));
            if (Double.class.equals(type) || Long.class.equals(type)) {
                path = new Path2D.Double();
            } else {
                path = new Path2D.Float();
            }
            boolean move = true;
            for (int i=0; i<length; i++) {
                final double xi, yi;
                if (Double.isNaN(yi = y.doubleValue(i)) || Double.isNaN(xi = x.doubleValue(i))) {
                    move = true;
                    continue;
                }
                if (move) {
                    move = false;
                    path.moveTo(xi, yi);
                } else {
                    path.lineTo(xi, yi);
                }
            }
            bounds = path.getBounds2D();
        }

        /**
         * Returns the series name.
         */
        @Override
        public String getName() {
            return name;
        }

        /**
         * Returns the color for this series.
         */
        @Override
        public Paint getPaint() {
            return color;
        }

        /**
         * Returns the minimal and maximum (<var>x</var>,<var>y</var>) values.
         */
        @Override
        public Rectangle2D bounds() {
            return (Rectangle2D) bounds.clone();
        }

        /**
         * Returns the series data as a path. This method does not clone the path for
         * performance reason. However since the {@link Shape} interface doesn't provide
         * setter methods, it should be reasonable.
         */
        @Override
        public Shape path() {
            return path;
        }
    }
}

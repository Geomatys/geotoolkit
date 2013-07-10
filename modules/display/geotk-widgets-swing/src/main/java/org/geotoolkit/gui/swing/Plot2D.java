/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1998-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing;

import java.awt.Font;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Line2D;
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
import java.util.HashMap;
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
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.util.Numbers;
import org.apache.sis.util.logging.Logging;

import static java.lang.Math.hypot;


/**
 * Displays two axes and an arbitrary amount of series with zoom capability.
 * Axes may have arbitrary orientation (they don't need to be perpendicular).
 * It is possible for example to create a plot with a vertical <var>x</var>
 * axis increasing downward, like the ones used in oceanography for plotting
 * the data of <cite>Conductivity, Temperature, Depth</cite> (CTD) Sensors.
 * Axes can also be oblique for simulating 3D effects.
 * <p>
 * Axes color and font can bet set with call to {@link #setForeground} and {@link #setFont}
 * methods respectively. A scroll pane can be created with {@link #createScrollPane}.
 * The example below creates a plot with zoom capability restricted to the <var>x</var> axis:
 *
 * {@preformat java
 *     float[] x = ...:
 *     float[] y = ...:
 *     Plot2D plot = new Plot2D(true, false);
 *     plot.addXAxis("Some x values");
 *     plot.addYAxis("Some y values");
 *     plot.addSeries("Random values", Color.BLUE, x, y);
 * }
 *
 * <table cellspacing="24" cellpadding="12" align="center"><tr valign="top"><td>
 * <img src="doc-files/Plot2D.png">
 * </td><td width="500" bgcolor="lightblue">
 * {@section Demo}
 * The image on the left side gives an example of this widget appearance.
 * To try this component in your browser, see the
 * <a href="http://www.geotoolkit.org/demos/geotk-simples/applet/Plot2D.html">demonstration applet</a>.
 * </td></tr></table>
 *
 * @author Martin Desruisseaux (MPO, Geomatys)
 * @version 3.00
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
     * not necessarily in the same order.
     *
     * @see #addXAxis
     * @see #addSeries
     */
    private final List<Axis2D> xAxes = new ArrayList<>(3);

    /**
     * The set of <var>y</var> axes. There is usually only one axis, but more axes are allowed.
     * All {@code Entry.yAxis} instance <strong>must</strong> appears in this list as well, but
     * not necessarily in the same order.
     *
     * @see #addYAxis
     * @see #addSeries
     */
    private final List<Axis2D> yAxes = new ArrayList<>(3);

    /**
     * The set of series to plot. Keys are {@link Series} objects while values are {@code Entry}
     * objects with the <var>x</var> and <var>y</var> axis to use for the series.
     *
     * @see #addSeries
     */
    private final Map<Series,Entry> series = new LinkedHashMap<>();

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
     * The default cycle of colors. They are used only if the user added a series
     * without specifying explicitly the color to use for that series.
     * <p>
     * Those default colors may change in future Geotk versions. For safety, users are
     * encouraged to specify the desired color explicitly when adding a series to a plot.
     */
    protected static final List<Color> DEFAULT_COLORS = UnmodifiableArrayList.wrap(
        new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN, Color.MAGENTA}
    );

    /**
     * The color to use for drawing grid lines, or {@code null} if the grid should not be drawn.
     */
    private Color gridColor = Color.LIGHT_GRAY;

    /**
     * Listener class for various events.
     */
    private static final class Listeners extends ComponentAdapter {
        /**
         * When resized, force the widget to layout its axis.
         */
        @Override public void componentResized(final ComponentEvent event) {
            final Plot2D c = (Plot2D) event.getSource();
            c.layoutAxes(false);
        }
    }

    /**
     * Crestes an initially empty {@code Plot2D} with
     * zoom capabilities on horizontal and vertical axis.
     */
    public Plot2D() {
        this(SCALE_X | SCALE_Y | TRANSLATE_X | TRANSLATE_Y | RESET);
    }

    /**
     * Creates an initially empty {@code Plot2D} with
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
     *         bitwise combination of the following constants:
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
     * Adds a new serie to the plot. This convenience method wraps the given arrays into {@link Vector}
     * objects and delegates to {@linkplain #addSeries(Map, Vector, Vector)}.
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
        return addSeries(properties(name, color), Vector.create(x), Vector.create(y));
    }

    /**
     * Adds a new serie to the plot. This convenience method wraps the given arrays into {@link Vector}
     * objects and delegates to {@link #addSeries(Map, Vector, Vector)}.
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
        return addSeries(properties(name, color), Vector.create(x), Vector.create(y));
    }

    /**
     * Creates a properties map for the given arguments.
     */
    private static Map<String,Object> properties(final String name, final Paint color) {
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put("Name", name);
        properties.put("Paint", color);
        return properties;
    }

    /**
     * Adds a new serie to the plot. This method creates a default {@link Series} implementation
     * for the given vectors and delegates to {@link #addSeries(Series)}. The series is configured
     * using the values given in the {@code properties} map. The following keys are recognized:
     * <p>
     * <ul>
     *   <li>{@code "Name"} for a {@link String} value to be used as the {@linkplain Series#name series name}.</li>
     *   <li>{@code "Paint"} for a {@link Paint} value to be used as the {@linkplain Series#paint series paint}.</li>
     * </ul>
     * <p>
     * Any keys not recognized by this method are ignored and can be used by subclasses for
     * their own additional information. Missing entries will be replaced by default values.
     * Future versions of the {@code Plot2D} class may add more keys - this method is using
     * a {@link Map} argument for allowing such extensibility.
     *
     * @param  properties The properties to be given to the new series.
     * @param  x The vector of <var>x</var> values.
     * @param  y The vector of <var>y</var> values.
     * @return The series added.
     * @throws MismatchedSizeException if the arrays don't have the same length.
     */
    public Series addSeries(Map<String,?> properties, final Vector x, final Vector y)
            throws MismatchedSizeException
    {
        if (properties == null) {
            properties = Collections.emptyMap();
        }
        String name = (String) properties.get("Name");
        Paint color = (Paint) properties.get("Paint");
        if (color == null) {
            color = DEFAULT_COLORS.get(series.size() % DEFAULT_COLORS.size());
        }
        boolean fill = Boolean.TRUE.equals(properties.get("Fill")); // Undocumented (for now) feature.
        return addSeries(new DefaultSeries(name, color, x, y, fill));
    }

    /**
     * Adds a new serie to the plot. The new series will use the axes given by the last calls
     * to {@link #addXAxis addXAxis} and {@link #addYAxis addYAxis}.
     *
     * @param  series The serie to add.
     * @return The added series, returned for convenience.
     */
    public Series addSeries(final Series series) {
        /*
         * Computes the data extremums before to create axes because we need the zoom affine
         * transform, and the calculation of the zoom transform needs the data extremums.
         */
        final Rectangle2D bounds = series.bounds();
        if (!bounds.isEmpty()) {
            if (seriesBounds == null) {
                seriesBounds = new Rectangle2D.Double();
                seriesBounds.setRect(bounds);
            } else {
                seriesBounds.add(bounds);
            }
            if (zoomIsReset()) {
                reset(); // Needed for computing the zoom.
            }
        }
        /*
         * Gets the axes, creating them if needed.
         */
        final Axis2D xAxis;
        final Axis2D yAxis;
        boolean axisCreated = false;
        try {
            if (nextXAxis != null) {
                axisCreated = true;
                xAxis = new Axis2D();
                layoutAxis(xAxis, xAxes.size(), true);
                inferGraduation(xAxis, true); // Must be after layoutAxis.
                final AbstractGraduation grad = (AbstractGraduation) xAxis.getGraduation();
                grad.setTitle(nextXAxis);
                xAxes.add(xAxis);
                nextXAxis = null;
            } else {
                xAxis = currentAxes.xAxis;
            }
            if (nextYAxis != null) {
                axisCreated = true;
                yAxis = new Axis2D();
                layoutAxis(yAxis, yAxes.size(), false);
                inferGraduation(yAxis, false); // Must be after layoutAxis.
                final AbstractGraduation grad = (AbstractGraduation) yAxis.getGraduation();
                grad.setTitle(nextYAxis);
                yAxes.add(yAxis);
                nextYAxis = null;
            } else {
                yAxis = currentAxes.yAxis;
            }
        } catch (NoninvertibleTransformException exception) {
            throw new IllegalStateException(exception);
        }
        if (axisCreated) {
            // At least one axis has been created.
            currentAxes = new Entry(xAxis, yAxis);
        }
        this.series.put(series, currentAxes);
        if (title == null) {
            title = series.name();
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
     * Returns the color to use for drawing grid lines, or {@code null} if the grid should not
     * be drawn.
     *
     * @return The current grid color, or {@code null} if none.
     */
    public Color getGridColor() {
        return gridColor;
    }

    /**
     * Sets the color to use for drawing grid lines, or {@code null} if the grid should not
     * be drawn.
     *
     * @param color The new grid color to use, or {@code null} if none.
     */
    public void setGridColor(final Color color) {
        gridColor = color;
    }

    /**
     * Returns the {<var>x</var>, <var>y</var>} axes for the specified series.
     *
     * @param  series The series for which axis are wanted.
     * @return An array of length 2 containing <var>x</var> and <var>y</var> axis.
     * @throws NoSuchElementException if this widget doesn't contains the specified series.
     */
    public Axis2D[] getAxes(final Series series) throws NoSuchElementException {
        final Entry entry = this.series.get(series);
        if (entry != null) {
            assert xAxes.indexOf(entry.xAxis) >= 0 : xAxes;
            assert yAxes.indexOf(entry.yAxis) >= 0 : yAxes;
            return new Axis2D[] {
                entry.xAxis,
                entry.yAxis
            };
        }
        throw new NoSuchElementException(series.name());
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
     * Returns the margin between the {@linkplain #getBounds() widget bounds} and the
     * {@linkplain #getZoomableBounds zoomable bounds}. The zoomable bounds is the area
     * where the graph will be plotted.
     *
     * @return The margin between widget bounds and the area where the graph is plotted.
     *
     * @since 3.00
     */
    public Insets getMargin() {
        return new Insets(top, left, bottom, right);
    }

    /**
     * Sets the margin between the {@linkplain #getBounds() widget bounds} and the
     * {@linkplain #getZoomableBounds zoomable bounds} to the given insets.
     *
     * @param margin The new margin between widget bounds and the area where the graph is plotted.
     *
     * @since 3.00
     */
    public void setMargin(final Insets margin) {
        top    = margin.top;
        left   = margin.left;
        bottom = margin.bottom;
        right  = margin.right;
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
    private static void addAxisRange(final Map<Axis2D,Rectangle2D> unions,
            final Axis2D axis, final Rectangle2D bounds)
    {
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
     * Reinitializes the affine transform {@link #zoom zoom} in order to cancel any zoom, rotation or
     * translation. The argument {@code yAxisUpward} indicates whether the <var>y</var> axis should
     * point upwards, which is usually {@code true} for a plot.
     */
    @Override
    protected void reset(final Rectangle zoomableBounds, final boolean yAxisUpward) {
        layoutAxes(true);
        /*
         * It is okay to use the same IdentityHashMap instance for both X and Y axes because the
         * same Axis2D instance should never be used for both axes. Note however that a plain HashMap
         * would not work because X and Y axis could be equal in the sense of Axis2D.equals(Object).
         */
        final Map<Axis2D,Rectangle2D> unions = new IdentityHashMap<>();
        for (final Map.Entry<Series,Entry> e : series.entrySet()) {
            final Rectangle2D bounds = e.getKey().bounds();
            final Entry entry = e.getValue();
            addAxisRange(unions, entry.xAxis, bounds);
            addAxisRange(unions, entry.yAxis, bounds);
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
        super.reset(zoomableBounds, yAxisUpward);
    }

    /**
     * Sets axes location. This method is automatically invoked when the axes need to be layout.
     * This occurs for example when new axis are added, or when the component has been resized.
     * This method does not change the graduations.
     *
     * @param force If {@code true}, then axes orientation and position are reset to their default
     *        value. If {@code false}, then this method tries to preserve axes orientation and
     *        position relative to widget's border.
     */
    private void layoutAxes(final boolean force) {
        final int width  = getWidth();
        final int height = getHeight();
        final double tx  = width  - lastWidth;
        final double ty  = height - lastHeight;
        int axisCount = 0;
        for (final Axis2D axis : xAxes) {
            if (force) {
                layoutAxis(axis, axisCount, true);
            } else {
                resize(axis, tx, ty);
            }
            axisCount++;
        }
        axisCount = 0;
        for (final Axis2D axis : yAxes) {
            if (force) {
                layoutAxis(axis, axisCount, false);
            } else {
                resize(axis, tx, ty);
            }
            axisCount++;
        }
        lastWidth  = width;
        lastHeight = height;
    }

    /**
     * Forces the layout of the given axis. This method changes only the axis position,
     * not the axis graduation. To change the graduation, invoke {@link #inferGraduation}
     * <strong>after</strong> the axis has been put at its proper location on the widget area.
     *
     * @param axis The axis to layout.
     * @param axisCount The index of the given axis.
     * @param isX {@code true} if the given axis is an X axis, or {@code false} for an Y axis.
     */
    private void layoutAxis(final Axis2D axis, final int axisCount, final boolean isX) {
        final int width  = super.getWidth();
        final int height = super.getHeight();
        final int x1, y1, x2, y2;
        x1 = left;
        y1 = height - bottom;
        if (isX) {
            x2 = width - right;
            y2 = y1;
        } else {
            x2 = x1;
            y2 = top;
        }
        axis.setLabelClockwise(isX);
        axis.setLine(x1, y1, x2, y2);
        translatePerpendicularly(axis, xOffset*axisCount, yOffset*axisCount);
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
     *   </li>
     * </ul>
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
     * Invoked when this component has been resized. This method adjust axis length while
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
        if (distance(P1) <= distance(P2)) {
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
     * Returns the distance from the origin (0,0) to the given point. We compute the distance
     * instead then the square of the distance because the later may overflow, while the Java
     * {@link Math#hypot} implementation is designated for avoiding such overflow.
     */
    private static double distance(final Point2D point) {
        return hypot(point.getX(), point.getY());
    }

    /**
     * Changes the {@linkplain #zoom zoom} by applying an affine transform. The {@code change}
     * transform must express a change in the units of the {@linkplain #addSeries(Series) series
     * added} to this widget. The location of axes will <strong>not</strong> change as a result
     * of the given transform. Instead, the axis graduations will be updated with new minimal
     * and maximal values matching the new zoom.
     */
    @Override
    public void transform(final AffineTransform change) {
        super.transform(change);
        try {
            /*
             * The affine transform from "data" to "pixel" coordinates changed. If we assume that
             * the axes position don't change, then the graduations need to be updated in order
             * to reflect the affine transform change. We perform this update by converting the
             * axes coordinates from pixel units to data units. By definition, the (x,y) values
             * of axes end-points in "data" units are the extremums on the X and Y axes respectively.
             */
            for (final Axis2D axis : xAxes) {
                inferGraduation(axis, true);
            }
            for (final Axis2D axis : yAxes) {
                inferGraduation(axis, false);
            }
        } catch (NoninvertibleTransformException exception) {
            Logging.unexpectedException(Plot2D.class, "transform", exception);
        }
        repaint();
    }

    /**
     * Sets the graduation of the given axis according its current position. The following
     * conditions must be hold before this method is invoked:
     * <p>
     * <ul>
     *   <li>The {@link #zoom} transform must be set to the "data to pixels" transform.</li>
     *   <li>The axis must be at its proper location in the widget area, typically through a
     *       call to {@link #layoutAxis} before this method call}.</li>
     * </ul>
     *
     * @param axis The axis for which to set the graduation.
     * @param isX  {@code true} if the given axis is a X axis.
     */
    private void inferGraduation(final Axis2D axis, final boolean isX) throws NoninvertibleTransformException {
        Point2D P1 = axis.getP1();
        Point2D P2 = axis.getP2();
        P1 = zoom.inverseTransform(P1, P1);
        P2 = zoom.inverseTransform(P2, P2);
        double min, max;
        if (isX) {
            min = P1.getX();
            max = P2.getX();
        } else {
            min = P1.getY();
            max = P2.getY();
        }
        if (min > max) {
            final double tmp = max;
            max = min;
            min = tmp;
        }
        final AbstractGraduation grad = (AbstractGraduation) axis.getGraduation();
        grad.setMinimum(min);
        grad.setMaximum(max);
    }

    /**
     * Paints the axes and all series. At the opposite of typical {@link ZoomPane} subclasses, this
     * method does not use directly the {@linkplain #zoom zoom} transform. The zoom is honored only
     * indirectly since the axis graduations have been determined from the zoom by the
     * {@link #transform(AffineTransform)} method.
     */
    @Override
    protected void paintComponent(final Graphics2D graphics) {
        if (xAxes.isEmpty() || yAxes.isEmpty()) {
            return;
        }
        final Rectangle bounds    = getZoomableBounds(null);
        final Stroke    oldStroke = graphics.getStroke();
        final Paint     oldPaint  = graphics.getPaint();
        final Shape     oldClip   = graphics.getClip();
        final Font      oldFont   = graphics.getFont();
        final Object    oldHint   = graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        /*
         * Draws the grid lines before to paint the series. We use the first (x,y)
         * axes since they are the closest ones to the center of the graph area.
         */
        final Axis2D xAxis = xAxes.get(0);
        final Axis2D yAxis = yAxes.get(0);
        final double xx1 = xAxis.getX1();
        final double xy1 = xAxis.getY1();
        final double xx2 = xAxis.getX2();
        final double xy2 = xAxis.getY2();
        final double yx1 = yAxis.getX1();
        final double yy1 = yAxis.getY1();
        final double yx2 = yAxis.getX2();
        final double yy2 = yAxis.getY2();
        final double xxD = (yx2 - yx1);
        final double xyD = (yy2 - yy1);
        final double yxD = (xx2 - xx1);
        final double yyD = (xy2 - xy1);
        final Line2D line = new Line2D.Double();
        if (gridColor != null) {
            graphics.setPaint(gridColor);
            final Point2D.Double point = new Point2D.Double();
            Axis2D.TickIterator tk = xAxis.new TickIterator(null);
            for (tk.nextMajor(); !tk.isDone(); tk.nextMajor()) {
                tk.currentPosition(point);
                line.setLine(point.x, point.y, point.x + xxD, point.y + xyD);
                graphics.draw(line);
            }
            tk = yAxis.new TickIterator(null);
            for (tk.nextMajor(); !tk.isDone(); tk.nextMajor()) {
                tk.currentPosition(point);
                line.setLine(point.x, point.y, point.x + yxD, point.y + yyD);
                graphics.draw(line);
            }
        }
        /*
         * Paints series first.
         */
        graphics.clip(bounds);
        graphics.setStroke(new BasicStroke((float) (1/getGraphicsScale())));
        final TransformedShape transformed = new TransformedShape();
        for (final Map.Entry<Series,Entry> e : series.entrySet()) {
            final Series series = e.getKey();
            final Entry  entry  = e.getValue();
            final AffineTransform transform = Axis2D.createAffineTransform(entry.xAxis, entry.yAxis);
            final Shape path = series.path();
            transformed.setTransform(transform);
            transformed.setOriginalShape(path);
            graphics.setPaint(series.paint());
            if (series instanceof DefaultSeries && ((DefaultSeries) series).fill) {
                graphics.fill(transformed);
            } else {
                graphics.draw(transformed);
            }
        }
        /*
         * Paints axes on top of series, then paint the remainder of the box
         * around the graph area.
         */
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldHint);
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
        line.setLine(xx2, xy2, xx2+xxD, xy2+xyD); graphics.draw(line);
        line.setLine(yx2, yy2, yx2+yxD, yy2+yyD); graphics.draw(line);
        /*
         * Paints the title.
         */
        if (title != null) {
            final FontRenderContext context = graphics.getFontRenderContext();
            final GlyphVector glyphs = titleFont.createGlyphVector(context, title);
            final Rectangle2D titleBounds = glyphs.getVisualBounds();
            graphics.drawGlyphVector(glyphs, (float) ((getWidth() - titleBounds.getWidth()) / 2), 20);
        }
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
     * @version 3.00
     *
     * @since 1.1
     * @module
     */
    public interface Series {
        /**
         * Returns the name of this series. If only one series is plotted,
         * then the name of that series will be used as the plot title.
         *
         * @return The name of this series, or {@code null} if none.
         */
        String name();

        /**
         * Returns the color to use for plotting this series.
         *
         * @return The color to use for plotting this series.
         */
        Paint paint();

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
     * @version 3.00
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
        private final Paint color;

        /**
         * The path, which may be float or double precision.
         */
        private final Path2D path;

        /**
         * The minimal and maximum (<var>x</var>,<var>y</var>) values.
         */
        private final Rectangle2D bounds;

        /**
         * {@code true} if the {@linkplain #path path} is a closed polygon which should be painted
         * using {@link Graphics2D#fill(Shape)} instead of {@link Graphics2D#draw(Shape)}. This is
         * not a public API at this time. The usual value is {@code false}.
         */
        final boolean fill;

        /**
         * Constructs a series with the given name and (<var>x</var>,<var>y</var>) vectors.
         *
         * @throws MismatchedSizeException if the arrays don't have the same length.
         */
        public DefaultSeries(final String name, final Paint color, final Vector x, final Vector y, boolean fill)
                throws MismatchedSizeException
        {
            this.name  = name;
            this.color = color;
            final int length = x.size();
            if (length != y.size()) {
                throw new MismatchedSizeException(Errors.format(Errors.Keys.MISMATCHED_ARRAY_LENGTH_2, "x", "y"));
            }
            /*
             * Creates a Path2D of Float type if it is sufficient
             * for the provided data, or of Double tpe otherwise.
             */
            final Class<?> type = Numbers.widestClass(
                    Numbers.primitiveToWrapper(x.getElementType()).asSubclass(Number.class),
                    Numbers.primitiveToWrapper(y.getElementType()).asSubclass(Number.class));
            if (type == Double.class || type == Long.class) {
                path = new Path2D.Double();
            } else {
                path = new Path2D.Float();
            }
            /*
             * Creates the shape.
             */
            boolean move = true;
            for (int i=0; i<length; i++) {
                double xi = x.doubleValue(i);
                double yi = y.doubleValue(i);
                if (Double.isNaN(yi) || Double.isNaN(xi)) {
                    if (!move) {
                        fill = false; // We will not be able to close the shape.
                        move = true;
                    }
                    continue;
                }
                if (move) {
                    move = false;
                    path.moveTo(xi, yi);
                } else {
                    path.lineTo(xi, yi);
                }
            }
            if (fill) {
                path.closePath();
            }
            this.fill = fill;
            bounds = path.getBounds2D();
        }

        /**
         * Returns the series name.
         */
        @Override
        public String name() {
            return name;
        }

        /**
         * Returns the color for this series.
         */
        @Override
        public Paint paint() {
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

        /**
         * Returns a string representation for debugging purpose.
         */
        @Override
        public String toString() {
            final Rectangle2D bounds = this.bounds;
            return "Series[\"" + name + "\", " +
                   "x=[" + bounds.getMinX() + " \u2026 " + bounds.getMaxX() + "], " +
                   "y=[" + bounds.getMinY() + " \u2026 " + bounds.getMaxY() + "]]";
        }
    }
}

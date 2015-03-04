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
package org.geotoolkit.gui.swing.image;

import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import java.util.Map;
import java.util.Locale;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.opengis.coverage.Coverage;
import org.opengis.coverage.SampleDimension;

import org.geotoolkit.display.axis.Graduation;
import org.geotoolkit.internal.coverage.CoverageUtilities;


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
 * @version 3.16
 *
 * @since 1.1
 * @module
 */
@SuppressWarnings("serial")
public class ColorRamp extends JComponent {
    /**
     * The object to use for painting the color ramp.
     */
    @SuppressWarnings("serial")
    private final class Painter extends org.geotoolkit.internal.swing.ColorRamp {
        /** Tells the painter that we want to be informed about color changes. */
        @Override protected boolean reportColorChanges() {
            return true;
        }

        /** Allows user to override the {@code createGraduation} method. */
        @Override protected Graduation createGraduation(final Graduation reuse,
                final SampleDimension band, final double minimum, final double maximum)
        {
            return ColorRamp.this.createGraduation(reuse, band, minimum, maximum);
        }

        /** Uses the widget locale for formatting error messages and graduation labels. */
        @Override public Locale getLocale() {
            return ColorRamp.this.getLocale();
        }
    }

    /**
     * The object on which to delegate the paint operations.
     */
    private final Painter painter;

    /**
     * The {@link ComponentUI} object for computing preferred
     * size, drawn the component and handle some events.
     */
    private final UI ui;

    /**
     * Constructs an initially empty color ramp. Colors can be
     * set using one of the {@code setColors(...)} methods.
     */
    public ColorRamp() {
        ui = new UI();
        setOpaque(true);
        setUI(ui);
        painter = new Painter();
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
        return painter.getGraduation();
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
        final Graduation oldGraduation = painter.setGraduation(graduation);
        final boolean changed = !Objects.equals(graduation, oldGraduation);
        if (changed) {
            if (oldGraduation != null) {
                oldGraduation.removePropertyChangeListener(ui);
            }
            if (graduation != null) {
                graduation.addPropertyChangeListener(ui);
            }
            repaint();
            firePropertyChange("graduation", oldGraduation, graduation);
        }
        return changed;
    }

    /**
     * Returns the colors painted by this {@code ColorRamp}.
     *
     * @return The colors (never {@code null}).
     */
    public Color[] getColors() {
        return painter.getColors();
    }

    /**
     * Sets the colors to paint. If the new colors are different than the old ones, then this
     * method fires a {@linkplain PropertyChangeEvent property change event} named {@code "colors"}
     * with values of type {@code Color[]}.
     *
     * @param  colors The colors to paint, or {@code null} if none.
     * @return {@code true} if the state of this {@code ColorRamp} changed as a result of this call.
     *
     * @see #setColors(Coverage)
     * @see #setColors(SampleDimension)
     * @see #setColors(IndexColorModel)
     * @see #getColors()
     * @see #getGraduation()
     */
    public boolean setColors(final Color... colors) {
        return fireColorChange(painter.setColors(colors));
    }

    /**
     * Sets the colors to paint as an array of ARGB values. This method performs the same
     * work than {@link #setColors(Color[])}, but is more efficient if the colors were
     * already available as an array of ARGB values.
     * <p>
     * If the new colors are different than the old ones, then this method fires a
     * {@linkplain PropertyChangeEvent property change event} named {@code "colors"}
     * with values of type {@code Color[]} - not {@code int[]}.
     *
     * @param  colors The colors to paint, or {@code null} if none.
     * @return {@code true} if the state of this {@code ColorRamp} changed as a result of this call.
     *
     * @since 3.09
     */
    public boolean setColors(final int... colors) {
        return fireColorChange(painter.setColors(colors));
    }

    /**
     * Fires a property change event if the colors changed.
     *
     * @param  changes The old and new colors, or {@code null} if there is no change.
     * @return Whatever that were a change.
     */
    private boolean fireColorChange(final Color[][] changes) {
        if (changes == null) {
            return false;
        }
        repaint();
        firePropertyChange("colors", changes[0], changes[1]);
        return true;
    }

    /**
     * Sets the colors to paint from an {@link IndexColorModel}. The default implementation
     * fetches the ARGB values from the index color model and invokes {@link #setColors(int[])}.
     *
     * @param  model The colors to paint, or {@code null} if none.
     * @return {@code true} if the state of this {@code ColorRamp} changed as a result of this call.
     *
     * @see #setColors(Coverage)
     * @see #setColors(SampleDimension)
     * @see #setColors(Color[])
     * @see #getColors()
     * @see #getGraduation()
     */
    public boolean setColors(final IndexColorModel model) {
        int[] colors = null;
        if (model != null) {
            colors = new int[model.getMapSize()];
            model.getRGBs(colors);
        }
        return setColors(colors);
    }

    /**
     * Sets the graduation and the colors from a sample dimension.
     * The default implementation fetches the palette and the minimum and maximum values
     * from the supplied band, and then invokes {@link #setColors(Color[]) setColors} and
     * {@link #setGraduation setGraduation}.
     *
     * @param  band The sample dimension, or {@code null} if none.
     * @return {@code true} if the state of this {@code ColorRamp} changed as a result of this call.
     *
     * @see #setColors(Coverage)
     * @see #setColors(SampleDimension)
     * @see #setColors(IndexColorModel)
     * @see #setColors(Color[])
     * @see #getColors()
     * @see #getGraduation()
     */
    public boolean setColors(final SampleDimension band) {
        final Map.Entry<Graduation,Color[]> entry = painter.getColors(band);
        return setGraduation(entry.getKey()) | setColors(entry.getValue()); // Realy |, not ||
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
     * Returns {@code true} if the colors are interpolated at rendering time. If {@code false},
     * only the colors given to the {@code setColors(...)} method will be used: the color ramp
     * will be painted using rectangles of uniform colors. If {@code true}, then a linear
     * interpolation will be used between every colors given to the {@code setColors(...)}
     * method.
     *
     * @return Whatever the colors will be interpolated at rendering time.
     *
     * @since 3.16
     */
    public boolean isInterpolationEnabled() {
        return painter.interpolationEnabled;
    }

    /**
     * Sets whatever the colors should be interpolated at rendering time.
     * If this method is never invoked, then the default value is {@code true}.
     *
     * @param enabled Whatever the colors will be interpolated at rendering time.
     *
     * @since 3.16
     */
    public void setInterpolationEnabled(final boolean enabled) {
        final boolean old = painter.interpolationEnabled;
        painter.interpolationEnabled = enabled;
        if (old != enabled) {
            firePropertyChange("interpolationEnabled", old, enabled);
            repaint();
        }
    }

    /**
     * Returns the component's orientation (horizontal or vertical). It should be one of the
     * following constants: {@link SwingConstants#HORIZONTAL} or {@link SwingConstants#VERTICAL}.
     *
     * @return The component orientation.
     */
    public int getOrientation() {
        return painter.getOrientation();
    }

    /**
     * Sets the component's orientation (horizontal or vertical).
     *
     * @param orient {@link SwingConstants#HORIZONTAL} or {@link SwingConstants#VERTICAL}.
     */
    public void setOrientation(final int orient) {
        final int old = painter.setOrientation(orient);
        if (old != orient) {
            firePropertyChange("orientation", old, orient);
            repaint();
        }
    }

    /**
     * Tests if graduation labels are paint on top of the
     * colors ramp. Default value is {@code true}.
     *
     * @return {@code true} if graduation labels are drawn.
     */
    public boolean isLabelVisibles() {
        return painter.labelVisibles;
    }

    /**
     * Sets whatever the graduation labels should be painted on top of the colors ramp.
     *
     * @param visible {@code true} if graduation labels should be drawn.
     */
    public void setLabelVisibles(final boolean visible) {
        final boolean old = painter.labelVisibles;
        painter.labelVisibles = visible;
        if (old != visible) {
            firePropertyChange("labelVisibles", old, visible);
            repaint();
        }
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
        painter.autoForeground = (color == null);
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
    final Rectangle2D paint(final Graphics2D graphics, final Rectangle bounds) {
        return painter.paint(graphics, bounds, getFont(), getForeground());
    }

    /**
     * Returns a graduation for the specified sample dimension, minimum and maximum values. If
     * the supplied {@code reuse} object is non-null and is of the appropriate class, then this
     * method can return {@code reuse} without creating a new graduation object. Otherwise this
     * method must returns a graduation of the appropriate class, usually
     * {@link org.geotoolkit.display.axis.NumberGraduation} or
     * {@link org.geotoolkit.display.axis.LogarithmicNumberGraduation}.
     * <p>
     * In every cases, this method must set graduations
     * {@linkplain org.geotoolkit.display.axis.AbstractGraduation#setMinimum minimum},
     * {@linkplain org.geotoolkit.display.axis.AbstractGraduation#setMaximum maximum} and
     * {@linkplain org.geotoolkit.display.axis.AbstractGraduation#setUnit unit}
     * according the values given in arguments.
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
        return Painter.createDefaultGraduation(reuse, band.getSampleToGeophysics(),
                minimum, maximum, band.getUnits(), getLocale());
    }

    /**
     * Returns an image representation for this color ramp. The image size will be this
     * {@linkplain #getSize() widget size}.
     *
     * @return The color ramp as a buffered image.
     *
     * @since 2.4
     */
    public BufferedImage toImage() {
        return painter.toImage(getWidth(), getHeight(), getFont(), getForeground());
    }

    /**
     * Returns a string representation for this color ramp.
     */
    @Override
    public String toString() {
        return painter.toString(getClass());
    }

    /**
     * Notifies this component that it now has a parent component. This method
     * is invoked by <i>Swing</i> and shouldn't be directly used.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        final Graduation graduation = painter.getGraduation();
        if (graduation != null) {
            graduation.removePropertyChangeListener(ui); // Avoid duplication
            graduation.addPropertyChangeListener(ui);
        }
    }

    /**
     * Notifies this component that it no longer has a parent component.
     * This method is invoked by <i>Swing</i> and shouldn't be directly used.
     */
    @Override
    public void removeNotify() {
        final Graduation graduation = painter.getGraduation();
        if (graduation != null) {
            graduation.removePropertyChangeListener(ui);
        }
        super.removeNotify();
    }

    /**
     * Returns the size of a vertical or horizontal rectangle, depending
     * on the orientation of this widget.
     */
    final Dimension getSize(final int size) {
        switch (getOrientation()) {
            case SwingConstants.HORIZONTAL: return new Dimension(size, 16);
            case SwingConstants.VERTICAL:   return new Dimension(16, size);
            default: throw new AssertionError(this);
        }
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
         * Returns the minimal {@link ColorRamp} size.
         */
        @Override
        public Dimension getMinimumSize(final JComponent c) {
            return ((ColorRamp) c).getSize(2*Painter.MARGIN);
        }

        /**
         * Returns the prefered {@link ColorRamp} size.
         */
        @Override
        public Dimension getPreferredSize(final JComponent c) {
            return ((ColorRamp) c).getSize(256);
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
            if (ramp.painter.hasColors()) {
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

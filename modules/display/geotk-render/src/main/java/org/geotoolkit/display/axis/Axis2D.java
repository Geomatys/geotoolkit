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
package org.geotoolkit.display.axis;

import java.awt.Font;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.font.GlyphVector;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;
import java.awt.geom.IllegalPathStateException;

import java.io.Serializable;
import java.util.Map;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import static java.lang.Double.isNaN;
import static java.lang.Double.NaN;

import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.cs.AxisDirection;

import org.geotoolkit.util.Cloneable;
import org.apache.sis.util.Classes;
import org.geotoolkit.display.shape.DoubleDimension2D;
import org.apache.sis.referencing.cs.DefaultCoordinateSystemAxis;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;


/**
 * An axis as a graduated line. {@code Axis2D} objets are really {@link Line2D} objects with a
 * {@link Graduation}. Because axis are {@link Line2D}, they can be located anywhere in a widget
 * with any orientation. Lines are drawn from starting point
 * ({@linkplain #getX1 <var>x<sub>1</sub></var>},{@linkplain #getY1 <var>y<sub>1</sub></var>})
 * to end point
 * ({@linkplain #getX2 <var>x<sub>2</sub></var>},{@linkplain #getY2 <var>y<sub>2</sub></var>}),
 * using a graduation from minimal value {@link Graduation#getMinimum} to maximal
 * value {@link Graduation#getMaximum}.
 * <p>
 * Note the line's coordinates (<var>x<sub>1</sub></var>,<var>y<sub>1</sub></var>) and
 * (<var>x<sub>2</sub></var>,<var>y<sub>2</sub></var>) are completely independent of graduation
 * minimal and maximal values. Line's coordinates should be expressed in some units convenient
 * for rendering, as pixels or point (1/72 of inch). On the opposite, graduation can have any
 * arbitrary units, which is given by {@link Graduation#getUnit}. The
 * {@link #createAffineTransform createAffineTransform} static method can be used for mapping
 * logical coordinates to pixels coordinates for an arbitrary pair of {@code Axis2D} objects,
 * which doesn't need to be perpendicular.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 *
 * @see DefaultCoordinateSystemAxis
 * @see AxisDirection
 * @see Graduation
 *
 * @since 1.0
 * @module
 */
public class Axis2D extends Line2D implements Cloneable, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -8396436909942389360L;

    /**
     * Coordonnées des premier et dernier points de l'axe. Ces coordonnées
     * sont exprimées en "points" (1/72 de pouce), ce qui n'a rien à voir
     * avec les unités de {@link Graduation#getMinimum} et {@link Graduation#getMaximum}.
     */
    private double x1=8, y1=8, x2=648, y2=8;

    /**
     * Longueur des graduations, en points. Chaque graduations sera tracée à partir de
     * {@code [sub]TickStart} (généralement 0) jusqu'à {@code [sub]TickEnd}. Par convention,
     * des valeurs positives désignent l'intérieur du graphique et des valeurs négatives
     * l'extérieur.
     */
    private double tickStart=0, tickEnd=9, subTickStart=0, subTickEnd=5;

    /**
     * Indique dans quelle direction se trouve la graduation de l'axe. La valeur -1 indique
     * qu'il faudrait tourner l'axe dans le sens des aiguilles d'une montre pour qu'il soit
     * par-dessus sa graduation. La valeur +1 indique au contraire qu'il faudrait le tourner
     * dans le sens inverse des aiguilles d'une montre pour le même effet.
     */
    private byte relativeCCW = +1;

    /**
     * Modèle qui contient les minimum, maximum et la graduation de l'axe.
     */
    private final Graduation graduation;

    /**
     * The coordinate system axis object associated to this axis, or {@code null} if it has
     * not been created yet.
     */
    private transient DefaultCoordinateSystemAxis information;

    /**
     * Compte le nombre de modifications apportées à l'axe, afin de détecter
     * les changements faits pendant qu'un itérateur balaye la graduation.
     */
    private transient int modCount;

    /**
     * Indique si {@link #getPathIterator} doit retourner {@link #iterator}. Ce champ prend
     * temporairement la valeur de {@code true} pendant l'exécution de {@link #paint}.
     */
    private transient boolean isPainting;

    /**
     * Itérateur utilisé pour dessiner l'axe lors du dernier appel de
     * la méthode {@link #paint}. Cet itérateur sera réutilisé autant
     * que possible afin de diminuer le nombre d'objets créés lors de
     * chaque traçage.
     */
    private transient TickPathIterator iterator;

    /**
     * Coordonnées de la boîte englobant l'axe (<u>sans</u> ses étiquettes
     * de graduation) lors du dernier traçage par la méthode {@link #paint}.
     * Ces coordonnées sont indépendantes de {@link #lastContext} et ont été
     * obtenues sans transformation affine "utilisateur".
     */
    private transient Rectangle2D axisBounds;

    /**
     * Coordonnées de la boîte englobant les étiquettes de graduations (<u>sans</u>
     * le reste de l'axe) lors du dernier traçage par la méthode {@link #paint}. Ces
     * coordonnées ont été calculées en utilisant {@link #lastContext} mais ont été
     * obtenues sans transformation affine "utilisateur".
     */
    private transient Rectangle2D labelBounds;

    /**
     * Coordonnées de la boîte englobant la légende de l'axe lors du dernier traçage
     * par la méthode {@link #paint}. Ces coordonnées ont été calculées en utilisant
     * {@link #lastContext} mais ont été obtenues sans transformation affine "utilisateur".
     */
    private transient Rectangle2D legendBounds;

    /**
     * Dernier objet {@link FontRenderContext} a avoir été
     * utilisé lors du traçage par la méthode {@link #paint}.
     */
    private transient FontRenderContext lastContext;

    /**
     * Largeur et hauteur maximales des étiquettes de la graduation, ou
     * {@code null} si cette dimension n'a pas encore été déterminée.
     */
    private transient Dimension2D maximumSize;

    /**
     * A default font to use when no rendering hint were provided for the
     * {@link Graduation#TICK_LABEL_FONT} key. Cached here only for performance.
     */
    private transient Font defaultFont;

    /**
     * A set of rendering hints for this axis.
     */
    private RenderingHints hints;

    /**
     * Constructs an axis with a default {@link NumberGraduation}.
     */
    public Axis2D() {
        this(new NumberGraduation(null));
    }

    /**
     * Constructs an axis with the specified graduation.
     *
     * @param graduation The graduation to be given to this axis.
     */
    public Axis2D(final Graduation graduation) {
        this.graduation = graduation;
        graduation.addPropertyChangeListener(new PropertyChangeListener() {
            @Override public void propertyChange(final PropertyChangeEvent event) {
                synchronized (Axis2D.this) {
                    modCount++;
                    clearCache();
                }
            }
        });
    }

    /**
     * Returns the axis's graduation.
     *
     * @return The graduation used by this axis.
     */
    public Graduation getGraduation() {
        return graduation;
    }

    /**
     * Returns the <var>x</var> coordinate of the start point. By convention,
     * this coordinate should be in pixels or points (1/72 of inch) for proper
     * positionning of ticks and labels.
     *
     * @see #getY1
     * @see #getX2
     * @see #setLine
     */
    @Override
    public double getX1() {
        return x1;
    }

    /**
     * Returns the <var>x</var> coordinate of the end point. By convention,
     * this coordinate should be in pixels or points (1/72 of inch) for proper
     * positionning of ticks and labels.
     *
     * @see #getY2
     * @see #getX1
     * @see #setLine
     */
    @Override
    public double getX2() {
        return x2;
    }

    /**
     * Returns the <var>y</var> coordinate of the start point. By convention,
     * this coordinate should be in pixels or points (1/72 of inch) for proper
     * positionning of ticks and labels.
     *
     * @see #getX1
     * @see #getY2
     * @see #setLine
     */
    @Override
    public double getY1() {
        return y1;
    }

    /**
     * Returns the <var>y</var> coordinate of the end point. By convention,
     * this coordinate should be in pixels or points (1/72 of inch) for proper
     * positionning of ticks and labels.
     *
     * @see #getX2
     * @see #getY1
     * @see #setLine
     */
    @Override
    public double getY2() {
        return y2;
    }

    /**
     * Returns the (<var>x</var>,<var>y</var>) coordinates of the start point.
     * By convention, those coordinates should be in pixels or points (1/72 of
     * inch) for proper positionning of ticks and labels.
     */
    @Override
    public synchronized Point2D getP1() {
        return new Point2D.Double(x1,y1);
    }

    /**
     * Returns the (<var>x</var>,<var>y</var>) coordinates of the end point.
     * By convention, those coordinates should be in pixels or points (1/72 of
     * inch) for proper positionning of ticks and labels.
     */
    @Override
    public synchronized Point2D getP2() {
        return new Point2D.Double(x2,y2);
    }

    /**
     * Returns the axis length. This is the distance between starting point (@link #getP1 P1})
     * and end point ({@link #getP2 P2}). This length is usually measured in pixels or points
     * (1/72 of inch).
     *
     * @return The axis length in display units.
     */
    public synchronized double getLength() {
        return Math.hypot(x1 - x2, y1 - y2);
    }

    /**
     * Returns a bounding box for this axis. The bounding box includes the axis's line
     * ({@link #getP1 P1}) to ({@link #getP2 P2}), the axis's ticks and all labels.
     *
     * @see #getX1
     * @see #getY1
     * @see #getX2
     * @see #getY2
     */
    @Override
    public synchronized Rectangle2D getBounds2D() {
        if (axisBounds == null) {
            paint(null); // Force the computation of bounding box size.
        }
        final Rectangle2D bounds = (Rectangle2D) axisBounds.clone();
        if (labelBounds !=null) bounds.add(labelBounds );
        if (legendBounds!=null) bounds.add(legendBounds);
        return bounds;
    }

    /**
     * Sets the location of the endpoints of this {@code Axis2D} to the specified
     * coordinates. Coordinates should be in pixels (for screen rendering) or points
     * (for paper rendering). Using points units make it easy to render labels with
     * a reasonable font size, no matter the screen resolution or the axis graduation.
     *
     * @param x1 Coordinate <var>x</var> of starting point.
     * @param y1 Coordinate <var>y</var> of starting point
     * @param x2 Coordinate <var>x</var> of end point.
     * @param y2 Coordinate <var>y</var> of end point.
     * @throws IllegalArgumentException If a coordinate is {@code NaN} or infinite.
     *
     * @see #getX1
     * @see #getY1
     * @see #getX2
     * @see #getY2
     */
    @Override
    public synchronized void setLine(final double x1, final double y1,
                                     final double x2, final double y2)
            throws IllegalArgumentException
    {
        AbstractGraduation.ensureFinite("x1", x1);
        AbstractGraduation.ensureFinite("y1", y1);
        AbstractGraduation.ensureFinite("x2", x2);
        AbstractGraduation.ensureFinite("y2", y2);
        modCount++; // Must be first
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        clearCache();
    }

    /**
     * Returns {@code true} if the axis would have to rotate clockwise in order to
     * overlaps its graduation.
     *
     * @return {@code true} if the axis would have to rotate clockwise in order to
     *         overlaps its graduation.
     */
    public boolean isLabelClockwise() {
        return relativeCCW < 0;
    }

    /**
     * Sets the label's locations relative to this axis. Value {@code true} means
     * that the axis would have to rotate clockwise in order to overlaps its graduation.
     * Value {@code false} means that the axis would have to rotate counter-clockwise
     * in order to overlaps its graduation.
     *
     * @param c {@code true} if the axis would have to rotate clockwise in order to
     *        overlaps its graduation.
     */
    public synchronized void setLabelClockwise(final boolean c) {
        modCount++; // Must be first
        relativeCCW = c ? (byte) -1 : (byte) +1;
    }

    /**
     * Returns a default font to use when no rendering hint were provided for
     * the {@link Graduation#TICK_LABEL_FONT} key.
     *
     * @return A default font (never {@code null}).
     */
    private synchronized Font getDefaultFont() {
        if (defaultFont == null) {
            defaultFont = new Font("SansSerif", Font.PLAIN, 9);
        }
        return defaultFont;
    }

    /**
     * Returns an iterator object that iterates along the {@code Axis2D} boundary
     * and provides access to the geometry of the shape outline. The shape includes
     * the axis line, graduation and labels. If an optional {@link AffineTransform}
     * is specified, the coordinates returned in the iteration are transformed accordingly.
     *
     * @param transform The transform to apply on the coordinates to be iterated.
     */
    @Override
    public PathIterator getPathIterator(final AffineTransform transform) {
        return getPathIterator(transform, NaN);
    }

    /**
     * Returns an iterator object that iterates along the {@code Axis2D} boundary
     * and provides access to the geometry of the shape outline. The shape includes
     * the axis line, graduation and labels. If an optional {@link AffineTransform}
     * is specified, the coordinates returned in the iteration are transformed accordingly.
     *
     * @param transform The transform to apply on the coordinates to be iterated.
     * @param flatness Control the subdivisions of curves by straight lines.
     */
    @Override
    public synchronized PathIterator getPathIterator(final AffineTransform transform, final double flatness) {
        if (isPainting) {
            TickPathIterator iterator = this.iterator;
            if (iterator != null) {
                iterator.rewind(transform);
            } else {
                this.iterator = iterator = new TickPathIterator(transform);
            }
            return iterator;
        }
        return new CompletePathIterator(transform, flatness);
    }

    /**
     * Draw this axis in the specified graphics context. This method is equivalents
     * to {@code Graphics2D.draw(this)}.  However, this method may be slightly
     * faster and produce better quality output.
     *
     * @param graphics The graphics context to use for drawing.
     */
    public synchronized void paint(final Graphics2D graphics) {
        if (!(getLength() > 0)) {
            return;
        }
        /*
         * Initialise l'itérateur en appelant 'init' (contrairement à 'getPathIterator'
         * qui n'appelle que 'rewind') pour des résultats plus rapides et plus constants.
         */
        TickPathIterator iterator = this.iterator;
        if (iterator != null) {
            iterator.init(null);
        } else {
            this.iterator = iterator = new TickPathIterator(null);
        }
        final boolean sameContext;
        final Shape clip;
        if (graphics != null) {
            clip = graphics.getClip();
            iterator.setFontRenderContext(graphics.getFontRenderContext());
            iterator.setRenderingHint(graphics, Graduation.AXIS_TITLE_FONT);
            iterator.setRenderingHint(graphics, Graduation.TICK_LABEL_FONT);
            final FontRenderContext context = iterator.getFontRenderContext();
            sameContext = clip!=null && context.equals(lastContext);
        } else {
            clip = null;
            sameContext = false;
            iterator.setFontRenderContext(null);
        }
        /*
         * Calcule (si ce n'était pas déjà fait) les coordonnées d'un rectangle qui englobe l'axe et
         * sa graduation (mais sans les étiquettes de graduation).  Cette information nous permettra
         * de vérifier s'il est vraiment nécessaire de redessiner l'axe en vérifiant s'il intercepte
         * avec le "clip" du graphique.
         */
        if (axisBounds == null) {
            axisBounds = new Rectangle2D.Double(Math.min(x1,x2), Math.min(y1,y2),
                                                Math.abs(x2-x1), Math.abs(y2-y1));
            while (!iterator.isDone()) {
                axisBounds.add(iterator.point);
                iterator.next();
            }
        }
        /*
         * Dessine l'axe et ses barres de graduation (mais sans les étiquettes).
         */
        if (graphics != null) {
            if (clip == null || clip.intersects(axisBounds)) try {
                isPainting = true;
                graphics.draw(this);
            } finally {
                isPainting = false;
            }
        }
        /*
         * Dessine les étiquettes de graduations. Ce bloc peut etre exécuté même si
         * 'graphics' est nul.  Dans ce cas, les étiquettes ne seront pas dessinées
         * mais le calcul de l'espace qu'elles occupent sera quand même effectué.
         */
        if (!sameContext || labelBounds==null || clip.intersects(labelBounds) || maximumSize==null)
        {
            Rectangle2D lastLabelBounds = labelBounds = null;
            double maxWidth  = 0;
            double maxHeight = 0;
            iterator.rewind();
            while (!iterator.isTickDone()) {
                if (iterator.isMajorTick()) {
                    final GlyphVector glyphs = iterator.currentLabelGlyphs();
                    final Rectangle2D bounds = iterator.currentLabelBounds();
                    if (glyphs!=null && bounds!=null) {
                        if (lastLabelBounds==null || !lastLabelBounds.intersects(bounds)) {
                            if (graphics!=null && (clip==null || clip.intersects(bounds))) {
                                graphics.drawGlyphVector(glyphs,
                                        (float) bounds.getMinX(),
                                        (float) bounds.getMaxY());
                            }
                            lastLabelBounds = bounds;
                            final double width  = bounds.getWidth();
                            final double height = bounds.getHeight();
                            if (width  > maxWidth)  maxWidth =width;
                            if (height > maxHeight) maxHeight=height;
                        }
                        if (labelBounds == null) {
                            labelBounds = new Rectangle2D.Float();
                            labelBounds.setRect(bounds);
                        } else {
                            labelBounds.add(bounds);
                        }
                    }
                }
                iterator.nextMajor();
            }
            maximumSize = new DoubleDimension2D(maxWidth, maxHeight);
        }
        /*
         * Ecrit la légende de l'axe. Ce bloc peut etre exécuté même si
         * 'graphics' est nul.  Dans ce cas, la légende ne sera pas écrite
         * mais le calcul de l'espace qu'elle occupe sera quand même effectué.
         */
        if (!sameContext || legendBounds==null || clip.intersects(legendBounds)) {
            final String title = graduation.getTitle(true);
            if (title != null) {
                final Font font = iterator.getTitleFont();
                final GlyphVector glyphs = font.createGlyphVector(iterator.getFontRenderContext(), title);
                final AffineTransform rotatedTr = new AffineTransform();
                final Rectangle2D bounds = iterator.centerAxisLabel(glyphs.getVisualBounds(), rotatedTr, maximumSize);
                if (graphics != null) {
                    final AffineTransform currentTr = graphics.getTransform();
                    try {
                        graphics.transform(rotatedTr);
                        graphics.drawGlyphVector(glyphs,
                                (float) bounds.getMinX(),
                                (float) bounds.getMaxY());
                    } finally {
                        graphics.setTransform(currentTr);
                    }
                }
                legendBounds = AffineTransforms2D.transform(rotatedTr, bounds, bounds);
            }
        }
        lastContext = iterator.getFontRenderContext();
    }

    /**
     * Returns the value of a single preference for the rendering algorithms. Hint categories
     * include controls for label fonts and colors. Some of the keys and their associated values
     * are defined in the {@link Graduation} interface.
     *
     * @param  key The key corresponding to the hint to get.
     * @return An object representing the value for the specified hint key, or {@code null}
     *         if no value is associated to the specified key.
     *
     * @see Graduation#TICK_LABEL_FONT
     * @see Graduation#AXIS_TITLE_FONT
     */
    public synchronized Object getRenderingHint(final RenderingHints.Key key) {
        return (hints != null) ? hints.get(key) : null;
    }

    /**
     * Sets the value of a single preference for the rendering algorithms. Hint categories
     * include controls for label fonts and colors.  Some of the keys and their associated
     * values are defined in the {@link Graduation} interface.
     *
     * @param key The key of the hint to be set.
     * @param value The value indicating preferences for the specified hint category.
     *              A {@code null} value removes any hint for the specified key.
     *
     * @see Graduation#TICK_LABEL_FONT
     * @see Graduation#AXIS_TITLE_FONT
     */
    public synchronized void setRenderingHint(final RenderingHints.Key key, final Object value) {
        modCount++;
        if (value != null) {
            if (hints == null) {
                hints = new RenderingHints(key, value);
                clearCache();
            } else if (!value.equals(hints.put(key, value))) {
                clearCache();
            }
        } else if (hints != null) {
            if (hints.remove(key) != null) {
                clearCache();
            }
            if (hints.isEmpty()) {
                hints = null;
            }
        }
    }

    /**
     * Efface la cache interne. Cette méthode doit être appelée
     * chaque fois que des propriétés de l'axe ont changées.
     */
    private void clearCache() {
        axisBounds   = null;
        labelBounds  = null;
        legendBounds = null;
        maximumSize  = null;
        information  = null;
    }

    /**
     * Returns a string representation of this axis.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this));
        return buffer.append("[\"").append(graduation.getTitle(true)).append("\"]").toString();
    }

    /**
     * Returns this axis name and direction. Information include a name (usually the
     * {@linkplain Graduation#getTitle graduation title}) and an direction. The direction is usually
     * {@linkplain AxisDirection#DISPLAY_UP up} or {@linkplain AxisDirection#DISPLAY_DOWN down}
     * for vertical axis, {@linkplain AxisDirection#DISPLAY_RIGHT right} or
     * {@linkplain AxisDirection#DISPLAY_LEFT left} for horizontal axis, or
     * {@linkplain AxisDirection#OTHER other} otherwise.
     *
     * @return Axis name and direction.
     */
    public synchronized DefaultCoordinateSystemAxis toCoordinateSystemAxis() {
        if (information == null) {
            String abbreviation = "z";
            AxisDirection direction = AxisDirection.OTHER;
            if (x1 == x2) {
                if (y1 < y2) {
                    direction = AxisDirection.DISPLAY_DOWN;
                } else if (y1 > y2) {
                    direction = AxisDirection.DISPLAY_UP;
                }
                abbreviation = "y";
            } else if (y1 == y2) {
                if (x1 < x2) {
                    direction = AxisDirection.DISPLAY_RIGHT;
                } else if (x1 > x2) {
                    direction = AxisDirection.DISPLAY_LEFT;
                }
                abbreviation = "x";
            }
            information = new DefaultCoordinateSystemAxis(
                    Collections.singletonMap(IdentifiedObject.NAME_KEY, graduation.getTitle(false)),
                    abbreviation, direction, graduation.getUnit());
        }
        return information;
    }

    /**
     * Creates an affine transform mapping logical to pixels coordinates for a pair
     * of axes. The affine transform will maps coordinates in the following way:
     * <p>
     * <ul>
     *   <li>For each input coordinates (<var>x</var>,<var>y</var>), the <var>x</var> and
     *       <var>y</var> values are expressed in the same units than the {@code xAxis}
     *       and {@code yAxis} graduations, respectively.</li>
     *   <li>The output point is the pixel's coordinates for the (<var>x</var>,<var>y</var>)
     *       values. Changing the <var>x</var> value move the pixel location in parallel with
     *       the {@code xAxis}, which may or may not be horizontal. Changing the <var>y</var>
     *       value move the pixel location in parallel with the {@code yAxis}, which may or
     *       may not be vertical.</li>
     * </ul>
     *
     * @param  xAxis The <var>x</var> axis. This axis doesn't have to be horizontal;
     *               it can have any orientation, including vertical.
     * @param  yAxis The <var>y</var> axis. This axis doesn't have to be vertical;
     *               it can have any orientation, including horizontal.
     * @return An affine transform mapping logical to pixels coordinates.
     */
    public static AffineTransform createAffineTransform(final Axis2D xAxis, final Axis2D yAxis) {
        /*   x
         *  │
         *  │\
         *  │  \ P     Soit:       X  :  l'axe des <var>x</var> du graphique.
         *  │   │                  Y  :  l'axe des <var>y</var> du graphique.
         *   \  │                  P  :  un point à placer sur le graphique.
         *     \│             (Px,Py) :  les composantes du point P selon les axes x et y.
         *       \ y          (Pi,Pj) :  les composantes du point P en coordonnées "pixels".
         *
         * Désignons par <b>ex</b> et <b>ey</b> des vecteurs unitaires dans la direction de l'axe des
         * <var>x</var> et l'axe des <var>y</var> respectivement. Désignons par <b>i</b> et <b>j</b>
         * des vecteurs unitaires vers le droite et vers le haut de l'écran respectivement. On peut
         * décomposer les vecteurs unitaires <b>ex</b> et <b>ey</b> par:
         *
         *          ex = exi*i + exj*j
         *          ey = eyi*i + eyj*j
         * Donc,    P  = Px*ex + Py*ey   =   (Px*exi+Py*eyi)*i + (Px*exj + Py*eyj)*j
         *
         * Cette relation ne s'applique que si les deux systèmes de coordonnées (xy et ij) ont
         * la même origine. En pratique, ce ne sera pas le cas. Il faut donc compliquer un peu:
         *
         *      Pi = (Px-Ox)*exi+(Py-Oy)*eyi + Oi        où (Ox,Oy) sont les minimums des axes des x et y.
         *      Pj = (Px-Ox)*exj+(Py-Oy)*eyj + Oj           (Oi,Oj) est l'origine du système d'axe ij.
         *
         * ┌    ┐   ┌                                ┐┌    ┐     ┌                                      ┐
         * │ Pi │   │ exi   eyi   Oi-(Ox*exi+Oy*eyi) ││ Px │     │ exi*Px + eyi*Py + Oi-(Ox*exi+Oy*oyi) │
         * │ Pj │ = │ exj   eyj   Oj-(Ox*exj+Oy*eyj) ││ Py │  =  │ exj*Px + eyj*Py + Oj-(Ox*exj+Oy*oyj) │
         * │  1 │   │  0    0              1         ││  1 │     │                   1                  │
         * └    ┘   └                                ┘└    ┘     └                                      ┘
         */
        final double px, ox, exi, exj;
        synchronized (xAxis) {
            final Graduation mx = xAxis.getGraduation();
            final double range = mx.getSpan();
            px  = xAxis.getX1();
            exi = (xAxis.getX2() - px) / range;
            exj = (xAxis.getY2() - xAxis.getY1()) / range;
            ox  = mx.getMinimum();
        }
        final double py, oy, eyi, eyj;
        synchronized (yAxis) {
            final Graduation my = yAxis.getGraduation();
            final double range = my.getSpan();
            py  = yAxis.getY1();
            eyi = (yAxis.getX2() - yAxis.getX1()) / range;
            eyj = (yAxis.getY2() - py) / range;
            oy  = my.getMinimum();
        }
        return new AffineTransform(exi, exj, eyi, eyj,
                px - (ox*exi + oy*eyi),
                py - (ox*exj + oy*eyj));
    }





    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////                                                                            ////////
    ////////                            TICK AND PATH ITERATORS                         ////////
    ////////                                                                            ////////
    ////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Iterates along the graduation ticks and provides access to the graduation values. Each
     * {@code Axis2D.TickIterator} object traverses the graduation of the unclosing {@link Axis2D}
     * object independently from any other {@link TickIterator} objects in use at the same time.
     * If a change occurs in the underlying {@link Axis2D} object during the iteration, then
     * {@link #refresh} must be invoked in order to reset the iterator as if a new instance was
     * created. Except for {@link #refresh} method, using the iterator after a change in the
     * underlying {@link Axis2D} may thrown a {@link ConcurrentModificationException}.
     *
     * @author Martin Desruisseaux (MPO, IRD)
     * @version 3.00
     *
     * @since 2.0
     * @module
     */
    public class TickIterator implements org.geotoolkit.display.axis.TickIterator {
        /**
         * The underyling tick iterator.
         */
        @SuppressWarnings("hiding")
        private org.geotoolkit.display.axis.TickIterator iterator;

        /**
         * A copy of {@link Axis2D#hints}. A copy is required because some hints (especially
         * {@link Graduation#VISUAL_AXIS_LENGTH} and {@link Graduation#VISUAL_TICK_SPACING})
         * are going to be overwritten. This map may also contains additional hints provided
         * by {@link Graphics2D} in the {@link Axis2D#paint} method. This object will never
         * be {@code null}.
         */
        @SuppressWarnings("hiding")
        private final RenderingHints hints;

        /**
         * {@code scaleX} and {@code scaleY} are used for scaling logical coordinates to pixel
         * coordinates. Those scale factors <strong>must</strong> be the same than the one that
         * appears in {@link Axis2D#createAffineTransform}.
         */
        private double scaleX, scaleY;

        /**
         * {@code (tickX, tickY)} is a unitary vector perpendicular to the axis.
         */
        private double tickX, tickY;

        /**
         * The minimum value {@link Graduation#getMinimum}.
         * This value is copied here for faster access.
         */
        private double minimum;

        /**
         * Value returned by the last call to {@link #currentLabel}. This value is
         * cached here in order to avoid that {@link #getGlyphVector} computes it again.
         */
        private transient String label;

        /**
         * Value returned by the last call to {@link #getGlyphVector}. This value is
         * cached here in order to avoid that {@link #getBounds} computes it again.
         */
        private transient GlyphVector glyphs;

        /**
         * The font to use for rendering tick. If a rendering hint was provided for the
         * {@link Graduation#TICK_LABEL_FONT} key, then the value is used as the font.
         * Otherwise, a default font is created and used.
         */
        private transient Font font;

        /**
         * The font context from {@link Graphics2D#getFontContext},
         * or {@code null} for a default one.
         */
        private transient FontRenderContext fontContext;

        /**
         * Value of {@link Axis2D#modCount} when {@link #init} was last invoked. This value is
         * used in order to detect changes to the underlying {@link Axis2D} during iteration.
         */
        @SuppressWarnings("hiding")
        private transient int modCount;

        /**
         * Constructs an iterator.
         *
         * @param fontContext Information needed to correctly measure text, or
         *        {@code null} if unknown. This object is usually given by
         *        {@link Graphics2D#getFontRenderContext}.
         */
        @SuppressWarnings({"unchecked","rawtypes"})
        public TickIterator(final FontRenderContext fontContext) {
            /*
             * The unsafe cast below is a workaround for the mismatch between the generic types
             * in the RenderingHints class declaration and in the constructor signature.  It is
             * safe since our API allows only Key objects to be put in the map.  We can not use
             * directly putAll(Axis2D.this.hints) instead because the Axis2D hints may be null.
             */
            this.hints = new RenderingHints((Map) Axis2D.this.hints);
            this.fontContext = fontContext;
            refresh();
        }

        /**
         * Copies a rendering hints from the specified {@link Graphics2D}, providing that
         * it is not already defined.
         */
        final void setRenderingHint(final Graphics2D graphics, final RenderingHints.Key key) {
            if (hints.get(key) == null) {
                final Object value = graphics.getRenderingHint(key);
                if (value != null) {
                    hints.put(key, value);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isDone() {
            return iterator.isDone();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isMajorTick() {
            return iterator.isMajorTick();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double currentPosition() {
            return iterator.currentPosition();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double currentValue() {
            return iterator.currentValue();
        }

        /**
         * Returns the coordinates of the intersection point between current tick
         * and the underlying axis. Units are the same than axis start point
         * ({@linkplain #getX1 <var>x<sub>1</sub></var>},{@linkplain #getY1 <var>y<sub>1</sub></var>})
         * and end point
         * ({@linkplain #getX2 <var>x<sub>2</sub></var>},{@linkplain #getY2 <var>y<sub>2</sub></var>}).
         * This is usually pixels.
         *
         * @param  dest A destination point that stores the intersection coordinates,
         *         or {@code null} to create a new {@link Point2D} object.
         * @return {@code dest}, or a new {@link Point2D} object if {@code dest} was null.
         */
        public Point2D currentPosition(final Point2D dest) {
            final double     position = currentPosition() - minimum;
            final double x = position*scaleX + getX1();
            final double y = position*scaleY + getY1();
            ensureValid();
            if (dest != null) {
                dest.setLocation(x,y);
                return dest;
            }
            return new Point2D.Double(x, y);
        }

        /**
         * Returns the coordinates of the current tick.
         * Units are the same than axis start point
         * ({@linkplain #getX1 <var>x<sub>1</sub></var>},{@linkplain #getY1 <var>y<sub>1</sub></var>})
         * and end point
         * ({@linkplain #getX2 <var>x<sub>2</sub></var>},{@linkplain #getY2 <var>y<sub>2</sub></var>}).
         * This is usually pixels.
         *
         * @param  dest A destination line that stores the current tick coordinates,
         *         or {@code null} to create a new {@link Line2D} object.
         * @return {@code dest}, or a new {@link Line2D} object if {@code dest} was null.
         */
        public Line2D currentTick(final Line2D dest) {
            final boolean isMajorTick = isMajorTick();
            final double position = currentPosition() - minimum;
            final double x  = position*scaleX + getX1();
            final double y  = position*scaleY + getY1();
            final double s1 = isMajorTick ? tickStart : subTickStart;
            final double s2 = isMajorTick ? tickEnd   : subTickEnd;
            final double x1 = x+tickX*s1;
            final double y1 = y+tickY*s1;
            final double x2 = x+tickX*s2;
            final double y2 = y+tickY*s2;
            ensureValid();
            if (dest != null) {
                dest.setLine(x1, y1, x2, y2);
                return dest;
            }
            return new Line2D.Double(x1, y1, x2, y2);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String currentLabel() {
            if (label == null) {
                label = iterator.currentLabel();
            }
            return label;
        }

        /**
         * Returns the label for current tick as a glyphs vector. This method is used
         * together with {@link #currentLabelBounds} for labels rendering. <strong>Do
         * not change the returned {@link GlyphVector}</strong>, since the glyphs vector
         * is not cloned for performance raisons. This method returns {@code null}
         * if it can't produces a glyph vector for current tick.
         *
         * @return The label for the current tick as a glyphs vector.
         */
        public GlyphVector currentLabelGlyphs() {
            if (glyphs == null) {
                final String label = currentLabel();
                if (label != null) {
                    glyphs = getTickFont().createGlyphVector(getFontRenderContext(), label);
                }
            }
            return glyphs;
        }

        /**
         * Returns a bounding box for the current tick label. Units are the same than axis start point
         * ({@linkplain #getX1 <var>x<sub>1</sub></var>},{@linkplain #getY1 <var>y<sub>1</sub></var>})
         * and end point
         * ({@linkplain #getX2 <var>x<sub>2</sub></var>},{@linkplain #getY2 <var>y<sub>2</sub></var>}).
         * This is usually pixels. This method can be used as in the example below:
         *
         * {@preformat java
         *     Axis2D.TickIterator iterator = axis.new TickIterator(null);
         *     while (iterator.hasNext()) {
         *         GlyphVector glyphs = iterator.currentLabelGlyphs();
         *         Rectangle2D bounds = iterator.currentLabelBounds();
         *         graphics.drawGlyphVector(glyphs, (float) bounds.getMinX(), (float) bounds.getMaxY());
         *         iterator.next();
         *     }
         * }
         *
         * This method returns {@code null} if it can't compute bounding box for current tick.
         *
         * @return A bounding box for the current tick label.
         */
        public Rectangle2D currentLabelBounds() {
            final GlyphVector glyphs = currentLabelGlyphs();
            if (glyphs == null) {
                return null;
            }
            final Rectangle2D bounds = glyphs.getVisualBounds();
            final double      height = bounds.getHeight();
            final double      width  = bounds.getWidth();
            final double  tickStart  = (0.5*height) - Math.min(Axis2D.this.tickStart, 0);
            final double    position = currentPosition() - minimum;
            final double x= position*scaleX + getX1();
            final double y= position*scaleY + getY1();
            bounds.setRect(x - (1+tickX)*(0.5*width)  - tickX*tickStart,
                           y + (1-tickY)*(0.5*height) - tickY*tickStart - height,
                           width, height);
            ensureValid();
            return bounds;
        }

        /**
         * Returns the font for tick labels. This is the font used for drawing the tick label
         * formatted by {@link TickIterator#currentLabel}.
         *
         * @return The font (never {@code null}).
         */
        private Font getTickFont() {
            if (font == null) {
                Object candidate = hints.get(Graduation.TICK_LABEL_FONT);
                if (candidate instanceof Font) {
                    font = (Font) candidate;
                } else {
                    font = getDefaultFont();
                }
            }
            return font;
        }

        /**
         * Returns the font for axis title. This is the font used for drawing the title
         * formatted by {@link Graduation#getTitle}.
         *
         * @return The font (never {@code null}).
         */
        final Font getTitleFont() {
            Object candidate = hints.get(Graduation.AXIS_TITLE_FONT);
            if (candidate instanceof Font) {
                return (Font) candidate;
            }
            final Font font = getTickFont();
            return font.deriveFont(Font.BOLD, font.getSize2D() * (12f/9));
        }

        /**
         * Retourne un rectangle centré vis-à-vis l'axe. Les coordonnées de ce rectangle seront
         * les mêmes que celles de l'axe, habituellement des pixels ou des points (1/72 de pouce).
         * Cette méthode s'utilise typiquement comme suit:
         *
         * {@preformat java
         *     Graphics2D  graphics = ...
         *     FontRenderContext fc = graphics.getFontRenderContext();
         *     TickIterator      it = axis.new TickIterator(fc);
         *     Font            font = it.getTitleFont();
         *     String         title = axis.getGraduation().getTitle(true);
         *     GlyphVector   glyphs = font.createGlyphVector(fontContext, title);
         *     Rectangle2D   bounds = centerAxisLabel(glyphs.getVisualBounds());
         *     graphics.drawGlyphVector(glyphs, (float) bounds.getMinX(), (float) bounds.getMaxY());
         * }
         *
         * @param bounds
         *          Un rectangle englobant les caractères à écrire. La position (<var>x</var>,
         *          <var>y</var>) de ce rectangle est généralement (mais pas obligatoirement)
         *          l'origine (0,0). Ce rectangle est habituellement obtenu par un appel à
         *          {@link Font#createGlyphVector(FontContext,String)}.
         * @param toRotate
         *          Si non-nul, transformation affine sur laquelle appliquer une rotation égale à
         *          l'angle de l'axe.  Cette méthode peut limiter la rotation aux quadrants 1 et 2
         *          afin de conserver une lecture agréable du texte.
         * @param maximumSize
         *          Largeur et hauteur maximales des étiquettes de graduation. Cette information
         *          est utilisée pour écarter l'étiquette de l'axe suffisament pour qu'elle
         *          n'écrase pas les étiquettes de graduation.
         * @return
         *          Le rectangle {@code bounds}, modifié pour être centré sur l'axe.
         */
        final Rectangle2D centerAxisLabel(final Rectangle2D     bounds,
                                          final AffineTransform toRotate,
                                          final Dimension2D maximumSize)
        {
            final double height = bounds.getHeight();
            final double width  = bounds.getWidth();
            final double tx = 0;
            final double ty = height + Math.abs(maximumSize.getWidth()*tickX) + Math.abs(maximumSize.getHeight()*tickY);
            final double x1 = getX1();
            final double y1 = getY1();
            final double x2 = getX2();
            final double y2 = getY2();
            /////////////////////////////////////
            //// Compute unit vector (ux,uy) ////
            /////////////////////////////////////
            double ux = x2 - x1;
            double uy = y2 - y1;
            double ul = Math.hypot(ux, uy);
            ux /= ul;
            uy /= ul;
            //////////////////////////////////////////////
            //// Get the central position of the axis ////
            //////////////////////////////////////////////
            double x = 0.5 * (x1+x2);
            double y = 0.5 * (y1+y2);
            ////////////////////////////////////////
            //// Apply the parallel translation ////
            ////////////////////////////////////////
            x += ux*tx;
            y += uy*tx;
            ////////////////////////////////////
            //// Adjust sign of unit vector ////
            ////////////////////////////////////
            ux *= relativeCCW;
            uy *= relativeCCW;
            /////////////////////////////////////////////
            //// Apply the perpendicular translation ////
            /////////////////////////////////////////////
            x += uy*ty;
            y -= ux*ty;
            ///////////////////////////////////
            //// Offset the point for text ////
            ///////////////////////////////////
            final double anchorX = x;
            final double anchorY = y;
            if (toRotate == null) {
                y += 0.5*height * (1-ux);
                x -= 0.5*width  * (1-uy);
            } else {
                if (ux < 0) {
                    ux = -ux;
                    uy = -uy;
                    y += height;
                }
                x -= 0.5*width;
                toRotate.rotate(Math.atan2(uy,ux), anchorX, anchorY);
            }
            bounds.setRect(x, y-height, width, height);
            ensureValid();
            return bounds;
        }

        /**
         * Moves the iterator to the next minor or major tick.
         */
        @Override
        public void next() {
            this.label  = null;
            this.glyphs = null;
            iterator.next();
        }

        /**
         * Moves the iterator to the next major tick. This move ignore any minor ticks
         * between current position and the next major tick.
         */
        @Override
        public void nextMajor() {
            this.label  = null;
            this.glyphs = null;
            iterator.nextMajor();
        }

        /**
         * Reset the iterator on its first tick. All other properties are left unchanged.
         */
        @Override
        public void rewind() {
            this.label  = null;
            this.glyphs = null;
            iterator.rewind();
        }

        /**
         * Reset the iterator on its first tick. If some axis properies have changed (e.g. minimum
         * and/or maximum values), then the new settings are taken in account. This {@link #refresh}
         * method help to reduce garbage-collection by constructing an {@code Axis2D.TickIterator}
         * object only once and reuse it for each axis's rendering.
         */
        public void refresh() {
            synchronized (Axis2D.this) {
                this.label  = null;
                this.glyphs = null;
                // Do NOT modify 'fontContext'.

                final Graduation graduation = getGraduation();
                final double     dx = getX2() - getX1();
                final double     dy = getY2() - getY1();
                final double  range = graduation.getSpan();
                final double length = Math.hypot(dx, dy);
                hints.put(Graduation.VISUAL_AXIS_LENGTH, length);

                this.scaleX   =  dx/range;
                this.scaleY   =  dy/range;
                this.tickX    = -dy/length*relativeCCW;
                this.tickY    = +dx/length*relativeCCW;
                this.minimum  = graduation.getMinimum();
                this.iterator = graduation.getTickIterator(hints, iterator);
                this.modCount = Axis2D.this.modCount;
            }
        }

        /**
         * Retourne le contexte utilisé pour dessiner les caractères.
         * Cette méthode ne retourne jamais {@code null}.
         */
        final FontRenderContext getFontRenderContext() {
            if (fontContext == null) {
                fontContext = new FontRenderContext(null, false, false);
            }
            return fontContext;
        }

        /**
         * Spécifie le contexte à utiliser pour dessiner les caractères,
         * ou {@code null} pour utiliser un contexte par défaut.
         */
        final void setFontRenderContext(final FontRenderContext context) {
            fontContext = context;
        }

        /**
         * Vérifie que l'axe n'a pas changé depuis le dernier appel de {@link #init}.
         * Cette méthode doit être appelée <u>à la fin</u> des méthodes de cette classe
         * qui lisent les champs de {@link Axis2D}.
         */
        final void ensureValid() {
            if (this.modCount != Axis2D.this.modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }




    /**
     * Itérateur balayant l'axe et ses barres de graduations pour leur traçage. Cet itérateur ne
     * balaye pas les étiquettes de graduations. Puisque cet itérateur ne retourne que des droites
     * et jamais de courbes, il ne prend pas d'argument {@code flatness}.
     * <p>
     * <strong>WARNING:</strong> There is a clash in the semantic of {@link #isDone} between the
     * {@link PathIterator} contract and the {@link TickIterator} contract. Since this object is
     * used only for iterating over the shape outline, not over tick, it should be of no concern
     * to the user.
     *
     * @author Martin Desruisseaux (MPO, IRD)
     * @version 3.00
     *
     * @since 2.0
     * @module
     */
    private class TickPathIterator extends TickIterator implements PathIterator {
        /**
         * Transformation affine à appliquer sur les données. Il doit s'agir d'une transformation
         * affine appropriée pour l'écriture de texte (généralement en pixels ou en points). Il ne
         * s'agit <u>pas</u> de la transformation affine créée par
         * {@link Axis2D#createAffineTransform}.
         */
        protected AffineTransform transform;

        /**
         * Coordonnées de la prochaine graduation à retourner par une des méthodes
         * {@code currentSegment(...)}. Ces coordonnées n'auront <u>pas</u>
         * été transformées selon la transformation affine {@link #transform}.
         */
        private final Line2D.Double line = new Line2D.Double();

        /**
         * Coordonnées du prochain point à retourner par une des méthodes
         * {@code currentSegment(...)}. Ces coordonnées auront été
         * transformées selon la transformation affine {@link #transform}.
         */
        private final Point2D.Double point = new Point2D.Double();

        /**
         * Type du prochain segment. Ce type est retourné par les méthodes
         * {@code currentSegment(...)}. Il doit s'agir en général d'une
         * des constantes {@link #SEG_MOVETO} ou {@link #SEG_LINETO}.
         */
        private int type = SEG_MOVETO;

        /**
         * Entier indiquant quel sera le prochain item a retourner (début ou
         * fin d'une graduation, début ou fin de l'axe, etc.). Il doit s'agir
         * d'une des constantes {@link #AXIS_MOVETO}, {@link #AXIS_LINETO},
         * {@link #TICK_MOVETO},  {@link #TICK_LINETO}, etc.
         */
        private int nextType = AXIS_MOVETO;

        /** Constante pour {@link #nextType}.*/ private static final int AXIS_MOVETO = 0;
        /** Constante pour {@link #nextType}.*/ private static final int AXIS_LINETO = 1;
        /** Constante pour {@link #nextType}.*/ private static final int TICK_MOVETO = 2;
        /** Constante pour {@link #nextType}.*/ private static final int TICK_LINETO = 3;

        /**
         * Construit un itérateur.
         *
         * @param transform Transformation affine à appliquer sur les données. Il doit
         *            s'agir d'une transformation affine appropriée pour l'écriture de
         *            texte (généralement en pixels ou en points). Il ne s'agit <u>pas</u>
         *            de la transformation affine créée par {@link Axis2D#createAffineTransform}.
         */
        public TickPathIterator(final AffineTransform transform) {
            super(null);
            // 'refresh' est appelée par le constructeur parent.
            this.transform = transform;
            next();
        }

        /**
         * Initialise cet itérateur. Cette méthode peut être appelée pour réutiliser un itérateur
         * qui a déjà servit, plutôt que d'en construire un autre.
         *
         * @param transform Transformation affine à appliquer sur les données. Il doit
         *        s'agir d'une transformation affine appropriée pour l'écriture de
         *        texte (généralement en pixels ou en points). Il ne s'agit <u>pas</u>
         *        de la transformation affine créée par {@link Axis2D#createAffineTransform}.
         */
        final void init(final AffineTransform transform) {
            refresh();
            setFontRenderContext(null);
            this.type      = SEG_MOVETO;
            this.nextType  = AXIS_MOVETO;
            this.transform = transform;
            next();
        }

        /**
         * Repositione l'itérateur au début de la graduation
         * avec une nouvelle transformation affine.
         */
        public void rewind(final AffineTransform transform) {
            super.rewind();
            // Keep 'fontContext'.
            this.type      = SEG_MOVETO;
            this.nextType  = AXIS_MOVETO;
            this.transform = transform;
            next();
        }

        /**
         * Repositione l'itérateur au début de la graduation
         * en conservant la transformation affine actuelle.
         */
        @Override
        public final void rewind() {
            rewind(transform);
        }

        /**
         * Return the winding rule for determining the insideness of the path.
         */
        @Override
        public final int getWindingRule() {
            return WIND_NON_ZERO;
        }

        /**
         * Returns {@code true} if the underlying tick iterator is done.
         * This method is defined as a workaround for the clash between
         * {@link PathIterator#isDone} and {@link TickIterator#isDone}.
         */
        final boolean isTickDone() {
            return super.isDone();
        }

        /**
         * Tests if the iteration is complete. This is the {@link PathIterator#isDone()}
         * method which is defined here. In order to query {@link TickIterator#isDone()},
         * invoke {@link #isTickDone()} instead.
         */
        @Override
        public boolean isDone() {
            return (nextType == TICK_LINETO) && super.isDone();
        }

        /**
         * Returns the coordinates and type of the current path segment
         * in the iteration. The return value is the path segment type:
         * {@code SEG_MOVETO} or {@code SEG_LINETO}.
         */
        @Override
        public int currentSegment(final float[] coords) {
            coords[0] = (float) point.x;
            coords[1] = (float) point.y;
            return type;
        }

        /**
         * Returns the coordinates and type of the current path segment
         * in the iteration. The return value is the path segment type:
         * {@code SEG_MOVETO} or {@code SEG_LINETO}.
         */
        @Override
        public int currentSegment(final double[] coords) {
            coords[0] = point.x;
            coords[1] = point.y;
            return type;
        }

        /**
         * Moves the iterator to the next segment of the path forwards
         * along the primary direction of traversal as long as there are
         * more points in that direction.
         */
        @Override
        public void next() {
            switch (nextType) {
                default: { // Should not happen
                    throw new IllegalPathStateException(Integer.toString(nextType));
                }
                case AXIS_MOVETO: { // Premier point de l'axe
                    point.x  = getX1();
                    point.y  = getY1();
                    type     = SEG_MOVETO;
                    nextType = AXIS_LINETO;
                    break;
                }
                case AXIS_LINETO: { // Fin de l'axe
                    point.x  = getX2();
                    point.y  = getY2();
                    type     = SEG_LINETO;
                    nextType = TICK_MOVETO;
                    break;
                }
                case TICK_MOVETO: { // Premier point d'une graduation
                    currentTick(line);
                    point.x  = line.x1;
                    point.y  = line.y1;
                    type     = SEG_MOVETO;
                    nextType = TICK_LINETO;
                    break;
                }
                case TICK_LINETO: { // Dernier point d'une graduation
                    point.x  = line.x2;
                    point.y  = line.y2;
                    type     = SEG_LINETO;
                    nextType = TICK_MOVETO;
                    prepareLabel();
                    super.next();
                    break;
                }
            }
            if (transform != null) {
                transform.transform(point, point);
            }
            ensureValid();
        }

        /**
         * Méthode appelée automatiquement par {@link #next} pour
         * indiquer qu'il faudra se préparer à tracer une étiquette.
         */
        protected void prepareLabel() {
        }
    }




    /**
     * Itérateur balayant l'axe et ses barres de graduations pour leur traçage.
     * Cet itérateur balaye aussi les étiquettes de graduations.
     *
     * @author Martin Desruisseaux (MPO, IRD)
     * @version 3.00
     *
     * @since 2.0
     * @module
     */
    private final class CompletePathIterator extends TickPathIterator {
        /**
         * Controle le remplacement des courbes par des droites. La valeur
         * {@link java.lang.Double#NaN} indique qu'un tel remplacement n'a pas lieu.
         */
        private final double flatness;

        /**
         * Chemin de l'étiquette {@link #label}.
         */
        private PathIterator path;

        /**
         * Etiquette de graduation à tracer.
         */
        private Shape label;

        /**
         * Rectangle englobant l'étiquette {@link #label} courante.
         */
        @SuppressWarnings("hiding")
        private Rectangle2D labelBounds;

        /**
         * Valeur maximale de {@code labelBounds.getWidth()} trouvée jusqu'à maintenant.
         */
        private double maxWidth = 0;

        /**
         * Valeur maximale de {@code labelBounds.getHeight()} trouvée jusqu'à maintenant.
         */
        private double maxHeight = 0;

        /**
         * Prend la valeur {@code true} lorsque la légende de l'axe a été écrite.
         */
        private boolean isDone;

        /**
         * Construit un itérateur.
         *
         * @param transform Transformation affine à appliquer sur les données. Il doit
         *        s'agir d'une transformation affine appropriée pour l'écriture de
         *        texte (généralement en pixels ou en points). Il ne s'agit <u>pas</u>
         *        de la transformation affine créée par {@link Axis2D#createAffineTransform}.
         * @param flatness Contrôle le remplacement des courbes par des droites. La valeur
         *        {@link java.lang.Double#NaN} indique qu'un tel remplacement ne doit pas être fait.
         */
        public CompletePathIterator(final AffineTransform transform, final double flatness) {
            super(transform);
            this.flatness = flatness;
        }

        /**
         * Retourne un itérateur balayant la forme géométrique spécifiée.
         */
        private PathIterator getPathIterator(final Shape shape) {
            return isNaN(flatness) ? shape.getPathIterator(transform)
                                   : shape.getPathIterator(transform, flatness);
        }

        /**
         * Lance une exception; cet itérateur n'est conçu pour n'être utilisé qu'une seule fois.
         */
        @Override
        public void rewind(final AffineTransform transform) {
            throw new UnsupportedOperationException();
        }

        /**
         * Tests if the iteration is complete.
         */
        @Override
        public boolean isDone() {
            return (path != null) ? path.isDone() : super.isDone();
        }

        /**
         * Returns the coordinates and type of the current path segment in the iteration.
         */
        @Override
        public int currentSegment(final float[] coords) {
            return (path != null) ? path.currentSegment(coords) : super.currentSegment(coords);
        }

        /**
         * Returns the coordinates and type of the current path segment in the iteration.
         */
        @Override
        public int currentSegment(final double[] coords) {
            return (path != null) ? path.currentSegment(coords) : super.currentSegment(coords);
        }

        /**
         * Moves the iterator to the next segment of the path forwards along the primary
         * direction of traversal as long as there are more points in that direction.
         */
        @Override
        public void next() {
            if (path != null) {
                path.next();
                if (!path.isDone()) {
                    return;
                }
                path = null;
            }
            if (label != null) {
                path  = getPathIterator(label);
                label = null;
                if (path != null) {
                    if (!path.isDone()) {
                        return;
                    }
                    path = null;
                }
            }
            if (!isDone) {
                super.next();
                if (isDone()) {
                    /*
                     * Quand tout le reste est terminé, prépare l'écriture de la légende de l'axe.
                     */
                    isDone = true;
                    final String title = graduation.getTitle(true);
                    if (title != null) {
                        final GlyphVector glyphs;
                        glyphs = getTitleFont().createGlyphVector(getFontRenderContext(), title);
                        if (transform != null) {
                            transform = new AffineTransform(transform);
                        } else {
                            transform = new AffineTransform();
                        }
                        final Rectangle2D bounds = centerAxisLabel(glyphs.getVisualBounds(),
                                transform, new DoubleDimension2D(maxWidth, maxHeight));
                        path = getPathIterator(glyphs.getOutline((float) bounds.getMinX(),
                                                                 (float) bounds.getMaxY()));
                    }
                }
            }
        }

        /**
         * Méthode appelée automatiquement par {@link #next} pour
         * indiquer qu'il faudra se préparer à tracer une étiquette.
         */
        @Override
        protected void prepareLabel() {
            if (isMajorTick()) {
                final GlyphVector glyphs = currentLabelGlyphs();
                final Rectangle2D bounds = currentLabelBounds();
                if (glyphs!=null && bounds!=null) {
                    if (labelBounds==null || !labelBounds.intersects(bounds)) {
                        label = glyphs.getOutline((float) bounds.getMinX(),
                                                  (float) bounds.getMaxY());
                        final double width  = bounds.getWidth();
                        final double height = bounds.getHeight();
                        if (width  > maxWidth)  maxWidth =width;
                        if (height > maxHeight) maxHeight=height;
                        labelBounds=bounds;
                    }
                }
            }
        }
    }
}

/*
 * Geotools 2 - OpenSource mapping toolkit
 * (C) 2003, Geotools Project Managment Committee (PMC)
 * (C) 2001, Institut de Recherche pour le Développement
 * (C) 1998, Pêches et Océans Canada
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
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.geotoolkit.display2d.ext.vectorfield;


import java.awt.Font;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.font.GlyphVector;

import org.opengis.referencing.operation.TransformException;


/**
 * Provides the mechanism for {@link RenderedGridMarks} layer to return the appearance of
 * their marks. This class allows the layer to retrieve the appearance of its marks a mark
 * at a time.  The default implementation paints circles at mark locations. Subclasses must
 * implement at least the following methods:
 *
 * <ul>
 *   <li>{@link #getIteratorPosition}</li>
 *   <li>{@link #setIteratorPosition}</li>
 *   <li>{@link #next}</li>
 *   <li>{@link #position}</li>
 * </ul>
 *
 * Si, à la position de chaque marque, on souhaite dessiner une figure orientable dans l'espace
 * (par exemple une flèche de courant ou une ellipse de marée), la classe dérivée pourra redéfinir
 * une ou plusieurs des méthodes ci-dessous. Redéfinir ces méthodes permet par exemple de dessiner
 * des flèches dont la forme exacte (par exemple une, deux ou trois têtes) et la couleur varie avec
 * l'amplitude, la direction ou d'autres critères de votre choix.
 *
 * <ul>
 *   <li>{@link #amplitude}</li>
 *   <li>{@link #direction}</li>
 *   <li>{@link #markShape}</li>
 *   <li>{@link #geographicArea}</li>
 *   <li>{@link #label}</li>
 *   <li>{@link #labelPosition}</li>
 * </ul>
 *
 * @source $URL$
 * @version $Id$
 * @author Martin Desruisseaux
 */
public abstract class MarkIterator {
    /**
     * Forme géométrique à utiliser par défaut lorsqu'aucune autre forme n'a
     * été spécifiée. La position centrale de la station doit correspondre à
     * la coordonnée (0,0) de cette forme. La dimension de cette forme est
     * exprimée en pixels. La forme par défaut sera un cercle centré à
     * (0,0) et d'un diamètre de 10 pixels.
     */
    static final Shape DEFAULT_SHAPE = new Ellipse2D.Float(-5, -5, 10, 10);

    /**
     * Default color for marks.
     */
    static final Color DEFAULT_COLOR = new Color(102, 102, 153, 192);

    /**
     * The default font.
     */
    private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /**
     * The default font, to be used if the user didn't overrided {@link #font()} method.
     * This font is set by {@link RenderedMarks} at rendering time.
     */
    transient Font font = DEFAULT_FONT;

    /**
     * Construct a mark iterator.  The <code>MarkIterator</code> is initially positioned
     * before the first mark. A call to {@link #next} must be done before the first mark
     * can be read.
     */
    public MarkIterator() {
    }

    /**
     * Returns the current iterator position. A newly created <code>MarkIterator</code> will
     * returns -1, since new iterators are initially positioned before the first mark.
     */
    public abstract int getIteratorPosition();

    /**
     * Moves the iterator the specified mark. A call to <code>setIteratorPosition(0)</code> moves
     * the iterator on the first mark. A call to <code>setIteratorPosition(-1)</code> moves the
     * iterator to the front of this <code>MarkIterator</code> object, just before the first mark
     * (i.e. in the same position as when this <code>MarkIterator</code> object was just created).
     *
     * @param  index The new position (0 for the first mark, 1 for the second one, etc.).
     * @throws IllegalArgumentException if <code>index</code> is smaller than -1 or greater
     *         than the last position allowed.
     */
    public abstract void setIteratorPosition(int index) throws IllegalArgumentException;

    /**
     * Moves the iterator to the next marks. A <code>MarkIterator</code> is initially positioned
     * before the first mark; the first call to the method <code>next()</code> makes the first
     * mark the current mark; the second call makes the second mark the current mark, and so on.
     * This method is usually invoked in a <code>while</code> loop as below:
     *
     * <blockquote><pre>
     * MarkIterator it = layer.{@linkplain RenderedMarks#getMarkIterator getMarkIterator()};
     * while (it.next()) {
     *     // <cite>Query current mark properties here...</cite>
     * }
     * </pre></blockquote>
     *
     * @return <code>true</code> if the new current mark is valid;
     *         <code>false</code> if there are no more marks.
     */
    public abstract boolean next();

    /**
     * Returns <code>true</code> if the current mark should be painted.
     * The default implementation returns always <code>true</code>.
     */
    public boolean visible() {
        return true;
    }

    /**
     * Returns <code>true</code> if the current mark is visible in the specified clip. The rectangle
     * <code>clip</code> must have been created by {@link RenderedMarks#getGridClip}. This method is
     * overriden by {@link RenderedGridMarks#Iterator}.
     */
    boolean visible(final Rectangle clip) {
        return visible();
    }

    /**
     * Returns the (<var>x</var>,<var>y</var>) coordinates for the current mark.  Coordinates must
     * be expressed according the {@linkplain RenderedMarks#getCoordinateSystem layer's coordinate
     * system} (a geographic one by default). This method can returns <code>null</code> if the
     * current mark location is unknow.
     *
     * @throws TransformException if a transform was required and failed.
     *
     * @see #geographicArea
     */
    public abstract Point2D position() throws TransformException;

    /**
     * Returns the horizontal amplitude for the current mark. This amplitude tells how big a
     * mark should be painted. It is useful for painting wind arrows or some other quantifiable
     * marks. The default implementation returns always 1.
     */
    public double amplitude() {
        return 1;
    }

    /**
     * Returns the arithmetic direction for the current mark. This angle must be expressed
     * in arithmetic radians (i.e. angle 0 point toward the right and angles increase
     * counter-clockwise). This information is useful for painting wind arrows for example.
     * The default implementation returns always 0.
     */
    public double direction() {
        return 0;
    }

    /**
     * Returns the geometric shape for the current mark. This shape may be mark dependent, or be
     * the same for all marks. This shape must be centred at the origin (0,0) and its coordinates
     * must be expressed in dots (1/72 of inch). For example in order to paint wind arrows, this
     * shape should be oriented toward positives <var>x</var> (i.e. toward 0 arithmetic radians),
     * has a base centred at (0,0) and have a raisonable size (for example 16&times;4 pixels).
     * The method {@link RenderedMarks#paint(RenderingContext)} will automatically takes care of
     * rotation, translation and scale in order to adjust this model to each mark properties.
     * The default implementation returns a circle centred at (0,0) with a diameter of 10 dots.
     * <br><br>
     * The returned shape should be treated as immutable. Implementations are encouraged
     * to return shared instances as much as possible.
     */
    public Shape markShape() {
        return DEFAULT_SHAPE;
    }

    /**
     * Returns the icon for the current mark, or <code>null</code> if none.
     * The default implementation returns always <code>null</code>.
     */
    public RenderedImage markIcon() {
        return null;
    }

    /**
     * Returns the geographic area around the current mark, or <code>null</code> if none.
     * This area must be expressed according the {@linkplain RenderedMarks#getCoordinateSystem
     * layer's coordinate system}. Usually (but not mandatory), this area will contains the point
     * returned by {@link #position}. A typical use of this method is for painting the land which
     * belong to a building (the mark). The default implementation returns always <code>null</code>,
     * which means that this layer paint marks without geographic area.
     */
    public Shape geographicArea() {
        return null;
    }

    /**
     * Returns the font for current label. The default implementation returns a default
     * font, which is rendering dependent.
     */
    public Font font() {
        return font;
    }

    /**
     * Returns the label for the current mark, or <code>null</code> if none.
     * The default implementation returns always <code>null</code>.
     */
    public String label() {
        return null;
    }

    /**
     * Returns the label position relative to the mark's {@linkplain #position position}.
     * The default implementation returns always {@link LegendPosition#CENTER}.
     */
    public LegendPosition labelPosition() {
        return LegendPosition.CENTER;
    }

    /**
     * Returns the paint for current mark.
     */
    public Paint markPaint() {
        return DEFAULT_COLOR;
    }

    /**
     * Paint a mark, its geographic area and its label. This method is automatically invoked from
     * {@link RenderedMarks#paint(RenderingContext)} at rendering time.   Subclasses can override
     * this method in order to gets finer control on the rendering process of marks (e.g. colors,
     * stroke, painting order, etc.). The default implementation use the following pseudo-code:
     *
     * <blockquote><pre>
     *   if (<var>geographicArea</var> != null) {
     *       graphics.setColor(<cite>some default color</cite>);
     *       graphics.draw(<var>geographicArea</var>);
     *   }
     *   if (<var>markIcon</var> != null) {
     *       graphics.drawRenderedImage(<var>markIcon</var>, <var>iconXY</var>);
     *   }
     *   if (<var>markShape</var> != null) {
     *       graphics.setPaint({@linkplain #markPaint});
     *       graphics.fill(<var>markShape</var>);
     *   }
     *   if (<var>label</var> != null) {
     *       graphics.setColor(<cite>some default color</cite>);
     *       graphics.drawGlyphVector(<var>label</var>, <var>labelXY</var>.x, <var>labelXY</var>.y);
     *   }
     * </pre></blockquote>
     *
     * @param graphics  The graphics where painting while occurs. The affine transform is set to
     *                  the Java2D default, also know as the {@linkplain RenderingContext#textCS
     *                  text coordinate system} in this library.
     * @param geographicArea The geographic area which belong to the mark, or <code>null</code> if
     *                  none. This shape is already transformed in the Java2D coordinate system.
     * @param markShape The shape for the mark, or <code>null</code> if none. This shape is already
     *                  transformed in the Java2D coordinate system.
     * @param markIcon  The icon for the mark, or <code>null</code> if none.
     * @param iconXY    The affine transform to apply on <code>markIcon</code>. May be
     *                  <code>null</code> if <code>markIcon</code> was <code>null</code>.
     * @param label     The label to draw, or <code>null</code> if none.
     * @param labelXY   The label position. May be <code>null</code> if <code>label</code> was
     *                  <code>null</code>.
     */
    protected void paint(final Graphics2D      graphics,
                         final Shape           geographicArea,
                         final Shape           markShape,
                         final RenderedImage   markIcon,
                         final AffineTransform iconXY,
                         final GlyphVector     label,
                         final Point2D.Float   labelXY)
    {
                
        if (geographicArea != null) {
            graphics.setColor(Color.ORANGE);
            graphics.draw(geographicArea);
        }
        if (markIcon != null) {
            graphics.drawRenderedImage(markIcon, iconXY);
        }
        if (markShape != null) {
            graphics.setPaint(markPaint());
            graphics.fill(markShape);
        }
        if (label != null) {
            graphics.setColor(Color.BLACK);
            graphics.drawGlyphVector(label, labelXY.x, labelXY.y);
        }
    }
    
//
//    /**
//     * Returns a tooltip text for the current mark, or <code>null</code> if none.
//     * The default implementation returns always <code>null</code>.
//     *
//     * <strong>Note: This method is not a commited part of the API.
//     *         It may moves elsewhere in a future version.</strong>
//     *
//     * @param  event The mouse event.
//     * @return The tool tip text for the current mark, or <code>null</code> if none.
//     */
//    protected String getToolTipText(GeoMouseEvent event) {
//        return null;
//    }
//
//    /**
//     * Returns an action text for the current mark, or <code>null</code> if none.
//     * This method may be call for example on mouse click. The action doesn't need
//     * to be executed immediately. For example it may be put in a tool bar, or saved
//     * in a macro recorder. The default implementation returns always <code>null</code>.
//     *
//     * <strong>Note: This method is not a commited part of the API.
//     *         It may moves elsewhere in a future version.</strong>
//     *
//     * @param  event The mouse event.
//     * @return The action for the current mark, or <code>null</code> if none.
//     */
//    protected Action getAction(GeoMouseEvent event) {
//        return null;
//    }
    
}
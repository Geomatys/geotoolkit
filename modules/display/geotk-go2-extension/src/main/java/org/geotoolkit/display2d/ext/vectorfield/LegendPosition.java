/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.ext.vectorfield;


import java.awt.geom.Rectangle2D;
import javax.swing.SwingConstants;
import java.io.ObjectStreamException;
import java.util.NoSuchElementException;
import javax.media.jai.EnumeratedParameter;


/**
 * Position of a legend relative to the widget's bounds.
 * The position may be one of the following constants:
 * <br><br>
 * <table border="2" cellpadding="8" bgcolor="floralwhite">
 * <tr><td>{@link #NORTH_WEST}</td>  <td>{@link  #NORTH}</td>  <td>{@link #NORTH_EAST}</td></tr>
 * <tr><td>{@link       #WEST}</td>  <td>{@link #CENTER}</td>  <td>{@link       #EAST}</td></tr>
 * <tr><td>{@link #SOUTH_WEST}</td>  <td>{@link  #SOUTH}</td>  <td>{@link #SOUTH_EAST}</td></tr>
 * </table>
 *
 * @source $URL$
 * @version $Id$
 * @author Martin Desruisseaux
 */
public final class LegendPosition extends EnumeratedParameter {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 3829355545629152600L;
    
    /**
     * The central position in an area.
     */
    public static final LegendPosition CENTER =
                        new LegendPosition("CENTER", SwingConstants.CENTER);
    
    /**
     * Compass-direction North (up).
     */
    public static final LegendPosition NORTH =
                        new LegendPosition("NORTH", SwingConstants.NORTH);
    
    /**
     * Compass-direction north-east (upper right).
     */
    public static final LegendPosition NORTH_EAST =
                        new LegendPosition("NORTH_EAST", SwingConstants.NORTH_EAST);
    
    /**
     * Compass-direction east (right).
     */
    public static final LegendPosition EAST =
                        new LegendPosition("EAST", SwingConstants.EAST);
    
    /**
     * Compass-direction south-east (lower right).
     */
    public static final LegendPosition SOUTH_EAST =
                        new LegendPosition("SOUTH_EAST", SwingConstants.SOUTH_EAST);
    
    /**
     * Compass-direction south (down).
     */
    public static final LegendPosition SOUTH =
                        new LegendPosition("SOUTH", SwingConstants.SOUTH);
    
    /**
     * Compass-direction south-west (lower left).
     */
    public static final LegendPosition SOUTH_WEST =
                        new LegendPosition("SOUTH_WEST", SwingConstants.SOUTH_WEST);
    
    /**
     * Compass-direction west (left).
     */
    public static final LegendPosition WEST =
                        new LegendPosition("WEST", SwingConstants.WEST);
    
    /**
     * Compass-direction north west (upper left).
     */
    public static final LegendPosition NORTH_WEST =
                        new LegendPosition("NORTH_WEST", SwingConstants.NORTH_WEST);

    /**
     * Legen positions by value. Used to canonicalize after deserialization.
     */
    private static final LegendPosition[] ENUMS =
                         {CENTER,NORTH,NORTH_EAST,EAST,SOUTH_EAST,SOUTH,SOUTH_WEST,WEST,NORTH_WEST};
    static {
        for (int i=0; i<ENUMS.length; i++) {
            if (ENUMS[i].getValue()!=i) {
                throw new AssertionError(ENUMS[i]);
            }
        }
    }
    
    /**
     * Constructs a new enum with the specified value.
     */
    private LegendPosition(final String name, final int value) {
        super(name, value);
    }
    
    /**
     * Returns the enum for the specified value. This method is provided for compatibility with
     * {@link SwingConstants}.
     *
     * @param value The enum value.
     * @return The enum for the specified value.
     * @throws NoSuchElementException if there is no enum for the specified value.
     */
    public static LegendPosition getEnum(final int value) throws NoSuchElementException {
        if (value>=0 && value<ENUMS.length) return ENUMS[value];
        throw new NoSuchElementException(String.valueOf(value));
    }

    /**
     * Uses a single instance of {@link LegendPosition} after deserialization.
     * It allows client code to test <code>enum1==enum2</code> instead of
     * <code>enum1.equals(enum2)</code>.
     *
     * @return A single instance of this enum.
     * @throws ObjectStreamException if deserialization failed.
     */
    private Object readResolve() throws ObjectStreamException {
        return getEnum(getValue());
    }

    /**
     * Retourne la position horizontale correspondant au quadran spécifié. Cette position sera
     * {@link SwingConstants#LEFT}, {@link SwingConstants#CENTER} ou {@link SwingConstants#RIGHT}.
     *
     * @return Alignement horizontal du quadrant spécifié.
     */
    final int getHorizontalAlignment() {
        switch (getValue()) {
            case SwingConstants.NORTH_EAST: // Fall through
            case SwingConstants.SOUTH_EAST: // Fall through
            case SwingConstants.      EAST: return SwingConstants.RIGHT;
            case SwingConstants.NORTH_WEST: // Fall through
            case SwingConstants.SOUTH_WEST: // Fall through
            case SwingConstants.      WEST: return SwingConstants.LEFT;
            case SwingConstants.     NORTH: // Fall through
            case SwingConstants.     SOUTH: // Fall through
            case SwingConstants.    CENTER: return SwingConstants.CENTER;
            default: throw new IllegalStateException();
        }
    }

    /**
     * Retourne la position verticale correspondant au quadran spécifié. Cette position sera
     * {@link SwingConstants#TOP}, {@link SwingConstants#CENTER} ou {@link SwingConstants#BOTTOM}.
     *
     * @return Alignement vertical du quadrant spécifié.
     */
    final int getVerticalAlignment() {
        switch (getValue()) {
            case SwingConstants.NORTH_EAST: // Fall through
            case SwingConstants.NORTH_WEST: // Fall through
            case SwingConstants.     NORTH: return SwingConstants.TOP;
            case SwingConstants.SOUTH_EAST: // Fall through
            case SwingConstants.SOUTH_WEST: // Fall through
            case SwingConstants.     SOUTH: return SwingConstants.BOTTOM;
            case SwingConstants.      EAST: // Fall through
            case SwingConstants.      WEST: // Fall through
            case SwingConstants.    CENTER: return SwingConstants.CENTER;
            default: throw new IllegalStateException();
        }
    }

    /**
     * Set the location of a rectangle in such a way that the specified point is located at
     * the compass-direction represented by this <code>LegendPosition</code>.   For example
     * <code>{@link #EAST}.getLocation(rectangle, 10, 20)</code> will translate the rectangle
     * in such a way that the coordinate (10, 20) is located in the midle of the right side.
     * <br><br>
     * Note that this method use the Java2D default coordinate system, in which the origin (0,0)
     * is located at the upper-left corner. Consequently, direction {@link #WEST} point toward
     * minimal {@linkplain Rectangle2D#getX x} while direction {@link #NORTH} point toward minimal
     * {@linkplain Rectangle2D#getY y}.
     *
     * @param  rect The rectangle to translate.
     * @param  x <var>x</var> anchor ordinate.
     * @param  y <var>y</var> anchor ordinate.
     * @return The <code>glyphs</code> bounding box translated to a position relative to (x,y).
     */
    final void setLocation(final Rectangle2D rect, double x, double y) {
        final double height = rect.getHeight();
        final double width  = rect.getWidth();
        switch (getHorizontalAlignment()) {
            case SwingConstants.RIGHT:  x -=     width; break;
            case SwingConstants.CENTER: x -= 0.5*width; break;
            case SwingConstants.LEFT:                   break;
            default: throw new IllegalStateException();
        }
        switch (getVerticalAlignment()) {
            case SwingConstants.BOTTOM: y -=     height; break;
            case SwingConstants.CENTER: y -= 0.5*height; break;
            case SwingConstants.TOP:                     break;
            default: throw new IllegalStateException();
        }
        rect.setRect(x, y, width, height);
    }
}

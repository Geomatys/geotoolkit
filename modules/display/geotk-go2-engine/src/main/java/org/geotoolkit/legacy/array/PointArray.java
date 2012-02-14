/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.legacy.array;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.geotoolkit.legacy.geom.CompressionLevel;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.resources.Errors;


/**
 * Classe de base des classes enveloppant un tableau de points (<var>x</var>,<var>y</var>).
 * Les acc�s aux �l�ments de ce tableaux ne peuvent pas �tre fait de fa�on al�atoires. Ils
 * doivent obligatoirement passer par un it�rateur retourn� par {@link #iterator}. Cette
 * limitation est n�cessaire pour faciliter l'impl�mentation de certains algorithmes de
 * compression des donn�es.
 * <p>
 * <strong>Note sur le vocabulaire employ�:</strong> Dans la documentation de cette classe,
 * le terme <em>point</em> se r�f�re � une paire de coordonn�es (<var>x</var>,<var>y</var>)
 * tandis que le terme  <em>coordonn�e</em>  se r�f�re � une seule valeur  <var>x</var> ou
 * <var>y</var>  (en fran�ais, "ordonn�e" est plut�t utilis� pour la coordonn�e le long de
 * l'axe des <var>y</var>, la coordonn�e le long de l'axe des <var>x</var> �tant l'abscisse).
 * Pour un point situ� � l'index <code>i</code>, les coordonn�es <var>x</var> et <var>y</var>
 * correspondantes se trouvent aux index <code>2*i</code> et <code>2*i+1</code> respectivement.
 *
 * @version $Id: PointArray.java 20168 2006-06-23 12:36:38Z desruisseaux $
 * @author Martin Desruisseaux
 *
 * @see DefaultArray
 * @see GenericArray
 * @see JTSArray
 * @see DefaultArray#getInstance
 * @see org.geotoolkit.renderer.j2d.Renderer
 * @see org.geotoolkit.gui.swing.MapPane
 *
 * @deprecated Replaced by {@link org.geotoolkit.geometry.array.PointArray2D}
 *             as part of the port of J2D-renderer to the new GO-1 based API. Note that it is not
 *             possible to mix J2D-renderer classes with GO-1 rendering engine. Migration from
 *             J2D-renderer to the GO-1 API will requires the replacement of all deprecated classes
 *             together. Because the new GO-1 rendering engine is a work in progress, see
 *             <A HREF="http://jira.codehaus.org/browse/GEOT-776">GEOT-776</A> in order to determine
 *             if enough functionalites have been ported for yours need.
 * @module pending
 */
public abstract class PointArray implements Serializable {
    /**
     * Serial version for compatibility with previous version.
     */
    private static final long serialVersionUID = 1281113806110831086L;

    /**
     * V�rifie la validit� des arguments sp�cifi�s.
     *
     * @param  array Tableau de coordonn�es (<var>x</var>,<var>y</var>).
     * @param  lower Index de la premi�re coordonn�es <var>x</var> �
     *         prendre en compte dans le tableau <code>array</code>.
     * @param  upper Index suivant celui de la derni�re coordonn�e <var>y</var> �
     *         prendre en compte dans le tableau <code>array</code>. La diff�rence
     *         <code>upper-lower</code> doit obligatoirement �tre paire.
     * @throws IllegalArgumentException si la plage <code>[lower..upper]</code>
     *         n'est pas valide ou est en dehors des limites du tableau.
     */
    static void checkRange(final float[] array, final int lower, final int upper)
            throws IllegalArgumentException
    {
        assert (array.length & 1) == 0 : array.length;
        assert (lower        & 1) == 0 : lower;
        assert (upper        & 1) == 0 : upper;
        if (upper < lower) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_RANGE_$2,
                                               new Integer(lower), new Integer(upper)));
        }
        if (((upper-lower)&1) !=0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ODD_ARRAY_LENGTH_$1,
                                               new Integer(upper-lower)));
        }
        if (lower < 0) {
            throw new ArrayIndexOutOfBoundsException(lower);
        }
        if (upper > array.length) {
            throw new ArrayIndexOutOfBoundsException(upper);
        }
    }

    /**
     * Construct a new array.
     */
    protected PointArray() {
    }

    /**
     * Returns the index of the first valid ordinate.
     *
     * This method is overriden by all <code>PointArray</code> subclasses in this package.
     * Note that this method is not <code>protected</code> in this <code>PointArray</code>
     * class because it is used only by {@link #capacity}, which is a package-private helper
     * method for {@link #toArray} implementations only.
     */
    int lower() {
        return 0;
    }

    /**
     * Returns the index after the last valid ordinate.
     *
     * This method is overriden by all <code>PointArray</code> subclasses in this package.
     * Note that this method is not <code>protected</code> in this <code>PointArray</code>
     * class because it is used only by {@link #capacity}, which is a package-private helper
     * method for {@link #toArray} implementations only.
     */
    int upper() {
        return 2*count();
    }

    /**
     * Returns the number of points in this array.
     */
    public abstract int count();

    /**
     * Returns an estimation of memory usage in bytes. This method is for information
     * purpose only. The memory used by this array may be shared with an other array,
     * resulting in a total memory consumption lower than the sum of <code>getMemoryUsage()</code>
     * return values. Furthermore, this method do not take in account the extra bytes
     * generated by Java Virtual Machine for each objects.
     *
     * @return An <em>estimation</em> of memory usage in bytes.
     */
    public abstract long getMemoryUsage();

    /**
     * Returns the first point in this array. If <code>point</code> is null, a new
     * {@link Point2D} object is allocated and then the result is stored in this object.
     * In either case <code>point</code>, which contains the first point, is returned for
     * convenience.
     *
     * @param  point The object in which to store the first point, or <code>null</code>.
     * @return <code>point</code> or a new {@link Point2D}, which contains the first point.
     *
     * @deprecated Retrofited in the {@code get(0,point)} method.
     */
    public abstract Point2D getFirstPoint(final Point2D point);

    /**
     * Returns the last point in this array. If <code>point</code> is null, a new
     * {@link Point2D} object is allocated and then the result is stored in this object.
     * In either case <code>point</code>, which contains the last point, is returned for
     * convenience.
     *
     * @param  point The object in which to store the last point, or <code>null</code>.
     * @return <code>point</code> or a new {@link Point2D}, which contains the last point.
     *
     * @deprecated Retrofited in the {@code get(length()-1,point)} method.
     */
    public abstract Point2D getLastPoint(final Point2D point);

    /**
     * Returns an iterator object that iterates along the point coordinates.
     *
     * @param  index Index of the first point to returns in the iteration.
     * @return The iterator.
     */
    public abstract PointIterator iterator(final int index);

    /**
     * Returns an iterator object that iterates along the point coordinates.
     * If an optional affine transform is specified, the coordinates returned
     * in the iteration are transformed accordingly.
     *
     * @see #toShape
     * @see ShapeAdapter#getPathIterator(AffineTransform)
     */
    PathIterator getPathIterator(final AffineTransform at) {
        return new ShapeAdapter.Iterator(iterator(0), at);
    }
    
    /**
     * Returns the bounding box of all <var>x</var> and <var>y</var> ordinates.
     * If this array is empty, then this method returns <code>null</code>.
     * The default implementation iterates through all coordinates provided by
     * {@link PointIterator}.
     */
    public Rectangle2D getBounds2D() {
        float xmin = Float.POSITIVE_INFINITY;
        float xmax = Float.NEGATIVE_INFINITY;
        float ymin = Float.POSITIVE_INFINITY;
        float ymax = Float.NEGATIVE_INFINITY;
        final PointIterator it = iterator(0);
        while (it.hasNext()) {
            final float x = it.nextX();
            final float y = it.nextY();
            if (x<xmin) xmin=x;
            if (x>xmax) xmax=x;
            if (y<ymin) ymin=y;
            if (y>ymax) ymax=y;
        }
        if (xmin<=xmax && ymin<=ymax) {
            return new Rectangle2D.Float(xmin, ymin, xmax-xmin, ymax-ymin);
        } else {
            return null;
        }
    }

    /**
     * Retourne un tableau enveloppant les m�mes points que le tableau courant,
     * mais des index <code>lower</code> inclusivement jusqu'� <code>upper</code>
     * exclusivement. Si le sous-tableau ne contient aucun point (c'est-�-dire si
     * <code>lower==upper</code>), alors cette m�thode retourne <code>null</code>.
     *
     * @param lower Index du premier point � prendre en compte.
     * @param upper Index suivant celui du dernier point � prendre en compte.
     */
    public abstract PointArray subarray(final int lower, final int upper);

    /**
     * Ins�re tous les points de <code>toMerge</code> dans le tableau <code>this</code>.
     * Si le drapeau <code>reverse</code> � la valeur <code>true</code>, alors les points
     * de <code>toMerge</code> seront copi�es en ordre inverse.
     *
     * @param  index Index � partir d'o� ins�rer les points dans ce tableau. Le point � cet
     *         index ainsi que tous ceux qui le suivent seront d�cal�s vers des index plus �lev�s.
     * @param  toMerge Tableau de points � ins�rer. Ses valeurs seront copi�es.
     */
    public final PointArray insertAt(final int index, final PointArray toMerge, final boolean reverse) {
        return toMerge.insertTo(this, index, reverse);
    }

    /**
     * Ins�re les donn�es de <code>this</code> dans le tableau sp�cifi�. Cette m�thode est
     * strictement r�serv�e � l'impl�mentation de {@link #insertAt(int,PointArray,boolean)}.
     * La classe {@link DefaultArray} remplace l'impl�mentation par d�faut par une nouvelle
     * impl�mentation qui �vite de copier les donn�es avec {@link #toArray()}.
     */
    PointArray insertTo(final PointArray dest, final int index, final boolean reverse) {
        final float[] array = toArray();
        return dest.insertAt(index, array, 0, array.length, reverse);
    }

    /**
     * Ins�re les donn�es (<var>x</var>,<var>y</var>) du tableau <code>toMerge</code> sp�cifi�.
     * Si le drapeau <code>reverse</code> � la valeur <code>true</code>, alors les points de
     * <code>toMerge</code> seront copi�es en ordre inverse.
     *
     * @param  index Index � partir d'o� ins�rer les points dans ce tableau. Le point � cet
     *         index ainsi que tous ceux qui le suivent seront d�cal�s vers des index plus �lev�s.
     * @param  toMerge Tableau de coordonn�es (<var>x</var>,<var>y</var>) � ins�rer dans ce
     *         tableau de points. Ses valeurs seront copi�es.
     * @param  lower Index de la premi�re coordonn�e de <code>toMerge</code> � copier dans ce tableau.
     * @param  upper Index suivant celui de la derni�re coordonn�e de <code>toMerge</code> � copier.
     * @param  reverse <code>true</code> s'il faut inverser l'ordre des points de <code>toMerge</code>
     *         lors de la copie. Cette inversion ne change pas l'ordre (<var>x</var>,<var>y</var>) des
     *         coordonn�es de chaque points.
     *
     * @return <code>this</code> si l'insertion � pu �tre faite sur
     *         place, ou un autre tableau si �a n'a pas �t� possible.
     */
    public abstract PointArray insertAt(final int index, final float toMerge[],
                                        final int lower, final int upper, final boolean reverse);

    /**
     * Renverse l'ordre de tous les points compris dans ce tableau.
     *
     * @return <code>this</code> si l'inversion a pu �tre faite sur-place,
     *         ou un autre tableau si �a n'a pas �t� possible.
     */
    public abstract PointArray reverse();

    /**
     * Retourne un tableau immutable qui contient les m�mes donn�es que celui-ci.
     * Apr�s l'appel de cette m�thode, toute tentative de modification (avec les
     * m�thodes {@link #insertAt} ou {@link #reverse}) vont retourner un autre
     * tableau de fa�on � ne pas modifier le tableau immutable.
     *
     * @param  level The compression level, or <code>null</code> if no compression is wanted.
     * @return Tableau immutable et �ventuellement compress�, <code>this</code>
     *         si ce tableau r�pondait d�j� aux conditions ou <code>null</code>
     *         si ce tableau ne contient aucune donn�e.
     */
    public PointArray getFinal(final CompressionLevel level) {
        return (count()>0) ? this : null;
    }

    /**
     * Append (<var>x</var>,<var>y</var>) coordinates to the specified destination array.
     * The destination array will be filled starting at index {@link ArrayData#length}.
     * If <code>resolution2</code> is greater than 0, then points that are closer than
     * <code>sqrt(resolution2)</code> from previous one will be skiped.
     *
     * @param  The destination array. The coordinates will be filled in
     *         {@link ArrayData#array}, which will be expanded if needed.
     *         After this method completed, {@link ArrayData#length} will
     *         contains the index after the <code>array</code>'s element
     *         filled with the last <var>y</var> ordinate.
     * @param  resolution2 The minimum squared distance desired between points.
     *
     * @todo        : Current implementations compute distance using Pythagoras formulas, which
     *                is okay for projected coordinates but not right for geographic (longitude
     *                / latitude) coordinates. This is not a real problem when the rendering CS
     *                is the same than the data CS,  since the decimation performed here target
     *                specifically the rendering device  (the important thing is that distances
     *                look okay to user's eyes). However, it may be a problem when the rendering
     *                CS is different, since points that are equidistant in the data CS may not
     *                be equidistant in the rendering CS.
     */
    public abstract void toArray(ArrayData dest, float resolution2);

    /**
     * Retourne une copie de toutes les coordonn�es (<var>x</var>,<var>y</var>) de ce tableau.
     */
    public final float[] toArray() {
        final ArrayData data = new ArrayData(2*count());
        toArray(data, 0);
        assert data.length == data.array.length;
        return data.array;
    }

    /**
     * Used by {@link #toArray}  implementations in order to expand the destination array
     * as needed.   This method is invoking when <code>toArray</code> has already started
     * to fill the destination array. The <code>src</code> and <code>dst</code> arguments
     * refer to the current position in the copy loop.  This method try to guess the size
     * that the destination array should have based on the proportion of the array filled
     * up to date, and conservatively expand its guess by about 12%.
     *
     * @param  src The index position in the source array.
     * @param  dst The index position in the destination array.
     * @param  offset The first element to be filled in the destination array.
     * @return A guess of the required length for completing the array filling.
     */
    final int capacity(int src, int dst, final int offset) {
        final int lower  = lower();
        final int length = upper() - lower;
        int guess;
        dst -= offset;
        src -= lower;
        assert (src & 1) == 0 : src;
        assert (dst & 1) == 0 : dst;
        if (src == 0) {
            guess = length / 8;
        } else {
            guess = (int)(dst * (long)length / src);  // Prediction of the total length required.
            guess -= dst;                             // The amount to growth.
            guess += guess/8;                         // Conservatively add some space.
        }
        guess &= ~1; // Make sure the length is even.
        return offset + Math.min(length, dst+Math.max(guess, 32));
    }

    /**
     * Returns this <code>PointArray</code> as a {@linkplain Shape shape}.  This shape is not
     * designed for map rendering. It is rather a debugging tool, as well as a convenient way
     * to draw lines in some simple context (e.g. {@link org.geotoolkit.gui.swing.Plot2D}).
     *
     * @param transform An optional transform to apply on coordinates, or <code>null</code> if none.
     * @return The lines in this <code>PointArray</code> as a Java2D {@linkplain Shape shape}.
     */
    public final Shape toShape(final AffineTransform transform) {
        return new ShapeAdapter(this, transform);
    }

    /**
     * Retourne une cha�ne de caract�res repr�sentant ce tableau. Cette cha�ne
     * contiendra le nom de la classe utilis�e, le nombre de points ainsi que
     * les points de d�part et d'arriv�.
     */
    @Override
    public final String toString() {
        final Point2D.Float point = new Point2D.Float();
        final StringBuffer buffer = new StringBuffer(Classes.getShortClassName(this));
        final int count = count();
        buffer.append('[');
        buffer.append(count);
        buffer.append(" points");
        if (count!=0) {
            getFirstPoint(point);
            buffer.append(" (");
            buffer.append(point.x);
            buffer.append(", ");
            buffer.append(point.y);
            buffer.append(")-(");

            getLastPoint(point);
            buffer.append(point.x);
            buffer.append(", ");
            buffer.append(point.y);
            buffer.append(')');
        }
        buffer.append(']');
        return buffer.toString();
    }

    /**
     * Indique si ce tableau est identique au tableau sp�cifi�. Deux
     * tableaux seront consid�r�s identiques s'ils contiennent les
     * m�mes points dans le m�me ordre.
     */
    public final boolean equals(final PointArray that) {
        if (that==this) return true;
        if (that==null) return false;
        if (this.count() != that.count()) {
            return false;
        }
        final PointIterator it1 = this.iterator(0);
        final PointIterator it2 = that.iterator(0);
        while (it1.hasNext()) {
            if (!it2.hasNext() ||
                Float.floatToIntBits(it1.nextX()) != Float.floatToIntBits(it2.nextX()) ||
                Float.floatToIntBits(it1.nextY()) != Float.floatToIntBits(it2.nextY())) return false;
        }
        return !it2.hasNext();
    }

    /**
     * Indique si cet objet est identique � l'objet sp�cifi�.   Cette m�thode consid�re deux
     * objets identiques si <code>that</code> est d'une classe d�riv�e de {@link PointArray}
     * et si les deux tableaux contiennent les m�mes points dans le m�me ordre.
     */
    @Override
    public final boolean equals(final Object that) {
        return (that instanceof PointArray) && equals((PointArray) that);
    }

    /**
     * Retourne un code repr�sentant cet objet.
     */
    @Override
    public final int hashCode() {
        final Point2D point = getFirstPoint(null);
        return count() ^ Float.floatToIntBits((float)point.getX())
                       ^ Float.floatToIntBits((float)point.getY());
    }
}

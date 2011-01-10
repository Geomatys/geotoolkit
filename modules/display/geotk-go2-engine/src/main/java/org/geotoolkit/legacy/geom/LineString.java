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
package org.geotoolkit.legacy.geom;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotoolkit.legacy.array.ArrayData;
import org.geotoolkit.legacy.array.DefaultArray;
import org.geotoolkit.legacy.array.PointArray;
import org.geotoolkit.legacy.array.PointIterator;
import org.geotoolkit.math.Statistics;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.display.shape.ShapeUtilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Utilities;

import org.geotoolkit.util.logging.Logging;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;


/**
 * Lignes trac�es sans lever le crayon. Ces lignes ne repr�sentent par forc�ment une forme ferm�e
 * (un polygone). Les objets <code>LineString</code> ont deux caract�ristiques particuli�res:
 *
 * <ul>
 *   <li>Ils m�morisent s�par�ment les points qui ne font que former une bordure. Par exemple, si
 *       seulement la moiti� d'une �le appara�t sur une carte, les points qui servent � joindre
 *       les deux extr�mit�s des polylignes (en suivant la bordure de la carte l� o� l'�le est
 *       coup�e) n'ont pas de r�alit� g�ographique. Dans chaque objet <code>LineString</code>, il doit
 *       y avoir une distinction claire entre les v�ritable points g�ographique les "points de
 *       bordure". Ces points sont m�moris�s s�par�ments dans les tableaux
 *       {@link #prefix}/{@link #suffix} et {@link #array} respectivement.</li>
 *
 *   <li>Ils peuvent �tre cha�n�s avec d'autres objets <code>LineString</code>. Former une cha�ne
 *       d'objets <code>LineString</code> peut �tre utile lorsque les coordonn�es d'une c�te ont �t�
 *       obtenues � partir de la digitalisation de plusieurs cartes bathym�triques, que l'on joindra
 *       en une ligne continue au moment du tra�age. Elle peut aussi se produire lorsqu'une ligne
 *       qui se trouve pr�s du bord de la carte entre, sort, r�entre et resort plusieurs fois du
 *       cadre.</li>
 * </ul>
 *
 * Par convention, toutes les m�thodes statiques de cette classe peuvent agir
 * sur une cha�ne d'objets {@link LineString} plut�t que sur une seule instance.
 *
 * @version $Id: LineString.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 * @module pending
 */
final class LineString implements Serializable {
    /**
     * Serial version for compatibility with previous version.
     */
    private static final long serialVersionUID = 719026440018802849L;

    /**
     * Set to <code>true</code> for disallowing two consecutive points with the same value.
     * Some algorithms in this class and in {@link PathAnalyser} require the distance between
     * two consecutive points to be greater than 0 in all cases.
     */
    private static final boolean REMOVE_DOUBLONS = true;

    /**
     * Set to <code>true</code> for removing doublons in borders as well.
     *
     * @todo     : Current algorithm prevent the creation of curved line. The algorithm
     *             should be modified in such a way that  doublons are removed from the
     *             first of last points of the main data instead of from the border (the
     *             should not be modified by '[append|prepend]Border'. The work done by
     *             'addBorder' is correct however). How to do: add the following methods:
     *             'remove[First|Last]Points(int n)', and use it in '[append|prepend]Border'.
     */
    private static final boolean REMOVE_DOUBLONS_IN_BORDER = false;

    /**
     * Set to <code>true</code> if {@link #freeze} should try to merge the {@link #array} of
     * two consecutive polylines. Experience has show that it was often a bad idea, since it
     * force a lot of copies during clipping.
     */
    private static final boolean MERGE_POLYLINE_DATA = false;

    /**
     * Polylignes pr�c�dentes et suivantes. La classe <code>LineString</code> impl�mente une liste �
     * double liens. Chaque objet <code>LineString</code> est capable d'acc�der et d'agir sur les
     * autres �l�ments de la liste � laquelle il appartient. En cons�quent, il n'est pas n�cessaire
     * d'utiliser une classe s�par�e (par exemple {@link java.util.LinkedList}) comme conteneur.
     * Il ne s'agit pas forc�ment d'un bon concept de programmation, mais il est pratique dans le
     * cas particulier de la classe <code>LineString</code> et offre de bonnes performances.
     */
    private LineString previous, next;

    /**
     * Coordonn�es formant la polyligne. Ces coordonn�es doivent �tre celles d'un trait de c�te ou
     * de toute autre forme g�om�trique ayant une signification cartographique. Les points qui
     * servent � "couper" un polygone (par exemple des points longeant la bordure de la carte)
     * doivent �tre m�moris�s s�par�ment dans le tableau <code>suffix</code>.
     */
    private PointArray array;

    /**
     * Coordonn�es � retourner apr�s celles de <code>array</code>. Ces coordonn�es servent
     * g�n�ralement � refermer un polygone, par exemple en suivant le cadre de la carte. Ce
     * champ peut �tre nul s'il ne s'applique pas.
     */
    private PointArray suffix;

    /**
     * Valeur minimales et maximales autoris�es comme arguments pour les m�thodes {@link #getArray}
     * et {@link #setArray}. Lorsque ces valeurs sont utilis�es en ordre croissant, {@link #getArray}
     * retourne dans l'ordre les tableaux {@link #prefix}, {@link #array} et {@link #suffix}.
     * <br><br>
     * Note: si les valeurs de ces constantes changent, alors il faudra revoir l'impl�mentation des
     * m�thodes suivantes:
     *
     *    {@link #getArray},
     *    {@link #setArray},
     *    {@link #reverse},
     *    {@link #freeze},
     */
    private static final int FIRST_ARRAY=0, LAST_ARRAY=1;

    /**
     * Construit un objet qui enveloppera les points sp�cifi�s.
     * Cette polyligne fera initialement partie d'aucune liste.
     */
    LineString(final PointArray array) {
        this.array = array;
    }

    /**
     * Construit des objets m�morisant les coordonn�es <code>data</code>. Les valeurs
     * <code>NaN</code> au d�but et � la fin de <code>data</code> seront ignor�es. Celles
     * qui apparaissent au milieu auront pour effet de s�parer le trait en plusieurs polylignes.
     *
     * @param data   Tableau de coordonn�es (peut contenir des NaN).
     * @return       Tableau de polylignes. Peut avoir une longueur de 0, mais ne sera jamais nul.
     */
    public static LineString[] getInstances(final float[] data) {
        return getInstances(data, 0, data.length);
    }

    /**
     * Construit des objets m�morisant les coordonn�es <code>data</code> de l'index
     * <code>lower</code> inclusivement jusqu'� <code>upper</code> exclusivement. Ces
     * index doivent se r�f�rer � la position absolue dans le tableau <code>data</code>,
     * c'est-�-dire �tre le double de l'index de la coordonn�e. Les valeurs <code>NaN</code>
     * au d�but et � la fin de <code>data</code> seront ignor�es. Celles qui apparaissent au
     * milieu auront pour effet de s�parer le trait en plusieurs polylignes.
     *
     * @param data   Tableau de coordonn�es (peut contenir des NaN).
     * @param lower  Index de la premi�re donn�e � consid�rer.
     * @param upper  Index suivant celui de la derni�re donn�e.
     * @return       Tableau de polylignes. Peut avoir une longueur de 0, mais ne sera jamais nul.
     */
    public static LineString[] getInstances(float[] data, int lower, int upper) {
        if (REMOVE_DOUBLONS) {
            final float[] candidate = removeDoublons(data, lower, upper);
            if (candidate != null) {
                data  = candidate;
                lower = 0;
                upper = data.length;
            }
        }
        final List polylines = new ArrayList();
        for (int i=lower; i<upper; i+=2) {
            if (!Float.isNaN(data[i]) && !Float.isNaN(data[i+1])) {
                final int lowerValid = i;
                while ((i+=2) < upper) {
                    if (Float.isNaN(data[i]) || Float.isNaN(data[i+1])) {
                        break;
                    }
                }
                final PointArray points = DefaultArray.getInstance(data, lowerValid, i, true);
                if (points != null) {
                    polylines.add(new LineString(points));
                }
            }
        }
        return (LineString[]) polylines.toArray(new LineString[polylines.size()]);
    }

    /**
     * Remove consecutive identical points, since it hurt many algorithms in this
     * package. {@link Float#NaN} values are ignored (they may have doublons).
     *
     * @param  data  The data to examine.
     * @param  lower The lower index to examine in <code>data</code>, inclusive.
     * @param  upper The upper index to examine in <code>data</code>, inclusive.
     * @return <code>null</code> if no doublons was found in <code>data</code>,
     *         otherwise a new array without doublons.
     */
    private static float[] removeDoublons(final float[] data, final int lower, final int upper) {
        int dest = 0;
        float[] copy = null;
        for (int i=lower; (i+=2)<upper;) {
            if (data[i-2]==data[i] && data[i-1]==data[i+1]) {
                if (copy == null) {
                    dest = i-lower;
                    copy = new float[upper-lower-2];
                    System.arraycopy(data, lower, copy, 0, dest);
                }
                continue;
            }
            if (copy != null) {
                copy[dest++] = data[i  ];
                copy[dest++] = data[i+1];
            }
        }
        if (copy != null) {
            copy = XArrays.resize(copy, dest);
        }
        return copy;
    }

    /**
     * Renvoie le premier �l�ment de la liste � laquelle appartient la
     * polyligne. Cette m�thode peut retourner <code>scan</code>, mais
     * jamais <code>null</code>  (sauf si l'argument <code>scan</code>
     * est nul).
     */
    private static LineString getFirst(LineString scan) {
        if (scan != null) {
            while (scan.previous != null) {
                scan = scan.previous;
                assert scan.previous != scan;
                assert scan.next     != scan;
            }
        }
        return scan;
    }

    /**
     * Renvoie le dernier �l�ment de la liste � laquelle appartient la
     * polyligne. Cette m�thode peut retourner <code>scan</code>, mais
     * jamais <code>null</code>  (sauf si l'argument <code>scan</code>
     * est nul).
     */
    private static LineString getLast(LineString scan) {
        if (scan != null) {
            while (scan.next != null) {
                scan = scan.next;
                assert scan.previous != scan;
                assert scan.next     != scan;
            }
        }
        return scan;
    }

    /**
     * Ajoute la polyligne <code>toAdd</code> � la fin de la polyligne <code>queue</code>.
     * Les arguments <code>queue</code> et <code>toAdd</code> peuvent �tre n'importe
     * quel maillon d'une cha�ne, mais cette m�thode sera plus rapide si <code>queue</code>
     * est le dernier maillon.
     *
     * @param  queue <code>LineString</code> � la fin duquel ajouter <code>toAdd</code>. Si cet
     *               argument est nul, alors cette m�thode retourne directement <code>toAdd</code>.
     * @param  toAdd <code>LineString</code> � ajouter � <code>queue</code>. Cet objet sera ajout�
     *               m�me s'il est vide. Si cet argument est nul, alors cette m�thode retourne
     *               <code>queue</code> sans rien faire.
     * @return <code>LineString</code> r�sultant de la fusion. Les anciens objets <code>queue</code>
     *         et <code>toAdd</code> peuvent avoir �t� modifi�s et ne devraient plus �tre utilis�s.
     * @throws IllegalArgumentException si <code>toAdd</code> avait d�j� �t� ajout� �
     *         <code>queue</code>.
     */
    public static LineString append(LineString queue, LineString toAdd)
            throws IllegalArgumentException
    {
        // On doit faire l'ajout m�me si 'toAdd' est vide.
        final LineString veryLast = getLast(toAdd);
        toAdd = getFirst(toAdd);
        queue = getLast (queue);
        if (toAdd == null) return queue;
        if (queue == null) return toAdd;
        if (queue == veryLast) {
            throw new IllegalArgumentException();
        }

        assert queue.next     == null;
        assert toAdd.previous == null;
        queue.next     = toAdd;
        toAdd.previous = queue;

        assert getFirst(queue) == getFirst(toAdd);
        assert getLast (queue) == getLast (toAdd);
        assert veryLast.next   == null;
        return veryLast;
    }

    /**
     * Supprime ce maillon de la cha�ne. Ce maillon
     * conservera toutefois ses donn�es.
     */
    private void remove() {
        if (previous != null) {
            previous.next = next;
        }
        if (next != null) {
            next.previous = previous;
        }
        previous = next = null;
    }

    /**
     * Indique si cette polyligne est vide. Une polyligne est vide si tous
     * ces tableaux sont nuls. Cette m�thode ne v�rifie pas l'�tat des
     * autres maillons de la cha�ne.
     */
    private boolean isEmpty() {
        return array==null && suffix==null;
    }

    /**
     * Retourne un des tableaux de donn�es de cette polyligne. Le tableau retourn�
     * peut �tre {@link #prefix}, {@link #array} ou {@link #suffix} selon que
     * l'argument est -1, 0 ou +1 respectivement. Toute autre valeur lancera
     * une exception.
     *
     * @param arrayID Un code compris entre {@link #FIRST_ARRAY}
     *                et {@link #LAST_ARRAY} inclusivement.
     */
    private PointArray getArray(final int arrayID) {
        switch (arrayID) {
        //  case -1: return prefix;
            case  0: return array;
            case +1: return suffix;
            default: throw new IllegalArgumentException(String.valueOf(arrayID));
        }
    }

    /**
     * Modifie un des tableaux de donn�es de cette polyligne. Le tableau modifi�
     * peut �tre {@link #prefix}, {@link #array} ou {@link #suffix} selon que
     * l'argument est -1, 0 ou +1 respectivement.  Toute autre valeur lancera
     * une exception.
     *
     * @param arrayID Un code compris entre {@link #FIRST_ARRAY}
     *                et {@link #LAST_ARRAY} inclusivement.
     */
    private void setArray(final int arrayID, final PointArray data) {
        switch (arrayID) {
        //  case -1: prefix=data; break;
            case  0: array =data; break;
            case +1: suffix=data; break;
            default: throw new IllegalArgumentException(String.valueOf(arrayID));
        }
    }

    /**
     * Returns an estimation of memory usage in bytes.  This method is for information
     * purpose only. The memory really used by two polylines may be lower than the sum
     * of their  <code>getMemoryUsage()</code>  return values,  since polylines try to
     * share their data when possible. Furthermore, this method do not take in account
     * the extra bytes generated by Java Virtual Machine for each objects.
     *
     * @return An <em>estimation</em> of memory usage in bytes.
     */
    public static long getMemoryUsage(LineString scan) {
        scan = getFirst(scan);
        long count = 16; // Take in account 4 internal fields of reference type (4 bytes each).
        while (scan != null) {
            for (int i=FIRST_ARRAY; i<=LAST_ARRAY; i++) {
                final PointArray data = scan.getArray(i);
                if (data!=null) {
                    count += data.getMemoryUsage();
                }
            }
            scan = scan.next;
        }
        return count;
    }

    /**
     * Retourne le nombre de points de la polyligne sp�cifi�e
     * ainsi que de tous les polylignes qui le suivent.
     *
     * @param scan Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *             mais cette m�thode sera plus rapide si c'est le premier maillon.
     */
    public static int getPointCount(LineString scan) {
        scan = getFirst(scan);
        int count = 0;
        while (scan != null) {
            for (int i=FIRST_ARRAY; i<=LAST_ARRAY; i++) {
                final PointArray data = scan.getArray(i);
                if (data!=null) {
                    count += data.count();
                }
            }
            scan = scan.next;
        }
        return count;
    }

    /**
     * V�rifie si la polyligne sp�cifi�e est vide.
     *
     * @param scan Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *             mais cette m�thode sera plus rapide si c'est le premier maillon.
     */
    public static boolean isEmpty(LineString scan) {
        scan = getFirst(scan);
        while (scan != null) {
            if (!scan.isEmpty()) {
                return false;
            }
            scan = scan.next;
        }
        return true;
    }

    /**
     * Returns <code>true</code> if at least one point of the specified polyline is a border.
     */
    public static boolean hasBorder(LineString scan) {
        scan = getFirst(scan);
        while (scan != null) {
            if (scan.suffix != null) {
                return true;
            }
            scan = scan.next;
        }
        return false;
    }

    /**
     * Donne � la coordonn�e sp�cifi�e la valeur du premier point. Si une bordure a �t�
     * ajout�e avec la m�thode {@link #prepend}, elle sera pris en compte. Si cet objet
     * <code>LineString</code> ne contient aucun point, l'objet qui suit dans la cha�ne
     * sera automatiquement interrog�.
     *
     * @param  scan  Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *               mais cette m�thode sera plus rapide si c'est le premier maillon.
     * @param  point Point dans lequel m�moriser la coordonn�e.
     * @return L'argument <code>point</code>, ou un nouveau point
     *         si <code>point</code> �tait nul.
     * @throws NoSuchElementException Si <code>scan</code> est nul
     *         ou s'il ne reste plus de points dans la cha�ne.
     *
     * @see #getFirstPoints
     * @see #getLastPoint
     */
    public static Point2D getFirstPoint(LineString scan, final Point2D point)
            throws NoSuchElementException
    {
        scan = getFirst(scan);
        while (scan != null) {
            for (int i=FIRST_ARRAY; i<=LAST_ARRAY; i++) {
                final PointArray data=scan.getArray(i);
                if (data != null) {
                    return data.getFirstPoint(point);
                }
            }
            scan = scan.next;
        }
        throw new NoSuchElementException();
    }

    /**
     * Donne � la coordonn�e sp�cifi�e la valeur du dernier point. Si une bordure a �t�
     * ajout�e avec la m�thode {@link #append}, elle sera pris en compte.  Si cet objet
     * <code>LineString</code> ne contient aucun point, l'objet qui pr�c�de dans la cha�ne
     * sera automatiquement interrog�.
     *
     * @param  scan  Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *               mais cette m�thode sera plus rapide si c'est le dernier maillon.
     * @param  point Point dans lequel m�moriser la coordonn�e.
     * @return L'argument <code>point</code>, ou un nouveau point
     *         si <code>point</code> �tait nul.
     * @throws NoSuchElementException Si <code>scan</code> est nul
     *         ou s'il ne reste plus de points dans la cha�ne.
     *
     * @see #getLastPoints
     * @see #getFirstPoint
     */
    public static Point2D getLastPoint(LineString scan, final Point2D point)
            throws NoSuchElementException
    {
        scan = getLast(scan);
        while (scan != null) {
            for (int i=LAST_ARRAY; i>=FIRST_ARRAY; i--) {
                PointArray data=scan.getArray(i);
                if (data != null) {
                    return data.getLastPoint(point);
                }
            }
            scan = scan.previous;
        }
        throw new NoSuchElementException();
    }

    /**
     * Donne aux coordonn�es sp�cifi�es les valeurs des premiers points.
     *
     * @param scan   Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *               mais cette m�thode sera plus rapide si c'est le premier maillon.
     * @param points Tableau dans lequel m�moriser les premi�res coordonn�es. <code>points[0]</code>
     *               contiendra la premi�re coordonn�e, <code>points[1]</code> la seconde, etc. Si
     *               un �l�ment de ce tableau est nul, un objet {@link Point2D} sera automatiquement
     *               cr��.
     *
     * @throws NoSuchElementException Si <code>scan</code> est nul ou
     *         s'il ne reste pas suffisament de points dans la cha�ne.
     */
    public static void getFirstPoints(LineString scan, final Point2D points[])
            throws NoSuchElementException
    {
        scan = getFirst(scan);
        if (points.length == 0) {
            return;
        }
        if (scan == null) {
            throw new NoSuchElementException();
        }
        int      arrayID = FIRST_ARRAY;
        PointArray  data = null;
        PointIterator it = null;
        for (int j=0; j<points.length; j++) {
            while (it==null || !it.hasNext()) {
                if (arrayID > LAST_ARRAY) {
                    arrayID = FIRST_ARRAY;
                    scan    = scan.next;
                    if (scan == null) {
                        throw new NoSuchElementException();
                    }
                }
                data = scan.getArray(arrayID++);
                if (data != null) {
                    it = data.iterator(0);
                }
            }
            if (points[j] == null) {
                points[j] = new Point2D.Float(it.nextX(), it.nextY());
            } else {
                points[j].setLocation(it.nextX(), it.nextY());
            }
            if (REMOVE_DOUBLONS) {
                assert j==0 || !points[j].equals(points[j-1]) : scan;
            }
        }
        assert Utilities.equals(getFirstPoint(scan, null), points[0]) : scan;
    }

    /**
     * Donne aux coordonn�es sp�cifi�es les valeurs des derniers points.
     *
     * @param scan   Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *               mais cette m�thode sera plus rapide si c'est le dernier maillon.
     * @param points Tableau dans lequel m�moriser les derni�res coordonn�es.
     *               <code>points[length-1]</code> contiendra la derni�re coordonn�e,
     *               <code>points[length-2]</code> l'avant derni�re, etc. Si un �l�ment de
     *               ce tableau est nul, un objet {@link Point2D} sera automatiquement cr��.
     *
     * @throws NoSuchElementException Si <code>scan</code> est nul ou
     *         s'il ne reste pas suffisament de points dans la cha�ne.
     */
    public static void getLastPoints(LineString scan, final Point2D points[])
            throws NoSuchElementException
    {
        scan = getLast(scan);
        if (points.length == 0) {
            // N�cessaire pour l'impl�mentation ci-dessous.
            return;
        }
        if (scan == null) {
            throw new NoSuchElementException();
        }
        int startIndex = -points.length;
        int    arrayID = LAST_ARRAY+1;
        PointArray data;
        /*
         * Recherche la position � partir d'o� lire les donn�es.  A la
         * sortie de cette boucle, la premi�re donn�e valide sera � la
         * position <code>scan.getArray(arrayID).iterator(i)</code>.
         */
        do {
            do {
                if (--arrayID < FIRST_ARRAY) {
                    arrayID = LAST_ARRAY;
                    scan = scan.previous;
                    if (scan==null) {
                        throw new NoSuchElementException();
                    }
                }
                data = scan.getArray(arrayID);
            }
            while (data==null);
            startIndex += data.count();
        }
        while (startIndex < 0);
        /*
         * Proc�de � la m�morisation des coordonn�es.   Note: parvenu � ce stade, 'data' devrait
         * obligatoirement �tre non-nul. Un {@link NullPointerException} dans le code ci-dessous
         * serait une erreur de programmation.
         */
        PointIterator it = data.iterator(startIndex);
        for (int j=0; j<points.length; j++) {
            while (!it.hasNext()) {
                do {
                    if (++arrayID > LAST_ARRAY) {
                        arrayID = FIRST_ARRAY;
                        scan = scan.next;
                    }
                    data = scan.getArray(arrayID);
                }
                while (data==null);
                it = data.iterator(0);
            }
            if (points[j] == null) {
                points[j]=new Point2D.Float(it.nextX(), it.nextY());
            } else {
                points[j].setLocation(it.nextX(), it.nextY());
            }
            if (REMOVE_DOUBLONS) {
                assert j==0 || !points[j].equals(points[j-1]) : scan;
            }
        }
        assert !it.hasNext();
        assert Utilities.equals(getLastPoint(scan, null), points[points.length-1]) : scan;
    }

    /**
     * Retourne une polyligne qui couvrira les donn�es de cette polyligne
     * de l'index <code>lower</code> inclusivement jusqu'� l'index
     * <code>upper</code> exclusivement.
     *
     * @param scan  Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *              mais cette m�thode sera plus rapide si c'est le premier maillon.
     * @param lower Index du premier point � retenir.
     * @param upper Index suivant celui du dernier point � retenir.
     * @return      Une cha�ne de nouvelles polylignes, ou <code>scan</code> si aucun
     *              point n'a �t� ignor�s. Si la polyligne obtenu ne contient aucun
     *              point, alors cette m�thode retourne <code>null</code>.
     */
    public static LineString subpoly(LineString scan, int lower, int upper) {
        if (lower == upper) {
            return null;
        }
        scan = getFirst(scan);
        if (lower==0 && upper==getPointCount(scan)) {
            return scan;
        }
        LineString queue=null;
        while (scan!=null) {
            LineString toAdd = null;
            for (int i=FIRST_ARRAY; i<=LAST_ARRAY; i++) {
                PointArray data = scan.getArray(i);
                if (data == null) {
                    continue;
                }
                /*
                 * V�rifie si le tableau 'data' contient au moins quelques points
                 * � prendre en compte. Si ce n'est pas le cas, il sera ignor� en
                 * bloc.
                 */
                int count = data.count();
                if (count <= lower) {
                    lower -= count;
                    upper -= count;
                    continue;
                }
                /*
                 * Prend en compte les donn�es de 'data' de 'lower' jusqu'� 'upper',
                 * mais sans d�passer la longueur du tableau. S'il reste encore des
                 * points � aller chercher (upper!=0), on examinera les tableaux suivants.
                 */
                if (count > upper) {
                    count = upper;
                }
                assert lower >= 0 : lower;
                assert count <= data.count() : count;
                data = data.subarray(lower, count);
                if (data != null) {
                    if (toAdd == null) {
                        toAdd = new LineString(null);
                        queue = append(queue, toAdd);
                    }
                    assert toAdd.getArray(i)==null;
                    toAdd.setArray(i, data);
                }
                lower  = 0;
                upper -= count;
                if (upper==0) {
                    return queue;
                }
            }
            scan = scan.next;
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Ajoute des points � la bordure de cette polyligne. Cette m�thode est r�serv�e
     * � un usage interne par {@link #prependBorder} et {@link #appendBorder}.
     */
    private void addBorder(float[] data, int lower, int upper, final boolean toEnd) {
        if (REMOVE_DOUBLONS) {
            final float[] candidate = removeDoublons(data, lower, upper);
            if (candidate != null) {
                data  = candidate;
                lower = 0;
                upper = data.length;
            }
        }
        if (suffix == null) {
            suffix = DefaultArray.getInstance(data, lower, upper, true);
        } else {
            suffix = suffix.insertAt(toEnd ? suffix.count() : 0, data, lower, upper, false);
        }
    }

    /**
     * Ajoute des points au d�but de cette polyligne. Ces points seront consid�r�s comme
     * faisant partie de la bordure de la carte, et non comme des points repr�sentant
     * une structure g�ographique.
     *
     * @param  scan  Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne.
     * @param  data  Coordonn�es � ajouter sous forme de paires de nombres (x,y).
     * @param  lower Index du premier <var>x</var> � ajouter � la bordure.
     * @param  upper Index suivant celui du dernier <var>y</var> � ajouter � la bordure.
     * @return LineString r�sultant. Ca sera en g�n�ral <code>scan</code>.
     */
    public static LineString prependBorder(LineString scan, final float[] data,
                                           final int lower, int upper)
    {
        if (REMOVE_DOUBLONS_IN_BORDER) {
            try {
                final Point2D check = getFirstPoint(scan, null);
                final float x = (float)check.getX();
                final float y = (float)check.getY();
                while (lower<upper && data[upper-2]==x && data[upper-1]==y) {
                    upper -= 2;
                }
            } catch (NoSuchElementException exception) {
                // No points in this polyline, no doublons, no problem. Continue...
            }
        }
        final int length = upper-lower;
        if (length > 0) {
            scan = getFirst(scan);
            if (scan==null || scan.array!=null) {
                scan = getFirst(append(new LineString(null), scan));
                assert scan.array==null;
            }
            scan.addBorder(data, lower, upper, false);
        }
        return scan;
    }

    /**
     * Ajoute des points � la fin de cette polyligne. Ces points seront consid�r�s comme
     * faisant partie de la bordure de la carte, et non comme des points repr�sentant
     * une structure g�ographique.
     *
     * @param  scan  Polyline. Cet argument peut �tre n'importe quel maillon d'une cha�ne.
     * @param  data  Coordonn�es � ajouter sous forme de paires de nombres (x,y).
     * @param  lower Index du premier <var>x</var> � ajouter � la bordure.
     * @param  upper Index suivant celui du dernier <var>y</var> � ajouter � la bordure.
     * @return Polyligne r�sultante. Ca sera en g�n�ral <code>scan</code>.
     */
    public static LineString appendBorder(LineString scan, final float[] data, int lower, final int upper) {
        if (REMOVE_DOUBLONS_IN_BORDER) {
            try {
                final Point2D check = getLastPoint(scan, null);
                final float x = (float)check.getX();
                final float y = (float)check.getY();
                while (lower<upper && data[lower]==x && data[lower+1]==y) {
                    lower += 2;
                }
            } catch (NoSuchElementException exception) {
                // No points in this polyline, no doublons, no problem. Continue...
            }
        }
        final int length = upper-lower;
        if (length > 0) {
            scan = getLast(scan);
            if (scan == null) {
                scan = new LineString(null);
            }
            scan.addBorder(data, lower, upper, true);
        }
        return scan;
    }

    /**
     * Inverse l'ordre de tous les points.  Cette m�thode retournera le
     * premier maillon d'une nouvelle cha�ne de polylignes qui contiendra
     * les donn�es en ordre inverse.
     *
     * @param  scan Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *              mais cette m�thode sera plus rapide si c'est le dernier maillon.
     */
    public static LineString reverse(LineString scan) {
        LineString queue = null;
        for (scan=getLast(scan); scan!=null; scan=scan.previous) {
            for (int arrayID=LAST_ARRAY; arrayID>=FIRST_ARRAY; arrayID--) {
                PointArray array = scan.getArray(arrayID);
                if (array != null) {
                    array = array.reverse();
                    /*
                     * Tous les tableaux sont balay�s dans cette boucle,
                     * un � un et dans l'ordre inverse. Les pr�fix doivent
                     * devenir des suffix, et les suffix doivent devenir
                     * des pr�fix.
                     */
                    if (arrayID == 0) {
                        queue = append(queue, new LineString(array));
                    } else {
                        queue = getLast(queue); // Par pr�caution.
                        if (queue == null) {
                            queue = new LineString(null);
                        }
                        assert queue.suffix==null;
                        queue.suffix=array;
                    }
                }
            }
        }
        return queue;
    }

    /**
     * Retourne les coordonn�es d'une bo�te qui englobe compl�tement tous
     * les points de la polyligne. Si cette polyligne ne contient aucun point,
     * alors cette m�thode retourne <code>null</code>.
     *
     * @param  scan Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *              mais cette m�thode sera plus rapide si c'est le premier maillon.
     * @return Un rectangle englobeant toutes les coordonn�es de cette polyligne et de
     *         ceux qui la suivent, ou <code>null</code>.
     */
    public static Rectangle2D getBounds2D(LineString scan) {
        Rectangle2D bounds = null;
        for (scan=getFirst(scan); scan!=null; scan=scan.next) {
            for (int arrayID=FIRST_ARRAY; arrayID<=LAST_ARRAY; arrayID++) {
                final PointArray array = scan.getArray(arrayID);
                if (array != null) {
                    final Rectangle2D toAdd = array.getBounds2D();
                    if (toAdd != null) {
                        if (bounds != null) {
                            bounds.add(toAdd);
                        } else {
                            bounds = toAdd;
                        }
                    }
                }
            }
        }
        return bounds;
    }

    /**
     * Retourne les coordonn�es d'une bo�te qui englobe compl�tement tous
     * les points de la polyligne. Si cette polyligne ne contient aucun point,
     * alors cette m�thode retourne <code>null</code>.
     *
     * @param  scan Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *              mais cette m�thode sera plus rapide si c'est le premier maillon.
     * @param  transform Transformation � appliquer sur les donn�es (nulle pour aucune).
     * @return Un rectangle englobeant toutes les coordonn�es de cette polyligne et de
     *         ceux qui la suivent, ou <code>null</code>.
     * @throws TransformException Si une projection cartographique a �chou�.
     */
    public static Rectangle2D getBounds2D(LineString scan, final MathTransform2D transform)
            throws TransformException
    {
        if (transform==null || transform.isIdentity()) {
            return getBounds2D(scan);
        }
        float xmin = Float.POSITIVE_INFINITY;
        float xmax = Float.NEGATIVE_INFINITY;
        float ymin = Float.POSITIVE_INFINITY;
        float ymax = Float.NEGATIVE_INFINITY;
        final Point2D.Float point = new Point2D.Float();
        for (scan=getFirst(scan); scan!=null; scan=scan.next) {
            for (int arrayID=FIRST_ARRAY; arrayID<=LAST_ARRAY; arrayID++) {
                final PointArray array = scan.getArray(arrayID);
                if (array != null) {
                    final PointIterator it = array.iterator(0);
                    while (it.hasNext()) {
                        point.x = it.nextX();
                        point.y = it.nextY();
                        transform.transform(point, point);
                        if (point.x<xmin) xmin=point.x;
                        if (point.x>xmax) xmax=point.x;
                        if (point.y<ymin) ymin=point.y;
                        if (point.y>ymax) ymax=point.y;
                    }
                }
            }
        }
        if (xmin<=xmax && ymin<=ymax) {
            return new Rectangle2D.Float(xmin, ymin, xmax-xmin, ymax-ymin);
        } else {
            return null;
        }
    }

    /**
     * Renvoie des statistiques sur la r�solution d'un polyligne. Cette r�solution sera
     * la distance moyenne entre deux points du polyligne,  mais sans prendre en compte
     * les "points de bordure"  (par exemple les points qui suivent le bord d'une carte
     * plut�t que de repr�senter une structure g�ographique r�elle).
     * <br><br>
     * La r�solution est calcul�e en utilisant le syst�me de coordonn�es sp�cifi�. Les
     * unit�s du r�sultat seront donc  les unit�s des deux premiers axes de ce syst�me
     * de coordonn�es,  <strong>sauf</strong>  si les deux premiers axes utilisent des
     * coordonn�es g�ographiques angulaires  (c'est le cas notamment des objets {@link
     * GeographicCoordinateSystem}).  Dans ce dernier cas,  le calcul utilisera plut�t
     * les distances orthodromiques sur l'ellipso�de ({@link Ellipsoid}) du syst�me de
     * coordonn�es.   En d'autres mots, pour les syst�mes cartographiques, le r�sultat
     * de cette m�thode sera toujours exprim� en unit�s lin�aires (souvent des m�tres)
     * peu importe que le syst�me de coordonn�es soit {@link ProjectedCoordinateSystem}
     * ou {@link GeographicCoordinateSystem}.
     *
     * @param  scan Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *         mais cette m�thode sera plus rapide si c'est le premier maillon.
     * @param  transformation Syst�mes de coordonn�es source et destination.
     *         <code>getSourceCS()</code> doit �tre le syst�me interne des points
     *         des polylignes, tandis que  <code>getTargetCS()</code> doit �tre le
     *         syst�me dans lequel faire le calcul. C'est <code>getTargetCS()</code>
     *         qui d�terminera les unit�s du r�sultat. Cet argument peut �tre nul
     *         si aucune transformation n'est n�cessaire. Dans ce cas, le syst�me
     *         de coordonn�es <code>getTargetCS()</code> sera suppos� cart�sien.
     * @return Statistiques sur la r�solution. L'objet retourn� ne sera jamais nul, mais les
     *         statistiques seront tous � NaN si cette courbe de niveau ne contenait aucun
     *         point. Voir la description de cette m�thode pour les unit�s.
     * @throws TransformException Si une transformation de coordonn�es a �chou�e.
     */
    static Statistics getResolution(LineString scan, final CoordinateOperation transformation)
            throws TransformException
    {
        /*
         * Checks the coordinate system validity. If valid and if geographic,
         * gets the ellipsoid to use for orthodromic distance computations.
         */
        final MathTransform2D transform;
        final Ellipsoid       ellipsoid;
        final Unit         xUnit, yUnit;
        if (transformation != null) {
            final MathTransform tr = transformation.getMathTransform();
            transform = !tr.isIdentity() ? (MathTransform2D) tr : null;
            final CoordinateReferenceSystem targetCRS = transformation.getTargetCRS();
            xUnit = targetCRS.getCoordinateSystem().getAxis(0).getUnit();
            yUnit = targetCRS.getCoordinateSystem().getAxis(1).getUnit();
            if (!Utilities.equals(xUnit, yUnit)) {
                throw new IllegalArgumentException(Errors.format(
                          Errors.Keys.NON_CARTESIAN_COORDINATE_SYSTEM_$1,
                          targetCRS.getName().toString()));
            }
            ellipsoid = CRSUtilities.getHeadGeoEllipsoid(targetCRS);
        } else {
            transform = null;
            ellipsoid = null;
            xUnit = yUnit = null;
        }
        /*
         * Compute statistics...
         */
        final Statistics stats = new Statistics();
        Point2D          point = new Point2D.Double();
        Point2D           last = null;
        for (scan=getFirst(scan); scan!=null; scan=scan.next) {
            final PointArray array = scan.array;
            if (array == null) {
                continue;
            }
            final PointIterator it = array.iterator(0);
            while (it.hasNext()) {
                point.setLocation(it.nextX(), it.nextY());
                if (transform != null) {
                    point = transform.transform(point, point);
                }
                final double distance;
                if (ellipsoid != null) {
                    
                    point.setLocation(
                            NonSI.DEGREE_ANGLE.getConverterTo(xUnit).convert(point.getX()),
                            NonSI.DEGREE_ANGLE.getConverterTo(yUnit).convert(point.getY()) );
                    if (last == null) {
                        last = (Point2D) point.clone();
                        continue;
                    }
                    //TODO : avoid a cast to the implementation to have this method
                    distance = ((DefaultEllipsoid)ellipsoid).orthodromicDistance(last, point);
                } else {
                    if (last == null) {
                        last = (Point2D) point.clone();
                        continue;
                    }
                    distance = last.distance(point);
                }
                stats.add(distance);
                final Point2D swap = last;
                last = point;
                point = swap;
            }
        }
        return stats;
    }

    /**
     * Modifie la r�solution de cette carte. Cette m�thode proc�dera en interpolant les donn�es
     * de fa�on � ce que chaque point soit s�par� du pr�c�dent par la distance sp�cifi�e.  Cela
     * peut se traduire par des �conomies importante de m�moire  si  une trop grande r�solution
     * n'est pas n�cessaire. Notez que cette op�ration est irreversible.  Appeler cette m�thode
     * une seconde fois avec une r�solution plus fine gonflera la taille des tableaux internes,
     * mais sans am�lioration r�elle de la pr�cision.
     *
     * @param  scan Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *         mais cette m�thode sera plus rapide si c'est le premier maillon.
     * @param  transformation Transformation permettant de convertir les coordonn�es des polylignes
     *         vers des coordonn�es cart�siennes. Cet argument peut �tre nul si les coordonn�es de
     *         <code>this</code> sont d�j� exprim�es selon un syst�me de coordonn�es cart�siennes.
     * @param  resolution R�solution d�sir�e, selon les m�mes unit�s que {@link #getResolution}.
     * @throws TransformException Si une erreur est survenue lors d'une projection cartographique.
     *
     * @see #getResolution
     */
    public static void setResolution(LineString scan, final CoordinateOperation transformation,
                                     final double resolution)
            throws TransformException
    {
        /*
         * Checks arguments validity. This method do not support latitude/longitude
         * coordinates. Coordinates must be projected in some linear units.
         */
        if (!(resolution > 0)) {
            throw new IllegalArgumentException(String.valueOf(resolution));
        }
        final MathTransform2D transform;
        final MathTransform2D inverseTransform;
        if (transformation != null) {
            final CoordinateReferenceSystem targetCRS = transformation.getTargetCRS();
            if (CRSUtilities.getHeadGeoEllipsoid(targetCRS)!=null ||
                !Utilities.equals(targetCRS.getCoordinateSystem().getAxis(0).getUnit(), 
                targetCRS.getCoordinateSystem().getAxis(1).getUnit()))
            {
                throw new IllegalArgumentException(Errors.format(
                          Errors.Keys.NON_CARTESIAN_COORDINATE_SYSTEM_$1,
                          targetCRS.getName().toString()));
            }
            final MathTransform tr = transformation.getMathTransform();
            if (!tr.isIdentity()) {
                transform        = (MathTransform2D) tr;
                inverseTransform = (MathTransform2D) transform.inverse();
            } else {
                transform        = null;
                inverseTransform = null;
            }
        } else {
            transform        = null;
            inverseTransform = null;
        }
        /*
         * Performs the linear interpolations, assuming
         * that we are using a cartesian coordinate system.
         */
        for (scan=getFirst(scan); scan!=null; scan=scan.next) {
            final PointArray points = scan.array;
            if (points == null) {
                continue;
            }
            /*
             * Obtiens les coordonn�es projet�es. Si ces coordonn�es repr�sentent des
             * degr�s de longitudes et latitudes, alors une projection cartographique
             * sera obligatoire afin de faire correctement les calculs de distances.
             */
            float[] array = points.toArray();
            assert (array.length & 1)==0;
            if (transform!=null && !transform.isIdentity()) {
                transform.transform(array, 0, array, 0, array.length/2);
            }
            if (array.length >= 2) {
                /*
                 * Effectue la d�cimation des coordonn�es. La toute premi�re
                 * coordonn�e sera conserv�e inchang�e. Il en ira de m�me de
                 * la derni�re, � la fin de ce bloc.
                 */
                final Point2D.Float point = new Point2D.Float(array[0], array[1]);
                final Line2D.Float   line = new  Line2D.Float(0,0, point.x, point.y);
                int destIndex   = 2; // Ne touche pas au premier point.
                int sourceIndex = 2; // Le premier point est d�j� lu.
                while (sourceIndex < array.length) {
                    line.x1 = line.x2;
                    line.y1 = line.y2;
                    line.x2 = array[sourceIndex++];
                    line.y2 = array[sourceIndex++];
                    Point2D next;
                    while ((next=ShapeUtilities.colinearPoint(line, point, resolution)) != null) {
                        if (destIndex == sourceIndex) {
                            final int extra = 256;
                            final float[] oldArray=array;
                            array=new float[array.length + extra];
                            System.arraycopy(oldArray, 0,         array, 0,                                  destIndex);
                            System.arraycopy(oldArray, destIndex, array, sourceIndex+=extra, oldArray.length-destIndex);
                        }
                        assert destIndex < sourceIndex;
                        array[destIndex++] = line.x1 = point.x = (float)next.getX();
                        array[destIndex++] = line.y1 = point.y = (float)next.getY();
                    }
                }
                /*
                 * La d�cimation est maintenant termin�e. V�rifie si le dernier point
                 * appara�t dans le tableau d�cim�. S'il n'appara�t pas, on l'ajoutera.
                 * Ensuite, on lib�rera la m�moire r�serv�e en trop.
                 */
                if (array[destIndex-2] != line.x2  ||  array[destIndex-1] != line.y2) {
                    if (destIndex == array.length) {
                        array = XArrays.resize(array, destIndex+2);
                    }
                    array[destIndex++] = line.x2;
                    array[destIndex++] = line.y2;
                }
                if (destIndex != array.length) {
                    array = XArrays.resize(array, destIndex);
                }
            }
            /*
             * Les interpolations �tant termin�es, reconvertit les coordonn�es
             * selon leur syst�me de coordonn�s initial et m�morise le nouveau
             * tableau d�cim� � la place de l'ancien.
             */
            if (inverseTransform != null) {
                inverseTransform.transform(array, 0, array, 0, array.length/2);
            }
            scan.array = (array!=null && array.length!=0) ? new DefaultArray(array) : null;
        }
    }

    /**
     * D�clare que les donn�es de cette polyligne ne vont plus changer. Cette
     * m�thode peut r�aranger les tableaux de points d'une fa�on plus compacte.
     *
     * @param  scan     Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *                  mais cette m�thode sera plus rapide si c'est le premier maillon.
     * @param  close    <code>true</code> pour indiquer que ces polylignes repr�sentent une
     *                  forme g�om�trique ferm�e (donc un polygone).
     * @param  compress An optional compression level, or <code>null</code> to lets the compression
     *                  unchanged.
     *
     * @return La polyligne compress�e (habituellement <code>scan</code> lui-m�me),
     *         ou <code>null</code> si la polyligne ne contenait aucune donn�e.
     */
    public static LineString freeze(LineString scan, final boolean close,
                                    final CompressionLevel compress) {
        scan = getFirst(scan);
        /*
         * Etape 1: Si on a demand� � fermer le polygone, v�rifie si le premier maillon de
         *          la cha�ne ne contenait qu'une bordure.  Si c'est le cas, on d�m�nagera
         *          cette bordure � la fin du dernier maillon.
         */
        if (close && scan!=null && scan.suffix!=null && scan.array==null) {
            LineString last = getLast(scan);
            if (last != scan) {
                if (last.suffix != null) {
                    last.suffix = last.suffix.insertAt(last.suffix.count(), scan.suffix, false);
                } else {
                    last.suffix = scan.suffix;
                }
                scan.suffix = null;
            }
        }
        /*
         * Etape 2: Fusionne ensemble des polylignes qui peuvent l'�tre.
         *          Deux polylignes peuvent �tre fusionn�es ensemble si elles
         *          ne sont s�par�es par aucune bordure, ou si elle sont toutes
         *          deux des bordures.
         */
        if (scan != null) {
            LineString previous = scan;
            LineString current  = scan;
            while ((current=current.next) != null) {
                if (previous.suffix == null) {
                    if (previous.array != null) {
                        // D�m�nage le tableau de points de 'previous' au d�but
                        // de celui de 'current' si aucune bordure ne les s�pare.
                        if (current.array != null) {
                            if (MERGE_POLYLINE_DATA) {
                                current.array = current.array.insertAt(0, previous.array, false);
                                previous.array = null;
                            }
                        } else {
                            current.array = previous.array;
                            previous.array = null;
                        }
                    }
                } else {
                    if (current.array == null) {
                        // D�m�nage le suffix de 'previous' au d�but de
                        // celui de 'current' si rien ne les s�pare.
                        if (current.suffix != null) {
                            current.suffix = current.suffix.insertAt(0, previous.suffix, false);
                        } else {
                            current.suffix = previous.suffix;
                        }
                        previous.suffix = null;
                    }
                }
                previous=current;
            }
        }
        /*
         * Etape 3: G�le et compresse les tableaux de points, et
         *          �limine les �ventuels tableaux devenus inutile.
         */
        LineString root = scan;
        while (scan!=null) {
            /*
             * Comprime tous les tableaux d'un maillon de la cha�ne.
             * La compression maximale ("full") ne sera toutefois pas
             * appliqu�e sur les "points de bordure".
             */
            for (int arrayID=FIRST_ARRAY; arrayID<=LAST_ARRAY; arrayID++) {
                final PointArray array = scan.getArray(arrayID);
                if (array != null) {
                    PointArray sub = array.getFinal( (arrayID==0) ? compress : null );
                    scan.setArray(arrayID, sub );
                }
            }
            /*
             * Supprime les maillons devenus vides. Ca peut avoir pour effet
             * de changer de maillon ("root") pour le d�but de la cha�ne.
             */
            LineString current=scan;
            scan = scan.next;
            if (current.isEmpty()) {
                current.remove();
                if (current == root) {
                    root = scan;
                }
            }
        }
        return root;
    }

    /**
     * Copy (<var>x</var>,<var>y</var>) coordinates in the specified destination array.
     * If <code>resolution</code> is greater than 0, then points that are closer than
     * <code>resolution</code> from previous one will be skiped.
     *
     * @param  The destination array. The coordinates will be filled in {@link ArrayData#array}
     *         from index {@link ArrayData#length}. The array will be expanded if needed, and
     *         {@link ArrayData#length} will be updated with index after the <code>array</code>'s
     *         element filled with the last <var>y</var> ordinates.
     * @param  resolution The minimum distance desired between points, in this polyline's
     *         coordinate system.
     * @param  transform The transform to apply, or <code>null</code> if none.
     * @throws TransformException if a transformation failed.
     */
    public static void toArray(LineString poly, final ArrayData dest, float resolution,
                               final MathTransform2D transform) throws TransformException
    {
        resolution *= resolution;
        poly = getFirst(poly);
        GeneralPath path = null;
        for (LineString scan=poly; scan!=null; scan=scan.next) {
            for (int i=FIRST_ARRAY; i<=LAST_ARRAY; i++) {
                final PointArray array = scan.getArray(i);
                if (array != null) {
                    final int lower = dest.length();
                    // On ne d�cime pas les points de bordure (i!=0).
                    array.toArray(dest, (i==0) ? resolution : 0);
                    if (transform != null) {
                        if (i==0) {
                            // Transform the main data: fast way, no curves.
                            final float[] data = dest.array();
                            transform.transform(data, lower, data, lower, (dest.length()-lower)/2);
                        } else {
                            // Transform the borders: slower, can create curves.
                            if (path == null) {
                                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                            }
                            path.reset();
                            dest.extract(lower, path);
                            dest.append(transform.createTransformedShape(path));
                        }
                    }
                }
            }
        }
    }

    /**
     * Retourne une repr�sentation de cet objet sous forme
     * de cha�ne de caract�res.  Cette repr�sentation sera
     * de la forme <code>"LineString[3 of 4; 47 pts]"</code>.
     */
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer(Classes.getShortClassName(this));
        buffer.append('[');
        int index=1;
        for (LineString scan=previous; scan!=null; scan=scan.previous) {
            index++;
        }
        buffer.append(index);
        for (LineString scan=next; scan!=null; scan=scan.next) {
            index++;
        }
        buffer.append(" of ");
        buffer.append(index);
        buffer.append("; ");
        buffer.append(array!=null ? array.count() : 0);
        buffer.append(" points");
        if (suffix != null) {
            buffer.append(" + ");
            buffer.append(suffix.count());
            buffer.append(" in border");
        }
        buffer.append(']');
        return buffer.toString();
    }

    /**
     * Retourne un code repr�sentant la polyligne sp�cifi�e.
     *
     * @param  scan Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *              mais cette m�thode sera plus rapide si c'est le premier maillon.
     * @return Un code calcul� � partir de quelques points de la polyligne sp�cifi�e.
     */
    public static int hashCode(LineString scan) {
        int code = (int)serialVersionUID;
        for (scan=getFirst(scan); scan!=null; scan=scan.next) {
            if (scan.array != null) {
                code = 37*code + scan.array.hashCode();
            }
        }
        return code;
    }

    /**
     * Indique si deux polylignes contiennent les m�mes points. Cette m�thode
     * retourne aussi <code>true</code> si les deux arguments sont nuls.
     *
     * @param poly1 Premi�re polyligne. Cet argument peut �tre n'importe quel maillon d'une
     *              cha�ne, mais cette m�thode sera plus rapide si c'est le premier maillon.
     * @param poly2 Seconde polyligne. Cet argument peut �tre n'importe quel maillon d'une
     *              cha�ne, mais cette m�thode sera plus rapide si c'est le premier maillon.
     */
    public static boolean equals(LineString poly1, LineString poly2) {
        poly1 = getFirst(poly1);
        poly2 = getFirst(poly2);
        while (poly1 != poly2) {
            if (poly1==null || poly2==null) {
                return false;
            }
            for (int arrayID=FIRST_ARRAY; arrayID<=LAST_ARRAY; arrayID++) {
                final PointArray array1 = poly1.getArray(arrayID);
                final PointArray array2 = poly2.getArray(arrayID);
                if (!Utilities.equals(array1, array2)) {
                    return false;
                }
            }
            poly1 = poly1.next;
            poly2 = poly2.next;
        }
        return true;
    }

    /**
     * Retourne une copie de la polyligne sp�cifi�e. Cette m�thode ne copie que les r�f�rences
     * vers une version immutable des tableaux de points. Les points eux-m�mes ne sont pas
     * copi�s, ce qui permet d'�viter de consommer une quantit� excessive de m�moire.
     *
     * @param  scan Polyligne. Cet argument peut �tre n'importe quel maillon d'une cha�ne,
     *              mais cette m�thode sera plus rapide si c'est le premier maillon.
     * @return Copie de la cha�ne <code>scan</code>.
     */
    public static LineString clone(LineString scan) {
        LineString queue=null;
        for (scan=getFirst(scan); scan!=null; scan=scan.next) {
            final LineString toMerge = new LineString(null);
            for (int arrayID=FIRST_ARRAY; arrayID<=LAST_ARRAY; arrayID++) {
                PointArray array = scan.getArray(arrayID);
                if (array != null) {
                    array = array.getFinal(null);
                }
                toMerge.setArray(arrayID, array);
            }
            if (!toMerge.isEmpty()) {
                queue = append(queue, toMerge);
            }
        }
        return queue;
    }




    /**
     * A set of points ({@link Point2D}) from a polyline or a polygon.
     * This set of points is returned by {@link Polyline#getPoints}.
     *
     * @version $Id: LineString.java 17672 2006-01-19 00:25:55Z desruisseaux $
     * @author Martin Desruisseaux
     */
    static final class Collection extends AbstractCollection {
        /**
         * Premi�re polyligne de la cha�ne de points � balayer.
         */
        private final LineString data;

        /**
         * Transformation � appliquer sur chacun des points.
         */
        private final MathTransform2D transform;

        /**
         * Construit un ensemble de points.
         */
        public Collection(final LineString data, final MathTransform2D transform) {
            this.data = data;
            this.transform = transform;
        }

        /**
         * Retourne le nombre de points dans cet ensemble.
         */
        public int size() {
            return getPointCount(data);
        }

        /**
         * Retourne un it�rateur balayant les points de cet ensemble.
         */
        public java.util.Iterator iterator() {
            return new Iterator(data, transform);
        }
    }




    /**
     * Iterateur balayant les coordonn�es d'un polyligne ou d'un polygone.
     *
     * @version $Id: LineString.java 17672 2006-01-19 00:25:55Z desruisseaux $
     * @author Martin Desruisseaux
     */
    static final class Iterator implements java.util.Iterator {
        /**
         * Polyligne qui sert de point de d�part � cet it�rateur.
         * Cette informations est utilis�e par {@link #rewind}.
         */
        private final LineString start;

        /**
         * Polyligne qui sera balay�e par les prochains appels de {@link #next}.
         * Ce champs sera mis � jour au fur et � mesure que l'on passera d'une
         * polyligne � l'autre.
         */
        private LineString current;

        /**
         * Code indiquant quel champs de {@link #current} est pr�sentement en cours d'examen:
         *
         *    -1 pour {@link LineString#prefix},
         *     0 pour {@link LineString#array} et
         *    +1 pour {@link LineString#suffix}.
         */
        private int arrayID = FIRST_ARRAY-1;;

        /**
         * It�rateur balayant les donn�es. Cet it�rateur
         * aura �t� obtenu d'un tableau {@link PointArray}.
         */
        private PointIterator iterator;

        /**
         * Transformation � appliquer sur les coordonn�es,
         * ou <code>null</code> s'il n'y en a pas.
         */
        private final MathTransform2D transform;

        /**
         * Point utilis� temporairement pour les projections.
         */
        private final Point2D.Float point = new Point2D.Float();

        /**
         * Initialise l'it�rateur de fa�on � d�marrer
         * les balayages � partir de la polyligne sp�cifi�e.
         *
         * @param start Polyligne (peut �tre nul).
         * @param transform Transformation � appliquer sur les
         *        coordonn�es, ou <code>null</code> s'il n'y en a pas.
         */
        public Iterator(final LineString start, final MathTransform2D transform) {
            this.start = current = getFirst(start);
            this.transform = (transform!=null && !transform.isIdentity()) ? transform : null;
            nextArray();
        }

        /**
         * Avance l'it�rateur au prochain tableau.
         */
        private void nextArray() {
            while (current != null) {
                while (++arrayID <= LAST_ARRAY) {
                    final PointArray array = current.getArray(arrayID);
                    if (array != null) {
                        iterator = array.iterator(0);
                        if (iterator.hasNext()) {
                            return;
                        }
                    }
                }
                arrayID = LineString.FIRST_ARRAY-1;
                current = current.next;
            }
            iterator = null;
        }

        /**
         * Indique s'il reste des donn�es que peut retourner {@link #next}.
         */
        @Override
        public boolean hasNext() {
            while (iterator != null) {
                if (iterator.hasNext()) {
                    return true;
                }
                nextArray();
            }
            return false;
        }

        /**
         * Retourne les coordonn�es du point suivant.
         *
         * @return Le point suivant comme un objet {@link Point2D}.
         */
        @Override
        public Object next() throws NoSuchElementException {
            if (hasNext()) {
                Point2D point = (Point2D) iterator.next();
                if (transform != null) try {
                    point = transform.transform(point, point);
                } catch (TransformException exception) {
                    // Should not happen, since {@link Polyline#setCoordinateSystem}
                    // has already successfully projected every points.
                    unexpectedException("LineString", "next", exception);
                    return null;
                }
                return point;
            } else {
                throw new NoSuchElementException();
            }
        }

        /**
         * Retourne les coordonn�es du point suivant. Contrairement � la m�thode {@link #next()},
         * celle-ci retourne <code>null</code> sans lancer d'exception s'il ne reste plus de point
         * � balayer.
         *
         * @param  dest Point dans lequel m�moriser le r�sultat. Si cet argument
         *         est nul, un nouvel objet sera cr�� et retourn� pour m�moriser
         *         les coordonn�es.
         * @return S'il restait des coordonn�es � lire, le point <code>point</code> qui avait �t�
         *         sp�cifi� en argument. Si <code>point</code> �tait nul, un objet {@link Point2D}
         *         nouvellement cr��. S'il ne restait plus de donn�es � lire, cette m�thode retourne
         *         toujours <code>null</code>.
         */
        final Point2D.Float next(Point2D.Float dest) {
            while (hasNext()) {
                if (dest != null) {
                    dest.x = iterator.nextX();
                    dest.y = iterator.nextY();
                } else {
                    dest = new Point2D.Float(iterator.nextX(), iterator.nextY());
                }
                if (transform != null) try {
                    transform.transform(dest, dest);
                } catch (TransformException exception) {
                    // Should not happen, since {@link Polyline#setCoordinateSystem}
                    // has already successfully projected every points.
                    unexpectedException("LineString", "next", exception);
                    continue;
                }
                return dest;
            }
            return null;
        }

        /**
         * Retourne les coordonn�es du prochain point dans le champs
         * (<var>x2</var>,<var>y2</var>) de la ligne sp�cifi�e. Les
         * anciennes coordonn�es (<var>x2</var>,<var>y2</var>) seront
         * pr�alablement copi�es dans (<var>x1</var>,<var>y1</var>).
         * Si cette m�thode a r�ussie, elle retourne <code>true</code>.
         *
         * Si elle a �chou�e parce qu'il ne restait plus de points disponibles, elle
         * aura tout de m�me copi� les coordonn�es (<var>x2</var>,<var>y2</var>) dans
         * (<var>x1</var>,<var>y1</var>) (ce qui aura pour effet de donner � la ligne
         * une longueur de 0) et retournera <code>false</code>.
         */
        final boolean next(final Line2D.Float line) {
            line.x1 = line.x2;
            line.y1 = line.y2;
            while (hasNext()) {
                if (transform == null) {
                    line.x2 = iterator.nextX();
                    line.y2 = iterator.nextY();
                } else try {
                    point.x = iterator.nextX();
                    point.y = iterator.nextY();
                    transform.transform(point, point);
                    line.x2 = point.x;
                    line.y2 = point.y;
                } catch (TransformException exception) {
                    // Should not happen, since {@link Polyline#setCoordinateSystem}
                    // has already successfully projected every points.
                    unexpectedException("LineString", "next", exception);
                    continue;
                }
                return true;
            }
            return false;
        }

        /**
         * Repositionne cet it�rateur � son point de d�part.
         */
        final void rewind() {
            current  = start;
            arrayID  = FIRST_ARRAY-1;
            nextArray();
        }

        /**
         * Cette op�ration n'est pas support�e.
         *
         * @throws UnsupportedOperationException Syst�matiquement lanc�e.
         */
        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * M�thode appel�e lorsqu'une erreur inatendue est survenue.
     *
     * @param source Nom de la classe dans laquelle est survenu l'exception.
     * @param method Nom de la m�thode dans laquelle est survenu l'exception.
     * @param exception L'exception survenue.
     */
    static void unexpectedException(final String classe, final String method,
                                    final TransformException exception)
    {
        Logging.getLogger(LineString.class).log(Level.WARNING, method.toString() +" " +exception.getMessage());
    }
}

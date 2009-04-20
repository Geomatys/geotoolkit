/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display.array;

import org.geotoolkit.display2d.array.GenericArray;
import org.geotoolkit.display2d.array.DefaultArray;
import org.geotoolkit.display2d.array.PointArray;
import org.geotoolkit.display2d.array.PointIterator;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.geotoolkit.display2d.geom.CompressionLevel;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.display2d.array.DynamicArray;


/**
 * Test the <code>org.geotools.renderer.array</code> package.
 *
 * @source $URL: http://svn.geotools.org/branches/legacy/migrate/test/org/geotools/renderer/array/PointArrayTest.java $
 * @version $Id: PointArrayTest.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 */
public class PointArrayTest extends TestCase {
    /**
     * Set to <code>true</code> for printing some informations to the standard output.
     */
    private static boolean PRINT = false;

    /**
     * The object to use for generating random numbers.
     */
    private final Random random = new Random();

    /**
     * Returns the test suite.
     */
    public static Test suite() {
        return new TestSuite(PointArrayTest.class);
    }
    
    /**
     * Constructs a test case with the given name.
     */
    public PointArrayTest(final String name) {
        super(name);
    }

    /**
     * Test {@link DefaultArray} and its subclasses.
     */
    public void testDefaultArray() {
        float[] checkPoints = new float[2];
        PointArray   points = new DefaultArray(checkPoints);
        for (int i=0; i<2000; i++) {
            /*
             * Construit un tableau (de longueur paire) qui sera ins�r� au milieu
             * des points d�j� existants. Ce tableau sera remplis avec des nombres
             * al�atoires pour distinguer les diff�rentes coordonn�es.
             */
            final float[] toMerge = new float[(int)(32*random.nextDouble()) & ~1];
            for (int j=0; j<toMerge.length; j++) {
                toMerge[j] = (float) (2000*random.nextDouble()-1000);
            }
            /*
             * S�lectionne au hasard une plage de valeurs � prendre en compte dans
             * le tableau <code>toMerge</code>. Les autres valeurs seront abandonn�es.
             * Choisit aussi au hasard un point d'insertions pour les nouveaux points.
             */
            final int lower = (int)(toMerge.length*(0.49*random.nextDouble()    )) & ~1;
            final int upper = (int)(toMerge.length*(0.49*random.nextDouble()+0.5)) & ~1;
            final int index = (int)(checkPoints.length  *random.nextDouble()     ) & ~1;
            /*
             * Ins�re les nouveaux points dans le tableau 'checkPoints'. Ce tableau est
             * maintenu uniquement � des fins de comparaisons avec le "vrai" tableau de
             * points. V�rifie si les deux tableaux ont un contenu identique.
             */
            final boolean reverse = (random.nextDouble() > 0.5);
            points = points.insertAt(index/2, toMerge, lower, upper, reverse);
            if (reverse) {
                DynamicArray.reverse(toMerge, lower, upper);
            }
            checkPoints = XArrays.insert(toMerge, lower, checkPoints, index, upper-lower);
            assertEquals(checkPoints.length, 2*points.count());
            assertTrue(Arrays.equals(points.toArray(), checkPoints));
        }
        /*
         * Pr�pare une liste d'index d�limitant des plages de points �
         * l'int�rieur du tableau. Ces plages seront extraites plusieurs
         * fois sur diff�rentes versions du tableau de points.
         */
        final int index[]=new int[8];
        for (int i=0; i<index.length; i++) {
            index[i] = (int)(random.nextDouble()*points.count());
        }
        Arrays.sort(index);
        /*
         * Teste maintenant l'extraction d'un sous-tableau
         * ainsi que la compression des donn�es.
         */
        for (int i=0; i<=3; i++) {
            switch (i) {
                case 0: {
                    break;
                }
                case 1: {
                    points = points.getFinal(CompressionLevel.DIRECT_AS_FLOATS);
                    break;
                }
                case 2: {
                    checkPoints=points.toArray();
                    Arrays.sort(checkPoints);
                    points = new DefaultArray(checkPoints);
                    break;
                }
                case 3: {
                    points = points.getFinal(CompressionLevel.RELATIVE_AS_BYTES);
                    break;
                }
            }
            if (PRINT) {
                System.out.println(points);
                for (int j=0; j<index.length; j++) {
                    System.out.print("    [");
                    System.out.print(index[j]);
                    System.out.print("..");
                    System.out.print(index[j+1]);
                    System.out.print("]: ");
                    System.out.println(points.subarray(index[j], index[++j]));
                }
                System.out.println();
            }
        }
        /*
         * Recherche l'�cart maximal entre les
         * donn�es compress�es et les donn�es r�elles.
         */
        float dx=0, dy=0;
        final PointIterator iterator=points.iterator(0);
        for (int i=0; iterator.hasNext();) {
            assertTrue(i<checkPoints.length);
            // The compressed array may have less points than 'checkPoints'
            final float dxi = Math.abs(iterator.nextX()-checkPoints[i++]);
            final float dyi = Math.abs(iterator.nextY()-checkPoints[i++]);
            if (dxi > dx) dx=dxi;
            if (dyi > dy) dy=dyi;
        }
        if (PRINT) {
            System.out.print(dx);
            System.out.print(", ");
            System.out.println(dy);
        }
    }

    /**
     * Test {@link GenericArray}.
     */
    public void testGenericArray() {
        for (int xt=0; xt<8; xt++) {
            for (int yt=0; yt<8; yt++) {
                final Object x = create(xt);
                final Object y = create(yt);
                final GenericArray array = new GenericArray(x,y);
                final float[] data = array.toArray();
                if (xt>=2 && yt>=2) {
                    for (int i=0; i<data.length; i++) {
                        final double value = Array.getDouble((i&1)==0 ? x : y, i/2);
                        assertEquals("GenericArray.toArray() returned wrong values",
                                     Float.floatToIntBits((float)value),
                                     Float.floatToIntBits(data[i]));
                    }
                }
            }
        }
    }

    /**
     * Returns a random array of one of the primitive Java type.
     */
    private Object create(final int type) {
        final int length = 128;
        switch (type) {
            case 0: {
                final boolean[] array = new boolean[length];
                for (int i=0; i<array.length; i++) {
                    array[i] = random.nextBoolean();
                }
                return array;
            }
            case 1: {
                final char[] array = new char[length];
                for (int i=0; i<array.length; i++) {
                    array[i] = (char)random.nextInt();
                }
                return array;
            }
            case 2: {
                final byte[] array = new byte[length];
                for (int i=0; i<array.length; i++) {
                    array[i] = (byte)random.nextInt();
                }
                return array;
            }
            case 3: {
                final short[] array = new short[length];
                for (int i=0; i<array.length; i++) {
                    array[i] = (short)random.nextInt();
                }
                return array;
            }
            case 4: {
                final int[] array = new int[length];
                for (int i=0; i<array.length; i++) {
                    array[i] = random.nextInt();
                }
                return array;
            }
            case 5: {
                final long[] array = new long[length];
                for (int i=0; i<array.length; i++) {
                    array[i] = random.nextLong();
                }
                return array;
            }
            case 6: {
                final float[] array = new float[length];
                for (int i=0; i<array.length; i++) {
                    array[i] = random.nextFloat();
                }
                return array;
            }
            default: {
                final double[] array = new double[length];
                for (int i=0; i<array.length; i++) {
                    array[i] = random.nextDouble();
                }
                return array;
            }
        }
    }

    /**
     * V�rifie le bon fonctionnement de cette classe. Cette m�thode peut �tre appel�e
     * sans argument.  Les assertions doivent �tre activ�es (option <code>-ea</code>)
     * pour que la v�rification soit effective. Cette m�thode peut aussi �tre ex�cut�e
     * sans les assertions pour tester les performances.
     */
    public static void main(final String[] args) {
        PRINT = true;
        final PointArrayTest test = new PointArrayTest(null);
        test.testDefaultArray();
        test.testGenericArray();
    }
}

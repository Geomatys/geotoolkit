/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util;

import java.util.Arrays;
import java.util.Comparator;
import java.lang.reflect.Array;

import org.geotoolkit.lang.Static;


/**
 * Simple operations on arrays. This class provides a central place for inserting and deleting
 * elements in an array, as well as resizing the array.
 * <p>
 * The {@code resize} methods provided in this class are very similar to the {@code copyOf}
 * methods provided in {@link Arrays} since Java 6, except that they accept {@code null}
 * arrays and do not copy anything if the given array already has the requested length.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see Arrays
 *
 * @since 2.0
 * @module
 */
public final class XArrays extends Static {
    /**
     * An empty array of {@code double} primitive type.
     * Such arrays are immutable and can be safely shared.
     *
     * @since 3.20
     */
    public static final double[] EMPTY_DOUBLE = new double[0];

    /**
     * An empty array of {@code float} primitive type.
     * Such arrays are immutable and can be safely shared.
     *
     * @since 3.20
     */
    public static final float[] EMPTY_FLOAT = new float[0];

    /**
     * An empty array of {@code long} primitive type.
     * Such arrays are immutable and can be safely shared.
     *
     * @since 3.20
     */
    public static final long[] EMPTY_LONG = new long[0];

    /**
     * An empty array of {@code int} primitive type.
     * Such arrays are immutable and can be safely shared.
     *
     * @since 3.20
     */
    public static final int[] EMPTY_INT = new int[0];

    /**
     * An empty array of {@code short} primitive type.
     * Such arrays are immutable and can be safely shared.
     *
     * @since 3.20
     */
    public static final short[] EMPTY_SHORT = new short[0];

    /**
     * An empty array of {@code byte} primitive type.
     * Such arrays are immutable and can be safely shared.
     *
     * @since 3.20
     */
    public static final byte[] EMPTY_BYTE = new byte[0];

    /**
     * An empty array of {@code char} primitive type.
     * Such arrays are immutable and can be safely shared.
     *
     * @since 3.20
     */
    public static final char[] EMPTY_CHAR = new char[0];

    /**
     * An empty array of {@code boolean} primitive type.
     * Such arrays are immutable and can be safely shared.
     *
     * @since 3.20
     */
    public static final boolean[] EMPTY_BOOLEAN = new boolean[0];

    /**
     * Do not allow instantiation of this class.
     */
    private XArrays() {
    }

    /**
     * Returns an array containing the same elements as the given {@code array} but with the
     * specified {@code length}, truncating or padding with {@code null} if necessary.
     * <ul>
     *   <li><p>If the given {@code length} is longer than the length of the given {@code array},
     *       then the returned array will contain all the elements of {@code array} at index
     *       <var>i</var> &lt; {@code array.length}. Elements at index <var>i</var> &gt;=
     *       {@code array.length} are initialized to {@code null}.</p></li>
     *
     *   <li><p>If the given {@code length} is shorter than the length of the given {@code array},
     *       then the returned array will contain only the elements of {@code array} at index
     *       <var>i</var> &lt; {@code length}. Remainding elements are not copied.</p></li>
     *
     *   <li><p>If the given {@code length} is equal to the length of the given {@code array},
     *       then {@code array} is returned unchanged. <strong>No copy</strong> is performed.
     *       This behavior is different than the {@link Arrays#copyOf} one.</p></li>
     * </ul>
     * <p>
     * Note that if the given array is {@code null}, then this method unconditionally returns
     * {@code null} no matter the value of the {@code length} argument.
     *
     * @param  <E> The array elements.
     * @param  array  Array to resize, or {@code null}.
     * @param  length Length of the desired array.
     * @return A new array of the requested length, or {@code array} if the given
     *         array is {@code null} or already have the requested length.
     *
     * @see Arrays#copyOf(Object[],int)
     */
    public static <E> E[] resize(final E[] array, final int length) {
        return (array == null || array.length == length) ? array : Arrays.copyOf(array, length);
    }

    /**
     * Returns an array containing the same elements as the given {@code array} but
     * specified {@code length}, truncating or padding with zeros if necessary.
     * This method returns {@code null} if and only if the given array is {@code null},
     * in which case the value of the {@code length} argument is ignored.
     *
     * @param  array  Array to resize, or {@code null}.
     * @param  length Length of the desired array.
     * @return A new array of the requested length, or {@code array} if the given
     *         array is {@code null} or already have the requested length.
     *
     * @see Arrays#copyOf(double[],int)
     */
    public static double[] resize(final double[] array, final int length) {
        if (array != null) {
            if (length == 0) {
                return EMPTY_DOUBLE;
            }
            if (array.length != length) {
                return Arrays.copyOf(array, length);
            }
        }
        return array;
    }

    /**
     * Returns an array containing the same elements as the given {@code array} but
     * specified {@code length}, truncating or padding with zeros if necessary.
     * This method returns {@code null} if and only if the given array is {@code null},
     * in which case the value of the {@code length} argument is ignored.
     *
     * @param  array  Array to resize, or {@code null}.
     * @param  length Length of the desired array.
     * @return A new array of the requested length, or {@code array} if the given
     *         array is {@code null} or already have the requested length.
     *
     * @see Arrays#copyOf(float[],int)
     */
    public static float[] resize(final float[] array, final int length) {
        if (array != null) {
            if (length == 0) {
                return EMPTY_FLOAT;
            }
            if (array.length != length) {
                return Arrays.copyOf(array, length);
            }
        }
        return array;
    }

    /**
     * Returns an array containing the same elements as the given {@code array} but
     * specified {@code length}, truncating or padding with zeros if necessary.
     * This method returns {@code null} if and only if the given array is {@code null},
     * in which case the value of the {@code length} argument is ignored.
     *
     * @param  array  Array to resize, or {@code null}.
     * @param  length Length of the desired array.
     * @return A new array of the requested length, or {@code array} if the given
     *         array is {@code null} or already have the requested length.
     *
     * @see Arrays#copyOf(long[],int)
     */
    public static long[] resize(final long[] array, final int length) {
        if (array != null) {
            if (length == 0) {
                return EMPTY_LONG;
            }
            if (array.length != length) {
                return Arrays.copyOf(array, length);
            }
        }
        return array;
    }

    /**
     * Returns an array containing the same elements as the given {@code array} but
     * specified {@code length}, truncating or padding with zeros if necessary.
     * This method returns {@code null} if and only if the given array is {@code null},
     * in which case the value of the {@code length} argument is ignored.
     *
     * @param  array  Array to resize, or {@code null}.
     * @param  length Length of the desired array.
     * @return A new array of the requested length, or {@code array} if the given
     *         array is {@code null} or already have the requested length.
     *
     * @see Arrays#copyOf(int[],int)
     */
    public static int[] resize(final int[] array, final int length) {
        if (array != null) {
            if (length == 0) {
                return EMPTY_INT;
            }
            if (array.length != length) {
                return Arrays.copyOf(array, length);
            }
        }
        return array;
    }

    /**
     * Returns an array containing the same elements as the given {@code array} but
     * specified {@code length}, truncating or padding with zeros if necessary.
     * This method returns {@code null} if and only if the given array is {@code null},
     * in which case the value of the {@code length} argument is ignored.
     *
     * @param  array  Array to resize, or {@code null}.
     * @param  length Length of the desired array.
     * @return A new array of the requested length, or {@code array} if the given
     *         array is {@code null} or already have the requested length.
     *
     * @see Arrays#copyOf(short[],int)
     */
    public static short[] resize(final short[] array, final int length) {
        if (array != null) {
            if (length == 0) {
                return EMPTY_SHORT;
            }
            if (array.length != length) {
                return Arrays.copyOf(array, length);
            }
        }
        return array;
    }

    /**
     * Returns an array containing the same elements as the given {@code array} but
     * specified {@code length}, truncating or padding with zeros if necessary.
     * This method returns {@code null} if and only if the given array is {@code null},
     * in which case the value of the {@code length} argument is ignored.
     *
     * @param  array  Array to resize, or {@code null}.
     * @param  length Length of the desired array.
     * @return A new array of the requested length, or {@code array} if the given
     *         array is {@code null} or already have the requested length.
     *
     * @see Arrays#copyOf(byte[],int)
     */
    public static byte[] resize(final byte[] array, final int length) {
        if (array != null) {
            if (length == 0) {
                return EMPTY_BYTE;
            }
            if (array.length != length) {
                return Arrays.copyOf(array, length);
            }
        }
        return array;
    }

   /**
     * Returns an array containing the same elements as the given {@code array} but
     * specified {@code length}, truncating or padding with zeros if necessary.
     * This method returns {@code null} if and only if the given array is {@code null},
     * in which case the value of the {@code length} argument is ignored.
     *
     * @param  array  Array to resize, or {@code null}.
     * @param  length Length of the desired array.
     * @return A new array of the requested length, or {@code array} if the given
     *         array is {@code null} or already have the requested length.
     *
     * @see Arrays#copyOf(char[],int)
    */
    public static char[] resize(final char[] array, final int length) {
        if (array != null) {
            if (length == 0) {
                return EMPTY_CHAR;
            }
            if (array.length != length) {
                return Arrays.copyOf(array, length);
            }
        }
        return array;
    }

    /**
     * Returns an array containing the same elements as the given {@code array} but
     * specified {@code length}, truncating or padding with {@code false} if necessary.
     * This method returns {@code null} if and only if the given array is {@code null},
     * in which case the value of the {@code length} argument is ignored.
     *
     * @param  array  Array to resize, or {@code null}.
     * @param  length Length of the desired array.
     * @return A new array of the requested length, or {@code array} if the given
     *         array is {@code null} or already have the requested length.
     *
     * @see Arrays#copyOf(boolean[],int)
     */
    public static boolean[] resize(final boolean[] array, final int length) {
        if (array != null) {
            if (length == 0) {
                return EMPTY_BOOLEAN;
            }
            if (array.length != length) {
                return Arrays.copyOf(array, length);
            }
        }
        return array;
    }

    /**
     * Returns an array containing the same elements than the given array except for
     * the given range. If the {@code length} argument is 0, then this method creates
     * the {@code array} reference unchanged. Otherwise this method creates a new array.
     * In every cases, the given array is never modified.
     *
     * @param <E>     The array type.
     * @param array   Array from which to remove elements.
     * @param first   Index of the first element to remove from the given {@code array}.
     * @param length  Number of elements to remove.
     * @return        Array with the same elements than the given {@code array} except for the
     *                removed elements, or {@code array} if {@code length} is 0.
     */
    private static <E> E doRemove(final E array, final int first, final int length) {
        if (length == 0) {
            return array;
        }
        int arrayLength = Array.getLength(array);
        @SuppressWarnings("unchecked")
        final E newArray = (E) Array.newInstance(array.getClass().getComponentType(), arrayLength -= length);
        System.arraycopy(array, 0,            newArray, 0,                 first);
        System.arraycopy(array, first+length, newArray, first, arrayLength-first);
        return newArray;
    }

    /**
     * Returns an array containing the same elements than the given array except for
     * the given range. If the {@code length} argument is 0, then this method returns
     * the {@code array} reference unchanged (except if empty). Otherwise this method
     * creates a new array. In every cases, the given array is never modified.
     *
     * @param <E>     The type of array elements.
     * @param array   Array from which to remove elements.
     * @param first   Index of the first element to remove from the given {@code array}.
     * @param length  Number of elements to remove.
     * @return        Array with the same elements than the given {@code array} except for the
     *                removed elements, or {@code array} if {@code length} is 0.
     */
    public static <E> E[] remove(final E[] array, final int first, final int length) {
        return doRemove(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array except for
     * the given range. If the {@code length} argument is 0, then this method returns
     * the {@code array} reference unchanged (except if empty). Otherwise this method
     * creates a new array. In every cases, the given array is never modified.
     *
     * @param array   Array from which to remove elements.
     * @param first   Index of the first element to remove from the given {@code array}.
     * @param length  Number of elements to remove.
     * @return        Array with the same elements than the given {@code array} except for the
     *                removed elements, or {@code array} if {@code length} is 0.
     */
    public static double[] remove(final double[] array, final int first, final int length) {
        return (first == 0 && length == array.length) ? EMPTY_DOUBLE : doRemove(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array except for
     * the given range. If the {@code length} argument is 0, then this method returns
     * the {@code array} reference unchanged (except if empty). Otherwise this method
     * creates a new array. In every cases, the given array is never modified.
     *
     * @param array   Array from which to remove elements.
     * @param first   Index of the first element to remove from the given {@code array}.
     * @param length  Number of elements to remove.
     * @return        Array with the same elements than the given {@code array} except for the
     *                removed elements, or {@code array} if {@code length} is 0.
     */
    public static float[] remove(final float[] array, final int first, final int length) {
        return (first == 0 && length == array.length) ? EMPTY_FLOAT : doRemove(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array except for
     * the given range. If the {@code length} argument is 0, then this method returns
     * the {@code array} reference unchanged (except if empty). Otherwise this method
     * creates a new array. In every cases, the given array is never modified.
     *
     * @param array   Array from which to remove elements.
     * @param first   Index of the first element to remove from the given {@code array}.
     * @param length  Number of elements to remove.
     * @return        Array with the same elements than the given {@code array} except for the
     *                removed elements, or {@code array} if {@code length} is 0.
     */
    public static long[] remove(final long[] array, final int first, final int length) {
        return (first == 0 && length == array.length) ? EMPTY_LONG : doRemove(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array except for
     * the given range. If the {@code length} argument is 0, then this method returns
     * the {@code array} reference unchanged (except if empty). Otherwise this method
     * creates a new array. In every cases, the given array is never modified.
     *
     * @param array   Array from which to remove elements.
     * @param first   Index of the first element to remove from the given {@code array}.
     * @param length  Number of elements to remove.
     * @return        Array with the same elements than the given {@code array} except for the
     *                removed elements, or {@code array} if {@code length} is 0.
     */
    public static int[] remove(final int[] array, final int first, final int length) {
        return (first == 0 && length == array.length) ? EMPTY_INT : doRemove(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array except for
     * the given range. If the {@code length} argument is 0, then this method returns
     * the {@code array} reference unchanged (except if empty). Otherwise this method
     * creates a new array. In every cases, the given array is never modified.
     *
     * @param array   Array from which to remove elements.
     * @param first   Index of the first element to remove from the given {@code array}.
     * @param length  Number of elements to remove.
     * @return        Array with the same elements than the given {@code array} except for the
     *                removed elements, or {@code array} if {@code length} is 0.
     */
    public static short[] remove(final short[] array, final int first, final int length) {
        return (first == 0 && length == array.length) ? EMPTY_SHORT : doRemove(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array except for
     * the given range. If the {@code length} argument is 0, then this method returns
     * the {@code array} reference unchanged (except if empty). Otherwise this method
     * creates a new array. In every cases, the given array is never modified.
     *
     * @param array   Array from which to remove elements.
     * @param first   Index of the first element to remove from the given {@code array}.
     * @param length  Number of elements to remove.
     * @return        Array with the same elements than the given {@code array} except for the
     *                removed elements, or {@code array} if {@code length} is 0.
     */
    public static byte[] remove(final byte[] array, final int first, final int length) {
        return (first == 0 && length == array.length) ? EMPTY_BYTE : doRemove(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array except for
     * the given range. If the {@code length} argument is 0, then this method returns
     * the {@code array} reference unchanged (except if empty). Otherwise this method
     * creates a new array. In every cases, the given array is never modified.
     *
     * @param array   Array from which to remove elements.
     * @param first   Index of the first element to remove from the given {@code array}.
     * @param length  Number of elements to remove.
     * @return        Array with the same elements than the given {@code array} except for the
     *                removed elements, or {@code array} if {@code length} is 0.
     */
    public static char[] remove(final char[] array, final int first, final int length) {
        return (first == 0 && length == array.length) ? EMPTY_CHAR : doRemove(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array except for
     * the given range. If the {@code length} argument is 0, then this method returns
     * the {@code array} reference unchanged (except if empty). Otherwise this method
     * creates a new array. In every cases, the given array is never modified.
     *
     * @param array   Array from which to remove elements.
     * @param first   Index of the first element to remove from the given {@code array}.
     * @param length  Number of elements to remove.
     * @return        Array with the same elements than the given {@code array} except for the
     *                removed elements, or {@code array} if {@code length} is 0.
     */
    public static boolean[] remove(final boolean[] array, final int first, final int length) {
        return (first == 0 && length == array.length) ? EMPTY_BOOLEAN : doRemove(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with additional
     * "spaces" in the given range. These "spaces" will be made up of {@code null} elements.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code array}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given array is never modified.
     *
     * @param <E>     The type of array elements.
     * @param array   Array in which to insert spaces.
     * @param first   Index where the first space should be inserted. All {@code array} elements
     *                having an index equal to or higher than {@code index} will be moved forward.
     * @param length  Number of spaces to insert.
     * @return        Array containing the {@code array} elements with the additional space
     *                inserted, or {@code array} if {@code length} is 0.
     */
    private static <E> E doInsert(final E array, final int first, final int length) {
        if (length == 0) {
            return array;
        }
        final int arrayLength = Array.getLength(array);
        @SuppressWarnings("unchecked")
        final E newArray = (E) Array.newInstance(array.getClass().getComponentType(), arrayLength + length);
        System.arraycopy(array, 0,     newArray, 0,            first            );
        System.arraycopy(array, first, newArray, first+length, arrayLength-first);
        return newArray;
    }

    /**
     * Returns an array containing the same elements than the given array, with additional
     * "spaces" in the given range. These "spaces" will be made up of {@code null} elements.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code array}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given array is never modified.
     *
     * @param <E>     The type of array elements.
     * @param array   Array in which to insert spaces.
     * @param first   Index where the first space should be inserted. All {@code array} elements
     *                having an index equal to or higher than {@code index} will be moved forward.
     * @param length  Number of spaces to insert.
     * @return        Array containing the {@code array} elements with the additional space
     *                inserted, or {@code array} if {@code length} is 0.
     */
    public static <E> E[] insert(final E[] array, final int first, final int length) {
        return doInsert(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with additional
     * "spaces" in the given range. These "spaces" will be made up of elements initialized
     * to zero.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code array}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given array is never modified.
     *
     * @param array   Array in which to insert spaces.
     * @param first   Index where the first space should be inserted. All {@code array} elements
     *                having an index equal to or higher than {@code index} will be moved forward.
     * @param length  Number of spaces to insert.
     * @return        Array containing the {@code array} elements with the additional space
     *                inserted, or {@code array} if {@code length} is 0.
     */
    public static double[] insert(final double[] array, final int first, final int length) {
        return doInsert(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with additional
     * "spaces" in the given range. These "spaces" will be made up of elements initialized
     * to zero.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code array}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given array is never modified.
     *
     * @param array   Array in which to insert spaces.
     * @param first   Index where the first space should be inserted. All {@code array} elements
     *                having an index equal to or higher than {@code index} will be moved forward.
     * @param length  Number of spaces to insert.
     * @return        Array containing the {@code array} elements with the additional space
     *                inserted, or {@code array} if {@code length} is 0.
     */
    public static float[] insert(final float[] array, final int first, final int length) {
        return doInsert(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with additional
     * "spaces" in the given range. These "spaces" will be made up of elements initialized
     * to zero.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code array}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given array is never modified.
     *
     * @param array   Array in which to insert spaces.
     * @param first   Index where the first space should be inserted. All {@code array} elements
     *                having an index equal to or higher than {@code index} will be moved forward.
     * @param length  Number of spaces to insert.
     * @return        Array containing the {@code array} elements with the additional space
     *                inserted, or {@code array} if {@code length} is 0.
     */
    public static long[] insert(final long[] array, final int first, final int length) {
        return doInsert(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with additional
     * "spaces" in the given range. These "spaces" will be made up of elements initialized
     * to zero.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code array}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given array is never modified.
     *
     * @param array   Array in which to insert spaces.
     * @param first   Index where the first space should be inserted. All {@code array} elements
     *                having an index equal to or higher than {@code index} will be moved forward.
     * @param length  Number of spaces to insert.
     * @return        Array containing the {@code array} elements with the additional space
     *                inserted, or {@code array} if {@code length} is 0.
     */
    public static int[] insert(final int[] array, final int first, final int length) {
        return doInsert(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with additional
     * "spaces" in the given range. These "spaces" will be made up of elements initialized
     * to zero.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code array}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given array is never modified.
     *
     * @param array   Array in which to insert spaces.
     * @param first   Index where the first space should be inserted. All {@code array} elements
     *                having an index equal to or higher than {@code index} will be moved forward.
     * @param length  Number of spaces to insert.
     * @return        Array containing the {@code array} elements with the additional space
     *                inserted, or {@code array} if {@code length} is 0.
     */
    public static short[] insert(final short[] array, final int first, final int length) {
        return doInsert(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with additional
     * "spaces" in the given range. These "spaces" will be made up of elements initialized
     * to zero.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code array}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given array is never modified.
     *
     * @param array   Array in which to insert spaces.
     * @param first   Index where the first space should be inserted. All {@code array} elements
     *                having an index equal to or higher than {@code index} will be moved forward.
     * @param length  Number of spaces to insert.
     * @return        Array containing the {@code array} elements with the additional space
     *                inserted, or {@code array} if {@code length} is 0.
     */
    public static byte[] insert(final byte[] array, final int first, final int length) {
        return doInsert(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with additional
     * "spaces" in the given range. These "spaces" will be made up of elements initialized
     * to zero.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code array}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given array is never modified.
     *
     * @param array   Array in which to insert spaces.
     * @param first   Index where the first space should be inserted. All {@code array} elements
     *                having an index equal to or higher than {@code index} will be moved forward.
     * @param length  Number of spaces to insert.
     * @return        Array containing the {@code array} elements with the additional space
     *                inserted, or {@code array} if {@code length} is 0.
     */
    public static char[] insert(final char[] array, final int first, final int length) {
        return doInsert(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with additional
     * "spaces" in the given range. These "spaces" will be made up of elements initialized
     * to {@code false}.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code array}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given array is never modified.
     *
     * @param array   Array in which to insert spaces.
     * @param first   Index where the first space should be inserted. All {@code array} elements
     *                having an index equal to or higher than {@code index} will be moved forward.
     * @param length  Number of spaces to insert.
     * @return        Array containing the {@code array} elements with the additional space
     *                inserted, or {@code array} if {@code length} is 0.
     */
    public static boolean[] insert(final boolean[] array, final int first, final int length) {
        return doInsert(array, first, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with the content
     * of an other array inserted at the given index.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code dst}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given arrays are never modified.
     *
     * @param <E>     The type of array elements.
     * @param src     Array to entirely or partially insert into {@code dst}.
     * @param srcOff  Index of the first element of {@code src} to insert into {@code dst}.
     * @param dst     Array in which to insert {@code src} data.
     * @param dstOff  Index of the first element in {@code dst} where to insert {@code src} data.
     *                All elements of {@code dst} whose index is equal to or greater than
     *                {@code dstOff} will be moved forward.
     * @param length  Number of {@code src} elements to insert.
     * @return        Array which contains the merge of {@code src} and {@code dst}. This method
     *                returns directly {@code dst} when {@code length} is zero, but never return
     *                {@code src}.
     */
    private static <E> E doInsert(final E src, final int srcOff,
                                  final E dst, final int dstOff, final int length)
    {
        if (length == 0) {
            return dst;
        }
        final int dstLength = Array.getLength(dst);
        @SuppressWarnings("unchecked")
        final E newArray = (E) Array.newInstance(dst.getClass().getComponentType(), dstLength+length);
        System.arraycopy(dst, 0,      newArray, 0,             dstOff          );
        System.arraycopy(src, srcOff, newArray, dstOff,        length          );
        System.arraycopy(dst, dstOff, newArray, dstOff+length, dstLength-dstOff);
        return newArray;
    }

    /**
     * Returns an array containing the same elements than the given array, with the content
     * of an other array inserted at the given index.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code dst}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given arrays are never modified.
     *
     * @param <E>     The type of array elements.
     * @param src     Array to entirely or partially insert into {@code dst}.
     * @param srcOff  Index of the first element of {@code src} to insert into {@code dst}.
     * @param dst     Array in which to insert {@code src} data.
     * @param dstOff  Index of the first element in {@code dst} where to insert {@code src} data.
     *                All elements of {@code dst} whose index is equal to or greater than
     *                {@code dstOff} will be moved forward.
     * @param length  Number of {@code src} elements to insert.
     * @return        Array which contains the merge of {@code src} and {@code dst}. This method
     *                returns directly {@code dst} when {@code length} is zero, but never return
     *                {@code src}.
     */
    public static <E> E[] insert(final E[] src, final int srcOff,
                                 final E[] dst, final int dstOff, final int length)
    {
        return doInsert(src, srcOff, dst, dstOff, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with the content
     * of an other array inserted at the given index.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code dst}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given arrays are never modified.
     *
     * @param src     Array to entirely or partially insert into {@code dst}.
     * @param srcOff  Index of the first element of {@code src} to insert into {@code dst}.
     * @param dst     Array in which to insert {@code src} data.
     * @param dstOff  Index of the first element in {@code dst} where to insert {@code src} data.
     *                All elements of {@code dst} whose index is equal to or greater than
     *                {@code dstOff} will be moved forward.
     * @param length  Number of {@code src} elements to insert.
     * @return        Array which contains the merge of {@code src} and {@code dst}. This method
     *                returns directly {@code dst} when {@code length} is zero, but never return
     *                {@code src}.
     */
    public static double[] insert(final double[] src, final int srcOff,
                                  final double[] dst, final int dstOff, final int length)
    {
        return doInsert(src, srcOff, dst, dstOff, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with the content
     * of an other array inserted at the given index.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code dst}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given arrays are never modified.
     *
     * @param src     Array to entirely or partially insert into {@code dst}.
     * @param srcOff  Index of the first element of {@code src} to insert into {@code dst}.
     * @param dst     Array in which to insert {@code src} data.
     * @param dstOff  Index of the first element in {@code dst} where to insert {@code src} data.
     *                All elements of {@code dst} whose index is equal to or greater than
     *                {@code dstOff} will be moved forward.
     * @param length  Number of {@code src} elements to insert.
     * @return        Array which contains the merge of {@code src} and {@code dst}. This method
     *                returns directly {@code dst} when {@code length} is zero, but never return
     *                {@code src}.
     */
    public static float[] insert(final float[] src, final int srcOff,
                                 final float[] dst, final int dstOff, final int length)
    {
        return doInsert(src, srcOff, dst, dstOff, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with the content
     * of an other array inserted at the given index.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code dst}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given arrays are never modified.
     *
     * @param src     Array to entirely or partially insert into {@code dst}.
     * @param srcOff  Index of the first element of {@code src} to insert into {@code dst}.
     * @param dst     Array in which to insert {@code src} data.
     * @param dstOff  Index of the first element in {@code dst} where to insert {@code src} data.
     *                All elements of {@code dst} whose index is equal to or greater than
     *                {@code dstOff} will be moved forward.
     * @param length  Number of {@code src} elements to insert.
     * @return        Array which contains the merge of {@code src} and {@code dst}. This method
     *                returns directly {@code dst} when {@code length} is zero, but never return
     *                {@code src}.
     */
    public static long[] insert(final long[] src, final int srcOff,
                                final long[] dst, final int dstOff, final int length)
    {
        return doInsert(src, srcOff, dst, dstOff, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with the content
     * of an other array inserted at the given index.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code dst}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given arrays are never modified.
     *
     * @param src     Array to entirely or partially insert into {@code dst}.
     * @param srcOff  Index of the first element of {@code src} to insert into {@code dst}.
     * @param dst     Array in which to insert {@code src} data.
     * @param dstOff  Index of the first element in {@code dst} where to insert {@code src} data.
     *                All elements of {@code dst} whose index is equal to or greater than
     *                {@code dstOff} will be moved forward.
     * @param length  Number of {@code src} elements to insert.
     * @return        Array which contains the merge of {@code src} and {@code dst}. This method
     *                returns directly {@code dst} when {@code length} is zero, but never return
     *                {@code src}.
     */
    public static int[] insert(final int[] src, final int srcOff,
                               final int[] dst, final int dstOff, final int length)
    {
        return doInsert(src, srcOff, dst, dstOff, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with the content
     * of an other array inserted at the given index.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code dst}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given arrays are never modified.
     *
     * @param src     Array to entirely or partially insert into {@code dst}.
     * @param srcOff  Index of the first element of {@code src} to insert into {@code dst}.
     * @param dst     Array in which to insert {@code src} data.
     * @param dstOff  Index of the first element in {@code dst} where to insert {@code src} data.
     *                All elements of {@code dst} whose index is equal to or greater than
     *                {@code dstOff} will be moved forward.
     * @param length  Number of {@code src} elements to insert.
     * @return        Array which contains the merge of {@code src} and {@code dst}. This method
     *                returns directly {@code dst} when {@code length} is zero, but never return
     *                {@code src}.
     */
    public static short[] insert(final short[] src, final int srcOff,
                                 final short[] dst, final int dstOff, final int length)
    {
        return doInsert(src, srcOff, dst, dstOff, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with the content
     * of an other array inserted at the given index.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code dst}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given arrays are never modified.
     *
     * @param src     Array to entirely or partially insert into {@code dst}.
     * @param srcOff  Index of the first element of {@code src} to insert into {@code dst}.
     * @param dst     Array in which to insert {@code src} data.
     * @param dstOff  Index of the first element in {@code dst} where to insert {@code src} data.
     *                All elements of {@code dst} whose index is equal to or greater than
     *                {@code dstOff} will be moved forward.
     * @param length  Number of {@code src} elements to insert.
     * @return        Array which contains the merge of {@code src} and {@code dst}. This method
     *                returns directly {@code dst} when {@code length} is zero, but never return
     *                {@code src}.
     */
    public static byte[] insert(final byte[] src, final int srcOff,
                                final byte[] dst, final int dstOff, final int length)
    {
        return doInsert(src, srcOff, dst, dstOff, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with the content
     * of an other array inserted at the given index.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code dst}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given arrays are never modified.
     *
     * @param src     Array to entirely or partially insert into {@code dst}.
     * @param srcOff  Index of the first element of {@code src} to insert into {@code dst}.
     * @param dst     Array in which to insert {@code src} data.
     * @param dstOff  Index of the first element in {@code dst} where to insert {@code src} data.
     *                All elements of {@code dst} whose index is equal to or greater than
     *                {@code dstOff} will be moved forward.
     * @param length  Number of {@code src} elements to insert.
     * @return        Array which contains the merge of {@code src} and {@code dst}. This method
     *                returns directly {@code dst} when {@code length} is zero, but never return
     *                {@code src}.
     */
    public static char[] insert(final char[] src, final int srcOff,
                                final char[] dst, final int dstOff, final int length)
    {
        return doInsert(src, srcOff, dst, dstOff, length);
    }

    /**
     * Returns an array containing the same elements than the given array, with the content
     * of an other array inserted at the given index.
     * <p>
     * If the {@code length} argument is 0, then this method returns the {@code dst}
     * reference unchanged. Otherwise this method creates a new array. In every cases,
     * the given arrays are never modified.
     *
     * @param src     Array to entirely or partially insert into {@code dst}.
     * @param srcOff  Index of the first element of {@code src} to insert into {@code dst}.
     * @param dst     Array in which to insert {@code src} data.
     * @param dstOff  Index of the first element in {@code dst} where to insert {@code src} data.
     *                All elements of {@code dst} whose index is equal to or greater than
     *                {@code dstOff} will be moved forward.
     * @param length  Number of {@code src} elements to insert.
     * @return        Array which contains the merge of {@code src} and {@code dst}. This method
     *                returns directly {@code dst} when {@code length} is zero, but never return
     *                {@code src}.
     */
    public static boolean[] insert(final boolean[] src, final int srcOff,
                                   final boolean[] dst, final int dstOff, final int length)
    {
        return doInsert(src, srcOff, dst, dstOff, length);
    }

    /**
     * Returns a copy of the given array with a single element appended at the end.
     * This method should be invoked only on rare occasions. If many elements are to
     * be added, use {@link java.util.ArrayList} instead.
     *
     * @param <T>      The type of elements in the array.
     * @param array    The array to copy with a new element. The original array will not be modified.
     * @param element  The element to add (can be null).
     * @return         A copy of the given array with the given element appended at the end.
     *
     * @see #concatenate(Object[][])
     *
     * @since 3.20
     */
    public static <T> T[] append(final T[] array, final T element) {
        final T[] copy = Arrays.copyOf(array, array.length + 1);
        copy[array.length] = element;
        return copy;
    }

    /**
     * Reverses the order of elements in the given array.
     * This operation is performed in-place.
     *
     * @param entries The array in which to reverse the order of elements.
     *
     * @since 3.11
     */
    public static void reverse(final Object[] entries) {
        for (int i=entries.length/2; --i >= 0;) {
            final int j = (entries.length - 1) - i;
            final Object tmp = entries[i];
            entries[i] = entries[j];
            entries[j] = tmp;
        }
    }

    /**
     * Returns a copy of the given array where each value has been casted to the {@code float} type.
     *
     * @param  data The array to copy, or {@code null}.
     * @return A copy of the given array with values casted to the {@code float} type, or
     *         {@code null} if the given array was null.
     */
    public static float[] copyAsFloats(final double[] data) {
        if (data == null) return null;
        final float[] result = new float[data.length];
        for (int i=0; i<data.length; i++) {
            result[i] = (float) data[i];
        }
        return result;
    }

    /**
     * Returns a copy of the given array where each value has been
     * {@linkplain Math#round(double) rounded} to the {@code int} type.
     *
     * @param  data The array to copy, or {@code null}.
     * @return A copy of the given array with values rounded to the {@code int} type, or
     *         {@code null} if the given array was null.
     */
    public static int[] copyAsInts(final double[] data) {
        if (data == null) return null;
        final int[] result = new int[data.length];
        for (int i=0; i<data.length; i++) {
            result[i] = (int) Math.round(data[i]);
        }
        return result;
    }

    /**
     * Returns {@code true} if all elements in the specified array are in increasing order.
     * This method is useful in assertions.
     *
     * @param <E>         The type of array elements.
     * @param array       The array to test for order.
     * @param comparator  The comparator to use for comparing order.
     * @param strict      {@code true} if elements should be strictly sorted (i.e. equal
     *                    elements are not allowed}, or {@code false} otherwise.
     * @return {@code true} if all elements in the given array are sorted in increasing order.
     */
    public static <E> boolean isSorted(final E[] array, final Comparator<E> comparator, final boolean strict) {
        for (int i=1; i<array.length; i++) {
            final int c = comparator.compare(array[i], array[i-1]);
            if (strict ? c <= 0 : c < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if all elements in the specified array are in increasing order.
     * Since {@code NaN} values are unordered, they may appears anywhere in the array; they
     * will be ignored. This method is useful in assertions.
     *
     * @param array  The array to test for order.
     * @param strict {@code true} if elements should be strictly sorted (i.e. equal elements
     *               are not allowed}, or {@code false} otherwise.
     * @return {@code true} if all elements in the given array are sorted in increasing order.
     */
    public static boolean isSorted(final double[] array, final boolean strict) {
        int previous = 0;
        for (int i=1; i<array.length; i++) {
            final double e = array[i];
            final double p = array[previous];
            if (strict ? e <= p : e < p) {
                return false;
            }
            if (!Double.isNaN(e)) {
                previous = i;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if all elements in the specified array are in increasing order.
     * Since {@code NaN} values are unordered, they may appears anywhere in the array; they
     * will be ignored. This method is useful in assertions.
     *
     * @param array  The array to test for order.
     * @param strict {@code true} if elements should be strictly sorted (i.e. equal elements
     *               are not allowed}, or {@code false} otherwise.
     * @return {@code true} if all elements in the given array are sorted in increasing order.
     */
    public static boolean isSorted(final float[] array, final boolean strict) {
        int previous = 0;
        for (int i=1; i<array.length; i++) {
            final float e = array[i];
            final float p = array[previous];
            if (strict ? e <= p : e < p) {
                return false;
            }
            if (!Float.isNaN(e)) {
                previous = i;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if all elements in the specified array are in increasing order.
     * This method is useful in assertions.
     *
     * @param array  The array to test for order.
     * @param strict {@code true} if elements should be strictly sorted (i.e. equal elements
     *               are not allowed}, or {@code false} otherwise.
     * @return {@code true} if all elements in the given array are sorted in increasing order.
     */
    public static boolean isSorted(final long[] array, final boolean strict) {
        for (int i=1; i<array.length; i++) {
            final long e = array[i];
            final long p = array[i-1];
            if (strict ? e <= p : e < p) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if all elements in the specified array are in increasing order.
     * This method is useful in assertions.
     *
     * @param array  The array to test for order.
     * @param strict {@code true} if elements should be strictly sorted (i.e. equal elements
     *               are not allowed}, or {@code false} otherwise.
     * @return {@code true} if all elements in the given array are sorted in increasing order.
     */
    public static boolean isSorted(final int[] array, final boolean strict) {
        for (int i=1; i<array.length; i++) {
            final int e = array[i];
            final int p = array[i-1];
            if (strict ? e <= p : e < p) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if all elements in the specified array are in increasing order.
     * This method is useful in assertions.
     *
     * @param array  The array to test for order.
     * @param strict {@code true} if elements should be strictly sorted (i.e. equal elements
     *               are not allowed}, or {@code false} otherwise.
     * @return {@code true} if all elements in the given array are sorted in increasing order.
     */
    public static boolean isSorted(final short[] array, final boolean strict) {
        for (int i=1; i<array.length; i++) {
            final short e = array[i];
            final short p = array[i-1];
            if (strict ? e <= p : e < p) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if all elements in the specified array are in increasing order.
     * This method is useful in assertions.
     *
     * @param array  The array to test for order.
     * @param strict {@code true} if elements should be strictly sorted (i.e. equal elements
     *               are not allowed}, or {@code false} otherwise.
     * @return {@code true} if all elements in the given array are sorted in increasing order.
     */
    public static boolean isSorted(final byte[] array, final boolean strict) {
        for (int i=1; i<array.length; i++) {
            final byte e = array[i];
            final byte p = array[i-1];
            if (strict ? e <= p : e < p) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if all elements in the specified array are in increasing order.
     * This method is useful in assertions.
     *
     * @param array  The array to test for order.
     * @param strict {@code true} if elements should be strictly sorted (i.e. equal elements
     *               are not allowed}, or {@code false} otherwise.
     * @return {@code true} if all elements in the given array are sorted in increasing order.
     */
    public static boolean isSorted(final char[] array, final boolean strict) {
        for (int i=1; i<array.length; i++) {
            final char e = array[i];
            final char p = array[i-1];
            if (strict ? e <= p : e < p) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if all values in the specified array are equal to the specified
     * value, which may be {@link Double#NaN}.
     *
     * @param array The array to check.
     * @param value The expected value.
     * @return {@code true} if all elements in the given array are equal to the given value.
     */
    public static boolean allEquals(final double[] array, final double value) {
        if (Double.isNaN(value)) {
            for (int i=0; i<array.length; i++) {
                if (!Double.isNaN(array[i])) {
                    return false;
                }
            }
        } else {
            for (int i=0; i<array.length; i++) {
                if (array[i] != value) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if all values in the specified array are equal to the specified
     * value, which may be {@link Float#NaN}.
     *
     * @param array The array to check.
     * @param value The expected value.
     * @return {@code true} if all elements in the given array are equal to the given value.
     */
    public static boolean allEquals(final float[] array, final float value) {
        if (Float.isNaN(value)) {
            for (int i=0; i<array.length; i++) {
                if (!Float.isNaN(array[i])) {
                    return false;
                }
            }
        } else {
            for (int i=0; i<array.length; i++) {
                if (array[i] != value) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the specified array contains at least one
     * {@link Double#NaN NaN} value.
     *
     * @param array The array to check.
     * @return {@code true} if the given array contains at least one NaN value.
     */
    public static boolean hasNaN(final double[] array) {
        for (int i=0; i<array.length; i++) {
            if (Double.isNaN(array[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the specified array contains at least one
     * {@link Float#NaN NaN} value.
     *
     * @param array The array to check.
     * @return {@code true} if the given array contains at least one NaN value.
     */
    public static boolean hasNaN(final float[] array) {
        for (int i=0; i<array.length; i++) {
            if (Float.isNaN(array[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the specified array contains the specified value, ignoring case.
     * This method should be used only for very small arrays.
     *
     * @param  array The array to search in. May be {@code null}.
     * @param  value The value to search.
     * @return {@code true} if the array is non-null and contains the given value,
     *         or {@code false} otherwise.
     */
    public static boolean containsIgnoreCase(final String[] array, final String value) {
        if (array != null) {
            for (final String element : array) {
                if (value.equalsIgnoreCase(element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the specified array contains the specified reference.
     * The comparisons are performed using the {@code ==} operator.
     * <p>
     * This method should be used only for very small arrays, or for searches to be performed
     * only once, because it performs a linear search. If more than one search need to be done
     * on the same array, consider using {@link java.util.IdentityHashMap} instead.
     *
     * @param  array The array to search in. May be {@code null} and may contains null elements.
     * @param  value The value to search. May be {@code null}.
     * @return {@code true} if the array is non-null and contains the value (which may be null),
     *         or {@code false} otherwise.
     *
     * @since 3.17
     */
    public static boolean containsIdentity(final Object[] array, final Object value) {
        if (array != null) {
            for (final Object element : array) {
                if (element == value) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the specified array contains the specified value.
     * The comparisons are performed using the {@link Object#equals(Object)} method.
     * <p>
     * This method should be used only for very small arrays, or for searches to be performed
     * only once, because it performs a linear search. If more than one search need to be done
     * on the same array, consider using {@link java.util.HashSet} instead.
     *
     * @param  array The array to search in. May be {@code null} and may contains null elements.
     * @param  value The value to search. May be {@code null}.
     * @return {@code true} if the array is non-null and contains the value (which may be null),
     *         or {@code false} otherwise.
     */
    public static boolean contains(final Object[] array, final Object value) {
        if (array != null) {
            for (final Object element : array) {
                if (Utilities.equals(element, value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if at least one element in the first array is {@linkplain Object#equals
     * equals} to an element in the second array. The element doesn't need to be at the same index
     * in both array.
     * <p>
     * This method should be used only for very small arrays since it may be very slow. If the
     * arrays are large or if an array will be involved in more than one search, consider using
     * {@link java.util.HashSet} instead.
     *
     * @param array1 The first array.
     * @param array2 The second array.
     * @return {@code true} if both array have at least one element in common.
     */
    public static boolean intersects(final Object[] array1, final Object[] array2) {
        for (final Object element : array1) {
            if (contains(array2, element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the concatenation of all given arrays. This method performs the following checks:
     * <p>
     * <ul>
     *   <li>If the {@code arrays} argument is {@code null} or contains only {@code null}
     *       elements, then this method returns {@code null}.</li>
     *   <li>Otherwise if the {@code arrays} argument contains exactly one non-null array with
     *       a length greater than zero, then that array is returned. It is not copied.</li>
     *   <li>Otherwise a new array with a length equals to the sum of the length of every
     *       non-null arrays is created, and the content of non-null arrays are appended
     *       in the new array in declaration order.</li>
     * </ul>
     *
     * @param <T>    The type of arrays.
     * @param arrays The arrays to concatenate, or {@code null}.
     * @return       The concatenation of all non-null arrays (may be a direct reference to one
     *               of the given array if it can be returned with no change), or {@code null}.
     *
     * @see #append(Object[], Object)
     *
     * @since 3.07
     */
    public static <T> T[] concatenate(final T[]... arrays) {
        T[] result = null;
        if (arrays != null) {
            int length = 0;
            for (T[] array : arrays) {
                if (array != null) {
                    length += array.length;
                }
            }
            int offset = 0;
            for (T[] array : arrays) {
                if (array != null) {
                    if (result == null) {
                        if (array.length == length) {
                            return array;
                        }
                        result = Arrays.copyOf(array, length);
                    } else {
                        System.arraycopy(array, 0, result, offset, array.length);
                    }
                    offset += array.length;
                }
            }
        }
        return result;
    }

    /**
     * Returns the union of two sorted arrays. The input arrays shall be sorted in strictly
     * increasing order (for performance raison, this is verified only if assertions are enabled).
     * The output array is the union of the input arrays without duplicated values, with elements
     * in strictly increasing order.
     *
     * @param  array1 The first array.
     * @param  array2 The second array.
     * @return The union of the given array without duplicated values.
     *
     * @since 3.04
     */
    public static int[] union(final int[] array1, final int[] array2) {
        assert isSorted(array1, true);
        assert isSorted(array2, true);
        int[] union  = new int[array1.length + array2.length];
        int nu=0;
        for (int ix=0, iy=0;;) {
            if (ix == array1.length) {
                final int no = array2.length - iy;
                System.arraycopy(array2, iy, union, nu, no);
                nu += no;
                break;
            }
            if (iy == array2.length) {
                final int no = array1.length - ix;
                System.arraycopy(array1, ix, union, nu, no);
                nu += no;
                break;
            }
            final int sx = array1[ix];
            final int sy = array2[iy];
            final int s;
            if (sx <= sy) {
                s = sx;
                ix++;
                if (sx == sy) {
                    iy++;
                }
            } else {
                s = sy;
                iy++;
            }
            union[nu++] = s;
        }
        union = resize(union, nu);
        assert isSorted(union, true);
        return union;
    }
}

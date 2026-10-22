/**
 * Copyright (C) 2026 Geomatys and A5 contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.dggs.a5.internal.utils;

import java.util.Arrays;

/**
 * Growable array of {@code int}, replacing the upstream growable {@code number[]}
 * of integers, mainly the flat triple-space cell arrays
 * {@code (originId, quintant, x, y, z, originId, quintant, ...)} of the traversal
 * and regions modules.
 *
 * <p>Not an upstream module. Not thread-safe (instances are local to one call).</p>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class IntList {

    private int[] data;
    private int size;

    /**
     * Creates an empty list.
     */
    public IntList() {
        this(16);
    }

    /**
     * Creates an empty list with the given initial capacity.
     *
     * @param capacity initial capacity
     */
    public IntList(int capacity) {
        data = new int[Math.max(capacity, 4)];
    }

    /**
     * Creates a list holding a copy of the given values.
     *
     * @param values initial values
     */
    public static IntList of(int... values) {
        final IntList list = new IntList(values.length);
        System.arraycopy(values, 0, list.data, 0, values.length);
        list.size = values.length;
        return list;
    }

    /**
     * @return number of values (TS {@code .length})
     */
    public int length() {
        return size;
    }

    /**
     * @param index value index, in [0, length)
     * @return value at index (TS {@code list[index]})
     */
    public int get(int index) {
        if (index >= size) throw new IndexOutOfBoundsException(index);
        return data[index];
    }

    /**
     * @param index value index, in [0, length)
     * @param value new value (TS {@code list[index] = value})
     */
    public void set(int index, int value) {
        if (index >= size) throw new IndexOutOfBoundsException(index);
        data[index] = value;
    }

    private void ensureCapacity(int capacity) {
        if (capacity > data.length) {
            data = Arrays.copyOf(data, Math.max(capacity, data.length * 2));
        }
    }

    /**
     * Appends one value (TS {@code list.push(v)}).
     *
     * @param v value
     */
    public void push(int v) {
        ensureCapacity(size + 1);
        data[size++] = v;
    }

    /**
     * Appends one triple-space cell (TS {@code list.push(originId, quintant, x, y, z)}).
     */
    public void push(int originId, int quintant, int x, int y, int z) {
        ensureCapacity(size + 5);
        final int[] d = data;
        int s = size;
        d[s++] = originId;
        d[s++] = quintant;
        d[s++] = x;
        d[s++] = y;
        d[s++] = z;
        size = s;
    }

    /**
     * Appends all values of another list.
     *
     * @param other values to append
     */
    public void pushAll(IntList other) {
        ensureCapacity(size + other.size);
        System.arraycopy(other.data, 0, data, size, other.size);
        size += other.size;
    }

    /**
     * Removes all values (TS {@code list.length = 0}).
     */
    public void clear() {
        size = 0;
    }

    /**
     * Truncates the list (TS {@code list.length = n}, truncation only).
     *
     * @param length new length, not greater than the current one
     */
    public void setLength(int length) {
        if (length < 0 || length > size) throw new IllegalArgumentException("Invalid length " + length);
        size = length;
    }

    /**
     * @return a copy of this list (TS {@code list.slice()})
     */
    public IntList slice() {
        final IntList copy = new IntList(size);
        System.arraycopy(data, 0, copy.data, 0, size);
        copy.size = size;
        return copy;
    }

    /**
     * @return values as a new array
     */
    public int[] toArray() {
        return Arrays.copyOf(data, size);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IntList)) return false;
        final IntList other = (IntList) obj;
        return Arrays.equals(data, 0, size, other.data, 0, other.size);
    }

    @Override
    public int hashCode() {
        int h = 1;
        for (int i = 0; i < size; i++) h = 31 * h + data[i];
        return h;
    }

    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }
}

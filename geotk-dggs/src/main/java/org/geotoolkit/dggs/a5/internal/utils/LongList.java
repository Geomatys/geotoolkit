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
 * Growable array of {@code long}, replacing the upstream growable {@code bigint[]}
 * of cell ids (values are raw unsigned 64 bits patterns).
 *
 * <p>Not an upstream module. Not thread-safe (instances are local to one call).</p>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class LongList {

    private long[] data;
    private int size;

    /**
     * Creates an empty list.
     */
    public LongList() {
        this(16);
    }

    /**
     * Creates an empty list with the given initial capacity.
     *
     * @param capacity initial capacity
     */
    public LongList(int capacity) {
        data = new long[Math.max(capacity, 4)];
    }

    /**
     * Creates a list holding a copy of the given values.
     *
     * @param values initial values
     */
    public static LongList of(long... values) {
        final LongList list = new LongList(values.length);
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
    public long get(int index) {
        if (index >= size) throw new IndexOutOfBoundsException(index);
        return data[index];
    }

    /**
     * @param index value index, in [0, length)
     * @param value new value (TS {@code list[index] = value})
     */
    public void set(int index, long value) {
        if (index >= size) throw new IndexOutOfBoundsException(index);
        data[index] = value;
    }

    /**
     * Appends one value (TS {@code list.push(v)}).
     *
     * @param v value
     */
    public void push(long v) {
        if (size == data.length) data = Arrays.copyOf(data, data.length * 2);
        data[size++] = v;
    }

    /**
     * Appends all values of an array.
     *
     * @param values values to append
     */
    public void pushAll(long[] values) {
        if (size + values.length > data.length) {
            data = Arrays.copyOf(data, Math.max(size + values.length, data.length * 2));
        }
        System.arraycopy(values, 0, data, size, values.length);
        size += values.length;
    }

    /**
     * Removes all values (TS {@code list.length = 0}).
     */
    public void clear() {
        size = 0;
    }

    /**
     * @return values as a new array (trimmed to the length)
     */
    public long[] toArray() {
        return Arrays.copyOf(data, size);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LongList)) return false;
        final LongList other = (LongList) obj;
        return Arrays.equals(data, 0, size, other.data, 0, other.size);
    }

    @Override
    public int hashCode() {
        int h = 1;
        for (int i = 0; i < size; i++) h = 31 * h + Long.hashCode(data[i]);
        return h;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(Long.toUnsignedString(data[i], 16));
        }
        return sb.append(']').toString();
    }
}

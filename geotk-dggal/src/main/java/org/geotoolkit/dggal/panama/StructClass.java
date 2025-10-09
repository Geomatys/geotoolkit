/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.dggal.panama;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.StructLayout;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class StructClass {

    protected final MemorySegment struct;

    protected StructClass(MemorySegment struct) {
        this.struct = struct;
        if (struct.address() == 0x0l) {
            throw new NullPointerException("Object pointer is null : 0x0");
        }
    }

    protected StructClass(SegmentAllocator allocator) {
        this.struct = allocator.allocate(getLayout());
    }

    protected abstract MemoryLayout getLayout();

    @Override
    public String toString() {
        final Class<? extends StructClass> clazz = getClass();
        final String name = clazz.getSimpleName();
        final List<String> attributes = new ArrayList<>();

        final MemoryLayout layout = getLayout();
        if (layout instanceof StructLayout sl) {
            for(MemoryLayout ml : sl.memberLayouts()) {
                final String attName = ml.name().orElse(null);
                if (attName != null) {
                    String result;
                    try {
                        Object value = clazz.getMethod("get" + toCamelCase(attName)).invoke(this);
                        if (value != null && value.getClass().isArray()) {
                            //pick the first 10 values
                            final int length = Array.getLength(value);
                            final StringBuilder sb = new StringBuilder("[");
                            for (int i = 0; i < 10 && i < length; i++) {
                                sb.append(Array.get(value, i));
                                sb.append(',');
                            }
                            sb.append("...]");
                            value = sb.toString();
                        }
                        result = String.valueOf(value);
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException ex) {
                        result = "[Failed to get value]";
                    }
                    attributes.add(attName + " : " + result);
                }
            }
        }

        return toStringTree(name, attributes);
    }

    private static String toCamelCase(String str) {
        final StringBuilder sb = new StringBuilder();
        for (String s : str.split("_")) {
            if (!s.isEmpty()) {
                sb.append(Character.toUpperCase(s.charAt(0)));
                sb.append(s.substring(1));
            }
        }
        return sb.toString();
    }

    /**
     * Returns a graphical representation of the specified objects. This representation can be
     * printed to the {@linkplain System#out standard output stream} (for example) if it uses
     * a monospaced font and supports unicode.
     *
     * @param  root  The root name of the tree to format.
     * @param  objects The objects to format as root children.
     * @return A string representation of the tree.
     */
    static String toStringTree(String root, final Iterable<?> objects) {
        final StringBuilder sb = new StringBuilder();
        if (root != null) {
            sb.append(root);
        }
        if (objects != null) {
            final Iterator<?> ite = objects.iterator();
            while (ite.hasNext()) {
                sb.append('\n');
                final Object next = ite.next();
                final boolean last = !ite.hasNext();
                sb.append(last ? "\u2514\u2500 " : "\u251C\u2500 ");

                final String[] parts = String.valueOf(next).split("\n");
                sb.append(parts[0]);
                for (int k=1;k<parts.length;k++) {
                    sb.append('\n');
                    sb.append(last ? ' ' : '\u2502');
                    sb.append("  ");
                    sb.append(parts[k]);
                }
            }
        }
        return sb.toString();
    }
}

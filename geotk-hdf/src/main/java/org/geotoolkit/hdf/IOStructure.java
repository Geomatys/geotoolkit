/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
 */
package org.geotoolkit.hdf;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.geotoolkit.hdf.btree.BTreeV1;
import org.geotoolkit.hdf.btree.BTreeV2;
import org.geotoolkit.hdf.btree.BTreeV2InternalNode;
import org.geotoolkit.hdf.btree.BTreeV2LeafNode;
import org.geotoolkit.hdf.heap.FractalHeap;
import org.geotoolkit.hdf.heap.FractalHeapDirectBlock;
import org.geotoolkit.hdf.heap.FractalHeapIndirectBlock;
import org.geotoolkit.hdf.heap.GlobalHeap;
import org.geotoolkit.hdf.heap.LocalHeap;
import org.geotoolkit.hdf.io.HDF5DataInput;
import org.geotoolkit.util.StringUtilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class IOStructure {

    public abstract void read(HDF5DataInput channel) throws IOException;

    @Override
    public String toString() {
        return reflectionToString(this);
    }

    public static IOStructure loadIdentifiedObject(HDF5DataInput channel) throws IOException{
        channel.mark();
        final byte[] signature = channel.readNBytes(4);
        channel.reset();
        final IOStructure structure;
        if (Arrays.equals(FreeSpaceManager.SIGNATURE, signature)) {
            structure = new FreeSpaceManager();
        } else if (Arrays.equals(FreeSpaceSectionList.SIGNATURE, signature)) {
            structure = new FreeSpaceSectionList();
        } else if (Arrays.equals(ObjectHeaderContinuationBlockV2.SIGNATURE, signature)) {
            structure = new ObjectHeaderContinuationBlockV2();
        } else if (Arrays.equals(ObjectHeaderV2.SIGNATURE, signature)) {
            structure = new ObjectHeaderV2();
        } else if (Arrays.equals(SharedMessageRecordList.SIGNATURE, signature)) {
            structure = new SharedMessageRecordList();
        } else if (Arrays.equals(SharedObjectHeaderMessageTable.SIGNATURE, signature)) {
            structure = new SharedObjectHeaderMessageTable();
        } else if (Arrays.equals(SuperBlock.SIGNATURE, signature)) {
            structure = new SuperBlock();
        } else if (Arrays.equals(SymbolTableNode.SIGNATURE, signature)) {
            structure = new SymbolTableNode();
        } else if (Arrays.equals(BTreeV1.SIGNATURE, signature)) {
            structure = new BTreeV1();
        } else if (Arrays.equals(BTreeV2.SIGNATURE, signature)) {
            structure = new BTreeV2();
        } else if (Arrays.equals(BTreeV2InternalNode.SIGNATURE, signature)) {
            structure = new BTreeV2InternalNode();
        } else if (Arrays.equals(BTreeV2LeafNode.SIGNATURE, signature)) {
            structure = new BTreeV2LeafNode();
        } else if (Arrays.equals(FractalHeap.SIGNATURE, signature)) {
            structure = new FractalHeap();
        } else if (Arrays.equals(FractalHeapDirectBlock.SIGNATURE, signature)) {
            structure = new FractalHeapDirectBlock();
        } else if (Arrays.equals(FractalHeapIndirectBlock.SIGNATURE, signature)) {
            structure = new FractalHeapIndirectBlock();
        } else if (Arrays.equals(GlobalHeap.SIGNATURE, signature)) {
            structure = new GlobalHeap();
        } else if (Arrays.equals(LocalHeap.SIGNATURE, signature)) {
            structure = new LocalHeap();
        } else {
            throw new IOException("Unknown structure for signature " + Arrays.toString(signature));
        }

        structure.read(channel);
        return structure;
    }

    public static String reflectionToString(Object object) {
        final List<String> list = new ArrayList<>();
        final List<Field> fields = new ArrayList<>();
        getAllFields(fields, object.getClass());
        for (Field f : fields) {
            if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                f.setAccessible(true);
                String name = f.getName();
                String value = "<null>";
                try {
                    Object v = f.get(object);
                    if (v != null) {
                        if (v instanceof CharSequence) {
                            value = "\"" + v + "\"";
                            value = value.replace('\n', ' ');
                        } else if (v.getClass().isArray()) {
                            final Object[] a = new Object[Array.getLength(v)];
                            for(int i=0;i<a.length;i++) {
                                a[i] = Array.get(v, i);
                            }
                            value = Arrays.toString(a);
                        } else if (v instanceof Iterable ite) {
                            final Iterator iterator = ite.iterator();
                            final List<String> lst = new ArrayList<>();
                            int i = 0;
                            while (iterator.hasNext()) {
                                Object c = iterator.next();
                                lst.add(i + "." + c.toString());
                                i++;
                            }
                            value = StringUtilities.toStringTree("", lst);
                        } else {
                            value = String.valueOf(v);
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    value = "<" + ex.getMessage() + ">";
                }
                list.add(name + "=" + value);
            }
        }

        String name = object.getClass().getSimpleName();
        return StringUtilities.toStringTree(name, list);
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }
}

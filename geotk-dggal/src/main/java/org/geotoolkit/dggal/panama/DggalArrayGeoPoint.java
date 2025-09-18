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

import java.lang.foreign.MemorySegment;
import java.nio.ByteOrder;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DggalArrayGeoPoint implements AutoCloseable {

    private final DGGAL dggal;
    private final MemorySegment pointer;

    public DggalArrayGeoPoint(DGGAL dggal, MemorySegment pointer) {
        if (pointer.address() == 0x0l) {
            throw new NullPointerException("Object pointer is null : 0x0");
        }
        this.dggal = dggal;
        this.pointer = pointer;
    }

    public int getCount() throws Throwable {
        return (int) dggal.DGGAL_Array_GeoPoint_getCount.invokeExact(pointer);
    }

    public double[] toArray() throws Throwable {
        final MemorySegment mem = (MemorySegment) dggal.DGGAL_Array_GeoPoint_getPointer.invokeExact(pointer);
        final double[] dst = new double[getCount()*2];
        mem.reinterpret(dst.length*8).asByteBuffer().order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().get(dst);
        return dst;
    }

    @Override
    public void close() throws Exception {
        try {
            dggal.DGGAL_Array_GeoPoint_delete.invokeExact(pointer);
        } catch (Throwable ex) {
            throw new Exception(ex);
        }
    }

}

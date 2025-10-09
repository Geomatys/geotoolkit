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

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DggalGeoExtent extends StructClass {

    private static final GroupLayout LAYOUT_LL;
    private static final GroupLayout LAYOUT_UR;

    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
       LAYOUT_LL = DggalGeoPoint.LAYOUT.withName("ll"),
       LAYOUT_UR = DggalGeoPoint.LAYOUT.withName("ur")
    ).withName("GeoExtent");

    public DggalGeoExtent(MemorySegment address) {
        super(address);
    }

    public DggalGeoExtent(SegmentAllocator allocator) {
        super(allocator);
    }

    @Override
    protected MemoryLayout getLayout() {
        return LAYOUT;
    }

    public DggalGeoPoint getLl() {
        return new DggalGeoPoint(struct.asSlice(0, DggalGeoPoint.LAYOUT.byteSize()));
    }


    public DggalGeoPoint getUr() {
        return new DggalGeoPoint(struct.asSlice(16, DggalGeoPoint.LAYOUT.byteSize()));
    }

    @Override
    public String toString() {
        return "GeoExtent(ll:" + getLl() + ", ur:" + getUr()+")";
    }

}

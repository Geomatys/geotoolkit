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
import java.lang.foreign.ValueLayout;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DggalGeoPoint extends StructClass {

    private static final ValueLayout.OfDouble LAYOUT_LAT;
    private static final ValueLayout.OfDouble LAYOUT_LON;

    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
       LAYOUT_LAT = DGGAL.C_DOUBLE.withName("lat"),
       LAYOUT_LON = DGGAL.C_DOUBLE.withName("lon")
    ).withName("GeoPoint");

    public DggalGeoPoint(MemorySegment address) {
        super(address);
    }

    public DggalGeoPoint(SegmentAllocator allocator) {
        super(allocator);
    }

    @Override
    protected MemoryLayout getLayout() {
        return LAYOUT;
    }

    public double getLat() {
        return struct.get(LAYOUT_LAT, 0);
    }

    public void setLat(double value) {
        struct.set(LAYOUT_LAT, 0, value);
    }

    public double getLon() {
        return struct.get(LAYOUT_LON, 8);
    }

    public void setLon(double value) {
        struct.set(LAYOUT_LON, 8, value);
    }

    @Override
    public String toString() {
        return "GeoPoint(lat:" + getLat() + ", lon:" + getLon()+")";
    }

}

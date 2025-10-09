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

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.ByteOrder;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DggalDggrs implements AutoCloseable {

    private final String name;
    private final DGGAL dggal;
    private final MemorySegment pointer;

    public DggalDggrs(DGGAL dggal, MemorySegment pointer, String name) {
        if (pointer.address() == 0x0l) {
            throw new NullPointerException("Object pointer is null : 0x0");
        }
        this.dggal = dggal;
        this.pointer = pointer;
        this.name = name;
    }

    public long getZoneFromTextID(String zoneId) throws Throwable {
        try (Arena tempArena = Arena.ofConfined()){
            return (long) dggal.DGGAL_DGGRS_getZoneFromTextID.invokeExact(pointer,tempArena.allocateFrom(zoneId));
        }
    }

    public int getZoneLevel(long zoneId) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_getZoneLevel.invokeExact(pointer, zoneId);
    }

    public int countZoneEdges(long zoneId) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_countZoneEdges.invokeExact(pointer, zoneId);
    }

    public int getRefinementRatio() throws Throwable {
        return (int) dggal.DGGAL_DGGRS_getRefinementRatio.invokeExact(pointer);
    }

    public int getMaxDGGRSZoneLevel() throws Throwable {
        return (int) dggal.DGGAL_DGGRS_getMaxDGGRSZoneLevel.invokeExact(pointer);
    }

    /**
     * @return geopoint in radians
     */
    public double[] getZoneWGS84Centroid(long zoneId) throws Throwable {
        try (Arena tempArena = Arena.ofConfined()){
            final DggalGeoPoint pt = new DggalGeoPoint(tempArena);
            dggal.DGGAL_DGGRS_getZoneWGS84Centroid.invokeExact(pointer, zoneId, pt.struct);
            return new double[]{pt.getLat(), pt.getLon()};
        }
    }

    /**
     * @return geopoint[] in radians
     */
    public double[] getZoneWGS84Vertices(long zoneId) throws Throwable {
        try (Arena tempArena = Arena.ofConfined()){
            final int nbVertices = countZoneEdges(zoneId);
            final MemorySegment buffer = tempArena.allocate(DggalGeoPoint.LAYOUT.byteSize() * nbVertices);
            final int nb = (int) dggal.DGGAL_DGGRS_getZoneWGS84Vertices.invokeExact(pointer, zoneId, buffer);
            final double[] vertices = new double[nb*2];
            buffer.asByteBuffer().order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().get(vertices);
            return vertices;
        }
    }

    public double getZoneArea(long zoneId) throws Throwable {
        return (double) dggal.DGGAL_DGGRS_getZoneArea.invokeExact(pointer, zoneId);
    }

    public long countSubZones(long zoneId, int zoneDepth) throws Throwable {
        return (long) dggal.DGGAL_DGGRS_countSubZones.invokeExact(pointer, zoneId, zoneDepth);
    }

    public String getZoneTextID(long zoneId) throws Throwable {
        try (Arena tempArena = Arena.ofConfined()){
            final MemorySegment buffer = tempArena.allocate(256);
            dggal.DGGAL_DGGRS_getZoneTextID.invokeExact(pointer, zoneId, buffer);
            return buffer.getString(0);
        }
    }

    public long[] getZoneParents(long zoneId) throws Throwable {
        try (Arena tempArena = Arena.ofConfined()){
            final MemorySegment buffer = tempArena.allocate(3 * 8);
            final int nb = (int) dggal.DGGAL_DGGRS_getZoneParents.invokeExact(pointer, zoneId, buffer);
            final long[] dst = new long[nb];
            buffer.asByteBuffer().order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().get(dst);
            return dst;
        }
    }

    public long[] getZoneChildren(long zoneId) throws Throwable {
        try (Arena tempArena = Arena.ofConfined()){
            final MemorySegment buffer = tempArena.allocate(13 * 8);
            final int nb = (int) dggal.DGGAL_DGGRS_getZoneChildren.invokeExact(pointer, zoneId, buffer);
            final long[] dst = new long[nb];
            buffer.asByteBuffer().order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().get(dst);
            return dst;
        }
    }

    public long[] getZoneNeighbors(long zoneId) throws Throwable {
        try (Arena tempArena = Arena.ofConfined()){
            final MemorySegment buffer1 = tempArena.allocate(6 * 8);
            final MemorySegment buffer2 = tempArena.allocate(6 * 4);
            final int nb = (int) dggal.DGGAL_DGGRS_getZoneNeighbors.invokeExact(pointer, zoneId, buffer1, buffer2);
            final long[] dst = new long[nb];
            buffer1.asByteBuffer().order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().get(dst);
            return dst;
        }
    }

    public long getZoneCentroidParent(long zoneId) throws Throwable {
        return (long) dggal.DGGAL_DGGRS_getZoneCentroidParent.invokeExact(pointer, zoneId);
    }

    public long getZoneCentroidChild(long zoneId) throws Throwable {
        return (long) dggal.DGGAL_DGGRS_getZoneCentroidChild.invokeExact(pointer, zoneId);
    }

    /**
     * @return [ll_lat,ll_lon, ur_lat, ur_lon] in radians
     */
    public double[] getZoneWGS84Extent(long zoneId) throws Throwable {
        try (Arena tempArena = Arena.ofConfined()){
            final DggalGeoExtent extent = new DggalGeoExtent(tempArena);
            dggal.DGGAL_DGGRS_getZoneWGS84Extent.invokeExact(pointer, zoneId, extent.struct);
            final DggalGeoPoint ll = extent.getLl();
            final DggalGeoPoint ur = extent.getUr();
            return new double[]{ll.getLat(), ll.getLon(), ur.getLat(), ur.getLon()};
        }
    }

    /**
     * @param geoextent [ll_lat,ll_lon, ur_lat, ur_lon] in radians
     */
    public long[] listZones(int level, double[] geoextent) throws Throwable {
        try (Arena tempArena = Arena.ofConfined()){
            //DGGAL do not like null
            if (geoextent == null) {
                geoextent = new double[]{
                    Math.toRadians(-90),
                    Math.toRadians(-180),
                    Math.toRadians(90),
                    Math.toRadians(180)
                };
            }

            final DggalGeoExtent extent = new DggalGeoExtent(tempArena);
            extent.getLl().setLat(geoextent[0]);
            extent.getLl().setLon(geoextent[1]);
            extent.getUr().setLat(geoextent[2]);
            extent.getUr().setLon(geoextent[3]);
            try (final DggalArrayZone array = new DggalArrayZone(dggal,
                    (MemorySegment) dggal.DGGAL_DGGRS_listZones.invokeExact(pointer, level, extent.struct))) {
                return array.toArray();
            }
        }
    }

    public double[] getZoneRefinedWGS84Vertices(long zone, int refinement) throws Throwable {
        final MemorySegment arrayPointer = (MemorySegment) dggal.DGGAL_DGGRS_getZoneRefinedWGS84Vertices.invokeExact(pointer, zone, refinement);
        if (arrayPointer.address() == 0x0l) return new double[0];
        try (final DggalArrayGeoPoint array = new DggalArrayGeoPoint(dggal,arrayPointer)) {
            return array.toArray();
        }
    }

    public long[] getSubZones(long zone, int depth) throws Throwable {
        final MemorySegment arrayPointer = (MemorySegment) dggal.DGGAL_DGGRS_getSubZones.invokeExact(pointer, zone, depth);
        if (arrayPointer.address() == 0x0l) return new long[0];
        try (final DggalArrayZone array = new DggalArrayZone(dggal, arrayPointer)) {
            return array.toArray();
        }
    }

    /**
     * @return centroid [lat,lon] in radians
     */
    public long getZoneFromWGS84Centroid(int level, double[] centroid) throws Throwable {
        try (Arena tempArena = Arena.ofConfined()){
            final DggalGeoPoint gp = new DggalGeoPoint(tempArena);
            gp.setLat(centroid[0]);
            gp.setLon(centroid[1]);
            return (long) dggal.DGGAL_DGGRS_getZoneFromWGS84Centroid.invokeExact(pointer, level, gp.struct);
        }
    }

    public long countZones(int level) throws Throwable {
        return (long) dggal.DGGAL_DGGRS_countZones.invokeExact(pointer, level);
    }

    public long getFirstSubZone(long zone, int relativeDepth) throws Throwable {
        return (long) dggal.DGGAL_DGGRS_getFirstSubZone.invokeExact(pointer, zone, relativeDepth);
    }

    public int getIndexMaxDepth() throws Throwable {
        return (int) dggal.DGGAL_DGGRS_getIndexMaxDepth.invokeExact(pointer);
    }

    public int getMaxChildren() throws Throwable {
        return (int) dggal.DGGAL_DGGRS_getMaxChildren.invokeExact(pointer);
    }

    public int getMaxNeighbors() throws Throwable {
        return (int) dggal.DGGAL_DGGRS_getMaxNeighbors.invokeExact(pointer);
    }

    public int getMaxParents() throws Throwable {
        return (int) dggal.DGGAL_DGGRS_getMaxParents.invokeExact(pointer);
    }

    public long getSubZoneAtIndex(long parent, int relativeDepth, long index) throws Throwable {
        return (long) dggal.DGGAL_DGGRS_getSubZoneAtIndex.invokeExact(pointer, parent, relativeDepth, index);
    }

    public long getSubZoneIndex(long parent, long subzone) throws Throwable {
        return (long) dggal.DGGAL_DGGRS_getSubZoneIndex.invokeExact(pointer, parent, subzone);
    }

    public double[] getSubZoneCRSCentroids(long parent, long crs, int relativeDepth) throws Throwable {
        try (final DggalArrayPointd array = new DggalArrayPointd(dggal,
            (MemorySegment)dggal.DGGAL_DGGRS_getSubZoneCRSCentroids.invokeExact(pointer, parent, crs, relativeDepth))){
            return array.toArray();
        }
    }

    public double[] getSubZoneWGS84Centroids(long parent, int relativeDepth) throws Throwable {
        try (final DggalArrayGeoPoint array = new DggalArrayGeoPoint(dggal,
            (MemorySegment)dggal.DGGAL_DGGRS_getSubZoneWGS84Centroids.invokeExact(pointer, parent, relativeDepth))){
            return array.toArray();
        }
    }

    public double[] getZoneRefinedCRSVertices(long zone, long crs, int refinement) throws Throwable {
        try (final DggalArrayPointd array = new DggalArrayPointd(dggal,
            (MemorySegment)dggal.DGGAL_DGGRS_getZoneRefinedCRSVertices.invokeExact(pointer, zone, crs, refinement))){
            return array.toArray();
        }
    }

    public double[] getZoneCRSCentroid(long zone, long crs) throws Throwable {
        try (Arena tempArena = Arena.ofConfined()){
            final DggalPointd gp = new DggalPointd(tempArena);
            dggal.DGGAL_DGGRS_getZoneCRSCentroid.invokeExact(pointer, zone, crs, gp.struct);
            return new double[]{gp.getX(), gp.getY()};
        }
    }

    /**
     * @return [ll_lat,ll_lon, ur_lat, ur_lon] in radians
     */
    public double[] getZoneCRSExtent(long zone, long crs) throws Throwable {
        try (Arena tempArena = Arena.ofConfined()){
            final DggalGeoExtent gp = new DggalGeoExtent(tempArena);
            dggal.DGGAL_DGGRS_getZoneCRSExtent.invokeExact(pointer, zone, crs, gp.struct);
            final DggalGeoPoint ll = gp.getLl();
            final DggalGeoPoint ur = gp.getUr();
            return new double[]{ll.getLat(), ll.getLon(), ur.getLat(), ur.getLon()};
        }
    }

    public long[] compactZones(long[] zones) throws Throwable {
        try (final DggalArrayZone array = new DggalArrayZone(dggal, (MemorySegment) dggal.DGGAL_Array_DGGRSZone_new.invokeExact(zones.length))){
            array.set(zones);
            dggal.DGGAL_DGGRS_compactZones.invokeExact(pointer, array.getPointer());
            return array.toArray();
        }
    }

    public int get64KDepth() throws Throwable {
        return (int) dggal.DGGAL_DGGRS_get64KDepth.invokeExact(pointer);
    }

    public int getMaxDepth() throws Throwable {
        return (int) dggal.DGGAL_DGGRS_getMaxDepth.invokeExact(pointer);
    }

    public int areZonesNeighbors(long a, long b) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_areZonesNeighbors.invokeExact(pointer, a, b);
    }

    public int areZonesSiblings(long a, long b) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_areZonesSiblings.invokeExact(pointer, a, b);
    }

    public int doZonesOverlap(long a, long b) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_doZonesOverlap.invokeExact(pointer, a, b);
    }

    public int doesZoneContain(long hayStack, long needle) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_doesZoneContain.invokeExact(pointer, hayStack, needle);
    }

    public int isZoneAncestorOf(long ancestor, long descendant, int maxDepth) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_isZoneAncestorOf.invokeExact(pointer, ancestor, descendant, maxDepth);
    }

    public int isZoneContainedIn(long needle, long hayStack) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_isZoneContainedIn.invokeExact(pointer, needle, hayStack);
    }

    public int isZoneDescendantOf(long descendant, long ancestor, int maxDepth) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_isZoneDescendantOf.invokeExact(pointer, descendant, ancestor, maxDepth);
    }

    public int isZoneImmediateChildOf(long child, long parent) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_isZoneImmediateChildOf.invokeExact(pointer, child, parent);
    }

    public int isZoneImmediateParentOf(long parent, long child) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_isZoneImmediateParentOf.invokeExact(pointer, parent, child);
    }

    public int zoneHasSubZone(long hayStack, long needle) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_zoneHasSubZone.invokeExact(pointer, hayStack, needle);
    }

    public int getLevelFromMetersPerSubZone(double physicalMetersPerSubZone, int relativeDepth) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_getLevelFromMetersPerSubZone.invokeExact(pointer, physicalMetersPerSubZone, relativeDepth);
    }

    /**
     * @param geoextent [ll_lat,ll_lon, ur_lat, ur_lon] in radians
     */
    public int getLevelFromPixelsAndExtent(double[] geoextent, int width, int height, int relativeDepth) throws Throwable {
        try (Arena tempArena = Arena.ofConfined()){
            final DggalGeoExtent extent = new DggalGeoExtent(tempArena);
            extent.getLl().setLat(geoextent[0]);
            extent.getLl().setLon(geoextent[1]);
            extent.getUr().setLat(geoextent[2]);
            extent.getUr().setLon(geoextent[3]);
            return (int) dggal.DGGAL_DGGRS_getLevelFromPixelsAndExtent.invokeExact(pointer, extent.struct, width, height, relativeDepth);
        }
    }

    public int getLevelFromRefZoneArea(double metersSquared) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_getLevelFromRefZoneArea.invokeExact(pointer, metersSquared);
    }

    public int getLevelFromScaleDenominator(double scaleDenominator, int relativeDepth, double mmPerPixel) throws Throwable {
        return (int) dggal.DGGAL_DGGRS_getLevelFromScaleDenominator.invokeExact(pointer, scaleDenominator, relativeDepth, mmPerPixel);
    }

    public double getMetersPerSubZoneFromLevel(int parentLevel, int relativeDepth) throws Throwable {
        return (double) dggal.DGGAL_DGGRS_getMetersPerSubZoneFromLevel.invokeExact(pointer, parentLevel, relativeDepth);
    }

    public double getRefZoneArea(int level) throws Throwable {
        return (double) dggal.DGGAL_DGGRS_getRefZoneArea.invokeExact(pointer, level);
    }

    public double getScaleDenominatorFromLevel(int parentLevel, int relativeDepth, double mmPerPixel) throws Throwable {
        return (double) dggal.DGGAL_DGGRS_getScaleDenominatorFromLevel.invokeExact(pointer, parentLevel, relativeDepth, mmPerPixel);
    }

    @Override
    public void close() throws Exception {
        try {
            dggal.DGGAL_DGGRS_delete.invokeExact(pointer);
        } catch (Throwable ex) {
            throw new Exception(ex);
        }
    }

    @Override
    public String toString() {
        return "DggalDggrs:" + name;
    }

}

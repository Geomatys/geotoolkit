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
package org.geotoolkit.dggs.h3;

import com.uber.h3core.util.LatLng;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import org.geotoolkit.dggs.h3.internal.shared.H3Index;

/**
 * Python code provided by Jerome Saint-Louis to ensure H3 subchild results match DGGRS requirements.
 *
 *
 * @author Jerome Saint-Louis (Ecere), original code in python
 * @author Johann Sorel (Geomatys), java port
 */
public final class H3Ext {

    /**
     * Performs spherical linear interpolation between two geographic points.
     * Assumes a perfect sphere model for simplicity.
     */
    public static LatLng slerp(LatLng start, LatLng end, double ratio) {
        final double startLatRad = Math.toRadians(start.lat);
        final double startLngRad = Math.toRadians(start.lng);
        final double endLatRad   = Math.toRadians(end.lat);
        final double endLngRad   = Math.toRadians(end.lng);

        final double sinStartLatRad = Math.sin(startLatRad);
        final double cosStartLatRad = Math.cos(startLatRad);
        final double sinEndLatRad   = Math.sin(endLatRad);
        final double cosEndLatRad   = Math.cos(endLatRad);

        final double cosOmega = sinStartLatRad * sinEndLatRad
                              + cosStartLatRad * cosEndLatRad * Math.cos(endLngRad - startLngRad);
        final double omega = Math.acos(cosOmega);

        if (omega == 0.0) {
            return new LatLng(start.lat, start.lng);
        }

        final double sinOmega = Math.sin(omega);
        final double a = Math.sin((1.0 - ratio) * omega) / sinOmega;
        final double b = Math.sin(ratio * omega) / sinOmega;

        final double x = a * cosStartLatRad * Math.cos(startLngRad)
                       + b * cosEndLatRad   * Math.cos(endLngRad);
        final double y = a * cosStartLatRad * Math.sin(startLngRad)
                       + b * cosEndLatRad   * Math.sin(endLngRad);
        final double z = a * sinStartLatRad + b * sinEndLatRad;

        final double lat = Math.atan2(z, Math.sqrt(x*x + y*y));
        final double lng = Math.atan2(y, x);

        return new LatLng(Math.toDegrees(lat), Math.toDegrees(lng));
    }

    /**
     * Checks if a candidate H3 index is geometrically contained within an
     * ancestral H3 index using a vertex-based boundary check with SLERP.
     */
    public static boolean zoneHasSubZone(long ancestorZone, long candidateSubzone) {
        final int candidateRes = H3Index.getResolution(candidateSubzone);
        final int ancestorRes  = H3Index.getResolution(ancestorZone);
        if (candidateRes <= ancestorRes) {
            return false;
        }

        final LatLng centroid = H3Dggrs.H3.cellToLatLng(candidateSubzone);
        final List<Long> vertexIndices = H3Dggrs.H3.cellToVertexes(candidateSubzone);

        for (Long vertexIndex : vertexIndices) {
            if (vertexIndex == 0) continue;

            final LatLng vertex = H3Dggrs.H3.vertexToLatLng(vertexIndex);
            final LatLng interpolated = slerp(vertex, centroid, 0.01);
            final long coarser_zone = H3Dggrs.H3.latLngToCell(interpolated.lat, interpolated.lng, ancestorRes);

            if (coarser_zone == ancestorZone) {
                return true;
            }
        }

        return false;
    }

    /**
     * Calculates all geometrically contained sub-zones within an ancestral zone
     * at a specific relative depth, using a rigorous topological search and
     * the specified optimization.
     */
    public static LongStream getGeometricSubZones(long ancestorZone, int relativeDepth) {
        final int ancestorRes = H3Index.getResolution(ancestorZone);
        final int targetRes = ancestorRes + relativeDepth;
        if (targetRes < 0 || targetRes > 15) {
            throw new IllegalArgumentException("Relative depth results in an invalid resolution.");
        }

        final long centerChild = H3Dggrs.H3.cellToCenterChild(ancestorZone, ancestorRes+1);
        final List<Long> rings = new ArrayList<>();
        rings.addAll(H3Dggrs.H3.gridRing(centerChild, 1));
        rings.addAll(H3Dggrs.H3.gridRing(centerChild, 2));

        LongStream stream = LongStream.empty();
        stream = LongStream.concat(stream, H3Dggrs.H3.cellToChildren(centerChild, targetRes).stream().mapToLong(Long::longValue));
        for (Long r : rings) {
            LongStream s = H3Dggrs.H3.cellToChildren(r, targetRes)
                    .parallelStream()
                    .filter((Long l) -> zoneHasSubZone(ancestorZone, l))
                    .mapToLong(Long::longValue);
            stream = LongStream.concat(stream, s);
        }

        return stream;
    }

}

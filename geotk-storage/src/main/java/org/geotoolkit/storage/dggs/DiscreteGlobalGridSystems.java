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
package org.geotoolkit.storage.dggs;

import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Loop;
import com.google.common.geometry.S2Point;
import com.google.common.geometry.S2Polygon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import org.geotoolkit.storage.dggs.internal.shared.GridAsDiscreteGlobalGridResource;
import java.util.List;
import java.util.Set;
import java.util.function.LongFunction;
import java.util.stream.Stream;
import javax.measure.IncommensurableException;
import org.apache.sis.geometries.LinearRing;
import org.apache.sis.geometries.PointSequence;
import org.apache.sis.geometries.math.TupleArray;
import org.apache.sis.geometries.math.TupleArrays;
import org.apache.sis.geometries.internal.shared.ArraySequence;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.geometry.wrapper.GeometryWrapper;
import org.apache.sis.measure.AngleFormat;
import org.apache.sis.measure.Latitude;
import org.apache.sis.measure.Longitude;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.internal.shared.GeodeticObjectBuilder;
import org.apache.sis.metadata.iso.extent.DefaultBoundingPolygon;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.Utilities;
import org.geotoolkit.storage.dggs.internal.shared.ComputedZoneIndexList;
import org.geotoolkit.storage.dggs.internal.shared.FeatureSetAsDiscreteGlobalGridResource;
import org.geotoolkit.storage.dggs.internal.shared.MemoryDiscreteGlobalGridResource;
import org.geotoolkit.storage.rs.CodedResource;
import org.geotoolkit.storage.rs.internal.shared.CodedCoverageAsFeatureSet;
import org.geotoolkit.storage.rs.internal.shared.s2.Factory;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Utility methods for DGGRS.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DiscreteGlobalGridSystems {

    private static final GeometryFactory GF = new GeometryFactory();

    public static List<Object> createComputedList(long start, long step, int count, LongFunction<Object> toId) {
        return new ComputedZoneIndexList(start, step, count, toId);
    }

    /**
     * Compute the full ellipsoid surface in m².
     *
     * @todo current version use a sphere formula
     * @param gcrs to extract ellipsoid from
     * @return ellipsoid surface
     */
    public static double computeSurface(GeographicCRS gcrs) {
        final Ellipsoid ellipsoid = gcrs.getDatum().getEllipsoid();
        final double semiMajorAxis = ellipsoid.getSemiMajorAxis();
        final double semiMinorAxis = ellipsoid.getSemiMinorAxis();
        final double r = (semiMajorAxis + semiMinorAxis) / 2;
        final double surfaceArea = 4.0 * Math.PI * r * r;
        return surfaceArea;
    }

    /**
     * Create a local orthographic projection at given longitude and latitude.
     *
     * @todo move this somewhere else.
     */
    public static ProjectedCRS createOrthographicCRS(final GeographicCRS baseCRS,
                final double latitude, final double longitude) throws FactoryException
        {
            return newBuilder("ORTHOGRAPHIC", latitude, longitude)
                    .setConversionMethod("Orthographic")
                    .setParameter("Latitude of natural origin",  latitude,  Units.DEGREE)
                    .setParameter("Longitude of natural origin", longitude, Units.DEGREE)
                    .createProjectedCRS(baseCRS, null);
        }

    /**
     * Creates a new builder initialized to the projection name for the given coordinates.
     */
    private static final GeodeticObjectBuilder newBuilder(String name, final double latitude, final double longitude) {
        final AngleFormat  f = new AngleFormat("DD°MM′SS″");
        final StringBuffer b = new StringBuffer();
        b.append(name).append(" @ ");
        f.format(new Latitude (latitude),  b, null).append(' ');
        f.format(new Longitude(longitude), b, null);
        return new GeodeticObjectBuilder().addName(b.toString());
    }

    /**
     * View given grid coverage as a DiscreteGlobalGridResource.
     */
    public static DiscreteGlobalGridResource viewAsDggrs(GridCoverageResource base, DiscreteGlobalGridReferenceSystem dggrs) throws DataStoreException, IncommensurableException, TransformException {
        return new GridAsDiscreteGlobalGridResource(dggrs, base);
    }

    /**
     * View given FeatureSet as a DiscreteGlobalGridResource.
     */
    public static CodedResource viewAsDggrs(FeatureSet base, DiscreteGlobalGridReferenceSystem dggrs) throws DataStoreException, IncommensurableException {
        return new FeatureSetAsDiscreteGlobalGridResource(dggrs, base);
    }

    /**
     * View given DiscreteGlobalGridCoverage as a DiscreteGlobalGridResource.
     */
    public static DiscreteGlobalGridResource viewAsResource(DiscreteGlobalGridCoverage coverage) {
        return new MemoryDiscreteGlobalGridResource(coverage);
    }

    /**
     * View given DiscreteGlobalGridCoverage as a FeatureSet.
     */
    public static FeatureSet viewAsFeatureSet(DiscreteGlobalGridCoverage coverage, boolean idAsLong, String geometryType) {
        return new CodedCoverageAsFeatureSet(coverage, idAsLong, geometryType);
    }

    /**
     * Find the first smallest single cell on it's level which intersects the given envelope.
     * Result may contain more then one zone in case the data overlaps several root zones.
     *
     */
    public static List<Zone> firstIntersect(DiscreteGlobalGridReferenceSystem dggrs, Envelope env) throws TransformException {
        final GeneralEnvelope genv = new GeneralEnvelope(Envelopes.transform(env, dggrs.getGridSystem().getCrs()));

        try (Stream<Zone> stream = dggrs.getGridSystem().getHierarchy().getGrids().get(0).getZones()) {
            final S2Polygon polygon = toS2Polygon(genv);
            return search(stream.toList(), polygon);
        }
    }

    private static List<Zone> search(Collection<? extends Zone> zones, S2Polygon env) {

        final List<Zone> candidates = new ArrayList<>(1);
        for (Zone z : zones) {
            S2Polygon geometry = DiscreteGlobalGridSystems.toS2Polygon(z.getGeographicExtent());
            if (geometry == null || env.intersects(geometry)) {
                candidates.add(z);
            }
        }

        if (candidates.size() == 1) {
            final List<Zone> subZones = search(candidates.get(0).getChildren(), env);
            if (subZones.size() == 1) {
                return subZones;
            } else {
                //return this zone
                return candidates;
            }
        } else {
            return candidates;
        }
    }

    /**
     * Compact the given list of zones.
     * A parent zone is added in the list when all it's children are present, those children will be removed.
     * Returned zones are sorted.
     *
     * @param zones to compact, expected to all be at the same level
     * @param minLevel minimum parent level to compact zone
     * @return sorted list of compact zones
     */
    public static List<Zone> compact(List<Zone> zones, int minLevel) {
        if (zones.size() <= 1) return zones;

        if (zones.get(0).getLocationType().getRefinementLevel() <= minLevel) {
            return zones;
        }

        final Set<Zone> allChildren = new HashSet(zones);
        final Set<Zone> candidateParents = new HashSet<>();

        for (Zone z : zones) {
            candidateParents.addAll(z.getParents());
        }

        //we keep the children to remove in a separate colleciton because they can be referenced by multiple parents
        final Set<Zone> childrenToRemove = new HashSet<>();
        final List<Zone> fullParents = new ArrayList<>();
        for (Zone parent : candidateParents) {
            Collection<? extends Zone> children = parent.getChildren();
            if (allChildren.containsAll(children)) {
                fullParents.add(parent);
                childrenToRemove.addAll(children);
            }
        }

        //remove the child zone if and only if all parents are in the list
        final Iterator<Zone> iterator = childrenToRemove.iterator();
        while (iterator.hasNext()) {
            final Zone zone = iterator.next();
            if (!fullParents.containsAll(zone.getParents())) {
                //we don't have all parents for the zone, we must keep it
                iterator.remove();
            }
        }


        //try to compact parents further
        final List<Zone> compacted = compact(fullParents, minLevel);
        //add leftover children
        allChildren.removeAll(childrenToRemove);
        compacted.addAll(allChildren);

        Collections.sort(compacted);

        return compacted;
    }

    public static Polygon toJTSPolygon(GeographicExtent extent) {
        return toJTSPolygon(toS2Polygon(extent));
    }

    public static Polygon toJTSPolygon(S2Polygon s2) {
        if (s2 == null) return null;
        final double[] coords = toArray(s2);
        final CoordinateSequence cs = new PackedCoordinateSequence.Double(coords, 2, 0);
        final Polygon polygon = GF.createPolygon(cs);
        polygon.setUserData(CommonCRS.WGS84.normalizedGeographic());
        return polygon;
    }

    public static org.apache.sis.geometries.Polygon toSISPolygon(GeographicExtent extent) {
        return toSISPolygon(toS2Polygon(extent));

    }

    public static org.apache.sis.geometries.Polygon toSISPolygon(S2Polygon s2) {
        if (s2 == null) return null;
        final double[] coords = toArray(s2);
        final TupleArray positions = TupleArrays.of(CommonCRS.WGS84.normalizedGeographic(), coords);
        final PointSequence sequence = new ArraySequence(positions);
        final LinearRing exterior = org.apache.sis.geometries.GeometryFactory.createLinearRing(sequence);
        return org.apache.sis.geometries.GeometryFactory.createPolygon(exterior, null);
    }

    /**
     * @return polygon points, as a closed (first point == last point)
     */
    private static double[] toArray(S2Polygon s2) {
        if (s2 == null) return null;
        /*
        S2 ensure the interior is in the left side.
        if it's not the case then we have crossed the date-line
        */
        final List<S2Point> vertices = s2.getLoops().get(0).orientedVertices();
        final double[] coords = new double[(vertices.size()+1)*2];
        for (int k = 0, i = 0, n = vertices.size(); i < n; i++) {
            final S2Point pt = vertices.get(i);
            final S2LatLng latlng = new S2LatLng(pt);
            coords[k++] = latlng.lngDegrees();
            coords[k++] = latlng.latDegrees();
        }
        coords[coords.length-2] = coords[0];
        coords[coords.length-1] = coords[1];

        // We compute the area which will give us the polygon orientation.
        double area = 0.0;
        for (int i = 0; i < coords.length-2; i+=2) {
            area += (coords[i+2]-coords[i]) * (coords[i+3]+coords[i+1]);
        }

        if (area > 0.0) {
            //polygon is clockwise, which means it's crossing the dateline, we need to fix it
            //push every longitude coordinate which is negative by +360
            boolean allOver180 = true;
            for (int i = 0; i < coords.length; i+=2) {
                if (coords[i] < 0) coords[i] += 360;
                if (coords[i] < 180) allOver180 = false;
            }
            if (allOver180) {
                for (int i = 0; i < coords.length; i+=2) {
                    coords[i] += -360;
                }
            }
        }

        return coords;
    }

    /**
     * @param genv in CRS:84
     * @return S2 polygon
     */
    public static S2Polygon toS2Polygon(Envelope env) {
        if (!Utilities.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(), CommonCRS.WGS84.normalizedGeographic())) {
            throw new IllegalArgumentException("Envelope must be in CRS:84");
        }

        GeneralEnvelope genv = GeneralEnvelope.castOrCopy(env);

        final double minLon = genv.getLower(0);
        final double minLat = genv.getLower(1);
        final double maxLon = genv.getUpper(0);
        final double maxLat = genv.getUpper(1);
        final double spanLon = genv.getSpan(0);
        final double spanLat = genv.getSpan(1);

        final double[] lons;
        final double[] lats;

        if (spanLon >= 360) {
            //add 2 split points
            lons = new double[]{
                minLon,
                minLon + (maxLon - minLon) * (1.0/3.0),
                minLon + (maxLon - minLon) * (2.0/3.0),
                maxLon
            };
        } else if (spanLon >= 180) {
            lons = new double[]{
                minLon,
                minLon + (maxLon - minLon) * (1.0/2.0),
                maxLon
            };
        } else {
            lons = new double[]{
                minLon,
                maxLon
            };
        }

        if (spanLat >= 180) {
            lats = new double[]{
                minLat,
                minLat + (maxLat - minLat) * (1.0/2.0),
                maxLat
            };
        } else {
            lats = new double[]{
                minLat,
                maxLat
            };
        }

        //crosses the antimeridian
        if (minLon > maxLon) {
            //reverse array to preserve CCW direction
            for (int i = 0; i < lons.length / 2; i++) {
                double t = lons[i];
                lons[i] = lons[lons.length - 1 - i];
                lons[lons.length - 1 - i] = t;
            }
        }

        //create polygon in counter-clockwise direction
        final List<S2Point> points = new ArrayList<>();
        for (int i = 0; i < lons.length; i++) {
            points.add(S2LatLng.fromDegrees(lats[0], lons[i]).toPoint());
        }
        for (int i = 1; i < lats.length-1; i++) { //start at 1, end at size-2, do not duplicate corner point
            points.add(S2LatLng.fromDegrees(lats[i], lons[lons.length-1]).toPoint());
        }
        for (int i = lons.length-1; i >= 0; i--) {
            points.add(S2LatLng.fromDegrees(lats[lats.length-1], lons[i]).toPoint());
        }
        for (int i = lats.length-2; i >= 1; i--) { //start at size-2, end at 1, do not duplicate corner point
            points.add(S2LatLng.fromDegrees(lats[i], lons[0]).toPoint());
        }

        return new S2Polygon(new S2Loop(points));
    }

    public static S2Polygon toS2Polygon(GeographicExtent extent) {
        if (extent == null) {
            return null;
        } else if (extent instanceof BoundingPolygon bp) {
            Collection<? extends org.opengis.geometry.Geometry> polygons = bp.getPolygons();
            if (polygons.size() != 1) {
                throw new UnsupportedOperationException("Only single geometry BoundingPolygon types supported.");
            }
            org.opengis.geometry.Geometry geometry = polygons.iterator().next();
            if (!(geometry instanceof GeometryWrapper gw)) {
                throw new UnsupportedOperationException("Only GeometryWrapper geometry type supported");
            }
            Object geom = Factory.INSTANCE.getGeometry(gw);
            if (geom instanceof S2Polygon p) {
                return p;
            } else {
                throw new UnsupportedOperationException("Only S2 geometry types supported.");
            }
        } else {
            throw new UnsupportedOperationException("Only BoundingPolygon types supported.");
        }
    }

    public static BoundingPolygon toGeographicExtent(S2Polygon polygon) {
        return new DefaultBoundingPolygon(Factory.INSTANCE.castOrWrap(polygon));
    }

    /**
     * Search zones which intersect the requested polygon.
     *
     * @param rootZones to start searching in
     * @param level wanted zones level
     * @param geometry to search
     * @return stream of zones at the requested level which intersect the polygon
     */
    public static Stream<Zone> spatialSearch(Collection< ? extends Zone> rootZones, int level, S2Polygon geometry) {

        //we need to remove duplicates because some children may be in multiple parents
        final Set<Object> visited = Collections.synchronizedSet(new HashSet<>());
        Stream<Zone> stream = rootZones.stream().flatMap((z) -> search(z, geometry, level, visited));
        return stream;
    }

    private static Stream<Zone> search(Zone zone, S2Polygon geometry, int level, Set<Object> visited) {
        final Object zid = zone.getIdentifier();
        if (visited.contains(zid)) return Stream.empty();

        final S2Polygon zgeom = DiscreteGlobalGridSystems.toS2Polygon(zone.getGeographicExtent());
        if (zgeom == null || geometry == null || geometry.intersects(zgeom)) {
            if (zone.getLocationType().getRefinementLevel() == level) {
                if (visited.add(zid)) {
                    //zone might have been visited earlier
                    return Stream.of(zone);
                } else {
                    return Stream.empty();
                }
            } else {
                //search children
                return zone.getChildren().stream().flatMap((c) -> search(c,geometry,level, visited));
            }
        }
        return Stream.empty();
    }
}

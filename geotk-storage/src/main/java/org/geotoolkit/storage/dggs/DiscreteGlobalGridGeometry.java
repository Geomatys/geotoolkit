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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometries.Geometry;
import org.apache.sis.geometries.operation.GeometryOperations;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.Utilities;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGrid;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridHierarchy;
import org.geotoolkit.referencing.rs.Code;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.geotoolkit.storage.rs.internal.shared.CodeTransforms;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.geotoolkit.storage.rs.CodeTransform;
import org.geotoolkit.storage.rs.internal.shared.SubTransform;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.ReferenceSystem;

/**
 * DGGRS coverage geometry.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class DiscreteGlobalGridGeometry extends CodedGeometry {

    //computed
    private Integer depth;
    private boolean bboxComputed = false;
    private GeographicExtent geoExtent;

    public static DiscreteGlobalGridGeometry unstructured(DiscreteGlobalGridReferenceSystem dggrs, List<Object> zoneIds, GeographicExtent bbox) {
        return new Unstructured(dggrs, zoneIds, bbox);
    }

    public static DiscreteGlobalGridGeometry unstructured(DiscreteGlobalGridReferenceSystem dggrs, GridExtent extent, CodeTransform trs, GeographicExtent bbox) {
        return new Unstructured(dggrs, extent, trs, bbox);
    }

    public static DiscreteGlobalGridGeometry subZone(DiscreteGlobalGridReferenceSystem dggrs, Object baseZoneId, Integer relativeDepth) {
        final SubZoneTransform trs = new SubZoneTransform(dggrs, baseZoneId, relativeDepth);
        return new SubZone(trs, null);
    }

    public static DiscreteGlobalGridGeometry subZones(DiscreteGlobalGridReferenceSystem dggrs, Object[] baseZoneIds, Integer relativeDepth) {
        final SubZoneTransform[] subZones = new SubZoneTransform[baseZoneIds.length];
        for (int i = 0; i < subZones.length; i++) {
            subZones[i] = new SubZoneTransform(dggrs, baseZoneIds[i], relativeDepth);
        }
        final SubZoneTransforms trs = new SubZoneTransforms(subZones);
        return new SubZones(trs, null);
    }

    /**
     * @param dggrs not null
     * @param zoneIds all ids are expected to be at the same depth level
     * @param bbox can be null
     */
    protected DiscreteGlobalGridGeometry(DiscreteGlobalGridReferenceSystem dggrs, List<Object> zoneIds, GeographicExtent bbox) {
        this(dggrs,
             new GridExtent(null, 0, zoneIds.size(), false),
             CodeTransforms.toTransform(dggrs, zoneIds),
             bbox);
    }

    private DiscreteGlobalGridGeometry(DiscreteGlobalGridReferenceSystem dggrs, Object[] baseZoneIds, Integer relativeDepth, List<Object> zoneIds, GeographicExtent bbox) {
        this(dggrs,
            (zoneIds == null) ? null : new GridExtent(null, 0, zoneIds.size(), false),
            (zoneIds == null) ? null : CodeTransforms.toTransform(dggrs, zoneIds), bbox);
    }

    protected DiscreteGlobalGridGeometry(DiscreteGlobalGridReferenceSystem dggrs, GridExtent extent, CodeTransform trs, GeographicExtent geoExtent) {
        super(dggrs, extent, trs, geoExtent);
        this.geoExtent = geoExtent;
        bboxComputed = geoExtent != null;
    }

    /**
     * Returns the DiscreteGlobalGridReferenceSystem.
     *
     * @return DiscreteGlobalGridReferenceSystem, never null
     */
    @Override
    public DiscreteGlobalGridReferenceSystem getReferenceSystem() {
        return (DiscreteGlobalGridReferenceSystem) super.getReferenceSystem();
    }

    /**
     * @return can be null
     */
    public Object[] getBaseZoneIds() {
        return null;
    }

    /**
     * @return can be null
     */
    public Integer getRelativeDepth() {
        return null;
    }

    @Override
    public Envelope getEnvelope() {
        final DiscreteGlobalGridHierarchy hierarchy = getReferenceSystem().getGridSystem().getHierarchy();
        GeneralEnvelope all = null;
        for (Object zid : getZoneIds()) {
            final Zone zone = hierarchy.getZone(zid);
            final Envelope env = zone.getEnvelope();
            if (env != null) {
                if (all == null) {
                    all = new GeneralEnvelope(env);
                } else {
                    all.add(env);
                }
            }
        }
        return all;
    }

    @Override
    public Envelope getEnvelope(CoordinateReferenceSystem crs) throws TransformException {
        if (crs == null) return getEnvelope();

        final CoordinateReferenceSystem baseCrs = getReferenceSystem().getGridSystem().getCrs();
        MathTransform trs = null;
        if (!Utilities.equalsIgnoreMetadata(baseCrs, crs)) {
            try {
                trs = CRS.findOperation(baseCrs, crs, null).getMathTransform();
            } catch (FactoryException ex) {
                throw new TransformException(ex);
            }
        } else {
            return getEnvelope();
        }

        final DiscreteGlobalGridHierarchy hierarchy = getReferenceSystem().getGridSystem().getHierarchy();
        GeneralEnvelope all = null;
        for (Object zid : getZoneIds()) {
            final Zone zo = hierarchy.getZone(zid);
            final Geometry geometry = DiscreteGlobalGridSystems.toSISPolygon(zo.getGeographicExtent());
            final Geometry trsGeom = GeometryOperations.SpatialEdition.transform(geometry, crs, trs);
            final Envelope env = trsGeom.getEnvelope();
            if (env != null) {
                if (all == null) {
                    all = new GeneralEnvelope(env);
                } else {
                    all.add(env);
                }
            }
        }
        return all;
    }

    @Override
    public double[] getResolution(boolean allowEstimate) {
        double[] resolution2D = getResolutionProjected(allowEstimate);
        return new double[]{resolution2D[0]};
    }

    @Override
    public double[] getResolutionProjected(boolean allowEstimate) {
        final DiscreteGlobalGridReferenceSystem.Coder coder = getReferenceSystem().createCoder();
        final double[] res = new double[]{Double.NaN,Double.NaN};
        try {
            for (Object zone : getZoneIds()) {
                final Zone zo = coder.decode(zone);
                final Envelope env = zo.getEnvelope();
                if (env != null) {
                    double r0 = env.getSpan(0);
                    double r1 = env.getSpan(1);
                    if (Double.isNaN(res[0])) {
                        res[0] = r0;
                        res[1] = r1;
                    } else {
                        if (res[0] > r0) res[0] = r0;
                        if (res[1] > r1) res[1] = r1;
                    }
                    break;
                }
            }
        } catch (TransformException ex) {
            //do nothing
        }
        return res;
    }

    @Override
    public synchronized GeographicExtent getGeographicExtent() {
        if (bboxComputed) return geoExtent;
        bboxComputed = true;
        final List<Object> zones = getZoneIds();
        final DiscreteGlobalGridReferenceSystem.Coder coder = getReferenceSystem().createCoder();

        GeneralEnvelope all = null;
        for (Object zone : zones) {
            try {
                final Zone zo = coder.decode(zone);
                final Envelope env = zo.getEnvelope();
                if (env != null) {
                    if (all == null) {
                        all = new GeneralEnvelope(env);
                    } else {
                        all.add(env);
                    }
                }
            } catch (TransformException ex) {
                geoExtent = null;
            }
        }
        return geoExtent;
    }

    /**
     * List of zones selected in the geometry.
     *
     * @return List of zone identifiers, never null
     */
    public abstract List<Object> getZoneIds();

    /**
     * @return refinement level of zones in the coverage geometry.
     */
    public synchronized int getRefinementLevel() {
        if (depth != null) return depth;

        //find refinement level in the cells
        //check all zone are at same depth
        final DiscreteGlobalGridReferenceSystem dggrs = getReferenceSystem();
        final List<Object> zones = getZoneIds();
        final DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();
        Integer d = null;
        try {
            for (Object zone : zones) {
                final int level = coder.decode(zone).getLocationType().getRefinementLevel();
                if (d == null) d = level;
                else if (d != level) throw new IllegalArgumentException("Geometry is composed of zones of different depth. Geometry is incorrect");
            }
        } catch (TransformException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
        depth = d;
        return depth;
    }

    /**
     * Convert the geometry to a different DGGRS.
     *
     * @param targetDggrs target DGGRS, may be the same or null
     * @param availableDepths optional zone depth range to restrict zones
     * @param relativeTileDepth optional tiling indicator, in which case the parent tile zones will be computed,
     *                          if defined, availableDepths must also be defined
     * @return new geometry
     */
    public DiscreteGlobalGridGeometry transformTo(DiscreteGlobalGridReferenceSystem targetDggrs, NumberRange<Integer> availableDepths, Integer relativeTileDepth) throws TransformException, IncommensurableException {
        final DiscreteGlobalGridReferenceSystem dggrs = getReferenceSystem();


        if (targetDggrs != null && !targetDggrs.equals(dggrs)) {
            //changing DGGRS, we will return what matches best

            //get the resolution of this geometry
            final DiscreteGlobalGridHierarchy dggh = dggrs.getGridSystem().getHierarchy();
            final int refinementLevel = getRefinementLevel();
            final DiscreteGlobalGrid grid = dggh.getGrids().get(refinementLevel);
            final Quantity<?> precision = grid.getPrecision();

            //find the best grid that match in the target DGGRS
            DiscreteGlobalGridHierarchy targetDggh = targetDggrs.getGridSystem().getHierarchy();

            DiscreteGlobalGrid bestGrid = targetDggh.getGrid(precision);
            int bestLevel = bestGrid.getRefinementLevel();

            //check if it's in the range we have and adjust it if needed
            if (availableDepths != null && !availableDepths.contains(bestLevel)) {
                final int min = (int) availableDepths.getMinDouble(true);
                final int max = (int) availableDepths.getMaxDouble(true);
                if (bestLevel <= min) {
                    bestLevel = min;
                } else if (bestLevel >= max) {
                    bestLevel = max;
                }
                bestGrid = targetDggh.getGrids().get(bestLevel);
            }

            if (relativeTileDepth == null) {
                final List<Object> zids = bestGrid.getZones(getGeographicExtent()).map(Zone::getIdentifier).toList();
                return DiscreteGlobalGridGeometry.unstructured(targetDggrs, zids, null);
            } else {
                //get the grid at parent level
                bestGrid = targetDggh.getGrids().get(bestLevel - relativeTileDepth);

                //rebuild the query in target dggrs at existing levels
                Object[] baseZoneIds = bestGrid.getZones(getGeographicExtent()).map(Zone::getIdentifier).toArray();
                return DiscreteGlobalGridGeometry.subZones(targetDggrs, baseZoneIds, relativeTileDepth);
            }
        }

        if (availableDepths != null) {
            final DiscreteGlobalGridHierarchy dggh = dggrs.getGridSystem().getHierarchy();
            int bestLevel = getRefinementLevel();

            //check if it's in the range we have and adjust it if needed
            if (!availableDepths.contains(bestLevel)) {
                final int min = (int) availableDepths.getMinDouble(true);
                final int max = (int) availableDepths.getMaxDouble(true);
                if (bestLevel <= min) {
                    bestLevel = min;
                } else if (bestLevel >= max) {
                    bestLevel = max;
                }
                DiscreteGlobalGrid bestGrid = dggh.getGrids().get(bestLevel);

                if (relativeTileDepth == null) {
                    final List<Object> zids = bestGrid.getZones(getGeographicExtent()).map(Zone::getIdentifier).toList();
                    return DiscreteGlobalGridGeometry.unstructured(targetDggrs, zids, null);
                } else {
                    //get the grid at parent level
                    bestGrid = dggh.getGrids().get(bestLevel - relativeTileDepth);

                    //rebuild the query in target dggrs at existing levels
                    Object[] baseZoneIds = bestGrid.getZones(getGeographicExtent()).map(Zone::getIdentifier).toArray();
                    return DiscreteGlobalGridGeometry.subZones(targetDggrs, baseZoneIds, relativeTileDepth);
                }
            }
        }

        //unchanged
        return this;
    }

    @Override
    public int hashCode() {
        return 61 * super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    private static final class Unstructured extends DiscreteGlobalGridGeometry {

        public Unstructured(DiscreteGlobalGridReferenceSystem dggrs, List<Object> zoneIds, GeographicExtent bbox) {
            super(dggrs,
                (zoneIds != null) ? new GridExtent(null, 0, zoneIds.size(), false) : null,
                (zoneIds != null) ? CodeTransforms.toTransform(dggrs, zoneIds) : null,
                bbox);
        }

        public Unstructured(DiscreteGlobalGridReferenceSystem dggrs, GridExtent extent, CodeTransform trs, GeographicExtent bbox) {
            super(dggrs,
                extent,
                trs,
                bbox);
        }

        @Override
        public List<Object> getZoneIds() {
            final CodeTransforms.Listed trs = (CodeTransforms.Listed) getGridToRS();
            return (List<Object>) trs.getList();
        }

    }

    private static final class SubZone extends DiscreteGlobalGridGeometry {

        public SubZone(SubZoneTransform trs, GeographicExtent bbox) {
            super(trs.dggrs, trs.extent, trs, null);
        }

        /**
         * @return can be null
         */
        public Object[] getBaseZoneIds() {
            return new Object[]{((SubZoneTransform)getGridToRS()).baseZoneId};
        }

        /**
         * @return can be null
         */
        public Integer getRelativeDepth() {
            return ((SubZoneTransform)getGridToRS()).relativeDepth;
        }

        @Override
        public List<Object> getZoneIds() {
            try {
                return List.of(((SubZoneTransform)getGridToRS()).getZids());
            } catch (TransformException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }

    private static final class SubZones extends DiscreteGlobalGridGeometry {

        public SubZones(SubZoneTransforms trs, GeographicExtent bbox) {
            super(trs.dggrs, trs.extent, trs, null);
        }

        /**
         * @return can be null
         */
        public Object[] getBaseZoneIds() {
            return ((SubZoneTransforms)getGridToRS()).tileZoneIds;
        }

        /**
         * @return can be null
         */
        public Integer getRelativeDepth() {
            return ((SubZoneTransforms)getGridToRS()).relativeDepth;
        }

        @Override
        public List<Object> getZoneIds() {
            return ((SubZoneTransforms)getGridToRS()).getZids();
        }
    }

    private static class SubZoneTransform extends SubTransform {

        private final DiscreteGlobalGridReferenceSystem dggrs;
        private final Object baseZoneId;
        private final Integer relativeDepth;
        private final GridExtent extent;
        //computed when needed
        private Object[] zids;
        private Map<Object,Integer> index = new HashMap<>();

        private SubZoneTransform(DiscreteGlobalGridReferenceSystem dggrs, Object baseZoneId, Integer relativeDepth) {
            this.dggrs = dggrs;
            this.baseZoneId = baseZoneId;
            this.relativeDepth = relativeDepth;
            this.extent = new GridExtent(null, 0, dggrs.getGridSystem().getHierarchy().getZone(baseZoneId).countChildrenAtRelativeDepth(relativeDepth), false);
        }

        @Override
        public ReferenceSystem getRS() {
            return dggrs;
        }

        @Override
        public int getDimension() {
            return 1;
        }

        public Object[] getZids() throws TransformException {
            init();
            return zids;
        }

        private synchronized void init() throws TransformException {
            if (zids != null) return;

            final DiscreteGlobalGridHierarchy dggh = dggrs.getGridSystem().getHierarchy();
            zids = new Object[(int) extent.getSize(0)];
            final Zone zone = dggh.getZone(baseZoneId);
            final int searchedLevel = zone.getLocationType().getRefinementLevel() + relativeDepth;
            int idx = 0;
            try (Stream<Object> s = dggh.getGrids().get(searchedLevel).getZones(zone).map(Zone::getIdentifier)) {
                Iterator<Object> iterator = s.iterator();
                while (iterator.hasNext()) {
                    Object zid = iterator.next();
                    zids[idx] = zid;
                    index.put(zid, idx);
                    idx++;
                }
            }
        }

        @Override
        public Code toCode(int[] gridPosition) throws TransformException {
            init();
            return new Code(dggrs, new Object[]{zids[gridPosition[0]]});
        }

        @Override
        public int[] toGrid(Code location) throws TransformException {
            init();
            final Integer i = index.get(location.getOrdinate(0));
            if (i == null) throw new TransformException("Location code outside this grid : " + location.getOrdinate(0));
            return new int[]{i};
        }

        private Integer toGridInternal(Object zid) throws TransformException {
            init();
            return index.get(zid);
        }

        @Override
        public CodeTransform split(int offset, int size) {
            if (offset == 0 && size == 1) return this;
            throw new IllegalArgumentException("Can not split transform at offset " + offset +" with size " + size);
        }

        @Override
        public void toAddress(int[] gridPosition, Object[] location, int offset) throws TransformException {
            init();
            location[offset] = zids[gridPosition[offset]];
        }

        @Override
        public void toGrid(Object[] location, int[] gridPosition, int offset) throws TransformException {
            init();
            final Integer i = index.get(location[offset]);
            if (i == null) throw new TransformException("Location code outside this grid : " + location[offset]);
            gridPosition[offset] = i;
        }

    }

    private static class SubZoneTransforms extends SubTransform {

        private final DiscreteGlobalGridReferenceSystem dggrs;
        private final SubZoneTransform[] tiles;
        private final long[] offsets;
        private final Object[] tileZoneIds;
        private final int relativeDepth;
        private final GridExtent extent;
        private final long count;

        private final AbstractList<Object> zids = new AbstractList<Object>() {
            @Override
            public Object get(int index) {
                int idx = Arrays.binarySearch(offsets, index);
                if (idx < 0) {
                    idx = -(idx +1);
                    // use the previous iterator
                    idx--;
                }
                if (idx == offsets.length) {
                    throw new IllegalArgumentException("Position is outside grid : " + index);
                }
                try {
                    return tiles[idx].getZids()[index - (int)offsets[idx]];
                } catch (TransformException ex) {
                    throw new RuntimeException(ex.getMessage(), ex);
                }
            }

            @Override
            public int size() {
                return (int) count;
            }
        };

        public SubZoneTransforms(SubZoneTransform[] tiles) {
            this.dggrs = tiles[0].dggrs;
            this.tiles = tiles;
            this.offsets = new long[tiles.length];
            this.tileZoneIds = new Object[tiles.length];
            this.relativeDepth = tiles[0].relativeDepth;
            long count = 0;
            for (int i = 0; i < tiles.length; i++) {
                offsets[i] = count;
                count += tiles[i].extent.getSize(0);
                tileZoneIds[i] = tiles[i].baseZoneId;
            }
            this.extent = new GridExtent(null, 0, count, false);
            this.count = count;
        }

        @Override
        public ReferenceSystem getRS() {
            return tiles[0].dggrs;
        }

        @Override
        public int getDimension() {
            return 1;
        }

        @Override
        public CodeTransform split(int offset, int size) {
            if (offset == 0 && size == 1) return this;
            throw new IllegalArgumentException("Can not split transform at offset " + offset +" with size " + size);
        }

        public List<Object> getZids() {
            return zids;
        }

        @Override
        public void toAddress(int[] gridPosition, Object[] location, int offset) throws TransformException {
            final int gp = gridPosition[offset];

            int idx = Arrays.binarySearch(offsets, gp);
            if (idx < 0) {
                idx = -(idx +1);
                // use the previous iterator
                idx--;
            }
            if (idx == offsets.length) {
                throw new TransformException("Position is outside grid : " + gp);
            }
            tiles[idx].toAddress(new int[]{gp - (int)offsets[idx]}, location, offset);
        }

        @Override
        public void toGrid(Object[] location, int[] gridPosition, int offset) throws TransformException {
            final Object zid = location[offset];
            for (SubZoneTransform trs : tiles) {
                Integer g = trs.toGridInternal(zid);
                if (g != null) {
                    gridPosition[offset] = g;
                    return;
                }
            }
            throw new TransformException("Location code outside this grid : " + zid);
        }

    }
}

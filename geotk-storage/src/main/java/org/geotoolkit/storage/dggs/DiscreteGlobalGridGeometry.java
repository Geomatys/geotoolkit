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
import java.util.List;
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
import org.geotoolkit.storage.rs.CodedGeometry;
import org.geotoolkit.storage.rs.internal.shared.CodeTransforms;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.geotoolkit.storage.rs.CodeTransform;
import org.opengis.metadata.extent.GeographicExtent;

/**
 * DGGRS coverage geometry.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DiscreteGlobalGridGeometry extends CodedGeometry {

    //computed
    private Integer depth;
    private boolean bboxComputed = false;
    private GeographicExtent geoExtent;

    public static DiscreteGlobalGridGeometry unstructured(DiscreteGlobalGridReferenceSystem dggrs, List<Object> zoneIds, GeographicExtent bbox) {
        if (zoneIds != null) {
            final DiscreteGlobalGridTransform.Unstructured trs = DiscreteGlobalGridTransform.unstructured(dggrs, zoneIds.toArray());
            return new DiscreteGlobalGridGeometry(dggrs, trs.getExtent(), trs, bbox);

        } else {
            return new DiscreteGlobalGridGeometry(dggrs, null, null, bbox);
        }
    }

    public static DiscreteGlobalGridGeometry unstructured(DiscreteGlobalGridReferenceSystem dggrs, GridExtent extent, CodeTransform trs, GeographicExtent bbox) {
        return new DiscreteGlobalGridGeometry(dggrs, extent, trs, bbox);
    }

    public static DiscreteGlobalGridGeometry subZone(DiscreteGlobalGridReferenceSystem dggrs, Object baseZoneId, Integer relativeDepth) {
        final DiscreteGlobalGridTransform.SubZoneTransform trs = DiscreteGlobalGridTransform.subZone(dggrs, baseZoneId, relativeDepth);
        return new DiscreteGlobalGridGeometry(dggrs, trs.getExtent(), trs, null);
    }

    public static DiscreteGlobalGridGeometry subZones(DiscreteGlobalGridReferenceSystem dggrs, Object[] baseZoneIds, Integer relativeDepth) {
        DiscreteGlobalGridTransform.SubZoneTransforms trs = DiscreteGlobalGridTransform.subZones(dggrs, baseZoneIds, relativeDepth);
        return new DiscreteGlobalGridGeometry(dggrs, trs.getExtent(), trs, null);
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
    public final Object[] getBaseZoneIds() {
        final CodeTransform gridToRS = getGridToRS();
        if (gridToRS instanceof DiscreteGlobalGridTransform t) {
            return t.getBaseZoneIds();
        }
        return null;
    }

    /**
     * @return can be null
     */
    public final Integer getRelativeDepth() {
        final CodeTransform gridToRS = getGridToRS();
        if (gridToRS instanceof DiscreteGlobalGridTransform t) {
            return t.getRelativeDepth();
        }
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
     * @return List of zone identifiers
     */
    public final List<Object> getZoneIds() {
        final CodeTransform gridToRS = getGridToRS();
        if (gridToRS instanceof DiscreteGlobalGridTransform t) {
            try {
                return t.getZoneIds();
            } catch (TransformException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        } else if (gridToRS instanceof CodeTransforms.Listed t) {
            return (List<Object>) t.getList();
        }
        return null;
    }

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

}

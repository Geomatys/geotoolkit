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
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometries.Geometry;
import org.apache.sis.geometries.operation.GeometryOperations;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.geotoolkit.storage.rs.internal.shared.CodeTransforms;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.geotoolkit.storage.rs.CodeTransform;

/**
 * DGGRS coverage geometry.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DiscreteGlobalGridGeometry extends CodedGeometry {

    //computed
    private Integer depth;
    private boolean bboxComputed = false;
    private GeographicBoundingBox bbox;

    /**
     * @param dggrs not null
     * @param zoneIds all ids are expected to be at the same depth level
     * @param bbox can be null
     */
    public DiscreteGlobalGridGeometry(DiscreteGlobalGridReferenceSystem dggrs, List<Object> zoneIds, GeographicBoundingBox bbox) {
        this(dggrs,
            (zoneIds == null) ? null : new GridExtent(null, 0, zoneIds.size(), false),
            (zoneIds == null) ? null : CodeTransforms.toTransform(dggrs, zoneIds), bbox);
    }

    public DiscreteGlobalGridGeometry(DiscreteGlobalGridReferenceSystem dggrs, GridExtent extent, CodeTransform trs, GeographicBoundingBox bbox) {
        super(dggrs, extent, trs, bbox);
        this.bbox = bbox;
        bboxComputed = bbox != null;
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

    @Override
    public Envelope getEnvelope() {
        final DiscreteGlobalGridReferenceSystem.Coder coder = getReferenceSystem().createCoder();
        GeneralEnvelope all = null;
        for (Object zid : getZoneIds()) {
            try {
                final Zone zone = coder.decode(zid);
                final Envelope env = zone.getEnvelope();
                if (env != null) {
                    if (all == null) {
                        all = new GeneralEnvelope(env);
                    } else {
                        all.add(env);
                    }
                }
            } catch (TransformException ex) {
                return null;
            }
        }
        return all;
    }

    @Override
    public Envelope getEnvelope(CoordinateReferenceSystem crs) throws TransformException {
        if (crs == null) return getEnvelope();

        MathTransform trs;
        try {
            trs = CRS.findOperation(CommonCRS.WGS84.normalizedGeographic(), crs, null).getMathTransform();
        } catch (FactoryException ex) {
            throw new TransformException(ex);
        }

        final DiscreteGlobalGridReferenceSystem.Coder coder = getReferenceSystem().createCoder();
        GeneralEnvelope all = null;
        for (Object zid : getZoneIds()) {
            try {
                final Zone zo = coder.decode(zid);
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
            } catch (TransformException ex) {
                return null;
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
    public synchronized GeographicBoundingBox getGeographicExtent() {
        if (bboxComputed) return bbox;
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
                bbox = null;
            }
        }
        return bbox;
    }

    /**
     * List of zones selected in the geometry.
     *
     * @return List of zone identifiers, never null
     */
    public List<Object> getZoneIds(){
        final CodeTransforms.Listed trs = (CodeTransforms.Listed) getGridToRS();
        return (List<Object>) trs.getList();
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

    @Override
    public int hashCode() {
        return 61 * super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}

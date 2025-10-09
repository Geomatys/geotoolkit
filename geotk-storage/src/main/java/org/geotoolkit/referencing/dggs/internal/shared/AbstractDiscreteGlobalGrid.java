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
package org.geotoolkit.referencing.dggs.internal.shared;

import org.apache.sis.referencing.operation.transform.AbstractMathTransform;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGrid;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractDiscreteGlobalGrid<T extends AbstractDiscreteGlobalGridHierarchy> implements DiscreteGlobalGrid {

    protected final T hierarchy;
    protected final int level;

    //cache transforms
    private CoordinateReferenceSystem crs;
    private int crsDim;
    private MathTransform crsToId;
    private MathTransform idToCrs;

    public AbstractDiscreteGlobalGrid(T hierarchy, int level) {
        this.hierarchy = hierarchy;
        this.level = level;
    }

    @Override
    public final int getRefinementLevel() {
        return level;
    }

    /**
     * Create the long identifer to/from transforms once.
     * @return true if suceeded
     */
    private synchronized boolean initTransform() {
        if (crs != null) return crsToId != null;

        crs = hierarchy.dggrs.getGridSystem().getCrs();
        crsDim = crs.getCoordinateSystem().getDimension();
        if (!hierarchy.dggrs.getZonalSystem().supportUInt64Form()) return false;

        idToCrs = new AbstractMathTransform() {
            @Override
            public int getSourceDimensions() {
                return 1;
            }

            @Override
            public int getTargetDimensions() {
                return crsDim;
            }

            @Override
            public Matrix transform(double[] source, int soffset, double[] target, int toffset, boolean bln) throws TransformException {
                final long zone = getZoneLongIdentifier(source, soffset);
                target[toffset] = Double.longBitsToDouble(zone);
                return null;
            }
        };
        crsToId = new AbstractMathTransform() {
            @Override
            public int getSourceDimensions() {
                return crsDim;
            }

            @Override
            public int getTargetDimensions() {
                return 1;
            }

            @Override
            public Matrix transform(double[] source, int soffset, double[] target, int toffset, boolean bln) throws TransformException {
                getZonePosition(Double.doubleToRawLongBits(source[soffset]), target, toffset);
                return null;
            }
        };

        return true;
    }

    @Override
    public final MathTransform createTransformToCrs() throws UnsupportedOperationException {
        if (initTransform()) throw new UnsupportedOperationException();
        return idToCrs;
    }

    @Override
    public final MathTransform createTransformToIdentifiers() throws UnsupportedOperationException {
        if (initTransform()) throw new UnsupportedOperationException();
        return crsToId;
    }

    /**
     * Convert on coordinate in base CRS to zone long identifier.
     */
    protected abstract long getZoneLongIdentifier(double[] source, int soffset);

    /**
     * Convert zone long identifier to coordinate in base CRS.
     */
    protected abstract void getZonePosition(long zoneId, double[] target, int toffset);

}

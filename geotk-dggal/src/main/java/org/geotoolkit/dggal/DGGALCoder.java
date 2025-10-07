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
package org.geotoolkit.dggal;

import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import org.apache.sis.measure.Quantities;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.Utilities;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class DGGALCoder extends DiscreteGlobalGridReferenceSystem.Coder{

    private final DGGALDggrs dggrs;
    private final CoordinateReferenceSystem baseCrs;
    private int level = 0;


    public DGGALCoder(DGGALDggrs dggrs) {
        this.dggrs = dggrs;
        this.baseCrs = this.dggrs.getGridSystem().getCrs();
    }

    @Override
    public DGGALDggrs getReferenceSystem() {
        return dggrs;
    }

    @Override
    public Quantity<?> getPrecision(DirectPosition dp) {
        double area;
        try {
            area = dggrs.dggal.getRefZoneArea(level);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
        return Quantities.create(Math.sqrt(area), Units.METRE);
    }

    @Override
    public void setPrecision(Quantity<?> qnt, DirectPosition dp) throws IncommensurableException {
        final double v = qnt.getValue().doubleValue();
        int level;
        try {
            level = dggrs.dggal.getLevelFromRefZoneArea(v*v);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
        setPrecisionLevel(level);
    }

    @Override
    public int getPrecisionLevel() {
        return level;
    }

    @Override
    public void setPrecisionLevel(int level) throws IncommensurableException {
        this.level = level;
    }

    @Override
    public String encode(DirectPosition dp) throws TransformException {
        return idAsText(encodeIdentifier(dp));
    }

    @Override
    public Long encodeIdentifier(DirectPosition dp) throws TransformException {
        final CoordinateReferenceSystem dpcrs = dp.getCoordinateReferenceSystem();
        if (dpcrs != null && !Utilities.equalsIgnoreMetadata(baseCrs, dpcrs)) {
            MathTransform trs;
            try {
                trs = CRS.findOperation(dpcrs, baseCrs, null).getMathTransform();
                dp = trs.transform(dp, null);
            } catch (FactoryException ex) {
                throw new TransformException(ex.getMessage(), ex);
            }
        }

        try {
            return dggrs.dggal.getZoneFromWGS84Centroid(level, new double[]{
                Math.toRadians(dp.getCoordinate(1)),
                Math.toRadians(dp.getCoordinate(0))});
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    final String idAsText(final long hash) {
        try {
            return dggrs.dggal.getZoneTextID(hash);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    final long idAsLong(final CharSequence cs) {
        try {
            return dggrs.dggal.getZoneFromTextID(cs.toString());
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}

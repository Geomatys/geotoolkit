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
package org.geotoolkit.dggs.s2;

import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;
import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import javax.measure.Unit;
import org.apache.sis.measure.Quantities;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.Utilities;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class S2Coder extends DiscreteGlobalGridReferenceSystem.Coder{

    private final S2Dggrs dggrs;
    private final CoordinateReferenceSystem baseCrs;
    private int level = 0;
    /**
     * In meters
     */
    private double[] precisionsPerLevel;


    public S2Coder(S2Dggrs dggrs) {
        this.dggrs = dggrs;
        this.baseCrs = this.dggrs.getGridSystem().getCrs();
    }

    @Override
    public S2Dggrs getReferenceSystem() {
        return dggrs;
    }

    private void computePrecisions() {
        if (precisionsPerLevel != null) return;

        final GeographicCRS gcrs = (GeographicCRS) baseCrs;
        final Ellipsoid ellipsoid = gcrs.getDatum().getEllipsoid();
        final double semiMajorAxis = ellipsoid.getSemiMajorAxis();
        final double semiMinorAxis = ellipsoid.getSemiMinorAxis();
        final double r = (semiMajorAxis + semiMinorAxis) / 2;
        final double surfaceArea = 4.0 * Math.PI * r * r;

        final double[] array = new double[15];
        array[0] = Math.sqrt(surfaceArea /6);
        for (int i = 1; i < array.length; i++) {
            array[i] = array[i-1] / Math.sqrt(7);
        }
        precisionsPerLevel = array;
    }

    @Override
    public Quantity<?> getPrecision(DirectPosition dp) {
        computePrecisions();
        return Quantities.create(precisionsPerLevel[level], Units.METRE);
    }

    @Override
    public void setPrecision(Quantity<?> qnt, DirectPosition dp) throws IncommensurableException {
        computePrecisions();
        final Quantity<?> q = qnt.to((Unit)Units.METRE);
        double searched = q.getValue().doubleValue();
        int bestMatch = 0;
        for (int l = 0; l < precisionsPerLevel.length; l++) {
            if (searched < precisionsPerLevel[l]) {
                bestMatch = l;
            } else {
                break;
            }
        }
        setPrecisionLevel(bestMatch);
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
        return S2Dggh.idAsText(encodeIdentifier(dp));
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
        S2CellId cid = S2CellId.fromLatLng(S2LatLng.fromDegrees(dp.getCoordinate(1), dp.getCoordinate(0)));
        return cid.parent(level).id();
    }

}

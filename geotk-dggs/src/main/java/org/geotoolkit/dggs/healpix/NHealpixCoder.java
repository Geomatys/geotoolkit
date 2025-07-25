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
package org.geotoolkit.dggs.healpix;

import cds.healpix.HashComputer;
import cds.healpix.Healpix;
import cds.healpix.HealpixNested;
import cds.healpix.HealpixNestedPolygonComputer;
import java.util.stream.Stream;
import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import javax.measure.Unit;
import org.apache.sis.measure.Quantities;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.Utilities;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.dggs.Zone;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
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
final class NHealpixCoder extends DiscreteGlobalGridReferenceSystem.Coder{

    private final NHealpixDggrs dggrs;
    private final CoordinateReferenceSystem baseCrs;
    private int level = 0;

    //cache
    private HealpixNested healpixNested = Healpix.getNested(level);
    private HashComputer hashComputer = healpixNested.newHashComputer();
    private HealpixNestedPolygonComputer polygonComputer = healpixNested.newPolygonComputer();
    /**
     * In meters
     */
    private double[] precisionsPerLevel;

    public NHealpixCoder(NHealpixDggrs dggrs) {
        this.dggrs = dggrs;
        this.baseCrs = this.dggrs.getGridSystem().getCrs();
    }

    @Override
    public NHealpixDggrs getReferenceSystem() {
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

        final double[] array = new double[Healpix.DEPTH_MAX];
        array[0] = Math.sqrt(surfaceArea / 12); //12 root cells
        for (int i = 1; i < array.length; i++) {
            array[i] = array[i-1] / 2;
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
        //update caches
        healpixNested = Healpix.getNested(level);
        hashComputer = healpixNested.newHashComputer();
        polygonComputer = healpixNested.newPolygonComputer();
    }

    @Override
    public Stream<Zone> intersect(Envelope env) throws TransformException {
        return super.intersect(env);


        //Bugged, raises assertion errors
        /*
        final double minX = env.getMinimum(0);
        final double minY = env.getMinimum(1);
        final double maxX = env.getMaximum(0);
        final double maxY = env.getMaximum(1);

        //api does not say, but when looking at the code, first point should not be duplicated at the end of the list
        final HealpixNestedBMOC candidate = polygonComputer.overlappingCenters(new double[][]{
                {minX, minY},
                {minX, maxY},
                {maxX, maxY},
                {maxX, minY}});
        final List<Zone> zones = new ArrayList<>();
        final Iterator<HealpixNestedBMOC.CurrentValueAccessor> iterator = candidate.iterator();
        while (iterator.hasNext()) {
            final HealpixNestedBMOC.CurrentValueAccessor acc = iterator.next();
            final NHealpixZone z = new NHealpixZone(dggrs, acc.getDepth(), acc.getHash());
            if (z.getOrder() != level) {
                zones.addAll(z.getChildrenAtRelativeDepth(level-z.getOrder()).toList());
            } else {
                zones.add(z);
            }
        }
        return zones.stream();
        */
    }

    @Override
    public String idToText(long hash) {
        return Long.toUnsignedString(hash);
    }

    @Override
    public long idToNumeric(CharSequence cs) {
        return Long.parseUnsignedLong(cs.toString());
    }

    @Override
    public long encodeNumeric(DirectPosition dp) throws TransformException {
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

        double lon = Math.toRadians(dp.getCoordinate(0));
        double lat = Math.toRadians(dp.getCoordinate(1));
        if (lon < 0) lon += Math.PI + Math.PI;

        final long hash = hashComputer.hash(lon, lat);
        return FitsSerialization.getHash(level+1, hash);
    }

    @Override
    public Zone decode(long hash) throws TransformException {
        return new NHealpixZone(dggrs, hash);
    }
}

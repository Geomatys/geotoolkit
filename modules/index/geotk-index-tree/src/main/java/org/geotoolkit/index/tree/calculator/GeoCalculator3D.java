/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.index.tree.calculator;

import org.geotoolkit.geometry.GeneralEnvelope;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getMedian;
import org.geotoolkit.index.tree.Node;
import static org.geotoolkit.index.tree.Node.*;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 * Define a 3 dimension geographical Calculator.
 *
 * @author Rémi Maréchal (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public class GeoCalculator3D extends GeoCalculator{

    public GeoCalculator3D(double radius, int... dims) {
        super(radius, dims);
    }
    
    /**
     * Find {@code DirectPosition} Hilbert coordinate from this Node.
     *
     * @param pt {@code DirectPosition}
     * @throws IllegalArgumentException if parameter "dPt" is out of this node
     * boundary.
     * @throws IllegalArgumentException if parameter dPt is null.
     * @return int[] table of length 3 which contains 3 coordinates.
     */
    public static int[] getHilbCoord(final Node candidate, final DirectPosition dPt, final Envelope envelop, final int hilbertOrder) {
        ArgumentChecks.ensureNonNull("DirectPosition dPt : ", dPt);
        if (!new GeneralEnvelope(envelop).contains(dPt)) {
            throw new IllegalArgumentException("Point is out of this node boundary");
        }
        final Calculator calc = candidate.getTree().getCalculator();
        assert calc instanceof GeoCalculator3D : "getHilbertCoord : GeoCalculator3D type required";
        final double div  = 2 << hilbertOrder - 1;
        final double divX = envelop.getSpan(0) / div;
        final double divY = envelop.getSpan(1) / div;
        final double divZ = envelop.getSpan(2) / div;
        double hdx        = (Math.abs(dPt.getOrdinate(0) - envelop.getLowerCorner().getOrdinate(0)) / divX);
        double hdy        = (Math.abs(dPt.getOrdinate(1) - envelop.getLowerCorner().getOrdinate(1)) / divY);
        double hdz        = (Math.abs(dPt.getOrdinate(2) - envelop.getLowerCorner().getOrdinate(2)) / divZ);
        int hx      = (int) hdx; 
        int hy      = (int) hdy;
        int hz      = (int) hdz;
        if (hx == div) hx--;
        if (hy == div) hy--;
        if (hz == div) hz--;

        if (calc.getSpace(envelop) <= 0) {
            int index = -1;
            for (int i = 0; i < 3; i++) {
                if (envelop.getSpan(i) <= 0) {
                    index = i;break;
                }
            }
            switch (index) {
                case 0  : return new int[]{hy, hz};
                case 1  : return new int[]{hx, hz};
                case 2  : return new int[]{hx, hy};
                default : throw new IllegalStateException("hilbertCoord not find");
            }
        }else{
            return new int[]{hx, hy, hz};
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getHVOfEntry(final Node candidate, final Envelope entry) {
        ArgumentChecks.ensureNonNull("impossible to define Hilbert coordinate with null entry", entry);
        final DirectPosition ptCE   = getMedian(entry);
        final GeneralEnvelope bound = new GeneralEnvelope(candidate.getBoundary());
        final int order             = (Integer) candidate.getUserProperty(PROP_HILBERT_ORDER);
        final int dimH              = 2 << order - 1;
        if (! bound.contains(ptCE)) throw new IllegalArgumentException("entry is out of this node boundary");
        if (getSpace(bound) <= 0) {
            if (getEdge(bound) <= 0) {
                final int nbCells = 2 << 2 * order - 1;
                int index = -1;
                for (int i = 0, d = bound.getDimension(); i<d; i++) {
                    if (bound.getSpan(i) > 0) {
                        index = i; break;
                    }
                }
                final double fract  = bound.getSpan(index) / nbCells;
                final double lenght = Math.abs(bound.getLowerCorner().getOrdinate(index) - ptCE.getOrdinate(index));
                int result          = (int) (lenght / fract);
                if (result == nbCells) result--;
                return result;
            }
            int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
            return ((int[]) candidate.getUserProperty(PROP_HILBERT_TABLE))[hCoord[0] + hCoord[1] * dimH];
        }
        int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
        return ((int[]) candidate.getUserProperty(PROP_HILBERT_TABLE))[hCoord[0] + hCoord[1] * dimH + hCoord[2] * (dimH << 1)];
    }
}

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

import java.util.List;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getMedian;
import org.geotoolkit.index.tree.Node;
import static org.geotoolkit.index.tree.Node.*;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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
     * {@inheritDoc }
     */
    @Override
    public void createBasicHL(final Node candidate, final int order, final Envelope bound) throws MismatchedDimensionException {
        ArgumentChecks.ensurePositive("impossible to create Hilbert Curve with negative indice", order);
        candidate.getChildren().clear();
        final CoordinateReferenceSystem crs = bound.getCoordinateReferenceSystem();
        final List<DirectPosition> listOfCentroidChild = (List<DirectPosition>) candidate.getUserProperty(PROP_CENTROIDS);
        listOfCentroidChild.clear();
        candidate.setUserProperty(PROP_ISLEAF, true);
        candidate.setUserProperty(PROP_HILBERT_ORDER, order);
        candidate.setBound(bound);
        final List<Node> listN = candidate.getChildren();
        listN.clear();
        if (order > 0) {
            final int dim = 2<<((Integer) candidate.getUserProperty(PROP_HILBERT_ORDER))-1;
            if (getSpace(bound) <= 0) {
                final int nbCells2D = 2<<(2*order-1);
                if (getEdge(bound) <= 0) {
                    int index = -1;
                    for (int i = 0; i<3; i++) {
                        if (bound.getSpan(i) > 0) {
                            index = i;break;
                        }
                    }
                    final double fract = bound.getSpan(index)/(2*nbCells2D);
                    final double valMin = bound.getLowerCorner().getOrdinate(index);
                    final DirectPosition dpt = new GeneralDirectPosition(crs);
                    for(int i = 1; i<2*nbCells2D; i+= 2){
                        for (int j = 0; j<bound.getDimension(); j++) {
                            if(j!=index)dpt.setOrdinate(j, bound.getMedian(j));
                        }
                        dpt.setOrdinate(index, valMin + i * fract);
                        listOfCentroidChild.add(dpt);
                    }
                    int[] groundZero = new int[nbCells2D];
                    for (int i = 0, s = listOfCentroidChild.size(); i < s; i++) {
                        groundZero[i] = i;
                        listN.add(HilbertRTree.createCell(candidate.getTree(), candidate, listOfCentroidChild.get(i), i, null));
                    }
                    candidate.setUserProperty(PROP_HILBERT_TABLE, groundZero);

                }else{
                    int index = -1;
                    for (int i = 0; i<3; i++) {
                        if (bound.getSpan(i) <= 0) {
                            index = i;break;
                        }
                    }
                    int[][] tabHV = new int[dim][dim];
                    int  d0, d1;
                    switch(index){
                        case 0  : d0 = 1; d1 = 2; break;//defined on yz plan
                        case 1  : d0 = 0; d1 = 2; break;//defined on xz
                        case 2  : d0 = 0; d1 = 1; break;//defined on xy
                        default : throw new IllegalStateException("invalid no space index : "+index);
                    }
                    listOfCentroidChild.addAll(createPath(candidate, order, d0, d1));
                    for (int i = 0, s = listOfCentroidChild.size(); i < s; i++) {
                        final DirectPosition ptCTemp = listOfCentroidChild.get(i);
                        ArgumentChecks.ensureNonNull("the crs ptCTemp", ptCTemp.getCoordinateReferenceSystem());
                        int[] tabTemp = getHilbCoord(candidate, ptCTemp, bound, order);
                        tabHV[tabTemp[0]][tabTemp[1]] = i;
                        listN.add(HilbertRTree.createCell(candidate.getTree(), candidate, ptCTemp, i, null));
                    }
                    candidate.setUserProperty(PROP_HILBERT_TABLE, tabHV);
                }

            } else {

                int[][][] tabHV = new int[dim][dim][dim];

                listOfCentroidChild.addAll(createPath(candidate, order, 0, 1,2));

                for (int i = 0, s = listOfCentroidChild.size(); i < s; i++) {
                    final DirectPosition ptCTemp = listOfCentroidChild.get(i);
                    ArgumentChecks.ensureNonNull("the crs ptCTemp", ptCTemp.getCoordinateReferenceSystem());
                    int[] tabTemp = getHilbCoord(candidate, ptCTemp, bound, order);
                    tabHV[tabTemp[0]][tabTemp[1]][tabTemp[2]] = i;
                    listN.add(HilbertRTree.createCell(candidate.getTree(), candidate, ptCTemp, i, null));
                }
                candidate.setUserProperty(PROP_HILBERT_TABLE, tabHV);
            }
        } else {
            listOfCentroidChild.add(new GeneralDirectPosition(getMedian(bound)));
            listN.add(HilbertRTree.createCell(candidate.getTree(), candidate, listOfCentroidChild.get(0), 0, null));
        }
        candidate.setBound(bound);
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
        final double div = 2<<hilbertOrder-1;
        final double divX = envelop.getSpan(0) / div;
        final double divY = envelop.getSpan(1) / div;
        final double divZ = envelop.getSpan(2) / div;
        double hdx = (Math.abs(dPt.getOrdinate(0) - envelop.getLowerCorner().getOrdinate(0)) / divX);
        double hdy = (Math.abs(dPt.getOrdinate(1) - envelop.getLowerCorner().getOrdinate(1)) / divY);
        double hdz = (Math.abs(dPt.getOrdinate(2) - envelop.getLowerCorner().getOrdinate(2)) / divZ);
        final int hx = (hdx <= 1) ? 0 : 1;
        final int hy = (hdy <= 1) ? 0 : 1;
        final int hz = (hdz <= 1) ? 0 : 1;

        if (calc.getSpace(envelop) <= 0) {
            int index = -1;
            for (int i = 0; i<3; i++) {
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
        final DirectPosition ptCE = getMedian(entry);
        final GeneralEnvelope bound = new GeneralEnvelope(candidate.getBoundary());
        final int order = (Integer) candidate.getUserProperty(PROP_HILBERT_ORDER);
        if (! bound.contains(ptCE)) throw new IllegalArgumentException("entry is out of this node boundary");
        if (getSpace(bound) <= 0) {
            if (getEdge(bound) <= 0) {
                final int nbCells = 2 << 2*order-1;
                int index = -1;
                for (int i = 0, d = bound.getDimension(); i<d; i++) {
                    if (bound.getSpan(i) > 0) {
                        index = i; break;
                    }
                }
                final double fract = bound.getSpan(index) / nbCells;
                final double lenght = Math.abs(bound.getLowerCorner().getOrdinate(index) - ptCE.getOrdinate(index));
                int result = (int) (lenght / fract);
                if (result == nbCells) result--;
                return result;
            }
            int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
            return ((int[][]) candidate.getUserProperty(PROP_HILBERT_TABLE))[hCoord[0]][hCoord[1]];
        }
        int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
        return ((int[][][]) candidate.getUserProperty(PROP_HILBERT_TABLE))[hCoord[0]][hCoord[1]][hCoord[2]];
    }
}

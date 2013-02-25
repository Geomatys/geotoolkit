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

import java.util.ArrayList;
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
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Define a two dimension geographical Calculator.
 *
 * @author Rémi Maréchal (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public class GeoCalculator2D extends GeoCalculator{

    public GeoCalculator2D(double radius, int... dims) {
        super(radius, dims);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void createBasicHL(final Node candidate, final int order, final Envelope bound) {
        ArgumentChecks.ensurePositive("impossible to create Hilbert Curve with negative indice", order);
        candidate.getChildren().clear();
        final CoordinateReferenceSystem crs = bound.getCoordinateReferenceSystem();
        final List<DirectPosition> listOfCentroidChild = new ArrayList<DirectPosition>();
        candidate.setUserProperty(PROP_ISLEAF, true);
        candidate.setUserProperty(PROP_HILBERT_ORDER, order);
        candidate.setBound(bound);
        final List<Node> listN = candidate.getChildren();
        listN.clear();
        if (order > 0) {
            final double width  = bound.getSpan(dims[0]);
            final double height = bound.getSpan(dims[1]);
            final int dimH      = 2 << order - 1;
            int[] tabHV         = new int[dimH << 1];

            double fract, ymin, xmin;
            final int nbCells = 2 << (2 * order - 1);
            if (width * height <= 0) {
                if (width <= 0) {
                    fract = height / (2 * nbCells);
                    ymin  = bound.getLowerCorner().getOrdinate(dims[1]);
                    xmin  = bound.getLowerCorner().getOrdinate(dims[0]);
                    for (int i = 1; i < 2 * nbCells; i += 2) {
                        final DirectPosition dpt = new GeneralDirectPosition(crs);
                        dpt.setOrdinate(dims[0], xmin);
                        dpt.setOrdinate(dims[1], ymin + i * fract);
                        listOfCentroidChild.add(dpt);
                    }
                } else {
                    fract = width / (2 * nbCells);
                    ymin  = bound.getLowerCorner().getOrdinate(dims[1]);
                    xmin  = bound.getLowerCorner().getOrdinate(dims[0]);
                    for (int i = 1; i < 2 * nbCells; i += 2) {
                        final DirectPosition dpt = new GeneralDirectPosition(crs);
                        dpt.setOrdinate(dims[0], xmin + i * fract);
                        dpt.setOrdinate(dims[1], ymin);
                        listOfCentroidChild.add(dpt);
                    }
                }
                int[] groundZero = new int[nbCells];
                for (int i = 0, s = listOfCentroidChild.size(); i < s; i++) {
                    groundZero[i] = i;
                    listN.add(HilbertRTree.createCell(candidate.getTree(), candidate, listOfCentroidChild.get(i), i, null));
                }
                candidate.setUserProperty(PROP_HILBERT_TABLE, groundZero);
            } else {
                listOfCentroidChild.addAll(createPath(candidate, order, dims[0], dims[1]));
                for (int i = 0, s = listOfCentroidChild.size(); i < s; i++) {
                    final DirectPosition ptCTemp = listOfCentroidChild.get(i);
                    ArgumentChecks.ensureNonNull("the crs ptCTemp", ptCTemp.getCoordinateReferenceSystem());
                    int[] tabTemp = getHilbCoord(candidate, ptCTemp, bound, order);
                    tabHV[tabTemp[0] + tabTemp[1] * dimH] = i;
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
     * @return int[] table of length 2 which contains two coordinates.
     */
    public int[] getHilbCoord(final Node candidate, final DirectPosition dPt, final Envelope envelop, final int hilbertOrder) {
        ArgumentChecks.ensureNonNull("DirectPosition dPt : ", dPt);
        if (!new GeneralEnvelope(envelop).contains(dPt)) {
            throw new IllegalArgumentException("Point is out of this node boundary");
        }
        final double div  = 2 << (hilbertOrder - 1);
        final double divX = envelop.getSpan(dims[0]) / div;
        final double divY = envelop.getSpan(dims[1]) / div;
        final double hdx  = (Math.abs(dPt.getOrdinate(dims[0]) - envelop.getLowerCorner().getOrdinate(dims[0])) / divX);
        final double hdy  = (Math.abs(dPt.getOrdinate(dims[1]) - envelop.getLowerCorner().getOrdinate(dims[1])) / divY);
        final int hx      = (hdx <= 1) ? 0 : 1;
        final int hy      = (hdy <= 1) ? 0 : 1;
        return new int[]{hx, hy};
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getHVOfEntry(final Node candidate, final Envelope entry) {
        ArgumentChecks.ensureNonNull("impossible to define Hilbert coordinate with null entry", entry);
        final DirectPosition ptCE = getMedian(entry);
        final GeneralEnvelope bound = new GeneralEnvelope(candidate.getBoundary());
        if (! bound.contains(ptCE)) {
            throw new IllegalArgumentException("entry is out of this node boundary");
        }
        final Calculator calc = candidate.getTree().getCalculator();
        final int order       = (Integer) candidate.getUserProperty(PROP_HILBERT_ORDER);
        final int dimH        = 2 << order - 1; 
        if (calc.getSpace(bound) <= 0) {
            final double w      = bound.getSpan(dims[0]);
            final double h      = bound.getSpan(dims[1]);
            final int ordinate  = (w > h) ? 0 : 1;
            final int nbCells   = 2 << (2 * order - 1);
            final double fract  = bound.getSpan(ordinate) / nbCells;
            final double lenght = Math.abs(bound.getLower(ordinate) - ptCE.getOrdinate(ordinate));
            int result          = (int) (lenght / fract);
            if (result == nbCells)result--;
            return result;
        } else {
            int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
            return ((int[]) candidate.getUserProperty(PROP_HILBERT_TABLE))[hCoord[0] + hCoord[1] * dimH];
        }
    }

}

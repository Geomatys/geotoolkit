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
package org.geotoolkit.index.tree;

import java.util.List;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Class which contains tree test utils methods.
 *
 * @author Rémi Maréchal (Géomatys).
 */
public abstract class TreeTest {

    /**
     * Compare 2 lists elements.
     *
     * <blockquote><font size=-1> <strong> NOTE: return {@code true} if listA
     * and listB are empty. </strong> </font></blockquote>
     *
     * @param listA
     * @param listB
     * @throws IllegalArgumentException if listA or ListB is null.
     * @return true if listA contains same elements from listB.
     */
    protected boolean compareList(final List listA, final List listB) {
        ArgumentChecks.ensureNonNull("compareList : listA", listA);
        ArgumentChecks.ensureNonNull("compareList : listB", listB);

        if (listA.isEmpty() && listB.isEmpty()) return true;
        if (listA.size() != listB.size()) return false;

        boolean shapequals = false;
        for (Object objA : listA) {
            final Envelope shs = (Envelope) objA;
            for (Object objB : listB) {
                final Envelope shr = (Envelope) objB;
                if (new GeneralEnvelope(shs).equals(shr, 1E-9, false)) {
                    shapequals = true;
                    break;
                }
            }
            if (!shapequals) return false;
            shapequals = false;
        }
        return true;
    }
    
    /**
     * Return boundary of all element union from list parameter.
     * 
     * @param list
     * @return boundary of all elements union from list parameter.
     */
    protected double[] getEnvelopeMin(final List<Envelope> list) {
        ArgumentChecks.ensureNonNull("compareList : listA", list);
        assert(!list.isEmpty()):"list to get envelope min should not be empty.";
        final double[] ge = DefaultTreeUtils.getCoords(list.get(0));
        for (int i = 1; i < list.size();i++) {
            DefaultTreeUtils.add(ge, DefaultTreeUtils.getCoords(list.get(i)));
        }
        return ge;
    }
    
    /**
     * Find all entries number in a {@link Tree}.
     * 
     * @param tree where to looking for entries.
     * @return all entries number in a {@link Tree}.
     */
    protected boolean checkTreeElts(Tree tree) {
        final int treeElement = tree.getElementsNumber();
        if (tree instanceof HilbertRTree) {
            return DefaultTreeUtils.countEltsInHilbertNode(tree.getRoot(), 0) == treeElement;
        }
        return DefaultTreeUtils.countElementsRecursively(tree.getRoot(), 0) == treeElement;
    }
    

    /**
     * Create a default adapted test entry({@code GeneralEnvelope}).
     *
     * @param position the median of future entry.
     * @return {@code GeneralEnvelope} entry.
     */
    public static GeneralEnvelope createEntry(final DirectPosition position) {
        final double[] coord = position.getCoordinate();
        final int length = coord.length;
        final double[] coordLow = new double[length];
        final double[] coordUpp = new double[length];
        for (int i = 0; i < length; i++) {
            coordLow[i] = coord[i] - (Math.random() * 5 + 5);
            coordUpp[i] = coord[i] + (Math.random() * 5 + 5);
        }
        final CoordinateReferenceSystem crs = position.getCoordinateReferenceSystem();
        if (crs == null) return new GeneralEnvelope(new GeneralDirectPosition(coordLow), new GeneralDirectPosition(coordUpp));
        final GeneralDirectPosition dpLow = new GeneralDirectPosition(crs);
        final GeneralDirectPosition dpUpp = new GeneralDirectPosition(crs);
        for (int i = 0; i < length; i++) {
            dpLow.setOrdinate(i, coordLow[i]);
            dpUpp.setOrdinate(i, coordUpp[i]);
        }
        return new GeneralEnvelope(dpLow, dpUpp);
    }
}

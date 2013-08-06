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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.geotoolkit.index.tree.hilbert.HilbertNode;
import org.opengis.geometry.Envelope;

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
    protected boolean compareLists(final List<double[]> listA, final List<double[]> listB) {
        ArgumentChecks.ensureNonNull("compareList : listA", listA);
        ArgumentChecks.ensureNonNull("compareList : listB", listB);

        if (listA.isEmpty() && listB.isEmpty()) return true;
        if (listA.size() != listB.size()) return false;

        boolean shapequals = false;
        for (double[] objA : listA) {
            for (double[] objB : listB) {
                if (Arrays.equals(objA, objB)) {
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
        final double[] ge = TreeUtilities.getCoords(list.get(0));
        for (int i = 1; i < list.size();i++) {
            TreeUtilities.add(ge, TreeUtilities.getCoords(list.get(i)));
        }
        return ge;
    }
    
    /**
     * Return boundary of all element union from list parameter.
     * 
     * @param list
     * @return boundary of all elements union from list parameter.
     */
    protected double[] getExtent(final List<double[]> list) {
        ArgumentChecks.ensureNonNull("compareList : listA", list);
        assert(!list.isEmpty()):"list to get envelope min should not be empty.";
        final double[] ge = list.get(0).clone();
        for (int i = 1; i < list.size(); i++) {
            TreeUtilities.add(ge, list.get(i));
        }
        return ge;
    }
    
    /**
     * Find all entries number in a {@link Tree}.
     * 
     * @param tree where to looking for entries.
     * @return all entries number in a {@link Tree}.
     */
    protected boolean checkTreeElts(Tree tree) throws IOException {
        final int treeElement = tree.getElementsNumber();
        if (tree instanceof HilbertRTree) {
            return TreeUtilities.countEltsInHilbertNode((HilbertNode)tree.getRoot(), 0) == treeElement;
        }
        return TreeUtilities.countElementsRecursively(tree.getRoot(), 0) == treeElement;
    }
    

    /**
     * Create a default adapted test entry({@code GeneralEnvelope}).
     *
     * @param position the median of future entry.
     * @return {@code GeneralEnvelope} entry.
     */
    public static double[] createEntry(final double[] position) {
        final int length = position.length;
        final double[] envelope = new double[length << 1];
        for (int i = 0; i < length; i++) {
            envelope[i] = position[i] - (Math.random() * 5 + 5);
            envelope[i+length] = position[i] + (Math.random() * 5 + 5);
        }
        return envelope;
    }
}

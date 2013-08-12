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
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.filter.SpatialFilterType;
import static org.geotoolkit.filter.SpatialFilterType.*;
import static org.geotoolkit.index.tree.TreeUtilities.*;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Envelope;

/**
 * Logics operate.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public final class TreeX {

    private TreeX() {
    }
    
    /**
     * Effectuate different logics operations on tree.
     *
     * @param tree 
     * @param areaSearch area of search.
     * @param logicFilter different logic operation.
     * @see SpatialFilterType
     */
    public static int[] search(final Tree tree, final Envelope regionSearch, final SpatialFilterType logicFilter) throws StoreIndexException {
        ArgumentChecks.ensureNonNull("TreeX search : tree", tree);
        ArgumentChecks.ensureNonNull("TreeX search : Envelope", regionSearch);
        ArgumentChecks.ensureNonNull("TreeX search : SpatialFilterType", logicFilter);
        if (!CRS.equalsIgnoreMetadata(regionSearch.getCoordinateReferenceSystem(), tree.getCrs())) 
            throw new IllegalArgumentException("TreeX search : the 2 CRS within tree and region search should be equals.");
        TreeElementMapper tEM = tree.getTreeElementMapper();
        int[] tabSearch = tree.searchID(regionSearch);
        int[] tabResult = new int[tabSearch.length];
        int currentPosition = 0;
        try {
            switch (logicFilter) {
                case INTERSECTS : case BBOX : {
                    return tabSearch;
                } 
                case CONTAINS : {
                    for (int i = 0; i < tabSearch.length; i++) {
                        final Envelope env = (Envelope) tEM.getObjectFromTreeIdentifier(tabSearch[i]);
                        if (contains(getCoords(env), getCoords(regionSearch), true)) tabResult[currentPosition++] = tabSearch[i];
                    }
                } break;
                case DISJOINT : {
                    int[] tabRef;
                    final GeneralEnvelope env = new GeneralEnvelope(tree.getCrs());
                    env.setEnvelope(tree.getRoot().getBoundary());
                    tabRef = tree.searchID(env);
                    tabResult = new int[Math.max(tabSearch.length, tabRef.length)];
                    for(int i = 0; i < tabRef.length; i++) {
                        final Envelope envRef = (Envelope) tEM.getObjectFromTreeIdentifier(tabRef[i]);
                        boolean find = false;
                        for(int j = 0; j < tabSearch.length; j++) {
                            final Envelope envSearch = (Envelope) tEM.getObjectFromTreeIdentifier(tabSearch[j]);
                            if(envRef == envSearch){
                                find = true;
                                break;
                            }
                        }
                        if (!find) tabResult[currentPosition++] = tabRef[i];
                    }
                } break;
                case WITHIN : {
                    for ( int i = 0; i < tabSearch.length; i++) {
                        final Envelope env = (Envelope) tEM.getObjectFromTreeIdentifier(tabSearch[i]);
                        if (contains(getCoords(regionSearch), getCoords(env), true)) tabResult[currentPosition++] = tabSearch[i];
                    }
                } break;
                case TOUCHES : {
                    for(int i = 0; i < tabSearch.length; i++){
                        final Envelope env = (Envelope) tEM.getObjectFromTreeIdentifier(tabSearch[i]);
                        if (touches(getCoords(regionSearch), getCoords(env))) tabResult[currentPosition++] = tabSearch[i];
                    }
                } break;
                case EQUALS : {
                    for(int i = 0; i < tabSearch.length; i++){
                        final Envelope envelop = (Envelope) tEM.getObjectFromTreeIdentifier(tabSearch[i]);
                        final double[] env = getCoords(envelop);
                        if (arrayEquals(env, getCoords(regionSearch), 1E-9)) tabResult[currentPosition++] = tabSearch[i];
                    }
                } break;
                case OVERLAPS : {
                    for (int i = 0; i < tabSearch.length; i++) {
                        final Envelope envelop = (Envelope) tEM.getObjectFromTreeIdentifier(tabSearch[i]);
                        final double[] env = getCoords(envelop);
                        if (intersects(getCoords(regionSearch), env, false) 
                        && !contains(env, getCoords(regionSearch), true) 
                        && !contains(getCoords(regionSearch), env, true)) tabResult[currentPosition++] = tabSearch[i];
                    }
                } break;
                default : throw new IllegalStateException("not implemented yet");
            }
            return Arrays.copyOf(tabResult, currentPosition);
        } catch (IOException ex) {
            throw new StoreIndexException(ex);
        }
    }
}

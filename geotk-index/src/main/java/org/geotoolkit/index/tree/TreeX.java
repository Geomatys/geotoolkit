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
import static org.geotoolkit.internal.tree.TreeUtilities.*;
import org.opengis.geometry.Envelope;
import org.apache.sis.util.Utilities;

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
     * @return integer table which contain all tree identifier from data which match with search criterion. (area and logic filter)
     * @throws StoreIndexException if problem during search action.
     * @see SpatialFilterType
     * @see Tree#searchID(org.opengis.geometry.Envelope)
     */
    public static int[] search(final Tree tree, final Envelope regionSearch, final SpatialFilterType logicFilter) throws StoreIndexException {
        ArgumentChecks.ensureNonNull("TreeX search : tree", tree);
        ArgumentChecks.ensureNonNull("TreeX search : Envelope", regionSearch);
        ArgumentChecks.ensureNonNull("TreeX search : SpatialFilterType", logicFilter);
        if (!Utilities.equalsIgnoreMetadata(regionSearch.getCoordinateReferenceSystem(), tree.getCrs()))
            throw new IllegalArgumentException("TreeX search : the 2 CRS within tree and region search should be equals.");
        final TreeElementMapper tEM = tree.getTreeElementMapper();
        TreeIdentifierIterator iterSearch = tree.search(regionSearch);
        int tabResultLength = 100;
        int[] tabResult = new int[tabResultLength];
        int currentPosition = 0;
        try {
            switch (logicFilter) {
                case INTERSECTS : case BBOX : {
                    return tree.searchID(regionSearch);
                }
                case CONTAINS : {
                    while (iterSearch.hasNext()) {
                        final int currentTreeID = iterSearch.nextInt();
                         final Envelope env = tEM.getEnvelope(tEM.getObjectFromTreeIdentifier(currentTreeID));
                        if (contains(getCoords(env), getCoords(regionSearch), true)) {
                            if (currentPosition == tabResultLength) {
                                tabResultLength = tabResultLength << 1;
                                final int[] tabTemp = tabResult;
                                tabResult = new int[tabResultLength];
                                System.arraycopy(tabTemp, 0, tabResult, 0, currentPosition);
                            }
                            tabResult[currentPosition++] = currentTreeID;
                        }
                    }
                } break;
                case DISJOINT : {
                    final GeneralEnvelope treeExtends = new GeneralEnvelope(tree.getCrs());
                    treeExtends.setEnvelope(tree.getExtent());
                    final TreeIdentifierIterator allIter = tree.search(treeExtends);

                    while (allIter.hasNext()) {
                        final int currentTreeID = allIter.nextInt();
                         final Envelope env = tEM.getEnvelope(tEM.getObjectFromTreeIdentifier(currentTreeID));
                        if (!intersects(getCoords(env), getCoords(regionSearch), true)) {
                            if (currentPosition == tabResultLength) {
                                tabResultLength = tabResultLength << 1;
                                final int[] tabTemp = tabResult;
                                tabResult = new int[tabResultLength];
                                System.arraycopy(tabTemp, 0, tabResult, 0, currentPosition);
                            }
                            tabResult[currentPosition++] = currentTreeID;
                        }
                    }
                } break;
                case WITHIN : {

                    while (iterSearch.hasNext()) {
                        final int currentTreeID = iterSearch.nextInt();
                         final Envelope env = tEM.getEnvelope(tEM.getObjectFromTreeIdentifier(currentTreeID));
                        if (contains(getCoords(regionSearch), getCoords(env), true)) {
                            if (currentPosition == tabResultLength) {
                                tabResultLength = tabResultLength << 1;
                                final int[] tabTemp = tabResult;
                                tabResult = new int[tabResultLength];
                                System.arraycopy(tabTemp, 0, tabResult, 0, currentPosition);
                            }
                            tabResult[currentPosition++] = currentTreeID;
                        }
                    }
                } break;
                case TOUCHES : {
                    while (iterSearch.hasNext()) {
                        final int currentTreeID = iterSearch.nextInt();
                         final Envelope env = tEM.getEnvelope(tEM.getObjectFromTreeIdentifier(currentTreeID));
                        if (touches(getCoords(regionSearch), getCoords(env))) {
                            if (currentPosition == tabResultLength) {
                                tabResultLength = tabResultLength << 1;
                                final int[] tabTemp = tabResult;
                                tabResult = new int[tabResultLength];
                                System.arraycopy(tabTemp, 0, tabResult, 0, currentPosition);
                            }
                            tabResult[currentPosition++] = currentTreeID;
                        }
                    }
                } break;
                case EQUALS : {
                    while (iterSearch.hasNext()) {
                        final int currentTreeID = iterSearch.nextInt();
                         final Envelope env = tEM.getEnvelope(tEM.getObjectFromTreeIdentifier(currentTreeID));
                        if (arrayEquals(getCoords(env), getCoords(regionSearch), 1E-9)) {
                            if (currentPosition == tabResultLength) {
                                tabResultLength = tabResultLength << 1;
                                final int[] tabTemp = tabResult;
                                tabResult = new int[tabResultLength];
                                System.arraycopy(tabTemp, 0, tabResult, 0, currentPosition);
                            }
                            tabResult[currentPosition++] = currentTreeID;
                        }
                    }
                } break;
                case OVERLAPS : {

                    while (iterSearch.hasNext()) {
                        final int currentTreeID = iterSearch.nextInt();
                         final Envelope envelop = tEM.getEnvelope(tEM.getObjectFromTreeIdentifier(currentTreeID));
                         final double[] env = getCoords(envelop);
                        if (intersects(getCoords(regionSearch), env, false)
                        && !contains(env, getCoords(regionSearch), true)
                        && !contains(getCoords(regionSearch), env, true)) {
                            if (currentPosition == tabResultLength) {
                                tabResultLength = tabResultLength << 1;
                                final int[] tabTemp = tabResult;
                                tabResult = new int[tabResultLength];
                                System.arraycopy(tabTemp, 0, tabResult, 0, currentPosition);
                            }
                            tabResult[currentPosition++] = currentTreeID;
                        }
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

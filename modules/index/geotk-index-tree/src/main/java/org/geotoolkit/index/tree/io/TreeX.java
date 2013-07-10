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
package org.geotoolkit.index.tree.io;

import java.util.ArrayList;
import java.util.List;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.filter.SpatialFilterType;
import static org.geotoolkit.filter.SpatialFilterType.*;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
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

    /**Effectuate different logics operations on tree.
     *
     * @param tree
     * @param areaSearch
     * @param logicFilter different logic operation.
     * @param visitor
     * @see SpatialFilterType
     */
    @Deprecated
    public static void search(final Tree tree, final Envelope regionSearch, final SpatialFilterType logicFilter, final TreeVisitor visitor) {
        ArgumentChecks.ensureNonNull("TreeX search : tree", tree);
        ArgumentChecks.ensureNonNull("TreeX search : Envelope", regionSearch);
        ArgumentChecks.ensureNonNull("TreeX search : SpatialFilterType", logicFilter);
        ArgumentChecks.ensureNonNull("TreeX search : TreeVisitor", visitor);
        if (!CRS.equalsIgnoreMetadata(regionSearch.getCoordinateReferenceSystem(), tree.getCrs())) 
            throw new IllegalArgumentException("TreeX search : the 2 CRS within tree and region search should be equals.");
        search(tree, getCoords(regionSearch), logicFilter, visitor);
        
//        final List listSearch = new ArrayList<Envelope>();
//        final GeneralEnvelope areaSearch = new GeneralEnvelope(regionSearch);
//        TreeVisitor defVisitor = new DefaultTreeVisitor(listSearch);
//        switch(logicFilter){
//            case INTERSECTS : {
//                tree.search(regionSearch, visitor);
//            } break;
//            case BBOX : {
//                tree.search(regionSearch, visitor);
//            } break;
//            case CONTAINS : {
//                tree.search(areaSearch, defVisitor);
//                for(int i = 0; i < listSearch.size(); i++){
//                    final Envelope env = (Envelope) listSearch.get(i);
//                    if(new GeneralEnvelope(env).contains(areaSearch, true))visitor.visit(env);
//                }
//            } break;
//            case DISJOINT : {
//                tree.search(areaSearch, defVisitor);
//                final List listRef = new ArrayList<Envelope>();
//                final GeneralEnvelope rootBound = new GeneralEnvelope(tree.getCrs());
//                rootBound.setEnvelope(tree.getRoot().getBoundary());
//                tree.search(rootBound, new DefaultTreeVisitor(listRef));
//                for(int i = 0; i < listRef.size(); i++) {
//                    final Envelope envRef = (Envelope)listRef.get(i);
//                    boolean find = false;
//                    for(int j = 0; j < listSearch.size(); j++) {
//                        final Envelope envSearch = (Envelope) listSearch.get(j);
//                        if(envRef == envSearch){
//                            find = true;
//                            break;
//                        }
//                    }
//                    if(!find)visitor.visit(envRef);
//                }
//            } break;
//            case WITHIN : {
//                tree.search(areaSearch, defVisitor);
//                for(int i = 0; i < listSearch.size(); i++){
//                    final Envelope env = (Envelope) listSearch.get(i);
//                    if(areaSearch.contains(env, true))visitor.visit(env);
//                }
//            } break;
//            case TOUCHES : {
//                tree.search(areaSearch, defVisitor);
//                for(int i = 0; i < listSearch.size(); i++){
//                    final Envelope env = (Envelope) listSearch.get(i);
//                    final GeneralEnvelope ge = new GeneralEnvelope(env);
//                    if(ge.intersects(areaSearch, true)&&!ge.intersects(areaSearch, false))visitor.visit(env);
//                }
//            } break;
//
//            case EQUALS : {
//                tree.search(areaSearch, defVisitor);
//                for(int i = 0; i < listSearch.size(); i++){
//                    final Envelope env = (Envelope) listSearch.get(i);
//                    final GeneralEnvelope ge = new GeneralEnvelope(env);
//                    if(ge.equals(areaSearch, 1E-9, true))visitor.visit(env);
//                }
//            } break;
//            case OVERLAPS : {
//                tree.search(areaSearch, defVisitor);
//                for(int i = 0; i < listSearch.size(); i++){
//                    final Envelope env = (Envelope) listSearch.get(i);
//                    final GeneralEnvelope ge = new GeneralEnvelope(env);
//                    if(ge.intersects(areaSearch, false)&&!ge.contains(areaSearch, true)&&!areaSearch.contains(ge, true))visitor.visit(env);
//                }
//            } break;
//            default : throw new IllegalStateException("not implemented yet");
//        }
    }
    
    /**
     * Effectuate different logics operations on tree.
     *
     * @param tree
     * @param areaSearch
     * @param logicFilter different logic operation.
     * @param visitor
     * @see SpatialFilterType
     */
    public static void search(final Tree tree, final double[] regionSearch, final SpatialFilterType logicFilter, final TreeVisitor visitor) {
        final List listSearch = new ArrayList();
        TreeVisitor defVisitor = new DefaultTreeVisitor(listSearch);
        switch(logicFilter){
            case INTERSECTS : {
                tree.search(regionSearch, visitor);
            } break;
            case BBOX : {
                tree.search(regionSearch, visitor);
            } break;
            case CONTAINS : {
                tree.search(regionSearch, defVisitor);
                for(int i = 0; i < listSearch.size(); i++){
                    final Envelope env = (Envelope) listSearch.get(i);
                    if (contains(getCoords(env), regionSearch, true)) visitor.visit(env);
                }
            } break;
            case DISJOINT : {
                tree.search(regionSearch, defVisitor);
                final List listRef = new ArrayList<Envelope>();
                tree.search(tree.getRoot().getBoundary(), new DefaultTreeVisitor(listRef));
                for(int i = 0; i < listRef.size(); i++) {
                    final Envelope envRef = (Envelope)listRef.get(i);
                    boolean find = false;
                    for(int j = 0; j < listSearch.size(); j++) {
                        final Envelope envSearch = (Envelope) listSearch.get(j);
                        if(envRef == envSearch){
                            find = true;
                            break;
                        }
                    }
                    if(!find)visitor.visit(envRef);
                }
            } break;
            case WITHIN : {
                tree.search(regionSearch, defVisitor);
                for ( int i = 0; i < listSearch.size(); i++) {
                    final Envelope env = (Envelope) listSearch.get(i);
                    if (contains(regionSearch, getCoords(env), true)) visitor.visit(env);
                }
            } break;
            case TOUCHES : {
                tree.search(regionSearch, defVisitor);
                for(int i = 0; i < listSearch.size(); i++){
                    final Envelope env = (Envelope) listSearch.get(i);
                    if (touches(regionSearch, getCoords(env))) visitor.visit(env);
                }
            } break;

            case EQUALS : {
                tree.search(regionSearch, defVisitor);
                for(int i = 0; i < listSearch.size(); i++){
                    final Envelope envelop = (Envelope) listSearch.get(i);
                    final double[] env = getCoords(envelop);
                    if (arrayEquals(env, regionSearch, 1E-9)) visitor.visit(envelop);
                }
            } break;
            case OVERLAPS : {
                tree.search(regionSearch, defVisitor);
                for (int i = 0; i < listSearch.size(); i++) {
                    final Envelope envelop = (Envelope) listSearch.get(i);
                    final double[] env = getCoords(envelop);
                    if (intersects(regionSearch, env, false) 
                    && !contains(env, regionSearch, true) 
                    && !contains(regionSearch, env, true)) visitor.visit(envelop);
                }
            } break;
            default : throw new IllegalStateException("not implemented yet");
        }
    }
}

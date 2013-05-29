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
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.filter.SpatialFilterType;
import org.opengis.geometry.Envelope;

/**Logics operate.
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
    public static void search(final Tree tree, final Envelope regionSearch, final SpatialFilterType logicFilter, final TreeVisitor visitor) {
        final List<Envelope> listSearch = new ArrayList<Envelope>();
        final GeneralEnvelope areaSearch = new GeneralEnvelope(regionSearch);
        TreeVisitor defVisitor = new DefaultTreeVisitor(listSearch);
        switch(logicFilter){
            case INTERSECTS : {
                tree.search(regionSearch, visitor);
            } break;
            case BBOX : {
                tree.search(regionSearch, visitor);
            } break;
            case CONTAINS : {
                tree.search(areaSearch, defVisitor);
                for(Envelope env : listSearch){
                    if(new GeneralEnvelope(env).contains(areaSearch, true))visitor.visit(env);
                }
            } break;
            case DISJOINT : {
                tree.search(areaSearch, defVisitor);
                final List<Envelope> listRef = new ArrayList<Envelope>();
                tree.search(tree.getRoot().getBoundary(), new DefaultTreeVisitor(listRef));
                for(Envelope envRef : listRef){
                    boolean find = false;
                    for(Envelope envSearch : listSearch){
                        if(envRef == envSearch){
                            find = true;
                            break;
                        }
                    }
                    if(!find)visitor.visit(envRef);
                }
            } break;
            case WITHIN : {
                tree.search(areaSearch, defVisitor);
                for(Envelope env : listSearch){
                    if(areaSearch.contains(env, true))visitor.visit(env);
                }
            } break;
            case TOUCHES : {
                tree.search(areaSearch, defVisitor);
                for(Envelope env : listSearch){
                    final GeneralEnvelope ge = new GeneralEnvelope(env);
                    if(ge.intersects(areaSearch, true)&&!ge.intersects(areaSearch, false))visitor.visit(env);
                }
            } break;

            case EQUALS : {
                tree.search(areaSearch, defVisitor);
                for(Envelope env : listSearch){
                    final GeneralEnvelope ge = new GeneralEnvelope(env);
                    if(ge.equals(areaSearch, 1E-9, true))visitor.visit(env);
                }
            } break;
            case OVERLAPS : {
                tree.search(areaSearch, defVisitor);
                for(Envelope env : listSearch){
                    final GeneralEnvelope ge = new GeneralEnvelope(env);
                    if(ge.intersects(areaSearch, false)&&!ge.contains(areaSearch, true)&&!areaSearch.contains(ge, true))visitor.visit(env);
                }
            } break;
            default : throw new IllegalStateException("not implemented yet");
        }
    }
}

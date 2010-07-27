/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.index.quadtree;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Represent a tyle in the fack quad tree.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Node extends AbstractNode{

    protected Node n0;
    protected Node n1;
    protected Node n2;
    protected Node n3;

    /**
     * @param envelope the node bounds [MinX,MinY,MaxX,MaxY]
     */
    public Node(double minx, double miny, double maxx, double maxy) {
        super(minx,miny,maxx,maxy);
    }

    public Node(Envelope env) {
        super(env);
    }

    @Override
    public void setSubNodes(Node ... nodes) {
        n0 = null;
        n1 = null;
        n2 = null;
        n3 = null;
        for(int i=0;i<nodes.length;i++){
            final Node n = nodes[i];
            switch(i){
                case 0: this.n0=n; break;
                case 1: this.n1=n; break;
                case 2: this.n2=n; break;
                case 3: this.n3=n; break;
                default: throw new IllegalArgumentException("Exprected maximum 4 nodes.");
            }
        }
    }

    @Override
    public int getNumSubNodes() {
        if(n0 == null){
            return 0;
        }else if(n1 == null){
            return 1;
        }else if(n2 == null){
            return 2;
        }else if(n3 == null){
            return 3;
        }else{
            return 4;
        }
    }

    @Override
    public Node getSubNode(int pos) throws StoreException {
        switch(pos){
            case 0: return n0;
            case 1: return n1;
            case 2: return n2;
            case 3: return n3;
        }
        throw new IllegalArgumentException("Index over 3 not allowed");
    }

}

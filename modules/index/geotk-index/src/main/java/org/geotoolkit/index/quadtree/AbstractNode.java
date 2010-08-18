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
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Represent a tyle in the fack quad tree.
 *
 * @author Tommaso Nolli
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractNode {

    public static final int NONE = 0;
    public static final int INTERSECT = 1;
    public static final int CONTAINED = 2;

    /**
     * [MinX,MinY,MaxX,MaxY]
     */
    private double minx;
    private double miny;
    private double maxx;
    private double maxy;
    private int numShapesId;
    private int[] shapesId;


    /**
     * @param envelope the node bounds [MinX,MinY,MaxX,MaxY]
     */
    public AbstractNode(double minx, double miny, double maxx, double maxy) {
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
        this.shapesId = new int[1];
        this.numShapesId = 0;
        Arrays.fill(this.shapesId, -1);
    }

    public AbstractNode(Envelope env) {
        this.minx = env.getMinX();
        this.miny = env.getMinY();
        this.maxx = env.getMaxX();
        this.maxy = env.getMaxY();
        this.shapesId = new int[1];
        this.numShapesId = 0;
        Arrays.fill(this.shapesId, -1);
    }

    public abstract void setSubNodes(AbstractNode ... nodes);

    /**
     * @return Returns the bounds. [MinX,MinY,MaxX,MaxY]
     */
    public double[] getEnvelope() {
        return new double[]{minx,miny,maxx,maxy};
    }

    public void setEnvelope(double minx, double miny, double maxx, double maxy) {
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
    }

    public void setEnvelope(double[] env) {
        this.minx = env[0];
        this.miny = env[1];
        this.maxx = env[2];
        this.maxy = env[3];
    }

    public Envelope getBounds(Envelope buffer){
        buffer.init(minx, maxx, miny, maxy);
        return buffer;
    }

    /** 
     * @return Number of sub nodes
     */
    public abstract int getNumSubNodes();

    /**
     * DOCUMENT ME!
     *
     * @return Returns the number of records stored.
     */
    public int getNumShapeIds() {
        return this.numShapesId;
    }

    /**
     * Gets the Node at the requested position
     * 
     * @param pos The position
     * @return A Node
     * @throws StoreException DOCUMENT ME!
     */
    public abstract AbstractNode getSubNode(int pos) throws StoreException;

    /**
     * Add a shape id
     * 
     * @param id
     */
    public void addShapeId(int id) {
        if (this.shapesId.length == this.numShapesId) {
            // Increase the array
            final int[] newIds = new int[this.numShapesId * 2];
            Arrays.fill(newIds, -1);
            System.arraycopy(this.shapesId, 0, newIds, 0, this.numShapesId);
            this.shapesId = newIds;
        }

        this.shapesId[this.numShapesId] = id;
        this.numShapesId++;
    }

    /**
     * Gets a shape id
     * 
     * @param pos The position
     * @return The shape id (or recno) at the requested position
     * @throws ArrayIndexOutOfBoundsException DOCUMENT ME!
     */
    public int getShapeId(int pos) {
        if (pos >= this.numShapesId) {
            throw new ArrayIndexOutOfBoundsException("Requsted " + pos
                    + " but size = " + this.numShapesId);
        }

        return this.shapesId[pos];
    }

    /**
     * Sets the shape ids
     * @param ids
     */
    public void setShapesId(int[] ids) {
        if (ids == null) {
            this.numShapesId = 0;
        } else {
            this.shapesId = ids;
            this.numShapesId = 0;

            for(int i : ids) {
                if (i == -1) {
                    break;
                }
                this.numShapesId++;
            }
        }
    }

    /**
     * DOCUMENT ME!
     * @return Returns the shapesId.
     */
    public int[] getShapesId() {
        return this.shapesId;
    }

    @Override
    public String toString() {
        return toString(null);
    }

    /**
     * Special toString, that will add "<>" before each node that intersect the
     * given envelope.
     * @param env
     * @return
     */
    public String toString(Envelope env) {
        final StringBuilder sb = new StringBuilder();

        if(env != null && env.intersects(getBounds(new Envelope()))){
            sb.append("<> ");
        }

        sb.append("Node : envelope[");
        sb.append(minx).append(',').append(miny).append(',');
        sb.append(maxx).append(',').append(maxy).append(']');
        sb.append(" ids(nb=").append(getNumShapeIds());
        sb.append(")[");
        for(int i=0,n=getNumShapeIds();i<n;i++){
            sb.append(shapesId[i]);
            if(i<n-1){
                sb.append(',');
            }
        }
        sb.append("]");

        for(int i=0,n=getNumSubNodes();i<n;i++){
            sb.append('\n');
            try{
                String subIterator;
                //move text to the right
                if(i<n-1){
                    subIterator = "\u251C\u2500\u2500" + getSubNode(i).toString(env);
                    subIterator = subIterator.replaceAll("\n", "\n\u2502\u00A0\u00A0");
                }else{
                    subIterator = "\u2514\u2500\u2500" + getSubNode(i).toString(env);
                    subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0");
                }
                sb.append(subIterator);
            }catch(StoreException ex){
                QuadTree.LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
        }

        return sb.toString();
    }

    public boolean intersects(Envelope other){
        return !(other.getMinX() > maxx ||
                 other.getMaxX() < minx ||
                 other.getMinY() > maxy ||
                 other.getMaxY() < miny);
    }

    public int relation(Envelope other){
        final double envMinx = other.getMinX();
        final double envMaxx = other.getMaxX();
        final double envMiny = other.getMinY();
        final double envMaxy = other.getMaxY();

        if(minx >= envMinx &&
               maxx <= envMaxx &&
               miny >= envMiny &&
               maxy <= envMaxy){
            //we are contained by the given envelope
            return CONTAINED;
        }else if(!(envMinx > maxx ||
                   envMaxx < minx ||
                   envMiny > maxy ||
                   envMaxy < miny)){
            //we intersect
            return INTERSECT;
        }else{
            return NONE;
        }
    }

}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.display3d.scene.quadtree;

import org.apache.sis.geometry.GeneralEnvelope;
import org.opengis.geometry.Envelope;

import javax.vecmath.Point3i;
import java.awt.*;

/**
 * Utilities methods for QuadTree management
 *
 * @author Thomas Rouby (Geomatys))
 */
public final class QuadTreeUtils {

    /**
     * Get the QuadTree Grid Size for a specific tree depth
     *
     * @param treeDepth
     * @return
     */
    public static Dimension getGridSize(int treeDepth) {
        final int numTile = (int)Math.pow(2, treeDepth);
        return new Dimension(numTile,numTile);
    }

    /**
     * Get the number of pixel for the grid at a specific tree depth
     *
     * @param treeDepth
     * @param pixelPerTile
     * @return
     */
    public static long getNumPixel(int treeDepth, long pixelPerTile) {
        final int gridSize = (int)Math.pow(2, treeDepth);
        return gridSize*pixelPerTile;
    }

    /**
     * Get the scale (crs / pixels) for the grid at a specific tree depth
     *
     * @param treeDepth
     * @param pixelPerTile
     * @param crsSpan
     * @return
     */
    public static double getScale(int treeDepth, long pixelPerTile, double crsSpan) {
        final long numPixel = getNumPixel(treeDepth, pixelPerTile);
        return crsSpan/numPixel;
    }

    /**
     * Search the nearest tree depth for a specific scale
     *
     * @param scale
     * @param pixelPerTile
     * @param crsSpan
     * @return
     */
    public static int getTreeDepth(double scale, long pixelPerTile, double crsSpan){
        int treeDepth = -1;
        double quadTreeScale;
        do {
            quadTreeScale = getScale(++treeDepth, pixelPerTile, crsSpan);
        } while (scale < quadTreeScale);
        return treeDepth;
    }

    /**
     * Search the top QuadTree node of the QuadTree
     *
     * @param node
     * @return
     */
    public static QuadTreeNode findRootNode(QuadTreeNode node) {
        QuadTreeNode searchNode = node;
        while (searchNode.getQuadParent() != null) {
            searchNode = searchNode.getQuadParent();
        }
        return searchNode;
    }

    public static Point3i findPosition(Point[] id) {

        final int treeDepth = id.length;
        final Dimension gridSize = getGridSize(treeDepth);

        int x=0, y=0;
        int factX = gridSize.width/2;
        int factY = gridSize.height/2;

        for (int i=0; i<treeDepth; i++){
            final Point pid = id[i];

            if (pid.x == 1) {
                x += factX;
            }
            if (pid.y == 1) {
                y += factY;
            }

            factX = factX/2;
            factY = factY/2;
        }

        return new Point3i(x, y, treeDepth);
    }

    public static Point[] findId(Point3i pos) {
        return findId(pos.z, pos.x, pos.y);
    }

    public static Point[] findId(int treeDepth, int numX, int numY){
        final Dimension gridSize = getGridSize(treeDepth);

        if (numX < 0 || numX >= gridSize.width || numY < 0 || numY >= gridSize.height) return null;

        final Point[] id = new Point[treeDepth];

        int tmpX = 0, tmpY=0;
        int factX = gridSize.width/2;
        int factY = gridSize.height/2;

        for (int i=0; i<treeDepth; i++) {
            int x=0,y=0;

            if (tmpX+factX <= numX) {
                tmpX+=factX;
                x=1;
            }
            if (tmpY+factY <= numY) {
                tmpY+=factY;
                y=1;
            }

            id[i] = new Point(x,y);

            factX = factX/2;
            factY = factY/2;
        }

        return id;
    }

    /**
     * Search the id of the quad tree node
     *
     * @param treeDepth
     * @param envelope
     * @param lon
     * @param lat
     * @return
     */
    public static Point[] findId(int treeDepth, Envelope envelope, double lon, double lat) {

        if (lon < envelope.getMinimum(0) || lon > envelope.getMaximum(0) || lat < envelope.getMinimum(1) || lat > envelope.getMaximum(1)) return null;

        final Point[] id = new Point[treeDepth];

        final GeneralEnvelope tmpEnv = new GeneralEnvelope(envelope);
        for (int i=0; i<treeDepth; i++) {
            int x,y;

            if (lon < tmpEnv.getMedian(0)) {
                x = 0;
                tmpEnv.setRange(0, tmpEnv.getMinimum(0), tmpEnv.getMedian(0));
            } else {
                x = 1;
                tmpEnv.setRange(0, tmpEnv.getMedian(0), tmpEnv.getMaximum(0));
            }

            if (lat < tmpEnv.getMedian(1)) {
                y = 1;
                tmpEnv.setRange(1, tmpEnv.getMinimum(1), tmpEnv.getMedian(1));
            } else {
                y = 0;
                tmpEnv.setRange(1, tmpEnv.getMedian(1), tmpEnv.getMaximum(1));
            }

            id[i] = new Point(x,y);
        }
        return id;
    }

}

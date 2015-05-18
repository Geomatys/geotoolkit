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

import java.awt.Dimension;
import java.awt.Point;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.math.XMath;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.referencing.cs.PredefinedCS;
import org.apache.sis.internal.system.DefaultFactories;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.OperationMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.geotoolkit.display3d.Map3D;

/**
 * @author Thomas Rouby (Geomatys))
 */
public class QuadTree {

    private final QuadTreeNode rootNode;
    private final int minTreeDepth, maxTreeDepth;
    private final Map3D map;

    /**
     * Arbitrary tileSize in pixel of each Tile3D of the node
     */
    private Dimension tileSize = new Dimension(256,256);

    private Dimension ptsSize = new Dimension(25,25);

    public QuadTree(Map3D map, Envelope envelope) {
        this(map, envelope, 1, 12);
    }

    public QuadTree(Map3D map, Envelope envelope, int minTreeDepth, int maxTreeDepth) {
        this.map = map;
        this.minTreeDepth = Math.max(1,minTreeDepth);
        this.maxTreeDepth = Math.max(1, maxTreeDepth);

        final Envelope plateCarre = transformToPlateCarre(envelope);
        final GeneralEnvelope tmpEnv = (plateCarre instanceof GeneralEnvelope)?((GeneralEnvelope)plateCarre):(new GeneralEnvelope(plateCarre));

        final double lonSpan = tmpEnv.getSpan(0);
        final double latSpan = tmpEnv.getSpan(1);

        if (lonSpan > latSpan){
            tmpEnv.setRange(1, tmpEnv.getMaximum(1)-lonSpan, tmpEnv.getMaximum(1));
        } else if (latSpan > lonSpan) {
            tmpEnv.setRange(0, tmpEnv.getMinimum(0), tmpEnv.getMinimum(0)+latSpan);
        }

        rootNode = new QuadTreeNode(map, null, tmpEnv, null);
    }

    private Envelope transformToPlateCarre(Envelope env){
        try{
            final Envelope tmpEnv = org.geotoolkit.referencing.CRS.transform(env,
                    CRS.getHorizontalComponent(env.getCoordinateReferenceSystem()));

            if (tmpEnv.getCoordinateReferenceSystem() instanceof GeographicCRS){

                final GeographicCRS geoCrs = (GeographicCRS) tmpEnv.getCoordinateReferenceSystem();

                final MathTransformFactory mathTransformFactory = FactoryFinder.getMathTransformFactory(null);
                final ParameterValueGroup plate_carree = mathTransformFactory.getDefaultParameters("Plate_Carree");

                final CoordinateOperationFactory coordinateOperationFactory = org.geotoolkit.referencing.CRS.getCoordinateOperationFactory(true);
                final OperationMethod operationMethod = coordinateOperationFactory.getOperationMethod("Plate_Carree");

                final Map<String, Object> params = new HashMap<String, Object>();
                params.put("name","plate_carre");
                final Conversion createDefiningConversion = coordinateOperationFactory.createDefiningConversion(params, operationMethod, plate_carree);

                final CRSFactory crsFactory = DefaultFactories.forBuildin(CRSFactory.class);
                final ProjectedCRS createProjectedCRS = crsFactory.createProjectedCRS(params, geoCrs, createDefiningConversion, PredefinedCS.PROJECTED);

                return org.geotoolkit.referencing.CRS.transform(tmpEnv, createProjectedCRS);
            }
        } catch (Exception ex) {
            Map3D.LOGGER.log(Level.WARNING, "", ex);
        }

        return env;
    }

    public Envelope getGeneralEnvelope() {
        return rootNode.getEnvelope();
    }

    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return rootNode.getEnvelope().getCoordinateReferenceSystem();
    }

    public QuadTreeNode getRootNode() {
        return rootNode;
    }

    public int getMaxTreeDepth() {
        return this.maxTreeDepth;
    }

    public int getMinTreeDepth() {
        return this.minTreeDepth;
    }

    public QuadTreeNode getOrCreateNode(int treeDepth, int numX, int numY) {
        if (treeDepth > this.maxTreeDepth || treeDepth < this.minTreeDepth) return null;
        final Point[] id = QuadTreeUtils.findId(treeDepth, numX, numY);
        return rootNode.getOrCreateChild(id);
    }

    public QuadTreeNode getOrCreateNode(Point[] id) {
        if (id.length > this.maxTreeDepth || id.length < this.minTreeDepth) return null;
        return rootNode.getOrCreateChild(id);
    }

    public List<QuadTreeNode> findView(int treeDepth, double valX, double valY, double viewDist) {
        final List<QuadTreeNode> viewList = new ArrayList<>();
        searchView(viewList, this.rootNode, XMath.clamp(treeDepth, this.minTreeDepth, this.maxTreeDepth), valX, valY, viewDist);
        return viewList;
    }

    private boolean needSubdivise(QuadTreeNode candidate, int targetDepth, double x, double y, double distTest) {

        final int testDepth = candidate.getTreeDepth();
        if (testDepth >= targetDepth || testDepth > this.maxTreeDepth) return false;
        if (testDepth < this.minTreeDepth) return true;

        final Envelope envelope = candidate.getEnvelope();

        if (envelope.getMinimum(0) <= x && envelope.getMaximum(0) >= x && envelope.getMinimum(1) <= y && envelope.getMaximum(1) >= y) {
            return true;
        }

        final double rayonX = envelope.getSpan(0)/2.0;
        final double rayonY = envelope.getSpan(1)/2.0;
        final double rayon = Math.hypot(rayonX, rayonY);

        final double distanceX = Math.abs(envelope.getMedian(0)-x);
        final double distanceY = Math.abs(envelope.getMedian(1)-y);
        final double distance = Math.hypot(distanceX, distanceY);

        final double nodeDist = distance - rayon;
        if(nodeDist<0) return true;

        final int diff = (int)Math.floor(nodeDist / distTest);

        return testDepth < targetDepth-diff;
    }

    private void searchView(List<QuadTreeNode> viewList, QuadTreeNode node, int treeDepth, double x, double y, double viewDist){
        if (node.getTreeDepth() >= treeDepth || node.getTreeDepth() > this.maxTreeDepth){
            viewList.add(node);
        } else {
            if (needSubdivise(node, treeDepth, x, y, viewDist)){
                searchView(viewList, node.getOrCreateChild(new Point(0,0)), treeDepth, x, y, viewDist);
                searchView(viewList, node.getOrCreateChild(new Point(1,0)), treeDepth, x, y, viewDist);
                searchView(viewList, node.getOrCreateChild(new Point(0,1)), treeDepth, x, y, viewDist);
                searchView(viewList, node.getOrCreateChild(new Point(1,1)), treeDepth, x, y, viewDist);
            } else {
                viewList.add(node);
            }
        }
    }

    public Dimension getTileSize() {
        return tileSize;
    }

    public void setTileSize(Dimension tileSize) {
        this.tileSize = tileSize;
    }

    public Dimension getPtsSize() {
        return ptsSize;
    }

    public void setPtsSize(Dimension ptsSize) {
        this.ptsSize = ptsSize;
    }

    @Override
    public String toString() {
        return super.toString()+"\n"+rootNode.toString();
    }

}

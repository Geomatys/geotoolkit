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

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.texture.TextureData;
import java.awt.Dimension;
import java.awt.Point;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.math.XMath;
import org.opengis.geometry.Envelope;

import javax.vecmath.Point3i;
import java.util.Arrays;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.display3d.scene.ContextContainer3D;
import org.geotoolkit.display3d.scene.SceneNode3D;
import org.geotoolkit.display3d.scene.Terrain;
import org.geotoolkit.display3d.scene.component.Tile3D;

import org.geotoolkit.gui.swing.tree.Trees;

/**
 * @author Thomas Rouby (Geomatys))
 */
public class QuadTreeNode extends SceneNode3D {

    /**
     * The id of this node. The id contains the id of the parent node and the node position on parent node.
     */
    private final Point parentPosition;
    /**
     * position in pyramid x,y,depth
     */
    private final Point3i pyramidPosition;

    /**
     * The Tile3D SceneNode to draw
     */
    private Tile3D data;

    /**
     * The envelope for the Tile3D sceneNode on create
     */
    private Envelope envelope;

    private boolean isDataImageLoaded = false, isDataMNTLoaded = false;

    /**
     * Arbitrary tileSize in pixel of each Tile3D of the node
     */
    private static final Dimension tileSize = new Dimension(256,256);
    private static final Dimension ptsSize = new Dimension(25,25);

    private final QuadTreeNode quadParent;
    private QuadTreeNode[][] quadchildren;

    /**
     * Constructor for root QuadTree node
     *
     * @param map
     * @param parent
     * @param envelope if null, get map.getTerrain().getEnvelope()
     * @param position
     */
    public QuadTreeNode(Map3D map, QuadTreeNode parent, Envelope envelope, Point position) {
        super(map);

        this.envelope = envelope;
        if (this.envelope == null) {
            final Terrain terrain = ((ContextContainer3D)map.getContainer()).getTerrain();
            if (terrain != null) {
                this.envelope = terrain.getEnvelope();
            }
        }

        quadParent = parent;
        if(parent == null){
            this.pyramidPosition = new Point3i(0,0,0);
            this.parentPosition = null;
        }else{
            this.pyramidPosition = new Point3i(
                    parent.pyramidPosition.x*2 + position.x,
                    parent.pyramidPosition.y*2 + position.y,
                    parent.getPosition().z+1);
            this.parentPosition = position;
        }
    }

    public void setDataImage(TextureData textureData) {
        if (data != null && !isDataImageLoaded) {
            isDataImageLoaded = true;
            data.setTextureDataImg(textureData);
        }
    }

    public boolean isDataImageLoaded() {
        return isDataImageLoaded;
    }

    public void setDataMNT(float[] vertices) {
        if (data != null && !isDataMNTLoaded) {
            isDataMNTLoaded = true;
            data.setMNT(vertices);
        }
    }

    public boolean isDataMNTLoaded() {
        return isDataMNTLoaded;
    }

    private synchronized QuadTreeNode[][] getQuadChildren(boolean create){
        if(quadchildren==null && create){
            quadchildren = new QuadTreeNode[2][2];
            quadchildren[0][0] = createQuadChild(0, 0);
            quadchildren[0][1] = createQuadChild(0, 1);
            quadchildren[1][0] = createQuadChild(1, 0);
            quadchildren[1][1] = createQuadChild(1, 1);
        }
        return quadchildren;
    }

    private QuadTreeNode createQuadChild(int x, int y){
        final Envelope env = this.getEnvelope();
        GeneralEnvelope childEnv = new GeneralEnvelope(env.getCoordinateReferenceSystem());
        if (x == 0) {
            childEnv.setRange(0, env.getMinimum(0), env.getMedian(0));
        } else {
            childEnv.setRange(0, env.getMedian(0), env.getMaximum(0));
        }
        if (y == 0) {
            childEnv.setRange(1, env.getMedian(1), env.getMaximum(1));
        } else {
            childEnv.setRange(1, env.getMinimum(1), env.getMedian(1));
        }
        return new QuadTreeNode(getCanvas(), this, childEnv, new Point(x, y));
    }

    /**
     * Get or create the child at position of succession of couple (x,y)
     * Couple (x,y) can be :
     * (0,0),(0,1),(1,0) or (1,1)
     *
     * @param id
     * @return
     */
    public QuadTreeNode getOrCreateChild(Point[] id) {
        if (id == null) return null;
        QuadTreeNode searchNode = this;
        for (Point pos : id) {
                searchNode = searchNode.getOrCreateChild(pos);
            }
        return searchNode;
    }

    /**
     * Get or create the child at position (x,y)
     * Couple (x,y) can be :
     * (0,0),(0,1),(1,0) or (1,1)
     *
     * @param pos
     * @return
     */
    public QuadTreeNode getOrCreateChild(Point pos) {
        if (pos == null) return null;
        final QuadTreeNode[][] children = getQuadChildren(true);
        return children[pos.x][pos.y];
    }

    /**
     * Return the object id of this node
     * @return
     */
    public Point[] getId() {
        final Point[] stack = new Point[getTreeDepth()];
        QuadTreeNode node = this;
        int i=stack.length-1;
        while(i>=0){
            stack[i] = node.parentPosition;
            node = node.quadParent;
            i--;
        }
        return stack;
    }

    /**
     * Get the sceneNode to draw
     * @return
     */
    public SceneNode3D getData() {
        return this.data;
    }

    public QuadTreeNode getQuadParent() {
        return quadParent;
    }

    /**
     * Create data and initialize with first parents nodes with data
     *
     * @return
     */
    public SceneNode3D getOrCreateData() {
        if (this.data == null) {
            this.data = new Tile3D(getCanvas(), this.envelope, this.pyramidPosition, this.ptsSize.width, this.ptsSize.height);
            this.initDataMnt();
            this.initDataImage();
            this.getChildren().add(data);
        }
        return this.data;
    }

    public void removeData() {
        if (this.data != null) {
            this.getChildren().remove(this.data);
            this.data.dispose();
            this.data = null;
        }
        this.isDataImageLoaded = false;
        this.isDataMNTLoaded = false;
    }

    public boolean isData(){
        return this.data != null;
    }

    private void initDataImage() {
        final QuadTreeNode parentImage = this.getFirstParentWithDataImage();

        if (parentImage != null && parentImage.isData() && parentImage.isDataImageLoaded()) {
            final Tile3D dataImage = parentImage.data;
            if (dataImage != null) {

                final float[] rangeUVs = dataImage.getUVsRange();
                float minU = rangeUVs[0],
                      minV = rangeUVs[1],
                      maxU = rangeUVs[2],
                      maxV = rangeUVs[3];

                final Point[] ids = getId();
                for (int i = parentImage.getTreeDepth(); i<this.getTreeDepth(); i++) {
                    final Point pos = ids[i];
                    final float medianU = (maxU+minU)/2.0f;
                    final float medianV = (maxV+minV)/2.0f;
                    if (pos.x == 0) {
                        maxU = medianU;
                    } else {
                        minU = medianU;
                    }
                    if (pos.y == 0) {
                        maxV = medianV;
                    } else {
                        minV = medianV;
                    }
                }

                final float[] uvs = this.data.getUVsAsArray();

                for (int j=0; j<this.data.getNumVertex(); j++){
                    final float oldU = uvs[j*2];
                    uvs[j*2] = oldU * maxU + (1.0f-oldU)*minU;
                    final float oldV = uvs[j*2+1];
                    uvs[j*2 + 1] = oldV * maxV + (1.0f-oldV)*minV;
                }

                this.data.setTextureImg(dataImage.getTextureImg());
                this.data.setUVs(uvs);
                this.data.setUVsRange(minU, minV, maxU, maxV);
            }
        }
    }

    private void initDataMnt() {
        final QuadTreeNode parentMnt = this.getFirstParentWithDataMNT();

        if (parentMnt != null && parentMnt.isData() && parentMnt.isDataMNTLoaded()) {
            final Tile3D dataMNT = parentMnt.data;
            if (dataMNT != null && parentMnt.isDataMNTLoaded()) {

                float minU, minV, maxU, maxV;
                minU = minV = 0.0f;
                maxU = maxV = 1.0f;

                final Point[] ids = getId();
                for (int i = parentMnt.getTreeDepth(); i<this.getTreeDepth(); i++) {
                    final Point pos = ids[i];
                    final float medianU = (maxU+minU)/2.0f;
                    final float medianV = (maxV+minV)/2.0f;
                    if (pos.x == 0) {
                        maxU = medianU;
                    } else {
                        minU = medianU;
                    }
                    if (pos.y == 0) {
                        maxV = medianV;
                    } else {
                        minV = medianV;
                    }
                }

                final float[] vertices = this.data.getVerticesAsArray();

                final Dimension ptsSize = this.data.getPtsNumber();

                for (int x=0; x<ptsSize.width; x++) {
                    for (int y=0; y<ptsSize.height; y++) {

                        // (i+j*axis0Pts)*3

                        final int pixel0 = XMath.clamp(x, 1, ptsSize.width-1);
                        final int pixel1 = XMath.clamp(y, 1, ptsSize.height-1);

                        final double valX = (pixel0-1.0) / (ptsSize.width-2.0);
                        final double valY = (pixel1-1.0) / (ptsSize.height-2.0);

                        final double scaleX = valX * maxU + (1.0f-valX)*minU;
                        final double scaleY = valY * maxV + (1.0f-valY)*minV;

                        vertices[(x+y*ptsSize.width)*3 + 2] = (float)dataMNT.getZValue(scaleX, scaleY);

                        if (x == 0 || y == 0 || x == ptsSize.width-1 || y == ptsSize.height-1) {
                            vertices[(x+y*ptsSize.width)*3 + 2] -= 2000.0f;
                        }
                    }
                }

                this.data.setVertices(vertices);
            }
        }
    }

    /**
     * Return first parent with data != null
     * return null else or if this node is the root node
     *
     * @return
     */
    private QuadTreeNode getFirstParentWithDataImage() {
        if (this.getTreeDepth() == 0) return null;

        QuadTreeNode parent = quadParent;
        while (parent!= null){
            if (parent.isData() && parent.isDataImageLoaded()) {
                return parent;
            }
            parent = parent.quadParent;
        }
        return null;
    }

    /**
     * Return first parent with data != null
     * return null else or if this node is the root node
     *
     * @return
     */
    private QuadTreeNode getFirstParentWithDataMNT() {
        if (this.getTreeDepth() == 0) return null;

        QuadTreeNode parent = quadParent;
        while (parent!=null){
            if (parent.isData() && parent.isDataMNTLoaded()) {
                return parent;
            }
            parent = parent.quadParent;
        }
        return null;
    }

    /**
     * Shortcut to id.length
     * @return
     */
    public int getTreeDepth() {
        return this.pyramidPosition.z;
    }

    /**
     * Return the QuadTreeNode position relative to his parent
     * @return
     */
    public Point getNodePosition() {
        return this.parentPosition;
    }

    /**
     * Return the QuadTreeNode position relative to mosaic
     * @return
     */
    public Point3i getPosition() {
        return this.pyramidPosition;
    }

    public Dimension getTileSize() {
        return this.tileSize;
    }

    public Dimension getPtsSize() {
        return this.ptsSize;
    }

    public Envelope getEnvelope() {
        return this.envelope;
    }

    @Override
    public String toString() {
        final QuadTreeNode[][] children = getQuadChildren(false);
        if(children == null){
            return pyramidPosition.toString() +isData();
        }else{
            return Trees.toString(pyramidPosition.toString() +isData(),
                    Arrays.asList(children[0][0],children[0][1],children[1][0],children[1][1]));
        }

    }

    @Override
    public void dispose(GLAutoDrawable glDrawable) {
        super.dispose(glDrawable);
        if (data != null) {
            data.dispose(glDrawable);
            this.removeData();
        }
    }
}

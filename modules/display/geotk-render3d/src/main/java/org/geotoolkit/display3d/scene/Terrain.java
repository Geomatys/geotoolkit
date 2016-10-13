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
package org.geotoolkit.display3d.scene;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.Dimension;
import java.awt.Point;
import java.io.InputStream;
import javax.measure.IncommensurableException;

import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.display3d.scene.camera.TrackBallCamera;
import org.geotoolkit.display3d.scene.component.Tile3D;
import org.geotoolkit.display3d.scene.loader.ElevationLoader;
import org.geotoolkit.display3d.scene.loader.ImageLoader;
import org.geotoolkit.display3d.scene.quadtree.QuadTree;
import org.geotoolkit.display3d.scene.quadtree.QuadTreeNode;
import org.geotoolkit.display3d.scene.quadtree.QuadTreeUtils;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Thomas Rouby (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class Terrain extends SceneNode3D {

    private final QuadTree quadTree;

    private ImageLoader loaderImg;
    private ElevationLoader loaderMNT;

    private TextureData textureDataDefault, textureDataError, textureDataBlank;

    public Terrain(Map3D map, Envelope envelope, int numMosaic) throws TransformException, FactoryException {
        super(map);
        this.quadTree = new QuadTree(map, envelope, 1, numMosaic);

        setUpdater(new TerrainUpdater(this));
    }

    public QuadTree getQuadTree() {
        return this.quadTree;
    }

    @Override
    protected void initInternal(GLAutoDrawable glDrawable) throws GLException {

        final GL gl = glDrawable.getGL();
        final GLProfile glProfile = glDrawable.getGLProfile();

        if (gl.isGL2()) {
            try{
                InputStream defaultImgStream = this.getClass().getClassLoader().getResourceAsStream("img/default.png");
                textureDataDefault = TextureIO.newTextureData(glProfile, defaultImgStream, 3, GL.GL_RGB, false, null);
                InputStream errorImgStream = this.getClass().getClassLoader().getResourceAsStream("img/error.png");
                textureDataError = TextureIO.newTextureData(glProfile, errorImgStream, 3, GL.GL_RGB, false, null);
                InputStream blankImgStream = this.getClass().getClassLoader().getResourceAsStream("img/blank.png");
                textureDataBlank = TextureIO.newTextureData(glProfile, blankImgStream, 3, GL.GL_RGB, false, null);

                final QuadTreeNode root = quadTree.getRootNode();
                final SceneNode3D tile = root.getOrCreateData();

                TerrainUpdater updater = (TerrainUpdater) getUpdater();
                if (updater == null) {
                    updater = new TerrainUpdater(this);
                    setUpdater(updater);
                }
                updater.initialize(glDrawable);
                updater.updateImageOn(root);
                updater.updateMntOn(root);

                if ( tile instanceof Tile3D){
                    ((Tile3D)tile).checkTexture(glDrawable, Tile3D.INDEX_IMAGE);
                }

            } catch (Exception ex) {
                throw new GLException(ex.getMessage(),ex);
            }
        }
    }

    /* ***********************************************************
     ***************** GETTER & SETTER CLASSIC *******************
     *************************************************************/

    public TextureData getTextureDataDefault() {
        return textureDataDefault;
    }

    public TextureData getTextureDataError() {
        return textureDataError;
    }

    public TextureData getTextureDataBlank() {
        return textureDataBlank;
    }

    public ElevationLoader getElevationLoader(){
        return this.loaderMNT;
    }

    public void setElevationLoader(ElevationLoader loaderMNT) throws PortrayalException {
        this.loaderMNT = loaderMNT;
        this.loaderMNT.setOutputCRS(this.quadTree.getCoordinateReferenceSystem());
    }

    public ImageLoader getImageLoader() {
        return loaderImg;
    }

    public void setImageLoader(ImageLoader loaderImg) throws PortrayalException {
        this.loaderImg = loaderImg;
        this.loaderImg.setOutputCRS(this.quadTree.getCoordinateReferenceSystem());
    }

    public double getProjectionDist(double length) {
        final TrackBallCamera camera = getCanvas().getCamera();
        return camera.getProjectionLength(length);
    }

     /* ***********************************************************
     ***************** GETTER & SETTER OTHERS *******************
     *************************************************************/


    public double getAltitudeOf(DirectPosition position, double scale) throws PortrayalException {
        return this.loaderMNT.getValueOf(position, scale);
    }

    public double getAltitudeSmoothOf(DirectPosition position, double scale) throws PortrayalException {
        return this.loaderMNT.getSmoothValueOf(position, scale);
    }

    public Point getPositionOf(double longitude, double latitude, Envelope gridEnvelope, Dimension gridSize){
        final double stepLon = gridEnvelope.getSpan(0) / gridSize.width;
        final double stepLat = gridEnvelope.getSpan(1) / gridSize.height;

        final double minLon = gridEnvelope.getMinimum(0);
        final double minLat = gridEnvelope.getMinimum(1);

        final int posLon = (int)(Math.floor((longitude-minLon)/stepLon));
        final int posLat = (gridSize.height-1) - (int)(Math.floor((latitude-minLat)/stepLat));

        return new Point(posLon, posLat);
    }

    public Point getPositionOf(double longitude, double latitude){
        final TrackBallCamera camera = getCanvas().getCamera();
        final double scale = camera.getViewScale(camera.getLength());
        final int scaleIndex = this.getNearestScaleIndex(scale);
        return getPositionOf(longitude, latitude, this.quadTree.getGeneralEnvelope(), QuadTreeUtils.getGridSize(scaleIndex));
    }

    public Envelope getEnvelope(){
        return this.quadTree.getGeneralEnvelope();
    }

    public double getMinScale() {
        final int treeDepth = this.quadTree.getMinTreeDepth();
        final int tileWidth = this.quadTree.getTileSize().width;
        final double tileSpan = this.quadTree.getGeneralEnvelope().getSpan(0);

        return QuadTreeUtils.getScale(treeDepth, tileWidth, tileSpan);
    }

    public double getMaxScale() {
        final int treeDepth = this.quadTree.getMaxTreeDepth();
        final int tileWidth = this.quadTree.getTileSize().width;
        final double tileSpan = this.quadTree.getGeneralEnvelope().getSpan(0);

        return QuadTreeUtils.getScale(treeDepth, tileWidth, tileSpan);
    }

    public int getNearestScaleIndex(double scale) {
        return Math.min(this.quadTree.getMaxTreeDepth(), QuadTreeUtils.getTreeDepth(scale, this.quadTree.getTileSize().width, this.getEnvelope().getSpan(0)));
    }

    @Override
    public void dispose(GLAutoDrawable glDrawable){
        super.dispose(glDrawable);
    }
}

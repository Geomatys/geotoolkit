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

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLRunnable;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.awt.AWTTextureData;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import javax.measure.IncommensurableException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import org.geotoolkit.display.PortrayalException;

import org.geotoolkit.display.primitive.SceneNode;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.display3d.scene.camera.Camera;
import org.geotoolkit.display3d.scene.camera.TrackBallCamera;
import org.geotoolkit.display3d.scene.component.Tile3D;
import org.geotoolkit.display3d.scene.loader.ElevationLoader;
import org.geotoolkit.display3d.scene.loader.ImageLoader;
import org.geotoolkit.display3d.scene.quadtree.JQuadView;
import org.geotoolkit.display3d.scene.quadtree.QuadTree;
import org.geotoolkit.display3d.scene.quadtree.QuadTreeNode;
import org.geotoolkit.display3d.scene.quadtree.QuadTreeUtils;
import org.geotoolkit.math.XMath;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;

/**
 * @author Thomas Rouby (Geomatys)
 */
public class TerrainUpdater implements PropertyChangeListener, Updater {

    private final Comparator<QuadTreeNode> DISTANCE_COMPARATOR = new Comparator<QuadTreeNode>(){
            public int compare(QuadTreeNode o1, QuadTreeNode o2) {

                final int depth1 = o1.getTreeDepth();
                final int depth2 = o2.getTreeDepth();

                final int dz = Integer.compare(depth2, depth1);
                if(dz!=0) return dz;

                final Envelope env1 = o1.getEnvelope();
                final Vector2d center1 = new Vector2d(env1.getMedian(0), env1.getMedian(1));

                final Envelope env2 = o2.getEnvelope();
                final Vector2d center2 = new Vector2d(env2.getMedian(0), env2.getMedian(1));

                final double d1 = dists(center1);
                final double d2 = dists(center2);

                if(d1<d2){
                    return -1;
                }else if(d1>d2){
                    return +1;
                }else{
                    final int dx = Double.compare(center1.x, center2.x);
                    if(dx!=0) return dx;
                    final int dy = Double.compare(center1.y, center2.y);
                    return dy;
                }
            }

            private double dists(Vector2d candidate){
                double dx = (lastCameraPos.x - candidate.x);
                dx *= dx;
                double dy = (lastCameraPos.y - candidate.y);
                dy *= dy;
                return dx + dy;
            }

        };

    private final BlockingQueue queue = new ArrayBlockingQueue(1024);
    private final ThreadPoolExecutor executor;
    private final boolean debug = false;
    private JQuadView debugQuad;

    private final Map3D map3d;
    private final Terrain terrain;
    private volatile boolean needUpdate = false;

    //variables used for update
    private Vector3d lastCameraPos;
    private final Set<QuadTreeNode> nodes = new TreeSet<>(DISTANCE_COMPARATOR);
    private final ConcurrentLinkedDeque<QuadTreeNode> garbage = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean updating = new AtomicBoolean();
    private final Loader loader = new Loader();

    private GLContext externalContext;

    private List<GLRunnable> textureLoad = new ArrayList<>();

    private GLProfile glProfile;
    //private GL loaderGL;

    public TerrainUpdater(Terrain terrain) {
        this.terrain = terrain;
        this.map3d = terrain.getCanvas();

        //listen to camera changes to update
        map3d.getCamera().addPropertyChangeListener(this);

        //create all core threads now
        int nbThread = Runtime.getRuntime().availableProcessors();
        if(nbThread >= 4) nbThread -= 1; //keep a free thread for rendering
        executor = new ThreadPoolExecutor(
            nbThread, nbThread, 1, TimeUnit.MINUTES, queue);
        executor.prestartAllCoreThreads();

        if(debug){
            debugQuad = new JQuadView();
        }

        loader.start();
    }

    @Override
    public void forceUpdate() {
        needUpdate = true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //camera has changed
        final String propname = evt.getPropertyName();
        if(propname.equals(Camera.PROP_CENTER) || propname.equals(Camera.PROP_EYE)){
            needUpdate = true;
        }
    }

    public void updateScene() throws DataStoreException, TransformException, IncommensurableException {

        //remove all tasks not done yet.
        queue.clear();
        nodes.clear();

        final TrackBallCamera camera = this.map3d.getCamera();
        lastCameraPos = new Vector3d(camera.getCenter());

        final double cameraLength = camera.getLength();
        final double viewScale = camera.getViewScale(cameraLength);
        final double viewDist = camera.getProjectionLength(cameraLength)/2.0;
        final int indexScale = terrain.getNearestScaleIndex(viewScale);

        final QuadTree quadTree = terrain.getQuadTree();

        //load the remaining tiles
        final Dimension gridSize = QuadTreeUtils.getGridSize(indexScale);
        final Dimension tileSize = quadTree.getTileSize();

        //calculate the list of tiles we need to render
        final List<QuadTreeNode> viewPts = quadTree.findView(indexScale, lastCameraPos.x, lastCameraPos.y, viewDist);
        nodes.addAll(viewPts);
        final int viewSize = viewPts.size();

        //loop on terrain node and remove tiles which are not in the list
        final List<SceneNode> tiles = terrain.getChildren();
        final List<QuadTreeNode> removeNodes = new ArrayList<>();
        for(int i=tiles.size()-1;i>=0;i--){
            final SceneNode node = tiles.get(i);
            if (node instanceof QuadTreeNode) {
                final QuadTreeNode quadTreeNode = (QuadTreeNode)node;

                if(nodes.contains(quadTreeNode)){
                    //node already here, check if it needs some update
                    if(quadTreeNode.isData() && quadTreeNode.isDataImageLoaded() && quadTreeNode.isDataMNTLoaded()){
                        //no update needed
                        nodes.remove(quadTreeNode);
                    }
                }else{
                    removeNodes.add(quadTreeNode);
                }
            }
        }


        //nothing to load
        if(!nodes.isEmpty()){
            for(final QuadTreeNode node : nodes){
                node.getOrCreateData();
                tiles.add(node);

                if(!node.isDataMNTLoaded()){
                    final Runnable loader = new Runnable() {
                        @Override
                        public void run() {
                            //retest, maybe another thread did the job
                            if(!node.isDataMNTLoaded()){
                                try {
                                    updateMntOn(node);
                                } catch (Exception ex) {
                                    terrain.getCanvas().getMonitor().exceptionOccured(ex, Level.WARNING);
                                }
                            }
                        }
                    };
                    executor.execute(loader);
                }

                if(!node.isDataImageLoaded()) {
                    final Runnable loader = new Runnable() {
                        @Override
                        public void run() {
                            //retest, maybe another thread did the job
                            if(!node.isDataImageLoaded()){
                                try {
                                    updateImageOn(node);
                                } catch (Exception ex) {
                                    terrain.getCanvas().getMonitor().exceptionOccured(ex, Level.WARNING);
                                }
                            }
                        }
                    };
                    executor.execute(loader);
                }
            }
        }

        if (debug) {
            System.out.println("3D terrain update queue size : "+viewSize+" "+ queue.size());
        }

        //remove obsolete tiles
        for (QuadTreeNode rmNode : removeNodes) {
            tiles.remove(rmNode);
            garbage.add(rmNode);
        }

        if(debug){
            List<QuadTreeNode> quadNodes = new ArrayList<>();
            for (SceneNode tile : tiles){
                if (tile instanceof QuadTreeNode) {
                    quadNodes.add((QuadTreeNode)tile);
                }
            }
            debugQuad.setNodes(quadNodes);
        }
    }

    public void updateMntOn(final QuadTreeNode node) throws PortrayalException {

        if (node.isData()) {
            final SceneNode3D sceneNode3d  = node.getData();

            if (sceneNode3d instanceof Tile3D) {
                final Tile3D tile3d = (Tile3D) sceneNode3d;
                final GeneralEnvelope tileEnv = (node.getEnvelope() instanceof GeneralEnvelope)?((GeneralEnvelope)node.getEnvelope()):(new GeneralEnvelope(node.getEnvelope()));
                final Dimension textureDimension = node.getTileSize();

                final ElevationLoader loaderMNT = terrain.getElevationLoader();
                final BufferedImage targetImage = loaderMNT.getBufferedImageOf(tileEnv, textureDimension);
                final Raster rasterMNT = targetImage.getTile(0, 0);

                float[] vertices = tile3d.getVerticesAsArray();
                final Dimension ptsSize = tile3d.getPtsNumber();
                final Dimension axisSize = tile3d.getAxisNumber();

                for (int x=0; x<ptsSize.width; x++) {
                    for (int y=0; y<ptsSize.height; y++) {

                        // (i+j*axis0Pts)*3

                        final int col = XMath.clamp(x-1, 0, axisSize.width - 1);
                        final int row = XMath.clamp(y-1, 0, axisSize.height - 1);

                        final int pixel0 = XMath.clamp((int)(((double)col/((double)axisSize.width-1.0))*textureDimension.width), 0, textureDimension.width-1);
                        final int pixel1 = XMath.clamp((int)(((double)row/((double)axisSize.height-1.0))*textureDimension.height), 0, textureDimension.height-1);

                        final int coord = x + y * ptsSize.width;
                        final int coordZ = coord *3 + 2;

                        vertices[coordZ] = rasterMNT.getSampleFloat(pixel0,pixel1,0);
                        if(Float.isNaN(vertices[coordZ])){
                            vertices[coordZ] = (float)loaderMNT.getMinimumElevation();
                        }

                        if (x == 0 || y == 0 || x == ptsSize.width-1 || y == ptsSize.height-1) {
                            vertices[coordZ] += Tile3D.borderZTranslate;
                        }

                    }
                }

                node.setDataMNT(vertices);
            }
        } else {
            System.out.println("Try to update MNT on " + node.getPosition() + " but has no data");
        }
    }

    public void updateImageOn(final QuadTreeNode node) throws PortrayalException {
        if (node.isData()) {
            final Envelope tileEnv = node.getEnvelope();
            final Dimension textureDimension = node.getTileSize();
            final ImageLoader loaderImg = terrain.getImageLoader();

            final BufferedImage targetImage = loaderImg.getBufferedImageOf(tileEnv, textureDimension);
            final TextureData data = new AWTTextureData(glProfile, 0, 0, false, targetImage);
            node.setDataImage(data);
        } else {
            System.out.println("Try to update image on " + node.getPosition() + " but has no data");
        }
    }

    public synchronized void initialize(GLAutoDrawable glDrawable) {
        this.glProfile = glDrawable.getGLProfile();

        final GLDrawableFactory factory = glDrawable.getFactory();
        externalContext = factory.createExternalGLContext();
    }

    @Override
    public synchronized void update(GLAutoDrawable glDrawable){

        //clear garbage
        for(QuadTreeNode candidate = garbage.pollFirst();candidate!=null;candidate=garbage.pollFirst()){
            candidate.dispose(glDrawable);
        }

        if(!needUpdate) return;
        needUpdate = false;

        queue.clear();
        updating.set(true);
        synchronized(LOCK){
            LOCK.notifyAll();
        }
    }

    public void stopUpdate(boolean awaitTermination) throws InterruptedException {
        executor.shutdown();
    }

//    /**
//     * Not working right yet.
//     * Waiting for sgothel answer.
//     *
//     * @param gl
//     * @return
//     */
//    private GL createLoaderGL(GL gl) {
//        final GLContext baseContext = gl.getContext();
//        final boolean isCurrent = baseContext.isCurrent();
//        final GLProfile profile = gl.getGLProfile();
//        final GLDrawableFactory factory = GLDrawableFactory.getFactory(profile);
//        final AbstractGraphicsDevice device = baseContext.getGLDrawable().getNativeSurface().getGraphicsConfiguration().getScreen().getDevice();
//        final GLDrawable loaderDrawable = factory.createDummyDrawable(device, true, profile);
//        loaderDrawable.setRealized(true);
//        final GLContext loaderContext = loaderDrawable.createContext(baseContext);
//
//        makeCurrent(loaderContext);
//        if(isCurrent) {
//            makeCurrent(baseContext);
//        }else{
//            loaderContext.release();
//        }
//        return loaderContext.getGL();
//    }

    private void makeCurrent(GLContext ctx) {
        if( GLContext.CONTEXT_NOT_CURRENT >= ctx.makeCurrent() ) {
            throw new GLException("Couldn't make ctx current: "+ctx);
        }
    }

    private final Object LOCK = new Object();
    private class Loader extends Thread{

        public void doWait(){
            synchronized(LOCK){
                if(!updating.get()){
                    try {
                        LOCK.wait();
                    } catch (InterruptedException ex) {
                        Map3D.LOGGER.log(Level.INFO, ex.getMessage(),ex);
                    }
                }
            }
        }
        @Override
        public void run() {
            while(true){
                while(updating.getAndSet(false)){
                    try {
                        updateScene();
                    } catch (DataStoreException | TransformException | IncommensurableException ex) {
                        Map3D.LOGGER.log(Level.INFO, ex.getMessage(),ex);
                    }
                }
                doWait();
            }
        }
    }

}

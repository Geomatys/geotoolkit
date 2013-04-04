/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.display3d.container;

import com.ardor3d.framework.Scene;
import com.ardor3d.image.Texture;
import com.ardor3d.image.util.awt.AWTImageLoader;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.light.DirectionalLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.extension.Skybox;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.ResourceSource;
import com.ardor3d.util.resource.URLResourceSource;
import java.net.URL;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.map.MapContext;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.display.canvas.Canvas;

/**
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public final class A3DContainer implements Scene {

    static {
        //register image loaders
        AWTImageLoader.registerLoader();
    }

    private final A3DCanvas canvas;
    private final Node root = new Node("root");
    private final Node scene = new Node("scene");
    private final Skybox skybox = buildSkyBox();

    private MapItemNode contextNode = null;
    private MapContext context = null;

    public A3DContainer(final A3DCanvas canvas) {
        ArgumentChecks.ensureNonNull("canvas", canvas);
        this.canvas = canvas;

        // Zbuffer -------------------------------------------------------------
        final ZBufferState buf = new ZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        root.setRenderState(buf);

        // Lights --------------------------------------------------------------
        final DirectionalLight dLight = new DirectionalLight();
        dLight.setEnabled(true);
        dLight.setAttenuate(true);
        dLight.setDiffuse(new ColorRGBA(1f, 1f, 1f, 0.5f));
        dLight.setDirection(new Vector3(-1, -1, -1));
        final DirectionalLight dLight2 = new DirectionalLight();
        dLight2.setEnabled(true);
        dLight2.setAttenuate(true);
        dLight2.setDiffuse(new ColorRGBA(1f, 1f, 1f, 0.5f));
        dLight2.setDirection(new Vector3(1, 1, 1));

        final LightState lightState = new LightState();
        lightState.attach(dLight);
        lightState.attach(dLight2);
        lightState.setTwoSidedLighting(false);
        lightState.setEnabled(true);
        root.setRenderState(lightState);

        // ---------------------------------------------------------------------
        final WireframeState wireframeState = new WireframeState();
        wireframeState.setEnabled(false);
        root.setRenderState(wireframeState);
        root.getSceneHints().setRenderBucketType(RenderBucketType.Opaque);

        //--------------------------------------------------------------------
        final CullState cullFrontFace = new CullState();
        cullFrontFace.setEnabled(true);
        cullFrontFace.setCullFace(CullState.Face.Back);
        root.setRenderState(cullFrontFace);
        //root.setRenderState(buildFog());

        // Skybox --------------------------------------------------------------
        root.attachChild(skybox);
        root.attachChild(scene);
        
    }

    /**
     * @return Root node of the scene
     */
    public Node getRoot() {
        return root;
    }

    /**
     * @return Scene node
     */
    public Node getScene(){
        return scene;
    }
    
    /**
     * @return currently rendered mapcontext
     */
    public MapContext getContext() {
        return context;
    }

    /**
     * @return set rendered map context
     */
    public void setContext(final MapContext context) {
        this.context = context;
        
        if(contextNode != null){
            contextNode.removeFromParent();
        }

        contextNode = new MapItemNode(canvas, context);
        scene.attachChild(contextNode);
    }

    @Override
    public boolean renderUnto(final Renderer renderer) {
        // Execute renderQueue item
        canvas.getTaskQueueManager().getQueue(GameTaskQueue.RENDER).execute();
        renderer.draw(root);
        return true;
    }

    @Override
    public PickResults doPick(final Ray3 pickRay) {
        // does nothing.
        return null;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void update(final Camera camera, final double tpf, final boolean b) {
        skybox.setTranslation(camera.getLocation());
    }

    /**
     * Builds the sky box.
     */
    private static Skybox buildSkyBox() {
        final Skybox skybox = new Skybox("skybox", 10,10,10);
        
        final String name = "space";
        final String dir = "/images/skybox/"+name+"/";

        final Texture single = TextureManager.load(
                new URLResourceSource(A3DContainer.class.getResource(dir + name+".jpg")), 
                Texture.MinificationFilter.BilinearNearestMipMap, true);
//        final Texture north = TextureManager.load(
//                new URLResourceSource(A3DContainer.class.getResource(dir + name+".jpg")), 
//                Texture.MinificationFilter.BilinearNearestMipMap, true);
//        final Texture south = TextureManager.load(
//                new URLResourceSource(A3DContainer.class.getResource(dir + name+"_south.jpg")), 
//                Texture.MinificationFilter.BilinearNearestMipMap, true);
//        final Texture east  = TextureManager.load(
//                new URLResourceSource(A3DContainer.class.getResource(dir + name+"_east.jpg")),  
//                Texture.MinificationFilter.BilinearNearestMipMap, true);
//        final Texture west  = TextureManager.load(
//                new URLResourceSource(A3DContainer.class.getResource(dir + name+"_west.jpg")), 
//                Texture.MinificationFilter.BilinearNearestMipMap, true);
//        final Texture up    = TextureManager.load(
//                new URLResourceSource(A3DContainer.class.getResource(dir + name+"_up.jpg")), 
//                Texture.MinificationFilter.BilinearNearestMipMap, true);
//        final Texture down  = TextureManager.load(
//                new URLResourceSource(A3DContainer.class.getResource(dir + name+"_down.jpg")), 
//                Texture.MinificationFilter.BilinearNearestMipMap, true);

        skybox.setTexture(Skybox.Face.North, single);
        skybox.setTexture(Skybox.Face.West, single);
        skybox.setTexture(Skybox.Face.South, single);
        skybox.setTexture(Skybox.Face.East, single);
        skybox.setTexture(Skybox.Face.Up, single);
        skybox.setTexture(Skybox.Face.Down, single);

        return skybox;
    }
  
}

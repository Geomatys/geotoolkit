/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
 *    (C) 2009, Johann Sorel
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

import com.ardor3d.annotation.MainThread;
import com.ardor3d.extension.model.collada.ColladaImporter;
import com.ardor3d.framework.Scene;
import com.ardor3d.image.Image;
import com.ardor3d.image.Texture;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.light.DirectionalLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.ComplexSpatialController;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.extension.Skybox;
import com.ardor3d.scenegraph.hint.DataMode;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.TextureManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.display3d.primitive.A3DGraphic;
import org.geotoolkit.map.MapContext;

import org.opengis.display.canvas.Canvas;
import org.opengis.display.container.ContainerListener;
import org.opengis.display.container.GraphicsContainer;
import org.opengis.geometry.Envelope;

/**
 * @author Johann Sorel (Puzzle-GIS)
 */
public final class A3DContainer implements Scene, GraphicsContainer<A3DGraphic> {

    static {
        //register image loaders
        AWTImageLoader.registerLoader();
    }

    private final A3DCanvas canvas;
    private final Node root = new Node("root");
    private final Node scene = new Node("scene");
    private final Skybox skybox = buildSkyBox();

    private ContextNode contextNode = null;
    private MapContext context = null;

    public A3DContainer(A3DCanvas canvas) {
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

        // ---------------------------------------------------------------------
//        final CullState cullFrontFace = new CullState();
//        cullFrontFace.setEnabled(true);
//        cullFrontFace.setCullFace(CullState.Face.None);
//        root.setRenderState(cullFrontFace);
//        root.setRenderState(buildFog());

        // Skybox --------------------------------------------------------------
        root.attachChild(skybox);
        root.attachChild(scene);

//        try {
//            scene.attachChild(createDynamicNode());
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//        }

    }



    public Node createDynamicNode() throws MalformedURLException{
        final Node group = new Node("planes");
        group.setTranslation(0, 200, 0);

        final Node plane1 = ColladaImporter.readColladaScene(A3DContainer.class.getResource("/models/mirage.dae"));
        plane1.setRotation(new Matrix3().fromAngleNormalAxis(Math.PI * -0.5, new Vector3(1, 0, 0)));
        plane1.setScale(0.2,0.2,0.2);

        group.attachChild(plane1);

        group.addController(new ComplexSpatialController<Node>(){

            double rayon = 3000;
            double step = Math.PI;
            double angle = 0;

            @Override
            public void update(double time, Node caller) {
                angle += step*time/10f;
                if(angle >= Math.PI*2){
                    angle -= Math.PI*2;
                }
//                reactor.forceRespawn();
                Matrix3 rt = new Matrix3().fromAngleNormalAxis(Math.PI-angle, new Vector3(0, 1, 0));
                caller.setRotation(rt);
                caller.setTranslation(Math.cos(angle)*rayon, 500, Math.sin(angle)*rayon);
//                caller.updateWorldTransform(true);
            }
        });

//        group.getSceneHints().setLightCombineMode(LightCombineMode.Off);

        final CullState cullFrontFace = new CullState();
        cullFrontFace.setEnabled(true);
        cullFrontFace.setCullFace(CullState.Face.Back);
        group.setRenderState(cullFrontFace);

        return group;
    }





    private double translateX = 0;
    private double translateY = 0;
    private double scaling = 1f;


    public ReadOnlyVector3 correctLocation(Vector3 vect){
        Vector3 corrected = new Vector3(vect);
        corrected.setX(corrected.getX()/scaling +translateX);
        corrected.setZ(corrected.getZ()/scaling +translateY);
        return corrected;
    }

    public MapContext getContext() {
        return context;
    }

    public void setContext(MapContext context, boolean loadAll) {
        this.context = context;

        if(contextNode != null){
            contextNode.removeFromParent();
        }

        contextNode = new ContextNode(canvas, context, loadAll);
        try {
            Envelope env = context.getBounds();
            translateX = env.getMedian(0);
            translateY = env.getMedian(1);
            contextNode.setTranslation(-translateX*scaling,0,-translateY*scaling);

        } catch (IOException ex) {
            Logger.getLogger(A3DContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        scene.attachChild(contextNode);
    }

    public void setScaling(double scaling) {
        this.scaling = scaling;
    }

    public Node getRoot() {
        return root;
    }

    public Node getScene(){
        return scene;
    }

    @MainThread
    @Override
    public boolean renderUnto(final Renderer renderer) {
        // Execute renderQueue item
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).execute();

        renderer.draw(root);
//        Debugger.drawNormals(root, renderer);
        return true;
    }

    @Override
    public PickResults doPick(final Ray3 pickRay) {
        // does nothing.
        return null;
    }

    @Override
    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public Envelope getGraphicsEnvelope() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<A3DGraphic> graphics() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addContainerListener(ContainerListener arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeContainerListener(ContainerListener arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dispose() {
    }

    public void update(Camera camera, double tpf, boolean b) {
        if(scene.getScale().getX() != scaling){
            scene.setScale(scaling);
        }
        
        skybox.setTranslation(camera.getLocation());
    }


    /**
     * Setup fog.
     */
//    private FogState buildFog() {
//        final FogState fogState = new FogState();
//        fogState.setDensity(1.0f);
//        fogState.setEnabled(true);
//        fogState.setColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
//        fogState.setEnd((float) farPlane);
//        fogState.setStart((float) farPlane / 10.0f);
//        fogState.setDensityFunction(FogState.DensityFunction.Linear);
//        fogState.setQuality(FogState.Quality.PerVertex);
//        return fogState;
//    }

    /**
     * Builds the sky box.
     */
    private static Skybox buildSkyBox() {
        Skybox skybox = new Skybox("skybox", 10,10,10);

        final String name = "mystic";
        final String dir = "/images/skybox/"+name+"/";

        final Texture north = TextureManager.load(
                A3DContainer.class.getResource(dir + name+"_north.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap, Image.Format.Guess, true);
        final Texture south = TextureManager.load(
                A3DContainer.class.getResource(dir + name+"_south.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap, Image.Format.Guess, true);
        final Texture east = TextureManager.load(
                A3DContainer.class.getResource(dir + name+"_east.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap, Image.Format.Guess, true);
        final Texture west = TextureManager.load(
                A3DContainer.class.getResource(dir + name+"_west.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap, Image.Format.Guess, true);
        final Texture up = TextureManager.load(
                A3DContainer.class.getResource(dir + name+"_up.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap, Image.Format.Guess, true);
        final Texture down = TextureManager.load(
                A3DContainer.class.getResource(dir + name+"_down.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap, Image.Format.Guess, true);

        skybox.setTexture(Skybox.Face.North, north);
        skybox.setTexture(Skybox.Face.West, west);
        skybox.setTexture(Skybox.Face.South, south);
        skybox.setTexture(Skybox.Face.East, east);
        skybox.setTexture(Skybox.Face.Up, up);
        skybox.setTexture(Skybox.Face.Down, down);

        return skybox;
    }
  
}

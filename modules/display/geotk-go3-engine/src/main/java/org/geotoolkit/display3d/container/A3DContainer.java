
package org.geotoolkit.display3d.container;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.framework.Scene;
import com.ardor3d.image.Image;
import com.ardor3d.image.Texture;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.light.DirectionalLight;
import com.ardor3d.light.Light;
import com.ardor3d.light.PointLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.BlendState.DestinationFunction;
import com.ardor3d.renderer.state.BlendState.SourceFunction;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.FogState;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.Spatial.LightCombineMode;
import com.ardor3d.scenegraph.event.DirtyType;
import com.ardor3d.scenegraph.extension.Skybox;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.scenegraph.shape.Quad;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.geom.BufferUtils;
import java.nio.FloatBuffer;
import java.util.Collection;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.display3d.primitive.A3DGraphic;
import org.geotoolkit.display3d.primitive.SkyDome;
import org.geotoolkit.map.MapContext;
import org.opengis.display.canvas.Canvas;
import org.opengis.display.container.ContainerListener;
import org.opengis.display.container.GraphicsContainer;
import org.opengis.geometry.Envelope;

/**
 * @author Johann Sorel (Puzzle-GIS)
 */
public final class A3DContainer implements Scene, GraphicsContainer<A3DGraphic> {

    private final A3DCanvas canvas;
    private final Node root = new Node("root");
    private ContextNode contextNode = null;
    private MapContext context = null;
//    final LightState lightState;

    /** The far plane. */
    private final double farPlane = 2000.0;

    /** The skybox. */
    private Skybox skybox;
    private SkyDome skydomeUp;

    public A3DContainer(A3DCanvas canvas) {
        this.canvas = canvas;

        /**
         * Create a ZBuffer to display pixels closest to the camera above farther ones.
         */
        final ZBufferState buf = new ZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        root.setRenderState(buf);


        // ---- LIGHTS
        /** Set up a basic, default light. */
        final PointLight light = new PointLight();
        light.setAttenuate(false);
        light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
        light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1f));
        light.setLocation(new Vector3(0, 200, 0));
        light.setEnabled(true);

        /** Attach the light to a lightState and the lightState to rootNode. */
        LightState lightState = new LightState();
        lightState.setEnabled(true);
        lightState.attach(light);
        root.setRenderState(lightState);

        WireframeState wireframeState = new WireframeState();
        wireframeState.setEnabled(false);
        root.setRenderState(wireframeState);
        root.setRenderBucketType(RenderBucketType.Opaque);


        // Setup some standard states for the scene.
        final CullState cullFrontFace = new CullState();
        cullFrontFace.setEnabled(true);
        cullFrontFace.setCullFace(CullState.Face.None);
        root.setRenderState(cullFrontFace);

        root.setRenderState(buildFog());

        //build the skybox
        AWTImageLoader.registerLoader();

//        skydomeUp = buildSkyDome();

        root.attachChild(buildSkyBox());
//        root.attachChild(skydomeUp);
        root.attachChild(buildPlan());

        
    }

    public MapContext getContext() {
        return context;
    }

    public void setContext(MapContext context) {
        this.context = context;

        if(contextNode != null){
            contextNode.removeFromParent();
        }

        contextNode = new ContextNode(canvas, context);
        root.attachChild(contextNode);
//        // setup a target to LightNode, if you dont want terrain with light's effect remove it.
//        skydomeUp.setTarget(contextNode);
    }

    public Node getRoot() {
        return root;
    }

    @MainThread
    @Override
    public boolean renderUnto(final Renderer renderer) {
        renderer.draw(root);
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void update(Camera camera, double tpf, boolean b) {
        
        skybox.setTranslation(camera.getLocation());

//        if(contextNode != null){
//            contextNode.updateGeometricState(tpf, b);
//        }
    }


    private Node buildPlan(){
        final Node plan = new Node("plan");
        plan.setLightCombineMode(LightCombineMode.Off);

        final float over = 0.1f;
        final float width = 1.2f;
        final float step = 250;
        final int lenght = 1000;

        final Quad back = new Quad();
        back.initialize(2*lenght, 2*lenght);
        back.setDefaultColor(new ColorRGBA(1, 1, 1, 0.6f));
        back.setModelBound(new BoundingBox());
        back.updateModelBound();

        final BlendState blend = new BlendState();
        blend.setBlendEnabled(true);
        blend.setSourceFunction(SourceFunction.SourceAlpha);
        blend.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);


        for(int i=-lenght;i<=lenght;i+=step){
            final FloatBuffer verts = BufferUtils.createVector3Buffer(2);
            verts.put(i).put(-lenght).put(over);
            verts.put(i).put(lenght).put(over);
            Line line = new Line("Lines", verts, null, null, null);
            line.getMeshData().setIndexMode(IndexMode.LineStrip);
            line.setLineWidth(width);
            line.setDefaultColor(ColorRGBA.DARK_GRAY);
            line.setAntialiased(true);
            line.setModelBound(new BoundingBox());
            line.updateModelBound();
            plan.attachChild(line);
        }

        for(int i=-lenght;i<=lenght;i+=step){
            final FloatBuffer verts = BufferUtils.createVector3Buffer(2);
            verts.put(-lenght).put(i).put(over);
            verts.put(lenght).put(i).put(over);
            Line line = new Line("Lines", verts, null, null, null);
            line.getMeshData().setIndexMode(IndexMode.LineStrip);
            line.setLineWidth(width);
            line.setDefaultColor(ColorRGBA.DARK_GRAY);
            line.setAntialiased(true);
            line.setModelBound(new BoundingBox());
            line.updateModelBound();
            plan.attachChild(line);
        }

        plan.attachChild(back);
        plan.setRenderState(blend);
        plan.setCullHint(Spatial.CullHint.Never);

        plan.setRotation(new Matrix3().fromAngleNormalAxis(Math.PI * -0.5, new Vector3(1, 0, 0)));

        return plan;
    }

    /**
     * Setup fog.
     */
    private FogState buildFog() {
        final FogState fogState = new FogState();
        fogState.setDensity(1.0f);
        fogState.setEnabled(true);
        fogState.setColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        fogState.setEnd((float) farPlane);
        fogState.setStart((float) farPlane / 10.0f);
        fogState.setDensityFunction(FogState.DensityFunction.Linear);
        fogState.setQuality(FogState.Quality.PerVertex);
        return fogState;
    }

    /**
     * Builds the sky box.
     */
    private Skybox buildSkyBox() {
        skybox = new Skybox("skybox", 10,10,10);

        final String name = "default";
        final String dir = "/images/skybox/"+name+"/";

        //normal order : 1 3 2 4 6 5

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

//        skybox.setRotation(new Matrix3().fromAngleNormalAxis(Math.PI * 0.5, new Vector3(1, 0, 0)));

        return skybox;
    }

    private SkyDome buildSkyDome() {
        SkyDome skydome = new SkyDome("skyskydome", new Vector3(0.0f,0.0f,0.0f), 11, 18, 850f);
//        skydome.setModelBound(new BoundingSphere());
//        skydome.updateModelBound();
//        skydome.updateRenderState();
        skydome.setUpdateTime(1.0f);
        skydome.setTimeWarp(720.0f);
        skydome.setDay(267);
        skydome.setLatitude(-22.9f);
        skydome.setLongitude(-47.083f);
        skydome.setStandardMeridian(-45.0f);
        skydome.setSunPosition(5.75f);             // 5:45 am
        skydome.setTurbidity(2.0f);
        skydome.setSunEnabled(false);
        skydome.setExposure(true, 18.0f);
        skydome.setOvercastFactor(0.0f);
        skydome.setGammaCorrection(2.5f);
//        skydome.setRootNode(root);
        skydome.setIntensity(1.0f);

//        skydome.setTranslation(0, -50, 0);

        return skydome;
    }
    

}

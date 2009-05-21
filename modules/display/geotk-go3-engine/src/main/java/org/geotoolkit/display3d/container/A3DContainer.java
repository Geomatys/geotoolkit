
package org.geotoolkit.display3d.container;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.bounding.BoundingBox;
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
import com.ardor3d.renderer.state.FogState;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.Spatial.LightCombineMode;
import com.ardor3d.scenegraph.extension.Skybox;
import com.ardor3d.scenegraph.shape.Quad;
import com.ardor3d.util.TextureManager;

import java.io.IOException;
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

    private final double farPlane = 2000.0;
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
        dLight.setDiffuse(new ColorRGBA(1, 1, 1, 1));
        dLight.setDirection(new Vector3(-1, -1, -1));
        final DirectionalLight dLight2 = new DirectionalLight();
        dLight2.setEnabled(true);
        dLight2.setDiffuse(new ColorRGBA(1, 1, 1, 1));
        dLight2.setDirection(new Vector3(1, 1, 1));

        final LightState lightState = new LightState();
        lightState.attach(dLight);
        lightState.attach(dLight2);
        lightState.setTwoSidedLighting(false);
        lightState.setEnabled(true);
        root.setRenderState(lightState);
        root.setLightCombineMode(LightCombineMode.Replace);

        // ---------------------------------------------------------------------
        WireframeState wireframeState = new WireframeState();
        wireframeState.setEnabled(false);
        root.setRenderState(wireframeState);
        root.setRenderBucketType(RenderBucketType.Opaque);

        // ---------------------------------------------------------------------
        final CullState cullFrontFace = new CullState();
        cullFrontFace.setEnabled(true);
        cullFrontFace.setCullFace(CullState.Face.None);
        root.setRenderState(cullFrontFace);
        root.setRenderState(buildFog());

        // Skybox --------------------------------------------------------------
        root.attachChild(skybox);
    }

    private double translateX = 0;
    private double translateY = 0;
    private final double scaleX = 0.2;
    private final double scaleY = 0.2;


    public ReadOnlyVector3 correctLocation(Vector3 vect){
        Vector3 corrected = new Vector3(vect);
        corrected.setX(corrected.getX()/scaleX +translateX);
        corrected.setZ(corrected.getZ()/scaleY +translateY);
        return corrected;
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
        try {
            Envelope env = context.getBounds();
            contextNode.setScale(0.2f,1, 0.2f);
            translateX = env.getMedian(0);
            translateY = env.getMedian(1);
            contextNode.setTranslation(-translateX*scaleX,0,-translateY*scaleY);

        } catch (IOException ex) {
            Logger.getLogger(A3DContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
//        contextNode.setScale(0.2f, 1, 0.2f);
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void update(Camera camera, double tpf, boolean b) {
        skybox.setTranslation(camera.getLocation());
    }

    private Node buildPlan(final Envelope env){
        final Node plan = new Node("plan");
        plan.setLightCombineMode(LightCombineMode.Off);

        final float over = 0.1f;
        final float width = 1.2f;
        final float minx = (float) env.getMinimum(0);
        final float maxx = (float) env.getMaximum(0);
        final float miny = (float) env.getMinimum(1);
        final float maxy = (float) env.getMaximum(1);

        final Quad back = new Quad();
        back.initialize(env.getSpan(0), env.getSpan(1));
        back.setDefaultColor(new ColorRGBA(1, 1, 1, 0.6f));
        back.setTranslation(env.getMedian(0), env.getMedian(1), 0);
        back.setModelBound(new BoundingBox());
        back.updateModelBound();

//        final BlendState blend = new BlendState();
//        blend.setBlendEnabled(true);
//        blend.setSourceFunction(SourceFunction.SourceAlpha);
//        blend.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
//
//        final int nbGrid = 50;
//        float step = (float) (env.getSpan(0) / nbGrid);

//        for(int i=0;i<=nbGrid;i++){
//            final FloatBuffer verts = BufferUtils.createVector3Buffer(2);
//            verts.put(minx +step*i).put(miny).put(over);
//            verts.put(minx +step*i).put(maxy).put(over);
//            Line line = new Line("Lines", verts, null, null, null);
//            line.getMeshData().setIndexMode(IndexMode.LineStrip);
//            line.setLineWidth(width);
//            line.setDefaultColor(ColorRGBA.DARK_GRAY);
//            line.setAntialiased(true);
//            line.setModelBound(new BoundingBox());
//            line.updateModelBound();
//            plan.attachChild(line);
//        }
//
//        step = (float) (env.getSpan(1) / nbGrid);
//
//        for(int i=0;i<=nbGrid;i++){
//            final FloatBuffer verts = BufferUtils.createVector3Buffer(2);
//            verts.put(miny +step*i).put(minx).put(over);
//            verts.put(miny +step*i).put(maxx).put(over);
//            Line line = new Line("Lines", verts, null, null, null);
//            line.getMeshData().setIndexMode(IndexMode.LineStrip);
//            line.setLineWidth(width);
//            line.setDefaultColor(ColorRGBA.DARK_GRAY);
//            line.setAntialiased(true);
//            line.setModelBound(new BoundingBox());
//            line.updateModelBound();
//            plan.attachChild(line);
//        }


        plan.attachChild(back);
//        plan.setRenderState(blend);
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
    private static Skybox buildSkyBox() {
        Skybox skybox = new Skybox("skybox", 10,10,10);

        final String name = "default";
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

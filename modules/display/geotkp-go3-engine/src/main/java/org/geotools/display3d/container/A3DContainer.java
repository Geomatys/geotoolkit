
package org.geotools.display3d.container;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.Scene;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.light.DirectionalLight;
import com.ardor3d.light.Light;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Ray3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial.LightCombineMode;
import com.ardor3d.scenegraph.shape.Quad;
import com.ardor3d.util.geom.BufferUtils;
import java.nio.FloatBuffer;
import java.util.Collection;
import org.geotools.display3d.canvas.A3DCanvas;
import org.geotools.display3d.primitive.A3DGraphic;
import org.geotools.map.MapContext;
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

    public A3DContainer(A3DCanvas canvas) {
        this.canvas = canvas;

        final ZBufferState buf = new ZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        root.setRenderState(buf);

        Light light = new DirectionalLight();
        light.setAttenuate(false);
        light.setShadowCaster(false);
        light.setDiffuse(new ColorRGBA(1f,1f,1f,1f));
        light.setAmbient(new ColorRGBA(1f,1f,1f,1f));
        light.setEnabled(true);

        /** Attach the light to a lightState and the lightState to rootNode. */
        final LightState lightState = new LightState();
        lightState.setEnabled(true);
        lightState.attach(light);
        lightState.setTwoSidedLighting(true);

//        root.attachChild(buildPlan());
        root.setRenderState(lightState);
        root.setScale(0.1f);
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

//    private Node buildPlan(){
//        final Node plan = new Node("plan");
//
//        final Quad back = new Quad();
//        back.initialize(1000, 1000);
//        back.setLightCombineMode(LightCombineMode.Off);
//        back.setDefaultColor(ColorRGBA.DARK_GRAY);
//
////        final BlendState blend = new BlendState();
////        blend.setBlendEnabled(true);
////        blend.setSourceFunction(SourceFunction.SourceAlpha);
////        blend.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
//
//        for(int i=-500;i<500;i+=10){
//            final FloatBuffer verts = BufferUtils.createVector3Buffer(2);
//            verts.put(i).put(-500).put(0.001f);
//            verts.put(i).put(500).put(0.001f);
//            Line line = new Line("Lines", verts, null, null, null);
//            line.getMeshData().setIndexMode(IndexMode.LineStrip);
//            line.setLineWidth(1);
//            line.setLightCombineMode(LightCombineMode.Off);
//            line.setDefaultColor(ColorRGBA.ORANGE);
////            line.setAntialiased(true);
//            plan.attachChild(line);
//        }
//
//        for(int i=-500;i<500;i+=10){
//            final FloatBuffer verts = BufferUtils.createVector3Buffer(2);
//            verts.put(-500).put(i).put(0.001f);
//            verts.put(500).put(i).put(0.001f);
//            Line line = new Line("Lines", verts, null, null, null);
//            line.getMeshData().setIndexMode(IndexMode.LineStrip);
//            line.setLineWidth(1);
//            line.setLightCombineMode(LightCombineMode.Off);
//            line.setDefaultColor(ColorRGBA.ORANGE);
////            line.setAntialiased(true);
//            plan.attachChild(line);
//        }
//
//        plan.attachChild(back);
////        plan.setRenderState(blend);
//        return plan;
//    }

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

}

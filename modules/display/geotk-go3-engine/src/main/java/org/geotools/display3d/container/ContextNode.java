
package org.geotools.display3d.container;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Quad;
import com.ardor3d.util.geom.BufferUtils;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.display3d.canvas.A3DCanvas;
import org.geotools.display3d.primitive.A3DGraphic;
import org.geotools.map.GraphicBuilder;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class ContextNode extends A3DGraphic{

    private static final GraphicBuilder<A3DGraphic> DEFAULT_BUILDER = new A3DGraphicBuilder();

    private final MapContext context;

    public ContextNode(A3DCanvas canvas, MapContext context) {
        this.context = context;

        attachChild(buildPlan());

        double z = 10d;
        for(final MapLayer layer : context.layers()){

            GraphicBuilder<? extends A3DGraphic> builder = layer.getGraphicBuilder(A3DGraphic.class);

            if(builder == null){
                builder = DEFAULT_BUILDER;
            }

            Collection<? extends A3DGraphic> graphics = builder.createGraphics(layer, canvas);

            for(A3DGraphic gra : graphics){
                gra.setTranslation(0, 0, z);
                this.attachChild(gra);
            }
            z += 10d;
        }

        final Envelope env;
        try {
            env = context.getBounds();

            float minX = (float) env.getMinimum(0);
            float minY = (float) env.getMinimum(1);
            float maxX = (float) env.getMaximum(0);
            float maxY = (float) env.getMaximum(1);

            setTranslation(-env.getMedian(0), -env.getMedian(1), 0);
//            setScale(0.999f);
//            setScale(0.9f, 0.9f, 1);



        } catch (IOException ex) {
            Logger.getLogger(ContextNode.class.getName()).log(Level.SEVERE, null, ex);
        }


    }


    private Node buildPlan(){
        final Node plan = new Node("plan");

        final Envelope env;
        try {
            env = context.getBounds();
        } catch (IOException ex) {
            Logger.getLogger(ContextNode.class.getName()).log(Level.SEVERE, null, ex);
            return plan;
        }

        plan.setTranslation(env.getMedian(0), env.getMedian(1), -0.1f);
        float minX = (float) env.getMinimum(0);
        float minY = (float) env.getMinimum(1);
        float maxX = (float) env.getMaximum(0);
        float maxY = (float) env.getMaximum(1);


        final Quad back = new Quad();
        back.initialize(maxX-minX, maxY-minY);
        back.setLightCombineMode(LightCombineMode.Off);
        back.setDefaultColor(ColorRGBA.DARK_GRAY);

        plan.attachChild(back);

//        final BlendState blend = new BlendState();
//        blend.setBlendEnabled(true);
//        blend.setSourceFunction(SourceFunction.SourceAlpha);
//        blend.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);

        float step = (maxX - minX) / 100f;

        for(float i= minX;i<=maxX;i+=step){
            final FloatBuffer verts = BufferUtils.createVector3Buffer(2);
            verts.put(i).put(minY).put(0.001f);
            verts.put(i).put(maxY).put(0.001f);
            Line line = new Line("Lines", verts, null, null, null);
            line.getMeshData().setIndexMode(IndexMode.LineStrip);
            line.setLineWidth(1);
            line.setLightCombineMode(LightCombineMode.Off);
            line.setDefaultColor(ColorRGBA.ORANGE);
//            line.setAntialiased(true);
            plan.attachChild(line);
        }

        for(float i= minY;i<=maxY;i+=step){
            final FloatBuffer verts = BufferUtils.createVector3Buffer(2);
            verts.put(minX).put(i).put(0.001f);
            verts.put(maxX).put(i).put(0.001f);
            Line line = new Line("Lines", verts, null, null, null);
            line.getMeshData().setIndexMode(IndexMode.LineStrip);
            line.setLineWidth(1);
            line.setLightCombineMode(LightCombineMode.Off);
            line.setDefaultColor(ColorRGBA.ORANGE);
//            line.setAntialiased(true);
            plan.attachChild(line);
        }

//        plan.setRenderState(blend);
        return plan;
    }

}

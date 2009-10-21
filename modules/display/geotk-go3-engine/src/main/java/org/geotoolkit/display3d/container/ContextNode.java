/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.state.ClipState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.util.geom.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.display3d.primitive.A3DGraphic;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;

import org.geotoolkit.util.logging.Logging;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class ContextNode extends A3DGraphic{

    private static final GraphicBuilder<A3DGraphic> DEFAULT_BUILDER = new ProgressiveA3DGraphicBuilder();
    private static final GraphicBuilder<A3DGraphic> FULL_LOAD_BUILDER = new FullLoadA3DGraphicBuilder();

    private final MapContext context;

    public ContextNode(A3DCanvas canvas, MapContext context, boolean loadAll) {
        super(canvas);
        this.context = context;


        try {
            this.attachChild(buildPlan(context.getBounds()));
        } catch (IOException ex) {
            Logging.getLogger(ContextNode.class).log(Level.SEVERE, null, ex);
        }

        for(final MapLayer layer : context.layers()){

            GraphicBuilder<? extends A3DGraphic> builder = layer.getGraphicBuilder(A3DGraphic.class);

            if(builder == null){
                builder = (loadAll) ? FULL_LOAD_BUILDER : DEFAULT_BUILDER;
            }

            for(A3DGraphic gra : builder.createGraphics(layer, canvas)){
                this.attachChild(gra);
            }
        }
    }

    private Node buildPlan(final Envelope env){
        final Node plan = new Node("plan");
        plan.getSceneHints().setLightCombineMode(LightCombineMode.Off);

        final float over = -10f;
        final float width = 1f;
        final float minx = (float) env.getMinimum(0);
        final float maxx = (float) env.getMaximum(0);
        final float miny = (float) env.getMinimum(1);
        final float maxy = (float) env.getMaximum(1);

        final Box back = new Box("ceiling", new Vector3(minx, -env.getSpan(0)/20 -13, miny), new Vector3(maxx, -13, maxy));
        back.setDefaultColor(new ColorRGBA(1, 1, 1, 1f));
        back.setModelBound(new BoundingBox());
        back.updateModelBound();

        final int nbGrid = 10;
        float step = (float) (env.getSpan(0) / nbGrid);
        FloatBuffer verts = BufferUtils.createVector3Buffer(4*(nbGrid+1));
        for(int i=0;i<=nbGrid;i++){
            verts.put(minx +step*i).put(over).put(miny);
            verts.put(minx +step*i).put(over).put(maxy);
        }
        step = (float) (env.getSpan(1) / nbGrid);
        for(int i=0;i<=nbGrid;i++){
            verts.put(minx).put(over).put(miny +step*i);
            verts.put(maxx).put(over).put(miny +step*i);
        }
        final Line line = new Line("Lines", verts, null, null, null);
        line.getMeshData().setIndexMode(IndexMode.Lines);
        line.setLineWidth(width);
        line.setDefaultColor(ColorRGBA.GRAY);
        line.setModelBound(new BoundingBox());
        line.updateModelBound();

        final CullState cullFrontFace = new CullState();
        cullFrontFace.setEnabled(true);
        cullFrontFace.setCullFace(CullState.Face.Back);
        line.setRenderState(cullFrontFace);

//        final ClipState state = new ClipState();
//        state.setEnabled(false);
//        line.setRenderState(state);


//        plan.attachChild(back);
        plan.attachChild(line);
        return plan;
    }

}

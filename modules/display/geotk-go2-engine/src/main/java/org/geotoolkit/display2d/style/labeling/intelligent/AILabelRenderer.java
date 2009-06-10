/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.style.labeling.intelligent;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.labeling.DefaultLinearLabelCandidateRenderer;
import org.geotoolkit.display2d.style.labeling.DefaultPointLabelCandidateRenderer;
import org.geotoolkit.display2d.style.labeling.LabelDescriptor;
import org.geotoolkit.display2d.style.labeling.LabelLayer;
import org.geotoolkit.display2d.style.labeling.LabelRenderer;
import org.geotoolkit.display2d.style.labeling.PointLabelDescriptor;

/**
 * Default implementation of label renderer.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class AILabelRenderer implements LabelRenderer{

    private final DefaultPointLabelCandidateRenderer POINT_RENDERER;
    private final DefaultLinearLabelCandidateRenderer LINEAR_RENDERER;


    private final RenderingContext2D context;
    private final List<LabelLayer> layers = new ArrayList<LabelLayer>();
    
    public AILabelRenderer(RenderingContext2D context) {
        if(context == null) throw new NullPointerException("Rendering context can not be null");
        this.context = context;

        LINEAR_RENDERER = new DefaultLinearLabelCandidateRenderer(context);
        POINT_RENDERER = new DefaultPointLabelCandidateRenderer(context);
    }
    
    /**
     * {@inheritDoc }
     */ 
    @Override
    public RenderingContext2D getRenderingContext() {
        return context;
    }
    
    /**
     * {@inheritDoc }
     */ 
    @Override
    public void append(final LabelLayer layer) {
        layers.add(layer);
    }
    
    /**
     * {@inheritDoc }
     */ 
    @Override
    public void portrayLabels(){
        final Graphics2D g2 = context.getGraphics();
        //enable antialiasing for labels
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<Candidate> candidates = new ArrayList<Candidate>();

        for(final LabelLayer layer : layers){

            for(LabelDescriptor label : layer.labels()){
                if(label instanceof PointLabelDescriptor){
                    candidates.addAll(POINT_RENDERER.generateCandidats((PointLabelDescriptor) label));

                    Candidate shp = POINT_RENDERER.generateOptimalCandidat((PointLabelDescriptor)label);
                    POINT_RENDERER.render(shp,(PointLabelDescriptor)label);
                }

//                else if(label instanceof LinearLabelDescriptor){
//                    Shape shp = LINEAR_RENDERER.generateOptimalCandidat((LinearLabelDescriptor) label);
//                    LINEAR_RENDERER.render(context, shp,(LinearLabelDescriptor) label);
//                }
            }
        }
        
    }
    
}

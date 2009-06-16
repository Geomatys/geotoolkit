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
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.labeling.LabelDescriptor;
import org.geotoolkit.display2d.style.labeling.LabelLayer;
import org.geotoolkit.display2d.style.labeling.LabelRenderer;
import org.geotoolkit.display2d.style.labeling.LinearLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.PointLabelDescriptor;

/**
 * Default implementation of label renderer.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class AILabelRenderer implements LabelRenderer{

    private final AIPointLabelCandidateRenderer POINT_RENDERER;
    private final AILinearLabelCandidateRenderer LINEAR_RENDERER;


    private final RenderingContext2D context;
    private final List<LabelLayer> layers = new ArrayList<LabelLayer>();
    
    public AILabelRenderer(RenderingContext2D context) {
        if(context == null) throw new NullPointerException("Rendering context can not be null");
        this.context = context;

        LINEAR_RENDERER = new AILinearLabelCandidateRenderer(context);
        POINT_RENDERER = new AIPointLabelCandidateRenderer(context);
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

        //generate all the candidates
        for(final LabelLayer layer : layers){
            for(LabelDescriptor label : layer.labels()){
                if(label instanceof PointLabelDescriptor){
                    candidates.add(POINT_RENDERER.generateCandidat((PointLabelDescriptor) label));
                }else if(label instanceof LinearLabelDescriptor){
                    candidates.add(LINEAR_RENDERER.generateCandidat((LinearLabelDescriptor) label));
                }
            }
        }

        //displace or remove the candidates
        candidates = optimize(candidates);

        //paint the remaining candidates
        for(Candidate candidate : candidates){
            if(candidate instanceof PointCandidate){
                POINT_RENDERER.render(candidate);
            }else if(candidate instanceof LinearCandidate){
                LINEAR_RENDERER.render(candidate);
            }
        }
        
    }

    private static List<Candidate> optimize(List<Candidate> candidates){
        //TODO : make an algorithm to displace the labels in the most efficient way

        final List<Candidate> cleaned = new ArrayList<Candidate>();


        loop:
        for(int i= candidates.size()-1; i>=0; i--){
            Candidate candidate = candidates.get(i);

            if(candidate instanceof PointCandidate){
                //check if the candidate intersect another candidate
                Area shape = ((PointCandidate)candidate).getBounds();

                for(Candidate other : cleaned){
                    if(other == candidate) continue;
                    if(other instanceof LinearCandidate) continue;

                    Area otherShape = ((PointCandidate)other).getBounds();
                    if(otherShape.intersects(shape.getBounds2D())){
                        continue loop;
                    }

                }

                cleaned.add(candidate);
            }else{
                cleaned.add(candidate);
            }

        }

        return cleaned;
    }


}

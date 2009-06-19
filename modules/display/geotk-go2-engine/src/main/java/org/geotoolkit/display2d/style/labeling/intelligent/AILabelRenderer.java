/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private final List<LabelLayer> layers = new ArrayList<LabelLayer>();

    private RenderingContext2D context;
    private AIPointLabelCandidateRenderer pointRenderer;
    private AILinearLabelCandidateRenderer LinearRenderer;
    
    public AILabelRenderer() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setRenderingContext(RenderingContext2D context){
        if(context == null) throw new NullPointerException("Rendering context can not be null");
        this.context = context;
        LinearRenderer = new AILinearLabelCandidateRenderer(context);
        pointRenderer = new AIPointLabelCandidateRenderer(context);
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
        //priority is in the order of the layers provided.
        int priority = layers.size();
        for(final LabelLayer layer : layers){
            for(LabelDescriptor label : layer.labels()){
                Candidate c = null;

                if(label instanceof PointLabelDescriptor){
                    c = pointRenderer.generateCandidat((PointLabelDescriptor) label);
                }else if(label instanceof LinearLabelDescriptor){
                    c = LinearRenderer.generateCandidat((LinearLabelDescriptor) label);
                }else{
                    c = null;
                }

                if(c != null){
                    c.setPriority(priority);
                    candidates.add(c);
                }
            }
            priority--;
        }

        //displace or remove the candidates
        candidates = optimize(candidates);

        //paint the remaining candidates
        for(Candidate candidate : candidates){
            if(candidate instanceof PointCandidate){
                pointRenderer.render(candidate);
            }else if(candidate instanceof LinearCandidate){
                LinearRenderer.render(candidate);
            }
        }
        
    }

    private List<Candidate> clipOutofBounds(List<Candidate> candidates){
        final Rectangle bounds = context.getCanvasDisplayBounds();
        final List<Candidate> correctCandidates = new ArrayList<Candidate>();

        for(Candidate candidate : candidates){
            if(candidate instanceof PointCandidate){
                PointCandidate pc = (PointCandidate) candidate;
                if(bounds.contains(pc.getBounds())){
                    correctCandidates.add(candidate);
                }
            }else{
                correctCandidates.add(candidate);
            }
        }


        return correctCandidates;
    }

    private List<Candidate> sortByXY(List<Candidate> candidates){

        Collections.sort(candidates, new Comparator<Candidate>() {
            @Override
            public int compare(Candidate c1, Candidate c2) {
                int diff = c2.getPriority() - c1.getPriority();

                if(diff == 0){
                    if(c1 instanceof LinearCandidate){
                        return -1;
                    }else if(c2 instanceof LinearCandidate){
                        return 1;
                    }else{
                        PointCandidate pc1 = (PointCandidate) c1;
                        PointCandidate pc2 = (PointCandidate) c2;

                        return (int) ((pc2.x + pc2.y) - (pc1.x + pc1.y) +0.5);
                    }
                }else{
                    return diff;
                }
            }
        });

        return candidates;
    }

    private List<Candidate> optimize(List<Candidate> candidates){
        //TODO : make an algorithm to displace the labels in the most efficient way

        candidates = clipOutofBounds(candidates);
        candidates = sortByXY(candidates);

        final List<Candidate> cleaned = new ArrayList<Candidate>();

        loop:
        for(int i= candidates.size()-1; i>=0; i--){
            Candidate candidate = candidates.get(i);

            for(Candidate other : cleaned){
                if(other == candidate) continue;

                if(AIUtilities.intersects(candidate,other)){
                    continue loop;
                }
            }

            cleaned.add(candidate);
        }

        return cleaned;
    }


}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.display2d.style.labeling.decimate;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.labeling.candidate.Candidate;
import org.geotoolkit.display2d.style.labeling.DefaultLabelRenderer;
import org.geotoolkit.display2d.style.labeling.LabelDescriptor;
import org.geotoolkit.display2d.style.labeling.candidate.LabelingUtilities;
import org.geotoolkit.display2d.style.labeling.LabelLayer;
import org.geotoolkit.display2d.style.labeling.candidate.LinearCandidate;
import org.geotoolkit.display2d.style.labeling.LinearLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.candidate.PointCandidate;
import org.geotoolkit.display2d.style.labeling.PointLabelDescriptor;

/**
 * Label renderer that apply a simple decimation on labels to remove all
 * overlaping labels and labels partly visible on the map edges.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DecimationLabelRenderer extends DefaultLabelRenderer{

    private final List<LabelLayer> layers = new ArrayList<LabelLayer>();

    private PointLabelCandidateRenderer pointRenderer;
    private LinearLabelCandidateRenderer LinearRenderer;
    
    public DecimationLabelRenderer() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setRenderingContext(RenderingContext2D context){
        super.setRenderingContext(context);
        LinearRenderer = new LinearLabelCandidateRenderer(context);
        pointRenderer = new PointLabelCandidateRenderer(context);
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

        layers.clear();
    }

    private List<Candidate> optimize(List<Candidate> candidates){
        candidates = LabelingUtilities.clipOutofBounds(context,candidates);
        candidates = LabelingUtilities.sortByXY(candidates);

        final List<Candidate> cleaned = new ArrayList<Candidate>();

        loop:
        for(int i= candidates.size()-1; i>=0; i--){
            Candidate candidate = candidates.get(i);

            for(Candidate other : cleaned){
                if(other == candidate) continue;

                if(LabelingUtilities.intersects(candidate,other)){
                    continue loop;
                }
            }

            cleaned.add(candidate);
        }

        return cleaned;
    }


}

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
package org.geotoolkit.display2d.ext.labeling;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.labeling.DefaultLabelRenderer;
import org.geotoolkit.display2d.style.labeling.LabelDescriptor;
import org.geotoolkit.display2d.style.labeling.LabelLayer;
import org.geotoolkit.display2d.style.labeling.LinearLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.PointLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.candidate.Candidate;
import org.geotoolkit.display2d.style.labeling.candidate.LabelingUtilities;
import org.geotoolkit.display2d.style.labeling.candidate.LinearCandidate;
import org.geotoolkit.display2d.style.labeling.candidate.PointCandidate;
import org.geotoolkit.display2d.style.labeling.decimate.LinearLabelCandidateRenderer;
import org.geotoolkit.display2d.style.labeling.decimate.PointLabelCandidateRenderer;

/**
 * Label renderer that apply a simple decimation on labels to remove all
 * overlaping labels and try do displace them to achieve good results.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DisplacementLabelRenderer extends DefaultLabelRenderer{

    private final List<LabelLayer> layers = new ArrayList<LabelLayer>();

    private RenderingContext2D context;
    private PointLabelCandidateRenderer pointRenderer;
    private LinearLabelCandidateRenderer LinearRenderer;

    public DisplacementLabelRenderer() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setRenderingContext(final RenderingContext2D context){
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
    public boolean portrayLabels(){
        final Graphics2D g2 = context.getGraphics();
        //enable antialiasing for labels
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<Candidate> candidates = new ArrayList<Candidate>();

        //generate all the candidates
        //priority is in the order of the layers provided.
        int priority = layers.size();
        for(final LabelLayer layer : layers){
            for(LabelDescriptor label : layer.labels()){
                Candidate[] cs = null;

                if(label instanceof PointLabelDescriptor){
                    cs = pointRenderer.generateCandidat((PointLabelDescriptor) label);
                }else if(label instanceof LinearLabelDescriptor){
                    cs = LinearRenderer.generateCandidat((LinearLabelDescriptor) label);
                }else{
                    cs = null;
                }

                if(cs != null){
                    for(Candidate c : cs){
                        c.setPriority(priority);
                        candidates.add(c);
                    }
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
        return !candidates.isEmpty();
    }

    private List<Candidate> optimize(List<Candidate> candidates){

        candidates = new ArrayList<Candidate>(SimulatedAnnealing.simulate(candidates, 40, 0.99));

        candidates = LabelingUtilities.clipOutofBounds(context,candidates);
//        candidates = LabelingUtilities.sortByXY(candidates);
        candidates = LabelingUtilities.sortByCost(candidates);

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

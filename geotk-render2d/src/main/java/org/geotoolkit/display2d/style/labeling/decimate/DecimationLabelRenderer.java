/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2015, Geomatys
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
import java.util.SortedSet;
import java.util.TreeSet;
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

/**
 * Label renderer that apply a simple decimation on labels to remove all
 * overlaping labels and labels partly visible on the map edges.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DecimationLabelRenderer extends DefaultLabelRenderer{

    private final SortedSet<Candidate> candidates = new TreeSet<Candidate>(LabelingUtilities.XY_COMPARATOR);

    private final List<LabelLayer> layers = new ArrayList<LabelLayer>();

    private PointLabelCandidateRenderer pointRenderer;
    private LinearLabelCandidateRenderer LinearRenderer;

    public DecimationLabelRenderer() {
    }

    @Override
    public LabelLayer createLabelLayer() {
        return new DecimateLabelLayer(false, true);
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
        candidates = LabelingUtilities.clipOutofBounds(context,candidates);
        candidates = LabelingUtilities.sortByXY(candidates);
        return candidates;
    }

    private class DecimateLabelLayer implements LabelLayer{

        private final List<LabelDescriptor> labels = new ArrayList<LabelDescriptor>(){

            @Override
            public boolean add(LabelDescriptor label) {
                if(label instanceof PointLabelDescriptor){
                    final Candidate[] pcs = pointRenderer.generateCandidat((PointLabelDescriptor) label);
                    if(pcs == null) return true;
                    for(Candidate c : pcs){
                        final PointCandidate pc = (PointCandidate) c;
                        pc.setPriority(1);
                        synchronized(candidates){
                            if(!LabelingUtilities.intersects(pc,candidates)){
                                candidates.add(pc);
                            }else{
                            }
                        }
                    }
                }else if(label instanceof LinearLabelDescriptor){
                    final Candidate[] lcs = LinearRenderer.generateCandidat((LinearLabelDescriptor) label);
                    for(Candidate c : lcs){
                        final LinearCandidate lc = (LinearCandidate) c;
                        lc.setPriority(1);
                        synchronized(candidates){
                            candidates.add(lc);
                        }
                    }
                }
                return true;
            }

        };

        private final boolean obstacle;
        private final boolean labelled;

        public DecimateLabelLayer(final boolean isObstacle, final boolean isLabelled) {
            this.labelled = isLabelled;
            this.obstacle = isObstacle;
        }

        @Override
        public boolean isObstacle() {
            return obstacle;
        }

        @Override
        public boolean isLabelled() {
            return labelled;
        }

        @Override
        public List<LabelDescriptor> labels() {
            return labels;
        }

    }

}

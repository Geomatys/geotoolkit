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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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
    
    private final SortedSet<Candidate> candidates = Collections.synchronizedSortedSet(new TreeSet<Candidate>(LabelingUtilities.XY_COMPARATOR){
        
        public boolean add(final LabelDescriptor label) {

            if(label instanceof PointLabelDescriptor){
                final PointCandidate pc = (PointCandidate)pointRenderer.generateCandidat((PointLabelDescriptor) label);
                if(pc == null) return true;
                pc.setPriority(1);
                if(!LabelingUtilities.intersects(pc,candidates)){
                    candidates.add(pc);
                }                  
            }else if(label instanceof LinearLabelDescriptor){
                final LinearCandidate lc = (LinearCandidate)LinearRenderer.generateCandidat((LinearLabelDescriptor) label);
                lc.setPriority(1);
                candidates.add(lc);
            }
            return true;
        }
        
    });

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
    public void portrayLabels(){
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
                    final PointCandidate pc = (PointCandidate)pointRenderer.generateCandidat((PointLabelDescriptor) label);
                    if(pc == null) return true;
                    pc.setPriority(1);
                    synchronized(candidates){
                        if(!LabelingUtilities.intersects(pc,candidates)){
                            candidates.add(pc);
                        }                  
                    }
                }else if(label instanceof LinearLabelDescriptor){
                    final LinearCandidate lc = (LinearCandidate)LinearRenderer.generateCandidat((LinearLabelDescriptor) label);
                    lc.setPriority(1);
                    synchronized(candidates){
                        candidates.add(lc);
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

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.process.coverage.isoline2;

import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.memory.MemoryCoverageStore;
import org.geotoolkit.data.FeatureCollection;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.apache.sis.referencing.CommonCRS;
import org.junit.Test;
import static org.junit.Assert.*;
import org.geotoolkit.feature.Feature;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class IsolineTest {
    
    @Test
    public void test() throws Exception{
        
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 3);
        env.setRange(1, 0, 3);
        
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setEnvelope(env);
        gcb.setRenderedImage(new float[][]{
            {100,100,100},
            {100,200,100},
            {100,100,100}
        });
        final GridCoverage2D coverage = gcb.getGridCoverage2D();
        final MemoryCoverageStore store = new MemoryCoverageStore(coverage);
        final CoverageReference ref = store.getCoverageReference(store.getNames().iterator().next());
        
        
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("coverage", "isoline2");
        final ParameterValueGroup procparams = desc.getInputDescriptor().createValue();
        procparams.parameter("inCoverageRef").setValue(ref);
        procparams.parameter("inIntervals").setValue(new double[]{150});
        final org.geotoolkit.process.Process process = desc.createProcess(procparams);
        final ParameterValueGroup result = process.call();
        FeatureCollection<Feature> col = (FeatureCollection) result.parameter("outFeatureCollection").getValue();
        assertEquals(1, col.size());
        for(Feature f : col){
            System.out.println(f);
        }
        
        
    }
    
    @Test
    public void test2() throws Exception{
        
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 3);
        env.setRange(1, 0, 3);
        
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setEnvelope(env);
        gcb.setRenderedImage(new float[][]{
            {10,10,20},
            {10,10,20},
            {10,15,10},
        });
        final GridCoverage2D coverage = gcb.getGridCoverage2D();
        final MemoryCoverageStore store = new MemoryCoverageStore(coverage);
        final CoverageReference ref = store.getCoverageReference(store.getNames().iterator().next());
        
        
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("coverage", "isoline2");
        final ParameterValueGroup procparams = desc.getInputDescriptor().createValue();
        procparams.parameter("inCoverageRef").setValue(ref);
        procparams.parameter("inIntervals").setValue(new double[]{15});
        final org.geotoolkit.process.Process process = desc.createProcess(procparams);
        final ParameterValueGroup result = process.call();
        FeatureCollection<Feature> col = (FeatureCollection) result.parameter("outFeatureCollection").getValue();
        assertEquals(2, col.size());
        for(Feature f : col){
            System.out.println(f);
        }
        
    }
    
    @Test
    public void test3() throws Exception{
        
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 3);
        env.setRange(1, 0, 3);
        
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setEnvelope(env);
        gcb.setRenderedImage(new float[][]{
            {10,10,20},
            {10,10,20},
            {10,15,10},
            {10,15,10},
        });
        final GridCoverage2D coverage = gcb.getGridCoverage2D();
        final MemoryCoverageStore store = new MemoryCoverageStore(coverage);
        final CoverageReference ref = store.getCoverageReference(store.getNames().iterator().next());
        
        
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("coverage", "isoline2");
        final ParameterValueGroup procparams = desc.getInputDescriptor().createValue();
        procparams.parameter("inCoverageRef").setValue(ref);
        procparams.parameter("inIntervals").setValue(new double[]{15});
        final org.geotoolkit.process.Process process = desc.createProcess(procparams);
        final ParameterValueGroup result = process.call();
        FeatureCollection<Feature> col = (FeatureCollection) result.parameter("outFeatureCollection").getValue();
        assertEquals(4, col.size());
        for(Feature f : col){
            System.out.println(f);
        }
                
    }
    
    
}

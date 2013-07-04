/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.process.coverage.isoline2;

import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.memory.MemoryCoverageStore;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

/**
 *
 * @author jsorel
 */
public class IsolineTest {
    
    @Test
    public void test() throws Exception{
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
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
        
        for(Feature f : col){
            System.out.println(f);
        }
        
        
    }
    
    @Test
    public void test2() throws Exception{
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
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
        
        for(Feature f : col){
            System.out.println(f);
        }
        
    }
    
    @Test
    public void test3() throws Exception{
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
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
        
        for(Feature f : col){
            System.out.println(f);
        }
                
    }
    
    
}

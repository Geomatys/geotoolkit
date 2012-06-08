/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.process.coverage.bandcombiner;

import java.util.List;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.coverage.Coverage;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class CombinerDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "Raster band combiner";
    
    /**
     * Mandatory - Bands to merge.
     */
    public static final ParameterDescriptor<Coverage> IN_RED =
            new DefaultParameterDescriptor<Coverage>("source","Raster bands to ombine.", Coverage.class,null,true);
    
    public static final ParameterDescriptor<Coverage> IN_GREEN =
            new DefaultParameterDescriptor<Coverage>("source","Raster bands to ombine.", Coverage.class,null,true);
    
    public static final ParameterDescriptor<Coverage> IN_BLUE =
            new DefaultParameterDescriptor<Coverage>("source","Raster bands to ombine.", Coverage.class,null,true);
    
    public static final ParameterDescriptorGroup INPUT_DESC = new DefaultParameterDescriptorGroup(NAME + "InputParameters", IN_RED, IN_GREEN, IN_BLUE);
    
    /**
     * Mandatory - Resulting image.
     */
    public static final ParameterDescriptor<Coverage> OUT_BAND =
            new DefaultParameterDescriptor<Coverage>("result","Coverage created", Coverage.class,null,true);
    
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME + "OutputParameters", OUT_BAND);
    
    public static final ProcessDescriptor INSTANCE = new CombinerDescriptor();
    
    private CombinerDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION, new SimpleInternationalString("Get multiple raster bands to merge them into one entity"), INPUT_DESC, OUTPUT_DESC);
    }
    
    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new CombinerProcess(input);
    }
    
}

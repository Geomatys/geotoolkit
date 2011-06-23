
package org.geotoolkit.pending.demo.processing.factory;

import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

public class AddDescriptor extends AbstractProcessDescriptor{
        
    /**Process name : addition */
    public static final String NAME = "add";
    
    /**
     * Input parameters
     */
    public static final ParameterDescriptor<Double> FIRST_NUMBER =
            new DefaultParameterDescriptor("first", "first number", Double.class, null, true);
    public static final ParameterDescriptor<Double> SECOND_NUMBER =
            new DefaultParameterDescriptor("second", "second number", Double.class, null, true);
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{FIRST_NUMBER,SECOND_NUMBER});
    
    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<Double> RESULT_NUMBER =
            new DefaultParameterDescriptor("result", "addition result", Double.class, null, true);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{RESULT_NUMBER});

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new AddDescriptor();

    private AddDescriptor() {
        super(NAME, MathProcessFactory.IDENTIFICATION,
                new SimpleInternationalString("Adds two double."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess() {
        return new AddProcess();
    }
    
}

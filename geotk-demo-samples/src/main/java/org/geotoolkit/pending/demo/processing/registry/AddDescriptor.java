
package org.geotoolkit.pending.demo.processing.registry;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.apache.sis.util.ResourceInternationalString;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;


public class AddDescriptor extends AbstractProcessDescriptor{

    /**Process name : addition */
    public static final String NAME = "addition";
    public static final ResourceInternationalString ABSRACT = new Description("addProcess");

    /**
     * Input parameters with translation bundle.
     */
    public static final ParameterDescriptor<Double> FIRST_NUMBER = new ParameterBuilder()
                .addName("first")
                .setRemarks(new Description("addFirst"))
                .setRequired(true)
                .create(Double.class, null);

    public static final ParameterDescriptor<Double> SECOND_NUMBER = new ParameterBuilder()
                .addName("second")
                .setRemarks(new Description("addSecond"))
                .setRequired(true)
                .create(Double.class, null);

    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FIRST_NUMBER,SECOND_NUMBER);

    /**
     * OutputParameters with translation bundle.
     */
    public static final ParameterDescriptor<Double> RESULT_NUMBER  = new ParameterBuilder()
                .addName("result")
                .setRemarks(new Description("addResult"))
                .setRequired(true)
                .create(Double.class, null);

    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT_NUMBER);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new AddDescriptor();

    private AddDescriptor() {
        super(NAME, DemoProcessRegistry.IDENTIFICATION,
                ABSRACT,
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new AddProcess(this, input);
    }

}

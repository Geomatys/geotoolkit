
package org.geotoolkit.pending.demo.processing.registry;

import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.util.ResourceInternationalString;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;

public class AddDescriptor extends AbstractProcessDescriptor{

    //bundle path
    private static final String BUNDLE = "org/geotoolkit/pending/demo/processing/bundle";

    //keys
    private static final String ADD_PROCESS = "addProcess";
    private static final String ADD_FIRST = "addFirst";
    private static final String ADD_SECOND = "addSecond";
    private static final String ADD_RESULT = "addResult";

    /**Process name : addition */
    public static final String NAME = "addition";
    public static final ResourceInternationalString ABSRACT = new ResourceInternationalString(BUNDLE, ADD_PROCESS);

    /**
     * Input parameters with translation bundle.
     */
    public static final ParameterDescriptor<Double> FIRST_NUMBER;
    static {
        final Map<String, Object> propertiesFirst = new HashMap<String, Object>();
        propertiesFirst.put(IdentifiedObject.NAME_KEY,        "first");
        propertiesFirst.put(IdentifiedObject.REMARKS_KEY,     new ResourceInternationalString(BUNDLE, ADD_FIRST));
        FIRST_NUMBER = new DefaultParameterDescriptor<Double>(propertiesFirst, Double.class, null, null, null, null, null, true);
    }

    public static final ParameterDescriptor<Double> SECOND_NUMBER;
    static {
        final Map<String, Object> propertiesSecond = new HashMap<String, Object>();
        propertiesSecond.put(IdentifiedObject.NAME_KEY,        "second");
        propertiesSecond.put(IdentifiedObject.REMARKS_KEY,     new ResourceInternationalString(BUNDLE, ADD_SECOND));
        SECOND_NUMBER = new DefaultParameterDescriptor<Double>(propertiesSecond, Double.class, null, null, null, null, null, true);
    }

    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{FIRST_NUMBER,SECOND_NUMBER});

    /**
     * OutputParameters with translation bundle.
     */
    public static final ParameterDescriptor<Double> RESULT_NUMBER;
    static {
        final Map<String, Object> propertiesResult = new HashMap<String, Object>();
        propertiesResult.put(IdentifiedObject.NAME_KEY,        "result");
        propertiesResult.put(IdentifiedObject.REMARKS_KEY,     new ResourceInternationalString(BUNDLE, ADD_RESULT));
        RESULT_NUMBER = new DefaultParameterDescriptor<Double>(propertiesResult, Double.class, null, null, null, null, null, true);
    }

    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{RESULT_NUMBER});

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
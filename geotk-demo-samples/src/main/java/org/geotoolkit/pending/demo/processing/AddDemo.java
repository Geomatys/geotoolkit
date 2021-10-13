package org.geotoolkit.pending.demo.processing;

import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

public class AddDemo {

    public static void main(String[] args) throws ProcessException, NoSuchIdentifierException {
        Demos.init();
        System.out.println("---------------------------------------- Process Addition demo");


        double number1 = 10.0;
        double number2 = 15.3;

        //Find the addition process
        ProcessDescriptor descriptor = ProcessFinder.getProcessDescriptor("geotoolkit", "math:add");

        //fill process input from process descriptor
        ParameterValueGroup in = descriptor.getInputDescriptor().createValue();
        in.parameter("first").setValue(number1);
        in.parameter("second").setValue(number2);

        System.out.println("Addition of " + number1 + "+" + number2);

        //process creation with inputs
        org.geotoolkit.process.Process process = descriptor.createProcess(in);

        //Execute the process and get output results
        ParameterValueGroup output = process.call();

        double result = (Double) output.parameter("result").getValue();
        System.out.println("Result : "+result);
    }
}

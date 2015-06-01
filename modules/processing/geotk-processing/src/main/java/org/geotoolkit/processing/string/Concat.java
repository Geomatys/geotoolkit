/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.processing.string;

import static org.geotoolkit.parameter.Parameters.getOrCreate;
import org.geotoolkit.parameter.ParametersExt;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import static org.geotoolkit.processing.string.ConcatDescriptor.INSTANCE;
import static org.geotoolkit.processing.string.ConcatDescriptor.PREFIX;
import static org.geotoolkit.processing.string.ConcatDescriptor.RESULT_OUT;
import static org.geotoolkit.processing.string.ConcatDescriptor.SUFFIX;
import static org.geotoolkit.processing.string.ConcatDescriptor.VALUE;

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class Concat extends AbstractProcess {

    public Concat(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }
    
    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {
        fireProcessStarted("Start concat");
        final ParameterValue prefixVal = ParametersExt.getValue(inputParameters, PREFIX.getName().getCode());
        String prefix = null;
        if (prefixVal != null) {
            prefix = prefixVal.stringValue();
        }
        final ParameterValue suffixVal = ParametersExt.getValue(inputParameters, SUFFIX.getName().getCode());
        String suffix = null;
        if (suffixVal != null) {
            suffix = suffixVal.stringValue();
        }
        final String value  = getOrCreate(VALUE, inputParameters).stringValue();
        
        String result = value;
        if (prefix != null) {
            result = prefix + value;
        }
        
        if (suffix != null) {
            result = result + suffix;
        }
        
        getOrCreate(RESULT_OUT, outputParameters).setValue(result);
        
        fireProcessCompleted("Concat done.");
    }
    
}

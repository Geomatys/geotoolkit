/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.processing.math.add;

import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Addition process between two numbers.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class AddProcess extends AbstractProcess {

    public AddProcess(final ParameterValueGroup input) {
        super(AddDescriptor.INSTANCE,input);
    }

    @Override
    protected void execute() {
        final double first = inputParameters.getValue(AddDescriptor.FIRST_NUMBER);
        final double second = inputParameters.getValue(AddDescriptor.SECOND_NUMBER);
        final double result = first + second;
        outputParameters.getOrCreate(AddDescriptor.RESULT_NUMBER).setValue(result);
    }

}

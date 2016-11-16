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
package org.geotoolkit.processing.math.sum;

import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.parameter.Parameters.*;
/**
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class SumProcess extends AbstractProcess {

    public SumProcess(final ParameterValueGroup input) {
        super(SumDescriptor.INSTANCE,input);
    }

    @Override
    protected void execute() {

        final Double[] set = value(SumDescriptor.SET, inputParameters);

        double sum = 0.0;
        for(int i=0; i<set.length; i++) {
            sum += set[i].doubleValue();
        }

        getOrCreate(SumDescriptor.RESULT_NUMBER, outputParameters).setValue(sum);
    }

}

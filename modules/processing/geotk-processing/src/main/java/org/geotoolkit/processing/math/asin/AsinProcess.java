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
package org.geotoolkit.processing.math.asin;

import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class AsinProcess extends AbstractProcess {

    public AsinProcess(final ParameterValueGroup input){
        super(AsinDescriptor.INSTANCE,input);
    }

    @Override
    protected void execute() {
        final double first = inputParameters.getValue(AsinDescriptor.FIRST_NUMBER);
        final double result = Math.asin(first);
        outputParameters.getOrCreate(AsinDescriptor.RESULT_NUMBER).setValue(result);
    }

}

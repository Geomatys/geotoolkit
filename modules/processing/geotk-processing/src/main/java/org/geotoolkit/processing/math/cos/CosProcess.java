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
package org.geotoolkit.processing.math.cos;

import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class CosProcess extends AbstractProcess {

    public CosProcess(final ParameterValueGroup input){
        super(CosDescriptor.INSTANCE,input);
    }

    @Override
    protected void execute() {
        final double first = inputParameters.getValue(CosDescriptor.FIRST_NUMBER);
        final double result = Math.cos(first);
        outputParameters.getOrCreate(CosDescriptor.RESULT_NUMBER).setValue(result);
    }

}

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
package org.geotoolkit.processing.math.min;

import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.processing.math.min.MinDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class MinProcess extends AbstractProcess {

    public MinProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    @Override
    protected void execute() {

        final Double[] set = value(SET, inputParameters);

        double min = Math.min(set[0].doubleValue(), set[1].doubleValue());
        for (int i=1; i<set.length; i++) {
            min = Math.min(min, set[i].doubleValue());
        }

        getOrCreate(RESULT_NUMBER, outputParameters).setValue(min);
    }

}

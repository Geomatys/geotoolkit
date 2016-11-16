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
package org.geotoolkit.processing.jts.lenght;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.processing.AbstractProcess;

import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.parameter.Parameters.*;

/**
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class LenghtProcess extends AbstractProcess {

    public LenghtProcess(final ParameterValueGroup input) {
        super(LenghtDescriptor.INSTANCE,input);
    }

    @Override
    protected void execute() {

        final Geometry geom1 = value(LenghtDescriptor.GEOM, inputParameters);

        final double result = geom1.getLength();

        getOrCreate(LenghtDescriptor.RESULT, outputParameters).setValue(result);
    }

}

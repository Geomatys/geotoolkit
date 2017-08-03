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
package org.geotoolkit.processing.jts.isempty;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.processing.AbstractProcess;

import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.processing.jts.isempty.IsEmptyDescriptor.*;

/**
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class IsEmptyProcess extends AbstractProcess {

    public IsEmptyProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    @Override
    protected void execute() {
        final Geometry geom1 = inputParameters.getValue(GEOM);
        final boolean result = geom1.isEmpty();
        outputParameters.getOrCreate(RESULT).setValue(result);
    }

}

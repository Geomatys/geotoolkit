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

import org.locationtech.jts.geom.Geometry;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.processing.GeotkProcessingRegistry;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class LenghtDescriptor extends AbstractProcessDescriptor{

    /**
     * Process name : lenght
     *
     * @todo Wrong name. Should be spelled length
     */
    public static final String NAME = "jts:lenght";

    /**
     * Input parameters
     */
    public static final ParameterDescriptor<Geometry> GEOM = new ParameterBuilder()
            .addName("geom")
            .setRemarks("Geometry JTS source")
            .setRequired(true)
            .create(Geometry.class, null);


    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(GEOM);

    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<Double> RESULT = new ParameterBuilder()
            .addName("result")
            .setRemarks("Length result")
            .setRequired(true)
            .create(Double.class, null);

    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new LenghtDescriptor();

    private LenghtDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Returns the length of this Geometry."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new LenghtProcess(input);
    }

}

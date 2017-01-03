/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.processing.coverage.reducetodomain;

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.opengis.coverage.Coverage;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ReduceToDomainDescriptor extends AbstractProcessDescriptor {

    /**Process name : reduceToDomain */
    public static final String NAME = "coverage:reduceToDomain";

    /**
     * Mandatory - Coverage
     */
    public static final ParameterDescriptor<Coverage> COVERAGE_IN = new ParameterBuilder()
            .addName("coverage_in")
            .setRemarks("Input coverage ")
            .setRequired(true)
            .create(Coverage.class, null);

    /**
     * Mandatory - Coverage
     */
    public static final ParameterDescriptor<Coverage> COVERAGE_OUT = new ParameterBuilder()
            .addName("coverage_out")
            .setRemarks("Output coverage ")
            .setRequired(true)
            .create(Coverage.class, null);

    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(COVERAGE_IN);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(COVERAGE_OUT);

    /**Instance */
    public static final ProcessDescriptor INSTANCE = new ReduceToDomainDescriptor();

    /**
     * Default constructor
     */
    private ReduceToDomainDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Reduce to CRS domain a coverage, correctly transpose values which cross the anti-meridan."),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new ReduceToDomainProcess(input);
    }

}

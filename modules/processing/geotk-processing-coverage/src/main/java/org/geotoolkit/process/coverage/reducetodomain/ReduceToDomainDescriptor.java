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
package org.geotoolkit.process.coverage.reducetodomain;

import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.opengis.coverage.Coverage;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ReduceToDomainDescriptor extends AbstractProcessDescriptor {

    /**Process name : reduceToDomain */
    public static final String NAME = "reduceToDomain";

    /**
     * Mandatory - Coverage
     */
    public static final ParameterDescriptor<Coverage> COVERAGE_IN =
            new DefaultParameterDescriptor("coverage_in", "Input coverage ", Coverage.class, null, true);

    /**
     * Mandatory - Coverage
     */
    public static final ParameterDescriptor<Coverage> COVERAGE_OUT =
            new DefaultParameterDescriptor("coverage_out", "Output coverage ", Coverage.class, null, true);

    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{COVERAGE_IN});

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{COVERAGE_OUT});

    /**Instance */
    public static final ProcessDescriptor INSTANCE = new ReduceToDomainDescriptor();

    /**
     * Default constructor
     */
    private ReduceToDomainDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION,
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

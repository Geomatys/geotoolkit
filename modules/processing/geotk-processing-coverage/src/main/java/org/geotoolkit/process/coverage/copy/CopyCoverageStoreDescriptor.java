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
package org.geotoolkit.process.coverage.copy;

import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class CopyCoverageStoreDescriptor extends AbstractProcessDescriptor {

    /**Process name : copycoveragestore */
    public static final String NAME = "copycoveragestore";

    /**
     * Mandatory - CoverageStore
     */
    public static final ParameterDescriptor<CoverageStore> STORE_IN =
            new DefaultParameterDescriptor("store_in", "Input coverage store", CoverageStore.class, null, true);

    /**
     * Mandatory - CoverageStore
     */
    public static final ParameterDescriptor<CoverageStore> STORE_OUT =
            new DefaultParameterDescriptor("store_out", "Output coverage store", CoverageStore.class, null, true);

    /**
     * Mandatory - drop before insertion or not.
     */
    public static final ParameterDescriptor<Boolean> ERASE =
            new DefaultParameterDescriptor("erase", "Erase type if already presents.",
            Boolean.class, false, true);
    
    /**
     * Optional - reduce to domain, true by default.
     */
    public static final ParameterDescriptor<Boolean> REDUCE_TO_DOMAIN =
            new DefaultParameterDescriptor("reduceToDomain", "Reduce to domain.",
            Boolean.class, true, false);

    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{STORE_IN, STORE_OUT, ERASE, REDUCE_TO_DOMAIN});

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{});

    /**Instance */
    public static final ProcessDescriptor INSTANCE = new CopyCoverageStoreDescriptor();

    /**
     * Default constructor
     */
    private CopyCoverageStoreDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Parameter description of Coverage to Feature process."),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new CopyCoverageStoreProcess(input);
    }

}

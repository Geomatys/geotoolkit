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
package org.geotoolkit.processing.coverage.copy;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class CopyCoverageStoreDescriptor extends AbstractProcessDescriptor {

    /**Process name : copycoveragestore */
    public static final String NAME = "coverage:copycoveragestore";

    /**
     * Mandatory - CoverageStore
     */
    public static final ParameterDescriptor<CoverageStore> STORE_IN = new ParameterBuilder()
            .addName("store_in")
            .setRemarks("Input coverage store")
            .setRequired(true)
            .create(CoverageStore.class, null);

    /**
     * Mandatory - CoverageStore
     */
    public static final ParameterDescriptor<CoverageStore> STORE_OUT = new ParameterBuilder()
            .addName("store_out")
            .setRemarks("Output coverage store")
            .setRequired(true)
            .create(CoverageStore.class, null);

    /**
     * Mandatory - drop before insertion or not.
     */
    public static final ParameterDescriptor<Boolean> ERASE = new ParameterBuilder()
            .addName("erase")
            .setRemarks("Erase type if already presents.")
            .setRequired(true)
            .create(Boolean.class, false);

    /**
     * Optional - reduce to domain, true by default.
     */
    public static final ParameterDescriptor<Boolean> REDUCE_TO_DOMAIN = new ParameterBuilder()
            .addName("reduceToDomain")
            .setRemarks("Reduce to domain.")
            .setRequired(false)
            .create(Boolean.class, true);

    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(STORE_IN, STORE_OUT, ERASE, REDUCE_TO_DOMAIN);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup();

    /**Instance */
    public static final ProcessDescriptor INSTANCE = new CopyCoverageStoreDescriptor();

    /**
     * Default constructor
     */
    private CopyCoverageStoreDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
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

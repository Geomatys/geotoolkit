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
package org.geotoolkit.processing.metadata.merge;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameters description of Merge process.
 * name of the process : "merge"
 * inputs :
 * <ul>
 *     <li>FIRST_IN "first" first metadata</li>
 *     <li>SECOND_IN "second" second metadata</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>RESULT_OUT "result" merged metadata</li>
 * </ul>
 * 
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class MergeDescriptor extends AbstractProcessDescriptor {

    /**
     * Process name : merge
     */
    public static final String NAME = "metadata:merge";

    /**
     * Mandatory - first metadata
     */
    public static final String FIRST_IN_NAME = "first";
    public static final ParameterDescriptor<Metadata> FIRST_IN = new ParameterBuilder()
            .addName(FIRST_IN_NAME)
            .setRemarks("First metadata file")
            .setRequired(true)
            .create(Metadata.class, null);

    /**
     * Mandatory - second metadata
     */
    public static final String SECOND_IN_NAME = "second";
    public static final ParameterDescriptor<Metadata> SECOND_IN = new ParameterBuilder()
            .addName(SECOND_IN_NAME)
            .setRemarks("Second metadata file")
            .setRequired(true)
            .create(Metadata.class, null);

    /**
     * Input Parameters
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FIRST_IN, SECOND_IN);

    /**
     * Mandatory - merged metadata
     */
    public static final String RESULT_OUT_NAME = "result";
    public static final ParameterDescriptor<Metadata> RESULT_OUT = new ParameterBuilder()
            .addName(RESULT_OUT_NAME)
            .setRemarks("Merged metadata")
            .setRequired(true)
            .create(Metadata.class, null);
    
    /** 
     * Output Parameters 
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT_OUT);
    
    public static final ProcessDescriptor INSTANCE = new MergeDescriptor();

    private MergeDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Merge two metadata objects."),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new Merge(input);
    }
    
}

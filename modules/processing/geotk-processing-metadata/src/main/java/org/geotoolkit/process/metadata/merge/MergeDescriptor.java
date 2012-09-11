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
package org.geotoolkit.process.metadata.merge;

import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.metadata.MetadataProcessingRegistry;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.GeneralParameterDescriptor;
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
 * @module pending
 */
public final class MergeDescriptor extends AbstractProcessDescriptor {

    /**
     * Process name : merge
     */
    public static final String NAME = "merge";

    /**
     * Mandatory - first metadata
     */
    public static final ParameterDescriptor<Metadata> FIRST_IN =
            new DefaultParameterDescriptor("first", "First metadata file", 
            Metadata.class, null, true);
    
    /**
     * Mandatory - second metadata
     */
    public static final ParameterDescriptor<Metadata> SECOND_IN =
            new DefaultParameterDescriptor("second", "Second metadata file", 
            Metadata.class, null, true);
            
    /** 
     * Input Parameters 
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{FIRST_IN, SECOND_IN});

    /**
     * Mandatory - merged metadata
     */
    public static final ParameterDescriptor<Metadata> RESULT_OUT =
            new DefaultParameterDescriptor("result", "Merged metadata", 
            Metadata.class, null, true);
    
    /** 
     * Output Parameters 
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",RESULT_OUT);
    
    public static final ProcessDescriptor INSTANCE = new MergeDescriptor();

    private MergeDescriptor() {
        super(NAME, MetadataProcessingRegistry.IDENTIFICATION,
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

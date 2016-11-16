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
package org.geotoolkit.processing.vector.filter;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.filter.Filter;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameter description of Filter process.
 * name of the process : "filter"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection </li>
 *     <li>FILTER_IN "filter_in" FeatureExtend</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection</li>
 * </ul>
 * @author Quentin Boileau
 * @module
 */
public final class FilterDescriptor extends VectorDescriptor {

    /**Process name : filter */
    public static final String NAME = "filter";

    /**
     * Mandatory - Filter
     */
    public static final ParameterDescriptor<Filter> FILTER_IN = new ParameterBuilder()
            .addName("filter_in")
            .setRemarks("Filter")
            .setRequired(true)
            .create(org.opengis.filter.Filter.class, null);

    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_IN, FILTER_IN);

    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);
    
    /** Instance */
    public static final ProcessDescriptor INSTANCE = new FilterDescriptor();

    /**
     * Default constructor
     */
    private FilterDescriptor() {
        super(NAME, "Apply a filter to a FeatureCollection", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new FilterProcess(input);
    }
}

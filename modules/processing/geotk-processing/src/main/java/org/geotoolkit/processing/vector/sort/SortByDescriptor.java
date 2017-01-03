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
package org.geotoolkit.processing.vector.sort;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorDescriptor;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameter description of SortBy process.
 * name of the process : "sortby"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection </li>
 *     <li>SORTER_IN "sorter_in" Array of SortBy used give the order</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection</li>
 * </ul>
 * @author Quentin Boileau
 * @module
 */
public final class SortByDescriptor extends VectorDescriptor {

    /**Process name : sortby */
    public static final String NAME = "vector:sortby";
    /**
     * Mandatory - Array of SortBy used give the order
     */
    public static final ParameterDescriptor<org.opengis.filter.sort.SortBy[]> SORTER_IN = new ParameterBuilder()
            .addName("sorter_in")
            .setRemarks("Array of SortBy used give the order")
            .setRequired(true)
            .create(org.opengis.filter.sort.SortBy[].class, null);

    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_IN, SORTER_IN);

    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);
    
    /** Instance */
    public static final ProcessDescriptor INSTANCE = new SortByDescriptor();

    /**
     * Default constructor
     */
    private SortByDescriptor() {
        super(NAME, "Sort a FeatureCollection", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new SortByProcess(input);
    }
}

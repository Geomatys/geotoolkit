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
package org.geotoolkit.processing.coverage.coveragetofeatures;

import java.util.Collection;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.coverage.CoverageProcessingRegistry;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.opengis.feature.Feature;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameter description of Coverage to Feature process.
 * Name of the process : "coveragetofeatures"
 * inputs :
 * <ul>
 *     <li>READER_IN "reader_in" the coverage reader input</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection simplified</li>
 * </ul>
 * @author Quentin Boileau
 * @module
 */
public final class CoverageToFeaturesDescriptor extends AbstractProcessDescriptor{

    /**
     * Mandatory - CoverageReader
     */
    public static final ParameterDescriptor<GridCoverageReader> READER_IN = new ParameterBuilder()
            .addName("reader_in")
            .setRemarks("Inpute GridCoverageReader")
            .setRequired(true)
            .create(GridCoverageReader.class, null);
    /**
     * Mandatory - Resulting Feature Collection
     */
    public static final ParameterDescriptor<Collection<Feature>> FEATURE_OUT = (ParameterDescriptor)new ParameterBuilder()
            .addName("feature_out")
            .setRemarks("Outpute Feature")
            .setRequired(true)
            .create(Collection.class, null);

    /**Process name : coveragetofeatures */
    public static final String NAME = "coveragetofeatures";

    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(READER_IN);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);

    /**Instance */
    public static final ProcessDescriptor INSTANCE = new CoverageToFeaturesDescriptor();

    /**
     * Default constructor
     */
    private CoverageToFeaturesDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Parameter description of Coverage to Feature process."),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new CoverageToFeaturesProcess(input);
    }
}

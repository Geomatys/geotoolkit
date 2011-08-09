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
package org.geotoolkit.process.coverage.coveragetofeatures;

import java.util.Collection;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.feature.Feature;

import org.opengis.parameter.GeneralParameterDescriptor;
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
 * @module pending
 */
public final class CoverageToFeaturesDescriptor extends AbstractProcessDescriptor{

    /**
     * Mandatory - CoverageReader
     */
    public static final ParameterDescriptor<GridCoverageReader> READER_IN =
            new DefaultParameterDescriptor("reader_in", "Inpute GridCoverageReader", GridCoverageReader.class, null, true);
    /**
     * Mandatory - Resulting Feature Collection
     */
    public static final ParameterDescriptor<Collection<Feature>> FEATURE_OUT =
            new DefaultParameterDescriptor("feature_out", "Outpute Feature", Collection.class, null, true);
    
    /**Process name : coveragetofeatures */
    public static final String NAME = "coveragetofeatures";

    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{READER_IN});

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{FEATURE_OUT});
    
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
        return new CoverageToFeatures(input);
    }
}

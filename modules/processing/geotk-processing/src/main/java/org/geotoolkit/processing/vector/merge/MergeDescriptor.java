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
package org.geotoolkit.processing.vector.merge;

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorProcessingRegistry;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameters description for Merge process.
 * name of the process : "merge"
 * inputs :
 * <ul>
 *     <li>FEATURES_IN "features_in"   Array of FeatureCollection</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection intersected</li>
 * </ul>
 * @author Quentin Boileau
 * @module pending
 */
public final class MergeDescriptor extends AbstractProcessDescriptor {

   /**Process name : merge */
    public static final String NAME = "merge";
    /**
     * Mandatory - Array of FeatureCollection
     */
    public static final ParameterDescriptor<FeatureCollection[]> FEATURES_IN = new ParameterBuilder()
            .addName("features_in")
            .setRemarks("Inpute array of FeatureCollection")
            .setRequired(true)
            .create(FeatureCollection[].class, null);

    public static final ParameterDescriptor<FeatureCollection> FEATURE_OUT = new ParameterBuilder()
            .addName("feature_out")
            .setRemarks("The merged FeatureCollection")
            .setRequired(false)
            .create(FeatureCollection.class, null);
    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURES_IN);
    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);
    /** Instance */
    public static final ProcessDescriptor INSTANCE = new MergeDescriptor();

    /**
     * Default constructor
     */
    protected MergeDescriptor() {

        super(NAME, VectorProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Merge many FeatureCollection in one. The fist FeatureCollection found in "
                + "the input Collection have his FeatureType preserved. The others will be adapted to this one."),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new MergeProcess(input);
    }
}

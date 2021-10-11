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
package org.geotoolkit.processing.vector.clip;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorDescriptor;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Description of clip process.
 * name of the process : "clip"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection to clip</li>
 *     <li>FEATURE_CLIP "feature_clip" FeatureCollection for clip</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection clipped</li>
 * </ul>
 * @author Quentin Boileau
 * @module
 */
public final class ClipDescriptor extends VectorDescriptor {

    /**Process name : clip */
    public static final String NAME = "vector:clip";

    /**
     * Mandatory - Feature Collection for clipping
     */
    public static final ParameterDescriptor<FeatureCollection> FEATURE_CLIP = new ParameterBuilder()
            .addName("feature_clip")
            .setRemarks("Inpute FeatureCollection for clipping")
            .setRequired(true)
            .create(FeatureCollection.class, null);

    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_IN, FEATURE_CLIP);

    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new ClipDescriptor();

    /**
     * Default constructor
     */
    private ClipDescriptor() {
        super(NAME, "Return the result FeatureCollection of clipping", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new ClipProcess(input);
    }
}

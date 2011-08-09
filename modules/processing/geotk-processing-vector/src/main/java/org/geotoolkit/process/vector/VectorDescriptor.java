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
package org.geotoolkit.process.vector;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.util.SimpleInternationalString;

import org.opengis.feature.Feature;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * Input and output descriptor for vector process.
 * Inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection to clip</li>
 * </ul>
 * Outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection clipped</li>
 * </ul>
 * @author Quentin Boileau
 * @module pending
 */
public abstract class VectorDescriptor extends AbstractProcessDescriptor {

    /**
     * Mandatory - Feature Collection
     */
    public static final ParameterDescriptor<FeatureCollection<Feature>> FEATURE_IN =
            new DefaultParameterDescriptor("feature_in", "Input FeatureCollection", FeatureCollection.class, null, true);
    /**
     * Mandatory - Resulting Feature Collection
     */
    public static final ParameterDescriptor<FeatureCollection<Feature>> FEATURE_OUT =
            new DefaultParameterDescriptor("feature_out", "Output FeatureFeatureCollection", FeatureCollection.class, null, true);

    /**
     * Default constructor
     * @param name : process descriptor name
     * @param msg  : process descriptor message
     * @param input : process input
     * @param output : process output
     */
    protected VectorDescriptor(final String name, final String msg,
            final ParameterDescriptorGroup input, final ParameterDescriptorGroup output) {

        super(name, VectorProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString(msg),
                input, output);
    }
}

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
package org.geotoolkit.processing.vector.clipgeometry;

import org.locationtech.jts.geom.Geometry;
import org.apache.sis.parameter.ParameterBuilder;

import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorDescriptor;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Description of clip by geometry process.
 * Clip a FeatureCollection with a geometry and return a resulting FeatureCollection.
 * name of the process : "clipGeometry"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection to clip</li>
 *     <li>CLIP_GEOMETRY_IN "clip_geometry_in" Geometry used to clip</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection clipped</li>
 * </ul>
 * @author Quentin Boileau
 * @module
 */
public final class ClipGeometryDescriptor extends VectorDescriptor {

    /**Process name : clipGeometry */
    public static final String NAME = "vector:clipGeometry";
    /**
     * Mandatory - Clipping Geometry
     */
    public static final ParameterDescriptor<Geometry> CLIP_GEOMETRY_IN = new ParameterBuilder()
            .addName("clip_geometry_in")
            .setRemarks("Input clip geometry")
            .setRequired(true)
            .create(Geometry.class, null);

    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_IN, CLIP_GEOMETRY_IN);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);

    /**Instance */
    public static final ProcessDescriptor INSTANCE = new ClipGeometryDescriptor();

    /**
     * Default constructor
     */
    private ClipGeometryDescriptor() {
        super(NAME, "Clip a FeatureCollection with a geometry and return a resulting FeatureCollection", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new ClipGeometryProcess(input);
    }
}

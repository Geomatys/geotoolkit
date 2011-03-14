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
package org.geotoolkit.process.vector.clipGeometry;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.vector.VectorDescriptor;

import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * Description of centroid process.
 * @author Quentin Boleau
 * @module pending
 */
public class ClipGeometryDescriptor extends VectorDescriptor {

    public static final String NAME = "clipGeometry";
    /**
     * Mandatory - Feature Collection
     */
    public static final ParameterDescriptor<Geometry> CLIP_GEOMETRY_DESC =
            new DefaultParameterDescriptor("clip_geometry_in", "Inpute clip geometry", Geometry.class, null, true);
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{FEATURE_IN, CLIP_GEOMETRY_DESC});
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{FEATURE_OUT});
    public static final ProcessDescriptor INSTANCE = new ClipGeometryDescriptor();

    private ClipGeometryDescriptor() {
        super(NAME, "Return the centroid of a feature", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess() {
        return new ClipGeometry();
    }
}

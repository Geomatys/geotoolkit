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
package org.geotoolkit.process.vector.clipgeometry;

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
 * @module pending
 */
public final class ClipGeometryDescriptor extends VectorDescriptor {

    /**Process name : clipGeometry */
    public static final String NAME = "clipGeometry";
    /**
     * Mandatory - Clipping Geometry
     */
    public static final ParameterDescriptor<Geometry> CLIP_GEOMETRY_IN=
            new DefaultParameterDescriptor("clip_geometry_in", "Input clip geometry", Geometry.class, null, true);

    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{FEATURE_IN, CLIP_GEOMETRY_IN});

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{FEATURE_OUT});

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
    public Process createProcess() {
        return new ClipGeometry();
    }
}

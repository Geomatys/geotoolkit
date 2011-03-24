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
package org.geotoolkit.process.vector.nearest;

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
 * Parameters description for Nearest process.
 * name of the process : "nearest"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection</li>
 *     <li>GEOMETRY_IN "geometry_in" a geometry</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection intersected</li>
 * </ul>
 * @author Quentin Boileau
 * @module pending
 */
public final class NearestDescriptor extends VectorDescriptor {

    /**Process name : nearest */
    public static final String NAME = "nearest";
    /**
     * Mandatory - Intersection geometry
     */
    public static final ParameterDescriptor<Geometry> GEOMETRY_IN =
            new DefaultParameterDescriptor("geometry_in", "Input geometry", Geometry.class, null, true);
    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{FEATURE_IN, GEOMETRY_IN});
    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{FEATURE_OUT});
    /** Instance */
    public static final ProcessDescriptor INSTANCE = new NearestDescriptor();

    /**
     * Default constructor
     */
    private NearestDescriptor() {
        super(NAME, "Return the nearest Feature(s) in a FeatureCollection with the inputGeometry", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess() {
        return new Nearest();
    }
}

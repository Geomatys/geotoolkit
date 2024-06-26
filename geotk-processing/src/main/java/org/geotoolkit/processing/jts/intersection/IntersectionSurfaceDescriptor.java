/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.processing.jts.intersection;

import org.locationtech.jts.geom.Geometry;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;


/**
 * Description of the intersection surface process.
 *
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class IntersectionSurfaceDescriptor extends AbstractProcessDescriptor {
    /**Process name : intersectionSurface */
    public static final String NAME = "jts:intersectionSurface";

    /**
     * Input parameters
     */
    public static final ParameterDescriptor<Geometry> GEOM1 = new ParameterBuilder()
            .addName("geom1")
            .setRemarks("Geometry JTS source")
            .setRequired(true)
            .create(Geometry.class, null);
    public static final ParameterDescriptor<Geometry> GEOM2 = new ParameterBuilder()
            .addName("geom2")
            .setRemarks("Geometry JTS")
            .setRequired(true)
            .create(Geometry.class, null);

    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(GEOM1,GEOM2);

    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<Double> RESULT_SURFACE = new ParameterBuilder()
            .addName("result_surface")
            .setRemarks("The intersection surface result")
            .setRequired(true)
            .create(Double.class, null);

    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT_SURFACE);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new IntersectionSurfaceDescriptor();

    private IntersectionSurfaceDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Computes an intersection Geometry between the source geometry "
                + "(geom1) and the other (geom2), and calculates the intersection surface."),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new IntersectionSurfaceProcess(input);
    }

}

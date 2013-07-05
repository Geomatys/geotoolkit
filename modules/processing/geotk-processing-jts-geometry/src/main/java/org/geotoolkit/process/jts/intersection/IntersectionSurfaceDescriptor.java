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
package org.geotoolkit.process.jts.intersection;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.jts.JTSProcessingRegistry;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;


/**
 * Description of the intersection surface process.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class IntersectionSurfaceDescriptor extends AbstractProcessDescriptor {
    /**Process name : intersectionSurface */
    public static final String NAME = "intersectionSurface";

    /**
     * Input parameters
     */
    public static final ParameterDescriptor<Geometry> GEOM1 =
            new DefaultParameterDescriptor("geom1", "Geometry JTS source", Geometry.class, null, true);
    public static final ParameterDescriptor<Geometry> GEOM2 =
            new DefaultParameterDescriptor("geom2", "Geometry JTS", Geometry.class, null, true);

    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{GEOM1,GEOM2});

    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<Geometry> RESULT_SURFACE =
            new DefaultParameterDescriptor("result_surface", "The intersection surface result", Double.class, null, true);

    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{RESULT_SURFACE});

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new IntersectionSurfaceDescriptor();

    private IntersectionSurfaceDescriptor() {
        super(NAME, JTSProcessingRegistry.IDENTIFICATION,
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

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.processing.coverage.compose;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Process which merge several coverages clipping each one with provided geometries.
 *
 * @author Jean-Loup Amiot (Geomatys)
 */
public class ComposeDescriptor extends AbstractProcessDescriptor {

    public static final String ID = "coverage:compose";

    public static final ParameterDescriptor<GridGeometry2D> GRID_PARAM = new ParameterBuilder()
            .addName("gridGeometry")
            .setRequired(false)
            .create(GridGeometry2D.class, null);

    public static final ParameterDescriptor<GridCoverage2D> COVERAGE_PARAM = new ParameterBuilder()
            .addName("coverage")
            .setRequired(true)
            .create(GridCoverage2D.class, null);

    public static final ParameterDescriptor<Geometry> INCLUDE_PARAM = new ParameterBuilder()
            .addName("include")
            .setRequired(false)
            .create(Geometry.class, null);
    public static final ParameterDescriptor<Geometry> EXCLUDE_PARAM = new ParameterBuilder()
            .addName("exclude")
            .setRequired(false)
            .create(Geometry.class, null);

    public static final ParameterDescriptorGroup LAYER_PARAM = new ParameterBuilder()
            .addName("layer")
            .setRequired(true)
            .createGroup(1, Integer.MAX_VALUE, COVERAGE_PARAM, INCLUDE_PARAM, EXCLUDE_PARAM);

    public static final ParameterDescriptorGroup INPUT = new ParameterBuilder()
            .addName("input")
            .setRequired(true)
            .createGroup(1, 1, LAYER_PARAM, GRID_PARAM);

    public static final ParameterDescriptorGroup OUTPUT = new ParameterBuilder()
            .addName("output")
            .setRequired(true)
            .createGroup(1, 1, COVERAGE_PARAM);

    /**
     * Unique instance of this descriptor.
     */
    public static final ComposeDescriptor INSTANCE = new ComposeDescriptor();

    public ComposeDescriptor() {
        super(ID, GeotkProcessingRegistry.IDENTIFICATION, new SimpleInternationalString("Combine coverage"), INPUT, OUTPUT);
    }

    @Override
    public Process createProcess(ParameterValueGroup pvg) {
        return new Compose(pvg);
    }

}

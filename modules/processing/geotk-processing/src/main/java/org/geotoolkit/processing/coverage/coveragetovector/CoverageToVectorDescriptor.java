/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.processing.coverage.coveragetovector;

import org.apache.sis.coverage.grid.GridCoverage;
import org.locationtech.jts.geom.Geometry;

import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Description of a coverage to polygon process.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class CoverageToVectorDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "coverage:CoverageToVector";

    /**
     * Mandatory - Coverage to process
     */
    public static final ParameterDescriptor<GridCoverage> COVERAGE = new ParameterBuilder()
            .addName("coverage")
            .setRemarks("Coverage to process.")
            .setRequired(true)
            .create(GridCoverage.class,null);

    /**
     * Optional - Ranges to regroup
     */
    public static final ParameterDescriptor<NumberRange[]> RANGES = new ParameterBuilder()
            .addName("ranges")
            .setRemarks("Ranges to regroup.")
            .setRequired(false)
            .create(NumberRange[].class,null);

    /**
     * Optional - selected band, default 0
     */
    public static final ParameterDescriptor<Integer> BAND = new ParameterBuilder()
            .addName("band")
            .setRemarks("Band to transform")
            .setRequired(false)
            .create(Integer.class,0);

    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName(NAME+"InputParameters").createGroup(COVERAGE,RANGES,BAND);

    /**
     * Mandatory - Result of vectorisation
     */
    public static final ParameterDescriptor<Geometry[]> GEOMETRIES = new ParameterBuilder()
            .addName("geometries")
            .setRemarks("Result of vectorisation.")
            .setRequired(true)
            .create(Geometry[].class,null);


    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName(NAME+"OutputParameters").createGroup(GEOMETRIES);

    public static final ProcessDescriptor INSTANCE = new CoverageToVectorDescriptor();


    private CoverageToVectorDescriptor(){
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Transform a coverage in features "
                + "by agregating pixels as geometries when they are in the same range."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new CoverageToVectorProcess(input);
    }

}

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
package org.geotoolkit.process.coverage;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.SimpleInternationalString;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * Description of a coverage to polygon process.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class CoverageToVectorDescriptor extends AbstractProcessDescriptor{

    public static final String NAME = "CoverageToVector";

    /**
     * Mandatory - Coverage to process
     */
    public static final ParameterDescriptor<GridCoverage2D> COVERAGE =
            new DefaultParameterDescriptor<GridCoverage2D>("coverage","Coverage to process.",GridCoverage2D.class,null,true);

    /**
     * Optional - Ranges to regroup
     */
    public static final ParameterDescriptor<NumberRange[]> RANGES =
            new DefaultParameterDescriptor<NumberRange[]>("ranges","Ranges to regroup.",NumberRange[].class,null,false);

    /**
     * Optional - selected band, default 0
     */
    public static final ParameterDescriptor<Integer> BAND =
            new DefaultParameterDescriptor<Integer>("band","Band to transform",Integer.class,0,false);

    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"InputParameters",
                COVERAGE,RANGES,BAND);

    /**
     * Mandatory - Result of vectorisation
     */
    public static final ParameterDescriptor<Geometry[]> GEOMETRIES =
            new DefaultParameterDescriptor<Geometry[]>("geometries","Result of vectorisation.",Geometry[].class,null,true);


    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"OutputParameters",
                GEOMETRIES);
    
    public static final ProcessDescriptor INSTANCE = new CoverageToVectorDescriptor();


    private CoverageToVectorDescriptor(){
        super(NAME, CoverageProcessFactory.IDENTIFICATION,
                new SimpleInternationalString("Transform a coverage in features "
                + "by agregating pixels as geometries when they are in the same range."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess() {
        return new CoverageToVectorProcess();
    }

}

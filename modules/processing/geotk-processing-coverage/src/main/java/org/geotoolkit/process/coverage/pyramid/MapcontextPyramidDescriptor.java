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
package org.geotoolkit.process.coverage.pyramid;

import java.awt.Dimension;
import org.geotoolkit.coverage.PyramidalModel;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Description of a mapcontext pyramid process.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class MapcontextPyramidDescriptor extends AbstractProcessDescriptor{

    public static final String NAME = "mapcontextpyramid";

    /**
     * Mandatory - Mapcontext to process.
     */
    public static final ParameterDescriptor<MapContext> IN_MAPCONTEXT =
            new DefaultParameterDescriptor<MapContext>("context",
            "Mapcontext to tyle.",MapContext.class,null,true);

    /**
     * Mandatory - Envelope on which to generate tiles.
     */
    public static final ParameterDescriptor<Envelope> IN_EXTENT =
            new DefaultParameterDescriptor<Envelope>("extent",
            "Area on which to create the tiles",Envelope.class,null,true);
    
    /**
     * Mandatory - Size of the tiles.
     */
    public static final ParameterDescriptor<Dimension> IN_TILE_SIZE =
            new DefaultParameterDescriptor<Dimension>("tilesize",
            "Tile size.",Dimension.class,new Dimension(256,256),true);
    
    /**
     * Mandatory - Scales to create.
     * Expressed in CRS unit by pixel.
     */
    public static final ParameterDescriptor<double[]> IN_SCALES =
            new DefaultParameterDescriptor<double[]>("scales",
            "Different scales to generate. (in crs unit by pixel)",double[].class,null,true);
    
    /**
     * Mandatory - Container which will receive the tiles.
     */
    public static final ParameterDescriptor<PyramidalModel> IN_CONTAINER =
            new DefaultParameterDescriptor<PyramidalModel>("container",
            "The container which will receive the tiles.",PyramidalModel.class,null,true);
    

    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"InputParameters",
                IN_MAPCONTEXT,IN_EXTENT,IN_TILE_SIZE,IN_SCALES,IN_CONTAINER);


    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"OutputParameters");
    
    public static final ProcessDescriptor INSTANCE = new MapcontextPyramidDescriptor();


    private MapcontextPyramidDescriptor(){
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Create a pyramid/mosaic from the given"
                + "mapcontext. Created tiles are stored in the given container."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new MapcontextPyramidProcess(input);
    }

}

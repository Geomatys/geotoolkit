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
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.coverage.PyramidalCoverageReference;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
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
     * Optional - Number of painters.
     * Increasing the number of painting thread may improve performances.
     * But also uses more memory. Default will be set to number of computer cpu cores.
     */
    public static final ParameterDescriptor<Integer> IN_NBPAINTER =
            new DefaultParameterDescriptor<Integer>("painters",
            "Number of threads painting images.",Integer.class,null,false);
    
    /**
     * Mandatory - Container which will receive the tiles.
     */
    public static final ParameterDescriptor<PyramidalCoverageReference> IN_CONTAINER =
            new DefaultParameterDescriptor<PyramidalCoverageReference>("container",
            "The container which will receive the tiles.",PyramidalCoverageReference.class,null,true);
    
    /**
     * Optional - Rendering hints.
     * Hints for the rendering engine.
     */
    public static final ParameterDescriptor<Hints> IN_HINTS =
            new DefaultParameterDescriptor<Hints>("hints","Rendering hints",Hints.class,null,false);

    /**
     * Optional - Update mode.
     * Add new data to old Pyramid without erase.
     */
    public static final ParameterDescriptor<Boolean> IN_UPDATE =
            new DefaultParameterDescriptor<Boolean>("update", "Update mode.", Boolean.class, false, false);
    
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"InputParameters",
                IN_MAPCONTEXT,IN_EXTENT,IN_TILE_SIZE,IN_SCALES,IN_NBPAINTER,IN_CONTAINER,IN_HINTS, IN_UPDATE);

    public static final ParameterDescriptor<PyramidalCoverageReference> OUT_CONTAINER =
            new DefaultParameterDescriptor<PyramidalCoverageReference>("outContainer",
            "The container which will receive the tiles.",PyramidalCoverageReference.class,null,true);
    
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"OutputParameters", OUT_CONTAINER);
    
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

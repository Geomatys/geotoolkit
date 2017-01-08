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
package org.geotoolkit.display2d.process.pyramid;

import java.awt.Dimension;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.display2d.process.GO2ProcessingRegistry;
import org.geotoolkit.storage.coverage.PyramidalCoverageReference;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Description of a mapcontext pyramid process.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class MapcontextPyramidDescriptor extends AbstractProcessDescriptor{

    public static final String NAME = "mapcontextpyramid";

    /**
     * Mandatory - Mapcontext to process.
     */
    public static final ParameterDescriptor<MapContext> IN_MAPCONTEXT = new ParameterBuilder()
            .addName("context")
            .setRemarks("Mapcontext to tyle.")
            .setRequired(true)
            .create(MapContext.class,null);

    /**
     * Mandatory - Envelope on which to generate tiles.
     */
    public static final ParameterDescriptor<Envelope> IN_EXTENT = new ParameterBuilder()
            .addName("extent")
            .setRemarks("Area on which to create the tiles")
            .setRequired(true)
            .create(Envelope.class,null);
    
    /**
     * Mandatory - Size of the tiles.
     */
    public static final ParameterDescriptor<Dimension> IN_TILE_SIZE = new ParameterBuilder()
            .addName("tilesize")
            .setRemarks("Tile size.")
            .setRequired(true)
            .create(Dimension.class,new Dimension(256,256));
    
    /**
     * Mandatory - Scales to create.
     * Expressed in CRS unit by pixel.
     */
    public static final ParameterDescriptor<double[]> IN_SCALES = new ParameterBuilder()
            .addName("scales")
            .setRemarks("Different scales to generate. (in crs unit by pixel)")
            .setRequired(true)
            .create(double[].class,null);
    
    /**
     * Optional - Number of painters.
     * Increasing the number of painting thread may improve performances.
     * But also uses more memory. Default will be set to number of computer cpu cores.
     */
    public static final ParameterDescriptor<Integer> IN_NBPAINTER = new ParameterBuilder()
            .addName("painters")
            .setRemarks("Number of threads painting images.")
            .setRequired(false)
            .create(Integer.class,null);
    
    /**
     * Mandatory - Container which will receive the tiles.
     */
    public static final ParameterDescriptor<PyramidalCoverageReference> IN_CONTAINER = new ParameterBuilder()
            .addName("container")
            .setRemarks("The container which will receive the tiles.")
            .setRequired(true)
            .create(PyramidalCoverageReference.class,null);
    
    /**
     * Optional - Rendering hints.
     * Hints for the rendering engine.
     */
    public static final ParameterDescriptor<Hints> IN_HINTS = new ParameterBuilder()
            .addName("hints")
            .setRemarks("Rendering hints")
            .setRequired(false)
            .create(Hints.class,null);

    /**
     * Optional - Update mode.
     * Add new data to old Pyramid without erase.
     */
    public static final ParameterDescriptor<Boolean> IN_UPDATE = new ParameterBuilder()
            .addName("update")
            .setRemarks("Update mode.")
            .setRequired(false)
            .create(Boolean.class,Boolean.FALSE);
    
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName(NAME+"InputParameters").createGroup(
                IN_MAPCONTEXT,IN_EXTENT,IN_TILE_SIZE,IN_SCALES,IN_NBPAINTER,IN_CONTAINER,IN_HINTS, IN_UPDATE);

    public static final ParameterDescriptor<PyramidalCoverageReference> OUT_CONTAINER = new ParameterBuilder()
            .addName("outContainer")
            .setRemarks("The container which will receive the tiles.")
            .setRequired(true)
            .create(PyramidalCoverageReference.class,null);
    
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName(NAME+"OutputParameters").createGroup(OUT_CONTAINER);
    
    public static final ProcessDescriptor INSTANCE = new MapcontextPyramidDescriptor();


    private MapcontextPyramidDescriptor(){
        super(NAME, GO2ProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Create a pyramid/mosaic from the given"
                + "mapcontext. Created tiles are stored in the given container."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new MapcontextPyramidProcess(input);
    }

}

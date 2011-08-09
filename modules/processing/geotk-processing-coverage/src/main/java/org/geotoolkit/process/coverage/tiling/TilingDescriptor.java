/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.process.coverage.tiling;

import java.awt.geom.AffineTransform;
import java.io.File;

import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.geotoolkit.util.SimpleInternationalString;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Description of a coverage to polygon process.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class TilingDescriptor extends AbstractProcessDescriptor{

    public static final String NAME = "tiling";

    /**
     * Mandatory - Coverage to process
     */
    public static final ParameterDescriptor<File> IN_SOURCE_FILE =
            new DefaultParameterDescriptor<File>("source","Coverage to tyle.",File.class,null,true);

    /**
     * Mandatory - Output folder
     */
    public static final ParameterDescriptor<File> IN_TILES_FOLDER =
            new DefaultParameterDescriptor<File>("target","Folder where tiles will be stored.",File.class,null,true);

    /**
     * Optional - Grid to CRS
     */
    public static final ParameterDescriptor<AffineTransform> IN_GRID_TO_CRS =
            new DefaultParameterDescriptor<AffineTransform>("gridToCRS","MathTransform from grid to crs.",AffineTransform.class,null,false);


    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"InputParameters",
                IN_SOURCE_FILE,IN_TILES_FOLDER,IN_GRID_TO_CRS);

    /**
     * Mandatory - Resulting tile manager
     */
    public static final ParameterDescriptor<TileManager> OUT_TILE_MANAGER =
            new DefaultParameterDescriptor<TileManager>("manager","Tile manager.",TileManager.class,null,true);

    /**
     * Optional - Coordinate Reference system of the tyle manager.
     */
    public static final ParameterDescriptor<CoordinateReferenceSystem> OUT_CRS =
            new DefaultParameterDescriptor<CoordinateReferenceSystem>("crs","Tile manager's coordinate reference system.",CoordinateReferenceSystem.class,null,false);


    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"OutputParameters",
                OUT_TILE_MANAGER,OUT_CRS);
    
    public static final ProcessDescriptor INSTANCE = new TilingDescriptor();


    private TilingDescriptor(){
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Create a pyramid/mosaic from the given"
                + "source. Created tiles are stored in the given folder."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new TilingProcess(input);
    }

}

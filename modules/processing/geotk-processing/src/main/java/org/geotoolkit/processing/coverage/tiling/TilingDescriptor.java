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
package org.geotoolkit.processing.coverage.tiling;

import java.awt.geom.AffineTransform;
import java.io.File;
import javax.imageio.ImageReader;
import org.apache.sis.parameter.ParameterBuilder;

import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.coverage.CoverageProcessingRegistry;
import org.apache.sis.util.iso.SimpleInternationalString;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Description of a coverage to polygon process.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class TilingDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "tiling";

    /**
     * Mandatory - Coverage to process
     */
    public static final ParameterDescriptor<File> IN_SOURCE_FILE = new ParameterBuilder()
            .addName("source")
            .setRemarks("Coverage to tile.")
            .setRequired(true)
            .create(File.class,null);
    
    public static final ParameterDescriptor<ImageReader> IN_SOURCE_READER = new ParameterBuilder()
            .addName("sourceReader")
            .setRemarks("An image reader for the input")
            .setRequired(false)
            .create(ImageReader.class,null);

    /**
     * Mandatory - Output folder
     */
    public static final ParameterDescriptor<File> IN_TILES_FOLDER = new ParameterBuilder()
            .addName("target")
            .setRemarks("Folder where tiles will be stored.")
            .setRequired(true)
            .create(File.class,null);

    /**
     * Optional - Grid to CRS
     */
    public static final ParameterDescriptor<AffineTransform> IN_GRID_TO_CRS = new ParameterBuilder()
            .addName("gridToCRS")
            .setRemarks("MathTransform from grid to crs.")
            .setRequired(false)
            .create(AffineTransform.class,null);


    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName(NAME+"InputParameters").createGroup(
                IN_SOURCE_FILE,IN_SOURCE_READER,IN_TILES_FOLDER,IN_GRID_TO_CRS);

    /**
     * Mandatory - Resulting tile manager
     */
    public static final ParameterDescriptor<TileManager> OUT_TILE_MANAGER = new ParameterBuilder()
            .addName("manager")
            .setRemarks("Tile manager.")
            .setRequired(true)
            .create(TileManager.class,null);

    /**
     * Optional - Coordinate Reference system of the tile manager.
     */
    public static final ParameterDescriptor<CoordinateReferenceSystem> OUT_CRS = new ParameterBuilder()
            .addName("crs")
            .setRemarks("Tile manager's coordinate reference system.")
            .setRequired(false)
            .create(CoordinateReferenceSystem.class,null);


    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName(NAME+"OutputParameters").createGroup(
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

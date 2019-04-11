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

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.AbstractMap;
import java.util.Map.Entry;
import javax.imageio.ImageReader;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.image.io.mosaic.MosaicImageWriteParam;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.TileWritingPolicy;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import static org.geotoolkit.processing.coverage.tiling.TilingDescriptor.*;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class TilingProcess extends AbstractProcess {

    TilingProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *
     * @param imgReader input image reader
     * @param inFile input image file
     * @param output output folder where tiles will be stored
     * @param gridtoCRS grid to crs transform
     */
    public TilingProcess(ImageReader imgReader, File inFile, File output, AffineTransform gridtoCRS){
        super(INSTANCE, asParameters(imgReader,inFile,output,gridtoCRS));
    }

    private static ParameterValueGroup asParameters(ImageReader imgReader, File file, File output, AffineTransform gridtoCRS){
        final Parameters params = Parameters.castOrWrap(INPUT_DESC.createValue());
        if(imgReader!=null) params.getOrCreate(IN_SOURCE_READER).setValue(imgReader);
        if(file!=null) params.getOrCreate(IN_SOURCE_FILE).setValue(file);
        params.getOrCreate(IN_TILES_FOLDER).setValue(output);
        params.getOrCreate(IN_GRID_TO_CRS).setValue(gridtoCRS);
        return params;
    }

    /**
     * Execute process now.
     *
     * @return TileManager and CoordinateReferenceSystem
     * @throws ProcessException
     */
    public Entry<TileManager,CoordinateReferenceSystem> executeNow() throws ProcessException {
        execute();
        final CoordinateReferenceSystem crs = (CoordinateReferenceSystem) outputParameters.parameter(TilingDescriptor.OUT_CRS.getName().getCode()).getValue();
        final TileManager tilemanager = (TileManager) outputParameters.parameter(TilingDescriptor.OUT_TILE_MANAGER.getName().getCode()).getValue();
        return new AbstractMap.SimpleEntry<>(tilemanager,crs);
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameters", inputParameters);

        final Object input;
        final ImageReader imgReader = inputParameters.getValue(IN_SOURCE_READER);
        if (imgReader != null) {
            input = imgReader;
        } else {
            input = inputParameters.getValue(IN_SOURCE_FILE);
        }
        final File output = inputParameters.getValue(IN_TILES_FOLDER);
        AffineTransform gridtoCRS = inputParameters.getValue(IN_GRID_TO_CRS);

        if (!output.exists()) {
            output.mkdirs();
        }


        final ImageCoverageReader reader = new ImageCoverageReader();
        try {
            reader.setInput(input);
            final SpatialMetadata coverageMetadata = reader.getCoverageMetadata();
            CoordinateReferenceSystem crs = coverageMetadata.getInstanceForType(CoordinateReferenceSystem.class);
            if(gridtoCRS == null) {
                final RectifiedGrid grid = coverageMetadata.getInstanceForType(RectifiedGrid.class);
                try {
                    gridtoCRS = MetadataHelper.INSTANCE.getAffineTransform(grid, null);
                } catch(Exception ex) {
                    /*silent exit, not a georeferenced coverage*/
                }
            }

            final MosaicBuilder builder = new MosaicBuilder();
            builder.setTileSize(new Dimension(512, 512));
            builder.setGridToCRS(gridtoCRS);
            //let the builder build the best pyramid resolutions
            //builder.setSubsamplings(new int[]{1,2,4,6,8,12,16,20,30});
            builder.setTileDirectory(output);

            final MosaicImageWriteParam params = new MosaicImageWriteParam();
            params.setTileWritingPolicy(TileWritingPolicy.WRITE_NEWS_ONLY);
            final TileManager tileManager = builder.writeFromInput(input, params);

            outputParameters.getOrCreate(OUT_TILE_MANAGER).setValue(tileManager);
            outputParameters.getOrCreate(OUT_CRS).setValue(crs);
        } catch (Exception ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

}

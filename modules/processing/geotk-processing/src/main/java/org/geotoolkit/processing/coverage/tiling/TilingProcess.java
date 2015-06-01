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
import javax.imageio.ImageReader;

import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.image.io.mosaic.MosaicImageWriteParam;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.TileWritingPolicy;
import org.geotoolkit.processing.AbstractProcess;

import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


import static org.geotoolkit.parameter.Parameters.*;
import org.geotoolkit.process.ProcessException;
import static org.geotoolkit.processing.coverage.tiling.TilingDescriptor.*;
import org.apache.sis.util.ArgumentChecks;
/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class TilingProcess extends AbstractProcess {

    TilingProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameters", inputParameters);
        
        final Object input;
        final ImageReader imgReader = value(IN_SOURCE_READER, inputParameters);
        if (imgReader != null) {
            input = imgReader;
        } else {
            input = value(IN_SOURCE_FILE, inputParameters);
        }
        final File output = value(IN_TILES_FOLDER, inputParameters);
        AffineTransform gridtoCRS = value(IN_GRID_TO_CRS, inputParameters);

        if (!output.exists()) {
            output.mkdirs();
        }


        final ImageCoverageReader reader = new ImageCoverageReader();
        try {
            reader.setInput(input);
            final SpatialMetadata coverageMetadata = reader.getCoverageMetadata(0);
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

            getOrCreate(OUT_TILE_MANAGER, outputParameters).setValue(tileManager);
            getOrCreate(OUT_CRS, outputParameters).setValue(crs);
        } catch (Exception ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

}

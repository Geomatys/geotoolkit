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

package org.geotoolkit.process.coverage;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.io.File;

import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.image.io.mosaic.MosaicImageWriteParam;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.TileWritingPolicy;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.util.SimpleInternationalString;

import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class TilingProcess extends AbstractProcess{

    TilingProcess(){
        super(TilingDescriptor.INSTANCE);
    }

    @Override
    public void run() {
        if (inputParameters == null) {
            getMonitor().failed(new ProcessEvent(this, -1,
                    new SimpleInternationalString("Input parameters not set."),
                    new NullPointerException("Input parameters not set.")));
        }

        final File input = (File) inputParameters.parameter(TilingDescriptor.IN_SOURCE_FILE.getName().getCode()).getValue();
        final File output = (File) inputParameters.parameter(TilingDescriptor.IN_TILES_FOLDER.getName().getCode()).getValue();
        AffineTransform gridtoCRS = (AffineTransform)
                inputParameters.parameter(TilingDescriptor.IN_GRID_TO_CRS.getName().getCode()).getValue();

        if(!output.exists()){
            output.mkdirs();
        }


        final ImageCoverageReader reader = new ImageCoverageReader();
        try {
            reader.setInput(input);
            final SpatialMetadata coverageMetadata = reader.getCoverageMetadata(0);
            CoordinateReferenceSystem crs = coverageMetadata.getInstanceForType(CoordinateReferenceSystem.class);
            if(gridtoCRS == null){
                final RectifiedGrid grid = coverageMetadata.getInstanceForType(RectifiedGrid.class);
                try{
                    gridtoCRS = MetadataHelper.INSTANCE.getAffineTransform(grid, null);
                }catch(Exception ex){ 
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
            
            final ParameterValueGroup result = super.getOutput();
            result.parameter(TilingDescriptor.OUT_TILE_MANAGER.getName().getCode()).setValue(tileManager);
            result.parameter(TilingDescriptor.OUT_CRS.getName().getCode()).setValue(crs);
            getMonitor().ended(new ProcessEvent(this));
        } catch (Exception ex) {
            getMonitor().failed(new ProcessEvent(this, 0, new SimpleInternationalString(ex.getLocalizedMessage()), ex));
            return;
        }

    }

}

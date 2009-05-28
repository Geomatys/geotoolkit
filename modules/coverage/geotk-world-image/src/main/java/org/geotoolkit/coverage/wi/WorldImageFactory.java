/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.coverage.wi;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReader;

import org.geotoolkit.coverage.PrjFileReader;
import org.geotoolkit.coverage.WorldFileReader;
import org.geotools.coverage.io.CoverageReader;
import org.geotools.coverage.io.DefaultCoverageReader;

import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.image.io.mosaic.MosaicImageReader;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.TileWritingPolicy;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WorldImageFactory {
    
    private static final File TILE_CACHE_FOLDER = new File(System.getProperty("java.io.tmpdir") + File.separator + "imageTiles");
    private static final Logger LOGGER = org.geotoolkit.util.logging.Logging.getLogger("org.geotools.coverage");
    
    /**
     * Create a simple reader which doesnt use any pyramid or mosaic tiling.
     * Use this reader if you know you have a small image.
     */
    public CoverageReader createSimpleReader(File input) throws IOException, NoninvertibleTransformException{
        final MathTransform trs = readTransform(input);
        final CoordinateReferenceSystem crs = readCRS(input);
        final ImageReader reader = buildSimpleReader(input);
        return new DefaultCoverageReader(reader,trs,crs);
    }
    
    /**
     * Create a mosaic reader which will create a cache of tiles at different
     * resolutions. Tiles creation time depends on the available memory, the image
     * size and it's format. The creation time can go from a few seconds to several
     * minuts or even hours if you give him an image like the full resolution BlueMarble.
     */
    public CoverageReader createMosaicReader(File input) throws IOException, NoninvertibleTransformException{
        final int tileSize = 512;
        final File tileFolder = getTempFolder(input,tileSize);
        return createMosaicReader(input, tileSize, tileFolder);
    }
    
    /**
     * Create a mosaic reader which will create a cache of tiles at different
     * resolutions.
     * 
     * @param tileSize : favorite tile size, this should go over 2000, recommmanded 512 or 256.
     * @param tileFolder : cache directory where tiles will be stored
     */
    public CoverageReader createMosaicReader(File input, int tileSize, File tileFolder) throws IOException, NoninvertibleTransformException{
        final MathTransform trs = readTransform(input);
        final CoordinateReferenceSystem crs = readCRS(input);                
        final ImageReader reader = buildMosaicReader(input, tileSize, tileFolder);
        return new DefaultCoverageReader(reader,trs,crs);
    }
    
    /**
     * Create a simple reader.
     */
    private static ImageReader buildSimpleReader(File input){
        throw new UnsupportedOperationException("Simple reader not implemented yet");
    }
    
    /**
     * Create a Mosaic reader.
     */
    private static ImageReader buildMosaicReader(File input, int tileSize, File tileFolder) throws IOException{
        final MosaicBuilder builder = new MosaicBuilder();
        builder.setTileSize(new Dimension(tileSize,tileSize));
        //let the builder build the best pyramid resolutions
        //builder.setSubsamplings(new int[]{1,2,4,6,8,12,16,20,30});
        builder.setTileDirectory(tileFolder);
                
        final TileManager manager = builder.createTileManager(input, 0, TileWritingPolicy.WRITE_NEWS_ONLY);
        final MosaicImageReader reader = new MosaicImageReader();
        reader.setInput(manager);
        return reader;
    }
    
    /**
     * Get or create a temp folder to store the mosaic. the folder is based on the
     * file name, so tiles can be find again if the file name hasn't change.
     */
    private static File getTempFolder(File input,int tileSize) throws IOException{
        
        final int stop = input.getName().lastIndexOf(".");
        
        final String name;
        if(stop > 0){
            //remove the extension part
            name = input.getName().substring(0, stop);
        }else{
            //no extension? use the full name
            name = input.getName();
        }
        final StringBuilder builder = new StringBuilder(TILE_CACHE_FOLDER.getAbsolutePath());
        builder.append(File.separator);
        builder.append(name);
        builder.append("_");
        builder.append(tileSize);
        File cacheFolder = new File(builder.toString());
        
        //create the tile folder if not created
        cacheFolder.mkdirs();
        
        return cacheFolder;
    }
    
    /**
     * read the transform provided in the world image file.
     * 
     */
    private static MathTransform readTransform(File input) throws IOException{
        MathTransform transform = null;
        
        final Set<String> possibleExtensions = WorldImageConstants.getWorldExtension(input.getName());
        
        final int stop = input.getAbsolutePath().lastIndexOf(".");
        final String worldFilePath;
        if(stop > 0){
            //remove the extension part
            worldFilePath = input.getAbsolutePath().substring(0, stop);
        }else{
            //no extension? use the full name
            worldFilePath = input.getAbsolutePath();
        }
        
        for(String ext : possibleExtensions){
            File candidate = new File(worldFilePath + ext);
            if(candidate.exists()){
                transform = new AffineTransform2D(WorldFileReader.parse(new FileInputStream(candidate)));
                break;
            }
        }
        
        return transform;
    }
    
    /**
     * Extract the src of the given file by using the .prj file.
     * if the file could not be find or parsed, return WGS:84.
     */
    private static CoordinateReferenceSystem readCRS(File source) throws IOException{
        CoordinateReferenceSystem crs = null;
        
        final String filePath = source.getAbsolutePath();
        final int index = filePath.lastIndexOf(".");
        final String prjFilePath = filePath.substring(0, index) + ".prj";
        final File prjFile = new File(prjFilePath);

        if (prjFile.exists()) {            
            try {
                crs = PrjFileReader.parse(new FileInputStream(prjFile));
            } catch (FactoryException e) {
                LOGGER.log(Level.INFO, e.getLocalizedMessage(), e);
            }
        }

        if (crs == null) {
            //we could not read the CRS, we assume it is WGS84
            crs = DefaultGeographicCRS.WGS84;
            LOGGER.info("Unable to find crs, continu with WGS4 CRS");
        }
        
        return crs;
    }
    
}

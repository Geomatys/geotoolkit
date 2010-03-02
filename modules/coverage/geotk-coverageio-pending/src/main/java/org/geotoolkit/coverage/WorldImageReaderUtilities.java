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
package org.geotoolkit.coverage;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageReader;

import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.image.io.mosaic.MosaicImageReader;
import org.geotoolkit.image.io.mosaic.MosaicImageWriteParam;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.TileWritingPolicy;

import org.opengis.referencing.operation.NoninvertibleTransformException;

/**
 * Utility class to aquiere a coverage reader from a file.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WorldImageReaderUtilities {
    
    private static final File TILE_CACHE_FOLDER = new File(System.getProperty("java.io.tmpdir") + File.separator + "imageTiles");

    private WorldImageReaderUtilities(){}

    /**
     * Create a simple reader which doesnt use any pyramid or mosaic tiling.
     * Use this reader if you know you have a small image.
     */
    public static GridCoverageReader createSimpleReader(File input) throws IOException, CoverageStoreException{
        final ImageCoverageReader ic = new ImageCoverageReader();
        ic.setInput(input);
        return ic;
    }
    
    /**
     * Create a mosaic reader which will create a cache of tiles at different
     * resolutions. Tiles creation time depends on the available memory, the image
     * size and it's format. The creation time can go from a few seconds to several
     * minuts or even hours if you give him an image like the full resolution BlueMarble.
     */
    public static GridCoverageReader createMosaicReader(File input) throws IOException, NoninvertibleTransformException, CoverageStoreException{
        final int tileSize = 512;
        final File tileFolder = getTempFolder(input,tileSize);
        return createMosaicReader(input, tileSize, tileFolder);
    }

    /**
     * Create a mosaic reader which will create a cache of tiles at different
     * resolutions. Tiles creation time depends on the available memory, the image
     * size and it's format. The creation time can go from a few seconds to several
     * minuts or even hours if you give him an image like the full resolution BlueMarble.
     */
    public static GridCoverageReader createMosaicReader(URL input) throws IOException, NoninvertibleTransformException, CoverageStoreException{
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
    public static GridCoverageReader createMosaicReader(File input, int tileSize, File tileFolder) throws IOException, NoninvertibleTransformException, CoverageStoreException{
        final ImageReader reader = buildMosaicReader(input, tileSize, tileFolder);
        final ImageCoverageReader ic = new ImageCoverageReader();
        ic.setInput(reader);
        return ic;
    }

    /**
     * Create a mosaic reader which will create a cache of tiles at different
     * resolutions.
     *
     * @param tileSize : favorite tile size, this should go over 2000, recommmanded 512 or 256.
     * @param tileFolder : cache directory where tiles will be stored
     */
    public static GridCoverageReader createMosaicReader(URL input, int tileSize, File tileFolder) throws IOException, NoninvertibleTransformException, CoverageStoreException{
        final ImageReader reader = buildMosaicReader(input, tileSize, tileFolder);
        final ImageCoverageReader ic = new ImageCoverageReader();
        ic.setInput(reader);
        return ic;
    }
    
    /**
     * Create a Mosaic reader.
     */
    private static ImageReader buildMosaicReader(Object input, int tileSize, File tileFolder) throws IOException{
        final MosaicBuilder builder = new MosaicBuilder();
        builder.setTileSize(new Dimension(tileSize,tileSize));
        //let the builder build the best pyramid resolutions
        //builder.setSubsamplings(new int[]{1,2,4,6,8,12,16,20,30});
        builder.setTileDirectory(tileFolder);

        final MosaicImageWriteParam params = new MosaicImageWriteParam();
        params.setTileWritingPolicy(TileWritingPolicy.WRITE_NEWS_ONLY);
        final TileManager manager = builder.writeFromInput(input, params);
        final MosaicImageReader reader = new MosaicImageReader();
        reader.setInput(manager);
        return reader;
    }
    
    /**
     * Get or create a temp folder to store the mosaic. the folder is based on the
     * file name, so tiles can be find again if the file name hasn't change.
     */
    private static File getTempFolder(File input,int tileSize) throws IOException{
        return getTempFolder(input.getName(), tileSize);
    }

    /**
     * Get or create a temp folder to store the mosaic. the folder is based on the
     * file name, so tiles can be find again if the file name hasn't change.
     */
    private static File getTempFolder(URL input,int tileSize) throws IOException{
        return getTempFolder(input.getFile(), tileSize);
    }

    /**
     * Get or create a temp folder to store the mosaic. the folder is based on the
     * file name, so tiles can be find again if the file name hasn't change.
     */
    private static File getTempFolder(String pathName,int tileSize) throws IOException{

        //we must test both since the slash may change if we are in a jar on windows
        int lastSlash = pathName.lastIndexOf('/');
        int second = pathName.lastIndexOf('\\');
        if(second > lastSlash) lastSlash = second;

        if(lastSlash >= 0){
            pathName = pathName.substring(lastSlash+1, pathName.length());
        }
        
        final int stop = pathName.lastIndexOf(".");

        final String name;
        if(stop >= 0){
            //remove the extension part
            name = pathName.substring(0, stop);
        }else{
            //no extension? use the full name
            name = pathName;
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
    
}

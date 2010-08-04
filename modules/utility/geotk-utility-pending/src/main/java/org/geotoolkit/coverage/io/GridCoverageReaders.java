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
package org.geotoolkit.coverage.io;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;

import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.image.io.mosaic.MosaicImageReader;
import org.geotoolkit.image.io.mosaic.MosaicImageWriteParam;
import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.TileManagerFactory;
import org.geotoolkit.image.io.mosaic.TileWritingPolicy;
import org.geotoolkit.lang.Static;

import org.opengis.coverage.grid.GridGeometry;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * Utility class to aquiere a coverage reader from a file.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@Static
public class GridCoverageReaders {

    private static final File TILE_CACHE_FOLDER = new File(System.getProperty("java.io.tmpdir") + File.separator + "imageTiles");

    private GridCoverageReaders(){}

    /**
     * Create a simple reader which doesnt use any pyramid or mosaic tiling.
     * Use this reader if you know you have a small image.
     */
    public static GridCoverageReader createSimpleReader(File input) throws CoverageStoreException{
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
    public static GridCoverageReader createMosaicReader(File input) throws IOException, CoverageStoreException{
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
    public static GridCoverageReader createMosaicReader(URL input) throws IOException, CoverageStoreException{
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
    public static GridCoverageReader createMosaicReader(File input, int tileSize, File tileFolder) throws IOException, CoverageStoreException{
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
    public static GridCoverageReader createMosaicReader(URL input, int tileSize, File tileFolder) throws IOException, CoverageStoreException{
        final ImageReader reader = buildMosaicReader(input, tileSize, tileFolder);
        final ImageCoverageReader ic = new ImageCoverageReader();
        ic.setInput(reader);
        return ic;
    }

    /**
     * Open an already existing mosaic.
     *
     * @param folder : File folder where all tiles are stored
     * @return GridCoverageReader
     * @throws IOException
     * @throws CoverageStoreException
     */
    public static GridCoverageReader openMosaic(File folder) throws IOException, CoverageStoreException{

        //our folder is a collection of tiles for the same layer
        final Collection<Tile> tiles = new ArrayList<Tile>();
        final CoordinateReferenceSystem crs = visit(folder, tiles);

        final TileManager[] managers = TileManagerFactory.DEFAULT.create(tiles);
        final TileManager manager = managers[0];

        final ImageCoverageReader reader = new ImageCoverageReader() {
            @Override
            public GridGeometry2D getGridGeometry(int index) throws CoverageStoreException {
                //override the CRS
                final GridGeometry gridGeom;
                try {
                    gridGeom = manager.getGridGeometry();
                } catch (IOException ex) {
                    throw new CoverageStoreException(ex);
                }
                return new GridGeometry2D(gridGeom.getGridRange(), gridGeom.getGridToCRS(), crs);
            }

        };

        reader.setInput(manager);
        return reader;
    }

    /**
     * Visit all files and directories contained in the directory specified.
     *
     * @param file The starting file or folder.
     */
    private static CoordinateReferenceSystem visit(final File file, Collection<Tile> tiles) {

        CoordinateReferenceSystem crs = null;

        if (file.isDirectory()) {
            final File[] list = file.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    CoordinateReferenceSystem ccrs = visit(list[i],tiles);
                    if(ccrs != null){
                        crs = ccrs;
                    }
                }
            }
        } else {
            CoordinateReferenceSystem ccrs = test(file,tiles);
            if(ccrs != null){
                crs = ccrs;
            }
        }

        return crs;
    }

    /**
     * @param candidate to be an coverage tile.
     */
    private static CoordinateReferenceSystem test(final File candidate, Collection<Tile> tiles){
        if (candidate.isFile()){
            try {
                final ImageReader reader = XImageIO.getReader(candidate, Boolean.TRUE, Boolean.FALSE);
                final IIOMetadata metadata = reader.getImageMetadata(0);

                if(metadata instanceof SpatialMetadata){
                    final SpatialMetadata meta = (SpatialMetadata) metadata;
                    final CoordinateReferenceSystem crs = meta.getInstanceForType(CoordinateReferenceSystem.class);
                    final RectifiedGrid grid = meta.getInstanceForType(RectifiedGrid.class);
                    final MathTransform trs = MetadataHelper.INSTANCE.getGridToCRS(grid);

                    final Tile tile = new Tile(reader.getOriginatingProvider(), candidate, 0,null,(AffineTransform)trs);
                    tiles.add(tile);
                    return crs;
                }

            } catch (IOException ex) {
                //don't log, this can happen very often when testing non image files
            }
        }
        return null;
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
    private static File getTempFolder(File input,int tileSize) {
        return getTempFolder(input.getName(), tileSize);
    }

    /**
     * Get or create a temp folder to store the mosaic. the folder is based on the
     * file name, so tiles can be find again if the file name hasn't change.
     */
    private static File getTempFolder(URL input,int tileSize) {
        return getTempFolder(input.getFile(), tileSize);
    }

    /**
     * Get or create a temp folder to store the mosaic. the folder is based on the
     * file name, so tiles can be find again if the file name hasn't change.
     */
    private static File getTempFolder(String pathName,int tileSize) {

        final int lastSlash = pathName.lastIndexOf(File.separator);

        if(lastSlash >= 0){
            pathName = pathName.substring(lastSlash+1, pathName.length());
        }

        final int stop = pathName.lastIndexOf('.');

        final String name;
        if(stop >= 0){
            //remove the extension part
            name = pathName.substring(0, stop);
        }else{
            //no extension? use the full name
            name = pathName;
        }
        final StringBuilder builder = new StringBuilder(TILE_CACHE_FOLDER.getAbsolutePath());
        builder.append(File.separator).append(name).append('_').append(tileSize);
        final File cacheFolder = new File(builder.toString());

        //create the tile folder if not created
        cacheFolder.mkdirs();

        return cacheFolder;
    }

}

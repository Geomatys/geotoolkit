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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;

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
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.io.wkt.PrjFiles;
import org.geotoolkit.lang.Static;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.util.logging.Logging;

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
public final class GridCoverageReaders {

    private static final Logger LOGGER = Logging.getLogger(GridCoverageReaders.class);

    private static final File TILE_CACHE_FOLDER = new File(System.getProperty("java.io.tmpdir") + File.separator + "imageTiles");

    private GridCoverageReaders(){}

    /**
     * Create a mosaic reader which will create a cache of tiles at different
     * resolutions. Tiles creation time depends on the available memory, the image
     * size and it's format. The creation time can go from a few seconds to several
     * minuts or even hours if you give him an image like the full resolution BlueMarble.
     */
    public static GridCoverageReader createMosaicReader(final URL input) throws IOException, CoverageStoreException{
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
    public static GridCoverageReader createMosaicReader(final File input, final int tileSize, final File tileFolder) throws IOException, CoverageStoreException{
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
    public static GridCoverageReader createMosaicReader(final URL input, final int tileSize, final File tileFolder) throws IOException, CoverageStoreException{
        final ImageReader reader = buildMosaicReader(input, tileSize, tileFolder);
        final ImageCoverageReader ic = new ImageCoverageReader();
        ic.setInput(reader);
        return ic;
    }

    /**
     * Open a tile manager or create it from a folder of images.
     * The tile manager classe is concurrent and can be used by several 
     * grid coverage reader at the same time.
     * 
     * @param file
     * @return Entry<TileManager,CoordinateReferenceSystem>
     * @throws IOException
     * @throws CoverageStoreException
     *
     * @deprecated Partially replaced by {@link TileManagerFactory#create(File)} and {@link PrjFiles#read(File)}.
     */
    @Deprecated
    public static Entry<TileManager,CoordinateReferenceSystem> openTileManager(final File file) throws IOException, CoverageStoreException{
        TileManager manager = null;
        final CoordinateReferenceSystem crs;
        if(file.exists() && !file.isDirectory()){
            //file is likely to be a serialized tile manager
            final ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            Object obj;
            try {
                obj = in.readObject();
            } catch (ClassNotFoundException ex) {
                throw new CoverageStoreException(ex);
            }finally{
                in.close();
            }

            if(obj instanceof TileManager){
                manager = (TileManager)obj;

                final File prjFile = (File)IOUtilities.changeExtension(file, "prj");
                if(prjFile.exists()){
                    crs = PrjFiles.read(prjFile);
                }else{
                    throw new CoverageStoreException("No projection file associated with the tile manager.");
                }
            }else{
                throw new CoverageStoreException("File is not a serialized TileManager.");
            }
        }else{
            //our folder is a collection of tiles for the same layer
            final Collection<Tile> tiles = new ArrayList<Tile>();
            crs = visit(file, tiles);

            final TileManager[] managers = TileManagerFactory.DEFAULT.create(tiles);

            if(managers.length == 0){
                throw new CoverageStoreException("No files could be handle as tiles.");
            }else if(managers.length > 1){
                LOGGER.log(Level.WARNING, "Coverage tiles are not aligned, "
                        + "verify that images do not overlap and overviews preserve ratio. "
                        + "Found "+managers.length+" tile managers.");

                manager = managers[0];
                double resolution = XAffineTransform.getScale(manager.getGridGeometry().getGridToCRS());

                for(int i=1; i<managers.length; i++){
                    final TileManager tm = managers[i];
                    final double tmRes = XAffineTransform.getScale(tm.getGridGeometry().getGridToCRS());
                    if(tmRes < resolution){
                        //we found a tile manager with a better resolution. lets use it
                        manager = tm;
                        resolution = tmRes;
                    }else if(tmRes == resolution){
                        //two tile manager with same resolution, that mean two base tiles have
                        //been separated, we better choose the one that contain the more tiles.
                        LOGGER.log(Level.WARNING, "Two base tiles have been placed in different tile managers.");
                        if(tm.getTiles().size()> manager.getTiles().size()){
                            //this manager has more tile, use it
                            manager = tm;
                            resolution = tmRes;
                        }
                    }
                }
            }else{
                manager = managers[0];
            }
        }

        return new SimpleEntry<TileManager, CoordinateReferenceSystem>(manager, crs);
    }

    /**
     * Visit all files and directories contained in the directory specified.
     *
     * @param file The starting file or folder.
     */
    private static CoordinateReferenceSystem visit(final File file, final Collection<Tile> tiles) {

        CoordinateReferenceSystem crs = null;

        if (file.isDirectory()) {
            final File[] list = file.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    final CoordinateReferenceSystem ccrs = visit(list[i],tiles);
                    if(ccrs != null){
                        crs = ccrs;
                    }
                }
            }
        } else {
            final CoordinateReferenceSystem ccrs = test(file,tiles);
            if(ccrs != null){
                crs = ccrs;
            }
        }

        return crs;
    }

    /**
     * @param candidate to be an coverage tile.
     */
    private static CoordinateReferenceSystem test(final File candidate, final Collection<Tile> tiles){
        if (candidate.isFile()){
            try {
                final ImageReader reader = XImageIO.getReader(candidate, Boolean.FALSE, Boolean.FALSE);
                final IIOMetadata metadata = reader.getImageMetadata(0);
                final ImageReaderSpi spi = org.geotoolkit.image.io.ImageReaderAdapter.Spi.unwrap(reader.getOriginatingProvider());

                if(metadata instanceof SpatialMetadata){
                    final SpatialMetadata meta = (SpatialMetadata) metadata;
                    final CoordinateReferenceSystem crs = meta.getInstanceForType(CoordinateReferenceSystem.class);
                    final RectifiedGrid grid = meta.getInstanceForType(RectifiedGrid.class);
                    final MathTransform trs = MetadataHelper.INSTANCE.getGridToCRS(grid);
                    final AffineTransform af = new AffineTransform((AffineTransform)trs);
                    final int totalWidth = reader.getWidth(0);
                    final int totalHeight = reader.getHeight(0);

                    //image may contain some overview, we must register all overviews as tiles
                    final int nbImg = reader.getNumImages(true);
                    for(int i=0;i<nbImg;i++){
                        final double scaleX = (totalWidth/(double)reader.getWidth(i));
                        final double scaleY = (totalHeight/(double)reader.getHeight(i));
                        final AffineTransform c = new AffineTransform(
                                af.getScaleX() * scaleX,
                                af.getShearY() * scaleY,
                                af.getShearX() * scaleX,
                                af.getScaleY() * scaleY,
                                af.getTranslateX(),
                                af.getTranslateY());

                        final Tile tile = new Tile(spi, candidate, i,null,c);
                        tiles.add(tile);
                    }

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
    private static ImageReader buildMosaicReader(final Object input, final int tileSize, final File tileFolder) throws IOException{
        final TileManager manager = buildTileManager(input, tileSize, tileFolder);
        final MosaicImageReader reader = new MosaicImageReader();
        reader.setInput(manager);
        return reader;
    }

    /**
     * Create a TileManager from the given input.
     */
    public static TileManager buildTileManager(final Object input, final int tileSize, final File tileFolder) throws IOException{
        return buildTileManager(input, null, tileSize, tileFolder);
    }

    /**
     * Create a TileManager from the given input.
     *
     * @param input : original source
     * @param gridtoCRS : grid to crs
     * @param tileSize : wanted tiles size
     * @param tileFolder : where to store created tiles
     */
    public static TileManager buildTileManager(final Object input, final AffineTransform gridtoCRS, final int tileSize, final File tileFolder) throws IOException{
        final MosaicBuilder builder = new MosaicBuilder();
        builder.setTileSize(new Dimension(tileSize,tileSize));
        builder.setGridToCRS(gridtoCRS);
        //let the builder build the best pyramid resolutions
        //builder.setSubsamplings(new int[]{1,2,4,6,8,12,16,20,30});
        builder.setTileDirectory(tileFolder);

        final MosaicImageWriteParam params = new MosaicImageWriteParam();
        params.setTileWritingPolicy(TileWritingPolicy.WRITE_NEWS_ONLY);
        final TileManager manager = builder.writeFromInput(input, params);
        return manager;
    }


    /**
     * Get or create a temp folder to store the mosaic. the folder is based on the
     * file name, so tiles can be find again if the file name hasn't change.
     */
    private static File getTempFolder(final URL input,final int tileSize) {
        return getTempFolder(input.getFile(), tileSize);
    }

    /**
     * Get or create a temp folder to store the mosaic. the folder is based on the
     * file name, so tiles can be find again if the file name hasn't change.
     */
    private static File getTempFolder(String pathName,final int tileSize) {

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

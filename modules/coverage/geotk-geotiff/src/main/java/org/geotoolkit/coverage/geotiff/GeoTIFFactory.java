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
package org.geotoolkit.coverage.geotiff;

import com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.DefaultCoverageReader;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.coverage.geotiff.IIOMetadataAdpaters.GeoTiffIIOMetadataDecoder;
import org.geotoolkit.coverage.geotiff.crs_adapters.GeoTiffMetadata2CRSAdapter;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.image.io.mosaic.MosaicImageReader;
import org.geotoolkit.image.io.mosaic.MosaicImageWriteParam;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.TileWritingPolicy;
import org.geotoolkit.referencing.CRS;

import org.geotoolkit.referencing.operation.transform.ProjectiveTransform;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GeoTIFFactory {
    
    private static final File TILE_CACHE_FOLDER = new File(System.getProperty("java.io.tmpdir") + File.separator + "imageTiles");
    private static final Logger LOGGER = org.geotoolkit.util.logging.Logging.getLogger("org.geotoolkit.coverage.wi");

    /** SPI for creating tiff readers in ImageIO tools */
    private final static TIFFImageReaderSpi readerSPI = new TIFFImageReaderSpi();

    /**
     * Create a simple reader which doesnt use any pyramid or mosaic tiling.
     * Use this reader if you know you have a small image.
     */
    public CoverageReader createSimpleReader(File input) throws IOException, NoninvertibleTransformException{
        final Object[] metadatas = read(input);
        final MathTransform trs = (MathTransform) metadatas[1];
        final CoordinateReferenceSystem crs = ((GeneralEnvelope)metadatas[0]).getCoordinateReferenceSystem();
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
     * resolutions. Tiles creation time depends on the available memory, the image
     * size and it's format. The creation time can go from a few seconds to several
     * minuts or even hours if you give him an image like the full resolution BlueMarble.
     */
    public CoverageReader createMosaicReader(URL input) throws IOException, NoninvertibleTransformException{
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
        final Object[] metadatas = read(input);
        final MathTransform trs = (MathTransform) metadatas[1];
        final CoordinateReferenceSystem crs = ((GeneralEnvelope)metadatas[0]).getCoordinateReferenceSystem();
        final ImageReader reader = buildMosaicReader(input, tileSize, tileFolder);
        return new DefaultCoverageReader(reader,trs,crs);
    }

    /**
     * Create a mosaic reader which will create a cache of tiles at different
     * resolutions.
     *
     * @param tileSize : favorite tile size, this should go over 2000, recommmanded 512 or 256.
     * @param tileFolder : cache directory where tiles will be stored
     */
    public CoverageReader createMosaicReader(URL input, int tileSize, File tileFolder) throws IOException, NoninvertibleTransformException{
        final Object[] metadatas = read(input);
        final MathTransform trs = (MathTransform) metadatas[1];
        final CoordinateReferenceSystem crs = ((GeneralEnvelope)metadatas[0]).getCoordinateReferenceSystem();
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

    private Object[] read(Object input) throws IOException {
        if (input == null) {
            throw new IOException("GeoTiffReader:No source set to read this coverage.");
        }

        // /////////////////////////////////////////////////////////////////////
        // Set the source being careful in case it is an URL pointing to a file
        // /////////////////////////////////////////////////////////////////////
        Object source;
        try {
            source = input;
            // setting source
            if (input instanceof URL) {
                final URL sourceURL = (URL) input;
                if (sourceURL.getProtocol().equalsIgnoreCase("http") || sourceURL.getProtocol().equalsIgnoreCase("ftp")) {
                    try {
                        source = sourceURL.openStream();
                    } catch (IOException e) {
                        new RuntimeException(e);
                    }
                } else if (sourceURL.getProtocol().equalsIgnoreCase("file")) {
                    source = new File(URLDecoder.decode(sourceURL.getFile(),
                            "UTF-8"));
                }
            }


            final ImageInputStream inStream;
            boolean closeMe = true;
            // /////////////////////////////////////////////////////////////////////
            // Get a stream in order to read from it for getting the basic
            // information for this coverage
            // /////////////////////////////////////////////////////////////////////
            if ((source instanceof InputStream) || (source instanceof ImageInputStream)) {
                closeMe = false;
            }
            if (source instanceof ImageInputStream) {
                inStream = (ImageInputStream) source;
            } else {
                inStream = ImageIO.createImageInputStream(source);
            }
            if (inStream == null) {
                throw new IllegalArgumentException("No input stream for the provided source");
            }
            Object[] metadatas = getHRInfo(inStream);

            if (closeMe){
                inStream.close();
            }

            return metadatas;

        } catch (IOException e) {
            throw e;
        } catch (TransformException e) {
            throw new IOException(e);
        } catch (FactoryException e) {
            throw new IOException(e);
        }
    }

    /**
     *
     * @param inStream
     * @return an arry, first object is the envelope, second is the affine transform
     * @throws IOException
     * @throws FactoryException
     * @throws GeoTiffException
     * @throws TransformException
     * @throws MismatchedDimensionException
     * @throws IOException
     */
    private static Object[] getHRInfo(ImageInputStream inStream) throws IOException, FactoryException,
            GeoTiffException, TransformException, MismatchedDimensionException,
            IOException {

        // /////////////////////////////////////////////////////////////////////
        // Forcing longitude first since the geotiff specification seems to
        // assume that we have first longitude the latitude.
        // /////////////////////////////////////////////////////////////////////
        final Hints hints = new Hints();
        hints.add(new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER,Boolean.TRUE));

        // //
        // Get a reader for this format
        // //

        final ImageReader reader = readerSPI.createReaderInstance();

        // //
        // get the METADATA
        // //
        reader.setInput(inStream);
        final IIOMetadata iioMetadata = reader.getImageMetadata(0);
        final GeoTiffIIOMetadataDecoder metadata = new GeoTiffIIOMetadataDecoder(iioMetadata);
        final GeoTiffMetadata2CRSAdapter gtcs = (GeoTiffMetadata2CRSAdapter) GeoTiffMetadata2CRSAdapter.get(hints);

        // //
        // get the CRS INFO
        // //

        CoordinateReferenceSystem crs;
        final Object tempCRS = hints.get(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM);
        if (tempCRS != null) {
            crs = (CoordinateReferenceSystem) tempCRS;
            LOGGER.log(Level.WARNING, "Using forced coordinate reference system "+crs.toWKT());
        } else {
            crs = gtcs.createCoordinateSystem(metadata);
            // //
            // get the dimension of the hr image and build the model as well as
            // computing the resolution
            // //
        }

        int hrWidth = reader.getWidth(0);
        int hrHeight = reader.getHeight(0);
        final Rectangle actualDim = new Rectangle(0, 0, hrWidth, hrHeight);

        MathTransform raster2Model = gtcs.getRasterToModel(metadata);
        final AffineTransform tempTransform = new AffineTransform((AffineTransform) raster2Model);
        tempTransform.translate(-0.5, -0.5);
        GeneralEnvelope originalEnvelope = CRS.transform(ProjectiveTransform.create(tempTransform), new GeneralEnvelope(actualDim));
        originalEnvelope.setCoordinateReferenceSystem(crs);

        return new Object[]{originalEnvelope,raster2Model};
    }
    
}

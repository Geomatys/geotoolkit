/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.storage.uri;

import java.util.Objects;
import java.util.Properties;
import javax.imageio.spi.ImageReaderSpi;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.storage.multires.TileFormat;

/**
 * Definition of a tile encoding and path resolving.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class URITileFormat extends TileFormat {

    private static final String PROP_FORMAT          = "format";
    private static final String PROP_COMPRESSION     = "compression";
    private static final String PROP_IMGCOMPRESSION  = "imagecompression";
    private static final String PROP_PATTERN         = "pattern";

    /**
     * Common format for PNG tiles.
     * Pattern is {y}/{x}.png
     */
    public static final URITileFormat PNG;

    /**
     * Common format for geotiff tiles.
     * Pattern is {y}/{x}.tiff
     */
    public static final URITileFormat TIFF;
    static {
        try {
            PNG = new URITileFormat("image/png");
        } catch (DataStoreException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
        try {
            TIFF = new URITileFormat("image/x-geotiff");
        } catch (DataStoreException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    private String pattern;
    private String format;
    private String imageCompression;

    //caches
    private ImageReaderSpi imageSpi;
    private DataStoreProvider storeProvider;

    /**
     * Cas or create a new URITileFormat from given TileFormat.
     * @param format not null
     * @return URITileFormat
     * @throws DataStoreException
     */
    public static URITileFormat castOrCopy(TileFormat format) throws DataStoreException {
        if (format instanceof URITileFormat) {
            return (URITileFormat) format;
        } else {
            return new URITileFormat(format);
        }
    }

    /**
     * Create an URITileFormat from given TileFormat.
     *
     * @param tileFormat not null
     * @throws DataStoreException
     */
    public URITileFormat(TileFormat tileFormat) throws DataStoreException {
        ArgumentChecks.ensureNonNull("tile format", tileFormat);
        this.mimeType = tileFormat.getMimeType();
        this.providerId = tileFormat.getProviderId();
        this.compression = tileFormat.getCompression();

        String extension = null;
        if (this.mimeType != null) {
            final String[] formatNames = XImageIO.getFormatNamesByMimeType(this.mimeType, true, false);
            if (formatNames.length != 0) {
                this.format = this.mimeType;
                imageSpi = XImageIO.getReaderSpiByFormatName(formatNames[0]);
                final String[] fileSuffixes = imageSpi.getFileSuffixes();
                if (fileSuffixes != null) {
                    //javadoc guarantee to have at least one elemeent, otherwise it would be null
                    extension = fileSuffixes[0];
                }
            } else {
                throw new DataStoreException("Unsupported image mime-type : " + this.mimeType);
            }
        }

        if (imageSpi == null && this.providerId != null) {
            //Search for a datastore format
            storeProvider = org.geotoolkit.storage.DataStores.getProviderById(providerId);
            if (storeProvider != null) {
                format = providerId;
                final StoreMetadata meta = storeProvider.getClass().getAnnotation(StoreMetadata.class);
                if (meta != null) {
                    final String[] fileSuffixes = meta.fileSuffixes();
                    if (fileSuffixes.length != 0) extension = fileSuffixes[0];
                }
            } else {
                throw new DataStoreException("No datastore provider found for name : " + providerId);
            }
        }

        pattern = "{y}/{x}";
        if (extension != null) pattern += "."+extension;
    }

    public URITileFormat(String mimeTypeOrFormat) throws DataStoreException {
        this.compression = TileFormat.Compression.NONE;

        //search image format by mime type
        final String[] formatNames = XImageIO.getFormatNamesByMimeType(mimeTypeOrFormat, true, false);
        if (formatNames.length != 0) {
            format = mimeTypeOrFormat;
            imageSpi = XImageIO.getReaderSpiByFormatName(formatNames[0]);
            this.mimeType = mimeTypeOrFormat;
        } else {
            //search image format by format name
            try {
                imageSpi = XImageIO.getReaderSpiByFormatName(mimeTypeOrFormat);
                format = mimeTypeOrFormat;
                String[] mimeTypes = imageSpi.getMIMETypes();
                if (mimeTypes != null && mimeTypes.length > 0) {
                    this.mimeType = mimeTypes[0];
                }
            } catch (IllegalArgumentException ex) {
                //not found
            }
        }

        String extension = null;
        if (imageSpi != null) {
            //image format found
            final String[] fileSuffixes = imageSpi.getFileSuffixes();
            if (fileSuffixes != null) {
                //javadoc guarantee to have at least one elemeent, otherwise it would be null
                extension = fileSuffixes[0];
            }
        } else {
            //Search for a datastore format
            storeProvider = org.geotoolkit.storage.DataStores.getProviderById(mimeTypeOrFormat);
            if (storeProvider != null) {
                format = mimeTypeOrFormat;
                this.providerId = mimeTypeOrFormat;
                final StoreMetadata meta = storeProvider.getClass().getAnnotation(StoreMetadata.class);
                if (meta != null) {
                    final String[] fileSuffixes = meta.fileSuffixes();
                    if (fileSuffixes.length != 0) extension = fileSuffixes[0];
                }
            } else {
                throw new DataStoreException("No image or datastore format found for name or mime-type : "+mimeTypeOrFormat);
            }
        }
        pattern = "{y}/{x}";
        if (extension != null) pattern += "."+extension;
    }

    public URITileFormat(DataStoreProvider provider) throws DataStoreException {
        this.compression = TileFormat.Compression.NONE;

        String extension = null;
        //Search for a datastore format
        storeProvider = provider;
        format = storeProvider.getShortName();
        this.providerId = storeProvider.getShortName();
        final StoreMetadata meta = storeProvider.getClass().getAnnotation(StoreMetadata.class);
        if (meta != null) {
            final String[] fileSuffixes = meta.fileSuffixes();
            if (fileSuffixes.length != 0) extension = fileSuffixes[0];
        }
        pattern = "{y}/{x}";
        if (extension != null) pattern += "."+extension;
    }

    /**
     * @return true if this tile format is processed by an ImageSpi.
     */
    public boolean isImage() {
        return imageSpi != null;
    }

    /**
     * Set file compression.
     * @param compression not null
     */
    public void setCompression(Compression compression) {
        ArgumentChecks.ensureNonNull("compression", compression);
        this.compression = compression;
    }

    /**
     * Set additional image compression parameters.
     * @param imageCompression parameters passed to method ImageWriteParam.setCompressionType, or null
     */
    public void setImageCompression(String imageCompression) {
        this.imageCompression = imageCompression;
    }

    /**
     * @return parameters passed to method ImageWriteParam.setCompressionType, or null
     */
    public String getImageCompression() {
        return imageCompression;
    }

    /**
     * @param pattern URI path pattern, example : {y}/{x}.png
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * @return URI path pattern, example : {y}/{x}.png
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Get tile format name.
     * This it the format as declared by the user.
     * It can be a mime-type, JAI format name or datastore provider name.
     *
     * @return format name.
     */
    public String getFormat() {
        return format;
    }

    /**
     * @return Single tile DataStoreProvider, null if format is an image.
     */
    public DataStoreProvider getStoreProvider() {
        return storeProvider;
    }

    /**
     * @return Single tile ImageReaderSpi, null if format is not an image.
     */
    public ImageReaderSpi getImageSpi() {
        return imageSpi;
    }

    /**
     * Write URIFormat to given properties
     * @param properties not null
     */
    public void toProperties(Properties properties) {
        ArgumentChecks.ensureNonNull("properties", properties);
        properties.setProperty(PROP_FORMAT, format);
        final Compression compression = getCompression();
        final String pattern = getPattern();
        if (Compression.NONE != compression) {
            properties.setProperty(PROP_COMPRESSION, compression.name());
        }
        if (pattern != null) {
            properties.setProperty(PROP_PATTERN, pattern);
        }
        if (imageCompression != null) {
            properties.setProperty(PROP_IMGCOMPRESSION, imageCompression);
        }
    }

    /**
     * Read URIFormat from properties.
     * @param properties not null
     * @return format read
     * @throws DataStoreException
     */
    public static URITileFormat fromProperties(Properties properties) throws DataStoreException {
        ArgumentChecks.ensureNonNull("properties", properties);
        final String compression = properties.getProperty(PROP_COMPRESSION);
        final String imagecompression = properties.getProperty(PROP_IMGCOMPRESSION);
        final String mimeTypeOrFormat = properties.getProperty(PROP_FORMAT);
        final String pattern = properties.getProperty(PROP_PATTERN);
        final URITileFormat format = new URITileFormat(mimeTypeOrFormat);
        if (compression != null) format.compression = Compression.valueOf(compression);
        if (imagecompression != null) format.imageCompression = imagecompression;
        if (pattern != null) format.pattern = pattern;
        return format;
    }

    @Override
    public int hashCode() {
        return 13;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final URITileFormat other = (URITileFormat) obj;
        if (!Objects.equals(this.pattern, other.pattern)) {
            return false;
        }
        if (!Objects.equals(this.format, other.format)) {
            return false;
        }
        if (getCompression() != other.getCompression()) {
            return false;
        }
        if (!Objects.equals(getImageCompression(), other.getImageCompression())) {
            return false;
        }
        return true;
    }

}

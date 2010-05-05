/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import javax.imageio.ImageReader;
import javax.imageio.IIOException;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.SpatialImageReader;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.sql.table.SpatialDatabase;
import org.geotoolkit.internal.sql.table.CatalogException;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.geotoolkit.resources.Errors;


/**
 * A structure which contain the information to be added in the {@linkplain CoverageDatabase
 * Coverage Database} for a new coverage reference. The information provided in this class
 * match closely the layout of the coverage database. Instances of this class are created
 * by {@link Layer#addCoverageReferences(Collection, CoverageDatabaseListener)} and can be
 * freely modified before the insertion in the database actually occurs.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.12
 *
 * @see CoverageDatabaseListener
 *
 * @since 3.12 (derived from Seagis)
 * @module
 */
public final class NewGridCoverageReference {
    /**
     * The series in which the images will be added, or {@code null} if unknown.
     * This field may be set explicitly by {@link WritableGridCoverageIterator},
     * and is read by {@link WritableGridCoverageTable}.
     */
    SeriesEntry series;

    /**
     * Contains most informations like image reader provider, input, image index and image bounds.
     * This field is not allowed to be {@code null}.
     */
    private final Tile tile;

    /**
     * The path to the coverage file (not including the filename), or {@code null} if the filename
     * has no parent directory. This is the first part of the input file, which is splitted in its
     * "{@linkplain #path}/{@linkplain #filename}.{@linkplain #extension}" components.
     */
    public final File path;

    /**
     * The filename, not including the {@linkplain #path} and {@linkplain #extension}.
     * This is part of the input file, which is splitted in its
     * "{@linkplain #path}/{@linkplain #filename}.{@linkplain #extension}" components.
     */
    public final String filename;

    /**
     * The filename extension (not including the leading dot), or {@code null} if none.
     * This is part of the input file, which is splitted in its
     * "{@linkplain #path}/{@linkplain #filename}.{@linkplain #extension}" components.
     */
    public final String extension;

    /**
     * The image bounds. The rectangle {@linkplain Rectangle#width width} and
     * {@linkplain Rectangle#height height} must be set to the image size. The
     * rectangle ({@linkplain#x x},{@linkplain#y y}) origin is usually (0,0),
     * but different value are allowed. For example the origin can be set to
     * the {@linkplain Tile#getLocation location of a tile} in tiled images.
     * <p>
     * If the (x,y) origin is different than (0,0), then it will be interpreted as the
     * translation to apply on the grid before to apply the <cite>grid to CRS</cite>
     * transform at reading time.
     * <p>
     * This field is never {@code null}. However users can modify it before the
     * new entry is inserted in the database.
     */
    public final Rectangle imageBounds;

    /**
     * The <cite>grid to CRS</cite> transform, which maps always the pixel
     * {@linkplain PixelOrientation#UPPER_LEFT upper left} corner.
     * <p>
     * If {@link #imageBounds} has a (x,y) origin different than (0,0), then the (x,y)
     * translation shall be applied before the {@code gridToCRS} transform.
     * <p>
     * This field is never {@code null}. However users can modify it before the
     * new entry is inserted in the database.
     */
    public final AffineTransform gridToCRS;

    /**
     * The horizontal CRS identifier. This shall be the value of a primary key
     * in the {@code "spatial_ref_sys"} PostGIS table.
     */
    public int horizontalSRID;

    /**
     * The vertical CRS identifier, ignored if {@link #verticalValues} is {@code null}.
     * When not ignored, this shall be the value of a primary key in the
     * {@code "spatial_ref_sys"} PostGIS table.
     */
    public int verticalSRID;

    /**
     * The vertical coordinate values, or {@code null} if none.
     */
    public double[] verticalValues;

    /**
     * The date range, or {@code null} if none. This array usually contains only one element,
     * but more than one time range is allowed if the image file contains data at many times.
     */
    public DateRange[] dateRanges;

    /**
     * Creates an entry for the given tile.
     *
     * @param  database The database where the new entry will be added.
     * @param  tile The tile to use for the entry.
     * @throws IOException if an error occured while reading the image.
     */
    NewGridCoverageReference(final SpatialDatabase database, final Tile tile)
            throws IOException, FactoryException
    {
        this(database, tile, tile.getImageReader());
    }

    /**
     * Creates en entry for the given reader.
     *
     * @param  database The database where the new entry will be added.
     * @param  reader The image reader.
     * @param  imageIndex Index of the image to read.
     * @throws IOException if an error occured while reading the image.
     */
    NewGridCoverageReference(final SpatialDatabase database, final ImageReader reader,
            final int imageIndex) throws IOException, FactoryException
    {
        this(database, new Tile(reader.getOriginatingProvider(), reader.getInput(), imageIndex,
             new Rectangle(reader.getWidth(imageIndex), reader.getHeight(imageIndex))), reader);
    }

    /**
     * Creates en entry for the given reader.
     *
     * @param  database The database where the new entry will be added.
     * @param  tile Information about the image to be inserted.
     * @param  reader The image reader, or {@code null}.
     * @param  helper The metadata helper, or {@code null} for creating it automatically.
     * @throws IOException if an error occured while reading the image.
     */
    private NewGridCoverageReference(final SpatialDatabase database,
            final Tile tile, final ImageReader reader) throws IOException, FactoryException
    {
        this.tile = tile;
        /*
         * Get the input, which must be an instance of File.
         * Split that input file into the path components.
         */
        Object input = IOUtilities.tryToFile(tile.getInput());
        if (!(input instanceof File)) {
            throw new IIOException(Errors.format(Errors.Keys.UNKNOWN_TYPE_$1,
                    Classes.getShortClassName(input)));
        }
        final File inputFile = (File) input;
        path = inputFile.getParentFile();
        final String name = inputFile.getName();
        final int split = name.lastIndexOf('.');
        if (split >= 0) {
            filename  = name.substring(0, split);
            extension = name.substring(split + 1);
        } else {
            filename  = name;
            extension = null;
        }
        /*
         * Get the metadata, preferrably from the Tile object (especially the gridToCRS transform),
         * then from the SpatialMetadata (CRS, etc.).
         */
        SpatialMetadata metadata = null;
        if (reader != null) {
            final IIOMetadata candidate = reader.getImageMetadata(tile.getImageIndex());
            if (candidate instanceof SpatialMetadata) {
                metadata = (SpatialMetadata) candidate;
            }
        }
        imageBounds = tile.getRegion();
        AffineTransform gridToCRS = tile.getGridToCRS();
        if (gridToCRS != null) {
            gridToCRS = new AffineTransform(gridToCRS);
        } else if (metadata != null) {
            final MetadataHelper helper = new MetadataHelper(
                    (reader instanceof SpatialImageReader) ? (SpatialImageReader) reader : null);
            gridToCRS = helper.getAffineTransform(metadata.getInstanceForType(RectifiedGrid.class), null);
        }
        this.gridToCRS = gridToCRS;
        if (metadata != null) {
            final CoordinateReferenceSystem crs = metadata.getInstanceForType(CoordinateReferenceSystem.class);
            if (crs != null) {
                final CRSAuthorityFactory crsFactory = database.getCRSAuthorityFactory();
                final CoordinateReferenceSystem horizontalCRS = CRS.getHorizontalCRS(crs);
                if (horizontalCRS != null) {
                    final Integer id = getIdentifier(horizontalCRS, crsFactory);
                    if (id != null) {
                        horizontalSRID = id;
                    }
                }
                final CoordinateReferenceSystem verticalCRS = CRS.getVerticalCRS(crs);
                if (verticalCRS != null) {
                    final Integer id = getIdentifier(verticalCRS, crsFactory);
                    if (id != null) {
                        verticalSRID = id;
                    }
                }
            }
        }
        /*
         * Close the reader but do not dispose it, since it may be used for the next entry.
         */
        if (reader != null) {
            input = reader.getInput();
            reader.reset();
            if (input instanceof Closeable) {
                ((Closeable) input).close();
            } else if (input instanceof ImageInputStream) {
                ((ImageInputStream) input).close();
            }
        }
    }

    /**
     * Returns the identifier of the given CRS. This method search first in the PostGIS
     * {@code "spatial_ref_sys"} table. If no identifier is found in that table, then it
     * search for the EPSG code on the assumption that PostGIS codes are often the same
     * numerical values than EPSG codes (not that there is nothing enforcing that; this
     * is only an observed common practice).
     *
     * @param  crs The CRS for which the identifier is wanted.
     * @param  crsFactory The PostGIS CRS factory, or {@code null} if none.
     * @return The identifier, or {@code null} if none.
     * @throws FactoryException If an error occured while searching for the identifier.
     */
    private static Integer getIdentifier(final CoordinateReferenceSystem crs, final CRSAuthorityFactory crsFactory)
            throws FactoryException
    {
        if (crsFactory instanceof AbstractAuthorityFactory) {
            final String id = ((AbstractAuthorityFactory) crsFactory)
                    .getIdentifiedObjectFinder(crs.getClass()).findIdentifier(crs);
            if (id != null) try {
                return Integer.valueOf(id);
            } catch (NumberFormatException e) {
                throw new FactoryException(e);
            }
        }
        return CRS.lookupEpsgCode(crs, true);
    }

    /**
     * Returns the most appropriate series in which to insert the coverage.
     * This is heuristic rules used when no series was explicitly defined.
     * <p>
     * Note: if {@link #extension} is {@code null} (not the same as an empty string),
     * it is interpreted as "any extension".
     *
     * @param  candidates The series to consider.
     * @return The series that seems the best match.
     * @throws CatalogException if there is ambiguity between series.
     */
    final SeriesEntry choose(final Collection<SeriesEntry> candidates) throws CatalogException {
        series = null;
        int mimeMatching = 0; // Greater the number, better is the matching of MIME type.
        int pathMatching = 0; // Greater the number, better is the matching of the file path.
        final ImageReaderSpi spi = tile.getImageReaderSpi();
        final String extension = (this.extension != null) ? this.extension : "";
        for (final SeriesEntry candidate : candidates) {
            /*
             * Asks for every files in the Series directory (e.g. "/home/data/foo/*.png"). The
             * filename contains a wildcard, but we will not use that. It is just a way to get
             * the path & extension, so we can check if the series have the expected extension.
             */
            final File allFiles = candidate.file("*");
            final String name   = allFiles.getName();
            final int split     = name.lastIndexOf('.');
            final String ext    = (split >= 0) ? name.substring(split + 1) : "";
            if (!extension.equalsIgnoreCase(ext)) {
                continue;
            }
            /*
             * Checks if the Series image format matches one of the ImageReader's format name.
             * If the ImageReader declares more generic types than the expected one, for example
             * if the ImageReader declares "NetCDF" while the Series expects "NetCDF-foo", we will
             * accept the ImageReader anyway but we will keep a trace of the quality of the matching,
             * so we can select a better match if we find one later.
             */
            if (spi != null) {
                final String[] mimeTypes = spi.getMIMETypes();
                final String[] formatNames = spi.getFormatNames();
                final String format = candidate.format.imageFormat.trim().toLowerCase();
                final String[] names = (mimeTypes != null && format.indexOf('/') >= 0) ? mimeTypes : formatNames;
                for (String type : names) {
                    type = type.trim().toLowerCase();
                    final int length = type.length();
                    if (length > mimeMatching && format.startsWith(type)) {
                        mimeMatching = length;
                        pathMatching = 0; // Format matching has precedence over path matching.
                        // Consequence of above, series will be assigned in the check for pathname below.
                    }
                }
            }
            /*
             * The most straightforward properties match (file extension, mime type...).
             * Now check the path in a more lenient way: we compare the Series path with
             * the ImageReader input's path starting from the end, and retain the series
             * with the deepest (in directory tree) match. If more than one series match
             * with the same deep, we retains the last one assuming that it is the one
             * for the most recent data.
             */
            int depth = 0;
            File f1 = path;
            File f2 = allFiles.getParentFile();
            while (f1 != null && f2 != null && f1.getName().equals(f2.getName())) {
                depth++;
                f1 = f1.getParentFile();
                f2 = f2.getParentFile();
            }
            if (depth >= pathMatching) {
                pathMatching = depth;
                series = candidate;
            }
        }
        if (series == null) {
            throw new CatalogException(Errors.format(Errors.Keys.NO_SERIES_SPECIFIED));
        }
        return series;
    }

    /**
     * Returns the format name. The default implementation select the longuest name,
     * on the assumption that it is the most explicit name.
     *
     * @param  upper {@code true} for the upper-case flavor (if available).
     * @return The format name (never {@code null} and never empty).
     * @throws IOException if the format can not be obtained.
     */
    protected String getFormatName(final boolean upper) throws IOException {
        String format = "";
        final ImageReaderSpi spi = tile.getImageReaderSpi();
        String[] formats = spi.getFormatNames();
        if (formats != null) {
            for (final String candidate : formats) {
                final int d = candidate.length() - format.length();
                if (d >= 0) {
                    if (d == 0) {
                        if (!upper) {
                            continue;
                        }
                        int na=0, nb=0;
                        for (int i=candidate.length(); --i>=0;) {
                            if (Character.isUpperCase(candidate.charAt(i))) na++;
                            if (Character.isUpperCase(format   .charAt(i))) nb++;
                        }
                        if (na <= nb) {
                            continue;
                        }
                    }
                    format = candidate;
                }
            }
        }
        if (format.length() == 0) {
            // No format found - fall back on mime types.
            formats = spi.getMIMETypes();
            if (formats != null) {
                for (final String candidate : formats) {
                    if (candidate.length() > format.length()) {
                        format = candidate;
                    }
                }
            }
            if (format.length() == 0) {
                throw new IOException(Errors.format(Errors.Keys.UNDEFINED_FORMAT));
            }
        }
        return format;
    }

    /**
     * Returns a string representation for debugging purpose.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(tile);
        if (series != null) {
            buffer.append(" in ").append(series);
        }
        return buffer.toString();
    }
}

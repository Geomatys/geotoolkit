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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import javax.imageio.IIOImage;
import static javax.imageio.ImageIO.createImageOutputStream;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.sis.coverage.BandedCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.Assets;
import org.geotoolkit.storage.Assets.Data;
import org.geotoolkit.storage.coverage.BandedCoverageResource;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.storage.coverage.WritableBandedCoverageResource;
import org.geotoolkit.storage.multires.AbstractTileMatrix;
import org.geotoolkit.storage.multires.DeferredTile;
import org.geotoolkit.storage.multires.Tile;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.opengis.geometry.DirectPosition;

/**
 * Tile matrix with resources located by URI.
 *
 * @author Johann Sorel (Geomatys)
 */
public class URITileMatrix extends AbstractTileMatrix {

    private final URI folderUri;
    private final ClientSecurity security;
    private final URITileFormat format;
    private final Dimension dataSize;

    //first evaluated when needed
    private Boolean isOnFileSystem = null;

    public URITileMatrix(TileMatrixSet parent, URI folder, ClientSecurity security, URITileFormat format, String id,
            DirectPosition upperLeft, Dimension gridSize, Dimension tileSize, Dimension dataSize, double scale) {
        super(id, parent, upperLeft, gridSize, tileSize, scale);
        ArgumentChecks.ensureNonNull("folder", folder);
        ArgumentChecks.ensureNonNull("format", format);
        ArgumentChecks.ensureNonNull("security", security);
        this.security = security;
        this.dataSize = dataSize;
        this.folderUri = folder;
        this.format = format;
    }

    /**
     * @return base URI to resolve tile paths.
     */
    public URI getFolder() {
        return folderUri;
    }

    /**
     * @return tile format
     */
    public URITileFormat getFormat() {
        return format;
    }

    @Override
    public URITileMatrixSet getTileMatrixSet() {
        return (URITileMatrixSet) super.getTileMatrixSet();
    }

    @Override
    public GridExtent getDataExtent() {
        if (dataSize != null) {
            return new GridExtent(dataSize.width, dataSize.height);
        }
        return super.getDataExtent();
    }

    @Override
    protected boolean isWritable() throws DataStoreException {
        return toPath(folderUri) != null;
    }

    @Override
    public boolean isMissing(long col, long row) {
        final URI tileUri = getTilePath(col, row);
        final Path tilePath = toPath(tileUri);
        if (tilePath != null) {
            return !Files.exists(tilePath);
        } else {
            //getting tile may be expensive
            //we assume the tile exist
            return false;
        }
    }

    @Override
    public Tile getTile(long col, long row, Map hints) throws DataStoreException {
        final URI tileUri = getTilePath(col, row);
        final Path tilePath = toPath(tileUri);
        if (tilePath != null && !Files.exists(tilePath)) return null;
        return URITile.create(this, tileUri, security, new Point(Math.toIntExact(col), Math.toIntExact(row)));
    }

    @Override
    public void deleteTile(int tileX, int tileY) throws DataStoreException {
        final URI tileUri = getTilePath(tileX, tileY);
        final Path tilePath = toPath(tileUri);
        if (tilePath == null) {
            throw new DataStoreException("Tile " + tileUri + " is not on the file system, delete is not supported.");
        }
        try {
            Files.deleteIfExists(tilePath);
        } catch (IOException ex) {
            throw new DataStoreException();
        }
    }

    public URI getTilePath(long x, long y) {
        return URIPattern.resolve(folderUri, this, format.getPattern(), x, y);
    }

    /**
     * Note : we don't start by deleting the previous tile, the end replace operation will do it.
     * @param tile
     * @throws DataStoreException
     */
    @Override
    protected void writeTile(Tile tile) throws DataStoreException {
        ArgumentChecks.ensureNonNull("tile", tile);
        final int tileX = tile.getPosition().x;
        final int tileY = tile.getPosition().y;
        final URI tileUri = getTilePath(tileX, tileY);
        final Path tilePath = toPath(tileUri);
        if (tilePath == null) {
            throw new DataStoreException("Tile " + tileUri + " is not on the file system, writing is not supported.");
        }
        final Path tempTilePath = tilePath.resolveSibling(tilePath.getFileName().toString()+"_"+Thread.currentThread().getId());
        try {
            Files.createDirectories(tilePath.getParent());
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }

        if (format.isImage()) {
            if (tile instanceof ImageTile) {
                try {
                    final RenderedImage image = ((ImageTile) tile).getImage();
                    final ImageWriterSpi writerSpi = XImageIO.getImageWriterSpi(format.getImageSpi());
                    final ImageWriter writer = writerSpi.createWriterInstance();

                    final File output = tempTilePath.toFile();
                    output.delete();
                    final ImageOutputStream stream = createImageOutputStream(output);

                    try {
                        ImageWriteParam writeParam = writer.getDefaultWriteParam();
                        if (format.getImageCompression() != null) {
                            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                            writeParam.setCompressionType(format.getImageCompression());
                        }
                        writer.setOutput(stream);
                        writer.write(null, new IIOImage(image, null, null), writeParam);
                        writer.dispose();
                    } finally {
                        stream.close();
                    }
                } catch (IOException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            } else {
                throw new DataStoreException("Invalid tile class "+tile.getClass()+". "
                        + "TileMatrixSet format is declared as image, an ImageTile instance is expected.");
            }
        } else {
            final DataStoreProvider provider = format.getStoreProvider();
            final StorageConnector connector = new StorageConnector(tempTilePath);
            try (final DataStore store = provider.open(connector)) {

                Resource tileData = tile;
                if (tile instanceof DeferredTile) {
                    tileData = ((DeferredTile) tile).open();
                }

                if (tileData instanceof Assets) {
                    final Assets source = (Assets) tile;
                    if (store instanceof Assets) {
                        final Assets target = (Assets) store;
                        for (Data data : source.getDatas()) {
                            target.addData(data);
                        }
                    } else {
                        throw new DataStoreException("No procedure found to copy from "+tile.getClass()+" to "+store.getClass());
                    }
                } if (tileData instanceof BandedCoverageResource && store instanceof WritableBandedCoverageResource) {
                    final BandedCoverage bandedCoverage = ((BandedCoverageResource) tileData).read(null);
                    ((WritableBandedCoverageResource) store).write(bandedCoverage);
                } else {
                    throw new DataStoreException("No procedure found to copy from "+tile.getClass()+" to "+store.getClass());
                }
            } finally {
                connector.closeAllExcept(null);
            }
        }

        //compress produced tile
        final URITileFormat.Compression compression = format.getCompression();
        switch(compression.name()) {
            case "GZ" : {
                //read and compress datas
                byte[] datas;
                try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(tempTilePath))) {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    final GZIPOutputStream gout = new GZIPOutputStream(out);
                    IOUtilities.copy(in, gout);
                    gout.finish();
                    gout.close();
                    datas = out.toByteArray();
                } catch (IOException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
                //write result
                try (OutputStream fout = new BufferedOutputStream(Files.newOutputStream(tempTilePath))) {
                    fout.write(datas);
                } catch (IOException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            } break;
            case "LZ4" : {
                //read and compress datas
                byte[] datas;
                try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(tempTilePath))) {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    final FramedLZ4CompressorOutputStream gout = new FramedLZ4CompressorOutputStream(out);
                    IOUtilities.copy(in, gout);
                    gout.finish();
                    gout.close();
                    datas = out.toByteArray();
                } catch (IOException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
                //write result
                try (OutputStream fout = new BufferedOutputStream(Files.newOutputStream(tempTilePath))) {
                    fout.write(datas);
                } catch (IOException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            } break;
        }

        try {
            //replace tile file by new tile.
            Files.move(tempTilePath, tilePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    /**
     * Find an existing tile in the tile matrix.
     */
    @Override
    public Tile anyTile() throws DataStoreException{
        String pattern = format.getPattern();
        int idx = pattern.lastIndexOf('.');
        final String suffix = pattern.substring(idx);

        final Path folderPath = toPath(folderUri);
        if (folderPath == null) {
            //TODO : find a better solution
            return getTile(0, 0);
        } else {
            //search for a tile file
            try (final Stream<Path> stream = Files.find(folderPath, 256, (Path t, BasicFileAttributes u) -> t.getFileName().toString().endsWith(suffix))) {
                final Optional<Path> first = stream.findFirst();
                if (first.isPresent()) {
                    return URITile.create(this, first.get().toUri(), security, new Point(0, 0));
                }
            } catch (IOException | DataStoreException ex) {
                //do nothing
            }
            throw new DataStoreException("No tiles in tile matrix");
        }
    }

    /**
     * This method could be synchronized but causes a lot of locks.
     * We are better of recomputing the value a few times in the worst case.
     *
     * @return Path if uri is on the filesytem, null otherwise
     */
    private Path toPath(URI uri) {
        //defensive copy
        final Boolean isOnFs = this.isOnFileSystem;
        if (isOnFs == null) {
            try {
                final Path path = Paths.get(uri);
                this.isOnFileSystem = Boolean.TRUE;
                return path;
            } catch (FileSystemNotFoundException | SecurityException | IllegalArgumentException ex) {
                return null;
            }
        }
        return isOnFs ? Paths.get(uri) : null;
    }
}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.postgresql;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.iharder.Base64;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.PyramidSet;
import org.geotoolkit.coverage.PyramidalModel;
import org.geotoolkit.coverage.PyramidalModelReader;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.coverage.postgresql.epsg.EPSGWriter;
import org.geotoolkit.coverage.postgresql.io.WKBRasterWriter;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.type.Name;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PGCoverageReference implements CoverageReference, PyramidalModel{

    private final PGCoverageStore store;
    private final PGPyramidSet pyramidSet;
    private final Name name;

    public PGCoverageReference(final PGCoverageStore store, final Name name) {
        this.store = store;
        this.name = name;
        this.pyramidSet = new PGPyramidSet(this);
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public int getImageIndex() {
        return 0;
    }

    @Override
    public boolean isWritable() throws DataStoreException {
        return true;
    }

    @Override
    public PGCoverageStore getStore() {
        return store;
    }

    @Override
    public GridCoverageReader createReader() throws DataStoreException {
        final PyramidalModelReader reader = new PyramidalModelReader();
        reader.setInput(this);
        return reader;
    }

    @Override
    public GridCoverageWriter createWriter() throws DataStoreException {
        throw new UnsupportedOperationException("Writer not available on pyramidal model.");
    }

    @Override
    public Image getLegend() throws DataStoreException {
        return null;
    }

    @Override
    public PyramidSet getPyramidSet() throws DataStoreException {
        return pyramidSet;
    }

    @Override
    public Pyramid createPyramid(final CoordinateReferenceSystem crs) throws DataStoreException {

        String pyramidId = "";

        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = store.getDataSource().getConnection();
            cnx.setReadOnly(false);
            //find or insert coordinate reference system
            final EPSGWriter writer = new EPSGWriter(store);
            final String epsgCode = String.valueOf(writer.getOrCreateCoordinateReferenceSystem(crs));

            stmt = cnx.createStatement();

            final int layerId = store.getLayerId(name.getLocalPart());

            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO ");
            query.append(store.encodeTableName("Pyramid"));
            query.append("(\"layerId\",\"epsg\") VALUES (");
            query.append(layerId);
            query.append(",'");
            query.append(epsgCode);
            query.append("')");

            stmt.executeUpdate(query.toString(), new String[]{"id"});

            rs = stmt.getGeneratedKeys();
            if(rs.next()){
                pyramidId = String.valueOf(rs.getInt(1));
            }
        }catch(FactoryException ex){
            throw new DataStoreException(ex);
        }catch(SQLException ex){
            throw new DataStoreException(ex);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }

        pyramidSet.mustUpdate();
        for(Pyramid p : pyramidSet.getPyramids()){
            if(p.getId().equals(pyramidId)){
                return p;
            }
        }

        //should not happen
        throw new DataStoreException("Generated pyramid not found.");
    }

    @Override
    public GridMosaic createMosaic(final String pyramidId, final Dimension gridSize, final Dimension tilePixelSize,
            final DirectPosition upperleft, final double pixelscale) throws DataStoreException {

        long mosaicId = 0;

        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = store.getDataSource().getConnection();
            cnx.setReadOnly(false);
            stmt = cnx.createStatement();

            final int pyramidIdInt = Integer.valueOf(pyramidId);

            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO ");
            query.append(store.encodeTableName("Mosaic"));
            query.append("(\"pyramidId\",\"upperCornerX\",\"upperCornerY\",\"gridWidth\",\"gridHeight\",\"scale\",\"tileWidth\",\"tileHeight\") VALUES (");
            query.append(pyramidIdInt           ).append(',');
            query.append(upperleft.getOrdinate(0)).append(',');
            query.append(upperleft.getOrdinate(1)).append(',');
            query.append(gridSize.width         ).append(',');
            query.append(gridSize.height        ).append(',');
            query.append(pixelscale             ).append(',');
            query.append(tilePixelSize.width    ).append(',');
            query.append(tilePixelSize.height   );
            query.append(")");

            stmt.executeUpdate(query.toString(), new String[]{"id"});

            rs = stmt.getGeneratedKeys();
            if(rs.next()){
                mosaicId = rs.getLong(1);
            }

            final CoordinateReferenceSystem crs = upperleft.getCoordinateReferenceSystem();
            final CoordinateSystem cs = crs.getCoordinateSystem();
            for(int i=2,n=cs.getDimension();i<n;i++){
                final double value = upperleft.getOrdinate(i);
                query = new StringBuilder();
                query.append("INSERT INTO ");
                query.append(store.encodeTableName("MosaicAxis"));
                query.append("(\"mosaicId\",\"indice\",\"value\") VALUES (");
                query.append(mosaicId).append(',');
                query.append(i).append(',');
                query.append(value);
                query.append(")");
                stmt.executeUpdate(query.toString());
            }

        }catch(SQLException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }

        pyramidSet.mustUpdate();
        for (final Pyramid p : pyramidSet.getPyramids()) {
            if (p.getId().equals(pyramidId)) {
                for(GridMosaic mosaic : p.getMosaics()){
                    if (((PGGridMosaic)mosaic).getDatabaseId() == mosaicId) {
                        return mosaic;
                    }
                }
            }
        }

        //should not happen
        throw new DataStoreException("Generated mosaic not found.");
    }

    @Override
    public void writeTiles(final String pyramidId, final String mosaicId,
            final RenderedImage image, final boolean onlyMissing) throws DataStoreException {
        final int offsetX = image.getMinTileX();
        final int offsetY = image.getMinTileY();

        final RejectedExecutionHandler rejectHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        final BlockingQueue queue = new ArrayBlockingQueue(Runtime.getRuntime().availableProcessors());
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            0, Runtime.getRuntime().availableProcessors(), 1, TimeUnit.MINUTES, queue, rejectHandler);

        for(int y=0; y<image.getNumYTiles();y++){
            for(int x=0;x<image.getNumXTiles();x++){
                final Raster raster = image.getTile(offsetX+x, offsetY+y);
                final RenderedImage img = new BufferedImage(image.getColorModel(),
                        (WritableRaster)raster, image.getColorModel().isAlphaPremultiplied(), null);

                final int tx = offsetX+x;
                final int ty = offsetY+y;

                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            writeTile(pyramidId, mosaicId, tx, ty, img);
                        } catch (DataStoreException ex) {
                            Logger.getLogger(PGCoverageReference.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

            }
        }
    }

    @Override
    public void writeTile(String pyramidId, String mosaicId, int col, int row, RenderedImage image) throws DataStoreException {

        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            final WKBRasterWriter writer = new WKBRasterWriter();
            final byte[] wkbimg = writer.write(image, null, 0);
            final String base64 = Base64.encodeBytes(wkbimg);

            final StringBuilder query = new StringBuilder();
            query.append("INSERT INTO");
            query.append(store.encodeTableName("Tile"));
            query.append("(\"raster\",\"mosaicId\",\"positionX\",\"positionY\") VALUES ( (");
            query.append("encode(").append("decode('").append(base64).append("','base64')").append(",'hex')").append(")::raster,");
            query.append(Integer.valueOf(mosaicId)).append(',');
            query.append(col).append(',');
            query.append(row).append(')');

            cnx = store.getDataSource().getConnection();
            cnx.setReadOnly(false);
            stmt = cnx.createStatement();

            stmt.executeUpdate(query.toString());


        }catch(IOException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }catch(SQLException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }

    }

}

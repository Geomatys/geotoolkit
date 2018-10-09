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
import java.awt.Point;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;
import net.iharder.Base64;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.wkb.WKBRasterWriter;
import org.geotoolkit.data.multires.AbstractMosaic;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.data.multires.Tile;
import org.geotoolkit.process.Monitor;
import org.geotoolkit.storage.coverage.CoverageStoreContentEvent;
import org.geotoolkit.storage.coverage.ImageTile;
import org.opengis.geometry.DirectPosition;

/**
 *
 * @author Johann Sorel
 */
public class PGGridMosaic extends AbstractMosaic{

    private final PGCoverageResource ref;
    private final long id;

    public PGGridMosaic(final PGCoverageResource ref, final long id,
            Pyramid pyramid, DirectPosition upperLeft, Dimension gridSize, Dimension tileSize, double scale) {
        super(String.valueOf(id),pyramid, upperLeft, gridSize, tileSize, scale);
        this.ref = ref;
        this.id = id;
    }

    @Override
    protected boolean isWritable() throws CoverageStoreException {
        return ref.isWritable();
    }

    @Override
    public boolean isMissing(int col, int row) {
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{

            cnx = ref.getStore().getDataSource().getConnection();
            stmt = cnx.createStatement();

            final long mosaicId = getDatabaseId();

            final StringBuilder query = new StringBuilder();
            query.append("SELECT count(raster) FROM ");
            query.append(ref.getStore().encodeTableName("Tile"));
            query.append(" WHERE \"mosaicId\"=").append(mosaicId);
            query.append(" AND \"positionX\"=").append(col);
            query.append(" AND \"positionY\"=").append(row);

            rs = stmt.executeQuery(query.toString());
            rs.next();
            return rs.getInt(1) <= 0;
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }finally{
            ref.getStore().closeSafe(cnx, stmt, rs);
        }
    }

    public long getDatabaseId() {
        return id;
    }

    public PGCoverageResource getCoverageReference() {
        return ref;
    }

    @Override
    public ImageTile getTile(int col, int row, Map hints) throws DataStoreException {
        final PGTileReference tile = new PGTileReference(this, new Point(col, row));
        return tile;
    }

    @Override
    public void writeTiles(Stream<Tile> tiles, final Monitor monitor) throws DataStoreException {

        try {
            tiles.parallel().forEach((Tile tile) -> {
                if (tile instanceof ImageTile) {
                    final ImageTile imgTile = (ImageTile) tile;
                    try {
                        writeTile(imgTile.getPosition(), imgTile.getImage());
                    } catch (IOException ex) {
                        throw new BackingStoreException(new DataStoreException(ex.getMessage(), ex));
                    } catch (DataStoreException ex) {
                        throw new BackingStoreException(ex);
                    }
                } else {
                    throw new BackingStoreException(new DataStoreException("Only ImageTile are supported."));
                }
            });
        } catch (BackingStoreException ex) {
            throw (DataStoreException) ex.getCause();
        }
    }

    public void writeTile(Point pt, RenderedImage image) throws DataStoreException {
        final int col = pt.x;
        final int row = pt.y;
        final PGCoverageStore pgstore = ref.getStore();
        Connection cnx = null;
        Statement insertStmt = null;
        Statement deleteStmt = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        try{
            StringBuilder query = new StringBuilder();
            query.append("SELECT id,\"mosaicId\",\"positionX\",\"positionY\" FROM ")
                 .append(pgstore.encodeTableName("Tile"))
                 .append(" WHERE \"mosaicId\"=").append(id)
                 .append(" AND \"positionX\"=").append(Integer.valueOf(col))
                 .append(" AND \"positionY\"=").append(Integer.valueOf(row));
            cnx = pgstore.getDataSource().getConnection();
            cnx.setReadOnly(false);
            selectStmt = cnx.createStatement();
            rs = selectStmt.executeQuery(query.toString());

            while (rs.next()) {
                final int id = rs.getInt("id");
                query = new StringBuilder();
                query.append("DELETE FROM ")
                     .append(pgstore.encodeTableName("Tile"))
                     .append(" WHERE id=").append(id);
                deleteStmt = cnx.createStatement();
                deleteStmt.executeUpdate(query.toString());
            }

            final WKBRasterWriter writer = new WKBRasterWriter();
            final byte[] wkbimg = writer.write(image, null, 0);
            final String base64 = Base64.encodeBytes(wkbimg);

            query = new StringBuilder();
            query.append("INSERT INTO");
            query.append(pgstore.encodeTableName("Tile"));
            query.append("(\"raster\",\"mosaicId\",\"positionX\",\"positionY\") VALUES ( (");
            query.append("encode(").append("decode('").append(base64).append("','base64')").append(",'hex')").append(")::raster,");
            query.append(id).append(',');
            query.append(col).append(',');
            query.append(row).append(')');

            insertStmt = cnx.createStatement();
            insertStmt.executeUpdate(query.toString());

            final CoverageStoreContentEvent event = ref.fireTileUpdated(getPyramid().getIdentifier(), getIdentifier(), Collections.singletonList(new Point(col,row)));
            pgstore.forwardEvent(event);
        }catch(IOException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }catch(SQLException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }finally{
            pgstore.closeSafe(cnx, selectStmt, rs);
            pgstore.closeSafe(insertStmt);
            pgstore.closeSafe(deleteStmt);
        }

    }

    @Override
    public void deleteTile(int col, int row) throws DataStoreException {
        final PGCoverageStore pgstore = ref.getStore();
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = pgstore.getDataSource().getConnection();
            cnx.setReadOnly(false);
            stmt = cnx.createStatement();
            final StringBuilder sql = new StringBuilder("DELETE FROM ");
            sql.append(pgstore.encodeTableName("Tile"));
            sql.append(" WHERE \"mosaicId\" = ").append(id);
            sql.append(" AND \"positionX\" = ").append(col);
            sql.append(" AND \"positionY\" = ").append(row);
            stmt.executeUpdate(sql.toString());
        }catch(SQLException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }finally{
            pgstore.closeSafe(cnx, stmt, rs);
            ref.mustUpdate();
        };
    }


}

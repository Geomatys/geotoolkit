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

import java.awt.Point;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import net.iharder.Base64;
import org.geotoolkit.coverage.TileReference;
import org.geotoolkit.coverage.postgresql.io.WKBRasterImageReader;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PGTileReference implements TileReference{

    private static final WKBRasterImageReader.Spi SPI = new WKBRasterImageReader.Spi();
    
    private final PGGridMosaic mosaic;
    private final Point position;
    private SoftReference<byte[]> data = null;

    public PGTileReference(final PGGridMosaic mosaic, final Point position) {
        this.mosaic = mosaic;
        this.position = position;
    }

    @Override
    public ImageReader getImageReader() throws IOException {
        final ImageReader reader = SPI.createReaderInstance();
        reader.setInput(getInput());
        return reader;
    }

    @Override
    public ImageReaderSpi getImageReaderSpi() {
        return SPI;
    }

    @Override
    public synchronized Object getInput() {
        final PGCoverageStore store = mosaic.getCoverageReference().getStore();
        byte[] buffer = null;
        if(data != null){
            buffer = data.get();
        }
        if(buffer == null){
            try {
                buffer = download();
                data = new SoftReference<byte[]>(buffer);
            } catch (SQLException ex) {
                store.getLogger().log(Level.WARNING, ex.getMessage(),ex);
            }
        }
        
        return buffer;
    }

    private byte[] download() throws SQLException{
        final PGCoverageStore store = mosaic.getCoverageReference().getStore();
            
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            
            cnx = store.getDataSource().getConnection();
            stmt = cnx.createStatement();
            
            final long mosaicId = mosaic.getDatabaseId();

            final StringBuilder query = new StringBuilder();
            query.append("SELECT encode(st_asbinary(\"raster\"),'base64') FROM ");
            query.append(store.encodeTableName("Tile"));
            query.append(" WHERE \"mosaicId\"=").append(mosaicId);
            query.append(" AND \"positionX\"=").append(position.x);
            query.append(" AND \"positionY\"=").append(position.y);
            
            rs = stmt.executeQuery(query.toString());
            
            if(rs.next()){
                byte[] data = rs.getBytes(1);
                try {
                    data = Base64.decode(data);
                } catch (IOException ex) {
                    throw new SQLException("Failed to uncompressed base64 : "+ex.getMessage(),ex);
                }
                return data;
            }
            
            throw new SQLException("No tile found for mosaic "+mosaicId +" and position "+position.x+"/"+position.y);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }
        
    }
    
    @Override
    public int getImageIndex() {
        return 0;
    }

    @Override
    public Point getPosition() {
        return position;
    }
    
}

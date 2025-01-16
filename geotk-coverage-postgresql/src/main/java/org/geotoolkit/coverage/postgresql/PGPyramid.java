/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.multires.AbstractPyramid;
import org.geotoolkit.data.multires.Mosaic;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.storage.coverage.CoverageStoreManagementEvent;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PGPyramid extends AbstractPyramid {

    private final PGCoverageResource ref;
    private final long id;
    final List<Mosaic> mosaics = new ArrayList<>();

    public PGPyramid(PGCoverageResource ref, long id, CoordinateReferenceSystem crs) {
        super(String.valueOf(id), crs);
        this.ref = ref;
        this.id = id;
    }

    @Override
    public Collection<? extends Mosaic> getMosaics() {
        return Collections.unmodifiableList(mosaics);
    }

    @Override
    public Mosaic createMosaic(Mosaic template) throws DataStoreException {
        final Dimension gridSize = template.getGridSize();
        final Dimension tilePixelSize = template.getTileSize();
        final DirectPosition upperleft = template.getUpperLeftCorner();
        final double pixelscale = template.getScale();

        final PGCoverageStore pgstore = ref.getOriginator();
        long mosaicId = 0;

        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = pgstore.getDataSource().getConnection();
            cnx.setReadOnly(false);
            stmt = cnx.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO ");
            query.append(pgstore.encodeTableName("Mosaic"));
            query.append("(\"pyramidId\",\"upperCornerX\",\"upperCornerY\",\"gridWidth\",\"gridHeight\",\"scale\",\"tileWidth\",\"tileHeight\") VALUES (");
            query.append(id           ).append(',');
            query.append(upperleft.getCoordinate(0)).append(',');
            query.append(upperleft.getCoordinate(1)).append(',');
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
                final double value = upperleft.getCoordinate(i);
                query = new StringBuilder();
                query.append("INSERT INTO ");
                query.append(pgstore.encodeTableName("MosaicAxis"));
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
            pgstore.closeSafe(cnx, stmt, rs);
        }

        ref.mustUpdate();
        final CoverageStoreManagementEvent event = ref.fireMosaicAdded(getIdentifier(), String.valueOf(mosaicId));
        pgstore.forwardEvent(event);
        for (final Pyramid p : ref.getPyramids()) {
            if (p.getIdentifier().equals(getIdentifier())) {
                for(Mosaic mosaic : p.getMosaics()){
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
    public void deleteMosaic(String mosaicId) throws DataStoreException {
        final PGCoverageStore pgstore = ref.getOriginator();

        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = pgstore.getDataSource().getConnection();
            cnx.setReadOnly(false);
            stmt = cnx.createStatement();
            final int mosaicdIdInt = Integer.valueOf(mosaicId);
            final StringBuilder sql = new StringBuilder("DELETE FROM ");
            sql.append(pgstore.encodeTableName("Mosaic"));
            sql.append(" WHERE id = ");
            sql.append(mosaicdIdInt);
            stmt.executeUpdate(sql.toString());
        }catch(SQLException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }finally{
            pgstore.closeSafe(cnx, stmt, rs);
            ref.mustUpdate();
        }
    }

}

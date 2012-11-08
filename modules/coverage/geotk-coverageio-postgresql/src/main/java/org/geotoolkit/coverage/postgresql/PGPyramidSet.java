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
import java.awt.geom.Point2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.geotoolkit.coverage.DefaultPyramid;
import org.geotoolkit.coverage.DefaultPyramidSet;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PGPyramidSet extends DefaultPyramidSet{
    
    private final PGCoverageReference ref;
    private boolean updated = false;

    public PGPyramidSet(PGCoverageReference ref) {
        this.ref = ref;
    }

    void mustUpdate(){
        updated = false;
    }
    
    @Override
    public Collection<Pyramid> getPyramids() {
        if(!updated){
            updateModel();
        }
        return super.getPyramids();
    }
    
    /**
     * Explore pyramids and rebuild model
     */
    private synchronized void updateModel(){
        if(updated) return;
        super.getPyramids().clear();
        final PGCoverageStore store = ref.getStore();
        
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = store.getDataSource().getConnection();
            stmt = cnx.createStatement();
            final int layerId = store.getLayerId(ref.getName().getLocalPart());
            
            final StringBuilder query = new StringBuilder();        
            query.append("SELECT p.id, p.crs FROM ");
            query.append(store.encodeTableName("Pyramid"));
            query.append(" as p ");
            query.append(" WHERE p.\"layerId\" = ");
            query.append(layerId);
            
            final Map<Integer,String> map = new HashMap<Integer, String>();
            rs = stmt.executeQuery(query.toString());
            while(rs.next()){
                map.put(rs.getInt(1),rs.getString(2));
            }
            store.closeSafe(null,stmt,rs);
            for(Entry<Integer,String> entry : map.entrySet()){
                super.getPyramids().add(readPyramid(cnx, entry.getKey(), entry.getValue()));
            }
            
        }catch(SQLException ex){
            store.getLogger().log(Level.WARNING, ex.getMessage(),ex);
        }catch(FactoryException ex){
            store.getLogger().log(Level.WARNING, ex.getMessage(),ex);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }
        
        updated = true;
    }
    
    private Pyramid readPyramid(final Connection cnx, final int pyramidId, final String wktcrs) throws SQLException, FactoryException{
        
        final CoordinateReferenceSystem crs = CRS.parseWKT(wktcrs);
        final DefaultPyramid pyramid = new DefaultPyramid(String.valueOf(pyramidId), this, crs);
        final PGCoverageStore store = ref.getStore();
        
        Statement stmt = null;
        ResultSet rs = null;
        try{
            stmt = cnx.createStatement();
            
            final StringBuilder query = new StringBuilder();        
            query.append("SELECT \"id\",\"upperCornerX\",\"upperCornerY\",\"gridWidth\",\"gridHeight\",\"scale\",\"tileWidth\",\"tileHeight\" FROM ");
            query.append(store.encodeTableName("Mosaic"));
            query.append(" WHERE \"pyramidId\" = ");
            query.append(pyramidId);
            
            rs = stmt.executeQuery(query.toString());
            while(rs.next()){
                final long mosaicId = rs.getLong(1);
                final double cornerX = rs.getDouble(2);
                final double cornerY = rs.getDouble(3);
                final int gridWidth = rs.getInt(4);
                final int gridHeight = rs.getInt(5);
                final double scale = rs.getDouble(6);
                final int tileWidth = rs.getInt(7);
                final int tileHeight = rs.getInt(8);
                
                final PGGridMosaic mosaic = new PGGridMosaic(ref, mosaicId, pyramid, 
                        new Point2D.Double(cornerX, cornerY), 
                        new Dimension(gridWidth, gridHeight),
                        new Dimension(tileWidth, tileHeight), scale);
                pyramid.getMosaics().put(scale, mosaic);
            }
            
        }catch(SQLException ex){
            store.getLogger().log(Level.WARNING, ex.getMessage(),ex);
        }finally{
            store.closeSafe(null, stmt, rs);
        }
        
        return pyramid;
    }
        
}

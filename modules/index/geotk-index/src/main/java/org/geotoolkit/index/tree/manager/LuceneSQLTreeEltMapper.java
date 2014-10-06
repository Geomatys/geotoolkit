/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.index.tree.manager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class LuceneSQLTreeEltMapper implements TreeElementMapper<NamedEnvelope> {

     /**
     * Mutual Coordinate Reference System from all stored NamedEnvelopes.
     */
    private final CoordinateReferenceSystem crs;

    private final DataSource source;

    private Connection conRO;
    private Connection conT;

    public LuceneSQLTreeEltMapper(final CoordinateReferenceSystem crs, final DataSource source) throws IOException {
        this.crs    = crs;
        this.source = source;
        try {
            this.conRO = source.getConnection();
            this.conRO.setReadOnly(true);

            this.conT  = source.getConnection();
            this.conT.setAutoCommit(false);
        } catch (SQLException ex) {
            throw new IOException("Error while trying to connect the treemap datasource", ex);
        }
    }


    @Override
    public int getTreeIdentifier(final NamedEnvelope env) throws IOException {
        int result = -1;
        try {
            final PreparedStatement stmt = conRO.prepareStatement("SELECT \"id\" FROM \"treemap\".\"records\" WHERE \"identifier\"=?");
            stmt.setString(1, env.getId());
            final ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            throw new IOException("Error while getting tree identifier for envelope", ex);
        }
        return result;
    }

    @Override
    public Envelope getEnvelope(final NamedEnvelope object) throws IOException {
        return object;
    }

    @Override
    public void setTreeIdentifier(final NamedEnvelope env, final int treeIdentifier) throws IOException {
        try {
            if (env != null) {
                final PreparedStatement existStmt = conRO.prepareStatement("SELECT \"id\" FROM \"treemap\".\"records\" WHERE \"id\"=?");
                existStmt.setInt(1, treeIdentifier);
                final ResultSet rs = existStmt.executeQuery();
                final boolean exist = rs.next();
                rs.close();
                existStmt.close();
                if (exist) {
                    final PreparedStatement stmt = conT.prepareStatement("UPDATE \"treemap\".\"records\" "
                                                                       + "SET \"identifier\"=?, \"nbenv\"=?, \"minx\"=?, \"maxx\"=?, \"miny\"=?, \"maxy\"=? "
                                                                       + "WHERE \"id\"=?");
                    stmt.setString(1, env.getId());
                    stmt.setInt(2, env.getNbEnv());
                    stmt.setDouble(3, env.getMinimum(0));
                    stmt.setDouble(4, env.getMaximum(0));
                    stmt.setDouble(5, env.getMinimum(1));
                    stmt.setDouble(6, env.getMaximum(1));

                    stmt.setInt(7, treeIdentifier);
                    stmt.executeUpdate();
                    stmt.close();
                    conT.commit();
                } else {
                    final PreparedStatement stmt = conT.prepareStatement("INSERT INTO \"treemap\".\"records\" "
                                                                       + "VALUES (?, ?, ?, ?, ?, ?, ?)");
                    stmt.setInt(1, treeIdentifier);
                    stmt.setString(2, env.getId());
                    stmt.setInt(3, env.getNbEnv());
                    stmt.setDouble(4, env.getMinimum(0));
                    stmt.setDouble(5, env.getMaximum(0));
                    stmt.setDouble(6, env.getMinimum(1));
                    stmt.setDouble(7, env.getMaximum(1));

                    stmt.executeUpdate();
                    stmt.close();
                    conT.commit();
                }
            } else {
                final PreparedStatement remove = conT.prepareStatement("DELETE FROM \"treemap\".\"records\" WHERE \"id\"=?");
                remove.setInt(1, treeIdentifier);
                remove.executeUpdate();
                remove.close();
                conT.commit();
            }
        } catch (SQLException ex) {
            throw new IOException("Error while getting tree identifier for envelope", ex);
        }
    }

    @Override
    public NamedEnvelope getObjectFromTreeIdentifier(final int treeIdentifier) throws IOException {
        NamedEnvelope result = null;
        try {
            final PreparedStatement stmt = conRO.prepareStatement("SELECT * FROM \"treemap\".\"records\" WHERE \"id\"=?");
            stmt.setInt(1, treeIdentifier);
            final ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                final String identifier = rs.getString("identifier");
                final int nbEnv         = rs.getInt("nbenv");
                final double minx       = rs.getDouble("minx");
                final double maxx       = rs.getDouble("maxx");
                final double miny       = rs.getDouble("miny");
                final double maxy       = rs.getDouble("maxy");
                result = new NamedEnvelope(crs, identifier, nbEnv);
                result.setRange(0, minx, maxx);
                result.setRange(1, miny, maxy);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            throw new IOException("Error while getting envelope", ex);
        }
        return result;
    }

    @Override
    public void clear() throws IOException {
        try {
            final PreparedStatement stmt = conT.prepareStatement("DELETE FROM \"treemap\".\"records\"");
            stmt.executeUpdate();
            stmt.close();
            conT.commit();
        } catch (SQLException ex) {
            throw new IOException("Error while removing all records", ex);
        }
    }

    @Override
    public void flush() throws IOException {
        // do nothing in this implementation
    }

    @Override
    public void close() throws IOException {
        if (conRO != null) try {
            conRO.close();
            conT.close();
            conRO = null;
            conT  = null;
            if (source instanceof DefaultDataSource) {
                ((DefaultDataSource)source).shutdown();
            }
        } catch (SQLException ex) {
            throw new IOException("SQL exception while closing SQL tree mapper", ex);
        }
    }

    @Override
    public boolean isClosed() {
        return conRO == null;
    }

}

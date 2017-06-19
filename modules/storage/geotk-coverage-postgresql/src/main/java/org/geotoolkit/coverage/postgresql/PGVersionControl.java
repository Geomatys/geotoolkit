/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.Pyramid;
import org.geotoolkit.temporal.object.ISODateParser;
import org.geotoolkit.version.AbstractVersionControl;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersioningException;
import org.opengis.util.GenericName;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;

/**
 * Postgresql version control.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PGVersionControl extends AbstractVersionControl{

    public static TimeZone GMT0 = TimeZone.getTimeZone("GMT+0");
    /**
     * Version label for pyramid without version informations.
     */
    public static final String UNSET = "unset";

    private final PGCoverageStore store;
    private final GenericName name;

    public PGVersionControl(PGCoverageStore store, GenericName name) {
        this.store = store;
        this.name = name;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public boolean isAutomatic() {
        return false;
    }

    @Override
    public boolean isVersioned() throws VersioningException {
        return true;
    }

    @Override
    public Version createVersion(Date date) throws VersioningException {
        final Timestamp ts = new Timestamp(date.getTime());
        return new Version(this, ts.toString(), ts);
    }

    @Override
    public void dropVersion(Version version) throws VersioningException {
        try {
            final PyramidalCoverageResource ref = (PyramidalCoverageResource) store.getCoverageReference(name, version);
            final Collection<Pyramid> pyramids = ref.getPyramidSet().getPyramids();
            for(Pyramid p : pyramids){
                ref.deletePyramid(p.getId());
            }
        } catch (DataStoreException ex) {
            throw new VersioningException(ex.getMessage(), ex);
        }

    }

    @Override
    public List<Version> list() throws VersioningException {
        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT distinct(pp.value) FROM ");
        sql.append(store.encodeTableName("Layer"));
        sql.append(" AS l ");
        sql.append("INNER JOIN ");
        sql.append(store.encodeTableName("Pyramid"));
        sql.append(" AS p ON p.\"layerId\" = l.id ");
        sql.append("LEFT OUTER JOIN ");
        sql.append(store.encodeTableName("PyramidProperty"));
        sql.append(" AS pp ON pp.\"pyramidId\" = p.id AND pp.key = 'version' ");
        sql.append("WHERE l.name = '");
        sql.append(name.tip()).append('\'');

        final ISODateParser dateparser = new ISODateParser();
        final List<Date> dates = new ArrayList<>();
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        final Date unset = new Date();
        try{
            cnx = store.getDataSource().getConnection();
            stmt = cnx.createStatement();
            rs = stmt.executeQuery(sql.toString());
            while(rs.next()){
                final String dateiso = rs.getString(1);
                if(dateiso == null || dateiso.isEmpty()){
                    dates.add(unset);
                }else{
                    dates.add(dateparser.parseToDate(dateiso));
                }
            }
        }catch(SQLException ex){
            throw new VersioningException(ex.getMessage(), ex);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }

        Collections.sort(dates);
        final List<Version> versions = new ArrayList<Version>(dates.size());
        for(Date d : dates){
            if(d == unset){
                versions.add(new Version(this, UNSET, d));
            }else{
                versions.add(new Version(this, d.toString(), d));
            }
        }
        return versions;
    }

    @Override
    public void trim(Version version) throws VersioningException {
        super.trim(version); //TODO
    }

    @Override
    public void revert(Version version) throws VersioningException {
        super.revert(version); //TODO
    }

}

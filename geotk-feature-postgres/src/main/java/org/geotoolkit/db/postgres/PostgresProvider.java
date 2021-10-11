/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2013, Geomatys
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
package org.geotoolkit.db.postgres;

import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.db.AbstractJDBCProvider;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.geotoolkit.db.JDBCFeatureStore;
import org.geotoolkit.db.dialect.SQLDialect;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * PostgreSQL/PostGIS  feature store factory.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@StoreMetadata(
        formatName = PostgresProvider.NAME,
        capabilities = {Capability.READ, Capability.WRITE},
        resourceTypes = {FeatureSet.class})
@StoreMetadataExt(
        resourceTypes = ResourceType.VECTOR,
        geometryTypes ={Geometry.class,
                        Point.class,
                        LineString.class,
                        Polygon.class,
                        MultiPoint.class,
                        MultiLineString.class,
                        MultiPolygon.class})
public class PostgresProvider extends AbstractJDBCProvider{

    /** factory identification **/
    public static final String NAME = "postgresql";

    /**
     * Parameter for loose bbox filter.
     */
    public static final ParameterDescriptor<Boolean> LOOSEBBOX = new ParameterBuilder()
            .addName("Loose bbox")
            .addName(Bundle.formatInternational(Bundle.Keys.lbbox))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.lbbox_remarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.TRUE);

    /**
     * Parameter for database port.
     */
    public static final ParameterDescriptor<Integer> PORT = createFixedPort(5432);


    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName(NAME).addName("PostgresParameters").createGroup(
                HOST,PORT,DATABASE,SCHEMA,TABLE,USER,PASSWORD,
                DATASOURCE,MAXCONN,MINCONN,VALIDATECONN,FETCHSIZE,MAXWAIT,LOOSEBBOX,SIMPLETYPE);

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    protected String getJDBCURLDatabaseName() {
        return "postgresql";
    }

    @Override
    protected String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    protected SQLDialect createSQLDialect(JDBCFeatureStore dataStore) {
        return new PostgresDialect((DefaultJDBCFeatureStore)dataStore);
    }

    @Override
    protected String getValidationQuery() {
        return "select now()";
    }

    @Override
    protected DefaultJDBCFeatureStore toFeatureStore(ParameterValueGroup params, String factoryId) {
        //add versioning support
        return new PostgresStore(params, factoryId);
    }

}

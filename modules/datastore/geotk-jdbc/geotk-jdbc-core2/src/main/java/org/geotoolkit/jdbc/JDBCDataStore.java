/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) Geomatys
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
package org.geotoolkit.jdbc;

import org.geotoolkit.jdbc.dialect.SQLDialect;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.util.logging.Logger;
import javax.sql.DataSource;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.jdbc.FilterToSQL;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.opengis.feature.FeatureFactory;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.filter.FilterFactory;
import org.opengis.geometry.Envelope;


/**
 * @author Johann Sorel (Geomatys)
 *
 * @module pending
 */
public interface JDBCDataStore extends DataStore {


    /**
     * The native SRID associated to a certain descriptor
     */
    public static final String JDBC_NATIVE_SRID = "nativeSRID";
    /**
     * name of table to use to store geometries when {@link #associations}
     * is set.
     */
    public static final String GEOMETRY_TABLE = "geometry";
    /**
     * name of table to use to store multi geometries made up of non-multi
     * geometries when {@link #associations} is set.
     */
    public static final String MULTI_GEOMETRY_TABLE = "multi_geometry";
    /**
     * name of table to use to store geometry associations when {@link #associations}
     * is set.
     */
    public static final String GEOMETRY_ASSOCIATION_TABLE = "geometry_associations";
    /**
     * name of table to use to store feature relationships (information about
     * associations) when {@link #associations} is set.
     */
    public static final String FEATURE_RELATIONSHIP_TABLE = "feature_relationships";
    /**
     * name of table to use to store feature associations when {@link #associations}
     * is set.
     */
    public static final String FEATURE_ASSOCIATION_TABLE = "feature_associations";
    /**
     * The envelope returned when bounds is called against a geometryless feature type
     */
    public static final Envelope EMPTY_ENVELOPE = new JTSEnvelope2D();

    PrimaryKey getPrimaryKey(FeatureType type) throws DataStoreException;

    /**
     * The factory used to create feature types.
     */
    FeatureTypeFactory getFeatureTypeFactory();

    /**
     * Sets the factory used to create feature types.
     */
    void setFeatureTypeFactory(final FeatureTypeFactory typeFactory);

    /**
     * Sets the factory used to create features.
     */
    void setFeatureFactory(final FeatureFactory featureFactory);

    /**
     * The factory used to create filters.
     */
    FilterFactory getFilterFactory();

    /**
     * The factory used to create features.
     */
    FeatureFactory getFeatureFactory();

    /**
     * Sets the factory used to create filters.
     */
    void setFilterFactory(final FilterFactory filterFactory);

    /**
     * The factory used to create geometries.
     */
    GeometryFactory getGeometryFactory();

    /**
     * Sets the factory used to create geometries.
     */
    void setGeometryFactory(final GeometryFactory geometryFactory);

    /**
     * The namespace uri of the datastore.
     *
     * @return The namespace uri, may be <code>null</code>.
     */
    String getNamespaceURI();

    /**
     * The logger for the datastore.
     */
    Logger getLogger();

    void setDialect(SQLDialect dialect);

    SQLDialect getDialect();

    void setDatabaseSchema(String databaseSchema);

    String getDatabaseSchema();

    void setDataSource(DataSource source);

    DataSource getDataSource();

    /**
     * The current fetch size. The fetch size influences how many records are read from the
     * dbms at a time. If set to a value less or equal than zero, all the records will be
     * read in one shot, severily increasing the memory requirements to read a big number
     * of features.
     * @return int size
     */
    int getFetchSize();

    /**
     * Changes the fetch size.
     * @param fetchSize
     */
    void setFetchSize(final int fetchSize);

    /**
     * Creates a new instance of a filter to sql encoder.
     * <p>
     * The <tt>featureType</tt> may be null but it is not recommended. Such a
     * case where this may neccessary is when a literal needs to be encoded in
     * isolation.
     * </p>
     */
    FilterToSQL createFilterToSQL(final FeatureType featureType);

}

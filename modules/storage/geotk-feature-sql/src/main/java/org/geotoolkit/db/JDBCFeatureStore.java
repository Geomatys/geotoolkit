/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2014, Geomatys
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
package org.geotoolkit.db;

import java.awt.RenderingHints;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.db.dialect.SQLDialect;
import org.geotoolkit.db.reverse.DataBaseModel;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class JDBCFeatureStore extends AbstractFeatureStore{
    
    public static final RenderingHints.Key RESAMPLING = new org.geotoolkit.factory.Hints.Key(Object.class);
    
    /**
     * Query language supported : SQL.
     */
    public static final String CUSTOM_SQL = "CUSTOM-SQL";
    
    /**
     * Property information, The native SRID associated to a certain descriptor.
     * Value is an Integer.
     */
    public static final String JDBC_PROPERTY_SRID = "nativeSRID";
    
    /**
     * Property information, if the field is unique in the database.
     * Value is a Boolean.
     */
    public static final String JDBC_PROPERTY_UNIQUE = "unique";
    
    /**
     * Property information, if the field is a relation.
     * Value is a RelationMetaModel.
     */
    public static final String JDBC_PROPERTY_RELATION = "relation";

    public JDBCFeatureStore(ParameterValueGroup params) {
        super(params);
    }
          
    /**
     * Each database type have slim deformation from the SQL specification.
     * The dialect object provide informations on those changes.
     * 
     * @return SQLDialect, never null
     */
    public abstract SQLDialect getDialect();
    
    /**
     * Source object providing connexions to the database.
     * 
     * @return DataSource, never null
     */
    public abstract DataSource getDataSource();
    
    /**
     * @return the database schema used, if any.
     */
    public abstract String getDatabaseSchema();
    
    /**
     * @return the database model.
     */
    public abstract DataBaseModel getDatabaseModel();        
    
    /**
     * @return logger used by this featurestore.
     */
    @Override
    public Logger getLogger(){
        return super.getLogger();
    }
    
    /**
     * @return the database default namespace.
     */
    @Override
    public String getDefaultNamespace(){
        return super.getDefaultNamespace();
    }
    
    /**
     * Returns the select query fetch size.
     * Using a high value will require more memory but improve the overall performance.
     * @return int sql fetch size
     */
    public abstract int getFetchSize();
    
}

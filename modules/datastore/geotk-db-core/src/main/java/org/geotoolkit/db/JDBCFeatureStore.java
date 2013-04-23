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
package org.geotoolkit.db;

import java.util.logging.Logger;
import javax.sql.DataSource;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.db.dialect.SQLDialect;
import org.geotoolkit.db.reverse.DataBaseModel;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface JDBCFeatureStore extends FeatureStore{
        
    /**
     * Each database type have slim deformation from the SQL specification.
     * The dialect object provide informations on those changes.
     * 
     * @return SQLDialect, never null
     */
    SQLDialect getDialect();
    
    /**
     * Source object providing connexions to the database.
     * 
     * @return DataSource, never null
     */
    DataSource getDataSource();
    
    /**
     * @return the database schema used, if any.
     */
    String getDatabaseSchema();
    
    /**
     * @return the database model.
     */
    DataBaseModel getDatabaseModel();        
    
    /**
     * @return logger used by this featurestore.
     */
    Logger getLogger();
    
    /**
     * @return the database default namespace.
     */
    String getDefaultNamespace();
    
}

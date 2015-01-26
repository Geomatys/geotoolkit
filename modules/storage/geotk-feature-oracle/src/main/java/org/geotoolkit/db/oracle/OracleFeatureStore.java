/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.db.oracle;

import javax.sql.DataSource;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.geotoolkit.parameter.Parameters;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class OracleFeatureStore extends DefaultJDBCFeatureStore {

    public OracleFeatureStore(String host, int port, String database, String schema, String user, String password) throws DataStoreException {
        super(toParameters(host,port,database,schema,user,password), OracleFeatureStoreFactory.NAME);
        ((OracleFeatureStoreFactory)getFactory()).prepareStore(this, parameters);
    }

    public OracleFeatureStore(ParameterValueGroup params, String factoryId) {
        super(params, factoryId);
    }

    private static ParameterValueGroup toParameters(String host, int port,
            String database, String schema, String user, String password){
        final ParameterValueGroup params = OracleFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(OracleFeatureStoreFactory.HOST,    params).setValue(host);
        Parameters.getOrCreate(OracleFeatureStoreFactory.PORT,    params).setValue(port);
        Parameters.getOrCreate(OracleFeatureStoreFactory.DATABASE,params).setValue(database);
        Parameters.getOrCreate(OracleFeatureStoreFactory.SCHEMA,  params).setValue(schema);
        Parameters.getOrCreate(OracleFeatureStoreFactory.USER,    params).setValue(user);
        Parameters.getOrCreate(OracleFeatureStoreFactory.PASSWORD,params).setValue(password);
        return params;
    }

    @Override
    public void setDataSource(DataSource ds) {
        super.setDataSource(ds);
    }

}

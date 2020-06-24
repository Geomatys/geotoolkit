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
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class OracleStore extends DefaultJDBCFeatureStore {

    public OracleStore(String host, int port, String database, String schema, String user, String password) throws DataStoreException {
        super(toParameters(host,port,database,schema,user,password), OracleProvider.NAME);
        ((OracleProvider)getProvider()).prepareStore(this, parameters);
    }

    public OracleStore(ParameterValueGroup params, String factoryId) {
        super(params, factoryId);
    }

    private static ParameterValueGroup toParameters(String host, int port,
            String database, String schema, String user, String password){
        final Parameters params = Parameters.castOrWrap(OracleProvider.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(OracleProvider.HOST).setValue(host);
        params.getOrCreate(OracleProvider.PORT).setValue(port);
        params.getOrCreate(OracleProvider.DATABASE).setValue(database);
        params.getOrCreate(OracleProvider.SCHEMA).setValue(schema);
        params.getOrCreate(OracleProvider.USER).setValue(user);
        params.getOrCreate(OracleProvider.PASSWORD).setValue(password);
        return params;
    }

    @Override
    public void setDataSource(DataSource ds) {
        super.setDataSource(ds);
    }

}

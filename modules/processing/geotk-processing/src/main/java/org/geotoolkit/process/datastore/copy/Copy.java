/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.datastore.copy;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;
import org.opengis.parameter.ParameterValueGroup;

import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.storage.DataStoreException;

import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.filter.DefaultPropertyName;
import org.geotoolkit.filter.visitor.DuplicatingFilterVisitor;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersioningException;

import static org.geotoolkit.parameter.Parameters.*;
import static org.geotoolkit.process.datastore.copy.CopyDescriptor.*;

/**
 * Copy feature from one datastore to another.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class Copy extends AbstractProcess {

    private static Logger LOGGER = Logging.getLogger(Copy.class);

    /**
     * Default constructor
     */
    public Copy(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {

        final FeatureStore sourceDS = value(SOURCE_STORE, inputParameters);
        final FeatureStore targetDS = value(TARGET_STORE, inputParameters);
        Session targetSS = value(TARGET_SESSION, inputParameters);
        final Boolean eraseParam    = value(ERASE,        inputParameters);
        final Boolean newVersion    = value(NEW_VERSION,  inputParameters);
        // Type name can be removed, it's embedded in the query param.
        final String typenameParam  = value(TYPE_NAME,    inputParameters);
        final Query queryParam      = value(QUERY, inputParameters);

        final boolean doCommit = targetSS == null;
        
        final Session sourceSS = sourceDS.createSession(false);
        if (targetSS == null) {
            if (targetDS != null) {
                targetSS = targetDS.createSession(true);
            } else {
                throw new ProcessException("Input target_session or target_datastore missing.", this, null);
            }
        } 

        boolean reBuildQuery = false;

        final String queryName;
        if (queryParam != null) {
            queryName = queryParam.getTypeName().getLocalPart();
            reBuildQuery = true;
        } else if (typenameParam != null) {
            queryName = typenameParam;
        } else {
            queryName = "*";
        }

        final Set<Name> names;
        if ("*".equals(queryName)) {
            //all values
            try {
                names = sourceDS.getNames();
            } catch (DataStoreException ex) {
                throw new ProcessException(ex.getMessage(), this, ex);
            }
        } else {
            //pick only the wanted names
            names = new HashSet<>();
            final List<String> wanted = UnmodifiableArrayList.wrap(queryName.split(","));
            for(String s : wanted) {
                try{
                    final FeatureType type = sourceDS.getFeatureType(s);
                    names.add(type.getName());
                } catch (DataStoreException ex) {
                    throw new ProcessException(ex.getMessage(), this, ex);
                }
            }
        }

        final float size = names.size();
        int inc = 0;
        for (Name n : names) {
            fireProgressing("Copying "+n+".", (int)((inc*100f)/size), false);
            try {

                Query query;
                if (reBuildQuery) {
                    QueryBuilder builder = new QueryBuilder(queryParam);
                    builder.setTypeName(n);
                    query = builder.buildQuery();
                } else {
                    query = queryParam != null ? queryParam : QueryBuilder.all(n);
                }

                insert(n, sourceSS, targetSS, query, eraseParam, newVersion);
            } catch (DataStoreException ex) {
                throw new ProcessException(ex.getMessage(), this, ex);
            }
            inc++;
        }

        try {
            
            Date lastVersionDate = null;
            if (doCommit) {
                LOGGER.log(Level.INFO, "Commit all changes");
                targetSS.commit();
                
                //find last version
                for (Name n : names) {
                    if(targetSS.getFeatureStore().getQueryCapabilities().handleVersioning()) {
                        final List<Version> versions = targetSS.getFeatureStore().getVersioning(n).list();
                        if (!versions.isEmpty()) {
                            if (lastVersionDate == null || versions.get(versions.size()-1).getDate().getTime() > lastVersionDate.getTime()) {
                                lastVersionDate = versions.get(versions.size()-1).getDate();
                            }
                        }
                    }
                }
            }

            if(lastVersionDate != null) {
                Parameters.getOrCreate(VERSION, outputParameters).setValue(lastVersionDate);
            }

        } catch (DataStoreException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        } catch (VersioningException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

    private void insert(Name name, final Session sourceSS, final Session targetSS, Query query,
                        final boolean erase, final boolean newVersion) throws DataStoreException{

        FeatureType type = sourceSS.getFeatureStore().getFeatureType(name);

        //Change * to featureType default geometry name
        if (query != null && query.getFilter() != null) {
            final Filter newFilter = (Filter) query.getFilter().accept(new BBOXFilterVisitor(), type);
            final QueryBuilder builder = new QueryBuilder(query);
            builder.setFilter(newFilter);
            query = builder.buildQuery();
        }
        final FeatureCollection collection = sourceSS.getFeatureCollection(query);
        
        //get the real FeatureType of collection (in case of reprojection, CRS is different).
        type = collection.getFeatureType();
        
        if(targetSS.getFeatureStore().getNames().contains(name)) {
            //ERASE
            if(erase) {
                targetSS.getFeatureStore().deleteFeatureType(name);
                targetSS.getFeatureStore().createFeatureType(name, type);
            }
        }else{
            targetSS.getFeatureStore().createFeatureType(name, type);
        }

        //get the created name, namespace might change
        name = targetSS.getFeatureStore().getFeatureType(type.getName().getLocalPart()).getName();

        if (targetSS.getFeatureStore().getQueryCapabilities().handleVersioning()) {
            try {
                targetSS.getFeatureStore().getVersioning(name).startVersioning();
            } catch (VersioningException ex) {
                throw new DataStoreException(ex.getLocalizedMessage(), ex);
            }
        }

        //NEW VERSION (remove old features)
        if (newVersion) {
            targetSS.removeFeatures(name, QueryBuilder.all(name).getFilter());
        }

        //Logging
        final StringBuilder logMsg = new StringBuilder("Insert ");
        logMsg.append(collection.size()).append(" features ");
        logMsg.append("in type ").append(name.getLocalPart());
        logMsg.append(" [");
        if (erase) {
            logMsg.append("ERASE");
            if (newVersion) logMsg.append(", NEWVERSION");
        }
        if (newVersion) logMsg.append("NEWVERSION");
        logMsg.append("]");
        LOGGER.log(Level.INFO, logMsg.toString());
        //APPEND
        targetSS.addFeatures(name, collection);
    }

    /**
     * Override BBox filters if property name equals to * to set name form
     * default geometry name in given FeatureType.
     */
    private class BBOXFilterVisitor extends DuplicatingFilterVisitor {
        @Override
        public Object visit(PropertyName expression, Object extraData) {
            if ("*".equals(expression.getPropertyName()) && extraData instanceof FeatureType) {
                return new DefaultPropertyName(((FeatureType)extraData).getGeometryDescriptor().getType().getName().getLocalPart());
            }
            return super.visit(expression, extraData);
        }

    }
}

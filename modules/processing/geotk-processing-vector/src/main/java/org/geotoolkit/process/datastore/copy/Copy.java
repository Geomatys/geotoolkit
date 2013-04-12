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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.memory.WrapFeatureCollection;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import static org.geotoolkit.parameter.Parameters.*;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import static org.geotoolkit.process.datastore.copy.CopyDescriptor.*;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Copy feature from one datastore to another.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class Copy extends AbstractProcess {

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

        fireProcessStarted("Starting copy.");

        final FeatureStore sourceDS    = value(SOURCE_STORE, inputParameters);
        final FeatureStore targetDS    = value(TARGET_STORE, inputParameters);
        final Boolean eraseParam    = value(ERASE,        inputParameters);
        final String typenameParam  = value(TYPE_NAME,    inputParameters);
        final Query queryParam      = value(QUERY,        inputParameters);

        final Set<Name> names;
        if (queryParam != null) {
            //if a query is given, ignore type name parameter
            try {
                insert(queryParam, sourceDS, targetDS, eraseParam);
            } catch (DataStoreException ex) {
                throw new ProcessException(ex.getMessage(), this, ex);
            }
            return;
        }

        if ("*".equals(typenameParam)) {
            //all values
            try {
                names = sourceDS.getNames();
            } catch (DataStoreException ex) {
                throw new ProcessException(ex.getMessage(), this, ex);
            }
        } else {
            //pick only the wanted names
            names = new HashSet<Name>();
            for(String s : typenameParam.split(",")) {
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
            fireProgressing("Copying "+n.getLocalPart()+".", (int)((inc*100f)/size), false);
            try {
                insert(n, sourceDS, targetDS, eraseParam);
            } catch (DataStoreException ex) {
                throw new ProcessException(ex.getMessage(), this, ex);
            }
            inc++;
        }

        fireProcessCompleted("Copy successful.");
    }

    private void insert(final Name name, final FeatureStore source, final FeatureStore target, final boolean erase) throws DataStoreException{

        final FeatureType type = source.getFeatureType(name);
        final Session session = source.createSession(false);
        final FeatureCollection collection = session.getFeatureCollection(QueryBuilder.all(name));

        final FeatureCollection wrap = new WrapFeatureCollection(collection) {

            long count = 0;
            @Override
            protected Feature modify(Feature original) throws FeatureStoreRuntimeException {
                fireProgressing(name.getLocalPart()+":"+(count++),0, false);
                return original;
            }
        };


        if(target.getNames().contains(name)) {
            if(erase) {
                target.deleteSchema(name);
                target.createSchema(name, type);
            }
        }else{
            target.createSchema(name, type);
        }

        final Hints hints = new Hints();
        hints.put(HintsPending.UPDATE_ID_ON_INSERT, Boolean.FALSE);
        target.addFeatures(name, wrap, hints);

    }

    private void insert(final Query query, final FeatureStore source, final FeatureStore target, final boolean erase) throws DataStoreException{

        final Name name = query.getTypeName();
        final FeatureType type = source.getFeatureType(name);
        final Session session = source.createSession(false);
        final FeatureCollection collection = session.getFeatureCollection(query);

        if(target.getNames().contains(name)) {
            if(erase) {
                target.deleteSchema(name);
                target.createSchema(name, type);
            }
        }else{
            target.createSchema(name, type);
        }

        final Hints hints = new Hints();
        hints.put(HintsPending.UPDATE_ID_ON_INSERT, Boolean.FALSE);
        target.addFeatures(name, collection, hints);
    }

}

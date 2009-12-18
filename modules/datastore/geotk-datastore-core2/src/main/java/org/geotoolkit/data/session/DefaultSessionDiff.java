/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.data.session;

import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreException;

/**
 * Contain a list of all modification, ensure concurrency when accesing
 * deltas and lock when commiting or reverting changes.
 *
 * @todo must be concurrent
 * @author Johann Sorel (Geomatys)
 */
public class DefaultSessionDiff{

    private final List<Delta> deltas = new ArrayList<Delta>();

    /**
     * {@inheritDoc }
     */
    public Delta[] getDeltas() {
        return deltas.toArray(new Delta[deltas.size()]);
    }

    public void add(Delta alt){
        deltas.add(alt);
    }

    public void commit(DataStore store) throws DataStoreException{
        //todo : must lock on the diff to avoid sync issues
        for(final Delta alt : deltas){
            alt.commit(store);
            alt.dispose();
            //todo must remove the alteration
        }

        deltas.clear();
    }

    public void rollback(){
        deltas.clear();
    }

    public void remove(Delta alt){
        deltas.remove(alt);
    }

}

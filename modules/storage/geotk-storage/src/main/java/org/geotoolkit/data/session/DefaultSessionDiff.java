/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.geotoolkit.data.FeatureStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.internal.util.UnmodifiableArrayList;

/**
 * Contain a list of all modification, ensure concurrency when accesing
 * deltas and lock when commiting or reverting changes.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultSessionDiff{

    private final List<Delta> deltas = new ArrayList<Delta>();
    private List<Delta> readCopy = null;

    private final ReadWriteLock rwlock = new ReentrantReadWriteLock();
    private final Lock readLock = rwlock.readLock();
    private final Lock writeLock = rwlock.writeLock();


    public DefaultSessionDiff(){
    }

    /**
     * {@inheritDoc }
     */
    public List<Delta> getDeltas() {
        readLock.lock();
        try{
            List<Delta> cp = readCopy;
            if(cp != null){
                return cp;
            }
        }finally{
            readLock.unlock();
        }

        /*
         * Double-check: was a deprecated practice before Java 5.
         * Is okay since Java 5 provided that the readCopy field
         * is protected by the readlock.
         */
        writeLock.lock();
        try{
            List<Delta> cp = readCopy;
            if(cp == null){
                cp = UnmodifiableArrayList.wrap(deltas.toArray(new Delta[deltas.size()]));
                readCopy = cp;
            }
            return cp;
        }finally{
            writeLock.unlock();
        }
    }

    public void add(final Delta alt){
        writeLock.lock();
        try{
            deltas.add(alt);
            readCopy = null;
        }finally{
            writeLock.unlock();
        }
    }

    public void commit(final FeatureStore store) throws DataStoreException{
        writeLock.lock();
        try{
            for(int i=0,n=deltas.size();i<n;i++){
                final Delta alt = deltas.get(i);
                final Map<String,String> updates = alt.commit(store);
                alt.dispose();

                //update next deltas
                if(updates != null){
                    for(int j=i+1;j<n;j++){
                        final Delta next = deltas.get(j);
                        next.update(updates);
                    }
                }
            }
            deltas.clear();
            readCopy = null;
        }finally{
            writeLock.unlock();
        }
    }

    public void rollback(){
        writeLock.lock();
        try{
            deltas.clear();
            readCopy = null;
        }finally{
            writeLock.unlock();
        }
    }

}

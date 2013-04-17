/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.s57.model;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.geotoolkit.data.iso8211.DataRecord;
import org.geotoolkit.data.s57.S57Constants;
import org.geotoolkit.storage.DataStoreException;

/**
 * S57 Object reader which merge a set of updates on the fly.
 * 
 * Not thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class S57UpdatedReader extends S57ObjectReader{
    
    private final List<File> updateFiles = new ArrayList<File>();
    //contains updates of type Deletion or Update
    private List<List<Entry<Pointer,S57Object>>> updateCells;
    //contains updates of type Insertion
    private List<List<S57Object>> newCells; 
    
    /**
     * Read all update files, store the update sequences in updateCells variable.
     * @throws DataStoreException , if reading an update failed.
     */
    private void loadUpdates() throws IOException{
        if(updateCells!=null) return;
        
        updateCells = new ArrayList<List<Entry<Pointer,S57Object>>>();
        newCells = new ArrayList<List<S57Object>>();
        
        //sort files based on name
        Collections.sort(updateFiles);
        
        //read each file and store the update sequences
        S57ObjectReader reader = null;
        for(File f : updateFiles){
            try{
                reader = new S57ObjectReader();
                reader.setInput(f);
                reader.setPredicate(predicate); //reuse the same predicate                
                final List<Entry<Pointer,S57Object>> updateSequence = new ArrayList<Entry<Pointer, S57Object>>();
                final List<S57Object> newSequence = new ArrayList<S57Object>();
                while(reader.hasNext()){
                    final S57Object update = reader.next();
                    final Pointer pointer = update.generatePointer();
                    if(update instanceof VectorRecord){
                        final VectorRecord vr = new VectorRecord();
                        if(vr.updateInstruction == S57Constants.UpdateInstruction.INSERT){
                            
                        }
                    }else if(update instanceof FeatureRecord){
                        final FeatureRecord fr = new FeatureRecord();
                        
                    }
                    updateSequence.add(new AbstractMap.SimpleImmutableEntry<Pointer, S57Object>(pointer, update));
                }
                
                updateCells.add(updateSequence);
                newCells.add(newSequence);
            }finally{
                if(reader!=null){
                    reader.dispose();
                }
            }
        }
        
    }
    
    /**
     * Set the update files to handle.
     * Must be set before reading starts.
     * @param files 
     */
    public void setUpdateFiles(final Collection<File> files){
        updateFiles.clear();
        updateFiles.addAll(files);
    }

    /**
     * 
     * @throws IOException 
     */
    @Override
    protected void findNext() throws IOException {
        super.findNext(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Override base reader, modifying records with updates on the fly.
     * @param rec
     * @return
     * @throws IOException 
     */
    @Override
    protected S57Object toS57Object(DataRecord rec) throws IOException {
        S57Object candidate = super.toS57Object(rec);
        if(candidate == null) return candidate;
        
        //load updates if not already done
        loadUpdates();
        //apply updates
        final Pointer candidateUID = candidate.generatePointer();
        loop:
        for(List<Entry<Pointer,S57Object>> updateSequence : updateCells){
            for(Entry<Pointer,S57Object> update : updateSequence){
                if(update.getKey().equals(candidateUID)){
                    candidate = applyUpdate(candidate, update.getValue());
                    if(candidate == null){
                        //a deletion update, no need to go further
                        break loop;
                    }
                }
            }
        }
        
        return candidate;
    }
    
    /**
     * Apply the given update to candidate object.
     * 
     * @param candidate not null
     * @param update not null
     * @return updated object or null if update consist of a deletion.
     */
    private S57Object applyUpdate(S57Object candidate, S57Object update){
        //TODO
        return candidate;
    }
    
}

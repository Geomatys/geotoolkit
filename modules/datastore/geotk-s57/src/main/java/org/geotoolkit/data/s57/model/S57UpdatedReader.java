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
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import org.geotoolkit.data.s57.S57Constants;

/**
 * S57 Object reader which merge a set of updates on the fly.
 * Several UpdateReaders can be stacked to handle multiple update files.
 * 
 * Not thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class S57UpdatedReader extends S57Reader{
    
    //wrapped reader
    private S57Reader baseReader;
    
    private File updateFile;
    //contains updates of type Deletion or Update
    private List<Entry<Pointer,S57Object>> updateCells;
    //contains updates of type Insertion
    private List<VectorRecord> newVectors; 
    private List<FeatureRecord> newFeatures; 
    private boolean vectorsInserted = false;
    private boolean featuresInserted = false;

    public S57UpdatedReader(S57Reader baseReader, File updateFile) {
        this.baseReader = baseReader;
        this.updateFile = updateFile;
    }
    
    @Override
    public void setInput(Object input) throws IOException{
        if(input instanceof S57Reader){
            baseReader = (S57Reader) input;
        }else{
            throw new IOException("Unsupported input, only handle S57Reader : "+input);
        }
    }
    
    /**
     * Read update file, store the update sequences in updateCells variable.
     * @throws IOException , if reading the update file failed.
     */
    private void loadUpdates() throws IOException{
        if(updateCells!=null) return;
        
        updateCells = new ArrayList<Entry<Pointer,S57Object>>();
        newVectors = new ArrayList<VectorRecord>();
        newFeatures = new ArrayList<FeatureRecord>();
                
        //read file and store the update sequences
        S57Reader reader = null;
        try{
            reader = new S57FileReader();
            reader.setDsid(baseReader.dsid);
            reader.setInput(updateFile);
            reader.setPredicate(predicate); //reuse the same predicate     
            while(reader.hasNext()){
                final S57Object update = reader.next();
                final Pointer pointer = update.generatePointer();
                if(update instanceof VectorRecord){
                    final VectorRecord vr = (VectorRecord) update;
                    if(vr.updateInstruction == S57Constants.UpdateInstruction.INSERT){
                        newVectors.add(vr);
                    }else{
                        updateCells.add(new AbstractMap.SimpleImmutableEntry<Pointer, S57Object>(pointer, update));
                    }
                }else if(update instanceof FeatureRecord){
                    final FeatureRecord fr = (FeatureRecord) update;
                    if(fr.updateInstruction == S57Constants.UpdateInstruction.INSERT){
                        newFeatures.add(fr);
                    }else{
                        updateCells.add(new AbstractMap.SimpleImmutableEntry<Pointer, S57Object>(pointer, update));
                    }
                }
            }

        }finally{
            if(reader!=null){
                reader.dispose();
            }
        }
    }
    
    /**
     * Set the update file to handle.
     * Must be set before reading starts.
     * @param files 
     */
    public void setUpdateFile(final File file){
        this.updateFile = file;
    }

    /**
     * Get the update file.
     * @return File
     */
    public File getUpdateFile() {
        return updateFile;
    }
    
    /**
     * Insert new records in the reading stream.
     * @throws IOException 
     */
    @Override
    protected void findNext() throws IOException {
        if(record!=null) return;
        
        //load updates if not already done
        loadUpdates();
        
        //we insert new vectors records at the beginning
        if(!vectorsInserted){
            if(newVectors.isEmpty()){
                vectorsInserted = true;
            }else{
                record = newVectors.remove(0);
                return;
            }
        }
        
        while(baseReader.hasNext() && record == null){
            record = updateS57Object(baseReader.next());
        }
        
        //we insert new features records at the end
        if(record == null && !featuresInserted){
            if(newFeatures.isEmpty()){
                featuresInserted = true;
            }else{
                record = newFeatures.remove(0);
            }
        }
    }

    /**
     * Modifying records with updates.
     * @param rec
     * @return
     * @throws IOException 
     */
    protected S57Object updateS57Object(S57Object candidate) throws IOException {
        if(candidate == null) return candidate;
        
        //apply updates
        final Pointer candidateUID = candidate.generatePointer();
        loop:
        for(Entry<Pointer,S57Object> update : updateCells){
            if(update.getKey().equals(candidateUID)){
                candidate = applyUpdate(candidate, update.getValue());
                if(candidate == null){
                    //a deletion update, no need to go further
                    break loop;
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
    private S57Object applyUpdate(final S57Object candidate, final S57Object update) throws IOException{
                
        if(candidate instanceof FeatureRecord){
            final FeatureRecord fr = (FeatureRecord) candidate;
            final FeatureRecord ur = (FeatureRecord) update;
            
            //check version
            if(fr.version+1 != ur.version){
                throw new IOException("Incoherent update for record "+fr.generatePointer()
                        +" , base version is "+fr.version+" updated version is "+ur.version);
            }
            
            if(ur.updateInstruction == S57Constants.UpdateInstruction.DELETE){
                return null;
            }
            
            //update version
            fr.version = ur.version;
            
            //update attributes
            updateAttributes(fr.attributes, ur.attributes);
            updateAttributes(fr.nattributes, ur.nattributes);
            
            //update spatial pointers
            updateByControl(ur.spatialControl, fr.spatialPointers, ur.spatialPointers);
            
            //update object pointers
            updateByControl(ur.objectControl, fr.objectPointers, ur.objectPointers);
            
            
        }else if(candidate instanceof VectorRecord){
            final VectorRecord vr = (VectorRecord) candidate;
            final VectorRecord ur = (VectorRecord) update;
            
            //check version
            if(vr.version+1 != ur.version){
                throw new IOException("Incoherent update for record "+vr.generatePointer()
                        +" , base version is "+vr.version+" updated version is "+ur.version);
            }
            
            if(ur.updateInstruction == S57Constants.UpdateInstruction.DELETE){
                return null;
            }
            
            //update attributes
            updateAttributes(vr.attributes, ur.attributes);
            
            //update object pointers
            updateByControl(ur.recordPointerControl, vr.records, ur.records);
            
            //update coord pointers
            updateByControl(ur.coordinateControl, vr.coords, ur.coords);
            
            //update version
            vr.version = ur.version;
            
        }
        
        return candidate;
    }
    
    private void updateAttributes(final List<? extends BaseAttribute> attributes, final List<? extends BaseAttribute> updates){
        for(BaseAttribute att : updates){
            if(att.value.length() == 1 && att.value.charAt(0) == att.attfLexicalLevel.getDeleteValue()){
                //delete attribute
                final ListIterator<? extends BaseAttribute> ite = attributes.listIterator();
                while(ite.hasNext()){
                    final BaseAttribute frt = ite.next();
                    if(frt.code == att.code){
                        ite.remove();
                    }
                }
            }else{
                //update attribute
                for(BaseAttribute frt : attributes){
                    if(frt.code == att.code){
                        frt.value = att.value;
                    }
                }
            }
        }
    }
    
    private void updateByControl(final BaseControl control, final List base, final List updates){
        if(control==null) return;
        if(control.number==0) return;
            
        if(control.update == S57Constants.UpdateInstruction.DELETE){
            base.subList(control.index-1, control.index-1+control.number).clear();
        }else if(control.update == S57Constants.UpdateInstruction.INSERT){            
            base.addAll(control.index-1, updates.subList(0, control.number));
        }else if(control.update == S57Constants.UpdateInstruction.MODIFY){
            for(int i=0;i<control.number;i++){
                base.set(control.index-1+i, updates.get(i));
            }
        }
    }
    
}

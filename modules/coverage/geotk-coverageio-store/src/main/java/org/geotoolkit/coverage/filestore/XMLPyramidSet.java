/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.filestore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.PyramidSet;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.xml.MarshallerPool;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@XmlRootElement(name="PyramidSet")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLPyramidSet implements PyramidSet{
    
    @XmlTransient
    private static MarshallerPool POOL;
    
    private static synchronized MarshallerPool getPoolInstance() throws JAXBException{
        if(POOL == null){
            POOL = new MarshallerPool(XMLPyramidSet.class);
        }
        return POOL;
    }
    
    
    @XmlElement(name="Pyramid")
    private List<XMLPyramid> pyramids;
    
    @XmlTransient
    private String id;
    @XmlTransient
    private File mainfile;

    void initialize(File mainFile){
        this.mainfile = mainFile;
        //calculate id based on file name
        id = mainfile.getName();
        int index = id.lastIndexOf('.');
        if(index > 0){
            id = id.substring(0,index);
        }
        
        for(XMLPyramid pyramid : pyramids()){
            pyramid.initialize(this);
        }
    }
    
    public List<XMLPyramid> pyramids() {
        if(pyramids == null){
            pyramids = new ArrayList<XMLPyramid>();
        }
        return pyramids;
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * @return xml file where the pyramid set definition is stored.
     */
    public File getMainfile() {
        return mainfile;
    }
    
    /**
     * @return Folder where each pyramid is stored.
     */
    public File getFolder(){
        return new File(mainfile.getParentFile(),getId());
    }
    
    @Override
    public Collection<Pyramid> getPyramids() {
        return (Collection)pyramids();
    }

    @Override
    public List<String> getFormats() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Envelope getEnvelope() {
        for(XMLPyramid pyramid : pyramids()){
            for(XMLMosaic mosaic : pyramid.mosaics()){
                return mosaic.getEnvelope();
            }
        }
        return null;
    }
    
    @Override
    public String toString(){
        return Trees.toString(Classes.getShortClassName(this)+" "+getId(), getPyramids());
    }
    
    /**
     * Get the pyramid for with given id.
     * 
     * @param pyramidId
     * @return
     * @throws DataStoreException 
     */
    XMLPyramid getPyramid(String pyramidId) throws DataStoreException{
        for(final Pyramid py : getPyramids()){
            if(pyramidId.equals(py.getId())){
                final XMLPyramid pyramid = (XMLPyramid) py;
                return pyramid;
            }
        }
        throw new DataStoreException("No pyramid for ID : " + pyramidId);
    }
    
    /**
     * Create and register a new pyramid in the set.
     * 
     * @param crs
     * @return 
     */
    Pyramid createPyramid(CoordinateReferenceSystem crs) {
        final XMLPyramid pyramid = new XMLPyramid();
        pyramid.crs = crs.toWKT();
        pyramid.id = IdentifiedObjects.getIdentifier(crs);
        pyramid.postfix = "png";
        pyramid.initialize(this);
        pyramids().add(pyramid);
        return pyramid;
    }
    
    /**
     * Write this pyramid set in it's main file.
     * @throws JAXBException 
     */
    public void write() throws JAXBException{
        final MarshallerPool pool = getPoolInstance();
        final Marshaller marshaller = pool.acquireMarshaller();
        try{
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(this, getMainfile());
        }finally{
            pool.release(marshaller);
        }
    }
    
    /**
     * Read the given file and return an XMLPyramidSet.
     * 
     * @param file
     * @return
     * @throws JAXBException 
     */
    public static XMLPyramidSet read(File file) throws JAXBException{
        final MarshallerPool pool = getPoolInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final XMLPyramidSet set;
        try{
            set = (XMLPyramidSet) unmarshaller.unmarshal(file);
        }finally{
            pool.release(unmarshaller);
        }
        set.initialize(file);
        return set;
    }

}

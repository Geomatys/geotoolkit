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
package org.geotoolkit.coverage.xmlstore;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import net.iharder.Base64;
import org.apache.sis.io.wkt.Convention;
import org.apache.sis.io.wkt.FormattableObject;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.cs.DiscreteReferencingFactory;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLPyramid implements Pyramid{

    @XmlElement(name="id")
    String id;
    @XmlElement(name="crs")
    String crs;
    @XmlElement(name="serializedCrs")
    String serializedCrs;
    @XmlElement(name="Mosaic")
    List<XMLMosaic> mosaics;

    @XmlTransient
    private XMLPyramidSet set;
    @XmlTransient
    private CoordinateReferenceSystem crsobj;

    void initialize(XMLPyramidSet set) {
        this.set = set;
        for(XMLMosaic mosaic : mosaics()){
            mosaic.initialize(this);
        }
    }

    public List<XMLMosaic> mosaics() {
        if(mosaics == null){
            mosaics = new ArrayList<>();
        }
        return mosaics;
    }

    @Override
    public String getId() {
        return id;
    }

    public File getFolder(){
        return new File(getPyramidSet().getRef().getFolder(),getId());
    }

    @Override
    public XMLPyramidSet getPyramidSet() {
        return set;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        if(crsobj != null){
            return crsobj;
        }

        try {
            try {
                crsobj = (CoordinateReferenceSystem) Base64.decodeToObject(serializedCrs);
            } catch (IOException ex) {
                Logging.getLogger(this.getClass()).log(Level.WARNING, ex.getMessage(), ex);
            }
            
            if(crsobj==null){
                if(crs.startsWith("EPSG")){
                    crsobj = CRS.decode(crs);
                }else{
                    crsobj = CRS.parseWKT(crs);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(XMLPyramid.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        if(crsobj.getCoordinateSystem().getDimension()>2){
            //get mosaic additional axis values to recreate discret axis
            final SortedSet[] discretValues = new SortedSet[crsobj.getCoordinateSystem().getDimension()];
            for(int i=0;i<discretValues.length;i++){
                discretValues[i] = new TreeSet();
            }
            
            for(XMLMosaic mosaic : mosaics()){
                for(int i=2;i<discretValues.length;i++){
                    discretValues[i].add(mosaic.upperLeft[i]);
                }
            }
            
            final double[][] table = new double[discretValues.length][0];
            for(int i=0;i<discretValues.length;i++){
                final Object[] ds = discretValues[i].toArray();
                final double[] vals = new double[ds.length];
                for(int k=0;k<ds.length;k++){
                    vals[k] = (Double)ds[k];
                }
                table[i] = vals;
            }
            
            crsobj = DiscreteReferencingFactory.createDiscreteCRS(crsobj, table);
        }
        
        
        return crsobj;
    }

    void setCoordinateReferenceSystem(CoordinateReferenceSystem crs){
        this.crs = ((FormattableObject)crs).toString(Convention.WKT1);        
        try {
            this.serializedCrs = Base64.encodeObject((Serializable)crs);
        } catch (IOException ex) {
            Logging.getLogger(this.getClass()).log(Level.WARNING, ex.getMessage(), ex);
        }
    }
    
    @Override
    public double[] getScales() {
        final SortedSet<Double> scaleSet = new TreeSet<Double>();
        
        for(GridMosaic m : mosaics()){
            scaleSet.add(m.getScale());
        }
        
        final double[] scales = new double[scaleSet.size()];
        int i=0;
        for(Double d : scaleSet){
            scales[i] = d;
            i++;
        }
        return scales;
    }

    @Override
    public Collection<GridMosaic> getMosaics(int index) {
        final List<GridMosaic> candidates = new ArrayList<GridMosaic>();
        final double[] scales = getScales();
        for(GridMosaic m : mosaics()){
            if(m.getScale() == scales[index]){
                candidates.add(m);
            }
        }
        return candidates;
    }

    @Override
    public List<GridMosaic> getMosaics() {
        return new ArrayList<GridMosaic>(mosaics());
    }

    @Override
    public String toString(){
        return Trees.toString(
                Classes.getShortClassName(this)
                +" "+IdentifiedObjects.getIdentifier(getCoordinateReferenceSystem())
                +" "+getId(),
                mosaics());
    }

    /**
     * Return the mosaic with the given id
     *
     * @param mosaicId
     * @return
     * @throws DataStoreException if no mosaic have this id
     */
    XMLMosaic getMosaic(String mosaicId) throws DataStoreException{
        for(final XMLMosaic mo : mosaics()){
            if(mosaicId.equals(mo.getId())){
                return mo;
            }
        }
        throw new DataStoreException("No mosaic for ID : " + mosaicId);
    }

    /**
     * Create and register a new mosaic in this pyramid
     *
     * @param gridSize
     * @param tilePixelSize
     * @param upperleft
     * @param pixelscale
     * @return
     */
    XMLMosaic createMosaic(Dimension gridSize, Dimension tilePixelSize, DirectPosition upperleft, double pixelscale) {
        final XMLMosaic mosaic = new XMLMosaic();
        mosaic.scale = pixelscale;
        mosaic.gridWidth = gridSize.width;
        mosaic.gridHeight = gridSize.height;
        mosaic.tileWidth = tilePixelSize.width;
        mosaic.tileHeight = tilePixelSize.height;
        mosaic.upperLeft = upperleft.getCoordinate();
        mosaics.add(mosaic);
        mosaic.initialize(this);
        return mosaic;
    }

}

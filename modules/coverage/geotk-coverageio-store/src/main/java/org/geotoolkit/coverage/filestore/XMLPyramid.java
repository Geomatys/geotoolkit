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

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
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
    @XmlElement(name="postfix")
    String postfix;
    @XmlElement(name="Mosaic")
    List<XMLMosaic> mosaics;
    
    @XmlTransient
    private XMLPyramidSet set;
    @XmlTransient
    private final SortedMap<Double,XMLMosaic> sorted = new TreeMap<Double, XMLMosaic>();
    @XmlTransient
    private CoordinateReferenceSystem crsobj; 
    
    void initialize(XMLPyramidSet set) {
        this.set = set;
        for(XMLMosaic mosaic : mosaics()){
            sorted.put(mosaic.getScale(), mosaic);
            mosaic.initialize(this);
        }
    }
    
    public List<XMLMosaic> mosaics() {
        if(mosaics == null){
            mosaics = new ArrayList<XMLMosaic>();
        }
        return mosaics;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getPostfix() {
        return postfix;
    }
    
    public File getFolder(){
        return new File(getPyramidSet().getFolder(),getId());
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
            if(crs.startsWith("EPSG")){
                crsobj = CRS.decode(crs);
            }else{
                crsobj = CRS.parseWKT(crs);
            }
        } catch (Exception ex) {
            Logger.getLogger(XMLPyramid.class.getName()).log(Level.SEVERE, null, ex);
        }
        return crsobj;
    }

    @Override
    public double[] getScales() {
        final Set<Double> scales = sorted.keySet();
        final double[] array = new double[scales.size()];
        int i=0;
        for(Double d : scales){
            array[i] = d;
            i++;
        }
        return array;
    }

    @Override
    public GridMosaic getMosaic(int index) {
        int i=0;
        for(XMLMosaic mosaic : sorted.values()){
            if(i==index){
                return mosaic;
            }
            i++;
        }
        throw new ArrayIndexOutOfBoundsException(index);
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
    XMLMosaic createMosaic(Dimension gridSize, Dimension tilePixelSize, Point2D upperleft, double pixelscale) {
        final XMLMosaic mosaic = new XMLMosaic();
        mosaic.scale = pixelscale;
        mosaic.gridWidth = gridSize.width;
        mosaic.gridHeight = gridSize.height;
        mosaic.tileWidth = tilePixelSize.width;
        mosaic.tileHeight = tilePixelSize.height;
        mosaic.upperleftX = upperleft.getX();
        mosaic.upperleftY = upperleft.getY();
        mosaic.completion = "";
        mosaics.add(mosaic);
        sorted.put(pixelscale, mosaic);
        mosaic.initialize(this);
        return mosaic;
    }

}

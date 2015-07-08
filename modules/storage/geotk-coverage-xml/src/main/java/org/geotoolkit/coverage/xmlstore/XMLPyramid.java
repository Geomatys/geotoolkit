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
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import net.iharder.Base64;
import org.apache.sis.io.wkt.*;
import org.apache.sis.io.wkt.WKTFormat;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.storage.coverage.GridMosaic;
import org.geotoolkit.storage.coverage.Pyramid;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="XMLPyramid")
public class XMLPyramid implements Pyramid {

    @XmlElement(name="id")
    String id;
    @XmlElement(name="crs")
    String crs;
    @XmlElement(name="serializedCrs")
    String serializedCrs;
    @XmlElement(name="Mosaic")
    List<XMLMosaic> mosaics = new CopyOnWriteArrayList<>();

    @XmlTransient
    private XMLPyramidSet set;
    @XmlTransient
    protected CoordinateReferenceSystem crsobj;

    /**
     * Default constructor is reserved for JAXB usage. If an user wants to create a pyramid, he MUST initialize it with
     * a CRS, using following builder : {@linkplain #XMLPyramid(org.opengis.referencing.crs.CoordinateReferenceSystem)}.
     */
    private XMLPyramid() {}

    XMLPyramid(CoordinateReferenceSystem pyramidCRS) throws DataStoreException {
        setCoordinateReferenceSystem(pyramidCRS);
    }

    /**
     * Initialize the pyramid, including its inner mosaics.
     * @param set The parent set of pyramid for this object.
     * @throws DataStoreException If the pyramid CRS is null or invalid.
     */
    void initialize(XMLPyramidSet set) throws DataStoreException {
        // A simple security. If the pyramid CRS is invalid, we will return the error at initialization.
        try {
            getCoordinateReferenceSystem();
        } catch (Exception e) {
            throw new DataStoreException("Pyramid cannot be initialized : invalid CRS.", e);
        }
        this.set = set;
        for(XMLMosaic mosaic : mosaics()){
            mosaic.initialize(this);
        }
    }

    public List<XMLMosaic> mosaics() {
        if(mosaics == null){
            mosaics = new CopyOnWriteArrayList<>();
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

        if (crs != null) {
            try {
                if (crs.startsWith("EPSG")) {
                    crsobj = CRS.decode(crs);
                } else {
                    crsobj = CRS.parseWKT(crs);
                }
            } catch (Exception e) {
                // Exception should be thrown only at initialisation, as we will call the method at object built.
                throw new RuntimeException(e);
            }
        }
        
        if (serializedCrs != null) {
            try {
                crsobj = (CoordinateReferenceSystem) Base64.decodeToObject(serializedCrs);
            } catch (Exception ex) {
                Logging.getLogger(this.getClass()).log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        return crsobj;
    }

    void setCoordinateReferenceSystem(CoordinateReferenceSystem crs) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Input CRS", crs);
        //-- init 
        crsobj = crs;
        this.crs = null;
        this.serializedCrs = null;
        
        //-- try wkt2 writing
        final WKTFormat f = new WKTFormat(null, null);
        f.setConvention(Convention.WKT2);
        this.crs = f.format(crs);
        
        //-- if problem try to serialize  CRS
        if (f.getWarnings() != null) {
            if (crs instanceof Serializable) {
                try {
                    this.serializedCrs = Base64.encodeObject((Serializable)crs);
                } catch (IOException serializedEx) {
                    throw new DataStoreException(serializedEx);
                }
            }
        }
        assert (this.crs != null || serializedCrs != null);
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
                +" "+IdentifiedObjects.getIdentifierOrName(getCoordinateReferenceSystem())
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

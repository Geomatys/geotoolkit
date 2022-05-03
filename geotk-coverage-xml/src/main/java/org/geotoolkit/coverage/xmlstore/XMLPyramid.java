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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.sis.io.wkt.*;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.storage.multires.AbstractTileMatrixSet;
import org.geotoolkit.storage.multires.ScaleSortedMap;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.storage.multires.WritableTileMatrix;
import org.geotoolkit.storage.multires.WritableTileMatrixSet;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="XMLPyramid")
public class XMLPyramid implements WritableTileMatrixSet {

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
    public GenericName getIdentifier() {
        return Names.createLocalName(null, null, id);
    }

    public Path getFolder(){
        return getPyramidSet().getRef().getFolder().resolve(id);
    }

    public XMLPyramidSet getPyramidSet() {
        return set;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        if(crsobj != null){
            return crsobj;
        }

        if (serializedCrs != null) {
            try {
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(serializedCrs)));
                crsobj = (CoordinateReferenceSystem) in.readObject();
            } catch (Exception ex) {
                final String msg = "Unable to read base64 serialized CRS, fallback to WKT : "+ex.getMessage();
                Logger.getLogger("org.geotoolkit.coverage.xmlstore").log(Level.WARNING, msg);
            }
        }

        if (crs != null && crsobj == null) {
            try {
                if (crs.startsWith("EPSG")) {
                    crsobj = CRS.forCode(crs);
                } else {
                    crsobj = CRS.fromWKT(crs);
                }
            } catch (Exception e) {
                // Exception should be thrown only at initialisation, as we will call the method at object built.
                throw new RuntimeException(e);
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

        if (crs instanceof Serializable) {
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bo);
                out.writeObject(crs);
                out.close();
                this.serializedCrs = Base64.getEncoder().encodeToString(bo.toByteArray());
            } catch (IOException serializedEx) {
                Logger.getLogger("org.geotoolkit.coverage.xmlstore").log(Level.WARNING, serializedEx.getMessage(), serializedEx);
            }
        }


//////        //-- if problem try to serialize  CRS
//////        if (f.getWarnings() != null) {
//////            if (crs instanceof Serializable) {
//////                try {
//////                    this.serializedCrs = Base64.encodeObject((Serializable)crs);
//////                } catch (IOException serializedEx) {
//////                    throw new DataStoreException(serializedEx);
//////                }
//////            }
//////        }
        assert (this.crs != null || serializedCrs != null);
    }

    @Override
    public SortedMap<GenericName,WritableTileMatrix> getTileMatrices() {
        final ScaleSortedMap<WritableTileMatrix> map = new ScaleSortedMap<>();
        for(WritableTileMatrix wtm : mosaics()) {
            map.insertByScale(wtm);
        }
        return map;
    }

    @Override
    public String toString(){
        return AbstractTileMatrixSet.toString(this);
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
            if(mosaicId.equals(mo.getIdentifier())){
                return mo;
            }
        }
        throw new DataStoreException("No mosaic for ID : " + mosaicId);
    }

    @Override
    public WritableTileMatrix createTileMatrix(org.apache.sis.storage.tiling.TileMatrix templateSis) throws DataStoreException {
        final TileMatrix template = (TileMatrix) templateSis;
        final XMLMosaic mosaic = new XMLMosaic();
        mosaic.scale = template.getResolution()[0];
        mosaic.gridWidth = TileMatrices.getGridSize(template).width;
        mosaic.gridHeight = TileMatrices.getGridSize(template).height;
        mosaic.tileWidth = template.getTileSize().width;
        mosaic.tileHeight = template.getTileSize().height;
        mosaic.upperLeft = TileMatrices.getUpperLeftCorner(template).getCoordinate();
        //for backward compatibility
        mosaic.dataPixelWidth = mosaic.gridWidth * mosaic.tileWidth;
        mosaic.dataPixelHeight = mosaic.gridHeight * mosaic.tileHeight;
        mosaics.add(mosaic);
        mosaic.initialize(this);
        set.getRef().save();
        return mosaic;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void deleteTileMatrix(String mosaicId) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

}

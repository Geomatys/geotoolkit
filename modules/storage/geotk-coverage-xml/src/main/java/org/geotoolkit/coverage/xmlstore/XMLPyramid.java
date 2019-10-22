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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.io.wkt.*;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.util.StringUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
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

    @Override
    public String getFormat() {
        final String format = set.getFormatName();
        switch (format) {
            case "JPEG": return "image/jpeg";
            case "PNG": return "image/png";
            case "PostGISWKBraster" : return "application/wkb"; // better mime type ?
            default : throw new IllegalStateException("unexpected pyramid format");
        }
    }

    public List<XMLMosaic> mosaics() {
        if(mosaics == null){
            mosaics = new CopyOnWriteArrayList<>();
        }
        return mosaics;
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    public Path getFolder(){
        return getPyramidSet().getRef().getFolder().resolve(getIdentifier());
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
                Logging.getLogger("org.geotoolkit.coverage.xmlstore").log(Level.WARNING, msg);
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
                Logging.getLogger("org.geotoolkit.coverage.xmlstore").log(Level.WARNING, serializedEx.getMessage(), serializedEx);
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
    public double[] getScales() {
        final SortedSet<Double> scaleSet = new TreeSet<Double>();

        for(Mosaic m : mosaics()){
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
    public Collection<Mosaic> getMosaics(int index) {
        final List<Mosaic> candidates = new ArrayList<>();
        final double[] scales = getScales();
        for(Mosaic m : mosaics()){
            if(m.getScale() == scales[index]){
                candidates.add(m);
            }
        }
        return candidates;
    }

    @Override
    public List<Mosaic> getMosaics() {
        return new ArrayList<Mosaic>(mosaics());
    }

    @Override
    public Envelope getEnvelope() {
        GeneralEnvelope env = null;
        for(Mosaic mosaic : getMosaics()){
            if(env==null){
                env = new GeneralEnvelope(mosaic.getEnvelope());
            }else{
                env.add(mosaic.getEnvelope());
            }
        }
        return env;
    }

    @Override
    public String toString(){
        return StringUtilities.toStringTree(Classes.getShortClassName(this)
                +" "+IdentifiedObjects.getIdentifierOrName(getCoordinateReferenceSystem())
                +" "+getIdentifier(),
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
            if(mosaicId.equals(mo.getIdentifier())){
                return mo;
            }
        }
        throw new DataStoreException("No mosaic for ID : " + mosaicId);
    }

    @Override
    public Mosaic createMosaic(Mosaic template) throws DataStoreException {
        final XMLMosaic mosaic = new XMLMosaic();
        mosaic.scale = template.getScale();
        mosaic.gridWidth = template.getGridSize().width;
        mosaic.gridHeight = template.getGridSize().height;
        mosaic.tileWidth = template.getTileSize().width;
        mosaic.tileHeight = template.getTileSize().height;
        mosaic.upperLeft = template.getUpperLeftCorner().getCoordinate();
        mosaic.dataPixelWidth = template.getDataExtent().getSize(0);
        mosaic.dataPixelHeight = template.getDataExtent().getSize(1);
        mosaics.add(mosaic);
        mosaic.initialize(this);
        set.getRef().save();
        return mosaic;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void deleteMosaic(String mosaicId) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

}

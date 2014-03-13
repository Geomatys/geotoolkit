/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
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
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ProgressMonitor;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.coverage.AbstractPyramidalModel;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.opengis.coverage.SampleDimensionType;
import org.opengis.feature.type.Name;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@XmlRootElement(name="CoverageReference")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLCoverageReference extends AbstractPyramidalModel {

    
    @XmlTransient
    private static MarshallerPool POOL;

    private static synchronized MarshallerPool getPoolInstance() throws JAXBException{
        if(POOL == null){
            POOL = new MarshallerPool(JAXBContext.newInstance(XMLPyramidSet.class), null);
        }
        return POOL;
    }
    
    @XmlElement(name="PyramidSet")
    private XMLPyramidSet set;
    /** One of geophysics/native */
    @XmlElement(name="packMode")
    private String packMode;
    @XmlElement(name="SampleDimension")
    private List<XMLSampleDimension> sampleDimensions;
    @XmlElement(name="ColorModel")
    private XMLColorModel colorModel;
    
    @XmlTransient
    private String id;
    @XmlTransient
    private File mainfile;
    //caches
    @XmlTransient
    private List<GridSampleDimension> cacheDimensions = null;
    @XmlTransient
    private ColorModel cacheColorModel = null;

    public XMLCoverageReference() {
        super(null, null, 0);
    }

    public XMLCoverageReference(XMLCoverageStore store, Name name, XMLPyramidSet set) {
        super(store,name,0);
        this.set = set;
    }

    public List<XMLSampleDimension> getXMLSampleDimensions() {
        if(sampleDimensions==null){
            sampleDimensions = new ArrayList<>();
        }
        return sampleDimensions;
    }

    public void copy(XMLCoverageReference ref){
        this.set = ref.set;
        this.colorModel = ref.colorModel;
    }
    
    void initialize(File mainFile){
        this.mainfile = mainFile;
        //calculate id based on file name
        id = mainfile.getName();
        int index = id.lastIndexOf('.');
        if(index > 0){
            id = id.substring(0,index);
        }

        final XMLPyramidSet set = getPyramidSet();
        for(XMLPyramid pyramid : set.pyramids()){
            pyramid.initialize(set);
        }
    }
    
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
    public boolean isWritable() throws CoverageStoreException {
        return true;
    }

    @Override
    public XMLPyramidSet getPyramidSet() {
        return set;
    }

    @Override
    public Pyramid createPyramid(CoordinateReferenceSystem crs) throws DataStoreException {
        final XMLPyramidSet set = getPyramidSet();
        save();
        return set.createPyramid(crs);
    }

    @Override
    public void deletePyramid(String pyramidId) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    @Override
    public GridMosaic createMosaic(String pyramidId, Dimension gridSize,
    Dimension tilePixelSize, DirectPosition upperleft, double pixelscale) throws DataStoreException {
        final XMLPyramidSet set = getPyramidSet();
        final XMLPyramid pyramid = (XMLPyramid) set.getPyramid(pyramidId);
        final XMLMosaic mosaic = pyramid.createMosaic(gridSize, tilePixelSize, upperleft, pixelscale);
        save();
        return mosaic;
    }

    @Override
    public void deleteMosaic(String pyramidId, String mosaicId) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    @Override
    public void writeTile(String pyramidId, String mosaicId, int col, int row, RenderedImage image) throws DataStoreException {
        final XMLPyramidSet set = getPyramidSet();
        final XMLPyramid pyramid = (XMLPyramid) set.getPyramid(pyramidId);
        final XMLMosaic mosaic = pyramid.getMosaic(mosaicId);
        mosaic.createTile(col,row,image);
        save();
    }

    /**
     * Save the coverage reference in the file
     * @throws DataStoreException
     */
    synchronized void save() throws DataStoreException{
        try {
            final MarshallerPool pool = getPoolInstance();
            final Marshaller marshaller = pool.acquireMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(this, getMainfile());
            pool.recycle(marshaller);
        } catch (JAXBException ex) {
            Logger.getLogger(XMLCoverageReference.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    /**
     * Read the given file and return an XMLCoverageReference.
     *
     * @param file
     * @return
     * @throws JAXBException
     */
    public static XMLCoverageReference read(File file) throws JAXBException{
        final MarshallerPool pool = getPoolInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final XMLCoverageReference ref;
        ref = (XMLCoverageReference) unmarshaller.unmarshal(file);
        pool.recycle(unmarshaller);
        ref.initialize(file);
        return ref;
    }
    
    @Override
    public void writeTiles(String pyramidId, String mosaicId, RenderedImage image, boolean onlyMissing, ProgressMonitor monitor) throws DataStoreException {
        final XMLPyramidSet set = getPyramidSet();
        final XMLPyramid pyramid = (XMLPyramid) set.getPyramid(pyramidId);
        final XMLMosaic mosaic = pyramid.getMosaic(mosaicId);
        mosaic.writeTiles(image,onlyMissing, monitor);
        save();
    }

    @Override
    public void deleteTile(String pyramidId, String mosaicId, int col, int row) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    @Override
    public synchronized List<GridSampleDimension> getSampleDimensions() throws DataStoreException {
        if(cacheDimensions==null){
            if(sampleDimensions==null || sampleDimensions.isEmpty()) return null;
            cacheDimensions = new ArrayList<>();
            for(XMLSampleDimension xsd : sampleDimensions){
                cacheDimensions.add(xsd.buildSampleDimension());
            }
        }

        return cacheDimensions;
    }

    @Override
    public void setSampleDimensions(List<GridSampleDimension> dimensions) throws DataStoreException {
        if(dimensions==null) return;
        this.cacheDimensions = null; //clear cache

        if(sampleDimensions==null) sampleDimensions = new ArrayList<>();
        sampleDimensions.clear();
        for(GridSampleDimension dimension : dimensions){
            final SampleDimensionType sdt = dimension.getSampleDimensionType();
            final XMLSampleDimension dim = new XMLSampleDimension();
            dim.fill(dimension);
            dim.setSampleType(sdt);
            sampleDimensions.add(dim);
        }
        save();
    }

    @Override
    public ColorModel getColorModel() {
        if(cacheColorModel==null && colorModel!=null){
            cacheColorModel = colorModel.buildColorModel();
        }
        return cacheColorModel;
    }

    @Override
    public void setColorModel(ColorModel colorModel) throws DataStoreException {
        this.cacheColorModel = null; //clear cache
        
        if(colorModel instanceof ComponentColorModel){
            this.colorModel = new XMLColorModelComponent();
            ((XMLColorModelComponent)this.colorModel).fill((ComponentColorModel)colorModel);
        }else if(colorModel instanceof DirectColorModel){
            this.colorModel = new XMLColorModelDirect();
            ((XMLColorModelDirect)this.colorModel).fill((DirectColorModel)colorModel);
        }else if(colorModel instanceof IndexColorModel){
            this.colorModel = new XMLColorModelIndexed();
            ((XMLColorModelIndexed)this.colorModel).fill((IndexColorModel)colorModel);
        }
        save();
    }

    @Override
    public ViewType getPackMode() {
        return ViewType.valueOf(packMode);
    }

    @Override
    public void setPackMode(ViewType packMode) {
        this.packMode = packMode.name();
    }

}

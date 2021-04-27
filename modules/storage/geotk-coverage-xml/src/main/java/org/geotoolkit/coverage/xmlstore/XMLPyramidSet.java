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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.xml.bind.annotation.*;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Classes;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.util.StringUtilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.geotoolkit.storage.multires.TileMatrixSet;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLPyramidSet {

    public static final String GEOPHYSICS = "geophysics";
    public static final String NATIVE = "native";


    @XmlElement(name="Pyramid")
    private List<XMLPyramid> pyramids;

    @XmlTransient
    private ImageReaderSpi spi;
    @XmlTransient
    private XMLCoverageResource ref;

    public XMLPyramidSet() {
    }

    public String getFormatName() {
        final String format = ref.getPreferredFormat();
        if(format!=null && !format.isEmpty()) return format;
        return ref.getPackMode().equals("GEOPHYSICS") ? "tiff" : "PNG";
    }

    public XMLCoverageResource getRef() {
        return ref;
    }

    public void setRef(XMLCoverageResource ref) {
        this.ref = ref;
    }

    public ImageReaderSpi getReaderSpi() throws DataStoreException{
        if(spi == null){
            try {
                final ImageReader reader = XImageIO.getReaderByFormatName(getFormatName(), null, Boolean.FALSE, Boolean.FALSE);
                spi = reader.getOriginatingProvider();
                reader.dispose();
            } catch (IOException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }
        return spi;
    }

    public List<XMLPyramid> pyramids() {
        if(pyramids == null){
            pyramids = new ArrayList<>();
        }
        return pyramids;
    }

    public Collection<TileMatrixSet> getPyramids() {
        return (Collection)pyramids();
    }

    @Override
    public String toString(){
        return StringUtilities.toStringTree(Classes.getShortClassName(this)+" "+ref.getId(), getPyramids());
    }

    /**
     * Create and register a new pyramid in the set.
     *
     * @param crs The {@link org.opengis.referencing.crs.CoordinateReferenceSystem} for the image data of the pyramid.
     * @return The newly created pyramid.
     * @throws org.apache.sis.storage.DataStoreException If the given CRS is null or invalid.
     */
    TileMatrixSet createPyramid(final String layerName, final CoordinateReferenceSystem crs) throws DataStoreException {
        final XMLPyramid pyramid = new XMLPyramid(crs);
        pyramid.id = UUID.randomUUID().toString();
        pyramid.initialize(this);
        pyramids().add(pyramid);
        return pyramid;
    }

}

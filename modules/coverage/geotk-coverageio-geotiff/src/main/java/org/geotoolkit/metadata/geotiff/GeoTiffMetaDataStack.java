/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.metadata.geotiff;

import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.GeoTIFFTagSet;
import java.util.Collection;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.util.NullArgumentException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static org.geotoolkit.metadata.geotiff.GeoTiffMetaDataUtils.*;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;
import static org.geotoolkit.util.DomUtilities.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class GeoTiffMetaDataStack {

    private final Node tiffTree;
    private final Element ifd;

    //what needs to be written when flush is called
    private final List<KeyDirectoryEntry> entries = new ArrayList<KeyDirectoryEntry>();
    private final StringBuilder asciiValues = new StringBuilder();
    private final List<Double> doubleValues = new ArrayList<Double>();
    private final List<TiePoint> tiePoints = new ArrayList<TiePoint>();
    private Node nPixelScale = null;
    private Node nTransform = null;

    GeoTiffMetaDataStack(Node tiffTree) {
        if(tiffTree == null){
            throw new NullArgumentException("Tiff metadata tree can not be null.");
        }
        this.tiffTree = tiffTree;
        this.ifd = (Element)getNodeByLocalName(tiffTree, TAG_GEOTIFF_IFD);

        //remove previous tags if exists
        final Node nAscii = getNodeByNumber(ifd, getGeoAsciiParamsTag().getNumber());
        if(nAscii != null){
            ifd.removeChild(nAscii);
        }

        final Node nDoubles = getNodeByNumber(ifd, getGeoDoubleParamsTag().getNumber());
        if(nDoubles != null){
            ifd.removeChild(nDoubles);
        }
    }

    void addShort(int keyId, int value){
        final KeyDirectoryEntry entry = new KeyDirectoryEntry(keyId, 0, 1, value);
        entries.add(entry);
    }

    void addDouble(int keyID, double value) {        
        final KeyDirectoryEntry entry = new KeyDirectoryEntry(
                keyID, 
                getGeoDoubleParamsTag().getNumber(), 
                1,
                doubleValues.size());
        entries.add(entry);
        
        doubleValues.add(value);
    }

    void addAscii(int keyID, String value) {
        // +1 for the '|' character to be appended
        final int lenght = value.length() + 1;
        final KeyDirectoryEntry entry = new KeyDirectoryEntry(
                keyID, 
                getGeoAsciiParamsTag().getNumber(), 
                lenght, 
                asciiValues.length());
        entries.add(entry);
        
        asciiValues.append(value);
        asciiValues.append('|');
    }

    void setModelPixelScale(double x, double y, double z) {
        nPixelScale = createTiffField(getModelPixelScaleTag());
        nPixelScale.appendChild(createTiffDoubles(x,y,z));
    }

    void addModelTiePoint(TiePoint tp) {
        tiePoints.add(tp);
    }

    void setModelTransformation(final AffineTransform gridToCRS) {
        // See pag 28 of the spec for an explanation
        final double[] modelTransformation = new double[16];
        modelTransformation[0] = gridToCRS.getScaleX();
        modelTransformation[1] = gridToCRS.getShearX();
        modelTransformation[2] = 0;
        modelTransformation[3] = gridToCRS.getTranslateX();
        modelTransformation[4] = gridToCRS.getShearY();
        modelTransformation[5] = gridToCRS.getScaleY();
        modelTransformation[6] = 0;
        modelTransformation[7] = gridToCRS.getTranslateY();
        modelTransformation[8] = 0;
        modelTransformation[9] = 0;
        modelTransformation[10] = 0;
        modelTransformation[11] = 0;
        modelTransformation[12] = 0;
        modelTransformation[13] = 0;
        modelTransformation[14] = 0;
        modelTransformation[15] = 1;

        nTransform = createTiffField(getModelTransformationTag());
        final Node nValues = createTiffDoubles(modelTransformation);
        nTransform.appendChild(nValues);  
    }

    static Node createModelTransformationElement(double ... values) {
        final Node nTransformation = createTiffField(getModelTransformationTag());
        final Node nValues = createTiffDoubles(values);
        nTransformation.appendChild(nValues);
        return nTransformation;
    }

    static Node createModelTiePointsElement(Collection<? extends TiePoint> tiePoints) {
        final Node nTiePoints = createTiffField(getModelTiePointTag());
        final Node nValues = createNode(TAG_GEOTIFF_DOUBLES);
        nTiePoints.appendChild(nValues);

        for(final TiePoint tp : tiePoints) {
            nValues.appendChild(createTiffDouble(tp.rasterI));
            nValues.appendChild(createTiffDouble(tp.rasterJ));
            nValues.appendChild(createTiffDouble(tp.rasterK));
            nValues.appendChild(createTiffDouble(tp.coverageX));
            nValues.appendChild(createTiffDouble(tp.coverageY));
            nValues.appendChild(createTiffDouble(tp.coverageZ));
        }
        return nTiePoints;
    }


    /**
     * Write all stored informations in the tiff metadata tree.
     */
    void flush(){

        //write GeoKeyDirectory
        //first line (4 int) contain the version and number of keys
        //Header={KeyDirectoryVersion, KeyRevision, MinorRevision, NumberOfKeys}
        final int[] values = new int[4 + 4*entries.size()];
        values[0] = GEOTIFF_VERSION;
        values[1] = REVISION_MAJOR;
        values[2] = REVISION_MINOR;
        values[3] = entries.size();
        for(int i=0,l=4,n=entries.size(); i<n; i++,l+=4){
            final KeyDirectoryEntry entry = entries.get(i);
            values[l]   = entry.valueKey;
            values[l+1] = entry.valuelocation;
            values[l+2] = entry.valueNb;
            values[l+3] = entry.valueOffset;
        }

        final Node nGeoKeyDir = createTiffField(getGeoKeyDirectoryTag());
        nGeoKeyDir.appendChild(createTiffShorts(values));
        ifd.appendChild(nGeoKeyDir);

        //write tagsets
        ifd.setAttribute(ATT_TAGSETS,
                BaselineTIFFTagSet.class.getName() + ","
                + GeoTIFFTagSet.class.getName());

        if(nPixelScale != null){
            ifd.appendChild(nPixelScale);
        }

        if(!tiePoints.isEmpty()){
            ifd.appendChild(createModelTiePointsElement(tiePoints));
        }else if(nTransform != null) {
            ifd.appendChild(nTransform);
        }

        if(!doubleValues.isEmpty()){
            final Node nDoubles = createTiffField(getGeoDoubleParamsTag());
            final Node nValues = createTiffDoubles(doubleValues);
            nDoubles.appendChild(nValues);
            ifd.appendChild(nDoubles);
        }

        if(asciiValues.length() > 0){
            final Node nAsciis = createTiffField(getGeoAsciiParamsTag());
            final Node nValues = createTiffAsciis(asciiValues.toString());
            nAsciis.appendChild(nValues);
            ifd.appendChild(nAsciis);
        }

    }

}

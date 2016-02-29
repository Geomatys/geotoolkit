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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.sis.util.ArgumentChecks;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static org.geotoolkit.metadata.geotiff.GeoTiffMetaDataUtils.*;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;
import static org.geotoolkit.util.DomUtilities.*;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.NullArgumentException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 *
 * note : with java 9 class will become only accessible by its module (no public signature on class header).
 */
public final class GeoTiffMetaDataStack {

    /**
     * Date formatter to format in accordance with tiff specification.<br><br>
     *
     * More informations at : http://www.awaresystems.be/imaging/tiff/tifftags/datetime.html.
     */
    private static final SimpleDateFormat S_DATE_FORMAT = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    static {
        //Force removing timezone
        S_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private final Element ifd;

    //what needs to be written when flush is called
    private final List<KeyDirectoryEntry> entries = new ArrayList<KeyDirectoryEntry>();
    private final StringBuilder asciiValues       = new StringBuilder();
    private final List<Double> doubleValues       = new ArrayList<Double>();
    private final List<TiePoint> tiePoints        = new ArrayList<TiePoint>();
    private Node nPixelScale                      = null;
    private Node nTransform                       = null;
    private List<Node> noDatas                    = new ArrayList<Node>();
    private Node minSampleValue                   = null;
    private Node maxSampleValue                   = null;
    private Node date                             = null;

    public GeoTiffMetaDataStack(Node tiffTree) {
        ensureNonNull("tiffTree", tiffTree);

        Element tmpIfd = (Element) getNodeByLocalName(tiffTree, TAG_GEOTIFF_IFD);
        if (tmpIfd == null) {
            ifd = (Element) tiffTree.appendChild(createNode(TAG_GEOTIFF_IFD));
        } else {
            ifd = tmpIfd;
        }

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

    void addShort(final int keyId, final int value){
        final KeyDirectoryEntry entry = new KeyDirectoryEntry(keyId, 0, 1, value);
        entries.add(entry);
    }

    void addDouble(final int keyID, final double value) {
        final KeyDirectoryEntry entry = new KeyDirectoryEntry(
                keyID,
                getGeoDoubleParamsTag().getNumber(),
                1,
                doubleValues.size());
        entries.add(entry);

        doubleValues.add(value);
    }

    void addAscii(final int keyID, final String value) {
        //-- if already exist data separate by a "|"
        if (asciiValues.length() > 0) asciiValues.append('|');

        final KeyDirectoryEntry entry = new KeyDirectoryEntry(
                keyID,
                getGeoAsciiParamsTag().getNumber(),
                value.length(),
                asciiValues.length());
        entries.add(entry);
        asciiValues.append(value);
    }

    void setModelPixelScale(final double x, final double y, final double z) {
        nPixelScale = createTiffField(getModelPixelScaleTag());
        nPixelScale.appendChild(createTiffDoubles(x,y,z));
    }

    /**
     * Set Nodata values into this {@link GeoTiffMetaDataStack} in aim of build or write metadata.
     *
     * @param noDataValue expected setted nodata.
     * @throws NullArgumentException if noDataValue is {@code null}.
     * @throws IllegalArgumentException if noDataValue is empty.
     */
    void setNoData(final String noDataValue) {
        ArgumentChecks.ensureNonNull("noDataValue", noDataValue);
        if (noDataValue.isEmpty())
            throw new IllegalArgumentException("GeotiffMetadataStack : you try to "
                    + "set an empty Nodata Value (String) into metadata tree node.");
        Node noData = createTiffField(GeoTiffConstants.GDAL_NODATA_KEY, "noData");
        noData.appendChild(createTiffAsciis(noDataValue));
        noDatas.add(noData);
    }

    /**
     * Set minimum sample values into this {@link GeoTiffMetaDataStack} in aim of build or write metadata.
     *
     * @param minimumSampleValues minimum sample value for each image bands.
     * @throws NullArgumentException if maximumSampleValues array is {@code null}.
     */
    void setMinSampleValue(final int ...minimumSampleValues) {
        minSampleValue = createTiffField(GeoTiffConstants.MinSampleValue, "minSampleValue");
        minSampleValue.appendChild(createTiffShorts(minimumSampleValues));
    }

    /**
     * Set maximum sample values into this {@link GeoTiffMetaDataStack} in aim of build or write metadata.
     *
     * @param maximumSampleValue maximum sample value for each image bands.
     * @throws NullArgumentException if maximumSampleValues array is {@code null}.
     */
    void setMaxSampleValue(final int ...maximumSampleValues) {
        maxSampleValue = createTiffField(GeoTiffConstants.MaxSampleValue, "maxSampleValue");
        maxSampleValue.appendChild(createTiffShorts(maximumSampleValues));
    }

    /**
     * Set date into this {@link GeoTiffMetaDataStack} in aim of build or write metadata.
     *
     * @param date which be set into metadata node.
     * @throws NullArgumentException if date is {@code null}.
     */
    synchronized void setDate(final Date date) {
        ArgumentChecks.ensureNonNull("date", date);
        this.date = createTiffField(GeoTiffConstants.DateTime, "date");
        final String sdat = S_DATE_FORMAT.format(date);
        this.date.appendChild(createTiffAsciis(sdat));
    }

    void addModelTiePoint(final TiePoint tp) {
        tiePoints.add(tp);
    }

    public void setModelTransformation(final AffineTransform gridToCRS) {
        // See pag 28 of the spec for an explanation
        final double[] modelTransformation = new double[16];
        modelTransformation[0]  = gridToCRS.getScaleX();
        modelTransformation[1]  = gridToCRS.getShearX();
        modelTransformation[2]  = 0;
        modelTransformation[3]  = gridToCRS.getTranslateX();
        modelTransformation[4]  = gridToCRS.getShearY();
        modelTransformation[5]  = gridToCRS.getScaleY();
        modelTransformation[6]  = 0;
        modelTransformation[7]  = gridToCRS.getTranslateY();
        modelTransformation[8]  = 0;
        modelTransformation[9]  = 0;
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

    static Node createModelTransformationElement(final double ... values) {
        final Node nTransformation = createTiffField(getModelTransformationTag());
        final Node nValues = createTiffDoubles(values);
        nTransformation.appendChild(nValues);
        return nTransformation;
    }

    static Node createModelTiePointsElement(final Collection<? extends TiePoint> tiePoints) {
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
    public void flush(){

        if (!entries.isEmpty()) {
            //write GeoKeyDirectory
            //first line (4 int) contain the version and number of keys
            //Header={KeyDirectoryVersion, KeyRevision, MinorRevision, NumberOfKeys}
            final int[] values = new int[4 + 4*entries.size()];
            values[0] = GEOTIFF_VERSION;
            values[1] = REVISION_MAJOR;
            values[2] = REVISION_MINOR;
            values[3] = entries.size();
            for (int i = 0, l = 4, n = entries.size(); i < n; i++, l += 4) {
                final KeyDirectoryEntry entry = entries.get(i);
                values[l]   = entry.valueKey;
                values[l+1] = entry.valuelocation;
                values[l+2] = entry.valueNb;
                values[l+3] = entry.valueOffset;
            }

            final Node nGeoKeyDir = createTiffField(getGeoKeyDirectoryTag());
            nGeoKeyDir.appendChild(createTiffShorts(values));
            ifd.appendChild(nGeoKeyDir);
        }

        //write tagsets
        ifd.setAttribute(ATT_TAGSETS,
                BaselineTIFFTagSet.class.getName() + ","
                + GeoTIFFTagSet.class.getName());

        if (nPixelScale != null) ifd.appendChild(nPixelScale);

        if (!tiePoints.isEmpty()) {
            ifd.appendChild(createModelTiePointsElement(tiePoints));
        } else if (nTransform != null) {
            ifd.appendChild(nTransform);
        }

        if (minSampleValue != null) ifd.appendChild(minSampleValue);

        if (maxSampleValue != null) ifd.appendChild(maxSampleValue);

        if (!noDatas.isEmpty())
            for (Node nd : noDatas) ifd.appendChild(nd);

        if (date != null)
            ifd.appendChild(date);

        if (!doubleValues.isEmpty()) {
            final Node nDoubles = createTiffField(getGeoDoubleParamsTag());
            final Node nValues = createTiffDoubles(doubleValues);
            nDoubles.appendChild(nValues);
            ifd.appendChild(nDoubles);
        }

        if (asciiValues.length() > 0) {
            final Node nAsciis = createTiffField(getGeoAsciiParamsTag());
            final Node nValues = createTiffAsciis(asciiValues.toString());
            nAsciis.appendChild(nValues);
            ifd.appendChild(nAsciis);
        }
    }
}

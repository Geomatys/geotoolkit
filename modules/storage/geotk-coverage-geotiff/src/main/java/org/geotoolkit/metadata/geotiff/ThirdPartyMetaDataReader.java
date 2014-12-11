/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.resources.Vocabulary;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Tiff format may contain multiple additional tags.
 * Some of them can have valuable metadata or style informations.
 * 
 * Various lists of possible tags :
 * http://search.cpan.org/dist/Image-MetaData-JPEG/lib/Image/MetaData/JPEG/TagLists.pod
 * http://www.awaresystems.be/imaging/tiff/tifftags/private.html
 * http://www.awaresystems.be/imaging/tiff/tifftags/privateifd.html
 * 
 * @author Johann Sorel  (Geomatys)
 * @author Marechal Remi (Geomatys).
 */
public strictfp class ThirdPartyMetaDataReader {

    private static final Logger LOGGER = Logging.getLogger(ThirdPartyMetaDataReader.class);

    private final Node root;
    
    public ThirdPartyMetaDataReader(final IIOMetadata imageMetadata) throws IOException{
        root = imageMetadata.getAsTree(imageMetadata.getNativeMetadataFormatName());
        if(root == null) throw new IOException("No image metadatas");
    }
    
    /**
     * Fill the given Spatial Metadatas with additional informations.
     */
    public void fillSpatialMetaData(SpatialMetadata metadata) throws IOException {
        double[] realFillValue = null;
        
        int samplePerPixels = -1;
        int bitsPerSamples = -1;
        int sampleFormat = -1;
        
        long[] minSampleValues = null;
        long[] maxSampleValues = null;
        double[] gDalMinSampleValue = null;
        double[] gDalMaxSampleValue = null;
        
        double[] modelTransformation = null;
        double[] modelPixelScale = null;
        double scaleZ  = 1;
        double offsetZ = 0;
        boolean scaleFound = false;
        
        //-- get sample per pixel and scale / offset
        final NodeList children = root.getChildNodes();
        for(int i = 0, n = children.getLength(); i < n; i++) {
            final IIOMetadataNode child = (IIOMetadataNode) children.item(i);
            final int number = Integer.valueOf(child.getAttribute("number"));
            switch (number) {
                case 277 : { //-- samples per pixel
                    final Node sppNode = child.getChildNodes().item(0);
                    samplePerPixels = (int) GeoTiffMetaDataUtils.readTiffLongs(sppNode)[0];
                    break;
                }
                case 34264 : { //-- ModelTransformationTag
                    scaleFound = true;
                    final Node mdTransNode = child.getChildNodes().item(0);
                    modelTransformation = GeoTiffMetaDataUtils.readTiffDoubles(mdTransNode);
                    
                   /*
                    * get pixelScaleZ at coordinate 10 and pixelOffsetZ at coordinate 11.
                    * always array of length 16 like follow.
                    * |Sx, 0, 0, Tx|
                    * |0, Sy, 0, Ty|
                    * |0, 0, Sz, Tz|
                    * |0, 0,  0, 1 |
                    */
                    //-- scaleZ
                    if (StrictMath.abs(modelTransformation[10]) > 1E-9) {
                        if (modelPixelScale != null) assert StrictMath.abs(StrictMath.abs(modelTransformation[10]) - StrictMath.abs(modelPixelScale[2])) < 1E-9;
                        scaleZ = modelTransformation[10];
                    }
                    offsetZ = modelTransformation[11];
                    break;
                }
            }
            if (scaleFound && samplePerPixels != -1) break;
        }
        
        for(int i = 0, n = children.getLength(); i < n; i++){
            final IIOMetadataNode child = (IIOMetadataNode) children.item(i);
            final int number = Integer.valueOf(child.getAttribute("number"));
            
            switch (number) {
                //-- apparemment meme categories pour tout les gridSampleDimensions ...??? (à definir pas rencontrer pour le moment).
                //GDAL tags
                case 42112 :  {//metadatas as xml
                    final Node valueNode = child.getChildNodes().item(0);
                    String stats = GeoTiffMetaDataUtils.readTiffAsciis(valueNode);
                    stats = stats.replaceAll(">( |\n|\t)*<", "><").trim();
                    Double min = null;
                    Double max = null;
                    try {
                        final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                        final Document doc = db.parse(new ByteArrayInputStream(stats.getBytes())); 
                        final NodeList lst = doc.getElementsByTagName("Item");
                        for (int k = 0, kn = lst.getLength(); k < kn; k++) {
                            final Element node = (Element) lst.item(k);
                            final String name  = node.getAttribute("name");
                            if ("STATISTICS_MINIMUM".equals(name)) {
                                min = Double.valueOf(node.getTextContent());
                            } else if ("STATISTICS_MAXIMUM".equals(name)) {
                                max = Double.valueOf(node.getTextContent());
                            }
                        }
                        
                       /*
                        * Pour le moment fill les valeurs. Peut etre GDAL defini un min et max par bands.
                        * Pas encore rencontré.
                        * Tableau en prevision.
                        */
                        if (min != null && max != null) {
                            gDalMinSampleValue = new double[samplePerPixels];
                            Arrays.fill(gDalMinSampleValue, min);
                            gDalMaxSampleValue = new double[samplePerPixels];
                            Arrays.fill(gDalMaxSampleValue, max);
                        }
                    } catch (ParserConfigurationException | SAXException | NumberFormatException ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    }
                    break;
                }
                case 42113 : {// no data value as ascii text
                   /* 
                    * Il est possible avec geotiff que des nodata differents soient present pour chaque bandes de l'image.
                    * On peut donc potentiellement avoir un nodata different sur chacune de nos bandes -> String[].
                    * (Jamais rencontrer encore a voir ...)
                    */
                    final Node valueNode = child.getChildNodes().item(0);
                    String str = GeoTiffMetaDataUtils.readTiffAsciis(valueNode);
                    if (str.contains(",")) {
                        final String[] strs = str.split(",");
                        assert strs.length == 2 : "Unformat nodata string value. Found : ("+str+")";
                        str = strs[0] +"."+ strs[1];
                    }
                    try {
                        double realFillVal;
                        if (str.trim().equalsIgnoreCase("nan")) {
                            realFillVal = Double.NaN;
                        } else {
                            realFillVal = Double.valueOf(str).doubleValue(); 
                        }
//                        final double realFillVal = Double.valueOf(str).doubleValue(); 
                        realFillValue = new double[samplePerPixels];
                        Arrays.fill(realFillValue, realFillVal);
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.INFO, "No data value cannot be read.", e);
                    }
                    break;
                }
                case 33550 : { //-- ModelPixelScaleTag
                    final Node mdptNode = child.getChildNodes().item(0);
                    modelPixelScale = GeoTiffMetaDataUtils.readTiffDoubles(mdptNode);
                    //-- get pixel scale Z. Always array of length 3 (ScaleX, ScaleY, ScaleZ).
                    if (StrictMath.abs(modelPixelScale[2]) > 1E-9) {
                        if (modelTransformation != null) assert StrictMath.abs(StrictMath.abs(modelTransformation[10]) - StrictMath.abs(modelPixelScale[2])) < 1E-9;
                        scaleZ = modelPixelScale[2];
                    }
                    break;
                }
                case 258 : { //-- bits per sample
                    final Node bpsNode = child.getChildNodes().item(0);
                    bitsPerSamples = (int) GeoTiffMetaDataUtils.readTiffLongs(bpsNode)[0];
                    break;
                }
                case 339 : { //-- sample format 
                    final Node sfNode = child.getChildNodes().item(0);
                    sampleFormat = (int) GeoTiffMetaDataUtils.readTiffLongs(sfNode)[0];
                    break;
                }
                case 280 : { //-- min sample Value
                    final Node sppNode = child.getChildNodes().item(0);
                    minSampleValues =  GeoTiffMetaDataUtils.readTiffLongs(sppNode);
                    break;
                }
                case 281 : { //-- max sample value
                    final Node sfNode = child.getChildNodes().item(0);
                    maxSampleValues = GeoTiffMetaDataUtils.readTiffLongs(sfNode);
                    break;
                }
            }
        }
        
        if (realFillValue == null && gDalMinSampleValue == null && gDalMaxSampleValue == null && minSampleValues == null && maxSampleValues == null) return;
        
        final DimensionAccessor accessor = new DimensionAccessor(metadata);
        
        assert bitsPerSamples  != -1;
        assert samplePerPixels != -1;
        
        double[] minSV = new double[samplePerPixels];
        double[] maxSV = new double[samplePerPixels];
        
        if (minSampleValues != null && maxSampleValues != null) {  
            assert minSampleValues.length == samplePerPixels;            
            assert maxSampleValues.length == samplePerPixels;
            for (int i = 0; i < samplePerPixels; i++) {
                minSV[i] = minSampleValues[i];
                maxSV[i] = maxSampleValues[i];
            }
        } else if (gDalMinSampleValue != null) {
            minSV = gDalMinSampleValue;
            maxSV = gDalMaxSampleValue;
        } else {
           /*
            * If min and max sample values are not stipulate in metadata, we assume 
            * that min and max interval is with exclusives terminal because in lot of case 
            * the "No category Data" has often a value at interval terminals.
            */
                double min;
                double max;
                switch (bitsPerSamples) {
                    case Byte.SIZE : {
                        min = 0;
                        max = 255;
                        break;
                    }
                    case Short.SIZE : {
                        if (sampleFormat == 2) {
                            min = -32768;
                            max = 32767;
                        } else {
                            min = 0;
                            max = 0xFFFF;
                        }
                        break;
                    }
                    case Integer.SIZE : {
                        if (sampleFormat == 3) {
                            //-- Float
                            min = Float.MIN_VALUE;
                            max = Float.MAX_VALUE;
                        } else {
                            //-- integer
                            min = Integer.MIN_VALUE;
                            max = Integer.MAX_VALUE;
                        }
                        break;
                    }
                    case Double.SIZE : {
                        min = Double.MIN_VALUE;
                        max = Double.MAX_VALUE;
                        break;
                    }
                    default : throw new IllegalStateException("Unknow sample type");
                }
                Arrays.fill(minSV, min);
                Arrays.fill(maxSV, max);
        } 
        
        for (int b = 0; b < samplePerPixels; b++) {
            if (accessor.childCount() >= samplePerPixels) {
                accessor.selectChild(b); 
            } else {
                accessor.selectChild(accessor.appendChild());
            }
            
            final List<Category> categories = new ArrayList<>();
            if (realFillValue != null) {
                    categories.add(new Category(Vocabulary.formatInternational(Vocabulary.Keys.NODATA), new Color(0,0,0,0), realFillValue[b]));
                    if (minSV[b] < realFillValue[b] && realFillValue[b] < maxSV[b]) {
                        categories.add(new Category("data", null, 
                            NumberRange.create(minSV[b], true, realFillValue[b], false), scaleZ, offsetZ));
                        categories.add(new Category("data", null, 
                            NumberRange.create(realFillValue[b], false, maxSV[b], true), scaleZ, offsetZ));
                    } else {
                        categories.add(new Category("data", null, 
                            NumberRange.create(minSV[b], !(minSV[b] == realFillValue[b]), maxSV[b], !(maxSV[b] == realFillValue[b])), scaleZ, offsetZ));
                    }
                } else {
                    categories.add(new Category("data", null, 
                            NumberRange.create(minSV[b], true, maxSV[b], true), scaleZ, offsetZ));
                }
            final GridSampleDimension dim = new GridSampleDimension(""+b, categories.toArray(new Category[categories.size()]), null);
            accessor.setDimension(dim, Locale.ENGLISH);
            if (realFillValue != null) {
                accessor.setAttribute("realFillValue", realFillValue[b]);
            }
        }
    }
}

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
public class ThirdPartyMetaDataReader {

    private static final Logger LOGGER = Logging.getLogger(ThirdPartyMetaDataReader.class);

    private final Node root;
    
    public ThirdPartyMetaDataReader(final IIOMetadata imageMetadata) throws IOException{
        root = imageMetadata.getAsTree(imageMetadata.getNativeMetadataFormatName());
        if(root == null) throw new IOException("No image metadatas");
    }
    
    /**
     * Fill the given Spatial Metadatas with additional informations.
     */
    public void fillSpatialMetaData(SpatialMetadata metadata) throws IOException{
        
        final List<Category> categories = new ArrayList<>();
        Category noDataCategory = null;
        Double realFillValue = null;
        
        int samplePerPixels = -1;
        int bitsPerSamples = -1;
        int sampleFormat = -1;
        
        final NodeList children = root.getChildNodes();
        for(int i=0, n=children.getLength(); i<n; i++){
            final IIOMetadataNode child = (IIOMetadataNode) children.item(i);
            final int number = Integer.valueOf(child.getAttribute("number"));
            
            switch (number) {

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
                        for(int k=0,kn=lst.getLength();k<kn;k++){
                            final Element node = (Element) lst.item(k);
                            final String name  = node.getAttribute("name");
                            if ("STATISTICS_MINIMUM".equals(name)) {
                                min = Double.valueOf(node.getTextContent());
                            } else if ("STATISTICS_MAXIMUM".equals(name)) {
                                max = Double.valueOf(node.getTextContent());
                            }
                            if (min != null && max != null) {
                                categories.add(new Category("data", null, 
                                        NumberRange.create(1, true, 100, true), NumberRange.create(min, true, max, true)));
                            }
                        }
                    } catch (ParserConfigurationException | SAXException | NumberFormatException ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    }
                    break;
                }
                case 42113 : {// no data value as ascii text
                    final Node valueNode = child.getChildNodes().item(0);
                    final String str = GeoTiffMetaDataUtils.readTiffAsciis(valueNode);
                    try {
                        realFillValue = Double.valueOf(str);
                        noDataCategory = Category.NODATA;
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.INFO, "No data value cannot be read.", e);
                    }
                    //categories.add(new Category(Vocabulary.formatInternational(Vocabulary.Keys.NODATA), new Color(0,0,0,0), noData));
                    break;
                }
                case 258 : { //-- bits per sample
                    final Node bpsNode = child.getChildNodes().item(0);
                    bitsPerSamples = (int) GeoTiffMetaDataUtils.readTiffLongs(bpsNode)[0];
                    break;
                }
                case 277 : { //-- samples per pixel
                    final Node sppNode = child.getChildNodes().item(0);
                    samplePerPixels = (int) GeoTiffMetaDataUtils.readTiffLongs(sppNode)[0];
                    break;
                }
                case 339 : { //-- sample format 
                    final Node sfNode = child.getChildNodes().item(0);
                    sampleFormat = (int) GeoTiffMetaDataUtils.readTiffLongs(sfNode)[0];
                    break;
                }
            }
        }
        
        assert bitsPerSamples  != -1;
        assert samplePerPixels != -1;
        
        final DimensionAccessor accessor = new DimensionAccessor(metadata);
        final int categoriArrayLength = (noDataCategory != null) ? 2 : 1;
        if (categories.isEmpty()) {
            double min;
            double max;
            switch (bitsPerSamples) {
                case Byte.SIZE : {
                    min = 0;
                    max = 255;
                    break;
                }
                case Short.SIZE : {
                    min = 0;
                    max = 0xFFFF;
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
            for (int s = 0; s < samplePerPixels; s++) {
                categories.add(new Category("data", null, 
                                        NumberRange.create(1, true, 100, true), NumberRange.create(min, true, max, true)));
            }
        } 
        
        final int categoriesNumber = categories.size();
        for (int c = 0; c < categoriesNumber; c++) {
            if (accessor.childCount() >= categoriesNumber) {
                accessor.selectChild(c); 
            } else {
                accessor.selectChild(accessor.appendChild());
            }

            final Category[] cats = new Category[categoriArrayLength];
            cats[0] = categories.get(c);
            if (noDataCategory != null) cats[1] = noDataCategory;
            final GridSampleDimension dim = new GridSampleDimension(""+c, cats, null);
            accessor.setDimension(dim, Locale.ENGLISH);
            if (realFillValue != null) {
                accessor.setAttribute("realFillValue", realFillValue);
            }
        }    
    }
}

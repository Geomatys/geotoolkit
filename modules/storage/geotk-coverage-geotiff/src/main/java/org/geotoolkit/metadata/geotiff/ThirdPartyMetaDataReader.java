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
 * @author Johann Sorel (Geomatys)
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
        Double realFillValue = null;
        
        final NodeList children = root.getChildNodes();
        for(int i=0,n=children.getLength();i<n;i++){
            final IIOMetadataNode child = (IIOMetadataNode) children.item(i);
            final int number = Integer.valueOf(child.getAttribute("number"));
            
            switch(number){

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
                            final String name = node.getAttribute("name");
                            if("STATISTICS_MINIMUM".equals(name)){
                                min = Double.valueOf(node.getTextContent());
                            }else if("STATISTICS_MAXIMUM".equals(name)){
                                max = Double.valueOf(node.getTextContent());
                            }
                        }
                    } catch (ParserConfigurationException | SAXException | NumberFormatException ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    }
                    
                    if(min!=null && max!=null){
                        categories.add(new Category("data", null, 
                                NumberRange.create(1, true, 100, true), NumberRange.create(min, true, max, true)));
                    }
                    
                    }break;
                case 42113 : {// no data value as ascii text
                    final Node valueNode = child.getChildNodes().item(0);
                    final String str = GeoTiffMetaDataUtils.readTiffAsciis(valueNode);
                    try {
                        realFillValue = Double.valueOf(str);
                        categories.add(Category.NODATA);
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.INFO, "No data value cannot be read.", e);
                    }
                    //categories.add(new Category(Vocabulary.formatInternational(Vocabulary.Keys.NODATA), new Color(0,0,0,0), noData));
                    }break;
            }
            
        }
        
        
        if(!categories.isEmpty()){
            final DimensionAccessor accessor = new DimensionAccessor(metadata);
            if(accessor.childCount()>0){
                accessor.selectChild(0);
            }else{
                accessor.selectChild(accessor.appendChild());
                final GridSampleDimension dim = new GridSampleDimension("samples", categories.toArray(new Category[0]), null);
                accessor.setDimension(dim, Locale.ENGLISH);
            }
            if(realFillValue!=null){
                accessor.setAttribute("realFillValue", realFillValue);
            }
        }
                
    }
    
}

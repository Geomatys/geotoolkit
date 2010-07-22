/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.gx.xml;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.gx.model.GxModelConstants;
import org.geotoolkit.data.gx.model.PlayList;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.StaxStreamWriter;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import static org.geotoolkit.data.gx.xml.GxConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class GxWriter extends StaxStreamWriter{

    private String URI_KML = KmlConstants.URI_KML_2_2;
    private final KmlWriter kmlWriter = new KmlWriter();

    /**
     *
     */
    public GxWriter(){
        super();
    }

    /**
     * <p>Set output. This method use kml uri version 2.2.</p>
     *
     * @param output
     * @throws XMLStreamException
     * @throws IOException
     */
    @Override
    public void setOutput(Object output) throws XMLStreamException, IOException{
        super.setOutput(output);
        try {
            this.kmlWriter.setOutput(writer, URI_KML);
        } catch (KmlException ex) {
            Logger.getLogger(GxWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param tour
     * @throws XMLStreamException
     * @throws KmlException
     */
    public void writeTour(Feature tour)
            throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_GX, TAG_TOUR);
        this.kmlWriter.writeCommonAbstractFeature(tour);
        if(tour.getProperties(GxModelConstants.ATT_TOUR_PLAY_LIST.getName()) != null){
            Iterator i = tour.getProperties(GxModelConstants.ATT_TOUR_PLAY_LIST.getName()).iterator();
            while(i.hasNext()){
                this.writePlayList((PlayList) ((Property) i.next()).getValue());
            }
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param playlist
     * @throws XMLStreamException
     */
    private void writePlayList(PlayList playlist) throws XMLStreamException{
        writer.writeStartElement(URI_GX, TAG_PLAYLIST);
        writer.writeEndElement();
    }

}

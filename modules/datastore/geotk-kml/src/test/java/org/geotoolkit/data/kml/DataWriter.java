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
package org.geotoolkit.data.kml;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.Extensions.Names;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.xml.KmlExtensionWriter;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import org.geotoolkit.xml.StaxStreamWriter;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DataWriter extends StaxStreamWriter implements KmlExtensionWriter{

    private static final String URI_DATA = "http://www.sandres.com";

    public DataWriter(){
        super();
    }

    /**
     *
     * @param output
     * @throws XMLStreamException
     * @throws IOException
     */
    @Override
    public void setOutput(Object output) throws XMLStreamException, IOException{
        super.setOutput(output);
    }

    /**
     *
     * @param racine
     */
    public void write(List<String> racine) {
        try {
            writer.writeStartDocument("UTF-8", "1.0");
            writer.setDefaultNamespace(URI_DATA);
            this.writeRacine(racine);
            writer.writeEndDocument();
            writer.flush();

        } catch (KmlException ex) {
            Logger.getLogger(KmlWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(KmlWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param racine
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeRacine(List<String> racine) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_DATA,"racine");
        for(String element : racine){
            this.writeElement(element);
        }
        writer.writeEndElement();
    }

    public void writeElement(String element) throws XMLStreamException{
            writer.writeStartElement(URI_DATA,"element");
            writer.writeCharacters(element);
            writer.writeEndElement();
    }

    @Override
    public boolean canHandleComplex(String kmlVersionUri, Names ext, Object contentObject) {
        if(contentObject instanceof List){
            return true;
        }
        return false;
    }

    @Override
    public boolean canHandleSimple(String kmlVersionUri, Names ext, String elementTag) {
        if("element".equals(elementTag)){
            return true;
        }
        return false;
    }

    @Override
    public void writeComplexExtensionElement(String kmlVersionUri, Extensions.Names ext, Object contentElement)
            throws XMLStreamException, KmlException {
        if(contentElement instanceof List){
            this.writeRacine((List<String>) contentElement);
        }
    }

    @Override
    public void writeSimpleExtensionElement(String kmlVersionUri, Extensions.Names ext, SimpleTypeContainer contentElement)
            throws XMLStreamException, KmlException {
        if("element".equals(contentElement.getTagName())){
            this.writeElement(URI_DATA);
        }
    }

}

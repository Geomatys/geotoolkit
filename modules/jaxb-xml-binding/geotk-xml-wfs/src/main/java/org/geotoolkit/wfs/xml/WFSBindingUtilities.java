/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.wfs.xml;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.wfs.xml.v110.WFSCapabilitiesType;
import org.geotoolkit.xml.MarshallerPool;

import org.opengis.metadata.citation.OnlineResource;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WFSBindingUtilities {


     public static WFSCapabilitiesType unmarshall(final Object source, final WFSVersion version) throws JAXBException{

         final MarshallerPool pool = WFSMarshallerPool.getInstance();
         Unmarshaller unMarshaller = null;
         try {
             switch(version){
                 case v110 : break;
                 default: throw new IllegalArgumentException("unknonwed version");
             }
             unMarshaller = pool.acquireUnmarshaller();
             final Object unmarshalled = unmarshall(source, unMarshaller);
             if (unmarshalled instanceof JAXBElement) {
                 return (WFSCapabilitiesType) ((JAXBElement)unmarshalled).getValue();
             } else {
                return (WFSCapabilitiesType) unmarshalled;
             }
         } finally {
             if (unMarshaller != null) {
                 pool.release(unMarshaller);
             }
         }
     }

     private static final Object unmarshall(final Object source, final Unmarshaller unMarshaller)
            throws JAXBException{
        if(source instanceof File){
            return unMarshaller.unmarshal( (File)source );
        }else if(source instanceof InputSource){
            return unMarshaller.unmarshal( (InputSource)source );
        }else if(source instanceof InputStream){
            return unMarshaller.unmarshal( (InputStream)source );
        }else if(source instanceof Node){
            return unMarshaller.unmarshal( (Node)source );
        }else if(source instanceof Reader){
            return unMarshaller.unmarshal( (Reader)source );
        }else if(source instanceof Source){
            return unMarshaller.unmarshal( (Source)source );
        }else if(source instanceof URL){
            return unMarshaller.unmarshal( (URL)source );
        }else if(source instanceof XMLEventReader){
            return unMarshaller.unmarshal( (XMLEventReader)source );
        }else if(source instanceof XMLStreamReader){
            return unMarshaller.unmarshal( (XMLStreamReader)source );
        }else if(source instanceof OnlineResource){
            final OnlineResource online = (OnlineResource) source;
            try {
                final URL url = online.getLinkage().toURL();
                return unMarshaller.unmarshal(url);
            } catch (MalformedURLException ex) {
                Logging.getLogger(WFSBindingUtilities.class).log(Level.WARNING, null, ex);
                return null;
            }

        }else{
            throw new IllegalArgumentException("Source object is not a valid class :" + source.getClass());
        }

    }

}

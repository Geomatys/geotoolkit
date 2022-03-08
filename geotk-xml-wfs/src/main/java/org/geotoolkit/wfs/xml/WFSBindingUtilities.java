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
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import org.apache.sis.util.ArgumentChecks;

import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.ows.xml.ExceptionResponse;

import org.opengis.metadata.citation.OnlineResource;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WFSBindingUtilities {


     public static WFSCapabilities unmarshall(final Object source, final WFSVersion version) throws JAXBException{
         return unmarshall(source, version, WFSCapabilities.class);
     }

     public static <T> T unmarshall(final Object source, final WFSVersion version, final Class<T> dataType) throws JAXBException {
        ArgumentChecks.ensureNonNull("Return type", dataType);

        final MarshallerPool pool = WFSMarshallerPool.getInstance(version);

        final Unmarshaller unMarshaller = pool.acquireUnmarshaller();
        Object unmarshalled = unmarshall(source, unMarshaller);
        pool.recycle(unMarshaller);

        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement)unmarshalled).getValue();
        }

        if (unmarshalled == null) {
            throw new JAXBException("No value available in given data source");
        } else if (dataType.isAssignableFrom(unmarshalled.getClass())) {
            return (T) unmarshalled;
        } else if (unmarshalled instanceof ExceptionResponse) {
            throw new JAXBException(((ExceptionResponse)unmarshalled).toException());
        } else {
            throw new JAXBException(String.format(
                    "Read object is not compatible with queried type.%nExpected: %s%nBut was: %s",
                    dataType, unmarshalled.getClass()
            ));
        }
     }

     private static Object unmarshall(final Object source, final Unmarshaller unMarshaller)
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
                Logger.getLogger("org.geotoolkit.wfs.xml").log(Level.WARNING, null, ex);
                return null;
            }

        }else{
            throw new IllegalArgumentException("Source object is not a valid class :" + source.getClass());
        }

    }

}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.wms.xml;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.apache.sis.xml.MarshallerPool;

import org.opengis.metadata.citation.OnlineResource;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @module
 */
 public class WMSBindingUtilities {

     public static AbstractWMSCapabilities unmarshall(final Object source, final WMSVersion version) throws JAXBException, MalformedURLException {
        MarshallerPool selectedPool = WMSMarshallerPool.getInstance(version);
        Unmarshaller unMarshaller = selectedPool.acquireUnmarshaller();
        AbstractWMSCapabilities c = (AbstractWMSCapabilities) unmarshall(source, unMarshaller);
        selectedPool.recycle(unMarshaller);
        return c;
     }

     private static Object unmarshall(final Object source, final Unmarshaller unMarshaller)
            throws JAXBException, MalformedURLException{
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
            final URL url = online.getLinkage().toURL();
            return unMarshaller.unmarshal(url);
        }else{
            throw new IllegalArgumentException("Source object is not a valid class :" + source.getClass());
        }
    }

    public static void updateLayerURL(final String url, final AbstractLayer layer) {
        if (layer.getStyle() != null) {
            for (Style style : layer.getStyle()) {
                if (style.getLegendURL() != null) {
                    for (AbstractLegendURL legend : style.getLegendURL()) {
                        if (legend.getOnlineResource() != null
                                && legend.getOnlineResource().getHref() != null) {
                            final String legendURL = legend.getOnlineResource().getHref();
                            final int index = legendURL.indexOf('?');
                            if (index != -1) {
                                final String s = legendURL.substring(index + 1);
                                legend.getOnlineResource().setHref(url + s);
                            }
                        }
                    }
                }
            }
        }

        for (AbstractLayer childLayer : layer.getLayer()) {
            updateLayerURL(url, childLayer);
        }
    }

    /**
     * @return true if it founds the layer
     */
    public static boolean searchLayerByName(final List<AbstractLayer> stack, final AbstractLayer candidate, final String name){
        if(candidate == null){
            return false;
        }

        //add current layer in the stack
        stack.add(candidate);

        if(name.equals(candidate.getName())){
            return true;
        }

        //search it's children
        final List<? extends AbstractLayer> layers = candidate.getLayer();
        if(layers != null){
            for(AbstractLayer layer : layers){
                if(searchLayerByName(stack, layer, name)){
                    return true;
                }
            }
        }

        //we didn't find the searched layer in this layer, remove it from the stack
        stack.remove(stack.size()-1);
        return false;
    }

    public static void explore(List<AbstractLayer> buffer, AbstractLayer candidate){
        buffer.add(candidate);
        final List<? extends AbstractLayer> layers = candidate.getLayer();
        if(layers != null){
            for(AbstractLayer child : layers){
                explore(buffer, child);
            }
        }
    }
}

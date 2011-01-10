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
package org.geotoolkit.sld.xml;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;

import org.opengis.filter.FilterFactory2;
import org.opengis.util.FactoryException;
import org.opengis.sld.StyledLayerDescriptor;

/**
 * Utility class to read and write XML OGC SLD files.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JAXBSLDUtilities {

    private static final Logger LOGGER = Logging.getLogger(JAXBSLDUtilities.class);
    
    private final FilterFactory2 filterFactory;
    private final MutableStyleFactory styleFactory;
    private final MutableSLDFactory sldFactory;

    private static MarshallerPool POOL_100;
    private static MarshallerPool POOL_110;

    public static MarshallerPool getMarshallerPoolSLD100() {
        if (POOL_100 == null) {
            try {
                POOL_100 = new MarshallerPool(org.geotoolkit.sld.xml.v100.StyledLayerDescriptor.class);
            } catch (JAXBException ex) {
                throw new RuntimeException("Could not load jaxbcontext for sld 100.",ex);
            }
        }
        return POOL_100;
    }

    public static MarshallerPool getMarshallerPoolSLD110() {
        if (POOL_110 == null) {
            try {
                POOL_110 = new MarshallerPool(org.geotoolkit.sld.xml.v110.StyledLayerDescriptor.class, org.geotoolkit.internal.jaxb.geometry.ObjectFactory.class);
            } catch (JAXBException ex) {
                throw new RuntimeException("Could not load jaxbcontext for sld 100.",ex);
            }
        }
        return POOL_110;
    }
    
    
    public JAXBSLDUtilities(final FilterFactory2 filterFactory, final MutableStyleFactory styleFactory, final MutableSLDFactory sldFactory) {
        this.filterFactory = filterFactory;
        this.styleFactory = styleFactory;
        this.sldFactory = sldFactory;
    }

    //--------- SLD Version 1.0.0 ----------------------------------------------
    public  org.geotoolkit.sld.xml.v100.StyledLayerDescriptor transformV100(final StyledLayerDescriptor sld){
        return new GTtoSLD100Transformer().visit(sld, null);
    }
    
    public MutableStyledLayerDescriptor transformV100(final org.geotoolkit.sld.xml.v100.StyledLayerDescriptor sld){
        return new SLD100toGTTransformer(filterFactory,styleFactory,sldFactory).visit(sld);
    }
    
    public  org.geotoolkit.sld.xml.v100.StyledLayerDescriptor unmarshallV100(final File sldFile){
        org.geotoolkit.sld.xml.v100.StyledLayerDescriptor sld = null;
        
        try {
            final Unmarshaller unmarshaller = getMarshallerPoolSLD100().acquireUnmarshaller();
            try {
                sld = ( org.geotoolkit.sld.xml.v100.StyledLayerDescriptor) unmarshaller.unmarshal(sldFile);
            } finally {
                getMarshallerPoolSLD100().release(unmarshaller);
            }
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        
        return sld;
    }
    
    public File marshallV100( final org.geotoolkit.sld.xml.v100.StyledLayerDescriptor sld, final File sldFile) {
        
        try {
            final Marshaller marshaller = getMarshallerPoolSLD100().acquireMarshaller();
            try {
                marshaller.marshal(sld, sldFile);
            } finally {
                getMarshallerPoolSLD100().release(marshaller);
            }
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        
        return sldFile;
    }
        
    //--------- SLD Version 1.1.0 ----------------------------------------------   
    public org.geotoolkit.sld.xml.v110.StyledLayerDescriptor transformV110(final StyledLayerDescriptor sld){
        return new GTtoSLD110Transformer().visit(sld, null);
    }
    
    public MutableStyledLayerDescriptor transformV110(final org.geotoolkit.sld.xml.v110.StyledLayerDescriptor sld) throws FactoryException{
        return new SLD110toGTTransformer(filterFactory,styleFactory,sldFactory).visit(sld);
    }
    
    public org.geotoolkit.sld.xml.v110.StyledLayerDescriptor unmarshallV110(final File sldFile){
        org.geotoolkit.sld.xml.v110.StyledLayerDescriptor sld = null;
        
        try {
            final Unmarshaller unmarshaller = getMarshallerPoolSLD110().acquireUnmarshaller();
            try {
                sld = (org.geotoolkit.sld.xml.v110.StyledLayerDescriptor) unmarshaller.unmarshal(sldFile);
            } finally {
                getMarshallerPoolSLD110().release(unmarshaller);
            }
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        
        return sld;
    }
    
    public File marshallV110(final org.geotoolkit.sld.xml.v110.StyledLayerDescriptor sld, final File sldFile) {
        
        try {
            final Marshaller marshaller = getMarshallerPoolSLD110().acquireMarshaller();
            try {
                marshaller.marshal(sld, sldFile);
            } finally {
                getMarshallerPoolSLD110().release(marshaller);
            }
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        
        return sldFile;
    }
    
}

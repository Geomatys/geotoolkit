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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.style.MutableStyleFactory;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.filter.FilterFactory2;
import org.opengis.sld.StyledLayerDescriptor;
import org.opengis.util.FactoryException;
import javax.xml.bind.JAXBContext;

/**
 * Utility class to read and write XML OGC SLD files.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class JAXBSLDUtilities {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.sld.xml");

    private final FilterFactory2 filterFactory;
    private final MutableStyleFactory styleFactory;
    private final MutableSLDFactory sldFactory;

    private static MarshallerPool POOL_100;
    private static MarshallerPool POOL_110;

    public static MarshallerPool getMarshallerPoolSLD100() {
        if (POOL_100 == null) {
            final List<Class> classes = getSLD100PoolClasses();
            try {
                POOL_100 = new MarshallerPool(JAXBContext.newInstance(classes.toArray(new Class[classes.size()])), null);
            } catch (JAXBException ex) {
                throw new RuntimeException("Could not load jaxbcontext for sld 100.",ex);
            }
        }
        return POOL_100;
    }

    public static MarshallerPool getMarshallerPoolSLD110() {
        if (POOL_110 == null) {
            final List<Class> classes = getSLD110PoolClasses();
            try {
                POOL_110 = new MarshallerPool(JAXBContext.newInstance(classes.toArray(new Class[classes.size()])), null);
            } catch (JAXBException ex) {
                throw new RuntimeException("Could not load jaxbcontext for sld 110.",ex);
            }
        }
        return POOL_110;
    }

    public static List<Class> getSLD100PoolClasses(){
        final List<Class> classes = new ArrayList<>();
        classes.add(org.geotoolkit.sld.xml.v100.StyledLayerDescriptor.class);

        final ServiceLoader<org.geotoolkit.sld.xml.v100.SymbolizerType> additionalTypes = ServiceLoader.load(org.geotoolkit.sld.xml.v100.SymbolizerType.class);
        final Iterator<org.geotoolkit.sld.xml.v100.SymbolizerType> ite = additionalTypes.iterator();
        while(ite.hasNext()){
            org.geotoolkit.sld.xml.v100.SymbolizerType st = ite.next();
            classes.add(st.getClass());
        }

        return classes;
    }

    public static List<Class> getSLD110PoolClasses(){
        final List<Class> classes = new ArrayList<>();
        classes.add(org.geotoolkit.sld.xml.v110.StyledLayerDescriptor.class);
        classes.add(org.apache.sis.internal.jaxb.geometry.ObjectFactory.class);

        final ServiceLoader<org.geotoolkit.se.xml.v110.SymbolizerType> additionalTypes = ServiceLoader.load(org.geotoolkit.se.xml.v110.SymbolizerType.class);
        final Iterator<org.geotoolkit.se.xml.v110.SymbolizerType> ite = additionalTypes.iterator();
        while(ite.hasNext()){
            org.geotoolkit.se.xml.v110.SymbolizerType st = ite.next();
            final Class sc = st.getClass();
            classes.add(sc);

            final String factoryClassName = sc.getName()+"ObjectFactory";
            try {
                classes.add(sc.getClassLoader().loadClass(factoryClassName));
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException("Could not load Extension symbolizer object factory : "+factoryClassName,ex);
            }
        }

        return classes;
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
            sld = ( org.geotoolkit.sld.xml.v100.StyledLayerDescriptor) unmarshaller.unmarshal(sldFile);
            getMarshallerPoolSLD100().recycle(unmarshaller);
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return sld;
    }

    public File marshallV100( final org.geotoolkit.sld.xml.v100.StyledLayerDescriptor sld, final File sldFile) {

        try {
            final Marshaller marshaller = getMarshallerPoolSLD100().acquireMarshaller();
            marshaller.marshal(sld, sldFile);
            getMarshallerPoolSLD100().recycle(marshaller);
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
            sld = (org.geotoolkit.sld.xml.v110.StyledLayerDescriptor) unmarshaller.unmarshal(sldFile);
            getMarshallerPoolSLD110().recycle(unmarshaller);
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return sld;
    }

    public File marshallV110(final org.geotoolkit.sld.xml.v110.StyledLayerDescriptor sld, final File sldFile) {

        try {
            final Marshaller marshaller = getMarshallerPoolSLD110().acquireMarshaller();
            marshaller.marshal(sld, sldFile);
            getMarshallerPoolSLD110().recycle(marshaller);
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return sldFile;
    }

}

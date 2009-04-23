/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.sld.xml;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.style.MutableStyleFactory;

import org.opengis.filter.FilterFactory2;
import org.opengis.sld.StyledLayerDescriptor;

/**
 * Utility class to read and write XML OGC SLD files.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class JAXBSLDUtilities {

    private static final NamespacePrefixMapperImpl SLD_NAMESPACE = new NamespacePrefixMapperImpl("http://www.opengis.net/sld");

    private final FilterFactory2 filterFactory;
    private final MutableStyleFactory styleFactory;
    private final MutableSLDFactory sldFactory;

    public JAXBSLDUtilities(FilterFactory2 filterFactory, MutableStyleFactory styleFactory, MutableSLDFactory sldFactory) {
        this.filterFactory = filterFactory;
        this.styleFactory = styleFactory;
        this.sldFactory = sldFactory;
    }

    //--------- SLD Version 1.0.0 ----------------------------------------------
    public  org.geotoolkit.internal.jaxb.v100.sld.StyledLayerDescriptor transform_V100(StyledLayerDescriptor sld){
        return new GTtoSLD100Transformer().visit(sld, null);
    }
    
    public MutableStyledLayerDescriptor transform_V100(org.geotoolkit.internal.jaxb.v100.sld.StyledLayerDescriptor sld){
        return new SLD100toGTTransformer(filterFactory,styleFactory,sldFactory).visit(sld);
    }
    
    public  org.geotoolkit.internal.jaxb.v100.sld.StyledLayerDescriptor unmarshall_V100(File sldFile){
        org.geotoolkit.internal.jaxb.v100.sld.StyledLayerDescriptor sld = null;
        
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(org.geotoolkit.internal.jaxb.v100.sld.StyledLayerDescriptor.class);
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            sld = ( org.geotoolkit.internal.jaxb.v100.sld.StyledLayerDescriptor) unmarshaller.unmarshal(sldFile);
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        
        return sld;
    }
    
    public File marshall_V100( org.geotoolkit.internal.jaxb.v100.sld.StyledLayerDescriptor sld, File sldFile) {
        
        try {
            final JAXBContext context = JAXBContext.newInstance( org.geotoolkit.internal.jaxb.v100.sld.StyledLayerDescriptor.class);
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper",SLD_NAMESPACE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(sld, sldFile);
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        
        return sldFile;
    }
        
    //--------- SLD Version 1.1.0 ----------------------------------------------   
    public org.geotoolkit.internal.jaxb.v110.sld.StyledLayerDescriptor transform_V110(StyledLayerDescriptor sld){
        return new GTtoSLD110Transformer().visit(sld, null);
    }
    
    public MutableStyledLayerDescriptor transform_V110(org.geotoolkit.internal.jaxb.v110.sld.StyledLayerDescriptor sld){
        return new SLD110toGTTransformer(filterFactory,styleFactory,sldFactory).visit(sld);
    }
    
    public org.geotoolkit.internal.jaxb.v110.sld.StyledLayerDescriptor unmarshall_V110(File sldFile){
        org.geotoolkit.internal.jaxb.v110.sld.StyledLayerDescriptor sld = null;
        
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(org.geotoolkit.internal.jaxb.v110.sld.StyledLayerDescriptor.class);
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            sld = (org.geotoolkit.internal.jaxb.v110.sld.StyledLayerDescriptor) unmarshaller.unmarshal(sldFile);
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        
        return sld;
    }
    
    public File marshall_V110(org.geotoolkit.internal.jaxb.v110.sld.StyledLayerDescriptor sld, File sldFile) {
        
        try {
            final JAXBContext context = JAXBContext.newInstance(org.geotoolkit.internal.jaxb.v110.sld.StyledLayerDescriptor.class);
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper",SLD_NAMESPACE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(sld, sldFile);
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        
        return sldFile;
    }
    
}

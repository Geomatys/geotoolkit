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
package org.geotoolkit.style.sld;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import junit.framework.TestCase;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.sld.xml.v100.StyledLayerDescriptor;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.sld.xml.GTtoSLD100Transformer;
import org.geotoolkit.sld.xml.NamespacePrefixMapperImpl;
import org.geotoolkit.sld.xml.SLD100toGTTransformer;

import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.sld.Extent;
import org.opengis.sld.FeatureTypeConstraint;
import org.opengis.sld.LayerFeatureConstraints;
import org.opengis.sld.NamedLayer;
import org.opengis.sld.NamedStyle;
import org.opengis.sld.RemoteOWS;
import org.opengis.sld.UserLayer;

/**
 * Test class for sld jaxb marshelling and unmarshelling.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class SLD100Test extends TestCase{

    private static final FilterFactory2 FILTER_FACTORY;
    private static final MutableStyleFactory STYLE_FACTORY;
    private static final MutableSLDFactory SLD_FACTORY;

    static{
        final Hints hints = new Hints();
        hints.put(Hints.STYLE_FACTORY, MutableStyleFactory.class);
        hints.put(Hints.FILTER_FACTORY, FilterFactory2.class);
        STYLE_FACTORY = (MutableStyleFactory)FactoryFinder.getStyleFactory(hints);
        FILTER_FACTORY = (FilterFactory2) FactoryFinder.getFilterFactory(hints);
        SLD_FACTORY = new DefaultSLDFactory();
    }

    private static final NamespacePrefixMapperImpl SLD_NAMESPACE = new NamespacePrefixMapperImpl("http://www.opengis.net/sld");
    
    private static Unmarshaller UNMARSHALLER = null;
    private static Marshaller MARSHALLER = null;
    private static SLD100toGTTransformer TRANSFORMER_GT = null;
    private static GTtoSLD100Transformer TRANSFORMER_SLD = null;
    
    
    //FILES -------------------------------------
    private static File FILE_SLD = null;
    private static File TEST_FILE_SLD = null;
            
    
    
    static {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(org.geotoolkit.sld.xml.v100.StyledLayerDescriptor.class);
            UNMARSHALLER = jaxbContext.createUnmarshaller();
            MARSHALLER = jaxbContext.createMarshaller();
            MARSHALLER.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper",SLD_NAMESPACE);
            MARSHALLER.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            MARSHALLER.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException ex) {ex.printStackTrace();}
        assertNotNull(UNMARSHALLER);
        
        TRANSFORMER_GT = new SLD100toGTTransformer(FILTER_FACTORY, STYLE_FACTORY, SLD_FACTORY);
        assertNotNull(TRANSFORMER_GT);
        
        TRANSFORMER_SLD = new GTtoSLD100Transformer();
        assertNotNull(TRANSFORMER_SLD);
        
        try { 
            FILE_SLD = new File( SLD100Test.class.getResource("/org/geotoolkit/sample/SLD_v100.xml").toURI()  );
            
        } catch (URISyntaxException ex) { ex.printStackTrace(); }
        
        assertNotNull(FILE_SLD);
            
        try{
            TEST_FILE_SLD = File.createTempFile("test_sld_v100",".xml");        
        }catch(IOException ex){
            ex.printStackTrace();
        }
        
        //switch to false to avoid temp files to be deleted
        if(true){
            TEST_FILE_SLD.deleteOnExit();
        }
    
    }
    
    private Object unMarshall(File testFile) throws JAXBException{
        return UNMARSHALLER.unmarshal(testFile);
    }
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    // JAXB TEST MARSHELLING AND UNMARSHELLING FOR STYLE ORDERING //////////////
    ////////////////////////////////////////////////////////////////////////////
    
    @Test
    public void testSLD() throws JAXBException{
        
        //Read test-------------------------------------------------------------
        //----------------------------------------------------------------------
        Object obj = unMarshall(FILE_SLD);
        assertNotNull(obj);
        
        StyledLayerDescriptor jax = (StyledLayerDescriptor) obj;
        MutableStyledLayerDescriptor sld = TRANSFORMER_GT.visit(jax);
        assertNotNull(sld);
        
        //Details
        assertEquals(sld.getName(), "SLD : name");
        assertEquals(sld.getDescription().getTitle().toString(), "SLD : title");
        assertEquals(sld.getDescription().getAbstract().toString(), "SLD : abstract");
        
        //libraries, SLD1.0 does not store thoses informations
        assertEquals(sld.libraries().size(), 0);
        
        //layers
        assertEquals(sld.layers().size(), 2);
        
        //Named Layer-----------------------------------------------------------
        NamedLayer nl = (NamedLayer) sld.layers().get(0);
        assertEquals(nl.getName(), "Named layer : name");
        //no title, no description in SLD1.0        
        List<? extends FeatureTypeConstraint> cons = nl.getConstraints().constraints();
        assertEquals(cons.size(), 1);
        
        assertNotNull( cons.get(0).getFilter() );
        assertEquals(cons.get(0).getFeatureTypeName().getLocalPart(),"Feature type : name");
        assertEquals(cons.get(0).getExtent().size(), 3);
        
        Extent ext = cons.get(0).getExtent().get(0);
        assertEquals(ext.getName(), "Ext : Name 1");
        assertEquals(ext.getValue(), "Ext : Value 1");
        
        ext = cons.get(0).getExtent().get(1);
        assertEquals(ext.getName(), "Ext : Name 2");
        assertEquals(ext.getValue(), "Ext : Value 2");
        
        ext = cons.get(0).getExtent().get(2);
        assertEquals(ext.getName(), "Ext : Name 3");
        assertEquals(ext.getValue(), "Ext : Value 3");
        
        //Named Style-----------------------------------------------------------
        assertEquals(nl.styles().size(), 1);
        NamedStyle ns = (NamedStyle) nl.styles().get(0);
        assertEquals(ns.getName(), "Named style : name");
        
        //User Layer------------------------------------------------------------
        UserLayer ul = (UserLayer)sld.layers().get(1);
        assertEquals(ul.getName(), "User layer : name");
        //no title, no description in SLD1.0   
        
        RemoteOWS source = (RemoteOWS) ul.getSource();
        assertEquals(source.getService(), "WFS");
        assertEquals(source.getOnlineResource().getLinkage().toString(), "http://some.site.com/WFS?");
        
        cons = ((LayerFeatureConstraints)ul.getConstraints()).constraints();
        assertEquals(cons.size(), 1);
        
        assertNotNull( cons.get(0).getFilter() );
        assertEquals(cons.get(0).getFeatureTypeName().getLocalPart(),"Feature type : name");
        assertEquals(cons.get(0).getExtent().size(), 2);
        
        ext = cons.get(0).getExtent().get(0);
        assertEquals(ext.getName(), "Ext : Name 1");
        assertEquals(ext.getValue(), "Ext : Value 1");
        
        ext = cons.get(0).getExtent().get(1);
        assertEquals(ext.getName(), "Ext : Name 2");
        assertEquals(ext.getValue(), "Ext : Value 2");
        
        assertEquals(ul.styles().size(), 1);
        //we dont test the user style, this is done in the SE test
        
        
        //Write test------------------------------------------------------------
        //----------------------------------------------------------------------
        StyledLayerDescriptor pvt = TRANSFORMER_SLD.visit(sld, null);
        assertNotNull(pvt);
        
        assertEquals(pvt.getName(), "SLD : name");
        assertEquals(pvt.getTitle(), "SLD : title");
        assertEquals(pvt.getAbstract(), "SLD : abstract");
        
        //layers
        assertEquals(pvt.getNamedLayerOrUserLayer().size(), 2);
        
        //Named Layer-----------------------------------------------------------
        org.geotoolkit.sld.xml.v100.NamedLayer nlt = (org.geotoolkit.sld.xml.v100.NamedLayer) pvt.getNamedLayerOrUserLayer().get(0);
        assertEquals(nlt.getName(), "Named layer : name");
        //no title, no description in SLD1.0        
        List<org.geotoolkit.sld.xml.v100.FeatureTypeConstraint> constr = nlt.getLayerFeatureConstraints().getFeatureTypeConstraint();
        assertEquals(constr.size(), 1);
        
        assertNotNull(constr.get(0).getFilter());
        assertEquals(constr.get(0).getFeatureTypeName(),"Feature type : name");
        assertEquals(constr.get(0).getExtent().size(), 3);
        
        org.geotoolkit.sld.xml.v100.Extent extx = constr.get(0).getExtent().get(0);
        assertEquals(extx.getName(), "Ext : Name 1");
        assertEquals(extx.getValue(), "Ext : Value 1");
        
        extx = constr.get(0).getExtent().get(1);
        assertEquals(extx.getName(), "Ext : Name 2");
        assertEquals(extx.getValue(), "Ext : Value 2");
        
        extx = constr.get(0).getExtent().get(2);
        assertEquals(extx.getName(), "Ext : Name 3");
        assertEquals(extx.getValue(), "Ext : Value 3");
        
        //Named Style-----------------------------------------------------------
        assertEquals(nlt.getNamedStyleOrUserStyle().size(), 1);
        org.geotoolkit.sld.xml.v100.NamedStyle nst = (org.geotoolkit.sld.xml.v100.NamedStyle) nlt.getNamedStyleOrUserStyle().get(0);
        assertEquals(nst.getName(), "Named style : name");
        
        //User Layer------------------------------------------------------------
        org.geotoolkit.sld.xml.v100.UserLayer ulx = (org.geotoolkit.sld.xml.v100.UserLayer)pvt.getNamedLayerOrUserLayer().get(1);
        assertEquals(ulx.getName(), "User layer : name");
        //no title, no description in SLD1.0   
        
        org.geotoolkit.sld.xml.v100.RemoteOWS sourcex = (org.geotoolkit.sld.xml.v100.RemoteOWS) ulx.getRemoteOWS();
        assertEquals(sourcex.getService(), "WFS");
        assertEquals(sourcex.getOnlineResource().getHref(), "http://some.site.com/WFS?");
        
        constr = ulx.getLayerFeatureConstraints().getFeatureTypeConstraint();
        assertEquals(constr.size(), 1);
        
        assertNotNull(cons.get(0).getFilter());
        assertEquals(cons.get(0).getFeatureTypeName().getLocalPart(),"Feature type : name");
        assertEquals(cons.get(0).getExtent().size(), 2);
        
        ext = cons.get(0).getExtent().get(0);
        assertEquals(ext.getName(), "Ext : Name 1");
        assertEquals(ext.getValue(), "Ext : Value 1");
        
        ext = cons.get(0).getExtent().get(1);
        assertEquals(ext.getName(), "Ext : Name 2");
        assertEquals(ext.getValue(), "Ext : Value 2");
        
        assertEquals(ulx.getUserStyle().size(), 1);        
        
                
        MARSHALLER.marshal(pvt, TEST_FILE_SLD);
        
    }
    
    
    
}

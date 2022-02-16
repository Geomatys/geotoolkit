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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.measure.Units;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.se.xml.v110.ParameterValueType;
import org.geotoolkit.se.xml.v110.TextSymbolizerType;
import org.geotoolkit.sld.xml.GTtoSE110Transformer;
import org.geotoolkit.sld.xml.SE110toGTTransformer;
import org.geotoolkit.sld.xml.v110.StyledLayerDescriptor;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.sld.xml.GTtoSLD110Transformer;
import org.geotoolkit.sld.xml.JAXBSLDUtilities;
import org.geotoolkit.sld.xml.SLD110toGTTransformer;
import org.apache.sis.xml.MarshallerPool;
import static org.junit.Assert.*;
import org.junit.Test;
import org.geotoolkit.filter.FilterFactory2;
import org.geotoolkit.filter.FilterUtilities;
import org.opengis.style.TextSymbolizer;
import org.opengis.util.FactoryException;
import org.opengis.sld.Extent;
import org.opengis.sld.FeatureTypeConstraint;
import org.opengis.sld.LayerFeatureConstraints;
import org.opengis.sld.NamedLayer;
import org.opengis.sld.NamedStyle;
import org.opengis.sld.RemoteOWS;
import org.opengis.sld.UserLayer;
import org.opengis.style.StyleFactory;

/**
 * Test class for sld jaxb marshelling and unmarshelling.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class SLD110Test {

    private static final FilterFactory2 FILTER_FACTORY;
    private static final MutableStyleFactory STYLE_FACTORY;
    private static final MutableSLDFactory SLD_FACTORY;

    static{
        final Hints hints = new Hints();
        hints.put(Hints.STYLE_FACTORY, MutableStyleFactory.class);
        hints.put(Hints.FILTER_FACTORY, FilterFactory2.class);
        STYLE_FACTORY = (MutableStyleFactory) DefaultFactories.forBuildin(StyleFactory.class);
        FILTER_FACTORY = FilterUtilities.FF;
        SLD_FACTORY = new DefaultSLDFactory();
    }

    private static MarshallerPool POOL;
    private static SLD110toGTTransformer TRANSFORMER_GT = null;
    private static GTtoSLD110Transformer TRANSFORMER_SLD = null;


    //FILES -------------------------------------
    private static File FILE_SLD = null;
    private static File TEST_FILE_SLD = null;



    static {
        POOL = JAXBSLDUtilities.getMarshallerPoolSLD110();

        TRANSFORMER_GT = new SLD110toGTTransformer(FILTER_FACTORY, STYLE_FACTORY, SLD_FACTORY);
        assertNotNull(TRANSFORMER_GT);

        TRANSFORMER_SLD = new GTtoSLD110Transformer();
        assertNotNull(TRANSFORMER_SLD);

        try {
            FILE_SLD = new File( SLD110Test.class.getResource("/org/geotoolkit/sample/SLD_v110.xml").toURI()  );

        } catch (URISyntaxException ex) { ex.printStackTrace(); }

        assertNotNull(FILE_SLD);

        try{
            TEST_FILE_SLD = File.createTempFile("test_sld_v110",".xml");
        }catch(IOException ex){
            ex.printStackTrace();
        }

        //switch to false to avoid temp files to be deleted
        if(true){
            TEST_FILE_SLD.deleteOnExit();
        }

    }


    ////////////////////////////////////////////////////////////////////////////
    // JAXB TEST MARSHELLING AND UNMARSHELLING FOR STYLE ORDERING //////////////
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testSLD() throws JAXBException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test-------------------------------------------------------------
        //----------------------------------------------------------------------
        Object obj = UNMARSHALLER.unmarshal(FILE_SLD);
        assertNotNull(obj);

        StyledLayerDescriptor jax = (StyledLayerDescriptor) obj;
        MutableStyledLayerDescriptor sld = TRANSFORMER_GT.visit(jax);
        assertNotNull(sld);

        //Details
        assertEquals(sld.getName(), "SLD : name");
        assertEquals(sld.getDescription().getTitle().toString(), "SLD : title");
        assertEquals(sld.getDescription().getAbstract().toString(), "SLD : abstract");

        //libraries
        assertEquals(sld.libraries().size(), 1);
        assertEquals(sld.libraries().get(0).getOnlineResource().getLinkage().toString(), "http://geomayts.fr/anSLDFile.xml");

        //layers
        assertEquals(sld.layers().size(), 2);

        //Named Layer-----------------------------------------------------------
        NamedLayer nl = (NamedLayer) sld.layers().get(0);
        assertEquals(nl.getName(), "Named layer : name");
        assertEquals(nl.getDescription().getTitle().toString(), "Named layer : title");
        assertEquals(nl.getDescription().getAbstract().toString(), "Named layer : abstract");

        List<? extends FeatureTypeConstraint> cons = nl.getConstraints().constraints();
        assertEquals(cons.size(), 1);

        assertNotNull( cons.get(0).getFilter() );
        assertTrue(  cons.get(0).getFeatureTypeName().toString().endsWith("FeatureName"));
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
        assertEquals(ns.getDescription().getTitle().toString(), "Named style : title");
        assertEquals(ns.getDescription().getAbstract().toString(), "Named style : abstract");

        //User Layer------------------------------------------------------------
        UserLayer ul = (UserLayer)sld.layers().get(1);
        assertEquals(ul.getName(), "User layer : name");
        assertEquals(ul.getDescription().getTitle().toString(), "User layer : title");
        assertEquals(ul.getDescription().getAbstract().toString(), "User layer : abstract");

        RemoteOWS source = (RemoteOWS) ul.getSource();
        assertEquals(source.getService(), "WFS");
        assertEquals(source.getOnlineResource().getLinkage().toString(), "http://some.site.com/WFS?");

        cons = ((LayerFeatureConstraints)ul.getConstraints()).constraints();
        assertEquals(cons.size(), 1);

        assertNotNull( cons.get(0).getFilter() );
        assertTrue(  cons.get(0).getFeatureTypeName().toString().endsWith("FeatureName"));
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
        assertEquals(String.valueOf(pvt.getDescription().getTitle()), "SLD : title");
        assertEquals(String.valueOf(pvt.getDescription().getAbstract()), "SLD : abstract");

        //layers
        assertEquals(pvt.getNamedLayerOrUserLayer().size(), 2);

        //Named Layer-----------------------------------------------------------
        org.geotoolkit.sld.xml.v110.NamedLayer nlt = (org.geotoolkit.sld.xml.v110.NamedLayer) pvt.getNamedLayerOrUserLayer().get(0);
        assertEquals(nlt.getName(), "Named layer : name");
        assertEquals(String.valueOf(nlt.getDescription().getTitle()), "Named layer : title");
        assertEquals(String.valueOf(nlt.getDescription().getAbstract()), "Named layer : abstract");

        List<org.geotoolkit.sld.xml.v110.FeatureTypeConstraint> constr = nlt.getLayerFeatureConstraints().getFeatureTypeConstraint();
        assertEquals(constr.size(), 1);

        assertNotNull(constr.get(0).getFilter());
        assertTrue( cons.get(0).getFeatureTypeName().toString().endsWith("FeatureName"));
        assertEquals(constr.get(0).getExtent().size(), 3);

        org.geotoolkit.sld.xml.v110.Extent extx = constr.get(0).getExtent().get(0);
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
        org.geotoolkit.sld.xml.v110.NamedStyle nst = (org.geotoolkit.sld.xml.v110.NamedStyle) nlt.getNamedStyleOrUserStyle().get(0);
        assertEquals(nst.getName(), "Named style : name");
        assertEquals(String.valueOf(nst.getDescription().getTitle()), "Named style : title");
        assertEquals(String.valueOf(nst.getDescription().getAbstract()), "Named style : abstract");

        //User Layer------------------------------------------------------------
        org.geotoolkit.sld.xml.v110.UserLayer ulx = (org.geotoolkit.sld.xml.v110.UserLayer)pvt.getNamedLayerOrUserLayer().get(1);
        assertEquals(ulx.getName(), "User layer : name");
        assertEquals(String.valueOf(ulx.getDescription().getTitle()), "User layer : title");
        assertEquals(String.valueOf(ulx.getDescription().getAbstract()), "User layer : abstract");

        org.geotoolkit.sld.xml.v110.RemoteOWS sourcex = (org.geotoolkit.sld.xml.v110.RemoteOWS) ulx.getRemoteOWS();
        assertEquals(sourcex.getService(), "WFS");
        assertEquals(sourcex.getOnlineResource().getHref(), "http://some.site.com/WFS?");

        constr = ulx.getLayerFeatureConstraints().getFeatureTypeConstraint();
        assertEquals(constr.size(), 1);

        assertNotNull(cons.get(0).getFilter());
        assertEquals(cons.get(0).getFeatureTypeName().tip().toString(),"FeatureName");
        assertEquals(cons.get(0).getExtent().size(), 2);

        ext = cons.get(0).getExtent().get(0);
        assertEquals(ext.getName(), "Ext : Name 1");
        assertEquals(ext.getValue(), "Ext : Value 1");

        ext = cons.get(0).getExtent().get(1);
        assertEquals(ext.getName(), "Ext : Name 2");
        assertEquals(ext.getValue(), "Ext : Value 2");

        assertEquals(ulx.getUserStyle().size(), 1);


        MARSHALLER.marshal(pvt, TEST_FILE_SLD);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testUnitTranscription() {
        final SE110toGTTransformer se2gt = new SE110toGTTransformer(FILTER_FACTORY, STYLE_FACTORY);
        final GTtoSE110Transformer gt2se = new GTtoSE110Transformer();
        final TextSymbolizerType textSymbol = new TextSymbolizerType();
        final ParameterValueType labelType = new ParameterValueType();
        labelType.getContent().add("label");
        textSymbol.setLabel(labelType);

        textSymbol.setUom("km");
        TextSymbolizer transcriptedText = se2gt.visit(textSymbol);
        assertEquals("Converted unit of measure", Units.KILOMETRE, transcriptedText.getUnitOfMeasure());
        TextSymbolizerType revertedSymbol = gt2se.visit(transcriptedText, null).getValue();
        assertEquals("Reverted unit of measure", "km", revertedSymbol.getUom());

        textSymbol.setUom(null);
        transcriptedText = se2gt.visit(textSymbol);
        assertEquals("Converted unit of measure", Units.POINT, transcriptedText.getUnitOfMeasure());
        revertedSymbol = gt2se.visit(transcriptedText, null).getValue();
        assertEquals("Reverted unit of measure", "http://www.opengeospatial.org/se/units/pixel", revertedSymbol.getUom());

        textSymbol.setUom("px");
        transcriptedText = se2gt.visit(textSymbol);
        assertEquals("Converted unit of measure", Units.POINT, transcriptedText.getUnitOfMeasure());
        revertedSymbol = gt2se.visit(transcriptedText, null).getValue();
        assertEquals("Reverted unit of measure", "http://www.opengeospatial.org/se/units/pixel", revertedSymbol.getUom());

        textSymbol.setUom("meter");
        transcriptedText = se2gt.visit(textSymbol);
        assertEquals("Converted unit of measure", Units.METRE, transcriptedText.getUnitOfMeasure());
        revertedSymbol = gt2se.visit(transcriptedText, null).getValue();
        assertEquals("Reverted unit of measure", "http://www.opengeospatial.org/se/units/metre", revertedSymbol.getUom());
    }
}

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
package org.geotoolkit.style.se;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import junit.framework.TestCase;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.se.xml.v110.CoverageStyleType;
import org.geotoolkit.se.xml.v110.FeatureTypeStyleType;
import org.geotoolkit.se.xml.v110.ObjectFactory;
import org.geotoolkit.se.xml.v110.RuleType;
import org.geotoolkit.sld.xml.v110.UserStyle;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.sld.xml.GTtoSE110Transformer;
import org.geotoolkit.sld.xml.NamespacePrefixMapperImpl;
import org.geotoolkit.sld.xml.SE110toGTTransformer;

import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.style.ContrastMethod;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.SemanticType;
import org.opengis.style.TextSymbolizer;

/**
 * Test class for style jaxb marshelling and unmarshelling.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class SEforSLD110Test extends TestCase{

    private static final FilterFactory2 FILTER_FACTORY;
    private static final MutableStyleFactory STYLE_FACTORY;

    static{
        final Hints hints = new Hints();
        hints.put(Hints.STYLE_FACTORY, MutableStyleFactory.class);
        hints.put(Hints.FILTER_FACTORY, FilterFactory2.class);
        STYLE_FACTORY = (MutableStyleFactory)FactoryFinder.getStyleFactory(hints);
        FILTER_FACTORY = (FilterFactory2) FactoryFinder.getFilterFactory(hints);
    }

    private static final NamespacePrefixMapperImpl SLD_NAMESPACE = new NamespacePrefixMapperImpl("http://www.opengis.net/sld");
    
    private static Unmarshaller UNMARSHALLER;
    private static Marshaller MARSHALLER;
    private static final SE110toGTTransformer TRANSFORMER_GT;
    private static final GTtoSE110Transformer TRANSFORMER_OGC;
    
    private static final String valueName = "name";
    private static final String valueTitle = "title";
    private static final String valueAbstract = "abstract";
    private static final String valueFTN = "A feature type name";
    private static final String valueGeom = null;
    
    //FILES -------------------------------------
    private static File FILE_SE_SYMBOL_POINT;
    private static File FILE_SE_SYMBOL_LINE;
    private static File FILE_SE_SYMBOL_POLYGON;
    private static File FILE_SE_SYMBOL_TEXT;
    private static File FILE_SE_SYMBOL_RASTER;
    private static File FILE_SE_STYLE;
    private static File FILE_SE_FTS;
    private static File FILE_SE_RULE;
    
    private static File TEST_FILE_SE_SYMBOL_POINT;
    private static File TEST_FILE_SE_SYMBOL_LINE;
    private static File TEST_FILE_SE_SYMBOL_POLYGON;
    private static File TEST_FILE_SE_SYMBOL_TEXT;
    private static File TEST_FILE_SE_SYMBOL_RASTER;
    private static File TEST_FILE_SE_STYLE;
    private static File TEST_FILE_SE_FTS;
    private static File TEST_FILE_SE_RULE;
            
    
    static{
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(org.geotoolkit.sld.xml.v110.StyledLayerDescriptor.class);
            UNMARSHALLER = jaxbContext.createUnmarshaller();
            MARSHALLER = jaxbContext.createMarshaller();
            MARSHALLER.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper",SLD_NAMESPACE);
            MARSHALLER.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            MARSHALLER.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException ex) {ex.printStackTrace();}
        assertNotNull(UNMARSHALLER);
        
        TRANSFORMER_GT = new SE110toGTTransformer(FILTER_FACTORY, STYLE_FACTORY);
        assertNotNull(TRANSFORMER_GT);
        
        TRANSFORMER_OGC = new GTtoSE110Transformer();
        assertNotNull(TRANSFORMER_OGC);
        
        try { 
            FILE_SE_SYMBOL_POINT = new File( SEforSLD110Test.class.getResource("/org/geotoolkit/sample/SE_symbol_point_v110.xml").toURI() );
            FILE_SE_SYMBOL_LINE = new File( SEforSLD110Test.class.getResource("/org/geotoolkit/sample/SE_symbol_line_v110.xml").toURI() );
            FILE_SE_SYMBOL_POLYGON = new File( SEforSLD110Test.class.getResource("/org/geotoolkit/sample/SE_symbol_polygon_v110.xml").toURI() );
            FILE_SE_SYMBOL_TEXT = new File( SEforSLD110Test.class.getResource("/org/geotoolkit/sample/SE_symbol_text_v110.xml").toURI() );
            FILE_SE_SYMBOL_RASTER = new File( SEforSLD110Test.class.getResource("/org/geotoolkit/sample/SE_symbol_raster_v110.xml").toURI() );
            FILE_SE_STYLE = new File( SEforSLD110Test.class.getResource("/org/geotoolkit/sample/SLD_userstyle_v110.xml").toURI() );
            FILE_SE_FTS = new File( SEforSLD110Test.class.getResource("/org/geotoolkit/sample/SE_fts_v110.xml").toURI() );
            FILE_SE_RULE = new File( SEforSLD110Test.class.getResource("/org/geotoolkit/sample/SE_rule_v110.xml").toURI() );
            
        } catch (URISyntaxException ex) { ex.printStackTrace(); }
        
        assertNotNull(FILE_SE_SYMBOL_POINT);
        assertNotNull(FILE_SE_SYMBOL_LINE);
        assertNotNull(FILE_SE_SYMBOL_POLYGON);
        assertNotNull(FILE_SE_SYMBOL_TEXT);
        assertNotNull(FILE_SE_SYMBOL_RASTER);
        assertNotNull(FILE_SE_STYLE);
        assertNotNull(FILE_SE_FTS);
        assertNotNull(FILE_SE_RULE);
                
        try{
            TEST_FILE_SE_SYMBOL_POINT = File.createTempFile("test_se_symbol_point_v110", ".xml");
            TEST_FILE_SE_SYMBOL_LINE = File.createTempFile("test_se_symbol_line_v110", ".xml");
            TEST_FILE_SE_SYMBOL_POLYGON = File.createTempFile("test_se_symbol_polygon_v110", ".xml");
            TEST_FILE_SE_SYMBOL_TEXT = File.createTempFile("test_se_symbol_text_v110", ".xml");
            TEST_FILE_SE_SYMBOL_RASTER = File.createTempFile("test_se_symbol_raster_v110", ".xml");
            TEST_FILE_SE_STYLE = File.createTempFile("test_se_style_v110", ".xml");
            TEST_FILE_SE_FTS = File.createTempFile("test_se_fts_v110", ".xml");
            TEST_FILE_SE_RULE = File.createTempFile("test_se_rule_v110", ".xml");
        }catch(IOException ex){
            ex.printStackTrace();
        }
        
        //switch to false to avoid temp files to be deleted
        if(false){
            TEST_FILE_SE_SYMBOL_POINT.deleteOnExit();
            TEST_FILE_SE_SYMBOL_LINE.deleteOnExit();
            TEST_FILE_SE_SYMBOL_POLYGON.deleteOnExit();
            TEST_FILE_SE_SYMBOL_TEXT.deleteOnExit();
            TEST_FILE_SE_SYMBOL_RASTER.deleteOnExit();
            TEST_FILE_SE_STYLE.deleteOnExit();
            TEST_FILE_SE_FTS.deleteOnExit();
            TEST_FILE_SE_RULE.deleteOnExit();
        }
        
    
    }
    
    private Object unMarshall(File testFile) throws JAXBException{
        return UNMARSHALLER.unmarshal(testFile);
    }
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    // JAXB TEST MARSHELLING AND UNMARSHELLING FOR STYLE ORDERING //////////////
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testStyle() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_SE_STYLE);
        assertNotNull(obj);
        
        UserStyle jax = (UserStyle) obj;
        MutableStyle style = TRANSFORMER_GT.visitUserStyle(jax);
        assertNotNull(style);
        
        assertEquals(style.getName(), valueName);
        assertEquals(style.getDescription().getTitle().toString(), valueTitle);
        assertEquals(style.getDescription().getAbstract().toString(), valueAbstract);
        assertEquals(style.isDefault(), true);
        
        assertEquals(style.featureTypeStyles().size(), 3);
        
        
        //Write test
        UserStyle pvt = TRANSFORMER_OGC.visit(style, null);
        assertNotNull(pvt);
        
        assertEquals(pvt.getName(), valueName);
        assertEquals(pvt.getDescription().getTitle(), valueTitle);
        assertEquals(pvt.getDescription().getAbstract(), valueAbstract);
        assertEquals(pvt.isIsDefault(), Boolean.TRUE);
        
        assertEquals(pvt.getFeatureTypeStyleOrCoverageStyleOrOnlineResource().size(), 3);
        
        MARSHALLER.marshal(pvt, TEST_FILE_SE_STYLE);
        
    }

    @Test
    public void testFTS() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_SE_FTS);
        assertNotNull(obj);
        
        JAXBElement<?> jax = (JAXBElement<?>) obj;
        MutableFeatureTypeStyle fts = TRANSFORMER_GT.visitFTS(jax.getValue());
        assertNotNull(fts);
        
        assertEquals(fts.getName(), valueName);
        assertEquals(fts.getDescription().getTitle().toString(), valueTitle);
        assertEquals(fts.getDescription().getAbstract().toString(), valueAbstract);
        assertEquals(fts.featureTypeNames().iterator().next().getLocalPart(), valueFTN);
        
        assertEquals(fts.rules().size(), 3);
        assertEquals(fts.semanticTypeIdentifiers().size(), 6);
        
        Iterator<SemanticType> ite = fts.semanticTypeIdentifiers().iterator();
        assertEquals(ite.next(), SemanticType.ANY);
        assertEquals(ite.next(), SemanticType.POINT);
        assertEquals(ite.next(), SemanticType.LINE);
        assertEquals(ite.next(), SemanticType.POLYGON);
        assertEquals(ite.next(), SemanticType.TEXT);
        assertEquals(ite.next(), SemanticType.RASTER);
        
        
        //Write test
        CoverageStyleType pvt = (CoverageStyleType) TRANSFORMER_OGC.visit(fts, null);
        assertNotNull(pvt);
        
        assertEquals(pvt.getName(), valueName);
        assertEquals(pvt.getDescription().getTitle(), valueTitle);
        assertEquals(pvt.getDescription().getAbstract(), valueAbstract);
        assertTrue(pvt.getCoverageName().contains(valueFTN) );
        
        assertEquals(pvt.getRuleOrOnlineResource().size(), 3);
        assertEquals(pvt.getSemanticTypeIdentifier().size(), 6);
                
        assertEquals(pvt.getSemanticTypeIdentifier().get(0), "generic:any");
        assertEquals(pvt.getSemanticTypeIdentifier().get(1), "generic:point");
        assertEquals(pvt.getSemanticTypeIdentifier().get(2), "generic:line");
        assertEquals(pvt.getSemanticTypeIdentifier().get(3), "generic:polygon");
        assertEquals(pvt.getSemanticTypeIdentifier().get(4), "generic:text");
        assertEquals(pvt.getSemanticTypeIdentifier().get(5), "generic:raster");
        
        MARSHALLER.marshal(new ObjectFactory().createCoverageStyle(pvt), TEST_FILE_SE_FTS);
        
    }

    @Test
    public void testRule() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_SE_RULE);
        assertNotNull(obj);
        
        JAXBElement<?> jax = (JAXBElement<?>) obj;
        MutableRule rule = TRANSFORMER_GT.visitRule(jax.getValue());
        assertNotNull(rule);
        
        assertEquals(rule.getName(), valueName);
        assertEquals(rule.getDescription().getTitle().toString(), valueTitle);
        assertEquals(rule.getDescription().getAbstract().toString(), valueAbstract);
        assertEquals(rule.getMinScaleDenominator(),500d);
        assertEquals(rule.getMaxScaleDenominator(),1000d);
        
        assertNull(rule.getLegend());
        assertNotNull(rule.getFilter());
        
        assertEquals(rule.symbolizers().size(), 3);
        
        
        //Write test
        RuleType pvt = (RuleType) TRANSFORMER_OGC.visit(rule, null);
        assertNotNull(pvt);
        
        assertEquals(pvt.getName(), valueName);
        assertEquals(pvt.getDescription().getTitle(), valueTitle);
        assertEquals(pvt.getDescription().getAbstract(), valueAbstract);
        assertEquals(pvt.getMinScaleDenominator(),500d);
        assertEquals(pvt.getMaxScaleDenominator(),1000d);
        
        assertNull(pvt.getLegendGraphic());
        
        assertEquals(pvt.getSymbolizer().size(), 3);
                
        MARSHALLER.marshal(new ObjectFactory().createRule(pvt), TEST_FILE_SE_RULE);
        
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // JAXB TEST MARSHELLING AND UNMARSHELLING FOR SYMBOLIZERS /////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testPointSymbolizer() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_SE_SYMBOL_POINT);
        assertNotNull(obj);
        
        JAXBElement<org.geotoolkit.se.xml.v110.PointSymbolizerType> jax = (JAXBElement<org.geotoolkit.se.xml.v110.PointSymbolizerType>) obj;
        PointSymbolizer pointSymbol = TRANSFORMER_GT.visit(jax.getValue());
        assertNotNull(pointSymbol);
        
        assertEquals(pointSymbol.getGeometryPropertyName(), valueGeom);
        assertEquals(NonSI.PIXEL, pointSymbol.getUnitOfMeasure());
        assertNotNull(pointSymbol.getGraphic());
        
        assertEquals(pointSymbol.getGraphic().getOpacity().evaluate(null, Float.class), 0.7f);
        assertEquals(pointSymbol.getGraphic().getRotation().evaluate(null, Float.class), 110f);
        assertEquals(pointSymbol.getGraphic().getSize().evaluate(null, Float.class), 32f);
        Mark mark = (Mark) pointSymbol.getGraphic().graphicalSymbols().get(0);
        assertEquals(mark.getWellKnownName().evaluate(null, String.class), "square");
        assertEquals(mark.getStroke().getWidth().evaluate(null, Float.class), 13f);
        assertEquals(mark.getStroke().getOpacity().evaluate(null, Float.class), 0.4f);
        assertEquals(mark.getStroke().getLineJoin().evaluate(null, String.class), "bevel");
        assertEquals(mark.getStroke().getLineCap().evaluate(null, String.class), "butt");
        assertEquals(mark.getStroke().getDashOffset().evaluate(null, Float.class), 2.3f);
        assertEquals(mark.getStroke().getColor().toString(), "#404040");
        
        assertEquals(mark.getFill().getOpacity().evaluate(null, Float.class), 1.0f);
        assertEquals(mark.getFill().getColor().toString(), "#808080");
        
        //Write test
        JAXBElement<org.geotoolkit.se.xml.v110.PointSymbolizerType> pvt = TRANSFORMER_OGC.visit(pointSymbol,null);
        assertNotNull(pvt);
        
        assertEquals(pvt.getValue().getGeometry().getPropertyName().getContent() , "");
        org.geotoolkit.se.xml.v110.GraphicType gra = pvt.getValue().getGraphic();
        
        assertNotNull(gra.getOpacity());
        assertNotNull(gra.getRotation());
        assertNotNull(gra.getSize());
        assertEquals(gra.getExternalGraphicOrMark().size() , 1);
                
        assertNotNull(pvt.getValue().getGraphic());
        assertEquals(pvt.getValue().getGeometry().getPropertyName().getContent(), "");
        
        MARSHALLER.marshal(pvt, TEST_FILE_SE_SYMBOL_POINT);
        
    }

    @Test
    public void testLineSymbolizer() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_SE_SYMBOL_LINE);
        assertNotNull(obj);
        
        JAXBElement<org.geotoolkit.se.xml.v110.LineSymbolizerType> jax = (JAXBElement<org.geotoolkit.se.xml.v110.LineSymbolizerType>) obj;
        LineSymbolizer lineSymbol = TRANSFORMER_GT.visit(jax.getValue());
        assertNotNull(lineSymbol);
        
        assertEquals(lineSymbol.getGeometryPropertyName(), valueGeom);
        assertEquals(SI.METER, lineSymbol.getUnitOfMeasure());
        assertNotNull(lineSymbol.getStroke());
        
        assertEquals(lineSymbol.getStroke().getWidth().evaluate(null, Float.class), 13f);
        assertEquals(lineSymbol.getStroke().getOpacity().evaluate(null, Float.class), 0.4f);
        assertEquals(lineSymbol.getStroke().getLineJoin().evaluate(null, String.class), "bevel");
        assertEquals(lineSymbol.getStroke().getLineCap().evaluate(null, String.class), "butt");
        assertEquals(lineSymbol.getStroke().getDashOffset().evaluate(null, Float.class), 2.3f);
        assertEquals(lineSymbol.getStroke().getColor().toString(), "#FF0000");
                
        //Write test
        JAXBElement<org.geotoolkit.se.xml.v110.LineSymbolizerType> pvt = TRANSFORMER_OGC.visit(lineSymbol,null);
        assertNotNull(pvt);
        
        assertEquals(pvt.getValue().getGeometry().getPropertyName().getContent() , "");
        assertNotNull(pvt.getValue().getStroke());
        
        MARSHALLER.marshal(pvt, TEST_FILE_SE_SYMBOL_LINE);
        
    }

    @Test
    public void testPolygonSymbolizer() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_SE_SYMBOL_POLYGON);
        assertNotNull(obj);
        
        JAXBElement<org.geotoolkit.se.xml.v110.PolygonSymbolizerType> jax = (JAXBElement<org.geotoolkit.se.xml.v110.PolygonSymbolizerType>) obj;
        PolygonSymbolizer polySymbol = TRANSFORMER_GT.visit(jax.getValue());
        assertNotNull(polySymbol);
        
        assertEquals(polySymbol.getGeometryPropertyName(), valueGeom);
        assertEquals(NonSI.FOOT, polySymbol.getUnitOfMeasure());
        assertNotNull(polySymbol.getStroke());
        
        assertEquals(polySymbol.getStroke().getWidth().evaluate(null, Float.class), 13f);
        assertEquals(polySymbol.getStroke().getOpacity().evaluate(null, Float.class), 0.4f);
        assertEquals(polySymbol.getStroke().getLineJoin().evaluate(null, String.class), "bevel");
        assertEquals(polySymbol.getStroke().getLineCap().evaluate(null, String.class), "butt");
        assertEquals(polySymbol.getStroke().getDashOffset().evaluate(null, Float.class), 2.3f);
        assertEquals(polySymbol.getStroke().getColor().toString(), "#FF0000");
        
        assertEquals(polySymbol.getFill().getOpacity().evaluate(null, Float.class), 1.0f);
        assertEquals(polySymbol.getFill().getColor().toString(), "#0000FF");
                
        //Write test
        JAXBElement<org.geotoolkit.se.xml.v110.PolygonSymbolizerType> pvt = TRANSFORMER_OGC.visit(polySymbol,null);
        assertNotNull(pvt);
        
        assertEquals(pvt.getValue().getGeometry().getPropertyName().getContent() , "");
        assertNotNull(pvt.getValue().getStroke());
        assertNotNull(pvt.getValue().getFill());
        
        MARSHALLER.marshal(pvt, TEST_FILE_SE_SYMBOL_POLYGON);
        
    }

    @Test
    public void testTextSymbolizer() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_SE_SYMBOL_TEXT);
        assertNotNull(obj);
        
        JAXBElement<org.geotoolkit.se.xml.v110.TextSymbolizerType> jax = (JAXBElement<org.geotoolkit.se.xml.v110.TextSymbolizerType>) obj;
        TextSymbolizer textSymbol = TRANSFORMER_GT.visit(jax.getValue());
        assertNotNull(textSymbol);
        
        assertEquals(textSymbol.getGeometryPropertyName(), valueGeom);
        assertEquals(NonSI.FOOT, textSymbol.getUnitOfMeasure());
        assertNotNull(textSymbol.getFill());
        
        assertEquals(textSymbol.getFill().getOpacity().evaluate(null, Float.class), 1.0f);
        assertEquals(textSymbol.getFill().getColor().toString(), "#FFC800");
        
        assertEquals(textSymbol.getHalo().getRadius().evaluate(null, Float.class), 5f);
        assertEquals(textSymbol.getHalo().getFill().getOpacity().evaluate(null, Float.class), 0.52f);
                
        assertEquals(textSymbol.getLabel().toString(), "aField");
        
        assertEquals(textSymbol.getFont().getFamily().get(0).evaluate(null,String.class), "arial");
        assertEquals(textSymbol.getFont().getFamily().get(1).evaluate(null,String.class), "serif");
        assertEquals(textSymbol.getFont().getSize().evaluate(null,Float.class), 17f);
        assertEquals(textSymbol.getFont().getStyle().evaluate(null,String.class), "italic");
        assertEquals(textSymbol.getFont().getWeight().evaluate(null,String.class), "bold");
        
        //Write test
        JAXBElement<org.geotoolkit.se.xml.v110.TextSymbolizerType> pvt = TRANSFORMER_OGC.visit(textSymbol,null);
        assertNotNull(pvt);
        
        assertEquals(pvt.getValue().getGeometry().getPropertyName().getContent() , "");
        assertNotNull(pvt.getValue().getFill());
        
        MARSHALLER.marshal(pvt, TEST_FILE_SE_SYMBOL_TEXT);
        
    }

    @Test
    public void testRasterSymbolizer() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_SE_SYMBOL_RASTER);
        assertNotNull(obj);
        
        JAXBElement<org.geotoolkit.se.xml.v110.RasterSymbolizerType> jax = (JAXBElement<org.geotoolkit.se.xml.v110.RasterSymbolizerType>) obj;
        RasterSymbolizer rasterSymbol = TRANSFORMER_GT.visit(jax.getValue());
        assertNotNull(rasterSymbol);
        
        assertEquals(rasterSymbol.getGeometryPropertyName(), valueGeom);
        assertEquals(SI.METER, rasterSymbol.getUnitOfMeasure());
        
        assertNotNull(rasterSymbol.getChannelSelection());
        assertEquals(rasterSymbol.getChannelSelection().getRGBChannels()[0].getChannelName(), "band_1");
        assertEquals(rasterSymbol.getChannelSelection().getRGBChannels()[1].getChannelName(), "band_2");
        assertEquals(rasterSymbol.getChannelSelection().getRGBChannels()[2].getChannelName(), "band_3");
        
//        assertNotNull(rasterSymbol.getColorMap());
//        assertNotNull( rasterSymbol.getColorMap().getFunction() );
        
        assertNotNull(rasterSymbol.getContrastEnhancement());
        assertEquals(rasterSymbol.getContrastEnhancement().getMethod(), ContrastMethod.NORMALIZE);
        assertEquals(rasterSymbol.getContrastEnhancement().getGammaValue().evaluate(null,Float.class), 3f);
        
        assertNotNull(rasterSymbol.getImageOutline());
        
        assertNotNull(rasterSymbol.getOpacity());
        assertEquals(rasterSymbol.getOpacity().evaluate(null,Float.class), 0.32f);
        
        assertNotNull(rasterSymbol.getOverlapBehavior());
        assertEquals(rasterSymbol.getOverlapBehavior(), OverlapBehavior.EARLIEST_ON_TOP);
        
        assertNotNull(rasterSymbol.getShadedRelief());
        assertEquals(rasterSymbol.getShadedRelief().isBrightnessOnly(), true);
        assertEquals(rasterSymbol.getShadedRelief().getReliefFactor().evaluate(null, Float.class), 5f);
        
        
        //Write test
        JAXBElement<org.geotoolkit.se.xml.v110.RasterSymbolizerType> pvt = TRANSFORMER_OGC.visit(rasterSymbol,null);
        assertNotNull(pvt);
        
        org.geotoolkit.se.xml.v110.RasterSymbolizerType rs = pvt.getValue();
        
        assertEquals(rs.getGeometry().getPropertyName().getContent() , "");
        
        assertNotNull(rs.getChannelSelection());
        assertEquals(rs.getChannelSelection().getRedChannel().getSourceChannelName(), "band_1");
        assertEquals(rs.getChannelSelection().getGreenChannel().getSourceChannelName(), "band_2");
        assertEquals(rs.getChannelSelection().getBlueChannel().getSourceChannelName(), "band_3");
        
//        assertNotNull(rs.getColorMap());
        //TODO test colormap content
        
        assertNotNull(rs.getContrastEnhancement());
        assertNotNull(rs.getContrastEnhancement().getNormalize());
        assertNull(rs.getContrastEnhancement().getHistogram());
        assertEquals(rs.getContrastEnhancement().getGammaValue().doubleValue(), 3d);
        
        assertNotNull(rs.getImageOutline());
        
        assertNotNull(rs.getOpacity());
        assertEquals(rs.getContrastEnhancement().getGammaValue().doubleValue(), 3d);
        
        assertNotNull(rs.getOverlapBehavior());
//        assertNotNull(rs.getOverlapBehavior().getEARLIESTONTOP());
//        assertNull(rs.getOverlapBehavior().getAVERAGE());
//        assertNull(rs.getOverlapBehavior().getLATESTONTOP());
//        assertNull(rs.getOverlapBehavior().getRANDOM());
        
        assertNotNull(rs.getShadedRelief());
        assertEquals(rs.getShadedRelief().isBrightnessOnly().booleanValue(), true);
        assertEquals(rs.getShadedRelief().getReliefFactor().doubleValue(), 5d);
        
        MARSHALLER.marshal(pvt, TEST_FILE_SE_SYMBOL_RASTER);
        
    }
    
}

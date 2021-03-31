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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.internal.system.DefaultFactories;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.sld.xml.v100.FeatureTypeStyle;
import org.geotoolkit.sld.xml.v100.Rule;
import org.geotoolkit.sld.xml.v100.UserStyle;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.sld.xml.GTtoSE100Transformer;
import org.geotoolkit.sld.xml.JAXBSLDUtilities;
import org.geotoolkit.sld.xml.SE100toGTTransformer;
import org.apache.sis.xml.MarshallerPool;
import org.apache.sis.measure.Units;
import static org.junit.Assert.*;
import org.junit.Test;
import org.geotoolkit.filter.FilterFactory2;
import org.geotoolkit.filter.FilterUtilities;
import org.opengis.filter.Expression;
import org.opengis.filter.ValueReference;
import org.opengis.style.ContrastMethod;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.SemanticType;
import org.opengis.style.StyleFactory;
import org.opengis.style.TextSymbolizer;

/**
 * Test class for style jaxb marshelling and unmarshelling.
 *
 * @author Johann Sorel (Geomatys)
 */
public class SEforSLD100Test {

    private static final double DELTA = 0.000001;
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
    private static SE100toGTTransformer TRANSFORMER_GT = null;
    private static GTtoSE100Transformer TRANSFORMER_OGC = null;

    private final String valueName = "name";
    private final String valueTitle = "title";
    private final String valueAbstract = "abstract";
    private final String valueFTN = "A feature type name";
    private final String valueGeom = null;

    //FILES -------------------------------------
    private static File FILE_SE_SYMBOL_POINT = null;
    private static File FILE_SE_SYMBOL_LINE = null;
    private static File FILE_SE_SYMBOL_POLYGON = null;
    private static File FILE_SE_SYMBOL_TEXT = null;
    private static File FILE_SE_SYMBOL_RASTER = null;
    private static File FILE_SE_STYLE = null;
    private static File FILE_SE_FTS = null;
    private static File FILE_SE_RULE = null;

    private static File TEST_FILE_SE_SYMBOL_POINT = null;
    private static File TEST_FILE_SE_SYMBOL_LINE = null;
    private static File TEST_FILE_SE_SYMBOL_POLYGON = null;
    private static File TEST_FILE_SE_SYMBOL_TEXT = null;
    private static File TEST_FILE_SE_SYMBOL_RASTER = null;
    private static File TEST_FILE_SE_STYLE = null;
    private static File TEST_FILE_SE_FTS = null;
    private static File TEST_FILE_SE_RULE = null;

    static{

        POOL = JAXBSLDUtilities.getMarshallerPoolSLD100();


        TRANSFORMER_GT = new SE100toGTTransformer(FILTER_FACTORY, STYLE_FACTORY);
        assertNotNull(TRANSFORMER_GT);

        TRANSFORMER_OGC = new GTtoSE100Transformer();
        assertNotNull(TRANSFORMER_OGC);

        try {
            FILE_SE_SYMBOL_POINT = new File( SEforSLD100Test.class.getResource("/org/geotoolkit/sample/SE_symbol_point_v100.xml").toURI() );
            FILE_SE_SYMBOL_LINE = new File( SEforSLD100Test.class.getResource("/org/geotoolkit/sample/SE_symbol_line_v100.xml").toURI() );
            FILE_SE_SYMBOL_POLYGON = new File( SEforSLD100Test.class.getResource("/org/geotoolkit/sample/SE_symbol_polygon_v100.xml").toURI() );
            FILE_SE_SYMBOL_TEXT = new File( SEforSLD100Test.class.getResource("/org/geotoolkit/sample/SE_symbol_text_v100.xml").toURI() );
            FILE_SE_SYMBOL_RASTER = new File( SEforSLD100Test.class.getResource("/org/geotoolkit/sample/SE_symbol_raster_v100.xml").toURI() );
            FILE_SE_STYLE = new File( SEforSLD100Test.class.getResource("/org/geotoolkit/sample/SLD_userstyle_v100.xml").toURI() );
            FILE_SE_FTS = new File( SEforSLD100Test.class.getResource("/org/geotoolkit/sample/SE_fts_v100.xml").toURI() );
            FILE_SE_RULE = new File( SEforSLD100Test.class.getResource("/org/geotoolkit/sample/SE_rule_v100.xml").toURI() );

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
            TEST_FILE_SE_SYMBOL_POINT = File.createTempFile("test_se_symbol_point_v100", ".xml");
            TEST_FILE_SE_SYMBOL_LINE = File.createTempFile("test_se_symbol_line_v100", ".xml");
            TEST_FILE_SE_SYMBOL_POLYGON = File.createTempFile("test_se_symbol_polygon_v100", ".xml");
            TEST_FILE_SE_SYMBOL_TEXT = File.createTempFile("test_se_symbol_text_v100", ".xml");
            TEST_FILE_SE_SYMBOL_RASTER = File.createTempFile("test_se_symbol_raster_v100", ".xml");
            TEST_FILE_SE_STYLE = File.createTempFile("test_se_style_v100", ".xml");
            TEST_FILE_SE_FTS = File.createTempFile("test_se_fts_v100", ".xml");
            TEST_FILE_SE_RULE = File.createTempFile("test_se_rule_v100", ".xml");
        }catch(IOException ex){
            ex.printStackTrace();
        }

        //switch to false to avoid temp files to be deleted
        if(true){
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

    ////////////////////////////////////////////////////////////////////////////
    // JAXB TEST MARSHELLING AND UNMARSHELLING FOR STYLE ORDERING //////////////
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testStyle() throws JAXBException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_SE_STYLE);
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
        assertEquals(pvt.getTitle(), valueTitle);
        assertEquals(pvt.getAbstract(), valueAbstract);
        assertEquals(pvt.isIsDefault(), Boolean.TRUE);

        assertEquals(pvt.getFeatureTypeStyle().size(), 3);

        MARSHALLER.marshal(pvt, TEST_FILE_SE_STYLE);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testFTS() throws JAXBException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_SE_FTS);
        assertNotNull(obj);

        FeatureTypeStyle jax = (FeatureTypeStyle) obj;
        MutableFeatureTypeStyle fts = TRANSFORMER_GT.visitFTS(jax);
        assertNotNull(fts);

        assertEquals(fts.getName(), valueName);
        assertEquals(fts.getDescription().getTitle().toString(), valueTitle);
        assertEquals(fts.getDescription().getAbstract().toString(), valueAbstract);
        assertEquals(fts.featureTypeNames().iterator().next().toString(), valueFTN);

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
        FeatureTypeStyle pvt = TRANSFORMER_OGC.visit(fts, null);
        assertNotNull(pvt);

        assertEquals(pvt.getName(), valueName);
        assertEquals(pvt.getTitle(), valueTitle);
        assertEquals(pvt.getAbstract(), valueAbstract);
        assertEquals(pvt.getFeatureTypeName(), valueFTN);

        assertEquals(pvt.getRule().size(), 3);
        assertEquals(pvt.getSemanticTypeIdentifier().size(), 6);

        assertEquals(pvt.getSemanticTypeIdentifier().get(0), "generic:any");
        assertEquals(pvt.getSemanticTypeIdentifier().get(1), "generic:point");
        assertEquals(pvt.getSemanticTypeIdentifier().get(2), "generic:line");
        assertEquals(pvt.getSemanticTypeIdentifier().get(3), "generic:polygon");
        assertEquals(pvt.getSemanticTypeIdentifier().get(4), "generic:text");
        assertEquals(pvt.getSemanticTypeIdentifier().get(5), "generic:raster");

        MARSHALLER.marshal(pvt, TEST_FILE_SE_FTS);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testRule() throws JAXBException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_SE_RULE);
        assertNotNull(obj);

        Rule jax = (Rule) obj;
        MutableRule rule = TRANSFORMER_GT.visitRule(jax);
        assertNotNull(rule);

        assertEquals(rule.getName(), valueName);
        assertEquals(rule.getDescription().getTitle().toString(), valueTitle);
        assertEquals(rule.getDescription().getAbstract().toString(), valueAbstract);
        assertEquals(rule.getMinScaleDenominator(),500d, DELTA);
        assertEquals(rule.getMaxScaleDenominator(),1000d, DELTA);

        assertNull(rule.getLegend());
        assertNotNull(rule.getFilter());

        assertEquals(rule.symbolizers().size(), 4);


        //Write test
        Rule pvt = TRANSFORMER_OGC.visit(rule, null);
        assertNotNull(pvt);

        assertEquals(pvt.getName(), valueName);
        assertEquals(pvt.getTitle(), valueTitle);
        assertEquals(pvt.getAbstract(), valueAbstract);
        assertEquals(pvt.getMinScaleDenominator(),500d, DELTA);
        assertEquals(pvt.getMaxScaleDenominator(),1000d, DELTA);

        assertNull(pvt.getLegendGraphic());

        assertEquals(pvt.getSymbolizer().size(), 4);

        MARSHALLER.marshal(pvt, TEST_FILE_SE_RULE);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }


    ////////////////////////////////////////////////////////////////////////////
    // JAXB TEST MARSHELLING AND UNMARSHELLING FOR SYMBOLIZERS /////////////////
    ////////////////////////////////////////////////////////////////////////////

    private static String stringValue(Expression e) {
        return e.apply(null).toString();
    }

    private static float floatValue(Expression e) {
        return ((Number) e.apply(null)).floatValue();
    }

    @Test
    public void testPointSymbolizer() throws JAXBException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_SE_SYMBOL_POINT);
        assertNotNull(obj);

        JAXBElement<org.geotoolkit.sld.xml.v100.PointSymbolizer> jax = (JAXBElement<org.geotoolkit.sld.xml.v100.PointSymbolizer>) obj;
        PointSymbolizer pointSymbol = TRANSFORMER_GT.visit(jax.getValue());
        assertNotNull(pointSymbol);

        assertEquals(pointSymbol.getGeometryPropertyName(), valueGeom);
        assertEquals(Units.POINT, pointSymbol.getUnitOfMeasure());
        assertNotNull(pointSymbol.getGraphic());

        assertEquals(floatValue(pointSymbol.getGraphic().getOpacity()), 0.7f, DELTA);
        assertEquals(floatValue(pointSymbol.getGraphic().getRotation()), 110f, DELTA);
        assertEquals(floatValue(pointSymbol.getGraphic().getSize()), 32f, DELTA);
        Mark mark = (Mark) pointSymbol.getGraphic().graphicalSymbols().get(0);
        assertEquals(stringValue(mark.getWellKnownName()), "square");
        assertEquals(floatValue(mark.getStroke().getWidth()), 13f, DELTA);
        assertEquals(floatValue(mark.getStroke().getOpacity()), 0.4f, DELTA);
        assertEquals(stringValue(mark.getStroke().getLineJoin()), "bevel");
        assertEquals(stringValue(mark.getStroke().getLineCap()), "butt");
        assertEquals(floatValue(mark.getStroke().getDashOffset()), 2.3f, DELTA);
        assertEquals(stringValue(mark.getStroke().getColor()), "#404040");

        assertEquals(floatValue(mark.getFill().getOpacity()), 1.0f, DELTA);
        assertEquals(stringValue(mark.getFill().getColor()), "#808080");

        //Write test
        JAXBElement<org.geotoolkit.sld.xml.v100.PointSymbolizer> pvt = TRANSFORMER_OGC.visit(pointSymbol,null);
        assertNotNull(pvt);

        assertEquals(pvt.getValue().getGeometry().getPropertyName().getContent() , "");
        org.geotoolkit.sld.xml.v100.Graphic gra = pvt.getValue().getGraphic();

        assertNotNull(gra.getOpacity());
        assertNotNull(gra.getRotation());
        assertNotNull(gra.getSize());
        assertEquals(gra.getExternalGraphicOrMark().size() , 1);

        assertNotNull(pvt.getValue().getGraphic());
        assertEquals(pvt.getValue().getGeometry().getPropertyName().getContent(), "");

        MARSHALLER.marshal(pvt, TEST_FILE_SE_SYMBOL_POINT);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testLineSymbolizer() throws JAXBException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_SE_SYMBOL_LINE);
        assertNotNull(obj);

        JAXBElement<org.geotoolkit.sld.xml.v100.LineSymbolizer> jax = (JAXBElement<org.geotoolkit.sld.xml.v100.LineSymbolizer>) obj;
        LineSymbolizer lineSymbol = TRANSFORMER_GT.visit(jax.getValue());
        assertNotNull(lineSymbol);

        assertEquals(lineSymbol.getGeometryPropertyName(), valueGeom);
        assertEquals(Units.POINT, lineSymbol.getUnitOfMeasure());
        assertNotNull(lineSymbol.getStroke());

        assertEquals(floatValue(lineSymbol.getStroke().getWidth()), 13f, DELTA);
        assertEquals(floatValue(lineSymbol.getStroke().getOpacity()), 0.4f, DELTA);
        assertEquals(stringValue(lineSymbol.getStroke().getLineJoin()), "bevel");
        assertEquals(stringValue(lineSymbol.getStroke().getLineCap()), "butt");
        assertEquals(floatValue(lineSymbol.getStroke().getDashOffset()), 2.3f, DELTA);
        assertEquals(stringValue(lineSymbol.getStroke().getColor()), "#404040");

        //Write test
        JAXBElement<org.geotoolkit.sld.xml.v100.LineSymbolizer> pvt = TRANSFORMER_OGC.visit(lineSymbol,null);
        assertNotNull(pvt);

        assertEquals(pvt.getValue().getGeometry().getPropertyName().getContent() , "");
        assertNotNull(pvt.getValue().getStroke());

        MARSHALLER.marshal(pvt, TEST_FILE_SE_SYMBOL_LINE);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testPolygonSymbolizer() throws JAXBException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_SE_SYMBOL_POLYGON);
        assertNotNull(obj);

        JAXBElement<org.geotoolkit.sld.xml.v100.PolygonSymbolizer> jax = (JAXBElement<org.geotoolkit.sld.xml.v100.PolygonSymbolizer>) obj;
        PolygonSymbolizer polySymbol = TRANSFORMER_GT.visit(jax.getValue());
        assertNotNull(polySymbol);

        assertEquals(polySymbol.getGeometryPropertyName(), valueGeom);
        assertEquals(Units.POINT, polySymbol.getUnitOfMeasure());
        assertNotNull(polySymbol.getStroke());

        assertEquals(floatValue(polySymbol.getStroke().getWidth()), 13f, DELTA);
        assertEquals(floatValue(polySymbol.getStroke().getOpacity()), 0.4f, DELTA);
        assertEquals(stringValue(polySymbol.getStroke().getLineJoin()), "bevel");
        assertEquals(stringValue(polySymbol.getStroke().getLineCap()), "butt");
        assertEquals(floatValue(polySymbol.getStroke().getDashOffset()), 2.3f, DELTA);
        assertEquals(stringValue(polySymbol.getStroke().getColor()), "#404040");

        assertEquals(floatValue(polySymbol.getFill().getOpacity()), 1.0f, DELTA);
        assertEquals(stringValue(polySymbol.getFill().getColor()), "#808080");

        //Write test
        JAXBElement<org.geotoolkit.sld.xml.v100.PolygonSymbolizer> pvt = TRANSFORMER_OGC.visit(polySymbol,null);
        assertNotNull(pvt);

        assertEquals(pvt.getValue().getGeometry().getPropertyName().getContent() , "");
        assertNotNull(pvt.getValue().getStroke());
        assertNotNull(pvt.getValue().getFill());

        MARSHALLER.marshal(pvt, TEST_FILE_SE_SYMBOL_POLYGON);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testTextSymbolizer() throws JAXBException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_SE_SYMBOL_TEXT);
        assertNotNull(obj);

        JAXBElement<org.geotoolkit.sld.xml.v100.TextSymbolizer> jax = (JAXBElement<org.geotoolkit.sld.xml.v100.TextSymbolizer>) obj;
        TextSymbolizer textSymbol = TRANSFORMER_GT.visit(jax.getValue());
        assertNotNull(textSymbol);

        assertEquals(textSymbol.getGeometryPropertyName(), valueGeom);
        assertEquals(Units.POINT, textSymbol.getUnitOfMeasure());
        assertNotNull(textSymbol.getFill());

        assertEquals(floatValue(textSymbol.getFill().getOpacity()), 1.0f, DELTA);
        assertEquals(stringValue(textSymbol.getFill().getColor()), "#808080");

        assertEquals(floatValue(textSymbol.getHalo().getRadius()), 5f, DELTA);
        assertEquals(floatValue(textSymbol.getHalo().getFill().getOpacity()), 0.52f, DELTA);

        assertEquals( ((ValueReference)textSymbol.getLabel()).getXPath(), "aField");

        assertEquals(stringValue(textSymbol.getFont().getFamily().get(0)), "arial");
        assertEquals(stringValue(textSymbol.getFont().getFamily().get(1)), "serif");
        assertEquals(floatValue(textSymbol.getFont().getSize()), 17f, DELTA);
        assertEquals(stringValue(textSymbol.getFont().getStyle()), "italic");
        assertEquals(stringValue(textSymbol.getFont().getWeight()), "bold");

        //Write test
        JAXBElement<org.geotoolkit.sld.xml.v100.TextSymbolizer> pvt = TRANSFORMER_OGC.visit(textSymbol,null);
        assertNotNull(pvt);

        assertEquals(pvt.getValue().getGeometry().getPropertyName().getContent() , "");
        assertNotNull(pvt.getValue().getFill());

        MARSHALLER.marshal(pvt, TEST_FILE_SE_SYMBOL_TEXT);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testRasterSymbolizer() throws JAXBException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_SE_SYMBOL_RASTER);
        assertNotNull(obj);

        JAXBElement<org.geotoolkit.sld.xml.v100.RasterSymbolizer> jax = (JAXBElement<org.geotoolkit.sld.xml.v100.RasterSymbolizer>) obj;
        RasterSymbolizer rasterSymbol = TRANSFORMER_GT.visit(jax.getValue());
        assertNotNull(rasterSymbol);

        assertEquals(rasterSymbol.getGeometryPropertyName(), valueGeom);
        assertEquals(Units.POINT, rasterSymbol.getUnitOfMeasure());

        assertNotNull(rasterSymbol.getChannelSelection());
        assertEquals(rasterSymbol.getChannelSelection().getRGBChannels()[0].getChannelName(), "band_1");
        assertEquals(rasterSymbol.getChannelSelection().getRGBChannels()[1].getChannelName(), "band_2");
        assertEquals(rasterSymbol.getChannelSelection().getRGBChannels()[2].getChannelName(), "band_3");

        assertNotNull(rasterSymbol.getColorMap());
//        assertNotNull( rasterSymbol.getColorMap().getFunction() );

        assertNotNull(rasterSymbol.getContrastEnhancement());
        assertEquals(rasterSymbol.getContrastEnhancement().getMethod(), ContrastMethod.NORMALIZE);
        assertEquals(floatValue(rasterSymbol.getContrastEnhancement().getGammaValue()), 3f, DELTA);

        assertNotNull(rasterSymbol.getImageOutline());

        assertNotNull(rasterSymbol.getOpacity());
        assertEquals(floatValue(rasterSymbol.getOpacity()), 0.32f, DELTA);

        assertNotNull(rasterSymbol.getOverlapBehavior());
        assertEquals(rasterSymbol.getOverlapBehavior(), OverlapBehavior.EARLIEST_ON_TOP);

        assertNotNull(rasterSymbol.getShadedRelief());
        assertEquals(rasterSymbol.getShadedRelief().isBrightnessOnly(), true);
        assertEquals(floatValue(rasterSymbol.getShadedRelief().getReliefFactor()), 5f, DELTA);


        //Write test
        JAXBElement<org.geotoolkit.sld.xml.v100.RasterSymbolizer> pvt = TRANSFORMER_OGC.visit(rasterSymbol,null);
        assertNotNull(pvt);

        org.geotoolkit.sld.xml.v100.RasterSymbolizer rs = pvt.getValue();

        assertEquals(rs.getGeometry().getPropertyName().getContent() , "");

        assertNotNull(rs.getChannelSelection());
        assertEquals(rs.getChannelSelection().getRedChannel().getSourceChannelName(), "band_1");
        assertEquals(rs.getChannelSelection().getGreenChannel().getSourceChannelName(), "band_2");
        assertEquals(rs.getChannelSelection().getBlueChannel().getSourceChannelName(), "band_3");

        assertNotNull(rs.getColorMap());
        //TODO test colormap content

        assertNotNull(rs.getContrastEnhancement());
        assertNotNull(rs.getContrastEnhancement().getNormalize());
        assertNull(rs.getContrastEnhancement().getHistogram());
        assertEquals(rs.getContrastEnhancement().getGammaValue().doubleValue(), 3d, DELTA);

        assertNotNull(rs.getImageOutline());

        assertNotNull(rs.getOpacity());
        assertEquals(rs.getContrastEnhancement().getGammaValue().doubleValue(), 3d, DELTA);

        assertNotNull(rs.getOverlapBehavior());
        assertNotNull(rs.getOverlapBehavior().getEARLIESTONTOP());
        assertNull(rs.getOverlapBehavior().getAVERAGE());
        assertNull(rs.getOverlapBehavior().getLATESTONTOP());
        assertNull(rs.getOverlapBehavior().getRANDOM());

        assertNotNull(rs.getShadedRelief());
        assertEquals(rs.getShadedRelief().isBrightnessOnly().booleanValue(), true);
        assertEquals(rs.getShadedRelief().getReliefFactor().doubleValue(), 5d, DELTA);

        MARSHALLER.marshal(pvt, TEST_FILE_SE_SYMBOL_RASTER);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }
}

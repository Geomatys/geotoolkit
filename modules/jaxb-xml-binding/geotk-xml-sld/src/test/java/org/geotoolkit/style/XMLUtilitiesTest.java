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
package org.geotoolkit.style;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.xml.bind.JAXBException;
import junit.framework.TestCase;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.sld.MutableNamedLayer;
import org.geotoolkit.sld.MutableNamedStyle;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.sld.MutableUserLayer;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.xml.XMLUtilities;
import org.geotoolkit.util.SimpleInternationalString;

import org.opengis.filter.expression.Expression;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.sld.SLDLibrary;
import org.opengis.style.AnchorPoint;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.Description;
import org.opengis.style.Displacement;
import org.opengis.style.Fill;
import org.opengis.style.Font;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Halo;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.PointPlacement;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.ShadedRelief;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;
import org.geotoolkit.sld.xml.Specification;
import org.geotoolkit.util.logging.Logging;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.PropertyIsBetween;

/**
 * Test class for XMLUtilities.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class XMLUtilitiesTest extends TestCase{

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


    private static final XMLUtilities util = new XMLUtilities();

    private static File FILE_SLD_V100 = null;
    private static File FILE_SLD_V110 = null;
    private static File FILE_STYLE_V100 = null;
    private static File FILE_STYLE_V110 = null;
    private static File FILE_FTS_V100 = null;
    private static File FILE_FTS_V110 = null;
    private static File FILE_RULE_V100 = null;
    private static File FILE_RULE_V110 = null;
    private static File FILE_FILTER_V100 = null;
    private static File FILE_FILTER_V110 = null;

    private static File TEST_FILE_SLD_V100 = null;
    private static File TEST_FILE_SLD_V110 = null;
    private static File TEST_FILE_STYLE_V100 = null;
    private static File TEST_FILE_STYLE_V110 = null;
    private static File TEST_FILE_FTS_V100 = null;
    private static File TEST_FILE_FTS_V110 = null;
    private static File TEST_FILE_RULE_V100 = null;
    private static File TEST_FILE_RULE_V110 = null;
    private static File TEST_FILE_FILTER_V100 = null;
    private static File TEST_FILE_FILTER_V110 = null;



    static{

        try {
            FILE_SLD_V100 = new File( XMLUtilitiesTest.class.getResource("/org/geotoolkit/sample/SLD_v100.xml").toURI()  );
            FILE_SLD_V110 = new File( XMLUtilitiesTest.class.getResource("/org/geotoolkit/sample/SLD_v110.xml").toURI()  );
            FILE_STYLE_V100 = new File( XMLUtilitiesTest.class.getResource("/org/geotoolkit/sample/SLD_userstyle_v100.xml").toURI()  );
            FILE_STYLE_V110 = new File( XMLUtilitiesTest.class.getResource("/org/geotoolkit/sample/SLD_userstyle_v110.xml").toURI()  );
            FILE_FTS_V100 = new File( XMLUtilitiesTest.class.getResource("/org/geotoolkit/sample/SE_fts_v100.xml").toURI()  );
            FILE_FTS_V110 = new File( XMLUtilitiesTest.class.getResource("/org/geotoolkit/sample/SE_fts_v110.xml").toURI()  );
            FILE_RULE_V100 = new File( XMLUtilitiesTest.class.getResource("/org/geotoolkit/sample/SE_rule_v100.xml").toURI()  );
            FILE_RULE_V110 = new File( XMLUtilitiesTest.class.getResource("/org/geotoolkit/sample/SE_rule_v110.xml").toURI()  );
            FILE_FILTER_V100 = new File( XMLUtilitiesTest.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsBetween.xml").toURI()  );
            FILE_FILTER_V110 = new File( XMLUtilitiesTest.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsBetween.xml").toURI()  );

        } catch (URISyntaxException ex) { ex.printStackTrace(); }

        try{
            TEST_FILE_SLD_V100 = File.createTempFile("test_sld_v100(xmlutils)",".xml");     
            TEST_FILE_SLD_V110 = File.createTempFile("test_sld_v110(xmlutils)",".xml");     
            TEST_FILE_STYLE_V100 = File.createTempFile("test_style_v100(xmlutils)",".xml");     
            TEST_FILE_STYLE_V110 = File.createTempFile("test_style_v110(xmlutils)",".xml");     
            TEST_FILE_FTS_V100 = File.createTempFile("test_fts_v100(xmlutils)",".xml");     
            TEST_FILE_FTS_V110 = File.createTempFile("test_fts_v110(xmlutils)",".xml");     
            TEST_FILE_RULE_V100 = File.createTempFile("test_rule_v100(xmlutils)",".xml");     
            TEST_FILE_RULE_V110 = File.createTempFile("test_rule_v110(xmlutils)",".xml");     
            TEST_FILE_FILTER_V100 = File.createTempFile("test_filter_v100(xmlutils)",".xml");     
            TEST_FILE_FILTER_V110 = File.createTempFile("test_filter_v110(xmlutils)",".xml");       
        }catch(IOException ex){
            ex.printStackTrace();
        }
        
        //switch to false to avoid temp files to be deleted
        if(true){
            TEST_FILE_SLD_V100.deleteOnExit();
            TEST_FILE_SLD_V110.deleteOnExit();
            TEST_FILE_STYLE_V100.deleteOnExit();
            TEST_FILE_STYLE_V110.deleteOnExit();
            TEST_FILE_FTS_V100.deleteOnExit();
            TEST_FILE_FTS_V110.deleteOnExit();
            TEST_FILE_RULE_V100.deleteOnExit();
            TEST_FILE_RULE_V110.deleteOnExit();
            TEST_FILE_FILTER_V100.deleteOnExit();
            TEST_FILE_FILTER_V110.deleteOnExit();
        }
        
    }

    @Test
    public void testSLDReading() throws JAXBException{
        MutableStyledLayerDescriptor sld;
        sld = util.readSLD(FILE_SLD_V100, org.geotoolkit.sld.xml.Specification.StyledLayerDescriptor.V_1_0_0);
        assertNotNull(sld);
        sld = util.readSLD(FILE_SLD_V110, org.geotoolkit.sld.xml.Specification.StyledLayerDescriptor.V_1_1_0);
        assertNotNull(sld);
    }

    @Test
    public void testSLDWriting() throws JAXBException{
        MutableStyledLayerDescriptor sld = createSLD();
        util.writeSLD(TEST_FILE_SLD_V100, sld, Specification.StyledLayerDescriptor.V_1_0_0);
        util.writeSLD(TEST_FILE_SLD_V110, sld, Specification.StyledLayerDescriptor.V_1_1_0);
    }

    @Test
    public void testStyleReading() throws JAXBException{
        MutableStyle style;
        style = util.readStyle(FILE_STYLE_V100, Specification.SymbologyEncoding.SLD_1_0_0);
        assertNotNull(style);
        style = util.readStyle(FILE_STYLE_V110, Specification.SymbologyEncoding.V_1_1_0);
        assertNotNull(style);
    }

    @Test
    public void testStyleWriting() throws JAXBException{
        MutableStyle style = createSEStyle();
        util.writeStyle(TEST_FILE_STYLE_V100, style, Specification.StyledLayerDescriptor.V_1_0_0);
        util.writeStyle(TEST_FILE_STYLE_V110, style, Specification.StyledLayerDescriptor.V_1_1_0);
    }

    @Test
    public void testFTSReading() throws JAXBException{
        MutableFeatureTypeStyle fts;
        fts = util.readFeatureTypeStyle(FILE_FTS_V100, Specification.SymbologyEncoding.SLD_1_0_0);
        assertNotNull(fts);
        fts = util.readFeatureTypeStyle(FILE_FTS_V110, Specification.SymbologyEncoding.V_1_1_0);
        assertNotNull(fts);
    }

    @Test
    public void testFTSWriting() throws JAXBException{
        MutableFeatureTypeStyle fts = createFTS();
        util.writeFeatureTypeStyle(TEST_FILE_FTS_V100, fts, Specification.SymbologyEncoding.SLD_1_0_0);
        util.writeFeatureTypeStyle(TEST_FILE_FTS_V110, fts, Specification.SymbologyEncoding.V_1_1_0);
    }

    @Test
    public void testRuleReading() throws JAXBException{
        MutableRule rule;
        rule = util.readRule(FILE_RULE_V100, Specification.SymbologyEncoding.SLD_1_0_0);
        assertNotNull(rule);
        rule = util.readRule(FILE_RULE_V110, Specification.SymbologyEncoding.V_1_1_0);
        assertNotNull(rule);
    }

    @Test
    public void testRuleWriting() throws JAXBException{
        MutableRule rule = createRule();
        util.writeRule(TEST_FILE_RULE_V100, rule, Specification.SymbologyEncoding.SLD_1_0_0);
        util.writeRule(TEST_FILE_RULE_V110, rule, Specification.SymbologyEncoding.V_1_1_0);
    }

    @Test
    public void testFilterReading() throws JAXBException{
        Filter filter;
        filter = util.readFilter(FILE_FILTER_V100, Specification.Filter.V_1_0_0);
        assertNotNull(filter);
        filter = util.readFilter(FILE_FILTER_V110, Specification.Filter.V_1_1_0);
        assertNotNull(filter);
    }

    @Test
    public void testFilterWriting() throws JAXBException{
        Filter filter = createFilter();
        util.writeFilter(TEST_FILE_FILTER_V100, filter, Specification.Filter.V_1_0_0);
        util.writeFilter(TEST_FILE_FILTER_V110, filter, Specification.Filter.V_1_1_0);
    }



    //-------------for tests----------------------------------------------------
    private static MutableStyledLayerDescriptor createSLD(){
        MutableStyledLayerDescriptor geoSLD = SLD_FACTORY.createSLD();
        geoSLD.setVersion("1.1.0");
        geoSLD.setName("the sld name");
        geoSLD.setDescription(STYLE_FACTORY.description(
                new SimpleInternationalString("the title"),
                new SimpleInternationalString("the abstract")));

        //Libraries-------------------------------------------------------------
        OnlineResource online = null;
        try { online = STYLE_FACTORY.onlineResource(new URI("http://geomayts.fr/anSLDFile.xml"));
        } catch (URISyntaxException ex) {
            Logging.getLogger(XMLUtilitiesTest.class).log(Level.SEVERE, null, ex);
        }
        SLDLibrary lib = SLD_FACTORY.createSLDLibrary(online);
        geoSLD.libraries().add(lib);


        //named layer-----------------------------------------------------------
        MutableNamedLayer named = SLD_FACTORY.createNamedLayer();
        named.setName("A named layer");
        named.setDescription(STYLE_FACTORY.description(
                new SimpleInternationalString("the named layer title"),
                new SimpleInternationalString("the named layer description")));
        MutableNamedStyle mns = SLD_FACTORY.createNamedStyle();
        mns.setName("named style name");
        mns.setDescription(STYLE_FACTORY.description(
                new SimpleInternationalString("the named style title"),
                new SimpleInternationalString("the named style description")));
        named.styles().add(mns);
        geoSLD.layers().add(named);


        //user layer------------------------------------------------------------
        MutableUserLayer user = SLD_FACTORY.createUserLayer();
        user.setName("A user layer");
        user.setDescription(STYLE_FACTORY.description(
                new SimpleInternationalString("the user layer title"),
                new SimpleInternationalString("the user layer description")));

        MutableStyle style = createSEStyle();
        user.styles().add(style);
        geoSLD.layers().add(user);

        return geoSLD;
    }

    private static MutableStyle createSEStyle(){
        final MutableStyle style = STYLE_FACTORY.style();
        final MutableFeatureTypeStyle fts1 = STYLE_FACTORY.featureTypeStyle();
        final MutableFeatureTypeStyle fts2 = STYLE_FACTORY.featureTypeStyle();
        final MutableRule rule1 = STYLE_FACTORY.rule();
        final MutableRule rule2 = STYLE_FACTORY.rule();

        //style-----------------------------------------------------------------
        style.setName("Style Name");
        style.setDescription( STYLE_FACTORY.description("Style title", "Style abstract") );
        style.setDefault(true);
        style.featureTypeStyles().add(fts1);
        style.featureTypeStyles().add(fts2);

        //fts 1-----------------------------------------------------------------
        fts1.setName("FTS 1 name");
        fts1.setDescription(STYLE_FACTORY.description("FTS 1 title", "FTS 1 abstract") );

        try { fts1.setOnlineResource(STYLE_FACTORY.onlineResource(new URI("http://geomatys.fr/aFTS1.xml")));
        } catch (URISyntaxException ex) { ex.printStackTrace(); }

        //fts 2-----------------------------------------------------------------
        fts2.setName("FTS 2 name");
        fts2.setDescription(STYLE_FACTORY.description("FTS 2 title", "FTS 2 abstract") );

        fts2.rules().add(rule1);
        fts2.rules().add(rule2);

        //rule 1----------------------------------------------------------------
        rule1.setName("Rule 1 name");
        rule1.setDescription(STYLE_FACTORY.description("Rule 1 title", "Rule 1 abstract") );

        try { rule1.setOnlineResource(STYLE_FACTORY.onlineResource(new URI("http://geomatys.fr/aRule1.xml")));
        } catch (URISyntaxException ex) { ex.printStackTrace(); }


        //rule 2----------------------------------------------------------------
        rule2.setName("Rule 2 name");
        rule2.setDescription(STYLE_FACTORY.description("Rule 2 title", "Rule 2 abstract") );

        rule2.symbolizers().add( createPointSymbolizer() );
        rule2.symbolizers().add( createLineSymbolizer() );
        rule2.symbolizers().add( createPolygonSymbolizer() );
        rule2.symbolizers().add( createTextSymbolizer() );
        rule2.symbolizers().add( createRasterSymbolizer() );

        return style;
    }

    private static MutableFeatureTypeStyle createFTS(){
        final MutableFeatureTypeStyle fts2 = STYLE_FACTORY.featureTypeStyle();
        final MutableRule rule2 = STYLE_FACTORY.rule();

        //fts 2-----------------------------------------------------------------
        fts2.setName("FTS 2 name");
        fts2.setDescription(STYLE_FACTORY.description("FTS 2 title", "FTS 2 abstract") );

        fts2.rules().add(rule2);


        //rule 2----------------------------------------------------------------
        rule2.setName("Rule 2 name");
        rule2.setDescription(STYLE_FACTORY.description("Rule 2 title", "Rule 2 abstract") );

        rule2.symbolizers().add( createPointSymbolizer() );
        rule2.symbolizers().add( createLineSymbolizer() );
        rule2.symbolizers().add( createPolygonSymbolizer() );
        rule2.symbolizers().add( createTextSymbolizer() );
        rule2.symbolizers().add( createRasterSymbolizer() );

        return fts2;
    }

    private static MutableRule createRule(){
        final MutableRule rule2 = STYLE_FACTORY.rule();

        //rule 2----------------------------------------------------------------
        rule2.setName("Rule 2 name");
        rule2.setDescription(STYLE_FACTORY.description("Rule 2 title", "Rule 2 abstract") );

        rule2.symbolizers().add( createPointSymbolizer() );
        rule2.symbolizers().add( createLineSymbolizer() );
        rule2.symbolizers().add( createPolygonSymbolizer() );
        rule2.symbolizers().add( createTextSymbolizer() );
        rule2.symbolizers().add( createRasterSymbolizer() );

        return rule2;
    }

    private static PointSymbolizer createPointSymbolizer(){
        String name = "Point symbolizer name";
        Description desc = STYLE_FACTORY.description("Point symbolizer title", "Point symbolizer description");
        Unit uom = NonSI.PIXEL;
        String geom = "geom";

        List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        symbols.add( STYLE_FACTORY.mark() );
        Expression opacity = FILTER_FACTORY.literal(0.7);
        Expression size = FILTER_FACTORY.literal(32);
        Expression rotation = FILTER_FACTORY.literal(110);
        AnchorPoint anchor = STYLE_FACTORY.anchorPoint(23, 12);
        Displacement disp = STYLE_FACTORY.displacement(21, 15);

        Graphic graphic = STYLE_FACTORY.graphic(symbols, opacity, size, rotation, anchor, disp);

        return STYLE_FACTORY.pointSymbolizer(name,geom,desc,uom,graphic);
    }

    private static LineSymbolizer createLineSymbolizer(){
        String name = "the line symbolizer name";
        Description desc = STYLE_FACTORY.description("Line symbolizer title", "Line symbolizer description");
        Unit uom = SI.METER;
        String geom = "geom";

        Stroke stroke = STYLE_FACTORY.stroke(Color.RED, 3, new float[]{3,6});
        Expression offset = FILTER_FACTORY.literal(5);

        return STYLE_FACTORY.lineSymbolizer(name,geom,desc,uom,stroke, offset);
    }

    private static PolygonSymbolizer createPolygonSymbolizer(){
        String name = "Polygon symbolizer name";
        Description desc = STYLE_FACTORY.description("Polygon symbolizer title", "Polygon symbolizer description");
        Unit uom = NonSI.FOOT;
        String geom = "geom";

        Stroke stroke = STYLE_FACTORY.stroke(Color.RED, 3, new float[]{3,6});
        Fill fill = STYLE_FACTORY.fill(Color.BLUE);
        Expression offset = FILTER_FACTORY.literal(5);
        Displacement disp = STYLE_FACTORY.displacement(9, 7);

        return STYLE_FACTORY.polygonSymbolizer(name,geom,desc,uom,stroke, fill, disp, offset);
    }

    private static TextSymbolizer createTextSymbolizer(){
        String name = "Text symbolizer name";
        Description desc = STYLE_FACTORY.description("Text symbolizer title", "Text symbolizer description");
        Unit uom = NonSI.FOOT;
        String geom = "geom";

        Fill fill = STYLE_FACTORY.fill(Color.ORANGE);
        Halo halo = STYLE_FACTORY.halo(Color.PINK, 12);
        PointPlacement placement = STYLE_FACTORY.pointPlacement();
        Font font = STYLE_FACTORY.font();
        Expression label = FILTER_FACTORY.literal("the feature field name");

        return STYLE_FACTORY.textSymbolizer(name,geom,desc,uom,label, font, placement, halo, fill);
    }

    private static RasterSymbolizer createRasterSymbolizer(){
        String name = "Raster symbolizer name";
        Description desc = STYLE_FACTORY.description("Raster symbolizer title", "Raster symbolizer description");
        Unit uom = SI.METER;
        String geom = "geom";

        Expression opacity = FILTER_FACTORY.literal(0.5);
        ChannelSelection selection = STYLE_FACTORY.channelSelection(
                STYLE_FACTORY.selectedChannelType("chanel2", FILTER_FACTORY.literal(1)));

        OverlapBehavior overlap = OverlapBehavior.RANDOM;
        ColorMap colorMap = STYLE_FACTORY.colorMap();
        ContrastEnhancement enchance = STYLE_FACTORY.contrastEnhancement();
        ShadedRelief relief = STYLE_FACTORY.shadedRelief(FILTER_FACTORY.literal(3),true);

        Symbolizer outline = createLineSymbolizer();

        return STYLE_FACTORY.rasterSymbolizer(name,geom,desc,uom,opacity, selection, overlap, colorMap, enchance, relief, outline);
    }

    private static PropertyIsBetween createFilter(){
       FilterFactory ff = FactoryFinder.getFilterFactory(null);
       Expression field = ff.property("aFiled");
       Expression lower = ff.literal(50d);
       Expression upper = ff.literal(100d);
        
       return ff.between(field, lower, upper);
    }
    
}

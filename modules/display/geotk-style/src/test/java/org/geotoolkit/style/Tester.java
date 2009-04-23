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

package org.geotoolkit.style;

import java.awt.Color;
import java.io.File;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.sld.MutableNamedLayer;
import org.geotoolkit.sld.MutableNamedStyle;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.sld.MutableUserLayer;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.xml.JAXBSLDUtilities;
import org.geotoolkit.util.SimpleInternationalString;

import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.metadata.citation.OnLineResource;
import org.opengis.sld.Layer;
import org.opengis.sld.SLDLibrary;
import org.opengis.sld.StyledLayerDescriptor;
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

/**
 * @author Johann Sorel (Geomatys)
 */
public class Tester {

    private static final FilterFactory2 FILTER_FACTORY;
    private static final MutableStyleFactory STYLE_FACTORY;
    private static final MutableSLDFactory SLD_FACTORY;
    private static final JAXBSLDUtilities SLD_UTILITIES;

    static{
        final Hints hints = new Hints();
        hints.put(Hints.STYLE_FACTORY, MutableStyleFactory.class);
        hints.put(Hints.FILTER_FACTORY, FilterFactory2.class);
        STYLE_FACTORY = (MutableStyleFactory)FactoryFinder.getStyleFactory(hints);
        FILTER_FACTORY = (FilterFactory2) FactoryFinder.getFilterFactory(hints);
        SLD_FACTORY = new DefaultSLDFactory();
        SLD_UTILITIES = new JAXBSLDUtilities(FILTER_FACTORY, STYLE_FACTORY, SLD_FACTORY);
    }

    @Test
    public void marshalltests() {

        File sld100 = new File("sld_v100.xml");
        File sld110 = new File("sld_v110.xml");
        sld100.deleteOnExit();
        sld110.deleteOnExit();

        testSLDMarshall_V100( sld100 );
        testSLDMarshall_V110( sld110 );
        
        try { testSLDUnMarshall_V100(new File(Tester.class.getResource("/org/geotoolkit/sample/SLD_v100.xml").toURI()));
        } catch (URISyntaxException ex) { ex.printStackTrace(); }
        
        try { testSLDUnMarshall_V110(new File(Tester.class.getResource("/org/geotoolkit/sample/SLD_v110.xml").toURI()));
        } catch (URISyntaxException ex) { ex.printStackTrace(); }
        
    }
   
    
    //--------------------- V1.0.0 ---------------------------------------------
    private static void testSLDMarshall_V100(File file) {
        StyledLayerDescriptor geoSLD = createSLD();
        org.geotoolkit.internal.jaxb.v100.sld.StyledLayerDescriptor sld = SLD_UTILITIES.transform_V100(geoSLD);
        SLD_UTILITIES.marshall_V100(sld, file);
    }

    private static void testSLDUnMarshall_V100(File file) {        
        org.geotoolkit.internal.jaxb.v100.sld.StyledLayerDescriptor sld = SLD_UTILITIES.unmarshall_V100(file);
        MutableStyledLayerDescriptor geoSLD = SLD_UTILITIES.transform_V100(sld);
        showSLD(geoSLD);
    }
    
    
    
    //--------------------- V1.1.0 ---------------------------------------------
    private static void testSLDMarshall_V110(File file) {
        StyledLayerDescriptor geoSLD = createSLD();
        org.geotoolkit.internal.jaxb.v110.sld.StyledLayerDescriptor sld = SLD_UTILITIES.transform_V110(geoSLD);
        SLD_UTILITIES.marshall_V110(sld, file);
    }

    private static void testSLDUnMarshall_V110(File file) {        
        org.geotoolkit.internal.jaxb.v110.sld.StyledLayerDescriptor sld = SLD_UTILITIES.unmarshall_V110(file);
        MutableStyledLayerDescriptor geoSLD = SLD_UTILITIES.transform_V110(sld);
        showSLD(geoSLD);
    }
    
    
    private static StyledLayerDescriptor createSLD(){
        MutableStyledLayerDescriptor geoSLD = SLD_FACTORY.createSLD();
        geoSLD.setVersion("1.1.0");
        geoSLD.setName("the sld name");
        geoSLD.setDescription(STYLE_FACTORY.description(
                new SimpleInternationalString("the title"), 
                new SimpleInternationalString("the abstract")));
        
        //Libraries-------------------------------------------------------------
        OnLineResource online = null;
        try { online = STYLE_FACTORY.onlineResource(new URI("http://geomayts.fr/anSLDFile.xml"));
        } catch (URISyntaxException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
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
    
    
    private static void showSLD(StyledLayerDescriptor sld){
        System.out.println("-----------------------------------------------------");
        System.out.println(sld.toString());
        
        System.out.println("LIBRARIES");
        for(SLDLibrary lib : sld.libraries()){
            System.out.println(lib);
        }
        
        System.out.println("LAYERS");
        for(Layer layer : sld.layers()){
            System.out.println(layer);
        }
        
        
    }
    
    private static void showSLD(org.geotoolkit.internal.jaxb.v110.sld.StyledLayerDescriptor sld){
        
        System.out.println("NAME =" + sld.getName());
        
        org.geotoolkit.internal.jaxb.v110.se.DescriptionType des = sld.getDescription();
        System.out.println("DESC : title="+des.getTitle());
        System.out.println("DESC : abstract="+des.getAbstract());
        
        for(org.geotoolkit.internal.jaxb.v110.sld.UseSLDLibrary lib : sld.getUseSLDLibrary()){
            System.out.println("SLD_LIB : resource=" + lib.getOnlineResource());
        }
        
        for(Object obj : sld.getNamedLayerOrUserLayer()){
            if(obj instanceof org.geotoolkit.internal.jaxb.v110.sld.NamedLayer){
                org.geotoolkit.internal.jaxb.v110.sld.NamedLayer named = (org.geotoolkit.internal.jaxb.v110.sld.NamedLayer) obj;
                System.out.println("LAYER : NAMED : name=" + named.getName());
                org.geotoolkit.internal.jaxb.v110.se.DescriptionType desc = named.getDescription();
                if(desc != null){
                    System.out.println("LAYER : NAMED : DESC : title=" + desc.getTitle());
                    System.out.println("LAYER : NAMED : DESC : abstract=" + desc.getAbstract());
                }
                
                org.geotoolkit.internal.jaxb.v110.sld.LayerFeatureConstraints cons = named.getLayerFeatureConstraints();
                for(org.geotoolkit.internal.jaxb.v110.sld.FeatureTypeConstraint con : cons.getFeatureTypeConstraint()){
                    System.out.println("LAYER : NAMED : CONSTR : ftname=" + con.getFeatureTypeName());
                }
                
                for(Object style : named.getNamedStyleOrUserStyle()){
                    if(style instanceof org.geotoolkit.internal.jaxb.v110.sld.NamedStyle){
                        org.geotoolkit.internal.jaxb.v110.sld.NamedStyle ns = (org.geotoolkit.internal.jaxb.v110.sld.NamedStyle) style;
                        System.out.println("LAYER : NAMED : STYLE : NAMED : name=" + ns.getName());
                        org.geotoolkit.internal.jaxb.v110.se.DescriptionType nsdesc = ns.getDescription();
                        if(desc != null){
                            System.out.println("LAYER : NAMED : STYLE : NAMED : DESC : title=" + nsdesc.getTitle());
                            System.out.println("LAYER : NAMED : STYLE : NAMED : DESC : abstract=" + nsdesc.getAbstract());
                        }
                    }else if(style instanceof org.geotoolkit.internal.jaxb.v110.sld.UserStyle){
                        
                    }
                }
                
            }else if(obj instanceof org.geotoolkit.internal.jaxb.v110.sld.UserLayer){
                org.geotoolkit.internal.jaxb.v110.sld.UserLayer user = (org.geotoolkit.internal.jaxb.v110.sld.UserLayer) obj;
                System.out.println("LAYER : USER : name=" + user.getName());
                org.geotoolkit.internal.jaxb.v110.se.DescriptionType desc = user.getDescription();
                if(desc != null){
                    System.out.println("LAYER : USER : DESC : title=" + desc.getTitle());
                    System.out.println("LAYER : USER : DESC : abstract=" + desc.getAbstract());
                }
                                
                org.geotoolkit.internal.jaxb.v110.sld.RemoteOWS ows = user.getRemoteOWS();
                if(ows != null){
                    System.out.println("LAYER : USER : OWS : service="+ows.getService());
                    System.out.println("LAYER : USER : OWS : resource="+ows.getOnlineResource());
                }
                
                org.geotoolkit.internal.jaxb.v110.sld.LayerFeatureConstraints ftcons = user.getLayerFeatureConstraints();
                for(org.geotoolkit.internal.jaxb.v110.sld.FeatureTypeConstraint con : ftcons.getFeatureTypeConstraint()){
                    System.out.println("LAYER : USER : FT_CONSTR : ftname=" + con.getFeatureTypeName());
                }
                
                org.geotoolkit.internal.jaxb.v110.sld.LayerCoverageConstraints cvcons = user.getLayerCoverageConstraints();
                for(org.geotoolkit.internal.jaxb.v110.sld.CoverageConstraint con : cvcons.getCoverageConstraint()){
                    System.out.println("LAYER : USER : CV_CONSTR : cname=" + con.getCoverageName());
                    org.geotoolkit.internal.jaxb.v110.sld.CoverageExtent ext = con.getCoverageExtent();
                    if(ext != null){
                        if(ext.getTimePeriod() != null){
                            System.out.println("LAYER : USER : CV_CONSTR : EXTENT : timeperiod=" + ext.getTimePeriod());
                        }
                        
                        for(org.geotoolkit.internal.jaxb.v110.sld.RangeAxis axi : ext.getRangeAxis()){
                            System.out.println("LAYER : USER : CV_CONSTR : EXTENT : RANGE : name="+axi.getName() +" value=" + axi.getValue());
                        }
                    }
                }
                
                
            }
        }
        
        System.out.println("VERSION : "+sld.getVersion());
        
        System.out.println("SLD =" + sld);
        
    }
    
    
}

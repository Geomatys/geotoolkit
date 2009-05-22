
package org.geotoolkit.debug;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import javax.xml.bind.JAXBException;
import org.geotools.coverage.io.CoverageReader;
import org.geotoolkit.coverage.wi.WorldImageFactory;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.postgis.PostgisDataStoreFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.sld.MutableNamedLayer;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;

import org.geotoolkit.style.function.ThreshholdsBelongTo;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.sld.xml.Specification.StyledLayerDescriptor;
import org.geotoolkit.sld.xml.XMLUtilities;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.util.RandomStyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.style.AnchorPoint;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.ContrastMethod;
import org.opengis.style.Description;
import org.opengis.style.Displacement;
import org.opengis.style.Fill;
import org.opengis.style.Font;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicFill;
import org.opengis.style.GraphicStroke;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Halo;
import org.opengis.style.LabelPlacement;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.ShadedRelief;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;

/**
 *
 * @author johann sorel (Puzzle-GIS)
 */
public class ContextBuilder {

    public static final DefaultStyleFactory SF = new DefaultStyleFactory();
    public static final FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2(null);
    public static final RandomStyleFactory RANDOM_FACTORY = new RandomStyleFactory();

    public static MapContext buildRealCityContext() {

        MapContext context = null;
        MapLayer layer;
        CoverageReader reader;

        Map<String,Object> params;
        File shape;
        DataStore store;
        FeatureSource fs;
        MutableStyle style;

        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);

            reader = readWorldImage(new File("/home/eclesia/GIS_DATA/JEU_VILLE/ortho/1998-0897-1797-83.jpg"));
            layer = MapBuilder.createCoverageLayer(reader, SF.style(), "worldimage");
            context.layers().add(layer);

            reader = readWorldImage(new File("/home/eclesia/GIS_DATA/JEU_VILLE/ortho/1998-0897-1798-83.tif"));
            layer = MapBuilder.createCoverageLayer(reader, SF.style(), "worldimage2");
            context.layers().add(layer);

            reader = readWorldImage(new File("/home/eclesia/GIS_DATA/JEU_VILLE/ortho/1998-0897-1799-83.tif"));
            layer = MapBuilder.createCoverageLayer(reader, SF.style(), "worldimage3");
            context.layers().add(layer);

            reader = readWorldImage(new File("/home/eclesia/GIS_DATA/JEU_VILLE/ortho/1998-0897-1800-83.tif"));
            layer = MapBuilder.createCoverageLayer(reader, SF.style(), "worldimage4");
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/JEU_VILLE/ALTI_LIGNE_ISO.SHP");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = createNewLineStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("isoligne", ""));
            layer.setName("isoligne");
            layer.setVisible(true);
            layer.setElevationModel(MapBuilder.createElevationModel(null, FF.property("ALTITUDE"), FF.literal(0)));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/JEU_VILLE/BATIMENT_SURF.SHP");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = createRealBuildingStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("batiments", ""));
            layer.setName("batiments");
            layer.setVisible(true);
            layer.setElevationModel(MapBuilder.createElevationModel(null, FF.property("Z_MIN"), FF.literal(0)));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/JEU_VILLE/EQ_PYLONE.SHP");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = createLabeledPointStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("pylone", ""));
            layer.setName("pylone");
            layer.setVisible(true);
            layer.setElevationModel(MapBuilder.createElevationModel(null, FF.property("ALTITUDE"), FF.literal(0)));
            context.layers().add(layer);

            context.setCoordinateReferenceSystem(layer.getBounds().getCoordinateReferenceSystem());
            context.setAreaOfInterest(context.getBounds());
            context.setDescription(SF.description("Democontext", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;

    }

    public static MapLayer createPostGISLayer() throws IOException{
        
        final Map params = new HashMap<String, Object>();
        params.put("dbtype", "postgis");
        params.put(PostgisDataStoreFactory.HOST.key, "pulsar.teledetection.fr");
        params.put(PostgisDataStoreFactory.PORT.key, 5432);
        params.put(PostgisDataStoreFactory.SCHEMA.key, "archeo");
        params.put(PostgisDataStoreFactory.DATABASE.key, "Archeo");
        params.put(PostgisDataStoreFactory.USER.key, "archeo");
        params.put(PostgisDataStoreFactory.PASSWD.key, "arch3o");
        
        DataStore store = DataStoreFinder.getDataStore(params);
                
        FeatureSource fs = store.getFeatureSource("StructuresGeo");
        fs.getBounds();
        FeatureCollection coll = fs.getFeatures();
        org.geotools.feature.FeatureIterator ite = coll.features();
        try{
        while(ite.hasNext()){
            SimpleFeature f = (SimpleFeature) ite.next();
            
//            f.getProperty("NOTEXISTINGPROPERTY"); //THIS LINE RAISE AN ERROR
            f.getDefaultGeometryProperty().getValue();
        }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            ite.close();
        }
        
        
        return MapBuilder.createFeatureLayer(fs, SF.style(SF.pointSymbolizer()));
    }
    
    public static MapLayer createVectorLayer(String path){
        final File file = new File(path);
        return createVectorLayer(file);
    }
    
    public static MapLayer createVectorLayer(File file){
        try {
            Map<String,Object> params = new HashMap<String,Object>();
            params.put( "url", file.toURI().toURL() );
            DataStore store = DataStoreFinder.getDataStore(params);
            FeatureSource fs = store.getFeatureSource(store.getTypeNames()[0]);

            MutableStyle nStyle = null;
            FeatureType typ = fs.getSchema();
            AttributeDescriptor att = typ.getGeometryDescriptor();
            AttributeType type = att.getType();
            Class cla = type.getBinding();

            nStyle = RANDOM_FACTORY.createRandomVectorStyle(fs);

//            if (cla.equals(Polygon.class) || cla.equals(MultiPolygon.class)) {
//                nStyle = createNewPolygonStyle();
//            } else if (cla.equals(LineString.class) || cla.equals(MultiLineString.class)) {
//                nStyle = createNewLineStyle();
//            } else if (cla.equals(Point.class) || cla.equals(MultiPoint.class)) {
//                nStyle = createNewPointStyle();
//            }

            MapLayer layer = MapBuilder.createFeatureLayer(fs, nStyle);
            layer.setDescription(SF.description(file.getName(), ""));

            return layer;
        } catch (Exception ex) {
            return null;
        }
    }
    

    public static MutableStyle createNewPointStyle(){


        List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        symbols.add(createMark("square",Color.RED));

        Expression opacity = FF.literal(1d);
        Expression expSize = FF.literal(16);
        Expression expRotation = FF.literal(0);
        AnchorPoint anchor = null;
        Displacement disp = null;
        org.opengis.style.Graphic graphic = SF.graphic(symbols, opacity, expSize, expRotation, anchor,disp);

        Unit uom = NonSI.PIXEL;
        String geom = null;
        String name = "unIdQuelconque";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;


        PointSymbolizer ps = SF.pointSymbolizer(name,geom,desc,uom,graphic);

        return SF.style(ps);
    }

    public static MutableStyle createNewLineStyle(){

        Stroke stroke = createNewStroke();
        Expression offset = FF.literal(0);                                 //--
        Unit uom = NonSI.PIXEL;                                                       //--
        String geom = null;                                                                         //--
        String name = "unIdQuelconque";                                                             //OK
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;                                      //OK

        LineSymbolizer symbol = SF.lineSymbolizer(name,geom,desc,uom,stroke, offset);

        return SF.style(symbol);
    }
    
    public static MutableStyle createElecLineStyle(){

        Expression color = SF.literal(Color.RED);
        Expression opacity = FF.literal(1d);
        Expression width = FF.literal(2d);
        Expression join = FF.literal("bevel");
        Expression cap = FF.literal("round");
        float[] dashes = null;
        Expression strokeOffset = FF.literal(0d);

        Stroke stroke = SF.stroke(color, opacity, width, join, cap, dashes, strokeOffset);
        
        Expression offset = FF.literal(0);
        Unit uom = NonSI.PIXEL;
        String geom = null;
        String name = "unIdQuelconque";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        LineSymbolizer symbol = SF.lineSymbolizer(name,geom,desc,uom,stroke, offset);

        return SF.style(symbol);
    }

    public static MutableStyle createNewPolygonStyle(){

        Fill fill = createNewFill();
        Stroke stroke = createNewStroke();

        Displacement disp = SF.displacement(0, 0);
        Expression offset = FF.literal(0);                                 //--
        Unit uom = NonSI.PIXEL;                                                        //--
        String geom = null;                                                                         //--
        String name = "unIdQuelconque";                                                             //OK
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;                                      //OK


        PolygonSymbolizer symbol = SF.polygonSymbolizer(name,geom,desc,uom,stroke, fill, disp, offset);

        return SF.style(symbol);
    }

    public static MutableStyle createRasterStyle(){

        final List<InterpolationPoint> values = new ArrayList<InterpolationPoint>();
//        values.add( SF.createInterpolationPoint(SF.literal(Color.BLACK), 8000));
//        values.add( SF.createInterpolationPoint(SF.literal(Color.BLUE), 8300));
//        values.add( SF.createInterpolationPoint(SF.literal(new Color(0,150,0)), 8600));
//        values.add( SF.createInterpolationPoint(SF.literal(new Color(100,50,50)), 30000));
//        values.add( SF.createInterpolationPoint(SF.literal(Color.WHITE), 60000));
        values.add( SF.interpolationPoint(SF.literal(new Color(0f,0f,0f,0f)), 8300));
        values.add( SF.interpolationPoint(SF.literal(Color.WHITE), 16000));
        final Literal lookup = StyleConstants.DEFAULT_CATEGORIZE_LOOKUP;
        final Literal fallback = StyleConstants.DEFAULT_FALLBACK;
        final Function function = SF.interpolateFunction(
                lookup, values, Method.COLOR, Mode.LINEAR, fallback);

        final ChannelSelection selection = StyleConstants.DEFAULT_RASTER_CHANNEL_RGB;

//        final ChannelSelection selection = SF.createChannelSelection(
//                SF.createSelectedChannelType("0", FF.literal(1)));

        Expression opacity = FF.literal(1f);
        OverlapBehavior overlap = OverlapBehavior.LATEST_ON_TOP;
//        ColorMap colorMap = SF.createColorMap(function);
        ColorMap colorMap = null;
        ContrastEnhancement enchance = SF.contrastEnhancement(FF.literal(1.0f),ContrastMethod.NONE);
        ShadedRelief relief = null; //StyleConstants.DEFAULT_SHADED_RELIEF;
        Symbolizer outline = null;//createDashLineSymbolizer();
        Unit uom = NonSI.PIXEL;
        String geom = StyleConstants.DEFAULT_GEOM;
        String name = "raster symbol name";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        RasterSymbolizer symbol = SF.rasterSymbolizer(name,geom,desc,uom,opacity, selection, overlap, colorMap, enchance, relief, outline);
        
        return SF.style(symbol);
    }

    public static Stroke createNewStroke(){

        Expression color = SF.literal(Color.GREEN.darker());                           //OK
        Expression opacity = FF.literal(1d);                               //OK
        Expression width = FF.literal(1d);                                //OK
        Expression join = FF.literal("bevel");                             //OK
        Expression cap = FF.literal("round");                              //OK
        float[] dashes = null;                                                             //OK
        Expression strokeOffset = FF.literal(0d);                          //OK

        Stroke stroke = SF.stroke(color, opacity, width, join, cap, dashes, strokeOffset);
        return stroke;
    }

    public static Fill createNewFill(){
        Expression fillColor = SF.literal(Color.BLUE);                           //OK
        Expression fillOpacity = FF.literal(1d);                               //OK

        Fill fill = SF.fill(null, fillColor, fillOpacity);
        return fill;
    }

    public static MutableStyle createRealRoadStyle(){

        MutableSLDFactory SLDF = new DefaultSLDFactory();
        MutableStyledLayerDescriptor sld = SLDF.createSLD();
        MutableNamedLayer layer = SLDF.createNamedLayer();
        sld.layers().add(layer);

        
        Symbolizer[] symbols = new Symbolizer[3];

        Expression offset = FF.literal(0);
        Unit uom = SI.METER;
        String geom = null;
        String name = "unIdQuelconque";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        //bande blanche en direct au milieu de la route
        Stroke stroke = SF.stroke(Color.BLACK, 1d, null);
        LineSymbolizer symbol = SF.lineSymbolizer(name,geom,desc,uom,stroke, offset);
        symbols[2] = symbol;

        //partie roulante
        GraphicFill fill = null;
        GraphicStroke strk = null;
        Expression color = SF.literal(Color.RED.darker());
        Expression opacity = FF.literal(1d);
        Expression width = FF.literal(22d);
        Expression join = StyleConstants.STROKE_JOIN_BEVEL;
        Expression cap = StyleConstants.STROKE_CAP_BUTT;
        float[] dashes = null;
        Expression off = FF.literal(0d);

        stroke = SF.stroke(color,opacity,width,join,cap,dashes,off);
        symbol = SF.lineSymbolizer(name,geom,desc,uom,stroke, offset);
        symbols[1] = symbol;

        //trottoir
        fill = null;
        strk = null;
        color = SF.literal(Color.BLACK);
        opacity = FF.literal(1d);
        width = FF.literal(24d);
        join = StyleConstants.STROKE_JOIN_BEVEL;
        cap = StyleConstants.STROKE_CAP_BUTT;
        dashes = null;
        off = FF.literal(0d);

        stroke = SF.stroke(color,opacity,width,join,cap,dashes,off);
        symbol = SF.lineSymbolizer(name,geom,desc,uom,stroke, offset);
        symbols[0] = symbol;

        
        final FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2(null);
        
        final MutableRule rule1 = SF.rule();
        rule1.symbolizers().add(symbols[0]);
        rule1.symbolizers().add(symbols[1]);
        rule1.symbolizers().add(symbols[2]);
        final Filter autoroute = FF.equals(FF.property("CATEGORIE"), FF.literal("Autoroute"));
        rule1.setFilter(autoroute);
        
        
        symbols = new Symbolizer[3];

        offset = FF.literal(0);
        uom = SI.METER;
        geom = null;
        name = "unIdQuelconque";
        desc = StyleConstants.DEFAULT_DESCRIPTION;

        //bande blanche en direct au milieu de la route
        stroke = SF.stroke(Color.WHITE, 0.20d, new float[]{1,3});
        symbol = SF.lineSymbolizer(name,geom,desc,uom,stroke, offset);
        symbols[2] = symbol;

        //partie roulante
        fill = null;
        strk = null;
        color = SF.literal(Color.BLACK);
        opacity = FF.literal(1d);
        width = FF.literal(8d);
        join = FF.literal("round");
        cap = FF.literal("round");
        dashes = null;
        off = FF.literal(0d);

        stroke = SF.stroke(color,opacity,width,join,cap,dashes,off);
        symbol = SF.lineSymbolizer(name,geom,desc,uom,stroke, offset);
        symbols[1] = symbol;

        //trottoir
        fill = null;
        strk = null;
        color = SF.literal(Color.DARK_GRAY);
        opacity = FF.literal(1d);
        width = FF.literal(10.5d);
        join = FF.literal("round");
        cap = FF.literal("round");
        dashes = null;
        off = FF.literal(0d);

        stroke = SF.stroke(color,opacity,width,join,cap,dashes,off);
        symbol = SF.lineSymbolizer(name,geom,desc,uom,stroke, offset);
        symbols[0] = symbol;
        
        
        final MutableRule rule2 = SF.rule();
        rule2.symbolizers().add(symbols[0]);
        rule2.symbolizers().add(symbols[1]);
        rule2.symbolizers().add(symbols[2]);
        final Filter notAutoroute = FF.not(autoroute);
        rule2.setFilter(notAutoroute);
        
        
        final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
        fts.rules().add(rule1);
        fts.rules().add(rule2);
        
        final MutableStyle style = SF.style();
        style.featureTypeStyles().add(fts);

        layer.styles().add(style);

        XMLUtilities xml = new XMLUtilities();
        try {
            xml.writeSLD(new File("test.xml"), sld, StyledLayerDescriptor.V_1_0_0);
        } catch (JAXBException ex) {
            Logger.getLogger(ContextBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

        return style;
    }

    public static MutableStyle createLabeledPointStyle(){

        Unit uom = NonSI.PIXEL;
        String geom = null;
        String name = "unIdQuelconque";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;
        Symbolizer[] ps = new Symbolizer[2];
        List<GraphicalSymbol> symbols = null;
        Expression opacity = null;
        Expression expSize = null;
        Expression expRotation = null;
        AnchorPoint anchor = null;
        Displacement disp = null;
        Graphic graphic = null;

        symbols = new ArrayList<GraphicalSymbol>();
        symbols.add(createMark("cross",Color.RED));
        opacity = FF.literal(1d);
        expSize = FF.literal(16);
        expRotation = FF.literal(0);
        anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
        disp = SF.displacement(0, 0);
        graphic = SF.graphic(symbols, opacity, expSize, expRotation, anchor,disp);
        ps[0] = SF.pointSymbolizer(name,geom,desc,uom,graphic);

        Expression label = FF.property("ALTITUDE");
        Expression weight = StyleConstants.FONT_WEIGHT_BOLD;
        Expression style = StyleConstants.FONT_STYLE_NORMAL;
        Expression size = FF.literal(14);
        List<Expression> families = new ArrayList<Expression>();
        Font font = SF.font(families, style, weight, size);
        LabelPlacement placement = SF.pointPlacement(
                SF.anchorPoint(0,0),
                SF.displacement(10, -10),
                FF.literal(45));
        Halo halo = SF.halo(Color.RED, 3f);
        Fill fill = SF.fill(Color.WHITE);
        TextSymbolizer text = SF.textSymbolizer(name,geom,desc,uom,label, font, placement, halo, fill);
        ps[1] = text;

        return SF.style(ps);
    }
    
    public static Mark createMark(String strWkn, Color inside){

        Graphic graphic = SF.graphic();                                    //OK
        GraphicFill graphicFill = SF.graphicFill(
                graphic.graphicalSymbols(),
                graphic.getOpacity(),
                graphic.getSize(),
                graphic.getRotation(),
                graphic.getAnchorPoint(),
                graphic.getDisplacement());                        //OK

        Expression fillColor = SF.literal(inside);                           //OK
        Expression fillOpacity = FF.literal(1d);                               //OK

        Fill fill = SF.fill(null, fillColor, fillOpacity);


        Expression color = SF.literal(Color.BLACK);                           //OK
        Expression opacity = FF.literal(1d);                               //OK
        Expression width = FF.literal(1d);                                //OK
        Expression join = FF.literal("bevel");                             //OK
        Expression cap = FF.literal("round");                              //OK
        float[] dashes = null;                                                             //OK
        Expression strokeOffset = FF.literal(0d);                          //OK

        Stroke stroke = SF.stroke(color, opacity, width, join, cap, dashes, strokeOffset);


//        OnLineResource rsc = null;                                                                  //OK
//        Icon icon = null;                                                                           //OK
//        try {
//            icon = new ImageIcon(ImageIO.read(new File("motif2.png")));
//        } catch (IOException ex) {ex.printStackTrace();}
//
//        String format = "image/png";                                                                //OK
//        int index = 0;                                                                              //OK
//        ExternalMark external = STYLE_BUILDER.createExternalMark(rsc, icon, format, index);


        Expression wkn = FF.literal(strWkn);                               //OK
        Mark mark = SF.mark(wkn, fill, stroke);

        return mark;

    }

    public static LineSymbolizer createRealWorldLineSymbolizer(){
        Expression offset = FF.literal(0);
        Unit uom = SI.METER;
        String geom = null;
        String name = "unIdQuelconque";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        Stroke stroke = SF.stroke(Color.BLACK, 8);
        LineSymbolizer symbol = SF.lineSymbolizer(name,geom,desc,uom,stroke, offset);

        return symbol;
    }

    public static LineSymbolizer createDashLineSymbolizer(){
        Expression offset = FF.literal(0);
        Unit uom = NonSI.PIXEL;
        String geom = null;
        String name = "unIdQuelconque";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        Stroke stroke = SF.stroke(Color.RED, 5,new float[]{10,5});
        LineSymbolizer symbol = SF.lineSymbolizer(name,geom,desc,uom,stroke, offset);

        return symbol;
    }

    public static MutableStyle createRealPointStyle(){

        Unit uom = SI.METER;
        String geom = null;
        String name = "unIdQuelconque";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;
        PointSymbolizer[] ps = new PointSymbolizer[5];
        List<GraphicalSymbol> symbols = null;
        Expression opacity = null;
        Expression expSize = null;
        Expression expRotation = null;
        AnchorPoint anchor = null;
        Displacement disp = null;
        Graphic graphic = null;


        symbols = new ArrayList<GraphicalSymbol>();
        symbols.add(createMark("star",Color.YELLOW));
        opacity = FF.literal(1d);
        expSize = FF.literal(20);
        expRotation = FF.literal(0);
        anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
        disp = SF.displacement(0, 0);
        graphic = SF.graphic(symbols, opacity, expSize, expRotation, anchor,disp);
        ps[0] = SF.pointSymbolizer(name,geom,desc,uom,graphic);

        symbols = new ArrayList<GraphicalSymbol>();
        symbols.add(createMark("square",Color.BLUE));
        opacity = FF.literal(1d);
        expSize = FF.literal(20);
        expRotation = FF.literal(0);
        anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
        disp = SF.displacement(22, 0);
        graphic = SF.graphic(symbols, opacity, expSize, expRotation, anchor,disp);
        ps[1] = SF.pointSymbolizer(name,geom,desc,uom,graphic);

        symbols = new ArrayList<GraphicalSymbol>();
        symbols.add(createMark("cross",Color.RED));
        opacity = FF.literal(1d);
        expSize = FF.literal(20);
        expRotation = FF.literal(0);
        anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
        disp = SF.displacement(0, 22);
        graphic = SF.graphic(symbols, opacity, expSize, expRotation, anchor,disp);
        ps[2] = SF.pointSymbolizer(name,geom,desc,uom,graphic);

        symbols = new ArrayList<GraphicalSymbol>();
        symbols.add(createMark("circle",Color.GREEN));
        opacity = FF.literal(1d);
        expSize = FF.literal(20);
        expRotation = FF.literal(0);
        anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
        disp = SF.displacement(22,22);
        graphic = SF.graphic(symbols, opacity, expSize, expRotation, anchor,disp);
        ps[3] = SF.pointSymbolizer(name,geom,desc,uom,graphic);

        symbols = new ArrayList<GraphicalSymbol>();
        symbols.add(createMark("arrow",Color.MAGENTA));
        opacity = FF.literal(1d);
        expSize = FF.literal(20);
        expRotation = FF.literal(0);
        anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
        disp = SF.displacement(0, 48);
        graphic = SF.graphic(symbols, opacity, expSize, expRotation, anchor,disp);
        ps[4] = SF.pointSymbolizer(name,geom,desc,uom,graphic);


        return SF.style(ps);
    }

    public static MutableStyle createRealTrainStyle(){

        Symbolizer[] symbols = new Symbolizer[5];

        Expression offset = FF.literal(0);
        Unit uom = SI.METER;
        String geom = null;
        String name = "unIdQuelconque";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        //support de voie
        Stroke stroke = SF.stroke(Color.DARK_GRAY, 5, new float[]{1f,3f});
        Symbolizer symbol = SF.lineSymbolizer(name,geom,desc,uom,stroke, offset);
        symbols[0] = symbol;

        //trame de fer
        stroke = SF.stroke(Color.BLACK, 3);
        symbol = SF.lineSymbolizer(name,geom,desc,uom,stroke, offset);
        symbols[1] = symbol;

        //gravié
        stroke = SF.stroke(Color.WHITE, 2f);
        symbol = SF.lineSymbolizer(name,geom,desc,uom,stroke, offset);
        symbols[2] = symbol;

        //support de voie
        stroke = SF.stroke(Color.DARK_GRAY, 2, new float[]{1f,3f});
        symbol = SF.lineSymbolizer(name,geom,desc,uom,stroke, offset);
        symbols[3] = symbol;

        symbols[4] = createLineLabel();


        return SF.style(symbols);
    }

    public static TextSymbolizer createLineLabel(){
        Unit uom = SI.METER;
        String geom = null;
        String name = "unIdQuelconque";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;


        Expression label = FF.literal("Rail road");
        Expression weight = StyleConstants.FONT_WEIGHT_BOLD;
        Expression style = StyleConstants.FONT_STYLE_ITALIC;
        Expression size = FF.literal(30);
        List<Expression> families = new ArrayList<Expression>();
        Font font = SF.font(families, style, weight, size);
        LabelPlacement placement = SF.linePlacement(
                FF.literal(5),
                FF.literal(10),
                FF.literal(50),
                true, 
                true, 
                true);
        Halo halo = SF.halo(Color.BLACK, 3f);
        Fill fill = SF.fill(Color.WHITE);
        TextSymbolizer text = SF.textSymbolizer(name,geom,desc,uom,label, font, placement, halo, fill);
        
        return text;
    }
    
    public static MutableStyle createRealBuildingStyle(){

        Symbolizer[] symbols = new Symbolizer[2];

        Fill fill;
        Stroke stroke;
        PolygonSymbolizer symbol;
        Displacement disp = SF.displacement(0,0);
        Expression offset = FF.literal(0);
        Unit uom = SI.METER;
        String geom = null;
        String name = "unIdQuelconque";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        //decalage noir,  ombre de la maison
        disp = SF.displacement(3, -3);
        offset = FF.literal(0);
        fill = SF.fill(Color.DARK_GRAY);
        stroke = SF.stroke(Color.BLACK, 0d);
        symbol = SF.polygonSymbolizer(name,geom,desc,uom,stroke, fill, disp, offset);
        symbols[0] = symbol;

        //bord du toit
        disp = SF.displacement(0,0);
        offset = FF.literal(0);
        fill = SF.fill(new Color(1f,0.6f,0.6f));
        stroke = SF.stroke(Color.RED, 0.2d);
        symbol = SF.polygonSymbolizer(name,geom,desc,uom,stroke, fill, disp, offset);
        symbols[1] = symbol;

//        //un peu plus clair
//        disp = STYLE_BUILDER.createDisplacement(0,0);
//        offset = STYLE_BUILDER.literalExpression(-3);
//        fill = STYLE_BUILDER.createFill(new Color(1f,0.3f,0.3f));
//        stroke = STYLE_BUILDER.createStroke(Color.RED, 0d);
//        symbol = STYLE_BUILDER.createPolygonSymbolizer(stroke, fill, disp, offset, uom, geom, name, desc);
//        symbols[2] = symbol;
//
//        disp = STYLE_BUILDER.createDisplacement(0,0);
//        offset = STYLE_BUILDER.literalExpression(-6);
//        fill = STYLE_BUILDER.createFill(new Color(1f,0.6f,0.6f));
//        stroke = STYLE_BUILDER.createStroke(Color.RED, 0d);
//        symbol = STYLE_BUILDER.createPolygonSymbolizer(stroke, fill, disp, offset, uom, geom, name, desc);
//        symbols[3] = symbol;



        return SF.style(symbols);
    }

    public static MutableStyle createRealWaterStyle(){

        Symbolizer[] symbols = new Symbolizer[3];

        Fill fill;
        Stroke stroke;
        PolygonSymbolizer symbol;
        Displacement disp = SF.displacement(0,0);
        Expression offset = FF.literal(0);
        Unit uom = NonSI.PIXEL;
        String geom = null;
        String name = "unIdQuelconque";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;


        //bord de mer
        disp = SF.displacement(0,0);
        offset = FF.literal(20);
        fill = SF.fill(null, SF.literal(new Color(0.7f,0.7f,1f)),FF.literal(0.7f));
        stroke = SF.stroke(Color.BLUE.brighter(), 0d);
        symbol = SF.polygonSymbolizer(name,geom,desc,uom,stroke, fill, disp, offset);
        symbols[0] = symbol;

        //un peu plus sombre
        disp = SF.displacement(0,0);
        offset = FF.literal(10);
        fill = SF.fill(null, SF.literal(new Color(0.5f,0.5f,1f)),FF.literal(0.7f));
        stroke = SF.stroke(Color.BLUE, 0d);
        symbol = SF.polygonSymbolizer(name,geom,desc,uom,stroke, fill, disp, offset);
        symbols[1] = symbol;

        //un peu plus sombre
        disp = SF.displacement(0,0);
        offset = FF.literal(0);
        fill = SF.fill(null, SF.literal(new Color(0.3f,0.3f,1f)),FF.literal(0.7f));
        stroke = SF.stroke(Color.BLUE.darker(), 0d);
        symbol = SF.polygonSymbolizer(name,geom,desc,uom,stroke, fill, disp, offset);
        symbols[2] = symbol;

        return SF.style(symbols);
    }

    public static boolean copier( File source, File destination) {
        boolean resultat = false;

        // Declaration des flux
        java.io.FileInputStream sourceFile = null;
        java.io.FileOutputStream destinationFile = null;

        try {
            // Création du fichier :
            destination.createNewFile();

            // Ouverture des flux
            sourceFile = new java.io.FileInputStream(source);
            destinationFile = new java.io.FileOutputStream(destination);

            // Lecture par segment de 0.5Mo
            byte buffer[] = new byte[512 * 1024];
            int nbLecture;

            while ((nbLecture = sourceFile.read(buffer)) != -1) {
                destinationFile.write(buffer, 0, nbLecture);
            }

            // Copie réussie
            resultat = true;
        } catch (java.io.FileNotFoundException f) {
        } catch (java.io.IOException e) {
        } finally {
            // Quoi qu'il arrive, on ferme les flux
            try {
                sourceFile.close();
            } catch (Exception e) {
            }
            try {
                destinationFile.close();
            } catch (Exception e) {
            }
        }
        return (resultat);
    }
    
    public static CoverageReader readWorldImage( File gridFile ) throws IOException, NoninvertibleTransformException{
       WorldImageFactory factory = new WorldImageFactory();
       return factory.createMosaicReader(gridFile);
    }

    
    public static MutableStyle createCategorizeStyle() {

        final Map<Expression,Expression> values = new HashMap<Expression,Expression>();
        values.put(StyleConstants.CATEGORIZE_LESS_INFINITY, SF.literal(Color.WHITE));
        values.put(FF.literal(0), SF.literal(Color.BLUE));
        values.put(FF.literal(19), SF.literal(Color.YELLOW));
        values.put(FF.literal(20), SF.literal(Color.GREEN));
        values.put(FF.literal(21), SF.literal(Color.YELLOW));
        values.put(FF.literal(30), SF.literal(Color.RED));
        final Literal lookup = StyleConstants.DEFAULT_CATEGORIZE_LOOKUP;
        final Literal fallback = StyleConstants.DEFAULT_FALLBACK;
        final Function categorizeFunction = SF.categorizeFunction(
                lookup, values, ThreshholdsBelongTo.PRECEDING, fallback);

        final ChannelSelection selection = SF.channelSelection(
                SF.selectedChannelType("0", FF.literal(1)));


        Expression opacity = FF.literal(1f);
        OverlapBehavior overlap = OverlapBehavior.LATEST_ON_TOP;
        ColorMap colorMap = SF.colorMap(categorizeFunction);
        ContrastEnhancement enchance = StyleConstants.DEFAULT_CONTRAST_ENHANCEMENT;
        ShadedRelief relief = StyleConstants.DEFAULT_SHADED_RELIEF;
        Symbolizer outline = null; //createRealWorldLineSymbolizer();
        Unit uom = NonSI.FOOT;
        String geom = StyleConstants.DEFAULT_GEOM;
        String name = "raster symbol name";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        RasterSymbolizer symbol = SF.rasterSymbolizer(name,geom,desc,uom,opacity, selection, overlap, colorMap, enchance, relief, outline);

        return SF.style(symbol);
    }

//    private static MutableStyle createGridMarkStyle() {
////        Symbolizer symbol1 = SF.createRasterSymbolizer();
//        Symbolizer symbol2 = new VectorFieldSymbolizer();
//        return SF.style(new Symbolizer[]{symbol2});
//    }
    
}

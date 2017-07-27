
package org.geotoolkit.pending.demo.symbology;


import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.map.ElevationModel;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.geotoolkit.style.function.ThreshholdsBelongTo;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
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
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.ShadedRelief;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;

import javax.measure.Unit;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.data.shapefile.ShapefileFeatureStore;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.apache.sis.measure.Units;
import org.geotoolkit.storage.coverage.CoverageResource;

import static org.geotoolkit.style.StyleConstants.*;
import org.opengis.style.ExternalMark;

/**
 *
 * @author jsorel
 */
public class Styles {

    /**
     * Factories used in all symbology exemples.
     */
    protected static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    protected static final MutableSLDFactory SLDF = new DefaultSLDFactory();
    protected static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                                                   new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

    //////////////////////////////////////////////////////////////////////
    // POINT SYMBOLIZER //////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    public static MutableStyle defaultPoint(){
        final PointSymbolizer symbol = DEFAULT_POINT_SYMBOLIZER;
        final MutableStyle style = SF.style(symbol);
        return style;
    }

    public static MutableStyle markPoint(){

        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;

        //the visual element
        final Expression size = FF.literal(12);
        final Expression opacity = LITERAL_ONE_FLOAT;
        final Expression rotation = LITERAL_ONE_FLOAT;
        final AnchorPoint anchor = DEFAULT_ANCHOR_POINT;
        final Displacement disp = DEFAULT_DISPLACEMENT;

        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        final Stroke stroke = SF.stroke(Color.BLACK, 2);
        final Fill fill = SF.fill(Color.RED);
        final Mark mark = SF.mark(MARK_CIRCLE, fill, stroke);
        symbols.add(mark);
        final Graphic graphic = SF.graphic(symbols, opacity, size, rotation, anchor, disp);

        final PointSymbolizer symbolizer = SF.pointSymbolizer(name,geometry,desc,unit, graphic);
        final MutableStyle style = SF.style(symbolizer);
        return style;
    }

    public static MutableStyle imagePoint() throws URISyntaxException{

        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;

        //the visual element
        final Expression size = FF.literal(12);
        final Expression opacity = LITERAL_ONE_FLOAT;
        final Expression rotation = LITERAL_ONE_FLOAT;
        final AnchorPoint anchor = DEFAULT_ANCHOR_POINT;
        final Displacement disp = DEFAULT_DISPLACEMENT;

        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();

        final GraphicalSymbol external = SF.externalGraphic(
                    SF.onlineResource(Styles.class.getResource("/data/fish.png").toURI()),
                    "image/png",null);
        symbols.add(external);
        final Graphic graphic = SF.graphic(symbols, opacity, size, rotation, anchor, disp);

        final PointSymbolizer symbolizer = SF.pointSymbolizer(name,geometry,desc,unit, graphic);
        final MutableStyle style = SF.style(symbolizer);
        return style;
    }

    public static MutableStyle ttfPoint() throws URISyntaxException{

        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;

        //the visual element
        final Expression size = FF.literal(32);
        final Expression opacity = LITERAL_ONE_FLOAT;
        final Expression rotation = LITERAL_ONE_FLOAT;
        final AnchorPoint anchor = DEFAULT_ANCHOR_POINT;
        final Displacement disp = DEFAULT_DISPLACEMENT;

        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();

        final Stroke stroke = SF.stroke(Color.BLACK, 1);
        final Fill fill = SF.fill(Color.RED);
        final ExternalMark external = SF.externalMark(
                    SF.onlineResource(IconBuilder.FONTAWESOME.toURI()),
                    "ttf",FontAwesomeIcons.ICON_DELICIOUS.codePointAt(0));
        final Mark mark = SF.mark(external, fill, stroke);

        symbols.add(mark);
        final Graphic graphic = SF.graphic(symbols, opacity, size, rotation, anchor, disp);

        final PointSymbolizer symbolizer = SF.pointSymbolizer(name,geometry,desc,unit, graphic);
        final MutableStyle style = SF.style(symbolizer);
        return style;
    }

    public static MutableStyle ttfPoint2() throws URISyntaxException{

        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;

        //the visual element
        final Expression size = FF.literal(12);
        final Expression opacity = LITERAL_ONE_FLOAT;
        final Expression rotation = LITERAL_ONE_FLOAT;
        final AnchorPoint anchor = DEFAULT_ANCHOR_POINT;
        final Displacement disp = DEFAULT_DISPLACEMENT;

        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();

        final Stroke stroke = SF.stroke(Color.BLACK, 1);
        final Fill fill = SF.fill(Color.RED);
        final Expression external = FF.literal("ttf:Dialog?char=0x2A");
        final Mark mark = SF.mark(external, fill, stroke);

        symbols.add(mark);
        final Graphic graphic = SF.graphic(symbols, opacity, size, rotation, anchor, disp);

        final PointSymbolizer symbolizer = SF.pointSymbolizer(name,geometry,desc,unit, graphic);
        final MutableStyle style = SF.style(symbolizer);
        return style;
    }


    //////////////////////////////////////////////////////////////////////
    // LINE SYMBOLIZER //////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    public static MutableStyle defaultLine(){
        final LineSymbolizer symbol = DEFAULT_LINE_SYMBOLIZER;
        final MutableStyle style = SF.style(symbol);
        return style;
    }

    public static MutableStyle colorLine(){

        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;
        final Expression offset = LITERAL_ZERO_FLOAT;

        //the visual element
        final Expression color = SF.literal(Color.BLUE);
        final Expression width = FF.literal(4);
        final Expression opacity = LITERAL_ONE_FLOAT;
        final Stroke stroke = SF.stroke(color,width,opacity);

        final LineSymbolizer symbolizer = SF.lineSymbolizer(name,geometry,desc,unit,stroke,offset);
        final MutableStyle style = SF.style(symbolizer);
        return style;
    }

    public static MutableStyle dashedLine() throws URISyntaxException{

        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;
        final Expression offset = LITERAL_ONE_FLOAT;

        //the visual element
        final Expression color = SF.literal(Color.BLUE);
        final Expression width = FF.literal(2);
        final Expression opacity = LITERAL_ONE_FLOAT;
        final Expression linecap = STROKE_CAP_BUTT;
        final Expression linejoin = STROKE_JOIN_ROUND;
        final float[] dashes = new float[]{8,4,2,2,2,2,2,4};
        final Expression dashOffset = LITERAL_ZERO_FLOAT;
        final Stroke stroke = SF.stroke(color,opacity,width,linejoin,linecap,dashes,dashOffset);

        final LineSymbolizer symbolizer = SF.lineSymbolizer(name,geometry,desc,unit,stroke,offset);
        final MutableStyle style = SF.style(symbolizer);
        return style;
    }

    public static MutableStyle uomLine() throws URISyntaxException{

        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.METRE;
        final Expression offset = LITERAL_ZERO_FLOAT;

        //the visual element
        final Expression color = SF.literal(Color.BLUE);
        final Expression width = FF.literal(400);
        final Expression opacity = LITERAL_ONE_FLOAT;
        final Stroke stroke = SF.stroke(color,width,opacity);

        final LineSymbolizer symbolizer = SF.lineSymbolizer(name,geometry,desc,unit,stroke,offset);
        final MutableStyle style = SF.style(symbolizer);
        return style;
    }

    public static MutableStyle graphicFillLine() throws URISyntaxException{

        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;
        final Expression offset = LITERAL_ONE_FLOAT;

        //the stroke fill
        //a pattern that will be repeated like a mosaic
        final Expression size = FF.literal(12);
        final Expression opacity = LITERAL_ONE_FLOAT;
        final Expression rotation = LITERAL_ONE_FLOAT;
        final AnchorPoint anchor = DEFAULT_ANCHOR_POINT;
        final Displacement disp = DEFAULT_DISPLACEMENT;
        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        final Stroke fillStroke = SF.stroke(Color.BLACK, 2);
        final Fill fill = SF.fill(Color.RED);
        final Mark mark = SF.mark(MARK_CIRCLE, fill, fillStroke);
        symbols.add(mark);
        final GraphicFill graphicfill = SF.graphicFill(symbols, opacity, size, rotation, anchor, disp);

        //the visual element
        final Expression color = SF.literal(Color.BLUE);
        final Expression width = FF.literal(4);
        final Expression linecap = STROKE_CAP_ROUND;
        final Expression linejoin = STROKE_JOIN_BEVEL;
        final float[] dashes = new float[]{8,4,2,2,2,2,2,4};
        final Expression dashOffset = LITERAL_ZERO_FLOAT;
        final Stroke stroke = SF.stroke(graphicfill,color,opacity,width,linejoin,linecap,dashes,dashOffset);

        final LineSymbolizer symbolizer = SF.lineSymbolizer(name,geometry,desc,unit,stroke,offset);
        final MutableStyle style = SF.style(symbolizer);
        return style;
    }

    public static MutableStyle graphicStrokeLine() throws URISyntaxException{

        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;
        final Expression offset = LITERAL_ONE_FLOAT;

        //the stroke fill
        //a pattern that will be repeated like a mosaic
        final Expression size = FF.literal(12);
        final Expression opacity = LITERAL_ONE_FLOAT;
        final Expression rotation = LITERAL_ONE_FLOAT;
        final AnchorPoint anchor = DEFAULT_ANCHOR_POINT;
        final Displacement disp = DEFAULT_DISPLACEMENT;
        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        final GraphicalSymbol external = SF.externalGraphic(
                    SF.onlineResource(Styles.class.getResource("/data/fish.png").toURI()),
                    "image/png",null);
        symbols.add(external);
        final Graphic graphic = SF.graphic(symbols, opacity, size, rotation, anchor, disp);

        final Expression initialGap = LITERAL_ZERO_FLOAT;
        final Expression strokeGap = FF.literal(10);
        final GraphicStroke graphicStroke = SF.graphicStroke(graphic,strokeGap,initialGap);

        //the visual element
        final Expression color = SF.literal(Color.BLUE);
        final Expression width = FF.literal(4);
        final Expression linecap = STROKE_CAP_ROUND;
        final Expression linejoin = STROKE_JOIN_BEVEL;
        final float[] dashes = new float[]{8,4,2,2,2,2,2,4};
        final Expression dashOffset = LITERAL_ZERO_FLOAT;
        final Stroke stroke = SF.stroke(graphicStroke,color,opacity,width,linejoin,linecap,dashes,dashOffset);

        final LineSymbolizer symbolizer = SF.lineSymbolizer(name,geometry,desc,unit,stroke,offset);
        final MutableStyle style = SF.style(symbolizer);
        return style;
    }


    //////////////////////////////////////////////////////////////////////
    // POLYGON SYMBOLIZER ////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    public static MutableStyle defaultPolygon(){
        final PolygonSymbolizer symbol = DEFAULT_POLYGON_SYMBOLIZER;
        final MutableStyle style = SF.style(symbol);
        return style;
    }

    public static MutableStyle colorPolygon(){

        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;
        final Displacement disp = DEFAULT_DISPLACEMENT;
        final Expression offset = LITERAL_ZERO_FLOAT;

        //stroke element
        final Expression color = SF.literal(Color.BLUE);
        final Expression width = FF.literal(4);
        final Expression opacity = LITERAL_ONE_FLOAT;
        final Stroke stroke = SF.stroke(color,width,opacity);

        //fill element
        final Fill fill = SF.fill(Color.ORANGE);

        final PolygonSymbolizer symbolizer = SF.polygonSymbolizer(name,geometry,desc,unit,stroke,fill,disp,offset);
        final MutableStyle style = SF.style(symbolizer);
        return style;
    }

    public static MutableStyle graphicFillPolygon(){

        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;
        final Displacement disp = DEFAULT_DISPLACEMENT;
        final Expression offset = LITERAL_ZERO_FLOAT;

        //stroke element
        final Expression color = SF.literal(Color.BLUE);
        final Expression width = FF.literal(2);
        final Expression opacity = LITERAL_ONE_FLOAT;
        final Stroke stroke = SF.stroke(color,width,opacity);

        //fill element
        //a pattern that will be repeated like a mosaic
        final Expression size = FF.literal(12);
        final Expression rotation = LITERAL_ONE_FLOAT;
        final AnchorPoint anchor = DEFAULT_ANCHOR_POINT;
        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        final Stroke fillStroke = SF.stroke(Color.BLACK, 2);
        final Fill pattern = SF.fill(Color.BLUE);
        final Mark mark = SF.mark(MARK_CROSS, pattern, fillStroke);
        symbols.add(mark);
        final GraphicFill graphicfill = SF.graphicFill(symbols, opacity, size, rotation, anchor, disp);
        final Fill fill = SF.fill(graphicfill, color, opacity);

        final PolygonSymbolizer symbolizer = SF.polygonSymbolizer(name,geometry,desc,unit,stroke,fill,disp,offset);
        final MutableStyle style = SF.style(symbolizer);
        return style;
    }

    public static MutableStyle shadowPolygon(){

        //first symbol classic
        final PolygonSymbolizer symbol1 = SF.polygonSymbolizer(
                null,
                SF.fill(Color.ORANGE),
                null);

        //second symbol for the shadow
        final PolygonSymbolizer symbol2 = SF.polygonSymbolizer(
                "mySymbol",
                (String)null,
                DEFAULT_DESCRIPTION,
                Units.POINT,
                null,
                SF.fill(Color.DARK_GRAY),
                SF.displacement(3, -4),
                LITERAL_ZERO_FLOAT);

        final MutableStyle style = SF.style(symbol2,symbol1);
        return style;
    }

    public static MutableStyle offsetPolygon(){

        //we produce a gradient border effect by combining several
        //symbolizer progressivly smaller

        final PolygonSymbolizer symbol1 = SF.polygonSymbolizer(
                "mySymbol",
                (String)null,
                DEFAULT_DESCRIPTION,
                Units.POINT,
                null,
                SF.fill(new Color(255,0, 0)),
                DEFAULT_DISPLACEMENT,
                FF.literal(0));

        final PolygonSymbolizer symbol2 = SF.polygonSymbolizer(
                "mySymbol",
                (String)null,
                DEFAULT_DESCRIPTION,
                Units.POINT,
                null,
                SF.fill(new Color(255, 70, 70)),
                DEFAULT_DISPLACEMENT,
                FF.literal(-10));

        final PolygonSymbolizer symbol3 = SF.polygonSymbolizer(
                "mySymbol",
                (String)null,
                DEFAULT_DESCRIPTION,
                Units.POINT,
                null,
                SF.fill(new Color(255, 140, 140)),
                DEFAULT_DISPLACEMENT,
                FF.literal(-20));

        final PolygonSymbolizer symbol4 = SF.polygonSymbolizer(
                "mySymbol",
                (String)null,
                DEFAULT_DESCRIPTION,
                Units.POINT,
                null,
                SF.fill(new Color(255, 210, 210)),
                DEFAULT_DISPLACEMENT,
                FF.literal(-30));

        final MutableStyle style = SF.style(symbol1,symbol2,symbol3,symbol4);
        return style;
    }

    //////////////////////////////////////////////////////////////////////
    // TEXT SYMBOLIZER ///////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    public static MutableStyle defaultText(){
        final TextSymbolizer symbol = DEFAULT_TEXT_SYMBOLIZER;
        final MutableStyle style = SF.style(DEFAULT_POLYGON_SYMBOLIZER,symbol);
        return style;
    }

    public static MutableStyle centeredText(){

        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;
        final Expression label = FF.property("CNTRY_NAME");
        final Font font = SF.font(
                FF.literal("Arial"),
                FONT_STYLE_ITALIC,
                FONT_WEIGHT_BOLD,
                FF.literal(14));
        final LabelPlacement placement = SF.pointPlacement();
        final Halo halo = SF.halo(Color.WHITE, 1);
        final Fill fill = SF.fill(Color.BLUE);

        final TextSymbolizer symbol = SF.textSymbolizer(name, geometry, desc, unit, label, font, placement, halo, fill);

        final MutableStyle style = SF.style(DEFAULT_POLYGON_SYMBOLIZER,symbol);
        return style;
    }

    public static MutableStyle linedText(){

        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;
        final Expression label = FF.property("CNTRY_NAME");
        final Font font = SF.font(
                FF.literal("Arial"),
                FONT_STYLE_ITALIC,
                FONT_WEIGHT_BOLD,
                FF.literal(14));
        final LabelPlacement placement = SF.linePlacement(FF.literal(0));
        final Halo halo = SF.halo(Color.WHITE, 1);
        final Fill fill = SF.fill(Color.BLUE);

        final TextSymbolizer symbol = SF.textSymbolizer(name, geometry, desc, unit, label, font, placement, halo, fill);

        final MutableStyle style = SF.style(DEFAULT_POLYGON_SYMBOLIZER,symbol);
        return style;
    }

    //////////////////////////////////////////////////////////////////////
    // RASTER SYMBOLIZER /////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    public static MutableStyle defaultRaster(){
        final RasterSymbolizer symbol = DEFAULT_RASTER_SYMBOLIZER;
        final MutableStyle style = SF.style(symbol);
        return style;
    }

    public static MutableStyle customRaster(){

        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;
        final Expression opacity = LITERAL_ONE_FLOAT;
        final ChannelSelection channels = null;
        final OverlapBehavior overlap = null;
        final ColorMap colormap = null;
        final ContrastEnhancement enhance = null;
        final ShadedRelief relief = null;
        final Symbolizer outline = null;

        final RasterSymbolizer symbol = SF.rasterSymbolizer(
                name,(String)null,desc,unit,opacity,
                channels,overlap,colormap,enhance,relief,outline);
        final MutableStyle style = SF.style(symbol);
        return style;
    }

    public static MutableStyle colorInterpolationRaster(){

        final List<InterpolationPoint> values = new ArrayList<>();
        values.add( SF.interpolationPoint(1003, SF.literal(new Color(46,154,88))));
        values.add( SF.interpolationPoint(1800, SF.literal(new Color(251,255,128))));
        values.add( SF.interpolationPoint(2800, SF.literal(new Color(224,108,31))));
        values.add( SF.interpolationPoint(3500, SF.literal(new Color(200,55,55))));
        values.add( SF.interpolationPoint(4397, SF.literal(new Color(215,244,244 ))));
        final Expression lookup = DEFAULT_CATEGORIZE_LOOKUP;
        final Literal fallback = DEFAULT_FALLBACK;
        final Function function = SF.interpolateFunction(
                lookup, values, Method.COLOR, Mode.LINEAR, fallback);

        final ChannelSelection selection = null;
        final Expression opacity = LITERAL_ONE_FLOAT;
        final OverlapBehavior overlap = OverlapBehavior.LATEST_ON_TOP;
        final ColorMap colorMap = SF.colorMap(function);
        final ContrastEnhancement enchance = SF.contrastEnhancement(LITERAL_ONE_FLOAT,ContrastMethod.NONE);
        final ShadedRelief relief = SF.shadedRelief(LITERAL_ONE_FLOAT);
        final Symbolizer outline = null;
        final Unit uom = Units.POINT;
        final String geom = DEFAULT_GEOM;
        final String name = "raster symbol name";
        final Description desc = DEFAULT_DESCRIPTION;

        final RasterSymbolizer symbol = SF.rasterSymbolizer(
                name,geom,desc,uom,opacity, selection, overlap, colorMap, enchance, relief, outline);
        return SF.style(symbol);
    }

    public static MutableStyle colorCategorizeRaster(){

        final Map<Expression, Expression> values = new HashMap<>();
        values.put( StyleConstants.CATEGORIZE_LESS_INFINITY, SF.literal(new Color(46,154,88)));
        values.put( new DefaultLiteral<Number>(1003), SF.literal(new Color(46,154,88)));
        values.put( new DefaultLiteral<Number>(1800), SF.literal(new Color(251,255,128)));
        values.put( new DefaultLiteral<Number>(2800), SF.literal(new Color(224,108,31)));
        values.put( new DefaultLiteral<Number>(3500), SF.literal(new Color(200,55,55)));
        values.put( new DefaultLiteral<Number>(4397), SF.literal(new Color(215,244,244 )));
        final Expression lookup = DEFAULT_CATEGORIZE_LOOKUP;
        final Literal fallback = DEFAULT_FALLBACK;
        final Function function = SF.categorizeFunction(lookup, values, ThreshholdsBelongTo.SUCCEEDING, fallback);

        final ChannelSelection selection = null;
        final Expression opacity = LITERAL_ONE_FLOAT;
        final OverlapBehavior overlap = OverlapBehavior.LATEST_ON_TOP;
        final ColorMap colorMap = SF.colorMap(function);
        final ContrastEnhancement enchance = SF.contrastEnhancement(LITERAL_ONE_FLOAT,ContrastMethod.NONE);
        final ShadedRelief relief = SF.shadedRelief(LITERAL_ONE_FLOAT);
        final Symbolizer outline = null;
        final Unit uom = Units.POINT;
        final String geom = DEFAULT_GEOM;
        final String name = "raster symbol name";
        final Description desc = DEFAULT_DESCRIPTION;

        final RasterSymbolizer symbol = SF.rasterSymbolizer(
                name,geom,desc,uom,opacity, selection, overlap, colorMap, enchance, relief, outline);
        return SF.style(symbol);
    }

    /**
     * Relief shading requieres a secondary data for the elevation model.
     */
    public static MapLayer ShadedReliefRaster() throws CoverageStoreException{

        final RasterSymbolizer shadedSymbolizer = SF.rasterSymbolizer(
                null,
                FF.literal(1),
                null,
                null,
                null,
                null,
                SF.shadedRelief(FF.literal(1), true),
                null);


        //create your maplayer with your datas
        final CoverageResource elevationData = null;

        final MapLayer layer = MapBuilder.createCoverageLayer(null, SF.style(shadedSymbolizer));
        final ElevationModel elevationModel = MapBuilder.createElevationModel(elevationData);
        //associate this elevation model to the layer.
        layer.setElevationModel(elevationModel);

        //TIP : a default ElevationModel can be set in the Hints passed to the
        // protrayal service, or set in the default Hint values
        //Hints.putSystemDefault(GO2Hints.KEY_ELEVATION_MODEL, elevationModel);

        return layer;
    }

    //////////////////////////////////////////////////////////////////////
    // RULES /////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    public static MutableStyle scaleRule(){

        final MutableRule over = SF.rule();
        over.setMinScaleDenominator(1000000);
        over.symbolizers().add(SF.polygonSymbolizer(DEFAULT_STROKE, SF.fill(Color.RED), null));

        final MutableRule under = SF.rule();
        under.setMaxScaleDenominator(1000000);
        under.symbolizers().add(SF.polygonSymbolizer(DEFAULT_STROKE, SF.fill(Color.GREEN), null));

        final MutableStyle style = SF.style();
        final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
        fts.rules().add(over);
        fts.rules().add(under);
        style.featureTypeStyles().add(fts);
        return style;
    }

    public static MutableStyle filterRule(){

        final MutableRule over = SF.rule();
        over.setFilter(FF.greaterOrEqual(FF.property("POP_CNTRY"), FF.literal(5000000)));
        over.symbolizers().add(SF.polygonSymbolizer(DEFAULT_STROKE, SF.fill(Color.RED), null));

        final MutableRule under = SF.rule();
        under.setFilter(FF.less(FF.property("POP_CNTRY"), FF.literal(5000000)));
        under.symbolizers().add(SF.polygonSymbolizer(DEFAULT_STROKE, SF.fill(Color.GREEN), null));

        final MutableStyle style = SF.style();
        final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
        fts.rules().add(over);
        fts.rules().add(under);
        style.featureTypeStyles().add(fts);
        return style;
    }

    //////////////////////////////////////////////////////////////////////
    // Unormalize VectorField ////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    public static MutableStyle vectorFieldtRaster(){
        //todo replace by cell symbolizer
        //final Symbolizer symbol = new VectorFieldSymbolizer();
        final MutableStyle style = SF.style();
        return style;
    }


    //////////////////////////////////////////////////////////////////////
    // SAMPLE DATA ///////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    public static MapContext createWorldContext(MutableStyle style) throws DataStoreException, URISyntaxException, MalformedURLException {
        MapContext context = MapBuilder.createContext(CommonCRS.WGS84.normalizedGeographic());
        context.setName("demo context");
        context.setDescription(SF.description("demo context", ""));

        FeatureStore store;
        FeatureCollection fs;

        store = new ShapefileFeatureStore(JAbstractMapPane.class.getResource("/data/world/Countries.shp").toURI());
        fs = store.createSession(true).getFeatureCollection(QueryBuilder.all(store.getNames().iterator().next()));
        if(style == null){
            style = SF.style(SF.polygonSymbolizer(SF.stroke(Color.BLACK, 0),SF.fill(SF.literal(new Color(0f, 0.5f, 0.2f,1f)),FF.literal(0.3f)),null));
        }
        MapLayer layer = MapBuilder.createFeatureLayer(fs, style);
        layer.setDescription(SF.description("world background", ""));
        layer.setName("world background");
        context.layers().add(layer);

        return context;
    }

    public static MapContext createPolygonContext(MutableStyle style) throws DataStoreException, URISyntaxException, MalformedURLException {
        MapContext context = MapBuilder.createContext(CommonCRS.WGS84.normalizedGeographic());
        context.setName("demo context");
        context.setDescription(SF.description("demo context", ""));

        FeatureStore store;
        FeatureCollection fs;

        store = new ShapefileFeatureStore(JAbstractMapPane.class.getResource("/data/world/city.shp").toURI());
        fs = store.createSession(true).getFeatureCollection(QueryBuilder.all(store.getNames().iterator().next()));
        if(style == null){
            style = SF.style(SF.polygonSymbolizer(SF.stroke(Color.BLACK, 0),SF.fill(SF.literal(new Color(0f, 0.5f, 0.2f,1f)),FF.literal(0.3f)),null));
        }
        MapLayer layer = MapBuilder.createFeatureLayer(fs, style);
        layer.setDescription(SF.description("city", ""));
        layer.setName("city");
        context.layers().add(layer);

        return context;
    }

    public static MapContext createRasterContext(MutableStyle style) throws CoverageStoreException, URISyntaxException {
        MapContext context = MapBuilder.createContext(CommonCRS.WGS84.normalizedGeographic());
        context.setName("demo context");
        context.setDescription(SF.description("demo context", ""));

        File cloudFile = new File(Styles.class.getResource("/data/coverage/clouds.jpg").toURI());
        final MapLayer layer = MapBuilder.createCoverageLayer(cloudFile);
        layer.setDescription(SF.description("raster", ""));
        layer.setName("raster");
        context.layers().add(layer);

        return context;
    }

}

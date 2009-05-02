
package org.geotoolkit.gui.swing.debug;


import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.geotools.feature.FeatureCollection;
import org.geotools.coverage.io.CoverageReader;
import org.geotools.data.DataSourceException;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.postgis.PostgisDataStoreFactory;

import org.geotoolkit.util.RandomStyleFactory;
import org.geotoolkit.coverage.wi.WorldImageFactory;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.display2d.ext.vectorfield.GridMarkGraphicBuilder;
import org.geotoolkit.display2d.ext.vectorfield.VectorFieldSymbolizer;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.sld.MutableNamedLayer;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.geotoolkit.style.function.ThreshholdsBelongTo;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.wms.WebMapServer;
import org.geotoolkit.wms.map.WMSMapLayer;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
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
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;

/**
 *
 * @author johann sorel
 */
public class ContextBuilder {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();
    public static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    public static final RandomStyleFactory RANDOM_FACTORY = new RandomStyleFactory();

    public static MapContext buildMNTContext(){
        MapContext context = null;
        MapLayer layer;

        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            File gridFile;

            CoverageReader cover = null;
            gridFile = new File("/home/sorel/GIS_DATA/mnt/16_bit_dem_large.tif");
            try {
                cover = readWorldImage(gridFile);
            } catch (DataSourceException ex) {
                ex.printStackTrace();
            }catch (IOException ex){
                ex.printStackTrace();
            }

            layer = MapBuilder.createCoverageLayer(cover, createRasterStyle(),"bigDem");
            layer.setDescription(SF.description("bigDem", ""));
            context.layers().add(layer);

            context.setCoordinateReferenceSystem(layer.getBounds().getCoordinateReferenceSystem());
            context.setAreaOfInterest(context.getBounds());
            context.setDescription(SF.description("Democontext", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;
    }

    public static MapContext buildArcheoContext(){
        MapContext context = null;
        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            context.layers().add(createVectorLayer("/home/sorel/GIS_DATA/other/countries.shp"));
            context.layers().add(createPostGISLayer());
            context.setDescription(SF.description("Democontext", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;
    }
    
    public static MapContext buildLocalContext() {
        MapContext context = null;
        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            context.layers().add(createVectorLayer("/home/eclesia/GIS_DATA/normandy/bn_Lim_departements.shp"));
            context.layers().add(createVectorLayer("/home/eclesia/GIS_DATA/normandy/TRONCON_ROUTE.SHP"));
            context.layers().add(createVectorLayer("/home/eclesia/GIS_DATA/normandy/Patrimoine.SHP"));
            context.setDescription(SF.description("Democontext", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return context;

    }
    
    public static MapContext buildBMContext(){
        
        MapContext context = null;
        MapLayer layer;

        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            Map<String,Object> params;
            File shape;
            DataStore store;
            FeatureSource<SimpleFeatureType, SimpleFeature> fs;
            MutableStyle style;
            File gridFile;
            
            CoverageReader cover = null;
            gridFile = new File("/home/eclesia/GIS_DATA/bluemarble/bm_winter.png");
            try {
                cover = readWorldImage(gridFile);
            } catch (DataSourceException ex) {
                ex.printStackTrace();
            }catch (IOException ex){
                ex.printStackTrace();
            }
            
            layer = MapBuilder.createCoverageLayer(cover, createRasterStyle(),"blueMarble");
            layer.setDescription(SF.description("blueMarble", ""));
            context.layers().add(layer);
                        
            context.setCoordinateReferenceSystem(layer.getBounds().getCoordinateReferenceSystem());
            context.setAreaOfInterest(context.getBounds());
            context.setDescription(SF.description("Democontext", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;
        
    }
    
    public static MapContext buildGridOneBandContext(){
        
        MapContext context = null;
        MapLayer layer;

        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            File gridFile;
            
            CoverageReader cover = null;
            gridFile = new File("/home/sorel/GIS_DATA/1.tif");
            try {
                cover = readWorldImage(gridFile);
            } catch (DataSourceException ex) {
                ex.printStackTrace();
            }catch (IOException ex){
                ex.printStackTrace();
            }
            
            layer = MapBuilder.createCoverageLayer(cover, createRasterStyle(),"elevation");
            layer.setDescription(SF.description("elevation", ""));
            context.layers().add(layer);
                        
            context.setCoordinateReferenceSystem(layer.getBounds().getCoordinateReferenceSystem());
            context.setAreaOfInterest(context.getBounds());
            context.setDescription(SF.description("Democontext", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;
        
    }
    
    public static MapContext buildSmallVectorContext() {

        MapContext context = null;
        MapLayer layer;

        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            Map<String,Object> params;
            File shape;
            DataStore store;
            FeatureSource<SimpleFeatureType, SimpleFeature> fs;
            MutableStyle style;

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/JEU_VILLE/RESROU_TRONCON_ROUTE.SHP");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = createNewLineStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("line", ""));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/JEU_VILLE/BATIMENT_SURF.SHP");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = createNewPolygonStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("polygons", ""));
            context.layers().add(layer);

            /*params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/JEU_VILLE/clip_POLYGONE.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = createNewPolygonStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("clip", ""));
            context.layers().add(layer);*/

            context.setCoordinateReferenceSystem(layer.getBounds().getCoordinateReferenceSystem());
            context.setAreaOfInterest(context.getBounds());
            context.setDescription(SF.description("small vector context", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;

    }

    public static MapContext buildBigVectorsContext() {

        MapContext context = null;
        MapLayer layer;

        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            Map<String,Object> params;
            File shape;
            DataStore store;
            FeatureSource<SimpleFeatureType, SimpleFeature> fs;
            MutableStyle style;

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/ROADL.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("road", ""));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/RAILRDL.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("railroad", ""));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/TUNDRAA.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("tundra", ""));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/POLBNDA.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("polbnda", ""));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/GRASSA.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("grass", ""));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/CROPA.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("crop", ""));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/INWATERA.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("inwater", ""));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/TREESA.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("trees", ""));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/WATRCRSL.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("watercrsl", ""));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/CONTOURL.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("contour", ""));
            context.layers().add(layer);

            context.setCoordinateReferenceSystem(layer.getBounds().getCoordinateReferenceSystem());
            context.setAreaOfInterest(context.getBounds());
            context.setDescription(SF.description("Democontext", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;

    }

    public static MapContext buildBigRoadContext() {

        MapContext context = null;
        MapLayer layer;

        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            Map<String,Object> params;
            File shape;
            DataStore store;
            FeatureSource<SimpleFeatureType, SimpleFeature> fs;
            MutableStyle style;

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/ROADL.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("road", ""));
            context.layers().add(layer);

//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/INWATERA.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("inwater", ""));
//            context.layers().add(layer);
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/TREESA.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("tree", ""));
//            context.layers().add(layer);
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/CONTOURL.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("contour", ""));
//            context.layers().add(layer);

            context.setCoordinateReferenceSystem(layer.getBounds().getCoordinateReferenceSystem());
            context.setAreaOfInterest(context.getBounds());
            context.setDescription(SF.description("Big road context", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;

    }

    public static MapContext buildCiteTest130Context() {

        MapContext context = null;
        MapLayer layer;

        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            Map<String,Object> params;
            File shape;
            DataStore store;
            FeatureSource<SimpleFeatureType, SimpleFeature> fs;
            MutableStyle style;

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/data-wms-1.3.0/shapefile/cite:BasicPolygons.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("poly", ""));
            context.layers().add(layer);

//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/data-wms-1.3.0/shapefile/cite:Bridges.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("brifges", ""));
//            context.layers().add(layer);
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/data-wms-1.3.0/shapefile/cite:BuildingCenters.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("building center", ""));
//            context.layers().add(layer);
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/data-wms-1.3.0/shapefile/cite:Buildings.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("building", ""));
//            context.layers().add(layer);
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/data-wms-1.3.0/shapefile/cite:DividedRoutes.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("Divided routes", ""));
//            context.layers().add(layer);
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/data-wms-1.3.0/shapefile/cite:Forests.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("forest", ""));
//            context.layers().add(layer);
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/data-wms-1.3.0/shapefile/cite:Lakes.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("lakes", ""));
//            context.layers().add(layer);
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/data-wms-1.3.0/shapefile/cite:MapNeatline.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("map neatline", ""));
//            context.layers().add(layer);
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/data-wms-1.3.0/shapefile/cite:NamedPlaces.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("named places", ""));
//            context.layers().add(layer);
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/data-wms-1.3.0/shapefile/cite:Ponds.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("ponds", ""));
//            context.layers().add(layer);
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/data-wms-1.3.0/shapefile/cite:RoadSegments.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("road segments", ""));
//            context.layers().add(layer);
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/data-wms-1.3.0/shapefile/cite:Streams.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("streams", ""));
//            context.layers().add(layer);


            context.setCoordinateReferenceSystem(layer.getBounds().getCoordinateReferenceSystem());
            context.setAreaOfInterest(context.getBounds());
            context.setDescription(SF.description("Democontext", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;

    }

    public static MapContext buildJGrassDataContext(){
        
        MapContext context = null;
        MapLayer layer;

        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            File gridFile;
            
            CoverageReader cover = null;
            gridFile = new File("/home/sorel/GIS_DATA/orto/024120.jpg");
            try {
                cover = readWorldImage(gridFile);
            } catch (DataSourceException ex) {
                ex.printStackTrace();
            }catch (IOException ex){
                ex.printStackTrace();
            }
            
            layer = MapBuilder.createCoverageLayer(cover, createRasterStyle(),"024120");
            layer.setDescription(SF.description("raster1", ""));
            context.layers().add(layer);
            
//
//            gridFile = new File("/home/sorel/GIS_DATA/orto/024160.jpg");
//            try {
//                cover = readWorldImage(gridFile);
//            } catch (DataSourceException ex) {
//                ex.printStackTrace();
//            }catch (IOException ex){
//                ex.printStackTrace();
//            }
//
//            layer = MAP_BUILDER.createCoverageLayer(cover, createRasterStyle(),"024160");
//            layer.setDescription(SF.description("raster1", ""));
//            context.layers().add(layer);
//
//
//            gridFile = new File("/home/sorel/GIS_DATA/orto/025090.jpg");
//            try {
//                cover = readWorldImage(gridFile);
//            } catch (DataSourceException ex) {
//                ex.printStackTrace();
//            }catch (IOException ex){
//                ex.printStackTrace();
//            }
//
//            layer = MAP_BUILDER.createCoverageLayer(cover, createRasterStyle(),"025090");
//            layer.setDescription(SF.description("raster1", ""));
//            context.layers().add(layer);
//
//
//            gridFile = new File("/home/sorel/GIS_DATA/orto/025100.jpg");
//            try {
//                cover = readWorldImage(gridFile);
//            } catch (DataSourceException ex) {
//                ex.printStackTrace();
//            }catch (IOException ex){
//                ex.printStackTrace();
//            }
//
//            layer = MAP_BUILDER.createCoverageLayer(cover, createRasterStyle(),"025100");
//            layer.setDescription(SF.description("raster1", ""));
//            context.layers().add(layer);
//
//
//            gridFile = new File("/home/sorel/GIS_DATA/orto/025110.jpg");
//            try {
//                cover = readWorldImage(gridFile);
//            } catch (DataSourceException ex) {
//                ex.printStackTrace();
//            }catch (IOException ex){
//                ex.printStackTrace();
//            }
//
//            layer = MAP_BUILDER.createCoverageLayer(cover, createRasterStyle(),"025110");
//            layer.setDescription(SF.description("raster1", ""));
//            context.layers().add(layer);
//            
//            
//            gridFile = new File("/home/sorel/GIS_DATA/orto/025120.jpg");
//            try {
//                cover = readWorldImage(gridFile);
//            } catch (DataSourceException ex) {
//                ex.printStackTrace();
//            }catch (IOException ex){
//                ex.printStackTrace();
//            }
//            
//            layer = LAYER_BUILDER.create(cover, createRasterStyle(),"025120");
//            layer.setDescription(STYLE_FACTORY.description("raster1", ""));
//            context.layers().add(layer);
//            
//            
//            gridFile = new File("/home/sorel/GIS_DATA/orto/025130.jpg");
//            try {
//                cover = readWorldImage(gridFile);
//            } catch (DataSourceException ex) {
//                ex.printStackTrace();
//            }catch (IOException ex){
//                ex.printStackTrace();
//            }
//            
//            layer = LAYER_BUILDER.create(cover, createRasterStyle(),"025130");
//            layer.setDescription(STYLE_FACTORY.description("raster1", ""));
//            context.layers().add(layer);
            
            
//            gridFile = new File("/home/sorel/GIS_DATA/orto/025140.jpg");
//            try {
//                cover = readWorldImage(gridFile);
//            } catch (DataSourceException ex) {
//                ex.printStackTrace();
//            }catch (IOException ex){
//                ex.printStackTrace();
//            }
//            
//            layer = LAYER_BUILDER.create(cover, createRasterStyle(),"025140");
//            layer.setDescription(STYLE_FACTORY.description("raster1", ""));
//            context.layers().add(layer);
            
            
//            gridFile = new File("/home/sorel/GIS_DATA/orto/025150.jpg");
//            try {
//                cover = readWorldImage(gridFile);
//            } catch (DataSourceException ex) {
//                ex.printStackTrace();
//            }catch (IOException ex){
//                ex.printStackTrace();
//            }
//            
//            layer = LAYER_BUILDER.create(cover, createRasterStyle(),"025150");
//            layer.setDescription(STYLE_FACTORY.description("raster1", ""));
//            context.layers().add(layer);

            context.setCoordinateReferenceSystem(layer.getBounds().getCoordinateReferenceSystem());
            context.setAreaOfInterest(context.getBounds());
            context.setDescription(SF.description("Democontext", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;
        
    }

    public static MapContext buildMassiveVectorsContext() {

        MapContext context = null;
        MapLayer layer = null;

        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            Map<String,Object> params;
            File shape;
            DataStore store;
            FeatureSource<SimpleFeatureType, SimpleFeature> fs;
            Style style;

            File DefaultPrjFile = new File("/home/sorel/GIS_DATA/GIS/DCW_South-America_Africa_shp/AA.prj");
            File folder = new File("/home/sorel/GIS_DATA/GIS/DCW_South-America_Africa_shp");

            File[] list = folder.listFiles();
            if (list != null){
                for (File file : list) {
                    if(file.getName().toLowerCase().endsWith("shp")){

                        String prjName = file.getAbsolutePath().substring(0, file.getAbsolutePath().length()-3) + "prj";

                        File prjFile = new File(prjName);
                        if(!prjFile.exists()){
                            copier(DefaultPrjFile, prjFile);
                        }

                        layer = createVectorLayer(file);
                        context.layers().add(layer);
                    }
                }
            }

            context.setCoordinateReferenceSystem(layer.getBounds().getCoordinateReferenceSystem());
            context.setAreaOfInterest(context.getBounds());
            context.setDescription(SF.description("Democontext", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;

    }

    public static MapContext buildWindArrowContext(){

        MapContext context = null;
        MapLayer layer = null;

        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);

            GridCoverage2D coverage = CoverageBuilder.createCoverage();
            layer = MapBuilder.createCoverageLayer(coverage, createRasterStyle(),"layer");
            layer.graphicBuilders().add(new GridMarkGraphicBuilder());
            context.layers().add(layer);

            context.setCoordinateReferenceSystem(layer.getBounds().getCoordinateReferenceSystem());
            context.setAreaOfInterest(context.getBounds());
//            context.Title("DemoContext");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;
    }

    public static MapContext buildRealCityContext() {

        MapContext context = null;
        MapLayer layer;

        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            Map<String,Object> params;
            File shape;
            DataStore store;
            FeatureSource<SimpleFeatureType, SimpleFeature> fs;
            MutableStyle style;
            File gridFile;
            GridCoverage2D cover = null;

//            gridFile = new File("/home/sorel/GIS_DATA/JEU_VILLE/ortho/1998-0897-1797-83.jpg");
//            CoverageReader reader = readWorldImage(gridFile);
////            try {
////                GeoTiffReader reader = new GeoTiffReader(gridFile, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
////                cover = (GridCoverage2D) reader.read(null);
//////                WorldImageReader reader = new WorldImageReader(gridFile);
//////                cover = (GridCoverage2D) reader.read(null);
////            } catch (DataSourceException ex) {
////                cover = null;
////                ex.printStackTrace();
////            }catch (IOException ex){
////                cover = null;
////                ex.printStackTrace();
////            }
//            layer = MapBuilder.createCoverageLayer(reader, SF.style(SF.rasterSymbolizer()),"1998-0897-1798-83");
////            layer = MAP_BUILDER.createCoverageLayer(cover, createRasterStyle(),"1998-0897-1797-83");
//            layer.setDescription(SF.description("raster1", ""));
//            layer.setName("raster1");
//            layer.setVisible(false);
//            context.layers().add(layer);
//
//            gridFile = new File("/home/sorel/GIS_DATA/JEU_VILLE/ortho/1998-0897-1798-83.TIF");
//            try {
//                GeoTiffReader reader = new GeoTiffReader(gridFile, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
//                cover = (GridCoverage2D) reader.read(null);
//            } catch (DataSourceException ex) {
//                cover = null;
//                ex.printStackTrace();
//            }catch (IOException ex){
//                cover = null;
//                ex.printStackTrace();
//            }
//            layer = MapBuilder.createCoverageLayer(cover, SF.style(SF.rasterSymbolizer()),"1998-0897-1798-83");
//            layer.setDescription(SF.description("raster2", ""));
//            layer.setName("raster2");
//            layer.setVisible(false);
//            layer.setStyle(createGridMarkStyle());
//            context.layers().add(layer);
//
//            gridFile = new File("/home/sorel/GIS_DATA/JEU_VILLE/ortho/1998-0897-1799-83.TIF");
//            try {
//                GeoTiffReader reader = new GeoTiffReader(gridFile, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
//                cover = (GridCoverage2D) reader.read(null);
//            } catch (DataSourceException ex) {
//                cover = null;
//                ex.printStackTrace();
//            }catch (IOException ex){
//                cover = null;
//                ex.printStackTrace();
//            }
//            layer = MapBuilder.createCoverageLayer(cover, SF.style(SF.rasterSymbolizer()),"1998-0897-1799-83");
//            layer.setDescription(SF.description("raster3", ""));
//            layer.setName("raster3");
//            layer.setVisible(false);
//            context.layers().add(layer);
//
//            gridFile = new File("/home/sorel/GIS_DATA/JEU_VILLE/ortho/1998-0897-1800-83.TIF");
//            try {
//                GeoTiffReader reader = new GeoTiffReader(gridFile, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
//                cover = (GridCoverage2D) reader.read(null);
//            } catch (DataSourceException ex) {
//                cover = null;
//                ex.printStackTrace();
//            }catch (IOException ex){
//                cover = null;
//                ex.printStackTrace();
//            }
//            layer = MapBuilder.createCoverageLayer(cover, SF.style(SF.rasterSymbolizer()),"1998-0897-1800-83");
//            layer.setDescription(SF.description("raster4", ""));
//            layer.setName("raster4");
//            layer.setVisible(false);
//            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/JEU_VILLE/ADMIN_COMMUNE.SHP");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = createNewLineStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("communes", ""));
            layer.setName("communes");
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
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/JEU_VILLE/RESFER_TRONCON_VOIE_FERREE.SHP");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = createRealTrainStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("chemin de fer", ""));
            layer.setName("chemin de fer");
            layer.setVisible(true);
            context.layers().add(layer);


            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/JEU_VILLE/RESROU_TRONCON_ROUTE.SHP");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = createRealRoadStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("routes", ""));
            layer.setName("routes");
            layer.setVisible(true);
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
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/JEU_VILLE/MER.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = createRealWaterStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("mer", ""));
            layer.setName("mer");
            layer.setVisible(true);
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/JEU_VILLE/EQ_LIGNE_ELEC.SHP");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = createElecLineStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(SF.description("ligne electrique", ""));
            layer.setName("ligne electrique");
            layer.setVisible(true);
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
            context.layers().add(layer);

//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/JEU_VILLE/TOPON_TOPONYME.SHP");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = createRealPointStyle();
//            layer = LAYER_BUILDER.create(fs, style);
//            layer.setDescription(STYLE_FACTORY.description("points", ""));
//            context.layers().add(layer);


            context.setCoordinateReferenceSystem(layer.getBounds().getCoordinateReferenceSystem());
            context.setAreaOfInterest(context.getBounds());
            context.setDescription(SF.description("Democontext", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;

    }

    public static MapContext buildWMSContext() {
        final MapContext context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
        MapLayer layer;

        try {
//            WebMapServer wms = new WebMapServer(new URL("http://demo.geomatys.com/constellation/WS/wms?"),WebMapServer.Version.v130);
            WebMapServer wms = new WebMapServer(new URL("http://www2.demis.nl/WMS/wms.asp?wms=WorldMap"),"1.1.1");
            WMSMapLayer wmsLayer = new WMSMapLayer(wms,"Bathymetry,Countries");
//            WMSMapLayer wmsLayer = new WMSMapLayer(wms,"BlueMarble");
//            wmsLayer.setName("Builtup+areas,Bathymetry,Countries,Topography,Coastlines,Waterbodies,Inundated,Rivers,Streams,Railroads,Highways,Roads,Trails,Borders,Cities,Settlements,Spot+elevations,Airports,Ocean+features");
//            wmsLayer.setDescription(SF.description("wms layer", ""));
//            wmsLayer.setOutputFormat("image/png");
//            wmsLayer.setVersion("1.3.0");

            context.layers().add(wmsLayer);
//            context.layers().add(createVectorLayer("/home/eclesia/GIS_DATA/normandy/bn_Lim_departements.shp"));

//            Map<String,Object> params = new HashMap<String,Object>();
//            File shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/COASTL.shp");
//            params.put( "url", shape.toURI().toURL() );
//
//            DataStore store = DataStoreFinder.getDataStore(params);
//            FeatureSource<SimpleFeatureType, SimpleFeature> fs = store.getFeatureSource(store.getTypeNames()[0]);
//            MutableStyle style = createNewLineStyle();
//            layer = MAP_BUILDER.createFeatureLayer(fs, style);
//            layer.setDescription(SF.description("points", ""));
//            context.layers().add(layer);

            context.setDescription(SF.description("Democontext", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;
    }

    public static MapContext buildMixedContext() {
        MapContext context = null;

        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            context.setDescription(SF.description("Democontext", ""));

            String format = "image/png";
            String version = "1.1.1";

//            WMSMapLayer wmsLayer0 = new WMSMapLayer(new WebMapServer(new URL(" http://demo.geomatys.fr/seagis/WS/wms"),"1.1.1"));
//            wmsLayer0.setName("BlueMarble");
//            wmsLayer0.setVersion(version);
//            wmsLayer0.setOutputFormat(format);
//            wmsLayer0.setDescription(SF.description("Geomatys BlueMarble", ""));
//
//            WMSMapLayer wmsLayer1 = new WMSMapLayer(new WebMapServer(new URL("http://www2.demis.nl/WMS/wms.asp?wms=WorldMap"),"1.1.1"));
//            wmsLayer1.setName("Bathymetry,Countries,Topography,Builtup areas,Coastlines,Waterbodies,Inundated,Rivers,Streams,Railroads,Highways,Roads,Trails,Borders,Cities,Settlements,Spot+elevations,Airports,Ocean+features");
//            wmsLayer1.setVersion(version);
//            wmsLayer1.setOutputFormat(format);
//            wmsLayer1.setDescription(SF.description("Demis General", ""));
//
//            WMSMapLayer wmsLayer2 = new WMSMapLayer(new WebMapServer(new URL("http://demo.geomatys.fr/geoserver/wms"),"1.1.1"));
//            wmsLayer2.setName("topp:states");
//            wmsLayer2.setVersion(version);
//            wmsLayer2.setOutputFormat(format);
//            wmsLayer2.setDescription(SF.description("Geomatys States", ""));
//
//
//            Map<String,Object> params;
//            File shape;
//            DataStore store;
//            FeatureSource<SimpleFeatureType, SimpleFeature> fs;
//            MutableStyle style;
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/ROADL.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            MapLayer local0 = MAP_BUILDER.createFeatureLayer(fs, style);
//            local0.setDescription(SF.description("roads", ""));
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/GRASSA.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            MapLayer local1 = MAP_BUILDER.createFeatureLayer(fs, style);
//            local1.setDescription(SF.description("grass", ""));
//
//            params = new HashMap<String,Object>();
//            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/CONTOURL.shp");
//            params.put( "url", shape.toURI().toURL() );
//            store = DataStoreFinder.getDataStore(params);
//            fs = store.getFeatureSource(store.getTypeNames()[0]);
//            style = RANDOM_FACTORY.createRandomVectorStyle(fs);
//            MapLayer local2 = MAP_BUILDER.createFeatureLayer(fs, style);
//            local2.setDescription(SF.description("contour", ""));
//
//            wmsLayer1.setStyles("");
//            wmsLayer2.setStyles("");
//            context.layers().add(wmsLayer0);
//            context.layers().add(wmsLayer1);
//            context.layers().add(wmsLayer2);
//            context.layers().add(local0);
//            context.layers().add(local1);
//            context.layers().add(local2);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;
    }

    public static MapContext buildSeveralWMSContext() {
        MapContext context = null;

        try {
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            context.setDescription(SF.description("Democontext", ""));

////            String format = "image/png";
////            String version = "1.1.1";
////
////            WMSMapLayer wmsLayer0 = new WMSMapLayer(new WebMapServer(new URL(" http://demo.geomatys.fr/seagis/WS/wms"),"1.1.1"));
////            wmsLayer0.setName("BlueMarble");
////            wmsLayer0.setVersion(version);
////            wmsLayer0.setOutputFormat(format);
////            wmsLayer0.setDescription(SF.description("Geomatys BlueMarble", ""));
////
////            WMSMapLayer wmsLayer1 = new WMSMapLayer(new WebMapServer(new URL("http://www2.demis.nl/WMS/wms.asp?wms=WorldMap"),"1.1.1"));
////            wmsLayer1.setName("Bathymetry,Countries,Topography,Builtup areas,Coastlines,Waterbodies,Inundated,Rivers,Streams,Railroads,Highways,Roads,Trails,Borders,Cities,Settlements,Spot+elevations,Airports,Ocean+features");
////            wmsLayer1.setVersion(version);
////            wmsLayer1.setOutputFormat(format);
////            wmsLayer1.setDescription(SF.description("Demis General", ""));
////
////            WMSMapLayer wmsLayer2 = new WMSMapLayer(new WebMapServer(new URL("http://demo.geomatys.fr/geoserver/wms"),"1.1.1"));
////            wmsLayer2.setName("topp:states");
////            wmsLayer2.setVersion(version);
////            wmsLayer2.setOutputFormat(format);
////            wmsLayer2.setDescription(SF.description("Geomatys States", ""));
////
//////            WMSMapLayer wmsLayer3 = new WMSMapLayer(new WebMapServer(new URL("http://sigma.openplans.org:8080/geoserver/wms"),"1.1.1"));
//////            wmsLayer3.setName("topp:major_roads");
//////            wmsLayer3.setVersion(version);
//////            wmsLayer3.setOutputFormat(format);
//////            wmsLayer3.setTitle("Sigma roads");
////
////            WMSMapLayer wmsLayer4 = new WMSMapLayer(new WebMapServer(new URL("http://labs.metacarta.com/wms/vmap0"),"1.1.1"));
////            wmsLayer4.setName("basic");
////            wmsLayer4.setVersion(version);
////            wmsLayer4.setOutputFormat(format);
////            wmsLayer4.setDescription(SF.description("MetaCarta basic", ""));
////
////            WMSMapLayer wmsLayer5 = new WMSMapLayer(new WebMapServer(new URL("http://atlas.nrcan.gc.ca/cgi-bin/toporamawms_en"),"1.1.1"));
////            wmsLayer5.setName("wms_atlasofcanada_eng");
////            wmsLayer5.setVersion(version);
////            wmsLayer5.setOutputFormat(format);
////            wmsLayer5.setDescription(SF.description("NRCan atlas", ""));
////
////            wmsLayer1.setStyles("");
////            wmsLayer2.setStyles("");
//////            wmsLayer3.setStyles("");
////            wmsLayer4.setStyles("");
////            wmsLayer5.setStyles("");
////            context.layers().add(wmsLayer0);
////            context.layers().add(wmsLayer1);
////            context.layers().add(wmsLayer2);
//            context.layers().add(wmsLayer3);
//            context.layers().add(wmsLayer4);
//            context.layers().add(wmsLayer5);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;
    }

    public static MapContext buildNeptuneContext(){
        MapContext context = null;
        
        
        try {
            final Map params = new HashMap<String, Object>();
            params.put("dbtype", "postgis");
            params.put(PostgisDataStoreFactory.HOST.key, "localhost");
            params.put(PostgisDataStoreFactory.PORT.key, 5432);
            params.put(PostgisDataStoreFactory.SCHEMA.key, "public");
            params.put(PostgisDataStoreFactory.DATABASE.key, "neptune");
            params.put(PostgisDataStoreFactory.USER.key, "admin");
            params.put(PostgisDataStoreFactory.PASSWD.key, "adminadmin");

            DataStore store = DataStoreFinder.getDataStore(params);

            FeatureSource fs = store.getFeatureSource("Troncon");
//            fs.getBounds();
//            FeatureCollection coll = fs.getFeatures();
//            org.geotools.feature.FeatureIterator ite = coll.features();
//            try{
//            while(ite.hasNext()){
//                SimpleFeature f = (SimpleFeature) ite.next();
//
//    //            f.getProperty("NOTEXISTINGPROPERTY"); //THIS LINE RAISE AN ERROR
//                f.getDefaultGeometryProperty().getValue();
//            }
//            }catch(Exception ex){
//                ex.printStackTrace();
//            }finally{
//                ite.close();
//            }
            
            
            
            context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
            context.layers().add(createVectorLayer("/home/sorel/GIS_DATA/other/countries.shp"));
            context.layers().add(MapBuilder.createFeatureLayer(fs, SF.style(SF.lineSymbolizer())));
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

//        XMLUtilities xml = new XMLUtilities();
//        try {
//            xml.writeSLD(new File("test.xml"), sld, StyledLayerDescriptor.V_1_0_0);
//        } catch (JAXBException ex) {
//            Logger.getLogger(ContextBuilder.class.getName()).log(Level.SEVERE, null, ex);
//        }

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

    private static MutableStyle createGridMarkStyle() {
//        Symbolizer symbol1 = SF.createRasterSymbolizer();
        Symbolizer symbol2 = new VectorFieldSymbolizer();
        return SF.style(new Symbolizer[]{symbol2});
    }
    
}

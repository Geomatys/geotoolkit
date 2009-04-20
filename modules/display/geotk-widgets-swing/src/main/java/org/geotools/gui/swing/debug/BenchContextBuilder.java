
package org.geotools.gui.swing.debug;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.style.Fill;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.Stroke;

public class BenchContextBuilder {

    public static final MutableStyleFactory STYLE_FACTORY = new DefaultStyleFactory();

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
            style = createLineStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(STYLE_FACTORY.description("roads", ""));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/INWATERA.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = createPolygonStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(STYLE_FACTORY.description("inwater", ""));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/TREESA.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = createPolygonStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(STYLE_FACTORY.description("trees", ""));
            context.layers().add(layer);

            params = new HashMap<String,Object>();
            shape = new File("/home/sorel/GIS_DATA/GIS/DCW_Europe_North-Asia_shp/CONTOURL.shp");
            params.put( "url", shape.toURI().toURL() );
            store = DataStoreFinder.getDataStore(params);
            fs = store.getFeatureSource(store.getTypeNames()[0]);
            style = createLineStyle();
            layer = MapBuilder.createFeatureLayer(fs, style);
            layer.setDescription(STYLE_FACTORY.description("contour", ""));
            context.layers().add(layer);

            context.setCoordinateReferenceSystem(layer.getBounds().getCoordinateReferenceSystem());
            context.setDescription(STYLE_FACTORY.description("demo context", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;

    }

    public static MutableStyle createLineStyle(){
        MutableStyle style = null;

        Stroke stroke = STYLE_FACTORY.stroke(Color.RED, 1);
        LineSymbolizer symbol = STYLE_FACTORY.lineSymbolizer(stroke, "the_geom");
        style = STYLE_FACTORY.style(symbol);

        return style;
    }

    public static MutableStyle createPolygonStyle(){
        MutableStyle style = null;

        Stroke stroke = STYLE_FACTORY.stroke(Color.RED, 1);
        Fill fill = STYLE_FACTORY.fill(Color.BLUE);
        PolygonSymbolizer symbol = STYLE_FACTORY.polygonSymbolizer(stroke, fill, "the_geom");
        style = STYLE_FACTORY.style(symbol);

        return style;
    }


}

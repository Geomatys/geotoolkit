
package org.geotoolkit.pending.demo.symbology;

import com.vividsolutions.jts.geom.Coordinate;
import org.opengis.style.Fill;
import org.opengis.style.Halo;
import org.opengis.style.LabelPlacement;
import org.opengis.style.Font;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.ext.isoline.IsolineGraphicBuilder;
import org.geotoolkit.display2d.ext.isoline.ValueExtractor;
import org.geotoolkit.filter.function.math.MathFunctionFactory;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.MutableStyle;
import org.jdesktop.swingx.JXErrorPane;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.style.Description;
import org.opengis.style.TextSymbolizer;

import static org.geotoolkit.style.StyleConstants.*;

/**
 *
 * @author sorel
 */
public class JIsoline extends JAbstractMapPane{
  

    public JIsoline() throws DataStoreException{
        super(createContext());
    }

    private static MapContext createContext() throws DataStoreException {
        final MapContext context = Styles.createWorldContext(null);
        Map<String,Serializable> params;
        DataStore store;
        FeatureCollection fs;
        MutableStyle style;

        MapLayer layer;

        //stations -------------------------------------------------------------
        try{
            params = new HashMap<String,Serializable>();
            params.put( "url", JAbstractMapPane.class.getResource("/data/weather/stations2.shp") );
            store = DataStoreFinder.getDataStore(params);
            fs = store.createSession(true).getFeatureCollection(QueryBuilder.all(store.getNames().iterator().next()));
            layer = MapBuilder.createFeatureLayer(fs, createStationStyle());
            layer.setDescription(SF.description("stations", ""));
            layer.setName("stations");
            context.layers().add(layer);
        }catch(Exception ex){
            JXErrorPane.showDialog(ex);
            return context;
        }

        //bonus : temp isoligne-------------------------------------------------
        final MapLayer isoTemplayer = MapBuilder.createFeatureLayer(fs, SF.style());
        final IsolineGraphicBuilder gb = new IsolineGraphicBuilder(new ValueExtractor() {
            private PropertyName prop = FF.property("A_temp");

            @Override
            public Coordinate getValues(RenderingContext2D context, Feature feature) throws IOException {
                final Geometry geom = (Geometry) feature.getDefaultGeometryProperty().getValue();
                final Point centroid = geom.getCentroid();
                final Coordinate c = new Coordinate();
                c.x = centroid.getX();
                c.y = centroid.getY();
                c.z = prop.evaluate(feature, Double.class);
                return c;
            }
        });

        gb.setInterpolateCoverageColor(true);
        gb.setIsoLineStyle(SF.style(
                SF.lineSymbolizer(SF.stroke(Color.BLUE, 1),null),
                SF.textSymbolizer(
                    "Temperature",
                    (String)null,
                    SF.description("Temperature", "Temperature"),
                    NonSI.PIXEL,
                    FF.function(MathFunctionFactory.ROUND, FF.property("value")),
                    SF.font(9),
                    SF.linePlacement(FF.literal(3),FF.literal(100),FF.literal(600),true,true,true),
                    SF.halo(Color.WHITE, 0),
                    SF.fill(Color.BLUE))
                    ));
        isoTemplayer.graphicBuilders().add(gb);
        context.layers().add(1,isoTemplayer);
        //----------------------------------------------------------------------

        return context;
    }

    private static MutableStyle createStationStyle(){
        
        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = NonSI.PIXEL;
        final Expression label = FF.property("A_temp");
        final Font font = SF.font(
                FF.literal("Arial"),
                FONT_STYLE_ITALIC,
                FONT_WEIGHT_BOLD,
                FF.literal(14));
        final LabelPlacement placement = SF.pointPlacement();
        final Halo halo = SF.halo(Color.WHITE, 1);
        final Fill fill = SF.fill(Color.BLUE);

        final TextSymbolizer symbol = SF.textSymbolizer(name, geometry, desc, unit, label, font, placement, halo, fill);
        final MutableStyle style = SF.style(DEFAULT_POINT_SYMBOLIZER,symbol);
        
        return style;
    }

    @Override
    protected JComponent createConfigPane() {
        return new JPanel();
    }

}

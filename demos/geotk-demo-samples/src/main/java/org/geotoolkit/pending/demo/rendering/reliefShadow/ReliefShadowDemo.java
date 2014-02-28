
package org.geotoolkit.pending.demo.rendering.reliefShadow;

import java.io.File;
import javax.imageio.ImageReader;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.filestore.FileCoverageStore;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.ElevationModel;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import static org.geotoolkit.style.StyleConstants.DEFAULT_DESCRIPTION;
import static org.geotoolkit.style.StyleConstants.LITERAL_ONE_FLOAT;
import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.Description;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.ShadedRelief;
import org.opengis.style.Symbolizer;

/**
 * Show how to use {@link ElevationModel} to add shadow on image in renderer. 
 *
 * @author Remi Marechal (Geomatys).
 */
public class ReliefShadowDemo {
    public static final MutableStyleFactory SF = new DefaultStyleFactory();
    protected static final FilterFactory FF    = FactoryFinder.getFilterFactory(null);
    
    /**
     * Relief path of Digital Elevation Model (DEM).
     */
    final static File reliefPath = new File("data/cloudsRelief.tiff");
    /**
     * Create {@link GridCoverageReader} which will be return by {@link ElevationModel} to read DEM.
     */
    
    public static void main(String[] args) throws Exception {
        Demos.init();
        ImageReader covPath = XImageIO.getReaderByFormatName("tiff-wf", reliefPath, Boolean.FALSE, false);
            covPath.setInput(reliefPath);
//        final GridCoverageReader  demGCR = CoverageIO.createSimpleReader(covPath);
        
        /*
         * Coverage which will be shadowed.
         */
        final File input = new File("data/clouds.jpg");
        final FileCoverageStore store = new FileCoverageStore(input.toURL(), "JPEG");
        final Name name = store.getNames().iterator().next();
        final CoverageReference ref = store.getCoverageReference(name);
//        final GridCoverageReader reader = CoverageIO.createSimpleReader(input);
//        final GridCoverage2D grid = (GridCoverage2D) reader.read(0, null);
        //create a mapcontext
        final MapContext context  = MapBuilder.createContext();        
        final CoverageMapLayer cl = MapBuilder.createCoverageLayer(ref, SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER));
        final double azimuth = 130;
        final double altitude = 2;
        final double scale = 55;
        final ElevationModel elevModel = new ElevationModel(ref, azimuth, altitude, scale, AxisDirection.UP);
        
        /*
         * Define Elevation Model object to get informations necessary to compute shadow on coverage. 
         */
        cl.setElevationModel(elevModel);
        
        MutableStyle style = customRaster();
        cl.setStyle(style);
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setEnvelope(-180, -90, 180, 90);
        context.setAreaOfInterest(env);
        context.layers().add(cl);
        JMap2DFrame.show(context);
    }
    
    /*
     * Define style.
     */
    public static MutableStyle customRaster() {

        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = NonSI.PIXEL;
        final Expression opacity = LITERAL_ONE_FLOAT;
        final ChannelSelection channels = null;
        final OverlapBehavior overlap = null;
        final ColorMap colormap = null;
        final ContrastEnhancement enhance = null;
        /*
         * Define if we want shadow.
         * First argument define in percent the dimming of shadow pixel value.
         * Second argument define if we want increase sunny pixel value.
         */
        final ShadedRelief relief = SF.shadedRelief(FF.literal(60),false);
        final Symbolizer outline = null;

        final RasterSymbolizer symbol = SF.rasterSymbolizer(
                name,(String)null,desc,unit,opacity,
                channels,overlap,colormap,enhance,relief,outline);
        final MutableStyle style = SF.style(symbol);
        return style;
    }    
}

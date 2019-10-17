
package org.geotoolkit.pending.demo.rendering.reliefShadow;

import java.io.File;
import java.net.URL;
import javax.imageio.ImageReader;
import javax.measure.Unit;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.coverage.worldfile.FileCoverageStore;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.gui.javafx.render2d.FXMapFrame;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.map.ElevationModel;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import static org.geotoolkit.style.StyleConstants.DEFAULT_DESCRIPTION;
import static org.geotoolkit.style.StyleConstants.LITERAL_ONE_FLOAT;
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
    protected static final FilterFactory FF    = DefaultFactories.forBuildin(FilterFactory.class);

    /**
     * Create {@link GridCoverageReader} which will be return by {@link ElevationModel} to read DEM.
     */

    public static void main(String[] args) throws Exception {
        Demos.init();

        //Relief path of Digital Elevation Model (DEM).
        URL reliefURL = ReliefShadowDemo.class.getResource("/data/coverage/cloudsRelief.tiff");
        File reliefPath = new File(reliefURL.toURI());

        ImageReader covPath = XImageIO.getReaderByFormatName("tiff-wf", reliefPath, Boolean.FALSE, false);
        covPath.setInput(reliefPath);

        FileCoverageStore store = new FileCoverageStore(reliefPath.toURL(), "AUTO");
        final GridCoverageResource ref = DataStores.flatten(store, true, GridCoverageResource.class).iterator().next();

//        final GridCoverageReader  demGCR = CoverageIO.createSimpleReader(covPath);

        /*
         * Coverage which will be shadowed.
         */
//        final File input = new File(ReliefShadowDemo.class.findResource("/data/coverage/clouds.jpg").toURI());
//        final FileCoverageStore store = new FileCoverageStore(input.toURL(), "JPEG");
//        final Name name = store.getNames().iterator().next();
//        final CoverageReference ref = store.findResource(name);
//        final GridCoverageReader reader = CoverageIO.createSimpleReader(input);
//        final GridCoverage2D grid = (GridCoverage2D) reader.read(0, null);

        File cloudFile = new File(ReliefShadowDemo.class.getResource("/data/coverage/clouds.jpg").toURI());
        //create a mapcontext
        final MapContext context  = MapBuilder.createContext();
        final MapLayer cl = MapBuilder.createCoverageLayer(cloudFile);
        final double azimuth = 45;
        final double altitude = 2;
        final double scale = 0.4;
        final ElevationModel elevModel = new ElevationModel(ref, azimuth, altitude, scale, AxisDirection.UP);

        /*
         * Define Elevation Model object to get informations necessary to compute shadow on coverage.
         */
        cl.setElevationModel(elevModel);

        MutableStyle style = customRaster();
        cl.setStyle(style);
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setEnvelope(-180, -90, 180, 90);
        context.setAreaOfInterest(env);
        context.layers().add(cl);
        FXMapFrame.show(context);
    }

    /*
     * Define style.
     */
    public static MutableStyle customRaster() {

        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;
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
        final ShadedRelief relief = SF.shadedRelief(FF.literal(10),true);
        final Symbolizer outline = null;

        final RasterSymbolizer symbol = SF.rasterSymbolizer(
                name,(String)null,desc,unit,opacity,
                channels,overlap,colormap,enhance,relief,outline);
        final MutableStyle style = SF.style(symbol);
        return style;
    }
}


package org.geotoolkit.pending.demo.mapmodel;

import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.map.ProcessedCollection;
import org.geotoolkit.map.ProcessedCoverageReference;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.pending.demo.Demos;
import static org.geotoolkit.pending.demo.mapmodel.MapContextDemo.openWorldFile;
import org.geotoolkit.process.coverage.resample.ResampleDescriptor;
import org.geotoolkit.process.vector.buffer.BufferDescriptor;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Demonstrate a MapLayer which source is the result of a process chain.
 *
 */
public class ProcessedLayerDemo {

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                                                   new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

    public static void main(String[] args) throws Exception {
        Demos.init();

        final MapContext context = MapBuilder.createContext();
        final MapLayer processedColLayer = createProcessedCollectionLayer();
        final MapLayer processedCovLayer = createProcessedCoverageLayer();
        context.layers().add(processedCovLayer);
        context.layers().add(processedColLayer);

        JMap2DFrame.show(context);
    }

    private static MapLayer createProcessedCollectionLayer() throws Exception{
        final FeatureCollection basedata = MapContextDemo.openShapeFile();

        final ParameterValueGroup parameters = BufferDescriptor.INSTANCE.getInputDescriptor().createValue();
        Parameters.getOrCreate(BufferDescriptor.FEATURE_IN, parameters).setValue(basedata);
        Parameters.getOrCreate(BufferDescriptor.LENIENT_TRANSFORM_IN, parameters).setValue(true);
        Parameters.getOrCreate(BufferDescriptor.DISTANCE_IN, parameters).setValue(50000);

        final ProcessedCollection processed = new ProcessedCollection();
        processed.setProcessDescriptor(BufferDescriptor.INSTANCE);
        processed.setInputParameters(parameters);
        processed.setLifespan(-1);
        processed.setResultParameter(BufferDescriptor.FEATURE_OUT.getName().getCode());

        final MutableStyle style = SF.style(StyleConstants.DEFAULT_LINE_SYMBOLIZER);
        final MapLayer layer = MapBuilder.createCollectionLayer(processed, style);
        return layer;

    }

    private static MapLayer createProcessedCoverageLayer() throws Exception{
        final CoordinateReferenceSystem crs = CRS.decode("EPSG:2154");
        GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -30, +30);
        env.setRange(1, 20, 80);


        final GridCoverageReader baseData = openWorldFile();

        final ParameterValueGroup parameters = ResampleDescriptor.INSTANCE.getInputDescriptor().createValue();
        Parameters.getOrCreate(ResampleDescriptor.IN_COVERAGE, parameters).setValue(baseData.read(0, null));
        Parameters.getOrCreate(ResampleDescriptor.IN_COORDINATE_REFERENCE_SYSTEM, parameters).setValue(env.getCoordinateReferenceSystem());

        final ProcessedCoverageReference processed = new ProcessedCoverageReference();
        processed.setProcessDescriptor(ResampleDescriptor.INSTANCE);
        processed.setInputParameters(parameters);
        processed.setLifespan(-1);
        processed.setResultParameter(ResampleDescriptor.OUT_COVERAGE.getName().getCode());


        final MutableStyle style = SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER);
        final MapLayer layer = MapBuilder.createCoverageLayer(processed, style);
        return layer;
    }


}


package org.geotoolkit.data.om.netcdf;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.internal.feature.AttributeConvention;
import org.geotoolkit.storage.feature.FeatureReader;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.data.om.OMFeatureTypes;
import org.geotoolkit.feature.ReprojectMapper;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gml.AxisResolve;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.observation.model.ObservationDataset;
import org.geotoolkit.observation.model.SamplingFeature;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.observation.AnyFeature;
import org.opengis.observation.Observation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 */
class NetcdfFeatureReader implements FeatureReader {

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.data.om");

    private boolean firstCRS = true;
    protected FeatureType type;
    protected List<Feature> features = new ArrayList<>();
    protected int cpt = 0;

    NetcdfFeatureReader(final Path source, final FeatureType type) throws NetCDFParsingException {
        this.type = type;
        ObservationDataset result = NetCDFExtractor.getObservationFromNetCDF(source, "temp");
        for (Observation obs : result.observations) {
            final Feature feat = getFeatureFromFOI(obs.getFeatureOfInterest());
            if (feat != null) {
                features.add(feat);
            }
        }
    }

    @Override
    public FeatureType getFeatureType() {
        return type;
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        final Feature result = features.get(cpt);
        cpt++;
        return result;
    }

    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        return cpt < features.size();
    }

    protected final Feature getFeatureFromFOI(final AnyFeature afoi) {
        if (afoi instanceof SamplingFeature feature) {

            try {
                final Geometry geom = feature.getGeometry();

                if (firstCRS && geom != null) {
                    //configure crs in the feature type
                    final CoordinateReferenceSystem crs = JTS.findCoordinateReferenceSystem(geom);
                    type = new ReprojectMapper(type, crs).getMappedType();
                    firstCRS = false;
                }
                final Feature f = type.newInstance();
                f.setPropertyValue(AttributeConvention.IDENTIFIER, feature.getId());
                f.setPropertyValue(OMFeatureTypes.ATT_DESC.toString(), feature.getDescription());
                f.setPropertyValue(OMFeatureTypes.ATT_NAME.toString(), feature.getName());
                f.setPropertyValue(OMFeatureTypes.ATT_POSITION.toString(), geom);

                final List<String> sampleds = new ArrayList<>();
                if (feature.getSampledFeatureId() != null) {
                    sampleds.add(feature.getSampledFeatureId());
                }
                f.setPropertyValue(OMFeatureTypes.ATT_SAMPLED.toString(), sampleds);
                return f;
            } catch (FactoryException ex) {
                LOGGER.log(Level.WARNING, "error while transforming GML geometry to JTS", ex);
            }
        }
        return null;
    }


    @Override
    public void close() {
        // do nothing
    }

    @Override
    public void remove() throws FeatureStoreRuntimeException {
        throw new FeatureStoreRuntimeException("Not supported.");
    }

}

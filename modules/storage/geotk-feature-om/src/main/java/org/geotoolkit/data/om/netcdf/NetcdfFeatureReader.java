
package org.geotoolkit.data.om.netcdf;

import org.geotoolkit.data.om.OMFeatureTypes;
import com.vividsolutions.jts.geom.Geometry;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.feature.FeatureTypeExt;
import org.apache.sis.feature.ReprojectFeatureType;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.sampling.xml.SamplingFeature;
import org.geotoolkit.sos.netcdf.ExtractionResult;
import org.geotoolkit.sos.netcdf.NetCDFExtractor;
import org.geotoolkit.sos.netcdf.NetCDFParsingException;
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

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.om");

    private boolean firstCRS = true;
    protected FeatureType type;
    protected List<Feature> features = new ArrayList<>();
    protected int cpt = 0;

    NetcdfFeatureReader(final Path source, final FeatureType type) throws NetCDFParsingException {
        this.type = type;
        ExtractionResult result = NetCDFExtractor.getObservationFromNetCDF(source, "temp");
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

    protected final Feature getFeatureFromFOI(final AnyFeature foi) {
        if (foi instanceof SamplingFeature) {
            final SamplingFeature feature = (SamplingFeature) foi;

            final org.opengis.geometry.Geometry isoGeom = feature.getGeometry();
            try {
                final Geometry geom;
                if (isoGeom instanceof AbstractGeometry) {
                    geom = GeometrytoJTS.toJTS((AbstractGeometry) isoGeom, false);
                } else {
                    geom = null;
                }
                if (firstCRS && isoGeom != null) {
                    //configure crs in the feature type
                    final CoordinateReferenceSystem crs = ((AbstractGeometry) isoGeom).getCoordinateReferenceSystem(false);
                    type = new ReprojectFeatureType(type, crs);
                    firstCRS = false;
                }
                final Feature f = type.newInstance();
                f.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), feature.getId());
                f.setPropertyValue(OMFeatureTypes.ATT_DESC.toString(), feature.getDescription());
                f.setPropertyValue(OMFeatureTypes.ATT_NAME.toString(), feature.getName());
                f.setPropertyValue(OMFeatureTypes.ATT_POSITION.toString(),geom);

                final List<String> sampleds = new ArrayList<>();
                for (FeatureProperty featProp : feature.getSampledFeatures()) {
                    if (featProp.getHref() != null) {
                        sampleds.add(featProp.getHref());
                    }
                }
                f.setPropertyValue(OMFeatureTypes.ATT_SAMPLED.toString(),sampleds);
                return f;
            } catch (FactoryException ex) {
                LOGGER.log(Level.WARNING, "error while transforming GML geometry to JTS", ex);
            }
        } else {
            LOGGER.warning("unable to find a valid feature of interest in the observation");
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

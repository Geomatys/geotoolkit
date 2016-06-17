
package org.geotoolkit.data.om.netcdf;

import org.geotoolkit.data.om.OMFeatureTypes;
import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureFactory;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.DefaultFeatureType;
import org.geotoolkit.feature.type.DefaultGeometryDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.sampling.xml.SamplingFeature;
import org.geotoolkit.sos.netcdf.ExtractionResult;
import org.geotoolkit.sos.netcdf.NetCDFExtractor;
import org.geotoolkit.sos.netcdf.NetCDFParsingException;
import org.opengis.observation.AnyFeature;
import org.opengis.observation.Observation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * 
 */
class NetcdfFeatureReader implements FeatureReader {

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.om");
    protected static final FeatureFactory FF = FeatureFactory.LENIENT;

    private boolean firstCRS = true;
    protected final FeatureType type;
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
            final Collection<Property> props = new ArrayList<>();
            final org.opengis.geometry.Geometry isoGeom = feature.getGeometry();
            try {
                final Geometry geom;
                if (isoGeom instanceof AbstractGeometry) {
                    geom = GeometrytoJTS.toJTS((AbstractGeometry) isoGeom, false);
                } else {
                    geom = null;
                }
                if (firstCRS && isoGeom != null) {
                    CoordinateReferenceSystem crs = ((AbstractGeometry) isoGeom).getCoordinateReferenceSystem(false);
                    if (type instanceof DefaultFeatureType) {
                        ((DefaultFeatureType) type).setCoordinateReferenceSystem(crs);
                    }
                    if (type.getGeometryDescriptor() instanceof DefaultGeometryDescriptor) {
                        ((DefaultGeometryDescriptor) type.getGeometryDescriptor()).setCoordinateReferenceSystem(crs);
                    }
                    firstCRS = false;
                }
                props.add(FF.createAttribute(feature.getDescription(), (AttributeDescriptor) type.getDescriptor(OMFeatureTypes.ATT_DESC), null));
                props.add(FF.createAttribute(feature.getName(), (AttributeDescriptor) type.getDescriptor(OMFeatureTypes.ATT_NAME), null));
                props.add(FF.createAttribute(geom, (AttributeDescriptor) type.getDescriptor(OMFeatureTypes.ATT_POSITION), null));
                boolean empty = true;
                for (FeatureProperty featProp : feature.getSampledFeatures()) {
                    if (featProp.getHref() != null) {
                        props.add(FF.createAttribute(featProp.getHref(), (AttributeDescriptor) type.getDescriptor(OMFeatureTypes.ATT_SAMPLED), null));
                        empty = false;
                    }
                }
                if (empty) {
                    props.add(FF.createAttribute(null, (AttributeDescriptor) type.getDescriptor(OMFeatureTypes.ATT_SAMPLED), null));
                }
                return FF.createFeature(props, type, feature.getId());
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

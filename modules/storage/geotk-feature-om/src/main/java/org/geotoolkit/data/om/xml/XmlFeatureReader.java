/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014-2016, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.om.xml;

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.geotoolkit.feature.FeatureTypeExt;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.FeatureProperty;
import static org.geotoolkit.data.om.OMFeatureTypes.ATT_DESC;
import static org.geotoolkit.data.om.OMFeatureTypes.ATT_NAME;
import static org.geotoolkit.data.om.OMFeatureTypes.ATT_POSITION;
import static org.geotoolkit.data.om.OMFeatureTypes.ATT_SAMPLED;
import org.geotoolkit.observation.xml.AbstractObservation;
import org.geotoolkit.sampling.xml.SamplingFeature;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.observation.AnyFeature;
import org.opengis.observation.Observation;
import org.opengis.observation.ObservationCollection;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 */
class XmlFeatureReader implements FeatureReader {

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.om");

    private boolean firstCRS = true;
    protected FeatureType type;
    protected List<Feature> features = new ArrayList<>();
    protected int cpt = 0;

    XmlFeatureReader(Path source, final FeatureType type) throws JAXBException, IOException {
        this.type = type;
        final Object fileObject = XmlObservationStore.unmarshallObservationFile(source);
        if (fileObject instanceof AbstractObservation) {
            final AbstractObservation obs = (AbstractObservation) fileObject;
            final Feature feat = getFeatureFromFOI(obs.getFeatureOfInterest());
            if (feat != null) {
                features.add(feat);
            }
        } else if (fileObject instanceof ObservationCollection) {
            final ObservationCollection coll = (ObservationCollection) fileObject;
            for (Observation obs : coll.getMember()) {
                final Feature feat = getFeatureFromFOI(obs.getFeatureOfInterest());
                if (feat != null) {
                    features.add(feat);
                }
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
            final Feature f = type.newInstance();
            f.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), feature.getId());

            final org.opengis.geometry.Geometry isoGeom = feature.getGeometry();
            try {
                final Geometry geom;
                if (isoGeom instanceof AbstractGeometry) {
                    geom = GeometrytoJTS.toJTS((AbstractGeometry) isoGeom);
                } else {
                    geom = null;
                }
                if (firstCRS && isoGeom != null) {
                    //configure crs in the feature type
                    final CoordinateReferenceSystem crs = ((AbstractGeometry) isoGeom).getCoordinateReferenceSystem(false);
                    type = FeatureTypeExt.createSubType(type, null, crs);
                    firstCRS = false;
                }
                f.setPropertyValue(ATT_DESC.toString(), feature.getDescription());
                if (feature.getName() != null) {
                    f.setPropertyValue(ATT_NAME.toString(), feature.getName().toString());
                }
                f.setPropertyValue(ATT_POSITION.toString(),geom);

                final List<String> sampleds = new ArrayList<>();
                for (FeatureProperty featProp : feature.getSampledFeatures()) {
                    if (featProp.getHref() != null) {
                        sampleds.add(featProp.getHref());
                    }
                }
                f.setPropertyValue(ATT_SAMPLED.toString(),sampleds);
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

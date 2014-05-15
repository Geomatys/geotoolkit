/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.data.om;

import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import static org.geotoolkit.data.om.OMFeatureTypes.*;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.type.DefaultGeometryDescriptor;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.observation.xml.AbstractObservation;
import org.geotoolkit.sampling.xml.SamplingFeature;
import org.geotoolkit.sos.xml.SOSMarshallerPool;
import org.geotoolkit.storage.DataFileStore;
import org.geotoolkit.util.FileUtilities;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.observation.AnyFeature;
import org.opengis.observation.Observation;
import org.opengis.observation.ObservationCollection;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 *  @author Guilhem Legal (Geomatys)
 */
public class OMXmlFeatureStore extends AbstractOMFeatureStore implements DataFileStore {

    private final File source;
    
    public OMXmlFeatureStore(final ParameterValueGroup params, final File source) {
        super(params, FileUtilities.getFileName(source));
        this.source = source;
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(OMXmlFeatureStoreFactory.NAME);
    }
    
    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        final FeatureType sft = getFeatureType(query.getTypeName());
        try {
            return handleRemaining(new OMReader(sft), query);
        } catch (JAXBException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void refreshMetaModel() {
        return;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public File[] getDataFiles() throws DataStoreException {
        return new File[]{source};
    }
    
    private static Object unmarshallObservationFile(final File f) throws JAXBException {
        final Unmarshaller um = SOSMarshallerPool.getInstance().acquireUnmarshaller();
        Object obj = um.unmarshal(f);
        if (obj instanceof JAXBElement) {
            obj = ((JAXBElement)obj).getValue();
        }
        if (obj != null) {
            return obj;
        }
        throw new JAXBException("the observation file does not contain a valid O&M object");
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // No supported stuffs /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void createFeatureType(final Name typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatureType(final Name typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteFeatureType(final Name typeName) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(final Name groupName, final Filter filter, final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter, Hints hints) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Feature Reader //////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private class OMReader implements FeatureReader {

        private boolean firstCRS = true;
        protected final FeatureType type;
        protected List<Feature> features = new ArrayList<>();
        protected int cpt = 0;

        private OMReader(final FeatureType type) throws JAXBException {
            this.type = type;
            final Object fileObject = unmarshallObservationFile(source);
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

                final Collection<Property> props = new ArrayList<>();
                final org.opengis.geometry.Geometry isoGeom = feature.getGeometry();
                try {
                    final Geometry geom;
                    if (isoGeom instanceof AbstractGeometry) {
                        geom = GeometrytoJTS.toJTS((AbstractGeometry)isoGeom);
                    } else {
                        geom = null;
                    }

                    if (firstCRS && isoGeom != null) {
                        CoordinateReferenceSystem crs = isoGeom.getCoordinateReferenceSystem();
                        if (type instanceof DefaultSimpleFeatureType) {
                            ((DefaultSimpleFeatureType) type).setCoordinateReferenceSystem(crs);
                        }
                        if (type.getGeometryDescriptor() instanceof DefaultGeometryDescriptor) {
                            ((DefaultGeometryDescriptor) type.getGeometryDescriptor()).setCoordinateReferenceSystem(crs);
                        }
                        firstCRS = false;
                    }

                    props.add(FF.createAttribute(feature.getDescription(), (AttributeDescriptor) type.getDescriptor(ATT_DESC), null));
                    props.add(FF.createAttribute(feature.getName(), (AttributeDescriptor) type.getDescriptor(ATT_NAME), null));
                    props.add(FF.createAttribute(geom, (AttributeDescriptor) type.getDescriptor(ATT_POSITION), null));

                    for (FeatureProperty featProp : feature.getSampledFeatures()) {
                        if (featProp.getHref() != null) {
                            props.add(FF.createAttribute(featProp.getHref(), (AttributeDescriptor) type.getDescriptor(ATT_SAMPLED), null));
                        }
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
        public void remove() throws FeatureStoreRuntimeException{
            throw new FeatureStoreRuntimeException("Not supported.");
        }

    }
}

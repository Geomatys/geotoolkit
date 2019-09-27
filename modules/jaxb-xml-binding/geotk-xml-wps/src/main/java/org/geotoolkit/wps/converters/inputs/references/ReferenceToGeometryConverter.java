/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.inputs.references;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.UnconvertibleObjectException;
import static org.geotoolkit.data.AbstractFileFeatureStoreFactory.PATH;
import org.geotoolkit.data.FeatureStoreUtilities;
import static org.geotoolkit.data.geojson.GeoJSONProvider.PARAMETERS_DESCRIPTOR;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v200.Reference;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

/**
 * Implementation of ObjectConverter to convert a reference into a Geometry.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class ReferenceToGeometryConverter extends AbstractReferenceInputConverter<Geometry> {

    private static ReferenceToGeometryConverter INSTANCE;

    private ReferenceToGeometryConverter() {
    }

    public static synchronized ReferenceToGeometryConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToGeometryConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Geometry> getTargetClass() {
        return Geometry.class;
    }

    /**
     * {@inheritDoc}
     *
     * @return Geometry.
     */
    @Override
    public Geometry convert(final Reference source, final Map<String, Object> params) throws UnconvertibleObjectException {

        final String mime = source.getMimeType() != null ? source.getMimeType() : WPSMimeType.TEXT_XML.val();
        final InputStream stream = getInputStreamFromReference(source);

        if (mime.equalsIgnoreCase(WPSMimeType.TEXT_XML.val()) || mime.equalsIgnoreCase(WPSMimeType.APP_GML.val())
                || mime.equalsIgnoreCase(WPSMimeType.TEXT_GML.val())) {

            Unmarshaller unmarsh = null;
            try {
                unmarsh = WPSMarshallerPool.getInstance().acquireUnmarshaller();
                Object value = unmarsh.unmarshal(stream);
                WPSMarshallerPool.getInstance().recycle(unmarsh);
                if (value != null && value instanceof JAXBElement) {
                    value = ((JAXBElement) value).getValue();
                }
                return GeometrytoJTS.toJTS((AbstractGeometryType) value);

            } catch (NoSuchAuthorityCodeException ex) {
                throw new UnconvertibleObjectException("ReferenceType geometry invalid input", ex);
            } catch (FactoryException ex) {
                throw new UnconvertibleObjectException("ReferenceType geometry invalid input", ex);
            } catch (JAXBException ex) {
                throw new UnconvertibleObjectException("ReferenceType geometry invalid input : Unmarshallable geometry", ex);
            }
        } else if (mime.equalsIgnoreCase(WPSMimeType.APP_GEOJSON.val())) {
            ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
            try {
                param.parameter(PATH.getName().getCode()).setValue(URI.create(source.getHref()));
                DataStore store = DataStores.open(param);

                List<FeatureSet> featureSets = new ArrayList<>(DataStores.flatten(store, true, FeatureSet.class));

                if (featureSets.size() != 1)
                    throw new UnconvertibleObjectException("Expected one Geometry. Found " + featureSets.size());

                final FeatureSet featureSet = featureSets.get(0);

                long collectionSize = FeatureStoreUtilities.getCount(featureSet);
                if (collectionSize != 1)
                    throw new UnconvertibleObjectException("Expected one geometry. Found " + collectionSize);

                try (Stream<Feature> st = featureSet.features(false)) {
                    Feature feature = st.findFirst().get();
                    return FeatureExt.getDefaultGeometryValue(feature)
                            .filter(Geometry.class::isInstance)
                            .map(Geometry.class::cast)
                            .orElseThrow(() -> new UnconvertibleObjectException("The found value may not be of Geometry type or may be null"));
                }
            } catch (DataStoreException ex) {
                throw new UnconvertibleObjectException(ex);
            }
        }
        else
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + source.getMimeType());
    }
}

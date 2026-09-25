/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.storage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.xml.namespace.QName;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.IllegalNameException;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.ogcwxs.request.feature.DescribeFeatureType;
import org.geotoolkit.ogcwxs.request.feature.GetFeature;
import org.geotoolkit.ows.xml.BoundingBox;
import org.geotoolkit.storage.feature.GenericNameIndex;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Feature Set for a WFS feature type. Read-only : {@code Transaction} is not covered by
 * {@link org.geotoolkit.ogcwxs.client.feature.WFSApi}.
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Bialota (Geomatys)
 */
public final class WFSResource extends AbstractFeatureSet {

    private final WebFeatureClient store;
    private final org.geotoolkit.wfs.xml.FeatureType ftt;

    private volatile FeatureType type;

    WFSResource(WebFeatureClient store, org.geotoolkit.wfs.xml.FeatureType ftt) {
        super(null);
        this.store = store;
        this.ftt = ftt;
    }

    @Override
    public synchronized FeatureType getType() throws DataStoreException {
        if (type != null) {
            return type;
        }

        final QName typeName = ftt.getName();
        final byte[] schema;
        try {
            final DescribeFeatureType params = new DescribeFeatureType().typeNames(List.of(typeName)).version(store.getVersion());
            schema = store.getApi().describeFeatureType(params).send().body();
        } catch (ServiceException | CapabilitiesException ex) {
            throw new DataStoreException(ex);
        }

        final FeatureType parsed;
        try {
            final JAXBFeatureTypeReader reader = new JAXBFeatureTypeReader();
            final GenericNameIndex<FeatureType> types = reader.read(new ByteArrayInputStream(schema));
            parsed = types.get(typeName.getLocalPart());
        } catch (jakarta.xml.bind.JAXBException | IllegalNameException ex) {
            throw new DataStoreException(ex);
        }

        final FeatureTypeBuilder builder = new FeatureTypeBuilder(parsed);
        try {
            final CoordinateReferenceSystem crs = CRS.forCode(toEpsgCode(ftt.getDefaultCRS()));
            for (PropertyTypeBuilder pt : builder.properties()) {
                if (pt instanceof AttributeTypeBuilder atb && Geometry.class.isAssignableFrom(atb.getValueClass())) {
                    atb.setCRS(crs);
                    atb.addRole(AttributeRole.DEFAULT_GEOMETRY);
                }
            }
        } catch (FactoryException ex) {
            throw new DataStoreException(ex);
        }

        type = builder.build();
        return type;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        final List<? extends BoundingBox> bboxes = ftt.getBoundingBox();
        if (bboxes == null || bboxes.isEmpty()) {
            return Optional.empty();
        }
        final BoundingBox bbox = bboxes.get(0);
        try {
            final String crsCode = bbox.getCrs();
            final CoordinateReferenceSystem crs = crsCode != null ? CRS.forCode(crsCode) : CommonCRS.WGS84.normalizedGeographic();
            final GeneralEnvelope env = new GeneralEnvelope(crs);
            final List<Double> lower = bbox.getLowerCorner();
            final List<Double> upper = bbox.getUpperCorner();
            final Integer dims = bbox.getDimensions();
            if (dims == null) {
                return Optional.empty();
            }
            for (int i = 0; i < dims; i++) {
                env.setRange(i, lower.get(i), upper.get(i));
            }
            return Optional.of(env);
        } catch (FactoryException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final FeatureType ft = getType();

        final GetFeature params;
        try {
            params = new GetFeature().typeName(ftt.getName()).version(store.getVersion());
        } catch (CapabilitiesException ex) {
            throw new DataStoreException(ex);
        }

        final InputStream in;
        try {
            in = store.getApi().getFeatureAsStream(params).send().body();
        } catch (ServiceException ex) {
            throw new DataStoreException(ex);
        }

        final JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader(ft);
        reader.getProperties().put(JAXPStreamFeatureReader.SKIP_UNEXPECTED_PROPERTY_TAGS, true);
        final Stream<Feature> fr;
        try {
            fr = reader.readAsStream(in);
        } catch (Exception ex) {
            throw new DataStoreException(ex);
        }

        return fr;
    }

    private static String toEpsgCode(String defaultCRS) {
        if (defaultCRS != null && defaultCRS.contains("EPSG")) {
            final int last = defaultCRS.lastIndexOf(':');
            return "EPSG:" + defaultCRS.substring(last + 1);
        }
        return defaultCRS;
    }

}

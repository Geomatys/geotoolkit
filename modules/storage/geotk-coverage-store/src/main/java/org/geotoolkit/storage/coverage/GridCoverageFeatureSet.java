/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.storage.coverage;

import java.util.Optional;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.geometry.GeometricUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.internal.feature.CoverageFeature;
import org.geotoolkit.internal.feature.TypeConventions;
import org.geotoolkit.storage.AbstractResource;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;

/**
 * Decorate a GridCoverageResource as a FeatureSet.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GridCoverageFeatureSet extends AbstractResource implements FeatureSet {

    private final GridCoverageResource gcr;

    public GridCoverageFeatureSet(GridCoverageResource gcr) throws DataStoreException {
        identifier = NamedIdentifier.castOrCopy(gcr.getIdentifier().orElse(null));
        this.gcr = gcr;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.ofNullable(gcr.getGridGeometry().getEnvelope());
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        final GridCoverageReader reader = gcr.acquireReader();
        try {
            final FeatureType type = CoverageFeature.createCoverageType(reader);
            gcr.recycle(reader);
            return type;
        } catch (CoverageStoreException ex) {
            try {
                reader.dispose();
            } catch (CoverageStoreException ex2) {
                ex.addSuppressed(ex2);
            }
            throw ex;
        }
    }

    @Override
    public Stream<Feature> features(boolean parallal) throws DataStoreException {
        final FeatureType type = getType();
        final FeatureAssociationRole role = (FeatureAssociationRole) type.getProperty(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        final Feature feature = type.newInstance();

        try {
            final GridGeometry gridGeom = gcr.getGridGeometry();
            Envelope envelope = gridGeom.getEnvelope();
            if (envelope != null) {
                Geometry geom = GeometricUtilities.toJTSGeometry(envelope, GeometricUtilities.WrapResolution.SPLIT);
                if (geom != null) {
                    JTS.setCRS(geom, gridGeom.getCoordinateReferenceSystem());
                    feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), geom);
                }
            }
        } catch (CoverageStoreException ex) {
            throw ex;
        }
        feature.setProperty(CoverageFeature.coverageRecords(gcr, role));
        return Stream.of(feature);
    }
}

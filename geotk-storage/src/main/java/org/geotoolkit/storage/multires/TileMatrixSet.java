/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage.multires;

import java.util.Collections;
import java.util.Optional;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.distribution.DefaultFormat;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.base.MetadataBuilder;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.iso.Types;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 * A TileMatrixSet is a collection of TileMatrix in the same CRS but at different
 * scale levels.
 * <p>
 * Note : if the {@linkplain CoordinateReferenceSystem } of the TileMatrixSet has more
 * then two dimensions, it is possible to find TileMatrix at the same scale.
 * Each TileMatrix been located on a different slice in one of the {@linkplain CoordinateReferenceSystem }
 * axis.
 * </p>
 *
 * @author Johann Sorel (Geomatys)
 */
public interface TileMatrixSet extends org.apache.sis.storage.tiling.TileMatrixSet {

    public static final String AUTHORITY_MIME = "MIME";
    public static final String AUTHORITY_EXTENSION = "EXTENSION";

    /**
     * Returns an envelope that encompasses all {@code TileMatrix} instances in this set.
     * This is the {@linkplain org.apache.sis.geometry.GeneralEnvelope#add(Envelope) union}
     * of all values returned by {@code TileMatrix.getTilingScheme().getEnvelope()}.
     * May be empty if too costly to compute.
     *
     * @return the bounding box for all tile matrices in CRS coordinates, if available.
     */
    @Override
    default Optional<Envelope> getEnvelope() {
        final GeneralEnvelope env = new GeneralEnvelope(getCoordinateReferenceSystem());
        env.setToNaN();
        for (TileMatrix tileMatrix : getTileMatrices().values()) {
            if (env.isAllNaN()) {
                env.setEnvelope(tileMatrix.getTilingScheme().getEnvelope());
            } else {
                env.add(tileMatrix.getTilingScheme().getEnvelope());
            }
        }
        return Optional.of(env);
    }

    /**
     * Returns information about this {@link TileMatrixSet}.
     *
     * @return information about this {@link TileMatrixSet}. Should not be {@code null}.
     * @throws DataStoreException if an error occurred while reading the metadata.
     */
    default Metadata getMetaData() throws DataStoreException {
        final MetadataBuilder mb = new MetadataBuilder();
        final GenericName identifier = getIdentifier();
        if (identifier != null) mb.addTitle(identifier.toString());
        getEnvelope().ifPresent((envelope) -> mb.addExtent(envelope, null));
        return mb.buildAndFreeze();
    }

    /**
     * Create format from common informations
     * @param longName
     * @param shortName
     * @param mimeType
     * @param extension
     * @return
     */
    public static DefaultFormat createFormat(String longName, String shortName, String mimeType, String extension) {
        ArgumentChecks.ensureNonNull("LongName", longName);
        ArgumentChecks.ensureNonNull("ShortName", shortName);
        final DefaultFormat format = new DefaultFormat();

        final DefaultCitation citation = new DefaultCitation();
        citation.setTitle(Types.toInternationalString(longName));
        citation.setAlternateTitles(Collections.singletonList(Types.toInternationalString(shortName)));
        format.setFormatSpecificationCitation(citation);

        if (mimeType != null && !mimeType.isBlank()) {
            final DefaultCitation mieAuth = new DefaultCitation(AUTHORITY_MIME);
            final DefaultIdentifier mimeIdentifier = new DefaultIdentifier(mieAuth, mimeType);
            format.getIdentifiers().add(mimeIdentifier);
        }

        if (extension != null && !extension.isBlank()) {
            final DefaultCitation mieAuth = new DefaultCitation(AUTHORITY_EXTENSION);
            final DefaultIdentifier mimeIdentifier = new DefaultIdentifier(mieAuth, extension);
            format.getIdentifiers().add(mimeIdentifier);
        }

        return format;
    }
}

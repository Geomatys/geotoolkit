/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.kml.map;

import com.vividsolutions.jts.geom.Geometry;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LatLonBox;
import org.geotoolkit.data.kml.model.MultiGeometry;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.map.AbstractMapLayer;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.style.MutableStyle;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

import org.opengis.geometry.Envelope;
import org.geotoolkit.data.kml.xml.KmlConstants;

/**
 *
 * @author Samuel Andrés
 * @module
 */
public class KmlMapLayer extends AbstractMapLayer {

    final Kml kml;

    public KmlMapLayer(MutableStyle style, Kml kml) throws IOException {
        super(style);
        this.kml = kml;
        graphicBuilders().add(KMLGraphicBuilder.INSTANCE);
    }

    /*
     * -------------------------------------------------------------------------
     * BOUNDS METHODS
     * -------------------------------------------------------------------------
     */

    /**
     * Retrieves Kmldocument bounds.
     */
    @Override
    public Envelope getBounds() {
        final JTSEnvelope2D envelope = new JTSEnvelope2D(CommonCRS.WGS84.normalizedGeographic());
        return getFeatureEnvelope(kml.getAbstractFeature(), envelope);
    }

    /**
     * This method extends envelope with feature contents.
     */
    private JTSEnvelope2D getFeatureEnvelope(Feature feature, JTSEnvelope2D envelope) {

        final FeatureType featureType = feature.getType();

        if (featureType.equals(KmlModelConstants.TYPE_PLACEMARK)) {
            envelope = getAbstractGeometryEnvelope((AbstractGeometry) feature.getPropertyValue(KmlConstants.TAG_GEOMETRY), envelope);
        } else if (KmlModelConstants.TYPE_CONTAINER.isAssignableFrom(featureType)) {
            Collection<?> properties = null;
            if (featureType.equals(KmlModelConstants.TYPE_DOCUMENT)) {
                properties = (Collection<?>) feature.getPropertyValue(KmlConstants.TAG_FEATURES);
            } else if (featureType.equals(KmlModelConstants.TYPE_FOLDER)) {
                properties = (Collection<?>) feature.getPropertyValue(KmlConstants.TAG_FEATURES);
            }
            if (properties != null) {
                Iterator<?> i = properties.iterator();
                while (i.hasNext()) {
                    envelope = getFeatureEnvelope((Feature) i.next(), envelope);
                }
            }
        } else if (KmlModelConstants.TYPE_OVERLAY.isAssignableFrom(featureType)) {
            if (featureType.equals(KmlModelConstants.TYPE_GROUND_OVERLAY)) {
                final LatLonBox latLonBox = (LatLonBox) feature.getPropertyValue(KmlConstants.TAG_LAT_LON_BOX);
                envelope.expandToInclude(
                        new JTSEnvelope2D(
                        latLonBox.getWest(), latLonBox.getEast(),
                        latLonBox.getSouth(), latLonBox.getNorth(),
                        CommonCRS.WGS84.normalizedGeographic()));
            }
        }
        return envelope;
    }

    /**
     * This method extends envelope with Geometry content.
     */
    private JTSEnvelope2D getAbstractGeometryEnvelope(AbstractGeometry geometry, JTSEnvelope2D envelope) {
        if (geometry instanceof Geometry) {
            envelope.expandToInclude(new JTSEnvelope2D(((Geometry) geometry).getEnvelopeInternal(),
                    CommonCRS.WGS84.normalizedGeographic()));
        } else if (geometry instanceof MultiGeometry) {
            for (AbstractGeometry geom : ((MultiGeometry) geometry).getGeometries()) {
                envelope = getAbstractGeometryEnvelope(geom, envelope);
            }
        }
        return envelope;
    }
}

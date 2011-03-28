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
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.map.AbstractMapLayer;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.MutableStyle;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class KmlMapLayer extends AbstractMapLayer {

    final Kml kml;    

    public KmlMapLayer(MutableStyle style, Kml kml) 
            throws IOException {
        
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
     * <p>Retrieves Kmldocument bounds.</p>
     *
     * @{@inheritDoc }
     *
     * @return
     */
    @Override
    public Envelope getBounds() {

        final JTSEnvelope2D envelope = new JTSEnvelope2D(DefaultGeographicCRS.WGS84);
        return this.getFeatureEnvelope(this.kml.getAbstractFeature(), envelope);
    }

    /**
     * <p>This method extends envelope with feature contents.</p>
     *
     * @param feature
     * @return
     */
    private JTSEnvelope2D getFeatureEnvelope(Feature feature, JTSEnvelope2D envelope) {

        final FeatureType featureType = feature.getType();

        if (featureType.equals(KmlModelConstants.TYPE_PLACEMARK)) {
            if (feature.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()) != null) {
                envelope = this.getAbstractGeometryEnvelope(
                        (AbstractGeometry) feature.getProperty(
                        KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue(), envelope);
            }
        }
        else if (FeatureTypeUtilities.isDecendedFrom(featureType, KmlModelConstants.TYPE_CONTAINER)) {
            Collection<Property> properties = null;
            if (featureType.equals(KmlModelConstants.TYPE_DOCUMENT)) {
                properties = feature.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName());
            } else if (featureType.equals(KmlModelConstants.TYPE_FOLDER)) {
                properties = feature.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName());
            }
            if (properties != null) {
                Iterator i = properties.iterator();
                while (i.hasNext()) {
                    envelope = this.getFeatureEnvelope((Feature) ((Property) i.next()).getValue(), envelope);
                }
            }
        }
        else if (FeatureTypeUtilities.isDecendedFrom(featureType, KmlModelConstants.TYPE_OVERLAY)) {
            if (featureType.equals(KmlModelConstants.TYPE_GROUND_OVERLAY)) {
                if (feature.getProperty(KmlModelConstants.ATT_GROUND_OVERLAY_LAT_LON_BOX.getName()) != null) {
                    final LatLonBox latLonBox = (LatLonBox) feature.getProperty(
                            KmlModelConstants.ATT_GROUND_OVERLAY_LAT_LON_BOX.getName()).getValue();
                    envelope.expandToInclude(
                            new JTSEnvelope2D(
                            latLonBox.getWest(), latLonBox.getEast(),
                            latLonBox.getSouth(), latLonBox.getNorth(),
                            DefaultGeographicCRS.WGS84));
                }
            }
        }
        return envelope;
    }

    /**
     * <p>This method extends envelope with Geometry content.</p>
     *
     * @param geometry
     * @return
     */
    private JTSEnvelope2D getAbstractGeometryEnvelope(
            AbstractGeometry geometry, JTSEnvelope2D envelope) {

        if (geometry instanceof Geometry) {
            envelope.expandToInclude(new JTSEnvelope2D(((Geometry) geometry).getEnvelopeInternal(),
                    DefaultGeographicCRS.WGS84));
        } else if (geometry instanceof MultiGeometry) {
            for (AbstractGeometry geom : ((MultiGeometry) geometry).getGeometries()) {
                envelope = this.getAbstractGeometryEnvelope(geom, envelope);
            }
        }
        return envelope;
    }
   
}

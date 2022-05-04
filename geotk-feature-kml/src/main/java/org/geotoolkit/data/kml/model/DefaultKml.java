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
package org.geotoolkit.data.kml.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.data.kml.xml.KmlConstants;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Samuel Andrés
 * @module
 */
public class DefaultKml extends AbstractResource implements Kml {

    private final Extensions extensions = new Extensions();
    private String version = URI_KML_2_2;
    private NetworkLinkControl networkLinkControl;
    private Feature abstractFeature;
    private Map<String, String> extensionsUris = new HashMap<>();

    public DefaultKml() {
        super(null, false);
    }

    public DefaultKml(NetworkLinkControl networkLinkControl,
            Feature abstractFeature,
            List<SimpleTypeContainer> kmlSimpleExtensions,
            List<Object> kmlObjectExtensions) {
        super(null, false);
        this.networkLinkControl = networkLinkControl;
        this.abstractFeature = abstractFeature;
        if (kmlSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.KML).addAll(kmlSimpleExtensions);
        }
        if (kmlObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.KML).addAll(kmlObjectExtensions);
        }
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public NetworkLinkControl getNetworkLinkControl() {
        return this.networkLinkControl;
    }

    @Override
    public Feature getAbstractFeature() {
        return this.abstractFeature;
    }

    @Override
    public void setVersion(String version) throws KmlException {
        if (URI_KML_2_1.equals(version) || URI_KML_2_2.equals(version)) {
            this.version = version;
        } else {
            throw new KmlException("Bad Kml version Uri. This reader supports 2.1 and 2.2 versions.");
        }
    }

    @Override
    public void setNetworkLinkControl(NetworkLinkControl networkLinkCOntrol) {
        this.networkLinkControl = networkLinkCOntrol;
    }

    @Override
    public void setAbstractFeature(Feature feature) {
        this.abstractFeature = feature;
    }

    @Override
    public Extensions extensions() {
        return this.extensions;
    }

    @Override
    public Map<String, String> getExtensionsUris() {
        return this.extensionsUris;
    }

    @Override
    public void setExtensionsUris(Map<String, String> extensionsUris) {
        this.extensionsUris = extensionsUris;
    }

    @Override
    public void addExtensionUri(String uri, String prefix){
        this.extensionsUris.put(uri, prefix);
    }

    @Override
    public String toString() {
        String resultat = "KML DEFAULT : "
                + "AbstractFeature : " + this.abstractFeature;
        return resultat;
    }

    @Override
    public Optional<Envelope> getEnvelope() {
        final JTSEnvelope2D envelope = new JTSEnvelope2D(CommonCRS.WGS84.normalizedGeographic());
        return Optional.ofNullable(getFeatureEnvelope(getAbstractFeature(), envelope));
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

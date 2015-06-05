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

package org.geotoolkit.wmc;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.client.Client;
import org.geotoolkit.client.ClientFactory;
import org.geotoolkit.client.ClientFinder;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.feature.type.DefaultName;
import org.apache.sis.geometry.Envelope2D;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.style.DefaultDescription;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.RandomStyleBuilder;
import org.geotoolkit.style.StyleConstants;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.wmc.xml.v110.*;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.feature.type.Name;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.Description;
import org.opengis.util.FactoryException;
import javax.xml.bind.JAXBContext;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class WMCUtilities {

    public static String getServiceId(String source) {
        String id = null;
        if (source.toLowerCase().contains("wms")) {
            id = "wms";
        }
        if (source.toLowerCase().contains("wmts")) {
            id = "wmts";
        }
        if (source.toLowerCase().contains("wfs")) {
            id = "wfs";
        }
        return id;
    }

    /**
     * Get a {@linkplain MarshallerPool marshaller pool} for the WMC binding.
     *
     * @return The {@linkplain MarshallerPool marshaller pool}.
     * @throws JAXBException
     */
    public static MarshallerPool getMarshallerPool() throws JAXBException {
        return new MarshallerPool(JAXBContext.newInstance(
                "org.geotoolkit.ogc.xml.exception:" +
                "org.geotoolkit.wmc.xml.v110:" +
                "org.geotoolkit.ogc.xml.v100"), null);
    }

    /**
     * This method will get WMC informations to create a new valid
     * {@link MapContext}.
     *
     * @param source : An {@link InputSream}, the WMC as an xml.
     * @return A map context containing informations given by a wmc document.
     * @throws JAXBException if the xml cannot be read.
     */
    public static MapContext getMapContext(InputStream source) throws JAXBException {
        final MarshallerPool pool = getMarshallerPool();
        final Unmarshaller um = pool.acquireUnmarshaller();
        Object o = um.unmarshal(source);
        if (o instanceof JAXBElement) {
            o = ((JAXBElement) o).getValue();
        }
        //Get needed markups
        ViewContextType root = (ViewContextType) o;

        final MapContext context = getMapContext(root);

        pool.recycle(um);
        return context;
    }

    /**
     * Create a Geotk {@link MapContext} embeding layers described by given wmc.
     *
     * @param root The {@link ViewContextType}, root markup of the wmc.
     * @return a {@link MapContext} containing layers extracted from wmc.
     */
    public static MapContext getMapContext(ViewContextType root) {
        ArgumentChecks.ensureNonNull("ViewContextType", root);
        CoordinateReferenceSystem srs = CommonCRS.WGS84.normalizedGeographic();
        final MapContext context = MapBuilder.createContext();

        //Get needed markups
        GeneralType general = root.getGeneral();
        LayerListType layers = root.getLayerList();

        if (general != null) {
            //Retrieve enveloppe for the map context.
            BoundingBoxType bbox = general.getBoundingBox();
            try {
                srs = CRS.decode(bbox.getSRS(), true);
            } catch (NoSuchAuthorityCodeException ex) {
                Logger.getLogger(WMCUtilities.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FactoryException ex) {
                Logger.getLogger(WMCUtilities.class.getName()).log(Level.SEVERE, null, ex);
            }
            final double width = bbox.getMaxx().subtract(bbox.getMinx()).doubleValue();
            final double height = bbox.getMaxy().subtract(bbox.getMiny()).doubleValue();
            Envelope aoi = new Envelope2D(srs, bbox.getMinx().doubleValue(), bbox.getMiny().doubleValue(), width, height);

            SimpleInternationalString title = new SimpleInternationalString(general.getTitle());
            SimpleInternationalString description = new SimpleInternationalString((general.getAbstract() == null) ? "No description" : general.getAbstract());
            Description desc = new DefaultDescription(title, description);
            context.setDescription(desc);
            context.setAreaOfInterest(aoi);
        }
        //set context general values
        context.setName(root.getId());
        context.setCoordinateReferenceSystem(srs);

        //fill context with layers
        for (final LayerType layerType : layers.getLayer()) {

            //build server from parameters.
            final ServerType serverType = layerType.getServer();
            final Client server;
            final String serviceId = getServiceId(serverType.getService().value());
            final Name layerName = DefaultName.valueOf(layerType.getName());
            try {
                final URL serviceURL = new URL(serverType.getOnlineResource().getHref());

                final ClientFactory factory = ClientFinder.getFactoryById(serviceId);
                final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
                parameters.put("identifier", serviceId);
                parameters.put("version", serverType.getVersion());
                parameters.put("url", serviceURL);
                server = factory.open(parameters);
            } catch (Exception ex) {
                Logger.getLogger(WMCUtilities.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }

            if (server instanceof CoverageStore) {
                final CoverageStore cs = (CoverageStore) server;
                try {
                    for (Name n : cs.getNames()) {
                        if (n.tip().toString().equalsIgnoreCase(layerName.tip().toString())) {
                            final CoverageReference ref = cs.getCoverageReference(n);
                            final CoverageMapLayer mapLayer = MapBuilder.createCoverageLayer(ref,
                                    GO2Utilities.STYLE_FACTORY.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER));
                            context.layers().add(mapLayer);
                        }
                    }
                } catch (DataStoreException ex) {
                    Logger.getLogger(WMCUtilities.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }

            } else if (server instanceof FeatureStore) {
                final FeatureStore wfs = (FeatureStore) server;
                final Session storeSession = wfs.createSession(true);
                final FeatureCollection collection = storeSession.getFeatureCollection(QueryBuilder.all(layerName));
                final MutableStyle style = RandomStyleBuilder.createRandomVectorStyle(collection.getFeatureType());
                final MapLayer layer = MapBuilder.createFeatureLayer(collection, style);
                context.layers().add(layer);
            }
        }
        return context;
    }
}

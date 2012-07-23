/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import org.geotoolkit.client.Server;
import org.geotoolkit.client.ServerFactory;
import org.geotoolkit.client.ServerFinder;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.data.wfs.WebFeatureServer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.DefaultDescription;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.RandomStyleFactory;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.wmc.xml.v110.*;
import org.geotoolkit.wms.WMSCoverageReference;
import org.geotoolkit.wms.WebMapServer;
import org.geotoolkit.wmts.WebMapTileServer;
import org.geotoolkit.xml.MarshallerPool;
import org.opengis.feature.type.Name;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.Description;
import org.opengis.util.FactoryException;

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
     * This method will get WMC informations to create a new valid {@link MapContext}.
     *
     * @return A map context containing informations given by a wmc document.
     */
    public static MapContext getMapContext(InputStream source) throws JAXBException {
        final MapContext context = MapBuilder.createContext();
        final MarshallerPool pool = new MarshallerPool("org.geotoolkit.ogc.xml.exception:" + "org.geotoolkit.wmc.xml.v110");
        final Unmarshaller um = pool.acquireUnmarshaller();
        Object o = um.unmarshal(source);
        if (o instanceof JAXBElement) {
            o = ((JAXBElement) o).getValue();
        }
        //Get needed markups
        ViewContextType root = (ViewContextType) o;
        GeneralType general = root.getGeneral();
        LayerListType layers = root.getLayerList();
        BoundingBoxType bbox = general.getBoundingBox();


        //Retrieve enveloppe for the map context.
        CoordinateReferenceSystem srs = null;//DefaultGeographicCRS.WGS84;
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

        //set context general values
        context.setName(root.getId());
        SimpleInternationalString title = new SimpleInternationalString(general.getTitle());
        SimpleInternationalString description = new SimpleInternationalString((general.getAbstract() == null) ? "No description" : general.getAbstract());
        Description desc = new DefaultDescription(title, description);
        context.setDescription(desc);
        context.setAreaOfInterest(aoi);
        context.setCoordinateReferenceSystem(srs);

        //fill context with layers
        for (final LayerType layerType : layers.getLayer()) {

            //build server from parameters.
            final ServerType serverType = layerType.getServer();
            final Server server;
            final String serviceId = getServiceId(serverType.getService().value());
            final Name layerName = DefaultName.valueOf(layerType.getName());
            try {
                final URL serviceURL = new URL(serverType.getOnlineResource().getHref());

                final ServerFactory factory = ServerFinder.getFactory(serviceId);
                final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
                parameters.put("identifier", serviceId);
                parameters.put("version", serverType.getVersion());
                parameters.put("url", serviceURL);
                server = factory.create(parameters);
            } catch (Exception ex) {
                Logger.getLogger(WMCUtilities.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }

            if(server instanceof CoverageStore){
                final CoverageStore cs = (CoverageStore) server;
                try{
                    for(Name n : cs.getNames()){
                        if(n.getLocalPart().equalsIgnoreCase(layerName.getLocalPart())){                            
                            final CoverageReference ref = cs.getCoverageReference(n);
                            final CoverageMapLayer mapLayer = MapBuilder.createCoverageLayer(ref, 
                                    GO2Utilities.STYLE_FACTORY.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), ref.getName().toString());
                            context.layers().add(mapLayer);
                        }
                    }
                }catch(DataStoreException ex){
                    Logger.getLogger(WMCUtilities.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }                
                
            } else if(server instanceof DataStore){
                final DataStore wfs = (DataStore) server;
                final Session storeSession = wfs.createSession(true);
                final FeatureCollection collection = storeSession.getFeatureCollection(QueryBuilder.all(layerName));
                final MutableStyle style = RandomStyleFactory.createRandomVectorStyle(collection);
                final MapLayer layer = MapBuilder.createFeatureLayer(collection, style);
                context.layers().add(layer);
            }
        }

        pool.release(um);
        return context;
    }
}

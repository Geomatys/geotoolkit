package org.geotoolkit.data.kml.xml;

import org.geotoolkit.data.xal.xml.XalReader;
import org.geotoolkit.data.atom.xml.AtomReader;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.KmlFactory;
import org.geotoolkit.data.kml.DefaultKmlFactory;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.model.AbstractColorStyle;
import org.geotoolkit.data.kml.model.AbstractContainer;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.AbstractLatLonBox;
import org.geotoolkit.data.kml.model.AbstractObject;
import org.geotoolkit.data.kml.model.AbstractOverlay;
import org.geotoolkit.data.kml.model.AbstractStyleSelector;
import org.geotoolkit.data.kml.model.AbstractSubStyle;
import org.geotoolkit.data.kml.model.AbstractTimePrimitive;
import org.geotoolkit.data.kml.model.AbstractView;
import org.geotoolkit.data.kml.model.Alias;
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.BalloonStyle;
import org.geotoolkit.data.kml.model.BasicLink;
import org.geotoolkit.data.kml.model.Boundary;
import org.geotoolkit.data.kml.model.Camera;
import org.geotoolkit.data.kml.model.Change;
import org.geotoolkit.data.kml.model.ColorMode;
import org.geotoolkit.data.kml.model.Coordinate;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Create;
import org.geotoolkit.data.kml.model.Data;
import org.geotoolkit.data.kml.model.Delete;
import org.geotoolkit.data.kml.model.DisplayMode;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.Folder;
import org.geotoolkit.data.kml.model.GridOrigin;
import org.geotoolkit.data.kml.model.GroundOverlay;
import org.geotoolkit.data.kml.model.Icon;
import org.geotoolkit.data.kml.model.IconStyle;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.ImagePyramid;
import org.geotoolkit.data.kml.model.ItemIcon;
import org.geotoolkit.data.kml.model.ItemIconState;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.LabelStyle;
import org.geotoolkit.data.kml.model.LatLonAltBox;
import org.geotoolkit.data.kml.model.LatLonBox;
import org.geotoolkit.data.kml.model.LineString;
import org.geotoolkit.data.kml.model.LineStyle;
import org.geotoolkit.data.kml.model.LinearRing;
import org.geotoolkit.data.kml.model.Link;
import org.geotoolkit.data.kml.model.ListItem;
import org.geotoolkit.data.kml.model.ListStyle;
import org.geotoolkit.data.kml.model.Location;
import org.geotoolkit.data.kml.model.Lod;
import org.geotoolkit.data.kml.model.LookAt;
import org.geotoolkit.data.kml.model.Model;
import org.geotoolkit.data.kml.model.MultiGeometry;
import org.geotoolkit.data.kml.model.NetworkLink;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.Orientation;
import org.geotoolkit.data.kml.model.Pair;
import org.geotoolkit.data.kml.model.PhotoOverlay;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.PolyStyle;
import org.geotoolkit.data.kml.model.Polygon;
import org.geotoolkit.data.kml.model.RefreshMode;
import org.geotoolkit.data.kml.model.Region;
import org.geotoolkit.data.kml.model.ResourceMap;
import org.geotoolkit.data.kml.model.Scale;
import org.geotoolkit.data.kml.model.Schema;
import org.geotoolkit.data.kml.model.SchemaData;
import org.geotoolkit.data.kml.model.ScreenOverlay;
import org.geotoolkit.data.kml.model.Shape;
import org.geotoolkit.data.kml.model.SimpleData;
import org.geotoolkit.data.kml.model.SimpleField;
import org.geotoolkit.data.kml.model.Snippet;
import org.geotoolkit.data.kml.model.Style;
import org.geotoolkit.data.kml.model.StyleMap;
import org.geotoolkit.data.kml.model.StyleState;
import org.geotoolkit.data.kml.model.TimeSpan;
import org.geotoolkit.data.kml.model.TimeStamp;
import org.geotoolkit.data.kml.model.Units;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.model.Vec2;
import org.geotoolkit.data.kml.model.ViewRefreshMode;
import org.geotoolkit.data.kml.model.ViewVolume;
import org.geotoolkit.data.xal.model.AddressDetails;
import org.geotoolkit.data.xal.model.XalException;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.geotoolkit.data.utilities.DateUtilities;
import org.geotoolkit.xml.StaxStreamReader;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class KmlReader extends StaxStreamReader {

    private Kml root;
    private static final KmlFactory kmlFactory = new DefaultKmlFactory();
    private final XalReader xalReader = new XalReader();
    private final AtomReader atomReader = new AtomReader();
    private final DateUtilities fastDateParser = new DateUtilities();

    public KmlReader() {
        super();
//        System.setProperty("javax.xml.stream.XMLInputFactory", "com.bea.xml.stream.MXParserFactory");
//        System.setProperty("javax.xml.stream.XMLEventFactory", "com.bea.xml.stream.EventFactory");

//        System.setProperty("javax.xml.stream.XMLInputFactory", "com.ctc.wstx.stax.WstxInputFactory");
//        System.setProperty("javax.xml.stream.XMLEventFactory", "com.ctc.wstx.stax.WstxEventFactory");
    }

    @Override
    public void setInput(Object input) throws IOException, XMLStreamException {
        super.setInput(input);
        this.xalReader.setInput(reader);
        this.atomReader.setInput(reader);
    }

    /**
     * <p>This method reads the Kml document assigned to the KmlReader.</p>
     *
     * @return The Kml object mapping the document.
     */
    public Kml read() {

        try {

            while (reader.hasNext()) {

                switch (reader.next()) {

                    case XMLStreamConstants.START_ELEMENT:
                        final String eName = reader.getLocalName();
                        final String eUri = reader.getNamespaceURI();

                        if (URI_KML.equals(eUri)) {
                            if (TAG_KML.equals(eName)) {
                                this.root = this.readKml();
                            }
                        }
                        break;
                }
            }
        } catch (XMLStreamException ex) {
            Logger.getLogger(KmlReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KmlException ex) {
            System.out.println("KML EXCEPTION : " + ex.getMessage());
        }
        return this.root;
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Kml readKml() throws XMLStreamException, KmlException {
        NetworkLinkControl networkLinkControl = null;
        AbstractFeature abstractFeature = null;
        List<SimpleType> kmlSimpleExtensions = null;
        List<AbstractObject> kmlObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {
                        if (TAG_NETWORK_LINK_CONTROL.equals(eName)) {
                            networkLinkControl = this.readNetworkLinkControl();
                        } else if (isAbstractFeature(eName)) {
                            abstractFeature = this.readAbstractFeature(eName);
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_KML.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createKml(networkLinkControl, abstractFeature, kmlSimpleExtensions, kmlObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Placemark readPlacemark() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractFeature
        String name = null;
        boolean visibility = DEF_VISIBILITY;
        boolean open = DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink link = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        String snippet = null;
        String description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        String styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<AbstractStyleSelector>();
        Region region = null;
        ExtendedData extendedData = null;
        List<SimpleType> featureSimpleExtensions = null;
        List<AbstractObject> featureObjectExtensions = null;

        // Placemark
        AbstractGeometry abstractGeometry = null;
        List<SimpleType> placemarkSimpleExtensions = null;
        List<AbstractObject> placemarkObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_NAME.equals(eName)) {
                            name = reader.getElementText();
                        } else if (TAG_VISIBILITY.equals(eName)) {
                            visibility = parseBoolean(reader.getElementText());
                        } else if (TAG_OPEN.equals(eName)) {
                            open = parseBoolean(reader.getElementText());
                        } else if (TAG_ADDRESS.equals(eName)) {
                            address = reader.getElementText();
                        } else if (TAG_PHONE_NUMBER.equals(eName)) {
                            phoneNumber = reader.getElementText();
                        } else if (TAG_SNIPPET.equals(eName)) {
                            snippet = reader.getElementText();
                        } else if (TAG_DESCRIPTION.equals(eName)) {
                            description = reader.getElementText();
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = reader.getElementText();
                        } else if (isAbstractView(eName)) {
                            view = this.readAbstractView(eName);
                        } else if (isAbstractTimePrimitive(eName)) {
                            timePrimitive = this.readAbstractTimePrimitive(eName);
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector.add(this.readAbstractStyleSelector(eName));
                        } else if (TAG_REGION.equals(eName)) {
                            region = this.readRegion();
                        } else if (TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = this.readExtendedData();
                        }

                        // PLACEMARK
                        else if (isAbstractGeometry(eName)) {
                            abstractGeometry = this.readAbstractGeometry(eName);
                        }


                    } else if (URI_ATOM.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = this.readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = this.readAtomLink();
                        }
                    }
                    if (URI_XAL.equals(eUri)) {
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = this.readXalAddressDetails();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_PLACEMARK.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createPlacemark(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber, snippet, description, view, timePrimitive, styleUrl,
                styleSelector, region, extendedData, featureSimpleExtensions, featureObjectExtensions,
                abstractGeometry, placemarkSimpleExtensions, placemarkObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Region readRegion() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // Region
        LatLonAltBox latLonAltBox = null;
        Lod lod = null;
        List<SimpleType> regionSimpleExtensions = null;
        List<AbstractObject> regionObjectExtentions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // REGION
                        if (TAG_LAT_LON_ALT_BOX.equals(eName)) {
                            latLonAltBox = this.readLatLonAltBox();
                        } else if (TAG_LOD.equals(eName)) {
                            lod = this.readLod();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_REGION.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createRegion(objectSimpleExtensions, idAttributes,
                latLonAltBox, lod, regionSimpleExtensions, regionObjectExtentions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private Lod readLod() throws XMLStreamException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // Lod
        double minLodPixels = DEF_MIN_LOD_PIXELS;
        double maxLodPixels = DEF_MAX_LOD_PIXELS;
        double minFadeExtent = DEF_MIN_FADE_EXTENT;
        double maxFadeExtent = DEF_MAX_FADE_EXTENT;
        List<SimpleType> lodSimpleExtentions = null;
        List<AbstractObject> lodObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // REGION
                        if (TAG_MIN_LOD_PIXELS.equals(eName)) {
                            minLodPixels = parseDouble(reader.getElementText());
                        } else if (TAG_MAX_LOD_PIXELS.equals(eName)) {
                            maxLodPixels = parseDouble(reader.getElementText());
                        } else if (TAG_MIN_FADE_EXTENT.equals(eName)) {
                            minFadeExtent = parseDouble(reader.getElementText());
                        } else if (TAG_MAX_FADE_EXTENT.equals(eName)) {
                            maxFadeExtent = parseDouble(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LOD.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createLod(objectSimpleExtensions, idAttributes,
                minLodPixels, maxLodPixels, minFadeExtent, maxFadeExtent, lodSimpleExtentions, lodObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private ExtendedData readExtendedData() throws XMLStreamException {
        List<Data> datas = new ArrayList<Data>();
        List<SchemaData> schemaDatas = new ArrayList<SchemaData>();
        List<Object> anyOtherElements = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // EXTENDED DATA
                        if (TAG_DATA.equals(eName)) {
                            datas.add(this.readData());
                        } else if (TAG_SCHEMA_DATA.equals(eName)) {
                            schemaDatas.add(this.readSchemaData());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_EXTENDED_DATA.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        
        return KmlReader.kmlFactory.createExtendedData(datas, schemaDatas, anyOtherElements);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private Data readData() throws XMLStreamException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // Data
        String displayName = null;
        String value = null;
        List<Object> dataExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // REGION
                        if (TAG_DISPLAY_NAME.equals(eName)) {
                            displayName = reader.getElementText();
                        } else if (TAG_VALUE.equals(eName)) {
                            value = reader.getElementText();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_DATA.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createData(objectSimpleExtensions, idAttributes, displayName, value, dataExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private SchemaData readSchemaData() throws XMLStreamException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // Data
        List<SimpleData> simpleDatas = new ArrayList<SimpleData>();
        List<Object> schemaDataExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // SCHEMA DATA
                        if (TAG_SIMPLE_DATA.equals(eName)) {
                            simpleDatas.add(this.readSimpleData());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_SCHEMA_DATA.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createSchemaData(objectSimpleExtensions, idAttributes, simpleDatas, schemaDataExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private SimpleData readSimpleData() throws XMLStreamException {
        return KmlReader.kmlFactory.createSimpleData(reader.getAttributeValue(null, ATT_NAME), reader.getElementText());
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private NetworkLinkControl readNetworkLinkControl() throws XMLStreamException, KmlException {
        double minRefreshPeriod = DEF_MIN_REFRESH_PERIOD;
        double maxSessionLength = DEF_MAX_SESSION_LENGTH;
        String cookie = null;
        String message = null;
        String linkName = null;
        String linkDescription = null;
        Snippet linkSnippet = null;
        String expires = null;
        Update update = null;
        AbstractView view = null;
        List<SimpleType> networkLinkControlSimpleExtensions = null;
        List<AbstractObject> networkLinkControlObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        if (TAG_MIN_REFRESH_PERIOD.equals(eName)) {
                            minRefreshPeriod = parseDouble(reader.getElementText());
                        }
                        if (TAG_MAX_SESSION_LENGTH.equals(eName)) {
                            maxSessionLength = parseDouble(reader.getElementText());
                        }
                        if (TAG_COOKIE.equals(eName)){
                            cookie = reader.getElementText();
                        }
                        if (TAG_MESSAGE.equals(eName)){
                            message = reader.getElementText();
                        }
                        if (TAG_LINK_NAME.equals(eName)){
                            linkName = reader.getElementText();
                        }
                        if (TAG_LINK_DESCRIPTION.equals(eName)){
                            linkDescription = reader.getElementText();
                        }
                        if (TAG_LINK_SNIPPET.equals(eName)){
                            linkSnippet = this.readSnippet();
                        }
                        if (TAG_EXPIRES.equals(eName)){
                            expires = reader.getElementText();
                        }
                        if (TAG_UPDATE.equals(eName)){
                            update = this.readUpdate();
                        }
                        if (isAbstractView(eName)){
                            this.readAbstractView(eName);
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_NETWORK_LINK_CONTROL.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createNetworkLinkControl(minRefreshPeriod, maxSessionLength,
                cookie, message, linkName, linkDescription, linkSnippet, expires, update, view,
                networkLinkControlSimpleExtensions, networkLinkControlObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private Update readUpdate() throws XMLStreamException, KmlException{
        List<Create> creates = new ArrayList<Create>();
        List<Delete> deletes = new ArrayList<Delete>();
        List<Change> changes = new ArrayList<Change>();
        List<Object> updateOpExtensions = null;
        List<Object> updateExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        if (TAG_CREATE.equals(eName)) {
                            creates.add(this.readCreate());
                        }
                        if (TAG_DELETE.equals(eName)) {
                            deletes.add(this.readDelete());
                        }
                        if (TAG_CHANGE.equals(eName)){
                            changes.add(this.readChange());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_UPDATE.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }

        return KmlReader.kmlFactory.createUpdate(creates, deletes, changes, updateOpExtensions, updateExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Create readCreate() throws XMLStreamException, KmlException{
        List<AbstractContainer> containers = new ArrayList<AbstractContainer>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        if (isAbstractContainer(eName)) {
                            containers.add(this.readAbstractContainer(eName));
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_CREATE.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return KmlReader.kmlFactory.createCreate(containers);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Delete readDelete() throws XMLStreamException, KmlException{
        List<AbstractFeature> features = new ArrayList<AbstractFeature>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        if (isAbstractObject(eName)) {
                            features.add(this.readAbstractFeature(eName));
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_DELETE.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return KmlReader.kmlFactory.createDelete(features);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Change readChange() throws XMLStreamException, KmlException{
        List<AbstractObject> objects = new ArrayList<AbstractObject>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        if (isAbstractObject(eName)) {
                            objects.add(this.readAbstractObject(eName));
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_CHANGE.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return KmlReader.kmlFactory.createChange(objects);
    }

    /**
     *
     * @param eName
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private AbstractObject readAbstractObject(String eName) throws XMLStreamException, KmlException{
        AbstractObject resultat = null;
        if (TAG_REGION.equals(eName)){
            resultat = this.readRegion();
        } else if (TAG_LOD.equals(eName)){
            resultat = this.readLod();
        } else if (TAG_LINK.equals(eName)){
            resultat = this.readLink(eName);
        } else if (TAG_ICON.equals(eName)){
            resultat = this.readIcon(eName);
        } else if (TAG_LOCATION.equals(eName)){
            resultat = this.readLocation();
        } else if (TAG_ORIENTATION.equals(eName)){
            resultat = this.readOrientation();
        } else if (TAG_RESOURCE_MAP.equals(eName)){
            resultat = this.readResourceMap();
        } else if (TAG_SCHEMA_DATA.equals(eName)){
            resultat = this.readSchemaData();
        } else if (TAG_SCALE.equals(eName)){
            resultat = this.readScale();
        } else if (TAG_ALIAS.equals(eName)){
            resultat = this.readAlias();
        } else if (TAG_VIEW_VOLUME.equals(eName)){
            resultat = this.readViewVolume();
        } else if (TAG_IMAGE_PYRAMID.equals(eName)){
            resultat = this.readImagePyramid();
        } else if (TAG_PAIR.equals(eName)){
            resultat = this.readPair();
        } else if (TAG_ITEM_ICON.equals(eName)){
            resultat = this.readItemIcon();
        } else if (isAbstractFeature(eName)){
            resultat = this.readAbstractFeature(eName);
        } else if (isAbstractGeometry(eName)){
            resultat = this.readAbstractGeometry(eName);
        } else if (isAbstractStyleSelector(eName)){
            resultat = this.readAbstractStyleSelector(eName);
        } else if (isAbstractSubStyle(eName)){
            resultat = this.readAbstractSubStyle(eName);
        } else if (isAbstractView(eName)){
           resultat = this.readAbstractView(eName);
        } else if (isAbstractTimePrimitive(eName)){
            resultat= this.readAbstractTimePrimitive(eName);
        } else if (isAbstractLatLonBox(eName)){
            resultat= this.readAbstractLatLonBox(eName);
        }
        return resultat;
    }

    /**
     * 
     * @param eName
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private AbstractLatLonBox readAbstractLatLonBox(String eName) throws XMLStreamException, KmlException{
        AbstractLatLonBox resultat = null;
        if (TAG_LAT_LON_ALT_BOX.equals(eName)){
            resultat = this.readLatLonAltBox();
        } else if (TAG_LAT_LON_BOX.equals(eName)){
            resultat = this.readLatLonBox();
        }
        return resultat;
    }

    /**
     *
     * @param eName
     * @return
     */
    private AbstractSubStyle readAbstractSubStyle(String eName) throws XMLStreamException, KmlException{
        AbstractSubStyle resultat = null;
        if (TAG_BALLOON_STYLE.equals(eName)){
            resultat = this.readBalloonStyle();
        } else if (TAG_LIST_STYLE.equals(eName)){
            resultat = this.readListStyle();
        } else if (isAbstractColorStyle(eName)){
            resultat = this.readAbstractColorStyle(eName);
        }
        return resultat;
    }

    /**
     *
     * @param eName
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private AbstractColorStyle readAbstractColorStyle(String eName) throws XMLStreamException, KmlException{
        AbstractColorStyle resultat = null;
        if (TAG_ICON_STYLE.equals(eName)){
            resultat = this.readIconStyle();
        } else if (TAG_LABEL_STYLE.equals(eName)){
            resultat = this.readLabelStyle();
        } else if (TAG_POLY_STYLE.equals(eName)){
            resultat = this.readPolyStyle();
        } else if (TAG_LINE_STYLE.equals(eName)){
            resultat = this.readLineStyle();
        }
        return resultat;
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    private Snippet readSnippet() throws XMLStreamException{
        int maxLines = DEF_MAX_SNIPPET_LINES_ATT;
        if (reader.getAttributeValue(null, ATT_MAX_LINES) != null){
            maxLines = Integer.parseInt(reader.getAttributeValue(null, ATT_MAX_LINES));
        }
        String content = reader.getElementText();
        return KmlReader.kmlFactory.createSnippet(maxLines, content);
    }

    /**
     *
     * @param eName The tag name.
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private AbstractGeometry readAbstractGeometry(String eName) throws XMLStreamException, KmlException {
        AbstractGeometry resultat = null;
        if (TAG_MULTI_GEOMETRY.equals(eName)) {
            resultat = readMultiGeometry();
        }
        if (TAG_LINE_STRING.equals(eName)) {
            resultat = readLineString();
        }
        if (TAG_POLYGON.equals(eName)) {
            resultat = readPolygon();
        }
        if (TAG_POINT.equals(eName)) {
            resultat = readPoint();
        }
        if (TAG_LINEAR_RING.equals(eName)) {
            resultat = readLinearRing();
        }
        if (TAG_MODEL.equals(eName)) {
            resultat = readModel();
        }
        return resultat;
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Polygon readPolygon() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractGeometry
        List<SimpleType> abstractGeometrySimpleExtensions = null;
        List<AbstractObject> abstractGeometryObjectExtensions = null;

        // Polygon
        boolean extrude = DEF_EXTRUDE;
        boolean tessellate = DEF_TESSELLATE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        Boundary outerBoundaryIs = null;
        List<Boundary> innerBoundariesAre = new ArrayList<Boundary>();
        List<SimpleType> polygonSimpleExtensions = null;
        List<AbstractObject> polygonObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // POLYGON
                    if (URI_KML.equals(eUri)) {
                        if (TAG_EXTRUDE.equals(eName)) {
                            extrude = parseBoolean(reader.getElementText());
                        } else if (TAG_TESSELLATE.equals(eName)) {
                            tessellate = parseBoolean(reader.getElementText());
                        } else if (TAG_ALTITUDE_MODE.equals(eName)) {
                            altitudeMode = AltitudeMode.transform(reader.getElementText());
                        } else if (TAG_OUTER_BOUNDARY_IS.equals(eName)) {
                            outerBoundaryIs = this.readBoundary();
                        } else if (TAG_INNER_BOUNDARY_IS.equals(eName)) {
                            innerBoundariesAre.add(this.readBoundary());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_POLYGON.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createPolygon(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode, outerBoundaryIs, innerBoundariesAre,
                polygonSimpleExtensions, polygonObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private Boundary readBoundary() throws XMLStreamException{
        LinearRing linearRing = null;
        List<SimpleType> boundarySimpleExtensions = null;
        List<AbstractObject> boundaryObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // BOUNDARY
                    if (URI_KML.equals(eUri)) {
                        if (TAG_LINEAR_RING.equals(eName)) {
                            linearRing = this.readLinearRing();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if ((TAG_OUTER_BOUNDARY_IS.equals(reader.getLocalName()) || TAG_INNER_BOUNDARY_IS.equals(reader.getLocalName()))
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createBoundary(linearRing, boundarySimpleExtensions, boundaryObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Model readModel() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractGeometry
        List<SimpleType> abstractGeometrySimpleExtensions = null;
        List<AbstractObject> abstractGeometryObjectExtensions = null;

        // Model
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        Location location = null;
        Orientation orientation = null;
        Scale scale = null;
        Link link = null;
        ResourceMap resourceMap = null;
        List<SimpleType> modelSimpleExtensions = null;
        List<AbstractObject> modelObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // MODEL
                    if (URI_KML.equals(eUri)) {
                        if (TAG_ALTITUDE_MODE.equals(eName)) {
                            altitudeMode = AltitudeMode.transform(reader.getElementText());
                        } else if (TAG_LOCATION.equals(eName)) {
                            location = this.readLocation();
                        } else if (TAG_ORIENTATION.equals(eName)) {
                            orientation = this.readOrientation();
                        } else if (TAG_SCALE_BIG.equals(eName)) {
                            scale = readScale();
                        } else if (TAG_LINK.equals(eName)) {
                            link = this.readLink(eName);
                        } else if (TAG_RESOURCE_MAP.equals(eName)) {
                            resourceMap = readResourceMap();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_MODEL.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createModel(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                altitudeMode, location, orientation, scale, link, resourceMap,
                modelSimpleExtensions, modelObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private ResourceMap readResourceMap() throws XMLStreamException{
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // ResourceMap
        List<Alias> aliases = new ArrayList<Alias>();
        List<SimpleType> resourceMapSimpleExtensions = null;
        List<AbstractObject> resourceMapObjectExtensions = null;


        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // RESOURCE MAP
                    if (URI_KML.equals(eUri)) {
                        if (TAG_ALIAS.equals(eName)) {
                            aliases.add(this.readAlias());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_RESOURCE_MAP.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createResourceMap(objectSimpleExtensions, idAttributes,
                aliases, resourceMapSimpleExtensions, resourceMapObjectExtensions);

    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
     private Alias readAlias() throws XMLStreamException{
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // Alias
        String targetHref = null;
        String sourceHref = null;
        List<SimpleType> alaisSimpleExtensions = null;
        List<AbstractObject> aliasObjectExtensions = null;


        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // ALIAS
                    if (URI_KML.equals(eUri)) {
                        if (TAG_TARGET_HREF.equals(eName)) {
                            targetHref = reader.getElementText();
                        } else if (TAG_SOURCE_HREF.equals(eName)) {
                            sourceHref = reader.getElementText();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_ALIAS.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createAlias(objectSimpleExtensions, idAttributes,
                targetHref, sourceHref, alaisSimpleExtensions, aliasObjectExtensions);

    }

     /**
      * 
      * @return
      * @throws XMLStreamException
      */
    private Scale readScale() throws XMLStreamException{
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // Scale
        double x = DEF_X;
        double y = DEF_Y;
        double z = DEF_Z;
        List<SimpleType> scaleSimpleExtensions = null;
        List<AbstractObject> scaleObjectExtensions = null;


        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // SCALE
                    if (URI_KML.equals(eUri)) {
                        if (TAG_X.equals(eName)) {
                            x = parseDouble(reader.getElementText());
                        } else if (TAG_Y.equals(eName)) {
                            y = parseDouble(reader.getElementText());
                        } else if (TAG_Z.equals(eName)){
                            z = parseDouble(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_SCALE_BIG.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createScale(objectSimpleExtensions, idAttributes,
                x, y, z, scaleSimpleExtensions, scaleObjectExtensions);
        
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Location readLocation() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // Location
        double longitude = DEF_LONGITUDE;
        double latitude = DEF_LATITUDE;
        double altitude = DEF_ALTITUDE;
        List<SimpleType> locationSimpleExtensions = null;
        List<AbstractObject> locationObjectExtensions = null;


        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // LOCATION
                    if (URI_KML.equals(eUri)) {
                        if (TAG_LONGITUDE.equals(eName)) {
                            longitude = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        } else if (TAG_LATITUDE.equals(eName)) {
                            latitude = KmlUtilities.checkAngle90(parseDouble(reader.getElementText()));
                        } else if (TAG_ALTITUDE.equals(eName)){
                            altitude = parseDouble(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LOCATION.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createLocation(objectSimpleExtensions, idAttributes,
                longitude, latitude, altitude, locationSimpleExtensions, locationObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Orientation readOrientation() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // Orientation
        double heading = DEF_HEADING;
        double tilt = DEF_TILT;
        double roll = DEF_ROLL;
        List<SimpleType> orientationSimpleExtensions = null;
        List<AbstractObject> orientationObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // ORIENTATION
                    if (URI_KML.equals(eUri)) {
                        if (TAG_HEADING.equals(eName)) {
                            heading = KmlUtilities.checkAngle360(parseDouble(reader.getElementText()));
                        } else if (TAG_TILT.equals(eName)) {
                            tilt = KmlUtilities.checkAnglePos180(parseDouble(reader.getElementText()));
                        } else if (TAG_ROLL.equals(eName)) {
                            roll = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_ORIENTATION.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createOrientation(objectSimpleExtensions, idAttributes,
                heading, tilt, roll, orientationSimpleExtensions, orientationObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private LinearRing readLinearRing() throws XMLStreamException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractGeometry
        List<SimpleType> abstractGeometrySimpleExtensions = null;
        List<AbstractObject> abstractGeometryObjectExtensions = null;

        // LinearRing
        boolean extrude = DEF_EXTRUDE;
        boolean tessellate = DEF_TESSELLATE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        Coordinates coordinates = null;
        List<SimpleType> linearRingSimpleExtensions = null;
        List<AbstractObject> linearRingObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // LINEAR RING
                    if (URI_KML.equals(eUri)) {
                        if (TAG_EXTRUDE.equals(eName)) {
                            extrude = parseBoolean(reader.getElementText());
                        } else if (TAG_TESSELLATE.equals(eName)) {
                            tessellate = parseBoolean(reader.getElementText());
                        } else if (TAG_ALTITUDE_MODE.equals(eName)) {
                            altitudeMode = AltitudeMode.transform(reader.getElementText());
                        } else if (TAG_COORDINATES.equals(eName)) {
                            coordinates = readCoordinates(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LINEAR_RING.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createLinearRing(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode, coordinates,
                linearRingSimpleExtensions, linearRingObjectExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    private LineString readLineString() throws XMLStreamException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractGeometry
        List<SimpleType> abstractGeometrySimpleExtensions = null;
        List<AbstractObject> abstractGeometryObjectExtensions = null;

        // LineString
        boolean extrude = DEF_EXTRUDE;
        boolean tessellate = DEF_TESSELLATE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        Coordinates coordinates = null;
        List<SimpleType> lineStringSimpleExtensions = null;
        List<AbstractObject> lineStringObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // LINE STRING
                    if (URI_KML.equals(eUri)) {
                        if (TAG_EXTRUDE.equals(eName)) {
                            extrude = parseBoolean(reader.getElementText());
                        } else if (TAG_TESSELLATE.equals(eName)) {
                            tessellate = parseBoolean(reader.getElementText());
                        } else if (TAG_ALTITUDE_MODE.equals(eName)) {
                            altitudeMode = AltitudeMode.transform(reader.getElementText());
                        } else if (TAG_COORDINATES.equals(eName)) {
                            coordinates = readCoordinates(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LINE_STRING.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createLineString(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode, coordinates,
                lineStringSimpleExtensions, lineStringObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private MultiGeometry readMultiGeometry() throws XMLStreamException, KmlException{
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractGeometry
        List<SimpleType> abstractGeometrySimpleExtensions = null;
        List<AbstractObject> abstractGeometryObjectExtensions = null;

        // Multi Geometry
        List<AbstractGeometry> geometries = new ArrayList<AbstractGeometry>();
        List<SimpleType> multiGeometrySimpleExtensions = null;
        List<AbstractObject> multiGeometryObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // MULTI GEOMETRY
                    if (URI_KML.equals(eUri)) {
                        if (isAbstractGeometry(eName)) {
                            geometries.add(this.readAbstractGeometry(eName));
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_MULTI_GEOMETRY.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createMultiGeometry(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                geometries, multiGeometrySimpleExtensions, multiGeometryObjectExtensions);

    }

    /**
     *
     * @param eName The tag name.
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private AbstractFeature readAbstractFeature(String eName) throws XMLStreamException, KmlException {
        AbstractFeature resultat = null;
        if (isAbstractContainer(eName)) {
            resultat = this.readAbstractContainer(eName);
        } else if (isAbstractOverlay(eName)) {
            resultat = this.readAbstractOverlay(eName);
        } else if (TAG_NETWORK_LINK.equals(eName)) {
            resultat = readNetworkLink();
        } else if (isAbstractOverlay(eName)) {
            resultat = readAbstractOverlay(eName);
        } else if (TAG_PLACEMARK.equals(eName)) {
            resultat = readPlacemark();
        }
        return resultat;
    }

    /**
     *
     * @param eName The Tag name.
     * @return
     * @throws XMLStreamException
     */
    private AbstractOverlay readAbstractOverlay(String eName) throws XMLStreamException, KmlException {
        AbstractOverlay resultat = null;
        if (TAG_GROUND_OVERLAY.equals(eName)) {
            resultat = readGroundOverlay();
        }
        if (TAG_PHOTO_OVERLAY.equals(eName)) {
            resultat = readPhotoOverlay();
        }
        if (TAG_SCREEN_OVERLAY.equals(eName)) {
            resultat = readScreenOverlay();
        }
        return resultat;
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private GroundOverlay readGroundOverlay() throws XMLStreamException, KmlException {

        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractFeature
        String name = null;
        boolean visibility = DEF_VISIBILITY;
        boolean open = DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink link = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        String snippet = null;
        String description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        String styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<AbstractStyleSelector>();
        Region region = null;
        ExtendedData extendedData = null;
        List<SimpleType> featureSimpleExtensions = null;
        List<AbstractObject> featureObjectExtensions = null;

        // AbstractOverlay
        Color color = DEF_COLOR;
        int drawOrder = DEF_DRAW_ORDER;
        Icon icon = null;
        List<SimpleType> abstractOverlaySimpleExtensions = null;
        List<AbstractObject> abstractOverlayObjectExtensions = null;

        // GroundOverlay
        double altitude = DEF_ALTITUDE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        LatLonBox latLonBox = null;
        List<SimpleType> groundOverlaySimpleExtensions = null;
        List<AbstractObject> groundOverlayObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_NAME.equals(eName)) {
                            name = reader.getElementText();
                        } else if (TAG_VISIBILITY.equals(eName)) {
                            visibility = parseBoolean(reader.getElementText());
                        } else if (TAG_OPEN.equals(eName)) {
                            open = parseBoolean(reader.getElementText());
                        } else if (TAG_ADDRESS.equals(eName)) {
                            address = reader.getElementText();
                        } else if (TAG_PHONE_NUMBER.equals(eName)) {
                            phoneNumber = reader.getElementText();
                        } else if (TAG_SNIPPET.equals(eName)) {
                            snippet = reader.getElementText();
                        } else if (TAG_DESCRIPTION.equals(eName)) {
                            description = reader.getElementText();
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = reader.getElementText();
                        } else if (isAbstractView(eName)) {
                            view = this.readAbstractView(eName);
                        } else if (isAbstractTimePrimitive(eName)) {
                            timePrimitive = this.readAbstractTimePrimitive(eName);
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = reader.getElementText();
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector.add(this.readAbstractStyleSelector(eName));
                        } else if (TAG_REGION.equals(eName)) {
                            region = this.readRegion();
                        } else if (TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = this.readExtendedData();
                        }

                        // ABSTRACT OVERLAY
                        else if (TAG_COLOR.equals(eName)) {
                            color = KmlUtilities.parseColor(reader.getElementText());
                        } else if (TAG_DRAW_ORDER.equals(eName)) {
                            drawOrder = Integer.parseInt(reader.getElementText());
                        } else if (TAG_ICON.equals(eName)) {
                            icon = readIcon(eName);
                        }

                        // GROUND OVERLAY
                        else if (TAG_ALTITUDE.equals(eName)) {
                            altitude = parseDouble(reader.getElementText());
                        } else if (TAG_ALTITUDE_MODE.equals(eName)) {
                            altitudeMode = AltitudeMode.transform(reader.getElementText());
                        } else if (TAG_LAT_LON_BOX.equals(eName)) {
                            latLonBox = this.readLatLonBox();
                        }


                    } else if (URI_ATOM.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = this.readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = this.readAtomLink();
                        }
                    }
                    if (URI_XAL.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = this.readXalAddressDetails();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_GROUND_OVERLAY.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createGroundOverlay(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link,
                address, addressDetails, phoneNumber, snippet, description, view, timePrimitive,
                styleUrl, styleSelector, region, extendedData,
                featureSimpleExtensions, featureObjectExtensions,
                color, drawOrder, icon,
                abstractOverlaySimpleExtensions, abstractOverlayObjectExtensions,
                altitude, altitudeMode, latLonBox,
                groundOverlaySimpleExtensions, groundOverlayObjectExtensions);
    }

    /**
     * 
     * @param stopName
     * @return
     * @throws KmlException
     * @throws XMLStreamException
     */
    private Link readLink(String stopName) throws XMLStreamException {

        // Comme BasicLink
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        String href = null;
        List<SimpleType> basicLinkSimpleExtensions = null;
        List<AbstractObject> basicLinkObjectExtensions = null;

        // Spécifique à Link
        RefreshMode refreshMode = DEF_REFRESH_MODE;
        double refreshInterval = DEF_REFRESH_INTERVAL;
        ViewRefreshMode viewRefreshMode = DEF_VIEW_REFRESH_MODE;
        double viewRefreshTime = DEF_VIEW_REFRESH_TIME;
        double viewBoundScale = DEF_VIEW_BOUND_SCALE;
        String viewFormat = null;
        String httpQuery = null;
        List<SimpleType> linkSimpleExtensions = null;
        List<AbstractObject> linkObjectExtensions = null;
        
        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // COMME BASIC LINK
                        if (TAG_HREF.equals(eName)) {
                            href = reader.getElementText();
                        }

                        // SPECIFIQUE A LINK
                        if (TAG_REFRESH_MODE.equals(eName)) {
                            refreshMode = RefreshMode.transform(reader.getElementText());
                        } else if (TAG_REFRESH_INTERVAL.equals(eName)) {
                            refreshInterval = parseDouble(reader.getElementText());
                        } else if (TAG_VIEW_REFRESH_MODE.equals(eName)) {
                            viewRefreshMode = ViewRefreshMode.transform(reader.getElementText());
                        } else if (TAG_VIEW_REFRESH_TIME.equals(eName)) {
                            viewRefreshTime = parseDouble(reader.getElementText());
                        } else if (TAG_VIEW_BOUND_SCALE.equals(eName)) {
                            viewBoundScale = parseDouble(reader.getElementText());
                        } else if (TAG_VIEW_FORMAT.equals(eName)) {
                            viewFormat = reader.getElementText();
                        } else if (TAG_HTTP_QUERY.equals(eName)) {
                            httpQuery = reader.getElementText();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (stopName.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createLink(objectSimpleExtensions, idAttributes,
                href, basicLinkSimpleExtensions, basicLinkObjectExtensions,
                refreshMode, refreshInterval, viewRefreshMode, viewRefreshTime, viewBoundScale, viewFormat, httpQuery,
                linkSimpleExtensions, linkObjectExtensions);
    }


    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private Icon readIcon(String stopName) throws XMLStreamException{
        return KmlReader.kmlFactory.createIcon(this.readLink(stopName));
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private LatLonBox readLatLonBox() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractLatLonBox
        double north = DEF_NORTH;
        double south = DEF_SOUTH;
        double east = DEF_EAST;
        double west = DEF_WEST;
        List<SimpleType> abstractLatLonBoxSimpleExtensions = null;
        List<AbstractObject> abstractLatLonBoxObjectExtensions = null;

        // LatLonBox
        double rotation = DEF_ROTATION;
        List<SimpleType> latLonBoxSimpleExtensions = null;
        List<AbstractObject> latLonBoxObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // ABSTRACT LATLONBOX
                        if (TAG_NORTH.equals(eName)) {
                            north = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        } else if (TAG_SOUTH.equals(eName)) {
                            south = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        } else if (TAG_EAST.equals(eName)) {
                            east = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        } else if (TAG_WEST.equals(eName)) {
                            west = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        } 
                        
                        // LATLONBOX
                        else if (TAG_ROTATION.equals(eName)) {
                            rotation = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        }

                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LAT_LON_BOX.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return KmlReader.kmlFactory.createLatLonBox(objectSimpleExtensions, idAttributes,
                north, south, east, west,
                abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions,
                rotation, latLonBoxSimpleExtensions, latLonBoxObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private LatLonAltBox readLatLonAltBox() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractLatLonBox
        double north = DEF_NORTH;
        double south = DEF_SOUTH;
        double east = DEF_EAST;
        double west = DEF_WEST;
        List<SimpleType> abstractLatLonBoxSimpleExtensions = null;
        List<AbstractObject> abstractLatLonBoxObjectExtensions = null;

        // LatLonAltBox
        double minAltitude = DEF_MIN_ALTITUDE;
        double maxAltitude = DEF_MAX_ALTITUDE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        List<SimpleType> latLonAltBoxSimpleExtensions = null;
        List<AbstractObject> latLonAltBoxObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // ABSTRACT LATLONBOX
                        if (TAG_NORTH.equals(eName)) {
                            north = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        } else if (TAG_SOUTH.equals(eName)) {
                            south = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        } else if (TAG_EAST.equals(eName)) {
                            east = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        } else if (TAG_WEST.equals(eName)) {
                            west = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        }

                        // LATLONALTBOX
                        else if (TAG_MIN_ALTITUDE.equals(eName)) {
                            minAltitude = parseDouble(reader.getElementText());
                        } else if (TAG_MAX_ALTITUDE.equals(eName)) {
                            maxAltitude = parseDouble(reader.getElementText());
                        } else if (TAG_MIN_ALTITUDE.equals(eName)) {
                            altitudeMode = AltitudeMode.transform(reader.getElementText());
                        }

                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LAT_LON_ALT_BOX.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return KmlReader.kmlFactory.createLatLonAltBox(objectSimpleExtensions, idAttributes,
                north, south, east, west, abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions,
                minAltitude, maxAltitude, altitudeMode, latLonAltBoxSimpleExtensions, latLonAltBoxObjectExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private ImagePyramid readImagePyramid() throws XMLStreamException, KmlException {

        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // ImagePyramid
        int titleSize = DEF_TITLE_SIZE;
        int maxWidth = DEF_MAX_WIDTH;
        int maxHeight = DEF_MAX_HEIGHT;
        GridOrigin gridOrigin = DEF_GRID_ORIGIN;
        List<SimpleType> imagePyramidSimpleExtensions = null;
        List<AbstractObject> imagePyramidObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // IMAGE PYRAMID
                        if (TAG_TITLE_SIZE.equals(eName)) {
                            titleSize = Integer.parseInt(reader.getElementText());
                        } else if (TAG_MAX_WIDTH.equals(eName)) {
                            maxWidth = Integer.parseInt(reader.getElementText());
                        } else if (TAG_MAX_HEIGHT.equals(eName)) {
                            maxHeight = Integer.parseInt(reader.getElementText());
                        } else if (TAG_GRID_ORIGIN.equals(eName)) {
                            gridOrigin = GridOrigin.transform(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_IMAGE_PYRAMID.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createImagePyramid(objectSimpleExtensions, idAttributes,
                titleSize, maxWidth, maxHeight, gridOrigin,
                imagePyramidSimpleExtensions, imagePyramidObjectExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private ViewVolume readViewVolume() throws XMLStreamException, KmlException {

        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // ViewVolume
        double leftFov = DEF_LEFT_FOV;
        double rightFov = DEF_RIGHT_FOV;
        double bottomFov = DEF_BOTTOM_FOV;
        double topFov = DEF_TOP_FOV;
        double near = DEF_NEAR;
        List<SimpleType> viewVolumeSimpleExtensions = null;
        List<AbstractObject> viewVolumeObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // VIEW VOLUME
                        if (TAG_LEFT_FOV.equals(eName)) {
                            leftFov = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        } else if (TAG_RIGHT_FOV.equals(eName)) {
                            rightFov = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        } else if (TAG_BOTTOM_FOV.equals(eName)) {
                            bottomFov =  KmlUtilities.checkAngle90(parseDouble(reader.getElementText()));
                        } else if (TAG_TOP_FOV.equals(eName)) {
                            topFov =  KmlUtilities.checkAngle90(parseDouble(reader.getElementText()));
                        } else if (TAG_NEAR.equals(eName)) {
                            near = parseDouble(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_VIEW_VOLUME.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createViewVolume(objectSimpleExtensions, idAttributes,
                leftFov, rightFov, bottomFov, topFov, near, viewVolumeSimpleExtensions, viewVolumeObjectExtensions);
    }

    private PhotoOverlay readPhotoOverlay() throws XMLStreamException, KmlException {

        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractFeature
        String name = null;
        boolean visibility = DEF_VISIBILITY;
        boolean open = DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink link = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        String snippet = null;
        String description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        String styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<AbstractStyleSelector>();
        Region region = null;
        ExtendedData extendedData = null;
        List<SimpleType> featureSimpleExtensions = null;
        List<AbstractObject> featureObjectExtensions = null;

        // AbstractOverlay
        Color color = DEF_COLOR;
        int drawOrder = DEF_DRAW_ORDER;
        Icon icon = null;
        List<SimpleType> abstractOverlaySimpleExtensions = null;
        List<AbstractObject> abstractOverlayObjectExtensions = null;

        // PhotoOverlay
        double rotation = DEF_ROTATION;
        ViewVolume viewVolume = null;
        ImagePyramid imagePyramid = null;
        Point point = null;
        Shape shape = null;
        List<SimpleType> photoOverlaySimpleExtensions = null;
        List<AbstractObject> photoOverlayObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_NAME.equals(eName)) {
                            name = reader.getElementText();
                        } else if (TAG_VISIBILITY.equals(eName)) {
                            visibility = parseBoolean(reader.getElementText());
                        } else if (TAG_OPEN.equals(eName)) {
                            open = parseBoolean(reader.getElementText());
                        } else if (TAG_ADDRESS.equals(eName)) {
                            address = reader.getElementText();
                        } else if (TAG_PHONE_NUMBER.equals(eName)) {
                            phoneNumber = reader.getElementText();
                        } else if (TAG_SNIPPET.equals(eName)) {
                            snippet = reader.getElementText();
                        } else if (TAG_DESCRIPTION.equals(eName)) {
                            description = reader.getElementText();
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = reader.getElementText();
                        } else if (isAbstractView(eName)) {
                            view = this.readAbstractView(eName);
                        } else if (isAbstractTimePrimitive(eName)) {
                            timePrimitive = this.readAbstractTimePrimitive(eName);
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = reader.getElementText();
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector.add(this.readAbstractStyleSelector(eName));
                        } else if (TAG_REGION.equals(eName)) {
                            region = this.readRegion();
                        } else if (TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = this.readExtendedData();
                        }

                        // ABSTRACT OVERLAY
                        else if (TAG_COLOR.equals(eName)) {
                            color = KmlUtilities.parseColor(reader.getElementText());
                        } else if (TAG_DRAW_ORDER.equals(eName)) {
                            drawOrder = Integer.parseInt(reader.getElementText());
                        } else if (TAG_ICON.equals(eName)) {
                            icon = readIcon(eName);
                        }

                        // PHOTO OVERLAY
                        else if (TAG_ROTATION.equals(eName)) {
                            rotation = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        } else if (TAG_VIEW_VOLUME.equals(eName)) {
                            viewVolume = this.readViewVolume();
                        } else if (TAG_IMAGE_PYRAMID.equals(eName)) {
                            imagePyramid = this.readImagePyramid();
                        } else if (TAG_POINT.equals(eName)) {
                            point = this.readPoint();
                        } else if (TAG_SHAPE.equals(eName)) {
                            shape = Shape.transform(reader.getElementText());
                        }


                    } else if (URI_ATOM.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = this.readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = this.readAtomLink();
                        }
                    }
                    if (URI_XAL.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = this.readXalAddressDetails();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_PHOTO_OVERLAY.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createPhotoOverlay(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber,
                snippet, description, view, timePrimitive, styleUrl, styleSelector, region, extendedData,
                abstractOverlaySimpleExtensions, abstractOverlayObjectExtensions,
                color, drawOrder, icon, abstractOverlaySimpleExtensions, abstractOverlayObjectExtensions,
                rotation, viewVolume, imagePyramid, point, shape,
                photoOverlaySimpleExtensions, photoOverlayObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private ScreenOverlay readScreenOverlay() throws XMLStreamException, KmlException {

        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractFeature
        String name = null;
        boolean visibility = DEF_VISIBILITY;
        boolean open = DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink link = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        String snippet = null;
        String description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        String styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<AbstractStyleSelector>();
        Region region = null;
        ExtendedData extendedData = null;
        List<SimpleType> featureSimpleExtensions = null;
        List<AbstractObject> featureObjectExtensions = null;

        // AbstractOverlay
        Color color = DEF_COLOR;
        int drawOrder = DEF_DRAW_ORDER;
        Icon icon = null;
        List<SimpleType> abstractOverlaySimpleExtensions = null;
        List<AbstractObject> abstractOverlayObjectExtensions = null;

        // ScreenOverlay
        Vec2 overlayXY = null;
        Vec2 screenXY = null;
        Vec2 rotationXY = null;
        Vec2 size = null;
        double rotation = DEF_ROTATION;
        List<SimpleType> screenOverlaySimpleExtensions = null;
        List<AbstractObject> screenOverlayObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_NAME.equals(eName)) {
                            name = reader.getElementText();
                        } else if (TAG_VISIBILITY.equals(eName)) {
                            visibility = parseBoolean(reader.getElementText());
                        } else if (TAG_OPEN.equals(eName)) {
                            open = parseBoolean(reader.getElementText());
                        } else if (TAG_ADDRESS.equals(eName)) {
                            address = reader.getElementText();
                        } else if (TAG_PHONE_NUMBER.equals(eName)) {
                            phoneNumber = reader.getElementText();
                        } else if (TAG_SNIPPET.equals(eName)) {
                            snippet = reader.getElementText();
                        } else if (TAG_DESCRIPTION.equals(eName)) {
                            description = reader.getElementText();
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = reader.getElementText();
                        } else if (isAbstractView(eName)) {
                            view = this.readAbstractView(eName);
                        } else if (isAbstractTimePrimitive(eName)) {
                            timePrimitive = this.readAbstractTimePrimitive(eName);
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = reader.getElementText();
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector.add(this.readAbstractStyleSelector(eName));
                        } else if (TAG_REGION.equals(eName)) {
                            region = this.readRegion();
                        } else if (TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = this.readExtendedData();
                        }

                        // ABSTRACT OVERLAY
                        else if (TAG_COLOR.equals(eName)) {
                            color = KmlUtilities.parseColor(reader.getElementText());
                        } else if (TAG_DRAW_ORDER.equals(eName)) {
                            drawOrder = Integer.parseInt(reader.getElementText());
                        } else if (TAG_ICON.equals(eName)) {
                            icon = readIcon(eName);
                        }

                        // SCREEN OVERLAY
                        else if (TAG_OVERLAY_XY.equals(eName)) {
                            overlayXY = this.readVec2(eName);
                        } else if (TAG_SCREEN_XY.equals(eName)) {
                            screenXY = this.readVec2(eName);
                        } else if (TAG_ROTATION_XY.equals(eName)) {
                            rotationXY = this.readVec2(eName);
                        } else if (TAG_SIZE.equals(eName)) {
                            size = this.readVec2(eName);
                        } else if (TAG_ROTATION.equals(eName)) {
                            rotation = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        }


                    } else if (URI_ATOM.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = this.readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = this.readAtomLink();
                        }
                    }
                    if (URI_XAL.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = this.readXalAddressDetails();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_SCREEN_OVERLAY.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createScreenOverlay(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive, styleUrl, styleSelector,
                region, extendedData, featureSimpleExtensions, featureObjectExtensions,
                color, drawOrder, icon, abstractOverlaySimpleExtensions, abstractOverlayObjectExtensions,
                overlayXY, screenXY, rotationXY, size, rotation,
                screenOverlaySimpleExtensions, screenOverlayObjectExtensions);
    }

    /**
     *
     * @param eName The tag name.
     * @return
     * @throws XMLStreamException
     */
    private AbstractContainer readAbstractContainer(String eName) throws XMLStreamException, KmlException {
        AbstractContainer resultat = null;
        if (TAG_FOLDER.equals(eName)) {
            resultat = readFolder();
        } else if (TAG_DOCUMENT.equals(eName)) {
            resultat = readDocument();
        }
        return resultat;
    }

    /**
     *
     * @param eName The tag name.
     * @return
     * @throws XMLStreamException
     */
    private AbstractView readAbstractView(String eName) throws XMLStreamException, KmlException {
        AbstractView resultat = null;
        if (TAG_LOOK_AT.equals(eName)) {
            resultat = readLookAt();
        } else if (TAG_CAMERA.equals(eName)) {
            resultat = readCamera();
        }
        return resultat;
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private LookAt readLookAt() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractView
        List<SimpleType> abstractViewSimpleExtensions = null;
        List<AbstractObject> abstractViewObjectExtensions = null;

        // LookAt
        double longitude = DEF_LONGITUDE;
        double latitude = DEF_LATITUDE;
        double altitude = DEF_ALTITUDE;
        double heading = DEF_HEADING;
        double tilt = DEF_TILT;
        double range = DEF_RANGE;
        List<SimpleType> lookAtSimpleExtensions = null;
        List<AbstractObject> lookAtObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // LOOK AT
                        if (TAG_LONGITUDE.equals(eName)) {
                            longitude = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        } else if (TAG_LATITUDE.equals(eName)) {
                            latitude =  KmlUtilities.checkAngle90(parseDouble(reader.getElementText()));
                        } else if (TAG_ALTITUDE.equals(eName)) {
                            altitude = parseDouble(reader.getElementText());
                        } else if (TAG_HEADING.equals(eName)) {
                            heading = KmlUtilities.checkAngle360(parseDouble(reader.getElementText()));
                        } else if (TAG_TILT.equals(eName)) {
                            tilt = KmlUtilities.checkAnglePos180(parseDouble(reader.getElementText()));
                        } else if (TAG_RANGE.equals(eName)) {
                            range = parseDouble(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LOOK_AT.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return KmlReader.kmlFactory.createLookAt(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, range,
                lookAtSimpleExtensions, lookAtObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Camera readCamera() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractView
        List<SimpleType> abstractViewSimpleExtensions = null;
        List<AbstractObject> abstractViewObjectExtensions = null;

        // Camera
        double longitude = DEF_LONGITUDE;
        double latitude = DEF_LATITUDE;
        double altitude = DEF_ALTITUDE;
        double heading = DEF_HEADING;
        double tilt = DEF_TILT;
        double roll = DEF_ROLL;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        List<SimpleType> cameraSimpleExtensions = null;
        List<AbstractObject> cameraObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // CAMERA
                        if (TAG_LONGITUDE.equals(eName)) {
                            longitude = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        } else if (TAG_LATITUDE.equals(eName)) {
                            latitude =  KmlUtilities.checkAngle90(parseDouble(reader.getElementText()));
                        } else if (TAG_ALTITUDE.equals(eName)) {
                            altitude = parseDouble(reader.getElementText());
                        } else if (TAG_HEADING.equals(eName)) {
                            heading = KmlUtilities.checkAngle360(parseDouble(reader.getElementText()));
                        } else if (TAG_TILT.equals(eName)) {
                            tilt = KmlUtilities.checkAnglePos180(parseDouble(reader.getElementText()));
                        } else if (TAG_ROLL.equals(eName)) {
                            roll = KmlUtilities.checkAngle180(parseDouble(reader.getElementText()));
                        } else if (TAG_ALTITUDE_MODE.equals(eName)) {
                            altitudeMode = AltitudeMode.transform(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_CAMERA.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return KmlReader.kmlFactory.createCamera(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, roll, altitudeMode,
                cameraSimpleExtensions, cameraObjectExtensions);
    }

    /**
     *
     * @param eName The tag name
     * @return
     * @throws XMLStreamException
     */
    private AbstractStyleSelector readAbstractStyleSelector(String eName) throws XMLStreamException, KmlException {
        AbstractStyleSelector resultat = null;
        if (TAG_STYLE.equals(eName)) {
            resultat = readStyle();
        } else if (TAG_STYLE_MAP.equals(eName)) {
            resultat = readStyleMap();
        }
        return resultat;
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private StyleMap readStyleMap() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractStyleSelector
        List<SimpleType> styleSelectorSimpleExtensions = null;
        List<AbstractObject> styleSelectorObjectExtensions = null;

        // StyleMap
        List<Pair> pairs = new ArrayList<Pair>();
        List<SimpleType> styleMapSimpleExtensions = null;
        List<AbstractObject> styleMapObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // STYLE MAP
                        if (TAG_PAIR.equals(eName)) {
                            pairs.add(this.readPair());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_STYLE_MAP.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return KmlReader.kmlFactory.createStyleMap(objectSimpleExtensions, idAttributes,
                styleSelectorSimpleExtensions, styleSelectorObjectExtensions,
                pairs, styleMapSimpleExtensions, styleMapObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Pair readPair() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // Pair
        StyleState key = DEF_STYLE_STATE;
        String styleUrl = null;
        AbstractStyleSelector styleSelector = null;
        List<SimpleType> pairSimpleExtensions = null;
        List<AbstractObject> pairObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // PAIR
                        if (TAG_KEY.equals(eName)) {
                            key = StyleState.transform(reader.getElementText());
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = reader.getElementText();
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector = this.readAbstractStyleSelector(eName);
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_PAIR.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return KmlReader.kmlFactory.createPair(objectSimpleExtensions, idAttributes,
                key, styleUrl, styleSelector, pairSimpleExtensions, pairObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Style readStyle() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractStyleSelector
        List<SimpleType> styleSelectorSimpleExtensions = null;
        List<AbstractObject> styleSelectorObjectExtensions = null;

        // Style
        IconStyle iconStyle = null;
        LabelStyle labelStyle = null;
        LineStyle lineStyle = null;
        PolyStyle polyStyle = null;
        BalloonStyle balloonStyle = null;
        ListStyle listStyle = null;
        List<SimpleType> styleSimpleExtensions = null;
        List<AbstractObject> styleObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // STYLE
                        if (TAG_ICON_STYLE.equals(eName)) {
                            iconStyle = this.readIconStyle();
                        } else if (TAG_LABEL_STYLE.equals(eName)) {
                            labelStyle = this.readLabelStyle();
                        } else if (TAG_LINE_STYLE.equals(eName)) {
                            lineStyle = this.readLineStyle();
                        } else if (TAG_POLY_STYLE.equals(eName)) {
                            polyStyle = this.readPolyStyle();
                        } else if (TAG_BALLOON_STYLE.equals(eName)) {
                            balloonStyle = this.readBalloonStyle();
                        } else if (TAG_LIST_STYLE.equals(eName)) {
                            listStyle = this.readListStyle();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_STYLE.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return KmlReader.kmlFactory.createStyle(objectSimpleExtensions, idAttributes,
                styleSelectorSimpleExtensions, styleSelectorObjectExtensions,
                iconStyle, labelStyle, lineStyle, polyStyle, balloonStyle, listStyle,
                styleSimpleExtensions, styleObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private IconStyle readIconStyle() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractSubStyle
        List<SimpleType> subStyleSimpleExtensions = null;
        List<AbstractObject> subStyleObjectExtensions = null;

        // AbstractColorStyle
        Color color = DEF_COLOR;
        ColorMode colorMode = DEF_COLOR_MODE;
        List<SimpleType> colorStyleSimpleExtensions = null;
        List<AbstractObject> colorStyleObjectExtensions = null;

        // IconStyle
        double scale = DEF_SCALE;
        double heading = DEF_HEADING;
        BasicLink icon = null;
        Vec2 hotSpot = null;
        List<SimpleType> iconStyleSimpleExtensions = null;
        List<AbstractObject> iconStyleObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // COLOR STYLE
                        if (TAG_COLOR.equals(eName)) {
                            color = KmlUtilities.parseColor(reader.getElementText());
                        } else if (TAG_COLOR_MODE.equals(eName)) {
                            colorMode = ColorMode.transform(reader.getElementText());
                        }

                        // ICON STYLE
                        else if (TAG_SCALE.equals(eName)) {
                            scale = parseDouble(reader.getElementText());
                        } else if (TAG_HEADING.equals(eName)) {
                            heading = KmlUtilities.checkAngle360(parseDouble(reader.getElementText()));
                        } else if (TAG_ICON.equals(eName)) {
                            icon = this.readBasicLink(TAG_ICON);
                        } else if (TAG_HOT_SPOT.equals(eName)) {
                            hotSpot = this.readVec2(TAG_HOT_SPOT);
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_ICON_STYLE.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createIconStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode,
                colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, heading, icon, hotSpot,
                iconStyleSimpleExtensions, iconStyleObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private LabelStyle readLabelStyle() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractSubStyle
        List<SimpleType> subStyleSimpleExtensions = null;
        List<AbstractObject> subStyleObjectExtensions = null;

        // AbstractColorStyle
        Color color = DEF_COLOR;
        ColorMode colorMode = DEF_COLOR_MODE;
        List<SimpleType> colorStyleSimpleExtensions = null;
        List<AbstractObject> colorStyleObjectExtensions = null;

        // LabelStyle
        double scale = DEF_SCALE;
        List<SimpleType> labelStyleSimpleExtensions = null;
        List<AbstractObject> labelStyleObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // COLOR STYLE
                        if (TAG_COLOR.equals(eName)) {
                            color = KmlUtilities.parseColor(reader.getElementText());
                        } else if (TAG_COLOR_MODE.equals(eName)) {
                            colorMode = ColorMode.transform(reader.getElementText());
                        }

                        // LABEL STYLE
                        else if (TAG_SCALE.equals(eName)) {
                            scale = parseDouble(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LABEL_STYLE.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createLabelStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, labelStyleSimpleExtensions, labelStyleObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private LineStyle readLineStyle() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractSubStyle
        List<SimpleType> subStyleSimpleExtensions = null;
        List<AbstractObject> subStyleObjectExtensions = null;

        // AbstractColorStyle
        Color color = DEF_COLOR;
        ColorMode colorMode = DEF_COLOR_MODE;
        List<SimpleType> colorStyleSimpleExtensions = null;
        List<AbstractObject> colorStyleObjectExtensions = null;

        // LineStyle
        double width = DEF_WIDTH;
        List<SimpleType> lineStyleSimpleExtensions = null;
        List<AbstractObject> lineStyleObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // COLOR STYLE
                        if (TAG_COLOR.equals(eName)) {
                            color = KmlUtilities.parseColor(reader.getElementText());
                        } else if (TAG_COLOR_MODE.equals(eName)) {
                            colorMode = ColorMode.transform(reader.getElementText());
                        }

                        // LINE STYLE
                        else if (TAG_WIDTH.equals(eName)) {
                            width = parseDouble(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LINE_STYLE.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createLineStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                width, lineStyleSimpleExtensions, lineStyleObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private PolyStyle readPolyStyle() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractSubStyle
        List<SimpleType> subStyleSimpleExtensions = null;
        List<AbstractObject> subStyleObjectExtensions = null;

        // AbstractColorStyle
        Color color = DEF_COLOR;
        ColorMode colorMode = DEF_COLOR_MODE;
        List<SimpleType> colorStyleSimpleExtensions = null;
        List<AbstractObject> colorStyleObjectExtensions = null;

        // PolyStyle
        boolean fill = DEF_FILL;
        boolean outline = DEF_OUTLINE;
        List<SimpleType> polyStyleSimpleExtensions = null;
        List<AbstractObject> polyStyleObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // COLOR STYLE
                        if (TAG_COLOR.equals(eName)) {
                            color = KmlUtilities.parseColor(reader.getElementText());
                        } else if (TAG_COLOR_MODE.equals(eName)) {
                            colorMode = ColorMode.transform(reader.getElementText());
                        }

                        // POLY STYLE
                        else if (TAG_FILL.equals(eName)) {
                            fill = parseBoolean(reader.getElementText());
                        } else if (TAG_OUTLINE.equals(eName)) {
                            outline = parseBoolean(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_POLY_STYLE.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createPolyStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                fill, outline, polyStyleSimpleExtensions, polyStyleObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private BalloonStyle readBalloonStyle() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractSubStyle
        List<SimpleType> subStyleSimpleExtensions = null;
        List<AbstractObject> subStyleObjectExtensions = null;

        // BalloonStyle
        Color bgColor = DEF_BG_COLOR;
        Color textColor = DEF_TEXT_COLOR;
        String text = null;
        DisplayMode displayMode = DEF_DISPLAY_MODE;
        List<SimpleType> balloonStyleSimpleExtensions = null;
        List<AbstractObject> balloonStyleObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // BALLOON STYLE
                        if (TAG_BG_COLOR.equals(eName)) {
                            bgColor = KmlUtilities.parseColor(reader.getElementText());
                        } else if (TAG_TEXT_COLOR.equals(eName)) {
                            textColor = KmlUtilities.parseColor(reader.getElementText());
                        } else if (TAG_TEXT.equals(eName)) {
                            text = this.readElementText();
                        } else if (TAG_DISPLAY_MODE.equals(eName)) {
                            displayMode = DisplayMode.transform(reader.getElementText());
                        }

                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_BALLOON_STYLE.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createBalloonStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                bgColor, textColor, text, displayMode,
                balloonStyleSimpleExtensions, balloonStyleObjectExtensions);
    }

    /**
     * <p>This method is a try to separate CDATA and text content.</p>
     *
     * @return
     * @throws XMLStreamException
     */
    private String readElementText() throws XMLStreamException{
        String resultat = null;
        boucle:
        while (reader.hasNext()) {
            switch (reader.getEventType()) {
                case XMLStreamConstants.CDATA:
                    System.out.println("CDATA");
                    resultat = reader.getText();
                    break;
                case XMLStreamConstants.CHARACTERS:
                    System.out.println("CHARACTERS");
                    resultat = reader.getText();
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    System.out.println("END");
                    break boucle;
            }
            reader.next();
        }

        return resultat;
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private ListStyle readListStyle() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractSubStyle
        List<SimpleType> subStyleSimpleExtensions = null;
        List<AbstractObject> subStyleObjectExtensions = null;

        // ListStyle
        ListItem listItem = DEF_LIST_ITEM;
        Color bgColor = DEF_BG_COLOR;
        List<ItemIcon> itemIcons = new ArrayList<ItemIcon>();
        int maxSnippetLines = DEF_MAX_SNIPPET_LINES;
        List<SimpleType> listStyleSimpleExtensions = null;
        List<AbstractObject> listStyleObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // LIST STYLE
                        if (TAG_LIST_ITEM.equals(eName)) {
                            listItem = listItem.transform(reader.getElementText());
                        } else if (TAG_BG_COLOR.equals(eName)) {
                            bgColor = KmlUtilities.parseColor(reader.getElementText());
                        } else if (TAG_ITEM_ICON.equals(eName)) {
                            itemIcons.add(this.readItemIcon());
                        } else if (TAG_MAX_SNIPPET_LINES.equals(eName)) {
                            maxSnippetLines = Integer.parseInt(reader.getElementText());
                        }

                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LIST_STYLE.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createListStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                listItem, bgColor, itemIcons, maxSnippetLines,
                listStyleSimpleExtensions, listStyleObjectExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    private ItemIcon readItemIcon() throws XMLStreamException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // ListStyle
        List<ItemIconState> states = null;
        String href = null;
        List<SimpleType> itemIconSimpleExtensions = null;
        List<AbstractObject> itemIconObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // ITEM ICON
                        if (TAG_STATE.equals(eName)) {
                            states = this.readStates();
                        } else if (TAG_HREF.equals(eName)) {
                            href = reader.getElementText();
                        }

                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_ITEM_ICON.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createItemIcon(objectSimpleExtensions, idAttributes,
                states, href, itemIconSimpleExtensions, itemIconObjectExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    private List<ItemIconState> readStates() throws XMLStreamException {

        List<ItemIconState> states = new ArrayList<ItemIconState>();
        for (String iiss : reader.getElementText().split(" ")) {
            ItemIconState iis = ItemIconState.transform(iiss);
            if (iis != null) {
                states.add(iis);
            }
        }
        return states;
    }

    /**
     * 
     * @param stopTag
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private BasicLink readBasicLink(String stopTag) throws XMLStreamException, KmlException {

        if (stopTag == null) {
            throw new KmlException("The stop tag cannot be null. "
                    + "It's probably an <Icon> tag according to KML 2.2 specification.");
        }

        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        String href = null;
        List<SimpleType> basicLinkSimpleExtensions = null;
        List<AbstractObject> basicLinkObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {
                        if (TAG_HREF.equals(eName)) {
                            href = reader.getElementText();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (stopTag.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createBasicLink(objectSimpleExtensions, idAttributes,
                href, basicLinkSimpleExtensions, basicLinkObjectExtensions);
    }

    /**
     * 
     * @param stopTag
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Vec2 readVec2(String stopTag) throws XMLStreamException, KmlException {

        if (stopTag == null) {
            throw new KmlException("The stop tag cannot be null. "
                    + "It's propably <hotSpot>, <rotationXY>, <size>, <overlayXY> or <screenXY> according to KML 2.2 specification.");
        }

        double x = DEF_VEC2_X;
        String sx = reader.getAttributeValue(null, ATT_X);
        if (sx != null) {
            x = parseDouble(sx);
        }

        double y = DEF_VEC2_Y;
        String sy = reader.getAttributeValue(null, ATT_Y);
        if (sy != null) {
            y = parseDouble(sy);
        }

        Units xUnit = Units.transform(reader.getAttributeValue(null, ATT_XUNITS), DEF_VEC2_XUNIT);
        Units yUnit = Units.transform(reader.getAttributeValue(null, ATT_YUNITS), DEF_VEC2_YUNIT);

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.END_ELEMENT:
                    if (stopTag.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createVec2(x, y, xUnit, yUnit);
    }

    /**
     *
     * @param eName The tag name
     * @return
     * @throws XMLStreamException
     */
    private AbstractTimePrimitive readAbstractTimePrimitive(String eName) throws XMLStreamException {
        AbstractTimePrimitive resultat = null;
        if (TAG_TIME_STAMP.equals(eName)) {
            resultat = readTimeStamp();
        } else if (TAG_TIME_SPAN.equals(eName)) {
            resultat = readTimeSpan();
        }
        return resultat;
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private TimeSpan readTimeSpan() throws XMLStreamException{
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractTimePrimitive
        List<SimpleType> AbstractTimePrimitiveSimpleExtensions = null;
        List<AbstractObject> AbstractTimePrimitiveObjectExtensions = null;

        // TimeSpan
        Calendar begin = null;
        Calendar end = null;
        List<SimpleType> TimeSpanSimpleExtensions = null;
        List<AbstractObject> TimeSpanObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {
                        if (TAG_BEGIN.equals(eName)) {
                            begin = fastDateParser.getCalendar(reader.getElementText());
                        } else if (TAG_END.equals(eName)) {
                            end = fastDateParser.getCalendar(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_TIME_SPAN.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return KmlReader.kmlFactory.createTimeSpan(objectSimpleExtensions, idAttributes,
                AbstractTimePrimitiveSimpleExtensions, AbstractTimePrimitiveObjectExtensions,
                begin, end, TimeSpanSimpleExtensions, TimeSpanObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private TimeStamp readTimeStamp() throws XMLStreamException{
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractTimePrimitive
        List<SimpleType> AbstractTimePrimitiveSimpleExtensions = null;
        List<AbstractObject> AbstractTimePrimitiveObjectExtensions = null;

        // TimeStamp
        Calendar when = null;
        List<SimpleType> TimeStampSimpleExtensions = null;
        List<AbstractObject> TimeStampObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {
                        if (TAG_WHEN.equals(eName)) {
                            when = fastDateParser.getCalendar(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_TIME_STAMP.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return KmlReader.kmlFactory.createTimeStamp(objectSimpleExtensions, idAttributes,
                AbstractTimePrimitiveSimpleExtensions, AbstractTimePrimitiveObjectExtensions,
                when, TimeStampSimpleExtensions, TimeStampObjectExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    private AtomPersonConstruct readAtomPersonConstruct() throws XMLStreamException {
        return this.atomReader.readAuthor();
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private AtomLink readAtomLink() throws XMLStreamException {
        return this.atomReader.readLink();
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private AddressDetails readXalAddressDetails() throws XMLStreamException {
        AddressDetails resultat = null;
        try {
            resultat = this.xalReader.readAddressDetails();
        } catch (XalException ex) {
            Logger.getLogger(KmlReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultat;
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Folder readFolder() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();
        
        // AbstractFeature
        String name = null;
        boolean visibility = DEF_VISIBILITY;
        boolean open = DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink link = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        String snippet = null;
        String description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        String styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<AbstractStyleSelector>();
        Region region = null;
        ExtendedData extendedData = null;
        List<SimpleType> featureSimpleExtensions = null;
        List<AbstractObject> featureObjectExtensions = null;

        // Container
        List<SimpleType> abstractContainerSimpleExtensions = null;
        List<AbstractObject> abstractContainerObjectExtensions = null;

        // Folder
        List<AbstractFeature> features = new ArrayList<AbstractFeature>();
        List<SimpleType> folderSimpleExtensions = null;
        List<AbstractObject> folderObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_NAME.equals(eName)) {
                            name = reader.getElementText();
                        } else if (TAG_VISIBILITY.equals(eName)) {
                            visibility = parseBoolean(reader.getElementText());
                        } else if (TAG_OPEN.equals(eName)) {
                            open = parseBoolean(reader.getElementText());
                        } else if (TAG_ADDRESS.equals(eName)) {
                            address = reader.getElementText();
                        } else if (TAG_PHONE_NUMBER.equals(eName)) {
                            phoneNumber = reader.getElementText();
                        } else if (TAG_SNIPPET.equals(eName)) {
                            snippet = reader.getElementText();
                        } else if (TAG_DESCRIPTION.equals(eName)) {
                            description = reader.getElementText();
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = reader.getElementText();
                        } else if (isAbstractView(eName)) {
                            view = this.readAbstractView(eName);
                        } else if (isAbstractTimePrimitive(eName)) {
                            timePrimitive = this.readAbstractTimePrimitive(eName);
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = reader.getElementText();
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector.add(this.readAbstractStyleSelector(eName));
                        } else if (TAG_REGION.equals(eName)) {
                            region = this.readRegion();
                        } else if (TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = this.readExtendedData();
                        }

                        // FOLDER
                        else if (isAbstractFeature(eName)) {
                            features.add(this.readAbstractFeature(eName));
                        }

                    } else if (URI_ATOM.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = this.readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = this.readAtomLink();
                        }
                    }
                    if (URI_XAL.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = this.readXalAddressDetails();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_FOLDER.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return KmlReader.kmlFactory.createFolder(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive, styleUrl, styleSelector, region, extendedData,
                featureSimpleExtensions, featureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions,
                features, folderSimpleExtensions, folderObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Document readDocument() throws XMLStreamException, KmlException {

        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractFeature
        String name = null;
        boolean visibility = DEF_VISIBILITY;
        boolean open = DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink link = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        String snippet = null;
        String description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        String styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<AbstractStyleSelector>();
        Region region = null;
        ExtendedData extendedData = null;
        List<SimpleType> featureSimpleExtensions = null;
        List<AbstractObject> featureObjectExtensions = null;

        // Container
        List<SimpleType> abstractContainerSimpleExtensions = null;
        List<AbstractObject> abstractContainerObjectExtensions = null;

        // Document
        List<Schema> schemas = new ArrayList<Schema>();
        List<AbstractFeature> features = new ArrayList<AbstractFeature>();
        List<SimpleType> documentSimpleExtensions = null;
        List<AbstractObject> documentObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_NAME.equals(eName)) {
                            name = reader.getElementText();
                        } else if (TAG_VISIBILITY.equals(eName)) {
                            visibility = parseBoolean(reader.getElementText());
                        } else if (TAG_OPEN.equals(eName)) {
                            open = parseBoolean(reader.getElementText());
                        } else if (TAG_ADDRESS.equals(eName)) {
                            address = reader.getElementText();
                        } else if (TAG_PHONE_NUMBER.equals(eName)) {
                            phoneNumber = reader.getElementText();
                        } else if (TAG_SNIPPET.equals(eName)) {
                            snippet = reader.getElementText();
                        } else if (TAG_DESCRIPTION.equals(eName)) {
                            description = reader.getElementText();
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = reader.getElementText();
                        } else if (isAbstractView(eName)) {
                            view = this.readAbstractView(eName);
                        } else if (isAbstractTimePrimitive(eName)) {
                            timePrimitive = this.readAbstractTimePrimitive(eName);
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = reader.getElementText();
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector.add(this.readAbstractStyleSelector(eName));
                        } else if (TAG_REGION.equals(eName)) {
                            region = this.readRegion();
                        } else if (TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = this.readExtendedData();
                        }

                        // DOCUMENT
                        else if (TAG_SCHEMA.equals(eName)) {
                            schemas.add(this.readSchema());
                        } else if (isAbstractFeature(eName)) {
                            features.add(this.readAbstractFeature(eName));
                        }


                    } else if (URI_ATOM.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = this.readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = this.readAtomLink();
                        }
                    }
                    if (URI_XAL.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = this.readXalAddressDetails();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_DOCUMENT.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createDocument(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber, snippet, description,
                view, timePrimitive, styleUrl, styleSelector, region, extendedData, featureSimpleExtensions, featureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions,
                schemas, features, documentSimpleExtensions, documentObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private Schema readSchema() throws XMLStreamException {

        // Schema
        List<SimpleField> simplefields = new ArrayList<SimpleField>();
        //public List<SchemaExtension> getSchemaExtensions();
        String name = reader.getAttributeValue(null, ATT_NAME);
        String id = reader.getAttributeValue(null, ATT_ID);

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // SCHEMA
                    if (URI_KML.equals(eUri)) {
                        if (TAG_SIMPLE_FIELD.equals(eName)) {
                            simplefields.add(this.readSimpleField());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_SCHEMA.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createSchema(simplefields, name, id);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private SimpleField readSimpleField() throws XMLStreamException {

        // SimpleField
        String displayName = null;
        //public List<SimpleFieldExtension> getSimpleFieldExtensions();
        String name = reader.getAttributeValue(null, ATT_NAME);
        String type = reader.getAttributeValue(null, ATT_TYPE);

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // SIMPLE FIELD
                    if (URI_KML.equals(eUri)) {
                        if (TAG_DISPLAY_NAME.equals(eName)) {
                            displayName = reader.getElementText();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_SIMPLE_FIELD.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createSimpleField(displayName, type, name);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private NetworkLink readNetworkLink() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractFeature
        String name = null;
        boolean visibility = DEF_VISIBILITY;
        boolean open = DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink atomLink = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        String snippet = null;
        String description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        String styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<AbstractStyleSelector>();
        Region region = null;
        ExtendedData extendedData = null;
        List<SimpleType> featureSimpleExtensions = null;
        List<AbstractObject> featureObjectExtensions = null;

        // NetworkLink
        boolean refreshVisibility = DEF_REFRESH_VISIBILITY;
        boolean flyToView = DEF_FLY_TO_VIEW;
        Link link = null;
        List<SimpleType> networkLinkSimpleExtensions = null;
        List<AbstractObject> networkLinkObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_NAME.equals(eName)) {
                            name = reader.getElementText();
                        } else if (TAG_VISIBILITY.equals(eName)) {
                            visibility = parseBoolean(reader.getElementText());
                        } else if (TAG_OPEN.equals(eName)) {
                            open = parseBoolean(reader.getElementText());
                        } else if (TAG_ADDRESS.equals(eName)) {
                            address = reader.getElementText();
                        } else if (TAG_PHONE_NUMBER.equals(eName)) {
                            phoneNumber = reader.getElementText();
                        } else if (TAG_SNIPPET.equals(eName)) {
                            snippet = reader.getElementText();
                        } else if (TAG_DESCRIPTION.equals(eName)) {
                            description = reader.getElementText();
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = reader.getElementText();
                        } else if (isAbstractView(eName)) {
                            view = this.readAbstractView(eName);
                        } else if (isAbstractTimePrimitive(eName)) {
                            timePrimitive = this.readAbstractTimePrimitive(eName);
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector.add(this.readAbstractStyleSelector(eName));
                        } else if (TAG_REGION.equals(eName)) {
                            region = this.readRegion();
                        } else if (TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = this.readExtendedData();
                        }

                        // NETWORK LINK
                        else if (TAG_REFRESH_VISIBILITY.equals(eName)) {
                            refreshVisibility = parseBoolean(reader.getElementText());
                        } else if (TAG_FLY_TO_VIEW.equals(eName)) {
                            flyToView = parseBoolean(reader.getElementText());
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            link = this.readLink(eName);
                        }


                    } else if (URI_ATOM.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = this.readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            atomLink = this.readAtomLink();
                        }
                    }
                    if (URI_XAL.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = this.readXalAddressDetails();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_NETWORK_LINK.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createNetworkLink(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, atomLink, address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive, styleUrl,
                styleSelector, region, extendedData,
                featureSimpleExtensions, featureObjectExtensions,
                refreshVisibility, flyToView, link,
                networkLinkSimpleExtensions, networkLinkObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private Point readPoint() throws XMLStreamException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractGeometry
        List<SimpleType> abstractGeometrySimpleExtensions = null;
        List<AbstractObject> abstractGeometryObjectExtensions = null;

        // Point
        boolean extrude = DEF_EXTRUDE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        Coordinates coordinates = null;
        List<SimpleType> pointSimpleExtensions = null;
        List<AbstractObject> pointObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // POINT
                    if (URI_KML.equals(eUri)) {
                        if (TAG_EXTRUDE.equals(eName)) {
                            extrude = parseBoolean(reader.getElementText());
                        } else if (TAG_ALTITUDE_MODE.equals(eName)) {
                            altitudeMode = AltitudeMode.transform(reader.getElementText());
                        } else if (TAG_COORDINATES.equals(eName)) {
                            coordinates = readCoordinates(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_POINT.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createPoint(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, altitudeMode, coordinates, pointSimpleExtensions, pointObjectExtensions);
    }

    /**
     * <p>This method transforms a String of KML coordinates into an instance of Coordinates.</p>
     *
     * @param coordinates The coordinates String.
     * @return
     */
    private Coordinates readCoordinates(String coordinates) {
        List<Coordinate> coordinatesList = new ArrayList<Coordinate>();

        String[] coordinatesStringList = coordinates.split("[\\s]+");
        
        for (String coordinatesString : coordinatesStringList) {
            if(!coordinatesString.equals("")){
                coordinatesList.add(KmlReader.kmlFactory.createCoordinate(coordinatesString));
            }
        }

        return KmlReader.kmlFactory.createCoordinates(coordinatesList);
    }

    /**
     *
     * @return
     */
    private IdAttributes readIdAttributes() {
        return KmlReader.kmlFactory.createIdAttributes(
                reader.getAttributeValue(null, ATT_ID), reader.getAttributeValue(null, ATT_TARGET_ID));
    }


    /*
     *  METHODES DE TEST SUR LES TYPES ABSTRAITS
     */

    /**
     *
     * @param eName the tag name.
     * @return true if the tag name is an AbstractGeometry element.
     */
    private boolean isAbstractGeometry(String eName) {
        return (TAG_MULTI_GEOMETRY.equals(eName)
                || TAG_LINE_STRING.equals(eName)
                || TAG_POLYGON.equals(eName)
                || TAG_POINT.equals(eName)
                || TAG_LINEAR_RING.equals(eName)
                || TAG_MODEL.equals(eName));
    }

    /**
     *
     * @param eName the tag name.
     * @return true if the tag name is an AbstractFeature element.
     */
    private boolean isAbstractFeature(String eName) {
        return (TAG_FOLDER.equals(eName)
                || TAG_GROUND_OVERLAY.equals(eName)
                || TAG_PHOTO_OVERLAY.equals(eName)
                || TAG_NETWORK_LINK.equals(eName)
                || TAG_DOCUMENT.equals(eName)
                || TAG_SCREEN_OVERLAY.equals(eName)
                || TAG_PLACEMARK.equals(eName));
    }

    /**
     *
     * @param eName the tag name.
     * @return true if the tag name is an AbstractContainer element.
     */
    private boolean isAbstractContainer(String eName) {
        return (TAG_FOLDER.equals(eName)
                || TAG_DOCUMENT.equals(eName));
    }

    /**
     *
     * @param eName the tag name.
     * @return true if the tag name is an AbstractOverlay element.
     */
    private boolean isAbstractOverlay(String eName) {
        return (TAG_GROUND_OVERLAY.equals(eName)
                || TAG_PHOTO_OVERLAY.equals(eName)
                || TAG_SCREEN_OVERLAY.equals(eName));
    }

    /**
     *
     * @param eName the tag name.
     * @return true if the tag name is an AbstractView element.
     */
    private boolean isAbstractView(String eName) {
        return (TAG_LOOK_AT.equals(eName)
                || TAG_CAMERA.equals(eName));
    }

    /**
     *
     * @param eName the tag name.
     * @return true if the tag name is an AbstractTimePrimitive element.
     */
    private boolean isAbstractTimePrimitive(String eName) {
        return (TAG_TIME_STAMP.equals(eName)
                || TAG_TIME_SPAN.equals(eName));
    }

    /**
     *
     * @param eName the tag name.
     * @return true if the tag name is an AbstractStyleSelector element.
     */
    private boolean isAbstractStyleSelector(String eName) {
        return (TAG_STYLE.equals(eName)
                || TAG_STYLE_MAP.equals(eName));
    }

    /**
     *
     * @param eName
     * @return
     */
    private boolean isAbstractSubStyle(String eName){
        return (TAG_BALLOON_STYLE.equals(eName)
                || TAG_LIST_STYLE.equals(eName)
                || isAbstractColorStyle(eName));
    }

    /**
     *
     * @param eName
     * @return
     */
    private boolean isAbstractColorStyle(String eName){
        return (TAG_ICON_STYLE.equals(eName)
                || TAG_LABEL_STYLE.equals(eName)
                || TAG_POLY_STYLE.equals(eName)
                || TAG_LINE_STYLE.equals(eName));
    }

    /**
     *
     * @param eName
     * @return
     */
    private boolean isAbstractObject(String eName) {
        // Compléter avec les autres types hériant de AbstractObject
        return (isAbstractFeature(eName)
                || isAbstractGeometry(eName)
                || isAbstractStyleSelector(eName)
                || isAbstractSubStyle(eName)
                || isAbstractView(eName));
    }

    /**
     * 
     * @param eName
     * @return
     */
    private boolean isAbstractLatLonBox(String eName) {
        return (TAG_LAT_LON_ALT_BOX.equals(eName)
                || TAG_LAT_LON_BOX.equals(eName));
    }
}

package org.geotoolkit.data.kml;

import com.ctc.wstx.stax.WstxInputFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.geotoolkit.data.model.KmlFactory;
import org.geotoolkit.data.model.KmlFactoryDefault;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.kml.AbstractContainer;
import org.geotoolkit.data.model.kml.AbstractFeature;
import org.geotoolkit.data.model.kml.AbstractGeometry;
import org.geotoolkit.data.model.kml.AbstractObject;
import org.geotoolkit.data.model.kml.AbstractOverlay;
import org.geotoolkit.data.model.kml.AbstractStyleSelector;
import org.geotoolkit.data.model.kml.AbstractTimePrimitive;
import org.geotoolkit.data.model.kml.AbstractView;
import org.geotoolkit.data.model.kml.Alias;
import org.geotoolkit.data.model.kml.AltitudeMode;
import org.geotoolkit.data.model.kml.Angle180;
import org.geotoolkit.data.model.kml.Angle360;
import org.geotoolkit.data.model.kml.Angle90;
import org.geotoolkit.data.model.kml.Anglepos180;
import org.geotoolkit.data.model.kml.BalloonStyle;
import org.geotoolkit.data.model.kml.BasicLink;
import org.geotoolkit.data.model.kml.Boundary;
import org.geotoolkit.data.model.kml.Camera;
import org.geotoolkit.data.model.kml.Color;
import org.geotoolkit.data.model.kml.ColorMode;
import org.geotoolkit.data.model.kml.Coordinate;
import org.geotoolkit.data.model.kml.Coordinates;
import org.geotoolkit.data.model.kml.Data;
import org.geotoolkit.data.model.kml.DisplayMode;
import org.geotoolkit.data.model.kml.Document;
import org.geotoolkit.data.model.kml.ExtendedData;
import org.geotoolkit.data.model.kml.Folder;
import org.geotoolkit.data.model.kml.GroundOverlay;
import org.geotoolkit.data.model.kml.IconStyle;
import org.geotoolkit.data.model.kml.IdAttributes;
import org.geotoolkit.data.model.kml.ItemIcon;
import org.geotoolkit.data.model.kml.ItemIconState;
import org.geotoolkit.data.model.kml.Kml;
import org.geotoolkit.data.model.kml.KmlException;
import org.geotoolkit.data.model.kml.LabelStyle;
import org.geotoolkit.data.model.kml.LatLonAltBox;
import org.geotoolkit.data.model.kml.LatLonBox;
import org.geotoolkit.data.model.kml.LineString;
import org.geotoolkit.data.model.kml.LineStyle;
import org.geotoolkit.data.model.kml.LinearRing;
import org.geotoolkit.data.model.kml.Link;
import org.geotoolkit.data.model.kml.ListItem;
import org.geotoolkit.data.model.kml.ListStyle;
import org.geotoolkit.data.model.kml.Location;
import org.geotoolkit.data.model.kml.Lod;
import org.geotoolkit.data.model.kml.LookAt;
import org.geotoolkit.data.model.kml.Model;
import org.geotoolkit.data.model.kml.MultiGeometry;
import org.geotoolkit.data.model.kml.NetworkLink;
import org.geotoolkit.data.model.kml.NetworkLinkControl;
import org.geotoolkit.data.model.kml.Orientation;
import org.geotoolkit.data.model.kml.Pair;
import org.geotoolkit.data.model.kml.PhotoOverlay;
import org.geotoolkit.data.model.kml.Placemark;
import org.geotoolkit.data.model.kml.Point;
import org.geotoolkit.data.model.kml.PolyStyle;
import org.geotoolkit.data.model.kml.Polygon;
import org.geotoolkit.data.model.kml.RefreshMode;
import org.geotoolkit.data.model.kml.Region;
import org.geotoolkit.data.model.kml.ResourceMap;
import org.geotoolkit.data.model.kml.Scale;
import org.geotoolkit.data.model.kml.Schema;
import org.geotoolkit.data.model.kml.SchemaData;
import org.geotoolkit.data.model.kml.ScreenOverlay;
import org.geotoolkit.data.model.kml.SimpleData;
import org.geotoolkit.data.model.kml.SimpleField;
import org.geotoolkit.data.model.kml.Style;
import org.geotoolkit.data.model.kml.StyleMap;
import org.geotoolkit.data.model.kml.StyleState;
import org.geotoolkit.data.model.kml.TimeSpan;
import org.geotoolkit.data.model.kml.TimeStamp;
import org.geotoolkit.data.model.kml.Units;
import org.geotoolkit.data.model.kml.Vec2;
import org.geotoolkit.data.model.kml.ViewRefreshMode;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xsd.SimpleType;
import org.geotoolkit.xml.StaxStreamReader;
import static org.geotoolkit.data.model.ModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class KmlReader extends StaxStreamReader {

    private XMLInputFactory inputFactory;//A SUPPRIMER
    private Kml root;
    private KmlFactory kmlFactory;

    public KmlReader(File file) {
        super();
        this.initSource(file);
    }

    private void initSource(Object o) {
        // Choice of the StAX implementation of Java 6 interface
        System.setProperty("javax.xml.stream.XMLInputFactory", "com.ctc.wstx.stax.WstxInputFactory");
        System.setProperty("javax.xml.stream.XMLEventFactory", "com.ctc.wstx.stax.WstxEventFactory");

        // Factories
        //XMLInputFactory factory = new WstxInputFactory();// Implementation explicitly named
        this.inputFactory = XMLInputFactory.newInstance();// Transparent implementation based on the previous choice.
        //this.inputFactory = new WstxInputFactory();
        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
        inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        inputFactory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
        ((WstxInputFactory) inputFactory).configureForSpeed();

        if (this.inputFactory.isPropertySupported("javax.xml.stream.isValidating")) {
            this.inputFactory.setProperty("javax.xml.stream.isValidating", Boolean.TRUE);
//            System.out.println("Validation active : " + this.inputFactory.getProperty("javax.xml.stream.isValidating"));
        }

        // Errors displaying
        this.inputFactory.setXMLReporter(new XMLReporter() {

            public void report(String message, String typeErreur, Object source, javax.xml.stream.Location location) throws XMLStreamException {
//                System.out.println("Erreur de type : " + typeErreur + ", message : " + message);
            }
        });

        try {
            this.setInput(o);
        } catch (IOException ex) {
            Logger.getLogger(KmlReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(KmlReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.kmlFactory = new KmlFactoryDefault();
    }

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

        return this.kmlFactory.createKml(networkLinkControl, abstractFeature, kmlSimpleExtensions, kmlObjectExtensions);
    }

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
                        if (TAG_ATOM_PERSON_CONSTRUCT.equals(eName)) {
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

        return this.kmlFactory.createPlacemark(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber, snippet, description, view, timePrimitive, styleUrl,
                styleSelector, region, extendedData, featureSimpleExtensions, featureObjectExtensions,
                abstractGeometry, placemarkSimpleExtensions, placemarkObjectExtensions);
    }

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

        return this.kmlFactory.createRegion(objectSimpleExtensions, idAttributes,
                latLonAltBox, lod, regionSimpleExtensions, regionObjectExtentions);
    }

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
                            minLodPixels = Double.parseDouble(reader.getElementText());
                        } else if (TAG_MAX_LOD_PIXELS.equals(eName)) {
                            maxLodPixels = Double.parseDouble(reader.getElementText());
                        } else if (TAG_MIN_FADE_EXTENT.equals(eName)) {
                            minFadeExtent = Double.parseDouble(reader.getElementText());
                        } else if (TAG_MAX_FADE_EXTENT.equals(eName)) {
                            maxFadeExtent = Double.parseDouble(reader.getElementText());
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

        return this.kmlFactory.createLod(objectSimpleExtensions, idAttributes,
                minLodPixels, maxLodPixels, minFadeExtent, maxFadeExtent, lodSimpleExtentions, lodObjectExtensions);
    }

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
        
        return this.kmlFactory.createExtendedData(datas, schemaDatas, anyOtherElements);
    }

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

        return this.kmlFactory.createData(objectSimpleExtensions, idAttributes, displayName, value, dataExtensions);
    }

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

        return this.kmlFactory.createSchemaData(objectSimpleExtensions, idAttributes, simpleDatas, schemaDataExtensions);
    }

    private SimpleData readSimpleData() throws XMLStreamException {
        return this.kmlFactory.createSimpleData(reader.getAttributeValue(null, ATT_NAME), reader.getElementText());
    }

    private NetworkLinkControl readNetworkLinkControl() {
        NetworkLinkControl resultat = null;
//        if()
//        kml:minRefreshPeriod" minOccurs="0"/>
//      <element ref="kml:maxSessionLength" minOccurs="0"/>
//      <element ref="kml:cookie" minOccurs="0"/>
//      <element ref="kml:message" minOccurs="0"/>
//      <element ref="kml:linkName" minOccurs="0"/>
//      <element ref="kml:linkDescription" minOccurs="0"/>
//      <element ref="kml:linkSnippet" minOccurs="0"/>
//      <element ref="kml:expires" minOccurs="0"/>
//      <element ref="kml:Update" minOccurs="0"/>
//      <element ref="kml:AbstractViewGroup" minOccurs="0"/>
//      <element ref="kml:NetworkLinkControlSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
//      <element ref="kml:NetworkLinkControlObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>

        return resultat;
    }

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

        return this.kmlFactory.createPolygon(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode, outerBoundaryIs, innerBoundariesAre,
                polygonSimpleExtensions, polygonObjectExtensions);
    }

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

        return this.kmlFactory.createBoundary(linearRing, boundarySimpleExtensions, boundaryObjectExtensions);
    }

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
                        } else if (TAG_SCALE.equals(eName)) {
                            scale = readScale();
                        } else if (TAG_LINK.equals(eName)) {
                            link = this.readLink(eName);
                        } else if (TAG_SCALE.equals(eName)) {
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

        return this.kmlFactory.createModel(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                altitudeMode, location, orientation, scale, link, resourceMap,
                modelSimpleExtensions, modelObjectExtensions);
    }

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

        return this.kmlFactory.createResourceMap(objectSimpleExtensions, idAttributes,
                aliases, resourceMapSimpleExtensions, resourceMapObjectExtensions);

    }

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

        return this.kmlFactory.createAlias(objectSimpleExtensions, idAttributes,
                targetHref, sourceHref, alaisSimpleExtensions, aliasObjectExtensions);

    }

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
                            x = Double.parseDouble(reader.getElementText());
                        } else if (TAG_Y.equals(eName)) {
                            y = Double.parseDouble(reader.getElementText());
                        } else if (TAG_Z.equals(eName)){
                            y = Double.parseDouble(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_SCALE.equals(reader.getLocalName()) && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return this.kmlFactory.createScale(objectSimpleExtensions, idAttributes,
                x, y, z, scaleSimpleExtensions, scaleObjectExtensions);
        
    }

    private Location readLocation() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // Location
        Angle180 longitude = DEF_LONGITUDE;
        Angle90 latitude = DEF_LATITUDE;
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
                            longitude = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_LATITUDE.equals(eName)) {
                            latitude = this.kmlFactory.createAngle90(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_ALTITUDE.equals(eName)){
                            altitude = Double.parseDouble(reader.getElementText());
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

        return this.kmlFactory.createLocation(objectSimpleExtensions, idAttributes,
                longitude, latitude, altitude, locationSimpleExtensions, locationObjectExtensions);
    }

    private Orientation readOrientation() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // Orientation
        Angle360 heading = DEF_HEADING;
        Anglepos180 tilt = DEF_TILT;
        Angle180 roll = DEF_ROLL;
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
                            heading = this.kmlFactory.createAngle360(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_TILT.equals(eName)) {
                            tilt = this.kmlFactory.createAnglepos180(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_ROLL.equals(eName)) {
                            roll = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
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

        return this.kmlFactory.createOrientation(objectSimpleExtensions, idAttributes,
                heading, tilt, roll, orientationSimpleExtensions, orientationObjectExtensions);
    }

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

        return this.kmlFactory.createLinearRing(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode, coordinates,
                linearRingSimpleExtensions, linearRingObjectExtensions);
    }

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

        return this.kmlFactory.createLineString(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode, coordinates,
                lineStringSimpleExtensions, lineStringObjectExtensions);
    }

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

        return this.kmlFactory.createMultiGeometry(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                geometries, multiGeometrySimpleExtensions, multiGeometryObjectExtensions);

    }

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
     * Reads an AbstractOverlay
     * @param eName The Tag name
     * @return an AbstractOverlay instance
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
        Link icon = null;
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
                            color = this.kmlFactory.createColor(reader.getElementText());
                        } else if (TAG_DRAW_ORDER.equals(eName)) {
                            drawOrder = Integer.parseInt(reader.getElementText());
                        } else if (TAG_ICON.equals(eName)) {
                            icon = readLink(eName);
                        }

                        // GROUND OVERLAY
                        else if (TAG_ALTITUDE.equals(eName)) {
                            altitude = Double.parseDouble(reader.getElementText());
                        } else if (TAG_ALTITUDE_MODE.equals(eName)) {
                            altitudeMode = AltitudeMode.transform(reader.getElementText());
                        } else if (TAG_LAT_LON_BOX.equals(eName)) {
                            latLonBox = this.readLatLonBox();
                        }


                    } else if (URI_ATOM.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_ATOM_PERSON_CONSTRUCT.equals(eName)) {
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

        return this.kmlFactory.createGroundOverlay(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link,
                address, addressDetails, phoneNumber, snippet, description, view, timePrimitive,
                styleUrl, styleSelector, region, extendedData,
                featureSimpleExtensions, featureObjectExtensions,
                color, drawOrder, icon,
                abstractOverlaySimpleExtensions, abstractOverlayObjectExtensions,
                altitude, altitudeMode, latLonBox,
                groundOverlaySimpleExtensions, groundOverlayObjectExtensions);
    }

    private Link readLink(String stopTag) throws KmlException, XMLStreamException {

        if (stopTag == null) {
            throw new KmlException("The stop tag cannot be null. "
                    + "It's probably an <Icon>, <Link> or <Url> tag according to KML 2.2 specification.");
        }

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
                            refreshInterval = Double.parseDouble(reader.getElementText());
                        } else if (TAG_VIEW_REFRESH_MODE.equals(eName)) {
                            viewRefreshMode = ViewRefreshMode.transform(reader.getElementText());
                        } else if (TAG_VIEW_REFRESH_TIME.equals(eName)) {
                            viewRefreshTime = Double.parseDouble(reader.getElementText());
                        } else if (TAG_VIEW_BOUND_SCALE.equals(eName)) {
                            viewBoundScale = Double.parseDouble(reader.getElementText());
                        } else if (TAG_VIEW_FORMAT.equals(eName)) {
                            viewFormat = reader.getElementText();
                        } else if (TAG_HTTP_QUERY.equals(eName)) {
                            httpQuery = reader.getElementText();
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

        return this.kmlFactory.createLink(objectSimpleExtensions, idAttributes,
                href, basicLinkSimpleExtensions, basicLinkObjectExtensions,
                refreshMode, refreshInterval, viewRefreshMode, viewRefreshTime, viewBoundScale, viewFormat, httpQuery,
                linkSimpleExtensions, linkObjectExtensions);
    }

    private LatLonBox readLatLonBox() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractLatLonBox
        Angle180 north = DEF_NORTH;
        Angle180 south = DEF_SOUTH;
        Angle180 east = DEF_EAST;
        Angle180 west = DEF_WEST;
        List<SimpleType> abstractLatLonBoxSimpleExtensions = null;
        List<AbstractObject> abstractLatLonBoxObjectExtensions = null;

        // LatLonBox
        Angle180 rotation = DEF_ROTATION;
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
                            north = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_SOUTH.equals(eName)) {
                            south = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_EAST.equals(eName)) {
                            east = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_WEST.equals(eName)) {
                            west = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
                        } 
                        
                        // LATLONBOX
                        else if (TAG_ROTATION.equals(eName)) {
                            rotation = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
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
        return this.kmlFactory.createLatLonBox(objectSimpleExtensions, idAttributes,
                north, south, east, west,
                abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions,
                rotation, latLonBoxSimpleExtensions, latLonBoxObjectExtensions);
    }

    private LatLonAltBox readLatLonAltBox() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractLatLonBox
        Angle180 north = DEF_NORTH;
        Angle180 south = DEF_SOUTH;
        Angle180 east = DEF_EAST;
        Angle180 west = DEF_WEST;
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
                            north = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_SOUTH.equals(eName)) {
                            south = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_EAST.equals(eName)) {
                            east = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_WEST.equals(eName)) {
                            west = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
                        }

                        // LATLONALTBOX
                        else if (TAG_MIN_ALTITUDE.equals(eName)) {
                            minAltitude = Double.parseDouble(reader.getElementText());
                        } else if (TAG_MAX_ALTITUDE.equals(eName)) {
                            maxAltitude = Double.parseDouble(reader.getElementText());
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
        return this.kmlFactory.createLatLonAltBox(objectSimpleExtensions, idAttributes,
                north, south, east, west, abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions,
                minAltitude, maxAltitude, altitudeMode, latLonAltBoxSimpleExtensions, latLonAltBoxObjectExtensions);
    }

    private PhotoOverlay readPhotoOverlay() {
        PhotoOverlay resultat = null;

        return resultat;
    }

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
        Link icon = null;
        List<SimpleType> abstractOverlaySimpleExtensions = null;
        List<AbstractObject> abstractOverlayObjectExtensions = null;

        // ScreenOverlay
        Vec2 overlayXY = null;
        Vec2 screenXY = null;
        Vec2 rotationXY = null;
        Vec2 size = null;
        Angle180 rotation = DEF_ROTATION;
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
                            color = this.kmlFactory.createColor(reader.getElementText());
                        } else if (TAG_DRAW_ORDER.equals(eName)) {
                            drawOrder = Integer.parseInt(reader.getElementText());
                        } else if (TAG_ICON.equals(eName)) {
                            icon = readLink(eName);
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
                            rotation = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
                        }


                    } else if (URI_ATOM.equals(eUri)) {

                        // ABSTRACT FEATURE
                        if (TAG_ATOM_PERSON_CONSTRUCT.equals(eName)) {
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

        return this.kmlFactory.createScreenOverlay(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive, styleUrl, styleSelector,
                region, extendedData, featureSimpleExtensions, featureObjectExtensions,
                color, drawOrder, icon, abstractOverlaySimpleExtensions, abstractOverlayObjectExtensions,
                overlayXY, screenXY, rotationXY, size, rotation,
                screenOverlaySimpleExtensions, screenOverlayObjectExtensions);
    }

    /**
     *
     * @param eName The tag name
     * @return An AbstractContainer instance
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
     * @param eName The tag name
     * @return An AbstractView instance
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

    private LookAt readLookAt() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractView
        List<SimpleType> abstractViewSimpleExtensions = null;
        List<AbstractObject> abstractViewObjectExtensions = null;

        // LookAt
        Angle180 longitude = DEF_LONGITUDE;
        Angle90 latitude = DEF_LATITUDE;
        double altitude = DEF_ALTITUDE;
        Angle360 heading = DEF_HEADING;
        Anglepos180 tilt = DEF_TILT;
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
                            longitude = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_LATITUDE.equals(eName)) {
                            latitude = this.kmlFactory.createAngle90(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_ALTITUDE.equals(eName)) {
                            altitude = Double.parseDouble(reader.getElementText());
                        } else if (TAG_HEADING.equals(eName)) {
                            heading = this.kmlFactory.createAngle360(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_TILT.equals(eName)) {
                            tilt = this.kmlFactory.createAnglepos180(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_RANGE.equals(eName)) {
                            range = Double.parseDouble(reader.getElementText());
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
        return this.kmlFactory.createLookAt(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, range,
                lookAtSimpleExtensions, lookAtObjectExtensions);
    }

    private Camera readCamera() throws XMLStreamException, KmlException {
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractView
        List<SimpleType> abstractViewSimpleExtensions = null;
        List<AbstractObject> abstractViewObjectExtensions = null;

        // Camera
        Angle180 longitude = DEF_LONGITUDE;
        Angle90 latitude = DEF_LATITUDE;
        double altitude = DEF_ALTITUDE;
        Angle360 heading = DEF_HEADING;
        Anglepos180 tilt = DEF_TILT;
        Angle180 roll = DEF_ROLL;
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
                            longitude = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_LATITUDE.equals(eName)) {
                            latitude = this.kmlFactory.createAngle90(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_ALTITUDE.equals(eName)) {
                            altitude = Double.parseDouble(reader.getElementText());
                        } else if (TAG_HEADING.equals(eName)) {
                            heading = this.kmlFactory.createAngle360(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_TILT.equals(eName)) {
                            tilt = this.kmlFactory.createAnglepos180(Double.parseDouble(reader.getElementText()));
                        } else if (TAG_ROLL.equals(eName)) {
                            roll = this.kmlFactory.createAngle180(Double.parseDouble(reader.getElementText()));
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
        return this.kmlFactory.createCamera(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, roll,
                cameraSimpleExtensions, cameraObjectExtensions);
    }

    /**
     *
     * @param eName The tag name
     * @return An AbstractStyleSelector instance
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
        return this.kmlFactory.createStyleMap(objectSimpleExtensions, idAttributes,
                styleSelectorSimpleExtensions, styleSelectorObjectExtensions,
                pairs, styleMapSimpleExtensions, styleMapObjectExtensions);
    }

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
        return this.kmlFactory.createPair(objectSimpleExtensions, idAttributes,
                key, styleUrl, styleSelector, pairSimpleExtensions, pairObjectExtensions);
    }

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
        return this.kmlFactory.createStyle(objectSimpleExtensions, idAttributes,
                styleSelectorSimpleExtensions, styleSelectorObjectExtensions,
                iconStyle, labelStyle, lineStyle, polyStyle, balloonStyle, listStyle,
                styleSimpleExtensions, styleObjectExtensions);
    }

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
        Angle360 heading = DEF_HEADING;
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
                            color = this.kmlFactory.createColor(reader.getElementText());
                        } else if (TAG_COLOR_MODE.equals(eName)) {
                            colorMode = ColorMode.transform(reader.getElementText());
                        }

                        // ICON STYLE
                        else if (TAG_SCALE.equals(eName)) {
                            scale = Double.parseDouble(reader.getElementText());
                        } else if (TAG_HEADING.equals(eName)) {
                            heading = this.kmlFactory.createAngle360(Double.parseDouble(reader.getElementText()));
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

        return this.kmlFactory.createIconStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode,
                colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, heading, icon, hotSpot,
                iconStyleSimpleExtensions, iconStyleObjectExtensions);
    }

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
                            color = this.kmlFactory.createColor(reader.getElementText());
                        } else if (TAG_COLOR_MODE.equals(eName)) {
                            colorMode = ColorMode.transform(reader.getElementText());
                        }

                        // LABEL STYLE
                        else if (TAG_SCALE.equals(eName)) {
                            scale = Double.parseDouble(reader.getElementText());
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

        return this.kmlFactory.createLabelStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, labelStyleSimpleExtensions, labelStyleObjectExtensions);
    }

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
                            color = this.kmlFactory.createColor(reader.getElementText());
                        } else if (TAG_COLOR_MODE.equals(eName)) {
                            colorMode = ColorMode.transform(reader.getElementText());
                        }

                        // LINE STYLE
                        else if (TAG_WIDTH.equals(eName)) {
                            width = Double.parseDouble(reader.getElementText());
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

        return this.kmlFactory.createLineStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                width, lineStyleSimpleExtensions, lineStyleObjectExtensions);
    }

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
                            color = this.kmlFactory.createColor(reader.getElementText());
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

        return this.kmlFactory.createPolyStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                fill, outline, polyStyleSimpleExtensions, polyStyleObjectExtensions);
    }

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
                            bgColor = this.kmlFactory.createColor(reader.getElementText());
                        } else if (TAG_TEXT_COLOR.equals(eName)) {
                            textColor = this.kmlFactory.createColor(reader.getElementText());
                        } else if (TAG_TEXT.equals(eName)) {
                            text = reader.getElementText();
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

        return this.kmlFactory.createBalloonStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                bgColor, textColor, text, displayMode,
                balloonStyleSimpleExtensions, balloonStyleObjectExtensions);
    }

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
                            bgColor = this.kmlFactory.createColor(reader.getElementText());
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

        return this.kmlFactory.createListStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                listItem, bgColor, itemIcons, maxSnippetLines,
                listStyleSimpleExtensions, listStyleObjectExtensions);
    }

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

        return this.kmlFactory.createItemIcon(objectSimpleExtensions, idAttributes,
                states, href, itemIconSimpleExtensions, itemIconObjectExtensions);
    }

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

        return this.kmlFactory.createBasicLink(objectSimpleExtensions, idAttributes,
                href, basicLinkSimpleExtensions, basicLinkObjectExtensions);
    }

    private Vec2 readVec2(String stopTag) throws XMLStreamException, KmlException {

        if (stopTag == null) {
            throw new KmlException("The stop tag cannot be null. "
                    + "It's propably <hotSpot>, <rotationXY>, <size>, <overlayXY> or <screenXY> according to KML 2.2 specification.");
        }

        double x = DEF_VEC2_X;
        String sx = reader.getAttributeValue(null, ATT_X);
        if (sx != null) {
            x = Double.parseDouble(sx);
        }

        double y = DEF_VEC2_Y;
        String sy = reader.getAttributeValue(null, ATT_Y);
        if (sy != null) {
            y = Double.parseDouble(sy);
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

        return this.kmlFactory.createVec2(x, y, xUnit, yUnit);
    }

    /**
     *
     * @param eName The tag name
     * @return An AbstractTimePrimitive instance
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

    private TimeSpan readTimeSpan() throws XMLStreamException{
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractTimePrimitive
        List<SimpleType> AbstractTimePrimitiveSimpleExtensions = null;
        List<AbstractObject> AbstractTimePrimitiveObjectExtensions = null;

        // TimeSpan
        String begin = null;
        String end = null;
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
                            begin = reader.getElementText();
                        } else if (TAG_END.equals(eName)) {
                            end = reader.getElementText();
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
        return this.kmlFactory.createTimeSpan(objectSimpleExtensions, idAttributes,
                AbstractTimePrimitiveSimpleExtensions, AbstractTimePrimitiveObjectExtensions,
                begin, end, TimeSpanSimpleExtensions, TimeSpanObjectExtensions);
    }

    private TimeStamp readTimeStamp() throws XMLStreamException{
        // AbstractObject
        List<SimpleType> objectSimpleExtensions = null;
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractTimePrimitive
        List<SimpleType> AbstractTimePrimitiveSimpleExtensions = null;
        List<AbstractObject> AbstractTimePrimitiveObjectExtensions = null;

        // TimeStamp
        String when = null;
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
                            when = reader.getElementText();
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
        return this.kmlFactory.createTimeStamp(objectSimpleExtensions, idAttributes,
                AbstractTimePrimitiveSimpleExtensions, AbstractTimePrimitiveObjectExtensions,
                when, TimeStampSimpleExtensions, TimeStampObjectExtensions);
    }

    /**
     *
     * @return An AtomPersonConstruct instance
     * @throws XMLStreamException
     */
    private AtomPersonConstruct readAtomPersonConstruct() throws XMLStreamException {
        List<String> names = new ArrayList<String>();
        List<String> uris = new ArrayList<String>();
        List<String> emails = new ArrayList<String>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_ATOM.equals(eUri)) {
                        if (TAG_ATOM_NAME.equals(eName)) {
                            names.add(reader.getElementText());
                        } else if (TAG_ATOM_URI.equals(eName)) {
                            uris.add(reader.getElementText());
                        } else if (TAG_ATOM_NAME.equals(eName)) {
                            emails.add(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_ATOM_PERSON_CONSTRUCT.equals(reader.getLocalName()) && URI_ATOM.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return this.kmlFactory.createAtomPersonConstruct(names, uris, emails);
    }

    private AtomLink readAtomLink() throws XMLStreamException {
        AtomLink resultat = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_ATOM.equals(eUri)) {
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_ATOM_LINK.equals(reader.getLocalName()) && URI_ATOM.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return resultat;
    }

    private AddressDetails readXalAddressDetails() throws XMLStreamException {
        AddressDetails resultat = null;
        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_XAL.equals(eUri)) {
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_XAL_ADDRESS_DETAILS.equals(reader.getLocalName()) && URI_XAL.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return resultat;
    }

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
                        if (TAG_ATOM_PERSON_CONSTRUCT.equals(eName)) {
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
        return this.kmlFactory.createFolder(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive, styleUrl, styleSelector, region, extendedData,
                featureSimpleExtensions, featureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions,
                features, folderSimpleExtensions, folderObjectExtensions);
    }

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
                        if (TAG_ATOM_PERSON_CONSTRUCT.equals(eName)) {
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

        return this.kmlFactory.createDocument(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber, snippet, description,
                view, timePrimitive, styleUrl, styleSelector, region, extendedData, featureSimpleExtensions, featureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions,
                schemas, features, documentSimpleExtensions, documentObjectExtensions);
    }

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

        return this.kmlFactory.createSchema(simplefields, name, id);
    }

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

        return this.kmlFactory.createSimpleField(displayName, type, name);
    }

    private NetworkLink readNetworkLink() {
        NetworkLink resultat = null;

        return resultat;
    }

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

        return this.kmlFactory.createPoint(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, altitudeMode, coordinates, pointSimpleExtensions, pointObjectExtensions);
    }

    /**
     * Transforms a String of KML coordinates into an instance of Coordinates
     * @param coordinates The coordinates String
     * @return The Coordinates object wich is a List of Coordinate instances.
     */
    private Coordinates readCoordinates(String coordinates) {
        List<Coordinate> coordinatesList = new ArrayList<Coordinate>();

        String[] coordinatesStringList = coordinates.split("[\\s]+");
        
        for (String coordinatesString : coordinatesStringList) {
            if(!coordinatesString.equals("")){
                coordinatesList.add(this.kmlFactory.createCoordinate(coordinatesString));
            }
        }

        return this.kmlFactory.createCoordinates(coordinatesList);
    }

    private IdAttributes readIdAttributes() {
        return this.kmlFactory.createIdAttributes(
                reader.getAttributeValue(null, ATT_ID), reader.getAttributeValue(null, ATT_TARGET_ID));
    }


    /*
     *  METHODES DE TEST SUR LES TYPES ABSTRAITS
     */
    private boolean isAbstractGeometry(String eName) {
        return (TAG_MULTI_GEOMETRY.equals(eName)
                || TAG_LINE_STRING.equals(eName)
                || TAG_POLYGON.equals(eName)
                || TAG_POINT.equals(eName)
                || TAG_LINEAR_RING.equals(eName)
                || TAG_MODEL.equals(eName));
    }

    private boolean isAbstractFeature(String eName) {
        return (TAG_FOLDER.equals(eName)
                || TAG_GROUND_OVERLAY.equals(eName)
                || TAG_PHOTO_OVERLAY.equals(eName)
                || TAG_NETWORK_LINK.equals(eName)
                || TAG_DOCUMENT.equals(eName)
                || TAG_SCREEN_OVERLAY.equals(eName)
                || TAG_PLACEMARK.equals(eName));
    }

    private boolean isAbstractContainer(String eName) {
        return (TAG_FOLDER.equals(eName)
                || TAG_DOCUMENT.equals(eName));
    }

    private boolean isAbstractOverlay(String eName) {
        return (TAG_GROUND_OVERLAY.equals(eName)
                || TAG_PHOTO_OVERLAY.equals(eName)
                || TAG_SCREEN_OVERLAY.equals(eName));
    }

    private boolean isAbstractView(String eName) {
        return (TAG_LOOK_AT.equals(eName)
                || TAG_CAMERA.equals(eName));
    }

    private boolean isAbstractTimePrimitive(String eName) {
        return (TAG_TIME_STAMP.equals(eName)
                || TAG_TIME_SPAN.equals(eName));
    }

    private boolean isAbstractStyleSelector(String eName) {
        return (TAG_STYLE.equals(eName)
                || TAG_STYLE_MAP.equals(eName));
    }

    /*
     * METHODES UTILITAIRES
     */
    /**
     * Adaptation of boolean parsing to XML boolean values 1 and 0.
     * @param bool The String to parse
     * @return true if bool is equal to "true" or "1".
     */
    private static boolean parseBoolean(String bool) {
        boolean resultat = Boolean.parseBoolean(bool);
        if (!resultat && Integer.parseInt(bool) == 1) {
            resultat = true;
        }
        return resultat;
    }
}

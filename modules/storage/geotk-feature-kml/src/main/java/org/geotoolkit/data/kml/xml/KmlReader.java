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
package org.geotoolkit.data.kml.xml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;

import java.net.URISyntaxException;
import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.xal.xml.XalReader;
import org.geotoolkit.atom.xml.AtomReader;
import org.geotoolkit.data.kml.KmlFactory;
import org.geotoolkit.data.kml.DefaultKmlFactory;
import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.model.AbstractColorStyle;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.AbstractLatLonBox;
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
import org.geotoolkit.data.kml.model.Create;
import org.geotoolkit.data.kml.model.Data;
import org.geotoolkit.data.kml.model.Delete;
import org.geotoolkit.data.kml.model.DisplayMode;
import org.geotoolkit.data.kml.model.EnumAltitudeMode;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.GridOrigin;
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
import org.geotoolkit.data.kml.model.Metadata;
import org.geotoolkit.data.kml.model.Model;
import org.geotoolkit.data.kml.model.MultiGeometry;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.Orientation;
import org.geotoolkit.data.kml.model.Pair;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.PolyStyle;
import org.geotoolkit.data.kml.model.Polygon;
import org.geotoolkit.data.kml.model.RefreshMode;
import org.geotoolkit.data.kml.model.Region;
import org.geotoolkit.data.kml.model.ResourceMap;
import org.geotoolkit.data.kml.model.Scale;
import org.geotoolkit.data.kml.model.Schema;
import org.geotoolkit.data.kml.model.SchemaData;
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
import org.geotoolkit.data.kml.model.Url;
import org.geotoolkit.data.kml.model.Vec2;
import org.geotoolkit.data.kml.model.ViewRefreshMode;
import org.geotoolkit.data.kml.model.ViewVolume;
import org.geotoolkit.xal.model.AddressDetails;
import org.geotoolkit.xal.model.XalException;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import org.geotoolkit.temporal.object.ISODateParser;
import org.geotoolkit.xml.StaxStreamReader;

import org.apache.sis.util.logging.Logging;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;
import static org.geotoolkit.data.kml.KmlUtilities.*;
import org.opengis.feature.Feature;

/**
 *
 * @author Samuel Andrés
 * @module
 */
public class KmlReader extends StaxStreamReader {

    private static String URI_KML;
    private static KmlFactory KML_FACTORY;
    private static final XalReader XAL_READER = new XalReader();
    private static final AtomReader ATOM_READER = new AtomReader();
    private final ISODateParser fastDateParser = new ISODateParser();
    private final List<KmlExtensionReader> extensionReaders = new ArrayList<KmlExtensionReader>();
    private final List<KmlExtensionReader> dataReaders = new ArrayList<KmlExtensionReader>();
    //Boolean use to specify if the read document contain namespace or not
    private boolean useNamespace = true;

    public KmlReader() {
        KML_FACTORY = DefaultKmlFactory.getInstance();
//        System.setProperty("javax.xml.stream.XMLInputFactory", "com.bea.xml.stream.MXParserFactory");
//        System.setProperty("javax.xml.stream.XMLEventFactory", "com.bea.xml.stream.EventFactory");

//        System.setProperty("javax.xml.stream.XMLInputFactory", "com.ctc.wstx.stax.WstxInputFactory");
//        System.setProperty("javax.xml.stream.XMLEventFactory", "com.ctc.wstx.stax.WstxEventFactory");
    }

    public KmlReader(KmlFactory kmlFactory) {
        KML_FACTORY = kmlFactory;
    }

    /**
     * Set input. This method doesn't indicate kml uri version whose detection
     * is automatic at kml root reading. In other cases, method with Kml version uri
     * argument is necessary.
     */
    @Override
    public void setInput(Object input) throws IOException, XMLStreamException {
        super.setInput(input);
        XAL_READER.setInput(reader);
        ATOM_READER.setInput(reader);
    }

    /**
     * Set input. This method is necessary if Kml elements are read out of Kml document
     * with kml root elements.
     */
    public void setInput(Object input, String KmlVersionUri) throws IOException, XMLStreamException, KmlException {
        this.setInput(input);
        if (checkNamespace(KmlVersionUri)) {
            URI_KML = KmlVersionUri;
        } else {
            throw new KmlException("Bad Kml version Uri. This reader supports 2.1 and 2.2 versions.");
        }
    }

    /**
     * This method allows to add extensions readers.
     * An extension reader must implement KmlExtensionReader interface.
     */
    public void addExtensionReader(StaxStreamReader reader) throws KmlException, IOException, XMLStreamException {
        if (reader instanceof KmlExtensionReader) {
            this.extensionReaders.add((KmlExtensionReader) reader);
            reader.setInput(this.reader);
        } else {
            throw new KmlException("Extension reader must implements " + KmlExtensionReader.class.getName() + " interface.");
        }
    }

    public void addDataReader(StaxStreamReader reader) throws KmlException, IOException, XMLStreamException {
        if (reader instanceof KmlExtensionReader) {
            this.dataReaders.add((KmlExtensionReader) reader);
            reader.setInput(this.reader);
        } else {
            throw new KmlException("Extension reader must implements " + KmlExtensionReader.class.getName() + " interface.");
        }
    }

    protected KmlExtensionReader getDataReader(String containingTag, String contentsUri, String contentsTag) {
        for (KmlExtensionReader r : this.dataReaders) {
            if (r.canHandleComplexExtension(URI_KML, containingTag, contentsUri, contentsTag)
                    || r.canHandleSimpleExtension(URI_KML, containingTag, contentsUri, contentsTag)) {
                return r;
            }
        }
        return null;
    }

    protected KmlExtensionReader getComplexExtensionReader(String containingTag, String contentsUri, String contentsTag)
            throws KmlException
    {
        for (KmlExtensionReader r : this.extensionReaders) {
            if (r.canHandleComplexExtension(URI_KML, containingTag, contentsUri, contentsTag)) {
                return r;
            }
        }
        return null;
    }

    protected KmlExtensionReader getSimpleExtensionReader(String containingTag, String contentsUri, String contentsTag)
            throws KmlException
    {
        for (KmlExtensionReader r : this.extensionReaders) {
            if (r.canHandleSimpleExtension(URI_KML, containingTag, contentsUri, contentsTag)) {
                return r;
            }
        }
        return null;
    }

    /**
     * Returns kml namespace version uri.
     */
    public String getVersionUri() {
        return URI_KML;
    }

    /**
     * Reads the Kml document assigned to the KmlReader.
     *
     * @return The Kml object mapping the document.
     */
    public Kml read() throws XMLStreamException, KmlException, URISyntaxException {
        Kml root = null;
        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (checkNamespace(eUri)) {
                        if (TAG_KML.equals(eName)) {
                            URI_KML = eUri;
                            Map<String, String> extensionsUris = new HashMap<String, String>();
                            for (int i = 0; i < reader.getNamespaceCount(); i++) {
                                if (reader.getNamespacePrefix(i) != null) {
                                    extensionsUris.put(reader.getNamespaceURI(i), reader.getNamespacePrefix(i));
                                }
                            }
                            root = readKml();
                            root.setExtensionsUris(extensionsUris);
                            if (!useNamespace) {
                                URI_KML = URI_KML_2_1;
                            }
                            root.setVersion(URI_KML);
                        }
                    }
                    break;
                }
            }
        }
        return root;
    }

    private Kml readKml() throws XMLStreamException, KmlException, URISyntaxException {
        NetworkLinkControl networkLinkControl = null;
        Feature abstractFeature = null;
        List<SimpleTypeContainer> kmlSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> kmlObjectExtensions = new ArrayList<Object>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (checkNamespace(eUri)) {
                        if (TAG_NETWORK_LINK_CONTROL.equals(eName)) {
                            networkLinkControl = readNetworkLinkControl();
                        } else if (isAbstractFeature(eName)) {
                            abstractFeature = readAbstractFeature(eName);
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = getComplexExtensionReader(TAG_KML, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_KML, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.KML.equals(extensionLevel)) {
                                kmlObjectExtensions.add(ext);
                            } else if (extensionLevel == null) {
                                if (ext instanceof Feature) {
                                    abstractFeature = (Feature) ext;
                                }
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_KML, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_KML, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.KML.equals(extensionLevel)) {
                                kmlSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_KML.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return KmlReader.KML_FACTORY.createKml(
                networkLinkControl, abstractFeature,
                kmlSimpleExtensions, kmlObjectExtensions);
    }

    private Feature readPlacemark() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractFeature
        String name = null;
        boolean visibility = DEF_VISIBILITY;
        boolean open = DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink link = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        Object snippet = null;
        Object description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        URI styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<AbstractStyleSelector>();
        Region region = null;
        Object extendedData = null;
        List<SimpleTypeContainer> featureSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> featureObjectExtensions = new ArrayList<Object>();

        // Placemark
        AbstractGeometry abstractGeometry = null;
        List<SimpleTypeContainer> placemarkSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> placemarkObjectExtensions = new ArrayList<Object>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_NAME:          name         = reader.getElementText(); break;
                            case TAG_VISIBILITY:    visibility   = parseBoolean(reader.getElementText()); break;
                            case TAG_OPEN:          open         = parseBoolean(reader.getElementText()); break;
                            case TAG_ADDRESS:       address      = reader.getElementText(); break;
                            case TAG_PHONE_NUMBER:  phoneNumber  = reader.getElementText(); break;
                            case TAG_SNIPPET:       snippet      = readElementText(); break;
                            case TAG_SNIPPET_BIG:   snippet      = readSnippet(); break;
                            case TAG_DESCRIPTION:   description  = readElementText(); break;
                            case TAG_STYLE_URL:     styleUrl     = new URI(reader.getElementText()); break;
                            case TAG_REGION:        region       = readRegion(); break;
                            case TAG_EXTENDED_DATA: extendedData = readExtendedData(); break;
                            case TAG_META_DATA:     extendedData = readMetaData(); break;
                            default: {
                                if (isAbstractView(eName)) {
                                    view = readAbstractView(eName);
                                } else if (isAbstractStyleSelector(eName)) {
                                    styleSelector.add(readAbstractStyleSelector(eName));
                                } else if (isAbstractTimePrimitive(eName)) {
                                    timePrimitive = readAbstractTimePrimitive(eName);
                                } else if (isAbstractGeometry(eName)) {
                                    abstractGeometry = readAbstractGeometry(eName);
                                }
                                break;
                            }
                        }
                    } // ATOM
                    else if (URI_ATOM.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = readAtomLink();
                        }
                    } // XAL
                    else if (URI_XAL.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = readXalAddressDetails();
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_PLACEMARK, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_PLACEMARK, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.FEATURE.equals(extensionLevel)) {
                                featureObjectExtensions.add(ext);
                            } else if (Extensions.Names.PLACEMARK.equals(extensionLevel)) {
                                placemarkObjectExtensions.add(ext);
                            } else if (extensionLevel == null) {
                                if (ext instanceof AbstractGeometry) {
                                    abstractGeometry = (AbstractGeometry) ext;
                                }
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_PLACEMARK, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_PLACEMARK, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.FEATURE.equals(extensionLevel)) {
                                featureSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.PLACEMARK.equals(extensionLevel)) {
                                placemarkSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_PLACEMARK.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return KmlReader.KML_FACTORY.createPlacemark(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive, styleUrl,
                styleSelector, region, extendedData, featureSimpleExtensions, featureObjectExtensions,
                abstractGeometry, placemarkSimpleExtensions, placemarkObjectExtensions);
    }

    public Region readRegion() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = readIdAttributes();

        // Region
        LatLonAltBox latLonAltBox = null;
        Lod lod = null;
        List<SimpleTypeContainer> regionSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> regionObjectExtentions = new ArrayList<Object>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        // REGION
                        if (TAG_LAT_LON_ALT_BOX.equals(eName)) {
                            latLonAltBox = readLatLonAltBox();
                        } else if (TAG_LOD.equals(eName)) {
                            lod = readLod();
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_REGION, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_REGION, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.REGION.equals(extensionLevel)) {
                                regionObjectExtentions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_REGION, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_REGION, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.REGION.equals(extensionLevel)) {
                                regionSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_REGION.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createRegion(objectSimpleExtensions, idAttributes,
                latLonAltBox, lod, regionSimpleExtensions, regionObjectExtentions);
    }

    private Lod readLod() throws XMLStreamException, KmlException, URISyntaxException {
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // Lod
        double minLodPixels = DEF_MIN_LOD_PIXELS;
        double maxLodPixels = DEF_MAX_LOD_PIXELS;
        double minFadeExtent = DEF_MIN_FADE_EXTENT;
        double maxFadeExtent = DEF_MAX_FADE_EXTENT;
        List<SimpleTypeContainer> lodSimpleExtentions = new ArrayList<>();
        List<Object> lodObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_MIN_LOD_PIXELS:  minLodPixels  = parseDouble(reader.getElementText());  break;
                            case TAG_MAX_LOD_PIXELS:  maxLodPixels  = parseDouble(reader.getElementText());  break;
                            case TAG_MIN_FADE_EXTENT: minFadeExtent = parseDouble(reader.getElementText()); break;
                            case TAG_MAX_FADE_EXTENT: maxFadeExtent = parseDouble(reader.getElementText()); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_LOD, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LOD, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.LOD.equals(extensionLevel)) {
                                lodObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_LOD, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LOD, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.LOD.equals(extensionLevel)) {
                                lodSimpleExtentions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_LOD.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createLod(objectSimpleExtensions, idAttributes,
                minLodPixels, maxLodPixels, minFadeExtent, maxFadeExtent,
                lodSimpleExtentions, lodObjectExtensions);
    }

    public ExtendedData readExtendedData() throws XMLStreamException, URISyntaxException, KmlException {
        List<Data> datas = new ArrayList<>();
        List<SchemaData> schemaDatas = new ArrayList<>();
        List<Object> anyOtherElements = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        // EXTENDED DATA
                        if (TAG_DATA.equals(eName)) {
                            datas.add(readData());
                        } else if (TAG_SCHEMA_DATA.equals(eName)) {
                            schemaDatas.add(readSchemaData());
                        }
                    } // OTHER FREE DATA
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getDataReader(TAG_EXTENDED_DATA, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_EXTENDED_DATA, eUri, eName);
                            Object ext = result.getKey();
                            anyOtherElements.add(ext);
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_EXTENDED_DATA.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createExtendedData(
                datas, schemaDatas, anyOtherElements);
    }

    @Deprecated
    public Metadata readMetaData() throws XMLStreamException, KmlException, URISyntaxException {
        List<Object> content = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    KmlExtensionReader r;
                    if ((r = this.getDataReader(TAG_META_DATA, eUri, eName)) != null) {
                        Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_META_DATA, eUri, eName);
                        content.add(result.getKey());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_META_DATA.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createMetadata(content);
    }

    private Data readData() throws XMLStreamException {
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // Data
        String name = reader.getAttributeValue(null, ATT_NAME);
        Object displayName = null;
        String value = null;
        List<Object> dataExtensions = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        // REGION
                        if (TAG_DISPLAY_NAME.equals(eName)) {
                            displayName = readElementText();
                        } else if (TAG_VALUE.equals(eName)) {
                            value = reader.getElementText();
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_DATA.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createData(objectSimpleExtensions, idAttributes,
                name, displayName, value, dataExtensions);
    }

    private SchemaData readSchemaData() throws XMLStreamException, URISyntaxException {
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // SchemaData
        URI schemaURL = new URI(reader.getAttributeValue(null, ATT_SCHEMA_URL));
        List<SimpleData> simpleDatas = new ArrayList<>();
        List<Object> schemaDataExtensions = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        // SCHEMA DATA
                        if (TAG_SIMPLE_DATA.equals(eName)) {
                            simpleDatas.add(readSimpleData());
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_SCHEMA_DATA.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createSchemaData(objectSimpleExtensions,
                idAttributes, schemaURL, simpleDatas, schemaDataExtensions);
    }

    private SimpleData readSimpleData() throws XMLStreamException {
        return KmlReader.KML_FACTORY.createSimpleData(
                reader.getAttributeValue(null, ATT_NAME), reader.getElementText());
    }

    private NetworkLinkControl readNetworkLinkControl() throws XMLStreamException, KmlException, URISyntaxException {
        double minRefreshPeriod = DEF_MIN_REFRESH_PERIOD;
        double maxSessionLength = DEF_MAX_SESSION_LENGTH;
        String cookie = null;
        String message = null;
        String linkName = null;
        Object linkDescription = null;
        Snippet linkSnippet = null;
        Calendar expires = null;
        Update update = null;
        AbstractView view = null;
        List<SimpleTypeContainer> networkLinkControlSimpleExtensions = new ArrayList<>();
        List<Object> networkLinkControlObjectExtensions = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_MIN_REFRESH_PERIOD:
                                minRefreshPeriod = parseDouble(reader.getElementText());
                                break;
                            case TAG_MAX_SESSION_LENGTH:
                                checkVersion(URI_KML_2_2);
                                maxSessionLength = parseDouble(reader.getElementText());
                                break;
                            case TAG_COOKIE:
                                cookie = reader.getElementText();
                                break;
                            case TAG_MESSAGE:
                                message = reader.getElementText();
                                break;
                            case TAG_LINK_NAME:
                                linkName = reader.getElementText();
                                break;
                            case TAG_LINK_DESCRIPTION:
                                linkDescription = readElementText();
                                break;
                            case TAG_LINK_SNIPPET:
                                linkSnippet = readSnippet();
                                break;
                            case TAG_EXPIRES:
                                expires = fastDateParser.getCalendar(reader.getElementText());
                                break;
                            case TAG_UPDATE:
                                update = readUpdate();
                                break;
                            default:
                                if (isAbstractView(eName)) {
                                    readAbstractView(eName);
                                }
                                break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_NETWORK_LINK_CONTROL, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_NETWORK_LINK_CONTROL, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.NETWORK_LINK_CONTROL.equals(extensionLevel)) {
                                networkLinkControlObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_NETWORK_LINK_CONTROL, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_NETWORK_LINK_CONTROL, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.NETWORK_LINK_CONTROL.equals(extensionLevel)) {
                                networkLinkControlSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_NETWORK_LINK_CONTROL.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createNetworkLinkControl(minRefreshPeriod, maxSessionLength,
                cookie, message, linkName, linkDescription, linkSnippet, expires, update, view,
                networkLinkControlSimpleExtensions, networkLinkControlObjectExtensions);
    }

    public Update readUpdate() throws XMLStreamException, KmlException, URISyntaxException {
        URI targetHref = null;
        List<Object> updates = new ArrayList<>();
        List<Object> updateOpExtensions = new ArrayList<>();
        List<Object> updateExtensions = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_TARGET_HREF:
                                targetHref = new URI(reader.getElementText());
                                break;
                            case TAG_CREATE:
                                updates.add(readCreate());
                                break;
                            case TAG_DELETE:
                                updates.add(readDelete());
                                break;
                            case TAG_CHANGE:
                                updates.add(readChange());
                                break;
                            case TAG_REPLACE:
                                this.checkVersion(URI_KML_2_1);
                                updates.add(readReplace());
                                break;
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_UPDATE.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createUpdate(targetHref, updates,
                updateOpExtensions, updateExtensions);
    }

    @Deprecated
    private Feature readReplace() throws XMLStreamException, KmlException, URISyntaxException {
        Feature replace = null;
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (equalsNamespace(eUri)) {
                        if (isAbstractFeature(eName)) {
                            replace = readAbstractFeature(eName);
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_REPLACE.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return replace;
    }

    private Create readCreate() throws XMLStreamException, KmlException, URISyntaxException {
        List<Feature> containers = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        if (isAbstractContainer(eName)) {
                            containers.add(readAbstractContainer(eName));
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_CREATE.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createCreate(containers);
    }

    private Delete readDelete() throws XMLStreamException, KmlException, URISyntaxException {
        List<Feature> features = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        if (isAbstractObject(eName)) {
                            features.add(readAbstractFeature(eName));
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_DELETE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_DELETE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (extensionLevel == null) {
                                if (ext instanceof Feature) {
                                    features.add((Feature) ext);
                                }
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_DELETE.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createDelete(features);
    }

    private Change readChange() throws XMLStreamException, KmlException, URISyntaxException {
        List<Object> objects = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        if (isAbstractObject(eName)) {
                            objects.add(readAbstractObject(eName));
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_CHANGE.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createChange(objects);
    }

    private Object readAbstractObject(String eName) throws XMLStreamException, KmlException, URISyntaxException {
        Object resultat = null;
        if (eName != null) switch (eName) {
            case TAG_REGION:        resultat = readRegion();       break;
            case TAG_LOD:           resultat = readLod();          break;
            case TAG_LINK:          resultat = readLink(eName);    break;
            case TAG_ICON:          resultat = readIcon(eName);    break;
            case TAG_LOCATION:      resultat = readLocation();     break;
            case TAG_ORIENTATION:   resultat = readOrientation();  break;
            case TAG_RESOURCE_MAP:  resultat = readResourceMap();  break;
            case TAG_SCHEMA_DATA:   resultat = readSchemaData();   break;
            case TAG_SCALE:         resultat = readScale();        break;
            case TAG_ALIAS:         resultat = readAlias();        break;
            case TAG_VIEW_VOLUME:   resultat = readViewVolume();   break;
            case TAG_IMAGE_PYRAMID: resultat = readImagePyramid(); break;
            case TAG_PAIR:          resultat = readPair();         break;
            case TAG_ITEM_ICON:     resultat = readItemIcon();     break;
            default: {
                if (isAbstractFeature(eName)) {
                    resultat = readAbstractFeature(eName);
                } else if (isAbstractGeometry(eName)) {
                    resultat = readAbstractGeometry(eName);
                } else if (isAbstractStyleSelector(eName)) {
                    resultat = readAbstractStyleSelector(eName);
                } else if (isAbstractSubStyle(eName)) {
                    resultat = readAbstractSubStyle(eName);
                } else if (isAbstractView(eName)) {
                    resultat = readAbstractView(eName);
                } else if (isAbstractTimePrimitive(eName)) {
                    resultat = readAbstractTimePrimitive(eName);
                } else if (isAbstractLatLonBox(eName)) {
                    resultat = readAbstractLatLonBox(eName);
                }
                break;
            }
        }
        return resultat;
    }

    private AbstractLatLonBox readAbstractLatLonBox(String eName)
            throws XMLStreamException, KmlException, URISyntaxException
    {
        AbstractLatLonBox resultat = null;
        if (TAG_LAT_LON_ALT_BOX.equals(eName)) {
            resultat = readLatLonAltBox();
        } else if (TAG_LAT_LON_BOX.equals(eName)) {
            resultat = readLatLonBox();
        }
        return resultat;
    }

    private AbstractSubStyle readAbstractSubStyle(String eName)
            throws XMLStreamException, KmlException, URISyntaxException
    {
        AbstractSubStyle resultat = null;
        if (TAG_BALLOON_STYLE.equals(eName)) {
            resultat = readBalloonStyle();
        } else if (TAG_LIST_STYLE.equals(eName)) {
            resultat = readListStyle();
        } else if (isAbstractColorStyle(eName)) {
            resultat = readAbstractColorStyle(eName);
        }
        return resultat;
    }

    private AbstractColorStyle readAbstractColorStyle(String eName)
            throws XMLStreamException, KmlException, URISyntaxException
    {
        AbstractColorStyle resultat = null;
        if (eName != null) switch (eName) {
            case TAG_ICON_STYLE:  resultat = readIconStyle();  break;
            case TAG_LABEL_STYLE: resultat = readLabelStyle(); break;
            case TAG_POLY_STYLE:  resultat = readPolyStyle();  break;
            case TAG_LINE_STYLE:  resultat = readLineStyle();  break;
        }
        return resultat;
    }

    public Snippet readSnippet() throws XMLStreamException {
        int maxLines = DEF_MAX_SNIPPET_LINES_ATT;
        if (reader.getAttributeValue(null, ATT_MAX_LINES) != null) {
            maxLines = Integer.parseInt(reader.getAttributeValue(null, ATT_MAX_LINES));
        }
        Object content = readElementText();
        return KmlReader.KML_FACTORY.createSnippet(maxLines, content);
    }

    private AbstractGeometry readAbstractGeometry(String eName)
            throws XMLStreamException, KmlException, URISyntaxException
    {
        AbstractGeometry resultat = null;
        if (eName != null) switch (eName) {
            case TAG_MULTI_GEOMETRY: resultat = readMultiGeometry(); break;
            case TAG_LINE_STRING:    resultat = readLineString();    break;
            case TAG_POLYGON:        resultat = readPolygon();       break;
            case TAG_POINT:          resultat = readPoint();         break;
            case TAG_LINEAR_RING:    resultat = readLinearRing();    break;
            case TAG_MODEL:          resultat = readModel();         break;
        }
        return resultat;
    }

    private Polygon readPolygon() throws XMLStreamException, KmlException, URISyntaxException {
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractGeometry
        List<SimpleTypeContainer> abstractGeometrySimpleExtensions = new ArrayList<>();
        List<Object> abstractGeometryObjectExtensions = new ArrayList<>();

        // Polygon
        boolean extrude = DEF_EXTRUDE;
        boolean tessellate = DEF_TESSELLATE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        Boundary outerBoundaryIs = null;
        List<Boundary> innerBoundariesAre = new ArrayList<>();
        List<SimpleTypeContainer> polygonSimpleExtensions = new ArrayList<>();
        List<Object> polygonObjectExtensions = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (equalsNamespace(eUri)) {
                        if (eName != null) switch (eName) {     // POLYGON
                            case TAG_EXTRUDE:           extrude         = parseBoolean(reader.getElementText()); break;
                            case TAG_TESSELLATE:        tessellate      = parseBoolean(reader.getElementText()); break;
                            case TAG_ALTITUDE_MODE:     altitudeMode    = readAltitudeMode();                    break;
                            case TAG_OUTER_BOUNDARY_IS: outerBoundaryIs = readBoundary();                        break;
                            case TAG_INNER_BOUNDARY_IS: innerBoundariesAre.add(readBoundary());                  break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_POLYGON, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_POLYGON, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.GEOMETRY.equals(extensionLevel)) {
                                abstractGeometryObjectExtensions.add(ext);
                            } else if (Extensions.Names.POLYGON.equals(extensionLevel)) {
                                polygonObjectExtensions.add(ext);
                            } else if (extensionLevel == null) {
                                if (ext instanceof AltitudeMode) {
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_POLYGON, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_POLYGON, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.GEOMETRY.equals(extensionLevel)) {
                                abstractGeometrySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.POLYGON.equals(extensionLevel)) {
                                polygonSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_POLYGON.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createPolygon(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode, outerBoundaryIs, innerBoundariesAre,
                polygonSimpleExtensions, polygonObjectExtensions);
    }

    private Boundary readBoundary() throws XMLStreamException, KmlException, URISyntaxException {
        LinearRing linearRing = null;
        List<SimpleTypeContainer> boundarySimpleExtensions = null;
        List<Object> boundaryObjectExtensions = null;
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // BOUNDARY
                    if (equalsNamespace(eUri)) {
                        if (TAG_LINEAR_RING.equals(eName)) {
                            linearRing = readLinearRing();
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_OUTER_BOUNDARY_IS, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_OUTER_BOUNDARY_IS, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.BOUNDARY.equals(extensionLevel)) {
                                boundaryObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getComplexExtensionReader(TAG_INNER_BOUNDARY_IS, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_INNER_BOUNDARY_IS, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.BOUNDARY.equals(extensionLevel)) {
                                boundaryObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_OUTER_BOUNDARY_IS, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_OUTER_BOUNDARY_IS, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.BOUNDARY.equals(extensionLevel)) {
                                boundarySimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_INNER_BOUNDARY_IS, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_INNER_BOUNDARY_IS, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.BOUNDARY.equals(extensionLevel)) {
                                boundarySimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if ((TAG_OUTER_BOUNDARY_IS.equals(reader.getLocalName())
                            || TAG_INNER_BOUNDARY_IS.equals(reader.getLocalName()))
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createBoundary(linearRing, boundarySimpleExtensions, boundaryObjectExtensions);
    }

    public Model readModel() throws XMLStreamException, KmlException, URISyntaxException {
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractGeometry
        List<SimpleTypeContainer> abstractGeometrySimpleExtensions = new ArrayList<>();
        List<Object> abstractGeometryObjectExtensions = new ArrayList<>();

        // Model
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        Location location = null;
        Orientation orientation = null;
        Scale scale = null;
        Link link = null;
        ResourceMap resourceMap = null;
        List<SimpleTypeContainer> modelSimpleExtensions = new ArrayList<>();
        List<Object> modelObjectExtensions = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_ALTITUDE_MODE:    altitudeMode = readAltitudeMode(); break;
                            case TAG_LOCATION:         location     = readLocation();     break;
                            case TAG_ORIENTATION:      orientation  = readOrientation();  break;
                            case TAG_SCALE_BIG:        scale        = readScale();        break;
                            case TAG_LINK:             link         = readLink(eName);    break;
                            case TAG_RESOURCE_MAP:
                                checkVersion(URI_KML_2_2);
                                resourceMap = readResourceMap();
                                break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_MODEL, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_MODEL, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.GEOMETRY.equals(extensionLevel)) {
                                abstractGeometryObjectExtensions.add(ext);
                            } else if (Extensions.Names.MODEL.equals(extensionLevel)) {
                                modelObjectExtensions.add(ext);
                            } else if (extensionLevel == null) {
                                if (ext instanceof AltitudeMode) {
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_MODEL, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_MODEL, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.GEOMETRY.equals(extensionLevel)) {
                                abstractGeometrySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.MODEL.equals(extensionLevel)) {
                                modelSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_MODEL.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createModel(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                altitudeMode, location, orientation, scale, link, resourceMap,
                modelSimpleExtensions, modelObjectExtensions);
    }

    private ResourceMap readResourceMap() throws XMLStreamException, URISyntaxException, KmlException {
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // ResourceMap
        List<Alias> aliases = new ArrayList<>();
        List<SimpleTypeContainer> resourceMapSimpleExtensions = new ArrayList<>();
        List<Object> resourceMapObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (equalsNamespace(eUri)) {
                        if (TAG_ALIAS.equals(eName)) {
                            aliases.add(readAlias());
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_RESOURCE_MAP, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_RESOURCE_MAP, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.RESOURCE_MAP.equals(extensionLevel)) {
                                resourceMapObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_RESOURCE_MAP, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_RESOURCE_MAP, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.RESOURCE_MAP.equals(extensionLevel)) {
                                resourceMapSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_RESOURCE_MAP.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createResourceMap(objectSimpleExtensions, idAttributes,
                aliases, resourceMapSimpleExtensions, resourceMapObjectExtensions);
    }

    private Alias readAlias() throws XMLStreamException, URISyntaxException, KmlException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // Alias
        URI targetHref = null;
        URI sourceHref = null;
        List<SimpleTypeContainer> alaisSimpleExtensions = new ArrayList<>();
        List<Object> aliasObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (equalsNamespace(eUri)) {
                        if (TAG_TARGET_HREF.equals(eName)) {
                            targetHref = new URI(reader.getElementText());
                        } else if (TAG_SOURCE_HREF.equals(eName)) {
                            sourceHref = new URI(reader.getElementText());
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_ALIAS, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_ALIAS, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.ALIAS.equals(extensionLevel)) {
                                aliasObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_ALIAS, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_ALIAS, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.ALIAS.equals(extensionLevel)) {
                                alaisSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_ALIAS.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createAlias(objectSimpleExtensions, idAttributes,
                targetHref, sourceHref, alaisSimpleExtensions, aliasObjectExtensions);
    }

    private Scale readScale() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // Scale
        double x = DEF_X;
        double y = DEF_Y;
        double z = DEF_Z;
        List<SimpleTypeContainer> scaleSimpleExtensions = new ArrayList<>();
        List<Object> scaleObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // SCALE
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_X: x = parseDouble(reader.getElementText()); break;
                            case TAG_Y: y = parseDouble(reader.getElementText()); break;
                            case TAG_Z: z = parseDouble(reader.getElementText()); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_SCALE_BIG, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_SCALE_BIG, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.SCALE.equals(extensionLevel)) {
                                scaleObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_SCALE_BIG, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_SCALE_BIG, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.SCALE.equals(extensionLevel)) {
                                scaleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_SCALE_BIG.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createScale(objectSimpleExtensions, idAttributes,
                x, y, z, scaleSimpleExtensions, scaleObjectExtensions);
    }

    private Location readLocation() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // Location
        double longitude = DEF_LONGITUDE;
        double latitude = DEF_LATITUDE;
        double altitude = DEF_ALTITUDE;
        List<SimpleTypeContainer> locationSimpleExtensions = new ArrayList<>();
        List<Object> locationObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // LOCATION
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_LONGITUDE: longitude = parseDouble(reader.getElementText()); break;
                            case TAG_LATITUDE:  latitude  = parseDouble(reader.getElementText()); break;
                            case TAG_ALTITUDE:  altitude  = parseDouble(reader.getElementText()); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_LOCATION, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LOCATION, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.LOCATION.equals(extensionLevel)) {
                                locationObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_LOCATION, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LOCATION, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.LOCATION.equals(extensionLevel)) {
                                locationSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_LOCATION.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createLocation(objectSimpleExtensions, idAttributes,
                longitude, latitude, altitude, locationSimpleExtensions, locationObjectExtensions);
    }

    private Orientation readOrientation() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // Orientation
        double heading = DEF_HEADING;
        double tilt = DEF_TILT;
        double roll = DEF_ROLL;
        List<SimpleTypeContainer> orientationSimpleExtensions = new ArrayList<>();
        List<Object> orientationObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // ORIENTATION
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_HEADING: heading = parseDouble(reader.getElementText()); break;
                            case TAG_TILT:    tilt    = parseDouble(reader.getElementText()); break;
                            case TAG_ROLL:    roll    = parseDouble(reader.getElementText()); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_ORIENTATION, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_ORIENTATION, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.ORIENTATION.equals(extensionLevel)) {
                                orientationObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_ORIENTATION, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_ORIENTATION, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.ORIENTATION.equals(extensionLevel)) {
                                orientationSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_ORIENTATION.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createOrientation(objectSimpleExtensions,
                idAttributes, heading, tilt, roll, orientationSimpleExtensions,
                orientationObjectExtensions);
    }

    private LinearRing readLinearRing() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractGeometry
        List<SimpleTypeContainer> abstractGeometrySimpleExtensions = new ArrayList<>();
        List<Object> abstractGeometryObjectExtensions = new ArrayList<>();

        // LinearRing
        boolean extrude = DEF_EXTRUDE;
        boolean tessellate = DEF_TESSELLATE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        CoordinateSequence coordinates = null;
        List<SimpleTypeContainer> linearRingSimpleExtensions = new ArrayList<>();
        List<Object> linearRingObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // LINEAR RING
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_EXTRUDE:       extrude      = parseBoolean(reader.getElementText()); break;
                            case TAG_TESSELLATE:    tessellate   = parseBoolean(reader.getElementText()); break;
                            case TAG_ALTITUDE_MODE: altitudeMode = readAltitudeMode(); break;
                            case TAG_COORDINATES:   coordinates  = readCoordinates(reader.getElementText()); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_LINEAR_RING, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LINEAR_RING, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.GEOMETRY.equals(extensionLevel)) {
                                abstractGeometryObjectExtensions.add(ext);
                            } else if (Extensions.Names.LINEAR_RING.equals(extensionLevel)) {
                                linearRingObjectExtensions.add(ext);
                            } else if (extensionLevel == null) {
                                if (ext instanceof AltitudeMode) {
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_LINEAR_RING, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LINEAR_RING, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.GEOMETRY.equals(extensionLevel)) {
                                abstractGeometrySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.LINEAR_RING.equals(extensionLevel)) {
                                linearRingSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_LINEAR_RING.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createLinearRing(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode, coordinates,
                linearRingSimpleExtensions, linearRingObjectExtensions);
    }

    private LineString readLineString() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractGeometry
        List<SimpleTypeContainer> abstractGeometrySimpleExtensions = new ArrayList<>();
        List<Object> abstractGeometryObjectExtensions = new ArrayList<>();

        // LineString
        boolean extrude = DEF_EXTRUDE;
        boolean tessellate = DEF_TESSELLATE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        CoordinateSequence coordinates = null;
        List<SimpleTypeContainer> lineStringSimpleExtensions = new ArrayList<>();
        List<Object> lineStringObjectExtensions = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // LINE STRING
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_EXTRUDE:       extrude      = parseBoolean(reader.getElementText()); break;
                            case TAG_TESSELLATE:    tessellate   = parseBoolean(reader.getElementText()); break;
                            case TAG_ALTITUDE_MODE: altitudeMode = readAltitudeMode(); break;
                            case TAG_COORDINATES:   coordinates  = readCoordinates(reader.getElementText()); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_LINE_STRING, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LINE_STRING, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.GEOMETRY.equals(extensionLevel)) {
                                abstractGeometryObjectExtensions.add(ext);
                            } else if (Extensions.Names.LINE_STRING.equals(extensionLevel)) {
                                lineStringObjectExtensions.add(ext);
                            } else if (extensionLevel == null) {
                                if (ext instanceof AltitudeMode) {
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_LINE_STRING, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LINE_STRING, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.GEOMETRY.equals(extensionLevel)) {
                                abstractGeometrySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.LINE_STRING.equals(extensionLevel)) {
                                lineStringSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_LINE_STRING.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createLineString(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode, coordinates,
                lineStringSimpleExtensions, lineStringObjectExtensions);
    }

    private MultiGeometry readMultiGeometry() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractGeometry
        List<SimpleTypeContainer> abstractGeometrySimpleExtensions = new ArrayList<>();
        List<Object> abstractGeometryObjectExtensions = new ArrayList<>();

        // Multi Geometry
        List<AbstractGeometry> geometries = new ArrayList<>();
        List<SimpleTypeContainer> multiGeometrySimpleExtensions = new ArrayList<>();
        List<Object> multiGeometryObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (equalsNamespace(eUri)) {
                        if (isAbstractGeometry(eName)) {
                            geometries.add(readAbstractGeometry(eName));
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_MULTI_GEOMETRY, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_MULTI_GEOMETRY, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.GEOMETRY.equals(extensionLevel)) {
                                abstractGeometryObjectExtensions.add(ext);
                            } else if (Extensions.Names.MULTI_GEOMETRY.equals(extensionLevel)) {
                                multiGeometryObjectExtensions.add(ext);
                            } else if (extensionLevel == null) {
                                if (ext instanceof AbstractGeometry) {
                                    geometries.add((AbstractGeometry) ext);
                                }
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_MULTI_GEOMETRY, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_MULTI_GEOMETRY, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.GEOMETRY.equals(extensionLevel)) {
                                abstractGeometrySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.MULTI_GEOMETRY.equals(extensionLevel)) {
                                multiGeometrySimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_MULTI_GEOMETRY.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createMultiGeometry(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                geometries, multiGeometrySimpleExtensions, multiGeometryObjectExtensions);
    }

    private Feature readAbstractFeature(String eName) throws XMLStreamException, KmlException, URISyntaxException {
        Feature resultat = null;
        if (isAbstractContainer(eName)) {
            resultat = readAbstractContainer(eName);
        } else if (isAbstractOverlay(eName)) {
            resultat = readAbstractOverlay(eName);
        } else if (TAG_NETWORK_LINK.equals(eName)) {
            resultat = readNetworkLink();
        } else if (TAG_PLACEMARK.equals(eName)) {
            resultat = readPlacemark();
        }
        return resultat;
    }

    private Feature readAbstractOverlay(String eName) throws XMLStreamException, KmlException, URISyntaxException {
        Feature resultat = null;
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

    private Feature readGroundOverlay() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractFeature
        String name = null;
        boolean visibility = DEF_VISIBILITY;
        boolean open = DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink link = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        Object snippet = null;
        Object description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        URI styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<>();
        Region region = null;
        Object extendedData = null;
        List<SimpleTypeContainer> featureSimpleExtensions = new ArrayList<>();
        List<Object> featureObjectExtensions = new ArrayList<>();

        // AbstractOverlay
        Color color = DEF_COLOR;
        int drawOrder = DEF_DRAW_ORDER;
        Icon icon = null;
        List<SimpleTypeContainer> abstractOverlaySimpleExtensions = new ArrayList<>();
        List<Object> abstractOverlayObjectExtensions = new ArrayList<>();

        // GroundOverlay
        double altitude = DEF_ALTITUDE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        LatLonBox latLonBox = null;
        List<SimpleTypeContainer> groundOverlaySimpleExtensions = new ArrayList<>();
        List<Object> groundOverlayObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_NAME:          name         = reader.getElementText(); break;
                            case TAG_VISIBILITY:    visibility   = parseBoolean(reader.getElementText()); break;
                            case TAG_OPEN:          open         = parseBoolean(reader.getElementText()); break;
                            case TAG_ADDRESS:       address      = reader.getElementText(); break;
                            case TAG_PHONE_NUMBER:  phoneNumber  = reader.getElementText(); break;
                            case TAG_SNIPPET:       snippet      = readElementText(); break;
                            case TAG_SNIPPET_BIG:   snippet      = readSnippet(); break;
                            case TAG_DESCRIPTION:   description  = readElementText(); break;
                            case TAG_STYLE_URL:     styleUrl     = new URI(reader.getElementText()); break;
                            case TAG_REGION:        region       = readRegion(); break;
                            case TAG_EXTENDED_DATA: extendedData = readExtendedData(); break;
                            case TAG_META_DATA:     extendedData = readMetaData(); break;
                            case TAG_COLOR:         color        = KmlUtilities.parseColor(reader.getElementText()); break;
                            case TAG_DRAW_ORDER:    drawOrder    = Integer.parseInt(reader.getElementText()); break;
                            case TAG_ICON:          icon         = readIcon(eName); break;
                            case TAG_ALTITUDE:      altitude     = parseDouble(reader.getElementText()); break;
                            case TAG_ALTITUDE_MODE: altitudeMode = readAltitudeMode(); break;
                            case TAG_LAT_LON_BOX:   latLonBox    = readLatLonBox(); break;
                            default: {
                                if (isAbstractView(eName)) {
                                    view = readAbstractView(eName);
                                } else if (isAbstractTimePrimitive(eName)) {
                                    timePrimitive = readAbstractTimePrimitive(eName);
                                } else if (isAbstractStyleSelector(eName)) {
                                    styleSelector.add(readAbstractStyleSelector(eName));
                                }
                                break;
                            }
                        }
                    } else if (URI_ATOM.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = readAtomLink();
                        }
                    } else if (URI_XAL.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = readXalAddressDetails();
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_GROUND_OVERLAY, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_GROUND_OVERLAY, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.FEATURE.equals(extensionLevel)) {
                                featureObjectExtensions.add(ext);
                            } else if (Extensions.Names.OVERLAY.equals(extensionLevel)) {
                                abstractOverlayObjectExtensions.add(ext);
                            } else if (Extensions.Names.GROUND_OVERLAY.equals(extensionLevel)) {
                                groundOverlayObjectExtensions.add(ext);
                            } else if (extensionLevel == null) {
                                if (ext instanceof AltitudeMode) {
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_GROUND_OVERLAY, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_GROUND_OVERLAY, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.FEATURE.equals(extensionLevel)) {
                                featureSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.OVERLAY.equals(extensionLevel)) {
                                abstractOverlaySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.GROUND_OVERLAY.equals(extensionLevel)) {
                                groundOverlaySimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_GROUND_OVERLAY.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createGroundOverlay(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link,
                address, addressDetails, phoneNumber, snippet, description, view, timePrimitive,
                styleUrl, styleSelector, region, extendedData,
                featureSimpleExtensions, featureObjectExtensions,
                color, drawOrder, icon,
                abstractOverlaySimpleExtensions, abstractOverlayObjectExtensions,
                altitude, altitudeMode, latLonBox,
                groundOverlaySimpleExtensions, groundOverlayObjectExtensions);
    }

    private Link readLink(String stopName) throws XMLStreamException, KmlException, URISyntaxException {

        // Comme BasicLink
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        String href = null;
        List<SimpleTypeContainer> basicLinkSimpleExtensions = new ArrayList<>();
        List<Object> basicLinkObjectExtensions = new ArrayList<>();

        // Spécifique à Link
        RefreshMode refreshMode = DEF_REFRESH_MODE;
        double refreshInterval = DEF_REFRESH_INTERVAL;
        ViewRefreshMode viewRefreshMode = DEF_VIEW_REFRESH_MODE;
        double viewRefreshTime = DEF_VIEW_REFRESH_TIME;
        double viewBoundScale = DEF_VIEW_BOUND_SCALE;
        String viewFormat = null;
        String httpQuery = null;
        List<SimpleTypeContainer> linkSimpleExtensions = new ArrayList<>();
        List<Object> linkObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_HREF:              href            = reader.getElementText(); break;
                            case TAG_REFRESH_MODE:      refreshMode     = RefreshMode.transform(reader.getElementText()); break;
                            case TAG_REFRESH_INTERVAL:  refreshInterval = parseDouble(reader.getElementText()); break;
                            case TAG_VIEW_REFRESH_MODE: viewRefreshMode = ViewRefreshMode.transform(reader.getElementText()); break;
                            case TAG_VIEW_REFRESH_TIME: viewRefreshTime = parseDouble(reader.getElementText()); break;
                            case TAG_VIEW_BOUND_SCALE:  viewBoundScale  = parseDouble(reader.getElementText()); break;
                            case TAG_VIEW_FORMAT:       viewFormat      = reader.getElementText(); break;
                            case TAG_HTTP_QUERY:        httpQuery       = reader.getElementText(); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(stopName, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, stopName, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.BASIC_LINK.equals(extensionLevel)) {
                                basicLinkObjectExtensions.add(ext);
                            } else if (Extensions.Names.LINK.equals(extensionLevel)) {
                                linkObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(stopName, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, stopName, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.BASIC_LINK.equals(extensionLevel)) {
                                basicLinkSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.LINK.equals(extensionLevel)) {
                                linkSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (stopName.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createLink(objectSimpleExtensions, idAttributes,
                href, basicLinkSimpleExtensions, basicLinkObjectExtensions,
                refreshMode, refreshInterval, viewRefreshMode, viewRefreshTime,
                viewBoundScale, viewFormat, httpQuery,
                linkSimpleExtensions, linkObjectExtensions);
    }

    private Icon readIcon(String stopName) throws XMLStreamException, KmlException, URISyntaxException {
        return KmlReader.KML_FACTORY.createIcon(readLink(stopName));
    }

    @Deprecated
    private Url readUrl(String stopName) throws XMLStreamException, KmlException, URISyntaxException {
        return KmlReader.KML_FACTORY.createUrl(readLink(stopName));
    }

    private LatLonBox readLatLonBox() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractLatLonBox
        double north = DEF_NORTH;
        double south = DEF_SOUTH;
        double east = DEF_EAST;
        double west = DEF_WEST;
        List<SimpleTypeContainer> abstractLatLonBoxSimpleExtensions = new ArrayList<>();
        List<Object> abstractLatLonBoxObjectExtensions = new ArrayList<>();

        // LatLonBox
        double rotation = DEF_ROTATION;
        List<SimpleTypeContainer> latLonBoxSimpleExtensions = new ArrayList<>();
        List<Object> latLonBoxObjectExtensions = new ArrayList<>();

        boucle:
        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_NORTH: north = parseDouble(reader.getElementText()); break;
                            case TAG_SOUTH: south = parseDouble(reader.getElementText()); break;
                            case TAG_EAST:  east  = parseDouble(reader.getElementText()); break;
                            case TAG_WEST:  west  = parseDouble(reader.getElementText()); break;
                            case TAG_ROTATION: rotation = parseDouble(reader.getElementText()); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_LAT_LON_BOX, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LAT_LON_BOX, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.ABSTRACT_LAT_LON_BOX.equals(extensionLevel)) {
                                abstractLatLonBoxObjectExtensions.add(ext);
                            } else if (Extensions.Names.LAT_LON_BOX.equals(extensionLevel)) {
                                latLonBoxObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_LAT_LON_BOX, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LAT_LON_BOX, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.ABSTRACT_LAT_LON_BOX.equals(extensionLevel)) {
                                abstractLatLonBoxSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.LAT_LON_BOX.equals(extensionLevel)) {
                                latLonBoxSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LAT_LON_BOX.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return KmlReader.KML_FACTORY.createLatLonBox(objectSimpleExtensions, idAttributes,
                north, south, east, west,
                abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions,
                rotation, latLonBoxSimpleExtensions, latLonBoxObjectExtensions);
    }

    private LatLonAltBox readLatLonAltBox() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractLatLonBox
        double north = DEF_NORTH;
        double south = DEF_SOUTH;
        double east = DEF_EAST;
        double west = DEF_WEST;
        List<SimpleTypeContainer> abstractLatLonBoxSimpleExtensions = new ArrayList<>();
        List<Object> abstractLatLonBoxObjectExtensions = new ArrayList<>();

        // LatLonAltBox
        double minAltitude = DEF_MIN_ALTITUDE;
        double maxAltitude = DEF_MAX_ALTITUDE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        List<SimpleTypeContainer> latLonAltBoxSimpleExtensions = new ArrayList<>();
        List<Object> latLonAltBoxObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_NORTH: north = parseDouble(reader.getElementText()); break;
                            case TAG_SOUTH: south = parseDouble(reader.getElementText()); break;
                            case TAG_EAST:  east  = parseDouble(reader.getElementText()); break;
                            case TAG_WEST:  west =  parseDouble(reader.getElementText()); break;
                            case TAG_MIN_ALTITUDE: minAltitude = parseDouble(reader.getElementText()); break;
                            case TAG_MAX_ALTITUDE: maxAltitude = parseDouble(reader.getElementText()); break;
                            case TAG_ALTITUDE_MODE: altitudeMode = readAltitudeMode(); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_LAT_LON_ALT_BOX, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LAT_LON_ALT_BOX, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.ABSTRACT_LAT_LON_BOX.equals(extensionLevel)) {
                                abstractLatLonBoxObjectExtensions.add(ext);
                            } else if (Extensions.Names.LAT_LON_ALT_BOX.equals(extensionLevel)) {
                                latLonAltBoxObjectExtensions.add(ext);
                            } else if (extensionLevel == null) {
                                if (ext instanceof AltitudeMode) {
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_LAT_LON_ALT_BOX, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LAT_LON_ALT_BOX, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.ABSTRACT_LAT_LON_BOX.equals(extensionLevel)) {
                                abstractLatLonBoxSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.LAT_LON_ALT_BOX.equals(extensionLevel)) {
                                latLonAltBoxSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_LAT_LON_ALT_BOX.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createLatLonAltBox(objectSimpleExtensions,
                idAttributes, north, south, east, west,
                abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions,
                minAltitude, maxAltitude, altitudeMode,
                latLonAltBoxSimpleExtensions, latLonAltBoxObjectExtensions);
    }

    private ImagePyramid readImagePyramid() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // ImagePyramid
        int titleSize = DEF_TITLE_SIZE;
        int maxWidth = DEF_MAX_WIDTH;
        int maxHeight = DEF_MAX_HEIGHT;
        GridOrigin gridOrigin = DEF_GRID_ORIGIN;
        List<SimpleTypeContainer> imagePyramidSimpleExtensions = new ArrayList<>();
        List<Object> imagePyramidObjectExtensions = new ArrayList<>();

        boucle:
        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_TITLE_SIZE: titleSize = Integer.parseInt(reader.getElementText()); break;
                            case TAG_MAX_WIDTH:  maxWidth = Integer.parseInt(reader.getElementText()); break;
                            case TAG_MAX_HEIGHT: maxHeight = Integer.parseInt(reader.getElementText()); break;
                            case TAG_GRID_ORIGIN: gridOrigin = GridOrigin.transform(reader.getElementText()); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_IMAGE_PYRAMID, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_IMAGE_PYRAMID, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.IMAGE_PYRAMID.equals(extensionLevel)) {
                                imagePyramidObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_IMAGE_PYRAMID, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_IMAGE_PYRAMID, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.IMAGE_PYRAMID.equals(extensionLevel)) {
                                imagePyramidSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_IMAGE_PYRAMID.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createImagePyramid(objectSimpleExtensions, idAttributes,
                titleSize, maxWidth, maxHeight, gridOrigin,
                imagePyramidSimpleExtensions, imagePyramidObjectExtensions);
    }

    private ViewVolume readViewVolume() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // ViewVolume
        double leftFov = DEF_LEFT_FOV;
        double rightFov = DEF_RIGHT_FOV;
        double bottomFov = DEF_BOTTOM_FOV;
        double topFov = DEF_TOP_FOV;
        double near = DEF_NEAR;
        List<SimpleTypeContainer> viewVolumeSimpleExtensions = new ArrayList<>();
        List<Object> viewVolumeObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_LEFT_FOV:   leftFov = parseDouble(reader.getElementText()); break;
                            case TAG_RIGHT_FOV:  rightFov = parseDouble(reader.getElementText()); break;
                            case TAG_BOTTOM_FOV: bottomFov = parseDouble(reader.getElementText()); break;
                            case TAG_TOP_FOV:    topFov = parseDouble(reader.getElementText()); break;
                            case TAG_NEAR:       near = parseDouble(reader.getElementText()); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_VIEW_VOLUME, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_VIEW_VOLUME, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.VIEW_VOLUME.equals(extensionLevel)) {
                                viewVolumeObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_VIEW_VOLUME, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_VIEW_VOLUME, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.VIEW_VOLUME.equals(extensionLevel)) {
                                viewVolumeSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_VIEW_VOLUME.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createViewVolume(objectSimpleExtensions, idAttributes,
                leftFov, rightFov, bottomFov, topFov, near,
                viewVolumeSimpleExtensions, viewVolumeObjectExtensions);
    }

    private Feature readPhotoOverlay() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractFeature
        String name = null;
        boolean visibility = DEF_VISIBILITY;
        boolean open = DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink link = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        Object snippet = null;
        Object description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        URI styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<>();
        Region region = null;
        Object extendedData = null;
        List<SimpleTypeContainer> featureSimpleExtensions = new ArrayList<>();
        List<Object> featureObjectExtensions = new ArrayList<>();

        // AbstractOverlay
        Color color = DEF_COLOR;
        int drawOrder = DEF_DRAW_ORDER;
        Icon icon = null;
        List<SimpleTypeContainer> abstractOverlaySimpleExtensions = new ArrayList<>();
        List<Object> abstractOverlayObjectExtensions = new ArrayList<>();

        // PhotoOverlay
        double rotation = DEF_ROTATION;
        ViewVolume viewVolume = null;
        ImagePyramid imagePyramid = null;
        Point point = null;
        Shape shape = null;
        List<SimpleTypeContainer> photoOverlaySimpleExtensions = new ArrayList<>();
        List<Object> photoOverlayObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_NAME:          name = reader.getElementText(); break;
                            case TAG_VISIBILITY:    visibility = parseBoolean(reader.getElementText()); break;
                            case TAG_OPEN:          open = parseBoolean(reader.getElementText()); break;
                            case TAG_ADDRESS:       address = reader.getElementText(); break;
                            case TAG_PHONE_NUMBER:  phoneNumber = reader.getElementText(); break;
                            case TAG_SNIPPET:       snippet = readElementText(); break;
                            case TAG_SNIPPET_BIG:   snippet = readSnippet(); break;
                            case TAG_DESCRIPTION:   description = readElementText(); break;
                            case TAG_STYLE_URL:     styleUrl = new URI(reader.getElementText()); break;
                            case TAG_REGION:        region = readRegion(); break;
                            case TAG_EXTENDED_DATA: extendedData = readExtendedData(); break;
                            case TAG_META_DATA:     extendedData = readMetaData(); break;
                            case TAG_COLOR:         color = KmlUtilities.parseColor(reader.getElementText()); break;
                            case TAG_DRAW_ORDER:    drawOrder = Integer.parseInt(reader.getElementText()); break;
                            case TAG_ICON:          icon = readIcon(eName); break;
                            case TAG_ROTATION:      rotation = parseDouble(reader.getElementText()); break;
                            case TAG_VIEW_VOLUME:   viewVolume = readViewVolume(); break;
                            case TAG_IMAGE_PYRAMID: imagePyramid = readImagePyramid(); break;
                            case TAG_POINT:         point = readPoint(); break;
                            case TAG_SHAPE:         shape = Shape.transform(reader.getElementText()); break;
                            default: {
                                if (isAbstractStyleSelector(eName)) {
                                    styleSelector.add(readAbstractStyleSelector(eName));
                                } else if (isAbstractView(eName)) {
                                    view = readAbstractView(eName);
                                } else if (isAbstractTimePrimitive(eName)) {
                                    timePrimitive = readAbstractTimePrimitive(eName);
                                }
                                break;
                            }
                        }
                    } // ATOM
                    else if (URI_ATOM.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = readAtomLink();
                        }
                    } // XAL
                    else if (URI_XAL.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = readXalAddressDetails();
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_PHOTO_OVERLAY, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_PHOTO_OVERLAY, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.FEATURE.equals(extensionLevel)) {
                                featureObjectExtensions.add(ext);
                            } else if (Extensions.Names.OVERLAY.equals(extensionLevel)) {
                                abstractOverlayObjectExtensions.add(ext);
                            } else if (Extensions.Names.PHOTO_OVERLAY.equals(extensionLevel)) {
                                photoOverlayObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_PHOTO_OVERLAY, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_PHOTO_OVERLAY, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.FEATURE.equals(extensionLevel)) {
                                featureSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.OVERLAY.equals(extensionLevel)) {
                                abstractOverlaySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.PHOTO_OVERLAY.equals(extensionLevel)) {
                                photoOverlaySimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_PHOTO_OVERLAY.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createPhotoOverlay(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber,
                snippet, description, view, timePrimitive, styleUrl, styleSelector, region, extendedData,
                abstractOverlaySimpleExtensions, abstractOverlayObjectExtensions,
                color, drawOrder, icon, abstractOverlaySimpleExtensions, abstractOverlayObjectExtensions,
                rotation, viewVolume, imagePyramid, point, shape,
                photoOverlaySimpleExtensions, photoOverlayObjectExtensions);
    }

    private Feature readScreenOverlay() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractFeature
        String name = null;
        boolean visibility = DEF_VISIBILITY;
        boolean open = DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink link = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        Object snippet = null;
        Object description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        URI styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<>();
        Region region = null;
        Object extendedData = null;
        List<SimpleTypeContainer> featureSimpleExtensions = new ArrayList<>();
        List<Object> featureObjectExtensions = new ArrayList<>();

        // AbstractOverlay
        Color color = DEF_COLOR;
        int drawOrder = DEF_DRAW_ORDER;
        Icon icon = null;
        List<SimpleTypeContainer> abstractOverlaySimpleExtensions = new ArrayList<>();
        List<Object> abstractOverlayObjectExtensions = new ArrayList<>();

        // ScreenOverlay
        Vec2 overlayXY = null;
        Vec2 screenXY = null;
        Vec2 rotationXY = null;
        Vec2 size = null;
        double rotation = DEF_ROTATION;
        List<SimpleTypeContainer> screenOverlaySimpleExtensions = new ArrayList<>();
        List<Object> screenOverlayObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_NAME:          name = reader.getElementText(); break;
                            case TAG_VISIBILITY:    visibility = parseBoolean(reader.getElementText()); break;
                            case TAG_OPEN:          open = parseBoolean(reader.getElementText()); break;
                            case TAG_ADDRESS:       address = reader.getElementText(); break;
                            case TAG_PHONE_NUMBER:  phoneNumber = reader.getElementText(); break;
                            case TAG_SNIPPET:       snippet = readElementText(); break;
                            case TAG_SNIPPET_BIG:   snippet = readSnippet(); break;
                            case TAG_DESCRIPTION:   description = readElementText(); break;
                            case TAG_STYLE_URL:     styleUrl = new URI(reader.getElementText()); break;
                            case TAG_REGION:        region = readRegion(); break;
                            case TAG_EXTENDED_DATA: extendedData = readExtendedData(); break;
                            case TAG_META_DATA:     extendedData = readMetaData(); break;
                            case TAG_COLOR:         color = KmlUtilities.parseColor(reader.getElementText()); break;
                            case TAG_DRAW_ORDER:    drawOrder = Integer.parseInt(reader.getElementText()); break;
                            case TAG_ICON:          icon = readIcon(eName); break;
                            case TAG_OVERLAY_XY:    overlayXY = readVec2(eName); break;
                            case TAG_SCREEN_XY:     screenXY = readVec2(eName); break;
                            case TAG_ROTATION_XY:   rotationXY = readVec2(eName); break;
                            case TAG_SIZE:          size = readVec2(eName); break;
                            case TAG_ROTATION:      rotation = parseDouble(reader.getElementText()); break;
                            default: {
                                if (isAbstractStyleSelector(eName)) {
                                    styleSelector.add(readAbstractStyleSelector(eName));
                                } else if (isAbstractView(eName)) {
                                    view = readAbstractView(eName);
                                } else if (isAbstractTimePrimitive(eName)) {
                                    timePrimitive = readAbstractTimePrimitive(eName);
                                }
                                break;
                            }
                        }
                    } // ATOM
                    else if (URI_ATOM.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = readAtomLink();
                        }
                    } // XAL
                    else if (URI_XAL.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = readXalAddressDetails();
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_SCREEN_OVERLAY, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_SCREEN_OVERLAY, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.FEATURE.equals(extensionLevel)) {
                                featureObjectExtensions.add(ext);
                            } else if (Extensions.Names.OVERLAY.equals(extensionLevel)) {
                                abstractOverlayObjectExtensions.add(ext);
                            } else if (Extensions.Names.SCREEN_OVERLAY.equals(extensionLevel)) {
                                screenOverlayObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_SCREEN_OVERLAY, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_SCREEN_OVERLAY, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.FEATURE.equals(extensionLevel)) {
                                featureSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.OVERLAY.equals(extensionLevel)) {
                                abstractOverlaySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.SCREEN_OVERLAY.equals(extensionLevel)) {
                                screenOverlaySimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_SCREEN_OVERLAY.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createScreenOverlay(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive, styleUrl, styleSelector,
                region, extendedData, featureSimpleExtensions, featureObjectExtensions,
                color, drawOrder, icon, abstractOverlaySimpleExtensions, abstractOverlayObjectExtensions,
                overlayXY, screenXY, rotationXY, size, rotation,
                screenOverlaySimpleExtensions, screenOverlayObjectExtensions);
    }

    private Feature readAbstractContainer(String eName) throws XMLStreamException, KmlException, URISyntaxException {
        Feature resultat = null;
        if (TAG_FOLDER.equals(eName)) {
            resultat = readFolder();
        } else if (TAG_DOCUMENT.equals(eName)) {
            resultat = readDocument();
        }
        return resultat;
    }

    public AbstractView readAbstractView(String eName) throws XMLStreamException, KmlException, URISyntaxException {
        AbstractView resultat = null;
        if (TAG_LOOK_AT.equals(eName)) {
            resultat = readLookAt();
        } else if (TAG_CAMERA.equals(eName)) {
            resultat = readCamera();
        }
        return resultat;
    }

    private LookAt readLookAt() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractView
        List<SimpleTypeContainer> abstractViewSimpleExtensions = new ArrayList<>();
        List<Object> abstractViewObjectExtensions = new ArrayList<>();

        // LookAt
        double longitude = DEF_LONGITUDE;
        double latitude = DEF_LATITUDE;
        double altitude = DEF_ALTITUDE;
        double heading = DEF_HEADING;
        double tilt = DEF_TILT;
        double range = DEF_RANGE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        List<SimpleTypeContainer> lookAtSimpleExtensions = new ArrayList<>();
        List<Object> lookAtObjectExtensions = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_LONGITUDE: longitude = parseDouble(reader.getElementText()); break;
                            case TAG_LATITUDE:  latitude = parseDouble(reader.getElementText()); break;
                            case TAG_ALTITUDE:  altitude = parseDouble(reader.getElementText()); break;
                            case TAG_HEADING:   heading = parseDouble(reader.getElementText()); break;
                            case TAG_TILT:
                                if (checkVersionSimple(URI_KML_2_1)) {
                                    tilt = KmlUtilities.checkAnglePos90(parseDouble(reader.getElementText()));
                                } else {
                                    tilt = parseDouble(reader.getElementText());
                                }
                                break;
                            case TAG_RANGE: range = parseDouble(reader.getElementText()); break;
                            case TAG_ALTITUDE_MODE: altitudeMode = readAltitudeMode(); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_LOOK_AT, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LOOK_AT, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.VIEW.equals(extensionLevel)) {
                                abstractViewObjectExtensions.add(ext);
                            } else if (Extensions.Names.LOOK_AT.equals(extensionLevel)) {
                                lookAtObjectExtensions.add(ext);
                            } else if (extensionLevel == null) {
                                if (ext instanceof AltitudeMode) {
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_LOOK_AT, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LOOK_AT, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.VIEW.equals(extensionLevel)) {
                                abstractViewSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.LOOK_AT.equals(extensionLevel)) {
                                lookAtSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_LOOK_AT.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createLookAt(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, range, altitudeMode,
                lookAtSimpleExtensions, lookAtObjectExtensions);
    }

    private Camera readCamera() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractView
        List<SimpleTypeContainer> abstractViewSimpleExtensions = new ArrayList<>();
        List<Object> abstractViewObjectExtensions = new ArrayList<>();

        // Camera
        double longitude = DEF_LONGITUDE;
        double latitude = DEF_LATITUDE;
        double altitude = DEF_ALTITUDE;
        double heading = DEF_HEADING;
        double tilt = DEF_TILT;
        double roll = DEF_ROLL;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        List<SimpleTypeContainer> cameraSimpleExtensions = new ArrayList<>();
        List<Object> cameraObjectExtensions = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_LONGITUDE:  longitude = parseDouble(reader.getElementText()); break;
                            case TAG_LATITUDE:   latitude  = parseDouble(reader.getElementText()); break;
                            case TAG_ALTITUDE:   altitude  = parseDouble(reader.getElementText()); break;
                            case TAG_HEADING:    heading   = parseDouble(reader.getElementText()); break;
                            case TAG_TILT:       tilt      = parseDouble(reader.getElementText()); break;
                            case TAG_ROLL:       roll      = parseDouble(reader.getElementText()); break;
                            case TAG_ALTITUDE_MODE: altitudeMode = readAltitudeMode(); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_CAMERA, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_CAMERA, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.VIEW.equals(extensionLevel)) {
                                abstractViewObjectExtensions.add(ext);
                            } else if (Extensions.Names.CAMERA.equals(extensionLevel)) {
                                cameraObjectExtensions.add(ext);
                            } else if (extensionLevel == null) {
                                if (ext instanceof AltitudeMode) {
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_CAMERA, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_CAMERA, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.VIEW.equals(extensionLevel)) {
                                abstractViewSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.CAMERA.equals(extensionLevel)) {
                                cameraSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_CAMERA.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createCamera(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, roll, altitudeMode,
                cameraSimpleExtensions, cameraObjectExtensions);
    }

    public AbstractStyleSelector readAbstractStyleSelector(String eName)
            throws XMLStreamException, KmlException, URISyntaxException
    {
        AbstractStyleSelector resultat = null;
        if (TAG_STYLE.equals(eName)) {
            resultat = readStyle();
        } else if (TAG_STYLE_MAP.equals(eName)) {
            resultat = readStyleMap();
        }
        return resultat;
    }

    private StyleMap readStyleMap() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractStyleSelector
        List<SimpleTypeContainer> styleSelectorSimpleExtensions = new ArrayList<>();
        List<Object> styleSelectorObjectExtensions = new ArrayList<>();

        // StyleMap
        List<Pair> pairs = new ArrayList<>();
        List<SimpleTypeContainer> styleMapSimpleExtensions = new ArrayList<>();
        List<Object> styleMapObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (equalsNamespace(eUri)) {
                        // STYLE MAP
                        if (TAG_PAIR.equals(eName)) {
                            pairs.add(readPair());
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_STYLE_MAP, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_STYLE_MAP, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.STYLE_SELECTOR.equals(extensionLevel)) {
                                styleSelectorObjectExtensions.add(ext);
                            } else if (Extensions.Names.STYLE_MAP.equals(extensionLevel)) {
                                styleMapObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_STYLE_MAP, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_STYLE_MAP, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.STYLE_SELECTOR.equals(extensionLevel)) {
                                styleSelectorSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.STYLE_MAP.equals(extensionLevel)) {
                                styleMapSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_STYLE_MAP.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createStyleMap(objectSimpleExtensions, idAttributes,
                styleSelectorSimpleExtensions, styleSelectorObjectExtensions,
                pairs, styleMapSimpleExtensions, styleMapObjectExtensions);
    }

    private Pair readPair() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // Pair
        StyleState key = DEF_STYLE_STATE;
        URI styleUrl = null;
        AbstractStyleSelector styleSelector = null;
        List<SimpleTypeContainer> pairSimpleExtensions = new ArrayList<>();
        List<Object> pairObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {

                        // PAIR
                        if (TAG_KEY.equals(eName)) {
                            key = StyleState.transform(reader.getElementText());
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = new URI(reader.getElementText());
                        } else if (isAbstractStyleSelector(eName)) {
                            checkVersion(URI_KML_2_2);
                            styleSelector = readAbstractStyleSelector(eName);
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_PAIR, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_PAIR, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.PAIR.equals(extensionLevel)) {
                                pairObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_PAIR, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_PAIR, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.PAIR.equals(extensionLevel)) {
                                pairSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_PAIR.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createPair(objectSimpleExtensions, idAttributes,
                key, styleUrl, styleSelector, pairSimpleExtensions, pairObjectExtensions);
    }

    private Style readStyle() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractStyleSelector
        List<SimpleTypeContainer> styleSelectorSimpleExtensions = new ArrayList<>();
        List<Object> styleSelectorObjectExtensions = new ArrayList<>();

        // Style
        IconStyle iconStyle = null;
        LabelStyle labelStyle = null;
        LineStyle lineStyle = null;
        PolyStyle polyStyle = null;
        BalloonStyle balloonStyle = null;
        ListStyle listStyle = null;
        List<SimpleTypeContainer> styleSimpleExtensions = new ArrayList<>();
        List<Object> styleObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_ICON_STYLE:    iconStyle = readIconStyle(); break;
                            case TAG_LABEL_STYLE:   labelStyle = readLabelStyle(); break;
                            case TAG_LINE_STYLE:    lineStyle = readLineStyle(); break;
                            case TAG_POLY_STYLE:    polyStyle = readPolyStyle(); break;
                            case TAG_BALLOON_STYLE: balloonStyle = readBalloonStyle(); break;
                            case TAG_LIST_STYLE:    listStyle = readListStyle(); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_STYLE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_STYLE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.STYLE_SELECTOR.equals(extensionLevel)) {
                                styleSelectorObjectExtensions.add(ext);
                            } else if (Extensions.Names.STYLE.equals(extensionLevel)) {
                                styleObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_STYLE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_STYLE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.STYLE_SELECTOR.equals(extensionLevel)) {
                                styleSelectorSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.STYLE.equals(extensionLevel)) {
                                styleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_STYLE.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
            }
        }
        return KmlReader.KML_FACTORY.createStyle(objectSimpleExtensions, idAttributes,
                styleSelectorSimpleExtensions, styleSelectorObjectExtensions,
                iconStyle, labelStyle, lineStyle, polyStyle, balloonStyle, listStyle,
                styleSimpleExtensions, styleObjectExtensions);
    }

    private IconStyle readIconStyle() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractSubStyle
        List<SimpleTypeContainer> subStyleSimpleExtensions = new ArrayList<>();
        List<Object> subStyleObjectExtensions = new ArrayList<>();

        // AbstractColorStyle
        Color color = DEF_COLOR;
        ColorMode colorMode = DEF_COLOR_MODE;
        List<SimpleTypeContainer> colorStyleSimpleExtensions = new ArrayList<>();
        List<Object> colorStyleObjectExtensions = new ArrayList<>();

        // IconStyle
        double scale = DEF_SCALE;
        double heading = DEF_HEADING;
        BasicLink icon = null;
        Vec2 hotSpot = null;
        List<SimpleTypeContainer> iconStyleSimpleExtensions = new ArrayList<>();
        List<Object> iconStyleObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_COLOR:      color = KmlUtilities.parseColor(reader.getElementText()); break;
                            case TAG_COLOR_MODE: colorMode = ColorMode.transform(reader.getElementText()); break;
                            case TAG_SCALE:      scale = parseDouble(reader.getElementText()); break;
                            case TAG_HEADING:    heading = parseDouble(reader.getElementText()); break;
                            case TAG_ICON:       icon = readBasicLink(TAG_ICON); break;
                            case TAG_HOT_SPOT:   hotSpot = readVec2(TAG_HOT_SPOT); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_ICON_STYLE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_ICON_STYLE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.SUB_STYLE.equals(extensionLevel)) {
                                subStyleObjectExtensions.add(ext);
                            } else if (Extensions.Names.COLOR_STYLE.equals(extensionLevel)) {
                                colorStyleObjectExtensions.add(ext);
                            } else if (Extensions.Names.ICON_STYLE.equals(extensionLevel)) {
                                iconStyleObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_ICON_STYLE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_ICON_STYLE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.SUB_STYLE.equals(extensionLevel)) {
                                subStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.COLOR_STYLE.equals(extensionLevel)) {
                                colorStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.ICON_STYLE.equals(extensionLevel)) {
                                iconStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_ICON_STYLE.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createIconStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode,
                colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, heading, icon, hotSpot,
                iconStyleSimpleExtensions, iconStyleObjectExtensions);
    }

    private LabelStyle readLabelStyle() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractSubStyle
        List<SimpleTypeContainer> subStyleSimpleExtensions = new ArrayList<>();
        List<Object> subStyleObjectExtensions = new ArrayList<>();

        // AbstractColorStyle
        Color color = DEF_COLOR;
        ColorMode colorMode = DEF_COLOR_MODE;
        List<SimpleTypeContainer> colorStyleSimpleExtensions = new ArrayList<>();
        List<Object> colorStyleObjectExtensions = new ArrayList<>();

        // LabelStyle
        double scale = DEF_SCALE;
        List<SimpleTypeContainer> labelStyleSimpleExtensions = new ArrayList<>();
        List<Object> labelStyleObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_COLOR:      color = KmlUtilities.parseColor(reader.getElementText()); break;
                            case TAG_COLOR_MODE: colorMode = ColorMode.transform(reader.getElementText()); break;
                            case TAG_SCALE:      scale = parseDouble(reader.getElementText()); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_LABEL_STYLE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LABEL_STYLE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.SUB_STYLE.equals(extensionLevel)) {
                                subStyleObjectExtensions.add(ext);
                            } else if (Extensions.Names.COLOR_STYLE.equals(extensionLevel)) {
                                colorStyleObjectExtensions.add(ext);
                            } else if (Extensions.Names.LABEL_STYLE.equals(extensionLevel)) {
                                labelStyleObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_LABEL_STYLE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LABEL_STYLE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.SUB_STYLE.equals(extensionLevel)) {
                                subStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.COLOR_STYLE.equals(extensionLevel)) {
                                colorStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.LABEL_STYLE.equals(extensionLevel)) {
                                labelStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_LABEL_STYLE.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createLabelStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, labelStyleSimpleExtensions, labelStyleObjectExtensions);
    }

    private LineStyle readLineStyle() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractSubStyle
        List<SimpleTypeContainer> subStyleSimpleExtensions = new ArrayList<>();
        List<Object> subStyleObjectExtensions = new ArrayList<>();

        // AbstractColorStyle
        Color color = DEF_COLOR;
        ColorMode colorMode = DEF_COLOR_MODE;
        List<SimpleTypeContainer> colorStyleSimpleExtensions = new ArrayList<>();
        List<Object> colorStyleObjectExtensions = new ArrayList<>();

        // LineStyle
        double width = DEF_WIDTH;
        List<SimpleTypeContainer> lineStyleSimpleExtensions = new ArrayList<>();
        List<Object> lineStyleObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_COLOR:      color = KmlUtilities.parseColor(reader.getElementText()); break;
                            case TAG_COLOR_MODE: colorMode = ColorMode.transform(reader.getElementText()); break;
                            case TAG_WIDTH:      width = parseDouble(reader.getElementText()); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_LINE_STYLE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LINE_STYLE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.SUB_STYLE.equals(extensionLevel)) {
                                subStyleObjectExtensions.add(ext);
                            } else if (Extensions.Names.COLOR_STYLE.equals(extensionLevel)) {
                                colorStyleObjectExtensions.add(ext);
                            } else if (Extensions.Names.LINE_STYLE.equals(extensionLevel)) {
                                lineStyleObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_LINE_STYLE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LINE_STYLE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.SUB_STYLE.equals(extensionLevel)) {
                                subStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.COLOR_STYLE.equals(extensionLevel)) {
                                colorStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.LINE_STYLE.equals(extensionLevel)) {
                                lineStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_LINE_STYLE.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createLineStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                width, lineStyleSimpleExtensions, lineStyleObjectExtensions);
    }

    private PolyStyle readPolyStyle() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractSubStyle
        List<SimpleTypeContainer> subStyleSimpleExtensions = new ArrayList<>();
        List<Object> subStyleObjectExtensions = new ArrayList<>();

        // AbstractColorStyle
        Color color = DEF_COLOR;
        ColorMode colorMode = DEF_COLOR_MODE;
        List<SimpleTypeContainer> colorStyleSimpleExtensions = new ArrayList<>();
        List<Object> colorStyleObjectExtensions = new ArrayList<>();

        // PolyStyle
        boolean fill = DEF_FILL;
        boolean outline = DEF_OUTLINE;
        List<SimpleTypeContainer> polyStyleSimpleExtensions = new ArrayList<>();
        List<Object> polyStyleObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_COLOR:      color = KmlUtilities.parseColor(reader.getElementText()); break;
                            case TAG_COLOR_MODE: colorMode = ColorMode.transform(reader.getElementText()); break;
                            case TAG_FILL:       fill = parseBoolean(reader.getElementText()); break;
                            case TAG_OUTLINE:    outline = parseBoolean(reader.getElementText()); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_POLY_STYLE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_POLY_STYLE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.SUB_STYLE.equals(extensionLevel)) {
                                subStyleObjectExtensions.add(ext);
                            } else if (Extensions.Names.COLOR_STYLE.equals(extensionLevel)) {
                                colorStyleObjectExtensions.add(ext);
                            } else if (Extensions.Names.POLY_STYLE.equals(extensionLevel)) {
                                polyStyleObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_POLY_STYLE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_POLY_STYLE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.SUB_STYLE.equals(extensionLevel)) {
                                subStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.COLOR_STYLE.equals(extensionLevel)) {
                                colorStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.POLY_STYLE.equals(extensionLevel)) {
                                polyStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_POLY_STYLE.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createPolyStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                fill, outline, polyStyleSimpleExtensions, polyStyleObjectExtensions);
    }

    private BalloonStyle readBalloonStyle() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractSubStyle
        List<SimpleTypeContainer> subStyleSimpleExtensions = new ArrayList<>();
        List<Object> subStyleObjectExtensions = new ArrayList<>();

        // BalloonStyle
        Color bgColor = DEF_BG_COLOR;
        Color textColor = DEF_TEXT_COLOR;
        Object text = null;
        DisplayMode displayMode = DEF_DISPLAY_MODE;
        List<SimpleTypeContainer> balloonStyleSimpleExtensions = new ArrayList<>();
        List<Object> balloonStyleObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_BG_COLOR:
                            case TAG_COLOR:
                                if (TAG_COLOR.equals(eName)) {
                                    System.out.println("<" + TAG_COLOR + "> is deprecated in <"
                                                           + TAG_BALLOON_STYLE + "> element and will be replaced by <"
                                                           + TAG_BG_COLOR + "> element.");
                                }
                                bgColor = KmlUtilities.parseColor(reader.getElementText());
                                break;
                            case TAG_TEXT_COLOR:
                                textColor = KmlUtilities.parseColor(reader.getElementText());
                                break;
                            case TAG_TEXT:
                                text = readElementText();
                                break;
                            case TAG_DISPLAY_MODE:
                                checkVersion(URI_KML_2_2);
                                displayMode = DisplayMode.transform(reader.getElementText());
                                break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_BALLOON_STYLE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_BALLOON_STYLE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.SUB_STYLE.equals(extensionLevel)) {
                                subStyleObjectExtensions.add(ext);
                            } else if (Extensions.Names.BALLOON_STYLE.equals(extensionLevel)) {
                                balloonStyleObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_BALLOON_STYLE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_BALLOON_STYLE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.SUB_STYLE.equals(extensionLevel)) {
                                subStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.BALLOON_STYLE.equals(extensionLevel)) {
                                balloonStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_BALLOON_STYLE.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createBalloonStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                bgColor, textColor, text, displayMode,
                balloonStyleSimpleExtensions, balloonStyleObjectExtensions);
    }

    /**
     * This method is a try to separate CDATA and text content.
     */
    public Object readElementText() throws XMLStreamException {
        Object resultat = null;
boucle: while (reader.hasNext()) {
            switch (reader.getEventType()) {
                case XMLStreamConstants.CDATA:
                    resultat = KML_FACTORY.createCdata(reader.getText());
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (resultat != null) {
                        resultat = resultat.toString() + reader.getText();
                    } else {
                        resultat = reader.getText();
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    break boucle;
            }
            reader.next();
        }
        return resultat;
    }

    private ListStyle readListStyle() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractSubStyle
        List<SimpleTypeContainer> subStyleSimpleExtensions = new ArrayList<>();
        List<Object> subStyleObjectExtensions = new ArrayList<>();

        // ListStyle
        ListItem listItem = DEF_LIST_ITEM;
        Color bgColor = DEF_BG_COLOR;
        List<ItemIcon> itemIcons = new ArrayList<>();
        int maxSnippetLines = DEF_MAX_SNIPPET_LINES;
        List<SimpleTypeContainer> listStyleSimpleExtensions = new ArrayList<>();
        List<Object> listStyleObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_LIST_ITEM:  listItem = listItem.transform(reader.getElementText()); break;
                            case TAG_BG_COLOR:   bgColor = KmlUtilities.parseColor(reader.getElementText()); break;
                            case TAG_ITEM_ICON:  itemIcons.add(readItemIcon()); break;
                            case TAG_MAX_SNIPPET_LINES:
                                checkVersion(URI_KML_2_2);
                                maxSnippetLines = Integer.parseInt(reader.getElementText());
                                break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_LIST_STYLE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LIST_STYLE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.SUB_STYLE.equals(extensionLevel)) {
                                subStyleObjectExtensions.add(ext);
                            } else if (Extensions.Names.LIST_STYLE.equals(extensionLevel)) {
                                listStyleObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_LIST_STYLE, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_LIST_STYLE, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.SUB_STYLE.equals(extensionLevel)) {
                                subStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.LIST_STYLE.equals(extensionLevel)) {
                                listStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_LIST_STYLE.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createListStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                listItem, bgColor, itemIcons, maxSnippetLines,
                listStyleSimpleExtensions, listStyleObjectExtensions);
    }

    private ItemIcon readItemIcon() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // ListStyle
        List<ItemIconState> states = null;
        String href = null;
        List<SimpleTypeContainer> itemIconSimpleExtensions = new ArrayList<>();
        List<Object> itemIconObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {

                        // ITEM ICON
                        if (TAG_STATE.equals(eName)) {
                            states = readStates();
                        } else if (TAG_HREF.equals(eName)) {
                            href = reader.getElementText();
                        }

                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_ITEM_ICON, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_ITEM_ICON, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.ITEM_ICON.equals(extensionLevel)) {
                                itemIconObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_ITEM_ICON, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_ITEM_ICON, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.ITEM_ICON.equals(extensionLevel)) {
                                itemIconSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_ITEM_ICON.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createItemIcon(objectSimpleExtensions, idAttributes,
                states, href, itemIconSimpleExtensions, itemIconObjectExtensions);
    }

    private List<ItemIconState> readStates() throws XMLStreamException {
        List<ItemIconState> states = new ArrayList<>();
        for (String iiss : reader.getElementText().split(" ")) {
            ItemIconState iis = ItemIconState.transform(iiss);
            if (iis != null) {
                states.add(iis);
            }
        }
        return states;
    }

    private BasicLink readBasicLink(String stopTag) throws XMLStreamException, KmlException, URISyntaxException {

        if (stopTag == null) {
            throw new KmlException("The stop tag cannot be null. "
                    + "It's probably an <Icon> tag according to KML 2.2 specification.");
        }

        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        String href = null;
        List<SimpleTypeContainer> basicLinkSimpleExtensions = new ArrayList<>();
        List<Object> basicLinkObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        if (TAG_HREF.equals(eName)) {
                            href = reader.getElementText();
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(stopTag, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, stopTag, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.BASIC_LINK.equals(extensionLevel)) {
                                basicLinkObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(stopTag, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, stopTag, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.BASIC_LINK.equals(extensionLevel)) {
                                basicLinkSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (stopTag.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createBasicLink(objectSimpleExtensions, idAttributes,
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
            x = parseDouble(sx);
        }

        double y = DEF_VEC2_Y;
        String sy = reader.getAttributeValue(null, ATT_Y);
        if (sy != null) {
            y = parseDouble(sy);
        }

        Units xUnit = Units.transform(reader.getAttributeValue(null, ATT_XUNITS), DEF_VEC2_XUNIT);
        Units yUnit = Units.transform(reader.getAttributeValue(null, ATT_YUNITS), DEF_VEC2_YUNIT);

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.END_ELEMENT: {
                    if (stopTag.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createVec2(x, y, xUnit, yUnit);
    }

    public AbstractTimePrimitive readAbstractTimePrimitive(String eName)
            throws XMLStreamException, KmlException, URISyntaxException
    {
        AbstractTimePrimitive resultat = null;
        if (TAG_TIME_STAMP.equals(eName)) {
            resultat = readTimeStamp();
        } else if (TAG_TIME_SPAN.equals(eName)) {
            resultat = readTimeSpan();
        }
        return resultat;
    }

    private TimeSpan readTimeSpan() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractTimePrimitive
        List<SimpleTypeContainer> abstractTimePrimitiveSimpleExtensions = new ArrayList<>();
        List<Object> abstractTimePrimitiveObjectExtensions = new ArrayList<>();

        // TimeSpan
        Calendar begin = null;
        Calendar end = null;
        List<SimpleTypeContainer> timeSpanSimpleExtensions = new ArrayList<>();
        List<Object> timeSpanObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        if (TAG_BEGIN.equals(eName)) {
                            begin = (Calendar) fastDateParser.getCalendar(reader.getElementText()).clone();
                        } else if (TAG_END.equals(eName)) {
                            end = (Calendar) fastDateParser.getCalendar(reader.getElementText()).clone();
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_TIME_SPAN, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_TIME_SPAN, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.TIME_PRIMITIVE.equals(extensionLevel)) {
                                abstractTimePrimitiveObjectExtensions.add(ext);
                            } else if (Extensions.Names.TIME_SPAN.equals(extensionLevel)) {
                                timeSpanObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_TIME_SPAN, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_TIME_SPAN, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.TIME_PRIMITIVE.equals(extensionLevel)) {
                                abstractTimePrimitiveSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.TIME_SPAN.equals(extensionLevel)) {
                                timeSpanSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_TIME_SPAN.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createTimeSpan(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions,
                begin, end, timeSpanSimpleExtensions, timeSpanObjectExtensions);
    }

    private TimeStamp readTimeStamp() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractTimePrimitive
        List<SimpleTypeContainer> abstractTimePrimitiveSimpleExtensions = new ArrayList<>();
        List<Object> abstractTimePrimitiveObjectExtensions = new ArrayList<>();

        // TimeStamp
        Calendar when = null;
        List<SimpleTypeContainer> timeStampSimpleExtensions = new ArrayList<>();
        List<Object> timeStampObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        if (TAG_WHEN.equals(eName)) {
                            when = readCalendar();
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_TIME_STAMP, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_TIME_STAMP, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.TIME_PRIMITIVE.equals(extensionLevel)) {
                                abstractTimePrimitiveObjectExtensions.add(ext);
                            } else if (Extensions.Names.TIME_STAMP.equals(extensionLevel)) {
                                timeStampObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_TIME_STAMP, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_TIME_STAMP, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.TIME_PRIMITIVE.equals(extensionLevel)) {
                                abstractTimePrimitiveSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.TIME_STAMP.equals(extensionLevel)) {
                                timeStampSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_TIME_STAMP.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createTimeStamp(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions,
                when, timeStampSimpleExtensions, timeStampObjectExtensions);
    }

    public AtomPersonConstruct readAtomPersonConstruct() throws XMLStreamException {
        return ATOM_READER.readAuthor();
    }

    public AtomLink readAtomLink() throws XMLStreamException {
        return ATOM_READER.readLink();
    }

    public AddressDetails readXalAddressDetails() throws XMLStreamException {
        AddressDetails resultat = null;
        try {
            resultat = XAL_READER.readAddressDetails();
        } catch (XalException ex) {
            Logging.getLogger("org.geotoolkit.data.kml.map").log(Level.SEVERE, null, ex);
        }
        return resultat;
    }

    private Feature readFolder() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractFeature
        String name = null;
        boolean visibility = DEF_VISIBILITY;
        boolean open = DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink link = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        Object snippet = null;
        Object description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        URI styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<>();
        Region region = null;
        Object extendedData = null;
        List<SimpleTypeContainer> featureSimpleExtensions = new ArrayList<>();
        List<Object> featureObjectExtensions = new ArrayList<>();

        // Container
        List<SimpleTypeContainer> abstractContainerSimpleExtensions = new ArrayList<>();
        List<Object> abstractContainerObjectExtensions = new ArrayList<>();

        // Folder
        List<Feature> features = new ArrayList<>();
        List<SimpleTypeContainer> folderSimpleExtensions = new ArrayList<>();
        List<Object> folderObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_NAME:          name = reader.getElementText(); break;
                            case TAG_VISIBILITY:    visibility = parseBoolean(reader.getElementText()); break;
                            case TAG_OPEN:          open = parseBoolean(reader.getElementText()); break;
                            case TAG_ADDRESS:       address = reader.getElementText(); break;
                            case TAG_PHONE_NUMBER:  phoneNumber = reader.getElementText(); break;
                            case TAG_SNIPPET:       snippet = readElementText(); break;
                            case TAG_SNIPPET_BIG:   snippet = readSnippet(); break;
                            case TAG_DESCRIPTION:   description = readElementText(); break;
                            case TAG_STYLE_URL:     styleUrl = new URI(reader.getElementText()); break;
                            case TAG_REGION:        region = readRegion(); break;
                            case TAG_EXTENDED_DATA: extendedData = readExtendedData(); break;
                            case TAG_META_DATA:     extendedData = readMetaData(); break;
                            default: {
                                if (isAbstractFeature(eName)) {
                                    features.add(readAbstractFeature(eName));
                                } else if (isAbstractStyleSelector(eName)) {
                                    styleSelector.add(readAbstractStyleSelector(eName));
                                } else if (isAbstractView(eName)) {
                                    view = readAbstractView(eName);
                                } else if (isAbstractTimePrimitive(eName)) {
                                    timePrimitive = readAbstractTimePrimitive(eName);
                                }
                                break;
                            }
                        }
                    } // ATOM
                    else if (URI_ATOM.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = readAtomLink();
                        }
                    } // XAL
                    else if (URI_XAL.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = readXalAddressDetails();
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_FOLDER, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_FOLDER, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.FEATURE.equals(extensionLevel)) {
                                featureObjectExtensions.add(ext);
                            } else if (Extensions.Names.CONTAINER.equals(extensionLevel)) {
                                abstractContainerObjectExtensions.add(ext);
                            } else if (Extensions.Names.FOLDER.equals(extensionLevel)) {
                                folderObjectExtensions.add(ext);
                            } else if (extensionLevel == null) {
                                if (ext instanceof Feature) {
                                    features.add((Feature) ext);
                                }
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_FOLDER, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_FOLDER, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.FEATURE.equals(extensionLevel)) {
                                featureSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.CONTAINER.equals(extensionLevel)) {
                                abstractContainerSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.FOLDER.equals(extensionLevel)) {
                                folderSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_FOLDER.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createFolder(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive, styleUrl, styleSelector, region, extendedData,
                featureSimpleExtensions, featureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions,
                features, folderSimpleExtensions, folderObjectExtensions);
    }

    private Feature readDocument() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractFeature
        String name = null;
        boolean visibility = DEF_VISIBILITY;
        boolean open = DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink link = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        Object snippet = null;
        Object description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        URI styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<>();
        Region region = null;
        Object extendedData = null;
        List<SimpleTypeContainer> featureSimpleExtensions = new ArrayList<>();
        List<Object> featureObjectExtensions = new ArrayList<>();

        // Container
        List<SimpleTypeContainer> abstractContainerSimpleExtensions = new ArrayList<>();
        List<Object> abstractContainerObjectExtensions = new ArrayList<>();

        // Document
        List<Schema> schemas = new ArrayList<>();
        List<Feature> features = new ArrayList<>();
        List<SimpleTypeContainer> documentSimpleExtensions = new ArrayList<>();
        List<Object> documentObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_NAME:          name = reader.getElementText(); break;
                            case TAG_VISIBILITY:    visibility = parseBoolean(reader.getElementText()); break;
                            case TAG_OPEN:          open = parseBoolean(reader.getElementText()); break;
                            case TAG_ADDRESS:       address = reader.getElementText(); break;
                            case TAG_PHONE_NUMBER:  phoneNumber = reader.getElementText(); break;
                            case TAG_SNIPPET:       snippet = readElementText(); break;
                            case TAG_SNIPPET_BIG:   snippet = readSnippet(); break;
                            case TAG_DESCRIPTION:   description = readElementText(); break;
                            case TAG_STYLE_URL:     styleUrl = new URI(reader.getElementText()); break;
                            case TAG_REGION:        region = readRegion(); break;
                            case TAG_EXTENDED_DATA: extendedData = readExtendedData(); break;
                            case TAG_META_DATA:     extendedData = readMetaData();  break;
                            case TAG_SCHEMA:
                                checkVersion(URI_KML_2_2);
                                schemas.add(readSchema());
                                break;
                            default: {
                                if (isAbstractFeature(eName)) {
                                    features.add(readAbstractFeature(eName));
                                } else if (isAbstractView(eName)) {
                                    view = readAbstractView(eName);
                                } else if (isAbstractTimePrimitive(eName)) {
                                    timePrimitive = readAbstractTimePrimitive(eName);
                                } else if (isAbstractStyleSelector(eName)) {
                                    styleSelector.add(readAbstractStyleSelector(eName));
                                }
                                break;
                            }
                        }
                    } // ATOM
                    else if (URI_ATOM.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = readAtomLink();
                        }
                    } // XAL
                    else if (URI_XAL.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = readXalAddressDetails();
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_DOCUMENT, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_DOCUMENT, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.FEATURE.equals(extensionLevel)) {
                                featureObjectExtensions.add(ext);
                            } else if (Extensions.Names.CONTAINER.equals(extensionLevel)) {
                                abstractContainerObjectExtensions.add(ext);
                            } else if (Extensions.Names.DOCUMENT.equals(extensionLevel)) {
                                documentObjectExtensions.add(ext);
                            } else if (extensionLevel == null) {
                                if (ext instanceof Feature) {
                                    features.add((Feature) ext);
                                }
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_DOCUMENT, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_DOCUMENT, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.FEATURE.equals(extensionLevel)) {
                                featureSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.CONTAINER.equals(extensionLevel)) {
                                abstractContainerSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.DOCUMENT.equals(extensionLevel)) {
                                documentSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_DOCUMENT.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createDocument(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails,
                phoneNumber, snippet, description,
                view, timePrimitive, styleUrl, styleSelector, region, extendedData,
                featureSimpleExtensions, featureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions,
                schemas, features, documentSimpleExtensions, documentObjectExtensions);
    }

    private Schema readSchema() throws XMLStreamException {
        List<SimpleField> simplefields = new ArrayList<>();
        List<Object> schemaExtensions = new ArrayList<>();
        String name = reader.getAttributeValue(null, ATT_NAME);
        String id = reader.getAttributeValue(null, ATT_ID);
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        if (TAG_SIMPLE_FIELD.equals(eName)) {
                            simplefields.add(readSimpleField());
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_SCHEMA.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createSchema(simplefields, name, id, schemaExtensions);
    }

    private SimpleField readSimpleField() throws XMLStreamException {
        Object displayName = null;
        List<Object> simpleFieldExtensions = new ArrayList<>();
        String name = reader.getAttributeValue(null, ATT_NAME);
        String type = reader.getAttributeValue(null, ATT_TYPE);
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        if (TAG_DISPLAY_NAME.equals(eName)) {
                            displayName = readElementText();
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_SIMPLE_FIELD.equals(reader.getLocalName()) && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createSimpleField(displayName, type, name, simpleFieldExtensions);
    }

    private Feature readNetworkLink() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractFeature
        String name = null;
        boolean visibility = DEF_VISIBILITY;
        boolean open = DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink atomLink = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        Object snippet = null;
        Object description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        URI styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<>();
        Region region = null;
        Object extendedData = null;
        List<SimpleTypeContainer> featureSimpleExtensions = new ArrayList<>();
        List<Object> featureObjectExtensions = new ArrayList<>();

        // NetworkLink
        boolean refreshVisibility = DEF_REFRESH_VISIBILITY;
        boolean flyToView = DEF_FLY_TO_VIEW;
        Link link = null;
        List<SimpleTypeContainer> networkLinkSimpleExtensions = new ArrayList<>();
        List<Object> networkLinkObjectExtensions = new ArrayList<>();

boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_NAME:               name = reader.getElementText(); break;
                            case TAG_VISIBILITY:         visibility = parseBoolean(reader.getElementText()); break;
                            case TAG_OPEN:               open = parseBoolean(reader.getElementText()); break;
                            case TAG_ADDRESS:            address = reader.getElementText(); break;
                            case TAG_PHONE_NUMBER:       phoneNumber = reader.getElementText(); break;
                            case TAG_SNIPPET:            snippet = readElementText(); break;
                            case TAG_SNIPPET_BIG:        snippet = readSnippet(); break;
                            case TAG_DESCRIPTION:        description = readElementText(); break;
                            case TAG_STYLE_URL:          styleUrl = new URI(reader.getElementText()); break;
                            case TAG_REGION:             region = readRegion(); break;
                            case TAG_EXTENDED_DATA:      extendedData = readExtendedData(); break;
                            case TAG_META_DATA:          extendedData = readMetaData(); break;
                            case TAG_REFRESH_VISIBILITY: refreshVisibility = parseBoolean(reader.getElementText()); break;
                            case TAG_FLY_TO_VIEW:        flyToView = parseBoolean(reader.getElementText()); break;
                            case TAG_LINK:               link = readLink(eName); break;
                            case TAG_URL:                link = readUrl(eName); break;
                            default: {
                                if (isAbstractView(eName)) {
                                    view = readAbstractView(eName);
                                } else if (isAbstractStyleSelector(eName)) {
                                    styleSelector.add(readAbstractStyleSelector(eName));
                                } else if (isAbstractTimePrimitive(eName)) {
                                    timePrimitive = readAbstractTimePrimitive(eName);
                                }
                                break;
                            }
                        }
                    } // ATOM
                    else if (URI_ATOM.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            atomLink = readAtomLink();
                        }
                    } //XAL
                    else if (URI_XAL.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = readXalAddressDetails();
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_NETWORK_LINK, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_NETWORK_LINK, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.FEATURE.equals(extensionLevel)) {
                                featureObjectExtensions.add(ext);
                            } else if (Extensions.Names.NETWORK_LINK.equals(extensionLevel)) {
                                networkLinkObjectExtensions.add(ext);
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_NETWORK_LINK, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_NETWORK_LINK, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.FEATURE.equals(extensionLevel)) {
                                featureSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.NETWORK_LINK.equals(extensionLevel)) {
                                networkLinkSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_NETWORK_LINK.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createNetworkLink(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, atomLink, address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive, styleUrl,
                styleSelector, region, extendedData,
                featureSimpleExtensions, featureObjectExtensions,
                refreshVisibility, flyToView, link,
                networkLinkSimpleExtensions, networkLinkObjectExtensions);
    }

    private Point readPoint() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<>();
        IdAttributes idAttributes = readIdAttributes();

        // AbstractGeometry
        List<SimpleTypeContainer> abstractGeometrySimpleExtensions = new ArrayList<>();
        List<Object> abstractGeometryObjectExtensions = new ArrayList<>();

        // Point
        boolean extrude = DEF_EXTRUDE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        CoordinateSequence coordinates = null;
        List<SimpleTypeContainer> pointSimpleExtensions = new ArrayList<>();
        List<Object> pointObjectExtensions = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();
                    if (equalsNamespace(eUri)) {
                        switch (eName) {
                            case TAG_EXTRUDE:        extrude = parseBoolean(reader.getElementText()); break;
                            case TAG_ALTITUDE_MODE:  altitudeMode = readAltitudeMode(); break;
                            case TAG_COORDINATES:    coordinates = readCoordinates(reader.getElementText()); break;
                        }
                    } // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if ((r = this.getComplexExtensionReader(TAG_POINT, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_POINT, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.GEOMETRY.equals(extensionLevel)) {
                                abstractGeometryObjectExtensions.add(ext);
                            } else if (Extensions.Names.POINT.equals(extensionLevel)) {
                                pointObjectExtensions.add(ext);
                            } else if (extensionLevel == null) {
                                if (ext instanceof AltitudeMode) {
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = getSimpleExtensionReader(TAG_POINT, eUri, eName)) != null) {
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(URI_KML, TAG_POINT, eUri, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if (Extensions.Names.OBJECT.equals(extensionLevel)) {
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.GEOMETRY.equals(extensionLevel)) {
                                abstractGeometrySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if (Extensions.Names.POINT.equals(extensionLevel)) {
                                pointSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (TAG_POINT.equals(reader.getLocalName())
                            && containsNamespace(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
                }
            }
        }
        return KmlReader.KML_FACTORY.createPoint(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, altitudeMode, coordinates, pointSimpleExtensions, pointObjectExtensions);
    }

    /**
     * This method transforms a String of KML coordinates into an instance of Coordinates.
     *
     * @param coordinates The coordinates String.
     */
    public CoordinateSequence readCoordinates(String coordinates) {
        List<Coordinate> coordinatesList = new ArrayList<>();
        String[] coordinatesStringList = coordinates.split("[\\s]+");
        for (String coordinatesString : coordinatesStringList) {
            if (!coordinatesString.isEmpty()) {
                coordinatesList.add(KmlReader.KML_FACTORY.createCoordinate(coordinatesString));
            }
        }
        return KmlReader.KML_FACTORY.createCoordinates(coordinatesList);
    }

    public IdAttributes readIdAttributes() {
        return KmlReader.KML_FACTORY.createIdAttributes(
                reader.getAttributeValue(null, ATT_ID), reader.getAttributeValue(null, ATT_TARGET_ID));
    }

    public AltitudeMode readAltitudeMode() throws XMLStreamException {
        return EnumAltitudeMode.transform(reader.getElementText());
    }

    public Calendar readCalendar() throws XMLStreamException {
        return (Calendar) fastDateParser.getCalendar(reader.getElementText()).clone();
    }

    public void checkVersion(String... versions) throws KmlException {
        for (String version : versions) {
            if (URI_KML.equals(version)) {
                return;
            }
        }
        throw new KmlException("Kml reader error : Element not allowed by " + URI_KML + " namespace.");
    }

    public boolean checkVersionSimple(String version) {
        if (!useNamespace) {
            return true;
        }
        return URI_KML.equals(version);
    }

    /**
     * say to our reader if we have a namespace or not
     */
    public void setUseNamespace(boolean value) {
        useNamespace = value;
    }

    /**
     * Check the namespace of our kml document
     *
     * @param namespaceURI
     *      the namespace to test
     * @return
     *      true if we have (or if we don't use namespace),
     *      false otherwise
     */
    private boolean checkNamespace(String namespaceURI) {
        //if we hve a valid namespace, return true
        if (URI_KML_2_2.equals(namespaceURI)
                || URI_KML_2_1.equals(namespaceURI)
                || URI_KML_GOOGLE_2_2.equals(namespaceURI)) {
            return true;
        }
        return !useNamespace;
    }

    /**
     * check namespace of our document
     *
     * @param namespaceURI
     *      the namespace we get
     * @return
     *      true if we have a valid namespace (or if we don't use namespace),
     *      false otherwise
     */
    private boolean equalsNamespace(String namespaceURI) {
        if (!useNamespace) {
            return true;
        } else {
            return (URI_KML.equals(namespaceURI));
        }
    }

    /**
     * check if the namespace of our document contains the string given.
     *
     * @param reference
     *      the string to test
     * @return
     *      true if we have a valid namespace (or if we don't use namespace),
     *      false otherwise
     */
    private boolean containsNamespace(String reference) {
        if (!useNamespace) {
            return true;
        } else {
            return (URI_KML.contains(reference));
        }
    }
}

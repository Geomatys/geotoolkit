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
import java.net.URISyntaxException;
import org.geotoolkit.xal.xml.XalReader;
import org.geotoolkit.atom.xml.AtomReader;
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
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
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
import org.geotoolkit.data.kml.model.Coordinates;
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
import org.geotoolkit.temporal.object.FastDateParser;
import org.geotoolkit.xml.StaxStreamReader;
import org.opengis.feature.Feature;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class KmlReader extends StaxStreamReader {

    private String URI_KML;
    private static final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();
    private final XalReader xalReader = new XalReader();
    private final AtomReader atomReader = new AtomReader();
    private final FastDateParser fastDateParser = new FastDateParser();
    private final List<KmlExtensionReader> extensionReaders = new ArrayList<KmlExtensionReader>();

    public KmlReader() {
        super();
//        System.setProperty("javax.xml.stream.XMLInputFactory", "com.bea.xml.stream.MXParserFactory");
//        System.setProperty("javax.xml.stream.XMLEventFactory", "com.bea.xml.stream.EventFactory");

//        System.setProperty("javax.xml.stream.XMLInputFactory", "com.ctc.wstx.stax.WstxInputFactory");
//        System.setProperty("javax.xml.stream.XMLEventFactory", "com.ctc.wstx.stax.WstxEventFactory");
    }

    /**
     * <p>Set input. This method doesn't indicate kml uri version whose detection
     * is automatic at kml root reading. In other cases, method with Kml version uri
     * argument is necessary.</p>
     *
     * @param input
     * @throws IOException
     * @throws XMLStreamException
     */
    @Override
    public void setInput(Object input) throws IOException, XMLStreamException {
        super.setInput(input);
        this.xalReader.setInput(reader);
        this.atomReader.setInput(reader);
    }

    /**
     * <p>Set input. This method is necessary if Kml elements are read out of Kml document
     * with kml root elements.</p>
     *
     * @param input
     * @param KmlVersionUri
     * @throws IOException
     * @throws XMLStreamException
     * @throws KmlException
     */
    public void setInput(Object input, String KmlVersionUri)
            throws IOException, XMLStreamException, KmlException {
        this.setInput(input);
        if (URI_KML_2_2.equals(KmlVersionUri) || URI_KML_2_1.equals(KmlVersionUri))
            this.URI_KML = KmlVersionUri;
        else
            throw new KmlException("Bad Kml version Uri. This reader supports 2.1 and 2.2 versions.");
    }

    /**
     * <p>This method allows to add extensions readers.</p>
     * <p>An extension reader must implement KmlExtensionReader interface.</p>
     *
     * @param reader
     * @throws KmlException
     * @throws IOException
     * @throws XMLStreamException
     */
    public void addExtensionReader(StaxStreamReader reader) 
            throws KmlException, IOException, XMLStreamException{
        if (reader instanceof KmlExtensionReader){
            this.extensionReaders.add((KmlExtensionReader) reader);
            reader.setInput(this.reader);
        } else {
            throw new KmlException("Extension reader must implements "+KmlExtensionReader.class.getName()+" interface.");
        }
    }

    /**
     *
     * @param containingTag
     * @param contentsTag
     * @return
     * @throws KmlException
     */
    protected KmlExtensionReader getComplexExtensionReader(String containingTag, String contentsTag) throws KmlException{
        for(KmlExtensionReader r : this.extensionReaders){
            if(r.canHandleComplexExtension(containingTag, contentsTag)){
                return r;
            }
        }
        return null;
    }

    /**
     * 
     * @param containingTag
     * @param contentsTag
     * @return
     * @throws KmlException
     */
    protected KmlExtensionReader getSimpleExtensionReader(String containingTag, String contentsTag) throws KmlException{
        for(KmlExtensionReader r : this.extensionReaders){
            if(r.canHandleSimpleExtension(containingTag, contentsTag)){
                return r;
            }
        }
        return null;
    }

    /**
     * <p>This method returns kml namespace version uri.</p>
     * 
     * @return
     */
    public String getVersionUri(){
        return this.URI_KML;
    }

    /**
     * <p>This method reads the Kml document assigned to the KmlReader.</p>
     *
     * @return The Kml object mapping the document.
     */
    public Kml read(){
        Kml root = null;
        try {

            while (reader.hasNext()) {
                switch (reader.next()) {

                    case XMLStreamConstants.START_ELEMENT:
                        final String eName = reader.getLocalName();
                        final String eUri = reader.getNamespaceURI();

                        if (URI_KML_2_2.equals(eUri)
                                || URI_KML_2_1.equals(eUri)) {
                            if (TAG_KML.equals(eName)) {
                                this.URI_KML = eUri;

                                Map<String, String> extensionsUris = new HashMap<String, String>();
                                for(int i = 0; i<reader.getNamespaceCount(); i++){
                                    if(reader.getNamespacePrefix(i) != null){
                                        extensionsUris.put(reader.getNamespaceURI(i), reader.getNamespacePrefix(i));
                                    }
                                }
                                root = this.readKml();
                                root.setExtensionsUris(extensionsUris);
                                root.setVersion(URI_KML);
                            }
                        }
                        break;
                }
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(KmlReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(KmlReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KmlException ex) {
            System.out.println("KML EXCEPTION : " + ex.getMessage());
        }
        return root;
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private Kml readKml() 
            throws XMLStreamException, KmlException, URISyntaxException {

        NetworkLinkControl networkLinkControl = null;
        Feature abstractFeature = null;
        List<SimpleTypeContainer> kmlSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> kmlObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (URI_KML.equals(eUri)) {
                        if (TAG_NETWORK_LINK_CONTROL.equals(eName)) {
                            networkLinkControl = this.readNetworkLinkControl();
                        } else if (isAbstractFeature(eName)) {
                            abstractFeature = this.readAbstractFeature(eName);
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_KML, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_KML, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.KML.equals(extensionLevel)){
                                kmlObjectExtensions.add(ext);
                            } else if(extensionLevel == null){
                                if (ext instanceof Feature){
                                   abstractFeature = (Feature) ext;
                                }
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_KML, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_KML, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.KML.equals(extensionLevel)){
                                kmlSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_KML.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return KmlReader.kmlFactory.createKml(
                networkLinkControl, abstractFeature,
                kmlSimpleExtensions, kmlObjectExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private Feature readPlacemark() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
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

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
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
                            snippet = this.readElementText();
                        } else if (TAG_SNIPPET_BIG.equals(eName)) {
                            snippet = this.readSnippet();
                        } else if (TAG_DESCRIPTION.equals(eName)) {
                            description = this.readElementText();
                        } else if (isAbstractView(eName)) {
                            view = this.readAbstractView(eName);
                        } else if (isAbstractTimePrimitive(eName)) {
                            timePrimitive = this.readAbstractTimePrimitive(eName);
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = new URI(reader.getElementText());
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector.add(this.readAbstractStyleSelector(eName));
                        } else if (TAG_REGION.equals(eName)) {
                            region = this.readRegion();
                        } else if (TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = this.readExtendedData();
                        } else if (TAG_META_DATA.equals(eName)) {
                            extendedData = this.readMetaData();
                        }

                        // PLACEMARK
                        else if (isAbstractGeometry(eName)) {
                            abstractGeometry = this.readAbstractGeometry(eName);
                        }
                    }
                    // ATOM
                    else if (URI_ATOM.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = this.readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = this.readAtomLink();
                        }
                    }
                    // XAL
                    else if (URI_XAL.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = this.readXalAddressDetails();
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_PLACEMARK, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_PLACEMARK, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.FEATURE.equals(extensionLevel)){
                                featureObjectExtensions.add(ext);
                            } else if(Extensions.Names.PLACEMARK.equals(extensionLevel)){
                                placemarkObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_PLACEMARK, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_PLACEMARK, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.FEATURE.equals(extensionLevel)){
                                featureSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.PLACEMARK.equals(extensionLevel)){
                                placemarkSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_PLACEMARK.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }

        return KmlReader.kmlFactory.createPlacemark(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive, styleUrl,
                styleSelector, region, extendedData, featureSimpleExtensions, featureObjectExtensions,
                abstractGeometry, placemarkSimpleExtensions, placemarkObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    public Region readRegion() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // Region
        LatLonAltBox latLonAltBox = null;
        Lod lod = null;
        List<SimpleTypeContainer> regionSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> regionObjectExtentions = new ArrayList<Object>();

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
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_REGION, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_REGION, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.REGION.equals(extensionLevel)){
                                regionObjectExtentions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_REGION, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_REGION, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.REGION.equals(extensionLevel)){
                                regionSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_REGION.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws KmlException
     * @throws URISyntaxException
     */
    private Lod readLod()
            throws XMLStreamException, KmlException, URISyntaxException {
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // Lod
        double minLodPixels = DEF_MIN_LOD_PIXELS;
        double maxLodPixels = DEF_MAX_LOD_PIXELS;
        double minFadeExtent = DEF_MIN_FADE_EXTENT;
        double maxFadeExtent = DEF_MAX_FADE_EXTENT;
        List<SimpleTypeContainer> lodSimpleExtentions = new ArrayList<SimpleTypeContainer>();
        List<Object> lodObjectExtensions = new ArrayList<Object>();

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
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_LOD, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LOD, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.LOD.equals(extensionLevel)){
                                lodObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_LOD, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LOD, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.LOD.equals(extensionLevel)){
                                lodSimpleExtentions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LOD.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }

        return KmlReader.kmlFactory.createLod(objectSimpleExtensions, idAttributes,
                minLodPixels, maxLodPixels, minFadeExtent, maxFadeExtent,
                lodSimpleExtentions, lodObjectExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws URISyntaxException
     */
    public ExtendedData readExtendedData() 
            throws XMLStreamException, URISyntaxException {

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
                    if (TAG_EXTENDED_DATA.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        
        return KmlReader.kmlFactory.createExtendedData(
                datas, schemaDatas, anyOtherElements);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @deprecated
     */
    @Deprecated
    public Metadata readMetaData()
            throws XMLStreamException{
        return KmlReader.kmlFactory.createMetadata();
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private Data readData() 
            throws XMLStreamException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // Data
        String name = reader.getAttributeValue(null, ATT_NAME);
        Object displayName = null;
        String value = null;
        List<Object> dataExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // REGION
                        if (TAG_DISPLAY_NAME.equals(eName)) {
                            displayName = this.readElementText();
                        } else if (TAG_VALUE.equals(eName)) {
                            value = reader.getElementText();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_DATA.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createData(objectSimpleExtensions, idAttributes,
                name, displayName, value, dataExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws URISyntaxException
     */
    private SchemaData readSchemaData() 
            throws XMLStreamException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // SchemaData
        URI schemaURL = new URI(reader.getAttributeValue(null, ATT_SCHEMA_URL));
        List<SimpleData> simpleDatas = new ArrayList<SimpleData>();
        List<Object> schemaDataExtensions = new ArrayList<Object>();

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
                    if (TAG_SCHEMA_DATA.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }

        return KmlReader.kmlFactory.createSchemaData(objectSimpleExtensions,
                idAttributes, schemaURL, simpleDatas, schemaDataExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private SimpleData readSimpleData() 
            throws XMLStreamException {
        return KmlReader.kmlFactory.createSimpleData(
                reader.getAttributeValue(null, ATT_NAME), reader.getElementText());
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private NetworkLinkControl readNetworkLinkControl() 
            throws XMLStreamException, KmlException, URISyntaxException {

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
        List<SimpleTypeContainer> networkLinkControlSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> networkLinkControlObjectExtensions = new ArrayList<Object>();

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
                            checkVersion(URI_KML_2_2);
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
                            linkDescription = this.readElementText();
                        }
                        if (TAG_LINK_SNIPPET.equals(eName)){
                            linkSnippet = this.readSnippet();
                        }
                        if (TAG_EXPIRES.equals(eName)){
                            expires = fastDateParser.getCalendar(reader.getElementText());
                        }
                        if (TAG_UPDATE.equals(eName)){
                            update = this.readUpdate();
                        }
                        if (isAbstractView(eName)){
                            this.readAbstractView(eName);
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_NETWORK_LINK_CONTROL, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_NETWORK_LINK_CONTROL, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.NETWORK_LINK_CONTROL.equals(extensionLevel)){
                                networkLinkControlObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_NETWORK_LINK_CONTROL, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_NETWORK_LINK_CONTROL, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.NETWORK_LINK_CONTROL.equals(extensionLevel)){
                                networkLinkControlSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_NETWORK_LINK_CONTROL.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws KmlException
     * @throws URISyntaxException
     */
    public Update readUpdate() 
            throws XMLStreamException, KmlException, URISyntaxException{

        URI targetHref = null;
        List<Object> updates = new ArrayList<Object>();
        List<Object> updateOpExtensions = new ArrayList<Object>();
        List<Object> updateExtensions = new ArrayList<Object>();
        
        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {
                        if (TAG_TARGET_HREF.equals(eName)) {
                            targetHref = new URI(reader.getElementText());
                        }
                        if (TAG_CREATE.equals(eName)) {
                            updates.add(this.readCreate());
                        }
                        if (TAG_DELETE.equals(eName)) {
                            updates.add(this.readDelete());
                        }
                        if (TAG_CHANGE.equals(eName)){
                            updates.add(this.readChange());
                        }
                        if (TAG_REPLACE.equals(eName)){
                            this.checkVersion(URI_KML_2_1);
                            updates.add(this.readReplace());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_UPDATE.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }

        return KmlReader.kmlFactory.createUpdate(targetHref, updates,
                updateOpExtensions, updateExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     * @deprecated
     */
    @Deprecated
    private Feature readReplace() 
            throws XMLStreamException, KmlException, URISyntaxException{

        Feature replace = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (URI_KML.equals(eUri)) {
                        if (isAbstractFeature(eName)) {
                            replace = this.readAbstractFeature(eName);
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_REPLACE.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }

        return replace;
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private Create readCreate() throws XMLStreamException, KmlException, URISyntaxException{
        List<Feature> containers = new ArrayList<Feature>();

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
                    if (TAG_CREATE.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     */
    private Delete readDelete() 
            throws XMLStreamException, KmlException, URISyntaxException{

        List<Feature> features = new ArrayList<Feature>();

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
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_DELETE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_DELETE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(extensionLevel == null){
                                if (ext instanceof Feature){
                                   features.add((Feature) ext);
                                }
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_DELETE.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     */
    private Change readChange() 
            throws XMLStreamException, KmlException, URISyntaxException{

        List<Object> objects = new ArrayList<Object>();

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
                    if (TAG_CHANGE.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     */
    private Object readAbstractObject(String eName)
            throws XMLStreamException, KmlException, URISyntaxException{
        Object resultat = null;
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
     * @throws URISyntaxException
     */
    private AbstractLatLonBox readAbstractLatLonBox(String eName) 
            throws XMLStreamException, KmlException, URISyntaxException{

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
    private AbstractSubStyle readAbstractSubStyle(String eName) 
            throws XMLStreamException, KmlException, URISyntaxException{

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
     * @throws URISyntaxException
     */
    private AbstractColorStyle readAbstractColorStyle(String eName) 
            throws XMLStreamException, KmlException, URISyntaxException{

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
    public Snippet readSnippet() 
            throws XMLStreamException{

        int maxLines = DEF_MAX_SNIPPET_LINES_ATT;
        if (reader.getAttributeValue(null, ATT_MAX_LINES) != null){
            maxLines = Integer.parseInt(reader.getAttributeValue(null, ATT_MAX_LINES));
        }
        Object content = this.readElementText();
        return KmlReader.kmlFactory.createSnippet(maxLines, content);
    }

    /**
     *
     * @param eName
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private AbstractGeometry readAbstractGeometry(String eName) 
            throws XMLStreamException, KmlException, URISyntaxException {

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
     * @throws URISyntaxException
     */
    private Polygon readPolygon() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractGeometry
        List<SimpleTypeContainer> abstractGeometrySimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractGeometryObjectExtensions = new ArrayList<Object>();

        // Polygon
        boolean extrude = DEF_EXTRUDE;
        boolean tessellate = DEF_TESSELLATE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        Boundary outerBoundaryIs = null;
        List<Boundary> innerBoundariesAre = new ArrayList<Boundary>();
        List<SimpleTypeContainer> polygonSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> polygonObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (URI_KML.equals(eUri)) {
                        // POLYGON
                        if (TAG_EXTRUDE.equals(eName)) {
                            extrude = parseBoolean(reader.getElementText());
                        } else if (TAG_TESSELLATE.equals(eName)) {
                            tessellate = parseBoolean(reader.getElementText());
                        } else if (TAG_ALTITUDE_MODE.equals(eName)) {
                            altitudeMode = EnumAltitudeMode.transform(reader.getElementText());
                        } else if (TAG_OUTER_BOUNDARY_IS.equals(eName)) {
                            outerBoundaryIs = this.readBoundary();
                        } else if (TAG_INNER_BOUNDARY_IS.equals(eName)) {
                            innerBoundariesAre.add(this.readBoundary());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_POLYGON, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_POLYGON, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.GEOMETRY.equals(extensionLevel)){
                                abstractGeometryObjectExtensions.add(ext);
                            } else if(Extensions.Names.POLYGON.equals(extensionLevel)){
                                polygonObjectExtensions.add(ext);
                            } else if(extensionLevel == null){
                                if (ext instanceof AltitudeMode){
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_POLYGON, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_POLYGON, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.GEOMETRY.equals(extensionLevel)){
                                abstractGeometrySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.POLYGON.equals(extensionLevel)){
                                polygonSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_POLYGON.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws KmlException
     * @throws URISyntaxException
     */
    private Boundary readBoundary() 
            throws XMLStreamException, KmlException, URISyntaxException{

        LinearRing linearRing = null;
        List<SimpleTypeContainer> boundarySimpleExtensions = null;
        List<Object> boundaryObjectExtensions = null;

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
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_OUTER_BOUNDARY_IS, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_OUTER_BOUNDARY_IS, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.BOUNDARY.equals(extensionLevel)){
                                boundaryObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getComplexExtensionReader(TAG_INNER_BOUNDARY_IS, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_INNER_BOUNDARY_IS, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.BOUNDARY.equals(extensionLevel)){
                                boundaryObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_OUTER_BOUNDARY_IS, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_OUTER_BOUNDARY_IS, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.BOUNDARY.equals(extensionLevel)){
                                boundarySimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_INNER_BOUNDARY_IS, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_INNER_BOUNDARY_IS, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.BOUNDARY.equals(extensionLevel)){
                                boundarySimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if ((TAG_OUTER_BOUNDARY_IS.equals(reader.getLocalName())
                            || TAG_INNER_BOUNDARY_IS.equals(reader.getLocalName()))
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }

        return KmlReader.kmlFactory.createBoundary(
                linearRing, boundarySimpleExtensions, boundaryObjectExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private Model readModel() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractGeometry
        List<SimpleTypeContainer> abstractGeometrySimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractGeometryObjectExtensions = new ArrayList<Object>();

        // Model
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        Location location = null;
        Orientation orientation = null;
        Scale scale = null;
        Link link = null;
        ResourceMap resourceMap = null;
        List<SimpleTypeContainer> modelSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> modelObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // MODEL
                    if (URI_KML.equals(eUri)) {
                        if (TAG_ALTITUDE_MODE.equals(eName)) {
                            altitudeMode = EnumAltitudeMode.transform(reader.getElementText());
                        } else if (TAG_LOCATION.equals(eName)) {
                            location = this.readLocation();
                        } else if (TAG_ORIENTATION.equals(eName)) {
                            orientation = this.readOrientation();
                        } else if (TAG_SCALE_BIG.equals(eName)) {
                            scale = readScale();
                        } else if (TAG_LINK.equals(eName)) {
                            link = this.readLink(eName);
                        } else if (TAG_RESOURCE_MAP.equals(eName)) {
                            checkVersion(URI_KML_2_2);
                            resourceMap = readResourceMap();
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_MODEL, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_MODEL, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.GEOMETRY.equals(extensionLevel)){
                                abstractGeometryObjectExtensions.add(ext);
                            } else if(Extensions.Names.MODEL.equals(extensionLevel)){
                                modelObjectExtensions.add(ext);
                            } else if(extensionLevel == null){
                                if (ext instanceof AltitudeMode){
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_MODEL, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_MODEL, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.GEOMETRY.equals(extensionLevel)){
                                abstractGeometrySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.MODEL.equals(extensionLevel)){
                                modelSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_MODEL.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     * @throws KmlException
     */
    private ResourceMap readResourceMap() 
            throws XMLStreamException, URISyntaxException, KmlException{

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // ResourceMap
        List<Alias> aliases = new ArrayList<Alias>();
        List<SimpleTypeContainer> resourceMapSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> resourceMapObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (URI_KML.equals(eUri)) {
                        if (TAG_ALIAS.equals(eName)) {
                            aliases.add(this.readAlias());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_RESOURCE_MAP, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_RESOURCE_MAP, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.RESOURCE_MAP.equals(extensionLevel)){
                                resourceMapObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_RESOURCE_MAP, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_RESOURCE_MAP, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.RESOURCE_MAP.equals(extensionLevel)){
                                resourceMapSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_RESOURCE_MAP.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     * @throws KmlException
     */
     private Alias readAlias() 
             throws XMLStreamException, URISyntaxException, KmlException{

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // Alias
        URI targetHref = null;
        URI sourceHref = null;
        List<SimpleTypeContainer> alaisSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> aliasObjectExtensions = new ArrayList<Object>();


        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (URI_KML.equals(eUri)) {
                        if (TAG_TARGET_HREF.equals(eName)) {
                            targetHref = new URI(reader.getElementText());
                        } else if (TAG_SOURCE_HREF.equals(eName)) {
                            sourceHref = new URI(reader.getElementText());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_ALIAS, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_ALIAS, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.ALIAS.equals(extensionLevel)){
                                aliasObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_ALIAS, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_ALIAS, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.ALIAS.equals(extensionLevel)){
                                alaisSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_ALIAS.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
      * @throws KmlException
      * @throws URISyntaxException
      */
    private Scale readScale() 
            throws XMLStreamException, KmlException, URISyntaxException{
        
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // Scale
        double x = DEF_X;
        double y = DEF_Y;
        double z = DEF_Z;
        List<SimpleTypeContainer> scaleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> scaleObjectExtensions = new ArrayList<Object>();


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
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_SCALE_BIG, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_SCALE_BIG, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.SCALE.equals(extensionLevel)){
                                scaleObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_SCALE_BIG, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_SCALE_BIG, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.SCALE.equals(extensionLevel)){
                                scaleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
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
     * @throws URISyntaxException
     */
    private Location readLocation() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // Location
        double longitude = DEF_LONGITUDE;
        double latitude = DEF_LATITUDE;
        double altitude = DEF_ALTITUDE;
        List<SimpleTypeContainer> locationSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> locationObjectExtensions = new ArrayList<Object>();


        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // LOCATION
                    if (URI_KML.equals(eUri)) {
                        if (TAG_LONGITUDE.equals(eName)) {
                            longitude = parseDouble(reader.getElementText());
                        } else if (TAG_LATITUDE.equals(eName)) {
                            latitude = parseDouble(reader.getElementText());
                        } else if (TAG_ALTITUDE.equals(eName)){
                            altitude = parseDouble(reader.getElementText());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_LOCATION, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LOCATION, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.LOCATION.equals(extensionLevel)){
                                locationObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_LOCATION, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LOCATION, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.LOCATION.equals(extensionLevel)){
                                locationSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LOCATION.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     */
    private Orientation readOrientation() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // Orientation
        double heading = DEF_HEADING;
        double tilt = DEF_TILT;
        double roll = DEF_ROLL;
        List<SimpleTypeContainer> orientationSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> orientationObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // ORIENTATION
                    if (URI_KML.equals(eUri)) {
                        if (TAG_HEADING.equals(eName)) {
                            heading = parseDouble(reader.getElementText());
                        } else if (TAG_TILT.equals(eName)) {
                            tilt = parseDouble(reader.getElementText());
                        } else if (TAG_ROLL.equals(eName)) {
                            roll = parseDouble(reader.getElementText());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_ORIENTATION, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_ORIENTATION, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.ORIENTATION.equals(extensionLevel)){
                                orientationObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_ORIENTATION, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_ORIENTATION, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.ORIENTATION.equals(extensionLevel)){
                                orientationSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_ORIENTATION.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws KmlException
     * @throws URISyntaxException
     */
    private LinearRing readLinearRing() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractGeometry
        List<SimpleTypeContainer> abstractGeometrySimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractGeometryObjectExtensions = new ArrayList<Object>();

        // LinearRing
        boolean extrude = DEF_EXTRUDE;
        boolean tessellate = DEF_TESSELLATE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        Coordinates coordinates = null;
        List<SimpleTypeContainer> linearRingSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> linearRingObjectExtensions = new ArrayList<Object>();

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
                            altitudeMode = EnumAltitudeMode.transform(reader.getElementText());
                        } else if (TAG_COORDINATES.equals(eName)) {
                            coordinates = readCoordinates(reader.getElementText());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_LINEAR_RING, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LINEAR_RING, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.GEOMETRY.equals(extensionLevel)){
                                abstractGeometryObjectExtensions.add(ext);
                            } else if(Extensions.Names.LINEAR_RING.equals(extensionLevel)){
                                linearRingObjectExtensions.add(ext);
                            } else if(extensionLevel == null){
                                if (ext instanceof AltitudeMode){
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_LINEAR_RING, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LINEAR_RING, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.GEOMETRY.equals(extensionLevel)){
                                abstractGeometrySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.LINEAR_RING.equals(extensionLevel)){
                                linearRingSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LINEAR_RING.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws KmlException
     * @throws URISyntaxException
     */
    private LineString readLineString() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractGeometry
        List<SimpleTypeContainer> abstractGeometrySimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractGeometryObjectExtensions = new ArrayList<Object>();

        // LineString
        boolean extrude = DEF_EXTRUDE;
        boolean tessellate = DEF_TESSELLATE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        Coordinates coordinates = null;
        List<SimpleTypeContainer> lineStringSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> lineStringObjectExtensions = new ArrayList<Object>();

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
                            altitudeMode = EnumAltitudeMode.transform(reader.getElementText());
                        } else if (TAG_COORDINATES.equals(eName)) {
                            coordinates = readCoordinates(reader.getElementText());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_LINE_STRING, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LINE_STRING, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.GEOMETRY.equals(extensionLevel)){
                                abstractGeometryObjectExtensions.add(ext);
                            } else if(Extensions.Names.LINE_STRING.equals(extensionLevel)){
                                lineStringObjectExtensions.add(ext);
                            } else if(extensionLevel == null){
                                if (ext instanceof AltitudeMode){
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_LINE_STRING, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LINE_STRING, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.GEOMETRY.equals(extensionLevel)){
                                abstractGeometrySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.LINE_STRING.equals(extensionLevel)){
                                lineStringSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LINE_STRING.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     */
    private MultiGeometry readMultiGeometry() 
            throws XMLStreamException, KmlException, URISyntaxException{

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractGeometry
        List<SimpleTypeContainer> abstractGeometrySimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractGeometryObjectExtensions = new ArrayList<Object>();

        // Multi Geometry
        List<AbstractGeometry> geometries = new ArrayList<AbstractGeometry>();
        List<SimpleTypeContainer> multiGeometrySimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> multiGeometryObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (URI_KML.equals(eUri)) {
                        if (isAbstractGeometry(eName)) {
                            geometries.add(this.readAbstractGeometry(eName));
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_MULTI_GEOMETRY, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_MULTI_GEOMETRY, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.GEOMETRY.equals(extensionLevel)){
                                abstractGeometryObjectExtensions.add(ext);
                            } else if(Extensions.Names.MULTI_GEOMETRY.equals(extensionLevel)){
                                multiGeometryObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_MULTI_GEOMETRY, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_MULTI_GEOMETRY, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.GEOMETRY.equals(extensionLevel)){
                                abstractGeometrySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.MULTI_GEOMETRY.equals(extensionLevel)){
                                multiGeometrySimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_MULTI_GEOMETRY.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @param eName The tag name
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private Feature readAbstractFeature(String eName) 
            throws XMLStreamException, KmlException, URISyntaxException {

        Feature resultat = null;
        if (isAbstractContainer(eName)) {
            resultat = this.readAbstractContainer(eName);
        } else if (isAbstractOverlay(eName)) {
            resultat = this.readAbstractOverlay(eName);
        } else if (TAG_NETWORK_LINK.equals(eName)) {
            resultat = readNetworkLink();
        } else if (TAG_PLACEMARK.equals(eName)) {
            resultat = readPlacemark();
        }
        return resultat;
    }

    /**
     *
     * @param eName The tag name
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private Feature readAbstractOverlay(String eName) 
            throws XMLStreamException, KmlException, URISyntaxException {

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

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private Feature readGroundOverlay()
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
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

        // AbstractOverlay
        Color color = DEF_COLOR;
        int drawOrder = DEF_DRAW_ORDER;
        Icon icon = null;
        List<SimpleTypeContainer> abstractOverlaySimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractOverlayObjectExtensions = new ArrayList<Object>();

        // GroundOverlay
        double altitude = DEF_ALTITUDE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        LatLonBox latLonBox = null;
        List<SimpleTypeContainer> groundOverlaySimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> groundOverlayObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
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
                            snippet = this.readElementText();
                        } else if (TAG_SNIPPET_BIG.equals(eName)) {
                            snippet = this.readSnippet();
                        } else if (TAG_DESCRIPTION.equals(eName)) {
                            description = this.readElementText();
                        }else if (isAbstractView(eName)) {
                            view = this.readAbstractView(eName);
                        } else if (isAbstractTimePrimitive(eName)) {
                            timePrimitive = this.readAbstractTimePrimitive(eName);
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = new URI(reader.getElementText());
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector.add(this.readAbstractStyleSelector(eName));
                        } else if (TAG_REGION.equals(eName)) {
                            region = this.readRegion();
                        } else if (TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = this.readExtendedData();
                        } else if (TAG_META_DATA.equals(eName)) {
                            extendedData = this.readMetaData();
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
                            altitudeMode = EnumAltitudeMode.transform(reader.getElementText());
                        } else if (TAG_LAT_LON_BOX.equals(eName)) {
                            latLonBox = this.readLatLonBox();
                        }


                    } else if (URI_ATOM.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = this.readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = this.readAtomLink();
                        }
                    } else if (URI_XAL.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = this.readXalAddressDetails();
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_GROUND_OVERLAY, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_GROUND_OVERLAY, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.FEATURE.equals(extensionLevel)){
                                featureObjectExtensions.add(ext);
                            } else if(Extensions.Names.OVERLAY.equals(extensionLevel)){
                                abstractOverlayObjectExtensions.add(ext);
                            } else if(Extensions.Names.GROUND_OVERLAY.equals(extensionLevel)){
                                groundOverlayObjectExtensions.add(ext);
                            } else if(extensionLevel == null){
                                if (ext instanceof AltitudeMode){
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_GROUND_OVERLAY, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_GROUND_OVERLAY, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.FEATURE.equals(extensionLevel)){
                                featureSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.OVERLAY.equals(extensionLevel)){
                                abstractOverlaySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.GROUND_OVERLAY.equals(extensionLevel)){
                                groundOverlaySimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_GROUND_OVERLAY.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private Link readLink(String stopName)
            throws XMLStreamException, KmlException, URISyntaxException {

        // Comme BasicLink
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        String href = null;
        List<SimpleTypeContainer> basicLinkSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> basicLinkObjectExtensions = new ArrayList<Object>();

        // Spécifique à Link
        RefreshMode refreshMode = DEF_REFRESH_MODE;
        double refreshInterval = DEF_REFRESH_INTERVAL;
        ViewRefreshMode viewRefreshMode = DEF_VIEW_REFRESH_MODE;
        double viewRefreshTime = DEF_VIEW_REFRESH_TIME;
        double viewBoundScale = DEF_VIEW_BOUND_SCALE;
        String viewFormat = null;
        String httpQuery = null;
        List<SimpleTypeContainer> linkSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> linkObjectExtensions = new ArrayList<Object>();
        
        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
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
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(stopName, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(stopName, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.BASIC_LINK.equals(extensionLevel)){
                                basicLinkObjectExtensions.add(ext);
                            } else if(Extensions.Names.LINK.equals(extensionLevel)){
                                linkObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(stopName, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(stopName, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.BASIC_LINK.equals(extensionLevel)){
                                basicLinkSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.LINK.equals(extensionLevel)){
                                linkSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (stopName.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createLink(objectSimpleExtensions, idAttributes,
                href, basicLinkSimpleExtensions, basicLinkObjectExtensions,
                refreshMode, refreshInterval, viewRefreshMode, viewRefreshTime,
                viewBoundScale, viewFormat, httpQuery,
                linkSimpleExtensions, linkObjectExtensions);
    }


    /**
     * 
     * @param stopName
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private Icon readIcon(String stopName) 
            throws XMLStreamException, KmlException, URISyntaxException{
        return KmlReader.kmlFactory.createIcon(this.readLink(stopName));
    }

    /**
     *
     * @param stopName
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     * @deprecated
     */
    @Deprecated
    private Url readUrl(String stopName) 
            throws XMLStreamException, KmlException, URISyntaxException{
        return KmlReader.kmlFactory.createUrl(this.readLink(stopName));
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private LatLonBox readLatLonBox() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractLatLonBox
        double north = DEF_NORTH;
        double south = DEF_SOUTH;
        double east = DEF_EAST;
        double west = DEF_WEST;
        List<SimpleTypeContainer> abstractLatLonBoxSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractLatLonBoxObjectExtensions = new ArrayList<Object>();

        // LatLonBox
        double rotation = DEF_ROTATION;
        List<SimpleTypeContainer> latLonBoxSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> latLonBoxObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // ABSTRACT LATLONBOX
                        if (TAG_NORTH.equals(eName)) {
                            north = parseDouble(reader.getElementText());
                        } else if (TAG_SOUTH.equals(eName)) {
                            south = parseDouble(reader.getElementText());
                        } else if (TAG_EAST.equals(eName)) {
                            east = parseDouble(reader.getElementText());
                        } else if (TAG_WEST.equals(eName)) {
                            west = parseDouble(reader.getElementText());
                        } 
                        
                        // LATLONBOX
                        else if (TAG_ROTATION.equals(eName)) {
                            rotation = parseDouble(reader.getElementText());
                        }

                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_LAT_LON_BOX, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LAT_LON_BOX, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.ABSTRACT_LAT_LON_BOX.equals(extensionLevel)){
                                abstractLatLonBoxObjectExtensions.add(ext);
                            } else if(Extensions.Names.LAT_LON_BOX.equals(extensionLevel)){
                                latLonBoxObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_LAT_LON_BOX, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LAT_LON_BOX, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.ABSTRACT_LAT_LON_BOX.equals(extensionLevel)){
                                abstractLatLonBoxSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.LAT_LON_BOX.equals(extensionLevel)){
                                latLonBoxSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LAT_LON_BOX.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     */
    private LatLonAltBox readLatLonAltBox() 
            throws XMLStreamException, KmlException, URISyntaxException {
        
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractLatLonBox
        double north = DEF_NORTH;
        double south = DEF_SOUTH;
        double east = DEF_EAST;
        double west = DEF_WEST;
        List<SimpleTypeContainer> abstractLatLonBoxSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractLatLonBoxObjectExtensions = new ArrayList<Object>();

        // LatLonAltBox
        double minAltitude = DEF_MIN_ALTITUDE;
        double maxAltitude = DEF_MAX_ALTITUDE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        List<SimpleTypeContainer> latLonAltBoxSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> latLonAltBoxObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // ABSTRACT LATLONBOX
                        if (TAG_NORTH.equals(eName)) {
                            north = parseDouble(reader.getElementText());
                        } else if (TAG_SOUTH.equals(eName)) {
                            south = parseDouble(reader.getElementText());
                        } else if (TAG_EAST.equals(eName)) {
                            east = parseDouble(reader.getElementText());
                        } else if (TAG_WEST.equals(eName)) {
                            west = parseDouble(reader.getElementText());
                        }

                        // LATLONALTBOX
                        else if (TAG_MIN_ALTITUDE.equals(eName)) {
                            minAltitude = parseDouble(reader.getElementText());
                        } else if (TAG_MAX_ALTITUDE.equals(eName)) {
                            maxAltitude = parseDouble(reader.getElementText());
                        } else if (TAG_MIN_ALTITUDE.equals(eName)) {
                            altitudeMode = EnumAltitudeMode.transform(reader.getElementText());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_LAT_LON_ALT_BOX, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LAT_LON_ALT_BOX, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.ABSTRACT_LAT_LON_BOX.equals(extensionLevel)){
                                abstractLatLonBoxObjectExtensions.add(ext);
                            } else if(Extensions.Names.LAT_LON_ALT_BOX.equals(extensionLevel)){
                                latLonAltBoxObjectExtensions.add(ext);
                            } else if(extensionLevel == null){
                                if (ext instanceof AltitudeMode){
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_LAT_LON_ALT_BOX, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LAT_LON_ALT_BOX, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.ABSTRACT_LAT_LON_BOX.equals(extensionLevel)){
                                abstractLatLonBoxSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.LAT_LON_ALT_BOX.equals(extensionLevel)){
                                latLonAltBoxSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LAT_LON_ALT_BOX.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return KmlReader.kmlFactory.createLatLonAltBox(objectSimpleExtensions, 
                idAttributes,north, south, east, west,
                abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions,
                minAltitude, maxAltitude, altitudeMode,
                latLonAltBoxSimpleExtensions, latLonAltBoxObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private ImagePyramid readImagePyramid() throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // ImagePyramid
        int titleSize = DEF_TITLE_SIZE;
        int maxWidth = DEF_MAX_WIDTH;
        int maxHeight = DEF_MAX_HEIGHT;
        GridOrigin gridOrigin = DEF_GRID_ORIGIN;
        List<SimpleTypeContainer> imagePyramidSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> imagePyramidObjectExtensions = new ArrayList<Object>();

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
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_IMAGE_PYRAMID, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_IMAGE_PYRAMID, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.IMAGE_PYRAMID.equals(extensionLevel)){
                                imagePyramidObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_IMAGE_PYRAMID, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_IMAGE_PYRAMID, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.IMAGE_PYRAMID.equals(extensionLevel)){
                                imagePyramidSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_IMAGE_PYRAMID.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     */
    private ViewVolume readViewVolume()
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // ViewVolume
        double leftFov = DEF_LEFT_FOV;
        double rightFov = DEF_RIGHT_FOV;
        double bottomFov = DEF_BOTTOM_FOV;
        double topFov = DEF_TOP_FOV;
        double near = DEF_NEAR;
        List<SimpleTypeContainer> viewVolumeSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> viewVolumeObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // VIEW VOLUME
                        if (TAG_LEFT_FOV.equals(eName)) {
                            leftFov = parseDouble(reader.getElementText());
                        } else if (TAG_RIGHT_FOV.equals(eName)) {
                            rightFov = parseDouble(reader.getElementText());
                        } else if (TAG_BOTTOM_FOV.equals(eName)) {
                            bottomFov =  parseDouble(reader.getElementText());
                        } else if (TAG_TOP_FOV.equals(eName)) {
                            topFov =  parseDouble(reader.getElementText());
                        } else if (TAG_NEAR.equals(eName)) {
                            near = parseDouble(reader.getElementText());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_VIEW_VOLUME, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_VIEW_VOLUME, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.VIEW_VOLUME.equals(extensionLevel)){
                                viewVolumeObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_VIEW_VOLUME, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_VIEW_VOLUME, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.VIEW_VOLUME.equals(extensionLevel)){
                                viewVolumeSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_VIEW_VOLUME.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return KmlReader.kmlFactory.createViewVolume(objectSimpleExtensions, idAttributes,
                leftFov, rightFov, bottomFov, topFov, near,
                viewVolumeSimpleExtensions, viewVolumeObjectExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private Feature readPhotoOverlay()
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
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

        // AbstractOverlay
        Color color = DEF_COLOR;
        int drawOrder = DEF_DRAW_ORDER;
        Icon icon = null;
        List<SimpleTypeContainer> abstractOverlaySimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractOverlayObjectExtensions = new ArrayList<Object>();

        // PhotoOverlay
        double rotation = DEF_ROTATION;
        ViewVolume viewVolume = null;
        ImagePyramid imagePyramid = null;
        Point point = null;
        Shape shape = null;
        List<SimpleTypeContainer> photoOverlaySimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> photoOverlayObjectExtensions = new ArrayList<Object>();

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
                            snippet = this.readElementText();
                        } else if (TAG_SNIPPET_BIG.equals(eName)) {
                            snippet = this.readSnippet();
                        } else if (TAG_DESCRIPTION.equals(eName)) {
                            description = this.readElementText();
                        } else if (isAbstractView(eName)) {
                            view = this.readAbstractView(eName);
                        } else if (isAbstractTimePrimitive(eName)) {
                            timePrimitive = this.readAbstractTimePrimitive(eName);
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = new URI(reader.getElementText());
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector.add(this.readAbstractStyleSelector(eName));
                        } else if (TAG_REGION.equals(eName)) {
                            region = this.readRegion();
                        } else if (TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = this.readExtendedData();
                        } else if (TAG_META_DATA.equals(eName)) {
                            extendedData = this.readMetaData();
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
                            rotation = parseDouble(reader.getElementText());
                        } else if (TAG_VIEW_VOLUME.equals(eName)) {
                            viewVolume = this.readViewVolume();
                        } else if (TAG_IMAGE_PYRAMID.equals(eName)) {
                            imagePyramid = this.readImagePyramid();
                        } else if (TAG_POINT.equals(eName)) {
                            point = this.readPoint();
                        } else if (TAG_SHAPE.equals(eName)) {
                            shape = Shape.transform(reader.getElementText());
                        }
                    }
                    // ATOM
                    else if (URI_ATOM.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = this.readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = this.readAtomLink();
                        }
                    }
                    // XAL
                    else if (URI_XAL.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = this.readXalAddressDetails();
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_PHOTO_OVERLAY, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_PHOTO_OVERLAY, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.FEATURE.equals(extensionLevel)){
                                featureObjectExtensions.add(ext);
                            } else if(Extensions.Names.OVERLAY.equals(extensionLevel)){
                                abstractOverlayObjectExtensions.add(ext);
                            } else if(Extensions.Names.PHOTO_OVERLAY.equals(extensionLevel)){
                                photoOverlayObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_PHOTO_OVERLAY, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_PHOTO_OVERLAY, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.FEATURE.equals(extensionLevel)){
                                featureSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.OVERLAY.equals(extensionLevel)){
                                abstractOverlaySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.PHOTO_OVERLAY.equals(extensionLevel)){
                                photoOverlaySimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_PHOTO_OVERLAY.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     */
    private Feature readScreenOverlay()
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
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

        // AbstractOverlay
        Color color = DEF_COLOR;
        int drawOrder = DEF_DRAW_ORDER;
        Icon icon = null;
        List<SimpleTypeContainer> abstractOverlaySimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractOverlayObjectExtensions = new ArrayList<Object>();

        // ScreenOverlay
        Vec2 overlayXY = null;
        Vec2 screenXY = null;
        Vec2 rotationXY = null;
        Vec2 size = null;
        double rotation = DEF_ROTATION;
        List<SimpleTypeContainer> screenOverlaySimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> screenOverlayObjectExtensions = new ArrayList<Object>();

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
                            snippet = this.readElementText();
                        } else if (TAG_SNIPPET_BIG.equals(eName)) {
                            snippet = this.readSnippet();
                        } else if (TAG_DESCRIPTION.equals(eName)) {
                            description = this.readElementText();
                        } else if (isAbstractView(eName)) {
                            view = this.readAbstractView(eName);
                        } else if (isAbstractTimePrimitive(eName)) {
                            timePrimitive = this.readAbstractTimePrimitive(eName);
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = new URI(reader.getElementText());
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector.add(this.readAbstractStyleSelector(eName));
                        } else if (TAG_REGION.equals(eName)) {
                            region = this.readRegion();
                        } else if (TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = this.readExtendedData();
                        } else if (TAG_META_DATA.equals(eName)) {
                            extendedData = this.readMetaData();
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
                            rotation = parseDouble(reader.getElementText());
                        }
                    }
                    // ATOM
                    else if (URI_ATOM.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = this.readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = this.readAtomLink();
                        }
                    }
                    // XAL
                    else if (URI_XAL.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = this.readXalAddressDetails();
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_SCREEN_OVERLAY, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_SCREEN_OVERLAY, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.FEATURE.equals(extensionLevel)){
                                featureObjectExtensions.add(ext);
                            } else if(Extensions.Names.OVERLAY.equals(extensionLevel)){
                                abstractOverlayObjectExtensions.add(ext);
                            } else if(Extensions.Names.SCREEN_OVERLAY.equals(extensionLevel)){
                                screenOverlayObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_SCREEN_OVERLAY, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_SCREEN_OVERLAY, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.FEATURE.equals(extensionLevel)){
                                featureSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.OVERLAY.equals(extensionLevel)){
                                abstractOverlaySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.SCREEN_OVERLAY.equals(extensionLevel)){
                                screenOverlaySimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_SCREEN_OVERLAY.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @param eName The tag name
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private Feature readAbstractContainer(String eName) 
            throws XMLStreamException, KmlException, URISyntaxException {

        Feature resultat = null;
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
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    public AbstractView readAbstractView(String eName) 
            throws XMLStreamException, KmlException, URISyntaxException {
        
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
     * @throws URISyntaxException
     */
    private LookAt readLookAt() 
            throws XMLStreamException, KmlException, URISyntaxException {
        
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractView
        List<SimpleTypeContainer> abstractViewSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractViewObjectExtensions = new ArrayList<Object>();

        // LookAt
        double longitude = DEF_LONGITUDE;
        double latitude = DEF_LATITUDE;
        double altitude = DEF_ALTITUDE;
        double heading = DEF_HEADING;
        double tilt = DEF_TILT;
        double range = DEF_RANGE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        List<SimpleTypeContainer> lookAtSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> lookAtObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // LOOK AT
                        if (TAG_LONGITUDE.equals(eName)) {
                            longitude = parseDouble(reader.getElementText());
                        } else if (TAG_LATITUDE.equals(eName)) {
                            latitude =  parseDouble(reader.getElementText());
                        } else if (TAG_ALTITUDE.equals(eName)) {
                            altitude = parseDouble(reader.getElementText());
                        } else if (TAG_HEADING.equals(eName)) {
                            heading = parseDouble(reader.getElementText());
                        } else if (TAG_TILT.equals(eName)) {
                            if(checkVersionSimple(URI_KML_2_1))
                                tilt = KmlUtilities.checkAnglePos90(parseDouble(reader.getElementText()));
                            else
                                tilt = parseDouble(reader.getElementText());
                        } else if (TAG_RANGE.equals(eName)) {
                            range = parseDouble(reader.getElementText());
                        } else if (TAG_ALTITUDE_MODE.equals(eName)) {
                            altitudeMode = EnumAltitudeMode.transform(reader.getElementText());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_LOOK_AT, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LOOK_AT, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.VIEW.equals(extensionLevel)){
                                abstractViewObjectExtensions.add(ext);
                            } else if(Extensions.Names.LOOK_AT.equals(extensionLevel)){
                                lookAtObjectExtensions.add(ext);
                            } else if(extensionLevel == null){
                                if (ext instanceof AltitudeMode){
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_LOOK_AT, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LOOK_AT, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.VIEW.equals(extensionLevel)){
                                abstractViewSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.LOOK_AT.equals(extensionLevel)){
                                lookAtSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LOOK_AT.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return KmlReader.kmlFactory.createLookAt(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, range, altitudeMode,
                lookAtSimpleExtensions, lookAtObjectExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private Camera readCamera() 
            throws XMLStreamException, KmlException, URISyntaxException {
        
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractView
        List<SimpleTypeContainer> abstractViewSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractViewObjectExtensions = new ArrayList<Object>();

        // Camera
        double longitude = DEF_LONGITUDE;
        double latitude = DEF_LATITUDE;
        double altitude = DEF_ALTITUDE;
        double heading = DEF_HEADING;
        double tilt = DEF_TILT;
        double roll = DEF_ROLL;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        List<SimpleTypeContainer> cameraSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> cameraObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // CAMERA
                        if (TAG_LONGITUDE.equals(eName)) {
                            longitude = parseDouble(reader.getElementText());
                        } else if (TAG_LATITUDE.equals(eName)) {
                            latitude =  parseDouble(reader.getElementText());
                        } else if (TAG_ALTITUDE.equals(eName)) {
                            altitude = parseDouble(reader.getElementText());
                        } else if (TAG_HEADING.equals(eName)) {
                            heading = parseDouble(reader.getElementText());
                        } else if (TAG_TILT.equals(eName)) {
                            tilt = parseDouble(reader.getElementText());
                        } else if (TAG_ROLL.equals(eName)) {
                            roll = parseDouble(reader.getElementText());
                        } else if (TAG_ALTITUDE_MODE.equals(eName)) {
                            altitudeMode = EnumAltitudeMode.transform(reader.getElementText());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_CAMERA, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_CAMERA, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.VIEW.equals(extensionLevel)){
                                abstractViewObjectExtensions.add(ext);
                            } else if(Extensions.Names.CAMERA.equals(extensionLevel)){
                                cameraObjectExtensions.add(ext);
                            } else if(extensionLevel == null){
                                if (ext instanceof AltitudeMode){
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_CAMERA, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_CAMERA, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.VIEW.equals(extensionLevel)){
                                abstractViewSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.CAMERA.equals(extensionLevel)){
                                cameraSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_CAMERA.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @param eNameThe tag name
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    public AbstractStyleSelector readAbstractStyleSelector(String eName) 
            throws XMLStreamException, KmlException, URISyntaxException {

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
     * @throws URISyntaxException
     */
    private StyleMap readStyleMap() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractStyleSelector
        List<SimpleTypeContainer> styleSelectorSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> styleSelectorObjectExtensions = new ArrayList<Object>();

        // StyleMap
        List<Pair> pairs = new ArrayList<Pair>();
        List<SimpleTypeContainer> styleMapSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> styleMapObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    // KML
                    if (URI_KML.equals(eUri)) {
                        // STYLE MAP
                        if (TAG_PAIR.equals(eName)) {
                            pairs.add(this.readPair());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_STYLE_MAP, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_STYLE_MAP, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.STYLE_SELECTOR.equals(extensionLevel)){
                                styleSelectorObjectExtensions.add(ext);
                            } else if(Extensions.Names.STYLE_MAP.equals(extensionLevel)){
                                styleMapObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_STYLE_MAP, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_STYLE_MAP, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.STYLE_SELECTOR.equals(extensionLevel)){
                                styleSelectorSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.STYLE_MAP.equals(extensionLevel)){
                                styleMapSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_STYLE_MAP.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     */
    private Pair readPair() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // Pair
        StyleState key = DEF_STYLE_STATE;
        URI styleUrl = null;
        AbstractStyleSelector styleSelector = null;
        List<SimpleTypeContainer> pairSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> pairObjectExtensions = new ArrayList<Object>();

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
                            styleUrl = new URI(reader.getElementText());
                        } else if (isAbstractStyleSelector(eName)) {
                            checkVersion(URI_KML_2_2);
                            styleSelector = this.readAbstractStyleSelector(eName);
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_PAIR, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_PAIR, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.PAIR.equals(extensionLevel)){
                                pairObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_PAIR, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_PAIR, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.PAIR.equals(extensionLevel)){
                                pairSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_PAIR.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     */
    private Style readStyle() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractStyleSelector
        List<SimpleTypeContainer> styleSelectorSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> styleSelectorObjectExtensions = new ArrayList<Object>();

        // Style
        IconStyle iconStyle = null;
        LabelStyle labelStyle = null;
        LineStyle lineStyle = null;
        PolyStyle polyStyle = null;
        BalloonStyle balloonStyle = null;
        ListStyle listStyle = null;
        List<SimpleTypeContainer> styleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> styleObjectExtensions = new ArrayList<Object>();

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
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_STYLE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_STYLE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.STYLE_SELECTOR.equals(extensionLevel)){
                                styleSelectorObjectExtensions.add(ext);
                            } else if(Extensions.Names.STYLE.equals(extensionLevel)){
                                styleObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_STYLE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_STYLE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.STYLE_SELECTOR.equals(extensionLevel)){
                                styleSelectorSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.STYLE.equals(extensionLevel)){
                                styleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_STYLE.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
    private IconStyle readIconStyle() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractSubStyle
        List<SimpleTypeContainer> subStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> subStyleObjectExtensions = new ArrayList<Object>();

        // AbstractColorStyle
        Color color = DEF_COLOR;
        ColorMode colorMode = DEF_COLOR_MODE;
        List<SimpleTypeContainer> colorStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> colorStyleObjectExtensions = new ArrayList<Object>();

        // IconStyle
        double scale = DEF_SCALE;
        double heading = DEF_HEADING;
        BasicLink icon = null;
        Vec2 hotSpot = null;
        List<SimpleTypeContainer> iconStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> iconStyleObjectExtensions = new ArrayList<Object>();

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
                            heading = parseDouble(reader.getElementText());
                        } else if (TAG_ICON.equals(eName)) {
                            icon = this.readBasicLink(TAG_ICON);
                        } else if (TAG_HOT_SPOT.equals(eName)) {
                            hotSpot = this.readVec2(TAG_HOT_SPOT);
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_ICON_STYLE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_ICON_STYLE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.SUB_STYLE.equals(extensionLevel)){
                                subStyleObjectExtensions.add(ext);
                            } else if(Extensions.Names.COLOR_STYLE.equals(extensionLevel)){
                                colorStyleObjectExtensions.add(ext);
                            } else if(Extensions.Names.ICON_STYLE.equals(extensionLevel)){
                                iconStyleObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_ICON_STYLE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_ICON_STYLE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.SUB_STYLE.equals(extensionLevel)){
                                subStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.COLOR_STYLE.equals(extensionLevel)){
                                colorStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.ICON_STYLE.equals(extensionLevel)){
                                iconStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_ICON_STYLE.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     */
    private LabelStyle readLabelStyle() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractSubStyle
        List<SimpleTypeContainer> subStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> subStyleObjectExtensions = new ArrayList<Object>();

        // AbstractColorStyle
        Color color = DEF_COLOR;
        ColorMode colorMode = DEF_COLOR_MODE;
        List<SimpleTypeContainer> colorStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> colorStyleObjectExtensions = new ArrayList<Object>();

        // LabelStyle
        double scale = DEF_SCALE;
        List<SimpleTypeContainer> labelStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> labelStyleObjectExtensions = new ArrayList<Object>();

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
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_LABEL_STYLE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LABEL_STYLE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.SUB_STYLE.equals(extensionLevel)){
                                subStyleObjectExtensions.add(ext);
                            } else if(Extensions.Names.COLOR_STYLE.equals(extensionLevel)){
                                colorStyleObjectExtensions.add(ext);
                            } else if(Extensions.Names.LABEL_STYLE.equals(extensionLevel)){
                                labelStyleObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_LABEL_STYLE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LABEL_STYLE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.SUB_STYLE.equals(extensionLevel)){
                                subStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.COLOR_STYLE.equals(extensionLevel)){
                                colorStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.LABEL_STYLE.equals(extensionLevel)){
                                labelStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LABEL_STYLE.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     */
    private LineStyle readLineStyle() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractSubStyle
        List<SimpleTypeContainer> subStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> subStyleObjectExtensions = new ArrayList<Object>();

        // AbstractColorStyle
        Color color = DEF_COLOR;
        ColorMode colorMode = DEF_COLOR_MODE;
        List<SimpleTypeContainer> colorStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> colorStyleObjectExtensions = new ArrayList<Object>();

        // LineStyle
        double width = DEF_WIDTH;
        List<SimpleTypeContainer> lineStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> lineStyleObjectExtensions = new ArrayList<Object>();

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
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_LINE_STYLE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LINE_STYLE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.SUB_STYLE.equals(extensionLevel)){
                                subStyleObjectExtensions.add(ext);
                            } else if(Extensions.Names.COLOR_STYLE.equals(extensionLevel)){
                                colorStyleObjectExtensions.add(ext);
                            } else if(Extensions.Names.LINE_STYLE.equals(extensionLevel)){
                                lineStyleObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_LINE_STYLE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LINE_STYLE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.SUB_STYLE.equals(extensionLevel)){
                                subStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.COLOR_STYLE.equals(extensionLevel)){
                                colorStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.LINE_STYLE.equals(extensionLevel)){
                                lineStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LINE_STYLE.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
    private PolyStyle readPolyStyle() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractSubStyle
        List<SimpleTypeContainer> subStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> subStyleObjectExtensions = new ArrayList<Object>();

        // AbstractColorStyle
        Color color = DEF_COLOR;
        ColorMode colorMode = DEF_COLOR_MODE;
        List<SimpleTypeContainer> colorStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> colorStyleObjectExtensions = new ArrayList<Object>();

        // PolyStyle
        boolean fill = DEF_FILL;
        boolean outline = DEF_OUTLINE;
        List<SimpleTypeContainer> polyStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> polyStyleObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        //e COLOR STYLE
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
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_POLY_STYLE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_POLY_STYLE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.SUB_STYLE.equals(extensionLevel)){
                                subStyleObjectExtensions.add(ext);
                            } else if(Extensions.Names.COLOR_STYLE.equals(extensionLevel)){
                                colorStyleObjectExtensions.add(ext);
                            } else if(Extensions.Names.POLY_STYLE.equals(extensionLevel)){
                                polyStyleObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_POLY_STYLE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_POLY_STYLE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.SUB_STYLE.equals(extensionLevel)){
                                subStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.COLOR_STYLE.equals(extensionLevel)){
                                colorStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.POLY_STYLE.equals(extensionLevel)){
                                polyStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_POLY_STYLE.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     */
    private BalloonStyle readBalloonStyle() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractSubStyle
        List<SimpleTypeContainer> subStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> subStyleObjectExtensions = new ArrayList<Object>();

        // BalloonStyle
        Color bgColor = DEF_BG_COLOR;
        Color textColor = DEF_TEXT_COLOR;
        Object text = null;
        DisplayMode displayMode = DEF_DISPLAY_MODE;
        List<SimpleTypeContainer> balloonStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> balloonStyleObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {

                        // BALLOON STYLE
                        if (TAG_BG_COLOR.equals(eName)
                                || TAG_COLOR.equals(eName)) {
                            if(TAG_COLOR.equals(eName))
                                System.out.println("<"+TAG_COLOR+"> is deprecated in <" +
                                        TAG_BALLOON_STYLE+"> element and will be replaced by <"+
                                        TAG_BG_COLOR+"> element.");
                            bgColor = KmlUtilities.parseColor(reader.getElementText());
                        } else if (TAG_TEXT_COLOR.equals(eName)) {
                            textColor = KmlUtilities.parseColor(reader.getElementText());
                        } else if (TAG_TEXT.equals(eName)) {
                            text = this.readElementText();
                        } else if (TAG_DISPLAY_MODE.equals(eName)) {
                            checkVersion(URI_KML_2_2);
                            displayMode = DisplayMode.transform(reader.getElementText());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_BALLOON_STYLE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_BALLOON_STYLE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.SUB_STYLE.equals(extensionLevel)){
                                subStyleObjectExtensions.add(ext);
                            } else if(Extensions.Names.BALLOON_STYLE.equals(extensionLevel)){
                                balloonStyleObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_BALLOON_STYLE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_BALLOON_STYLE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.SUB_STYLE.equals(extensionLevel)){
                                subStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.BALLOON_STYLE.equals(extensionLevel)){
                                balloonStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_BALLOON_STYLE.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
    public Object readElementText() 
            throws XMLStreamException{

        Object resultat = null;
        boucle:
        while (reader.hasNext()) {
            switch (reader.getEventType()) {
                case XMLStreamConstants.CDATA:
                    resultat = kmlFactory.createCdata(reader.getText());
                    break;
                case XMLStreamConstants.CHARACTERS:
                    resultat = reader.getText();
                    break;
                case XMLStreamConstants.END_ELEMENT:
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
     * @throws URISyntaxException
     */
    private ListStyle readListStyle() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractSubStyle
        List<SimpleTypeContainer> subStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> subStyleObjectExtensions = new ArrayList<Object>();

        // ListStyle
        ListItem listItem = DEF_LIST_ITEM;
        Color bgColor = DEF_BG_COLOR;
        List<ItemIcon> itemIcons = new ArrayList<ItemIcon>();
        int maxSnippetLines = DEF_MAX_SNIPPET_LINES;
        List<SimpleTypeContainer> listStyleSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> listStyleObjectExtensions = new ArrayList<Object>();

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
                            checkVersion(URI_KML_2_2);
                            maxSnippetLines = Integer.parseInt(reader.getElementText());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_LIST_STYLE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LIST_STYLE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.SUB_STYLE.equals(extensionLevel)){
                                subStyleObjectExtensions.add(ext);
                            } else if(Extensions.Names.LIST_STYLE.equals(extensionLevel)){
                                listStyleObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_LIST_STYLE, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_LIST_STYLE, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.SUB_STYLE.equals(extensionLevel)){
                                subStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.LIST_STYLE.equals(extensionLevel)){
                                listStyleSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_LIST_STYLE.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws KmlException
     * @throws URISyntaxException
     */
    private ItemIcon readItemIcon() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // ListStyle
        List<ItemIconState> states = null;
        String href = null;
        List<SimpleTypeContainer> itemIconSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> itemIconObjectExtensions = new ArrayList<Object>();

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
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_ITEM_ICON, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_ITEM_ICON, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.ITEM_ICON.equals(extensionLevel)){
                                itemIconObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_ITEM_ICON, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_ITEM_ICON, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.ITEM_ICON.equals(extensionLevel)){
                                itemIconSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_ITEM_ICON.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
    private List<ItemIconState> readStates()
            throws XMLStreamException {

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
     * @throws URISyntaxException
     */
    private BasicLink readBasicLink(String stopTag)
            throws XMLStreamException, KmlException, URISyntaxException {

        if (stopTag == null) {
            throw new KmlException("The stop tag cannot be null. "
                    + "It's probably an <Icon> tag according to KML 2.2 specification.");
        }

        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        String href = null;
        List<SimpleTypeContainer> basicLinkSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> basicLinkObjectExtensions = new ArrayList<Object>();

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
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(stopTag, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(stopTag, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.BASIC_LINK.equals(extensionLevel)){
                                basicLinkObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(stopTag, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(stopTag, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.BASIC_LINK.equals(extensionLevel)){
                                basicLinkSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (stopTag.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @param eName
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    public AbstractTimePrimitive readAbstractTimePrimitive(String eName) 
            throws XMLStreamException, KmlException, URISyntaxException {

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
     * @throws KmlException
     * @throws URISyntaxException
     */
    private TimeSpan readTimeSpan() 
            throws XMLStreamException, KmlException, URISyntaxException{

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractTimePrimitive
        List<SimpleTypeContainer> abstractTimePrimitiveSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractTimePrimitiveObjectExtensions = new ArrayList<Object>();

        // TimeSpan
        Calendar begin = null;
        Calendar end = null;
        List<SimpleTypeContainer> timeSpanSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> timeSpanObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {
                        if (TAG_BEGIN.equals(eName)) {
                            begin = (Calendar) fastDateParser.getCalendar(reader.getElementText()).clone();
                        } else if (TAG_END.equals(eName)) {
                            end = (Calendar) fastDateParser.getCalendar(reader.getElementText()).clone();
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_TIME_SPAN, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_TIME_SPAN, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.TIME_PRIMITIVE.equals(extensionLevel)){
                                abstractTimePrimitiveObjectExtensions.add(ext);
                            } else if(Extensions.Names.TIME_SPAN.equals(extensionLevel)){
                                timeSpanObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_TIME_SPAN, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_TIME_SPAN, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.TIME_PRIMITIVE.equals(extensionLevel)){
                                abstractTimePrimitiveSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.TIME_SPAN.equals(extensionLevel)){
                                timeSpanSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_TIME_SPAN.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return KmlReader.kmlFactory.createTimeSpan(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions,
                begin, end, timeSpanSimpleExtensions, timeSpanObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private TimeStamp readTimeStamp() 
            throws XMLStreamException, KmlException, URISyntaxException{

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractTimePrimitive
        List<SimpleTypeContainer> abstractTimePrimitiveSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractTimePrimitiveObjectExtensions = new ArrayList<Object>();

        // TimeStamp
        Calendar when = null;
        List<SimpleTypeContainer> timeStampSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> timeStampObjectExtensions = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {
                        if (TAG_WHEN.equals(eName)) {
                            when = (Calendar) fastDateParser.getCalendar(reader.getElementText()).clone();
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_TIME_STAMP, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_TIME_STAMP, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.TIME_PRIMITIVE.equals(extensionLevel)){
                                abstractTimePrimitiveObjectExtensions.add(ext);
                            } else if(Extensions.Names.TIME_STAMP.equals(extensionLevel)){
                                timeStampObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_TIME_STAMP, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_TIME_STAMP, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.TIME_PRIMITIVE.equals(extensionLevel)){
                                abstractTimePrimitiveSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.TIME_STAMP.equals(extensionLevel)){
                                timeStampSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_TIME_STAMP.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return KmlReader.kmlFactory.createTimeStamp(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions,
                when, timeStampSimpleExtensions, timeStampObjectExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    public AtomPersonConstruct readAtomPersonConstruct()
            throws XMLStreamException {
        return this.atomReader.readAuthor();
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    public AtomLink readAtomLink()
            throws XMLStreamException {
        return this.atomReader.readLink();
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    public AddressDetails readXalAddressDetails() 
            throws XMLStreamException {

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
     * @throws URISyntaxException
     */
    private Feature readFolder() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
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

        // Container
        List<SimpleTypeContainer> abstractContainerSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractContainerObjectExtensions = new ArrayList<Object>();

        // Folder
        List<Feature> features = new ArrayList<Feature>();
        List<SimpleTypeContainer> folderSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> folderObjectExtensions = new ArrayList<Object>();

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
                            snippet = this.readElementText();
                        } else if (TAG_SNIPPET_BIG.equals(eName)) {
                            snippet = this.readSnippet();
                        } else if (TAG_DESCRIPTION.equals(eName)) {
                            description = this.readElementText();
                        } else if (isAbstractView(eName)) {
                            view = this.readAbstractView(eName);
                        } else if (isAbstractTimePrimitive(eName)) {
                            timePrimitive = this.readAbstractTimePrimitive(eName);
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = new URI(reader.getElementText());
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector.add(this.readAbstractStyleSelector(eName));
                        } else if (TAG_REGION.equals(eName)) {
                            region = this.readRegion();
                        } else if (TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = this.readExtendedData();
                        } else if (TAG_META_DATA.equals(eName)) {
                            extendedData = this.readMetaData();
                        }

                        // FOLDER
                        else if (isAbstractFeature(eName)) {
                            features.add(this.readAbstractFeature(eName));
                        }

                    }
                    // ATOM
                    else if (URI_ATOM.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = this.readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = this.readAtomLink();
                        }
                    }
                    // XAL
                    else if (URI_XAL.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = this.readXalAddressDetails();
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_FOLDER, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_FOLDER, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.FEATURE.equals(extensionLevel)){
                                featureObjectExtensions.add(ext);
                            } else if(Extensions.Names.CONTAINER.equals(extensionLevel)){
                                abstractContainerObjectExtensions.add(ext);
                            } else if(Extensions.Names.FOLDER.equals(extensionLevel)){
                                folderObjectExtensions.add(ext);
                            } else if(extensionLevel == null){
                                if (ext instanceof Feature){
                                   features.add((Feature) ext);
                                }
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_FOLDER, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_FOLDER, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.FEATURE.equals(extensionLevel)){
                                featureSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.CONTAINER.equals(extensionLevel)){
                                abstractContainerSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.FOLDER.equals(extensionLevel)){
                                folderSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_FOLDER.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
     * @throws URISyntaxException
     */
    private Feature readDocument() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
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

        // Container
        List<SimpleTypeContainer> abstractContainerSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractContainerObjectExtensions = new ArrayList<Object>();

        // Document
        List<Schema> schemas = new ArrayList<Schema>();
        List<Feature> features = new ArrayList<Feature>();
        List<SimpleTypeContainer> documentSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> documentObjectExtensions = new ArrayList<Object>();

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
                            snippet = this.readElementText();
                        } else if (TAG_SNIPPET_BIG.equals(eName)) {
                            snippet = this.readSnippet();
                        } else if (TAG_DESCRIPTION.equals(eName)) {
                            description = this.readElementText();
                        } else if (isAbstractView(eName)) {
                            view = this.readAbstractView(eName);
                        } else if (isAbstractTimePrimitive(eName)) {
                            timePrimitive = this.readAbstractTimePrimitive(eName);
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = new URI(reader.getElementText());
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector.add(this.readAbstractStyleSelector(eName));
                        } else if (TAG_REGION.equals(eName)) {
                            region = this.readRegion();
                        } else if (TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = this.readExtendedData();
                        } else if (TAG_META_DATA.equals(eName)) {
                            extendedData = this.readMetaData();
                        }

                        // DOCUMENT
                        else if (TAG_SCHEMA.equals(eName)) {
                            checkVersion(URI_KML_2_2);
                            schemas.add(this.readSchema());
                        } else if (isAbstractFeature(eName)) {
                            features.add(this.readAbstractFeature(eName));
                        }
                    }
                    // ATOM
                    else if (URI_ATOM.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = this.readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            link = this.readAtomLink();
                        }
                    }
                    // XAL
                    else if (URI_XAL.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = this.readXalAddressDetails();
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_DOCUMENT, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_DOCUMENT, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.FEATURE.equals(extensionLevel)){
                                featureObjectExtensions.add(ext);
                            } else if(Extensions.Names.CONTAINER.equals(extensionLevel)){
                                abstractContainerObjectExtensions.add(ext);
                            } else if(Extensions.Names.DOCUMENT.equals(extensionLevel)){
                                documentObjectExtensions.add(ext);
                            } else if(extensionLevel == null){
                                if (ext instanceof Feature){
                                   features.add((Feature) ext);
                                }
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_DOCUMENT, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_DOCUMENT, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.FEATURE.equals(extensionLevel)){
                                featureSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.CONTAINER.equals(extensionLevel)){
                                abstractContainerSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.DOCUMENT.equals(extensionLevel)){
                                documentSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_DOCUMENT.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }

        return KmlReader.kmlFactory.createDocument(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails,
                phoneNumber, snippet, description,
                view, timePrimitive, styleUrl, styleSelector, region, extendedData,
                featureSimpleExtensions, featureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions,
                schemas, features, documentSimpleExtensions, documentObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private Schema readSchema()
            throws XMLStreamException {

        // Schema
        List<SimpleField> simplefields = new ArrayList<SimpleField>();
        List<Object> schemaExtensions = new ArrayList<Object>();
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

        return KmlReader.kmlFactory.createSchema(simplefields, name, id, schemaExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private SimpleField readSimpleField()
            throws XMLStreamException {

        // SimpleField
        Object displayName = null;
        List<Object> simpleFieldExtensions = new ArrayList<Object>();
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
                            displayName = this.readElementText();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_SIMPLE_FIELD.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }

        return KmlReader.kmlFactory.createSimpleField(displayName, type, name, simpleFieldExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Feature readNetworkLink() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
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

        // NetworkLink
        boolean refreshVisibility = DEF_REFRESH_VISIBILITY;
        boolean flyToView = DEF_FLY_TO_VIEW;
        Link link = null;
        List<SimpleTypeContainer> networkLinkSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> networkLinkObjectExtensions = new ArrayList<Object>();

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
                            snippet = this.readElementText();
                        } else if (TAG_SNIPPET_BIG.equals(eName)) {
                            snippet = this.readSnippet();
                        } else if (TAG_DESCRIPTION.equals(eName)) {
                            description = this.readElementText();
                        } else if (isAbstractView(eName)) {
                            view = this.readAbstractView(eName);
                        } else if (isAbstractTimePrimitive(eName)) {
                            timePrimitive = this.readAbstractTimePrimitive(eName);
                        } else if (TAG_STYLE_URL.equals(eName)) {
                            styleUrl = new URI(reader.getElementText());
                        } else if (isAbstractStyleSelector(eName)) {
                            styleSelector.add(this.readAbstractStyleSelector(eName));
                        } else if (TAG_REGION.equals(eName)) {
                            region = this.readRegion();
                        } else if (TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = this.readExtendedData();
                        } else if (TAG_META_DATA.equals(eName)) {
                            extendedData = this.readMetaData();
                        }

                        // NETWORK LINK
                        else if (TAG_REFRESH_VISIBILITY.equals(eName)) {
                            refreshVisibility = parseBoolean(reader.getElementText());
                        } else if (TAG_FLY_TO_VIEW.equals(eName)) {
                            flyToView = parseBoolean(reader.getElementText());
                        } else if (TAG_LINK.equals(eName)) {
                            link = this.readLink(eName);
                        } else if (TAG_URL.equals(eName)) {
                            link = this.readUrl(eName);
                        }
                    }
                    // ATOM
                    else if (URI_ATOM.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_ATOM_AUTHOR.equals(eName)) {
                            author = this.readAtomPersonConstruct();
                        } else if (TAG_ATOM_LINK.equals(eName)) {
                            atomLink = this.readAtomLink();
                        }
                    }
                    //XAL
                    else if (URI_XAL.equals(eUri)) {
                        checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = this.readXalAddressDetails();
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_NETWORK_LINK, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_NETWORK_LINK, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.FEATURE.equals(extensionLevel)){
                                featureObjectExtensions.add(ext);
                            } else if(Extensions.Names.NETWORK_LINK.equals(extensionLevel)){
                                networkLinkObjectExtensions.add(ext);
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_NETWORK_LINK, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_NETWORK_LINK, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.FEATURE.equals(extensionLevel)){
                                featureSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.NETWORK_LINK.equals(extensionLevel)){
                                networkLinkSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_NETWORK_LINK.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
    private Point readPoint() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        IdAttributes idAttributes = this.readIdAttributes();

        // AbstractGeometry
        List<SimpleTypeContainer> abstractGeometrySimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> abstractGeometryObjectExtensions = new ArrayList<Object>();

        // Point
        boolean extrude = DEF_EXTRUDE;
        AltitudeMode altitudeMode = DEF_ALTITUDE_MODE;
        Coordinates coordinates = null;
        List<SimpleTypeContainer> pointSimpleExtensions = new ArrayList<SimpleTypeContainer>();
        List<Object> pointObjectExtensions = new ArrayList<Object>();

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
                            altitudeMode = EnumAltitudeMode.transform(reader.getElementText());
                        } else if (TAG_COORDINATES.equals(eName)) {
                            coordinates = readCoordinates(reader.getElementText());
                        }
                    }
                    // EXTENSIONS
                    else {
                        KmlExtensionReader r;
                        if((r = this.getComplexExtensionReader(TAG_POINT, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_POINT, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.GEOMETRY.equals(extensionLevel)){
                                abstractGeometryObjectExtensions.add(ext);
                            } else if(Extensions.Names.POINT.equals(extensionLevel)){
                                pointObjectExtensions.add(ext);
                            } else if(extensionLevel == null){
                                if (ext instanceof AltitudeMode){
                                    altitudeMode = (AltitudeMode) ext;
                                }
                            }
                        } else if ((r = this.getSimpleExtensionReader(TAG_POINT, eName)) != null){
                            Entry<Object, Extensions.Names> result = r.readExtensionElement(TAG_POINT, eName);
                            Object ext = result.getKey();
                            Extensions.Names extensionLevel = result.getValue();
                            if(Extensions.Names.OBJECT.equals(extensionLevel)){
                                objectSimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.GEOMETRY.equals(extensionLevel)){
                                abstractGeometrySimpleExtensions.add((SimpleTypeContainer) ext);
                            } else if(Extensions.Names.POINT.equals(extensionLevel)){
                                pointSimpleExtensions.add((SimpleTypeContainer) ext);
                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_POINT.equals(reader.getLocalName())
                            && URI_KML.contains(reader.getNamespaceURI())) {
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
    public Coordinates readCoordinates(String coordinates) {

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
    public IdAttributes readIdAttributes() {
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
    public boolean isAbstractGeometry(String eName) {
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
    public boolean isAbstractFeature(String eName) {
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
    public boolean isAbstractContainer(String eName) {
        return (TAG_FOLDER.equals(eName)
                || TAG_DOCUMENT.equals(eName));
    }

    /**
     *
     * @param eName the tag name.
     * @return true if the tag name is an AbstractOverlay element.
     */
    public boolean isAbstractOverlay(String eName) {
        return (TAG_GROUND_OVERLAY.equals(eName)
                || TAG_PHOTO_OVERLAY.equals(eName)
                || TAG_SCREEN_OVERLAY.equals(eName));
    }

    /**
     *
     * @param eName the tag name.
     * @return true if the tag name is an AbstractView element.
     */
    public boolean isAbstractView(String eName) {
        return (TAG_LOOK_AT.equals(eName)
                || TAG_CAMERA.equals(eName));
    }

    /**
     *
     * @param eName the tag name.
     * @return true if the tag name is an AbstractTimePrimitive element.
     */
    public boolean isAbstractTimePrimitive(String eName) {
        return (TAG_TIME_STAMP.equals(eName)
                || TAG_TIME_SPAN.equals(eName));
    }

    /**
     *
     * @param eName the tag name.
     * @return true if the tag name is an AbstractStyleSelector element.
     */
    public boolean isAbstractStyleSelector(String eName) {
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
    public boolean isAbstractColorStyle(String eName){
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
    public boolean isAbstractObject(String eName) {
        // Traiter le cas particuloer du TAG_ICON qui peut être un basicLink
        return (isAbstractFeature(eName)
                || isAbstractGeometry(eName)
                || isAbstractStyleSelector(eName)
                || isAbstractSubStyle(eName)
                || isAbstractView(eName)
                || TAG_PAIR.equals(eName)
                || TAG_LINK.equals(eName)
                || TAG_VIEW_VOLUME.equals(eName)
                || TAG_REGION.equals(eName)
                || TAG_LOD.equals(eName)
                || TAG_ORIENTATION.equals(eName)
                || TAG_SCHEMA_DATA.equals(eName));
    }

    /**
     * 
     * @param eName
     * @return
     */
    public boolean isAbstractLatLonBox(String eName) {
        return (TAG_LAT_LON_ALT_BOX.equals(eName)
                || TAG_LAT_LON_BOX.equals(eName));
    }

    /**
     *
     * @param versions
     * @throws KmlException
     */
    public void checkVersion(String... versions) throws KmlException{
        for(String version : versions)
            if(this.URI_KML.equals(version))
                return;
        throw new KmlException("Kml reader error : Element not allowed by "+this.URI_KML+" namespace.");
    }

    /**
     *
     * @param version
     * @return
     */
    public boolean checkVersionSimple(String version){
        return this.URI_KML.equals(version);
    }

    /*
     * READING EXTENSIONS METHODS
     */

    public Object readExtensionsScheduler(String containingTag, String contentTag)
            throws KmlException, XMLStreamException, URISyntaxException{
        Object resultat = this.getComplexExtensionReader(containingTag, contentTag);
        if (resultat == null){
            resultat = this.getSimpleExtensionReader(containingTag, contentTag);
        }
        return resultat;
    }
}

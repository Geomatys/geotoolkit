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

import com.vividsolutions.jts.geom.CoordinateSequence;

import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.opengis.feature.Feature;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.xal.xml.XalWriter;
import org.geotoolkit.atom.xml.AtomWriter;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.model.AbstractColorStyle;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.AbstractLatLonBox;
import org.geotoolkit.data.kml.model.AbstractObject;
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
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.Extensions.Names;
import org.geotoolkit.data.kml.model.GridOrigin;
import org.geotoolkit.data.kml.model.Icon;
import org.geotoolkit.data.kml.model.IconStyle;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.ImagePyramid;
import org.geotoolkit.data.kml.model.ItemIcon;
import org.geotoolkit.data.kml.model.ItemIconState;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
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
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.model.Url;
import org.geotoolkit.data.kml.model.Vec2;
import org.geotoolkit.data.kml.model.ViewRefreshMode;
import org.geotoolkit.data.kml.model.ViewVolume;
import org.geotoolkit.xal.model.AddressDetails;
import org.geotoolkit.xal.model.XalException;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import org.geotoolkit.data.kml.xsd.Cdata;
import org.geotoolkit.xml.StaxStreamWriter;

import org.apache.sis.util.logging.Logging;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 * This class provides a method to read KML files, version 2.2.
 *
 * @author Samuel Andrés
 * @module pending
 */
public class KmlWriter extends StaxStreamWriter {

    private static String URI_KML;
    private final XalWriter XAL_WRITER = new XalWriter();
    private final AtomWriter ATOM_WRITER = new AtomWriter();
    private final List<StaxStreamWriter> extensionWriters = new ArrayList<>();
    private final List<StaxStreamWriter> dataWriters = new ArrayList<>();

    public KmlWriter() {
    }

    /**
     * Set output. This method doesn't indicate kml uri version whose detection
     * is automatic at kml root writing. In other cases, method with Kml version uri
     * argument is necessary.
     */
    @Override
    public void setOutput(Object output) throws XMLStreamException, IOException {
        super.setOutput(output);
        XAL_WRITER.setOutput(writer);
        ATOM_WRITER.setOutput(writer);
    }

    /**
     * Set input. This method is necessary if Kml elements are read out of Kml document
     * with kml root elements.
     */
    public void setOutput(Object output, String KmlVersionUri) throws XMLStreamException, IOException, KmlException {
        setOutput(output);
        if (URI_KML_2_2.equals(KmlVersionUri) || URI_KML_2_1.equals(KmlVersionUri)) {
            URI_KML = KmlVersionUri;
        } else {
            throw new KmlException("Bad Kml version Uri. This reader supports 2.1 and 2.2 versions.");
        }
    }

    /**
     * This method adds a writer for given uri extensions.
     */
    public void addExtensionWriter(String uri, StaxStreamWriter writer)
            throws KmlException, IOException, XMLStreamException
    {
        if (writer instanceof KmlExtensionWriter) {
            extensionWriters.add(writer);
            writer.setOutput(this.writer);
        } else {
            throw new KmlException("Extension writer must implements "
                    + KmlExtensionWriter.class.getName() + " interface.");
        }
    }

    /**
     * This method adds a customized writers for data containers.
     */
    public void addDataWriter(String uri, StaxStreamWriter writer)
            throws KmlException, IOException, XMLStreamException
    {
        if (writer instanceof KmlExtensionWriter) {
            dataWriters.add(writer);
            writer.setOutput(this.writer);
        } else {
            throw new KmlException("Extension writer must implements "
                    + KmlExtensionWriter.class.getName() + " interface.");
        }
    }

    /**
     * This method writes a Kml 2.2 / 2.1 document into the file assigned to the KmlWriter.
     *
     * @param kml The Kml object to write.
     */
    public void write(Kml kml) throws XMLStreamException, KmlException {

        // FACULTATIF : INDENTATION DE LA SORTIE
        //streamWriter = new IndentingXMLStreamWriter(streamWriter);

        writer.writeStartDocument("UTF-8", "1.0");
        URI_KML = kml.getVersion();
        writer.setDefaultNamespace(URI_KML);
        writer.writeStartElement(URI_KML, TAG_KML);
        //writer.writeDefaultNamespace(URI_KML);

        if (URI_KML.equals(URI_KML_2_2)) {
            for (String uri : kml.getExtensionsUris().keySet()) {
                writer.writeNamespace(kml.getExtensionsUris().get(uri), uri);
                writer.setPrefix(kml.getExtensionsUris().get(uri), uri);
            }
        }
        writeKml(kml);
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
    }

    /**
     *
     * @param kml The Kml object to write.
     */
    private void writeKml(Kml kml) throws XMLStreamException, KmlException {
        writeNetworkLinkControl(kml.getNetworkLinkControl());
        writeAbstractFeature(kml.getAbstractFeature());
        writeStandardExtensionLevel(kml.extensions(), Names.KML);
    }

    private void writeNetworkLinkControl(NetworkLinkControl value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_NETWORK_LINK_CONTROL);
            writeMinRefreshPeriod(value.getMinRefreshPeriod());
            if (checkVersionSimple(URI_KML_2_2)) {
                writeMaxSessionLength(value.getMaxSessionLength());
            }
            writeCookie         (value.getCookie());
            writeMessage        (value.getMessage());
            writeLinkName       (value.getLinkName());
            writeLinkDescription(value.getLinkDescription());
            writeLinkSnippet    (value.getLinkSnippet());
            writeExpires        (value.getExpires());
            writeUpdate         (value.getUpdate());
            writeAbstractView   (value.getView());
            writeStandardExtensionLevel(value.extensions(), Names.NETWORK_LINK_CONTROL);
            writer.writeEndElement();
        }
    }

    public void writeUpdate(Update value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_UPDATE);
            writeTargetHref(value.getTargetHref());
            for (Object object : value.getUpdates()) {
                if (object instanceof Create) {
                    writeCreate((Create) object);
                } else if (object instanceof Delete) {
                    writeDelete((Delete) object);
                } else if (object instanceof Change) {
                    writeChange((Change) object);
                } else if (object instanceof Feature) {
                    checkVersion(URI_KML_2_1);
                    writeReplace((Feature) object);
                } else {
                    throw new KmlException(object.getClass().getCanonicalName() + " instance is not allowed here");
                }
            }
            writer.writeEndElement();
        }
    }

    @Deprecated
    private void writeReplace(Feature value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_REPLACE);
        writeAbstractFeature(value);
        writer.writeEndElement();
    }

    private void writeCreate(Create value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_CREATE);
        for (Feature container : value.getContainers()) {
            writeAbstractContainer(container);
        }
        writer.writeEndElement();
    }

    private void writeDelete(Delete value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_DELETE);
        for (Feature feature : value.getFeatures()) {
            writeAbstractFeature(feature);
        }
        writer.writeEndElement();
    }

    private void writeChange(Change value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_CHANGE);
        for (Object object : value.getObjects()) {
            writeObject(object);
        }
        writer.writeEndElement();
    }

    private void writeObject(Object value) throws XMLStreamException, KmlException {
        if (value instanceof Region) {
            writeRegion((Region) value);
        } else if (value instanceof Lod) {
            writeLod((Lod) value);
        } else if (value instanceof Link) {
            writeLink((Link) value);
        } else if (value instanceof Icon) {
            writeIcon((Icon) value);
        } else if (value instanceof Location) {
            writeLocation((Location) value);
        } else if (value instanceof Orientation) {
            writeOrientation((Orientation) value);
        } else if (value instanceof ResourceMap) {
            writeResourceMap((ResourceMap) value);
        } else if (value instanceof SchemaData) {
            writeSchemaData((SchemaData) value);
        } else if (value instanceof Scale) {
            writeScale((Scale) value);
        } else if (value instanceof Alias) {
            writeAlias((Alias) value);
        } else if (value instanceof ViewVolume) {
            writeViewVolume((ViewVolume) value);
        } else if (value instanceof ImagePyramid) {
            writeImagePyramid((ImagePyramid) value);
        } else if (value instanceof Pair) {
            writePair((Pair) value);
        } else if (value instanceof ItemIcon) {
            writeItemIcon((ItemIcon) value);
        } else if (value instanceof Feature) {
            writeAbstractFeature((Feature) value);
        } else if (value instanceof AbstractGeometry) {
            writeGeometry((AbstractGeometry) value);
        } else if (value instanceof AbstractStyleSelector) {
            writeStyleSelector((AbstractStyleSelector) value);
        } else if (value instanceof AbstractSubStyle) {
            writeSubStyle((AbstractSubStyle) value);
        } else if (value instanceof AbstractView) {
            writeAbstractView((AbstractView) value);
        } else if (value instanceof AbstractTimePrimitive) {
            writeTimePrimitive((AbstractTimePrimitive) value);
        } else if (value instanceof AbstractLatLonBox) {
            writeLatLonBox((AbstractLatLonBox) value);
        }
    }

    private void writeLatLonBox(AbstractLatLonBox value) throws XMLStreamException, KmlException {
        if (value instanceof LatLonAltBox) {
            writeLatLonAltBox((LatLonAltBox) value);
        } else if (value instanceof LatLonBox) {
            writeLatLonBox((LatLonBox) value);
        }
    }

    private void writeSubStyle(AbstractSubStyle value) throws XMLStreamException, KmlException {
        if (value instanceof BalloonStyle) {
            writeBalloonStyle((BalloonStyle) value);
        } else if (value instanceof ListStyle) {
            writeListStyle((ListStyle) value);
        } else if (value instanceof AbstractColorStyle) {
            writeColorStyle((AbstractColorStyle) value);
        }
    }

    private void writeColorStyle(AbstractColorStyle value) throws XMLStreamException, KmlException {
        if (value instanceof IconStyle) {
            writeIconStyle((IconStyle) value);
        } else if (value instanceof LabelStyle) {
            writeLabelStyle((LabelStyle) value);
        } else if (value instanceof PolyStyle) {
            writePolyStyle((PolyStyle) value);
        } else if (value instanceof LineStyle) {
            writeLineStyle((LineStyle) value);
        }
    }

    /**
     * This method writes the common fields for instances of AbstractObject.
     *
     * @param value The AbstractObject to write.
     */
    public void writeCommonAbstractObject(AbstractObject value) throws XMLStreamException, KmlException {
        writeIdAttributes(value.getIdAttributes());
        writeSimpleExtensionsScheduler(Names.OBJECT, value.extensions().simples(Names.OBJECT));
    }

    /**
     * This method writes identification attributes.
     *
     * @param value The IdAttributes to write.
     */
    private void writeIdAttributes(IdAttributes value) throws XMLStreamException {
        if (value != null) {
            if (value.getId() != null) {
                writer.writeAttribute(ATT_ID, value.getId());
            }
            if (value.getTargetId() != null) {
                writer.writeAttribute(ATT_TARGET_ID, value.getTargetId());
            }
        }
    }

    /**
     *
     * @param value The AbstractFeature to write.
     */
    private void writeAbstractFeature(Feature value) throws XMLStreamException, KmlException {
        if (value != null) {
            if (KmlModelConstants.TYPE_CONTAINER.isAssignableFrom(value.getType())) {
                writeAbstractContainer(value);
            } else if (value.getType().equals(KmlModelConstants.TYPE_NETWORK_LINK)) {
                writeNetworkLink(value);
            } else if (KmlModelConstants.TYPE_OVERLAY.isAssignableFrom(value.getType())) {
                writeAbstractOverlay(value);
            } else if (value.getType().equals(KmlModelConstants.TYPE_PLACEMARK)) {
                writePlacemark(value);
            } else { //FEATURE EXTENSIONS SUBSTITUTION
                for (StaxStreamWriter candidate : extensionWriters) {
                    if (((KmlExtensionWriter) candidate).canHandleComplex(URI_KML, null, value)) {
                        ((KmlExtensionWriter) candidate).writeComplexExtensionElement(URI_KML, null, value);
                    }
                }
            }
        }
    }

    private void writeNetworkLink(Feature value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_NETWORK_LINK);
        writeCommonAbstractFeature(value);
        writeRefreshVisibility((Boolean) value.getPropertyValue(KmlConstants.TAG_REFRESH_VISIBILITY));
        writeFlyToView((Boolean) value.getPropertyValue(KmlConstants.TAG_FLY_TO_VIEW));

        Object link = value.getPropertyValue(KmlConstants.TAG_LINK);
        if (link instanceof Url) {
            writeUrl((Url) link);
        } else if (link instanceof Link) {
            writeLink((Link) link);
        }
        writeStandardExtensionLevel((Extensions) value.getPropertyValue(KmlConstants.TAG_EXTENSIONS), Names.NETWORK_LINK);
        writer.writeEndElement();
    }

    /**
     * This method writes the common fields for instances of AbstractFeature.
     *
     * @param feature The AbstractFeature to write.
     */
    public void writeCommonAbstractFeature(Feature feature) throws XMLStreamException, KmlException {
        writeIdAttributes((IdAttributes) feature.getPropertyValue(KmlConstants.ATT_ID));
        writeSimpleExtensionsScheduler(Names.OBJECT,
                ((Extensions) feature.getPropertyValue(KmlConstants.TAG_EXTENSIONS)).simples(Names.OBJECT));

        writeName               ((String)               feature.getPropertyValue(KmlConstants.TAG_NAME));
        writeVisibility         ((Boolean)              feature.getPropertyValue(KmlConstants.TAG_VISIBILITY));
        writeOpen               ((Boolean)              feature.getPropertyValue(KmlConstants.TAG_OPEN));
        writeAtomPersonConstruct((AtomPersonConstruct)  feature.getPropertyValue(KmlConstants.TAG_ATOM_AUTHOR));
        writeAtomLink           ((AtomLink)             feature.getPropertyValue(KmlConstants.TAG_ATOM_LINK));
        writeAddress            ((String)               feature.getPropertyValue(KmlConstants.TAG_ADDRESS));
        writeXalAddresDetails   ((AddressDetails)       feature.getPropertyValue(KmlConstants.TAG_XAL_ADDRESS_DETAILS));
        writePhoneNumber        ((String)               feature.getPropertyValue(KmlConstants.TAG_PHONE_NUMBER));
        writeSnippet            (                       feature.getPropertyValue(KmlConstants.TAG_SNIPPET));
        writeDescription        (                       feature.getPropertyValue(KmlConstants.TAG_DESCRIPTION));
        writeAbstractView       ((AbstractView)         feature.getPropertyValue(KmlConstants.TAG_VIEW));
        writeTimePrimitive(     (AbstractTimePrimitive) feature.getPropertyValue(KmlConstants.TAG_TIME_PRIMITIVE));
        writeStyleUrl           ((URI)                  feature.getPropertyValue(KmlConstants.TAG_STYLE_URL));
        for (final Object value : (Iterable<?>) feature.getPropertyValue(KmlConstants.TAG_STYLE_SELECTOR)) {
            writeStyleSelector((AbstractStyleSelector) value);
        }
        writeRegion((Region) feature.getPropertyValue(KmlConstants.TAG_REGION));
        writeExtendedData(feature.getPropertyValue(KmlConstants.TAG_EXTENDED_DATA));
        writeStandardExtensionLevel((Extensions) feature.getPropertyValue(KmlConstants.TAG_EXTENSIONS), Names.FEATURE);
    }

    private void writeExtendedData(Object value) throws XMLStreamException, KmlException {
        if (value != null) {
            if (value instanceof Collection) {
                for (Object ex : (Collection)value) {
                    if (ex instanceof ExtendedData) {
                        writeExtendedData((ExtendedData) ex);
                    } else if (ex instanceof Metadata) {
                        writeMetaData((Metadata) ex);
                    }
                }
            } else if (value instanceof ExtendedData) {
                writeExtendedData((ExtendedData) value);
            } else if (value instanceof Metadata) {
                writeMetaData((Metadata) value);
            }
        }
    }

    public void writeExtendedData(ExtendedData value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_EXTENDED_DATA);
            for (Data data : value.getDatas()) {
                writeData(data);
            }
            for (SchemaData schemaData : value.getSchemaData()) {
                writeSchemaData(schemaData);
            }
            writeDataScheduler(value.getAnyOtherElements());
            writer.writeEndElement();
        }
    }

    @Deprecated
    private void writeMetaData(Metadata value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_META_DATA);
            writeDataScheduler(value.getContent());
            writer.writeEndElement();
        }
    }

    private void writeSchemaData(SchemaData value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_SCHEMA_DATA);
            writer.writeAttribute(ATT_SCHEMA_URL, value.getSchemaURL().toString());
            writeCommonAbstractObject(value);
            for (SimpleData simpleData : value.getSimpleDatas()) {
                writeSimpleData(simpleData);
            }
            writer.writeEndElement();
        }
    }

    private void writeSimpleData(SimpleData value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_SIMPLE_DATA);
            writer.writeAttribute(ATT_NAME, value.getName());
            writer.writeCharacters(value.getContent());
            writer.writeEndElement();
        }
    }

    private void writeData(Data value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_DATA);
            writer.writeAttribute(ATT_NAME, value.getName());
            writeCommonAbstractObject(value);
            writeDisplayName(value.getDisplayName());
            writeValue(value.getValue());
            if (value.getDataExtensions() != null) {
                // TODO
            }
            writer.writeEndElement();
        }
    }

    private void writeRegion(Region value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_REGION);
            writeCommonAbstractObject(value);
            writeLatLonAltBox(value.getLatLonAltBox());
            if (value.getLod() != null) {
                writeLod(value.getLod());
            }
            writeStandardExtensionLevel(value.extensions(), Names.REGION);
            writer.writeEndElement();
        }
    }

    private void writeLod(Lod value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_LOD);
            writeCommonAbstractObject(value);
            writeMinLodPixels (value.getMinLodPixels());
            writeMaxLodPixels (value.getMaxLodPixels());
            writeMinFadeExtent(value.getMinFadeExtent());
            writeMaxFadeExtent(value.getMaxFadeExtent());
            writeStandardExtensionLevel(value.extensions(), Names.LOD);
            writer.writeEndElement();
        }
    }

    private void writeLatLonAltBox(LatLonAltBox value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_LAT_LON_ALT_BOX);
            writeCommonLatLonBox(value);
            writeMinAltitude (value.getMinAltitude());
            writeMaxAltitude (value.getMaxAltitude());
            writeAltitudeMode(value.getAltitudeMode());
            writeStandardExtensionLevel(value.extensions(), Names.LAT_LON_ALT_BOX);
            writer.writeEndElement();
        }
    }

    private void writeAtomPersonConstruct(AtomPersonConstruct value) throws XMLStreamException {
        if (value != null) {
            ATOM_WRITER.writeAuthor(value);
        }
    }

    private void writeAtomLink(AtomLink value) throws XMLStreamException {
        if (value != null) {
            ATOM_WRITER.writeLink(value);
        }
    }

    private void writeXalAddresDetails(AddressDetails value) throws XMLStreamException {
        if (value != null) {
            try {
                XAL_WRITER.writeAddressDetails(value);
            } catch (XalException ex) {
                Logging.getLogger("org.geotoolkit.data.kml.map").log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     *
     * @param value The AbstractTimePrimitive to write.
     */
    private void writeTimePrimitive(AbstractTimePrimitive value) throws XMLStreamException, KmlException {
        if (value != null) {
            if (value instanceof TimeSpan) {
                writeTimeSpan((TimeSpan) value);
            } else if (value instanceof TimeStamp) {
                writeTimeStamp((TimeStamp) value);
            }
        }
    }

    private void writeTimeSpan(TimeSpan value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_TIME_SPAN);
            writeCommonAbstractTimePrimitive(value);
            writeBegin(value.getBegin());
            writeEnd(value.getEnd());
            writeStandardExtensionLevel(value.extensions(), Names.TIME_SPAN);
            writer.writeEndElement();
        }
    }

    private void writeTimeStamp(TimeStamp value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_TIME_STAMP);
            writeCommonAbstractTimePrimitive(value);
            writeWhen(value.getWhen());
            writeStandardExtensionLevel(value.extensions(), Names.TIME_STAMP);
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param value The AbstractView to write.
     */
    public void writeAbstractView(AbstractView value) throws XMLStreamException, KmlException {
        if (value != null) {
            if (value instanceof LookAt) {
                writeLookAt((LookAt) value);
            } else if (value instanceof Camera) {
                writeCamera((Camera) value);
            }
        }
    }

    private void writeLookAt(LookAt value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_LOOK_AT);
        writeCommonView  (value);
        writeLongitude   (value.getLongitude());
        writeLatitude    (value.getLatitude());
        writeAltitude    (value.getAltitude());
        writeHeading     (value.getHeading());
        writeTilt        (value.getTilt());
        writeRange       (value.getRange());
        writeAltitudeMode(value.getAltitudeMode());
        writeStandardExtensionLevel(value.extensions(), Names.LOOK_AT);
        writer.writeEndElement();
    }

    private void writeCamera(Camera value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_CAMERA);
        writeCommonView  (value);
        writeLongitude   (value.getLongitude());
        writeLatitude    (value.getLatitude());
        writeAltitude    (value.getAltitude());
        writeHeading     (value.getHeading());
        writeTilt        (value.getTilt());
        writeRoll        (value.getRoll());
        writeAltitudeMode(value.getAltitudeMode());
        writeStandardExtensionLevel(value.extensions(), Names.CAMERA);
        writer.writeEndElement();
    }

    /**
     * This method writes the common fields for AbstractView instances.
     *
     * @param value The AbstractView to write.
     */
    private void writeCommonView(AbstractView value) throws XMLStreamException, KmlException {
        writeCommonAbstractObject(value);
        writeStandardExtensionLevel(value.extensions(), Names.VIEW);
    }

    /**
     * This method writes the common fields for AbstractTimePrimitive instances.
     *
     * @param value The AbstractTimePrimitive to write.
     */
    public void writeCommonAbstractTimePrimitive(AbstractTimePrimitive value)
            throws XMLStreamException, KmlException
    {
        writeCommonAbstractObject(value);
        writeStandardExtensionLevel(value.extensions(), Names.TIME_PRIMITIVE);
    }

    /**
     *
     * @param value The AbstractStyleSelector to write.
     */
    private void writeStyleSelector(AbstractStyleSelector value)
            throws XMLStreamException, KmlException
    {
        if (value instanceof Style) {
            writeStyle((Style) value);
        } else if (value instanceof StyleMap) {
            writeStyleMap((StyleMap) value);
        }
    }

    /**
     * This method writes the common fields for AbstractStyleSelector instances.
     */
    private void writeCommonStyleSelector(AbstractStyleSelector value) throws XMLStreamException, KmlException {
        writeCommonAbstractObject(value);
        writeStandardExtensionLevel(value.extensions(), Names.STYLE_SELECTOR);
    }

    private void writeStyleMap(StyleMap value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_STYLE_MAP);
        writeCommonStyleSelector(value);
        for (Pair pair : value.getPairs()) {
            writePair(pair);
        }
        writeStandardExtensionLevel(value.extensions(), Names.STYLE_MAP);
        writer.writeEndElement();
    }

    private void writePair(Pair value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_PAIR);
        writeCommonAbstractObject(value);
        writeKey(value.getKey());
        writeStyleUrl(value.getStyleUrl());
        if (value.getAbstractStyleSelector() != null) {
            checkVersion(URI_KML_2_2);
            writeStyleSelector(value.getAbstractStyleSelector());
        }
        writeStandardExtensionLevel(value.extensions(), Names.PAIR);
        writer.writeEndElement();
    }

    private void writeStyle(Style value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_STYLE);
        writeCommonStyleSelector(value);
        writeIconStyle   (value.getIconStyle());
        writeLabelStyle  (value.getLabelStyle());
        writeLineStyle   (value.getLineStyle());
        writePolyStyle   (value.getPolyStyle());
        writeBalloonStyle(value.getBalloonStyle());
        writeListStyle(value.getListStyle());
        writeStandardExtensionLevel(value.extensions(), Names.STYLE);
        writer.writeEndElement();
    }

    private void writeIconStyle(IconStyle value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_ICON_STYLE);
            writeCommonColorStyle(value);
            writeScale  (value.getScale());
            writeHeading(value.getHeading());
            writeIcon   (value.getIcon());
            writeHotSpot(value.getHotSpot());
            writeStandardExtensionLevel(value.extensions(), Names.ICON_STYLE);
            writer.writeEndElement();
        }
    }

    private void writeLabelStyle(LabelStyle value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_LABEL_STYLE);
            writeCommonColorStyle(value);
            writeScale(value.getScale());
            writeStandardExtensionLevel(value.extensions(), Names.LABEL_STYLE);
            writer.writeEndElement();
        }
    }

    private void writeLineStyle(LineStyle value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_LINE_STYLE);
            writeCommonColorStyle(value);
            writeWidth(value.getWidth());
            writeStandardExtensionLevel(value.extensions(), Names.LINE_STYLE);
            writer.writeEndElement();
        }
    }

    private void writePolyStyle(PolyStyle value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_POLY_STYLE);
            writeCommonColorStyle(value);
            writeFill(value.getFill());
            writeOutline(value.getOutline());
            writeStandardExtensionLevel(value.extensions(), Names.POLY_STYLE);
            writer.writeEndElement();
        }
    }

    private void writeBalloonStyle(BalloonStyle value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_BALLOON_STYLE);
            writeCommonSubStyle(value);
            writeBgColor  (value.getBgColor());
            writeTextColor(value.getTextColor());
            writeText     (value.getText());
            if (checkVersionSimple(URI_KML_2_2)) {
                writeDisplayMode(value.getDisplayMode());
            }
            writeStandardExtensionLevel(value.extensions(), Names.BALLOON_STYLE);
            writer.writeEndElement();
        }
    }

    private void writeListStyle(ListStyle value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_LIST_STYLE);
            writeCommonSubStyle(value);
            writeListItem(value.getListItem());
            writeBgColor(value.getBgColor());
            for (ItemIcon itemIcon : value.getItemIcons()) {
                writeItemIcon(itemIcon);
            }
            if (checkVersionSimple(URI_KML_2_2)) {
                writeMaxSnippetLines(value.getMaxSnippetLines());
            }
            writeStandardExtensionLevel(value.extensions(), Names.LIST_STYLE);
            writer.writeEndElement();
        }
    }

    private void writeItemIcon(ItemIcon value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_ITEM_ICON);
            writeCommonAbstractObject(value);
            writeStates(value.getStates());
            writeHref(value.getHref());
            writeStandardExtensionLevel(value.extensions(), Names.ITEM_ICON);
            writer.writeEndElement();
        }
    }

    private void writeStates(List<ItemIconState> value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_STATE);
            int i = 0;
            int size = value.size();
            for (ItemIconState itemIconState : value) {
                i++;
                if (i == size) {
                    writer.writeCharacters(itemIconState.getItemIconState());
                } else {
                    writer.writeCharacters(itemIconState.getItemIconState() + " ");
                }
            }
            writer.writeEndElement();
        }
    }

    /**
     * This method writes an icon element typed as BasicLink.
     */
    private void writeIcon(BasicLink value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_ICON);
            writeIdAttributes(value.getIdAttributes());
            writeSimpleExtensionsScheduler(Names.OBJECT, value.extensions().simples(Names.OBJECT));
            writeHref(value.getHref());
            writeStandardExtensionLevel(value.extensions(), Names.BASIC_LINK);
            writer.writeEndElement();
        }
    }

    /**
     * This method writes an icon element typed as Link.
     */
    private void writeIcon(Icon value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_ICON);
        writeLink_structure(value);
        writer.writeEndElement();
    }

    private void writeLink(Link value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_LINK);
        writeLink_structure(value);
        writer.writeEndElement();
    }

    @Deprecated
    private void writeUrl(Url value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_URL);
        writeLink_structure(value);
        writer.writeEndElement();
    }

    /**
     * This method writes the Link structure used by different elements.
     */
    private void writeLink_structure(Link value) throws XMLStreamException, KmlException {
        writeCommonAbstractObject(value);
        writeHref(value.getHref());
        writeStandardExtensionLevel(value.extensions(), Names.BASIC_LINK);
        writeRefreshMode    (value.getRefreshMode());
        writeRefreshInterval(value.getRefreshInterval());
        writeViewRefreshMode(value.getViewRefreshMode());
        writeViewRefreshTime(value.getViewRefreshTime());
        writeViewBoundScale (value.getViewBoundScale());
        writeViewFormat     (value.getViewFormat());
        writeHttpQuery      (value.getHttpQuery());
        writeStandardExtensionLevel(value.extensions(), Names.LINK);
    }

    /**
     * This method writes the common fields for instances of AbstractColorStyle.
     *
     * @param value The AbstractColorStyle to write.
     */
    private void writeCommonColorStyle(AbstractColorStyle value) throws XMLStreamException, KmlException {
        writeCommonSubStyle(value);
        writeColor(value.getColor());
        writeColorMode(value.getColorMode());
        writeStandardExtensionLevel(value.extensions(), Names.COLOR_STYLE);
    }

    /**
     * This method writes the common fields for instances of AbstractSubStyle.
     *
     * @param value The AbstractSubStyle to write.
     */
    private void writeCommonSubStyle(AbstractSubStyle value) throws XMLStreamException, KmlException {
        writeCommonAbstractObject(value);
        writeStandardExtensionLevel(value.extensions(), Names.SUB_STYLE);
    }

    private void writePlacemark(Feature value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_PLACEMARK);
        writeCommonAbstractFeature(value);
        writeGeometry((AbstractGeometry) value.getPropertyValue(KmlConstants.TAG_GEOMETRY));
        writeStandardExtensionLevel((Extensions) value.getPropertyValue(KmlConstants.TAG_EXTENSIONS), Names.PLACEMARK);
        writer.writeEndElement();
    }

    /**
     *
     * @param value The AbstractContainer to write
     */
    private void writeAbstractContainer(Feature value) throws XMLStreamException, KmlException {
        if (value.getType().equals(KmlModelConstants.TYPE_FOLDER)) {
            writeFolder(value);
        } else if (value.getType().equals(KmlModelConstants.TYPE_DOCUMENT)) {
            writeDocument(value);
        }
    }

    /**
     *
     * @param value The AbstractOverlay to write.
     */
    private void writeAbstractOverlay(Feature value) throws XMLStreamException, KmlException {
        if (value.getType().equals(KmlModelConstants.TYPE_GROUND_OVERLAY)) {
            writeGroundOverlay(value);
        } else if (value.getType().equals(KmlModelConstants.TYPE_SCREEN_OVERLAY)) {
            writeScreenOverlay(value);
        } else if (value.getType().equals(KmlModelConstants.TYPE_PHOTO_OVERLAY)) {
            writePhotoOverlay(value);
        }
    }

    private void writePhotoOverlay(Feature value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_PHOTO_OVERLAY);
            writeCommonOverlay(value);
            Double rotation = (Double) value.getPropertyValue(KmlConstants.TAG_ROTATION);
            if (rotation != null) {
                writeRotation(rotation);
            }
            writeViewVolume((ViewVolume) value.getPropertyValue(KmlConstants.TAG_VIEW_VOLUME));
            writeImagePyramid((ImagePyramid) value.getPropertyValue(KmlConstants.TAG_IMAGE_PYRAMID));
            writePoint((Point) value.getPropertyValue(KmlConstants.TAG_POINT));
            writeShape((Shape) value.getPropertyValue(KmlConstants.TAG_SHAPE));
            writeStandardExtensionLevel((Extensions) value.getPropertyValue(KmlConstants.TAG_EXTENSIONS), Names.PHOTO_OVERLAY);
            writer.writeEndElement();
        }
    }

    private void writeImagePyramid(ImagePyramid value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_IMAGE_PYRAMID);
            writeCommonAbstractObject(value);
            writeTitleSize (value.getTitleSize());
            writeMaxWidth  (value.getMaxWidth());
            writeMaxHeight (value.getMaxHeight());
            writeGridOrigin(value.getGridOrigin());
            writeStandardExtensionLevel(value.extensions(), Names.IMAGE_PYRAMID);
            writer.writeEndElement();
        }
    }

    private void writeViewVolume(ViewVolume value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_VIEW_VOLUME);
            writeCommonAbstractObject(value);
            writeLeftFov  (value.getLeftFov());
            writeRightFov (value.getRightFov());
            writeBottomFov(value.getBottomFov());
            writeTopFov   (value.getTopFov());
            writeNear     (value.getNear());
            writeStandardExtensionLevel(value.extensions(), Names.VIEW_VOLUME);
            writer.writeEndElement();
        }
    }

    private void writeScreenOverlay(Feature value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_SCREEN_OVERLAY);
            writeCommonOverlay(value);
            writeOverlayXY ((Vec2) value.getPropertyValue(KmlConstants.TAG_OVERLAY_XY));
            writeScreenXY  ((Vec2) value.getPropertyValue(KmlConstants.TAG_SCREEN_XY));
            writeRotationXY((Vec2) value.getPropertyValue(KmlConstants.TAG_ROTATION_XY));
            writeSize      ((Vec2) value.getPropertyValue(KmlConstants.TAG_SIZE));
            Double rotation = (Double) value.getPropertyValue(KmlConstants.TAG_ROTATION);
            if (rotation != null) {
                writeRotation(rotation);
            }
            writeStandardExtensionLevel((Extensions) value.getPropertyValue(KmlConstants.TAG_EXTENSIONS), Names.SCREEN_OVERLAY);
            writer.writeEndElement();
        }
    }

    private void writeGroundOverlay(Feature value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_GROUND_OVERLAY);
            writeCommonOverlay(value);
            Double altitude = (Double) value.getPropertyValue(KmlConstants.TAG_ALTITUDE);
            if (altitude != null) {
                writeAltitude(altitude);
            }
            writeAltitudeMode((AltitudeMode) value.getPropertyValue(KmlConstants.TAG_ALTITUDE_MODE));
            writeLatLonBox((LatLonBox) value.getPropertyValue(KmlConstants.TAG_LAT_LON_BOX));
            writeStandardExtensionLevel((Extensions) value.getPropertyValue(KmlConstants.TAG_EXTENSIONS), Names.GROUND_OVERLAY);
            writer.writeEndElement();
        }
    }

    private void writeLatLonBox(LatLonBox value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_LAT_LON_BOX);
            writeCommonLatLonBox(value);
            writeRotation(value.getRotation());
            writeStandardExtensionLevel(value.extensions(), Names.LAT_LON_BOX);
            writer.writeEndElement();
        }
    }

    /**
     * This method writes tha common fields for AbstractLatLonBox instances.
     */
    private void writeCommonLatLonBox(AbstractLatLonBox value) throws XMLStreamException, KmlException {
        writeCommonAbstractObject(value);
        writeNorth(value.getNorth());
        writeSouth(value.getSouth());
        writeEast (value.getEast());
        writeWest (value.getWest());
        writeStandardExtensionLevel(value.extensions(), Names.ABSTRACT_LAT_LON_BOX);
    }

    private void writeCommonOverlay(Feature value) throws XMLStreamException, KmlException {
        writeCommonAbstractFeature(value);
        writeColor((Color) value.getPropertyValue(KmlConstants.TAG_COLOR));
        writeDrawOrder((Integer) value.getPropertyValue(KmlConstants.TAG_DRAW_ORDER));
        Icon icon = (Icon) value.getPropertyValue(KmlConstants.TAG_ICON);
        if (icon != null) {
            writeIcon(icon);
        }
        writeStandardExtensionLevel((Extensions) value.getPropertyValue(KmlConstants.TAG_EXTENSIONS), Names.OVERLAY);
    }

    /**
     * This method writes tha common fields for AbstractContainer instances.
     *
     * @param value The AbstractContainer to write.
     * @throws XMLStreamException
     */
    private void writeCommonContainer(Feature value) throws XMLStreamException, KmlException {
        writeCommonAbstractFeature(value);
        writeStandardExtensionLevel((Extensions) value.getPropertyValue(KmlConstants.TAG_EXTENSIONS), Names.CONTAINER);
    }

    private void writeFolder(Feature value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_FOLDER);
            writeCommonContainer(value);
            Iterator<?> i = ((Iterable<?>) value.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
            while (i.hasNext()) {
                writeAbstractFeature((Feature) i.next());
            }
            writeStandardExtensionLevel((Extensions) value.getPropertyValue(KmlConstants.TAG_EXTENSIONS), Names.FOLDER);
            writer.writeEndElement();
        }
    }

    private void writeDocument(Feature value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_DOCUMENT);
            writeCommonContainer(value);
            Iterator<?> i = ((Iterable<?>) value.getPropertyValue(KmlConstants.TAG_SCHEMA)).iterator();
            while (i.hasNext()) {
                checkVersion(URI_KML_2_2);
                writeSchema((Schema) i.next());
            }
            i = ((Iterable<?>) value.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
            while (i.hasNext()) {
                writeAbstractFeature((Feature) (i.next()));
            }
            writeStandardExtensionLevel((Extensions) value.getPropertyValue(KmlConstants.TAG_EXTENSIONS), Names.DOCUMENT);
            writer.writeEndElement();
        }
    }

    private void writeSchema(Schema value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_SCHEMA);
            if (value.getName() != null) {
                writer.writeAttribute(ATT_NAME, value.getName());
            }
            if (value.getId() != null) {
                writer.writeAttribute(ATT_ID, value.getId());
            }
            for (SimpleField sf : value.getSimpleFields()) {
                writeSimpleField(sf);
            }
            writer.writeEndElement();
        }
    }

    private void writeSimpleField(SimpleField value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_SIMPLE_FIELD);
            if (value.getType() != null) {
                writer.writeAttribute(ATT_TYPE, value.getType());
            }
            if (value.getName() != null) {
                writer.writeAttribute(ATT_NAME, value.getName());
            }
            writeDisplayName(value.getDisplayName());
            writer.writeEndElement();
        }
    }

    private void writeGeometry(AbstractGeometry value) throws XMLStreamException, KmlException {
        if (value != null) {
            if (value instanceof MultiGeometry) {
                writeMultiGeometry((MultiGeometry) value);
            } else if (value instanceof LineString) {
                writeLineString((LineString) value);
            } else if (value instanceof Polygon) {
                writePolygon((Polygon) value);
            } else if (value instanceof Point) {
                writePoint((Point) value);
            } else if (value instanceof LinearRing) {
                writeLinearRing((LinearRing) value);
            } else if (value instanceof Model) {
                writeModel((Model) value);
            } else {
                for (StaxStreamWriter candidate : extensionWriters) {
                    if (((KmlExtensionWriter) candidate).canHandleComplex(URI_KML, null, value)) {
                        ((KmlExtensionWriter) candidate).writeComplexExtensionElement(URI_KML, null, value);
                    }
                }
            }
        }
    }

    /**
     * This method writes the common fields for instances of AbstractGeometry.
     */
    public void writeCommonAbstractGeometry(AbstractGeometry value) throws XMLStreamException, KmlException {
        writeCommonAbstractObject(value);
        writeStandardExtensionLevel(value.extensions(), Names.GEOMETRY);
    }

    public void writeModel(Model value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_MODEL);
        writeCommonAbstractGeometry(value);
        writeAltitudeMode(value.getAltitudeMode());
        if (value.getLocation() != null) {
            writeLocation(value.getLocation());
        }
        if (value.getOrientation() != null) {
            writeOrientation(value.getOrientation());
        }
        if (value.getScale() != null) {
            writeScale(value.getScale());
        }
        if (value.getLink() != null) {
            writeLink(value.getLink());
        }
        if (value.getRessourceMap() != null) {
            checkVersion(URI_KML_2_2);
            writeResourceMap(value.getRessourceMap());
        }
        writeStandardExtensionLevel(value.extensions(), Names.MODEL);
        writer.writeEndElement();
    }

    private void writeLocation(Location value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_LOCATION);
        writeCommonAbstractObject(value);
        writeLongitude(value.getLongitude());
        writeLatitude (value.getLatitude());
        writeAltitude (value.getAltitude());
        writeStandardExtensionLevel(value.extensions(), Names.LOCATION);
        writer.writeEndElement();
    }

    private void writeOrientation(Orientation value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_ORIENTATION);
        writeCommonAbstractObject(value);
        writeHeading(value.getHeading());
        writeTilt   (value.getTilt());
        writeRoll   (value.getRoll());
        writeStandardExtensionLevel(value.extensions(), Names.ORIENTATION);
        writer.writeEndElement();
    }

    private void writeScale(Scale value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_SCALE_BIG);
        writeX(value.getX());
        writeY(value.getY());
        writeZ(value.getZ());
        writeStandardExtensionLevel(value.extensions(), Names.SCALE);
        writer.writeEndElement();
    }

    private void writeResourceMap(ResourceMap value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_RESOURCE_MAP);
        writeCommonAbstractObject(value);
        for (Alias alias : value.getAliases()) {
            writeAlias(alias);
        }
        writeStandardExtensionLevel(value.extensions(), Names.RESOURCE_MAP);
        writer.writeEndElement();
    }

    private void writeAlias(Alias value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_ALIAS);
        writeCommonAbstractObject(value);
        writeTargetHref(value.getTargetHref());
        writeSourceHref(value.getSourceHref());
        writeStandardExtensionLevel(value.extensions(), Names.ALIAS);
        writer.writeEndElement();
    }

    private void writePolygon(Polygon value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_POLYGON);
        writeCommonAbstractGeometry(value);
        writeExtrude     (value.getExtrude());
        writeTessellate  (value.getTessellate());
        writeAltitudeMode(value.getAltitudeMode());
        if (value.getOuterBoundary() != null) {
            writeOuterBoundary(value.getOuterBoundary());
        }
        for (Boundary innerBoundary : value.getInnerBoundaries()) {
            writeInnerBoundary(innerBoundary);
        }
        writeStandardExtensionLevel(value.extensions(), Names.POLYGON);
        writer.writeEndElement();
    }

    private void writeOuterBoundary(Boundary value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_OUTER_BOUNDARY_IS);
        writeBoundary(value);
        writer.writeEndElement();
    }

    private void writeInnerBoundary(Boundary value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_INNER_BOUNDARY_IS);
        writeBoundary(value);
        writer.writeEndElement();
    }

    private void writeBoundary(Boundary value) throws XMLStreamException, KmlException {
        if (value.getLinearRing() != null) {
            writeLinearRing(value.getLinearRing());
        }
        writeStandardExtensionLevel(value.extensions(), Names.BOUNDARY);
    }

    private void writeLineString(LineString value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_LINE_STRING);
        writeCommonAbstractGeometry(value);
        writeExtrude     (value.getExtrude());
        writeTessellate  (value.getTessellate());
        writeAltitudeMode(value.getAltitudeMode());
        writeCoordinates (value.getCoordinateSequence());
        writeStandardExtensionLevel(value.extensions(), Names.LINE_STRING);
        writer.writeEndElement();
    }

    private void writeLinearRing(LinearRing value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_LINEAR_RING);
        writeCommonAbstractGeometry(value);
        writeExtrude     (value.getExtrude());
        writeTessellate  (value.getTessellate());
        writeAltitudeMode(value.getAltitudeMode());
        writeCoordinates (value.getCoordinateSequence());
        writeStandardExtensionLevel(value.extensions(), Names.LINEAR_RING);
        writer.writeEndElement();
    }

    private void writeMultiGeometry(MultiGeometry value) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_KML, TAG_MULTI_GEOMETRY);
        writeCommonAbstractGeometry(value);
        for (AbstractGeometry geometry : value.getGeometries()) {
            writeGeometry(geometry);
        }
        writeStandardExtensionLevel(value.extensions(), Names.MULTI_GEOMETRY);
        writer.writeEndElement();
    }

    private void writePoint(Point value) throws XMLStreamException, KmlException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_POINT);
            writeCommonAbstractGeometry(value);
            writeExtrude     (value.getExtrude());
            writeAltitudeMode(value.getAltitudeMode());
            writeCoordinates (value.getCoordinateSequence());
            writeStandardExtensionLevel(value.extensions(), Names.POINT);
            writer.writeEndElement();
        }
    }

    public void writeCoordinates(CoordinateSequence value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_COORDINATES);
            writer.writeCharacters(KmlUtilities.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeExtrude(boolean value) throws XMLStreamException {
        if (DEF_EXTRUDE != value) {
            writer.writeStartElement(URI_KML, TAG_EXTRUDE);
            if (value) {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    private void writeVisibility(Boolean value) throws XMLStreamException {
        if (value != null && DEF_VISIBILITY != value) {
            writer.writeStartElement(URI_KML, TAG_VISIBILITY);
            writer.writeCharacters(value ? SimpleTypeContainer.BOOLEAN_TRUE
                                         : SimpleTypeContainer.BOOLEAN_FALSE);
            writer.writeEndElement();
        }
    }

    private void writeOpen(Boolean value) throws XMLStreamException {
        if (value != null && DEF_OPEN != value) {
            writer.writeStartElement(URI_KML, TAG_OPEN);
            writer.writeCharacters(value ? SimpleTypeContainer.BOOLEAN_TRUE
                                         : SimpleTypeContainer.BOOLEAN_FALSE);
            writer.writeEndElement();
        }
    }

    private void writeFill(boolean value) throws XMLStreamException {
        if (DEF_FILL != value) {
            writer.writeStartElement(URI_KML, TAG_FILL);
            if (value) {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    private void writeOutline(boolean value) throws XMLStreamException {
        if (DEF_OUTLINE != value) {
            writer.writeStartElement(URI_KML, TAG_OUTLINE);
            if (value) {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    private void writeTessellate(boolean value) throws XMLStreamException {
        if (DEF_TESSELLATE != value) {
            writer.writeStartElement(URI_KML, TAG_TESSELLATE);
            if (value) {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    private void writeRefreshVisibility(Boolean value) throws XMLStreamException {
        if (value != null && DEF_REFRESH_VISIBILITY != value) {
            writer.writeStartElement(URI_KML, TAG_REFRESH_VISIBILITY);
            if (value) {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    private void writeFlyToView(Boolean value) throws XMLStreamException {
        if (value != null && DEF_FLY_TO_VIEW != value) {
            writer.writeStartElement(URI_KML, TAG_FLY_TO_VIEW);
            if (value) {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    private void writeAddress(String value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_ADDRESS);
            writer.writeCharacters(value);
            writer.writeEndElement();
        }
    }

    private void writeSnippet(Object value) throws XMLStreamException {
        if (value != null) {
            if (value instanceof String || value instanceof Cdata) {
                writer.writeStartElement(URI_KML, TAG_SNIPPET);
                writeCharacterContent(value);
            } else if (value instanceof Snippet) {
                Snippet s = (Snippet) value;
                writer.writeStartElement(URI_KML, TAG_SNIPPET_BIG);
                if (DEF_MAX_SNIPPET_LINES_ATT != s.getMaxLines()) {
                    writer.writeAttribute(ATT_MAX_LINES, String.valueOf(s.getMaxLines()));
                }
                writeCharacterContent(s.getContent());
            }
            writer.writeEndElement();
        }
    }

    private void writePhoneNumber(String value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_PHONE_NUMBER);
            writer.writeCharacters(value);
            writer.writeEndElement();
        }
    }

    private void writeName(String value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_NAME);
            writer.writeCharacters(value);
            writer.writeEndElement();
        }
    }

    private void writeDescription(Object value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_DESCRIPTION);
            writeCharacterContent(value);
            writer.writeEndElement();
        }
    }

    public void writeHref(String value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_HREF);
            writer.writeCharacters(value);
            writer.writeEndElement();
        }
    }

    private void writeText(Object value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_TEXT);
            writeCharacterContent(value);
            writer.writeEndElement();
        }
    }

    private void writeStyleUrl(URI value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_STYLE_URL);
            writer.writeCharacters(value.toString());
            writer.writeEndElement();
        }
    }

    private void writeViewFormat(String value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_VIEW_FORMAT);
            writer.writeCharacters(value);
            writer.writeEndElement();
        }
    }

    private void writeHttpQuery(String value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_HTTP_QUERY);
            writer.writeCharacters(value);
            writer.writeEndElement();
        }
    }

    private void writeTargetHref(URI value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_TARGET_HREF);
            writer.writeCharacters(value.toString());
            writer.writeEndElement();
        }
    }

    private void writeSourceHref(URI value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_SOURCE_HREF);
            writer.writeCharacters(value.toString());
            writer.writeEndElement();
        }
    }

    public void writeBegin(Calendar value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_BEGIN);
            writeCalendar(value);
            writer.writeEndElement();
        }
    }

    public void writeEnd(Calendar value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_END);
            writeCalendar(value);
            writer.writeEndElement();
        }
    }

    public void writeWhen(Calendar value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_WHEN);
            writeCalendar(value);
            writer.writeEndElement();
        }
    }

    private void writeCalendar(Calendar value) throws XMLStreamException {
        writer.writeCharacters(KmlUtilities.getXMLFormatedCalendar(value, true));
    }

    private void writeDisplayName(Object value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_DISPLAY_NAME);
            writeCharacterContent(value);
            writer.writeEndElement();
        }
    }

    private void writeValue(String value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_VALUE);
            writer.writeCharacters(value);
            writer.writeEndElement();
        }
    }

    private void writeCookie(String value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_COOKIE);
            writer.writeCharacters(value);
            writer.writeEndElement();
        }
    }

    private void writeMessage(String value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_MESSAGE);
            writer.writeCharacters(value);
            writer.writeEndElement();
        }
    }

    private void writeLinkName(String value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_LINK_NAME);
            writer.writeCharacters(value);
            writer.writeEndElement();
        }
    }

    private void writeLinkDescription(Object value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_LINK_DESCRIPTION);
            writeCharacterContent(value);
            writer.writeEndElement();
        }
    }

    private void writeExpires(Calendar value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_EXPIRES);
            writeCalendar(value);
            writer.writeEndElement();
        }
    }

    private void writeLinkSnippet(Snippet value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_LINK_SNIPPET);
            if (DEF_MAX_SNIPPET_LINES_ATT != value.getMaxLines()) {
                writer.writeAttribute(ATT_MAX_LINES, String.valueOf(value.getMaxLines()));
            }
            writeCharacterContent(value.getContent());
            writer.writeEndElement();
        }
    }

    private void writeColor(Color value) throws XMLStreamException {
        if (value != null && DEF_COLOR != value) {
            writer.writeStartElement(URI_KML, TAG_COLOR);
            writer.writeCharacters(KmlUtilities.toKmlColor(value));
            writer.writeEndElement();
        }
    }

    private void writeBgColor(Color value) throws XMLStreamException {
        if (value != null && DEF_BG_COLOR != value) {
            writer.writeStartElement(URI_KML, TAG_BG_COLOR);
            writer.writeCharacters(KmlUtilities.toKmlColor(value));
            writer.writeEndElement();
        }
    }

    private void writeTextColor(Color value) throws XMLStreamException {
        if (value != null && DEF_TEXT_COLOR != value) {
            writer.writeStartElement(URI_KML, TAG_TEXT_COLOR);
            writer.writeCharacters(KmlUtilities.toKmlColor(value));
            writer.writeEndElement();
        }
    }

    private void writeColorMode(ColorMode value) throws XMLStreamException {
        if (value != null && DEF_COLOR_MODE != value) {
            writer.writeStartElement(URI_KML, TAG_COLOR_MODE);
            writer.writeCharacters(value.getColorMode());
            writer.writeEndElement();
        }
    }

    public void writeAltitudeMode(AltitudeMode value) throws XMLStreamException, KmlException {
        if (value != null && DEF_ALTITUDE_MODE != value) {
            if (value instanceof org.geotoolkit.data.kml.model.EnumAltitudeMode) {
                writer.writeStartElement(URI_KML, TAG_ALTITUDE_MODE);
                writer.writeCharacters(value.getAltitudeMode());
                writer.writeEndElement();
            } else {
                for (StaxStreamWriter candidate : extensionWriters) {
                    if (((KmlExtensionWriter) candidate).canHandleComplex(URI_KML, null, value)) {
                        ((KmlExtensionWriter) candidate).writeComplexExtensionElement(URI_KML, null, value);
                    }
                }
            }
        }
    }

    private void writeDisplayMode(DisplayMode value) throws XMLStreamException {
        if (value != null && DEF_DISPLAY_MODE != value) {
            writer.writeStartElement(URI_KML, TAG_ALTITUDE_MODE);
            writer.writeCharacters(value.getDisplayMode());
            writer.writeEndElement();
        }
    }

    private void writeKey(StyleState value) throws XMLStreamException {
        if (value != null && DEF_STYLE_STATE != value) {
            writer.writeStartElement(URI_KML, TAG_KEY);
            writer.writeCharacters(value.getStyleState());
            writer.writeEndElement();
        }
    }

    private void writeRefreshMode(RefreshMode value) throws XMLStreamException {
        if (value != null && DEF_REFRESH_MODE != value) {
            writer.writeStartElement(URI_KML, TAG_REFRESH_MODE);
            writer.writeCharacters(value.getRefreshMode());
            writer.writeEndElement();
        }
    }

    private void writeViewRefreshMode(ViewRefreshMode value) throws XMLStreamException {
        if (value != null && DEF_VIEW_REFRESH_MODE != value) {
            writer.writeStartElement(URI_KML, TAG_VIEW_REFRESH_MODE);
            writer.writeCharacters(value.getViewRefreshMode());
            writer.writeEndElement();
        }
    }

    private void writeListItem(ListItem value) throws XMLStreamException {
        if (value != null && DEF_LIST_ITEM != value) {
            writer.writeStartElement(URI_KML, TAG_LIST_ITEM);
            writer.writeCharacters(value.getItem());
            writer.writeEndElement();
        }
    }

    private void writeShape(Shape value) throws XMLStreamException {
        if (value != null && DEF_SHAPE != value) {
            writer.writeStartElement(URI_KML, TAG_SHAPE);
            writer.writeCharacters(value.getShape());
            writer.writeEndElement();
        }
    }

    private void writeGridOrigin(GridOrigin value) throws XMLStreamException {
        if (value != null && DEF_GRID_ORIGIN != value) {
            writer.writeStartElement(URI_KML, TAG_GRID_ORIGIN);
            writer.writeCharacters(value.getGridOrigin());
            writer.writeEndElement();
        }
    }

    private void writeScale(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_SCALE != value) {
            writer.writeStartElement(URI_KML, TAG_SCALE);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeWidth(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_WIDTH != value) {
            writer.writeStartElement(URI_KML, TAG_WIDTH);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeAltitude(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_ALTITUDE != value) {
            writer.writeStartElement(URI_KML, TAG_ALTITUDE);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeRange(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_RANGE != value) {
            writer.writeStartElement(URI_KML, TAG_RANGE);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeDrawOrder(Integer value) throws XMLStreamException {
        if (value != null && DEF_DRAW_ORDER != value) {
            writer.writeStartElement(URI_KML, TAG_DRAW_ORDER);
            writer.writeCharacters(Integer.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeX(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_X != value) {
            writer.writeStartElement(URI_KML, TAG_X);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeY(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_Y != value) {
            writer.writeStartElement(URI_KML, TAG_Y);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeZ(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_Z != value) {
            writer.writeStartElement(URI_KML, TAG_Z);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeMinAltitude(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_MIN_ALTITUDE != value) {
            writer.writeStartElement(URI_KML, TAG_MIN_ALTITUDE);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeMaxAltitude(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_MAX_ALTITUDE != value) {
            writer.writeStartElement(URI_KML, TAG_MAX_ALTITUDE);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeMinLodPixels(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_MIN_LOD_PIXELS != value) {
            writer.writeStartElement(URI_KML, TAG_MIN_LOD_PIXELS);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeMaxLodPixels(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_MAX_LOD_PIXELS != value) {
            writer.writeStartElement(URI_KML, TAG_MAX_LOD_PIXELS);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeMinFadeExtent(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_MIN_FADE_EXTENT != value) {
            writer.writeStartElement(URI_KML, TAG_MIN_FADE_EXTENT);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeMaxFadeExtent(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_MAX_FADE_EXTENT != value) {
            writer.writeStartElement(URI_KML, TAG_MAX_FADE_EXTENT);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeRefreshInterval(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_REFRESH_INTERVAL != value) {
            writer.writeStartElement(URI_KML, TAG_REFRESH_INTERVAL);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeViewRefreshTime(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_VIEW_REFRESH_TIME != value) {
            writer.writeStartElement(URI_KML, TAG_VIEW_REFRESH_TIME);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeViewBoundScale(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_VIEW_BOUND_SCALE != value) {
            writer.writeStartElement(URI_KML, TAG_VIEW_BOUND_SCALE);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeNear(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_NEAR != value) {
            writer.writeStartElement(URI_KML, TAG_NEAR);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeMinRefreshPeriod(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_MIN_REFRESH_PERIOD != value) {
            writer.writeStartElement(URI_KML, TAG_MIN_REFRESH_PERIOD);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeMaxSessionLength(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_MAX_SESSION_LENGTH != value) {
            writer.writeStartElement(URI_KML, TAG_MAX_SESSION_LENGTH);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeTitleSize(int value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_TITLE_SIZE != value) {
            writer.writeStartElement(URI_KML, TAG_TITLE_SIZE);
            writer.writeCharacters(Integer.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeMaxWidth(int value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_MAX_WIDTH != value) {
            writer.writeStartElement(URI_KML, TAG_MAX_WIDTH);
            writer.writeCharacters(Integer.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeMaxHeight(int value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_MAX_HEIGHT != value) {
            writer.writeStartElement(URI_KML, TAG_MAX_HEIGHT);
            writer.writeCharacters(Integer.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeMaxSnippetLines(int value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_MAX_SNIPPET_LINES != value) {
            writer.writeStartElement(URI_KML, TAG_MAX_SNIPPET_LINES);
            writer.writeCharacters(Integer.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeHeading(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_HEADING != value) {
            writer.writeStartElement(URI_KML, TAG_HEADING);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeBottomFov(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_BOTTOM_FOV != value) {
            writer.writeStartElement(URI_KML, TAG_BOTTOM_FOV);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeTopFov(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_TOP_FOV != value) {
            writer.writeStartElement(URI_KML, TAG_TOP_FOV);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeLeftFov(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_LEFT_FOV != value) {
            writer.writeStartElement(URI_KML, TAG_LEFT_FOV);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeRightFov(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_RIGHT_FOV != value) {
            writer.writeStartElement(URI_KML, TAG_RIGHT_FOV);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeLongitude(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_LONGITUDE != value) {
            writer.writeStartElement(URI_KML, TAG_LONGITUDE);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeLatitude(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_LATITUDE != value) {
            writer.writeStartElement(URI_KML, TAG_LATITUDE);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeTilt(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_TILT != value) {
            writer.writeStartElement(URI_KML, TAG_TILT);
            writer.writeCharacters(Double.toString(KmlUtilities.checkAnglePos180(value)));
            writer.writeEndElement();
        }
    }

    private void writeRotation(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_ROTATION != value) {
            writer.writeStartElement(URI_KML, TAG_ROTATION);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeNorth(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_NORTH != value) {
            writer.writeStartElement(URI_KML, TAG_NORTH);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeSouth(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_SOUTH != value) {
            writer.writeStartElement(URI_KML, TAG_SOUTH);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeEast(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_EAST != value) {
            writer.writeStartElement(URI_KML, TAG_EAST);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeWest(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_WEST != value) {
            writer.writeStartElement(URI_KML, TAG_WEST);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeRoll(double value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value) && DEF_ROLL != value) {
            writer.writeStartElement(URI_KML, TAG_ROLL);
            writer.writeCharacters(Double.toString(value));
            writer.writeEndElement();
        }
    }

    private void writeVec2(Vec2 value) throws XMLStreamException {
        if (KmlUtilities.isFiniteNumber(value.getX()) && DEF_VEC2_X != value.getX()) {
            writer.writeAttribute(ATT_X, Double.toString(value.getX()));
        }
        if (KmlUtilities.isFiniteNumber(value.getY()) && DEF_VEC2_Y != value.getY()) {
            writer.writeAttribute(ATT_Y, Double.toString(value.getY()));
        }
        if (value.getXUnits() != null && !DEF_VEC2_XUNIT.equals(value.getXUnits())) {
            writer.writeAttribute(ATT_XUNITS, value.getXUnits().getUnit());
        }
        if (value.getYUnits() != null && !DEF_VEC2_YUNIT.equals(value.getYUnits())) {
            writer.writeAttribute(ATT_YUNITS, value.getYUnits().getUnit());
        }
    }

    private void writeHotSpot(Vec2 value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_HOT_SPOT);
            writeVec2(value);
            writer.writeEndElement();
        }
    }

    private void writeOverlayXY(Vec2 value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_OVERLAY_XY);
            writeVec2(value);
            writer.writeEndElement();
        }
    }

    private void writeScreenXY(Vec2 value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_SCREEN_XY);
            writeVec2(value);
            writer.writeEndElement();
        }
    }

    private void writeRotationXY(Vec2 value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_ROTATION_XY);
            writeVec2(value);
            writer.writeEndElement();
        }
    }

    private void writeSize(Vec2 value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(URI_KML, TAG_SIZE);
            writeVec2(value);
            writer.writeEndElement();
        }
    }

    /**
     * Writes character content as CDATA if input String contains "&lt;" character.
     * Following KML elements mays contains CDATA:
     * <ul>
     * <li>snippet,</p>
     * <li>description,</li>
     * <li>text,</li>
     * <li>linkDescription,</li>
     * <li>linkSnippet.</li>
     * </ul>
     */
    private void writeCharacterContent(Object value) throws XMLStreamException {
        if (value instanceof Cdata) {
            writer.writeCData(value.toString());
        } else if (value instanceof String) {
            writer.writeCharacters((String) value);
        } else {
            throw new IllegalArgumentException("Only String or CDATA argument.");
        }
    }

    /*
     * ------------------------ UTILITARY METHODS ------------------------------
     */
    private void checkVersion(String version) throws KmlException {
        if (URI_KML.equals(version)) {
            return;
        }
        throw new KmlException("Kml writer error : Element not allowed by " + URI_KML + " namespace.");
    }

    private boolean checkVersionSimple(String version) {
        return URI_KML.equals(version);
    }

    /*
     * ------------------- WRITING EXTENSIONS METHODS --------------------------
     */
    /**
     * Writes complex extensions using associated writer.
     */
    private void writeComplexExtensionsScheduler(Extensions.Names ext, List<Object> objectExtensions)
            throws KmlException, XMLStreamException
    {
        for (Object object : objectExtensions) {
            for (StaxStreamWriter candidate : extensionWriters) {
                if (((KmlExtensionWriter) candidate).canHandleComplex(URI_KML, ext, object)) {
                    ((KmlExtensionWriter) candidate).writeComplexExtensionElement(URI_KML, ext, object);
                }
            }
        }
    }

    /**
     * Writes simple extensions using associated writer.
     */
    private void writeSimpleExtensionsScheduler(Extensions.Names ext, List<SimpleTypeContainer> simpleExtensions)
            throws KmlException, XMLStreamException
    {
        for (SimpleTypeContainer object : simpleExtensions) {
            for (StaxStreamWriter candidate : extensionWriters) {
                if (((KmlExtensionWriter) candidate).canHandleSimple(URI_KML, ext, object.getTagName())) {
                    ((KmlExtensionWriter) candidate).writeSimpleExtensionElement(URI_KML, ext, object);
                }
            }
        }
    }

    /**
     * Writes extensions at given level.
     */
    private void writeStandardExtensionLevel(Extensions extensions, Names level)
            throws KmlException, XMLStreamException
    {
        writeComplexExtensionsScheduler(level, extensions.complexes(level));
        writeSimpleExtensionsScheduler(level, extensions.simples(level));
    }

    /**
     * Writes XML data using associated writer.
     */
    private void writeDataScheduler(List<Object> data) throws KmlException, XMLStreamException {
        if (data != null) {
            for (Object object : data) {
                for (StaxStreamWriter candidate : dataWriters) {
                    if (((KmlExtensionWriter) candidate).canHandleComplex(URI_KML, null, object)) {
                        ((KmlExtensionWriter) candidate).writeComplexExtensionElement(URI_KML, null, object);
                    } else if (((KmlExtensionWriter) candidate).canHandleSimple(URI_KML, null, ((SimpleTypeContainer) object).getTagName())) {
                        ((KmlExtensionWriter) candidate).writeSimpleExtensionElement(URI_KML, null, (SimpleTypeContainer) object);
                    }
                }
            }
        }
    }
}

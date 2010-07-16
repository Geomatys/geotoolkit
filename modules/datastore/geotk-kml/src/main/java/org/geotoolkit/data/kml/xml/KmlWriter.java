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

import org.geotoolkit.data.xal.xml.XalWriter;
import org.geotoolkit.data.atom.xml.AtomWriter;
import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
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
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Create;
import org.geotoolkit.data.kml.model.Data;
import org.geotoolkit.data.kml.model.Delete;
import org.geotoolkit.data.kml.model.DisplayMode;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.Extensions.Names;
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
import org.geotoolkit.data.kml.model.Metadata;
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
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.model.Url;
import org.geotoolkit.data.kml.model.Vec2;
import org.geotoolkit.data.kml.model.ViewRefreshMode;
import org.geotoolkit.data.kml.model.ViewVolume;
import org.geotoolkit.data.xal.model.AddressDetails;
import org.geotoolkit.data.xal.model.XalException;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.geotoolkit.data.kml.xsd.Cdata;
import org.geotoolkit.xml.StaxStreamWriter;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 * <p>This class provides a method to read KML files, version 2.2.</p>
 *
 * @author Samuel Andrés
 */
public class KmlWriter extends StaxStreamWriter {

    private String URI_KML;
    private final XalWriter xalWriter = new XalWriter();
    private final AtomWriter atomWriter = new AtomWriter();

    public KmlWriter(){
        super();
    }

    /**
     * <p>Set output. This method doesn't indicate kml uri version whose detection
     * is automatic at kml root writing. In other cases, method with Kml version uri
     * argument is necessary.</p>
     *
     * @param output
     * @throws XMLStreamException
     * @throws IOException
     */
    @Override
    public void setOutput(Object output) throws XMLStreamException, IOException{
        super.setOutput(output);
        this.xalWriter.setOutput(writer);
        this.atomWriter.setOutput(writer);
        this.writer.setPrefix(PREFIX_XAL, URI_XAL);
        this.writer.setPrefix(PREFIX_ATOM, URI_ATOM);
    }

    /**
     * <p>Set input. This method is necessary if Kml elements are read out of Kml document
     * with kml root elements.</p>
     *
     * @param output
     * @param KmlVersionUri
     * @throws XMLStreamException
     * @throws IOException
     * @throws KmlException
     */
    public void setOutput(Object output, String KmlVersionUri)
            throws XMLStreamException, IOException, KmlException{
        this.setOutput(output);
        if (URI_KML_2_2.equals(KmlVersionUri) || URI_KML_2_1.equals(KmlVersionUri))
            this.URI_KML = KmlVersionUri;
        else
            throw new KmlException("Bad Kml version Uri. This reader supports 2.1 and 2.2 versions.");
    }

    /**
     * <p>This method writes a Kml 2.2 document into the file assigned to the KmlWriter.</p>
     *
     * @param kml The Kml object to write.
     */
    public void write(Kml kml) {
        try {

            // FACULTATIF : INDENTATION DE LA SORTIE
            //streamWriter = new IndentingXMLStreamWriter(streamWriter);
            writer.writeStartDocument("UTF-8", "1.0");
            URI_KML = kml.getVersion();
            writer.setDefaultNamespace(URI_KML);
            writer.writeStartElement(URI_KML,TAG_KML);
            //writer.writeDefaultNamespace(URI_KML);
            if(URI_KML.equals(URI_KML_2_2)){
                writer.writeNamespace(PREFIX_ATOM, URI_ATOM);
                writer.writeNamespace(PREFIX_XAL, URI_XAL);
            }
            /*streamWriter.writeNamespace(PREFIX_XSI, URI_XSI);
            streamWriter.writeAttribute(URI_XSI,
                    "schemaLocation",
                    URI_KML+" C:/Users/w7mainuser/Documents/OGC_SCHEMAS/sld/1.1.0/StyledLayerDescriptor.xsd");
            streamWriter.writeAttribute("version", "0");*/
            this.writeKml(kml);
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();

        } catch (KmlException ex) {
            Logger.getLogger(KmlWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(KmlWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param kml The Kml object to write.
     * @throws XMLStreamException
     */
    private void writeKml(Kml kml) throws XMLStreamException, KmlException{
        if (kml.getNetworkLinkControl() != null){
            this.writeNetworkLinkControl(kml.getNetworkLinkControl());
        }
        if (kml.getAbstractFeature() != null){
            this.writeAbstractFeature(kml.getAbstractFeature());
        }
        if (kml.extensions().simples(Names.KML) != null){
        }
        if (kml.extensions().complexes(Names.KML) != null){
        }
    }

    /**
     *
     * @param networkLinkControl
     * @throws XMLStreamException
     */
    private void writeNetworkLinkControl(NetworkLinkControl networkLinkControl) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_NETWORK_LINK_CONTROL);
        if (isFiniteNumber(networkLinkControl.getMinRefreshPeriod())){
            this.writeMinRefreshPeriod(networkLinkControl.getMinRefreshPeriod());
        }
        if (isFiniteNumber(networkLinkControl.getMaxSessionLength())
                && checkVersionSimple(URI_KML_2_2)){
            this.writeMaxSessionLength(networkLinkControl.getMaxSessionLength());
        }
        if (networkLinkControl.getCookie() != null){
            this.writeCookie(networkLinkControl.getCookie());
        }
        if (networkLinkControl.getMessage() != null){
            this.writeMessage(networkLinkControl.getMessage());
        }
        if (networkLinkControl.getLinkName() != null){
            this.writeLinkName(networkLinkControl.getLinkName());
        }
        if (networkLinkControl.getLinkDescription() != null){
            this.writeLinkDescription(networkLinkControl.getLinkDescription());
        }
        if (networkLinkControl.getLinkSnippet() != null){
            this.writeLinkSnippet(networkLinkControl.getLinkSnippet());
        }
        if (networkLinkControl.getExpires() != null){
            this.writeExpires(networkLinkControl.getExpires());
        }
        if (networkLinkControl.getUpdate() != null){
            this.writeUpdate(networkLinkControl.getUpdate());
        }
        if (networkLinkControl.getView() != null){
            this.writeAbstractView(networkLinkControl.getView());
        }
        if (networkLinkControl.extensions().simples(Names.NETWORK_LINK_CONTROL) != null){
        }
        if (networkLinkControl.extensions().complexes(Names.NETWORK_LINK_CONTROL) != null){
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param update
     * @throws XMLStreamException
     */
    private void writeUpdate(Update update) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_UPDATE);
        if(update.getTargetHref() != null){
            this.writeTargetHref(update.getTargetHref());
        }
        for (Object object : update.getUpdates()){
            if(object instanceof Create)
                this.writeCreate((Create) object);
            else if (object instanceof Delete)
                this.writeDelete((Delete) object);
            else if (object instanceof Change)
                this.writeChange((Change) object);
            else if (object instanceof AbstractFeature){
                checkVersion(URI_KML_2_1);
                this.writeReplace((AbstractFeature) object);
            } else {
                throw new KmlException(object.getClass().getCanonicalName()+ " instance is not allowed here");
            }
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param replace
     * @throws XMLStreamException
     * @deprecated
     */
    @Deprecated
    private void writeReplace(AbstractFeature replace) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_REPLACE);
        this.writeAbstractFeature(replace);
        writer.writeEndElement();
    }

    /**
     *
     * @param create
     * @throws XMLStreamException
     */
    private void writeCreate(Create create) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_CREATE);
        for (AbstractContainer container : create.getContainers()){
            this.writeAbstractContainer(container);
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param delete
     * @throws XMLStreamException
     */
    private void writeDelete(Delete delete) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_DELETE);
        for (AbstractFeature feature : delete.getFeatures()){
            this.writeAbstractFeature(feature);
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param change
     * @throws XMLStreamException
     */
    private void writeChange(Change change) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_CHANGE);
        for (AbstractObject object : change.getObjects()){
            this.writeAbstractObject(object);
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param object
     * @throws XMLStreamException
     */
    private void writeAbstractObject(AbstractObject object) throws XMLStreamException, KmlException{
        if (object instanceof Region){
            this.writeRegion((Region) object);
        } else if (object instanceof Lod){
           this.writeLod((Lod) object);
        } else if (object instanceof Link){
            this.writeLink((Link) object);
        } else if (object instanceof Icon){
            this.writeIcon((Icon) object);
        } else if (object instanceof Location){
            this.writeLocation((Location) object);
        } else if (object instanceof Orientation){
            this.writeOrientation((Orientation) object);
        } else if (object instanceof ResourceMap){
            this.writeResourceMap((ResourceMap) object);
        } else if (object instanceof SchemaData){
            this.writeSchemaData((SchemaData) object);
        } else if (object instanceof Scale){
           this.writeScale((Scale) object);
        } else if (object instanceof Alias){
            this.writeAlias((Alias) object);
        } else if (object instanceof ViewVolume){
            this.writeViewVolume((ViewVolume) object);
        } else if (object instanceof ImagePyramid){
            this.writeImagePyramid((ImagePyramid) object);
        } else if (object instanceof Pair){
            this.writePair((Pair) object);
        } else if (object instanceof ItemIcon){
            this.writeItemIcon((ItemIcon) object);
        } else if (object instanceof AbstractFeature){
            this.writeAbstractFeature((AbstractFeature) object);
        } else if (object instanceof AbstractGeometry){
            this.writeAbstractGeometry((AbstractGeometry) object);
        } else if (object instanceof AbstractStyleSelector){
            this.writeAbstractStyleSelector((AbstractStyleSelector) object);
        } else if (object instanceof AbstractSubStyle){
            this.writeAbstractSubStyle((AbstractSubStyle) object);
        } else if (object instanceof AbstractView){
            this.writeAbstractView((AbstractView) object);
        } else if (object instanceof AbstractTimePrimitive){
            this.writeAbstractTimePrimitive((AbstractTimePrimitive) object);
        } else if (object instanceof AbstractLatLonBox){
            this.writeAbstractLatLonBox((AbstractLatLonBox) object);
        }
    }

    private void writeAbstractLatLonBox(AbstractLatLonBox abstractLatLonBox) throws XMLStreamException{
        if(abstractLatLonBox instanceof LatLonAltBox){
            this.writeLatLonAltBox((LatLonAltBox) abstractLatLonBox);
        } else if (abstractLatLonBox instanceof LatLonBox){
            this.writeLatLonBox((LatLonBox) abstractLatLonBox);
        }
    }

    /**
     *
     * @param subStyle
     * @throws XMLStreamException
     */
    private void writeAbstractSubStyle(AbstractSubStyle subStyle) throws XMLStreamException{
        if (subStyle instanceof BalloonStyle){
            this.writeBalloonStyle((BalloonStyle) subStyle);
        } else if (subStyle instanceof ListStyle){
            this.writeListStyle((ListStyle) subStyle);
        } else if (subStyle instanceof AbstractColorStyle){
            this.writeAbstractColorStyle((AbstractColorStyle) subStyle);
        }
    }

    /**
     *
     * @param colorStyle
     * @throws XMLStreamException
     */
    private void writeAbstractColorStyle(AbstractColorStyle colorStyle) throws XMLStreamException{
        if (colorStyle instanceof IconStyle){
            this.writeIconStyle((IconStyle) colorStyle);
        } else if (colorStyle instanceof LabelStyle){
            this.writeLabelStyle((LabelStyle) colorStyle);
        } else if (colorStyle instanceof PolyStyle){
            this.writePolyStyle((PolyStyle) colorStyle);
        } else if (colorStyle instanceof LineStyle){
            this.writeLineStyle((LineStyle) colorStyle);
        }
    }

    /**
     * <p>This method writes the common fields for
     * instances of AbstractObject.</p>
     *
     * @param abstractObject The AbstractObject to write.
     * @throws XMLStreamException
     */
    private void writeCommonAbstractObject(AbstractObject abstractObject) throws XMLStreamException{
        if (abstractObject.getIdAttributes() != null){
            this.writeIdAttributes(abstractObject.getIdAttributes());
        }
        if (abstractObject.extensions().simples(Names.OBJECT) != null){
        }
    }

    /**
     * <p>This method writes identification attributes.</p>
     *
     * @param idAttributes The IdAttributes to write.
     * @throws XMLStreamException
     */
    private void writeIdAttributes(IdAttributes idAttributes) throws XMLStreamException{
        if(idAttributes.getId() != null){
            writer.writeAttribute(ATT_ID, idAttributes.getId());
        }
        if(idAttributes.getTargetId() != null){
            writer.writeAttribute(ATT_TARGET_ID, idAttributes.getTargetId());
        }
    }

    /**
     *
     * @param abstractFeature The AbstractFeature to write.
     * @throws XMLStreamException
     */
    private void writeAbstractFeature(AbstractFeature abstractFeature) throws XMLStreamException, KmlException{
        if (abstractFeature instanceof AbstractContainer){
            this.writeAbstractContainer((AbstractContainer)abstractFeature);
        } else if (abstractFeature instanceof NetworkLink){
            this.writeNetworkLink((NetworkLink) abstractFeature);
        } else if (abstractFeature instanceof AbstractOverlay){
            this.writeAbstractOverlay((AbstractOverlay)abstractFeature);
        } else if (abstractFeature instanceof Placemark){
            this.writePlacemark((Placemark)abstractFeature);
        }
    }

    /**
     *
     * @param networkLink
     * @throws XMLStreamException
     */
    private void writeNetworkLink(NetworkLink networkLink) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_NETWORK_LINK);
        this.writeCommonAbstractFeature(networkLink);
        this.writeRefreshVisibility(networkLink.getRefreshVisibility());
        this.writeFlyToView(networkLink.getFlyToView());
        if (networkLink.getLink() != null){
            if (networkLink.getLink() instanceof Url)
                this.writeUrl((Url) networkLink.getLink());
            else
                this.writeLink(networkLink.getLink());
        }
        if (networkLink.extensions().simples(Names.NETWORK_LINK) != null){
        }
        if (networkLink.extensions().complexes(Names.NETWORK_LINK) != null){
        }
        writer.writeEndElement();
    }

    /**
     * <p>This method writes the common fields for
     * instances of AbstractFeature.</p>
     *
     * @param abstractFeature The AbstractFeature to write.
     * @throws XMLStreamException
     */
    private void writeCommonAbstractFeature(AbstractFeature abstractFeature) throws XMLStreamException, KmlException{
        this.writeCommonAbstractObject(abstractFeature);
        if (abstractFeature.getName() != null){
            this.writeName(abstractFeature.getName());
        }
        this.writeVisibility(abstractFeature.getVisibility());
        this.writeOpen(abstractFeature.getOpen());
        if (abstractFeature.getAuthor() != null){
            this.writeAtomPersonConstruct(abstractFeature.getAuthor());
        }
        if (abstractFeature.getAtomLink() != null){
            this.writeAtomLink(abstractFeature.getAtomLink());
        }
        if (abstractFeature.getAddress() != null){
            this.writeAddress(abstractFeature.getAddress());
        }
        if (abstractFeature.getAddressDetails() != null){
            this.writeXalAddresDetails(abstractFeature.getAddressDetails());
        }
        if (abstractFeature.getPhoneNumber() != null){
            this.writePhoneNumber(abstractFeature.getPhoneNumber());
        }
        if (abstractFeature.getSnippet() != null){
            this.writeSnippet(abstractFeature.getSnippet());
        }
        if (abstractFeature.getDescription() != null){
            this.writeDescription(abstractFeature.getDescription());
        }
        if (abstractFeature.getView() != null){
            this.writeAbstractView(abstractFeature.getView());
        }
        if (abstractFeature.getTimePrimitive() != null){
            this.writeAbstractTimePrimitive(abstractFeature.getTimePrimitive());
        }
        if (abstractFeature.getStyleUrl() != null){
            this.writeStyleUrl(abstractFeature.getStyleUrl());
        }
        for(AbstractStyleSelector abstractStyleSelector : abstractFeature.getStyleSelectors()){
            this.writeAbstractStyleSelector(abstractStyleSelector);
        }
        if (abstractFeature.getRegion() != null){
            this.writeRegion(abstractFeature.getRegion());
        }
        if (abstractFeature.getExtendedData() != null){
            this.writeExtendedData(abstractFeature.getExtendedData());
        }
        if (abstractFeature.extensions().simples(Names.FEATURE) != null){
        }
        if (abstractFeature.extensions().complexes(Names.FEATURE) != null){
        }
    }

    /**
     *
     * @param dataContainer
     * @throws XMLStreamException
     */
    private void writeExtendedData(Object dataContainer) throws XMLStreamException{
        if(dataContainer instanceof ExtendedData){
            this.writeExtendedData((ExtendedData) dataContainer);
        } else if (dataContainer instanceof Metadata){
            this.writeMetaData((Metadata) dataContainer);
        }
    }

    /**
     *
     * @param extendedData
     * @throws XMLStreamException
     */
    private void writeExtendedData(ExtendedData extendedData) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_EXTENDED_DATA);
        for (Data data : extendedData.getDatas()){
            this.writeData(data);
        }
        for (SchemaData schemaData : extendedData.getSchemaData()){
            this.writeSchemaData(schemaData);
        }
        if (extendedData.getAnyOtherElements() != null){

        }
        writer.writeEndElement();
    }

    /**
     *
     * @param metadata
     * @deprecated
     */
    @Deprecated
    private void writeMetaData(Metadata metadata) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_META_DATA);
        writer.writeEndElement();
    }

    /**
     * 
     * @param schemaData
     * @throws XMLStreamException
     */
    private void writeSchemaData(SchemaData schemaData) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_SCHEMA_DATA);
        writer.writeAttribute(ATT_SCHEMA_URL, schemaData.getSchemaURL().toString());
        this.writeCommonAbstractObject(schemaData);
        for (SimpleData simpleData : schemaData.getSimpleDatas()){
            this.writeSimpleData(simpleData);
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param simpleData
     * @throws XMLStreamException
     */
    private void writeSimpleData(SimpleData simpleData) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_SIMPLE_DATA);
        writer.writeAttribute(ATT_NAME, simpleData.getName());
        writer.writeCharacters(simpleData.getContent());
        writer.writeEndElement();
    }

    /**
     * 
     * @param data
     * @throws XMLStreamException
     */
    private void writeData(Data data) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_DATA);
        writer.writeAttribute(ATT_NAME, data.getName());
        this.writeCommonAbstractObject(data);
        if (data.getDisplayName() != null){
            this.writeDisplayName(data.getDisplayName());
        }
        if (data.getValue() != null){
            this.writeValue(data.getValue());
        }
        if (data.getDataExtensions() != null){

        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param region
     * @throws XMLStreamException
     */
    private void writeRegion(Region region) throws XMLStreamException{
        writer.writeStartElement(URI_KML,TAG_REGION);
        this.writeCommonAbstractObject(region);
        if(region.getLatLonAltBox() != null){
            this.writeLatLonAltBox(region.getLatLonAltBox());
        }
        if(region.getLod() != null){
            this.writeLod(region.getLod());
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param lod
     * @throws XMLStreamException
     */
    private void writeLod(Lod lod) throws XMLStreamException{
        writer.writeStartElement(URI_KML,TAG_LOD);
        this.writeCommonAbstractObject(lod);
        if (isFiniteNumber(lod.getMinLodPixels())){
            this.writeMinLodPixels(lod.getMinLodPixels());
        }
        if (isFiniteNumber(lod.getMaxLodPixels())){
            this.writeMaxLodPixels(lod.getMaxLodPixels());
        }
        if (isFiniteNumber(lod.getMinFadeExtent())){
            this.writeMinFadeExtent(lod.getMinFadeExtent());
        }
        if (isFiniteNumber(lod.getMaxFadeExtent())){
            this.writeMaxFadeExtent(lod.getMaxFadeExtent());
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param latLonAltBox
     * @throws XMLStreamException
     */
    private void writeLatLonAltBox(LatLonAltBox latLonAltBox) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_LAT_LON_ALT_BOX);
        this.writeCommonAbstractLatLonBox(latLonAltBox);
        if (isFiniteNumber(latLonAltBox.getMinAltitude())){
            this.writeMinAltitude(latLonAltBox.getMinAltitude());
        }
        if (isFiniteNumber(latLonAltBox.getMaxAltitude())){
            this.writeMaxAltitude(latLonAltBox.getMaxAltitude());
        }
        if (latLonAltBox.getAltitudeMode() != null){
            this.writeAltitudeMode(latLonAltBox.getAltitudeMode());
        }
        writer.writeEndElement();
    }

    private void writeAtomPersonConstruct(AtomPersonConstruct person) throws XMLStreamException{
        this.atomWriter.writeAuthor(person);
    }

    private void writeAtomLink(AtomLink link) throws XMLStreamException{
        this.atomWriter.writeLink(link);
    }

    /**
     *
     * @param details
     */
    private void writeXalAddresDetails(AddressDetails details) throws XMLStreamException{
        this.xalWriter.setWriter(writer);
        try {
            this.xalWriter.writeAddressDetails(details);
        } catch (XalException ex) {
            Logger.getLogger(KmlWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.writer = this.xalWriter.getWriter();
    }

    /**
     *
     * @param abstractTimePrimitive The AbstractTimePrimitive to write.
     * @throws XMLStreamException
     */
    private void writeAbstractTimePrimitive(AbstractTimePrimitive abstractTimePrimitive) throws XMLStreamException{
        if (abstractTimePrimitive instanceof TimeSpan){
            this.writeTimeSpan((TimeSpan) abstractTimePrimitive);
        } else if (abstractTimePrimitive instanceof TimeStamp){
            this.writeTimeStamp((TimeStamp) abstractTimePrimitive);
        }
    }

    /**
     * 
     * @param timeSpan
     * @throws XMLStreamException
     */
    private void writeTimeSpan(TimeSpan timeSpan) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_TIME_SPAN);
        this.writeCommonAbstractTimePrimitive(timeSpan);
        if (timeSpan.getBegin() != null){
            this.writeBegin(timeSpan.getBegin());
        }
        if (timeSpan.getEnd() != null){
            this.writeEnd(timeSpan.getEnd());
        }
        if (timeSpan.extensions().simples(Names.TIME_SPAN) != null){
        }
        if (timeSpan.extensions().complexes(Names.TIME_SPAN) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param timeStamp
     * @throws XMLStreamException
     */
    private void writeTimeStamp(TimeStamp timeStamp) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_TIME_STAMP);
        this.writeCommonAbstractTimePrimitive(timeStamp);
        if (timeStamp.getWhen() != null){
            this.writeWhen(timeStamp.getWhen());
        }
        if (timeStamp.extensions().simples(Names.TIME_STAMP) != null){
        }
        if (timeStamp.extensions().complexes(Names.TIME_STAMP) != null){
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param abstractView The AbstractView to write.
     * @throws XMLStreamException
     */
    private void writeAbstractView(AbstractView abstractView) throws XMLStreamException{
        if (abstractView instanceof LookAt){
            this.writeLookAt((LookAt)abstractView);
        } else if (abstractView instanceof Camera){
            this.writeCamera((Camera)abstractView);
        }
    }

    /**
     * 
     * @param lookAt
     * @throws XMLStreamException
     */
    private void writeLookAt(LookAt lookAt) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_LOOK_AT);
        this.writeCommonAbstractView(lookAt);
        if (isFiniteNumber(lookAt.getLongitude())){
            this.writeLongitude(lookAt.getLongitude());
        }
        if (isFiniteNumber(lookAt.getLatitude())){
            this.writeLatitude(lookAt.getLatitude());
        }
        if (isFiniteNumber(lookAt.getAltitude())){
            this.writeAltitude(lookAt.getAltitude());
        }
        if (isFiniteNumber(lookAt.getHeading())){
            this.writeHeading(lookAt.getHeading());
        }
        if (isFiniteNumber(lookAt.getTilt())){
            this.writeTilt(lookAt.getTilt());
        }
        if (isFiniteNumber(lookAt.getRange())){
            this.writeRange(lookAt.getRange());
        }
        if (lookAt.extensions().simples(Names.LOOK_AT) != null){
        }
        if (lookAt.extensions().complexes(Names.LOOK_AT) != null){
        }
        writer.writeEndElement();
    }

    private void writeCamera(Camera camera) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_CAMERA);
        this.writeCommonAbstractView(camera);
        if (isFiniteNumber(camera.getLongitude())){
            this.writeLongitude(camera.getLongitude());
        }
        if (isFiniteNumber(camera.getLatitude())){
            this.writeLatitude(camera.getLatitude());
        }
        if (isFiniteNumber(camera.getAltitude())){
            this.writeAltitude(camera.getAltitude());
        }
        if (isFiniteNumber(camera.getHeading())){
            this.writeHeading(camera.getHeading());
        }
        if (isFiniteNumber(camera.getTilt())){
            this.writeTilt(camera.getTilt());
        }
        if (isFiniteNumber(camera.getRoll())){
            this.writeRoll(camera.getRoll());
        }
        if (camera.getAltitudeMode() != null){
            this.writeAltitudeMode(camera.getAltitudeMode());
        }
        if (camera.extensions().simples(Names.CAMERA) != null){
        }
        if (camera.extensions().complexes(Names.CAMERA) != null){
        }
        writer.writeEndElement();
    }

    /**
     * <p>This method writes the common fields for
     * AbstractView instances.</p>
     * 
     * @param abstractView The AbstractView to write.
     * @throws XMLStreamException
     */
    private void writeCommonAbstractView(AbstractView abstractView) throws XMLStreamException{
        this.writeCommonAbstractObject(abstractView);
        if (abstractView.extensions().simples(Names.VIEW) != null){
        }
        if (abstractView.extensions().complexes(Names.VIEW) != null){
        }
    }

    /**
     * <p>This method writes the common fields for
     * AbstractTimePrimitive instances.</p>
     *
     * @param abstractTimePrimitive The AbstractTimePrimitive to write.
     * @throws XMLStreamException
     */
    private void writeCommonAbstractTimePrimitive(AbstractTimePrimitive abstractTimePrimitive) throws XMLStreamException{
        this.writeCommonAbstractObject(abstractTimePrimitive);
        if (abstractTimePrimitive.extensions().simples(Names.TIME_PRIMITIVE) != null){
        }
        if (abstractTimePrimitive.extensions().complexes(Names.TIME_PRIMITIVE) != null){
        }
    }

    /**
     *
     * @param abstractStyleSelector The AbstractStyleSelector to write.
     * @throws XMLStreamException
     */
    private void writeAbstractStyleSelector(AbstractStyleSelector abstractStyleSelector) throws XMLStreamException, KmlException{
        if (abstractStyleSelector instanceof Style){
            this.writeStyle((Style)abstractStyleSelector);
        } else if (abstractStyleSelector instanceof StyleMap){
            this.writeStyleMap((StyleMap)abstractStyleSelector);
        }
    }

    /**
     * <p>This method writes the common fields for
     * AbstractStyleSelector instances.</p>
     *
     * @param abstractStyleSelector
     * @throws XMLStreamException
     */
    private void writeCommonAbstractStyleSelector(AbstractStyleSelector abstractStyleSelector) throws XMLStreamException{
        this.writeCommonAbstractObject(abstractStyleSelector);
        if (abstractStyleSelector.extensions().simples(Names.STYLE_SELECTOR) != null){
        }
        if (abstractStyleSelector.extensions().complexes(Names.STYLE_SELECTOR) != null){
        }
    }

    /**
     * 
     * @param styleMap
     * @throws XMLStreamException
     */
    private void writeStyleMap(StyleMap styleMap) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_STYLE_MAP);
        this.writeCommonAbstractStyleSelector(styleMap);
        for(Pair pair : styleMap.getPairs()){
            this.writePair(pair);
        }
        if (styleMap.extensions().simples(Names.STYLE_MAP) != null){
        }
        if (styleMap.extensions().complexes(Names.STYLE_MAP) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param pair
     * @throws XMLStreamException
     */
    private void writePair(Pair pair) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_PAIR);
        this.writeCommonAbstractObject(pair);
        if (pair.getKey() != null){
            this.writeKey(pair.getKey());
        }
        if (pair.getStyleUrl() != null){
            this.writeStyleUrl(pair.getStyleUrl());
        }
        if (pair.getAbstractStyleSelector() != null){
            checkVersion(URI_KML_2_2);
            this.writeAbstractStyleSelector(pair.getAbstractStyleSelector());
        }
        if (pair.extensions().simples(Names.PAIR) != null){
        }
        if (pair.extensions().complexes(Names.PAIR) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param style
     * @throws XMLStreamException
     */
    private void writeStyle(Style style) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_STYLE);
        this.writeCommonAbstractStyleSelector(style);
        if (style.getIconStyle() != null){
            this.writeIconStyle(style.getIconStyle());
        }
        if (style.getLabelStyle() != null){
            this.writeLabelStyle(style.getLabelStyle());
        }
        if (style.getLineStyle() != null){
            this.writeLineStyle(style.getLineStyle());
        }
        if (style.getPolyStyle() != null){
            this.writePolyStyle(style.getPolyStyle());
        }
        if (style.getBalloonStyle() != null){
            this.writeBalloonStyle(style.getBalloonStyle());
        }
        if (style.getListStyle() != null){
            this.writeListStyle(style.getListStyle());
        }
        if (style.extensions().simples(Names.STYLE) != null){
        }
        if (style.extensions().complexes(Names.STYLE) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param iconStyle
     * @throws XMLStreamException
     */
    private void writeIconStyle(IconStyle iconStyle) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_ICON_STYLE);
        this.writeCommonAbstractColorStyle(iconStyle);
        if (isFiniteNumber(iconStyle.getScale())){
            this.writeScale(iconStyle.getScale());
        }
        if (isFiniteNumber(iconStyle.getHeading())){
            this.writeHeading(iconStyle.getHeading());
        }
        if (iconStyle.getIcon() != null){
            this.writeIcon(iconStyle.getIcon());
        }
        if (iconStyle.getHotSpot() != null){
            this.writeHotSpot(iconStyle.getHotSpot());
        }
        if (iconStyle.extensions().simples(Names.ICON_STYLE) != null){
        }
        if (iconStyle.extensions().complexes(Names.ICON_STYLE) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param labelStyle
     * @throws XMLStreamException
     */
    private void writeLabelStyle(LabelStyle labelStyle) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_LABEL_STYLE);
        this.writeCommonAbstractColorStyle(labelStyle);
        if (isFiniteNumber(labelStyle.getScale())){
            this.writeScale(labelStyle.getScale());
        }
        if (labelStyle.extensions().simples(Names.LABEL_STYLE) != null){
        }
        if (labelStyle.extensions().complexes(Names.LABEL_STYLE) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param lineStyle
     * @throws XMLStreamException
     */
    private void writeLineStyle(LineStyle lineStyle) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_LINE_STYLE);
        this.writeCommonAbstractColorStyle(lineStyle);
        if (isFiniteNumber(lineStyle.getWidth())){
            this.writeWidth(lineStyle.getWidth());
        }
        if (lineStyle.extensions().simples(Names.LINE_STYLE) != null){
        }
        if (lineStyle.extensions().complexes(Names.LINE_STYLE) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param polyStyle
     * @throws XMLStreamException
     */
    private void writePolyStyle(PolyStyle polyStyle) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_POLY_STYLE);
        this.writeCommonAbstractColorStyle(polyStyle);
        this.writeFill(polyStyle.getFill());
        this.writeOutline(polyStyle.getOutline());
        if (polyStyle.extensions().simples(Names.POLY_STYLE) != null){
        }
        if (polyStyle.extensions().complexes(Names.POLY_STYLE) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param balloonStyle
     * @throws XMLStreamException
     */
    private void writeBalloonStyle(BalloonStyle balloonStyle) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_BALLOON_STYLE);
        this.writeCommonAbstractSubStyle(balloonStyle);
        if (balloonStyle.getBgColor() != null){
            this.writeBgColor(balloonStyle.getBgColor());
        }
        if (balloonStyle.getTextColor() != null){
            this.writeTextColor(balloonStyle.getTextColor());
        }
        if (balloonStyle.getText() != null){
            this.writeText(balloonStyle.getText());
        }
        if (balloonStyle.getDisplayMode() != null
                && checkVersionSimple(URI_KML_2_2)){
            this.writeDisplayMode(balloonStyle.getDisplayMode());
        }
        if (balloonStyle.extensions().simples(Names.BALLOON_STYLE) != null){
        }
        if (balloonStyle.extensions().complexes(Names.BALLOON_STYLE) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param listStyle
     * @throws XMLStreamException
     */
    private void writeListStyle(ListStyle listStyle) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_LIST_STYLE);
        this.writeCommonAbstractSubStyle(listStyle);
        if (listStyle.getListItem() != null){
            this.writeListItem(listStyle.getListItem());
        }
        if (listStyle.getBgColor() != null){
            this.writeBgColor(listStyle.getBgColor());
        }
        for(ItemIcon itemIcon : listStyle.getItemIcons()){
            this.writeItemIcon(itemIcon);
        }
        if (isFiniteNumber(listStyle.getMaxSnippetLines())
                && checkVersionSimple(URI_KML_2_2)){
            this.writeMaxSnippetLines(listStyle.getMaxSnippetLines());
        }
        if (listStyle.extensions().simples(Names.LIST_STYLE) != null){
        }
        if (listStyle.extensions().complexes(Names.LIST_STYLE) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param itemIcon
     * @throws XMLStreamException
     */
    private void writeItemIcon(ItemIcon itemIcon) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_ITEM_ICON);
        this.writeCommonAbstractObject(itemIcon);
        if (itemIcon.getStates() != null){
            this.writeStates(itemIcon.getStates());
        }
        if (itemIcon.getHref() != null){
            this.writeHref(itemIcon.getHref());
        }
        if (itemIcon.extensions().simples(Names.ITEM_ICON) != null){
        }
        if (itemIcon.extensions().complexes(Names.ITEM_ICON) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param itemIconStates
     * @throws XMLStreamException
     */
    private void writeStates(List<ItemIconState> itemIconStates) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_STATE);
        int i = 0;
        int size = itemIconStates.size();
        for(ItemIconState itemIconState : itemIconStates){
            i++;
            if(i == size){
                writer.writeCharacters(itemIconState.getItemIconState());
            } else {
                writer.writeCharacters(itemIconState.getItemIconState()+" ");
            }
        }
        writer.writeEndElement();
    }

    /**
     * <p>This method writes an icon element typed as BasicLink.</p>
     *
     * @param icon
     * @throws XMLStreamException
     */
    private void writeIcon(BasicLink icon) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_ICON);
        if (icon.getIdAttributes() != null){
            this.writeIdAttributes(icon.getIdAttributes());
        }
        if (icon.extensions().simples(Names.OBJECT) != null){
        }
        if (icon.getHref() != null){
            this.writeHref(icon.getHref());
        }
        if (icon.extensions().simples(Names.BASIC_LINK) != null){
        }
        if (icon.extensions().complexes(Names.BASIC_LINK) != null){
        }
        writer.writeEndElement();
    }

    /**
     * <p>This method writes an icon element typed as Link.</p>
     *
     * @param icon
     * @throws XMLStreamException
     */
    private void writeIcon(Icon icon) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_ICON);
        this.writeLink_structure(icon);
        writer.writeEndElement();
    }

    /**
     * 
     * @param link
     * @throws XMLStreamException
     */
    private void writeLink(Link link) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_LINK);
        this.writeLink_structure(link);
        writer.writeEndElement();
    }

    /**
     * 
     * @param url
     * @throws XMLStreamException
     * @deprecated
     */
    @Deprecated
    private void writeUrl(Url url) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_URL);
        this.writeLink_structure(url);
        writer.writeEndElement();
    }

    /**
     * <p>This method writes the Link structure used by different elements.</p>
     *
     * @param link
     * @throws XMLStreamException
     */
    private void writeLink_structure(Link link) throws XMLStreamException{
        this.writeCommonAbstractObject(link);
        if (link.getHref() != null){
            this.writeHref(link.getHref());
        }
        if (link.extensions().simples(Names.BASIC_LINK) != null){
        }
        if (link.extensions().complexes(Names.BASIC_LINK) != null){
        }
        if (link.getRefreshMode() != null){
            this.writeRefreshMode(link.getRefreshMode());
        }
        if (isFiniteNumber(link.getRefreshInterval())){
            this.writeRefreshInterval(link.getRefreshInterval());
        }
        if (link.getViewRefreshMode() != null){
            this.writeViewRefreshMode(link.getViewRefreshMode());
        }
        if (isFiniteNumber(link.getViewRefreshTime())){
            this.writeViewRefreshTime(link.getViewRefreshTime());
        }
        if (isFiniteNumber(link.getViewBoundScale())){
            this.writeViewBoundScale(link.getViewBoundScale());
        }
        if (link.getViewFormat() != null){
            this.writeViewFormat(link.getViewFormat());
        }
        if (link.getHttpQuery() != null){
            this.writeHttpQuery(link.getHttpQuery());
        }
        if (link.extensions().simples(Names.LINK) != null){
        }
        if (link.extensions().complexes(Names.LINK) != null){
        }
    }

    /**
     * <p>This method writes the common fields for
     * instances of AbstractColorStyle.</p>
     *
     * @param abstractColorStyle The AbstractColorStyle to write.
     * @throws XMLStreamException
     */
    private void writeCommonAbstractColorStyle(AbstractColorStyle abstractColorStyle) throws XMLStreamException{
        this.writeCommonAbstractSubStyle(abstractColorStyle);
        if (abstractColorStyle.getColor() != null){
            this.writeColor(abstractColorStyle.getColor());
        }
        if (abstractColorStyle.getColorMode() != null){
            this.writeColorMode(abstractColorStyle.getColorMode());
        }
        if (abstractColorStyle.extensions().simples(Names.COLOR_STYLE) != null){
        }
        if (abstractColorStyle.extensions().complexes(Names.COLOR_STYLE) != null){
        }
    }

    /**
     * <p>This method writes the common fields for
     * instances of AbstractSubStyle.</p>
     *
     * @param abstractSubStyle The AbstractSubStyle to write.
     * @throws XMLStreamException
     */
    private void writeCommonAbstractSubStyle(AbstractSubStyle abstractSubStyle) throws XMLStreamException{
        this.writeCommonAbstractObject(abstractSubStyle);
        if (abstractSubStyle.extensions().simples(Names.SUB_STYLE) != null){
        }
        if (abstractSubStyle.extensions().complexes(Names.SUB_STYLE) != null){
        }
    }

    /**
     * 
     * @param placemark
     * @throws XMLStreamException
     */
    private void writePlacemark(Placemark placemark) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_PLACEMARK);
        this.writeCommonAbstractFeature(placemark);
        if (placemark.getAbstractGeometry() != null){
            this.writeAbstractGeometry(placemark.getAbstractGeometry());
        }
        if (placemark.extensions().simples(Names.PLACEMARK) != null){
        }
        if (placemark.extensions().complexes(Names.PLACEMARK) != null){
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param abstractContainer The AbstractContainer to write
     * @throws XMLStreamException
     */
    private void writeAbstractContainer(AbstractContainer abstractContainer) throws XMLStreamException, KmlException{
        if (abstractContainer instanceof Folder){
            this.writeFolder((Folder)abstractContainer);
        } else if (abstractContainer instanceof Document){
            this.writeDocument((Document)abstractContainer);
        }
    }

    /**
     *
     * @param abstractOverlay The AbstractOverlay to write.
     * @throws XMLStreamException
     */
    private void writeAbstractOverlay(AbstractOverlay abstractOverlay) throws XMLStreamException, KmlException{
        if (abstractOverlay instanceof GroundOverlay){
            this.writeGroundOverlay((GroundOverlay)abstractOverlay);
        } else if (abstractOverlay instanceof ScreenOverlay){
            this.writeScreenOverlay((ScreenOverlay)abstractOverlay);
        } else if (abstractOverlay instanceof PhotoOverlay){
            this.writePhotoOverlay((PhotoOverlay) abstractOverlay);
        }
    }

    /**
     *
     * @param photoOverlay
     * @throws XMLStreamException
     */
    private void writePhotoOverlay(PhotoOverlay photoOverlay) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_PHOTO_OVERLAY);
        this.writeCommonAbstractOverlay(photoOverlay);
        if (isFiniteNumber(photoOverlay.getRotation())){
            this.writeRotation(photoOverlay.getRotation());
        }
        if (photoOverlay.getViewVolume() != null){
            this.writeViewVolume(photoOverlay.getViewVolume());
        }
        if (photoOverlay.getImagePyramid() != null){
            this.writeImagePyramid(photoOverlay.getImagePyramid());
        }
        if (photoOverlay.getPoint() != null){
            this.writePoint(photoOverlay.getPoint());
        }
        if (photoOverlay.getShape() != null){
            this.writeShape(photoOverlay.getShape());
        }
        if (photoOverlay.extensions().simples(Names.PHOTO_OVERLAY) != null){
        }
        if (photoOverlay.extensions().complexes(Names.PHOTO_OVERLAY) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param imagePyramid
     * @throws XMLStreamException
     */
    private void writeImagePyramid(ImagePyramid imagePyramid) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_IMAGE_PYRAMID);
        this.writeCommonAbstractObject(imagePyramid);
        if (isFiniteNumber(imagePyramid.getTitleSize())){
            this.writeTitleSize(imagePyramid.getTitleSize());
        }
        if (isFiniteNumber(imagePyramid.getMaxWidth())){
            this.writeMaxWidth(imagePyramid.getMaxWidth());
        }
        if (isFiniteNumber(imagePyramid.getMaxHeight())){
            this.writeMaxHeight(imagePyramid.getMaxHeight());
        }
        if (imagePyramid.getGridOrigin() != null){
            this.writeGridOrigin(imagePyramid.getGridOrigin());
        }
        writer.writeEndElement();
    }
    
    /**
     *
     * @param viewVolume
     * @throws XMLStreamException
     */
    private void writeViewVolume(ViewVolume viewVolume) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_VIEW_VOLUME);
        this.writeCommonAbstractObject(viewVolume);
        if (isFiniteNumber(viewVolume.getLeftFov())){
            this.writeLeftFov(viewVolume.getLeftFov());
        }
        if (isFiniteNumber(viewVolume.getRightFov())){
            this.writeRightFov(viewVolume.getRightFov());
        }
        if (isFiniteNumber(viewVolume.getBottomFov())){
            this.writeBottomFov(viewVolume.getBottomFov());
        }
        if (isFiniteNumber(viewVolume.getTopFov())){
            this.writeTopFov(viewVolume.getTopFov());
        }
        if (isFiniteNumber(viewVolume.getNear())){
            this.writeNear(viewVolume.getNear());
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param screenOverlay
     * @throws XMLStreamException
     */
    private void writeScreenOverlay(ScreenOverlay screenOverlay) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_SCREEN_OVERLAY);
        this.writeCommonAbstractOverlay(screenOverlay);
        if (screenOverlay.getOverlayXY() != null){
            this.writeOverlayXY(screenOverlay.getOverlayXY());
        }
        if (screenOverlay.getScreenXY() != null){
            this.writeScreenXY(screenOverlay.getScreenXY());
        }
        if (screenOverlay.getRotationXY() != null){
            this.writeRotationXY(screenOverlay.getRotationXY());
        }
        if (screenOverlay.getSize() != null){
            this.writeSize(screenOverlay.getSize());
        }
        if (isFiniteNumber(screenOverlay.getRotation())){
            this.writeRotation(screenOverlay.getRotation());
        }
        if (screenOverlay.extensions().simples(Names.SCREEN_OVERLAY) != null){
        }
        if (screenOverlay.extensions().complexes(Names.SCREEN_OVERLAY) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param groundOverlay
     * @throws XMLStreamException
     */
    private void writeGroundOverlay(GroundOverlay groundOverlay) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_GROUND_OVERLAY);
        this.writeCommonAbstractOverlay(groundOverlay);
        if (isFiniteNumber(groundOverlay.getAltitude())){
            this.writeAltitude(groundOverlay.getAltitude());
        }
        if (groundOverlay.getAltitudeMode() != null){
            this.writeAltitudeMode(groundOverlay.getAltitudeMode());
        }
        if (groundOverlay.getLatLonBox() != null){
            this.writeLatLonBox(groundOverlay.getLatLonBox());
        }
        if (groundOverlay.extensions().simples(Names.GROUND_OVERLAY) != null){
        }
        if (groundOverlay.extensions().complexes(Names.GROUND_OVERLAY) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param latLonBox
     * @throws XMLStreamException
     */
    private void writeLatLonBox(LatLonBox latLonBox) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_LAT_LON_BOX);
        this.writeCommonAbstractLatLonBox(latLonBox);
        if (isFiniteNumber(latLonBox.getRotation())){
            this.writeRotation(latLonBox.getRotation());
        }
        if (latLonBox.extensions().simples(Names.LAT_LON_BOX) != null){
        }
        if (latLonBox.extensions().complexes(Names.LAT_LON_BOX) != null){
        }
        writer.writeEndElement();
    }

    /**
     * <p>This method writes tha common fields for
     * AbstractLatLonBox instances.</p>
     *
     * @param abstractLatLonBox
     * @throws XMLStreamException
     */
    private void writeCommonAbstractLatLonBox(AbstractLatLonBox abstractLatLonBox) throws XMLStreamException{
        this.writeCommonAbstractObject(abstractLatLonBox);
        if (isFiniteNumber(abstractLatLonBox.getNorth())){
            this.writeNorth(abstractLatLonBox.getNorth());
        }
        if (isFiniteNumber(abstractLatLonBox.getSouth())){
            this.writeSouth(abstractLatLonBox.getSouth());
        }
        if (isFiniteNumber(abstractLatLonBox.getEast())){
            this.writeEast(abstractLatLonBox.getEast());
        }
        if (isFiniteNumber(abstractLatLonBox.getWest())){
            this.writeWest(abstractLatLonBox.getWest());
        }
        if (abstractLatLonBox.extensions().simples(Names.ABSTRACT_LAT_LON_BOX) != null){
        }
        if (abstractLatLonBox.extensions().complexes(Names.ABSTRACT_LAT_LON_BOX) != null){
        }
    }
    
    private void writeCommonAbstractOverlay(AbstractOverlay abstractOverlay) throws XMLStreamException, KmlException{
        this.writeCommonAbstractFeature(abstractOverlay);
        if (abstractOverlay.getColor() != null){
            this.writeColor(abstractOverlay.getColor());
        }
        if (isFiniteNumber(abstractOverlay.getDrawOrder())){
            this.writeDrawOrder(abstractOverlay.getDrawOrder());
        }
        if (abstractOverlay.getIcon() != null){
            this.writeIcon(abstractOverlay.getIcon());
        }
        if (abstractOverlay.extensions().simples(Names.OVERLAY) != null){
        }
        if (abstractOverlay.extensions().complexes(Names.OVERLAY) != null){
        }
    }

    /**
     * <p>This method writes tha common fields for
     * AbstractContainer instances.</p>
     *
     * @param abstractContainer The AbstractContainer to write.
     * @throws XMLStreamException
     */
    private void writeCommonAbstractContainer(AbstractContainer abstractContainer) throws XMLStreamException, KmlException{
        this.writeCommonAbstractFeature(abstractContainer);
        if (abstractContainer.extensions().simples(Names.CONTAINER) != null){
        }
        if (abstractContainer.extensions().complexes(Names.CONTAINER) != null){
        }
    }

    /**
     *
     * @param folder
     * @throws XMLStreamException
     */
    private void writeFolder(Folder folder) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_FOLDER);
        this.writeCommonAbstractContainer(folder);

        for(AbstractFeature abstractFeature : folder.getAbstractFeatures()){
            this.writeAbstractFeature(abstractFeature);
        }
        if (folder.extensions().simples(Names.FOLDER) != null){
        }
        if (folder.extensions().complexes(Names.FOLDER) != null){
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param document
     * @throws XMLStreamException
     */
    private void writeDocument(Document document) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_DOCUMENT);
        this.writeCommonAbstractContainer(document);
        for(Schema schema : document.getSchemas()){
            checkVersion(URI_KML_2_2);
            this.writeSchema(schema);
        }
        for(AbstractFeature abstractFeature : document.getAbstractFeatures()){
            this.writeAbstractFeature(abstractFeature);
        }
        if (document.extensions().simples(Names.DOCUMENT) != null){
        }
        if (document.extensions().complexes(Names.DOCUMENT) != null){
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param schema
     * @throws XMLStreamException
     */
    private void writeSchema(Schema schema) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_SCHEMA);
        if (schema.getName() != null){
            writer.writeAttribute(ATT_NAME, schema.getName());
        }
        if (schema.getId() != null){
            writer.writeAttribute(ATT_ID, schema.getId());
        }
        for (SimpleField sf : schema.getSimpleFields()){
            this.writeSimpleField(sf);
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param simpleField
     * @throws XMLStreamException
     */
    private void writeSimpleField(SimpleField simpleField) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_SIMPLE_FIELD);
        if (simpleField.getType() != null){
            writer.writeAttribute(ATT_TYPE, simpleField.getType());
        }
        if (simpleField.getName() != null){
            writer.writeAttribute(ATT_NAME, simpleField.getName());
        }
        if (simpleField.getDisplayName() != null){
            this.writeDisplayName(simpleField.getDisplayName());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param abstractGeometry
     * @throws XMLStreamException
     */
    private void writeAbstractGeometry(AbstractGeometry abstractGeometry) throws XMLStreamException, KmlException{
        if (abstractGeometry instanceof MultiGeometry){
            this.writeMultiGeometry((MultiGeometry) abstractGeometry);
        } else if (abstractGeometry instanceof LineString){
            this.writeLineString((LineString) abstractGeometry);
        } else if (abstractGeometry instanceof Polygon){
            this.writePolygon((Polygon) abstractGeometry);
        } else if (abstractGeometry instanceof Point){
            this.writePoint((Point) abstractGeometry);
        } else if (abstractGeometry instanceof LinearRing){
            this.writeLinearRing((LinearRing) abstractGeometry);
        } else if (abstractGeometry instanceof Model){
            this.writeModel((Model) abstractGeometry);
        }
    }

    /**
     * <p>This method writes the common fields for
     * instances of AbstractGeometry.</p>
     *
     * @param abstractGeometry
     * @throws XMLStreamException
     */
    private void writeCommonAbstractGeometry(AbstractGeometry abstractGeometry) throws XMLStreamException{
        this.writeCommonAbstractObject(abstractGeometry);
        if (abstractGeometry.extensions().simples(Names.GEOMETRY) != null){
        }
        if (abstractGeometry.extensions().complexes(Names.GEOMETRY) != null){
        }
    }

    /**
     * 
     * @param model
     * @throws XMLStreamException
     */
    private void writeModel(Model model) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_MODEL);
        this.writeCommonAbstractGeometry(model);
        if (model.getAltitudeMode() != null){
            this.writeAltitudeMode(model.getAltitudeMode());
        }
        if (model.getLocation() != null){
            this.writeLocation(model.getLocation());
        }
        if (model.getOrientation() != null){
            this.writeOrientation(model.getOrientation());
        }
        if (model.getScale() != null){
            this.writeScale(model.getScale());
        }
        if (model.getLink() != null){
            this.writeLink(model.getLink());
        }
        if (model.getRessourceMap() != null){
            checkVersion(URI_KML_2_2);
            this.writeResourceMap(model.getRessourceMap());
        }
        if (model.extensions().simples(Names.MODEL) != null){
        }
        if (model.extensions().complexes(Names.MODEL) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param location
     * @throws XMLStreamException
     */
    private void writeLocation(Location location) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_LOCATION);
        this.writeCommonAbstractObject(location);
        if (isFiniteNumber(location.getLongitude())){
            this.writeLongitude(location.getLongitude());
        }
        if (isFiniteNumber(location.getLatitude())){
            this.writeLatitude(location.getLatitude());
        }
        if (isFiniteNumber(location.getAltitude())){
            this.writeAltitude(location.getAltitude());
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param orientation
     * @throws XMLStreamException
     */
    private void writeOrientation(Orientation orientation) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_ORIENTATION);
        this.writeCommonAbstractObject(orientation);
        if (isFiniteNumber(orientation.getHeading())){
            this.writeHeading(orientation.getHeading());
        }
        if (isFiniteNumber(orientation.getTilt())){
            this.writeTilt(orientation.getTilt());
        }
        if (isFiniteNumber(orientation.getRoll())){
            this.writeRoll(orientation.getRoll());
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param scale
     * @throws XMLStreamException
     */
    private void writeScale(Scale scale) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_SCALE_BIG);
        if (isFiniteNumber(scale.getX())){
            this.writeX(scale.getX());
        }
        if (isFiniteNumber(scale.getY())){
            this.writeY(scale.getY());
        }
        if (isFiniteNumber(scale.getZ())){
            this.writeZ(scale.getZ());
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param resourceMap
     * @throws XMLStreamException
     */
    private void writeResourceMap(ResourceMap resourceMap) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_RESOURCE_MAP);
        this.writeCommonAbstractObject(resourceMap);
        for (Alias alias : resourceMap.getAliases()){
            this.writeAlias(alias);
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param alias
     * @throws XMLStreamException
     */
    private void writeAlias(Alias alias) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_ALIAS);
        this.writeCommonAbstractObject(alias);
        if (alias.getTargetHref() != null){
            this.writeTargetHref(alias.getTargetHref());
        }
        if (alias.getSourceHref() != null){
            this.writeSourceHref(alias.getSourceHref());
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param polygon
     * @throws XMLStreamException
     */
    private void writePolygon(Polygon polygon) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_POLYGON);
        this.writeCommonAbstractGeometry(polygon);
        this.writeExtrude(polygon.getExtrude());
        this.writeTessellate(polygon.getTessellate());
        if (polygon.getAltitudeMode() != null){
            this.writeAltitudeMode(polygon.getAltitudeMode());
        }
        if (polygon.getOuterBoundary() != null){
            this.writeOuterBoundaryIs(polygon.getOuterBoundary());
        }
        for(Boundary innerBoundaryIs : polygon.getInnerBoundaries()){
            this.writeInnerBoundaryIs(innerBoundaryIs);
        }
        if (polygon.extensions().simples(Names.POLYGON) != null){
        }
        if (polygon.extensions().complexes(Names.POLYGON) != null){
        }
        writer.writeEndElement();
    }

    private void writeOuterBoundaryIs(Boundary boundary) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_OUTER_BOUNDARY_IS);
        this.writeBoundary(boundary);
        writer.writeEndElement();
    }
    
    private void writeInnerBoundaryIs(Boundary boundary) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_INNER_BOUNDARY_IS);
        this.writeBoundary(boundary);
        writer.writeEndElement();
    }

    /**
     * 
     * @param boundary
     * @throws XMLStreamException
     */
    private void writeBoundary(Boundary boundary) throws XMLStreamException{
        if (boundary.getLinearRing() != null){
            this.writeLinearRing(boundary.getLinearRing());
        }
        if (boundary.extensions().simples(Names.BOUNDARY) != null){
        }
        if (boundary.extensions().complexes(Names.BOUNDARY) != null){
        }
    }

    /**
     * 
     * @param lineString
     * @throws XMLStreamException
     */
    private void writeLineString(LineString lineString) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_LINE_STRING);
        this.writeCommonAbstractGeometry(lineString);
        this.writeExtrude(lineString.getExtrude());
        this.writeTessellate(lineString.getTessellate());
        if (lineString.getAltitudeMode() != null){
            this.writeAltitudeMode(lineString.getAltitudeMode());
        }

        this.writeCoordinates(lineString.getCoordinateSequence());

        if (lineString.extensions().simples(Names.LINE_STRING) != null){
        }
        if (lineString.extensions().complexes(Names.LINE_STRING) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param linearRing
     * @throws XMLStreamException
     */
    private void writeLinearRing(LinearRing linearRing) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_LINEAR_RING);
        this.writeCommonAbstractGeometry(linearRing);
        this.writeExtrude(linearRing.getExtrude());
        this.writeTessellate(linearRing.getTessellate());
        if (linearRing.getAltitudeMode() != null){
            this.writeAltitudeMode(linearRing.getAltitudeMode());
        }
        this.writeCoordinates(linearRing.getCoordinateSequence());
        if (linearRing.extensions().simples(Names.LINEAR_RING) != null){
        }
        if (linearRing.extensions().complexes(Names.LINEAR_RING) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param multiGeometry
     * @throws XMLStreamException
     */
    private void writeMultiGeometry(MultiGeometry multiGeometry) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_MULTI_GEOMETRY);
        this.writeCommonAbstractGeometry(multiGeometry);
        for (AbstractGeometry abstractGeometry : multiGeometry.getGeometries()){
            this.writeAbstractGeometry(abstractGeometry);
        }
        if (multiGeometry.extensions().simples(Names.MULTI_GEOMETRY) != null){
        }
        if (multiGeometry.extensions().complexes(Names.MULTI_GEOMETRY) != null){
        }
        writer.writeEndElement();
    }


    /**
     *
     * @{@inheritDoc }
     */
    private void writePoint(Point point) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_POINT);
        this.writeCommonAbstractGeometry(point);
        this.writeExtrude(point.getExtrude());
        if (point.getAltitudeMode() != null){
            this.writeAltitudeMode(point.getAltitudeMode());
        }
        if (point.getCoordinateSequence() != null){
            this.writeCoordinates(point.getCoordinateSequence());
        }
        if (point.extensions().simples(Names.POINT) != null){
        }
        if (point.extensions().complexes(Names.POINT) != null){
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param coordinates
     * @throws XMLStreamException
     */
    private void writeCoordinates(Coordinates coordinates) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_COORDINATES);
        writer.writeCharacters(KmlUtilities.toString(coordinates));
        writer.writeEndElement();
    }

    /**
     *
     * @param extrude
     * @throws XMLStreamException
     */
    private void writeExtrude(boolean extrude) throws XMLStreamException{
        if (DEF_EXTRUDE != extrude){
            writer.writeStartElement(URI_KML, TAG_EXTRUDE);
            if(extrude){
                writer.writeCharacters(SimpleType.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleType.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param visibility
     * @throws XMLStreamException
     */
    private void writeVisibility(boolean visibility) throws XMLStreamException{
        if (DEF_VISIBILITY != visibility){
            writer.writeStartElement(URI_KML, TAG_VISIBILITY);
            if(visibility){
                writer.writeCharacters(SimpleType.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleType.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param open
     * @throws XMLStreamException
     */
    private void writeOpen(boolean open) throws XMLStreamException{
        if (DEF_OPEN != open){
            writer.writeStartElement(URI_KML, TAG_OPEN);
            if(open){
                writer.writeCharacters(SimpleType.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleType.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param fill
     * @throws XMLStreamException
     */
    private void writeFill(boolean fill) throws XMLStreamException{
        if (DEF_FILL != fill){
            writer.writeStartElement(URI_KML, TAG_FILL);
            if(fill){
                writer.writeCharacters(SimpleType.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleType.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param outline
     * @throws XMLStreamException
     */
    private void writeOutline(boolean outline) throws XMLStreamException{
        if (DEF_OUTLINE != outline){
            writer.writeStartElement(URI_KML, TAG_OUTLINE);
            if(outline){
                writer.writeCharacters(SimpleType.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleType.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param tessellate
     * @throws XMLStreamException
     */
    private void writeTessellate(boolean tessellate) throws XMLStreamException{
        if (DEF_TESSELLATE != tessellate){
            writer.writeStartElement(URI_KML, TAG_TESSELLATE);
            if(tessellate){
                writer.writeCharacters(SimpleType.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleType.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param refreshVisibility
     * @throws XMLStreamException
     */
    private void writeRefreshVisibility(boolean refreshVisibility) throws XMLStreamException{
        if (DEF_REFRESH_VISIBILITY != refreshVisibility){
            writer.writeStartElement(URI_KML, TAG_REFRESH_VISIBILITY);
            if(refreshVisibility){
                writer.writeCharacters(SimpleType.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleType.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     * 
     * @param flyToView
     * @throws XMLStreamException
     */
    private void writeFlyToView(boolean flyToView) throws XMLStreamException{
        if (DEF_FLY_TO_VIEW != flyToView){
            writer.writeStartElement(URI_KML, TAG_FLY_TO_VIEW);
            if(flyToView){
                writer.writeCharacters(SimpleType.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleType.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param address
     * @throws XMLStreamException
     */
    private void writeAddress(String address) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_ADDRESS);
        writer.writeCharacters(address);
        writer.writeEndElement();
    }

    /**
     *
     * @param snippet
     * @throws XMLStreamException
     */
    private void writeSnippet(Object snippet) throws XMLStreamException{
        if(snippet instanceof String || snippet instanceof Cdata){
            writer.writeStartElement(URI_KML, TAG_SNIPPET);
            this.writeCharacterContent(snippet);
        }
        else if (snippet instanceof Snippet){
            writer.writeStartElement(URI_KML, TAG_SNIPPET_BIG);
            if(DEF_MAX_SNIPPET_LINES_ATT != ((Snippet) snippet).getMaxLines())
                writer.writeAttribute(ATT_MAX_LINES, String.valueOf(((Snippet) snippet).getMaxLines()));
            this.writeCharacterContent(((Snippet) snippet).getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param phoneNumber
     * @throws XMLStreamException
     */
    private void writePhoneNumber(String phoneNumber) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_PHONE_NUMBER);
        writer.writeCharacters(phoneNumber);
        writer.writeEndElement();
    }

    /**
     *
     * @param name
     * @throws XMLStreamException
     */
    private void writeName(String name) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_NAME);
        writer.writeCharacters(name);
        writer.writeEndElement();
    }

    /**
     *
     * @param description
     * @throws XMLStreamException
     */
    private void writeDescription(Object description) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_DESCRIPTION);
        this.writeCharacterContent(description);
        writer.writeEndElement();
    }

    /**
     *
     * @param href
     * @throws XMLStreamException
     */
    private void writeHref(String href) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_HREF);
        writer.writeCharacters(href);
        writer.writeEndElement();
    }

    /**
     *
     * @param text
     * @throws XMLStreamException
     */
    private void writeText(Object text) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_TEXT);
        this.writeCharacterContent(text);
        writer.writeEndElement();
    }

    /**
     *
     * @param uri
     * @throws XMLStreamException
     */
    private void writeStyleUrl(URI uri) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_STYLE_URL);
        writer.writeCharacters(uri.toString());
        writer.writeEndElement();
    }

    /**
     *
     * @param viewFormat
     * @throws XMLStreamException
     */
    private void writeViewFormat(String viewFormat) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_VIEW_FORMAT);
        writer.writeCharacters(viewFormat);
        writer.writeEndElement();
    }

    /**
     *
     * @param httpQuery
     * @throws XMLStreamException
     */
    private void writeHttpQuery(String httpQuery) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_HTTP_QUERY);
        writer.writeCharacters(httpQuery);
        writer.writeEndElement();
    }

    /**
     *
     * @param targetHref
     * @throws XMLStreamException
     */
    private void writeTargetHref(URI targetHref) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_TARGET_HREF);
        writer.writeCharacters(targetHref.toString());
        writer.writeEndElement();
    }

    /**
     *
     * @param sourceHref
     * @throws XMLStreamException
     */
    private void writeSourceHref(URI sourceHref) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_SOURCE_HREF);
        writer.writeCharacters(sourceHref.toString());
        writer.writeEndElement();
    }

    /**
     *
     * @param begin
     * @throws XMLStreamException
     */
    private void writeBegin(Calendar begin) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_BEGIN);
        this.writeCalendar(begin);
        writer.writeEndElement();
    }

    /**
     *
     * @param end
     * @throws XMLStreamException
     */
    private void writeEnd(Calendar end) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_END);
        this.writeCalendar(end);
        writer.writeEndElement();
    }

    /**
     *
     * @param when
     * @throws XMLStreamException
     */
    private void writeWhen(Calendar when) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_WHEN);
        this.writeCalendar(when);
        writer.writeEndElement();
    }

    /**
     *
     * @param calendar
     * @throws XMLStreamException
     */
    private void writeCalendar(Calendar calendar) throws XMLStreamException{
        writer.writeCharacters(KmlUtilities.getFormatedString(calendar, true));
    }

    /**
     *
     * @param displayName
     * @throws XMLStreamException
     */
    private void writeDisplayName(Object displayName) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_DISPLAY_NAME);
        this.writeCharacterContent(displayName);
        writer.writeEndElement();
    }

    /**
     *
     * @param value
     * @throws XMLStreamException
     */
    private void writeValue(String value) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_VALUE);
        writer.writeCharacters(value);
        writer.writeEndElement();
    }


    /**
     * 
     * @param cookie
     * @throws XMLStreamException
     */
    private void writeCookie(String cookie) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_COOKIE);
        writer.writeCharacters(cookie);
        writer.writeEndElement();
    }

    /**
     *
     * @param message
     * @throws XMLStreamException
     */
    private void writeMessage(String message) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_MESSAGE);
        writer.writeCharacters(message);
        writer.writeEndElement();
    }

    /**
     *
     * @param linkName
     * @throws XMLStreamException
     */
    private void writeLinkName(String linkName) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_LINK_NAME);
        writer.writeCharacters(linkName);
        writer.writeEndElement();
    }

    /**
     *
     * @param linkDescription
     * @throws XMLStreamException
     */
    private void writeLinkDescription(Object linkDescription) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_LINK_DESCRIPTION);
        this.writeCharacterContent(linkDescription);
        writer.writeEndElement();
    }

    /**
     *
     * @param expires
     * @throws XMLStreamException
     */
    private void writeExpires(Calendar expires) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_EXPIRES);
        this.writeCalendar(expires);
        writer.writeEndElement();
    }

    /**
     * 
     * @param linkSnippet
     * @throws XMLStreamException
     */
    private void writeLinkSnippet(Snippet linkSnippet) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_LINK_SNIPPET);
        if(DEF_MAX_SNIPPET_LINES_ATT != linkSnippet.getMaxLines())
            writer.writeAttribute(ATT_MAX_LINES, String.valueOf(linkSnippet.getMaxLines()));
        this.writeCharacterContent(linkSnippet.getContent());
        writer.writeEndElement();
    }

    /**
     * 
     * @param color
     * @throws XMLStreamException
     */
    private void writeColor(Color color) throws XMLStreamException{
        if (DEF_COLOR != color){
            writer.writeStartElement(URI_KML, TAG_COLOR);
            writer.writeCharacters(KmlUtilities.toKmlColor(color));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param color
     * @throws XMLStreamException
     */
    private void writeBgColor(Color color) throws XMLStreamException{
        if (DEF_BG_COLOR != color){
            writer.writeStartElement(URI_KML, TAG_BG_COLOR);
            writer.writeCharacters(KmlUtilities.toKmlColor(color));
            writer.writeEndElement();
        }
    }

    /**
     * 
     * @param color
     * @throws XMLStreamException
     */
    private void writeTextColor(Color color) throws XMLStreamException{
        if(DEF_TEXT_COLOR != color){
            writer.writeStartElement(URI_KML, TAG_TEXT_COLOR);
            writer.writeCharacters(KmlUtilities.toKmlColor(color));
            writer.writeEndElement();
        }
    }

    /**
     * 
     * @param colorMode
     * @throws XMLStreamException
     */
    private void writeColorMode(ColorMode colorMode) throws XMLStreamException{
        if(DEF_COLOR_MODE != colorMode){
            writer.writeStartElement(URI_KML, TAG_COLOR_MODE);
            writer.writeCharacters(colorMode.getColorMode());
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param altitudeMode
     * @throws XMLStreamException
     */
    private void writeAltitudeMode(AltitudeMode altitudeMode) throws XMLStreamException{
        if(DEF_ALTITUDE_MODE != altitudeMode){
            writer.writeStartElement(URI_KML, TAG_ALTITUDE_MODE);
            writer.writeCharacters(altitudeMode.getAltitudeMode());
            writer.writeEndElement();
        }
    }

    /**
     * 
     * @param displayMode
     * @throws XMLStreamException
     */
    private void writeDisplayMode(DisplayMode displayMode) throws XMLStreamException{
        if (DEF_DISPLAY_MODE != displayMode){
            writer.writeStartElement(URI_KML, TAG_ALTITUDE_MODE);
            writer.writeCharacters(displayMode.getDisplayMode());
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param styleState
     * @throws XMLStreamException
     */
    private void writeKey(StyleState styleState) throws XMLStreamException{
        if (DEF_STYLE_STATE != styleState){
            writer.writeStartElement(URI_KML, TAG_KEY);
            writer.writeCharacters(styleState.getStyleState());
            writer.writeEndElement();
        }
    }

    /**
     * 
     * @param refreshMode
     * @throws XMLStreamException
     */
    private void writeRefreshMode(RefreshMode refreshMode) throws XMLStreamException {
        if (DEF_REFRESH_MODE != refreshMode){
            writer.writeStartElement(URI_KML, TAG_REFRESH_MODE);
            writer.writeCharacters(refreshMode.getRefreshMode());
            writer.writeEndElement();
        }
    }

    /**
     * 
     * @param viewRefreshMode
     * @throws XMLStreamException
     */
    private void writeViewRefreshMode(ViewRefreshMode viewRefreshMode) throws XMLStreamException {
        if (DEF_VIEW_REFRESH_MODE != viewRefreshMode){
            writer.writeStartElement(URI_KML, TAG_VIEW_REFRESH_MODE);
            writer.writeCharacters(viewRefreshMode.getViewRefreshMode());
            writer.writeEndElement();
        }
    }

    /**
     * 
     * @param listItem
     * @throws XMLStreamException
     */
    private void writeListItem(ListItem listItem) throws XMLStreamException{
        if (DEF_LIST_ITEM != listItem){
            writer.writeStartElement(URI_KML, TAG_LIST_ITEM);
            writer.writeCharacters(listItem.getItem());
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param shape
     * @throws XMLStreamException
     */
    private void writeShape(Shape shape) throws XMLStreamException{
        if (DEF_SHAPE != shape){
            writer.writeStartElement(URI_KML, TAG_SHAPE);
            writer.writeCharacters(shape.getShape());
            writer.writeEndElement();
        }
    }

    /**
     * 
     * @param gridOrigin
     * @throws XMLStreamException
     */
    private void writeGridOrigin(GridOrigin gridOrigin) throws XMLStreamException{
        if (DEF_GRID_ORIGIN != gridOrigin){
            writer.writeStartElement(URI_KML, TAG_GRID_ORIGIN);
            writer.writeCharacters(gridOrigin.getGridOrigin());
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param scale
     * @throws XMLStreamException
     */
    private void writeScale(double scale) throws XMLStreamException{
        if(DEF_SCALE != scale){
            writer.writeStartElement(URI_KML, TAG_SCALE);
            writer.writeCharacters(Double.toString(scale));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param width
     * @throws XMLStreamException
     */
    private void writeWidth(double width) throws XMLStreamException{
        if(DEF_WIDTH != width){
            writer.writeStartElement(URI_KML, TAG_WIDTH);
            writer.writeCharacters(Double.toString(width));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param altitude
     * @throws XMLStreamException
     */
    private void writeAltitude(double altitude) throws XMLStreamException{
        if (DEF_ALTITUDE != altitude){
            writer.writeStartElement(URI_KML, TAG_ALTITUDE);
            writer.writeCharacters(Double.toString(altitude));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param range
     * @throws XMLStreamException
     */
    private void writeRange(double range) throws XMLStreamException{
        if (DEF_RANGE != range){
            writer.writeStartElement(URI_KML, TAG_RANGE);
            writer.writeCharacters(Double.toString(range));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param drawOrder
     * @throws XMLStreamException
     */
    private void writeDrawOrder(int drawOrder) throws XMLStreamException{
        if (DEF_DRAW_ORDER != drawOrder){
            writer.writeStartElement(URI_KML, TAG_DRAW_ORDER);
            writer.writeCharacters(Integer.toString(drawOrder));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param x
     * @throws XMLStreamException
     */
    private void writeX(double x) throws XMLStreamException{
        if (DEF_X != x){
            writer.writeStartElement(URI_KML, TAG_X);
            writer.writeCharacters(Double.toString(x));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param y
     * @throws XMLStreamException
     */
    private void writeY(double y) throws XMLStreamException{
        if (DEF_Y != y){
            writer.writeStartElement(URI_KML, TAG_Y);
            writer.writeCharacters(Double.toString(y));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param z
     * @throws XMLStreamException
     */
    private void writeZ(double z) throws XMLStreamException{
        if (DEF_Z != z){
            writer.writeStartElement(URI_KML, TAG_Z);
            writer.writeCharacters(Double.toString(z));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param minAltitude
     * @throws XMLStreamException
     */
    private void writeMinAltitude(double minAltitude) throws XMLStreamException{
        if(DEF_MIN_ALTITUDE != minAltitude){
            writer.writeStartElement(URI_KML, TAG_MIN_ALTITUDE);
            writer.writeCharacters(Double.toString(minAltitude));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param maxAltitude
     * @throws XMLStreamException
     */
    private void writeMaxAltitude(double maxAltitude) throws XMLStreamException{
        if (DEF_MAX_ALTITUDE != maxAltitude){
            writer.writeStartElement(URI_KML, TAG_MAX_ALTITUDE);
            writer.writeCharacters(Double.toString(maxAltitude));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param pixels
     * @throws XMLStreamException
     */
    private void writeMinLodPixels(double pixels) throws XMLStreamException{
        if (DEF_MIN_LOD_PIXELS != pixels){
            writer.writeStartElement(URI_KML, TAG_MIN_LOD_PIXELS);
            writer.writeCharacters(Double.toString(pixels));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param pixels
     * @throws XMLStreamException
     */
    private void writeMaxLodPixels(double pixels) throws XMLStreamException{
        if (DEF_MAX_LOD_PIXELS != pixels){
            writer.writeStartElement(URI_KML, TAG_MAX_LOD_PIXELS);
            writer.writeCharacters(Double.toString(pixels));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param fadeExtent
     * @throws XMLStreamException
     */
    private void writeMinFadeExtent(double fadeExtent) throws XMLStreamException{
        if (DEF_MIN_FADE_EXTENT != fadeExtent){
            writer.writeStartElement(URI_KML, TAG_MIN_FADE_EXTENT);
            writer.writeCharacters(Double.toString(fadeExtent));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param fadeExtent
     * @throws XMLStreamException
     */
    private void writeMaxFadeExtent(double fadeExtent) throws XMLStreamException{
        if (DEF_MAX_FADE_EXTENT != fadeExtent){
            writer.writeStartElement(URI_KML, TAG_MAX_FADE_EXTENT);
            writer.writeCharacters(Double.toString(fadeExtent));
            writer.writeEndElement();
        }
    }

    /**
     *
     *
     * @param refreshInterval
     * @throws XMLStreamException
     */
    private void writeRefreshInterval(double refreshInterval) throws XMLStreamException {
        if(DEF_REFRESH_INTERVAL != refreshInterval){
            writer.writeStartElement(URI_KML, TAG_REFRESH_INTERVAL);
            writer.writeCharacters(Double.toString(refreshInterval));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param viewRefreshTime
     * @throws XMLStreamException
     */
    private void writeViewRefreshTime(double viewRefreshTime) throws XMLStreamException {
        if(DEF_VIEW_REFRESH_TIME != viewRefreshTime){
            writer.writeStartElement(URI_KML, TAG_VIEW_REFRESH_TIME);
            writer.writeCharacters(Double.toString(viewRefreshTime));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param viewBoundScale
     * @throws XMLStreamException
     */
    private void writeViewBoundScale(double viewBoundScale) throws XMLStreamException {
        if (DEF_VIEW_BOUND_SCALE != viewBoundScale){
            writer.writeStartElement(URI_KML, TAG_VIEW_BOUND_SCALE);
            writer.writeCharacters(Double.toString(viewBoundScale));
            writer.writeEndElement();
        }
    }

    /**
     * 
     * @param near
     * @throws XMLStreamException
     */
    private void writeNear(double near) throws XMLStreamException {
        if (DEF_NEAR != near){
            writer.writeStartElement(URI_KML, TAG_NEAR);
            writer.writeCharacters(Double.toString(near));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param minRefreshPeriod
     * @throws XMLStreamException
     */
    private void writeMinRefreshPeriod(double minRefreshPeriod) throws XMLStreamException {
        if (DEF_MIN_REFRESH_PERIOD != minRefreshPeriod){
            writer.writeStartElement(URI_KML, TAG_MIN_REFRESH_PERIOD);
            writer.writeCharacters(Double.toString(minRefreshPeriod));
            writer.writeEndElement();
        }
    }

    /**
     * 
     * @param maxSessionLength
     * @throws XMLStreamException
     */
    private void writeMaxSessionLength(double maxSessionLength) throws XMLStreamException {
        if (DEF_MAX_SESSION_LENGTH != maxSessionLength){
            writer.writeStartElement(URI_KML, TAG_MAX_SESSION_LENGTH);
            writer.writeCharacters(Double.toString(maxSessionLength));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param titleSize
     * @throws XMLStreamException
     */
    private void writeTitleSize(int titleSize) throws XMLStreamException{
        if (DEF_TITLE_SIZE != titleSize){
            writer.writeStartElement(URI_KML, TAG_TITLE_SIZE);
            writer.writeCharacters(Integer.toString(titleSize));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param maxWidth
     * @throws XMLStreamException
     */
    private void writeMaxWidth(int maxWidth) throws XMLStreamException{
        if (DEF_MAX_WIDTH != maxWidth){
            writer.writeStartElement(URI_KML, TAG_MAX_WIDTH);
            writer.writeCharacters(Integer.toString(maxWidth));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param maxHeight
     * @throws XMLStreamException
     */
    private void writeMaxHeight(int maxHeight) throws XMLStreamException{
        if (DEF_MAX_HEIGHT != maxHeight){
            writer.writeStartElement(URI_KML, TAG_MAX_HEIGHT);
            writer.writeCharacters(Integer.toString(maxHeight));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param msl
     * @throws XMLStreamException
     */
    private void writeMaxSnippetLines(int msl) throws XMLStreamException{
        if (DEF_MAX_SNIPPET_LINES != msl){
            writer.writeStartElement(URI_KML, TAG_MAX_SNIPPET_LINES);
            writer.writeCharacters(Integer.toString(msl));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param heading
     * @throws XMLStreamException
     */
    private void writeHeading(double heading) throws XMLStreamException{
        if (DEF_HEADING != heading){
            writer.writeStartElement(URI_KML, TAG_HEADING);
            writer.writeCharacters(Double.toString(heading));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param bottomFov
     * @throws XMLStreamException
     */
    private void writeBottomFov(double bottomFov) throws XMLStreamException{
        if (DEF_BOTTOM_FOV != bottomFov){
            writer.writeStartElement(URI_KML, TAG_BOTTOM_FOV);
            writer.writeCharacters(Double.toString(bottomFov));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param topFov
     * @throws XMLStreamException
     */
    private void writeTopFov(double topFov) throws XMLStreamException{
        if (DEF_TOP_FOV != topFov){
            writer.writeStartElement(URI_KML, TAG_TOP_FOV);
            writer.writeCharacters(Double.toString(topFov));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param leftFov
     * @throws XMLStreamException
     */
    private void writeLeftFov(double leftFov) throws XMLStreamException{
        if (DEF_LEFT_FOV != leftFov){
            writer.writeStartElement(URI_KML, TAG_LEFT_FOV);
            writer.writeCharacters(Double.toString(leftFov));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param rightFov
     * @throws XMLStreamException
     */
    private void writeRightFov(double rightFov) throws XMLStreamException{
        if (DEF_RIGHT_FOV != rightFov){
            writer.writeStartElement(URI_KML, TAG_RIGHT_FOV);
            writer.writeCharacters(Double.toString(rightFov));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param longitude
     * @throws XMLStreamException
     */
    private void writeLongitude(double longitude) throws XMLStreamException{
        if (DEF_LONGITUDE != longitude){
            writer.writeStartElement(URI_KML, TAG_LONGITUDE);
            writer.writeCharacters(Double.toString(longitude));
            writer.writeEndElement();
        }
    }

    /**
     * This method writes a latitude angle.
     * @param latitude The latitude cookie.
     * @throws XMLStreamException
     */
    private void writeLatitude(double latitude) throws XMLStreamException{
        if (DEF_LATITUDE != latitude){
            writer.writeStartElement(URI_KML, TAG_LATITUDE);
            writer.writeCharacters(Double.toString(latitude));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param tilt
     * @throws XMLStreamException
     */
    private void writeTilt(double tilt) throws XMLStreamException{
        if (DEF_TILT != tilt){
            writer.writeStartElement(URI_KML, TAG_TILT);
            writer.writeCharacters(Double.toString(KmlUtilities.checkAnglePos180(tilt)));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param rotation
     * @throws XMLStreamException
     */
    private void writeRotation(double rotation) throws XMLStreamException{
        if (DEF_ROTATION != rotation){
            writer.writeStartElement(URI_KML, TAG_ROTATION);
            writer.writeCharacters(Double.toString(rotation));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param north
     * @throws XMLStreamException
     */
    private void writeNorth(double north) throws XMLStreamException{
        if (DEF_NORTH != north){
            writer.writeStartElement(URI_KML, TAG_NORTH);
            writer.writeCharacters(Double.toString(north));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param south
     * @throws XMLStreamException
     */
    private void writeSouth(double south) throws XMLStreamException{
        if (DEF_SOUTH != south){
            writer.writeStartElement(URI_KML, TAG_SOUTH);
            writer.writeCharacters(Double.toString(south));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param east
     * @throws XMLStreamException
     */
    private void writeEast(double east) throws XMLStreamException{
        if (DEF_EAST != east){
            writer.writeStartElement(URI_KML, TAG_EAST);
            writer.writeCharacters(Double.toString(east));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param west
     * @throws XMLStreamException
     */
    private void writeWest(double west) throws XMLStreamException{
        if (DEF_WEST != west){
            writer.writeStartElement(URI_KML, TAG_WEST);
            writer.writeCharacters(Double.toString(west));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param roll
     * @throws XMLStreamException
     */
    private void writeRoll(double roll) throws XMLStreamException{
        if(DEF_ROLL != roll){
            writer.writeStartElement(URI_KML, TAG_ROLL);
            writer.writeCharacters(Double.toString(roll));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param vec2
     * @throws XMLStreamException
     */
    private void writeVec2(Vec2 vec2) throws XMLStreamException{
        if (isFiniteNumber(vec2.getX()) && DEF_VEC2_X != vec2.getX()){
            writer.writeAttribute(ATT_X, Double.toString(vec2.getX()));
        }
        if (isFiniteNumber(vec2.getY()) && DEF_VEC2_Y != vec2.getY()){
            writer.writeAttribute(ATT_Y, Double.toString(vec2.getY()));
        }
        if (vec2.getXUnits() != null && !DEF_VEC2_XUNIT.equals(vec2.getXUnits())){
            writer.writeAttribute(ATT_XUNITS, vec2.getXUnits().getUnit());
        }
        if (vec2.getYUnits() != null && !DEF_VEC2_YUNIT.equals(vec2.getYUnits())){
            writer.writeAttribute(ATT_YUNITS, vec2.getYUnits().getUnit());
        }
    }

    /**
     *
     * @param hotspot
     * @throws XMLStreamException
     */
    private void writeHotSpot(Vec2 hotspot) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_HOT_SPOT);
        this.writeVec2(hotspot);
        writer.writeEndElement();
    }

    /**
     *
     * @param overlayXY
     * @throws XMLStreamException
     */
    private void writeOverlayXY(Vec2 overlayXY) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_OVERLAY_XY);
        this.writeVec2(overlayXY);
        writer.writeEndElement();
    }

    /**
     *
     * @param screenXY
     * @throws XMLStreamException
     */
    private void writeScreenXY(Vec2 screenXY) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_SCREEN_XY);
        this.writeVec2(screenXY);
        writer.writeEndElement();
    }

    /**
     *
     * @param rotationXY
     * @throws XMLStreamException
     */
    private void writeRotationXY(Vec2 rotationXY) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_ROTATION_XY);
        this.writeVec2(rotationXY);
        writer.writeEndElement();
    }

    /**
     *
     * @param size
     * @throws XMLStreamException
     */
    private void writeSize(Vec2 size) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_SIZE);
        this.writeVec2(size);
        writer.writeEndElement();
    }
    
    private void writeSimpleExtensions(List<SimpleType> simpleExtensions){

    }

    private void writeObjectExtensions(List<AbstractObject> objectExtensions){

    }

    /**
     * <p>This method whrites character content as
     * CDATA if input String contains "&lt;" character.
     * Following KML elements mays contains CDATA : </p>
     * <ul>
     * <li>snippet,</p>
     * <li>description,</li>
     * <li>text,</li>
     * <li>linkDescription,</li>
     * <li>linkSnippet.</li>
     * </ul>
     *
     * @param string
     * @throws XMLStreamException
     */
    private void writeCharacterContent(Object string) throws XMLStreamException{
        if(string instanceof Cdata)
            writer.writeCData(string.toString());
        else if (string instanceof String)
            writer.writeCharacters((String) string);
        else
            throw new IllegalArgumentException("Only String or CDATA argument.");
    }
    
    /*
     * METHODES UTILITAIRES
     */
    private static boolean isFiniteNumber(double d){
        return !(Double.isInfinite(d) && Double.isNaN(d));
    }

    /**
     *
     * @param version
     * @throws KmlException
     */
    private void checkVersion(String version) throws KmlException{
            if(this.URI_KML.equals(version))
                return;
        throw new KmlException("Kml writer error : Element not allowed by "+this.URI_KML+" namespace.");
    }

    /**
     * 
     * @param version
     * @return
     */
    private boolean checkVersionSimple(String version){
        return this.URI_KML.equals(version);
    }

}

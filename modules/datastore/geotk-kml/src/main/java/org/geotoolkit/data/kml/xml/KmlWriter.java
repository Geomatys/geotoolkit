package org.geotoolkit.data.kml.xml;

import org.geotoolkit.data.xal.xml.XalWriter;
import org.geotoolkit.data.atom.xml.AtomWriter;
import java.awt.Color;
import java.io.IOException;
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
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.model.Vec2;
import org.geotoolkit.data.kml.model.ViewRefreshMode;
import org.geotoolkit.data.kml.model.ViewVolume;
import org.geotoolkit.data.xal.model.AddressDetails;
import org.geotoolkit.data.xal.model.XalException;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.geotoolkit.xml.StaxStreamWriter;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 * <p>This class provides a method to read KML files, version 2.2.</p>
 *
 * @author Samuel Andrés
 */
public class KmlWriter extends StaxStreamWriter {

    private final XalWriter xalWriter = new XalWriter();
    private final AtomWriter atomWriter = new AtomWriter();

    public KmlWriter(){
        super();
    }

    @Override
    public void setOutput(Object output) throws XMLStreamException, IOException{
        super.setOutput(output);
        this.xalWriter.setOutput(writer);
        this.atomWriter.setOutput(writer);
        this.writer.setPrefix(PREFIX_XAL, URI_XAL);
        this.writer.setPrefix(PREFIX_ATOM, URI_ATOM);
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
            writer.setDefaultNamespace(URI_KML);
            writer.writeStartElement(URI_KML,TAG_KML);
            //writer.writeDefaultNamespace(URI_KML);
            writer.writeNamespace(PREFIX_ATOM, URI_ATOM);
            writer.writeNamespace(PREFIX_XAL, URI_XAL);
            /*streamWriter.writeNamespace(PREFIX_XSI, URI_XSI);
            streamWriter.writeAttribute(URI_XSI,
                    "schemaLocation",
                    URI_KML+" C:/Users/w7mainuser/Documents/OGC_SCHEMAS/sld/1.1.0/StyledLayerDescriptor.xsd");
            streamWriter.writeAttribute("version", "0");*/
            this.writeKml(kml);
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();

        } catch (XMLStreamException ex) {
            Logger.getLogger(KmlWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param kml The Kml object to write.
     * @throws XMLStreamException
     */
    private void writeKml(Kml kml) throws XMLStreamException{
        if (kml.getNetworkLinkControl() != null){
            this.writeNetworkLinkControl(kml.getNetworkLinkControl());
        }
        if (kml.getAbstractFeature() != null){
            this.writeAbstractFeature(kml.getAbstractFeature());
        }
        if (kml.getKmlSimpleExtensions() != null){
        }
        if (kml.getKmlObjectExtensions() != null){
        }
    }

    /**
     *
     * @param networkLinkControl
     * @throws XMLStreamException
     */
    private void writeNetworkLinkControl(NetworkLinkControl networkLinkControl) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_NETWORK_LINK_CONTROL);
        if (isFiniteNumber(networkLinkControl.getMinRefreshPeriod())){
            this.writeMinRefreshPeriod(networkLinkControl.getMinRefreshPeriod());
        }
        if (isFiniteNumber(networkLinkControl.getMaxSessionLength())){
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
        if (networkLinkControl.getLinkDescription() != null){
            this.writeSimpleExtensions(networkLinkControl.getNetworkLinkControlSimpleExtensions());
        }
        if (networkLinkControl.getLinkDescription() != null){
            this.writeObjectExtensions(networkLinkControl.getNetworkLinkControlObjectExtensions());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param update
     * @throws XMLStreamException
     */
    private void writeUpdate(Update update) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_UPDATE);

        if(update.getCreates() != null){
            for (Create create : update.getCreates()){
                this.writeCreate(create);
            }
        }
        if(update.getDeletes() != null){
            for (Delete delete : update.getDeletes()){
                this.writeDelete(delete);
            }
        }
        if(update.getChanges() != null){
            for (Change change : update.getChanges()){
                this.writeChange(change);
            }
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param create
     * @throws XMLStreamException
     */
    private void writeCreate(Create create) throws XMLStreamException{
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
    private void writeDelete(Delete delete) throws XMLStreamException{
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
    private void writeChange(Change change) throws XMLStreamException{
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
    private void writeAbstractObject(AbstractObject object) throws XMLStreamException{
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
        if (abstractObject.getObjectSimpleExtensions() != null){
            this.writeSimpleExtensions(abstractObject.getObjectSimpleExtensions());
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
    private void writeAbstractFeature(AbstractFeature abstractFeature) throws XMLStreamException{
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
    private void writeNetworkLink(NetworkLink networkLink) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_NETWORK_LINK);
        this.writeRefreshVisibility(networkLink.getRefreshVisibility());
        this.writeFlyToView(networkLink.getFlyToView());
        if (networkLink.getLink() != null){
            this.writeLink(networkLink.getLink());
        }
        if (networkLink.getNetworkLinkSimpleExtensions() != null){
            this.writeSimpleExtensions(networkLink.getNetworkLinkSimpleExtensions());
        }
        if (networkLink.getNetworkLinkObjectExtensions() != null){
            this.writeObjectExtensions(networkLink.getNetworkLinkObjectExtensions());
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
    private void writeCommonAbstractFeature(AbstractFeature abstractFeature) throws XMLStreamException{
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
        if (abstractFeature.getStyleSelectors() != null){
            for(AbstractStyleSelector abstractStyleSelector : abstractFeature.getStyleSelectors()){
                this.writeAbstractStyleSelector(abstractStyleSelector);
            }
        }
        if (abstractFeature.getRegion() != null){
            this.writeRegion(abstractFeature.getRegion());
        }
        if (abstractFeature.getExtendedData() != null){
            this.writeExtendedData(abstractFeature.getExtendedData());
        }
        if (abstractFeature.getAbstractFeatureSimpleExtensions() != null){
            this.writeSimpleExtensions(abstractFeature.getAbstractFeatureSimpleExtensions());
        }
        if (abstractFeature.getAbstractFeatureObjectExtensions() != null){
            this.writeObjectExtensions(abstractFeature.getAbstractFeatureObjectExtensions());
        }
    }

    /**
     * 
     * @param extendedData
     * @throws XMLStreamException
     */
    private void writeExtendedData(ExtendedData extendedData) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_EXTENDED_DATA);
        if (extendedData.getDatas() != null){
            for (Data data : extendedData.getDatas()){
                this.writeData(data);
            }
        }
        if (extendedData.getSchemaData() != null){
            for (SchemaData schemaData : extendedData.getSchemaData()){
                this.writeSchemaData(schemaData);
            }
        }
        if (extendedData.getAnyOtherElements() != null){

        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param schemaData
     * @throws XMLStreamException
     */
    private void writeSchemaData(SchemaData schemaData) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_SCHEMA_DATA);
        this.writeCommonAbstractObject(schemaData);
        if (schemaData.getSimpleDatas() != null){
            for (SimpleData simpleData : schemaData.getSimpleDatas()){
                this.writeSimpleData(simpleData);
            }
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
        writer.writeEmptyElement(URI_KML, TAG_TIME_SPAN);
        this.writeCommonAbstractTimePrimitive(timeSpan);
        if (timeSpan.getBegin() != null){
            this.writeBegin(timeSpan.getBegin());
        }
        if (timeSpan.getEnd() != null){
            this.writeEnd(timeSpan.getEnd());
        }
        if (timeSpan.getTimeSpanSimpleExtensions() != null){
            this.writeSimpleExtensions(timeSpan.getTimeSpanSimpleExtensions());
        }
        if (timeSpan.getTimeSpanObjectExtensions() != null){
            this.writeObjectExtensions(timeSpan.getTimeSpanObjectExtensions());
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param timeStamp
     * @throws XMLStreamException
     */
    private void writeTimeStamp(TimeStamp timeStamp) throws XMLStreamException{
        writer.writeEmptyElement(URI_KML, TAG_TIME_STAMP);
        this.writeCommonAbstractTimePrimitive(timeStamp);
        if (timeStamp.getWhen() != null){
            this.writeWhen(timeStamp.getWhen());
        }
        if (timeStamp.getTimeStampSimpleExtensions() != null){
            this.writeSimpleExtensions(timeStamp.getTimeStampSimpleExtensions());
        }
        if (timeStamp.getTimeStampObjectExtensions() != null){
            this.writeObjectExtensions(timeStamp.getTimeStampObjectExtensions());
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
        if (lookAt.getLookAtSimpleExtensions() != null){
            this.writeSimpleExtensions(lookAt.getLookAtSimpleExtensions());
        }
        if (lookAt.getLookAtObjectExtensions() != null){
            this.writeObjectExtensions(lookAt.getLookAtObjectExtensions());
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
        if (camera.getCameraSimpleExtensions() != null){
            this.writeSimpleExtensions(camera.getCameraSimpleExtensions());
        }
        if (camera.getCameraObjectExtensions() != null){
            this.writeObjectExtensions(camera.getCameraObjectExtensions());
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
        if (abstractView.getAbstractViewSimpleExtensions() != null){
            this.writeSimpleExtensions(abstractView.getAbstractViewSimpleExtensions());
        }
        if (abstractView.getAbstractViewObjectExtensions() != null){
            this.writeObjectExtensions(abstractView.getAbstractViewObjectExtensions());
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
        if (abstractTimePrimitive.getAbstractTimePrimitiveSimpleExtensions() != null){
            this.writeSimpleExtensions(abstractTimePrimitive.getAbstractTimePrimitiveSimpleExtensions());
        }
        if (abstractTimePrimitive.getAbstractTimePrimitiveObjectExtensions() != null){
            this.writeObjectExtensions(abstractTimePrimitive.getAbstractTimePrimitiveObjectExtensions());
        }
    }

    /**
     *
     * @param abstractStyleSelector The AbstractStyleSelector to write.
     * @throws XMLStreamException
     */
    private void writeAbstractStyleSelector(AbstractStyleSelector abstractStyleSelector) throws XMLStreamException{
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
        if (abstractStyleSelector.getAbstractStyleSelectorSimpleExtensions() != null){
            this.writeSimpleExtensions(abstractStyleSelector.getAbstractStyleSelectorSimpleExtensions());
        }
        if (abstractStyleSelector.getAbstractStyleSelectorObjectExtensions() != null){
            this.writeObjectExtensions(abstractStyleSelector.getAbstractStyleSelectorObjectExtensions());
        }
    }

    /**
     * 
     * @param styleMap
     * @throws XMLStreamException
     */
    private void writeStyleMap(StyleMap styleMap) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_STYLE_MAP);
        this.writeCommonAbstractStyleSelector(styleMap);
        if (styleMap.getPairs() != null){
            for(Pair pair : styleMap.getPairs()){
                this.writePair(pair);
            }
        }
        if (styleMap.getStyleMapSimpleExtensions() != null){
            this.writeSimpleExtensions(styleMap.getStyleMapSimpleExtensions());
        }
        if (styleMap.getStyleMapObjectExtensions() != null){
            this.writeObjectExtensions(styleMap.getStyleMapObjectExtensions());
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param pair
     * @throws XMLStreamException
     */
    private void writePair(Pair pair) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_PAIR);
        this.writeCommonAbstractObject(pair);
        if (pair.getKey() != null){
            this.writeKey(pair.getKey());
        }
        if (pair.getStyleUrl() != null){
            this.writeStyleUrl(pair.getStyleUrl());
        }
        if (pair.getAbstractStyleSelector() != null){
            this.writeAbstractStyleSelector(pair.getAbstractStyleSelector());
        }
        if (pair.getPairSimpleExtensions() != null){
            this.writeSimpleExtensions(pair.getPairSimpleExtensions());
        }
        if (pair.getPairObjectExtensions() != null){
            this.writeObjectExtensions(pair.getPairObjectExtensions());
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
        if (style.getStyleSimpleExtensions() != null){
            this.writeSimpleExtensions(style.getStyleSimpleExtensions());
        }
        if (style.getStyleObjectExtensions() != null){
            this.writeObjectExtensions(style.getStyleObjectExtensions());
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
        if (iconStyle.getIconStyleSimpleExtensions() != null){
            this.writeSimpleExtensions(iconStyle.getIconStyleSimpleExtensions());
        }
        if (iconStyle.getIconStyleObjectExtensions() != null){
            this.writeObjectExtensions(iconStyle.getIconStyleObjectExtensions());
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
        if (labelStyle.getLabelStyleSimpleExtensions() != null){
            this.writeSimpleExtensions(labelStyle.getLabelStyleSimpleExtensions());
        }
        if (labelStyle.getLabelStyleObjectExtensions() != null){
            this.writeObjectExtensions(labelStyle.getLabelStyleObjectExtensions());
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
        if (lineStyle.getLineStyleSimpleExtensions() != null){
            this.writeSimpleExtensions(lineStyle.getLineStyleSimpleExtensions());
        }
        if (lineStyle.getLineStyleObjectExtensions() != null){
            this.writeObjectExtensions(lineStyle.getLineStyleObjectExtensions());
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
        if (polyStyle.getPolyStyleSimpleExtensions() != null){
            this.writeSimpleExtensions(polyStyle.getPolyStyleSimpleExtensions());
        }
        if (polyStyle.getPolyStyleObjectExtensions() != null){
            this.writeObjectExtensions(polyStyle.getPolyStyleObjectExtensions());
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
        if (balloonStyle.getDisplayMode() != null){
            this.writeDisplayMode(balloonStyle.getDisplayMode());
        }
        if (balloonStyle.getBalloonStyleSimpleExtensions() != null){
            this.writeSimpleExtensions(balloonStyle.getBalloonStyleSimpleExtensions());
        }
        if (balloonStyle.getBalloonStyleObjectExtensions() != null){
            this.writeObjectExtensions(balloonStyle.getBalloonStyleObjectExtensions());
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
        if (listStyle.getItemIcons() != null){
            for(ItemIcon itemIcon : listStyle.getItemIcons()){
                this.writeItemIcon(itemIcon);
            }
        }
        if (isFiniteNumber(listStyle.getMaxSnippetLines())){
            this.writeMaxSnippetLines(listStyle.getMaxSnippetLines());
        }
        if (listStyle.getListStyleSimpleExtensions() != null){
            this.writeSimpleExtensions(listStyle.getListStyleSimpleExtensions());
        }
        if (listStyle.getListStyleObjectExtensions() != null){
            this.writeObjectExtensions(listStyle.getListStyleObjectExtensions());
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
        if (itemIcon.getItemIconSimpleExtensions() != null){
            this.writeSimpleExtensions(itemIcon.getItemIconSimpleExtensions());
        }
        if (itemIcon.getItemIconObjectExtensions() != null){
            this.writeObjectExtensions(itemIcon.getItemIconObjectExtensions());
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
        if (icon.getObjectSimpleExtensions() != null){
            this.writeSimpleExtensions(icon.getObjectSimpleExtensions());
        }
        if (icon.getHref() != null){
            this.writeHref(icon.getHref());
        }
        if (icon.getBasicLinkSimpleExtensions() != null){
            this.writeSimpleExtensions(icon.getBasicLinkSimpleExtensions());
        }
        if (icon.getBasicLinkObjectExtensions() != null){
            this.writeObjectExtensions(icon.getBasicLinkObjectExtensions());
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
        if (link.getBasicLinkSimpleExtensions() != null){
            this.writeSimpleExtensions(link.getBasicLinkSimpleExtensions());
        }
        if (link.getBasicLinkObjectExtensions() != null){
            this.writeObjectExtensions(link.getBasicLinkObjectExtensions());
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
        if (link.getLinkSimpleExtensions() != null){
            this.writeSimpleExtensions(link.getLinkSimpleExtensions());
        }
        if (link.getLinkObjectExtensions() != null){
            this.writeObjectExtensions(link.getLinkObjectExtensions());
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
        if (abstractColorStyle.getColorStyleSimpleExtensions() != null){
            this.writeSimpleExtensions(abstractColorStyle.getColorStyleSimpleExtensions());
        }
        if (abstractColorStyle.getColorStyleObjectExtensions() != null){
            this.writeObjectExtensions(abstractColorStyle.getColorStyleObjectExtensions());
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
        if (abstractSubStyle.getSubStyleSimpleExtensions() != null){
            this.writeSimpleExtensions(abstractSubStyle.getSubStyleSimpleExtensions());
        }
        if (abstractSubStyle.getSubStyleObjectExtensions() != null){
            this.writeObjectExtensions(abstractSubStyle.getSubStyleObjectExtensions());
        }
    }

    /**
     * 
     * @param placemark
     * @throws XMLStreamException
     */
    private void writePlacemark(Placemark placemark) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_PLACEMARK);
        this.writeCommonAbstractFeature(placemark);
        if (placemark.getAbstractGeometry() != null){
            this.writeAbstractGeometry(placemark.getAbstractGeometry());
        }
        if (placemark.getPlacemarkSimpleExtensions() != null){
            this.writeSimpleExtensions(placemark.getPlacemarkSimpleExtensions());
        }
        if (placemark.getPlacemarkObjectExtensions() != null){
            this.writeObjectExtensions(placemark.getPlacemarkObjectExtensions());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param abstractContainer The AbstractContainer to write
     * @throws XMLStreamException
     */
    private void writeAbstractContainer(AbstractContainer abstractContainer) throws XMLStreamException{
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
    private void writeAbstractOverlay(AbstractOverlay abstractOverlay) throws XMLStreamException{
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
    private void writePhotoOverlay(PhotoOverlay photoOverlay) throws XMLStreamException{
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
        if (photoOverlay.getPhotoOverlaySimpleExtensions() != null){
            this.writeSimpleExtensions(photoOverlay.getPhotoOverlaySimpleExtensions());
        }
        if (photoOverlay.getPhotoOverlayObjectExtensions() != null){
            this.writeObjectExtensions(photoOverlay.getPhotoOverlayObjectExtensions());
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
    private void writeScreenOverlay(ScreenOverlay screenOverlay) throws XMLStreamException{
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
        if (screenOverlay.getScreenOverlaySimpleExtensions() != null){
            this.writeSimpleExtensions(screenOverlay.getScreenOverlaySimpleExtensions());
        }
        if (screenOverlay.getScreenOverlayObjectExtensions() != null){
            this.writeObjectExtensions(screenOverlay.getScreenOverlayObjectExtensions());
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param groundOverlay
     * @throws XMLStreamException
     */
    private void writeGroundOverlay(GroundOverlay groundOverlay) throws XMLStreamException{
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
        if (groundOverlay.getGroundOverlaySimpleExtensions() != null){
            this.writeSimpleExtensions(groundOverlay.getGroundOverlaySimpleExtensions());
        }
        if (groundOverlay.getGroundOverlayObjectExtensions() != null){
            this.writeObjectExtensions(groundOverlay.getGroundOverlayObjectExtensions());
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
        if (latLonBox.getLatLonBoxSimpleExtensions() != null){
            this.writeSimpleExtensions(latLonBox.getLatLonBoxSimpleExtensions());
        }
        if (latLonBox.getLatLonBoxObjectExtensions() != null){
            this.writeObjectExtensions(latLonBox.getLatLonBoxObjectExtensions());
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
        if (abstractLatLonBox.getAbstractLatLonBoxSimpleExtensions() != null){
            this.writeSimpleExtensions(abstractLatLonBox.getAbstractLatLonBoxSimpleExtensions());
        }
        if (abstractLatLonBox.getAbstractLatLonBoxObjectExtensions() != null){
            this.writeObjectExtensions(abstractLatLonBox.getAbstractLatLonBoxObjectExtensions());
        }
    }
    
    private void writeCommonAbstractOverlay(AbstractOverlay abstractOverlay) throws XMLStreamException{
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
        if (abstractOverlay.getAbstractOverlaySimpleExtensions() != null){
            this.writeSimpleExtensions(abstractOverlay.getAbstractOverlaySimpleExtensions());
        }
        if (abstractOverlay.getAbstractOverlayObjectExtensions() != null){
            this.writeObjectExtensions(abstractOverlay.getAbstractOverlayObjectExtensions());
        }
    }

    /**
     * <p>This method writes tha common fields for
     * AbstractContainer instances.</p>
     *
     * @param abstractContainer The AbstractContainer to write.
     * @throws XMLStreamException
     */
    private void writeCommonAbstractContainer(AbstractContainer abstractContainer) throws XMLStreamException{
        this.writeCommonAbstractFeature(abstractContainer);
        if (abstractContainer.getAbstractContainerSimpleExtensions() != null){
            this.writeSimpleExtensions(abstractContainer.getAbstractContainerSimpleExtensions());
        }
        if (abstractContainer.getAbstractContainerObjectExtensions() != null){
            this.writeObjectExtensions(abstractContainer.getAbstractContainerObjectExtensions());
        }
    }

    /**
     *
     * @param folder
     * @throws XMLStreamException
     */
    private void writeFolder(Folder folder) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_FOLDER);
        this.writeCommonAbstractContainer(folder);

        if (folder.getAbstractFeatures() != null){
            for(AbstractFeature abstractFeature : folder.getAbstractFeatures()){
                this.writeAbstractFeature(abstractFeature);
            }
        }
        if (folder.getFolderSimpleExtensions() != null){
            this.writeSimpleExtensions(folder.getFolderSimpleExtensions());
        }
        if (folder.getFolderObjectExtensions() != null){
            this.writeObjectExtensions(folder.getFolderObjectExtensions());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param document
     * @throws XMLStreamException
     */
    private void writeDocument(Document document) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_DOCUMENT);
        this.writeCommonAbstractContainer(document);
        if (document.getSchemas() != null){
            for(Schema schema : document.getSchemas()){
                this.writeSchema(schema);
            }
        }
        if (document.getAbstractFeatures() != null){
            for(AbstractFeature abstractFeature : document.getAbstractFeatures()){
                this.writeAbstractFeature(abstractFeature);
            }
        }
        if (document.getDocumentSimpleExtensions() != null){
            this.writeSimpleExtensions(document.getDocumentSimpleExtensions());
        }
        if (document.getDocumentObjectExtensions() != null){
            this.writeObjectExtensions(document.getDocumentObjectExtensions());
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
        if (schema.getSimpleFields() != null){
            for (SimpleField sf : schema.getSimpleFields()){
                this.writeSimpleField(sf);
            }
        }
        if (schema.getName() != null){
            writer.writeAttribute(ATT_NAME, schema.getName());
        }
        if (schema.getId() != null){
            writer.writeAttribute(ATT_ID, schema.getId());
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
        if (simpleField.getDisplayName() != null){
            this.writeDisplayName(simpleField.getDisplayName());
        }
        if (simpleField.getType() != null){
            writer.writeAttribute(ATT_TYPE, simpleField.getType());
        }
        if (simpleField.getName() != null){
            writer.writeAttribute(ATT_ID, simpleField.getName());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param abstractGeometry
     * @throws XMLStreamException
     */
    private void writeAbstractGeometry(AbstractGeometry abstractGeometry) throws XMLStreamException{
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
        if (abstractGeometry.getAbstractGeometrySimpleExtensions() != null){
            this.writeSimpleExtensions(abstractGeometry.getAbstractGeometrySimpleExtensions());
        }
        if (abstractGeometry.getAbstractGeometryObjectExtensions() != null){
            this.writeObjectExtensions(abstractGeometry.getAbstractGeometryObjectExtensions());
        }
    }

    /**
     * 
     * @param model
     * @throws XMLStreamException
     */
    private void writeModel(Model model) throws XMLStreamException{
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
            this.writeResourceMap(model.getRessourceMap());
        }
        if (model.getModelSimpleExtensions() != null){
            this.writeSimpleExtensions(model.getModelSimpleExtensions());
        }
        if (model.getModelObjectExtensions() != null){
            this.writeObjectExtensions(model.getModelObjectExtensions());
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
        writer.writeStartElement(URI_KML, TAG_SCALE);
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
        if (resourceMap.getAliases() != null){
            for (Alias alias : resourceMap.getAliases()){
                this.writeAlias(alias);
            }
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
        if (polygon.getOuterBoundaryIs() != null){
            this.writeOuterBoundaryIs(polygon.getOuterBoundaryIs());
        }
        if (polygon.getInnerBoundariesAre() != null){
            for(Boundary innerBoundaryIs : polygon.getInnerBoundariesAre()){
                this.writeInnerBoundaryIs(innerBoundaryIs);
            }
        }
        if (polygon.getPolygonSimpleExtensions() != null){
            this.writeSimpleExtensions(polygon.getPolygonSimpleExtensions());
        }
        if (polygon.getPolygonObjectExtensions() != null){
            this.writeObjectExtensions(polygon.getPolygonObjectExtensions());
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
        if (boundary.getBoundarySimpleExtensions() != null){
            this.writeSimpleExtensions(boundary.getBoundarySimpleExtensions());
        }
        if (boundary.getBoundaryObjectExtensions() != null){
            this.writeObjectExtensions(boundary.getBoundaryObjectExtensions());
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
        if (lineString.getCoordinates() != null){
            this.writeCoordinates(lineString.getCoordinates());
        }
        if (lineString.getLineStringSimpleExtensions() != null){
            this.writeSimpleExtensions(lineString.getLineStringSimpleExtensions());
        }
        if (lineString.getLineStringObjectExtensions() != null){
            this.writeObjectExtensions(lineString.getLineStringObjectExtensions());
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
        if (linearRing.getCoordinates() != null){
            this.writeCoordinates(linearRing.getCoordinates());
        }
        if (linearRing.getLinearRingSimpleExtensions() != null){
            this.writeSimpleExtensions(linearRing.getLinearRingSimpleExtensions());
        }
        if (linearRing.getLinearRingObjectExtensions() != null){
            this.writeObjectExtensions(linearRing.getLinearRingObjectExtensions());
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param multiGeometry
     * @throws XMLStreamException
     */
    private void writeMultiGeometry(MultiGeometry multiGeometry) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_MULTI_GEOMETRY);
        this.writeCommonAbstractGeometry(multiGeometry);
        if (multiGeometry.getGeometries() != null){
            for (AbstractGeometry abstractGeometry : multiGeometry.getGeometries()){
                this.writeAbstractGeometry(abstractGeometry);
            }
        }
        if (multiGeometry.getMultiGeometrySimpleExtensions() != null){
            this.writeSimpleExtensions(multiGeometry.getMultiGeometrySimpleExtensions());
        }
        if (multiGeometry.getMultiGeometryObjectExtensions() != null){
            this.writeObjectExtensions(multiGeometry.getMultiGeometryObjectExtensions());
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
        if (point.getCoordinates() != null){
            this.writeCoordinates(point.getCoordinates());
        }
        if (point.getPointSimpleExtensions() != null){
            this.writeSimpleExtensions(point.getPointSimpleExtensions());
        }
        if (point.getPointObjectExtensions() != null){
            this.writeObjectExtensions(point.getPointObjectExtensions());
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
        writer.writeCharacters(coordinates.getCoordinatesString());
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
    private void writeSnippet(String snippet) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_SNIPPET);
        writer.writeCharacters(snippet);
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
    private void writeDescription(String description) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_DESCRIPTION);
        writer.writeCharacters(description);
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
    private void writeText(String text) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_TEXT);
        writer.writeCharacters(text);
        writer.writeEndElement();
    }

    /**
     *
     * @param text
     * @throws XMLStreamException
     */
    private void writeStyleUrl(String text) throws XMLStreamException{
        writer.writeStartElement(URI_KML, TAG_STYLE_URL);
        writer.writeCharacters(text);
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
    private void writeTargetHref(String targetHref) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_TARGET_HREF);
        writer.writeCharacters(targetHref);
        writer.writeEndElement();
    }

    /**
     *
     * @param sourceHref
     * @throws XMLStreamException
     */
    private void writeSourceHref(String sourceHref) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_SOURCE_HREF);
        writer.writeCharacters(sourceHref);
        writer.writeEndElement();
    }

    /**
     *
     * @param begin
     * @throws XMLStreamException
     */
    private void writeBegin(String begin) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_BEGIN);
        writer.writeCharacters(begin);
        writer.writeEndElement();
    }

    /**
     *
     * @param end
     * @throws XMLStreamException
     */
    private void writeEnd(String end) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_END);
        writer.writeCharacters(end);
        writer.writeEndElement();
    }

    /**
     *
     * @param when
     * @throws XMLStreamException
     */
    private void writeWhen(String when) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_WHEN);
        writer.writeCharacters(when);
        writer.writeEndElement();
    }

    /**
     *
     * @param displayName
     * @throws XMLStreamException
     */
    private void writeDisplayName(String displayName) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_DISPLAY_NAME);
        writer.writeCharacters(displayName);
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
    private void writeLinkDescription(String linkDescription) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_LINK_DESCRIPTION);
        writer.writeCharacters(linkDescription);
        writer.writeEndElement();
    }

    /**
     *
     * @param expires
     * @throws XMLStreamException
     */
    private void writeExpires(String expires) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_EXPIRES);
        writer.writeCharacters(expires);
        writer.writeEndElement();
    }

    /**
     * 
     * @param linkSnippet
     * @throws XMLStreamException
     */
    private void writeLinkSnippet(Snippet linkSnippet) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_LINK_SNIPPET);
        writer.writeAttribute(ATT_MAX_LINES, String.valueOf(linkSnippet.getMaxLines()));
        writer.writeCharacters(linkSnippet.getContent());
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
            System.out.println("TOP FOV : "+TAG_TOP_FOV);
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

    /*
     * METHODES UTILITAIRES
     */
    private static boolean isFiniteNumber(double d){
        return !(Double.isInfinite(d) && Double.isNaN(d));
    }

}

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

import org.geotoolkit.xal.xml.XalWriter;
import org.geotoolkit.atom.xml.AtomWriter;
import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
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
import org.geotoolkit.data.kml.model.Coordinates;
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
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.xml.StaxStreamWriter;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 * <p>This class provides a method to read KML files, version 2.2.</p>
 *
 * @author Samuel Andrés
 */
public class KmlWriter extends StaxStreamWriter {

    private String URI_KML;
    private final XalWriter xalWriter = new XalWriter();
    private final AtomWriter atomWriter = new AtomWriter();
    private final List<StaxStreamWriter> extensionWriters = new ArrayList<StaxStreamWriter>();
    private final List<StaxStreamWriter> dataWriters = new ArrayList<StaxStreamWriter>();

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
     * <p>This method adds a writer for given uri extensions.</p>
     *
     * @param uri
     * @param writer
     * @throws KmlException
     * @throws IOException
     * @throws XMLStreamException
     */
    public void addExtensionWriter(String uri, StaxStreamWriter writer)
            throws KmlException, IOException, XMLStreamException{
        if(writer instanceof KmlExtensionWriter){
            this.extensionWriters.add(writer);
            writer.setOutput(this.writer);
        } else {
            throw new KmlException("Extension writer must implements "+KmlExtensionWriter.class.getName()+" interface.");
        }
    }

    public void addDataWriter(String uri, StaxStreamWriter writer)
            throws KmlException, IOException, XMLStreamException{
        if(writer instanceof KmlExtensionWriter){
            this.dataWriters.add(writer);
            writer.setOutput(this.writer);
        } else {
            throw new KmlException("Extension writer must implements "+KmlExtensionWriter.class.getName()+" interface.");
        }
    }

    /**
     * <p>This method writes a Kml 2.2 / 2.1 document into the file assigned to the KmlWriter.</p>
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
                // Atom and xAL default values.
//                writer.writeNamespace(PREFIX_ATOM, URI_ATOM);
//                writer.writeNamespace(PREFIX_XAL, URI_XAL);
//                writer.setPrefix(PREFIX_XAL, URI_XAL);
//                writer.setPrefix(PREFIX_ATOM, URI_ATOM);

                for (String uri : kml.getExtensionsUris().keySet()){
                    writer.writeNamespace(kml.getExtensionsUris().get(uri), uri);
                    writer.setPrefix(kml.getExtensionsUris().get(uri), uri);
                }
            }
//            writer.writeNamespace(PREFIX_XSI, URI_XSI);
//            writer.writeAttribute(URI_XSI,
//                    "schemaLocation",
//                    URI_KML+" C:/Users/w7mainuser/Documents/OGC_SCHEMAS/sld/1.1.0/StyledLayerDescriptor.xsd");
//            writer.writeAttribute("version", "0");
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
        this.writeStandardExtensionLevel(
                kml.extensions(),
                Names.KML);
    }

    /**
     *
     * @param networkLinkControl
     * @throws XMLStreamException
     */
    private void writeNetworkLinkControl(NetworkLinkControl networkLinkControl) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_NETWORK_LINK_CONTROL);
        if (KmlUtilities.isFiniteNumber(networkLinkControl.getMinRefreshPeriod())){
            this.writeMinRefreshPeriod(networkLinkControl.getMinRefreshPeriod());
        }
        if (KmlUtilities.isFiniteNumber(networkLinkControl.getMaxSessionLength())
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
        this.writeStandardExtensionLevel(
                networkLinkControl.extensions(),
                Names.NETWORK_LINK_CONTROL);
        
        writer.writeEndElement();
    }

    /**
     *
     * @param update
     * @throws XMLStreamException
     */
    public void writeUpdate(Update update) throws XMLStreamException, KmlException{
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
            else if (object instanceof Feature){
                checkVersion(URI_KML_2_1);
                this.writeReplace((Feature) object);
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
    private void writeReplace(Feature replace) throws XMLStreamException, KmlException{
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
        for (Feature container : create.getContainers()){
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
        for (Feature feature : delete.getFeatures()){
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
        for (Object object : change.getObjects()){
            this.writeAbstractObject(object);
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param object
     * @throws XMLStreamException
     */
    private void writeAbstractObject(Object object) throws XMLStreamException, KmlException{
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
        } else if (object instanceof Feature){
            this.writeAbstractFeature((Feature) object);
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

    /**
     * 
     * @param abstractLatLonBox
     * @throws XMLStreamException
     */
    private void writeAbstractLatLonBox(AbstractLatLonBox abstractLatLonBox) throws XMLStreamException, KmlException{
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
     * @throws KmlException
     */
    private void writeAbstractSubStyle(AbstractSubStyle subStyle) 
            throws XMLStreamException, KmlException{

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
     * @throws KmlException
     */
    private void writeAbstractColorStyle(AbstractColorStyle colorStyle) 
            throws XMLStreamException, KmlException{

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
     * @throws KmlException
     */
    public void writeCommonAbstractObject(AbstractObject abstractObject) 
            throws XMLStreamException, KmlException{

        if (abstractObject.getIdAttributes() != null){
            this.writeIdAttributes(abstractObject.getIdAttributes());
        }
        this.writeSimpleExtensionsScheduler(Names.OBJECT,
                abstractObject.extensions().simples(Names.OBJECT));
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
    private void writeAbstractFeature(Feature abstractFeature) throws XMLStreamException, KmlException{
        if (FeatureTypeUtilities.isDecendedFrom(abstractFeature.getType(), KmlModelConstants.TYPE_CONTAINER)){
            this.writeAbstractContainer(abstractFeature);
        } else if (abstractFeature.getType().equals(KmlModelConstants.TYPE_NETWORK_LINK)){
            this.writeNetworkLink(abstractFeature);
        } else if (FeatureTypeUtilities.isDecendedFrom(abstractFeature.getType(), KmlModelConstants.TYPE_OVERLAY)){
            this.writeAbstractOverlay(abstractFeature);
        } else if (abstractFeature.getType().equals(KmlModelConstants.TYPE_PLACEMARK)){
            this.writePlacemark(abstractFeature);
        } else {
            System.out.println("ABSTRACT FEATURE PAS COMMUN !!!");
            for(StaxStreamWriter candidate : this.extensionWriters){
                if(((KmlExtensionWriter) candidate).canHandleComplex(null, abstractFeature)){
                    ((KmlExtensionWriter) candidate).writeComplexExtensionElement(abstractFeature);
                }
            }
        }
    }

    /**
     *
     * @param networkLink
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeNetworkLink(Feature networkLink) 
            throws XMLStreamException, KmlException{
        
        writer.writeStartElement(URI_KML, TAG_NETWORK_LINK);
        this.writeCommonAbstractFeature(networkLink);
        this.writeRefreshVisibility((Boolean) networkLink.getProperty(KmlModelConstants.ATT_NETWORK_LINK_REFRESH_VISIBILITY.getName()).getValue());
        this.writeFlyToView((Boolean) networkLink.getProperty(KmlModelConstants.ATT_NETWORK_LINK_FLY_TO_VIEW.getName()).getValue());

        if (networkLink.getProperty(KmlModelConstants.ATT_NETWORK_LINK_LINK.getName()) != null){
            Object link = networkLink.getProperty(KmlModelConstants.ATT_NETWORK_LINK_LINK.getName()).getValue();
            if (link instanceof Url)
                this.writeUrl((Url) link);
            else if (link instanceof Link)
                this.writeLink((Link) link);
        }
        this.writeStandardExtensionLevel(
                (Extensions) networkLink.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
                Names.NETWORK_LINK);
        writer.writeEndElement();
    }

    /**
     * <p>This method writes the common fields for
     * instances of AbstractFeature.</p>
     *
     * @param abstractFeature The AbstractFeature to write.
     * @throws XMLStreamException
     */
    public void writeCommonAbstractFeature(Feature abstractFeature) throws XMLStreamException, KmlException{
        Iterator i;
        if (abstractFeature.getProperty(KmlModelConstants.ATT_ID_ATTRIBUTES.getName()) != null){
            IdAttributes idAttributes = (IdAttributes) abstractFeature.getProperty(KmlModelConstants.ATT_ID_ATTRIBUTES.getName()).getValue();
            if(idAttributes != null){
                this.writeIdAttributes(idAttributes);
            }
        }

        this.writeSimpleExtensionsScheduler(Names.OBJECT,
                ((Extensions) abstractFeature.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName())
                .getValue()).simples(Names.OBJECT));

        if (abstractFeature.getProperty(KmlModelConstants.ATT_NAME.getName()) != null){
            String name = (String) abstractFeature.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue();
            if(name != null){
                this.writeName(name);
            }
        }

        this.writeVisibility((Boolean) abstractFeature.getProperty(KmlModelConstants.ATT_VISIBILITY.getName()).getValue());
        this.writeOpen((Boolean) abstractFeature.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());

        if (abstractFeature.getProperty(KmlModelConstants.ATT_AUTHOR.getName()) != null){
            AtomPersonConstruct author = (AtomPersonConstruct) abstractFeature.getProperty(KmlModelConstants.ATT_AUTHOR.getName()).getValue();
            if(author != null){
                this.writeAtomPersonConstruct(author);
            }
        }
        if (abstractFeature.getProperty(KmlModelConstants.ATT_LINK.getName()) != null){
            AtomLink atomLink = (AtomLink) abstractFeature.getProperty(KmlModelConstants.ATT_LINK.getName()).getValue();
            if(atomLink != null){
                this.writeAtomLink(atomLink);
            }
        }
        if (abstractFeature.getProperty(KmlModelConstants.ATT_ADDRESS.getName()) != null){
            String address = (String) abstractFeature.getProperty(KmlModelConstants.ATT_ADDRESS.getName()).getValue();
            if(address != null){
                this.writeAddress(address);
            }
        }
        if (abstractFeature.getProperty(KmlModelConstants.ATT_ADDRESS_DETAILS.getName()) != null){
            AddressDetails addressDetails = (AddressDetails) abstractFeature.getProperty(KmlModelConstants.ATT_ADDRESS_DETAILS.getName()).getValue();
            if(addressDetails != null){
                this.writeXalAddresDetails(addressDetails);
            }
        }
        if (abstractFeature.getProperty(KmlModelConstants.ATT_PHONE_NUMBER.getName()) != null){
            String phoneNumber = (String) abstractFeature.getProperty(KmlModelConstants.ATT_PHONE_NUMBER.getName()).getValue();
            if(phoneNumber != null){
                this.writePhoneNumber(phoneNumber);
            }
        }
        if (abstractFeature.getProperty(KmlModelConstants.ATT_SNIPPET.getName()) != null){
            Object snippet = abstractFeature.getProperty(KmlModelConstants.ATT_SNIPPET.getName()).getValue();
            if(snippet != null){
                this.writeSnippet(snippet);
            }
        }
        if (abstractFeature.getProperty(KmlModelConstants.ATT_DESCRIPTION.getName()) != null){
            Object description = abstractFeature.getProperty(KmlModelConstants.ATT_DESCRIPTION.getName()).getValue();
            if(description != null){
                this.writeDescription(description);
            }
        }
        if (abstractFeature.getProperty(KmlModelConstants.ATT_VIEW.getName()) != null){
            AbstractView view = (AbstractView) abstractFeature.getProperty(KmlModelConstants.ATT_VIEW.getName()).getValue();
            if(view != null){
                this.writeAbstractView(view);
            }
        }
        if (abstractFeature.getProperty(KmlModelConstants.ATT_TIME_PRIMITIVE.getName()) != null){
            AbstractTimePrimitive timePrimitive = (AbstractTimePrimitive) abstractFeature.getProperty(KmlModelConstants.ATT_TIME_PRIMITIVE.getName()).getValue();
            if(timePrimitive != null){
                this.writeAbstractTimePrimitive(timePrimitive);
            }
        }
        if (abstractFeature.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()) != null){
            URI styleUrl = (URI) abstractFeature.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()).getValue();
            if (styleUrl != null){
                this.writeStyleUrl(styleUrl);
            }
        }
        if (abstractFeature.getProperty(KmlModelConstants.ATT_STYLE_SELECTOR.getName()) != null){
            i = abstractFeature.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).iterator();
            while(i.hasNext()){
                this.writeAbstractStyleSelector((AbstractStyleSelector) ((Property) i.next()).getValue());
            }
        }
        if (abstractFeature.getProperty(KmlModelConstants.ATT_REGION.getName()) != null){
            Region region = (Region) abstractFeature.getProperty(KmlModelConstants.ATT_REGION.getName()).getValue();
            if(region != null){
                this.writeRegion(region);
            }
        }
        if (abstractFeature.getProperty(KmlModelConstants.ATT_EXTENDED_DATA.getName()) != null){
            Object extendedData = abstractFeature.getProperty(KmlModelConstants.ATT_EXTENDED_DATA.getName()).getValue();
            if(extendedData != null){
                this.writeExtendedData(extendedData);
            }
        }
        this.writeStandardExtensionLevel(
                (Extensions) abstractFeature.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
                Names.FEATURE);
    }

    /**
     * 
     * @param dataContainer
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeExtendedData(Object dataContainer) 
            throws XMLStreamException, KmlException{

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
     * @throws KmlException
     */
    private void writeExtendedData(ExtendedData extendedData) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_EXTENDED_DATA);
        for (Data data : extendedData.getDatas()){
            this.writeData(data);
        }
        for (SchemaData schemaData : extendedData.getSchemaData()){
            this.writeSchemaData(schemaData);
        }
        if (extendedData.getAnyOtherElements() != null){

            this.writeDataScheduler(extendedData.getAnyOtherElements());
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param metadata
     * @throws XMLStreamException
     * @throws KmlException
     * @deprecated
     */
    @Deprecated
    private void writeMetaData(Metadata metadata) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_META_DATA);
        this.writeDataScheduler(metadata.getContent());
        writer.writeEndElement();
    }

    /**
     * 
     * @param schemaData
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeSchemaData(SchemaData schemaData) 
            throws XMLStreamException, KmlException{

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
     * @throws KmlException
     */
    private void writeData(Data data) 
            throws XMLStreamException, KmlException{

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
     * @throws KmlException
     */
    private void writeRegion(Region region) 
            throws XMLStreamException, KmlException{
        
        writer.writeStartElement(URI_KML,TAG_REGION);
        this.writeCommonAbstractObject(region);
        if(region.getLatLonAltBox() != null){
            this.writeLatLonAltBox(region.getLatLonAltBox());
        }
        if(region.getLod() != null){
            this.writeLod(region.getLod());
        }
        this.writeStandardExtensionLevel(
                region.extensions(),
                Names.REGION);
        writer.writeEndElement();
    }

    /**
     * 
     * @param lod
     * @throws XMLStreamException
     */
    private void writeLod(Lod lod) 
            throws XMLStreamException, KmlException{
        
        writer.writeStartElement(URI_KML,TAG_LOD);
        this.writeCommonAbstractObject(lod);
        if (KmlUtilities.isFiniteNumber(lod.getMinLodPixels())){
            this.writeMinLodPixels(lod.getMinLodPixels());
        }
        if (KmlUtilities.isFiniteNumber(lod.getMaxLodPixels())){
            this.writeMaxLodPixels(lod.getMaxLodPixels());
        }
        if (KmlUtilities.isFiniteNumber(lod.getMinFadeExtent())){
            this.writeMinFadeExtent(lod.getMinFadeExtent());
        }
        if (KmlUtilities.isFiniteNumber(lod.getMaxFadeExtent())){
            this.writeMaxFadeExtent(lod.getMaxFadeExtent());
        }
        this.writeStandardExtensionLevel(
                lod.extensions(),
                Names.LOD);
        writer.writeEndElement();
    }

    /**
     * 
     * @param latLonAltBox
     * @throws XMLStreamException
     */
    private void writeLatLonAltBox(LatLonAltBox latLonAltBox) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_LAT_LON_ALT_BOX);
        this.writeCommonAbstractLatLonBox(latLonAltBox);
        if (KmlUtilities.isFiniteNumber(latLonAltBox.getMinAltitude())){
            this.writeMinAltitude(latLonAltBox.getMinAltitude());
        }
        if (KmlUtilities.isFiniteNumber(latLonAltBox.getMaxAltitude())){
            this.writeMaxAltitude(latLonAltBox.getMaxAltitude());
        }
        if (latLonAltBox.getAltitudeMode() != null){
            this.writeAltitudeMode(latLonAltBox.getAltitudeMode());
        }
        this.writeStandardExtensionLevel(
                latLonAltBox.extensions(),
                Names.LAT_LON_ALT_BOX);
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
     * @throws KmlException
     */
    private void writeAbstractTimePrimitive(AbstractTimePrimitive abstractTimePrimitive) 
            throws XMLStreamException, KmlException{

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
     * @throws KmlException
     */
    private void writeTimeSpan(TimeSpan timeSpan) 
            throws XMLStreamException, KmlException{
        
        writer.writeStartElement(URI_KML, TAG_TIME_SPAN);
        this.writeCommonAbstractTimePrimitive(timeSpan);
        if (timeSpan.getBegin() != null){
            this.writeBegin(timeSpan.getBegin());
        }
        if (timeSpan.getEnd() != null){
            this.writeEnd(timeSpan.getEnd());
        }
        this.writeStandardExtensionLevel(
                timeSpan.extensions(),
                Names.TIME_SPAN);
        writer.writeEndElement();
    }

    /**
     * 
     * @param timeStamp
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeTimeStamp(TimeStamp timeStamp) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_TIME_STAMP);
        this.writeCommonAbstractTimePrimitive(timeStamp);
        if (timeStamp.getWhen() != null){
            this.writeWhen(timeStamp.getWhen());
        }
        this.writeStandardExtensionLevel(
                timeStamp.extensions(),
                Names.TIME_STAMP);
        writer.writeEndElement();
    }

    /**
     *
     * @param abstractView The AbstractView to write.
     * @throws XMLStreamException
     */
    public void writeAbstractView(AbstractView abstractView) throws XMLStreamException, KmlException{
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
    private void writeLookAt(LookAt lookAt) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_LOOK_AT);
        this.writeCommonAbstractView(lookAt);
        if (KmlUtilities.isFiniteNumber(lookAt.getLongitude())){
            this.writeLongitude(lookAt.getLongitude());
        }
        if (KmlUtilities.isFiniteNumber(lookAt.getLatitude())){
            this.writeLatitude(lookAt.getLatitude());
        }
        if (KmlUtilities.isFiniteNumber(lookAt.getAltitude())){
            this.writeAltitude(lookAt.getAltitude());
        }
        if (KmlUtilities.isFiniteNumber(lookAt.getHeading())){
            this.writeHeading(lookAt.getHeading());
        }
        if (KmlUtilities.isFiniteNumber(lookAt.getTilt())){
            this.writeTilt(lookAt.getTilt());
        }
        if (KmlUtilities.isFiniteNumber(lookAt.getRange())){
            this.writeRange(lookAt.getRange());
        }
        if (lookAt.getAltitudeMode() != null){
            this.writeAltitudeMode(lookAt.getAltitudeMode());
        }
        this.writeStandardExtensionLevel(
                lookAt.extensions(),
                Names.LOOK_AT);
        writer.writeEndElement();
    }

    private void writeCamera(Camera camera) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_CAMERA);
        this.writeCommonAbstractView(camera);
        if (KmlUtilities.isFiniteNumber(camera.getLongitude())){
            this.writeLongitude(camera.getLongitude());
        }
        if (KmlUtilities.isFiniteNumber(camera.getLatitude())){
            this.writeLatitude(camera.getLatitude());
        }
        if (KmlUtilities.isFiniteNumber(camera.getAltitude())){
            this.writeAltitude(camera.getAltitude());
        }
        if (KmlUtilities.isFiniteNumber(camera.getHeading())){
            this.writeHeading(camera.getHeading());
        }
        if (KmlUtilities.isFiniteNumber(camera.getTilt())){
            this.writeTilt(camera.getTilt());
        }
        if (KmlUtilities.isFiniteNumber(camera.getRoll())){
            this.writeRoll(camera.getRoll());
        }
        if (camera.getAltitudeMode() != null){
            this.writeAltitudeMode(camera.getAltitudeMode());
        }
        this.writeStandardExtensionLevel(
                camera.extensions(),
                Names.CAMERA);
        writer.writeEndElement();
    }

    /**
     * <p>This method writes the common fields for
     * AbstractView instances.</p>
     * 
     * @param abstractView The AbstractView to write.
     * @throws XMLStreamException
     */
    private void writeCommonAbstractView(AbstractView abstractView) throws XMLStreamException, KmlException{
        this.writeCommonAbstractObject(abstractView);
        this.writeStandardExtensionLevel(
                abstractView.extensions(),
                Names.VIEW);
    }

    /**
     * <p>This method writes the common fields for
     * AbstractTimePrimitive instances.</p>
     *
     * @param abstractTimePrimitive The AbstractTimePrimitive to write.
     * @throws XMLStreamException
     */
    public void writeCommonAbstractTimePrimitive(AbstractTimePrimitive abstractTimePrimitive) 
            throws XMLStreamException, KmlException{

        this.writeCommonAbstractObject(abstractTimePrimitive);
        this.writeStandardExtensionLevel(
                abstractTimePrimitive.extensions(),
                Names.TIME_PRIMITIVE);
    }

    /**
     *
     * @param abstractStyleSelector The AbstractStyleSelector to write.
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeAbstractStyleSelector(AbstractStyleSelector abstractStyleSelector)
            throws XMLStreamException, KmlException{
        
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
     * @throws KmlException
     */
    private void writeCommonAbstractStyleSelector(AbstractStyleSelector abstractStyleSelector) 
            throws XMLStreamException, KmlException{

        this.writeCommonAbstractObject(abstractStyleSelector);
        this.writeStandardExtensionLevel(
                abstractStyleSelector.extensions(),
                Names.STYLE_SELECTOR);
    }

    /**
     *
     * @param styleMap
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeStyleMap(StyleMap styleMap) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_STYLE_MAP);
        this.writeCommonAbstractStyleSelector(styleMap);
        for(Pair pair : styleMap.getPairs()){
            this.writePair(pair);
        }
        this.writeStandardExtensionLevel(
                styleMap.extensions(),
                Names.STYLE_MAP);
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
        this.writeStandardExtensionLevel(
                pair.extensions(),
                Names.PAIR);
        writer.writeEndElement();
    }

    /**
     * 
     * @param style
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeStyle(Style style) 
            throws XMLStreamException, KmlException{

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
        this.writeStandardExtensionLevel(
                style.extensions(),
                Names.STYLE);
        writer.writeEndElement();
    }

    /**
     *
     * @param iconStyle
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeIconStyle(IconStyle iconStyle) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_ICON_STYLE);
        this.writeCommonAbstractColorStyle(iconStyle);
        if (KmlUtilities.isFiniteNumber(iconStyle.getScale())){
            this.writeScale(iconStyle.getScale());
        }
        if (KmlUtilities.isFiniteNumber(iconStyle.getHeading())){
            this.writeHeading(iconStyle.getHeading());
        }
        if (iconStyle.getIcon() != null){
            this.writeIcon(iconStyle.getIcon());
        }
        if (iconStyle.getHotSpot() != null){
            this.writeHotSpot(iconStyle.getHotSpot());
        }
        this.writeStandardExtensionLevel(
                iconStyle.extensions(),
                Names.ICON_STYLE);
        writer.writeEndElement();
    }

    /**
     *
     * @param labelStyle
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeLabelStyle(LabelStyle labelStyle) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_LABEL_STYLE);
        this.writeCommonAbstractColorStyle(labelStyle);
        if (KmlUtilities.isFiniteNumber(labelStyle.getScale())){
            this.writeScale(labelStyle.getScale());
        }
        this.writeStandardExtensionLevel(
                labelStyle.extensions(),
                Names.LABEL_STYLE);
        writer.writeEndElement();
    }

    /**
     *
     * @param lineStyle
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeLineStyle(LineStyle lineStyle) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_LINE_STYLE);
        this.writeCommonAbstractColorStyle(lineStyle);
        if (KmlUtilities.isFiniteNumber(lineStyle.getWidth())){
            this.writeWidth(lineStyle.getWidth());
        }
        this.writeStandardExtensionLevel(
                lineStyle.extensions(),
                Names.LINE_STYLE);
        writer.writeEndElement();
    }

    /**
     *
     * @param polyStyle
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writePolyStyle(PolyStyle polyStyle) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_POLY_STYLE);
        this.writeCommonAbstractColorStyle(polyStyle);
        this.writeFill(polyStyle.getFill());
        this.writeOutline(polyStyle.getOutline());
        this.writeStandardExtensionLevel(
                polyStyle.extensions(),
                Names.POLY_STYLE);
        writer.writeEndElement();
    }

    /**
     *
     * @param balloonStyle
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeBalloonStyle(BalloonStyle balloonStyle) 
            throws XMLStreamException, KmlException{

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
        this.writeStandardExtensionLevel(
                balloonStyle.extensions(),
                Names.BALLOON_STYLE);
        writer.writeEndElement();
    }

    /**
     * 
     * @param listStyle
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeListStyle(ListStyle listStyle) 
            throws XMLStreamException, KmlException{
        
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
        if (KmlUtilities.isFiniteNumber(listStyle.getMaxSnippetLines())
                && checkVersionSimple(URI_KML_2_2)){
            this.writeMaxSnippetLines(listStyle.getMaxSnippetLines());
        }
        this.writeStandardExtensionLevel(
                listStyle.extensions(),
                Names.LIST_STYLE);
        writer.writeEndElement();
    }

    /**
     * 
     * @param itemIcon
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeItemIcon(ItemIcon itemIcon) 
            throws XMLStreamException, KmlException{
        
        writer.writeStartElement(URI_KML, TAG_ITEM_ICON);
        this.writeCommonAbstractObject(itemIcon);
        if (itemIcon.getStates() != null){
            this.writeStates(itemIcon.getStates());
        }
        if (itemIcon.getHref() != null){
            this.writeHref(itemIcon.getHref());
        }
        this.writeStandardExtensionLevel(
                itemIcon.extensions(),
                Names.ITEM_ICON);
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
     * @throws KmlException
     */
    private void writeIcon(BasicLink icon) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_ICON);
        if (icon.getIdAttributes() != null){
            this.writeIdAttributes(icon.getIdAttributes());
        }

        this.writeSimpleExtensionsScheduler(Names.OBJECT,
                icon.extensions().simples(Names.OBJECT));

        if (icon.getHref() != null){
            this.writeHref(icon.getHref());
        }

        this.writeStandardExtensionLevel(
                icon.extensions(),
                Names.BASIC_LINK);
        writer.writeEndElement();
    }

    /**
     * <p>This method writes an icon element typed as Link.</p>
     *
     * @param icon
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeIcon(Icon icon) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_ICON);
        this.writeLink_structure(icon);
        writer.writeEndElement();
    }

    /**
     * 
     * @param link
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeLink(Link link) 
            throws XMLStreamException, KmlException{
        
        writer.writeStartElement(URI_KML, TAG_LINK);
        this.writeLink_structure(link);
        writer.writeEndElement();
    }

    /**
     * 
     * @param url
     * @throws XMLStreamException
     * @throws KmlException
     * @deprecated
     */
    @Deprecated
    private void writeUrl(Url url) 
            throws XMLStreamException, KmlException{
        
        writer.writeStartElement(URI_KML, TAG_URL);
        this.writeLink_structure(url);
        writer.writeEndElement();
    }

    /**
     * <p>This method writes the Link structure used by different elements.</p>
     *
     * @param link
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeLink_structure(Link link) 
            throws XMLStreamException, KmlException{

        this.writeCommonAbstractObject(link);

        if (link.getHref() != null){
            this.writeHref(link.getHref());
        }

        this.writeStandardExtensionLevel(
                link.extensions(),
                Names.BASIC_LINK);

        if (link.getRefreshMode() != null){
            this.writeRefreshMode(link.getRefreshMode());
        }
        if (KmlUtilities.isFiniteNumber(link.getRefreshInterval())){
            this.writeRefreshInterval(link.getRefreshInterval());
        }
        if (link.getViewRefreshMode() != null){
            this.writeViewRefreshMode(link.getViewRefreshMode());
        }
        if (KmlUtilities.isFiniteNumber(link.getViewRefreshTime())){
            this.writeViewRefreshTime(link.getViewRefreshTime());
        }
        if (KmlUtilities.isFiniteNumber(link.getViewBoundScale())){
            this.writeViewBoundScale(link.getViewBoundScale());
        }
        if (link.getViewFormat() != null){
            this.writeViewFormat(link.getViewFormat());
        }
        if (link.getHttpQuery() != null){
            this.writeHttpQuery(link.getHttpQuery());
        }
        
        this.writeStandardExtensionLevel(
                link.extensions(),
                Names.LINK);
    }

    /**
     * <p>This method writes the common fields for
     * instances of AbstractColorStyle.</p>
     *
     * @param abstractColorStyle The AbstractColorStyle to write.
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeCommonAbstractColorStyle(AbstractColorStyle abstractColorStyle) 
            throws XMLStreamException, KmlException{
        
        this.writeCommonAbstractSubStyle(abstractColorStyle);
        if (abstractColorStyle.getColor() != null){
            this.writeColor(abstractColorStyle.getColor());
        }
        if (abstractColorStyle.getColorMode() != null){
            this.writeColorMode(abstractColorStyle.getColorMode());
        }

        this.writeStandardExtensionLevel(
                abstractColorStyle.extensions(),
                Names.COLOR_STYLE);
    }

    /**
     * <p>This method writes the common fields for
     * instances of AbstractSubStyle.</p>
     *
     * @param abstractSubStyle The AbstractSubStyle to write.
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeCommonAbstractSubStyle(AbstractSubStyle abstractSubStyle) 
            throws XMLStreamException, KmlException{

        this.writeCommonAbstractObject(abstractSubStyle);
        this.writeStandardExtensionLevel(
                abstractSubStyle.extensions(),
                Names.SUB_STYLE);
    }

    /**
     * 
     * @param placemark
     * @throws XMLStreamException
     */
    private void writePlacemark(Feature placemark) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_PLACEMARK);
        this.writeCommonAbstractFeature(placemark);
        if (placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()) != null){
            AbstractGeometry geometry = (AbstractGeometry) placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
            if(geometry != null){
                this.writeAbstractGeometry(geometry);
            }
        }
        this.writeStandardExtensionLevel(
                (Extensions) placemark.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
                Names.PLACEMARK);
        writer.writeEndElement();
    }

    /**
     *
     * @param abstractContainer The AbstractContainer to write
     * @throws XMLStreamException
     */
    private void writeAbstractContainer(Feature abstractContainer) throws XMLStreamException, KmlException{
        if (abstractContainer.getType().equals(KmlModelConstants.TYPE_FOLDER)){
            this.writeFolder(abstractContainer);
        } else if (abstractContainer.getType().equals(KmlModelConstants.TYPE_DOCUMENT)){
            this.writeDocument(abstractContainer);
        }
    }

    /**
     *
     * @param abstractOverlay The AbstractOverlay to write.
     * @throws XMLStreamException
     */
    private void writeAbstractOverlay(Feature abstractOverlay) throws XMLStreamException, KmlException{
        if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_GROUND_OVERLAY)){
            this.writeGroundOverlay(abstractOverlay);
        } else if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_SCREEN_OVERLAY)){
            this.writeScreenOverlay(abstractOverlay);
        } else if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_PHOTO_OVERLAY)){
            this.writePhotoOverlay(abstractOverlay);
        }
    }

    /**
     * 
     * @param photoOverlay
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writePhotoOverlay(Feature photoOverlay) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_PHOTO_OVERLAY);
        this.writeCommonAbstractOverlay(photoOverlay);
        if (photoOverlay.getProperty(KmlModelConstants.ATT_PHOTO_OVERLAY_ROTATION.getName()) != null){
            Double rotation = (Double) photoOverlay.getProperty(KmlModelConstants.ATT_PHOTO_OVERLAY_ROTATION.getName()).getValue();
            if(rotation != null){
                this.writeRotation(rotation);
            }
        }
        if (photoOverlay.getProperty(KmlModelConstants.ATT_PHOTO_OVERLAY_VIEW_VOLUME.getName()) != null){
            ViewVolume viewVolume = (ViewVolume) photoOverlay.getProperty(KmlModelConstants.ATT_PHOTO_OVERLAY_VIEW_VOLUME.getName()).getValue();
            if(viewVolume != null){
                this.writeViewVolume(viewVolume);
            }
        }
        if (photoOverlay.getProperty(KmlModelConstants.ATT_PHOTO_OVERLAY_IMAGE_PYRAMID.getName()) != null){
            ImagePyramid imagePyramid = (ImagePyramid) photoOverlay.getProperty(KmlModelConstants.ATT_PHOTO_OVERLAY_IMAGE_PYRAMID.getName()).getValue();
            if(imagePyramid != null){
                this.writeImagePyramid(imagePyramid);
            }
        }
        if (photoOverlay.getProperty(KmlModelConstants.ATT_PHOTO_OVERLAY_POINT.getName()) != null){
            Point point = (Point) photoOverlay.getProperty(KmlModelConstants.ATT_PHOTO_OVERLAY_POINT.getName()).getValue();
            if(point != null){
                this.writePoint(point);
            }
        }
        if (photoOverlay.getProperty(KmlModelConstants.ATT_PHOTO_OVERLAY_SHAPE.getName()) != null){
            Shape shape = (Shape) photoOverlay.getProperty(KmlModelConstants.ATT_PHOTO_OVERLAY_SHAPE.getName()).getValue();
            if(shape != null){
                this.writeShape(shape);
            }
        }
        this.writeStandardExtensionLevel(
                (Extensions) photoOverlay.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
                Names.PHOTO_OVERLAY);
        writer.writeEndElement();
    }

    /**
     * 
     * @param imagePyramid
     * @throws XMLStreamException
     */
    private void writeImagePyramid(ImagePyramid imagePyramid) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_IMAGE_PYRAMID);
        this.writeCommonAbstractObject(imagePyramid);
        if (KmlUtilities.isFiniteNumber(imagePyramid.getTitleSize())){
            this.writeTitleSize(imagePyramid.getTitleSize());
        }
        if (KmlUtilities.isFiniteNumber(imagePyramid.getMaxWidth())){
            this.writeMaxWidth(imagePyramid.getMaxWidth());
        }
        if (KmlUtilities.isFiniteNumber(imagePyramid.getMaxHeight())){
            this.writeMaxHeight(imagePyramid.getMaxHeight());
        }
        if (imagePyramid.getGridOrigin() != null){
            this.writeGridOrigin(imagePyramid.getGridOrigin());
        }
        this.writeStandardExtensionLevel(
                imagePyramid.extensions(),
                Names.IMAGE_PYRAMID);
        writer.writeEndElement();
    }
    
    /**
     *
     * @param viewVolume
     * @throws XMLStreamException
     */
    private void writeViewVolume(ViewVolume viewVolume) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_VIEW_VOLUME);
        this.writeCommonAbstractObject(viewVolume);
        if (KmlUtilities.isFiniteNumber(viewVolume.getLeftFov())){
            this.writeLeftFov(viewVolume.getLeftFov());
        }
        if (KmlUtilities.isFiniteNumber(viewVolume.getRightFov())){
            this.writeRightFov(viewVolume.getRightFov());
        }
        if (KmlUtilities.isFiniteNumber(viewVolume.getBottomFov())){
            this.writeBottomFov(viewVolume.getBottomFov());
        }
        if (KmlUtilities.isFiniteNumber(viewVolume.getTopFov())){
            this.writeTopFov(viewVolume.getTopFov());
        }
        if (KmlUtilities.isFiniteNumber(viewVolume.getNear())){
            this.writeNear(viewVolume.getNear());
        }
        this.writeStandardExtensionLevel(
                viewVolume.extensions(),
                Names.VIEW_VOLUME);
        writer.writeEndElement();
    }

    /**
     *
     * @param screenOverlay
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeScreenOverlay(Feature screenOverlay) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_SCREEN_OVERLAY);
        this.writeCommonAbstractOverlay(screenOverlay);
        if (screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_OVERLAYXY.getName()) != null){
            Vec2 overlayXY = (Vec2) screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_OVERLAYXY.getName()).getValue();
            if(overlayXY != null){
                this.writeOverlayXY(overlayXY);
            }
        }
        if (screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_SCREENXY.getName()) != null){
            Vec2 screenXY = (Vec2) screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_SCREENXY.getName()).getValue();
            if(screenXY != null){
                this.writeScreenXY(screenXY);
            }
        }
        if (screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_ROTATIONXY.getName()) != null){
            Vec2 rotationXY = (Vec2) screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_ROTATIONXY.getName()).getValue();
            if(rotationXY != null){
                this.writeRotationXY(rotationXY);
            }
        }
        if (screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_SIZE.getName()) != null){
            Vec2 size = (Vec2) screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_SIZE.getName()).getValue();
            if(size != null){
                this.writeSize(size);
            }
        }
        if (screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_ROTATION.getName()) != null){
            Double rotation = (Double) screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_ROTATION.getName()).getValue();
            if(rotation != null){
                this.writeRotation(rotation);
            }
        }
        this.writeStandardExtensionLevel(
                (Extensions) screenOverlay.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
                Names.SCREEN_OVERLAY);
        writer.writeEndElement();
    }


    /**
     * 
     * @param groundOverlay
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeGroundOverlay(Feature groundOverlay) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_GROUND_OVERLAY);
        this.writeCommonAbstractOverlay(groundOverlay);
        if (groundOverlay.getProperty(KmlModelConstants.ATT_GROUND_OVERLAY_ALTITUDE.getName()) != null){
            Double altitude = (Double) groundOverlay.getProperty(KmlModelConstants.ATT_GROUND_OVERLAY_ALTITUDE.getName()).getValue();
            if(altitude != null){
                this.writeAltitude(altitude);
            }
        }
        if (groundOverlay.getProperty(KmlModelConstants.ATT_GROUND_OVERLAY_ALTITUDE_MODE.getName()) != null){
            AltitudeMode altitudeMode = (AltitudeMode) groundOverlay.getProperty(KmlModelConstants.ATT_GROUND_OVERLAY_ALTITUDE_MODE.getName()).getValue();
            if(altitudeMode != null){
                this.writeAltitudeMode(altitudeMode);
            }
        }
        if (groundOverlay.getProperty(KmlModelConstants.ATT_GROUND_OVERLAY_LAT_LON_BOX.getName()) != null){
            LatLonBox latLonBox = (LatLonBox) groundOverlay.getProperty(KmlModelConstants.ATT_GROUND_OVERLAY_LAT_LON_BOX.getName()).getValue();
            if(latLonBox != null){
                this.writeLatLonBox(latLonBox);
            }
        }
        this.writeStandardExtensionLevel(
                (Extensions) groundOverlay.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
                Names.GROUND_OVERLAY);
        writer.writeEndElement();
    }

    /**
     *
     * @param latLonBox
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeLatLonBox(LatLonBox latLonBox) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_LAT_LON_BOX);
        this.writeCommonAbstractLatLonBox(latLonBox);
        if (KmlUtilities.isFiniteNumber(latLonBox.getRotation())){
            this.writeRotation(latLonBox.getRotation());
        }
        this.writeStandardExtensionLevel(
                latLonBox.extensions(),
                Names.LAT_LON_BOX);
        writer.writeEndElement();
    }

    /**
     * <p>This method writes tha common fields for
     * AbstractLatLonBox instances.</p>
     *
     * @param abstractLatLonBox
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeCommonAbstractLatLonBox(AbstractLatLonBox abstractLatLonBox) 
            throws XMLStreamException, KmlException{

        this.writeCommonAbstractObject(abstractLatLonBox);
        if (KmlUtilities.isFiniteNumber(abstractLatLonBox.getNorth())){
            this.writeNorth(abstractLatLonBox.getNorth());
        }
        if (KmlUtilities.isFiniteNumber(abstractLatLonBox.getSouth())){
            this.writeSouth(abstractLatLonBox.getSouth());
        }
        if (KmlUtilities.isFiniteNumber(abstractLatLonBox.getEast())){
            this.writeEast(abstractLatLonBox.getEast());
        }
        if (KmlUtilities.isFiniteNumber(abstractLatLonBox.getWest())){
            this.writeWest(abstractLatLonBox.getWest());
        }
        this.writeStandardExtensionLevel(
                abstractLatLonBox.extensions(),
                Names.ABSTRACT_LAT_LON_BOX);
    }
    
    private void writeCommonAbstractOverlay(Feature abstractOverlay) throws XMLStreamException, KmlException{
        this.writeCommonAbstractFeature(abstractOverlay);
        if (abstractOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_COLOR.getName()) != null){
            Color color = (Color) abstractOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_COLOR.getName()).getValue();
            if(color != null){
                this.writeColor(color);
            }
        }
        if (abstractOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_DRAW_ORDER.getName()) != null){
            Integer drawOrder = (Integer) abstractOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_DRAW_ORDER.getName()).getValue();
            if(drawOrder != null){
                this.writeDrawOrder(drawOrder);
            }
        }
        if (abstractOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_ICON.getName()) != null){
            Icon icon = (Icon) abstractOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_ICON.getName()).getValue();
            if(icon != null){
                this.writeIcon(icon);
            }
        }
        this.writeStandardExtensionLevel(
                (Extensions) abstractOverlay.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
                Names.OVERLAY);
    }

    /**
     * <p>This method writes tha common fields for
     * AbstractContainer instances.</p>
     *
     * @param abstractContainer The AbstractContainer to write.
     * @throws XMLStreamException
     */
    private void writeCommonAbstractContainer(Feature abstractContainer) throws XMLStreamException, KmlException{
        this.writeCommonAbstractFeature(abstractContainer);
        this.writeStandardExtensionLevel(
                (Extensions) abstractContainer.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
                Names.CONTAINER);
    }

    /**
     *
     * @param folder
     * @throws XMLStreamException
     */
    private void writeFolder(Feature folder) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_FOLDER);
        Iterator i;
        this.writeCommonAbstractContainer(folder);
        if(folder.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()) != null){
            i = folder.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).iterator();
            while(i.hasNext()){
                this.writeAbstractFeature((Feature) ((Property) i.next()).getValue());
            }
        }
        this.writeStandardExtensionLevel(
                (Extensions) folder.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
                Names.FOLDER);
        writer.writeEndElement();
    }

    /**
     *
     * @param document
     * @throws XMLStreamException
     */
    private void writeDocument(Feature document) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_DOCUMENT);
        Iterator i;
        this.writeCommonAbstractContainer(document);
        if(document.getProperties(KmlModelConstants.ATT_DOCUMENT_SCHEMAS.getName()) != null){
            i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_SCHEMAS.getName()).iterator();
            while(i.hasNext()){
                checkVersion(URI_KML_2_2);
                this.writeSchema((Schema) ((Property) i.next()).getValue());
            }
        }
        if(document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()) != null){
            i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();
            while(i.hasNext()){
                this.writeAbstractFeature((Feature) ((Property) i.next()).getValue());
            }
        }
        this.writeStandardExtensionLevel(
                (Extensions) document.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
                Names.DOCUMENT);
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
     * @throws KmlException
     */
    private void writeCommonAbstractGeometry(AbstractGeometry abstractGeometry) 
            throws XMLStreamException, KmlException{

        this.writeCommonAbstractObject(abstractGeometry);
        this.writeStandardExtensionLevel(
                abstractGeometry.extensions(),
                Names.GEOMETRY);
    }

    /**
     *
     * @param model
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeModel(Model model) 
            throws XMLStreamException, KmlException{

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
        this.writeStandardExtensionLevel(
                model.extensions(),
                Names.MODEL);
        writer.writeEndElement();
    }

    /**
     *
     * @param location
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeLocation(Location location) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_LOCATION);
        this.writeCommonAbstractObject(location);
        if (KmlUtilities.isFiniteNumber(location.getLongitude())){
            this.writeLongitude(location.getLongitude());
        }
        if (KmlUtilities.isFiniteNumber(location.getLatitude())){
            this.writeLatitude(location.getLatitude());
        }
        if (KmlUtilities.isFiniteNumber(location.getAltitude())){
            this.writeAltitude(location.getAltitude());
        }
        this.writeStandardExtensionLevel(
                location.extensions(),
                Names.LOCATION);
        writer.writeEndElement();
    }

    /**
     *
     * @param orientation
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeOrientation(Orientation orientation) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_ORIENTATION);
        this.writeCommonAbstractObject(orientation);
        if (KmlUtilities.isFiniteNumber(orientation.getHeading())){
            this.writeHeading(orientation.getHeading());
        }
        if (KmlUtilities.isFiniteNumber(orientation.getTilt())){
            this.writeTilt(orientation.getTilt());
        }
        if (KmlUtilities.isFiniteNumber(orientation.getRoll())){
            this.writeRoll(orientation.getRoll());
        }
        this.writeStandardExtensionLevel(
                orientation.extensions(),
                Names.ORIENTATION);
        writer.writeEndElement();
    }

    /**
     * 
     * @param scale
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeScale(Scale scale) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_SCALE_BIG);
        if (KmlUtilities.isFiniteNumber(scale.getX())){
            this.writeX(scale.getX());
        }
        if (KmlUtilities.isFiniteNumber(scale.getY())){
            this.writeY(scale.getY());
        }
        if (KmlUtilities.isFiniteNumber(scale.getZ())){
            this.writeZ(scale.getZ());
        }
        this.writeStandardExtensionLevel(
                scale.extensions(),
                Names.SCALE);
        writer.writeEndElement();
    }

    /**
     *
     * @param resourceMap
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeResourceMap(ResourceMap resourceMap) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_RESOURCE_MAP);
        this.writeCommonAbstractObject(resourceMap);
        for (Alias alias : resourceMap.getAliases()){
            this.writeAlias(alias);
        }
        this.writeStandardExtensionLevel(
                resourceMap.extensions(),
                Names.RESOURCE_MAP);
        writer.writeEndElement();
    }

    /**
     *
     * @param alias
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeAlias(Alias alias) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_ALIAS);
        this.writeCommonAbstractObject(alias);
        if (alias.getTargetHref() != null){
            this.writeTargetHref(alias.getTargetHref());
        }
        if (alias.getSourceHref() != null){
            this.writeSourceHref(alias.getSourceHref());
        }
        this.writeStandardExtensionLevel(
                alias.extensions(),
                Names.ALIAS);
        writer.writeEndElement();
    }

    /**
     *
     * @param polygon
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writePolygon(Polygon polygon) 
            throws XMLStreamException, KmlException{

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
        this.writeStandardExtensionLevel(
                polygon.extensions(),
                Names.POLYGON);
        writer.writeEndElement();
    }

    /**
     *
     * @param boundary
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeOuterBoundaryIs(Boundary boundary) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_OUTER_BOUNDARY_IS);
        this.writeBoundary(boundary);
        writer.writeEndElement();
    }

    /**
     * 
     * @param boundary
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeInnerBoundaryIs(Boundary boundary) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_INNER_BOUNDARY_IS);
        this.writeBoundary(boundary);
        writer.writeEndElement();
    }

    /**
     *
     * @param boundary
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeBoundary(Boundary boundary) 
            throws XMLStreamException, KmlException{

        if (boundary.getLinearRing() != null){
            this.writeLinearRing(boundary.getLinearRing());
        }
        this.writeStandardExtensionLevel(
                boundary.extensions(),
                Names.BOUNDARY);
    }

    /**
     *
     * @param lineString
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeLineString(LineString lineString) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_LINE_STRING);
        this.writeCommonAbstractGeometry(lineString);
        this.writeExtrude(lineString.getExtrude());
        this.writeTessellate(lineString.getTessellate());
        if (lineString.getAltitudeMode() != null){
            this.writeAltitudeMode(lineString.getAltitudeMode());
        }

        this.writeCoordinates(lineString.getCoordinateSequence());

        this.writeStandardExtensionLevel(
                lineString.extensions(),
                Names.LINE_STRING);
        writer.writeEndElement();
    }

    /**
     *
     * @param linearRing
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeLinearRing(LinearRing linearRing) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_LINEAR_RING);
        this.writeCommonAbstractGeometry(linearRing);
        this.writeExtrude(linearRing.getExtrude());
        this.writeTessellate(linearRing.getTessellate());
        if (linearRing.getAltitudeMode() != null){
            this.writeAltitudeMode(linearRing.getAltitudeMode());
        }
        this.writeCoordinates(linearRing.getCoordinateSequence());
        this.writeStandardExtensionLevel(
                linearRing.extensions(),
                Names.LINEAR_RING);
        writer.writeEndElement();
    }

    /**
     *
     * @param multiGeometry
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeMultiGeometry(MultiGeometry multiGeometry)
            throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_KML, TAG_MULTI_GEOMETRY);
        this.writeCommonAbstractGeometry(multiGeometry);
        for (AbstractGeometry abstractGeometry : multiGeometry.getGeometries()){
            this.writeAbstractGeometry(abstractGeometry);
        }
        this.writeStandardExtensionLevel(
                multiGeometry.extensions(),
                Names.MULTI_GEOMETRY);
        writer.writeEndElement();
    }


    /**
     *
     * @{@inheritDoc }
     */
    private void writePoint(Point point) 
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_KML, TAG_POINT);
        this.writeCommonAbstractGeometry(point);
        this.writeExtrude(point.getExtrude());
        if (point.getAltitudeMode() != null){
            this.writeAltitudeMode(point.getAltitudeMode());
        }
        if (point.getCoordinateSequence() != null){
            this.writeCoordinates(point.getCoordinateSequence());
        }
        this.writeStandardExtensionLevel(
                point.extensions(),
                Names.POINT);
        writer.writeEndElement();
    }

    /**
     * 
     * @param coordinates
     * @throws XMLStreamException
     */
    public void writeCoordinates(Coordinates coordinates) 
            throws XMLStreamException{

        writer.writeStartElement(URI_KML, TAG_COORDINATES);
        writer.writeCharacters(KmlUtilities.toString(coordinates));
        writer.writeEndElement();
    }

    /**
     *
     * @param extrude
     * @throws XMLStreamException
     */
    private void writeExtrude(boolean extrude) 
            throws XMLStreamException{

        if (DEF_EXTRUDE != extrude){
            writer.writeStartElement(URI_KML, TAG_EXTRUDE);
            if(extrude){
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param visibility
     * @throws XMLStreamException
     */
    private void writeVisibility(Boolean v) 
            throws XMLStreamException{

        boolean visibility = v.booleanValue();
        if (DEF_VISIBILITY != visibility){
            writer.writeStartElement(URI_KML, TAG_VISIBILITY);
            if(visibility){
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param open
     * @throws XMLStreamException
     */
    private void writeOpen(Boolean o) 
            throws XMLStreamException{

        boolean open = o.booleanValue();
        if (DEF_OPEN != open){
            writer.writeStartElement(URI_KML, TAG_OPEN);
            if(open){
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param fill
     * @throws XMLStreamException
     */
    private void writeFill(boolean fill) 
            throws XMLStreamException{
        
        if (DEF_FILL != fill){
            writer.writeStartElement(URI_KML, TAG_FILL);
            if(fill){
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
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
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
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
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param refreshVisibility
     * @throws XMLStreamException
     */
    private void writeRefreshVisibility(Boolean rv) throws XMLStreamException{
        boolean refreshVisibility = rv.booleanValue();
        if (DEF_REFRESH_VISIBILITY != refreshVisibility){
            writer.writeStartElement(URI_KML, TAG_REFRESH_VISIBILITY);
            if(refreshVisibility){
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     * 
     * @param flyToView
     * @throws XMLStreamException
     */
    private void writeFlyToView(Boolean ftv) throws XMLStreamException{
        boolean flyToView = ftv.booleanValue();
        if (DEF_FLY_TO_VIEW != flyToView){
            writer.writeStartElement(URI_KML, TAG_FLY_TO_VIEW);
            if(flyToView){
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
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
    public void writeHref(String href) throws XMLStreamException{
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
    public void writeBegin(Calendar begin) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_BEGIN);
        this.writeCalendar(begin);
        writer.writeEndElement();
    }

    /**
     *
     * @param end
     * @throws XMLStreamException
     */
    public void writeEnd(Calendar end) throws XMLStreamException {
        writer.writeStartElement(URI_KML, TAG_END);
        this.writeCalendar(end);
        writer.writeEndElement();
    }

    /**
     *
     * @param when
     * @throws XMLStreamException
     */
    public void writeWhen(Calendar when) throws XMLStreamException {
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
    private void writeAltitudeMode(AltitudeMode altitudeMode) throws XMLStreamException, KmlException{
        if(DEF_ALTITUDE_MODE != altitudeMode){
            if (altitudeMode instanceof org.geotoolkit.data.kml.model.EnumAltitudeMode){
                writer.writeStartElement(URI_KML, TAG_ALTITUDE_MODE);
                writer.writeCharacters(altitudeMode.getAltitudeMode());
                writer.writeEndElement();
            } else {
                for(StaxStreamWriter candidate : this.extensionWriters){
                    if(((KmlExtensionWriter) candidate).canHandleComplex(null, altitudeMode)){
                        ((KmlExtensionWriter) candidate).writeComplexExtensionElement(altitudeMode);
                    }
                }
            }
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
        if (KmlUtilities.isFiniteNumber(vec2.getX()) && DEF_VEC2_X != vec2.getX()){
            writer.writeAttribute(ATT_X, Double.toString(vec2.getX()));
        }
        if (KmlUtilities.isFiniteNumber(vec2.getY()) && DEF_VEC2_Y != vec2.getY()){
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
     * UTILITARY METHODS
     */

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

    /*
     * WRITING EXTENSIONS METHODS
     */

    private void writeComplexExtensionsScheduler(Extensions.Names ext, List<Object> objectExtensions)
            throws KmlException, XMLStreamException{
        for(Object object : objectExtensions){
            for(StaxStreamWriter candidate : this.extensionWriters){
                if(((KmlExtensionWriter) candidate).canHandleComplex(ext, object)){
                    ((KmlExtensionWriter) candidate).writeComplexExtensionElement(object);
                }
            }
        }
    }

    private void writeSimpleExtensionsScheduler(Extensions.Names ext, List<SimpleTypeContainer> simpleExtensions)
            throws KmlException, XMLStreamException{
        for(SimpleTypeContainer object : simpleExtensions){
            for(StaxStreamWriter candidate : this.extensionWriters){
                if(((KmlExtensionWriter) candidate).canHandleSimple(ext, object.getTagName())){
                    ((KmlExtensionWriter) candidate).writeSimpleExtensionElement(object);
                }
            }
        }
    }


    private void writeStandardExtensionLevel(Extensions extensions, Names level)
            throws KmlException, XMLStreamException{

        this.writeComplexExtensionsScheduler(
                level, extensions.complexes(level));
        this.writeSimpleExtensionsScheduler(
                level, extensions.simples(level));
    }

    private void writeDataScheduler(List<Object> objectExtensions)
            throws KmlException, XMLStreamException{
        for(Object object : objectExtensions){
            for(StaxStreamWriter candidate : this.dataWriters){
                if(((KmlExtensionWriter) candidate).canHandleComplex(null, object)){
                    ((KmlExtensionWriter) candidate).writeComplexExtensionElement(object);
                } else if(((KmlExtensionWriter) candidate).canHandleSimple(null, ((SimpleTypeContainer) object).getTagName())){
                    ((KmlExtensionWriter) candidate).writeSimpleExtensionElement((SimpleTypeContainer) object);
                }
            }
        }
    }

}

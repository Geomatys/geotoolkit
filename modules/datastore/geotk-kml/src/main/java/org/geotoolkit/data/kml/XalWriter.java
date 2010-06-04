package org.geotoolkit.data.kml;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xal.AddressIdentifier;
import org.geotoolkit.data.model.xal.AddressLines;
import org.geotoolkit.data.model.xal.AdministrativeArea;
import org.geotoolkit.data.model.xal.Country;
import org.geotoolkit.data.model.xal.GenericTypedGrPostal;
import org.geotoolkit.data.model.xal.GrPostal;
import org.geotoolkit.data.model.xal.Locality;
import org.geotoolkit.data.model.xal.PostalServiceElements;
import org.geotoolkit.data.model.xal.SortingCode;
import org.geotoolkit.data.model.xal.Thoroughfare;
import org.geotoolkit.data.model.xal.Xal;
import org.geotoolkit.xml.StaxStreamWriter;
import org.omg.IOP.TAG_INTERNET_IOP;
import static org.geotoolkit.data.model.XalModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class XalWriter extends StaxStreamWriter {

    public XalWriter(File file){
        super();
        this.initSource(file);
    }

    public XalWriter(){
        super();
    }

    public void setWriter(XMLStreamWriter writer){this.writer = writer;}

    public XMLStreamWriter getWriter(){return this.writer;}

    private void initSource(Object o) {
        System.setProperty("javax.xml.stream.XMLOutputFactory", "com.ctc.wstx.stax.WstxOutputFactory");
        try {
            //this.outputFactory = XMLOutputFactory.newInstance();
            this.setOutput(o);
        } catch (IOException ex) {
            Logger.getLogger(KmlWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(KmlWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * <p>This method writes a xAL 2.0 document into the file assigned to the KmlWriter.</p>
     *
     * @param xal The Kml object to write.
     */
    public void write(Xal xal) {
        try {

            // FACULTATIF : INDENTATION DE LA SORTIE
            //streamWriter = new IndentingXMLStreamWriter(streamWriter);

            writer.writeStartDocument("UTF-8", "1.0");
            writer.setDefaultNamespace(URI_XAL);
            writer.writeStartElement(URI_XAL,TAG_XAL);
            /*writer.writeDefaultNamespace(URI_XAL);
            streamWriter.writeNamespace(PREFIX_XSI, URI_XSI);
            streamWriter.writeAttribute(URI_XSI,
                    "schemaLocation",
                    URI_KML+" C:/Users/w7mainuser/Documents/OGC_SCHEMAS/sld/1.1.0/StyledLayerDescriptor.xsd");
            streamWriter.writeAttribute("version", "0");*/
            this.writeXal(xal);
            writer.writeEndElement();
            writer.writeEndDocument();

            writer.flush();
            writer.close();

        } catch (XMLStreamException ex) {
            Logger.getLogger(KmlWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param xal The Xal object to write.
     * @throws XMLStreamException
     */
    private void writeXal(Xal xal) throws XMLStreamException{
        if (xal.getVersion() != null) writer.writeAttribute(ATT_VERSION, xal.getVersion());
        for(AddressDetails ad : xal.getAddressDetails()){
            this.writeAddressDetails(ad);
        }
    }

    /**
     *
     * @param addressDetails
     * @throws XMLStreamException
     */
    public void writeAddressDetails(AddressDetails addressDetails) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_ADDRESS_DETAILS);
        if (addressDetails.getAddressType() != null){
            writer.writeAttribute(ATT_ADDRESS_TYPE, addressDetails.getAddressType());
        }
        if (addressDetails.getCurrentStatus() != null){
            writer.writeAttribute(ATT_CURRENT_STATUS, addressDetails.getCurrentStatus());
        }
        if (addressDetails.getValidFromDate() != null){
            writer.writeAttribute(ATT_VALID_FROM_DATE, addressDetails.getValidFromDate());
        }
        if (addressDetails.getValidToDate() != null){
            writer.writeAttribute(ATT_VALID_TO_DATE, addressDetails.getValidToDate());
        }
        if (addressDetails.getUsage() != null){
            writer.writeAttribute(ATT_USAGE, addressDetails.getUsage());
        }
        if (addressDetails.getGrPostal() != null){
            this.writeGrPostal(addressDetails.getGrPostal());
        }
        if (addressDetails.getAddressDetailsKey() != null){
            writer.writeAttribute(ATT_ADDRESS_DETAILS_KEY, addressDetails.getAddressDetailsKey());
        }
        
        if (addressDetails.getPostalServiceElements() != null){
            this.writePostalServiceElements(addressDetails.getPostalServiceElements());
        }
        if (addressDetails.getAddress() != null){
            this.writeAddress(addressDetails.getAddress());
        }
         if (addressDetails.getAddressLines() != null){
            this.writeAddressLines(addressDetails.getAddressLines());
        }
        if (addressDetails.getCountry() != null){
            this.writeCountry(addressDetails.getCountry());
        }
        if (addressDetails.getAdministrativeArea() != null){
            this.writeAdministrativeArea(addressDetails.getAdministrativeArea());
        }
        if (addressDetails.getLocality() != null){
            this.writeLocality(addressDetails.getLocality());
        }
        if (addressDetails.getThoroughfare() != null){
            this.writeThoroughfare(addressDetails.getThoroughfare());
        }
        
        writer.writeEndElement();
    }

    
    private void writePostalServiceElements(PostalServiceElements postalServiceElements) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_POSTAL_SERVICE_ELEMENTS);
        if (postalServiceElements.getType() != null){
            writer.writeAttribute(ATT_ADDRESS_TYPE, postalServiceElements.getType());
        }
        if (postalServiceElements.getAddressIdentifiers() != null){
            for (AddressIdentifier addressIdentifier : postalServiceElements.getAddressIdentifiers()){
                this.writeAddressIdentifier(addressIdentifier);
            }
        }
        if (postalServiceElements.getEndorsementLineCode() != null){
            this.writeEndorsementLineCode(postalServiceElements.getEndorsementLineCode());
        }
        if (postalServiceElements.getKeyLineCode() != null){
            this.writeKeyLineCode(postalServiceElements.getKeyLineCode());
        }
        if (postalServiceElements.getBarcode() != null){
            this.writeBarcode(postalServiceElements.getBarcode());
        }
        if (postalServiceElements.getSortingCode() != null){
            this.writeSortingCode(postalServiceElements.getSortingCode());
        }
        if (postalServiceElements.getAddressLatitude() != null){
            this.writeAddressLatitude(postalServiceElements.getAddressLatitude());
        }
        if (postalServiceElements.getAddressLatitudeDirection() != null){
            this.writeAddressLatitudeDirection(postalServiceElements.getAddressLatitudeDirection());
        }
        if (postalServiceElements.getAddressLongitude() != null){
            this.writeAddressLongitude(postalServiceElements.getAddressLongitude());
        }
        if (postalServiceElements.getAddressLongitudeDirection() != null){
            this.writeAddressLongitudeDirection(postalServiceElements.getAddressLongitudeDirection());
        }
        if (postalServiceElements.getSupplementaryPostalServiceData() != null){
            for (GenericTypedGrPostal data : postalServiceElements.getSupplementaryPostalServiceData()){
                this.writeSupplementaryPostalServiceData(data);
            }
        }
        writer.writeEndElement();
    }

    private void writeAddress(GenericTypedGrPostal address) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_ADDRESS);
        this.writeGenericTypedGrPostal(address);
        writer.writeEndElement();
    }

    private void writeSupplementaryPostalServiceData(GenericTypedGrPostal data) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_SUPPLEMENTARY_POSTAL_SERVICE_DATA);
        this.writeGenericTypedGrPostal(data);
        writer.writeEndElement();
    }

    private void writeAddressLongitude(GenericTypedGrPostal address) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_ADDRESS_LONGITUDE);
        this.writeGenericTypedGrPostal(address);
        writer.writeEndElement();
    }

    private void writeAddressLongitudeDirection(GenericTypedGrPostal address) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_ADDRESS_LONGITUDE_DIRECTION);
        this.writeGenericTypedGrPostal(address);
        writer.writeEndElement();
    }

    private void writeAddressLatitude(GenericTypedGrPostal address) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_ADDRESS_LATITUDE);
        this.writeGenericTypedGrPostal(address);
        writer.writeEndElement();
    }

    private void writeAddressLatitudeDirection(GenericTypedGrPostal address) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_ADDRESS_LATITUDE_DIRECTION);
        this.writeGenericTypedGrPostal(address);
        writer.writeEndElement();
    }

    private void writeBarcode(GenericTypedGrPostal barcode) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_ENDORSEMENT_LINE_CODE);
        this.writeGenericTypedGrPostal(barcode);
        writer.writeEndElement();
    }

    private void writeEndorsementLineCode(GenericTypedGrPostal lineCode) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_ENDORSEMENT_LINE_CODE);
        this.writeGenericTypedGrPostal(lineCode);
        writer.writeEndElement();
    }

    private void writeKeyLineCode(GenericTypedGrPostal lineCode) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_KEY_LINE_CODE);
        this.writeGenericTypedGrPostal(lineCode);
        writer.writeEndElement();
    }

    private void writeAddressLines(AddressLines addressLines) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_ADDRESS_LINES);
        for (GenericTypedGrPostal addressLine : addressLines.getAddressLines()){
            this.writeAddressLine(addressLine);
        }
        writer.writeEndElement();
    }

    private void writeAddressLine(GenericTypedGrPostal addressline) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_ADDRESS_LINE);
        this.writeGenericTypedGrPostal(addressline);
        writer.writeEndElement();
    }

    private void writeCountry(Country country) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_COUNTRY);
        writer.writeEndElement();
    }

    private void writeAdministrativeArea(AdministrativeArea administrativeArea) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_ADMINISTRATIVE_AREA);
        writer.writeEndElement();
    }

    private void writeLocality(Locality locality) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_LOCALITY);
        writer.writeEndElement();
    }

    private void writeThoroughfare(Thoroughfare thoroughfare) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_THOROUGHFARE);
        writer.writeEndElement();
    }

    private void writeGenericTypedGrPostal(GenericTypedGrPostal generic) throws XMLStreamException{
        if (generic.getType() != null){
            writer.writeAttribute(ATT_TYPE, generic.getType());
        }
        if (generic.getGrPostal() != null){
            this.writeGrPostal(generic.getGrPostal());
        }
        if (generic.getContent() != null){
            writer.writeCharacters(generic.getContent());
        }
    }

    private void writeSortingCode(SortingCode sortingCode) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_SORTING_CODE);
        if (sortingCode.getType() != null){
            writer.writeAttribute(ATT_TYPE, sortingCode.getType());
        }
        if (sortingCode.getGrPostal() != null){
            this.writeGrPostal(sortingCode.getGrPostal());
        }
        writer.writeEndElement();
    }

    private void writeAddressIdentifier(AddressIdentifier identifier) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_ADDRESS_IDENTIFIER);
        if (identifier.getIdentifierType() != null){
            writer.writeAttribute(ATT_IDENTIFIER_TYPE, identifier.getIdentifierType());
        }
        if (identifier.getType() != null){
            writer.writeAttribute(ATT_TYPE, identifier.getType());
        }
        if (identifier.getGrPostal() != null){
            this.writeGrPostal(identifier.getGrPostal());
        }
        if (identifier.getContent() != null){
            writer.writeCharacters(identifier.getContent());
        }
        writer.writeEndElement();
    }

    private void writeGrPostal(GrPostal grPostal) throws XMLStreamException{
        if (grPostal.getCode() != null){
            writer.writeAttribute(ATT_CODE, grPostal.getCode());
        }
    }


}
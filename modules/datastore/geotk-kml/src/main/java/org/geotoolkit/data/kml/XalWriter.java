package org.geotoolkit.data.kml;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xal.AddressIdentifier;
import org.geotoolkit.data.model.xal.AddressLines;
import org.geotoolkit.data.model.xal.AdministrativeArea;
import org.geotoolkit.data.model.xal.Country;
import org.geotoolkit.data.model.xal.CountryNameCode;
import org.geotoolkit.data.model.xal.GenericTypedGrPostal;
import org.geotoolkit.data.model.xal.GrPostal;
import org.geotoolkit.data.model.xal.Locality;
import org.geotoolkit.data.model.xal.PostOffice;
import org.geotoolkit.data.model.xal.PostalCode;
import org.geotoolkit.data.model.xal.PostalServiceElements;
import org.geotoolkit.data.model.xal.SortingCode;
import org.geotoolkit.data.model.xal.SubAdministrativeArea;
import org.geotoolkit.data.model.xal.Thoroughfare;
import org.geotoolkit.data.model.xal.Xal;
import org.geotoolkit.xml.StaxStreamWriter;
import static org.geotoolkit.data.model.XalModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class XalWriter extends StaxStreamWriter {

    public XalWriter(){
        super();
    }

    public void setWriter(XMLStreamWriter writer){this.writer = writer;}

    @Override
    public XMLStreamWriter getWriter(){return this.writer;}

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

    private void writeAdministrativeAreaName(GenericTypedGrPostal name) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_ADMINISTRATIVE_AREA_NAME);
        this.writeGenericTypedGrPostal(name);
        writer.writeEndElement();
    }

    private void writeCountry(Country country) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_COUNTRY);
        if (country.getAddressLines() != null){
            for (GenericTypedGrPostal addressLine : country.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (country.getCountryNameCodes() != null){
            for (CountryNameCode countryNameCode : country.getCountryNameCodes()){
                this.writeCountryNameCode(countryNameCode);
            }
        }
        if (country.getCountryNames() != null){
            for (GenericTypedGrPostal countryName : country.getCountryNames()){
                this.writeCountryName(countryName);
            }
        }
        if (country.getAdministrativeArea() != null){
            this.writeAdministrativeArea(country.getAdministrativeArea());
        }
        if (country.getLocality() != null){
            this.writeLocality(country.getLocality());
        }
        if (country.getThoroughfare() != null){
            this.writeThoroughfare(country.getThoroughfare());
        }
        writer.writeEndElement();
    }

    private void writeCountryName (GenericTypedGrPostal countryName) throws XMLStreamException{
        writer.writeStartElement(URI_XAL, TAG_COUNTRY_NAME);
        this.writeGenericTypedGrPostal(countryName);
        writer.writeEndElement();
    }

    private void writeCountryNameCode(CountryNameCode countryNameCode) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_COUNTRY_NAME_CODE);
        if (countryNameCode.getScheme() != null){
            writer.writeAttribute(ATT_SCHEME, countryNameCode.getScheme());
        }
        if (countryNameCode.getGrPostal() != null){
            this.writeGrPostal(countryNameCode.getGrPostal());
        }
        if (countryNameCode.getContent() != null){
            writer.writeCharacters(countryNameCode.getContent());
        }
        writer.writeEndElement();
    }

    private void writeAdministrativeArea(AdministrativeArea administrativeArea) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_ADMINISTRATIVE_AREA);
        if (administrativeArea.getType() != null){
            writer.writeAttribute(ATT_TYPE, administrativeArea.getType());
        }
        if (administrativeArea.getUsageType() != null){
            writer.writeAttribute(ATT_USAGE_TYPE, administrativeArea.getUsageType());
        }
        if (administrativeArea.getIndicator() != null){
            writer.writeAttribute(ATT_INDICATOR, administrativeArea.getIndicator());
        }
        if (administrativeArea.getAddressLines() != null){
            for (GenericTypedGrPostal addressLine : administrativeArea.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (administrativeArea.getAdministrativeAreaNames() != null){
            for (GenericTypedGrPostal name : administrativeArea.getAdministrativeAreaNames()){
                this.writeAdministrativeAreaName(name);
            }
        }
        if (administrativeArea.getSubAdministrativeArea() != null){
            this.writeSubAdministrativeArea(administrativeArea.getSubAdministrativeArea());
        }
        if (administrativeArea.getLocality() != null){
            this.writeLocality(administrativeArea.getLocality());
        }
        else if (administrativeArea.getPostOffice() != null){
            this.writePostOffice(administrativeArea.getPostOffice());
        } else if (administrativeArea.getPostalCode() != null){
            this.writePostalCode(administrativeArea.getPostalCode());
        }
        writer.writeEndElement();
    }

    private void writePostOffice(PostOffice postOffice) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_POST_OFFICE);
        writer.writeEndElement();
    }
    
     private void writePostalCode(PostalCode postalCode) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_POSTAL_CODE);
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

    private void writeSubAdministrativeArea(SubAdministrativeArea subAdministrativeArea) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_SUB_ADMINISTRATIVE_AREA);
        if (subAdministrativeArea.getType() != null){
            writer.writeAttribute(ATT_TYPE, subAdministrativeArea.getType());
        }
        if (subAdministrativeArea.getUsageType() != null){
            writer.writeAttribute(ATT_USAGE_TYPE, subAdministrativeArea.getUsageType());
        }
        if (subAdministrativeArea.getIndicator() != null){
            writer.writeAttribute(ATT_INDICATOR, subAdministrativeArea.getIndicator());
        }
        if (subAdministrativeArea.getAddressLines() != null){
            for (GenericTypedGrPostal addressLine : subAdministrativeArea.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (subAdministrativeArea.getSubAdministrativeAreaNames() != null){
            for (GenericTypedGrPostal name : subAdministrativeArea.getSubAdministrativeAreaNames()){
                this.writeAdministrativeAreaName(name);
            }
        }
        if (subAdministrativeArea.getLocality() != null){
            this.writeLocality(subAdministrativeArea.getLocality());
        }
        else if (subAdministrativeArea.getPostOffice() != null){
            this.writePostOffice(subAdministrativeArea.getPostOffice());
        } else if (subAdministrativeArea.getPostalCode() != null){
            this.writePostalCode(subAdministrativeArea.getPostalCode());
        }
        writer.writeEndElement();
    }


}
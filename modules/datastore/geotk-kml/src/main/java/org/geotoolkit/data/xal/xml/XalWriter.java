package org.geotoolkit.data.xal.xml;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.data.xal.model.AddressDetails;
import org.geotoolkit.data.xal.model.AddressIdentifier;
import org.geotoolkit.data.xal.model.AddressLines;
import org.geotoolkit.data.xal.model.AdministrativeArea;
import org.geotoolkit.data.xal.model.BuildingName;
import org.geotoolkit.data.xal.model.Country;
import org.geotoolkit.data.xal.model.CountryNameCode;
import org.geotoolkit.data.xal.model.Department;
import org.geotoolkit.data.xal.model.DependentLocality;
import org.geotoolkit.data.xal.model.DependentLocalityNumber;
import org.geotoolkit.data.xal.model.DependentThoroughfare;
import org.geotoolkit.data.xal.model.Firm;
import org.geotoolkit.data.xal.model.GenericTypedGrPostal;
import org.geotoolkit.data.xal.model.GrPostal;
import org.geotoolkit.data.xal.model.LargeMailUser;
import org.geotoolkit.data.xal.model.LargeMailUserIdentifier;
import org.geotoolkit.data.xal.model.LargeMailUserName;
import org.geotoolkit.data.xal.model.Locality;
import org.geotoolkit.data.xal.model.MailStop;
import org.geotoolkit.data.xal.model.MailStopNumber;
import org.geotoolkit.data.xal.model.PostBox;
import org.geotoolkit.data.xal.model.PostBoxNumber;
import org.geotoolkit.data.xal.model.PostBoxNumberExtension;
import org.geotoolkit.data.xal.model.PostBoxNumberPrefix;
import org.geotoolkit.data.xal.model.PostBoxNumberSuffix;
import org.geotoolkit.data.xal.model.PostOffice;
import org.geotoolkit.data.xal.model.PostOfficeNumber;
import org.geotoolkit.data.xal.model.PostTown;
import org.geotoolkit.data.xal.model.PostTownSuffix;
import org.geotoolkit.data.xal.model.PostalCode;
import org.geotoolkit.data.xal.model.PostalCodeNumberExtension;
import org.geotoolkit.data.xal.model.PostalRoute;
import org.geotoolkit.data.xal.model.PostalRouteNumber;
import org.geotoolkit.data.xal.model.PostalServiceElements;
import org.geotoolkit.data.xal.model.Premise;
import org.geotoolkit.data.xal.model.PremiseLocation;
import org.geotoolkit.data.xal.model.PremiseName;
import org.geotoolkit.data.xal.model.PremiseNumber;
import org.geotoolkit.data.xal.model.PremiseNumberPrefix;
import org.geotoolkit.data.xal.model.PremiseNumberRange;
import org.geotoolkit.data.xal.model.PremiseNumberRangeFrom;
import org.geotoolkit.data.xal.model.PremiseNumberRangeTo;
import org.geotoolkit.data.xal.model.PremiseNumberSuffix;
import org.geotoolkit.data.xal.model.SortingCode;
import org.geotoolkit.data.xal.model.SubAdministrativeArea;
import org.geotoolkit.data.xal.model.SubPremise;
import org.geotoolkit.data.xal.model.SubPremiseLocation;
import org.geotoolkit.data.xal.model.SubPremiseName;
import org.geotoolkit.data.xal.model.SubPremiseNumber;
import org.geotoolkit.data.xal.model.SubPremiseNumberPrefix;
import org.geotoolkit.data.xal.model.SubPremiseNumberSuffix;
import org.geotoolkit.data.xal.model.Thoroughfare;
import org.geotoolkit.data.xal.model.ThoroughfareNumber;
import org.geotoolkit.data.xal.model.ThoroughfareNumberFrom;
import org.geotoolkit.data.xal.model.ThoroughfareNumberPrefix;
import org.geotoolkit.data.xal.model.ThoroughfareNumberRange;
import org.geotoolkit.data.xal.model.ThoroughfareNumberSuffix;
import org.geotoolkit.data.xal.model.ThoroughfareNumberTo;
import org.geotoolkit.data.xal.model.Xal;
import org.geotoolkit.data.xal.model.XalException;
import org.geotoolkit.xml.StaxStreamWriter;
import static org.geotoolkit.data.xal.xml.XalModelConstants.*;

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
     * @param xal The Xal object to write.
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

        } catch (XalException ex) {
            Logger.getLogger(XalWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(KmlWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param xal The Xal object to write.
     * @throws XMLStreamException
     */
    private void writeXal(Xal xal) throws XMLStreamException, XalException{
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
    public void writeAddressDetails(AddressDetails addressDetails) throws XMLStreamException, XalException{
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

    private void writeCountry(Country country) throws XMLStreamException, XalException{
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

    private void writeFirmName (GenericTypedGrPostal firmName) throws XMLStreamException{
        writer.writeStartElement(URI_XAL, TAG_FIRM_NAME);
        this.writeGenericTypedGrPostal(firmName);
        writer.writeEndElement();
    }

    private void writeDepartmentName (GenericTypedGrPostal departmentName) throws XMLStreamException{
        writer.writeStartElement(URI_XAL, TAG_DEPARTMENT_NAME);
        this.writeGenericTypedGrPostal(departmentName);
        writer.writeEndElement();
    }

    private void writeMailStopName(GenericTypedGrPostal mailStopName) throws XMLStreamException {
        writer.writeStartElement(URI_XAL, TAG_MAIL_STOP_NAME);
        this.writeGenericTypedGrPostal(mailStopName);
        writer.writeEndElement();
    }

    private void writePostalCodeNumber(GenericTypedGrPostal postalCodeNumber) throws XMLStreamException {
        writer.writeStartElement(URI_XAL, TAG_POSTAL_CODE_NUMBER);
        this.writeGenericTypedGrPostal(postalCodeNumber);
        writer.writeEndElement();
    }

    private void writeLocalityName(GenericTypedGrPostal localityName) throws XMLStreamException {
        writer.writeStartElement(URI_XAL, TAG_LOCALITY_NAME);
        this.writeGenericTypedGrPostal(localityName);
        writer.writeEndElement();
    }

    private void writeDependentLocalityName(GenericTypedGrPostal dependentLocalityName) throws XMLStreamException {
        writer.writeStartElement(URI_XAL, TAG_DEPENDENT_LOCALITY_NAME);
        this.writeGenericTypedGrPostal(dependentLocalityName);
        writer.writeEndElement();
    }

    private void writeTownName(GenericTypedGrPostal postTownName) throws XMLStreamException {
        writer.writeStartElement(URI_XAL, TAG_POST_TOWN_NAME);
        this.writeGenericTypedGrPostal(postTownName);
        writer.writeEndElement();
    }

    private void writePostalRouteName(GenericTypedGrPostal postalRouteName) throws XMLStreamException {
        writer.writeStartElement(URI_XAL, TAG_POSTAL_ROUTE_NAME);
        this.writeGenericTypedGrPostal(postalRouteName);
        writer.writeEndElement();
    }

    private void writePostOfficeName(GenericTypedGrPostal postOfficeName) throws XMLStreamException {
        writer.writeStartElement(URI_XAL, TAG_POST_OFFICE_NAME);
        this.writeGenericTypedGrPostal(postOfficeName);
        writer.writeEndElement();
    }

    private void writeThoroughfarePostDirection(GenericTypedGrPostal thoroughfarePostDirection) throws XMLStreamException {
        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_POST_DIRECTION);
        this.writeGenericTypedGrPostal(thoroughfarePostDirection);
        writer.writeEndElement();
    }

    private void writeThoroughfarePreDirection(GenericTypedGrPostal thoroughfarePreDirection) throws XMLStreamException {
        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_PRE_DIRECTION);
        this.writeGenericTypedGrPostal(thoroughfarePreDirection);
        writer.writeEndElement();
    }

    private void writeThoroughfareName(GenericTypedGrPostal name) throws XMLStreamException {
        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_NAME);
        this.writeGenericTypedGrPostal(name);
        writer.writeEndElement();
    }

    private void writeThoroughfareTrailingType(GenericTypedGrPostal thoroughfareTrailingType) throws XMLStreamException {
        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_TRAILING_TYPE);
        this.writeGenericTypedGrPostal(thoroughfareTrailingType);
        writer.writeEndElement();
    }

    private void writeThoroughfareLeadingType(GenericTypedGrPostal thoroughfareLeadingType) throws XMLStreamException {
        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_LEADING_TYPE);
        this.writeGenericTypedGrPostal(thoroughfareLeadingType);
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

    private void writeAdministrativeArea(AdministrativeArea administrativeArea) throws XMLStreamException, XalException{
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

    private void writeDependentLocality(DependentLocality dependentLocality) throws XMLStreamException, XalException {
        writer.writeStartElement(URI_XAL,TAG_DEPENDENT_LOCALITY);
        if (dependentLocality.getType() != null){
            writer.writeAttribute(ATT_TYPE, dependentLocality.getType());
        }
        if (dependentLocality.getUsageType() != null){
            writer.writeAttribute(ATT_USAGE_TYPE, dependentLocality.getUsageType());
        }
        if (dependentLocality.getConnector() != null){
            writer.writeAttribute(ATT_CONNECTOR, dependentLocality.getConnector());
        }
        if (dependentLocality.getIndicator() != null){
            writer.writeAttribute(ATT_INDICATOR, dependentLocality.getIndicator());
        }
        if (dependentLocality.getAddressLines() != null){
            for (GenericTypedGrPostal addressLine : dependentLocality.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (dependentLocality.getDependentLocalityNames() != null){
            for (GenericTypedGrPostal dependentLocalityName : dependentLocality.getDependentLocalityNames()){
                this.writeDependentLocalityName(dependentLocalityName);
            }
        }
        if (dependentLocality.getDependentLocalityNumber() != null){
            this.writeDependentLocalityNumber(dependentLocality.getDependentLocalityNumber());
        }
        if (dependentLocality.getPostBox() != null){
            this.writePostBox(dependentLocality.getPostBox());
        }
        if (dependentLocality.getLargeMailUser() != null){
            this.writeLargeMailUser(dependentLocality.getLargeMailUser());
        }
        if (dependentLocality.getPostOffice() != null){
            this.writePostOffice(dependentLocality.getPostOffice());
        }
        if (dependentLocality.getPostalRoute() != null){
            this.writePostalRoute(dependentLocality.getPostalRoute());
        }
        if (dependentLocality.getThoroughfare() != null){
            this.writeThoroughfare(dependentLocality.getThoroughfare());
        }
        if (dependentLocality.getPremise() != null){
            this.writePremise(dependentLocality.getPremise());
        }
        if (dependentLocality.getDependentLocality() != null){
            this.writeDependentLocality(dependentLocality.getDependentLocality());
        }
        if (dependentLocality.getPostalCode() != null){
            this.writePostalCode(dependentLocality.getPostalCode());
        }
        writer.writeEndElement();
    }

    private void writeLocality(Locality locality) throws XMLStreamException, XalException{
        writer.writeStartElement(URI_XAL,TAG_LOCALITY);
        if (locality.getType() != null){
            writer.writeAttribute(ATT_TYPE, locality.getType());
        }
        if (locality.getUsageType() != null){
            writer.writeAttribute(ATT_USAGE_TYPE, locality.getUsageType());
        }
        if (locality.getIndicator() != null){
            writer.writeAttribute(ATT_INDICATOR, locality.getIndicator());
        }
        if (locality.getAddressLines() != null){
            for (GenericTypedGrPostal addressLine : locality.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (locality.getLocalityNames() != null){
            for (GenericTypedGrPostal localityName : locality.getLocalityNames()){
                this.writeLocalityName(localityName);
            }
        }
        if (locality.getPostBox() != null){
            this.writePostBox(locality.getPostBox());
        }
        if (locality.getLargeMailUser() != null){
            this.writeLargeMailUser(locality.getLargeMailUser());
        }
        if (locality.getPostOffice() != null){
            this.writePostOffice(locality.getPostOffice());
        }
        if (locality.getPostalRoute() != null){
            this.writePostalRoute(locality.getPostalRoute());
        }
        if (locality.getThoroughfare() != null){
            this.writeThoroughfare(locality.getThoroughfare());
        }
        if (locality.getPremise() != null){
            this.writePremise(locality.getPremise());
        }
        if (locality.getDependentLocality() != null){
            this.writeDependentLocality(locality.getDependentLocality());
        }
        if (locality.getPostalCode() != null){
            this.writePostalCode(locality.getPostalCode());
        }
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

    private void writeSubAdministrativeArea(SubAdministrativeArea subAdministrativeArea) throws XMLStreamException, XalException {
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

    private void writePostBox(PostBox postBox) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_POST_BOX);
        if (postBox.getType() != null){
            writer.writeAttribute(ATT_TYPE, postBox.getType());
        }
        if (postBox.getIndicator() != null){
            writer.writeAttribute(ATT_INDICATOR, postBox.getIndicator());
        }
        if (postBox.getAddressLines() != null){
            for (GenericTypedGrPostal addressLine : postBox.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (postBox.getPostBoxNumber() != null){
            this.writePostBoxNumber(postBox.getPostBoxNumber());
        }
        if (postBox.getPostBoxNumberPrefix() != null){
            this.writePostBoxNumberPrefix(postBox.getPostBoxNumberPrefix());
        }
        if (postBox.getPostBoxNumberSuffix() != null){
            this.writePostBoxNumberSuffix(postBox.getPostBoxNumberSuffix());
        }
        if (postBox.getPostBoxNumberExtension() != null){
            this.writePostBoxNumberExtension(postBox.getPostBoxNumberExtension());
        }
        if (postBox.getFirm() != null){
            this.writeFirm(postBox.getFirm());
        }
        if (postBox.getPostalCode() != null){
            this.writePostalCode(postBox.getPostalCode());
        }
        writer.writeEndElement();

    }

    private void writePostBoxNumber(PostBoxNumber postBoxNumber) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_POST_BOX_NUMBER);
        if (postBoxNumber.getGrPostal() != null){
            this.writeGrPostal(postBoxNumber.getGrPostal());
        }
        if (postBoxNumber.getContent() != null){
            writer.writeCharacters(postBoxNumber.getContent());
        }
        writer.writeEndElement();
    }

    private void writePostBoxNumberPrefix(PostBoxNumberPrefix postBoxNumberPrefix) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_POST_BOX_NUMBER_PREFIX);
        if (postBoxNumberPrefix.getGrPostal() != null){
            this.writeGrPostal(postBoxNumberPrefix.getGrPostal());
        }
        if (postBoxNumberPrefix.getNumberPrefixSeparator() != null){
           writer.writeAttribute(ATT_NUMBER_PREFIX_SEPARATOR, postBoxNumberPrefix.getNumberPrefixSeparator());
        }
        if (postBoxNumberPrefix.getContent() != null){
            writer.writeCharacters(postBoxNumberPrefix.getContent());
        }
        writer.writeEndElement();
    }

    private void writePostBoxNumberSuffix(PostBoxNumberSuffix postBoxNumberSuffix) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_POST_BOX_NUMBER_SUFFIX);
        if (postBoxNumberSuffix.getGrPostal() != null){
            this.writeGrPostal(postBoxNumberSuffix.getGrPostal());
        }
        if (postBoxNumberSuffix.getNumberSuffixSeparator() != null){
           writer.writeAttribute(ATT_NUMBER_SUFFIX_SEPARATOR, postBoxNumberSuffix.getNumberSuffixSeparator());
        }
        if (postBoxNumberSuffix.getContent() != null){
            writer.writeCharacters(postBoxNumberSuffix.getContent());
        }
        writer.writeEndElement();
    }

    private void writePostBoxNumberExtension(PostBoxNumberExtension postBoxNumberExtension) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_POST_BOX_NUMBER_EXTENSION);
        if (postBoxNumberExtension.getNumberExtensionSeparator() != null){
           writer.writeAttribute(ATT_NUMBER_EXTENSION_SEPARATOR, postBoxNumberExtension.getNumberExtensionSeparator());
        }
        if (postBoxNumberExtension.getContent() != null){
            writer.writeCharacters(postBoxNumberExtension.getContent());
        }
        writer.writeEndElement();
    }

    private void writeFirm(Firm firm) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_FIRM);
        if (firm.getType() != null){
            writer.writeAttribute(ATT_TYPE, firm.getType());
        }
        if (firm.getAddressLines() != null){
            for (GenericTypedGrPostal addressLine : firm.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (firm.getFirmNames() != null){
            for (GenericTypedGrPostal firmName : firm.getFirmNames()){
                this.writeFirmName(firmName);
            }
        }
        if (firm.getDepartments() != null){
            for (Department department : firm.getDepartments()){
                this.writeDepartment(department);
            }
        }
        if (firm.getMailStop() != null){
            this.writeMailStop(firm.getMailStop());
        }
        if (firm.getPostalCode() != null){
            this.writePostalCode(firm.getPostalCode());
        }
        writer.writeEndElement();
    }

    private void writeDepartment(Department department) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_DEPARTMENT);
        if (department.getType() != null){
            writer.writeAttribute(ATT_TYPE, department.getType());
        }
        if (department.getAddressLines() != null){
            for (GenericTypedGrPostal addressLine : department.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (department.getDepartmentNames() != null){
            for (GenericTypedGrPostal firmName : department.getDepartmentNames()){
                this.writeDepartmentName(firmName);
            }
        }
        if (department.getMailStop() != null){
            this.writeMailStop(department.getMailStop());
        }
        if (department.getPostalCode() != null){
            this.writePostalCode(department.getPostalCode());
        }
        writer.writeEndElement();
    }

    private void writeMailStop(MailStop mailStop) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_MAIL_STOP);
        if (mailStop.getType() != null){
            writer.writeAttribute(ATT_TYPE, mailStop.getType());
        }
        if (mailStop.getAddressLines() != null){
            for (GenericTypedGrPostal addressLine : mailStop.getAddressLines()){
                this.writeAddressLine(null);
            }
        }
        if (mailStop.getMailStopNames() != null){
            for (GenericTypedGrPostal mailStopName : mailStop.getMailStopNames()){
                this.writeMailStopName(mailStopName);
            }
        }
        if (mailStop.getMailStopNumber() != null){
            this.writeMailStopNumber(mailStop.getMailStopNumber());
        }
        writer.writeEndElement();
    }

    private void writeMailStopNumber(MailStopNumber mailStopNumber) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_MAIL_STOP);
        if(mailStopNumber.getNameNumberSeparator() != null){
            writer.writeAttribute(ATT_NAME_NUMBER_SEPARATOR, mailStopNumber.getNameNumberSeparator());
        }
        if (mailStopNumber.getGrPostal() != null){
            this.writeGrPostal(mailStopNumber.getGrPostal());
        }
        if (mailStopNumber.getContent() != null){
            writer.writeCharacters(mailStopNumber.getContent());
        }
        writer.writeEndElement();
    }

    private void writePostOffice(PostOffice postOffice) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_POST_OFFICE);

        if (postOffice.getType() != null){
            writer.writeAttribute(ATT_TYPE, postOffice.getType());
        }
        if (postOffice.getIndicator() != null){
            writer.writeAttribute(ATT_INDICATOR, postOffice.getIndicator());
        }
        if (postOffice.getAddressLines() != null){
            for (GenericTypedGrPostal addressLine : postOffice.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (postOffice.getPostOfficeNames() != null){
            for (GenericTypedGrPostal postOfficeName : postOffice.getPostOfficeNames()){
                this.writePostOfficeName(postOfficeName);
            }
        }
        if (postOffice.getPostOfficeNumber() != null){
            this.writePostOfficeNumber(postOffice.getPostOfficeNumber());
        }
        if (postOffice.getPostalRoute() != null){
            this.writePostalRoute(postOffice.getPostalRoute());
        }
        if (postOffice.getPostBox() != null){
            this.writePostBox(postOffice.getPostBox());
        }
        if (postOffice.getPostalCode() != null){
            this.writePostalCode(postOffice.getPostalCode());
        }

        writer.writeEndElement();
    }

     private void writePostalCode(PostalCode postalCode) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_POSTAL_CODE);
        if (postalCode.getType() != null){
            writer.writeAttribute(ATT_TYPE, postalCode.getType());
        }
        if (postalCode.getAddressLines() != null){
            for (GenericTypedGrPostal addressLine : postalCode.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (postalCode.getPostalCodeNumbers() != null){
            for (GenericTypedGrPostal postalCodeNumber : postalCode.getPostalCodeNumbers()){
                this.writePostalCodeNumber(postalCodeNumber);
            }
        }
        if (postalCode.getPostalCodeNumberExtensions() != null){
            for (PostalCodeNumberExtension postalCodeNumberExtension : postalCode.getPostalCodeNumberExtensions()){
                this.writePostalCodeNumberExtension(postalCodeNumberExtension);
            }
        }
        if (postalCode.getPostTown() != null){
            this.writePostTown(postalCode.getPostTown());
        }
        writer.writeEndElement();
    }

    private void writePostalCodeNumberExtension(PostalCodeNumberExtension postalCodeNumberExtension) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_POSTAL_CODE_NUMBER_EXTENSION);
        if (postalCodeNumberExtension.getType() != null){
            writer.writeAttribute(ATT_TYPE, postalCodeNumberExtension.getType());
        }
        if (postalCodeNumberExtension.getNumberExtensionSeparator() != null){
            writer.writeAttribute(ATT_NUMBER_EXTENSION_SEPARATOR, postalCodeNumberExtension.getNumberExtensionSeparator());
        }
        if (postalCodeNumberExtension.getGrPostal() != null){
            this.writeGrPostal(postalCodeNumberExtension.getGrPostal());
        }
        if (postalCodeNumberExtension.getContent() != null){
            writer.writeCharacters(postalCodeNumberExtension.getContent());
        }
        writer.writeEndElement();
    }

    private void writePostTown(PostTown postTown) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_POSTAL_CODE_NUMBER_EXTENSION);
        if (postTown.getType() != null){
            writer.writeAttribute(ATT_TYPE, postTown.getType());
        }
        if (postTown.getAddressLines() != null){
            for (GenericTypedGrPostal addressLine : postTown.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (postTown.getPostTownNames() != null){
            for (GenericTypedGrPostal postTownName : postTown.getPostTownNames()){
                this.writeTownName(postTownName);
            }
        }
        if (postTown.getPostTownSuffix() != null){
            this.writeTownSuffix(postTown.getPostTownSuffix());
        }
        writer.writeEndElement();
    }


    private void writeTownSuffix(PostTownSuffix postTownSuffix) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_POST_TOWN_SUFFIX);
        if (postTownSuffix.getGrPostal() != null){
            this.writeGrPostal(postTownSuffix.getGrPostal());
        }
        if (postTownSuffix.getContent() != null){
            writer.writeCharacters(postTownSuffix.getContent());
        }
        writer.writeEndElement();
    }

    private void writeLargeMailUser(LargeMailUser largeMailUser) throws XMLStreamException, XalException {
        writer.writeStartElement(URI_XAL,TAG_LARGE_MAIL_USER);
        if (largeMailUser.getType() != null){
            writer.writeAttribute(ATT_TYPE, largeMailUser.getType());
        }
        if (largeMailUser.getAddressLines() != null){
            for(GenericTypedGrPostal addressLine : largeMailUser.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (largeMailUser.getLargeMailUserNames() != null){
            for(LargeMailUserName largeMailUserName : largeMailUser.getLargeMailUserNames()){
                this.writeLargeMailUserName(largeMailUserName);
            }
        }
        if (largeMailUser.getLargeMailUserIdentifier() != null){
            this.writeLargeMailUserIdentifier(largeMailUser.getLargeMailUserIdentifier());
        }
        if (largeMailUser.getBuildingNames() != null){
            for (BuildingName buildingName : largeMailUser.getBuildingNames()){
                this.writeBuildingName(buildingName);
            }
        }
        if (largeMailUser.getDepartment() != null){
            this.writeDepartment(largeMailUser.getDepartment());
        }
        if (largeMailUser.getPostBox() != null){
            this.writePostBox(largeMailUser.getPostBox());
        }
        if (largeMailUser.getThoroughfare() != null){
            this.writeThoroughfare(largeMailUser.getThoroughfare());
        }
        if (largeMailUser.getPostalCode() != null){
            this.writePostalCode(largeMailUser.getPostalCode());
        }
        writer.writeEndElement();
    }

    private void writeLargeMailUserName(LargeMailUserName largeMailUserName) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_LARGE_MAIL_USER_NAME);
        if (largeMailUserName.getType() != null){
            writer.writeAttribute(ATT_TYPE, largeMailUserName.getType());
        }
        if (largeMailUserName.getCode() != null){
            writer.writeAttribute(ATT_CODE, largeMailUserName.getCode());
        }
        if (largeMailUserName.getContent() != null){
            writer.writeCharacters(largeMailUserName.getContent());
        }
        writer.writeEndElement();
    }

    private void writeLargeMailUserIdentifier(LargeMailUserIdentifier largeMailUserIdentifier) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_LARGE_MAIL_USER_IDENTIFIER);
        if (largeMailUserIdentifier.getType() != null){
            writer.writeAttribute(ATT_TYPE, largeMailUserIdentifier.getType());
        }
        if (largeMailUserIdentifier.getIndicator() != null){
            writer.writeAttribute(ATT_INDICATOR, largeMailUserIdentifier.getIndicator());
        }
        if (largeMailUserIdentifier.getGrPostal() != null){
            this.writeGrPostal(largeMailUserIdentifier.getGrPostal());
        }
        if (largeMailUserIdentifier.getContent() != null){
            writer.writeCharacters(largeMailUserIdentifier.getContent());
        }
        writer.writeEndElement();
    }

    private void writeBuildingName(BuildingName buildingName) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_BUILDING_NAME);
        if (buildingName.getType() != null){
            writer.writeAttribute(ATT_TYPE, buildingName.getType());
        }
        if (buildingName.getTypeOccurrence() != null){
            writer.writeAttribute(ATT_TYPE_OCCURRENCE, buildingName.getTypeOccurrence().getAfterBeforeEnum());
        }
        if (buildingName.getGrPostal() != null){
            this.writeGrPostal(buildingName.getGrPostal());
        }
        if (buildingName.getContent() != null){
            writer.writeCharacters(buildingName.getContent());
        }
        writer.writeEndElement();
    }

    private void writePremiseName(PremiseName premiseName) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_PREMISE_NAME);
        if (premiseName.getType() != null){
            writer.writeAttribute(ATT_TYPE, premiseName.getType());
        }
        if (premiseName.getTypeOccurrence() != null){
            writer.writeAttribute(ATT_TYPE_OCCURRENCE, premiseName.getTypeOccurrence().getAfterBeforeEnum());
        }
        if (premiseName.getGrPostal() != null){
            this.writeGrPostal(premiseName.getGrPostal());
        }
        if (premiseName.getContent() != null){
            writer.writeCharacters(premiseName.getContent());
        }
        writer.writeEndElement();
    }

    private void writePremiseLocation(PremiseLocation premiseLocation) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_PREMISE_LOCATION);
        if (premiseLocation.getGrPostal() != null){
            this.writeGrPostal(premiseLocation.getGrPostal());
        }
        if (premiseLocation.getContent() != null){
            writer.writeCharacters(premiseLocation.getContent());
        }
        writer.writeEndElement();
    }

    private void writeSubPremiseName(SubPremiseName subPremiseName) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_SUB_PREMISE_NAME);
        if (subPremiseName.getType() != null){
            writer.writeAttribute(ATT_TYPE, subPremiseName.getType());
        }
        if (subPremiseName.getTypeOccurrence() != null){
            writer.writeAttribute(ATT_TYPE_OCCURRENCE, subPremiseName.getTypeOccurrence().getAfterBeforeEnum());
        }
        if (subPremiseName.getGrPostal() != null){
            this.writeGrPostal(subPremiseName.getGrPostal());
        }
        if (subPremiseName.getContent() != null){
            writer.writeCharacters(subPremiseName.getContent());
        }
        writer.writeEndElement();
    }

    private void writeSubPremiseLocation(SubPremiseLocation subPremiseLocation) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_SUB_PREMISE_LOCATION);
        if (subPremiseLocation.getGrPostal() != null){
            this.writeGrPostal(subPremiseLocation.getGrPostal());
        }
        if (subPremiseLocation.getContent() != null){
            writer.writeCharacters(subPremiseLocation.getContent());
        }
        writer.writeEndElement();
    }

    private void writePostalRoute(PostalRoute postalRoute) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_POSTAL_ROUTE);
        if (postalRoute.getType() != null){
            writer.writeAttribute(ATT_TYPE, postalRoute.getType());
        }
        if (postalRoute.getAddressLines() != null){
            for(GenericTypedGrPostal addressLine : postalRoute.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (postalRoute.getPostalRouteNames() != null){
            for(GenericTypedGrPostal postalRouteNames : postalRoute.getPostalRouteNames()){
                this.writePostalRouteName(postalRouteNames);
            }
        }
        if (postalRoute.getPostalRouteNumber() != null){
            this.writePostalRouteNumber(postalRoute.getPostalRouteNumber());
        }
        if (postalRoute.getPostBox() != null){
            this.writePostBox(postalRoute.getPostBox());
        }
        writer.writeEndElement();
    }

    private void writePostalRouteNumber(PostalRouteNumber postalRouteNumber) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_POSTAL_ROUTE_NUMBER);
        if (postalRouteNumber.getGrPostal() != null){
            this.writeGrPostal(postalRouteNumber.getGrPostal());
        }
        if (postalRouteNumber.getContent() != null){
            writer.writeCharacters(postalRouteNumber.getContent());
        }
        writer.writeEndElement();
    }

    private void writePostOfficeNumber(PostOfficeNumber postOfficeNumber) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_POST_OFFICE_NUMBER);
        if (postOfficeNumber.getIndicator() != null){
            writer.writeAttribute(ATT_INDICATOR, postOfficeNumber.getIndicator());
        }
        if (postOfficeNumber.getIndicatorOccurrence() != null){
            writer.writeAttribute(ATT_INDICATOR_OCCURRENCE, postOfficeNumber.getIndicatorOccurrence().getAfterBeforeEnum());
        }
        if (postOfficeNumber.getGrPostal() != null){
            this.writeGrPostal(postOfficeNumber.getGrPostal());
        }
        if (postOfficeNumber.getContent() != null){
            writer.writeCharacters(postOfficeNumber.getContent());
        }
        writer.writeEndElement();
    }

    private void writeDependentLocalityNumber(DependentLocalityNumber dependentLocalityNumber) throws XMLStreamException{
        writer.writeStartElement(URI_XAL,TAG_DEPENDENT_LOCALITY_NUMBER);
        if (dependentLocalityNumber.getNameNumberOccurrence() != null){
            writer.writeAttribute(ATT_NAME_NUMBER_OCCURRENCE, dependentLocalityNumber.getNameNumberOccurrence().getAfterBeforeEnum());
        }
        if (dependentLocalityNumber.getGrPostal() != null){
            this.writeGrPostal(dependentLocalityNumber.getGrPostal());
        }
        if (dependentLocalityNumber.getContent() != null){
            writer.writeCharacters(dependentLocalityNumber.getContent());
        }
        writer.writeEndElement();
    }

    private void writePremise(Premise premise) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_PREMISE);
        if (premise.getType() != null){
            writer.writeAttribute(ATT_TYPE, premise.getType());
        }
        if (premise.getPremiseDependency() != null){
            writer.writeAttribute(ATT_PREMISE_DEPENDENCY, premise.getPremiseDependency());
        }
        if (premise.getPremiseDependencyType() != null){
            writer.writeAttribute(ATT_PREMISE_DEPENDENCY_TYPE, premise.getPremiseDependencyType());
        }
        if (premise.getPremiseThoroughfareConnector() != null){
            writer.writeAttribute(ATT_PREMISE_THOROUGHFARE_CONNECTOR, premise.getPremiseThoroughfareConnector());
        }
        if (premise.getAddressLines() != null){
            for(GenericTypedGrPostal addressLine : premise.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (premise.getPremiseNames() != null){
            for(PremiseName premiseName : premise.getPremiseNames()){
                this.writePremiseName(premiseName);
            }
        }
        if (premise.getPremiseLocation() != null){
            this.writePremiseLocation(premise.getPremiseLocation());
        }
        if (premise.getPremiseNumbers() != null){
            for(PremiseNumber premiseNumber : premise.getPremiseNumbers()){
                this.writePremiseNumber(premiseNumber);
            }
        }
        if (premise.getPremiseNumberRange() != null){
            this.writePremiseNumberRange(premise.getPremiseNumberRange());
        }
        if (premise.getPremiseNumberPrefix() != null){
            for(PremiseNumberPrefix premiseNumberPrefix : premise.getPremiseNumberPrefix()){
                this.writePremiseNumberPrefix(premiseNumberPrefix);
            }
        }
        if (premise.getPremiseNumberSuffix() != null){
            for(PremiseNumberSuffix premiseNumberSuffix : premise.getPremiseNumberSuffix()){
                this.writePremiseNumberSuffix(premiseNumberSuffix);
            }
        }
        if (premise.getBuildingNames() != null){
            for(BuildingName buildingName : premise.getBuildingNames()){
                this.writeBuildingName(buildingName);
            }
        }
        if (premise.getSubPremises() != null){
            for(SubPremise subPremise : premise.getSubPremises()){
                this.writeSubPremise(subPremise);
            }
        }
        if (premise.getFirm() != null){
            this.writeFirm(premise.getFirm());
        }
        if (premise.getMailStop() != null){
            this.writeMailStop(premise.getMailStop());
        }
        if (premise.getPostalCode() != null){
            this.writePostalCode(premise.getPostalCode());
        }
        if (premise.getPremise() != null){
            this.writePremise(premise.getPremise());
        }
        writer.writeEndElement();
    }

    private void writePremiseNumber(PremiseNumber premiseNumber) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_PREMISE_NUMBER);
        if (premiseNumber.getNumberType() != null){
            writer.writeAttribute(ATT_NUMBER_TYPE, premiseNumber.getNumberType().getSingleRange());
        }
        if (premiseNumber.getType() != null){
            writer.writeAttribute(ATT_TYPE, premiseNumber.getType());
        }
        if (premiseNumber.getIndicator() != null){
            writer.writeAttribute(ATT_INDICATOR, premiseNumber.getIndicator());
        }
        if (premiseNumber.getIndicatorOccurrence() != null){
            writer.writeAttribute(ATT_INDICATOR_OCCURRENCE, premiseNumber.getIndicatorOccurrence().getAfterBeforeEnum());
        }
        if (premiseNumber.getNumberTypeOccurrence() != null){
            writer.writeAttribute(ATT_NUMBER_TYPE_OCCURRENCE, premiseNumber.getNumberTypeOccurrence().getAfterBeforeEnum());
        }
        if (premiseNumber.getGrPostal() != null){
            this.writeGrPostal(premiseNumber.getGrPostal());
        }
        if (premiseNumber.getContent() != null){
            writer.writeCharacters(premiseNumber.getContent());
        }
        writer.writeEndElement();
    }

    private void writeSubPremiseNumber(SubPremiseNumber subPremiseNumber) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_SUB_PREMISE_NUMBER);
        if (subPremiseNumber.getIndicator() != null){
            writer.writeAttribute(ATT_INDICATOR, subPremiseNumber.getIndicator());
        }
        if (subPremiseNumber.getIndicatorOccurrence() != null){
            writer.writeAttribute(ATT_INDICATOR_OCCURRENCE, subPremiseNumber.getIndicatorOccurrence().getAfterBeforeEnum());
        }
        if (subPremiseNumber.getNumberTypeOccurrence() != null){
            writer.writeAttribute(ATT_NUMBER_TYPE_OCCURRENCE, subPremiseNumber.getNumberTypeOccurrence().getAfterBeforeEnum());
        }
        if (subPremiseNumber.getPremiseNumberSeparator() != null){
            writer.writeAttribute(ATT_PREMISE_NUMBER_SEPARATOR, subPremiseNumber.getPremiseNumberSeparator());
        }
        if (subPremiseNumber.getType() != null){
            writer.writeAttribute(ATT_TYPE, subPremiseNumber.getType());
        }
        if (subPremiseNumber.getGrPostal() != null){
            this.writeGrPostal(subPremiseNumber.getGrPostal());
        }
        if (subPremiseNumber.getContent() != null){
            writer.writeCharacters(subPremiseNumber.getContent());
        }
        writer.writeEndElement();
    }

    private void writePremiseNumberRange(PremiseNumberRange premiseNumberRange) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_PREMISE_NUMBER_RANGE);
        if (premiseNumberRange.getRangeType() != null){
            writer.writeAttribute(ATT_RANGE_TYPE, premiseNumberRange.getRangeType());
        }
        if (premiseNumberRange.getIndicator() != null){
            writer.writeAttribute(ATT_INDICATOR, premiseNumberRange.getIndicator());
        }
        if (premiseNumberRange.getSeparator() != null){
            writer.writeAttribute(ATT_SEPARATOR, premiseNumberRange.getSeparator());
        }
        if (premiseNumberRange.getType() != null){
            writer.writeAttribute(ATT_TYPE, premiseNumberRange.getType());
        }
        if (premiseNumberRange.getIndicatorOccurrence() != null){
            writer.writeAttribute(ATT_INDICATOR_OCCURRENCE, premiseNumberRange.getIndicatorOccurrence().getAfterBeforeEnum());
        }
        if (premiseNumberRange.getNumberRangeOccurrence() != null){
            writer.writeAttribute(ATT_NUMBER_RANGE_OCCURRENCE, premiseNumberRange.getNumberRangeOccurrence().getAfterBeforeTypeEnum());
        }
        if (premiseNumberRange.getPremiseNumberRangeFrom() != null){
            this.writePremiseNumberRangeFrom(premiseNumberRange.getPremiseNumberRangeFrom());
        }
        if (premiseNumberRange.getPremiseNumberRangeTo() != null){
            this.writePremiseNumberRangeTo(premiseNumberRange.getPremiseNumberRangeTo());
        }
        writer.writeEndElement();
    }

    private void writePremiseNumberRangeFrom(PremiseNumberRangeFrom premiseNumberRangeFrom) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_PREMISE_NUMBER_RANGE_FROM);
        if (premiseNumberRangeFrom.getAddressLines() != null){
            for(GenericTypedGrPostal addressLine : premiseNumberRangeFrom.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (premiseNumberRangeFrom.getPremiseNumberPrefixes() != null){
            for(PremiseNumberPrefix premiseNumberPrefix : premiseNumberRangeFrom.getPremiseNumberPrefixes()){
                this.writePremiseNumberPrefix(premiseNumberPrefix);
            }
        }
        if (premiseNumberRangeFrom.getPremiseNumbers() != null){
            for(PremiseNumber premiseNumber : premiseNumberRangeFrom.getPremiseNumbers()){
                this.writePremiseNumber(premiseNumber);
            }
        }
        if (premiseNumberRangeFrom.getPremiseNumberSuffixes() != null){
            for(PremiseNumberSuffix premiseNumberSuffix : premiseNumberRangeFrom.getPremiseNumberSuffixes()){
                this.writePremiseNumberSuffix(premiseNumberSuffix);
            }
        }
        writer.writeEndElement();
    }

    private void writePremiseNumberRangeTo(PremiseNumberRangeTo premiseNumberRangeTo) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_PREMISE_NUMBER_RANGE_TO);
        if (premiseNumberRangeTo.getAddressLines() != null){
            for(GenericTypedGrPostal addressLine : premiseNumberRangeTo.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (premiseNumberRangeTo.getPremiseNumberPrefixes() != null){
            for(PremiseNumberPrefix premiseNumberPrefix : premiseNumberRangeTo.getPremiseNumberPrefixes()){
                this.writePremiseNumberPrefix(premiseNumberPrefix);
            }
        }
        if (premiseNumberRangeTo.getPremiseNumbers() != null){
            for(PremiseNumber premiseNumber : premiseNumberRangeTo.getPremiseNumbers()){
                this.writePremiseNumber(premiseNumber);
            }
        }
        if (premiseNumberRangeTo.getPremiseNumberSuffixes() != null){
            for(PremiseNumberSuffix premiseNumberSuffix : premiseNumberRangeTo.getPremiseNumberSuffixes()){
                this.writePremiseNumberSuffix(premiseNumberSuffix);
            }
        }
        writer.writeEndElement();
    }

    private void writePremiseNumberPrefix(PremiseNumberPrefix premiseNumberPrefix) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_PREMISE_NUMBER_PREFIX);
        if (premiseNumberPrefix.getNumberPrefixSeparator() != null){
            writer.writeAttribute(ATT_NUMBER_PREFIX_SEPARATOR, premiseNumberPrefix.getNumberPrefixSeparator());
        }
        if (premiseNumberPrefix.getType() != null){
            writer.writeAttribute(ATT_TYPE, premiseNumberPrefix.getType());
        }
        if (premiseNumberPrefix.getGrPostal() != null){
            this.writeGrPostal(premiseNumberPrefix.getGrPostal());
        }
        if (premiseNumberPrefix.getContent() != null){
            writer.writeCharacters(premiseNumberPrefix.getContent());
        }
        writer.writeEndElement();
    }

    private void writePremiseNumberSuffix(PremiseNumberSuffix premiseNumberSuffix) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_PREMISE_NUMBER_PREFIX);
        if (premiseNumberSuffix.getNumberSuffixSeparator() != null){
            writer.writeAttribute(ATT_NUMBER_SUFFIX_SEPARATOR, premiseNumberSuffix.getNumberSuffixSeparator());
        }
        if (premiseNumberSuffix.getType() != null){
            writer.writeAttribute(ATT_TYPE, premiseNumberSuffix.getType());
        }
        if (premiseNumberSuffix.getGrPostal() != null){
            this.writeGrPostal(premiseNumberSuffix.getGrPostal());
        }
        if (premiseNumberSuffix.getContent() != null){
            writer.writeCharacters(premiseNumberSuffix.getContent());
        }
        writer.writeEndElement();
    }

    private void writeSubPremiseNumberPrefix(SubPremiseNumberPrefix subPremiseNumberPrefix) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_SUB_PREMISE_NUMBER_PREFIX);
        if (subPremiseNumberPrefix.getNumberPrefixSeparator() != null){
            writer.writeAttribute(ATT_NUMBER_PREFIX_SEPARATOR, subPremiseNumberPrefix.getNumberPrefixSeparator());
        }
        if (subPremiseNumberPrefix.getType() != null){
            writer.writeAttribute(ATT_TYPE, subPremiseNumberPrefix.getType());
        }
        if (subPremiseNumberPrefix.getGrPostal() != null){
            this.writeGrPostal(subPremiseNumberPrefix.getGrPostal());
        }
        if (subPremiseNumberPrefix.getContent() != null){
            writer.writeCharacters(subPremiseNumberPrefix.getContent());
        }
        writer.writeEndElement();
    }

    private void writeSubPremiseNumberSuffix(SubPremiseNumberSuffix subPremiseNumberSuffix) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_SUB_PREMISE_NUMBER_PREFIX);
        if (subPremiseNumberSuffix.getNumberSuffixSeparator() != null){
            writer.writeAttribute(ATT_NUMBER_SUFFIX_SEPARATOR, subPremiseNumberSuffix.getNumberSuffixSeparator());
        }
        if (subPremiseNumberSuffix.getType() != null){
            writer.writeAttribute(ATT_TYPE, subPremiseNumberSuffix.getType());
        }
        if (subPremiseNumberSuffix.getGrPostal() != null){
            this.writeGrPostal(subPremiseNumberSuffix.getGrPostal());
        }
        if (subPremiseNumberSuffix.getContent() != null){
            writer.writeCharacters(subPremiseNumberSuffix.getContent());
        }
        writer.writeEndElement();
    }

    private void writeSubPremise(SubPremise subPremise) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_SUB_PREMISE);
        if (subPremise.getType() != null){
            writer.writeAttribute(ATT_TYPE, subPremise.getType());
        }
        if (subPremise.getAddressLines() != null){
            for(GenericTypedGrPostal addressLine : subPremise.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (subPremise.getSubPremiseNames() != null){
            for(SubPremiseName subPremiseName : subPremise.getSubPremiseNames()){
                this.writeSubPremiseName(subPremiseName);
            }
        }
        if (subPremise.getSubPremiseLocation() != null){
            this.writeSubPremiseLocation(subPremise.getSubPremiseLocation());
        }
        if (subPremise.getSubPremiseNumbers() != null){
            for(SubPremiseNumber subPremiseNumber : subPremise.getSubPremiseNumbers()){
                this.writeSubPremiseNumber(subPremiseNumber);
            }
        }
        if (subPremise.getSubPremiseNumberPrefixes() != null){
            for(SubPremiseNumberPrefix subPremiseNumberPrefix : subPremise.getSubPremiseNumberPrefixes()){
                this.writeSubPremiseNumberPrefix(subPremiseNumberPrefix);
            }
        }
        if (subPremise.getSubPremiseNumberSuffixes() != null){
            for(SubPremiseNumberSuffix subPremiseNumberSuffix : subPremise.getSubPremiseNumberSuffixes()){
                this.writeSubPremiseNumberSuffix(subPremiseNumberSuffix);
            }
        }
        if (subPremise.getBuildingNames() != null){
            for(BuildingName buildingName : subPremise.getBuildingNames()){
                this.writeBuildingName(buildingName);
            }
        }
        if (subPremise.getFirm() != null){
            this.writeFirm(subPremise.getFirm());
        }
        if (subPremise.getMailStop() != null){
            this.writeMailStop(subPremise.getMailStop());
        }
        if (subPremise.getPostalCode() != null){
            this.writePostalCode(subPremise.getPostalCode());
        }
        if (subPremise.getSubPremise() != null){
            this.writeSubPremise(subPremise.getSubPremise());
        }
        writer.writeEndElement();

    }

    private void writeThoroughfare(Thoroughfare thoroughfare) throws XMLStreamException, XalException{
        writer.writeStartElement(URI_XAL,TAG_THOROUGHFARE);
        if (thoroughfare.getType() != null){
            writer.writeAttribute(ATT_TYPE, thoroughfare.getType());
        }
        if (thoroughfare.getDependentThoroughfares() != null){
            writer.writeAttribute(ATT_DEPENDENT_THOROUGHFARES, thoroughfare.getDependentThoroughfares().getDependentThoroughfares());
        }
        if (thoroughfare.getDependentThoroughfaresIndicator() != null){
            writer.writeAttribute(ATT_DEPENDENT_THOROUGHFARES_INDICATOR, thoroughfare.getDependentThoroughfaresIndicator());
        }
        if (thoroughfare.getDependentThoroughfaresConnector() != null){
            writer.writeAttribute(ATT_DEPENDENT_THOROUGHFARES_CONNECTOR, thoroughfare.getDependentThoroughfaresConnector());
        }
        if (thoroughfare.getDependentThoroughfaresType() != null){
            writer.writeAttribute(ATT_DEPENDENT_THOROUGHFARES_TYPE, thoroughfare.getDependentThoroughfaresType());
        }
        if (thoroughfare.getAddressLines() != null){
            for(GenericTypedGrPostal addressLine : thoroughfare.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (thoroughfare.getThoroughfareNumbers() != null){
            for(Object object : thoroughfare.getThoroughfareNumbers()){
                if (object instanceof ThoroughfareNumber)
                    this.writeThoroughfareNumber((ThoroughfareNumber) object);
                else if (object instanceof ThoroughfareNumberRange)
                    this.writeThoroughfareNumberRange((ThoroughfareNumberRange) object);
                else
                    throw new XalException("Error writting Thoroughfare.\n" +
                            "Requiered here "+ThoroughfareNumber.class.toString()+" or "+
                            ThoroughfareNumberRange.class.toString()+".");
            }
        }
        if (thoroughfare.getThoroughfareNumberPrefixes() != null){
            for(ThoroughfareNumberPrefix prefix : thoroughfare.getThoroughfareNumberPrefixes()){
                this.writeThoroughfareNumberPrefix(prefix);
            }
        }
        if (thoroughfare.getThoroughfareNumberSuffixes() != null){
            for(ThoroughfareNumberSuffix suffix : thoroughfare.getThoroughfareNumberSuffixes()){
                this.writeThoroughfareNumberSuffix(suffix);
            }
        }
        if (thoroughfare.getThoroughfarePreDirection() != null){
            this.writeThoroughfarePreDirection(thoroughfare.getThoroughfarePreDirection());
        }
        if (thoroughfare.getThoroughfareLeadingType() != null){
            this.writeThoroughfareLeadingType(thoroughfare.getThoroughfareLeadingType());
        }
        if (thoroughfare.getThoroughfareNames() != null){
            for(GenericTypedGrPostal name : thoroughfare.getThoroughfareNames()){
                this.writeThoroughfareName(name);
            }
        }
        if (thoroughfare.getThoroughfareTrailingType() != null){
            this.writeThoroughfareTrailingType(thoroughfare.getThoroughfareTrailingType());
        }
        if (thoroughfare.getThoroughfarePostDirection() != null){
            this.writeThoroughfarePostDirection(thoroughfare.getThoroughfarePostDirection());
        }
        if (thoroughfare.getDependentThoroughfare() != null){
            this.writeDependentThoroughfare(thoroughfare.getDependentThoroughfare());
        }
        if (thoroughfare.getDependentLocality() != null){
            this.writeDependentLocality(thoroughfare.getDependentLocality());
        }
        if (thoroughfare.getPremise() != null){
            this.writePremise(thoroughfare.getPremise());
        }
        if (thoroughfare.getFirm() != null){
            this.writeFirm(thoroughfare.getFirm());
        }
        if (thoroughfare.getPostalCode() != null){
            this.writePostalCode(thoroughfare.getPostalCode());
        }
        writer.writeEndElement();
    }

    private void writeThoroughfareNumber(ThoroughfareNumber thoroughfareNumber) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_THOROUGHFARE_NUMBER);
        if (thoroughfareNumber.getNumberType() != null){
            writer.writeAttribute(ATT_NUMBER_TYPE, thoroughfareNumber.getNumberType().getSingleRange());
        }
        if (thoroughfareNumber.getType() != null){
            writer.writeAttribute(ATT_TYPE, thoroughfareNumber.getType());
        }
        if (thoroughfareNumber.getIndicator() != null){
            writer.writeAttribute(ATT_INDICATOR, thoroughfareNumber.getIndicator());
        }
        if (thoroughfareNumber.getIndicatorOccurence() != null){
            writer.writeAttribute(ATT_INDICATOR_OCCURRENCE, thoroughfareNumber.getIndicatorOccurence().getAfterBeforeEnum());
        }
        if (thoroughfareNumber.getNumberOccurence() != null){
            writer.writeAttribute(ATT_NUMBER_OCCURRENCE, thoroughfareNumber.getNumberOccurence().getAfterBeforeTypeEnum());
        }
        if (thoroughfareNumber.getGrPostal() != null){
            this.writeGrPostal(thoroughfareNumber.getGrPostal());
        }
        if (thoroughfareNumber.getContent() != null){
            writer.writeCharacters(thoroughfareNumber.getContent());
        }
        writer.writeEndElement();
    }

    private void writeThoroughfareNumberRange(ThoroughfareNumberRange thoroughfareNumberRange) throws XMLStreamException, XalException {
        writer.writeStartElement(URI_XAL,TAG_THOROUGHFARE_NUMBER_RANGE);
        if (thoroughfareNumberRange.getRangeType() != null){
            writer.writeAttribute(ATT_RANGE_TYPE, thoroughfareNumberRange.getRangeType().getOddEven());
        }
        if (thoroughfareNumberRange.getIndicator() != null){
            writer.writeAttribute(ATT_INDICATOR, thoroughfareNumberRange.getIndicator());
        }
        if (thoroughfareNumberRange.getSeparator() != null){
            writer.writeAttribute(ATT_SEPARATOR, thoroughfareNumberRange.getSeparator());
        }
        if (thoroughfareNumberRange.getIndicatorOccurence() != null){
            writer.writeAttribute(ATT_INDICATOR_OCCURRENCE, thoroughfareNumberRange.getIndicatorOccurence().getAfterBeforeEnum());
        }
        if (thoroughfareNumberRange.getNumberRangeOccurence() != null){
            writer.writeAttribute(ATT_NUMBER_RANGE_OCCURRENCE, thoroughfareNumberRange.getNumberRangeOccurence().getAfterBeforeTypeEnum());
        }
        if (thoroughfareNumberRange.getType() != null){
            writer.writeAttribute(ATT_TYPE, thoroughfareNumberRange.getType());
        }
        if (thoroughfareNumberRange.getAddressLines() != null){
            for(GenericTypedGrPostal addressLine : thoroughfareNumberRange.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (thoroughfareNumberRange.getThoroughfareNumberFrom() != null){
            this.writeThoroughfareNumberFrom(thoroughfareNumberRange.getThoroughfareNumberFrom());
        }
        if (thoroughfareNumberRange.getThoroughfareNumberTo() != null){
            this.writeThoroughfareNumberTo(thoroughfareNumberRange.getThoroughfareNumberTo());
        }
        writer.writeEndElement();
    }


    private void writeThoroughfareNumberSuffix(ThoroughfareNumberSuffix suffix) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_THOROUGHFARE_NUMBER_PREFIX);
        if (suffix.getNumberSuffixSeparator() != null){
            writer.writeAttribute(ATT_NUMBER_SUFFIX_SEPARATOR, suffix.getNumberSuffixSeparator());
        }
        if (suffix.getType() != null){
            writer.writeAttribute(ATT_TYPE, suffix.getType());
        }
        if (suffix.getGrPostal() != null){
            this.writeGrPostal(suffix.getGrPostal());
        }
        if (suffix.getContent() != null){
            writer.writeCharacters(suffix.getContent());
        }
        writer.writeEndElement();
    }

    private void writeThoroughfareNumberPrefix(ThoroughfareNumberPrefix prefix) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_THOROUGHFARE_NUMBER_PREFIX);
        if (prefix.getNumberPrefixSeparator() != null){
            writer.writeAttribute(ATT_NUMBER_PREFIX_SEPARATOR, prefix.getNumberPrefixSeparator());
        }
        if (prefix.getType() != null){
            writer.writeAttribute(ATT_TYPE, prefix.getType());
        }
        if (prefix.getGrPostal() != null){
            this.writeGrPostal(prefix.getGrPostal());
        }
        if (prefix.getContent() != null){
            writer.writeCharacters(prefix.getContent());
        }
        writer.writeEndElement();
    }

    private void writeThoroughfareNumberFrom(ThoroughfareNumberFrom thoroughfareNumberFrom) throws XMLStreamException, XalException {
        writer.writeStartElement(URI_XAL,TAG_THOROUGHFARE_NUMBER_FROM);
        if (thoroughfareNumberFrom.getGrPostal() != null){
            this.writeGrPostal(thoroughfareNumberFrom.getGrPostal());
        }
        if (thoroughfareNumberFrom.getContent() != null){
            for (Object object : thoroughfareNumberFrom.getContent()){
                if (object instanceof ThoroughfareNumberPrefix){
                    this.writeThoroughfareNumberPrefix((ThoroughfareNumberPrefix) object);
                } else if (object instanceof ThoroughfareNumber){
                    this.writeThoroughfareNumber((ThoroughfareNumber) object);
                } else if (object instanceof ThoroughfareNumberSuffix){
                    this.writeThoroughfareNumberSuffix((ThoroughfareNumberSuffix) object);
                } else if (object instanceof GenericTypedGrPostal){
                    this.writeAddressLine((GenericTypedGrPostal) object);
                } else if (object instanceof String){
                    writer.writeCharacters((String) object);
                } else{
                    throw new XalException("Invalid content for "+ThoroughfareNumberFrom.class.toString());
                }
            }
        }
        writer.writeEndElement();
    }

    private void writeThoroughfareNumberTo(ThoroughfareNumberTo thoroughfareNumberTo) throws XMLStreamException, XalException {
        writer.writeStartElement(URI_XAL,TAG_THOROUGHFARE_NUMBER_TO);
        if (thoroughfareNumberTo.getGrPostal() != null){
            this.writeGrPostal(thoroughfareNumberTo.getGrPostal());
        }
        if (thoroughfareNumberTo.getContent() != null){
            for (Object object : thoroughfareNumberTo.getContent()){
                if (object instanceof ThoroughfareNumberPrefix){
                    this.writeThoroughfareNumberPrefix((ThoroughfareNumberPrefix) object);
                } else if (object instanceof ThoroughfareNumber){
                    this.writeThoroughfareNumber((ThoroughfareNumber) object);
                } else if (object instanceof ThoroughfareNumberSuffix){
                    this.writeThoroughfareNumberSuffix((ThoroughfareNumberSuffix) object);
                } else if (object instanceof GenericTypedGrPostal){
                    this.writeAddressLine((GenericTypedGrPostal) object);
                } else if (object instanceof String){
                    writer.writeCharacters((String) object);
                } else{
                    throw new XalException("Invalid content for "+ThoroughfareNumberTo.class.toString());
                }
            }
        }
        writer.writeEndElement();
    }

    private void writeDependentThoroughfare(DependentThoroughfare thoroughfare) throws XMLStreamException {
        writer.writeStartElement(URI_XAL,TAG_DEPENDENT_THOROUGHFARE);
        if (thoroughfare.getType() != null){
            writer.writeAttribute(ATT_TYPE, thoroughfare.getType());
        }
        if (thoroughfare.getAddressLines() != null){
            for(GenericTypedGrPostal addressLine : thoroughfare.getAddressLines()){
                this.writeAddressLine(addressLine);
            }
        }
        if (thoroughfare.getThoroughfarePreDirection() != null){
            this.writeThoroughfarePreDirection(thoroughfare.getThoroughfarePreDirection());
        }
        if (thoroughfare.getThoroughfareLeadingType() != null){
            this.writeThoroughfareLeadingType(thoroughfare.getThoroughfareLeadingType());
        }
        if (thoroughfare.getThoroughfareNames() != null){
            for(GenericTypedGrPostal name : thoroughfare.getThoroughfareNames()){
                this.writeThoroughfareName(name);
            }
        }
        if (thoroughfare.getThoroughfareTrailingType() != null){
            this.writeThoroughfareTrailingType(thoroughfare.getThoroughfareTrailingType());
        }
        if (thoroughfare.getThoroughfarePostDirection() != null){
            this.writeThoroughfarePostDirection(thoroughfare.getThoroughfarePostDirection());
        }
        writer.writeEndElement();
    }
}
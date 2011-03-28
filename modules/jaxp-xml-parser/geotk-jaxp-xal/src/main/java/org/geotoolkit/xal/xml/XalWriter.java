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
package org.geotoolkit.xal.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotoolkit.xal.model.AddressDetails;
import org.geotoolkit.xal.model.AddressIdentifier;
import org.geotoolkit.xal.model.AddressLines;
import org.geotoolkit.xal.model.AdministrativeArea;
import org.geotoolkit.xal.model.BuildingName;
import org.geotoolkit.xal.model.Country;
import org.geotoolkit.xal.model.CountryNameCode;
import org.geotoolkit.xal.model.Department;
import org.geotoolkit.xal.model.DependentLocality;
import org.geotoolkit.xal.model.DependentLocalityNumber;
import org.geotoolkit.xal.model.DependentThoroughfare;
import org.geotoolkit.xal.model.Firm;
import org.geotoolkit.xal.model.GenericTypedGrPostal;
import org.geotoolkit.xal.model.GrPostal;
import org.geotoolkit.xal.model.LargeMailUser;
import org.geotoolkit.xal.model.LargeMailUserIdentifier;
import org.geotoolkit.xal.model.LargeMailUserName;
import org.geotoolkit.xal.model.Locality;
import org.geotoolkit.xal.model.MailStop;
import org.geotoolkit.xal.model.MailStopNumber;
import org.geotoolkit.xal.model.PostBox;
import org.geotoolkit.xal.model.PostBoxNumber;
import org.geotoolkit.xal.model.PostBoxNumberExtension;
import org.geotoolkit.xal.model.PostBoxNumberPrefix;
import org.geotoolkit.xal.model.PostBoxNumberSuffix;
import org.geotoolkit.xal.model.PostOffice;
import org.geotoolkit.xal.model.PostOfficeNumber;
import org.geotoolkit.xal.model.PostTown;
import org.geotoolkit.xal.model.PostTownSuffix;
import org.geotoolkit.xal.model.PostalCode;
import org.geotoolkit.xal.model.PostalCodeNumberExtension;
import org.geotoolkit.xal.model.PostalRoute;
import org.geotoolkit.xal.model.PostalRouteNumber;
import org.geotoolkit.xal.model.PostalServiceElements;
import org.geotoolkit.xal.model.Premise;
import org.geotoolkit.xal.model.PremiseLocation;
import org.geotoolkit.xal.model.PremiseName;
import org.geotoolkit.xal.model.PremiseNumber;
import org.geotoolkit.xal.model.PremiseNumberPrefix;
import org.geotoolkit.xal.model.PremiseNumberRange;
import org.geotoolkit.xal.model.PremiseNumberRangeFrom;
import org.geotoolkit.xal.model.PremiseNumberRangeTo;
import org.geotoolkit.xal.model.PremiseNumberSuffix;
import org.geotoolkit.xal.model.SortingCode;
import org.geotoolkit.xal.model.SubAdministrativeArea;
import org.geotoolkit.xal.model.SubPremise;
import org.geotoolkit.xal.model.SubPremiseLocation;
import org.geotoolkit.xal.model.SubPremiseName;
import org.geotoolkit.xal.model.SubPremiseNumber;
import org.geotoolkit.xal.model.SubPremiseNumberPrefix;
import org.geotoolkit.xal.model.SubPremiseNumberSuffix;
import org.geotoolkit.xal.model.Thoroughfare;
import org.geotoolkit.xal.model.ThoroughfareNumber;
import org.geotoolkit.xal.model.ThoroughfareNumberFrom;
import org.geotoolkit.xal.model.ThoroughfareNumberPrefix;
import org.geotoolkit.xal.model.ThoroughfareNumberRange;
import org.geotoolkit.xal.model.ThoroughfareNumberSuffix;
import org.geotoolkit.xal.model.ThoroughfareNumberTo;
import org.geotoolkit.xal.model.Xal;
import org.geotoolkit.xal.model.XalException;
import org.geotoolkit.xml.StaxStreamWriter;
import static org.geotoolkit.xal.xml.XalConstants.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class XalWriter extends StaxStreamWriter {

    public XalWriter() {
        super();
    }

    public void setWriter(XMLStreamWriter writer) {
        this.writer = writer;
    }

    @Override
    public XMLStreamWriter getWriter() {
        return this.writer;
    }

    /**
     * <p>This method writes a xAL 2.0 document into the file assigned to the KmlWriter.</p>
     *
     * @param xal The Xal object to write.
     */
    public void write(Xal xal) throws XMLStreamException, XalException {

        // FACULTATIF : INDENTATION DE LA SORTIE
        //streamWriter = new IndentingXMLStreamWriter(streamWriter);

        writer.writeStartDocument("UTF-8", "1.0");
        writer.setDefaultNamespace(URI_XAL);
        writer.writeStartElement(URI_XAL, TAG_XAL);
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
    }

    /**
     *
     * @param xal The Xal object to write.
     * @throws XMLStreamException
     */
    private void writeXal(Xal xal) 
            throws XMLStreamException, XalException {

        if (xal.getVersion() != null) {
            writer.writeAttribute(ATT_VERSION, xal.getVersion());
        }
        for (AddressDetails ad : xal.getAddressDetails()) {
            this.writeAddressDetails(ad);
        }
    }

    /**
     *
     * @param addressDetails
     * @throws XMLStreamException
     */
    public void writeAddressDetails(AddressDetails addressDetails) 
            throws XMLStreamException, XalException {

        writer.writeStartElement(URI_XAL, TAG_ADDRESS_DETAILS);
        if (addressDetails.getAddressType() != null) {
            writer.writeAttribute(ATT_ADDRESS_TYPE, addressDetails.getAddressType());
        }
        if (addressDetails.getCurrentStatus() != null) {
            writer.writeAttribute(ATT_CURRENT_STATUS, addressDetails.getCurrentStatus());
        }
        if (addressDetails.getValidFromDate() != null) {
            writer.writeAttribute(ATT_VALID_FROM_DATE, addressDetails.getValidFromDate());
        }
        if (addressDetails.getValidToDate() != null) {
            writer.writeAttribute(ATT_VALID_TO_DATE, addressDetails.getValidToDate());
        }
        if (addressDetails.getUsage() != null) {
            writer.writeAttribute(ATT_USAGE, addressDetails.getUsage());
        }
        if (addressDetails.getGrPostal() != null) {
            this.writeGrPostal(addressDetails.getGrPostal());
        }
        if (addressDetails.getAddressDetailsKey() != null) {
            writer.writeAttribute(ATT_ADDRESS_DETAILS_KEY, addressDetails.getAddressDetailsKey());
        }

        if (addressDetails.getPostalServiceElements() != null) {
            this.writePostalServiceElements(addressDetails.getPostalServiceElements());
        }
        if (addressDetails.getAddress() != null) {
            this.writeAddress(addressDetails.getAddress());
        }
        if (addressDetails.getAddressLines() != null) {
            this.writeAddressLines(addressDetails.getAddressLines());
        }
        if (addressDetails.getCountry() != null) {
            this.writeCountry(addressDetails.getCountry());
        }
        if (addressDetails.getAdministrativeArea() != null) {
            this.writeAdministrativeArea(addressDetails.getAdministrativeArea());
        }
        if (addressDetails.getLocality() != null) {
            this.writeLocality(addressDetails.getLocality());
        }
        if (addressDetails.getThoroughfare() != null) {
            this.writeThoroughfare(addressDetails.getThoroughfare());
        }

        writer.writeEndElement();
    }

    /**
     *
     * @param postalServiceElements
     * @throws XMLStreamException
     */
    private void writePostalServiceElements(PostalServiceElements postalServiceElements) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POSTAL_SERVICE_ELEMENTS);
        if (postalServiceElements.getType() != null) {
            writer.writeAttribute(ATT_TYPE, postalServiceElements.getType());
        }
        for (AddressIdentifier addressIdentifier : postalServiceElements.getAddressIdentifiers()) {
            this.writeAddressIdentifier(addressIdentifier);
        }
        if (postalServiceElements.getEndorsementLineCode() != null) {
            this.writeEndorsementLineCode(postalServiceElements.getEndorsementLineCode());
        }
        if (postalServiceElements.getKeyLineCode() != null) {
            this.writeKeyLineCode(postalServiceElements.getKeyLineCode());
        }
        if (postalServiceElements.getBarcode() != null) {
            this.writeBarcode(postalServiceElements.getBarcode());
        }
        if (postalServiceElements.getSortingCode() != null) {
            this.writeSortingCode(postalServiceElements.getSortingCode());
        }
        if (postalServiceElements.getAddressLatitude() != null) {
            this.writeAddressLatitude(postalServiceElements.getAddressLatitude());
        }
        if (postalServiceElements.getAddressLatitudeDirection() != null) {
            this.writeAddressLatitudeDirection(postalServiceElements.getAddressLatitudeDirection());
        }
        if (postalServiceElements.getAddressLongitude() != null) {
            this.writeAddressLongitude(postalServiceElements.getAddressLongitude());
        }
        if (postalServiceElements.getAddressLongitudeDirection() != null) {
            this.writeAddressLongitudeDirection(postalServiceElements.getAddressLongitudeDirection());
        }
        for (GenericTypedGrPostal data : postalServiceElements.getSupplementaryPostalServiceData()) {
            this.writeSupplementaryPostalServiceData(data);
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param address
     * @throws XMLStreamException
     */
    private void writeAddress(GenericTypedGrPostal address) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_ADDRESS);
        this.writeGenericTypedGrPostal(address);
        writer.writeEndElement();
    }

    /**
     *
     * @param data
     * @throws XMLStreamException
     */
    private void writeSupplementaryPostalServiceData(GenericTypedGrPostal data) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_SUPPLEMENTARY_POSTAL_SERVICE_DATA);
        this.writeGenericTypedGrPostal(data);
        writer.writeEndElement();
    }

    /**
     *
     * @param address
     * @throws XMLStreamException
     */
    private void writeAddressLongitude(GenericTypedGrPostal address) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_ADDRESS_LONGITUDE);
        this.writeGenericTypedGrPostal(address);
        writer.writeEndElement();
    }

    /**
     *
     * @param address
     * @throws XMLStreamException
     */
    private void writeAddressLongitudeDirection(GenericTypedGrPostal address) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_ADDRESS_LONGITUDE_DIRECTION);
        this.writeGenericTypedGrPostal(address);
        writer.writeEndElement();
    }

    private void writeAddressLatitude(GenericTypedGrPostal address) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_ADDRESS_LATITUDE);
        this.writeGenericTypedGrPostal(address);
        writer.writeEndElement();
    }

    /**
     *
     * @param address
     * @throws XMLStreamException
     */
    private void writeAddressLatitudeDirection(GenericTypedGrPostal address) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_ADDRESS_LATITUDE_DIRECTION);
        this.writeGenericTypedGrPostal(address);
        writer.writeEndElement();
    }

    /**
     *
     * @param barcode
     * @throws XMLStreamException
     */
    private void writeBarcode(GenericTypedGrPostal barcode) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_BARCODE);
        this.writeGenericTypedGrPostal(barcode);
        writer.writeEndElement();
    }

    /**
     *
     * @param lineCode
     * @throws XMLStreamException
     */
    private void writeEndorsementLineCode(GenericTypedGrPostal lineCode) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_ENDORSEMENT_LINE_CODE);
        this.writeGenericTypedGrPostal(lineCode);
        writer.writeEndElement();
    }

    /**
     *
     * @param lineCode
     * @throws XMLStreamException
     */
    private void writeKeyLineCode(GenericTypedGrPostal lineCode) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_KEY_LINE_CODE);
        this.writeGenericTypedGrPostal(lineCode);
        writer.writeEndElement();
    }

    /**
     *
     * @param addressLines
     * @throws XMLStreamException
     */
    private void writeAddressLines(AddressLines addressLines) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_ADDRESS_LINES);
        for (GenericTypedGrPostal addressLine : addressLines.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param addressline
     * @throws XMLStreamException
     */
    private void writeAddressLine(GenericTypedGrPostal addressline) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_ADDRESS_LINE);
        this.writeGenericTypedGrPostal(addressline);
        writer.writeEndElement();
    }

    /**
     *
     * @param name
     * @throws XMLStreamException
     */
    private void writeAdministrativeAreaName(GenericTypedGrPostal name) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_ADMINISTRATIVE_AREA_NAME);
        this.writeGenericTypedGrPostal(name);
        writer.writeEndElement();
    }

    /**
     *
     * @param name
     * @throws XMLStreamException
     */
    private void writeSubAdministrativeAreaName(GenericTypedGrPostal name) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_SUB_ADMINISTRATIVE_AREA_NAME);
        this.writeGenericTypedGrPostal(name);
        writer.writeEndElement();
    }

    /**
     *
     * @param country
     * @throws XMLStreamException
     * @throws XalException
     */
    private void writeCountry(Country country) 
            throws XMLStreamException, XalException {

        writer.writeStartElement(URI_XAL, TAG_COUNTRY);
        for (GenericTypedGrPostal addressLine : country.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (CountryNameCode countryNameCode : country.getCountryNameCodes()) {
            this.writeCountryNameCode(countryNameCode);
        }
        for (GenericTypedGrPostal countryName : country.getCountryNames()) {
            this.writeCountryName(countryName);
        }
        if (country.getAdministrativeArea() != null) {
            this.writeAdministrativeArea(country.getAdministrativeArea());
        }
        if (country.getLocality() != null) {
            this.writeLocality(country.getLocality());
        }
        if (country.getThoroughfare() != null) {
            this.writeThoroughfare(country.getThoroughfare());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param countryName
     * @throws XMLStreamException
     */
    private void writeCountryName(GenericTypedGrPostal countryName) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_COUNTRY_NAME);
        this.writeGenericTypedGrPostal(countryName);
        writer.writeEndElement();
    }

    /**
     *
     * @param firmName
     * @throws XMLStreamException
     */
    private void writeFirmName(GenericTypedGrPostal firmName) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_FIRM_NAME);
        this.writeGenericTypedGrPostal(firmName);
        writer.writeEndElement();
    }

    /**
     *
     * @param departmentName
     * @throws XMLStreamException
     */
    private void writeDepartmentName(GenericTypedGrPostal departmentName)
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_DEPARTMENT_NAME);
        this.writeGenericTypedGrPostal(departmentName);
        writer.writeEndElement();
    }

    /**
     *
     * @param mailStopName
     * @throws XMLStreamException
     */
    private void writeMailStopName(GenericTypedGrPostal mailStopName) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_MAIL_STOP_NAME);
        this.writeGenericTypedGrPostal(mailStopName);
        writer.writeEndElement();
    }

    /**
     *
     * @param postalCodeNumber
     * @throws XMLStreamException
     */
    private void writePostalCodeNumber(GenericTypedGrPostal postalCodeNumber) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POSTAL_CODE_NUMBER);
        this.writeGenericTypedGrPostal(postalCodeNumber);
        writer.writeEndElement();
    }

    /**
     *
     * @param localityName
     * @throws XMLStreamException
     */
    private void writeLocalityName(GenericTypedGrPostal localityName) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_LOCALITY_NAME);
        this.writeGenericTypedGrPostal(localityName);
        writer.writeEndElement();
    }

    /**
     *
     * @param dependentLocalityName
     * @throws XMLStreamException
     */
    private void writeDependentLocalityName(GenericTypedGrPostal dependentLocalityName) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_DEPENDENT_LOCALITY_NAME);
        this.writeGenericTypedGrPostal(dependentLocalityName);
        writer.writeEndElement();
    }

    /**
     *
     * @param postTownName
     * @throws XMLStreamException
     */
    private void writeTownName(GenericTypedGrPostal postTownName) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POST_TOWN_NAME);
        this.writeGenericTypedGrPostal(postTownName);
        writer.writeEndElement();
    }

    /**
     *
     * @param postalRouteName
     * @throws XMLStreamException
     */
    private void writePostalRouteName(GenericTypedGrPostal postalRouteName) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POSTAL_ROUTE_NAME);
        this.writeGenericTypedGrPostal(postalRouteName);
        writer.writeEndElement();
    }

    /**
     *
     * @param postOfficeName
     * @throws XMLStreamException
     */
    private void writePostOfficeName(GenericTypedGrPostal postOfficeName) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POST_OFFICE_NAME);
        this.writeGenericTypedGrPostal(postOfficeName);
        writer.writeEndElement();
    }

    /**
     *
     * @param thoroughfarePostDirection
     * @throws XMLStreamException
     */
    private void writeThoroughfarePostDirection(GenericTypedGrPostal thoroughfarePostDirection) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_POST_DIRECTION);
        this.writeGenericTypedGrPostal(thoroughfarePostDirection);
        writer.writeEndElement();
    }

    /**
     *
     * @param thoroughfarePreDirection
     * @throws XMLStreamException
     */
    private void writeThoroughfarePreDirection(GenericTypedGrPostal thoroughfarePreDirection) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_PRE_DIRECTION);
        this.writeGenericTypedGrPostal(thoroughfarePreDirection);
        writer.writeEndElement();
    }

    /**
     *
     * @param name
     * @throws XMLStreamException
     */
    private void writeThoroughfareName(GenericTypedGrPostal name) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_NAME);
        this.writeGenericTypedGrPostal(name);
        writer.writeEndElement();
    }

    /**
     *
     * @param thoroughfareTrailingType
     * @throws XMLStreamException
     */
    private void writeThoroughfareTrailingType(GenericTypedGrPostal thoroughfareTrailingType) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_TRAILING_TYPE);
        this.writeGenericTypedGrPostal(thoroughfareTrailingType);
        writer.writeEndElement();
    }

    /**
     *
     * @param thoroughfareLeadingType
     * @throws XMLStreamException
     */
    private void writeThoroughfareLeadingType(GenericTypedGrPostal thoroughfareLeadingType) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_LEADING_TYPE);
        this.writeGenericTypedGrPostal(thoroughfareLeadingType);
        writer.writeEndElement();
    }

    /**
     *
     * @param countryNameCode
     * @throws XMLStreamException
     */
    private void writeCountryNameCode(CountryNameCode countryNameCode) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_COUNTRY_NAME_CODE);
        if (countryNameCode.getScheme() != null) {
            writer.writeAttribute(ATT_SCHEME, countryNameCode.getScheme());
        }
        if (countryNameCode.getGrPostal() != null) {
            this.writeGrPostal(countryNameCode.getGrPostal());
        }
        if (countryNameCode.getContent() != null) {
            writer.writeCharacters(countryNameCode.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param administrativeArea
     * @throws XMLStreamException
     * @throws XalException
     */
    private void writeAdministrativeArea(AdministrativeArea administrativeArea) 
            throws XMLStreamException, XalException {

        writer.writeStartElement(URI_XAL, TAG_ADMINISTRATIVE_AREA);
        if (administrativeArea.getType() != null) {
            writer.writeAttribute(ATT_TYPE, administrativeArea.getType());
        }
        if (administrativeArea.getUsageType() != null) {
            writer.writeAttribute(ATT_USAGE_TYPE, administrativeArea.getUsageType());
        }
        if (administrativeArea.getIndicator() != null) {
            writer.writeAttribute(ATT_INDICATOR, administrativeArea.getIndicator());
        }
        for (GenericTypedGrPostal addressLine : administrativeArea.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (GenericTypedGrPostal name : administrativeArea.getAdministrativeAreaNames()) {
            this.writeAdministrativeAreaName(name);
        }
        if (administrativeArea.getSubAdministrativeArea() != null) {
            this.writeSubAdministrativeArea(administrativeArea.getSubAdministrativeArea());
        }
        if (administrativeArea.getLocality() != null) {
            this.writeLocality(administrativeArea.getLocality());
        } else if (administrativeArea.getPostOffice() != null) {
            this.writePostOffice(administrativeArea.getPostOffice());
        } else if (administrativeArea.getPostalCode() != null) {
            this.writePostalCode(administrativeArea.getPostalCode());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param dependentLocality
     * @throws XMLStreamException
     * @throws XalException
     */
    private void writeDependentLocality(DependentLocality dependentLocality) 
            throws XMLStreamException, XalException {

        writer.writeStartElement(URI_XAL, TAG_DEPENDENT_LOCALITY);
        if (dependentLocality.getType() != null) {
            writer.writeAttribute(ATT_TYPE, dependentLocality.getType());
        }
        if (dependentLocality.getUsageType() != null) {
            writer.writeAttribute(ATT_USAGE_TYPE, dependentLocality.getUsageType());
        }
        if (dependentLocality.getConnector() != null) {
            writer.writeAttribute(ATT_CONNECTOR, dependentLocality.getConnector());
        }
        if (dependentLocality.getIndicator() != null) {
            writer.writeAttribute(ATT_INDICATOR, dependentLocality.getIndicator());
        }
        for (GenericTypedGrPostal addressLine : dependentLocality.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (GenericTypedGrPostal dependentLocalityName : dependentLocality.getDependentLocalityNames()) {
            this.writeDependentLocalityName(dependentLocalityName);
        }
        if (dependentLocality.getDependentLocalityNumber() != null) {
            this.writeDependentLocalityNumber(dependentLocality.getDependentLocalityNumber());
        }
        if (dependentLocality.getPostBox() != null) {
            this.writePostBox(dependentLocality.getPostBox());
        }
        if (dependentLocality.getLargeMailUser() != null) {
            this.writeLargeMailUser(dependentLocality.getLargeMailUser());
        }
        if (dependentLocality.getPostOffice() != null) {
            this.writePostOffice(dependentLocality.getPostOffice());
        }
        if (dependentLocality.getPostalRoute() != null) {
            this.writePostalRoute(dependentLocality.getPostalRoute());
        }
        if (dependentLocality.getThoroughfare() != null) {
            this.writeThoroughfare(dependentLocality.getThoroughfare());
        }
        if (dependentLocality.getPremise() != null) {
            this.writePremise(dependentLocality.getPremise());
        }
        if (dependentLocality.getDependentLocality() != null) {
            this.writeDependentLocality(dependentLocality.getDependentLocality());
        }
        if (dependentLocality.getPostalCode() != null) {
            this.writePostalCode(dependentLocality.getPostalCode());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param locality
     * @throws XMLStreamException
     * @throws XalException
     */
    private void writeLocality(Locality locality) 
            throws XMLStreamException, XalException {

        writer.writeStartElement(URI_XAL, TAG_LOCALITY);
        if (locality.getType() != null) {
            writer.writeAttribute(ATT_TYPE, locality.getType());
        }
        if (locality.getUsageType() != null) {
            writer.writeAttribute(ATT_USAGE_TYPE, locality.getUsageType());
        }
        if (locality.getIndicator() != null) {
            writer.writeAttribute(ATT_INDICATOR, locality.getIndicator());
        }
        for (GenericTypedGrPostal addressLine : locality.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (GenericTypedGrPostal localityName : locality.getLocalityNames()) {
            this.writeLocalityName(localityName);
        }
        if (locality.getPostBox() != null) {
            this.writePostBox(locality.getPostBox());
        }
        if (locality.getLargeMailUser() != null) {
            this.writeLargeMailUser(locality.getLargeMailUser());
        }
        if (locality.getPostOffice() != null) {
            this.writePostOffice(locality.getPostOffice());
        }
        if (locality.getPostalRoute() != null) {
            this.writePostalRoute(locality.getPostalRoute());
        }
        if (locality.getThoroughfare() != null) {
            this.writeThoroughfare(locality.getThoroughfare());
        }
        if (locality.getPremise() != null) {
            this.writePremise(locality.getPremise());
        }
        if (locality.getDependentLocality() != null) {
            this.writeDependentLocality(locality.getDependentLocality());
        }
        if (locality.getPostalCode() != null) {
            this.writePostalCode(locality.getPostalCode());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param generic
     * @throws XMLStreamException
     */
    private void writeGenericTypedGrPostal(GenericTypedGrPostal generic) 
            throws XMLStreamException {

        if (generic.getType() != null) {
            writer.writeAttribute(ATT_TYPE, generic.getType());
        }
        if (generic.getGrPostal() != null) {
            this.writeGrPostal(generic.getGrPostal());
        }
        if (generic.getContent() != null) {
            writer.writeCharacters(generic.getContent());
        }
    }

    /**
     *
     * @param sortingCode
     * @throws XMLStreamException
     */
    private void writeSortingCode(SortingCode sortingCode) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_SORTING_CODE);
        if (sortingCode.getType() != null) {
            writer.writeAttribute(ATT_TYPE, sortingCode.getType());
        }
        if (sortingCode.getGrPostal() != null) {
            this.writeGrPostal(sortingCode.getGrPostal());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param identifier
     * @throws XMLStreamException
     */
    private void writeAddressIdentifier(AddressIdentifier identifier) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_ADDRESS_IDENTIFIER);
        if (identifier.getIdentifierType() != null) {
            writer.writeAttribute(ATT_IDENTIFIER_TYPE, identifier.getIdentifierType());
        }
        if (identifier.getType() != null) {
            writer.writeAttribute(ATT_TYPE, identifier.getType());
        }
        if (identifier.getGrPostal() != null) {
            this.writeGrPostal(identifier.getGrPostal());
        }
        if (identifier.getContent() != null) {
            writer.writeCharacters(identifier.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param grPostal
     * @throws XMLStreamException
     */
    private void writeGrPostal(GrPostal grPostal) 
            throws XMLStreamException {

        if (grPostal.getCode() != null) {
            writer.writeAttribute(ATT_CODE, grPostal.getCode());
        }
    }

    /**
     *
     * @param subAdministrativeArea
     * @throws XMLStreamException
     * @throws XalException
     */
    private void writeSubAdministrativeArea(SubAdministrativeArea subAdministrativeArea) 
            throws XMLStreamException, XalException {

        writer.writeStartElement(URI_XAL, TAG_SUB_ADMINISTRATIVE_AREA);
        if (subAdministrativeArea.getType() != null) {
            writer.writeAttribute(ATT_TYPE, subAdministrativeArea.getType());
        }
        if (subAdministrativeArea.getUsageType() != null) {
            writer.writeAttribute(ATT_USAGE_TYPE, subAdministrativeArea.getUsageType());
        }
        if (subAdministrativeArea.getIndicator() != null) {
            writer.writeAttribute(ATT_INDICATOR, subAdministrativeArea.getIndicator());
        }
        for (GenericTypedGrPostal addressLine : subAdministrativeArea.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (GenericTypedGrPostal name : subAdministrativeArea.getSubAdministrativeAreaNames()) {
            this.writeSubAdministrativeAreaName(name);
        }
        if (subAdministrativeArea.getLocality() != null) {
            this.writeLocality(subAdministrativeArea.getLocality());
        } else if (subAdministrativeArea.getPostOffice() != null) {
            this.writePostOffice(subAdministrativeArea.getPostOffice());
        } else if (subAdministrativeArea.getPostalCode() != null) {
            this.writePostalCode(subAdministrativeArea.getPostalCode());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param postBox
     * @throws XMLStreamException
     */
    private void writePostBox(PostBox postBox) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POST_BOX);
        if (postBox.getType() != null) {
            writer.writeAttribute(ATT_TYPE, postBox.getType());
        }
        if (postBox.getIndicator() != null) {
            writer.writeAttribute(ATT_INDICATOR, postBox.getIndicator());
        }
        for (GenericTypedGrPostal addressLine : postBox.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        if (postBox.getPostBoxNumber() != null) {
            this.writePostBoxNumber(postBox.getPostBoxNumber());
        }
        if (postBox.getPostBoxNumberPrefix() != null) {
            this.writePostBoxNumberPrefix(postBox.getPostBoxNumberPrefix());
        }
        if (postBox.getPostBoxNumberSuffix() != null) {
            this.writePostBoxNumberSuffix(postBox.getPostBoxNumberSuffix());
        }
        if (postBox.getPostBoxNumberExtension() != null) {
            this.writePostBoxNumberExtension(postBox.getPostBoxNumberExtension());
        }
        if (postBox.getFirm() != null) {
            this.writeFirm(postBox.getFirm());
        }
        if (postBox.getPostalCode() != null) {
            this.writePostalCode(postBox.getPostalCode());
        }
        writer.writeEndElement();

    }

    /**
     *
     * @param postBoxNumber
     * @throws XMLStreamException
     */
    private void writePostBoxNumber(PostBoxNumber postBoxNumber) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POST_BOX_NUMBER);
        if (postBoxNumber.getGrPostal() != null) {
            this.writeGrPostal(postBoxNumber.getGrPostal());
        }
        if (postBoxNumber.getContent() != null) {
            writer.writeCharacters(postBoxNumber.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param postBoxNumberPrefix
     * @throws XMLStreamException
     */
    private void writePostBoxNumberPrefix(PostBoxNumberPrefix postBoxNumberPrefix) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POST_BOX_NUMBER_PREFIX);
        if (postBoxNumberPrefix.getGrPostal() != null) {
            this.writeGrPostal(postBoxNumberPrefix.getGrPostal());
        }
        if (postBoxNumberPrefix.getNumberPrefixSeparator() != null) {
            writer.writeAttribute(ATT_NUMBER_PREFIX_SEPARATOR,
                    postBoxNumberPrefix.getNumberPrefixSeparator());
        }
        if (postBoxNumberPrefix.getContent() != null) {
            writer.writeCharacters(postBoxNumberPrefix.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param postBoxNumberSuffix
     * @throws XMLStreamException
     */
    private void writePostBoxNumberSuffix(PostBoxNumberSuffix postBoxNumberSuffix) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POST_BOX_NUMBER_SUFFIX);
        if (postBoxNumberSuffix.getGrPostal() != null) {
            this.writeGrPostal(postBoxNumberSuffix.getGrPostal());
        }
        if (postBoxNumberSuffix.getNumberSuffixSeparator() != null) {
            writer.writeAttribute(ATT_NUMBER_SUFFIX_SEPARATOR,
                    postBoxNumberSuffix.getNumberSuffixSeparator());
        }
        if (postBoxNumberSuffix.getContent() != null) {
            writer.writeCharacters(postBoxNumberSuffix.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param postBoxNumberExtension
     * @throws XMLStreamException
     */
    private void writePostBoxNumberExtension(PostBoxNumberExtension postBoxNumberExtension) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POST_BOX_NUMBER_EXTENSION);
        if (postBoxNumberExtension.getNumberExtensionSeparator() != null) {
            writer.writeAttribute(ATT_NUMBER_EXTENSION_SEPARATOR,
                    postBoxNumberExtension.getNumberExtensionSeparator());
        }
        if (postBoxNumberExtension.getContent() != null) {
            writer.writeCharacters(postBoxNumberExtension.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param firm
     * @throws XMLStreamException
     */
    private void writeFirm(Firm firm) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_FIRM);
        if (firm.getType() != null) {
            writer.writeAttribute(ATT_TYPE, firm.getType());
        }
        for (GenericTypedGrPostal addressLine : firm.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (GenericTypedGrPostal firmName : firm.getFirmNames()) {
            this.writeFirmName(firmName);
        }
        for (Department department : firm.getDepartments()) {
            this.writeDepartment(department);
        }
        if (firm.getMailStop() != null) {
            this.writeMailStop(firm.getMailStop());
        }
        if (firm.getPostalCode() != null) {
            this.writePostalCode(firm.getPostalCode());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param department
     * @throws XMLStreamException
     */
    private void writeDepartment(Department department) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_DEPARTMENT);
        if (department.getType() != null) {
            writer.writeAttribute(ATT_TYPE, department.getType());
        }
        for (GenericTypedGrPostal addressLine : department.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (GenericTypedGrPostal firmName : department.getDepartmentNames()) {
            this.writeDepartmentName(firmName);
        }
        if (department.getMailStop() != null) {
            this.writeMailStop(department.getMailStop());
        }
        if (department.getPostalCode() != null) {
            this.writePostalCode(department.getPostalCode());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param mailStop
     * @throws XMLStreamException
     */
    private void writeMailStop(MailStop mailStop) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_MAIL_STOP);
        if (mailStop.getType() != null) {
            writer.writeAttribute(ATT_TYPE, mailStop.getType());
        }
        for (GenericTypedGrPostal addressLine : mailStop.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (GenericTypedGrPostal mailStopName : mailStop.getMailStopNames()) {
            this.writeMailStopName(mailStopName);
        }
        if (mailStop.getMailStopNumber() != null) {
            this.writeMailStopNumber(mailStop.getMailStopNumber());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param mailStopNumber
     * @throws XMLStreamException
     */
    private void writeMailStopNumber(MailStopNumber mailStopNumber) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_MAIL_STOP_NUMBER);
        if (mailStopNumber.getNameNumberSeparator() != null) {
            writer.writeAttribute(ATT_NAME_NUMBER_SEPARATOR,
                    mailStopNumber.getNameNumberSeparator());
        }
        if (mailStopNumber.getGrPostal() != null) {
            this.writeGrPostal(mailStopNumber.getGrPostal());
        }
        if (mailStopNumber.getContent() != null) {
            writer.writeCharacters(mailStopNumber.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param postOffice
     * @throws XMLStreamException
     */
    private void writePostOffice(PostOffice postOffice) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POST_OFFICE);

        if (postOffice.getType() != null) {
            writer.writeAttribute(ATT_TYPE, postOffice.getType());
        }
        if (postOffice.getIndicator() != null) {
            writer.writeAttribute(ATT_INDICATOR, postOffice.getIndicator());
        }
        for (GenericTypedGrPostal addressLine : postOffice.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        System.out.println("POST OFFICE : "+postOffice);
        System.out.println("POST OFFICE NAMES : "+postOffice.getPostOfficeNames().size());
        for (GenericTypedGrPostal postOfficeName : postOffice.getPostOfficeNames()) {
            this.writePostOfficeName(postOfficeName);
        }
        if (postOffice.getPostOfficeNumber() != null) {
            this.writePostOfficeNumber(postOffice.getPostOfficeNumber());
        }
        if (postOffice.getPostalRoute() != null) {
            this.writePostalRoute(postOffice.getPostalRoute());
        }
        if (postOffice.getPostBox() != null) {
            this.writePostBox(postOffice.getPostBox());
        }
        if (postOffice.getPostalCode() != null) {
            this.writePostalCode(postOffice.getPostalCode());
        }

        writer.writeEndElement();
    }

    /**
     *
     * @param postalCode
     * @throws XMLStreamException
     */
    private void writePostalCode(PostalCode postalCode) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POSTAL_CODE);
        if (postalCode.getType() != null) {
            writer.writeAttribute(ATT_TYPE, postalCode.getType());
        }
        for (GenericTypedGrPostal addressLine : postalCode.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (GenericTypedGrPostal postalCodeNumber : postalCode.getPostalCodeNumbers()) {
            this.writePostalCodeNumber(postalCodeNumber);
        }
        for (PostalCodeNumberExtension postalCodeNumberExtension : postalCode.getPostalCodeNumberExtensions()) {
            this.writePostalCodeNumberExtension(postalCodeNumberExtension);
        }
        if (postalCode.getPostTown() != null) {
            this.writePostTown(postalCode.getPostTown());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param postalCodeNumberExtension
     * @throws XMLStreamException
     */
    private void writePostalCodeNumberExtension(PostalCodeNumberExtension postalCodeNumberExtension) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POSTAL_CODE_NUMBER_EXTENSION);
        if (postalCodeNumberExtension.getType() != null) {
            writer.writeAttribute(ATT_TYPE, postalCodeNumberExtension.getType());
        }
        if (postalCodeNumberExtension.getNumberExtensionSeparator() != null) {
            writer.writeAttribute(ATT_NUMBER_EXTENSION_SEPARATOR,
                    postalCodeNumberExtension.getNumberExtensionSeparator());
        }
        if (postalCodeNumberExtension.getGrPostal() != null) {
            this.writeGrPostal(postalCodeNumberExtension.getGrPostal());
        }
        if (postalCodeNumberExtension.getContent() != null) {
            writer.writeCharacters(postalCodeNumberExtension.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param postTown
     * @throws XMLStreamException
     */
    private void writePostTown(PostTown postTown) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POST_TOWN);
        if (postTown.getType() != null) {
            writer.writeAttribute(ATT_TYPE, postTown.getType());
        }
        for (GenericTypedGrPostal addressLine : postTown.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (GenericTypedGrPostal postTownName : postTown.getPostTownNames()) {
            this.writeTownName(postTownName);
        }
        if (postTown.getPostTownSuffix() != null) {
            this.writeTownSuffix(postTown.getPostTownSuffix());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param postTownSuffix
     * @throws XMLStreamException
     */
    private void writeTownSuffix(PostTownSuffix postTownSuffix) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POST_TOWN_SUFFIX);
        if (postTownSuffix.getGrPostal() != null) {
            this.writeGrPostal(postTownSuffix.getGrPostal());
        }
        if (postTownSuffix.getContent() != null) {
            writer.writeCharacters(postTownSuffix.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param largeMailUser
     * @throws XMLStreamException
     * @throws XalException
     */
    private void writeLargeMailUser(LargeMailUser largeMailUser) 
            throws XMLStreamException, XalException {

        writer.writeStartElement(URI_XAL, TAG_LARGE_MAIL_USER);
        if (largeMailUser.getType() != null) {
            writer.writeAttribute(ATT_TYPE, largeMailUser.getType());
        }
        for (GenericTypedGrPostal addressLine : largeMailUser.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (LargeMailUserName largeMailUserName : largeMailUser.getLargeMailUserNames()) {
            this.writeLargeMailUserName(largeMailUserName);
        }
        if (largeMailUser.getLargeMailUserIdentifier() != null) {
            this.writeLargeMailUserIdentifier(largeMailUser.getLargeMailUserIdentifier());
        }
        for (BuildingName buildingName : largeMailUser.getBuildingNames()) {
            this.writeBuildingName(buildingName);
        }
        if (largeMailUser.getDepartment() != null) {
            this.writeDepartment(largeMailUser.getDepartment());
        }
        if (largeMailUser.getPostBox() != null) {
            this.writePostBox(largeMailUser.getPostBox());
        }
        if (largeMailUser.getThoroughfare() != null) {
            this.writeThoroughfare(largeMailUser.getThoroughfare());
        }
        if (largeMailUser.getPostalCode() != null) {
            this.writePostalCode(largeMailUser.getPostalCode());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param largeMailUserName
     * @throws XMLStreamException
     */
    private void writeLargeMailUserName(LargeMailUserName largeMailUserName) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_LARGE_MAIL_USER_NAME);
        if (largeMailUserName.getType() != null) {
            writer.writeAttribute(ATT_TYPE, largeMailUserName.getType());
        }
        if (largeMailUserName.getCode() != null) {
            writer.writeAttribute(ATT_CODE, largeMailUserName.getCode());
        }
        if (largeMailUserName.getContent() != null) {
            writer.writeCharacters(largeMailUserName.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param largeMailUserIdentifier
     * @throws XMLStreamException
     */
    private void writeLargeMailUserIdentifier(LargeMailUserIdentifier largeMailUserIdentifier) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_LARGE_MAIL_USER_IDENTIFIER);
        if (largeMailUserIdentifier.getType() != null) {
            writer.writeAttribute(ATT_TYPE, largeMailUserIdentifier.getType());
        }
        if (largeMailUserIdentifier.getIndicator() != null) {
            writer.writeAttribute(ATT_INDICATOR, largeMailUserIdentifier.getIndicator());
        }
        if (largeMailUserIdentifier.getGrPostal() != null) {
            this.writeGrPostal(largeMailUserIdentifier.getGrPostal());
        }
        if (largeMailUserIdentifier.getContent() != null) {
            writer.writeCharacters(largeMailUserIdentifier.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param buildingName
     * @throws XMLStreamException
     */
    private void writeBuildingName(BuildingName buildingName) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_BUILDING_NAME);
        if (buildingName.getType() != null) {
            writer.writeAttribute(ATT_TYPE, buildingName.getType());
        }
        if (buildingName.getTypeOccurrence() != null) {
            writer.writeAttribute(ATT_TYPE_OCCURRENCE,
                    buildingName.getTypeOccurrence().getAfterBeforeEnum());
        }
        if (buildingName.getGrPostal() != null) {
            this.writeGrPostal(buildingName.getGrPostal());
        }
        if (buildingName.getContent() != null) {
            writer.writeCharacters(buildingName.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param premiseName
     * @throws XMLStreamException
     */
    private void writePremiseName(PremiseName premiseName) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_PREMISE_NAME);
        if (premiseName.getType() != null) {
            writer.writeAttribute(ATT_TYPE, premiseName.getType());
        }
        if (premiseName.getTypeOccurrence() != null) {
            writer.writeAttribute(ATT_TYPE_OCCURRENCE,
                    premiseName.getTypeOccurrence().getAfterBeforeEnum());
        }
        if (premiseName.getGrPostal() != null) {
            this.writeGrPostal(premiseName.getGrPostal());
        }
        if (premiseName.getContent() != null) {
            writer.writeCharacters(premiseName.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param premiseLocation
     * @throws XMLStreamException
     */
    private void writePremiseLocation(PremiseLocation premiseLocation) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_PREMISE_LOCATION);
        if (premiseLocation.getGrPostal() != null) {
            this.writeGrPostal(premiseLocation.getGrPostal());
        }
        if (premiseLocation.getContent() != null) {
            writer.writeCharacters(premiseLocation.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param subPremiseName
     * @throws XMLStreamException
     */
    private void writeSubPremiseName(SubPremiseName subPremiseName) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_SUB_PREMISE_NAME);
        if (subPremiseName.getType() != null) {
            writer.writeAttribute(ATT_TYPE, subPremiseName.getType());
        }
        if (subPremiseName.getTypeOccurrence() != null) {
            writer.writeAttribute(ATT_TYPE_OCCURRENCE,
                    subPremiseName.getTypeOccurrence().getAfterBeforeEnum());
        }
        if (subPremiseName.getGrPostal() != null) {
            this.writeGrPostal(subPremiseName.getGrPostal());
        }
        if (subPremiseName.getContent() != null) {
            writer.writeCharacters(subPremiseName.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param subPremiseLocation
     * @throws XMLStreamException
     */
    private void writeSubPremiseLocation(SubPremiseLocation subPremiseLocation) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_SUB_PREMISE_LOCATION);
        if (subPremiseLocation.getGrPostal() != null) {
            this.writeGrPostal(subPremiseLocation.getGrPostal());
        }
        if (subPremiseLocation.getContent() != null) {
            writer.writeCharacters(subPremiseLocation.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param postalRoute
     * @throws XMLStreamException
     */
    private void writePostalRoute(PostalRoute postalRoute) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POSTAL_ROUTE);
        if (postalRoute.getType() != null) {
            writer.writeAttribute(ATT_TYPE, postalRoute.getType());
        }
        for (GenericTypedGrPostal addressLine : postalRoute.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (GenericTypedGrPostal postalRouteNames : postalRoute.getPostalRouteNames()) {
            this.writePostalRouteName(postalRouteNames);
        }
        if (postalRoute.getPostalRouteNumber() != null) {
            this.writePostalRouteNumber(postalRoute.getPostalRouteNumber());
        }
        if (postalRoute.getPostBox() != null) {
            this.writePostBox(postalRoute.getPostBox());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param postalRouteNumber
     * @throws XMLStreamException
     */
    private void writePostalRouteNumber(PostalRouteNumber postalRouteNumber) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POSTAL_ROUTE_NUMBER);
        if (postalRouteNumber.getGrPostal() != null) {
            this.writeGrPostal(postalRouteNumber.getGrPostal());
        }
        if (postalRouteNumber.getContent() != null) {
            writer.writeCharacters(postalRouteNumber.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param postOfficeNumber
     * @throws XMLStreamException
     */
    private void writePostOfficeNumber(PostOfficeNumber postOfficeNumber) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_POST_OFFICE_NUMBER);
        if (postOfficeNumber.getIndicator() != null) {
            writer.writeAttribute(ATT_INDICATOR, postOfficeNumber.getIndicator());
        }
        if (postOfficeNumber.getIndicatorOccurrence() != null) {
            writer.writeAttribute(ATT_INDICATOR_OCCURRENCE,
                    postOfficeNumber.getIndicatorOccurrence().getAfterBeforeEnum());
        }
        if (postOfficeNumber.getGrPostal() != null) {
            this.writeGrPostal(postOfficeNumber.getGrPostal());
        }
        if (postOfficeNumber.getContent() != null) {
            writer.writeCharacters(postOfficeNumber.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param dependentLocalityNumber
     * @throws XMLStreamException
     */
    private void writeDependentLocalityNumber(DependentLocalityNumber dependentLocalityNumber) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_DEPENDENT_LOCALITY_NUMBER);
        if (dependentLocalityNumber.getNameNumberOccurrence() != null) {
            writer.writeAttribute(ATT_NAME_NUMBER_OCCURRENCE,
                    dependentLocalityNumber.getNameNumberOccurrence().getAfterBeforeEnum());
        }
        if (dependentLocalityNumber.getGrPostal() != null) {
            this.writeGrPostal(dependentLocalityNumber.getGrPostal());
        }
        if (dependentLocalityNumber.getContent() != null) {
            writer.writeCharacters(dependentLocalityNumber.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param premise
     * @throws XMLStreamException
     */
    private void writePremise(Premise premise) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_PREMISE);
        if (premise.getType() != null) {
            writer.writeAttribute(ATT_TYPE, premise.getType());
        }
        if (premise.getPremiseDependency() != null) {
            writer.writeAttribute(ATT_PREMISE_DEPENDENCY, premise.getPremiseDependency());
        }
        if (premise.getPremiseDependencyType() != null) {
            writer.writeAttribute(ATT_PREMISE_DEPENDENCY_TYPE, premise.getPremiseDependencyType());
        }
        if (premise.getPremiseThoroughfareConnector() != null) {
            writer.writeAttribute(ATT_PREMISE_THOROUGHFARE_CONNECTOR,
                    premise.getPremiseThoroughfareConnector());
        }
        for (GenericTypedGrPostal addressLine : premise.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (PremiseName premiseName : premise.getPremiseNames()) {
            this.writePremiseName(premiseName);
        }
        if (premise.getPremiseLocation() != null) {
            this.writePremiseLocation(premise.getPremiseLocation());
        }
        for (PremiseNumber premiseNumber : premise.getPremiseNumbers()) {
            this.writePremiseNumber(premiseNumber);
        }
        if (premise.getPremiseNumberRange() != null) {
            this.writePremiseNumberRange(premise.getPremiseNumberRange());
        }
        for (PremiseNumberPrefix premiseNumberPrefix : premise.getPremiseNumberPrefixes()) {
            this.writePremiseNumberPrefix(premiseNumberPrefix);
        }
        for (PremiseNumberSuffix premiseNumberSuffix : premise.getPremiseNumberSuffixes()) {
            this.writePremiseNumberSuffix(premiseNumberSuffix);
        }
        for (BuildingName buildingName : premise.getBuildingNames()) {
            this.writeBuildingName(buildingName);
        }
        for (SubPremise subPremise : premise.getSubPremises()) {
            this.writeSubPremise(subPremise);
        }
        if (premise.getFirm() != null) {
            this.writeFirm(premise.getFirm());
        }
        if (premise.getMailStop() != null) {
            this.writeMailStop(premise.getMailStop());
        }
        if (premise.getPostalCode() != null) {
            this.writePostalCode(premise.getPostalCode());
        }
        if (premise.getPremise() != null) {
            this.writePremise(premise.getPremise());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param premiseNumber
     * @throws XMLStreamException
     */
    private void writePremiseNumber(PremiseNumber premiseNumber) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_PREMISE_NUMBER);
        if (premiseNumber.getNumberType() != null) {
            writer.writeAttribute(ATT_NUMBER_TYPE, premiseNumber.getNumberType().getSingleRange());
        }
        if (premiseNumber.getType() != null) {
            writer.writeAttribute(ATT_TYPE, premiseNumber.getType());
        }
        if (premiseNumber.getIndicator() != null) {
            writer.writeAttribute(ATT_INDICATOR, premiseNumber.getIndicator());
        }
        if (premiseNumber.getIndicatorOccurrence() != null) {
            writer.writeAttribute(ATT_INDICATOR_OCCURRENCE, premiseNumber.getIndicatorOccurrence().getAfterBeforeEnum());
        }
        if (premiseNumber.getNumberTypeOccurrence() != null) {
            writer.writeAttribute(ATT_NUMBER_TYPE_OCCURRENCE, premiseNumber.getNumberTypeOccurrence().getAfterBeforeEnum());
        }
        if (premiseNumber.getGrPostal() != null) {
            this.writeGrPostal(premiseNumber.getGrPostal());
        }
        if (premiseNumber.getContent() != null) {
            writer.writeCharacters(premiseNumber.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param subPremiseNumber
     * @throws XMLStreamException
     */
    private void writeSubPremiseNumber(SubPremiseNumber subPremiseNumber) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_SUB_PREMISE_NUMBER);
        if (subPremiseNumber.getIndicator() != null) {
            writer.writeAttribute(ATT_INDICATOR, subPremiseNumber.getIndicator());
        }
        if (subPremiseNumber.getIndicatorOccurrence() != null) {
            writer.writeAttribute(ATT_INDICATOR_OCCURRENCE, subPremiseNumber.getIndicatorOccurrence().getAfterBeforeEnum());
        }
        if (subPremiseNumber.getNumberTypeOccurrence() != null) {
            writer.writeAttribute(ATT_NUMBER_TYPE_OCCURRENCE, subPremiseNumber.getNumberTypeOccurrence().getAfterBeforeEnum());
        }
        if (subPremiseNumber.getPremiseNumberSeparator() != null) {
            writer.writeAttribute(ATT_PREMISE_NUMBER_SEPARATOR, subPremiseNumber.getPremiseNumberSeparator());
        }
        if (subPremiseNumber.getType() != null) {
            writer.writeAttribute(ATT_TYPE, subPremiseNumber.getType());
        }
        if (subPremiseNumber.getGrPostal() != null) {
            this.writeGrPostal(subPremiseNumber.getGrPostal());
        }
        if (subPremiseNumber.getContent() != null) {
            writer.writeCharacters(subPremiseNumber.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param premiseNumberRange
     * @throws XMLStreamException
     */
    private void writePremiseNumberRange(PremiseNumberRange premiseNumberRange) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_PREMISE_NUMBER_RANGE);
        if (premiseNumberRange.getRangeType() != null) {
            writer.writeAttribute(ATT_RANGE_TYPE, premiseNumberRange.getRangeType());
        }
        if (premiseNumberRange.getIndicator() != null) {
            writer.writeAttribute(ATT_INDICATOR, premiseNumberRange.getIndicator());
        }
        if (premiseNumberRange.getSeparator() != null) {
            writer.writeAttribute(ATT_SEPARATOR, premiseNumberRange.getSeparator());
        }
        if (premiseNumberRange.getType() != null) {
            writer.writeAttribute(ATT_TYPE, premiseNumberRange.getType());
        }
        if (premiseNumberRange.getIndicatorOccurrence() != null) {
            writer.writeAttribute(ATT_INDICATOR_OCCURRENCE,
                    premiseNumberRange.getIndicatorOccurrence().getAfterBeforeEnum());
        }
        if (premiseNumberRange.getNumberRangeOccurrence() != null) {
            writer.writeAttribute(ATT_NUMBER_RANGE_OCCURRENCE,
                    premiseNumberRange.getNumberRangeOccurrence().getAfterBeforeTypeEnum());
        }
        if (premiseNumberRange.getPremiseNumberRangeFrom() != null) {
            this.writePremiseNumberRangeFrom(premiseNumberRange.getPremiseNumberRangeFrom());
        }
        if (premiseNumberRange.getPremiseNumberRangeTo() != null) {
            this.writePremiseNumberRangeTo(premiseNumberRange.getPremiseNumberRangeTo());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param premiseNumberRangeFrom
     * @throws XMLStreamException
     */
    private void writePremiseNumberRangeFrom(PremiseNumberRangeFrom premiseNumberRangeFrom) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_PREMISE_NUMBER_RANGE_FROM);
        for (GenericTypedGrPostal addressLine : premiseNumberRangeFrom.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (PremiseNumberPrefix premiseNumberPrefix : premiseNumberRangeFrom.getPremiseNumberPrefixes()) {
            this.writePremiseNumberPrefix(premiseNumberPrefix);
        }
        for (PremiseNumber premiseNumber : premiseNumberRangeFrom.getPremiseNumbers()) {
            this.writePremiseNumber(premiseNumber);
        }
        for (PremiseNumberSuffix premiseNumberSuffix : premiseNumberRangeFrom.getPremiseNumberSuffixes()) {
            this.writePremiseNumberSuffix(premiseNumberSuffix);
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param premiseNumberRangeTo
     * @throws XMLStreamException
     */
    private void writePremiseNumberRangeTo(PremiseNumberRangeTo premiseNumberRangeTo) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_PREMISE_NUMBER_RANGE_TO);
        for (GenericTypedGrPostal addressLine : premiseNumberRangeTo.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (PremiseNumberPrefix premiseNumberPrefix : premiseNumberRangeTo.getPremiseNumberPrefixes()) {
            this.writePremiseNumberPrefix(premiseNumberPrefix);
        }
        for (PremiseNumber premiseNumber : premiseNumberRangeTo.getPremiseNumbers()) {
            this.writePremiseNumber(premiseNumber);
        }
        for (PremiseNumberSuffix premiseNumberSuffix : premiseNumberRangeTo.getPremiseNumberSuffixes()) {
            this.writePremiseNumberSuffix(premiseNumberSuffix);
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param premiseNumberPrefix
     * @throws XMLStreamException
     */
    private void writePremiseNumberPrefix(PremiseNumberPrefix premiseNumberPrefix) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_PREMISE_NUMBER_PREFIX);
        if (premiseNumberPrefix.getNumberPrefixSeparator() != null) {
            writer.writeAttribute(ATT_NUMBER_PREFIX_SEPARATOR,
                    premiseNumberPrefix.getNumberPrefixSeparator());
        }
        if (premiseNumberPrefix.getType() != null) {
            writer.writeAttribute(ATT_TYPE, premiseNumberPrefix.getType());
        }
        if (premiseNumberPrefix.getGrPostal() != null) {
            this.writeGrPostal(premiseNumberPrefix.getGrPostal());
        }
        if (premiseNumberPrefix.getContent() != null) {
            writer.writeCharacters(premiseNumberPrefix.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param premiseNumberSuffix
     * @throws XMLStreamException
     */
    private void writePremiseNumberSuffix(PremiseNumberSuffix premiseNumberSuffix) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_PREMISE_NUMBER_SUFFIX);
        if (premiseNumberSuffix.getNumberSuffixSeparator() != null) {
            writer.writeAttribute(ATT_NUMBER_SUFFIX_SEPARATOR,
                    premiseNumberSuffix.getNumberSuffixSeparator());
        }
        if (premiseNumberSuffix.getType() != null) {
            writer.writeAttribute(ATT_TYPE, premiseNumberSuffix.getType());
        }
        if (premiseNumberSuffix.getGrPostal() != null) {
            this.writeGrPostal(premiseNumberSuffix.getGrPostal());
        }
        if (premiseNumberSuffix.getContent() != null) {
            writer.writeCharacters(premiseNumberSuffix.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param subPremiseNumberPrefix
     * @throws XMLStreamException
     */
    private void writeSubPremiseNumberPrefix(SubPremiseNumberPrefix subPremiseNumberPrefix) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_SUB_PREMISE_NUMBER_PREFIX);
        if (subPremiseNumberPrefix.getNumberPrefixSeparator() != null) {
            writer.writeAttribute(ATT_NUMBER_PREFIX_SEPARATOR,
                    subPremiseNumberPrefix.getNumberPrefixSeparator());
        }
        if (subPremiseNumberPrefix.getType() != null) {
            writer.writeAttribute(ATT_TYPE, subPremiseNumberPrefix.getType());
        }
        if (subPremiseNumberPrefix.getGrPostal() != null) {
            this.writeGrPostal(subPremiseNumberPrefix.getGrPostal());
        }
        if (subPremiseNumberPrefix.getContent() != null) {
            writer.writeCharacters(subPremiseNumberPrefix.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param subPremiseNumberSuffix
     * @throws XMLStreamException
     */
    private void writeSubPremiseNumberSuffix(SubPremiseNumberSuffix subPremiseNumberSuffix) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_SUB_PREMISE_NUMBER_SUFFIX);
        if (subPremiseNumberSuffix.getNumberSuffixSeparator() != null) {
            writer.writeAttribute(ATT_NUMBER_SUFFIX_SEPARATOR,
                    subPremiseNumberSuffix.getNumberSuffixSeparator());
        }
        if (subPremiseNumberSuffix.getType() != null) {
            writer.writeAttribute(ATT_TYPE, subPremiseNumberSuffix.getType());
        }
        if (subPremiseNumberSuffix.getGrPostal() != null) {
            this.writeGrPostal(subPremiseNumberSuffix.getGrPostal());
        }
        if (subPremiseNumberSuffix.getContent() != null) {
            writer.writeCharacters(subPremiseNumberSuffix.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param subPremise
     * @throws XMLStreamException
     */
    private void writeSubPremise(SubPremise subPremise) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_SUB_PREMISE);
        if (subPremise.getType() != null) {
            writer.writeAttribute(ATT_TYPE, subPremise.getType());
        }
        for (GenericTypedGrPostal addressLine : subPremise.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (SubPremiseName subPremiseName : subPremise.getSubPremiseNames()) {
            this.writeSubPremiseName(subPremiseName);
        }
        if (subPremise.getSubPremiseLocation() != null) {
            this.writeSubPremiseLocation(subPremise.getSubPremiseLocation());
        }
        for (SubPremiseNumber subPremiseNumber : subPremise.getSubPremiseNumbers()) {
            this.writeSubPremiseNumber(subPremiseNumber);
        }
        for (SubPremiseNumberPrefix subPremiseNumberPrefix : subPremise.getSubPremiseNumberPrefixes()) {
            this.writeSubPremiseNumberPrefix(subPremiseNumberPrefix);
        }
        for (SubPremiseNumberSuffix subPremiseNumberSuffix : subPremise.getSubPremiseNumberSuffixes()) {
            this.writeSubPremiseNumberSuffix(subPremiseNumberSuffix);
        }
        for (BuildingName buildingName : subPremise.getBuildingNames()) {
            this.writeBuildingName(buildingName);
        }
        if (subPremise.getFirm() != null) {
            this.writeFirm(subPremise.getFirm());
        }
        if (subPremise.getMailStop() != null) {
            this.writeMailStop(subPremise.getMailStop());
        }
        if (subPremise.getPostalCode() != null) {
            this.writePostalCode(subPremise.getPostalCode());
        }
        if (subPremise.getSubPremise() != null) {
            this.writeSubPremise(subPremise.getSubPremise());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param thoroughfare
     * @throws XMLStreamException
     * @throws XalException
     */
    private void writeThoroughfare(Thoroughfare thoroughfare) 
            throws XMLStreamException, XalException {

        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE);
        if (thoroughfare.getType() != null) {
            writer.writeAttribute(ATT_TYPE, thoroughfare.getType());
        }
        if (thoroughfare.getDependentThoroughfares() != null) {
            writer.writeAttribute(ATT_DEPENDENT_THOROUGHFARES,
                    thoroughfare.getDependentThoroughfares().getDependentThoroughfares());
        }
        if (thoroughfare.getDependentThoroughfaresIndicator() != null) {
            writer.writeAttribute(ATT_DEPENDENT_THOROUGHFARES_INDICATOR,
                    thoroughfare.getDependentThoroughfaresIndicator());
        }
        if (thoroughfare.getDependentThoroughfaresConnector() != null) {
            writer.writeAttribute(ATT_DEPENDENT_THOROUGHFARES_CONNECTOR,
                    thoroughfare.getDependentThoroughfaresConnector());
        }
        if (thoroughfare.getDependentThoroughfaresType() != null) {
            writer.writeAttribute(ATT_DEPENDENT_THOROUGHFARES_TYPE,
                    thoroughfare.getDependentThoroughfaresType());
        }
        for (GenericTypedGrPostal addressLine : thoroughfare.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        for (Object object : thoroughfare.getThoroughfareNumbers()) {
            if (object instanceof ThoroughfareNumber) {
                this.writeThoroughfareNumber((ThoroughfareNumber) object);
            } else if (object instanceof ThoroughfareNumberRange) {
                this.writeThoroughfareNumberRange((ThoroughfareNumberRange) object);
            } else {
                throw new XalException("Error writting Thoroughfare.\n"
                        + "Requiered here " + ThoroughfareNumber.class.toString() + " or "
                        + ThoroughfareNumberRange.class.toString() + ".");
            }
        }
        for (ThoroughfareNumberPrefix prefix : thoroughfare.getThoroughfareNumberPrefixes()) {
            this.writeThoroughfareNumberPrefix(prefix);
        }
        for (ThoroughfareNumberSuffix suffix : thoroughfare.getThoroughfareNumberSuffixes()) {
            this.writeThoroughfareNumberSuffix(suffix);
        }
        if (thoroughfare.getThoroughfarePreDirection() != null) {
            this.writeThoroughfarePreDirection(thoroughfare.getThoroughfarePreDirection());
        }
        if (thoroughfare.getThoroughfareLeadingType() != null) {
            this.writeThoroughfareLeadingType(thoroughfare.getThoroughfareLeadingType());
        }
        for (GenericTypedGrPostal name : thoroughfare.getThoroughfareNames()) {
            this.writeThoroughfareName(name);
        }
        if (thoroughfare.getThoroughfareTrailingType() != null) {
            this.writeThoroughfareTrailingType(thoroughfare.getThoroughfareTrailingType());
        }
        if (thoroughfare.getThoroughfarePostDirection() != null) {
            this.writeThoroughfarePostDirection(thoroughfare.getThoroughfarePostDirection());
        }
        if (thoroughfare.getDependentThoroughfare() != null) {
            this.writeDependentThoroughfare(thoroughfare.getDependentThoroughfare());
        }
        if (thoroughfare.getDependentLocality() != null) {
            this.writeDependentLocality(thoroughfare.getDependentLocality());
        }
        if (thoroughfare.getPremise() != null) {
            this.writePremise(thoroughfare.getPremise());
        }
        if (thoroughfare.getFirm() != null) {
            this.writeFirm(thoroughfare.getFirm());
        }
        if (thoroughfare.getPostalCode() != null) {
            this.writePostalCode(thoroughfare.getPostalCode());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param thoroughfareNumber
     * @throws XMLStreamException
     */
    private void writeThoroughfareNumber(ThoroughfareNumber thoroughfareNumber) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_NUMBER);
        if (thoroughfareNumber.getNumberType() != null) {
            writer.writeAttribute(ATT_NUMBER_TYPE,
                    thoroughfareNumber.getNumberType().getSingleRange());
        }
        if (thoroughfareNumber.getType() != null) {
            writer.writeAttribute(ATT_TYPE, thoroughfareNumber.getType());
        }
        if (thoroughfareNumber.getIndicator() != null) {
            writer.writeAttribute(ATT_INDICATOR, thoroughfareNumber.getIndicator());
        }
        if (thoroughfareNumber.getIndicatorOccurence() != null) {
            writer.writeAttribute(ATT_INDICATOR_OCCURRENCE,
                    thoroughfareNumber.getIndicatorOccurence().getAfterBeforeEnum());
        }
        if (thoroughfareNumber.getNumberOccurence() != null) {
            writer.writeAttribute(ATT_NUMBER_OCCURRENCE,
                    thoroughfareNumber.getNumberOccurence().getAfterBeforeTypeEnum());
        }
        if (thoroughfareNumber.getGrPostal() != null) {
            this.writeGrPostal(thoroughfareNumber.getGrPostal());
        }
        if (thoroughfareNumber.getContent() != null) {
            writer.writeCharacters(thoroughfareNumber.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param thoroughfareNumberRange
     * @throws XMLStreamException
     * @throws XalException
     */
    private void writeThoroughfareNumberRange(ThoroughfareNumberRange thoroughfareNumberRange) 
            throws XMLStreamException, XalException {

        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_NUMBER_RANGE);
        if (thoroughfareNumberRange.getRangeType() != null) {
            writer.writeAttribute(ATT_RANGE_TYPE,
                    thoroughfareNumberRange.getRangeType().getOddEven());
        }
        if (thoroughfareNumberRange.getIndicator() != null) {
            writer.writeAttribute(ATT_INDICATOR, thoroughfareNumberRange.getIndicator());
        }
        if (thoroughfareNumberRange.getSeparator() != null) {
            writer.writeAttribute(ATT_SEPARATOR, thoroughfareNumberRange.getSeparator());
        }
        if (thoroughfareNumberRange.getIndicatorOccurence() != null) {
            writer.writeAttribute(ATT_INDICATOR_OCCURRENCE,
                    thoroughfareNumberRange.getIndicatorOccurence().getAfterBeforeEnum());
        }
        if (thoroughfareNumberRange.getNumberRangeOccurence() != null) {
            writer.writeAttribute(ATT_NUMBER_RANGE_OCCURRENCE,
                    thoroughfareNumberRange.getNumberRangeOccurence().getAfterBeforeTypeEnum());
        }
        if (thoroughfareNumberRange.getType() != null) {
            writer.writeAttribute(ATT_TYPE, thoroughfareNumberRange.getType());
        }
        if (thoroughfareNumberRange.getGrPostal() != null) {
            this.writeGrPostal(thoroughfareNumberRange.getGrPostal());
        }
        for (GenericTypedGrPostal addressLine : thoroughfareNumberRange.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        if (thoroughfareNumberRange.getThoroughfareNumberFrom() != null) {
            this.writeThoroughfareNumberFrom(thoroughfareNumberRange.getThoroughfareNumberFrom());
        }
        if (thoroughfareNumberRange.getThoroughfareNumberTo() != null) {
            this.writeThoroughfareNumberTo(thoroughfareNumberRange.getThoroughfareNumberTo());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param suffix
     * @throws XMLStreamException
     */
    private void writeThoroughfareNumberSuffix(ThoroughfareNumberSuffix suffix) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_NUMBER_SUFFIX);
        if (suffix.getNumberSuffixSeparator() != null) {
            writer.writeAttribute(ATT_NUMBER_SUFFIX_SEPARATOR,
                    suffix.getNumberSuffixSeparator());
        }
        if (suffix.getType() != null) {
            writer.writeAttribute(ATT_TYPE, suffix.getType());
        }
        if (suffix.getGrPostal() != null) {
            this.writeGrPostal(suffix.getGrPostal());
        }
        if (suffix.getContent() != null) {
            writer.writeCharacters(suffix.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param prefix
     * @throws XMLStreamException
     */
    private void writeThoroughfareNumberPrefix(ThoroughfareNumberPrefix prefix) 
            throws XMLStreamException {

        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_NUMBER_PREFIX);
        if (prefix.getNumberPrefixSeparator() != null) {
            writer.writeAttribute(ATT_NUMBER_PREFIX_SEPARATOR,
                    prefix.getNumberPrefixSeparator());
        }
        if (prefix.getType() != null) {
            writer.writeAttribute(ATT_TYPE, prefix.getType());
        }
        if (prefix.getGrPostal() != null) {
            this.writeGrPostal(prefix.getGrPostal());
        }
        if (prefix.getContent() != null) {
            writer.writeCharacters(prefix.getContent());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param thoroughfareNumberFrom
     * @throws XMLStreamException
     * @throws XalException
     */
    private void writeThoroughfareNumberFrom(ThoroughfareNumberFrom thoroughfareNumberFrom) 
            throws XMLStreamException, XalException {

        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_NUMBER_FROM);
        if (thoroughfareNumberFrom.getGrPostal() != null) {
            this.writeGrPostal(thoroughfareNumberFrom.getGrPostal());
        }
        for (Object object : thoroughfareNumberFrom.getContent()) {
            if (object instanceof ThoroughfareNumberPrefix) {
                this.writeThoroughfareNumberPrefix((ThoroughfareNumberPrefix) object);
            } else if (object instanceof ThoroughfareNumber) {
                this.writeThoroughfareNumber((ThoroughfareNumber) object);
            } else if (object instanceof ThoroughfareNumberSuffix) {
                this.writeThoroughfareNumberSuffix((ThoroughfareNumberSuffix) object);
            } else if (object instanceof GenericTypedGrPostal) {
                this.writeAddressLine((GenericTypedGrPostal) object);
            } else if (object instanceof String) {
                writer.writeCharacters((String) object);
            } else {
                throw new XalException("Invalid content for " + ThoroughfareNumberFrom.class.toString());
            }
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param thoroughfareNumberTo
     * @throws XMLStreamException
     * @throws XalException
     */
    private void writeThoroughfareNumberTo(ThoroughfareNumberTo thoroughfareNumberTo) 
            throws XMLStreamException, XalException {

        writer.writeStartElement(URI_XAL, TAG_THOROUGHFARE_NUMBER_TO);
        if (thoroughfareNumberTo.getGrPostal() != null) {
            this.writeGrPostal(thoroughfareNumberTo.getGrPostal());
        }
        for (Object object : thoroughfareNumberTo.getContent()) {
            if (object instanceof ThoroughfareNumberPrefix) {
                this.writeThoroughfareNumberPrefix((ThoroughfareNumberPrefix) object);
            } else if (object instanceof ThoroughfareNumber) {
                this.writeThoroughfareNumber((ThoroughfareNumber) object);
            } else if (object instanceof ThoroughfareNumberSuffix) {
                this.writeThoroughfareNumberSuffix((ThoroughfareNumberSuffix) object);
            } else if (object instanceof GenericTypedGrPostal) {
                this.writeAddressLine((GenericTypedGrPostal) object);
            } else if (object instanceof String) {
                writer.writeCharacters((String) object);
            } else {
                throw new XalException("Invalid content for " +
                        ThoroughfareNumberTo.class.toString());
            }
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param thoroughfare
     * @throws XMLStreamException
     */
    private void writeDependentThoroughfare(DependentThoroughfare thoroughfare) 
            throws XMLStreamException {
        
        writer.writeStartElement(URI_XAL, TAG_DEPENDENT_THOROUGHFARE);
        if (thoroughfare.getType() != null) {
            writer.writeAttribute(ATT_TYPE, thoroughfare.getType());
        }
        for (GenericTypedGrPostal addressLine : thoroughfare.getAddressLines()) {
            this.writeAddressLine(addressLine);
        }
        if (thoroughfare.getThoroughfarePreDirection() != null) {
            this.writeThoroughfarePreDirection(thoroughfare.getThoroughfarePreDirection());
        }
        if (thoroughfare.getThoroughfareLeadingType() != null) {
            this.writeThoroughfareLeadingType(thoroughfare.getThoroughfareLeadingType());
        }
        for (GenericTypedGrPostal name : thoroughfare.getThoroughfareNames()) {
            this.writeThoroughfareName(name);
        }
        if (thoroughfare.getThoroughfareTrailingType() != null) {
            this.writeThoroughfareTrailingType(thoroughfare.getThoroughfareTrailingType());
        }
        if (thoroughfare.getThoroughfarePostDirection() != null) {
            this.writeThoroughfarePostDirection(thoroughfare.getThoroughfarePostDirection());
        }
        writer.writeEndElement();
    }
}

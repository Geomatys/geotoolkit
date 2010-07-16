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
package org.geotoolkit.data.xal;

import java.util.List;
import org.geotoolkit.data.xal.model.DefaultAddressLines;
import org.geotoolkit.data.xal.model.AddressDetails;
import org.geotoolkit.data.xal.model.DefaultAddressDetails;
import org.geotoolkit.data.xal.model.AddressIdentifier;
import org.geotoolkit.data.xal.model.DefaultAddressIdentifier;
import org.geotoolkit.data.xal.model.AddressLines;
import org.geotoolkit.data.xal.model.AdministrativeArea;
import org.geotoolkit.data.xal.model.DefaultAdministrativeArea;
import org.geotoolkit.data.xal.model.AfterBeforeEnum;
import org.geotoolkit.data.xal.model.AfterBeforeTypeNameEnum;
import org.geotoolkit.data.xal.model.BuildingName;
import org.geotoolkit.data.xal.model.DefaultBuildingName;
import org.geotoolkit.data.xal.model.Country;
import org.geotoolkit.data.xal.model.DefaultCountry;
import org.geotoolkit.data.xal.model.CountryNameCode;
import org.geotoolkit.data.xal.model.DefaultCountryNameCode;
import org.geotoolkit.data.xal.model.Department;
import org.geotoolkit.data.xal.model.DefaultDepartment;
import org.geotoolkit.data.xal.model.DependentLocality;
import org.geotoolkit.data.xal.model.DefaultDependentLocality;
import org.geotoolkit.data.xal.model.DependentLocalityNumber;
import org.geotoolkit.data.xal.model.DefaultDependentLocalityNumber;
import org.geotoolkit.data.xal.model.DependentThoroughfare;
import org.geotoolkit.data.xal.model.DefaultDependentThoroughfare;
import org.geotoolkit.data.xal.model.DependentThoroughfares;
import org.geotoolkit.data.xal.model.Firm;
import org.geotoolkit.data.xal.model.DefaultFirm;
import org.geotoolkit.data.xal.model.GenericTypedGrPostal;
import org.geotoolkit.data.xal.model.DefaultGenericTypedGrPostal;
import org.geotoolkit.data.xal.model.GrPostal;
import org.geotoolkit.data.xal.model.DefaultGrPostal;
import org.geotoolkit.data.xal.model.LargeMailUser;
import org.geotoolkit.data.xal.model.DefaultLargeMailUser;
import org.geotoolkit.data.xal.model.LargeMailUserIdentifier;
import org.geotoolkit.data.xal.model.DefaultLargeMailUserIdentifier;
import org.geotoolkit.data.xal.model.LargeMailUserName;
import org.geotoolkit.data.xal.model.DefaultLargeMailUserName;
import org.geotoolkit.data.xal.model.Locality;
import org.geotoolkit.data.xal.model.DefaultLocality;
import org.geotoolkit.data.xal.model.MailStop;
import org.geotoolkit.data.xal.model.DefaultMailStop;
import org.geotoolkit.data.xal.model.MailStopNumber;
import org.geotoolkit.data.xal.model.DefaultMailStopNumber;
import org.geotoolkit.data.xal.model.OddEvenEnum;
import org.geotoolkit.data.xal.model.PostBox;
import org.geotoolkit.data.xal.model.DefaultPostBox;
import org.geotoolkit.data.xal.model.PostBoxNumber;
import org.geotoolkit.data.xal.model.DefaultPostBoxNumber;
import org.geotoolkit.data.xal.model.PostBoxNumberExtension;
import org.geotoolkit.data.xal.model.DefaultPostBoxNumberExtension;
import org.geotoolkit.data.xal.model.PostBoxNumberPrefix;
import org.geotoolkit.data.xal.model.DefaultPostBoxNumberPrefix;
import org.geotoolkit.data.xal.model.PostBoxNumberSuffix;
import org.geotoolkit.data.xal.model.DefaultPostBoxNumberSuffix;
import org.geotoolkit.data.xal.model.PostOffice;
import org.geotoolkit.data.xal.model.DefaultPostOffice;
import org.geotoolkit.data.xal.model.PostOfficeNumber;
import org.geotoolkit.data.xal.model.DefaultPostOfficeNumber;
import org.geotoolkit.data.xal.model.PostTown;
import org.geotoolkit.data.xal.model.DefaultPostTown;
import org.geotoolkit.data.xal.model.PostTownSuffix;
import org.geotoolkit.data.xal.model.DefaultPostTownSuffix;
import org.geotoolkit.data.xal.model.PostalCode;
import org.geotoolkit.data.xal.model.DefaultPostalCode;
import org.geotoolkit.data.xal.model.PostalCodeNumberExtension;
import org.geotoolkit.data.xal.model.DefaultPostalCodeNumberExtension;
import org.geotoolkit.data.xal.model.PostalRoute;
import org.geotoolkit.data.xal.model.DefaultPostalRoute;
import org.geotoolkit.data.xal.model.PostalRouteNumber;
import org.geotoolkit.data.xal.model.DefaultPostalRouteNumber;
import org.geotoolkit.data.xal.model.PostalServiceElements;
import org.geotoolkit.data.xal.model.DefaultPostalServiceElements;
import org.geotoolkit.data.xal.model.Premise;
import org.geotoolkit.data.xal.model.DefaultPremise;
import org.geotoolkit.data.xal.model.PremiseLocation;
import org.geotoolkit.data.xal.model.DefaultPremiseLocation;
import org.geotoolkit.data.xal.model.PremiseName;
import org.geotoolkit.data.xal.model.DefaultPremiseName;
import org.geotoolkit.data.xal.model.PremiseNumber;
import org.geotoolkit.data.xal.model.DefaultPremiseNumber;
import org.geotoolkit.data.xal.model.PremiseNumberPrefix;
import org.geotoolkit.data.xal.model.DefaultPremiseNumberPrefix;
import org.geotoolkit.data.xal.model.PremiseNumberRange;
import org.geotoolkit.data.xal.model.DefaultPremiseNumberRange;
import org.geotoolkit.data.xal.model.PremiseNumberRangeFrom;
import org.geotoolkit.data.xal.model.DefaultPremiseNumberRangeFrom;
import org.geotoolkit.data.xal.model.PremiseNumberRangeTo;
import org.geotoolkit.data.xal.model.DefaultPremiseNumberRangeTo;
import org.geotoolkit.data.xal.model.PremiseNumberSuffix;
import org.geotoolkit.data.xal.model.DefaultPremiseNumberSuffix;
import org.geotoolkit.data.xal.model.SingleRangeEnum;
import org.geotoolkit.data.xal.model.SortingCode;
import org.geotoolkit.data.xal.model.DefaultSortingCode;
import org.geotoolkit.data.xal.model.SubAdministrativeArea;
import org.geotoolkit.data.xal.model.DefaultSubAdministrativeArea;
import org.geotoolkit.data.xal.model.SubPremise;
import org.geotoolkit.data.xal.model.DefaultSubPremise;
import org.geotoolkit.data.xal.model.SubPremiseLocation;
import org.geotoolkit.data.xal.model.DefaultSubPremiseLocation;
import org.geotoolkit.data.xal.model.SubPremiseName;
import org.geotoolkit.data.xal.model.DefaultSubPremiseName;
import org.geotoolkit.data.xal.model.SubPremiseNumber;
import org.geotoolkit.data.xal.model.DefaultSubPremiseNumber;
import org.geotoolkit.data.xal.model.SubPremiseNumberPrefix;
import org.geotoolkit.data.xal.model.DefaultSubPremiseNumberPrefix;
import org.geotoolkit.data.xal.model.SubPremiseNumberSuffix;
import org.geotoolkit.data.xal.model.DefaultSubPremiseNumberSuffix;
import org.geotoolkit.data.xal.model.Thoroughfare;
import org.geotoolkit.data.xal.model.DefaultThoroughfare;
import org.geotoolkit.data.xal.model.ThoroughfareNumber;
import org.geotoolkit.data.xal.model.DefaultThoroughfareNumber;
import org.geotoolkit.data.xal.model.ThoroughfareNumberFrom;
import org.geotoolkit.data.xal.model.DefaultThoroughfareNumberFrom;
import org.geotoolkit.data.xal.model.ThoroughfareNumberPrefix;
import org.geotoolkit.data.xal.model.DefaultThoroughfareNumberPrefix;
import org.geotoolkit.data.xal.model.ThoroughfareNumberRange;
import org.geotoolkit.data.xal.model.DefaultThoroughfareNumberRange;
import org.geotoolkit.data.xal.model.ThoroughfareNumberSuffix;
import org.geotoolkit.data.xal.model.DefaultThoroughfareNumberSuffix;
import org.geotoolkit.data.xal.model.ThoroughfareNumberTo;
import org.geotoolkit.data.xal.model.DefaultThoroughfareNumberTo;
import org.geotoolkit.data.xal.model.Xal;
import org.geotoolkit.data.xal.model.DefaultXal;
import org.geotoolkit.data.xal.model.XalException;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultXalFactory implements XalFactory {

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Xal createXal(List<AddressDetails> addressDetails, String version) {
        return new DefaultXal(addressDetails, version);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AddressDetails createAddressDetails(PostalServiceElements postalServiceElements,
            Object localisation, String addressType, String currentStatus, String validFromDate,
            String validToDate, String usage, GrPostal grPostal, String AddressDetailsKey) throws XalException {
        return new DefaultAddressDetails(postalServiceElements, localisation,
                addressType, currentStatus, validFromDate, validToDate, usage, grPostal, AddressDetailsKey);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AddressLines createAddressLines(List<GenericTypedGrPostal> addressLines) {
        return new DefaultAddressLines(addressLines);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal createGenericTypedGrPostal(String type, GrPostal grPostal, String Content) {
        return new DefaultGenericTypedGrPostal(type, grPostal, Content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal createGrPostal(String code) {
        return new DefaultGrPostal(code);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalServiceElements createPostalServiceElements(List<AddressIdentifier> addressIdentifiers,
            GenericTypedGrPostal endorsementLineCode, GenericTypedGrPostal keyLineCode, GenericTypedGrPostal barCode,
            SortingCode sortingCode, GenericTypedGrPostal addressLatitude, GenericTypedGrPostal addressLatitudeDirection,
            GenericTypedGrPostal addressLongitude, GenericTypedGrPostal addressLongitudeDirection,
            List<GenericTypedGrPostal> supplementaryPostalServiceData, String type) {
        return new DefaultPostalServiceElements(addressIdentifiers,endorsementLineCode,
            keyLineCode, barCode, sortingCode, addressLatitude,
            addressLatitudeDirection, addressLongitude, addressLongitudeDirection,
            supplementaryPostalServiceData, type);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SortingCode createSortingCode(String type, GrPostal grPostal) {
        return new DefaultSortingCode(type, grPostal);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AddressIdentifier createAddressIdentifier(String content, String identifierType,
            String type, GrPostal grPostal) {
        return new DefaultAddressIdentifier(content, identifierType, type, grPostal);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Country createCountry(List<GenericTypedGrPostal> addressLines, List<CountryNameCode> countryNameCodes,
            List<GenericTypedGrPostal> countryNames, Object localisation) throws XalException {
        return new DefaultCountry(addressLines, countryNameCodes, countryNames, localisation);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public CountryNameCode createCountryNameCode(String sheme, GrPostal grPostal, String content) {
        return new DefaultCountryNameCode(sheme, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AdministrativeArea createAdministrativeArea(List<GenericTypedGrPostal> addressLines, 
            List<GenericTypedGrPostal> administrativeAreaNames, SubAdministrativeArea subAdministrativeArea,
            Object localisation, String type, String usageType, String indicator) throws XalException {
        return new DefaultAdministrativeArea(addressLines, administrativeAreaNames,
                subAdministrativeArea, localisation, type, usageType, indicator);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SubAdministrativeArea createSubAdministrativeArea(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> subAdministrativeAreaNames,
            Object localisation, String type, String usageType, String indicator) throws XalException {
        return new DefaultSubAdministrativeArea(addressLines, subAdministrativeAreaNames, localisation, type, usageType, indicator);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Locality createLocality(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> localityNames, Object postal,
            Thoroughfare thoroughfare, Premise premise,
            DependentLocality dependentLocality, PostalCode postalCode,
            String type, String usageType, String indicator) throws XalException {
        return new DefaultLocality(addressLines, localityNames,
                postal, thoroughfare, premise, dependentLocality,
                postalCode, type, usageType, indicator);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumber createPostBoxNumber(GrPostal grPostal, String content) {
        return new DefaultPostBoxNumber(grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumberPrefix createPostBoxNumberPrefix(String numberPrefixSeparator, GrPostal grPostal, String content) {
        return new DefaultPostBoxNumberPrefix(numberPrefixSeparator, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumberSuffix createPostBoxNumberSuffix(String numberSuffixSeparator, GrPostal grPostal, String content) {
        return new DefaultPostBoxNumberSuffix(numberSuffixSeparator, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumberExtension createPostBoxNumberExtension(String numberExtensionSeparator, String content) {
        return new DefaultPostBoxNumberExtension(numberExtensionSeparator, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Firm createFirm(List<GenericTypedGrPostal> addressLines, List<GenericTypedGrPostal> firmNames, List<Department> departments, MailStop mailStop, PostalCode postalCode, String type) {
        return new DefaultFirm(addressLines, firmNames, departments, mailStop, postalCode, type);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBox createPostBox(List<GenericTypedGrPostal> addressLines,
            PostBoxNumber postBoxNumber, PostBoxNumberPrefix postBoxNumberPrefix,
            PostBoxNumberSuffix postBoxNumberSuffix, PostBoxNumberExtension postBoxNumberExtension,
            Firm firm, PostalCode postalCode, String type, String indicator) {
        return new DefaultPostBox(addressLines, postBoxNumber,
                postBoxNumberPrefix, postBoxNumberSuffix, postBoxNumberExtension,
                firm, postalCode, type, indicator);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Department createDepartment(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> departmentNames, MailStop mailStop, PostalCode postalCode, String type) {
        return new DefaultDepartment(addressLines, departmentNames, mailStop, postalCode, type);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public MailStop createMailStop(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> mailStopNames, MailStopNumber mailStopNumber, String type) {
        return new DefaultMailStop(addressLines, mailStopNames, mailStopNumber, type);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public MailStopNumber createMailStopNumber(String nameNumberSeparator, GrPostal grPostal, String content) {
        return new DefaultMailStopNumber(nameNumberSeparator, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalCode createPostalCode(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> postalCodeNumbers, List<PostalCodeNumberExtension> postalCodeNumberExtensions,
            PostTown postTown, String type) {
        return new DefaultPostalCode(addressLines, postalCodeNumbers, postalCodeNumberExtensions, postTown, type);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalCodeNumberExtension createPostalCodeNumberExtension(String type, String numberExtensionSeparator,
            GrPostal grPostal, String content) {
        return new DefaultPostalCodeNumberExtension(type, numberExtensionSeparator, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostTownSuffix createPostTownSuffix(GrPostal grPostal, String content) {
        return new DefaultPostTownSuffix(grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostTown createPostTown(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> postTownNames, PostTownSuffix postTownSuffix, String type) {
        return new DefaultPostTown(addressLines, postTownNames, postTownSuffix, type);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LargeMailUserIdentifier createLargeMailUserIdentifier(String type, String indicator, GrPostal grPostal, String content) {
        return new DefaultLargeMailUserIdentifier(type, indicator, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LargeMailUserName createLargeMailUserName(String type, String code, String content) {
        return new DefaultLargeMailUserName(type, code, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LargeMailUser createLargeMailUser(List<GenericTypedGrPostal> addressLines,
            List<LargeMailUserName> largeMailUserNames, LargeMailUserIdentifier largeMailUserIdentifier,
            List<BuildingName> buildingNames, Department department, PostBox postBox,
            Thoroughfare thoroughfare, PostalCode postalCode, String type) {
        return new DefaultLargeMailUser(addressLines, largeMailUserNames,
                largeMailUserIdentifier, buildingNames, department,
                postBox, thoroughfare, postalCode, type);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public BuildingName createBuildingName(String type, AfterBeforeEnum typeOccurrence,
            GrPostal grPostal, String content) {
        return new DefaultBuildingName(type, typeOccurrence, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalRouteNumber createPostalRouteNumber(GrPostal grPostal, String content) {
        return new DefaultPostalRouteNumber(grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalRoute createPostalRoute(List<GenericTypedGrPostal> addressLines,
            Object localisation,
            PostBox postBox, String type) throws XalException {
        return new DefaultPostalRoute(addressLines, localisation, postBox, type);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostOffice createPostOffice(List<GenericTypedGrPostal> addressLines,
            Object localisation, PostalRoute postalRoute, PostBox postBox,
            PostalCode postalCode, String type, String indicator) throws XalException {
        return new DefaultPostOffice(addressLines, localisation, postalRoute,
                postBox, postalCode, type, indicator);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostOfficeNumber createPostOfficeNumber(String indicator,
            AfterBeforeEnum indicatorOccurence, GrPostal grPostal, String content) {
        return new DefaultPostOfficeNumber(indicator, indicatorOccurence, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public DependentLocalityNumber createDependentLocalityNumber(
            AfterBeforeEnum nameNumberOccurence, GrPostal grPostal, String content) {
        return new DefaultDependentLocalityNumber(nameNumberOccurence, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public DependentLocality createDependentLocality(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> dependentLocalityNames,
            DependentLocalityNumber dependentLocalityNumber,
            Object localisation, Thoroughfare thoroughfare, Premise premise,
            DependentLocality dependentLocality, PostalCode postalCode, String type,
            String usageType, String connector, String indicator) throws XalException {
        return new DefaultDependentLocality(addressLines, dependentLocalityNames,
                dependentLocalityNumber, localisation, thoroughfare, premise,
                dependentLocality, postalCode, type, usageType, connector, indicator);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Premise createPremise(List<GenericTypedGrPostal> addressLines, List<PremiseName> premiseNames,
            Object location,
            List<PremiseNumberPrefix> premiseNumberPrefixes,
            List<PremiseNumberSuffix> premiseNumberSuffixes,
            List<BuildingName> buildingNames,
            Object sub,
            MailStop mailStop, PostalCode postalCode, Premise premise,
            String type, String premiseDependency, String premiseDependencyType,
            String premiseThoroughfareConnector) throws XalException{
        return new DefaultPremise(addressLines, premiseNames, location,
                premiseNumberPrefixes, premiseNumberSuffixes, buildingNames,
                sub, mailStop, postalCode, premise,
                type, premiseDependency, premiseDependencyType, premiseThoroughfareConnector);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseName createPremiseName(String type, AfterBeforeEnum typeOccurrence, GrPostal grPostal, String content) {
        return new DefaultPremiseName(type, typeOccurrence, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseLocation createPremiseLocation(GrPostal grPostal, String content){
        return new DefaultPremiseLocation(grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SubPremiseName createSubPremiseName(String type, AfterBeforeEnum typeOccurrence, GrPostal grPostal, String content) {
        return new DefaultSubPremiseName(type, typeOccurrence, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SubPremiseLocation createSubPremiseLocation(GrPostal grPostal, String content){
        return new DefaultSubPremiseLocation(grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseNumber createPremiseNumber(SingleRangeEnum numberType, String type, String indicator,
            AfterBeforeEnum indicatorOccurrence, AfterBeforeEnum numberTypeOccurrence,
            GrPostal grPostal, String content){
        return new DefaultPremiseNumber(numberType, type, indicator, indicatorOccurrence, numberTypeOccurrence, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseNumberRange createPremiseNumberRange(PremiseNumberRangeFrom premiseNumberRangeFrom,
            PremiseNumberRangeTo premiseNumberRangeTo, String rangeType,
            String indicator, String separator, String type,
            AfterBeforeEnum indicatorOccurrence, AfterBeforeTypeNameEnum numberRangeOccurrence){
        return new DefaultPremiseNumberRange(premiseNumberRangeFrom, premiseNumberRangeTo,
                rangeType, indicator, separator, type, indicatorOccurrence, numberRangeOccurrence);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseNumberRangeFrom createPremiseNumberRangeFrom(List<GenericTypedGrPostal> addressLines,
            List<PremiseNumberPrefix> premiseNumberPrefixes,
            List<PremiseNumber> premiseNumbers,
            List<PremiseNumberSuffix> premiseNumberSuffixes) {
        return new DefaultPremiseNumberRangeFrom(addressLines, premiseNumberPrefixes, premiseNumbers, premiseNumberSuffixes);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseNumberRangeTo createPremiseNumberRangeTo(List<GenericTypedGrPostal> addressLines,
            List<PremiseNumberPrefix> premiseNumberPrefixes,
            List<PremiseNumber> premiseNumbers,
            List<PremiseNumberSuffix> premiseNumberSuffixes) {
        return new DefaultPremiseNumberRangeTo(addressLines, premiseNumberPrefixes, premiseNumbers, premiseNumberSuffixes);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseNumberPrefix createPremiseNumberPrefix(String numberPrefixSeparator,
            String type, GrPostal grPostal, String content) {
        return new DefaultPremiseNumberPrefix(numberPrefixSeparator, type, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseNumberSuffix createPremiseNumberSuffix(String numberSuffixSeparator,
            String type, GrPostal grPostal, String content) {
        return new DefaultPremiseNumberSuffix(numberSuffixSeparator, type, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SubPremiseNumberPrefix createSubPremiseNumberPrefix(String numberPrefixSeparator,
            String type, GrPostal grPostal, String content) {
        return new DefaultSubPremiseNumberPrefix(numberPrefixSeparator, type, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SubPremiseNumberSuffix createSubPremiseNumberSuffix(String numberSuffixSeparator,
            String type, GrPostal grPostal, String content) {
        return new DefaultSubPremiseNumberSuffix(numberSuffixSeparator, type, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SubPremiseNumber createSubPremiseNumber(String indicator,
            AfterBeforeEnum indicatorOccurrence, AfterBeforeEnum numberTypeOccurrence,
            String premiseNumberSeparator, String type, GrPostal grPostal, String content) {
        return new DefaultSubPremiseNumber(indicator, indicatorOccurrence,
                numberTypeOccurrence, premiseNumberSeparator, type, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SubPremise createSubPremise(List<GenericTypedGrPostal> addressLines,
            List<SubPremiseName> subPremiseNames, Object location,
            List<SubPremiseNumberPrefix> subPremiseNumberPrefixes,
            List<SubPremiseNumberSuffix> subPremiseNumberSuffixes,
            List<BuildingName> buildingNames, Firm firm, MailStop mailStop,
            PostalCode postalCode, SubPremise subPremise, String type) throws XalException {
        return new DefaultSubPremise(addressLines, subPremiseNames, location,
                subPremiseNumberPrefixes, subPremiseNumberSuffixes,
                buildingNames, firm, mailStop, postalCode, subPremise, type);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Thoroughfare createThoroughfare(List<GenericTypedGrPostal> addressLines,
            List<Object> thoroughfareNumbers, List<ThoroughfareNumberPrefix> thoroughfareNumberPrefixes,
            List<ThoroughfareNumberSuffix> thoroughfareNumberSuffixes,
            GenericTypedGrPostal thoroughfarePreDirection, GenericTypedGrPostal thoroughfareLeadingType,
            List<GenericTypedGrPostal> thoroughfareNames, GenericTypedGrPostal thoroughfareTrailingType,
            GenericTypedGrPostal thoroughfarPostDirection, DependentThoroughfare dependentThoroughfare,
            Object location, String type, DependentThoroughfares dependentThoroughfares,
            String dependentThoroughfaresIndicator, String dependentThoroughfaresConnector,
            String dependentThoroughfaresType) throws XalException {
        return new DefaultThoroughfare(addressLines, thoroughfareNumbers,
                thoroughfareNumberPrefixes, thoroughfareNumberSuffixes,
                thoroughfarePreDirection, thoroughfareLeadingType,
                thoroughfareNames, thoroughfareTrailingType,
                thoroughfarPostDirection, dependentThoroughfare,
                location, type, dependentThoroughfares, dependentThoroughfaresIndicator,
                dependentThoroughfaresConnector, dependentThoroughfaresType);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ThoroughfareNumberRange createThoroughfareNumberRange(List<GenericTypedGrPostal> addressLines,
            ThoroughfareNumberFrom thoroughfareNumberFrom, ThoroughfareNumberTo thoroughfareNumberTo,
            OddEvenEnum rangeType, String indicator, String separator, String type,
            AfterBeforeEnum indicatorOccurrence, AfterBeforeTypeNameEnum numberRangeOccurrence) {
        return new DefaultThoroughfareNumberRange(addressLines, thoroughfareNumberFrom,
                thoroughfareNumberTo, rangeType, indicator, separator, type,
                indicatorOccurrence, numberRangeOccurrence);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ThoroughfareNumber createThoroughfareNumber(SingleRangeEnum numberType,
            String type, String indicator, AfterBeforeEnum indicatorOccurence,
            AfterBeforeTypeNameEnum numberOccurrence, GrPostal grPostal, String content) {
        return new DefaultThoroughfareNumber(numberType, type, indicator,
                indicatorOccurence, numberOccurrence, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ThoroughfareNumberFrom createThoroughfareNumberFrom(List<Object> content, GrPostal grPostal) throws XalException {
        return new DefaultThoroughfareNumberFrom(content, grPostal);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ThoroughfareNumberTo createThoroughfareNumberTo(List<Object> content, GrPostal grPostal) throws XalException {
        return new DefaultThoroughfareNumberTo(content, grPostal);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ThoroughfareNumberSuffix createThoroughfareNumberSuffix(
            String numberSuffixSeparator, String type, GrPostal grPostal, String content) {
        return new DefaultThoroughfareNumberSuffix(numberSuffixSeparator, type, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ThoroughfareNumberPrefix createThoroughfareNumberPrefix(
            String numberPrefixSeparator, String type, GrPostal grPostal, String content) {
        return new DefaultThoroughfareNumberPrefix(numberPrefixSeparator, type, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public DependentThoroughfare createDependentThoroughfare(List<GenericTypedGrPostal> addressLines,
            GenericTypedGrPostal thoroughfarePreDirection, GenericTypedGrPostal thoroughfareLeadingType,
            List<GenericTypedGrPostal> thoroughfareNames, GenericTypedGrPostal thoroughfareTrailingType,
            GenericTypedGrPostal thoroughfarePostDirection, String type) {
        return new DefaultDependentThoroughfare(addressLines, thoroughfarePreDirection,
                thoroughfareLeadingType, thoroughfareNames, thoroughfareTrailingType,
                thoroughfarePostDirection, type);
    }
}

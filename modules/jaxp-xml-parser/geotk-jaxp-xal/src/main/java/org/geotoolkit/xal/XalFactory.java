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
package org.geotoolkit.xal;

import java.util.List;
import org.geotoolkit.xal.model.AddressDetails;
import org.geotoolkit.xal.model.AddressIdentifier;
import org.geotoolkit.xal.model.AddressLines;
import org.geotoolkit.xal.model.AdministrativeArea;
import org.geotoolkit.xal.model.AfterBeforeEnum;
import org.geotoolkit.xal.model.AfterBeforeTypeNameEnum;
import org.geotoolkit.xal.model.BuildingName;
import org.geotoolkit.xal.model.Country;
import org.geotoolkit.xal.model.CountryNameCode;
import org.geotoolkit.xal.model.Department;
import org.geotoolkit.xal.model.DependentLocality;
import org.geotoolkit.xal.model.DependentLocalityNumber;
import org.geotoolkit.xal.model.DependentThoroughfare;
import org.geotoolkit.xal.model.DependentThoroughfares;
import org.geotoolkit.xal.model.Firm;
import org.geotoolkit.xal.model.GenericTypedGrPostal;
import org.geotoolkit.xal.model.GrPostal;
import org.geotoolkit.xal.model.LargeMailUser;
import org.geotoolkit.xal.model.LargeMailUserIdentifier;
import org.geotoolkit.xal.model.LargeMailUserName;
import org.geotoolkit.xal.model.Locality;
import org.geotoolkit.xal.model.MailStop;
import org.geotoolkit.xal.model.MailStopNumber;
import org.geotoolkit.xal.model.OddEvenEnum;
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
import org.geotoolkit.xal.model.SingleRangeEnum;
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

/**
 *
 * @author Samuel Andrés
 */
public interface XalFactory {

    /**
     *
     * @param addressDetails
     * @param version
     * @return
     */
    Xal createXal(List<AddressDetails> addressDetails, String version);

    /**
     *
     * @param postalServiceElements
     * @param localisation
     * @param addressType
     * @param currentStatus
     * @param validFromDate
     * @param validToDate
     * @param usage
     * @param grPostal
     * @param AddressDetailsKey
     * @return
     * @throws XalException
     */
    AddressDetails createAddressDetails(PostalServiceElements postalServiceElements, Object localisation,
            String addressType, String currentStatus, String validFromDate, String validToDate,
            String usage, GrPostal grPostal, String AddressDetailsKey) throws XalException;

    /**
     * 
     * @return
     */
    AddressDetails createAddressDetails();

    /**
     *
     * @param addressLines
     * @return
     */
    AddressLines createAddressLines(List<GenericTypedGrPostal> addressLines);

    /**
     * 
     * @return
     */
    AddressLines createAddressLines();

    /**
     * 
     * @param type
     * @param grPostal
     * @param Content
     * @return
     */
    GenericTypedGrPostal createGenericTypedGrPostal(String type, GrPostal grPostal, String Content);

    /**
     * 
     * @return
     */
    GenericTypedGrPostal createGenericTypedGrPostal();

    /**
     * 
     * @param code
     * @return
     */
    GrPostal createGrPostal(String code);

    /**
     * 
     * @param addressIdentifiers
     * @param endorsementLineCode
     * @param keyLineCode
     * @param barCode
     * @param sortingCode
     * @param addressLatitude
     * @param addressLatitudeDirection
     * @param addressLongitude
     * @param addressLongitudeDirection
     * @param supplementaryPostalServiceData
     * @param type
     * @return
     */
    PostalServiceElements createPostalServiceElements(List<AddressIdentifier> addressIdentifiers, GenericTypedGrPostal endorsementLineCode,
            GenericTypedGrPostal keyLineCode, GenericTypedGrPostal barCode, SortingCode sortingCode, GenericTypedGrPostal addressLatitude,
            GenericTypedGrPostal addressLatitudeDirection, GenericTypedGrPostal addressLongitude, GenericTypedGrPostal addressLongitudeDirection,
            List<GenericTypedGrPostal> supplementaryPostalServiceData, String type);

    /**
     * 
     * @return
     */
    PostalServiceElements createPostalServiceElements();

    /**
     * 
     * @param type
     * @param grPostal
     * @return
     */
    SortingCode createSortingCode(String type, GrPostal grPostal);

    /**
     *
     * @param content
     * @param identifierType
     * @param type
     * @param grPostal
     * @return
     */
    AddressIdentifier createAddressIdentifier(String content, String identifierType, String type, GrPostal grPostal);

    /**
     * 
     * @return
     */
    AddressIdentifier createAddressIdentifier();

    /**
     * 
     * @param addressLines
     * @param countryNameCodes
     * @param countryNames
     * @param localisation
     * @return
     * @throws XalException
     */
    Country createCountry(List<GenericTypedGrPostal> addressLines,
            List<CountryNameCode> countryNameCodes, List<GenericTypedGrPostal> countryNames, Object localisation) throws XalException;

    /**
     * 
     * @return
     */
    Country createCountry();

    /**
     * 
     * @param sheme
     * @param grPostal
     * @param content
     * @return
     */
    CountryNameCode createCountryNameCode(String sheme, GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    CountryNameCode createCountryNameCode();

    /**
     * 
     * @param addressLines
     * @param administrativeAreaNames
     * @param subAdministrativeArea
     * @param localisation
     * @param type
     * @param usageType
     * @param indicator
     * @return
     * @throws XalException
     */
    AdministrativeArea createAdministrativeArea(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> administrativeAreaNames, SubAdministrativeArea subAdministrativeArea,
            Object localisation, String type, String usageType, String indicator) throws XalException;

    /**
     * 
     * @return
     */
    AdministrativeArea createAdministrativeArea();

    /**
     * 
     * @param addressLines
     * @param subAdministrativeAreaNames
     * @param localisation
     * @param type
     * @param usageType
     * @param indicator
     * @return
     * @throws XalException
     */
    SubAdministrativeArea createSubAdministrativeArea(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> subAdministrativeAreaNames,
            Object localisation, String type, String usageType, String indicator) throws XalException;

    /**
     * 
     * @return
     */
    SubAdministrativeArea createSubAdministrativeArea();

    /**
     * 
     * @param addressLines
     * @param localityNames
     * @param postal
     * @param thoroughfare
     * @param premise
     * @param dependentLocality
     * @param postalCode
     * @param type
     * @param usageType
     * @param indicator
     * @return
     * @throws XalException
     */
    Locality createLocality(List<GenericTypedGrPostal> addressLines, List<GenericTypedGrPostal> localityNames,
            Object postal,
            Thoroughfare thoroughfare, Premise premise, DependentLocality dependentLocality, PostalCode postalCode,
            String type, String usageType, String indicator) throws XalException;

    /**
     * 
     * @return
     */
    Locality createLocality();

    /**
     * 
     * @param grPostal
     * @param content
     * @return
     */
    PostBoxNumber createPostBoxNumber(GrPostal grPostal, String content);

    /**
     * 
     * @param numberPrefixSeparator
     * @param grPostal
     * @param content
     * @return
     */
    PostBoxNumberPrefix createPostBoxNumberPrefix(
            String numberPrefixSeparator, GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    PostBoxNumberPrefix createPostBoxNumberPrefix();

    /**
     * 
     * @param numberSuffixSeparator
     * @param grPostal
     * @param content
     * @return
     */
    PostBoxNumberSuffix createPostBoxNumberSuffix(
            String numberSuffixSeparator, GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    PostBoxNumberSuffix createPostBoxNumberSuffix();

    /**
     * 
     * @param numberExtensionSeparator
     * @param content
     * @return
     */
    PostBoxNumberExtension createPostBoxNumberExtension(String numberExtensionSeparator, String content);

    /**
     * 
     * @param addressLines
     * @param firmNames
     * @param departments
     * @param mailStop
     * @param postalCode
     * @param type
     * @return
     */
    Firm createFirm(List<GenericTypedGrPostal> addressLines, List<GenericTypedGrPostal> firmNames,
            List<Department> departments, MailStop mailStop, PostalCode postalCode, String type);

    /**
     * 
     * @return
     */
    Firm createFirm();

    /**
     * 
     * @param addressLines
     * @param postBoxNumber
     * @param postBoxNumberPrefix
     * @param postBoxNumberSuffix
     * @param postBoxNumberExtension
     * @param firm
     * @param postalCode
     * @param type
     * @param indicator
     * @return
     */
    PostBox createPostBox(List<GenericTypedGrPostal> addressLines, PostBoxNumber postBoxNumber,
            PostBoxNumberPrefix postBoxNumberPrefix, PostBoxNumberSuffix postBoxNumberSuffix,
            PostBoxNumberExtension postBoxNumberExtension, Firm firm,
            PostalCode postalCode, String type, String indicator);

    /**
     * 
     * @return
     */
     PostBox createPostBox();

    /**
     * 
     * @param addressLines
     * @param departmentNames
     * @param mailStop
     * @param postalCode
     * @param type
     * @return
     */
    Department createDepartment(List<GenericTypedGrPostal> addressLines, List<GenericTypedGrPostal> departmentNames,
            MailStop mailStop, PostalCode postalCode, String type);

    /**
     * 
     * @return
     */
    Department createDepartment();

    /**
     *
     * @param addressLines
     * @param mailStopNames
     * @param mailStopNumber
     * @param type
     * @return
     */
    MailStop createMailStop(List<GenericTypedGrPostal> addressLines, List<GenericTypedGrPostal> mailStopNames,
            MailStopNumber mailStopNumber, String type);

    /**
     * 
     * @return
     */
    MailStop createMailStop();

    /**
     * 
     * @param nameNumberSeparator
     * @param grPostal
     * @param content
     * @return
     */
    MailStopNumber createMailStopNumber(String nameNumberSeparator, GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    MailStopNumber createMailStopNumber();

    /**
     * 
     * @param addressLines
     * @param postalCodeNumbers
     * @param postalCodeNumberExtensions
     * @param postTown
     * @param type
     * @return
     */
    PostalCode createPostalCode(List<GenericTypedGrPostal> addressLines, List<GenericTypedGrPostal> postalCodeNumbers,
            List<PostalCodeNumberExtension> postalCodeNumberExtensions, PostTown postTown, String type);

    /**
     * 
     * @return
     */
    PostalCode createPostalCode();

    /**
     * 
     * @param type
     * @param numberExtensionSeparator
     * @param grPostal
     * @param content
     * @return
     */
    PostalCodeNumberExtension createPostalCodeNumberExtension(String type, String numberExtensionSeparator,
            GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    PostalCodeNumberExtension createPostalCodeNumberExtension();

    /**
     * 
     * @param grPostal
     * @param content
     * @return
     */
    PostTownSuffix createPostTownSuffix(GrPostal grPostal, String content);

    /**
     * 
     * @param addressLines
     * @param postTownNames
     * @param postTownSuffix
     * @param type
     * @return
     */
    PostTown createPostTown(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> postTownNames, PostTownSuffix postTownSuffix, String type);

    /**
     * 
     * @return
     */
    PostTown createPostTown();

    /**
     *
     * @param type
     * @param indicator
     * @param grPostal
     * @param content
     * @return
     */
    LargeMailUserIdentifier createLargeMailUserIdentifier(String type,
            String indicator, GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    LargeMailUserIdentifier createLargeMailUserIdentifier();

    /**
     *
     * @param type
     * @param code
     * @param content
     * @return
     */
    LargeMailUserName createLargeMailUserName(String type, String code, String content);

    /**
     * 
     * @return
     */
    LargeMailUserName createLargeMailUserName();

    /**
     * 
     * @param addressLines
     * @param largeMailUserNames
     * @param largeMailUserIdentifier
     * @param buildingNames
     * @param department
     * @param postBox
     * @param thoroughfare
     * @param postalCode
     * @param type
     * @return
     */
    LargeMailUser createLargeMailUser(List<GenericTypedGrPostal> addressLines,
            List<LargeMailUserName> largeMailUserNames, LargeMailUserIdentifier largeMailUserIdentifier,
            List<BuildingName> buildingNames, Department department, PostBox postBox,
            Thoroughfare thoroughfare, PostalCode postalCode, String type);

    /**
     * 
     * @return
     */
    LargeMailUser createLargeMailUser();

    /**
     * 
     * @param type
     * @param typeOccurrence
     * @param grPostal
     * @param content
     * @return
     */
    BuildingName createBuildingName(String type, AfterBeforeEnum typeOccurrence,
            GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    BuildingName createBuildingName();

    /**
     *
     * @param grPostal
     * @param content
     * @return
     */
    PostalRouteNumber createPostalRouteNumber(GrPostal grPostal, String content);

    /**
     * 
     * @param addressLines
     * @param localisation
     * @param postBox
     * @param type
     * @return
     * @throws XalException
     */
    PostalRoute createPostalRoute(List<GenericTypedGrPostal> addressLines,
            Object localisation, PostBox postBox, String type)
            throws XalException;

    /**
     * 
     * @return
     */
    PostalRoute createPostalRoute();

    /**
     * 
     * @param addressLines
     * @param localisation
     * @param postalRoute
     * @param postBox
     * @param postalCode
     * @param type
     * @param indicator
     * @return
     * @throws XalException
     */
    PostOffice createPostOffice(List<GenericTypedGrPostal> addressLines, 
            Object localisation, PostalRoute postalRoute, PostBox postBox,
            PostalCode postalCode, String type, String indicator)
            throws XalException;

    /**
     * 
     * @return
     */
    PostOffice createPostOffice();

    /**
     * 
     * @param indicator
     * @param indicatorOccurence
     * @param grPostal
     * @param content
     * @return
     */
    PostOfficeNumber createPostOfficeNumber(String indicator,
            AfterBeforeEnum indicatorOccurence, GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    PostOfficeNumber createPostOfficeNumber();

    /**
     * 
     * @param nameNumberOccurence
     * @param grPostal
     * @param content
     * @return
     */
    DependentLocalityNumber createDependentLocalityNumber(
            AfterBeforeEnum nameNumberOccurence, GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    DependentLocalityNumber createDependentLocalityNumber();

    /**
     *
     * @param addressLines
     * @param dependentLocalityNames
     * @param dependentLocalityNumber
     * @param localisation
     * @param thoroughfare
     * @param premise
     * @param dependentLocality
     * @param postalCode
     * @param type
     * @param usageType
     * @param connector
     * @param indicator
     * @return
     * @throws XalException
     */
    DependentLocality createDependentLocality(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> dependentLocalityNames,
            DependentLocalityNumber dependentLocalityNumber,
            Object localisation, Thoroughfare thoroughfare, Premise premise,
            DependentLocality dependentLocality, PostalCode postalCode,
            String type, String usageType, String connector, String indicator) throws XalException;

    /**
     * 
     * @return
     */
    DependentLocality createDependentLocality();

    /**
     * 
     * @param addressLines
     * @param premiseNames
     * @param location
     * @param premiseNumberPrefixes
     * @param premiseNumberSuffixes
     * @param buildingNames
     * @param sub
     * @param mailStop
     * @param postalCode
     * @param premise
     * @param type
     * @param premiseDependency
     * @param premiseDependencyType
     * @param premiseThoroughfareConnector
     * @return
     * @throws XalException
     */
    Premise createPremise(List<GenericTypedGrPostal> addressLines, List<PremiseName> premiseNames,
            Object location,
            List<PremiseNumberPrefix> premiseNumberPrefixes,
            List<PremiseNumberSuffix> premiseNumberSuffixes,
            List<BuildingName> buildingNames,
            Object sub,
            MailStop mailStop, PostalCode postalCode, Premise premise,
            String type, String premiseDependency, String premiseDependencyType,
            String premiseThoroughfareConnector) throws XalException;

    /**
     * 
     * @return
     */
    Premise createPremise();

    /**
     * 
     * @param type
     * @param typeOccurrence
     * @param grPostal
     * @param content
     * @return
     */
    PremiseName createPremiseName(String type, AfterBeforeEnum typeOccurrence,
            GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    PremiseName createPremiseName();

    /**
     *
     * @param grPostal
     * @param content
     * @return
     */
    PremiseLocation createPremiseLocation(GrPostal grPostal, String content);
    /**
     *
     * @param type
     * @param typeOccurrence
     * @param grPostal
     * @param content
     * @return
     */
    SubPremiseName createSubPremiseName(String type, AfterBeforeEnum typeOccurrence,
            GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    SubPremiseName createSubPremiseName();

    /**
     *
     * @param grPostal
     * @param content
     * @return
     */
    SubPremiseLocation createSubPremiseLocation(GrPostal grPostal, String content);

    /**
     * 
     * @param numberType
     * @param type
     * @param indicator
     * @param indicatorOccurrence
     * @param numberTypeOccurrence
     * @param grPostal
     * @param content
     * @return
     */
    PremiseNumber createPremiseNumber(SingleRangeEnum numberType, String type, String indicator,
            AfterBeforeEnum indicatorOccurrence, AfterBeforeEnum numberTypeOccurrence,
            GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    PremiseNumber createPremiseNumber();

    /**
     *
     * @param premiseNumberRangeFrom
     * @param premiseNumberRangeTo
     * @param rangeType
     * @param indicator
     * @param separator
     * @param type
     * @param indicatorOccurrence
     * @param numberRangeOccurrence
     * @return
     */
    PremiseNumberRange createPremiseNumberRange(PremiseNumberRangeFrom premiseNumberRangeFrom,
            PremiseNumberRangeTo premiseNumberRangeTo, String rangeType,
            String indicator, String separator, String type,
            AfterBeforeEnum indicatorOccurrence, AfterBeforeTypeNameEnum numberRangeOccurrence);

    /**
     * 
     * @return
     */
    PremiseNumberRange createPremiseNumberRange();

    /**
     * 
     * @param addressLines
     * @param premiseNumberPrefixes
     * @param premiseNumbers
     * @param premiseNumberSuffixes
     * @return
     */
    PremiseNumberRangeFrom createPremiseNumberRangeFrom(List<GenericTypedGrPostal> addressLines,
            List<PremiseNumberPrefix> premiseNumberPrefixes,
            List<PremiseNumber> premiseNumbers,
            List<PremiseNumberSuffix> premiseNumberSuffixes);

    /**
     * 
     * @return
     */
    PremiseNumberRangeFrom createPremiseNumberRangeFrom();

    /**
     * 
     * @param addressLines
     * @param premiseNumberPrefixes
     * @param premiseNumbers
     * @param premiseNumberSuffixes
     * @return
     */
    PremiseNumberRangeTo createPremiseNumberRangeTo(List<GenericTypedGrPostal> addressLines,
            List<PremiseNumberPrefix> premiseNumberPrefixes,
            List<PremiseNumber> premiseNumbers,
            List<PremiseNumberSuffix> premiseNumberSuffixes);

    /**
     * 
     * @return
     */
    PremiseNumberRangeTo createPremiseNumberRangeTo();

    /**
     *
     * @param numberPrefixSeparator
     * @param type
     * @param grPostal
     * @param content
     * @return
     */
    PremiseNumberPrefix createPremiseNumberPrefix(String numberPrefixSeparator,
            String type, GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    PremiseNumberPrefix createPremiseNumberPrefix();

    /**
     * 
     * @param numberSuffixSeparator
     * @param type
     * @param grPostal
     * @param content
     * @return
     */
    PremiseNumberSuffix createPremiseNumberSuffix(String numberSuffixSeparator,
            String type, GrPostal grPostal, String content);

    /**
     *
     * @return
     */
    PremiseNumberSuffix createPremiseNumberSuffix();

    /**
     *
     * @param numberPrefixSeparator
     * @param type
     * @param grPostal
     * @param content
     * @return
     */
    SubPremiseNumberPrefix createSubPremiseNumberPrefix(String numberPrefixSeparator,
            String type, GrPostal grPostal, String content);

    /**
     *
     * @return
     */
    SubPremiseNumberPrefix createSubPremiseNumberPrefix();

    /**
     *
     * @param numberSuffixSeparator
     * @param type
     * @param grPostal
     * @param content
     * @return
     */
    SubPremiseNumberSuffix createSubPremiseNumberSuffix(String numberSuffixSeparator,
            String type, GrPostal grPostal, String content);

    /**
     *
     * @return
     */
    SubPremiseNumberSuffix createSubPremiseNumberSuffix();

    /**
     * 
     * @param indicator
     * @param indicatorOccurrence
     * @param numberTypeOccurrence
     * @param premiseNumberSeparator
     * @param type
     * @param grPostal
     * @param content
     * @return
     */
    SubPremiseNumber createSubPremiseNumber(String indicator, AfterBeforeEnum indicatorOccurrence,
            AfterBeforeEnum numberTypeOccurrence, String premiseNumberSeparator,
            String type, GrPostal grPostal, String content);

    /**
     *
     * @return
     */
    SubPremiseNumber createSubPremiseNumber();

    /**
     * 
     * @param addressLines
     * @param subPremiseNames
     * @param location
     * @param subPremiseNumberPrefixes
     * @param subPremiseNumberSuffixes
     * @param buildingNames
     * @param firm
     * @param mailStop
     * @param postalCode
     * @param subPremise
     * @param type
     * @return
     * @throws XalException
     */
    SubPremise createSubPremise(List<GenericTypedGrPostal> addressLines,
            List<SubPremiseName> subPremiseNames, Object location,
            List<SubPremiseNumberPrefix> subPremiseNumberPrefixes,
            List<SubPremiseNumberSuffix> subPremiseNumberSuffixes,
            List<BuildingName> buildingNames, Firm firm, MailStop mailStop,
            PostalCode postalCode, SubPremise subPremise, String type) throws XalException;

    /**
     * 
     * @return
     */
    SubPremise createSubPremise();
    
    /**
     * 
     * @param addressLines
     * @param thoroughfareNumbers
     * @param thoroughfareNumberPrefixes
     * @param thoroughfareNumberSuffixes
     * @param thoroughfarePreDirection
     * @param thoroughfareLeadingType
     * @param thoroughfareNames
     * @param thoroughfareTrailingType
     * @param thoroughfarPostDirection
     * @param dependentThoroughfare
     * @param location
     * @param type
     * @param dependentThoroughfares
     * @param dependentThoroughfaresIndicator
     * @param dependentThoroughfaresConnector
     * @param dependentThoroughfaresType
     * @return
     * @throws XalException
     */
    Thoroughfare createThoroughfare(List<GenericTypedGrPostal> addressLines, List<Object> thoroughfareNumbers,
            List<ThoroughfareNumberPrefix> thoroughfareNumberPrefixes,
            List<ThoroughfareNumberSuffix> thoroughfareNumberSuffixes,
            GenericTypedGrPostal thoroughfarePreDirection,
            GenericTypedGrPostal thoroughfareLeadingType,
            List<GenericTypedGrPostal> thoroughfareNames,
            GenericTypedGrPostal thoroughfareTrailingType,
            GenericTypedGrPostal thoroughfarPostDirection,
            DependentThoroughfare dependentThoroughfare,
            Object location,
            String type, DependentThoroughfares dependentThoroughfares, String dependentThoroughfaresIndicator,
            String dependentThoroughfaresConnector, String dependentThoroughfaresType) throws XalException;

    /**
     * 
     * @return
     */
    Thoroughfare createThoroughfare();

    /**
     *
     * @param addressLines
     * @param thoroughfareNumberFrom
     * @param thoroughfareNumberTo
     * @param rangeType
     * @param indicator
     * @param separator
     * @param type
     * @param indicatorOccurrence
     * @param numberRangeOccurrence
     * @return
     */
    ThoroughfareNumberRange createThoroughfareNumberRange(List<GenericTypedGrPostal> addressLines,
            ThoroughfareNumberFrom thoroughfareNumberFrom,
            ThoroughfareNumberTo thoroughfareNumberTo,
            OddEvenEnum rangeType, String indicator, String separator, String type,
            AfterBeforeEnum indicatorOccurrence, AfterBeforeTypeNameEnum numberRangeOccurrence, 
            GrPostal grPostal);

    /**
     *
     * @return
     */
    ThoroughfareNumberRange createThoroughfareNumberRange();

    /**
     * 
     * @param numberType
     * @param type
     * @param indicator
     * @param indicatorOccurence
     * @param numberOccurrence
     * @param grPostal
     * @param content
     * @return
     */
    ThoroughfareNumber createThoroughfareNumber(SingleRangeEnum numberType,
            String type, String indicator, AfterBeforeEnum indicatorOccurence,
            AfterBeforeTypeNameEnum numberOccurrence, GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    ThoroughfareNumber createThoroughfareNumber();

    /**
     * 
     * @param content
     * @param grPostal
     * @return
     * @throws XalException
     */
    ThoroughfareNumberFrom createThoroughfareNumberFrom(
            List<Object> content, GrPostal grPostal) throws XalException;

    /**
     * 
     * @param content
     * @param grPostal
     * @return
     * @throws XalException
     */
    ThoroughfareNumberTo createThoroughfareNumberTo(
            List<Object> content, GrPostal grPostal) throws XalException;

    /**
     * 
     * @param numberSuffixSeparator
     * @param type
     * @param grPostal
     * @param content
     * @return
     */
    ThoroughfareNumberSuffix createThoroughfareNumberSuffix(String numberSuffixSeparator,
            String type, GrPostal grPostal, String content);

    /**
     *
     * @return
     */
    ThoroughfareNumberSuffix createThoroughfareNumberSuffix();

    /**
     * 
     * @param numberPrefixSeparator
     * @param type
     * @param grPostal
     * @param content
     * @return
     */
    ThoroughfareNumberPrefix createThoroughfareNumberPrefix(String numberPrefixSeparator,
        String type, GrPostal grPostal, String content);

    /**
     * 
     * @return
     */
    ThoroughfareNumberPrefix createThoroughfareNumberPrefix();

    /**
     * 
     * @param addressLines
     * @param thoroughfarePreDirection
     * @param thoroughfareLeadingType
     * @param thoroughfareNames
     * @param thoroughfareTrailingType
     * @param thoroughfarePostDirection
     * @param type
     * @return
     */
    DependentThoroughfare createDependentThoroughfare(List<GenericTypedGrPostal> addressLines,
            GenericTypedGrPostal thoroughfarePreDirection, GenericTypedGrPostal thoroughfareLeadingType,
            List<GenericTypedGrPostal> thoroughfareNames, GenericTypedGrPostal thoroughfareTrailingType,
            GenericTypedGrPostal thoroughfarePostDirection, String type);

    /**
     * 
     * @return
     */
    DependentThoroughfare createDependentThoroughfare();
}

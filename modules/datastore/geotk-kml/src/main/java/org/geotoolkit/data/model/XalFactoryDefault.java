package org.geotoolkit.data.model;

import java.util.List;
import org.geotoolkit.data.model.xal.DefaultAddressLines;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xal.DefaultAddressDetails;
import org.geotoolkit.data.model.xal.AddressIdentifier;
import org.geotoolkit.data.model.xal.DefaultAddressIdentifier;
import org.geotoolkit.data.model.xal.AddressLines;
import org.geotoolkit.data.model.xal.AdministrativeArea;
import org.geotoolkit.data.model.xal.DefaultAdministrativeArea;
import org.geotoolkit.data.model.xal.AfterBeforeEnum;
import org.geotoolkit.data.model.xal.AfterBeforeTypeNameEnum;
import org.geotoolkit.data.model.xal.BuildingName;
import org.geotoolkit.data.model.xal.DefaultBuildingName;
import org.geotoolkit.data.model.xal.Country;
import org.geotoolkit.data.model.xal.DefaultCountry;
import org.geotoolkit.data.model.xal.CountryNameCode;
import org.geotoolkit.data.model.xal.DefaultCountryNameCode;
import org.geotoolkit.data.model.xal.Department;
import org.geotoolkit.data.model.xal.DefaultDepartment;
import org.geotoolkit.data.model.xal.DependentLocality;
import org.geotoolkit.data.model.xal.DefaultDependentLocality;
import org.geotoolkit.data.model.xal.DependentLocalityNumber;
import org.geotoolkit.data.model.xal.DefaultDependentLocalityNumber;
import org.geotoolkit.data.model.xal.DependentThoroughfare;
import org.geotoolkit.data.model.xal.DefaultDependentThoroughfare;
import org.geotoolkit.data.model.xal.DependentThoroughfares;
import org.geotoolkit.data.model.xal.Firm;
import org.geotoolkit.data.model.xal.DefaultFirm;
import org.geotoolkit.data.model.xal.GenericTypedGrPostal;
import org.geotoolkit.data.model.xal.DefaultGenericTypedGrPostal;
import org.geotoolkit.data.model.xal.GrPostal;
import org.geotoolkit.data.model.xal.GrPostalDefault;
import org.geotoolkit.data.model.xal.LargeMailUser;
import org.geotoolkit.data.model.xal.LargeMailUserDefault;
import org.geotoolkit.data.model.xal.LargeMailUserIdentifier;
import org.geotoolkit.data.model.xal.LargeMailUserIdentifierDefault;
import org.geotoolkit.data.model.xal.LargeMailUserName;
import org.geotoolkit.data.model.xal.LargeMailUserNameDefault;
import org.geotoolkit.data.model.xal.Locality;
import org.geotoolkit.data.model.xal.LocalityDefault;
import org.geotoolkit.data.model.xal.MailStop;
import org.geotoolkit.data.model.xal.MailStopDefault;
import org.geotoolkit.data.model.xal.MailStopNumber;
import org.geotoolkit.data.model.xal.MailStopNumberDefault;
import org.geotoolkit.data.model.xal.OddEvenEnum;
import org.geotoolkit.data.model.xal.PostBox;
import org.geotoolkit.data.model.xal.PostBoxDefault;
import org.geotoolkit.data.model.xal.PostBoxNumber;
import org.geotoolkit.data.model.xal.PostBoxNumberDefault;
import org.geotoolkit.data.model.xal.PostBoxNumberExtension;
import org.geotoolkit.data.model.xal.PostBoxNumberExtensionDefault;
import org.geotoolkit.data.model.xal.PostBoxNumberPrefix;
import org.geotoolkit.data.model.xal.PostBoxNumberPrefixDefault;
import org.geotoolkit.data.model.xal.PostBoxNumberSuffix;
import org.geotoolkit.data.model.xal.PostBoxNumberSuffixDefault;
import org.geotoolkit.data.model.xal.PostOffice;
import org.geotoolkit.data.model.xal.PostOfficeDefault;
import org.geotoolkit.data.model.xal.PostOfficeNumber;
import org.geotoolkit.data.model.xal.PostOfficeNumberDefault;
import org.geotoolkit.data.model.xal.PostTown;
import org.geotoolkit.data.model.xal.PostTownDefault;
import org.geotoolkit.data.model.xal.PostTownSuffix;
import org.geotoolkit.data.model.xal.PostTownSuffixDefault;
import org.geotoolkit.data.model.xal.PostalCode;
import org.geotoolkit.data.model.xal.PostalCodeDefault;
import org.geotoolkit.data.model.xal.PostalCodeNumberExtension;
import org.geotoolkit.data.model.xal.PostalCodeNumberExtensionDefault;
import org.geotoolkit.data.model.xal.PostalRoute;
import org.geotoolkit.data.model.xal.PostalRouteDefault;
import org.geotoolkit.data.model.xal.PostalRouteNumber;
import org.geotoolkit.data.model.xal.PostalRouteNumberDefault;
import org.geotoolkit.data.model.xal.PostalServiceElements;
import org.geotoolkit.data.model.xal.PostalServiceElementsDefault;
import org.geotoolkit.data.model.xal.Premise;
import org.geotoolkit.data.model.xal.PremiseDefault;
import org.geotoolkit.data.model.xal.PremiseLocation;
import org.geotoolkit.data.model.xal.PremiseLocationDefault;
import org.geotoolkit.data.model.xal.PremiseName;
import org.geotoolkit.data.model.xal.PremiseNameDefault;
import org.geotoolkit.data.model.xal.PremiseNumber;
import org.geotoolkit.data.model.xal.PremiseNumberDefault;
import org.geotoolkit.data.model.xal.PremiseNumberPrefix;
import org.geotoolkit.data.model.xal.PremiseNumberPrefixDefault;
import org.geotoolkit.data.model.xal.PremiseNumberRange;
import org.geotoolkit.data.model.xal.PremiseNumberRangeDefault;
import org.geotoolkit.data.model.xal.PremiseNumberRangeFrom;
import org.geotoolkit.data.model.xal.PremiseNumberRangeFromDefault;
import org.geotoolkit.data.model.xal.PremiseNumberRangeTo;
import org.geotoolkit.data.model.xal.PremiseNumberRangeToDefault;
import org.geotoolkit.data.model.xal.PremiseNumberSuffix;
import org.geotoolkit.data.model.xal.PremiseNumberSuffixDefault;
import org.geotoolkit.data.model.xal.SingleRangeEnum;
import org.geotoolkit.data.model.xal.SortingCode;
import org.geotoolkit.data.model.xal.SortingCodeDefault;
import org.geotoolkit.data.model.xal.SubAdministrativeArea;
import org.geotoolkit.data.model.xal.SubAdministrativeAreaDefault;
import org.geotoolkit.data.model.xal.SubPremise;
import org.geotoolkit.data.model.xal.SubPremiseDefault;
import org.geotoolkit.data.model.xal.SubPremiseLocation;
import org.geotoolkit.data.model.xal.SubPremiseLocationDefault;
import org.geotoolkit.data.model.xal.SubPremiseName;
import org.geotoolkit.data.model.xal.SubPremiseNameDefault;
import org.geotoolkit.data.model.xal.SubPremiseNumber;
import org.geotoolkit.data.model.xal.SubPremiseNumberDefault;
import org.geotoolkit.data.model.xal.SubPremiseNumberPrefix;
import org.geotoolkit.data.model.xal.SubPremiseNumberPrefixDefault;
import org.geotoolkit.data.model.xal.SubPremiseNumberSuffix;
import org.geotoolkit.data.model.xal.SubPremiseNumberSuffixDefault;
import org.geotoolkit.data.model.xal.Thoroughfare;
import org.geotoolkit.data.model.xal.ThoroughfareDefault;
import org.geotoolkit.data.model.xal.ThoroughfareNumber;
import org.geotoolkit.data.model.xal.ThoroughfareNumberDefault;
import org.geotoolkit.data.model.xal.ThoroughfareNumberFrom;
import org.geotoolkit.data.model.xal.ThoroughfareNumberFromDefault;
import org.geotoolkit.data.model.xal.ThoroughfareNumberPrefix;
import org.geotoolkit.data.model.xal.ThoroughfareNumberPrefixDefault;
import org.geotoolkit.data.model.xal.ThoroughfareNumberRange;
import org.geotoolkit.data.model.xal.ThoroughfareNumberRangeDefault;
import org.geotoolkit.data.model.xal.ThoroughfareNumberSuffix;
import org.geotoolkit.data.model.xal.ThoroughfareNumberSuffixDefault;
import org.geotoolkit.data.model.xal.ThoroughfareNumberTo;
import org.geotoolkit.data.model.xal.ThoroughfareNumberToDefault;
import org.geotoolkit.data.model.xal.Xal;
import org.geotoolkit.data.model.xal.XalDefault;
import org.geotoolkit.data.model.xal.XalException;

/**
 *
 * @author Samuel Andrés
 */
public class XalFactoryDefault implements XalFactory {

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Xal createXal(List<AddressDetails> addressDetails, String version) {
        return new XalDefault(addressDetails, version);
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
        return new GrPostalDefault(code);
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
        return new PostalServiceElementsDefault(addressIdentifiers,endorsementLineCode,
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
        return new SortingCodeDefault(type, grPostal);
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
        return new SubAdministrativeAreaDefault(addressLines, subAdministrativeAreaNames, localisation, type, usageType, indicator);
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
        return new LocalityDefault(addressLines, localityNames,
                postal, thoroughfare, premise, dependentLocality,
                postalCode, type, usageType, indicator);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumber createPostBoxNumber(GrPostal grPostal, String content) {
        return new PostBoxNumberDefault(grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumberPrefix createPostBoxNumberPrefix(String numberPrefixSeparator, GrPostal grPostal, String content) {
        return new PostBoxNumberPrefixDefault(numberPrefixSeparator, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumberSuffix createPostBoxNumberSuffix(String numberSuffixSeparator, GrPostal grPostal, String content) {
        return new PostBoxNumberSuffixDefault(numberSuffixSeparator, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumberExtension createPostBoxNumberExtension(String numberExtensionSeparator, String content) {
        return new PostBoxNumberExtensionDefault(numberExtensionSeparator, content);
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
        return new PostBoxDefault(addressLines, postBoxNumber,
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
        return new MailStopDefault(addressLines, mailStopNames, mailStopNumber, type);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public MailStopNumber createMailStopNumber(String nameNumberSeparator, GrPostal grPostal, String content) {
        return new MailStopNumberDefault(nameNumberSeparator, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalCode createPostalCode(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> postalCodeNumbers, List<PostalCodeNumberExtension> postalCodeNumberExtensions,
            PostTown postTown, String type) {
        return new PostalCodeDefault(addressLines, postalCodeNumbers, postalCodeNumberExtensions, postTown, type);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalCodeNumberExtension createPostalCodeNumberExtension(String type, String numberExtensionSeparator,
            GrPostal grPostal, String content) {
        return new PostalCodeNumberExtensionDefault(type, numberExtensionSeparator, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostTownSuffix createPostTownSuffix(GrPostal grPostal, String content) {
        return new PostTownSuffixDefault(grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostTown createPostTown(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> postTownNames, PostTownSuffix postTownSuffix, String type) {
        return new PostTownDefault(addressLines, postTownNames, postTownSuffix, type);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LargeMailUserIdentifier createLargeMailUserIdentifier(String type, String indicator, GrPostal grPostal, String content) {
        return new LargeMailUserIdentifierDefault(type, indicator, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LargeMailUserName createLargeMailUserName(String type, String code, String content) {
        return new LargeMailUserNameDefault(type, code, content);
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
        return new LargeMailUserDefault(addressLines, largeMailUserNames,
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
        return new PostalRouteNumberDefault(grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalRoute createPostalRoute(List<GenericTypedGrPostal> addressLines,
            Object localisation,
            PostBox postBox, String type) throws XalException {
        return new PostalRouteDefault(addressLines, localisation, postBox, type);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostOffice createPostOffice(List<GenericTypedGrPostal> addressLines,
            Object localisation, PostalRoute postalRoute, PostBox postBox,
            PostalCode postalCode, String type, String indicator) throws XalException {
        return new PostOfficeDefault(addressLines, localisation, postalRoute,
                postBox, postalCode, type, indicator);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostOfficeNumber createPostOfficeNumber(String indicator,
            AfterBeforeEnum indicatorOccurence, GrPostal grPostal, String content) {
        return new PostOfficeNumberDefault(indicator, indicatorOccurence, grPostal, content);
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
        return new PremiseDefault(addressLines, premiseNames, location,
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
        return new PremiseNameDefault(type, typeOccurrence, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseLocation createPremiseLocation(GrPostal grPostal, String content){
        return new PremiseLocationDefault(grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SubPremiseName createSubPremiseName(String type, AfterBeforeEnum typeOccurrence, GrPostal grPostal, String content) {
        return new SubPremiseNameDefault(type, typeOccurrence, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SubPremiseLocation createSubPremiseLocation(GrPostal grPostal, String content){
        return new SubPremiseLocationDefault(grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseNumber createPremiseNumber(SingleRangeEnum numberType, String type, String indicator,
            AfterBeforeEnum indicatorOccurrence, AfterBeforeEnum numberTypeOccurrence,
            GrPostal grPostal, String content){
        return new PremiseNumberDefault(numberType, type, indicator, indicatorOccurrence, numberTypeOccurrence, grPostal, content);
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
        return new PremiseNumberRangeDefault(premiseNumberRangeFrom, premiseNumberRangeTo,
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
        return new PremiseNumberRangeFromDefault(addressLines, premiseNumberPrefixes, premiseNumbers, premiseNumberSuffixes);
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
        return new PremiseNumberRangeToDefault(addressLines, premiseNumberPrefixes, premiseNumbers, premiseNumberSuffixes);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseNumberPrefix createPremiseNumberPrefix(String numberPrefixSeparator,
            String type, GrPostal grPostal, String content) {
        return new PremiseNumberPrefixDefault(numberPrefixSeparator, type, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseNumberSuffix createPremiseNumberSuffix(String numberSuffixSeparator,
            String type, GrPostal grPostal, String content) {
        return new PremiseNumberSuffixDefault(numberSuffixSeparator, type, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SubPremiseNumberPrefix createSubPremiseNumberPrefix(String numberPrefixSeparator,
            String type, GrPostal grPostal, String content) {
        return new SubPremiseNumberPrefixDefault(numberPrefixSeparator, type, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SubPremiseNumberSuffix createSubPremiseNumberSuffix(String numberSuffixSeparator,
            String type, GrPostal grPostal, String content) {
        return new SubPremiseNumberSuffixDefault(numberSuffixSeparator, type, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SubPremiseNumber createSubPremiseNumber(String indicator,
            AfterBeforeEnum indicatorOccurrence, AfterBeforeEnum numberTypeOccurrence,
            String premiseNumberSeparator, String type, GrPostal grPostal, String content) {
        return new SubPremiseNumberDefault(indicator, indicatorOccurrence,
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
        return new SubPremiseDefault(addressLines, subPremiseNames, location,
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
        return new ThoroughfareDefault(addressLines, thoroughfareNumbers,
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
        return new ThoroughfareNumberRangeDefault(addressLines, thoroughfareNumberFrom,
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
        return new ThoroughfareNumberDefault(numberType, type, indicator,
                indicatorOccurence, numberOccurrence, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ThoroughfareNumberFrom createThoroughfareNumberFrom(List<Object> content, GrPostal grPostal) throws XalException {
        return new ThoroughfareNumberFromDefault(content, grPostal);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ThoroughfareNumberTo createThoroughfareNumberTo(List<Object> content, GrPostal grPostal) throws XalException {
        return new ThoroughfareNumberToDefault(content, grPostal);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ThoroughfareNumberSuffix createThoroughfareNumberSuffix(
            String numberSuffixSeparator, String type, GrPostal grPostal, String content) {
        return new ThoroughfareNumberSuffixDefault(numberSuffixSeparator, type, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ThoroughfareNumberPrefix createThoroughfareNumberPrefix(
            String numberPrefixSeparator, String type, GrPostal grPostal, String content) {
        return new ThoroughfareNumberPrefixDefault(numberPrefixSeparator, type, grPostal, content);
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

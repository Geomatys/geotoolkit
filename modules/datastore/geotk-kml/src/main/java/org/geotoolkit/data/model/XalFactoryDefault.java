package org.geotoolkit.data.model;

import java.util.List;
import org.geotoolkit.data.model.xal.AddressLinesDefault;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xal.AddressDetailsDefault;
import org.geotoolkit.data.model.xal.AddressIdentifier;
import org.geotoolkit.data.model.xal.AddressIdentifierDefault;
import org.geotoolkit.data.model.xal.AddressLines;
import org.geotoolkit.data.model.xal.AdministrativeArea;
import org.geotoolkit.data.model.xal.AdministrativeAreaDefault;
import org.geotoolkit.data.model.xal.AfterBeforeEnum;
import org.geotoolkit.data.model.xal.BuildingName;
import org.geotoolkit.data.model.xal.BuildingNameDefault;
import org.geotoolkit.data.model.xal.Country;
import org.geotoolkit.data.model.xal.CountryDefault;
import org.geotoolkit.data.model.xal.CountryNameCode;
import org.geotoolkit.data.model.xal.CountryNameCodeDefault;
import org.geotoolkit.data.model.xal.Department;
import org.geotoolkit.data.model.xal.DepartmentDefault;
import org.geotoolkit.data.model.xal.DependentLocality;
import org.geotoolkit.data.model.xal.Firm;
import org.geotoolkit.data.model.xal.FirmDefault;
import org.geotoolkit.data.model.xal.GenericTypedGrPostal;
import org.geotoolkit.data.model.xal.GenericTypedGrPostalDefault;
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
import org.geotoolkit.data.model.xal.SortingCode;
import org.geotoolkit.data.model.xal.SortingCodeDefault;
import org.geotoolkit.data.model.xal.SubAdministrativeArea;
import org.geotoolkit.data.model.xal.SubAdministrativeAreaDefault;
import org.geotoolkit.data.model.xal.Thoroughfare;
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
        return new AddressDetailsDefault(postalServiceElements, localisation,
                addressType, currentStatus, validFromDate, validToDate, usage, grPostal, AddressDetailsKey);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AddressLines createAddressLines(List<GenericTypedGrPostal> addressLines) {
        return new AddressLinesDefault(addressLines);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal createGenericTypedGrPostal(String type, GrPostal grPostal, String Content) {
        return new GenericTypedGrPostalDefault(type, grPostal, Content);
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
        return new AddressIdentifierDefault(content, identifierType, type, grPostal);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Country createCountry(List<GenericTypedGrPostal> addressLines, List<CountryNameCode> countryNameCodes,
            List<GenericTypedGrPostal> countryNames, Object localisation) throws XalException {
        return new CountryDefault(addressLines, countryNameCodes, countryNames, localisation);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public CountryNameCode createCountryNameCode(String sheme, GrPostal grPostal, String content) {
        return new CountryNameCodeDefault(sheme, grPostal, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AdministrativeArea createAdministrativeArea(List<GenericTypedGrPostal> addressLines, 
            List<GenericTypedGrPostal> administrativeAreaNames, SubAdministrativeArea subAdministrativeArea,
            Object localisation, String type, String usageType, String indicator) throws XalException {
        return new AdministrativeAreaDefault(addressLines, administrativeAreaNames,
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
        return new FirmDefault(addressLines, firmNames, departments, mailStop, postalCode, type);
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
        return new DepartmentDefault(addressLines, departmentNames, mailStop, postalCode, type);
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
        return new BuildingNameDefault(type, typeOccurrence, grPostal, content);
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

}

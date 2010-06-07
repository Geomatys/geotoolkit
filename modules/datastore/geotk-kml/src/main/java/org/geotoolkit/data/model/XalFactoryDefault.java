package org.geotoolkit.data.model;

import java.util.List;
import java.util.List;
import org.geotoolkit.data.model.xal.AddressIdentifier;
import org.geotoolkit.data.model.xal.AddressLinesDefault;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xal.AddressDetailsDefault;
import org.geotoolkit.data.model.xal.AddressIdentifier;
import org.geotoolkit.data.model.xal.AddressIdentifierDefault;
import org.geotoolkit.data.model.xal.AddressLines;
import org.geotoolkit.data.model.xal.AdministrativeArea;
import org.geotoolkit.data.model.xal.AdministrativeAreaDefault;
import org.geotoolkit.data.model.xal.Country;
import org.geotoolkit.data.model.xal.CountryDefault;
import org.geotoolkit.data.model.xal.CountryNameCode;
import org.geotoolkit.data.model.xal.CountryNameCodeDefault;
import org.geotoolkit.data.model.xal.GenericTypedGrPostal;
import org.geotoolkit.data.model.xal.GenericTypedGrPostalDefault;
import org.geotoolkit.data.model.xal.GrPostal;
import org.geotoolkit.data.model.xal.GrPostalDefault;
import org.geotoolkit.data.model.xal.PostalServiceElements;
import org.geotoolkit.data.model.xal.PostalServiceElementsDefault;
import org.geotoolkit.data.model.xal.SortingCode;
import org.geotoolkit.data.model.xal.SortingCodeDefault;
import org.geotoolkit.data.model.xal.SubAdministrativeArea;
import org.geotoolkit.data.model.xal.SubAdministrativeAreaDefault;
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

}

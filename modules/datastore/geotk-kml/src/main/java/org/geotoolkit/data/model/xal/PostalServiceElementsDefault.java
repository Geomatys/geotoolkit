package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class PostalServiceElementsDefault implements PostalServiceElements{

    private List<AddressIdentifier> addressIdentifiers;
    private GenericTypedGrPostal endorsementLineCode;
    private GenericTypedGrPostal keyLineCode;
    private GenericTypedGrPostal barCode;
    private SortingCode sortingCode;
    private GenericTypedGrPostal addressLatitude;
    private GenericTypedGrPostal addressLatitudeDirection;
    private GenericTypedGrPostal addressLongitude;
    private GenericTypedGrPostal addressLongitudeDirection;
    private List<GenericTypedGrPostal> supplementaryPostalServiceData;
    private String type;

    public PostalServiceElementsDefault(List<AddressIdentifier> addressIdentifiers, GenericTypedGrPostal endorsementLineCode,
            GenericTypedGrPostal keyLineCode, GenericTypedGrPostal barCode, SortingCode sortingCode, GenericTypedGrPostal addressLatitude,
            GenericTypedGrPostal addressLatitudeDirection, GenericTypedGrPostal addressLongitude, GenericTypedGrPostal addressLongitudeDirection,
            List<GenericTypedGrPostal> supplementaryPostalServiceData, String type){
        this.addressIdentifiers = addressIdentifiers;
    this.endorsementLineCode = endorsementLineCode;
    this.keyLineCode = keyLineCode;
    this.barCode = barCode;
    this.sortingCode = sortingCode;
    this.addressLatitude = addressLatitude;
    this.addressLatitudeDirection = addressLatitudeDirection;
    this.addressLongitude = addressLongitude;
    this.addressLongitudeDirection = addressLongitudeDirection;
    this.supplementaryPostalServiceData = supplementaryPostalServiceData;
    this.type = type;
    }

    @Override
    public List<AddressIdentifier> getAddressIdentifiers() {return this.addressIdentifiers;}

    @Override
    public GenericTypedGrPostal getEndorsementLineCode() {return this.endorsementLineCode;}

    @Override
    public GenericTypedGrPostal getKeyLineCode() {return this.keyLineCode;}

    @Override
    public GenericTypedGrPostal getBarcode() {return this.barCode;}

    @Override
    public SortingCode getSortingCode() {return this.sortingCode;}

    @Override
    public GenericTypedGrPostal getAddressLatitude() {return this.addressLatitude;}

    @Override
    public GenericTypedGrPostal getAddressLatitudeDirection() {return this.addressLatitudeDirection;}

    @Override
    public GenericTypedGrPostal getAddressLongitude() {return this.addressLongitude;}

    @Override
    public GenericTypedGrPostal getAddressLongitudeDirection() {return this.addressLongitudeDirection;}

    @Override
    public List<GenericTypedGrPostal> getSupplementaryPostalServiceData() {return this.supplementaryPostalServiceData;}

    @Override
    public String getType() {return this.type;}

}

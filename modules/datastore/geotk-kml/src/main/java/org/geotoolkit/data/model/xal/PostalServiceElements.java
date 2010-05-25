package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public interface PostalServiceElements {

    public List<AddressIdentifier> getAddressIdentifiers();
    public EndorsementLineCode getEndorsementLineCode();
    public KeyLineCode getKeyLineCode();
    public Barcode getBarcode();
    public SortingCode getSortingCode();
    public AddressLatitude getAddressLatitude();
    public AddressLatitudeDirection getAddressLatitudeDirection();
    public AddressLongitude getAddressLongitude();
    public AddressLongitudeDirection getAddressLongitudeDirection();
    public List<SupplementaryPostalServiceData> getSupplementaryPostalServiceData();
}

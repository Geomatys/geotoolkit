package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public interface Country {

    public AddressLine getAddressLine();
    public CountryNameCode getCountryNameCode();
    public CountryName getCountryName();
    public AdministrativeArea getAdministrativeArea();
    //public Locality getLocality();
    //public Thoroughfare getThoroughfare();
}

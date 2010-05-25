package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public interface AddressDetails {

    public PostalServiceElements getPostalServiceElements();
    //public Address getAddress();
    public AddressLines getAddressLines();
    public Country getCountry();
    public AdministrativeArea getAdministrativeArea();
    //public Locality getLocality();
    //public Thoroughfare getThoroughfare();
    public String getAddressType();
    public String getCurrentStatus();
    public String getValidFromDate();
    public String getValidToDate();
    public String getUsage();
    public String getCode();
    public String getAddressDetailsKey();
    
}

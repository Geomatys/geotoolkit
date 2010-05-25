package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public interface AdministrativeArea {

    public AddressLine getAddressLine();
    public AdministrativeAreaName getAdministrativeAreaName();
    public SubAdministrativeArea getSubAdministrativeArea();
}
package org.geotoolkit.data.model.xal;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class CountryDefault implements Country {

    private final List<GenericTypedGrPostal> addressLines;
    private final List<CountryNameCode> countryNameCodes;
    private final List<GenericTypedGrPostal> countryNames;
    private AdministrativeArea administrativeArea;
    private Locality locality;
    private Thoroughfare thoroughfare;

    /**
     *
     * @param addressLines
     * @param countryNameCodes
     * @param countryNames
     * @param localisation
     * @throws XalException
     */
    public CountryDefault(List<GenericTypedGrPostal> addressLines,
            List<CountryNameCode> countryNameCodes, List<GenericTypedGrPostal> countryNames, Object localisation) throws XalException{
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.countryNameCodes = (countryNameCodes == null) ? EMPTY_LIST : countryNameCodes;
        this.countryNames = (countryNames == null) ? EMPTY_LIST : countryNames;
        if (localisation instanceof AdministrativeArea){
            this.administrativeArea = (AdministrativeArea) localisation;
        } else if (localisation instanceof Locality){
            this.locality = (Locality) localisation;
        } else if (localisation instanceof Thoroughfare){
            this.thoroughfare = (Thoroughfare) localisation;
        } else if (localisation != null) {
            throw new XalException("This kind of localisation is not allowed here.");
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getAddressLines() {return this.addressLines;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<CountryNameCode> getCountryNameCodes() {return this.countryNameCodes;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getCountryNames() {return this.countryNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AdministrativeArea getAdministrativeArea() {return this.administrativeArea;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Locality getLocality() {return this.locality;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Thoroughfare getThoroughfare() {return this.thoroughfare;}
}

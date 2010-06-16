package org.geotoolkit.data.model.xal;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultThoroughfare implements Thoroughfare {

    private final List<GenericTypedGrPostal> addressLines;
    private final List<Object> thoroughfareNumbers;
    private final List<ThoroughfareNumberPrefix> thoroughfareNumberPrefixes;
    private final List<ThoroughfareNumberSuffix> thoroughfareNumberSuffixes;
    private final GenericTypedGrPostal thoroughfarePreDirection;
    private final GenericTypedGrPostal thoroughfareLeadingType;
    private final List<GenericTypedGrPostal> thoroughfareNames;
    private final GenericTypedGrPostal thoroughfareTrailingType;
    private final GenericTypedGrPostal thoroughfarePostDirection;
    private final DependentThoroughfare dependentThoroughfare;
    private DependentLocality dependentLocality;
    private Premise premise;
    private Firm firm;
    private PostalCode postalCode;
    private final String type;
    private final DependentThoroughfares dependentThoroughfares;
    private final String dependentThoroughfaresIndicator;
    private final String dependentThoroughfaresConnector;
    private final String dependentThoroughfaresType;

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
     * @param thoroughfarePostDirection
     * @param dependentThoroughfare
     * @param location
     * @param type
     * @param dependentThoroughfares
     * @param dependentThoroughfaresIndicator
     * @param dependentThoroughfaresConnector
     * @param dependentThoroughfaresType
     * @throws XalException
     */
    public DefaultThoroughfare(List<GenericTypedGrPostal> addressLines, List<Object> thoroughfareNumbers,
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
            String dependentThoroughfaresConnector, String dependentThoroughfaresType) throws XalException{
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.thoroughfareNumbers = (thoroughfareNumbers == null) ? EMPTY_LIST : this.verifThoroughfareNumbers(thoroughfareNumbers);
        this.thoroughfareNumberPrefixes = (thoroughfareNumberPrefixes == null) ? EMPTY_LIST : thoroughfareNumberPrefixes;
        this.thoroughfareNumberSuffixes = (thoroughfareNumberSuffixes == null) ? EMPTY_LIST : thoroughfareNumberSuffixes;
        this.thoroughfarePreDirection = thoroughfarePreDirection;
        this.thoroughfareLeadingType = thoroughfareLeadingType;
        this.thoroughfareNames = (thoroughfareNames == null) ? EMPTY_LIST : thoroughfareNames;
        this.thoroughfareTrailingType = thoroughfareTrailingType;
        this.thoroughfarePostDirection = thoroughfarPostDirection;
        this.dependentThoroughfare = dependentThoroughfare;
        if (location instanceof DependentLocality){
            this.dependentLocality = (DependentLocality) location;
        } else if (location instanceof Premise){
            this.premise = (Premise) location;
        } else if (location instanceof Firm){
            this.firm = (Firm) location;
        } else if (location instanceof PostalCode){
            this.postalCode = (PostalCode) location;
        } else if (location != null){
            throw new XalException("This kind of location ("+location.getClass()+") is not allowed here : "+this.getClass());
        }
        this.type = type;
        this.dependentThoroughfares = dependentThoroughfares;
        this.dependentThoroughfaresIndicator = dependentThoroughfaresIndicator;
        this.dependentThoroughfaresConnector = dependentThoroughfaresConnector;
        this.dependentThoroughfaresType = dependentThoroughfaresType;
    }

    /**
     *
     * @param thoroughfareNumbers
     * @return
     * @throws XalException
     */
    private List<Object> verifThoroughfareNumbers(List<Object> thoroughfareNumbers) throws XalException{
        for (Object thoroughfareNumber : thoroughfareNumbers){
            if(!(thoroughfareNumber instanceof ThoroughfareNumber) && !(thoroughfareNumber instanceof ThoroughfareNumberRange))
                throw new XalException("This kind of thoroughfareNumber ("+thoroughfareNumber.getClass()+") is not allowed here : "+this.getClass());
        }
        return thoroughfareNumbers;
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
    public List<Object> getThoroughfareNumbers() {return this.thoroughfareNumbers;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<ThoroughfareNumberPrefix> getThoroughfareNumberPrefixes() {return this.thoroughfareNumberPrefixes;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<ThoroughfareNumberSuffix> getThoroughfareNumberSuffixes() {return this.thoroughfareNumberSuffixes;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getThoroughfarePreDirection() {return this.thoroughfarePreDirection;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getThoroughfareLeadingType() {return this.thoroughfareLeadingType;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getThoroughfareNames() {return this.thoroughfareNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getThoroughfareTrailingType() {return this.thoroughfareTrailingType;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getThoroughfarePostDirection() {return this.thoroughfarePostDirection;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public DependentThoroughfare getDependentThoroughfare() {return this.dependentThoroughfare;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public DependentLocality getDependentLocality() {return this.dependentLocality;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Premise getPremise() {return this.premise;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Firm getFirm() {return this.firm;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalCode getPostalCode() {return this.postalCode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {return this.type;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public DependentThoroughfares getDependentThoroughfares() {return this.dependentThoroughfares;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getDependentThoroughfaresIndicator() {return this.dependentThoroughfaresIndicator;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getDependentThoroughfaresConnector() {return this.dependentThoroughfaresConnector;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getDependentThoroughfaresType() {return this.dependentThoroughfaresType;}

}

package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class ThoroughfareDefault implements Thoroughfare {

    private final List<GenericTypedGrPostal> addressLines;
    private final List<Object> thoroughfareNumbers;
    private final List<ThoroughfareNumberPrefix> thoroughfareNumberPrefixes;
    private final List<ThoroughfareNumberSuffix> thoroughfareNumberSuffixes;
    private final GenericTypedGrPostal thoroughfarePreDirection;
    private final GenericTypedGrPostal thoroughfareLeadingType;
    private final List<GenericTypedGrPostal> thoroughfareNames;
    private final GenericTypedGrPostal thoroughfareTrailingType;
    private final GenericTypedGrPostal thoroughfarPostDirection;
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

    public ThoroughfareDefault(List<GenericTypedGrPostal> addressLines, List<Object> thoroughfareNumbers,
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
        this.addressLines = addressLines;
        this.thoroughfareNumbers = this.verifThoroughfareNumbers(thoroughfareNumbers);
        this.thoroughfareNumberPrefixes = thoroughfareNumberPrefixes;
        this.thoroughfareNumberSuffixes = thoroughfareNumberSuffixes;
        this.thoroughfarePreDirection = thoroughfarePreDirection;
        this.thoroughfareLeadingType = thoroughfareLeadingType;
        this.thoroughfareNames = thoroughfareNames;
        this.thoroughfareTrailingType = thoroughfareTrailingType;
        this.thoroughfarPostDirection = thoroughfarPostDirection;
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

    private List<Object> verifThoroughfareNumbers(List<Object> thoroughfareNumbers) throws XalException{
        for (Object thoroughfareNumber : thoroughfareNumbers){
            if(!(thoroughfareNumber instanceof ThoroughfareNumber) && !(thoroughfareNumber instanceof ThoroughfareNumberRange))
                throw new XalException("This kind of thoroughfareNumber ("+thoroughfareNumber.getClass()+") is not allowed here : "+this.getClass());
        }
        return thoroughfareNumbers;
    }

    @Override
    public List<GenericTypedGrPostal> getAddressLine() {return this.addressLines;}

    @Override
    public List<Object> getThoroughfareNumbers() {return this.thoroughfareNumbers;}

    @Override
    public List<ThoroughfareNumberPrefix> getThoroughfareNumberPrefixes() {return this.thoroughfareNumberPrefixes;}

    @Override
    public List<ThoroughfareNumberSuffix> getThoroughfareNumberSuffixes() {return this.thoroughfareNumberSuffixes;}

    @Override
    public GenericTypedGrPostal getThoroughfarePreDirection() {return this.thoroughfarePreDirection;}

    @Override
    public GenericTypedGrPostal getThoroughfareLeadingType() {return this.thoroughfareLeadingType;}

    @Override
    public List<GenericTypedGrPostal> getThoroughfareNames() {return this.thoroughfareNames;}

    @Override
    public GenericTypedGrPostal getThoroughfareTrailingType() {return this.thoroughfareTrailingType;}

    @Override
    public GenericTypedGrPostal getThoroughfarePostDirection() {return this.thoroughfarPostDirection;}

    @Override
    public DependentThoroughfare getDependentThoroughfare() {return this.dependentThoroughfare;}

    @Override
    public DependentLocality getDependentLocality() {return this.dependentLocality;}

    @Override
    public Premise getPremises() {return this.premise;}

    @Override
    public Firm getFirm() {return this.firm;}

    @Override
    public PostalCode getPostalCode() {return this.postalCode;}

    @Override
    public String getType() {return this.type;}

    @Override
    public DependentThoroughfares getDependentThoroughfares() {return this.dependentThoroughfares;}

    @Override
    public String getDependentThoroughfaresIndicator() {return this.dependentThoroughfaresIndicator;}

    @Override
    public String getDependentThoroughfaresConnector() {return this.dependentThoroughfaresConnector;}

    @Override
    public String getDependentThoroughfaresType() {return this.dependentThoroughfaresType;}

}

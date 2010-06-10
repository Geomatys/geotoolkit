package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class PremiseNumberRangeFromDefault implements PremiseNumberRangeFrom {

    private final List<GenericTypedGrPostal> addressLines;
    private final List<PremiseNumberPrefix> premiseNumberPrefixes;
    private final List<PremiseNumber> premiseNumbers;
    private final List<PremiseNumberSuffix> premiseNumberSuffixes;

    /**
     * 
     * @param addressLines
     * @param premiseNumberPrefixes
     * @param premiseNumbers
     * @param premiseNumberSuffixes
     */
    public PremiseNumberRangeFromDefault(List<GenericTypedGrPostal> addressLines,
            List<PremiseNumberPrefix> premiseNumberPrefixes,
            List<PremiseNumber> premiseNumbers,
            List<PremiseNumberSuffix> premiseNumberSuffixes){
        this.addressLines = addressLines;
        this.premiseNumberPrefixes = premiseNumberPrefixes;
        this.premiseNumbers = premiseNumbers;
        this.premiseNumberSuffixes = premiseNumberSuffixes;
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
    public List<PremiseNumberPrefix> getPremiseNumberPrefixes() {return this.premiseNumberPrefixes;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<PremiseNumber> getPremiseNumbers() {return this.premiseNumbers;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<PremiseNumberSuffix> getPremiseNumberSuffixes() {return this.premiseNumberSuffixes;}

}

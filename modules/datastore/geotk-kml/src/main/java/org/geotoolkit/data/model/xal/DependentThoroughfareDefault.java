package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class DependentThoroughfareDefault implements DependentThoroughfare {

    private final List<GenericTypedGrPostal> addressLines;
    private final GenericTypedGrPostal thoroughfarePreDirection;
    private final GenericTypedGrPostal thoroughfareLeadingType;
    private final List<GenericTypedGrPostal> thoroughfareNames;
    private final GenericTypedGrPostal thoroughfareTrailingType;
    private final GenericTypedGrPostal thoroughfarePostDirection;
    private final String type;

    /**
     *
     * @param addressLines
     * @param thoroughfarePreDirection
     * @param thoroughfareLeadingType
     * @param thoroughfareNames
     * @param thoroughfareTrailingType
     * @param thoroughfarePostDirection
     * @param type
     */
    public DependentThoroughfareDefault(List<GenericTypedGrPostal> addressLines,
            GenericTypedGrPostal thoroughfarePreDirection, GenericTypedGrPostal thoroughfareLeadingType,
            List<GenericTypedGrPostal> thoroughfareNames, GenericTypedGrPostal thoroughfareTrailingType,
            GenericTypedGrPostal thoroughfarePostDirection, String type){
        this.addressLines = addressLines;
        this.thoroughfarePreDirection = thoroughfarePreDirection;
        this.thoroughfareLeadingType = thoroughfareLeadingType;
        this.thoroughfareNames = thoroughfareNames;
        this.thoroughfareTrailingType = thoroughfareTrailingType;
        this.thoroughfarePostDirection = thoroughfarePostDirection;
        this.type = type;
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
    public String getType() {return this.type;}

}

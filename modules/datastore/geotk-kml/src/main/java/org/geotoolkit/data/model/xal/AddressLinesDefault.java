package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class AddressLinesDefault implements AddressLines {

    private List<GenericTypedGrPostal> addressLines;
    
    public AddressLinesDefault(List<GenericTypedGrPostal> addressLines){
        this.addressLines = addressLines;
    }
    
    /**
     * 
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getAddressLines() {return this.addressLines;}

}

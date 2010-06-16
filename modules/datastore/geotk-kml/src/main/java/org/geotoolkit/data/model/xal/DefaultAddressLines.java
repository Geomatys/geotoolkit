package org.geotoolkit.data.model.xal;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAddressLines implements AddressLines {

    private final List<GenericTypedGrPostal> addressLines;
    
    public DefaultAddressLines(List<GenericTypedGrPostal> addressLines){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
    }
    
    /**
     * 
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getAddressLines() {return this.addressLines;}

}

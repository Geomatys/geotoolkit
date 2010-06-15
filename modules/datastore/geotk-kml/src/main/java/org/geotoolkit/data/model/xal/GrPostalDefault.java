package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class GrPostalDefault implements GrPostal{

    private final String code;

    /**
     * 
     * @param code
     */
    public GrPostalDefault(String code){
        this.code = code;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getCode() {return this.code;}

}

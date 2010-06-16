package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultGrPostal implements GrPostal{

    private final String code;

    /**
     * 
     * @param code
     */
    public DefaultGrPostal(String code){
        this.code = code;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getCode() {return this.code;}

}

package org.geotoolkit.data.model.xal;

/**
 * <p>A spécific exception class for xAL parsing errors.</p>
 *
 * @author Samuel Andrés
 */
public class XalException extends Exception{

    private String message;

    /**
     *
     * @param message
     */
    public XalException(String message){
        this.message = message;
    }

    /**
     *
     * @return
     */
    @Override
    public String getMessage(){
        return this.message;
    }



}

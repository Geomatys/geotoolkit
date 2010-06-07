/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.data.model.kml;

/**
 * <p>A spécific exception class for KML parsing errors.</p>
 *
 * @author Samuel Andrés
 */
public class KmlException extends Exception{

    private final String message;

    /**
     *
     * @param message
     */
    public KmlException(String message){
        this.message = message;
    }

    /**
     *
     * @return
     */
    @Override
    public String getMessage(){return this.message;}
}

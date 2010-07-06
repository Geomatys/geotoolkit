/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.data.kml.model;

/**
 * <p>A spécific exception class for KML parsing errors.</p>
 *
 * @author Samuel Andrés
 */
public class KmlException extends Exception{

    /**
     *
     * @param message
     */
    public KmlException(String message){
        super(message);
    }

}

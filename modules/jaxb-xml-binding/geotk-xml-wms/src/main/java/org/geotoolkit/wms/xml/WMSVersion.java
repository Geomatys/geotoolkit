/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.wms.xml;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public enum WMSVersion {
    v111("1.1.1"),
    v130("1.3.0");

    private final String code;

    WMSVersion(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}

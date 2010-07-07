package org.geotoolkit.data.kml.model;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
@Deprecated
public interface MetaData {

    /**
     *
     * @return
     */
    public List<Object> getContent();

    /*
     * 
     */
    public void setContent(List<Object> content);
}

package org.geotoolkit.data.atom.model;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public interface AtomPersonConstruct {

    /**
     *
     * @return
     */
    public List<Object> getParams();

    /**
     * 
     * @param params
     */
    public void setParams(final List<Object> params);
}

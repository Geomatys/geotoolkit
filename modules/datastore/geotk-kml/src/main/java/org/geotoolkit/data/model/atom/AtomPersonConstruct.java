package org.geotoolkit.data.model.atom;

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
    public List<String> getNames();

    /**
     *
     * @return
     */
    public List<String> getUris();

    /**
     * 
     * @return
     */
    public List<String> getEmails();
}

package org.geotoolkit.data.model.atom;

/**
 *
 * @author Samuel Andrés
 */
public interface AtomLink {

    /**
     *
     * @return
     */
    public String getHref();

    /**
     *
     * @return
     */
    public String getRel();

    /**
     *
     * @return
     */
    public String getType();

    /**
     *
     * @return
     */
    public String getHreflang();

    /**
     *
     * @return
     */
    public String getTitle();

    /**
     * 
     * @return
     */
    public String getLength();
}

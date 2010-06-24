package org.geotoolkit.data.atom.model;

/**
 * <p>This interface maps atom link element.</p>
 *
 * <pre>
 * &lt;element name="link">
 *  &lt;complexType>
 *      &lt;attribute name="href" use="required"/>
 *      &lt;attribute name="rel"/>
 *      &lt;attribute name="type" type="atom:atomMediaType"/>
 *      &lt;attribute name="hreflang" type="atom:atomLanguageTag"/>
 *      &lt;attribute name="title"/>
 *      &lt;attribute name="length"/>
 *  &lt;/complexType>
 * &lt;/element>
 * </pre>
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

    /**
     *
     * @param href
     */
    public void setHref(final String href);

    /**
     *
     * @param rel
     */
    public void setRel(final String rel);

    /**
     *
     * @param type
     */
    public void setType(final String type);

    /**
     *
     * @param hreflang
     */
    public void setHreflang(final String hreflang);

    /**
     *
     * @param title
     */
    public void setTitle(final String title);

    /**
     *
     * @param length
     */
    public void setLength(final String length);
}

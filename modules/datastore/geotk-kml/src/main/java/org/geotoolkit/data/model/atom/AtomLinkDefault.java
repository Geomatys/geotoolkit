package org.geotoolkit.data.model.atom;

/**
 *
 * @author Samuel Andrés
 */
public class AtomLinkDefault implements AtomLink{

    final private String href;
    final private String rel;
    final private String type;
    final private String hreflang;
    final private String title;
    final private String length;

    /**
     *
     * @param href
     * @param rel
     * @param type
     * @param hreflang
     * @param title
     * @param length
     */
    public AtomLinkDefault(String href, String rel, String type, String hreflang, String title, String length){
        this.href = href;
        this.rel = rel;
        this.type = type;
        this.hreflang = hreflang;
        this.title = title;
        this.length = length;
    }
            
    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getHref() {return this.href;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getRel() {return this.rel;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {return this.type;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getHreflang() {return this.hreflang;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getTitle() {return this.title;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getLength() {return this.length;}

}

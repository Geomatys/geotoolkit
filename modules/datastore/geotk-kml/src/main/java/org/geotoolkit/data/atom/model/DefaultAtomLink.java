package org.geotoolkit.data.atom.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAtomLink implements AtomLink {

    private String href;
    private String rel;
    private String type;
    private String hreflang;
    private String title;
    private String length;

    /**
     *
     */
    public DefaultAtomLink() {
        this.href = null;
        this.rel = null;
        this.type = null;
        this.hreflang = null;
        this.title = null;
        this.length = null;
    }

    /**
     * 
     * @param href
     * @param rel
     * @param type
     * @param hreflang
     * @param title
     * @param length
     */
    public DefaultAtomLink(final String href, final String rel, final String type,
            final String hreflang, final String title, final String length) {
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
    public String getHref() {
        return this.href;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getRel() {
        return this.rel;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {
        return this.type;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getHreflang() {
        return this.hreflang;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getTitle() {
        return this.title;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getLength() {
        return this.length;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHref(final String href) {
        this.href = href;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRel(final String rel) {
        this.rel = rel;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setType(final String type) {
        this.type = type;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHreflang(final String hreflang) {
        this.hreflang = hreflang;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLength(final String length) {
        this.length = length;
    }
}

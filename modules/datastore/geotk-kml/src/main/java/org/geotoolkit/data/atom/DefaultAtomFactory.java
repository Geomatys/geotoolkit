package org.geotoolkit.data.atom;

import java.util.List;
import org.geotoolkit.data.atom.model.AtomEmail;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.atom.model.DefaultAtomLink;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.atom.model.DefaultAtomEmail;
import org.geotoolkit.data.atom.model.DefaultAtomPersonConstruct;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAtomFactory implements AtomFactory {

    /**
     * 
     * @{@inheritDoc }
     */
    @Override
    public AtomLink createAtomLink(String href, String rel, String type,
            String hreflang, String title, String length) {
        return new DefaultAtomLink(href, rel, type, hreflang, title, length);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomPersonConstruct createAtomPersonConstruct(List<Object> params) {
        return new DefaultAtomPersonConstruct(params);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomEmail createAtomEmail(String address) {
        return new DefaultAtomEmail(address);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomLink createAtomLink() {
        return new DefaultAtomLink();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomPersonConstruct createAtomPersonConstruct() {
        return new DefaultAtomPersonConstruct();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomEmail createAtomEmail() {
        return new DefaultAtomEmail();
    }
}

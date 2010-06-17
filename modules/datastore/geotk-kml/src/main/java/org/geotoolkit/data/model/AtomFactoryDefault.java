package org.geotoolkit.data.model;

import java.util.List;
import org.geotoolkit.data.model.atom.AtomEmail;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.atom.DefaultAtomLink;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.atom.DefaultAtomEmail;
import org.geotoolkit.data.model.atom.DefaultAtomPersonConstruct;

/**
 *
 * @author Samuel Andrés
 */
public class AtomFactoryDefault implements AtomFactory{

    /**
     * 
     * @{@inheritDoc }
     */
    @Override
    public AtomLink createAtomLink(String href, String rel, String type, String hreflang, String title, String length) {
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

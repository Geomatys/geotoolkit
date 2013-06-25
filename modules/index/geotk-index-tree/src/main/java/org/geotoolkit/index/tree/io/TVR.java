/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.index.tree.io;

/**
 * Util methods from TreeVisitorResult.
 *
 * @author Rémi Marechal (Geomatys).
 */
public final class TVR {

    private static final TreeVisitorResult CONTINUE      = TreeVisitorResult.CONTINUE;
    private static final TreeVisitorResult SKIP_SIBLINGS = TreeVisitorResult.SKIP_SIBLINGS;
    private static final TreeVisitorResult SKIP_SUBTREE  = TreeVisitorResult.SKIP_SUBTREE;
    private static final TreeVisitorResult TERMINATE     = TreeVisitorResult.TERMINATE;

    private TVR() {
    }

    public static boolean isContinue(final TreeVisitorResult tvr) {
        return tvr == CONTINUE;
    }

    public static boolean isSkipSibling(final TreeVisitorResult tvr) {
        return tvr == SKIP_SIBLINGS;
    }

    public static boolean isSkipSubTree(final TreeVisitorResult tvr) {
        return tvr == SKIP_SUBTREE;
    }

    public static boolean isTerminate(final TreeVisitorResult tvr) {
        return tvr == TERMINATE;
    }
}

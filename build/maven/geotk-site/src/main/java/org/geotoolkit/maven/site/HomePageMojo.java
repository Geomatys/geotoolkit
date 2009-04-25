/*
 *    Geotoolkit - An Open Source Java GIS Tookit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.maven.site;

import java.io.*;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;


/**
 * Modifies the home page in order to insert the Geotoolkit mascot.
 * Maven invocation syntax is:
 *
 * <blockquote><code>mvn org.geotoolkit.project:geotk-site:home --non-recursive</code></blockquote>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 *
 * @goal home
 * @phase post-site
 */
public class HomePageMojo extends AbstractMojo {
    /**
     * The file encoding.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * The Maven project running this plugin.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * Modifies the home page.
     *
     * @throws MojoExecutionException if the plugin execution failed.
     */
    @Override
    public void execute() throws MojoExecutionException {
        final File file = new File(project.getBuild().getDirectory(), "site/index.html");
        try {
            process(file);
        } catch (IOException e) {
            throw new MojoExecutionException(e.toString(), e);
        }
    }

    /**
     * Modifies the given home page. This method searchs for the two first
     * {@code <div class="section">} strings. A {@code <table>} with a single
     * cell is opened after the first section and closed before the second section.
     * The purpose of the table is to prevent the second question to be layout at
     * the right side of the image.
     *
     * @param file The file to the home page to modify.
     * @throws IOException If an error occured while reading or writing the page.
     */
    private static void process(final File file) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENCODING));
        final StringBuilder buffer = new StringBuilder();
        int section = 0; // The count of sections.
        String line;
        while ((line = in.readLine()) != null) {
            final String tl = line.trim();
            if (tl.length() == 0) {
                // Skip empty lines (Maven put a lot of them...).
                continue;
            }
            if (tl.startsWith("<div class=\"section\">")) {
                switch (++section) {
                    case 1: {
                        buffer.append(line).append('\n');
                        line = "<table><tr><td><img src=\"images/logos/Troll.jpg\" align=\"left\"/>";
                        break;
                    }
                    case 2: {
                        buffer.append("</td></tr></table>").append('\n');
                        break;
                    }
                }
            }
            buffer.append(line).append('\n');
        }
        in.close();
        final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODING);
        out.write(buffer.toString());
        out.close();
    }
}

/*
 *    Geotoolkit - An Open Source Java GIS Tookit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.maven.taglet;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Writer;


/**
 * Adds a javadoc tag at the end of class comment in a set of source files. The default
 * implementation adds the {@code @module} tag. The algorithm used in this class has
 * limited capabilities and expects comments formatted in a "classic" fashion:
 * <p>
 * <ul>
 *   <li>Javadoc comments just before the first "{@code class}" keyword found in the source file.</li>
 *   <li>"slash-star" style comment at the begining of the first comment line.</li>
 *   <li>"star-slash" style comment at the end of the last comment line.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 */
public class CommentUpdater implements FileFilter {
    /**
     * The sequence of characters starting a comment.
     * Must be first on a line.
     */
    private static final String START_COMMENT = "/**";

    /**
     * The sequence of characters ending a comment.
     * Must be last on a line.
     */
    private static final String END_COMMENT = "*/";

    /**
     * The javadoc tag to add.
     */
    private final String tag = "@module";

    /**
     * The value to add after the javadoc to add.
     * May be {@code null} if none.
     */
    private final String value = null;

    /**
     * If this {@code nearTag} is found, then {@link #tag} will be inserted the line
     * before or after.
     */
    private final String nearTag = "@since";

    /**
     * If {@code true}, then {@link #tag} will be inserted before {@link #nearTag}.
     * If {@code true}, then {@link #tag} will be inserted after {@link #nearTag}.
     */
    private final boolean insertBeforeNearTag = true;

    /**
     * Creates an updater for the default javadoc tag.
     */
    public CommentUpdater() {
    }

    /**
     * Process all files specified on the command line. If the specified files are directories,
     * all {@code .java} files found in those directories and sub-directories will be processed.
     *
     * @param  args List of files or directories to process.
     * @throws IOException if an I/O operation failed.
     */
    public static void main(final String[] args) throws IOException {
        final CommentUpdater processor = new CommentUpdater();
        for (int i=0; i<args.length; i++) {
            final int count = processor.processAll(new File(args[i]));
            System.out.print(count);
            System.out.println(" modified files.");
        }
    }

    /**
     * Tests whether or not the specified abstract pathname should be processed.
     */
    @Override
    public boolean accept(final File pathname) {
        return pathname.isDirectory() || (pathname.isFile() && pathname.getName().endsWith(".java"));
    }

    /**
     * If the specified file is a directory, then process all {@code .java} files in this
     * directory. Otherwise, unconditionnaly process the specified file a regular file.
     *
     * @param  file The directory to process.
     * @return The number of file modified.
     * @throws IOException if an I/O operation failed.
     */
    private int processAll(final File file) throws IOException {
        if (file.isDirectory()) {
            int count = 0;
            final File[] files = file.listFiles(this);
            for (int i=0; i<files.length; i++) {
                count += processAll(files[i]);
            }
            return count;
        } else {
            return process(file) ? 1 : 0;
        }
    }

    /**
     * Process the specified file.
     *
     * @param  file The file to process.
     * @return {@code true} if the file has been modified.
     * @throws IOException if an I/O operation failed.
     */
    protected boolean process(final File file) throws IOException {
        final LineNumberReader  in = new LineNumberReader(new FileReader(file));
        final StringBuilder buffer = new StringBuilder();
        final String lineSeparator = System.getProperty("line.separator", "\n");
        String message = null;
        int      insertAt = 0;
        int  startComment = 0;
        int   lastComment = 0;
        int    endComment = 0;
        boolean isComment = false;
        boolean   success = false;
        String line;
scan:   while ((line=in.readLine()) != null) {
            /*
             * Search for the begining of a comment block.  The comments must start at the
             * begining of the line (except for whitespace), otherwise we stop the process
             * as a safety.
             */
            if (!isComment) {
                int i = line.indexOf(START_COMMENT);
                if (i >= 0) {
                    while (--i >= 0) {
                        if (!Character.isWhitespace(line.charAt(i))) {
                            // This simple algorithm doesn't know how to process such file.
                            message = START_COMMENT + " should appears at the begining of the line.";
                            break scan;
                        }
                    }
                    startComment = buffer.length();
                    isComment = true;
                }
            }
            /*
             * Search for javadoc tag we want to add. If this javadoc tag is found, then
             * we will left this file unchanged.
             */
            buffer.append(line);
            buffer.append(lineSeparator);
            final int length = line.length();
            if (isComment) {
                int i = line.indexOf(tag);
                if (i >= 0) {
                    i += tag.length();
                    if (i >= length || !Character.isLetter(line.charAt(i))) {
                        message = tag + " tag already presents.";
                        break scan;
                    }
                }
                i = line.indexOf(nearTag);
                if (i >= 0) {
                    i += nearTag.length();
                    if (i >= length || !Character.isLetter(line.charAt(i))) {
                        insertAt = buffer.length();
                        if (insertBeforeNearTag) {
                            insertAt -= (length + lineSeparator.length());
                        }
                    }
                }
                /*
                 * Search for the end of a comment block. The comments must finish at the
                 * end of the line (except for whitespace), otherwise we stop the process
                 * as a safety.
                 */
                i = line.indexOf(END_COMMENT);
                if (i >= 0) {
                    i += END_COMMENT.length();
                    while (i < length) {
                        if (!Character.isWhitespace(line.charAt(i++))) {
                            // This simple algorithm doesn't know how to process such file.
                            message = END_COMMENT + " should appears at the end of the line.";
                            break scan;
                        }
                    }
                    endComment = buffer.length();
                    isComment = false;
                } else {
                    // Position before the line that close the comments.
                    lastComment = buffer.length();
                }
                continue scan;
            }
            /*
             * From this point, we know that we are not in a comment block and the previous
             * comments (if any) didn't had the javadoc tag that we want to add. Search for
             * the "class" word on the line.
             */
            int lower=0;
            String word;
            do {
                while (lower<length && Character.isWhitespace(line.charAt(lower))) lower++;
                int upper = lower;
                while (upper<length && Character.isLetter(line.charAt(upper))) upper++;
                if (lower == upper) {
                    // The first character found is not a letter.
                    continue scan;
                }
                if (upper<length && !Character.isWhitespace(line.charAt(upper))) {
                    // The word is not followed by a space. The class name was expected.
                    continue scan;
                }
                word = line.substring(lower, upper);
                lower = upper; // Will be the lower index for the next iteration.
            } while (!word.equals("class") && !word.equals("interface"));
            /*
             * We have now found the position where to inserts our javadoc tag. Process to the
             * insertion now, and then just copies all remaining lines without any processing.
             */
            if (insertAt <= startComment  ||  insertAt >= endComment) {
                if (lastComment <= startComment  ||  lastComment >= endComment) {
                    message = "No comments found for this class.";
                    break scan;
                }
                insertAt = lastComment;
            }
            final String insert;
            if (value != null) {
                insert = " * " + tag + ' ' + value + lineSeparator;
            } else {
                insert = " * " + tag + lineSeparator;
            }
            buffer.insert(insertAt, insert);
            while ((line=in.readLine()) != null) {
                buffer.append(line);
                buffer.append(lineSeparator);
            }
            success = true;
            break;
        }
        /*
         * Closes the input stream and log a message, if any.
         * If a javadoc tag has been added, overwrite the file now.
         */
        if (message != null) {
            System.out.print(file);
            System.out.print(':');
            System.out.println(in.getLineNumber());
            System.out.println(message);
            System.out.println();
        }
        in.close();
        if (success) {
            final Writer out = new FileWriter(file);
            out.write(buffer.toString());
            out.close();
        }
        return success;
    }
}

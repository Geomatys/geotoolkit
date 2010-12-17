import java.io.*;

/**
 * Searches for all "<code>org</code>" directories from the current directory,
 * and moves its sub-directories in a single "<code>org</code>" directory in
 * the current directory. The old sub-directories are deleted.
 */
public final class Move {
    private Move() {
    }

    public static void main(String[] args) {
        process(new File("."), null);
    }

    private static void process(final File directory, final File root) {
        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                File fileRoot = root;
                if (file.getName().equals("org")) {
                    File parent = file.getParentFile();
                    if (parent.getName().equals("java")) {
                        parent = parent.getParentFile();
                        if (parent.getName().equals("main")) {
                            if (parent.getPath().contains("modules")) {
                                fileRoot = file.getParentFile();
                            }
                        }
                    }
                }
                process(file, fileRoot);
                // Delete the directory later in this block.
            } else if (root != null) {
                final File dest = new File(file.getPath().substring(root.getPath().length() + 1));
                System.out.println(dest);
                dest.getParentFile().mkdirs();
                if (!file.renameTo(dest)) {
                    System.err.println("Can't move " + file);
                    System.exit(1);
                }
                continue;
            }
            if (!file.delete()) {
                System.err.println("Can't delete " + file);
                System.exit(1);
            }
        }
    }
}

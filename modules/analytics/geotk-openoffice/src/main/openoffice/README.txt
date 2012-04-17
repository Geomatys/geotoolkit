Subdirectories contain LibreOffice/OpenOffice.org documents that demonstrate the Geotk add-in.
To make the files more suitable to a versioning system, we did not committed the binary documents.
Instead, we applied the following step:

  * We unzipped the LibreOffice/OpenOffice.org documents, each document in its own directory.
  * We reformatted every XML files, then changed the indentation from 4 spaces to 1 space.
    The reformatting is needed because the whole XML contents were initially on a single line,
    which do not work well with 'diff' tools, including the versioning system itself.

To recreate the LibreOffice/OpenOffice.org files, just run "ant" in this directory (this is done
automatically by the Maven build). The result will be placed in the Maven target site directory.

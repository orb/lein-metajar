# lein-metajar

To bundle an application, leiningen provides an uberjar that smashes
all the dependent JARs into a single JAR. This works great, but JAR files
(before Java 8) are limited to 65535 files. This is sufficient even for most
reasonably-sized projects, but a very large Clojure project can exceed these
limits.

The metajar pluging is an attempt to create the functional equivalent
of an uberjar with dependencies in a lib dir and linked by the JAR
file's `MANIFEST.MF` `Class-Path`. This has the disadvantage that
running requires copying both the JAR and the associated `lib` dir,
but if this is done the resulting JAR can be used without worrying about the
JVM classpath, just as with an uberjar.

## Usage

Add `[lein-metajar "0.1.2"]` the `:plugins` vector of your `:user` profile or your `project.clj`.


To create the METAJAR:

    $ lein metajar

## License

Copyright © 2015 ThreatGRID

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

# lein-metajar

A Leiningen plugin to create the equivalent of an uberjar with dependencies in a lib
dir and linked by the JAR file's `MANIFEST.MF` `Class-Path`.

## Usage

Add `[lein-metajar "0.1.0-SNAPSHOT"]` the `:plugins` vector of your `:user` profile or your `project.clj`.


To create the METAJAR:

    $ lein metajar

## License

Copyright © 2015 ThreatGRID

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

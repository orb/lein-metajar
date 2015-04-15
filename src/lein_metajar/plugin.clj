(ns lein-metajar.plugin
  (:require [leiningen.core.main :as main]
            [leiningen.metajar :as metajar]))

(defn middleware [project]
  (assoc-in project [:manifest "Class-Path"] metajar/manifest-class-path))

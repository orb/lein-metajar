(ns lein-metajar.plugin
  (:require [leiningen.metajar :as metajar]))

(defn add-metajar-profile [project]
  (update-in project [:profiles :metajar]
             merge {:manifest {"Class-Path" metajar/manifest-class-path}}))

(defn middleware [project]
  (vary-meta project add-metajar-profile))

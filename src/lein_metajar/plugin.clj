(ns lein-metajar.plugin
  (:require [leiningen.metajar :as metajar]))

(defn add-metajar-profile [project]
  (assoc-in project [:profiles :metajar]
            {:manifest {"Class-Path" metajar/manifest-class-path}
             :aot [:all]}))


(defn middleware [project]
  (vary-meta project add-metajar-profile))

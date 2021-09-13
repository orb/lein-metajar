(ns lein-metajar.plugin
  (:require [leiningen.metajar :as metajar]
            [clojure.string :as str]))

;;Deprecated
(defn add-metajar-profile [project]
  (update-in project [:profiles :metajar]
             merge {:manifest {"Class-Path" metajar/manifest-class-path}}))

(defn default-metajar-profile
  [project]
  (let [libdir (get-in project [:profiles :metajar :libdir] "lib")
        libdir (if (str/ends-with? libdir "/")
                 libdir
                 (str libdir "/"))]
    (-> project
        (assoc-in [:profiles :metajar :libdir] libdir)
        (update-in [:profiles :metajar] merge {:manifest {"Class-Path" metajar/manifest-class-path}}))))

(defn middleware [project]
  (vary-meta project default-metajar-profile))

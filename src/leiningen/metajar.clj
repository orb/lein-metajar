(ns leiningen.metajar
  (:require [clojure.java.io :as io]
            [leiningen.core.project :as project]
            [leiningen.core.classpath :as classpath]
            [leiningen.core.main :as main]
            [leiningen.jar :as jar]
            [leiningen.uberjar :as uberjar]))

(defn meta-libdir [project]
  (io/file (:root project) "target" "lib"))

(defn file-exists [f]
  (.exists f))

(defn dependent-jars [project]
  (filter file-exists
          (classpath/resolve-dependencies :dependencies project)))

(defn updated-jar-list [project]
  (let [updated-project (project/unmerge-profiles project [:dev :provided])]
    (dependent-jars updated-project)))

(defn copy-files-to [files destination]
  (doseq [file files]
    (let [destination-file (io/file destination (.getName file))]
      (io/make-parents destination-file)
      (io/copy file destination-file))))

(defn manifest-class-path [project]
  (let [under-lib      #(str "lib/" (.getName %))
        jars           (updated-jar-list project)
        relative-paths (map under-lib jars)
        path           (clojure.string/join " " relative-paths)]
    path))

(defn metajar
  "Create the metajar, because... META"
  [project]

  ;; make the basic jar
  (jar/jar (project/merge-profiles project [:metajar]))

  ;; after jar because jar does a clean
  (let [target-dir (meta-libdir project)
        jars (updated-jar-list project)]

    (copy-files-to jars target-dir)
    (main/info "Copied" (count jars) "dependencies to target:" (.getName target-dir))))

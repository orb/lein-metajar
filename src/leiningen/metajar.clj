(ns leiningen.metajar
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [leiningen.core.project :as project]
            [leiningen.core.classpath :as classpath]
            [leiningen.core.main :as main]
            [leiningen.jar :as jar]
            [leiningen.uberjar :as uberjar]))

(defn- libpath [project]
  (let [project (project/merge-profiles project [:metajar])
        path (or (:libdir project) "lib")]
    (if (str/ends-with? path "/")
      path
      (str path \/))))


(defn meta-libdir [project]
  (io/file (:target-path project)
           (libpath project)))

(defn file-exists [f]
  (.exists f))

(defn dependent-jars [project]
  (filter file-exists
          (classpath/resolve-dependencies :dependencies project)))

;; Deprecated
(defn updated-jar-list [project]
  (let [updated-project (project/set-profiles project [:metajar])]
    (dependent-jars updated-project)))

(defn copy-files-to [files destination]
  (doseq [file files]
    (let [destination-file (io/file destination (.getName file))]
      (io/make-parents destination-file)
      (io/copy file destination-file))))

(defn manifest-class-path [project]
  (let [path           (libpath project)
        under-lib      #(str path (.getName %))
        jars           (dependent-jars (project/set-profiles project [:metajar :provided]))
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
        jars (dependent-jars (project/set-profiles project [:metajar]))]

    (copy-files-to jars target-dir)
    (main/info "Copied" (count jars) "dependencies to target:" (.getName target-dir))))

(ns leiningen.metajar
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [leiningen.core.project :as project]
            [leiningen.core.classpath :as classpath]
            [leiningen.core.main :as main]
            [leiningen.jar :as jar]
            [leiningen.uberjar :as uberjar]))

;; Deprecated
(defn- libpath [project]
  (let [project (project/merge-profiles project [:metajar])
        path (or (:libdir project) "lib")]
    (if (str/ends-with? path "/")
      path
      (str path \/))))


(defn meta-libdir [project]
  (io/file (:target-path project)
           (:libdir project)))

(defn file-exists [f]
  (.exists f))

(defn normalize
  [path]
  (.getCanonicalPath (io/file path)))

(defn parent?
  [parent child]
  (= (normalize parent) (.getParent (io/file (normalize child)))))

(defn ancestor?
  [parent child]
  (let [parent (normalize parent)
        child (normalize child)]
    (boolean (some (partial parent? parent)
                   (take-while (complement nil?) (iterate #(when (seq %) (.getParent (io/file %))) child))))))

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
  (let [path           (:libdir project)
        under-lib      #(str path (.getName %))
        jars           (dependent-jars (project/set-profiles project [:metajar :provided]))
        relative-paths (map under-lib jars)
        path           (clojure.string/join " " relative-paths)]
    (main/info "Wrote" (count relative-paths) "dependencies to MANIFEST.MF")
    path))

(defn metajar
  "Create the metajar, because... META"
  [project]

  (let [base-target (:target-path project)
        project (project/set-profiles project [:metajar])
        target-dir (meta-libdir project)]

    (when (not (ancestor? base-target target-dir))
      (main/abort (normalize target-dir) "is outside of the target-path directory."))

    ;; make the basic jar
    (jar/jar project)

    ;; after jar because jar does a clean
    (let [jars (dependent-jars project)]

      (copy-files-to jars target-dir)
      (main/info "Copied" (count jars) "dependencies to target" (normalize target-dir)))))

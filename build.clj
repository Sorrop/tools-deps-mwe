(ns build
  (:require [clojure.tools.build.api :as b]))


(def lib 'core)

(def version (format "0.0.%s" (b/git-count-revs nil)))

(def class-dir "target/classes")

(def basis (b/create-basis {:project "deps.edn"}))

(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn foo [_] (println "FOO was ran!"))

(defn clean [_]
  (b/delete {:path "target"}))


(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :version   version
                :lib       lib
                :basis     basis
                :src-dirs  ["src"]})
  (b/copy-dir {:src-dirs   ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file  jar-file
          :main 'core.main}))

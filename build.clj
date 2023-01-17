(ns build
  (:require [clojure.tools.build.api :as b]
            [clojure.core.server :as server]
            [cider.nrepl :refer (cider-nrepl-handler)]
            [nrepl.server
             :refer (start-server stop-server)]))

(def lib 'deps-mwe/core)

(def version (format "0.0.%s" (b/git-count-revs nil)))

(def class-dir "target/classes")

(def basis (b/create-basis {:project "deps.edn"}))

(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))


(defn clean [_]
  (b/delete {:path "target"}))

(defn uber [_]
  (clean nil)
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis basis
           :main 'deps-mwe.core}))


;; This is for obtaining a repl that can require this namespace
;; `clojure -T:build start-socket-repl :port 1337`
;; Does not work with emacs/cider :(
;; Connect to it with nc localhost 1337
(defn start-socket-repl [{:keys [port]}]
  (println "Starting socket REPL on port" port)
  (server/start-server
   {:port port
    :name :repl
    :accept 'clojure.core.server/io-prepl #_'clojure.core.server/repl
    :server-daemon false}))


;; This works with emacs/cider
;; clojure -T:build start-nrepl :port 7788
;; Then do `cider-connect` from emacs
(defn start-nrepl
  [{:keys [port]}]
  (println "Starting nREPL server on port" port)
  (start-server :port port
                :handler cider-nrepl-handler)
  @(promise))

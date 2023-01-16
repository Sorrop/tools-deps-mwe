(ns deps-mwe.core
  (:require [clj-http.client :as http]
            [jsonista.core :as json]
            [clojure.pprint :as pretty]
            [clojure.java.io :as io])
  (:gen-class))


(def obj-mapper
  json/keyword-keys-object-mapper)


(defn -main [& opts]
  (let [resp (-> (http/get "https://api.exchangerate.host/latest")
                 :body)
        data (json/read-value resp obj-mapper)]
    (pretty/pprint data (io/writer "results.edn"))
    (println "Done. Check results.edn")))

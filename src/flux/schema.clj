(ns flux.schema
  (:require [medley.core :as u])
  (:import [org.apache.solr.client.solrj.request.schema SchemaRequest$AddField]))


(defn add-fields [conn fields]
  (let [fields (if (sequential? fields) fields (vector fields))]
    (doseq [field fields]
      (let [req (->> field
                  (u/map-keys name)
                  (java.util.HashMap.)
                  (SchemaRequest$AddField.))]
        (.process req conn)))))


(defn get-fields [core]
  (-> core
    (.getLatestSchema)
    (.getFields)))

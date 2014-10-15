(ns flux.convert
  (:import [org.apache.solr.client.solrj SolrResponse]
           [org.apache.solr.common.util NamedList SimpleOrderedMap]
           [org.apache.solr.common SolrDocumentList SolrDocument]
           [org.apache.solr.common SolrInputDocument]
           [java.util ArrayList]))

(defprotocol ToKey
  (->key [_] "Convert the object to a proper key for a Clojure value."))

(extend-protocol ToKey
  Object
  (->key [o] o)
  String
  (->key [s] (keyword s)))

(defprotocol ToClojure
  (->clojure [_] "Transform the object into a Clojure value."))

(extend-protocol ToClojure

  Object
  (->clojure [obj]
    obj)

  SimpleOrderedMap
  (->clojure [obj]
    (reduce
     (fn [acc ^java.util.Map$Entry o]
       (assoc acc (keyword (.getKey o)) (->clojure (.getValue o))))
     {}
     (iterator-seq (.iterator obj))))

  NamedList
  (->clojure [obj]
    (into {}
          (mapv
           (fn [^java.util.Map$Entry o] (vector (->key (.getKey o)) (->clojure (.getValue o))))
           (iterator-seq (.iterator obj)))))

  ArrayList
  (->clojure [obj]
    (mapv ->clojure obj))

  SolrDocumentList
  (->clojure [obj]
    (merge
     {:numFound (.getNumFound obj)
      :start (.getStart obj)
      :docs (mapv ->clojure (iterator-seq (.iterator obj)))}
     (when-let [ms (.getMaxScore obj)]
       {:maxScore ms})))

  SolrDocument
  (->clojure [obj]
    (reduce
     (fn [acc f]
       (assoc acc (keyword f) (->clojure (.getFieldValue obj f))))
     {}
     (.getFieldNames obj)))

  SolrResponse
  (->clojure [obj]
    (->clojure (.getResponse obj)))

  SolrInputDocument
  (->clojure [obj]
    (reduce
     (fn [acc o]
       (assoc acc (keyword o) (.getFieldValue obj o)))
     {}
     (.getFieldNames obj))))

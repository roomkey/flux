(ns flux.update
  (:import [org.apache.solr.common SolrInputDocument]
           [org.apache.solr.common SolrInputField]))

(defn stringify-key
  "Turns keywords into strings without the leading colon, but retaining keyword
  namespace. Lets strings pass through unchanged."
  [k]
  (if (keyword? k)
    (subs (str k) 1)
    k))

(defn create-doc ^SolrInputDocument [document-map]
  (reduce-kv
    (fn [^SolrInputDocument doc k v]
      (doto doc
        (.addField (stringify-key k) v)))
    (SolrInputDocument. (java.util.HashMap.))
    document-map))

(defmethod print-method SolrInputDocument [^SolrInputDocument v ^java.io.Writer w]
  (.write w (.toString v)))

(ns flux.cloud
  (:import [org.apache.solr.client.solrj.impl CloudSolrClient]))

(defn create
  ([zk-hosts]
   (CloudSolrClient. zk-hosts))

  ([zk-hosts default-collection]
   (doto (CloudSolrClient. zk-hosts)
     (.setDefaultCollection default-collection))))

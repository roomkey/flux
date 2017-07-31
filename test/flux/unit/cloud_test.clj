(ns flux.unit.cloud-test
  (:require [clojure.test :refer :all]
            [flux.cloud :as cloud])
  (:import [org.apache.solr.client.solrj.impl CloudSolrClient]))

(deftest create-test
  (is (instance? CloudSolrClient
        (cloud/create "zk1:2181"))))

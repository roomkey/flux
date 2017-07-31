(ns flux.unit.http-test
  (:require [clojure.test :refer :all]
            [flux.http :as http])
  (:import [org.apache.solr.client.solrj.impl HttpSolrClient]))

(deftest create-test
  (is (instance? HttpSolrClient
        (http/create "http:///localhost:8983/solr" :some-collection))))

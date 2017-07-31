(ns flux.unit.convert-test
  (:import [org.apache.solr.common.util NamedList])
  (:require [flux.update :as update]
            [flux.convert :as convert]
            [flux.spec :as spec]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]))

(stest/check `update/create-doc)

#_(fact "Convert named list"
  (let [nl (NamedList. (into-array {"a" 1 "b" 2}))]
    (->clojure nl) => {:a 1 :b 2}))

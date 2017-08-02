(ns flux.unit.schema-test
  (:require [flux.schema :as schema]
            [flux.embedded :as e]
            [flux.spec :as spec]
            [clojure.spec.alpha :as s]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.properties :as prop]))

(defn get-core [container]
  (first (.getCores container)))

(defspec add-fields-to-schema 10
  (prop/for-all [inputs (s/gen (s/coll-of ::spec/field-attributes))]
    (let [{:keys [core core-container]} (e/create-dev-core "core1")]
      (schema/add-fields core inputs)
      (let [fields (set (keys (schema/get-fields (get-core core-container))))]
        (= fields (set (map :name inputs)))))))

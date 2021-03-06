(ns flux.unit.client-test
  (:require [flux.embedded :as e]
            [flux.client :as solr]
            [flux.update :as update]
            [flux.spec :as spec]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer :all]
            [medley.core :as u]))

(def cc (e/create-core-container "test/resources/solr"))
(def conn (e/create cc "core1"))

(s/def ::id spec/not-empty-string?)
(s/def ::name spec/not-empty-string?)
(s/def ::code spec/not-empty-string?)
(s/def ::is_primary boolean?)

(s/def ::solr-document
  (s/keys
    :req-un [::id]
    :opt-un [::name ::code ::is_primary]))

(s/def ::solr-add-input
  (s/or :single-doc ::solr-document
        :doc-collection (s/coll-of ::solr-document
                          :min-count 1)))

(defn reduce-by-id
  "Given a collection of solr inputs, returns a map from document id to ending
  document value."
  [input-sample]
  (let [inputs (->> input-sample
                 (map (fn [input]
                        (if (map? input)
                          (vector input)
                          input)))
                 (apply concat))]
    (reduce
      (fn [acc input]
        (assoc acc (:id input) (dissoc input :id)))
      {} inputs)))

(defn map-superset?
  "Returns true if m1 is a superset of m2. Does not work recursively."
  [m1 m2]
  ;; Removes falsey values
  (let [m1 (u/filter-vals identity m1)
        m2 (u/filter-vals identity m2)]
    (= m2 (select-keys m1 (keys m2)))))

(defn query-all
  "Full query of every document in a solr connection. Walks results pagination."
  [conn]
  (->> (map (fn [batch-idx]
              (->> (solr/query conn "*:*" {:start (* 10 batch-idx)})
                (:response)
                (:docs))) (range))
    (take-while seq)
    (apply concat)
    (reduce (fn [acc doc]
              (assoc acc (:id doc) (dissoc doc :id))) {})))

(defn db-state-with-adds
  "Applies a series of adds to the solr connection, queries everything and
  clears the database state."
  [conn series-of-adds]
  (doseq [add series-of-adds]
    (solr/add conn add)
    (solr/commit conn))
  (let [results (query-all conn)]
    (solr/delete-by-query conn "*:*")
    (solr/commit conn)
    results))

(defspec added-docs-are-queried-consistently 25
  (prop/for-all [series-of-adds (gen/vector (s/gen ::solr-add-input))]
    (let [results  (db-state-with-adds conn series-of-adds)
          expected (reduce-by-id series-of-adds)]
      (every? (fn [[key val]]
                (map-superset? (get results key) val)) expected))))

;; TODO add generative test for partial updates and deletes

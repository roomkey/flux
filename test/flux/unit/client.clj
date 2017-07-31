(ns flux.unit.client
  (:require [flux.embedded :as e]
            [flux.client :as solr]
            [flux.update :as update]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]))

(def cc (e/create-core-container "test/resources/solr"))
(def conn (e/create cc "core1"))

(def not-empty-string? (s/and string?
                              #(not (clojure.string/blank? %))))

(s/def ::id not-empty-string?)
(s/def ::name not-empty-string?)
(s/def ::code not-empty-string?)
(s/def ::is_primary boolean?)

(s/def ::solr-document
  (s/keys
    :req-un [::id]
    :opt-un [::name ::code ::is_primary]))

(s/def ::solr-add-input
  (s/or :single-doc ::solr-document
        :doc-collection (s/coll-of ::solr-document
                          :min-count 1)))

(defn keep-last-by [id coll]
  (let [seen (atom #{})]
    (->> coll
      (reverse)
      (remove (fn [x]
                (let [key   (get x id)
                      seen? (contains? @seen key)]
                  (swap! seen conj key)
                  seen?)))
      (reverse))))

(defn reduce-by-id
  "Given a collection of solr inputs, returns a map from document id to ending
  document value."
  [input-sample]
  (let [inputs (->> input-sample
                 (map (fn [input]
                        (if (map? input)
                          (vector input)
                          input)))
                 ;; Documents with the same id are overridden until commits happen
                 (map #(keep-last-by :id %))
                 (apply concat))]
    (reduce
      (fn [acc input]
        (update acc (:id input) merge (dissoc input :id)))
      {} inputs)))

(defn map-superset?
  "Returns true if m1 is a superset of m2. Does not work recursively."
  [m1 m2]
  (= m2 (select-keys m1 (keys m2))))


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
                (map-subset? val (get results key))) expected))))

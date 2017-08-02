(ns flux.spec
  (:require [flux.update :as update]
            [flux.convert :as convert]
            [clojure.spec.alpha :as s]
            [medley.core :as u]))

(def not-empty-string? (s/and string?
                              #(not (clojure.string/blank? %))))

(s/def ::input-document-key (s/and (s/or :keyword keyword? :string string?)
                                   #(not (clojure.string/blank? (name (val %))))))

(s/def ::input-document-value
  (s/and any?
         #(not (set? %))))

(defn has-same-key-name?
  "Returns true whenever a map has two keys that will be stored the same way in Solr.
  For instance: {:a 1 \"a\" 2}"
  [m]
  (when (map? m)
    (let [string-keys (map update/stringify-key (keys m))]
      (if (seq string-keys)
        (not (apply distinct? string-keys))
        false))))

(s/def ::input-document
  (s/and (s/map-of ::input-document-key ::input-document-value)
         #(not (has-same-key-name? %))))

(defn convert-input-document [input]
  (->> input
    (u/map-keys keyword) ;; keys come out as keywords
    (u/map-vals (fn [v]
                  (cond
                    ;; Sequences passed in as values only retain the first item of the sequence
                    (sequential? v) (first v)
                    :default        v)))))

(s/fdef update/create-doc
  :args (s/cat :input-document ::input-document)
  :fn #(= (convert/->clojure (:ret %))
          (convert-input-document (-> % :args :input-document))))

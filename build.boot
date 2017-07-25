(defn deps->pom-deps [deps]
  (map (fn [[dep-name {:keys [version] :as opts}]]
         (let [opts-seq (apply concat (dissoc opts :version))]
           (concat [dep-name version] opts-seq))) deps))


(defn deps []
  (-> (slurp "deps.edn")
    (read-string)
    (:deps)
    (deps->pom-deps)))


(set-env!
  :source-paths #{"src"}
  :dependencies (deps)
  :wagons       '[[s3-wagon-private "1.2.0"]]
  :repositories #(conj %
                   ["releases" {:url        "s3://rk-maven/releases/"
                                :username   (System/getenv "AWS_ACCESS_KEY_ID")
                                :passphrase (System/getenv "AWS_SECRET_ACCESS_KEY")}]))


(task-options!
  pom {:project     'com.roomkey/flux
       :description "A clojure client library for Solr"
       :url         "https://github.com/roomkey/flux"
       :scm         {:url "https://github.com/roomkey/flux"}})


(require
  '[boot-v.core :as v :refer :all])


(deftask dev-env []
  (set-env!
    :resources #(conj % "dev-resources"))
  identity)


(deftask test-env []
  (set-env!
    :source-paths   #(conj % "test"))
  identity)



;; Release ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftask build []
  (merge-env! :resource-paths #{"src"})
  (comp
    (v/set-pom-version)
    (pom)
    (jar)
    (install)))


(deftask release
  [u update KEYWORD kw "Versioning parameter: :minor, :major, :patch..."]
  (comp
    (v/v :update update)
    (build)
    (push :repo "releases")))

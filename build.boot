(set-env!
 :source-paths #{"src"}
 :dependencies '[[seancorfield/boot-tools-deps "0.1.3" :scope "test"]]
 :wagons '[[s3-wagon-private "1.2.0"]]
 :repositories #(conj %
                  ["releases" {:url "s3://rk-maven/releases/"
                               :username (System/getenv "AWS_ACCESS_KEY_ID")
                               :passphrase (System/getenv "AWS_SECRET_ACCESS_KEY")}]))

(require '[boot-tools-deps.core :refer [deps]])

(deps)

(task-options!
 pom {:project 'com.roomkey/flux
      :description "A clojure client library for Solr"
      :url "https://github.com/roomkey/flux"
      :scm {:url "https://github.com/roomkey/flux"}})


(set-env!
 :dependencies '[[com.roomkey/boot-v "0.1.3" :scope "test"]
                 [metosin/boot-alt-test "0.3.2" :scope "test"]])

(require
 '[boot-v.core :as v :refer :all]
 '[metosin.boot-alt-test :refer [alt-test] :rename {alt-test run-tests}])


(deftask dev-env []
  (deps :resolve-aliases :dev :classpath-aliases :dev)
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

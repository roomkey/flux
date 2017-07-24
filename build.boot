(set-env!
  :source-paths #{"src"}
  :dependencies
  '[[org.clojure/clojure "1.9.0-alpha17" :scope "provided"]
    [org.apache.solr/solr-core "5.4.0" :exclusions [joda-time org.slf4j/slf4j-api]]
    [org.apache.solr/solr-solrj "5.4.0" :exclusions [org.slf4j/slf4j-api]]])


(deftask dev-repl []
  (set-env!
    :resources    #(conj % "dev-resources")
    :dependencies #(conj % '[org.slf4j/slf4j-log4j12 "1.7.22"]))
  (repl))

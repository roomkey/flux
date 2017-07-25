(defproject com.roomkey/flux :lein-v
  :description "A clojure client library for Solr"
  :url "https://github.com/roomkey/flux"
  :plugins [[com.roomkey/lein-v "6.0.2"]]
  :repositories {"rk-public" {:url "http://rk-maven-public.s3-website-us-east-1.amazonaws.com/releases/"}
                 "releases"  {:url "s3://rk-maven/releases/"}}
  :release-tasks [["vcs" "assert-committed"]
                  ["v" "update"] ;; compute new version & tag it
                  ["vcs" "push"]
                  ["deploy"]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.apache.solr/solr-core "5.4.0" :exclusions [joda-time org.slf4j/slf4j-api]]
                 [org.apache.solr/solr-solrj "5.4.0" :exclusions [org.slf4j/slf4j-api]]]
  :profiles {:dev {:dependencies [[midje "1.8.3"]
                                  [org.slf4j/slf4j-log4j12 "1.7.22"]]
                   :resource-paths ["dev-resources"]}})

(defproject com.roomkey/flux :lein-v
  :description "A clojure client library for Solr"
  :url "https://github.com/roomkey/flux"
  :plugins [[com.roomkey/lein-v "6.0.2"]]
  :repositories {"rk-public" {:url "http://rk-maven-public.s3-website-us-east-1.amazonaws.com/releases/"}}
  :release-tasks [["vcs" "assert-committed"]
                  ["v" "update"] ;; compute new version & tag it
                  ["vcs" "push"]
                  ["deploy"]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.apache.solr/solr-core "5.4.0"]
                 [org.apache.solr/solr-solrj "5.4.0"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]
                                  [org.slf4j/slf4j-log4j12 "1.7.7"]
                                  [commons-logging "1.1.3"]]
                   :resource-paths ["dev-resources"]
                   :plugins [[lein-midje "3.1.1"]]}})

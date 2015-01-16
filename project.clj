(defproject com.roomkey/flux :lein-v
  :description "A clojure client library for Solr"
  :url "https://github.com/roomkey/flux"
  :plugins [[lein-maven-s3-wagon "0.2.4"]
            [com.roomkey/lein-v "3.3.4"]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.apache.solr/solr-core "4.6.0"]
                 [org.apache.solr/solr-solrj "4.6.0"]
                 [javax.servlet/servlet-api "2.5"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]
                                  [org.slf4j/slf4j-log4j12 "1.7.7"]]
                   :plugins [[lein-midje "3.1.1"]]}}
  :repositories {"rk-public" {:url "http://rk-maven-public.s3-website-us-east-1.amazonaws.com/releases/"} "releases" {:url "s3://rk-maven/releases/"}})

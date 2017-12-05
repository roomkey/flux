(defn deps->pom-deps [deps]
  (map (fn [[dep-name {:keys [version] :as opts}]]
         (let [opts-seq (apply concat (dissoc opts :version))]
           (concat [dep-name version] opts-seq))) deps))

(defn deps []
  (-> (slurp "deps.edn")
      (read-string)
      (:deps)
      (deps->pom-deps)))

(defproject com.roomkey/flux :lein-v
  :description "A clojure client library for Solr"
  :url "https://github.com/roomkey/flux"
  :plugins [[com.roomkey/lein-v "6.0.2"]]
  :mirrors {#"http://maven.restlet.org" "https://maven.restlet.com"}
  :repositories {"rk-public" {:url "https://rk-maven-public.c0pt3r.com/releases/"}
                 "releases"  {:url "s3://rk-maven/releases/"}}
  :release-tasks [["vcs" "assert-committed"]
                  ["v" "update"] ;; compute new version & tag it
                  ["vcs" "push"]
                  ["deploy"]]
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies ~(deps)
  :profiles {:dev {:resource-paths ["dev-resources"]}})

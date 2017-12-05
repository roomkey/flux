(defn deps [& aliases]
  (let [deps-edn (read-string (slurp "deps.edn"))
        ->pom-deps (fn [[dep-name {:keys [mvn/version] :as opts}]]
                     (let [opts-seq (apply concat (dissoc opts :mvn/version))]
                       (concat [dep-name version] opts-seq)))]
    (map ->pom-deps (apply merge (cons (:deps deps-edn) (map #(get-in deps-edn [:aliases % :extra-deps]) aliases))))))

(defn paths [& aliases]
  (let [deps-edn (read-string (slurp "deps.edn"))]
    (apply concat (:paths deps-edn) (map #(get-in deps-edn [:aliases % :extra-paths]) aliases))))

(defproject com.roomkey/flux :lein-v
  :description "A clojure client library for Solr"
  :url "https://github.com/roomkey/flux"
  :plugins [[com.roomkey/lein-v "6.0.2"]]
  :repositories {"rk-public" {:url "https://rk-maven-public.c0pt3r.com/releases/"}
                 "releases" {:url "s3://rk-maven/releases/"}}
  :release-tasks [["vcs" "assert-committed"]
                  ["v" "update"] ;; compute new version & tag it
                  ["vcs" "push"]
                  ["deploy"]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies ~(deps)
  :profiles {:dev {:dependencies ~(deps :dev)
                   :resource-paths ~(paths :dev)}})

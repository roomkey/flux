(ns flux.embedded
  (:require [clojure.java.io :as io])
  (:import [java.io File]
           [org.apache.solr.client.solrj.embedded EmbeddedSolrServer]
           [org.apache.solr.core CoreContainer]
           [org.apache.solr.client.solrj.request CoreAdminRequest$Create CoreAdminRequest]
           [java.nio.file Paths]
           [java.net URI]))

(defn- str->path [str-path]
  (-> (File. str-path)
    (.toURI)
    (Paths/get)))

(defn create-core-container
  "Creates a CoreContainer from a solr-home path and solr-config.xml path
   OR just a solr-home path.
   If the latter is used, $home/$solr.xml works well, as the new
   core.properties discovery mode.
   See: org.apache.solr.core.CoreLocator
        and
        http://wiki.apache.org/solr/Core%20Discovery%20(4.4%20and%20beyond)"
  ([^String solr-home]
   (CoreContainer/createAndLoad
     (str->path solr-home)))
  ([^String solr-home-path ^String solr-config-path]
   (CoreContainer/createAndLoad
    (str->path solr-home-path)
    (str->path solr-config-path))))

(defn create [^CoreContainer core-container core-name]
  {:pre [(some #(% core-name) [string? keyword?])]}
  (EmbeddedSolrServer. core-container (name core-name)))

;; Dev cores ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn temp-solr-directory
  ([] (temp-solr-directory ""))
  ([dir-prefix]
   (let [path     (java.nio.file.Files/createTempDirectory dir-prefix
                    (into-array java.nio.file.attribute.FileAttribute []))
         path-str (.toString path)]
     (doto (io/file path-str "solr.xml")
       (spit "<solr></solr>"))

     (doto (io/file path-str "managed-schema")
       (spit "<schema name=\"dev\" version=\"1.6\">\n    <dynamicField name=\"*\" type=\"string\" indexed=\"true\" stored=\"true\"  multiValued=\"true\"/>\n    <copyField source=\"*\" dest=\"text\"/>\n\n    <fieldType name=\"string\" class=\"solr.StrField\"/>\n\n    <fieldType name=\"text_basic\" class=\"solr.TextField\">\n        <analyzer>\n            <tokenizer class=\"solr.LowerCaseTokenizerFactory\" />\n        </analyzer>\n    </fieldType>\n</schema>"))

     (doto (io/file path-str "solrconfig.xml")
       (spit "<config>\n    <luceneMatchVersion>6.2.0</luceneMatchVersion>\n    <requestHandler name=\"/select\" class=\"solr.SearchHandler\" >\n        <lst name=\"defaults\">\n            <str name=\"df\">text</str>\n        </lst>\n    </requestHandler>\n</config>"))

     path)))

(defn create-dev-core [core-name]
  (let [solr-dir (str (temp-solr-directory))
        cc       (create-core-container solr-dir)]
    (let [conn (create cc core-name)]
      (CoreAdminRequest/createCore core-name (str solr-dir) conn)
      {:core-container cc
       :core           (first (.getCores cc))
       :conn           conn})))

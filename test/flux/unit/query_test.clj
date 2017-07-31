(ns flux.unit.query-test
  (:require [flux.query :refer :all]
            [clojure.test :refer :all]))

(deftest create-query-test
  (create-query-request {:q "*:*"}))

(deftest create-query-with-path
  (let [path "/docs"
        qr   (create-query-request path {:q "*:*"})]
    (is (= (.getPath qr) path))))

(deftest create-query-with-method
  (let [method :post
        path   "/docs"
        qr     (create-query-request :post "/docs" {:q "*:*"})]
    ;; TODO test method lookup
    (is (= (.getPath qr) path))))

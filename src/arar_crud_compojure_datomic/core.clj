(ns arar-crud-compojure-datomic.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes GET POST DELETE PUT]]
            [compojure.route :refer [not-found]]
            [clojure.pprint :refer [pprint]]
            [datomic.client.api :as d])
  (:gen-class))

(def cfg {:server-type :datomic-local
          :system "db"})
(def client (d/client cfg))
(def db-name (-> (d/list-databases client {})
                 first))
(def conn (d/connect client {:db-name db-name}))

(defroutes routes
  (GET "/" [] "<h1>Halo</h1>\n")
  
  (GET "/person" []
       (let [db (d/db conn)
             v (d/q {:query '[:find ?e ?name ?email
                              :where
                              [?e :person/name ?name]
                              [?e :person/email ?email]]
                     :args [db]})]
         (str "<table>"
              "<thead><tr><td>eid</td><td>name</td><td>email</td></tr></thead>"
              "<tbody>"
              (mapv (fn [[eid name email]]
                      (str "<tr><td>" eid "</td>"
                           "<td>" name "</td>"
                           "<td>" email "</td></tr>"))
                    v)
              "</tbody>"
              "</table>")))
  
  (GET "/person/:email" [email]
       (let [db (d/db conn)
             m (d/pull db '[*] [:person/email email])]
         (str "<table>"
              "<thead><tr><td>eid</td><td>name</td><td>email</td></tr></thead>"
              "<tbody>"
              "<tr>"
              "<td>" (:db/id m) "</td>"
              "<td>" (:person/name m) "</td>"
              "<td>" (:person/email m) "</td>"
              "</tr>"
              "</tbody>"
              "</table>")))
  
  (POST "/person" [name email]
        (if (contains?
             (d/transact conn {:tx-data [{:person/name name
                                          :person/email email}]})
             :db-after)
          "<h1>Create/Assert berhasil!</h1>\n"
          "<h1>Gagal!</h1>\n"))

  (DELETE "/person/:email" [email]
          (if (contains?
               (d/transact conn {:tx-data [[:db/retractEntity [:person/email email]]
                                           [:db/add "datomic.tx" :db/doc "wrong assertion"]]})
               :db-after)
            "<h1>Delete/Retract berhasil!</h1>\n"
            "<h1>Gagal!</h1>\n"))

  (PUT "/person/:email" [email name]
       (if (contains?
            (d/transact conn {:tx-data [[:db/add [:person/email email] :person/name name]
                                        [:db/add "datomic.tx" :db/doc "correct data"]]})
            :db-after)
         "<h1>Accumulate/Update berhasil!</h1>\n"
         "<h1>Gagal!</h1>\n"))
  
  (not-found "<h1>Page not found</h1>\n"))

(def handler
  (-> routes
      wrap-params))

(defn -main
  [& [port]]
  (let [port (or port (get (System/getenv) "PORT" 3000))
        port (cond-> port (string? port) Integer/parseInt)]
    (run-jetty handler {:port port
                        :join? false})))

(ns arar-crud-compojure-datomic.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes GET POST DELETE PUT]]
            [compojure.route :refer [not-found]]
            [hiccup2.core :refer [html]]
            [datomic.client.api :as d])
  (:gen-class))

(def cfg {:server-type :datomic-local
          :system "db"})
(def client (d/client cfg))
(def db-name (-> (d/list-databases client {})
                 first))
(def conn (d/connect client {:db-name db-name}))

(defroutes routes
  (GET "/" [] (str (html [:h1 "Halo"])))
  
  (GET "/person" []
       (let [db (d/db conn)
             v (d/q {:query '[:find ?e ?name ?email
                              :where
                              [?e :person/name ?name]
                              [?e :person/email ?email]]
                     :args [db]})]
         (str (html [:table {:style "border: 1px solid red"}
                       [:thead
                        [:tr
                         [:td "eid"]
                         [:td "name"]
                         [:td "email"]]]
                       [:tbody (for [x v]
                                 [:tr
                                  (for [xx x]
                                    [:td xx])])]]))))
  
  (GET "/person/:email" [email]
       (let [db (d/db conn)
             m (d/pull db '[*] [:person/email email])]
         (str (html [:table
                       [:thead
                        [:tr
                         [:td "eid"]
                         [:td "name"]
                         [:td "email"]]]
                       [:tbody
                        [:tr
                         [:td (:db/id m)]
                         [:td (:person/name m)]
                         [:td (:person/email m)]]]]))))
  
  (POST "/person" [name email]
        (if (contains?
             (d/transact conn {:tx-data [{:person/name name
                                          :person/email email}]})
             :db-after)
          (str (html [:h1 "Create/Assert berhasil!"]))
          (str (html [:h1 "Gagal!"]))))

  (DELETE "/person/:email" [email]
          (if (contains?
               (d/transact conn {:tx-data [[:db/retractEntity [:person/email email]]
                                           [:db/add "datomic.tx" :db/doc "wrong assertion"]]})
               :db-after)
            (str (html [:h1 "Delete/Retract berhasil!"]))
            (str (html [:h1 "Gagal!"]))))

  (PUT "/person/:email" [email name]
       (if (contains?
            (d/transact conn {:tx-data [[:db/add [:person/email email] :person/name name]
                                        [:db/add "datomic.tx" :db/doc "correct data"]]})
            :db-after)
         (str (html [:h1 "Accumulate/Update berhasil!"]))
         (str (html [:h1 "Gagal!"]))))
  
  (not-found (str (html [:h1 "Page not found!"]))))

(def handler
  (-> routes
      wrap-params))

(defn -main
  [& [port]]
  (let [port (or port (get (System/getenv) "PORT" 3000))
        port (cond-> port (string? port) Integer/parseInt)]
    (run-jetty handler {:port port
                        :join? false})))

(ns focus.core
  (:require [clj-ulid :as ulid]
            [datahike.api :as db])
  (:import (java.time LocalDateTime))
  (:gen-class))

(def ^:private counter (atom 0))
(defn next-id [] (swap! counter inc))
(def uri "datahike:mem://test")

(defn now [] (str (LocalDateTime/now)))

(def task
  {:title "My task"
   :events [[:created "2019-01-31"]]})

(defn is-started? [task]
  (boolean (some (comp #{:started} first) (:events task))))

(defn is-closed? [task]
  (-> task
      :events
      last
      first
      #{:started :created}
      not))

(defn status [st & extra]
  [st (now) (apply hash-map extra)])

(defn new-task [title & extra]
  {:db/id (next-id)
   :focus/id (ulid/ulid)
   :focus/title title
   :focus/events [(status :created)]
   :focus/metadata (apply hash-map extra)})

(defn change-status [task st & extra]
  (update task :events conj (apply status st extra)))

(defn start [task] (change-status task :started))
(defn finish [task] (change-status task :finished))
(defn wont-fix [task] (change-status task :aborted :reason :wont-fix))

(defn -main [& args]
  (db/create-database uri)
  (let [conn (db/connect uri)
        title (first args)]

    @(db/transact conn [(new-task title)])

    (println (db/q '[:find ?id ?events
                     :in $ ?title
                     :where [$ ?e :focus/title ?title]
                            [$ ?e :focus/id ?id]
                            [$ ?e :focus/events ?events]] @conn title))

    (db/delete-database uri)))

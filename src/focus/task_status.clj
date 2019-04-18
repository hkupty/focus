(ns focus.task-status
  (:require [clj-ulid :as ulid]
            [clojure.pprint :as pp]
            )
  (:import (java.time LocalDateTime)
           (java.util UUID)))

(def ^:private counter (atom 0))
(defn next-id [] (swap! counter inc))
(defn now [] (str (LocalDateTime/now)))

(+ 1 1)


(defn is-started? [task]
  (boolean (some (comp #{:started} first) (:events task))))

(defn is-closed?
  "Checks if task is closed"
  [task]
  (-> task
      :events
      last
      first
      #{:started :created}
      not))

(defn status
  "Return the task with a new status"
  [st & extra]
  [st (now) (apply hash-map extra)])

(defn change-status
  "Changes the status of a task"
  ([task st & extra] (update task :events conj (apply status st extra))))

(defn start [task] (change-status task :started))
(defn finish [task] (change-status task :finished))
(defn wont-fix [task] (change-status task :aborted :reason :wont-fix))


(defn new-task [title & extra]
  {:db/id (next-id)
   :focus/id (ulid/ulid)
   :focus/title title
   :focus/events [(status :created)]
   :focus/metadata (apply hash-map extra)})

(pp/pprint (new-task ::nt))

(println "asdf\nqwer\nzxcv")

(ns focus.core
  (:import (java.time LocalDateTime))
  (:gen-class))

(defn now [] (str (LocalDateTime/now)))

(def task
  {:title "My task"
   :events [[:start "2019-01-31"]]})

(defn status
  ([st & extra] [st (now) (apply hash-map extra)])
  ([st] [st (now)]))

(defn new-task [title] {:title title :events [(status :created)]})

(defn change-status
  ([task st] (update task :events conj (status st)))
  ([task st & extra] (update task :events conj (apply status st extra)))
  )

(defn start [task] (change-status task :started))
(defn finish [task] (change-status task :finished))
(defn wont-fix [task] (change-status task :aborted :reason :wont-fix))


(defn -main [& args]
  (-> args
      (first)
      (new-task)
      (start)
      (wont-fix)
      (println)))

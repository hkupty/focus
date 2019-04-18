(ns focus.core
  (:require [focus.task-status :as ts])
  (:gen-class))

(def uri "datahike:mem://test")

(def task (ts/new-task "My task"))

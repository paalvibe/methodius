(ns methodius.mem
  (:require [methodius.out :refer [info]]
            [db.db :as db]))

(defn resetmem! [options]
  (info "Resetting db")
  (db/reset-db)
  (info "Done..."))

(defn growmem! [options]
  (info "Resetting db")
  (info "TODO")
  (info "Done..."))

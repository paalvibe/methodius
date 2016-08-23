(ns db.db
  (:require [datomic.api :as d]))

; (def uri "datomic:mem://methodius")
; (def uri "datomic:dev://localhost:8001/methodius")
(def uri "datomic:dev://localhost:4334/methodius")

(defn create []
  (d/create-database uri))

(defn getconn []
    (d/connect uri))

(defn- transact-data [conn data]
  (d/transact conn data))

(defn do-files [path f]
  (let [file (java.io.File. path)
        files (.listFiles file)]
  (doseq [x files]
    (when (.isFile x)
      (let [data (read-string (slurp (.getCanonicalPath x)))]
        (println x)
        (println data)
        (f data))))))

(defn- base-setup []
  (let [conn (getconn)
        transact (partial transact-data conn)]
    (do-files "db/schema/" transact)
    (do-files "db/fixtures/" transact)))


(defn reset-db []
  (d/delete-database uri)
  (create)
  (base-setup))


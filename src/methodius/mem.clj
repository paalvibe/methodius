(ns methodius.mem
  (:require [methodius.out :refer [info]]
            [db.db :as db]
            [clojure.string :as string]))

(defn resetmem! [options]
  (info "Resetting db")
  (db/reset-db)
  (info "Done..."))

(defn dbgval [val]
  "TODO, make this a macro"
  (if false
    (info (str val)))
  val)

(defn process-rdr
  "Process reader reading it line-by-line"
  ([rdr]
     (process-rdr rdr identity))
  ([rdr process-fn]
     (process-rdr rdr process-fn println))
  ([rdr process-fn output-fn]
     (process-rdr rdr process-fn
                  (fn [line] line))) ; return nil if you want break
  ([rdr process-fn output-fn next-fn]
     (dbgval (loop [ret []]
               (let [line (next-fn (.readLine rdr))]
                 (if (not line) ret
                     (recur (conj ret (output-fn (process-fn line))))))))))

(defn- get-frame [rdr]
  (process-rdr
   rdr
   (fn [line] line)
   (fn [line] line)
   (fn [line] (if (string/blank? line) nil line))
))

(defn multi-nth [values indices]
  "force collection of every entry before prevent closing of stream (or reader).
Example call: (multi-nth (repeatedly #(get-frame rdr)) #{2 3})"
  (doall (map (partial nth values) indices)))

(defn- frames
  "Lazy frame reader"
  ([rdr n]
     (multi-nth (repeatedly #(get-frame rdr)) (range n)))
  ([rdr beg end]
     (multi-nth (repeatedly #(get-frame rdr)) (range beg end))))

(defn- show-pair [a-srt b-srt]
  (info (str a-srt " vs " b-srt))
  (with-open [a-rdr (clojure.java.io/reader a-srt)
              b-rdr (clojure.java.io/reader b-srt)]
    (dotimes [n 5]
      (info (str (get-frame a-rdr) "\n\t\t" (get-frame b-rdr))))))

(defn growmem! [options]
  (info "Grow mem")
  (info "TODO")
  (info "Done..."))

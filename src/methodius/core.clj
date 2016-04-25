(ns methodius.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [methodius.mem :as mem]
            [methodius.translate :as translate])
  (:gen-class))

; Command-line client for entire suite of functions

(def cli-options
  [;; First three strings describe a short-option, long-option with optional
   ;; example argument description, and a description. All three are optional
   ;; and positional.
   ["-p" "--port PORT" "Port number"
    :default 80
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ;; If no required argument description is given, the option is assumed to
   ;; be a boolean option defaulting to nil
   [nil "--detach" "Detach from controlling process"]
   ["-v" nil "Verbosity level; may be specified multiple times to increase value"
    ;; If no long-option is specified, an option :id must be given
    :id :verbosity
    :default 0
    ;; Use assoc-fn to create non-idempotent options
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Tool for translating srt's based on a trainable memory."
        ""
        "Usage: program-name [options] action"
        ""
        "Options:"
        options-summary
        ""
        "Actions:"
        "  resetmem Set up new mem"
        "  growmem Add data to mem"
        "  translate Print a server's status"
        ""
        "Please refer to the manual page for more information."]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    ;; Handle help and error conditions
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
    ;; Execute program with options
    (case (first arguments)
      "resetmem" (mem/resetmem! options)
      "growmem" (mem/growmem! options)
      "translate" (translate/translate! options)
      (exit 1 (usage summary)))))

                                        ; TODO
; todo,ensure time matches roughly for text string
                                        ; compose text string split across lines before uncomposing afterwards
                                        ; send missing strings to google translate
                                        ; apply common replacements (see war room movie scripts)
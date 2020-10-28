(ns lein-inferv.plugin
  (:require
   [leiningen.core.main]
   [clojure.string :refer [blank? trim-newline]]
   [clojure.java.shell :refer [sh]]))

;; must be computed once in order to ensure a stable timestamp
(def now-utc
  (.atZone
   (if (not (blank? (System/getenv "INFERV_TIMESTAMP_MS")))
             ;; obtain timestamp from env
     (-> (System/getenv "INFERV_TIMESTAMP_MS")
         read-string
         java.time.Instant/ofEpochMilli)
             ;; or grab current
     (java.time.Instant/now))
   java.time.ZoneOffset/UTC))

(defn has-git? [] (zero? (:exit (sh "git" "version"))))

(defn has-commit? [] (zero? (:exit (sh "git" "log"))))

(defn short-ref []
  (->> (sh "git" "rev-parse" "--short" "HEAD")
       :out
       trim-newline))

(def release-tasks
  ;; release is purely derived from git sha and timestamp 😑,
  ;; so there's no need to commit, bump, or tag anything
  {:release-tasks [["vcs" "assert-committed"]
                   ["deploy"]]})

(defn format-java-instant
  [instant]
  (.format
   (java.time.format.DateTimeFormatter/ofPattern
    "yyyyMMdd.HHmmss")
   instant))

(defn middleware
  "Leiningen middleware that:

   - takes a project map
   - replaces `version
   - returns a project map
   "
  [project]
  (cond
    (not (has-git?))
    (do
      (leiningen.core.main/warn
       "Skipping version inferrence: lein-inferv requires git, but git was not found. Skipping version inferrence.")
      project)

    (not has-commit?)
    (do
      (leiningen.core.main/warn
       "Skipping version inferrence: lein-inferv requires there to be at least 1 commit but this repo has none")
      project)

    :else (let [datetime (format-java-instant now-utc)]
            (merge project
                   release-tasks
                   {:version (format "%s.%s" datetime (short-ref))}))))


(comment


  ;; here's an example of taking a milliseconds string, parsing into a
  ;; java.time.Instant and formatting
  (format-java-instant
   (->
    "1603926430488"
    read-string
    java.time.Instant/ofEpochMilli
    (.atZone java.time.ZoneOffset/UTC))))

(ns lein-inferv.plugin
  (:require
   [leiningen.core.main]
   [clojure.string :refer [trim-newline]]
   [clojure.java.shell :refer [sh]]))

;; must be computed once in order to ensure a stable timestamp
(def now (java.time.Instant/now))

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

    :else (let [instant (.atZone now java.time.ZoneOffset/UTC)
                datetime (.format
                          (java.time.format.DateTimeFormatter/ofPattern
                           "yyyyMMdd.HHmmss")
                          instant)]
            (merge project
                   release-tasks
                   {:version (format "%s.%s" datetime (short-ref))}))))

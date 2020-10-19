(ns lein-inferv.plugin
  (:require
   [leiningen.core.main]
   [clojure.string :refer [trim-newline]]
   [clojure.java.shell :refer [sh]]))

(defn has-git? [] (zero? (:exit (sh "git" "version"))))

(defn has-commit? [] (zero? (:exit (sh "git" "log"))))

(defn short-ref []
  (->> (sh "git" "rev-parse" "--short" "HEAD")
       :out
       trim-newline))

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

    :else (let [instant (.atZone (java.time.Instant/now) java.time.ZoneOffset/UTC)
                datetime (.format
                          (java.time.format.DateTimeFormatter/ofPattern
                           "yyyyMMdd.HHmmss")
                          instant)]
            (assoc project :version (format "%s.%s" datetime (short-ref))))))

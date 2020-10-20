(defproject lein-inferv "_"
  :description "Opinionated inferred versions as a lein plugin"
  :url "https://github.com/devth/lein-inferv"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :plugins [[lein-inferv "20201020.013703.64afdf2"]]
  :deploy-repositories [["releases"  {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]]
  :eval-in-leiningen true)

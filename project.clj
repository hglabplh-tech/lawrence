(defproject de.active-group/lawrence "0.12.0-SNAPSHOT"
  :description "Lawrence: LR parser generator"
  :url "https://github.com/active-group/lawrence"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [de.active-group/active-clojure "0.42.2"]
                 [de.active-group/ephemerol "0.6.0-SNAPSHOT"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.trace "0.7.9"]]}}
  )


(ns clojure-web.jobs
  (:require [clojurewerkz.quartzite
             [jobs :as j :refer [defjob]]
             [scheduler :as qs]
             [triggers :as t]]
            [clojurewerkz.quartzite.schedule.cron :refer [cron-schedule schedule]]
            [taoensso.timbre :as log]))

(defjob NoOpJob
  [ctx]
  (log/info "Does nothing"))


(defjob CleanJob
  [ctx]
  (log/info "Cleaning..."))

(defn start []
  (let [tasks [{:job-key :jobs.noop
                :task NoOpJob
                :cron "0/5 * * * * ? *"}
               {:job-key :jobs.clean
                :task CleanJob
                :cron "0/10 * * * * ? *"}]
        s (-> (qs/initialize) qs/start)]
    (->> tasks
         (map-indexed (fn [idx {:keys [job-key task cron]}]
                        (let [job (j/build
                                   (j/of-type task)
                                   (j/with-identity (j/key (str "keys." job-key))))

                              trigger (t/build
                                       (t/with-identity (t/key (str "triggers." job-key)))
                                       (t/start-now)
                                       (t/with-schedule (schedule
                                                         (cron-schedule cron))))]
                          (qs/schedule s job trigger))))
             (doall))))


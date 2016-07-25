;
; Copyright 2016 OrgSync.
;
; Licensed under the Apache License, Version 2.0 (the "License")
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
;   http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
;

(ns oskr-expander.core
  (:require
    [com.stuartsierra.component :as component]
    [oskr-expander.producer :as producer]
    [oskr-expander.consumer :as consumer]
    [oskr-expander.process-manager :as process-manager]
    [environ.core :refer [env]]))

(defn create-system []
  (let [kafka-bootstrap (env "KAFKA_BOOTSTRAP" "kafka.service.consul:9092")]
    (-> (component/system-map
          :producer (producer/new-producer
                      kafka-bootstrap
                      (env "KAFKA_PART_TOPIC" "MessageParts")
                      :id)
          :process-manager (process-manager/new-process-manager)
          :consumer (consumer/new-consumer
                      kafka-bootstrap
                      (env "KAFKA_GROUP_ID" "oskr-expander")
                      (env "KAFKA_SPEC_TOPIC" "Communications")))

        (component/system-using
          {:process-manager [:consumer :producer]}))))


(comment
  (def s (create-system)))

(comment
  (alter-var-root #'s component/start))

(comment
  (alter-var-root #'s component/stop))
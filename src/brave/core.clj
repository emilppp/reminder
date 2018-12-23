(ns brave.core
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.string :as str]))


(defn get-key
  "Get the key located in ./key/key.txt"
  []
  (clojure.string/trim (slurp "key/key.txt")))

(defn api-root
  "Get the api-root url"
  []
  (str "http://ws.audioscrobbler.com/2.0/"))

(defn build-url
  "Get the url to call"
  [user key-val]
  (str (api-root) "?method=user.getrecenttracks&user=" user "&api_key=" key-val "&format=json"))

(defn fetch-data
  "Get the data"
  []
  (client/get (build-url "emilperssonhej" (get-key))))

(defn jsonify
  []
  (json/read-str (:body (fetch-data))))

(defn extract-data
  "Extract n amount of the relevant data"
  [n]
  (zipmap (range n) (take n (second (first (second (first (seq (jsonify)))))))))

(defn get-relevant-data
  "Artist name album"
  [n]
  (map #(select-keys % ["artist" "name" "album"]) (vals (extract-data n))))

(defn get-artist
  [data]
  (second (first (first (vals (select-keys data ["artist"]))))))

(defn get-album
  [data]
  (second (first (first (vals (select-keys data ["album"]))))))

(defn get-song
  [data]
  (second (first (select-keys data ["name"]))))

(defn print-result
  [in]
  (let [data (first in)]
    (println (count in))
  (if (empty? in)
    (println "Done.")
    (do
      (println  (get-artist data) " - " (get-song data) " - " (get-album data))
      (print-result (rest in))))))

(defn -main []
  (print-result (get-relevant-data 5)))

(-main)

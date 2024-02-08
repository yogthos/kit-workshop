(ns io.github.kit.gif2html.test-utils
  (:require
    [io.github.kit.gif2html.core :as core]
    [integrant.repl.state :as state]
    [migratus.core :as migratus]
    [next.jdbc :as jdbc]
    [clojure.test :refer [join-fixtures]]))

(defn system-state
  []
  (or @core/system state/system))

(defn clear-db-and-rerun-migrations
  []
  (jdbc/execute! (:db.sql/connection (system-state))
                 ["do
$$
    declare
        row record;
    begin
        for row in select * from pg_tables where schemaname = 'public'
            loop
                execute 'drop table public.' || quote_ident(row.tablename) || ' cascade';
            end loop;
    end;
$$;"])
  (migratus/migrate (:db.sql/migrations (system-state))))

(defn db-fixture [f]
  (clear-db-and-rerun-migrations)
  (f))

(defn system-fixture [f]
  (when (nil? (system-state))
    (core/start-app {:opts {:profile :test}}))
  (f))

(def test-fixtures (join-fixtures
                    [system-fixture
                     db-fixture]))
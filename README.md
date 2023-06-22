# Kit Workshop

## Table of Contents

* [Prerequisites](#prerequisites)
  * [Setup](#setup)
  * [Intro to Clojure](#intro-to-clojure)
  * [Conduct During the Workshop](#conduct-during-the-workshop)
* [Kit Workshop](#kit-workshop-1)
  * [Creating a Project](#creating-a-project)
  * [kit.edn](#kitedn)
  * [Starting the REPL](#starting-the-repl)
  * [Using Modules](#using-modules)
  * [CHECKPOINT 1](#checkpoint-1)
  * [What are Modules](#what-are-modules)
  * [Adding a Database](#adding-a-database)
  * [CHECKPOINT 2](#checkpoint-2)
  * [Managing the Database](#managing-the-database)
  * [Querying the Database](#querying-the-database)
  * [CHECKPOINT 3](#checkpoint-3)

## Prerequisites

macOS or Linux recommended.

### Setup

Make sure you have the following dependencies installed:

- Clojure CLI https://clojure.org/guides/install_clojure
- Java 11+ (17 recommended)
- Calva (+ clj-kondo extension), or editor of preference as long as nREPL compatible (Cursive, Emacs, etc.)
- Docker and docker compose. Check that you can run `docker compose up` on a simple `docker-compose.yml` file https://docs.docker.com/compose/gettingstarted/

Clone this repository and make sure you can run the project by doing the following

```bash
git clone git@github.com:nikolap/kit-workshop.git
cd kit-workshop
clj -M:dev:nrepl
```

This should start up a REPL prompt, it might output something like:

```
nREPL server started on port 50354 on host localhost - nrepl://localhost:50354
nREPL 0.9.0
Clojure 1.11.1
OpenJDK 64-Bit Server VM 17.0.1+12-LTS
Interrupt: Control+C
Exit:      Control+D or (exit) or (quit)
```

Inside the prompt, test that you can start the application server by running:

```clojure
(go)
```

Then go to [http://localhost:3000/api/health](http://localhost:3000/api/health) to check that the server is running.

### Intro to Clojure

This workshop assumes a basic familiarity with Clojure and functional programming. If you are new to Clojure, we can recommend the following resources to get started:

- High level overview https://yogthos.github.io/ClojureDistilled.html
- An Animated Introduction to Clojure https://markm208.github.io/cljbook/
- Clojure from the Ground Up https://aphyr.com/tags/Clojure-from-the-ground-up
- Clojure for the Brave and True https://www.braveclojure.com/foreword/

Of course you are also welcome to search around and find other resources suited to your learning style or interests.

We will **not** be going over the basics of Clojure during the workshop.

### Conduct During the Workshop

Some general guidelines during the workshop

- By attentding this workshop, you agree to the [Code of Conduct](/CONDUCT.md)
- If you get stuck, questions are encouraged
- Checkpoints and branches are available to make sure everyone's on the same page

## Kit Workshop

### Creating a Project

Kit uses [clj-new](https://github.com/seancorfield/clj-new) to create projects from the template. If you don't already have it installed on your local machine, you can pull it in by running

```bash
clojure -Ttools install com.github.seancorfield/clj-new '{:git/tag "v1.2.381"}' :as new
```

Now we can create our new project by running. Feel free to replace `yourname` with your name too!

```bash
clojure -Tnew create :template io.github.kit-clj :name yourname/gif2html
cd gif2html
```

You should now have a project with the following folders

```
├── env
│   ├── dev
│   │   ├── clj
│   │   │   └── yourname
│   │   │       └── gif2html
│   │   └── resources
│   ├── prod
│   │   ├── clj
│   │   │   └── yourname
│   │   │       └── gif2html
│   │   └── resources
│   └── test
│       └── resources
├── resources
├── src
│   └── clj
│       └── yourname
│           └── gif2html
│               └── web
│                   ├── controllers
│                   ├── middleware
│                   └── routes
└── test
    └── clj
        └── yourname
            └── gif2html
```

Let's take a look at what these folders are and their purpose.

* `env` - This folder contains environment dependent code.
  * `dev` - The code in this folder will only be run during development.
  * `prod` - The code in this folder will be compiled into the uberjar when the application is packaged for deployment. 
* `resources` - This folder contains static assets such as configuration files, HTML templates, and so on.
* `src/clj` - This folder contains the application code.
  * `controllers` - This package contains namespaces that handle your application business logic.
  * `middleware` - This package contains Ring routing middleware that encapsulates cross-cutting logic shared across the routes.
  * `routes` - This package is where server endpoints are defined.
* `test` - This folder contains the tests.

### kit.edn

Kit uses a module system that allows adding new functionality to existing Kit projects by installing modules from the REPL.
This file contains metadata about the project and referenes to module repositories that will be used to add new modules in the project.

Kit modules are templates that get injected in the project and generate code within exisitng project files. The metadata in `kit.edn` is
used to specify the paths and namespaces for the generated code.

### Starting the REPL

The REPL can be started by running the following command from the project folder:

    clj -M:dev:nrepl

Once the REPL starts you should see the following in the terminal, note that the PORT is selected at random:

```
nREPL server started on port 65110 on host localhost - nrepl://localhost:65110
nREPL 0.9.0
Clojure 1.11.1
OpenJDK 64-Bit Server VM 17.0.1+12-39
Interrupt: Control+C
Exit:      Control+D or (exit) or (quit)
user=>
```

Once you see the prompt, you can connect your editor to the REPL. We'll go through connecting Calva, but other editors should work similarly.

* Click on the `REPL` button at the bottom left.
* Select `Connect to a running REPL in your project`
* Select `deps.edn`
* Press `enter`, correct port should be detected automatically.

If everything went well then you should see the following prompt:

```clojure
; Connecting ...
; Hooking up nREPL sessions...
; Connected session: clj
; TIPS:
;   - You can edit the contents here. Use it as a REPL if you like.
;   - `alt+enter` evaluates the current top level form.
;   - `ctrl+enter` evaluates the current form.
;   - `alt+up` and `alt+down` traverse up and down the REPL command history
;      when the cursor is after the last contents at the prompt
;   - Clojure lines in stack traces are peekable and clickable.
clj꞉user꞉> 
```

### Using Modules

We'll need to pull the modules from the remote repository. This is accomplished by running the following commmand in the REPL:

```clojure
clj꞉user꞉> (kit/sync-modules)
:done
```
If the command ran successfully then you should see a new `modules` folder in the project containing the modules that were downloaded and are now available for use.
Let's list the available modules:

```clojure
clj꞉user꞉> (kit/list-modules)
:kit/html - adds support for HTML templating using Selmer
:kit/htmx - adds support for HTMX using hiccup
:kit/ctmx - adds support for HTMX using CTMX
:kit/metrics - adds support for metrics using prometheus through iapetos
:kit/sente - adds support for Sente websockets to cljs
:kit/sql - adds support for SQL. Available profiles [ :postgres :sqlite ]. Default profile :sqlite
:kit/cljs - adds support for cljs using shadow-cljs
:kit/nrepl - adds support for nREPL
:done
```

Finallly, let's try starting the server to make sure our application is working.

```clojure
clj꞉user꞉> (go)
:initiated
```
Let's navigate to `http://localhost:3000/api/health` and see if we have some health check information returned by the server:

```javascript
{"time":"Fri Feb 10 13:54:36 EST 2023","up-since":"Wed Jan 18 22:53:21 EST 2023","app":{"status":"up","message":""}}
```

### CHECKPOINT 1

At this point you should have your project setup, are able to run and connect to the REPL, and run the web server successfully.

[Click here to continue on to Checkpoint 2](https://github.com/nikolap/kit-workshop/tree/checkpoint-2)

### What are Modules

By now we've synced modules, but what are they? Kit modules consist of templates that can be used to inject code and resources into a Kit project.

By default, we have the public Kit modules repository linked under the `:modules` key of your `kit.edn` configuration of your project.

```clojure
{:root         "modules"
 :repositories [{:url  "https://github.com/kit-clj/modules.git"
                 :tag  "master"
                 :name "kit-modules"}]}
```

This configuration says to pull Kit modules template from the repository `https://github.com/kit-clj/modules.git` on the branch `master`.

This means that it is possible to extend this configuration with private or public modules that you write yourself. We won't be covering this during the workshop, but feel free to give it a try afterwards.

Now, let's use Modules to connect to a database.

### Adding a Database

Let's remind ourselves what modules we can install via `(kit/list-modules)`.

You'll notice there is `:kit/sql` module with various **profiles**. Some modules have features that can be chosen from when installing. To use a non-default feature simply specify the feature flag in an options map as a second argument to `kit/install-module`. For example, `{:feature-flag :postgres}`.

Now let's do this to set up PostgreSQL with our project:

    (kit/install-module :kit/sql {:feature-flag :postgres})

You should see something like this in your REPL output

```clojure
clj꞉user꞉> (kit/install-module :kit/sql {:feature-flag :postgres})
:kit/sql requires following modules: nil
applying features to config: [:base]
updating file: resources/system.edn
updating file: deps.edn
updating file: src/clj/io/github/kit/gif2html/core.clj
applying
 action: :append-requires 
 value: ["[kit.edge.db.sql.conman]" "[kit.edge.db.sql.migratus]"]
:kit/sql installed successfully!
restart required!
:done
```

Let's quickly add a `docker-compose.yml`. You can copy this over in to the root of the project:

```yml
version: '3.9'

services:
  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_PASSWORD: gif2html
      POSTGRES_USER: gif2html
      POSTGRES_DB: gif2html
    ports:
      - "5432:5432"
    volumes:
      - db_data:/var/lib/postgresql/data

volumes:
  db_data:
```

We can start this up by running

    docker compose up -d

This will start the services defined in our `docker-compose.yml` file in detached mode. For more about docker, you can read up on it [here](https://docs.docker.com/compose/).

Let's take a look at some of the code that was generated by installing the SQL module.

Firstly, let's see the dependencies added to our `deps.edn`.

```
io.github.kit-clj/kit-sql-conman    Conman is a connection pooling library and a utility for setting up HugSQL
io.github.kit-clj/kit-sql-migratus  Migratus is a library for managing DB migrations
org.postgresql/postgresql           This is the JDBC PostgreSQL connector
```

These dependencies were linked in our `src/clj/io/github/kit/gif2html/core.clj` as `requires` in the namespace.

We now also have two new directories in our `resources` folder: `migrations` and `sql`. Under `migrations` you will be able to create `.sql` up and down migrations for Migratus to apply to your database schema. And under `sql` there is an empty `queries.sql` file created. Here we will write [HugSQL](https://www.hugsql.org/) queries.

Our `system.edn` was changed to define the Database we'll be using. Here we have three new integrant components defined:

- `:db.sql/connection`: This is the pooled DB connection to our PostgreSQL database
- `:db.sql/query-fn`: The HugSQL queries defined in your `resources/sql/queries.sql` can be used with this function
- `:db.sql/migrations`: The configuration for Migratus.

Lastly, before we try running our system again, let's change our `:db.sql/connection` connection string in `system.edn`.

Here we have three different profiles. For sake of simplicity, we can use the same DB connection for `test` and `dev`.

Let's drop the `:test` and `:dev` profile, and instead use a `:default` profile with the following value.

    :default {:jdbc-url "jdbc:postgresql://localhost:5432/gif2html?user=gif2html&password=gif2html"}

Now let's run

    (reset)

And we should be connected to our database. Let's check that by running

```clojure
(require '[next.jdbc :as jdbc])
(jdbc/execute! 
  (:db.sql/connection state/system)
  ["select column_name from information_schema.columns where table_name = 'schema_migrations';"])
```

It should return the schema of our migrations table generated by Migratus.

Don't worry, these commands will be explained shortly.

### CHECKPOINT 2

At this point you should have your database up and running, and your server should be able to connect to it.

[Click here to continue on to Checkpoint 3](https://github.com/nikolap/kit-workshop/tree/checkpoint-3)

### Managing the Database

Database migrations are a way to manage changes to a database schema while preserving existing data. They are useful because they allow developers to evolve the database schema over time, track and test changes, and collaborate more effectively.

We can create and execute migrations thanks to [Migratus](https://github.com/yogthos/migratus). Let's create our first migration in the REPL!

```clojure
(migratus.core/create
  (:db.sql/migrations state/system)
  "create-gif-tables")
```

Here Migratus created for us two files, `20230218160207-create-gif-tables.up.sql` and `20230218160207-create-gif-tables.down.sql`.

The name is identical except for the suffix at the end, `.up.sql` or `.down.sql`. This allows us to express and **up** migration and a **down** migration. The benefit here is if ever you need to revert a migration you can specify the steps to do so.

Let's write our first migration now. What are some database columns you think might be needed for this service?

Here's the one we came up with

```sql
create table if not exists gifs
(
    id         serial primary key,
    link       text                      not null,
    name       text                      not null,
    created_at timestamptz default now() not null
);
```

Let's also write a down migration. To revert, we'll simply drop the table if it exists

```sql
drop table if exists gifs;
```

Now that we have our SQL migrations written out, let's try to execute them with Migratus in our REPL.

    (migratus.core/migrate (:db.sql/migrations state/system))

Now if we try that command from before to get our columns from the new `gifs` table, we should see this:

```clojure
clj꞉user꞉>(jdbc/execute!
  (:db.sql/connection state/system)
  ["select column_name from information_schema.columns where table_name = 'gifs';"])
[#:columns{:column_name "id"}
 #:columns{:column_name "created_at"}
 #:columns{:column_name "html"}
 #:columns{:column_name "name"}]
```

For the sake of practice, let's also roll back our migration.

    (migratus.core/rollback (:db.sql/migrations state/system))

Now querying for the table columns should return an empty array, `[]`.

If you ever need to completely roll back all migrations, you can run

    (migratus.core/reset (:db.sql/migrations state/system))

We can also run migrations by simply `(reset)`ing the system, since our `system.edn` has configured migrations to run on startup.

Now we have our initial database schema set up. Next up, we should write some queries for them.

### Querying the Database

For starters we'll create some simple queries to write and read from our database. We're using HugSQL for writing queries. There's some syntactic sugar we should be aware of, for full docs go to the [HugSQL](https://www.hugsql.org/getting-started) documentation. We'll go over a few below.

```sql
-- :name create-gif! :<!
-- :doc inserts and returns a gif
insert into gifs(link, name)
values (:link, :name)
returning *;

-- :name get-gif-by-id :? :1
-- :doc gets a single gif given its ID
select *
from gifs
where id = :id;

-- :name list-gifs
-- :doc lists all gifs
select *
from gifs;
```

Let's `(reset)` again and try out our queries in the REPL.

First, let's create an entry. We can create a gif by querying `:create-gif!` and giving it a map with two keys, `:link` and `:name`.

```clojure
clj꞉user꞉>((:db.sql/query-fn state/system)
  :create-gif! {:link "test html" :name "test name"})
[{:id 1, :link "test html", :name "test name", :created_at #inst"2023-02-18T16:25:05.857508000-00:00"}]
```

We can get that gif by querying for its ID in a similar fashion.

```clojure
clj꞉user꞉>((:db.sql/query-fn state/system) :get-gif-by-id {:id 1})
{:id 1, :link "test html", :name "test name", :created_at #inst"2023-02-18T16:25:05.857508000-00:00"}
```

And to list all of them we can query with an empty parameter map. Note this argument is required, so even if your query doesn't have any arguments you will need to provide `{}`.

```clojure
clj꞉user꞉>((:db.sql/query-fn state/system) :list-gifs {})
[{:id 1, :link "test html", :name "test name", :created_at #inst"2023-02-18T16:25:05.857508000-00:00"}]
```

We've been using the `(:db.sql/query-fn state/system)` function quite often for testing. Why not add it to our `user.clj` namespace. Since this component is only available when the system is started, we can either define it in a function, or have it in our rich comment block at the end. We'll do the latter in this example.

```clojure
(comment
  (go)
  (reset)
  (def query-fn (:db.sql/query-fn state/system)))
```

### CHECKPOINT 3

At this point you should have a `gifs` table in your database, queries written for it, and able to read and write from the REPL.

[Click here to continue on to Checkpoint 4](https://github.com/nikolap/kit-workshop/tree/checkpoint-4)

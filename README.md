# methodius
Srt translation tool

Run with:

```
lein run growmem some_directory_with_en_and_nb_srts
lein run resetmem
lein run translate some_english.srt
```
Run standalone (not yet connected with tasks):
```
java -jar target/uberjar/methodius-0.1.0-SNAPSHOT-standalone.jar 
```

## Installing datomic

http://docs.datomic.com/getting-started.html

Get a free license and download zip from web. In my case datomic-pro-0.9.5359.zip.

In the zipped out folder do:
```
datomic-pro-0.9.5359 $ bin/maven-install
```

To access the peer library from Leiningen you will need to add a dependency to your project.clj file:

```
:dependencies [...
               [com.datomic/datomic-pro "0.9.5206"]
              ...]
```

To download the Datomic Pro peer library dependency, you will need to configure your global Leiningen settings to include your my.datomic.com credentials. This will also require that you configure Leiningen for private repository authentication. Instructions for doing so can be found here: https://github.com/technomancy/leiningen/blob/master/doc/DEPLOY.md#authentication.

NOTE: You only need to follow these steps if you did not download Datomic manually and run bin/maven-install!

```
;; ~/.lein/credentials.clj.gpg
{#"my\.datomic\.com" {:username "USERNAME"
                      :password "PASSWORD"}}
```

Then encrypt it with gpg:
```
$ gpg --default-recipient-self -e \
    ~/.lein/credentials.clj > ~/.lein/credentials.clj.gpg
```

You will also need to configure your project to use this repository:

```
;; project.clj
:repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                 :creds :gpg}}
```

Run ```lein deps```.

If you get error:
```
gpg: can't query passphrase in batch mode
gpg: decryption failed: secret key not available
```
...then try installing gpg-agent.

Test if it is working by doing:
```
gpg --quiet --batch --decrypt ~/.lein/credentials.clj.gpg
gpg: can't query passphrase in batch mode
gpg: decryption failed: secret key not available
```

If on osx and version of gpg is 1.4.*, fix doing:
```
sudo brew install gpg2

sudo mv /usr/local/bin/gpg /usr/local/bin/gpg.1.4
sudo ln -s /usr/local/bin/gpg2 /usr/local/bin/gpg
gpg-agent
lein deps
```

The memory database is an in-memory implementation of the entire Datomic stack (minus durability). It is suitable for experimentation and testing. The memory system is included in the Datomic peer library.

To test, run ```lein repl```:
```
;; use the Datomic native Clojure library
methodius.core=> (require 'datomic.api)
nil
;; a basic in memory datomic database, called 'methodius'
;; alternative is dev which is local durable storage (as opposed to mem)
methodius.core=> (def uri "datomic:mem://methodius")
#'methodius.core/uri
;; ... is created
methodius.core=> (datomic.api/create-database uri)
true
;; connect to the database
methodius.core=> (def conn (datomic.api/connect uri))
#'methodius.core/conn
;; a datom "adds a fact, about a new entity with this temporary id, and asserts that the attribute db/doc has the value hello world"
#'methodius.core/query
```

;; Create users schema
```
(def schema-tx (read-string (slurp "db/schema/20160822000000_users.edn")))
(use '[datomic.api :only [q db] :as d])
(d/transact conn schema-tx)

;; Create users data
(def data-tx (read-string (slurp "db/fixtures/20160822000000_users.edn")))
(d/transact conn data-tx)

(d/transact conn [{:db/id #db/id[:db.part/user] :user/name "adam" :user/email "adam@junkey.com"}])

;; Now that there's a record in the database, you can query the database to retrieve records:
(d/q '[:find ?e ?name
  :where
  [?e :user/name ?name]
] (d/db (d/connect uri)))

(d/q '[:find ?e ?name ?email
  :where
  [?e :user/email "adm1@menneskemaskin.no"]
  [?e :user/name ?name]
  [?e :user/email ?email]
] (d/db (d/connect uri)))

(d/q '[:find ?e ?email ?name
  :where
  [?e :user/name ?name]
  [?e :user/email ?email]
] (d/db (d/connect uri)))

(def result (d/q '[:find ?e
  :where
  [?e :user/name ?name]
  [?e :user/email ?email]
] (d/db (d/connect uri))))
#{[17592186045418] [17592186045421]}

; Get entity of result
(datomic.api/entity (db conn) (first (first result)))
{:db/id 17592186045418}

(get (datomic.api/entity (db conn) (first (first result))) :user/name)
"adam"

;; Get schema
(defn get-user-schema [conn]
  (d/q '[:find ?id
         :where [?e :db/ident ?id]
                [_ :db.install/attribute ?e]]
       (d/db conn)))
(get-user-schema conn)

;; Inspect schema
(def system-ns #{"db" "db.type" "db.install" "db.part" 
                 "db.lang" "fressian" "db.unique" "db.excise" 
                 "db.cardinality" "db.fn"})

(d/q '[:find ?e ?ident
   :in $ ?system-ns
   :where
   [?e :db/ident ?ident]
   [(namespace ?ident) ?ns]
   [((comp not contains?) ?system-ns ?ns)]]
 (d/db conn) system-ns)


; example single insert user
@(d/transact conn [{:db/id #db/id[:db.part/user] :user/name "adam" :user/email "adam@junkey.com"}])


## How to run persistant dev db locally:
```
cd dev/software/datomic/datomic-pro-0.9.5359/ # or wherever you have stored datomic
bin/transactor paalvibe-dev-transactor-template.properties ; remember to set license key
```
Use datomic uri with dev reference:
```
(def uri "datomic:dev://localhost:4334/methodius")
```
# methodius
Srt translation tool

Run with:

```
lein run growmem some_directory_with_en_and_nb_srts
lein run resetmem
lein run translate some_english.srt
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
;; Import the Datomic Peer library (usable as 'Peer' from here on)
(import datomic.Peer)

;; Create the in memory database, calling the static class method Peer#createDatabase
(def uri "datomic:mem://hello")
(Peer/createDatabase uri)

;; Create a "connection" to the database
(def conn (Peer/connect uri))


;; Create a datom to describe the first bits of data we're entering into the DB:
(def datom ["db/add" (Peer/tempid "db.part/user") "db/doc" "hello world"])

;; Pass the datom to the transactor via the connection (note the 'vector of vectors' for the datom):
(def resp (.transact conn [datom]))
```

Or:
```
;; use the Datomic native Clojure library
datomic-tutorial.core=> (require 'datomic.api)
nil
;; a basic in memory datomic database, called 'hello'
datomic-tutorial.core=> (def uri "datomic:mem://hello")
#'datomic-tutorial.core/uri
;; ... is created
datomic-tutorial.core=> (datomic.api/create-database uri)
true
;; connect to the database
datomic-tutorial.core=> (def conn (datomic.api/connect uri))
#'datomic-tutorial.core/conn
;; a datom "adds a fact, about a new entity with this temporary id, and asserts that the attribute db/doc has the value hello world"
datomic-tutorial.core=> (def datom ["db/add" (datomic.api/tempid "db.part/user") "db/doc" "hello world"])
#'datomic-tutorial.core/datom
;; commit the fact via the Datomic transactor
datomic-tutorial.core=> (def resp (datomic.api/transact conn [datom]))
#'datomic-tutorial.core/resp
datomic-tutorial.core=> resp
#<promise$settable_future$reify__5376@b443e92: {:db-before datomic.db.Db@b515b169, :db-after datomic.db.Db@f20dfecc, :tx-data [#datom[13194139534312 50 #inst "2015-06-02T12:23:58.409-00:00" 13194139534312 true] #datom[17592186045417 62 "hello world" 13194139534312 true]], :tempids {-9223350046623220288 17592186045417}}>
;; this query "finds entities where we specify entities as an entity has the attribute db/doc with value hello world"
datomic-tutorial.core=> (def query "[:find ?entity :where [?entity :db/doc \"hello world\"]]")
#'datomic-tutorial.core/query
;; run the query against the 'db' snapshot as input, which we get from the connection
datomic-tutorial.core=> (def result (datomic.api/q query (datomic.api/db conn)))
#'datomic-tutorial.core/result
;; one result; the one we added
datomic-tutorial.core=> result
#{[17592186045417]}
```

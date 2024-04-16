### RUN
```bash
$ clojure -M:run
[main] INFO org.eclipse.jetty.server.Server - jetty-11.0.20; built: 2024-01-29T21:04:22.394Z; git: 922f8dc188f7011e60d0361de585fd4ac4d63064; jvm 21.0.2+13-LTS
[main] INFO org.eclipse.jetty.server.handler.ContextHandler - Started o.e.j.s.ServletContextHandler@2d94467b{/,null,AVAILABLE}
[main] INFO org.eclipse.jetty.server.AbstractConnector - Started ServerConnector@6ac4da08{HTTP/1.1, (http/1.1)}{0.0.0.0:3000}
[main] INFO org.eclipse.jetty.server.Server - Started Server@1c00809b{STARTING}[11.0.20,sto=0] @17333ms
```
### PLAY
```
$ curl http://localhost:3000/
<h1>Halo</h1>
$ curl -d "name=lucy&email=lucy@email.com" http://localhost:3000/person
<h1>Create/Assert berhasil!</h1>
$ curl -d "name=fred&email=fred@email.com" http://localhost:3000/person
<h1>Create/Assert berhasil!</h1>
$ curl -X PUT -d "name=Lucy" http://localhost:3000/person/lucy@email.com
<h1>Accumulate/Update berhasil!</h1>
$ curl -X DELETE http://localhost:3000/person/lucy@email.com
<h1>Delete/Retract berhasil!</h1>
$ curl http://localhost:3000/person/syd@email.com
<table><thead><tr><td>eid</td><td>name</td><td>email</td></tr></thead><tbody><tr><td>92358976733259</td><td>syd</td><td>syd@email.com</td></tr></tbody></table>
```
### REFERENCES
- [https://clojure.org/](https://clojure.org/)
- [https://github.com/weavejester/compojure/](https://github.com/weavejester/compojure/)
- [https://github.com/ring-clojure/ring/](https://github.com/ring-clojure/ring/)
- [https://docs.datomic.com/cloud/datomic-local.html](https://docs.datomic.com/cloud/datomic-local.html)

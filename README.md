# clojure web admin
Clojure web admin is a clojure(script)-based, metadata-driven web admin system,which includes basic features in
web admin system.The full feature list are as follows:

 * Login/Logout
 * CURD with authorities (based on korma)
 * optimistic lock
 * Menu tree with authorities
 * File upload
 * Defaul value for data when creating
 * font-end validation
 * back-end validation

## Quick Start

This project already has some build-in moduals including User, Role,  Organization.

* Import sql script to your mysql database
* Start front-end
```
lein figwheel
```
* Start back-end
```
lein run
```
* Log in as system administrator (username: admin  password: admin)

* Visit http://localhost:3000/ to see demo, [[demo in details|https://github.com/b1412/clojure-web-admin/wiki/Demo-in-details]].

## Metadata

## Form

## Permission system

## Validation

## Add new entity

# License

Copyright © 2015 Leon Zhou

Distributed under the Eclipse Public License, the same as Clojure.

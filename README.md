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
* Log in as system administrator (username: sa password: as)



## Usage
The project provides features based on metadata.Basicly, build-in metadata in database (e.g.,column-name,type-name
,nullable,etc.) are already used,you can also add some extra metadata  in comment which the project already provided.(e.g.,searchable,search-op,exportable,etc.).

Create a new table in database,design database schema and add extra metadata info in  comments.




The following types of metadeta  are supported out of the box:


### nullable
It is a built-in metadata.
The defaule value of nullable is 1.
If a column is set to not null. The value of nullable would be 0.
When the value is 0,it would add a validate rule to avoid null input.

See more details in Validation


### column-name
It is a build-in metadata.
The value of column-name would determine the value of label  in a form or search-box.

### column-def
It is a build-in metadata.
The value of column-name would give a default value to an input element when create an entity.

### type-name
It is a build-in metadata.
The value of column-name would determine how to render the column in a form or search-box.

See mor details in Forms.

### decimal-digits
It is a build-in metadata.
The defaule value is 0 and has no effects.
When set the column-name to 'decimal' and give it precision (e.g. decimal(5.2) ),
decimal-digits is the number after the dot.
It would give a formatter rule in form input element.
```
[:input.form-control
  {:field :numeric :fmt "%.2f"}]
```

### searchable
It is an extra metadata.
When searchable is set to 1, the column would be a search condition in search-box.

### search-op
It is an extra metadata.
Search operation.
Make most use of korma`s search operations,but recently only support some of them.

Available search operation
[=, like, and, or, >, <, >=, <=, in, not-in, not, not=, between]

### exportable
It is an extra metadata.
When exportable is set to 1,the column would be export in the excel.


### lookup-table
It is an extra metadata.
When column-name is 'select',this metadate should be specified.
The value represents the logic associative table.

### lookup-label
It is an extra metadata.
When column-name is 'select',this metadate should be specified.
The value represents the column in logic associative table which would display in dropdown selections in form.

### reserved
It is an extra metadata.
The column can not be insert/update by users.

### hidden-in-grid
It is an extra metadata, not shown in data grid.

### hidden-in-form
It is an extra metadata, not shown in New/Edit form.

### truncatable
It is an extra metadata, short the original value in DB.

### chart-label
It is an extra metadata, specify the x-axis of bar chart

### chart-value
It is an extra metadata, specified the y-axis of bar chart

## Forms
Column-name in details

#### decimal
#### image
#### attachment
#### enum
#### select


## Validation


## How to add new entities
So far adding new entities cause  a bit of pain, be patient, better tool is coming soon!

Follow the steps,you can get a new table with awesome features provided by the project both in font-end & back-end,then you can bulit you own business logic based on them.

* Create a new table in db.

In the showcase,take 'computer' table as an example.

* Design the schema  and add extra schema in comments.
```mysql
 CREATE TABLE `computer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL COMMENT 'importable=1\nexportable=1\nchart-label=1',
  `price` decimal(12,2) NOT NULL COMMENT 'importable=1\nexportable=1\nchart-value=1',
  `image` int(11) DEFAULT NULL COMMENT 'type-name=image\nhidden-in-grid=1',
  `status` varchar(12) DEFAULT NULL COMMENT 'type-name=enum\nenum-group=computer',
  `remark` varchar(1024) DEFAULT NULL COMMENT 'type-name=textarea\nhidden-in-grid=1\nimportable=1\nexportable=1',
  `creator_id` int(11) DEFAULT NULL COMMENT 'reserved=1\ntype-name=select\nlookup-table=user\nlookup-label=username',
  `created_at` datetime DEFAULT NULL COMMENT 'reserved=1',
  `updated_at` datetime DEFAULT NULL COMMENT 'reserved=1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8
```

* Declare the entity in entity.clj

```clojure
(defent computer)
```

* Init Db

```clojure
clojure-web.core> (use 'clojure-web.db.kit)
clojure-web.core> (use 'clojure-web.db.entity)
clojure-web.core> (insert-res-for-entity brand)
;;;Create resources for entity 'brand'
clojure-web.core> (insert-res-to-role brand 1)
;;; assign resources to Role 'admin'. 1 is the ID of Role'admin'

clojure-web.core> (reset! metadata-mem {})
;;; once you change your schema ,reset the metadata catch in memory.
```

* Create back-end

create a router for the new table

```clojure
(ns clojure-web.routes.computer
  (:require [clojure-web.common.routes-helper :refer [defcrud-routes]]
            [clojure-web.db.entity :refer [computer]]
            [clojure-web.render :as render]))

(defcrud-routes computer-routes computer)
```

add the new router to handler in handle.clj

```clojure
(defapi app-routes
  computer-routes
;; omit other routers
)))

```

* Create front-end

register handlers for new entity in handlers.cljs

```clojure
(def entities ["computer"
;;;omit other tables
])
```
creater a new datagrid.

```clojure
(ns clojure-web.computer
  (:require [clojure-web.components.bs-table :refer [create-bs-table]]))

(def computer-panel (create-bs-table "computer"))
```

add the new table to menu in core.cljs

```clojure
(def panels {"computers" computer-panel
;;; omit other panels
}
```

## Permission System
This project support organization-based authentication.

You should add a column named 'creater_id' to your table, and add 'belongs-to' relationship to 'User'
entity in you entity definition.

```clojure
(defent computer
  (k/belongs-to user {:fk :creator_id}))
```

Then you can query the table depend on current logged-in user.

The following scope  of query  are supported out of the box:


### System

The user can operate all the records in the table.

### orgs (TODO)

The user can operate records  created by users in his organization and all the sub-organizations.

### org

The user can operate records created by users in his organization

### user

The use can  operate records created by himself.

### Query API

The query API is  korma API compatible which can meet your simple requirements.

http://sqlkorma.com/docs#select

```clojure

query-entity([entity params])

;;; usage (query-entity user {:id 1})
;;; usage (query-entity user {})

```clojure

### Create API

### Update API

### Delete API



## Dependencies

* ReactBootstrap
* Reagent
* Re-frame
* Re-com
* Bootstraptable
* Bootstrap-fileinput

# License

Copyright © 2015 Leon Zhou

Distributed under the Eclipse Public License, the same as Clojure.

-- MySQL dump 10.13  Distrib 5.5.46, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: clojure_web
-- ------------------------------------------------------
-- Server version	5.5.46-0ubuntu0.14.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `brand`
--
DROP DATABASE IF EXISTS `clojure_web`;

CREATE DATABASE `clojure_web` charset utf8;

USE `clojure_web`;

DROP TABLE IF EXISTS `brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `brand` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL COMMENT 'chart-label=1\nimportable=1\nexportable=1',
  `file` int(11) DEFAULT NULL COMMENT 'type-name=attachment\n',
  `created_at` datetime DEFAULT NULL COMMENT 'reserved=1',
  `updated_at` datetime DEFAULT NULL COMMENT 'reserved=1',
  `creator_id` int(11) DEFAULT NULL COMMENT 'reserved=1\ntype-name=select\nlookup-table=user\nlookup-label=username',
  `rate` int(11) DEFAULT NULL COMMENT 'chart-value=1\nimportable=1\nexportable=1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brand`
--

LOCK TABLES `brand` WRITE;
/*!40000 ALTER TABLE `brand` DISABLE KEYS */;
INSERT INTO `brand` VALUES (1,'Brand-A',14,'2015-12-05 10:40:31','2015-12-05 11:06:19',3,3),(2,'Brand-B',15,'2015-12-05 10:40:58','2015-12-05 21:57:56',3,4),(3,'New-Brand',17,'2015-12-05 22:57:12','2015-12-05 22:57:12',1,3);
/*!40000 ALTER TABLE `brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `computer`
--

DROP TABLE IF EXISTS `computer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `brand_id` varchar(45) DEFAULT NULL COMMENT 'type-name=select\nlookup-table=brand\nlookup-label=name',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `computer`
--

LOCK TABLES `computer` WRITE;
/*!40000 ALTER TABLE `computer` DISABLE KEYS */;
INSERT INTO `computer` VALUES (3,'Computer-A',1345.99,4,'sold-out','Electro Pioneers, living legends and globally revered masters of electronic sound, Kraftwerk celebrate the 35th anniversary of their landmark 1974 hit \'Autobahn\' by releasing digitally remastered versions of eight astounding albums. 8 x individual CDs presented in special slipcases featuring newly expanded artwork, including many previously unseen images all of which have been reproduced to the highest technical standards.',2,'2015-12-05 09:16:43','2015-12-05 22:20:42','1'),(4,'Computer-B',900.00,5,'discount','blablabla...',3,'2015-12-05 09:16:43','2015-12-05 22:20:18','2'),(5,'Computer-C',1200.22,6,'sold-out','blablabla...',4,'2015-12-05 09:16:43','2015-12-05 22:20:28','1'),(31,'New-Computer',800.00,NULL,NULL,'blablabla',1,'2015-12-05 22:38:13','2015-12-05 22:38:13',NULL),(32,'Computer-D',700.99,16,'hot-selling','some remarks',1,'2015-12-05 22:42:48','2015-12-05 22:42:48','1');
/*!40000 ALTER TABLE `computer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dictionary`
--

DROP TABLE IF EXISTS `dictionary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dictionary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `label` varchar(45) DEFAULT NULL,
  `value` varchar(45) DEFAULT NULL,
  `group` varchar(45) DEFAULT NULL,
  `remark` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dictionary`
--

LOCK TABLES `dictionary` WRITE;
/*!40000 ALTER TABLE `dictionary` DISABLE KEYS */;
INSERT INTO `dictionary` VALUES (1,'system','system','operation-scope',NULL),(2,'orgs','orgs','operation-scope',NULL),(3,'org','org','operation-scope',NULL),(4,'user','user','operation-scope',NULL),(5,'man','man','gender',NULL),(6,'women','women','gender',NULL),(7,'other','other','operation-scope',NULL),(8,'yes','yes','yes-or-no',NULL),(9,'no','no','yes-or-no',NULL),(10,'project','project','task-type',NULL),(11,'entity','entity','task-type',NULL),(12,'menu','menu','componenet-type',NULL),(13,'button','button','componenet-type',NULL),(14,'action','action','componenet-type',NULL),(15,'sold-out','sold-out','computer',NULL),(16,'hot-selling','hot-selling','computer',NULL),(17,'discount','discount','computer',NULL);
/*!40000 ALTER TABLE `dictionary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization`
--

DROP TABLE IF EXISTS `organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL COMMENT 'searchable==1',
  `parent_id` int(11) DEFAULT NULL COMMENT 'type-name=select\nlookup-table-alise=parent\nlookup-table=organization\nlookup-label=name',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization`
--

LOCK TABLES `organization` WRITE;
/*!40000 ALTER TABLE `organization` DISABLE KEYS */;
INSERT INTO `organization` VALUES (1,'Parent',0),(2,'Child1',1),(3,'Child2',1),(4,'Child3',1),(5,'Grandchild1-1',2),(6,'Grandchild1-2',2),(7,'Grandchild2-1',3);
/*!40000 ALTER TABLE `organization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource`
--

DROP TABLE IF EXISTS `resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uri` varchar(245) DEFAULT NULL,
  `method` varchar(45) DEFAULT NULL COMMENT 'searchable==1',
  `desc` varchar(245) DEFAULT NULL COMMENT 'hidden-in-grid=1',
  `entity` varchar(45) NOT NULL COMMENT 'hidden-in-grid=1',
  `key` varchar(45) DEFAULT NULL,
  `label` varchar(45) DEFAULT NULL COMMENT 'hidden-in-grid=1',
  `type` varchar(45) DEFAULT 'menu' COMMENT 'hidden-in-grid=1',
  `parent_id` int(11) DEFAULT NULL COMMENT 'type-name=select\nlookup-table-alise=parent\nlookup-table=resource\nlookup-label=key',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource`
--

LOCK TABLES `resource` WRITE;
/*!40000 ALTER TABLE `resource` DISABLE KEYS */;
INSERT INTO `resource` VALUES (1,'/roles/[0-9]*/resources','GET','get resources of a role','role','get-role-resources','Assign','button',71),(2,'/roles/[0-9]*/resources','PUT','update resources of a role','role','update-role-resources',NULL,'button',71),(3,'/roles[/]?','GET','query roles','role','roles','Role','menu',0),(4,'/roles[/]?','POST','create a new role','role','new-role','New','button',224),(5,'/roles/[0-9]+','GET','get a specificrole','role','get-role','Edit','button',224),(6,'/roles/[0-9]+','DELETE','delete a specific role','role','delete-role','Delete','button',224),(7,'/roles/[0-9]+','PUT','update a specific role','role','edit-role','Update','button',224),(8,'/roles/meta','GET','get metadata of role','role','role-meta','Metadata','button',224),(9,'/roles/charts','GET','view role charts','role','role-charts','Charts','button',224),(10,'/roles/excel','POST','import  excel of roles','role','import-role-excel','Import','button',224),(11,'/roles/excel','GET','export  excel of roles','role','export-role-excel','Export','button',224),(12,'/roles/excel/template','GET','get excel template of roles','role','export-role-excel-template',NULL,'button',224),(13,'/users[/]?','GET','query users','user','users','User','menu',0),(14,'/users[/]?','POST','create a new user','user','new-user','New','button',234),(15,'/users/[0-9]+','GET','get a specificuser','user','get-user','Edit','button',234),(16,'/users/[0-9]+','DELETE','delete a specific user','user','delete-user','Delete','button',234),(17,'/users/[0-9]+','PUT','update a specific user','user','edit-user','Update','button',234),(18,'/users/meta','GET','get metadata of user','user','user-meta','Metadata','button',234),(19,'/users/charts','GET','view user charts','user','user-charts','Charts','button',234),(20,'/users/excel','POST','import  excel of users','user','import-user-excel','Import','button',234),(21,'/users/excel','GET','export  excel of users','user','export-user-excel','Export','button',234),(22,'/users/excel/template','GET','get excel template of users','user','export-user-excel-template',NULL,'button',234),(23,'/role_resources[/]?','GET','query role_resources','role_resource','role_resources','Role Resource','button',0),(24,'/role_resources[/]?','POST','create a new role_resource','role_resource','new-role_resource','New','button',244),(25,'/role_resources/[0-9]+','GET','get a specificrole_resource','role_resource','get-role_resource','Edit','button',244),(26,'/role_resources/[0-9]+','DELETE','delete a specific role_resource','role_resource','delete-role_resource','Delete','button',244),(27,'/role_resources/[0-9]+','PUT','update a specific role_resource','role_resource','edit-role_resource','Update','button',244),(28,'/role_resources/meta','GET','get metadata of role_resource','role_resource','role_resource-meta','Metadata','button',244),(29,'/role_resources/charts','GET','view role_resource charts','role_resource','role_resource-charts','Charts','button',244),(30,'/role_resources/excel','POST','import  excel of role_resources','role_resource','import-role_resource-excel','Import','button',244),(31,'/role_resources/excel','GET','export  excel of role_resources','role_resource','export-role_resource-excel','Export','button',244),(32,'/role_resources/excel/template','GET','get excel template of role_resources','role_resource','export-role_resource-excel-template',NULL,'button',244),(33,'/organizations[/]?','GET','query organizations','organization','organizations','Organization','menu',0),(34,'/organizations[/]?','POST','create a new organization','organization','new-organization','New','button',254),(35,'/organizations/[0-9]+','GET','get a specificorganization','organization','get-organization','Edit','button',254),(36,'/organizations/[0-9]+','DELETE','delete a specific organization','organization','delete-organization','Delete','button',254),(37,'/organizations/[0-9]+','PUT','update a specific organization','organization','edit-organization','Update','button',254),(38,'/organizations/meta','GET','get metadata of organization','organization','organization-meta','Metadata','button',254),(39,'/organizations/charts','GET','view organization charts','organization','organization-charts','Charts','button',254),(40,'/organizations/excel','POST','import  excel of organizations','organization','import-organization-excel','Import','button',254),(41,'/organizations/excel','GET','export  excel of organizations','organization','export-organization-excel','Export','button',254),(42,'/organizations/excel/template','GET','get excel template of organizations','organization','export-organization-excel-template',NULL,'button',254),(43,'/resources[/]?','GET','query resources','resource','resources','Resource','button',0),(44,'/resources[/]?','POST','create a new resource','resource','new-resource','New','button',264),(45,'/resources/[0-9]+','GET','get a specificresource','resource','get-resource','Edit','button',264),(46,'/resources/[0-9]+','DELETE','delete a specific resource','resource','delete-resource','Delete','button',264),(47,'/resources/[0-9]+','PUT','update a specific resource','resource','edit-resource','Update','button',264),(48,'/resources/meta','GET','get metadata of resource','resource','resource-meta','Metadata','button',264),(49,'/resources/charts','GET','view resource charts','resource','resource-charts','Charts','button',264),(50,'/resources/excel','POST','import  excel of resources','resource','import-resource-excel','Import','button',264),(51,'/resources/excel','GET','export  excel of resources','resource','export-resource-excel','Export','button',264),(52,'/resources/excel/template','GET','get excel template of resources','resource','export-resource-excel-template',NULL,'button',264),(54,'/computers[/]?','GET','query computers','computer','computers','Computer','menu',0),(55,'/computers[/]?','POST','create a new computer','computer','new-computer','New','button',54),(56,'/computers/[0-9]+','GET','get a specificcomputer','computer','get-computer','Edit','button',54),(57,'/computers/[0-9]+','DELETE','delete a specific computer','computer','delete-computer','Delete','button',54),(58,'/computers/[0-9]+','PUT','update a specific computer','computer','edit-computer','Update','button',54),(59,'/computers/meta','GET','get metadata of computer','computer','computer-meta','Metadata','button',54),(60,'/computers/charts','GET','view computer charts','computer','computer-charts','Charts','button',54),(61,'/computers/excel','POST','import  excel of computers','computer','import-computer-excel','Import','button',54),(62,'/computers/excel','GET','export  excel of computers','computer','export-computer-excel','Export','button',54),(63,'/computers/excel/template','GET','get excel template of computers','computer','export-computer-excel-template',NULL,'button',54),(64,'/brands[/]?','GET','query brands','brand','brands','Brand','menu',0),(65,'/brands[/]?','POST','create a new brand','brand','new-brand','New','button',64),(66,'/brands/[0-9]+','GET','get a specificbrand','brand','get-brand','Edit','button',64),(67,'/brands/[0-9]+','DELETE','delete a specific brand','brand','delete-brand','Delete','button',64),(68,'/brands/[0-9]+','PUT','update a specific brand','brand','edit-brand','Update','button',64),(69,'/brands/meta','GET','get metadata of brand','brand','brand-meta','Metadata','button',64),(70,'/brands/charts','GET','view brand charts','brand','brand-charts','Charts','button',64),(71,'/brands/excel','POST','import  excel of brands','brand','import-brand-excel','Import','button',64),(72,'/brands/excel','GET','export  excel of brands','brand','export-brand-excel','Export','button',64),(73,'/brands/excel/template','GET','get excel template of brands','brand','export-brand-excel-template',NULL,'button',64);
/*!40000 ALTER TABLE `resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(12) NOT NULL COMMENT 'searchable==1',
  `valid` varchar(12) NOT NULL COMMENT 'type-name=enum\nenum-group=yes-or-no\n',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'Admin','yes'),(2,'Manager','yes'),(3,'Staff','yes');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_resource`
--

DROP TABLE IF EXISTS `role_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL COMMENT 'searchable=1\n',
  `resource_id` int(11) DEFAULT NULL,
  `scope` varchar(10) DEFAULT 'system' COMMENT 'searchable=1\ntype-name=enum\nenum-group=search-scope',
  `valid` varchar(45) DEFAULT 'yes',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_resource`
--

LOCK TABLES `role_resource` WRITE;
/*!40000 ALTER TABLE `role_resource` DISABLE KEYS */;
INSERT INTO `role_resource` VALUES (1,1,1,'system','yes'),(2,1,2,'system','yes'),(3,1,3,'system','yes'),(4,1,4,'system','yes'),(5,1,5,'system','yes'),(6,1,6,'system','yes'),(7,1,7,'system','yes'),(8,1,8,'system','yes'),(9,1,9,'system','yes'),(10,1,10,'system','yes'),(11,1,11,'system','yes'),(12,1,12,'system','yes'),(13,1,13,'system','yes'),(14,1,14,'system','yes'),(15,1,15,'system','yes'),(16,1,16,'system','yes'),(17,1,17,'system','yes'),(18,1,18,'system','yes'),(19,1,19,'system','yes'),(20,1,20,'system','yes'),(21,1,21,'system','yes'),(22,1,22,'system','yes'),(23,1,23,'system','yes'),(24,1,24,'system','yes'),(25,1,25,'system','yes'),(26,1,26,'system','yes'),(27,1,27,'system','yes'),(28,1,28,'system','yes'),(29,1,29,'system','yes'),(30,1,30,'system','yes'),(31,1,31,'system','yes'),(32,1,32,'system','yes'),(33,1,33,'system','yes'),(34,1,34,'system','yes'),(35,1,35,'system','yes'),(36,1,36,'system','yes'),(37,1,37,'system','yes'),(38,1,38,'system','yes'),(39,1,39,'system','yes'),(40,1,40,'system','yes'),(41,1,41,'system','yes'),(42,1,42,'system','yes'),(43,1,43,'system','yes'),(44,1,44,'system','yes'),(45,1,45,'system','yes'),(46,1,46,'system','yes'),(47,1,47,'system','yes'),(48,1,48,'system','yes'),(49,1,49,'system','yes'),(50,1,50,'system','yes'),(51,1,51,'system','yes'),(52,1,52,'system','yes'),(53,1,54,'system','yes'),(54,1,55,'system','yes'),(55,1,56,'system','yes'),(56,1,57,'system','yes'),(57,1,58,'system','yes'),(58,1,59,'system','yes'),(59,1,60,'system','yes'),(60,1,61,'system','yes'),(61,1,62,'system','yes'),(62,1,63,'system','yes'),(63,3,58,'user','yes'),(65,3,59,'system','yes'),(69,3,56,'user','yes'),(70,3,55,'system','yes'),(71,3,54,'user','yes'),(72,2,58,'org','yes'),(74,2,59,'system','yes'),(76,2,57,'org','yes'),(79,2,56,'org','yes'),(80,2,55,'system','yes'),(81,2,54,'org','yes'),(82,1,64,'system','yes'),(83,1,65,'system','yes'),(84,1,66,'system','yes'),(85,1,67,'system','yes'),(86,1,68,'system','yes'),(87,1,69,'system','yes'),(88,1,70,'system','yes'),(89,1,71,'system','yes'),(90,1,72,'system','yes'),(91,1,73,'system','yes'),(92,3,69,'system','yes'),(93,3,64,'system','yes'),(94,2,69,'system','yes'),(95,2,68,'system','yes'),(96,2,67,'system','yes'),(97,2,66,'system','yes'),(98,2,65,'system','yes'),(99,2,64,'system','yes'),(100,3,18,'system','yes'),(101,3,13,'system','yes'),(102,2,18,'system','yes'),(103,2,13,'system','yes');
/*!40000 ALTER TABLE `role_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `upload`
--

DROP TABLE IF EXISTS `upload`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `upload` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `filename` varchar(45) DEFAULT NULL,
  `path` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `upload`
--

LOCK TABLES `upload` WRITE;
/*!40000 ALTER TABLE `upload` DISABLE KEYS */;
INSERT INTO `upload` VALUES (1,'1.jpeg','/uploads/1.jpeg'),(2,'2.jpg','/uploads/2.jpg'),(3,'3.jpg','/uploads/3.jpg'),(4,'c1.jpg','/uploads/c1.jpg'),(5,'c2.jpg','/uploads/c2.jpg'),(6,'c3.jpg','/uploads/c3.jpg'),(11,'2.jpg','/uploads/2.jpg'),(12,'file1.doc','/uploads/file1.doc'),(13,'file1.doc','/uploads/file1.doc'),(14,'file1.doc','/uploads/file1.doc'),(15,'file2.doc','/uploads/file2.doc'),(16,'c1.jpg','/uploads/c1.jpg'),(17,'new-file1.doc','/uploads/new-file1.doc');
/*!40000 ALTER TABLE `upload` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL COMMENT 'searchable=1\nsearch-op=like\nexportable=1\nchart-label=1\nimportable=1',
  `password` varchar(245) DEFAULT NULL COMMENT 'exportable=1\ntype-name=password\nhidden-in-grid=1\ntruncatable=1',
  `organization_id` int(11) DEFAULT NULL COMMENT 'type-name=select\nlookup-table=organization\nlookup-label=name\nsearchable=1\nexportable=1',
  `role_id` int(11) DEFAULT NULL COMMENT 'type-name=select\nlookup-table=role\nlookup-label=name',
  `avatar` int(11) DEFAULT NULL COMMENT 'type-name=image\nhidden-in-grid=1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='This is just to test how to alter comments';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','$2a$11$CxtoOdoxrDOjTbSedpD1u.8IdoTPPQWFodCIf7nmL0kr5IUihMT1u',1,1,1),(2,'staff','$2a$11$4kFZoLJuquwHkE7PvbESBePg5eFLEDFcBLuCi5U4L25So2o/9SheC',2,3,2),(3,'manager','$2a$11$kFYstqx1O8R6lbq7cAhE1.epUuRT.fupoQU3jGB91eVPnSYrml3b6',2,2,3),(4,'staff2','$2a$11$V/DLOrOziurGfGpcmMMPLu0UTYMQa1q1KZ4egxkngKD/1zFd4/1BC',3,3,11);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-12-06 12:18:16

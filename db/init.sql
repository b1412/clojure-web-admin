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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dictionary`
--

LOCK TABLES `dictionary` WRITE;
/*!40000 ALTER TABLE `dictionary` DISABLE KEYS */;
INSERT INTO `dictionary` VALUES (1,'system','system','operation-scope',NULL),(2,'orgs','orgs','operation-scope',NULL),(3,'org','org','operation-scope',NULL),(4,'user','user','operation-scope',NULL),(5,'man','man','gender',NULL),(6,'women','women','gender',NULL),(7,'other','other','operation-scope',NULL),(8,'yes','yes','yes-or-no',NULL),(9,'no','no','yes-or-no',NULL),(10,'project','project','task-type',NULL),(11,'entity','entity','task-type',NULL),(12,'menu','menu','componenet-type',NULL),(13,'button','button','componenet-type',NULL),(14,'action','action','componenet-type',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource`
--

LOCK TABLES `resource` WRITE;
/*!40000 ALTER TABLE `resource` DISABLE KEYS */;
INSERT INTO `resource` VALUES (1,'/roles/[0-9]*/resources','GET','get resources of a role','role','get-role-resources','Assign','button',71),(2,'/roles/[0-9]*/resources','PUT','update resources of a role','role','update-role-resources',NULL,'button',71),(3,'/roles[/]?','GET','query roles','role','roles','Role','menu',0),(4,'/roles[/]?','POST','create a new role','role','new-role','New','button',224),(5,'/roles/[0-9]+','GET','get a specificrole','role','get-role','Edit','button',224),(6,'/roles/[0-9]+','DELETE','delete a specific role','role','delete-role','Delete','button',224),(7,'/roles/[0-9]+','PUT','update a specific role','role','edit-role','Update','button',224),(8,'/roles/meta','GET','get metadata of role','role','role-meta','Metadata','button',224),(9,'/roles/charts','GET','view role charts','role','role-charts','Charts','button',224),(10,'/roles/excel','POST','import  excel of roles','role','import-role-excel','Import','button',224),(11,'/roles/excel','GET','export  excel of roles','role','export-role-excel','Export','button',224),(12,'/roles/excel/template','GET','get excel template of roles','role','export-role-excel-template',NULL,'button',224),(13,'/users[/]?','GET','query users','user','users','User','menu',0),(14,'/users[/]?','POST','create a new user','user','new-user','New','button',234),(15,'/users/[0-9]+','GET','get a specificuser','user','get-user','Edit','button',234),(16,'/users/[0-9]+','DELETE','delete a specific user','user','delete-user','Delete','button',234),(17,'/users/[0-9]+','PUT','update a specific user','user','edit-user','Update','button',234),(18,'/users/meta','GET','get metadata of user','user','user-meta','Metadata','button',234),(19,'/users/charts','GET','view user charts','user','user-charts','Charts','button',234),(20,'/users/excel','POST','import  excel of users','user','import-user-excel','Import','button',234),(21,'/users/excel','GET','export  excel of users','user','export-user-excel','Export','button',234),(22,'/users/excel/template','GET','get excel template of users','user','export-user-excel-template',NULL,'button',234),(23,'/role_resources[/]?','GET','query role_resources','role_resource','role_resources','Role Resource','button',0),(24,'/role_resources[/]?','POST','create a new role_resource','role_resource','new-role_resource','New','button',244),(25,'/role_resources/[0-9]+','GET','get a specificrole_resource','role_resource','get-role_resource','Edit','button',244),(26,'/role_resources/[0-9]+','DELETE','delete a specific role_resource','role_resource','delete-role_resource','Delete','button',244),(27,'/role_resources/[0-9]+','PUT','update a specific role_resource','role_resource','edit-role_resource','Update','button',244),(28,'/role_resources/meta','GET','get metadata of role_resource','role_resource','role_resource-meta','Metadata','button',244),(29,'/role_resources/charts','GET','view role_resource charts','role_resource','role_resource-charts','Charts','button',244),(30,'/role_resources/excel','POST','import  excel of role_resources','role_resource','import-role_resource-excel','Import','button',244),(31,'/role_resources/excel','GET','export  excel of role_resources','role_resource','export-role_resource-excel','Export','button',244),(32,'/role_resources/excel/template','GET','get excel template of role_resources','role_resource','export-role_resource-excel-template',NULL,'button',244),(33,'/organizations[/]?','GET','query organizations','organization','organizations','Organization','menu',0),(34,'/organizations[/]?','POST','create a new organization','organization','new-organization','New','button',254),(35,'/organizations/[0-9]+','GET','get a specificorganization','organization','get-organization','Edit','button',254),(36,'/organizations/[0-9]+','DELETE','delete a specific organization','organization','delete-organization','Delete','button',254),(37,'/organizations/[0-9]+','PUT','update a specific organization','organization','edit-organization','Update','button',254),(38,'/organizations/meta','GET','get metadata of organization','organization','organization-meta','Metadata','button',254),(39,'/organizations/charts','GET','view organization charts','organization','organization-charts','Charts','button',254),(40,'/organizations/excel','POST','import  excel of organizations','organization','import-organization-excel','Import','button',254),(41,'/organizations/excel','GET','export  excel of organizations','organization','export-organization-excel','Export','button',254),(42,'/organizations/excel/template','GET','get excel template of organizations','organization','export-organization-excel-template',NULL,'button',254),(43,'/resources[/]?','GET','query resources','resource','resources','Resource','menu',0),(44,'/resources[/]?','POST','create a new resource','resource','new-resource','New','button',264),(45,'/resources/[0-9]+','GET','get a specificresource','resource','get-resource','Edit','button',264),(46,'/resources/[0-9]+','DELETE','delete a specific resource','resource','delete-resource','Delete','button',264),(47,'/resources/[0-9]+','PUT','update a specific resource','resource','edit-resource','Update','button',264),(48,'/resources/meta','GET','get metadata of resource','resource','resource-meta','Metadata','button',264),(49,'/resources/charts','GET','view resource charts','resource','resource-charts','Charts','button',264),(50,'/resources/excel','POST','import  excel of resources','resource','import-resource-excel','Import','button',264),(51,'/resources/excel','GET','export  excel of resources','resource','export-resource-excel','Export','button',264),(52,'/resources/excel/template','GET','get excel template of resources','resource','export-resource-excel-template',NULL,'button',264);
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
  `valid` varchar(12) NOT NULL COMMENT 'searchable=1\ntype-name=enum\nenum-group=yes-or-no\n',
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
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_resource`
--

LOCK TABLES `role_resource` WRITE;
/*!40000 ALTER TABLE `role_resource` DISABLE KEYS */;
INSERT INTO `role_resource` VALUES (1,1,1,'system','yes'),(2,1,2,'system','yes'),(3,1,3,'system','yes'),(4,1,4,'system','yes'),(5,1,5,'system','yes'),(6,1,6,'system','yes'),(7,1,7,'system','yes'),(8,1,8,'system','yes'),(9,1,9,'system','yes'),(10,1,10,'system','yes'),(11,1,11,'system','yes'),(12,1,12,'system','yes'),(13,1,13,'system','yes'),(14,1,14,'system','yes'),(15,1,15,'system','yes'),(16,1,16,'system','yes'),(17,1,17,'system','yes'),(18,1,18,'system','yes'),(19,1,19,'system','yes'),(20,1,20,'system','yes'),(21,1,21,'system','yes'),(22,1,22,'system','yes'),(23,1,23,'system','yes'),(24,1,24,'system','yes'),(25,1,25,'system','yes'),(26,1,26,'system','yes'),(27,1,27,'system','yes'),(28,1,28,'system','yes'),(29,1,29,'system','yes'),(30,1,30,'system','yes'),(31,1,31,'system','yes'),(32,1,32,'system','yes'),(33,1,33,'system','yes'),(34,1,34,'system','yes'),(35,1,35,'system','yes'),(36,1,36,'system','yes'),(37,1,37,'system','yes'),(38,1,38,'system','yes'),(39,1,39,'system','yes'),(40,1,40,'system','yes'),(41,1,41,'system','yes'),(42,1,42,'system','yes'),(43,1,43,'system','yes'),(44,1,44,'system','yes'),(45,1,45,'system','yes'),(46,1,46,'system','yes'),(47,1,47,'system','yes'),(48,1,48,'system','yes'),(49,1,49,'system','yes'),(50,1,50,'system','yes'),(51,1,51,'system','yes'),(52,1,52,'system','yes');
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `upload`
--

LOCK TABLES `upload` WRITE;
/*!40000 ALTER TABLE `upload` DISABLE KEYS */;
INSERT INTO `upload` VALUES (1,'1.jpeg','/uploads/1.jpeg'),(2,'2.jpg','/uploads/2.jpg'),(3,'3.jpg','/uploads/3.jpg');
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
  `password` varchar(245) DEFAULT NULL COMMENT 'exportable=1\ntype-name=password\nhidden-in-grid=1\ntruncatable=1\nsearchable=1',
  `organization_id` int(11) DEFAULT NULL COMMENT 'type-name=select\nlookup-table=organization\nlookup-label=name\nsearchable=1\nexportable=1',
  `role_id` int(11) DEFAULT NULL COMMENT 'type-name=select\nlookup-table=role\nlookup-label=name',
  `avatar` int(11) DEFAULT NULL COMMENT 'type-name=image\nhidden-in-grid=1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='This is just to test how to alter comments';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','$2a$11$xMVVdRh62/zMWPIF/rMk5OX2XxWMMSFQys/WvtsJzF/3S1xSv.mki',1,1,1),(2,'staff','$2a$11$lyW.yacP30J4s66C/AJ8guYlvcOp8t0Lh8ErteSBjRV8Rc3G7sKYa',2,3,2),(3,'manager','$2a$11$h3OCYmz0ZXpnBDPZ1zXYP.Tj5wdCVP3kOvpwtJsWWTZmZmzuYaFD6',2,2,3);
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

-- Dump completed on 2015-12-05 19:33:20

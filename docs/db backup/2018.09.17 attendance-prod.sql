/*
Navicat MySQL Data Transfer

Source Server         : droplet - produção
Source Server Version : 50532
Source Host           : 192.241.180.208:3306
Source Database       : attendance

Target Server Type    : MYSQL
Target Server Version : 50532
File Encoding         : 65001

Date: 2018-09-17 11:40:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `name_complement` varchar(255) DEFAULT NULL,
  `center_id` bigint(20) NOT NULL,
  `resumo_mensal_id` bigint(20) DEFAULT NULL,
  `check_title_required` bit(1) NOT NULL,
  `last_attendance_date` date DEFAULT NULL,
  `last_attendance_total` bigint(20) DEFAULT NULL,
  `person_suggestion_by_days` int(11) DEFAULT NULL,
  `person_suggestion_by_events` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr758s90bud7kocnwq4eeq6r57` (`center_id`) USING BTREE,
  KEY `FKr758s90b` (`resumo_mensal_id`),
  CONSTRAINT `FKr758s90b` FOREIGN KEY (`resumo_mensal_id`) REFERENCES `resumo_mensal` (`id`),
  CONSTRAINT `FKr758s90bud7kocnwq4eeq6r57` FOREIGN KEY (`center_id`) REFERENCES `center` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for attendance
-- ----------------------------
DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `activity_id` bigint(20) NOT NULL,
  `person_id` bigint(20) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `last_edit_time` datetime NOT NULL,
  `last_edit_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_Attendance` (`date`,`activity_id`,`person_id`) USING BTREE,
  KEY `FKey1f232tapbr4hrd90blf6ga6` (`activity_id`) USING BTREE,
  KEY `FKd5wmpdsbs8oyb3b2amd78lfev` (`person_id`) USING BTREE,
  KEY `FK1s7bq6i5ro1sg7t1fuhcoor9p` (`last_edit_user_id`),
  CONSTRAINT `FK1s7bq6i5ro1sg7t1fuhcoor9p` FOREIGN KEY (`last_edit_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKd5wmpdsbs8oyb3b2amd78lfev` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FKey1f232tapbr4hrd90blf6ga6` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11731 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for center
-- ----------------------------
DROP TABLE IF EXISTS `center`;
CREATE TABLE `center` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `owner_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_Center` (`name`),
  KEY `FKcpusgqdg7aksfksddribt6s39` (`owner_id`) USING BTREE,
  CONSTRAINT `FKcpusgqdg7aksfksddribt6s39` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for output_view
-- ----------------------------
DROP TABLE IF EXISTS `output_view`;
CREATE TABLE `output_view` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `execution_status` varchar(255) DEFAULT NULL,
  `execution_time` datetime DEFAULT NULL,
  `formatcsv` bit(1) NOT NULL,
  `format_excel` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for person
-- ----------------------------
DROP TABLE IF EXISTS `person`;
CREATE TABLE `person` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `center_id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `short_name` varchar(255) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `check_colegial` bit(1) NOT NULL,
  `check_universitario` bit(1) NOT NULL,
  `status` int(11) DEFAULT NULL,
  `tag1` varchar(255) DEFAULT NULL,
  `tag2` varchar(255) DEFAULT NULL,
  `tag3` varchar(255) DEFAULT NULL,
  `tag4` varchar(255) DEFAULT NULL,
  `check_contribui` bit(1) NOT NULL,
  `check_contribui_value` decimal(19,2) DEFAULT NULL,
  `check_cooperador` bit(1) NOT NULL,
  `check_cooperador_date` date DEFAULT NULL,
  `check_estudante_mail` bit(1) NOT NULL,
  `check_estudante_mail_date` date DEFAULT NULL,
  `check_estudantewa` bit(1) NOT NULL,
  `check_estudantewadate` date DEFAULT NULL,
  `check_profissional_mail` bit(1) NOT NULL,
  `check_profissional_mail_date` date DEFAULT NULL,
  `check_profissionalwa` bit(1) NOT NULL,
  `check_profissionalwadate` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_Person` (`center_id`,`name`) USING BTREE,
  KEY `FKrfgxbh7txji5w5xtxbfffgrji` (`center_id`) USING BTREE,
  CONSTRAINT `FKrfgxbh7txji5w5xtxbfffgrji` FOREIGN KEY (`center_id`) REFERENCES `center` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=559 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for resumo_mensal
-- ----------------------------
DROP TABLE IF EXISTS `resumo_mensal`;
CREATE TABLE `resumo_mensal` (
  `id` bigint(20) NOT NULL,
  `labor` varchar(5) NOT NULL,
  `query` varchar(8) NOT NULL,
  `numero` varchar(255) NOT NULL,
  `letra` varchar(2) DEFAULT NULL,
  `descricao` varchar(255) NOT NULL,
  `descricao_nota` varchar(255) NOT NULL,
  `letra_descricao_nota` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for sharing
-- ----------------------------
DROP TABLE IF EXISTS `sharing`;
CREATE TABLE `sharing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` char(1) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `status` int(11) NOT NULL,
  `status_time` datetime NOT NULL,
  `center_id` bigint(20) NOT NULL,
  `activity_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uc_sharing` (`type`,`user_id`,`center_id`,`activity_id`),
  KEY `FKern95y6g83x9tocqnxup92vmr` (`activity_id`) USING BTREE,
  KEY `FKb6lnq5lh9klrgdkx854j9cry1` (`user_id`) USING BTREE,
  KEY `FK2u9qxb3i0o7fqwvpmvs835b28` (`center_id`),
  CONSTRAINT `FK2u9qxb3i0o7fqwvpmvs835b28` FOREIGN KEY (`center_id`) REFERENCES `center` (`id`),
  CONSTRAINT `FKb6lnq5lh9klrgdkx854j9cry1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKern95y6g83x9tocqnxup92vmr` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for temp_migration
-- ----------------------------
DROP TABLE IF EXISTS `temp_migration`;
CREATE TABLE `temp_migration` (
  `ano` varchar(255) NOT NULL,
  `nome` varchar(255) NOT NULL,
  `atividade` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `default_center_id` bigint(20) DEFAULT NULL,
  `access_token` varchar(255) DEFAULT NULL,
  `old_mail` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `username` (`username`),
  KEY `FKqc33j9r27e853qk4oy3d3x8xc` (`default_center_id`),
  CONSTRAINT `FKqc33j9r27e853qk4oy3d3x8xc` FOREIGN KEY (`default_center_id`) REFERENCES `center` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

-- ----------------------------
-- View structure for vw_1_attendance
-- ----------------------------
DROP VIEW IF EXISTS `vw_1_attendance`;
CREATE ALGORITHM=UNDEFINED DEFINER=`bumerangue`@`%` SQL SECURITY DEFINER VIEW `vw_1_attendance` AS select `v`.`attendance_id` AS `attendance_id`,`v`.`date` AS `date`,`v`.`activity_id` AS `activity_id`,`v`.`person_id` AS `person_id`,`v`.`title` AS `title`,`v`.`date_and_title` AS `date_and_title`,`v`.`last_edit_time` AS `last_edit_time`,`v`.`last_edit_user_id` AS `last_edit_user_id`,`v`.`last_edit_user_name` AS `last_edit_user_name`,`v`.`activity_description` AS `activity_description`,`v`.`activity_name` AS `activity_name`,`v`.`activity_name_complement` AS `activity_name_complement`,`v`.`resumo_mensal_id` AS `resumo_mensal_id`,`v`.`name` AS `name`,`v`.`short_name` AS `short_name`,`v`.`birthday` AS `birthday`,`v`.`phone` AS `phone`,`v`.`email` AS `email`,`v`.`check_colegial` AS `check_colegial`,`v`.`check_universitario` AS `check_universitario`,`v`.`status` AS `status`,`v`.`tag1` AS `tag1`,`v`.`tag2` AS `tag2`,`v`.`tag3` AS `tag3`,`v`.`tag4` AS `tag4`,`v`.`check_contribui` AS `check_contribui`,`v`.`check_contribui_value` AS `check_contribui_value`,`v`.`check_cooperador` AS `check_cooperador`,`v`.`check_cooperador_date` AS `check_cooperador_date`,`v`.`check_estudante_mail` AS `check_estudante_mail`,`v`.`check_estudante_mail_date` AS `check_estudante_mail_date`,`v`.`check_estudantewa` AS `check_estudantewa`,`v`.`check_estudantewadate` AS `check_estudantewadate`,`v`.`check_profissional_mail` AS `check_profissional_mail`,`v`.`check_profissional_mail_date` AS `check_profissional_mail_date`,`v`.`check_profissionalwa` AS `check_profissionalwa`,`v`.`check_profissionalwadate` AS `check_profissionalwadate`,`v`.`center_id` AS `center_id`,`v`.`center_description` AS `center_description`,`v`.`center_name` AS `center_name`,`v`.`owner_id` AS `owner_id`,`v`.`owner_email` AS `owner_email`,`v`.`owner_name` AS `owner_name`,`v`.`owner_username` AS `owner_username`,if((`v`.`activity_name_complement` = 'sr'),1,0) AS `sr`,if((`v`.`activity_name_complement` = 'sg'),1,0) AS `sg`,if((`v`.`activity_id` = 1),1,0) AS `sr_direcao_espiritual`,if((`v`.`activity_id` = 2),1,0) AS `sr_meditacao`,if((`v`.`activity_id` = 3),1,0) AS `sr_recolhimento`,if((`v`.`resumo_mensal_id` in (6,7)),1,0) AS `sr_curso_basico_doutrina`,if((`v`.`activity_id` = 5),1,0) AS `sr_catequese`,if((`v`.`resumo_mensal_id` in (10,12,14)),1,0) AS `sr_circulo`,if((`v`.`activity_id` = 7),1,0) AS `sr_retiro`,if((`v`.`activity_id` = 8),1,0) AS `sr_convivio`,if((`v`.`activity_id` = 9),1,0) AS `sr_vpv` from `vw_attendance` `v` where (`v`.`center_id` = 1) ;

-- ----------------------------
-- View structure for vw_1_person
-- ----------------------------
DROP VIEW IF EXISTS `vw_1_person`;
CREATE ALGORITHM=UNDEFINED DEFINER=`bumerangue`@`%` SQL SECURITY DEFINER VIEW `vw_1_person` AS select `p`.`id` AS `id`,`p`.`center_id` AS `center_id`,`p`.`name` AS `name`,`p`.`short_name` AS `short_name`,`p`.`birthday` AS `birthday`,`p`.`phone` AS `phone`,`p`.`email` AS `email`,`p`.`check_colegial` AS `check_colegial`,`p`.`check_universitario` AS `check_universitario`,`p`.`status` AS `status`,`p`.`tag1` AS `tag1`,`p`.`tag2` AS `tag2`,`p`.`tag3` AS `tag3`,`p`.`tag4` AS `tag4`,`p`.`check_contribui` AS `check_contribui`,`p`.`check_contribui_value` AS `check_contribui_value`,`p`.`check_cooperador` AS `check_cooperador`,`p`.`check_cooperador_date` AS `check_cooperador_date`,`p`.`check_estudante_mail` AS `check_estudante_mail`,`p`.`check_estudante_mail_date` AS `check_estudante_mail_date`,`p`.`check_estudantewa` AS `check_estudantewa`,`p`.`check_estudantewadate` AS `check_estudantewadate`,`p`.`check_profissional_mail` AS `check_profissional_mail`,`p`.`check_profissional_mail_date` AS `check_profissional_mail_date`,`p`.`check_profissionalwa` AS `check_profissionalwa`,`p`.`check_profissionalwadate` AS `check_profissionalwadate`,(select max(`vw_attendance`.`date`) from `vw_attendance` where ((`vw_attendance`.`person_id` = `p`.`id`) and (`vw_attendance`.`activity_name_complement` = 'sr'))) AS `sr`,(select max(`vw_attendance`.`date`) from `vw_attendance` where ((`vw_attendance`.`person_id` = `p`.`id`) and (`vw_attendance`.`activity_name_complement` = 'sg'))) AS `sg`,(select max(`attendance`.`date`) from `attendance` where ((`attendance`.`person_id` = `p`.`id`) and (`attendance`.`activity_id` = 1))) AS `sr_direcao_espiritual`,(select max(`attendance`.`date`) from `attendance` where ((`attendance`.`person_id` = `p`.`id`) and (`attendance`.`activity_id` = 2))) AS `sr_meditacao`,(select max(`attendance`.`date`) from `attendance` where ((`attendance`.`person_id` = `p`.`id`) and (`attendance`.`activity_id` = 3))) AS `sr_recolhimento`,(select max(`attendance`.`date`) from `attendance` where ((`attendance`.`person_id` = `p`.`id`) and (`attendance`.`activity_id` = 4))) AS `sr_curso_basico_doutrina`,(select max(`attendance`.`date`) from `attendance` where ((`attendance`.`person_id` = `p`.`id`) and (`attendance`.`activity_id` = 5))) AS `sr_catequese`,(select max(`attendance`.`date`) from `attendance` where ((`attendance`.`person_id` = `p`.`id`) and (`attendance`.`activity_id` in (6,10,11)))) AS `sr_circulo`,(select max(`attendance`.`date`) from `attendance` where ((`attendance`.`person_id` = `p`.`id`) and (`attendance`.`activity_id` = 7))) AS `sr_retiro`,(select max(`attendance`.`date`) from `attendance` where ((`attendance`.`person_id` = `p`.`id`) and (`attendance`.`activity_id` = 8))) AS `sr_convivio`,(select max(`attendance`.`date`) from `attendance` where ((`attendance`.`person_id` = `p`.`id`) and (`attendance`.`activity_id` = 9))) AS `sr_vpv` from `vw_person` `p` where (`p`.`center_id` = 1) ;

-- ----------------------------
-- View structure for vw_1_person_count
-- ----------------------------
DROP VIEW IF EXISTS `vw_1_person_count`;
CREATE ALGORITHM=UNDEFINED DEFINER=`bumerangue`@`%` SQL SECURITY DEFINER VIEW `vw_1_person_count` AS select 1 AS `id`,'ps' AS `type`,count(0) AS `total`,'01. Nomes' AS `aspect` from `vw_person` where (`vw_person`.`center_id` = 1) union all select 2 AS `id`,'ps' AS `type`,count(0) AS `total`,'02. NEW' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and (`vw_person`.`status` = 0)) union all select 3 AS `id`,'ps' AS `type`,count(0) AS `total`,'03. Nomes curtos' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and (`vw_person`.`short_name` is not null)) union all select 4 AS `id`,'sr' AS `type`,count(0) AS `total`,'04. Universitários' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and (`vw_person`.`check_universitario` = 1)) union all select 5 AS `id`,'sr' AS `type`,count(0) AS `total`,'05. Colegiais' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and (`vw_person`.`check_colegial` = 1)) union all select 6 AS `id`,'ps' AS `type`,count(0) AS `total`,'06. Com telefone' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and (`vw_person`.`phone` is not null)) union all select 7 AS `id`,'ps' AS `type`,count(0) AS `total`,'07. Sem telefone' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and isnull(`vw_person`.`phone`)) union all select 8 AS `id`,'ps' AS `type`,count(0) AS `total`,'08. Com aniversário' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and (`vw_person`.`birthday` is not null)) union all select 9 AS `id`,'ps' AS `type`,count(0) AS `total`,'09. Sem aniversário' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and isnull(`vw_person`.`birthday`)) union all select 10 AS `id`,'ps' AS `type`,count(0) AS `total`,'10. Com e-mail' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and (`vw_person`.`email` is not null)) union all select 11 AS `id`,'ps' AS `type`,count(0) AS `total`,'11. Sem e-mail' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and isnull(`vw_person`.`email`)) union all select 12 AS `id`,'sr' AS `type`,count(0) AS `total`,'12. Lista do WhatsApp de sr' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and (`vw_person`.`check_estudantewa` = 1)) union all select 13 AS `id`,'sr' AS `type`,count(0) AS `total`,'13. Lista de e-mail de sr' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and (`vw_person`.`check_estudante_mail` = 1)) union all select 14 AS `id`,'sg' AS `type`,count(0) AS `total`,'14. Cooperadores' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and (`vw_person`.`check_cooperador` = 1)) union all select 15 AS `id`,'sg' AS `type`,count(0) AS `total`,'15. Cooperadores que contribuem (R$)' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and (`vw_person`.`check_contribui` = 1)) union all select 16 AS `id`,'sg' AS `type`,count(0) AS `total`,'16. Lista do WhatsApp de sg' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and (`vw_person`.`check_profissionalwa` = 1)) union all select 17 AS `id`,'sg' AS `type`,count(0) AS `total`,'17. Lista de e-mail de sg' AS `aspect` from `vw_person` where ((`vw_person`.`center_id` = 1) and (`vw_person`.`check_profissional_mail` = 1)) ;

-- ----------------------------
-- View structure for vw_1_resumo_sr
-- ----------------------------
DROP VIEW IF EXISTS `vw_1_resumo_sr`;
CREATE ALGORITHM=UNDEFINED DEFINER=`bumerangue`@`%` SQL SECURITY DEFINER VIEW `vw_1_resumo_sr` AS select `vw_resumo_sr`.`center_id` AS `center_id`,`vw_resumo_sr`.`ano` AS `ano`,`vw_resumo_sr`.`ano_mes` AS `ano_mes`,`vw_resumo_sr`.`id` AS `id`,`vw_resumo_sr`.`numero` AS `numero`,`vw_resumo_sr`.`letra` AS `letra`,`vw_resumo_sr`.`descricao` AS `descricao`,`vw_resumo_sr`.`letra_descricao_nota` AS `letra_descricao_nota`,`vw_resumo_sr`.`descricao_nota` AS `descricao_nota`,`vw_resumo_sr`.`total` AS `total` from `vw_resumo_sr` where (`vw_resumo_sr`.`center_id` in ('center_id','1')) ;

-- ----------------------------
-- View structure for vw_attendance
-- ----------------------------
DROP VIEW IF EXISTS `vw_attendance`;
CREATE ALGORITHM=UNDEFINED DEFINER=`bumerangue`@`%` SQL SECURITY DEFINER VIEW `vw_attendance` AS select `t`.`id` AS `attendance_id`,`t`.`date` AS `date`,`t`.`activity_id` AS `activity_id`,`t`.`person_id` AS `person_id`,`t`.`title` AS `title`,(case when isnull(`t`.`title`) then convert(date_format(`t`.`date`,'%Y/%m/%d (%a)') using latin1) else concat(convert(date_format(`t`.`date`,'%Y/%m/%d (%a)') using latin1),' - ',`t`.`title`) end) AS `date_and_title`,`t`.`last_edit_time` AS `last_edit_time`,`t`.`last_edit_user_id` AS `last_edit_user_id`,`u2`.`name` AS `last_edit_user_name`,`a`.`description` AS `activity_description`,`a`.`name` AS `activity_name`,`a`.`name_complement` AS `activity_name_complement`,`a`.`resumo_mensal_id` AS `resumo_mensal_id`,`p`.`name` AS `name`,`p`.`short_name` AS `short_name`,`p`.`birthday` AS `birthday`,`p`.`phone` AS `phone`,`p`.`email` AS `email`,if((`p`.`check_colegial` = 1),'1','0') AS `check_colegial`,if((`p`.`check_universitario` = 1),'1','0') AS `check_universitario`,`p`.`status` AS `status`,`p`.`tag1` AS `tag1`,`p`.`tag2` AS `tag2`,`p`.`tag3` AS `tag3`,`p`.`tag4` AS `tag4`,if((`p`.`check_contribui` = 1),'1','0') AS `check_contribui`,`p`.`check_contribui_value` AS `check_contribui_value`,if((`p`.`check_cooperador` = 1),'1','0') AS `check_cooperador`,`p`.`check_cooperador_date` AS `check_cooperador_date`,if((`p`.`check_estudante_mail` = 1),'1','0') AS `check_estudante_mail`,`p`.`check_estudante_mail_date` AS `check_estudante_mail_date`,if((`p`.`check_estudantewa` = 1),'1','0') AS `check_estudantewa`,`p`.`check_estudantewadate` AS `check_estudantewadate`,if((`p`.`check_profissional_mail` = 1),'1','0') AS `check_profissional_mail`,`p`.`check_profissional_mail_date` AS `check_profissional_mail_date`,if((`p`.`check_profissionalwa` = 1),'1','0') AS `check_profissionalwa`,`p`.`check_profissionalwadate` AS `check_profissionalwadate`,`c`.`id` AS `center_id`,`c`.`description` AS `center_description`,`c`.`name` AS `center_name`,`c`.`owner_id` AS `owner_id`,`u`.`email` AS `owner_email`,`u`.`name` AS `owner_name`,`u`.`username` AS `owner_username` from (((((`attendance` `t` join `activity` `a`) join `person` `p`) join `center` `c`) join `user` `u`) join `user` `u2`) where ((`t`.`activity_id` = `a`.`id`) and (`t`.`person_id` = `p`.`id`) and (`a`.`center_id` = `c`.`id`) and (`c`.`owner_id` = `u`.`id`) and (`t`.`last_edit_user_id` = `u2`.`id`)) ;

-- ----------------------------
-- View structure for vw_person
-- ----------------------------
DROP VIEW IF EXISTS `vw_person`;
CREATE ALGORITHM=UNDEFINED DEFINER=`bumerangue`@`%` SQL SECURITY DEFINER VIEW `vw_person` AS select `p`.`id` AS `id`,`p`.`center_id` AS `center_id`,`p`.`name` AS `name`,`p`.`short_name` AS `short_name`,`p`.`birthday` AS `birthday`,`p`.`phone` AS `phone`,`p`.`email` AS `email`,if((`p`.`check_colegial` = 1),'1','0') AS `check_colegial`,if((`p`.`check_universitario` = 1),'1','0') AS `check_universitario`,`p`.`status` AS `status`,`p`.`tag1` AS `tag1`,`p`.`tag2` AS `tag2`,`p`.`tag3` AS `tag3`,`p`.`tag4` AS `tag4`,if((`p`.`check_contribui` = 1),'1','0') AS `check_contribui`,`p`.`check_contribui_value` AS `check_contribui_value`,if((`p`.`check_cooperador` = 1),'1','0') AS `check_cooperador`,`p`.`check_cooperador_date` AS `check_cooperador_date`,if((`p`.`check_estudante_mail` = 1),'1','0') AS `check_estudante_mail`,`p`.`check_estudante_mail_date` AS `check_estudante_mail_date`,if((`p`.`check_estudantewa` = 1),'1','0') AS `check_estudantewa`,`p`.`check_estudantewadate` AS `check_estudantewadate`,if((`p`.`check_profissional_mail` = 1),'1','0') AS `check_profissional_mail`,`p`.`check_profissional_mail_date` AS `check_profissional_mail_date`,if((`p`.`check_profissionalwa` = 1),'1','0') AS `check_profissionalwa`,`p`.`check_profissionalwadate` AS `check_profissionalwadate` from `person` `p` ;

-- ----------------------------
-- View structure for vw_resumo_mensal_avg
-- ----------------------------
DROP VIEW IF EXISTS `vw_resumo_mensal_avg`;
CREATE ALGORITHM=UNDEFINED DEFINER=`bumerangue`@`%` SQL SECURITY DEFINER VIEW `vw_resumo_mensal_avg` AS select `a`.`center_id` AS `center_id`,date_format(`a`.`date`,'%Y') AS `ano`,date_format(`a`.`date`,'%Y/%m') AS `ano_mes`,`a`.`person_id` AS `person_id`,`a`.`name` AS `nome`,`a`.`check_universitario` AS `check_universitario`,`a`.`check_colegial` AS `check_colegial`,`r`.`id` AS `id`,`r`.`labor` AS `labor`,`r`.`query` AS `query`,`r`.`numero` AS `numero`,`r`.`letra` AS `letra`,`r`.`descricao` AS `descricao`,`r`.`descricao_nota` AS `descricao_nota`,`r`.`letra_descricao_nota` AS `letra_descricao_nota` from (`vw_attendance` `a` join `resumo_mensal` `r`) where (`r`.`id` = `a`.`resumo_mensal_id`) ;

-- ----------------------------
-- View structure for vw_resumo_mensal_inner
-- ----------------------------
DROP VIEW IF EXISTS `vw_resumo_mensal_inner`;
CREATE ALGORITHM=UNDEFINED DEFINER=`bumerangue`@`%` SQL SECURITY DEFINER VIEW `vw_resumo_mensal_inner` AS select distinct `a`.`center_id` AS `center_id`,date_format(`a`.`date`,'%Y') AS `ano`,date_format(`a`.`date`,'%Y/%m') AS `ano_mes`,`a`.`person_id` AS `person_id`,`a`.`name` AS `nome`,`a`.`check_universitario` AS `check_universitario`,`a`.`check_colegial` AS `check_colegial`,`r`.`id` AS `id`,`r`.`labor` AS `labor`,`r`.`query` AS `query`,`r`.`numero` AS `numero`,`r`.`letra` AS `letra`,`r`.`descricao` AS `descricao`,`r`.`descricao_nota` AS `descricao_nota`,`r`.`letra_descricao_nota` AS `letra_descricao_nota` from (`vw_attendance` `a` join `resumo_mensal` `r`) where (`r`.`id` = `a`.`resumo_mensal_id`) ;

-- ----------------------------
-- View structure for vw_resumo_sr
-- ----------------------------
DROP VIEW IF EXISTS `vw_resumo_sr`;
CREATE ALGORITHM=UNDEFINED DEFINER=`bumerangue`@`%` SQL SECURITY DEFINER VIEW `vw_resumo_sr` AS select 'center_id' AS `center_id`,'ano' AS `ano`,'ano_mes' AS `ano_mes`,`resumo_mensal`.`id` AS `id`,`resumo_mensal`.`numero` AS `numero`,`resumo_mensal`.`letra` AS `letra`,`resumo_mensal`.`descricao` AS `descricao`,`resumo_mensal`.`letra_descricao_nota` AS `letra_descricao_nota`,`resumo_mensal`.`descricao_nota` AS `descricao_nota`,0 AS `total` from `resumo_mensal` where (`resumo_mensal`.`labor` = 'sr') union all select `vw_resumo_mensal_inner`.`center_id` AS `center_id`,`vw_resumo_mensal_inner`.`ano` AS `ano`,`vw_resumo_mensal_inner`.`ano_mes` AS `ano_mes`,`vw_resumo_mensal_inner`.`id` AS `id`,`vw_resumo_mensal_inner`.`numero` AS `numero`,`vw_resumo_mensal_inner`.`letra` AS `letra`,`vw_resumo_mensal_inner`.`descricao` AS `descricao`,`vw_resumo_mensal_inner`.`letra_descricao_nota` AS `letra_descricao_nota`,`vw_resumo_mensal_inner`.`descricao_nota` AS `descricao_nota`,count(0) AS `total` from `vw_resumo_mensal_inner` where ((`vw_resumo_mensal_inner`.`id` = 4) and (`vw_resumo_mensal_inner`.`check_universitario` = 1)) group by `vw_resumo_mensal_inner`.`center_id`,`vw_resumo_mensal_inner`.`ano`,`vw_resumo_mensal_inner`.`ano_mes` union all select `v`.`center_id` AS `center_id`,`v`.`ano` AS `ano`,`v`.`ano_mes` AS `ano_mes`,(`v`.`id` + 1) AS `(id+1)`,`v`.`numero` AS `numero`,'b' AS `b`,(select `resumo_mensal`.`descricao` from `resumo_mensal` where (`resumo_mensal`.`id` = (`v`.`id` + 1))) AS `(select descricao from resumo_mensal where id = (v.id+1))`,`v`.`letra_descricao_nota` AS `letra_descricao_nota`,`v`.`descricao_nota` AS `descricao_nota`,count(0) AS `total` from `vw_resumo_mensal_inner` `v` where ((`v`.`id` = 4) and (`v`.`check_colegial` = 1)) group by `v`.`center_id`,`v`.`ano`,`v`.`ano_mes` union all select `vw_resumo_mensal_inner`.`center_id` AS `center_id`,`vw_resumo_mensal_inner`.`ano` AS `ano`,`vw_resumo_mensal_inner`.`ano_mes` AS `ano_mes`,`vw_resumo_mensal_inner`.`id` AS `id`,`vw_resumo_mensal_inner`.`numero` AS `numero`,`vw_resumo_mensal_inner`.`letra` AS `letra`,`vw_resumo_mensal_inner`.`descricao` AS `descricao`,`vw_resumo_mensal_inner`.`letra_descricao_nota` AS `letra_descricao_nota`,`vw_resumo_mensal_inner`.`descricao_nota` AS `descricao_nota`,count(0) AS `total` from `vw_resumo_mensal_inner` where ((`vw_resumo_mensal_inner`.`id` = 6) and (`vw_resumo_mensal_inner`.`check_universitario` = 1)) group by `vw_resumo_mensal_inner`.`center_id`,`vw_resumo_mensal_inner`.`ano`,`vw_resumo_mensal_inner`.`ano_mes` union all select `v`.`center_id` AS `center_id`,`v`.`ano` AS `ano`,`v`.`ano_mes` AS `ano_mes`,(`v`.`id` + 1) AS `(id+1)`,`v`.`numero` AS `numero`,'b' AS `b`,(select `resumo_mensal`.`descricao` from `resumo_mensal` where (`resumo_mensal`.`id` = (`v`.`id` + 1))) AS `(select descricao from resumo_mensal where id = (v.id+1))`,`v`.`letra_descricao_nota` AS `letra_descricao_nota`,`v`.`descricao_nota` AS `descricao_nota`,count(0) AS `total` from `vw_resumo_mensal_inner` `v` where ((`v`.`id` = 6) and (`v`.`check_colegial` = 1)) group by `v`.`center_id`,`v`.`ano`,`v`.`ano_mes` union all select `vw_resumo_mensal_inner`.`center_id` AS `center_id`,`vw_resumo_mensal_inner`.`ano` AS `ano`,`vw_resumo_mensal_inner`.`ano_mes` AS `ano_mes`,`vw_resumo_mensal_inner`.`id` AS `id`,`vw_resumo_mensal_inner`.`numero` AS `numero`,`vw_resumo_mensal_inner`.`letra` AS `letra`,`vw_resumo_mensal_inner`.`descricao` AS `descricao`,`vw_resumo_mensal_inner`.`letra_descricao_nota` AS `letra_descricao_nota`,`vw_resumo_mensal_inner`.`descricao_nota` AS `descricao_nota`,count(0) AS `total` from `vw_resumo_mensal_inner` where (`vw_resumo_mensal_inner`.`id` = 10) group by `vw_resumo_mensal_inner`.`center_id`,`vw_resumo_mensal_inner`.`ano`,`vw_resumo_mensal_inner`.`ano_mes` union all select `vw_resumo_mensal_inner`.`center_id` AS `center_id`,`vw_resumo_mensal_inner`.`ano` AS `ano`,`vw_resumo_mensal_inner`.`ano_mes` AS `ano_mes`,`vw_resumo_mensal_inner`.`id` AS `id`,`vw_resumo_mensal_inner`.`numero` AS `numero`,`vw_resumo_mensal_inner`.`letra` AS `letra`,`vw_resumo_mensal_inner`.`descricao` AS `descricao`,`vw_resumo_mensal_inner`.`letra_descricao_nota` AS `letra_descricao_nota`,`vw_resumo_mensal_inner`.`descricao_nota` AS `descricao_nota`,count(0) AS `total` from `vw_resumo_mensal_inner` where (`vw_resumo_mensal_inner`.`id` = 12) group by `vw_resumo_mensal_inner`.`center_id`,`vw_resumo_mensal_inner`.`ano`,`vw_resumo_mensal_inner`.`ano_mes` union all select `vw_resumo_mensal_inner`.`center_id` AS `center_id`,`vw_resumo_mensal_inner`.`ano` AS `ano`,`vw_resumo_mensal_inner`.`ano_mes` AS `ano_mes`,`vw_resumo_mensal_inner`.`id` AS `id`,`vw_resumo_mensal_inner`.`numero` AS `numero`,`vw_resumo_mensal_inner`.`letra` AS `letra`,`vw_resumo_mensal_inner`.`descricao` AS `descricao`,`vw_resumo_mensal_inner`.`letra_descricao_nota` AS `letra_descricao_nota`,`vw_resumo_mensal_inner`.`descricao_nota` AS `descricao_nota`,count(0) AS `total` from `vw_resumo_mensal_inner` where (`vw_resumo_mensal_inner`.`id` = 14) group by `vw_resumo_mensal_inner`.`center_id`,`vw_resumo_mensal_inner`.`ano`,`vw_resumo_mensal_inner`.`ano_mes` union all select `vw_resumo_mensal_inner`.`center_id` AS `center_id`,`vw_resumo_mensal_inner`.`ano` AS `ano`,`vw_resumo_mensal_inner`.`ano_mes` AS `ano_mes`,`vw_resumo_mensal_inner`.`id` AS `id`,`vw_resumo_mensal_inner`.`numero` AS `numero`,`vw_resumo_mensal_inner`.`letra` AS `letra`,`vw_resumo_mensal_inner`.`descricao` AS `descricao`,`vw_resumo_mensal_inner`.`letra_descricao_nota` AS `letra_descricao_nota`,`vw_resumo_mensal_inner`.`descricao_nota` AS `descricao_nota`,count(0) AS `total` from `vw_resumo_mensal_inner` where (`vw_resumo_mensal_inner`.`id` = 15) group by `vw_resumo_mensal_inner`.`center_id`,`vw_resumo_mensal_inner`.`ano`,`vw_resumo_mensal_inner`.`ano_mes` union all select `vw_resumo_mensal_inner`.`center_id` AS `center_id`,`vw_resumo_mensal_inner`.`ano` AS `ano`,`vw_resumo_mensal_inner`.`ano_mes` AS `ano_mes`,`vw_resumo_mensal_inner`.`id` AS `id`,`vw_resumo_mensal_inner`.`numero` AS `numero`,`vw_resumo_mensal_inner`.`letra` AS `letra`,`vw_resumo_mensal_inner`.`descricao` AS `descricao`,`vw_resumo_mensal_inner`.`letra_descricao_nota` AS `letra_descricao_nota`,`vw_resumo_mensal_inner`.`descricao_nota` AS `descricao_nota`,count(0) AS `total` from `vw_resumo_mensal_inner` where (`vw_resumo_mensal_inner`.`id` = 16) group by `vw_resumo_mensal_inner`.`center_id`,`vw_resumo_mensal_inner`.`ano`,`vw_resumo_mensal_inner`.`ano_mes` union all select `vw_resumo_mensal_avg`.`center_id` AS `center_id`,`vw_resumo_mensal_avg`.`ano` AS `ano`,`vw_resumo_mensal_avg`.`ano_mes` AS `ano_mes`,`vw_resumo_mensal_avg`.`id` AS `id`,`vw_resumo_mensal_avg`.`numero` AS `numero`,`vw_resumo_mensal_avg`.`letra` AS `letra`,`vw_resumo_mensal_avg`.`descricao` AS `descricao`,`vw_resumo_mensal_avg`.`letra_descricao_nota` AS `letra_descricao_nota`,`vw_resumo_mensal_avg`.`descricao_nota` AS `descricao_nota`,round((count(0) / 4),0) AS `media` from `vw_resumo_mensal_avg` where ((`vw_resumo_mensal_avg`.`id` = 17) and (`vw_resumo_mensal_avg`.`check_universitario` = 1)) group by `vw_resumo_mensal_avg`.`center_id`,`vw_resumo_mensal_avg`.`ano`,`vw_resumo_mensal_avg`.`ano_mes` union all select `v`.`center_id` AS `center_id`,`v`.`ano` AS `ano`,`v`.`ano_mes` AS `ano_mes`,(`v`.`id` + 1) AS `(id+1)`,`v`.`numero` AS `numero`,'b' AS `b`,(select `resumo_mensal`.`descricao` from `resumo_mensal` where (`resumo_mensal`.`id` = (`v`.`id` + 1))) AS `(select descricao from resumo_mensal where id = (v.id+1))`,`v`.`letra_descricao_nota` AS `letra_descricao_nota`,`v`.`descricao_nota` AS `descricao_nota`,round((count(0) / 4),0) AS `media` from `vw_resumo_mensal_avg` `v` where ((`v`.`id` = 17) and (`v`.`check_colegial` = 1)) group by `v`.`center_id`,`v`.`ano`,`v`.`ano_mes` union all select `vw_resumo_mensal_inner`.`center_id` AS `center_id`,`vw_resumo_mensal_inner`.`ano` AS `ano`,`vw_resumo_mensal_inner`.`ano_mes` AS `ano_mes`,`vw_resumo_mensal_inner`.`id` AS `id`,`vw_resumo_mensal_inner`.`numero` AS `numero`,`vw_resumo_mensal_inner`.`letra` AS `letra`,`vw_resumo_mensal_inner`.`descricao` AS `descricao`,`vw_resumo_mensal_inner`.`letra_descricao_nota` AS `letra_descricao_nota`,`vw_resumo_mensal_inner`.`descricao_nota` AS `descricao_nota`,count(0) AS `total` from `vw_resumo_mensal_inner` where (`vw_resumo_mensal_inner`.`id` = 20) group by `vw_resumo_mensal_inner`.`center_id`,`vw_resumo_mensal_inner`.`ano`,`vw_resumo_mensal_inner`.`ano_mes` union all select `vw_resumo_mensal_inner`.`center_id` AS `center_id`,`vw_resumo_mensal_inner`.`ano` AS `ano`,`vw_resumo_mensal_inner`.`ano_mes` AS `ano_mes`,`vw_resumo_mensal_inner`.`id` AS `id`,`vw_resumo_mensal_inner`.`numero` AS `numero`,`vw_resumo_mensal_inner`.`letra` AS `letra`,`vw_resumo_mensal_inner`.`descricao` AS `descricao`,`vw_resumo_mensal_inner`.`letra_descricao_nota` AS `letra_descricao_nota`,`vw_resumo_mensal_inner`.`descricao_nota` AS `descricao_nota`,count(0) AS `total` from `vw_resumo_mensal_inner` where ((`vw_resumo_mensal_inner`.`id` = 22) and (`vw_resumo_mensal_inner`.`check_universitario` = 1)) group by `vw_resumo_mensal_inner`.`center_id`,`vw_resumo_mensal_inner`.`ano`,`vw_resumo_mensal_inner`.`ano_mes` union all select `v`.`center_id` AS `center_id`,`v`.`ano` AS `ano`,`v`.`ano_mes` AS `ano_mes`,(`v`.`id` + 1) AS `(id+1)`,`v`.`numero` AS `numero`,'b' AS `b`,(select `resumo_mensal`.`descricao` from `resumo_mensal` where (`resumo_mensal`.`id` = (`v`.`id` + 1))) AS `(select descricao from resumo_mensal where id = (v.id+1))`,`v`.`letra_descricao_nota` AS `letra_descricao_nota`,`v`.`descricao_nota` AS `descricao_nota`,count(0) AS `total` from `vw_resumo_mensal_inner` `v` where ((`v`.`id` = 22) and (`v`.`check_colegial` = 1)) group by `v`.`center_id`,`v`.`ano`,`v`.`ano_mes` union all select `vw_resumo_mensal_inner`.`center_id` AS `center_id`,`vw_resumo_mensal_inner`.`ano` AS `ano`,`vw_resumo_mensal_inner`.`ano_mes` AS `ano_mes`,`vw_resumo_mensal_inner`.`id` AS `id`,`vw_resumo_mensal_inner`.`numero` AS `numero`,`vw_resumo_mensal_inner`.`letra` AS `letra`,`vw_resumo_mensal_inner`.`descricao` AS `descricao`,`vw_resumo_mensal_inner`.`letra_descricao_nota` AS `letra_descricao_nota`,`vw_resumo_mensal_inner`.`descricao_nota` AS `descricao_nota`,count(0) AS `total` from `vw_resumo_mensal_inner` where ((`vw_resumo_mensal_inner`.`id` = 24) and (`vw_resumo_mensal_inner`.`check_universitario` = 1)) group by `vw_resumo_mensal_inner`.`center_id`,`vw_resumo_mensal_inner`.`ano_mes` union all select `v`.`center_id` AS `center_id`,`v`.`ano` AS `ano`,`v`.`ano_mes` AS `ano_mes`,(`v`.`id` + 1) AS `(id+1)`,`v`.`numero` AS `numero`,'b' AS `b`,(select `resumo_mensal`.`descricao` from `resumo_mensal` where (`resumo_mensal`.`id` = (`v`.`id` + 1))) AS `(select descricao from resumo_mensal where id = (v.id+1))`,`v`.`letra_descricao_nota` AS `letra_descricao_nota`,`v`.`descricao_nota` AS `descricao_nota`,count(0) AS `total` from `vw_resumo_mensal_inner` `v` where ((`v`.`id` = 24) and (`v`.`check_colegial` = 1)) group by `v`.`center_id`,`v`.`ano`,`v`.`ano_mes` union all select `i`.`center_id` AS `center_id`,`i`.`ano` AS `ano`,`i`.`ano_mes` AS `ano_mes`,1 AS `1`,'' AS `numero`,'' AS `letra`,'* SEM LABOR' AS `descricao`,'' AS `letra_descricao_nota`,'' AS `descricao_nota`,count(0) AS `count(*)` from `vw_resumo_mensal_inner` `i` where (`i`.`id` in (select `resumo_mensal`.`id` from `resumo_mensal`) and (`i`.`check_universitario` <> 1) and (`i`.`check_colegial` <> 1)) group by `i`.`center_id`,`i`.`ano`,`i`.`ano_mes` union all select `i`.`center_id` AS `center_id`,`i`.`ano` AS `ano`,`i`.`ano_mes` AS `ano_mes`,1 AS `1`,'' AS `numero`,'' AS `letra`,'* U + C' AS `descricao`,'' AS `letra_descricao_nota`,'' AS `descricao_nota`,count(0) AS `count(*)` from `vw_resumo_mensal_inner` `i` where (`i`.`id` in (select `resumo_mensal`.`id` from `resumo_mensal`) and (`i`.`check_universitario` = 1) and (`i`.`check_colegial` = 1)) group by `i`.`center_id`,`i`.`ano`,`i`.`ano_mes` ;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE IF NOT EXISTS `mentoridge` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;
USE `mentoridge`;

CREATE TABLE IF NOT EXISTS `address` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `dong_myun_li` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
    `gu` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
    `si_gun` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
    `state` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `cancellation` (
    `cancellation_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `approved` bit(1) NOT NULL,
    `reason` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `enrollment_id` bigint(20) NOT NULL,
    PRIMARY KEY (`cancellation_id`),
    KEY `FK_CANCELLATION_ENROLLMENT_ID` (`enrollment_id`),
    CONSTRAINT `FK_CANCELLATION_ENROLLMENT_ID` FOREIGN KEY (`enrollment_id`) REFERENCES `enrollment` (`enrollment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `career` (
    `career_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `company_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `job` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `license` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `others` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `mentor_id` bigint(20) NOT NULL,
    PRIMARY KEY (`career_id`),
    KEY `FK_CAREER_MENTOR_ID` (`mentor_id`),
    CONSTRAINT `FK_CAREER_MENTOR_ID` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `chatroom` (
    `chatroom_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `enrollment_id` bigint(20) NOT NULL,
    `mentee_id` bigint(20) NOT NULL,
    `mentor_id` bigint(20) NOT NULL,
    `accused_count` int(11) NOT NULL,
    `is_closed` bit(1) NOT NULL,
    PRIMARY KEY (`chatroom_id`),
    KEY `FK_CHATROOM_ENROLLMENT_ID` (`enrollment_id`),
    KEY `FK_CHATROOM_MENTEE_ID` (`mentee_id`),
    KEY `FK_CHATROOM_MENTOR_ID` (`mentor_id`),
    CONSTRAINT `FK_CHATROOM_ENROLLMENT_ID` FOREIGN KEY (`enrollment_id`) REFERENCES `enrollment` (`enrollment_id`),
    CONSTRAINT `FK_CHATROOM_MENTEE_ID` FOREIGN KEY (`mentee_id`) REFERENCES `mentee` (`mentee_id`),
    CONSTRAINT `FK_CHATROOM_MENTOR_ID` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `education` (
    `education_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `education_level` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `major` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `others` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `school_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `mentor_id` bigint(20) NOT NULL,
    PRIMARY KEY (`education_id`),
    KEY `FK_EDUCATION_MENTOR_ID` (`mentor_id`),
    CONSTRAINT `FK_EDUCATION_MENTOR_ID` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `enrollment` (
    `enrollment_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `canceled` bit(1) NOT NULL,
    `closed` bit(1) NOT NULL,
    `lecture_id` bigint(20) NOT NULL,
    `lecture_price_id` bigint(20) NOT NULL,
    `mentee_id` bigint(20) NOT NULL,
    PRIMARY KEY (`enrollment_id`),
    KEY `FK_ENROLLMENT_LECTURE_ID` (`lecture_id`),
    KEY `FK_ENROLLMENT_LECTURE_PRICE_ID` (`lecture_price_id`),
    KEY `FK_ENROLLMENT_MENTEE_ID` (`mentee_id`),
    CONSTRAINT `FK_ENROLLMENT_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`),
    CONSTRAINT `FK_ENROLLMENT_LECTURE_PRICE_ID` FOREIGN KEY (`lecture_price_id`) REFERENCES `lecture_price` (`lecture_price_id`),
    CONSTRAINT `FK_ENROLLMENT_MENTEE_ID` FOREIGN KEY (`mentee_id`) REFERENCES `mentee` (`mentee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `inquiry` (
    `inquiry_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `content` longtext COLLATE utf8_unicode_ci NOT NULL,
    `title` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
    `type` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
    `user_id` bigint(20) NOT NULL,
    PRIMARY KEY (`inquiry_id`),
    KEY `FK_INQUIRY_USER_ID` (`user_id`),
    CONSTRAINT `FK_INQUIRY_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `lecture` (
    `lecture_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `content` longtext COLLATE utf8_unicode_ci NOT NULL,
    `difficulty_type` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
    `introduce` varchar(25) COLLATE utf8_unicode_ci NOT NULL,
    `sub_title` varchar(25) COLLATE utf8_unicode_ci NOT NULL,
    `thumbnail` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `title` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
    `mentor_id` bigint(20) NOT NULL,
    PRIMARY KEY (`lecture_id`),
    KEY `FK_LECTURE_MENTOR_ID` (`mentor_id`),
    CONSTRAINT `FK_LECTURE_MENTOR_ID` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `lecture_price` (
    `lecture_price_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `group_number` int(11) DEFAULT NULL,
    `is_group` tinyint(1) NOT NULL DEFAULT '0',
    `pertime_cost` bigint(20) NOT NULL,
    `pertime_lecture` int(11) NOT NULL,
    `total_cost` bigint(20) NOT NULL,
    `total_time` int(11) NOT NULL,
    `lecture_id` bigint(20) NOT NULL,
    PRIMARY KEY (`lecture_price_id`),
    KEY `FK_LECTURE_PRICE_LECTURE_ID` (`lecture_id`),
    CONSTRAINT `FK_LECTURE_PRICE_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `lecture_subject` (
    `lecture_subject_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `kr_subject` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
    `learning_kind` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `learning_kind_id` bigint(20) DEFAULT NULL,
    `lecture_id` bigint(20) NOT NULL,
    PRIMARY KEY (`lecture_subject_id`),
    KEY `FK_LECTURE_SUBJECT_LECTURE_ID` (`lecture_id`),
    CONSTRAINT `FK_LECTURE_SUBJECT_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `lecture_system_type` (
    `lecture_id` bigint(20) NOT NULL,
    `system_types` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    KEY `FK_LECTURE_SYSTEM_TYPE_LECTURE_ID` (`lecture_id`),
    CONSTRAINT `FK_LECTURE_SYSTEM_TYPE_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `notification` (
    `notification_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `checked` bit(1) NOT NULL,
    `checked_at` datetime DEFAULT NULL,
    `content` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `user_id` bigint(20) NOT NULL,
    PRIMARY KEY (`notification_id`),
    KEY `FK_NOTIFICATION_USER_ID` (`user_id`),
    CONSTRAINT `FK_NOTIFICATION_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `pick` (
    `pick_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `lecture_id` bigint(20) NOT NULL,
    `mentee_id` bigint(20) NOT NULL,
    PRIMARY KEY (`pick_id`),
    KEY `FK_PICK_LECTURE_ID` (`lecture_id`),
    KEY `FK_PICK_MENTEE_ID` (`mentee_id`),
    CONSTRAINT `FK_PICK_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`),
    CONSTRAINT `FK_PICK_MENTEE_ID` FOREIGN KEY (`mentee_id`) REFERENCES `mentee` (`mentee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `review` (
    `review_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `content` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `score` int(11) DEFAULT NULL,
    `enrollment_id` bigint(20) DEFAULT NULL,
    `lecture_id` bigint(20) NOT NULL,
    `parent_id` bigint(20) DEFAULT NULL,
    `user_id` bigint(20) NOT NULL,
    PRIMARY KEY (`review_id`),
    KEY `FK_REVIEW_ENROLLMENT_ID` (`enrollment_id`),
    KEY `FK_REVIEW_LECTURE_ID` (`lecture_id`),
    KEY `FK_REVIEW_PARENT_ID` (`parent_id`),
    KEY `FK_REVIEW_USER_ID` (`user_id`),
    CONSTRAINT `FK_REVIEW_ENROLLMENT_ID` FOREIGN KEY (`enrollment_id`) REFERENCES `enrollment` (`enrollment_id`),
    CONSTRAINT `FK_REVIEW_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`),
    CONSTRAINT `FK_REVIEW_PARENT_ID` FOREIGN KEY (`parent_id`) REFERENCES `review` (`review_id`),
    CONSTRAINT `FK_REVIEW_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `subject` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `kr_subject` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
    `learning_kind` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `learning_kind_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `mentee` (
    `mentee_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `subjects` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `user_id` bigint(20) NOT NULL,
    PRIMARY KEY (`mentee_id`),
    KEY `FK_MENTEE_USER_ID` (`user_id`),
    CONSTRAINT `FK_MENTEE_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `mentor` (
    `mentor_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `user_id` bigint(20) NOT NULL,
    PRIMARY KEY (`mentor_id`),
    KEY `FK_MENTOR_USER_ID` (`user_id`),
    CONSTRAINT `FK_MENTOR_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `mentoridge_file` (
    `file_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `content_type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `size` bigint(20) DEFAULT NULL,
    `type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
    `uuid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`file_id`),
    UNIQUE KEY `UK_harlw6kblbh2lw1tcb6uvp6x6` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `user` (
    `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` datetime DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `bio` longtext COLLATE utf8_unicode_ci,
    `birth_year` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `deleted` bit(1) NOT NULL,
    `deleted_at` datetime DEFAULT NULL,
    `email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `email_verified` bit(1) NOT NULL,
    `email_verified_at` datetime DEFAULT NULL,
    `email_verify_token` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `fcm_token` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `gender` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `image` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
    `nickname` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
    `password` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
    `phone_number` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `provider` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `provider_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `quit_reason` longtext COLLATE utf8_unicode_ci,
    `role` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `username` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
    `dong_myun_li` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `si_gun_gu` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `state` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `last_login_at` datetime DEFAULT NULL,
    `accused_count` int(11) NOT NULL,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `UK_n4swgcf30j6bmtb4l4cjryuym` (`nickname`),
    UNIQUE KEY `UK_sb8bbouer5wak8vyiiy4pf2bx` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

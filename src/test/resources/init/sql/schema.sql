-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        5.7.24-log - MySQL Community Server (GPL)
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  11.1.0.6116
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 테이블 mentoridge.address 구조 내보내기
DROP TABLE IF EXISTS `address`;
CREATE TABLE IF NOT EXISTS `address` (
                                         `address_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `dong_myun_li` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `gu` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `si_gun` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `state` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         PRIMARY KEY (`address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.career 구조 내보내기
DROP TABLE IF EXISTS `career`;
CREATE TABLE IF NOT EXISTS `career` (
                                        `career_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                        `created_at` datetime DEFAULT NULL,
                                        `updated_at` datetime DEFAULT NULL,
                                        `company_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `job` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `license` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `others` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `mentor_id` bigint(20) NOT NULL,
                                        PRIMARY KEY (`career_id`),
                                        KEY `FK_CAREER_MENTOR_ID` (`mentor_id`),
                                        CONSTRAINT `FK_CAREER_MENTOR_ID` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.chatroom 구조 내보내기
DROP TABLE IF EXISTS `chatroom`;
CREATE TABLE IF NOT EXISTS `chatroom` (
                                          `chatroom_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                          `created_at` datetime DEFAULT NULL,
                                          `updated_at` datetime DEFAULT NULL,
                                          `accused_count` int(11) NOT NULL,
                                          `closed` tinyint(1) NOT NULL DEFAULT '0',
                                          `mentee_id` bigint(20) NOT NULL,
                                          `mentor_id` bigint(20) NOT NULL,
                                          PRIMARY KEY (`chatroom_id`),
                                          KEY `FK_CHATROOM_MENTEE_ID` (`mentee_id`),
                                          KEY `FK_CHATROOM_MENTOR_ID` (`mentor_id`),
                                          CONSTRAINT `FK_CHATROOM_MENTEE_ID` FOREIGN KEY (`mentee_id`) REFERENCES `mentee` (`mentee_id`),
                                          CONSTRAINT `FK_CHATROOM_MENTOR_ID` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.education 구조 내보내기
DROP TABLE IF EXISTS `education`;
CREATE TABLE IF NOT EXISTS `education` (
                                           `education_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                           `created_at` datetime DEFAULT NULL,
                                           `updated_at` datetime DEFAULT NULL,
                                           `education_level` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `major` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `others` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `school_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `mentor_id` bigint(20) NOT NULL,
                                           PRIMARY KEY (`education_id`),
                                           KEY `FK_EDUCATION_MENTOR_ID` (`mentor_id`),
                                           CONSTRAINT `FK_EDUCATION_MENTOR_ID` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.enrollment 구조 내보내기
DROP TABLE IF EXISTS `enrollment`;
CREATE TABLE IF NOT EXISTS `enrollment` (
                                            `enrollment_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                            `created_at` datetime DEFAULT NULL,
                                            `updated_at` datetime DEFAULT NULL,
                                            `checked` tinyint(1) NOT NULL DEFAULT '0',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.inquiry 구조 내보내기
DROP TABLE IF EXISTS `inquiry`;
CREATE TABLE IF NOT EXISTS `inquiry` (
                                         `inquiry_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `created_at` datetime DEFAULT NULL,
                                         `updated_at` datetime DEFAULT NULL,
                                         `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `user_id` bigint(20) NOT NULL,
                                         PRIMARY KEY (`inquiry_id`),
                                         KEY `FK_INQUIRY_USER_ID` (`user_id`),
                                         CONSTRAINT `FK_INQUIRY_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.lecture 구조 내보내기
DROP TABLE IF EXISTS `lecture`;
CREATE TABLE IF NOT EXISTS `lecture` (
                                         `lecture_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `created_at` datetime DEFAULT NULL,
                                         `updated_at` datetime DEFAULT NULL,
                                         `approved` tinyint(1) NOT NULL DEFAULT '0',
                                         `closed` tinyint(1) NOT NULL DEFAULT '0',
                                         `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `difficulty` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `introduce` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `sub_title` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `thumbnail` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `title` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `mentor_id` bigint(20) NOT NULL,
                                         PRIMARY KEY (`lecture_id`),
                                         KEY `FK_LECTURE_MENTOR_ID` (`mentor_id`),
                                         CONSTRAINT `FK_LECTURE_MENTOR_ID` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.lecture_price 구조 내보내기
DROP TABLE IF EXISTS `lecture_price`;
CREATE TABLE IF NOT EXISTS `lecture_price` (
                                               `lecture_price_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                               `created_at` datetime DEFAULT NULL,
                                               `updated_at` datetime DEFAULT NULL,
                                               `is_group` tinyint(1) NOT NULL DEFAULT '0',
                                               `number_of_lectures` int(11) NOT NULL,
                                               `number_of_members` int(11) DEFAULT NULL,
                                               `price_per_hour` bigint(20) NOT NULL,
                                               `time_per_lecture` int(11) NOT NULL,
                                               `total_price` bigint(20) NOT NULL,
                                               `lecture_id` bigint(20) NOT NULL,
                                               PRIMARY KEY (`lecture_price_id`),
                                               KEY `FK_LECTURE_PRICE_LECTURE_ID` (`lecture_id`),
                                               CONSTRAINT `FK_LECTURE_PRICE_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.lecture_subject 구조 내보내기
DROP TABLE IF EXISTS `lecture_subject`;
CREATE TABLE IF NOT EXISTS `lecture_subject` (
                                                 `lecture_subject_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                 `created_at` datetime DEFAULT NULL,
                                                 `updated_at` datetime DEFAULT NULL,
                                                 `lecture_id` bigint(20) NOT NULL,
                                                 `subject_id` bigint(20) NOT NULL,
                                                 PRIMARY KEY (`lecture_subject_id`),
                                                 KEY `FK_LECTURE_SUBJECT_LECTURE_ID` (`lecture_id`),
                                                 KEY `FK_LECTURE_SUBJECT_SUBJECT_ID` (`subject_id`),
                                                 CONSTRAINT `FK_LECTURE_SUBJECT_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`),
                                                 CONSTRAINT `FK_LECTURE_SUBJECT_SUBJECT_ID` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`subject_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.lecture_system_type 구조 내보내기
DROP TABLE IF EXISTS `lecture_system_type`;
CREATE TABLE IF NOT EXISTS `lecture_system_type` (
                                                     `lecture_id` bigint(20) NOT NULL,
                                                     `systems` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                     KEY `FK_LECTURE_SYSTEM_TYPE_LECTURE_ID` (`lecture_id`),
                                                     CONSTRAINT `FK_LECTURE_SYSTEM_TYPE_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.mentee 구조 내보내기
DROP TABLE IF EXISTS `mentee`;
CREATE TABLE IF NOT EXISTS `mentee` (
                                        `mentee_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                        `created_at` datetime DEFAULT NULL,
                                        `updated_at` datetime DEFAULT NULL,
                                        `subjects` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `user_id` bigint(20) NOT NULL,
                                        PRIMARY KEY (`mentee_id`),
                                        KEY `FK_MENTEE_USER_ID` (`user_id`),
                                        CONSTRAINT `FK_MENTEE_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.mentor 구조 내보내기
DROP TABLE IF EXISTS `mentor`;
CREATE TABLE IF NOT EXISTS `mentor` (
                                        `mentor_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                        `created_at` datetime DEFAULT NULL,
                                        `updated_at` datetime DEFAULT NULL,
                                        `bio` longtext COLLATE utf8mb4_unicode_ci,
                                        `user_id` bigint(20) NOT NULL,
                                        PRIMARY KEY (`mentor_id`),
                                        KEY `FK_MENTOR_USER_ID` (`user_id`),
                                        CONSTRAINT `FK_MENTOR_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.mentoridge_file 구조 내보내기
DROP TABLE IF EXISTS `mentoridge_file`;
CREATE TABLE IF NOT EXISTS `mentoridge_file` (
                                                 `file_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                 `created_at` datetime DEFAULT NULL,
                                                 `updated_at` datetime DEFAULT NULL,
                                                 `content_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                 `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                 `size` bigint(20) DEFAULT NULL,
                                                 `type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                 `uuid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                 PRIMARY KEY (`file_id`),
                                                 UNIQUE KEY `UK_5f3mg4575qo3w5mhoogcw3wey` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.notification 구조 내보내기
DROP TABLE IF EXISTS `notification`;
CREATE TABLE IF NOT EXISTS `notification` (
                                              `notification_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                              `created_at` datetime DEFAULT NULL,
                                              `updated_at` datetime DEFAULT NULL,
                                              `checked` tinyint(1) NOT NULL DEFAULT '0',
                                              `checked_at` datetime DEFAULT NULL,
                                              `content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `user_id` bigint(20) NOT NULL,
                                              PRIMARY KEY (`notification_id`),
                                              KEY `FK_NOTIFICATION_USER_ID` (`user_id`),
                                              CONSTRAINT `FK_NOTIFICATION_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.pick 구조 내보내기
DROP TABLE IF EXISTS `pick`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.review 구조 내보내기
DROP TABLE IF EXISTS `review`;
CREATE TABLE IF NOT EXISTS `review` (
                                        `review_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                        `created_at` datetime DEFAULT NULL,
                                        `updated_at` datetime DEFAULT NULL,
                                        `content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.subject 구조 내보내기
DROP TABLE IF EXISTS `subject`;
CREATE TABLE IF NOT EXISTS `subject` (
                                         `subject_id` bigint(20) NOT NULL,
                                         `kr_subject` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `learning_kind` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         PRIMARY KEY (`subject_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.user 구조 내보내기
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
                                      `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                      `created_at` datetime DEFAULT NULL,
                                      `updated_at` datetime DEFAULT NULL,
                                      `accused_count` int(11) NOT NULL,
                                      `birth_year` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `deleted` tinyint(1) NOT NULL DEFAULT '0',
                                      `deleted_at` datetime DEFAULT NULL,
                                      `email_verified` tinyint(1) NOT NULL DEFAULT '0',
                                      `email_verified_at` datetime DEFAULT NULL,
                                      `email_verify_token` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `fcm_token` longtext COLLATE utf8mb4_unicode_ci,
                                      `gender` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `last_login_at` datetime DEFAULT NULL,
                                      `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `nickname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `phone_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `provider` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `provider_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `quit_reason` longtext COLLATE utf8mb4_unicode_ci,
                                      `role` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `dong_myun_li` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `si_gun_gu` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `state` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      PRIMARY KEY (`user_id`),
                                      UNIQUE KEY `UK_n4swgcf30j6bmtb4l4cjryuym` (`nickname`),
                                      UNIQUE KEY `UK_sb8bbouer5wak8vyiiy4pf2bx` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

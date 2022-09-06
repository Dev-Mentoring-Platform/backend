-- --------------------------------------------------------
-- 호스트:                          3.37.39.47
-- 서버 버전:                        8.0.30-0ubuntu0.20.04.2 - (Ubuntu)
-- 서버 OS:                        Linux
-- HeidiSQL 버전:                  10.3.0.5771
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 테이블 mentoridge.address 구조 내보내기
DROP TABLE IF EXISTS `address`;
CREATE TABLE IF NOT EXISTS `address` (
  `address_id` bigint NOT NULL AUTO_INCREMENT,
  `dong_myun_li` varchar(50) DEFAULT NULL,
  `gu` varchar(50) DEFAULT NULL,
  `si_gun` varchar(50) DEFAULT NULL,
  `state` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`address_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20280 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.career 구조 내보내기
DROP TABLE IF EXISTS `career`;
CREATE TABLE IF NOT EXISTS `career` (
  `career_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `company_name` varchar(255) DEFAULT NULL,
  `job` varchar(255) DEFAULT NULL,
  `license` varchar(255) DEFAULT NULL,
  `others` varchar(255) DEFAULT NULL,
  `mentor_id` bigint NOT NULL,
  PRIMARY KEY (`career_id`),
  KEY `FK_CAREER_MENTOR_ID` (`mentor_id`),
  CONSTRAINT `FK_CAREER_MENTOR_ID` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=432 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.chatroom 구조 내보내기
DROP TABLE IF EXISTS `chatroom`;
CREATE TABLE IF NOT EXISTS `chatroom` (
  `chatroom_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `accused_count` int NOT NULL,
  `closed` tinyint(1) NOT NULL DEFAULT '0',
  `mentee_id` bigint NOT NULL,
  `mentor_id` bigint NOT NULL,
  `mentee_in` tinyint(1) NOT NULL DEFAULT '0',
  `mentor_in` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`chatroom_id`),
  KEY `FK_CHATROOM_MENTEE_ID` (`mentee_id`),
  KEY `FK_CHATROOM_MENTOR_ID` (`mentor_id`),
  CONSTRAINT `FK_CHATROOM_MENTEE_ID` FOREIGN KEY (`mentee_id`) REFERENCES `mentee` (`mentee_id`),
  CONSTRAINT `FK_CHATROOM_MENTOR_ID` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=138 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.comment 구조 내보내기
DROP TABLE IF EXISTS `comment`;
CREATE TABLE IF NOT EXISTS `comment` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `content` longtext,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `FK_COMMENT_POST_ID` (`post_id`),
  KEY `FK_COMMENT_USER_ID` (`user_id`),
  CONSTRAINT `FK_COMMENT_POST_ID` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`),
  CONSTRAINT `FK_COMMENT_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.education 구조 내보내기
DROP TABLE IF EXISTS `education`;
CREATE TABLE IF NOT EXISTS `education` (
  `education_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `education_level` varchar(255) DEFAULT NULL,
  `major` varchar(255) DEFAULT NULL,
  `others` varchar(255) DEFAULT NULL,
  `school_name` varchar(255) DEFAULT NULL,
  `mentor_id` bigint NOT NULL,
  PRIMARY KEY (`education_id`),
  KEY `FK_EDUCATION_MENTOR_ID` (`mentor_id`),
  CONSTRAINT `FK_EDUCATION_MENTOR_ID` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=416 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.enrollment 구조 내보내기
DROP TABLE IF EXISTS `enrollment`;
CREATE TABLE IF NOT EXISTS `enrollment` (
  `enrollment_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `checked` tinyint(1) NOT NULL DEFAULT '0',
  `checked_at` datetime DEFAULT NULL,
  `lecture_id` bigint NOT NULL,
  `lecture_price_id` bigint NOT NULL,
  `mentee_id` bigint NOT NULL,
  `finished` tinyint(1) NOT NULL DEFAULT '0',
  `finished_at` datetime DEFAULT NULL,
  PRIMARY KEY (`enrollment_id`),
  KEY `FK_ENROLLMENT_LECTURE_ID` (`lecture_id`),
  KEY `FK_ENROLLMENT_LECTURE_PRICE_ID` (`lecture_price_id`),
  KEY `FK_ENROLLMENT_MENTEE_ID` (`mentee_id`),
  CONSTRAINT `FK_ENROLLMENT_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`),
  CONSTRAINT `FK_ENROLLMENT_LECTURE_PRICE_ID` FOREIGN KEY (`lecture_price_id`) REFERENCES `lecture_price` (`lecture_price_id`),
  CONSTRAINT `FK_ENROLLMENT_MENTEE_ID` FOREIGN KEY (`mentee_id`) REFERENCES `mentee` (`mentee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=505 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.inquiry 구조 내보내기
DROP TABLE IF EXISTS `inquiry`;
CREATE TABLE IF NOT EXISTS `inquiry` (
  `inquiry_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `content` longtext NOT NULL,
  `title` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`inquiry_id`),
  KEY `FK_INQUIRY_USER_ID` (`user_id`),
  CONSTRAINT `FK_INQUIRY_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.lecture 구조 내보내기
DROP TABLE IF EXISTS `lecture`;
CREATE TABLE IF NOT EXISTS `lecture` (
  `lecture_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `approved` tinyint(1) NOT NULL DEFAULT '0',
  `content` longtext NOT NULL,
  `difficulty` varchar(20) NOT NULL,
  `introduce` varchar(25) NOT NULL,
  `sub_title` varchar(25) NOT NULL,
  `thumbnail` varchar(255) DEFAULT NULL,
  `title` varchar(40) NOT NULL,
  `mentor_id` bigint NOT NULL,
  PRIMARY KEY (`lecture_id`),
  KEY `FK_LECTURE_MENTOR_ID` (`mentor_id`),
  CONSTRAINT `FK_LECTURE_MENTOR_ID` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=274 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.lecture_price 구조 내보내기
DROP TABLE IF EXISTS `lecture_price`;
CREATE TABLE IF NOT EXISTS `lecture_price` (
  `lecture_price_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `is_group` tinyint(1) NOT NULL DEFAULT '0',
  `number_of_lectures` int NOT NULL,
  `number_of_members` int DEFAULT NULL,
  `price_per_hour` bigint NOT NULL,
  `time_per_lecture` int NOT NULL,
  `total_price` bigint NOT NULL,
  `lecture_id` bigint NOT NULL,
  `closed` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`lecture_price_id`),
  KEY `FK_LECTURE_PRICE_LECTURE_ID` (`lecture_id`),
  CONSTRAINT `FK_LECTURE_PRICE_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`)
) ENGINE=InnoDB AUTO_INCREMENT=391 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.lecture_subject 구조 내보내기
DROP TABLE IF EXISTS `lecture_subject`;
CREATE TABLE IF NOT EXISTS `lecture_subject` (
  `lecture_subject_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `lecture_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  PRIMARY KEY (`lecture_subject_id`),
  KEY `FK_LECTURE_SUBJECT_LECTURE_ID` (`lecture_id`),
  KEY `FK_LECTURE_SUBJECT_SUBJECT_ID` (`subject_id`),
  CONSTRAINT `FK_LECTURE_SUBJECT_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`),
  CONSTRAINT `FK_LECTURE_SUBJECT_SUBJECT_ID` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`subject_id`)
) ENGINE=InnoDB AUTO_INCREMENT=312 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.lecture_system_type 구조 내보내기
DROP TABLE IF EXISTS `lecture_system_type`;
CREATE TABLE IF NOT EXISTS `lecture_system_type` (
  `lecture_id` bigint NOT NULL,
  `systems` varchar(255) DEFAULT NULL,
  KEY `FK_LECTURE_SYSTEM_TYPE_LECTURE_ID` (`lecture_id`),
  CONSTRAINT `FK_LECTURE_SYSTEM_TYPE_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.liking 구조 내보내기
DROP TABLE IF EXISTS `liking`;
CREATE TABLE IF NOT EXISTS `liking` (
  `liking_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`liking_id`),
  KEY `FK_LIKING_POST_ID` (`post_id`),
  KEY `FK_LIKING_USER_ID` (`user_id`),
  CONSTRAINT `FK_LIKING_POST_ID` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`),
  CONSTRAINT `FK_LIKING_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.mentee 구조 내보내기
DROP TABLE IF EXISTS `mentee`;
CREATE TABLE IF NOT EXISTS `mentee` (
  `mentee_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `subjects` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`mentee_id`),
  KEY `FK_MENTEE_USER_ID` (`user_id`),
  CONSTRAINT `FK_MENTEE_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1134 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.mentee_review 구조 내보내기
DROP TABLE IF EXISTS `mentee_review`;
CREATE TABLE IF NOT EXISTS `mentee_review` (
  `mentee_review_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `score` int DEFAULT NULL,
  `enrollment_id` bigint NOT NULL,
  `lecture_id` bigint NOT NULL,
  `mentee_id` bigint NOT NULL,
  PRIMARY KEY (`mentee_review_id`),
  KEY `FK_MENTEE_REVIEW_ENROLLMENT_ID` (`enrollment_id`),
  KEY `FK_MENTEE_REVIEW_LECTURE_ID` (`lecture_id`),
  KEY `FK_MENTEE_REVIEW_MENTEE_ID` (`mentee_id`),
  CONSTRAINT `FK_MENTEE_REVIEW_ENROLLMENT_ID` FOREIGN KEY (`enrollment_id`) REFERENCES `enrollment` (`enrollment_id`),
  CONSTRAINT `FK_MENTEE_REVIEW_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`),
  CONSTRAINT `FK_MENTEE_REVIEW_MENTEE_ID` FOREIGN KEY (`mentee_id`) REFERENCES `mentee` (`mentee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=393 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.mentor 구조 내보내기
DROP TABLE IF EXISTS `mentor`;
CREATE TABLE IF NOT EXISTS `mentor` (
  `mentor_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `bio` longtext,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`mentor_id`),
  KEY `FK_MENTOR_USER_ID` (`user_id`),
  CONSTRAINT `FK_MENTOR_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=365 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.mentoridge_file 구조 내보내기
DROP TABLE IF EXISTS `mentoridge_file`;
CREATE TABLE IF NOT EXISTS `mentoridge_file` (
  `file_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `size` bigint DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`file_id`),
  UNIQUE KEY `UK_5f3mg4575qo3w5mhoogcw3wey` (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=138 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.mentor_review 구조 내보내기
DROP TABLE IF EXISTS `mentor_review`;
CREATE TABLE IF NOT EXISTS `mentor_review` (
  `mentor_review_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `mentor_id` bigint NOT NULL,
  `parent_id` bigint NOT NULL,
  PRIMARY KEY (`mentor_review_id`),
  KEY `FK_MENTOR_REVIEW_MENTOR_ID` (`mentor_id`),
  KEY `FK_MENTOR_REVIEW_PARENT_ID` (`parent_id`),
  CONSTRAINT `FK_MENTOR_REVIEW_MENTOR_ID` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`),
  CONSTRAINT `FK_MENTOR_REVIEW_PARENT_ID` FOREIGN KEY (`parent_id`) REFERENCES `mentee_review` (`mentee_review_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.message 구조 내보내기
DROP TABLE IF EXISTS `message`;
CREATE TABLE IF NOT EXISTS `message` (
  `message_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `checked` bit(1) NOT NULL,
  `text` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `chatroom_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  PRIMARY KEY (`message_id`),
  KEY `FK_MESSAGE_CHATROOM_ID` (`chatroom_id`),
  KEY `FK_MESSAGE_USER_ID` (`sender_id`),
  CONSTRAINT `FK_MESSAGE_CHATROOM_ID` FOREIGN KEY (`chatroom_id`) REFERENCES `chatroom` (`chatroom_id`),
  CONSTRAINT `FK_MESSAGE_USER_ID` FOREIGN KEY (`sender_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=776 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.notice 구조 내보내기
DROP TABLE IF EXISTS `notice`;
CREATE TABLE IF NOT EXISTS `notice` (
  `notice_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `content` longtext,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.notification 구조 내보내기
DROP TABLE IF EXISTS `notification`;
CREATE TABLE IF NOT EXISTS `notification` (
  `notification_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `checked` tinyint(1) NOT NULL DEFAULT '0',
  `checked_at` datetime DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`notification_id`),
  KEY `FK_NOTIFICATION_USER_ID` (`user_id`),
  CONSTRAINT `FK_NOTIFICATION_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=720 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.pick 구조 내보내기
DROP TABLE IF EXISTS `pick`;
CREATE TABLE IF NOT EXISTS `pick` (
  `pick_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `lecture_id` bigint NOT NULL,
  `lecture_price_id` bigint NOT NULL,
  `mentee_id` bigint NOT NULL,
  PRIMARY KEY (`pick_id`),
  KEY `FK_PICK_LECTURE_ID` (`lecture_id`),
  KEY `FK_PICK_LECTURE_PRICE_ID` (`lecture_price_id`),
  KEY `FK_PICK_MENTEE_ID` (`mentee_id`),
  CONSTRAINT `FK_PICK_LECTURE_ID` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`),
  CONSTRAINT `FK_PICK_LECTURE_PRICE_ID` FOREIGN KEY (`lecture_price_id`) REFERENCES `lecture_price` (`lecture_price_id`),
  CONSTRAINT `FK_PICK_MENTEE_ID` FOREIGN KEY (`mentee_id`) REFERENCES `mentee` (`mentee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=891 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.post 구조 내보내기
DROP TABLE IF EXISTS `post`;
CREATE TABLE IF NOT EXISTS `post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `content` longtext,
  `title` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `hits` int NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`post_id`),
  KEY `FK_POST_USER_ID` (`user_id`),
  CONSTRAINT `FK_POST_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.subject 구조 내보내기
DROP TABLE IF EXISTS `subject`;
CREATE TABLE IF NOT EXISTS `subject` (
  `subject_id` bigint NOT NULL,
  `kr_subject` varchar(50) NOT NULL,
  `learning_kind` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`subject_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 mentoridge.user 구조 내보내기
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `accused_count` int NOT NULL,
  `birth_year` varchar(255) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  `email_verified` tinyint(1) NOT NULL DEFAULT '0',
  `email_verified_at` datetime DEFAULT NULL,
  `email_verify_token` varchar(255) DEFAULT NULL,
  `fcm_token` longtext,
  `gender` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `last_login_at` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `nickname` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `provider` varchar(255) DEFAULT NULL,
  `provider_id` varchar(255) DEFAULT NULL,
  `quit_reason` longtext,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `dong_myun_li` varchar(255) DEFAULT NULL,
  `si_gun_gu` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `refresh_token` longtext,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_n4swgcf30j6bmtb4l4cjryuym` (`nickname`),
  UNIQUE KEY `UK_sb8bbouer5wak8vyiiy4pf2bx` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1391 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

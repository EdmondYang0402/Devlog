-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: devlog
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `devlog`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `devlog` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `devlog`;

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `summary` varchar(500) DEFAULT NULL COMMENT '摘要',
  `content` longtext NOT NULL COMMENT '正文内容（Markdown）',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图URL',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `status` tinyint DEFAULT '1' COMMENT '0草稿 1发布',
  `view_count` bigint DEFAULT '0' COMMENT '浏览量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (13,'LeetCode1 两数之和','','<span style=\"font-size:14px;font-family:\'Noto Serif SC\', \'Songti SC\', SimSun, serif\">题目描述:\n给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。\n\n你可以假设每种输入只会对应一个答案，并且你不能使用两次相同的元素。\n\n你可以按任意顺序返回答案。</span>\n\n**解法一**:对于此问题，很容易想到使用时间复杂度为O(N^2)的暴力法，得出答案。\n\n**解法二**:整体采用一个以空间换时间的思路，时间复杂度为O(N)，空间复杂度为O(N).可以使用Hashmap，保存之前已经遇到过的数字，如果正好遇到能求和组成target的，则返回数组下标。\n\nclass Solution {\n    public int[] twoSum(int[] nums, int target) {\n        Map<Integer, Integer> map = new HashMap<>();\n\n        for (int i = 0; i < nums.length; i++) {\n            int need = target - nums[i];\n\n            if (map.containsKey(need)) {\n                return new int[]{map.get(need), i};\n            }\n\n            map.put(nums[i], i);\n        }\n\n        return new int[]{};\n    }\n}','https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/024bc112-0336-4dc2-8667-73bc74245c45.webp',1,7,1,2,'2026-07-23 20:00:00','2026-07-23 20:27:59'),(14,'LeetCode242 有效的字母异位词','','给定两个字符串 s 和 t ，编写一个函数来判断 t 是否是 s 的 字母异位词。\n\n \n示例 1:\n\n输入: s = \"anagram\", t = \"nagaram\"\n输出: true\n示例 2:\n\n输入: s = \"rat\", t = \"car\"\n输出: false\n\n乍一看没有任何思路。实际上把字符串拆成字符数组后排序就行。\nclass Solution {\n    public boolean isAnagram(String s, String t) {\n        if(s.length()!=t.length()){\n            return false;\n        }\n        char[] str1=s.toCharArray();\n        char[] str2=t.toCharArray();\n        Arrays.sort(str1);\n        Arrays.sort(str2);\n        return Arrays.equals(str1,str2);\n    }\n}','https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/c3eac500-1ecc-47ac-b5f4-ef27895e0930.webp',1,7,1,2,'2026-07-23 20:13:11','2026-07-23 20:27:59'),(15,'博客项目简介','','这个项目是本人学了并且实现b站黑马的“大事件 big_event”项目后，突发奇想，遂决定开发的。\n\n俗话说得好啊:\n\n**古人学问无遗力，\n少壮工夫老始成。\n纸上得来终觉浅，\n绝知此事要躬行。**\n\n如果不能把学到的技术应用于实践，那等于没学。但本人并不是前端程序员，所以前端只负责提需求给codex。\n后端在重复写了很多代码后，把部分开发也交给了codex，后面自己改一部分。大部分代码我都可以理解，不然真成轮椅人了。\n前端参考了一些b站优秀项目，之后会放出链接，目前仍在持续更新中，有什么意见大家也可以通过评论告诉我，谢谢大家！','https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/d7a3e030-f793-40cf-acbf-a1fe9cbb6e30.webp',1,6,1,6,'2026-07-23 20:21:00','2026-07-27 16:17:48'),(16,'关于自己访问博客卡顿的问题及分析','','我今天下午访问了一下自己的前端，突然觉得特别卡。\n\n具体来说，就是干什么都卡。鼠标滚轮往下滑的时候会有明显延迟，网页也是一段一段往下走，连背景樱花都出现了明显掉帧。\n\n我想了想，第一，我是直接通过 `localhost` 访问的，应该不存在什么网络问题。第二，我的笔记本性能也不可能差到连一个网页都带不动。\n\n因为这是我第一次做这种博客，所以一开始也没怎么注意图片资源的问题。后来检查了一下，发现轮播背景图单张居然有 5～10MB。\n\n很多地方其实根本不需要这么高的清晰度。像背景轮播，本身前面就挡着毛玻璃，背景还带模糊效果，分辨率完全可以适当降低。文章封面就更不用说了，1080p 已经绰绰有余，900p 其实也够用。\n\n整理好需求后，我就开始降低图片分辨率。但即使降到 1080p，单张图片依旧有 2～5MB，还是没有达到我的预期。\n\n后来 GPT 提醒我，问题不只是分辨率，图片格式也很重要。我原来的图片基本都是 PNG，转成 WebP 后，体积才真正明显降下来。\n\n后面就比较简单了，直接让 Codex 写一个批量转换脚本，把指定目录里的 PNG 图片统一转成 WebP。\n\n目前来看，超大 PNG 背景图很可能就是这次卡顿的主要原因。等全部换成 WebP 后，再看是否还需要继续排查动画和渲染逻辑。\n','https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/2c0e1080-e130-44f4-8e2c-0e811415347e.webp',1,5,1,3,'2026-07-23 20:57:14','2026-07-27 12:58:59'),(17,'LeetCode20 有效的括号','','<span style=\"font-size:14px;font-family:KaiTi, \'STKaiti\', serif\">给定一个只包括 \'(\'，\')\'，\'{\'，\'}\'，\'[\'，\']\' 的字符串 s ，判断字符串是否有效。\n\n有效字符串需满足：\n\n左括号必须用相同类型的右括号闭合。\n左括号必须以正确的顺序闭合。\n每个右括号都有一个对应的相同类型的左括号。</span>\n\n\n这是一道典型的栈题。\n\n首先判断字符串长度。如果长度是奇数，括号一定无法两两配对，直接返回 false。\n\n使用 HashMap 保存右括号与左括号的对应关系：\n\n) -> (\n] -> [\n} -> {\n\n遍历字符串：\n\n遇到左括号，压入栈中。\n遇到右括号，先判断栈是否为空。\n如果不为空，再判断当前右括号对应的左括号是否等于栈顶元素。\n匹配则弹出栈顶，否则返回 false。\n\n遍历结束后，如果栈为空，说明所有括号都完成匹配。\n```java\nclass Solution {\n    public boolean isValid(String s) {\n        int n = s.length();\n        if (n % 2 == 1) {\n            return false;\n        }\n\n        Map<Character, Character> pairs = new HashMap<Character, Character>() {{\n            put(\')\', \'(\');\n            put(\']\', \'[\');\n            put(\'}\', \'{\');\n        }};\n        Deque<Character> stack = new LinkedList<Character>();\n        for (int i = 0; i < n; i++) {\n            char ch = s.charAt(i);\n            if (ch == \'{\' || ch == \'(\' || ch == \'[\'){\n                 stack.push(ch);\n            }\n            else if(pairs.get(ch)==stack.peek())stack.pop();\n            else return false;\n            }\n        return stack.isEmpty();\n    }\n}\n```\n\n</span>','https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/3bf6e27d-2fef-469b-ab8b-b0feb867acd5.webp',1,7,1,2,'2026-07-24 11:27:41','2026-07-24 11:28:40'),(18,'LC219 存在重复元素','','最近HashMap相关的题目也做了有一定数量了。所以这题一眼就看出来了。\n值得注意的是:\n1.if(map.containsKey)会先于i - map.get(nums[i])执行，而后者在map为空的时候是会报nullPointerError的。\n2.还有关于key-value，令数组信息保存到hashmap中，谁做key谁做value？\n\n\n```java\nclass Solution {\n    public boolean containsNearbyDuplicate(int[] nums, int k) {\n        int n= nums.length;\n        Map <Integer,Integer> map=new HashMap<>(); \n        for(int i=0;i<n;i++){\n            if(map.containsKey(nums[i])\n            && i - map.get(nums[i]) <= k){\n                return true;\n            }\n            else{\n                map.put(nums[i],i);\n            }\n        }\n        return false;\n    }\n}\n```','https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/bebcbfa1-43ae-4a61-b598-4eadbc63cc8b.webp',1,7,1,2,'2026-07-24 12:42:48','2026-07-27 16:17:23'),(19,'博客开发记录1','','\n从六月底开始，和Codex一起做断断续续做网站差不多快一个月了，V1应该不会添加什么新功能了，文章也写了一些进去了。\n测试完加上修Bug后，该看看怎么部署上线了。\n\n<span style=\"font-size:18px;font-family:\'Noto Serif SC\', \'Songti SC\', SimSun, serif\">展望</span>\n未来可能还会加项目展示，音乐播放器，相册这种功能，后台也不是很漂亮(当然访客是看不到的)，考虑重构一下这样？\n\n\n以上','https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/f0757b83-0565-4d48-8d2c-f7018120203b.webp',1,9,1,6,'2026-07-24 13:09:54','2026-07-24 16:44:19'),(20,'关于手记','','关于手记\n\n我对于手记这一栏的定义就是，会写一些比较琐碎的东西，比如说开发进度，还有一些记录生活这样的？\n\n总之是一个比较轻型，随意的栏目。','https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/3ace0712-9b12-473c-b497-2a58894fae0c.webp',1,9,1,6,'2026-07-24 13:14:36','2026-07-27 16:18:09'),(21,'图片相关问题','','咱这个不是个二次元图片占比较大的博客嘛，自然少不了在各种图片格式，大小中做取舍，自然是分辨率越高，呈现的效果越好，但是考虑到浏览器的加载，OSS的存储等，就不能盲目的追求所谓的画质了。\n\n关于WEBP与JPG:\n一开始我以为 WebP 更像是移动端优化格式，所以 PC 页面继续使用 2K JPG 会更稳。后来查了一下才发现，WebP 本身就是面向网页图片设计的，主流 PC 浏览器早已支持。真正需要考虑的不是 PC 还是手机，而是图片用途、体积和质量。\n\nJPG 并不是不能用。如果一张 2K JPG 经过合理压缩后只有几百 KB，完全可以继续使用。WebP 也不是必须，它只是通常能在相近观感下进一步降低体积。\n\n图片格式不是越新越好，也不是所有图片都必须统一。网页背景和封面优先考虑 WebP；本地母版保留 PNG 或 JPG；截图、透明图和需要无损的素材再单独处理。','https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/629464f4-c402-47fc-859c-442e6c773fe7.webp',1,1,1,1,'2026-07-27 13:12:27','2026-07-27 13:12:53');
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_tag`
--

DROP TABLE IF EXISTS `article_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_tag` (
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`article_id`,`tag_id`),
  KEY `idx_article_tag_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_tag`
--

LOCK TABLES `article_tag` WRITE;
/*!40000 ALTER TABLE `article_tag` DISABLE KEYS */;
INSERT INTO `article_tag` VALUES (13,9,'2026-07-23 20:00:00'),(13,11,'2026-07-23 20:00:00'),(18,9,'2026-07-24 12:54:55');
/*!40000 ALTER TABLE `article_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '????',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '????',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '??????',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='?????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'技术类',NULL,1,'2026-07-14 18:24:25','2026-07-14 18:26:31'),(5,'计算机基础','数据结构 算法等....',0,'2026-07-16 13:42:07','2026-07-23 17:52:54'),(6,'Java后端技术',NULL,0,'2026-07-23 17:53:49','2026-07-23 17:53:49'),(7,'数据结构 算法','分类下会包含具体的数据结构与算法',0,'2026-07-23 18:06:47','2026-07-23 18:06:47'),(9,'手记','项目开发、学习、生活与阶段思考',11,'2026-07-24 12:56:55','2026-07-24 12:56:55');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `article_id` bigint NOT NULL COMMENT '所属文章',
  `user_id` bigint NOT NULL COMMENT '评论用户',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID，NULL表示一级评论',
  `reply_user_id` bigint DEFAULT NULL COMMENT '回复的用户ID',
  `content` text NOT NULL COMMENT '评论内容',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '0正常 1删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_article` (`article_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `media_review`
--

DROP TABLE IF EXISTS `media_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `media_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(200) NOT NULL COMMENT '作品名称',
  `media_type` tinyint NOT NULL COMMENT '作品类型：0书籍，1电影，2番剧，3游戏',
  `status` tinyint NOT NULL COMMENT '作品状态：0计划，1进行中，2已完成，3搁置',
  `cover_url` varchar(500) DEFAULT NULL COMMENT '封面图片地址',
  `rating` tinyint DEFAULT NULL COMMENT '个人评分：1到10，1分对应半星，10分对应五星',
  `short_review` varchar(500) DEFAULT NULL COMMENT '短评',
  `content` text COMMENT '详细评价',
  `finished_date` date DEFAULT NULL COMMENT '完成日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_media_type` (`media_type`),
  KEY `idx_status` (`status`),
  KEY `idx_finished_date` (`finished_date`),
  KEY `idx_rating` (`rating`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='作品档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `media_review`
--

LOCK TABLES `media_review` WRITE;
/*!40000 ALTER TABLE `media_review` DISABLE KEYS */;
INSERT INTO `media_review` VALUES (1,'葬送的芙莉莲',2,2,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/df79c9bd-c8df-4564-9084-473f66554708.webp',9,'神中神',NULL,'2024-04-01','2026-07-21 13:52:25','2026-07-21 13:52:25'),(2,'机动战士高达 逆袭的夏亚',1,2,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/fe66d453-7d83-4ace-bca4-0b831a7f24b1.webp',9,'拉拉可是能成为我木琴的女人!!!',NULL,'2026-07-01','2026-07-21 13:54:04','2026-07-23 12:28:32'),(3,'攻壳机动队',2,2,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/acceac41-5ad0-4b97-a670-55a260c8060a.webp',10,'攻壳机动队的TV版','如果让我选一些喜欢的番剧，我会把攻壳放在TOP1。当时对未来的预言，许多在今日已经验证。在一个科学技术发达的社会中，比起技术，更值得关注的是生活在这样的社会中的人。\n义体化给人带来的是什么呢？随着肉体的不断更新，电子元件替代器官，而思想也在变化着。人又凭借什么确定自己还是原来的自己？\nGhost in the shell。\n更值得人深入思考的是，攻壳对政治的态度...\n几年前看的，还是二刷，凭借记忆写了一些，大部分细节也忘记了，总而言之，攻壳是值得多次观看的优秀番剧','2022-04-01','2026-07-23 12:35:57','2026-07-23 12:56:54'),(4,'星际牛仔 カウボーイビバップ',2,2,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/8cce9c14-a8ae-4109-82bd-4909b18ec964.webp',9,NULL,'大概是19年看的，这些经典番剧大大拔高了我滴审美，使我年纪轻轻就在成为二次元婆罗门的路上一去不复返','2019-04-01','2026-07-23 12:36:13','2026-07-23 13:02:50'),(5,'新世纪福音战士',0,0,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/fb534bf0-4862-4558-81b4-958666938493.webp',9,NULL,NULL,NULL,'2026-07-23 12:36:24','2026-07-23 12:59:14'),(6,'鸣潮',3,1,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/11fef7c8-8a50-4311-8d51-80c6f3de1a7e.jpg',9,'Hello~库狗~','2024年入坑的，记得那时候是椿的限定池子。\n整体评价这个游戏的话，就是不断地能输出7-8分的剧情，而且演出肉眼可见的越来越好，台词真的非常非常细腻，使得我在很多时候都能去共情游戏角色的处境。\n拯救文明不是一个很空泛的，宏大的话题，漂泊者只是参与了人类的自救，每一个角色的自我救赎，并非救世主。我爱那些有着喜怒哀乐的，具体的人。\n但是数值膨胀的确实厉害，1.0的C到3.0是真的用不了一点了。我记得我玩原的时候，1.0的胡桃队还可以满星深渊来着。鸣潮C位不保值倒是真的，不过咱也是个比较喜欢看剧情的，影响不大。\n整体来说还是非常推荐一玩这个游戏的，但是玩二游还是不要看社区比较好~','2024-06-01','2026-07-23 12:44:35','2026-07-23 12:58:32'),(7,'BanG Dream! It\'s MyGO!!!!!',2,2,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/274772f5-1fbb-457c-bc6c-b687774c6998.webp',8,NULL,'孩子的第一部BANG DREAM。角色和梗已经破圈了哈哈。','2024-08-01','2026-07-23 13:01:18','2026-07-23 13:01:18'),(8,'终将成为你',2,0,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/2d69e072-249d-48ce-86df-e1c51de2ae67.webp',8,NULL,'剧情差不多忘完了，只记得很甜了。这不是邓煜得菲尔茨奖了嘛23333，又想起来自己看过这部番了。',NULL,'2026-07-24 11:40:12','2026-07-24 11:40:12'),(9,'魔法少女小圆',2,2,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/7f264ea3-8eaf-4bb8-baa8-92009913a412.webp',9,NULL,'\"奇迹和魔法都是存在的，但都是有代价的\"\n披着萌萌外皮的黑深残动漫，很有老虚风格','2018-03-01','2026-07-24 11:41:28','2026-07-24 11:51:03'),(10,'魔法少女小圆 叛逆的物语',2,2,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/34653d8d-571e-433c-815c-f90a269bfd37.webp',9,NULL,NULL,NULL,'2026-07-24 12:30:39','2026-07-24 12:30:39'),(11,'魔女之旅',2,2,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/5a677458-1603-4036-a806-e816e9c13354.webp',7,NULL,'不带脑子的看还是不错滴，当年也算特别火的动漫了。伊雷娜特别可爱','2020-12-19','2026-07-24 12:31:20','2026-07-24 12:57:07'),(12,'安达与岛村',2,1,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/63dd9c6b-2601-484f-837a-1d0f06973d41.webp',8,NULL,'只看了一集，看起来像情感比较细腻的纯爱作品，期待后续展开',NULL,'2026-07-27 11:34:54','2026-07-27 11:35:06');
/*!40000 ALTER TABLE `media_review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `site_background`
--

DROP TABLE IF EXISTS `site_background`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `site_background` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用：0-禁用，1-启用',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序，数字越小越靠前',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_enabled` (`enabled`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站背景图管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `site_background`
--

LOCK TABLES `site_background` WRITE;
/*!40000 ALTER TABLE `site_background` DISABLE KEYS */;
INSERT INTO `site_background` VALUES (21,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/a498ad40-5a28-44c5-a1d2-1a63fdeb0f8f.jpg',NULL,1,1,'2026-07-24 15:10:59','2026-07-24 15:10:59'),(22,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/3d1a837d-c85b-4600-a0e9-0120c0b05d7b.jpg',NULL,1,1,'2026-07-24 15:11:24','2026-07-24 15:11:24'),(23,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/7acffe20-be9d-4f7c-a88d-3e740a12f247.jpg',NULL,1,1,'2026-07-24 15:11:54','2026-07-24 15:11:54'),(24,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/34693f99-1e3d-417f-9ab8-ff026424689d.jpg',NULL,1,1,'2026-07-24 15:12:18','2026-07-24 15:12:18'),(25,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/8d3de08d-5a25-442f-99dc-3b5b791e5d17.jpg',NULL,1,0,'2026-07-24 15:13:02','2026-07-24 15:13:02'),(26,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/be3f9d51-b76c-4708-9607-3c8599468ad3.jpg',NULL,1,0,'2026-07-24 15:15:18','2026-07-24 15:15:18'),(27,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/3f481869-2bc1-44a1-9d40-a2e58fc75f0f.jpg',NULL,1,0,'2026-07-24 15:15:36','2026-07-24 15:15:36'),(28,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/269cc509-cb3f-46d4-9293-2b377aabbfaf.jpg',NULL,1,0,'2026-07-24 15:16:56','2026-07-24 15:16:56'),(29,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/f92d88bd-f654-4003-8613-2b096ecbf411.jpg',NULL,1,0,'2026-07-24 15:17:43','2026-07-24 15:17:43'),(30,'https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/4d062e9b-a369-4111-ad8d-196a7187a394.jpg',NULL,1,0,'2026-07-24 15:18:32','2026-07-24 15:18:32');
/*!40000 ALTER TABLE `site_background` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `site_config`
--

DROP TABLE IF EXISTS `site_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `site_config` (
  `id` bigint NOT NULL COMMENT '配置ID，固定为1',
  `site_title` varchar(100) NOT NULL COMMENT '站点标题',
  `hero_subtitle` varchar(255) DEFAULT NULL COMMENT '首页副标题',
  `hero_keywords` json DEFAULT NULL COMMENT '首页展示关键词',
  `author_name` varchar(100) NOT NULL COMMENT '博主展示名称',
  `author_bio` varchar(500) DEFAULT NULL COMMENT '博主简介',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像地址',
  `profile_background_url` varchar(500) DEFAULT NULL COMMENT '资料卡背景地址',
  `announcement` varchar(1000) DEFAULT NULL COMMENT '公告',
  `github_url` varchar(500) DEFAULT NULL COMMENT 'GitHub地址',
  `gitee_url` varchar(500) DEFAULT NULL COMMENT 'Gitee地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='站点配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `site_config`
--

LOCK TABLES `site_config` WRITE;
/*!40000 ALTER TABLE `site_config` DISABLE KEYS */;
INSERT INTO `site_config` VALUES (1,'Hathaway\'s Blog','只盼来日登蜀道，再续出师表','[\"Spring Boot\", \"Java\", \"算法\"]','Hathaway','Java 后端 \n喜欢动漫和电影','https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/article/cover/2026/07/4be9dd1d-5c91-492e-a852-d7c8702b83f2.jpg',NULL,'这里是 Hathaway 的个人博客，记录技术笔记、在日生活和读书感想,还有喜欢的动漫。欢迎留言交流！',NULL,NULL,'2026-07-16 13:11:01','2026-07-21 15:24:46');
/*!40000 ALTER TABLE `site_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (1,'JWT','2026-07-16 13:41:21','2026-07-23 17:54:00'),(2,'数据结构与算法','2026-07-16 13:41:27','2026-07-16 13:41:27'),(3,'操作系统','2026-07-16 13:42:19','2026-07-16 13:42:19'),(4,'计算机网络','2026-07-16 13:42:25','2026-07-16 13:42:25'),(5,'spring boot','2026-07-16 13:42:31','2026-07-16 13:42:31'),(7,'Redis','2026-07-23 17:54:07','2026-07-23 17:54:07'),(8,'Interceptor','2026-07-23 17:54:58','2026-07-23 17:54:58'),(9,'HashMap','2026-07-23 18:06:58','2026-07-23 18:07:14'),(10,'二分查找','2026-07-23 18:07:10','2026-07-23 18:07:10'),(11,'暴力法','2026-07-23 19:51:03','2026-07-23 19:51:03'),(12,'栈','2026-07-24 11:28:10','2026-07-24 11:28:10');
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `bio` varchar(500) DEFAULT NULL,
  `role` tinyint DEFAULT '0',
  `status` tinyint DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','627635716@qq.com','$2a$10$nYDjp3O4NVBQcplXPqRZi.IWlBbw7CiEcfrEJicAi64OOXuuMRmRG','https://devlog-shanghai-hathaway.oss-cn-shanghai.aliyuncs.com/user/avatar/2026/07/f750f3c2-cfe5-4fce-b936-cc0de7c23ebc.webp','大家好这是我的个人博客....',1,0,'2026-07-01 12:13:16','2026-07-27 12:58:44'),(2,'Edmond','123456@qq.com','$2a$10$nKDtcJkOCM47FDjDy.fX6ODSHTjWRrUMf.55mtBFAJvdWIUbiQoU6',NULL,NULL,0,0,'2026-07-02 15:14:05','2026-07-02 15:14:05'),(3,'YYC123','12345@qq.com','$2a$10$TXasioQYe8rGKBfp/n88w.KUt.K3Ae1L3VjPzFv50GudvqWZ3W6PG',NULL,NULL,0,0,'2026-07-05 16:53:06','2026-07-05 16:53:06'),(10,'admin123','a627635716@gmail.com','$2a$10$smmc5ixxDG3d0qNgZSyG5.W/6GeJhZAQugmsS9kLv7EQeZbett4gK',NULL,NULL,0,0,'2026-07-10 15:19:37','2026-07-10 15:19:37'),(14,'root123','666@qq.com','$2a$10$Q.oPf4Jb01JrFnhOPmhxFOmKJFqe.Dut2gPvVlYuL2g2OMhW6Dghq',NULL,NULL,0,0,'2026-07-15 14:55:03','2026-07-15 14:55:03'),(15,'ShawnZJJ','shawnzjj@icloud.com','$2a$10$Mv4iaEH27oTC0dSHnqWS/OsByBgSy9baF9G9kk3re.xXyde2lQCKu',NULL,NULL,1,0,'2026-07-15 22:17:17','2026-07-15 22:17:17'),(16,'114514','2838291796@qq.com','$2a$10$ZLGekgo5.EdNaCiVHOadk.6354J.zH7ITbI5f/YIt9DuTmm174bW6','','这是一个一个，哼嗯哼…',1,0,'2026-07-16 22:12:52','2026-07-27 17:16:02'),(37,'YYC12345','a6276@gmail.com','$2a$10$5Tez./Pz1sPO/FItNyl/4O2Kqk44qaFBfY4Tl141IaTxhTZDUv0fa',NULL,NULL,0,0,'2026-07-24 15:49:44','2026-07-24 15:49:44'),(48,'AAA123','123@163.com','$2a$10$eo6RGiMhuFv8oA/MZfBjgOJk6LAv4Rji5x8sEmZHWCCK5zrkcXDk2',NULL,NULL,0,0,'2026-07-24 15:59:43','2026-07-24 15:59:43'),(60,'cf2607241624','cf2607241624@example.invalid','$2a$10$4Ty2Bid0YlGA4xxapLKVqepZRjOT7SvA48UaE9vbCnd6DFU9GyOsq',NULL,NULL,0,0,'2026-07-24 16:29:14','2026-07-24 16:34:59'),(61,'YYC6666','627635@qq.com','$2a$10$Es9mKBfxKAC.O2B4/RWiHeBMhvLoomkbGkgZx9pVhHb6/T0QD/EW2',NULL,NULL,0,0,'2026-07-24 16:38:41','2026-07-24 16:38:41'),(62,'asdfghjkl','1194785854@qq.com','$2a$10$DqSPnYX4GH0a1wFUbg9x8uPQuXC0BV.I61FZEQ4mvz88UBlZ3FNJ.',NULL,NULL,0,0,'2026-07-24 16:43:46','2026-07-24 16:43:46');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'devlog'
--

--
-- Dumping routines for database 'devlog'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-28 11:45:45

-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 07, 2019 at 04:59 PM
-- Server version: 10.4.6-MariaDB
-- PHP Version: 7.3.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `mi6`
--

-- --------------------------------------------------------

--
-- Table structure for table `agents`
--

CREATE TABLE `agents` (
  `id` int(10) UNSIGNED NOT NULL,
  `service_number` int(10) UNSIGNED NOT NULL,
  `secret_code` text NOT NULL,
  `active` tinyint(1) NOT NULL,
  `license_to_kill` tinyint(1) NOT NULL,
  `license_end_term` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `agents`
--

INSERT INTO `agents` (`id`, `service_number`, `secret_code`, `active`, `license_to_kill`, `license_end_term`) VALUES
(1, 7, 'For ThE Royal QUEEN', 1, 1, '2019-11-22'),
(2, 8, 'For HeR MAJESTY', 1, 0, NULL),
(5, 6, 'Diamonds ArE FOREVER', 0, 1, '2019-10-31');

-- --------------------------------------------------------

--
-- Table structure for table `logins`
--

CREATE TABLE `logins` (
  `id` int(10) UNSIGNED NOT NULL,
  `agent_id` int(10) UNSIGNED NOT NULL,
  `login_attempt` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `succeed` tinyint(1) NOT NULL,
  `timeout_duration` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `logins`
--

INSERT INTO `logins` (`id`, `agent_id`, `login_attempt`, `succeed`, `timeout_duration`) VALUES
(16, 1, '2019-10-31 13:56:48', 0, 1),
(17, 1, '2019-10-31 13:56:49', 0, 2),
(19, 1, '2019-10-31 13:57:33', 0, 8),
(20, 1, '2019-10-31 14:00:49', 0, 10),
(29, 1, '2019-11-01 08:05:48', 0, 1),
(30, 1, '2019-11-01 08:05:52', 0, 2),
(32, 1, '2019-11-01 08:06:04', 0, 4),
(60, 1, '2019-11-05 09:28:48', 1, 0),
(61, 1, '2019-11-05 09:36:45', 1, 0),
(62, 1, '2019-11-05 09:40:16', 1, 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `agents`
--
ALTER TABLE `agents`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `logins`
--
ALTER TABLE `logins`
  ADD PRIMARY KEY (`id`),
  ADD KEY `agent_id` (`agent_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `agents`
--
ALTER TABLE `agents`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `logins`
--
ALTER TABLE `logins`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=63;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `logins`
--
ALTER TABLE `logins`
  ADD CONSTRAINT `logins_ibfk_1` FOREIGN KEY (`agent_id`) REFERENCES `agents` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

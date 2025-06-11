-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 15, 2025 at 05:25 PM
-- Wersja serwera: 10.4.32-MariaDB
-- Wersja PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `sklep`
--
CREATE DATABASE IF NOT EXISTS `sklep` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `sklep`;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `szczegoly_zamowienia`
--

CREATE TABLE `szczegoly_zamowienia` (
  `id_szczegolu` int(11) NOT NULL,
  `id_zamowienia` int(11) NOT NULL,
  `id_towaru` int(11) NOT NULL,
  `ilosc` int(11) NOT NULL DEFAULT 1,
  `cena_jednostkowa` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `towary`
--

CREATE TABLE `towary` (
  `id_towaru` int(11) NOT NULL,
  `nazwa` varchar(255) NOT NULL,
  `opis` text DEFAULT NULL,
  `cena_jednostkowa` decimal(10,2) NOT NULL,
  `ilosc_dostepna` int(11) NOT NULL DEFAULT 0,
  `data_dodania` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `towary`
--

INSERT INTO `towary` (`id_towaru`, `nazwa`, `opis`, `cena_jednostkowa`, `ilosc_dostepna`, `data_dodania`) VALUES
(1, 'Laptop Lenovo ThinkPad X1 Carbon', 'Wydajny laptop biznesowy z ekranem 14 cali', 5500.00, 15, '2025-04-15 06:38:48'),
(2, 'Mysz Logitech MX Master 3S', 'Ergonomiczna mysz bezprzewodowa', 320.00, 50, '2025-04-15 06:38:48'),
(3, 'Monitor Dell UltraSharp U2723QE', 'Monitor 27 cali 4K USB-C Hub', 1800.00, 25, '2025-04-15 06:38:48'),
(4, 'Klawiatura Mechaniczna Corsair K70 RGB MK.2', 'Gamingowa klawiatura mechaniczna z podświetleniem RGB', 650.00, 30, '2025-04-15 06:38:48'),
(5, 'Słuchawki Bezprzewodowe Sony WH-1000XM5', 'Redukcja szumów, wysoka jakość dźwięku', 1400.00, 40, '2025-04-15 06:38:48'),
(6, 'Dysk SSD Samsung 980 PRO 1TB NVMe', 'Szybki dysk SSD PCIe 4.0', 450.00, 60, '2025-04-15 06:38:48'),
(7, 'Smartfon Apple iPhone 15 Pro Max 256GB', 'Najnowszy flagowy smartfon Apple', 6800.00, 10, '2025-04-15 06:38:48'),
(8, 'Tablet Samsung Galaxy Tab S9+ Wi-Fi', 'Wydajny tablet z rysikiem S Pen', 3500.00, 20, '2025-04-15 06:38:48'),
(9, 'Drukarka Laserowa Brother HL-L2350DW', 'Czarno-biała drukarka laserowa z Wi-Fi', 400.00, 35, '2025-04-15 06:38:48'),
(10, 'Router Bezprzewodowy TP-Link Archer AX55', 'Router Wi-Fi 6 Dual-Band', 280.00, 45, '2025-04-15 06:38:48'),
(11, 'Kamera Internetowa Logitech C920s HD Pro', 'Kamera internetowa Full HD 1080p', 220.00, 55, '2025-04-15 06:38:48'),
(12, 'Głośniki Komputerowe Logitech Z407 Bluetooth', 'Głośniki 2.1 z subwooferem i Bluetooth', 300.00, 30, '2025-04-15 06:38:48'),
(13, 'Pendrive SanDisk Ultra Flair 64GB USB 3.0', 'Szybki pendrive USB 3.0', 50.00, 100, '2025-04-15 06:38:48'),
(14, 'Torba na Laptopa Targus CityLite Pro 15.6\"', 'Profesjonalna torba na laptopa', 180.00, 65, '2025-04-15 06:38:48'),
(15, 'Kabel HDMI 2.1 o długości 2m', 'Kabel HDMI 8K Ultra High Speed', 40.00, 80, '2025-04-15 06:38:48'),
(16, 'fsdfsdfdsfs', 'nowa', 99.00, 555, '2025-04-15 07:40:39');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `zamowienia`
--

CREATE TABLE `zamowienia` (
  `id_zamowienia` int(11) NOT NULL,
  `id_klienta` int(11) NOT NULL,
  `data_zamowienia` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` varchar(50) DEFAULT 'Nowe',
  `wartosc_calkowita` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `zamowienia`
--

INSERT INTO `zamowienia` (`id_zamowienia`, `id_klienta`, `data_zamowienia`, `status`, `wartosc_calkowita`) VALUES
(1, 101, '2025-04-15 06:38:48', 'Nowe', NULL),
(2, 102, '2025-04-15 06:38:48', 'W realizacji', NULL),
(3, 103, '2025-04-15 06:38:48', 'Zakończone', NULL),
(4, 101, '2025-04-15 06:38:48', 'Nowe', NULL),
(5, 104, '2025-04-15 06:38:48', 'Nowe', NULL),
(6, 102, '2025-04-15 06:38:48', 'Wysłane', NULL),
(7, 105, '2025-04-15 06:38:48', 'Nowe', NULL),
(8, 103, '2025-04-15 06:38:48', 'Zwrócone', NULL),
(9, 104, '2025-04-15 06:38:48', 'Nowe', NULL),
(10, 101, '2025-04-15 06:38:48', 'Przygotowane do wysyłki', NULL);

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `szczegoly_zamowienia`
--
ALTER TABLE `szczegoly_zamowienia`
  ADD PRIMARY KEY (`id_szczegolu`),
  ADD KEY `idx_szczegoly_zamowienia_id_zamowienia` (`id_zamowienia`),
  ADD KEY `idx_szczegoly_zamowienia_id_towaru` (`id_towaru`);

--
-- Indeksy dla tabeli `towary`
--
ALTER TABLE `towary`
  ADD PRIMARY KEY (`id_towaru`);

--
-- Indeksy dla tabeli `zamowienia`
--
ALTER TABLE `zamowienia`
  ADD PRIMARY KEY (`id_zamowienia`),
  ADD KEY `idx_zamowienia_id_klienta` (`id_klienta`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `szczegoly_zamowienia`
--
ALTER TABLE `szczegoly_zamowienia`
  MODIFY `id_szczegolu` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `towary`
--
ALTER TABLE `towary`
  MODIFY `id_towaru` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `zamowienia`
--
ALTER TABLE `zamowienia`
  MODIFY `id_zamowienia` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `szczegoly_zamowienia`
--
ALTER TABLE `szczegoly_zamowienia`
  ADD CONSTRAINT `szczegoly_zamowienia_ibfk_1` FOREIGN KEY (`id_zamowienia`) REFERENCES `zamowienia` (`id_zamowienia`) ON DELETE CASCADE,
  ADD CONSTRAINT `szczegoly_zamowienia_ibfk_2` FOREIGN KEY (`id_towaru`) REFERENCES `towary` (`id_towaru`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Mar 09, 2024 at 12:27 PM
-- Wersja serwera: 8.0.36-cll-lve
-- Wersja PHP: 8.1.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `kino`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `bilet`
--

CREATE TABLE `bilet` (
  `id_biletu` int NOT NULL,
  `id_rezer` int NOT NULL,
  `id_seansu` int NOT NULL,
  `id_miejsca` int NOT NULL,
  `cena` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Struktura tabeli dla tabeli `film`
--

CREATE TABLE `film` (
  `id_filmu` int NOT NULL,
  `tytul` varchar(50) NOT NULL,
  `czas_trwania` int NOT NULL,
  `ocena` double NOT NULL,
  `opis` text NOT NULL,
  `id_gatunku` int NOT NULL,
  `okladka` mediumblob,
  `cena` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Struktura tabeli dla tabeli `gatunek`
--

CREATE TABLE `gatunek` (
  `id_gatunku` int NOT NULL,
  `nazwa_gatunku` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Struktura tabeli dla tabeli `miejsca`
--

CREATE TABLE `miejsca` (
  `id_miejsca` int NOT NULL,
  `id_sali` int NOT NULL,
  `rzad` int NOT NULL,
  `fotel` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Struktura tabeli dla tabeli `rezerwacje`
--

CREATE TABLE `rezerwacje` (
  `id_rezer` int NOT NULL,
  `nr_rezerwacji` varchar(5) NOT NULL,
  `id_uzyt` int NOT NULL,
  `data_rezer` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `kwota_rezer` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Struktura tabeli dla tabeli `sale`
--

CREATE TABLE `sale` (
  `id_sali` int NOT NULL,
  `numer` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `sale`
--

INSERT INTO `sale` (`id_sali`, `numer`) VALUES
(1, 1);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `seanse`
--

CREATE TABLE `seanse` (
  `id_seansu` int NOT NULL,
  `id_filmu` int NOT NULL,
  `id_sala` int NOT NULL DEFAULT '1',
  `data` date NOT NULL,
  `pora_emisji` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


--
-- Struktura tabeli dla tabeli `uzytkownicy`
--

CREATE TABLE `uzytkownicy` (
  `id_uzyt` int NOT NULL,
  `login` varchar(50) NOT NULL,
  `imie` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `nazwisko` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `email` varchar(70) NOT NULL,
  `haslo` varchar(40) NOT NULL,
  `adres` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `data_ur` date DEFAULT NULL,
  `numer_tel` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `bilet`
--
ALTER TABLE `bilet`
  ADD PRIMARY KEY (`id_biletu`),
  ADD KEY `id_rezer` (`id_rezer`),
  ADD KEY `id_miejsca` (`id_miejsca`),
  ADD KEY `id_seansu` (`id_seansu`);

--
-- Indeksy dla tabeli `film`
--
ALTER TABLE `film`
  ADD PRIMARY KEY (`id_filmu`),
  ADD KEY `id_gatunku` (`id_gatunku`);

--
-- Indeksy dla tabeli `gatunek`
--
ALTER TABLE `gatunek`
  ADD PRIMARY KEY (`id_gatunku`);

--
-- Indeksy dla tabeli `miejsca`
--
ALTER TABLE `miejsca`
  ADD PRIMARY KEY (`id_miejsca`),
  ADD KEY `id_sali` (`id_sali`);

--
-- Indeksy dla tabeli `rezerwacje`
--
ALTER TABLE `rezerwacje`
  ADD PRIMARY KEY (`id_rezer`),
  ADD KEY `id_hist_rezer` (`nr_rezerwacji`),
  ADD KEY `id_uzyt` (`id_uzyt`);

--
-- Indeksy dla tabeli `sale`
--
ALTER TABLE `sale`
  ADD PRIMARY KEY (`id_sali`);

--
-- Indeksy dla tabeli `seanse`
--
ALTER TABLE `seanse`
  ADD PRIMARY KEY (`id_seansu`),
  ADD KEY `id_filmu` (`id_filmu`),
  ADD KEY `id_sala` (`id_sala`);

--
-- Indeksy dla tabeli `uzytkownicy`
--
ALTER TABLE `uzytkownicy`
  ADD PRIMARY KEY (`id_uzyt`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bilet`
--
ALTER TABLE `bilet`
  MODIFY `id_biletu` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;

--
-- AUTO_INCREMENT for table `film`
--
ALTER TABLE `film`
  MODIFY `id_filmu` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=65;

--
-- AUTO_INCREMENT for table `gatunek`
--
ALTER TABLE `gatunek`
  MODIFY `id_gatunku` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `miejsca`
--
ALTER TABLE `miejsca`
  MODIFY `id_miejsca` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=61;

--
-- AUTO_INCREMENT for table `rezerwacje`
--
ALTER TABLE `rezerwacje`
  MODIFY `id_rezer` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `sale`
--
ALTER TABLE `sale`
  MODIFY `id_sali` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `seanse`
--
ALTER TABLE `seanse`
  MODIFY `id_seansu` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=65;

--
-- AUTO_INCREMENT for table `uzytkownicy`
--
ALTER TABLE `uzytkownicy`
  MODIFY `id_uzyt` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bilet`
--
ALTER TABLE `bilet`
  ADD CONSTRAINT `bilet_ibfk_2` FOREIGN KEY (`id_miejsca`) REFERENCES `miejsca` (`id_miejsca`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `bilet_ibfk_3` FOREIGN KEY (`id_seansu`) REFERENCES `seanse` (`id_seansu`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `bilet_ibfk_4` FOREIGN KEY (`id_rezer`) REFERENCES `rezerwacje` (`id_rezer`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `film`
--
ALTER TABLE `film`
  ADD CONSTRAINT `film_ibfk_1` FOREIGN KEY (`id_gatunku`) REFERENCES `gatunek` (`id_gatunku`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `miejsca`
--
ALTER TABLE `miejsca`
  ADD CONSTRAINT `miejsca_ibfk_1` FOREIGN KEY (`id_sali`) REFERENCES `sale` (`id_sali`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `rezerwacje`
--
ALTER TABLE `rezerwacje`
  ADD CONSTRAINT `rezerwacje_ibfk_6` FOREIGN KEY (`id_uzyt`) REFERENCES `uzytkownicy` (`id_uzyt`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `seanse`
--
ALTER TABLE `seanse`
  ADD CONSTRAINT `seanse_ibfk_1` FOREIGN KEY (`id_filmu`) REFERENCES `film` (`id_filmu`) ON DELETE RESTRICT ON UPDATE RESTRICT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

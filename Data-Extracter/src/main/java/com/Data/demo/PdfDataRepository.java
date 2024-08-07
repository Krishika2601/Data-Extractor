package com.Data.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PdfDataRepository extends JpaRepository<PdfData, Long> {

	List<PdfData> findBySerialNumber(String serialNumber);
}

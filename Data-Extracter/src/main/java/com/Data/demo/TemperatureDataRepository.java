package com.Data.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TemperatureDataRepository extends JpaRepository<TemperatureData, Long> {
	  @Query("SELECT DISTINCT t.serialNumber FROM TemperatureData t")
	List<String> findDistinctSerialNumbers();

	List<TemperatureData> findBySerialNumber(String serialNumber);
}
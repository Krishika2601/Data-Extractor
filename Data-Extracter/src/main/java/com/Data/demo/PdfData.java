package com.Data.demo;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pdf_data")
public class PdfData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "firmware_version")
    private String firmwareVersion;

    @Column(name = "log_cycle")
    private String logCycle;

    @Column(name = "log_interval")
    private String logInterval;

    @Column(name = "first_log")
    private String firstLog;

    @Column(name = "last_log")
    private String lastLog;

    @Column(name = "duration_time")
    private String durationTime;

    @Column(name = "data_points")
    private int dataPoints;

    @Column(name = "start_mode")
    private String startMode;

    @Column(name = "start_delay")
    private String startDelay;

    @Column(name = "stop_mode")
    private String stopMode;

    @Column(name = "highest_temperature")
    private double highestTemperature;

    @Column(name = "lowest_temperature")
    private double lowestTemperature;

    @Column(name = "average_temperature")
    private double averageTemperature;

    @Column(name = "mkt")
    private double mkt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public String getLogCycle() {
		return logCycle;
	}

	public void setLogCycle(String logCycle) {
		this.logCycle = logCycle;
	}

	public String getLogInterval() {
		return logInterval;
	}

	public void setLogInterval(String logInterval) {
		this.logInterval = logInterval;
	}

	public String getFirstLog() {
		return firstLog;
	}

	public void setFirstLog(String firstLog) {
		this.firstLog = firstLog;
	}

	public String getLastLog() {
		return lastLog;
	}

	public void setLastLog(String lastLog) {
		this.lastLog = lastLog;
	}

	public String getDurationTime() {
		return durationTime;
	}

	public void setDurationTime(String durationTime) {
		this.durationTime = durationTime;
	}

	public int getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(int dataPoints) {
		this.dataPoints = dataPoints;
	}

	public String getStartMode() {
		return startMode;
	}

	public void setStartMode(String startMode) {
		this.startMode = startMode;
	}

	public String getStartDelay() {
		return startDelay;
	}

	public void setStartDelay(String startDelay) {
		this.startDelay = startDelay;
	}

	public String getStopMode() {
		return stopMode;
	}

	public void setStopMode(String stopMode) {
		this.stopMode = stopMode;
	}

	public double getHighestTemperature() {
		return highestTemperature;
	}

	public void setHighestTemperature(double highestTemperature) {
		this.highestTemperature = highestTemperature;
	}

	public double getLowestTemperature() {
		return lowestTemperature;
	}

	public void setLowestTemperature(double lowestTemperature) {
		this.lowestTemperature = lowestTemperature;
	}

	public double getAverageTemperature() {
		return averageTemperature;
	}

	public void setAverageTemperature(double averageTemperature2) {
		this.averageTemperature = averageTemperature2;
	}

	public double getMkt() {
		return mkt;
	}

	public void setMkt(double mkt) {
		this.mkt = mkt;
	}

	public void setExtractedData(String text) {
		// TODO Auto-generated method stub
		
	}
	private LocalDateTime dateTime;
    private Double temperature;

    // Constructor, getters, and setters for dateTime and temperature

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }


    // Getters and setters
}

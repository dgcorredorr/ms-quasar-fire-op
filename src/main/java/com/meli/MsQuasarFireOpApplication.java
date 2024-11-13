package com.meli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.meli.common.configuration.GeneralConfig;
import com.meli.common.utils.log.ServiceLogger;
import com.meli.common.utils.enums.LogLevel;

@SpringBootApplication
public class MsQuasarFireOpApplication {

	private static ServiceLogger<MsQuasarFireOpApplication> logger = new ServiceLogger<>(MsQuasarFireOpApplication.class);

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		SpringApplication.run(MsQuasarFireOpApplication.class, args);
		long endTime = System.currentTimeMillis();
		long executionTime = endTime - startTime;
		String logMessage = GeneralConfig.getAppName() + " | " 
		+ GeneralConfig.getAppVersion() + " | " 
		+ GeneralConfig.getAppDescription();
		MsQuasarFireOpApplication.logger.log(logMessage, null, LogLevel.INFO, null, executionTime);
	}

}

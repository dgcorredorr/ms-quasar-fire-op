package com.meli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.meli.common.configuration.GeneralConfig;
import com.meli.common.utils.log.ServiceLogger;
import com.meli.common.utils.enums.LogLevel;

@SpringBootApplication
public class MsTemplateApplication {

	private static ServiceLogger<MsTemplateApplication> logger = new ServiceLogger<>(MsTemplateApplication.class);

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		SpringApplication.run(MsTemplateApplication.class, args);
		long endTime = System.currentTimeMillis();
		long executionTime = endTime - startTime;
		String logMessage = GeneralConfig.getAppName() + " | " 
		+ GeneralConfig.getAppVersion() + " | " 
		+ GeneralConfig.getAppDescription();
		MsTemplateApplication.logger.log(logMessage, null, LogLevel.INFO, null, executionTime);
	}

}

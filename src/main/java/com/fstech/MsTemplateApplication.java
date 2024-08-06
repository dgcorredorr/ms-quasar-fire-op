package com.fstech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fstech.common.configuration.GeneralConfig;
import com.fstech.common.utils.log.ServiceLogger;
import com.fstech.common.utils.enums.LogLevel;
import com.fstech.common.utils.enums.Task;

@SpringBootApplication
public class MsTemplateApplication {

	private static ServiceLogger<MsTemplateApplication> logger = new ServiceLogger<>(MsTemplateApplication.class);

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		SpringApplication.run(MsTemplateApplication.class, args);
		long endTime = System.currentTimeMillis();
		long executionTime = endTime - startTime;
		Task task = Task.INIT_MICROSERVICE;
		task.setOrigin(Task.Origin.builder()
				.originClass(MsTemplateApplication.class.getName())
				.originMethod("main(String[] args)")
				.build());
		String logMessage = GeneralConfig.getAppName() + " | " 
		+ GeneralConfig.getAppVersion() + " | " 
		+ GeneralConfig.getAppDescription();
		MsTemplateApplication.logger.log(logMessage, task, LogLevel.INFO, null, executionTime);
	}

}

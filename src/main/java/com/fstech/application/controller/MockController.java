package com.fstech.application.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.fstech.application.dto.GenericRequestDto;
import com.fstech.application.dto.GenericResponseDto;
import com.fstech.application.service.MessageService;
import com.fstech.common.utils.enums.MessageMapping;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@Validated
@RequestMapping("api/v1/mock")
public class MockController {

    private final MessageService messageService;

    public MockController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public Mono<ResponseEntity<GenericResponseDto>> getMock(ServerWebExchange exchange, @RequestParam String testParam1, @RequestParam(required = false) String testParam2) {
        return messageService.mapMessage(MessageMapping.DEFAULT_SUCCESS)
                .map(message -> GenericResponseDto.builder()
                        .origin(exchange.getRequest().getPath().toString())
                        .requestId(exchange.getLogPrefix())
                        .message(message)
                        .success(true)
                        .documents(String.join(" ", testParam1, testParam2))
                        .build())
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @PostMapping
    public Mono<ResponseEntity<GenericResponseDto>> postMock(ServerWebExchange exchange,
            @Valid @RequestBody Mono<GenericRequestDto> requestDtoMono) {

        return requestDtoMono
                .flatMap(requestDto -> messageService.mapMessage(MessageMapping.DEFAULT_SUCCESS)
                        .map(message -> GenericResponseDto.builder()
                                .origin(exchange.getRequest().getPath().toString())
                                .requestId(exchange.getLogPrefix())
                                .message(message)
                                .success(true)
                                .documents(requestDto.getDocuments())
                                .build())
                        .map(response -> new ResponseEntity<>(response, HttpStatus.OK)));
    }

}

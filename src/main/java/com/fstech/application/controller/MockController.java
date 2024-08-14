package com.fstech.application.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;

import com.fstech.application.dto.GenericRequestDto;
import com.fstech.application.dto.GenericResponseDto;
import com.fstech.application.dto.MockDto;
import com.fstech.application.service.MessageService;
import com.fstech.common.utils.enums.MessageMapping;
import com.fstech.provider.RestProvider;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpMethod;
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
        private final RestProvider restProvider;

        public MockController(MessageService messageService, RestProvider restProvider) {
                this.messageService = messageService;
                this.restProvider = restProvider;
        }

        @GetMapping
        public Mono<ResponseEntity<GenericResponseDto>> getMock(ServerWebExchange exchange,
                        @RequestParam Integer testParam) {

                return restProvider.sendRequest(HttpMethod.GET, "https://jsonplaceholder.typicode.com/todos/" + testParam, null,
                                MockDto.class, null, null)
                                .flatMap((ResponseEntity<MockDto> todoItem) -> {
                                        GenericResponseDto genericResponseDto = GenericResponseDto.builder()
                                                        .origin(exchange.getRequest().getPath().toString())
                                                        .requestId(exchange.getLogPrefix())
                                                        .message(messageService
                                                                        .mapMessage(MessageMapping.DEFAULT_SUCCESS))
                                                        .success(true)
                                                        .documents(todoItem.getBody())
                                                        .build();
                                        return Mono.just(new ResponseEntity<>(genericResponseDto, HttpStatus.OK));
                                });
        }

        @PostMapping
        public Mono<ResponseEntity<GenericResponseDto>> postMock(ServerWebExchange exchange,
                        @Valid @RequestBody GenericRequestDto requestDto) {

                String message = messageService.mapMessage(MessageMapping.DEFAULT_SUCCESS);

                GenericResponseDto response = GenericResponseDto.builder()
                                .origin(exchange.getRequest().getPath().toString())
                                .requestId(exchange.getLogPrefix())
                                .message(message)
                                .success(true)
                                .documents(requestDto.getDocuments())
                                .build();

                return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));

        }

}

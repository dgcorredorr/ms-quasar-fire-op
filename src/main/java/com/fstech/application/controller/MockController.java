package com.fstech.application.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fstech.common.utils.MockUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/${api.context-path}/${api.version}/${api.controller.mock}")
public class MockController {

        @Operation(summary = "Obtener un ejemplo por ID", description = "Devuelve un ejemplo basado en su ID desde el archivo mock.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "id": "123",
                                          "name": "John Doe",
                                          "email": "john.doe@example.com"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "404", description = "Ejemplo no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "Contacto no encontrado"
                                        }
                                        """)))
        })
        @GetMapping("/{id}")
        public Mono<ResponseEntity<Object>> getMockById(
                        @Parameter(description = "ID del ejemplo a buscar", required = true) @PathVariable String id) {
                Object mockData = MockUtils.getMockValue("userExample");
                return Mono.just(ResponseEntity.ok(mockData));
        }

        @Operation(summary = "Crear un nuevo ejemplo", description = "Crea un nuevo ejemplo en el sistema.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Ejemplo creado con éxito", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "id": "124",
                                          "name": "Jane Doe",
                                          "email": "jane.doe@example.com"
                                        }
                                        """)))
        })
        @PostMapping
        public Mono<ResponseEntity<Object>> createMock(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del ejemplo a crear", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "name": "Jane Doe",
                                          "email": "jane.doe@example.com"
                                        }
                                        """))) @RequestBody Map<String, Object> newMock) {
                // Lógica para crear el nuevo ejemplo
                return Mono.just(ResponseEntity.status(201).body(MockUtils.getMockValue("createUserResponseExample")));
        }

        @Operation(summary = "Actualizar un ejemplo por ID", description = "Actualiza un ejemplo existente basado en su ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ejemplo actualizado con éxito", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "id": "123",
                                          "name": "John Doe",
                                          "email": "john.doe@example.com"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "404", description = "Ejemplo no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "Contacto no encontrado"
                                        }
                                        """)))
        })
        @PutMapping("/{id}")
        public Mono<ResponseEntity<Object>> updateMock(
                        @Parameter(description = "ID del ejemplo a actualizar", required = true) @PathVariable String id,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del ejemplo", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "id": "123",
                                          "name": "John Doe",
                                          "email": "john.doe@example.com"
                                        }
                                        """))) @RequestBody Map<String, Object> updatedMock) {
                // Lógica para actualizar el ejemplo
                return Mono.just(ResponseEntity.ok(MockUtils.getMockValue("userExample")));
        }

        @Operation(summary = "Eliminar un ejemplo por ID", description = "Elimina un ejemplo existente basado en su ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Ejemplo eliminado con éxito"),
                        @ApiResponse(responseCode = "404", description = "Ejemplo no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "Contacto no encontrado"
                                        }
                                        """)))
        })
        @DeleteMapping("/{id}")
        public Mono<ResponseEntity<Void>> deleteMock(
                        @Parameter(description = "ID del ejemplo a eliminar", required = true) @PathVariable String id) {
                // Lógica para eliminar el ejemplo
                return Mono.just(ResponseEntity.noContent().build());
        }
}

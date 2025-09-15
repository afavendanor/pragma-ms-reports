package co.com.pragma.api;

import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.model.error.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET})
@Tag(name = "HealthCheckController", description = "Entrada para verificar la salud de la aplicación")
public class HealthCheckController {

    @GetMapping(value = "/healthcheck")
    @Operation(summary = "Health Check", description = "Verifica si el servicio está en funcionamiento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "El servicio está funcionando correctamente.")
    })
    public Mono<ResponseEntity<GenericResponseDTO<String>>> healthCheck() {
        return Mono.just(ResponseEntity.ok(new GenericResponseDTO<>(HttpStatus.OK, ResponseCode.MSSO001, "Service is up and running")));

    }
}
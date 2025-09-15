package co.com.pragma.api;

import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.api.dto.LoanStatsDTO;
import co.com.pragma.api.handler.LoanApplicationHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET})
@Tag(name = "LoanApplicationController", description = "Entrada para las operaciones relacionadas al modelo de solicitud")
@Validated
public class LoanApplicationController {

    private final LoanApplicationHandler loanApplicationHandler;

    @GetMapping(value = "/reports/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADVISER', 'ROLE_ADMIN')")
    @Operation(summary = "Resumen de solicitudes", description = "Permite recibir parámetros para realizar resumen de solicitudes en la app")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "El servicio responde correctamente"),
            @ApiResponse(responseCode = "400", description = "Los datos recibidos no cumplen con la obligatoriedad o formatos esperados", content = @Content(schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encuentran registros con los datos ingresados", content = @Content(schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error inesperado durante el proceso", content = @Content(schema = @Schema(implementation = GenericResponseDTO.class)))})
    public Mono<ResponseEntity<GenericResponseDTO<LoanStatsDTO>>> obtainSummary() {
        return loanApplicationHandler.obtainSummary()
                .map(genericResponseDto -> ResponseEntity.status(genericResponseDto.getResponseCode()).body(genericResponseDto));
    }

}
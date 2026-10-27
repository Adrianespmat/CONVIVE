package es.convive.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class TratamientoErrores {

    @ExceptionHandler(AccesoDenegadoException.class)
    ResponseEntity<ErrorRespuesta> accesoDenegado(AccesoDenegadoException ex, HttpServletRequest solicitud) {
        return respuesta(HttpStatus.FORBIDDEN, ex.getMessage(), solicitud);
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ErrorRespuesta> credenciales(BadCredentialsException ex, HttpServletRequest solicitud) {
        return respuesta(HttpStatus.UNAUTHORIZED, "Credenciales no válidas", solicitud);
    }

    @ExceptionHandler(ConflictoException.class)
    ResponseEntity<ErrorRespuesta> conflicto(ConflictoException ex, HttpServletRequest solicitud) {
        return respuesta(HttpStatus.CONFLICT, ex.getMessage(), solicitud);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    ResponseEntity<ErrorRespuesta> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest solicitud) {
        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage(), solicitud);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorRespuesta> validacion(MethodArgumentNotValidException ex, HttpServletRequest solicitud) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        ErrorRespuesta cuerpo = new ErrorRespuesta(Instant.now(), 400, "Datos no válidos", "Revisa los campos indicados", solicitud.getRequestURI(), campos);
        return ResponseEntity.badRequest().body(cuerpo);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ErrorRespuesta> restricciones(ConstraintViolationException ex, HttpServletRequest solicitud) {
        return respuesta(HttpStatus.BAD_REQUEST, "Los datos no cumplen las restricciones", solicitud);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorRespuesta> inesperado(Exception ex, HttpServletRequest solicitud) {
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Se ha producido un error interno", solicitud);
    }

    private ResponseEntity<ErrorRespuesta> respuesta(HttpStatus estado, String mensaje, HttpServletRequest solicitud) {
        ErrorRespuesta cuerpo = new ErrorRespuesta(Instant.now(), estado.value(), estado.getReasonPhrase(), mensaje, solicitud.getRequestURI());
        return ResponseEntity.status(estado).body(cuerpo);
    }
}

package curso.api.rest;
import java.sql.SQLException;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	// Tratamento de excessão específica quando id do usuário não for encontrado
	@ExceptionHandler(UsuarioNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUsuarioNotFoundException(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Number " + HttpStatus.NOT_FOUND.value(),
				HttpStatus.NOT_FOUND.getReasonPhrase());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}
	@ExceptionHandler(TokenValidationException.class)
	public ResponseEntity<ErrorResponse> handleTokenValidationException(TokenValidationException ex) {
	    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Number " + HttpStatus.UNAUTHORIZED.value(),
	            HttpStatus.UNAUTHORIZED.getReasonPhrase());
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}

	// Tratamento de excessões mais comuns
	@ExceptionHandler({ Exception.class, RuntimeException.class, Throwable.class })
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		String errorMessage = extractorErrorMessage(ex);
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setError(errorMessage);
		errorResponse.setCode(status.value() + " ==> " + status.getReasonPhrase());

		return new ResponseEntity<>(errorResponse, headers, status);

	}

	// Tratamento de excessões a nível de banco de dados

	@ExceptionHandler({ DataIntegrityViolationException.class, ConstraintViolationException.class, PSQLException.class,
			SQLException.class })
	protected ResponseEntity<Object> handleExceptionDataIntegry(Exception ex) {
		
		/* Insere a classe "extractorErrorMessage" que retorna os erros de cada excessão 
		na string errorMessage */
		String errorMessage = extractorErrorMessage(ex);
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setError(errorMessage);
		errorResponse.setCode(
				HttpStatus.INTERNAL_SERVER_ERROR.value() + "->" + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	// Retorna a mensagem raiz de cada erro lançado
	
	private String extractorErrorMessage(Exception ex) {
		if (ex instanceof DataIntegrityViolationException) {
				return ((DataIntegrityViolationException) ex).getCause().getCause().getMessage();
				
			} else if (ex instanceof ConstraintViolationException) {
				return((ConstraintViolationException) ex).getCause().getCause().getMessage();
			
				} else if (ex instanceof PSQLException) {
					return((PSQLException) ex).getCause().getCause().getMessage();
				
					} else if (ex instanceof SQLException) {
						return((SQLException) ex).getCause().getCause().getMessage();
						
						
							}else if (ex instanceof MethodArgumentNotValidException) {
										return((MethodArgumentNotValidException) ex).getCause().getCause().getMessage();
					
		} else {
			return ex.getMessage();
		}

	}
}


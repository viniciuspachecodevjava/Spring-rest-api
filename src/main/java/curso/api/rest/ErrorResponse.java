package curso.api.rest;

public class ErrorResponse {

	private String error;
	private String Description;
	private String code;
	
	public ErrorResponse(String error, String code, String description) {
		super();
		this.error = error;
		Description = description;
		this.code = code;
	}
	
	
	public ErrorResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	
}

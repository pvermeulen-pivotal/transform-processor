package org.springframework.cloud.stream.app.transform.processor;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class TransformProcessorProperties {
	private static final String DEFAULT_OUTPUT_SEPARATOR = ",";

	private String jsonFields;
	private String outputFieldSeparator = DEFAULT_OUTPUT_SEPARATOR;

	@NotBlank
	public String getJsonFields() {
		return jsonFields;
	}

	public void setJsonFields(String jsonFields) {
		this.jsonFields = jsonFields;
	}

	public String[] getJsonFieldArray() {
		return jsonFields.split(",");
	}

	public String getOutputFieldSeparator() {
		return outputFieldSeparator;
	}

	public void setOutputFieldSeparator(String outputFieldSeparator) {
		this.outputFieldSeparator = outputFieldSeparator;
	}

}

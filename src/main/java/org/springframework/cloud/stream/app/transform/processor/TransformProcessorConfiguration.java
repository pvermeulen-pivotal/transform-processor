package org.springframework.cloud.stream.app.transform.processor;

import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableBinding(Processor.class)
@EnableConfigurationProperties(TransformProcessorProperties.class)
public class TransformProcessorConfiguration {
	private final Logger log = LoggerFactory.getLogger(TransformProcessorConfiguration.class);
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final String QUOTE = "\"";

	@Autowired
	private TransformProcessorProperties properties;

	@SuppressWarnings("static-access")
	@Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
	public Object transform(Message<?> message) {
		StringBuilder strB = new StringBuilder();
		String[] fields = properties.getJsonFieldArray();
		log.info("Json Field Array: " + fields);

		JSONObject jsonObj;
		String str = null;
		try {
			str = makePayloadString(message);
			log.info("message payload " + str);
			jsonObj = new JSONObject(str);
			for (String field : fields) {
				strB.append(convertField(jsonObj.get(field)) + properties.getOutputFieldSeparator());
			}
			str = strB.toString().substring(0, strB.toString().length() - 1);
			log.info("output message: " + str);
			return MessageBuilder.fromMessage(message).withPayload(str).build();
		} catch (JSONException e) {
			log.error("Error converting message payload to JSON exception: " + e.getMessage() + " message: " + str);
		}
		return message;
	}

	private String convertField(Object obj) {
		if (obj == null) {
			return QUOTE + QUOTE;
		}

		if (obj instanceof String) {
			return QUOTE + (String) obj + QUOTE;
		} else {
			if (String.valueOf(obj).toUpperCase() == "NULL")
				return QUOTE + QUOTE;
			return String.valueOf(obj);
		}
	}

	private String makePayloadString(Message<?> message) {
		Object payload = message.getPayload();
		if (payload instanceof String) {
			return (String) payload;
		} else if (payload instanceof LinkedHashMap) {
			try {
				return objectMapper.writeValueAsString(payload);
			} catch (JsonProcessingException e) {
				log.error("---- json conversion exception: " + e.getMessage());
				return null;
			}
		} else if (payload instanceof byte[]) {
			return new String((byte[]) payload);
		} else {
			return null;
		}
	}

}

package com.cjremmett.application.kafka;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class KafkaProducer {

	@JsonAutoDetect
	public static class KafkaTestEvent
	{
		public String message;

		public KafkaTestEvent(
				String _message
		){
			this.message = _message;
		}

		public KafkaTestEvent(){}

		public String getMessage()
		{
			return message;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/produce-message")
	@JsonIgnoreProperties(ignoreUnknown = true)
	public void produceMessage(@RequestBody KafkaTestEvent kafkaTestEvent) {
		// do nothing
	};
}
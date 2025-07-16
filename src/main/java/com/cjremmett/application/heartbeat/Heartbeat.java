package com.cjremmett.application.heartbeat;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class Heartbeat {

	public static class HeartbeatReport
	{
		public HeartbeatReport()
		{
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			timestamp = dateFormat.format(date);
		}

		private final String timestamp;

		public String getTimestamp()
		{
			return timestamp;
		}
	}

	@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public HeartbeatReport getHeartbeat()
	{
		return new HeartbeatReport();
	}
}
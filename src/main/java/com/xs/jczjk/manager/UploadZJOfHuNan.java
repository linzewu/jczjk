package com.xs.jczjk.manager;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

@Service
public class UploadZJOfHuNan {
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Value("${jcz.url}")
	private String url;
	
	
	
	public String getAccessToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject jo =new JSONObject();
		jo.put("CompanyId", "431000019");
		jo.put("Source", "431000");
		jo.put("IPCType", "getAccessToken");
		jo.put("IPCType.value", "{\"username\":\"czjdc19\",\"password\":\"bG0eB3cD\"}");
		HttpEntity<String> request = new HttpEntity<String>("", headers);
	    return restTemplate.postForObject(url+"/restapi/detecting/get_access_token", request, String.class);
	}
	
	
	
	
	
	

}

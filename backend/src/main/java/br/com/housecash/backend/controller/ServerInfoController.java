package br.com.housecash.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.housecash.backend.service.LocaleService;

@RestController
@RequestMapping("/server/info")
@PreAuthorize("isAuthenticated()")
public class ServerInfoController {

	@Autowired
	private LocaleService localeService;

	@GetMapping("")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public Map<String, String> info() {
		Map<String, String> info = new HashMap<String, String>();
		info.put("version", "0.0.1");
		return info;
	}

	@GetMapping("/i18n")
	public String i18n() {
	    return localeService.getMessage("user.hello");
	}

}

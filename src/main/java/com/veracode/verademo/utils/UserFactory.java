package com.veracode.verademo.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserFactory {
	private static final String COOKIE_NAME = "user";

	public static User createFromRequest(HttpServletRequest req) {
		Cookie cookie = getCookieFromRequestByName(req, COOKIE_NAME);
		
		if (cookie == null) {
			return null;
		}
		
		if (cookie.getValue().isEmpty()) {
			return null;
		}
		
		InputStream stream = new ByteArrayInputStream(cookie.getValue().getBytes(StandardCharsets.UTF_8));
		stream = Base64.getDecoder().wrap(stream);
		ObjectInputStream in;
		try {
			/* START BAD CODE */
			in = new ObjectInputStream(stream);
			User user = (User) in.readObject();
			in.close();
			/* END BAD CODE */
			return user;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private static Cookie getCookieFromRequestByName(HttpServletRequest req, String username) {
		if (req == null) {
			return null;
		}
		
		Cookie[] cookies = req.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			if (cookies[i].getName().equals(username)) {
				return cookies[i];
			}
		}
		return null;
	}
	
	public static HttpServletResponse updateInResponse(User user, HttpServletResponse response) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream stream;
		try {
			stream = new ObjectOutputStream(out);
			stream.writeObject(user);
			stream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.addCookie(new Cookie(COOKIE_NAME, new String(Base64.getEncoder().encode(out.toByteArray()))));
		
		return response;
	}
}

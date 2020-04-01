package br.com.cashhouse.server.util.security;

public class SecurityAccess {

	public static enum User {
		
		MARCELO(1, "marcelo@mail.com", "test", 1),
		BIRO(2, "biro@mail.com", "test"),
		EDUARDO(3, "eduardo@mail.com", "test"),
		WILL(4, "will@mail.com", "test"),
		GABI(5, "gabi@mail.com", "test"),
		CAROL(6, "carol@mail.com", "test"),
		RAISSA(7, "rai@mail.com", "test"),
		JEAN(8, "jean@mail.com", "test", 2), 
		GRETCHEN(9, "gretchen@mail.com", "test"),
		FERNANDO(10, "fernando@mail.com", "test");
		
		private Integer id;
		private String username;
		private String password;
		private Integer dashboard;
		
		private User(Integer id, String username, String password) {
			this(id, username, password, null);
		}
		
		private User(Integer id, String username, String password, Integer dashboard) {
			this.id = id;
			this.username = username;
			this.password = password;
			this.dashboard = dashboard;
		}
		
		protected boolean hasDashboard() {
			return dashboard != null;
		}
		
		protected Integer getDashboard() {
			return dashboard;
		}
		
	}
	
	private User userLogged;
	private User userDashboard;
	
	private SecurityAccess() {
	}
	
	public static SecurityAccess login() {
		return new SecurityAccess();
	}
	
	public SecurityAccess with(User user) {
		userLogged = user;
		return this;
	}
	
	public SecurityAccess dashboard(User user) {
		
		if(userLogged == null) {
			throw new IllegalArgumentException("Undeclared User. Use first SecurityAccess.login().with(USER)");
		}
		
		if(!user.hasDashboard()) {
			throw new IllegalArgumentException(String.format("This user ( %s ) does not have Dashboard", user.name()));
		}
		
		this.userDashboard = user;
		return this;
	}

	public Integer getId() {
		if(userLogged == null) {
			throw new IllegalArgumentException("Undeclared User. Use first SecurityAccess.login().with(USER)");
		}
		return userLogged.id;
	}

	public String getUsername() {
		if(userLogged == null) {
			throw new IllegalArgumentException("Undeclared User. Use first SecurityAccess.login().with(USER)");
		}
		return userLogged.username;
	}

	public String getPassword() {
		if(userLogged == null) {
			throw new IllegalArgumentException("Undeclared User. Use first SecurityAccess.login().with(USER)");
		}
		return userLogged.password;
	}
	
	public Integer getDashboard() {
		if(userLogged == null) {
			throw new IllegalArgumentException("Undeclared User. Use first SecurityAccess.login().with(USER)");
		}
		if(userDashboard == null || !userDashboard.hasDashboard()) {
			return null;
		}
		return userDashboard.dashboard;
	}

}

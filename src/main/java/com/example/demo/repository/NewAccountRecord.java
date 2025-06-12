package com.example.demo.repository;

public class NewAccountRecord {
		private String name;
		private String id;
		private String ps;
		
		public NewAccountRecord(String name, String id, String ps) {
			//super();
			this.name = name;
			this.id = id;
			this.ps = ps;
		}
		
		public String getName() {
			return name;
		}
		
		public String getId() {
			return id;
		}
		
		public String getPs() {
			return ps;
		}

}

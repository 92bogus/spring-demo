package kr.co.mytour.learningtest.user.sqlservice;

import javax.annotation.PostConstruct;

import kr.co.mytour.learningtest.user.sqlservice.SqlRegistry.SqlNotFoundException;

public class BaseSqlService implements SqlService {

	public BaseSqlService() {
		System.out.println(getClass().getName() + " - 생성됨.");
	}

	protected SqlReader sqlReader;
	protected SqlRegistry sqlRegistry;
	
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}
	
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	@PostConstruct
	public void loadSql() {
		System.out.println(getClass().getName() + " - loadSql() called...");
		this.sqlReader.read(this.sqlRegistry);
	}
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		try {
			return this.sqlRegistry.findSql(key);
		} catch(SqlNotFoundException e) {
			throw new SqlRetrievalFailureException(e);
		}
	}

}

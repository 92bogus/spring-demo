package kr.co.mytour.learningtest.user.sqlservice;

import javax.annotation.PostConstruct;

public class DefaultSqlService extends BaseSqlService {
	public DefaultSqlService() {
		System.out.println(getClass().getName() + " - 생성됨");
		setSqlReader(new JaxbXmlSqlReader());
		setSqlRegistry(new HashMapSqlRegistry());
	}
	
	/* 다른 오브젝트로 DI했을 시 사용되지않는 디폴트 의존오브젝트 방지하기 위해 후처리기로 처리 */	// loadSql도 마찬가지로 PostConstruct인데 순서가 안맞는지 오류남. 추후 확인필요
	/*@PostConstruct
	public void configureDefaultObject() {
		System.out.println(getClass().getName() + " - configureDefaultObject called...");
		if(this.sqlReader == null) {
			setSqlReader(new JaxbXmlSqlReader());
		}
		if(this.sqlRegistry == null) {
			setSqlRegistry(new HashMapSqlRegistry());
		}
	}*/
}

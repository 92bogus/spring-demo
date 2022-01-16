package kr.co.mytour.learningtest.user.sqlservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import kr.co.mytour.learningtest.user.dao.UserDao;
import kr.co.mytour.learningtest.user.sqlservice.jaxb.SqlType;
import kr.co.mytour.learningtest.user.sqlservice.jaxb.Sqlmap;

/* 자기참조빈을 이용한 SqlService 구현체 */
public class XmlSqlService implements SqlService, SqlRegistry, SqlReader {

	private SqlReader sqlReader;
	private SqlRegistry sqlRegistry;

	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}

	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	private Map<String, String> sqlMap = new HashMap<String, String>();

	/* sqlmap.xml 파일명 따로 지정 가능하도록 프로퍼티를 추가한다 */
	private String sqlmapFile;

	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}

	public XmlSqlService() {
//		String contextPath = Sqlmap.class.getPackage().getName();
//
//		try {
//			JAXBContext context = JAXBContext.newInstance(contextPath);
//			Unmarshaller unmarshaller = context.createUnmarshaller();
//			InputStream is = UserDao.class.getResourceAsStream("sqlmap.xml");
//			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
//
//			for(SqlType sql : sqlmap.getSql()) {
//				sqlMap.put(sql.getKey(), sql.getValue());
//			}
//		} catch(JAXBException e) {
//			throw new RuntimeException(e);
//		}
		System.out.println("XmlSqlService bean created...");
	}

	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		/*String sql = sqlMap.get(key);
		if(sql == null) {
			throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
		} else {
			return sql;
		}*/
		try {
			return this.sqlRegistry.findSql(key);
		} catch(SqlNotFoundException e) {
			throw new SqlRetrievalFailureException(e);
		}
	}

//	생성자에서 예외가 날 수 있는 위험성이 있는 작업은 피하는게 좋으므로 따로 빼주자.
	@PostConstruct
	public void loadSql() {
//		System.out.println(getClass().getName() + "- loadSql called...");
//		String contextPath = Sqlmap.class.getPackage().getName();
//
//		try {
//			JAXBContext context = JAXBContext.newInstance(contextPath);
//			Unmarshaller unmarshaller = context.createUnmarshaller();
//			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);
//			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
//
//			for(SqlType sql : sqlmap.getSql()) {
//				sqlMap.put(sql.getKey(), sql.getValue());
//			}
//		} catch(JAXBException e) {
//			throw new RuntimeException(e);
//		}
		this.sqlReader.read(this.sqlRegistry);
	}

	/* SqlRegistry 구현 메소드 START */
	@Override
	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if(sql == null) throw new SqlNotFoundException(key + "에 대한 SQL을 찾을 수 없습니다.");
		else return sql;
	}
	/* SqlRegistry 구현 메소드 END */

	/* SqlReader 구현 메소드 */
	@Override
	public void read(SqlRegistry sqlRegistry) {
		String contextPath = Sqlmap.class.getPackage().getName();

		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(sqlmapFile);
			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);

			for(SqlType sql : sqlmap.getSql()) {
				sqlRegistry.registerSql(sql.getKey(), sql.getValue());
			}
		} catch(JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}

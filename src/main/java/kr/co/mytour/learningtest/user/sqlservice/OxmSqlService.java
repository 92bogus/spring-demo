package kr.co.mytour.learningtest.user.sqlservice;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import kr.co.mytour.learningtest.user.dao.UserDao;
import kr.co.mytour.learningtest.user.sqlservice.SqlRegistry.SqlNotFoundException;
import kr.co.mytour.learningtest.user.sqlservice.jaxb.SqlType;
import kr.co.mytour.learningtest.user.sqlservice.jaxb.Sqlmap;

public class OxmSqlService implements SqlService {

	private final BaseSqlService baseSqlService = new BaseSqlService();

	private final OxmSqlReader oxmSqlReader = new OxmSqlReader();

	private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	/* OxmSqlReader에게 전달하는 역할을 해준다, 창구역할 S */
	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.oxmSqlReader.setUnmarshaller(unmarshaller);
	}

	/*public void setSqlmapFile(String sqlmapFile) {
		this.oxmSqlReader.setSqlmapFile(sqlmapFile);
	}*/

	public void setSqlmap(Resource sqlmap) {
		this.oxmSqlReader.setSqlmap(sqlmap);
	}
	/* OxmSqlReader에게 전달하는 역할을 해준다, 창구역할 E */

	/* 내부 클래스 */
	private class OxmSqlReader implements SqlReader {
//		private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";

		private Unmarshaller unmarshaller;
//		private String sqlmapFile = DEFAULT_SQLMAP_FILE;
		private Resource sqlmap = new ClassPathResource("sqlmap.xml", UserDao.class);

		public void setUnmarshaller(Unmarshaller unmarshaller) {
			this.unmarshaller = unmarshaller;
		}

		/*public void setSqlmapFile(String sqlmapFile) {
			this.sqlmapFile = sqlmapFile;
		}*/

		public void setSqlmap(Resource sqlmap) {
			this.sqlmap = sqlmap;
		}

		@Override
		public void read(SqlRegistry sqlRegistry) {
			try {
//				Source source = new StreamSource(UserDao.class.getResourceAsStream(this.sqlmapFile));
				Source source = new StreamSource(this.sqlmap.getInputStream());
				Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);

				for(SqlType sql : sqlmap.getSql()) {
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}
			} catch (IOException e) {
				throw new IllegalArgumentException(this.sqlmap.getFilename() + "을 가져올 수 없습니다.");
			}
		}
	}

	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		/*try {
			return this.sqlRegistry.findSql(key);
		} catch(SqlNotFoundException e) {
			throw new SqlRetrievalFailureException(e);
		}*/
		return this.baseSqlService.getSql(key);
	}

	@PostConstruct
	public void loadSql() {
//		this.oxmSqlReader.read(this.sqlRegistry);
		// 위임하자.
		this.baseSqlService.setSqlReader(this.oxmSqlReader);
		this.baseSqlService.setSqlRegistry(this.sqlRegistry);

		this.baseSqlService.loadSql();
	}
}

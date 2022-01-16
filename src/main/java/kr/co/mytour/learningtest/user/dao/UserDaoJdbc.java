package kr.co.mytour.learningtest.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import kr.co.mytour.learningtest.user.domain.Level;
import kr.co.mytour.learningtest.user.domain.User;
import kr.co.mytour.learningtest.user.exception.DuplicateUserIdException;
import kr.co.mytour.learningtest.user.sqlservice.SqlService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

//@Component("userDao")	// id를 직접 지정; 지정 안하면 userDaoJdbc
@Repository	// 스프링은 다오에는 해당 어노테이션을 지정하는 것을 권장함
public class UserDaoJdbc implements UserDao {

	@Autowired
	private SqlService sqlService;
	/*public void setSqlService(SqlService sqlService) {
		this.sqlService = sqlService;
	}*/ // 필드에 직접 주입하므로 수정자메소드를 거칠 필요가 없다. 반면 setDataSource는 필드에 @Autowired를 직접 붙일수가 없는게
	// 직접 필드에 주입하는게 아니라서;
	// 팁: 다오에서는 어차피 목 오브젝트를 이용해 테스트하기가 어려우므로 직접 필드에 붙이곤 하고
	// 비즈니스 로직인 서비스엔 안전하게 수정자메소드가 있도록 하는게 좋음
	
//	private ConnectionMaker connectionMaker;

//	public UserDao(ConnectionMaker connectionMaker) {
//		this.connectionMaker = connectionMaker;
//	}
	
//	public void setConnectionMaker(ConnectionMaker connectionMaker) {
//		this.connectionMaker = connectionMaker;
//	}
	
	private JdbcTemplate jdbcTemplate;
	
	//private DataSource dataSource;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		//this.dataSource = dataSource;
		//this.jdbcContext = new JdbcContext();
		//this.jdbcContext.setDataSource(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		
		//this.dataSource = dataSource;	// 아직 수정안된게 있음..
	}
	
	//private JdbcContext jdbcContext;
	
	// JdbcContext를 주입받지말고 DataSource만 UserDao가 주입받은 후 수동으로 DI 시켜주기로 변경
//	public void setJdbcContext(JdbcContext jdbcContext) {
//		this.jdbcContext = jdbcContext;
//	}
/*
	private String sqlAdd;
	public void setSqlAdd(String sqlAdd) {
		this.sqlAdd = sqlAdd;
	}
*/
	/*
	private Map<String, String> sqlMap;
	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}
	*/
	// AddStatement 클래스에서 메소드 로컬변수(user)를 사용할 수 있도록 final로 해준다
	public void add(final User user)/* throws SQLException*/ throws DuplicateUserIdException {
//		Connection c = null;
//		PreparedStatement ps = null;
//		
//		try {
////			c = this.connectionMaker.makeConnection();
//			c = dataSource.getConnection();
//			ps = c.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
//			ps.setString(1, user.getId());
//			ps.setString(2, user.getName());
//			ps.setString(3, user.getPassword());
//			
//			ps.executeUpdate();
//			
//		} catch(SQLException e) {
//			throw e;
//		} finally {
//			if(ps != null) {
//				try {
//					ps.close();
//				} catch(SQLException e) {
//					
//				}
//			}
//			
//			if(c != null) {
//				try {
//					c.close();
//				} catch(SQLException e) {
//					
//				}
//			}
//		}
		
//		class AddStatement implements StatementStrategy {	// 전략클래스를 java파일로 각각 만드니.. 클래스파일이 늘어난다. 중첩클래스로 바꿔봄
////			User user;
////			
////			public AddStatement(User user) {
////				this.user = user;
////			}
//			
//			@Override
//			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//				PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
//				ps.setString(1, user.getId());
//				ps.setString(2, user.getName());
//				ps.setString(3, user.getPassword());
//				
//				return ps;
//			}
//			
//		}
//		
//		StatementStrategy st = new AddStatement();	// user 파라미터 제거
		
		// 익명 내부클래스로 변경
//		jdbcContextWithStatementStrategy(new StatementStrategy() {
//			@Override
//			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//				PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
//				ps.setString(1, user.getId());
//				ps.setString(2, user.getName());
//				ps.setString(3, user.getPassword());
//				
//				return ps;
//			}
//		});
		
		// jdbcContext로 컨텍스트 분리
//		this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {
//			
//			@Override
//			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//				PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
//				ps.setString(1, user.getId());
//				ps.setString(2, user.getName());
//				ps.setString(3, user.getPassword());
//				
//				return ps;
//			}
//		});
		
		// jdbcTemplate은 SQLException을 DataAccessException 런타임으로 포장해서 던지고 있음, 필요하다면 캐치해서 처리하면 된다
		//try {
			this.jdbcTemplate.update(/*"insert into users(id, name, password, level, login, recommend, email) "
					+ "values(?, ?, ?, ?, ?, ?, ?)"*//*this.sqlAdd,*//*this.sqlMap.get("add")*/this.sqlService.getSql("userAdd"), 
					user.getId(), user.getName(), user.getPassword(), 
					user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
		//} catch(DuplicateKeyException e) {
			// DuplicatekeyException은 스프링이 드라이버나 DB 메타정보를 참고해서 DB별로 미리 준비된 매핑정보를 참고해서 던지는 예외클래스이다. 
			// 참고로 런타임예외임. 만약 정책 등으로 인해 변환해줘야한다면, 아래와 같이 포장해서 던질 수 있다.
			//			if(e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) {
//				throw new DuplicateUserIdException(e);
//			} else {
//				throw new RuntimeException(e);
//			}
			
			//throw new DuplicateUserIdException(e);
		//}
		

	}
	
	public User get(String id)/* throws SQLException*/ {
//		Connection c = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		
//		try {
//	//		Connection c = this.connectionMaker.makeConnection();
//			c = dataSource.getConnection();
//			ps = c.prepareStatement("select * from users where id = ?");
//			ps.setString(1, id);
//			rs = ps.executeQuery();
//			
//			User user = null;
//			
//			if(rs.next()) {
//				user = new User();
//				user.setId(rs.getString("id"));
//				user.setName(rs.getString("name"));
//				user.setPassword(rs.getString("password"));
//			}
//			
//			if(user == null) throw new EmptyResultDataAccessException(1);
//			
//			return user;
//			
//		} catch(SQLException e) {
//			throw e;
//		} finally {
//			if(rs != null) {
//				try {
//					rs.close();
//				} catch(SQLException e) {
//					
//				}
//			}
//			
//			if(ps != null) {
//				try {
//					ps.close();
//				} catch(SQLException e) {
//					
//				}
//			}
//			
//			if(c != null) {
//				try {
//					c.close();
//				} catch(SQLException e) {
//					
//				}
//			}
//		}
		
		return this.jdbcTemplate.queryForObject(/*"select * from users where id = ?"*//*this.sqlMap.get("get")*/this.sqlService.getSql("userGet"),
				new Object[] {id}, // SQL에 바인딩 할 파라미터 값, 가변인자 대신 배열을 사용한다.
				this.userMapper
		);
		
	}
	
	public void deleteAll() /*throws SQLException */{
//		Connection c = null;
//		PreparedStatement ps = null;
//		
//		try {
//			c = dataSource.getConnection();
//			//ps = c.prepareStatement("delete from users");
//			StatementStrategy strategy = new DeleteAllStatement();
//			ps = strategy.makePreparedStatement(c);
//			
//			ps.executeUpdate();
//		} catch(SQLException e) {
//			throw e;
//		} finally {
//			if(ps != null) {
//				try {
//					ps.close();
//				} catch(SQLException e) {
//					
//				}
//			}
//			
//			if(c != null) {
//				try {
//					c.close();
//				} catch(SQLException e) {
//					
//				}
//			}
//		}
//		
		
		//StatementStrategy st = new DeleteAllStatement();	
		//jdbcContextWithStatementStrategy(st);
		//this.jdbcContext.executeSql("delete from users");
		
//		this.jdbcTemplate.update(new PreparedStatementCreator() {
//
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				return con.prepareStatement("delete from users");
//			}
//			
//		});
		
		this.jdbcTemplate.update(/*"delete from users"*//*this.sqlMap.get("deleteAll")*/this.sqlService.getSql("userDeleteAll"));
		
	}

	public int getCount() /*throws SQLException */{
//		Connection c = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		
//		try {
//			 c = dataSource.getConnection();
//			 ps = c.prepareStatement("select count(*) from users");
//			 rs = ps.executeQuery();
//			 rs.next();
//			 
//			 return rs.getInt(1);
//			 
//		} catch(SQLException e) {
//			throw e;
//		} finally {
//			if(rs != null) {
//				try {
//					rs.close();
//				} catch(SQLException e) {
//					
//				}
//			}
//			
//			if(ps != null) {
//				try {
//					ps.close();
//				} catch(SQLException e) {
//					
//				}
//			}
//			
//			if(c != null) {
//				try {
//					c.close();
//				} catch(SQLException e) {
//					
//				}
//			}
//		}
//		this.jdbcTemplate.query(new PreparedStatementCreator() {
//
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				return con.prepareStatement("select count(*) from users");
//			}
//			
//		}, new ResultSetExtractor<Integer>() {
//
//			@Override
//			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
//				rs.next();
//				return rs.getInt(1);
//			}
//			
//		});
		
		return this.jdbcTemplate.queryForObject(/*"select count(*) from users"*//*this.sqlMap.get("getCount")*/this.sqlService.getSql("userGetCount"), Integer.class);	// queryForInt, queryForLong은 스프링 3.2로부터 deprecated 됨
		// jdbcTemplate의 queryForInt를 까보면 내부적으로 queryForObject를 이런식으로 쓰고있었음
	}
	
//	public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
//		Connection c = null;
//		PreparedStatement ps = null;
//		
//		try {
//			c = dataSource.getConnection();
//			ps = stmt.makePreparedStatement(c);
//			ps.executeUpdate();
//		} catch(SQLException e) {
//			throw e;
//		} finally {
//			if(ps != null) {
//				try {
//					ps.close();
//				} catch(SQLException e) {
//					
//				}
//			}
//			
//			if(c != null) {
//				try {
//					c.close();
//				} catch(SQLException e) {
//					
//				}
//			}
//		}
//	}
	
	public List<User> getAll() {
		return this.jdbcTemplate.query(/*"select * from users order by id"*//*this.sqlMap.get("getAll")*/this.sqlService.getSql("userGetAll"), this.userMapper);
	}
	
	private RowMapper<User> userMapper = new RowMapper<User>() {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			user.setEmail(rs.getString("email"));
			return user;
		}
		
	};

	@Override
	public void update(User user) {
		this.jdbcTemplate.update(/*"update users set name = ?, password = ?, level = ?, login = ?, " + 
								 "recommend = ?, email = ? where id = ?"*//*this.sqlMap.get("update")*/this.sqlService.getSql("userUpdate"), user.getName(), user.getPassword(),
								 user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail(),
								 user.getId());
	}
	
}

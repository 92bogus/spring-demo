package kr.co.mytour.learningtest.user.sqlservice;

import org.springframework.jdbc.object.SqlUpdate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapSqlRegistry implements UpdatableSqlRegistry {
    private Map<String, String> sqlMap = new ConcurrentHashMap<String, String>();

    /* SqlRegistry Implements S */
    @Override
    public void updateSql(String key, String sql) throws SqlUpdateFailureException {
        if(sqlMap.get(key) == null) {
            throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다");
        }

        sqlMap.put(key, sql);
    }

    @Override
    public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException {
        for(Map.Entry<String, String> entry : sqlmap.entrySet()) {
            updateSql(entry.getKey(), entry.getValue());
        }
    }
    /* SqlRegistry Implements E */

    /* UpdatableSqlRegistry Implements S */
    @Override
    public void registerSql(String key, String sql) {
        sqlMap.put(key, sql);
    }

    @Override
    public String findSql(String key) throws SqlNotFoundException {
        String sql = sqlMap.get(key);
        if(sql == null) throw new SqlNotFoundException(key + "를 이용해서 SQL을 찾을 수 없습니다");
        else return sql;
    }
    /* UpdatableSqlRegistry Implements E */
}


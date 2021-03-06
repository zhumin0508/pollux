/*
 * Copyright (c) 2012-2013, Poplar Yfyang 杨友峰 (poplar1123@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.polluxframework.paginator.support;

import com.polluxframework.paginator.dialect.Dialect;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author poplar.yfyang
 * @author miemiedev
 */
public class SqlHelp {
	private static Logger logger = LoggerFactory.getLogger(SqlHelp.class);

	private SqlHelp() {
	}

	/**
	 * 查询总纪录数
	 *
	 * @param mappedStatement mapped
	 * @param parameter       参数
	 * @param boundSql        boundSql
	 * @param dialect         database dialect
	 * @return 总记录数
	 * @throws java.sql.SQLException sql查询错误
	 */
	public static int getCount(MappedStatement mappedStatement, Transaction transaction, Object parameter, BoundSql boundSql, Dialect dialect) throws SQLException {
		final String countSql = dialect.getCountSQL();
		logger.debug("Total count SQL [{}] ", countSql);
		logger.debug("Total count Parameters: {} ", parameter == null ? "" : parameter);

		Connection connection = transaction.getConnection();
		PreparedStatement countStmt = connection.prepareStatement(countSql);
		DefaultParameterHandler handler = new DefaultParameterHandler(mappedStatement, parameter, boundSql);
		handler.setParameters(countStmt);
		int count = 0;
		try (ResultSet rs = countStmt.executeQuery()) {
			if (rs.next()) {
				count = rs.getInt(1);
			}
			logger.debug("Total count: {}", count);
		}
		return count;
	}

}
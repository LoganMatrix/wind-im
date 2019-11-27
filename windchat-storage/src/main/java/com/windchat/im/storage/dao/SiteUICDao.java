/** 
 * Copyright 2018-2028 Akaxin Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.windchat.im.storage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.windchat.common.logs.LogUtils;
import com.windchat.common.utils.StringHelper;
import com.windchat.im.storage.bean.UicBean;
import com.windchat.im.storage.connection.DatabaseConnection;
import com.windchat.im.storage.dao.sql.SQLConst;

/**
 * 用户邀请码
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-11 17:26:52
 */
public class SiteUICDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteUICDao.class);
	private final String UIC_TABLE = SQLConst.SITE_USER_UIC;
	private final String USER_PROFILE_TABLE = SQLConst.SITE_USER_PROFILE;

	public static SiteUICDao getInstance() {
		return SingletonHolder.instance;
	}

	static class SingletonHolder {
		private static SiteUICDao instance = new SiteUICDao();
	}

	/**
	 * 新增UIC码
	 * 
	 * @param bean
	 * @return
	 * @throws SQLException
	 */
	public boolean addUIC(UicBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + UIC_TABLE + "(uic,status,create_time) VALUES(?,?,?);";

		int result;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();

			ps = conn.prepareStatement(sql);
			ps.setString(1, bean.getUic());
			ps.setInt(2, bean.getStatus());
			ps.setLong(3, System.currentTimeMillis());

			result = ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getUic(), bean.getStatus(), startTime);
		return result > 0;
	}

	/**
	 * 批量新增UIC
	 * 
	 * @param bean
	 * @param num
	 * @return
	 * @throws SQLException
	 */
	public boolean batchAddUIC(UicBean bean, int num, int length) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + UIC_TABLE + "(uic,status,create_time) VALUES(?,?,?);";
		int successCount = 0;
		length = length < 6 ? 6 : length;// 最短6位

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			conn.setAutoCommit(false);
			for (int i = 0; i < num; i++) {
				try {
					String uicValue = StringHelper.generateRandomNumber(length);

					ps = conn.prepareStatement(sql);
					ps.setString(1, uicValue);
					ps.setInt(2, bean.getStatus());
					ps.setLong(3, System.currentTimeMillis());
					ps.executeUpdate();

					ps.clearParameters();
					successCount++;
				} catch (Exception e) {
					logger.error("execute uic sql error ", e);
				}
			}
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			conn.rollback();
			logger.error(StringHelper.format("batch add uic error bean={} num={}", bean.toString(), num), e);
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, successCount, sql, "randomUic", bean.getStatus(),
				System.currentTimeMillis());
		return successCount > 0;
	}

	/**
	 * 查询UIC使用情况
	 * 
	 * @param uic
	 * @return
	 * @throws SQLException
	 */
	public UicBean queryUIC(String uic) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT uic,site_user_id,status,create_time,use_time FROM " + UIC_TABLE + " WHERE uic=?;";

		UicBean bean = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();

			pst = conn.prepareStatement(sql);
			pst.setString(1, uic);

			rs = pst.executeQuery();
			if (rs.next()) {
				bean = new UicBean();
				bean.setUic(rs.getString(1));
				bean.setSiteUserId(rs.getString(2));
				bean.setStatus(rs.getInt(3));
				bean.setCreateTime(rs.getLong(4));
				bean.setUseTime(rs.getLong(5));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, bean, sql, uic);
		return bean;
	}

	/**
	 * 更新用户使用的UIC
	 * 
	 * @param bean
	 * @return
	 * @throws SQLException
	 */
	public boolean updateUIC(UicBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + UIC_TABLE + " SET site_user_id=?,status=?,use_time=? WHERE uic=?;";

		long currentTime = System.currentTimeMillis();
		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();

			ps = conn.prepareStatement(sql);
			ps.setString(1, bean.getSiteUserId());
			ps.setInt(2, bean.getStatus());
			ps.setLong(3, currentTime);
			ps.setString(4, bean.getUic());

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getSiteUserId(), bean.getStatus(), currentTime,
				bean.getUic());
		return result > 0;
	}

	public List<UicBean> queryUicList(int pageNum, int pageSize, int status) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<UicBean> uicList = new ArrayList<UicBean>();
		String sql = "SELECT a.id,a.uic,a.site_user_id,b.user_name,a.create_time,a.use_time FROM " + UIC_TABLE
				+ " AS a LEFT JOIN " + USER_PROFILE_TABLE
				+ " AS b ON a.site_user_id=b.site_user_id where a.status=? LIMIT ?,?;";

		int startNum = (pageNum - 1) * pageSize;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, status);
			pst.setInt(2, startNum);
			pst.setInt(3, pageSize);

			rs = pst.executeQuery();
			while (rs.next()) {
				UicBean bean = new UicBean();
				bean.setId(rs.getInt(1));
				bean.setUic(rs.getString(2));
				bean.setSiteUserId(rs.getString(3));
				bean.setUserName(rs.getString(4));
				bean.setCreateTime(rs.getLong(5));
				bean.setUseTime(rs.getLong(6));
				uicList.add(bean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, uicList, sql);
		return uicList;
	}

	/**
	 * 查询所有UIC列表
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws SQLException
	 */
	public List<UicBean> queryAllUicList(int pageNum, int pageSize) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<UicBean> uicList = new ArrayList<UicBean>();
		String sql = "SELECT a.id,a.uic,a.site_user_id,b.user_name,a.create_time,a.use_time FROM " + UIC_TABLE
				+ " AS a LEFT JOIN " + USER_PROFILE_TABLE + " AS b ON a.site_user_id=b.site_user_id LIMIT ?,?;";

		int startNum = (pageNum - 1) * pageSize;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, startNum);
			pst.setInt(2, pageSize);

			rs = pst.executeQuery();
			while (rs.next()) {
				UicBean bean = new UicBean();
				bean.setId(rs.getInt(1));
				bean.setUic(rs.getString(2));
				bean.setSiteUserId(rs.getString(3));
				bean.setUserName(rs.getString(4));
				bean.setCreateTime(rs.getLong(5));
				bean.setUseTime(rs.getLong(6));
				uicList.add(bean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, uicList, sql, startNum, pageSize);
		return uicList;
	}
}

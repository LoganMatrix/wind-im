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
package com.windchat.im.storage.service;

import java.sql.SQLException;
import java.util.List;

import com.windchat.im.storage.api.IUserGroupDao;
import com.windchat.im.storage.bean.UserGroupBean;
import com.windchat.im.storage.dao.SiteGroupMessageDao;
import com.windchat.im.storage.dao.SiteUserGroupDao;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-03-03 15:04:47
 */
public class UserGroupDaoService implements IUserGroupDao {

	@Override
	public List<String> checkGroupMember(String siteGroupId, List<String> userIds) throws SQLException {
		return SiteUserGroupDao.getInstance().checkGroupMember(siteGroupId, userIds);
	}

	@Override
	public UserGroupBean getUserGroupSetting(String siteUserId, String siteGroupId) throws SQLException {
		return SiteUserGroupDao.getInstance().getUserGroupSetting(siteUserId, siteGroupId);
	}

	@Override
	public boolean updateUserGroupSetting(String siteUserId, UserGroupBean bean) throws SQLException {
		return SiteUserGroupDao.getInstance().updateUserGroupSetting(siteUserId, bean);
	}

	@Override
	public boolean isMute(String siteUserId, String siteGroupId) throws SQLException {
		return SiteUserGroupDao.getInstance().queryMute(siteUserId, siteGroupId);
	}

	@Override
	public boolean updateMute(String siteUserId, String siteGroupId, boolean mute) throws SQLException {
		return SiteUserGroupDao.getInstance().updateMute(siteUserId, siteGroupId, mute);
	}

	@Override
	public int queryGroupMessagePerDay(long now, int day) throws SQLException {
		int groupCount = SiteGroupMessageDao.getInstance().queryNumMessagePerDay(now, day);
		return groupCount;
	}

}

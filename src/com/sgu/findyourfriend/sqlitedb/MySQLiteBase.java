/*
 * 	 This file is part of Find Your Friend.
 *
 *   Find Your Friend is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Find Your Friend is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Find Your Friend.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sgu.findyourfriend.sqlitedb;

import com.sgu.findyourfriend.mgr.SettingManager;

public class MySQLiteBase {
	private static final String DATABASE_NAME_PREFIX = "com.sgu.findyourfriend.";
	public static final int DATABASE_VERSION = 1;
	
	
	public static String getDataBaseName() {
		return  DATABASE_NAME_PREFIX + SettingManager.getInstance().getLastAccountIdLogin() + ".db";
	}
}

package model.dao;

import db.DB;
import model.dao.impl.DepartmentDaoJdbc;
import model.dao.impl.SellerDaoJdbc;

public class DaoFactory {
	/**
	 * At the moment, I only use Mysql JDBC, but if there is the need to adapt to other kind of database, I gotta create a dbmanager
	 * @return
	 */
	
	public static DaoSeller createSellerDao() {
		return new SellerDaoJdbc(DB.getConnection());
	}
	
	public static DaoDepartment createDepartmentDao() {
		return new DepartmentDaoJdbc(DB.getConnection());
	}
}

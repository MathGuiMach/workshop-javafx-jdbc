package model.dao;

import db.DB;
import model.dao.impl.DepartmentDaoJdbc;
import model.dao.impl.SellerDaoJdbc;

public class DaoFactory {
	
	public static DaoSeller createSellerDaoJdbc() {
		return new SellerDaoJdbc(DB.getConnection());
	}
	
	public static DaoDepartment createDepartmentDaoJdbc() {
		return new DepartmentDaoJdbc();//mockup
	}
}

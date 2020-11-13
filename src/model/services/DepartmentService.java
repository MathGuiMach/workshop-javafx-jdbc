package model.services;

import java.util.List;

import model.dao.DaoDepartment;
import model.dao.DaoFactory;
import model.entities.Department;

public class DepartmentService {

	private DaoDepartment dao = DaoFactory.createDepartmentDao();
	
	public List<Department> findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate(Department d) {
		if(d.getId() == null) {
			dao.insert(d);
		} else {
			dao.update(d);
		}
	}
}

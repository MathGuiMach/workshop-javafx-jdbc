package model.dao.impl;

import java.util.ArrayList;
import java.util.List;

import model.dao.DaoDepartment;
import model.entities.Department;

public class DepartmentDaoJdbc implements DaoDepartment {

	@Override
	public Department findById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(Department obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Department obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Department> findAll() {
		//Mockup
		List<Department> list = new ArrayList<>();
		list.add(new Department(1,"Books"));
		list.add(new Department(2,"Computers"));
		list.add(new Department(3,"Electronics"));
		return list;
	}

}

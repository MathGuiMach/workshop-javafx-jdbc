package model.dao;

import java.util.List;

import model.entities.Department;
import model.entities.Seller;

public interface DaoSeller extends Dao<Seller> {

	public List<Seller> findByDepartmentId(Department d);

}

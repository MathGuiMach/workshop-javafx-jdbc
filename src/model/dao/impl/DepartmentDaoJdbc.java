package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DaoDepartment;
import model.entities.Department;

public class DepartmentDaoJdbc implements DaoDepartment {

private Connection conn;
	
	public DepartmentDaoJdbc(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public Department findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try	 {
			st = conn.prepareStatement("Select * from Department where id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if(rs.next()) {
				return new Department(rs.getInt("Id"),rs.getString("Name")); 
			}
			return null;
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;
		try	 {
			st = conn.prepareStatement("Insert into Department (Name) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getName());
			
			int affectedRows = st.executeUpdate();
			
			if(affectedRows > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} 
			else {
				throw new DbException("Unexpected error! No nows affected!");
			}
			
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;
		
		try	 {
			st = conn.prepareStatement("Update Department set Name = ? where Id = ?");
			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());
			
			st.executeUpdate();
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		
		try	 {
			st = conn.prepareStatement("delete from department where id = ?");
			st.setInt(1, id);
						
			st.executeUpdate();
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try	 {
			st = conn.prepareStatement("Select * from Department");
			rs = st.executeQuery();
			
			List<Department> departments = new ArrayList<>();
			while(rs.next()) {
				departments.add(new Department(rs.getInt("Id"),rs.getString("Name"))); 
			}
			
			return departments;
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

}

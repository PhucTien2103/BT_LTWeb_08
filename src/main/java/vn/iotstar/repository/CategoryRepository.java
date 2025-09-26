package vn.iotstar.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Category;


import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	//Tìm Kiếm theo nội dung tên 
	List<Category> findByCategoryNameContaining(String name); 
	//Tìm kiếm và Phân trang 
	Page<Category> findByCategoryNameContaining(String name,Pageable pageable); 
	Optional<Category> findByCategoryName(String name); 
}

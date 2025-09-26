package vn.iotstar.service;

import java.util.Date;          // ✅
import java.util.List;
import java.util.Optional;

import vn.iotstar.entity.Product;

public interface IProductService {
    List<Product> findAll();
    Optional<Product> findByProductName(String name);
    Optional<Product> findByCreateDate(Date date);   // ✅
    <S extends Product> S save(S entity);
    void deleteById(Long id);
    Optional<Product> findById(Long id);
}

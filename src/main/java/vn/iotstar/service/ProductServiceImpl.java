package vn.iotstar.service;

import java.util.Date;          // ✅
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Product;
import vn.iotstar.repository.ProductRepository;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    ProductRepository productRepository;

    @Override public List<Product> findAll() { return productRepository.findAll(); }
    @Override public Optional<Product> findByProductName(String name) { return productRepository.findByProductName(name); }
    @Override public Optional<Product> findByCreateDate(Date date) { return productRepository.findByCreateDate(date); } // ✅
    @Override public <S extends Product> S save(S entity) { return productRepository.save(entity); }
    @Override public void deleteById(Long id) { productRepository.deleteById(id); }
    @Override public Optional<Product> findById(Long id) { return productRepository.findById(id); }
}

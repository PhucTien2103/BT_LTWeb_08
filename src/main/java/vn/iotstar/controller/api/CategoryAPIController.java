package vn.iotstar.controller.api;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.entity.Category;
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IStorageService;

@RestController
@RequestMapping(path = "/api/category")
public class CategoryAPIController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IStorageService storageService;

    // GET all: theo hướng dẫn trả về List JSON thuần để Ajax render bảng
    @GetMapping
    public ResponseEntity<?> getAllCategory() {
        return ResponseEntity.ok().body(categoryService.findAll());
        // Hoặc có thể dùng wrapper:
        // return new ResponseEntity<>(new Response(true, "Thành công", categoryService.findAll()), HttpStatus.OK);
    }

    // Lấy 1 category theo id (POST /getCategory)
    @PostMapping(path = "/getCategory")
    public ResponseEntity<Response> getCategory(@Validated @RequestParam("id") Long id) {
        Optional<Category> category = categoryService.findById(id);
        if (category.isPresent()) {
            return new ResponseEntity<>(new Response(true, "Thành công", category.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response(false, "Thất bại", null), HttpStatus.NOT_FOUND);
    }

    // Thêm category (icon REQUIRED theo hướng dẫn)
    @PostMapping(path = "/addCategory")
    public ResponseEntity<?> addCategory(@Validated @RequestParam("categoryName") String categoryName,
                                         @Validated @RequestParam("icon") MultipartFile icon) {
        Optional<Category> optCategory = categoryService.findByCategoryName(categoryName);
        if (optCategory.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Category đã tồn tại trong hệ thống");
        }

        Category category = new Category();
        if (!icon.isEmpty()) {
            String uu = UUID.randomUUID().toString();
            category.setIcon(storageService.getSorageFilename(icon, uu));
            storageService.store(icon, category.getIcon());
        }
        category.setCategoryName(categoryName);
        categoryService.save(category);

        return new ResponseEntity<>(new Response(true, "Thêm Thành công", category), HttpStatus.OK);
    }

    // Cập nhật category (icon REQUIRED theo hướng dẫn)
    @PutMapping(path = "/updateCategory")
    public ResponseEntity<Response> updateCategory(@Validated @RequestParam("categoryId") Long categoryId,
                                                   @Validated @RequestParam("categoryName") String categoryName,
                                                   @Validated @RequestParam("icon") MultipartFile icon) {
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        }

        Category entity = optCategory.get();
        if (!icon.isEmpty()) {
            String uu = UUID.randomUUID().toString();
            entity.setIcon(storageService.getSorageFilename(icon, uu));
            storageService.store(icon, entity.getIcon());
        }
        entity.setCategoryName(categoryName);
        categoryService.save(entity);

        return new ResponseEntity<>(new Response(true, "Cập nhật Thành công", entity), HttpStatus.OK);
    }

    // Xóa category
    @DeleteMapping(path = "/deleteCategory")
    public ResponseEntity<?> deleteCategory(@Validated @RequestParam("categoryId") Long categoryId) {
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        }
        categoryService.delete(optCategory.get());
        // Theo hướng dẫn, có thể trả về entity hoặc wrapper:
        // return ResponseEntity.ok().body(optCategory.get());
        return new ResponseEntity<>(new Response(true, "Xóa Thành công", optCategory.get()), HttpStatus.OK);
    }
}

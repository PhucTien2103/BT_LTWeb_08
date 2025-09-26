package vn.iotstar.controller.api;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.model.ProductModel;
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IStorageService;

@RestController
@RequestMapping(path = "/api/product")
public class ProductApiController {

    @Autowired
    IProductService productService;

    @Autowired
    ICategoryService categoryService;

    @Autowired
    IStorageService storageService;

    @GetMapping
    public ResponseEntity<Response> getAllProduct() {
        return new ResponseEntity<>(new Response(true, "Thành công", productService.findAll()), HttpStatus.OK);
    }

    @PostMapping(path = "/addProduct")
    public ResponseEntity<Response> saveOrUpdate(
            @Validated @RequestParam("productName") String productName,
            @RequestParam("imageFile") MultipartFile productImages,
            @Validated @RequestParam("unitPrice") Double productPrice,
            @Validated @RequestParam("discount") Double promotionalPrice,
            @Validated @RequestParam("description") String productDescription,
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("quantity") Integer quantity,
            @Validated @RequestParam("status") Short status) {

        Optional<Product> optProduct = productService.findByProductName(productName);
        if (optProduct.isPresent()) {
            return new ResponseEntity<>(
                    new Response(false, "Sản phẩm này đã tồn tại trong hệ thống", optProduct.get()),
                    HttpStatus.BAD_REQUEST);
        }

        Product product = new Product();
        Timestamp timestamp = new Timestamp(new Date(System.currentTimeMillis()).getTime());

        try {
            ProductModel proModel = new ProductModel();
            // đổ param thẳng vào model tạm
            proModel.setProductName(productName);
            proModel.setImageFile(productImages);
            proModel.setUnitPrice(productPrice);
            proModel.setDiscount(promotionalPrice);
            proModel.setDescription(productDescription);
            proModel.setCategoryId(categoryId);
            proModel.setQuantity(quantity);
            proModel.setStatus(status);

            BeanUtils.copyProperties(proModel, product);

            // map Category
            Category cate = new Category();
            cate.setCategoryId(proModel.getCategoryId());
            product.setCategory(cate);

            // lưu file ảnh nếu có
            if (proModel.getImageFile() != null && !proModel.getImageFile().isEmpty()) {
                String uu = UUID.randomUUID().toString();
                product.setImages(storageService.getSorageFilename(proModel.getImageFile(), uu));
                storageService.store(proModel.getImageFile(), product.getImages());
            }

            product.setCreateDate(timestamp);
            productService.save(product);

            // lấy lại theo createDate (đúng mẫu tài liệu)
            optProduct = productService.findByCreateDate(timestamp);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new Response(false, "Lỗi xử lý", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new Response(true, "Thành công", optProduct.orElse(product)), HttpStatus.OK);
    }
}

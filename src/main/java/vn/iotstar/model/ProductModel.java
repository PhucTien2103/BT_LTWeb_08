package vn.iotstar.model;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductModel {

    @NotBlank
    private String productName;

    // Ảnh upload (không bắt buộc)
    private MultipartFile imageFile;

    @NotNull @Min(0)
    private Double unitPrice;

    @NotNull @Min(0)
    private Double discount;

    @NotBlank
    private String description;

    @NotNull
    private Long categoryId;

    @NotNull @Min(0)
    private Integer quantity;

    @NotNull
    private Short status;
}

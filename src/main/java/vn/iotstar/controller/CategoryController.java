package vn.iotstar.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import vn.iotstar.entity.Category;
import vn.iotstar.model.CategoryModel;
import vn.iotstar.service.ICategoryService;

@Controller
@RequestMapping("admin/categories")
public class CategoryController {

	@Autowired
	ICategoryService cateSer;

	@GetMapping("")
	public String list(ModelMap model) {
		model.addAttribute("categories", cateSer.findAll());
		return "admin/list";
	}

	// Trang thêm mới danh mục
	@GetMapping("add")
	public String add(Model model) {
		CategoryModel cate = new CategoryModel();
		cate.setEdit(false);
		model.addAttribute("category", cate);
		return "admin/categories/addOrEdit";
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(Model model, @Validated @ModelAttribute("category") CategoryModel cateModel,
	        BindingResult result) {
	    if (result.hasErrors()) {
	        return new ModelAndView("admin/categories/addOrEdit");
	    }

	    if (cateModel == null) {
	        model.addAttribute("message", "Danh mục không hợp lệ!");
	        return new ModelAndView("admin/categories/addOrEdit");
	    }

	    Category category = new Category();
	    BeanUtils.copyProperties(cateModel, category);
	    cateSer.save(category);
	    String message = cateModel.getEdit() ? "Chỉnh sửa thành công!" : "Lưu thành công!";
	    model.addAttribute("message", message);
	    return new ModelAndView("redirect:/admin/categories");
	}

	@GetMapping("edit")
	public ModelAndView edit(ModelMap model, @RequestParam("id") Long id) {
		Optional<Category> optCategory = cateSer.findById(id);
		CategoryModel cateModel = new CategoryModel();
		
		if (optCategory.isPresent()) {
			Category entity = optCategory.get();
			BeanUtils.copyProperties(entity, cateModel);
			cateModel.setEdit(true);
			model.addAttribute("category", cateModel);
			return new ModelAndView("admin/categories/addOrEdit", model);
		}

		model.addAttribute("message", "Category Không tồn tại !!!!");
		return new ModelAndView("forward:/admin/categories/searchpaginated", model);
	}

	@GetMapping("/searchpaging")
	public String searchDefault(ModelMap model,
	        @RequestParam(name = "name", required = false) String name,
	        @RequestParam(name = "page", defaultValue = "1") int page,
	        @RequestParam(name = "size", defaultValue = "5") int size) {

	    Pageable pageable = PageRequest.of(page - 1, size);
	    Page<Category> categoryPage;

	    if (StringUtils.hasText(name)) {
	        categoryPage = cateSer.findByCategoryNameContaining(name, pageable);
	    } else {
	        categoryPage = cateSer.findAll(pageable);
	    }

	    int totalPages = categoryPage.getTotalPages();
	    List<Integer> pageNumbers = new ArrayList<>();
	    if (totalPages > 0) {
	        for (int i = 1; i <= totalPages; i++) {
	            pageNumbers.add(i);
	        }
	    }

	    model.addAttribute("categoryPage", categoryPage);
	    model.addAttribute("pageNumbers", pageNumbers);
	    model.addAttribute("name", name);

	    return "admin/categories/searchpaging";
	}

	@GetMapping("/delete")
	public String delete(ModelMap model, @RequestParam("id") Long id) {
		cateSer.deleteById(id);
		model.addAttribute("message", "Category was deleted");
		return "forward:/admin/categories";
	}

	@GetMapping("search")
	public String search(ModelMap model,@RequestParam(name="name",required = false) String name) {
		List<Category> list = null;
		if(StringUtils.hasText(name)) {
			list = cateSer.findByCategoryNameContaining(name);
		}
		else {
			list = cateSer.findAll();
		}
		model.addAttribute("categories",list);
		return "admin/categories/search";
	}
	
	@GetMapping("ajax")
	public String ajaxPage() {
	    return "admin/categories/ajax";
	}

}
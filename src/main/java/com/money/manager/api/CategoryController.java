package com.money.manager.api;

import com.money.manager.db.dao.CategoryDao;
import com.money.manager.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/resources/categories")
public class CategoryController {

    private final CategoryDao categoryDao;

    @Autowired
    public CategoryController(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Category> getCategories() {
        return categoryDao.findAll();

    }
}

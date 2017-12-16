package com.money.manager.api;

import com.money.manager.dao.CategoryDao;
import com.money.manager.dao.HibernateCategoryDao;
import com.money.manager.model.Category;
import com.money.manager.exception.CustomException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/resources/categories")
public class CategoryController {

    private final static CategoryDao categoryDao = new HibernateCategoryDao();

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Category> getCategories() throws CustomException {
        return categoryDao.findAll();

    }
}

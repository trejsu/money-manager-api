package com.money.manager.api;

import com.money.manager.model.Category;
import com.money.manager.service.CategoryService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    private final static String CATEGORIES_URL = "/resources/categories";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @SneakyThrows
    public void shouldReturnListOfCategories() {
        final List<Category> categories = Arrays.asList(
                new Category("awesome category", true),
                new Category("super category", false),
                new Category("great category", true));
        when(categoryService.getCategories()).thenReturn(categories);

        final ResultActions response = mockMvc.perform(get(CATEGORIES_URL));

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("awesome category", "super category", "great category")))
                .andExpect(jsonPath("$[*].profit", containsInAnyOrder(true, false, true)));
    }
}

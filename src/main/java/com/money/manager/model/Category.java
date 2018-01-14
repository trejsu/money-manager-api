package com.money.manager.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "CATEGORY")
@EqualsAndHashCode
@IdClass(Category.CategoryPK.class)
public class Category {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class CategoryPK implements Serializable {
        private String name;
        private boolean profit;
    }

    @Id
    @Column(name = "name")
    @NotNull
    private String name;

    @Id
    @Column(name = "profit")
    @NotNull
    private Boolean profit;
}

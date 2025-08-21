package com.alertsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "menus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id")
    @Builder.Default
    private Long parentId = 0L;

    @Column(name = "tree_path")
    private String treePath;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "type", nullable = false)
    private Integer type; // 0:目錄 1:選單 2:按鈕

    @Column(name = "route_name")
    private String routeName;

    @Column(name = "route_path", length = 128)
    private String routePath;

    @Column(name = "component", length = 128)
    private String component;

    @Column(name = "permission", length = 128)
    private String permission;

    @Column(name = "always_show")
    @Builder.Default
    private Integer alwaysShow = 0; // 0:否 1:是

    @Column(name = "keep_alive")
    @Builder.Default
    private Integer keepAlive = 0; // 0:否 1:是

    @Column(name = "visible")
    @Builder.Default
    private Integer visible = 1; // 0:隱藏 1:顯示

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "icon", length = 64)
    private String icon;

    @Column(name = "redirect", length = 128)
    private String redirect;

    @Column(name = "params")
    private String params;

    // 一對多：父選單對子選單
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<Menu> children;

    // 多對一：子選單對父選單
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Menu parent;

    // 多對多：選單與角色的關聯
    @JsonBackReference
    @ManyToMany(mappedBy = "menus", fetch = FetchType.LAZY)
    private Set<Role> roles;

    /**
     * 選單類型枚舉
     */
    public enum MenuType {
        DIRECTORY(0, "目錄"),
        MENU(1, "選單"),
        BUTTON(2, "按鈕");

        private final Integer value;
        private final String description;

        MenuType(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static MenuType fromValue(Integer value) {
            for (MenuType type : MenuType.values()) {
                if (type.getValue().equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown menu type value: " + value);
        }
    }

    /**
     * 判斷是否為根選單
     */
    public boolean isRoot() {
        return parentId == null || parentId == 0L;
    }

    /**
     * 判斷是否為目錄
     */
    public boolean isDirectory() {
        return MenuType.DIRECTORY.getValue().equals(type);
    }

    /**
     * 判斷是否為選單
     */
    public boolean isMenu() {
        return MenuType.MENU.getValue().equals(type);
    }

    /**
     * 判斷是否為按鈕
     */
    public boolean isButton() {
        return MenuType.BUTTON.getValue().equals(type);
    }

    /**
     * 判斷是否可見
     */
    public boolean isVisible() {
        return visible != null && visible == 1;
    }

    /**
     * 判斷是否總是顯示
     */
    public boolean isAlwaysShow() {
        return alwaysShow != null && alwaysShow == 1;
    }

    /**
     * 判斷是否需要緩存
     */
    public boolean isKeepAlive() {
        return keepAlive != null && keepAlive == 1;
    }
}
